package com.example.smartorders.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartorders.R;
import com.example.smartorders.adapters.CheckoutOrderItemsAdapter;
import com.example.smartorders.models.MyApplication;
import com.example.smartorders.repository.PaymentRepositoryImpl;
import com.example.smartorders.service.PaymentServiceImpl;
import com.example.smartorders.utils.SharedPreferencesUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Token;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CheckoutActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "CheckoutActivity ";
    private Button meetOutside;
    private Button meetAtDoor;
    private Button leaveAtDoor;
    private Spinner deliveryOrPickup;
    private String [] deliveryOrPickupArray;
    private RecyclerView orderListSelected;
    private Button addPaymentMethodBtn;
    private Button placeOrderBtn;
    private FirebaseAuth mAuth;
    private final int addCardSuccessfullyFromCheckout = 1;
    private Map<String,String> tokenTo4LastDigitsMap = new HashMap();
    private EditText paymentSelected;
    private TextView subTotalPriceText;
    private TextView totalPriceText;
    private TextView deliveryFeesValue;
    private Double totalPrice;
    /*TODO hardcoded will have to be changed */
    private static final String deliveryFee = "1.5";
    private  DecimalFormat df = new DecimalFormat("#.##");
    private boolean userHasAddedCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        mAuth = FirebaseAuth.getInstance();

        MyApplication app = (MyApplication) getApplicationContext();
        String totalPriceReceived = app.getPrice();

        /* TODO Don't hardcode home as the user might want to select work */
        SharedPreferences sharedPrefs = getSharedPreferences("home", Context.MODE_PRIVATE);
        String city = sharedPrefs.getString("city","");
        Double latitude = Double.valueOf(Objects.requireNonNull(sharedPrefs.getString("latitude", "")));
        Double longtitude = Double.valueOf(Objects.requireNonNull(sharedPrefs.getString("longtitude", "")));
        String streetName = sharedPrefs.getString("placeName","");
        String deliveryOption = sharedPrefs.getString("deliveryOption","");

        /*Get views*/
        ImageView mapImage = findViewById(R.id.mapView);
        TextView streetNameText = findViewById(R.id.streetNameText);
        TextView cityText = findViewById(R.id.cityText);
        TextView restaurantNameText = findViewById(R.id.restaurantNameText);
        meetOutside = findViewById(R.id.meetOutsideBtn);
        meetOutside.setEnabled(false);
        meetAtDoor = findViewById(R.id.meetAtDoorBtn);
        meetAtDoor.setEnabled(false);
        leaveAtDoor = findViewById(R.id.leaveAtDoorBtn);
        leaveAtDoor.setEnabled(false);
        deliveryOrPickup = findViewById(R.id.deliveryOptionDropdown);
        TextView deliveryTimeText = findViewById(R.id.deliveryTimeText);
        TextView addItems = findViewById(R.id.addItems);
        orderListSelected = findViewById(R.id.orderRecyclerView);
        subTotalPriceText = findViewById(R.id.subTotalPriceValue);
        deliveryFeesValue = findViewById(R.id.deliveryFeesValue);
        totalPriceText = findViewById(R.id.totalPriceValue);
        if(totalPriceReceived != null) {
            subTotalPriceText.setText("£" + df.format(Double.parseDouble(totalPriceReceived)));
            totalPrice = Double.parseDouble(totalPriceReceived) + Double.parseDouble(deliveryFee);
            totalPriceText.setText("£" + df.format(totalPrice));
        }
        deliveryFeesValue.setText("£"+deliveryFee);
        addPaymentMethodBtn = findViewById(R.id.addPaymentMethodBtn);
        paymentSelected = findViewById(R.id.paymentSelected);
        paymentSelected.setVisibility(View.GONE);
        placeOrderBtn = findViewById(R.id.placeOrderBtn);
        placeOrderBtn.setEnabled(false);

        /*TODO will need to make it a variable if we have more restaurants */
        restaurantNameText.setText("Greek Artisan Pastries");
        cityText.setText(city);
        streetNameText.setText(streetName);

        deliveryOrPickupArray = getResources().getStringArray(R.array.delivery_or_pickup);
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, deliveryOrPickupArray);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        deliveryOrPickup.setAdapter(spinnerAdapter);

        /*TODO again dont hardcode this. It should vary according to circumstances */
        deliveryTimeText.setText("Delivery Time 20-30 mins");

        displayMapImage(latitude, longtitude, mapImage);

        setDeliveryOptionsButtons(deliveryOption);

        showOrderItemsList();

        addItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Go to Restaurant Activity */
                Intent intent = new Intent(getApplicationContext(), RestaurantActivity.class);
                startActivity(intent);
            }
        });

        addPaymentMethodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Go to add Payment Method Activity */
                Intent intent = new Intent(getApplicationContext(), AddPaymentMethodActivity.class);
                intent.putExtra("totalPrice", String.valueOf(totalPrice));
                intent.putExtra("deliveryOrPickup",deliveryOrPickup.getSelectedItem().toString());
                startActivityForResult(intent, addCardSuccessfullyFromCheckout);

            }
        });
        /*Look at sharedPrefs to see if the user has added a payment method */
        SharedPreferences prefers = getSharedPreferences("payments",Context.MODE_PRIVATE);
        if(!prefers.getString("lastPaymentMethod","").equals("")){
            paymentSelected.setVisibility(View.VISIBLE);
            addPaymentMethodBtn.setVisibility(View.GONE);
            placeOrderBtn.setEnabled(true);
            paymentSelected.setText("**********"+prefers.getString("lastPaymentMethod",""));
        }

        placeOrderBtn.setOnClickListener(view -> {
            /*Process the payment */
            new PaymentServiceImpl().processPayment(totalPriceReceived, CheckoutActivity.this, deliveryOrPickup.getSelectedItem().toString());
        });

    }// end of onCreate


    @Override
    public void onResume(){
        super.onResume();
        showOrderItemsList();
        MyApplication app = (MyApplication) getApplicationContext();
        String totalPriceReceived = app.getPrice();
        subTotalPriceText.setText("£" + df.format(Double.parseDouble(totalPriceReceived)));
        totalPrice = Double.parseDouble(totalPriceReceived) + Double.parseDouble(deliveryFee);
        totalPriceText.setText("£" + df.format(totalPrice));
        System.out.println("IN ON RESUME " +isUserHasAddedCard());
        if(isUserHasAddedCard()){
            paymentSelected.setVisibility(View.VISIBLE);
            addPaymentMethodBtn.setVisibility(View.GONE);
            placeOrderBtn.setEnabled(true);
        }
    }

    private void displayMapImage(Double latitude, Double longtitude, ImageView mapImage) {
        /*Starting a new thread here to make the hhtpGet call otherwise it throws NetworkMainThreadException,
        * another option is asyncTask */
        Thread thread = new Thread(){
            final String maps_static_api_key = getResources().getString(R.string.maps_static_api_key);
            final String URL = "http://maps.google.com/maps/api/staticmap?center="+latitude+","+longtitude+"&zoom=15&size=450x200&sensor=false&key="+maps_static_api_key;
            final HttpClient httpclient = new DefaultHttpClient();
            final HttpGet request = new HttpGet(URL);
            Bitmap bmp = null;
            @Override
            public void run() {
                InputStream in = null;
                try {
                    in = httpclient.execute(request).getEntity().getContent();
                    bmp = BitmapFactory.decodeStream(in);
                    in.close();
                    synchronized (this) {
                        wait(500);
                        runOnUiThread(() -> mapImage.setImageBitmap(bmp));
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            };
        };
        thread.start();
    }

    private void showOrderItemsList() {
        /*Get items on the listView */
        MyApplication app = (MyApplication) getApplicationContext();
        Map<String, Map<String,Double>> quantityNamePriceMap = app.getQuantityNamePriceMap();
        ArrayList<OrderItemsListHelper> helperList = new ArrayList<>();

        for(Map.Entry<String, Map<String,Double>> entry : quantityNamePriceMap.entrySet()){
            for(Map.Entry<String,Double> secondMap : entry.getValue().entrySet()) {
                OrderItemsListHelper orderItemsListHelper = new OrderItemsListHelper();
                orderItemsListHelper.setQuantity(entry.getKey());
                orderItemsListHelper.setFoodName(secondMap.getKey());
                orderItemsListHelper.setPrice(String.valueOf(secondMap.getValue()));
                helperList.add(orderItemsListHelper);
            }
        }

        CheckoutOrderItemsAdapter adapterAddress = new CheckoutOrderItemsAdapter(this, helperList);
        orderListSelected.setAdapter(adapterAddress);
        orderListSelected.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setDeliveryOptionsButtons(String deliveryOption) {
        /*Set the buttons for delivery options */
        String [] deliveryOptionsAvailable = getResources().getStringArray(R.array.array_on_delivery_options);
        /*Meet Outside option */
        if (deliveryOption.equals(deliveryOptionsAvailable[0])){
            meetOutside.setEnabled(true);
        }
        /*Meet at door option */
        else if (deliveryOption.equals(deliveryOptionsAvailable[1])){
            meetAtDoor.setEnabled(true);
        }
        /*Leave at door option */
        if (deliveryOption.equals(deliveryOptionsAvailable[2])){
            leaveAtDoor.setEnabled(true);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        Toast.makeText(getApplicationContext(),deliveryOrPickupArray[position] , Toast.LENGTH_LONG).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    public static class OrderItemsListHelper {
        private  String quantity;
        private String foodName;
        private String price;

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getFoodName() {
            return foodName;
        }

        public void setFoodName(String foodName) {
            this.foodName = foodName;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        @Override
        public String toString() {
            return "OrderItemsListHelper{" +
                    "quantity='" + quantity + '\'' +
                    ", foodName='" + foodName + '\'' +
                    ", price='" + price + '\'' +
                    '}';
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == addCardSuccessfullyFromCheckout) {
            if (data.hasExtra("token")) {
                setUserHasAddedCard(true);
                saveToPrefsLastCardAdded(data.getStringExtra("token"));
            }
        }
    }

    private void saveToPrefsLastCardAdded(String token) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Stripe.apiKey = "sk_test_50wnmiroW2ia9FhnXBPj2slO00E1JGcvFg";
                    String last4Digits = Token.retrieve(token).getCard().getLast4();
                    System.out.println("saveToPrefsLastCardAdded " +last4Digits);
                    synchronized (this) {
                        wait(50);
                        runOnUiThread(() -> {
                            /*Update UI paymentSelected EditText and save to preferences the 4 digits */
                            System.out.println("INSIDE saveToPrefsLastCardAdded of synchronized block "+ last4Digits);
                            paymentSelected.setText("**********"+last4Digits);
                            SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(CheckoutActivity.this, "payments");
                            sharedPreferencesUtil.getEditor().putString("lastPaymentMethod", last4Digits);
                            sharedPreferencesUtil.getEditor().apply();
                        });
                    }
                } catch (StripeException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public boolean isUserHasAddedCard() {
        return userHasAddedCard;
    }

    public void setUserHasAddedCard(boolean userHasAddedCard) {
        this.userHasAddedCard = userHasAddedCard;
    }
}
