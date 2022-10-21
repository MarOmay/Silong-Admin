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
import com.silong.Object.Pet;

import com.silong.Operation.Utility;
import com.silong.admin.AdminData;
import com.silong.admin.Dashboard;

import java.io.File;
import java.util.ArrayList;

public class RecordsChecker extends AsyncTask {

    private Activity activity;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    public RecordsChecker(Activity activity){
        this.activity = activity;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try{
            mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
            mReference = mDatabase.getReference("recordSummary");
            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        //Get all User uid
                        ArrayList<String> keys = new ArrayList<>();
                        ArrayList<String> list = new ArrayList<>();
                        for (DataSnapshot snap : snapshot.getChildren()){
                            if (snap.getKey().equals("null") || snap.getKey() == null || snap == null){
                                continue;
                            }

                            keys.add("pet-" + snap.getKey());

                            File file = new File(activity.getFilesDir(), "pet-" + snap.getKey());
                            if (file.exists()){
                                //Check if status of local record matches
                                Pet tempPet = AdminData.fetchRecordFromLocal(activity, snap.getKey());

                                if (tempPet.getStatus() != Integer.parseInt(snap.getValue().toString())){
                                    //delete local record, to rewrite new record
                                    file.delete();
                                    RecordFetcher recordFetcher = new RecordFetcher(snap.getKey(), activity);
                                    recordFetcher.execute();
                                    continue;
                                }

                                //check if dateModified matches
                                DatabaseReference tempRef = mDatabase.getReference("Pets").child(snap.getKey()).child("lastModified");
                                tempRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        String lastModified = snapshot.getValue().toString();

                                        if (!lastModified.equals(tempPet.getLastModified())){
                                            //download latest version
                                            RecordFetcher recordFetcher = new RecordFetcher(snap.getKey(), activity);
                                            recordFetcher.execute();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Utility.log("RecordsChecker.dIB: " + error.getMessage());
                                    }
                                });
                            }
                            else {
                                RecordFetcher recordFetcher = new RecordFetcher(snap.getKey(), activity);
                                recordFetcher.execute();
                            }
                            list.add(snap.getKey());
                        }

                        //get pet- files only
                        ArrayList<File> petFiles = new ArrayList<>();
                        File [] files = activity.getFilesDir().listFiles();
                        for (File file : files){
                            if (file.getAbsolutePath().contains("pet-"))
                                petFiles.add(file);
                        }

                        for (File petFile : petFiles){
                            boolean found = false;
                            for (String key : keys){
                                File tempFile = new File(activity.getFilesDir(), key);
                                if (petFile.getAbsolutePath().equals(tempFile.getAbsolutePath()))
                                    found = true;
                            }

                            //remove file if not found in key
                            if (!found)
                                petFile.delete();
                        }

                        //delete local copy of deleted accounts
                        cleanLocalRecord(list, "pet-");

                        Dashboard.recCheckDone = true;
                        Dashboard.checkCompletion();
                    }
                    catch (Exception e){
                        Utility.log("RecordsChecker.dIB: " + e.getMessage());
                    }
                    AdminData.populateRecords(activity);
                    sendBroadcast();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    AdminData.populateRecords(activity);
                    sendBroadcast();
                    Utility.log("RecordsChecker.dIB.oC: " + error.getMessage());
                }
            });
        }
        catch (Exception e){
            Utility.log("RecordsChecker.dIB: " + e.getMessage());
        }
        return null;
    }

    private void cleanLocalRecord(ArrayList<String> list, String prefix){
        File [] files = activity.getFilesDir().listFiles();
        ArrayList<File> accountFiles = new ArrayList<>();

        //filter out non-account files
        for (File file : files){
            if (file.getAbsolutePath().contains(prefix)){
                accountFiles.add(file);
            }
        }

        //filter deleted accounts
        ArrayList<File> deletedAccounts = new ArrayList<>();
        for (File file : accountFiles){
            boolean found = false;
            for (String s : list){
                if (file.getAbsolutePath().contains(s))
                    found = true;
            }
            if (!found)
                deletedAccounts.add(file);
        }

        for (File file : deletedAccounts){
            try {
                file.delete();
            }
            catch (Exception e){
                Utility.log("RecordsChecker.cLR: " + e.getMessage());
            }
        }

    }

    private void sendBroadcast(){
        Intent intent = new Intent("RC-done");
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }
}
