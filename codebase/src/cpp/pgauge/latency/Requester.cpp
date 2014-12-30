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
#include "Requester.h"

#include "../shared/PGUtilities.h"

PGAUGE_NAMESPACE

//////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////// Constructors ////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////
Requester::Requester( PGConfiguration configuration )
{
	this->configuration = configuration;
	this->logger = new Logger( "pgauge.requester" );
	this->rtiamb = new RTI::RTIambassador();
	this->fedamb = new RequesterFederateAmbassador( configuration, logger );
	this->dataset = LatencyDataset();
}

Requester::~Requester()
{
	delete this->logger;
	delete this->rtiamb;
	delete this->fedamb;
	delete this->fom;
}

//////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////// Instance Methods //////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////
void Requester::runFederate()
{
	initialize();
	synchronize( "READY_TO_RUN" );

	// wait for the resign point to be announced, because the responder wait for that to
	// happen before responding to anything, so it'll screw up results for the first iteration
	// if we don't wait!
	while( fedamb->isAnnounced("READY_TO_RESIGN") == RTI::RTI_FALSE )
		rtiamb->tick();

	logger->info( "Commencing for %d iterations", configuration.getIterations() );
	int payloadSize = configuration.getPayload();
	for( int i = 1; i <= configuration.getIterations(); i++ )
	{
		// send the ping request
		RTI::ParameterHandleValuePairSet *phvps = fom->pingPHVPS( payload.c_str(), payloadSize );
		rtiamb->sendInteraction( fom->ping(), *phvps, "" );
		delete phvps;
		
		// start the clock
		unsigned long startTime = PGUtilities::currentTimeMicros();

		// wait for the response
		while( fedamb->received == false )
			rtiamb->tick();
		
		// record the time
		unsigned long endTime = PGUtilities::currentTimeMicros();
		dataset.record( endTime-startTime );
		
		// reset the flag
		fedamb->received = false;
		
		// print a notice if we're at that point
		if( (i % configuration.getPrintInterval()) == 0 )
			logger->info( "Completed %d iterations", i );
	}
	
	synchronize( "READY_TO_RESIGN" );
	cleanup();
	printResults();

	if( configuration.isLog() == RTI::RTI_TRUE )
		dataset.writeToFile( "requester.log" );
}

void Requester::initialize()
{
	logger->info( "Initializing Requester Federate" );
	
	// join the federation
	rtiamb->joinFederationExecution( configuration.getFederateName().c_str(),
	                                 configuration.getFederationName().c_str(),
	                                 fedamb );
	
	// initialize the handles
	this->fom = new PGFom( rtiamb );

	// publish and subscribe
	rtiamb->publishInteractionClass( fom->ping() );
	rtiamb->subscribeInteractionClass( fom->pingAck() );
	
	// pre-fill payload data with random stuff
	// ASCII lower case characters 97-122
	this->payload = "";
	int i = 0;
	for( i = 0; i < configuration.getPayload(); i++ )
	{
		int randomCharacter;
		randomCharacter = rand() % 26;
		this->payload += (char)(randomCharacter+97);
	}
	
	// print configuration information
	logger->info( "=== (LatencyRequester) ====================" );
	logger->info( " federateName    : %s", configuration.getFederateName().c_str() );
	logger->info( " federationName  : %s", configuration.getFederationName().c_str() );
	logger->info( " iterations      : %d", configuration.getIterations() );
	logger->info( " payloadSize     : %s", PGUtilities::bytesToString(configuration.getPayload()).c_str() );
	logger->info( "===========================================" );
}

void Requester::cleanup()
{
	rtiamb->resignFederationExecution( RTI::DELETE_OBJECTS_AND_RELEASE_ATTRIBUTES );
}

void Requester::printResults()
{
	logger->info( "=== (LatencyRequester) ====================" );
	logger->info( " federateName       : %s", configuration.getFederateName().c_str() );
	logger->info( " iterations         : %d", configuration.getIterations() );
	logger->info( " average latency    : %u microseconds", dataset.getAverage() );
	logger->info( " 80%% average        : %u microseconds", dataset.getEightyAverage() );
	logger->info( " average (2std.dev) : %u microseconds", dataset.getAverageWithinTwoStandardDeviations() );
	logger->info( " standard deviation : %f", dataset.getStandardDeviation() );
	logger->info( " lowest rountrip    : %u microseconds", dataset.getLow() );
	logger->info( " highest rountrip   : %u microseconds", dataset.getHigh() );
	logger->info( "===========================================" );
}

void Requester::synchronize( const char *label )
{
	// wait for the announcement
	while( fedamb->isAnnounced(label) == false )
		rtiamb->tick();
	
	// signal that we're there
	rtiamb->synchronizationPointAchieved( label );
	
	// wait for the federation to come along
	while( fedamb->isSynchronized(label) == false )
		rtiamb->tick();
}

//////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////// Static Methods ///////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////

PGAUGE_NAMESPACE_END
