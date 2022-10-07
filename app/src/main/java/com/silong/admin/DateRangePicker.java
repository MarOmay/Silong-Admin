package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.silong.CustomView.DateRangeFromPicker;
import com.silong.CustomView.DateRangeToPicker;

public class DateRangePicker extends AppCompatActivity {

    EditText dateRangeFrom, dateRangeTo;
    TextView applyRangeBtn, cancelRangeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_range_picker);
        getSupportActionBar().hide();

        dateRangeFrom = findViewById(R.id.dateRangeFrom);
        dateRangeTo = findViewById(R.id.dateRangeTo);
        applyRangeBtn = findViewById(R.id.applyRangeBtn);
        cancelRangeBtn = findViewById(R.id.cancelRangeBtn);
    }

    public void onPressedDateRangeFrom(View view){
        DialogFragment newFragment =  new DateRangeFromPicker(DateRangePicker.this);
        newFragment.show(getSupportFragmentManager(), "dateFromPicker");
    }

    public void onPressedDateRangeTo(View view){
        DialogFragment newFragment =  new DateRangeToPicker(DateRangePicker.this);
        newFragment.show(getSupportFragmentManager(), "dateToPicker");
    }

    public void onPressedCancel(View view){
        this.finish();
    }
}