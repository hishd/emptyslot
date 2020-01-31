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

public class AddVehicleVehicleAdapter extends RecyclerView.Adapter<AddVehicleVehicleAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Vehicle> vehicles;
    // if checkedPosition = -1, there is no default selection
    // if checkedPosition = 0, 1st item is selected by default
    private int checkedPosition = 0;

    public AddVehicleVehicleAdapter(Context context, ArrayList<Vehicle> vehicles) {
        this.context = context;
        this.vehicles = vehicles;
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

    public Vehicle getSelected() {
        if (checkedPosition != -1) {
            return vehicles.get(checkedPosition);
        }
        return null;
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

            imgVehicle.setImageDrawable(ContextCompat.getDrawable(context, veh.getResID()));

            if (checkedPosition == -1)
                imgSelected.setVisibility(View.GONE);
            else {
                if (checkedPosition == getAdapterPosition())
                    imgSelected.setVisibility(View.VISIBLE);
                else
                    imgSelected.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imgSelected.setVisibility(View.VISIBLE);
                    if (checkedPosition != getAdapterPosition()) {
                        notifyItemChanged(checkedPosition);
                        checkedPosition = getAdapterPosition();
                    }
                }
            });
        }
    }
}
