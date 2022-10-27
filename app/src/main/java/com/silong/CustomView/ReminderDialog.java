package com.silong.CustomView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.admin.R;

public class ReminderDialog extends MaterialAlertDialogBuilder {

    private Activity activity;
    private Context context;

    public ReminderDialog (@NonNull Activity activity){
        super((Context) activity);
        this.activity = activity;
        context = (Context) activity;

        LayoutInflater inflater = activity.getLayoutInflater();
        View content = inflater.inflate(R.layout.reminder_dialog,null);

        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setView(content);

        super.setPositiveButton(Html.fromHtml("<b>"+"SET"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //code here
            }
        });

        super.setNegativeButton(Html.fromHtml("<b>"+"CANCEL"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //codes here
            }
        });
    }
}
