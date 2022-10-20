package com.silong.CustomView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.Object.Adoption;
import com.silong.Operation.Utility;
import com.silong.admin.R;

public class RescheduleDialog extends MaterialAlertDialogBuilder {

    private Context context;
    private Activity activity;
    private String userID;
    private Adoption adoption;

    public TextView reschedDate;
    public TextView reschedTime;

    public RescheduleDialog(@NonNull Activity activity, String userID, Adoption adoption){
        super((Context) activity);
        this.activity = activity;
        context = (Context) activity;
        this.userID = userID;
        this.adoption = adoption;

        LayoutInflater inflater = activity.getLayoutInflater();
        View content = inflater.inflate(R.layout.reschedule_picker,null);

        reschedDate = content.findViewById(R.id.reschedDate);
        reschedTime = content.findViewById(R.id.reschedTime);

        String[] dateTime = adoption.getAppointmentDate().split(" ");
        String date = dateTime[0].replace("-", "/");
        String time = dateTime[1] + " " + dateTime[2];

        reschedDate.setText(date);
        reschedTime.setText(time);

        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setView(content);
        super.setCancelable(false);

        super.setPositiveButton(Html.fromHtml("<b>"+"SAVE"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                try {
                    String newDate = reschedDate.getText().toString();
                    String newTime = reschedTime.getText().toString();

                    if (newDate.equals(date) && newTime.equals(time)){
                        Toast.makeText(activity, "No changes made", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent = new Intent("reschedule-set");
                    intent.putExtra("userID", userID);
                    intent.putExtra("petID", String.valueOf(adoption.getPetID()));
                    Utility.log("RescedDia.oC: PetID-" + adoption.getPetID());
                    intent.putExtra("date", newDate);
                    intent.putExtra("time", newTime);
                    LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
                }
                catch (Exception e){
                    Toast.makeText(activity, "Can't process request", Toast.LENGTH_SHORT).show();
                    Utility.log("ReschedDia: " + e.getMessage());
                }

            }
        });

        super.setNegativeButton(Html.fromHtml("<b>"+"CANCEL"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //code here
            }
        });
    }
}
