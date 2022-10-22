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
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.silong.CustomView.EmailPrompt;
import com.silong.CustomView.LoadingDialog;
import com.silong.CustomView.ResetLinkNotice;
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
    static boolean passwordVisible = false;
    int ctr = 0;
    ImageView showHideIv;

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
        showHideIv = (ImageView) findViewById(R.id.showHideIv);

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
        String email = loginEmailEt.getText().toString().trim();
        String password = loginPasswordEt.getText().toString().trim();

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
                                                Utility.log("LogIn.aL: " + e.getMessage());
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Utility.log("LogIn.aL.oC: " + error.getMessage());
                                        }
                                    });
                                }
                                catch (Exception e){
                                    loadingDialog.dismissLoadingDialog();
                                    Toast.makeText(LogIn.this, "Login failed.", Toast.LENGTH_SHORT).show();
                                    Utility.log("LogIn.aL: " + e.getMessage());
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingDialog.dismissLoadingDialog();
                                Toast.makeText(LogIn.this, "Login failed.", Toast.LENGTH_SHORT).show();
                                Utility.log("LogIn.aL.oF: " + e.getMessage());
                            }
                        });
            }
            catch (Exception e){
                Utility.log("LogIn.aL: " + e.getMessage());
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
            mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    try {
                        AdminData.firstName = snapshot.child("firstName").getValue().toString();
                        AdminData.lastName = snapshot.child("lastName").getValue().toString();
                        AdminData.adminEmail = snapshot.child("email").getValue().toString();
                        AdminData.contact = snapshot.child("contact").getValue().toString();

                        AdminData.role_manageRequests = (boolean) snapshot.child("roles").child("manageRequests").getValue();
                        AdminData.role_appointments = (boolean) snapshot.child("roles").child("appointments").getValue();
                        AdminData.role_manageRecords = (boolean) snapshot.child("roles").child("manageRecords").getValue();
                        AdminData.role_manageReports = (boolean) snapshot.child("roles").child("manageReports").getValue();
                        AdminData.role_editAgreement = (boolean) snapshot.child("roles").child("editAgreement").getValue();
                        AdminData.role_editContact = (boolean) snapshot.child("roles").child("editContact").getValue();
                        AdminData.role_editSchedule = (boolean) snapshot.child("roles").child("editSchedule").getValue();
                        AdminData.role_manageRoles = (boolean) snapshot.child("roles").child("manageRoles").getValue();
                        AdminData.role_manageDatabase = (boolean) snapshot.child("roles").child("manageDatabase").getValue();

                        Toast.makeText(LogIn.this, "Welcome, " + AdminData.firstName + "!", Toast.LENGTH_SHORT).show();

                        //send first name to next activity
                        Intent intent = new Intent("update-first-name");
                        intent.putExtra("message", AdminData.firstName);
                        LocalBroadcastManager.getInstance(LogIn.this).sendBroadcast(intent);

                        new ImageProcessor().saveToLocal(getApplicationContext(), "userID", AdminData.adminID);
                        new ImageProcessor().saveToLocal(getApplicationContext(), "firstName", AdminData.firstName);
                        new ImageProcessor().saveToLocal(getApplicationContext(), "lastName", AdminData.lastName);
                        new ImageProcessor().saveToLocal(getApplicationContext(), "email", AdminData.adminEmail);
                        new ImageProcessor().saveToLocal(getApplicationContext(), "contact", AdminData.contact);

                        new ImageProcessor().saveToLocal(getApplicationContext(), "role_manageRequests", String.valueOf(AdminData.role_manageRequests));
                        new ImageProcessor().saveToLocal(getApplicationContext(), "role_appointments", String.valueOf(AdminData.role_appointments));
                        new ImageProcessor().saveToLocal(getApplicationContext(), "role_manageRecords", String.valueOf(AdminData.role_manageRecords));
                        new ImageProcessor().saveToLocal(getApplicationContext(), "role_manageReports", String.valueOf(AdminData.role_manageReports));
                        new ImageProcessor().saveToLocal(getApplicationContext(), "role_editAgreement", String.valueOf(AdminData.role_editAgreement));
                        new ImageProcessor().saveToLocal(getApplicationContext(), "role_editContact", String.valueOf(AdminData.role_editContact));
                        new ImageProcessor().saveToLocal(getApplicationContext(), "role_editSchedule", String.valueOf(AdminData.role_editSchedule));
                        new ImageProcessor().saveToLocal(getApplicationContext(), "role_manageRoles", String.valueOf(AdminData.role_manageRoles));
                        new ImageProcessor().saveToLocal(getApplicationContext(), "role_manageDatabase", String.valueOf(AdminData.role_manageDatabase));

                        loadingDialog.dismissLoadingDialog();
                        Intent gotoDashboard = new Intent(LogIn.this, Dashboard.class);
                        startActivity(gotoDashboard);
                        Utility.dbLog("Successful login.");

                        LogIn.this.finish();
                    }
                    catch (Exception e){
                        loadingDialog.dismissLoadingDialog();
                        Utility.log("LogIn.fAD: " + e.getMessage());
                        Toast.makeText(LogIn.this, "Database error. Please try again.", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Utility.log("LogIn.aL.oC: " + error.getMessage());
                }
            });

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
                            Utility.log("LogIn.eC: " + e.getMessage());
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
                        Utility.log("LogIn.rP: " + e.getMessage());
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
                Utility.log("LogIn.mMR: " + e.getMessage());
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
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    public void onShowHide(View view){
        if (ctr == 0){
            new Utility().passwordFieldTransformer(loginPasswordEt, true);
            showHideIv.setImageDrawable(getDrawable(R.drawable.ic_baseline_visibility_24));
            loginPasswordEt.setSelection(loginPasswordEt.getText().length());
            ctr++;
        }
        else {
            new Utility().passwordFieldTransformer(loginPasswordEt, false);
            showHideIv.setImageDrawable(getDrawable(R.drawable.ic_baseline_visibility_off_24));
            loginPasswordEt.setSelection(loginPasswordEt.getText().length());
            ctr--;
        }
    }
}