package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

public class EditRecord extends AppCompatActivity {

    ImageView editRecordPicIv, editRecordBackIv;
    Button saveChangesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_record);
        getSupportActionBar().hide();

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        editRecordBackIv = (ImageView) findViewById(R.id.editRecordBackIv);
        editRecordPicIv = (ImageView) findViewById(R.id.editRecordPicIv);
        saveChangesBtn = (Button) findViewById(R.id.saveChangesBtn);
    }

    public void back(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}