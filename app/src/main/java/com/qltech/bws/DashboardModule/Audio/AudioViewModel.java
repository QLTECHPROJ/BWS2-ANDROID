package com.qltech.bws.DashboardModule.Audio;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.qltech.bws.R;

public class AudioViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AudioViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(String.valueOf(R.string.Explore));
    }

    public LiveData<String> getText() {
        return mText;
    }
}