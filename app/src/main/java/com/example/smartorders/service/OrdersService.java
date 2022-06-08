package com.example.smartorders.service;

import com.example.smartorders.interfaces.OnGetDataListenerPastOrders;

public interface OrdersService {
    void retrievePastOrderFromFirebase(OnGetDataListenerPastOrders listener);
}
