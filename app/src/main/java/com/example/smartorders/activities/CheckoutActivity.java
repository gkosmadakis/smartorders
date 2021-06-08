package com.example.smartorders.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartorders.adapters.CheckoutOrderItemsAdapter;
import com.example.smartorders.models.MyApplication;
import com.example.smartorders.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stripe.Stripe;
import com.stripe.android.view.CardInputWidget;
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
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CheckoutActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "CheckoutActivity ";
    private TextView streetNameText;
    private TextView cityText;
    private TextView restaurantNameText;
    private Button meetOutside;
    private Button meetAtDoor;
    private Button leaveAtDoor;
    private Spinner deliveryOrPickup;
    private String [] deliveryOrPickupArray;
    private TextView deliveryTimeText;
    private TextView addItems;
    private RecyclerView orderListSelected;
    private Button addPaymentMethodBtn;
    private Button placeOrderBtn;
    private String paymentIntentClientSecret;
    private Stripe stripe;
    private CardInputWidget cardInputWidget;
    private FirebaseAuth mAuth;
    private final int addCardSuccessfullyFromCheckout = 1;
    private Map<String,String> tokenTo4LastDigitsMap = new HashMap();
    private EditText paymentSelected;
    private TextView subTotalPriceText;
    private TextView totalPriceText;
    private TextView deliveryFeesValue;
    private  ProgressDialog mProgressDialog;
    private DatabaseReference mDatabase;
    private Double totalPrice;
    /*TODO hardcoded will have to be changed */
    private static final String deliveryFee = "1.5";
    private  DecimalFormat df = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        MyApplication app = (MyApplication) getApplicationContext();
        String totalPriceReceived = app.getPrice();

        /* TODO Don't hardcode home as the user might want to select work */
        SharedPreferences sharedPrefs = getSharedPreferences("home", Context.MODE_PRIVATE);
        String city = sharedPrefs.getString("city","");
        Double latitude = Double.valueOf(sharedPrefs.getString("latitude",""));
        Double longtitude = Double.valueOf(sharedPrefs.getString("longtitude",""));
        String streetName = sharedPrefs.getString("placeName","");
        String deliveryOption = sharedPrefs.getString("deliveryOption","");

        /*Get views*/
        ImageView mapImage = findViewById(R.id.mapView);
        streetNameText = findViewById(R.id.streetNameText);
        cityText = findViewById(R.id.cityText);
        restaurantNameText = findViewById(R.id.restaurantNameText);
        meetOutside = findViewById(R.id.meetOutsideBtn);
        meetOutside.setEnabled(false);
        meetAtDoor = findViewById(R.id.meetAtDoorBtn);
        meetAtDoor.setEnabled(false);
        leaveAtDoor = findViewById(R.id.leaveAtDoorBtn);
        leaveAtDoor.setEnabled(false);
        deliveryOrPickup = findViewById(R.id.deliveryOptionDropdown);
        deliveryTimeText = findViewById(R.id.deliveryTimeText);
        addItems = findViewById(R.id.addItems);
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

        placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Process the payment */
                processPayment(totalPriceReceived);
            }
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
    }

    private void processPayment(String totalPriceReceived) {
        DocumentReference chargeRef;
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Please wait");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();

        chargeRef = FirebaseFirestore.getInstance().collection("stripe_customers").document(mAuth.getUid()).collection("charges").document();
        Map<String, Object> chargeDetails = new HashMap<>();
        /*It expects cents so i multiply by 100 */
        chargeDetails.put("amount",Double.parseDouble(totalPriceReceived)*100);
        chargeDetails.put("currency","gbp");
        chargeRef.set(chargeDetails);
        chargeRef.update("amount",Double.parseDouble(totalPriceReceived)*100).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), totalPriceReceived+" charged successfully", Toast.LENGTH_LONG).show();
                Log.i(TAG,"Firestore charges collection created, calling Firebase Function createStripeCharge");
                /*Here call the firestore and get the last charge created and see if the status was a success */
                getLastChargeFromFirestore();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG,e.getLocalizedMessage());
            }
        });
    }

    private void getLastChargeFromFirestore() {
        /*if payment is successful add the order to Firebase DB
         * Check Firestore to see if the order was success, get the Id document and check if it has status success */
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query query = rootRef.collection("stripe_customers").document(mAuth.getUid()).collection("charges")
                .orderBy("created", Query.Direction.DESCENDING)
                .limit(1);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData().get("status"));
                        if(document.getData().get("status").equals("succeeded")){
                            /*That means the transaction was completed successfully inform the user */
                            Log.i(TAG, "Transaction status "+document.getData().get("status")+
                                    " Amount charged "+document.getData().get("amount"));
                            /* and add the order to Firebase Database */
                            int orderID = storeOrderToFirebaseDB();
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(), OrderStatusActivity.class);
                                intent.putExtra("Activity", "CheckoutActivity");
                                intent.putExtra("orderId",String.valueOf(orderID));
                                startActivity(intent);
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting charge: ", task.getException());
                }
            }
        });
    }

    private int storeOrderToFirebaseDB() {
        DatabaseReference current_user_db_orders = mDatabase.child(mAuth.getUid()).child("Orders");
        /*Generate a random number from 1000-10000 as order ID */
        final int random = new Random().nextInt(9001) + 1000; // [1000, 10000] + 1000 => [1000, 10000]

        current_user_db_orders.child("Order ID "+ random).child("TotalPrice").setValue(totalPriceText.getText().toString());

        MyApplication app = (MyApplication) getApplicationContext();
        Map<String, Map<String,Double>> quantityNamePriceMap = app.getQuantityNamePriceMap();

        int counter = 1;
        for(Map.Entry<String, Map<String,Double>> entry : quantityNamePriceMap.entrySet()){

            for(Map.Entry<String,Double> secondMap : entry.getValue().entrySet()) {
                current_user_db_orders.child("Order ID "+ random).child("Items").child(String.valueOf(counter)).child("quantity").setValue(entry.getKey());
                current_user_db_orders.child("Order ID "+ random).child("Items").child(String.valueOf(counter)).child("name").setValue(secondMap.getKey());
                current_user_db_orders.child("Order ID "+ random).child("Items").child(String.valueOf(counter)).child("price").setValue(secondMap.getValue());
                counter++;
            }
        }
        SharedPreferences sharedPrefs = getSharedPreferences("home", Context.MODE_PRIVATE);
        current_user_db_orders.child("Order ID "+ random).child("deliveryOption").setValue(sharedPrefs.getString("deliveryOption",""));
        current_user_db_orders.child("Order ID "+ random).child("deliveryOrPickup").setValue(deliveryOrPickup.getSelectedItem().toString());
        current_user_db_orders.child("Order ID "+ random).child("deliveryAddress").setValue(sharedPrefs.getString("fullAddressDelivery",""));

        Map<String,String> instructionToFoodNameMap = app.getInstructionToFoodNameMap();
        for(Map.Entry<String,String> entry : instructionToFoodNameMap.entrySet()){
            current_user_db_orders.child("Order ID "+ random).child("Items").child("foodInstruction").child(entry.getKey()).setValue(entry.getValue());
        }

        return random;
    }

    private void displayMapImage(Double latitude, Double longtitude, ImageView mapImage) {
        /*Starting a new thread here to make the hhtpGet call otherwise it throws NetworkMainThreadException,
        * another option is asyncTask */
        Thread thread = new Thread(){
            String URL = "http://maps.google.com/maps/api/staticmap?center="+latitude+","+longtitude+"&zoom=15&size=450x200&sensor=false&key=XXXXXXXXXXX";
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet request = new HttpGet(URL);
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mapImage.setImageBitmap(bmp);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
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
                FirebaseFirestore.getInstance().collection("stripe_customers").document(mAuth.getUid()).collection("tokens")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        getCard4LastDigitsFromTokens((String) document.getData().get("token"));
                                        /*if the token brought from the AddCard Activity is the same as the one found in
                                        * Firestore then that means either is the only token or if the user has already added
                                        * cards find the one added last time and save it to shared preferences*/
                                        if(data.getStringExtra("token").equals(document.getData().get("token"))){
                                            saveToPrefsLastCardAdded(data.getStringExtra("token"));
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
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
                    SharedPreferences prefers = getSharedPreferences("payments",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefers.edit();
                    editor.putString("lastPaymentMethod", last4Digits);
                    editor.apply();

                    synchronized (this) {
                        wait(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                /*Update UI */
                                paymentSelected.setVisibility(View.VISIBLE);
                                addPaymentMethodBtn.setVisibility(View.GONE);
                                placeOrderBtn.setEnabled(true);
                                paymentSelected.setText("**********"+last4Digits);
                            }
                        });
                    }
                } catch (StripeException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private void getCard4LastDigitsFromTokens(String token) {
        Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    Stripe.apiKey = "sk_test_50wnmiroW2ia9FhnXBPj2slO00E1JGcvFg";
                    String last4Digits = Token.retrieve(token).getCard().getLast4();
                    Log.i( TAG, "Found Card ending in " + last4Digits
                            + " Brand is " + Token.retrieve(token).getCard().getBrand());
                    tokenTo4LastDigitsMap.put(token, last4Digits);
                } catch (StripeException ex) {
                    ex.printStackTrace();
                }
            }
        };
        thread.start();
    }


}
