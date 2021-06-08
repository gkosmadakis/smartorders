package com.example.smartorders.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.smartorders.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AddCardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private final static String TAG = "AddCardActivity ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        mAuth = FirebaseAuth.getInstance();

        Button addCardBtn = findViewById(R.id.addCardButton);
        addCardBtn.setOnClickListener((View view) -> {
            // Get the card details from the card widget
            CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
            Card card = cardInputWidget.getCard();
            if (card != null) {
                // Create a Stripe token from the card details
                Stripe stripe = new Stripe(getApplicationContext(), PaymentConfiguration.getInstance(getApplicationContext()).getPublishableKey());
                stripe.createToken(card, new ApiResultCallback<Token>() {
                    @Override
                    public void onSuccess(@NonNull Token token) {
                        // Send the token identifier to the server...
                        DocumentReference tokenRef;
                        Map<String, Object> tokenDetails = new HashMap<>();
                        tokenDetails.put("token",token.getId());
                        tokenRef = FirebaseFirestore.getInstance().collection("stripe_customers").document(mAuth.getUid()).collection("tokens").document();
                        tokenRef.set(tokenDetails).addOnSuccessListener(new OnSuccessListener<Void>(){
                            @Override
                            public void onSuccess(Void aVoid){
                                Toast.makeText(getApplicationContext(), "Token successfully added to database", Toast.LENGTH_LONG).show();
                                Log.i(TAG, "Firestore tokens collection created, calling Firebase Function addPaymentSource");
                                Intent data = new Intent();
                                data.putExtra("token",token.getId());
                                // Activity finished ok, return the data
                                setResult(RESULT_OK, data);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener(){
                            @Override
                            public void onFailure(@NonNull Exception e){
                                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                Log.e(TAG, e.getLocalizedMessage());
                            }
                        });
                    }
                    @Override
                    public void onError(@NonNull Exception e) {
                        // Handle error
                        Log.e(TAG,"Calling stripeCreateToken encountered an error "+e.getLocalizedMessage());
                    }
                });
            }
        });
    }


}
