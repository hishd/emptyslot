package com.test.emptyslot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import spencerstudios.com.bungeelib.Bungee;

public class activity_app_intro extends AppCompatActivity {

    ViewPager viewPagerIntro;
    LinearLayout linearDots;
    Animation shrinkEnter;
    Animation animateCaption;

    IntroSliderAdapter sliderAdapter;

    private TextView[] mDots;

    Button btnPrev;
    Button btnNextFinish;

    private int mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_intro);

        viewPagerIntro = findViewById(R.id.viewPagerIntro);
        linearDots = findViewById(R.id.linearDots);
        btnPrev = findViewById(R.id.btnPrev);
        btnPrev.setVisibility(View.INVISIBLE);
        btnNextFinish = findViewById(R.id.btnNextFinish);

        shrinkEnter = AnimationUtils.loadAnimation(this,R.anim.shrink_enter);
        animateCaption = AnimationUtils.loadAnimation(this,R.anim.zoom_enter);

        btnNextFinish.startAnimation(shrinkEnter);

        sliderAdapter = new IntroSliderAdapter(this);

        viewPagerIntro.setAdapter(sliderAdapter);

        viewPagerIntro.startAnimation(animateCaption);

        addDotIndicators(0);

        viewPagerIntro.addOnPageChangeListener(viewListner);

        btnNextFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(btnNextFinish.getText().equals("Finish")){
                    checkAndRequestPermissions();
                    return;
                }

                viewPagerIntro.setCurrentItem(mCurrentPage+1);
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPagerIntro.setCurrentItem(mCurrentPage-1);
            }
        });
    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(activity_app_intro.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity_app_intro.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(activity_app_intro.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                ActivityCompat.requestPermissions(activity_app_intro.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }else{
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            startNext();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1: {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(activity_app_intro.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        startNext();
                    }
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    void addDotIndicators(int position){
        mDots = new TextView[5];
        linearDots.removeAllViews();

        for(int i=0; i<mDots.length; i++){
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.dotColor));

            linearDots.addView(mDots[i]);
        }

        if(mDots.length>0){
            mDots[position].setTextSize(50);
            mDots[position].setTextColor(getResources().getColor(R.color.dotColorLarge));
        }
    }

    void startNext(){
        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(activity_app_intro.this,activity_login.class));
        Bungee.zoom(this);
        finish();
    }

    ViewPager.OnPageChangeListener viewListner = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotIndicators(position);
            mCurrentPage = position;

            if(position==0){
                btnNextFinish.setEnabled(true);
                btnPrev.setEnabled(false);
                btnPrev.setVisibility(View.INVISIBLE);

                btnNextFinish.setText("Next");
                btnPrev.setText("");
            } else if(position==mDots.length-1){
                btnNextFinish.setEnabled(true);
                btnPrev.setEnabled(true);
                btnPrev.setVisibility(View.VISIBLE);

                btnNextFinish.setText("Finish");
                btnPrev.setText("Previous");
            } else{
                btnNextFinish.setEnabled(true);
                btnPrev.setEnabled(true);
                btnPrev.setVisibility(View.VISIBLE);

                btnNextFinish.setText("Next");
                btnPrev.setText("Previous");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
