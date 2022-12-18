package com.example.smartorders.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartorders.R;
import com.example.smartorders.interfaces.OnGetDataListenerOrderStatus;
import com.example.smartorders.service.OrdersService;
import com.example.smartorders.service.OrdersServiceImpl;
import com.google.firebase.database.DatabaseError;

import java.util.Timer;
import java.util.TimerTask;

public class OrderProgressActivity extends AppCompatActivity {
    private final OrdersService ordersService = new OrdersServiceImpl();
    private String orderId;
    public int minutes = 0;
    public int seconds = 59;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_progress);
        TextView orderProgressTime = findViewById(R.id.orderProgressTime);
        Button homeBtn = findViewById(R.id.homeButton);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            orderId = extras.getString("orderId");
        }
        getOrderTimeFromFirebase(new OnGetDataListenerOrderStatus() {
            @Override
            public void onStart() {
                orderProgressTime.setText("Your order is being processed...");
            }
            @Override
            public void onAcceptOrder(int preparationTime) {
                Log.e("OrderProgressActivity ", "OnSuccess Called preparation TIME is "+ preparationTime);
                orderProgressTime.setText("Your order will be with you in " + preparationTime
                        + " minutes");
                minutes = preparationTime -1;
                Timer t = new Timer();
                //Set the schedule function and rate
                t.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> {
                            TextView countDown = findViewById(R.id.countDown);
                            if(seconds == 0) {
                                countDown.setText(String.valueOf(minutes)+":0"+String.valueOf(seconds));
                                seconds = 59;
                                if(minutes == 0){
                                    countDown.setText("00:00");
                                    t.cancel();
                                    t.purge();
                                }
                                minutes = minutes-1;
                            }
                            else if(seconds <= 9 && seconds > 0){
                                countDown.setText(String.valueOf(minutes)+":0"+String.valueOf(seconds));
                                seconds -= 1;
                            }
                            else{
                                countDown.setText(String.valueOf(minutes)+":"+String.valueOf(seconds));
                                seconds -= 1;
                            }
                        });
                    }
                }, 0, 1000);
            }
            @Override
            public void onDeclineOrder(String declineMessage) {
                orderProgressTime.setText("Your order is declined by the restaurant please contact them ");
                Log.e("OrderProgressActivity ", "OnSuccess Called preparation TIME is "+ declineMessage);
            }
            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("OrderProgressActivity ","Error while getting data from Firebase "+databaseError);
            }
        }, orderId);

        homeBtn.setOnClickListener(view -> {
            Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(homeIntent);
        });
    }

    private void getOrderTimeFromFirebase(final OnGetDataListenerOrderStatus listener, String orderId){
         ordersService.retrieveOrderPreparationTimeFromFirebase(listener, orderId);
    }

}
