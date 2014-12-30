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
package org.portico.pgauge;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import hla.rti13.java1.EncodingHelpers;
import hla.rti13.java1.RTIambassador;

public class PGUtilities
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	static
	{
		// load the logging configuration
		PropertyConfigurator.configure( "etc/log4j.properties" );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/**
	 * Get a logger with the given name. We use this method because it ensures that the logging
	 * configuration will have been appropriate loaded before the logger is fetched.
	 */
	public static Logger getLogger( String name )
	{
		return Logger.getLogger( name );
	}

	public static byte[] randomData( int size )
	{
		byte[] data = new byte[size];
		new Random().nextBytes( data );
		return data;
	}

	/**
	 * Use the given RTI and Federate ambassadors to synchronize on the identified sync point.
	 * This method will wait until the FederateAmbassador has the sync point announced to it. It
	 * will then achieve the point and wait until the federation synchronizes on it.
	 */
	public static void synchronize( String label,
	                                RTIambassador rtiamb,
	                                PGFederateAmbassador fedamb ) throws Exception
	{
		// wait for the announcement
		while( fedamb.isAnnounced(label) == false )
			rtiamb.tick();
		
		// signal that we're there
		rtiamb.synchronizationPointAchieved( label );
		
		// wait for the federation to come along
		while( fedamb.isSynchronized(label) == false )
			rtiamb.tick();
	}
	
	/**
	 * Joins a federation, it will try 10 times before giving up and throwing an exception
	 */
	public static void join( String federateName,
	                         String federationName,
	                         PGFederateAmbassador fedamb,
	                         RTIambassador rtiamb )
		throws Exception
	{
		for( int i = 0; i < 10; i++ )
		{
			try
			{
				rtiamb.joinFederationExecution( federateName, federationName, fedamb );
				return;
			}
			catch( Exception e )
			{
				if( i == 9 )
					throw e;
				
				Thread.sleep( 50 );
			}
		}
	}
	
	/**
	 * Takes a given int representing a size in bytes and converts it to a String representing the
	 * size. For example, 1024 would yield "1KB". Prints up to two decimal places. Uses "b" for
	 * bytes, "KB" for kilobytes and "MB" for megabytes.
	 */
	public static String bytesToString( int bytes )
	{
		int kilobyte = 1024;
		int megabyte = kilobyte * kilobyte;
		String result = null;
		if( bytes >= megabyte )
			result = String.format( "%.2f", reduce(bytes,megabyte) );
		else if( bytes >= kilobyte )
			result = String.format( "%.2f", reduce(bytes,kilobyte) );
		else
			result = ""+bytes;
		
		// String.format() and all that rounds up (which I don't want) and in 1.5 we can't
		// specify the rounding mode on Decimal format, so I'm left to this hack.
		if( result.endsWith("00") )
			result = result.replace(".00","");
		else if( result.endsWith("0") )
			result = result.substring(0,result.length()-1);

		if( bytes >= megabyte )
			return result+"MB";
		else if( bytes >= kilobyte )
			return result + "KB";
		else
			return result + "b";
	}

	private static double reduce( int bytes, int unitSize )
	{
		int main = bytes / unitSize;
		double remainder = ( (bytes % unitSize) / (double)unitSize );
		remainder = (double)((int)(remainder*100))/100;
		return ((double)main)+remainder;
	}

	/**
	 * Takes a string representing the size of a payload and converts it to the actual number of
	 * bytes that would be. For example, "1kb" returns 1024. The supported suffixes are "kb" for
	 * kilobytes, "mb" for megabytes, "b" for bytes. If no character is provided, the number is
	 * taken to be the byte value and is returned. 
	 */
	public static int stringToBytes( String string )
	{
		string = string.trim().toUpperCase();
		if( string.endsWith("MB") )
		{
			double mbs = Double.parseDouble( string.substring(0,string.length()-2) ); 
			return (int)(mbs*(1024*1024));
		}
		else if( string.endsWith("KB") || string.endsWith("K") )
		{
			double kbs = Double.parseDouble( string.substring(0,string.length()-2) );
			return (int)(kbs*1024);
		}
		else if( string.endsWith("B") )
		{
			return Integer.parseInt( string.substring(0,string.length()-1) );
		}
		else
		{
			return Integer.parseInt( string );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////// Property Fetching and Validation Methods ///////////////////////
	////////////////////////////////////////////////////////////////////////////////////////
	public static String getString( Properties properties, String key )
	{
		return (String)properties.get( key );
	}
	
	public static boolean getBoolean( Properties properties, String key )
	{
		return Boolean.parseBoolean( (String)properties.get(key) );
	}
	
	public static int getInt( Properties properties, String key )
	{
		return Integer.parseInt( (String)properties.get(key) );
	}
	
	public static List<String> getList( Properties properties, String key )
	{
		String listString = (String)properties.get( key );
		return getList( listString );
	}
	
	public static List<String> getList( String listString )
	{
		ArrayList<String> list = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer( listString, "," );
		while( tokenizer.hasMoreTokens() )
			list.add( tokenizer.nextToken().trim() );
		
		return list;
	}

	public static List<String> listOfStrings( int size, String prefix )
	{
		ArrayList<String> list = new ArrayList<String>( size );
		for( int i = 1; i <= size; i++ )
			list.add( prefix+i );
		
		return list;
	}

	////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////// Encoding Methods ///////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////
	public static byte[] time( double time ) throws Exception
	{
		return EncodingHelpers.encodeDouble( time );
	}
	
	public static double time( byte[] time ) throws Exception
	{
		return EncodingHelpers.decodeDouble( time );
	}
	
	
}
