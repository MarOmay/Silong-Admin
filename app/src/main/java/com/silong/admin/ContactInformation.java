package com.silong.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.CustomView.EditContactDialog;
import com.silong.CustomView.LoadingDialog;
import com.silong.Operation.Utility;

public class ContactInformation extends AppCompatActivity {

    private FirebaseDatabase mDatabase;

    private String FBPAGE = "";
    private String EMAIL = "";
    private String PHONE = "";
    private String TELEPHONE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_information);
        getSupportActionBar().hide();

        //initialize Firebase objects
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        //register receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mConfirmReceiver, new IntentFilter("update-contact-info"));

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        fetchContactInfoFromCloud();

    }

    public void onPressedFacebook(View view){
        EditContactDialog editContactDialog = new EditContactDialog(ContactInformation.this, EditContactDialog.FACEBOOK_PAGE, FBPAGE);
        editContactDialog.show();
    }

    public void onPressedEmail(View view){
        EditContactDialog editContactDialog = new EditContactDialog(ContactInformation.this, EditContactDialog.EMAIL_ADDRESS, EMAIL);
        editContactDialog.show();
    }

    public void onPressedPhone(View view){
        EditContactDialog editContactDialog = new EditContactDialog(ContactInformation.this, EditContactDialog.MOBILE_NUMBER, PHONE);
        editContactDialog.show();
    }

    public void onPressedTelephone(View view){
        EditContactDialog editContactDialog = new EditContactDialog(ContactInformation.this, EditContactDialog.TELEPHONE_NUMBER, TELEPHONE);
        editContactDialog.show();
    }

    private void fetchContactInfoFromCloud(){
        if (!Utility.internetConnection(ContactInformation.this)){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        LoadingDialog loadingDialog = new LoadingDialog(ContactInformation.this);
        loadingDialog.startLoadingDialog();

        try {

            DatabaseReference mRef = mDatabase.getReference("publicInformation").child("contactInformation");
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    //get required data
                    for (DataSnapshot snap : snapshot.getChildren()){

                        switch (snap.getKey()){
                            case "facebookPage": FBPAGE = snap.getValue().toString(); break;
                            case "email": EMAIL = snap.getValue().toString(); break;
                            case "phone": PHONE = snap.getValue().toString(); break;
                            case "telephone": TELEPHONE = snap.getValue().toString(); break;
                        }

                    }

                    loadingDialog.dismissLoadingDialog();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    loadingDialog.dismissLoadingDialog();
                    Utility.log("ContactInfo.fCIFC.oC: " + error.getMessage());
                }
            });

        }
        catch (Exception e){
            Toast.makeText(this, "Failed fetch data from cloud", Toast.LENGTH_SHORT).show();
            Utility.log("ContactInfo.fCIFC: " + e.getMessage());
            loadingDialog.dismissLoadingDialog();
        }
    }

    private BroadcastReceiver mConfirmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            LoadingDialog loadingDialog = new LoadingDialog(ContactInformation.this);
            loadingDialog.startLoadingDialog();

            try {
                int infoType = intent.getIntExtra("infoType", -1);
                String newInfo = intent.getStringExtra("newInfo");
                String childNode = "";
                String plainText = "";

                switch (infoType){
                    case EditContactDialog.FACEBOOK_PAGE:
                        childNode = "facebookPage";
                        plainText = "Facebook Page";
                        break;
                    case EditContactDialog.EMAIL_ADDRESS:
                        childNode = "email";
                        plainText = "Email Address";
                        break;
                    case EditContactDialog.MOBILE_NUMBER:
                        childNode = "phone";
                        plainText = "Cellphone Number";
                        break;
                    case EditContactDialog.TELEPHONE_NUMBER:
                        childNode = "telephone";
                        plainText = "Telephone Number";
                        break;
                }

                if (childNode.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Error processing request", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    DatabaseReference mRef = mDatabase.getReference("publicInformation").child("contactInformation").child(childNode);
                    String finalPlainText = plainText;
                    mRef.setValue(newInfo)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    switch (infoType){
                                        case EditContactDialog.FACEBOOK_PAGE:
                                            FBPAGE = newInfo;
                                            break;
                                        case EditContactDialog.EMAIL_ADDRESS:
                                            EMAIL = newInfo;
                                            break;
                                        case EditContactDialog.MOBILE_NUMBER:
                                            PHONE = newInfo;
                                            break;
                                        case EditContactDialog.TELEPHONE_NUMBER:
                                            TELEPHONE = newInfo;
                                            break;
                                    }

                                    Utility.dbLog("Updated " + finalPlainText + " to " + newInfo);

                                    Toast.makeText(getApplicationContext(), "Successfully updated!", Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismissLoadingDialog();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Failed to update", Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismissLoadingDialog();
                                    Utility.log("ContactInfo.mCR.oC: " + e.getMessage());
                                }
                            });
                }


            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Error processing request", Toast.LENGTH_SHORT).show();
                loadingDialog.dismissLoadingDialog();
                Utility.log("ContactInfo.mCR: " + e.getMessage());
            }

        }
    };

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mConfirmReceiver);
        super.onDestroy();
    }
}