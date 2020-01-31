package com.hishd.emptyslot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SignUpVehicleItemAdapter extends RecyclerView.Adapter<SignUpVehicleItemAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Vehicle> vehicles;

    public SignUpVehicleItemAdapter(Context context, ArrayList<Vehicle> vehicleArrayList) {
        this.context = context;
        this.vehicles = vehicleArrayList;
    }

    public void setVehicles(ArrayList<Vehicle> vehicleArrayList) {
        this.vehicles = new ArrayList<>();
        this.vehicles = vehicleArrayList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_vehicle_item, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(vehicles.get(position));
    }

    @Override
    public int getItemCount() {
        return vehicles.size();
    }

    public ArrayList<Vehicle> getSelected() {
        ArrayList<Vehicle> selectedVehicles = new ArrayList<>();
        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get(i).isChecked()) {
                selectedVehicles.add(vehicles.get(i));
            }
        }
        return selectedVehicles;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgVehicle;
        ImageView imgSelected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgVehicle = itemView.findViewById(R.id.imgVehicle);
            imgSelected = itemView.findViewById(R.id.imgSelected);
        }

        void bind(final Vehicle veh) {
            imgSelected.setVisibility(veh.isChecked() ? View.VISIBLE : View.GONE);
            imgVehicle.setImageDrawable(ContextCompat.getDrawable(context, veh.getResID()));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    veh.setChecked(!veh.isChecked());
                    imgSelected.setVisibility(veh.isChecked() ? View.VISIBLE : View.GONE);
                }
            });
        }
    }
}
