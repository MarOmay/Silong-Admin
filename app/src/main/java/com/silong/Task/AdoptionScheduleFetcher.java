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
import com.silong.EnumClass.RequestCode;
import com.silong.Object.Request;
import com.silong.Operation.Utility;
import com.silong.admin.AdminData;
import com.silong.admin.Dashboard;

public class AdoptionScheduleFetcher extends AsyncTask {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private Activity activity;

    public AdoptionScheduleFetcher(Activity activity){
        this.activity = activity;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try{

            mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
            mReference = mDatabase.getReference().child("adoptionRequest");

            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap : snapshot.getChildren()){
                        Request request = new Request();
                        String key = snap.getKey().toString();
                        request.setRequestCode(RequestCode.APPOINTMENT);
                        request.setUserID(key);

                        Object tempStatus = snap.child("status").getValue();
                        if (tempStatus == null)
                            continue;

                        String status = tempStatus.toString();

                        if (status.equals("3"))
                            Utility.log("ASF.dIB: (adoption request)");
                        else
                            continue;

                        String details = snap.child("petID").getValue().toString();
                        request.setRequestDetails(details);

                        String date = snap.child("appointmentDate").getValue().toString();
                        date += " " + snap.child("appointmentTime").getValue().toString().replace("*",":");
                        request.setDate(date);

                        if (AdminData.requests.size() < 1){
                            AdminData.requests.add(request);
                        }
                        else{
                            boolean found = false;
                            for (Request r : AdminData.requests){

                                if (r.getUserID().equals(request.getUserID()) &&
                                        r.getRequestCode() == request.getRequestCode())
                                    found = true;
                            }
                            if (!found)
                                AdminData.requests.add(request);

                        }

                    }

                    //send broadcast to show/hide red dot
                    if (AdminData.requests.isEmpty()){
                        sendBroadcast(false);
                    }
                    else {
                        sendBroadcast(true);
                    }

                    Dashboard.adopSchedDone = true;
                    Dashboard.checkCompletion();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Utility.log("ASF.dIB.oC: " + error.getMessage());
                }
            });

        }
        catch (Exception e){
            Utility.log("ASF.dIB: " + e.getMessage());
        }
        return null;
    }

    private void sendBroadcast(boolean notify){
        Intent intent = new Intent("ARF-sb-notify");
        intent.putExtra("notify", notify);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }
}
