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
#include "ThroughputListener.h"

#include "../shared/PGUtilities.h"

PGAUGE_NAMESPACE

//////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////// Constructors ////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////
ThroughputListener::ThroughputListener( PGConfiguration configuration )
{
	this->configuration = configuration;
	
	this->logger = new Logger( "pgauge.listener" );
	this->rtiamb = new RTI::RTIambassador();
	this->fedamb = new PGFederateAmbassador( configuration, logger );
}

ThroughputListener::~ThroughputListener()
{
	delete this->logger;
	delete this->rtiamb;
	delete this->fedamb;
	delete this->fom;
}

//////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////// Instance Methods //////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////
void ThroughputListener::runFederate()
{
	initialize();
	synchronize( "READY_TO_RUN" );
	logger->info( "Commencing for %d iterations", configuration.getIterations() );

	this->startTime = PGUtilities::currentTimeMillis();
	
	while( fedamb->discoveredObjects.size() > 0 )
		rtiamb->tick();
	
	this->endTime = PGUtilities::currentTimeMillis();

	synchronize( "READY_TO_RESIGN" );
	
	cleanup();
	printResults();
}

void ThroughputListener::initialize()
{
	logger->info( "Initializing Listener Federate" );
	
	// join the federation
	rtiamb->joinFederationExecution( configuration.getFederateName().c_str(),
	                                 configuration.getFederationName().c_str(),
	                                 fedamb );
	
	// initialize the handles
	this->fom = new PGFom( rtiamb );

	// enable time policy
	/*if( configuration.isTimestepped() )
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
	}*/
	
	// publish and subscribe
	RTI::AttributeHandleSet *handleset = fom->pgaugeHandleSet();
	rtiamb->subscribeObjectClassAttributes( fom->pgauge(), *handleset );
	delete handleset;

	// wait to discover the objects we are expecting
	while( fedamb->discoveredObjects.size() < (unsigned)configuration.getObjects() )
		rtiamb->tick();
	
	// print configuration
	logger->info( "=== (ThroughputListener) ==================" );
	logger->info( " federateName     : %s", configuration.getFederateName().c_str() );
	logger->info( " federationName   : %s", configuration.getFederationName().c_str() );
	logger->info( " timestepped      : %d", configuration.isTimestepped() );
	logger->info( " expectedObjects  : %d", configuration.getObjects() );
	logger->info( "===========================================" );
}

void ThroughputListener::cleanup()
{
	rtiamb->resignFederationExecution( RTI::DELETE_OBJECTS_AND_RELEASE_ATTRIBUTES );
}

void ThroughputListener::printResults()
{
	unsigned long durationMillis = endTime - startTime;
	double durationSeconds = ((double)durationMillis) / 1000.0;
	int reflectionsPerSecond = (int)(fedamb->updateCount / durationSeconds);
	
	logger->info( "=== (ThroughputListener) ==================" );
	logger->info( " federateName  : %s", configuration.getFederateName().c_str() );
	logger->info( " execution time: %dms", durationMillis );
	logger->info( " reflections   : %d (%d/s)", fedamb->updateCount, reflectionsPerSecond );
	logger->info( "===========================================" );
}

void ThroughputListener::synchronize( const char *label )
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
