package com.example.smartorders;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartorders.activity.AddToBasketActivity;
import com.example.smartorders.activity.RestaurantActivity;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class FoodListAdapter extends RecyclerView.Adapter{
    private final Context context;
    private List<MenuDetailDataModel> modifiedReceivedList;

    public FoodListAdapter(Context context, List<MenuDetailDataModel> modifiedReceivedList) {
        this.context = context;
        this.modifiedReceivedList = modifiedReceivedList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_list, parent, false);
        TextItemViewHolder textItemViewHolder = new TextItemViewHolder(view);
        return textItemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TextItemViewHolder textItemViewHolder = (TextItemViewHolder)holder;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "FoodListAdapter " + modifiedReceivedList.get(position).getName(), Toast.LENGTH_SHORT).show();
                /*User selected/clicked on one item from the food menu start add to basket activity */
                MenuDetailDataModel selectedItem = modifiedReceivedList.get(position);
                Intent i = new Intent(context, AddToBasketActivity.class);
                i.putExtra("selectedItem", (Serializable) selectedItem);
                i.putExtra("Class","FoodListAdapter");
                if (context instanceof RestaurantActivity) {
                    //context.startActivity(i);
                    ((RestaurantActivity)context).startActivityForResult(i, 1);
                    //i.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                }

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
