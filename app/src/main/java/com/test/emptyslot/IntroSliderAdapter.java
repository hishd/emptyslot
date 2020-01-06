package com.test.emptyslot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.airbnb.lottie.LottieAnimationView;

public class IntroSliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    final String[] lottieAnimations = {"intro_nearby_spots.json","intro_book_slots.json","intro_add_parking_spaces.json","intro_contribute.json","intro_location.json"};

    final String[] captions = {"Find Parking Spots","Book Parking Spots","Add your Parking Spaces","Contribute to Public","Provide Location Access"};

    final String[] contents = {
            "Find nearby parking spaces near your destination, with a single tap. All you need to do is to enter your destination and search for a suitable parking space.",
            "Search for Booking enabled parking spaces near your destination and simply place a booking by inserting your enter time and duration that you are willing to stay.",
            "If you have available spaces and if you are willing to transform them as parking spaces you can add the information to our Empty SLOT Platform and we will get to you as soon as we can.",
            "If you find any public parking spaces that enables drivers to park their rides freely, you can add the parking location information to our Empty SLOT Platform, and they will be visible to the public.",
            "Inorder to navigate you to your destination the app will require your GPS location, please provide the location access by clicking ALLOW on the next screen"};

    public IntroSliderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return captions.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater != null ? layoutInflater.inflate(R.layout.layout_intro_slider, container, false) : null;;

        LottieAnimationView animationViewIntro = (LottieAnimationView) view.findViewById(R.id.animationViewIntro);
        TextView txtCaption = (TextView) view.findViewById(R.id.txtCaption);
        TextView txtContent = (TextView) view.findViewById(R.id.txtContent);

        animationViewIntro.enableMergePathsForKitKatAndAbove(true);
        animationViewIntro.setAnimation(lottieAnimations[position]);
        animationViewIntro.playAnimation();
        txtCaption.setText(captions[position]);
        txtContent.setText(contents[position]);

        container.addView(view);

        return view;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
