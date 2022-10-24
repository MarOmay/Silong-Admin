package com.silong.admin;

import android.content.Intent;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.silong.Object.Admin;

import com.silong.Operation.Utility;

import java.util.HashMap;
import java.util.Map;

public class ProcessSignUp extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private Admin ADMIN;
    private String PASSWORD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_sign_up);
        getSupportActionBar().hide();

        //Initialize Firebase objects
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
        mDatabase = database.getReference("Admins");

        ADMIN = (Admin) getIntent().getSerializableExtra("DATA");
        PASSWORD = (String) getIntent().getStringExtra("PASSWORD");

        try{
            registerEmail();
        }
        catch (Exception e){
            Toast.makeText(ProcessSignUp.this, "There is a problem registering your email.", Toast.LENGTH_SHORT).show();
            Utility.log("PSU: " + e.getMessage());
            backToSignUp();
        }
    }

    private void registerEmail(){
        try {
            mAuth.createUserWithEmailAndPassword(ADMIN.getAdminEmail(), PASSWORD)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                try{
                                    //Sign into account first for auth rules
                                    mAuth.signInWithEmailAndPassword(ADMIN.getAdminEmail(), PASSWORD)
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    //Get uid from Firebase
                                                    ADMIN.setAdminID(mAuth.getCurrentUser().getUid());
                                                    saveAdminData();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(ProcessSignUp.this, "Database error. PSU", Toast.LENGTH_SHORT).show();
                                                    Utility.log("PSU: " + e.getMessage());
                                                    //Bring admin back to sign up page, and autofill the data
                                                    backToSignUp();
                                                }
                                            });
                                }
                                catch (Exception e){
                                    Toast.makeText(ProcessSignUp.this, "There is a problem getting you signed up.", Toast.LENGTH_SHORT).show();
                                    Utility.log("PSU: " + e.getMessage());
                                    backToSignUp();
                                }

                            }
                            else {
                                backToSignUp();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Utility.log("PSU: " + e.getMessage());

                            if (!Utility.internetConnection(getApplicationContext())){
                                Toast.makeText(ProcessSignUp.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                            }
                            else if (e instanceof FirebaseAuthInvalidCredentialsException){
                                Toast.makeText(ProcessSignUp.this, "Please use a valid email address.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(ProcessSignUp.this, "There is a problem getting you signed up.", Toast.LENGTH_SHORT).show();
                            }

                            //Bring user back to sign up page, and autofill the data
                            backToSignUp();
                        }
                    });
        }
        catch (Exception e){
            Toast.makeText(this, "Something went wrong. (PSU)", Toast.LENGTH_SHORT).show();
            Utility.log("PSU: " + e.getMessage());
            onBackPressed();
        }
    }

    private void saveAdminData(){
        Map<String, Object> map = new HashMap<>();
        map.put("firstName",ADMIN.getFirstName());
        map.put("lastName",ADMIN.getLastName());
        map.put("email", ADMIN.getAdminEmail());
        map.put("contact", ADMIN.getContact());
        map.put("accountStatus", true);
        map.put("roles/manageRequests", false);
        map.put("roles/appointments", false);
        map.put("roles/manageRecords", false);
        map.put("roles/manageReports", false);
        map.put("roles/editAgreement", false);
        map.put("roles/editContact", false);
        map.put("roles/editSchedule", false);
        map.put("roles/manageRoles", false);
        map.put("roles/manageDatabase", false);

        mDatabase.child(ADMIN.getAdminID()).updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Utility.dbLog("Create new Admin account: " + ADMIN.getAdminEmail());
                        Toast.makeText(ProcessSignUp.this, "New Admin account created for " + ADMIN.getAdminEmail(), Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Utility.log("PSU: " + e.getMessage());

                        if (!Utility.internetConnection(getApplicationContext())){
                            Toast.makeText(ProcessSignUp.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(ProcessSignUp.this, "There's a problem getting you signed up.", Toast.LENGTH_SHORT).show();
                        }
                        onBackPressed();
                    }
                });
    }

    private void backToSignUp(){
        Intent intent = new Intent(ProcessSignUp.this, CreateAdminAccount.class);
        intent.putExtra("SIGNUPDATA", ADMIN);
        startActivity(intent);
        finish();
    }
}