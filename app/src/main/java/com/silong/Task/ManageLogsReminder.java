package com.silong.Task;

import android.app.Activity;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.Operation.Utility;
import com.silong.admin.AdminData;
import com.silong.admin.Dashboard;

public class ManageLogsReminder extends AsyncTask {

    private Activity activity;

    public ManageLogsReminder(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        try {
            //get schedule
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
            DatabaseReference mRef = mDatabase.getReference("publicInformation").child("databaseMaintenanceSchedule");
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        if (snapshot.getValue() != null){
                            String today = Utility.dateToday();
                            String sched = snapshot.getValue().toString();

                            if (!today.equals(sched)){
                                Dashboard.mlReminder = true;
                                Dashboard.checkCompletion();
                                AdminData.DATABASE_MAINTENANCE = false;
                                AdminData.DATABASE_MAINTENANCE_DATE = sched;
                                return;
                            }

                            AdminData.DATABASE_MAINTENANCE = true;
                            AdminData.DATABASE_MAINTENANCE_DATE = sched;

                            //check permission
                            DatabaseReference permission = mDatabase.getReference("Admins").child(AdminData.adminID).child("roles").child("manageDatabase");
                            permission.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    Dashboard.mlReminder = true;
                                    Dashboard.checkCompletion();

                                    if (snapshot.getValue() != null){
                                        boolean allowed = (boolean) snapshot.getValue();
                                        if (allowed){
                                            //show notification
                                            new Utility().showNotification(activity, "Database Maintenance", "Please free up some space from the database.\nGenerate report before deleting data.");
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Dashboard.mlReminder = true;
                                    Dashboard.checkCompletion();
                                    Utility.log("MLR.dIB.oDC.oC: " + error.getMessage());
                                }
                            });
                        }
                    }
                    catch (Exception e){
                        Dashboard.mlReminder = true;
                        Dashboard.checkCompletion();
                        Utility.log("MLR.dIB:.oDC " + e.getMessage());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Dashboard.mlReminder = true;
                    Dashboard.checkCompletion();
                    Utility.log("MLR.dIB.oc: " + error.getMessage());
                }
            });
        }
        catch (Exception e){
            Dashboard.mlReminder = true;
            Dashboard.checkCompletion();
            Utility.log("MLR.dIB: " + e.getMessage());
        }

        return null;
    }
}
