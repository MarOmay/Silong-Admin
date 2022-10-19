package com.silong.CustomView;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;



public class TimeRangeToPicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    Context context;
    private OfficeTimeAdjustmentDialog otad;

    public TimeRangeToPicker(Context context, OfficeTimeAdjustmentDialog otad){
        this.context = context;
        this.otad = otad;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String[] formatted = otad.officeTimeTo.getText().toString().split(" ");
        String[] time = formatted[0].split(":");
        int mHour = Integer.parseInt(time[0]);
        int mMinute = Integer.parseInt(time[1]);

        if (formatted[1].equals("PM")){
            mHour += 12;
        }

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

        otad.officeTimeTo.setText(selectedTime);
    }
}
