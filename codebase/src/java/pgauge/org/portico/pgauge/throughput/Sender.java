/*
 *   Copyright 2009 The Portico Project
 *
 *   This file is part of pgauge (a sub-project of Portico).
 *
 *   pgauge is free software; you can redistribute it and/or modify
 *   it under the terms of the Common Developer and Distribution License (CDDL) 
 *   as published by Sun Microsystems. For more information see the LICENSE file.
 *   
 *   Use of this software is strictly AT YOUR OWN RISK!!!
 *   If something bad happens you do not have permission to come crying to me.
 *   (that goes for your lawyer as well)
 *
 */
package org.portico.pgauge.throughput;

import static org.portico.pgauge.PGUtilities.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.portico.pgauge.PGConfiguration;
import org.portico.pgauge.PGFederateAmbassador;
import org.portico.pgauge.PGFom;
import org.portico.pgauge.PGUtilities;
import org.portico.pgauge.gui.ThroughputChart;

import hla.rti13.java1.RTIambassador;
import hla.rti13.java1.ResignAction;

public class Sender implements Runnable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected PGConfiguration configuration;
	private RTIambassador rtiamb;
	private SenderFederateAmbassador fedamb;
	protected PGFom fom;
	protected Logger logger;
	protected List<Integer> objectHandles;
	private byte[] payload;

	private boolean ready;
	
	// results storage
	private ThroughputDataset dataset;
	
	// chart display
	private ThroughputChart chart;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Sender( PGConfiguration configuration )
	{
		this.configuration = configuration;
		this.objectHandles = new ArrayList<Integer>();
		this.ready = false;
		this.dataset = new ThroughputDataset();
		
		if( configuration.isLiveGUI() )
			this.chart = new ThroughputChart( dataset, configuration );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public void run()
	{
		try
		{
			runFederate();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

	private void runFederate() throws Exception
	{	
		initialize();
		synchronize( "READY_TO_RUN", rtiamb, fedamb );
		logger.info( "Commencing for "+configuration.getIterations()+" iterations" );
		
		dataset.record( 0 );
		for( int i = 1; i <= configuration.getIterations(); i++ )
		{
			simulate( i );
			
			if( (i % configuration.getRecordInterval()) == 0 )
				record( i );
			
			rtiamb.tick();
		}
		
		// completed, delete the objects we were updating
		for( Integer objectHandle : objectHandles )
			rtiamb.deleteObjectInstance( objectHandle, "" );
		
		synchronize( "READY_TO_RESIGN", rtiamb, fedamb );
		cleanup();
		
		// only print the results now if we're not in an automated scenario
		// if we are, the scenario executor will take care of calling printResults
		if( configuration.isThroughputScenario() == false )
			printResults();
		if( configuration.isLog() )
			dataset.writeToFile( "results/"+configuration.getFederateName()+".log" );
		
		if( configuration.isChart() && configuration.isLiveGUI() == false )
			this.chart = new ThroughputChart( dataset, configuration );
	}

	private void record( int iterationCount )
	{
		dataset.record( iterationCount );
		if( chart != null )
			chart.updateChart();
	}

	//////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////// Simulation Methods ///////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	private void initialize() throws Exception
	{
		// randomize the name to avoid clashes in federations with multiple instances running
		
		logger = getLogger( "pgauge.sender" );
		rtiamb = new RTIambassador();
		fedamb = new SenderFederateAmbassador();
		logger.info( "Initializing Sender federate" );
		
		// join the federation, try and number of times to be safe
		rtiamb.joinFederationExecution( configuration.getFederateName(),
		                                configuration.getFederationName(),
		                                fedamb );

		// intialize handles
		fom = new PGFom( rtiamb );
		
		// enable time policy
		if( configuration.isTimestepped() )
		{
			rtiamb.enableAsynchronousDelivery();
			rtiamb.enableTimeConstrained();
			while( fedamb.constrained == false )
				rtiamb.tick();
			
			rtiamb.enableTimeRegulation( time(fedamb.federateTime), time(1.0) );
			while( fedamb.regulating == false )
				rtiamb.tick();
		}
		
		// publish and subscribe
		rtiamb.publishObjectClass( fom.pgauge(), fom.pgaugeHandleSet() );
		
		// register the objects we will update
		for( int i = 0; i < configuration.getObjects(); i++ )
			objectHandles.add( rtiamb.registerObjectInstance(fom.pgauge()) );
		
		// pre-fill payload data
		payload = randomData( configuration.getPayloadSize() );
		
		// print configuration
		logger.info( "=== (ThroughputSender) ====================" );
		logger.info( " federateName     : "+configuration.getFederateName() );
		logger.info( " federationName   : "+configuration.getFederationName() );
		logger.info( " timestepped      : "+configuration.isTimestepped() );
		logger.info( " iterations       : "+configuration.getIterations() );
		logger.info( " objects          : "+configuration.getObjects() );
		logger.info( " payloadSize      : "+configuration.getPayloadSizeAsString() );
		logger.info( "===========================================" );
		
		this.ready = true;
	}

	private void simulate( int iteration ) throws Exception
	{
		for( Integer objectHandle : objectHandles )
		{
    		// update the attribute values, always use a time, if we're not timestepped we won't
    		// be regulating, in which case the RTI will ignore the time
    		rtiamb.updateAttributeValues( objectHandle,
    		                              fom.pgaugeSupplied(payload),
    		                              time(++iteration),
    		                              "" );
		}

		// advance time if we are timestepped
		if( configuration.isTimestepped() && iteration <= configuration.getIterations() )
		{
			double newtime = iteration++;
			rtiamb.timeAdvanceRequest( time(newtime) );
			while( fedamb.federateTime < newtime )
				rtiamb.tick();
		}
		
		if( (iteration % configuration.getPrintInterval()) == 0 )
			logger.info( "Completed " + iteration + " iterations" );
	}
	
	private void cleanup() throws Exception
	{
		rtiamb.resignFederationExecution( ResignAction.DELETE_OBJECTS_AND_RELEASE_ATTRIBUTES );
	}

	public void printResults()
	{
		// calculate how long the sending took overall, and what the average/s was
		long iterations = configuration.getIterations();
		long updates = iterations * configuration.getObjects();
		double duration = dataset.getDurationSeconds();
		int average = (int)(updates/duration);
		int throughput = (int)((updates*configuration.getPayloadSize()) / duration);
		String throughputString = PGUtilities.bytesToString( throughput );

		logger.info( "=== (ThroughputSender) ====================" );
		logger.info( " federateName  : "+configuration.getFederateName() );
		logger.info( " execution time: "+dataset.getDurationMillis()+"ms" );
		logger.info( " updateRate    : "+average+" updates/s ("+throughputString+"/s)" );
		logger.info( "===========================================" );
	}

	public boolean isReady()
	{
		return this.ready;
	}

	public ThroughputDataset getDataSet()
	{
		return this.dataset;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static void main( String[] args )
	{
		// things you can control
		//  -iterations
		//  -timestepped
		//  -payloadSize
		//  -printInterval
		PGConfiguration configuration = PGConfiguration.defaultConfiguration();
		configuration.setFederateName( "sender" );
		
		try
		{
    		configuration.parse( args );
		}
		catch( Exception e )
		{
			Logger logger = PGUtilities.getLogger( "pgauge" );
			logger.error( "No scenario provided" );
			logger.error( "usage: " );
			logger.error( PGConfiguration.usage() );
		}

		new Sender(configuration).run();
	}

	/////////////////////////////////////////////////////////////
	//////////////////// Private Inner Class ////////////////////
	/////////////////////////////////////////////////////////////
	private class SenderFederateAmbassador extends PGFederateAmbassador
	{
	}
}
