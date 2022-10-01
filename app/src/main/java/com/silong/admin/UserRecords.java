package com.silong.admin;

import static com.silong.Operation.Utility.dateToday;
import static com.silong.Operation.Utility.timeNow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
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
import com.silong.CustomView.CustomBarGraph;
import com.silong.CustomView.CustomPieChart;
import com.silong.CustomView.ExportDialog;
import com.silong.EnumClass.Gender;
import com.silong.EnumClass.PetAge;
import com.silong.EnumClass.PetColor;
import com.silong.EnumClass.PetSize;
import com.silong.EnumClass.PetType;
import com.silong.Object.Adoption;
import com.silong.Object.Pet;
import com.silong.Object.User;
import com.silong.Operation.Spreadsheet;
import com.silong.Operation.Utility;

import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;

public class UserRecords extends AppCompatActivity {

    private FirebaseDatabase mDatabase;

    private ArrayList<User> users = new ArrayList<>();

    ImageView userExport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_records);
        getSupportActionBar().hide();

        //register receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mExportDialogReceiver, new IntentFilter("export-requested"));

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        userExport = findViewById(R.id.userExport);

        //initialize Firebase objects
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        extractUsers();
    }

    public void onPressedExport(View view){

        if (!Utility.requestPermission(UserRecords.this, Utility.STORAGE_REQUEST_CODE))
            return;

        ExportDialog exportDialog = new ExportDialog(UserRecords.this);
        exportDialog.show();
    }

    private void showGenderGraph(){
        int male = 0, female = 0;

        try {
            for (User user : users){
                int temp = user.getGender() == Gender.MALE ? male++ : female++;
            }

            if (male + female == 0)
                return;

            //Gender Pie Chart
            ArrayList<PieEntry> userGender = new ArrayList<>();
            if (male > 0)
                userGender.add(new PieEntry(male, "Male"));
            if (female > 0)
                userGender.add(new PieEntry(female, "Female"));

            CustomPieChart userGenderPieChart = findViewById(R.id.userGenderPieChart);
            userGenderPieChart.setEntries(userGender).refresh();

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

            CustomBarGraph userAgeBarChart = findViewById(R.id.userAgeBarChart);
            userAgeBarChart.setEntries(labels, barDataSet1, barDataSet2).refresh();

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

            if (active + inactive + deleted == 0)
                return;

            //Account Status Pie Chart
            ArrayList<PieEntry> status = new ArrayList<>();
            if (active > 0)
                status.add(new PieEntry(active, "Active"));
            if (inactive > 0)
                status.add(new PieEntry(inactive, "Deactivated"));
            if (deleted > 0)
                status.add(new PieEntry(deleted, "Deleted"));

            CustomPieChart accountStatusPieChart = findViewById(R.id.accountStatusPieChart);
            accountStatusPieChart.setEntries(status).refresh();

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

            if (with + without == 0)
                return;

            //Adoption History Pie Chart
            ArrayList<PieEntry> history = new ArrayList<>();
            if (with > 0)
                history.add(new PieEntry(with, "With"));
            if (without > 0)
                history.add(new PieEntry(without, "Without"));

            CustomPieChart adoptionHistoryPieChart = findViewById(R.id.adoptionHistoryPieChart);
            adoptionHistoryPieChart.setEntries(history).refresh();

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
                        extractUserInfo(users.size()-1, user.getUserID(), "lastName");
                        extractUserInfo(users.size()-1, user.getUserID(), "firstName");

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
                        case "firstName":
                            String firstName = snapshot.getValue().toString();
                            User f = users.get(index);
                            f.setFirstName(firstName);
                            users.set(index, f);
                            break;
                        case "lastName":
                            String lastName = snapshot.getValue().toString();
                            User l = users.get(index);
                            l.setLastName(lastName);
                            users.set(index, l);
                            break;
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

    private BroadcastReceiver mExportDialogReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //prepare file

            ArrayList<Object[]> entries = new ArrayList<>();
            //labels
            entries.add(new Object[]{"Account Status", "First Name", "Last Name", "Gender", "Age", "Birthday"});

            for (User user : users){
                entries.add(new Object[]{
                        user.getAccountStatus() ? "Active" : user.isDeleted() ? "Deleted" : "Deactivated",
                        user.getFirstName(),
                        user.getLastName(),
                        user.getGender() == Gender.MALE ? "Male" : "Female",
                        String.valueOf(Utility.getAge(user.getBirthday())),
                        user.getBirthday()
                });
            }

            Spreadsheet spreadsheet = new Spreadsheet(UserRecords.this);
            spreadsheet.setEntries(entries);

            Workbook workbook = spreadsheet.create();

            //export process here
            String exportType = intent.getStringExtra("exportType"); //email or device
            if (exportType.equals("email")){
                spreadsheet.sendAsEmail("User Records");
            }
            else if (exportType.equals("device")){

                String filename = "User Records" + "-" + dateToday() + "-" + timeNow().replace("*", "") + ".xls";
                boolean success = spreadsheet.writeToFile(filename, false);

                if (success)
                    Toast.makeText(UserRecords.this, "Exported to Documents/Silong/"+filename, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(UserRecords.this, "Export failed", Toast.LENGTH_SHORT).show();

            }

        }
    };

    public void back(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mExportDialogReceiver);
    }
}