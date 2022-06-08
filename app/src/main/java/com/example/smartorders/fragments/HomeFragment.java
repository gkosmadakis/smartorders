package com.example.smartorders.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.smartorders.R;
import com.example.smartorders.activities.RestaurantActivity;
import com.example.smartorders.viewmodels.HomeViewModel;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class HomeFragment extends Fragment  {

    private final int[] sampleImages = {R.drawable.carousel1, R.drawable.carousel2, R.drawable.carousel3, R.drawable.carousel4, R.drawable.carousel5};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel.getText().observe(getViewLifecycleOwner(), s -> {
        });
        CardView cardView = root.findViewById(R.id.cardView);
        cardView.setOnClickListener(view -> {
            Intent i = new Intent(getContext(), RestaurantActivity.class);
            startActivityForResult(i, 1);
        });
        CarouselView carouselView = root.findViewById(R.id.carouselView);
        carouselView.setPageCount(sampleImages.length);
        carouselView.setImageListener(imageListener);
        return root;
    }//end of onCreateView

    ImageListener imageListener = (position, imageView) -> imageView.setImageResource(sampleImages[position]);

    @Override
    public void onPause() {
        super.onPause();
    }


}
