package com.brainwellnessspa;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.CheckBox;
import android.widget.EditText;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.brainwellnessspa.DashboardOldModule.Adapters.DirectionAdapter;
import com.brainwellnessspa.DashboardOldModule.Models.ViewAllAudioListModel;
import com.brainwellnessspa.DashboardOldModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia;
import com.brainwellnessspa.reminderModule.models.ReminderMinutesListModel;
import com.brainwellnessspa.reminderModule.models.ReminderSelectionModel;
import com.brainwellnessspa.reminderModule.models.SetReminderOldModel;
import com.brainwellnessspa.RoomDataBase.AudioDatabase;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.RoomDataBase.DownloadPlaylistDetails;
import com.brainwellnessspa.Services.GlobalInitExoPlayer;
import com.brainwellnessspa.Services.PlayerJobService;
import com.brainwellnessspa.Utility.APINewClient;
import com.brainwellnessspa.Utility.AppSignatureHashHelper;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.CryptLib;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.dashboardModule.activities.AddPlaylistActivity;
import com.brainwellnessspa.dashboardModule.models.AudioDetailModel;
import com.brainwellnessspa.dashboardModule.models.HomeScreenModel;
import com.brainwellnessspa.dashboardModule.models.PlaylistDetailsModel;
import com.brainwellnessspa.dashboardModule.models.SucessModel;
import com.brainwellnessspa.databinding.ReminderSelectionlistLayoutBinding;
import com.brainwellnessspa.databinding.ReminderTimelistLayoutBinding;
import com.brainwellnessspa.userModuleTwo.activities.SplashActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
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

import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.isDownloading;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.getSpace;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.hundredVolume;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.player;

public class BWSApplication extends Application {
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'playlist_table' ADD COLUMN 'PlaylistImageDetails' TEXT");
        }
    };
    public static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'playlist_table' ADD COLUMN 'UserID' TEXT");
            database.execSQL("ALTER TABLE 'audio_table' ADD COLUMN 'UserID' TEXT");
        }
    };
    public static String BatteryStatus = "", IsLock;
    public static long oldSongPos = 0;
    public static String PlayerAudioId = "";
    public static int playlistDetailRefresh = 0;
    public static boolean AudioInterrupted = false, logout = false;
    public static Analytics analytics;
    public static List<String> downloadAudioDetailsList = new ArrayList<>();
    public static List<DownloadAudioDetails> playlistDownloadAudioDetailsList = new ArrayList<>();
    static List<String> remiderDays = new ArrayList<>();
    static Context mContext;
    static BWSApplication BWSApplication;
    static String currantTime = "", am_pm, hourString, minuteSting;
    static int Chour, Cminute;

    static TextView tvTime;
    public static AudioDatabase DB;
    public static int comeReminder = 0, isPlayPlaylist = 0;

    public static LocalBroadcastManager localBroadcastManager;
    public static Intent localIntent;

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

    public static List<String> GetAllMediaDownload(Context ctx) {
        SharedPreferences shared1 =
            ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        String UserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "");
        String CoUserID = shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "");
        DB = getAudioDataBase(ctx);
        DB.taskDao()
                .geAllDataBYDownloadedForAll("Complete").observe((LifecycleOwner) ctx, audioList -> {
            downloadAudioDetailsList = audioList;
            DB.taskDao().geAllDataBYDownloadedForAll("Complete").removeObserver(audioListx -> {
            });
        });
        return downloadAudioDetailsList;
    }

    private static void CallObserverMethodGetAllMedia(Context ctx) {
        SharedPreferences shared1 =
            ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        String UserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "");
        String CoUserID = shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "");
        DB = getAudioDataBase(ctx);
        DB.taskDao()
                .geAllData1LiveForAll().observe((LifecycleOwner) ctx, audioList -> {
            playlistDownloadAudioDetailsList = audioList;

        });
    }

    private static void GetPlaylistDetail(Activity act, Context ctx, String PlaylistID,
                                          LinearLayout llDownload, ImageView ivDownloads, int songSize) {
        SharedPreferences shared1 =
                ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        String UserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "");
        String CoUserID = shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "");
        DB = getAudioDataBase(ctx);
        DB.taskDao()
                .getPlaylist1(PlaylistID,CoUserID).observe((LifecycleOwner) ctx, audioList -> {

            if (audioList.size() != 0) {
                ivDownloads.setImageResource(R.drawable.ic_download_done_icon);
                llDownload.setClickable(false);
                llDownload.setEnabled(false);
                ivDownloads.setColorFilter(act.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
            } else if (songSize == 0) {
                ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                llDownload.setClickable(false);
                llDownload.setEnabled(false);
                ivDownloads.setColorFilter(act.getResources().getColor(R.color.light_gray), PorterDuff.Mode.SRC_IN);
            } else if (audioList.size() == 0) {
                llDownload.setClickable(true);
                llDownload.setEnabled(true);
                ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                ivDownloads.setColorFilter(act.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
            }
        });
    }

    public static void callAudioDetails(String audioId, Context ctx, Activity act, String CoUserID, String comeFrom,
                                        List<DownloadAudioDetails> mDataDownload,
                                        List<ViewAllAudioListModel.ResponseData.Detail> mDataViewAll,
                                        List<PlaylistDetailsModel.ResponseData.PlaylistSong> mDataPlaylist,
                                        List<MainPlayModel> mDataPlayer, int position) {
//            TODO Mansi  Hint This code is Audio Detail Dialog
        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.open_detail_page_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ctx.getResources().getColor(R.color.blue_transparent_extra)));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        final TextView tvTitleDec = dialog.findViewById(R.id.tvTitleDec);
        final TextView tvName = dialog.findViewById(R.id.tvName);
        final TextView tvSubDec = dialog.findViewById(R.id.tvSubDec);
        final TextView tvReadMore = dialog.findViewById(R.id.tvReadMore);
        final TextView tvSubDire = dialog.findViewById(R.id.tvSubDire);
        final TextView tvDire = dialog.findViewById(R.id.tvDire);
        final TextView tvDesc = dialog.findViewById(R.id.tvDesc);
        final TextView tvDuration = dialog.findViewById(R.id.tvDuration);
        final TextView tvDownloads = dialog.findViewById(R.id.tvDownloads);
        final ImageView ivRestaurantImage = dialog.findViewById(R.id.ivRestaurantImage);
        final ImageView ivDownloads = dialog.findViewById(R.id.ivDownloads);
        final ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        final FrameLayout progressBarHolder = dialog.findViewById(R.id.progressBarHolder);
        final RelativeLayout cvImage = dialog.findViewById(R.id.cvImage);
        final LinearLayout llAddPlaylist = dialog.findViewById(R.id.llAddPlaylist);
        final LinearLayout llDownload = dialog.findViewById(R.id.llDownload);
        final LinearLayout llBack = dialog.findViewById(R.id.llBack);
        final LinearLayout llRemovePlaylist = dialog.findViewById(R.id.llRemovePlaylist);
        final RecyclerView rvDirlist = dialog.findViewById(R.id.rvDirlist);
        GetAllMediaDownload(ctx);
        dialog.setOnKeyListener((v1, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog.dismiss();
                return true;
            }
            return false;
        });

        llBack.setOnClickListener(v -> dialog.dismiss());
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
        String PlayFrom = shared.getString(CONSTANTS.PREF_KEY_PlayFrom, "");


        if (isNetworkConnected(ctx)) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.invalidate();
            Call<AudioDetailModel> listCall = APINewClient.getClient().getAudioDetail(CoUserID, audioId);
            listCall.enqueue(new Callback<AudioDetailModel>() {
                @Override
                public void onResponse(Call<AudioDetailModel> call, Response<AudioDetailModel> response) {
                    try {
                        progressBar.setVisibility(View.GONE);
                        AudioDetailModel listModel = response.body();
                        cvImage.setVisibility(View.VISIBLE);
                        llAddPlaylist.setVisibility(View.VISIBLE);
                        llDownload.setVisibility(View.VISIBLE);

                        if (comeFrom.equalsIgnoreCase("audioPlayer")) {
                            if (PlayFrom.equalsIgnoreCase("Created")) {
                                llRemovePlaylist.setVisibility(View.VISIBLE);
                            } else {
                                llRemovePlaylist.setVisibility(View.GONE);
                            }
                        }
                        String AudioFile = "", PlaylistId = "", audioFileName = "";
                        if (comeFrom.equalsIgnoreCase("downloadList")) {
                            AudioFile = mDataDownload.get(position).getAudioFile();
                            PlaylistId = mDataDownload.get(position).getPlaylistId();
                            audioFileName = mDataDownload.get(position).getName();
                        } else if (comeFrom.equalsIgnoreCase("playlist")) {
                            AudioFile = mDataPlaylist.get(position).getAudioFile();
                            PlaylistId = mDataPlaylist.get(position).getPlaylistID();
                            audioFileName = mDataPlaylist.get(position).getName();
                        } else if (comeFrom.equalsIgnoreCase("viewAllAudioList")) {
                            AudioFile = mDataViewAll.get(position).getAudioFile();
                            PlaylistId = "";
                            audioFileName = mDataViewAll.get(position).getName();
                        } else if (comeFrom.equalsIgnoreCase("audioPlayer")) {
                            AudioFile = mDataPlayer.get(position).getAudioFile();
                            PlaylistId = mDataPlayer.get(position).getPlaylistID();
                            audioFileName = mDataPlayer.get(position).getName();
                        }
                        GetMedia(AudioFile, ctx, audioFileName, ivDownloads, tvDownloads, llDownload);
                        try {
                            Glide.with(ctx).load(listModel.getResponseData().get(0).getImageFile()).thumbnail(0.05f)
                                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(32))).priority(Priority.HIGH)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(ivRestaurantImage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        tvName.setText(listModel.getResponseData().get(0).getName());
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
        llDownload.setOnClickListener(view -> {
            ivDownloads.setImageResource(R.drawable.ic_download_done_icon);
            ivDownloads.setColorFilter(ctx.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
            llDownload.setClickable(false);
            llDownload.setEnabled(false);
            callDownload(comeFrom, mDataDownload, mDataViewAll, mDataPlaylist, mDataPlayer, position, ctx, ivDownloads, act, llDownload);
        });
        if (comeFrom.equalsIgnoreCase("downloadList")) {

        } else if (comeFrom.equalsIgnoreCase("playlist")) {

        } else if (comeFrom.equalsIgnoreCase("viewAllAudioList")) {

        } else if (comeFrom.equalsIgnoreCase("audioPlayer")) {

        }

        llRemovePlaylist.setOnClickListener(v13 -> {
            SharedPreferences sharedd = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            String AudioFlag = sharedd.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
            String myPlaylist = sharedd.getString(CONSTANTS.PREF_KEY_PlayFrom, "0");
            int pos = sharedd.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
            String pID = sharedd.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "0");
            String pName = sharedd.getString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "");
            String PlaylistId = "";

            if (comeFrom.equalsIgnoreCase("playlist")) {
                PlaylistId = mDataPlaylist.get(0).getPlaylistID();
            } else if (comeFrom.equalsIgnoreCase("audioPlayer")) {
                PlaylistId = mDataPlayer.get(0).getPlaylistID();
            }
            String pids = PlaylistId;
            if (AudioFlag.equalsIgnoreCase("playlist") && pID.equalsIgnoreCase(PlaylistId) && mDataPlayer.size() == 1) {
                showToast("Currently you play this playlist, you can't remove last audio", act);
            } else {
                if (isNetworkConnected(ctx)) {
                    showProgressBar(progressBar, progressBarHolder, act);
                    Call<SucessModel> listCall = APINewClient.getClient().RemoveAudio(CoUserID, audioId, PlaylistId);
                    listCall.enqueue(new Callback<SucessModel>() {
                        @Override
                        public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                            if (response.isSuccessful()) {
                                hideProgressBar(progressBar, progressBarHolder, act);
                                SucessModel listModel = response.body();
                                if (AudioFlag.equalsIgnoreCase("playlist")) {
                                    Gson gson12 = new Gson();
                                    String json12 = shared.getString(CONSTANTS.PREF_KEY_MainAudioList, String.valueOf(gson12));
                                    Type type1 = new TypeToken<ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>>() {
                                    }.getType();
                                    ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong> arrayList1 = gson12.fromJson(json12, type1);
                                    int pos = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                                    if (comeFrom.equalsIgnoreCase("playlist")) {
                                        mDataPlaylist.remove(position);
                                        String pID = shared.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "0");
                                        int oldpos = pos;
                                        if (pID.equalsIgnoreCase(pids)) {
                                            if (mDataPlaylist.size() != 0) {
                                                if (pos == position && position < mDataPlaylist.size() - 1) {
                                                    pos = pos;
                                                } else if (pos == position && position == mDataPlaylist.size() - 1) {
                                                    pos = 0;
                                                } else if (pos < position && pos < mDataPlaylist.size() - 1) {
                                                    pos = pos;
                                                } else if (pos < position && pos == mDataPlaylist.size() - 1) {
                                                    pos = pos;
                                                } else if (pos > position && pos == mDataPlaylist.size()) {
                                                    pos = pos - 1;
                                                }
                                                SharedPreferences sharedd = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedd.edit();
                                                Gson gson = new Gson();
                                                String json = gson.toJson(mDataPlaylist);
                                                editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json);
                                                editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, pos);
                                                editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, pids);
                                                editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, pName);
                                                editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "Created");
                                                editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "playlist");
                                                editor.apply();
                                                Type type = new TypeToken<ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>>() {
                                                }.getType();
                                                ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);
                                                int listSize = arrayList.size();
                                                for (int i = 0; i < listSize; i++) {
                                                    MainPlayModel mainPlayModel = new MainPlayModel();
                                                    mainPlayModel.setID(arrayList.get(i).getId());
                                                    mainPlayModel.setName(arrayList.get(i).getName());
                                                    mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                                                    mainPlayModel.setPlaylistID(arrayList.get(i).getPlaylistID());
                                                    mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                                                    mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                                                    mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                                                    mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                                                    mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                                                    mDataPlayer.add(mainPlayModel);
                                                }
                                                SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor1 = sharedz.edit();
                                                Gson gsonz = new Gson();
                                                String jsonz = gsonz.toJson(mDataPlayer);
                                                editor1.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
                                                editor1.commit();
                                                if (player != null) {
                                                    player.removeMediaItem(oldpos);
                                                    player.setPlayWhenReady(true);
                                                }
                                                dialog.dismiss();
                                            }
                                        }

                                        localIntent.putExtra("MyReminder", "update");
                                        localBroadcastManager.sendBroadcast(localIntent);
                                        dialog.dismiss();
                                    } else if (comeFrom.equalsIgnoreCase("audioPlayer")) {
                                        mDataPlayer.remove(pos);
                                        arrayList1.remove(pos);
                                        String pID = shared.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "0");
                                        if (pID.equalsIgnoreCase(pids)) {
                                            int oldpos = pos;
                                            if (mDataPlayer.size() != 0) {
                                                if (pos < mDataPlayer.size() - 1) {
                                                    pos = pos;
                                                } else if (pos == mDataPlayer.size() - 1) {
                                                    pos = 0;
                                                } else if (pos == mDataPlayer.size()) {
                                                    pos = 0;
                                                } else if (pos > mDataPlayer.size()) {
                                                    pos = pos - 1;
                                                }
                                                SharedPreferences sharedd = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedd.edit();
                                                Gson gson = new Gson();
                                                String json = gson.toJson(mDataPlayer);
                                                String json1 = gson.toJson(arrayList1);
                                                editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json1);
                                                editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, json);
                                                editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, pos);
                                                editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, pids);
                                                editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, pName);
                                                editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "Created");
                                                editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "playlist");
                                                editor.apply();
