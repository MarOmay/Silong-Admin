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
import com.silong.Object.User;
import com.silong.admin.AdminData;

import java.io.File;
import java.util.ArrayList;

public class AccountsChecker extends AsyncTask {

    private Activity activity;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    public AccountsChecker(Activity activity){
        this.activity = activity;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try{
            mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
            mReference = mDatabase.getReference("accountSummary");
            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        //Get all User uid
                        ArrayList<String> list = new ArrayList<>();
                        for (DataSnapshot snap : snapshot.getChildren()){

                            File file = new File(activity.getFilesDir(), "account-" + snap.getKey());
                            if (file.exists()){
                                //Check if status of local record matches
                                User tempUser = AdminData.fetchAccountFromLocal(activity, snap.getKey());
                                if (tempUser.getAccountStatus() != (Boolean) snap.getValue()){
                                    //delete local record, to rewrite new record
                                    file.delete();
                                    AccountFetcher accountFetcher = new AccountFetcher(snap.getKey(), activity);
                                    accountFetcher.execute();
                                }
                            }
                            else {
                                AccountFetcher accountFetcher = new AccountFetcher(snap.getKey(), activity);
                                accountFetcher.execute();
                            }
                            list.add("account-" + snap.getKey());
                        }
                        //delete local copy of deleted accounts
                        cleanLocalRecord(list, "account-");
                    }
                    catch (Exception e){
                        Log.d("AC-dIB", e.getMessage());
                    }
                    AdminData.populateAccounts(activity);
                    updateAccountList();
                    sendBroadcast();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    AdminData.populateAccounts(activity);
                    sendBroadcast();
                }
            });
        }
        catch (Exception e){
            Log.d("AC-dIB", "RTDB error");
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
                Log.d("AC-cLR", e.getMessage());
            }
        }

    }

    private void updateAccountList(){
        Intent intent = new Intent("update-account-list");
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }

    private void sendBroadcast(){
        Intent intent = new Intent("AC-done");
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }
}