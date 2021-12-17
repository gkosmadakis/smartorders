package com.example.smartorders.interfaces;

import com.example.smartorders.models.MenuData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.Map;

public interface OnGetDataListener {
    void onStart();
    void onSuccess(DataSnapshot data, Map<String, Map<Integer, MenuData>> menuDetailMap);
    void onFailed(DatabaseError databaseError);
}
