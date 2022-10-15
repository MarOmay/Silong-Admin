package com.silong.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AgreementAdapter extends RecyclerView.Adapter<AgreementAdapter.ViewHolder> {

    AgreementData agreementData[];
    Context context;

    public AgreementAdapter( AgreementData[] agreementData, AdoptionAgreement activity){
        this.agreementData = agreementData;
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
                Toast.makeText(context, agreementDataList.getAgreementTitle(), Toast.LENGTH_SHORT).show();
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
