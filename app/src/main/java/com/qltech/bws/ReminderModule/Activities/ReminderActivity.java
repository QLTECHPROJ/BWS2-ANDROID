package com.qltech.bws.ReminderModule.Activities;

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
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qltech.bws.BWSApplication;
import com.qltech.bws.R;
import com.qltech.bws.ReminderModule.Models.SelectPlaylistModel;
import com.qltech.bws.ReminderModule.Models.SetReminderModel;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.ActivityReminderBinding;
import com.qltech.bws.databinding.SelectPlaylistLayoutBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.qltech.bws.DashboardModule.Account.AccountFragment.ComeScreenReminder;

public class ReminderActivity extends AppCompatActivity {
    ActivityReminderBinding binding;
    String am_pm, hourString, minuteSting;
    Activity activity;
    Context context;
    Dialog dialog;
    String UserId, PlaylistID = "", PlaylistName = "", ComeFrom = "", Time = "", Day = "";
    ArrayList<String> remiderDays = new ArrayList<>();
    private int mHour, mMinute;
    SelectPlaylistAdapter adapter;
    TimePickerDialog timePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reminder);
        context = ReminderActivity.this;
        activity = ReminderActivity.this;
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserId = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        if (getIntent().getExtras() != null) {
            ComeFrom = getIntent().getStringExtra("ComeFrom");
            PlaylistID = getIntent().getStringExtra(CONSTANTS.PlaylistID);
            PlaylistName = getIntent().getStringExtra("PlaylistName");
        }

        if (getIntent().getExtras() != null) {
            Time = getIntent().getStringExtra("Time");
            Day = getIntent().getStringExtra("Day");
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

        ShowPlaylistName();

        if (Time.equalsIgnoreCase("") ||
                Time.equalsIgnoreCase("0") ||
                Time == null) {
            binding.tvTime.setText("09:00 am");
        } else {
            binding.tvTime.setText(Time);
        }

        binding.llSelectTime.setOnClickListener(view -> {
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            timePickerDialog = new TimePickerDialog(ReminderActivity.this, R.style.TimePickerTheme,
                    (view1, hourOfDay, minute) -> {
                        if (hourOfDay < 10) {
                            hourString = "0" + hourOfDay;
                            am_pm = "AM";
                        } else if (hourOfDay > 12) {
                            am_pm = "PM";
                            hourOfDay = hourOfDay - 12;
                            hourString = "" + hourOfDay;
                            if (hourOfDay < 10) {
                                hourString = "0" + hourString;
                            }

                        } else {
                            hourString = "" + hourOfDay;
                            am_pm = "AM";
                        }
                        if (minute < 10)
                            minuteSting = "0" + minute;
                        else
                            minuteSting = "" + minute;

                        binding.tvTime.setText(hourString + ":" + minuteSting + " " + am_pm);
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        });

        if (Day.contains("0")) {
            remiderDays.add("0");
            binding.tvSunday.setTextColor(context.getResources().getColor(R.color.extra_light_blue));
            binding.tvSunday.setBackground(context.getResources().getDrawable(R.drawable.fill_transparent_bg));
        }

        if (Day.contains("1")) {
            remiderDays.add("1");
            binding.tvMonday.setTextColor(context.getResources().getColor(R.color.extra_light_blue));
            binding.tvMonday.setBackground(context.getResources().getDrawable(R.drawable.fill_transparent_bg));
        }

        if (Day.contains("2")) {
            remiderDays.add("2");
            binding.tvTuesday.setTextColor(context.getResources().getColor(R.color.extra_light_blue));
            binding.tvTuesday.setBackground(context.getResources().getDrawable(R.drawable.fill_transparent_bg));
        }

        if (Day.contains("3")) {
            remiderDays.add("3");
            binding.tvWednesday.setTextColor(context.getResources().getColor(R.color.extra_light_blue));
            binding.tvWednesday.setBackground(context.getResources().getDrawable(R.drawable.fill_transparent_bg));
        }

        if (Day.contains("4")) {
            remiderDays.add("4");
            binding.tvThursday.setTextColor(context.getResources().getColor(R.color.extra_light_blue));
            binding.tvThursday.setBackground(context.getResources().getDrawable(R.drawable.fill_transparent_bg));
        }

        if (Day.contains("5")) {
            remiderDays.add("5");
            binding.tvFriday.setTextColor(context.getResources().getColor(R.color.extra_light_blue));
            binding.tvFriday.setBackground(context.getResources().getDrawable(R.drawable.fill_transparent_bg));
        }

        if (Day.contains("6")) {
            remiderDays.add("6");
            binding.tvSaturday.setTextColor(context.getResources().getColor(R.color.extra_light_blue));
            binding.tvSaturday.setBackground(context.getResources().getDrawable(R.drawable.fill_transparent_bg));
        }

        Log.e("remiderDays", TextUtils.join(",", remiderDays));

        if (ComeFrom.equalsIgnoreCase("") || ComeFrom == null) {
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

                llBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

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

                prepareData(rvSelectPlaylist, llError);
                dialog.show();
                dialog.setCancelable(false);
            });
        } else if (ComeFrom.equalsIgnoreCase("1")) {
            binding.ivArrow.setVisibility(View.GONE);
        }

        binding.llSunday.setOnClickListener(view -> {
            if (!remiderDays.contains("0")) {
                remiderDays.add("0");
                binding.tvSunday.setTextColor(context.getResources().getColor(R.color.extra_light_blue));
                binding.tvSunday.setBackground(context.getResources().getDrawable(R.drawable.fill_transparent_bg));
            } else {
                remiderDays.remove("0");
                binding.tvSunday.setTextColor(context.getResources().getColor(R.color.dark_blue_gray));
                binding.tvSunday.setBackground(context.getResources().getDrawable(R.drawable.transparent_bg));
            }
            Log.e("remiderDays", TextUtils.join(",", remiderDays));
        });

        binding.llMonday.setOnClickListener(view -> {
            if (!remiderDays.contains("1")) {
                remiderDays.add("1");
                binding.tvMonday.setTextColor(context.getResources().getColor(R.color.extra_light_blue));
                binding.tvMonday.setBackground(context.getResources().getDrawable(R.drawable.fill_transparent_bg));
            } else {
                remiderDays.remove("1");
                binding.tvMonday.setTextColor(context.getResources().getColor(R.color.dark_blue_gray));
                binding.tvMonday.setBackground(context.getResources().getDrawable(R.drawable.transparent_bg));
            }
            Log.e("remiderDays", TextUtils.join(",", remiderDays));
        });

        binding.llTuesday.setOnClickListener(view -> {
            if (!remiderDays.contains("2")) {
                remiderDays.add("2");
                binding.tvTuesday.setTextColor(context.getResources().getColor(R.color.extra_light_blue));
                binding.tvTuesday.setBackground(context.getResources().getDrawable(R.drawable.fill_transparent_bg));
            } else {
                remiderDays.remove("2");
                binding.tvTuesday.setTextColor(context.getResources().getColor(R.color.dark_blue_gray));
                binding.tvTuesday.setBackground(context.getResources().getDrawable(R.drawable.transparent_bg));
            }
            Log.e("remiderDays", TextUtils.join(",", remiderDays));
        });

        binding.llWednesday.setOnClickListener(view -> {
            if (!remiderDays.contains("3")) {
                remiderDays.add("3");
                binding.tvWednesday.setTextColor(context.getResources().getColor(R.color.extra_light_blue));
                binding.tvWednesday.setBackground(context.getResources().getDrawable(R.drawable.fill_transparent_bg));
            } else {
                remiderDays.remove("3");
                binding.tvWednesday.setTextColor(context.getResources().getColor(R.color.dark_blue_gray));
                binding.tvWednesday.setBackground(context.getResources().getDrawable(R.drawable.transparent_bg));
            }
            Log.e("remiderDays", TextUtils.join(",", remiderDays));
        });

        binding.llThursday.setOnClickListener(view -> {
            if (!remiderDays.contains("4")) {
                remiderDays.add("4");
                binding.tvThursday.setTextColor(context.getResources().getColor(R.color.extra_light_blue));
                binding.tvThursday.setBackground(context.getResources().getDrawable(R.drawable.fill_transparent_bg));
            } else {
                remiderDays.remove("4");
                binding.tvThursday.setTextColor(context.getResources().getColor(R.color.dark_blue_gray));
                binding.tvThursday.setBackground(context.getResources().getDrawable(R.drawable.transparent_bg));
            }
            Log.e("remiderDays", TextUtils.join(",", remiderDays));
        });

        binding.llFriday.setOnClickListener(view -> {
            if (!remiderDays.contains("5")) {
                remiderDays.add("5");
                binding.tvFriday.setTextColor(context.getResources().getColor(R.color.extra_light_blue));
                binding.tvFriday.setBackground(context.getResources().getDrawable(R.drawable.fill_transparent_bg));
            } else {
                remiderDays.remove("5");
                binding.tvFriday.setTextColor(context.getResources().getColor(R.color.dark_blue_gray));
                binding.tvFriday.setBackground(context.getResources().getDrawable(R.drawable.transparent_bg));
            }
            Log.e("remiderDays", TextUtils.join(",", remiderDays));
        });

        binding.llSaturday.setOnClickListener(view -> {
            if (!remiderDays.contains("6")) {
                remiderDays.add("6");
                binding.tvSaturday.setTextColor(context.getResources().getColor(R.color.extra_light_blue));
                binding.tvSaturday.setBackground(context.getResources().getDrawable(R.drawable.fill_transparent_bg));
            } else {
                remiderDays.remove("6");
                binding.tvSaturday.setTextColor(context.getResources().getColor(R.color.dark_blue_gray));
                binding.tvSaturday.setBackground(context.getResources().getDrawable(R.drawable.transparent_bg));
            }
            Log.e("remiderDays", TextUtils.join(",", remiderDays));
        });
    }

    private void ShowPlaylistName() {
        if (PlaylistName.equalsIgnoreCase("")) {
            binding.tvPlaylistName.setText("Select Playlist");
        } else {
            binding.tvPlaylistName.setText(PlaylistName);
        }
        String dateStr = binding.tvTime.getText().toString();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm.S'Z' a", Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        df.setTimeZone(TimeZone.getDefault());
        String formattedDate = df.format(date);
        Log.e("TIMEZONES",formattedDate);

        binding.btnSave.setOnClickListener(view -> {
            if (PlaylistName.equalsIgnoreCase("")) {
                BWSApplication.showToast("Please select playlist name", context);
            } else if (remiderDays.size() == 0) {
                BWSApplication.showToast("Please select days", context);
            } else {
                if (BWSApplication.isNetworkConnected(context)) {
                    BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                    Call<SetReminderModel> listCall = APIClient.getClient().SetReminder(PlaylistID, UserId, CONSTANTS.FLAG_ONE,
                            formattedDate , TextUtils.join(",", remiderDays));
                    listCall.enqueue(new Callback<SetReminderModel>() {
                        @Override
                        public void onResponse(Call<SetReminderModel> call, Response<SetReminderModel> response) {
                            if (response.isSuccessful()) {
                                Log.e("remiderDays", TextUtils.join(",", remiderDays));
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

    private void prepareData(RecyclerView rvSelectPlaylist, LinearLayout llError) {
        if (BWSApplication.isNetworkConnected(context)) {
            BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
            Call<SelectPlaylistModel> listCall = APIClient.getClient().getAllPlayListing(UserId);
            listCall.enqueue(new Callback<SelectPlaylistModel>() {
                @Override
                public void onResponse(Call<SelectPlaylistModel> call, Response<SelectPlaylistModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
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
                }

                @Override
                public void onFailure(Call<SelectPlaylistModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
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
            holder.binding.cbChecked.setTag(model.get(position));
            holder.binding.cbChecked.setChecked(position == mSelectedItem);
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

                binding.cbChecked.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mSelectedItem = getAdapterPosition();
                        notifyDataSetChanged();
                        PlaylistID = model.get(mSelectedItem).getID();
                        PlaylistName = model.get(mSelectedItem).getName();
                        ShowPlaylistName();
                        dialog.dismiss();
                    }
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
        } else {
            finish();
        }
    }
}