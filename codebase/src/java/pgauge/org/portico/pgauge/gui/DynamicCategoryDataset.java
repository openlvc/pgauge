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
package org.portico.pgauge.gui;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.SlidingCategoryDataset;

public class DynamicCategoryDataset extends SlidingCategoryDataset
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final long serialVersionUID = 98121116105109L;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private DefaultCategoryDataset underlying;
	private int firstColumn;
	private int maxColumns;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public DynamicCategoryDataset( int maxColumns )
	{
		super( new DefaultCategoryDataset(), 0, maxColumns );
		this.underlying = (DefaultCategoryDataset)super.getUnderlyingDataset();
		this.firstColumn = 0;
		this.maxColumns = maxColumns;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@SuppressWarnings("rawtypes")
	public void addValue( double value, Comparable rowKey, Comparable columnKey )
	{
		underlying.addValue( value, rowKey, columnKey );
		
		// update the sliding window
		if( underlying.getColumnCount() > maxColumns )
			super.setFirstCategoryIndex( ++firstColumn );
		
		super.fireDatasetChanged();
	}
	
	@SuppressWarnings("rawtypes")
	public void addValue( Number value, Comparable rowKey, Comparable columnKey )
	{
		underlying.addValue( value, rowKey, columnKey );

		// update the sliding window
		if( underlying.getColumnCount() > maxColumns )
			super.setFirstCategoryIndex( ++firstColumn );
		
		super.fireDatasetChanged();
	}

	public void setRangeToAll()
	{
		this.firstColumn = 0;
		this.maxColumns = underlying.getColumnCount();
		super.setFirstCategoryIndex( firstColumn );
		super.setMaximumCategoryCount( maxColumns );
		super.fireDatasetChanged();
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
