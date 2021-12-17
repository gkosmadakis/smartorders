package com.example.smartorders.repository;

import com.example.smartorders.interfaces.OnGetDataListener;

public interface MenuRepository {

    void getMenuFromFirebase(String child, final OnGetDataListener listener);
}
