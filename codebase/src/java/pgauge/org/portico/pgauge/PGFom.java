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

import hla.rti13.java1.AttributeHandleSet;
import hla.rti13.java1.AttributeHandleSetFactory;
import hla.rti13.java1.RTIambassador;
import hla.rti13.java1.SuppliedAttributes;
import hla.rti13.java1.SuppliedAttributesFactory;
import hla.rti13.java1.SuppliedParameters;
import hla.rti13.java1.SuppliedParametersFactory;

public class PGFom
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private int pgauge;
	private int pgaugePayload;
	private int ping;
	private int pingPayload;
	private int pingAck;
	private int pingAckPayload;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public PGFom( RTIambassador rtiamb ) throws Exception
	{
		this.pgauge         = rtiamb.getObjectClassHandle( "ObjectRoot.PGauge" );
		this.pgaugePayload  = rtiamb.getAttributeHandle( "payload", pgauge );
		this.ping           = rtiamb.getInteractionClassHandle( "InteractionRoot.Ping" );
		this.pingPayload    = rtiamb.getParameterHandle( "payload", ping );
		this.pingAck        = rtiamb.getInteractionClassHandle( "InteractionRoot.PingAck" );
		this.pingAckPayload = rtiamb.getParameterHandle( "payload", pingAck );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public int pgauge()
	{
		return pgauge;
	}
	
	public int pgaugePayload()
	{
		return pgaugePayload;
	}
	
	public int ping()
	{
		return ping;
	}
	
	public int pingPayload()
	{
		return pingPayload;
	}
	
	public int pingAck()
	{
		return pingAck;
	}

	public int pingAckPayload()
	{
		return pingAckPayload;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////// Collection Convenience Methods /////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	public AttributeHandleSet pgaugeHandleSet() throws Exception
	{
		AttributeHandleSet handleSet = AttributeHandleSetFactory.create( 1 );
		handleSet.add( pgaugePayload );
		return handleSet;
	}
	
	public SuppliedAttributes pgaugeSupplied( byte[] payload ) throws Exception
	{
		SuppliedAttributes attributes = SuppliedAttributesFactory.create( 1 );
		attributes.add( pgaugePayload, payload );
		return attributes;
	}
	
	public SuppliedParameters pingSupplied( byte[] payload ) throws Exception
	{
		SuppliedParameters parameters = SuppliedParametersFactory.create( 1 ); 
		parameters.add( pingPayload, payload );
		return parameters;
	}

	public SuppliedParameters pingAckSupplied( byte[] payload ) throws Exception
	{
		SuppliedParameters parameters = SuppliedParametersFactory.create( 1 ); 
		parameters.add( pingAckPayload, payload );
		return parameters;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
