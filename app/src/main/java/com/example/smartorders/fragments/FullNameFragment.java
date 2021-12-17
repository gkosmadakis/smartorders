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
import com.example.smartorders.service.UserServiceImpl;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FullNameFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.full_name_fragment, container, false);
        Button firstAndLastNameBtn = rootView.findViewById(R.id.buttonNext);
        final EditText firstName = rootView.findViewById(R.id.firstName);
        final EditText lastName = rootView.findViewById(R.id.lastName);
        SingleInstanceUser authCredential = SingleInstanceUser.getInstance();
        if(TextUtils.isEmpty(firstName.getText().toString())){
            firstName.setError("Please enter your first name");
        }
        else if(TextUtils.isEmpty(lastName.getText().toString())){
            lastName.setError("Please enter your last name");
        }

        firstAndLastNameBtn.setOnClickListener(view -> {
            ((VerificationActivity) getActivity()).setCurrentItem(4, true);
            UserServiceImpl userService = new UserServiceImpl();
            // Create EmailAuthCredential with email and password
            AuthCredential credential = EmailAuthProvider.getCredential(authCredential.getEmail(), authCredential.getPassword());
            /* Here i link the user with their phone along with their email
             * The user has just finished the registration at this step they just entered First and last name*/
            userService.linkUserWithPhoneCredential(credential, getContext());
            //set singleton object with appropriate values
            authCredential.setFirstName(firstName.getText().toString());
            authCredential.setLastName(lastName.getText().toString());
        });

        return rootView;
    }

}

