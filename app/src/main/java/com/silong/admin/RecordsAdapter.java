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

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.ViewHolder>{

    PetRecordsData petRecordsData[];
    Context context;

    public RecordsAdapter(PetRecordsData[] petRecordsData, ManageRecords activity){
        this.petRecordsData = petRecordsData;
        this.context = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.records_item_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final PetRecordsData petRecordsDataList = petRecordsData[position];
        holder.genderType.setText(petRecordsDataList.getGenderType());
        holder.estAge.setText(petRecordsDataList.getEstAge());
        holder.estSize.setText(petRecordsDataList.getEstSize());
        holder.petColor.setText(petRecordsDataList.getPetColor());
        holder.petPic.setImageResource(petRecordsDataList.getPetPic());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, petRecordsDataList.getGenderType(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return petRecordsData.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView petPic;
        TextView genderType;
        TextView estAge;
        TextView petColor;
        TextView estSize;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            petPic = itemView.findViewById(R.id.petPicIv);
            genderType = itemView.findViewById(R.id.genderTypeTv);
            estAge = itemView.findViewById(R.id.estAgeTv);
            petColor = itemView.findViewById(R.id.petColorTv);
            estSize = itemView.findViewById(R.id.estSizeTv);
        }
    }
}
