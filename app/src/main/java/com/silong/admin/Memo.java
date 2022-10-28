package com.silong.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silong.CustomView.RescuedDatePicker;
import com.silong.Object.Adoption;
import com.silong.Object.AppointmentRecords;
import com.silong.Operation.EmailNotif;
import com.silong.Operation.Utility;

import java.util.HashMap;
import java.util.Map;

public class Memo extends AppCompatActivity {

    EditText memoDetails, memoDate;

    private FirebaseDatabase mDatabase;

    private Map<String, Object> MAP;
    private Adoption ADOPTION;
    private String USERID;
    private String NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);
        getSupportActionBar().hide();
        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        //initialize Firebase objects
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        memoDate = findViewById(R.id.memoDate);
        memoDetails = findViewById(R.id.memoDetails);

        memoDate.setText(Utility.dateToday().replace("-","/"));

        try {
            MAP = (Map<String, Object>) getIntent().getSerializableExtra("map");
            ADOPTION = (Adoption) getIntent().getSerializableExtra("adoption");
            USERID = getIntent().getStringExtra("userID");
            NAME = getIntent().getStringExtra("name");
        }
        catch (Exception e){
            Utility.log("Memo: " + e.getMessage());
            Toast.makeText(this, "Action can't be performed", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Memo.this, Dashboard.class));
            finish();
        }
    }

    public void onPressedDate(View view){
        RescuedDatePicker rescuedDatePicker = new RescuedDatePicker(Memo.this, memoDate);
        rescuedDatePicker.show(getSupportFragmentManager(), null);
    }

    public void onPressedComplete(View view){
        if (!Utility.internetConnection(Memo.this)){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        if (memoDate.getText().toString().length() <= 0 || memoDetails.getText().toString().length() <= 0){
            Toast.makeText(this, "Please fill out required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadToCloud(MAP, USERID, ADOPTION, NAME);
    }

    private void uploadToCloud(Map<String, Object> multiNodeMap, String userID, Adoption adoption, String name){

        multiNodeMap.put("Users/"+userID+"/adoptionHistory/"+adoption.getPetID()+"/actualAdotionDate", memoDate.getText().toString());
        multiNodeMap.put("Users/"+userID+"/adoptionHistory/"+adoption.getPetID()+"/memo", memoDetails.getText().toString());

        DatabaseReference mRef = mDatabase.getReference();
        mRef.updateChildren(multiNodeMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        //send email notif
                        String userEmail = AdminData.fetchAccountFromLocal(Memo.this, userID).getEmail();
                        EmailNotif emailNotif = new EmailNotif(userEmail, EmailNotif.ADOPTION_SUCCESSFUL, adoption);
                        emailNotif.sendNotif();

                        Toast.makeText(Memo.this, "Appointment confirmed!", Toast.LENGTH_SHORT).show();
                        for (AppointmentRecords ap : AdminData.appointments){
                            if (ap.getUserID().equals(userID))
                                AdminData.appointments.remove(ap);
                        }

                        Utility.dbLog("Tagged adoption as done. Client: " + name);

                        startActivity(new Intent(Memo.this, Dashboard.class));
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Memo.this, "Failed to process request", Toast.LENGTH_SHORT).show();
                        Utility.log("Memo.uTC: " + e.getMessage());
                    }
                });
    }

    public void back(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Memo.this, Dashboard.class));
        this.finish();
    }
}