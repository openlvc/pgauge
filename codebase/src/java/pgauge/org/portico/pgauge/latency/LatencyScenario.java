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

import org.apache.log4j.Logger;
import org.portico.pgauge.ExecutionManager;
import org.portico.pgauge.PGConfiguration;
import org.portico.pgauge.PGUtilities;

public class LatencyScenario
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
	public LatencyScenario( PGConfiguration scenarioConfiguration )
	{
		this.scenarioConfiguration = scenarioConfiguration;
		this.logger = PGUtilities.getLogger( "pgauge.scenario" );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public void runScenario() throws Exception
	{
		// create the requester and responder
		PGConfiguration requesterConfiguration = (PGConfiguration)scenarioConfiguration.clone();
		requesterConfiguration.setFederateName( "requester" );
		Requester requester = new Requester( requesterConfiguration );

		PGConfiguration responderConfiguration = (PGConfiguration)scenarioConfiguration.clone();
		responderConfiguration.setFederateName( "responder" );
		Responder responder = new Responder( responderConfiguration );

		// create an exmanager and kick it off
		ExecutionManager exmanager = new ExecutionManager( scenarioConfiguration );
		Thread exthread = new Thread( exmanager, "exmanager" );
		logger.info( "String Execution Manager" );
		exthread.start();

		while( exmanager.isReady() == false )
			Thread.sleep( 50 );

		// start the requester and responder
		Thread requesterThread = new Thread( requester, "requester" );
		requesterThread.start();
		while( requester.isReady() == false )
			Thread.sleep( 50 ); // just to get the configuration printing nice and separated
		
		Thread responderThread = new Thread( responder, "responder" );
		responderThread.start();
		
		// wait for everyone to finish
		while( exthread.isAlive() )
			exthread.join();
		
		// print the results one at a time so they're legible
		logger.info( "Scenario over, printing results" );
		requester.printResults();
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
