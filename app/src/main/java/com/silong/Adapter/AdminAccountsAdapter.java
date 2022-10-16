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

import com.silong.Object.AdminAccountsData;
import com.silong.admin.ManageRoles;
import com.silong.admin.R;

import java.util.ConcurrentModificationException;

public class AdminAccountsAdapter extends RecyclerView.Adapter<AdminAccountsAdapter.ViewHolder> {

    AdminAccountsData[] adminAccountsData;
    Context context;

    public AdminAccountsAdapter(AdminAccountsData[] adminAccountsData, ManageRoles activity){
        this. adminAccountsData = adminAccountsData;
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
        final AdminAccountsData adminAccountsDataList = adminAccountsData[position];
        holder.adminAvatar.setImageResource(adminAccountsDataList.getAdminAvatar());
        holder.adminAccName.setText(adminAccountsDataList.getAdminAccName());
        holder.adminAccEmail.setText(adminAccountsDataList.getAdminAccEmail());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, adminAccountsDataList.getAdminAccName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() { return adminAccountsData.length; }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView adminAvatar;
        TextView adminAccName;
        TextView adminAccEmail;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            adminAvatar = itemView.findViewById(R.id.adminAccPicIv);
            adminAccName = itemView.findViewById(R.id.adminAccNameTv);
            adminAccEmail = itemView.findViewById(R.id.adminAccEmailTv);
        }
    }
}
