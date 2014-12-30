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
#include "ExecutionManager.h"

PGAUGE_NAMESPACE

//////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////// Constructors ////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////
ExecutionManager::ExecutionManager( PGConfiguration configuration )
{
	this->ready = RTI::RTI_FALSE;
	this->configuration = configuration;
	this->logger = new Logger( "exmanager" );
	this->rtiamb = new RTI::RTIambassador();
	this->fedamb = new PGFederateAmbassador( configuration, logger );
}

ExecutionManager::~ExecutionManager()
{
	delete this->rtiamb;
	delete this->fedamb;
	delete this->logger;
}

//////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////// Instance Methods //////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////
void ExecutionManager::run()
{
	logger->info( "Creating and joining federation..." );
	
	// create and join the federation
	rtiamb->createFederationExecution( configuration.getFederationName().c_str(),
	                                   configuration.getFedFile().c_str() );
	rtiamb->joinFederationExecution( "exmanager",
	                                 configuration.getFederationName().c_str(),
	                                 fedamb );
	logger->info( "Created and joined federation [%s] as [exmanager]",
	              configuration.getFederationName().c_str() );
	this->ready = RTI::RTI_TRUE;

	
	// announce the ready-to-run sync point and wait for the used to hit enter before
	// achieving it, thus holding up other federates until we are ready
	rtiamb->registerFederationSynchronizationPoint( "READY_TO_RUN", "" );
	logger->info( " >>>>> press return to begin the test <<<<< " );
	cin.get();

	rtiamb->synchronizationPointAchieved( "READY_TO_RUN" );
	while( fedamb->isSynchronized("READY_TO_RUN") == RTI::RTI_FALSE )
		rtiamb->tick();

	// announce the ready-to-resign sync point and wait until it is synchronized on
	// the test federates will sync on this when they're prepared to end the test
	rtiamb->registerFederationSynchronizationPoint( "READY_TO_RESIGN", "" );
	while( fedamb->isAnnounced("READY_TO_RESIGN") == RTI::RTI_FALSE )
		rtiamb->tick();

	rtiamb->synchronizationPointAchieved( "READY_TO_RESIGN" );
	logger->info( "Waiting for other federates to finish simulating..." );
	while( fedamb->isSynchronized("READY_TO_RESIGN") == RTI::RTI_FALSE )
		rtiamb->tick();

	// register the final point and wait for it to be achieved. no other federates should
	// take notice of this point or achieve it, rather, they should just resign. This way,
	// it will be achieved when we are the last federate left
	rtiamb->registerFederationSynchronizationPoint( "READY_TO_DESTROY", "" );
	while( fedamb->isAnnounced("READY_TO_DESTROY") == RTI::RTI_FALSE )
		rtiamb->tick();

	rtiamb->synchronizationPointAchieved( "READY_TO_DESTROY" );
	logger->info( "Waiting for other federates to resign..." );
	while( fedamb->isSynchronized("READY_TO_DESTROY") == RTI::RTI_FALSE )
		rtiamb->tick();
	
	logger->info( "Resigning and destroying federation" );
	rtiamb->resignFederationExecution( RTI::DELETE_OBJECTS_AND_RELEASE_ATTRIBUTES );
	rtiamb->destroyFederationExecution( configuration.getFederationName().c_str() );
}

RTI::Boolean ExecutionManager::isReady()
{
	return this->ready;
}

//////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////// Static Methods ///////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////

PGAUGE_NAMESPACE_END
