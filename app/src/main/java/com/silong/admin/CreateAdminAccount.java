package com.silong.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.silong.Object.Admin;
import com.silong.Operation.InputValidator;

public class CreateAdminAccount extends AppCompatActivity {

    ImageView createAdminBackIv;
    EditText createAdminFnameEt, createAdminLnameEt, createAdminEmailEt,
            createAdminContactEt, createAdminPasswordEt, createAdminConfirmpassEt;
    Button createAdminCreateBtn;

    private FirebaseAnalytics mAnalytics;
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
        mAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();

        createAdminBackIv = (ImageView) findViewById(R.id.createAdminBackIv);
        createAdminFnameEt = (EditText) findViewById(R.id.createAdminFnameEt);
        createAdminLnameEt = (EditText) findViewById(R.id.createAdminLnameEt);
        createAdminEmailEt = (EditText) findViewById(R.id.createAdminEmailEt);
        createAdminContactEt = (EditText) findViewById(R.id.createAdminContactEt);
        createAdminPasswordEt = (EditText) findViewById(R.id.createAdminPasswordEt);
        createAdminConfirmpassEt = (EditText) findViewById(R.id.createAdminConfirmpassEt);
        createAdminCreateBtn = (Button) findViewById(R.id.createAdminCreateBtn);

        createAdminCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validate entries before accepting response
                if(createAdminFnameEt.getText().toString().trim().length() < 1||
                        createAdminLnameEt.getText().toString().trim().length() < 1 ||
                        createAdminPasswordEt.getText().toString().trim().length() < 1 ||
                        createAdminConfirmpassEt.getText().toString().trim().length() < 1 ||
                        createAdminEmailEt.getText().toString().trim().length() < 1 ||
                        createAdminContactEt.getText().toString().trim().length() < 1
                ){
                    Toast.makeText(getApplicationContext(), "Please answer all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (createAdminPasswordEt.getText().length() < 8){
                    Toast.makeText(getApplicationContext(), "Password must be at least 8 characters.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (!createAdminPasswordEt.getText().toString().equals(createAdminConfirmpassEt.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Password does not match.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Validate name
                else if (!InputValidator.checkName(createAdminFnameEt.getText().toString())){
                    Toast.makeText(CreateAdminAccount.this, "Please check your first name.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (!InputValidator.checkName(createAdminLnameEt.getText().toString())){
                    Toast.makeText(CreateAdminAccount.this, "Please check your last name.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Validate email
                else if (!InputValidator.checkEmail(createAdminEmailEt.getText().toString())){
                    Toast.makeText(CreateAdminAccount.this, "Please check the format of your email.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Validate contact
                else if (!InputValidator.checkContact(createAdminContactEt.getText().toString())){
                    Toast.makeText(CreateAdminAccount.this, "Please check your contact number.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Prepare data for transfer
                admin = new Admin();
                admin.setFirstName(createAdminFnameEt.getText().toString());
                admin.setLastName(createAdminLnameEt.getText().toString());
                admin.setAdminEmail(createAdminEmailEt.getText().toString());
                admin.setContact(createAdminContactEt.getText().toString());

                password = createAdminPasswordEt.getText().toString();

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
        if (internetConnection()){
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
                                Log.d("CAA", e.getMessage());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateAdminAccount.this, "There is a problem checking your email.", Toast.LENGTH_SHORT).show();
                            Log.d("CAA", e.getMessage());
                        }
                    });
        }
        else {
            Toast.makeText(this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean internetConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo!=null){
            return true;
        }
        return false;
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