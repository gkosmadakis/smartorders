package com.example.smartorders.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.smartorders.R;
import com.example.smartorders.adapters.FoodListAdapter;
import com.example.smartorders.models.MenuData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PageFragment extends Fragment {

    private Map<Integer, MenuData> modifiedReceivedListMap;
    private String headerRequested;

    public static PageFragment newInstance(int page, String headerRequested,Map<Integer, MenuData> menuDetailsListMapRequested) {
        Bundle args = new Bundle();
        args.putSerializable("List", (Serializable) menuDetailsListMapRequested);
        args.putString("headerRequested", headerRequested);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        modifiedReceivedListMap = (Map<Integer, MenuData>) getArguments().getSerializable("List");
        headerRequested = getArguments().getString("headerRequested");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView listView = view.findViewById(R.id.foodListView);
        TextView categoryText = view.findViewById(R.id.categoryText);
        /*Set the menu category from the keys of the map */
        categoryText.setText(headerRequested);
        final FoodListAdapter adapter = new FoodListAdapter(getContext(), new ArrayList<>(modifiedReceivedListMap.values()) );
        listView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        listView.setLayoutManager(layoutManager);
    }

}
