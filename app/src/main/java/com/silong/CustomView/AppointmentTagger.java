package com.silong.CustomView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.Object.Adoption;
import com.silong.Object.AppointmentRecords;
import com.silong.Operation.EmailNotif;
import com.silong.Operation.ImageProcessor;
import com.silong.Operation.Utility;
import com.silong.admin.AdminData;
import com.silong.admin.AppointmentsList;
import com.silong.admin.R;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AppointmentTagger extends MaterialAlertDialogBuilder {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private Adoption ADOPTION;

    ImageView taggerPetPic;
    TextView taggerPetId,taggerDateTime;

    private Activity activity;

    public AppointmentTagger(@NonNull Activity activity, String userID, String name, Adoption adoption) {
        super((Context) activity);
        Context context = (Context) activity;

        this.activity = activity;

        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setTitle(name);
        super.setMessage("Tag this application as done?\n");

        LayoutInflater inflater = activity.getLayoutInflater();
        View content = inflater.inflate(R.layout.appointment_tagger_info,null);
        taggerPetPic =  content.findViewById(R.id.taggerPetPic);
        taggerPetId = content.findViewById(R.id.taggerPetId);
        taggerDateTime = content.findViewById(R.id.taggerDateTime);

        //display petID
        taggerPetId.setText("PetID: " + adoption.getPetID());

        //diplay appointment date and time
        String[] dateTime = adoption.getAppointmentDate().split(" ");
        taggerDateTime.setText(dateTime[0].replace("-","/") + "\n" + dateTime[1] + " " + dateTime[2]);

        //fetch and display pet photo
        File pic = new File(activity.getFilesDir(), "approved-"+adoption.getPetID());
        if (pic.exists()){
            Bitmap bmp = BitmapFactory.decodeFile(activity.getFilesDir() + "/approved-" + adoption.getPetID());
            taggerPetPic.setImageBitmap(bmp);
            attachListener();
        }
        else {
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
            DatabaseReference mRef = mDatabase.getReference("Pets").child(String.valueOf(adoption.getPetID())).child("photo");
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        String photoAsString = snapshot.getValue().toString();
                        Bitmap bmp = new ImageProcessor().toBitmap(photoAsString);
                        taggerPetPic.setImageBitmap(bmp);
                        attachListener();
                        new ImageProcessor().saveToLocal(activity, bmp, "approved-" + adoption.getPetID());
                    }
                    catch (Exception e){
                        Toast.makeText(activity, "Can't fetch pet photo", Toast.LENGTH_SHORT).show();
                        Utility.log("AppointmentTagger: " + e.getMessage());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(activity, "Can't fetch pet photo", Toast.LENGTH_SHORT).show();
                    Utility.log("AppointmentTagger: " + error.getMessage());
                }
            });
        }

        super.setView(content);

        this.ADOPTION = adoption;

        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        super.setPositiveButton(Html.fromHtml("<b>"+"YES"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //update adoptionRequest
                DatabaseReference sRef = mDatabase.getReference().child("adoptionRequest").child(userID).child("status");
                sRef.setValue("5");

                //reset cancellation counter
                DatabaseReference cRef = mDatabase.getReference().child("Users").child(userID).child("cancellation");
                cRef.setValue(0);

                //send email notif
                String userEmail = AdminData.getUser(userID).getEmail();
                EmailNotif emailNotif = new EmailNotif(userEmail, EmailNotif.ADOPTION_SUCCESSFUL, ADOPTION);
                emailNotif.sendNotif();

                Toast.makeText(activity, "Appointment confirmed!", Toast.LENGTH_SHORT).show();
                for (AppointmentRecords ap : AdminData.appointments){
                    if (ap.getUserID().equals(userID))
                        AdminData.appointments.remove(ap);
                }
                activity.onBackPressed();

                Utility.dbLog("Tagged adoption as done. Client: " + name);
            }
        });
        super.setNegativeButton(Html.fromHtml("<b>"+"DELETE"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AppointmentCancel appointmentCancel = new AppointmentCancel(activity, userID, ADOPTION);
                appointmentCancel.show();
            }
        });

        super.setNeutralButton(Html.fromHtml("<b>" + "RESCHEDULE" + "</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent = new Intent("reschedule-trigger");
                intent.putExtra("userID", userID);
                intent.putExtra("adoption", (Serializable) adoption);
                LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);

            }
        });
    }

    private void attachListener(){
        taggerPetPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageDialog imageDialog = new ImageDialog(activity, taggerPetPic.getDrawable());
                imageDialog.show();
            }
        });
    }
}
