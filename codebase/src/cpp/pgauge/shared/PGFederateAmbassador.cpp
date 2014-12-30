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
#include "PGFederateAmbassador.h"

PGAUGE_NAMESPACE

//////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////// Constructors ////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////
PGFederateAmbassador::PGFederateAmbassador( PGConfiguration configuration, Logger *logger )
{
	this->configuration = configuration; // we don't clean this up
	this->logger = logger;               // we don't clean this up
	this->constrained = RTI::RTI_FALSE;
	this->regulating = RTI::RTI_FALSE;
	this->federateTime = 0.0;
	this->synchronizationPoints = map<string,RTI::Boolean>();
	this->discoveredObjects = set<RTI::ObjectHandle>();
	this->updateCount = 0;
}

PGFederateAmbassador::~PGFederateAmbassador() throw( RTI::FederateInternalError )
{
}

//////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////// Instance Methods //////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////
RTI::Boolean PGFederateAmbassador::isAnnounced( string label )
{
	if( synchronizationPoints.find(label) == synchronizationPoints.end() )
		return RTI::RTI_FALSE;
	else
		return RTI::RTI_TRUE;
}

RTI::Boolean PGFederateAmbassador::isSynchronized( string label )
{
	if( isAnnounced(label) == false )
		return RTI::RTI_FALSE;
	else
		return synchronizationPoints[label];
}

RTI::Boolean PGFederateAmbassador::hasTimeReached( double time )
{
	if( this->federateTime >= time )
		return RTI::RTI_TRUE;
	else
		return RTI::RTI_FALSE;
}

void PGFederateAmbassador::announceSynchronizationPoint( const char *label, const char *tag )
	throw( RTI::FederateInternalError )
{
	string sLabel = label;
	synchronizationPoints[sLabel] = RTI::RTI_FALSE;
}

void PGFederateAmbassador::federationSynchronized( const char *label )
	throw( RTI::FederateInternalError )
{
	string sLabel = label;
	synchronizationPoints[sLabel] = RTI::RTI_TRUE;
}

void PGFederateAmbassador::timeRegulationEnabled( const RTI::FedTime& theFederateTime )
	throw( RTI::InvalidFederationTime,
	       RTI::EnableTimeRegulationWasNotPending,
	       RTI::FederateInternalError )
{
	regulating = RTI::RTI_TRUE;
	this->federateTime = ((const RTIfedTime&)theFederateTime).getTime();
}

void PGFederateAmbassador::timeConstrainedEnabled( const RTI::FedTime& federateTime )
	throw( RTI::InvalidFederationTime,
	       RTI::EnableTimeConstrainedWasNotPending,
	       RTI::FederateInternalError )
{
	constrained = RTI::RTI_TRUE;
	this->federateTime = ((const RTIfedTime&)federateTime).getTime();
}


void PGFederateAmbassador::timeAdvanceGrant( const RTI::FedTime& theTime )
	throw( RTI::InvalidFederationTime,
	       RTI::TimeAdvanceWasNotInProgress,
	       RTI::FederateInternalError )
{
	this->federateTime = ((const RTIfedTime&)theTime).getTime();
}

void PGFederateAmbassador::discoverObjectInstance( RTI::ObjectHandle theObject,
                                                   RTI::ObjectClassHandle theClass,
                                                   const char *theName )
	throw ( RTI::CouldNotDiscover, RTI::ObjectClassNotKnown, RTI::FederateInternalError )
{
	discoveredObjects.insert( theObject );
}

void PGFederateAmbassador::reflectAttributeValues( RTI::ObjectHandle theObject,
                                                   const RTI::AttributeHandleValuePairSet& theAttributes,
                                                   const RTI::FedTime& theTime,
                                                   const char *theTag,
                                                   RTI::EventRetractionHandle theHandle )
	throw( RTI::ObjectNotKnown,
           RTI::AttributeNotKnown,
           RTI::FederateOwnsAttributes,
           RTI::InvalidFederationTime,
           RTI::FederateInternalError )
{
	++updateCount;

	// log the update count if we're at the print interval
	if( (updateCount % configuration.getPrintInterval()) == 0 )
		logger->info( "Received %d updates", updateCount );

}

void PGFederateAmbassador::reflectAttributeValues( RTI::ObjectHandle theObject,
                                                   const RTI::AttributeHandleValuePairSet& theAttributes,
                                                   const char *theTag )
	throw( RTI::ObjectNotKnown,
           RTI::AttributeNotKnown,
           RTI::FederateOwnsAttributes,
           RTI::FederateInternalError )
{
	++updateCount;

	// log the update count if we're at the print interval
	if( (updateCount % configuration.getPrintInterval()) == 0 )
		logger->info( "Received %d updates", updateCount );
}

void PGFederateAmbassador::receiveInteraction( RTI::InteractionClassHandle theInteraction,
                                               const RTI::ParameterHandleValuePairSet& theParameters,
                                               const RTI::FedTime& theTime,
                                               const char *theTag,
                                               RTI::EventRetractionHandle theHandle )
	throw( RTI::InteractionClassNotKnown,
           RTI::InteractionParameterNotKnown,
           RTI::InvalidFederationTime,
           RTI::FederateInternalError )
{
	
}

void PGFederateAmbassador::receiveInteraction( RTI::InteractionClassHandle theInteraction,
                                               const RTI::ParameterHandleValuePairSet& theParameters,
                                               const char *theTag )
	throw( RTI::InteractionClassNotKnown,
           RTI::InteractionParameterNotKnown,
           RTI::FederateInternalError )
{
	
}

void PGFederateAmbassador::removeObjectInstance( RTI::ObjectHandle theObject,
                                                 const char *theTag )
	throw( RTI::ObjectNotKnown,
           RTI::FederateInternalError )
{
	discoveredObjects.erase( theObject );
}

void PGFederateAmbassador::removeObjectInstance( RTI::ObjectHandle theObject,
                                                 const RTI::FedTime& theTime,
                                                 const char *theTag,
                                                 RTI::EventRetractionHandle theHandle )
	throw( RTI::ObjectNotKnown,
           RTI::InvalidFederationTime,
           RTI::FederateInternalError )
{
	discoveredObjects.erase( theObject );
}


//////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////// Static Methods ///////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////

PGAUGE_NAMESPACE_END
