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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.silong.CustomView.LoadingDialog;
import com.silong.Object.User;
import com.silong.Operation.Utility;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ManageAccount extends AppCompatActivity {

    public static String keyword = "";

    EditText accountSearchEt;
    ImageView accountSearchIv, accountCreateIv, accountBackIv;
    RecyclerView accountsRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_accounts);
        getSupportActionBar().hide();

        keyword = "";

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        //Receive trigger from Dashbooard to update account list
        LocalBroadcastManager.getInstance(this).registerReceiver(mTriggerUpdate, new IntentFilter("update-account-list"));

        //Receive uid from AccountAdapter
        LocalBroadcastManager.getInstance(this).registerReceiver(mShowUser, new IntentFilter("show-selected-user"));

        accountSearchEt = (EditText) findViewById(R.id.accountSearchEt);
        accountSearchIv = (ImageView) findViewById(R.id.accountSearchIv);
        accountCreateIv = (ImageView) findViewById(R.id.accountCreateIv);
        accountBackIv = (ImageView) findViewById(R.id.accountBackIv);
        accountsRecycler = (RecyclerView) findViewById(R.id.accountsRecycler);

        accountsRecycler.setHasFixedSize(true);
        accountsRecycler.setLayoutManager(new LinearLayoutManager(ManageAccount.this));

        loadAccountList();
        manualAddSearchListener();

    }

    public void onPressedBack(View view){
        onBackPressed();
    }

    public void onPressedAdd(View view){
        if (Utility.internetConnection(getApplicationContext())){
            Intent i = new Intent(ManageAccount.this, CreateAdminAccount.class);
            startActivity(i);
        }
        else {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onPressedSearch(View view){
        keyword = accountSearchEt.getText().toString();
        loadAccountList();
    }

    private void manualAddSearchListener(){
        accountSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                keyword = accountSearchEt.getText().toString();
                loadAccountList();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void loadAccountList(){
        LoadingDialog loadingDialog = new LoadingDialog(ManageAccount.this);
        loadingDialog.startLoadingDialog();

        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                AdminData.users.sort(new Comparator<User>() {
                    @Override
                    public int compare(User user, User t1) {
                        return user.getFirstName().toLowerCase().compareTo(t1.getFirstName().toLowerCase());
                    }
                });
            }
        }
        catch (Exception e){
            Toast.makeText(this, "Preparing resources...", Toast.LENGTH_SHORT).show();
            Log.d("ManageAccount", e.getMessage());
        }

        UserAccountData[] accountData = new UserAccountData[AdminData.users.size()];

        for (User user : AdminData.users){
            String name = user.getFirstName() + " " + user.getLastName();
            accountData[AdminData.users.indexOf(user)] = new UserAccountData(user.getUserID(), name, user.getEmail(), user.getPhoto());
        }

        AccountAdapter accountAdapter = new AccountAdapter(accountData, ManageAccount.this);
        accountsRecycler.setAdapter(accountAdapter);

        loadingDialog.dismissLoadingDialog();
    }

    public void back(View view){
        onBackPressed();
    }

    private BroadcastReceiver mTriggerUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadAccountList();
        }
    };

    private BroadcastReceiver mShowUser = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String uid = intent.getStringExtra("uid");
            Intent i = new Intent(ManageAccount.this, UserInformation.class);
            i.putExtra("uid", uid);
            startActivity(i);
            finish();
        }
    };

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ManageAccount.this, Dashboard.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mTriggerUpdate);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mShowUser);
    }
}