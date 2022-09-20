package com.silong.Adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.silong.CustomView.AppointmentTagger;
import com.silong.Object.Adoption;
import com.silong.Object.AppointmentRecords;
import com.silong.admin.R;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    AppointmentRecords appointmentRecords[];
    Activity activity;

    public AppointmentAdapter(AppointmentRecords[] appointmentRecords, Activity activity){
        this.appointmentRecords = appointmentRecords;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.appointments_item_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final AppointmentRecords appointmentRecordsList = appointmentRecords[position];
        holder.name.setText(appointmentRecordsList.getName());
        holder.dateTime.setText(appointmentRecordsList.getDateTime());
        holder.petId.setText(appointmentRecordsList.getPetId());
        holder.userPic.setImageBitmap(appointmentRecordsList.getUserPic());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Adoption adoption = new Adoption();
                    adoption.setAppointmentDate(appointmentRecordsList.getDateTime());
                    adoption.setPetID(Integer.parseInt(appointmentRecordsList.getPetId()));

                    AppointmentTagger appointmentTagger = new AppointmentTagger(activity, appointmentRecordsList.getUserID(), appointmentRecordsList.getName(), adoption);
                    appointmentTagger.show();
                }
                catch (Exception e){
                    Toast.makeText(activity, "Action can't be performed.", Toast.LENGTH_SHORT).show();
                    Log.d("AA-oBVH", e.getMessage());
                }
            }
        });
    }

    @Override
    public int getItemCount() { return appointmentRecords.length; }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView dateTime;
        TextView petId;
        ImageView userPic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.appointmentUserNameTv);
            dateTime = itemView.findViewById(R.id.appointmentDateTimeTv);
            petId = itemView.findViewById(R.id.appointmentPetIdTv);
            userPic = itemView.findViewById(R.id.appointmentUserPic);
        }
    }
}
