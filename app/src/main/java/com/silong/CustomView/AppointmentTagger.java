package com.silong.CustomView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silong.Object.Adoption;
import com.silong.Object.AppointmentRecords;
import com.silong.Operation.EmailNotif;
import com.silong.Operation.Utility;
import com.silong.admin.AdminData;
import com.silong.admin.AppointmentsList;
import com.silong.admin.R;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AppointmentTagger extends MaterialAlertDialogBuilder {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private Adoption ADOPTION;

    public AppointmentTagger(@NonNull Activity activity, String userID, String name, Adoption adoption) {
        super((Context) activity);
        Context context = (Context) activity;

        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setTitle(name);
        super.setMessage("Tag this application as done?\n");

        this.ADOPTION = adoption;

        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        super.setPositiveButton(Html.fromHtml("<b>"+"YES"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //update adoptionRequest
                DatabaseReference sRef = mDatabase.getReference().child("adoptionRequest").child(userID).child("status");
                sRef.setValue("5");

                //reset cancellation counter
                DatabaseReference cRef = mDatabase.getReference().child("Users").child(userID).child("cancellation");
                cRef.setValue(0);

                //send email notif
                String userEmail = AdminData.getUser(userID).getEmail();
                EmailNotif emailNotif = new EmailNotif(userEmail, EmailNotif.ADOPTION_SUCCESSFUL, ADOPTION);
                emailNotif.sendNotif();

                Toast.makeText(activity, "Appointment confirmed!", Toast.LENGTH_SHORT).show();
                for (AppointmentRecords ap : AdminData.appointments){
                    if (ap.getUserID().equals(userID))
                        AdminData.appointments.remove(ap);
                }
                activity.onBackPressed();

                Utility.dbLog("Tagged adoption as done. Client: " + name);
            }
        });
        super.setNegativeButton(Html.fromHtml("<b>"+"NO"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //codes here
            }
        });

        super.setNeutralButton(Html.fromHtml("<b>" + "RESCHEDULE" + "</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent = new Intent("reschedule-trigger");
                intent.putExtra("userID", userID);
                intent.putExtra("adoption", (Serializable) adoption);
                LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);

            }
        });
    }
}
