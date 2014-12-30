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
#include "PGUtilities.h"

PGAUGE_NAMESPACE

//////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////// Constructors ////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////
PGUtilities::PGUtilities()
{
}

PGUtilities::~PGUtilities()
{
}

//////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////// Static Methods ///////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////
unsigned long PGUtilities::currentTimeMillis()
{
#ifdef WIN32
	LARGE_INTEGER now;
	LARGE_INTEGER freq;
	QueryPerformanceFrequency( &freq );
	QueryPerformanceCounter( &now );

	return (unsigned long)(now.QuadPart / (freq.QuadPart/1000));
#else
	struct timeval now;
	gettimeofday( &now, NULL );
	unsigned long millis = (now.tv_sec * 1000) + (now.tv_usec/1000);
	return millis;
#endif
}

unsigned long PGUtilities::currentTimeMicros()
{
#ifdef WIN32
	LARGE_INTEGER now;
	LARGE_INTEGER freq;
	QueryPerformanceFrequency( &freq );
	QueryPerformanceCounter( &now );

	return (unsigned long)(now.QuadPart / (freq.QuadPart/(1000*1000)));
#else
	struct timeval now;
	gettimeofday( &now, NULL );
	unsigned long micros = (now.tv_sec * (1000*1000)) + now.tv_usec;
	return micros;
#endif
}

string PGUtilities::bytesToString( int bytes )
{
	int kilobyte = 1024;
	int megabyte = kilobyte * kilobyte;
	
	if( bytes >= megabyte )
	{
		ostringstream oss;
		oss.precision( 2 );
		oss << PGUtilities::reduce(bytes,megabyte);
		oss << "MB";
		return oss.str();
	}
	else if( bytes >= kilobyte )
	{
		ostringstream oss;
		oss.precision( 2 );
		oss << PGUtilities::reduce(bytes,kilobyte);
		oss << "KB";
		return oss.str();
	}
	else
	{
		ostringstream oss;
		oss << bytes;
		oss << "b";
		return oss.str();
	}
}

double PGUtilities::reduce( int bytes, int unitSize )
{
	int wholeNumber = bytes / unitSize;
	double remainder = ( (bytes % unitSize) / (double)unitSize );
	remainder = (double)((int)(remainder*100))/100;
	return ((double)wholeNumber)+remainder;
}

int PGUtilities::stringToBytes( string bytes )
{
	// figure out if they've mentioned bytes, kilobytes or megabytes
	size_t found = string::npos;
	found = bytes.find( "MB" );
	if( found != string::npos )
	{
		// it's MB 
		string sizePart = bytes.substr( 0, bytes.length()-2 );
		stringstream ss( sizePart );
		double actualSize = 0.0;
		ss >> actualSize;
		return (int)(actualSize*1024*1024);
	}
	
	found = bytes.find( "KB" );
	if( found != string::npos )
	{
		// it's KB
		string sizePart = bytes.substr( 0, bytes.length()-2 );
		stringstream ss( sizePart );
		double actualSize = 0.0;
		ss >> actualSize;
		return (int)(actualSize*1024);
	}
	
	found = bytes.find( "b" );
	if( found != string::npos )
	{
		// it's bytes
		string sizePart = bytes.substr( 0, bytes.length()-1 );
		stringstream ss( sizePart );
		double actualSize = 0.0;
		ss >> actualSize;
		return (int)actualSize;
	}
	
	// default to assume that they just put a number, which results in using bytes
	stringstream ss( bytes );
	double actualSize = 0.0;
	ss >> actualSize;
	return (int)actualSize;
}

PGAUGE_NAMESPACE_END
