package com.example.smartorders.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartorders.R;
import com.example.smartorders.config.Config;
import com.example.smartorders.service.PaymentService;
import com.example.smartorders.service.PaymentServiceImpl;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

public class AddPaypalActivity extends AppCompatActivity {
    private static final int PAYPAL_REQUEST_CODE = 7777;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);
    private String deliveryOrPickup;
    private Button btnPayNow;
    private EditText amountToPay;
    private String amount = "";
    private PaymentService paymentService = new PaymentServiceImpl();

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
        btnPayNow.setOnClickListener(view ->
                paymentService.processPaypalPayment(amountToPay.getText().toString(), this, PAYPAL_REQUEST_CODE));
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
                        paymentService.storeOrderToFirebaseDB(AddPaypalActivity.this, amountToPay.getText().toString(), deliveryOrPickup);
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

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

}

