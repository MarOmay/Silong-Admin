package com.silong.Task;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.Operation.ImageProcessor;
import com.silong.admin.AdminData;

public class RecordFetcher extends AsyncTask {

    private String id;
    private Activity activity;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    public RecordFetcher(String id, Activity activity){
        this.activity = activity;
        this.id = id;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try{
            //create local copy
            mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

            AdminData.writePetToLocal(activity, id, "petID", id);

            DatabaseReference tempReference = mDatabase.getReference("Pets/" + id);
            tempReference.child("status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int status = Integer.valueOf(snapshot.getValue().toString());
                    AdminData.writePetToLocal(activity, id, "status", String.valueOf(status));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("type").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int type = Integer.valueOf(snapshot.getValue().toString());
                    AdminData.writePetToLocal(activity, id, "type", String.valueOf(type));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("gender").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int gender = Integer.valueOf(snapshot.getValue().toString());
                    AdminData.writePetToLocal(activity, id, "gender", String.valueOf(gender));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("size").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int size = Integer.valueOf(snapshot.getValue().toString());
                    AdminData.writePetToLocal(activity, id, "size", String.valueOf(size));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("age").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int age = Integer.valueOf(snapshot.getValue().toString());
                    AdminData.writePetToLocal(activity, id, "age", String.valueOf(age));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("color").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String color = snapshot.getValue().toString();
                    AdminData.writePetToLocal(activity, id, "color", String.valueOf(color));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("modifiedBy").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String modifiedBy = snapshot.getValue().toString();
                    AdminData.writePetToLocal(activity, id, "modifiedBy", String.valueOf(modifiedBy));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("lastModified").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String lastModified = snapshot.getValue().toString();
                    AdminData.writePetToLocal(activity, id, "lastModified", String.valueOf(lastModified));
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
                    new ImageProcessor().saveToLocal(activity, bitmap, "petpic-" + id);
                    AdminData.populateRecords(activity);
                    updateRecordList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        catch (Exception e){
            Log.d("RC-dIB", e.getMessage());
        }
        return null;
    }

    private void updateRecordList(){
        //Intent intent = new Intent("update-record-list");
        //LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
