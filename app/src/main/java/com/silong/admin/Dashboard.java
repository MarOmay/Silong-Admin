package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

    private SwipeRefreshLayout dashboardRefresher;
    LinearLayout requestsPad, appointmentsPad, manageRecordsPad, manageAccountsPad;
    MaterialCardView requestsDot, appointmentsDot;
    TextView adminFnameTv, logoutTv;

    private FirebaseAuth mAuth;

    public static boolean actReqDone = false, adopReqDone = false, adopSchedDone = false,
                    appointReqDone = false, actCheckDone = false, recCheckDone = false;

    public static LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().hide();

        actReqDone = false;
        adopReqDone = false;
        adopSchedDone = false;
        appointReqDone = false;
        actCheckDone = false;
        recCheckDone = false;

        loadingDialog = new LoadingDialog(this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.darkerNighty));
        }

        //initialize Firebase objects
        mAuth = FirebaseAuth.getInstance();

        //register receivers
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("update-first-name"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRequestsNotify, new IntentFilter("ARF-sb-notify"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mAppointmentNotify, new IntentFilter("AF-sb-notify"));

        AdminData.populateRecords(this);

        for (File file : getFilesDir().listFiles()){
            Utility.log("Dashboard.oC- FileInDir:" + file.getAbsolutePath());
        }

        dashboardRefresher = findViewById(R.id.dashboardRefresher);

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

        dashboardRefresher.setOnRefreshListener(refreshListener);

    }

    //onPressed Methods

    public void onPressedSettings(View view){
        Utility.animateOnClick(this, view);
        Intent i = new Intent(Dashboard.this, AboutTheOffice.class);
        startActivity(i);
    }

    public void onPressedRequests(View view){
        Utility.animateOnClick(this, view);
        new Utility().checkPermission(Dashboard.this, "manageRequests", AdminData.adminID, true);
    }

    public void onPressedAppointment(View view){
        Utility.animateOnClick(this, view);
        new Utility().checkPermission(Dashboard.this, "appointments", AdminData.adminID, true);
    }

    public void onPressedManageAccounts(View view){
        Utility.animateOnClick(this, view);
        Intent i = new Intent (Dashboard.this, ManageAccount.class);
        startActivity(i);
        finish();
    }

    public void onPressedManageRecords(View view){
        Utility.animateOnClick(this, view);
        new Utility().checkPermission(Dashboard.this, "manageRecords", AdminData.adminID, true);
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

    public static void checkCompletion(){

        if (actReqDone && adopReqDone && adopSchedDone &&
                appointReqDone && actCheckDone && recCheckDone){
            loadingDialog.dismissLoadingDialog();
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

    private BroadcastReceiver mRequestsNotify = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                boolean notify = intent.getBooleanExtra("notify", false);
                requestsDot.setVisibility( notify ? View.VISIBLE : View.INVISIBLE);
            }
            catch (Exception e){
                Utility.log("Dashboard.mRN: " + e.getMessage());
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
                Utility.log("Dashboard.mAN: " + e.getMessage());
            }
        }
    };

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            startSync();
            dashboardRefresher.setRefreshing(false);
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRequestsNotify);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mAppointmentNotify);
        super.onDestroy();
    }
}