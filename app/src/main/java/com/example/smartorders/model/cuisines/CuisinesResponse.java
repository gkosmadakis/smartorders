package com.example.smartorders.model.cuisines;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class CuisinesResponse {

    public List<CuisinesItem> cuisinesItems;

    public void setCuisinesItems(List<CuisinesItem> cuisinesItems){
        this.cuisinesItems = cuisinesItems;
    }

    public List<CuisinesItem> getCuisinesItems(){
        return cuisinesItems;
    }
}

