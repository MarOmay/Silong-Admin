package com.silong.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silong.CustomView.LoadingDialog;
import com.silong.Object.Admin;
import com.silong.Operation.Utility;

import java.util.HashMap;
import java.util.Map;


public class AdminRoles extends AppCompatActivity {

    private MaterialCheckBox manageReqCb, appointmentsCb, manageRecCb, manageRepCb, editAgreeCb, editContactCb, editSchedCb, manageRolesCb;
    private TextView adminName;
    private Admin admin;

    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_roles);
        getSupportActionBar().hide();
        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        //initialize Firebase objects
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        adminName = findViewById(R.id.adminName);
        manageReqCb = findViewById(R.id.manageReqCb);
        appointmentsCb = findViewById(R.id.appointmentsCb);
        manageRecCb = findViewById(R.id.manageRecCb);
        manageRepCb = findViewById(R.id.manageRepCb);
        editAgreeCb = findViewById(R.id.editAgreeCb);
        editContactCb = findViewById(R.id.editContactCb);
        editSchedCb = findViewById(R.id.editSchedCb);
        manageRolesCb = findViewById(R.id.manageRolesCb);

        //extract data
        try {
            admin = (Admin) getIntent().getSerializableExtra("ADMIN");
            //update UI
            adminName.setText(admin.getFirstName() + " " + admin.getLastName());
            manageReqCb.setChecked(admin.isRole_manageRequests());
            appointmentsCb.setChecked(admin.isRole_appointments());
            manageRecCb.setChecked(admin.isRole_manageRecords());
            manageRepCb.setChecked(admin.isRole_manageReports());
            editAgreeCb.setChecked(admin.isRole_editAgreement());
            editContactCb.setChecked(admin.isRole_editContact());
            editSchedCb.setChecked(admin.isRole_editSchedule());
            manageRolesCb.setChecked(admin.isRole_manageRoles());
            //manageDatabase
        }
        catch (Exception e){
            Toast.makeText(this, "Can't process request", Toast.LENGTH_SHORT).show();
            Utility.log("AdminRoles.oC: " + e.getMessage());
            onBackPressed();
        }

    }

    public void onPressedCheckBox(View view){
        if (!Utility.internetConnection(AdminRoles.this)){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        LoadingDialog loadingDialog = new LoadingDialog(AdminRoles.this);
        loadingDialog.startLoadingDialog();

        try {
            //gather data
            boolean manageRequests = manageReqCb.isChecked();
            boolean appointments = appointmentsCb.isChecked();
            boolean manageRecords = manageRecCb.isChecked();
            boolean manageReports = manageRepCb.isChecked();
            boolean editAgreement = editAgreeCb.isChecked();
            boolean editContact = editContactCb.isChecked();
            boolean editSchedule = editSchedCb.isChecked();
            boolean manageRoles = manageRolesCb.isChecked();
            boolean manageDatabase = false;

            //prepare data
            Map<String, Object> map = new HashMap<>();
            map.put("manageRequests", manageRequests);
            map.put("appointments", appointments);
            map.put("manageRecords", manageRecords);
            map.put("manageReports", manageReports);
            map.put("editAgreement", editAgreement);
            map.put("editContact", editContact);
            map.put("editSchedule", editSchedule);
            map.put("manageRoles", manageRoles);
            map.put("manageDatabase", manageDatabase);

            DatabaseReference mRef = mDatabase.getReference("Admins").child(admin.getAdminID()).child("roles");
            mRef.updateChildren(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            //Set local values
                            admin.setRole_manageRequests(manageRequests);
                            admin.setRole_appointments(appointments);
                            admin.setRole_manageRecords(manageRecords);
                            admin.setRole_manageReports(manageReports);
                            admin.setRole_editAgreement(editAgreement);
                            admin.setRole_editContact(editContact);
                            admin.setRole_editSchedule(editSchedule);
                            admin.setRole_manageRoles(manageRoles);
                            admin.setRole_manageDatabase(manageDatabase);

                            Utility.dbLog("Updated roles for " + admin.getAdminEmail());
                            Toast.makeText(AdminRoles.this, "Roles updated", Toast.LENGTH_SHORT).show();
                            loadingDialog.dismissLoadingDialog();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AdminRoles.this, "Operation cancelled", Toast.LENGTH_SHORT).show();
                            loadingDialog.dismissLoadingDialog();
                        }
                    });
        }
        catch (Exception e){
            Toast.makeText(this, "Request failed", Toast.LENGTH_SHORT).show();
            Utility.log("AdminRoles.oPCB: " + e.getMessage());
            loadingDialog.dismissLoadingDialog();
        }
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}