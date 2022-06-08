package com.example.smartorders.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AccountViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AccountViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}