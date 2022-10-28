package com.silong.CustomView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.Operation.Utility;
import com.silong.admin.AdminData;
import com.silong.admin.R;

public class ReminderDialog extends MaterialAlertDialogBuilder {

    private Activity activity;
    private Context context;
    private EditText reminderDate;

    public ReminderDialog (@NonNull Activity activity, FragmentManager fragmentManager){
        super((Context) activity);
        this.activity = activity;
        context = (Context) activity;

        LayoutInflater inflater = activity.getLayoutInflater();
        View content = inflater.inflate(R.layout.reminder_dialog,null);

        reminderDate = content.findViewById(R.id.reminderDate);

        if ( AdminData.DATABASE_MAINTENANCE_DATE.length() > 0){
            reminderDate.setText(AdminData.DATABASE_MAINTENANCE_DATE.replace("-","/"));
        }
        else {
            reminderDate.setText(Utility.dateToday().replace("-","/"));
        }

        reminderDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RescuedDatePicker rescuedDatePicker = new RescuedDatePicker(activity, reminderDate);
                rescuedDatePicker.show(fragmentManager, null);

            }
        });

        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setView(content);

        super.setPositiveButton(Html.fromHtml("<b>"+"SET"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (reminderDate.getText().toString().equals(AdminData.DATABASE_MAINTENANCE_DATE.replace("-","/"))
                    && reminderDate.getText().toString().length() > 0){
                    Toast.makeText(activity, "Can't set same day reminder", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent("reminder-set");
                    intent.putExtra("date",reminderDate.getText().toString().replace("/","-"));
                    LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
                }
            }
        });

        super.setNegativeButton(Html.fromHtml("<b>"+"CANCEL"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //codes here
            }
        });
    }
}
