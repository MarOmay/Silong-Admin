package com.silong.CustomView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;


import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.silong.admin.R;

public class ClauseDeleteDialog extends MaterialAlertDialogBuilder {

    private Activity activity;
    private Context context;
    private String clauseTitle;

    public ClauseDeleteDialog(@NonNull Activity activity, String clauseTitle) {
        super((Context) activity);

        this.activity = activity;
        this.context = (Context) activity;
        this.clauseTitle = clauseTitle;

        super.setTitle("Delete this clause?");
        super.setMessage("\"" + clauseTitle + "\"");
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setCancelable(false);

        super.setPositiveButton(Html.fromHtml("<b>"+"DELETE"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent("confirm-delete");
                LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
            }
        });
        super.setNegativeButton(Html.fromHtml("<b>"+"CANCEL"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
    }
}
