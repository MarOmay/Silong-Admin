package com.silong.CustomView;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.admin.R;

public class AppointmentTagger extends MaterialAlertDialogBuilder {

    public AppointmentTagger(@NonNull Context context) {
        super(context);
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setMessage("Tag as done?\n");

        super.setPositiveButton(Html.fromHtml("<b>"+"YES"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //codes here
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
