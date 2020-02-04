package com.hishd.emptyslot;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.hishd.emptyslot.Util.FirebaseES;

public class activity_public_parking extends AppCompatActivity {

    ImageView img_back;
    FirebaseES firebaseES;
    TextView view_what_is_temp_parking;
    CFAlertDialog.Builder popUpDialog;
    Animation fadeIn, enterLeft, enterRight, enterFromTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_parking);

        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_enter);
        enterLeft = AnimationUtils.loadAnimation(this, R.anim.slide_left_enter);
        enterRight = AnimationUtils.loadAnimation(this, R.anim.swipe_right_enter);
        enterFromTop = AnimationUtils.loadAnimation(this, R.anim.slide_down_enter);

        final TextView txtParkingName = findViewById(R.id.txtParkingName);
        final TextView txtParkingNameCaption = findViewById(R.id.txtParkingNameCaption);
        final TextView txtLandmarks = findViewById(R.id.txtLandmarks);
        final TextView txtSmartParkingArea = findViewById(R.id.txtSmartParkingArea);
        final Button btnNavigate = findViewById(R.id.btnNavigate);
        final LinearLayout layout_total_spaces = findViewById(R.id.layout_total_spaces);

        layout_total_spaces.startAnimation(enterFromTop);
        txtParkingNameCaption.startAnimation(enterLeft);
        txtLandmarks.startAnimation(enterRight);
        txtSmartParkingArea.startAnimation(enterLeft);
        btnNavigate.startAnimation(fadeIn);

        txtParkingName.startAnimation(enterFromTop);

        popUpDialog = new CFAlertDialog.Builder(this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setTitle("Temporary Parking")
                .setMessage("The term Temporary Parking Space indicates parking spaces which can be used to park the vehicles only for a short amount of periods.\nUsually the Temporary Parking Spaces cen be found near to main roads.\nAs they are close to the main roads it might be unsafe to park.")
                .setTextGravity(Gravity.CENTER_HORIZONTAL);

        img_back = findViewById(R.id.img_back);
        firebaseES = new FirebaseES(this);
        view_what_is_temp_parking = findViewById(R.id.view_what_is_temp_parking);

        firebaseES = new FirebaseES(this);
        firebaseES.loadPublicParkingInfo(this, getIntent().getStringExtra("REFERANCE"));

        Log.i("Referance", getIntent().getStringExtra("REFERANCE"));

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        view_what_is_temp_parking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpDialog.show();
            }
        });

    }
}
