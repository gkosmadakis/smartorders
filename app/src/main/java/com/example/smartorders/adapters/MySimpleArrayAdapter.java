package com.example.smartorders.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartorders.R;
import com.example.smartorders.activities.FindAddressActivity;
import com.example.smartorders.activities.PaymentInfoActivity;
import com.example.smartorders.activities.SettingsActivity;

import java.util.Arrays;
import java.util.List;

public class MySimpleArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    boolean notifyDataSetChangedCalled;
    private final String [] savedPlaces;

    public MySimpleArrayAdapter(Context context, String[] values,String [] savedPlaces) {
        super(context, R.layout.my_list, values);
        this.context = context;
        this.values = values;
        this.savedPlaces = savedPlaces;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView imageView = null;
        View rowView = null;
        // Convert String Array to List
        List<String> list = Arrays.asList(values);
        if (context instanceof FindAddressActivity){
           rowView = inflater.inflate(R.layout.address_fields, parent, false);
           EditText editText = rowView.findViewById(R.id.addressListItem);
           if (notifyDataSetChangedCalled){
               editText.setText(values[position]);
               editText.setHint(list.get(position));
           }
           else {
               editText.setHint(list.get(position));
           }
        }
        else if(context instanceof SettingsActivity) {
            rowView = inflater.inflate(R.layout.my_list, parent, false);
            TextView textView =  rowView.findViewById(R.id.textView);
            imageView =  rowView.findViewById(R.id.icon);
            textView.setText(values[position]);
            TextView childTextView = rowView.findViewById(R.id.savedPlaceChildView);
            if (savedPlaces != null) {
                childTextView.setText(savedPlaces[position]);
            }
        }
        else if(context instanceof PaymentInfoActivity) {
            rowView = inflater.inflate(R.layout.payment_items_list, parent, false);
            TextView textView =  rowView.findViewById(R.id.last4DisigitsView);
            imageView =  rowView.findViewById(R.id.cardIcon);
            textView.setText(values[position]);
        }
        else{
            rowView = inflater.inflate(R.layout.settings_items_list, parent, false);
            TextView textView =  rowView.findViewById(R.id.settingItemView);
            imageView =  rowView.findViewById(R.id.settingIcon);
            textView.setText(values[position]);
        }
        // Change the icons
        String s = values[position];
        switch (s){
            case "Your favourites":
            imageView.setImageResource(R.drawable.baseline_favorite_black_18dp);
            break;
            case "Payment":
            imageView.setImageResource(R.drawable.baseline_payment_black_18dp);
            break;
            case "Help":
            imageView.setImageResource(R.drawable.baseline_help_black_18dp);
            break;
            case "Promotions":
            imageView.setImageResource(R.drawable.baseline_local_offer_black_18dp);
            break;
            case "Settings":
            imageView.setImageResource(R.drawable.baseline_settings_black_18dp);
            break;
            case "Home":
            imageView.setImageResource(R.drawable.baseline_home_black_18dp);
            break;
            case "Work":
            imageView.setImageResource(R.drawable.baseline_work_outline_black_18dp);
            break;
            case "Mastercard":
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
        if(position == list.size()){
            notifyDataSetChangedCalled = false;
        }
        return rowView;
    }

    public void notifyDataSetChangedWrapper(){
        notifyDataSetChangedCalled = true;
        notifyDataSetChanged();
    }
}
