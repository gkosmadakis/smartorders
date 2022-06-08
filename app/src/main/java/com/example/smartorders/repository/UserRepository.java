package com.example.smartorders.repository;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;

import com.example.smartorders.models.SingleInstanceUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.PhoneAuthCredential;
/*User repository for user interaction with Firebase
* See UserService
* */
public interface UserRepository {

    void linkUserWithPhoneCredential(AuthCredential credential, Context context);
    void signInWithPhoneAuthCredential(PhoneAuthCredential credential, Context context);
    void addUserToRealtimeDatabase(SingleInstanceUser authCredentialSingleInstance);
    void storeUserDetailsToSharedPrefs(String password, Context context, String uId);
    void updateUserEmailInFirebase(Context context, final String newEmail);
    void updateUserPhoneInFirebase(Context context, Intent data, EditText phoneNumberField);
    void updateUserFirstOrLastName(Context context, String childKeyInDB, String childValueInDB, String sharedPrefsKey, String updateTag);
    void updateUserPassword(Context context, String passwordExisting, String passwordNew);
    void updateUserPhoneWithCredential(Context context, PhoneAuthCredential credential);
    void deleteUserFromFirebase(Context context);
}
