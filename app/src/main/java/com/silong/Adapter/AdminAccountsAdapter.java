package com.silong.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.silong.Object.Admin;
import com.silong.admin.AdminRoles;
import com.silong.admin.ManageRoles;
import com.silong.admin.R;

public class AdminAccountsAdapter extends RecyclerView.Adapter<AdminAccountsAdapter.ViewHolder> {

    Admin[] adminAccountsData;
    Activity activity;
    Context context;

    public AdminAccountsAdapter(Admin[] adminAccountsData, Activity activity){
        this. adminAccountsData = adminAccountsData;
        this.activity = activity;
        this.context = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.admin_accounts_item_list, parent, false);
        AdminAccountsAdapter.ViewHolder viewHolder = new AdminAccountsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Admin adminAccountsDataList = adminAccountsData[position];
        holder.adminAccName.setText(adminAccountsDataList.getFirstName() + " " + adminAccountsDataList.getLastName());
        holder.adminAccEmail.setText(adminAccountsDataList.getAdminEmail());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, AdminRoles.class);
                intent.putExtra("ADMIN", adminAccountsDataList);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() { return adminAccountsData.length; }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView adminAccName;
        TextView adminAccEmail;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            adminAccName = itemView.findViewById(R.id.adminAccNameTv);
            adminAccEmail = itemView.findViewById(R.id.adminAccEmailTv);
        }
    }
}
