package com.example.smartorders.models;

import com.stripe.android.PaymentConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.multidex.MultiDexApplication;


public class MyApplication extends MultiDexApplication {
    private String quantity;
    private String price;
    private ArrayList foodNames = new ArrayList();
    private Map<String, Map<String, Double>> quantityNamePriceMap = new HashMap();
    private Map<String, String> instructionToFoodNameMap = new HashMap<>();

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public ArrayList getFoodNames() {
        return foodNames;
    }

    public void setFoodNames(ArrayList foodNames) {
        this.foodNames = foodNames;
    }

    public Map<String, Map<String, Double>> getQuantityNamePriceMap() {
        return quantityNamePriceMap;
    }

    public void setQuantityNamePriceMap(Map<String, Map<String, Double>> quantityNamePriceMap) {
        this.quantityNamePriceMap = quantityNamePriceMap;
    }

    public Map<String, String> getInstructionToFoodNameMap() {
        return instructionToFoodNameMap;
    }

    public void setInstructionToFoodNameMap(Map<String, String> instructionToFoodNameMap) {
        this.instructionToFoodNameMap = instructionToFoodNameMap;
    }

    public boolean hasBasketItems() {
        boolean hasItems = false;

        if(quantity != "" && price != "" && !foodNames.isEmpty() && !quantityNamePriceMap.isEmpty()){
            hasItems = true;
        }
        return hasItems;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_rAfyYnFhIKSse7WRU4mrfDni00gkN8BQ67"
        );
    }

    public void clearBasket(){
        quantity = "";
        price = "";
        foodNames.clear();
        quantityNamePriceMap.clear();
    }

}