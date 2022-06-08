package com.example.smartorders.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartorders.R;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private final Context _context;
    private final List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private final HashMap<String, List<String>> _listDataChild;
    public static HashMap<String,String> arrayList;
    boolean notifyDataSetChangedCalled;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.delivery_options_child, null);
        }
        EditText txtListChild = convertView.findViewById(R.id.instructionsField);
        if (notifyDataSetChangedCalled) {
            if (childText != "") {
                txtListChild.setText(childText);
            }
        }
        else {
            txtListChild.setHint(childText);
        }
        txtListChild.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                String instruction = ((EditText) view).getText().toString();
                arrayList = new HashMap<String, String>();
                System.out.println("Instruction is " + instruction);
                arrayList.put(String.valueOf(groupPosition), instruction);
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.delivery_options_list, null);
        }
        ImageView img_selection = convertView.findViewById(R.id.tickedIcon);
        if (isExpanded) {
            int imageResourceId = R.drawable.baseline_done_black_18dp;
            img_selection.setImageResource(imageResourceId);
            img_selection.setVisibility(View.VISIBLE);
        } else {
            img_selection.setVisibility(View.GONE);
        }
        TextView lblListHeader = convertView.findViewById(R.id.deliveryOptionsView);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        String s = _listDataHeader.get(groupPosition);
        ImageView imageView = convertView.findViewById(R.id.lineIcon);
        switch (s) {
            case "Meet Outside":
                imageView.setImageResource(R.drawable.baseline_directions_car_black_18dp);
                break;
            case "Meet at door":
                imageView.setImageResource(R.drawable.baseline_person_black_18dp);
                break;
            case "Leave at door":
                imageView.setImageResource(R.drawable.baseline_meeting_room_black_18dp);
                break;
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void notifyDataSetChangedWrapper(){
        notifyDataSetChangedCalled = true;
        notifyDataSetChanged();
    }

}
