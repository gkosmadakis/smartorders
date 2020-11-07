package com.example.smartorders;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smartorders.activity.VerificationActivity;
import com.example.smartorders.activity.storeDataListener;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EmailAddressFragment extends Fragment {

    private storeDataListener storeDataListener;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);
            View rootView = inflater.inflate(R.layout.enter_email_address_fragment, container, false);
            Button buttonInFragment1 = rootView.findViewById(R.id.buttonNext);
            final EditText emailAddress = rootView.findViewById(R.id.emailAddress);

            if (TextUtils.isEmpty(emailAddress.getText().toString())){
                emailAddress.setError("Please Enter an email address");
            }
            else if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress.getText().toString()).matches()) {
                emailAddress.setError("Please Enter a valid email address");
            }

                buttonInFragment1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((VerificationActivity) getActivity()).setCurrentItem(2, true);
                        storeDataListener.storeEmailAddress(emailAddress.getText().toString());

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

