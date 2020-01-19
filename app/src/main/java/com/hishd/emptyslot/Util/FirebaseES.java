package com.hishd.emptyslot.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnim;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hishd.emptyslot.R;
import com.hishd.emptyslot.Vehicle;
import com.hishd.emptyslot.activity_login;
import com.hishd.emptyslot.activity_main_map_view;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import spencerstudios.com.bungeelib.Bungee;

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

    public void addPublicParking(final Activity activity, final String parkingName, final String landMarks, final String noOfSlots, final String specialNotes, final double locationLat, final double locationLon, final byte[] imageFile, final boolean isTemp) {
        databaseReference = firebaseDatabase.getReference("parking/public");
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

                            publicParkingLot = new PublicParkingLot(parkingName, landMarks, noOfSlots, specialNotes, locationLat, locationLon, task.getResult().toString(), isTemp, false, false, new ArrayList<PublicParkingNode>());
                            databaseReference = firebaseDatabase.getReference("parking/public");

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
                        Bungee.zoom(context);
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
