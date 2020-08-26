package com.qltech.bws.DashboardModule.Account;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AccountViewModel  extends ViewModel {
    private MutableLiveData<String> mText,getmText;

    public AccountViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
        getmText = new MutableLiveData<>();
        getmText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
