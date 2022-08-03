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

        //Array for accounts
        UserAccountData[] userAccountData = new UserAccountData[]{
                new UserAccountData("Kenjhi Maloles", "kenjhimaloles09@gmail.com", R.drawable.sample1),
                new UserAccountData("Charl Nikki Belano", "cnbelano@gmail.com", R.drawable.sample2),
                new UserAccountData("Jireh Trinidad", "jirehtrinidad@gmail.com", R.drawable.sample3),
                new UserAccountData("Alexis Omay", "alexomay@gmail.com", R.drawable.sample4),
                new UserAccountData("Joshua Dagatan", "joshuadagatan@gmail.com", R.drawable.sample5)
        };

        AccountAdapter accountAdapter = new AccountAdapter(userAccountData, ManageAccount.this);
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