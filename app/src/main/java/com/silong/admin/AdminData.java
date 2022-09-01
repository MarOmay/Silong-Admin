package com.silong.admin;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.TextView;

import com.silong.CustomView.LoadingDialog;
import com.silong.Object.Pet;
import com.silong.Object.Request;
import com.silong.Object.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;

public class AdminData {

    public static String adminID;
    public static String firstName;
    public static String lastName;
    public static String adminEmail;
    public static String contact;

    public static ArrayList<User> users = new ArrayList<User>();
    public static ArrayList<String> uidList = new ArrayList<>();
    public static ArrayList<Pet> pets = new ArrayList<>();

    public static ArrayList<Request> requests = new ArrayList<>();

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
            try{
                Log.d("AdminData", e.getMessage());
            }
            catch (Exception ex){
                //For some reason this causes the app to crash, so I had to enclose Log in another try-catch
            }
        }
    }

    public static void populateAccounts(Activity activity){
        //Clear current ArrayList to avoid duplicate entries
        users.clear();

        LoadingDialog loadingDialog = new LoadingDialog(activity);
        loadingDialog.startLoadingDialog();

        /* Fetch info from APP-SPECIFIC file, then populate static variables */

        try{
            ArrayList<File> accounts = new ArrayList<File>();

            for (File file : activity.getFilesDir().listFiles()){
                if (file.getAbsolutePath().contains("account-")){
                    accounts.add(file);
                }
            }

            //read each account info
            for (File account : accounts){
                User user = new User();

                //Read basic info
                BufferedReader bufferedReader = new BufferedReader(new FileReader(account));
                String line;
                while ((line = bufferedReader.readLine()) != null){
                    line = line.replace(";","");

                    String [] temp = line.split(":");
                    switch (temp[0]){
                        case "userID": user.setUserID(temp[1]); break;
                        case "accountStatus": user.setAccountStatus(Boolean.parseBoolean(temp[1])); break;
                        case "firstName": user.setFirstName(temp[1]); break;
                        case "lastName": user.setLastName(temp[1]); break;
                        case "email": user.setEmail(temp[1]); break;
                        case "contact": user.setContact(temp[1]); break;
                        case "birthday" : user.setBirthday(temp[1]); break;
                        case "gender" : user.setGender(Integer.parseInt(temp[1]));
                    }

                }
                bufferedReader.close();

                //Read avatar
                try{
                    user.setPhoto(BitmapFactory.decodeFile(activity.getFilesDir() + "/avatar-" + user.getUserID()));
                }
                catch (Exception e){
                    Log.d("AdminData", e.getMessage());
                }

                users.add(user);
                loadingDialog.dismissLoadingDialog();
            }

        }
        catch (Exception e){
            Log.d("AdminData", e.getMessage());
        }
    }

    public static User getUser(String userID) {
        for (User user : AdminData.users){
            if (user.getUserID().equals(userID))
                return user;
        }
        return null;
    }

    public static void populateRecords(Activity activity){
        //Clear current ArrayList to avoid duplicate entries
        pets.clear();

        /* Fetch info from APP-SPECIFIC file, then populate static variables */

        try{
            ArrayList<File> petRecords = new ArrayList<File>();

            for (File file : activity.getFilesDir().listFiles()){
                if (file.getAbsolutePath().contains("pet-")){
                    petRecords.add(file);
                }
            }

            //read each account info
            for (File record : petRecords){
                Pet pet = new Pet();

                //Read basic info
                BufferedReader bufferedReader = new BufferedReader(new FileReader(record));
                String line;
                while ((line = bufferedReader.readLine()) != null){
                    line = line.replace(";","");

                    String [] temp = line.split(":");
                    switch (temp[0]){
                        case "petID": pet.setPetID(temp[1]); break;
                        case "status": pet.setStatus(Integer.parseInt(temp[1])); break;
                        case "type": pet.setType(Integer.parseInt(temp[1])); break;
                        case "gender": pet.setGender(Integer.parseInt(temp[1])); break;
                        case "size": pet.setSize(Integer.parseInt(temp[1])); break;
                        case "age": pet.setAge(Integer.parseInt(temp[1])); break;
                        case "color" : pet.setColor(temp[1]); break;
                        case "modifiedBy" : pet.setModifiedBy(temp[1]); break;
                        case "lastModified" : pet.setLastModified(temp[1]); break;
                    }

                }
                bufferedReader.close();

                //Read avatar
                try{
                    pet.setPhoto(BitmapFactory.decodeFile(activity.getFilesDir() + "/petpic-" + pet.getPetID()));
                }
                catch (Exception e){
                    Log.d("AdminData-pR", e.getMessage());
                }

                pets.add(pet);
            }

        }
        catch (Exception e){
            Log.d("AdminData-pR", e.getMessage());
        }
    }

    public static Pet getPet(String id) {
        for (Pet p : AdminData.pets){
            if (p.getPetID().equals(id))
                return p;
        }
        return null;
    }

    public static void writeToLocal(Context context, String filename, String desc, String content){
        //Check if file exists
        File file = new File(context.getFilesDir() + "/account-" + filename);
        if (!file.exists()){
            try{
                FileOutputStream fileOuputStream = context.openFileOutput("account-" + filename, Context.MODE_PRIVATE);
            }
            catch (Exception e){
                Log.d("AdminData-writeToLocal0", e.getMessage());
            }
        }
        //Create local storage copy of user data
        try (FileOutputStream fileOutputStream = context.openFileOutput( "account-" + filename, Context.MODE_APPEND)) {
            String data = desc + ":" + content + ";\n";
            fileOutputStream.write(data.getBytes());
            fileOutputStream.flush();
        }
        catch (Exception e){
            Log.d("AdminData-writeToLocal1", e.getMessage());
        }
    }

    public static void writePetToLocal(Context context, String filename, String desc, String content){
        //Check if file exists
        File file = new File(context.getFilesDir() + "/pet-" + filename);
        if (!file.exists()){
            try{
                FileOutputStream fileOuputStream = context.openFileOutput("pet-" + filename, Context.MODE_PRIVATE);
            }
            catch (Exception e){
                Log.d("AdminData-wPTL0", e.getMessage());
            }
        }
        //Create local storage copy of pet profile
        try (FileOutputStream fileOutputStream = context.openFileOutput( "pet-" + filename, Context.MODE_APPEND)) {
            String data = desc + ":" + content + ";\n";
            fileOutputStream.write(data.getBytes());
            fileOutputStream.flush();
        }
        catch (Exception e){
            Log.d("AdminData-wPTL1", e.getMessage());
        }
    }

    public static User fetchAccountFromLocal(Activity activity, String uid){
        User user = new User();

        try{
            File file = new File(activity.getFilesDir(), "account-" + uid);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null){
                line = line.replace(";","");

                String [] temp = line.split(":");
                switch (temp[0]){
                    case "userID": user.setUserID(temp[1]); break;
                    case "accountStatus": user.setAccountStatus(Boolean.parseBoolean(temp[1])); break;
                    case "firstName": user.setFirstName(temp[1]); break;
                    case "lastName": user.setLastName(temp[1]); break;
                    case "email": user.setEmail(temp[1]); break;
                    case "contact": user.setContact(temp[1]); break;
                    case "birthday" : user.setBirthday(temp[1]); break;
                }

            }
            bufferedReader.close();
        }
        catch (Exception e){
            Log.d("AdminData", e.getMessage());
        }

        return user;
    }

    public static Pet fetchRecordFromLocal(Activity activity, String uid){
        Pet pet = new Pet();

        try{
            File file = new File(activity.getFilesDir(), "pet-" + uid);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null){
                line = line.replace(";","");

                String [] temp = line.split(":");
                switch (temp[0]){
                    case "petID": pet.setPetID(temp[1]); break;
                    case "status": pet.setStatus(Integer.parseInt(temp[1])); break;
                    case "type": pet.setType(Integer.parseInt(temp[1])); break;
                    case "gender": pet.setGender(Integer.parseInt(temp[1])); break;
                    case "size": pet.setSize(Integer.parseInt(temp[1])); break;
                    case "age": pet.setAge(Integer.parseInt(temp[1])); break;
                    case "color" : pet.setColor(temp[1]); break;
                }

            }
            bufferedReader.close();
        }
        catch (Exception e){
            Log.d("AdminData-fRFL", e.getMessage());
        }

        return pet;
    }
}
