package com.example.smartorders.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.smartorders.R;
import com.example.smartorders.activities.HomeActivity;
import com.example.smartorders.activities.VerificationActivity;
import com.example.smartorders.models.SingleInstanceUser;
import com.example.smartorders.service.UserServiceImpl;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AcceptTermsFragment extends Fragment {

    @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);
            View rootView = inflater.inflate(R.layout.accept_terms_fragment, container, false);
            Button acceptTermsBtn = rootView.findViewById(R.id.buttonNext);
            acceptTermsBtn.setOnClickListener(view -> {
                ((VerificationActivity)getActivity()).setCurrentItem (5, true);
                // user has accepted terms and conditions so add the user to realtime database
                UserServiceImpl userService = new UserServiceImpl();
                SingleInstanceUser authCredentialSingleInstance = SingleInstanceUser.getInstance();
                userService.addUserToRealtimeDatabase(authCredentialSingleInstance);
                // call storeUserDetailsToSharedPrefs here to store user details to shared preferences
                userService.storeUserDetailsToSharedPrefs(authCredentialSingleInstance.getPassword(), getContext(), authCredentialSingleInstance.getuID());
                // start Home Activity
                Intent intent = new Intent(view.getContext(), HomeActivity.class);
                startActivity(intent);
            });

            return rootView;
        }
    }

