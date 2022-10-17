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
import com.silong.Object.Adoption;
import com.silong.Operation.Utility;
import com.silong.admin.UserInformation;

public class AdoptionHistoryFetcher extends AsyncTask {

    private Activity activity;
    private String uid;

    public AdoptionHistoryFetcher(Activity activity, String uid){
        this.activity = activity;
        this.uid = uid;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        try {
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
            DatabaseReference mRef = mDatabase.getReference("Users").child(uid).child("adoptionHistory");
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot snap : snapshot.getChildren()){
                        Adoption adoption = new Adoption();

                        int petID = Integer.parseInt(snap.getKey());
                        int status = Integer.parseInt(snap.child("status").getValue().toString());
                        String dateReq = snap.child("dateRequested").getValue().toString();

                        adoption.setPetID(petID);
                        adoption.setStatus(status);
                        adoption.setDateRequested(dateReq);

                        UserInformation.ADOPTIONS.add(adoption);
                    }

                    sendBroadcast();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception e){
            Utility.log("AHF.dIB: " + e.getMessage());
        }
        return null;
    }

    private void sendBroadcast(){
        Intent intent = new Intent("refresh-history");
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }
}
