package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.silong.Adapter.RecordsAdapter;
import com.silong.CustomView.LoadingDialog;
import com.silong.EnumClass.Gender;
import com.silong.EnumClass.PetAge;
import com.silong.EnumClass.PetType;
import com.silong.Object.Pet;
import com.silong.Operation.Utility;

import java.util.ArrayList;
import java.util.Comparator;

public class ManageRecords extends AppCompatActivity {

    private boolean fDog = true, fCat = true;
    private boolean fMale = true, fFemale = true;
    private boolean fPuppy = true, fYoung = true, fOld = true;

    ImageView recordsBackIv, filterIv;
    private SwipeRefreshLayout mrecordRefresher;
    LinearLayout recordsAddTile, recordsCreateReportTile, filterLl;
    RecyclerView recordsRecycler;
    Spinner filterTypeSp, filterGenderSp, filterAgeSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_records);
        getSupportActionBar().hide();

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        //Register receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mShowPet, new IntentFilter("show-selected-pet"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mUpdateList, new IntentFilter("update-record-list"));

        mrecordRefresher = findViewById(R.id.mrecordRefresher);
        recordsBackIv = (ImageView) findViewById(R.id.recordsBackIv);
        recordsAddTile = (LinearLayout) findViewById(R.id.recordsAddTile);
        recordsCreateReportTile = (LinearLayout) findViewById(R.id.recordsCreateReportTile);
        recordsRecycler = (RecyclerView) findViewById(R.id.recordsRecycler);
        filterIv = findViewById(R.id.filterIv);
        filterLl = findViewById(R.id.filterLl);
        filterTypeSp = findViewById(R.id.filterTypeSp);
        filterGenderSp = findViewById(R.id.filterGenderSp);
        filterAgeSp = findViewById(R.id.filterAgeSp);

        recordsRecycler.setHasFixedSize(true);
        recordsRecycler.setLayoutManager(new LinearLayoutManager(ManageRecords.this));

        AdminData.populateRecords(this);
        AdminData.populate(this);
        AdminData.requests.clear();
        AdminData.appointments.clear();
        AdminData.users.clear();

        loadRecordList();

        mrecordRefresher.setOnRefreshListener(refreshListener);

        //filter spinner temp adapter
        prepareFilterLayout();
        filterLl.setVisibility(View.GONE);
    }

    public void onPressedFilter(View view){
        if (filterLl.getVisibility() == View.GONE){
            filterLl.setVisibility(View.VISIBLE);
            //reset filter
            filterTypeSp.setSelection(0);
            filterGenderSp.setSelection(0);
            filterAgeSp.setSelection(0);
        }
        else {
            filterLl.setVisibility(View.GONE);
            //show all
            fDog = true;
            fCat = true;
            fMale = true;
            fFemale = true;
            fPuppy = true;
            fYoung = true;
            fOld = true;
            loadRecordList();
        }
    }

    public void onPressedAddRecord(View view){
        if (Utility.internetConnection(getApplicationContext())){
            if (AdminData.DATABASE_MAINTENANCE){
                Toast.makeText(this, "This feature is disabled until maintenance is done.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(ManageRecords.this, AddRecord.class);
            startActivity(i);
        }
        else {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onPressedCreateReports(View view){
        new Utility().checkPermission(ManageRecords.this, "manageReports", AdminData.adminID, false);
    }

    public void loadRecordList(){
        LoadingDialog loadingDialog = new LoadingDialog(ManageRecords.this);
        loadingDialog.startLoadingDialog();

        try {

            ArrayList<Pet> filtered = filterList();

            //sort
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                filtered.sort(new Comparator<Pet>() {
                    @Override
                    public int compare(Pet pet, Pet t1) {
                        int id1 = Integer.parseInt(pet.getPetID());
                        int id2 = Integer.parseInt(t1.getPetID());
                        return ((Integer) id1).compareTo(((Integer) id2));
                    }
                });
            }

            int listSize = filtered.size();

            Pet[] pets = new Pet[listSize];

            for (int i=0; i<listSize; i++){
                pets[i] = filtered.get(i);
            }

            RecordsAdapter recordsAdapter = new RecordsAdapter(ManageRecords.this, pets);
            recordsRecycler.setAdapter(recordsAdapter);
        }
        catch (Exception e){
            Utility.log("ManageRecord.lRL: " + e.getMessage());
        }

        loadingDialog.dismissLoadingDialog();
    }

    private ArrayList<Pet> filterList(){
        ArrayList<Pet> filteredList = new ArrayList<>();

        for (Pet pet : AdminData.pets){

            if (pet.getType() == PetType.DOG && !fDog)
                continue;
            else if (pet.getType() == PetType.CAT && !fCat)
                continue;

            if (pet.getGender() == Gender.MALE && !fMale)
                continue;
            else if (pet.getGender() == Gender.FEMALE && !fFemale)
                continue;

            if (!fPuppy && pet.getAge() == PetAge.PUPPY)
                continue;
            if (!fYoung && pet.getAge() == PetAge.YOUNG)
                continue;
            if (!fOld && pet.getAge() == PetAge.OLD)
                continue;

            filteredList.add(pet);
        }

        return filteredList;
    }

    private void prepareFilterLayout(){
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.type, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterTypeSp.setAdapter(typeAdapter);

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterGenderSp.setAdapter(genderAdapter);

        ArrayAdapter<CharSequence> ageAdapter = ArrayAdapter.createFromResource(this,
                R.array.age, android.R.layout.simple_spinner_item);
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterAgeSp.setAdapter(ageAdapter);

        //onItemSelected
        filterTypeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = filterTypeSp.getSelectedItem().toString();
                switch (selected.toLowerCase()){
                    case "all":
                        fDog = true;
                        fCat = true;
                        break;
                    case "dog":
                        fDog = true;
                        fCat = false;
                        break;
                    case "cat":
                        fDog = false;
                        fCat = true;
                        break;
                }
                loadRecordList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        filterGenderSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = filterGenderSp.getSelectedItem().toString();
                switch (selected.toLowerCase()){
                    case "all":
                        fMale = true;
                        fFemale = true;
                        break;
                    case "male":
                        fMale = true;
                        fFemale = false;
                        break;
                    case "female":
                        fMale = false;
                        fFemale = true;
                        break;
                }
                loadRecordList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        filterAgeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = filterAgeSp.getSelectedItem().toString();
                switch (selected.toLowerCase()){
                    case "all":
                        fPuppy = true;
                        fYoung = true;
                        fOld = true;
                        break;
                    case "puppy/kitten":
                        fPuppy = true;
                        fYoung = false;
                        fOld = false;
                        break;
                    case "young":
                        fPuppy = false;
                        fYoung = true;
                        fOld = false;
                        break;
                    case "adult":
                        fPuppy = false;
                        fYoung = false;
                        fOld = true;
                        break;
                }
                loadRecordList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void back(View view){
        onBackPressed();
    }

    private BroadcastReceiver mUpdateList = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadRecordList();
        }
    };

    private BroadcastReceiver mShowPet = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String id = intent.getStringExtra("id");
            Intent i = new Intent(ManageRecords.this, AddRecord.class);
            i.putExtra("id", id);
            startActivity(i);
            finish();
        }
    };

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadRecordList();
            mrecordRefresher.setRefreshing(false);
        }
    };

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ManageRecords.this, Dashboard.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mUpdateList);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mShowPet);
    }
}