package com.silong.CustomView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.admin.R;

public class DeactivationDialog extends MaterialAlertDialogBuilder {

    public DeactivationDialog(@NonNull Context context, String string) {
        super(context);
        super.setTitle(Html.fromHtml("<b>"+"Deactivate?"+"</b>"));
        super.setIcon(context.getDrawable(R.drawable.circlelogo_gradient));
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setMessage("This will deactivate " +string+"\'s account. Do you want to proceed?\n");
        super.setPositiveButton(Html.fromHtml("<b>"+"YES"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent("deactivate-user");
                intent.putExtra("deactivate", true);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });
        super.setNegativeButton(Html.fromHtml("<b>"+"CANCEL"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent("deactivate-user");
                intent.putExtra("deactivate", false);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });
    }
}
