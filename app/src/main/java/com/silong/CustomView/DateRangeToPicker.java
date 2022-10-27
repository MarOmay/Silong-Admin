package com.silong.CustomView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;

import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DateRangeToPicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private Context context;
    private DateRangePickerDialog drpd;
    private DateRangePickerReport drpr;

    public DateRangeToPicker(Context context, DateRangePickerDialog drpd){
        this.context = context;
        this.drpd = drpd;
    }

    public DateRangeToPicker(Context context, DateRangePickerReport drpr){
        this.context = context;
        this.drpr = drpr;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        final Calendar cx = Calendar.getInstance();
        cx.set(2022, 0,1);
        long mini = (cx.getTimeInMillis());
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        dialog.getDatePicker().setMinDate(mini);
        return  dialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        String selectedDate = month+1 + "/" + day + "/" + year;

        if (drpd != null){
            if (selectedDate.equals(drpd.newToDate))
                return;
            drpd.setNewToDate(selectedDate);
            drpd.refresh();
            drpd.setChanged(true);
        }

        if (drpr != null){
            if (selectedDate.equals(drpr.newToDate))
                return;
            drpr.setNewToDate(selectedDate);
            drpr.refresh();
            drpr.setChanged(true);
        }

    }
}
