package com.example.smartorders.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartorders.R;
import com.example.smartorders.activities.VerificationActivity;
import com.example.smartorders.models.SingleInstanceUser;
import com.mukesh.OtpView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EnterOTPFragment extends Fragment {

    private String verificationCode;
    private static final String TAG ="EnterOTPFragment";
    private final SingleInstanceUser authCredentialSingleInstance = SingleInstanceUser.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.enter_otp_fragment, container, false);
        Button otpBtn = rootView.findViewById(R.id.buttonNext);
        /*Hide some of the buttons/ textviews etc */
        TextView enterCodeText = rootView.findViewById(R.id.enterCodeView);
        enterCodeText.setVisibility(View.GONE);
        Button resendButton = rootView.findViewById(R.id.resendButton);
        resendButton.setVisibility(View.GONE);
        OtpView otpView = rootView.findViewById(R.id.otp_view);
        otpView.setOtpCompletionListener(otp -> {

            Log.d("onOtpCompleted= TYPED", otp);
            verificationCode = otp;
        });
        otpView.setVisibility(View.GONE);


        enterCodeText.setVisibility(View.VISIBLE);
        otpView.setVisibility(View.VISIBLE);
        resendButton.setVisibility(View.VISIBLE);


        ((VerificationActivity) getActivity()).startPhoneNumberVerification(((VerificationActivity) getActivity()).getPhoneNumber());

        otpBtn.setOnClickListener(view -> {
            final String phoneNumberRetrieved = ((VerificationActivity) getActivity()).getPhoneNumber();
            authCredentialSingleInstance.setPhoneNumber(phoneNumberRetrieved);
            ((VerificationActivity) getActivity()).setCurrentItem(1, true);
            ((VerificationActivity) getActivity()).verifyPhoneNumberWithCode(((VerificationActivity) getActivity()).getmVerificationId(), verificationCode, phoneNumberRetrieved);
        });

        resendButton.setOnClickListener(view -> {
            final String phoneNumberRetrieved = ((VerificationActivity) getActivity()).getPhoneNumber();
            Toast.makeText(getContext(), "Resend Button Clicked", Toast.LENGTH_SHORT).show();
            ((VerificationActivity) getActivity()).resendVerificationCode(phoneNumberRetrieved, ((VerificationActivity) getActivity()).getmResendToken());
        });
        //}

        return rootView;
    }

}

