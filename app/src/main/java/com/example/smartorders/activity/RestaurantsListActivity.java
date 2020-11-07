package com.example.smartorders.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.smartorders.BuildConfig;
import com.example.smartorders.CategoriesData;
import com.example.smartorders.DaggerNetworkComponent;
import com.example.smartorders.DataAdapter;
import com.example.smartorders.NetworkModule;
import com.example.smartorders.R;
import com.example.smartorders.ZomatoServiceApi;
import com.example.smartorders.model.search.RestaurantsItem;
import com.example.smartorders.model.search.SearchResponse;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class RestaurantsListActivity extends AppCompatActivity implements DataAdapter.OnNoteListener{

    @Inject
    ZomatoServiceApi zomatoServiceApi;
    private DisposableObserver disposableObserver;
    private RecyclerView recyclerView;
    private String category;
    private String localLongtitude;
    private String localLatitude;
    private SearchResponse searchResponse;
    private List<RestaurantsItem> retrievedList;

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
            "http://api.learn2crack.com/android/images/froyo.png",
            "http://api.learn2crack.com/android/images/ginger.png",
            "http://api.learn2crack.com/android/images/honey.png",
            "http://api.learn2crack.com/android/images/icecream.png",
            "http://api.learn2crack.com/android/images/jellybean.png",
            "http://api.learn2crack.com/android/images/kitkat.png",
            "http://api.learn2crack.com/android/images/lollipop.png",
            "http://api.learn2crack.com/android/images/marshmallow.png"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants_list);

        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        localLatitude = intent.getStringExtra("latitude");
        localLongtitude = intent.getStringExtra("longtitude");


        recyclerView = findViewById(R.id.restaurantListView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);

        DaggerNetworkComponent.builder()
                .networkModule(new NetworkModule(BuildConfig.ZOMATO_BASE_URL))
                .build()
                .inject(this);

        //fetchRestaurantSearchDataFromZomato();
    }

    private void fetchRestaurantSearchDataFromZomato() {
        /*Call Zomato, method getCategorySearch and get restaurants*/
        disposableObserver = zomatoServiceApi.getCategorySearch(localLatitude,localLongtitude,category)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<SearchResponse>() {
                    @Override
                    public void onNext(SearchResponse searchRes) {
                        searchResponse = searchRes;
                        System.out.println(searchResponse);
                        Log.i("Results received: ", String.valueOf(searchResponse.getRestaurants()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        //finish();
                        Log.i("OnErrorListener of ","fetchRestaurantSearchDataFromZomato");
                    }

                    @Override
                    public void onComplete() {
                        Log.i("OnCompleteListener of ","fetchRestaurantSearchDataFromZomato");
                        retrievedList = searchResponse.getRestaurants();
                        for (RestaurantsItem item : retrievedList) {
                            Log.i("Restaurant ", item.getRestaurant().getName());
                        }
                        ArrayList<CategoriesData> categoriesData = prepareData();
                        DataAdapter adapter = new DataAdapter(getApplicationContext(), categoriesData, RestaurantsListActivity.this);
                        recyclerView.setAdapter(adapter);
                    }
                });

    }

    @Override
    public void onNoteClick(int position) {

        Toast.makeText(this, "Item Clicked "+position, Toast.LENGTH_SHORT).show();
        switch (position){

            case 0:
                Intent i = new Intent(this, RestaurantActivity.class);
                //i.putExtra("longtitude",localLongtitude);
                //i.putExtra("latitude",localLatitude);
                //i.putExtra("category","Delivery");
                startActivity(i);
                break;
            case 1:
                Toast.makeText(this, "Not yet implemented ", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(this, "Not yet implemented ", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(this, "Not yet implemented ", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private ArrayList<CategoriesData> prepareData(){

        ArrayList<CategoriesData> restaurants = new ArrayList<>();
        System.out.println(retrievedList.size());
        for(int i=0; i < retrievedList.size(); i++){
            CategoriesData categoriesData = new CategoriesData();
            categoriesData.setCategoryName(retrievedList.get(i).getRestaurant().getName());
            categoriesData.setCategoryImage(categoryImageUrls[i]);
            restaurants.add(categoriesData);
        }
        return restaurants;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != disposableObserver && !disposableObserver.isDisposed()) {
            disposableObserver.dispose();
        }
    }

}
