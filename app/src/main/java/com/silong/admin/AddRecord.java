package com.silong.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.Object.Pet;
import com.silong.Operation.ImagePicker;

import java.io.BufferedInputStream;

public class AddRecord extends AppCompatActivity {

    private final int PICK_IMAGE = 2;

    ImageView addRecordPicIv, addRecordBackIv;
    Button saveRecordBtn;
    ChipGroup typeToggle, genderToggle, sizeToggle, colorToggle;
    Chip addDogChip, addCatChip, addMaleChip, addFemaleChip, addSmallChip, addMediumChip, addLargeChip;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);
        getSupportActionBar().hide();

        //Initialize Firebase objects
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        initializeCounter();

        addRecordBackIv = (ImageView) findViewById(R.id.addRecordBackIv);
        addRecordPicIv = (ImageView) findViewById(R.id.addRecordPicIv);
        typeToggle = findViewById(R.id.typeToggle);
        genderToggle = findViewById(R.id.genderToggle);
        sizeToggle = findViewById(R.id.sizeToggle);
        colorToggle = findViewById(R.id.colorToggle);
        saveRecordBtn = (Button) findViewById(R.id.saveRecordBtn);

        addDogChip = findViewById(R.id.addDogChip);
        addCatChip = findViewById(R.id.addCatChip);
        addMaleChip = findViewById(R.id.addMaleChip);
        addFemaleChip = findViewById(R.id.addFemaleChip);
        addSmallChip = findViewById(R.id.addSmallChip);
        addMediumChip = findViewById(R.id.addMediumChip);
        addLargeChip = findViewById(R.id.addLargeChip);
    }

    public void onPressedPhoto(View view){
        new ImagePicker(AddRecord.this, PICK_IMAGE);
    }

    public void onPressedSave(View view){
        //validate input
        if (addRecordPicIv.getDrawable() == null){
            Toast.makeText(this, "Please select a photo.", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (typeToggle.getCheckedChipIds().isEmpty()){
            Toast.makeText(this, "Please select pet type.", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (genderToggle.getCheckedChipIds().isEmpty()){
            Toast.makeText(this, "Please select pet gender.", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (sizeToggle.getCheckedChipIds().isEmpty()){
            Toast.makeText(this, "Please select pet size.", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (colorToggle.getCheckedChipIds().isEmpty()){
            Toast.makeText(this, "Please select pet color.", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            Pet pet = new Pet();
        }

    }

    private void uploadPetProfile(){

    }

    private void initializeCounter(){
        //check if counter filed exists
        mReference = mDatabase.getReference("Pets").child("counter");
        try{
            mReference = mDatabase.getReference("Pets").child("counter");
            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        counter = Integer.parseInt(snapshot.getValue().toString());
                    }
                    catch (Exception ex){
                        mReference.setValue(0);
                        counter = 0;
                        Log.d("AddRecord", ex.getMessage());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception e){
            mReference.setValue(0);
            counter = 0;
            Log.d("AddRecord", e.getMessage());
        }
    }

    public void back(View view){
        onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            try{
                BufferedInputStream bufferedInputStream = new BufferedInputStream(getContentResolver().openInputStream(data.getData()));
                Bitmap bitmap = BitmapFactory.decodeStream(bufferedInputStream);
                addRecordPicIv.setImageBitmap(bitmap);
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Unable to choose file", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}