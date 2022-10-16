package com.silong.Operation;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silong.admin.AdminData;
import com.silong.admin.R;


import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utility {

    public final static int STORAGE_REQUEST_CODE = 3;

    public static final String LOG_SEPARATOR = "#LOG-SEPARATOR#";
    public static final String CLAUSE_SEPARATOR = "#CLAUSE-SEPARATOR#";

    public static boolean internetConnection(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo!=null){
            return true;
        }
        return false;
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {

        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static String dateToday(){
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
        Date date = new Date();
        return formatter.format(date);
    }

    public static String timeNow(){
        SimpleDateFormat formatter = new SimpleDateFormat("HH*mm*ss");
        Date date = new Date();
        return formatter.format(date);
    }

    public static void log(String message){
        if (message.length() < 1)
            return;
        else
            Log.d("DEBUGGER>>>", message);
    }

    public static void log(Activity activity, String log){
        Context context = (Context) activity;
        //Check if file exists
        File file = new File(context.getFilesDir() + "/logs.dat");
        if (!file.exists()){
            try{
                FileOutputStream fileOuputStream = context.openFileOutput("logs.dat", Context.MODE_PRIVATE);
            }
            catch (Exception e){
                Log.d("LOGS.DAT", "Error writing logs");
            }
        }
        //Create local storage copy of user data
        try (FileOutputStream fileOutputStream = context.openFileOutput( "logs.dat", Context.MODE_APPEND)) {
            String data = dateToday() + "@" + timeNow() + " : " + log + "\n";
            fileOutputStream.write(data.getBytes());
            fileOutputStream.flush();
        }
        catch (Exception e){
            Log.d("Utility-log", e.getMessage());
        }
    }

    public static int getAge(String bday) {

        String [] date = bday.split("/");

        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();

        int age = 0;

        birthDate.set(Integer.valueOf(date[2]), Integer.valueOf(date[0]), Integer.valueOf(date[1]));
        if (birthDate.after(today)) {
            throw new IllegalArgumentException("Can't be born in the future");
        }

        age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        return age;
    }

    public static void animateOnClick(Context context, View view){
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.custom_on_click));
    }

    public static boolean requestPermission(Activity activity, int requestCode){

        //Check WRITE permissions
        if(ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(activity.getApplicationContext(), "Silong needs WRITE access to export file.", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
            return false;
        }

        return true;
    }

    //  NON STATIC METHODS

    public void showNotification(Context context, String title, String message) {
        //SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance(context);
        Intent intent = new Intent(context, context.getClass());
        int reqCode = 101;

        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_IMMUTABLE);
        String CHANNEL_ID = "channel_name";// The id of the channel.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.silong_admin_app_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(reqCode, notificationBuilder.build()); // 0 is the request code, it should be unique id

        Log.d("showNotification", "showNotification: " + reqCode);
    }

    public void passwordFieldTransformer(EditText field, boolean visible){
        field.setTransformationMethod(visible ? null : new PasswordTransformationMethod());
    }

    public static void dbLog(String message){
        try {

            String date = dateToday();
            String time = timeNow();

            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;

            String email = "";

            if (AdminData.adminEmail != null)
                email = AdminData.adminEmail;
            else if (AdminData.adminID != null)
                email = AdminData.adminID;
            else
                email = "EMAIL_NOT_FOUND";

            String data = email + LOG_SEPARATOR + message + LOG_SEPARATOR + manufacturer + LOG_SEPARATOR + model;

            FirebaseDatabase tempDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
            DatabaseReference tempRef = tempDatabase.getReference("adminLogs").child(date + "--" + time);

            tempRef.setValue(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            log("Utility.dbLog: Logged to RTDB");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            log("Utility.dbLog: Failed to write log to RTDB");
                        }
                    });
        }
        catch (Exception e){
            log("Utility.dbLog: " + e.getMessage());
        }

    }

}
