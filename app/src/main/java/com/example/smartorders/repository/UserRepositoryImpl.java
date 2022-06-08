package com.example.smartorders.repository;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.example.smartorders.activities.HomeActivity;
import com.example.smartorders.activities.UpdateAccountActivity;
import com.example.smartorders.activities.UpdatePasswordActivity;
import com.example.smartorders.activities.UpdatePhoneActivity;
import com.example.smartorders.activities.VerificationActivity;
import com.example.smartorders.models.SingleInstanceUser;
import com.example.smartorders.utils.SharedPreferencesUtil;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
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
    public static final int RESULT_OK = -1;

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

    @Override
    public void updateUserEmailInFirebase(Context context, String newEmail) {
        FirebaseUser user = mAuth.getCurrentUser();
        // Get auth credentials from the user for re-authentication
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        AuthCredential credential = EmailAuthProvider
                .getCredential(sp.getString("email",""), sp.getString("password","")); // Current Login Credentials \\
        // Prompt the user to re-provide their sign-in credentials
        assert user != null;
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    Log.d(TAG, "User re-authenticated.");
                    //Now change your email address \\
                    //----------------Code for Changing Email Address----------\\
                    FirebaseUser user1 = mAuth.getCurrentUser();
                    assert user1 != null;
                    user1.updateEmail(newEmail)
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Log.d(TAG, "User email address updated.");
                                    Toast.makeText(context, "Email updated successfully", Toast.LENGTH_SHORT).show();
                                    /*Update real time database*/
                                    String user_id = mAuth.getCurrentUser().getUid();
                                    DatabaseReference current_user_db = mDatabase.child(user_id);
                                    current_user_db.child("email").setValue(newEmail);
                                    current_user_db.child(sp.getString("phoneNumber","")).setValue(newEmail);
                                    /*Update Preferences */
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString("email",newEmail);
                                    editor.apply();
                                    Intent data = new Intent();
                                    data.putExtra("emailUpdated", newEmail);
                                    // Activity finished ok, return the data
                                    ((UpdateAccountActivity)context).setResult(RESULT_OK, data);
                                    ((UpdateAccountActivity)context).finish();
                                }
                            });
                });
    }

    @Override
    public void updateUserPhoneInFirebase(Context context, Intent data, EditText phoneNumberField) {
        Log.d(TAG, Objects.requireNonNull(data.getExtras().getString("verifiedPhone")));
        /*Create a new ID in the Database and add the new phone and the current data email,first/last name*/
        String user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        DatabaseReference current_user_db = mDatabase.child(user_id);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        current_user_db.child("email").setValue(sp.getString("email", ""));
        current_user_db.child("first name").setValue(sp.getString("firstName", ""));
        current_user_db.child("last name").setValue(sp.getString("lastName", ""));
        current_user_db.child(Objects.requireNonNull(data.getExtras().getString("verifiedPhone"))).setValue(sp.getString("email", ""));
        current_user_db.child("phone").setValue(data.getExtras().getString("verifiedPhone"));
        /*Update in Prefs. Store in preferences the new phone*/
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("phoneNumber", data.getExtras().getString("verifiedPhone"));
        editor.apply();
        Intent dataIntent = new Intent();
        dataIntent.putExtra("phoneUpdated", phoneNumberField.getText().toString());
        // Activity finished ok, return the data
        ((UpdateAccountActivity)context).setResult(RESULT_OK, data);
        ((UpdateAccountActivity)context).finish();
    }

    @Override
    public void updateUserFirstOrLastName(Context context, String childKeyInDB, String childValueInDB, String sharedPrefsKey, String updateTag) {
        DatabaseReference urlReferenceToId = FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        urlReferenceToId.child(childKeyInDB).setValue(childValueInDB);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(sharedPrefsKey, childValueInDB);
        editor.apply();
        Intent data = new Intent();
        data.putExtra(updateTag, childValueInDB);
        // Activity finished ok, return the data
        ((UpdateAccountActivity)context).setResult(RESULT_OK, data);
        ((UpdateAccountActivity)context).finish();
    }

    @Override
    public void updateUserPassword(Context context, String passwordExisting, String passwordNew) {
        final FirebaseUser user = mAuth.getCurrentUser();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String email = sp.getString("email","");
        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        assert email != null;
        AuthCredential credential = EmailAuthProvider.getCredential(email, passwordExisting);
        // Prompt the user to re-provide their sign-in credentials
        assert user != null;
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.updatePassword(passwordNew).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Log.d(TAG, "Password updated");
                                Intent data = new Intent();
                                data.putExtra("passwordUpdated", passwordNew);
                                // Activity finished ok, return the data
                                ((UpdatePasswordActivity)context).setResult(RESULT_OK, data);
                                ((UpdatePasswordActivity)context).finish();
                            } else {
                                Log.d(TAG, "Error password not updated");
                            }
                        });
                    } else {
                        Log.d(TAG, "Error auth failed");
                    }
                });
    }

    @Override
    public void updateUserPhoneWithCredential(Context context, PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        Log.d(TAG, "signInWithCredential:success");
                        final FirebaseUser user = Objects.requireNonNull(task.getResult()).getUser();
                        if(user!=null) {
                            user.updatePhoneNumber(credential).addOnCompleteListener(task1 -> {
                                Toast.makeText(context, "UpdatePhoneNumber success", Toast.LENGTH_SHORT).show();
                                linkNewPhone(context);
                                Log.d(TAG, "updated phone: "+user.getPhoneNumber());
                                Intent data = new Intent();
                                data.putExtra("verifiedPhone", user.getPhoneNumber());
                                data.putExtra("phoneUpdated", user.getPhoneNumber());
                                // Activity finished ok, return the data
                                ((UpdatePhoneActivity)context).setResult(RESULT_OK, data);
                                ((UpdatePhoneActivity)context).finish();
                            });
                        }
                        else {
                            Toast.makeText(context, "UpdatePhoneNumber user=null", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Sign in failed, display a message
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Toast.makeText(context, "FirebaseAuthInvalidCredentialsException", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void deleteUserFromFirebase(Context context) {
        FirebaseUser user = mAuth.getCurrentUser();
        String user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        DatabaseReference current_user_db = mDatabase.child(user_id);
        current_user_db.removeValue();
        assert user != null;
        user.delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User account deleted.");
                        Toast.makeText(context, "Delete success", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void linkNewPhone(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String email = sp.getString("email","");
        String password = sp.getString("password","");
        // Create EmailAuthCredential with email and password
        assert email != null;
        assert password != null;
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        Log.d(TAG, "Email,password "+email +" "+password);
        linkUserWithPhoneCredential(credential, context);
    }

}
