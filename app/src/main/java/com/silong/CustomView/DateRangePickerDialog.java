package com.silong.CustomView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.admin.R;

public class DateRangePickerDialog extends MaterialAlertDialogBuilder {

    public DateRangePickerDialog (@NonNull Activity activity){
        super((Context) activity);
        Context context = (Context) activity;
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setView(R.layout.date_range_picker_dialog);

        super.setPositiveButton(Html.fromHtml("<b>"+"APPLY"+"</b>"), new DialogInterface.OnClickListener() {
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
