package com.silong.CustomDialog;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.admin.R;

public class LoadingDialog {

    Activity activity;
    AlertDialog alertDialog;

    public LoadingDialog(Activity myActivity) {
        activity = myActivity;
    }

    //loading dialog for short waiting screens
    public void startLoadingDialog(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity, R.style.AlertDialogTheme);
        //builder.setBackground(getDrawable(activity, R.drawable.dialog_bg));
        builder.setCancelable(true);

        ProgressBar pBar = new ProgressBar(activity);
        pBar.setIndeterminate(true);
        pBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(251,82,139)));
        builder.setView(pBar);

        alertDialog = builder.show();
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
    }

    //to dismiss the dialog
    public void dismissLoadingDialog() {
        alertDialog.dismiss();
    }
}