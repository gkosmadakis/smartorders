package com.example.smartorders.service;

import android.app.Activity;
import android.content.Context;

import com.example.smartorders.repository.PaymentRepository;
import com.example.smartorders.repository.PaymentRepositoryImpl;

public class PaymentServiceImpl implements PaymentService{
    private final PaymentRepository paymentRepository = new PaymentRepositoryImpl();

    @Override
    public void processPayment(String totalPriceReceived, Context context, String deliveryOrPickup) {
        paymentRepository.processPayment(totalPriceReceived, context, deliveryOrPickup);
    }

    @Override
    public void getLastChargeFromFirestore(Context context, String totalPriceReceived, String deliveryOrPickup) {
        paymentRepository.getLastChargeFromFirestore(context, totalPriceReceived, deliveryOrPickup);
    }

    @Override
    public int storeOrderToFirebaseDB(Context context, String totalPriceReceived, String deliveryOrPickup) {
        return paymentRepository.storeOrderToFirebaseDB(context, totalPriceReceived, deliveryOrPickup);
    }

    @Override
    public void processPaypalPayment(String amount, Activity addPaypalActivity, int PAYPAL_REQUEST_CODE) {
         paymentRepository.processPaypalPayment(amount, addPaypalActivity, PAYPAL_REQUEST_CODE);
    }
}
