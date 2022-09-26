package com.silong.CustomView;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.admin.R;

public class ExportDataDialog extends MaterialAlertDialogBuilder {

    public ExportDataDialog(@NonNull Context context) {
        super(context);
        super.setTitle(Html.fromHtml("<b>"+"Export Data"+"</b>"));
        super.setIcon(context.getDrawable(R.drawable.req_appointment_icon));
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setMessage("Do you want to export this graph into a spreadsheet?");

        super.setPositiveButton(Html.fromHtml("<b>"+"EXPORT"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //codes here
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
