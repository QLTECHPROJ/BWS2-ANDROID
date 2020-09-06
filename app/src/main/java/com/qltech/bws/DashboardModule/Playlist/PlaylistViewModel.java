package com.qltech.bws.DashboardModule.Playlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PlaylistViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PlaylistViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}