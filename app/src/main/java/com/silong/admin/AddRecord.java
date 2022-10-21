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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.CustomView.LoadingDialog;
import com.silong.CustomView.RescuedDatePicker;
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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AddRecord extends AppCompatActivity {

    private final int PICK_IMAGE_1 = 1, PICK_IMAGE_2 = 2, PICK_IMAGE_3 = 3;

    ImageView addRecordPicIv1, addRecordPicIv2, addRecordPicIv3, addRecordBackIv, deleteIcon;
    Button saveRecordBtn;
    ChipGroup typeToggle, genderToggle, ageToggle, sizeToggle, colorToggle;
    EditText addMarks, addRescuedDate;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private Pet selectedPet;

    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);
        getSupportActionBar().hide();
        //heheheh testing lang

        //Initialize Firebase objects
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        initializeCounter();

        addRecordBackIv = (ImageView) findViewById(R.id.addRecordBackIv);
        addRecordPicIv1 = (ImageView) findViewById(R.id.addRecordPicIv1);
        addRecordPicIv2 = (ImageView) findViewById(R.id.addRecordPicIv2);
        addRecordPicIv3 = (ImageView) findViewById(R.id.addRecordPicIv3);
        typeToggle = findViewById(R.id.typeToggle);
        genderToggle = findViewById(R.id.genderToggle);
        ageToggle = findViewById(R.id.ageToggle);
        sizeToggle = findViewById(R.id.sizeToggle);
        colorToggle = findViewById(R.id.colorToggle);
        saveRecordBtn = (Button) findViewById(R.id.saveRecordBtn);
        addMarks = findViewById(R.id.addMarks);
        addRescuedDate = findViewById(R.id.addRescuedDate);

        deleteIcon = findViewById(R.id.deleteIcon);
        deleteIcon.setVisibility(View.INVISIBLE);
        deleteIcon.setClickable(false);
        deleteIcon.setEnabled(false);

        loadForEdit();
    }

    private boolean isEditMode = false;
    private void loadForEdit(){
        try {
            String id = getIntent().getStringExtra("id");
            selectedPet = AdminData.getPet(id);
            if (selectedPet != null) {
                //change header label
                TextView headerTv = findViewById(R.id.headerTv);
                headerTv.setText("Edit Record");

                deleteIcon.setVisibility(View.VISIBLE);
                deleteIcon.setClickable(true);
                deleteIcon.setEnabled(true);

                addRecordPicIv1.setImageBitmap(selectedPet.getPhoto());

                //load optional data
                File extrapic1 = new File(getFilesDir(), "extrapic-" + selectedPet.getPetID() + "-1");
                if (extrapic1.exists()){
                    Bitmap bmp = BitmapFactory.decodeFile(extrapic1.getAbsolutePath());
                    addRecordPicIv2.setImageBitmap(bmp);
                }
                File extrapic2 = new File(getFilesDir(), "extrapic-" + selectedPet.getPetID() + "-2");
                if (extrapic2.exists()){
                    Bitmap bmp = BitmapFactory.decodeFile(extrapic2.getAbsolutePath());
                    addRecordPicIv3.setImageBitmap(bmp);
                }
                if (selectedPet.getDistMark() != null){
                    addMarks.setText(selectedPet.getDistMark());
                }
                if (selectedPet.getRescueDate() != null){
                    addRescuedDate.setText(selectedPet.getRescueDate());
                }


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

                isEditMode = true;
            }
        }
        catch (Exception e){
            Utility.log("AddRecord.uPP: " + e.getMessage());
        }
    }
    
    public void onPressedDelete(View view){
        //check internet
        if (!Utility.internetConnection(AddRecord.this)){
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
            return;
        }

        String petID = selectedPet.getPetID();
        if (petID != null){
            //delete from recordSummary only (keeps the record in Pets)
            DatabaseReference tempRef = mDatabase.getReference().child("recordSummary").child(petID);
            tempRef.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //delete local copy
                    File file = new File(AddRecord.this.getFilesDir(), "pet-"+petID);
                    file.delete();
                    //notify user
                    Toast.makeText(AddRecord.this, "Record deleted successfully.", Toast.LENGTH_SHORT).show();

                    Utility.dbLog("Deleted record. PetID:" + petID);

                    //go back to Dashboard to trigger sync
                    Intent intent = new Intent(AddRecord.this, Dashboard.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddRecord.this, "Record deletion failed. (oFL)", Toast.LENGTH_SHORT).show();
                    Utility.log("AddRecord.uPP: " + e.getMessage());
                }
            });
        }
        else {
            Toast.makeText(this, "Record deletion failed.", Toast.LENGTH_SHORT).show();
        }

    }

    public void onPressedPhoto(View view){
        switch (view.getId()){
            case R.id.addRecordPicIv1:
                new ImagePicker(AddRecord.this, PICK_IMAGE_1);
                break;
            case R.id.addRecordPicIv2:
                if (addRecordPicIv1.getDrawable() == null){
                    Toast.makeText(this, "Pick a primary photo first", Toast.LENGTH_SHORT).show();
                    return;
                }
                new ImagePicker(AddRecord.this, PICK_IMAGE_2);
                break;
            case R.id.addRecordPicIv3:
                if (addRecordPicIv2.getDrawable() == null){
                    Toast.makeText(this, "Pick a secondary photo first", Toast.LENGTH_SHORT).show();
                    return;
                }
                new ImagePicker(AddRecord.this, PICK_IMAGE_3);
                break;
        }
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
        //check internet
        if (!Utility.internetConnection(AddRecord.this)){
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
            return;
        }

        //validate input
        if (addRecordPicIv1.getDrawable() == null){
            Toast.makeText(this, "Please select a photo.", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (new ImageProcessor().checkFileSize(addRecordPicIv1.getDrawable(), true) == false){
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
            //add photos
            ImageProcessor imageProcessor = new ImageProcessor();
            imageProcessor.checkFileSize(addRecordPicIv1.getDrawable(), true);
            Pet pet = new Pet();
            pet.setPhotoAsString(imageProcessor.toUTF8(addRecordPicIv1.getDrawable(), true));

            //add optional data
            if (addRecordPicIv2.getDrawable() != null){
                String temp = imageProcessor.toUTF8(addRecordPicIv2.getDrawable(), true);
                pet.getExtraPhotosAsString().add(temp);
            }
            if (addRecordPicIv3.getDrawable() != null){
                String temp = imageProcessor.toUTF8(addRecordPicIv3.getDrawable(), true);
                pet.getExtraPhotosAsString().add(temp);
            }
            if (!addMarks.getText().toString().isEmpty()){
                pet.setDistMark(addMarks.getText().toString().replace(":","").replace(";",""));
            }
            if (!addRescuedDate.getText().toString().isEmpty()){
                pet.setRescueDate(addRescuedDate.getText().toString());
            }


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

                String emailHolder = "EMAIL_NOT_FOUND";
                if (AdminData.adminEmail != null)
                    emailHolder = AdminData.adminEmail;
                else if (AdminData.firstName != null || AdminData.lastName != null)
                    emailHolder = AdminData.firstName + "_" + AdminData.lastName;

                Map<String, Object> map = new HashMap();
                map.put("status", PetStatus.ACTIVE);
                map.put("type", pet.getType());
                map.put("gender", pet.getGender());
                map.put("color", pet.getColor());
                map.put("age", pet.getAge());
                map.put("size", pet.getSize());
                map.put("photo", pet.getPhotoAsString());
                map.put("modifiedBy", emailHolder);
                map.put("lastModified", Utility.dateToday() + " " + Utility.timeNow());

                //optional data
                for (int i = 0; i < pet.getExtraPhotosAsString().size(); i++){
                    map.put("extraPhoto/photo"+(i+1), pet.getExtraPhotosAsString().get(i));
                }
                if (pet.getDistMark() != null){
                    map.put("distMark", pet.getDistMark());
                }
                if (pet.getRescueDate() != null){
                    map.put("rescueDate", pet.getRescueDate());
                }

                try {
                    if (selectedPet != null){
                        counter = Integer.parseInt(selectedPet.getPetID());
                    }
                }
                catch (Exception e){
                    Utility.log("AddRecord.uPP: " + e.getMessage());
                }

                mReference = mDatabase.getReference("Pets").child(String.valueOf(counter));
                mReference.updateChildren(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                mReference = mDatabase.getReference("recordSummary").child(String.valueOf(counter));
                                mReference.setValue(PetStatus.ACTIVE);

                                if (isEditMode)
                                    Utility.dbLog("Edited record. PetID:" + counter);
                                else
                                    Utility.dbLog("Published record. PetID:" + counter);

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

                                    //optional data
                                    for (int i=0; i < pet.getExtraPhotosAsString().size(); i++){
                                        bitmap = new ImageProcessor().toBitmap(pet.getExtraPhotosAsString().get(i));
                                        new ImageProcessor().saveToLocal(getApplicationContext(), bitmap, "extrapic-" + counter + "-" + (i+1));
                                    }
                                    if (pet.getDistMark() != null){
                                        AdminData.writePetToLocal(getApplicationContext(), String.valueOf(counter), "distMark", pet.getDistMark());
                                    }
                                    if (pet.getRescueDate() != null){
                                        AdminData.writePetToLocal(getApplicationContext(), String.valueOf(counter), "rescueDate", pet.getRescueDate());
                                    }

                                }
                                catch (Exception ex){
                                    Utility.log("AddRecord.uPP: " + ex.getMessage());
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

                                pet.setStatus(PetStatus.ACTIVE);
                                pet.setPetID(String.valueOf(counter));
                                pet.setModifiedBy(AdminData.adminEmail);
                                pet.setLastModified(Utility.dateToday() + " " + Utility.timeNow());
                                pet.setPhoto(new ImageProcessor().toBitmap(pet.getPhotoAsString()));

                                for (int i = 0; i < AdminData.pets.size(); i++){
                                    if (AdminData.pets.get(i).getPetID().equals(pet.getPetID())){
                                        AdminData.pets.set(i, pet);
                                    }
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
                                Utility.log("AddRecord.uPP: " + e.getMessage());
                            }
                        });
            }
            catch (Exception e){
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(this, "Request canceled.", Toast.LENGTH_SHORT).show();
                Utility.log("AddRecord.uPP: " + e.getMessage());
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
                        Utility.log("AddRecord.uPP: " + ex.getMessage());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Utility.log("AddRecord.uPP: " + error.getMessage());
                }
            });
        }
        catch (Exception e){
            mReference.setValue(0);
            counter = 0;
            Utility.log("AddRecord.uPP: " + e.getMessage());
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
        if (requestCode == PICK_IMAGE_1 || requestCode == PICK_IMAGE_2 || requestCode == PICK_IMAGE_3) {
            try{
                BufferedInputStream bufferedInputStream = new BufferedInputStream(getContentResolver().openInputStream(data.getData()));
                Bitmap bitmap = BitmapFactory.decodeStream(bufferedInputStream);

                if (!new ImageProcessor().checkFileSize(bitmap, true)) {
                    Toast.makeText(getApplicationContext(), "Please select a picture less than 5MB.", Toast.LENGTH_SHORT).show();
                    return;
                }

                bitmap = new ImageProcessor().tempCompress(bitmap);

                try {
                    switch (requestCode){
                        case PICK_IMAGE_1: addRecordPicIv1.setImageBitmap(bitmap); break;
                        case PICK_IMAGE_2: addRecordPicIv2.setImageBitmap(bitmap); break;
                        case PICK_IMAGE_3: addRecordPicIv3.setImageBitmap(bitmap); break;
                    }

                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Please select a picture less than 5MB.", Toast.LENGTH_SHORT).show();
                    Utility.log("AddRecord.uPP: " + e.getMessage());
                }

            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Unable to choose file", Toast.LENGTH_SHORT).show();
                Utility.log("AddRecord.uPP: " + e.getMessage());
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

    public void onPressedRescuedDate(View view){
        RescuedDatePicker rescuedDatePicker = new RescuedDatePicker(AddRecord.this, addRescuedDate);
        rescuedDatePicker.show(getSupportFragmentManager(), "rescude_date");
    }
}