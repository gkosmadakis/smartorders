package com.example.smartorders.repository;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.ListView;

import com.stripe.android.model.Card;

public interface StripeRepository {
    void createStripeToken(Context context, Card card);
    void getStripeTokens(Context context, Intent data);
    void getSourcesFromFirestore(Context context, ProgressDialog mProgressDialog, ListView listView);
}
