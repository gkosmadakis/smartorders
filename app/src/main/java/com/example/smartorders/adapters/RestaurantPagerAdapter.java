package com.example.smartorders.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.smartorders.fragments.PageFragment;
import com.example.smartorders.interfaces.OnGetDataListener;
import com.example.smartorders.models.MenuData;
import com.example.smartorders.service.MenuService;
import com.example.smartorders.service.MenuServiceImpl;
import com.example.smartorders.utils.MapUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RestaurantPagerAdapter extends FragmentStatePagerAdapter {
    private int PAGE_COUNT = 0;
    private final Context context;
    private LinkedHashMap<String, Map<Integer, MenuData>> modifiedMenuDetailLinkedHashMap;
    private ProgressDialog mProgressDialog;
    private List<String> tabTitles;
    private final MenuService menuService = new MenuServiceImpl();

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
        Map <Integer, MenuData> menuDetailDataModelList = modifiedMenuDetailLinkedHashMap.get(headerRequested);
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
        menuService.getMenuFromFirebase(child, new OnGetDataListener() {
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
            public void onSuccess(DataSnapshot data, Map <String, Map<Integer, MenuData>> menuDetailMap) {
                //DO SOME THING WHEN GET DATA SUCCESS HERE
                MapUtils mapUtils = new MapUtils();
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    for (Map.Entry<String, Map<Integer, MenuData>> entry : menuDetailMap.entrySet()) {
                        Log.i("RestaurantPagerAdapter", "Key is "+entry.getKey()+ " Value is "+entry.getValue());
                    }
                    modifiedMenuDetailLinkedHashMap = mapUtils.modifyMenuDetailMap(menuDetailMap);
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

}

