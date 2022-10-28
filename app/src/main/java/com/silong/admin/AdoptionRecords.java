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
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.CustomView.CustomBarGraph;
import com.silong.CustomView.CustomPieChart;
import com.silong.CustomView.DateRangeFromPicker;
import com.silong.CustomView.DateRangePickerDialog;
import com.silong.CustomView.DateRangePickerReport;
import com.silong.CustomView.DateRangeToPicker;
import com.silong.CustomView.ExportDialog;
import com.silong.CustomView.LoadingDialog;
import com.silong.EnumClass.Gender;
import com.silong.EnumClass.PetAge;
import com.silong.EnumClass.PetType;
import com.silong.Object.Adoption;
import com.silong.Object.Pet;
import com.silong.Object.User;
import com.silong.Operation.Spreadsheet;
import com.silong.Operation.Utility;

import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

public class AdoptionRecords extends AppCompatActivity {

    private FirebaseDatabase mDatabase;

    private ArrayList<Adoption> adoptions = new ArrayList<>();
    private ArrayList<Adoption> adoptions_cc = new ArrayList<>(); //for cross-checking
    private ArrayList<Pet> pets = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();

    private ArrayList<Adoption> EXPORTABLE = new ArrayList<>();

    public static String dateFrom = Utility.dateToday().replace("-","/");
    public static String dateTo = Utility.dateToday().replace("-","/");

    public static boolean customDate = false;

