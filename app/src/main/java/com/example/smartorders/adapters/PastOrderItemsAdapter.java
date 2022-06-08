package com.example.smartorders.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.smartorders.R;
import com.example.smartorders.fragments.PastOrdersFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PastOrderItemsAdapter extends RecyclerView.Adapter<PastOrderItemsAdapter.MyViewHolder> {
    private final LayoutInflater inflater;
    private final Context context;
    private final List<PastOrdersFragment.PastOrderItemsListHelper> orderItemsList;

    public PastOrderItemsAdapter(Context context,List<PastOrdersFragment.PastOrderItemsListHelper> orderItemsList) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.orderItemsList=orderItemsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_items_on_past_orders, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PastOrdersFragment.PastOrderItemsListHelper orderItemsListHelper = orderItemsList.get(position);
        holder.deliveryTitle.setText(orderItemsListHelper.getDeliveryTitle());
        holder.totalPrice.setText("Total: "+orderItemsListHelper.getTotalPrice());
        holder.imageViewLogo.setImageResource(orderItemsListHelper.getImageView());
        holder.orderCompletedTitle.setText(orderItemsListHelper.getOrderCompletedTitle());
        holder.orderID.setText(orderItemsListHelper.getOrderId());
        Map<String,String> pastOrderItemsNameToQuantity = orderItemsListHelper.getPastOrderItemsNameToQuantity();
        ArrayList<PastOrderListOfItemsAdapter.PastOrderItemModel> helperList = new ArrayList<>();
        for(Map.Entry<String,String> entry : pastOrderItemsNameToQuantity.entrySet()){
            PastOrderListOfItemsAdapter.PastOrderItemModel orderItemsHelper = new PastOrderListOfItemsAdapter.PastOrderItemModel();
            orderItemsHelper.setPastOrderItemName(entry.getKey());
            orderItemsHelper.setPastOrderItemQuantity(entry.getValue());
            helperList.add(orderItemsHelper);
        }
        PastOrderListOfItemsAdapter adapter = new PastOrderListOfItemsAdapter(context,helperList);
        holder.orderItems.setAdapter(adapter);
        holder.orderItems.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // Disallow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                    // Allow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            // Handle ListView touch events.
            v.onTouchEvent(event);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return orderItemsList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView deliveryTitle;
        TextView totalPrice;
        ImageView imageViewLogo;
        TextView orderCompletedTitle;
        TextView orderID;
        ListView orderItems;

        public MyViewHolder(View itemView) {
            super(itemView);
            deliveryTitle =  itemView.findViewById(R.id.deliveryTitle);
            totalPrice =  itemView.findViewById(R.id.totalPrice);
            imageViewLogo = itemView.findViewById(R.id.imageViewPastOrders);
            orderCompletedTitle =  itemView.findViewById(R.id.orderCompletedTitle);
            orderID =  itemView.findViewById(R.id.orderID);
            orderItems = itemView.findViewById(R.id.orderListPastOrders);
        }
    }
}

