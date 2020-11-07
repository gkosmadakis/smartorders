package com.example.smartorders.model.cuisines;
import org.parceler.Parcel;

@Parcel
public class Cuisines {

    public String name;
    public int id;

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }
}

