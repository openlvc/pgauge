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
#ifndef REQUESTER_H_
#define REQUESTER_H_

#include "../common.h"
#include "../shared/PGConfiguration.h"
#include "../shared/PGFom.h"
#include "../shared/Logger.h"

#include "LatencyDataset.h"
#include "RequesterFederateAmbassador.h"

PGAUGE_NAMESPACE

class Requester
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private:
		PGConfiguration configuration;
		Logger *logger;
		RTI::RTIambassador *rtiamb;
		RequesterFederateAmbassador *fedamb;
		PGFom *fom;
		string payload;
		LatencyDataset dataset;
		
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public:
		Requester( PGConfiguration configuration );
		virtual ~Requester();

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public:
		void runFederate();
	
	private:
		void initialize();
		void cleanup();
		void printResults();
		void synchronize( const char *label );

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

};

PGAUGE_NAMESPACE_END

#endif /* REQUESTER_H_ */
