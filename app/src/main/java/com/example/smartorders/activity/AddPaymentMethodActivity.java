package com.example.smartorders.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.smartorders.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Token;

public class AddPaymentMethodActivity extends AppCompatActivity {
    private final int addCardSuccessfully = 1;
    private final int addCardSuccessfullyFromPayment = 2;
    private static final String TAG = "AddPaymentMethodActivity ";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment_method);

        mAuth = FirebaseAuth.getInstance();

        TextView cardView = findViewById(R.id.cardView);
        TextView paypalView = findViewById(R.id.paypalView);
        getSupportActionBar().setTitle("Add Card");
        String totalPrice = "";
        String deliveryOrPickup = "";
        if(getIntent().getStringExtra("totalPrice")!= null && getIntent().getStringExtra("deliveryOrPickup")!=null) {
            totalPrice = getIntent().getStringExtra("totalPrice");
            deliveryOrPickup = getIntent().getStringExtra("deliveryOrPickup");
        }

        else{
            paypalView.setVisibility(View.GONE);

        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddCardActivity.class);
                startActivityForResult(intent, addCardSuccessfullyFromPayment);
            }
        });

        String finalDeliveryOrPickup = deliveryOrPickup;
        String finalTotalPrice = totalPrice;
        paypalView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), AddPaypalActivity.class);
                intent.putExtra("totalPrice", finalTotalPrice);
                intent.putExtra("deliveryOrPickup", finalDeliveryOrPickup);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == addCardSuccessfully) {
            if (data.hasExtra("token")) {
                setResult(RESULT_OK, data);
                finish();
            }
        }

        else if (resultCode == RESULT_OK && requestCode == addCardSuccessfullyFromPayment){
            if (data.hasExtra("token")) {
                FirebaseFirestore.getInstance().collection("stripe_customers").document(mAuth.getUid()).collection("tokens")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        //getCard4LastDigitsFromTokens((String) document.getData().get("token"));
                                        /*if the token brought from the AddCard Activity is the same as the one found in
                                         * Firestore then that means either is the only token or if the user has already added
                                         * cards find the one added last time and save it to shared preferences*/
                                        if(data.getStringExtra("token").equals(document.getData().get("token"))){
                                            saveToPrefsLastCardAdded(data.getStringExtra("token"));
                                            finish();
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        }
    }

    private void saveToPrefsLastCardAdded(String token) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Stripe.apiKey = "sk_test_50wnmiroW2ia9FhnXBPj2slO00E1JGcvFg";
                    String last4Digits = Token.retrieve(token).getCard().getLast4();
                    SharedPreferences prefers = getSharedPreferences("payments", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefers.edit();
                    editor.putString("lastPaymentMethod", last4Digits);
                    editor.apply();


                } catch (StripeException ex) {
                    ex.printStackTrace();
                }
            }
        };
        thread.start();
    }

}
