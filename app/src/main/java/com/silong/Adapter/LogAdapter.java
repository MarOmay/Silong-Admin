package com.silong.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.silong.Object.LogData;
import com.silong.admin.Log;
import com.silong.admin.R;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder>{

    LogData logData[];
    Activity activity;

    public LogAdapter (LogData[] logData, Activity activity){
        this.logData = logData;
        this.activity = activity;
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
        holder.logRecordDate.setText(logDataList.getLogRecordDate());
        holder.logRecordDesc.setText(logDataList.getLogRecordDesc());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, logDataList.getLogRecordDesc(), Toast.LENGTH_SHORT).show();
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
