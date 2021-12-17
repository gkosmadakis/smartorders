package com.example.smartorders.service;

import android.content.Context;

import com.example.smartorders.repository.UserRepository;
import com.example.smartorders.models.SingleInstanceUser;
import com.example.smartorders.repository.UserRepositoryImpl;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.PhoneAuthCredential;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository = new UserRepositoryImpl();


    @Override
    public void linkUserWithPhoneCredential(AuthCredential credential, Context context) {
        userRepository.linkUserWithPhoneCredential(credential, context);
    }

    @Override
    public void addUserToRealtimeDatabase(SingleInstanceUser authCredentialSingleInstance) {
        userRepository.addUserToRealtimeDatabase(authCredentialSingleInstance);
    }

    @Override
    public void storeUserDetailsToSharedPrefs(String password, Context context, String uId) {
        userRepository.storeUserDetailsToSharedPrefs(password, context, uId);
    }

    @Override
    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential, Context context) {
        userRepository.signInWithPhoneAuthCredential(credential, context);
    }
}
