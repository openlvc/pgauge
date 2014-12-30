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
package org.portico.pgauge.objectcreation;

import static org.portico.pgauge.PGUtilities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.portico.pgauge.PGConfiguration;
import org.portico.pgauge.PGFederateAmbassador;
import org.portico.pgauge.PGFom;
import org.portico.pgauge.PGUtilities;

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
	private PGFederateAmbassador fedamb;
	protected PGFom fom;
	protected Logger logger;
	protected List<Integer> objectHandles;

	private boolean ready;
	
	private long startTime;
	private long createCallsCompletedTime; // time all registerObject calls are done
	private long finishTime; // time federation synchronizes (other feds has discovered)
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Sender( PGConfiguration configuration )
	{
		this.configuration = configuration;
		this.objectHandles = new ArrayList<Integer>();
		this.ready = false;
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
		logger.info( "Registering "+configuration.getObjects()+" objects" );

		startTime = System.nanoTime();
		for( int i = 0; i < configuration.getObjects(); i++ )
		{
			rtiamb.registerObjectInstance( fom.pgauge() );

			// tick every 10 registrations to allow us to clear any pending work
			if( (i % 10) == 0 )
				rtiamb.tick();
		}
		createCallsCompletedTime = System.nanoTime();
		
		synchronize( "READY_TO_RESIGN", rtiamb, fedamb );
		finishTime = System.nanoTime();

		cleanup();
		
		// only print the results now if we're not in an automated scenario
		// if we are, the scenario executor will take care of calling printResults
		if( configuration.isObjectCreationScenario() == false )
			printResults();
	}

	//////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////// Simulation Methods ///////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	private void initialize() throws Exception
	{
		// randomize the name to avoid clashes in federations with multiple instances running
		
		logger = getLogger( "pgauge.sender" );
		rtiamb = new RTIambassador();
		fedamb = new PGFederateAmbassador();
		logger.info( "Initializing Sender federate" );
		
		// join the federation, try and number of times to be safe
		rtiamb.joinFederationExecution( configuration.getFederateName(),
		                                configuration.getFederationName(),
		                                fedamb );

		// intialize handles
		fom = new PGFom( rtiamb );
		
		// publish and subscribe
		rtiamb.publishObjectClass( fom.pgauge(), fom.pgaugeHandleSet() );
		
		// print configuration
		logger.info( "=== (CreationSender) ======================" );
		logger.info( " federateName     : "+configuration.getFederateName() );
		logger.info( " federationName   : "+configuration.getFederationName() );
		logger.info( " objects          : "+configuration.getObjects() );
		logger.info( "===========================================" );
		
		this.ready = true;
	}

	private void cleanup() throws Exception
	{
		rtiamb.resignFederationExecution( ResignAction.DELETE_OBJECTS_AND_RELEASE_ATTRIBUTES );
	}

	public void printResults()
	{
		long durationNanos = finishTime - startTime;
		long creationTimeNanos = createCallsCompletedTime - startTime;
		
		logger.info( "=== (CreationSender) ======================" );
		logger.info( " federateName  : "+configuration.getFederateName() );
		logger.info( " execution time: "+TimeUnit.NANOSECONDS.toMillis(durationNanos)+"ms" );
		logger.info( "  ->create time: "+TimeUnit.NANOSECONDS.toMillis(creationTimeNanos)+"ms" );
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
		//  -objects
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
}
