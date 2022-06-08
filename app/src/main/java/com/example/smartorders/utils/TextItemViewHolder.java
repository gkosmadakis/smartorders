package com.example.smartorders.utils;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.smartorders.R;

public class TextItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView nameText;
    private final TextView subheaderText;
    private final TextView descriptionText;
    private final TextView priceText;


    public TextItemViewHolder(View itemView) {
        super(itemView);
        nameText = itemView.findViewById(R.id.nameText);
        subheaderText = itemView.findViewById(R.id.subheaderText);
        descriptionText = itemView.findViewById(R.id.descriptionText);
        priceText = itemView.findViewById(R.id.priceText);
    }

    public void bind(String name,String subheader,String description,String price) {
        nameText.setText(name);
        subheaderText.setText(subheader);
        descriptionText.setText(description);
        priceText.setText(price);
    }

}
