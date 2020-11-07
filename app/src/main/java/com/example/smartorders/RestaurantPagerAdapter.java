package com.example.smartorders;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.example.smartorders.activity.RestaurantActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.rpc.Help;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class RestaurantPagerAdapter extends FragmentStatePagerAdapter {
    private int PAGE_COUNT = 0;
    private Context context;
    private LinkedHashMap<String, Map<Integer,MenuDetailDataModel>> modifiedMenuDetailLinkedHashMap;
    private  ProgressDialog mProgressDialog;
    List<String> tabTitles;

    public RestaurantPagerAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        callFirebaseToGetMenu("FoodMenu");
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        /*here i take the menu category and the corresponding list of MenuDetailDataModel of the menu category
        * i.e Picked for you -> list of the Picked for you*/
        String headerRequested = tabTitles.get(position);
        Map <Integer,MenuDetailDataModel> menuDetailDataModelList = modifiedMenuDetailLinkedHashMap.get(headerRequested);
        return PageFragment.newInstance(position + 1, headerRequested,menuDetailDataModelList);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles.get(position);
    }

    private void callFirebaseToGetMenu(String child) {
        new RestaurantActivity().retrieveMenuDetailsFromFirebase(child, new OnGetDataListener() {
            @Override
            public void onStart() {
                //DO SOME THING WHEN START GET DATA HERE
                if (mProgressDialog == null) {
                    mProgressDialog = new ProgressDialog(context);
                    mProgressDialog.setMessage("Loading");
                    mProgressDialog.setIndeterminate(true);
                }
                mProgressDialog.show();
            }

            @Override
            public void onSuccess(DataSnapshot data, Map <String, Map<Integer,MenuDetailDataModel>> menuDetailMap) {
                //DO SOME THING WHEN GET DATA SUCCESS HERE
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    for (Map.Entry<String, Map<Integer,MenuDetailDataModel>> entry : menuDetailMap.entrySet()) {
                        Log.i("RestaurantPagerAdapter", "Key is "+entry.getKey()+ " Value is "+entry.getValue());
                    }

                    modifiedMenuDetailLinkedHashMap = modifyMenuDetailMap(menuDetailMap);
                    tabTitles =  new ArrayList<>(modifiedMenuDetailLinkedHashMap.keySet());
                    PAGE_COUNT = tabTitles.size();
                    notifyDataSetChanged();
                }
            }
            @Override
            public void onFailed(DatabaseError databaseError) {
                //DO SOME THING WHEN GET DATA FAILED HERE
                Log.e("RestaurantPageAdapter ","Error while getting data from Firebase "+databaseError);
            }
        });
    }

    private LinkedHashMap modifyMenuDetailMap(Map<String, Map<Integer,MenuDetailDataModel>> menuDetailMap) {
        Map<String, Map<Integer,MenuDetailDataModel>> modifiedMenuDetailHashMap = new HashMap<>();
        Iterator<Map.Entry<String, Map<Integer,MenuDetailDataModel>>> iterator = menuDetailMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Map<Integer,MenuDetailDataModel>> entry = iterator.next();
            String key = entry.getKey();
            if(key.equals("VeganPies")){
                iterator.remove();
                modifiedMenuDetailHashMap.put("Vegan Pies", entry.getValue());
            }
            if(key.equals("PickedForYou")){
                iterator.remove();
                modifiedMenuDetailHashMap.put("Picked for you", entry.getValue());
            }
            if(key.equals("MeatPies")){
                iterator.remove();
                modifiedMenuDetailHashMap.put("Meat Pies", entry.getValue());
            }
            if(key.equals("DairyFreeSweets")){
                iterator.remove();
                modifiedMenuDetailHashMap.put("Dairy Free Sweets", entry.getValue());
            }
            if(key.equals("VegetarianPies")){
                iterator.remove();
                modifiedMenuDetailHashMap.put("Vegetarian Pies", entry.getValue());
            }
            if(key.equals("Sweets")){
                iterator.remove();
                modifiedMenuDetailHashMap.put("Sweets", entry.getValue());
            }
            if(key.equals("Coffees")){
                iterator.remove();
                modifiedMenuDetailHashMap.put("Coffees", entry.getValue());
            }
        }
        /*Here i put in the map first the Picked for you to appear first in the tabs and in the menu list */
        LinkedHashMap<String, Map<Integer,MenuDetailDataModel>> modifiedMenuDetailLinkedHashMap = new LinkedHashMap<String, Map<Integer,MenuDetailDataModel>>(modifiedMenuDetailHashMap.size());
        Map<Integer, MenuDetailDataModel> pickedForYou = modifiedMenuDetailHashMap.remove("Picked for you");
        modifiedMenuDetailLinkedHashMap.put("Picked for you", pickedForYou);
        modifiedMenuDetailLinkedHashMap.putAll(modifiedMenuDetailHashMap);

        for (Map.Entry<String, Map<Integer,MenuDetailDataModel>> entry : modifiedMenuDetailLinkedHashMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Log.i("Modified Map ","Key is "+key+" value is "+value);
        }
        return modifiedMenuDetailLinkedHashMap;
    }

}

