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
#include "RequesterFederateAmbassador.h"

PGAUGE_NAMESPACE

//////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////// Constructors ////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////
RequesterFederateAmbassador::RequesterFederateAmbassador( PGConfiguration configuration,
                                                          Logger *logger )
	: PGFederateAmbassador( configuration, logger )
{
	this->received = false;
}

RequesterFederateAmbassador::~RequesterFederateAmbassador() throw( RTI::FederateInternalError )
{
	 
}

//////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////// Instance Methods //////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////
void RequesterFederateAmbassador::receiveInteraction( RTI::InteractionClassHandle theInteraction,
                                                      const RTI::ParameterHandleValuePairSet& theParameters,
                                                      const char *theTag )
	throw( RTI::InteractionClassNotKnown,
           RTI::InteractionParameterNotKnown,
           RTI::FederateInternalError )
{
	this->received = true;
}

//////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////// Static Methods ///////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////

PGAUGE_NAMESPACE_END
