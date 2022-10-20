package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.silong.CustomView.DatabaseWarningDialog;

public class ManageDatabase extends AppCompatActivity {

    private MaterialCheckBox pastLogsCb, deletedUsersCb, adoptedPetsCb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_database);
        getSupportActionBar().hide();
        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        pastLogsCb = findViewById(R.id.pastLogsCb);
        deletedUsersCb = findViewById(R.id.deletedUsersCb);
        adoptedPetsCb = findViewById(R.id.adoptedPetsCb);
    }

    public void onPressedDeleteDb(View view){
        DatabaseWarningDialog databaseWarningDialog = new DatabaseWarningDialog(ManageDatabase.this);
        databaseWarningDialog.show();
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