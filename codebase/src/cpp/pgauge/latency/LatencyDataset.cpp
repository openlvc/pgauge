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
#include "LatencyDataset.h"

PGAUGE_NAMESPACE

//////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////// Constructors ////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////
LatencyDataset::LatencyDataset()
{
	this->results = vector<unsigned long>();
}

LatencyDataset::~LatencyDataset()
{
}

//////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////// Instance Methods //////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////
void LatencyDataset::record( unsigned long latencyInMicros )
{
	results.push_back( latencyInMicros );
}

int LatencyDataset::size()
{
	return results.size();
}

unsigned long LatencyDataset::getAverage()
{
	unsigned long total = 0;
	vector<unsigned long>::iterator iterator;
	for( iterator=results.begin(); iterator < results.end(); iterator++ )
		total += *iterator;
	
	return (unsigned long)(total/results.size());
}

double LatencyDataset::getStandardDeviation()
{
	// big ups to Mandeep Sandhu for figuring the maths out for me
	// previously, when there was sufficient variance in the set, or you had a giant
	// dataset (or both), the calculation I was using would fail. I calculated the
	// sum of all the squares first in a temp var, but this number got so big that
	// the temp var rolled over past it's max possible value (in an unsigned long!).
	// This approach calculates things in a rolling average sort of way, without the
	// need to sum everything up first. Much, much nicer.
	double rollingMean = 0.0;
	double rollingVariance = 0.0;
	double rollingStddev = 0.0;
	int count = 1;

	vector<unsigned long>::iterator iterator;
	for( iterator=results.begin(); iterator < results.end(); iterator++ )
	{
		unsigned long current = *iterator;
		count++;
		double temp = rollingMean;
		rollingMean += (current-rollingMean) / count;

		temp = (current-temp);
		rollingVariance += (count-1)*temp*temp / count;
		rollingStddev = sqrt(rollingVariance/count-1);
	}
	
	return rollingStddev;
}

unsigned long LatencyDataset::getAverageWithinTwoStandardDeviations()
{
	double stddev = getStandardDeviation();
	unsigned long average = getAverage();
	double min = average - (stddev*2);
	double max = average + (stddev*2);
	
	unsigned long sum = 0;
	unsigned long sumCount = 0;
	vector<unsigned long>::iterator iterator;
	for( iterator=results.begin(); iterator < results.end(); iterator++ )
	{
		if( *iterator < min || *iterator > max )
			continue;
		
		sum += *iterator;
		sumCount++;
	}
	
	return (unsigned long)sum/sumCount;
}

unsigned long LatencyDataset::getEightyAverage()
{
	// sort the dataset
	vector<unsigned long> copy( results );
	sort( copy.begin(), copy.end() );
	
	// ignore the first and last 10%
	unsigned long start = copy.size()/10;
	unsigned long end = copy.size() - start;
	unsigned long total = 0;
	unsigned long counted = 0;
	unsigned long i = 0;
	for( i = start; i < end; i++ )
	{
		total += copy[i];
		++counted;
	}
	
	return (unsigned long)total / counted;
}

unsigned long LatencyDataset::getLow()
{
	unsigned long low = ULONG_MAX;
	vector<unsigned long>::iterator iterator;
	for( iterator=results.begin(); iterator < results.end(); iterator++ )
	{
		if( *iterator < low )
			low = *iterator;
	}
	
	return low;
}

unsigned long LatencyDataset::getHigh()
{
	unsigned long high = 0;
	vector<unsigned long>::iterator iterator;
	for( iterator=results.begin(); iterator < results.end(); iterator++ )
	{
		if( *iterator > high )
			high = *iterator;
	}
	
	return high;
}

void LatencyDataset::writeToFile( const char *filename )
{
	ofstream outfile;
	outfile.open( filename );
	vector<unsigned long>::iterator iterator;
	for( iterator = results.begin(); iterator < results.end(); iterator++ )
	{
		outfile << *iterator << endl;
	}

	outfile.close();
}

//////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////// Static Methods ///////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////

PGAUGE_NAMESPACE_END

