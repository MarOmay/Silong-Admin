package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.silong.Adapter.LogAdapter;
import com.silong.Object.LogData;

public class Log extends AppCompatActivity {

    ImageView logBackIv;
    RecyclerView logsRecycler;
    ImageView logDateRange;
    Button logsDownloadBtn, logsSendemailBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        getSupportActionBar().hide();

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        logBackIv = findViewById(R.id.logBackIv);
        logsRecycler = findViewById(R.id.logsRecycler);
        logDateRange = findViewById(R.id.logDateRange);
        logsDownloadBtn = findViewById(R.id.logsDownloadBtn);
        logsSendemailBtn = findViewById(R.id.logsSendemailBtn);

        logsRecycler.setHasFixedSize(true);
        logsRecycler.setLayoutManager(new LinearLayoutManager(Log.this));

        loadData();

    }

    public void loadData(){
        LogData[] logData = new LogData[]{
                new LogData("07/24/1999", "Pinanganak si jepoy."),
                new LogData("04/14/2003", "What the freak."),
                new LogData("11/02/2001", "Hell no."),
                new LogData("01/11/2019", "Hehe."),
                new LogData("12/25/2022", "Hbd lawd."),
                new LogData("07/24/1999", "Pinanganak si jepoy."),
                new LogData("04/14/2003", "What the freak."),
                new LogData("11/02/2001", "Hell no."),
                new LogData("01/11/2019", "Hehe."),
                new LogData("12/25/2022", "Hbd lawd."),
                new LogData("07/24/1999", "Pinanganak si jepoy."),
                new LogData("04/14/2003", "What the freak."),
                new LogData("11/02/2001", "Hell no."),
                new LogData("01/11/2019", "Hehe."),
                new LogData("12/25/2022", "Hbd lawd.")
        };

        LogAdapter logAdapter = new LogAdapter(logData, Log.this);
        logsRecycler.setAdapter(logAdapter);
    }

    public void back(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    public void onDateRangePressed(View view){
        Intent i = new Intent(Log.this, DateRangePicker.class);
        startActivity(i);
    }
}