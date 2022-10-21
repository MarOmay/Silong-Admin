package com.silong.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silong.Adapter.AppointmentAdapter;
import com.silong.CustomView.LoadingDialog;
import com.silong.CustomView.ReschedDatePicker;
import com.silong.CustomView.ReschedTimePicker;
import com.silong.CustomView.RescheduleDialog;
import com.silong.Object.Adoption;
import com.silong.Object.AppointmentRecords;
import com.silong.Operation.EmailNotif;
import com.silong.Operation.Utility;


import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class AppointmentsList extends AppCompatActivity {

    ImageView appointmentBackIv;
    RecyclerView appointmentRecycler;

    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments_list);
        getSupportActionBar().hide();

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        //register receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRescheduleReceiver, new IntentFilter("reschedule-trigger"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mSetNewSchedReceiver, new IntentFilter("reschedule-set"));

        //initialize Firebase objects
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        appointmentBackIv = (ImageView) findViewById(R.id.appointmentBackIv);
        appointmentRecycler = (RecyclerView) findViewById(R.id.appointmentRecycler);

        appointmentRecycler.setHasFixedSize(true);
        appointmentRecycler.setLayoutManager(new LinearLayoutManager(AppointmentsList.this));

        loadAppointments();

    }

    public void loadAppointments(){

        LoadingDialog loadingDialog = new LoadingDialog(AppointmentsList.this);
        loadingDialog.startLoadingDialog();

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
            Utility.log("AppointmentsList.lA: " + e.getMessage());
        }

        int listSize = AdminData.appointments.size();

        //make a copy of appointments
        AppointmentRecords [] appointmentRecords = new AppointmentRecords[listSize];
        for (int i = 0; i < listSize; i++){
            AppointmentRecords ar = AdminData.appointments.get(i);
            appointmentRecords[i] = new AppointmentRecords(ar.getName(), ar.getDateTime(), ar.getPetId(), ar.getUserPic(), ar.getUserID());
            Utility.log("AppointmentsList.lA: Added appointment - " + ar.getName());
        }

        //set appointmentRecycler adapter
        AppointmentAdapter appointmentAdapter = new AppointmentAdapter(appointmentRecords, AppointmentsList.this);
        appointmentRecycler.setAdapter(appointmentAdapter);

        loadingDialog.dismissLoadingDialog();

    }

    private BroadcastReceiver mRescheduleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String userID = intent.getStringExtra("userID");
                Adoption adoption = (Adoption) intent.getSerializableExtra("adoption");

                RescheduleDialog rescheduleDialog = new RescheduleDialog(AppointmentsList.this, userID, adoption);
                rescheduleDialog.show();

                rescheduleDialog.reschedDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ReschedDatePicker reschedDatePicker = new ReschedDatePicker(AppointmentsList.this, rescheduleDialog);
                        reschedDatePicker.show(getSupportFragmentManager(), null);
                    }
                });

                rescheduleDialog.reschedTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ReschedTimePicker reschedTimePicker = new ReschedTimePicker(AppointmentsList.this, rescheduleDialog);
                        reschedTimePicker.show(getSupportFragmentManager(), null);
                    }
                });

            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Can't process request.", Toast.LENGTH_SHORT).show();
                Utility.log("AppointmentsList.mRR: " + e.getMessage());
            }
        }
    };

    private BroadcastReceiver mSetNewSchedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            LoadingDialog loadingDialog = new LoadingDialog(AppointmentsList.this);
            loadingDialog.startLoadingDialog();

            try {
                //parse data
                String userID = intent.getStringExtra("userID");
                String petID =  intent.getStringExtra("petID");
                String date = intent.getStringExtra("date").replace("/", "-");
                String time = intent.getStringExtra("time").replace(":", "*");

                //prepare data
                Map<String, Object> map = new HashMap<>();
                map.put("appointmentDate", date);
                map.put("appointmentTime", time);

                Utility.log("AppointmentsList.mSNSR: PetID-" + petID);

                //upload data
                DatabaseReference mRef = mDatabase.getReference("adoptionRequest").child(userID);
                mRef.updateChildren(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                String dateTime = date.replace("-","/") + " " + time.replace("*", ":");

                                try {
                                    String email = AdminData.getUser(userID).getEmail();
                                    Adoption adoption = new Adoption();
                                    adoption.setAppointmentDate(dateTime);
                                    adoption.setPetID(Integer.valueOf(petID));
                                    EmailNotif emailNotif = new EmailNotif(email, EmailNotif.APPOINTMENT_CHANGED, adoption);
                                    emailNotif.sendNotif();
                                }
                                catch (Exception e){
                                    Toast.makeText(getApplicationContext(), "Email notification not sent", Toast.LENGTH_SHORT).show();
                                    Utility.log("AppointmentsList.mSNSR: " + e.getMessage());
                                }

                                for (AppointmentRecords ar : AdminData.appointments){
                                    if (ar.getPetId().equals(petID)){
                                        //replace appointment in list

                                        int index = AdminData.appointments.indexOf(ar);
                                        AppointmentRecords appRec = new AppointmentRecords();
                                        appRec.setName(ar.getName());
                                        appRec.setUserPic(ar.getUserPic());
                                        appRec.setUserID(ar.getUserID());
                                        appRec.setPetId(ar.getPetId());
                                        appRec.setDateTime(dateTime);

                                        AdminData.appointments.set(index, appRec);

                                        loadAppointments();
                                        break;

                                    }
                                }

                                Toast.makeText(getApplicationContext(), "Successfully rescheduled", Toast.LENGTH_SHORT).show();
                                loadingDialog.dismissLoadingDialog();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Failed to save changes", Toast.LENGTH_SHORT).show();
                                Utility.log("AppointmentsList.mSNSR: " + e.getMessage());
                                loadingDialog.dismissLoadingDialog();
                            }
                        });

            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Failed to save changes", Toast.LENGTH_SHORT).show();
                Utility.log("AppointmentsList.mSNSR: " + e.getMessage());
                loadingDialog.dismissLoadingDialog();
            }

        }
    };

    public void onPressedBack(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AppointmentsList.this, Dashboard.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRescheduleReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSetNewSchedReceiver);
        super.onDestroy();
    }
}