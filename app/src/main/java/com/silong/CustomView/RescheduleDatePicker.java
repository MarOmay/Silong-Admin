package com.silong.CustomView;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.silong.admin.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

public class RescheduleDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private DatePickerDialog dpd;

    private RescheduleDialog rd;

    public void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Calendar now = Calendar.getInstance();

        dpd = DatePickerDialog.newInstance(this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        // restrict to weekdays only
        ArrayList<Calendar> weekdays = new ArrayList<Calendar>();
        Calendar day = Calendar.getInstance();
        day.add(Calendar.DAY_OF_YEAR, 2);
        for (int i = 0; i < 11; i++) {
            if (day.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && day.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                Calendar d = (Calendar) day.clone();
                weekdays.add(d);
            }
            day.add(Calendar.DATE, 1);
        }

        Calendar[] weekdayDays = weekdays.toArray(new Calendar[weekdays.size()]);
        dpd.setSelectableDays(weekdayDays);
        dpd.setAccentColor(ContextCompat.getColor(getContext(), R.color.pink));

        int nightModeFlags =
                getContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                dpd.setThemeDark(true);
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                dpd.setThemeDark(false);
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                dpd.setThemeDark(true);
                break;
        }

        dpd.show(requireFragmentManager(), "Datepickerdialog");

        dpd.setCancelable(false);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String selectedDate = monthOfYear+1 + "/" + dayOfMonth + "/" + year;

        if (selectedDate.equals(rd.reschedDate.getText().toString()))
            return;

        rd.reschedDate.setText(selectedDate);
        this.dismiss();
    }

    public RescheduleDialog getRd() {
        return rd;
    }

    public void setRd(RescheduleDialog rd) {
        this.rd = rd;
    }
}