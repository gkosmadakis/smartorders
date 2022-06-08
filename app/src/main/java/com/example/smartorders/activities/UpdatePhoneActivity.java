package com.example.smartorders.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartorders.R;
import com.example.smartorders.service.UserService;
import com.example.smartorders.service.UserServiceImpl;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OtpView;

import java.util.concurrent.TimeUnit;

public class UpdatePhoneActivity extends AppCompatActivity {
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private static final String TAG = "UpdatePhoneActivity";
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String verificationCode;
    private String newPhoneNumber;
    private final UserService userService = new UserServiceImpl();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_phone);
        OtpView otpView = findViewById(R.id.otpView);
        Button resendCodeBtn = findViewById(R.id.resendButton);
        otpView.setOtpCompletionListener(otp -> {
            Log.d("onOtpCompleted=>", otp);
            verificationCode = otp;
            /*delete user from authentication and delete user id from real time database*/
            deleteCurrentUser();
            /*update user with the new phone number*/
            verifyPhoneNumberWithCode(mVerificationId,verificationCode);
        });
        resendCodeBtn.setOnClickListener(view -> resendVerificationCode(newPhoneNumber,mResendToken));
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                mVerificationInProgress = false;
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                mVerificationInProgress = false;
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
        // [END phone_auth_callbacks]
    }//end of onCreate

    private void deleteCurrentUser() {
        userService.deleteUserFromFirebase(UpdatePhoneActivity.this);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            verifyWithPhoneAuthCredential(credential);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void verifyWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        userService.updateUserPhoneWithCredential(UpdatePhoneActivity.this, credential);
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = getIntent();
        newPhoneNumber = intent.getStringExtra("phoneNumber");
        startPhoneNumberVerification(newPhoneNumber);
    }

    public void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]
        Toast.makeText(this, "startPhoneNumberVerification", Toast.LENGTH_SHORT).show();
        mVerificationInProgress = true;
    }

    public void resendVerificationCode(String phoneNumber,
                                       PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
        Toast.makeText(this,"resendVerificationCode", Toast.LENGTH_LONG);
    }
}
