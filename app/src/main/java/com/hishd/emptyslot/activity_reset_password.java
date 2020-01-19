package com.hishd.emptyslot;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnim;
import com.hishd.emptyslot.Util.Validator;

public class activity_reset_password extends AppCompatActivity {

    EditText txtEmail;
    Button btnSubmit;
    LinearLayout layoutTop;
    RelativeLayout layoutMiddle;
    RelativeLayout layout_bottom;

    Animation animateTop, animateMiddle, animateBottom;

    Animation shakeEditText;
    Vibrator vibrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        txtEmail = findViewById(R.id.txtEmail);
        btnSubmit = findViewById(R.id.btnSubmit);
        layoutTop = findViewById(R.id.layoutTop);
        layoutMiddle = findViewById(R.id.layoutMiddle);
        layout_bottom = findViewById(R.id.layout_bottom);

        animateTop = AnimationUtils.loadAnimation(this, R.anim.shrink_enter);
        animateMiddle = AnimationUtils.loadAnimation(this, R.anim.slide_down_enter);
        animateBottom = AnimationUtils.loadAnimation(this, R.anim.slide_up_enter);

        layoutTop.startAnimation(animateTop);
        layoutMiddle.startAnimation(animateMiddle);
        layout_bottom.startAnimation(animateBottom);

        vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shakeEditText = AnimationUtils.loadAnimation(activity_reset_password.this, R.anim.anim_shake_edit_text);

                if (!Validator.validateEmail(txtEmail.getText().toString())) {
                    showAlertDialog("Enter a valid Email Address");
                    vibrate.vibrate(30);
                    return;
                }

            }
        });
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
