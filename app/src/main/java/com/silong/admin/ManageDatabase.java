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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.CustomView.DatabaseWarningDialog;
import com.silong.CustomView.LoadingDialog;
import com.silong.CustomView.ReminderDialog;
import com.silong.Operation.Utility;
import com.silong.Task.ManageLogsReminder;

public class ManageDatabase extends AppCompatActivity {

    private MaterialCheckBox pastLogsCb, deletedUsersCb;

    private boolean logsDone = false, usersDone = false;

    private FirebaseDatabase mDatabase;

    private LoadingDialog loadingDialog = new LoadingDialog(ManageDatabase.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_database);
        getSupportActionBar().hide();

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        //initialize Firebase objects
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        //registered receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mDeleteReceiver, new IntentFilter("delete-authorized"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mSetReminderReceiver, new IntentFilter("reminder-set"));

        pastLogsCb = findViewById(R.id.pastLogsCb);
        deletedUsersCb = findViewById(R.id.deletedUsersCb);
    }

    public void onPressedReminder(View view){
        ReminderDialog reminderDialog = new ReminderDialog(ManageDatabase.this, getSupportFragmentManager());
        reminderDialog.show();
    }

    public void onPressedDeleteDb(View view){
        if (!Utility.internetConnection(ManageDatabase.this)){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pastLogsCb.isChecked() && !deletedUsersCb.isChecked()){
            Toast.makeText(getApplicationContext(), "Nothing to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseWarningDialog databaseWarningDialog = new DatabaseWarningDialog(ManageDatabase.this);
        databaseWarningDialog.show();
    }

    private void deleteLogs(){
        try {
            DatabaseReference mRef = mDatabase.getReference("adminLogs");
            mRef.setValue(null)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            logsDone = true;
                            checkCompletion();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ManageDatabase.this, "Unable to delete logs.", Toast.LENGTH_SHORT).show();
                            logsDone = true;
                            checkCompletion();
                            Utility.log("ManageDatabase.dL.oF: " + e.getMessage());
                        }
                    });
        }
        catch (Exception e){
            Toast.makeText(ManageDatabase.this, "Unable to delete logs.", Toast.LENGTH_SHORT).show();
            logsDone = true;
            checkCompletion();
            Utility.log("ManageDatabase.dL: " + e.getMessage());
        }
    }

    private void deleteUsers(){
        try {
            DatabaseReference mRef = mDatabase.getReference("accountSummary");
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot snap : snapshot.getChildren()){
                        String status = snap.getValue().toString();
                        if (status.equals("deleted")){
                            //delete from Users/
                            mDatabase.getReference("Users").child(snap.getKey()).setValue(null);
                            //delete from accountSummary/
                            mDatabase.getReference("accountSummary").child(snap.getKey()).setValue(null);
                        }
                    }

                    usersDone = true;
                    checkCompletion();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ManageDatabase.this, "Unable to delete users.", Toast.LENGTH_SHORT).show();
                    usersDone = true;
                    checkCompletion();
                    Utility.log("ManageDatabase.dU: " + error.getMessage());
                }
            });
        }
        catch (Exception e){
            Toast.makeText(ManageDatabase.this, "Unable to delete users.", Toast.LENGTH_SHORT).show();
            usersDone = true;
            checkCompletion();
            Utility.log("ManageDatabase.dU: " + e.getMessage());
        }
    }

    private void checkCompletion(){
        if (logsDone && usersDone){
            Toast.makeText(ManageDatabase.this, "Data deleted successfully", Toast.LENGTH_SHORT).show();

            if (pastLogsCb.isChecked() && deletedUsersCb.isChecked())
                Utility.dbLog("Cleared logs and deleted User Accounts.");
            else if (deletedUsersCb.isChecked())
                Utility.dbLog("Cleared deleted User Accounts.");
            else if (pastLogsCb.isChecked())
                Utility.dbLog("Cleared logs.");

            pastLogsCb.setChecked(false);
            deletedUsersCb.setChecked(false);
            loadingDialog.dismissLoadingDialog();
        }
    }

    private BroadcastReceiver mSetReminderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LoadingDialog loadingDialog = new LoadingDialog(ManageDatabase.this);
            loadingDialog.startLoadingDialog();

            try {
                String date = intent.getStringExtra("date");
                DatabaseReference mRef = mDatabase.getReference("publicInformation").child("databaseMaintenanceSchedule");
                mRef.setValue(date)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ManageDatabase.this, "Reminder set successfully", Toast.LENGTH_SHORT).show();
                                ManageLogsReminder manageLogsReminder = new ManageLogsReminder(ManageDatabase.this);
                                manageLogsReminder.execute();
                                loadingDialog.dismissLoadingDialog();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ManageDatabase.this, "Failed to update database", Toast.LENGTH_SHORT).show();
                                Utility.log("ManageDatabase.mSRR: " + e.getMessage());
                                loadingDialog.dismissLoadingDialog();
                            }
                        });
            }
            catch (Exception e){
                Toast.makeText(ManageDatabase.this, "Operation failed", Toast.LENGTH_SHORT).show();
                Utility.log("ManageDatabase.mSRR: " + e.getMessage());
                loadingDialog.dismissLoadingDialog();
            }
        }
    };

    private BroadcastReceiver mDeleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                loadingDialog.startLoadingDialog();

                if (pastLogsCb.isChecked())
                    deleteLogs();
                else
                    logsDone = true;

                if (deletedUsersCb.isChecked())
                    deleteUsers();
                else
                    usersDone = true;

            }
            catch (Exception e){
                Toast.makeText(ManageDatabase.this, "Can't process request", Toast.LENGTH_SHORT).show();
                Utility.log("ManageDatabase.mDR: " + e.getMessage());
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDeleteReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSetReminderReceiver);
        super.onDestroy();
    }
}