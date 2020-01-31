package com.hishd.emptyslot;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.hishd.emptyslot.Util.AppConfig;

import spencerstudios.com.bungeelib.Bungee;

public class activity_launch extends AppCompatActivity {

    LottieAnimationView animationViewLogo;
    AppConfig appConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        appConfig = new AppConfig(this);

        animationViewLogo = findViewById(R.id.animationViewLogo);

        animationViewLogo.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (appConfig.isUserLoggedIn()) {
                            startActivity(new Intent(activity_launch.this, activity_main_map_view.class));
                            Bungee.zoom(activity_launch.this);
                            finish();
                            return;
                        }

                        if (appConfig.isAppIntroFinished()) {
                            startActivity(new Intent(activity_launch.this, activity_login.class));
                            Bungee.zoom(activity_launch.this);
                            finish();
                            return;
                        }

                        startActivity(new Intent(activity_launch.this, activity_app_intro.class));
                        Bungee.zoom(activity_launch.this);
                        finish();

                    }
                }, 1000);

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }
}
