package com.example.smartorders.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.viewpager.widget.PagerAdapter;

import com.example.smartorders.R;
import com.example.smartorders.adapters.RestaurantPagerAdapter;
import com.example.smartorders.models.MyApplication;
import com.example.smartorders.utils.VerticalViewPager;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

public class RestaurantActivity extends AppCompatActivity {

    private AppBarLayout appBarLayout;
    private Button viewBasketBtn;
    private String quantityAdded;
    private String price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        appBarLayout = findViewById(R.id.appbar);
        /*Display the back button on the toolbar */
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* if the basket is empty navigate to Home Activity */
                MyApplication app = (MyApplication) getApplicationContext();
                if(!app.hasBasketItems()){
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                }
                /* if the basket has items go to checkout activity */
                else{
                    finish();
                }
            }
        });
        /*Hide the action bar */
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        /*Instantiate the nested scroll view that holds the menu recycler view. Without this the menu is not showing */
        NestedScrollView nestedScrollView = findViewById(R.id.nestedScrollView);
        nestedScrollView.setFillViewport(true);

        TextView restaurantName = findViewById(R.id.restaurantName);
        TextView menuText = findViewById(R.id.menuText);

        VerticalViewPager mPager = findViewById(R.id.verticalViewPager);
        PagerAdapter pagerAdapter = new RestaurantPagerAdapter(getSupportFragmentManager(), RestaurantActivity.this);

        mPager.setAdapter(pagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setVisibility(View.GONE);
        tabLayout.setupWithViewPager(mPager);


        tabLayout.getViewTreeObserver().addOnScrollChangedListener(() -> {
            Log.i("RestaurantActivity", "onScroll called");
            Log.i("RestaurantActivity", "appBarLayout getY is " + appBarLayout.getY());
            if(appBarLayout.getY() < -367){
                Log.i("RestaurantActivity", "appBarLayout getY is " + appBarLayout.getY());
                /*when the user starts scrolling down hide the tabs show the action bar */
                tabLayout.setVisibility(View.VISIBLE);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
                getSupportActionBar().show();
                /*Hide restaurant name and menu text */
                restaurantName.setVisibility(View.GONE);
                menuText.setVisibility(View.GONE);
                //Make the back button on the action bar black //
                final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
                upArrow.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
                //Make the font text of the action bar black //
                getSupportActionBar().setTitle("Greek Artisan Pastries");
                Spannable text = new SpannableString(getSupportActionBar().getTitle());
                text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                getSupportActionBar().setTitle(text);
            }
            else{
                /*when the user has scrolled all the way to the top of the screen hide the action bar and the tabs */
                getSupportActionBar().hide();
                tabLayout.setVisibility(View.GONE);
                /*show again the restaurant name and the menu text */
                restaurantName.setVisibility(View.VISIBLE);
                menuText.setVisibility(View.VISIBLE);
            }
        });

        viewBasketBtn = findViewById(R.id.viewBasketBtn);
        viewBasketBtn.setVisibility(View.GONE);

        viewBasketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Get total price of goods here */
                MyApplication app = (MyApplication) getApplicationContext();
                if(app.hasBasketItems()) {
                    //String totalPrice = app.getPrice();
                    Intent intent = new Intent(view.getContext(), CheckoutActivity.class);
                    //intent.putExtra("totalPrice", totalPrice);
                    startActivity(intent);
                }
            }
        });

    }// end of onCreate


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (1) : {
                if (resultCode == Activity.RESULT_OK) {
                    // TODO Extract the data returned from the child Activity.
                    quantityAdded = data.getStringExtra("quantityAdded");
                    price = data.getStringExtra("price");
                }
                break;
            }
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent dataIntent = new Intent();
        dataIntent.putExtra("quantityAdded", quantityAdded);
        dataIntent.putExtra("price", price);

    }

    @Override
    public void onResume() {
        super.onResume();
        showViewBasketBtn();
    }

    private void showViewBasketBtn() {
        MyApplication app = (MyApplication) getApplicationContext();
        if(app.hasBasketItems()) {
            String quantityAdded = app.getQuantity();
            String price = app.getPrice();
            viewBasketBtn.setVisibility(View.VISIBLE);
            viewBasketBtn.setText("£" + price + "     View Basket     " + quantityAdded);
        }
    }
}
