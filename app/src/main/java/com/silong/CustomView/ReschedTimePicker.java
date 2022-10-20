package com.silong.CustomView;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import com.silong.Operation.Utility;

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

        Utility.log("DateTime: " + rd.reschedTime.getText().toString());
        String[] formatted = rd.reschedTime.getText().toString().split(" ");
        String[] time = formatted[0].split(":");
        int mHour = Integer.parseInt(time[0]);
        int mMinute = Integer.parseInt(time[1]);

        if (formatted[1].equals("PM") && mHour != 12){
            mHour += 12;
        }

        Utility.log("hour: " + mHour);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, mHour, mMinute, false);
        return  timePickerDialog;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        //calculate 24hr format
        String am_pm = "";
        if (hour >= 12){
            am_pm = "PM";
            if (hour > 12)
                hour -= 12;
        }
        else
            am_pm = "AM";

        if (hour == 0 )
            hour = 12;

        String selectedTime = hour + ":" + (minute < 10 ? ("0" + minute) : minute) + " " + am_pm;

        rd.reschedTime.setText(selectedTime);
    }
}
