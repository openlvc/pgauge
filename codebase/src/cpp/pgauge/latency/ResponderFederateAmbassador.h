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
#ifndef RESPONDERFEDERATEAMBASSADOR_H_
#define RESPONDERFEDERATEAMBASSADOR_H_

#include "../shared/PGConfiguration.h"
#include "../shared/PGFederateAmbassador.h"
#include "../shared/Logger.h"

PGAUGE_NAMESPACE

class ResponderFederateAmbassador : public PGFederateAmbassador
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	public:
		bool requestReceived;
		unsigned int pingsReceived;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public:
		ResponderFederateAmbassador( PGConfiguration configuration, Logger *logger );
		virtual ~ResponderFederateAmbassador() throw( RTI::FederateInternalError );

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public:
		virtual void receiveInteraction( RTI::InteractionClassHandle theInteraction,
		                                 const RTI::ParameterHandleValuePairSet& theParameters,
		                                 const char *theTag )
			throw( RTI::InteractionClassNotKnown,
		           RTI::InteractionParameterNotKnown,
		           RTI::FederateInternalError );

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

};

PGAUGE_NAMESPACE_END

#endif /* RESPONDERFEDERATEAMBASSADOR_H_ */
