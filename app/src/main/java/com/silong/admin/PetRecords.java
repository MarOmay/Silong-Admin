package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.silong.CustomView.CustomBarGraph;
import com.silong.CustomView.CustomPieChart;
import com.silong.EnumClass.Gender;
import com.silong.EnumClass.PetAge;
import com.silong.EnumClass.PetSize;
import com.silong.EnumClass.PetType;
import com.silong.Object.Pet;
import com.silong.Operation.Utility;

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

        dogTotalTv = findViewById(R.id.dogTotalTv);
        catTotalTv = findViewById(R.id.catTotalTv);

        int dogCtr = 0, catCtr = 0;

        for (Pet p : AdminData.pets){
            if (p.getType() == PetType.DOG)
                dogCtr++;
            else if (p.getType() == PetType.CAT)
                catCtr++;
        }

        dogTotalTv.setText(String.valueOf(dogCtr));
        catTotalTv.setText(String.valueOf(catCtr));

        // ALL CHARTS
        showGenderChart();
        showAgeChart();
        showSizeChart();

    }

    private void showGenderChart(){

        int maleD = 0, femaleD = 0;
        int maleC = 0, femaleC = 0;

        try {

            for (Pet pet : AdminData.pets){
                if (pet.getType() == PetType.DOG){
                    int temp = pet.getGender() == Gender.MALE ? maleD++ : femaleD++;
                }
                else if (pet.getType() == PetType.CAT){
                    int temp = pet.getGender() == Gender.MALE ? maleC++ : femaleC++;
                }

            }

            if (maleD + femaleD + maleC + femaleC == 0)
                return;

            //Dog Gender Pie Chart
            ArrayList<PieEntry> gender = new ArrayList<>();
            if (maleD > 0)
                gender.add(new PieEntry(maleD, "Male Dog"));
            if (femaleD > 0)
                gender.add(new PieEntry(femaleD, "Female Dog"));
            if (maleC > 0)
                gender.add(new PieEntry(maleC, "Male Cat"));
            if (femaleC > 0)
                gender.add(new PieEntry(femaleC, "Female Cat"));

            CustomPieChart dogGenderPieChart = findViewById(R.id.dogGenderPieChart);
            dogGenderPieChart.setEntries(gender).refresh();

        }
        catch (Exception e){
            Utility.log("PetRecords.showStatus: " + e.getMessage());
        }

    }

    private void showAgeChart(){
        int puppy = 0, kitten = 0;
        int youngD = 0, youngC = 0;
        int oldD = 0, oldC = 0;

        try {

            for (Pet pet : AdminData.pets){
                if (pet.getType() == PetType.DOG){
                    switch (pet.getAge()){
                        case PetAge.PUPPY: puppy++; break;
                        case PetAge.YOUNG: youngD++; break;
                        case PetAge.OLD: oldD++; break;
                    }
                }
                else if (pet.getType() == PetType.CAT){
                    switch (pet.getAge()){
                        case PetAge.PUPPY: kitten++; break;
                        case PetAge.YOUNG: youngC++; break;
                        case PetAge.OLD: oldC++; break;
                    }
                }

            }

            if (puppy + kitten + youngD + youngC + oldD + oldC == 0)
                return;

            ArrayList<PieEntry> age = new ArrayList<>();
            if (puppy > 0)
                age.add(new PieEntry(puppy, "Puppy"));
            if (kitten > 0)
                age.add(new PieEntry(kitten, "Kitten"));
            if (youngD > 0)
                age.add(new PieEntry(youngD, "Young Dog"));
            if (youngC > 0)
                age.add(new PieEntry(youngC, "Young Cat"));
            if (oldD > 0)
                age.add(new PieEntry(oldD, "Old Dog"));
            if (oldC > 0)
                age.add(new PieEntry(oldC, "Old Cat"));


            CustomPieChart customPieChart = findViewById(R.id.dogAgePieChart);
            customPieChart.setEntries(age).refresh();

        }
        catch (Exception e){
            Utility.log("AdoptionRecords.sPD: " + e.getMessage());
        }
    }

    private void showSizeChart(){
        int smallD = 0, smallC = 0;
        int mediumD = 0, mediumC = 0;
        int largeD = 0, largeC = 0;

        try {

            for (Pet pet : AdminData.pets){
                if (pet.getType() == PetType.DOG){
                    switch (pet.getSize()){
                        case PetSize.SMALL: smallD++; break;
                        case PetSize.MEDIUM: mediumD++; break;
                        case PetSize.LARGE: largeD++; break;
                    }
                }
                else if (pet.getType() == PetType.CAT){
                    switch (pet.getSize()){
                        case PetSize.SMALL: smallC++; break;
                        case PetSize.MEDIUM: mediumC++; break;
                        case PetSize.LARGE: largeC++; break;
                    }
                }

            }

            if (smallD + mediumD + largeD + smallC + mediumC + largeC == 0)
                return;

            ArrayList<PieEntry> size = new ArrayList<>();
            if (smallD > 0)
                size.add(new PieEntry(smallD, "Small Dog"));
            if (mediumD > 0)
                size.add(new PieEntry(mediumD, "Medium Dog"));
            if (largeD > 0)
                size.add(new PieEntry(largeD, "Large Dog"));
            if (smallC > 0)
                size.add(new PieEntry(smallC, "Small Cat"));
            if (mediumC > 0)
                size.add(new PieEntry(mediumC, "Medium Cat"));
            if (largeC > 0)
                size.add(new PieEntry(largeC, "Large Cat"));


            CustomPieChart customPieChart = findViewById(R.id.dogSizePieChart);
            customPieChart.setEntries(size).refresh();

        }
        catch (Exception e){
            Utility.log("AdoptionRecords.sPD: " + e.getMessage());
        }
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