//                                                if(mainPlayModelList.size()==1){
//                                                    miniPlayer = 1;
//                                                    audioClick = true;
//                                                    callNewPlayerRelease();
//                                                }else {
                                                if (player != null) {
                                                    player.removeMediaItem(oldpos);
                                                }
//                                                }
                                                dialog.dismiss();
                                                localIntent = new Intent("Reminder");
                                                localBroadcastManager = LocalBroadcastManager.getInstance(ctx);
                                                localIntent.putExtra("MyReminder", "update");
                                                localBroadcastManager.sendBroadcast(localIntent);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<SucessModel> call, Throwable t) {
                            hideProgressBar(progressBar, progressBarHolder, act);
                        }
                    });
                } else {
                    showToast(ctx.getString(R.string.no_server_found), act);
                }
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

        dialog.show();
        dialog.setCancelable(false);
    }

    public static void callPlaylistDetails(Context ctx, Activity act, String CoUSERID,
                                           String PlaylistId, String PlaylistName,
                                           FragmentManager fragmentManager1) {
        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.open_playlist_detail_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ctx.getResources().getColor(R.color.blue_transparent_extra)));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final TextView tvDesc = dialog.findViewById(R.id.tvDesc);
        final TextView tvReadMore = dialog.findViewById(R.id.tvReadMore);
        final TextView tvSubDec = dialog.findViewById(R.id.tvSubDec);
        final TextView tvTitleDec = dialog.findViewById(R.id.tvTitleDec);
        final TextView tvTime = dialog.findViewById(R.id.tvTime);
        final TextView tvName = dialog.findViewById(R.id.tvName);
        final TextView tvDownloads = dialog.findViewById(R.id.tvDownloads);
        final LinearLayout llDownload = dialog.findViewById(R.id.llDownload);
        final LinearLayout llBack = dialog.findViewById(R.id.llBack);
        final LinearLayout llOptions = dialog.findViewById(R.id.llOptions);
        final LinearLayout llRename = dialog.findViewById(R.id.llRename);
        final LinearLayout llDelete = dialog.findViewById(R.id.llDelete);
        final LinearLayout llFind = dialog.findViewById(R.id.llFind);
        final ImageView ivRestaurantImage = dialog.findViewById(R.id.ivRestaurantImage);
        final ImageView ivDownloads = dialog.findViewById(R.id.ivDownloads);
        final LinearLayout llAddPlaylist = dialog.findViewById(R.id.llAddPlaylist);
        final ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        final FrameLayout progressBarHolder = dialog.findViewById(R.id.progressBarHolder);
        final RecyclerView rvDirlist = dialog.findViewById(R.id.rvDirlist);
        dialog.setOnKeyListener((v1, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog.dismiss();
                localIntent.putExtra("MyReminder", "update");
                localBroadcastManager.sendBroadcast(localIntent);
                return true;
            }
            return false;
        });
        localIntent = new Intent("Reminder");
        localBroadcastManager = LocalBroadcastManager.getInstance(ctx);

        llBack.setOnClickListener(v -> {
            dialog.dismiss();
            localIntent.putExtra("MyReminder", "update");
            localBroadcastManager.sendBroadcast(localIntent);
        });

        CallObserverMethodGetAllMedia(ctx);
        if (isNetworkConnected(ctx)) {
            showProgressBar(progressBar, progressBarHolder, act);
            Call<PlaylistDetailsModel> listCall = APINewClient.getClient().getPlaylistDetail(CoUSERID, PlaylistId);
            listCall.enqueue(new Callback<PlaylistDetailsModel>() {
                @Override
                public void onResponse(Call<PlaylistDetailsModel> call, Response<PlaylistDetailsModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            hideProgressBar(progressBar, progressBarHolder, act);
                            PlaylistDetailsModel model = response.body();

                            GetPlaylistDetail(act, ctx, PlaylistId, llDownload, ivDownloads, model.getResponseData().getPlaylistSongs().size());
                            llDownload.setVisibility(View.VISIBLE);
                            DownloadPlaylistDetails downloadPlaylistDetails = new DownloadPlaylistDetails();
                            downloadPlaylistDetails.setPlaylistID(model.getResponseData().getPlaylistID());
                            downloadPlaylistDetails.setPlaylistName(model.getResponseData().getPlaylistName());
                            downloadPlaylistDetails.setPlaylistDesc(model.getResponseData().getPlaylistDesc());
                            downloadPlaylistDetails.setIsReminder("");
                            downloadPlaylistDetails.setPlaylistMastercat(model.getResponseData().getPlaylistMastercat());
                            downloadPlaylistDetails.setPlaylistSubcat(model.getResponseData().getPlaylistSubcat());
                            downloadPlaylistDetails.setPlaylistImage(model.getResponseData().getPlaylistImage());
                            downloadPlaylistDetails.setPlaylistImageDetails(model.getResponseData().getPlaylistImageDetail());
                            downloadPlaylistDetails.setTotalAudio(model.getResponseData().getTotalAudio());
                            downloadPlaylistDetails.setTotalDuration(model.getResponseData().getTotalDuration());
                            downloadPlaylistDetails.setTotalhour(model.getResponseData().getTotalhour());
                            downloadPlaylistDetails.setTotalminute(model.getResponseData().getTotalminute());
                            downloadPlaylistDetails.setCreated(model.getResponseData().getCreated());
                            tvName.setText(model.getResponseData().getPlaylistName());

                            String PlaylistDesc = model.getResponseData().getPlaylistDesc();
                            String PlaylistName = model.getResponseData().getPlaylistName();
                            String PlaylistID = model.getResponseData().getPlaylistID();
                            String TotalAudio = model.getResponseData().getTotalAudio();
                            String Totalhour = model.getResponseData().getTotalhour();
                            String Totalminute = model.getResponseData().getTotalminute();
                            llAddPlaylist.setOnClickListener(view -> {
                                Intent i = new Intent(ctx, AddPlaylistActivity.class);
                                i.putExtra("AudioId", "");
                                i.putExtra("ScreenView", "Playlist Details Screen");
                                i.putExtra("PlaylistID", PlaylistID);
                                i.putExtra("PlaylistName", PlaylistName);
                                i.putExtra("PlaylistImage", model.getResponseData().getPlaylistImage());
                                i.putExtra("PlaylistType", model.getResponseData().getCreated());
                                i.putExtra("Liked", "0");
                                ctx.startActivity(i);
                                dialog.dismiss();
                            });

                            llFind.setOnClickListener(view -> {
                                localIntent.putExtra("MyFindAudio", "update");
                                localBroadcastManager.sendBroadcast(localIntent);
                                dialog.dismiss();
                            });

                            if (model.getResponseData().getPlaylistMastercat().equalsIgnoreCase("")) {
                                tvDesc.setVisibility(View.GONE);
                            } else {
                                tvDesc.setVisibility(View.VISIBLE);
                                tvDesc.setText(model.getResponseData().getPlaylistMastercat());
                            }

//                                Properties p = new Properties();
//                                p.putValue("userId", UserID);
//                                p.putValue("playlistId", model.getResponseData().getPlaylistID());
//                                p.putValue("playlistName", model.getResponseData().getPlaylistName());
//                                p.putValue("playlistDescription", PlaylistDesc);
//                                if (PlaylistType.equalsIgnoreCase("1")) {
//                                    p.putValue("playlistType", "Created");
//                                } else if (PlaylistType.equalsIgnoreCase("0")) {
//                                    p.putValue("playlistType", "Default");
//                                }
//                                if (model.getResponseData().getTotalhour().equalsIgnoreCase("")) {
//                                    p.putValue("playlistDuration", "0h " + model.getResponseData().getTotalminute() + "m");
//                                } else if (model.getResponseData().getTotalminute().equalsIgnoreCase("")) {
//                                    p.putValue("playlistDuration", model.getResponseData().getTotalhour() + "h 0m");
//                                } else {
//                                    p.putValue("playlistDuration", model.getResponseData().getTotalhour() + "h " + model.getResponseData().getTotalminute() + "m");
//                                }
//
//                                p.putValue("audioCount", model.getResponseData().getTotalAudio());
//                                p.putValue("source", ScreenView);
//                                addToSegment("Playlist Details Viewed", p, CONSTANTS.screen);

                            if (model.getResponseData().getTotalAudio().equalsIgnoreCase("") ||
                                    model.getResponseData().getTotalAudio().equalsIgnoreCase("0") &&
                                            model.getResponseData().getTotalhour().equalsIgnoreCase("")
                                            && model.getResponseData().getTotalminute().equalsIgnoreCase("")) {
                                tvTime.setText("0 Audio | 0h 0m");
                            } else {
                                if (model.getResponseData().getTotalminute().equalsIgnoreCase("")) {
                                    tvTime.setText(model.getResponseData().getTotalAudio() + " Audio | "
                                            + model.getResponseData().getTotalhour() + "h 0m");
                                } else {
                                    tvTime.setText(model.getResponseData().getTotalAudio() + " Audio | "
                                            + model.getResponseData().getTotalhour() + "h " + model.getResponseData().getTotalminute() + "m");
                                }
                            }

                            if (model.getResponseData().getCreated().equalsIgnoreCase("1")) {
                                llOptions.setVisibility(View.GONE);
                                llRename.setVisibility(View.VISIBLE);
                                llDelete.setVisibility(View.VISIBLE);
                                llFind.setVisibility(View.GONE);
                            } else if (model.getResponseData().getCreated().equalsIgnoreCase("0")) {
                                llOptions.setVisibility(View.VISIBLE);
                                llRename.setVisibility(View.GONE);
                                llDelete.setVisibility(View.GONE);
                                llFind.setVisibility(View.VISIBLE);
                            } else if (model.getResponseData().getCreated().equalsIgnoreCase("2")) {
                                llOptions.setVisibility(View.VISIBLE);
                                llRename.setVisibility(View.GONE);
                                llDelete.setVisibility(View.GONE);
                                llFind.setVisibility(View.VISIBLE);
                            }

                            MeasureRatio measureRatio = measureRatio(ctx, 20,
                                    1, 1, 0.54f, 20);
                            ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
                            ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
                            ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
                            if (!model.getResponseData().getPlaylistImage().equalsIgnoreCase("")) {
                                Glide.with(ctx).load(model.getResponseData().getPlaylistImage()).thumbnail(0.05f)
                                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(32))).priority(Priority.HIGH)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(ivRestaurantImage);
                            } else {
                                Glide.with(ctx).load(R.drawable.ic_playlist_bg).thumbnail(0.05f)
                                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(32))).priority(Priority.HIGH)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(ivRestaurantImage);
                            }

