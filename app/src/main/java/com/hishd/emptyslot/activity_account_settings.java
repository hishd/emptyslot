package com.hishd.emptyslot;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnim;
import com.hishd.emptyslot.Util.AppConfig;
import com.hishd.emptyslot.Util.FirebaseES;
import com.hishd.emptyslot.Util.Validator;

import spencerstudios.com.bungeelib.Bungee;

public class activity_account_settings extends AppCompatActivity {

    ImageView img_back;
    TextView txtName;
    TextView txtEmail;
    TextView txtPassword;
    TextView txtChangeName;
    TextView txtChangeEmail;
    TextView txtChangePassword;
    TextView txtAddNewVehicles;
    Button btnLogout;
    AppConfig appConfig;
    String UserID;

    FirebaseES firebaseES;

    View mView;
    EditText txt1;
    EditText txt2;
    Button btnConfirm;

    Vibrator vibrate;


    AlertDialog.Builder alertDialog;
    AlertDialog dialog;

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
        txtAddNewVehicles = findViewById(R.id.txtAddNewVehicles);

        appConfig = new AppConfig(this);
        firebaseES = new FirebaseES(this);

        vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        if (appConfig.getLogedUserID() != null) {
            UserID = appConfig.getLogedUserID();
            Log.i("LOGGED_USER_ID", UserID);
            firebaseES.setSettingsUserInfo(this, UserID);
        }

        txtChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNameChangeDialog();
            }
        });

        txtChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmailChangeDialog();
            }
        });

        txtChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordChangeDialog();
            }
        });

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

        txtAddNewVehicles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_account_settings.this, activity_add_vehicles.class));
            }
        });
    }

    private void showNameChangeDialog() {
        alertDialog = new AlertDialog.Builder(activity_account_settings.this);
        mView = getLayoutInflater().inflate(R.layout.custom_name_change_dialog, null);
        txt1 = mView.findViewById(R.id.txtName);
        txt1.setText(txtName.getText().toString());
        btnConfirm = mView.findViewById(R.id.btnConfirm);
        alertDialog.setView(mView);
        dialog = alertDialog.create();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txt1.getText().toString().isEmpty()) {
                    showAlertDialog("Please enter a name").show();
                    vibrate.vibrate(30);
                    return;
                }
                if (!Validator.validateName(txt1.getText().toString())) {
                    showAlertDialog("Please enter a valid name").show();
                    vibrate.vibrate(30);
                    return;
                }

                dialog.dismiss();
                firebaseES.updateUserName(activity_account_settings.this, appConfig.getLogedUserID(), txt1.getText().toString());
            }
        });

        dialog.show();
    }

    private void showEmailChangeDialog() {
        alertDialog = new AlertDialog.Builder(activity_account_settings.this);
        mView = getLayoutInflater().inflate(R.layout.custom_email_change_dialog, null);
        txt1 = mView.findViewById(R.id.txtEmail);
        txt1.setText(txtEmail.getText().toString());
        btnConfirm = mView.findViewById(R.id.btnConfirm);
        alertDialog.setView(mView);
        dialog = alertDialog.create();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txt1.getText().toString().isEmpty()) {
                    showAlertDialog("Please enter a Email Address").show();
                    vibrate.vibrate(30);
                    return;
                }
                if (!Validator.validateEmail(txt1.getText().toString())) {
                    showAlertDialog("Please enter a valid Email Address").show();
                    vibrate.vibrate(30);
                    return;
                }

                dialog.dismiss();
                firebaseES.updateUserEmail(activity_account_settings.this, appConfig.getLogedUserID(), txt1.getText().toString());
            }
        });
        dialog.show();
    }

    private void showPasswordChangeDialog() {
        alertDialog = new AlertDialog.Builder(activity_account_settings.this);
        mView = getLayoutInflater().inflate(R.layout.custom_password_change_dialog, null);
        txt1 = mView.findViewById(R.id.txtPassword);
        txt2 = mView.findViewById(R.id.txtConfirmPassword);
        btnConfirm = mView.findViewById(R.id.btnConfirm);
        alertDialog.setView(mView);
        dialog = alertDialog.create();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txt1.getText().toString().length() < 6) {
                    showAlertDialog("Please enter a valid Password with minimum 6 characters").show();
                    vibrate.vibrate(30);
                    return;
                }
                if (!txt1.getText().toString().equals(txt2.getText().toString())) {
                    showAlertDialog("Passwords doesn't match").show();
                    vibrate.vibrate(30);
                    return;
                }

                dialog.dismiss();
                firebaseES.updateUserPassword(activity_account_settings.this, appConfig.getLogedUserID(), txt1.getText().toString());
            }
        });
        dialog.show();
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
