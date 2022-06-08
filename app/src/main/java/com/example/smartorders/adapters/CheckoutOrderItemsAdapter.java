package com.example.smartorders.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartorders.R;
import com.example.smartorders.activities.AddToBasketActivity;
import com.example.smartorders.activities.CheckoutActivity;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CheckoutOrderItemsAdapter extends RecyclerView.Adapter<CheckoutOrderItemsAdapter.MyViewHolder> {
    private final LayoutInflater inflater;
    private final Context context;
    private final List<CheckoutActivity.OrderItemsListHelper> orderItemsList;

    public CheckoutOrderItemsAdapter(Context context,List<CheckoutActivity.OrderItemsListHelper> orderItemsList) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.orderItemsList = orderItemsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_items_on_checkout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CheckoutActivity.OrderItemsListHelper orderItemsListHelper = orderItemsList.get(position);
        holder.quantity.setText(orderItemsListHelper.getQuantity()+"X");
        holder.foodName.setText(orderItemsListHelper.getFoodName());
        holder.price.setText("£"+orderItemsListHelper.getPrice());
        holder.itemView.setOnClickListener(view -> {
            Toast.makeText(context,"CheckoutOrderItemsAdapter " +orderItemsListHelper.getQuantity()+
                    orderItemsListHelper.getFoodName()+orderItemsListHelper.getPrice(),Toast.LENGTH_SHORT).show();
            Intent i = new Intent(context, AddToBasketActivity.class);
            i.putExtra("quantityToChange", (Serializable) orderItemsListHelper.getQuantity());
            i.putExtra("foodNameToChange", (Serializable) orderItemsListHelper.getFoodName());
            Double priceOfOneItem = Double.parseDouble(orderItemsListHelper.getPrice())/
                    Integer.parseInt(orderItemsListHelper.getQuantity());
            i.putExtra("priceToChange", (Serializable) String.valueOf(priceOfOneItem));
            i.putExtra("Class","CheckoutOrderItemsAdapter");
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return orderItemsList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView quantity;
        TextView foodName;
        TextView price;

        public MyViewHolder(View itemView) {
            super(itemView);
            quantity =  itemView.findViewById(R.id.quantityView);
            foodName =  itemView.findViewById(R.id.foodNameView);
            price = itemView.findViewById(R.id.priceView);
        }
    }
}
