package com.silong.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.Operation.ImageProcessor;
import com.silong.Operation.InputValidator;

public class LogIn extends AppCompatActivity {

    EditText loginEmailEt, loginPasswordEt;
    Button loginBtn;
    TextView forgotPasswordTv;

    private FirebaseAnalytics mAnalytics;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        getSupportActionBar().hide();

        //Initialize Firebase objects
        mAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        loginEmailEt = (EditText) findViewById(R.id.loginEmailEt);
        loginPasswordEt = (EditText) findViewById(R.id.loginPasswordEt);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        forgotPasswordTv = (TextView) findViewById(R.id.forgotPasswordTv);

        //for transpa status bar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = loginEmailEt.getText().toString();
                String password = loginPasswordEt.getText().toString();

                //Validate input
                if (email.length() < 1 || password.length() < 1){
                    Toast.makeText(LogIn.this, "Please fill out required fields.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (!InputValidator.checkEmail(email)){
                    Toast.makeText(LogIn.this, "Please check the format of your email.", Toast.LENGTH_SHORT).show();
                    return;
                }
                attemptLogin(email, password);
            }
        });

        forgotPasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent (LogIn.this, CreateAdminAccount.class);
                startActivity(i);
            }
        });
    }

    private void attemptLogin(String email, String password){
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();

        //Check internet connection first
        if (internetConnection()){
            //attempt to login
            try{
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                String uid = mAuth.getCurrentUser().getUid();
                                //Check if admin account
                                try {
                                    mReference = mDatabase.getReference("Admin");
                                    mReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            boolean found = false;
                                            for (DataSnapshot snap : snapshot.getChildren()){
                                                if (snap.getKey().equals(uid)){
                                                    AdminData.adminID = uid;
                                                    found = true;
                                                }
                                            }

                                            if (found){
                                                loadingDialog.dismissLoadingDialog();
                                                fetchAdminData();
                                            }
                                            else {
                                                Toast.makeText(LogIn.this, "Unauthorized access.", Toast.LENGTH_SHORT).show();
                                                loadingDialog.dismissLoadingDialog();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                catch (Exception e){
                                    loadingDialog.dismissLoadingDialog();
                                    Toast.makeText(LogIn.this, "Login failed.", Toast.LENGTH_SHORT).show();
                                    Log.d("LogIn", e.getMessage());
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingDialog.dismissLoadingDialog();
                                Toast.makeText(LogIn.this, "Login failed.", Toast.LENGTH_SHORT).show();
                                Log.d("LogIn", e.getMessage());
                            }
                        });
            }
            catch (Exception e){
                Log.d("LogIn", e.getMessage());
                loadingDialog.dismissLoadingDialog();
                Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
            }

        }
        else {
            loadingDialog.dismissLoadingDialog();
            Toast.makeText(this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    public void fetchAdminData(){
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();

        //Check internet connection first
        if (internetConnection()){
            //try to retrieve admin info
            try {
                mReference.child(AdminData.adminID).child("firstName").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AdminData.firstName = snapshot.getValue().toString();
                        Toast.makeText(LogIn.this, "Welcome, " + AdminData.firstName + "!", Toast.LENGTH_SHORT).show();
                        new ImageProcessor().saveToLocal(getApplicationContext(), "firstName", AdminData.firstName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                mReference.child(AdminData.adminID).child("lastName").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AdminData.lastName = snapshot.getValue().toString();
                        new ImageProcessor().saveToLocal(getApplicationContext(), "lastName", AdminData.lastName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                mReference.child(AdminData.adminID).child("email").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AdminData.adminEmail = snapshot.getValue().toString();
                        new ImageProcessor().saveToLocal(getApplicationContext(), "email", AdminData.adminEmail);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                mReference.child(AdminData.adminID).child("contact").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AdminData.contact = snapshot.getValue().toString();
                        new ImageProcessor().saveToLocal(getApplicationContext(), "contact", AdminData.contact);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //insert  adminInteraction

                Intent intent = new Intent(LogIn.this, Dashboard.class);
                startActivity(intent);
                finish();
            }
            catch (Exception e){
                Log.d("LogIn", e.getMessage());
                Toast.makeText(this, "Database error. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            loadingDialog.dismissLoadingDialog();
            Toast.makeText(this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }

    }

    //for transpa status bar
    public static void setWindowFlag(Activity activity, final int bits, boolean on) {

        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private boolean internetConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo!=null){
            return true;
        }
        return false;
    }
}