package com.example.smartorders.repository;

import android.util.Log;

import com.example.smartorders.interfaces.OnGetDataListener;
import com.example.smartorders.models.MenuData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MenuRepositoryImpl implements MenuRepository{


    @Override
    public void getMenuFromFirebase(String child, OnGetDataListener listener) {
        listener.onStart();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference menuDetails = mDatabase.child(child);
        Map<String, Map<Integer, MenuData>> menuDetailMap = new HashMap<>();
        menuDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot menuCategory : dataSnapshot.getChildren()) {
                    int counter = 0;
                    Map<Integer, MenuData> menuDetailItem = new HashMap<>();
                    Map<String, String> menuItemKeyValue = new HashMap<>();
                    for (DataSnapshot menuItems : menuCategory.getChildren()) {
                        counter++;
                        for (DataSnapshot menuItem : menuItems.getChildren()) {
                            menuItemKeyValue.put(menuItem.getKey(), menuItem.getValue(String.class));
                        }
                        MenuData menuDetailDataModel = new MenuData(menuItemKeyValue.get("name"), menuItemKeyValue.get("subheader"),
                                menuItemKeyValue.get("description"), menuItemKeyValue.get("price"));
                        menuDetailItem.put(counter, menuDetailDataModel);
                    }
                    menuDetailMap.put(menuCategory.getKey(), menuDetailItem);
                }
                listener.onSuccess(dataSnapshot, menuDetailMap);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MenuRepositoryImpl", "The read for menu details list failed: " + databaseError.getCode());
                listener.onFailed(databaseError);
            }
        });
    }

}
