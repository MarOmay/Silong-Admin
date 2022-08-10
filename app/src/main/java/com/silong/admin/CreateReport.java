package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CreateReport extends AppCompatActivity {

    ImageView createReportBackIv;
    LinearLayout adoptionRecordsTile, userRecordsTile, petRecordsTile, logsTile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report);
        getSupportActionBar().hide();

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        createReportBackIv = (ImageView) findViewById(R.id.createReportBackIv);
        adoptionRecordsTile = (LinearLayout) findViewById(R.id.adoptionRecordsTile);
        userRecordsTile = (LinearLayout) findViewById(R.id.userRecordsTile);
        petRecordsTile = (LinearLayout) findViewById(R.id.petRecordsTile);
        logsTile = (LinearLayout) findViewById(R.id.logsTile);
    }

    public void back(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}