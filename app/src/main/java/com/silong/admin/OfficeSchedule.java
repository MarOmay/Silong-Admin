package com.silong.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.silong.CustomView.LoadingDialog;
import com.silong.CustomView.OfficeTimeAdjustmentDialog;
import com.silong.CustomView.TimeRangeFromPicker;
import com.silong.CustomView.TimeRangeToPicker;
import com.silong.Operation.Utility;

import java.util.HashMap;

import java.util.Map;

public class OfficeSchedule extends AppCompatActivity {

    private TextView officeTimeTv;
    MaterialCheckBox monCb, tueCb, wedCb, thursCb, friCb;

    private FirebaseDatabase mDatabase;

    private String timeFrom = "09:00 AM";
    private String timeTo = "04:00 PM";
    private boolean MONDAY = false;
    private boolean TUESDAY = false;
    private boolean WEDNESDAY = false;
    private boolean THURSDAY = false;
    private boolean FRIDAY = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_schedule);
        getSupportActionBar().hide();

        //initialize Firebase objects
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        //register receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mTimeChangeReceiver, new IntentFilter("update-time"));

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        officeTimeTv = findViewById(R.id.officeTimeTv);
        monCb = findViewById(R.id.monCb);
        tueCb = findViewById(R.id.tueCb);
        wedCb = findViewById(R.id.wedCb);
        thursCb = findViewById(R.id.thursCb);
        friCb = findViewById(R.id.friCb);

        //get schedule from cloud
        fetchScheduleFromCloud();
    }

    public void onPressedAdjustTime(View view){
        OfficeTimeAdjustmentDialog otad = new OfficeTimeAdjustmentDialog(OfficeSchedule.this, timeFrom, timeTo);

        otad.officeTimeFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeRangeFromPicker trfp = new TimeRangeFromPicker(OfficeSchedule.this, otad);
                trfp.show(getSupportFragmentManager(), null);
            }
        });

        otad.officeTimeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeRangeToPicker trfp = new TimeRangeToPicker(OfficeSchedule.this, otad);
                trfp.show(getSupportFragmentManager(), null);
            }
        });

        otad.show();

    }

    public void onPressedCheckBox(View view){
        if (!Utility.internetConnection(OfficeSchedule.this)){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            updateUI();
            return;
        }

        try {

            updateSchedule(true);

        }
        catch (Exception e){
            Utility.log("OfficeSchedule.oPCB: " + e.getMessage());
        }
    }

    private void updateUI(){
        try {
            officeTimeTv.setText(timeFrom + " - " + timeTo);
            monCb.setChecked(MONDAY);
            tueCb.setChecked(TUESDAY);
            wedCb.setChecked(WEDNESDAY);
            thursCb.setChecked(THURSDAY);
            friCb.setChecked(FRIDAY);
        }
        catch (Exception e){
            Utility.log("OfficeSchedule.uUI: " + e.getMessage());
        }
    }

    private void fetchScheduleFromCloud(){
        if (!Utility.internetConnection(OfficeSchedule.this)){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        LoadingDialog loadingDialog = new LoadingDialog(OfficeSchedule.this);
        loadingDialog.startLoadingDialog();

        try {

            DatabaseReference mRef = mDatabase.getReference("publicInformation").child("officeSchedule");
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    int ctr = 0;
                    for (DataSnapshot snap : snapshot.getChildren()){
                        ctr++;

                        switch (snap.getKey()){
                            case "timeFrom": timeFrom = snap.getValue().toString(); break;
                            case "timeTo": timeTo = snap.getValue().toString(); break;
                            case "monday": MONDAY = (boolean) snap.getValue(); break;
                            case "tuesday": TUESDAY = (boolean) snap.getValue(); break;
                            case "wednesday": WEDNESDAY = (boolean) snap.getValue(); break;
                            case "thursday": THURSDAY = (boolean) snap.getValue(); break;
                            case "friday": FRIDAY = (boolean) snap.getValue(); break;
                        }
                    }

                    //check if there's schedule in rtdb
                    if (ctr == 0){
                        updateSchedule(false);
                    }

                    updateUI();

                    loadingDialog.dismissLoadingDialog();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Utility.log("OfficeSchedule.fSFC.oC: " + error.getMessage());
                    loadingDialog.dismissLoadingDialog();
                }
            });

        }
        catch (Exception e){
            Utility.log("OfficeSchedule.fSFC: " + e.getMessage());
            loadingDialog.dismissLoadingDialog();
        }
    }

    public void updateSchedule(boolean getFromUI){
        LoadingDialog loadingDialog = new LoadingDialog(OfficeSchedule.this);
        loadingDialog.startLoadingDialog();

        boolean monday = monCb.isChecked();
        boolean tuesday = tueCb.isChecked();
        boolean wednesday = wedCb.isChecked();
        boolean thursday = thursCb.isChecked();
        boolean friday = friCb.isChecked();

        Map<String, Object> map = new HashMap<>();
        map.put("timeFrom", timeFrom);
        map.put("timeTo", timeTo);
        map.put("monday", getFromUI ? monday : MONDAY);
        map.put("tuesday", getFromUI ? tuesday : TUESDAY);
        map.put("wednesday", getFromUI ? wednesday : WEDNESDAY);
        map.put("thursday", getFromUI ? thursday :  THURSDAY);
        map.put("friday", getFromUI ? friday :  FRIDAY);

        try {

            DatabaseReference mRef = mDatabase.getReference("publicInformation").child("officeSchedule");
            mRef.updateChildren(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            if (getFromUI){
                                MONDAY = monday;
                                TUESDAY = tuesday;
                                WEDNESDAY = wednesday;
                                THURSDAY = thursday;
                                FRIDAY = friday;
                            }

                            Utility.dbLog("Updated schedule.");
                            Toast.makeText(OfficeSchedule.this, "Schedule updated!", Toast.LENGTH_SHORT).show();
                            updateUI();
                            loadingDialog.dismissLoadingDialog();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(OfficeSchedule.this, "Can't update schedule. Try again.", Toast.LENGTH_SHORT).show();
                            Utility.log("OfficeSchedule.uS.oF: " + e.getMessage());
                            loadingDialog.dismissLoadingDialog();
                        }
                    });

        }
        catch (Exception e){
            Toast.makeText(this, "Can't update schedule. Try again.", Toast.LENGTH_SHORT).show();
            Utility.log("OfficeSchedule.uS: " + e.getMessage());
            loadingDialog.dismissLoadingDialog();
        }
    }


    private BroadcastReceiver mTimeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                String from = intent.getStringExtra("timeFrom");
                String to = intent.getStringExtra("timeTo");

                if (from.equals(timeFrom) && to.equals(timeTo)){
                    Toast.makeText(getApplicationContext(), "No changes made", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    timeFrom = from;
                    timeTo = to;
                    updateUI();
                    updateSchedule(false);
                }
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Can't process request", Toast.LENGTH_SHORT).show();
                Utility.log("OfficeSchedule.mTCR: " + e.getMessage());
            }
        }
    };

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mTimeChangeReceiver);
        super.onDestroy();
    }
}