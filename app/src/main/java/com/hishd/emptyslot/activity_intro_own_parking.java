package com.hishd.emptyslot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

import spencerstudios.com.bungeelib.Bungee;

public class activity_intro_own_parking extends AppCompatActivity {

    TextView view_top;
    TextView view_center;
    TextView view_bottom;
    LottieAnimationView animationView1;
    LottieAnimationView animationView2;
    Button btnGotIt;

    Animation fadeIn, enterLeft, enterRight, enterFromTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_own_parking);

        view_top = findViewById(R.id.view_top);
        view_center = findViewById(R.id.view_center);
        view_bottom = findViewById(R.id.view_bottom);
        animationView1 = findViewById(R.id.animationView1);
        animationView2 = findViewById(R.id.animationView2);
        btnGotIt = findViewById(R.id.btnGotIt);

        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_enter);
        enterLeft = AnimationUtils.loadAnimation(this, R.anim.slide_left_enter);
        enterRight = AnimationUtils.loadAnimation(this, R.anim.swipe_right_enter);
        enterFromTop = AnimationUtils.loadAnimation(this, R.anim.slide_down_enter);

        view_top.startAnimation(enterFromTop);
        view_center.startAnimation(enterFromTop);
        view_bottom.startAnimation(enterFromTop);

        animationView1.startAnimation(enterRight);
        animationView2.startAnimation(enterLeft);

        btnGotIt.startAnimation(fadeIn);

        btnGotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_intro_own_parking.this, activity_own_parkings.class));
                Bungee.zoom(activity_intro_own_parking.this);
                finish();
            }
        });
    }
}
