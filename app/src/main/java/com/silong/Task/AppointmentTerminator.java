package com.silong.Task;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.silong.EnumClass.PetStatus;
import com.silong.Object.Adoption;
import com.silong.Object.AppointmentRecords;
import com.silong.Object.User;
import com.silong.Operation.EmailNotif;
import com.silong.Operation.Utility;

import java.util.HashMap;
import java.util.Map;

public class AppointmentTerminator extends AsyncTask {

    private User user;
    private AppointmentRecords appointment;

    public AppointmentTerminator(User user, AppointmentRecords appointment){
        this.user = user;
        this.appointment = appointment;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        Map<String, Object> multiNodeMap = new HashMap<>();
        multiNodeMap.put("Pets/"+appointment.getPetId()+"/status", PetStatus.ACTIVE);
        multiNodeMap.put("recordSummary/"+appointment.getPetId(), PetStatus.ACTIVE);
        multiNodeMap.put("adoptionRequest/"+user.userID, null);
        multiNodeMap.put("Users/"+user.userID+"/adoptionHistory/"+appointment.getPetId()+"/dateRequested", appointment.getDateRequested());
        multiNodeMap.put("Users/"+user.userID+"/adoptionHistory/"+appointment.getPetId()+"/status", -1);

        mDatabase.getReference().updateChildren(multiNodeMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Adoption adoption = new Adoption();
                        adoption.setAppointmentDate(appointment.getDateTime());
                        adoption.setPetID(Integer.valueOf(appointment.getPetId()));

                        EmailNotif emailNotif = new EmailNotif(user.getEmail(), EmailNotif.APPOINTMENT_TERMINATED, adoption);
                        emailNotif.sendNotif();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Utility.log("AppointmentTerminator.dIB: " + e.getMessage());
                    }
                });


        return null;
    }
}
