package com.example.smartorders.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartorders.R;

import java.util.LinkedHashMap;

public class PaymentInfoAdapter extends BaseAdapter {
    private final Context context;
    private final LinkedHashMap<String, String> cardData;
    private final String[] mKeys;

    public PaymentInfoAdapter(Context context, LinkedHashMap<String, String> data) {
        this.context = context;
        cardData  = data;
        mKeys = cardData.keySet().toArray(new String[data.size()]);
    }

    @Override
    public int getCount() {
        return cardData.size();
    }

    @Override
    public Object getItem(int position) {
        return cardData.get(mKeys[position]);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       ImageView imageView = null;
       View rowView = null;
       rowView = inflater.inflate(R.layout.payment_items_list, parent, false);
       TextView textView =  rowView.findViewById(R.id.last4DisigitsView);
       imageView =  rowView.findViewById(R.id.cardIcon);
       textView.setText("\u2022\u2022\u2022\u2022 "+mKeys[position]);
        // Change the icons
        String s = getItem(position).toString();
        switch (s){
            case "MasterCard":
            imageView.setImageResource(R.drawable.mastercard);
                break;
            case "Visa":
            imageView.setImageResource(R.drawable.visa);
                break;
            case "American Express":
            imageView.setImageResource(R.drawable.americanexpress);
                break;
            case "Discover":
            imageView.setImageResource(R.drawable.discover);
                break;
            case "Diners Club":
            imageView.setImageResource(R.drawable.dinersclub);
                break;

        }
        return rowView;
    }

}
