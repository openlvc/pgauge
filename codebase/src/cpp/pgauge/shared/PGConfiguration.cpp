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
#include "PGConfiguration.h"
#include "PGUtilities.h"

PGAUGE_NAMESPACE

string PGConfiguration::PROP_FEDERATE_NAME = "federateName";
string PGConfiguration::PROP_FEDERATION_NAME = "federationName";
string PGConfiguration::PROP_FED_FILE = "fedFile";
string PGConfiguration::PROP_TIMESTEPPED = "timesteppd";
string PGConfiguration::PROP_ITERATIONS = "iterations";
string PGConfiguration::PROP_PRINT_INTERVAL = "printInterval";
string PGConfiguration::PROP_RECORD_INTERVAL = "recordInterval";
string PGConfiguration::PROP_LOG = "log";
string PGConfiguration::PROP_PAYLOAD_SIZE = "payload";
string PGConfiguration::PROP_OBJECTS = "objects";
string PGConfiguration::PROP_SCENARIO = "scenario";


//////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////// Constructors ////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////
PGConfiguration::PGConfiguration()
{
	this->federateName = "unknown";
	this->federationName = "pgauge";
	this->fedfile = "etc/pgauge.fed";
	this->timestepped = RTI::RTI_FALSE;
	this->iterations = 10000;
	this->printInterval = 1000;
	this->recordInterval = 1000;
	this->log = RTI::RTI_FALSE;
	this->payload = 4096;
	this->objects = 1;
	this->scenario = "unknown";
}

PGConfiguration::~PGConfiguration()
{
}

//////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////// Instance Methods //////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////
string PGConfiguration::getFederateName()
{
	return this->federateName;
}

void PGConfiguration::setFederateName( string name )
{
	this->federateName = name;
}

string PGConfiguration::getFederationName()
{
	return this->federationName;
}

void PGConfiguration::setFederationName( string name )
{
	this->federationName = name;
}

string PGConfiguration::getFedFile()
{
	return this->fedfile;
}

void PGConfiguration::setFedFile( string location )
{
	this->fedfile = location;
}

RTI::Boolean PGConfiguration::isTimestepped()
{
	return this->timestepped;
}

void PGConfiguration::setTimestepped( RTI::Boolean timestepped )
{
	this->timestepped = timestepped;
}

int PGConfiguration::getIterations()
{
	return this->iterations;
}

void PGConfiguration::setIterations( int iterations )
{
	this->iterations = iterations;
}

int PGConfiguration::getPrintInterval()
{
	return this->printInterval;
}

void PGConfiguration::setPrintInterval( int interval )
{
	this->printInterval = interval;
}

int PGConfiguration::getRecordInterval()
{
	return this->recordInterval;
}

void PGConfiguration::setRecordInterval( int interval )
{
	this->recordInterval = interval;
}

RTI::Boolean PGConfiguration::isLog()
{
	return this->log;
}

void PGConfiguration::setLog( RTI::Boolean log )
{
	this->log = log;
}

int PGConfiguration::getPayload()
{
	return this->payload;
}

void PGConfiguration::setPayload( int payload )
{
	this->payload = payload;
}

int PGConfiguration::getObjects()
{
	return this->objects;
}

void PGConfiguration::setObjects( int objects )
{
	this->objects = objects;
}

string PGConfiguration::getScenario()
{
	return this->scenario;
}

void PGConfiguration::setScenario( string scenario )
{
	this->scenario = scenario;
}

void PGConfiguration::parse( int count, char ** commandline )
{
	int i = 0;
	for( i = 0; i < count; i++ )
	{
		if( commandline[i][0] != '-' )
			continue;

		string argument = commandline[i];
		// find the position of the equals size
		size_t equalsSign = argument.find( "=" );
		if( equalsSign == string::npos )
			equalsSign = argument.length();
		
		// break the argument apart
		string key = argument.substr( 1, equalsSign-1 );
		string value = "";
		if( equalsSign < argument.length() )
			value = argument.substr( equalsSign+1 );
		
		// find out if the argument is anything we know about
		if( key == PGConfiguration::PROP_FEDERATE_NAME )
		{
			setFederateName( value );
		}
		else if( key == PGConfiguration::PROP_FEDERATION_NAME )
		{
			setFederationName( value );
		}
		else if( key == PGConfiguration::PROP_FED_FILE )
		{
			setFedFile( value );
		}
		else if( key == PGConfiguration::PROP_TIMESTEPPED )
		{
			setTimestepped( RTI::RTI_TRUE );
		}	
		else if( key == PGConfiguration::PROP_ITERATIONS )
		{
			stringstream ss( value );
			int iterations = 0;
			ss >> iterations;
			setIterations( iterations );
		}	
		else if( key == PGConfiguration::PROP_PRINT_INTERVAL )
		{
			stringstream ss( value );
			int interval = 0;
			ss >> interval;
			setPrintInterval( interval );
		}	
		else if( key == PGConfiguration::PROP_RECORD_INTERVAL )
		{
			stringstream ss( value );
			int interval = 0;
			ss >> interval;
			setRecordInterval( interval );
		}	
		else if( key == PGConfiguration::PROP_LOG )
		{
			setLog( RTI::RTI_TRUE );
		}	
		else if( key == PGConfiguration::PROP_PAYLOAD_SIZE )
		{
			setPayload( PGUtilities::stringToBytes(value) );
		}	
		else if( key == PGConfiguration::PROP_OBJECTS )
		{
			stringstream ss( value );
			int objects = 0;
			ss >> objects;
			setObjects( objects );
		}	
		else if( key == PGConfiguration::PROP_SCENARIO )
		{
			setScenario( value );
		}
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////// Static Methods ///////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////

PGAUGE_NAMESPACE_END
