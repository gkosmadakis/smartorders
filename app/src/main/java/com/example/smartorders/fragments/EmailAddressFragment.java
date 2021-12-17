package com.example.smartorders.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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

public class EmailAddressFragment extends Fragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);
            View rootView = inflater.inflate(R.layout.enter_email_address_fragment, container, false);
            Button emailBtn = rootView.findViewById(R.id.buttonNext);
            final EditText emailAddress = rootView.findViewById(R.id.emailAddress);

            if (TextUtils.isEmpty(emailAddress.getText().toString())){
                emailAddress.setError("Please Enter an email address");
            }
            else if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress.getText().toString()).matches()) {
                emailAddress.setError("Please Enter a valid email address");
            }

            emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((VerificationActivity) getActivity()).setCurrentItem(2, true);
                SingleInstanceUser authCredentialSingleInstance = SingleInstanceUser.getInstance();
                // set singleton object with appropriate values
                authCredentialSingleInstance.setEmail(emailAddress.getText().toString());
                }
            });

            return rootView;
        }

}

