package com.silong.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.silong.Object.Admin;
import com.silong.Object.Adoption;
import com.silong.admin.R;
import com.silong.admin.UserInformation;

import org.w3c.dom.Text;

public class AdoptionHistoryAdapter extends RecyclerView.Adapter<AdoptionHistoryAdapter.ViewHolder> {

    Adoption[] adoption;
    Context context;

    public AdoptionHistoryAdapter(Adoption[] adoption, UserInformation activity){
        this.adoption = adoption;
        this.context = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.adoption_history_item_list, parent, false);
        AdoptionHistoryAdapter.ViewHolder viewHolder = new AdoptionHistoryAdapter.ViewHolder(view);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Adoption adoptionDataList = adoption[position];
        holder.adoptionHistoryDate.setText(adoptionDataList.getDateRequested());
        holder.petIdHistory.setText("Pet ID# " + adoptionDataList.getPetID());

        String status = "";
        if (adoptionDataList.getStatus() < 1)
            status = "Cancelled";
        else if (adoptionDataList.getStatus() > 0 && adoptionDataList.getStatus() < 6)
            status = "Processing";
        else if (adoptionDataList.getStatus() == 6)
            status = "Successful";
        else
            status = "Declined";

        holder.statusHistory.setText(status);
    }

    @Override
    public int getItemCount() { return adoption.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView adoptionHistoryDate;
        TextView petIdHistory;
        TextView statusHistory;

        public ViewHolder (@NonNull View itemView){
            super(itemView);
            adoptionHistoryDate = itemView.findViewById(R.id.adoptionHistoryDate);
            petIdHistory = itemView.findViewById(R.id.petIdHistory);
            statusHistory = itemView.findViewById(R.id.statusHistory);
        }
    }
}
