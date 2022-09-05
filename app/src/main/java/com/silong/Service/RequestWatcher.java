package com.silong.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.Operation.Utility;
import com.silong.admin.LogIn;
import com.silong.admin.Splash;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class RequestWatcher extends Service {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("RequestWatcher", "Service started.");
        FirebaseApp.initializeApp(this);
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
        mReference = mDatabase.getReference().child("accountStatusRequests");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try {
                    int counter = 0;
                    for (DataSnapshot snap : snapshot.getChildren())
                        counter++;

                    if (counter > 0){
                        new Utility().showNotification(getApplicationContext(), "Silong Admin", "A new request just popped in.");
                    }
                    Log.d("RequestWatcher", "Requests: " + counter);
                }
                catch (Exception e){
                    Log.d("RequestWatcher", "RequestWatcher encountered an error.\n" + e.getMessage());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("RequestWatcher", "RequestWatcher encountered an error.\n" + error.getMessage());
            }
        });

        watchAdminData();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (isLoggedIn())
            startWatching();
        else
            super.onDestroy();
        Log.d("RequestWatcher", "Service destroyed.");
    }

    private void watchAdminData(){
        try{
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (!isLoggedIn()){
                        Log.d("RequestWatcher", "User has been logged out.");
                        onDestroy();
                    }
                    else {
                        Log.d("RequestWatcher", "User is still active.");
                    }
                }
            }, 5000, 60000); //run every 60 seconds
        }
        catch (Exception e){
            Log.d("RequestWatcher", "RequestWatcher encountered an error.\n" + e.getMessage());
        }
    }

    private boolean isLoggedIn(){
        return new File(getFilesDir(),"user.dat").exists();
    }

    public void startWatching(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                startService(new Intent(RequestWatcher.this, RequestWatcher.class));
            }
        });
        thread.run();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
