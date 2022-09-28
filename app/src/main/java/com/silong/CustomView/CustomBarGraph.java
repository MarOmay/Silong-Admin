package com.silong.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomBarGraph extends BarChart {
    public CustomBarGraph(Context context) {
        super(context);
    }

    public CustomBarGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomBarGraph setEntries(String[] labels, BarDataSet... dataSet){
        List<IBarDataSet> barDataSets = new ArrayList<>();

        for (BarDataSet bds : dataSet){
            barDataSets.add(bds);
        }

        BarData barData = new BarData(barDataSets);
        super.setData(barData);
        super.getDescription().setEnabled(false);
        XAxis xAxis2 = super.getXAxis();
        xAxis2.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis2.setCenterAxisLabels(true);
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.setGranularity(1);
        xAxis2.setGranularityEnabled(true);
        super.setDragEnabled(true);
        super.setVisibleXRangeMaximum(3);
        float ownerBarSpace = 0.1f;
        float ownerGroupSpace = 0.5f;
        barData.setBarWidth(0.15f);
        super.getXAxis().setAxisMinimum(0);
        super.animate();
        super.groupBars(0, ownerGroupSpace, ownerBarSpace);
        super.invalidate();
        return this;
    }

    public void refresh(){
        super.setVisibility(View.GONE);
        super.setVisibility(View.VISIBLE);
    }

}
