/*
 *   Copyright 2009 The Portico Project
 *
 *   This file is part of pgauge.
 *
 *   portico is free software; you can redistribute it and/or modify
 *   it under the terms of the Common Developer and Distribution License (CDDL)
 *   as published by Sun Microsystems. For more information see the LICENSE file.
 *
 *   Use of this software is strictly AT YOUR OWN RISK!!!
 *   If something bad happens you do not have permission to come crying to me.
 *   (that goes for your lawyer as well)
 *
 */
#include "shared/PGConfiguration.h"
#include "shared/ExecutionManager.h"
#include "throughput/ThroughputSender.h"
#include "throughput/ThroughputListener.h"
#include "latency/Requester.h"
#include "latency/Responder.h"

using namespace pgauge;

int main( int argc, char** argv )
{
	// parse the command line
	PGConfiguration *configuration = new PGConfiguration();
	configuration->parse( argc, argv );
	
	if( configuration->getScenario() == "exmanager" )
	{
		ExecutionManager exManager( *configuration );
		exManager.run();
	}
	else if( configuration->getScenario() == "throughput-sender" )
	{
		if( configuration->getFederateName() == "unknown" )
			configuration->setFederateName( "sender" );
		
		ThroughputSender sender( *configuration );
		sender.runFederate();
	}
	else if( configuration->getScenario() == "throughput-listener" )
	{
		if( configuration->getFederateName() == "unknown" )
			configuration->setFederateName( "listener" );
		
		ThroughputListener listener( *configuration );
		listener.runFederate();
	}
	else if( configuration->getScenario() == "latency-requester" )
	{
		if( configuration->getFederateName() == "unknown" )
			configuration->setFederateName( "requester" );
		
		Requester requester( *configuration );
		requester.runFederate();
	}
	else if( configuration->getScenario() == "latency-responder" )
	{
		if( configuration->getFederateName() == "unknown" )
			configuration->setFederateName( "responder" );
		
		Responder responder( *configuration );
		responder.runFederate();
	}
	else
	{
		Logger logger( "pgauge" );
		logger.info( "Unknown scenario: %s", configuration->getScenario().c_str() );
	}
	
	return 0;
}
