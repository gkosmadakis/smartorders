package com.example.smartorders.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartorders.R;
import com.example.smartorders.service.StripeService;
import com.example.smartorders.service.StripeServiceImpl;
import com.stripe.android.model.Card;
import com.stripe.android.view.CardInputWidget;

public class AddCardActivity extends AppCompatActivity {
    private StripeService stripeService = new StripeServiceImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        Button addCardBtn = findViewById(R.id.addCardButton);
        addCardBtn.setOnClickListener((View view) -> {
            // Get the card details from the card widget
            CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
            Card card = cardInputWidget.getCard();
            if (card != null) {
                // Create a Stripe token from the card details
                stripeService.createStripeToken(AddCardActivity.this, card);
            }
        });
    }

}
