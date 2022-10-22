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

public class ActivationRequestFetcher extends AsyncTask {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private Activity activity;

    public ActivationRequestFetcher(Activity activity){
        this.activity = activity;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try{

            mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
            mReference = mDatabase.getReference().child("accountStatusRequests");

            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap : snapshot.getChildren()){
                        Request request = new Request();
                        String key = snap.getKey().toString();
                        request.setRequestCode(RequestCode.ACCOUNT_ACTIVATION);
                        request.setUserID(key);

                        String details = snap.child("reason").getValue().toString();
                        request.setRequestDetails(details);

                        String date = snap.child("date").getValue().toString();
                        request.setDate(date);

                        for (Request r : AdminData.requests){
                            if (r.getUserID() ==  request.getUserID() &&
                                r.getRequestCode() == request.getRequestCode())
                                return;
                        }

                        AdminData.requests.add(request);

                    }

                    //send broadcast to show/hide red dot
                    if (AdminData.requests.isEmpty()){
                        sendBroadcast(false);
                    }
                    else {
                        sendBroadcast(true);
                    }

                    Dashboard.actReqDone = true;
                    Dashboard.checkCompletion();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Utility.log("ARF.dIB.oC: " + error.getMessage());
                }
            });

        }
        catch (Exception e){
            Utility.log("ARF.dIB: " + e.getMessage());
        }

        return null;
    }

    private void sendBroadcast(boolean notify){
        Intent intent = new Intent("ARF-sb-notify");
        intent.putExtra("notify", notify);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }
}
