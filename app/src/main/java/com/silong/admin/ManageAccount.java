package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import com.silong.Object.User;

public class ManageAccount extends AppCompatActivity {

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

        UserAccountData[] accountData = new UserAccountData[AdminData.users.size()];
        for (User user : AdminData.users){
            accountData[AdminData.users.indexOf(user)] = new UserAccountData(
              user.getFirstName() + " " + user.getLastName(),
              user.getEmail(), user.getPhoto());
        }

        AccountAdapter accountAdapter = new AccountAdapter(accountData, ManageAccount.this);
        accountsRecycler.setAdapter(accountAdapter);

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