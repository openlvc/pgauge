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

import java.util.HashMap;
import java.util.Map;

import hla.rti13.java1.EncodingHelpers;
import hla.rti13.java1.FederateInternalError;
import hla.rti13.java1.NullFederateAmbassador;

public class PGFederateAmbassador extends NullFederateAmbassador
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	public boolean constrained = false;
	public boolean regulating = false;
	public double federateTime = 0.0;
	
	protected Map<String,Boolean> synchronizationPoints;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public PGFederateAmbassador()
	{
		this.synchronizationPoints = new HashMap<String,Boolean>();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public boolean isAnnounced( String label )
	{
		return synchronizationPoints.containsKey( label );
	}
	
	public boolean isSynchronized( String label )
	{
		Boolean value = synchronizationPoints.get( label );
		if( value == null || value == false )
			return false;
		else
			return true;
	}
	
	public boolean hasTimeReached( double time )
	{
		if( federateTime >= time )
			return true;
		else
			return false;
	}

	///////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////// Federate Ambassador Callbacks //////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////
	
	//////////////////////////////////////
	// synchronization point management //
	//////////////////////////////////////
	public void synchronizationPointRegistrationFailed( String label )
	{
		// log this
	}

	public void synchronizationPointRegistrationSucceeded( String label )
	{
		//this.synchronizationPoints.put( label, false );
	}

	public void announceSynchronizationPoint( String label, String tag )
	{
		this.synchronizationPoints.put( label, false );
	}

	public void federationSynchronized( String label )
	{
		this.synchronizationPoints.put( label, true );
	}

	/////////////////////
	// time management //
	/////////////////////
	public void timeAdvanceGrant( byte[] theTime ) throws FederateInternalError
	{
		this.federateTime = EncodingHelpers.decodeDouble( theTime );
	}

	public void timeConstrainedEnabled( byte[] theFederateTime ) throws FederateInternalError
	{
		this.constrained = true;
		this.federateTime = EncodingHelpers.decodeDouble( theFederateTime );
	}

	public void timeRegulationEnabled( byte[] theFederateTime ) throws FederateInternalError
	{
		this.regulating = true;
		this.federateTime = EncodingHelpers.decodeDouble( theFederateTime );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
