package com.silong.CustomView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class CustomPieChart extends PieChart {

    PieDataSet pieDataSet;

    public CustomPieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomPieChart(Context context) {
        super(context);
    }

    public CustomPieChart setEntries(ArrayList<PieEntry> arrayList){
        pieDataSet = new PieDataSet(arrayList, "");

        PieData pieData = new PieData(pieDataSet);

        super.setData(pieData);

        setTotal(getTotal(arrayList));

        return this;
    }

    private CustomPieChart setTotal(int total){
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(16f);

        super.getDescription().setEnabled(false);
        super.getLegend().setEnabled(false);
        super.setCenterText(String.valueOf(total));
        super.setCenterTextSize(40);
        super.setCenterTextTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        super.animate();

        return this;
    }

    private int getTotal(ArrayList<PieEntry> arrayList){
        int total = 0;

        for (PieEntry entry : arrayList){
            total += entry.getValue();
        }

        return total;
    }

    public void refresh(){
        super.setVisibility(View.GONE);
        super.setVisibility(View.VISIBLE);
    }

    private ValueFormatter valueFormatter = new ValueFormatter() {
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            return super.getAxisLabel(value, axis);
        }

        @Override
        public String getPieLabel(float value, PieEntry pieEntry) {
            return super.getPieLabel(value, pieEntry);
        }
    };
}
