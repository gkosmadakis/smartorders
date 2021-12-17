package com.example.smartorders.service;

import android.content.Context;
import android.widget.ListView;

import com.example.smartorders.repository.DeliveryRepository;
import com.example.smartorders.repository.DeliveryRepositoryImpl;

public class DeliveryServiceImpl implements DeliveryService{
    private final DeliveryRepository deliveryRepository = new DeliveryRepositoryImpl();

    @Override
    public void saveDeliveryDetailsToFirebase(Context context, String savedPlace, String fullAddress, ListView addressResultsListView, String deliveryOptionSelected, String savedPlaceSelected, String city, String latitude, String longtitude) {
        deliveryRepository.saveDeliveryDetailsToFirebase(context, savedPlace,fullAddress, addressResultsListView, deliveryOptionSelected, savedPlaceSelected, city, latitude, longtitude);
    }
}
