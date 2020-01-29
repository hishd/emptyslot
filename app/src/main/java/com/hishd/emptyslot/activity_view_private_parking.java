package com.hishd.emptyslot;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hishd.emptyslot.Util.FirebaseES;

public class activity_view_private_parking extends AppCompatActivity {

    ImageView img_back;
    FirebaseES firebaseES;
    RecyclerView rclr_facilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_private_parking);

        img_back = findViewById(R.id.img_back);
        rclr_facilities = findViewById(R.id.rclr_facilities);

        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rclr_facilities.setLayoutManager(horizontalLayoutManagaer);
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(this, R.anim.rclr_layout_anim);
        rclr_facilities.setLayoutAnimation(animationController);

        firebaseES = new FirebaseES(this);
        firebaseES.loadPrivateParkingInfo(this, getIntent().getStringExtra("REFERANCE"), rclr_facilities);

        Log.i("Referance", getIntent().getStringExtra("REFERANCE"));


        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
