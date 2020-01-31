package com.hishd.emptyslot;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.hishd.emptyslot.Util.GridSpacingItemDecoration;
import com.hishd.emptyslot.Util.MyVehicle;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class activity_booking_private_parking extends AppCompatActivity {

    TextView txtParkingName;
    TextView txtParkingNameCaption;
    TextView txtParkingRate;
    TextView txtEstTime;
    TextView txtSelectedVehicle;
    RecyclerView rclr_myVehicles;
    Button btnBookNow;
    AppConfig appConfig;

    FirebaseRecyclerAdapter<MyVehicle, ViewHolder> adapter;

    LocationListener locationListener;
    LocationManager locationManager;
    Criteria criteria;
    String locationProvider;
    Location location;
    Location lotLocation;
    Vibrator vibrate;
    double estimatedTime = 100;

    FirebaseES firebaseES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_private_parking);

        appConfig = new AppConfig(activity_booking_private_parking.this);

        firebaseES = new FirebaseES(this);

        txtParkingName = findViewById(R.id.txtParkingName);
        txtParkingNameCaption = findViewById(R.id.txtParkingNameCaption);
        txtParkingRate = findViewById(R.id.txtParkingRate);
        rclr_myVehicles = findViewById(R.id.rclr_myVehicles);
        btnBookNow = findViewById(R.id.btnBookNow);
        txtEstTime = findViewById(R.id.txtEstTime);
        txtSelectedVehicle = findViewById(R.id.txtSelectedVehicle);

        txtParkingName.setText(getIntent().getStringExtra("PARKING_ID"));
        txtParkingNameCaption.setText(getIntent().getStringExtra("PARKING_NAME"));
        txtParkingRate.setText(String.valueOf(getIntent().getIntExtra("PARKING_RATE", 100)));

        vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        lotLocation = new Location("My Location");
        lotLocation.setLatitude(getIntent().getDoubleExtra("PARKING_LAT", 0));
        lotLocation.setLongitude(getIntent().getDoubleExtra("PARKING_LNG", 0));

        rclr_myVehicles.setLayoutManager(new GridLayoutManager(this, 3));
        rclr_myVehicles.addItemDecoration(new GridSpacingItemDecoration(3, 10, true));
        fetch();
        initFetchLocationEstTime();

        btnBookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtSelectedVehicle.getText().toString().length() < 4) {
                    showAlertDialog("Please Select a Vehicle").show();
                    vibrate.vibrate(30);
                    return;
                }

                if (estimatedTime > 10) {
                    new SweetAlertDialog(activity_booking_private_parking.this
                            , SweetAlertDialog.WARNING_TYPE).setTitleText("Are you Sure?")
                            .setContentText("The Calculated estimate time to reach the selected parking lot from your current location will be around\n" + String.format("%.2f", estimatedTime) + " minutes.\n If you are unable to reach the location within 10 minutes after placing this booking the booking will be automatically cancelled.")
                            .setConfirmText("Yes")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    firebaseES.checkAndPlaceBooking(txtParkingName.getText().toString(), txtParkingNameCaption.getText().toString(), txtSelectedVehicle.getText().toString());
                                }
                            }).setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    }).show();
                    return;
                }

                new SweetAlertDialog(activity_booking_private_parking.this
                        , SweetAlertDialog.WARNING_TYPE).setTitleText("Confirm Booking")
                        .setContentText("Confirm new booking to parking lot " + txtParkingNameCaption.getText().toString() + " for vehicle No : " + txtSelectedVehicle.getText().toString() + "\nPlease make sure you reach the place in 10 minutes otherwise the booking will be cancelled automatically.")
                        .setConfirmText("Confirm")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                firebaseES.checkAndPlaceBooking(txtParkingName.getText().toString(), txtParkingNameCaption.getText().toString(), txtSelectedVehicle.getText().toString());
                            }
                        }).setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                }).show();
                return;

            }
        });
    }

    private Flashbar showAlertDialog(String message) {
        return new Flashbar.Builder(activity_booking_private_parking.this)
                .gravity(Flashbar.Gravity.TOP)
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

    private void initFetchLocationEstTime() {
        if (ContextCompat.checkSelfPermission(activity_booking_private_parking.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity_booking_private_parking.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(activity_booking_private_parking.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(activity_booking_private_parking.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        } else {

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    txtEstTime.setText(String.format("%.2f", ((location.distanceTo(lotLocation) / 1000) / 30) * 60) + " Minutes");
                    estimatedTime = ((location.distanceTo(lotLocation) / 1000) / 30) * 60;
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            criteria = new Criteria();
            locationProvider = locationManager.getBestProvider(criteria, true);
            location = locationManager.getLastKnownLocation(locationProvider);

            if (location != null) {
                locationListener.onLocationChanged(location);
                txtEstTime.setText(String.format("%.2f", ((location.distanceTo(lotLocation) / 1000) / 30) * 60) + " Minutes");
            }

            locationManager.requestLocationUpdates(locationProvider, 5000, 0, locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(activity_booking_private_parking.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
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
            this.imgCategory.setImageDrawable(ContextCompat.getDrawable(activity_booking_private_parking.this, imgCategory));
        }

        public void bind(final String vehicleNo) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtSelectedVehicle.setText(vehicleNo);
                }
            });
        }


    }
}
