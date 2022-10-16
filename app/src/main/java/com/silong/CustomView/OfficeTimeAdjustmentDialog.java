package com.silong.CustomView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.Operation.Utility;
import com.silong.admin.Log;
import com.silong.admin.R;

import java.util.Calendar;

public class OfficeTimeAdjustmentDialog extends MaterialAlertDialogBuilder {

    Context context;
    Activity activity;

    public OfficeTimeAdjustmentDialog(@NonNull Activity activity){
        super((Context) activity);
        this.activity = activity;
        context = (Context) activity;

        LayoutInflater inflater = activity.getLayoutInflater();
        View content = inflater.inflate(R.layout.office_time_picker,null);

        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setView(content);

        super.setPositiveButton(Html.fromHtml("<b>"+"SAVE"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //code here
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
