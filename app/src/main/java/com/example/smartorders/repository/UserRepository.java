package com.example.smartorders.repository;

import android.content.Context;

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

}
