package com.brainwellnessspa;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Application;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.media.session.MediaSessionCompat;
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

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
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

import com.brainwellnessspa.dashboardModule.adapters.DirectionAdapter;
import com.brainwellnessspa.dashboardModule.enhance.AddPlaylistActivity;
import com.brainwellnessspa.dashboardModule.models.AudioDetailModel;
import com.brainwellnessspa.dashboardModule.models.HomeScreenModel;
import com.brainwellnessspa.dashboardModule.models.MainPlayModel;
import com.brainwellnessspa.dashboardModule.models.PlaylistDetailsModel;
import com.brainwellnessspa.dashboardModule.models.ReminderProceedModel;
import com.brainwellnessspa.dashboardModule.models.RenameNewPlaylistModel;
import com.brainwellnessspa.dashboardModule.models.SucessModel;
import com.brainwellnessspa.dashboardModule.models.ViewAllAudioListModel;
import com.brainwellnessspa.databinding.ReminderSelectionlistLayoutBinding;
import com.brainwellnessspa.encryptDecryptUtils.DownloadMedia;
import com.brainwellnessspa.encryptDecryptUtils.FileUtils;
import com.brainwellnessspa.reminderModule.models.ReminderSelectionModel;
import com.brainwellnessspa.reminderModule.models.SetReminderOldModel;
import com.brainwellnessspa.roomDataBase.AudioDatabase;
import com.brainwellnessspa.roomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.roomDataBase.DownloadPlaylistDetails;
import com.brainwellnessspa.services.GlobalInitExoPlayer;
import com.brainwellnessspa.services.PlayerJobService;
import com.brainwellnessspa.userModule.signupLogin.SignInActivity;
import com.brainwellnessspa.userModule.splashscreen.SplashActivity;
import com.brainwellnessspa.utility.APINewClient;
import com.brainwellnessspa.utility.AppSignatureHashHelper;
import com.brainwellnessspa.utility.AppUtils;
import com.brainwellnessspa.utility.CONSTANTS;
import com.brainwellnessspa.utility.CryptLib;
import com.brainwellnessspa.utility.MeasureRatio;
import com.brainwellnessspa.utility.MyMarkerView;
import com.brainwellnessspa.utility.MyValueFormatter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
//import com.clevertap.android.sdk.CleverTapAPI;
import com.downloader.PRDownloader;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.segment.analytics.Traits;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.encryptDecryptUtils.DownloadMedia.downloadIdOne;
import static com.brainwellnessspa.encryptDecryptUtils.DownloadMedia.filename;
import static com.brainwellnessspa.encryptDecryptUtils.DownloadMedia.isDownloading;
import static com.brainwellnessspa.services.GlobalInitExoPlayer.GetCurrentAudioPosition;
import static com.brainwellnessspa.services.GlobalInitExoPlayer.GetSourceName;
import static com.brainwellnessspa.utility.AppUtils.*;

/* TODO BWS App Common function */
public class BWSApplication extends Application {
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'playlist_table' ADD COLUMN 'PlaylistImageDetails' TEXT");
        }
    };
    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'playlist_table' ADD COLUMN 'UserID' TEXT");
            database.execSQL("ALTER TABLE 'audio_table' ADD COLUMN 'UserID' TEXT");
        }
    };
    public static int miniPlayer = 0;
    public static int addCouserBackStatus = 0;
    public static String BatteryStatus = "", IsLock = "0";
    public static Bitmap myBitmap = null;
    public static PlayerNotificationManager playerNotificationManager;
    public static long oldSongPos = 0;
    public static String addToRecentPlayId = "0", comeHomeScreen = "";
    public static AudioManager audioManager;
    public static int hundredVolume = 0, currentVolume = 0, maxVolume = 0, percent;
    public static String PlayerCurrantAudioPostion = "0";
    public static String PlayerAudioId = "", isUserDetail = "", isEnhanceBack = "", NotificationPlaylistCheck = "", PlayerStatus = "", cancelId = "", deleteId = "", IsFirstClick = "0", IsRefreshPlan = "0";
    public static SimpleExoPlayer player;
    public static int notificationId = 1234;
    public static NotificationManager notificationManager;
    public static boolean serviceConected = false, PlayerINIT = false, serviceRemoved = false;
    public static MediaSessionCompat mediaSession;
    public static TaskStackBuilder taskStackBuilder;
    public static PendingIntent resultPendingIntent;
    public static Intent resultIntent;
    public static NotificationCompat.Builder notificationBuilder;
    public static int isDisclaimer = 0;
    public static MediaSessionConnector mediaSessionConnector;
    public static int playlistDetailRefresh = 0;
    public static boolean AudioInterrupted = false, logout = false;
    public static Analytics analytics;
    public static boolean audioClick = false, tutorial = false;
    public static List<String> downloadAudioDetailsList = new ArrayList<>();
    public static List<DownloadAudioDetails> playlistDownloadAudioDetailsList = new ArrayList<>();
    static List<String> remiderDays = new ArrayList<>();
    static Context mContext;
    static BWSApplication BWSApplication;
    public static String currantTime = "", am_pm = "", hourString = "", minuteSting = "";
    public static String category = "";
    public static int Chour, Cminute;
    public static String key = "";
    public static TextView tvTime;
    static CheckBox cbCheck;
    public static AudioDatabase DB;
    public static int isPlayPlaylist = 0;
    public static int isSetLoginPin = 0;
    public static LocalBroadcastManager localBroadcastManager;
    public static Intent localIntent;
