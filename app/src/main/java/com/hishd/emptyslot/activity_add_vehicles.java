package com.hishd.emptyslot;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnim;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.hishd.emptyslot.Util.AppConfig;
import com.hishd.emptyslot.Util.FirebaseES;
import com.hishd.emptyslot.Util.MyVehicle;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class activity_add_vehicles extends AppCompatActivity {

    RecyclerView rclr_vehicles;
    RecyclerView rclr_myVehicles;
    FirebaseRecyclerAdapter<MyVehicle, ViewHolder> adapter;
    EditText txtRegNo;
    EditText txtVehModel;
    Button btnAddVehicle;
    Vibrator vibrate;
    Animation shakeEditText;
    FirebaseES firebaseES;
    AppConfig appConfig;
    private ArrayList<Vehicle> vehicles;
    private AddVehicleVehicleAdapter addVehicleVehicleAdapter;
    private int[] vehicleDrawables = {R.drawable.car, R.drawable.bus, R.drawable.suv, R.drawable.scooter};
    private String[] vehicleTypes = {"Car", "Van", "Jeep", "Bike"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicles);

        appConfig = new AppConfig(activity_add_vehicles.this);

        firebaseES = new FirebaseES(this);

        vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        rclr_vehicles = findViewById(R.id.rclr_vehicles);
        txtRegNo = findViewById(R.id.txtRegNo);
        txtVehModel = findViewById(R.id.txtVehModel);
        btnAddVehicle = findViewById(R.id.btnAddVehicle);
        rclr_myVehicles = findViewById(R.id.rclr_myVehicles);
        rclr_myVehicles.setLayoutManager(new GridLayoutManager(this, 3));

        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rclr_vehicles.setLayoutManager(horizontalLayoutManagaer);

        vehicles = new ArrayList<>();
        addVehicleVehicleAdapter = new AddVehicleVehicleAdapter(this, vehicles);
        rclr_vehicles.setAdapter(addVehicleVehicleAdapter);


        createVehicleList();

        fetch();

        btnAddVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtRegNo.getText().toString().length() < 6) {
                    initEditTextErrorAnimation(txtRegNo);
                    showAlertDialog("Enter a valid Registration No");
                    return;
                }
                if (txtVehModel.getText().toString().length() < 4) {
                    initEditTextErrorAnimation(txtVehModel);
                    showAlertDialog("Enter a valid Vehicle Model");
                    return;
                }

                MyVehicle vehicle = new MyVehicle(txtRegNo.getText().toString().replace("-", "_").toUpperCase(), txtVehModel.getText().toString(), addVehicleVehicleAdapter.getSelected().getName());
                firebaseES.checkAndAddVehicle(vehicle);
            }
        });
    }

    private void createVehicleList() {
        vehicles = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Vehicle vehicle = new Vehicle();
            vehicle.setName(vehicleTypes[i]);
            vehicle.setResID(vehicleDrawables[i]);
            vehicles.add(vehicle);
        }
        addVehicleVehicleAdapter.setVehicles(vehicles);
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

    private void fetch() {
        Query query = FirebaseDatabase.getInstance().getReference().child("users").child(appConfig.getLogedUserID()).child("my_vehicles");
        FirebaseRecyclerOptions<MyVehicle> options = new FirebaseRecyclerOptions.Builder<MyVehicle>().setQuery(query, new SnapshotParser<MyVehicle>() {
            @NonNull
            @Override
            public MyVehicle parseSnapshot(@NonNull DataSnapshot snapshot) {
                return new MyVehicle(snapshot.child("reg_no").getValue().toString(),
                        snapshot.child("vehicle_model").getValue().toString(),
                        snapshot.child("vehicle_category").getValue().toString());
            }
        }).build();

        adapter = new FirebaseRecyclerAdapter<MyVehicle, ViewHolder>(options) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_my_vehicle_item, parent, false);
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull MyVehicle myVehicle) {
                viewHolder.setTxtVehicleNo(myVehicle.reg_no);
                viewHolder.setTxtVehicleModel(myVehicle.vehicle_model);
                viewHolder.bind(myVehicle.reg_no);

                if (myVehicle.vehicle_category.equals("Car"))
                    viewHolder.setImgCategory(R.drawable.car);
                if (myVehicle.vehicle_category.equals("Van"))
                    viewHolder.setImgCategory(R.drawable.bus);
                if (myVehicle.vehicle_category.equals("Jeep"))
                    viewHolder.setImgCategory(R.drawable.suv);
                if (myVehicle.vehicle_category.equals("Bike"))
                    viewHolder.setImgCategory(R.drawable.scooter);
            }
        };

        rclr_myVehicles.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtVehicleNo;
        public ImageView imgCategory;
        public TextView txtVehicleModel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtVehicleNo = itemView.findViewById(R.id.txtVehicleNo);
            imgCategory = itemView.findViewById(R.id.imgCategory);
            txtVehicleModel = itemView.findViewById(R.id.txtVehicleModel);
        }

        public void setTxtVehicleModel(String txtVehicleModel) {
            this.txtVehicleModel.setText(txtVehicleModel);
        }

        public void setTxtVehicleNo(String txtVehicleNo) {
            this.txtVehicleNo.setText(txtVehicleNo);
        }

        public void setImgCategory(int imgCategory) {
            this.imgCategory.setImageDrawable(ContextCompat.getDrawable(activity_add_vehicles.this, imgCategory));
        }

        public void bind(final String vehicleNo) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SweetAlertDialog(activity_add_vehicles.this, SweetAlertDialog.WARNING_TYPE).setTitleText("Remove Vehicle")
                            .setContentText("Are you sure you want to remove vehicle \n " + vehicleNo + "\n\n")
                            .setConfirmText("Remove")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    firebaseES.removeVehicle(vehicleNo);
                                }
                            }).setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    }).show();
                }
            });
        }


    }
}