//                            getDownloadData();
                            int SongListSize = model.getResponseData().getPlaylistSongs().size();
//                            getMediaByPer(PlaylistID,SongListSize);
//                            SongListSize = model.getResponseData().getPlaylistSongs().size();
                            llAddPlaylist.setVisibility(View.VISIBLE);
//                            getDownloadData();

                            if (model.getResponseData().getPlaylistDesc().equalsIgnoreCase("")) {
                                tvTitleDec.setVisibility(View.GONE);
                                tvSubDec.setVisibility(View.GONE);
                            } else {
                                tvTitleDec.setVisibility(View.VISIBLE);
                                tvSubDec.setVisibility(View.VISIBLE);
                            }

                            tvSubDec.setText(model.getResponseData().getPlaylistDesc());
                            int linecount = tvSubDec.getLineCount();
                            if (linecount >= 4) {
                                tvReadMore.setVisibility(View.VISIBLE);
                            } else {
                                tvReadMore.setVisibility(View.GONE);
                            }

                            tvReadMore.setOnClickListener(v12 -> {
                                final Dialog dialog1 = new Dialog(ctx);
                                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog1.setContentView(R.layout.full_desc_layout);
                                dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                final TextView tvDesc = dialog1.findViewById(R.id.tvDesc);
                                final RelativeLayout tvClose = dialog1.findViewById(R.id.tvClose);
                                tvDesc.setText(model.getResponseData().getPlaylistDesc());

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

//                                if (model.getResponseData().getDownload().equalsIgnoreCase("1")) {
//                                    binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
//                                    binding.ivDownloads.setColorFilter(getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
//                                    binding.tvDownload.setTextColor(getResources().getColor(R.color.light_gray));
//                                    binding.llDownload.setClickable(false);
//                                    binding.llDownload.setEnabled(false);
//                                } else if (!model.getResponseData().getDownload().equalsIgnoreCase("")) {
//                                    binding.llDownload.setClickable(true);
//                                    binding.llDownload.setEnabled(true);
//                                    binding.ivDownloads.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
//                                    binding.tvDownload.setTextColor(getResources().getColor(R.color.white));
//                                    binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
//                                }
//                               binding.llDownload.setOnClickListener(view -> {
//                                if (isNetworkConnected(ctx)) {
//                                    showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
//                                    Call<DownloadPlaylistModel> listCall13 = null;
//                                    listCall13 = APIClient.getClient().getDownloadlistPlaylist(UserID, "", PlaylistID);
//                                    listCall13.enqueue(new Callback<DownloadPlaylistModel>() {
//                                        @Override
//                                        public void onResponse(Call<DownloadPlaylistModel> call13, Response<DownloadPlaylistModel> response13) {
//                                            if (response13.isSuccessful()) {
//                                                hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
//                                                DownloadPlaylistModel model1 = response13.body();
//                                                showToast(model1.getResponseMessage(), ctx);
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onFailure(Call<DownloadPlaylistModel> call13, Throwable t) {
//                                            hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
//                                        }
//                                    });
//
//                                } else {
//                                    Toast.makeText(getApplicationContext(), getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
//                                }
//                            });

                            if (model.getResponseData().getPlaylistSubcat().equalsIgnoreCase("")) {
                                rvDirlist.setVisibility(View.GONE);
                            } else {
                                rvDirlist.setVisibility(View.VISIBLE);
                                String[] elements = model.getResponseData().getPlaylistSubcat().split(",");
                                List<String> direction = Arrays.asList(elements);
                                DirectionAdapter directionAdapter = new DirectionAdapter(direction, ctx);
                                RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
                                rvDirlist.setLayoutManager(recentlyPlayed);
                                rvDirlist.setItemAnimator(new
                                        DefaultItemAnimator());
                                rvDirlist.setAdapter(directionAdapter);
                            }

                            llDelete.setOnClickListener(view43 -> {
                                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                                String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
                                String pID = shared.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "0");
                                if (AudioFlag.equalsIgnoreCase("playlist") && pID.equalsIgnoreCase(PlaylistId)) {
                                    showToast("Currently this playlist is in player,so you can't delete this playlist as of now", act);
                                } else {
                                    final Dialog dialoged = new Dialog(ctx);
                                    dialoged.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialoged.setContentView(R.layout.delete_playlist);
                                    dialoged.getWindow().setBackgroundDrawable(new ColorDrawable(ctx.getResources().getColor(R.color.dark_blue_gray)));
                                    dialoged.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                                    final TextView tvGoBack = dialoged.findViewById(R.id.tvGoBack);
                                    final TextView tvHeader = dialoged.findViewById(R.id.tvHeader);
                                    final RelativeLayout tvconfirm = dialoged.findViewById(R.id.tvconfirm);
                                    tvHeader.setText("Are you sure you want to delete " + PlaylistName + " ?");
                                    dialoged.setOnKeyListener((v44, keyCode, event) -> {
                                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                                            dialoged.dismiss();
//                            Fragment playlistFragment = new PlaylistFragment();
//                            FragmentManager fragmentManager1 = ctx.getSupportFragmentManager();
//                            fragmentManager1.beginTransaction()
//                                    .add(R.id.flContainer, playlistFragment)
//                                    .commit();
//                            Bundle bundle = new Bundle();
//                            playlistFragment.setArguments(bundle);
                                            return true;
                                        }
                                        return false;
                                    });

                                    tvconfirm.setOnClickListener(v -> {
                                        if (isNetworkConnected(ctx)) {
                                            showProgressBar(progressBar, progressBarHolder, act);
                                            Call<SucessModel> listCall12 = APINewClient.getClient().getDeletePlaylist(CoUSERID, PlaylistId);
                                            listCall12.enqueue(new Callback<SucessModel>() {
                                                @Override
                                                public void onResponse(Call<SucessModel> call12, Response<SucessModel> response12) {
                                                    try {
//                                            MyPlaylistIds = "";
//                                            deleteFrg = 1;
                                                        SucessModel listModel = response12.body();
                                                        if (listModel != null) {
                                                            showToast(listModel.getResponseMessage(), act);
                                                        }
                                                        hideProgressBar(progressBar, progressBarHolder, act);
                                                        dialoged.dismiss();
//                                                            Fragment audioFragment = new MainPlaylistFragment();
//                                                            fragmentManager1.beginTransaction()
//                                                                    .replace(R.id.flContainer, audioFragment)
//                                                                    .commit();
                                                        act.finish();
                                                        dialog.dismiss();

                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<SucessModel> call12, Throwable t) {
                                                    hideProgressBar(progressBar, progressBarHolder, act);
                                                }
                                            });
                                        } else {
                                            showToast(ctx.getString(R.string.no_server_found), act);
                                        }
                                    });

                                    tvGoBack.setOnClickListener(v22 -> dialoged.dismiss());
                                    dialoged.show();
                                    dialoged.setCanceledOnTouchOutside(true);
                                    dialoged.setCancelable(true);
                                }
                            });

                            llRename.setOnClickListener(view22 -> {
                                final Dialog dialogs = new Dialog(ctx);
                                dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialogs.setContentView(R.layout.create_palylist);
                                dialogs.getWindow().setBackgroundDrawable(new ColorDrawable(ctx.getResources().getColor(R.color.blue_transparent)));
                                dialogs.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                EditText edtCreate = dialogs.findViewById(R.id.edtCreate);
                                TextView tvCancel = dialogs.findViewById(R.id.tvCancel);
                                TextView tvHeading = dialogs.findViewById(R.id.tvHeading);
                                Button btnSendCode = dialogs.findViewById(R.id.btnSendCode);
                                tvHeading.setText(R.string.Rename_your_playlist);
                                btnSendCode.setText(R.string.Save);
                                edtCreate.requestFocus();
                                edtCreate.setText(model.getResponseData().getPlaylistName());
                                int position1 = edtCreate.getText().length();
                                Editable editObj = edtCreate.getText();
                                Selection.setSelection(editObj, position1);
                                dialog.setOnKeyListener((v23, keyCode, event) -> {
                                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                                        playlistDetailRefresh = 1;
                                        localIntent.putExtra("MyReminder", "update");
                                        localBroadcastManager.sendBroadcast(localIntent);
                                        dialog.dismiss();
                                        return true;
                                    }
                                    return false;
                                });

                                TextWatcher popupTextWatcher = new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        String number = edtCreate.getText().toString();
                                        if (!number.equalsIgnoreCase("")) {
                                            if (number.equalsIgnoreCase(model.getResponseData().getPlaylistName())) {
                                                btnSendCode.setEnabled(false);
                                                btnSendCode.setTextColor(ctx.getResources().getColor(R.color.white));
                                                btnSendCode.setBackgroundResource(R.drawable.gray_round_cornor);
                                            } else {
                                                btnSendCode.setEnabled(true);
                                                btnSendCode.setTextColor(ctx.getResources().getColor(R.color.light_black));
                                                btnSendCode.setBackgroundResource(R.drawable.white_round_cornor);
                                            }
                                        } else {
                                            btnSendCode.setEnabled(false);
                                            btnSendCode.setTextColor(ctx.getResources().getColor(R.color.white));
                                            btnSendCode.setBackgroundResource(R.drawable.gray_round_cornor);
                                        }
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                    }
                                };


                                edtCreate.addTextChangedListener(popupTextWatcher);

                                btnSendCode.setOnClickListener(view1 -> {
                                    if (isNetworkConnected(ctx)) {
                                        showProgressBar(progressBar, progressBarHolder, act);
                                        Call<SucessModel> listCall1 = APINewClient.getClient().getRenameNewPlaylist(CoUSERID, PlaylistID, edtCreate.getText().toString());
                                        listCall1.enqueue(new Callback<SucessModel>() {
                                            @Override
                                            public void onResponse(Call<SucessModel> call1, Response<SucessModel> response1) {
                                                try {
                                                    hideProgressBar(progressBar, progressBarHolder, act);
                                                    SucessModel listModel = response1.body();
                                                    if (listModel.getResponseCode().equalsIgnoreCase(ctx.getString(R.string.ResponseCodesuccess))) {
                                                        showToast(listModel.getResponseMessage(), act);
                                                        tvName.setText(edtCreate.getText().toString());
                                                        dialogs.dismiss();
                                                        localIntent.putExtra("MyReminder", "update");
                                                        localBroadcastManager.sendBroadcast(localIntent);
//                                        Properties p = new Properties();
//                                        p.putValue("userId", UserID);
//                                        p.putValue("playlistId", PlaylistID);
//                                        p.putValue("playlistName", PlaylistName);
//                                        p.putValue("playlistDescription", PlaylistDesc);
//                                        if (PlaylistType.equalsIgnoreCase("1")) {
//                                            p.putValue("playlistType", "Created");
//                                        } else if (PlaylistType.equalsIgnoreCase("0")) {
//                                            p.putValue("playlistType", "Default");
//                                        }
//                                        if (Totalhour.equalsIgnoreCase("")) {
//                                            p.putValue("playlistDuration", "0h " + Totalminute + "m");
//                                        } else if (Totalminute.equalsIgnoreCase("")) {
//                                            p.putValue("playlistDuration", Totalhour + "h 0m");
//                                        } else {
//                                            p.putValue("playlistDuration", Totalhour + "h " + Totalminute + "m");
//                                        }
//                                        p.putValue("audioCount", TotalAudio);
//                                        p.putValue("source", ScreenView);
//                                        addToSegment("Playlist Rename Clicked", p, CONSTANTS.track);
//                                        ctx.finish();
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<SucessModel> call1, Throwable t) {
                                                hideProgressBar(progressBar, progressBarHolder, act);
                                            }
                                        });
                                    } else {
                                        showToast(ctx.getString(R.string.no_server_found), act);
                                    }

                                });
                                tvCancel.setOnClickListener(v34 -> dialogs.dismiss());
                                dialogs.show();
                                dialogs.setCanceledOnTouchOutside(true);
                                dialogs.setCancelable(true);

                            });

                            llDownload.setOnClickListener(view -> callDownloadPlayList(act, model.getResponseData().getPlaylistSongs(), ctx, llDownload, ivDownloads, downloadPlaylistDetails, CoUSERID, PlaylistID));

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<PlaylistDetailsModel> call, Throwable t) {
                    hideProgressBar(progressBar, progressBarHolder, act);
                }
            });
        } else {
            showToast(ctx.getString(R.string.no_server_found), act);
        }

        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

