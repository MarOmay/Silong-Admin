package com.silong.admin;

import static com.silong.Operation.Utility.dateToday;
import static com.silong.Operation.Utility.timeNow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.silong.Adapter.LogAdapter;
import com.silong.CustomView.DateRangeFromPicker;
import com.silong.CustomView.DateRangePickerDialog;
import com.silong.CustomView.DateRangeToPicker;
import com.silong.Object.LogData;
import com.silong.Operation.Spreadsheet;
import com.silong.Operation.Utility;
import com.silong.Task.LogsFetcher;

import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class Log extends AppCompatActivity {

    ImageView logBackIv;
    RecyclerView logsRecycler;
    ImageView logDateRange;
    Button logsDownloadBtn, logsSendemailBtn;

    public static ArrayList<LogData> LOGDATA = new ArrayList<>();
    public static ArrayList<LogData> EXPORTABLE = new ArrayList<>();

    public static String dateFrom = Utility.dateToday().replace("-","/");
    public static String dateTo = Utility.dateToday().replace("-","/");

    public static boolean customDate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        getSupportActionBar().hide();

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        LOGDATA.clear();
        customDate = false;

        //register receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mReloadReceiver, new IntentFilter("refresh-logs"));

        logBackIv = findViewById(R.id.logBackIv);
        logsRecycler = findViewById(R.id.logsRecycler);
        logDateRange = findViewById(R.id.logDateRange);
        logsDownloadBtn = findViewById(R.id.logsDownloadBtn);
        logsSendemailBtn = findViewById(R.id.logsSendemailBtn);

        logsRecycler.setHasFixedSize(true);
        logsRecycler.setLayoutManager(new LinearLayoutManager(Log.this));

        LogsFetcher logsFetcher = new LogsFetcher(Log.this);
        logsFetcher.execute();

        loadData();

    }

    public void onDateRangePressed(View view){
        DateRangePickerDialog drpd = new DateRangePickerDialog(Log.this);
        drpd.show();

        DateRangeFromPicker drfp = new DateRangeFromPicker(Log.this, drpd);
        DateRangeToPicker drtp = new DateRangeToPicker(Log.this, drpd);

        drpd.fromET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drfp.show(getSupportFragmentManager(), null);

            }
        });

        drpd.toET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drtp.show(getSupportFragmentManager(), null);

            }
        });

    }

    public void loadData(){

        if (LOGDATA.isEmpty()){
            Utility.log("Log.LoadData: No logs to be displayed");
            return;
        }

        try {

            //sort by date and time
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LOGDATA.sort(new Comparator<LogData>() {
                    @Override
                    public int compare(LogData logData, LogData t1) {
                        String dt1 = logData.getDate()+logData.getTime();
                        String dt2 = t1.getDate()+t1.getTime();
                        return dt1.compareTo(dt2);
                    }
                });
            }

            int listSize = LOGDATA.size();

            LogData[] logData = new LogData[listSize];

            for (int i = 0; i < listSize; i++){
                logData[i] = LOGDATA.get(listSize-(i+1));
            }

            LogAdapter logAdapter = new LogAdapter(logData, Log.this);
            logsRecycler.setAdapter(logAdapter);


        }
        catch (Exception e){
            Toast.makeText(this, "Can't display logs.", Toast.LENGTH_SHORT).show();
            Utility.log("Log.loadData: " + e.getMessage());
        }

    }

    public void onPressedDownload(View view){
        if (EXPORTABLE.isEmpty()){
            Toast.makeText(this, "Nothing to export", Toast.LENGTH_SHORT).show();
            return;
        }

        Spreadsheet spreadsheet = toSpreadsheet();

        if (spreadsheet != null){

            String filename = "Logs" + "-" + dateToday() + "-" + timeNow().replace("*", "") + ".xls";
            boolean success = spreadsheet.writeToFile(filename, false);

            if (success)
                Toast.makeText(Log.this, "Exported to Documents/Silong/"+filename, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(Log.this, "Export failed", Toast.LENGTH_SHORT).show();

        }
        else {
            Toast.makeText(this, "Failed to export logs", Toast.LENGTH_SHORT).show();
            Utility.log("Log.oPD: Failed to export logs");
        }
    }

    public void onPressedEmail(View view){
        if (EXPORTABLE.isEmpty()){
            Toast.makeText(this, "Nothing to export", Toast.LENGTH_SHORT).show();
            return;
        }

        Spreadsheet spreadsheet = toSpreadsheet();

        if (spreadsheet != null){
            spreadsheet.sendAsEmail("Logs");
        }
        else {
            Toast.makeText(this, "Failed to export logs", Toast.LENGTH_SHORT).show();
            Utility.log("Log.oPE: Failed to export logs");
        }
    }

    private Spreadsheet toSpreadsheet(){

        try {
            ArrayList<Object[]> entries = new ArrayList<>();
            //labels
            entries.add(new Object[]{"Date", "Time", "Email", "Description", "Device Maker", "Device Model"});

            for (LogData data : EXPORTABLE){
                String[] entry = new String[6];

                entry[0] = data.getDate();
                entry[1] = data.getTime();
                entry[2] = data.getEmail();
                entry[3] = data.getDescription();
                entry[4] = data.getDeviceMaker();
                entry[5] = data.getDeviceModel();

                entries.add(entry);
            }

            Spreadsheet spreadsheet = new Spreadsheet(Log.this);
            spreadsheet.setEntries(entries);

            Workbook workbook = spreadsheet.create();

            return spreadsheet;
        }
        catch (Exception e){
            Utility.log("Log.oPD: " + e.getMessage());
        }

        return null;
    }


    private BroadcastReceiver mReloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            loadData();

        }
    };

    public void back(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReloadReceiver);
        super.onDestroy();
    }
}