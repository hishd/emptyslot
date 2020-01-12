package com.test.emptyslot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import spencerstudios.com.bungeelib.Bungee;

public class activity_signup extends AppCompatActivity {

    EditText txtName;
    EditText txtEmail;
    EditText txtPassword;
    EditText txtConfirmPassword;
    Button btnSignUp;
    TextView viewAlreadyAMember;
    RecyclerView rclr_vehicles;

    ImageView view_top;
    TextView view_caption_head;
    TextView view_caption_1;

    Animation fadeIn,enterLeft,enterRight,enterFromTop;

    private ArrayList<Vehicle> vehicles;
    private SignUpVehicleItemAdapter signUpVehicleItemAdapter;

    private int[] vehicleDrawables = {R.drawable.car,R.drawable.bus,R.drawable.suv,R.drawable.scooter};
    private String[] vehicleTypes = {"Car","Van","Jeep","Bike"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
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
        signUpVehicleItemAdapter = new SignUpVehicleItemAdapter(this,vehicles);
        rclr_vehicles.setAdapter(signUpVehicleItemAdapter);

        fadeIn = AnimationUtils.loadAnimation(this,R.anim.fade_enter);
        enterLeft = AnimationUtils.loadAnimation(this,R.anim.slide_left_enter);
        enterRight = AnimationUtils.loadAnimation(this,R.anim.swipe_right_enter);
        enterFromTop = AnimationUtils.loadAnimation(this,R.anim.slide_down_enter);

        txtName.startAnimation(enterLeft);
        txtEmail.startAnimation(enterRight);
        txtPassword.startAnimation(enterLeft);
        txtConfirmPassword.startAnimation(enterRight);
        btnSignUp.startAnimation(fadeIn);
        viewAlreadyAMember.startAnimation(fadeIn);

        view_top.startAnimation(enterFromTop);
        view_caption_head.startAnimation(fadeIn);
        view_caption_1.startAnimation(fadeIn);


        viewAlreadyAMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Bungee.fade(activity_signup.this);
            }
        });

        createVehicleList();

        rclr_vehicles.startAnimation(fadeIn);

    }

    private void createVehicleList(){
        vehicles = new ArrayList<>();
        for(int i = 0; i < 4 ; i++){
            Vehicle vehicle = new Vehicle();
            vehicle.setName(vehicleTypes[i]);
            vehicle.setResID(vehicleDrawables[i]);
            vehicles.add(vehicle);
        }
        signUpVehicleItemAdapter.setVehicles(vehicles);
    }
}
