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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.portico.pgauge.PGConfiguration;
import org.portico.pgauge.PGFederateAmbassador;
import org.portico.pgauge.PGFom;
import org.portico.pgauge.PGUtilities;
import org.portico.pgauge.gui.ThroughputChart;

import hla.rti13.java1.EventRetractionHandle;
import hla.rti13.java1.RTIambassador;
import hla.rti13.java1.ReflectedAttributes;
import hla.rti13.java1.ResignAction;

public class Listener implements Runnable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected PGConfiguration configuration;
	private RTIambassador rtiamb;
	private ListenerFederateAmbassador fedamb;
	protected PGFom fom;
	protected Logger logger;
	protected int objectHandle;

	// object data
	private Map<Integer,Integer> objects;
	private int updateCount;
	
	private boolean ready;
	
	// results
	private ThroughputDataset dataset;
	private ThroughputChart chart;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Listener( PGConfiguration configuration )
	{
		this.configuration = configuration;
		
		this.objects = new HashMap<Integer,Integer>();
		this.updateCount = 0;
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

		dataset.start();
		
		// if we're timestepped, request advances and tick along
		if( configuration.isTimestepped() )
		{
    		double requestedTime = fedamb.federateTime;
    		for( int iteration = 0; iteration < configuration.getIterations(); iteration++ )
    		{
    			requestedTime = requestedTime + 1.0;
    			rtiamb.timeAdvanceRequest( PGUtilities.time(requestedTime) );
    			while( fedamb.hasTimeReached(requestedTime) == false )
    				rtiamb.tick();

    			// should we print how far along we are?
    			if( (iteration % configuration.getPrintInterval()) == 0 )
    				logger.info( "Completed "+iteration+" iterations ("+updateCount+" updates received)" );
    			
    			// should we record for this iteration?
    			if( (iteration % configuration.getRecordInterval()) == 0 )
    				record( iteration );
    		}
		}
		
		// wait for all the object we're tracking to be deleted
		while( objects.size() > 0 )
			rtiamb.tick();
		
		synchronize( "READY_TO_RESIGN", rtiamb, fedamb );
		
		cleanup();
		
		// only print the results now if we're not in an automated scenario
		// if we are, the scenario executor will take care of calling printResults
		if( configuration.isThroughputScenario() == false )
			printResults();
		
		// log if necessary
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
		
		logger = getLogger( "pgauge.listener" );
		rtiamb = new RTIambassador();
		fedamb = new ListenerFederateAmbassador();
		logger.info( "Initializing Listener federate" );
		
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
		rtiamb.subscribeObjectClassAttributes( fom.pgauge(), fom.pgaugeHandleSet() );
		
		// wait to discover the objects we are expecting
		while( objects.size() < configuration.getObjects() )
			rtiamb.tick();
		
		// print configuration
		logger.info( "=== (ThroughputListener) ==================" );
		logger.info( " federateName     : "+configuration.getFederateName() );
		logger.info( " federationName   : "+configuration.getFederationName() );
		logger.info( " timestepped      : "+configuration.isTimestepped() );
		logger.info( " expectedObjects  : "+configuration.getObjects() );
		logger.info( "===========================================" );
		
		this.ready = true;
	}

	private void cleanup() throws Exception
	{
		rtiamb.resignFederationExecution( ResignAction.DELETE_OBJECTS_AND_RELEASE_ATTRIBUTES );
	}

	public void printResults()
	{
		int reflectionsPerSecond = (int)(updateCount / dataset.getDurationSeconds());
		
		logger.info( "=== (ThroughputListener) ==================" );
		logger.info( " federateName  : "+configuration.getFederateName() );
		logger.info( " execution time: "+dataset.getDurationMillis()+"ms" );
		logger.info( " reflections   : "+updateCount+" ("+reflectionsPerSecond+"/s)" );
		logger.info( "===========================================" );
	}

	public boolean isReady()
	{
		return this.ready;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static void main( String[] args )
	{
		// things you can control
		//  -iterations
		//  -timestepped
		//  -expectedObjects
		PGConfiguration configuration = PGConfiguration.defaultConfiguration();
		configuration.setFederateName( "listener" );

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

		new Listener(configuration).run();
	}

	/////////////////////////////////////////////////////////////
	//////////////////// Private Inner Class ////////////////////
	/////////////////////////////////////////////////////////////
	private class ListenerFederateAmbassador extends PGFederateAmbassador
	{
		///////////////////////
		// object management //
		///////////////////////
		public void discoverObjectInstance( int objectHandle, int classHandle, String objectName )
		{
			objects.put( objectHandle, objectHandle );
		}

		public void reflectAttributeValues( int objectHandle,
		                                    ReflectedAttributes attributes,
		                                    String tag )
		{
			++updateCount;
			
			if( configuration.isTimestepped() == false )
			{
				// record the update count if it's at the record interval
				if( (updateCount % configuration.getRecordInterval()) == 0 )
					record( updateCount );
				
				// log the update count if we're at the print interval
				if( (updateCount % configuration.getPrintInterval()) == 0 )
    				logger.info( "Received "+updateCount+" updates" );
			}
		}

		public void reflectAttributeValues( int objectHandle,
		                                    ReflectedAttributes attributes,
		                                    byte[] time,
		                                    String tag,
		                                    EventRetractionHandle erh )
		{
			++updateCount;

			if( configuration.isTimestepped() == false )
			{
				// record the update count if it's at the record interval
				if( (updateCount % configuration.getRecordInterval()) == 0 )
					record( updateCount );
				
				// log the update count if we're at the print interval
				if( (updateCount % configuration.getPrintInterval()) == 0 )
    				logger.info( "Received "+updateCount+" updates" );
			}
		}
		
		public void removeObjectInstance( int objectHandle,
		                                  byte[] time,
		                                  String tag,
		                                  EventRetractionHandle theHandle )
		{
			objects.remove( objectHandle );
		}

		public void removeObjectInstance( int objectHandle, String tag )
		{
			objects.remove( objectHandle );
		}
	}
}
