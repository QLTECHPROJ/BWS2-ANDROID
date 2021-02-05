package com.brainwellnessspa.ReminderModule.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Activities.MembershipChangeActivity;
import com.brainwellnessspa.R;
import com.brainwellnessspa.ReminderModule.Models.SelectPlaylistModel;
import com.brainwellnessspa.ReminderModule.Models.SetReminderModel;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivityReminderBinding;
import com.brainwellnessspa.databinding.SelectPlaylistLayoutBinding;
import com.segment.analytics.Properties;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.ComeScreenAccount;
import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;
import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.ComeScreenReminder;
import static com.brainwellnessspa.DashboardModule.Audio.AudioFragment.IsLock;

public class ReminderActivity extends AppCompatActivity {
    ActivityReminderBinding binding;
    Activity activity;
    Context context;
    int pos;
    Dialog dialog;
    String IsCheck = "", ReminderDay = "", ReminderId = "", am_pm, hourString, minuteSting, UserID, PlaylistID = "", PlaylistName = "", ComeFrom = "", Time = "", Day = "", currantTime = "";
    List<String> SelectedDay, remiderDays = new ArrayList<>();
    String ReminderDaySelected, ReminderDayTemp;
    private int mHour, mMinute;
    SelectPlaylistAdapter adapter;
    TimePickerDialog timePickerDialog;
    Properties p, p1, p2;
    public static int ComeScreenRemiderPlaylist = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reminder);
        context = ReminderActivity.this;
        activity = ReminderActivity.this;
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        if (getIntent().getExtras() != null) {
            ComeFrom = getIntent().getStringExtra("ComeFrom");
            PlaylistID = getIntent().getStringExtra(CONSTANTS.PlaylistID);
            PlaylistName = getIntent().getStringExtra("PlaylistName");
            ReminderId = getIntent().getStringExtra("ReminderId");
            ReminderDay = getIntent().getStringExtra("ReminderDay");
        }
        if (getIntent().getExtras() != null) {
            IsCheck = getIntent().getStringExtra("Time");
        }
        if (getIntent().getExtras() != null) {
            Time = getIntent().getStringExtra("Time");
            Day = getIntent().getStringExtra("Day");
            String[] myArray = Day.split(",");
            System.out.println("Contents of the array ::" + Arrays.toString(myArray));
            SelectedDay = Arrays.asList(myArray);
        }

        Log.e("DayDayDayDay", Day);
        Log.e("SelectedDaySelectedDay", TextUtils.join(",", SelectedDay));
        Properties p = new Properties();
        p.putValue("userId", UserID);
        p.putValue("reminderId", ReminderId);
        p.putValue("playlistId", PlaylistID);
        p.putValue("playlistName", PlaylistName);
        p.putValue("playlistType", "");
        if (IsCheck.equalsIgnoreCase("1")) {
            p.putValue("reminderStatus", "on");
        } else {
            p.putValue("reminderStatus", "off");
        }
        p.putValue("reminderTime", Time);
        p.putValue("reminderDay", ReminderDay);
        BWSApplication.addToSegment("Add/Edit Reminder Screen Viewed", p, CONSTANTS.screen);
        RefreshButton();
        ShowPlaylistName();

        binding.llBack.setOnClickListener(view -> {
            if (ComeScreenReminder == 1) {
                Intent i = new Intent(context, ReminderDetailsActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(0, 0);
            } else {
                ComeScreenAccount = 1;
                comefromDownload = "0";
                finish();
            }
        });

        if (Time.equalsIgnoreCase("") || Time.equalsIgnoreCase("0")) {
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("hh:mm a");
            simpleDateFormat1.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            DateFormat df = DateFormat.getTimeInstance();
            String gmtTime = df.format(new Date());
            Date currdate = new Date();
            try {
                currdate = simpleDateFormat1.parse(gmtTime);
                Log.e("currant currdate !!!!", String.valueOf(currdate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            currantTime = simpleDateFormat1.format(currdate);
            binding.tvTime.setText(currantTime);
        } else {
            binding.tvTime.setText(Time);
            currantTime = Time;
        }

        binding.llSelectTime.setOnClickListener(view -> {
            String[] time = currantTime.split(":");
            String min[] = time[1].split(" ");
            mHour = Integer.parseInt(time[0]);
//            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = Integer.parseInt(min[0]);
            String displayAmPm = min[1];
           /* if (displayAmPm.equalsIgnoreCase("p.m") || displayAmPm.equalsIgnoreCase("PM")) {
                mHour = mHour + 12;
            }*/

            timePickerDialog = new TimePickerDialog(ReminderActivity.this, R.style.TimePickerTheme,
                    (view1, hourOfDay, minute) -> {
                        if (hourOfDay < 10) {
                            if (hourOfDay == 0) {
                                hourString = "12";
                            } else {
                                hourString = "0" + hourOfDay;
                            }
                            am_pm = "AM";
                        } else if (hourOfDay > 12) {
                            am_pm = "PM";
                            hourOfDay = hourOfDay - 12;
                            hourString = "" + hourOfDay;
                            if (hourOfDay < 10) {
                                hourString = "0" + hourString;
                            }
                        } else if (hourOfDay == 12) {
                            am_pm = "PM";
                            hourString = "" + hourOfDay;
                        } else {
                            hourString = "" + hourOfDay;
                            am_pm = "AM";
                        }
                        if (minute < 10)
                            minuteSting = "0" + minute;
                        else
                            minuteSting = "" + minute;
//                        binding.tvTime.setText(hourOfDay + ":" + minute);
                        binding.tvTime.setText(hourString + ":" + minuteSting + " " + am_pm);
                        RefreshButton();
                        p2 = new Properties();
                        p2.putValue("userId", UserID);
                        p2.putValue("reminderId", ReminderId);
                        p2.putValue("reminderTime", hourString + ":" + minuteSting + " " + am_pm);
                        p2.putValue("playlistName", PlaylistName);
                        BWSApplication.addToSegment("Reminder Timer Clicked", p2, CONSTANTS.track);
                        ShowPlaylistName();
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        });

        if (Day.contains("0")) {
            ShowDaysSelection("0", binding.tvSunday);
        }

        if (Day.contains("1")) {
            ShowDaysSelection("1", binding.tvMonday);
        }

        if (Day.contains("2")) {
            ShowDaysSelection("2", binding.tvTuesday);
        }

        if (Day.contains("3")) {
            ShowDaysSelection("3", binding.tvWednesday);
        }

        if (Day.contains("4")) {
            ShowDaysSelection("4", binding.tvThursday);
        }

        if (Day.contains("5")) {
            ShowDaysSelection("5", binding.tvFriday);
        }

        if (Day.contains("6")) {
            ShowDaysSelection("6", binding.tvSaturday);
        }
        Log.e("remiderDays", TextUtils.join(",", remiderDays));

        binding.llSunday.setOnClickListener(view -> {
            DaysSelection("0", binding.tvSunday);
            RefreshButton();
        });

        binding.llMonday.setOnClickListener(view -> {
            DaysSelection("1", binding.tvMonday);
            RefreshButton();
        });

        binding.llTuesday.setOnClickListener(view -> {
            DaysSelection("2", binding.tvTuesday);
            RefreshButton();
        });

        binding.llWednesday.setOnClickListener(view -> {
            DaysSelection("3", binding.tvWednesday);
            RefreshButton();
        });

        binding.llThursday.setOnClickListener(view -> {
            DaysSelection("4", binding.tvThursday);
            RefreshButton();
        });

        binding.llFriday.setOnClickListener(view -> {
            DaysSelection("5", binding.tvFriday);
            RefreshButton();
        });

        binding.llSaturday.setOnClickListener(view -> {
            DaysSelection("6", binding.tvSaturday);
            RefreshButton();
        });

        if (ComeFrom.equalsIgnoreCase("")) {
            binding.ivArrow.setVisibility(View.VISIBLE);
            binding.llSelectPlaylist.setOnClickListener(view -> {
                dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.select_playlist);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_transparent)));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                final LinearLayout llBack = dialog.findViewById(R.id.llBack);
                final LinearLayout llError = dialog.findViewById(R.id.llError);
                final RecyclerView rvSelectPlaylist = dialog.findViewById(R.id.rvSelectPlaylist);
                final ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
                final FrameLayout progressBarHolder = dialog.findViewById(R.id.progressBarHolder);
                llBack.setOnClickListener(view12 -> dialog.dismiss());

                dialog.setOnKeyListener((v, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        return true;
                    }
                    return false;
                });

                RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
                rvSelectPlaylist.setLayoutManager(manager);
                rvSelectPlaylist.setItemAnimator(new DefaultItemAnimator());

                prepareData(rvSelectPlaylist, llError, progressBar, progressBarHolder);
                dialog.show();
                dialog.setCancelable(false);
            });
        } else if (ComeFrom.equalsIgnoreCase("1")) {
            binding.ivArrow.setVisibility(View.GONE);
        }

    }

    private void ShowDaysSelection(String value, TextView textView) {
        RefreshButton();
        remiderDays.add(value);
        textView.setTextColor(getResources().getColor(R.color.extra_light_blue));
        textView.setBackground(getResources().getDrawable(R.drawable.fill_transparent_bg));
    }

    private void DaysSelection(String value, TextView textView) {
        if (!remiderDays.contains(value)) {
            remiderDays.add(value);
            textView.setTextColor(getResources().getColor(R.color.extra_light_blue));
            textView.setBackground(getResources().getDrawable(R.drawable.fill_transparent_bg));
        } else {
            remiderDays.remove(value);
            textView.setTextColor(getResources().getColor(R.color.dark_blue_gray));
            textView.setBackground(getResources().getDrawable(R.drawable.transparent_bg));
        }
        p = new Properties();
        p.putValue("userId", UserID);
        if (ReminderId.equalsIgnoreCase("")) {
            p.putValue("reminderId", "");
        } else {
            p.putValue("reminderId", ReminderId);
        }
        ReminderDaySelected = TextUtils.join(",", remiderDays);
        if (ReminderDaySelected.contains("0")) {
            ReminderDayTemp = "Sun";
        }

        if (ReminderDaySelected.contains("1")) {
            ReminderDayTemp = "Mon";
        }

        if (ReminderDaySelected.contains("2")) {
            ReminderDayTemp = "Tue";
        }

        if (ReminderDaySelected.contains("3")) {
            ReminderDayTemp = "Wed";
        }

        if (ReminderDaySelected.contains("4")) {
            ReminderDayTemp = "Thu";
        }

        if (ReminderDaySelected.contains("5")) {
            ReminderDayTemp = "Fri";
        }

        if (ReminderDaySelected.contains("6")) {
            ReminderDayTemp = "Sat";
        }

        p.putValue("reminderDay", ReminderDayTemp);
//        "Sun,Mon,Tue,Wed,Thu,Fri,Sat"
        Log.e("ReminderDaySelected", ReminderDaySelected);
        p.putValue("playlistId", PlaylistID);
        p.putValue("playlistName", PlaylistName);
        BWSApplication.addToSegment("Reminder Day Selected", p, CONSTANTS.track);
        Log.e("remiderDays", TextUtils.join(",", remiderDays));
    }

    private void ShowPlaylistName() {
        if (PlaylistName.equalsIgnoreCase("")) {
            binding.tvPlaylistName.setText("Select Playlist");
        } else {
            binding.tvPlaylistName.setText(PlaylistName);
        }

        binding.btnSave.setOnClickListener(view -> {
            if (IsLock.equalsIgnoreCase("1")) {
                Intent i = new Intent(context, MembershipChangeActivity.class);
                i.putExtra("ComeFrom", "Plan");
                startActivity(i);
            } else if (IsLock.equalsIgnoreCase("2")) {
                BWSApplication.showToast(getString(R.string.reactive_plan), context);
            } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                if (PlaylistName.equalsIgnoreCase("")) {
                    BWSApplication.showToast("Please select playlist name", context);
                } else if (remiderDays.size() == 0) {
                    BWSApplication.showToast("Please select days", context);
                } else {
                    String sendTime = binding.tvTime.getText().toString();
                    SimpleDateFormat simpleDateFormat1x = new SimpleDateFormat("hh:mm a");
                    simpleDateFormat1x.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                    Date currdatex = new Date();
                    try {
                        currdatex = simpleDateFormat1x.parse(sendTime);
                        Log.e("sendTime currdate !!!!", String.valueOf(sendTime));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    sendTime = simpleDateFormat1x.format(currdatex);
                    Log.e("sendTime currdate###", sendTime);

                    if (BWSApplication.isNetworkConnected(context)) {
                        BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        Call<SetReminderModel> listCall = APIClient.getClient().SetReminder(PlaylistID, UserID, CONSTANTS.FLAG_ONE,
                                binding.tvTime.getText().toString(), TextUtils.join(",", remiderDays));
                        listCall.enqueue(new Callback<SetReminderModel>() {
                            @Override
                            public void onResponse(Call<SetReminderModel> call, Response<SetReminderModel> response) {
                                try {
                                    SetReminderModel listModel = response.body();
                                    if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                        Log.e("remiderDays", TextUtils.join(",", remiderDays));
                                        remiderDays.clear();
                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                        BWSApplication.showToast(listModel.getResponseMessage(), activity);
                                        if (ComeScreenReminder == 1) {
                                            Intent i = new Intent(context, ReminderDetailsActivity.class);
                                            startActivity(i);
                                            finish();
                                            overridePendingTransition(0, 0);
                                        } else {
                                            ComeScreenRemiderPlaylist = 1;
                                            finish();
                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<SetReminderModel> call, Throwable t) {
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            }
                        });
                    } else {
                        BWSApplication.showToast(getString(R.string.no_server_found), context);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        RefreshButton();
        super.onResume();
    }

    public void RefreshButton() {
        if (ComeFrom.equalsIgnoreCase("1")) {
            if (remiderDays.equals(SelectedDay) && binding.tvTime.getText().toString().equalsIgnoreCase(Time)) {
                binding.btnSave.setEnabled(false);
                binding.btnSave.setTextColor(getResources().getColor(R.color.white));
                binding.btnSave.setBackgroundResource(R.drawable.gray_extra_round_corners);
            } else if (!binding.tvTime.getText().toString().equalsIgnoreCase(Time)) {
                binding.btnSave.setEnabled(true);
                binding.btnSave.setTextColor(getResources().getColor(R.color.white));
                binding.btnSave.setBackgroundResource(R.drawable.extra_round_cornor);
            } else if (remiderDays.size() == 0) {
                binding.btnSave.setEnabled(false);
                binding.btnSave.setTextColor(getResources().getColor(R.color.white));
                binding.btnSave.setBackgroundResource(R.drawable.gray_extra_round_corners);
            } else if (!remiderDays.equals(SelectedDay)) {
                binding.btnSave.setEnabled(true);
                binding.btnSave.setTextColor(getResources().getColor(R.color.white));
                binding.btnSave.setBackgroundResource(R.drawable.extra_round_cornor);
            } else {
                binding.btnSave.setEnabled(true);
                binding.btnSave.setTextColor(getResources().getColor(R.color.white));
                binding.btnSave.setBackgroundResource(R.drawable.extra_round_cornor);
            }
        } else if (ComeFrom.equalsIgnoreCase("")) {
            if (!PlaylistName.equalsIgnoreCase("") && remiderDays.size() != 0) {
                binding.btnSave.setEnabled(true);
                binding.btnSave.setTextColor(getResources().getColor(R.color.white));
                binding.btnSave.setBackgroundResource(R.drawable.extra_round_cornor);
            } else {
                binding.btnSave.setEnabled(false);
                binding.btnSave.setTextColor(getResources().getColor(R.color.white));
                binding.btnSave.setBackgroundResource(R.drawable.gray_extra_round_corners);
            }
        }
    }

    private void prepareData(RecyclerView rvSelectPlaylist, LinearLayout llError, ProgressBar progressBar, FrameLayout progressBarHolder) {
        if (BWSApplication.isNetworkConnected(context)) {
            BWSApplication.showProgressBar(progressBar, progressBarHolder, activity);
            Call<SelectPlaylistModel> listCall = APIClient.getClient().getAllPlayListing(UserID);
            listCall.enqueue(new Callback<SelectPlaylistModel>() {
                @Override
                public void onResponse(Call<SelectPlaylistModel> call, Response<SelectPlaylistModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(progressBar, progressBarHolder, activity);
                            SelectPlaylistModel listModel = response.body();
                            adapter = new SelectPlaylistAdapter(listModel.getResponseData());
                            rvSelectPlaylist.setAdapter(adapter);

                            if (listModel.getResponseData().size() == 0) {
                                llError.setVisibility(View.GONE);
                                rvSelectPlaylist.setVisibility(View.GONE);
                            } else {
                                llError.setVisibility(View.GONE);
                                rvSelectPlaylist.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<SelectPlaylistModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(progressBar, progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), context);
        }
    }

    public class SelectPlaylistAdapter extends RecyclerView.Adapter<SelectPlaylistAdapter.MyViewHolder> {
        private List<SelectPlaylistModel.ResponseData> model;
        public int mSelectedItem = -1;

        public SelectPlaylistAdapter(List<SelectPlaylistModel.ResponseData> model) {
            this.model = model;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            SelectPlaylistLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.select_playlist_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            if ((position == pos)) {
                holder.binding.cbChecked.setChecked(position == pos);
            }

            holder.binding.cbChecked.setTag(model.get(position));
            holder.binding.cbChecked.setText(model.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return model.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            SelectPlaylistLayoutBinding binding;

            public MyViewHolder(SelectPlaylistLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;

                binding.cbChecked.setOnClickListener(view -> {
                    mSelectedItem = getAdapterPosition();
                    notifyDataSetChanged();
                    pos = mSelectedItem;
                    PlaylistID = model.get(mSelectedItem).getID();
                    PlaylistName = model.get(mSelectedItem).getName();
                    ShowPlaylistName();
                    dialog.dismiss();
                    p1 = new Properties();
                    p1.putValue("userId", UserID);
                    p1.putValue("playlistId", PlaylistID);
                    p1.putValue("playlistName", PlaylistName);
                    BWSApplication.addToSegment("Reminder Playlist List Clicked", p1, CONSTANTS.track);
                    RefreshButton();
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (ComeScreenReminder == 1) {
            Intent i = new Intent(context, ReminderDetailsActivity.class);
            startActivity(i);
            finish();
            overridePendingTransition(0, 0);
        } else {
            ComeScreenAccount = 1;
            comefromDownload = "0";
            finish();
        }
    }
}