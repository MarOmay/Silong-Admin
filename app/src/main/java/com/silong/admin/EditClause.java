package com.silong.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silong.CustomView.ClauseDeleteDialog;
import com.silong.CustomView.LoadingDialog;
import com.silong.Object.AgreementData;
import com.silong.Operation.Utility;

public class EditClause extends AppCompatActivity {

    Button deleteClauseBtn, saveClauseBtn;
    EditText clauseTitle, clauseBody;
    ImageView editClausePlus;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private AgreementData agreementData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_clause);
        getSupportActionBar().hide();

        //initialize Firebase objects
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        //register receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mDeleteReceiver, new IntentFilter("confirm-delete"));

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        editClausePlus = findViewById(R.id.editClausePlus);
        clauseTitle = findViewById(R.id.clauseTitle);
        clauseBody = findViewById(R.id.clauseBody);
        deleteClauseBtn = findViewById(R.id.deleteClauseBtn);
        saveClauseBtn = findViewById(R.id.saveClauseBtn);

        try {
            agreementData = (AgreementData) getIntent().getSerializableExtra("agreementData");
            if (agreementData.getAgreementBody().isEmpty() || agreementData.getAgreementTitle().isEmpty()){
                Toast.makeText(this, "Error processing request", Toast.LENGTH_SHORT).show();
                onBackPressed();
                return;
            }
            //change header text
            TextView headerText = findViewById(R.id.headerText);
            headerText.setText("Edit Clause");
            //update fields
            clauseTitle.setText(agreementData.getAgreementTitle());
            clauseBody.setText(agreementData.getAgreementBody());
            editMode = true;
        }
        catch (Exception e){
            Utility.log("EditClause: Not editMode");
        }
    }

    public void back(View view) {
        onBackPressed();
    }

    public void onPressedSave(View view){

        String title = clauseTitle.getText().toString().trim();
        String content = clauseBody.getText().toString().trim();

        //validate input
        if (title.isEmpty() || content.isEmpty()){
            Toast.makeText(this, "Please fill out necessary information", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            //old entry, keep old date
            if (editMode){
                if (title.equals(agreementData.getAgreementTitle()) && content.equals(agreementData.getAgreementBody())){
                    Toast.makeText(this, "No changes made", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                    return;
                }
                else {
                    uploadData(agreementData.getAgreementDate(), title + Utility.CLAUSE_SEPARATOR + content);
                }
            }
            //new entry
            else {
                String dateTime = Utility.dateToday() + "--" + Utility.timeNow();
                uploadData(dateTime, title + Utility.CLAUSE_SEPARATOR + content);
            }
        }

    }

    public void onPressedDelete(View view){
        //check if editing or adding mode
        if (!editMode){
            clauseTitle.setText("");
            clauseBody.setText("");
            Toast.makeText(this, "Fields cleared.", Toast.LENGTH_SHORT).show();
        }
        else {
            ClauseDeleteDialog clauseDeleteDialog = new ClauseDeleteDialog(this, agreementData.getAgreementTitle());
            clauseDeleteDialog.show();
        }
    }

    private boolean editMode = false;
    private boolean deleteMode = false;
    private void uploadData(@NonNull String dateTime, @Nullable String data){

        LoadingDialog loadingDialog = new LoadingDialog(EditClause.this);
        loadingDialog.startLoadingDialog();

        //check internet connection
        if (!Utility.internetConnection(EditClause.this)){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            loadingDialog.dismissLoadingDialog();
            return;
        }

        try {
            mReference = mDatabase.getReference("publicInformation").child("adoptionAgreement").child(dateTime);
            mReference.setValue(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(EditClause.this, "Clause " + (editMode ? (deleteMode ? "deleted" : "updated") : "uploaded"), Toast.LENGTH_SHORT).show();
                            loadingDialog.dismissLoadingDialog();
                            goBackFresh();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditClause.this, "Can't process request", Toast.LENGTH_SHORT).show();
                            Utility.log("EditClause.uD.oF: " + e.getMessage());
                            loadingDialog.dismissLoadingDialog();
                        }
                    });
        }
        catch (Exception e){
            Utility.log("EditClause.oPS: " + e.getMessage());
            loadingDialog.dismissLoadingDialog();
        }
    }

    private void goBackFresh(){
        Intent intent = new Intent(EditClause.this, AdoptionAgreement.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }


    private BroadcastReceiver mDeleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                deleteMode = true;
                uploadData(agreementData.getAgreementDate(), null);
            }
            catch (Exception e){
                Toast.makeText(EditClause.this, "Can't process request", Toast.LENGTH_SHORT).show();
                Utility.log("EditClause.oPD: " + e.getMessage());
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDeleteReceiver);
        super.onDestroy();
    }
}