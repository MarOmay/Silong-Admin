package com.silong.CustomView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class RescuedDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private Context context;
    private EditText editText;

    public RescuedDatePicker(Context context, EditText editText){
        this.editText = editText;
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int year, month, day;
        if (editText.getText().toString().isEmpty()){
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }
        else {
            String[] date = editText.getText().toString().split("/");
            year = Integer.parseInt(date[2]);
            month = Integer.parseInt(date[0]);
            day = Integer.parseInt(date[1]);
        }

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        return  dialog;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        String selectedDate = month+1 + "/" + day + "/" + year;
        editText.setText(selectedDate);
    }
}
