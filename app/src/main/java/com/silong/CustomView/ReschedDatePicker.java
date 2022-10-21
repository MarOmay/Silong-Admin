package com.silong.CustomView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class ReschedDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private Context context;
    private RescheduleDialog rd;

    public ReschedDatePicker(Context context, RescheduleDialog rd){
        this.context = context;
        this.rd = rd;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String[] date = rd.reschedDate.getText().toString().split("/");

        int year = Integer.valueOf(date[2]);
        int month = Integer.valueOf(date[0]);
        int day = Integer.valueOf(date[1]);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);

        return  dialog;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        String selectedDate = month+1 + "/" + day + "/" + year;

        if (selectedDate.equals(rd.reschedDate.getText().toString()))
            return;

        rd.reschedDate.setText(selectedDate);
    }
}
