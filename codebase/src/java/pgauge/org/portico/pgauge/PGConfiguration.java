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

/**
 * This class contains configuration data that can be used by all PGauge federates. Note that
 * different federates use different parts of this configuration data, so not all options apply
 * in all situations.
 */
public class PGConfiguration implements Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	public static final String PROP_FEDERATE_NAME = "federateName";
	public static final String PROP_FEDERATION_NAME = "federationName";
	public static final String PROP_FED_FILE = "fedFile";
	
	/** property for whether federates should timestep after each iteration (setting this will
	    also enabled time constrained and regulating for the federate */
	public static final String PROP_TIMESTEPPED = "timestepped";
	
	/** proprety for number of iterations to complete for the test */
	public static final String PROP_ITERATIONS = "iterations";
	
	/** print about progress each x iterations */
	public static final String PROP_PRINT_INTERVAL = "printInterval";
	
	/** record how long it takes to complete each x iterations */
	public static final String PROP_RECORD_INTERVAL = "recordInterval";
	
	/** should the federate log its activities to file? defaults to no */
	public static final String PROP_LOG = "log";
	
	/** should the live GUI be displayed */
	public static final String PROP_LIVE_GUI = "livegui";
	
	/** should a chart be displayed at the end? if the livegui is enabled, this is ignored */
	public static final String PROP_CHART = "chart";
	
	/** property for size of attribute updates sent by sender (in bytes) */
	public static final String PROP_PAYLOAD_SIZE = "payload";
	
	/** property for number of objects the sender will register, or for the number of
	    objects the listener expects to discover */
	public static final String PROP_OBJECTS = "objects";

	/** property identifying the scenario type, "throughput" or "latency" */
	public static final String PROP_SCENARIO_TYPE = "scenario";

	/** property of comma separated federate names to load as senders in a scenario (this is
	    only valid for the throughput automatic scenario executor, other federates ignore it) */
	public static final String PROP_SCENARIO_SENDERS = "senders";

	/** property of comma separated federate names to load as listeners in a scenario (this is
	    only valid for the throughput automatic scenario executor, other federates ignore it) */
	public static final String PROP_SCENARIO_LISTENERS = "listeners";
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private String federateName;
	private String federationName;
	private String fedfile;
	private boolean timestepped;
	private int iterations;
	private int printInterval;
	private int recordInterval;
	private boolean log;
	private int payload;
	private int objects;
	
	// these properties are only valid for the automatic scenario executor
	private String scenario;
	private List<String> senders;
	private List<String> listeners;
	
	private boolean livegui;
	private boolean chart;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private PGConfiguration()
	{
		this.federateName = "unknown";
		this.federationName = "pgauge";
		this.fedfile = "etc/pgauge.fed";
		this.timestepped = false;
		this.iterations = 10000;
		this.printInterval = 1000;
		this.recordInterval = 1000;
		this.log = false;
		this.payload = 4096;
		this.objects = 1;
		
		this.scenario = "unknown";
		this.senders = new ArrayList<String>();
		this.senders.add( "sender" ); // default, cleared if user supplies data
		this.listeners = new ArrayList<String>();
		this.listeners.add( "listener" ); // default, cleared if user supplies data
		
		this.livegui = false;
		this.chart = false;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
	
	public String getFederateName()
	{
		return federateName;
	}

	public void setFederateName( String federateName )
	{
		this.federateName = federateName;
	}

	public String getFederationName()
	{
		return federationName;
	}

	public void setFederationName( String federationName )
	{
		this.federationName = federationName;
	}

	public String getFedFile()
	{
		return fedfile;
	}

	public void setFedFile( String fedfile )
	{
		this.fedfile = fedfile;
	}

	public boolean isTimestepped()
	{
		return timestepped;
	}

	public void setTimestepped( boolean timestepped )
	{
		this.timestepped = timestepped;
	}

	public int getIterations()
	{
		return iterations;
	}

	public void setIterations( int iterations )
	{
		this.iterations = iterations;
	}

	public int getPrintInterval()
	{
		return printInterval;
	}

	public void setPrintInterval( int printInterval )
	{
		this.printInterval = printInterval;
	}
	
	public int getRecordInterval()
	{
		return recordInterval;
	}
	
	public void setRecordInterval( int recordInterval )
	{
		this.recordInterval = recordInterval;
	}

	public int getPayloadSize()
	{
		return payload;
	}
	
	public void setLog( boolean log )
	{
		this.log = log;
	}
	
	public boolean isLog()
	{
		return this.log;
	}

	public void setPayloadSize( int payloadSize )
	{
		this.payload = payloadSize;
	}
	
	public String getPayloadSizeAsString()
	{
		return PGUtilities.bytesToString( payload );
	}

	public int getObjects()
	{
		return this.objects;
	}
	
	public void setObjects( int expectedCount )
	{
		this.objects = expectedCount;
	}
	
	public boolean isLiveGUI()
	{
		return this.livegui;
	}
	
	public void setLiveGUI( boolean livegui )
	{
		this.livegui = livegui;
	}
	
	public boolean isChart()
	{
		return this.chart;
	}
	
	public void setChart( boolean chart )
	{
		this.chart = chart;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////// Scenario Properties /////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	public String getScenario()
	{
		return this.scenario;
	}
	
	public void setScenario( String type )
	{
		this.scenario = type;
	}
	
	public boolean isThroughputScenario()
	{
		return this.scenario.equals( "throughput" );
	}
	
	public boolean isLatencyScenario()
	{
		return this.scenario.equals( "latency" );
	}
	
	public boolean isObjectCreationScenario()
	{
		return this.scenario.equalsIgnoreCase( "objectCreation" );
	}
	
	public List<String> getSenders()
	{
		return this.senders;
	}
	
	public void setSenders( List<String> senders )
	{
		this.senders = senders;
	}
	
	public List<String> getListeners()
	{
		return this.listeners;
	}
	
	public void setListeners( List<String> listeners )
	{
		this.listeners = listeners;
	}

	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////// Configuration Parsing Methods ////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Looks at the given set of properties and loads any found values into the returned
	 * {@link PGConfiguration}. The keys for the properties are statics of this class. 
	 */
	public PGConfiguration parse( Properties properties )
	{
		if( properties.containsKey(PROP_FEDERATE_NAME) )
			setFederateName( PGUtilities.getString(properties,PROP_FEDERATE_NAME) );
		if( properties.containsKey(PROP_FEDERATION_NAME) )
			setFederationName( PGUtilities.getString(properties,PROP_FEDERATION_NAME) );
		if( properties.containsKey(PROP_FED_FILE) )
			setFedFile( PGUtilities.getString(properties,PROP_FED_FILE) );
		if( properties.containsKey(PROP_TIMESTEPPED) )
			setTimestepped( PGUtilities.getBoolean(properties,PROP_TIMESTEPPED) );
		if( properties.containsKey(PROP_ITERATIONS) )
			setIterations( PGUtilities.getInt(properties,PROP_ITERATIONS) );
		if( properties.containsKey(PROP_PRINT_INTERVAL) )
			setPrintInterval( PGUtilities.getInt(properties,PROP_PRINT_INTERVAL) );
		if( properties.containsKey(PROP_RECORD_INTERVAL) )
			setRecordInterval( PGUtilities.getInt(properties,PROP_RECORD_INTERVAL) );
		if( properties.containsKey(PROP_LOG) )
			setLog( true );
		if( properties.containsKey(PROP_PAYLOAD_SIZE) )
			setPayloadSize( PGUtilities.stringToBytes((String)properties.get(PROP_PAYLOAD_SIZE)) );
		if( properties.containsKey(PROP_OBJECTS) )
			setObjects( PGUtilities.getInt(properties,PROP_OBJECTS) );
		
		// scenario properties
		if( properties.containsKey(PROP_SCENARIO_TYPE) )
			setScenario( PGUtilities.getString(properties,PROP_SCENARIO_TYPE) );
		
		if( properties.containsKey(PROP_SCENARIO_SENDERS) )
		{
			int size = PGUtilities.getInt( properties, PROP_SCENARIO_SENDERS );
			setSenders( PGUtilities.listOfStrings(size,"sender") );
		}

		if( properties.containsKey(PROP_SCENARIO_LISTENERS) )
		{
			int size = PGUtilities.getInt( properties, PROP_SCENARIO_LISTENERS );
			setListeners( PGUtilities.listOfStrings(size,"listener") );
		}

		if( properties.containsKey(PROP_LIVE_GUI) )
			setLiveGUI( true );
		if( properties.containsKey(PROP_CHART) )
			setChart( true );

		return this;
	}

	/**
	 * Looks at the given command line and generates a {@link PGConfiguration} based off the
	 * provided information. For a value to be picked up, it must appear in the command line
	 * in the form "-propertyName=value", where the property name is one of the static variables
	 * provided in this class. 
	 */
	public PGConfiguration parse( String[] commandline ) throws Exception
	{
		for( String argument : commandline )
		{
			if( argument.startsWith("-") == false )
				continue;
			
			// break it down on the = sign (ignore the "-")
			String property = "";
			String value = "";
			int equalsSign = argument.indexOf( "=" );
			if( equalsSign > 0 )
			{
				property = argument.substring( 1, equalsSign );
				value = argument.substring( equalsSign+1 );
			}
			else
			{
				property = argument.substring( 1, argument.length() );
			}
			
			if( property.equalsIgnoreCase(PROP_FEDERATE_NAME) )
				setFederateName( value );
			else if( property.equalsIgnoreCase(PROP_FEDERATION_NAME) )
				setFederationName( value );
			else if( property.equalsIgnoreCase(PROP_FED_FILE) )
				setFedFile( value );
			else if( property.equalsIgnoreCase(PROP_TIMESTEPPED) )
				setTimestepped( Boolean.parseBoolean(value) );
			else if( property.equalsIgnoreCase(PROP_ITERATIONS) )
				setIterations( Integer.parseInt(value) );
			else if( property.equalsIgnoreCase(PROP_PRINT_INTERVAL) )
				setPrintInterval( Integer.parseInt(value) );
			else if( property.equalsIgnoreCase(PROP_RECORD_INTERVAL) )
				setRecordInterval( Integer.parseInt(value) );
			else if( property.equalsIgnoreCase(PROP_LOG) )
				setLog( true );
			else if( property.equalsIgnoreCase(PROP_PAYLOAD_SIZE) )
				setPayloadSize( PGUtilities.stringToBytes(value) );
			else if( property.equalsIgnoreCase(PROP_OBJECTS) )
				setObjects( Integer.parseInt(value) );
			// scenario properties
			else if( property.equalsIgnoreCase(PROP_SCENARIO_TYPE) )
				setScenario( value );
			else if( property.equalsIgnoreCase(PROP_SCENARIO_SENDERS) )
				setSenders( PGUtilities.listOfStrings(Integer.parseInt(value),"sender") );
			else if( property.equalsIgnoreCase(PROP_SCENARIO_LISTENERS) )
				setListeners( PGUtilities.listOfStrings(Integer.parseInt(value),"listener") );
			// gui options
			else if( property.equalsIgnoreCase(PROP_LIVE_GUI) )
				setLiveGUI( true );
			else if( property.equalsIgnoreCase(PROP_CHART) )
				setChart( true );
			else
				throw new Exception( "Unknown argument: " + argument );
		}
		
		return this;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static PGConfiguration defaultConfiguration()
	{
		return new PGConfiguration();
	}
	
	public static String usage()
	{
		String newline = System.getProperty( "line.separator");
		StringBuilder builder = new StringBuilder( "Usage: pgauge [options]" );
		builder.append( newline );
		builder.append( "Usage: All options provide in following form: -optionName=optionValue" );
		builder.append( newline );
		builder.append( "Usage: -scenario=scenarioType         (default:unset)"+newline );
		builder.append( "           options: throughput,latency,objectCreation"+newline );
		builder.append( "       -federateName=String           (default:unknown)"+newline );
		builder.append( "       -federationName=String         (default:pgauge)"+newline );
		builder.append( "       -fedFile=String                (default:etc/pgauge.fed)"+newline );		
		builder.append( "       -timestepped=boolean           (default:false)"+newline );
		builder.append( "       -iterations=int                (default:10000)"+newline );
		builder.append( "       -printInterval=int             (default:1000)"+newline );
		builder.append( "       -recordInterval=int            (default:1000)"+newline );
		builder.append( "       -log (if present, log to file) (default:false)"+newline );
		builder.append( "       -payload=String                (default:4KB)"+newline );
		builder.append( "       -livegui                       (default:false)"+newline );
		builder.append( "       -chart                         (default:false)"+newline );
		builder.append( newline );
		builder.append( "Throughput Scenario Specific Options:"+newline );
		builder.append( "       -objects=int                   (default:1)"+newline );
		builder.append( "           For Sender: number of objects it will register and update"+newline );
		builder.append( "           For Listener: total number of objects it expects to discover"+newline );
		builder.append( "       -senders=int                   (default:1)"+newline );
		builder.append( "       -listeners=int                 (default:1)"+newline );
		builder.append( newline );
		return builder.toString();
	}


}
