package com.example.smartorders.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.smartorders.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class EnterPhoneActivity extends AppCompatActivity {
    EditText phoneField;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "EnterPhoneActivity";
    private Timer timer = new Timer();
    private final long DELAY = 250; // in ms
    private String phoneNumber;
    boolean userIsRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_phone);

        Button startVerificationBtn = findViewById(R.id.startVerificationButton);
        phoneField = findViewById(R.id.phoneNumberField);
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            Log.d(TAG, "mAuthListener called "+firebaseAuth.getCurrentUser());

            }
        };
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
                            // TODO: do what you need here (refresh list)
                            phoneNumber = phoneField.getText().toString();
                            Log.d(TAG, "User finished typing");
                            checkIfUserIsRegisteredInFirebase();
                        }
                    }, DELAY);
                }
            }
        });

        startVerificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(EnterPhoneActivity.this, VerificationActivity.class);
                if (phoneField.getText().toString().length() > 0) {
                    i.putExtra("phoneNumber", phoneNumber);
                    i.putExtra("isUserRegistered", userIsRegistered);
                }
                startActivity(i);
            }
        });
    }//end of onCreate

    private void checkIfUserIsRegisteredInFirebase() {

        Query queries = mDatabase.orderByChild("phone").equalTo(phoneNumber);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    //create new user
                    Log.d(TAG, "User does not exist "+dataSnapshot.getChildren());
                    userIsRegistered = false;
                }
                else {
                    Log.d(TAG, "User Exists "+dataSnapshot.getChildren());
                    userIsRegistered = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        queries.addListenerForSingleValueEvent(eventListener);

}
    @Override
    public void onStart() {

        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthListener);
    }

}