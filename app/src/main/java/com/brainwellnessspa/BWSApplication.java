package com.brainwellnessspa;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Dialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.brainwellnessspa.DashboardModule.Adapters.DirectionAdapter;
import com.brainwellnessspa.DashboardTwoModule.AddPlaylistActivity;
import com.brainwellnessspa.DashboardTwoModule.Model.AudioDetailModel;
import com.brainwellnessspa.DashboardTwoModule.Model.SucessModel;
import com.brainwellnessspa.ReminderModule.Models.ReminderMinutesListModel;
import com.brainwellnessspa.ReminderModule.Models.ReminderSelectionModel;
import com.brainwellnessspa.ReminderModule.Models.SetReminderOldModel;
import com.brainwellnessspa.Services.PlayerJobService;
import com.brainwellnessspa.Utility.APINewClient;
import com.brainwellnessspa.Utility.AppSignatureHashHelper;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.CryptLib;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.ReminderSelectionlistLayoutBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.segment.analytics.Properties;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.Services.GlobalInitExoPlayer.getSpace;
import static com.brainwellnessspa.SplashModule.SplashScreenActivity.analytics;

public class BWSApplication extends Application {
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'playlist_table' ADD COLUMN 'PlaylistImageDetails' TEXT");
        }
    };
    private static Context mContext;
    private static BWSApplication BWSApplication;
    public static String BatteryStatus = "";
    public static String PlayerAudioId = "";
    static List<String> remiderDays = new ArrayList<>();

    public static Context getContext() {
        return mContext;
    }

    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, PlayerJobService.class);
        JobInfo.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder = new JobInfo.Builder(0, serviceComponent);
            builder.setMinimumLatency(1 * 1000); // wait at least
            builder.setOverrideDeadline(3 * 1000); // maximum delay
            //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
            //builder.setRequiresDeviceIdle(true); // device should be idle
            //builder.setRequiresCharging(false); // we don't care if the device is charging or not
            JobScheduler jobScheduler = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                jobScheduler = context.getSystemService(JobScheduler.class);
            }
            jobScheduler.schedule(builder.build());
        }
    }

    public static void callAudioDetails(String audioId, Context ctx, Activity act, String CoUserID) {
//            TODO Mansi  Hint This code is Audio Detail Dialog
        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.open_detail_page_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ctx.getResources().getColor(R.color.blue_transparent)));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        final TextView tvTitleDec = dialog.findViewById(R.id.tvTitleDec);
        final TextView tvSubDec = dialog.findViewById(R.id.tvSubDec);
        final TextView tvReadMore = dialog.findViewById(R.id.tvReadMore);
        final TextView tvSubDire = dialog.findViewById(R.id.tvSubDire);
        final TextView tvDire = dialog.findViewById(R.id.tvDire);
        final TextView tvDesc = dialog.findViewById(R.id.tvDesc);
        final TextView tvDuration = dialog.findViewById(R.id.tvDuration);
        final ImageView ivRestaurantImage = dialog.findViewById(R.id.ivRestaurantImage);
        final ImageView ivLike = dialog.findViewById(R.id.ivLike);
        final ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        final FrameLayout progressBarHolder = dialog.findViewById(R.id.progressBarHolder);
        final RelativeLayout cvImage = dialog.findViewById(R.id.cvImage);
        final LinearLayout llLike = dialog.findViewById(R.id.llLike);
        final LinearLayout llAddPlaylist = dialog.findViewById(R.id.llAddPlaylist);
        final LinearLayout llAddQueue = dialog.findViewById(R.id.llAddQueue);
        final LinearLayout llDownload = dialog.findViewById(R.id.llDownload);
        final LinearLayout llRemovePlaylist = dialog.findViewById(R.id.llRemovePlaylist);
        final LinearLayout llShuffle = dialog.findViewById(R.id.llShuffle);
        final LinearLayout llRepeat = dialog.findViewById(R.id.llRepeat);
        final LinearLayout llViewQueue = dialog.findViewById(R.id.llViewQueue);
        final RecyclerView rvDirlist = dialog.findViewById(R.id.rvDirlist);
        if (isNetworkConnected(ctx)) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.invalidate();
            Call<AudioDetailModel> listCall = APINewClient.getClient().getAudioDetail(CoUserID, "10");
            listCall.enqueue(new Callback<AudioDetailModel>() {
                @Override
                public void onResponse(Call<AudioDetailModel> call, Response<AudioDetailModel> response) {
                    try {
                        progressBar.setVisibility(View.GONE);
                        AudioDetailModel listModel = response.body();
                        cvImage.setVisibility(View.VISIBLE);
                        llLike.setVisibility(View.GONE);
                        llAddPlaylist.setVisibility(View.VISIBLE);
                        llAddQueue.setVisibility(View.GONE);
                        llDownload.setVisibility(View.VISIBLE);
                        llShuffle.setVisibility(View.VISIBLE);
                        llRepeat.setVisibility(View.VISIBLE);
                        llViewQueue.setVisibility(View.VISIBLE);
                        llRemovePlaylist.setVisibility(View.VISIBLE);

//                        if (comeFrom.equalsIgnoreCase("myPlayList") || comeFrom.equalsIgnoreCase("myLikeAudioList")) {
//                            binding.llRemovePlaylist.setVisibility(View.GONE);
//                        } else {
//                            if (MyPlaylist.equalsIgnoreCase("myPlaylist")) {
//                                binding.llRemovePlaylist.setVisibility(View.VISIBLE);
//                            } else {
//                                binding.llRemovePlaylist.setVisibility(View.GONE);
//                            }
//                        }

                        try {
                            Glide.with(ctx).load(listModel.getResponseData().get(0).getImageFile()).thumbnail(0.05f)
                                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(12))).priority(Priority.HIGH)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(ivRestaurantImage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (listModel.getResponseData().get(0).getAudioDescription().equalsIgnoreCase("")) {
                            tvTitleDec.setVisibility(View.GONE);
                            tvSubDec.setVisibility(View.GONE);
                        } else {
                            tvTitleDec.setVisibility(View.VISIBLE);
                            tvSubDec.setVisibility(View.VISIBLE);
                        }

                        tvSubDec.setText(listModel.getResponseData().get(0).getAudioDescription());
                        int linecount = tvSubDec.getLineCount();
                        if (linecount >= 4) {
                            tvReadMore.setVisibility(View.VISIBLE);
                        } else {
                            tvReadMore.setVisibility(View.GONE);
                        }
                        if (listModel.getResponseData().get(0).getAudiomastercat().equalsIgnoreCase("")) {
                            tvDesc.setVisibility(View.GONE);
                        } else {
                            tvDesc.setVisibility(View.VISIBLE);
                            tvDesc.setText(listModel.getResponseData().get(0).getAudiomastercat());
                        }
                        tvDuration.setText(listModel.getResponseData().get(0).getAudioDuration());

                        if (listModel.getResponseData().get(0).getAudioDirection().equalsIgnoreCase("")) {
                            tvSubDire.setText("");
                            tvSubDire.setVisibility(View.GONE);
                            tvDire.setVisibility(View.GONE);
                        } else {
                            tvSubDire.setText(listModel.getResponseData().get(0).getAudioDirection());
                            tvSubDire.setVisibility(View.VISIBLE);
                            tvDire.setVisibility(View.VISIBLE);
                        }

//                            if (listModel.getResponseData().get(0).getLike().equalsIgnoreCase("1")) {
//                                ivLike.setImageResource(R.drawable.ic_fill_like_icon);
//                            } else if (!listModel.getResponseData().get(0).getLike().equalsIgnoreCase("0")) {
//                                ivLike.setImageResource(R.drawable.ic_like_white_icon);
//                            }

                        tvReadMore.setOnClickListener(v12 -> {
                            final Dialog dialog1 = new Dialog(ctx);
                            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog1.setContentView(R.layout.full_desc_layout);
                            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            final TextView tvDesc = dialog1.findViewById(R.id.tvDesc);
                            final RelativeLayout tvClose = dialog1.findViewById(R.id.tvClose);
                            tvDesc.setText(listModel.getResponseData().get(0).getAudioDescription());

                            dialog1.setOnKeyListener((v3, keyCode, event) -> {
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    dialog1.dismiss();
                                    return true;
                                }
                                return false;
                            });

                            tvClose.setOnClickListener(v14 -> dialog1.dismiss());
                            dialog1.show();
                            dialog1.setCancelable(false);
                        });

                        if (listModel.getResponseData().get(0).getAudioSubCategory().equalsIgnoreCase("")) {
                            rvDirlist.setVisibility(View.GONE);
                        } else {
                            rvDirlist.setVisibility(View.VISIBLE);
                            String[] elements = listModel.getResponseData().get(0).getAudioSubCategory().split(",");
                            List<String> direction = Arrays.asList(elements);

                            DirectionAdapter directionAdapter = new DirectionAdapter(direction, ctx);
                            RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
                            rvDirlist.setLayoutManager(recentlyPlayed);
                            rvDirlist.setItemAnimator(new DefaultItemAnimator());
                            rvDirlist.setAdapter(directionAdapter);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<AudioDetailModel> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }

        llAddPlaylist.setOnClickListener(v13 -> {
            if (isNetworkConnected(ctx)) {
                showProgressBar(progressBar, progressBarHolder, act);
                Call<SucessModel> listCall = APINewClient.getClient().RemoveAudio(CoUserID, /*AudioId*/audioId, /*PlaylistId*/"34");
                listCall.enqueue(new Callback<SucessModel>() {
                    @Override
                    public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                        try {
                            if (response.isSuccessful()) {
                                hideProgressBar(progressBar, progressBarHolder, act);
                                SucessModel listModel = response.body();
                                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
//                                AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
//                                int pos = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
//
//                                if (audioPlay) {
//                                    if (AudioFlag.equalsIgnoreCase("SubPlayList")) {
//                                        Gson gson12 = new Gson();
//                                        String json12 = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson12));
//                                        Type type1 = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
//                                        }.getType();
//                                        ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList1 = gson12.fromJson(json12, type1);
//
//                                        if (!comeFrom.equalsIgnoreCase("")) {
//                                            mData.remove(position);
//                                            String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
//                                            int oldpos = pos;
//                                            if (pID.equalsIgnoreCase(PlaylistId)) {
//                                                if (mData.size() != 0) {
//                                                    if (pos == position && position < mData.size() - 1) {
//                                                        pos = pos;
//                                                    } else if (pos == position && position == mData.size() - 1) {
//                                                        pos = 0;
//                                                    } else if (pos < position && pos < mData.size() - 1) {
//                                                        pos = pos;
//                                                    } else if (pos < position && pos == mData.size() - 1) {
//                                                        pos = pos;
//                                                    } else if (pos > position && pos == mData.size()) {
//                                                        pos = pos - 1;
//                                                    }
//                                                    SharedPreferences sharedd = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
//                                                    SharedPreferences.Editor editor = sharedd.edit();
//                                                    Gson gson = new Gson();
//                                                    String json = gson.toJson(mData);
//                                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json);
//                                                    editor.putInt(CONSTANTS.PREF_KEY_position, pos);
//                                                    editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
//                                                    editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
//                                                    editor.putString(CONSTANTS.PREF_KEY_PlaylistId, PlaylistId);
//                                                    editor.putString(CONSTANTS.PREF_KEY_myPlaylist, myPlaylist);
//                                                    editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SubPlayList");
//                                                    editor.commit();
//                                                    Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
//                                                    }.getType();
//                                                    ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);
//                                                    listSize = arrayList.size();
//                                                    for (int i = 0; i < listSize; i++) {
//                                                        MainPlayModel mainPlayModel = new MainPlayModel();
//                                                        mainPlayModel.setID(arrayList.get(i).getID());
//                                                        mainPlayModel.setName(arrayList.get(i).getName());
//                                                        mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
//                                                        mainPlayModel.setPlaylistID(arrayList.get(i).getPlaylistID());
//                                                        mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
//                                                        mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
//                                                        mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
//                                                        mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
//                                                        mainPlayModel.setLike(arrayList.get(i).getLike());
//                                                        mainPlayModel.setDownload(arrayList.get(i).getDownload());
//                                                        mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
//                                                        mainPlayModelList.add(mainPlayModel);
//                                                    }
//                                                    SharedPreferences sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
//                                                    SharedPreferences.Editor editor1 = sharedz.edit();
//                                                    Gson gsonz = new Gson();
//                                                    String jsonz = gsonz.toJson(mainPlayModelList);
//                                                    editor1.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
//                                                    editor1.commit();
//                                                    if (player != null) {
//                                                        player.removeMediaItem(oldpos);
//                                                        player.setPlayWhenReady(true);
//                                                    }
//                                                    finish();
//                                                }
//                                            }
//                                            finish();
//                                        } else {
//                                            mainPlayModelList.remove(pos);
//                                            arrayList1.remove(pos);
//                                            String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
//                                            if (pID.equalsIgnoreCase(PlaylistId)) {
//                                                int oldpos = pos;
//                                                if (mainPlayModelList.size() != 0) {
//                                                    if (pos < mainPlayModelList.size() - 1) {
//                                                        pos = pos;
//                                                    } else if (pos == mainPlayModelList.size() - 1) {
//                                                        pos = 0;
//                                                    } else if (pos == mainPlayModelList.size()) {
//                                                        pos = 0;
//                                                    } else if (pos > mainPlayModelList.size()) {
//                                                        pos = pos - 1;
//                                                    }
//                                                    SharedPreferences sharedd = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
//                                                    SharedPreferences.Editor editor = sharedd.edit();
//                                                    Gson gson = new Gson();
//                                                    String json = gson.toJson(mainPlayModelList);
//                                                    String json1 = gson.toJson(arrayList1);
//                                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json1);
//                                                    editor.putString(CONSTANTS.PREF_KEY_audioList, json);
//                                                    editor.putInt(CONSTANTS.PREF_KEY_position, pos);
//                                                    editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
//                                                    editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
//                                                    editor.putString(CONSTANTS.PREF_KEY_PlaylistId, PlaylistId);
//                                                    editor.putString(CONSTANTS.PREF_KEY_myPlaylist, myPlaylist);
//                                                    editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SubPlayList");
//                                                    editor.commit();
////                                                if(mainPlayModelList.size()==1){
////                                                    miniPlayer = 1;
////                                                    audioClick = true;
////                                                    callNewPlayerRelease();
////                                                }else {
//                                                    if (player != null) {
//                                                        player.removeMediaItem(oldpos);
//                                                    }
////                                                }
//                                                    Intent i = new Intent(ctx, AudioPlayerActivity.class);
//                                                    i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                                                    ctx.startActivity(i);
//                                                    finish();
//                                                    overridePendingTransition(0, 0);
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<SucessModel> call, Throwable t) {
                        hideProgressBar(progressBar, progressBarHolder, act);
                    }
                });
            } else {
                showToast(ctx.getString(R.string.no_server_found), ctx);
            }
        });
        llAddPlaylist.setOnClickListener(view11 -> {

//                comeAddPlaylist = 2;
            Intent i = new Intent(ctx, AddPlaylistActivity.class);
            i.putExtra("AudioId", audioId);
            i.putExtra("ScreenView", "Audio Details Screen");
            i.putExtra("PlaylistID", "");
            i.putExtra("PlaylistName", "");
            i.putExtra("PlaylistImage", "");
            i.putExtra("PlaylistType", "");
            i.putExtra("Liked", "0");
            ctx.startActivity(i);
        });

        dialog.setOnKeyListener((v1, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog.dismiss();
                return true;
            }
            return false;
        });
        dialog.show();
        dialog.setCancelable(false);
    }

    public static void getReminderDay(Context ctx, Activity act, String CoUSERID, String playlistID, String playlistName) {
        ReminderSelectionModel[] reminderSelectionModel = new ReminderSelectionModel[] {
                new ReminderSelectionModel("Sunday"),
                new ReminderSelectionModel("Monday"),
                new ReminderSelectionModel("Tuesday"),
                new ReminderSelectionModel("Wednesday"),
                new ReminderSelectionModel("Thursday"),
                new ReminderSelectionModel("Friday"),
                new ReminderSelectionModel("Saturday"),};

        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.select_days_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ctx.getResources().getColor(R.color.blue_transparent)));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final LinearLayout llBack = dialog.findViewById(R.id.llBack);
        final RecyclerView rvSelectDay = dialog.findViewById(R.id.rvSelectDay);
        final TextView tvPlaylistName = dialog.findViewById(R.id.tvPlaylistName);
        final TextView tvSelectAll = dialog.findViewById(R.id.tvSelectAll);
        final TextView tvUnSelectAll = dialog.findViewById(R.id.tvUnSelectAll);
        final Button btnNext = dialog.findViewById(R.id.btnNext);

        tvPlaylistName.setText(playlistName);
        llBack.setOnClickListener(view12 -> dialog.dismiss());
        remiderDays.clear();
        dialog.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog.dismiss();
                return true;
            }
            return false;
        });
        llBack.setOnClickListener(v -> dialog.dismiss());
        RecyclerView.LayoutManager manager = new LinearLayoutManager(ctx);
        rvSelectDay.setLayoutManager(manager);
        rvSelectDay.setItemAnimator(new DefaultItemAnimator());
        ReminderSelectionListAdapter adapter = new ReminderSelectionListAdapter(reminderSelectionModel, act, ctx, tvSelectAll, tvUnSelectAll,
                btnNext, CoUSERID, playlistID, playlistName, dialog);
        rvSelectDay.setAdapter(adapter);

        Log.e("remiderDays", TextUtils.join(",", remiderDays));
        dialog.show();
        dialog.setCancelable(false);
    }

    public static void getReminderTime(Context ctx, Activity act, String coUSERID, String playlistID, String playlistName, Dialog dialogOld) {
        ArrayList<ReminderMinutesListModel> reminderMinutesModel = new ArrayList<>();

        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.select_timeslot_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ctx.getResources().getColor(R.color.blue_transparent)));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final LinearLayout llBack = dialog.findViewById(R.id.llBack);
        final Button btnSave = dialog.findViewById(R.id.btnSave);
        final TextView tvPlaylistName = dialog.findViewById(R.id.tvPlaylistName);
        final TextView tvGoBack = dialog.findViewById(R.id.tvGoBack);
        final TextView tvTime = dialog.findViewById(R.id.tvTime);
        final TimePicker timePicker = dialog.findViewById(R.id.timePicker);
        final RecyclerView rvSelectMinutesTimeSlot = dialog.findViewById(R.id.rvSelectMinutesTimeSlot);
        final RecyclerView rvSelectHoursTimeSlot = dialog.findViewById(R.id.rvSelectHoursTimeSlot);
        final ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        final FrameLayout progressBarHolder = dialog.findViewById(R.id.progressBarHolder);
        tvPlaylistName.setText(playlistName);
        llBack.setOnClickListener(view12 -> {
            dialogOld.dismiss();
            dialog.dismiss();
        });
        timePicker.setIs24HourView(true);

        ReminderMinutesListModel[] minutesListModels = new ReminderMinutesListModel[]{};


        dialog.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialogOld.dismiss();
                dialog.dismiss();
                return true;
            }
            return false;
        });

        RecyclerView.LayoutManager manager = new LinearLayoutManager(ctx);
        rvSelectMinutesTimeSlot.setLayoutManager(manager);
        rvSelectMinutesTimeSlot.setItemAnimator(new DefaultItemAnimator());

        ReminderMinutesListAdapter adapter = new ReminderMinutesListAdapter(reminderMinutesModel, act, ctx, coUSERID, playlistID, playlistName, dialog);
        rvSelectMinutesTimeSlot.setAdapter(adapter);
        btnSave.setOnClickListener(v -> {
            Log.e("remiderDays Done", TextUtils.join(",", remiderDays));
            if (isNetworkConnected(ctx)) {
                showProgressBar(progressBar, progressBarHolder, act);
                Call<SetReminderOldModel> listCall = APINewClient.getClient().getSetReminder(coUSERID, playlistID,
                        TextUtils.join(",", remiderDays), "09:00 am", CONSTANTS.FLAG_ONE);
                listCall.enqueue(new Callback<SetReminderOldModel>() {
                    @Override
                    public void onResponse(Call<SetReminderOldModel> call, Response<SetReminderOldModel> response) {
                        try {
                            SetReminderOldModel listModel = response.body();
                            if (listModel.getResponseCode().equalsIgnoreCase(ctx.getString(R.string.ResponseCodesuccess))) {
                                dialog.dismiss();
                                dialogOld.dismiss();
                                remiderDays.clear();
                                hideProgressBar(progressBar, progressBarHolder, act);
                                showToast(listModel.getResponseMessage(), act);
                                dialog.dismiss();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<SetReminderOldModel> call, Throwable t) {
                        hideProgressBar(progressBar, progressBarHolder, act);
                    }
                });
            } else {
                showToast(ctx.getString(R.string.no_server_found), ctx);
            }
        });

        tvGoBack.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
        dialog.setCancelable(false);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        BWSApplication = this;
    }

    private static class ReminderMinutesListAdapter extends RecyclerView.Adapter<ReminderMinutesListAdapter.MyViewHolder> {
        private ArrayList<ReminderMinutesListModel> minutesListModels;
        Activity act;
        Context ctx;
        TextView tvSelectAll, tvUnSelectAll;
        Button btnNext;
        String CoUSERID, PlaylistID, PlaylistName;
        Dialog dialogOld;

        public ReminderMinutesListAdapter(ArrayList<ReminderMinutesListModel> minutesListModels, Activity act, Context ctx
                , String CoUSERID, String PlaylistID, String PlaylistName, Dialog dialogOld) {
            this.minutesListModels = minutesListModels;
            this.act = act;
            this.ctx = ctx;
            this.tvSelectAll = tvSelectAll;
            this.tvUnSelectAll = tvUnSelectAll;
            this.btnNext = btnNext;
            this.CoUSERID = CoUSERID;
            this.PlaylistID = PlaylistID;
            this.PlaylistName = PlaylistName;
            this.dialogOld = dialogOld;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ReminderSelectionlistLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.reminder_selectionlist_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvDay.setText(minutesListModels.get(position).getMinutes());
        }

        @Override
        public int getItemCount() {
            return minutesListModels.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ReminderSelectionlistLayoutBinding binding;

            public MyViewHolder(ReminderSelectionlistLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    private static class ReminderSelectionListAdapter extends RecyclerView.Adapter<ReminderSelectionListAdapter.MyViewHolder> {
        private ReminderSelectionModel[] selectionModels;
        Activity act;
        Context ctx;
        TextView tvSelectAll, tvUnSelectAll;
        Button btnNext;
        String CoUSERID, PlaylistID, PlaylistName;
        Dialog dialogOld;

        public ReminderSelectionListAdapter(ReminderSelectionModel[] selectionModels, Activity act, Context ctx,
                                            TextView tvSelectAll, TextView tvUnSelectAll, Button btnNext, String CoUSERID, String PlaylistID, String PlaylistName
                , Dialog dialogOld) {
            this.selectionModels = selectionModels;
            this.act = act;
            this.ctx = ctx;
            this.tvSelectAll = tvSelectAll;
            this.tvUnSelectAll = tvUnSelectAll;
            this.btnNext = btnNext;
            this.CoUSERID = CoUSERID;
            this.PlaylistID = PlaylistID;
            this.PlaylistName = PlaylistName;
            this.dialogOld = dialogOld;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ReminderSelectionlistLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.reminder_selectionlist_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final ReminderSelectionModel myListData = selectionModels[position];
            holder.binding.tvDay.setText(selectionModels[position].getDay());
            holder.binding.tvDay.setOnClickListener(v -> {
                if (!remiderDays.contains(selectionModels[position].getDay())) {
                    remiderDays.add(selectionModels[position].getDay());
                } else {
                    remiderDays.remove(selectionModels[position].getDay());
                }
                Log.e("remiderDays", TextUtils.join(",", remiderDays));
                if (remiderDays.size() == selectionModels.length) {
                    tvSelectAll.setVisibility(View.GONE);
                    tvUnSelectAll.setVisibility(View.VISIBLE);
                } else {
                    tvSelectAll.setVisibility(View.VISIBLE);
                    tvUnSelectAll.setVisibility(View.GONE);
                }
                notifyDataSetChanged();
            });

            tvSelectAll.setOnClickListener(v -> {
                remiderDays.clear();
                for (int i = 0; i < selectionModels.length; i++) {
                    if (!remiderDays.contains(selectionModels[i].getDay())) {
                        remiderDays.add(selectionModels[i].getDay());
                        tvSelectAll.setVisibility(View.GONE);
                        tvUnSelectAll.setVisibility(View.VISIBLE);

                    }
                    Log.e("remiderDays", TextUtils.join(",", remiderDays));
                }
                notifyDataSetChanged();
            });

            tvUnSelectAll.setOnClickListener(v -> {
                remiderDays.clear();
                tvUnSelectAll.setVisibility(View.GONE);
                tvSelectAll.setVisibility(View.VISIBLE);
                if (!TextUtils.join(",", remiderDays).equalsIgnoreCase("")) {
                    Log.e("remiderDays", TextUtils.join(",", remiderDays));
                } else {
                    Log.e("remiderDays", "days cleared");
                }
                notifyDataSetChanged();
            });

            if (remiderDays.contains(selectionModels[position].getDay())) {
                holder.binding.tvDay.setTextColor(act.getResources().getColor(R.color.blue));
            } else {
                holder.binding.tvDay.setTextColor(act.getResources().getColor(R.color.dim_light_gray));
            }

            btnNext.setOnClickListener(v -> {
                if (remiderDays.size() == 0) {
                    showToast("Please select days", ctx);
                } else {
//                    Intent i = new Intent(ctx, TimeViewActivity.class);
//                    act.startActivity(i);
                    getReminderTime(ctx, act, CoUSERID, PlaylistID, PlaylistName, dialogOld);
                }
            });
        }

        @Override
        public int getItemCount() {
            return selectionModels.length;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ReminderSelectionlistLayoutBinding binding;

            public MyViewHolder(ReminderSelectionlistLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }


    public static MeasureRatio measureRatio(Context context, float outerMargin, float aspectX, float aspectY, float proportion, float innerMargin) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        try {
            WindowManager windowmanager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        } catch (Exception e) {
            e.printStackTrace();
        }
        float width = displayMetrics.widthPixels / displayMetrics.density;
        float widthImg = ((width - outerMargin) * proportion) - innerMargin;
        float height = widthImg * aspectY / aspectX;
        //Log.e("width.........", "" + context.getClass().getSimpleName()+","+width);
//        //Log.e("widthImg.........", "" + context.getClass().getSimpleName()+","+widthImg);
//        //Log.e("height...........", "" + context.getClass().getSimpleName()+","+height);
//        //Log.e("displayMetrics.density...........", "" + context.getClass().getSimpleName()+","+displayMetrics.density);
        return new MeasureRatio(widthImg, height, displayMetrics.density, proportion);
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String appStatus(Context ctx) {
        Boolean isInBackground = false;
        String myappStatus = "";
        ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(myProcess);
        isInBackground = myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
        if (isInBackground) {
            myappStatus = ctx.getString(R.string.Background);
            Log.e("myappStatus", ctx.getString(R.string.Background));
        } else {
            myappStatus = ctx.getString(R.string.Foreground);
            Log.e("myappStatus", ctx.getString(R.string.Foreground));
        }
        return myappStatus;
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public static void addToSegment(String TagName, Properties properties, String methodName) {
        long mySpace;
        mySpace = getSpace();
        int batLevel = 0;
        // Get the battery percentage and store it in a INT variable
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager bm = (BatteryManager) getContext().getSystemService(BATTERY_SERVICE);
            batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        }
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        //should check null because in airplane mode it will be null
        NetworkCapabilities nc;
        float downSpeed = 0;
        float upSpeed = 0;
        if (isNetworkConnected(getContext())) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
                downSpeed = (float) nc.getLinkDownstreamBandwidthKbps() / 1000;
                upSpeed = (float) (nc.getLinkUpstreamBandwidthKbps() / 1000);
            }
        }
        properties.putValue("deviceSpace", mySpace + " MB");
        properties.putValue("batteryLevel", batLevel + " %");
        properties.putValue("batteryState", BatteryStatus);
        properties.putValue("internetDownSpeed", downSpeed + " Mbps");
        properties.putValue("internetUpSpeed", upSpeed + " Mbps");
        try {
            if (methodName.equalsIgnoreCase("track")) {
                analytics.track(TagName, properties);
            } else if (methodName.equalsIgnoreCase("screen")) {
                analytics.screen(TagName, properties);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getKey(Context context) {
        AppSignatureHashHelper appSignatureHashHelper = new AppSignatureHashHelper(context);
        String key = appSignatureHashHelper.getAppSignatures().get(0);
        SharedPreferences shared = context.getSharedPreferences(CONSTANTS.PREF_KEY_Splash, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(CONSTANTS.PREF_KEY_SplashKey, appSignatureHashHelper.getAppSignatures().get(0));
        editor.commit();
        return key;
    }

    public static void showToast(String message, Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            Toast toast = new Toast(context);
            View view = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
            TextView tvMessage = view.findViewById(R.id.tvMessage);
            tvMessage.setText(message);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 35);
            toast.setView(view);
            toast.show();
        } else {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.toast_above_version_layout);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            TextView tvMessage = dialog.findViewById(R.id.tvMessage);
            tvMessage.setText(message);
            dialog.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    return true;
                }
                return false;
            });
            dialog.show();
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                dialog.hide();
            }, 2 * 600);

            dialog.setCancelable(true);
//            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
//            toast.show();
        }
    }

    public static String getProgressDisplayLine(long currentBytes, long totalBytes) {
        return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes);
    }

    private static String getBytesToMBString(long bytes) {
        return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00));
    }

    public static synchronized BWSApplication getInstance() {
        return BWSApplication;
    }

    public static void hideProgressBar(ProgressBar progressBar, FrameLayout progressBarHolder, Activity ctx) {
        try {
            progressBarHolder.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            ctx.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showProgressBar(ProgressBar progressBar, FrameLayout progressBarHolder, Activity ctx) {
        try {
            progressBarHolder.setVisibility(View.VISIBLE);
            ctx.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,3}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isNetworkConnected(Context context) {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            boolean flag = false;
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//For 3G check
            boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
//For WiFi Check
            boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
            flag = !(!is3g && !isWifi);
            return flag;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static String securityKey() {
        String key;
        String DeviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String AES = "OsEUHhecSs4gRGcy2vMQs1s/XajBrLGADR71cKMRNtA=";
        String RSA = "KlWxBHfKPGkkeTjkT7IEo32bZW8GlVCPq/nvVFuYfIY=";
        String TDES = "1dpra0SZhVPpiUQvikMvkDxEp7qLLJL9pe9G6Apg01g=";
        String SHA1 = "Ey8rBCHsqITEbh7KQKRmYObCGBXqFnvtL5GjMFQWHQo=";
        String MD5 = "/qc2rO3RB8Z/XA+CmHY0tCaJch9a5BdlQW1xb7db+bg=";

        Calendar calendar = Calendar.getInstance();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTime(new Date());
        SimpleDateFormat outputFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateAsString = outputFmt.format(calendar.getTime());
        //        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //2019-11-21 06:45:32
//        String currentDateandTime = sdf.format(new Date());
        String finalKey = "";
        HashMap<String, String> hash_map = new HashMap<String, String>();
        hash_map.put("AES", AES);
        hash_map.put("RSA", RSA);
        hash_map.put("TDES", TDES);
        hash_map.put("SHA1", SHA1);
        hash_map.put("MD5", MD5);

        Random random = new Random();
        List<String> keys = new ArrayList<String>(hash_map.keySet());
        String randomKey = keys.get(random.nextInt(keys.size()));
        String value = hash_map.get(randomKey);
        key = DeviceId + "." + dateAsString + "." + randomKey + "." + value;

        try {
            finalKey = ProgramForAES(key);
//            System.out.println(finalKey);
        } catch (Exception e) {
        }
        return finalKey;
    }

    public static String ProgramForAES(String baseString) {
        String cipher = "";
        try {
            String key = "5785abf057d4eea9e59151f75a6fadb724768053df2acdfabb68f2b946b972c6";
            CryptLib cryptLib = new CryptLib();
            cipher = cryptLib.encryptPlainTextWithRandomIV(baseString, key);
//            println("cipherText" + cipher);
            String decryptedString = cryptLib.decryptCipherTextWithRandomIV(cipher, key);
//            println("decryptedString" + decryptedString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipher;
    }

    public static void turnOffDozeMode(Context context) {  //you can use with or without passing context
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (pm.isIgnoringBatteryOptimizations(packageName)) // if you want to desable doze mode for this package
                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            else { // if you want to enable doze mode
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
            }
            context.startActivity(intent);
        }
    }
}