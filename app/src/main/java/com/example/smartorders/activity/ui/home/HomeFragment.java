package com.example.smartorders.activity.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.smartorders.CategoriesData;
import com.example.smartorders.BuildConfig;
import com.example.smartorders.DaggerNetworkComponent;
import com.example.smartorders.DataAdapter;
import com.example.smartorders.NetworkModule;
import com.example.smartorders.R;
import com.example.smartorders.ZomatoServiceApi;
import com.example.smartorders.activity.RestaurantActivity;
import com.example.smartorders.activity.RestaurantsListActivity;
import com.example.smartorders.model.categories.CategoriesItem;
import com.example.smartorders.model.categories.CategoryResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends Fragment implements DataAdapter.OnNoteListener {

    private HomeViewModel homeViewModel;
    private FusedLocationProviderClient locationProviderClient;
    private Location location;
    private String localLongtitude;
    private String localLatitude;
    private List<CategoriesItem> retrievedList;
    @Inject
    ZomatoServiceApi zomatoServiceApi;
    private DisposableObserver disposableObserver;
    private CategoryResponse categoryResponse;
    private final String categoryImageUrls[] = {
            "http://api.learn2crack.com/android/images/donut.png",
            "http://api.learn2crack.com/android/images/eclair.png",
            "http://api.learn2crack.com/android/images/froyo.png",
            "http://api.learn2crack.com/android/images/ginger.png",
            "http://api.learn2crack.com/android/images/honey.png",
            "http://api.learn2crack.com/android/images/icecream.png",
            "http://api.learn2crack.com/android/images/jellybean.png",
            "http://api.learn2crack.com/android/images/kitkat.png",
            "http://api.learn2crack.com/android/images/lollipop.png",
            "http://api.learn2crack.com/android/images/marshmallow.png",
            "http://api.learn2crack.com/android/images/donut.png",
            "http://api.learn2crack.com/android/images/eclair.png",
            "http://api.learn2crack.com/android/images/froyo.png"
    };
    private RecyclerView recyclerView;
    private CardView cardView;
    CarouselView carouselView;
    int[] sampleImages = {R.drawable.carousel1, R.drawable.carousel2, R.drawable.carousel3, R.drawable.carousel4, R.drawable.carousel5};


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            //    textView.setText(s);
            }
        });

        //recyclerView = root.findViewById(R.id.categoryListView);
        //recyclerView.setHasFixedSize(true);
        //RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        //recyclerView.setLayoutManager(layoutManager);

                /*DaggerNetworkComponent.builder()
                        .networkModule(new NetworkModule(BuildConfig.ZOMATO_BASE_URL))
                        .build()
                        .inject(this);*/

        //requestLocationAccess();
        cardView = root.findViewById(R.id.cardView);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), RestaurantActivity.class);
                //i.putExtra("longtitude",localLongtitude);
                //i.putExtra("latitude",localLatitude);
                //i.putExtra("category","Delivery");
                startActivityForResult(i, 1);
            }
        });

        carouselView = root.findViewById(R.id.carouselView);
        carouselView.setPageCount(sampleImages.length);
        carouselView.setImageListener(imageListener);

        return root;
    }//end of onCreateView

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };

    private ArrayList<CategoriesData> prepareData(){

        ArrayList<CategoriesData> categories = new ArrayList<>();
        for(int i=0; i < retrievedList.size(); i++){
            CategoriesData categoriesData = new CategoriesData();
            categoriesData.setCategoryName(retrievedList.get(i).getCategories().getName());
            categoriesData.setCategoryImage(categoryImageUrls[i]);
            categories.add(categoriesData);
        }
        return categories;
    }


    private void fetchLocationInformation() {

        //Guard check
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        //Listen for the last location from the provider
        locationProviderClient.getLastLocation()
                .addOnSuccessListener(loc -> {

                    //We did not get the last location, lets get it
                    if(null == loc) {

                        LocationRequest locationRequest = LocationRequest.create()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000);

                        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                        Log.i("OnSuccessListener of ","fetchLocationInformation "+loc);


                    } else {
                        Log.i("OnSuccessListener of ","fetchLocationInformation "+loc);
                        location = loc;
                        localLatitude = String.valueOf(loc.getLatitude());
                        localLongtitude = String.valueOf(loc.getLongitude());
                        //fetchDataFromZomatoToProceed();
                    }
                })
                .addOnFailureListener(e -> {
                    location = null;
                    //fetchDataFromZomatoToProceed();
                    Log.i("OnFailureListener of ","fetchLocationInformation");
                });
    }

    private LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            location = locationResult.getLastLocation();
            locationProviderClient.removeLocationUpdates(locationCallback);
            //fetchDataFromZomatoToProceed();
        }
    };

    private void fetchDataFromZomatoToProceed() {
        /*Call Zomato, method getCategories and get categories*/
        disposableObserver = zomatoServiceApi.getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<CategoryResponse>() {
                    @Override
                    public void onNext(CategoryResponse catRes) {
                        categoryResponse = catRes;
                        System.out.println(categoryResponse);
                        Log.i("Category received: "+"Next", String.valueOf(categoryResponse.getCategories().size()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        //finish();
                        Log.i("OnErrorListener of ","fetchDataFromZomatoToProceed");
                    }

                    @Override
                    public void onComplete() {
                        //launchNextScreen();
                        Log.i("OnCompleteListener of ","fetchDataFromZomatoToProceed");
                        retrievedList = categoryResponse.getCategories();
                        for (CategoriesItem item : retrievedList) {
                            Log.i("Categories ", item.getCategories().getName());
                        }
                        ArrayList<CategoriesData> categoriesData = prepareData();
                        //DataAdapter adapter = new DataAdapter(getContext(), categoriesData,HomeFragment.this::onNoteClick);
                        //recyclerView.setAdapter(adapter);
                    }
                });
    }

    private void requestLocationAccess() {

        //If the permission has been provided
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            //fetchLocationInformation();
            return;
        }

        //Request the permission from the user
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1001);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != disposableObserver && !disposableObserver.isDisposed()) {
            disposableObserver.dispose();
        }

        if(null != locationProviderClient) {
            locationProviderClient.removeLocationUpdates(locationCallback);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case 1001:
                if (PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                    //fetchLocationInformation();
                } else {
                    location = null;
                    //fetchDataFromZomatoToProceed();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onNoteClick(int position) {
        Toast.makeText(getContext(), "Item Clicked "+position, Toast.LENGTH_SHORT).show();
        switch (position){

            case 0:
                //Intent i = new Intent(getContext(), RestaurantsListActivity.class);
                //i.putExtra("longtitude",localLongtitude);
                //i.putExtra("latitude",localLatitude);
                //i.putExtra("category",retrievedList.get(position).getCategories().getName());
                //startActivity(i);
                break;
            case 1:
                Toast.makeText(getContext(), "Not yet implemented ", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(getContext(), "Not yet implemented ", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(getContext(), "Not yet implemented ", Toast.LENGTH_SHORT).show();
                break;
        }
    }


}
