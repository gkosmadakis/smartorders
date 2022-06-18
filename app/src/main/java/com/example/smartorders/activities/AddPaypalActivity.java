package com.example.smartorders.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartorders.R;
import com.example.smartorders.service.PaymentService;
import com.example.smartorders.service.PaymentServiceImpl;
import com.paypal.checkout.approve.Approval;
import com.paypal.checkout.approve.OnApprove;
import com.paypal.checkout.cancel.OnCancel;
import com.paypal.checkout.createorder.CreateOrder;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.error.ErrorInfo;
import com.paypal.checkout.error.OnError;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.Order;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PaymentButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class AddPaypalActivity extends AppCompatActivity {
    private static final int PAYPAL_REQUEST_CODE = 7777;
    private static final String TAG = "AddPaypalActivity ";
    private String deliveryOrPickup;
    private EditText amountToPay;
    private final PaymentService paymentService = new PaymentServiceImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_paypal);
        String totalPrice = getIntent().getStringExtra("totalPrice");
        deliveryOrPickup = getIntent().getStringExtra("deliveryOrPickup");
        //start paypal service
        PaymentButton paymentButton = findViewById(R.id.btnPayNow);
        amountToPay = findViewById(R.id.edtAmount);
        amountToPay.setText(totalPrice);

        paymentButton.setup(
                new CreateOrder() {
                    @Override
                    public void create(@NotNull CreateOrderActions createOrderActions) {
                        ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                        purchaseUnits.add(
                                new PurchaseUnit.Builder()
                                        .amount(
                                                new Amount.Builder()
                                                        .currencyCode(CurrencyCode.GBP)
                                                        .value(amountToPay.getText().toString())
                                                        .build()
                                        )
                                        .build()
                        );
                        Order order = new Order(
                                OrderIntent.CAPTURE,
                                new AppContext.Builder()
                                        .userAction(UserAction.PAY_NOW)
                                        .build(),
                                purchaseUnits
                        );
                        createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                    }
                },
                new OnApprove() {
                    @Override
                    public void onApprove(@NotNull Approval approval) {
                        approval.getOrderActions().capture(result -> {
                            Log.i(TAG, String.format("CaptureOrderResult: %s", result));
                            System.out.println("AddPaypalActivity on approve, order approved amount is " + amountToPay.getText().toString());
                            /*Store the order to Firebase Database */
                            int orderId = paymentService.storeOrderToFirebaseDB(AddPaypalActivity.this, amountToPay.getText().toString(), deliveryOrPickup);
                            Intent intent = new Intent(getApplicationContext(), OrderStatusActivity.class);
                            intent.putExtra("Activity", "AddPaypalActivity");
                            intent.putExtra("orderId", String.valueOf(orderId));
                            startActivity(intent);
                        });
                    }
                },
                new OnCancel() {
                    @Override
                    public void onCancel() {
                        Log.d(TAG, "Buyer cancelled the PayPal experience.");
                    }
                },
                new OnError() {
                    @Override
                    public void onError(@NotNull ErrorInfo errorInfo) {
                        Log.d(TAG, String.format("Error: %s", errorInfo));
                    }
                }
        );

    }// end of onCreate

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("AddPaypalActivity entering OnActivityResult");
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                System.out.println("AddPaypalActivity entering OnActivityResult inside ResultCodeOK " +resultCode);
            }
            } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            System.out.println("AddPaypalActivity entering OnActivityResult inside else if ResultCodeOK " +resultCode);
            }
        }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

