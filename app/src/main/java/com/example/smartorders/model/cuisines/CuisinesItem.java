package com.example.smartorders.model.cuisines;

import org.parceler.Parcel;

@Parcel
public class CuisinesItem {

    public Cuisines cuisines;

    public void setCuisines(Cuisines cuisines){
        this.cuisines = cuisines;
    }

    public Cuisines getCuisines(){
        return cuisines;
    }
}

