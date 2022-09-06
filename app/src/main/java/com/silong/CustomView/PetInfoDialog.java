package com.silong.CustomView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.EnumClass.PetStatus;
import com.silong.Object.Pet;
import com.silong.admin.AdminData;
import com.silong.admin.R;

public class PetInfoDialog extends MaterialAlertDialogBuilder {

    private Activity activity;
    private String petID;
    private Context context;

    public PetInfoDialog(@NonNull Activity activity, String petID){
        super((Context) activity);

        this.activity = activity;
        this.petID = petID;
        this.context = (Context) activity;

        super.setTitle(Html.fromHtml("<b>"+"Record"+"</b>"));
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setMessage(Html.fromHtml("<b>" + prepareInfo() + "</b>"));

        super.setPositiveButton(Html.fromHtml("<b>" + "EDIT" + "</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent("show-selected-pet");
                intent.putExtra("id", petID);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });

        super.setNegativeButton(Html.fromHtml("<b>" + "CLOSE" + "</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //code here
            }
        });
    }

    private String prepareInfo(){

        String info = "";

        Pet pet = AdminData.getPet(petID);

        if (pet == null)
            return "No info found";

        String status = " ";
        switch (pet.getStatus()){
            case PetStatus.ACTIVE: status = "ACTIVE"; break;
            case PetStatus.ADOPTED: status = "ADOPTED"; break;
            case PetStatus.EUTHANIZED: status = "EUTHANIZED"; break;
            case PetStatus.PROCESSING: status = "PROCESSING"; break;
        }

        info += "Pet ID: " + pet.getPetID();
        info += "<br>Status: " + status;
        info += "<br><br>Modified by: " + pet.getModifiedBy();
        String lastModif; //prevent NullPointerException if pet.getPetID() == null
        if (pet.getLastModified() == null) lastModif = " ";
        else lastModif = pet.getLastModified().replace("*",":");
        info += "<br>Last Modified: " + lastModif;

        return info;
    }


}