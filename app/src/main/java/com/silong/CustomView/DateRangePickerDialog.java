package com.silong.CustomView;

import android.app.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.Operation.Utility;
import com.silong.admin.Log;
import com.silong.admin.R;

import java.util.Calendar;

public class DateRangePickerDialog extends MaterialAlertDialogBuilder {

    public String newFromDate = Log.dateFrom
            , newToDate = Log.dateTo;

    public EditText fromET;
    public EditText toET;

    private Activity activity;
    private Context context;

    private boolean changed = false;

    public DateRangePickerDialog (@NonNull Activity activity){
        super((Context) activity);
        this.activity = activity;
        context = (Context) activity;

        LayoutInflater inflater = activity.getLayoutInflater();
        View content = inflater.inflate(R.layout.date_range_picker_dialog,null);

        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setView(content);

        fromET = content.findViewById(R.id.dateRangeFrom);
        toET = content.findViewById(R.id.dateRangeTo);

        refresh();

        super.setPositiveButton(Html.fromHtml("<b>"+"APPLY"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //check if dates are valid
                String[] fromS = newFromDate.split("/");
                Calendar from = Calendar.getInstance();
                from.set(Integer.valueOf(fromS[2]),Integer.valueOf(fromS[0]),Integer.valueOf(fromS[1]));

                String[] toS = newToDate.split("/");
                Calendar to = Calendar.getInstance();
                to.set(Integer.valueOf(toS[2]),Integer.valueOf(toS[0]),Integer.valueOf(toS[1]));

                if (from.after(to)){
                    Toast.makeText(activity, "Invalid date", Toast.LENGTH_SHORT).show();
                    Utility.log("DRPD: Invalid date");
                }
                else {
                    //set new values
                    Log.dateFrom = newFromDate;
                    Log.dateTo = newToDate;
                    Log.customDate = true;
                    LocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent("refresh-logs"));
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


    public void refresh(){

        fromET.setText(newFromDate);
        toET.setText(newToDate);

    }

    public String getNewFromDate() {
        return newFromDate;
    }

    public void setNewFromDate(String newFromDate) {
        this.newFromDate = newFromDate;
    }

    public String getNewToDate() {
        return newToDate;
    }

    public void setNewToDate(String newToDate) {
        this.newToDate = newToDate;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }


}
