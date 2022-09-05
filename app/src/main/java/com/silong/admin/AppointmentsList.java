package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class AppointmentsList extends AppCompatActivity {

    ImageView appointmentBackIv;
    RecyclerView appointmentRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments_list);
        getSupportActionBar().hide();

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        appointmentBackIv = (ImageView) findViewById(R.id.appointmentBackIv);
        appointmentRecycler = (RecyclerView) findViewById(R.id.appointmentRecycler);
    }

    public void back(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AppointmentsList.this, Dashboard.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}