package com.example.smartorders.repository;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.smartorders.activities.AddCardActivity;
import com.example.smartorders.activities.AddPaymentMethodActivity;
import com.example.smartorders.adapters.PaymentInfoAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class StripeRepositoryImpl implements StripeRepository {
    private final static String TAG = "StripeRepositoryImpl ";
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static final int RESULT_OK = -1;

    @Override
    public void createStripeToken(Context context, Card card) {
        Stripe stripe = new Stripe(context, PaymentConfiguration.getInstance(context).getPublishableKey());
        stripe.createToken(card, new ApiResultCallback<Token>() {
            @Override
            public void onSuccess(@NonNull Token token) {
                // Send the token identifier to the server...
                DocumentReference tokenRef;
                Map<String, Object> tokenDetails = new HashMap<>();
                tokenDetails.put("token",token.getId());
                tokenRef = FirebaseFirestore.getInstance().collection("stripe_customers").document(mAuth.getUid()).collection("tokens").document();
                tokenRef.set(tokenDetails).addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Token successfully added to database", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "Firestore tokens collection created, calling Firebase Function addPaymentSource");
                    Intent data = new Intent();
                    data.putExtra("token",token.getId());
                    // Activity finished ok, return the data
                    ((AddCardActivity)context).setResult(RESULT_OK, data);
                    ((AddCardActivity)context).finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, e.getLocalizedMessage());
                });
            }
            @Override
            public void onError(@NonNull Exception e) {
                // Handle error
                Log.e(TAG,"Calling stripeCreateToken encountered an error "+e.getLocalizedMessage());
            }
        });
    }

    @Override
    public void getStripeTokens(Context context, Intent data) {
        FirebaseFirestore.getInstance().collection("stripe_customers").document(mAuth.getUid()).collection("tokens")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            /*if the token brought from the AddCard Activity is the same as the one found in
                             * Firestore then that means either is the only token or if the user has already added
                             * cards find the one added last time and save it to shared preferences*/
                            if(data.getStringExtra("token").equals(document.getData().get("token"))){
                                ((AddPaymentMethodActivity)context).setResult(RESULT_OK, data);
                                ((AddPaymentMethodActivity)context).finish();
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    @Override
    public void getSourcesFromFirestore(Context context, ProgressDialog mProgressDialog, ListView listView) {
        AtomicReference<LinkedHashMap<String, String>> last4DigitsToBrandMap = new AtomicReference<>(new LinkedHashMap<>());
        FirebaseFirestore.getInstance().collection("stripe_customers").
                document(mAuth.getUid()).collection("sources")
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && Objects.requireNonNull(task.getResult()).getDocuments().size() > 0) {
                Toast.makeText(context, "Successfully retrieved sources", Toast.LENGTH_LONG).show();
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    last4DigitsToBrandMap.set(getLast4DigitsAndBrand(document.getData(), last4DigitsToBrandMap));
                }
                /*List view with items*/
                final PaymentInfoAdapter adapter = new PaymentInfoAdapter(context, last4DigitsToBrandMap);
                listView.setAdapter(adapter);
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
            else {
                Toast.makeText(context, "Did not find any card", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Error getting documents: ", task.getException());
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
            for(Map.Entry<String,String> cardDetails : last4DigitsToBrandMap.get().entrySet()) {
                Log.i(TAG,cardDetails.getKey()+" "+cardDetails.getValue());
            }
        });
    }

    private LinkedHashMap<String, String> getLast4DigitsAndBrand(Map<String, Object> data, AtomicReference<LinkedHashMap<String, String>> last4DigitsToBrandMap) {
        String brand = "";
        String last4 = "";
        for(Map.Entry<String,Object> cardDetails : data.entrySet()) {
            if(cardDetails.getKey().equals("brand")){
                brand = (String) cardDetails.getValue();
            }
            if(cardDetails.getKey().equals("last4")){
                last4 = (String) cardDetails.getValue();
            }
        }
        last4DigitsToBrandMap.get().put(last4,brand);
        return last4DigitsToBrandMap.get();
    }

}
