package com.silong.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.silong.Object.User;
import com.silong.admin.ManageAccount;
import com.silong.admin.R;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {

    private Context context;
    private User[] users;

    public AccountAdapter(Activity activity, User[] users){
        this.context = activity;
        this.users = users;
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
        final User user = users[position];

        String name = user.getFirstName() + " " + user.getLastName();

        if (name.equals("null null")){
            holder.userAccName.setText("Loading profile...");
        }
        else {
            holder.userAccName.setText(name);
        }

        holder.userAccEmail.setText(user.getEmail());

        if(user.getPhoto() == null){
            holder.userAccPic.setImageResource(R.drawable.avatar_placeholder);
        }
        else {
            holder.userAccPic.setImageBitmap(user.getPhoto());
        }

        //Filter visibility by search keyword
        if (ManageAccount.keyword.length() < 1){
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        else if (name.toLowerCase().contains(ManageAccount.keyword.toLowerCase().trim())){
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
                Intent intent = new Intent("show-selected-user");
                intent.putExtra("uid", user.getUserID());
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.length;
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
