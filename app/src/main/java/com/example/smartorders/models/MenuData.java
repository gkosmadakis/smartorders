package com.example.smartorders.models;

import java.io.Serializable;

public class MenuData implements Serializable{

    private String category;
    private String name;
    private String subheader;
    private String description;
    private String price;

    public MenuData(String name, String subheader, String description, String price) {
        this.name = name;
        this.subheader = subheader;
        this.description = description;
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubheader() {
        return subheader;
    }

    public void setSubheader(String subheader) {
        this.subheader = subheader;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
