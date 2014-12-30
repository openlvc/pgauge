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
#include "Logger.h"

PGAUGE_NAMESPACE

int Logger::globalLevel = LOG_UNSET;

//----------------------------------------------------------
//                      CONSTRUCTORS
//----------------------------------------------------------
Logger::Logger( char *name )
{
	this->name = new char[strlen(name)+1];
	strcpy( this->name, name );
	this->level = LOG_INFO;
}

Logger::~Logger()
{
	delete this->name;
}

//----------------------------------------------------------
//                    INSTANCE METHODS
//----------------------------------------------------------

void Logger::setName( char *name )
{
	if ( this->name )
		delete [] this->name;

	this->name = new char[strlen(name)+1];
	strcpy( this->name, name );
}

char* Logger::getName()
{
	return this->name;
}

void Logger::setLevel( int level )
{
	if( level >= LOG_OFF && level <= LOG_NOISY )
	{
		this->level = level;
	}
}

int Logger::getLevel()
{
	return this->level;
}

/////////////////////////////
// private logging methods //
/////////////////////////////
void Logger::log( const char *level, const char *message )
{
	// print the message
	//printf( "%s [%s] %s\n", level, this->name, message );
	cout << level << " [" << this->name << "] " << message << endl;
}

void Logger::log( const char *level, const char *format, va_list args )
{
	// turn the args into a single string
	// http://www.cplusplus.com/reference/clibrary/cstdio/vsprintf.html
	char buffer[MAX_MSG_LENGTH];
	vsprintf( buffer, format, args );

	// print the message
	//printf( "%s [%s] %s\n", level, this->name, buffer );
	cout << level << " [" << this->name << "] " << buffer << endl;
}

//
// This method will check to see if a message of the given level should
// be logged (given the current level of the logger) or not. If it should,
// true is returned, otherwise, false is returned.
//
// If the global level is set, the local level of the logger will not be
// consulted.
//
bool Logger::checkLevel( int messageLevel )
{
	if( Logger::globalLevel == LOG_UNSET )
	{
		if( messageLevel <= this->level )
			return true;
		else
			return false;
	}
	else
	{
		if( messageLevel <= Logger::globalLevel )
			return true;
		else
			return false;
	}
}

////////////////////////////
// public logging methods //
////////////////////////////
void Logger::fatal( const char *format, ... )
{
	// if the requested level is GREATER than the
	// logger level (the threshold), don't print it
	if( !checkLevel(LOG_FATAL) )
		return;

	// start the var-arg stuff
	va_list args;
	va_start( args, format );
	log( "FATAL", format, args );
	// do the varargs cleanup
	va_end( args );
}

void Logger::error( const char *format, ... )
{
	// if the requested level is GREATER than the
	// logger level (the threshold), don't print it
	if( !checkLevel(LOG_ERROR) )
		return;

	// start the var-arg stuff
	va_list args;
	va_start( args, format );
	// print the message
	log( "ERROR", format, args );
	// do the varargs cleanup
	va_end( args );
}

void Logger::warn ( const char *format, ... )
{
	// if the requested level is GREATER than the
	// logger level (the threshold), don't print it
	if( !checkLevel(LOG_WARN) )
		return;

	// start the var-arg stuff
	va_list args;
	va_start( args, format );
	// print the message
	log( "WARN ", format, args );
	// do the varargs cleanup
	va_end( args );
}

void Logger::info ( const char *format, ... )
{
	// if the requested level is GREATER than the
	// logger level (the threshold), don't print it
	if( !checkLevel(LOG_INFO) )
		return;

	// start the var-arg stuff
	va_list args;
	va_start( args, format );
	// print the message
	log( "INFO ", format, args );
	// do the varargs cleanup
	va_end( args );
}

void Logger::debug( const char *format, ... )
{
	// if the requested level is GREATER than the
	// logger level (the threshold), don't print it
	if( !checkLevel(LOG_DEBUG) )
		return;

	// start the var-arg stuff
	va_list args;
	va_start( args, format );
	// print the message
	log( "DEBUG", format, args );
	// do the varargs cleanup
	va_end( args );
}

void Logger::trace( const char *format, ... )
{
	// if the requested level is GREATER than the
	// logger level (the threshold), don't print it
	if( !checkLevel(LOG_TRACE) )
		return;

	// start the var-arg stuff
	va_list args;
	va_start( args, format );
	// print the message
	log( "TRACE", format, args );
	// do the varargs cleanup
	va_end( args );
}

void Logger::noisy( const char *format, ... )
{
	// if the requested level is GREATER than the
	// logger level (the threshold), don't print it
	if( !checkLevel(LOG_NOISY) )
		return;

	// start the var-arg stuff
	va_list args;
	va_start( args, format );
	// print the message
	log( "NOISY", format, args );
	// do the varargs cleanup
	va_end( args );
}

////////////////////////////////
// log level checking methods //
////////////////////////////////

bool Logger::isFatalEnabled()
{
	return checkLevel( LOG_FATAL );
}

bool Logger::isErrorEnabled()
{
	return checkLevel( LOG_ERROR );
}

bool Logger::isWarnEnabled()
{
	return checkLevel( LOG_WARN );
}

bool Logger::isInfoEnabled()
{
	return checkLevel( LOG_INFO );
}

bool Logger::isDebugEnabled()
{
	return checkLevel( LOG_DEBUG );
}

bool Logger::isTraceEnabled()
{
	return checkLevel( LOG_TRACE );
}

bool Logger::isNoisyEnabled()
{
	return checkLevel( LOG_NOISY );
}

//----------------------------------------------------------
//                     STATIC METHODS
//----------------------------------------------------------
void Logger::setGlobalLevel( int level )
{
	if( level == LOG_UNSET || (level >= LOG_OFF && level <= LOG_NOISY) )
	{
		Logger::globalLevel = level;
	}
}

/*
 * valid values for the string are "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL" and "OFF"
 */
void Logger::setGlobalLevel( char *level )
{
	if( strcmp(level,"NOISY") == 0 )
		Logger::setGlobalLevel( LOG_NOISY );
	else if( strcmp(level,"TRACE") == 0 )
		Logger::setGlobalLevel( LOG_TRACE );
	else if( strcmp(level,"DEBUG") == 0 )
		Logger::setGlobalLevel( LOG_DEBUG );
	else if( strcmp(level,"INFO") == 0 )
		Logger::setGlobalLevel( LOG_INFO );
	else if( strcmp(level,"WARN") == 0 )
		Logger::setGlobalLevel( LOG_WARN );
	else if( strcmp(level,"ERROR") == 0 )
		Logger::setGlobalLevel( LOG_ERROR );
	else if( strcmp(level,"FATAL") == 0 )
		Logger::setGlobalLevel( LOG_FATAL );
	else if( strcmp(level,"OFF") == 0 )
		Logger::setGlobalLevel( LOG_OFF );
}


int Logger::getGlobalLevel()
{
	return Logger::globalLevel;
}

PGAUGE_NAMESPACE_END
