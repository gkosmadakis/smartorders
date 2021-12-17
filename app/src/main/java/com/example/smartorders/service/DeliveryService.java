package com.example.smartorders.service;

import android.content.Context;
import android.widget.ListView;

public interface DeliveryService {

    void saveDeliveryDetailsToFirebase(Context context, String savedPlace, String fullAddress, ListView addressResultsListView, String deliveryOptionSelected, String savedPlaceSelected, String city, String latitude, String longtitude);
}
