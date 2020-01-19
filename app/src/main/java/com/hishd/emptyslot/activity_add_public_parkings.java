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

public class activity_add_public_parkings extends AppCompatActivity {

    private final int PICK_IMAGE_REQUEST = 1;
    FirebaseES firebaseES;
    Vibrator vibrate;
    Animation shakeEditText;
    double pickedLat;
    double pickedLon;
    Animation fadeIn, enterLeft, enterRight, enterFromTop;
    ImageView view_top;
    TextView view_caption_head;
    TextView view_caption_1;
    EditText txtParkingName;
    EditText txtLandmarks;
    EditText txtNoOfSlots;
    EditText txtSpecialNotes;
    EditText txtSelectLocation;
    TextView select_images;
    ImageView imgParking;
    TextView txt_temp_space;
    Switch switchTemp;
    TextView view_what_is_temp_parking;
    Button btnAddParking;
    CFAlertDialog.Builder popUpDialog;
    ScrollView scrollView;
    private Uri mImageUri;
    private File mImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_public_parkings);

        scrollView = findViewById(R.id.scrollView);

        firebaseES = new FirebaseES(this);
        vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_enter);
        enterLeft = AnimationUtils.loadAnimation(this, R.anim.slide_left_enter);
        enterRight = AnimationUtils.loadAnimation(this, R.anim.swipe_right_enter);
        enterFromTop = AnimationUtils.loadAnimation(this, R.anim.slide_down_enter);

        view_top = findViewById(R.id.view_top);
        view_caption_head = findViewById(R.id.view_caption_head);
        view_caption_1 = findViewById(R.id.view_caption_1);

        txtParkingName = findViewById(R.id.txtParkingName);
        txtLandmarks = findViewById(R.id.txtLandmarks);
        txtNoOfSlots = findViewById(R.id.txtNoOfSlots);
        txtSpecialNotes = findViewById(R.id.txtSpecialNotes);
        txtSelectLocation = findViewById(R.id.txtSelectLocation);
        select_images = findViewById(R.id.select_images);
        imgParking = findViewById(R.id.imgParking);
        txt_temp_space = findViewById(R.id.txt_temp_space);
        switchTemp = findViewById(R.id.switchTemp);
        view_what_is_temp_parking = findViewById(R.id.view_what_is_temp_parking);
        btnAddParking = findViewById(R.id.btnAddParking);

        view_top.startAnimation(enterFromTop);
        view_caption_head.startAnimation(enterRight);
        view_caption_1.startAnimation(enterLeft);

        txtParkingName.startAnimation(enterRight);
        txtLandmarks.startAnimation(enterLeft);
        txtNoOfSlots.startAnimation(enterRight);
        txtSpecialNotes.startAnimation(enterLeft);
        txtSelectLocation.startAnimation(enterRight);
        select_images.startAnimation(enterLeft);
        imgParking.startAnimation(fadeIn);

        switchTemp.startAnimation(enterRight);
        switchTemp.startAnimation(fadeIn);
        view_what_is_temp_parking.startAnimation(fadeIn);

        btnAddParking.startAnimation(fadeIn);

        popUpDialog = new CFAlertDialog.Builder(this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setTitle("Temporary Parking")
                .setMessage("The term Temporary Parking Space indicates parking spaces which can be used to park the vehicles only for a short amount of periods.\nUsually the Temporary Parking Spaces cen be found near to main roads.\nAs they are close to the main roads it might be unsafe to park.")
                .setTextGravity(Gravity.CENTER_HORIZONTAL);

        popUpDialog.setHeaderView(R.layout.popup_dialog_header_view);

        txtSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(activity_add_public_parkings.this, activity_location_picker.class), 999);
            }
        });

        imgParking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        view_what_is_temp_parking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpDialog.show();
            }
        });

        btnAddParking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shakeEditText = AnimationUtils.loadAnimation(activity_add_public_parkings.this, R.anim.anim_shake_edit_text);

                if (!Validator.validatePersonName(txtParkingName.getText().toString())) {
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                    txtParkingName.startAnimation(shakeEditText);
                    vibrate.vibrate(30);
                    showAlertDialog("Enter a valid parking name between 3-50 characters").show();
                    return;
                }
                if (txtLandmarks.getText().length() < 5) {
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                    txtLandmarks.startAnimation(shakeEditText);
                    vibrate.vibrate(30);
                    showAlertDialog("Please enter a valid landmark").show();
                    return;
                }
                if (txtNoOfSlots.getText().length() < 1) {
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                    txtNoOfSlots.startAnimation(shakeEditText);
                    vibrate.vibrate(30);
                    showAlertDialog("Please enter a valid number of slots").show();
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

            firebaseES.addPublicParking(this, txtParkingName.getText().toString(),
                    txtLandmarks.getText().toString(),
                    txtNoOfSlots.getText().toString(),
                    txtSpecialNotes.getText().toString(),
                    pickedLat,
                    pickedLon,
                    data,
                    switchTemp.isChecked());
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
