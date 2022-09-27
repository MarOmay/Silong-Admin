package com.silong.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.EnumClass.Gender;
import com.silong.Object.Adoption;
import com.silong.Object.User;
import com.silong.Operation.Utility;

import java.util.ArrayList;

public class UserRecords extends AppCompatActivity {

    private FirebaseDatabase mDatabase;

    private ArrayList<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_records);
        getSupportActionBar().hide();

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        //initialize Firebase objects
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        extractUsers();
    }

    private void showGenderGraph(){
        int male = 0, female = 0;

        try {
            for (User user : users){
                int temp = user.getGender() == Gender.MALE ? male++ : female++;
            }

            //Gender Pie Chart
            PieChart userGenderPieChart = (PieChart) findViewById(R.id.userGenderPieChart);
            userGenderPieChart.setVisibility(View.GONE);

            ArrayList<PieEntry> userGender = new ArrayList<>();
            userGender.add(new PieEntry(male, "Male"));
            userGender.add(new PieEntry(female, "Female"));
            PieDataSet pieDataSet = new PieDataSet(userGender, "");
            pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            pieDataSet.setValueTextColor(Color.WHITE);
            pieDataSet.setValueTextSize(16f);
            PieData userPieData = new PieData(pieDataSet);
            userGenderPieChart.setData(userPieData);
            userGenderPieChart.getDescription().setEnabled(false);
            userGenderPieChart.setCenterText("User Gender");
            userGenderPieChart.setVisibility(View.VISIBLE);
            userGenderPieChart.animate();
        }
        catch (Exception e){
            Utility.log("UserRecords.sGG: " + e.getMessage());
        }
    }

    private void showAgeGraph(){

        int male1 = 0, female1 = 0;
        int male2 = 0, female2 = 0;
        int male3 = 0, female3 = 0;
        int male4 = 0, female4 = 0;

        try {

            for (User user : users){
                int age = Utility.getAge(user.getBirthday());

                Utility.log("age: " + age);

                if (age >= 18 && age <= 28) {
                    int i = user.getGender() == Gender.MALE ? male1++ : female1++;
                }
                else if (age >= 29 && age <= 39) {
                    int i = user.getGender() == Gender.MALE ? male2++ : female2++;
                }
                else if (age >= 40 && age <= 50) {
                    int i = user.getGender() == Gender.MALE ? male3++ : female3++;
                }
                else if (age >= 51 && age <= 60) {
                    int i = user.getGender() == Gender.MALE ? male4++ : female4++;
                }
            }

            //User Age Bar Chart
            String[] labels = new String[]{"18-28", "29-39", "40-50", "51-60"};
            BarChart userAgeBarChart = (BarChart) findViewById(R.id.userAgeBarChart);
            userAgeBarChart.setVisibility(View.GONE);

            ArrayList<BarEntry> femaleAge = new ArrayList<>();
            //for 19-28
            femaleAge.add(new BarEntry(1,female1));
            //for 29-39
            femaleAge.add(new BarEntry(2,female2));
            //for 40-50
            femaleAge.add(new BarEntry(3,female3));
            //for 50-60
            femaleAge.add(new BarEntry(4, female4));
            ArrayList<BarEntry> maleAge = new ArrayList<>();
            //for 18-28
            maleAge.add(new BarEntry(1,male1));
            //for 29-39
            maleAge.add(new BarEntry(2,male2));
            //for 40-50
            maleAge.add(new BarEntry(3, male3));
            //for 50-60
            maleAge.add(new BarEntry(4, male4));
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
            userAgeBarChart.setVisibility(View.VISIBLE);
            userAgeBarChart.animate();
            userAgeBarChart.groupBars(0, ownerGroupSpace, ownerBarSpace);
            userAgeBarChart.invalidate();
        }
        catch (Exception e){
            Utility.log("UserRecords.sAG: " + e.getMessage());
        }
    }

    private void showStatusGraph(){
        int active = 0, inactive = 0, deleted = 0;

        try {

            for (User user : users){
                if (user.getAccountStatus())
                    active++;
                else if (!user.getAccountStatus() && !user.isDeleted())
                    inactive++;
                else if (!user.getAccountStatus() && user.isDeleted())
                    deleted++;
            }

            //Account Status Pie Chart
            PieChart accountStatusPieChart = (PieChart) findViewById(R.id.accountStatusPieChart);
            accountStatusPieChart.setVisibility(View.GONE);

            ArrayList<PieEntry> status = new ArrayList<>();
            status.add(new PieEntry(active, "Active"));
            status.add(new PieEntry(inactive, "Deactivated"));
            status.add(new PieEntry(deleted, "Deleted"));
            PieDataSet statusDataSet = new PieDataSet(status, "");
            statusDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            statusDataSet.setValueTextColor(Color.WHITE);
            statusDataSet.setValueTextSize(16f);
            PieData statusPieData = new PieData(statusDataSet);
            accountStatusPieChart.setData(statusPieData);
            accountStatusPieChart.getDescription().setEnabled(false);
            accountStatusPieChart.setCenterText("Account Status");
            accountStatusPieChart.setVisibility(View.VISIBLE);
            accountStatusPieChart.animate();
        }
        catch (Exception e){
            Utility.log("UserRecords.sSG: " + e.getMessage());
        }
    }

    private void showAdoptionHistory(){
        int with = 0, without = 0;

        try {

            for (User user : users){
                int count = user.adoptionHistory.size();
                if (count > 0)
                    with++;
                else
                    without++;
            }

            //Adoption History Pie Chart
            PieChart adoptionHistoryPieChart = (PieChart) findViewById(R.id.adoptionHistoryPieChart);
            adoptionHistoryPieChart.setVisibility(View.GONE);

            ArrayList<PieEntry> history = new ArrayList<>();
            history.add(new PieEntry(with, "With"));
            history.add(new PieEntry(without, "Without"));
            PieDataSet historyDataSet = new PieDataSet(history, "");
            historyDataSet.setColors(ColorTemplate.PASTEL_COLORS);
            historyDataSet.setValueTextColor(Color.WHITE);
            historyDataSet.setValueTextSize(16f);
            PieData historyPieData = new PieData(historyDataSet);
            adoptionHistoryPieChart.setData(historyPieData);
            adoptionHistoryPieChart.getDescription().setEnabled(false);
            adoptionHistoryPieChart.setCenterText("Adoption History");
            adoptionHistoryPieChart.setVisibility(View.VISIBLE);
            adoptionHistoryPieChart.animate();

        }
        catch (Exception e){
            Utility.log("UserRecords.sAH: " + e.getMessage());
        }
    }

    private void extractUsers(){
        //check internet connection
        if (!Utility.internetConnection(UserRecords.this)){
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            //get all userID
            DatabaseReference accSumRef = mDatabase.getReference("accountSummary");
            accSumRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot ds : snapshot.getChildren()){

                        //skip null, if any
                        if (ds.getKey().equals("null"))
                            continue;

                        User user = new User();
                        user.setUserID(ds.getKey());
                        String status = ds.getValue().toString();
                        switch (status){
                            case "true": user.setAccountStatus(true);break;
                            case "false":
                                user.setAccountStatus(false);
                                user.setDeleted(false);
                                break;
                            case "deleted":
                                user.setAccountStatus(false);
                                user.setDeleted(true);
                                break;
                        }

                        users.add(user);

                        extractUserInfo(users.size()-1, user.getUserID(), "adoptionHistory");
                        extractUserInfo(users.size()-1, user.getUserID(), "birthday");
                        extractUserInfo(users.size()-1, user.getUserID(), "gender");

                        showStatusGraph();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        catch (Exception e){
            Utility.log("UserRecords.extractUsers: " + e.getMessage());
        }
    }

    private void extractUserInfo(int index, String userID, String info){
        //info = adoptionHistory, birthday or gender only
        try {
            DatabaseReference tempRef = mDatabase.getReference("Users").child(userID).child(info);
            tempRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    switch (info){
                        case "birthday":
                            String birthday = snapshot.getValue().toString();
                            User b = users.get(index);
                            b.setBirthday(birthday);
                            users.set(index, b);
                            showAgeGraph();
                            break;
                        case "gender":
                            int gender = Integer.parseInt(snapshot.getValue().toString());
                            User g = users.get(index);
                            g.setGender(gender);
                            users.set(index, g);
                            showGenderGraph();
                            break;

                        case "adoptionHistory":
                            //check only if there's adoptionHistory
                            for (DataSnapshot ds : snapshot.getChildren()){
                                User ah = users.get(index);
                                ah.adoptionHistory.add(new Adoption());
                                users.set(index, ah);
                                showAdoptionHistory();
                                break;
                            }
                            break;
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        catch (Exception e){
            Utility.log("AdoptionRecords.ePI: " + e.getMessage());
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