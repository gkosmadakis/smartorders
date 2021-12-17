package com.example.smartorders.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.example.smartorders.R;
import com.example.smartorders.activities.FindAddressActivity;
import com.example.smartorders.adapters.ExpandableListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class DeliveryRepositoryImpl implements DeliveryRepository{
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private static final String TAG = "DeliveryRepositoryImpl";

    @Override
    public void saveDeliveryDetailsToFirebase(Context context, String savedPlace, String fullAddress, ListView addressResultsListView, String deliveryOptionSelected, String city, String savedPlaceSelected, String latitude, String longtitude) {
        String user_id = mAuth.getCurrentUser().getUid();
        /*Save the data in Firebase and in Shared Preferences. In Prefs i save to two different files for home and work */
        DatabaseReference current_user_db = mDatabase.child(user_id).child(savedPlace);
        SharedPreferences sp = context.getSharedPreferences(savedPlace, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        current_user_db.child("fullAddressDelivery").setValue(fullAddress);
        editor.putString("fullAddressDelivery", fullAddress);

        for (int i = 0; i < addressResultsListView.getCount(); i++) {
            View view = addressResultsListView.getChildAt(i);
            EditText editText = view.findViewById(R.id.addressListItem);
            String string = editText.getText().toString();
            Log.d(TAG, "EditText value is " + string);
            if(editText.getHint().equals("Business or building name")){
                current_user_db.child("businessOrBuildingName").setValue(string);
                editor.putString("businessOrBuildingName",string);
            }
            if(editText.getHint().equals("Street Address")){
                current_user_db.child("streetAddress").setValue(string);
                editor.putString("streetAddress",string);
            }
            if(editText.getHint().equals("Flat or house number")){
                current_user_db.child("flatOrHouseNumber").setValue(string);
                editor.putString("flatOrHouseNumber",string);
            }
            if(editText.getHint().equals("Postcode")){
                current_user_db.child("postcode").setValue(string);
                editor.putString("postcode",string);
            }
        }
        String instructionAdded = "";
        if(ExpandableListAdapter.arrayList != null) {
            for (Map.Entry m : ExpandableListAdapter.arrayList.entrySet()) {
                Log.d(TAG, "Instruction is " + m.getKey() + " " + m.getValue());
                instructionAdded = (String) m.getValue();
            }
        }

        current_user_db.child("deliveryOption").setValue(deliveryOptionSelected);
        editor.putString("deliveryOption",deliveryOptionSelected);

        current_user_db.child("deliveryOptionInstructions").setValue(instructionAdded);
        editor.putString("deliveryOptionInstructions",instructionAdded);
        editor.putString("placeName", savedPlaceSelected);
        /*Also save city and longtitude/latitude that are used to CheckoutActivity */
        editor.putString("city",city);
        editor.putString("latitude", String.valueOf(latitude));
        editor.putString("longtitude", String.valueOf(longtitude));
        editor.apply();
        ((FindAddressActivity)context).finish();
    }
}
