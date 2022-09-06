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
import com.silong.Object.AppointmentRecords;
import com.silong.admin.AdminData;
import com.silong.admin.R;

public class AppointmentTagger extends MaterialAlertDialogBuilder {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    public AppointmentTagger(@NonNull Activity activity, String userID, String name) {
        super((Context) activity);
        Context context = (Context) activity;

        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setTitle(name);
        super.setMessage("Tag this application as done?\n");

        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
        mReference = mDatabase.getReference().child("adoptionRequest").child(userID).child("status");

        super.setPositiveButton(Html.fromHtml("<b>"+"YES"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mReference.setValue("5");
                Toast.makeText(activity, "Appointment confirmed!", Toast.LENGTH_SHORT).show();
                for (AppointmentRecords ap : AdminData.appointments){
                    if (ap.getUserID().equals(userID))
                        AdminData.appointments.remove(ap);
                }
                activity.onBackPressed();
            }
        });
        super.setNegativeButton(Html.fromHtml("<b>"+"NO"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //codes here
            }
        });
    }
}
