package com.hishd.emptyslot;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnim;
import com.hishd.emptyslot.Util.FirebaseES;
import com.hishd.emptyslot.Util.Validator;

import spencerstudios.com.bungeelib.Bungee;

public class activity_login extends AppCompatActivity {

    ImageView imgViewBgLogin;
    ImageView imgLogo;
    LinearLayout layoutMiddle;
    LinearLayout layoutTop;
    LinearLayout viewCenterBottom;
    Animation bgAnim, centerLayoutAnim, fromBottom;
    Display display;
    RelativeLayout viewCenter;
    RelativeLayout viewBottom;

    EditText txtPhoneNo;
    EditText txtPassword;
    TextView txtForgotPassword;
    Button btnSignIn;
    TextView txtSignUp;

    Vibrator vibrate;
    Animation shakeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        centerLayoutAnim = AnimationUtils.loadAnimation(this, R.anim.zoom_enter);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.slide_up_enter);

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

        bgAnim = AnimationUtils.loadAnimation(this, R.anim.bg_welcome_anim);

        imgViewBgLogin.animate().translationY(-display.getHeight()).setDuration(800).setStartDelay(1500);
        imgLogo.animate().alpha(0).setDuration(800).setStartDelay(1600);
        layoutMiddle.animate().translationY(140).alpha(0).setDuration(800).setStartDelay(1600);
        layoutTop.startAnimation(bgAnim);

        vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewCenter.startAnimation(centerLayoutAnim);
                btnSignIn.startAnimation(fromBottom);
                viewCenterBottom.startAnimation(fromBottom);
                viewCenter.setVisibility(View.VISIBLE);
                viewBottom.setVisibility(View.VISIBLE);
            }
        }, 1800);

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity_login.this, activity_reset_password.class));
                Bungee.zoom(activity_login.this);
            }
        });

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_login.this, activity_signup.class));
                Bungee.zoom(activity_login.this);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Validator.validatePhone(txtPhoneNo.getText().toString())) {
                    showAlertDialog("Please enter a valid Mobile No").show();
                    initEditTextErrorAnimation(txtPhoneNo);
                    return;
                }
                if (txtPassword.getText().toString().length() < 6) {
                    showAlertDialog("Please enter a valid Password").show();
                    initEditTextErrorAnimation(txtPassword);
                    return;
                }

                FirebaseES firebaseES = new FirebaseES(activity_login.this);
                firebaseES.userLogin(activity_login.this, txtPhoneNo.getText().toString(), txtPassword.getText().toString());

            }
        });

    }

    private void initEditTextErrorAnimation(EditText editText) {
        shakeEditText = AnimationUtils.loadAnimation(this, R.anim.anim_shake_edit_text);
        vibrate.vibrate(20);
        editText.startAnimation(shakeEditText);
    }

    private Flashbar showAlertDialog(String message) {
        return new Flashbar.Builder(this)
                .gravity(Flashbar.Gravity.BOTTOM)
                .duration(1000)
                .message(message)
                .messageColor(ContextCompat.getColor(this, R.color.white))
                .backgroundColor(ContextCompat.getColor(this, R.color.errorMessageBackgroundColor))
                .showIcon()
                .iconColorFilterRes(R.color.errorMessageIconColor)
                .icon(R.drawable.ic_cross)
                .enterAnimation(FlashAnim.with(this)
                        .animateBar()
                        .duration(200)
                        .slideFromLeft()
                        .overshoot())
                .exitAnimation(FlashAnim.with(this)
                        .animateBar()
                        .duration(600)
                        .slideFromLeft()
                        .accelerate())
                .build();
    }
}
