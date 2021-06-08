package com.example.smartorders.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartorders.R;

public class PastOrderListOfItemsAdapter extends BaseAdapter {
    private final Context context;
    List<PastOrderItemModel> itemsOrdered;

    public PastOrderListOfItemsAdapter(Context context, List<PastOrderItemModel> itemsOrdered) {
        this.context = context;
        if (itemsOrdered == null) {
            throw new IllegalArgumentException(
                    "List of items order must not be null");
        }
        this.itemsOrdered = itemsOrdered;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PastOrderItemModel orderItemName = itemsOrdered.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = null;
        rowView = inflater.inflate(R.layout.row_with_item_order, parent, false);
        TextView textViewName =  rowView.findViewById(R.id.itemOrderedName);
        TextView textViewQuantity =  rowView.findViewById(R.id.itemOrderedQuantity);

        textViewQuantity.setText(orderItemName.getPastOrderItemQuantity());
        textViewName.setText(orderItemName.getPastOrderItemName());

        return rowView;
    }

    @Override
    public int getCount() {
        return itemsOrdered.size();
    }

    @Override
    public Object getItem(int position) {
        return itemsOrdered.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

   public final static class PastOrderItemModel {
        private String pastOrderItemName;
        private String pastOrderItemQuantity;

        public PastOrderItemModel() {
        }

        public String getPastOrderItemName() {
            return pastOrderItemName;
        }

        public void setPastOrderItemName(String pastOrderItemName) {
            this.pastOrderItemName = pastOrderItemName;
        }

       public String getPastOrderItemQuantity() {
           return pastOrderItemQuantity;
       }

       public void setPastOrderItemQuantity(String pastOrderItemQuantity) {
           this.pastOrderItemQuantity = pastOrderItemQuantity;
       }
   }

}





