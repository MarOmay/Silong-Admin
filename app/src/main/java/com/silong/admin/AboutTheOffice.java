package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.silong.Operation.Utility;

public class AboutTheOffice extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_the_office);
        getSupportActionBar().hide();
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    public void onPressedAdoptionAgreement(View view){
        new Utility().checkPermission(AboutTheOffice.this, "editAgreement", AdminData.adminID, false);
    }

    public void onPressedContactInformation(View view){
        new Utility().checkPermission(AboutTheOffice.this, "editContact", AdminData.adminID, false);
    }

    public void onPressedOfficeSchedule(View view){
        new Utility().checkPermission(AboutTheOffice.this, "editSchedule", AdminData.adminID, false);
    }

    public void onPressedManageRoles(View view){
        new Utility().checkPermission(AboutTheOffice.this, "manageRoles", AdminData.adminID, false);
    }

    public void onPressedManageDatabase(View view){
        new Utility().checkPermission(AboutTheOffice.this, "manageDatabase", AdminData.adminID, false);
    }

}