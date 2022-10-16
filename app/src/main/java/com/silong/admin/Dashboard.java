package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.silong.CustomView.LoadingDialog;
import com.silong.Operation.Utility;
import com.silong.Task.AccountsChecker;
import com.silong.Task.ActivationRequestFetcher;
import com.silong.Task.AdoptionRequestFetcher;
import com.silong.Task.AdoptionScheduleFetcher;
import com.silong.Task.AppointmentFetcher;
import com.silong.Task.RecordsChecker;

import java.io.File;
import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    LinearLayout requestsPad, appointmentsPad, manageRecordsPad, manageAccountsPad;
    MaterialCardView requestsDot, appointmentsDot;
    TextView adminFnameTv, logoutTv;

    private LoadingDialog loadingDialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().hide();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.darkerNighty));
        }

        //initialize Firebase objects
        mAuth = FirebaseAuth.getInstance();

        //register receivers
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("update-first-name"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mAccountsChecker, new IntentFilter("AC-done"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRecordsChecker, new IntentFilter("RC-done"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRequestsNotify, new IntentFilter("ARF-sb-notify"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mAppointmentNotify, new IntentFilter("AF-sb-notify"));

        loadingDialog = new LoadingDialog(Dashboard.this);

        AdminData.populateRecords(this);

        for (File file : getFilesDir().listFiles()){
            Log.d("FileInDir", file.getAbsolutePath());
        }

        requestsPad = (LinearLayout) findViewById(R.id.requestsPad);
        requestsDot = (MaterialCardView) findViewById(R.id.requestsDot);
        requestsDot.setVisibility(View.INVISIBLE);

        appointmentsPad = (LinearLayout) findViewById(R.id.appointmentsPad);
        appointmentsDot = (MaterialCardView) findViewById(R.id.appointmentsDot);
        appointmentsDot.setVisibility(View.INVISIBLE);

        manageAccountsPad = (LinearLayout) findViewById(R.id.manageAccountsPad);
        manageRecordsPad = (LinearLayout) findViewById(R.id.manageRecordsPad);

        adminFnameTv = (TextView) findViewById(R.id.adminFnameTv);
        adminFnameTv.setText(AdminData.firstName);

        logoutTv = findViewById(R.id.logoutTv);

        AdminData.populate(this);

        //Update local copies
        startSync();

    }

    //onPressed Methods

    public void onPressedSettings(View view){
        Utility.animateOnClick(this, view);
        Intent i = new Intent(Dashboard.this, AboutTheOffice.class);
        startActivity(i);
    }

    public void onPressedRequests(View view){
        Utility.animateOnClick(this, view);
        Intent i = new Intent(Dashboard.this, RequestList.class);
        startActivity(i);
        finish();
    }

    public void onPressedAppointment(View view){
        Utility.animateOnClick(this, view);
        Intent intent = new Intent(Dashboard.this, AppointmentsList.class);
        startActivity(intent);
        finish();
    }

    public void onPressedManageAccounts(View view){
        Utility.animateOnClick(this, view);
        Intent i = new Intent (Dashboard.this, ManageAccount.class);
        startActivity(i);
        finish();
    }

    public void onPressedManageRecords(View view){
        Utility.animateOnClick(this, view);
        Intent i = new Intent(Dashboard.this, ManageRecords.class);
        startActivity(i);
        finish();
    }

    public void onPressedLogout(View view){
        Utility.animateOnClick(this, view);
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

            //sync appointments
            AppointmentFetcher appointmentFetcher = new AppointmentFetcher(Dashboard.this);
            appointmentFetcher.execute();

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

    private BroadcastReceiver mAppointmentNotify = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                boolean notify = intent.getBooleanExtra("notify", false);
                appointmentsDot.setVisibility( notify ? View.VISIBLE : View.INVISIBLE);
            }
            catch (Exception e){
                Log.d("Dashboard-mAN", e.getMessage());
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mAppointmentNotify);
        super.onDestroy();
    }
}