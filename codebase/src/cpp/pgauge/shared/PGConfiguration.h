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
#ifndef PGCONFIGURATION_H_
#define PGCONFIGURATION_H_

#include "../common.h"

PGAUGE_NAMESPACE

class PGConfiguration
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private:
		// strings used to identify arguments in command line configuration arguments
		static string PROP_FEDERATE_NAME;
		static string PROP_FEDERATION_NAME;
		static string PROP_FED_FILE;
		static string PROP_TIMESTEPPED;
		static string PROP_ITERATIONS;
		static string PROP_PRINT_INTERVAL;
		static string PROP_RECORD_INTERVAL;
		static string PROP_LOG;
		static string PROP_PAYLOAD_SIZE;
		static string PROP_OBJECTS;
		static string PROP_SCENARIO;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private:
		string federateName;
		string federationName;
		string fedfile;
		RTI::Boolean timestepped;
		int iterations;
		int printInterval;
		int recordInterval;
		RTI::Boolean log;
		int payload;
		int objects;
		string scenario;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public:
		PGConfiguration();
		virtual ~PGConfiguration();

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public:
		string getFederateName();
		void setFederateName( string name );
		string getFederationName();
		void setFederationName( string name );
		string getFedFile();
		void setFedFile( string location );
		RTI::Boolean isTimestepped();
		void setTimestepped( RTI::Boolean timestepped );
		int getIterations();
		void setIterations( int iterations );
		int getPrintInterval();
		void setPrintInterval( int interval );
		int getRecordInterval();
		void setRecordInterval( int interval );
		RTI::Boolean isLog();
		void setLog( RTI::Boolean log );
		int getPayload();
		void setPayload( int payload );
		int getObjects();
		void setObjects( int objects );
		string getScenario();
		void setScenario( string scenario );
		
		void parse( int count, char** commandline );

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

};

PGAUGE_NAMESPACE_END

#endif /* PGCONFIGURATION_H_ */