//       TODO Mansi  Hint This code is Create playlist Dialog
       /* final Dialog dialog1 = new Dialog(ctx);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.create_palylist);
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(ctx.getResources().getColor(R.color.blue_transparent)));
        dialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final EditText edtCreate = dialog1.findViewById(R.id.edtCreate);
        final TextView tvCancel = dialog1.findViewById(R.id.tvCancel);
        final Button btnSendCode = dialog1.findViewById(R.id.btnSendCode);
        final ProgressBar progressBar1 = dialog1.findViewById(R.id.progressBar);
        final FrameLayout progressBarHolder1 = dialog1.findViewById(R.id.progressBarHolder);
        edtCreate.requestFocus();
        TextWatcher popupTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String number = edtCreate.getText().toString().trim();
                if (!number.isEmpty()) {
                    btnSendCode.setEnabled(true);
                    btnSendCode.setTextColor(ctx.getResources().getColor(R.color.light_black));
                    btnSendCode.setBackgroundResource(R.drawable.white_round_cornor);
                } else {
                    btnSendCode.setEnabled(false);
                    btnSendCode.setTextColor(ctx.getResources().getColor(R.color.white));
                    btnSendCode.setBackgroundResource(R.drawable.gray_round_cornor);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        edtCreate.addTextChangedListener(popupTextWatcher);
        dialog1.setOnKeyListener((v1, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog1.dismiss();
                return true;
            }
            return false;
        });

        btnSendCode.setOnClickListener(view1 -> {
            showProgressBar(progressBar1, progressBarHolder1, act);
            if (isNetworkConnected(ctx)) {
                Call<CreateNewPlaylistModel> listCall = APINewClient.getClient().getCreatePlaylist(CoUSERID, edtCreate.getText().toString());
                listCall.enqueue(new Callback<CreateNewPlaylistModel>() {
                    @Override
                    public void onResponse(Call<CreateNewPlaylistModel> call, Response<CreateNewPlaylistModel> response) {
                        try {
                            CreateNewPlaylistModel listModel = response.body();
                            if (listModel.getResponseCode().equalsIgnoreCase(ctx.getString(R.string.ResponseCodesuccess))) {
                                hideProgressBar(progressBar1, progressBarHolder1, act);
                                if (listModel.getResponseData().getIscreate().equalsIgnoreCase("0")) {
                                    showToast(listModel.getResponseMessage(), act);
                                } else if (listModel.getResponseData().getIscreate().equalsIgnoreCase("1") ||
                                        listModel.getResponseData().getIscreate().equalsIgnoreCase("")) {
//                                        ComeScreenMyPlaylist = 1;
//                                        callMyPlaylistsFragment("1", listModel.getResponseData().getId(), listModel.getResponseData().getName(), "", "0", "Your Created");
                                    dialog1.dismiss();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<CreateNewPlaylistModel> call, Throwable t) {
                        hideProgressBar(progressBar1, progressBarHolder1, act);
                    }
                });
            } else {
                showToast(ctx.getString(R.string.no_server_found), act);
            }
        });
        tvCancel.setOnClickListener(v13 -> dialog1.dismiss());
        dialog1.show();
        dialog1.setCancelable(false);*/
    }

    private static void callDownloadPlayList(Activity act, List<PlaylistDetailsModel.ResponseData.PlaylistSong> playlistSongsList, Context ctx,
                                             LinearLayout llDownload, ImageView ivDownloads, DownloadPlaylistDetails downloadPlaylistDetails,
                                             String CoUserId, String PlaylistID) {
        List<String> url = new ArrayList<>();
        List<String> name = new ArrayList<>();
        List<String> downloadPlaylistId = new ArrayList<>();
        List<PlaylistDetailsModel.ResponseData.PlaylistSong> playlistSongs2;
        playlistSongs2 = playlistSongsList;
        if (playlistDownloadAudioDetailsList.size() != 0) {
            for (int y = 0; y < playlistDownloadAudioDetailsList.size(); y++) {
                if (playlistSongs2.size() == 0) {
                    break;
                } else {
                    for (int x = 0; x < playlistSongs2.size(); x++) {
                        if (playlistSongs2.size() != 0) {
                            if (playlistSongs2.get(x).getAudioFile().equalsIgnoreCase(playlistDownloadAudioDetailsList.get(y).getAudioFile())) {
                                playlistSongs2.remove(x);
                            }
                            if (playlistSongs2.size() == 0) {
                                break;
                            }
                        } else break;
                    }
                }
            }
        }
        for (int x = 0; x < playlistSongs2.size(); x++) {
            name.add(playlistSongs2.get(x).getName());
            url.add(playlistSongs2.get(x).getAudioFile());
            downloadPlaylistId.add(playlistSongs2.get(x).getPlaylistID());
        }
        ivDownloads.setImageResource(R.drawable.ic_download_done_icon);
        llDownload.setClickable(false);
        llDownload.setEnabled(false);
        ivDownloads.setColorFilter(act.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        SharedPreferences sharedx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
        Gson gson1 = new Gson();
        String json = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson1));
        String json1 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson1));
        String json2 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson1));
        if (!json1.equalsIgnoreCase(String.valueOf(gson1))) {
            Type type = new TypeToken<List<String>>() {
            }.getType();
            List<String> fileNameList = gson1.fromJson(json, type);
            List<String> audioFile = gson1.fromJson(json1, type);
            List<String> playlistId1 = gson1.fromJson(json2, type);
            if (fileNameList.size() != 0) {
                url.addAll(audioFile);
                name.addAll(fileNameList);
                downloadPlaylistId.addAll(playlistId1);
            }
        }

        if (url.size() != 0) {
            if (!isDownloading) {
                isDownloading = true;
                DownloadMedia downloadMedia = new DownloadMedia(ctx.getApplicationContext(), act);
                downloadMedia.encrypt1(url, name, downloadPlaylistId/*, playlistSongs*/);
            }
            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String urlJson = gson.toJson(url);
            String nameJson = gson.toJson(name);
            String playlistIdJson = gson.toJson(downloadPlaylistId);
            editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson);
            editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson);
            editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
            editor.commit();
        }
        savePlaylist(ctx, downloadPlaylistDetails);
        saveAllMedia(playlistSongsList, PlaylistID, CoUserId, ctx, downloadPlaylistDetails);
    }

    private static void savePlaylist(Context ctx, DownloadPlaylistDetails downloadPlaylistDetails) {
        DB = getAudioDataBase(ctx);
        SharedPreferences shared1 =
                ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        String UserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "");
        String CoUserID = shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "");
        downloadPlaylistDetails.setUserId(CoUserID);
        try {
            AudioDatabase.databaseWriteExecutor.execute(() -> DB.taskDao().insertPlaylist(downloadPlaylistDetails));
        } catch (Exception | OutOfMemoryError e) {
            System.out.println(e.getMessage());
        }
    }

    public static AudioDatabase getAudioDataBase(Context ctx){
        DB = Room.databaseBuilder(ctx,
                AudioDatabase.class,
                "Audio_database")
                .addMigrations(MIGRATION_2_3)
               .build();
       return DB;
    }
    private static void saveAllMedia(List<PlaylistDetailsModel.ResponseData.PlaylistSong> playlistSongs,
                                     String PlaylistID, String CoUserId, Context ctx, DownloadPlaylistDetails downloadPlaylistDetails) {
        DB = getAudioDataBase(ctx);
        downloadPlaylistDetails.setUserId(CoUserId);
         Properties p = new Properties();
        p.putValue("userId", CoUserId);
        p.putValue("playlistId", downloadPlaylistDetails.getPlaylistID());
        p.putValue("playlistName", downloadPlaylistDetails.getPlaylistName());
        p.putValue("playlistDescription", downloadPlaylistDetails.getPlaylistDesc());
        if (downloadPlaylistDetails.getCreated().equalsIgnoreCase("1")) {
            p.putValue("playlistType", "Created");
        } else if (downloadPlaylistDetails.getCreated().equalsIgnoreCase("0")) {
            p.putValue("playlistType", "Default");
        }

        if (downloadPlaylistDetails.getTotalhour().equalsIgnoreCase("")) {
            p.putValue("playlistDuration", "0h " + downloadPlaylistDetails.getTotalminute() + "m");
        } else if (downloadPlaylistDetails.getTotalminute().equalsIgnoreCase("")) {
            p.putValue("playlistDuration", downloadPlaylistDetails.getTotalhour() + "h 0m");
        } else {
            p.putValue("playlistDuration", downloadPlaylistDetails.getTotalhour() + "h " + downloadPlaylistDetails.getTotalminute() + "m");
        }
        p.putValue("audioCount", downloadPlaylistDetails.getTotalAudio());
        p.putValue("source", "Downloaded Playlists");
        p.putValue("playerType", "Mini");
        p.putValue("audioService", appStatus(ctx));
        p.putValue("sound", String.valueOf(hundredVolume));
        addToSegment("Playlist Download Started", p, CONSTANTS.track);
        for (int i = 0; i < playlistSongs.size(); i++) {
            DownloadAudioDetails downloadAudioDetails = new DownloadAudioDetails();
            downloadAudioDetails.setUserId(CoUserId);
            downloadAudioDetails.setID(playlistSongs.get(i).getId());
            downloadAudioDetails.setName(playlistSongs.get(i).getName());
            downloadAudioDetails.setAudioFile(playlistSongs.get(i).getAudioFile());
            downloadAudioDetails.setAudioDirection(playlistSongs.get(i).getAudioDirection());
            downloadAudioDetails.setAudiomastercat(playlistSongs.get(i).getAudiomastercat());
            downloadAudioDetails.setAudioSubCategory(playlistSongs.get(i).getAudioSubCategory());
            downloadAudioDetails.setImageFile(playlistSongs.get(i).getImageFile());
            downloadAudioDetails.setPlaylistId(PlaylistID);
            downloadAudioDetails.setAudioDuration(playlistSongs.get(i).getAudioDuration());
            downloadAudioDetails.setIsSingle("0");
            if (playlistDownloadAudioDetailsList.size() != 0) {
                for (int y = 0; y < playlistDownloadAudioDetailsList.size(); y++) {
                    if (playlistSongs.get(i).getAudioFile().equalsIgnoreCase(playlistDownloadAudioDetailsList.get(y).getAudioFile())) {
                        downloadAudioDetails.setIsDownload("Complete");
                        downloadAudioDetails.setDownloadProgress(100);
                        break;
                    } else {
                        downloadAudioDetails.setIsDownload("pending");
                        downloadAudioDetails.setDownloadProgress(0);
                    }

                }
            } else {
                downloadAudioDetails.setIsDownload("pending");
                downloadAudioDetails.setDownloadProgress(0);
            }
            try {
                AudioDatabase.databaseWriteExecutor.execute(() -> DB.taskDao().insertMedia(downloadAudioDetails));
            } catch (Exception | OutOfMemoryError e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void GetMedia(String AudioFile, Context ctx, String audioFileName,
                                ImageView ivDownloads, TextView tvDownloads, LinearLayout llDownload) {
        DB=getAudioDataBase(ctx);
        SharedPreferences shared1 =
                ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        String UserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "");
        String CoUserID = shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "");
        DB.taskDao()
                .getaudioByPlaylist1(AudioFile, "",CoUserID).observe((LifecycleOwner) ctx, audioList -> {
            List<String> fileNameList = new ArrayList<>();
            List<String> audioFile1 = new ArrayList<>();
            List<String> playlistDownloadId = new ArrayList<>();
            SharedPreferences sharedx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
            Gson gson1 = new Gson();
            String json = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson1));
            String json1 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson1));
            String json2 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson1));
            if (!json1.equalsIgnoreCase(String.valueOf(gson1))) {
                Type type = new TypeToken<List<String>>() {
                }.getType();
                fileNameList = gson1.fromJson(json, type);
                audioFile1 = gson1.fromJson(json1, type);
                playlistDownloadId = gson1.fromJson(json2, type);
            }
            if (audioList.size() != 0) {
                ivDownloads.setImageResource(R.drawable.ic_download_done_icon);
                ivDownloads.setColorFilter(ctx.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                llDownload.setClickable(false);
                llDownload.setEnabled(false);
            } else {
                boolean entryNot = false;
                if (fileNameList.size() != 0) {
                    for (int i = 0; i < fileNameList.size(); i++) {
                        if (fileNameList.get(i).equalsIgnoreCase(audioFileName)
                                && playlistDownloadId.get(i).equalsIgnoreCase("")) {
                            entryNot = true;
                            break;
                        }
                    }
                }
                if (!entryNot) {
                    llDownload.setClickable(true);
                    llDownload.setEnabled(true);
                    ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
                    ivDownloads.setVisibility(View.VISIBLE);
                } else {
                    llDownload.setClickable(false);
                    llDownload.setEnabled(false);
                    ivDownloads.setImageResource(R.drawable.ic_download_done_icon);
                    ivDownloads.setColorFilter(ctx.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                }
            }
        });
    }

    public static void getPastIndexScore(HomeScreenModel.ResponseData indexData, BarChart barChart, Activity act) {
        if (indexData.getPastIndexScore().size() == 0) {
            barChart.clear();
            barChart.setVisibility(View.GONE);
        } else {
            barChart.setVisibility(View.VISIBLE);
            barChart.setDescription(null);
            barChart.setPinchZoom(false);
            barChart.setScaleEnabled(false);
            barChart.setDrawBarShadow(false);
            barChart.setDrawGridBackground(false);
            barChart.getAxisLeft().setDrawGridLines(false);
            barChart.getXAxis().setDrawGridLines(false);
            barChart.setBackgroundColor(Color.TRANSPARENT); //set whatever color you prefer

            //        float barWidth = 1f;
            int spaceForBar = 1;
            ArrayList<BarEntry> yAxisValues = new ArrayList<>();

            for (int i = 0; i < indexData.getPastIndexScore().size(); i++) {
                float val = Float.parseFloat(indexData.getPastIndexScore().get(i).getIndexScore());
                yAxisValues.add(new BarEntry(i * spaceForBar, val));
            }

            final ArrayList<String> xAxisValues = new ArrayList<>();
            for (int i = 0; i < indexData.getPastIndexScore().size(); i++) {
                xAxisValues.add(indexData.getPastIndexScore().get(i).getMonthName());
            }

            BarDataSet barDataSet;
            barDataSet = new BarDataSet(yAxisValues, "Past Index Score");
            barDataSet.setDrawIcons(false);
            barDataSet.setColor(act.getResources().getColor(R.color.app_theme_color));

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(barDataSet);

            BarData barData = new BarData(dataSets);
            barData.setBarWidth(4f);
            barData.setValueFormatter(new MyValueFormatter());
            barData.setValueTextSize(7f);

            barChart.setData(barData);
            barChart.notifyDataSetChanged();
            barChart.invalidate();

            XAxis xl = barChart.getXAxis();
            xl.setGranularity(1f);
            xl.setLabelCount(7);
            xl.setLabelRotationAngle(0);
            xl.setPosition(XAxis.XAxisPosition.BOTTOM);
            xl.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
            /*xl.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf(xAxisValues.get((int) value));
                }
            });*/
            YAxis yl = barChart.getAxisLeft();
            yl.setDrawAxisLine(true);
            yl.setDrawGridLines(false);
            yl.setGranularity(1f);
            yl.setGranularityEnabled(true);
            yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)
            barChart.setVisibleXRangeMaximum(xAxisValues.size());
            barChart.setDragEnabled(false);
            barChart.setTouchEnabled(false);
            barChart.getAxisRight().setEnabled(false);
            barChart.setVisibleXRange(0, xAxisValues.size());
            barChart.getBarData().setBarWidth(0.4f);
            barChart.getXAxis().setDrawGridLines(false);
            barChart.setFitBars(true);

            Legend l = barChart.getLegend();
            l.setEnabled(true);
//            l.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
            l.setWordWrapEnabled(true);
        }
    }

    public static class MyValueFormatter extends ValueFormatter implements IValueFormatter {
        private final DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,##0.00");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value);
        }
    }

    private static void callDownload(String comeFrom,
                                     List<DownloadAudioDetails> mDataDownload,
                                     List<ViewAllAudioListModel.ResponseData.Detail> mDataViewAll,
                                     List<PlaylistDetailsModel.ResponseData.PlaylistSong> mDataPlaylist,
                                     List<MainPlayModel> mDataPlayer, int position, Context ctx,
                                     ImageView ivDownloads, Activity act, LinearLayout llDownload) {
        List<String> fileNameList = new ArrayList<>();
        List<String> audioFile1;
        List<String> playlistDownloadId = new ArrayList<>();

        try {
            int i = position;
            String audioFile = "", Name = "";
            if (comeFrom.equalsIgnoreCase("downloadList")) {
                Name = mDataDownload.get(i).getName();
                audioFile = mDataDownload.get(i).getAudioFile();
                if (audioFile.equalsIgnoreCase("")) {
                    i = i + 1;
                    Name = mDataDownload.get(i).getName();
                    audioFile = mDataDownload.get(i).getAudioFile();
                }
            } else if (comeFrom.equalsIgnoreCase("playlist")) {
                Name = mDataPlaylist.get(i).getName();
                audioFile = mDataPlaylist.get(i).getAudioFile();
                if (audioFile.equalsIgnoreCase("")) {
                    i = i + 1;
                    Name = mDataPlaylist.get(i).getName();
                    audioFile = mDataPlaylist.get(i).getAudioFile();
                }
            } else if (comeFrom.equalsIgnoreCase("viewAllAudioList")) {
                Name = mDataViewAll.get(i).getName();
                audioFile = mDataViewAll.get(i).getAudioFile();
                if (audioFile.equalsIgnoreCase("")) {
                    i = i + 1;
                    Name = mDataViewAll.get(i).getName();
                    audioFile = mDataViewAll.get(i).getAudioFile();
                }
            } else if (comeFrom.equalsIgnoreCase("audioPlayer")) {
                Name = mDataPlayer.get(i).getName();
                audioFile = mDataPlayer.get(i).getAudioFile();
                if (audioFile.equalsIgnoreCase("")) {
                    i = i + 1;
                    Name = mDataPlayer.get(i).getName();
                    audioFile = mDataPlayer.get(i).getAudioFile();
                }
            }
            if (downloadAudioDetailsList.contains(Name)) {
                ivDownloads.setImageResource(R.drawable.ic_download_done_icon);
                ivDownloads.setColorFilter(ctx.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                llDownload.setClickable(false);
                llDownload.setEnabled(false);
                SaveMedia(i, 100, comeFrom, mDataDownload, mDataViewAll, mDataPlaylist, mDataPlayer, ctx);
            } else {
                List<String> url1 = new ArrayList<>();
                List<String> name1 = new ArrayList<>();
                List<String> downloadPlaylistId = new ArrayList<>();
                SharedPreferences sharedx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
                Gson gson1 = new Gson();
                String json = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson1));
                String json1 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson1));
                String json2 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson1));
                if (!json1.equalsIgnoreCase(String.valueOf(gson1))) {
                    Type type = new TypeToken<List<String>>() {
                    }.getType();
                    fileNameList = gson1.fromJson(json, type);
                    audioFile1 = gson1.fromJson(json1, type);
                    playlistDownloadId = gson1.fromJson(json2, type);
                    if (fileNameList.size() != 0) {
                        url1.addAll(audioFile1);
                        name1.addAll(fileNameList);
                        downloadPlaylistId.addAll(playlistDownloadId);
                    }
                }
                boolean entryNot = false;
                for (int f = 0; f < fileNameList.size(); f++) {
                    if (fileNameList.get(f).equalsIgnoreCase(Name)
                            && playlistDownloadId.get(f).equalsIgnoreCase("")) {
                        entryNot = true;
                        break;
                    }
                }
                if (!entryNot) {
                    url1.add(audioFile);
                    name1.add(Name);
                    downloadPlaylistId.add("");
                    if (url1.size() != 0) {
                        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = shared.edit();
                        Gson gson = new Gson();
                        String urlJson = gson.toJson(url1);
                        String nameJson = gson.toJson(name1);
                        String playlistIdJson = gson.toJson(downloadPlaylistId);
                        editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson);
                        editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson);
                        editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
                        editor.commit();
                    }
