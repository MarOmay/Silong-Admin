package com.silong.Task;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.Operation.ImageProcessor;
import com.silong.Operation.Utility;
import com.silong.admin.AdminData;

public class AccountFetcher extends AsyncTask {

    private String uid;
    private Activity activity;

    private FirebaseDatabase mDatabase;

    public AccountFetcher(String uid, Activity activity){
        this.activity = activity;
        this.uid = uid;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try{
            //create local copy
            mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

            AdminData.writeToLocal(activity, uid, "userID", uid);

            Utility.log("Fetching Pet: " + uid);

            DatabaseReference tempReference = mDatabase.getReference("Users").child(uid);
            tempReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        String status = (Boolean) snapshot.child("accountStatus").getValue() ? "true" : "false";
                        String fname = snapshot.child("firstName").getValue().toString();
                        String lname = snapshot.child("lastName").getValue().toString();
                        String email = snapshot.child("email").getValue().toString();
                        String contact = snapshot.child("contact").getValue().toString();
                        String birthday = snapshot.child("birthday").getValue().toString();

                        String lastModified = "no data";
                        if (snapshot.child("lastModified").getValue() != null)
                            lastModified = snapshot.child("lastModified").getValue().toString();

                        String gender = snapshot.child("gender").getValue().toString();
                        String addressLine = snapshot.child("address").child("addressLine").getValue().toString();
                        String barangay = snapshot.child("address").child("barangay").getValue().toString();
                        String photo = snapshot.child("photo").getValue().toString();

                        AdminData.writeToLocal(activity, uid, "accountStatus", status);
                        AdminData.writeToLocal(activity, uid, "firstName", fname);
                        AdminData.writeToLocal(activity, uid, "lastName", lname);
                        AdminData.writeToLocal(activity, uid, "email", email);
                        AdminData.writeToLocal(activity, uid, "contact", contact);
                        AdminData.writeToLocal(activity, uid, "birthday", birthday);
                        AdminData.writeToLocal(activity, uid, "lastModified", lastModified);
                        AdminData.writeToLocal(activity, uid, "gender", gender);
                        AdminData.writeToLocal(activity, uid, "addressLine", addressLine);
                        AdminData.writeToLocal(activity, uid, "barangay", barangay);

                        Bitmap bitmap = new ImageProcessor().toBitmap(photo);
                        new ImageProcessor().saveToLocal(activity, bitmap, "avatar-" + uid);
                        //AdminData.populateAccounts(activity);
                        updateAccountList();
                    }
                    catch (Exception e){
                        Utility.log("AccountFetcher.dIB: " + e.getMessage());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Utility.log("AccountFetcher.dIB: " + error.getMessage());
                }
            });

        }
        catch (Exception e){
            Utility.log("AccountFetcher.dIB: " + e.getMessage());
        }
        return null;
    }

    public void updateAccountList(){
        Intent intent = new Intent("update-account-list");
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }
}
