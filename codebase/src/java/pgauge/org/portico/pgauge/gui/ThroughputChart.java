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

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.portico.pgauge.PGConfiguration;
import org.portico.pgauge.throughput.ThroughputDataset;

public class ThroughputChart extends JFrame
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final long serialVersionUID = 98121116105109L;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private ThroughputDataset rawdata;
	private JFreeChart chart;
	private ChartPanel panel;
	private XYSeries series;
	private XYSeriesCollection dataset;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public ThroughputChart( ThroughputDataset rawdata, PGConfiguration configuration )
	{
		this.rawdata = rawdata;
		this.series = new XYSeries( "Iterations" );
		this.dataset = new XYSeriesCollection( series );
		backfill();

		// create the chart
		String title = "Attribute Updates for "+configuration.getFederateName()+" ("+
		               configuration.getPayloadSizeAsString()+" packets)";
		this.chart = ChartFactory.createXYLineChart( title,
		                                             "Iterations",
		                                             "Time to Complete (ms)",
		                                             dataset,
		                                             PlotOrientation.VERTICAL,
		                                             false,   // legend
		                                             true,    // tooltips
		                                             false ); // urls
		
		this.panel = new ChartPanel( this.chart );
		getContentPane().add( this.panel );
		this.pack();
		this.setVisible( true );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public void updateChart()
	{
		updateChart( rawdata.size() );
	}
	
	private void updateChart( int startIndex )
	{
		if( startIndex <= 1 )
			return; // first one doesn't count, it's the initial data
			
		int iterationCount = rawdata.getIterationCount( startIndex-1 );
		long duration = rawdata.getDurationMillis( startIndex-2, startIndex-1 );

		this.series.add( iterationCount, duration );
		//this.dataset.addValue( duration, "Iterations", ""+iterationCount );
	}
	
	private void backfill()
	{
		for( int i = 0; i < rawdata.size(); i++ )
			updateChart( i );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
