package com.silong.CustomView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.admin.R;

public class ImageDialog extends MaterialAlertDialogBuilder {

    ImageView imageDialogPic;

    public ImageDialog(@NonNull Activity activity) {

        super((Context) activity);
        super.setBackground(getContext().getDrawable(R.drawable.dialog_bg));

        LayoutInflater inflater = activity.getLayoutInflater();
        View content = inflater.inflate(R.layout.image_dialog_layout, null);
        imageDialogPic = content.findViewById(R.id.imageDialogPic);

        imageDialogPic.setImageDrawable(getContext().getDrawable(R.drawable.dahyun));

        super.setView(content);

        super.setPositiveButton(Html.fromHtml("<b>" + "CLOSE" + "</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //code here
            }
        });
    }
}
