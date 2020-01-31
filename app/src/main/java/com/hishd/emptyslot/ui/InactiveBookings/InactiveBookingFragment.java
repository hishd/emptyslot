package com.hishd.emptyslot.ui.InactiveBookings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.hishd.emptyslot.R;
import com.hishd.emptyslot.Util.AppConfig;
import com.hishd.emptyslot.Util.Booking;

public class InactiveBookingFragment extends Fragment {

    RecyclerView rclr_inactive_Bookings;
    AppConfig appConfig;
    FirebaseRecyclerAdapter<Booking, ViewHolder> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_inactive_bookings, container, false);
        rclr_inactive_Bookings = root.findViewById(R.id.rclr_inactive_Bookings);
        rclr_inactive_Bookings.setLayoutManager(new LinearLayoutManager(root.getContext()));
        appConfig = new AppConfig(root.getContext());
        fetch();
        return root;
    }

    private void fetch() {
        Query query = FirebaseDatabase.getInstance().getReference().child("users").child(appConfig.getLogedUserID()).child("bookings").child("expired");
        FirebaseRecyclerOptions<Booking> options = new FirebaseRecyclerOptions.Builder<Booking>().setQuery(query, new SnapshotParser<Booking>() {
            @NonNull
            @Override
            public Booking parseSnapshot(@NonNull DataSnapshot snapshot) {
                return new Booking("owner",
                        snapshot.child("vehicle_no").getValue().toString(),
                        snapshot.child("placed_timestamp").getValue().toString(),
                        snapshot.child("lot_id").getValue().toString(),
                        snapshot.child("lot_name").getValue().toString());
            }
        }).build();

        adapter = new FirebaseRecyclerAdapter<Booking, ViewHolder>(options) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_expired_booking_item, parent, false);
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Booking booking) {
                viewHolder.setTxtVehicleNo(booking.vehicle_no);
                viewHolder.setTxtLotName(booking.lot_name);
                viewHolder.setTxtBookedTime(booking.placed_timestamp);
            }
        };

        rclr_inactive_Bookings.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtVehicleNo;
        TextView txtLotName;
        TextView txtBookedTime;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtVehicleNo = itemView.findViewById(R.id.txtVehicleNo);
            txtLotName = itemView.findViewById(R.id.txtLotName);
            txtBookedTime = itemView.findViewById(R.id.txtBookedTime);
        }

        public void setTxtVehicleNo(String txtVehicleNo) {
            this.txtVehicleNo.setText(txtVehicleNo);
        }

        public void setTxtLotName(String txtLotName) {
            this.txtLotName.setText(txtLotName);
        }

        public void setTxtBookedTime(String txtBookedTime) {
            this.txtBookedTime.setText(txtBookedTime);
        }
    }
}