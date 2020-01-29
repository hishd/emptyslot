package com.hishd.emptyslot;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.hishd.emptyslot.Util.FirebaseES;

public class activity_public_parking extends AppCompatActivity {

    ImageView img_back;
    FirebaseES firebaseES;
    TextView view_what_is_temp_parking;
    CFAlertDialog.Builder popUpDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_parking);

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
