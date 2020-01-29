package com.hishd.emptyslot.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hishd.emptyslot.R;

import java.util.ArrayList;

public class ParkingFacilityAdapter extends RecyclerView.Adapter<ParkingFacilityAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Facility> facilities;

    public ParkingFacilityAdapter(Context context, ArrayList<Facility> facilities) {
        this.context = context;
        this.facilities = facilities;
    }

    public void setFacilities(ArrayList<Facility> facilities) {
        this.facilities = new ArrayList<>();
        this.facilities = facilities;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_facility_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(facilities.get(position));
    }

    @Override
    public int getItemCount() {
        return facilities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgFacility;
        TextView txtFacilityName;
        RelativeLayout layoutRoot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutRoot = itemView.findViewById(R.id.layoutRoot);
            imgFacility = itemView.findViewById(R.id.imgFacility);
            txtFacilityName = itemView.findViewById(R.id.txtFacilityName);
        }

        public void bind(final Facility facility) {
            layoutRoot.setBackground(ContextCompat.getDrawable(context, facility.background));
            txtFacilityName.setText(facility.facilityName);
            imgFacility.setImageDrawable(ContextCompat.getDrawable(context, facility.resID));
        }
    }
}
