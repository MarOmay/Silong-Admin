package com.silong.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.silong.CustomView.LoadingDialog;
import com.silong.EnumClass.Gender;
import com.silong.EnumClass.PetAge;
import com.silong.EnumClass.PetType;
import com.silong.Object.Adoption;
import com.silong.Object.Pet;
import com.silong.Object.User;
import com.silong.Operation.Utility;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AdoptionRecords extends AppCompatActivity {

    private FirebaseDatabase mDatabase;

    private ArrayList<Adoption> adoptions = new ArrayList<>();
    private ArrayList<Pet> pets = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();

    ImageView adoptionExport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoption_records);
        getSupportActionBar().hide();


        //initialize Firebase objects
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        adoptionExport = findViewById(R.id.adoptionExport);

        extractAdoptionHistory();
    }

    private void showRequests(){
        int processing = 0, successful = 0;

        try {
            for (Adoption adoption : adoptions){
                switch (adoption.getStatus()){
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6: processing++; break;
                    case 7: successful++; break;
                }
            }

            if (processing + successful == 0)
                return;

            //Requests Pie Chart
            ArrayList<PieEntry> requests = new ArrayList<>();
            if (successful > 0)
                requests.add(new PieEntry(successful, "Successful"));
            if (processing > 0)
                requests.add(new PieEntry(processing, "Processing"));

            CustomPieChart requestsPieChart = findViewById(R.id.requestsPieChart);
            requestsPieChart.setEntries(requests).refresh();

        }
        catch (Exception e){
            Utility.log("AdoptionRecords.showRequests: " + e.getMessage());
        }


    }

    private void showPetDemo(){
        int puppy = 0, kitten = 0;
        int youngD = 0, youngC = 0;
        int oldD = 0, oldC = 0;

        try {

            for (Pet pet : pets){
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

            //Pet Demographics Bar Chart
            String[] xAxisLables = new String[]{"Puppy/Kitten", "Young", "Adult"};

            ArrayList<BarEntry> entriesOne = new ArrayList<>();
            //for puppy/kitten
            entriesOne.add(new BarEntry(1,puppy));
            //for young
            entriesOne.add(new BarEntry(2,youngD));
            //for adult
            entriesOne.add(new BarEntry(3,oldD));

            ArrayList<BarEntry> entriesTwo = new ArrayList<>();
            //for puppy/kitten
            entriesTwo.add(new BarEntry(3,kitten));
            //for young
            entriesTwo.add(new BarEntry(4,youngC));
            //for adult
            entriesTwo.add(new BarEntry(5, oldC));

            BarDataSet barDataSetOne = new BarDataSet(entriesOne, "Cat");
            barDataSetOne.setColor(getResources().getColor(R.color.pink));
            BarDataSet barDataSetTwo = new BarDataSet(entriesTwo, "Dog");
            barDataSetTwo.setColor(getResources().getColor(R.color.orange));

            CustomBarGraph petDemogBarChart = findViewById(R.id.petDemogBarChart);
            petDemogBarChart.setEntries(xAxisLables, barDataSetOne, barDataSetTwo).refresh();
        }
        catch (Exception e){
            Utility.log("AdoptionRecords.sPD: " + e.getMessage());
        }
    }

    private void showOwnerDemo(){
        int male1 = 0, female1 = 0;
        int male2 = 0, female2 = 0;
        int male3 = 0, female3 = 0;
        int male4 = 0, female4 = 0;

        try {

            for (User user : users){
                int age = Utility.getAge(user.getBirthday());

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

            //Owner Demographics Bar Chart
            String[] xLabels = new String[]{"18-28", "29-39", "40-50", "51-60"};

            ArrayList<BarEntry> entries1 = new ArrayList<>();
            //for 19-28
            entries1.add(new BarEntry(1,male1));
            //for 29-39
            entries1.add(new BarEntry(2,male2));
            //for 40-50
            entries1.add(new BarEntry(3,male3));
            //for 50-60
            entries1.add(new BarEntry(4, male4));
            ArrayList<BarEntry> entries2 = new ArrayList<>();
            //for 18-28
            entries2.add(new BarEntry(3,female1));
            //for 29-39
            entries2.add(new BarEntry(4,female2));
            //for 40-50
            entries2.add(new BarEntry(5, female3));
            //for 50-60
            entries2.add(new BarEntry(6, female4));
            BarDataSet barDataSet1 = new BarDataSet(entries1, "Female");
            barDataSet1.setColor(Color.MAGENTA);
            BarDataSet barDataSet2 = new BarDataSet(entries2, "Male");
            barDataSet2.setColor(Color.BLUE);

            CustomBarGraph ownerDemogBarChart =  findViewById(R.id.ownerDemogBarChart);
            ownerDemogBarChart.setEntries(xLabels, barDataSet1, barDataSet2).refresh();

        }
        catch (Exception e){
            Utility.log("AdoptionRecords.sOD: " + e.getMessage());
        }
    }

    private void extractAdoptionHistory(){
        //check internet connection
        if (!Utility.internetConnection(AdoptionRecords.this)){
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            LoadingDialog loadingDialog = new LoadingDialog(AdoptionRecords.this);
            loadingDialog.startLoadingDialog();

            DatabaseReference summaryRef = mDatabase.getReference("accountSummary");
            summaryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot snap : snapshot.getChildren()){
                        DatabaseReference tempRef = mDatabase.getReference("Users").child(snap.getKey()).child("adoptionHistory");
                        tempRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                try {

                                    //read each record if any
                                    for (DataSnapshot ds : snapshot.getChildren()){

                                        //record adoption
                                        Adoption adoption = new Adoption();

                                        adoption.setPetID(Integer.parseInt(ds.getKey()));
                                        adoption.setStatus(Integer.parseInt(ds.child("status").getValue().toString()));
                                        adoption.setDateRequested(ds.child("dateRequested").getValue().toString());

                                        adoptions.add(adoption);

                                        //record only successful adoption
                                        if (adoption.getStatus() == 7){
                                            //record pet
                                            Pet pet = new Pet();
                                            pet.setPetID(ds.getKey());

                                            pets.add(pet);
                                            Utility.log("pet added: " + pet.getPetID());

                                            extractPetInfo(pets.size()-1, pet.getPetID(), "type");
                                            extractPetInfo(pets.size()-1, pet.getPetID(), "age");

                                            //record user
                                            User user = new User();
                                            user.setUserID(snap.getKey());

                                            users.add(user);

                                            extractUserInfo(users.size()-1, user.getUserID(), "firstName");
                                            extractUserInfo(users.size()-1, user.getUserID(), "lastName");
                                            extractUserInfo(users.size()-1, user.getUserID(), "gender");
                                            extractUserInfo(users.size()-1, user.getUserID(), "birthday");
                                        }

                                    }

                                    showRequests();
                                }
                                catch (Exception ex){
                                    Utility.log("AdoptionRecords.eAH: " + ex.getMessage());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    loadingDialog.dismissLoadingDialog();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception e){
            Utility.log("AdoptionRecords.eAH: " + e.getMessage());
        }

    }

    private void extractPetInfo(int index, String petID, String info){
        //info = type or age only
        try {
            DatabaseReference tempRef = mDatabase.getReference("Pets").child(petID).child(info);
            tempRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int inf = Integer.parseInt(snapshot.getValue().toString());

                    switch (info){
                        case "type":
                            Pet p = pets.get(index);
                            p.setType(inf);
                            pets.set(index, p);
                            Utility.log("triggered type");
                            break;
                        case "age":
                            Pet pp = pets.get(index);
                            pp.setAge(inf);
                            pets.set(index, pp);
                            Utility.log("triggered age");
                            break;
                    }

                    showPetDemo();

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

    private void extractUserInfo(int index, String userID, String info){
        //info = firstName, lastName, birthday or gender only
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
                            break;
                        case "gender":
                            int gender = Integer.parseInt(snapshot.getValue().toString());
                            User g = users.get(index);
                            g.setGender(gender);
                            users.set(index, g);
                            break;
                    }

                    showOwnerDemo();

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