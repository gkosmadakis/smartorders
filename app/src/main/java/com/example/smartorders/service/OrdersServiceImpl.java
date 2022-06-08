package com.example.smartorders.service;

import com.example.smartorders.interfaces.OnGetDataListenerPastOrders;
import com.example.smartorders.repository.OrdersRepository;
import com.example.smartorders.repository.OrdersRepositoryImpl;

public class OrdersServiceImpl implements OrdersService {
    private final OrdersRepository ordersRepository = new OrdersRepositoryImpl();

    @Override
    public void retrievePastOrderFromFirebase(OnGetDataListenerPastOrders listener) {
        ordersRepository.retrievePastOrderFromFirebase(listener);
    }
}
