package com.example.smartorders.repository;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.smartorders.activities.HomeActivity;
import com.example.smartorders.activities.VerificationActivity;
import com.example.smartorders.models.SingleInstanceUser;
import com.example.smartorders.utils.SharedPreferencesUtil;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class UserRepositoryImpl implements UserRepository {

    private static final String TAG = "UserRepositoryImpl";
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

    @Override
    public void linkUserWithPhoneCredential(AuthCredential credential, Context context){
        // Link user with credential email and password and store email, id, phone and display name to register user object
        Objects.requireNonNull(mAuth.getCurrentUser()).linkWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "linkWithCredential:success");
                        Toast.makeText(context, "Successfully Linked with phone", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w(TAG, "linkWithCredential:failure", task.getException());
                    }
                });
    }

    @Override
    public void addUserToRealtimeDatabase(SingleInstanceUser user) {
        String user_id = mAuth.getCurrentUser().getUid();
        DatabaseReference current_user_db = mDatabase.child(user_id);
        current_user_db.child("email").setValue(user.getEmail());
        current_user_db.child("first name").setValue(user.getFirstName());
        current_user_db.child("last name").setValue(user.getLastName());
        current_user_db.child(user.getPhoneNumber()).setValue(user.getEmail());  /* TODO maybe i don't need this anymore */
        current_user_db.child("phone").setValue(user.getPhoneNumber());
    }

    @Override
    public void storeUserDetailsToSharedPrefs(String password, Context context, String uId) {
        /* if shared Prefs data are empty then the user has uninstalled the app or changed device
        * but is still registered with Firebase
        * so store to their details to shared prefs */
        Log.i(TAG, "Entering getDataFromDBIfSharedPrefsEmpty");
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(context, "user_details");
        if (sharedPreferencesUtil.userDetailsNotSet()){
            Log.d(TAG, "Shared preferences empty calling Firebase");
            DatabaseReference url = FirebaseDatabase.getInstance().getReference();
            DatabaseReference usersRef = url.child("Users").child(uId);
            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                            if (user.getKey().equals("email")) {
                                sharedPreferencesUtil.getEditor().putString("email", user.getValue(String.class));
                            }
                            if (user.getKey().equals("first name")) {
                                sharedPreferencesUtil.getEditor().putString("firstName", user.getValue(String.class));
                            }
                            if (user.getKey().equals("last name")) {
                                sharedPreferencesUtil.getEditor().putString("lastName", user.getValue(String.class));
                            }
                            if (user.getKey().equals("phone")) {
                                sharedPreferencesUtil.getEditor().putString("phoneNumber", user.getValue(String.class));
                            }
                        // TODO SAVING PASSWORD AS PLAIN TEXT IN SHARED PREFERENCES IS NOT GOOD PRACTICE BECAUSE A USER IN
                        // A ROOTED DEVICE WILL HAVE ACCESS TO IT. NEED TO RETHINK WHY I SAVE IT AND WHERE I USE IT AND
                        // FIND ANOTHER WAY TO GET THE PASSWORD IF NEEDED ANYWHERE IN THE APP
                        sharedPreferencesUtil.getEditor().putString("password", password);
                        sharedPreferencesUtil.getEditor().apply();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG,"Getting data from Firebase failed: " + databaseError.getCode());
                }
            });
        }
    }


    // [START sign_in_with_phone]
    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential, Context context) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SingleInstanceUser authCredentialSingleInstance = SingleInstanceUser.getInstance();
                        authCredentialSingleInstance.setuId(task.getResult().getUser().getUid());
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        Log.d(TAG, "Calling createStripeCustomer firebase function");
                        Objects.requireNonNull(task.getResult()).getUser();
                        System.out.println("IS NEW USER " + Objects.requireNonNull(task.getResult().getAdditionalUserInfo()).isNewUser());
                        if(task.getResult().getAdditionalUserInfo().isNewUser()){
                            Log.d(TAG, "New User Signed in with uID " + task.getResult().getUser().getUid());
                            // user has never registered with Firebase so navigate them to Email Address Fragment to
                            // complete registration
                            ((VerificationActivity)context).setCurrentItem(1, true);
                        }
                        else{
                            // user is registered so log them in to Home Activity
                            Intent i = new Intent(context, HomeActivity.class);
                            context.startActivity(i);
                            Log.d(TAG, "Existing User Signed in with uID " + task.getResult().getUser().getUid());
                        }
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Toast.makeText(context, "Wrong Credentials", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
