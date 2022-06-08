package com.example.smartorders.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartorders.R;
import com.example.smartorders.service.StripeService;
import com.example.smartorders.service.StripeServiceImpl;

import java.util.Objects;

public class AddPaymentMethodActivity extends AppCompatActivity {
    private final int addCardSuccessfullyFromPayment = 2;
    private final StripeService stripeService = new StripeServiceImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment_method);
        TextView cardView = findViewById(R.id.cardView);
        TextView paypalView = findViewById(R.id.paypalView);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Add Card");
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
        int addCardSuccessfully = 1;
        if (resultCode == RESULT_OK && requestCode == addCardSuccessfully) {
            if (data.hasExtra("token")) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
        else if (resultCode == RESULT_OK && requestCode == addCardSuccessfullyFromPayment){
            if (data.hasExtra("token")) {
                stripeService.getStripeTokens(AddPaymentMethodActivity.this, data);
            }
        }
    }
}
