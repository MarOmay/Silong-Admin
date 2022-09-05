package com.silong.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.silong.CustomView.AppointmentReqDialog;
import com.silong.EnumClass.RequestCode;
import com.silong.Object.Request;
import com.silong.Object.User;
import com.silong.admin.AdminData;
import com.silong.admin.ManageAccount;
import com.silong.admin.R;
import com.silong.admin.RequestInformation;
import com.silong.admin.RequestList;

import java.util.ArrayList;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder>{

    ArrayList<Request> requests;
    Context context;
    Activity activity;

    public RequestAdapter(ArrayList<Request> requests, RequestList activity){
        this.requests = requests;
        this.context = activity;
        this.activity = activity;
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
        final Request request = requests.get(position);

        User user = AdminData.getUser(request.getUserID());

        holder.requestUserName.setText(user.getFirstName() + " " + user.getLastName());
        holder.requestDate.setText(request.getDate());
        holder.requestUserPic.setImageBitmap(user.getPhoto());

        //sort request icon
        int icon = 0;
        switch (request.getRequestCode()){
            case RequestCode.ADOPTION_REQUEST: icon = R.drawable.req_adoption_icon; break;
            case RequestCode.APPOINTMENT: icon = R.drawable.req_appointment_icon; break;
            case RequestCode.ACCOUNT_ACTIVATION: icon = R.drawable.req_reactivation_icon; break;
        }
        holder.requestIconIv.setImageResource(icon);

        //Filter visibility by search keyword
        if (RequestList.keyword.length() < 1){
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        else if (holder.requestUserName.getText().toString().toLowerCase().contains(RequestList.keyword.toLowerCase().trim())){
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        else {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (request.getRequestCode()){
                    case RequestCode.ADOPTION_REQUEST:
                        Intent gotoRI = new Intent(activity, RequestInformation.class);
                        gotoRI.putExtra("userID", request.getUserID());
                        gotoRI.putExtra("petID", request.getRequestDetails());
                        gotoRI.putExtra("dateRequested", request.getDate());
                        activity.startActivity(gotoRI);
                        activity.finish();
                        break;
                    case RequestCode.APPOINTMENT:
                        //prepare message
                        String s = "Name: " + user.getFirstName() + " " + user.getLastName();
                        s += "\nPetID: " + request.getRequestDetails();
                        String[] dt = request.getDate().split(" ");
                        s += "\nDate: " + dt[0];
                        s += "\nDate: " + dt[1].replace("*",":") + dt[2];
                        AppointmentReqDialog appointmentReqDialog = new AppointmentReqDialog(activity, s, user.userID);
                        appointmentReqDialog.show();
                        break;
                    case RequestCode.ACCOUNT_ACTIVATION:
                        Intent intent = new Intent(activity, ManageAccount.class);
                        intent.putExtra("goto-user-info", user.getUserID());
                        activity.startActivity(intent);
                        activity.finish();
                        break;
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView requestUserPic;
        TextView requestUserName;
        TextView requestDate;
        ImageView requestIconIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            requestUserPic = itemView.findViewById(R.id.requestUserPicIv);
            requestUserName = itemView.findViewById(R.id.requestUserNameTv);
            requestDate = itemView.findViewById(R.id.requestDateTv);
            requestIconIv = itemView.findViewById(R.id.requestIconIv);
        }
    }
}

