package com.example.smartorders.service;

import com.example.smartorders.interfaces.OnGetDataListener;
import com.example.smartorders.repository.MenuRepository;
import com.example.smartorders.repository.MenuRepositoryImpl;

public class MenuServiceImpl implements MenuService{
    private final MenuRepository menuRepository = new MenuRepositoryImpl();


    @Override
    public void getMenuFromFirebase(String child, OnGetDataListener listener) {
        menuRepository.getMenuFromFirebase(child, listener);
    }
}
