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
package org.portico.pgauge;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import hla.rti.ResignAction;
import hla.rti13.java1.RTIambassador;

import org.apache.log4j.Logger;

public class ExecutionManager implements Runnable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private PGConfiguration configuration;
	private Logger logger;
	private RTIambassador rtiamb;
	private ExmanagerFederateAmbassador fedamb;
	private boolean ready;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ExecutionManager( PGConfiguration configuration )
	{
		this.configuration = configuration;
		this.logger = PGUtilities.getLogger( "pgauge.exmanager" );
		this.ready = false;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public void run()
	{
		String federationName = configuration.getFederationName();
		String fedfile = configuration.getFedFile();

		try
		{
			rtiamb = new RTIambassador();
			fedamb = new ExmanagerFederateAmbassador();
			
			// create and join the federation
			rtiamb.createFederationExecution( federationName, fedfile );
			rtiamb.joinFederationExecution( "exmanager", federationName, fedamb );
			logger.info( "Created and joined federation ["+federationName+"] as [exmanager]" );
			this.ready = true;
			
			// announce the ready-to-run sync point and wait for the used to hit enter before
			// achieving it, thus holding up other federates until we are ready
			rtiamb.registerFederationSynchronizationPoint( "READY_TO_RUN", "" );
			InputWaiter waiter = new InputWaiter();
			waiter.start();
			while( waiter.isAlive() )
				rtiamb.tick();

			rtiamb.synchronizationPointAchieved( "READY_TO_RUN" );
			while( fedamb.isSynchronized("READY_TO_RUN") == false )
				rtiamb.tick();
			
			// announce the ready-to-resign sync point and wait until it is synchronized on
			// the test federates will sync on this when they're prepared to end the test
			rtiamb.registerFederationSynchronizationPoint( "READY_TO_RESIGN", "" );
			while( fedamb.isAnnounced("READY_TO_RESIGN") == false )
				rtiamb.tick();

			rtiamb.synchronizationPointAchieved( "READY_TO_RESIGN" );
			logger.info( "Waiting for other federates to finish simulating..." );
			while( fedamb.isSynchronized("READY_TO_RESIGN") == false )
				rtiamb.tick();
			
			// register the final point and wait for it to be achieved. no other federates should
			// take notice of this point or achieve it, rather, they should just resign. This way,
			// it will be achieved when we are the last federate left
			rtiamb.registerFederationSynchronizationPoint( "READY_TO_DESTROY", "" );
			while( fedamb.isAnnounced("READY_TO_DESTROY") == false )
				rtiamb.tick();
			
			rtiamb.synchronizationPointAchieved( "READY_TO_DESTROY" );
			logger.info( "Waiting for other federates to resign..." );
			while( fedamb.isSynchronized("READY_TO_DESTROY") == false )
				rtiamb.tick();
			
			logger.info( "Resigning and destroying federation" );
			rtiamb.resignFederationExecution( ResignAction.NO_ACTION );
			rtiamb.destroyFederationExecution( federationName );
		}
		catch( Throwable e )
		{
			logger.error( e, e );
		}
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
		configuration.setFederateName( "exmanager" );

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
		
		new ExecutionManager(configuration).run();
	}
	
	/////////////////////////////////////////////////////////////
	//////////////////// Private Inner Class ////////////////////
	/////////////////////////////////////////////////////////////
	private class ExmanagerFederateAmbassador extends PGFederateAmbassador
	{
	}

	/////////////////////////////////////////////////////////////
	//////////////////// Private Inner Class ////////////////////
	/////////////////////////////////////////////////////////////
	private class InputWaiter extends Thread
	{
		private InputWaiter()
		{
			super( "exmanager" );
		}

		public void run()
		{
			logger.info( " >>>>> press return to begin the test <<<<< " );
			try
			{
				BufferedReader reader = new BufferedReader( new InputStreamReader(System.in) );
				reader.readLine();
			}
			catch( Exception e )
			{
				throw new RuntimeException( e );
			}
		}
	}
}
