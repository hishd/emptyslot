package com.hishd.emptyslot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hishd.emptyslot.Util.AppConfig;

import spencerstudios.com.bungeelib.Bungee;

public class activity_account_settings extends AppCompatActivity {

    ImageView img_back;
    TextView txtName;
    TextView txtEmail;
    TextView txtPassword;
    TextView txtChangeName;
    TextView txtChangeEmail;
    TextView txtChangePassword;
    Button btnLogout;
    AppConfig appConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        img_back = findViewById(R.id.img_back);
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtChangeName = findViewById(R.id.txtChangeName);
        txtChangeEmail = findViewById(R.id.txtChangeEmail);
        txtChangePassword = findViewById(R.id.txtChangePassword);
        btnLogout = findViewById(R.id.btnLogout);

        appConfig = new AppConfig(this);


        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Bungee.fade(activity_account_settings.this);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appConfig.setUserLoggedOut();
                finish();
            }
        });
    }
}
