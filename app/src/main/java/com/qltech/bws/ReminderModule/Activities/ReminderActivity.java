package com.qltech.bws.ReminderModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.qltech.bws.BWSApplication;
import com.qltech.bws.R;
import com.qltech.bws.ReminderModule.Models.ReminderStatusModel;
import com.qltech.bws.ReminderModule.Models.SetReminderModel;
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
    String UserId,ReminderStatus = "",PlaylistID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reminder);
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserId = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_ReminderStatus, Context.MODE_PRIVATE);
        ReminderStatus = (shared.getString(CONSTANTS.PREF_KEY_ReminderStatus, ""));

        if (getIntent().getExtras() != null) {
            PlaylistID = getIntent().getStringExtra(CONSTANTS.PlaylistID);
        }
        context = ReminderActivity.this;
        activity = ReminderActivity.this;
        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (ReminderStatus.equalsIgnoreCase("1")){
            binding.switchStatus.setChecked(true);
        }else if (ReminderStatus.equalsIgnoreCase("0")
                || ReminderStatus.equalsIgnoreCase("") ){
            binding.switchStatus.setChecked(false);
        }
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

        binding.switchStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    prepareData("1");
                }else {
                    prepareData("0");
                }
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                if (BWSApplication.isNetworkConnected(context)) {
                    Call<SetReminderModel> listCall = APIClient.getClient().SetReminder("6",UserId, ReminderStatus,
                            binding.tvTime.getText().toString(),"WednesDay");
                    listCall.enqueue(new Callback<SetReminderModel>() {
                        @Override
                        public void onResponse(Call<SetReminderModel> call, Response<SetReminderModel> response) {
                            if (response.isSuccessful()) {
                                BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                                SetReminderModel listModel = response.body();
                                Toast.makeText(activity, listModel.getResponseMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onFailure(Call<SetReminderModel> call, Throwable t) {
                            BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                        }
                    });
                } else {
                    Toast.makeText(context, getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void prepareData(String reminderStatus){
        BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
        if (BWSApplication.isNetworkConnected(context)) {
            Call<ReminderStatusModel> listCall = APIClient.getClient().getReminderStatus(UserId,reminderStatus);
            listCall.enqueue(new Callback<ReminderStatusModel>() {
                @Override
                public void onResponse(Call<ReminderStatusModel> call, Response<ReminderStatusModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                        ReminderStatusModel listModel = response.body();
                        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_ReminderStatus, MODE_PRIVATE);
                        SharedPreferences.Editor editor = shared.edit();
                        editor.putString(CONSTANTS.PREF_KEY_ReminderStatus, reminderStatus);
                        editor.commit();
                        binding.tvTime.setText(listModel.getResponseData().get(0).getTime());

                    }
                }

                @Override
                public void onFailure(Call<ReminderStatusModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                }
            });
        } else {
            Toast.makeText(context, getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
        }
    }
}