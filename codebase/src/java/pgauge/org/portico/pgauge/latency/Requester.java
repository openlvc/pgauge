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
package org.portico.pgauge.latency;

import static org.portico.pgauge.PGUtilities.*;
import hla.rti13.java1.EventRetractionHandle;
import hla.rti13.java1.RTIambassador;
import hla.rti13.java1.ReceivedInteraction;
import hla.rti13.java1.ResignAction;

import org.apache.log4j.Logger;
import org.portico.pgauge.PGConfiguration;
import org.portico.pgauge.PGFederateAmbassador;
import org.portico.pgauge.PGFom;
import org.portico.pgauge.PGUtilities;
import org.portico.pgauge.gui.LatencyChart;

public class Requester implements Runnable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected PGConfiguration configuration;
	private RTIambassador rtiamb;
	private RequesterFederateAmbassador fedamb;
	protected PGFom fom;
	protected Logger logger;
	private byte[] payload;

	private boolean ready;
	
	// recorded data
	private LatencyDataset dataset;
	private LatencyChart chart;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Requester( PGConfiguration configuration )
	{
		this.configuration = configuration;
		this.ready = false;
		this.dataset = new LatencyDataset();
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

	/**
	 * The main federate loop. This federate will send a ping request to the federation and
	 * wait for the response to filter in from the responder federate. It will complete this for
	 * a number of iterations as defined in the configuration, recording the latency at each point.
	 */
	private void runFederate() throws Exception
	{	
		initialize();
		synchronize( "READY_TO_RUN", rtiamb, fedamb );
		
		// wait for the resign point to be announced, because the responder wait for that to
		// happen before responding to anything, so it'll screw up results for the first iteration
		// if we don't wait!
		while( fedamb.isAnnounced("READY_TO_RESIGN") == false )
			rtiamb.tick();
		
		logger.info( "Commencing for "+configuration.getIterations()+" iterations" );
		for( int i = 1; i <= configuration.getIterations(); i++ )
		{
			// start the clock
			long startTime = System.nanoTime();
			
			// send the ping request
			rtiamb.sendInteraction( fom.ping(), fom.pingSupplied(payload), "" );
			
			// wait for the response
			while( fedamb.received == false )
				rtiamb.tick();
			
			// record the time
			long endTime = System.nanoTime();
			dataset.record( endTime-startTime );
			if( chart != null ) chart.updateChart(i);
			
			// reset the flag
			fedamb.received = false;
			
			// print a notice if we're at that point
			if( (i % configuration.getPrintInterval()) == 0 )
				logger.info( "Completed " + i + " iterations" );
		}
		
		synchronize( "READY_TO_RESIGN", rtiamb, fedamb );
		cleanup();
		
		// only print the results now if we're not in an automated scenario
		// if we are, the scenario executor will take care of calling printResults
		if( configuration.isLatencyScenario() == false )
			printResults();

		// log to a file if necessary
		if( configuration.isLog() )
			dataset.writeToFile( "results/"+configuration.getFederateName()+".log" );
		
		// should we display a chart with the results
		if( configuration.isChart() && configuration.isLiveGUI() == false )
			this.chart = new LatencyChart( dataset, configuration );
	}

	//////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////// Helper Methods /////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	private void initialize() throws Exception
	{
		// randomize the name to avoid clashes in federations with multiple instances running
		
		logger = getLogger( "pgauge.requester" );
		rtiamb = new RTIambassador();
		fedamb = new RequesterFederateAmbassador();
		logger.info( "Initializing Requester federate" );
		
		// join the federation, try and number of times to be safe
		rtiamb.joinFederationExecution( configuration.getFederateName(),
		                                configuration.getFederationName(),
		                                fedamb );

		// intialize handles
		fom = new PGFom( rtiamb );
		
		// publish and subscribe
		rtiamb.publishInteractionClass( fom.ping() );
		rtiamb.subscribeInteractionClass( fom.pingAck() );
		
		// pre-fill payload data
		payload = randomData( configuration.getPayloadSize() );
		
		// print configuration
		logger.info( "=== (LatencyRequester) ====================" );
		logger.info( " federateName     : "+configuration.getFederateName() );
		logger.info( " federationName   : "+configuration.getFederationName() );
		logger.info( " iterations       : "+configuration.getIterations() );
		logger.info( " payloadSize      : "+configuration.getPayloadSizeAsString() );
		logger.info( "===========================================" );
		
		this.ready = true;
	}

	private void cleanup() throws Exception
	{
		rtiamb.resignFederationExecution( ResignAction.DELETE_OBJECTS_AND_RELEASE_ATTRIBUTES );
	}

	public void printResults()
	{
		logger.info( "=== (LatencyRequester) ====================" );
		logger.info( " federateName       : "+configuration.getFederateName() );
		logger.info( " iterations         : "+configuration.getIterations() );
		logger.info( " average latency    : "+dataset.getAverage()+" microseconds" );
		logger.info( " 80% average        : "+dataset.getEightyAverage()+" microseconds" );
		logger.info( " average (2std.dev) : "+dataset.getAverageWithinTwoStandardDeviations()+ " microseconds" );
		logger.info( " standard deviation : "+dataset.getStandardDeviation() );
		logger.info( " lowest rountrip    : "+dataset.getLow()+" microseconds" );
		logger.info( " highest rountrip   : "+dataset.getHigh()+" microseconds" );
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
		PGConfiguration configuration = PGConfiguration.defaultConfiguration();
		configuration.setFederateName( "requester" );
		
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

		new Requester(configuration).run();
	}

	/////////////////////////////////////////////////////////////
	//////////////////// Private Inner Class ////////////////////
	/////////////////////////////////////////////////////////////
	private class RequesterFederateAmbassador extends PGFederateAmbassador
	{
		public boolean received = false;
		
		public void receiveInteraction( int classHandle,
		                                ReceivedInteraction parameters,
		                                byte[] time,
		                                String tag,
		                                EventRetractionHandle erh )
		{
			received = true;
		}

		public void receiveInteraction( int classHandle, ReceivedInteraction parameters, String tag )
		{
			received = true;
		}

	}

}
