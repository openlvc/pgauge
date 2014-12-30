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
#include "Responder.h"

#include "../shared/PGUtilities.h"

PGAUGE_NAMESPACE

//////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////// Constructors ////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////
Responder::Responder( PGConfiguration configuration )
{
	this->configuration = configuration;
	this->logger = new Logger( "pgauge.responder" );
	this->rtiamb = new RTI::RTIambassador();
	this->fedamb = new ResponderFederateAmbassador( configuration, logger );
}

Responder::~Responder()
{
	delete this->logger;
	delete this->rtiamb;
	delete this->fedamb;
	delete this->fom;
}

//////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////// Instance Methods //////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////
void Responder::runFederate()
{
	initialize();
	synchronize( "READY_TO_RUN" );

	// achieve the "ready to resign point" and keep going until it is achieved
	while( fedamb->isAnnounced("READY_TO_RESIGN") == RTI::RTI_FALSE )
		rtiamb->tick();
	
	rtiamb->synchronizationPointAchieved( "READY_TO_RESIGN" );


	logger->info( "Waiting for ping requests" );
	int payloadSize = configuration.getPayload();
	while( true )
	{
		// tick for some callbacks
		rtiamb->tick();
		
		// have we received a request we need to respond to?
		if( fedamb->requestReceived )
		{
			RTI::ParameterHandleValuePairSet *phvps = fom->pingAckPHVPS( payload.c_str(), payloadSize );
			rtiamb->sendInteraction( fom->pingAck(), *phvps, "" );
			delete phvps;

			fedamb->requestReceived = false;
		}
		
		// check to see if we should finish yet
		if( fedamb->isSynchronized("READY_TO_RESIGN") == RTI::RTI_TRUE)
			break;
	}

	// clean things up and exit
	cleanup();
}

void Responder::initialize()
{
	logger->info( "Initializing Responder Federate" );
	
	// join the federation
	rtiamb->joinFederationExecution( configuration.getFederateName().c_str(),
	                                 configuration.getFederationName().c_str(),
	                                 fedamb );
	
	// initialize the handles
	this->fom = new PGFom( rtiamb );

	// publish and subscribe
	rtiamb->subscribeInteractionClass( fom->ping() );
	rtiamb->publishInteractionClass( fom->pingAck() );
	
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
	logger->info( "=== (LatencyResponder) ====================" );
	logger->info( " federateName    : %s", configuration.getFederateName().c_str() );
	logger->info( " federationName  : %s", configuration.getFederationName().c_str() );
	logger->info( " payloadSize     : %s", PGUtilities::bytesToString(configuration.getPayload()).c_str() );
	logger->info( "===========================================" );
}

void Responder::cleanup()
{
	rtiamb->resignFederationExecution( RTI::DELETE_OBJECTS_AND_RELEASE_ATTRIBUTES );
}

void Responder::printResults()
{
	logger->info( "DONE!" );
}

void Responder::synchronize( const char *label )
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
