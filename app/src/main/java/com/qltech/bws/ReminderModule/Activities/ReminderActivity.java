package com.qltech.bws.ReminderModule.Activities;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qltech.bws.BWSApplication;
import com.qltech.bws.R;
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

import static com.qltech.bws.DashboardModule.Account.AccountFragment.ComeScreenReminder;

public class ReminderActivity extends AppCompatActivity {
    private int mHour, mMinute;
    ActivityReminderBinding binding;
    String am_pm, hourString, minuteSting;
    Activity activity;
    Context context;
    String UserId, ReminderStatus = "", PlaylistID = "", PlaylistName = "", reminderDayNo = "", ComeFrom = "", Time = "";
    List<String> reminderDayList;
    ArrayList<String> remiderDays = new ArrayList<>();

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
            ComeFrom = getIntent().getStringExtra("ComeFrom");
            PlaylistID = getIntent().getStringExtra(CONSTANTS.PlaylistID);
            PlaylistName = getIntent().getStringExtra("PlaylistName");
        }

        if (getIntent().getExtras() != null){
            Time = getIntent().getStringExtra("Time");
        }
        binding.llBack.setOnClickListener(view -> {
            if (ComeScreenReminder == 1) {
                Intent i = new Intent(context, ReminderDetailsActivity.class);
                startActivity(i);
                finish();
            } else {
                finish();
            }
        });

        if (PlaylistName.equalsIgnoreCase("") ||
                PlaylistName == null) {
            binding.tvPlaylistName.setText("Select Playlist");
        } else {
            binding.tvPlaylistName.setText(PlaylistName);
        }

        if (Time.equalsIgnoreCase("")||
                Time == null){
            binding.tvTime.setText("12:00 pm");
        }else {
            binding.tvTime.setText(Time);
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

        if (ComeFrom.equalsIgnoreCase("") || ComeFrom == null) {
            binding.ivArrow.setVisibility(View.VISIBLE);
            binding.llSelectPlaylist.setOnClickListener(view -> {
                Intent i = new Intent(ReminderActivity.this, SelectPlaylistActivity.class);
                i.putExtra("ComeFrom", "");
                i.putExtra("PlaylistID", PlaylistID);
                i.putExtra("PlaylistName", PlaylistName);
                startActivity(i);
                finish();
            });
        } else if (ComeFrom.equalsIgnoreCase("1")) {
            binding.ivArrow.setVisibility(View.GONE);
        }

//        TextUtils.join(",", remiderDays)
        binding.btnSave.setOnClickListener(view -> {
            if (PlaylistName.equalsIgnoreCase("")) {
                BWSApplication.showToast("Please select playlist name", context);
            } else {
                if (BWSApplication.isNetworkConnected(context)) {
                    BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                    Call<SetReminderModel> listCall = APIClient.getClient().SetReminder(PlaylistID, UserId, CONSTANTS.FLAG_ONE,
                            binding.tvTime.getText().toString(), reminderDayNo);
                    listCall.enqueue(new Callback<SetReminderModel>() {
                        @Override
                        public void onResponse(Call<SetReminderModel> call, Response<SetReminderModel> response) {
                            if (response.isSuccessful()) {
                                remiderDays.clear();
                                BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                                SetReminderModel listModel = response.body();
                                BWSApplication.showToast(listModel.getResponseMessage(), activity);
                                if (ComeScreenReminder == 1) {
                                    Intent i = new Intent(context, ReminderDetailsActivity.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    finish();
                                }
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
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (ComeScreenReminder == 1) {
            Intent i = new Intent(context, ReminderDetailsActivity.class);
            startActivity(i);
            finish();
        } else {
            finish();
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
        public void onBindViewHolder(@NonNull ReminderDayAdapter.MyViewHolder holder, final int position) {
            holder.binding.tvday.setText(reminderDayList.get(position));
            if (position == 1) {
                ChangeFunction(holder, position, 1);
            } else {
                ChangeFunction(holder, position, 0);
            }
            holder.binding.llMainDay.setOnClickListener(view -> {
                row_index = position;
                pos++;
                notifyDataSetChanged();
            });

            if (row_index == position) {
                ChangeFunction(holder, position, 1);
            } else {
                if (position == 1 && pos == 0) {
                    ChangeFunction(holder, position, 1);
                } else {
                    ChangeFunction(holder, position, 0);
                }
            }

           /* holder.binding.llMainDay.setOnClickListener(view -> {
                holder.binding.tvday.setTextColor(context.getResources().getColor(R.color.extra_light_blue));
                holder.binding.tvday.setBackground(context.getResources().getDrawable(R.drawable.fill_transparent_bg));
                if (remiderDays.size() != 0) {
                    if (remiderDays.contains(String.valueOf(position))) {
                        remiderDays.add(String.valueOf(position));
                        holder.binding.tvday.setTextColor(context.getResources().getColor(R.color.extra_light_blue));
                        holder.binding.tvday.setBackground(context.getResources().getDrawable(R.drawable.fill_transparent_bg));
                    } else {
                        holder.binding.tvday.setTextColor(context.getResources().getColor(R.color.dark_blue_gray));
                        holder.binding.tvday.setBackground(context.getResources().getDrawable(R.drawable.transparent_bg));
                    }
                    Log.e("remiderDays", TextUtils.join(",", remiderDays));
                    Log.e("position", String.valueOf(position));
                }
            });*/
        }

        private void ChangeFunction(MyViewHolder holder, int position, int day) {
            if (day == 1) {
                holder.binding.tvday.setTextColor(context.getResources().getColor(R.color.extra_light_blue));
                holder.binding.tvday.setBackground(context.getResources().getDrawable(R.drawable.fill_transparent_bg));
                switch (position) {
                    case 0:
                        reminderDayNo = "0";
                        break;
                    case 1:
                        reminderDayNo = "1";
                        break;
                    case 2:
                        reminderDayNo = "2";
                        break;
                    case 3:
                        reminderDayNo = "3";
                        break;
                    case 4:
                        reminderDayNo = "4";
                        break;
                    case 5:
                        reminderDayNo = "5";
                        break;
                    case 6:
                        reminderDayNo = "6";
                        break;
                    default:
                        reminderDayNo = "1";
                        break;
                }
            } else {
                holder.binding.tvday.setTextColor(context.getResources().getColor(R.color.dark_blue_gray));
                holder.binding.tvday.setBackground(context.getResources().getDrawable(R.drawable.transparent_bg));
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