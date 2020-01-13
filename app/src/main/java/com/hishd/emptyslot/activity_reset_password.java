package com.hishd.emptyslot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class activity_reset_password extends AppCompatActivity {

    EditText txtEmail;
    Button btnSubmit;
    LinearLayout layoutTop;
    RelativeLayout layoutMiddle;
    RelativeLayout layout_bottom;

    Animation animateTop,animateMiddle,animateBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        txtEmail = findViewById(R.id.txtEmail);
        btnSubmit = findViewById(R.id.btnSubmit);
        layoutTop = findViewById(R.id.layoutTop);
        layoutMiddle = findViewById(R.id.layoutMiddle);
        layout_bottom = findViewById(R.id.layout_bottom);

        animateTop = AnimationUtils.loadAnimation(this,R.anim.shrink_enter);
        animateMiddle = AnimationUtils.loadAnimation(this,R.anim.slide_down_enter);
        animateBottom = AnimationUtils.loadAnimation(this,R.anim.slide_up_enter);

        layoutTop.startAnimation(animateTop);
        layoutMiddle.startAnimation(animateMiddle);
        layout_bottom.startAnimation(animateBottom);
    }
}
