package edu.dartmouth.cs.SleepDrunk;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.xy.XYPlot;

import java.util.ArrayList;
import java.util.List;

import edu.dartmouth.cs.SleepDrunk.ReactHighScoreDatabase.HighScoreEntry;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;


public class CurveFragment extends Fragment {

    private LineChartView chart;
    private LineChartData data;
    private int numberOfLines = 1;
    private int maxNumberOfLines = 1;
    private int numberOfPoints = 120;

    float[] randomNumbersTab = new float[numberOfPoints];

    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLines = true;
    private boolean hasPoints = true;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean hasLabels = true;
    private boolean isCubic = false;
    private boolean hasLabelForSelected = false;


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



        View rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);

        chart = (LineChartView) rootView.findViewById(R.id.chart);
        //chart.setOnValueTouchListener(new ValueTouchListener());

        // Generate some randome values.


        // Disable viewpirt recalculations, see toggleCubic() method for more info.
        //chart.setViewportCalculationEnabled(false);



        


		
		/*View view = inflater.inflate(R.layout.simple_xy_plot_example, container, false);
		
		
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
		// initialize our XYPlot reference:
        plot = (XYPlot) view.findViewById(R.id.mySimpleXYPlot);
        */

        mMessageIntentFilter = new IntentFilter();
        mMessageIntentFilter.addAction("GCM_NOTIFY");
        db= ReactHighScoreDatabase.getDatabase(getActivity());


        //refresh();

        return rootView;
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

      /*  for (int j = 0; j < numberOfPoints; ++j) {
            randomNumbersTab[j] = (float) Math.random() * 100f;
        }

*/

        List<HighScoreEntry> entries = ReactHighScoreDatabase.getDatabase(getActivity()).getAllEntries();
        List<Integer> rxtime = new ArrayList<Integer>();
        List<Integer> days = new ArrayList<Integer>();
        int i=1;
        int highscore = 0;

        for(HighScoreEntry entry :entries){
            int tmp = entry.getScore();
            rxtime.add(tmp);
            if(tmp > highscore){
                highscore =tmp;
            }
            days.add(i++);
        }

        List<Line> lines = new ArrayList<Line>();
        //for (int i = 0; i < numberOfLines; ++i) {

        List<PointValue> values = new ArrayList<PointValue>();
        for (int j = 0; j < rxtime.size(); ++j) {
            values.add(new PointValue(j+1, rxtime.get(j)));
        }

        Line line = new Line(values);
        line.setColor(ChartUtils.COLOR_GREEN);
        line.setShape(shape);
        line.setCubic(isCubic);
        line.setFilled(isFilled);
        line.setHasLabels(hasLabels);
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
        line.setHasLines(hasLines);
        line.setHasPoints(hasPoints);
        lines.add(line);
        //	}

//        int userBaseline = ReactView.getUserBaseline();
//        List<PointValue> baseline = new ArrayList<PointValue>();
//        values.add(new PointValue(0, userBaseline));
//        values.add(new PointValue(rxtime.size(), userBaseline));
//
//        Line uBaseLine = new Line(baseline);
//        uBaseLine.setColor(ChartUtils.COLOR_BLUE);
//        line.setShape(shape);
//        line.setCubic(isCubic);
//        line.setFilled(isFilled);
//        line.setHasLabels(hasLabels);
//        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
//        line.setHasLines(hasLines);
//        line.setHasPoints(hasPoints);
//        lines.add(uBaseLine);


        data = new LineChartData(lines);

        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("");
                axisY.setName("React Time");
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        data.setBaseValue(Float.NEGATIVE_INFINITY);



        chart.setLineChartData(data);

        chart.setZoomType(ZoomType.HORIZONTAL);










       /* final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = 0;
        v.top = 500;
        v.left = 0;
        v.right = numberOfPoints -1;
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);*/






/*
        plot.getSeriesSet().clear();
        plot.removeMarkers();

        plot.clear();
        plot.redraw();


*/












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
