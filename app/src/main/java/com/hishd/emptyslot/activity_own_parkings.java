package com.hishd.emptyslot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnim;
import com.bumptech.glide.Glide;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.hishd.emptyslot.Util.FirebaseES;
import com.hishd.emptyslot.Util.Validator;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class activity_own_parkings extends AppCompatActivity {

    private final int PICK_IMAGE_REQUEST = 1;
    ImageView view_top;
    TextView view_caption_head;
    TextView view_caption_1;
    EditText txtName;
    EditText txtContactNo;
    EditText txtAddress;
    EditText txtNoOfSlots;
    EditText txtRatePerHour;
    EditText txtSelectLocation;
    ImageView imgParking;
    Switch switch_hw;
    TextView view_what_is_hw;
    ProgressBar progressImgUpload;
    FirebaseES firebaseES;
    Button btnPlaceRequest;
    Vibrator vibrate;
    Animation shakeEditText;
    double pickedLat;
    double pickedLon;
    Animation fadeIn, enterLeft, enterRight, enterFromTop;
    ScrollView scrollView;
    CFAlertDialog.Builder popUpDialog;
    private Uri mImageUri;
    private File mImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_parkings);

        scrollView = findViewById(R.id.scrollView);

        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_enter);
        enterLeft = AnimationUtils.loadAnimation(this, R.anim.slide_left_enter);
        enterRight = AnimationUtils.loadAnimation(this, R.anim.swipe_right_enter);
        enterFromTop = AnimationUtils.loadAnimation(this, R.anim.slide_down_enter);

        vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        view_top = findViewById(R.id.view_top);
        view_caption_head = findViewById(R.id.view_caption_head);
        view_caption_1 = findViewById(R.id.view_caption_1);

        txtName = findViewById(R.id.txtName);
        txtContactNo = findViewById(R.id.txtContactNo);
        txtAddress = findViewById(R.id.txtAddress);
        txtNoOfSlots = findViewById(R.id.txtNoOfSlots);
        txtRatePerHour = findViewById(R.id.txtRatePerHour);
        txtSelectLocation = findViewById(R.id.txtSelectLocation);
        switch_hw = findViewById(R.id.switch_hw);
        view_what_is_hw = findViewById(R.id.view_what_is_hw);
        imgParking = findViewById(R.id.imgParking);
        btnPlaceRequest = findViewById(R.id.btnPlaceRequest);
        progressImgUpload = findViewById(R.id.progressImgUpload);

        view_top.startAnimation(enterFromTop);
        view_caption_head.startAnimation(enterRight);
        view_caption_1.startAnimation(enterLeft);

        txtName.startAnimation(enterRight);
        txtContactNo.startAnimation(enterLeft);
        txtAddress.startAnimation(enterRight);
        txtNoOfSlots.startAnimation(enterLeft);
        txtRatePerHour.startAnimation(enterRight);
        txtSelectLocation.startAnimation(enterLeft);
        imgParking.startAnimation(fadeIn);
        switch_hw.startAnimation(fadeIn);
        view_what_is_hw.startAnimation(fadeIn);

        firebaseES = new FirebaseES(this);

        popUpDialog = new CFAlertDialog.Builder(this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setTitle("Hardware Sensors")
                .setMessage("The Hardware Sensors are used to automate the parking slots that resides in the parking lot, while analyzing and monitoring each parking slot realtime to check whether the parking slot is accommodated or not.\nThere is no need of checking on all the marking slots manually as the sensors will automatically identity when a vehicle come towards a specific parking slot.")
                .setTextGravity(Gravity.CENTER_HORIZONTAL);


        popUpDialog.setHeaderView(R.layout.popup_dialog_header_view);

        txtSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(activity_own_parkings.this, activity_location_picker.class), 999);
            }
        });

        imgParking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        view_what_is_hw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpDialog.show();
            }
        });

        btnPlaceRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shakeEditText = AnimationUtils.loadAnimation(activity_own_parkings.this, R.anim.anim_shake_edit_text);

                if (!Validator.validatePersonName(txtName.getText().toString())) {
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                    txtName.startAnimation(shakeEditText);
                    vibrate.vibrate(30);
                    showAlertDialog("Please enter a valid name between 3-50 characters").show();
                    return;
                }
                if (!Validator.validatePhone(txtContactNo.getText().toString())) {
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                    txtContactNo.startAnimation(shakeEditText);
                    vibrate.vibrate(30);
                    showAlertDialog("Please enter a valid phone number").show();
                    return;
                }
                if (txtAddress.getText().length() < 5) {
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                    txtAddress.startAnimation(shakeEditText);
                    vibrate.vibrate(30);
                    showAlertDialog("Please enter a valid address").show();
                    return;
                }
                if (txtNoOfSlots.getText().length() < 1) {
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                    txtNoOfSlots.startAnimation(shakeEditText);
                    vibrate.vibrate(30);
                    showAlertDialog("Please enter a valid number of slots").show();
                    return;
                }
                if (txtRatePerHour.getText().length() < 2) {
                    txtRatePerHour.startAnimation(shakeEditText);
                    vibrate.vibrate(30);
                    showAlertDialog("Please enter a valid amount of rates").show();
                    return;
                }
                if (txtSelectLocation.getText().length() < 5) {
                    txtSelectLocation.startAnimation(shakeEditText);
                    vibrate.vibrate(30);
                    showAlertDialog("Please select your location").show();
                    return;
                }
                if (mImageUri == null) {
                    imgParking.startAnimation(shakeEditText);
                    vibrate.vibrate(30);
                    showAlertDialog("Please select your image").show();
                    return;
                }

                startFileUpload();
            }
        });

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    private void startFileUpload() {

        if (mImageUri != null) {
            Bitmap bitmap = ((BitmapDrawable) imgParking.getDrawable()).getBitmap();
            ByteArrayOutputStream byteArrayOPStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOPStream);
            byte[] data = byteArrayOPStream.toByteArray();

            firebaseES.setParkingInquiries(this,
                    txtName.getText().toString(),
                    txtContactNo.getText().toString(),
                    txtAddress.getText().toString(),
                    txtNoOfSlots.getText().toString(),
                    txtRatePerHour.getText().toString(),
                    pickedLat,
                    pickedLon,
                    data,
                    switch_hw.isChecked() ? "YES" : "NO",
                    false
            );
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 999 && resultCode == RESULT_OK) {
            txtSelectLocation.setText(String.format("LAT: %.4f", data.getDoubleExtra("LAT", 0)));
            txtSelectLocation.append(String.format(" LNG: %.4f", data.getDoubleExtra("LNG", 0)));

            pickedLat = data.getDoubleExtra("LAT", 0);
            pickedLon = data.getDoubleExtra("LNG", 0);
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Glide.with(this).load(mImageUri).into(imgParking);
        }
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
