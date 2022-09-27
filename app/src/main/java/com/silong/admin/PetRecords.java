package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class PetRecords extends AppCompatActivity {

    TextView dogTotalTv, catTotalTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_records);
        getSupportActionBar().hide();

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        dogTotalTv = (TextView) findViewById(R.id.dogTotalTv);
        catTotalTv = (TextView) findViewById(R.id.catTotalTv);

        // ALL DOGS
        //Dog Status Pie Chart
        PieChart dogStatusPieChart = (PieChart) findViewById(R.id.dogStatusPieChart);
        ArrayList<PieEntry> dogStatus = new ArrayList<>();
        dogStatus.add(new PieEntry(24, "Active"));
        dogStatus.add(new PieEntry(12, "Adopted"));
        dogStatus.add(new PieEntry(6, "Euthanized"));
        PieDataSet dogStatusDataSet = new PieDataSet(dogStatus, "");
        dogStatusDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dogStatusDataSet.setValueTextColor(Color.WHITE);
        dogStatusDataSet.setValueTextSize(16f);
        PieData dogStatusPieData = new PieData(dogStatusDataSet);
        dogStatusPieChart.setData(dogStatusPieData);
        dogStatusPieChart.getDescription().setEnabled(false);
        dogStatusPieChart.setCenterText("99");
        dogStatusPieChart.setCenterTextSize(40);
        dogStatusPieChart.setCenterTextTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        dogStatusPieChart.animate();

        //Dog Gender Pie Chart
        PieChart dogGenderPieChart = (PieChart) findViewById(R.id.dogGenderPieChart);
        ArrayList<PieEntry> dogGender = new ArrayList<>();
        dogGender.add(new PieEntry(23, "Male"));
        dogGender.add(new PieEntry(12, "Female"));
        PieDataSet dogGenderDataSet = new PieDataSet(dogGender, "");
        dogGenderDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dogGenderDataSet.setValueTextColor(Color.WHITE);
        dogGenderDataSet.setValueTextSize(16f);
        PieData dogGenderPieData = new PieData(dogGenderDataSet);
        dogGenderPieChart.setData(dogGenderPieData);
        dogGenderPieChart.getDescription().setEnabled(false);
        dogGenderPieChart.setCenterText("99");
        dogGenderPieChart.setCenterTextSize(40);
        dogGenderPieChart.setCenterTextTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        dogGenderPieChart.animate();

        //Dog Age Pie Chart
        PieChart dogAgePieChart = (PieChart) findViewById(R.id.dogAgePieChart);
        ArrayList<PieEntry> dogAge = new ArrayList<>();
        dogAge.add(new PieEntry(4, "Puppy"));
        dogAge.add(new PieEntry(32, "Young"));
        dogAge.add(new PieEntry(11, "Old"));
        PieDataSet dogAgeDataSet = new PieDataSet(dogAge, "");
        dogAgeDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        dogAgeDataSet.setValueTextColor(Color.WHITE);
        dogAgeDataSet.setValueTextSize(16f);
        PieData dogAgePieData = new PieData(dogAgeDataSet);
        dogAgePieChart.setData(dogAgePieData);
        dogAgePieChart.getDescription().setEnabled(false);
        dogAgePieChart.setCenterText("99");
        dogAgePieChart.setCenterTextSize(40);
        dogAgePieChart.setCenterTextTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        dogAgePieChart.animate();

        //Dog Size Pie Chart
        PieChart dogSizePieChart = (PieChart) findViewById(R.id.dogSizePieChart);
        ArrayList<PieEntry> dogSize = new ArrayList<>();
        dogSize.add(new PieEntry(1, "Small"));
        dogSize.add(new PieEntry(2, "Medium"));
        dogSize.add(new PieEntry(3, "Large"));
        PieDataSet dogSizeDataSet = new PieDataSet(dogSize, "");
        dogSizeDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dogSizeDataSet.setValueTextColor(Color.WHITE);
        dogSizeDataSet.setValueTextSize(16f);
        PieData dogSizePieData = new PieData(dogSizeDataSet);
        dogSizePieChart.setData(dogSizePieData);
        dogSizePieChart.getDescription().setEnabled(false);
        dogSizePieChart.setCenterText("99");
        dogSizePieChart.setCenterTextSize(40);
        dogSizePieChart.setCenterTextTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        dogSizePieChart.animate();

        // ALL CAT
        //Cat Status Pie Chart
        PieChart catStatusPieChart = (PieChart) findViewById(R.id.catStatusPieChart);
        ArrayList<PieEntry> catStatus = new ArrayList<>();
        catStatus.add(new PieEntry(24, "Active"));
        catStatus.add(new PieEntry(12, "Adopted"));
        catStatus.add(new PieEntry(6, "Euthanized"));
        PieDataSet catStatusDataSet = new PieDataSet(catStatus, "");
        catStatusDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        catStatusDataSet.setValueTextColor(Color.WHITE);
        catStatusDataSet.setValueTextSize(16f);
        PieData catStatusPieData = new PieData(catStatusDataSet);
        catStatusPieChart.setData(catStatusPieData);
        catStatusPieChart.getDescription().setEnabled(false);
        catStatusPieChart.setCenterText("99");
        catStatusPieChart.setCenterTextSize(40);
        catStatusPieChart.setCenterTextTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        catStatusPieChart.animate();

        //Dog Gender Pie Chart
        PieChart catGenderPieChart = (PieChart) findViewById(R.id.catGenderPieChart);
        ArrayList<PieEntry> catGender = new ArrayList<>();
        catGender.add(new PieEntry(23, "Male"));
        catGender.add(new PieEntry(12, "Female"));
        PieDataSet catGenderDataSet = new PieDataSet(catGender, "");
        catGenderDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        catGenderDataSet.setValueTextColor(Color.WHITE);
        catGenderDataSet.setValueTextSize(16f);
        PieData catGenderPieData = new PieData(catGenderDataSet);
        catGenderPieChart.setData(catGenderPieData);
        catGenderPieChart.getDescription().setEnabled(false);
        catGenderPieChart.setCenterText("99");
        catGenderPieChart.setCenterTextSize(40);
        catGenderPieChart.setCenterTextTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        catGenderPieChart.animate();

        //Dog Age Pie Chart
        PieChart catAgePieChart = (PieChart) findViewById(R.id.catAgePieChart);
        ArrayList<PieEntry> catAge = new ArrayList<>();
        catAge.add(new PieEntry(4, "Kitten"));
        catAge.add(new PieEntry(32, "Young"));
        catAge.add(new PieEntry(11, "Old"));
        PieDataSet catAgeDataSet = new PieDataSet(catAge, "");
        catAgeDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        catAgeDataSet.setValueTextColor(Color.WHITE);
        catAgeDataSet.setValueTextSize(16f);
        PieData catAgePieData = new PieData(catAgeDataSet);
        catAgePieChart.setData(catAgePieData);
        catAgePieChart.getDescription().setEnabled(false);
        catAgePieChart.setCenterText("99");
        catAgePieChart.setCenterTextSize(40);
        catAgePieChart.setCenterTextTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        catAgePieChart.animate();

        //Dog Size Pie Chart
        PieChart catSizePieChart = (PieChart) findViewById(R.id.catSizePieChart);
        ArrayList<PieEntry> catSize = new ArrayList<>();
        catSize.add(new PieEntry(1, "Small"));
        catSize.add(new PieEntry(2, "Medium"));
        catSize.add(new PieEntry(3, "Large"));
        PieDataSet catSizeDataSet = new PieDataSet(catSize, "");
        catSizeDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        catSizeDataSet.setValueTextColor(Color.WHITE);
        catSizeDataSet.setValueTextSize(16f);
        PieData catSizePieData = new PieData(catSizeDataSet);
        catSizePieChart.setData(catSizePieData);
        catSizePieChart.getDescription().setEnabled(false);
        catSizePieChart.setCenterText("99");
        catSizePieChart.setCenterTextSize(40);
        catSizePieChart.setCenterTextTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        catSizePieChart.animate();
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