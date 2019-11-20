package com.example.tours.ui.usertrip;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserTripViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public UserTripViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is user trip fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
