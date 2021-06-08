package com.example.smartorders.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartorders.R;
import com.example.smartorders.activities.VerificationActivity;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EnterPasswordOrCodeFragment extends Fragment {

    private OtpView otpView;
    private String verificationCode;
    private View rootView;
    private EditText password;
    private Button resendButton;
    private Button buttonNext;
    private TextView welcomeText, enterCodeText;
    private static final String TAG ="EnterPassdCodeFragment";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.enter_code_or_existing_user_fragment, container, false);
        buttonNext = rootView.findViewById(R.id.buttonNext);
        /*Hide some of the buttons/ textviews etc */
        welcomeText = rootView.findViewById(R.id.welcomeView);
        welcomeText.setVisibility(View.GONE);
        password = rootView.findViewById(R.id.existingPassword);
        password.setVisibility(View.GONE);
        enterCodeText = rootView.findViewById(R.id.enterCodeView);
        enterCodeText.setVisibility(View.GONE);
        resendButton = rootView.findViewById(R.id.resendButton);
        resendButton.setVisibility(View.GONE);
        otpView = rootView.findViewById(R.id.otp_view);
        otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {

                Log.d("onOtpCompleted= TYPED", otp);
                verificationCode = otp;
            }
        });
        otpView.setVisibility(View.GONE);

        if (((VerificationActivity) getActivity()).isUserRegistered()) {
            welcomeText.setVisibility(View.VISIBLE);
            password.setVisibility(View.VISIBLE);

        } else {
            enterCodeText.setVisibility(View.VISIBLE);
            otpView.setVisibility(View.VISIBLE);
            resendButton.setVisibility(View.VISIBLE);
        }

        if (((VerificationActivity) getActivity()).isUserRegistered()) {
            ((VerificationActivity) getActivity()).retrieveEmailFromDB(((VerificationActivity) getActivity()).getPhoneNumber());
            buttonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (TextUtils.isEmpty(password.getText().toString()) || password.getText().toString().length() < 6) {
                        password.setError("Please enter a password with 6 minimum characters");
                    } else {
                        final String phoneNumberRetrieved = ((VerificationActivity) getActivity()).getPhoneNumber();

                        ((VerificationActivity) getActivity()).startSignIn(phoneNumberRetrieved, password.getText().toString());
                    }
                }
            });
        } else {
            ((VerificationActivity) getActivity()).startPhoneNumberVerification(((VerificationActivity) getActivity()).getPhoneNumber());

            buttonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String phoneNumberRetrieved = ((VerificationActivity) getActivity()).getPhoneNumber();
                    ((VerificationActivity) getActivity()).setCurrentItem(1, true);
                    ((VerificationActivity) getActivity()).verifyPhoneNumberWithCode(((VerificationActivity) getActivity()).getmVerificationId(), verificationCode, phoneNumberRetrieved);
                }
            });
            resendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String phoneNumberRetrieved = ((VerificationActivity) getActivity()).getPhoneNumber();
                    Toast.makeText(getContext(), "Resend Button Clicked", Toast.LENGTH_SHORT).show();
                    ((VerificationActivity) getActivity()).resendVerificationCode(phoneNumberRetrieved, ((VerificationActivity) getActivity()).getmResendToken());

                }
            });
        }

        return rootView;
    }

}

