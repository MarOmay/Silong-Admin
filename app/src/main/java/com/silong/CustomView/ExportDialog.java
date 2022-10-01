package com.silong.CustomView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.admin.R;

public class ExportDialog extends MaterialAlertDialogBuilder {

    public ExportDialog(@NonNull Context context) {
        super(context);
        super.setTitle(Html.fromHtml("<b>"+"Export to Spreadsheet"+"</b>"));
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setMessage("How would you like to export this report?\n");
        super.setPositiveButton(Html.fromHtml("<b>"+"Email"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //code here
            }
        });
        super.setNegativeButton(Html.fromHtml("<b>"+"Device"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //code here
            }
        });
        super.setNeutralButton(Html.fromHtml("<b>" + "Cancel" + "</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //code here
            }
        });
    }
}
