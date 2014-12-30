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
#ifndef LATENCYDATASET_H_
#define LATENCYDATASET_H_

#include "../common.h"

PGAUGE_NAMESPACE

class LatencyDataset
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private:
		vector<unsigned long> results;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public:
		LatencyDataset();
		virtual ~LatencyDataset();

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public:
		void record( unsigned long latencyInMicros );
		int size();
		unsigned long getAverage();
		double getStandardDeviation();
		unsigned long getAverageWithinTwoStandardDeviations();
		unsigned long getEightyAverage();
		unsigned long getLow();
		unsigned long getHigh();

		void writeToFile( const char *filename );

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

};

PGAUGE_NAMESPACE_END

#endif /* LATENCYDATASET_H_ */
