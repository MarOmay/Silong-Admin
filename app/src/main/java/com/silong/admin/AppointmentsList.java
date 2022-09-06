package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.silong.Adapter.AppointmentAdapter;
import com.silong.Object.AppointmentRecords;
import com.silong.Task.AppointmentFetcher;

import java.util.Comparator;

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

        appointmentRecycler.setHasFixedSize(true);
        appointmentRecycler.setLayoutManager(new LinearLayoutManager(AppointmentsList.this));

        loadAppointments();

    }

    public void loadAppointments(){

        //try to sort
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                AdminData.appointments.sort(new Comparator<AppointmentRecords>() {
                    @Override
                    public int compare(AppointmentRecords appointmentRecords, AppointmentRecords t1) {
                        return appointmentRecords.getDateTime().compareTo(t1.getDateTime());
                    }
                });
            }
        }
        catch (Exception e){
            Log.d("DEBUGGER>>>", e.getMessage());
        }

        //make a copy of appointments
        AppointmentRecords [] appointmentRecords = new AppointmentRecords[AdminData.appointments.size()];
        for (AppointmentRecords appointment : AdminData.appointments){
            appointmentRecords[AdminData.appointments.indexOf(appointment)] = new AppointmentRecords(appointment.getName(), appointment.getDateTime(), appointment.getPetId(), appointment.getUserPic(), appointment.getUserID());
            Log.d("DEBUGGER>>>", "added appointment - " + appointment.getName());
        }

        //set appointmentRecycler adapter
        AppointmentAdapter appointmentAdapter = new AppointmentAdapter(appointmentRecords, AppointmentsList.this);
        appointmentRecycler.setAdapter(appointmentAdapter);
    }

    public void onPressedBack(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}