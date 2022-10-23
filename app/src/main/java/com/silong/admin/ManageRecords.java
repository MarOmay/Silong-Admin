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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.silong.Adapter.RecordsAdapter;
import com.silong.CustomView.LoadingDialog;
import com.silong.EnumClass.Gender;
import com.silong.EnumClass.PetAge;
import com.silong.EnumClass.PetColor;
import com.silong.EnumClass.PetSize;
import com.silong.EnumClass.PetType;
import com.silong.Object.Pet;
import com.silong.Object.PetRecordsData;
import com.silong.Operation.Utility;

import java.util.Comparator;

public class ManageRecords extends AppCompatActivity {

    ImageView recordsBackIv;
    private SwipeRefreshLayout mrecordRefresher;
    LinearLayout recordsAddTile, recordsCreateReportTile;
    RecyclerView recordsRecycler;

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

        recordsRecycler.setHasFixedSize(true);
        recordsRecycler.setLayoutManager(new LinearLayoutManager(ManageRecords.this));

        AdminData.populateRecords(this);
        AdminData.populate(this);
        AdminData.requests.clear();
        AdminData.appointments.clear();
        AdminData.users.clear();

        loadRecordList();

        mrecordRefresher.setOnRefreshListener(refreshListener);
    }

    public void onPressedAddRecord(View view){
        if (Utility.internetConnection(getApplicationContext())){
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

            //sort
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                AdminData.pets.sort(new Comparator<Pet>() {
                    @Override
                    public int compare(Pet pet, Pet t1) {
                        int id1 = Integer.parseInt(pet.getPetID());
                        int id2 = Integer.parseInt(t1.getPetID());
                        return ((Integer) id1).compareTo(((Integer) id2));
                    }
                });
            }

            PetRecordsData[] recordsData = new PetRecordsData[AdminData.pets.size()];

            for (Pet pet : AdminData.pets){
                //translate gender and type
                String genderType = "";
                switch (pet.getGender()){
                    case Gender.MALE: genderType = "Male"; break;
                    case Gender.FEMALE: genderType = "Female"; break;
                }
                switch (pet.getType()){
                    case PetType.DOG: genderType += " Dog"; break;
                    case PetType.CAT: genderType += " Cat"; break;
                }

                //translate age
                String age = "";
                switch (pet.getAge()){
                    case PetAge.PUPPY: age = (pet.getType() == PetType.DOG ? "Puppy" : "Kitten"); break;
                    case PetAge.YOUNG: age = "Young"; break;
                    case PetAge.OLD: age = "Old"; break;
                }

                //translate color
                String color = "";
                for (char c : pet.getColor().toCharArray()){
                    switch (Integer.parseInt(c+"")){
                        case PetColor.BLACK: color += "Black "; break;
                        case PetColor.BROWN: color += "Brown "; break;
                        case PetColor.CREAM: color += "Cream "; break;
                        case PetColor.WHITE: color += "White "; break;
                        case PetColor.ORANGE: color += "Orange "; break;
                        case PetColor.GRAY: color += "Gray "; break;
                    }
                }
                color.trim();
                color.replace(" ", " / ");

                //translate size
                String size = "";
                switch (pet.getSize()){
                    case PetSize.SMALL: size = "Small"; break;
                    case PetSize.MEDIUM: size = "Medium"; break;
                    case PetSize.LARGE: size = "Large"; break;
                }

                recordsData[AdminData.pets.indexOf(pet)] = new PetRecordsData(pet.getPetID(), genderType, age, color, size, pet.getPhoto());
            }

            RecordsAdapter recordsAdapter = new RecordsAdapter(recordsData, ManageRecords.this);
            recordsRecycler.setAdapter(recordsAdapter);
        }
        catch (Exception e){
            Utility.log("ManageRecord.lRL: " + e.getMessage());
        }

        loadingDialog.dismissLoadingDialog();
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