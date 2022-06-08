package com.example.smartorders.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.ListView;

import com.example.smartorders.repository.StripeRepository;
import com.example.smartorders.repository.StripeRepositoryImpl;
import com.stripe.android.model.Card;

public class StripeServiceImpl implements StripeService {
    private final StripeRepository stripeRepository = new StripeRepositoryImpl();

    @Override
    public void createStripeToken(Context context, Card card) {
        stripeRepository.createStripeToken(context, card);
    }

    @Override
    public void getStripeTokens(Context context, Intent data) {
        stripeRepository.getStripeTokens(context, data);
    }

    @Override
    public void getSourcesFromFirestore(Context context, ProgressDialog mProgressDialog, ListView listView) {
        stripeRepository.getSourcesFromFirestore(context, mProgressDialog, listView);
    }
}
