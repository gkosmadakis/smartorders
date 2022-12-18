package com.example.smartorders.interfaces;

import com.example.smartorders.fragments.PastOrdersFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.List;

public interface OnGetDataListenerOrderStatus {
    void onStart();
    void onAcceptOrder(int preparationTime);
    void onDeclineOrder(String declineMessage);
    void onFailed(DatabaseError databaseError);
}
