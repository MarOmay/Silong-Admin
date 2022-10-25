package com.silong.Adapter;

import android.app.Activity;
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
import com.silong.Object.Adoption;
import com.silong.Object.Request;
import com.silong.Object.User;
import com.silong.Operation.Utility;
import com.silong.admin.AdminData;
import com.silong.admin.ManageAccount;
import com.silong.admin.R;
import com.silong.admin.RequestInformation;
import com.silong.admin.RequestList;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder>{

    private Activity activity;
    private Request[] requests;

    public RequestAdapter(Activity activity, Request[] requests){
        this.activity = activity;
        this.requests = requests;
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
        final Request request = requests[position];

        User user = AdminData.fetchAccountFromLocal(activity, request.getUserID());

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
                try {
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
                            s += "\nTime: " + dt[1].replace("*",":") + dt[2];
                            Adoption adoption = new Adoption();
                            adoption.setPetID(Integer.parseInt(request.getRequestDetails()));
                            adoption.setAppointmentDate(dt[0] + " " + dt[1].replace("*",":") + " "+ dt[2]);
                            adoption.setDateRequested(request.getDate());
                            AppointmentReqDialog appointmentReqDialog = new AppointmentReqDialog(activity, s, user.userID, adoption);
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
                catch (Exception e){
                    Toast.makeText(activity, "Operation can't be performed.", Toast.LENGTH_SHORT).show();
                    Utility.log("RequestAdapter.oBVH: " + e.getMessage());
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return requests.length;
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

