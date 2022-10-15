package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class AdoptionAgreement extends AppCompatActivity {

    RecyclerView adoptionAgreementRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoption_agreement);
        getSupportActionBar().hide();

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        adoptionAgreementRecycler = findViewById(R.id.adoptionAgreementRecycler);

        adoptionAgreementRecycler.setHasFixedSize(true);
        adoptionAgreementRecycler.setLayoutManager(new LinearLayoutManager(AdoptionAgreement.this));

        AgreementData[] agreementData = new AgreementData[]{
                new AgreementData("Agreement Number One", "Agreement Body Number One"),
                new AgreementData("Agreement Number Two", "Agreement Body Number Two"),
                new AgreementData("Agreement Number Three", "Agreement Body Number Three"),
                new AgreementData("Agreement Number Four", "Agreement Body Number Four"),
                new AgreementData("Agreement Number Five", "Agreement Body Number Five"),
                new AgreementData("Agreement Number Six", "Agreement Body Number Six")
        };

        AgreementAdapter agreementAdapter = new AgreementAdapter(agreementData, AdoptionAgreement.this);
        adoptionAgreementRecycler.setAdapter(agreementAdapter);
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    public void onPressedPlus(View view){
        Intent i = new Intent(AdoptionAgreement.this, EditClause.class);
        startActivity(i);
    }
}