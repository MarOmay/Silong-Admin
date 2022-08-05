package com.silong.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {

    UserAccountData[] userAccountData;
    Context context;

    public AccountAdapter(UserAccountData[] userAccountData, ManageAccount activity){
        this.userAccountData = userAccountData;
        this.context = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.accounts_item_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final UserAccountData userAccountDataList = userAccountData[position];

        if (userAccountDataList.getUserAccName().equals("null null")){
            holder.userAccName.setText("Loading profile...");
        }
        else {
            holder.userAccName.setText(userAccountDataList.getUserAccName());
        }

        holder.userAccEmail.setText(userAccountDataList.getUserAccEmail());

        if(userAccountDataList.getUserAvatar() == null){
            holder.userAccPic.setImageResource(R.drawable.avatar_placeholder);
        }
        else {
            holder.userAccPic.setImageBitmap(userAccountDataList.getUserAvatar());
        }

        //Filter visibility by search keyword
        if (ManageAccount.keyword.length() < 1){
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        else if (userAccountDataList.getUserAccName().toLowerCase().contains(ManageAccount.keyword.toLowerCase().trim())){
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
                Toast.makeText(context, userAccountDataList.getUserAccName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userAccountData.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView userAccPic;
        TextView userAccName;
        TextView userAccEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userAccPic = itemView.findViewById(R.id.userAccPicIv);
            userAccName = itemView.findViewById(R.id.userAccNameTv);
            userAccEmail = itemView.findViewById(R.id.userAccEmailTv);
        }
    }
}
