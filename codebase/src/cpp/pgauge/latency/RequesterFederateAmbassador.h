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
#ifndef REQUESTERFEDERATEAMBASSADOR_H_
#define REQUESTERFEDERATEAMBASSADOR_H_

#include "../shared/Logger.h"
#include "../shared/PGConfiguration.h"
#include "../shared/PGFederateAmbassador.h"

PGAUGE_NAMESPACE

class RequesterFederateAmbassador : public PGFederateAmbassador
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	public:
		bool received;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public:
		RequesterFederateAmbassador( PGConfiguration configuration, Logger *logger );
		virtual ~RequesterFederateAmbassador() throw( RTI::FederateInternalError );

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

#endif /* REQUESTERFEDERATEAMBASSADOR_H_ */
