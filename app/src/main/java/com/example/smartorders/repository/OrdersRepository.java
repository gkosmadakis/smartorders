package com.example.smartorders.repository;

import com.example.smartorders.interfaces.OnGetDataListenerOrderStatus;
import com.example.smartorders.interfaces.OnGetDataListenerPastOrders;

public interface OrdersRepository {
    void retrievePastOrderFromFirebase(OnGetDataListenerPastOrders listener);
    void retrieveOrderPreparationTimeFromFirebase(OnGetDataListenerOrderStatus listener, String orderId);
}
