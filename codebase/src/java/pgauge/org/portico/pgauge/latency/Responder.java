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

public class Responder implements Runnable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected PGConfiguration configuration;
	private RTIambassador rtiamb;
	private ResponderFederateAmbassador fedamb;
	protected PGFom fom;
	protected Logger logger;
	private byte[] payload;

	private int pingsReceived;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Responder( PGConfiguration configuration )
	{
		this.configuration = configuration;
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
		
		// achieve the "ready to resign point" and keep going until it is achieved
		while( fedamb.isAnnounced("READY_TO_RESIGN") == false )
			rtiamb.tick();
		
		rtiamb.synchronizationPointAchieved( "READY_TO_RESIGN" );
		
		logger.info( "Waiting for ping requests" );
		while( true )
		{
			// tick for some callbacks
			rtiamb.tick();
			
			// have we received a request we need to respond to?
			if( fedamb.requestReceived )
			{
				rtiamb.sendInteraction( fom.pingAck(), fom.pingAckSupplied(payload), "" );
				fedamb.requestReceived = false;
			}
			
			// check to see if we should finish yet
			if( fedamb.isSynchronized("READY_TO_RESIGN") )
				break;
		}

		// clean things up and exit
		cleanup();
	}

	//////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////// Simulation Methods ///////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	private void initialize() throws Exception
	{
		// randomize the name to avoid clashes in federations with multiple instances running
		
		logger = getLogger( "pgauge.responder" );
		rtiamb = new RTIambassador();
		fedamb = new ResponderFederateAmbassador();
		logger.info( "Initializing Responder federate" );
		
		// join the federation, try and number of times to be safe
		rtiamb.joinFederationExecution( configuration.getFederateName(),
		                                configuration.getFederationName(),
		                                fedamb );

		// intialize handles
		fom = new PGFom( rtiamb );
		
		// publish and subscribe
		rtiamb.subscribeInteractionClass( fom.ping() );
		rtiamb.publishInteractionClass( fom.pingAck() );
		
		// pre-fill payload data
		payload = randomData( configuration.getPayloadSize() );
		
		// print configuration
		logger.info( "=== (LatencyResponder) ====================" );
		logger.info( " federateName     : "+configuration.getFederateName() );
		logger.info( " federationName   : "+configuration.getFederationName() );
		logger.info( " payloadSize      : "+configuration.getPayloadSizeAsString() );
		logger.info( "===========================================" );
	}

	private void cleanup() throws Exception
	{
		rtiamb.resignFederationExecution( ResignAction.DELETE_OBJECTS_AND_RELEASE_ATTRIBUTES );
	}

	public void printResults()
	{
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static void main( String[] args )
	{
		PGConfiguration configuration = PGConfiguration.defaultConfiguration();
		configuration.setFederateName( "responder" );
		
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

		new Responder(configuration).run();
	}

	/////////////////////////////////////////////////////////////
	//////////////////// Private Inner Class ////////////////////
	/////////////////////////////////////////////////////////////
	private class ResponderFederateAmbassador extends PGFederateAmbassador
	{
		public boolean requestReceived = false;
		
		public void receiveInteraction( int classHandle,
		                                ReceivedInteraction parameters,
		                                byte[] time,
		                                String tag,
		                                EventRetractionHandle erh )
		{
			requestReceived = true;
			++pingsReceived;

			if( (pingsReceived % configuration.getPrintInterval()) == 0 )
				logger.info( "Responded to "+pingsReceived+" pings" );
		}

		public void receiveInteraction( int classHandle, ReceivedInteraction parameters, String tag )
		{
			requestReceived = true;
			++pingsReceived;
			
			if( (pingsReceived % configuration.getPrintInterval()) == 0 )
				logger.info( "Responded to "+pingsReceived+" pings" );
		}

	}

}
