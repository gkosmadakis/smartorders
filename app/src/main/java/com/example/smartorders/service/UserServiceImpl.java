package com.example.smartorders.service;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;

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

    @Override
    public void updateUserEmailInFirebase(Context context, String newEmail, String password) {
        userRepository.updateUserEmailInFirebase(context, newEmail, password);
    }

    @Override
    public void updateUserPhoneInFirebase(Context context, Intent data) {
        userRepository.updateUserPhoneInFirebase(context, data);
    }

    @Override
    public void updateUserFirstOrLastName(Context context, String childKeyInDB, String childValueInDB, String sharedPrefsKey, String updateTag) {
        userRepository.updateUserFirstOrLastName(context, childKeyInDB, childValueInDB, sharedPrefsKey, updateTag);
    }

    @Override
    public void updateUserPassword(Context context, String passwordExisting, String passwordNew) {
        userRepository.updateUserPassword(context, passwordExisting, passwordNew);
    }

    @Override
    public void updateUserPhoneWithCredential(Context context, PhoneAuthCredential credential, String password) {
        userRepository.updateUserPhoneWithCredential(context, credential, password);
    }

    @Override
    public void deleteUserFromFirebase(Context context) {
        userRepository.deleteUserFromFirebase(context);
    }
}
