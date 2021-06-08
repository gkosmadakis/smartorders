package com.example.smartorders.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartorders.models.MyApplication;
import com.example.smartorders.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class OrderStatusActivity extends AppCompatActivity {

    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        Intent intent = getIntent();
        TextView orderStatus = findViewById(R.id.orderStatus);
        if(intent.getStringExtra("Activity").equals("CheckoutActivity")) {
            orderId = intent.getStringExtra("orderId");
        }
        if(intent.getStringExtra("Activity").equals("AddPaypalActivity")) {
            try {
                JSONObject jsonObject = new JSONObject(Objects.requireNonNull(intent.getStringExtra("PaymentDetails")));
                orderId = jsonObject.getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(orderId != "") {
            orderStatus.setText("Thank you! Your order with number " + orderId + " was sent to Greek Artisan Pastries. Click the button to continue browsing.");
            /*Clear the basket so that it doesn't show the items anymore */
            MyApplication app = (MyApplication) getApplicationContext();
            app.clearBasket();
        }
        /*there was an error placing the order */
        else {
            orderStatus.setText("There was an error placing your order. Please try again.");
        }

        Button homeBtn = findViewById(R.id.homeButton);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
    }

}
