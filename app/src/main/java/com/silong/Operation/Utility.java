package com.silong.Operation;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.silong.admin.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {

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

}
