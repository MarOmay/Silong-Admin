package com.silong.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.silong.CustomView.ImageDialog;
import com.silong.CustomView.PetInfoDialog;
import com.silong.EnumClass.Gender;
import com.silong.EnumClass.PetAge;
import com.silong.EnumClass.PetColor;
import com.silong.EnumClass.PetSize;
import com.silong.EnumClass.PetType;
import com.silong.Object.Pet;
import com.silong.Operation.Utility;
import com.silong.admin.R;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.ViewHolder>{

    private Activity activity;
    private Pet[] pets;

    public RecordsAdapter(Activity activity, Pet[] pets){
        this.activity = activity;
        this.pets = pets;
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
        final Pet pet = pets[position];

        //translate gender and type
        String genderType = "";
        switch (pet.getGender()){
            case Gender.MALE: genderType = "Male"; break;
            case Gender.FEMALE: genderType = "Female"; break;
        }
        switch (pet.getType()){
            case PetType.DOG: genderType += " Dog"; break;
            case PetType.CAT: genderType += " Cat"; break;
        }

        //translate age
        String age = "";
        switch (pet.getAge()){
            case PetAge.PUPPY: age = (pet.getType() == PetType.DOG ? "Puppy" : "Kitten"); break;
            case PetAge.YOUNG: age = "Young"; break;
            case PetAge.OLD: age = "Adult"; break;
        }

        //translate color
        String color = "";
        for (char c : pet.getColor().toCharArray()){
            switch (Integer.parseInt(c+"")){
                case PetColor.BLACK: color += "Black "; break;
                case PetColor.BROWN: color += "Brown "; break;
                case PetColor.CREAM: color += "Cream "; break;
                case PetColor.WHITE: color += "White "; break;
                case PetColor.ORANGE: color += "Orange "; break;
                case PetColor.GRAY: color += "Gray "; break;
            }
        }
        color.trim();
        color.replace(" ", " / ");

        //translate size
        String size = "";
        switch (pet.getSize()){
            case PetSize.SMALL: size = "Small"; break;
            case PetSize.MEDIUM: size = "Medium"; break;
            case PetSize.LARGE: size = "Large"; break;
        }

        holder.genderType.setText(genderType);
        holder.estAge.setText(age);
        holder.estSize.setText(size);
        holder.petColor.setText(color);
        holder.petPic.setImageBitmap(pet.getPhoto());
        holder.petID.setText("ID: " + pet.getPetID());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PetInfoDialog petInfoDialog = new PetInfoDialog(activity, pet.getPetID());
                    petInfoDialog.show();
                }
                catch (Exception e){
                    Toast.makeText(activity, "Operation can't be performed.", Toast.LENGTH_SHORT).show();
                    Utility.log("RecordsAdapter.obVH.oC: " + e.getMessage());
                }
            }
        });

        holder.petPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageDialog imageDialog = new ImageDialog(activity, holder.petPic.getDrawable());
                imageDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return pets.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView petPic;
        TextView genderType;
        TextView estAge;
        TextView petColor;
        TextView estSize;
        TextView petID;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            petPic = itemView.findViewById(R.id.petPicIv);
            genderType = itemView.findViewById(R.id.genderTypeTv);
            estAge = itemView.findViewById(R.id.estAgeTv);
            petColor = itemView.findViewById(R.id.petColorTv);
            estSize = itemView.findViewById(R.id.estSizeTv);
            petID = itemView.findViewById(R.id.petIDTv);
        }
    }
}
