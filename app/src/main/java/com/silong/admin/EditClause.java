package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class EditClause extends AppCompatActivity {

    Button deleteClauseBtn, saveClauseBtn;
    EditText clauseTitle, clauseBody;
    ImageView editClausePlus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_clause);
        getSupportActionBar().hide();

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        editClausePlus = findViewById(R.id.editClausePlus);
        clauseTitle = findViewById(R.id.clauseTitle);
        clauseBody = findViewById(R.id.clauseBody);
        deleteClauseBtn = findViewById(R.id.deleteClauseBtn);
        saveClauseBtn = findViewById(R.id.saveClauseBtn);
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