package com.silong.CustomView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.admin.R;

public class LogDetailsDialog extends MaterialAlertDialogBuilder {

    public LogDetailsDialog(@NonNull Activity activity, String string){
        super((Context) activity);
        Context context = (Context) activity;
        super.setTitle(Html.fromHtml("<b>"+"Log Details"+"</b>"));
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setMessage(string);

        super.setPositiveButton(Html.fromHtml("<b>" + "DISMISS" + "</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //code here
            }
        });
    }
}
