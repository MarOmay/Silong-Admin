package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.net.Inet4Address;

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
        Intent i = new Intent(AboutTheOffice.this, AdoptionAgreement.class);
        startActivity(i);
    }

    public void onPressedContactInformation(View view){
        Intent i = new Intent(AboutTheOffice.this, ContactInformation.class);
        startActivity(i);
    }

    public void onPressedOfficeSchedule(View view){
        Intent i = new Intent(AboutTheOffice.this, OfficeSchedule.class);
        startActivity(i);
    }
}