package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class UserInformation extends AppCompatActivity {

    Switch disableSw;
    ImageView profileIv, genderIv;
    TextView nameTv, emailTv, contactTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        //to adopt status bar to the pink header
        getSupportActionBar().hide();
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        disableSw = (Switch) findViewById(R.id.disableSw);
        profileIv = (ImageView) findViewById(R.id.profileIv);
        genderIv = (ImageView) findViewById(R.id.genderIv);
        nameTv = (TextView) findViewById(R.id.nameTv);
        emailTv = (TextView) findViewById(R.id.emailTv);
        contactTv = (TextView) findViewById(R.id.contactTv);

    }
}