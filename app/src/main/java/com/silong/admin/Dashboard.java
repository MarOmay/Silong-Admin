package com.silong.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
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
import com.silong.Service.RequestWatcher;
import com.silong.Task.AccountsChecker;
import com.silong.Task.ActivationRequestFetcher;
import com.silong.Task.AdoptionRequestFetcher;
import com.silong.Task.AdoptionScheduleFetcher;
import com.silong.Task.RecordsChecker;

import java.io.File;
import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    LinearLayout requestsPad, messagesPad, manageRecordsPad, manageAccountsPad;
    MaterialCardView requestsDot, messagesDot;
    TextView adminFnameTv, logoutTv;

    private FirebaseAnalytics mAnalytics;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().hide();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.darkerNighty));
        }

        //Initialize Firebase objects
        mAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        //register receivers
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("update-first-name"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mAccountsChecker, new IntentFilter("AC-done"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRecordsChecker, new IntentFilter("RC-done"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRequestsNotify, new IntentFilter("ARF-sb-notify"));

        loadingDialog = new LoadingDialog(Dashboard.this);

        //Update local copies
        startSync();

        for (File file : getFilesDir().listFiles()){
            Log.d("FileInDir", file.getAbsolutePath());
        }

        requestsPad = (LinearLayout) findViewById(R.id.requestsPad);
        requestsDot = (MaterialCardView) findViewById(R.id.requestsDot);
        requestsDot.setVisibility(View.INVISIBLE);

        messagesPad = (LinearLayout) findViewById(R.id.messagesPad);
        messagesDot = (MaterialCardView) findViewById(R.id.messagesDot);
        messagesDot.setVisibility(View.INVISIBLE);

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
        finish();
    }

    public void onPressedMessages(View view){
        Intent i = new Intent(Dashboard.this, Messages.class);
        startActivity(i);
        finish();
    }

    public void onPressedManageAccounts(View view){
        Intent i = new Intent (Dashboard.this, ManageAccount.class);
        startActivity(i);
        finish();
    }

    public void onPressedManageRecords(View view){
        Intent i = new Intent(Dashboard.this, ManageRecords.class);
        startActivity(i);
        finish();
    }

    public void onPressedLogout(View view){
        stopService(new Intent(this, RequestWatcher.class));
        AdminData.logout();
        mAuth.signOut();
        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Dashboard.this, Splash.class);
        startActivity(intent);
        finish();
    }

    //Asynchronous and background activities

    private void startSync(){
        if (Utility.internetConnection(getApplicationContext())){

            loadingDialog.startLoadingDialog();

            //sync account activation requests
            AdminData.requests = new ArrayList<>();
            ActivationRequestFetcher activationRequestFetcher = new ActivationRequestFetcher(Dashboard.this);
            activationRequestFetcher.execute();

            //sync adoption request
            AdoptionRequestFetcher adoptionRequestFetcher = new AdoptionRequestFetcher(Dashboard.this);
            adoptionRequestFetcher.execute();

            //sync adoption appointment
            AdoptionScheduleFetcher adoptionScheduleFetcher = new AdoptionScheduleFetcher(Dashboard.this);
            adoptionScheduleFetcher.execute();

            //sync account copies
            AccountsChecker accountsChecker = new AccountsChecker(Dashboard.this);
            accountsChecker.execute();

            //sync account copies
            RecordsChecker recordsChecker = new RecordsChecker(Dashboard.this);
            recordsChecker.execute();

        }
        else {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
            AdminData.populateAccounts(Dashboard.this);
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

    private BroadcastReceiver mAccountsChecker = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadingDialog.dismissLoadingDialog();
        }
    };

    private BroadcastReceiver mRecordsChecker = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadingDialog.dismissLoadingDialog();
        }
    };

    private BroadcastReceiver mRequestsNotify = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                boolean notify = intent.getBooleanExtra("notify", false);
                requestsDot.setVisibility( notify ? View.VISIBLE : View.INVISIBLE);
            }
            catch (Exception e){
                Log.d("Dashboard-mRN", e.getMessage());
            }
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mAccountsChecker);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRecordsChecker);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRequestsNotify);
        super.onDestroy();
    }
}