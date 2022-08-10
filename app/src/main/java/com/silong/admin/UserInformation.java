package com.silong.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.CustomView.DeactivationDialog;
import com.silong.CustomView.LoadingDialog;
import com.silong.EnumClass.Gender;
import com.silong.Object.User;
import com.silong.Operation.Utility;

public class UserInformation extends AppCompatActivity {

    SwitchMaterial disableSw;
    ImageView profileIv, genderIv, accountBackIv;
    TextView nameTv, emailTv, contactTv;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private User selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        //to adopt status bar to the pink header
        getSupportActionBar().hide();
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        //Initialize Firebase objects
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        //Receive decision from DeactivationDialog
        LocalBroadcastManager.getInstance(this).registerReceiver(mDeactivateAccount, new IntentFilter("deactivate-user"));

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

        //Display account info
        try {
            disableSw.setChecked(selectedUser.getAccountStatus());
            profileIv.setImageBitmap(selectedUser.getPhoto());
            genderIv.setImageResource(selectedUser.getGender() == Gender.MALE ? R.drawable.gender_male : R.drawable.gender_female);
            nameTv.setText(selectedUser.getFirstName() + " " + selectedUser.getLastName());
            emailTv.setText(selectedUser.getEmail());
            contactTv.setText(selectedUser.getContact());
        }
        catch (Exception e){
            Log.d("UserInfo", e.getMessage());
            Toast.makeText(this, "Can't display selected user.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserInformation.this, Dashboard.class);
            startActivity(intent);
            finish();
        }

    }

    public void onPressedBack(View view){
        onBackPressed();
    }

    public void onToggleStatus(View view){
        if (disableSw.isChecked()){
            setAccountStatus(selectedUser.getUserID(), true);
        }
        else {
            DeactivationDialog deactivationDialog = new DeactivationDialog(UserInformation.this, nameTv.getText().toString());
            deactivationDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    disableSw.setChecked(true);
                }
            });
            deactivationDialog.show();
        }
    }

    private User getUser(String uid){
        for (User u : AdminData.users){
            if (u.getUserID().equals(uid))
                return u;
        }
        return null;
    }

    private void setAccountStatus(String uid, boolean status){
        LoadingDialog loadingDialog = new LoadingDialog(UserInformation.this);
        loadingDialog.startLoadingDialog();
        //Check internet connection
        if (Utility.internetConnection(getApplicationContext())){
            try{
                //Change value in specific account
                DatabaseReference tempReference = mDatabase.getReference("Users/" + uid);
                tempReference.child("accountStatus").setValue(status)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                loadingDialog.dismissLoadingDialog();
                                setAccountStatusInSummary(uid, status);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                disableSw.setChecked(!status);
                                loadingDialog.dismissLoadingDialog();
                                Toast.makeText(getApplicationContext(), "Please try again.", Toast.LENGTH_SHORT).show();
                                Log.d("UserInfo", e.getMessage());
                            }
                        });
            }
            catch (Exception e){
                disableSw.setChecked(!status);
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(getApplicationContext(), "Please try again.", Toast.LENGTH_SHORT).show();
                Log.d("UserInfo", e.getMessage());
            }

        }
        else {
            Toast.makeText(this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
            disableSw.setChecked(!status);
            loadingDialog.dismissLoadingDialog();
        }
    }

    private void setAccountStatusInSummary(String uid, boolean status){
        LoadingDialog loadingDialog = new LoadingDialog(UserInformation.this);
        loadingDialog.startLoadingDialog();
        try{
            //Change value in accountSummary
            DatabaseReference tempReference = mDatabase.getReference("accountSummary/" + uid);
            tempReference.setValue(status)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Account " + (status ? "activated" : "deactivated"), Toast.LENGTH_SHORT).show();
                            loadingDialog.dismissLoadingDialog();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            disableSw.setChecked(!status);
                            loadingDialog.dismissLoadingDialog();
                            Toast.makeText(getApplicationContext(), "Please try again.", Toast.LENGTH_SHORT).show();
                            Log.d("UserInfo", e.getMessage());
                        }
                    });
        }
        catch (Exception e){
            disableSw.setChecked(!status);
            loadingDialog.dismissLoadingDialog();
            Toast.makeText(getApplicationContext(), "Please try again.", Toast.LENGTH_SHORT).show();
            Log.d("UserInfo", e.getMessage());
        }
    }

    private BroadcastReceiver mDeactivateAccount = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean decision = intent.getBooleanExtra("deactivate", false);
            if (decision){
                setAccountStatus(selectedUser.getUserID(), false);
            }
            else {
                disableSw.setChecked(true);
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
    }
}