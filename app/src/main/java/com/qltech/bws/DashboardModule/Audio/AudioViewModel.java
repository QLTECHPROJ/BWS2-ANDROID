package com.qltech.bws.DashboardModule.Audio;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AudioViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AudioViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}