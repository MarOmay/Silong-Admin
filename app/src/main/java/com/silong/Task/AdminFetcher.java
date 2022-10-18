package com.silong.Task;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.Object.Admin;
import com.silong.Operation.Utility;
import com.silong.admin.ManageRoles;

public class AdminFetcher extends AsyncTask {

    private Activity activity;

    public AdminFetcher(Activity activity){
        this.activity = activity;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        try {
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
            DatabaseReference mRef = mDatabase.getReference("Admins");
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot snap : snapshot.getChildren()){

                        Admin admin = new Admin();

                        admin.setAdminID(snap.getKey());
                        admin.setAdminEmail(snap.child("email").getValue().toString());
                        admin.setContact(snap.child("contact").getValue().toString());
                        admin.setFirstName(snap.child("firstName").getValue().toString());
                        admin.setLastName(snap.child("lastName").getValue().toString());

                        admin.setRole_manageRequests((boolean) snap.child("roles").child("manageRequests").getValue());
                        admin.setRole_appointments((boolean) snap.child("roles").child("appointments").getValue());
                        admin.setRole_manageRecords((boolean) snap.child("roles").child("manageRecords").getValue());
                        admin.setRole_manageReports((boolean) snap.child("roles").child("manageReports").getValue());
                        admin.setRole_editAgreement((boolean) snap.child("roles").child("editAgreement").getValue());
                        admin.setRole_editContact((boolean) snap.child("roles").child("editContact").getValue());
                        admin.setRole_editSchedule((boolean) snap.child("roles").child("editSchedule").getValue());
                        admin.setRole_manageRoles((boolean) snap.child("roles").child("manageRoles").getValue());
                        admin.setRole_manageDatabase((boolean) snap.child("roles").child("manageDatabase").getValue());

                        ManageRoles.ADMINS.add(admin);

                    }

                    sendBroadcast();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception e){
            Utility.log("AdminFetcher.dIB: " + e.getMessage());
        }

        return null;
    }

    private void sendBroadcast(){
        Intent intent = new Intent("refresh-admin-list");
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }
}
