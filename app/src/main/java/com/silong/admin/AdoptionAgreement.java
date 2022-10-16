package com.silong.admin;

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
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silong.Adapter.AgreementAdapter;
import com.silong.Object.AgreementData;
import com.silong.Operation.Utility;
import com.silong.Task.ClauseFetcher;

import java.util.ArrayList;
import java.util.Comparator;

public class AdoptionAgreement extends AppCompatActivity {

    RecyclerView adoptionAgreementRecycler;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    public static ArrayList<AgreementData> AGREEMENT_DATA = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoption_agreement);
        getSupportActionBar().hide();

        //initialize Firebase objects
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        //register receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mReloadReceiver, new IntentFilter("refresh-agreement"));

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        adoptionAgreementRecycler = findViewById(R.id.adoptionAgreementRecycler);

        adoptionAgreementRecycler.setHasFixedSize(true);
        adoptionAgreementRecycler.setLayoutManager(new LinearLayoutManager(AdoptionAgreement.this));

        AGREEMENT_DATA.clear();

        //fetch clause from RTDB
        ClauseFetcher clauseFetcher = new ClauseFetcher(AdoptionAgreement.this);
        clauseFetcher.execute();

    }

    private void loadContents(){

        if (AGREEMENT_DATA.isEmpty()){
            Toast.makeText(this, "No agreement written yet", Toast.LENGTH_SHORT).show();
            Utility.log("AdpAgmnt.lC: No clause to be displayed");
            return;
        }

        try {

            //sort by date
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                AGREEMENT_DATA.sort(new Comparator<AgreementData>() {
                    @Override
                    public int compare(AgreementData a1, AgreementData a2) {
                        return a1.getAgreementDate().compareTo(a2.getAgreementDate());
                    }
                });
            }

            int listSize = AGREEMENT_DATA.size();

            AgreementData[] agreementData = new AgreementData[listSize];

            for (int i = 0; i < listSize; i++){
                agreementData[i] = AGREEMENT_DATA.get(i);
            }

            AgreementAdapter agreementAdapter = new AgreementAdapter(agreementData, AdoptionAgreement.this);
            adoptionAgreementRecycler.setAdapter(agreementAdapter);

        }
        catch (Exception e){
            Toast.makeText(this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
            Utility.log("AdpAgmnt.lC: " + e.getMessage());
        }

    }


    private BroadcastReceiver mReloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            loadContents();

        }
    };


    public void back(View view) {
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

    public void onPressedPlus(View view){
        Intent i = new Intent(AdoptionAgreement.this, EditClause.class);
        startActivity(i);
    }
}