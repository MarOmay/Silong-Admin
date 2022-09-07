package com.silong.Task;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.Operation.ImageProcessor;
import com.silong.admin.AdminData;

public class AccountFetcher extends AsyncTask {

    private String uid;
    private Activity activity;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

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

            DatabaseReference tempReference = mDatabase.getReference("Users/" + uid);
            tempReference.child("accountStatus").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String status = (Boolean) snapshot.getValue() ? "true" : "false";
                    AdminData.writeToLocal(activity, uid, "accountStatus", status);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("firstName").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String fname = snapshot.getValue().toString();
                    AdminData.writeToLocal(activity, uid, "firstName", fname);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("lastName").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String lname = snapshot.getValue().toString();
                    AdminData.writeToLocal(activity, uid, "lastName", lname);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("email").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String email = snapshot.getValue().toString();
                    AdminData.writeToLocal(activity, uid, "email", email);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("contact").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String contact = snapshot.getValue().toString();
                    AdminData.writeToLocal(activity, uid, "contact", contact);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("birthday").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String birthday = snapshot.getValue().toString();
                    AdminData.writeToLocal(activity, uid, "birthday", birthday);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("lastModified").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String lastModified = "no data";
                    if (snapshot.getValue() != null)
                        lastModified = snapshot.getValue().toString();
                    AdminData.writeToLocal(activity, uid, "lastModified", lastModified);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("gender").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String gender = snapshot.getValue().toString();
                    AdminData.writeToLocal(activity, uid, "gender", gender);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("address").child("addressLine").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String addressLine = snapshot.getValue().toString();
                    AdminData.writeToLocal(activity, uid, "addressLine", addressLine);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("address").child("barangay").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String barangay = snapshot.getValue().toString();
                    AdminData.writeToLocal(activity, uid, "barangay", barangay);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("photo").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String photo = snapshot.getValue().toString();
                    Bitmap bitmap = new ImageProcessor().toBitmap(photo);
                    new ImageProcessor().saveToLocal(activity, bitmap, "avatar-" + uid);
                    AdminData.populateAccounts(activity);
                    updateAccountList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        catch (Exception e){
            Log.d("AF-dIB", e.getMessage());
        }
        return null;
    }

    public void updateAccountList(){
        Intent intent = new Intent("update-account-list");
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }
}
