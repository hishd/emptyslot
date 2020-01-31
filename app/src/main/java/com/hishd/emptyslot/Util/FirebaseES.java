package com.hishd.emptyslot.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnim;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.maps.android.clustering.ClusterManager;
import com.hishd.emptyslot.R;
import com.hishd.emptyslot.Vehicle;
import com.hishd.emptyslot.activity_booking_private_parking;
import com.hishd.emptyslot.activity_login;
import com.hishd.emptyslot.activity_main_map_view;
import com.hishd.emptyslot.activity_public_parking;
import com.hishd.emptyslot.activity_view_private_parking;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FirebaseES {

    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    StorageReference uploadFileReferenace;
    User user;
    Context context;
    Dialog dialog;
    ArrayList<String> vehicleList;
    Vibrator vibrate;
    AppConfig appConfig;
    ParkingInquiries parkingInquiries;
    PublicParkingLot publicParkingLot;
    PrivateParkingLot privateParkingLot;
    ClusterManager clusterManager;
    int hw_slots = 0;
    ChildEventListener public_parking_listner;
    private GoogleMap mMap;
    Animation fadeIn, enterLeft, enterRight, enterFromTop;


    public FirebaseES(Context context) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        this.context = context;
        vibrate = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        appConfig = new AppConfig(context);
    }

    private void showProgressDialog() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.loading_view);
        dialog.show();
        dialog.setCancelable(false);
    }

    private void dismissProgressDialog() {
        if (dialog != null)
            dialog.cancel();
    }

    public void checkAndPlaceBooking(final String lotId, final String parkingName, final String vehicleNo) {
        databaseReference = firebaseDatabase.getReference();
        showProgressDialog();

        databaseReference.child("users").child(appConfig.getLogedUserID()).child("bookings").child("active").child(vehicleNo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Booking booking = new Booking(appConfig.getLogedUserID(), vehicleNo, (DateFormat.format("dd-MM-yyyy hh:mm", new java.util.Date()).toString()), lotId, parkingName);
                    placeBooking(booking);
                } else {
                    showAlertDialog("A booking is active for the Selected Vehicle").show();
                    vibrate.vibrate(100);
                    dismissProgressDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dismissProgressDialog();
            }
        });
    }

    private void placeBooking(final Booking booking) {
        databaseReference = firebaseDatabase.getReference();
        databaseReference.child("parking_lot").child("private_parking").child(booking.lot_id).child("bookings").child(booking.vehicle_no).setValue(booking).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i("FIREBASE", "USER BOOKING DATA ADDED");
//                    Toast.makeText(context, "Booking Placed", Toast.LENGTH_LONG).show();
                    dismissProgressDialog();
                    databaseReference.child("users").child(appConfig.getLogedUserID()).child("bookings").child("active").child(booking.vehicle_no).setValue(booking);
                    new SweetAlertDialog(context).setTitleText("Booking Placed").setContentText("Booking Placed Successfully").show();
                } else {
                    Log.i("FIREBASE", "FAILED TO ADD Booking");
                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Something went wrong!")
                            .show();
                }
                dismissProgressDialog();
            }
        });
    }

    public void removeVehicle(String vehNo) {
        showProgressDialog();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.child("users").child(appConfig.getLogedUserID()).child("my_vehicles").child(vehNo).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dismissProgressDialog();
                if (task.isSuccessful())
                    Toast.makeText(context, "Vehicle Removed.", Toast.LENGTH_LONG).show();
                else
                    showAlertDialog("Unable to remove vehicle");
            }
        });
    }

    public void checkAndAddVehicle(final MyVehicle vehicle) {
        databaseReference = firebaseDatabase.getReference();
        showProgressDialog();

        databaseReference.child("users").child(appConfig.getLogedUserID()).child("my_vehicles").child(vehicle.reg_no).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null)
                    addMyVehicle(vehicle);
                else {
                    showAlertDialog("Vehicle Already Exists").show();
                    vibrate.vibrate(100);
                    dismissProgressDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dismissProgressDialog();
            }
        });
    }

    public void addMyVehicle(MyVehicle vehicle) {
        databaseReference = firebaseDatabase.getReference();
        databaseReference.child("users").child(appConfig.getLogedUserID()).child("my_vehicles").child(vehicle.reg_no).setValue(vehicle).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i("FIREBASE", "USER VEHICLE DATA ADDED");
                    Toast.makeText(context, "Vehicle Added", Toast.LENGTH_LONG).show();
                    dismissProgressDialog();
                } else
                    Log.i("FIREBASE", "FAILED TO ADD USER");
                dismissProgressDialog();
            }
        });
    }

    public void loadPublicParkingInfo(final Activity activity, String dbReferenace) {
        if (dbReferenace == null) {
            Toast.makeText(activity, "Failed to Load Public Parking Information", Toast.LENGTH_LONG).show();
            return;
        }

        final TextView txtParkingName = activity.findViewById(R.id.txtParkingName);
        final TextView txtTotalSpaces = activity.findViewById(R.id.txtTotalSpaces);
        final TextView txtParkingCategory = activity.findViewById(R.id.txtParkingCategory);
        final TextView txtParkingNameCaption = activity.findViewById(R.id.txtParkingNameCaption);
        final TextView txtLandmarks = activity.findViewById(R.id.txtLandmarks);
        final TextView txtSpecialInfo = activity.findViewById(R.id.txtSpecialInfo);
        final TextView txtSmartParkingArea = activity.findViewById(R.id.txtSmartParkingArea);
        final ImageView imgParkingLot = activity.findViewById(R.id.imgParkingLot);
        final Button btnNavigate = activity.findViewById(R.id.btnNavigate);
        final LinearLayout layout_total_spaces = activity.findViewById(R.id.layout_total_spaces);

        fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_enter);
        enterLeft = AnimationUtils.loadAnimation(context, R.anim.slide_left_enter);
        enterRight = AnimationUtils.loadAnimation(context, R.anim.swipe_right_enter);
        enterFromTop = AnimationUtils.loadAnimation(context, R.anim.slide_down_enter);

        databaseReference = firebaseDatabase.getReference();

        databaseReference.child("parking_lot").child("public_parking").child(dbReferenace).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Log.i("Public Parking", dataSnapshot.toString());
                    publicParkingLot = dataSnapshot.getValue(PublicParkingLot.class);
                    txtParkingName.setText(publicParkingLot.parking_name);
                    txtTotalSpaces.setText(publicParkingLot.available_slots);
                    txtParkingCategory.setText(publicParkingLot.is_temporary ? "Temporary" : "General");
                    txtParkingNameCaption.setText(publicParkingLot.parking_name);
                    txtLandmarks.setText(publicParkingLot.landmarks);
                    txtSpecialInfo.setText(publicParkingLot.special_notes);
                    txtSmartParkingArea.setText(publicParkingLot.smart_parking_active ? "YES" : "NO");
                    Glide.with(activity).load(publicParkingLot.image_url).into(imgParkingLot);

                    layout_total_spaces.startAnimation(enterFromTop);
                    txtParkingNameCaption.startAnimation(enterLeft);
                    txtLandmarks.startAnimation(enterRight);
                    txtSmartParkingArea.startAnimation(enterLeft);
                    btnNavigate.startAnimation(fadeIn);

                    txtParkingName.startAnimation(enterFromTop);

                    if (publicParkingLot.is_temporary)
                        txtParkingCategory.setTextColor(Color.RED);

                    btnNavigate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String uri = String.format(Locale.ENGLISH, "google.navigation:q=%f,%f", publicParkingLot.location_lat, publicParkingLot.location_lon);
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void loadPrivateParkingInfo(final Activity activity, final String dbReferenace, final RecyclerView rclr_facilities) {

        if (dbReferenace == null) {
            Toast.makeText(activity, "Failed to Load Private Parking Information", Toast.LENGTH_LONG).show();
            return;
        }

        final TextView txtParkingName = activity.findViewById(R.id.txtParkingName);
        final TextView txtTotalSpaces = activity.findViewById(R.id.txtTotalSpaces);
        final TextView txtAvailability = activity.findViewById(R.id.txtAvailability);
        final TextView txtParkingNameCaption = activity.findViewById(R.id.txtParkingNameCaption);
        final TextView txtParkingRate = activity.findViewById(R.id.txtParkingRate);
        final TextView txtSpecialInfo = activity.findViewById(R.id.txtSpecialInfo);
        final TextView txtAvailableSpaces = activity.findViewById(R.id.txtAvailableSpaces);
        final ImageView imgParkingLot = activity.findViewById(R.id.imgParkingLot);
        final Button btnNavigate = activity.findViewById(R.id.btnNavigate);
        final Button btnBookNow = activity.findViewById(R.id.btnBookNow);
        final LinearLayout layout_total_spaces = activity.findViewById(R.id.layout_total_spaces);
        final LinearLayout layout_available_spaces = activity.findViewById(R.id.layout_available_spaces);

        fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_enter);
        enterLeft = AnimationUtils.loadAnimation(context, R.anim.slide_left_enter);
        enterRight = AnimationUtils.loadAnimation(context, R.anim.swipe_right_enter);

        databaseReference = firebaseDatabase.getReference();

        databaseReference.child("parking_lot").child("private_parking").child(dbReferenace).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Log.i("Private Parking", dataSnapshot.toString());
                    privateParkingLot = dataSnapshot.getValue(PrivateParkingLot.class);
                    txtParkingName.setText(privateParkingLot.lot_name);
                    txtTotalSpaces.setText((String.valueOf(privateParkingLot.total_slots)));
                    txtAvailability.setText(privateParkingLot.availability.toUpperCase());
                    txtParkingNameCaption.setText(privateParkingLot.lot_name);
                    txtParkingRate.setText((String.valueOf(privateParkingLot.rate_hr)));
                    txtSpecialInfo.setText(privateParkingLot.info);
                    txtAvailableSpaces.setText((String.valueOf(privateParkingLot.available_slots)));
                    Glide.with(activity).load(privateParkingLot.image_url).into(imgParkingLot);

                    layout_available_spaces.startAnimation(enterLeft);
                    layout_total_spaces.startAnimation(enterRight);
                    btnNavigate.startAnimation(fadeIn);
                    btnBookNow.startAnimation(fadeIn);

                    ArrayList<Facility> facilityArrayList;
                    ParkingFacilityAdapter parkingFacilityAdapter;

                    facilityArrayList = new ArrayList<>();
                    parkingFacilityAdapter = new ParkingFacilityAdapter(context, facilityArrayList);
                    rclr_facilities.setAdapter(parkingFacilityAdapter);

                    if (privateParkingLot.facilities.get("card_payments")) {
                        Facility facility = new Facility("Card Payments", R.drawable.card_payments, R.drawable.facility_card_payment_bg);
                        facilityArrayList.add(facility);
                    }
                    if (privateParkingLot.facilities.get("cctv")) {
                        Facility facility = new Facility("CCTV", R.drawable.cctv, R.drawable.facility_cctv_bg);
                        facilityArrayList.add(facility);
                    }
                    if (privateParkingLot.facilities.get("closed_area")) {
                        Facility facility = new Facility("Closed Area", R.drawable.closed_area, R.drawable.facility_closed_area);
                        facilityArrayList.add(facility);
                    }
                    if (privateParkingLot.facilities.get("ev_points")) {
                        Facility facility = new Facility("EV Charge", R.drawable.ev_points, R.drawable.facility_evpoint_bg);
                        facilityArrayList.add(facility);
                    }
                    if (privateParkingLot.facilities.get("food_court")) {
                        Facility facility = new Facility("Food Court", R.drawable.food_court, R.drawable.facility_foodcourt_bg);
                        facilityArrayList.add(facility);
                    }
                    if (privateParkingLot.facilities.get("security")) {
                        Facility facility = new Facility("Security", R.drawable.security, R.drawable.facility_security_bg);
                        facilityArrayList.add(facility);
                    }
                    if (privateParkingLot.facilities.get("wash_rooms")) {
                        Facility facility = new Facility("Wash Rooms", R.drawable.wash_rooms, R.drawable.facility_washroom);
                        facilityArrayList.add(facility);
                    }
                    if (privateParkingLot.facilities.get("wifi")) {
                        Facility facility = new Facility("WiFi", R.drawable.wifi, R.drawable.facility_wifi_bg);
                        facilityArrayList.add(facility);
                    }
                    parkingFacilityAdapter.setFacilities(facilityArrayList);

                    btnNavigate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String uri = String.format(Locale.ENGLISH, "google.navigation:q=%f,%f", privateParkingLot.loc_lat, privateParkingLot.loc_lng);
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
                        }
                    });

                    btnBookNow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activity.startActivity(new Intent(context, activity_booking_private_parking.class)
                                    .putExtra("PARKING_ID", dbReferenace)
                                    .putExtra("PARKING_NAME", privateParkingLot.lot_name)
                                    .putExtra("PARKING_RATE", privateParkingLot.rate_hr)
                                    .putExtra("PARKING_LAT", privateParkingLot.loc_lat)
                                    .putExtra("PARKING_LNG", privateParkingLot.loc_lng));
                        }
                    });

                } else {
                    Toast.makeText(activity, "Failed to Load Parking Information", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("parking_lot").child("private_parking").child(dbReferenace).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    hw_slots = 0;
                    privateParkingLot = dataSnapshot.getValue(PrivateParkingLot.class);
                    txtAvailability.setText(privateParkingLot.availability);

                    for (Map.Entry<String, ParkingNode> node : privateParkingLot.parking_slots.entrySet())
                        if (!node.getValue().status) hw_slots++;
                    txtAvailableSpaces.setText((String.valueOf(privateParkingLot.total_slots - privateParkingLot.accomodated_slots - hw_slots - privateParkingLot.bookings.size())));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void populateParkings(GoogleMap mMap) {
        this.mMap = mMap;
        clusterManager = new ClusterManager<SlotMarkerItem>(context, mMap);
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);

        final PublicClusterRenderer clusterRenderer = new PublicClusterRenderer(context, mMap, clusterManager);

        clusterManager.setRenderer(clusterRenderer);
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<SlotMarkerItem>() {
            @Override
            public boolean onClusterItemClick(SlotMarkerItem slotMarkerItem) {
                if (slotMarkerItem.isPublicParking()) {
                    context.startActivity(new Intent(context, activity_public_parking.class).putExtra("REFERANCE", slotMarkerItem.getParkingID()));
                }
                if (!slotMarkerItem.isPublicParking()) {
                    context.startActivity(new Intent(context, activity_view_private_parking.class).putExtra("REFERANCE", slotMarkerItem.getParkingID()));
                }
                return false;
            }
        });

        populatePublicParkings();
        populatePrivateParkings();
    }


    private void populatePublicParkings() {

        databaseReference = firebaseDatabase.getReference("parking_lot/public_parking");

//        public_parking_listner = new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//        databaseReference.addChildEventListener(public_parking_listner);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        publicParkingLot = child.getValue(PublicParkingLot.class);
                        clusterManager.addItem(new SlotMarkerItem(child.getKey(), true, publicParkingLot.parking_name, publicParkingLot.location_lat, publicParkingLot.location_lon));
                    }
                } else {
                    Log.i("Public parkings", "No Public Parking Detected");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void populatePrivateParkings() {

        databaseReference = firebaseDatabase.getReference("parking_lot/private_parking");

//        public_parking_listner = new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//        databaseReference.addChildEventListener(public_parking_listner);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        privateParkingLot = child.getValue(PrivateParkingLot.class);
                        clusterManager.addItem(new SlotMarkerItem(child.getKey(), false, privateParkingLot.lot_name, privateParkingLot.loc_lat, privateParkingLot.loc_lng));
                    }
                } else {
                    Log.i("Private parking", "No Private Parking Detected");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addPublicParking(final Activity activity, final String parkingName, final String landMarks, final String noOfSlots, final String specialNotes, final double locationLat, final double locationLon, final byte[] imageFile, final boolean isTemp) {
        databaseReference = firebaseDatabase.getReference("parking_lot/public_parking");
        showProgressDialog();

        databaseReference.child(appConfig.getLogedUserID() + "_" + parkingName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dismissProgressDialog();
                if (dataSnapshot.getValue() == null)
                    uploadFilePublicParking(activity, parkingName, landMarks, noOfSlots, specialNotes, locationLat, locationLon, imageFile, isTemp);
                else {
                    showAlertDialog("Parking Space with a same name is already added by you.").show();
                    vibrate.vibrate(100);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dismissProgressDialog();
            }
        });
    }

    private void uploadFilePublicParking(final Activity activity, final String parkingName, final String landMarks, final String noOfSlots, final String specialNotes, final double locationLat, final double locationLon, final byte[] imageFile, final boolean isTemp) {
        storageReference = firebaseStorage.getReference("parking/public/uploads");

        final ProgressBar progressBar = activity.findViewById(R.id.progressImgUpload);

        uploadFileReferenace = storageReference.child(appConfig.getLogedUserID() + "_" + parkingName);

        uploadFileReferenace.putBytes(imageFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(0);
                    }
                }, 500);

                uploadFileReferenace.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful() && task.getResult() != null) {

                            publicParkingLot = new PublicParkingLot(parkingName, landMarks, noOfSlots, specialNotes, locationLat, locationLon, task.getResult().toString(), isTemp, false, false, new ArrayList<ParkingNode>());
                            databaseReference = firebaseDatabase.getReference("parking_lot/public_parking");

                            databaseReference.child(appConfig.getLogedUserID() + "_" + parkingName).setValue(publicParkingLot).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.i("FIREBASE", "PUBLIC PARKING ADDED");

                                        if (!activity.isFinishing() && !activity.isDestroyed()) {
                                            SweetAlertDialog sad = new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
                                                    .setTitleText("Parking Added")
                                                    .setContentText("The added parking space will be visible in the app, once we reviewed the space.")
                                                    .setConfirmText("Ok")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                            sweetAlertDialog.dismissWithAnimation();
                                                            activity.finish();
                                                        }
                                                    })
                                                    .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                            sweetAlertDialog.dismissWithAnimation();
                                                            activity.finish();
                                                        }
                                                    });
                                            sad.setCancelable(false);
                                            sad.show();
                                        } else {
                                            Toast.makeText(context, "Your Public Parking added", Toast.LENGTH_LONG).show();
                                        }

                                    } else
                                        Log.i("FIREBASE", "FAILED TO ADD PUBLIC PARKING");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            }).addOnCanceledListener(new OnCanceledListener() {
                                @Override
                                public void onCanceled() {

                                }
                            });

                        } else {
                            Toast.makeText(context, "There were some errors while adding the public parking entry.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to Upload Image", Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(context, "Uploading Image", Toast.LENGTH_LONG).show();
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressBar.setProgress((int) progress);
            }
        });
    }


    public void setParkingInquiries(final Activity activity, final String ownerName, final String contactNo, final String ownerAddress, final String noOfSlots, final String ratePerHour, final double locationLat, final double locationLon, final byte[] imageFile, final String requestHW, final boolean isCompleted) {
        databaseReference = firebaseDatabase.getReference("inquiries");
        showProgressDialog();

        databaseReference.child(contactNo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dismissProgressDialog();
                if (dataSnapshot.getValue() == null)
                    uploadFilePrivateInq(activity, ownerName, contactNo, ownerAddress, noOfSlots, ratePerHour, locationLat, locationLon, imageFile, requestHW, isCompleted);
                else {
                    showAlertDialog("You have pending Inquiries, wait till they get accepted.").show();
                    vibrate.vibrate(100);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dismissProgressDialog();
            }
        });
    }

    private void uploadFilePrivateInq(final Activity activity, final String ownerName, final String contactNo, final String ownerAddress, final String noOfSlots, final String ratePerHour, final double locationLat, final double locationLon, final byte[] imageFile, final String requestHW, final boolean isCompleted) {
        storageReference = firebaseStorage.getReference("inquiries/uploads");

        final ProgressBar progressBar = activity.findViewById(R.id.progressImgUpload);

        uploadFileReferenace = storageReference.child(contactNo);

        uploadFileReferenace.putBytes(imageFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(0);
                    }
                }, 500);

                uploadFileReferenace.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            parkingInquiries = new ParkingInquiries(ownerName, contactNo, ownerAddress, noOfSlots, ratePerHour, locationLat, locationLon, task.getResult().toString(), requestHW, isCompleted);
                            databaseReference = firebaseDatabase.getReference("inquiries");


                            databaseReference.child(contactNo).setValue(parkingInquiries).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.i("FIREBASE", "NEW INQUIRIES ADDED");

                                        if (!activity.isFinishing() && !activity.isDestroyed()) {
                                            SweetAlertDialog sad = new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
                                                    .setTitleText("Inquiry Placed")
                                                    .setContentText("We will get back to you soon once your inquiry gets reviewed")
                                                    .setConfirmText("Ok")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                            sweetAlertDialog.dismissWithAnimation();
                                                            activity.finish();
                                                        }
                                                    })
                                                    .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                            sweetAlertDialog.dismissWithAnimation();
                                                            activity.finish();
                                                        }
                                                    });
                                            sad.setCancelable(false);
                                            sad.show();
                                        } else {
                                            Toast.makeText(context, "Your Inquiry added", Toast.LENGTH_LONG).show();
                                        }

                                    } else
                                        Log.i("FIREBASE", "FAILED TO ADD PRIVATE PARK");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            }).addOnCanceledListener(new OnCanceledListener() {
                                @Override
                                public void onCanceled() {

                                }
                            });

                        } else {
                            Toast.makeText(context, "There were some errors while placing request", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to Upload Image", Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(context, "Uploading Image", Toast.LENGTH_LONG).show();
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressBar.setProgress((int) progress);
            }
        });
    }

    public void setSettingsUserInfo(final Activity activity, final String phone) {
        showProgressDialog();
        Log.i("FIREBASE", "Retrieving logged user info");
        databaseReference = firebaseDatabase.getReference();

        databaseReference.child("users").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    user = dataSnapshot.getValue(User.class);
                    ((TextView) activity.findViewById(R.id.txtName)).setText(user.username);
                    ((TextView) activity.findViewById(R.id.txtEmail)).setText(user.email);
                } else {
                    Toast.makeText(context, "Couldn't read user info", Toast.LENGTH_LONG).show();
                    vibrate.vibrate(100);
                }
                dismissProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dismissProgressDialog();
            }
        });
    }

    public void refreshSettingsUserInfo(final Activity activity, final String phone) {
        Log.i("FIREBASE", "Retrieving logged user info");
        databaseReference = firebaseDatabase.getReference();

        databaseReference.child("users").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    user = dataSnapshot.getValue(User.class);
                    ((TextView) activity.findViewById(R.id.txtName)).setText(user.username);
                    ((TextView) activity.findViewById(R.id.txtEmail)).setText(user.email);
                } else {
                    Toast.makeText(context, "Couldn't read user info", Toast.LENGTH_LONG).show();
                    vibrate.vibrate(100);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void checkAndCreateUser(final Context context, final String name, final String email, final String phone, final String pass, final ArrayList<Vehicle> vehicles) {

        showProgressDialog();

        vehicleList = new ArrayList<>();

        for (Vehicle v : vehicles) {
            vehicleList.add(v.getName());
        }

        user = new User(name, email, phone, pass, vehicleList);
        databaseReference = firebaseDatabase.getReference();

        databaseReference.child("users").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null)
                    createNewUser(context, phone);
                else {
                    showAlertDialog("User Already Exists. Please Login").show();
//                    Toast.makeText(context,"User Already Exists",Toast.LENGTH_LONG).show();
                    vibrate.vibrate(100);
                    dismissProgressDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dismissProgressDialog();
            }
        });

    }

    public void userLogin(final Context context, final String phone, final String pass) {

        showProgressDialog();

        databaseReference = firebaseDatabase.getReference();

        databaseReference.child("users").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dismissProgressDialog();

                if (dataSnapshot.getValue() != null) {
                    user = dataSnapshot.getValue(User.class);
                    if (user.password.equals(pass)) {
                        appConfig.setUserLoggedIn();
                        appConfig.setLoggedUserID(phone);
                        context.startActivity(new Intent(context, activity_main_map_view.class));
                        ((Activity) context).finish();
                    } else {
                        showAlertDialog("Invalid username or password").show();
                        vibrate.vibrate(80);
//                        Toast.makeText(context,"Invalid username or password",Toast.LENGTH_LONG).show();
                    }
                } else {
                    vibrate.vibrate(100);
                    showAlertDialog("User doesn't exist please register").show();
//                    Toast.makeText(context,"user doesn't exist please register",Toast.LENGTH_LONG).show();
                    dismissProgressDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dismissProgressDialog();
            }
        });

    }

    public void createNewUser(final Context context, String phone) {
        databaseReference.child("users").child(phone).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i("FIREBASE", "USER DATA ADDED");
                    Toast.makeText(context, "Registration Successful", Toast.LENGTH_LONG).show();
                    context.startActivity(new Intent(context, activity_login.class));
                    ((Activity) context).finish();
                } else
                    Log.i("FIREBASE", "FAILED TO ADD USER");

                dismissProgressDialog();
            }
        });
    }

    public void updateUserName(final Activity activity, final String userID, String name) {
        showProgressDialog();
        Log.i("FIREBASE", "Updating User Name");
        databaseReference = firebaseDatabase.getReference();

        databaseReference.child("users").child(userID).child("username").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i("FIREBASE", "USER DATA UPDATED");
                    Toast.makeText(context, "Name updated Successfully", Toast.LENGTH_LONG).show();
                    refreshSettingsUserInfo(activity, userID);
                } else
                    Log.i("FIREBASE", "FAILED TO UPDATE Name");
                dismissProgressDialog();
            }
        });
    }

    public void updateUserEmail(final Activity activity, final String userID, String email) {
        showProgressDialog();
        Log.i("FIREBASE", "Updating User Email");
        databaseReference = firebaseDatabase.getReference();

        databaseReference.child("users").child(userID).child("email").setValue(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i("FIREBASE", "USER DATA UPDATED");
                    Toast.makeText(context, "E-mail updated Successfully", Toast.LENGTH_LONG).show();
                    refreshSettingsUserInfo(activity, userID);
                } else
                    Log.i("FIREBASE", "FAILED TO UPDATE Email");
                dismissProgressDialog();
            }
        });
    }

    public void updateUserPassword(final Activity activity, final String userID, String password) {
        showProgressDialog();
        Log.i("FIREBASE", "Updating User Password");
        databaseReference = firebaseDatabase.getReference();

        databaseReference.child("users").child(userID).child("password").setValue(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i("FIREBASE", "USER DATA UPDATED");
                    Toast.makeText(context, "Password updated Successfully", Toast.LENGTH_LONG).show();
                    refreshSettingsUserInfo(activity, userID);
                } else
                    Log.i("FIREBASE", "FAILED TO UPDATE Password");
                dismissProgressDialog();
            }
        });
    }

    private Flashbar showAlertDialog(String message) {
        return new Flashbar.Builder((Activity) context)
                .gravity(Flashbar.Gravity.BOTTOM)
                .duration(1000)
                .message(message)
                .messageColor(ContextCompat.getColor(context, R.color.white))
                .backgroundColor(ContextCompat.getColor(context, R.color.errorMessageBackgroundColor))
                .showIcon()
                .iconColorFilterRes(R.color.errorMessageIconColor)
                .icon(R.drawable.ic_cross)
                .enterAnimation(FlashAnim.with(context)
                        .animateBar()
                        .duration(200)
                        .slideFromLeft()
                        .overshoot())
                .exitAnimation(FlashAnim.with(context)
                        .animateBar()
                        .duration(1000)
                        .slideFromLeft()
                        .accelerate())
                .build();
    }
}
