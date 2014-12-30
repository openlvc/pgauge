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
#ifndef EXECUTIONMANAGER_H_
#define EXECUTIONMANAGER_H_

#include "PGConfiguration.h"
#include "PGFederateAmbassador.h"
#include "Logger.h"
#include "../common.h"

PGAUGE_NAMESPACE

class ExecutionManager
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private:
		PGConfiguration configuration;
		RTI::RTIambassador *rtiamb;
		PGFederateAmbassador *fedamb;
		Logger *logger;
		RTI::Boolean ready;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public:
		ExecutionManager( PGConfiguration configuration );
		virtual ~ExecutionManager();

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public:
		void run();
		RTI::Boolean isReady();
		
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

};

PGAUGE_NAMESPACE_END

#endif /* EXECUTIONMANAGER_H_ */
