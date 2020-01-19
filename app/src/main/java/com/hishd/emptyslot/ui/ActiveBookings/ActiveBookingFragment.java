package com.hishd.emptyslot.ui.ActiveBookings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.hishd.emptyslot.R;

public class ActiveBookingFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_inactive_bookings, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);

        return root;
    }
}