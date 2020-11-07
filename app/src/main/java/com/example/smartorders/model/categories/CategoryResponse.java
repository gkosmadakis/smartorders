package com.example.smartorders.model.categories;
import org.parceler.Parcel;

import java.util.List;

    @Parcel
    public class CategoryResponse {

        public List<CategoriesItem> categories;

        public void setCategories(List<CategoriesItem> categories){
            this.categories = categories;
        }

        public List<CategoriesItem> getCategories(){
            return categories;
        }
    }

