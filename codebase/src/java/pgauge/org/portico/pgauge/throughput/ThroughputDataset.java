/*
 *   Copyright 2009 The Portico Project
 *
 *   This file is part of pgauge (a sub-project of Portico).
 *
 *   pgauge is free software; you can redistribute it and/or modify
 *   it under the terms of the Common Developer and Distribution License (CDDL) 
 *   as published by Sun Microsystems. For more information see the LICENSE file.
 *   
 *   Use of this software is strictly AT YOUR OWN RISK!!!
 *   If something bad happens you do not have permission to come crying to me.
 *   (that goes for your lawyer as well)
 *
 */
package org.portico.pgauge.throughput;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class collects data about a particular throughput scenario execution for a Sender.
 * This information can be used later to display information about how the RTI performed
 * throughout the test.
 */
public class ThroughputDataset
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private List<Integer> iterations;
	private List<Long> times;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ThroughputDataset()
	{
		this.iterations = new ArrayList<Integer>();
		this.times = new ArrayList<Long>();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public void start()
	{
		this.iterations.add( 0 );
		this.times.add( System.currentTimeMillis() );
	}

	/**
	 * Records that the sender has completed the given number of iterations at the current time.
	 * This test uses System.currentTimeMillis() to get the current time automatically.
	 */
	public void record( int iterationsCompleted )
	{
		this.iterations.add( iterationsCompleted );
		this.times.add( System.currentTimeMillis() );
	}
	
	public long getStartTime()
	{
		return times.get(0);
	}
	
	public long getEndTime()
	{
		return times.get( (times.size()-1) );
	}
	
	public long getDurationMillis()
	{
		return getEndTime()-getStartTime();
	}

	public long getDurationMillis( int startIndex, int endIndex )
	{
		return times.get(endIndex) - times.get(startIndex);
	}

	public double getDurationSeconds()
	{
		return ((double)getDurationMillis()) / (double)1000;
	}
	
	public double getDurationSeconds( int startIndex, int endIndex )
	{
		return ((double)getDurationMillis(startIndex,endIndex)) / (double)1000;
	}
	
	public int size()
	{
		return iterations.size();
	}
	
	public int getIterationCount( int index )
	{
		return iterations.get( index );
	}
	
	public long getTime( int index )
	{
		return times.get( index );
	}

	///////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////// Write to File //////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////
	public void writeToFile( String location ) throws Exception
	{
		// create the directory and file
		File file = new File( location );
		if( file.exists() == false )
		{
			file.getParentFile().mkdirs();
			file.createNewFile();
		}

		PrintWriter writer = new PrintWriter( file );
		for( int i = 0; i < iterations.size(); i++ )
			writer.println( ""+iterations.get(i)+","+times.get(i) );
		
		writer.flush();
		writer.close();
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
