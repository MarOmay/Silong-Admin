package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.silong.CustomView.DateRangePickerDialog;
import com.silong.CustomView.OfficeTimeAdjustmentDialog;
import com.silong.CustomView.TimeRangeFromPicker;
import com.silong.CustomView.TimeRangeToPicker;

import java.util.List;

public class OfficeSchedule extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_schedule);
        getSupportActionBar().hide();
        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        listView = findViewById(R.id.daysListView);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, getResources().getStringArray(R.array.days));
        listView.setAdapter(arrayAdapter);
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    public void onPressedAdjustTime(View view){
        OfficeTimeAdjustmentDialog otad = new OfficeTimeAdjustmentDialog(OfficeSchedule.this);
        otad.show();
    }
}