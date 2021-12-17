package com.example.smartorders.service;

import com.example.smartorders.interfaces.OnGetDataListener;

public interface MenuService {

    void getMenuFromFirebase(String child, final OnGetDataListener listener);
}
