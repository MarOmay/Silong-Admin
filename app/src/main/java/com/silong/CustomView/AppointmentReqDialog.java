package com.silong.CustomView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silong.Object.Adoption;
import com.silong.Operation.EmailNotif;
import com.silong.Operation.Utility;
import com.silong.admin.AdminData;
import com.silong.admin.R;

import java.util.HashMap;
import java.util.Map;

public class AppointmentReqDialog extends MaterialAlertDialogBuilder {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private Adoption ADOPTION;

    public AppointmentReqDialog(@NonNull Activity activity, String string, String userID, Adoption adoption) {
        super((Context) activity);
        Context context = (Context) activity;
        super.setTitle(Html.fromHtml("<b>"+"Appointment"+"</b>"));
        super.setIcon(context.getDrawable(R.drawable.req_appointment_icon_pink));
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setMessage(string);

        this.ADOPTION = adoption;

        Map<String, Object> multiNodeMap = new HashMap<>();
        multiNodeMap.put("adoptionRequest/"+userID+"/dateRequested", ADOPTION.getDateRequested());
        multiNodeMap.put("adoptionRequest/"+userID+"/petID", ADOPTION.getPetID());
        multiNodeMap.put("Users/"+userID+"/adoptionHistory/"+ADOPTION.getPetID()+"/dateRequested", ADOPTION.getDateRequested());

        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
        mReference = mDatabase.getReference();

        super.setPositiveButton(Html.fromHtml("<b>"+"ACCEPT"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //update RTDB
                String[] dateTime = ADOPTION.getAppointmentDate().split(" ");
                String appointmentDate = dateTime[0];
                String appointmentTime = dateTime[1].replace(":","*") + " " + dateTime[2];
                multiNodeMap.put("adoptionRequest/"+userID+"/appointmentDate", appointmentDate);
                multiNodeMap.put("adoptionRequest/"+userID+"/appointmentTime", appointmentTime);
                multiNodeMap.put("adoptionRequest/"+userID+"/status", "4");
                multiNodeMap.put("Users/"+userID+"/adoptionHistory/"+ADOPTION.getPetID()+"/status", "4");
                mReference.updateChildren(multiNodeMap);

                //send email notif
                String email = AdminData.getUser(userID).getEmail();
                EmailNotif emailNotif = new EmailNotif(email, EmailNotif.APPOINTMENT_CONFIRMED, ADOPTION);
                emailNotif.sendNotif();

                Utility.dbLog("Accepted appointment for " + email);

                Toast.makeText(activity, "Appointment confirmed!", Toast.LENGTH_SHORT).show();
                activity.onBackPressed();
            }
        });
        super.setNegativeButton(Html.fromHtml("<b>"+"DECLINE"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //update RTDB
                multiNodeMap.put("adoptionRequest/"+userID+"/appointmentDate", null);
                multiNodeMap.put("adoptionRequest/"+userID+"/appointmentTime", null);
                multiNodeMap.put("adoptionRequest/"+userID+"/status", "2");
                multiNodeMap.put("Users/"+userID+"/adoptionHistory/"+ADOPTION.getPetID()+"/status", "2");
                mReference.updateChildren(multiNodeMap);

                String email = AdminData.getUser(userID).getEmail();
                Utility.dbLog("Declined appointment for " + email);
                Toast.makeText(activity, "Appointment declined!", Toast.LENGTH_SHORT).show();
                activity.onBackPressed();
            }
        });
    }

}
