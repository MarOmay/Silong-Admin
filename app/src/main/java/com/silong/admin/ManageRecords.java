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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.silong.Adapter.RecordsAdapter;
import com.silong.CustomView.LoadingDialog;
import com.silong.Object.Pet;
import com.silong.Operation.Utility;

import java.util.Comparator;

public class ManageRecords extends AppCompatActivity {

    ImageView recordsBackIv;
    private SwipeRefreshLayout mrecordRefresher;
    LinearLayout recordsAddTile, recordsCreateReportTile;
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

            int listSize = AdminData.pets.size();

            Pet[] pets = new Pet[listSize];

            for (int i=0; i<listSize; i++){
                pets[i] = AdminData.pets.get(i);
            }

            RecordsAdapter recordsAdapter = new RecordsAdapter(ManageRecords.this, pets);
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