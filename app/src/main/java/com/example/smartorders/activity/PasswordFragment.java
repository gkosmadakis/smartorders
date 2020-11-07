package com.example.smartorders.activity;

import android.app.Activity;
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

public class PasswordFragment extends Fragment {

    private storeDataListener storeDataListener;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);
            final View rootView = inflater.inflate(R.layout.password_fragment, container, false);
            Button buttonInFragment1 = rootView.findViewById(R.id.buttonNext);
            final EditText password = rootView.findViewById(R.id.password);

            if (TextUtils.isEmpty(password.getText().toString()) || password.getText().toString().length() < 6 ){
                password.setError("Please enter a password with 6 minimum characters");
            }

                buttonInFragment1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((VerificationActivity) getActivity()).setCurrentItem(3, true);
                        storeDataListener.storePassword(password.getText().toString());
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

