package com.example.smartorders.repository;

import android.app.Activity;
import android.content.Context;

import com.example.smartorders.interfaces.ProcessPaymentCallback;

public interface PaymentRepository {
    boolean processPayment(String totalPriceReceived, Context context, String deliveryOrPickup, final ProcessPaymentCallback callback);
    void getLastChargeFromFirestore(Context context, String totalPriceReceived, String deliveryOrPickup);
    int storeOrderToFirebaseDB(Context context, String totalPriceReceived, String deliveryOrPickup);
}
