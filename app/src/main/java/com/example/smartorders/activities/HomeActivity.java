package com.example.smartorders.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.smartorders.config.Config;
import com.example.smartorders.models.MyApplication;
import com.example.smartorders.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.config.SettingsConfig;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.UserAction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

public class HomeActivity extends AppCompatActivity {

    private Button viewBasketBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_orders, R.id.navigation_account)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        SharedPreferences prefers = PreferenceManager.getDefaultSharedPreferences(this);
        viewBasketBtn = findViewById(R.id.viewBasketBtnHome);
        viewBasketBtn.setVisibility(View.GONE);
        viewBasketBtn.setOnClickListener(view -> {
            /*Get total price of goods here */
            MyApplication app = (MyApplication) getApplicationContext();
            if(!app.isBasketEmpty()) {
                String totalPrice = app.getPrice();
                Intent intent = new Intent(view.getContext(), CheckoutActivity.class);
                intent.putExtra("totalPrice",totalPrice);
                startActivity(intent);
            }
        });
        CheckoutConfig config = new CheckoutConfig(
                getApplication(),
                Config.PAYPAL_CLIENT_ID,
                Environment.SANDBOX,
                "com.example.smartorders://paypalpay",
                CurrencyCode.GBP,
                UserAction.PAY_NOW,
                new SettingsConfig(
                        true,
                        false
                )
        );
        PayPalCheckout.setConfig(config);
    }// end of onCreate

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onResume() {
        super.onResume();
        showViewBasketBtn();
    }

    private void showViewBasketBtn() {
        MyApplication app = (MyApplication) getApplicationContext();
        if(!app.isBasketEmpty()) {
            String quantityAdded = app.getQuantity();
            String price = app.getPrice();
            viewBasketBtn.setVisibility(View.VISIBLE);
            viewBasketBtn.setText("£" + price + "     View Basket     " + quantityAdded);
            System.out.println("Price is "+price+" Quantity "+quantityAdded+" Food names "+app.getFoodNames().size());
        }
    }

}
