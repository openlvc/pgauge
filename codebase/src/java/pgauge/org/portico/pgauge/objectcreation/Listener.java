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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.portico.pgauge.PGConfiguration;
import org.portico.pgauge.PGFederateAmbassador;
import org.portico.pgauge.PGFom;
import org.portico.pgauge.PGUtilities;

import hla.rti13.java1.RTIambassador;
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
	private boolean ready;

	// object data
	private Set<Integer> discoveredObjects;
	private long firstDiscovery;
	private long lastDiscovery;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Listener( PGConfiguration configuration )
	{
		this.configuration = configuration;
		this.discoveredObjects = new HashSet<Integer>();
		this.ready = false;
		
		this.firstDiscovery = 0;
		this.lastDiscovery = 0;
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
		logger.info( "Waiting for "+configuration.getObjects()+" discoveries" );

		// wait for all the object we're tracking to be deleted
		while( lastDiscovery == 0 )
			rtiamb.tick();

		logger.info( "Completed "+configuration.getObjects()+" discoveries" );
		synchronize( "READY_TO_RESIGN", rtiamb, fedamb );
		
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
		
		// publish and subscribe
		rtiamb.subscribeObjectClassAttributes( fom.pgauge(), fom.pgaugeHandleSet() );
		
		// print configuration
		logger.info( "=== (CreationListener) ====================" );
		logger.info( " federateName     : "+configuration.getFederateName() );
		logger.info( " federationName   : "+configuration.getFederationName() );
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
		long duration = TimeUnit.NANOSECONDS.toMillis(lastDiscovery-firstDiscovery);
		double durationSeconds = (double)duration/1000.0;
		long perSecond = (long)((double)configuration.getObjects() / durationSeconds);
		
		logger.info( "=== (CreationListener) ====================" );
		logger.info( " federateName  : "+configuration.getFederateName() );
		logger.info( " execution time: "+duration+"ms" );
		logger.info( " discoveries   : "+configuration.getObjects()+" ("+perSecond+"/s)" );
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
			// is this the first?
			if( discoveredObjects.isEmpty() )
				firstDiscovery = System.nanoTime();
			
			discoveredObjects.add( objectHandle );
			
			// are we done yet?
			if( discoveredObjects.size() == configuration.getObjects() )
				lastDiscovery = System.nanoTime();
		}
	}
}
