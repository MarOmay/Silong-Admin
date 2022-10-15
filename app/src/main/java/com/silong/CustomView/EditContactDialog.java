package com.silong.CustomView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.Operation.InputValidator;
import com.silong.admin.R;

public class EditContactDialog extends MaterialAlertDialogBuilder {

    public EditContactDialog(@NonNull Context context, String string){
        super(context);
        super.setTitle(Html.fromHtml("<b>"+string+"</b>"));
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));

        LinearLayout editText_layout = new LinearLayout(context);
        editText_layout.setOrientation(LinearLayout.VERTICAL);
        editText_layout.setVerticalGravity(10);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(60,30,60,0);
        EditText editText = new EditText(context);
        editText.setBackground(context.getResources().getDrawable(R.drawable.tf_background));
        editText.setPadding(30,0,0,0);
        editText.setHint(string);
        editText.setTextSize(14);
        editText.setLayoutParams(params);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText_layout.addView(editText);
        super.setView(editText_layout);

        super.setPositiveButton(Html.fromHtml("<b>"+"SAVE"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // code here
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
