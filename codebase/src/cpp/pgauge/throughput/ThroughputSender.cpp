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
#include "ThroughputSender.h"
#include "../shared/PGUtilities.h"

PGAUGE_NAMESPACE

//////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////// Constructors ////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////
ThroughputSender::ThroughputSender( PGConfiguration configuration )
{
	this->configuration = configuration;
	this->objectHandles = set<RTI::ObjectHandle>();
	
	this->logger = new Logger( "pgauge.sender" );
	this->rtiamb = new RTI::RTIambassador();
	this->fedamb = new PGFederateAmbassador( configuration, logger );
}

ThroughputSender::~ThroughputSender()
{
	delete this->logger;
	delete this->rtiamb;
	delete this->fedamb;
	delete this->fom;
}

//////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////// Instance Methods //////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////
void ThroughputSender::runFederate()
{
	initialize();
	synchronize( "READY_TO_RUN" );
	logger->info( "Commencing for %d iterations", configuration.getIterations() );

	this->startTime = PGUtilities::currentTimeMillis();
	
	int i;
	for( i = 1; i <= configuration.getIterations(); i++ )
	{
		simulate( i );
		rtiamb->tick();
	}
	
	this->endTime = PGUtilities::currentTimeMillis();

	// completed, delete the objects we were updating
	set<RTI::ObjectHandle>::iterator iterator;
	for( iterator = objectHandles.begin(); iterator != objectHandles.end(); iterator++ )
		rtiamb->deleteObjectInstance( *iterator, "" );
	
	synchronize( "READY_TO_RESIGN" );
	
	cleanup();
	printResults();
}

void ThroughputSender::simulate( int iteration )
{
	int payloadSize = configuration.getPayload();
	set<RTI::ObjectHandle>::iterator iterator;
	for( iterator = objectHandles.begin(); iterator != objectHandles.end(); iterator++ )
	{
		RTI::AttributeHandleValuePairSet *ahvps = fom->pgaugeAHVPS( payload.c_str(), payloadSize );
		rtiamb->updateAttributeValues( *iterator, *ahvps, "" );
		delete ahvps;
	}
	
	// advance time if we are timestepped
	if( configuration.isTimestepped() )
	{
		double newtime = ++fedamb->federateTime;
		const RTIfedTime ftime( newtime ); 
		rtiamb->timeAdvanceRequest( ftime );
		while( fedamb->federateTime < newtime )
			rtiamb->tick();
	}
	
	if( (iteration % configuration.getPrintInterval()) == 0 )
		logger->info( "Completed %d iterations", iteration );
}

void ThroughputSender::initialize()
{
	logger->info( "Initializing Sender Federate" );
	
	// join the federation
	rtiamb->joinFederationExecution( configuration.getFederateName().c_str(),
	                                 configuration.getFederationName().c_str(),
	                                 fedamb );
	
	// initialize the handles
	this->fom = new PGFom( rtiamb );

	// enable time policy
	if( configuration.isTimestepped() )
	{
		rtiamb->enableAsynchronousDelivery();
		rtiamb->enableTimeConstrained();
		while( fedamb->constrained == RTI::RTI_FALSE )
			rtiamb->tick();
		
		const RTIfedTime time( 0.0 );
		const RTIfedTime lookahead( 1.0 );
		rtiamb->enableTimeRegulation( time, lookahead );
		while( fedamb->regulating == RTI::RTI_FALSE )
			rtiamb->tick();
	}
	
	// publish and subscribe
	RTI::AttributeHandleSet *handleset = fom->pgaugeHandleSet();
	rtiamb->publishObjectClass( fom->pgauge(), *handleset );
	delete handleset;
	
	// register all object we will update
	int i = 0;
	for( i = 0; i < configuration.getObjects(); i++ )
		objectHandles.insert( rtiamb->registerObjectInstance(fom->pgauge()) );
	
	// pre-fill payload data with random stuff
	// ASCII lower case characters 97-122
	this->payload = "";
	for( i = 0; i < configuration.getPayload(); i++ )
	{
		int randomCharacter;
		randomCharacter = rand() % 26;
		this->payload += (char)(randomCharacter+97);
	}
	
	// print configuration information
	logger->info( "=== (ThroughputSender) ====================" );
	logger->info( " federateName     : %s", configuration.getFederateName().c_str() );
	logger->info( " federationName   : %s", configuration.getFederationName().c_str() );
	logger->info( " timestepped      : %d", configuration.isTimestepped() );
	logger->info( " iterations       : %d", configuration.getIterations() );
	logger->info( " objects          : %d", configuration.getObjects() );
	logger->info( " payloadSize      : %s", PGUtilities::bytesToString(configuration.getPayload()).c_str() );
	logger->info( "===========================================" );
}

void ThroughputSender::cleanup()
{
	rtiamb->resignFederationExecution( RTI::DELETE_OBJECTS_AND_RELEASE_ATTRIBUTES );
}

void ThroughputSender::printResults()
{
	// calculate how long the sending took overall, and what the average/s was
	int iterations = configuration.getIterations();
	unsigned long updates = iterations * configuration.getObjects();
	unsigned long durationMillis = endTime - startTime;
	double durationSeconds = ((double)durationMillis) / 1000.0;
	int average = (int)(updates/durationSeconds);
	int throughput = (int)((updates*configuration.getPayload()) / durationSeconds);
	string throughputString = PGUtilities::bytesToString( throughput );

	logger->info( "=== (ThroughputSender) ====================" );
	logger->info( " federateName  : %s", configuration.getFederateName().c_str() );
	logger->info( " execution time: %dms", durationMillis );
	logger->info( " updateRate    : %d updates/s (%s/s)", average, throughputString.c_str() );
	logger->info( "===========================================" );
}

void ThroughputSender::synchronize( const char *label )
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
