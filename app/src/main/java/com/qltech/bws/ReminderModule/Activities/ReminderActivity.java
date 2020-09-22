package com.qltech.bws.ReminderModule.Activities;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qltech.bws.BWSApplication;
import com.qltech.bws.R;
import com.qltech.bws.ReminderModule.Models.ReminderStatusModel;
import com.qltech.bws.ReminderModule.Models.SetReminderModel;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.ActivityReminderBinding;
import com.qltech.bws.databinding.ReminderDayBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReminderActivity extends AppCompatActivity {
    private int mHour, mMinute;
    ActivityReminderBinding binding;
    String am_pm, hourString, minuteSting;
    Activity activity;
    Context context;
    String UserId, ReminderStatus = "", reminderDay, PlaylistID = "", PlaylistName = "", reminderDayNo = "";
    List<String> reminderDayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reminder);
        context = ReminderActivity.this;
        activity = ReminderActivity.this;
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserId = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_ReminderStatus, Context.MODE_PRIVATE);
        ReminderStatus = (shared.getString(CONSTANTS.PREF_KEY_ReminderStatus, ""));
        reminderDayList = new ArrayList<>();
        reminderDayList.add("S");
        reminderDayList.add("M");
        reminderDayList.add("T");
        reminderDayList.add("W");
        reminderDayList.add("T");
        reminderDayList.add("F");
        reminderDayList.add("S");
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 7);
        binding.rvReminderDay.setLayoutManager(mLayoutManager);
        binding.rvReminderDay.setItemAnimator(new DefaultItemAnimator());
        ReminderDayAdapter reminderDayAdapter = new ReminderDayAdapter();
        binding.rvReminderDay.setAdapter(reminderDayAdapter);

        if (getIntent().getExtras() != null) {
            PlaylistID = getIntent().getStringExtra(CONSTANTS.PlaylistID);
            PlaylistName = getIntent().getStringExtra("PlaylistName");
        }

        binding.llBack.setOnClickListener(view -> finish());

        if (PlaylistName.equalsIgnoreCase("") ||
                PlaylistName == null) {
            binding.tvPlaylistName.setText("Select Playlist");
        } else {
            binding.tvPlaylistName.setText(PlaylistName);
        }

        if (ReminderStatus.equalsIgnoreCase("1")) {
            binding.switchStatus.setChecked(true);
        } else if (ReminderStatus.equalsIgnoreCase("0")
                || ReminderStatus.equalsIgnoreCase("")) {
            binding.switchStatus.setChecked(false);
        }

        binding.llSelectTime.setOnClickListener(view -> {
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
        });

        binding.llSelectPlaylist.setOnClickListener(view -> {
            Intent i = new Intent(ReminderActivity.this, SelectPlaylistActivity.class);
            startActivity(i);
            finish();
        });

        binding.switchStatus.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                prepareData("1");
            } else {
                prepareData("0");
            }
        });

        binding.btnSave.setOnClickListener(view -> {
            if (BWSApplication.isNetworkConnected(context)) {
                BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                Call<SetReminderModel> listCall = APIClient.getClient().SetReminder(PlaylistID, UserId, CONSTANTS.FLAG_ONE,
                        binding.tvTime.getText().toString(), reminderDayNo);
                listCall.enqueue(new Callback<SetReminderModel>() {
                    @Override
                    public void onResponse(Call<SetReminderModel> call, Response<SetReminderModel> response) {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                            SetReminderModel listModel = response.body();
                            BWSApplication.showToast(listModel.getResponseMessage(), activity);
                            Intent i = new Intent(context, ReminderDetailsActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<SetReminderModel> call, Throwable t) {
                        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                    }
                });
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), context);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void prepareData(String reminderStatus) {
        if (BWSApplication.isNetworkConnected(context)) {
            BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
            Call<ReminderStatusModel> listCall = APIClient.getClient().getReminderStatus(UserId, reminderStatus);
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
            BWSApplication.showToast(getString(R.string.no_server_found), context);
        }
    }

    public class ReminderDayAdapter extends RecyclerView.Adapter<ReminderDayAdapter.MyViewHolder> {
        private int row_index = -1, pos = 0;

        public ReminderDayAdapter() {
        }

        @NonNull
        @Override
        public ReminderDayAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ReminderDayBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.reminder_day, parent, false);
            return new ReminderDayAdapter.MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ReminderDayAdapter.MyViewHolder holder, int position) {
            holder.binding.tvday.setText(reminderDayList.get(position));

            if (position == 1) {
                ChangeFunction(holder, position, 1);
            } else {
                ChangeFunction(holder, position, 0);
            }
            holder.binding.llMainDay.setOnClickListener(view -> {
                pos++;
                if (row_index == position){
                    holder.binding.tvday.setTextColor(context.getResources().getColor(R.color.extra_light_blue));
                    holder.binding.tvday.setBackground(context.getResources().getDrawable(R.drawable.fill_transparent_bg));
                }else {
                    holder.binding.tvday.setTextColor(context.getResources().getColor(R.color.dark_blue_gray));
                    holder.binding.tvday.setBackground(context.getResources().getDrawable(R.drawable.transparent_bg));
                }
                notifyDataSetChanged();
            });
        }

        private void ChangeFunction(MyViewHolder holder, int position, int day) {
                switch (position) {
                    case 0:
                        reminderDay = "Sunday";
                        reminderDayNo = "7";
                        break;
                    case 1:
                        reminderDay = "Monday";
                        reminderDayNo = "1";
                        break;
                    case 2:
                        reminderDay = "Tuesday";
                        reminderDayNo = "2";
                        break;
                    case 3:
                        reminderDay = "Wednesday";
                        reminderDayNo = "3";
                        break;
                    case 4:
                        reminderDay = "Thursday";
                        reminderDayNo = "4";
                        break;
                    case 5:
                        reminderDay = "Friday";
                        reminderDayNo = "5";
                        break;
                    case 6:
                        reminderDay = "Saturday";
                        reminderDayNo = "6";
                        break;
                    default:
                        reminderDay = "Monday";
                        reminderDayNo = "1";
                        break;
                }
        }

        @Override
        public int getItemCount() {
            return reminderDayList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ReminderDayBinding binding;

            public MyViewHolder(ReminderDayBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}