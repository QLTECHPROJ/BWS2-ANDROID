package com.qltech.bws.DashboardModule.Appointment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AppointmentViewModel  extends ViewModel {
    private MutableLiveData<String> mText;

    public AppointmentViewModel() {
        mText = new MutableLiveData<>();
//        mText.setValue("This is appointment fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
