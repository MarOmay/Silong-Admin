package com.silong.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.silong.CustomView.PetInfoDialog;
import com.silong.Object.PetRecordsData;
import com.silong.admin.R;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.ViewHolder>{

    PetRecordsData petRecordsData[];
    Activity activity;

    public RecordsAdapter(PetRecordsData[] petRecordsData, Activity activity){
        this.petRecordsData = petRecordsData;
        this.activity = activity;
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
        holder.petPic.setImageBitmap(petRecordsDataList.getPetpic());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PetInfoDialog petInfoDialog = new PetInfoDialog(activity, petRecordsDataList.getPetID());
                petInfoDialog.show();
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
