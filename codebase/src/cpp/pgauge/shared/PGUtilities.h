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
#ifndef PGUTILITIES_H_
#define PGUTILITIES_H_

#include "../common.h"

PGAUGE_NAMESPACE

class PGUtilities
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private:

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private:
		PGUtilities();
		virtual ~PGUtilities();

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public:
		static unsigned long currentTimeMillis();
		static unsigned long currentTimeMicros();
		static string bytesToString( int bytes );
		static int stringToBytes( string bytes );
	
	private:
		static double reduce( int bytes, int unitSize );

};

PGAUGE_NAMESPACE_END

#endif /* PGUTILITIES_H_ */
