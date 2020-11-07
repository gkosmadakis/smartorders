package com.example.smartorders.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smartorders.MyApplication;
import com.example.smartorders.R;
import com.example.smartorders.config.Config;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;

public class AddPaypalActivity extends AppCompatActivity {
    private static final int PAYPAL_REQUEST_CODE = 7777;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);
    private String deliveryOrPickup;

    Button btnPayNow;
    EditText amountToPay;

    String amount = "";

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_paypal);
        String totalPrice = getIntent().getStringExtra("totalPrice");
        deliveryOrPickup = getIntent().getStringExtra("deliveryOrPickup");

        config.acceptCreditCards(false);//this will disable your card option
        //start paypal service
        Intent intent = new Intent(this,PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);

        btnPayNow = findViewById(R.id.btnPayNow);
        amountToPay = findViewById(R.id.edtAmount);
        amountToPay.setText(totalPrice);

        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processPayment();
            }
        });
    }

    private void processPayment() {
        amount = amountToPay.getText().toString();
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amount)),"GBP",
                "Purchase Goods",PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
        startActivityForResult(intent,PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        /*Store the order to Firebase Database */
                        storeOrderToFirebaseDB();
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        startActivity(new Intent(this, OrderStatusActivity.class)
                                .putExtra("Activity", "AddPaypalActivity")
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", amount));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
    }

    private int storeOrderToFirebaseDB() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference current_user_db_orders = mDatabase.child(mAuth.getUid()).child("Orders");
        /*Generate a random number from 1000-10000 as order ID */
        final int random = new Random().nextInt(9001) + 1000; // [1000, 10000] + 1000 => [1000, 10000]

        current_user_db_orders.child("Order ID "+ random).child("TotalPrice").setValue(amountToPay.getText().toString());

        MyApplication app = (MyApplication) getApplicationContext();
        Map<String, Map<String,Double>> quantityNamePriceMap = app.getQuantityNamePriceMap();

        int counter = 1;
        for(Map.Entry<String, Map<String,Double>> entry : quantityNamePriceMap.entrySet()){
            current_user_db_orders.child("Order ID "+ random).child("Items").child(String.valueOf(counter)).child("quantity").setValue(entry.getKey());
            for(Map.Entry<String,Double> secondMap : entry.getValue().entrySet()) {
                current_user_db_orders.child("Order ID "+ random).child("Items").child(String.valueOf(counter)).child("name").setValue(secondMap.getKey());
                current_user_db_orders.child("Order ID "+ random).child("Items").child(String.valueOf(counter)).child("price").setValue(secondMap.getValue());
            }
            counter++;
        }
        SharedPreferences sharedPrefs = getSharedPreferences("home", Context.MODE_PRIVATE);
        current_user_db_orders.child("Order ID "+ random).child("deliveryOption").setValue(sharedPrefs.getString("deliveryOption",""));
        current_user_db_orders.child("Order ID "+ random).child("deliveryOrPickup").setValue(deliveryOrPickup);
        current_user_db_orders.child("Order ID "+ random).child("deliveryAddress").setValue(sharedPrefs.getString("fullAddressDelivery",""));

        Map<String,String> instructionToFoodNameMap = app.getInstructionToFoodNameMap();
        for(Map.Entry<String,String> entry : instructionToFoodNameMap.entrySet()){
            current_user_db_orders.child("Order ID "+ random).child("Items").child("foodInstruction").child(entry.getKey()).setValue(entry.getValue());
        }

        return random;
    }
}

