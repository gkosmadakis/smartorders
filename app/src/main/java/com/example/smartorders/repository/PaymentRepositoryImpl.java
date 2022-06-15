package com.example.smartorders.repository;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.smartorders.activities.OrderStatusActivity;
import com.example.smartorders.config.Config;
import com.example.smartorders.interfaces.ProcessPaymentCallback;
import com.example.smartorders.models.MyApplication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class PaymentRepositoryImpl implements PaymentRepository{
    private static final String TAG = "PaymentRepositoryImpl ";
    private  ProgressDialog mProgressDialog;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);

    @Override
    public boolean processPayment(String totalPriceReceived, Context context, String deliveryOrPickup, final ProcessPaymentCallback callback) {
        DocumentReference chargeRef;
        AtomicBoolean successCharge = new AtomicBoolean(false);
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("Please wait");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
        chargeRef = FirebaseFirestore.getInstance().collection("stripe_customers").document(mAuth.getUid()).collection("charges").document();
        Map<String, Object> chargeDetails = new HashMap<>();
        /*It expects cents so i multiply by 100 */
        chargeDetails.put("amount",Double.parseDouble(totalPriceReceived)*100);
        chargeDetails.put("currency","gbp");
        chargeDetails.put("created", new Date().getTime() / 1000);
        chargeRef.set(chargeDetails);
        chargeRef.update("amount",Double.parseDouble(totalPriceReceived)*100).addOnSuccessListener(aVoid -> {
            Log.i(TAG,"Firestore charges collection created, calling Firebase Function createStripeCharge");
            /*Here call the firestore and get the last charge created and see if the status was a success */
            //getLastChargeFromFirestore(context, totalPriceReceived, deliveryOrPickup);
            successCharge.set(true);
            callback.onSuccess(successCharge.get());
        }).addOnFailureListener(e -> {
            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG,e.getLocalizedMessage());
            successCharge.set(false);
            callback.onSuccess(successCharge.get());
        });
        return successCharge.get();
    }

    @Override
    public void getLastChargeFromFirestore(Context context, String totalPriceReceived, String deliveryOrPickup) {
        /*if payment is successful add the order to Firebase DB
         * Check Firestore to see if the order was success, get the Id document and check if it has status success */
        Log.d(TAG,"Getting into getLastChargeFromFirestore");
                FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query query = rootRef.collection("stripe_customers").document(Objects.requireNonNull(mAuth.getUid())).collection("charges")
                .orderBy("created", Query.Direction.DESCENDING)
                .limit(1);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Log.d(TAG, "Document ID "+document.getId());
                    Intent intent = new Intent(context, OrderStatusActivity.class);
                    /*There was an error in the transaction show it to the user */
                    if(document.getData().get("error") != null){
                        String error = document.getData().get("error").toString();
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                        intent.putExtra("Activity", "CheckoutActivity");
                        intent.putExtra("error", error);
                        context.startActivity(intent);
                    }
                    /*That means the transaction was completed successfully inform the user */
                    else if(document.getData().get("status") != null){
                        Log.i(TAG, "Transaction status "+document.getData().get("status")+
                                " Amount charged "+document.getData().get("amount"));
                        /* and add the order to Firebase Database */
                        int orderID = storeOrderToFirebaseDB(context, totalPriceReceived, deliveryOrPickup);
                        intent.putExtra("Activity", "CheckoutActivity");
                        intent.putExtra("orderId",String.valueOf(orderID));
                        context.startActivity(intent);
                        Toast.makeText(context, totalPriceReceived+" charged successfully", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Log.d(TAG, "An unexpected error occurred "+ document.getData());
                    }
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                }
            } else {
                Log.d(TAG, "Error getting charge: ", task.getException());
            }
        });
    }

    @Override
    public int storeOrderToFirebaseDB(Context context, String totalPriceReceived, String deliveryOrPickup) {
        Log.d(TAG,"Getting into storeOrderToFirebaseDB");
        DatabaseReference current_user_db_orders = mDatabase.child(Objects.requireNonNull(mAuth.getUid())).child("Orders");
        /*Generate a random number from 1000-10000 as order ID */
        final int random = new Random().nextInt(9001) + 1000; // [1000, 10000] + 1000 => [1000, 10000]
        String childOfOrder = "order " + random;
        current_user_db_orders.child(childOfOrder).child("orderId").setValue(String.valueOf(random));
        current_user_db_orders.child(childOfOrder).child("totalPrice").setValue(totalPriceReceived);
        MyApplication app = (MyApplication) context.getApplicationContext();
        Map<String, Map<String,Double>> quantityNamePriceMap = app.getQuantityNamePriceMap();
        Map<String,String> instructionToFoodNameMap = app.getInstructionToFoodNameMap();
        int counter = 1;
        for(Map.Entry<String, Map<String,Double>> entry : quantityNamePriceMap.entrySet()){
            for(Map.Entry<String,Double> secondMap : entry.getValue().entrySet()) {
                current_user_db_orders.child(childOfOrder).child("items").child(counter +"item").child("quantity").setValue(entry.getKey());
                current_user_db_orders.child(childOfOrder).child("items").child(counter+"item").child("name").setValue(secondMap.getKey());
                current_user_db_orders.child(childOfOrder).child("items").child(counter+"item").child("price").setValue(secondMap.getValue());
                counter++;
            }
        }
        SharedPreferences sharedPrefs = context.getSharedPreferences("home", Context.MODE_PRIVATE);
        current_user_db_orders.child(childOfOrder).child("deliveryOption").setValue(sharedPrefs.getString("deliveryOption",""));
        current_user_db_orders.child(childOfOrder).child("deliveryOrPickup").setValue(deliveryOrPickup);
        current_user_db_orders.child(childOfOrder).child("deliveryAddress").setValue(sharedPrefs.getString("fullAddressDelivery",""));
        for(Map.Entry<String,String> entry : instructionToFoodNameMap.entrySet()){
            current_user_db_orders.child(childOfOrder).child("foodInstruction").child(entry.getKey()).setValue(entry.getValue());
        }
        return random;
    }

    @Override
    public void processPaypalPayment(String amount, Activity addPaypalActivity, int PAYPAL_REQUEST_CODE) {
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(amount),"GBP",
                "Purchase Goods",PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(addPaypalActivity.getApplicationContext(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
        addPaypalActivity.startActivityForResult(intent,PAYPAL_REQUEST_CODE);
    }
}
