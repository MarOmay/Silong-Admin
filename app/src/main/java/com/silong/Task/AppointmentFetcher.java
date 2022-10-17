package com.silong.Task;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.silong.Object.AppointmentRecords;

import com.silong.Object.User;
import com.silong.admin.AdminData;

public class AppointmentFetcher extends AsyncTask {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private Activity activity;

    public AppointmentFetcher(Activity activity){
        this.activity = activity;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try{
            Log.d("DEBUGGER>>>", "AppointmentFetcher started ");

            mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
            mReference = mDatabase.getReference().child("adoptionRequest");

            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap : snapshot.getChildren()){
                        AppointmentRecords appointment = new AppointmentRecords();
                        String key = snap.getKey().toString();

                        User user = AdminData.getUser(key);

                        if (user == null)
                            continue;

                        appointment.setName(user.getFirstName() + " " + user.getLastName());
                        appointment.setUserID(key);

                        String status = snap.child("status").getValue().toString();
                        if (status.equals("4"))
                            Log.d("DEBUGGER>>>", "adoption request");
                        else
                            continue;

                        String petID = snap.child("petID").getValue().toString();
                        appointment.setPetId(petID);

                        String date = snap.child("appointmentDate").getValue().toString();
                        date += " " + snap.child("appointmentTime").getValue().toString().replace("*",":");
                        appointment.setDateTime(date);

                        appointment.setUserPic(user.getPhoto());

                        if (AdminData.appointments.size() < 1){
                            AdminData.appointments.add(appointment);
                        }
                        else{
                            boolean found = false;
                            for (AppointmentRecords ap : AdminData.appointments){
                                Log.d("DEBUGGER>>>", "a-" + ap.getUserID());
                                Log.d("DEBUGGER>>>", "A-" + ap.getUserID());

                                if (ap.getUserID().equals(appointment.getUserID()))
                                    found = true;
                            }
                            if (!found)
                                AdminData.appointments.add(appointment);

                            Log.d("DEBUGGER>>>", "Found- " + found);

                        }

                        Log.d("DEBUGGER>>>", "details " + date);
                    }

                    //send broadcast to show/hide red dot
                    if (AdminData.appointments.isEmpty()){
                        sendBroadcast(false);
                    }
                    else {
                        sendBroadcast(true);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        catch (Exception e){
            Log.d("AppointmentF-dIB", e.getMessage());
        }

        return null;
    }

    private void sendBroadcast(boolean notify){
        Intent intent = new Intent("AF-sb-notify");
        intent.putExtra("notify", notify);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }
}
