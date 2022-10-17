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

    public DateRangeToPicker(Context context, DateRangePickerDialog drpd){
        this.context = context;
        this.drpd = drpd;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        return  dialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        String selectedDate = month+1 + "/" + day + "/" + year;

        if (selectedDate.equals(drpd.newToDate))
            return;

        drpd.setNewToDate(selectedDate);
        drpd.refresh();
        drpd.setChanged(true);
    }
}
