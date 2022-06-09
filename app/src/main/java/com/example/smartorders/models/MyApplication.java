package com.example.smartorders.models;

import com.stripe.android.PaymentConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.multidex.MultiDexApplication;


public class MyApplication extends MultiDexApplication {
    private String quantity;
    private String price;
    private ArrayList<String> foodNames = new ArrayList();
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

    public ArrayList<String> getFoodNames() {
        return foodNames;
    }

    public void setFoodNames(ArrayList<String> foodNames) {
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

    public boolean isBasketEmpty() {
        boolean isEmpty = true;
        if(quantity != null && price != null) {
            if (Integer.parseInt(quantity) > 0 && Double.parseDouble(price) > 0.0) {
                isEmpty = false;
            }
        }
        return isEmpty;
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
        instructionToFoodNameMap.clear();
    }

}