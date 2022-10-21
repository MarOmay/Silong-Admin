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
import com.silong.EnumClass.RequestCode;
import com.silong.Object.Request;
import com.silong.admin.AdminData;
import com.silong.admin.Dashboard;

public class AdoptionRequestFetcher extends AsyncTask {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private Activity activity;

    public AdoptionRequestFetcher(Activity activity){
        this.activity = activity;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try{
            Log.d("DEBUGGER>>>", "ARF started ");

            mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
            mReference = mDatabase.getReference().child("adoptionRequest");

            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap : snapshot.getChildren()){
                        Request request = new Request();
                        String key = snap.getKey().toString();
                        request.setRequestCode(RequestCode.ADOPTION_REQUEST);
                        request.setUserID(key);

                        if (snap.child("petID").getValue() == null ||
                                snap.child("dateRequested").getValue() == null ||
                                snap.child("status").getValue() == null){
                            continue;
                        }

                        String status = snap.child("status").getValue().toString();
                        if (status.equals("1"))
                            Log.d("DEBUGGER>>>", "adoption request");
                        else
                            continue;

                        String details = snap.child("petID").getValue().toString();
                        request.setRequestDetails(details);

                        String date = snap.child("dateRequested").getValue().toString();
                        request.setDate(date);

                        if (AdminData.requests.size() < 1){
                            AdminData.requests.add(request);
                        }
                        else{
                            boolean found = false;
                            for (Request r : AdminData.requests){
                                Log.d("DEBUGGER>>>", "r-" + r.getUserID());
                                Log.d("DEBUGGER>>>", "R-" + request.getUserID());
                                Log.d("DEBUGGER>>>", "r-" + r.getRequestCode());
                                Log.d("DEBUGGER>>>", "r-" + request.getRequestCode());

                                if (r.getUserID().equals(request.getUserID()) &&
                                        r.getRequestCode() == request.getRequestCode())
                                    found = true;
                            }
                            if (!found)
                                AdminData.requests.add(request);

                            Log.d("DEBUGGER>>>", "Found- " + found);

                        }

                        Log.d("DEBUGGER>>>", "details " + details);
                    }

                    //send broadcast to show/hide red dot
                    if (AdminData.requests.isEmpty()){
                        sendBroadcast(false);
                    }
                    else {
                        sendBroadcast(true);
                    }

                    Dashboard.adopReqDone = true;
                    Dashboard.checkCompletion();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        catch (Exception e){
            Log.d("ARF-dIB", e.getMessage());

        }
        return null;
    }

    private void sendBroadcast(boolean notify){
        Intent intent = new Intent("ARF-sb-notify");
        intent.putExtra("notify", notify);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }
}