//        fileNast = url1;
                    if (!isDownloading) {
                        isDownloading = true;
                        DownloadMedia downloadMedia = new DownloadMedia(ctx.getApplicationContext(), act);
                        downloadMedia.encrypt1(url1, name1, downloadPlaylistId);
                    }
                    ivDownloads.setImageResource(R.drawable.ic_download_done_icon);
                    ivDownloads.setColorFilter(ctx.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                    llDownload.setClickable(false);
                    llDownload.setEnabled(false);
                    SaveMedia(i, 0, comeFrom, mDataDownload, mDataViewAll, mDataPlaylist, mDataPlayer, ctx);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void SaveMedia(int i, int progress, String comeFrom,
                                  List<DownloadAudioDetails> mDataDownload,
                                  List<ViewAllAudioListModel.ResponseData.Detail> mDataViewAll,
                                  List<PlaylistDetailsModel.ResponseData.PlaylistSong> mDataPlaylist,
                                  List<MainPlayModel> mDataPlayer, Context ctx) {
        DB = getAudioDataBase(ctx);
        DownloadAudioDetails downloadAudioDetails = new DownloadAudioDetails();
        SharedPreferences shared1 =
                ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        String UserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "");
        String CoUserID = shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "");
        downloadAudioDetails.setUserId(CoUserID);
        if (comeFrom.equalsIgnoreCase("downloadList")) {
            downloadAudioDetails.setID(mDataDownload.get(i).getID());
            downloadAudioDetails.setName(mDataDownload.get(i).getName());
            downloadAudioDetails.setAudioFile(mDataDownload.get(i).getAudioFile());
            downloadAudioDetails.setPlaylistId("");
            downloadAudioDetails.setAudioDirection(mDataDownload.get(i).getAudioDirection());
            downloadAudioDetails.setAudiomastercat(mDataDownload.get(i).getAudiomastercat());
            downloadAudioDetails.setAudioSubCategory(mDataDownload.get(i).getAudioSubCategory());
            downloadAudioDetails.setImageFile(mDataDownload.get(i).getImageFile());
            downloadAudioDetails.setAudioDuration(mDataDownload.get(i).getAudioDuration());
        } else if (comeFrom.equalsIgnoreCase("playlist")) {
            downloadAudioDetails.setID(mDataPlaylist.get(i).getId());
            downloadAudioDetails.setName(mDataPlaylist.get(i).getName());
            downloadAudioDetails.setAudioFile(mDataPlaylist.get(i).getAudioFile());
            downloadAudioDetails.setPlaylistId("");
            downloadAudioDetails.setAudioDirection(mDataPlaylist.get(i).getAudioDirection());
            downloadAudioDetails.setAudiomastercat(mDataPlaylist.get(i).getAudiomastercat());
            downloadAudioDetails.setAudioSubCategory(mDataPlaylist.get(i).getAudioSubCategory());
            downloadAudioDetails.setImageFile(mDataPlaylist.get(i).getImageFile());
            downloadAudioDetails.setAudioDuration(mDataPlaylist.get(i).getAudioDuration());
        } else if (comeFrom.equalsIgnoreCase("viewAllAudioList")) {
            downloadAudioDetails.setID(mDataViewAll.get(i).getID());
            downloadAudioDetails.setName(mDataViewAll.get(i).getName());
            downloadAudioDetails.setAudioFile(mDataViewAll.get(i).getAudioFile());
            downloadAudioDetails.setPlaylistId("");
            downloadAudioDetails.setAudioDirection(mDataViewAll.get(i).getAudioDirection());
            downloadAudioDetails.setAudiomastercat(mDataViewAll.get(i).getAudiomastercat());
            downloadAudioDetails.setAudioSubCategory(mDataViewAll.get(i).getAudioSubCategory());
            downloadAudioDetails.setImageFile(mDataViewAll.get(i).getImageFile());
            downloadAudioDetails.setAudioDuration(mDataViewAll.get(i).getAudioDuration());
        } else if (comeFrom.equalsIgnoreCase("audioPlayer")) {
            downloadAudioDetails.setID(mDataPlayer.get(i).getID());
            downloadAudioDetails.setName(mDataPlayer.get(i).getName());
            downloadAudioDetails.setAudioFile(mDataPlayer.get(i).getAudioFile());
            downloadAudioDetails.setPlaylistId(mDataPlayer.get(i).getPlaylistID());
            downloadAudioDetails.setAudioDirection(mDataPlayer.get(i).getAudioDirection());
            downloadAudioDetails.setAudiomastercat(mDataPlayer.get(i).getAudiomastercat());
            downloadAudioDetails.setAudioSubCategory(mDataPlayer.get(i).getAudioSubCategory());
            downloadAudioDetails.setImageFile(mDataPlayer.get(i).getImageFile());
            downloadAudioDetails.setAudioDuration(mDataPlayer.get(i).getAudioDuration());
        }
        downloadAudioDetails.setIsSingle("1");
        downloadAudioDetails.setPlaylistId("");
        if (progress == 0) {
            downloadAudioDetails.setIsDownload("pending");
        } else {
            downloadAudioDetails.setIsDownload("Complete");
        }
        downloadAudioDetails.setDownloadProgress(progress);
        Log.e("Download Media Audio", "1");
        try {
            AudioDatabase.databaseWriteExecutor.execute(() -> DB.taskDao().insertMedia(downloadAudioDetails));
        } catch (Exception | OutOfMemoryError e) {
            System.out.println(e.getMessage());
        }
        Log.e("Download Media Audio", "3");
        SharedPreferences sharedx1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
        String AudioPlayerFlag = sharedx1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
        int PlayerPosition = sharedx1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
        Gson gsonx = new Gson();
        String json11 = sharedx1.getString(CONSTANTS.PREF_KEY_PlayerAudioList, String.valueOf(gsonx));
        String jsonw = sharedx1.getString(CONSTANTS.PREF_KEY_MainAudioList, String.valueOf(gsonx));
        ArrayList<DownloadAudioDetails> arrayList = new ArrayList<>();
        ArrayList<MainPlayModel> arrayList2 = new ArrayList<>();
        int size = 0;
        if (!jsonw.equalsIgnoreCase(String.valueOf(gsonx))) {
            Type type1 = new TypeToken<ArrayList<DownloadAudioDetails>>() {
            }.getType();
            Type type0 = new TypeToken<ArrayList<MainPlayModel>>() {
            }.getType();
            Gson gson1 = new Gson();
            arrayList = gson1.fromJson(jsonw, type1);
            arrayList2 = gson1.fromJson(json11, type0);
            size = arrayList2.size();
        }
        if (AudioPlayerFlag.equalsIgnoreCase("DownloadListAudio")) {
            arrayList.add(downloadAudioDetails);
            MainPlayModel mainPlayModel1 = new MainPlayModel();
            mainPlayModel1.setID(downloadAudioDetails.getID());
            mainPlayModel1.setName(downloadAudioDetails.getName());
            mainPlayModel1.setAudioFile(downloadAudioDetails.getAudioFile());
            mainPlayModel1.setAudioDirection(downloadAudioDetails.getAudioDirection());
            mainPlayModel1.setAudiomastercat(downloadAudioDetails.getAudiomastercat());
            mainPlayModel1.setAudioSubCategory(downloadAudioDetails.getAudioSubCategory());
            mainPlayModel1.setImageFile(downloadAudioDetails.getImageFile());
            mainPlayModel1.setAudioDuration(downloadAudioDetails.getAudioDuration());
            arrayList2.add(mainPlayModel1);
            SharedPreferences sharedd = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedd.edit();
            Gson gson = new Gson();
            String jsonx = gson.toJson(arrayList2);
            String json1q1 = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json1q1);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonx);
            editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, PlayerPosition);
            editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
            editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "");
            editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "");
            editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "DownloadListAudio");
            editor.commit();
            if (!arrayList2.get(PlayerPosition).getAudioFile().equals("")) {
                List<String> downloadAudioDetailsList = new ArrayList<>();
                GlobalInitExoPlayer ge = new GlobalInitExoPlayer();
                downloadAudioDetailsList.add(downloadAudioDetails.getName());
                ge.AddAudioToPlayer(size, arrayList2, downloadAudioDetailsList, ctx);
            }
