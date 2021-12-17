package com.example.smartorders.service;

import android.app.Activity;
import android.content.Context;

public interface PaymentService {

    void processPayment(String totalPriceReceived, Context context, String deliveryOrPickup);
    void getLastChargeFromFirestore(Context context, String totalPriceReceived, String deliveryOrPickup);
    int storeOrderToFirebaseDB(Context context, String totalPriceReceived, String deliveryOrPickup);
    void processPaypalPayment(String amount, Activity addPaypalActivity, int PAYPAL_REQUEST_CODE);
}
