package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class Dashboard extends AppCompatActivity {

    LinearLayout requestsPad, messagesPad, manageRecordsPad, manageAccountsPad;
    MaterialCardView requestsDot;
    TextView adminFnameTv, logoutTv;

    private FirebaseAnalytics mAnalytics;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().hide();

        //Initialize Firebase objects
        mAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();

        //Receive first name
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mMessageReceiver, new IntentFilter("update-first-name"));

        requestsPad = (LinearLayout) findViewById(R.id.requestsPad);
        requestsDot = (MaterialCardView) findViewById(R.id.requestsDot);
        messagesPad = (LinearLayout) findViewById(R.id.messagesPad);
        manageAccountsPad = (LinearLayout) findViewById(R.id.manageAccountsPad);
        manageRecordsPad = (LinearLayout) findViewById(R.id.manageRecordsPad);

        adminFnameTv = (TextView) findViewById(R.id.adminFnameTv);
        adminFnameTv.setText(AdminData.firstName);

        logoutTv = findViewById(R.id.logoutTv);
        logoutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        requestsPad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Dashboard.this, RequestList.class);
                startActivity(i);
            }
        });

        messagesPad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Dashboard.this, Messages.class);
                startActivity(i);
            }
        });

        manageAccountsPad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent (Dashboard.this, ManageAccount.class);
                startActivity(i);
            }
        });

        manageRecordsPad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Dashboard.this, ManageRecords.class);
                startActivity(i);
            }
        });

        AdminData.populate(this);

    }

    private void logout(){
        AdminData.logout();
        mAuth.signOut();
        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Dashboard.this, Splash.class);
        startActivity(intent);
        finish();
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            adminFnameTv.setText(message);
        }
    };

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