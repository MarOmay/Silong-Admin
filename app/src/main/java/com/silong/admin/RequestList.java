package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import com.silong.Adapter.RequestAdapter;
import com.silong.Object.RequestsData;

public class RequestList extends AppCompatActivity {

    EditText requestSearchEt;
    ImageView requestSearchIv, requestBackIv;
    RecyclerView requestsRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);
        getSupportActionBar().hide();

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        requestSearchEt = (EditText) findViewById(R.id.requestSearchEt);
        requestSearchIv = (ImageView) findViewById(R.id.requestSearchIv);
        requestBackIv = (ImageView) findViewById(R.id.requestBackIv);
        requestsRecycler = (RecyclerView) findViewById(R.id.requestsRecycler);

        requestsRecycler.setHasFixedSize(true);
        requestsRecycler.setLayoutManager(new LinearLayoutManager(RequestList.this));

        //Array for accounts
        RequestsData[] requestsData = new RequestsData[]{
                new RequestsData("Kenjhi Maloles", "January 01, 2001", R.drawable.sample1),
                new RequestsData("Charl Nikki Belano", "February 02, 2002", R.drawable.sample2),
                new RequestsData("Jireh Trinidad", "March 03, 2003", R.drawable.sample3),
                new RequestsData("Alexis Omay", "April 04, 2004", R.drawable.sample4),
                new RequestsData("Joshua Dagatan", "May 05, 2005", R.drawable.sample5)
        };

        RequestAdapter requestAdapter = new RequestAdapter(requestsData, RequestList.this);
        requestsRecycler.setAdapter(requestAdapter);

        requestBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void back(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RequestList.this, Dashboard.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}