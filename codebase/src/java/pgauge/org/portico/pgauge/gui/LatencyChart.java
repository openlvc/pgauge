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
import org.portico.pgauge.latency.LatencyDataset;

public class LatencyChart extends JFrame
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final long serialVersionUID = 98121116105109L;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private LatencyDataset rawdata;
	private XYSeries series;
	private XYSeriesCollection dataset;
	private JFreeChart chart;
	private ChartPanel panel;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public LatencyChart( LatencyDataset rawdata, PGConfiguration configuration )
	{
		this.rawdata = rawdata;
		this.series = new XYSeries( "Iterations" );
		this.dataset = new XYSeriesCollection( series );
		backfill(); // will initialize dataset
		
		// create the chart
		String title = "Round-Trip Latency ("+configuration.getPayloadSizeAsString()+" packets)";
		this.chart = ChartFactory.createXYLineChart( title,
		                                             "Iterations",
		                                             "Latency (microseconds)",
		                                             dataset,
		                                             PlotOrientation.VERTICAL,
		                                             false,   // legend
		                                             true,    // tooltips
		                                             false ); // urls

		// set max at 3 standard deviations from mean
		long max = rawdata.getAverage() + (long)(3*rawdata.getStandardDeviation());
		chart.getXYPlot().getRangeAxis().setRange( 0, max );
		
		this.panel = new ChartPanel( this.chart );
		getContentPane().add( this.panel );
		this.pack();
		this.setVisible( true );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public void updateChart( int iteration )
	{
		// conver the nano time into microseconds
		series.add( iteration, rawdata.get(iteration) );
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
