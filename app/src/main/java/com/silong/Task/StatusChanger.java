package com.silong.Task;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silong.Operation.Utility;
import com.silong.admin.AdminData;

import java.util.HashMap;
import java.util.Map;

public class StatusChanger extends AsyncTask {

    public final static String SUCCESS = "success";
    public final static String FAILURE = "failure";

    private String uid;
    private String email;
    private boolean status;
    private Activity activity;

    private FirebaseDatabase mDatabase;

    public StatusChanger(String uid, boolean status, Activity activity){
        this.uid = uid;
        this.status = status;
        this.activity = activity;
        this.email = AdminData.getUser(uid).getEmail();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try{
            //prepare data
            Map<String, Object> map = new HashMap<>();
            map.put("accountStatus", status);
            map.put("cancellation", 0);

            //Change value in specific account
            mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
            DatabaseReference tempReference = mDatabase.getReference("Users/" + uid);
            tempReference.updateChildren(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            setAccountStatusInSummary(uid, status);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            sendBroadcast(FAILURE, uid, status);
                            Utility.log("StatusChanger.dIB.oF: " + e.getMessage());
                        }
                    });
        }
        catch (Exception e){
            sendBroadcast(FAILURE, uid, status);
            Utility.log("StatusChanger.dIB: " + e.getMessage());
        }
        return null;
    }

    private void setAccountStatusInSummary(String uid, boolean status){
        try{
            //Change value in accountSummary
            DatabaseReference tempReference = mDatabase.getReference("accountSummary/" + uid);
            tempReference.setValue(status)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            if (status){
                                DatabaseReference activationRequest = mDatabase.getReference("accountStatusRequests").child(uid);
                                activationRequest.setValue(null);
                            }
                            sendBroadcast(SUCCESS, uid, status);

                            Utility.dbLog("Set " + email + " account status to " + status);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            sendBroadcast(FAILURE, uid, status);
                            Utility.log("StatusChanger.sASIS.oF: " + e.getMessage());
                        }
                    });
        }
        catch (Exception e){
            sendBroadcast(FAILURE, uid, status);
            Utility.log("StatusChanger.dIB.sASIS: " + e.getMessage());
        }
    }

    private void sendBroadcast(String code, String uid, boolean status){
        Intent intent = new Intent("SC-coded");
        intent.putExtra("code", code);
        intent.putExtra("uid", uid);
        intent.putExtra("status", status);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }
}
