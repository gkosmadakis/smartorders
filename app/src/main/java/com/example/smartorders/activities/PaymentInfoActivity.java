package com.example.smartorders.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartorders.R;
import com.example.smartorders.service.StripeService;
import com.example.smartorders.service.StripeServiceImpl;

public class PaymentInfoActivity extends AppCompatActivity {
    private ListView listView;
    private ProgressDialog mProgressDialog;
    private final StripeService stripeService = new StripeServiceImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_info);
        TextView addPaymentMethod = findViewById(R.id.addPaymentMethod);
        listView = findViewById(R.id.listView);
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
        stripeService.getSourcesFromFirestore(PaymentInfoActivity.this, mProgressDialog, listView);
        addPaymentMethod.setOnClickListener(view -> {
            Intent paymentIntent = new Intent(getApplicationContext(), AddPaymentMethodActivity.class);
            startActivity(paymentIntent);
        });
    }

}
