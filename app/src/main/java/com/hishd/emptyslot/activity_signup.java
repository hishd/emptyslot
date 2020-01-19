package com.hishd.emptyslot;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnim;
import com.hishd.emptyslot.Util.FirebaseES;
import com.hishd.emptyslot.Util.Validator;

import java.util.ArrayList;

import spencerstudios.com.bungeelib.Bungee;

public class activity_signup extends AppCompatActivity {

    EditText txtName;
    EditText txtEmail;
    EditText txtPhoneNo;
    EditText txtPassword;
    EditText txtConfirmPassword;
    Button btnSignUp;
    TextView viewAlreadyAMember;
    RecyclerView rclr_vehicles;

    ImageView view_top;
    TextView view_caption_head;
    TextView view_caption_1;


    Vibrator vibrate;
    Animation shakeEditText;

    Animation fadeIn, enterLeft, enterRight, enterFromTop;

    private ArrayList<Vehicle> vehicles;
    private SignUpVehicleItemAdapter signUpVehicleItemAdapter;

    private int[] vehicleDrawables = {R.drawable.car, R.drawable.bus, R.drawable.suv, R.drawable.scooter};
    private String[] vehicleTypes = {"Car", "Van", "Jeep", "Bike"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhoneNo = findViewById(R.id.txtPhoneNo);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        viewAlreadyAMember = findViewById(R.id.viewAlreadyAMember);

        view_top = findViewById(R.id.view_top);
        view_caption_head = findViewById(R.id.view_caption_head);
        view_caption_1 = findViewById(R.id.view_caption_1);

        rclr_vehicles = findViewById(R.id.rclr_vehicles);
        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rclr_vehicles.setLayoutManager(horizontalLayoutManagaer);

        vehicles = new ArrayList<>();
        signUpVehicleItemAdapter = new SignUpVehicleItemAdapter(this, vehicles);
        rclr_vehicles.setAdapter(signUpVehicleItemAdapter);

        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_enter);
        enterLeft = AnimationUtils.loadAnimation(this, R.anim.slide_left_enter);
        enterRight = AnimationUtils.loadAnimation(this, R.anim.swipe_right_enter);
        enterFromTop = AnimationUtils.loadAnimation(this, R.anim.slide_down_enter);

        txtName.startAnimation(enterLeft);
        txtEmail.startAnimation(enterRight);
        txtPhoneNo.startAnimation(enterLeft);
        txtPassword.startAnimation(enterRight);
        txtConfirmPassword.startAnimation(enterLeft);
        btnSignUp.startAnimation(fadeIn);
        viewAlreadyAMember.startAnimation(fadeIn);

        view_top.startAnimation(enterFromTop);
        view_caption_head.startAnimation(fadeIn);
        view_caption_1.startAnimation(fadeIn);

        vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        viewAlreadyAMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Bungee.fade(activity_signup.this);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        createVehicleList();

        rclr_vehicles.startAnimation(fadeIn);

    }

    void registerUser() {

        if (txtName.getText().toString().isEmpty()) {
            showAlertDialog("Please enter a name").show();
            initEditTextErrorAnimation(txtName);
            return;
        }
        if (!Validator.validateName(txtName.getText().toString())) {
            showAlertDialog("Please enter a valid name").show();
            initEditTextErrorAnimation(txtName);
            return;
        }
        if (txtEmail.getText().toString().isEmpty()) {
            showAlertDialog("Please enter a Email Address").show();
            initEditTextErrorAnimation(txtEmail);
            return;
        }
        if (!Validator.validateEmail(txtEmail.getText().toString())) {
            showAlertDialog("Please enter a valid Email Address").show();
            initEditTextErrorAnimation(txtEmail);
            return;
        }
        if (!Validator.validatePhone(txtPhoneNo.getText().toString())) {
            showAlertDialog("Please enter a valid Mobile No").show();
            initEditTextErrorAnimation(txtPhoneNo);
            return;
        }
        if (txtPassword.getText().toString().length() < 6) {
            showAlertDialog("Please enter a valid Password with minimum 6 characters").show();
            initEditTextErrorAnimation(txtPassword);
            return;
        }
        if (!txtPassword.getText().toString().equals(txtConfirmPassword.getText().toString())) {
            showAlertDialog("Passwords doesn't match").show();
            initEditTextErrorAnimation(txtPassword);
            initEditTextErrorAnimation(txtConfirmPassword);
            return;
        }
        if (signUpVehicleItemAdapter.getSelected().size() < 1) {
            showAlertDialog("Please select the vehicle types").show();
            vibrate.vibrate(20);
            return;
        }

        FirebaseES firebaseES = new FirebaseES(this);
        firebaseES.checkAndCreateUser(this, txtName.getText().toString(), txtEmail.getText().toString(), txtPhoneNo.getText().toString(), txtPassword.getText().toString(), signUpVehicleItemAdapter.getSelected());
    }

    private void createVehicleList() {
        vehicles = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Vehicle vehicle = new Vehicle();
            vehicle.setName(vehicleTypes[i]);
            vehicle.setResID(vehicleDrawables[i]);
            vehicles.add(vehicle);
        }
        signUpVehicleItemAdapter.setVehicles(vehicles);
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
