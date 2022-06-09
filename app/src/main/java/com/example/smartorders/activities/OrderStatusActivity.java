package com.example.smartorders.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartorders.R;
import com.example.smartorders.models.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class OrderStatusActivity extends AppCompatActivity {

    private String orderId;
    private String error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        Intent intent = getIntent();
        TextView orderStatus = findViewById(R.id.orderStatus);
        if(Objects.requireNonNull(intent.getStringExtra("Activity")).equals("CheckoutActivity")) {
            orderId = intent.getStringExtra("orderId");
            error = intent.getStringExtra("error");
        }
        if(Objects.requireNonNull(intent.getStringExtra("Activity")).equals("AddPaypalActivity")) {
            try {
                JSONObject jsonObject = new JSONObject(Objects.requireNonNull(intent.getStringExtra("PaymentDetails")));
                orderId = jsonObject.getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(orderId != null) {
            orderStatus.setText("Thank you! Your order with number " + orderId + " was sent to Greek Artisan Pastries. Click the button to continue browsing.");
            /*Clear the basket so that it doesn't show the items anymore */
        }
        else if(error != null){
            orderStatus.setText("There was an error on your transaction with message " + error);
        }
        /*there was an error placing the order */
        else {
            orderStatus.setText("There was an error placing your order. Please try again.");
        }
        MyApplication app = (MyApplication) getApplicationContext();
        app.clearBasket();
        Button homeBtn = findViewById(R.id.homeButton);
        homeBtn.setOnClickListener(view -> {
            Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(homeIntent);
        });
    }

}
