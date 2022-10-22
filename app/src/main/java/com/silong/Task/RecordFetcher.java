package com.silong.Task;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.Operation.ImageProcessor;
import com.silong.Operation.Utility;
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

            DatabaseReference tempReference = mDatabase.getReference("Pets").child(id);
            tempReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    try{
                        int status = Integer.valueOf(snapshot.child("status").getValue().toString());
                        int type = Integer.valueOf(snapshot.child("type").getValue().toString());
                        int gender = Integer.valueOf(snapshot.child("gender").getValue().toString());
                        int size = Integer.valueOf(snapshot.child("size").getValue().toString());
                        int age = Integer.valueOf(snapshot.child("age").getValue().toString());
                        String color = snapshot.child("color").getValue().toString();
                        String modifiedBy = snapshot.child("modifiedBy").getValue().toString();
                        String lastModified = snapshot.child("lastModified").getValue().toString();

                        //get optional data
                        String distMark = null, rescueDate = null, extrapic1 = null, extrapic2 = null;
                        try {
                            distMark = snapshot.child("distMark").getValue().toString();
                        }
                        catch (Exception e){
                            Utility.log("RecordFetcher: Check if exist distMark - " + e.getMessage());
                        }
                        try {
                            rescueDate = snapshot.child("rescueDate").getValue().toString();
                        }
                        catch (Exception e){
                            Utility.log("RecordFetcher: Check if exist rescueDate - " + e.getMessage());
                        }
                        try {
                            extrapic1 = snapshot.child("extraPhoto").child("photo1").getValue().toString();
                        }
                        catch (Exception e){
                            Utility.log("RecordFetcher: Check if exist extrapic1 - " + e.getMessage());
                        }
                        try {
                            extrapic2 = snapshot.child("extraPhoto").child("photo2").getValue().toString();
                        }
                        catch (Exception e){
                            Utility.log("RecordFetcher: Check if exist extrapic2 - " + e.getMessage());
                        }

                        //write to local
                        AdminData.writePetToLocal(activity, id, "status", String.valueOf(status));
                        AdminData.writePetToLocal(activity, id, "type", String.valueOf(type));
                        AdminData.writePetToLocal(activity, id, "gender", String.valueOf(gender));
                        AdminData.writePetToLocal(activity, id, "size", String.valueOf(size));
                        AdminData.writePetToLocal(activity, id, "age", String.valueOf(age));
                        AdminData.writePetToLocal(activity, id, "color", String.valueOf(color));
                        AdminData.writePetToLocal(activity, id, "modifiedBy", String.valueOf(modifiedBy));
                        AdminData.writePetToLocal(activity, id, "lastModified", String.valueOf(lastModified));

                        String photo = snapshot.child("photo").getValue().toString();
                        Bitmap bitmap = new ImageProcessor().toBitmap(photo);
                        new ImageProcessor().saveToLocal(activity, bitmap, "petpic-" + id);

                        //write extra to local
                        if (distMark != null){
                            AdminData.writePetToLocal(activity, id, "distMark", String.valueOf(distMark));
                        }
                        if (rescueDate != null){
                            AdminData.writePetToLocal(activity, id, "rescueDate", String.valueOf(rescueDate));
                        }
                        if (extrapic1 != null){
                            Bitmap bmp = new ImageProcessor().toBitmap(extrapic1);
                            new ImageProcessor().saveToLocal(activity, bmp, "extrapic-" + id + "-1");
                        }
                        if (extrapic2 != null){
                            Bitmap bmp = new ImageProcessor().toBitmap(extrapic2);
                            new ImageProcessor().saveToLocal(activity, bmp, "extrapic-" + id + "-2");
                        }

                        AdminData.populateRecords(activity);
                        updateRecordList();
                    }
                    catch (Exception e){
                        Utility.log("RecordFetcher.dIB.oDC: " + e.getMessage());
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Utility.log("RecordFetcher.dIB.oC: " + error.getMessage());
                }
            });

        }
        catch (Exception e){
            Utility.log("RecordFetcher.dIB: " + e.getMessage());
        }
        return null;
    }

    private void updateRecordList(){
        //Intent intent = new Intent("update-record-list");
        //LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
