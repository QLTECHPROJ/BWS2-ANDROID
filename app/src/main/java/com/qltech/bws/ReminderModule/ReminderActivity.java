package com.qltech.bws.ReminderModule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.qltech.bws.BWSApplication;
import com.qltech.bws.BillingOrderModule.Models.CardModel;
import com.qltech.bws.DashboardModule.Models.NextSessionViewModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.ActivityReminderBinding;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReminderActivity extends AppCompatActivity {
    private int mHour, mMinute;
    ActivityReminderBinding binding;
    String am_pm, hourString, minuteSting;
    Activity activity;
    Context context;
    String UserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reminder);
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserId = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        context = ReminderActivity.this;
        activity = ReminderActivity.this;
        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.llSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(ReminderActivity.this, R.style.TimePickerTheme,
                        (view1, hourOfDay, minute) -> {
                            if (hourOfDay < 10) {
                                hourString = "0" + hourOfDay;
                            } else {
                                hourString = "" + hourOfDay;
                            }
                            if (minute < 10)
                                minuteSting = "0" + minute;
                            else
                                minuteSting = "" + minute;
                            if (hourOfDay > 12) {
                                am_pm = "PM";
                                hourOfDay = hourOfDay - 12;
                            } else {
                                am_pm = "AM";
                            }
                            binding.tvTime.setText(hourString + ":" + minuteSting + " " + am_pm);
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BWSApplication.isNetworkConnected(context)) {

                    Call<CardModel> listCall = APIClient.getClient().SetReminder("6",UserId,"1",
                            binding.tvTime.getText().toString(),"");
                    listCall.enqueue(new Callback<CardModel>() {
                        @Override
                        public void onResponse(Call<CardModel> call, Response<CardModel> response) {
                            if (response.isSuccessful()) {
                                BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                                CardModel listModel = response.body();

                            }
                        }

                        @Override
                        public void onFailure(Call<CardModel> call, Throwable t) {
                            BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                        }
                    });
                } else {
                    Toast.makeText(context, getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}