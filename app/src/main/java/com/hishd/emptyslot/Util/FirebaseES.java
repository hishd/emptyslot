package com.hishd.emptyslot.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnim;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hishd.emptyslot.R;
import com.hishd.emptyslot.Vehicle;
import com.hishd.emptyslot.activity_login;
import com.hishd.emptyslot.activity_main_map_view;

import java.util.ArrayList;

import spencerstudios.com.bungeelib.Bungee;

public class FirebaseES {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    User user;
    Context context;
    Dialog dialog;
    ArrayList<String> vehicleList;
    Vibrator vibrate;
    AppConfig appConfig;

    public FirebaseES(Context context){
        firebaseDatabase = FirebaseDatabase.getInstance();
        this.context = context;
        vibrate = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        appConfig = new AppConfig(context);
    }

    private void showProgressDialog(){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.loading_view);
        dialog.show();
        dialog.setCancelable(false);
    }

    private void dismissProgressDialog(){
        if(dialog!=null)
            dialog.cancel();
    }



    public void checkAndCreateUser(final Context context, final String name, final String email, final String phone, final String pass, final ArrayList<Vehicle> vehicles){

        showProgressDialog();

        vehicleList = new ArrayList<>();

        for (Vehicle v: vehicles) {
            vehicleList.add(v.getName());
        }

        user = new User(name,email,phone,pass,vehicleList);
        databaseReference = firebaseDatabase.getReference();

        databaseReference.child("users").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()==null)
                    createNewUser(context,phone);
                else{
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

    public void userLogin(final Context context,final String phone, final String pass){

        showProgressDialog();

        databaseReference = firebaseDatabase.getReference();

        databaseReference.child("users").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    user = dataSnapshot.getValue(User.class);
                    if(user.password.equals(pass)){
                        appConfig.setUserLoggedIn();
                        context.startActivity(new Intent(context,activity_main_map_view.class));
                        Bungee.zoom(context);
                        ((Activity)context).finish();
                    }else{
                        showAlertDialog("Invalid username or password").show();
                        vibrate.vibrate(80);
//                        Toast.makeText(context,"Invalid username or password",Toast.LENGTH_LONG).show();
                        dismissProgressDialog();
                    }
                }
                else{
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

    public void createNewUser(final Context context,String phone){
        databaseReference.child("users").child(phone).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.i("FIREBASE","USER DATA ADDED");
                    Toast.makeText(context,"Registration Successful",Toast.LENGTH_LONG).show();
                    context.startActivity(new Intent(context,activity_login.class));
                    ((Activity)context).finish();
                }
                else
                    Log.i("FIREBASE","FAILED TO ADD USER");

                dismissProgressDialog();
            }
        });
    }

    private Flashbar showAlertDialog(String message){
        return new Flashbar.Builder((Activity)context)
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
