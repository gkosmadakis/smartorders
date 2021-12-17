package com.example.smartorders.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.smartorders.models.MenuData;
import com.example.smartorders.models.MyApplication;
import com.example.smartorders.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddToBasketActivity extends AppCompatActivity {

    private TextView foodNameSelected;
    private TextView subheaderFoodSelected;
    private TextView quantityText;
    private MenuData menuDetailDataModelSentFromIntent;
    private FloatingActionButton plusBtn, minusBtn;
    private Button addToBasketBtn;
    private int quantityAdded = 1;
    private int quantityToUpdate = 1;
    private double priceDouble;
    private TextView removeFromBasketView;
    private String quantityToChange;
    private String foodNameToChange;
    private String priceToChange;
    private EditText instructionsEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_basket);

        DecimalFormat df = new DecimalFormat("#.##");
        Intent intent = getIntent();
        foodNameSelected = findViewById(R.id.name);
        subheaderFoodSelected = findViewById(R.id.subheader);
        instructionsEditText = findViewById(R.id.instructionsEditText);
        minusBtn = findViewById(R.id.minusBtn);
        minusBtn.setEnabled(false);
        quantityText = findViewById(R.id.quantityText);
        plusBtn = findViewById(R.id.plusBtn);
        removeFromBasketView = findViewById(R.id.removeFromBasketView);
        removeFromBasketView.setVisibility(View.GONE);
        addToBasketBtn = findViewById(R.id.addToBasketBtn);

        if(intent.getStringExtra("Class").equals("FoodListAdapter")) {
            /*Get the object from FoodListAdapter */
            menuDetailDataModelSentFromIntent = (MenuData) intent.getSerializableExtra("selectedItem");
            priceDouble = getDoublePriceFromMenuDetailModel(menuDetailDataModelSentFromIntent.getPrice());
            /*Set the  name and the subheader texts */
            foodNameSelected.setText(menuDetailDataModelSentFromIntent.getName());
            subheaderFoodSelected.setText(menuDetailDataModelSentFromIntent.getSubheader());
            quantityText.setText(String.valueOf(quantityAdded));

            addToBasketBtn.setText("Add " + quantityAdded + " to basket " + "£" + priceDouble * quantityAdded);
        }

        if(intent.getStringExtra("Class").equals("CheckoutOrderItemsAdapter")){
            removeFromBasketView.setVisibility(View.VISIBLE);
            foodNameToChange = intent.getStringExtra("foodNameToChange");
            quantityToChange = intent.getStringExtra("quantityToChange");
            priceToChange = intent.getStringExtra("priceToChange");

            quantityToUpdate = Integer.parseInt(quantityToChange);
            if(Integer.parseInt(quantityToChange) > 1 ){
                minusBtn.setEnabled(true);
            }
            foodNameSelected.setText(foodNameToChange);
            quantityText.setText(quantityToChange);
            addToBasketBtn.setText("Update Basket "+"£"
                    +df.format(Double.parseDouble(priceToChange) *Integer.parseInt(quantityToChange)));
        }

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minusBtn.setEnabled(true);
                if(intent.getStringExtra("Class").equals("FoodListAdapter")) {
                    quantityAdded++;
                    quantityText.setText(String.valueOf(quantityAdded));
                    addToBasketBtn.setText("Add " + quantityAdded + " to basket "
                            + "£" + df.format(priceDouble * quantityAdded));
                }
                if(intent.getStringExtra("Class").equals("CheckoutOrderItemsAdapter")){
                    quantityToUpdate++;
                    quantityText.setText(String.valueOf(quantityToUpdate));
                    addToBasketBtn.setText("Update Basket "+"£"
                            +df.format(Double.parseDouble(priceToChange) *quantityToUpdate));
                }
            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantityAdded == 1 || quantityToUpdate == 1){
                    minusBtn.setEnabled(false);
                }
                if(intent.getStringExtra("Class").equals("FoodListAdapter")) {
                    quantityAdded--;
                    quantityText.setText(String.valueOf(quantityAdded));
                    addToBasketBtn.setText("Add " + quantityAdded + " to basket "
                            + "£" + df.format(priceDouble * quantityAdded));
                }
                if(intent.getStringExtra("Class").equals("CheckoutOrderItemsAdapter")){
                    quantityToUpdate--;
                    quantityText.setText(String.valueOf(quantityToUpdate));
                    addToBasketBtn.setText("Update Basket "+"£"
                            +df.format(Double.parseDouble(priceToChange) *quantityToUpdate));
                }
            }
        });

        addToBasketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(intent.getStringExtra("Class").equals("FoodListAdapter")) {
                    addToBasketItems();
                    finish();
                }
                if(intent.getStringExtra("Class").equals("CheckoutOrderItemsAdapter")){
                    updateBasket();
                    finish();
                }
            }
        });

        removeFromBasketView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItemFromBasket(quantityToChange, foodNameToChange, priceToChange);

            }
        });

    }// end of onCreate

    private void updateBasket() {
        MyApplication app = (MyApplication) getApplicationContext();
        String price = app.getPrice();
        String quantity = app.getQuantity();
        if(quantityToUpdate != Integer.parseInt(quantityToChange)) {
        /*here i get the total price and subtract the quantity*price sent from CheckoutOrderItemsAdapter
        and add the updated priceToChange*quantityToUpdate. Same for quantity*/
        Double updatedTotalPrice = (Double.parseDouble(price) - Double.parseDouble(quantityToChange)
                    * Double.parseDouble(priceToChange)) + Double.parseDouble(priceToChange) * quantityToUpdate;
        int updatedQuantity = (Integer.parseInt(quantity) - Integer.parseInt(quantityToChange)) + quantityToUpdate;

        Map<String, Map<String,Double>> quantityNamePriceMap = app.getQuantityNamePriceMap();

        Map<String, Double> foodNameAndPriceMap = new HashMap();
        if (quantityNamePriceMap.get(quantityText.getText().toString()) == null) {
            /*Remove the existing entry */
            quantityNamePriceMap.get(quantityToChange).remove(foodNameToChange);
            /*Add the updated entry */
            foodNameAndPriceMap.put(foodNameSelected.getText().toString(),Integer.parseInt(quantityText.getText().toString())*Double.parseDouble(priceToChange));
            quantityNamePriceMap.put(quantityText.getText().toString(),foodNameAndPriceMap);
        }
        else {
            /*Remove the existing entry */
            quantityNamePriceMap.get(quantityToChange).remove(foodNameToChange);
            Objects.requireNonNull(quantityNamePriceMap.get(quantityText.getText().toString()))
                    .put(foodNameSelected.getText().toString(),Integer.parseInt(quantityText.getText().toString())*Double.parseDouble(priceToChange));
        }

        app.setQuantityNamePriceMap(quantityNamePriceMap);
        app.setPrice(String.valueOf(updatedTotalPrice));
        app.setQuantity(String.valueOf(updatedQuantity));
      }
    }

    private void removeItemFromBasket(String quantityToRemove, String foodNameToRemove,String priceToRemove) {
        MyApplication app = (MyApplication) getApplicationContext();
        /*Update Quantity*/
        String existingTotalQuantity = app.getQuantity();
        int updatedQuantity = Integer.parseInt(existingTotalQuantity) - Integer.parseInt(quantityToRemove);
        app.setQuantity(String.valueOf(updatedQuantity));
        /*Update food name */
        ArrayList foodNamesAlreadyInBasket = app.getFoodNames();
        String foodNameFound = "";
        for(Object foodName : foodNamesAlreadyInBasket) {
            if(foodName.equals(foodNameToRemove)) {
                foodNameFound = (String) foodName;
                break;
            }
        }
        foodNamesAlreadyInBasket.remove(foodNameFound);
        /*Update Price */
        String existingTotalPrice = app.getPrice();
        Double updatedPrice = Double.parseDouble(existingTotalPrice) - Integer.parseInt(quantityToRemove) * Double.parseDouble(priceToRemove);
        app.setPrice(String.valueOf(updatedPrice));
        /*Update quantityNamePriceMap */
        app.getQuantityNamePriceMap().get(quantityToRemove).remove(foodNameToRemove);

        /* if the basket is empty navigate to Restaurant Activity and clear all the other activities */
        if(!app.hasBasketItems()){
            Intent intent = new Intent(getApplicationContext(), RestaurantActivity.class);
            startActivity(intent);
        }
        /* if the basket has items go to checkout activity */
        else{
            finish();
        }

    }

    private void addToBasketItems() {
        /*Would be good to write a Junit for this method */
        MyApplication app = (MyApplication) getApplicationContext();
        /*if the basket has already other items then update total price and quantity that is sent to Restaurant/HomeActivities */
        ArrayList foodNamesAlreadyInBasket = app.getFoodNames();
        Map<String, Map<String,Double>> quantityNamePriceMap = app.getQuantityNamePriceMap();
        Map<String, String> instructionToFoodNameMap = app.getInstructionToFoodNameMap();
        Double totalPrice = 0.0;
        if (app.hasBasketItems()){
            String price = app.getPrice();
            String quantity = app.getQuantity();
            totalPrice = (priceDouble*quantityAdded) + Double.parseDouble(price);
            quantityAdded += Integer.parseInt(quantity);
            for (int i = 0; i < Integer.parseInt(quantityText.getText().toString()); i++) {
                foodNamesAlreadyInBasket.add(foodNameSelected.getText().toString());
            }
        }
        else{
            totalPrice = priceDouble * quantityAdded;
            for (int i=1; i<= Integer.parseInt(quantityText.getText().toString()); i++) {
                foodNamesAlreadyInBasket.add(foodNameSelected.getText().toString());
            }
        }
        /*set the instructionToFoodNameMap */
        instructionToFoodNameMap.put(foodNameSelected.getText().toString(), instructionsEditText.getText().toString());
        /*Set the quantityNamePriceMap */
        /*Here i want the map to hold duplicate keys, quantity which is the key can be many times 1 or 2 so
        * keep all the quantities that are the same in the map */
        Map<String, Double> foodNameAndPriceMap = new HashMap();
        if (quantityNamePriceMap.get(quantityText.getText().toString()) == null) {
            foodNameAndPriceMap.put(foodNameSelected.getText().toString(),Integer.parseInt(quantityText.getText().toString())*priceDouble);
            quantityNamePriceMap.put(quantityText.getText().toString(),foodNameAndPriceMap);
        } else {
            Objects.requireNonNull(quantityNamePriceMap.get(quantityText.getText().toString()))
                    .put(foodNameSelected.getText().toString(),Integer.parseInt(quantityText.getText().toString())*priceDouble);
        }
        app.setQuantityNamePriceMap(quantityNamePriceMap);
        app.setFoodNames(foodNamesAlreadyInBasket);
        app.setPrice(String.valueOf(totalPrice));
        app.setQuantity(String.valueOf(quantityAdded));
        app.setInstructionToFoodNameMap(instructionToFoodNameMap);
    }

    private double getDoublePriceFromMenuDetailModel(String price) {
        /*Here I assume that the string price will always be £4.5 i.e pound symbol will always be first and then the price */
        String [] priceParts = price.split("£");
        priceDouble = Double.parseDouble(priceParts[1]);

        return priceDouble;
    }
}
