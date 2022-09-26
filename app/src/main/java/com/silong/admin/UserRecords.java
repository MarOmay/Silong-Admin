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

public class UserRecords extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_records);
        getSupportActionBar().hide();

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        //Gender Pie Chart
        PieChart userGenderPieChart = (PieChart) findViewById(R.id.userGenderPieChart);
        ArrayList<PieEntry> userGender = new ArrayList<>();
        userGender.add(new PieEntry(20, "Male"));
        userGender.add(new PieEntry(17, "Female"));
        PieDataSet pieDataSet = new PieDataSet(userGender, "");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(16f);
        PieData userPieData = new PieData(pieDataSet);
        userGenderPieChart.setData(userPieData);
        userGenderPieChart.getDescription().setEnabled(false);
        userGenderPieChart.setCenterText("User Gender");
        userGenderPieChart.animate();

        //User Age Bar Chart
        String[] labels = new String[]{"18-28", "29-39", "40-50", "50-60"};
        BarChart userAgeBarChart = (BarChart) findViewById(R.id.userAgeBarChart);
        ArrayList<BarEntry> femaleAge = new ArrayList<>();
        //for 19-28
        femaleAge.add(new BarEntry(1,17));
        //for 29-39
        femaleAge.add(new BarEntry(2,12));
        //for 40-50
        femaleAge.add(new BarEntry(3,6));
        //for 50-60
        femaleAge.add(new BarEntry(4, 1));
        ArrayList<BarEntry> maleAge = new ArrayList<>();
        //for 18-28
        maleAge.add(new BarEntry(1,15));
        //for 29-39
        maleAge.add(new BarEntry(2,17));
        //for 40-50
        maleAge.add(new BarEntry(3, 21));
        //for 50-60
        maleAge.add(new BarEntry(4, 6));
        BarDataSet barDataSet1 = new BarDataSet(femaleAge, "Female");
        barDataSet1.setColor(Color.MAGENTA);
        BarDataSet barDataSet2 = new BarDataSet(maleAge, "Male");
        barDataSet2.setColor(Color.BLUE);
        BarData barData = new BarData(barDataSet1, barDataSet2);
        userAgeBarChart.setData(barData);
        userAgeBarChart.getDescription().setEnabled(false);
        XAxis xAxis2 = userAgeBarChart.getXAxis();
        xAxis2.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis2.setCenterAxisLabels(true);
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.setGranularity(1);
        xAxis2.setGranularityEnabled(true);
        userAgeBarChart.setDragEnabled(true);
        userAgeBarChart.setVisibleXRangeMaximum(3);
        float ownerBarSpace = 0.1f;
        float ownerGroupSpace = 0.5f;
        barData.setBarWidth(0.15f);
        userAgeBarChart.getXAxis().setAxisMinimum(0);
        userAgeBarChart.animate();
        userAgeBarChart.groupBars(0, ownerGroupSpace, ownerBarSpace);
        userAgeBarChart.invalidate();

        //Account Status Pie Chart
        PieChart accountStatusPieChart = (PieChart) findViewById(R.id.accountStatusPieChart);
        ArrayList<PieEntry> status = new ArrayList<>();
        status.add(new PieEntry(20, "Active"));
        status.add(new PieEntry(17, "Deactivated"));
        status.add(new PieEntry(3, "Deleted"));
        PieDataSet statusDataSet = new PieDataSet(status, "");
        statusDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        statusDataSet.setValueTextColor(Color.WHITE);
        statusDataSet.setValueTextSize(16f);
        PieData statusPieData = new PieData(statusDataSet);
        accountStatusPieChart.setData(statusPieData);
        accountStatusPieChart.getDescription().setEnabled(false);
        accountStatusPieChart.setCenterText("Account Status");
        accountStatusPieChart.animate();

        //Adoption History Pie Chart
        PieChart adoptionHistoryPieChart = (PieChart) findViewById(R.id.adoptionHistoryPieChart);
        ArrayList<PieEntry> history = new ArrayList<>();
        history.add(new PieEntry(24, "With"));
        history.add(new PieEntry(17, "Without"));
        PieDataSet historyDataSet = new PieDataSet(history, "");
        historyDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        historyDataSet.setValueTextColor(Color.WHITE);
        historyDataSet.setValueTextSize(16f);
        PieData historyPieData = new PieData(historyDataSet);
        adoptionHistoryPieChart.setData(historyPieData);
        adoptionHistoryPieChart.getDescription().setEnabled(false);
        adoptionHistoryPieChart.setCenterText("Adoption History");
        adoptionHistoryPieChart.animate();

        //Communication History Pie Chart
        PieChart commHistoryPieChart = (PieChart) findViewById(R.id.commHistoryPieChart);
        ArrayList<PieEntry> comm = new ArrayList<>();
        comm.add(new PieEntry(31, "With"));
        comm.add(new PieEntry(10, "Without"));
        PieDataSet commDataSet = new PieDataSet(comm, "");
        commDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        commDataSet.setValueTextColor(Color.WHITE);
        commDataSet.setValueTextSize(16f);
        PieData commPieData = new PieData(commDataSet);
        commHistoryPieChart.setData(commPieData);
        commHistoryPieChart.getDescription().setEnabled(false);
        commHistoryPieChart.setCenterText("Communication History");
        commHistoryPieChart.animate();
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