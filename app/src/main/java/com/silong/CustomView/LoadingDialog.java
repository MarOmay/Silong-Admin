package com.silong.CustomView;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
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
        builder.setCancelable(false);

        ProgressBar pBar = new ProgressBar(activity);
        pBar.setIndeterminate(true);
        pBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(251,82,139)));
        builder.setView(pBar);
        try{
            alertDialog = builder.show();
            alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        }
        catch (Exception e){
            Log.d("LoadingDialog", e.getMessage());
        }
    }

    //to dismiss the dialog
    public void dismissLoadingDialog() {
        try{
            alertDialog.dismiss();
        }
        catch (Exception e){
            Log.d("LoadingDialog", e.getMessage());
        }
    }
}
