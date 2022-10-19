package com.silong.CustomView;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class ReschedTimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    Context context;
    private RescheduleDialog rd;

    public ReschedTimePicker(Context context, RescheduleDialog rd){
        this.context = context;
        this.rd = rd;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int mHour = Calendar.HOUR_OF_DAY;
        int mMinute = Calendar.MINUTE;

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, mHour, mMinute, false);
        return  timePickerDialog;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        //code here
    }
}
