package com.example.smartorders.interfaces;

import com.google.firebase.auth.AuthCredential;

public interface UserRepository {

    void linkWithCredential(AuthCredential credential);
}
