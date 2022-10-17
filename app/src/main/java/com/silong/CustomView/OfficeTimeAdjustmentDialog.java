package com.silong.CustomView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.silong.admin.R;


public class OfficeTimeAdjustmentDialog extends MaterialAlertDialogBuilder {

    Context context;
    Activity activity;

    public TextView officeTimeFrom;
    public TextView officeTimeTo;

    public OfficeTimeAdjustmentDialog(@NonNull Activity activity, String from, String to){
        super((Context) activity);

        this.activity = activity;
        context = (Context) activity;

        LayoutInflater inflater = activity.getLayoutInflater();
        View content = inflater.inflate(R.layout.office_time_picker,null);

        officeTimeFrom = content.findViewById(R.id.officeTimeFrom);
        officeTimeTo = content.findViewById(R.id.officeTimeTo);

        officeTimeFrom.setText(from);
        officeTimeTo.setText(to);

        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setView(content);
        super.setCancelable(false);

        super.setPositiveButton(Html.fromHtml("<b>"+"SAVE"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent("update-time");
                intent.putExtra("timeFrom", officeTimeFrom.getText().toString());
                intent.putExtra("timeTo", officeTimeTo.getText().toString());
                LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
            }
        });

        super.setNegativeButton(Html.fromHtml("<b>"+"CANCEL"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //code here
            }
        });
    }

}
