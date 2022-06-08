package com.example.smartorders.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.smartorders.R;
import com.example.smartorders.viewmodels.SearchViewModel;

public class SearchFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SearchViewModel dashboardViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        final TextView textView = root.findViewById(R.id.text_search);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));
        return root;
    }

}
