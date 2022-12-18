package com.example.smartorders.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.smartorders.R;
import com.example.smartorders.adapters.ExpandableListAdapter;
import com.example.smartorders.adapters.MySimpleArrayAdapter;
import com.example.smartorders.service.DeliveryService;
import com.example.smartorders.service.DeliveryServiceImpl;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class FindAddressActivity extends AppCompatActivity  {
    private static final String TAG = "FindAddressActivity";
    private RelativeLayout showAddressResultslayout,deliveryOptionsLayout,saveBtnLayout;
    private TextView addressView,deliveryOptionsView;
    private ListView addressResultsListView;
    private ExpandableListView deliveryOptionsListView;
    private Button saveAddressDeliveryBtn;
    private String [] addressResultsItem;
    private CardView cardView;
    private EditText fullAddressView;
    private TextView requiredView,removeAddressView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private ExpandableListAdapter deliveryOptionsListAdapter;
    private int lastExpandedPosition = -1;
    private AutocompleteSupportFragment autocompleteFragment;
    private String fullAddress;
    private String deliveryOptionSelected;
    private String savedPlace;
    private String savedPlaceSelected;
    private String city;
    private double latitude;
    private double longtitude;
    private final DeliveryService deliveryService = new DeliveryServiceImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_address);
        Intent intent = getIntent();
        savedPlace = intent.getStringExtra("savedPlace");
        cardView = findViewById(R.id.idCardView);
        /*Relative Layout Views hidden initially */
        showAddressResultslayout = findViewById(R.id.showAddressResultslayout);
        showAddressResultslayout.setVisibility(View.GONE);
        addressView = findViewById(R.id.addressView);
        addressView.setVisibility(View.GONE);
        fullAddressView = findViewById(R.id.fullAddressView);
        fullAddressView.setVisibility(View.GONE);
        addressResultsListView = findViewById(R.id.addressResultsListView);
        addressResultsListView.setVisibility(View.GONE);
        deliveryOptionsLayout = findViewById(R.id.deliveryOptionsLayout);
        deliveryOptionsLayout.setVisibility(View.GONE);
        deliveryOptionsView = findViewById(R.id.deliveryOptionsView);
        deliveryOptionsView.setVisibility(View.GONE);
        requiredView = findViewById(R.id.requiredView);
        requiredView.setVisibility(View.GONE);
        deliveryOptionsListView = findViewById(R.id.deliveryOptionsListView);
        deliveryOptionsListView.setVisibility(View.GONE);
        removeAddressView = findViewById(R.id.removeAddressView);
        removeAddressView.setVisibility(View.GONE);
        saveBtnLayout = findViewById(R.id.saveBtnLayout);
        saveAddressDeliveryBtn = findViewById(R.id.saveAddressDeliveryBtn);
        saveAddressDeliveryBtn.setEnabled(false);
        /*Check if in Shared Preferences there are saved data. If there are then present them when starting the activity */
        SharedPreferences sp = getSharedPreferences(savedPlace,Context.MODE_PRIVATE);
        if (!sp.getString("fullAddressDelivery","").equals("")){
            /*Get the first field the full address from the prefs*/
            fullAddress = sp.getString("fullAddressDelivery","");
            /*Get the delivery details */
            String deliveryOptions = sp.getString("deliveryOption","");
            String deliveryOptionInstructions = sp.getString("deliveryOptionInstructions","");
            /*I use an arraylist because it maintains insertion order so i want Business or Building name to be inserted first
             * etc */
            ArrayList<String> retrievedAddressDetails = new ArrayList<String>();
            retrievedAddressDetails.add(sp.getString("businessOrBuildingName",""));
            retrievedAddressDetails.add(sp.getString("streetAddress",""));
            retrievedAddressDetails.add(sp.getString("flatOrHouseNumber",""));
            retrievedAddressDetails.add(sp.getString("postcode",""));
            try {
                showListsForAddressesDeliveryOptions(null, deliveryOptions,deliveryOptionInstructions,retrievedAddressDetails);
            } catch (IOException e) {
                e.printStackTrace();
            }
            cardView.setVisibility(View.GONE);
            /*Get the next fields from prefs*/
            /* Call expand group for the appropriate delivery option */
            if(deliveryOptions.equals("Meet Outside")) {
                deliveryOptionsListView.expandGroup(0);
                deliveryOptionSelected = deliveryOptions;
            }
            if(deliveryOptions.equals("Meet at door")) {
                deliveryOptionsListView.expandGroup(1);
                deliveryOptionSelected = deliveryOptions;
            }
            if(deliveryOptions.equals("Leave at door")) {
                deliveryOptionsListView.expandGroup(2);
                deliveryOptionSelected = deliveryOptions;
            }
        }
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.api_key));
        }
        fullAddressView.setOnClickListener(view -> showSearchFragmentAddress());
        removeAddressView.setOnClickListener(view -> finish());
        saveAddressDeliveryBtn.setOnClickListener(view ->
                deliveryService.saveDeliveryDetailsToFirebase(FindAddressActivity.this,
                        savedPlace, fullAddress, addressResultsListView, deliveryOptionSelected, city, savedPlaceSelected,
                        String.valueOf(latitude), String.valueOf(longtitude)));
        showAutoCompleteFragment();
    }//end of onCreate

    private void showAutoCompleteFragment() {
        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG,Place.Field.NAME));
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getAddress() + ", " + place.getId());
                /*Add the call to show the List Views */
                /*Add the list Views for showAddressResultslayout and deliveryOptionsLayout*/
                try {
                    showListsForAddressesDeliveryOptions(place.getLatLng(), "","",null);
                    savedPlaceSelected = place.getName();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cardView.setVisibility(View.GONE);
            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    private void showSearchFragmentAddress() {
        cardView.setVisibility(View.VISIBLE);
        showAddressResultslayout.setVisibility(View.GONE);
        addressView.setVisibility(View.GONE);
        fullAddressView.setVisibility(View.GONE);
        addressResultsListView.setVisibility(View.GONE);
        deliveryOptionsLayout.setVisibility(View.GONE);
        deliveryOptionsView.setVisibility(View.GONE);
        requiredView.setVisibility(View.GONE);
        deliveryOptionsListView.setVisibility(View.GONE);
        removeAddressView.setVisibility(View.GONE);
        saveBtnLayout.setVisibility(View.GONE);
        showAutoCompleteFragment();
    }

    private void showListsForAddressesDeliveryOptions(LatLng latLng, String deliveryOptions, String deliveryInstruction, ArrayList<String> retrievedAddressDetails) throws IOException {
        showAddressResultslayout.setVisibility(View.VISIBLE);
        addressView.setVisibility(View.VISIBLE);
        fullAddressView.setVisibility(View.VISIBLE);
        fullAddressView.setFocusable(false);
        addressResultsListView.setVisibility(View.VISIBLE);
        deliveryOptionsLayout.setVisibility(View.VISIBLE);
        deliveryOptionsView.setVisibility(View.VISIBLE);
        requiredView.setVisibility(View.VISIBLE);
        deliveryOptionsListView.setVisibility(View.VISIBLE);
        removeAddressView.setVisibility(View.VISIBLE);
        saveBtnLayout.setVisibility(View.VISIBLE);
        saveAddressDeliveryBtn.setEnabled(false);
        addressResultsItem = getResources().getStringArray(R.array.array_on_address_fields);
        MySimpleArrayAdapter adapterAddress = new MySimpleArrayAdapter(this, addressResultsItem, null);
        addressResultsListView.setAdapter(adapterAddress);
        if(retrievedAddressDetails != null) {
             addressResultsItem = retrievedAddressDetails.toArray(new String[0]);
             adapterAddress = new MySimpleArrayAdapter(this, addressResultsItem,null);
             addressResultsListView.setAdapter(adapterAddress);
             adapterAddress.notifyDataSetChanged();
             adapterAddress.notifyDataSetChangedWrapper();
        }
        if(latLng != null) {
            fullAddress = getFullAddressFromLatLng(latLng.longitude, latLng.latitude);
        }
        Log.d(TAG, "Full Address retrieved is " + fullAddress);
        fullAddressView.setText(fullAddress);
        if(fullAddressView.getText().toString().length() > 45){
            fullAddressView.setTextSize(12);
        }
        else {
            fullAddressView.setTextSize(18);
        }
        // preparing list data
        prepareListData(deliveryOptions, deliveryInstruction);
        deliveryOptionsListAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        // setting list adapter
        deliveryOptionsListView.setAdapter(deliveryOptionsListAdapter);
        if (!deliveryOptions.equals("") && !deliveryInstruction.equals("")){
            deliveryOptionsListAdapter.notifyDataSetChanged();
            deliveryOptionsListAdapter.notifyDataSetChangedWrapper();
        }
        deliveryOptionsListView.setOnGroupClickListener((expandableListView, view, i, l) -> {
            Log.d(TAG, "Delivery option is " + listDataHeader.get(i));
            deliveryOptionSelected = listDataHeader.get(i);
            return false;
        });
        deliveryOptionsListView.setOnGroupExpandListener(groupPosition -> {
            saveAddressDeliveryBtn.setEnabled(true);
            removeAddressView.setVisibility(View.GONE);
            if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                deliveryOptionsListView.collapseGroup(lastExpandedPosition);
                saveAddressDeliveryBtn.setEnabled(true);
                removeAddressView.setVisibility(View.GONE);
            }
            lastExpandedPosition = groupPosition;
        });
        deliveryOptionsListView.setOnGroupCollapseListener(groupPosition -> {
            saveAddressDeliveryBtn.setEnabled(false);
            removeAddressView.setVisibility(View.VISIBLE);
        });
    }

    private String getFullAddressFromLatLng(double longitude, double latitude) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        String fullAddress = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        String mSubAdminArea = addresses.get(0).getSubAdminArea();
        /*Sometimes city returns null so get it from subAdminArea*/
        if(city == null){
            city = mSubAdminArea;
        }
        this.city = city;
        this.latitude = latitude;
        this.longtitude = longitude;
        Log.d(TAG, "Address "+ fullAddress+" City "+city+" state "+state+" Country "+country+" postcode "+postalCode+" knownName "+knownName);
        return fullAddress;
    }

    private void prepareListData(String deliveryOptions, String deliveryInstruction) {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        // Adding Header data
        listDataHeader.add("Meet Outside");
        listDataHeader.add("Meet at door");
        listDataHeader.add("Leave at door");
        // Adding child data
        List<String> meetOutsideChild = new ArrayList<String>();
        List<String> meetAtDoorChild = new ArrayList<String>();
        List<String> leaveAtDoorChild = new ArrayList<String>();
        /*this happens when there are delivery instructions saved in DB/Shared Prefs and we want to set the text on the
        * edit text field */
        if (!deliveryOptions.equals("") && !deliveryInstruction.equals("")){
            if(deliveryOptions.equals("Meet Outside")){
                meetOutsideChild.add(deliveryInstruction);
            }
            else {
                meetOutsideChild.add("");
            }
            if(deliveryOptions.equals("Meet at door")){
                meetAtDoorChild.add(deliveryInstruction);
            }
            else {
                meetAtDoorChild.add("");
            }
            if(deliveryOptions.equals("Leave at door")){
                leaveAtDoorChild.add(deliveryInstruction);
            }
            else {
                leaveAtDoorChild.add("");
            }
        }
        else{
            meetOutsideChild.add("");
            meetAtDoorChild.add("");
            leaveAtDoorChild.add("");
        }
        listDataChild.put(listDataHeader.get(0), meetOutsideChild); // Header, Child data
        listDataChild.put(listDataHeader.get(1), meetAtDoorChild);
        listDataChild.put(listDataHeader.get(2), leaveAtDoorChild);
}

}

