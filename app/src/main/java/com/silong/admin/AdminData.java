package com.silong.admin;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class AdminData {

    public static String adminID;
    public static String firstName;
    public static String lastName;
    public static String adminEmail;
    public static String contact;

    public static void logout(){
        //Delete user-related local files
        Splash.USERDATA.delete();

        //Empty static variables
        adminID = null;
        firstName = null;
        lastName = null;
        adminEmail = null;
        contact = null;

    }

    public static boolean isLoggedIn(Context context){
        File file = new File(context.getFilesDir(), "user.dat");
        return file.exists();
    }

    public static void populate(Activity activity){
        /* Fetch info from APP-SPECIFIC file, then populate static variables */

        //Populate all String and int variables
        try{
            File userdata = Splash.USERDATA;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(userdata));
            String line;
            while ((line = bufferedReader.readLine()) != null){
                line = line.replace(";","");

                String [] temp = line.split(":");
                switch (temp[0]){
                    case "userID": adminID = temp[1]; break;
                    case "email": adminEmail = temp[1]; break;
                    case "firstName": firstName = temp[1];
                        TextView tv = activity.findViewById(R.id.adminFnameTv);
                        tv.setText(AdminData.firstName); break;
                    case "lastName": lastName = temp[1]; break;
                    case "contact": contact = temp[1]; break;
                }

            }
            bufferedReader.close();
        }
        catch (Exception e){
            Log.d("AdminData", e.getMessage());
        }
    }
}
