package com.example.smartorders.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smartorders.R;
import com.example.smartorders.adapters.ViewPagerAdapter;
import com.example.smartorders.interfaces.storeDataListener;
import com.example.smartorders.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

public class VerificationActivity extends AppCompatActivity implements storeDataListener {

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private EditText mPhoneNumberField;
    private ViewPager viewPager;
    private static final String TAG = "VerificationActivity";
    private String emailAddress;
    private String password;
    private String firstName;
    private String lastName;
    private DatabaseReference mDatabase, url;
    private String phoneNumber;
    private String emailInDB;
    private boolean isUserRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        // Restore instance state
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phoneNumber");
        isUserRegistered = intent.getBooleanExtra("isUserRegistered",false);

        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));

        // Assign views
        mPhoneNumberField = findViewById(R.id.phoneNumberField);

        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null) {
                    Log.d(TAG, "AuthListener USER exists "+firebaseAuth.getCurrentUser());
                }
            }
        };

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
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

                signInWithPhoneAuthCredential(credential, mPhoneNumberField.getText().toString());
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    mPhoneNumberField.setError("Invalid phone number.");
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
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
                setmResendToken(mResendToken);
                setmVerificationId(mVerificationId);
            }
        };
        // [END phone_auth_callbacks]

    }// end of onCreate

    public void setCurrentItem (int item, boolean smoothScroll) {
        viewPager.setCurrentItem(item, smoothScroll);
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        mAuth.addAuthStateListener(mAuthListener);

        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(mPhoneNumberField.getText().toString());
            phoneNumber = mPhoneNumberField.getText().toString();
        }
            // [END_EXCLUDE]

    }
    // [END on_start_check_user]

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    public void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        Log.i(TAG, "Entering startPhoneNumberVerification");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
        this.phoneNumber = phoneNumber;
    }

    public void verifyPhoneNumberWithCode(String verificationId, String code, String phone) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential, phone);
    }

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential, final String phone) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid

                                Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                });
    }
    // [END sign_in_with_phone]

    private boolean validatePhoneNumber() {
        String phoneNumber = mPhoneNumberField.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberField.setError("Invalid phone number.");
            return false;
        }
        return true;
    }

    // [START resend_verification]
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
    // [END resend_verification]


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Toast.makeText(this,"onOptionsItem called", Toast.LENGTH_LONG).show();
            if(viewPager.getCurrentItem() > 0){
                viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(viewPager.getCurrentItem() > 0){
            viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
        } else {
            super.onBackPressed();
        }
    }

    public PhoneAuthProvider.ForceResendingToken getmResendToken() {
        return mResendToken;
    }

    public void setmResendToken(PhoneAuthProvider.ForceResendingToken mResendToken) {
        this.mResendToken = mResendToken;
    }

    public String getmVerificationId() {
        return mVerificationId;
    }

    public void setmVerificationId(String mVerificationId) {
        this.mVerificationId = mVerificationId;
    }

    public boolean isUserRegistered() {
        return isUserRegistered;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    //  TODO THIS WAY DOWN SHOULD GO TO ANOTHER CLASS
    @Override
    public void storeEmailAddress(String s) {

        emailAddress = s;
    }

    @Override
    public void storePassword(String s) {

        password = s;
    }

    @Override
    public void storeFullName(String first, String last){
        firstName = first;
        lastName = last;

        // Create EmailAuthCredential with email and password
        AuthCredential credential = EmailAuthProvider.getCredential(emailAddress, password);
        // Link the user with the details from the fragments in Firebase
        // [START link_credential]
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "linkWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            User registeredUser = new User();
                            registeredUser.setEmailAddress(user.getEmail());
                            registeredUser.setuId(user.getUid());
                            registeredUser.setPhoneNumber(user.getPhoneNumber());
                            registeredUser.setFirstName(user.getDisplayName());

                        } else {
                            Log.w(TAG, "linkWithCredential:failure", task.getException());
                            Toast.makeText(VerificationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END link_credential]
        String user_id = mAuth.getCurrentUser().getUid();
        DatabaseReference current_user_db = mDatabase.child(user_id);
        current_user_db.child("email").setValue(emailAddress);
        current_user_db.child("first name").setValue(firstName);
        current_user_db.child("last name").setValue(lastName);
        current_user_db.child(phoneNumber).setValue(emailAddress);
        current_user_db.child("phone").setValue(phoneNumber);

        /*store in shared preferences the data*/
        getDataFromDBIfSharedPrefsEmpty(password);
        storeInPrefsUserIsSignedIn();
    }

   public void startSignIn(final String phoneEntered, String password){
        /* At this point the user signs in. If the user has uninstalled the app the sharedPrefs will be empty so we need
        * to make a call to Firebase to get email,phone first name last name. Password will be taken from this method*/
       mAuth.signInWithEmailAndPassword(emailInDB, password)
               .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if (task.isSuccessful()) {
                           // Sign in success, update UI with the signed-in user's information
                           Log.d(TAG, "signInWithEmail:success");
                           FirebaseUser user = mAuth.getCurrentUser();
                            /* Do we have all the data from shared Prefs? */
                           getDataFromDBIfSharedPrefsEmpty(password);
                           storeInPrefsUserIsSignedIn();
                           // Log in the user and start Home Activity, with set flags all the other activities are finished so
                           // when the user clicks back will not go to the password fragment
                           Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                           startActivity(intent);

                       } else {
                           // If sign in fails, display a message to the user.
                           Log.w(TAG, "signInWithEmail:failure", task.getException());
                           Toast.makeText(VerificationActivity.this, "Authentication failed.",
                                   Toast.LENGTH_SHORT).show();
                       }
                   }
               });
        }

    private void storeInPrefsUserIsSignedIn() {
        /*At this point a new user is registered and accepted the Terms and Conditions
         * Save to shared Preferences that the user is registered and the UID from Firebase
         * so when re-opening the app to go straight to HomeActivity without entering phone/email/password */
        SharedPreferences sp = getSharedPreferences("user_logged_in", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("is_user_logged_in", "true");
        editor.putString("firebase_user_id", mAuth.getUid());
        editor.apply();
    }

    private void getDataFromDBIfSharedPrefsEmpty(String password) {
        /*Store to shared preferences the data*/
        Log.i(TAG, "Entering getDataFromDBIfSharedPrefsEmpty");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        /* if shared Prefs data are empty then the user has uninstalled the app or changed device but is still registered with Firebase */
        if (sp.getString("email","").equals("") && sp.getString("firstName","").equals("") &&
                sp.getString("lastName","").equals("") && sp.getString("phoneNumber","").equals("")){
            Log.d(TAG, "Shared preferences empty calling Firebase");
            url = FirebaseDatabase.getInstance().getReference();
            DatabaseReference usersRef = url.child("Users");
            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot userIDLevel : dataSnapshot.getChildren()) {
                        for (DataSnapshot userDetails : userIDLevel.getChildren()) {
                            if (userDetails.getKey().equals("email")) {
                                editor.putString("email", userDetails.getValue(String.class));
                            }
                            if (userDetails.getKey().equals("first name")) {
                                editor.putString("firstName", userDetails.getValue(String.class));
                            }
                            if (userDetails.getKey().equals("last name")) {
                                editor.putString("lastName", userDetails.getValue(String.class));
                            }
                            if (userDetails.getKey().equals("phone")) {
                                editor.putString("phoneNumber", userDetails.getValue(String.class));
                            }
                        }
                        // TODO SAVING PASSWORD AS PLAIN TEXT IN SHARED PREFERENCES IS NOT GOOD PRACTICE BECAUSE A USER IN
                        // A ROOTED DEVICE WILL HAVE ACCESS TO IT. NEED TO RETHINK WHY I SAVE IT AND WHERE I USE IT AND
                        // FIND ANOTHER WAY TO GET THE PASSWORD IF NEEDED ANYWHERE IN THE APP
                        editor.putString("password", password);
                        editor.apply();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG,"Getting data from Firebase failed: " + databaseError.getCode());
                }
            });
        }
    }

    public void retrieveEmailFromDB(final String phoneEntered) {
        url = FirebaseDatabase.getInstance().getReference();
        DatabaseReference allUsers = url.child("Users");

        allUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userIDLevel : dataSnapshot.getChildren()) {
                    for (DataSnapshot userDetails : userIDLevel.getChildren()) {
                        if (userDetails.getKey().equals(phoneEntered)) {
                           emailInDB = userDetails.getValue(String.class);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG,"Retrieval of email from Firebase failed: " + databaseError.getCode());
            }
        });
    }
}
