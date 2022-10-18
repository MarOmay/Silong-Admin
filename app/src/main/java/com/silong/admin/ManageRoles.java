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
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.silong.Adapter.AdminAccountsAdapter;
import com.silong.CustomView.LoadingDialog;
import com.silong.Object.Admin;
import com.silong.Operation.Utility;
import com.silong.Task.AdminFetcher;

import java.util.ArrayList;
import java.util.Comparator;

public class ManageRoles extends AppCompatActivity {

    private RecyclerView adminAccountsRecycler;

    public static ArrayList<Admin> ADMINS = new ArrayList<>();

    private LoadingDialog loadingDialog = new LoadingDialog(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_roles);
        getSupportActionBar().hide();

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        //register receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRefreshReceiver, new IntentFilter("refresh-admin-list"));

        adminAccountsRecycler = findViewById(R.id.adminAccountsRecycler);
        adminAccountsRecycler.setHasFixedSize(true);
        adminAccountsRecycler.setLayoutManager(new LinearLayoutManager(ManageRoles.this));

        ADMINS.clear();

        //fetch all admins
        AdminFetcher adminFetcher = new AdminFetcher(ManageRoles.this);
        adminFetcher.execute();

        loadingDialog.startLoadingDialog();

    }

    public void onPressedAdd(View view){
        if (Utility.internetConnection(getApplicationContext())){
            Intent i = new Intent(ManageRoles.this, CreateAdminAccount.class);
            startActivity(i);
        }
        else {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAdminList(){
        if (ADMINS.isEmpty()){
            Toast.makeText(this, "No admins to display", Toast.LENGTH_SHORT).show();
            Utility.log("ManageRoles.lAL: No admins to be displayed");
            return;
        }

        try {

            //sort by firstName
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ADMINS.sort(new Comparator<Admin>() {
                    @Override
                    public int compare(Admin a1, Admin a2) {
                        return a1.getFirstName().compareTo(a2.getFirstName());
                    }
                });
            }

            int listSize = ADMINS.size();

            Admin[] admins = new Admin[listSize];

            for (int i = 0; i < listSize; i++)
                admins[i] = ADMINS.get(i);

            AdminAccountsAdapter adminAccountsAdapter = new AdminAccountsAdapter(admins, ManageRoles.this);
            adminAccountsRecycler.setAdapter(adminAccountsAdapter);

        }
        catch (Exception e){
            Utility.log("ManageRoles.lA: " + e.getMessage());
        }
    }


    private BroadcastReceiver mRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            loadingDialog.dismissLoadingDialog();
            loadAdminList();

        }
    };



    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRefreshReceiver);
        super.onDestroy();
    }
}