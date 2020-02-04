package com.hishd.emptyslot;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hishd.emptyslot.Util.FirebaseES;

public class activity_view_private_parking extends AppCompatActivity {

    ImageView img_back;
    FirebaseES firebaseES;
    RecyclerView rclr_facilities;
    Animation fadeIn, enterLeft, enterRight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_private_parking);

        img_back = findViewById(R.id.img_back);
        rclr_facilities = findViewById(R.id.rclr_facilities);

        final Button btnNavigate = findViewById(R.id.btnNavigate);
        final Button btnBookNow = findViewById(R.id.btnBookNow);
        final LinearLayout layout_total_spaces = findViewById(R.id.layout_total_spaces);
        final LinearLayout layout_available_spaces = findViewById(R.id.layout_available_spaces);

        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_enter);
        enterLeft = AnimationUtils.loadAnimation(this, R.anim.slide_left_enter);
        enterRight = AnimationUtils.loadAnimation(this, R.anim.swipe_right_enter);


        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rclr_facilities.setLayoutManager(horizontalLayoutManagaer);
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(this, R.anim.rclr_layout_anim);
        rclr_facilities.setLayoutAnimation(animationController);

        firebaseES = new FirebaseES(this);
        firebaseES.loadPrivateParkingInfo(this, getIntent().getStringExtra("REFERANCE"), rclr_facilities);

        Log.i("Referance", getIntent().getStringExtra("REFERANCE"));

        layout_available_spaces.startAnimation(enterLeft);
        layout_total_spaces.startAnimation(enterRight);
        btnNavigate.startAnimation(fadeIn);
        btnBookNow.startAnimation(fadeIn);


        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
