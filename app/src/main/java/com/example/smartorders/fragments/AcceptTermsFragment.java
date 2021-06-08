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
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AcceptTermsFragment extends Fragment {
    private FirebaseAuth mAuth;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);
            mAuth = FirebaseAuth.getInstance();

            View rootView = inflater.inflate(R.layout.accept_terms_fragment, container, false);
            Button buttonInFragment1 = rootView.findViewById(R.id.buttonNext);
            buttonInFragment1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((VerificationActivity)getActivity()).setCurrentItem (5, true);

                    Intent intent = new Intent(view.getContext(), HomeActivity.class);
                    startActivity(intent);

                }
            });

            return rootView;
        }
    }

