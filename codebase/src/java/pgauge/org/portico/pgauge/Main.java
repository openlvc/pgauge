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

import org.apache.log4j.Logger;
import org.portico.pgauge.latency.LatencyScenario;
import org.portico.pgauge.objectcreation.ObjectCreationScenario;
import org.portico.pgauge.throughput.ThroughputScenario;

public class Main
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static void main( String[] args ) throws Exception
	{
		// check for a help request
		for( String temp : args )
		{
			if( temp.equals("--help") )
			{
				System.out.println( PGConfiguration.usage() );
				return;
			}
		}

		// parse the command line arguments and get our configuration
		PGConfiguration configuration = PGConfiguration.defaultConfiguration();
		configuration.parse( args );
		
		if( configuration.isThroughputScenario() )
		{
			new ThroughputScenario(configuration).runScenario();
		}
		else if( configuration.isLatencyScenario() )
		{
			new LatencyScenario(configuration).runScenario();
		}
		else if( configuration.isObjectCreationScenario() )
		{
			new ObjectCreationScenario(configuration).runScenario();
		}
		else
		{
			Logger logger = PGUtilities.getLogger( "pgauge" );
			logger.error( "No scenario provided" );
			logger.error( PGConfiguration.usage() );
		}
	}
}
