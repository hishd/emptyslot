package com.test.emptyslot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.LinearLayout;

public class activity_app_intro extends AppCompatActivity {

    ViewPager viewPagerIntro;
    LinearLayout linearBottom;

    IntroSliderAdapter sliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_intro);

        viewPagerIntro = findViewById(R.id.viewPagerIntro);
        linearBottom = findViewById(R.id.linearBottom);

        sliderAdapter = new IntroSliderAdapter(this);

        viewPagerIntro.setAdapter(sliderAdapter);
    }
}
