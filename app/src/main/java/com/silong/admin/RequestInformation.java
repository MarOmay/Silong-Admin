package com.silong.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silong.EnumClass.Gender;
import com.silong.EnumClass.PetAge;
import com.silong.EnumClass.PetColor;
import com.silong.EnumClass.PetSize;
import com.silong.EnumClass.PetType;
import com.silong.Object.Adoption;
import com.silong.Object.Pet;
import com.silong.Object.User;
import com.silong.Operation.EmailNotif;
import com.silong.Operation.ImageProcessor;
import com.silong.Operation.Utility;

import java.util.HashMap;
import java.util.Map;

public class RequestInformation extends AppCompatActivity {

    LinearLayout acceptBtn, declineBtn;

    ImageView reqInfoUserPicIv, reqInfoPetPicIv;
    TextView reqInfoUserNameTv, reqInfoUserEmailTv, reqInfoUserContactTv, reqInforUserAddressTv;
    TextView reqInfoGenderTypeTv, reqInfoAgeTv, reqInfoColorTv, reqInfoSizeTv, reqInfoSubmitDateTv;

    private User USER;
    private Pet PET;
    private String DATE;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_information);
        getSupportActionBar().hide();

        //initialize Firebase objects
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        boolean load_contents = true;
        try {
            String userID = getIntent().getStringExtra("userID");
            String petID = getIntent().getStringExtra("petID");
            DATE = getIntent().getStringExtra("dateRequested");

            USER = AdminData.getUser(userID);
            PET = AdminData.getPet(petID);

            if (USER == null){
                Toast.makeText(this, "Error: Account is invalid", Toast.LENGTH_SHORT).show();
                updateStatus("7");
                load_contents = false;
            }
            else if (PET == null) {
                Toast.makeText(this, "Error: Pet is invalid", Toast.LENGTH_SHORT).show();
                updateStatus("7");
                load_contents = false;
            }

        }
        catch (Exception e){
            Log.d("DEBUGGER>>>", "Failed to get userID or petID");
            gotoDashboard();
        }

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        //initialize views
        reqInfoUserPicIv = findViewById(R.id.reqInfoUserPicIv);
        reqInfoPetPicIv = findViewById(R.id.reqInfoPetPicIv);
        reqInfoUserNameTv = findViewById(R.id.reqInfoUserNameTv);
        reqInfoUserEmailTv = findViewById(R.id.reqInfoUserEmailTv);
        reqInfoUserContactTv = findViewById(R.id.reqInfoUserContactTv);
        reqInforUserAddressTv = findViewById(R.id.reqInforUserAddressTv);
        reqInfoGenderTypeTv = findViewById(R.id.reqInfoGenderTypeTv);
        reqInfoAgeTv = findViewById(R.id.reqInfoAgeTv);
        reqInfoColorTv = findViewById(R.id.reqInfoColorTv);
        reqInfoSizeTv = findViewById(R.id.reqInfoSizeTv);
        reqInfoSubmitDateTv = findViewById(R.id.reqInfoSubmitDateTv);;

        acceptBtn = (LinearLayout) findViewById(R.id.acceptBtn);
        declineBtn = (LinearLayout) findViewById(R.id.declineBtn);

        //display values
        if (load_contents)
            loadContents();
    }

    private void loadContents(){
        try {

            reqInfoUserPicIv.setImageBitmap(USER.getPhoto());
            reqInfoUserNameTv.setText(USER.getFirstName() + " " + USER.getLastName());
            reqInfoUserEmailTv.setText(USER.getEmail());
            reqInfoUserContactTv.setText(USER.getContact());
            reqInforUserAddressTv.setText(USER.getAddress().getAddressLine() + ", " + USER.getAddress().getBarangay());

            reqInfoPetPicIv.setImageBitmap(PET.getPhoto());

            String genderType = "";
            switch (PET.getGender()){
                case Gender.MALE: genderType = "Male"; break;
                case Gender.FEMALE: genderType = "Female"; break;
            }
            switch (PET.getType()){
                case PetType.DOG: genderType += " Dog"; break;
                case PetType.CAT: genderType += " Cat"; break;
            }
            reqInfoGenderTypeTv.setText(genderType);

            String age = "";
            switch (PET.getAge()){
                case PetAge.PUPPY: age = PET.getType() == PetType.DOG ? "Puppy" : "Kitten"; break;
                case PetAge.YOUNG: age = "Young"; break;
                case PetAge.OLD: age = "Old"; break;
            }
            reqInfoAgeTv.setText(age);

            String color = "";
            for (char c : PET.getColor().toCharArray()){
                switch (Integer.parseInt(c+"")){
                    case PetColor.BLACK: color += "Black "; break;
                    case PetColor.BROWN: color += "Brown "; break;
                    case PetColor.CREAM: color += "Cream "; break;
                    case PetColor.WHITE: color += "White "; break;
                    case PetColor.ORANGE: color += "Orange "; break;
                    case PetColor.GRAY: color += "Gray "; break;
                }
            }
            color.trim();
            color.replace(" ", " / ");
            reqInfoColorTv.setText(color);

            String size = "";
            switch (PET.getSize()){
                case PetSize.SMALL: size = "Small"; break;
                case PetSize.MEDIUM: size = "Medium"; break;
                case PetSize.LARGE: size = "Large"; break;
            }
            reqInfoSizeTv.setText(size);

            reqInfoSubmitDateTv.setText(DATE);
        }
        catch (Exception e){
            Toast.makeText(this, "Action can't be performed.", Toast.LENGTH_SHORT).show();
            Log.d("ReqInfo", e.getMessage());
        }
    }

    public void onPressedDecline(View view){
        updateStatus("7");

        //send email notif
        Adoption adoption = new Adoption();
        adoption.setPetID(Integer.parseInt(PET.getPetID()));
        EmailNotif emailNotif = new EmailNotif(USER.getEmail(), EmailNotif.DECLINED, adoption);
        emailNotif.sendNotif();

        //archive to user's RTDB
        DatabaseReference tempRef = mDatabase.getReference().child("Users").child(USER.getUserID()).child("adoptionHistory").child(PET.getPetID());
        Map<String, Object> map = new HashMap<>();
        map.put("dateRequested", DATE);
        map.put("status", 7); //7 == DECLINED
        tempRef.updateChildren(map);

        Utility.dbLog("Declined application. User:" + USER.getEmail() + " PetID:" + PET.getPetID());
    }

    public void onPressedAccept(View view){
        updateStatus("2");

        //send email notif
        Adoption adoption = new Adoption();
        adoption.setPetID(Integer.parseInt(PET.getPetID()));
        EmailNotif emailNotif = new EmailNotif(USER.getEmail(), EmailNotif.REQUEST_APPROVED, adoption);
        emailNotif.sendEmailApproval();

        new ImageProcessor().saveToLocal(getApplicationContext(), PET.getPhoto(), "approved-" + PET.getPetID());

        Utility.dbLog("Accepted application. User:" + USER.getEmail() + " PetID:" + PET.getPetID());
    }

    private void updateStatus(String status){
        //change status of request in RTDB
        Map<String, Object> map = new HashMap<>();
        map.put("dateRequested", DATE);
        map.put("petID", String.valueOf(PET.getPetID()));
        map.put("status", status);
        mReference = mDatabase.getReference().child("adoptionRequest").child(USER.getUserID());
        mReference.updateChildren(map);

        //change status of Pet in RTDB
        mReference = mDatabase.getReference().child("recordSummary").child(PET.getPetID());
        mReference.setValue(status.equals("2") ? null : 0);

        mReference = mDatabase.getReference().child("Pets").child(PET.getPetID()).child("status");
        mReference.setValue(status.equals("2") ? 2 : 0);

        Toast.makeText(this, "Adoption Request: " + (status.equals("2") ? "Approved" : "Declined"), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(RequestInformation.this, Dashboard.class);
        startActivity(intent);
        finish();
    }

    private void gotoDashboard(){
        Intent intent = new Intent(RequestInformation.this, Dashboard.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    public void back(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RequestInformation.this, RequestList.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}