package com.silong.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.CustomView.LoadingDialog;
import com.silong.Object.Pet;
import com.silong.Object.User;
import com.silong.Operation.ImageProcessor;
import com.silong.Operation.Utility;

import java.io.File;
import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    LinearLayout requestsPad, messagesPad, manageRecordsPad, manageAccountsPad;
    MaterialCardView requestsDot;
    TextView adminFnameTv, logoutTv;

    private FirebaseAnalytics mAnalytics;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().hide();

        //Initialize Firebase objects
        mAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        //Receive first name
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mMessageReceiver, new IntentFilter("update-first-name"));

        //Update records
        fetchActiveAccounts();
        fetchActiveRecords();

        for (File file : getFilesDir().listFiles()){
            Log.d("FileInDir", file.getAbsolutePath());
        }

        requestsPad = (LinearLayout) findViewById(R.id.requestsPad);
        requestsDot = (MaterialCardView) findViewById(R.id.requestsDot);
        messagesPad = (LinearLayout) findViewById(R.id.messagesPad);
        manageAccountsPad = (LinearLayout) findViewById(R.id.manageAccountsPad);
        manageRecordsPad = (LinearLayout) findViewById(R.id.manageRecordsPad);

        adminFnameTv = (TextView) findViewById(R.id.adminFnameTv);
        adminFnameTv.setText(AdminData.firstName);

        logoutTv = findViewById(R.id.logoutTv);

        AdminData.populate(this);

    }

    //onPressed Methods

    public void onPressedRequests(View view){
        Intent i = new Intent(Dashboard.this, RequestList.class);
        startActivity(i);
    }

    public void onPressedMessages(View view){
        Intent i = new Intent(Dashboard.this, Messages.class);
        startActivity(i);
    }

    public void onPressedManageAccounts(View view){
        Intent i = new Intent (Dashboard.this, ManageAccount.class);
        startActivity(i);
    }

    public void onPressedManageRecords(View view){
        Intent i = new Intent(Dashboard.this, ManageRecords.class);
        startActivity(i);
    }

    public void onPressedLogout(View view){
        AdminData.logout();
        mAuth.signOut();
        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Dashboard.this, Splash.class);
        startActivity(intent);
        finish();
    }

    //Asynchronous and background activities

    private void fetchActiveAccounts(){
        if (Utility.internetConnection(getApplicationContext())){
            LoadingDialog loadingDialog = new LoadingDialog(Dashboard.this);
            loadingDialog.startLoadingDialog();
            //Get all User accounts
            mReference = mDatabase.getReference("accountSummary");
            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        //Get all User uid
                        ArrayList<String> list = new ArrayList<>();
                        for (DataSnapshot snap : snapshot.getChildren()){

                            File file = new File(getFilesDir(), "account-" + snap.getKey());
                            if (file.exists()){
                                //Check if status of local record matches
                                User tempUser = AdminData.fetchAccountFromLocal(Dashboard.this, snap.getKey());
                                if (tempUser.getAccountStatus() != (Boolean) snap.getValue()){
                                    //delete local record, to rewrite new record
                                    file.delete();
                                    fetchAccountFromCloud(snap.getKey());
                                }
                            }
                            else {
                                fetchAccountFromCloud(snap.getKey());
                            }
                            list.add("account-" + snap.getKey());
                        }
                        //delete local copy of deleted accounts
                        cleanLocalRecord(list, "account-");
                    }
                    catch (Exception e){
                        Log.d("Dashboard", e.getMessage());
                    }
                    AdminData.populateAccounts(Dashboard.this);
                    updateAccountList();
                    loadingDialog.dismissLoadingDialog();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    AdminData.populateAccounts(Dashboard.this);
                    loadingDialog.dismissLoadingDialog();
                }
            });
        }
        else {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
            AdminData.populateAccounts(Dashboard.this);
        }
    }

    private void fetchAccountFromCloud(String uid){
        try{
            //create local copy
            AdminData.writeToLocal(getApplicationContext(), uid, "userID", uid);

            DatabaseReference tempReference = mDatabase.getReference("Users/" + uid);
            tempReference.child("accountStatus").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String status = (Boolean) snapshot.getValue() ? "true" : "false";
                    AdminData.writeToLocal(getApplicationContext(), uid, "accountStatus", status);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("firstName").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String fname = snapshot.getValue().toString();
                    AdminData.writeToLocal(getApplicationContext(), uid, "firstName", fname);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("lastName").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String lname = snapshot.getValue().toString();
                    AdminData.writeToLocal(getApplicationContext(), uid, "lastName", lname);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("email").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String email = snapshot.getValue().toString();
                    AdminData.writeToLocal(getApplicationContext(), uid, "email", email);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("contact").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String contact = snapshot.getValue().toString();
                    AdminData.writeToLocal(getApplicationContext(), uid, "contact", contact);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("birthday").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String birthday = snapshot.getValue().toString();
                    AdminData.writeToLocal(getApplicationContext(), uid, "birthday", birthday);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("gender").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String gender = snapshot.getValue().toString();
                    AdminData.writeToLocal(getApplicationContext(), uid, "gender", gender);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("photo").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String photo = snapshot.getValue().toString();
                    Bitmap bitmap = new ImageProcessor().toBitmap(photo);
                    new ImageProcessor().saveToLocal(getApplicationContext(), bitmap, "avatar-" + uid);
                    AdminData.populateAccounts(Dashboard.this);
                    updateAccountList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        catch (Exception e){
            Log.d("Dashboard", e.getMessage());
        }
    }

    private void updateAccountList(){
        Intent intent = new Intent("update-account-list");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void fetchActiveRecords(){
        if (Utility.internetConnection(getApplicationContext())){
            LoadingDialog loadingDialog = new LoadingDialog(Dashboard.this);
            loadingDialog.startLoadingDialog();
            //Get all User accounts
            mReference = mDatabase.getReference("Pets");
            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        //Get all User uid
                        ArrayList<String> list = new ArrayList<>();
                        for (DataSnapshot snap : snapshot.getChildren()){

                            //skip counter to avoid error
                            if (snap.getKey().equals("counter"))
                                continue;

                            File file = new File(getFilesDir(), "pet-" + snap.getKey());
                            if (file.exists()){
                                //Check if status of local record matches
                                Pet tempPet = AdminData.fetchRecordFromLocal(Dashboard.this, snap.getKey());
                                if (tempPet.getStatus() != Integer.valueOf(snap.getValue().toString())){
                                    //delete local record, to rewrite new record
                                    file.delete();
                                    fetchRecordFromCloud(snap.getKey());
                                }
                            }
                            else {
                                fetchRecordFromCloud(snap.getKey());
                            }
                            list.add("pet-" + snap.getKey());
                        }
                        //delete local copy of deleted accounts
                        cleanLocalRecord(list, "pet-");
                    }
                    catch (Exception e){
                        Log.d("Dashboard", e.getMessage());
                    }
                    AdminData.populateRecords(Dashboard.this);
                    updateRecordList();
                    loadingDialog.dismissLoadingDialog();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    AdminData.populateRecords(Dashboard.this);
                    loadingDialog.dismissLoadingDialog();
                }
            });
        }
        else {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
            AdminData.populateRecords(Dashboard.this);
        }
    }

    private void fetchRecordFromCloud(String id){
        try{
            //create local copy
            AdminData.writePetToLocal(getApplicationContext(), id, "petID", id);

            DatabaseReference tempReference = mDatabase.getReference("Pets/" + id);
            tempReference.child("status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int status = Integer.valueOf(snapshot.getValue().toString());
                    AdminData.writePetToLocal(getApplicationContext(), id, "status", String.valueOf(status));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("type").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int type = Integer.valueOf(snapshot.getValue().toString());
                    AdminData.writePetToLocal(getApplicationContext(), id, "type", String.valueOf(type));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("gender").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int gender = Integer.valueOf(snapshot.getValue().toString());
                    AdminData.writePetToLocal(getApplicationContext(), id, "gender", String.valueOf(gender));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("size").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int size = Integer.valueOf(snapshot.getValue().toString());
                    AdminData.writePetToLocal(getApplicationContext(), id, "size", String.valueOf(size));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("age").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int age = Integer.valueOf(snapshot.getValue().toString());
                    AdminData.writePetToLocal(getApplicationContext(), id, "age", String.valueOf(age));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("color").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String color = snapshot.getValue().toString();
                    AdminData.writePetToLocal(getApplicationContext(), id, "color", String.valueOf(color));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("photo").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String photo = snapshot.getValue().toString();
                    Bitmap bitmap = new ImageProcessor().toBitmap(photo);
                    new ImageProcessor().saveToLocal(getApplicationContext(), bitmap, "petpic-" + id);
                    AdminData.populateRecords(Dashboard.this);
                    updateRecordList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        catch (Exception e){
            Log.d("Dashboard", e.getMessage());
        }
    }

    private void updateRecordList(){
        Intent intent = new Intent("update-record-list");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void cleanLocalRecord(ArrayList<String> list, String prefix){
        File [] files = getFilesDir().listFiles();
        ArrayList<File> accountFiles = new ArrayList<>();

        //filter out non-account files
        for (File file : files){
            if (file.getAbsolutePath().contains(prefix)){
                accountFiles.add(file);
            }
        }

        //filter deleted accounts
        ArrayList<File> deletedAccounts = new ArrayList<>();
        for (File file : accountFiles){
            boolean found = false;
            for (String s : list){
                if (file.getAbsolutePath().contains(s))
                    found = true;
            }
            if (!found)
                deletedAccounts.add(file);
        }

        for (File file : deletedAccounts){
            try {
                file.delete();
            }
            catch (Exception e){
                Log.d("Dashboarc-cLR", e.getMessage());
            }
        }

    }

    //Broadcast Receivers

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            adminFnameTv.setText(message);
        }
    };

    //Method Overriding

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
}