//    public static CleverTapAPI clevertapDefaultInstance;
    public static String countryShortName = "", countryCode = "", countryFullName = "";

    public static Context getContext() {
        return mContext;
    }

    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, PlayerJobService.class);
        JobInfo.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder = new JobInfo.Builder(0, serviceComponent);
            builder.setMinimumLatency(1000); // wait at least
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
        DB = getAudioDataBase(ctx);
        DB.taskDao().geAllDataBYDownloadedForAll("Complete").observe((LifecycleOwner) ctx, audioList -> {
            downloadAudioDetailsList = audioList;
            DB.taskDao().geAllDataBYDownloadedForAll("Complete").removeObserver(audioListx -> {
            });
        });
        return downloadAudioDetailsList;
    }

    private static void CallObserverMethodGetAllMedia(Context ctx) {
        DB = getAudioDataBase(ctx);
        DB.taskDao().geAllData1LiveForAll().observe((LifecycleOwner) ctx, audioList -> {
            playlistDownloadAudioDetailsList = audioList;

        });
    }

    public static void callEnhanceActivity(Context ctx, Activity act) {
        showToast("Please Re-activate Your Membership Plan", act);
        /*SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LockClick, Context.MODE_PRIVATE);
        String OldTime = shared1.getString(CONSTANTS.PREF_KEY_LockApiTime, "");
        int SetTime = shared1.getInt(CONSTANTS.PREF_KEY_LockSetTime, 0);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat outputFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
        String currentDate = outputFmt.format(calendar.getTime());

        Log.e("Old Time:- ",OldTime);
        Log.e("Current Time:- ",currentDate);
        if(OldTime.equals("")){
            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LockClick, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(CONSTANTS.PREF_KEY_LockApiTime, currentDate);
            editor.apply();
            callLockMailSendAPi(ctx,act, currentDate);
        }else {
            try {
                Date d1 = outputFmt.parse(currentDate);
                Date d2 = outputFmt.parse(OldTime);
                long difference_In_Time = d1.getTime() - d2.getTime();
                long difference_In_Hours = (difference_In_Time / (1000 * 60 * 60)) % 24;

                Log.e("difference_In_Hours:- ", String.valueOf(difference_In_Hours));
                if (difference_In_Hours >= SetTime) {
                    callLockMailSendAPi(ctx, act,currentDate);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }*/
    }

   /* private static void callLockMailSendAPi(Context ctx, Activity act, String currentDate) {
        showToast("Please Reactivate Your Plan", act);
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LockClick, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(CONSTANTS.PREF_KEY_LockApiTime, currentDate);
        editor.apply();
    }*/

    public static void callDelete403(Activity act, String msg) {
        try {
            analytics.flush();
            analytics.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
        deleteCall(act);
        showToast(msg, act);
        callSignActivity(act);
    }

    public static void callSignActivity(Activity act) {
        Intent i = new Intent(act, SignInActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra("mobileNo", "");
        i.putExtra("countryCode", "");
        i.putExtra("name", "");
        i.putExtra("email", "");
        i.putExtra("countryShortName", "");
        act.startActivity(i);
        act.finish();
       /* HashMap<String, Object> profileUpdate = new HashMap<>();
        profileUpdate.put("MSG-push", true);
        profileUpdate.put("MSG-email", true);
        profileUpdate.put("MSG-sms", true);
        profileUpdate.put("MSG-whatsapp", true);
        clevertapDefaultInstance.pushEvent("CleverTap SDK Integrated", profileUpdate);*/
    }

    private static void GetPlaylistDetail(Activity act, Context ctx, String PlaylistID, LinearLayout llDownload, ImageView ivDownloads, int songSize) {
        SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        String CoUserID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "");
        DB = getAudioDataBase(ctx);
        DB.taskDao().getPlaylist1(PlaylistID, CoUserID).observe((LifecycleOwner) ctx, audioList -> {

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

    public static void callAudioDetails(String audioId, Context ctx, Activity act, String CoUserID, String comeFrom, List<DownloadAudioDetails> mDataDownload, List<ViewAllAudioListModel.ResponseData.Detail> mDataViewAll, List<PlaylistDetailsModel.ResponseData.PlaylistSong> mDataPlaylist, List<MainPlayModel> mDataPlayer, int position) {
        //            TODO Mansi  Hint This code is Audio Detail Dialog
        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.open_detail_page_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ctx.getResources().getColor(R.color.blue_transparent_extra)));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        localIntent = new Intent("Reminder");
        localBroadcastManager = LocalBroadcastManager.getInstance(ctx);
        Properties p = new Properties();
        MainPlayModel m;
        ArrayList<MainPlayModel> mpm = new ArrayList<MainPlayModel>();
        if (comeFrom.equalsIgnoreCase("downloadList")) {
            for (int i = 0; i < mDataDownload.size(); i++) {
                m = new MainPlayModel();
                m.setId(mDataDownload.get(i).getID());
                m.setName(mDataDownload.get(i).getName());
                m.setAudioFile(mDataDownload.get(i).getAudioFile());
                m.setPlaylistID(mDataDownload.get(i).getPlaylistId());
                m.setAudioDirection(mDataDownload.get(i).getAudioDirection());
                m.setAudiomastercat(mDataDownload.get(i).getAudiomastercat());
                m.setAudioSubCategory(mDataDownload.get(i).getAudioSubCategory());
                m.setImageFile(mDataDownload.get(i).getImageFile());
                m.setAudioDuration(mDataDownload.get(i).getAudioDuration());
                mpm.add(m);
            }
        } else if (comeFrom.equalsIgnoreCase("playlist")) {
            for (int i = 0; i < mDataPlaylist.size(); i++) {
                m = new MainPlayModel();
                m.setId(mDataPlaylist.get(i).getId());
                m.setName(mDataPlaylist.get(i).getName());
                m.setAudioFile(mDataPlaylist.get(i).getAudioFile());
                m.setPlaylistID(mDataPlaylist.get(i).getPlaylistID());
                m.setAudioDirection(mDataPlaylist.get(i).getAudioDirection());
                m.setAudiomastercat(mDataPlaylist.get(i).getAudiomastercat());
                m.setAudioSubCategory(mDataPlaylist.get(i).getAudioSubCategory());
                m.setImageFile(mDataPlaylist.get(i).getImageFile());
                m.setAudioDuration(mDataPlaylist.get(i).getAudioDuration());
                mpm.add(m);
            }
        } else if (comeFrom.equalsIgnoreCase("viewAllAudioList")) {
            for (int i = 0; i < mDataViewAll.size(); i++) {
                m = new MainPlayModel();
                m.setId(mDataViewAll.get(i).getID());
                m.setName(mDataViewAll.get(i).getName());
                m.setAudioFile(mDataViewAll.get(i).getAudioFile());
                m.setPlaylistID("");
                m.setAudioDirection(mDataViewAll.get(i).getAudioDirection());
                m.setAudiomastercat(mDataViewAll.get(i).getAudiomastercat());
                m.setAudioSubCategory(mDataViewAll.get(i).getAudioSubCategory());
                m.setImageFile(mDataViewAll.get(i).getImageFile());
                m.setAudioDuration(mDataViewAll.get(i).getAudioDuration());
                mpm.add(m);
            }
        } else if (comeFrom.equalsIgnoreCase("audioPlayer")) {
            mpm.addAll(mDataPlayer);
        }
        addAudioSegmentEvent(ctx, position, mpm, "Audio Details Viewed", CONSTANTS.screen, downloadAudioDetailsList, p);
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
                if (comeFrom.equalsIgnoreCase("audioPlayer")) {
                    localIntent = new Intent("Reminder");
                    localBroadcastManager = LocalBroadcastManager.getInstance(ctx);
                    localIntent.putExtra("MyReminder", "update");
                    localBroadcastManager.sendBroadcast(localIntent);
                }
                dialog.dismiss();
                return true;
            }
            return false;
        });

        llBack.setOnClickListener(v -> {
            if (comeFrom.equalsIgnoreCase("audioPlayer")) {
                localIntent = new Intent("Reminder");
                localBroadcastManager = LocalBroadcastManager.getInstance(ctx);
                localIntent.putExtra("MyReminder", "update");
                localBroadcastManager.sendBroadcast(localIntent);
            }
            dialog.dismiss();
        });
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
        String PlayFrom = shared.getString(CONSTANTS.PREF_KEY_PlayFrom, "");

        if (isNetworkConnected(ctx)) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.invalidate();
            Call<AudioDetailModel> listCall = APINewClient.getClient().getAudioDetail(CoUserID, audioId);
            listCall.enqueue(new Callback<AudioDetailModel>() {
                @Override
                public void onResponse(@NotNull Call<AudioDetailModel> call, @NotNull Response<AudioDetailModel> response) {
                    try {
                        progressBar.setVisibility(View.GONE);
                        AudioDetailModel listModel = response.body();
                        if (listModel != null) {
                            if (listModel.getResponseCode().equalsIgnoreCase(act.getString(R.string.ResponseCodesuccess))) {
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
                                    Glide.with(ctx).load(listModel.getResponseData().get(0).getImageFile()).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(new RoundedCorners(32))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(ivRestaurantImage);
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
                                    final LinearLayout llDiscalimer = dialog1.findViewById(R.id.llDiscalimer);
                                    tvDesc.setText(listModel.getResponseData().get(0).getAudioDescription());
                                    llDiscalimer.setVisibility(View.VISIBLE);
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
                            } else if (listModel.getResponseCode().equals(act.getString(R.string.ResponseCodeDeleted))) {
                                callDelete403(act, listModel.getResponseMessage());
                            } else {
                                showToast(listModel.getResponseMessage(), act);
                            }
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
            GetAllMediaDownload(ctx);
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
                    Call<SucessModel> listCall = APINewClient.getClient().removeAudio(CoUserID, audioId, PlaylistId);
                    listCall.enqueue(new Callback<SucessModel>() {
                        @Override
                        public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                            SucessModel listModel = response.body();
                            hideProgressBar(progressBar, progressBarHolder, act);
                            if (listModel != null) {
                                if (listModel.getResponseCode().equalsIgnoreCase(act.getString(R.string.ResponseCodesuccess))) {
                                    p.putValue("audioId", audioId);
                                    p.putValue("audioName", mpm.get(position).getName());
                                    p.putValue("audioDescription", "");
                                    p.putValue("directions", mpm.get(position).getAudioDirection());
                                    p.putValue("masterCategory", mpm.get(position).getAudiomastercat());
                                    p.putValue("subCategory", mpm.get(position).getAudioSubCategory());
                                    p.putValue("audioDuration", mpm.get(position).getAudioDuration());
                                    p.putValue("position", GetCurrentAudioPosition());
                                    if (downloadAudioDetailsList.contains(mpm.get(position).getName())) {
                                        p.putValue("audioType", "Downloaded");
                                    } else {
                                        p.putValue("audioType", "Streaming");
                                    }
                                    p.putValue("source", "Audio Details Screen");
                                    p.putValue("audioService", appStatus(ctx));
                                    p.putValue("bitRate", "");
                                    p.putValue("playlistId", mpm.get(pos).getPlaylistID());
                                    p.putValue("playlistName", "");
                                    p.putValue("playlistType", "");
                                    p.putValue("playlistDuration", "");
                                    p.putValue("sound", String.valueOf(hundredVolume));
                                    addToSegment("Audio Removed From Playlist", p, CONSTANTS.track);

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
                                                    } else if (pos == position && position == mDataPlaylist.size()) {
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
                                                        mainPlayModel.setId(arrayList.get(i).getId());
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
                                                    editor1.apply();
                                                    if (player != null) {
                                                        player.removeMediaItem(oldpos);
                                                        player.seekTo(pos, 0);
                                                        player.setPlayWhenReady(true);
                                                        GlobalInitExoPlayer gb = new GlobalInitExoPlayer();
                                                        gb.UpdateNotificationAudioPLayer(ctx);
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
                                                        player.seekTo(pos, 0);
                                                        player.setPlayWhenReady(true);
                                                        GlobalInitExoPlayer gb = new GlobalInitExoPlayer();
                                                        gb.UpdateNotificationAudioPLayer(ctx);
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
                                } else if (listModel.getResponseCode().equalsIgnoreCase(act.getString(R.string.ResponseCodeDeleted))) {
                                    callDelete403(act, listModel.getResponseMessage());
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
            p.putValue("audioId", audioId);
            p.putValue("audioName", mpm.get(position).getName());
            p.putValue("audioDescription", "");
            p.putValue("directions", mpm.get(position).getAudioDirection());
            p.putValue("masterCategory", mpm.get(position).getAudiomastercat());
            p.putValue("subCategory", mpm.get(position).getAudioSubCategory());
            p.putValue("audioDuration", mpm.get(position).getAudioDuration());
            p.putValue("position", GetCurrentAudioPosition());
            if (downloadAudioDetailsList.contains(mpm.get(position).getName())) {
                p.putValue("audioType", "Downloaded");
            } else {
                p.putValue("audioType", "Streaming");
            }
            p.putValue("source", "Audio Details Screen");
            p.putValue("audioService", appStatus(ctx));
            p.putValue("bitRate", "");
            p.putValue("sound", String.valueOf(hundredVolume));
            addToSegment("Add To Playlist Clicked", p, CONSTANTS.track);
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

    public static void callPlaylistDetails(Context ctx, Activity act, String CoUSERID, String PlaylistId, String PlaylistName, FragmentManager fragmentManager1, String ScreenView) {
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
                        hideProgressBar(progressBar, progressBarHolder, act);
                        PlaylistDetailsModel listModel = response.body();
                        if (listModel.getResponseCode().equalsIgnoreCase(act.getString(R.string.ResponseCodesuccess))) {
                            GetPlaylistDetail(act, ctx, PlaylistId, llDownload, ivDownloads, listModel.getResponseData().getPlaylistSongs().size());
                            llDownload.setVisibility(View.VISIBLE);
                            DownloadPlaylistDetails downloadPlaylistDetails = new DownloadPlaylistDetails();
                            downloadPlaylistDetails.setPlaylistID(listModel.getResponseData().getPlaylistID());
                            downloadPlaylistDetails.setPlaylistName(listModel.getResponseData().getPlaylistName());
                            downloadPlaylistDetails.setPlaylistDesc(listModel.getResponseData().getPlaylistDesc());
                            downloadPlaylistDetails.setIsReminder("");
                            downloadPlaylistDetails.setPlaylistMastercat(listModel.getResponseData().getPlaylistMastercat());
                            downloadPlaylistDetails.setPlaylistSubcat(listModel.getResponseData().getPlaylistSubcat());
                            downloadPlaylistDetails.setPlaylistImage(listModel.getResponseData().getPlaylistImage());
                            downloadPlaylistDetails.setPlaylistImageDetails(listModel.getResponseData().getPlaylistImageDetail());
                            downloadPlaylistDetails.setTotalAudio(listModel.getResponseData().getTotalAudio());
                            downloadPlaylistDetails.setTotalDuration(listModel.getResponseData().getTotalDuration());
                            downloadPlaylistDetails.setTotalhour(listModel.getResponseData().getTotalhour());
                            downloadPlaylistDetails.setTotalminute(listModel.getResponseData().getTotalminute());
                            downloadPlaylistDetails.setCreated(listModel.getResponseData().getCreated());
                            tvName.setText(listModel.getResponseData().getPlaylistName());

                            String PlaylistDesc = listModel.getResponseData().getPlaylistDesc();
                            String PlaylistName = listModel.getResponseData().getPlaylistName();
                            String PlaylistID = listModel.getResponseData().getPlaylistID();
                            String TotalAudio = listModel.getResponseData().getTotalAudio();
                            String Totalhour = listModel.getResponseData().getTotalhour();
                            String Totalminute = listModel.getResponseData().getTotalminute();
                            llAddPlaylist.setOnClickListener(view -> {
                                Properties p = new Properties();
                                p.putValue("playlistId", PlaylistID);
                                p.putValue("playlistName", PlaylistName);
                                p.putValue("source", "Playlist Details Screen");
                                if (listModel.getResponseData().getCreated().equals("1")) {
                                    p.putValue("playlistType", "Created");
                                } else if (listModel.getResponseData().getCreated().equals("0")) {
                                    p.putValue("playlistType", "Default");
                                } else if (listModel.getResponseData().getCreated().equals("2"))
                                    p.putValue("playlistType", "Suggested");

                                if (Totalhour.equals("")) {
                                    p.putValue("playlistDuration", "0h " + Totalminute + "m");
                                } else if (Totalminute.equals("")) {
                                    p.putValue("playlistDuration", Totalhour + "h 0m");
                                } else {
                                    p.putValue("playlistDuration", Totalhour + "h " + Totalminute + "m");
                                }
                                addToSegment("Add To Playlist Clicked", p, CONSTANTS.track);
                                Intent i = new Intent(ctx, AddPlaylistActivity.class);
                                i.putExtra("AudioId", "");
                                i.putExtra("ScreenView", "Playlist Details Screen");
                                i.putExtra("PlaylistID", PlaylistID);
                                i.putExtra("PlaylistName", PlaylistName);
                                i.putExtra("PlaylistImage", listModel.getResponseData().getPlaylistImage());
                                i.putExtra("PlaylistType", listModel.getResponseData().getCreated());
                                i.putExtra("Liked", "0");
                                ctx.startActivity(i);
                                dialog.dismiss();
                            });

                            llFind.setOnClickListener(view -> {
                                localIntent.putExtra("MyFindAudio", "update");
                                localBroadcastManager.sendBroadcast(localIntent);
                                dialog.dismiss();
                            });

                            if (listModel.getResponseData().getPlaylistMastercat().equalsIgnoreCase("")) {
                                tvDesc.setVisibility(View.GONE);
                            } else {
                                tvDesc.setVisibility(View.VISIBLE);
                                tvDesc.setText(listModel.getResponseData().getPlaylistMastercat());
                            }

                            Properties p = new Properties();
                            p.putValue("playlistId", listModel.getResponseData().getPlaylistID());
                            p.putValue("playlistName", listModel.getResponseData().getPlaylistName());
                            p.putValue("playlistDescription", PlaylistDesc);
                            if (listModel.getResponseData().getCreated().equalsIgnoreCase("1")) {
                                p.putValue("playlistType", "Created");
                            } else if (listModel.getResponseData().getCreated().equalsIgnoreCase("0")) {
                                p.putValue("playlistType", "Default");
                            } else if (listModel.getResponseData().getCreated().equalsIgnoreCase("2")) {
                                p.putValue("playlistType", "suggested");
                            }
                            if (listModel.getResponseData().getTotalhour().equalsIgnoreCase("")) {
                                p.putValue("playlistDuration", "0h " + listModel.getResponseData().getTotalminute() + "m");
                            } else if (listModel.getResponseData().getTotalminute().equalsIgnoreCase("")) {
                                p.putValue("playlistDuration", listModel.getResponseData().getTotalhour() + "h 0m");
                            } else {
                                p.putValue("playlistDuration", listModel.getResponseData().getTotalhour() + "h " + listModel.getResponseData().getTotalminute() + "m");
                            }
                            p.putValue("audioCount", listModel.getResponseData().getTotalAudio());
                            p.putValue("source", ScreenView);
                            addToSegment("Playlist Details Viewed", p, CONSTANTS.screen);

                            if (listModel.getResponseData().getTotalAudio().equalsIgnoreCase("") || listModel.getResponseData().getTotalAudio().equalsIgnoreCase("0") && listModel.getResponseData().getTotalhour().equalsIgnoreCase("") && listModel.getResponseData().getTotalminute().equalsIgnoreCase("")) {
                                tvTime.setText("0 Audio | 0h 0m");
                            } else {
                                if (listModel.getResponseData().getTotalminute().equalsIgnoreCase("")) {
                                    tvTime.setText(listModel.getResponseData().getTotalAudio() + " Audio | " + listModel.getResponseData().getTotalhour() + "h 0m");
                                } else {
                                    tvTime.setText(listModel.getResponseData().getTotalAudio() + " Audio | " + listModel.getResponseData().getTotalhour() + "h " + listModel.getResponseData().getTotalminute() + "m");
                                }
                            }

                            if (listModel.getResponseData().getCreated().equalsIgnoreCase("1")) {
                                llOptions.setVisibility(View.GONE);
                                llRename.setVisibility(View.VISIBLE);
                                llDelete.setVisibility(View.VISIBLE);
                                llFind.setVisibility(View.GONE);
                            } else if (listModel.getResponseData().getCreated().equalsIgnoreCase("0")) {
                                llOptions.setVisibility(View.VISIBLE);
                                llRename.setVisibility(View.GONE);
                                llDelete.setVisibility(View.GONE);
                                llFind.setVisibility(View.VISIBLE);
                            } else if (listModel.getResponseData().getCreated().equalsIgnoreCase("2")) {
                                llOptions.setVisibility(View.VISIBLE);
                                llRename.setVisibility(View.GONE);
                                llDelete.setVisibility(View.GONE);
                                llFind.setVisibility(View.VISIBLE);
                            }

                            MeasureRatio measureRatio = measureRatio(ctx, 20, 1, 1, 0.54f, 20);
                            ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
                            ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
                            ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
                            if (!listModel.getResponseData().getPlaylistImage().equalsIgnoreCase("")) {
                                Glide.with(ctx).load(listModel.getResponseData().getPlaylistImage()).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(new RoundedCorners(32))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(ivRestaurantImage);
                            } else {
                                Glide.with(ctx).load(R.drawable.ic_playlist_bg).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(new RoundedCorners(32))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(ivRestaurantImage);
                            }

                            //                            getDownloadData();
                            int SongListSize = listModel.getResponseData().getPlaylistSongs().size();
                            //                            getMediaByPer(PlaylistID,SongListSize);
                            //                            SongListSize = model.getResponseData().getPlaylistSongs().size();
                            llAddPlaylist.setVisibility(View.VISIBLE);
                            //                            getDownloadData();

                            if (listModel.getResponseData().getPlaylistDesc().equalsIgnoreCase("")) {
                                tvTitleDec.setVisibility(View.GONE);
                                tvSubDec.setVisibility(View.GONE);
                            } else {
                                tvTitleDec.setVisibility(View.VISIBLE);
                                tvSubDec.setVisibility(View.VISIBLE);
                            }

                            tvSubDec.setText(listModel.getResponseData().getPlaylistDesc());
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
                                final LinearLayout llDiscalimer = dialog1.findViewById(R.id.llDiscalimer);
                                tvDesc.setText(listModel.getResponseData().getPlaylistDesc());
                                llDiscalimer.setVisibility(View.GONE);
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
                            //                                    listCall13 = APINewClient.getClient().getDownloadlistPlaylist(UserID, "", PlaylistID);
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

                            if (listModel.getResponseData().getPlaylistSubcat().equalsIgnoreCase("")) {
                                rvDirlist.setVisibility(View.GONE);
                            } else {
                                rvDirlist.setVisibility(View.VISIBLE);
                                String[] elements = listModel.getResponseData().getPlaylistSubcat().split(",");
                                List<String> direction = Arrays.asList(elements);
                                DirectionAdapter directionAdapter = new DirectionAdapter(direction, ctx);
                                RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
                                rvDirlist.setLayoutManager(recentlyPlayed);
                                rvDirlist.setItemAnimator(new DefaultItemAnimator());
                                rvDirlist.setAdapter(directionAdapter);
                            }

                            llDelete.setOnClickListener(view43 -> {
                                Properties p1 = new Properties();
                                p1.putValue("playlistId", listModel.getResponseData().getPlaylistID());
                                p1.putValue("playlistName", listModel.getResponseData().getPlaylistName());
                                p1.putValue("playlistDescription", PlaylistDesc);
                                if (listModel.getResponseData().getCreated().equalsIgnoreCase("1")) {
                                    p1.putValue("playlistType", "Created");
                                } else if (listModel.getResponseData().getCreated().equalsIgnoreCase("0")) {
                                    p1.putValue("playlistType", "Default");
                                } else if (listModel.getResponseData().getCreated().equalsIgnoreCase("2")) {
                                    p1.putValue("playlistType", "suggested");
                                }
                                if (listModel.getResponseData().getTotalhour().equalsIgnoreCase("")) {
                                    p1.putValue("playlistDuration", "0h " + listModel.getResponseData().getTotalminute() + "m");
                                } else if (listModel.getResponseData().getTotalminute().equalsIgnoreCase("")) {
                                    p1.putValue("playlistDuration", listModel.getResponseData().getTotalhour() + "h 0m");
                                } else {
                                    p1.putValue("playlistDuration", listModel.getResponseData().getTotalhour() + "h " + listModel.getResponseData().getTotalminute() + "m");
                                }
                                p1.putValue("audioCount", listModel.getResponseData().getTotalAudio());
                                p1.putValue("source", ScreenView);
                                addToSegment("Delete Playlist Clicked", p1, CONSTANTS.screen);
                                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                                String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
                                String pID = shared.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "0");
                                if (AudioFlag.equalsIgnoreCase("playlist") && pID.equalsIgnoreCase(PlaylistId)) {
                                    int unicode = 0x1F6AB;
                                    String textIcons = new String(Character.toChars(unicode));
                                    showToast("You can't delete a playlist while it's playing." + textIcons, act);
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
                                                        SucessModel listModel1 = response12.body();
                                                        if (listModel1 != null) {
                                                            if (listModel1.getResponseCode().equalsIgnoreCase(ctx.getString(R.string.ResponseCodesuccess))) {
                                                                hideProgressBar(progressBar, progressBarHolder, act);
                                                                Properties p = new Properties();
                                                                p.putValue("playlistId", listModel.getResponseData().getPlaylistID());
                                                                p.putValue("playlistName", listModel.getResponseData().getPlaylistName());
                                                                p.putValue("playlistDescription", PlaylistDesc);
                                                                if (listModel.getResponseData().getCreated().equalsIgnoreCase("1")) {
                                                                    p.putValue("playlistType", "Created");
                                                                } else if (listModel.getResponseData().getCreated().equalsIgnoreCase("0")) {
                                                                    p.putValue("playlistType", "Default");
                                                                } else if (listModel.getResponseData().getCreated().equalsIgnoreCase("2")) {
                                                                    p.putValue("playlistType", "suggested");
                                                                }
                                                                if (listModel.getResponseData().getTotalhour().equalsIgnoreCase("")) {
                                                                    p.putValue("playlistDuration", "0h " + listModel.getResponseData().getTotalminute() + "m");
                                                                } else if (listModel.getResponseData().getTotalminute().equalsIgnoreCase("")) {
                                                                    p.putValue("playlistDuration", listModel.getResponseData().getTotalhour() + "h 0m");
                                                                } else {
                                                                    p.putValue("playlistDuration", listModel.getResponseData().getTotalhour() + "h " + listModel.getResponseData().getTotalminute() + "m");
                                                                }
                                                                p.putValue("audioCount", listModel.getResponseData().getTotalAudio());
                                                                p.putValue("source", ScreenView);
                                                                addToSegment("Playlist Deleted", p, CONSTANTS.screen);
                                                                dialoged.dismiss();
                                                                showToast(listModel.getResponseMessage(), act);
                                                                act.finish();
                                                                dialog.dismiss();
                                                            } else if (listModel1.getResponseCode().equalsIgnoreCase(act.getString(R.string.ResponseCodeDeleted))) {
                                                                callDelete403(act, listModel.getResponseMessage());
                                                            }
                                                        }
                                                        //                                                            Fragment audioFragment = new MainPlaylistFragment();
                                                        //                                                            fragmentManager1.beginTransaction()
                                                        //                                                                    .replace(R.id.flContainer, audioFragment)
                                                        //                                                                    .commit();

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
                                edtCreate.clearFocus();
                                edtCreate.setText(listModel.getResponseData().getPlaylistName());
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
                                Properties p1 = new Properties();
                                p1.putValue("playlistId", listModel.getResponseData().getPlaylistID());
                                p1.putValue("playlistName", listModel.getResponseData().getPlaylistName());
                                p1.putValue("playlistDescription", PlaylistDesc);
                                if (listModel.getResponseData().getCreated().equalsIgnoreCase("1")) {
                                    p.putValue("playlistType", "Created");
                                } else if (listModel.getResponseData().getCreated().equalsIgnoreCase("0")) {
                                    p.putValue("playlistType", "Default");
                                } else if (listModel.getResponseData().getCreated().equalsIgnoreCase("2")) {
                                    p.putValue("playlistType", "suggested");
                                }
                                if (listModel.getResponseData().getTotalhour().equalsIgnoreCase("")) {
                                    p.putValue("playlistDuration", "0h " + listModel.getResponseData().getTotalminute() + "m");
                                } else if (listModel.getResponseData().getTotalminute().equalsIgnoreCase("")) {
                                    p.putValue("playlistDuration", listModel.getResponseData().getTotalhour() + "h 0m");
                                } else {
                                    p.putValue("playlistDuration", listModel.getResponseData().getTotalhour() + "h " + listModel.getResponseData().getTotalminute() + "m");
                                }
                                p.putValue("audioCount", listModel.getResponseData().getTotalAudio());
                                p.putValue("source", ScreenView);
                                addToSegment("Playlist Rename Clicked", p, CONSTANTS.screen);

                                TextWatcher popupTextWatcher = new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        String number = edtCreate.getText().toString();
                                        if (!number.equalsIgnoreCase("")) {
                                            if (number.equalsIgnoreCase(listModel.getResponseData().getPlaylistName())) {
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
                                        Call<RenameNewPlaylistModel> listCall1 = APINewClient.getClient().getRenameNewPlaylist(CoUSERID, PlaylistID, edtCreate.getText().toString());
                                        listCall1.enqueue(new Callback<RenameNewPlaylistModel>() {
                                            @Override
                                            public void onResponse(Call<RenameNewPlaylistModel> call1, Response<RenameNewPlaylistModel> response1) {
                                                try {
                                                    hideProgressBar(progressBar, progressBarHolder, act);
                                                    RenameNewPlaylistModel listModel1 = response1.body();
                                                    if (listModel1 != null) {
                                                        if (listModel1.getResponseCode().equalsIgnoreCase(ctx.getString(R.string.ResponseCodesuccess))) {
                                                            if (listModel1.getResponseData().getIsRename().equalsIgnoreCase("0")) {
                                                                showToast(listModel1.getResponseMessage(), act);
                                                            } else if (listModel1.getResponseData().getIsRename().equalsIgnoreCase("1")) {
                                                                showToast(listModel1.getResponseMessage(), act);
                                                                tvName.setText(edtCreate.getText().toString());
                                                                Properties p1 = new Properties();
                                                                p1.putValue("playlistId", listModel.getResponseData().getPlaylistID());
                                                                p1.putValue("playlistName", edtCreate.getText().toString());
                                                                p1.putValue("playlistDescription", PlaylistDesc);
                                                                if (listModel.getResponseData().getCreated().equalsIgnoreCase("1")) {
                                                                    p.putValue("playlistType", "Created");
                                                                } else if (listModel.getResponseData().getCreated().equalsIgnoreCase("0")) {
                                                                    p.putValue("playlistType", "Default");
                                                                } else if (listModel.getResponseData().getCreated().equalsIgnoreCase("2")) {
                                                                    p.putValue("playlistType", "suggested");
                                                                }
                                                                if (listModel.getResponseData().getTotalhour().equalsIgnoreCase("")) {
                                                                    p.putValue("playlistDuration", "0h " + listModel.getResponseData().getTotalminute() + "m");
                                                                } else if (listModel.getResponseData().getTotalminute().equalsIgnoreCase("")) {
                                                                    p.putValue("playlistDuration", listModel.getResponseData().getTotalhour() + "h 0m");
                                                                } else {
                                                                    p.putValue("playlistDuration", listModel.getResponseData().getTotalhour() + "h " + listModel.getResponseData().getTotalminute() + "m");
                                                                }
                                                                p.putValue("audioCount", listModel.getResponseData().getTotalAudio());
                                                                p.putValue("source", ScreenView);
                                                                addToSegment("Playlist Renamed", p, CONSTANTS.screen);
                                                                localIntent.putExtra("MyReminder", "update");
                                                                localBroadcastManager.sendBroadcast(localIntent);
                                                                dialogs.dismiss();
                                                            }
                                                        } else if (listModel1.getResponseCode().equalsIgnoreCase(act.getString(R.string.ResponseCodeDeleted))) {
                                                            callDelete403(act, listModel.getResponseMessage());
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<RenameNewPlaylistModel> call1, Throwable t) {
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

                            llDownload.setOnClickListener(view -> callDownloadPlayList(act, listModel.getResponseData().getPlaylistSongs(), ctx, llDownload, ivDownloads, downloadPlaylistDetails, CoUSERID, PlaylistID));

                        } else if (listModel.getResponseCode().equalsIgnoreCase(act.getString(R.string.ResponseCodeDeleted))) {
                            callDelete403(act, listModel.getResponseMessage());
                        } else {
                            showToast(listModel.getResponseMessage(), act);
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

    private static void callDownloadPlayList(Activity act, List<PlaylistDetailsModel.ResponseData.PlaylistSong> playlistSongsList, Context ctx, LinearLayout llDownload, ImageView ivDownloads, DownloadPlaylistDetails downloadPlaylistDetails, String CoUserId, String PlaylistID) {
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
        SharedPreferences sharedx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
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
            editor.apply();
        }

        int unicode = 0x1F44C;
        String textIcon = new String(Character.toChars(unicode));

        int unicode1 = 0x1F44A;
        String textIcon1 = new String(Character.toChars(unicode));
        showToast("Your playlist is being downloaded! " + textIcon1, act);
        savePlaylist(ctx, downloadPlaylistDetails);
        saveAllMedia(playlistSongsList, PlaylistID, CoUserId, ctx, downloadPlaylistDetails);
    }

    private static void savePlaylist(Context ctx, DownloadPlaylistDetails downloadPlaylistDetails) {
        DB = getAudioDataBase(ctx);
        SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        String CoUserID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "");
        downloadPlaylistDetails.setUserID(CoUserID);
        try {
            AudioDatabase.databaseWriteExecutor.execute(() -> DB.taskDao().insertPlaylist(downloadPlaylistDetails));
        } catch (Exception | OutOfMemoryError e) {
            System.out.println(e.getMessage());
        }
    }

    public static AudioDatabase getAudioDataBase(Context ctx) {
        DB = Room.databaseBuilder(ctx, AudioDatabase.class, "Audio_database").addMigrations(MIGRATION_2_3).build();
        return DB;
    }

    private static void saveAllMedia(List<PlaylistDetailsModel.ResponseData.PlaylistSong> playlistSongs, String PlaylistID, String CoUserId, Context ctx, DownloadPlaylistDetails downloadPlaylistDetails) {
        DB = getAudioDataBase(ctx);
        downloadPlaylistDetails.setUserID(CoUserId);
        Properties p = new Properties();
        p.putValue("playlistId", downloadPlaylistDetails.getPlaylistID());
        p.putValue("playlistName", downloadPlaylistDetails.getPlaylistName());
        p.putValue("playlistDescription", downloadPlaylistDetails.getPlaylistDesc());
        if (downloadPlaylistDetails.getCreated().equalsIgnoreCase("1")) {
            p.putValue("playlistType", "Created");
        } else if (downloadPlaylistDetails.getCreated().equalsIgnoreCase("0")) {
            p.putValue("playlistType", "Default");
        } else if (downloadPlaylistDetails.getCreated().equals("2"))
            p.putValue("playlistType", "Suggested");

        if (downloadPlaylistDetails.getTotalhour().equalsIgnoreCase("")) {
            p.putValue("playlistDuration", "0h " + downloadPlaylistDetails.getTotalminute() + "m");
        } else if (downloadPlaylistDetails.getTotalminute().equalsIgnoreCase("")) {
            p.putValue("playlistDuration", downloadPlaylistDetails.getTotalhour() + "h 0m");
        } else {
            p.putValue("playlistDuration", downloadPlaylistDetails.getTotalhour() + "h " + downloadPlaylistDetails.getTotalminute() + "m");
        }
        p.putValue("audioCount", downloadPlaylistDetails.getTotalAudio());
        p.putValue("source", "Downloaded Playlists");
        p.putValue("audioService", appStatus(ctx));
        p.putValue("sound", String.valueOf(hundredVolume));
        addToSegment("Playlist Download Started", p, CONSTANTS.track);
        for (int i = 0; i < playlistSongs.size(); i++) {
            DownloadAudioDetails downloadAudioDetails = new DownloadAudioDetails();
            downloadAudioDetails.setUserID(CoUserId);
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

    public static void GetMedia(String AudioFile, Context ctx, String audioFileName, ImageView ivDownloads, TextView tvDownloads, LinearLayout llDownload) {
        DB = getAudioDataBase(ctx);
        SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        String CoUserID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "");
        DB.taskDao().getaudioByPlaylist1(AudioFile, "", CoUserID).observe((LifecycleOwner) ctx, audioList -> {
            List<String> fileNameList = new ArrayList<>();
            List<String> audioFile1 = new ArrayList<>();
            List<String> playlistDownloadId = new ArrayList<>();
            SharedPreferences sharedx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
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
                        if (fileNameList.get(i).equalsIgnoreCase(audioFileName) && playlistDownloadId.get(i).equalsIgnoreCase("")) {
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

    public static void getGraphIndexScore(HomeScreenModel.ResponseData indexData, BarChart barChart, LinearLayout llPastIndexScore, LineChart chart, Context ctx, Activity act) {
        if (indexData.getGraphIndexScore().size() == 0) {
            barChart.clear();
            barChart.setVisibility(View.GONE);
            llPastIndexScore.setVisibility(View.GONE);
        } else {
         /*   barChart.setVisibility(View.VISIBLE);
            llPastIndexScore.setVisibility(View.VISIBLE);
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

            for (int i = 0; i < indexData.getGraphIndexScore().size(); i++) {
                float val = Float.parseFloat(indexData.getGraphIndexScore().get(i).getIndexScore());
                yAxisValues.add(new BarEntry(i * spaceForBar, val));
            }

            final ArrayList<String> xAxisValues = new ArrayList<>();
            for (int i = 0; i < indexData.getGraphIndexScore().size(); i++) {
                xAxisValues.add(indexData.getGraphIndexScore().get(i).getMonthName());
            }

            BarDataSet barDataSet;
            barDataSet = new BarDataSet(yAxisValues, "Past wellness Score");
            barDataSet.setDrawIcons(false);
            barDataSet.setColor(act.getResources().getColor(R.color.app_theme_color));

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(barDataSet);

            BarData barData = new BarData(dataSets);
            barData.setBarWidth(0.5f);
            barDataSet.notifyDataSetChanged();
            barData.setValueFormatter(new MyValueFormatter());
            barData.setValueTextSize(7f);

            barChart.setData(barData);
            barChart.notifyDataSetChanged();
            barChart.invalidate();

            XAxis xl = barChart.getXAxis();
            xl.setDrawGridLines(true);
            xl.setDrawAxisLine(true);
            xl.setGranularity(1f);
            xl.setLabelCount(7);
            xl.setLabelRotationAngle(0);
            xl.setPosition(XAxis.XAxisPosition.BOTTOM);
            xl.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
            *//*xl.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf(xAxisValues.get((int) value));
                }
            });*//*
            YAxis yl = barChart.getAxisLeft();
            yl.setDrawAxisLine(true);
            yl.setDrawGridLines(true);
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
            barChart.getLegend().setEnabled(false);

            Legend l = barChart.getLegend();
            l.setEnabled(false);
            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            l.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
            l.setXEntrySpace(4f);
            l.setYEntrySpace(0f);
//                        l.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
            l.setWordWrapEnabled(true);*/

            // background color
            chart.setBackgroundColor(Color.WHITE);
            chart.getDescription().setEnabled(false);
            chart.setTouchEnabled(false);
            chart.setDrawGridBackground(false);
            MyMarkerView mv = new MyMarkerView(ctx, R.layout.custom_marker_view);
            mv.setChartView(chart);
            chart.setMarker(mv);
            chart.setDragEnabled(true);
            chart.setScaleEnabled(true);
            chart.setPinchZoom(false);
        }
        final ArrayList<String> xAxisValues = new ArrayList<>();
        XAxis xAxis = null;
        YAxis yAxis = null;
        if (indexData.getGraphIndexScore().size() != 0) {
            {
//            xAxisValues.add("");
                for (int i = 0; i < indexData.getGraphIndexScore().size(); i++) {
                    xAxisValues.add(indexData.getGraphIndexScore().get(i).getDisplayName());
                }
                xAxis = chart.getXAxis();
                xAxis.setAxisMinimum(0);
                if (xAxisValues.size() != 0) {
                    xAxis.setAxisMaximum(xAxisValues.size() - 1);
                    xAxis.setLabelCount(xAxisValues.size(), true);
//            xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
                    try {
                        xAxis.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getFormattedValue(float value) {
                                return String.valueOf(xAxisValues.get((int) value));
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                xAxis.setGranularity(1f);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setLabelRotationAngle(20);
                xAxis.enableGridDashedLine(10f, 10f, 0f);
            }

            {
                yAxis = chart.getAxisLeft();
                chart.getAxisRight().setEnabled(false);
                yAxis.enableGridDashedLine(10f, 10f, 0f);
                yAxis.setAxisMaximum(100f);
                yAxis.setAxisMinimum(0f);
            }
            {

                LimitLine ll1 = new LimitLine(180f, "Upper Limit");
                ll1.setLineWidth(4f);
                ll1.enableDashedLine(10f, 10f, 0f);
                ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                ll1.setTextSize(9f);

                LimitLine ll2 = new LimitLine(-30f, "Lower Limit");
                ll2.setLineWidth(4f);
                ll2.enableDashedLine(10f, 10f, 0f);
                ll2.setLabelPosition(LimitLabelPosition.RIGHT_BOTTOM);
                ll2.setTextSize(9f);

                // draw limit lines behind data instead of on top
                yAxis.setDrawLimitLinesBehindData(true);
                xAxis.setDrawLimitLinesBehindData(true);

                // add limit lines
                yAxis.addLimitLine(ll1);
                yAxis.addLimitLine(ll2);
                //xAxis.addLimitLine(llXAxis);
            }

            // add data
            setData(chart, ctx, indexData.getGraphIndexScore());
            Legend l = chart.getLegend();
            l.setForm(LegendForm.LINE);
            l.setFormSize(0f);
            l.setFormToTextSpace(5f);
            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            l.setDrawInside(false);
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            l.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
            l.setXEntrySpace(4f);
            l.setYEntrySpace(0f);
            l.setWordWrapEnabled(true);
        }
//        }
    }

    private static void setData(LineChart chart, Context ctx, List<HomeScreenModel.ResponseData.GraphIndexScore> pastIndexScore) {

        ArrayList<Entry> values = new ArrayList<>();
//        float val = Float.parseFloat("0");
//        values.add(new Entry(0, val, ctx.getResources().getDrawable(R.drawable.ic_star)));

        for (int i = 0; i < pastIndexScore.size(); i++) {
            float val = Float.parseFloat(pastIndexScore.get(i).getIndexScore());
            values.add(new Entry(i, val, ctx.getResources().getDrawable(R.drawable.ic_star)));
        }

        LineDataSet set1;

        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "Past Wellness Score");
            set1.setDrawIcons(false);
            // draw dashed line
            set1.enableDashedLine(10f, 5f, 0f);
            // black lines and points
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            // line thickness and point size
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            // draw points as solid circles
            set1.setDrawCircleHole(false);
            // customize legend entry
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 10f}, 0f));
            set1.setFormSize(15.f);
            // text size of values
            set1.setValueTextSize(9f);
            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            // set the filled area
            set1.setDrawFilled(true);
            set1.setFillFormatter((dataSet, dataProvider) -> chart.getAxisLeft().getAxisMinimum());
            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(ctx, R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.BLACK);
            }
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            chart.setData(data);
        }
    }

    public static void getUserActivity(HomeScreenModel.ResponseData indexData, BarChart barChart, LinearLayout llLegendActivity, Activity act) {
        if (indexData.getGraphAnalytics().size() == 0) {
            barChart.clear();
            barChart.setVisibility(View.GONE);
            llLegendActivity.setVisibility(View.GONE);
        } else {
            barChart.setVisibility(View.VISIBLE);
            llLegendActivity.setVisibility(View.VISIBLE);
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

            for (int i = 0; i < indexData.getGraphAnalytics().size(); i++) {
                float val = Float.parseFloat(indexData.getGraphAnalytics().get(i).getTime());
                yAxisValues.add(new BarEntry(i * spaceForBar, val));
            }

            final ArrayList<String> xAxisValues = new ArrayList<>();
            for (int i = 0; i < indexData.getGraphAnalytics().size(); i++) {
                xAxisValues.add(indexData.getGraphAnalytics().get(i).getDay());
            }

            BarDataSet barDataSet;
            barDataSet = new BarDataSet(yAxisValues, "Last 7 Days Time");
            barDataSet.setDrawIcons(false);
            barDataSet.notifyDataSetChanged();
            barDataSet.setColor(act.getResources().getColor(R.color.blue));

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(barDataSet);

            BarData barData = new BarData(dataSets);
            barData.setBarWidth(0.5f);
            barData.setValueFormatter(new MyValueFormatter());
            barData.setValueTextSize(7f);

            barChart.setData(barData);
            barChart.notifyDataSetChanged();
            barChart.invalidate();

            XAxis xl = barChart.getXAxis();
            xl.setGranularity(1f);
            xl.setDrawGridLines(true);
            xl.setDrawAxisLine(true);
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
            yl.setDrawGridLines(true);
            yl.setGranularity(1f);
            yl.setGranularityEnabled(true);
            yl.setAxisMaximum(12f);
            yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)
            barChart.setVisibleXRangeMaximum(xAxisValues.size());
            barChart.setDragEnabled(false);
            barChart.setTouchEnabled(false);
            barChart.getAxisRight().setEnabled(false);
            barChart.setVisibleXRange(0, xAxisValues.size());
            barChart.getBarData().setBarWidth(0.4f);
            barChart.getXAxis().setDrawGridLines(false);
            barChart.setFitBars(true);
            barChart.getLegend().setEnabled(false);

            Legend l = barChart.getLegend();
            l.setEnabled(false);
            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            l.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
            l.setXEntrySpace(4f);
            l.setYEntrySpace(0f);
            //            l.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
            l.setWordWrapEnabled(true);
        }
    }

    private static void callDownload(String comeFrom, List<DownloadAudioDetails> mDataDownload, List<ViewAllAudioListModel.ResponseData.Detail> mDataViewAll, List<PlaylistDetailsModel.ResponseData.PlaylistSong> mDataPlaylist, List<MainPlayModel> mDataPlayer, int position, Context ctx, ImageView ivDownloads, Activity act, LinearLayout llDownload) {
        List<String> fileNameList = new ArrayList<>();
        List<String> audioFile1;
        List<String> playlistDownloadId = new ArrayList<>();
        GetAllMediaDownload(ctx);
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
                int unicode = 0x1F642;
                String textIcon4 = new String(Character.toChars(unicode));
                showToast("Your audio is being downloaded!" + textIcon4, act);
            } else {
                List<String> url1 = new ArrayList<>();
                List<String> name1 = new ArrayList<>();
                List<String> downloadPlaylistId = new ArrayList<>();
                SharedPreferences sharedx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
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
                    if (fileNameList.get(f).equalsIgnoreCase(Name) && playlistDownloadId.get(f).equalsIgnoreCase("")) {
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
                        editor.apply();
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
                    int unicode = 0x1F44C;
                    String textIcon = new String(Character.toChars(unicode));
                    showToast("Yess! Download complete. Your wellness journey is ready!" + textIcon, act);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void SaveMedia(int i, int progress, String comeFrom, List<DownloadAudioDetails> mDataDownload, List<ViewAllAudioListModel.ResponseData.Detail> mDataViewAll, List<PlaylistDetailsModel.ResponseData.PlaylistSong> mDataPlaylist, List<MainPlayModel> mDataPlayer, Context ctx) {
        DB = getAudioDataBase(ctx);
        DownloadAudioDetails downloadAudioDetails = new DownloadAudioDetails();
        SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        String CoUserID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "");
        downloadAudioDetails.setUserID(CoUserID);
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
            downloadAudioDetails.setID(mDataPlayer.get(i).getId());
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
        try {
            AudioDatabase.databaseWriteExecutor.execute(() -> DB.taskDao().insertMedia(downloadAudioDetails));
        } catch (Exception | OutOfMemoryError e) {
            System.out.println(e.getMessage());
        }
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
            mainPlayModel1.setId(downloadAudioDetails.getID());
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
            editor.apply();
            if (!arrayList2.get(PlayerPosition).getAudioFile().equals("")) {
                List<String> downloadAudioDetailsList = new ArrayList<>();
                GlobalInitExoPlayer ge = new GlobalInitExoPlayer();
                downloadAudioDetailsList.add(downloadAudioDetails.getName());
                ge.AddAudioToPlayer(size, arrayList2, downloadAudioDetailsList, ctx);
            }
            //                callAddTransFrag();
        }
    }

    @SuppressLint("SetTextI18n")
    public static void getReminderDay(Context ctx, Activity act, String userId, String playlistID, String playlistName, FragmentActivity fragmentActivity, String Time, String RDay, String isSuggested, String reminderID, String isReminder, String created) {
        ReminderSelectionModel[] reminderSelectionModel = new ReminderSelectionModel[]{new ReminderSelectionModel("Sunday"), new ReminderSelectionModel("Monday"), new ReminderSelectionModel("Tuesday"), new ReminderSelectionModel("Wednesday"), new ReminderSelectionModel("Thursday"), new ReminderSelectionModel("Friday"), new ReminderSelectionModel("Saturday"),};
        localIntent = new Intent("Reminder");
        localBroadcastManager = LocalBroadcastManager.getInstance(ctx);
        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.select_days_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ctx.getResources().getColor(R.color.blue_transparent)));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final ImageView ivBack = dialog.findViewById(R.id.ivBack);
        final RecyclerView rvSelectDay = dialog.findViewById(R.id.rvSelectDay);
        final TextView tvPlaylistName = dialog.findViewById(R.id.tvPlaylistName);
        final TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        tvTime = dialog.findViewById(R.id.tvTime);
        final Button btnNext = dialog.findViewById(R.id.btnNext);
        cbCheck = dialog.findViewById(R.id.cbChecked);
        final LinearLayout llSelectTime = dialog.findViewById(R.id.llSelectTime);
        final LinearLayout llOptions = dialog.findViewById(R.id.llOptions);
        final ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        final FrameLayout progressBarHolder = dialog.findViewById(R.id.progressBarHolder);

        String timezoneName = "";
        cbCheck.setText(ctx.getString(R.string.select_all));

        if (Time.equalsIgnoreCase("") || Time.equalsIgnoreCase("0")) {
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("hh:mm a");
            simpleDateFormat1.setTimeZone(TimeZone.getDefault());
            timezoneName = simpleDateFormat1.getTimeZone().getID();
            Log.e("Display Name Time Zone", simpleDateFormat1.getTimeZone().getID());
            Log.e("Default Time Zone", simpleDateFormat1.getTimeZone().getDisplayName() + simpleDateFormat1.getTimeZone());
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
            if (created.equals("2")) tvTitle.setVisibility(View.VISIBLE);
            else tvTitle.setVisibility(View.GONE);
            Properties p = new Properties();
            p.putValue("reminderId ", "");
            p.putValue("playlistId ", "");
            p.putValue("playlistName ", "");
            p.putValue("playlistType ", "");
            p.putValue("reminderStatus ", "");
            p.putValue("reminderTime ", "");
            p.putValue("reminderDay", "");
            addToSegment("Add/Edit Reminder Screen Viewed", p, CONSTANTS.screen);
        } else {
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("hh:mm a");
            simpleDateFormat1.setTimeZone(TimeZone.getDefault());
            timezoneName = simpleDateFormat1.getTimeZone().getID();
            Log.e("Display Name Time Zone", simpleDateFormat1.getTimeZone().getID());
            tvTime.setText(Time);
            currantTime = Time;
            Properties p = new Properties();
            p.putValue("reminderId ", reminderID);
            p.putValue("playlistId ", playlistID);
            p.putValue("playlistName ", playlistName);
            switch (created) {
                case "1":
                    p.putValue("playlistType", "Created");
                    tvTitle.setVisibility(View.GONE);
                    break;
                case "0":
                    p.putValue("playlistType", "Default");
                    tvTitle.setVisibility(View.GONE);
                    break;
                case "2":
                    p.putValue("playlistType", "Suggested");
                    tvTitle.setVisibility(View.VISIBLE);
                    break;
            }
            switch (isReminder) {
                case "0":
                    p.putValue("reminderStatus ", "");
                    break;
                case "1":
                    p.putValue("reminderStatus ", "On");
                    break;
                case "2":
                    p.putValue("reminderStatus ", "Off");
                    break;
            }
            p.putValue("reminderTime ", Time);
            p.putValue("reminderDay", RDay);
            addToSegment("Add/Edit Reminder Screen Viewed", p, CONSTANTS.screen);
        }
        boolean areNotificationEnabled = NotificationManagerCompat.from(ctx).areNotificationsEnabled();
        Log.e("areNotificationEnabled", String.valueOf(areNotificationEnabled));
        if (!areNotificationEnabled) {
            Dialog dialogNotification = new Dialog(ctx);
            dialogNotification.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogNotification.setContentView(R.layout.custom_popup_layout);
            dialogNotification.getWindow().setBackgroundDrawable(new ColorDrawable(ctx.getResources().getColor(R.color.transparent_white)));
            dialogNotification.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            final TextView tvGoBack = dialogNotification.findViewById(R.id.tvGoBack);
            final TextView tvHeader = dialogNotification.findViewById(R.id.tvHeader);
            final TextView tvTitle1 = dialogNotification.findViewById(R.id.tvTitle);
            final Button btn = dialogNotification.findViewById(R.id.Btn);
            tvTitle1.setText(ctx.getString(R.string.unable_to_use_notification_title));
            tvHeader.setText(ctx.getString(R.string.unable_to_use_notification_content));
            btn.setText("Settings");
            tvGoBack.setText("Cancel");
            dialogNotification.setOnKeyListener((dialog1, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialogNotification.dismiss();
                }
                return false;
            });
            btn.setOnClickListener(v -> {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + ctx.getPackageName()));
                ctx.startActivity(intent);
                dialogNotification.dismiss();
            });
            tvGoBack.setOnClickListener(v -> {
                dialogNotification.dismiss();
            });
            dialogNotification.show();
            dialogNotification.setCancelable(false);
        } else {
            String[] time = currantTime.split(":");
            String[] min = time[1].split(" ");
            Chour = Integer.parseInt(time[0]);
            //            mHour = c.get(Calendar.HOUR_OF_DAY);
            Cminute = Integer.parseInt(min[0]);
            String displayAmPm = min[1];
            if (displayAmPm.equalsIgnoreCase("p.m") || displayAmPm.equalsIgnoreCase("PM")) {
                if (Chour != 12) Chour = Chour + 12;
            }

            tvPlaylistName.setText(playlistName);
            ivBack.setOnClickListener(view12 -> dialog.dismiss());
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

            RecyclerView.LayoutManager manager = new LinearLayoutManager(ctx);
            rvSelectDay.setLayoutManager(manager);
            rvSelectDay.setItemAnimator(new DefaultItemAnimator());
            ReminderSelectionListAdapter adapter = new ReminderSelectionListAdapter(reminderSelectionModel, act, ctx, btnNext, userId, playlistID, playlistName, dialog, fragmentActivity, tvTime, progressBarHolder, progressBar, llSelectTime, RDay, Time, isSuggested, reminderID, created, llOptions, timezoneName);
            rvSelectDay.setAdapter(adapter);

            Log.e("remiderDays", TextUtils.join(",", remiderDays));
            dialog.show();
            dialog.setCancelable(false);
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
//            Log.e("myappStatus", ctx.getString(R.string.Background));
        } else {
            myappStatus = ctx.getString(R.string.Foreground);
//            Log.e("myappStatus", ctx.getString(R.string.Foreground));
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

    public static void addAudioSegmentEvent(Context ctx, int position, ArrayList<MainPlayModel> mainPlayModelList, String eventName, String methodName, List<String> downloadAudioDetailsList, Properties p) {
        p.putValue("audioId", mainPlayModelList.get(position).getId());
        p.putValue("audioName", mainPlayModelList.get(position).getName());
        p.putValue("audioDescription", "");
        p.putValue("directions", mainPlayModelList.get(position).getAudioDirection());
        p.putValue("masterCategory", mainPlayModelList.get(position).getAudiomastercat());
        p.putValue("subCategory", mainPlayModelList.get(position).getAudioSubCategory());
        p.putValue("audioDuration", mainPlayModelList.get(position).getAudioDuration());
        p.putValue("position", GetCurrentAudioPosition());
        if (downloadAudioDetailsList.contains(mainPlayModelList.get(position).getName())) {
            p.putValue("audioType", "Downloaded");
        } else {
            p.putValue("audioType", "Streaming");
        }
        p.putValue("source", GetSourceName(ctx));
        p.putValue("playerType", "Main");
        p.putValue("audioService", appStatus(ctx));
        p.putValue("bitRate", "");
        p.putValue("sound", String.valueOf(hundredVolume));
        addToSegment(eventName, p, methodName);
    }

    public static void addDisclaimerToSegment(String event, Context ctx, Properties p) {
        p.putValue("position", GetCurrentAudioPosition());
        p.putValue("audioType", "Streaming");
        p.putValue("source", GetSourceName(ctx));
        p.putValue("playerType", "Main");
        p.putValue("bitRate", "");
        p.putValue("audioService", appStatus(ctx));
        p.putValue("sound", String.valueOf(hundredVolume));
        addToSegment(event, p, CONSTANTS.track);
    }

    public static void callIdentify(Context ctx) {
        SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        String mainAccountId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "");
        String userId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "");
        String email = shared1.getString(CONSTANTS.PREFE_ACCESS_EMAIL, "");
        String name = shared1.getString(CONSTANTS.PREFE_ACCESS_NAME, "");
        String dob = shared1.getString(CONSTANTS.PREFE_ACCESS_DOB, "");
        String mobile = shared1.getString(CONSTANTS.PREFE_ACCESS_MOBILE, "");
        String countryCode = shared1.getString(CONSTANTS.PREFE_ACCESS_CountryCode, "");
        String indexScore = shared1.getString(CONSTANTS.PREFE_ACCESS_INDEXSCORE, "");
        String scoreLevel = shared1.getString(CONSTANTS.PREFE_ACCESS_SCORELEVEL, "");
        String sleepTime = shared1.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "");
        String image = shared1.getString(CONSTANTS.PREFE_ACCESS_IMAGE, "");
        String isProfileCompleted = shared1.getString(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED, "");
        String isAssCompleted = shared1.getString(CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED, "");
        String isAdmin = shared1.getString(CONSTANTS.PREFE_ACCESS_isMainAccount, "");
        String userCounnt = shared1.getString(CONSTANTS.PREFE_ACCESS_coUserCount, "");
        String planId = shared1.getString(CONSTANTS.PREFE_ACCESS_PlanId, "");
        String planPurchaseDate = shared1.getString(CONSTANTS.PREFE_ACCESS_PlanPurchaseDate, "");
        String planExpDate = shared1.getString(CONSTANTS.PREFE_ACCESS_PlanExpireDate, "");
        String transactionId = shared1.getString(CONSTANTS.PREFE_ACCESS_TransactionId, "");
        String trialPeriodStart = shared1.getString(CONSTANTS.PREFE_ACCESS_TrialPeriodStart, "");
        String trialperiodEnd = shared1.getString(CONSTANTS.PREFE_ACCESS_TrialPeriodEnd, "");
        String planStatus = shared1.getString(CONSTANTS.PREFE_ACCESS_PlanStatus, "");

        Gson gson = new Gson();
        String json5 = shared1.getString(CONSTANTS.PREFE_ACCESS_AreaOfFocus, gson.toString());
        String areaOfFocus = "";

        if (!json5.equalsIgnoreCase(gson.toString())) areaOfFocus = json5;

        boolean isProf = false, isAss = false, isadm = false;
        if (isProfileCompleted.equalsIgnoreCase("1")) isProf = true;
        else isProf = false;

        if (isAssCompleted.equalsIgnoreCase("1")) isAss = true;
        else isAss = false;

        if (isAdmin.equalsIgnoreCase("1")) isadm = true;
        else isadm = false;
        /*HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
        profileUpdate.put("Identity", userId);
        profileUpdate.put("Name", name);
        profileUpdate.put("Phone", "+" + countryCode + mobile);
        profileUpdate.put("Photo", image);
        profileUpdate.put("UserGroupId", mainAccountId);
        profileUpdate.put("UserId", userId);
        profileUpdate.put("Id", userId);
        profileUpdate.put("IsAdmin", isadm);
        profileUpdate.put("DeviceId", Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID));
        profileUpdate.put("DeviceType", "Android");
        profileUpdate.put("CountryCode", countryCode);
        profileUpdate.put("DOB", dob);
        profileUpdate.put("IsProfileCompleted", isProf);
        profileUpdate.put("IsAssessmentCompleted", isAss);
        profileUpdate.put("WellnessScore", indexScore);
        profileUpdate.put("ScoreLevel", scoreLevel);
        profileUpdate.put("AreaOfFocus", areaOfFocus);
        profileUpdate.put("AvgSleepTime", sleepTime);
        profileUpdate.put("Plan", planId);
        profileUpdate.put("PlanStatus", planStatus);
        profileUpdate.put("PlanStartDt", planPurchaseDate);
        profileUpdate.put("PlanExpiryDt", planExpDate);
        profileUpdate.put("MSG-push", true);
        profileUpdate.put("MSG-email", true);
        profileUpdate.put("MSG-sms", true);
        profileUpdate.put("MSG-whatsapp", true);
        profileUpdate.put("Email", email);
        profileUpdate.put("MobileNo", mobile);
        profileUpdate.put("Mobile", mobile);
        CleverTapAPI cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(ctx);
        cleverTapDefaultInstance.onUserLogin(profileUpdate);
        cleverTapDefaultInstance.pushProfile(profileUpdate);*/

        analytics.identify(new Traits().putValue("userGroupId", mainAccountId).putValue("userId", userId).putValue("mobileNo", mobile).putValue("countryCode", countryCode).putValue("id", userId).putValue("isAdmin", isadm).putValue("deviceId", Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID)).putValue("deviceType", "Android").putName(name).putEmail(email).putPhone("+" + countryCode + mobile).putValue("DOB", dob).putValue("profileImage", image).putValue("isProfileCompleted", isProf).putValue("isAssessmentCompleted", isAss).putValue("wellnessScore", indexScore).putValue("scoreLevel", scoreLevel).putValue("areaOfFocus", areaOfFocus).putValue("avgSleepTime", sleepTime).putValue("plan", planId).putValue("planStatus", planStatus).putValue("planStartDt", planPurchaseDate).putValue("planExpiryDt", planExpDate));
    }

    public static void callFCMRegMethod(Context ctx) {
        SharedPreferences sharedPreferences2 = ctx.getSharedPreferences(CONSTANTS.FCMToken, Context.MODE_PRIVATE);
        String fcmId = sharedPreferences2.getString(CONSTANTS.Token, "");
        Log.e("Token", fcmId);
        if (TextUtils.isEmpty(fcmId)) {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    return;
                }
                // Get new FCM registration token
                String token = task.getResult();
                // Log and toast
                Log.e("newToken", token);
                SharedPreferences.Editor editor = getContext().getSharedPreferences(CONSTANTS.FCMToken, Context.MODE_PRIVATE).edit();
                editor.putString(CONSTANTS.Token, token); //Friend
                editor.apply();
            });
        }
    }

    public static void addToSegment(String TagName, Properties properties, String methodName) {
        long mySpace;
        mySpace = GlobalInitExoPlayer.getSpace();
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
        SharedPreferences shared1 = getContext().getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        String mainAccountId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "");
        String userId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "");
        String isAdmin = shared1.getString(CONSTANTS.PREFE_ACCESS_isMainAccount, "");
        boolean isadm = false;
        if (!mainAccountId.isEmpty() || !mainAccountId.equalsIgnoreCase(""))
            properties.putValue("userGroupId", mainAccountId);
        if (!userId.isEmpty() || !userId.equalsIgnoreCase(""))
            properties.putValue("userId", userId);

        isadm = isAdmin.equalsIgnoreCase("1");

        SharedPreferences sharedPreferences2 = getContext().getSharedPreferences(CONSTANTS.FCMToken, Context.MODE_PRIVATE);
        String fcmId = sharedPreferences2.getString(CONSTANTS.Token, "");
        callFCMRegMethod(getContext());

        properties.putValue("isAdmin", isadm);
        properties.putValue("deviceToken", fcmId);
        properties.putValue("deviceSpace", mySpace + " MB");
        properties.putValue("batteryLevel", batLevel + " %");
        properties.putValue("batteryState", BatteryStatus);
        properties.putValue("internetDownSpeed", downSpeed + " Mbps");
        properties.putValue("internetUpSpeed", upSpeed + " Mbps");
        properties.putValue("appType", "Android");
        properties.putValue("appVersion", String.valueOf(BuildConfig.VERSION_CODE));
        properties.putValue("deviceID", Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID));
        if (analytics == null) {
            SplashActivity sp = new SplashActivity();
            SharedPreferences shared1x = getContext().getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
            String segmentKey = shared1x.getString(CONSTANTS.PREFE_ACCESS_segmentKey, "");
            if(segmentKey.equals("")){
                if(New_BASE_URL.equals(STAGING_MAIN_URL)){
                    segmentKey = getContext().getString(R.string.segment_key_real_2_staging);
                }else {
                    segmentKey = getContext().getString(R.string.segment_key_real_2_live);
                }
            }
            sp.setAnalytics(segmentKey, getContext());
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
        editor.apply();
        return key;
    }

    public static void showToast(String message, Activity context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            Toast toast = new Toast(context);
            View view = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
            TextView tvMessage = view.findViewById(R.id.tvMessage);
            tvMessage.setText(message);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 160);
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
            //            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 160);
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

    public static void hideProgressBar(ProgressBar progressBar, FrameLayout progressBarHolder, Activity act) {
        try {
            progressBarHolder.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            act.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showProgressBar(ProgressBar progressBar, FrameLayout progressBarHolder, Activity act) {
        try {
            progressBarHolder.setVisibility(View.VISIBLE);
            act.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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

    public static String securityKey() {
        String key;
        String DeviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String AES = "OsEUHhecSs4gRGcy2vMQs1s/XajBrLGADR71cKMRNtA=";
        String RSA = "KlWxBHfKPGkkeTjkT7IEo32bZW8GlVCPq/nvVFuYfIY=";
        String TDES = "1dpra0SZhVPpiUQvikMvkDxEp7qLLJL9pe9G6Apg01g=";
        String SHA1 = "Ey8rBCHsqITEbh7KQKRmYObCGBXqFnvtL5GjMFQWHQo=";
        String MD5 = "/qc2rO3RB8Z/XA+CmHY0tCaJch9a5BdlQW1xb7db+bg=";

        Calendar calendar = Calendar.getInstance();
        TimeZone tm = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTime(new Date());
        SimpleDateFormat outputFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateAsString = outputFmt.format(calendar.getTime());
        TimeZone.setDefault(tm);
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
        /*clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        HashMap<String, Object> profileUpdate = new HashMap<>();
        profileUpdate.put("MSG-push", true);
        profileUpdate.put("MSG-email", true);
        profileUpdate.put("MSG-sms", true);
        profileUpdate.put("MSG-whatsapp", true);
        clevertapDefaultInstance.pushEvent("CleverTap SDK Integrated", profileUpdate);
        FirebaseAnalytics mFirebaseAnalytics;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setUserProperty("ct_objectId", Objects.requireNonNull(CleverTapAPI.getDefaultInstance(this)).getCleverTapID());*/
        callFCMRegMethod(getContext());

    }

    public static void deleteCall(Context context) {
        String userId, coUserId;
        SharedPreferences shared1 = context.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "");
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "");
        addCouserBackStatus = 0;
        PlayerAudioId = "";
        isUserDetail = "";
        isEnhanceBack = "";
        PlayerStatus = "";
        NotificationPlaylistCheck = "";
        cancelId = "";
        deleteId = "";
        IsFirstClick = "0";
        IsRefreshPlan = "0";
        currantTime = "";
        am_pm = "";
        hourString = "";
        minuteSting = "";
        category = "";
        comeHomeScreen = "";
        IsLock = "0";
        addToRecentPlayId = "0";
        SharedPreferences preferences = context.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.remove(CONSTANTS.PREFE_ACCESS_mainAccountID);
        edit.remove(CONSTANTS.PREFE_ACCESS_UserId);
        edit.remove(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER);
        edit.remove(CONSTANTS.PREFE_ACCESS_NAME);
        edit.remove(CONSTANTS.PREFE_ACCESS_EMAIL);
        edit.remove(CONSTANTS.PREFE_ACCESS_MOBILE);
        edit.remove(CONSTANTS.PREFE_ACCESS_SLEEPTIME);
        edit.remove(CONSTANTS.PREFE_ACCESS_INDEXSCORE);
        edit.remove(CONSTANTS.PREFE_ACCESS_IMAGE);
        edit.remove(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED);
        edit.remove(CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED);
        edit.remove(CONSTANTS.PREFE_ACCESS_directLogin);
        edit.remove(CONSTANTS.PREFE_ACCESS_SCORELEVEL);
        edit.remove(CONSTANTS.PREFE_ACCESS_USEREMAIL);
        edit.remove(CONSTANTS.PREFE_ACCESS_DeviceType);
        edit.remove(CONSTANTS.PREFE_ACCESS_DeviceID);
        edit.remove(CONSTANTS.PREFE_ACCESS_isPinSet);
        edit.remove(CONSTANTS.PREFE_ACCESS_isSetLoginPin);
        edit.remove(CONSTANTS.PREFE_ACCESS_isMainAccount);
        edit.remove(CONSTANTS.PREFE_ACCESS_isEmailVerified);
        edit.remove(CONSTANTS.PREFE_ACCESS_coUserCount);
        edit.remove(CONSTANTS.PREFE_ACCESS_DOB);
        edit.remove(CONSTANTS.PREFE_ACCESS_PlanId);
        edit.remove(CONSTANTS.PREFE_ACCESS_PlanPurchaseDate);
        edit.remove(CONSTANTS.PREFE_ACCESS_PlanExpireDate);
        edit.remove(CONSTANTS.PREFE_ACCESS_TransactionId);
        edit.remove(CONSTANTS.PREFE_ACCESS_TrialPeriodStart);
        edit.remove(CONSTANTS.PREFE_ACCESS_TrialPeriodEnd);
        edit.remove(CONSTANTS.PREFE_ACCESS_PlanStatus);
        edit.remove(CONSTANTS.PREFE_ACCESS_CardId);
        edit.remove(CONSTANTS.PREFE_ACCESS_AreaOfFocus);
        edit.remove(CONSTANTS.PREFE_ACCESS_assesmentContent);
        edit.remove(CONSTANTS.PREFE_ACCESS_PlanContent);
        edit.remove(CONSTANTS.PREF_KEY_UnLockAudiList);
        edit.remove(CONSTANTS.PREFE_ACCESS_PlanDeviceType);
        edit.remove(CONSTANTS.PREF_KEY_ReminderFirstLogin);
        edit.remove(CONSTANTS.PREF_KEY_UserPromocode);
        edit.remove(CONSTANTS.PREF_KEY_ReferLink);
        edit.remove(CONSTANTS.PREFE_ACCESS_isInCouser);
        edit.remove(CONSTANTS.PREFE_ACCESS_paymentType);
        edit.remove(CONSTANTS.PREFE_ACCESS_supportTitle);
        edit.remove(CONSTANTS.PREFE_ACCESS_supportText);
        edit.remove(CONSTANTS.PREFE_ACCESS_supportEmail);
        edit.remove(CONSTANTS.PREFE_ACCESS_IsLoginFirstTime);
        edit.clear();
        edit.apply();

        SharedPreferences preferred = context.getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE);
        SharedPreferences.Editor edited = preferred.edit();
        edited.remove(CONSTANTS.selectedCategoriesTitle);
        edited.remove(CONSTANTS.selectedCategoriesName);
        edited.remove(CONSTANTS.PREFE_ACCESS_SLEEPTIME);
        edited.clear();
        edited.apply();

        SharedPreferences preferred1 = context.getSharedPreferences(CONSTANTS.AssMain, Context.MODE_PRIVATE);
        SharedPreferences.Editor edited1 = preferred1.edit();
        edited1.remove(CONSTANTS.AssQus);
        edited1.remove(CONSTANTS.AssAns);
        edited1.remove(CONSTANTS.AssSort);
        edited1.clear();
        edited1.apply();

        SharedPreferences preferreed = context.getSharedPreferences(CONSTANTS.PREF_KEY_USER_ACTIVITY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editeed = preferreed.edit();
        editeed.remove(CONSTANTS.PREF_KEY_USER_TRACK_ARRAY);
        editeed.clear();
        editeed.apply();

        SharedPreferences preferrd = context.getSharedPreferences(CONSTANTS.PREF_KEY_Splash, Context.MODE_PRIVATE);
        SharedPreferences.Editor editd = preferrd.edit();
        editd.remove(CONSTANTS.PREF_KEY_SplashKey);
        editd.clear();
        editd.apply();

      /*  SharedPreferences preferrer = context.getSharedPreferences(CONSTANTS.FCMToken, Context.MODE_PRIVATE);
        SharedPreferences.Editor editerder = preferrer.edit();
        editerder.remove(CONSTANTS.Token);
        editerder.clear();
        editerder.apply();*/

        SharedPreferences preferrerd = context.getSharedPreferences(CONSTANTS.PREF_KEY_SEGMENT_PLAYLIST, Context.MODE_PRIVATE);
        SharedPreferences.Editor editerderd = preferrerd.edit();
        editerderd.remove(CONSTANTS.PREF_KEY_PlaylistID);
        editerderd.remove(CONSTANTS.PREF_KEY_PlaylistName);
        editerderd.remove(CONSTANTS.PREF_KEY_PlaylistDescription);
        editerderd.remove(CONSTANTS.PREF_KEY_PlaylistType);
        editerderd.remove(CONSTANTS.PREF_KEY_Totalhour);
        editerderd.remove(CONSTANTS.PREF_KEY_Totalminute);
        editerderd.remove(CONSTANTS.PREF_KEY_TotalAudio);
        editerderd.remove(CONSTANTS.PREF_KEY_ScreenView);
        editerderd.clear();
        editerderd.apply();

        SharedPreferences pref = context.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editt = pref.edit();
        editt.remove(CONSTANTS.PREF_KEY_IsDisclimer);
        editt.remove(CONSTANTS.PREF_KEY_Disclimer);
        editt.remove(CONSTANTS.PREF_KEY_UnLockAudiList);
        editt.clear();
        editt.apply();

        SharedPreferences shared = context.getSharedPreferences(CONSTANTS.PREF_KEY_LOGOUT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(CONSTANTS.PREF_KEY_LOGOUT_UserID, userId);
        editor.putString(CONSTANTS.PREF_KEY_LOGOUT_CoUserID, coUserId);
        editor.apply();

        SharedPreferences sharedxc = context.getSharedPreferences(CONSTANTS.FCMToken, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorxc = sharedxc.edit();
        editorxc.remove(CONSTANTS.Token);
        editorxc.apply();

        SharedPreferences preferred2 = context.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
        SharedPreferences.Editor edited2 = preferred2.edit();
        edited2.remove(CONSTANTS.PREF_KEY_MainAudioList);
        edited2.remove(CONSTANTS.PREF_KEY_PlayerAudioList);
        edited2.remove(CONSTANTS.PREF_KEY_AudioPlayerFlag);
        edited2.remove(CONSTANTS.PREF_KEY_PlayerPlaylistId);
        edited2.remove(CONSTANTS.PREF_KEY_PlayerPlaylistName);
        edited2.remove(CONSTANTS.PREF_KEY_PlayerPosition);
        edited2.remove(CONSTANTS.PREF_KEY_Cat_Name);
        edited2.remove(CONSTANTS.PREF_KEY_PlayFrom);
        edited2.clear();
        edited2.apply();
        logout = true;
        deleteCache(context);
    }

    private static class ReminderSelectionListAdapter extends RecyclerView.Adapter<ReminderSelectionListAdapter.MyViewHolder> {
        Activity act;
        Context ctx;
        Button btnNext;
        String userId, PlaylistID, PlaylistName, RDay, Time, isSuggested, reminderId, created, timezoneName;
        Dialog dialogOld;
        FragmentActivity fragmentActivity;
        TextView timeDisplay;
        ProgressBar progressBar;
        FrameLayout progressBarHolder;
        LinearLayout llSelectTime, llOptions;
        private final ReminderSelectionModel[] selectionModels;

        public ReminderSelectionListAdapter(ReminderSelectionModel[] selectionModels, Activity act, Context ctx, Button btnNext, String userId, String PlaylistID, String PlaylistName, Dialog dialogOld, FragmentActivity fragmentActivity, TextView timeDisplay, FrameLayout progressBarHolder, ProgressBar progressBar, LinearLayout llSelectTime, String RDay, String Time, String isSuggested, String reminderId, String created, LinearLayout llOptions, String timezoneName) {
            this.selectionModels = selectionModels;
            this.act = act;
            this.ctx = ctx;
            this.btnNext = btnNext;
            this.userId = userId;
            this.PlaylistID = PlaylistID;
            this.PlaylistName = PlaylistName;
            this.dialogOld = dialogOld;
            this.fragmentActivity = fragmentActivity;
            this.timeDisplay = timeDisplay;
            this.progressBarHolder = progressBarHolder;
            this.progressBar = progressBar;
            this.llSelectTime = llSelectTime;
            this.RDay = RDay;
            this.Time = Time;
            this.isSuggested = isSuggested;
            this.reminderId = reminderId;
            this.created = created;
            this.llOptions = llOptions;
            this.timezoneName = timezoneName;
        }

        @NotNull
        @Override
        public MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
            ReminderSelectionlistLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.reminder_selectionlist_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.binding.cbChecked.setText(selectionModels[position].getDay());

            if (RDay.contains(String.valueOf(position))) {
                remiderDays.add(String.valueOf(position));
                holder.binding.cbChecked.setChecked(true);
                Typeface face = ResourcesCompat.getFont(ctx, R.font.montserrat_bold);
                holder.binding.cbChecked.setTypeface(face);
            } else {
                holder.binding.cbChecked.setChecked(false);
                Typeface face = ResourcesCompat.getFont(ctx, R.font.montserrat_medium);
                holder.binding.cbChecked.setTypeface(face);
            }

            if (position == 0) {
                Log.e("Reminder RDay", RDay);

                if (remiderDays.size() == selectionModels.length) {
                    cbCheck.setChecked(true);
                }

                if (remiderDays.size() == 0) {
                    cbCheck.setText(ctx.getString(R.string.select_all));
                } else {
                    cbCheck.setText(ctx.getString(R.string.unselect_all));
                }
            }

            holder.binding.cbChecked.setOnClickListener(v -> {
                if (holder.binding.cbChecked.isChecked()) {
                    remiderDays.add(String.valueOf(position));
                    Typeface face = ResourcesCompat.getFont(ctx, R.font.montserrat_bold);
                    holder.binding.cbChecked.setTypeface(face);
                } else {
                    remiderDays.remove(String.valueOf(position));
                    Typeface face = ResourcesCompat.getFont(ctx, R.font.montserrat_medium);
                    holder.binding.cbChecked.setTypeface(face);
                }
                RDay = "";
                RDay = TextUtils.join(",", remiderDays);
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
                        Call<SetReminderOldModel> listCall = APINewClient.getClient().getSetReminder(userId, PlaylistID, TextUtils.join(",", remiderDays), tvTime.getText().toString(), CONSTANTS.FLAG_ONE);
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
                                       /* Properties p = new Properties();
                                        p.putValue("reminderId ",reminderId);
                                        p.putValue("playlistId ", PlaylistID);
                                        p.putValue("playlistName ",PlaylistName);
                                        switch (created) {
                                            case "1":
                                                p.putValue("playlistType", "Created");
                                                break;
                                            case "0":
                                                p.putValue("playlistType", "Default");
                                                break;
                                            case "2":
                                                p.putValue("playlistType", "Suggested");
                                                break;
                                        }
                                        p.putValue("reminderStatus ","On");
                                        p.putValue("reminderTime ", tvTime.getText().toString());
                                        p.putValue("reminderDay",TextUtils.join(",", remiderDays));
                                        addToSegment("Playlist Reminder Set", p, CONSTANTS.screen);*/
                                    } else if (listModel.getResponseCode().equalsIgnoreCase(ctx.getString(R.string.ResponseCodeDeleted))) {
                                        callDelete403(act, listModel.getResponseMessage());
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

                        if (isSuggested.equalsIgnoreCase("1")) {

                        }

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

            TimePickerDialog timepickerdialog1 = new TimePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, this, Chour, Cminute, false);
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
            if (minute < 10) minuteSting = "0" + minute;
            else minuteSting = "" + minute;
            tvTime.setText(hourString + ":" + minuteSting + " " + am_pm);
        }
    }

    public static boolean isNotificationEnabled() {

        AppOpsManager mAppOps = (AppOpsManager) getContext().getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = getContext().getApplicationInfo();
        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
        String pkg = getContext().getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        Class appOpsClass = null; /* Context.APP_OPS_MANAGER */

        try {

            appOpsClass = Class.forName(AppOpsManager.class.getName());

            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);

            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
            int value = (int) opPostNotificationValue.get(Integer.class);

            return ((int) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void callObserve2(Context ctx, Activity act) {
        SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        String UserId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "");
        String CoUserID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "");
        DB.taskDao().geAllData12(CoUserID).observe((LifecycleOwner) ctx, audioList -> {
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
            editorxc.apply();
            if (fileNameList.size() != 0) {
                isDownloading = true;
                DownloadMedia downloadMedia = new DownloadMedia(ctx, act);
                downloadMedia.encrypt1(audioFile, fileNameList, playlistDownloadId/*, playlistSongs*/);
            }
        });
    }

    public static void getDownloadData(Context ctx, String PlaylistID) {
        List<String> fileNameList, fileNameList1, audioFile, playlistDownloadId;
        try {
            SharedPreferences sharedy = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String jsony = sharedy.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson));
            String json1 = sharedy.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson));
            String jsonq = sharedy.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson));
            if (!jsony.equalsIgnoreCase(String.valueOf(gson))) {
                Type type = new TypeToken<List<String>>() {
                }.getType();
                fileNameList = gson.fromJson(jsony, type);
                fileNameList1 = gson.fromJson(jsony, type);
                audioFile = gson.fromJson(json1, type);
                playlistDownloadId = gson.fromJson(jsonq, type);

                if (playlistDownloadId.size() != 0) {
                    if (playlistDownloadId.contains(PlaylistID)) {
                        Log.e("cancel", String.valueOf(playlistDownloadId.size()));
                        for (int i = 1; i < fileNameList1.size() - 1; i++) {
                            if (playlistDownloadId.get(i).equalsIgnoreCase(PlaylistID)) {
                                Log.e("cancel name id", "My id " + i + fileNameList1.get(i));
                                fileNameList.remove(i);
                                audioFile.remove(i);
                                playlistDownloadId.remove(i);
                                Log.e("cancel id", "My id " + playlistDownloadId.size() + i);
                            }
                        }

                        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = shared.edit();
                        String nameJson = gson.toJson(fileNameList);
                        String urlJson = gson.toJson(audioFile);
                        String playlistIdJson = gson.toJson(playlistDownloadId);
                        editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson);
                        editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson);
                        editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
                        editor.commit();
                        if (playlistDownloadId.get(0).equalsIgnoreCase(PlaylistID)) {
                            PRDownloader.cancel(downloadIdOne);
                            filename = "";
                        }
                    }
                }
            }
        } catch (Exception e) {
            //            getDownloadData();
            e.printStackTrace();
            Log.e("Download Playlist ", "Download Playlist remove issue" + e.getMessage());
        }
    }

    public static void GetPlaylistMedia(String PlaylistID, String CoUserID, Context ctx) {
        DB = getAudioDataBase(ctx);
        DB.taskDao().getAllAudioByPlaylist1(PlaylistID, CoUserID).observe((LifecycleOwner) ctx, audioList -> {
            deleteDownloadFile(PlaylistID, CoUserID);
            if (audioList.size() != 0) {
                GetSingleMedia(audioList.get(0).getAudioFile(), ctx, audioList, 0, CoUserID);
            }
        });
    }

    public static void deleteDownloadFile(String PlaylistId, String CoUserID) {
        AudioDatabase.databaseWriteExecutor.execute(() -> DB.taskDao().deleteByPlaylistId(PlaylistId, CoUserID));
        deletePlaylist(PlaylistId, CoUserID);
    }

    public static void GetSingleMedia(String AudioFile, Context ctx, List<DownloadAudioDetails> audioList, int i, String CoUserID) {
        DB.taskDao().getLastIdByuId1(AudioFile, CoUserID).observe((LifecycleOwner) ctx, audioList1 -> {
            try {
                if (audioList1.size() != 0) {
                    if (audioList1.size() == 1) {
                        FileUtils.deleteDownloadedFile(ctx, audioList1.get(0).getName());
                    }
                }

                if (i < audioList.size() - 1) {
                    GetSingleMedia(audioList.get(i + 1).getAudioFile(), ctx, audioList, i + 1, CoUserID);
                    Log.e("DownloadMedia Call", String.valueOf(i + 1));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    public static void deletePlaylist(String playlistId, String CoUserID) {
        AudioDatabase.databaseWriteExecutor.execute(() -> DB.taskDao().deletePlaylist(playlistId, CoUserID));
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