//                callAddTransFrag();
        }
    }

    public static void getReminderDay(Context ctx, Activity act, String CoUSERID, String playlistID, String playlistName,
                                      FragmentActivity fragmentActivity, String Time, String RDay) {

        ReminderSelectionModel[] reminderSelectionModel = new ReminderSelectionModel[]{
                new ReminderSelectionModel("Sunday"),
                new ReminderSelectionModel("Monday"),
                new ReminderSelectionModel("Tuesday"),
                new ReminderSelectionModel("Wednesday"),
                new ReminderSelectionModel("Thursday"),
                new ReminderSelectionModel("Friday"),
                new ReminderSelectionModel("Saturday"),};
        localIntent = new Intent("Reminder");
        localBroadcastManager = LocalBroadcastManager.getInstance(ctx);
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
        tvTime = dialog.findViewById(R.id.tvTime);
        final Button btnNext = dialog.findViewById(R.id.btnNext);
        final CheckBox cbChecked = dialog.findViewById(R.id.cbChecked);
        final LinearLayout llSelectTime = dialog.findViewById(R.id.llSelectTime);
        final ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        final FrameLayout progressBarHolder = dialog.findViewById(R.id.progressBarHolder);

//        calendar = Calendar.getInstance();
//        Chour = calendar.get(Calendar.HOUR_OF_DAY);
//        Cminute = calendar.get(Calendar.MINUTE);

        if (Time.equalsIgnoreCase("") || Time.equalsIgnoreCase("0")) {
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("hh:mm a");
            simpleDateFormat1.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            DateFormat df = DateFormat.getTimeInstance();
            String gmtTime = df.format(new Date());
            Date currdate = new Date();
            try {
                currdate = simpleDateFormat1.parse(gmtTime);
//                Log.e("currant currdate !!!!", String.valueOf(currdate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            currantTime = simpleDateFormat1.format(currdate);
            tvTime.setText(currantTime);
        } else {
            tvTime.setText(Time);
            currantTime = Time;
        }
        String[] time = currantTime.split(":");
        String min[] = time[1].split(" ");
        Chour = Integer.parseInt(time[0]);
//            mHour = c.get(Calendar.HOUR_OF_DAY);
        Cminute = Integer.parseInt(min[0]);
        String displayAmPm = min[1];
        if (displayAmPm.equalsIgnoreCase("p.m") || displayAmPm.equalsIgnoreCase("PM")) {
            if (Chour != 12)
                Chour = Chour + 12;
        }

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
        llSelectTime.setOnClickListener(v -> {
            TimePickerThemeclass dialogfragment = new TimePickerThemeclass();
            dialogfragment.show(fragmentActivity.getSupportFragmentManager(), "Time Picker with Theme 4");
        });

        llBack.setOnClickListener(v -> dialog.dismiss());
        RecyclerView.LayoutManager manager = new LinearLayoutManager(ctx);
        rvSelectDay.setLayoutManager(manager);
        rvSelectDay.setItemAnimator(new DefaultItemAnimator());
        ReminderSelectionListAdapter adapter = new ReminderSelectionListAdapter(reminderSelectionModel, act, ctx, tvSelectAll, tvUnSelectAll,
                btnNext, CoUSERID, playlistID, playlistName, dialog, fragmentActivity, cbChecked, tvTime, progressBarHolder
                , progressBar, llSelectTime, RDay, Time);
        rvSelectDay.setAdapter(adapter);

        Log.e("remiderDays", TextUtils.join(",", remiderDays));
        dialog.show();
        dialog.setCancelable(false);
    }

/*
    public static void getReminderTime(Context ctx, Activity act, String coUSERID, String playlistID, String playlistName, Dialog dialogOld) {
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
        final RecyclerView rvSelectMinutesTimeSlot = dialog.findViewById(R.id.rvSelectMinutesTimeSlot);
        final RecyclerView rvSelectHoursTimeSlot = dialog.findViewById(R.id.rvSelectHoursTimeSlot);
        final ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        final FrameLayout progressBarHolder = dialog.findViewById(R.id.progressBarHolder);
        final TimePicker timePicker = dialog.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        tvPlaylistName.setText(playlistName);
        llBack.setOnClickListener(view12 -> {
            dialogOld.dismiss();
            localIntent.putExtra("MyReminder", "update");
            localBroadcastManager.sendBroadcast(localIntent);
            dialog.dismiss();
        });

        ReminderMinutesListModel[] minutesListModels = new ReminderMinutesListModel[]{
                new ReminderMinutesListModel(""),
                new ReminderMinutesListModel(""),
                new ReminderMinutesListModel(""),
                new ReminderMinutesListModel(""),
                new ReminderMinutesListModel("00"),
                new ReminderMinutesListModel("05"),
                new ReminderMinutesListModel("10"),
                new ReminderMinutesListModel("15"),
                new ReminderMinutesListModel("20"),
                new ReminderMinutesListModel("25"),
                new ReminderMinutesListModel("30"),
                new ReminderMinutesListModel("35"),
                new ReminderMinutesListModel("40"),
                new ReminderMinutesListModel("45"),
                new ReminderMinutesListModel("50"),
                new ReminderMinutesListModel("55"),
                new ReminderMinutesListModel(""),
                new ReminderMinutesListModel(""),
                new ReminderMinutesListModel(""),
                new ReminderMinutesListModel(""),
        };

        ReminderMinutesListModel[] hoursListModels = new ReminderMinutesListModel[]{
                new ReminderMinutesListModel(""), new ReminderMinutesListModel(""), new ReminderMinutesListModel(""),
                new ReminderMinutesListModel(""), new ReminderMinutesListModel("00"),
                new ReminderMinutesListModel("01"), new ReminderMinutesListModel("02"),
                new ReminderMinutesListModel("03"), new ReminderMinutesListModel("04"), new ReminderMinutesListModel("05"),
                new ReminderMinutesListModel("06"), new ReminderMinutesListModel("07"), new ReminderMinutesListModel("08"),
                new ReminderMinutesListModel("09"), new ReminderMinutesListModel("10"), new ReminderMinutesListModel("11"),
                new ReminderMinutesListModel("12"), new ReminderMinutesListModel("13"), new ReminderMinutesListModel("14"),
                new ReminderMinutesListModel("15"), new ReminderMinutesListModel("16"), new ReminderMinutesListModel("17"),
                new ReminderMinutesListModel("18"), new ReminderMinutesListModel("19"), new ReminderMinutesListModel("20"),
                new ReminderMinutesListModel("21"), new ReminderMinutesListModel("22"), new ReminderMinutesListModel("23"),
                new ReminderMinutesListModel(""), new ReminderMinutesListModel(""), new ReminderMinutesListModel(""),
                new ReminderMinutesListModel(""),
        };

        dialog.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialogOld.dismiss();
                localIntent.putExtra("MyReminder", "update");
                localBroadcastManager.sendBroadcast(localIntent);
                dialog.dismiss();
                return true;
            }
            return false;
        });

        RecyclerView.LayoutManager manager = new LinearLayoutManager(ctx);
        rvSelectMinutesTimeSlot.setLayoutManager(manager);
        rvSelectMinutesTimeSlot.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager manager1 = new LinearLayoutManager(ctx);
        rvSelectHoursTimeSlot.setLayoutManager(manager1);
        rvSelectHoursTimeSlot.setItemAnimator(new DefaultItemAnimator());

        ReminderMinutesListAdapter adapter = new ReminderMinutesListAdapter(minutesListModels, act, ctx, coUSERID, playlistID, playlistName, dialog);
        rvSelectMinutesTimeSlot.setAdapter(adapter);
        rvSelectMinutesTimeSlot.scrollToPosition(10);

        ReminderHoursListAdapter adapter1 = new ReminderHoursListAdapter(hoursListModels, act, ctx, coUSERID, playlistID, playlistName, dialog);
        rvSelectHoursTimeSlot.setAdapter(adapter1);
        rvSelectHoursTimeSlot.scrollToPosition(10);

        btnSave.setOnClickListener(v -> {
            dialog.dismiss();
        });

        tvGoBack.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
        dialog.setCancelable(false);
    }
*/

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
        if(analytics==null){
            SplashActivity sp = new SplashActivity();
            sp.setAnalytics(getContext().getString(R.string.segment_key_real));
        }
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

    public static void showToast(String message, Activity context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            Toast toast = new Toast(context);
            View view = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
            TextView tvMessage = view.findViewById(R.id.tvMessage);
            tvMessage.setText(message);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 35);
            toast.setView(view);
            toast.show();
        } else {
            /*final Dialog dialog = new Dialog(getContext());
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

            dialog.setCancelable(true);*/
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
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

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        BWSApplication = this;
    }

    private static class ReminderSelectionListAdapter extends RecyclerView.Adapter<ReminderSelectionListAdapter.MyViewHolder> {
        Activity act;
        Context ctx;
        TextView tvSelectAll, tvUnSelectAll;
        Button btnNext;
        String CoUSERID, PlaylistID, PlaylistName, RDay, Time;
        Dialog dialogOld;
        CheckBox cbCheck;
        FragmentActivity fragmentActivity;
        TextView timeDisplay;
        ProgressBar progressBar;
        FrameLayout progressBarHolder;
        LinearLayout llSelectTime;
        private final ReminderSelectionModel[] selectionModels;

        public ReminderSelectionListAdapter(ReminderSelectionModel[] selectionModels, Activity act, Context ctx,
                                            TextView tvSelectAll, TextView tvUnSelectAll, Button btnNext, String CoUSERID,
                                            String PlaylistID, String PlaylistName, Dialog dialogOld, FragmentActivity fragmentActivity,
                                            CheckBox cbCheck, TextView timeDisplay, FrameLayout progressBarHolder,
                                            ProgressBar progressBar, LinearLayout llSelectTime, String RDay, String Time) {
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
            this.fragmentActivity = fragmentActivity;
            this.cbCheck = cbCheck;
            this.timeDisplay = timeDisplay;
            this.progressBarHolder = progressBarHolder;
            this.progressBar = progressBar;
            this.llSelectTime = llSelectTime;
            this.RDay = RDay;
            this.Time = Time;
        }

        @NotNull
        @Override
        public MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
            ReminderSelectionlistLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.reminder_selectionlist_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.binding.cbChecked.setText(selectionModels[position].getDay());

            if(position==0) {
                Log.e("Reminder RDay", RDay);

                if (remiderDays.size() == selectionModels.length) {
                    cbCheck.setChecked(true);
                }
                if (remiderDays.size() == 0) {
                    tvSelectAll.setText("Select All");
                } else {
                    tvSelectAll.setText("Unselect All");
                }
            }
            if (RDay.contains(String.valueOf(position))) {
                remiderDays.add(String.valueOf(position));
                holder.binding.cbChecked.setChecked(true);
            }else{
                holder.binding.cbChecked.setChecked(false);
            }

            holder.binding.cbChecked.setOnClickListener(v -> {
                if (holder.binding.cbChecked.isChecked()) {
                    remiderDays.add(String.valueOf(position));
                    RDay = "";
                    RDay = TextUtils.join(",", remiderDays);
                } else {
                    remiderDays.remove(String.valueOf(position));
                    RDay = "";
                    RDay = TextUtils.join(",", remiderDays);
                }
                if (remiderDays.size() == 0) {
                    Log.e("remiderDays", "no data");
                } else {
                    Log.e("remiderDays R", RDay);
                }
            });

            cbCheck.setOnClickListener(view -> {
                remiderDays.clear();
                RDay = "";
                if (cbCheck.isChecked()) {
                    RDay = "0, 1, 2, 3, 4, 5, 6";
                }
                Log.e("remiderDays R cb ch", RDay);
                notifyDataSetChanged();
            });

            btnNext.setOnClickListener(v -> {
                if (remiderDays.size() == 0) {
                    showToast("Please select days", act);
                } else {
                    Log.e("remiderDays Done", TextUtils.join(",", remiderDays));
                    if (isNetworkConnected(ctx)) {
                        showProgressBar(progressBar, progressBarHolder, act);
                        Call<SetReminderOldModel> listCall = APINewClient.getClient().getSetReminder(CoUSERID, PlaylistID,
                                TextUtils.join(",", remiderDays), tvTime.getText().toString(), CONSTANTS.FLAG_ONE);
                        listCall.enqueue(new Callback<SetReminderOldModel>() {
                            @Override
                            public void onResponse(@NotNull Call<SetReminderOldModel> call, @NotNull Response<SetReminderOldModel> response) {
                                try {
                                    SetReminderOldModel listModel = response.body();
                                    if (listModel.getResponseCode().equalsIgnoreCase(ctx.getString(R.string.ResponseCodesuccess))) {
                                        dialogOld.dismiss();
                                        remiderDays.clear();
                                        Time = tvTime.getText().toString();
                                        hideProgressBar(progressBar, progressBarHolder, act);
                                        showToast(listModel.getResponseMessage(), act);
                                        localIntent.putExtra("MyReminder", "update");
                                        localBroadcastManager.sendBroadcast(localIntent);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<SetReminderOldModel> call, @NotNull Throwable t) {
                                hideProgressBar(progressBar, progressBarHolder, act);
                            }
                        });
                    } else {
                        showToast(ctx.getString(R.string.no_server_found), act);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return selectionModels.length;
        }

        public static class MyViewHolder extends RecyclerView.ViewHolder {
            ReminderSelectionlistLayoutBinding binding;

            public MyViewHolder(ReminderSelectionlistLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    public static class TimePickerThemeclass extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @NotNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            TimePickerDialog timepickerdialog1 = new TimePickerDialog(getActivity(),
                    AlertDialog.THEME_HOLO_LIGHT, this, Chour, Cminute, false);
            timepickerdialog1.setTitle("Select Time");
            return timepickerdialog1;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
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
            tvTime.setText(hourString + ":" + minuteSting + " " + am_pm);
        }
    }

    private static class ReminderHoursListAdapter extends RecyclerView.Adapter<ReminderHoursListAdapter.MyViewHolder> {
        Activity act;
        Context ctx;
        String CoUSERID, PlaylistID, PlaylistName;
        Dialog dialogOld;
        private final ReminderMinutesListModel[] minutesListModels;

        public ReminderHoursListAdapter(ReminderMinutesListModel[] minutesListModels, Activity act, Context ctx
                , String CoUSERID, String PlaylistID, String PlaylistName, Dialog dialogOld) {
            this.minutesListModels = minutesListModels;
            this.act = act;
            this.ctx = ctx;
            this.CoUSERID = CoUSERID;
            this.PlaylistID = PlaylistID;
            this.PlaylistName = PlaylistName;
            this.dialogOld = dialogOld;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ReminderTimelistLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.reminder_timelist_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            holder.binding.tvDay.setText(minutesListModels[position].getMinutes());
        }

        @Override
        public int getItemCount() {
            return minutesListModels.length;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public static class MyViewHolder extends RecyclerView.ViewHolder {
            ReminderTimelistLayoutBinding binding;

            public MyViewHolder(ReminderTimelistLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;

/*
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.tvDay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus){
                                mselectedItem = getAdapterPosition();
                                notifyDataSetChanged();
                            }
                        }
                    });
                }
*/
            }
        }
    }

    public static void callObserve2(Context ctx) {
        SharedPreferences shared1 =
                ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        String UserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "");
        String CoUserID = shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "");
        DB.taskDao()
                .geAllData12(CoUserID).observe((LifecycleOwner) ctx , audioList -> {
            List<String> fileNameList = new ArrayList<>();
            List<String> audioFile = new ArrayList<>();
            List<String> playlistDownloadId = new ArrayList<>();

            if (audioList.size() != 0) {
                for (int i = 0; i < audioList.size(); i++) {
                    if (audioList.get(i).getDownloadProgress() < 100) {
                        fileNameList.add(audioList.get(i).getName());
                        audioFile.add(audioList.get(i).getAudioFile());
                        playlistDownloadId.add(audioList.get(i).getPlaylistId());
                    }
                }
            }
            Gson gson = new Gson();
            SharedPreferences sharedxc = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
            SharedPreferences.Editor editorxc = sharedxc.edit();
            String nameJson = gson.toJson(fileNameList);
            String urlJson = gson.toJson(audioFile);
            String playlistIdJson = gson.toJson(playlistDownloadId);
            editorxc.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson);
            editorxc.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson);
            editorxc.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
            editorxc.commit();
            isDownloading = false;
        });
    }

 /*   public static void callObserve1(Context ctx) {
        SharedPreferences shared1 =
                ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        String UserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "");
        String CoUserID = shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "");
        DatabaseClient
                .getInstance(ctx)
                .getaudioDatabase()
                .taskDao()
                .geAllData12(CoUserID).observe((LifecycleOwner) ctx, audioList -> {
            if (audioList.size() != 0) {
                for (int i = 0; i < audioList.size(); i++) {
                    FileUtils.deleteDownloadedFile(ctx, audioList.get(i).getName());
                }
            }
            SharedPreferences preferences11 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_Logout_DownloadPlaylist, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit1 = preferences11.edit();
            edit1.remove(CONSTANTS.PREF_KEY_Logout_DownloadName);
            edit1.remove(CONSTANTS.PREF_KEY_Logout_DownloadUrl);
            edit1.remove(CONSTANTS.PREF_KEY_Logout_DownloadPlaylistId);
            edit1.clear();
            edit1.commit();
            AudioDatabase DB = Room.databaseBuilder(ctx,
                    AudioDatabase.class,
                    "Audio_database")
                    .addMigrations(MIGRATION_1_2)
                    .build();
            AudioDatabase.databaseWriteExecutor.execute(() -> {
                    DB.taskDao().deleteAll();
                });
                AudioDatabase.databaseWriteExecutor.execute(() -> {
                    DB.taskDao().deleteAllPlalist();
                });
        });
      }*/
  /*  public static String getRefreshToken(String code)
    {

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("https://accounts.google.com/o/oauth2/token");
        try
        {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
            nameValuePairs.add(new BasicNameValuePair("grant_type",    "authorization_code"));
            nameValuePairs.add(new BasicNameValuePair("client_id",     GOOGLE_CLIENT_ID));
            nameValuePairs.add(new BasicNameValuePair("client_secret", GOOGLE_CLIENT_SECRET));
            nameValuePairs.add(new BasicNameValuePair("code", code));
            nameValuePairs.add(new BasicNameValuePair("redirect_uri", GOOGLE_REDIRECT_URI));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            org.apache.http.HttpResponse response = client.execute(post);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer buffer = new StringBuffer();
            for (String line = reader.readLine(); line != null; line = reader.readLine())
            {
                buffer.append(line);
            }

            JSONObject json = new JSONObject(buffer.toString());
            String refreshToken = json.getString("refresh_token");
            return refreshToken;
        }
        catch (Exception e) { e.printStackTrace(); }

        return null;
    }
    private static String getAccessToken(String refreshToken) {

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("https://accounts.google.com/o/oauth2/token");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
            nameValuePairs.add(new BasicNameValuePair("grant_type", "refresh_token"));
            nameValuePairs.add(new BasicNameValuePair("client_id", GOOGLE_CLIENT_ID));
            nameValuePairs.add(new BasicNameValuePair("client_secret", GOOGLE_CLIENT_SECRET));
            nameValuePairs.add(new BasicNameValuePair("refresh_token", refreshToken));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            org.apache.http.HttpResponse response = client.execute(post);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer buffer = new StringBuffer();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                buffer.append(line);
            }

            JSONObject json = new JSONObject(buffer.toString());
            String accessToken = json.getString("access_token");

            return accessToken;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    private static String getAccessToken(String refreshToken){

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("https://accounts.google.com/o/oauth2/token");
        try
        {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
            nameValuePairs.add(new BasicNameValuePair("grant_type",    "refresh_token"));
            nameValuePairs.add(new BasicNameValuePair("client_id",     GOOGLE_CLIENT_ID));
            nameValuePairs.add(new BasicNameValuePair("client_secret", GOOGLE_CLIENT_SECRET));
            nameValuePairs.add(new BasicNameValuePair("refresh_token", refreshToken));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            org.apache.http.HttpResponse response = client.execute(post);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer buffer = new StringBuffer();
            for (String line = reader.readLine(); line != null; line = reader.readLine())
            {
                buffer.append(line);
            }

            JSONObject json = new JSONObject(buffer.toString());
            String accessToken = json.getString("access_token");

            return accessToken;

        }
        catch (IOException | JSONException e) { e.printStackTrace(); }

        return null;
    }*/
/*
    private static class ReminderMinutesListAdapter extends RecyclerView.Adapter<ReminderMinutesListAdapter.MyViewHolder> {
        Activity act;
        Context ctx;
        String CoUSERID, PlaylistID, PlaylistName;
        Dialog dialogOld;
        private final ReminderMinutesListModel[] minutesListModels;

        public ReminderMinutesListAdapter(ReminderMinutesListModel[] minutesListModels, Activity act, Context ctx
                , String CoUSERID, String PlaylistID, String PlaylistName, Dialog dialogOld) {
            this.minutesListModels = minutesListModels;
            this.act = act;
            this.ctx = ctx;
            this.CoUSERID = CoUSERID;
            this.PlaylistID = PlaylistID;
            this.PlaylistName = PlaylistName;
            this.dialogOld = dialogOld;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ReminderTimelistLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.reminder_timelist_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvDay.setText(minutesListModels[position].getMinutes());
        }

        @Override
        public int getItemCount() {
            return minutesListModels.length;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public static class MyViewHolder extends RecyclerView.ViewHolder {
            ReminderTimelistLayoutBinding binding;

            public MyViewHolder(ReminderTimelistLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;

*/
/*
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.tvDay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus){
                                mselectedItem = getAdapterPosition();
                                notifyDataSetChanged();
                            }
                        }
                    });
                }
*//*

            }
        }
    }
*/
}