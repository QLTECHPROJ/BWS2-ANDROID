package com.qltech.bws.ReminderModule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;

import com.qltech.bws.R;
import com.qltech.bws.databinding.ActivityReminderBinding;

import java.util.Calendar;

public class ReminderActivity extends AppCompatActivity {
    private int mHour, mMinute;
    ActivityReminderBinding binding;
    String am_pm, hourString, minuteSting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reminder);

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
    }
}