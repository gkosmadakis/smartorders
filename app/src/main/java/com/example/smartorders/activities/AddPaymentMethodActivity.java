package com.example.smartorders.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartorders.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class AddPaymentMethodActivity extends AppCompatActivity {
    private final int addCardSuccessfully = 1;
    private final int addCardSuccessfullyFromPayment = 2;
    private static final String TAG = "AddPaymntMethodActivity";
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

        cardView.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AddCardActivity.class);
            startActivityForResult(intent, addCardSuccessfullyFromPayment);
        });

        String finalDeliveryOrPickup = deliveryOrPickup;
        String finalTotalPrice = totalPrice;
        paypalView.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AddPaypalActivity.class);
            intent.putExtra("totalPrice", finalTotalPrice);
            intent.putExtra("deliveryOrPickup", finalDeliveryOrPickup);
            startActivity(intent);
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
        /*TODO it's a database call so it should go to a service */
        else if (resultCode == RESULT_OK && requestCode == addCardSuccessfullyFromPayment){
            if (data.hasExtra("token")) {
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
                                        setResult(RESULT_OK, data);
                                        finish();
                                    }
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        });
            }
        }
    }
}
