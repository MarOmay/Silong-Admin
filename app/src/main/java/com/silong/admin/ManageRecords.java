package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.card.MaterialCardView;

public class ManageRecords extends AppCompatActivity {

    ImageView recordsBackIv;
    LinearLayout recordsAddTile, recordsCreateReportTile;
    RecyclerView recordsRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_records);
        getSupportActionBar().hide();

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        recordsBackIv = (ImageView) findViewById(R.id.recordsBackIv);
        recordsAddTile = (LinearLayout) findViewById(R.id.recordsAddTile);
        recordsCreateReportTile = (LinearLayout) findViewById(R.id.recordsCreateReportTile);
        recordsRecycler = (RecyclerView) findViewById(R.id.recordsRecycler);

        recordsRecycler.setHasFixedSize(true);
        recordsRecycler.setLayoutManager(new LinearLayoutManager(ManageRecords.this));

        //Array for accounts
        PetRecordsData[] petRecordsData = new PetRecordsData[]{
                new PetRecordsData("Male Dog", "Young", "Medium", "Brown, Cream", R.drawable.sample1),
                new PetRecordsData("Female Dog", "Old", "Small", "Pink, Cream", R.drawable.sample2),
                new PetRecordsData("Male Dog", "Young", "Large", "Brown, Black", R.drawable.sample3),
                new PetRecordsData("Male Cat", "Young", "Small", "Cookies, Cream", R.drawable.sample4),
                new PetRecordsData("Female Cat", "Young", "Gigantic", "Blue, Cream", R.drawable.sample5),
                new PetRecordsData("Male Dog", "Young", "Medium", "Brown, Cream", R.drawable.sample1),
                new PetRecordsData("Female Dog", "Old", "Small", "Pink, Cream", R.drawable.sample2),
                new PetRecordsData("Male Dog", "Young", "Large", "Brown, Black", R.drawable.sample3),
                new PetRecordsData("Male Cat", "Young", "Small", "Cookies, Cream", R.drawable.sample4),
                new PetRecordsData("Female Cat", "Young", "Gigantic", "Blue, Cream", R.drawable.sample5)
        };

        RecordsAdapter recordsAdapter = new RecordsAdapter(petRecordsData, ManageRecords.this);
        recordsRecycler.setAdapter(recordsAdapter);
    }

    public void onAddRecordPressed(View view){
        Intent i = new Intent(ManageRecords.this, AddRecord.class);
        startActivity(i);
    }

    public void back(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}