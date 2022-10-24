package com.silong.CustomView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;

import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DateRangeFromPicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private Context context;
    private DateRangePickerDialog drpd;
    int yearz = 2022, monthz = 0, dayz = 1;

    public DateRangeFromPicker(Context context, DateRangePickerDialog drpd){
        this.context = context;
        this.drpd = drpd;
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

        if (selectedDate.equals(drpd.newFromDate))
            return;

        drpd.setNewFromDate(selectedDate);
        drpd.refresh();
        drpd.setChanged(true);
    }
}
