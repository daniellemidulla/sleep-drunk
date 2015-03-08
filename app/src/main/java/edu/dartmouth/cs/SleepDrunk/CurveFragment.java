package edu.dartmouth.cs.SleepDrunk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.app.Fragment;
import android.app.ListFragment;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import edu.dartmouth.cs.SleepDrunk.ReactHighScoreDatabase.HighScoreEntry;



import android.app.Activity;
import android.view.WindowManager;

import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.xy.*;
import android.graphics.*;
import java.util.Arrays;
import com.androidplot.Plot;


public class CurveFragment extends Fragment {
	
	 private XYPlot plot;
	
	private ReactHighScoreDatabase db ;
	private IntentFilter mMessageIntentFilter;
	private BroadcastReceiver mMessageUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			long id = intent.getLongExtra("id",-1);
			String regid_rec = intent.getStringExtra("regid");
			if (id != -1 && regid_rec!=null && regid_rec.equals(MainActivity2.regid)) {
				try{
					Thread.sleep(100);
				}catch(Exception e){
					e.printStackTrace();
				}
				
				refresh();
			}
		}
	};
	


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment


        


		
		View view = inflater.inflate(R.layout.simple_xy_plot_example, container, false);
		
		
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
		// initialize our XYPlot reference:
        plot = (XYPlot) view.findViewById(R.id.mySimpleXYPlot);
		
		mMessageIntentFilter = new IntentFilter();
		mMessageIntentFilter.addAction("GCM_NOTIFY");
		db= ReactHighScoreDatabase.getDatabase(getActivity());
		
		 
   
		
		return view;
	}

	/**
	
	public void refresh(){
	     // Create a couple arrays of y-values to plot:
	       // Number[] series1Numbers = {1, 8, 5, 2, 7, 4};
	        //Number[] series2Numbers = {4, 6, 3, 8, 2, 10};
	        
	        List<HighScoreEntry> entries = ReactHighScoreDatabase.getDatabase(getActivity()).getAllEntries();
	        List<Integer> scores = new ArrayList<Integer>();
	        List<Integer> counts = new ArrayList<Integer>();
	        int i=1;
	        for(HighScoreEntry entry :entries){
	        	scores.add(entry.getScore());
	        	counts.add(i++);
	        }
	        
			
	        // Turn the above arrays into XYSeries':
	        XYSeries series1 = new SimpleXYSeries(
	                scores,          // SimpleXYSeries takes a List so turn our array into a List
	                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
	                "ReactTime");                             // Set the display title of the series
	        
	        // same as above
	      //  XYSeries series2 = new SimpleXYSeries(Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");
	 
	        // Create a formatter to use for drawing a series using LineAndPointRenderer
	        // and configure it from xml:
	        LineAndPointFormatter series1Format = new LineAndPointFormatter();
	        series1Format.setPointLabelFormatter(new PointLabelFormatter());
	        series1Format.configure(getActivity().getApplicationContext(),
	                R.xml.line_point_formatter_with_plf1);
	 
	        // add a new series' to the xyplot:
	        plot.addSeries(series1, series1Format);
	 
	        // same as above:
	        LineAndPointFormatter series2Format = new LineAndPointFormatter();
	        series2Format.setPointLabelFormatter(new PointLabelFormatter());
	        series2Format.configure(getActivity().getApplicationContext(),
	                R.xml.line_point_formatter_with_plf2);
	       // plot.addSeries(series2, series2Format);
	 
	        // reduce the number of range labels
	        //plot.setTicksPerRangeLabel(10);
	        plot.getGraphWidget().setDomainLabelOrientation(-45);
		


	}
	
	
	
	
	public void refresh(){
	     // Create a couple arrays of y-values to plot:
	       // Number[] series1Numbers = {1, 8, 5, 2, 7, 4};
	        //Number[] series2Numbers = {4, 6, 3, 8, 2, 10};
	        
	        List<HighScoreEntry> entries = ReactHighScoreDatabase.getDatabase(getActivity()).getAllEntries();
	        List<Integer> scores = new ArrayList<Integer>();
	        List<Integer> counts = new ArrayList<Integer>();
	        int i=1;
	        for(HighScoreEntry entry :entries){
	        	scores.add(entry.getScore());
	        	counts.add(i++);
	        }
	        
			
	        // initialize our XYPlot reference:
	       // mySimpleXYPlot = (XYPlot) view.findViewById(R.id.mySimpleXYPlot);
	        plot.getBackgroundPaint().setColor(Color.WHITE);
	        plot.setBorderStyle(XYPlot.BorderStyle.NONE, null, null);
	        plot.getGraphWidget().getBackgroundPaint().setColor(Color.WHITE);
	        plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);



	        // Domain
	        plot.getGraphWidget().setDomainLabelPaint(null);
	        plot.getGraphWidget().setDomainOriginLinePaint(null);
	        plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, counts.size());     
	        plot.setDomainValueFormat(new DecimalFormat("0"));


	        //Range
	        plot.getGraphWidget().setRangeOriginLinePaint(null);
	        plot.setRangeStep(XYStepMode.SUBDIVIDE, scores.size());
	        plot.setRangeValueFormat(new DecimalFormat("0"));


	        //Remove legend
	        plot.getLayoutManager().remove(plot.getLegendWidget());
	        plot.getLayoutManager().remove(plot.getDomainLabelWidget());
	        plot.getLayoutManager().remove(plot.getRangeLabelWidget());
	        plot.getLayoutManager().remove(plot.getTitleWidget());

	        // Turn the above arrays into XYSeries':
	        XYSeries series1 = new SimpleXYSeries(
	                counts,          
	                scores, 
	                "Series1");                             // Set the display title of the series

	        // Create a formatter to use for drawing a series using LineAndPointRenderer:
	        LineAndPointFormatter series1Format = new LineAndPointFormatter(
	                Color.rgb(0, 200, 0),                   // line color
	                Color.rgb(0, 100, 0),                   // point color
	                Color.CYAN, new PointLabelFormatter());                            // fill color 

	     // setup our line fill paint to be a slightly transparent gradient:
	        Paint lineFill = new Paint();
	        lineFill.setAlpha(200);
	        lineFill.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.GREEN, Shader.TileMode.MIRROR));

	        series1Format.setFillPaint(lineFill);

	        // add a new series' to the xyplot:
	        plot.addSeries(series1, series1Format);

	        // by default, AndroidPlot displays developer guides to aid in laying out your plot.
	        // To get rid of them call disableAllMarkup():
	      //  plot.disableAllMarkup();
	    

		


	}
	*/
	
	
	
	public void refresh(){
	     // Create a couple arrays of y-values to plot:
	       // Number[] series1Numbers = {1, 8, 5, 2, 7, 4};
	        //Number[] series2Numbers = {4, 6, 3, 8, 2, 10};
	        
	        List<HighScoreEntry> entries = ReactHighScoreDatabase.getDatabase(getActivity()).getAllEntries();
	        List<Integer> values = new ArrayList<Integer>();
	        List<Integer> days = new ArrayList<Integer>();
	        int i=1;
	        int highscore = 0;

	        for(HighScoreEntry entry :entries){
	        	int tmp = entry.getScore();
	        	values.add(tmp);
	        	if(tmp > highscore){
	        		highscore =tmp;
	        	}
	        	days.add(i++);
	        }

        plot.getSeriesSet().clear();
        plot.removeMarkers();

        plot.clear();
        plot.redraw();
	        
	        plot.setDomainLabel("");
	        plot.setRangeLabel("ReactTime");
	        plot.setTitle("");
	        
	        plot.getDomainLabelWidget().
	        position(0, XLayoutStyle.ABSOLUTE_FROM_CENTER, 0, YLayoutStyle.RELATIVE_TO_BOTTOM,  AnchorPosition.BOTTOM_MIDDLE);
			
	        plot.setBorderStyle(Plot.BorderStyle.NONE, null, null);
	        plot.setPlotMargins(0, 0, 0, 0);
	        plot.setPlotPadding(0, 0, 0, 0);
	        plot.setGridPadding(0, 10, 5, 0);

	        plot.setBackgroundColor(Color.WHITE);

	       /* plot.position(
	        		plot.getGraphWidget(),
	                0,
	                XLayoutStyle.ABSOLUTE_FROM_LEFT,
	                0,
	                YLayoutStyle.RELATIVE_TO_CENTER,
	                AnchorPosition.LEsFT_MIDDLE);*/
	        

	        plot.getGraphWidget().getBackgroundPaint().setColor(Color.TRANSPARENT);
	        plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.YELLOW);

	        plot.getGraphWidget().getDomainLabelPaint().setColor(Color.WHITE);
	        plot.getGraphWidget().getRangeLabelPaint().setColor(Color.WHITE);

	        plot.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.BLUE);
	        plot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
	        plot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.GREEN);

	        // Domain
	        plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, days.size());     
	        plot.setDomainValueFormat(new DecimalFormat("0"));
	        plot.setDomainStepValue(1);
	        plot.setDomainBoundaries(0, days.size()+1, BoundaryMode.FIXED);

	        //Range
	        plot.setRangeBoundaries(0, highscore+100, BoundaryMode.FIXED);
	        plot.setRangeStepValue(10);
	        //mySimpleXYPlot.setRangeStep(XYStepMode.SUBDIVIDE, values.length);
	        plot.setRangeValueFormat(new DecimalFormat("0"));

	        //Remove legend
	     //   plot.getLayoutManager().remove(plot.getLegendWidget());
	      //  plot.getLayoutManager().remove(plot.getDomainLabelWidget());
	        //plot.getLayoutManager().remove(plot.getRangeLabelWidget());
	       // plot.getLayoutManager().remove(plot.getTitleWidget());

	        // Turn the above arrays into XYSeries':
	        XYSeries series1 = new SimpleXYSeries(
	                days,          
	                values, 
	                "React Time");                             // Set the display title of the series

	        // Create a formatter to use for drawing a series using LineAndPointRenderer:
	        LineAndPointFormatter series1Format = new LineAndPointFormatter(
	                Color.rgb(0, 200, 0),                   // line color
	                Color.rgb(0, 100, 0),                   // point color
	                Color.RED, new PointLabelFormatter(Color.BLACK));                            // fill color 

	     // setup our line fill paint to be a slightly transparent gradient:
	        Paint lineFill = new Paint();
	        lineFill.setAlpha(200);
	        lineFill.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.GREEN, Shader.TileMode.MIRROR));

	        series1Format.setFillPaint(lineFill);

	        // add a new series' to the xyplot:
	        plot.addSeries(series1, series1Format);

	        // by default, AndroidPlot displays developer guides to aid in laying out your plot.
	        // To get rid of them call disableAllMarkup():
	     //   plot.disableAllMarkup();
	    

		


	}
	
	
	
	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(mMessageUpdateReceiver, mMessageIntentFilter);
		refresh();
	}
	

	@Override
	public void onPause() {

		getActivity().unregisterReceiver(mMessageUpdateReceiver);
		super.onPause();
	}
	
	


	

}
