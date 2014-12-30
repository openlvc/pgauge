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
#ifndef PGFEDERATEAMBASSADOR_H_
#define PGFEDERATEAMBASSADOR_H_

#include "../common.h"
#include "NullFederateAmbassador.hh"
#include "PGConfiguration.h"
#include "Logger.h"

PGAUGE_NAMESPACE

class PGFederateAmbassador : public NullFederateAmbassador
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected:
		map<string,RTI::Boolean> synchronizationPoints;
		PGConfiguration configuration;
		Logger *logger;

	public:
		RTI::Boolean constrained;
		RTI::Boolean regulating;
		double federateTime;
		set<RTI::ObjectHandle> discoveredObjects;
		unsigned long updateCount;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public:
		PGFederateAmbassador( PGConfiguration configuration, Logger *logger );
		virtual ~PGFederateAmbassador() throw( RTI::FederateInternalError );

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public:
		RTI::Boolean isAnnounced( string label );
		RTI::Boolean isSynchronized( string label );
		RTI::Boolean hasTimeReached( double time );

		// federate ambassador methods
		virtual void announceSynchronizationPoint( const char *label, const char *tag )
			throw( RTI::FederateInternalError );
	
		virtual void federationSynchronized( const char *label )
			throw( RTI::FederateInternalError );

		virtual void timeRegulationEnabled( const RTI::FedTime& theFederateTime )
			throw( RTI::InvalidFederationTime,
			       RTI::EnableTimeRegulationWasNotPending,
			       RTI::FederateInternalError );
	
		virtual void timeConstrainedEnabled( const RTI::FedTime& federateTime )
			throw( RTI::InvalidFederationTime,
			       RTI::EnableTimeConstrainedWasNotPending,
			       RTI::FederateInternalError );
	
		virtual void timeAdvanceGrant( const RTI::FedTime& theTime )
			throw( RTI::InvalidFederationTime,
			       RTI::TimeAdvanceWasNotInProgress,
			       RTI::FederateInternalError );

		virtual void discoverObjectInstance( RTI::ObjectHandle theObject,
		                                     RTI::ObjectClassHandle theClass,
		                                     const char *theName )
			throw ( RTI::CouldNotDiscover, RTI::ObjectClassNotKnown, RTI::FederateInternalError );

		virtual void reflectAttributeValues( RTI::ObjectHandle theObject,
		                                     const RTI::AttributeHandleValuePairSet& theAttributes,
		                                     const RTI::FedTime& theTime,
		                                     const char *theTag,
		                                     RTI::EventRetractionHandle theHandle )
			throw( RTI::ObjectNotKnown,
		           RTI::AttributeNotKnown,
		           RTI::FederateOwnsAttributes,
		           RTI::InvalidFederationTime,
		           RTI::FederateInternalError );

		virtual void reflectAttributeValues( RTI::ObjectHandle theObject,
		                                     const RTI::AttributeHandleValuePairSet& theAttributes,
		                                     const char *theTag )
			throw( RTI::ObjectNotKnown,
		           RTI::AttributeNotKnown,
		           RTI::FederateOwnsAttributes,
		           RTI::FederateInternalError );

		virtual void receiveInteraction( RTI::InteractionClassHandle theInteraction,
		                                 const RTI::ParameterHandleValuePairSet& theParameters,
		                                 const RTI::FedTime& theTime,
		                                 const char *theTag,
		                                 RTI::EventRetractionHandle theHandle )
			throw( RTI::InteractionClassNotKnown,
		           RTI::InteractionParameterNotKnown,
		           RTI::InvalidFederationTime,
		           RTI::FederateInternalError );

		virtual void receiveInteraction( RTI::InteractionClassHandle theInteraction,
		                                 const RTI::ParameterHandleValuePairSet& theParameters,
		                                 const char *theTag )
			throw( RTI::InteractionClassNotKnown,
		           RTI::InteractionParameterNotKnown,
		           RTI::FederateInternalError );

		virtual void removeObjectInstance( RTI::ObjectHandle theObject,
		                                   const char *theTag )
			throw( RTI::ObjectNotKnown,
		           RTI::FederateInternalError );

		virtual void removeObjectInstance( RTI::ObjectHandle theObject,
		                                   const RTI::FedTime& theTime,
		                                   const char *theTag,
		                                   RTI::EventRetractionHandle theHandle )
			throw( RTI::ObjectNotKnown,
		           RTI::InvalidFederationTime,
		           RTI::FederateInternalError );

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

};

PGAUGE_NAMESPACE_END

#endif /* PGFEDERATEAMBASSADOR_H_ */
