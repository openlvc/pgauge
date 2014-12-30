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
#include "PGFom.h"

PGAUGE_NAMESPACE

//////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////// Constructors ////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////
PGFom::PGFom( RTI::RTIambassador *rtiamb )
{
	this->rtiamb = rtiamb;
	this->_pgauge         = rtiamb->getObjectClassHandle( "ObjectRoot.PGauge" );
	this->_pgaugePayload  = rtiamb->getAttributeHandle( "payload", this->_pgauge );
	this->_ping           = rtiamb->getInteractionClassHandle( "InteractionRoot.Ping" );
	this->_pingPayload    = rtiamb->getParameterHandle( "payload", this->_ping );
	this->_pingAck        = rtiamb->getInteractionClassHandle( "InteractionRoot.PingAck" );
	this->_pingAckPayload = rtiamb->getParameterHandle( "payload", this->_pingAck );
}

PGFom::~PGFom()
{
}

//////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////// Instance Methods //////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////
RTI::ObjectClassHandle PGFom::pgauge()
{
	return this->_pgauge; 
}

RTI::AttributeHandle PGFom::pgaugePayload()
{
	return this->_pgaugePayload;
}

RTI::InteractionClassHandle PGFom::ping()
{
	return this->_ping;
}

RTI::ParameterHandle PGFom::pingPayload()
{
	return this->_pingPayload;
}

RTI::InteractionClassHandle PGFom::pingAck()
{
	return this->_pingAck;
}

RTI::ParameterHandle PGFom::pingAckPayload()
{
	return this->_pingAckPayload;
}

RTI::AttributeHandleSet* PGFom::pgaugeHandleSet()
{
	RTI::AttributeHandleSet *set = RTI::AttributeHandleSetFactory::create(1);
	set->add( _pgaugePayload );
	return set;
}

RTI::AttributeHandleValuePairSet* PGFom::pgaugeAHVPS( const char *payload, int size )
{
	RTI::AttributeHandleValuePairSet *ahvps = RTI::AttributeSetFactory::create(1);
	ahvps->add( _pgaugePayload, payload, size );
	return ahvps;
}

RTI::ParameterHandleValuePairSet* PGFom::pingPHVPS( const char *payload, int size )
{
	RTI::ParameterHandleValuePairSet *phvps = RTI::ParameterSetFactory::create(1);
	phvps->add( _pingPayload, payload, size );
	return phvps;
}

RTI::ParameterHandleValuePairSet* PGFom::pingAckPHVPS( const char *payload, int size )
{
	RTI::ParameterHandleValuePairSet *phvps = RTI::ParameterSetFactory::create(1);
	phvps->add( _pingAckPayload, payload, size );
	return phvps;
}

//////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////// Static Methods ///////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////

PGAUGE_NAMESPACE_END
