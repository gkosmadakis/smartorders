package com.example.smartorders;

import com.example.smartorders.activity.ui.orders.PastOrdersFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.List;

public interface OnGetDataListenerPastOrders {
    void onStart();
    void onSuccess(DataSnapshot data, List<PastOrdersFragment.PastOrderItemsListHelper> pastOrderItemsComponents);
    void onFailed(DatabaseError databaseError);
}
