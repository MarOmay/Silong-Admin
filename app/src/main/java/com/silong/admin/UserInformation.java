package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.silong.CustomView.DeactivationDialog;
import com.silong.EnumClass.Gender;
import com.silong.Object.User;

public class UserInformation extends AppCompatActivity {

    SwitchMaterial disableSw;
    ImageView profileIv, genderIv, accountBackIv;
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

        //Get specific account info
        String uid = getIntent().getStringExtra("uid");
        User selectedUser = getUser(uid);
        if (selectedUser == null)
            onBackPressed();

        accountBackIv = (ImageView) findViewById(R.id.accountBackIv);
        disableSw = (SwitchMaterial) findViewById(R.id.disableSw);
        profileIv = (ImageView) findViewById(R.id.profileIv);
        genderIv = (ImageView) findViewById(R.id.genderIv);
        nameTv = (TextView) findViewById(R.id.nameTv);
        emailTv = (TextView) findViewById(R.id.emailTv);
        contactTv = (TextView) findViewById(R.id.contactTv);

        //Display account info
        disableSw.setChecked(selectedUser.getAccountStatus());
        profileIv.setImageBitmap(selectedUser.getPhoto());
        genderIv.setImageResource(selectedUser.getGender() == Gender.MALE ? R.drawable.gender_male : R.drawable.gender_female);
        nameTv.setText(selectedUser.getFirstName() + " " + selectedUser.getLastName());
        emailTv.setText(selectedUser.getEmail());
        contactTv.setText(selectedUser.getContact());
    }

    public void onPressedBack(View view){
        onBackPressed();
    }

    public void onToggleStatus(View view){
        if (disableSw.isChecked()){
            Toast.makeText(this, "Checked", Toast.LENGTH_SHORT).show();
        }
        else {
            DeactivationDialog deactivationDialog = new DeactivationDialog(UserInformation.this, nameTv.getText().toString());
            deactivationDialog.show();
            Toast.makeText(this, "Unchecked", Toast.LENGTH_SHORT).show();
        }
    }

    private User getUser(String uid){
        for (User u : AdminData.users){
            if (u.getUserID().equals(uid))
                return u;
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UserInformation.this, ManageAccount.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}