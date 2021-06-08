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

import com.example.smartorders.models.MenuDetailDataModel;
import com.example.smartorders.models.MyApplication;
import com.example.smartorders.interfaces.OnGetDataListener;
import com.example.smartorders.R;
import com.example.smartorders.adapters.RestaurantPagerAdapter;
import com.example.smartorders.utils.VerticalViewPager;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.viewpager.widget.PagerAdapter;

public class RestaurantActivity extends AppCompatActivity {

    private VerticalViewPager mPager;
    private PagerAdapter pagerAdapter;
    private DatabaseReference mDatabase;
    private AppBarLayout appBarLayout;
    private Button viewBasketBtn;
    private String quantityAdded;
    private String price;
    private Intent dataReceivedFromAddToBasketActivity;

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
                /*Go back when clicking on the arrow send data if the basket has items */
               /* Intent dataIntent = new Intent();
                dataIntent.putExtra("quantityAdded", quantityAdded);
                dataIntent.putExtra("price", price);
                // Activity finished ok, return the data
                setResult(RESULT_OK, dataIntent);*/
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

        mPager = findViewById(R.id.verticalViewPager);
        pagerAdapter = new RestaurantPagerAdapter(getSupportFragmentManager(),RestaurantActivity.this);

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


    public void retrieveMenuDetailsFromFirebase(String child, final OnGetDataListener listener) {
        listener.onStart();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference menuDetails = mDatabase.child(child);
        Map<String, Map<Integer,MenuDetailDataModel>> menuDetailMap = new HashMap<>();

        menuDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot menuCategory : dataSnapshot.getChildren()) {
                    if (menuCategory.getKey().equals("PickedForYou")) {
                        findDataAndPopulateMap(menuCategory, "PickedForYou");
                    }
                    if (menuCategory.getKey().equals("VegetarianPies")) {
                        findDataAndPopulateMap(menuCategory,"VegetarianPies");
                    }
                    if (menuCategory.getKey().equals("MeatPies")) {
                        findDataAndPopulateMap(menuCategory,"MeatPies");
                    }
                    if (menuCategory.getKey().equals("VeganPies")) {
                        findDataAndPopulateMap(menuCategory,"VeganPies");
                    }
                    if (menuCategory.getKey().equals("Sweets")) {
                        findDataAndPopulateMap(menuCategory,"Sweets");
                    }
                    if (menuCategory.getKey().equals("DairyFreeSweets")) {
                        findDataAndPopulateMap(menuCategory,"DairyFreeSweets");
                    }
                    if (menuCategory.getKey().equals("Coffees")) {
                        findDataAndPopulateMap(menuCategory,"Coffees");
                    }
                }
                listener.onSuccess(dataSnapshot,menuDetailMap);
            }

            private void findDataAndPopulateMap(DataSnapshot menuCategory, String category) {
                int counter = 0;
                Map<Integer,MenuDetailDataModel> menuDetailItem = new HashMap<>();
                for(DataSnapshot menuItems : menuCategory.getChildren()){
                    String name = "";
                    String subheader = "";
                    String description = "";
                    String price = "";
                    counter++;
                    for(DataSnapshot menuItem : menuItems.getChildren()) {
                        if (menuItem.getKey().equals("name")) {
                            name = menuItem.getValue(String.class);
                        }
                        if (menuItem.getKey().equals("subheader")) {
                            subheader = menuItem.getValue(String.class);
                        }
                        if (menuItem.getKey().equals("description")) {
                            description = menuItem.getValue(String.class);
                        }
                        if (menuItem.getKey().equals("price")) {
                            price = menuItem.getValue(String.class);
                        }
                    }
                    MenuDetailDataModel menuDetailDataModel = new MenuDetailDataModel(name, subheader, description, price);
                    menuDetailItem.put(counter, menuDetailDataModel);
                }
                menuDetailMap.put(category, menuDetailItem);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read for menu details list failed: " + databaseError.getCode());
                listener.onFailed(databaseError);
            }
        });
        for (Map.Entry<String, Map<Integer,MenuDetailDataModel>> entry : menuDetailMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Log.i("RestaurantPagerAdapter", "Key is "+key+ " Value is "+value);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (1) : {
                if (resultCode == Activity.RESULT_OK) {
                    // TODO Extract the data returned from the child Activity.
                    dataReceivedFromAddToBasketActivity = data;
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
