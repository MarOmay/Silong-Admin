package com.silong.CustomView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.Operation.Utility;
import com.silong.admin.CreateReport;
import com.silong.admin.R;

public class DatabaseWarningDialog extends MaterialAlertDialogBuilder {

    private Activity activity;
    private Context context;

    public DatabaseWarningDialog(@NonNull Activity activity){
        super((Context) activity);

        this.activity = activity;
        this.context = (Context) activity;

        super.setTitle(Html.fromHtml("<b>"+"Warning"+"</b>"));
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setMessage(context.getString(R.string.warningDia));

        LinearLayout createReportLayout = new LinearLayout(context);
        createReportLayout.setOrientation(LinearLayout.VERTICAL);
        createReportLayout.setVerticalGravity(10);
        TextView createReportTv = new TextView(context);
        createReportTv.setPaintFlags(createReportTv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        createReportTv.setText("Create Report");
        createReportTv.setTextColor(context.getResources().getColor(R.color.purple_700));
        createReportTv.setPadding(60,40,0,0);
        createReportTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utility.internetConnection(activity)){
                    Toast.makeText(activity, "No internet connection", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent i = new Intent(context, CreateReport.class);
                    context.startActivity(i);
                }

            }
        });
        createReportLayout.addView(createReportTv);

        super.setView(createReportLayout);

        super.setPositiveButton(Html.fromHtml("<b>"+"DELETE"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent("delete-authorized"));
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
