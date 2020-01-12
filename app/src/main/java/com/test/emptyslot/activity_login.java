package com.test.emptyslot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import spencerstudios.com.bungeelib.Bungee;

public class activity_login extends AppCompatActivity {

    ImageView imgViewBgLogin;
    ImageView imgLogo;
    LinearLayout layoutMiddle;
    LinearLayout layoutTop;
    LinearLayout viewCenterBottom;
    Animation bgAnim,centerLayoutAnim,fromBottom;
    Display display;
    RelativeLayout viewCenter;
    RelativeLayout viewBottom;

    EditText txtPhoneNo;
    EditText txtPassword;
    TextView txtForgotPassword;
    Button btnSignIn;
    TextView txtSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        centerLayoutAnim = AnimationUtils.loadAnimation(this,R.anim.zoom_enter);
        fromBottom = AnimationUtils.loadAnimation(this,R.anim.slide_up_enter);

        txtPhoneNo = findViewById(R.id.txtPhoneNo);
        txtPassword = findViewById(R.id.txtPassword);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        txtSignUp = findViewById(R.id.txtSignUp);


        imgViewBgLogin = findViewById(R.id.imgViewBgLogin);
        imgLogo = findViewById(R.id.imgLogo);
        display = getWindowManager().getDefaultDisplay();
        layoutMiddle = findViewById(R.id.layoutMiddle);
        layoutTop = findViewById(R.id.layoutTop);
        viewCenter = findViewById(R.id.viewCenter);
        viewBottom = findViewById(R.id.viewBottom);
        viewCenterBottom = findViewById(R.id.viewCenterBottom);

        bgAnim = AnimationUtils.loadAnimation(this,R.anim.bg_welcome_anim);

        imgViewBgLogin.animate().translationY(-display.getHeight()).setDuration(800).setStartDelay(1500);
        imgLogo.animate().alpha(0).setDuration(800).setStartDelay(1600);
        layoutMiddle.animate().translationY(140).alpha(0).setDuration(800).setStartDelay(1600);
        layoutTop.startAnimation(bgAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewCenter.startAnimation(centerLayoutAnim);
                btnSignIn.startAnimation(fromBottom);
                viewCenterBottom.startAnimation(fromBottom);
                viewCenter.setVisibility(View.VISIBLE);
                viewBottom.setVisibility(View.VISIBLE);
            }
        },1800);

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity_login.this,activity_reset_password.class));
                Bungee.zoom(activity_login.this);
            }
        });

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_login.this,activity_signup.class));
                Bungee.zoom(activity_login.this);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_login.this,activity_main_map_view.class));
            }
        });

    }
}
