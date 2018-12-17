package com.byonchat.android;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;

public class DetailGroupVoting extends AppCompatActivity implements OnChartValueSelectedListener {

    protected BarChart mChart;

    private Typeface tf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.detail_group_voting_activity);
        mChart = (BarChart) findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawBarShadow(true);
        mChart.setDrawValueAboveBar(false);
        mChart.setDescription("");
        mChart.setMaxVisibleValueCount(60);
        mChart.setPinchZoom(false);
        mChart.getAxisRight().setDrawLabels(false);

        tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        XAxis xl = mChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setTypeface(tf);
        xl.setTextColor(Color.BLUE);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        xl.setGridLineWidth(0.3f);

        YAxis yl = mChart.getAxisLeft();
        yl.setTypeface(tf);
        yl.setTextColor(Color.BLUE);
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setGridLineWidth(0.3f);
        yl.setAxisMinValue(0f);

        YAxis yr = mChart.getAxisRight();
        yr.setTypeface(tf);
        yr.setDrawAxisLine(false);
        yr.setTextColor(Color.BLUE);
        yr.setDrawGridLines(true);
        yr.setGridLineWidth(0.3f);
        yr.setAxisMinValue(0f);

        BarData data = new BarData(getXAxisValues(), getDataSet());
        mChart.setData(data);

        mChart.animateY(2500);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setFormSize(8f);
        l.setXEntrySpace(4f);
        mChart.setDrawValueAboveBar(true);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(false);
        mChart.setDrawBarShadow(false);
        mChart.animateXY(2000, 2000);
        mChart.invalidate();


    }

    public class MyValueFormatter implements ValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return Math.round(value) + "";
        }
    }

    private ArrayList<IBarDataSet> getDataSet() {
        ArrayList<IBarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        BarEntry v1e1 = new BarEntry(60.000f, 0);
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(20.000f, 1);
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(15.000f, 2);
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(5.000f, 3);
        valueSet1.add(v1e4);
        BarEntry v1e5 = new BarEntry(10.000f, 4);
        valueSet1.add(v1e5);

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Activities");
        barDataSet1.setDrawValues(false);
        barDataSet1.setValueTextColor(Color.BLUE);
        barDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet1.setValueFormatter(new MyValueFormatter());

        dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(barDataSet1);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("Ancol");
        xAxis.add("Palembang");
        xAxis.add("Sawarna");
        xAxis.add("Jepang");
        xAxis.add("Rumah");
        return xAxis;
    }


    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        if (e == null)
            return;
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("Ancol");
        xAxis.add("Palembang");
        xAxis.add("Sawarna");
        xAxis.add("Jepang");
        xAxis.add("Rumah");

        Toast.makeText(getApplicationContext(), xAxis.get(e.getXIndex()) + " = " + (int) (Math.round(e.getVal())), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {

    }
}
