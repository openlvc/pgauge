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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.portico.pgauge.ExecutionManager;
import org.portico.pgauge.PGConfiguration;
import org.portico.pgauge.PGUtilities;

public class ObjectCreationScenario
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private PGConfiguration scenarioConfiguration;
	private Logger logger;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ObjectCreationScenario( PGConfiguration scenarioConfiguration )
	{
		this.scenarioConfiguration = scenarioConfiguration;
		this.logger = PGUtilities.getLogger( "pgauge.scenario" );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public void runScenario() throws Exception
	{
		// create the sending/receiving federates
		List<SenderThread> senders = new ArrayList<SenderThread>();
		for( String senderName : scenarioConfiguration.getSenders() )
		{
			PGConfiguration clone = (PGConfiguration)scenarioConfiguration.clone();
			clone.setFederateName( senderName );
			senders.add( new SenderThread(new Sender(clone),senderName) );
		}
		
		List<ListenerThread> listeners = new ArrayList<ListenerThread>();
		for( String listenerName : scenarioConfiguration.getListeners() )
		{
			PGConfiguration clone = (PGConfiguration)scenarioConfiguration.clone();
			clone.setFederateName( listenerName );
			listeners.add( new ListenerThread(new Listener(clone),listenerName) );
		}
		
		// create an exmanager and kick it off
		ExecutionManager exmanager = new ExecutionManager( scenarioConfiguration );
		Thread exthread = new Thread( exmanager, "exmanager" );
		logger.info( "String Execution Manager" );
		exthread.start();

		while( exmanager.isReady() == false )
			Thread.sleep( 50 );

		// log the scenario details and kick things off with the senders
		logger.info( "Starting "+senders.size()+" senders: "+scenarioConfiguration.getSenders() );
		for( SenderThread senderThread : senders )
		{
			senderThread.start();
			while( senderThread.sender.isReady() == false )
				Thread.sleep( 50 );
		}
		
		logger.info( "Starting "+listeners.size()+" listeners: " + scenarioConfiguration.getListeners() );
		for( ListenerThread listenerThread : listeners )
		{
			listenerThread.start();
			while( listenerThread.listener.isReady() == false )
				Thread.sleep( 50 );
		}
		
		// wait for everyone to finish
		while( exthread.isAlive() )
			exthread.join();
		
		// print the results one at a time so they're legible
		logger.info( "Scenario over, printing results" );
		for( SenderThread senderThread : senders )
		{
			logger.info( "Results for sender ["+senderThread.name+"]:" );
			senderThread.sender.printResults();
		}
		
		for( ListenerThread listenerThread : listeners )
		{
			logger.info( "Results for listener ["+listenerThread.name+"]:" );
			listenerThread.listener.printResults();
		}
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	private class SenderThread extends Thread
	{
		public Sender sender;
		public String name;
		public SenderThread( Sender sender, String senderName )
		{
			super( sender, senderName );
			this.sender = sender;
			this.name = senderName;
		}
	}
	
	private class ListenerThread extends Thread
	{
		public Listener listener;
		public String name;
		public ListenerThread( Listener listener, String listenerName )
		{
			super( listener, listenerName );
			this.listener = listener;
			this.name = listenerName;
		}
	}
}
