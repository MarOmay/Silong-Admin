package com.silong.CustomView;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.admin.R;

public class ResetCounterDialog extends MaterialAlertDialogBuilder {
    public ResetCounterDialog(@NonNull Context context) {
        super(context);
        super.setTitle(Html.fromHtml("<b>Reset Cancellation Counter</b>"));
        super.setMessage("Reset cancellation counter for this account?");
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));

        super.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        super.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
    }
}
