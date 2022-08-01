package com.silong.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.CustomDialog.EmailPrompt;
import com.silong.CustomDialog.LoadingDialog;
import com.silong.CustomDialog.ResetLinkNotice;
import com.silong.Operation.ImageProcessor;
import com.silong.Operation.InputValidator;
import com.silong.Operation.Utility;

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

        //Receive email
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mMessageReceiver, new IntentFilter("reset-password-email"));

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

    }

    public void onPressedLogin(View view){
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

    public void onPressedForgotPassword(View view){
        //Get email
        EmailPrompt emailPrompt = new EmailPrompt(LogIn.this);
        emailPrompt.show();
    }

    private void attemptLogin(String email, String password){
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();

        //Check internet connection first
        if (Utility.internetConnection(getApplicationContext())){
            //attempt to login
            try{
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                String uid = mAuth.getCurrentUser().getUid();
                                //Check if admin account
                                try {
                                    mReference = mDatabase.getReference("Admins/" + uid);
                                    mReference.child("accountStatus").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            boolean accountStatus = false;

                                            try {
                                                accountStatus = (Boolean) snapshot.getValue();

                                                if (accountStatus){
                                                    AdminData.adminID = uid;
                                                    loadingDialog.dismissLoadingDialog();
                                                    fetchAdminData();
                                                }
                                                else {
                                                    Toast.makeText(LogIn.this, "Unauthorized access.", Toast.LENGTH_SHORT).show();
                                                    loadingDialog.dismissLoadingDialog();
                                                }
                                            }
                                            catch (Exception e){
                                                Toast.makeText(LogIn.this, "Unauthorized access.", Toast.LENGTH_SHORT).show();
                                                loadingDialog.dismissLoadingDialog();
                                                Log.d("LogIn", e.getMessage());
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
        if (Utility.internetConnection(getApplicationContext())){
            //try to retrieve admin info
            try {
                mReference.child("firstName").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AdminData.firstName = snapshot.getValue().toString();
                        Toast.makeText(LogIn.this, "Welcome, " + AdminData.firstName + "!", Toast.LENGTH_SHORT).show();
                        new ImageProcessor().saveToLocal(getApplicationContext(), "firstName", AdminData.firstName);
                        //send first name to next activity
                        Intent intent = new Intent("update-first-name");
                        intent.putExtra("message", AdminData.firstName);
                        LocalBroadcastManager.getInstance(LogIn.this).sendBroadcast(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                mReference.child("lastName").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AdminData.lastName = snapshot.getValue().toString();
                        new ImageProcessor().saveToLocal(getApplicationContext(), "lastName", AdminData.lastName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                mReference.child("email").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AdminData.adminEmail = snapshot.getValue().toString();
                        new ImageProcessor().saveToLocal(getApplicationContext(), "email", AdminData.adminEmail);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                mReference.child("contact").addValueEventListener(new ValueEventListener() {
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

                loadingDialog.dismissLoadingDialog();
                Intent intent = new Intent(LogIn.this, Dashboard.class);
                startActivity(intent);
                finish();
            }
            catch (Exception e){
                loadingDialog.dismissLoadingDialog();
                Log.d("LogIn", e.getMessage());
                Toast.makeText(this, "Database error. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            loadingDialog.dismissLoadingDialog();
            Toast.makeText(this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }

    }

    private void emailChecker(Context context, String email){
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();
        //Check internet connection
        if(Utility.internetConnection(getApplicationContext())){
            //Check if email is registered
            mAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            loadingDialog.dismissLoadingDialog();
                            if (task.getResult().getSignInMethods().isEmpty()){
                                Toast.makeText(getApplicationContext(), "Email is not registered.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                //Trigger Firebase to send instruction email
                                resetPassword(context, email);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Can't check your email right now.", Toast.LENGTH_SHORT).show();
                            Log.d("LogIn", e.getMessage());
                        }
                    });
        }
        else {
            Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }
        loadingDialog.dismissLoadingDialog();

    }

    private void resetPassword(Context context, String email){
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();

        //Send a password reset link to email
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingDialog.dismissLoadingDialog();
                        if (task.isSuccessful()) {
                            //Show email instruction dialog
                            ResetLinkNotice resetLinkNotice = new ResetLinkNotice(LogIn.this);
                            resetLinkNotice.show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingDialog.dismissLoadingDialog();
                    }
                });
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String email = intent.getStringExtra("email");
                emailChecker(getApplicationContext(), email);
            }
            catch (Exception e){
                Log.d("LogIn", e.getMessage());
            }
        }
    };

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

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
}