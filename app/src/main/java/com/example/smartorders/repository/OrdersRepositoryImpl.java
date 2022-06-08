package com.example.smartorders.repository;

import androidx.annotation.NonNull;

import com.example.smartorders.R;
import com.example.smartorders.fragments.PastOrdersFragment;
import com.example.smartorders.interfaces.OnGetDataListenerPastOrders;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OrdersRepositoryImpl implements OrdersRepository{
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public void retrievePastOrderFromFirebase(OnGetDataListenerPastOrders listener) {
        listener.onStart();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userDetails = mDatabase.child("Users").child(Objects.requireNonNull(mAuth.getUid())).child("Orders");
        ArrayList<PastOrdersFragment.PastOrderItemsListHelper> pastOrderItemsComponents = new ArrayList<>();
        userDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String orderId = "";
                for (DataSnapshot orderID : dataSnapshot.getChildren()) {
                    orderId = orderID.getKey();
                    Map<String, String> pastOrderItemsNameToQuantity = new HashMap<>();
                    String totalPrice = "";
                    for (DataSnapshot orderDetails : orderID.getChildren()) {
                        if(Objects.requireNonNull(orderDetails.getKey()).equals("TotalPrice")) {
                            totalPrice = orderDetails.getValue(String.class);
                        }
                        else if(orderDetails.getKey().equals("Items")){
                            for (DataSnapshot itemsLength : orderDetails.getChildren()) {
                                String itemName = "";
                                String itemQuantity = "";
                                for (DataSnapshot itemDetail : itemsLength.getChildren()) {
                                    if(itemDetail.getKey().equals("name")){
                                        itemName = itemDetail.getValue(String.class);
                                    }
                                    if(itemDetail.getKey().equals("quantity")){
                                        itemQuantity = itemDetail.getValue(String.class);
                                    }
                                }
                                pastOrderItemsNameToQuantity.put(itemName,itemQuantity);
                            }
                        }
                    }
                    PastOrdersFragment.PastOrderItemsListHelper itemObject = new PastOrdersFragment.PastOrderItemsListHelper("Your delivery by Greek Artisan Pastries",totalPrice,
                            R.drawable.greek_artisan_pastries_photo,"Order completed",orderId, pastOrderItemsNameToQuantity);
                    pastOrderItemsComponents.add(itemObject);
                }
                listener.onSuccess(dataSnapshot,pastOrderItemsComponents);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }
}
