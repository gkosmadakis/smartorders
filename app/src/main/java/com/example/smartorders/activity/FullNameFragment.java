package com.example.smartorders.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smartorders.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FullNameFragment extends Fragment {

    private storeDataListener storeDataListener;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);
            View rootView = inflater.inflate(R.layout.full_name_fragment, container, false);
            Button buttonInFragment1 = rootView.findViewById(R.id.buttonNext);
            final EditText firstName = rootView.findViewById(R.id.firstName);
            final EditText lastName = rootView.findViewById(R.id.lastName);

            if(TextUtils.isEmpty(firstName.getText().toString())){
                firstName.setError("Please enter your first name");
            }
            else if(TextUtils.isEmpty(lastName.getText().toString())){
                lastName.setError("Please enter your last name");
            }

                buttonInFragment1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((VerificationActivity) getActivity()).setCurrentItem(4, true);
                        storeDataListener.storeFullName(firstName.getText().toString(), lastName.getText().toString());
                    }
                });

            return rootView;
        }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            storeDataListener = (storeDataListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    }

