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
import com.silong.Object.User;
import com.silong.Operation.ImageProcessor;

import java.io.File;

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
        LoadingDialog loadingDialog = new LoadingDialog(Dashboard.this);
        loadingDialog.startLoadingDialog();
        //Get all User accounts
        mReference = mDatabase.getReference("accountSummary");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    //Get all User uid
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

                    }
                }
                catch (Exception e){
                    Log.d("Dashboard", e.getMessage());
                }
                AdminData.populateAccounts(Dashboard.this);
                loadingDialog.dismissLoadingDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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