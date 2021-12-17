package com.example.smartorders.service;

import android.content.Context;

import com.example.smartorders.models.SingleInstanceUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.PhoneAuthCredential;
/* Interface for User interaction with Firebase
* linkUserWithPhoneCredential is linking email with phone number in Firebase Authentication
* signInWithPhoneAuthCredential signs the user in with phone number and OTP
* addUserToRealtimeDatabase adds the user to Realtime Database with their UID
* storeUserDetailsToSharedPrefs stores user details like email, first, last name, phone and password to shared prefs so that to be used all over in the app*/

public interface UserService {

    void linkUserWithPhoneCredential(AuthCredential credential, Context context);
    void signInWithPhoneAuthCredential(PhoneAuthCredential credential, Context context);
    void addUserToRealtimeDatabase(SingleInstanceUser authCredentialSingleInstance);
    void storeUserDetailsToSharedPrefs(String password, Context context, String uId);

}
