package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.silong.Adapter.AdoptionHistoryAdapter;
import com.silong.CustomView.DeactivationDialog;
import com.silong.CustomView.LoadingDialog;
import com.silong.EnumClass.Gender;
import com.silong.Object.Adoption;
import com.silong.Object.User;
import com.silong.Operation.Utility;
import com.silong.Task.StatusChanger;

public class UserInformation extends AppCompatActivity {

    SwitchMaterial disableSw;
    ImageView profileIv, genderIv, accountBackIv;
    TextView nameTv, emailTv, contactTv;
    RecyclerView adoptionHistoryRecycler;

    private User selectedUser;

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        //to adopt status bar to the pink header
        getSupportActionBar().hide();
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        //register receivers
        LocalBroadcastManager.getInstance(this).registerReceiver(mDeactivateAccount, new IntentFilter("deactivate-user"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mStatusChanger, new IntentFilter("SC-coded"));

        //Get specific account info
        String uid = getIntent().getStringExtra("uid");
        selectedUser = getUser(uid);
        if (selectedUser == null)
            onBackPressed();

        accountBackIv = (ImageView) findViewById(R.id.accountBackIv);
        disableSw = (SwitchMaterial) findViewById(R.id.disableSw);
        profileIv = (ImageView) findViewById(R.id.profileIv);
        genderIv = (ImageView) findViewById(R.id.genderIv);
        nameTv = (TextView) findViewById(R.id.nameTv);
        emailTv = (TextView) findViewById(R.id.emailTv);
        contactTv = (TextView) findViewById(R.id.contactTv);
        adoptionHistoryRecycler = findViewById(R.id.adoptionHistoryRecycler);

        adoptionHistoryRecycler.setHasFixedSize(true);
        adoptionHistoryRecycler.setLayoutManager(new LinearLayoutManager(UserInformation.this));

        loadingDialog = new LoadingDialog(UserInformation.this);

        //Display account info
        displayAccountInfo();

    }

    private void displayAccountInfo() {
        try {
            disableSw.setChecked(selectedUser.getAccountStatus());
            profileIv.setImageBitmap(selectedUser.getPhoto());
            genderIv.setImageResource(selectedUser.getGender() == Gender.MALE ? R.drawable.gender_male : R.drawable.gender_female);
            nameTv.setText(selectedUser.getFirstName() + " " + selectedUser.getLastName());
            emailTv.setText(selectedUser.getEmail());
            contactTv.setText(selectedUser.getContact());
        } catch (Exception e) {
            Log.d("UserInfo", e.getMessage());
            Toast.makeText(this, "Can't display selected user.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserInformation.this, Dashboard.class);
            startActivity(intent);
            finish();
        }
    }

    public void onPressedBack(View view) {
        onBackPressed();
    }

    private boolean decisionMade = false;

    public void onToggleStatus(View view) {
        if (Utility.internetConnection(getApplicationContext())) {
            if (disableSw.isChecked()) {
                //activate account
                loadingDialog.startLoadingDialog();
                StatusChanger statusChanger = new StatusChanger(selectedUser.getUserID(), true, UserInformation.this);
                statusChanger.execute();
            } else {
                //ask if to deactivate account
                DeactivationDialog deactivationDialog = new DeactivationDialog(UserInformation.this, nameTv.getText().toString());
                deactivationDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        disableSw.setChecked(!decisionMade);
                    }
                });
                deactivationDialog.show();
            }
        } else {
            Toast.makeText(this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
            disableSw.setChecked(!disableSw.isChecked());
        }

    }

    private User getUser(String uid) {
        for (User u : AdminData.users) {
            if (u.getUserID().equals(uid))
                return u;
        }
        return null;
    }

    private BroadcastReceiver mDeactivateAccount = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean decision = intent.getBooleanExtra("deactivate", false);
            if (decision) {
                decisionMade = true;
                loadingDialog.startLoadingDialog();
                StatusChanger statusChanger = new StatusChanger(selectedUser.getUserID(), false, UserInformation.this);
                statusChanger.execute();
            } else {
                decisionMade = false;
                disableSw.setChecked(true);
            }
        }
    };

    private BroadcastReceiver mStatusChanger = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadingDialog.dismissLoadingDialog();

            String code = intent.getStringExtra("code");
            String uid = intent.getStringExtra("uid");
            boolean status = intent.getBooleanExtra("status", true);

            if (code.equals(StatusChanger.SUCCESS)) {
                Toast.makeText(UserInformation.this, "Account " + (status ? "activated" : "deactivated"), Toast.LENGTH_SHORT).show();
            } else if (code.equals(StatusChanger.FAILURE)) {
                disableSw.setChecked(!status);
                Toast.makeText(getApplicationContext(), "Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UserInformation.this, ManageAccount.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDeactivateAccount);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mStatusChanger);
    }
}