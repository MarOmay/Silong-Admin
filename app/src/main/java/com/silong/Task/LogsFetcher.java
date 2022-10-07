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
import com.silong.Object.LogData;
import com.silong.Operation.Utility;
import com.silong.admin.Log;

public class LogsFetcher extends AsyncTask {

    private Activity activity;

    public LogsFetcher(Activity activity){
        this.activity = activity;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        try {

            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
            DatabaseReference mReference = mDatabase.getReference("adminLogs");

            mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    //read and format each log
                    for (DataSnapshot ds : snapshot.getChildren()){

                        String[] key = ds.getKey().split("--");
                        String date = key[0].replace("-", "/");
                        String time = key[1].replace("*", ":");

                        String[] value = ds.getValue().toString().split(Utility.LOG_SEPARATOR);
                        String email = value[0];
                        String desc = value[1];
                        String maker = value[2];
                        String model = value[3];

                        LogData tempLog = new LogData();
                        tempLog.setDate(date);
                        tempLog.setTime(time);
                        tempLog.setEmail(email);
                        tempLog.setDescription(desc);
                        tempLog.setDeviceMaker(maker);
                        tempLog.setDeviceModel(model);

                        Log.LOGDATA.add(tempLog);

                    }

                    //refresh list
                    sendBroadcast();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        catch (Exception e){
            Utility.log("LogsFetcher.dIB: " + e.getMessage());
        }

        return null;
    }

    private void sendBroadcast(){
        Intent intent = new Intent("refresh-logs");
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }
}
