package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.silong.Object.Pet;
import com.silong.Object.User;

public class EditRecord extends AppCompatActivity {

    ImageView editRecordPicIv, editRecordBackIv;
    Button saveChangesBtn;

    private Pet selectedPet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_record);
        getSupportActionBar().hide();

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        String id = getIntent().getStringExtra("id");
        selectedPet = getPet(id);
        if (selectedPet == null)
            onBackPressed();

        editRecordBackIv = (ImageView) findViewById(R.id.editRecordBackIv);
        editRecordPicIv = (ImageView) findViewById(R.id.editRecordPicIv);
        saveChangesBtn = (Button) findViewById(R.id.saveChangesBtn);


    }

    private Pet getPet(String id) {
        for (Pet p : AdminData.pets){
            if (p.getPetID().equals(id))
                return p;
        }
        return null;
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