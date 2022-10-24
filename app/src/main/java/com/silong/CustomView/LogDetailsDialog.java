package com.silong.CustomView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.Object.LogData;
import com.silong.admin.R;

public class LogDetailsDialog extends MaterialAlertDialogBuilder {

    public LogDetailsDialog(@NonNull Activity activity, LogData logData){
        super((Context) activity);
        Context context = (Context) activity;
        super.setTitle(Html.fromHtml("<b>"+"Log Details"+"</b>"));
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));

        String message = "Date: " + logData.getDate() + " " + logData.getTime();
        message += "\nActioned by " + logData.getEmail();
        message += "\nusing " + logData.getDeviceMaker() + " " + logData.getDeviceModel();
        message += "\n\nDescription:";
        message += "\n" + logData.getDescription();

        super.setMessage(message);

        super.setPositiveButton(Html.fromHtml("<b>" + "DISMISS" + "</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //code here
            }
        });
    }
}
