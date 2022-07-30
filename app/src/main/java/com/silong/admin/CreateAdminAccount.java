package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class CreateAdminAccount extends AppCompatActivity {

    ImageView createAdminBackIv;
    EditText createAdminFnameEt, createAdminLnameEt, createAdminEmailEt,
            createAdminContactEt, createAdminPasswordEt, createAdminConfirmpassEt;
    Button createAdminCreateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_admin_account);
        getSupportActionBar().hide();

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        createAdminBackIv = (ImageView) findViewById(R.id.createAdminBackIv);
        createAdminFnameEt = (EditText) findViewById(R.id.createAdminFnameEt);
        createAdminLnameEt = (EditText) findViewById(R.id.createAdminLnameEt);
        createAdminEmailEt = (EditText) findViewById(R.id.createAdminEmailEt);
        createAdminContactEt = (EditText) findViewById(R.id.createAdminContactEt);
        createAdminPasswordEt = (EditText) findViewById(R.id.createAdminPasswordEt);
        createAdminConfirmpassEt = (EditText) findViewById(R.id.createAdminConfirmpassEt);
        createAdminCreateBtn = (Button) findViewById(R.id.createAdminCreateBtn);

        createAdminBackIv.setOnClickListener(new View.OnClickListener() {
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
        super.onBackPressed();
        this.finish();
    }
}