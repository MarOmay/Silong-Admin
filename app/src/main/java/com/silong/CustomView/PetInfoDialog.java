package com.silong.CustomView;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.admin.R;

public class PetInfoDialog extends MaterialAlertDialogBuilder {

    public PetInfoDialog(@NonNull Context context){
        super(context);

        super.setView(R.layout.pet_info_dialog);

        super.setPositiveButton(Html.fromHtml("<b>" + "EDIT" + "</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //code here
            }
        });

        super.setNegativeButton(Html.fromHtml("<b>" + "CLOSE" + "</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //code here
            }
        });
    }
}