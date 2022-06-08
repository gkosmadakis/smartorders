package com.example.smartorders.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.smartorders.R;
import com.example.smartorders.activities.VerificationActivity;
import com.example.smartorders.models.SingleInstanceUser;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PasswordFragment extends Fragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);
            final View rootView = inflater.inflate(R.layout.password_fragment, container, false);
            Button passwordBtn = rootView.findViewById(R.id.buttonNext);
            final EditText password = rootView.findViewById(R.id.password);
            if (TextUtils.isEmpty(password.getText().toString()) || password.getText().toString().length() < 6 ){
                password.setError("Please enter a password with 6 minimum characters");
            }
            passwordBtn.setOnClickListener(view -> {
                ((VerificationActivity) getActivity()).setCurrentItem(3, true);
                SingleInstanceUser authCredentialSingleInstance = SingleInstanceUser.getInstance();
                //set singleton object with appropriate values
                authCredentialSingleInstance.setPassword(password.getText().toString());
            });
            return rootView;
        }

}

