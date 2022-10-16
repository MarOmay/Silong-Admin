package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.database.FirebaseDatabase;

public class AdminRoles extends AppCompatActivity {

    MaterialCheckBox manageReqCb, appointmentsCb, manageRecCb, manageRepCb, editAgreeCb, editContactCb, editSchedCb, manageRolesCb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_roles);
        getSupportActionBar().hide();
        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        manageReqCb = findViewById(R.id.manageReqCb);
        appointmentsCb = findViewById(R.id.appointmentsCb);
        manageRecCb = findViewById(R.id.manageRecCb);
        manageRepCb = findViewById(R.id.manageRepCb);
        editAgreeCb = findViewById(R.id.editAgreeCb);
        editContactCb = findViewById(R.id.editContactCb);
        editSchedCb = findViewById(R.id.editSchedCb);
        manageRolesCb = findViewById(R.id.manageRolesCb);
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}