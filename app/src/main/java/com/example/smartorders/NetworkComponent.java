package com.example.smartorders;

import com.example.smartorders.activity.RestaurantsListActivity;
import com.example.smartorders.activity.ui.home.HomeFragment;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = NetworkModule.class)
@Singleton
public interface NetworkComponent {

    void inject(HomeFragment homeFragment);
    void inject(RestaurantsListActivity restaurantsListActivity);

    //void inject(RestaurantListModel restaurantListModel);
    //void inject(RestaurantDetailsModel restaurantDetailsModel);
    //void inject(ReviewModel reviewModel);
    //void inject(SearchModel searchModel);

}
