package com.silong.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.silong.CustomView.LogDetailsDialog;
import com.silong.Object.LogData;
import com.silong.admin.Log;
import com.silong.admin.R;

import java.util.Calendar;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder>{

    LogData logData[];
    Activity activity;

    public LogAdapter (LogData[] logData, Activity activity){
        this.logData = logData;
        this.activity = activity;

        Log.EXPORTABLE.clear();
        generateExportable();
    }

    private void generateExportable(){
        for (LogData log : logData){
            if (Log.customDate){

                String[] fromDate = Log.dateFrom.split("/");
                Calendar from = Calendar.getInstance();
                from.set(Integer.valueOf(fromDate[2]),Integer.valueOf(fromDate[0]),Integer.valueOf(fromDate[1]));

                String[] toDate = Log.dateTo.split("/");
                Calendar to = Calendar.getInstance();
                to.set(Integer.valueOf(toDate[2]),Integer.valueOf(toDate[0]),Integer.valueOf(toDate[1]));

                String[] logDate = log.getDate().split("/");
                Calendar logg = Calendar.getInstance();
                logg.set(Integer.valueOf(logDate[2]),Integer.valueOf(logDate[0]),Integer.valueOf(logDate[1]));

                if (!logg.after(to) && !logg.before(from)){
                    Log.EXPORTABLE.add(log);
                }
            }

        }
    }

    @NonNull
    @Override
    public LogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.logs_item_list, parent, false);
        LogAdapter.ViewHolder viewHolder = new LogAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LogAdapter.ViewHolder holder, int position) {
        final LogData logDataList = logData[position];
        holder.logRecordDate.setText(logDataList.getDate());
        holder.logRecordDesc.setText(logDataList.getDescription());

        //check if date range is set, then filter put logs if needed
        if (Log.customDate){

            String[] fromDate = Log.dateFrom.split("/");
            Calendar from = Calendar.getInstance();
            from.set(Integer.valueOf(fromDate[2]),Integer.valueOf(fromDate[0]),Integer.valueOf(fromDate[1]));

            String[] toDate = Log.dateTo.split("/");
            Calendar to = Calendar.getInstance();
            to.set(Integer.valueOf(toDate[2]),Integer.valueOf(toDate[0]),Integer.valueOf(toDate[1]));

            String[] logDate = logDataList.getDate().split("/");
            Calendar log = Calendar.getInstance();
            log.set(Integer.valueOf(logDate[2]),Integer.valueOf(logDate[0]),Integer.valueOf(logDate[1]));

            if (log.after(to) || log.before(from)){
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogDetailsDialog dialog = new LogDetailsDialog(activity, logDataList);
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() { return logData.length; }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView logRecordDate;
        TextView logRecordDesc;

        public ViewHolder (@NonNull View itemView){
            super(itemView);
             logRecordDate = itemView.findViewById(R.id.logRecordDate);
             logRecordDesc = itemView.findViewById(R.id.logRecordDesc);
        }
    }
}
