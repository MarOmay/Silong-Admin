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

import com.silong.Object.AgreementData;
import com.silong.admin.AdoptionAgreement;
import com.silong.admin.EditClause;
import com.silong.admin.R;

import java.io.Serializable;

public class AgreementAdapter extends RecyclerView.Adapter<AgreementAdapter.ViewHolder> {

    AgreementData agreementData[];
    Activity activity;
    Context context;

    public AgreementAdapter( AgreementData[] agreementData, AdoptionAgreement activity){
        this.agreementData = agreementData;
        this.activity = activity;
        this.context = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.agreement_item_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final AgreementData agreementDataList = agreementData[position];
        holder.agreementTitle.setText(agreementDataList.getAgreementTitle());
        holder.agreementBody.setText(agreementDataList.getAgreementBody());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, EditClause.class);
                intent.putExtra("agreementData", (Serializable) agreementDataList);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() { return agreementData.length ; }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView agreementTitle;
        TextView agreementBody;

        public ViewHolder (@NonNull View itemView){
            super(itemView);

            agreementTitle = itemView.findViewById(R.id.agreementTitle);
            agreementBody = itemView.findViewById(R.id.agreementBody);
        }
    }
}