    ImageView adoptionExport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoption_records);
        getSupportActionBar().hide();


        //initialize Firebase objects
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        //initialize receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mExportDialogReceiver, new IntentFilter("export-requested"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mReloadReceiver, new IntentFilter("refresh-records"));

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        adoptionExport = findViewById(R.id.adoptionExport);

        dateFrom = Utility.dateToday().replace("-","/");
        dateTo = Utility.dateToday().replace("-","/");
        customDate = false;

        extractAdoptionRequest();

    }

    public void onPressedExport(View view){

        if (!Utility.requestPermission(AdoptionRecords.this, Utility.STORAGE_REQUEST_CODE))
            return;

        ExportDialog exportDialog = new ExportDialog(AdoptionRecords.this);
        exportDialog.show();
    }

    public void onDateRangePressed(View view){
        DateRangePickerReport drpd = new DateRangePickerReport(AdoptionRecords.this);
        drpd.show();

        DateRangeFromPicker drfp = new DateRangeFromPicker(AdoptionRecords.this, drpd);
        DateRangeToPicker drtp = new DateRangeToPicker(AdoptionRecords.this, drpd);

        drpd.fromET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drfp.show(getSupportFragmentManager(), null);

            }
        });

        drpd.toET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drtp.show(getSupportFragmentManager(), null);

            }
        });

    }

    private void showRequests(ArrayList<Adoption> adoptionsList){

        EXPORTABLE = adoptionsList;

        if (adoptionsList.size() == 0){
            ArrayList<PieEntry> requests = new ArrayList<>();
            requests.add(new PieEntry(0, "No result"));
            CustomPieChart requestsPieChart = findViewById(R.id.requestsPieChart);
            requestsPieChart.setEntries(requests).refresh();
            return;
        }

        int processing = 0, successful = 0;

        try {
            for (Adoption adoption : adoptionsList){
                switch (adoption.getStatus()){
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5: processing++; break;
                    case 6: successful++; break;
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
            String[] xAxisLables = new String[]{"Kitten/Puppy", "Young", "Old"};

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

            BarDataSet barDataSetOne = new BarDataSet(entriesTwo, "Cat");
            barDataSetOne.setColor(getResources().getColor(R.color.pink));

            BarDataSet barDataSetTwo = new BarDataSet(entriesOne, "Dog");
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

            BarDataSet barDataSet1 = new BarDataSet(entries1, "Male");
            barDataSet1.setColor(Color.BLUE);
            BarDataSet barDataSet2 = new BarDataSet(entries2, "Female");
            barDataSet2.setColor(Color.MAGENTA);

            CustomBarGraph ownerDemogBarChart =  findViewById(R.id.ownerDemogBarChart);
            ownerDemogBarChart.setEntries(xLabels, barDataSet1, barDataSet2).refresh();

        }
        catch (Exception e){
            Utility.log("AdoptionRecords.sOD: " + e.getMessage());
        }
    }

    private void extractAdoptionRequest(){
        //check internet connection
        if (!Utility.internetConnection(AdoptionRecords.this)){
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LoadingDialog loadingDialog = new LoadingDialog(AdoptionRecords.this);
            loadingDialog.startLoadingDialog();

            DatabaseReference reqRef = mDatabase.getReference("adoptionRequest");
            reqRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot snap : snapshot.getChildren()){
                        try {
                            Adoption adoption = new Adoption();

                            String dateRequested = snap.child("dateRequested").getValue().toString();
                            int petID = Integer.parseInt(snap.child("petID").getValue().toString());
                            int status = Integer.parseInt(snap.child("status").getValue().toString());

                            adoption.setPetID(petID);
                            adoption.setDateRequested(dateRequested);
                            adoption.setStatus(status);

                            adoptions_cc.add(adoption);
                        }
                        catch (Exception e){
                            Utility.log("AdoptionRecords.eAR.oDC: " + e.getMessage());
                        }
                    }

                    loadingDialog.dismissLoadingDialog();
                    extractAdoptionHistory();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    loadingDialog.dismissLoadingDialog();
                    Utility.log("AdoptionRecords.eAR: " + error.getMessage());
                }
            });


        }
        catch (Exception e){
            Utility.log("AdoptionRecords.eAR: " + e.getMessage());
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

                        if (snap.getKey() == null || snap.getKey().equals("null"))
                            return; //skip

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

                                        //check if this data exists
                                        if (ds.child("actualAdotionDate").getValue() != null){
                                            adoption.setActualAdoptionDate(ds.child("actualAdotionDate").getValue().toString());
                                        }
                                        else {
                                            adoption.setActualAdoptionDate("N/A");
                                        }

                                        if (ds.child("memo").getValue() != null){
                                            adoption.setMemo(ds.child("memo").getValue().toString());
                                        }
                                        else {
                                            adoption.setMemo("N/A");
                                        }

                                        boolean found = false;
                                        for (Adoption ad : adoptions_cc)
                                            if (ad.getPetID() == adoption.getPetID()) found = true;

                                        if (adoption.getStatus() < 1 || adoption.getStatus() > 6)
                                            continue;
                                        else if (adoption.getStatus() != 6)
                                            if (!found)
                                                continue;

                                        adoptions.add(adoption);

                                        //successful adoption
                                        if (adoption.getStatus() > 0){
                                            //record pet
                                            Pet pet = new Pet();
                                            pet.setPetID(ds.getKey());
                                            pet.setOwner(snap.getKey());

                                            boolean foundInPets = false;
                                            for (Pet p : pets){
                                                if (p.getPetID().equals(pet.getPetID()))
                                                    foundInPets = true;
                                            }

                                            if (!foundInPets){
                                                pets.add(pet);
                                                Utility.log("pet added: " + pet.getPetID());

                                                extractPetInfo(pets.size()-1, pet.getPetID(), "type");
                                                extractPetInfo(pets.size()-1, pet.getPetID(), "age");
                                            }



                                            //record user
                                            User user = new User();
                                            user.setUserID(snap.getKey());

                                            boolean foundInUser = false;
                                            for (User u : users){
                                                if (u.getUserID().equals(user.getUserID()))
                                                    foundInUser = true;
                                            }

                                            if (!foundInUser){
                                                users.add(user);

                                                extractUserInfo(users.size()-1, user.getUserID(), "firstName");
                                                extractUserInfo(users.size()-1, user.getUserID(), "lastName");
                                                extractUserInfo(users.size()-1, user.getUserID(), "gender");
                                                extractUserInfo(users.size()-1, user.getUserID(), "birthday");
                                            }


                                        }

                                    }

                                    showRequests(adoptions);
                                }
                                catch (Exception ex){
                                    Utility.log("AdoptionRecords.eAH: " + ex.getMessage());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Utility.log("AdoptionRecords.eAH.oC: " + error.getMessage());
                            }
                        });
                    }

                    loadingDialog.dismissLoadingDialog();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Utility.log("AdoptionRecords.eAH.oC: " + error.getMessage());
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
                            break;
                        case "age":
                            Pet pp = pets.get(index);
                            pp.setAge(inf);
                            pets.set(index, pp);
                            break;
                    }

                    showPetDemo();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Utility.log("AdoptionRecords.ePI.oC: " + error.getMessage());
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

    private BroadcastReceiver mExportDialogReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (EXPORTABLE.size() == 0){
                Toast.makeText(AdoptionRecords.this, "Nothing to export", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<Object[]> entries = new ArrayList<>();

            //labels
            entries.add(new Object[]{"Date", "Status", "PetID", "Pet Type", "Pet Age", "Owner", "Gender", "Birthday", "Actual Adoption Date", "Memo"});

            //sort
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                EXPORTABLE.sort(new Comparator<Adoption>() {
                    @Override
                    public int compare(Adoption a1, Adoption a2) {
                        return a1.getDateRequested().compareTo(a2.getDateRequested());
                    }
                });
            }

            for (Adoption adoption : EXPORTABLE){

                String[] entry = new String[10];
                entry[0] = adoption.getDateRequested();

                switch (adoption.getStatus()){
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5: entry[1] = "PROCESSING"; break;
                    case 6: entry[1] = "SUCCESSFUL"; break;
                }

                if (entry[1] == null)
                    continue;

                entry[2] = String.valueOf(adoption.getPetID());

                for (Pet p : pets){
                    if (p.getPetID().equals(String.valueOf(adoption.getPetID()))){
                        switch (p.getType()){
                            case PetType.DOG: entry[3] = "Dog"; break;
                            case PetType.CAT: entry[3] = "Cat"; break;
                        }

                        switch (p.getAge()){
                            case PetAge.PUPPY: entry[4] = p.getType() == PetType.DOG ? "Puppy" : "Kitten"; break;
                            case PetAge.YOUNG: entry[4] = "Young"; break;
                            case PetAge.OLD: entry[4] = "Old"; break;
                        }

                        for (User u : users){
                            if (p.getOwner().equals(u.getUserID())){
                                entry[5] = u.getFirstName() + " " + u.getLastName();
                                entry[6] = u.getGender() == Gender.MALE ? "Male" : "Female";
                                entry[7] = u.getBirthday();
                            }

                        }

                    }

                }

                entry[8] = adoption.getActualAdoptionDate();
                entry[9] = adoption.getMemo();

                entries.add(entry);
            }

            Spreadsheet spreadsheet = new Spreadsheet(AdoptionRecords.this);
            spreadsheet.setEntries(entries);
            spreadsheet.setTitle("Adoption Records");

            Workbook workbook = spreadsheet.create();

            //export process here
            String exportType = intent.getStringExtra("exportType"); //email or device
            if (exportType.equals("email")){
                spreadsheet.sendAsEmail("Adoption Records");
            }
            else if (exportType.equals("device")){

                String filename = "Adoption Records" + "-" + dateToday() + "-" + timeNow().replace("*", "") + ".xls";
                boolean success = spreadsheet.writeToFile(filename, false);

                if (success)
                    Toast.makeText(AdoptionRecords.this, "Exported to Documents/Silong/"+filename, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(AdoptionRecords.this, "Export failed", Toast.LENGTH_SHORT).show();

            }

        }
    };

    private BroadcastReceiver mReloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                ArrayList<Adoption> tempList = new ArrayList<>();

                for (Adoption adoption : adoptions){
                    if (AdoptionRecords.customDate){

                        String[] fromDate = AdoptionRecords.dateFrom.split("/");
                        Calendar from = Calendar.getInstance();
                        from.set(Integer.valueOf(fromDate[2]),Integer.valueOf(fromDate[0]),Integer.valueOf(fromDate[1]));

                        String[] toDate = AdoptionRecords.dateTo.split("/");
                        Calendar to = Calendar.getInstance();
                        to.set(Integer.valueOf(toDate[2]),Integer.valueOf(toDate[0]),Integer.valueOf(toDate[1]));

                        String[] adDate = adoption.getDateRequested().split("-");
                        Calendar adCal = Calendar.getInstance();
                        adCal.set(Integer.valueOf(adDate[2]),Integer.valueOf(adDate[0]),Integer.valueOf(adDate[1]));

                        if (!adCal.after(to) && !adCal.before(from)){
                            tempList.add(adoption);
                        }
                    }
                }

                showRequests(tempList);

            }
            catch (Exception e){
                Utility.log("AdoptionRecords.mRR: " + e.getMessage());
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReloadReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mExportDialogReceiver);
    }
}