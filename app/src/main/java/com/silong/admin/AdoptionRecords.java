package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class AdoptionRecords extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoption_records);
        getSupportActionBar().hide();

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        //Requests Pie Chart
        PieChart requestsPieChart = (PieChart) findViewById(R.id.requestsPieChart);
        ArrayList<PieEntry> requests = new ArrayList<>();
        requests.add(new PieEntry(20, "Successful"));
        requests.add(new PieEntry(5, "Denied"));
        PieDataSet pieDataSet = new PieDataSet(requests, "");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(16f);
        PieData pieData = new PieData(pieDataSet);
        requestsPieChart.setData(pieData);
        requestsPieChart.getDescription().setEnabled(false);
        requestsPieChart.setCenterText("Requests");
        requestsPieChart.animate();

        //Pet Demographics Bar Chart
        String[] xAxisLables = new String[]{"Puppy/Kitten", "Young", "Adult"};
        BarChart petDemogBarChart = (BarChart) findViewById(R.id.petDemogBarChart);
        ArrayList<BarEntry> entriesOne = new ArrayList<>();
        //for puppy/kitten
        entriesOne.add(new BarEntry(1,10));
        //for young
        entriesOne.add(new BarEntry(2,20));
        //for adult
        entriesOne.add(new BarEntry(3,30));
        ArrayList<BarEntry> entriesTwo = new ArrayList<>();
        //for puppy/kitten
        entriesTwo.add(new BarEntry(3,30));
        //for young
        entriesTwo.add(new BarEntry(4,40));
        //for adult
        entriesTwo.add(new BarEntry(5, 50));
        BarDataSet barDataSetOne = new BarDataSet(entriesOne, "Cat");
        barDataSetOne.setColor(getResources().getColor(R.color.pink));
        BarDataSet barDataSetTwo = new BarDataSet(entriesTwo, "Dog");
        barDataSetTwo.setColor(getResources().getColor(R.color.orange));
        BarData data = new BarData(barDataSetOne, barDataSetTwo);
        petDemogBarChart.setData(data);
        petDemogBarChart.getDescription().setEnabled(false);
        XAxis xAxis = petDemogBarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLables));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);
        petDemogBarChart.setDragEnabled(true);
        petDemogBarChart.setVisibleXRangeMaximum(3);
        float barSpace = 0.1f;
        float groupSpace = 0.5f;
        data.setBarWidth(0.15f);
        petDemogBarChart.getXAxis().setAxisMinimum(0);
        petDemogBarChart.animate();
        petDemogBarChart.groupBars(0, groupSpace, barSpace);
        petDemogBarChart.invalidate();

        //Owner Demographics Bar Chart
        String[] xLabels = new String[]{"18-28", "29-39", "40-50", "50-60"};
        BarChart ownerDemogBarChart = (BarChart) findViewById(R.id.ownerDemogBarChart);
        ArrayList<BarEntry> entries1 = new ArrayList<>();
        //for 19-28
        entries1.add(new BarEntry(1,10));
        //for 29-39
        entries1.add(new BarEntry(2,20));
        //for 40-50
        entries1.add(new BarEntry(3,30));
        //for 50-60
        entries1.add(new BarEntry(4, 5));
        ArrayList<BarEntry> entries2 = new ArrayList<>();
        //for 18-28
        entries2.add(new BarEntry(3,30));
        //for 29-39
        entries2.add(new BarEntry(4,40));
        //for 40-50
        entries2.add(new BarEntry(5, 50));
        //for 50-60
        entries2.add(new BarEntry(6, 7));
        BarDataSet barDataSet1 = new BarDataSet(entries1, "Female");
        barDataSet1.setColor(Color.MAGENTA);
        BarDataSet barDataSet2 = new BarDataSet(entries2, "Male");
        barDataSet2.setColor(Color.BLUE);
        BarData barData = new BarData(barDataSet1, barDataSet2);
        ownerDemogBarChart.setData(barData);
        ownerDemogBarChart.getDescription().setEnabled(false);
        XAxis xAxis2 = ownerDemogBarChart.getXAxis();
        xAxis2.setValueFormatter(new IndexAxisValueFormatter(xLabels));
        xAxis2.setCenterAxisLabels(true);
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.setGranularity(1);
        xAxis2.setGranularityEnabled(true);
        ownerDemogBarChart.setDragEnabled(true);
        ownerDemogBarChart.setVisibleXRangeMaximum(3);
        float ownerBarSpace = 0.1f;
        float ownerGroupSpace = 0.5f;
        barData.setBarWidth(0.15f);
        ownerDemogBarChart.getXAxis().setAxisMinimum(0);
        ownerDemogBarChart.animate();
        ownerDemogBarChart.groupBars(0, ownerGroupSpace, ownerBarSpace);
        ownerDemogBarChart.invalidate();

    }

    public void back(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}