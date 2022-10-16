package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.google.firebase.database.FirebaseDatabase;
import com.silong.Adapter.AdminAccountsAdapter;
import com.silong.Object.AdminAccountsData;

public class ManageRoles extends AppCompatActivity {

    RecyclerView adminAccountsRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_roles);
        getSupportActionBar().hide();
        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        adminAccountsRecycler = findViewById(R.id.adminAccountsRecycler);
        adminAccountsRecycler.setHasFixedSize(true);
        adminAccountsRecycler.setLayoutManager(new LinearLayoutManager(ManageRoles.this));

        AdminAccountsData[] adminAccountsData = new AdminAccountsData[]{
                new AdminAccountsData("Admin One", "admin1@admin.com", R.drawable.admin_avatar),
                new AdminAccountsData("Admin Two", "admin2@admin.com", R.drawable.admin_avatar),
                new AdminAccountsData("Admin Three", "admin3@admin.com", R.drawable.admin_avatar)
        };

        AdminAccountsAdapter adminAccountsAdapter = new AdminAccountsAdapter(adminAccountsData, ManageRoles.this);
        adminAccountsRecycler.setAdapter(adminAccountsAdapter);
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}