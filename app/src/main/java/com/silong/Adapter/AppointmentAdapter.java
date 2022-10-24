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
import com.silong.Operation.Utility;
import com.silong.admin.AdminData;
import com.silong.admin.R;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private Activity activity;

    public AppointmentAdapter(Activity activity){
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
        final AppointmentRecords appointmentRecord = AdminData.appointments.get(position);
        holder.name.setText(appointmentRecord.getName());
        holder.dateTime.setText(appointmentRecord.getDateTime());
        holder.petId.setText(appointmentRecord.getPetId());
        holder.userPic.setImageBitmap(appointmentRecord.getUserPic());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!Utility.internetConnection(activity)){
                        Toast.makeText(activity, "No internet connection", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Adoption adoption = new Adoption();
                    adoption.setDateRequested(appointmentRecord.getDateRequested());
                    adoption.setAppointmentDate(appointmentRecord.getDateTime());
                    adoption.setPetID(Integer.parseInt(appointmentRecord.getPetId()));

                    AppointmentTagger appointmentTagger = new AppointmentTagger(activity, appointmentRecord.getUserID(), appointmentRecord.getName(), adoption);
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
    public int getItemCount() { return AdminData.appointments.size(); }

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
