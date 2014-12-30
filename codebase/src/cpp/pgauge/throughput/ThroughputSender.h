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
#ifndef THROUGHPUTSENDER_H_
#define THROUGHPUTSENDER_H_

#include "../common.h"
#include "../shared/PGConfiguration.h"
#include "../shared/PGFederateAmbassador.h"
#include "../shared/PGFom.h"
#include "../shared/Logger.h"

PGAUGE_NAMESPACE

class ThroughputSender
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
		PGFederateAmbassador *fedamb;
		PGFom *fom;
		set<RTI::ObjectHandle> objectHandles;
		string payload;
		
		// results
		unsigned long startTime;
		unsigned long endTime;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public:
		ThroughputSender( PGConfiguration configuration );
		virtual ~ThroughputSender();

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public:
		void runFederate();
	
	private:
		void initialize();
		void simulate( int iteration );
		void cleanup();
		void printResults();
		void synchronize( const char *label );

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

};

PGAUGE_NAMESPACE_END

#endif /* THROUGHPUTSENDER_H_ */
