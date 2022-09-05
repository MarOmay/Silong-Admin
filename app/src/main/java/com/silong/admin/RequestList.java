package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.silong.Adapter.RequestAdapter;
import com.silong.CustomView.LoadingDialog;
import com.silong.Object.Request;
import com.silong.Task.AppointmentFetcher;

import java.util.Comparator;

public class RequestList extends AppCompatActivity {

    public static String keyword = "";

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

        loadRequestList();
        manualAddSearchListener();

        //fetch appointments
        AppointmentFetcher appointmentFetcher = new AppointmentFetcher(RequestList.this);
        appointmentFetcher.execute();

    }

    public void onPressedCalendar(View view){
        Intent intent = new Intent(RequestList.this, AppointmentsList.class);
        startActivity(intent);
    }

    private void manualAddSearchListener(){
        requestSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                keyword = requestSearchEt.getText().toString();
                loadRequestList();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void loadRequestList(){
        LoadingDialog loadingDialog = new LoadingDialog(RequestList.this);
        loadingDialog.startLoadingDialog();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                AdminData.requests.sort(new Comparator<Request>() {
                    @Override
                    public int compare(Request request, Request t1) {
                        return request.getDate().compareTo(t1.getDate());
                    }
                });
            }
        }
        catch (Exception e){
            Toast.makeText(this, "Preparing resources...", Toast.LENGTH_SHORT).show();
            Log.d("RequestList-lRL", e.getMessage());
        }

        RequestAdapter requestAdapter = new RequestAdapter(AdminData.requests, RequestList.this);
        requestsRecycler.setAdapter(requestAdapter);

        loadingDialog.dismissLoadingDialog();
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