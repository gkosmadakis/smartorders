package com.example.smartorders.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private  SharedPreferences.Editor editor;

    public SharedPreferencesUtil(Context context, String sharePrefsKey) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(sharePrefsKey, Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();
    }

    public SharedPreferences.Editor getEditor() {
        return editor;
    }

    public void setEditor(SharedPreferences.Editor editor) {
        this.editor = editor;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean userDetailsNotSet(){
        return email == null && firstName == null && lastName == null && phoneNumber == null;
    }
}
