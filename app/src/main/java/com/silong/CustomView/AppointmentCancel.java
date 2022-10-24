package com.silong.CustomView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.Object.Adoption;
import com.silong.admin.R;

public class AppointmentCancel extends MaterialAlertDialogBuilder {

    private Activity activity;
    private Adoption adoption;
    private String userID;

    public AppointmentCancel(@NonNull Activity activity, String userID, Adoption adoption) {
        super(activity);

        super.setTitle("Forfeit application?");
        super.setMessage("This will cancel the adoption request.\nCancel this request?");
        super.setBackground(activity.getResources().getDrawable(R.drawable.dialog_bg));

        super.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent("cancel-request");
                intent.putExtra("userID", userID);
                intent.putExtra("petID", String.valueOf(adoption.getPetID()));
                intent.putExtra("date", adoption.getAppointmentDate());
                LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
            }
        });

        super.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
    }
}
