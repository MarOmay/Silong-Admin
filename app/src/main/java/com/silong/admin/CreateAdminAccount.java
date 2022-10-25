package com.silong.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.silong.Object.Admin;
import com.silong.Operation.InputValidator;
import com.silong.Operation.Utility;

public class CreateAdminAccount extends AppCompatActivity {

    ImageView createAdminBackIv;
    EditText createAdminFnameEt, createAdminLnameEt, createAdminEmailEt,
            createAdminContactEt, createAdminDesignation;
    Button createAdminCreateBtn;

    private FirebaseAuth mAuth;

    private Admin admin;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_admin_account);
        getSupportActionBar().hide();

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        //Initialize Firebase objects
        mAuth = FirebaseAuth.getInstance();

        createAdminBackIv = (ImageView) findViewById(R.id.createAdminBackIv);
        createAdminFnameEt = (EditText) findViewById(R.id.createAdminFnameEt);
        createAdminLnameEt = (EditText) findViewById(R.id.createAdminLnameEt);
        createAdminEmailEt = (EditText) findViewById(R.id.createAdminEmailEt);
        createAdminContactEt = (EditText) findViewById(R.id.createAdminContactEt);
        createAdminDesignation = (EditText) findViewById(R.id.createAdminDesignation);
        createAdminCreateBtn = (Button) findViewById(R.id.createAdminCreateBtn);

        createAdminCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validate entries before accepting response
                if(createAdminFnameEt.getText().toString().trim().length() < 1||
                        createAdminLnameEt.getText().toString().trim().length() < 1 ||
                        createAdminDesignation.getText().toString().trim().length() < 1 ||
                        createAdminEmailEt.getText().toString().trim().length() < 1 ||
                        createAdminContactEt.getText().toString().trim().length() < 1
                ){
                    Toast.makeText(getApplicationContext(), "Please answer all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Validate name
                else if (!InputValidator.checkName(createAdminFnameEt.getText().toString().trim())){
                    Toast.makeText(CreateAdminAccount.this, "Please check your first name.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (!InputValidator.checkName(createAdminLnameEt.getText().toString().trim())){
                    Toast.makeText(CreateAdminAccount.this, "Please check your last name.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Validate email
                else if (!InputValidator.checkEmail(createAdminEmailEt.getText().toString().trim())){
                    Toast.makeText(CreateAdminAccount.this, "Please check the format of your email.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Validate contact
                else if (!InputValidator.checkContact(createAdminContactEt.getText().toString().trim())){
                    Toast.makeText(CreateAdminAccount.this, "Please check your contact number.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Prepare data for transfer
                admin = new Admin();
                admin.setFirstName(createAdminFnameEt.getText().toString().trim());
                admin.setLastName(createAdminLnameEt.getText().toString().trim());
                admin.setAdminEmail(createAdminEmailEt.getText().toString().trim());
                admin.setContact(createAdminContactEt.getText().toString().trim());
                admin.setDesignation(createAdminDesignation.getText().toString().trim());

                password = admin.getFirstName().substring(0,1) + admin.getLastName().substring(0,1) + admin.getContact().substring(5, 11);

                checkEmail();

            }
        });

        createAdminBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void checkEmail(){
        //Check internet connection first
        if (Utility.internetConnection(CreateAdminAccount.this)){
            //Check if email is already registered
            mAuth.fetchSignInMethodsForEmail(admin.getAdminEmail())
                    .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            try {
                                if (task.getResult().getSignInMethods().isEmpty()){
                                    Intent i = new Intent(CreateAdminAccount.this, ProcessSignUp.class);
                                    i.putExtra("DATA", admin);
                                    i.putExtra("PASSWORD", password);
                                    startActivity(i);
                                    finish();
                                }
                                else {
                                    //Inform user that email is in use
                                    Toast.makeText(getApplicationContext(), "Email is already registered.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (Exception e){
                                Toast.makeText(CreateAdminAccount.this, "There is a problem checking your email.", Toast.LENGTH_SHORT).show();
                                Utility.log("CAA.cE: " + e.getMessage());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateAdminAccount.this, "There is a problem checking your email.", Toast.LENGTH_SHORT).show();
                            Utility.log("CAA.eC: " + e.getMessage());
                        }
                    });
        }
        else {
            Toast.makeText(this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    public void back(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}