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
#ifndef PGFOM_H_
#define PGFOM_H_

#include "../common.h"

PGAUGE_NAMESPACE

class PGFom
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private:
		RTI::RTIambassador *rtiamb;
		RTI::ObjectClassHandle _pgauge;
		RTI::AttributeHandle _pgaugePayload;
		RTI::InteractionClassHandle _ping;
		RTI::ParameterHandle _pingPayload;
		RTI::InteractionClassHandle _pingAck;
		RTI::ParameterHandle _pingAckPayload;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public:
		PGFom( RTI::RTIambassador *rtiamb );
		virtual ~PGFom();

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public:
		RTI::ObjectClassHandle pgauge();
		RTI::AttributeHandle pgaugePayload();
		RTI::InteractionClassHandle ping();
		RTI::ParameterHandle pingPayload();
		RTI::InteractionClassHandle pingAck();
		RTI::ParameterHandle pingAckPayload();

		RTI::AttributeHandleSet* pgaugeHandleSet();
		RTI::AttributeHandleValuePairSet* pgaugeAHVPS( const char *payload, int size );
		RTI::ParameterHandleValuePairSet* pingPHVPS( const char *payload, int size );
		RTI::ParameterHandleValuePairSet* pingAckPHVPS( const char *payload, int size );

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

};

PGAUGE_NAMESPACE_END

#endif /* PGFOM_H_ */
