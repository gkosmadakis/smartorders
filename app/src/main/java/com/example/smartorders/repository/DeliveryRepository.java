package com.example.smartorders.repository;

import android.content.Context;
import android.widget.ListView;

public interface DeliveryRepository {

    void saveDeliveryDetailsToFirebase(Context context, String savedPlace, String fullAddress, ListView addressResultsListView, String deliveryOptionSelected, String savedPlaceSelected, String city, String latitude, String longtitude);

}
