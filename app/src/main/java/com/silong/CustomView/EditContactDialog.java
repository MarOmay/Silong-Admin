package com.silong.CustomView;

import android.app.Activity;
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

import com.silong.admin.R;

public class EditContactDialog extends MaterialAlertDialogBuilder {

    public static final int FACEBOOK_PAGE = 0;
    public static final int EMAIL_ADDRESS = 1;
    public static final int MOBILE_NUMBER = 2;
    public static final int TELEPHONE_NUMBER = 3;

    private Activity activity;
    private Context context;

    public EditContactDialog(@NonNull Activity activity, int infoType, String currentInfo){
        super((Context) activity);

        this.activity = activity;
        this.context = (Context) activity;

        String title = "";
        switch (infoType){
            case FACEBOOK_PAGE: title = "Facebook Page"; break;
            case EMAIL_ADDRESS: title = "E-mail Address"; break;
            case MOBILE_NUMBER: title = "Cellphone Number"; break;
            case TELEPHONE_NUMBER: title = "Telephone Number"; break;
        }

        super.setTitle(Html.fromHtml("<b>"+title+"</b>"));
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
        editText.setHint(currentInfo);
        editText.setTextSize(14);
        editText.setLayoutParams(params);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText_layout.addView(editText);
        editText.setText(currentInfo);
        super.setCancelable(false);
        super.setView(editText_layout);

        super.setPositiveButton(Html.fromHtml("<b>"+"SAVE"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String data = editText.getText().toString().trim();
                if (data.length() <= 0 || data.equals(currentInfo)){
                    Toast.makeText(activity, "No changes made", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent("update-contact-info");
                    intent.putExtra("infoType", infoType);
                    intent.putExtra("newInfo", data);
                    LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
                }
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
