package com.silong.CustomView;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.admin.R;

public class AppointmentReqDialog extends MaterialAlertDialogBuilder {

    public AppointmentReqDialog(@NonNull Context context, String string) {
        super(context);
        super.setTitle(Html.fromHtml("<b>"+"Appointment"+"</b>"));
        super.setIcon(context.getDrawable(R.drawable.req_appointment_icon));
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setMessage(string);

        super.setPositiveButton(Html.fromHtml("<b>"+"ACCEPT"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //codes here
            }
        });
        super.setNegativeButton(Html.fromHtml("<b>"+"DECLINE"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //codes here
            }
        });
    }
}
