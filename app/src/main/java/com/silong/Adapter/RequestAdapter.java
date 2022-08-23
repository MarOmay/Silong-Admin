package com.silong.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.silong.Object.RequestsData;
import com.silong.admin.R;
import com.silong.admin.RequestList;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder>{

    RequestsData[] requestsData;
    Context context;

    public RequestAdapter(RequestsData[] requestsData, RequestList activity){
        this.requestsData = requestsData;
        this.context = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.requests_item_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final RequestsData requestsDataList = requestsData[position];
        holder.requestUserName.setText(requestsDataList.getRequestUserName());
        holder.requestDate.setText(requestsDataList.getRequestDate());
        holder.requestUserPic.setImageResource(requestsDataList.getRequestUserPic());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, requestsDataList.getRequestUserName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestsData.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView requestUserPic;
        TextView requestUserName;
        TextView requestDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            requestUserPic = itemView.findViewById(R.id.requestUserPicIv);
            requestUserName = itemView.findViewById(R.id.requestUserNameTv);
            requestDate = itemView.findViewById(R.id.requestDateTv);
        }
    }
}

