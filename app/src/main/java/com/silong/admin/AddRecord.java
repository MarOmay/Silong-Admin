package com.silong.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.CustomView.LoadingDialog;
import com.silong.EnumClass.Gender;
import com.silong.EnumClass.PetAge;
import com.silong.EnumClass.PetColor;
import com.silong.EnumClass.PetSize;
import com.silong.EnumClass.PetStatus;
import com.silong.EnumClass.PetType;
import com.silong.Object.Pet;
import com.silong.Operation.ImagePicker;
import com.silong.Operation.ImageProcessor;
import com.silong.Operation.Utility;

import java.io.BufferedInputStream;
import java.util.HashMap;
import java.util.Map;

public class AddRecord extends AppCompatActivity {

    private final int PICK_IMAGE = 2;

    ImageView addRecordPicIv, addRecordBackIv;
    Button saveRecordBtn;
    ChipGroup typeToggle, genderToggle, ageToggle, sizeToggle, colorToggle;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private Pet selectedPet;

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
        ageToggle = findViewById(R.id.ageToggle);
        sizeToggle = findViewById(R.id.sizeToggle);
        colorToggle = findViewById(R.id.colorToggle);
        saveRecordBtn = (Button) findViewById(R.id.saveRecordBtn);

        loadForEdit();
    }

    private void loadForEdit(){
        try {
            String id = getIntent().getStringExtra("id");
            selectedPet = AdminData.getPet(id);
            if (selectedPet != null) {
                //change header label
                TextView headerTv = findViewById(R.id.headerTv);
                headerTv.setText("Edit Record");

                addRecordPicIv.setImageBitmap(selectedPet.getPhoto());

                //set type
                switch (selectedPet.getType()){
                    case PetType.DOG: typeToggle.check(R.id.addDogChip); break;
                    case PetType.CAT: typeToggle.check(R.id.addCatChip); break;
                }

                Chip chip = findViewById(R.id.addPuppyChip);
                if (selectedPet.getType() == PetType.DOG){
                    chip.setText("PUPPY");
                }
                else {
                    chip.setText("KITTEN");
                }

                //set age
                switch (selectedPet.getAge()){
                    case PetAge.PUPPY: ageToggle.check(R.id.addPuppyChip); break;
                    case PetAge.YOUNG: ageToggle.check(R.id.addYoungChip); break;
                    case PetAge.OLD: ageToggle.check(R.id.addOldChip); break;
                }

                //set gender
                switch (selectedPet.getGender()){
                    case Gender.MALE: genderToggle.check(R.id.addMaleChip); break;
                    case Gender.FEMALE: genderToggle.check(R.id.addFemaleChip); break;
                }

                //set size
                switch (selectedPet.getSize()){
                    case PetSize.SMALL: sizeToggle.check(R.id.addSmallChip); break;
                    case PetSize.MEDIUM: sizeToggle.check(R.id.addMediumChip); break;
                    case PetSize.LARGE: sizeToggle.check(R.id.addLargeChip); break;
                }

                //set color
                for (char c : selectedPet.getColor().toCharArray()){
                    switch (Integer.parseInt(c+"")){
                        case PetColor.BLACK: colorToggle.check(R.id.addBlackChip); break;
                        case PetColor.BROWN: colorToggle.check(R.id.addBrownChip); break;
                        case PetColor.CREAM: colorToggle.check(R.id.addCreamChip); break;
                        case PetColor.WHITE: colorToggle.check(R.id.addWhiteChip); break;
                        case PetColor.ORANGE: colorToggle.check(R.id.addOrangeChip); break;
                        case PetColor.GRAY: colorToggle.check(R.id.addGrayChip); break;
                    }
                }
            }
        }
        catch (Exception e){
            Log.d("AddRecord", e.getMessage());
        }
    }

    public void onPressedPhoto(View view){
        new ImagePicker(AddRecord.this, PICK_IMAGE);
    }

    public void onPressedDog(View view){
        Chip chip = (Chip) findViewById(R.id.addPuppyChip);
        chip.setText("PUPPY");
    }

    public void onPressedCat(View view){
        Chip chip = (Chip) findViewById(R.id.addPuppyChip);
        chip.setText("KITTEN");
    }

    public void onPressedSave(View view){
        //validate input
        if (addRecordPicIv.getDrawable() == null){
            Toast.makeText(this, "Please select a photo.", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (new ImageProcessor().checkFileSize(addRecordPicIv.getDrawable(), true) == false){
            Toast.makeText(getApplicationContext(), "Please select a picture less than 2MB.", Toast.LENGTH_SHORT).show();
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
        else if (ageToggle.getCheckedChipIds().isEmpty()){
            Toast.makeText(this, "Please select set age group.", Toast.LENGTH_SHORT).show();
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
            ImageProcessor imageProcessor = new ImageProcessor();
            imageProcessor.checkFileSize(addRecordPicIv.getDrawable(), true);
            Pet pet = new Pet();
            pet.setPhotoAsString(imageProcessor.toUTF8(addRecordPicIv.getDrawable(), true));
            //identify selected age
            pet.setAge(PetAge.YOUNG);
            //identify selected type
            pet.setType(typeToggle.getCheckedChipId() == R.id.addDogChip ? PetType.DOG : PetType.CAT);
            //identify selected gender
            pet.setGender(genderToggle.getCheckedChipId() == R.id.addMaleChip ? Gender.MALE : Gender.FEMALE);
            //identify selected gender
            switch (ageToggle.getCheckedChipId()){
                case R.id.addPuppyChip: pet.setAge(PetAge.PUPPY); break;
                case R.id.addYoungChip: pet.setAge(PetAge.YOUNG); break;
                case R.id.addOldChip: pet.setAge(PetAge.OLD); break;
            }
            //identify selected size
            switch (sizeToggle.getCheckedChipId()){
                case R.id.addSmallChip: pet.setSize(PetSize.SMALL); break;
                case R.id.addMediumChip: pet.setSize(PetSize.MEDIUM); break;
                case R.id.addLargeChip: pet.setSize(PetSize.LARGE); break;
            }
            //identify selected color
            String color = "";
            for (Integer id : colorToggle.getCheckedChipIds()){
                switch (id){
                    case R.id.addBlackChip: color += PetColor.BLACK; break;
                    case R.id.addBrownChip: color += PetColor.BROWN; break;
                    case R.id.addCreamChip: color += PetColor.CREAM; break;
                    case R.id.addWhiteChip: color += PetColor.WHITE; break;
                    case R.id.addOrangeChip: color += PetColor.ORANGE; break;
                    case R.id.addGrayChip: color += PetColor.GRAY; break;
                }
            }
            pet.setColor(color);
            uploadPetProfile(pet);
        }

    }

    private void uploadPetProfile(Pet pet){
        LoadingDialog loadingDialog = new LoadingDialog(AddRecord.this);
        loadingDialog.startLoadingDialog();
        //check internet connection
        if (Utility.internetConnection(getApplicationContext())){
            try {
                Map<String, Object> map = new HashMap();
                map.put("status", PetStatus.ACTIVE);
                map.put("type", pet.getType());
                map.put("gender", pet.getGender());
                map.put("color", pet.getColor());
                map.put("age", pet.getAge());
                map.put("size", pet.getSize());
                map.put("photo", pet.getPhotoAsString());
                map.put("modifiedBy", AdminData.adminEmail);
                map.put("lastModified", Utility.dateToday() + " " + Utility.timeNow());

                try {
                    if (selectedPet != null){
                        counter = Integer.parseInt(selectedPet.getPetID());
                    }
                }
                catch (Exception e){
                    Log.d("AddRecord", e.getMessage());
                }

                mReference = mDatabase.getReference("Pets").child(String.valueOf(counter));
                mReference.updateChildren(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                mReference = mDatabase.getReference("recordSummary").child(String.valueOf(counter));
                                mReference.setValue(PetStatus.ACTIVE);

                                try {

                                    //write to file
                                    AdminData.writePetToLocal(getApplicationContext(), String.valueOf(counter), "petID", String.valueOf(counter));
                                    AdminData.writePetToLocal(getApplicationContext(), String.valueOf(counter), "status", String.valueOf(PetStatus.ACTIVE));
                                    AdminData.writePetToLocal(getApplicationContext(), String.valueOf(counter), "type", String.valueOf(pet.getType()));
                                    AdminData.writePetToLocal(getApplicationContext(), String.valueOf(counter), "gender", String.valueOf(pet.getGender()));
                                    AdminData.writePetToLocal(getApplicationContext(), String.valueOf(counter), "color", pet.getColor());
                                    AdminData.writePetToLocal(getApplicationContext(), String.valueOf(counter), "age", String.valueOf(pet.getAge()));
                                    AdminData.writePetToLocal(getApplicationContext(), String.valueOf(counter), "size", String.valueOf(pet.getSize()));
                                    AdminData.writePetToLocal(getApplicationContext(), String.valueOf(counter), "modifiedBy", AdminData.adminEmail);
                                    AdminData.writePetToLocal(getApplicationContext(), String.valueOf(counter), "lastModified", Utility.dateToday() + " " + Utility.timeNow());
                                    Bitmap bitmap = new ImageProcessor().toBitmap(pet.getPhotoAsString());
                                    new ImageProcessor().saveToLocal(getApplicationContext(), bitmap, "petpic-" + counter);
                                }
                                catch (Exception ex){
                                    Log.d("AddRecord-uPP", ex.getMessage());
                                }

                                //update counter
                                if (selectedPet == null){
                                    Toast.makeText(getApplicationContext(), "Record created successfully.", Toast.LENGTH_SHORT).show();
                                    counter ++;
                                    incrementCounter(counter);
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Changes may take some time to reflect.", Toast.LENGTH_SHORT).show();
                                }

                                //go back to previous screen
                                loadingDialog.dismissLoadingDialog();
                                onBackPressed();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingDialog.dismissLoadingDialog();
                                Toast.makeText(getApplicationContext(), "Request failed.", Toast.LENGTH_SHORT).show();
                                Log.d("AddRecord-uPP", e.getMessage());
                            }
                        });
            }
            catch (Exception e){
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(this, "Request canceled.", Toast.LENGTH_SHORT).show();
                Log.d("AddRecord-uPP", e.getMessage());
            }
        }
        else {
            loadingDialog.dismissLoadingDialog();
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
        }
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

    private void incrementCounter(int n){
        mReference = mDatabase.getReference("Pets").child("counter");
        mReference.setValue(n);
    }

    private void updateRecordList(){
        Intent intent = new Intent("update-record-list");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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
        Intent intent = new Intent(AddRecord.this, ManageRecords.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}