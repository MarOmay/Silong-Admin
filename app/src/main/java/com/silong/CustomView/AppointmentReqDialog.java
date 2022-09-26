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

        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
        mReference = mDatabase.getReference().child("adoptionRequest").child(userID).child("status");

        super.setPositiveButton(Html.fromHtml("<b>"+"ACCEPT"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //update RTDB
                mReference.setValue("4");

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
                mReference.setValue("2");
                String email = AdminData.getUser(userID).getEmail();
                Utility.dbLog("Declined appointment for " + email);
                Toast.makeText(activity, "Appointment declined!", Toast.LENGTH_SHORT).show();
                activity.onBackPressed();
            }
        });
    }

}
