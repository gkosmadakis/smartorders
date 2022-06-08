package com.example.smartorders.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.smartorders.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Timer;
import java.util.TimerTask;

public class EnterPhoneActivity extends AppCompatActivity {
    EditText phoneField;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "EnterPhoneActivity";
    private Timer timer = new Timer();
    private final long DELAY = 250; // in ms
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_phone);
        Button startVerificationBtn = findViewById(R.id.startVerificationButton);
        phoneField = findViewById(R.id.phoneNumberField);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> Log.d(TAG, "mAuthListener called "+firebaseAuth.getCurrentUser() + "mAuth " + mAuth.getCurrentUser());
        /* get the phone as soon as the user has finished typing it */
        phoneField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(timer != null)
                    timer.cancel();
            }
            @Override
            public void afterTextChanged(final Editable s) {
                if (s.length() >= 3) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            phoneNumber = phoneField.getText().toString();
                            Log.d(TAG, "User finished typing");
                        }
                    }, DELAY);
                }
            }
        });
        startVerificationBtn.setOnClickListener(view -> {
            Intent i = new Intent(EnterPhoneActivity.this, VerificationActivity.class);
            if (phoneField.getText().toString().length() > 0) {
                i.putExtra("phoneNumber", phoneNumber);
            }
            startActivity(i);
        });
    }//end of onCreate

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthListener);
    }

}