package com.example.smartorders.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.smartorders.R;
import com.example.smartorders.utils.TextItemViewHolder;
import com.example.smartorders.activities.AddToBasketActivity;
import com.example.smartorders.activities.RestaurantActivity;
import com.example.smartorders.models.MenuData;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class FoodListAdapter extends RecyclerView.Adapter{
    private final Context context;
    private final List<MenuData> modifiedReceivedList;

    public FoodListAdapter(Context context, List<MenuData> modifiedReceivedList) {
        this.context = context;
        this.modifiedReceivedList = modifiedReceivedList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_list, parent, false);
        return new TextItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TextItemViewHolder textItemViewHolder = (TextItemViewHolder)holder;
        holder.itemView.setOnClickListener(view -> {
            Toast.makeText(context, "FoodListAdapter " + modifiedReceivedList.get(position).getName(), Toast.LENGTH_SHORT).show();
            /*User selected/clicked on one item from the food menu start add to basket activity */
            MenuData selectedItem = modifiedReceivedList.get(position);
            Intent i = new Intent(context, AddToBasketActivity.class);
            i.putExtra("selectedItem", (Serializable) selectedItem);
            i.putExtra("Class","FoodListAdapter");
            if (context instanceof RestaurantActivity) {
                ((RestaurantActivity)context).startActivityForResult(i, 1);
            }
        });
        textItemViewHolder.bind(modifiedReceivedList.get(position).getName(),modifiedReceivedList.get(position).getSubheader(),
                modifiedReceivedList.get(position).getDescription(),modifiedReceivedList.get(position).getPrice());
    }

    @Override
    public int getItemCount() {
        return modifiedReceivedList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
