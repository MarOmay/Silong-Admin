package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.silong.CustomDialog.LoadingDialog;
import com.silong.Object.User;

import java.util.ArrayList;

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

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        accountSearchEt = (EditText) findViewById(R.id.accountSearchEt);
        accountSearchIv = (ImageView) findViewById(R.id.accountSearchIv);
        accountCreateIv = (ImageView) findViewById(R.id.accountCreateIv);
        accountBackIv = (ImageView) findViewById(R.id.accountBackIv);
        accountsRecycler = (RecyclerView) findViewById(R.id.accountsRecycler);

        accountsRecycler.setHasFixedSize(true);
        accountsRecycler.setLayoutManager(new LinearLayoutManager(ManageAccount.this));

        loadAccountList();

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

        accountSearchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyword = accountSearchEt.getText().toString();
                loadAccountList();
            }
        });

        accountBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        accountCreateIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ManageAccount.this, CreateAdminAccount.class);
                startActivity(i);
            }
        });
    }


    public void loadAccountList(){
        LoadingDialog loadingDialog = new LoadingDialog(ManageAccount.this);
        loadingDialog.startLoadingDialog();

        UserAccountData[] accountData = new UserAccountData[AdminData.users.size()];

        for (User user : AdminData.users){
            String name = user.getFirstName() + " " + user.getLastName();
            accountData[AdminData.users.indexOf(user)] = new UserAccountData(name, user.getEmail(), user.getPhoto());
        }

        AccountAdapter accountAdapter = new AccountAdapter(accountData, ManageAccount.this);
        accountsRecycler.setAdapter(accountAdapter);

        loadingDialog.dismissLoadingDialog();
    }

    public void back(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ManageAccount.this, Dashboard.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}