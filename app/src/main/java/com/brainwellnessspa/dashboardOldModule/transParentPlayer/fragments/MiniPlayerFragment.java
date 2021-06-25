package com.brainwellnessspa.dashboardOldModule.transParentPlayer.fragments;

import android.app.Activity;
import android.app.Application;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.dashboardOldModule.models.AppointmentDetailModel;
import com.brainwellnessspa.dashboardModule.models.AudioInterruptionModel;
import com.brainwellnessspa.dashboardModule.models.MainAudioModel;
import com.brainwellnessspa.dashboardModule.models.SearchBothModel;
import com.brainwellnessspa.dashboardModule.models.SubPlayListModel;
import com.brainwellnessspa.dashboardModule.models.SucessModel;
import com.brainwellnessspa.dashboardModule.models.SuggestedModel;
import com.brainwellnessspa.dashboardModule.models.ViewAllAudioListModel;
import com.brainwellnessspa.dashboardOldModule.transParentPlayer.models.MainPlayModel;
import com.brainwellnessspa.R;

import com.brainwellnessspa.roomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.services.GlobalInitExoPlayer;
import com.brainwellnessspa.utility.APIClient;
import com.brainwellnessspa.utility.CONSTANTS;
import com.brainwellnessspa.databinding.FragmentMiniExoCustomBinding;
import com.brainwellnessspa.databinding.FragmentMiniPlayerBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.util.Assertions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.segment.analytics.Properties;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.BATTERY_SERVICE;
import static com.brainwellnessspa.BWSApplication.BatteryStatus;
import static com.brainwellnessspa.BWSApplication.isDisclaimer;
import static com.brainwellnessspa.BWSApplication.AudioInterrupted;
import static com.brainwellnessspa.BWSApplication.PlayerStatus;
import static com.brainwellnessspa.BWSApplication.addToRecentPlayId;
import static com.brainwellnessspa.BWSApplication.DB;
import static com.brainwellnessspa.BWSApplication.getAudioDataBase;
import static com.brainwellnessspa.BWSApplication.oldSongPos;
import static com.brainwellnessspa.BWSApplication.audioClick;
import static com.brainwellnessspa.BWSApplication.miniPlayer;
import static com.brainwellnessspa.downloadModule.fragments.AudioDownloadsFragment.comefromDownload;
import static com.brainwellnessspa.BWSApplication.appStatus;
import static com.brainwellnessspa.services.GlobalInitExoPlayer.GetCurrentAudioPosition;
import static com.brainwellnessspa.services.GlobalInitExoPlayer.GetSourceName;
import static com.brainwellnessspa.BWSApplication.PlayerINIT;
import static com.brainwellnessspa.services.GlobalInitExoPlayer.callNewPlayerRelease;
import static com.brainwellnessspa.services.GlobalInitExoPlayer.getMediaBitmap;
import static com.brainwellnessspa.BWSApplication.player;

public class MiniPlayerFragment extends Fragment {
    public String  myAudioId = "";
    public int SegmentTagPlayer = 0;
    public AudioManager audioManager;
    public int hundredVolume = 0, currentVolume = 0, maxVolume = 0, percent;
    FragmentMiniPlayerBinding binding;
    FragmentMiniExoCustomBinding exoBinding;
    Context ctx;
    Activity activity;
    Intent localIntent1;
    LocalBroadcastManager localBroadcastManager1;
    View view;
    List<String> downloadAudioDetailsList;
    List<String> downloadAudioDetailsListGloble;
    List<File> filesDownloaded;
    ArrayList<MainPlayModel> mainPlayModelList, mainPlayModelList2;
    String IsRepeat = "", IsShuffle = "", UserID, AudioFlag = "", id = "", url = "", playFrom = "";
    int position, listSize;
    Boolean queuePlay, audioPlay;
    LocalBroadcastManager localBroadcastManager;
    Intent localIntent;
    PlayerControlView playerControlView;
    int counterinit = 0;
    Properties p;
    Handler handler1;
    private final BroadcastReceiver listener1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MakeArray2();
        }
    };
    Runnable UpdateSongTime1 = new Runnable() {
        @Override
        public void run() {
            handler1.removeCallbacks(UpdateSongTime1);
            if (counterinit <= 3) {
                initializePlayer();
            }
        }
    };
    private long mLastClickTime = 0;
    private final BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    @Override
    public void onPause() {
//        player.removeListener(player.);
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mini_player, container, false);
        view = binding.getRoot();
        ctx = getActivity();
        activity = getActivity();
        DB = getAudioDataBase(ctx);
        PlayerStatus = "Mini";
        mainPlayModelList = new ArrayList<>();
        mainPlayModelList2 = new ArrayList<>();
        downloadAudioDetailsList = new ArrayList<>();
        downloadAudioDetailsListGloble = new ArrayList<>();
        filesDownloaded = new ArrayList<>();
        exoBinding = DataBindingUtil.inflate(LayoutInflater.from(ctx)
                , R.layout.fragment_mini_exo_custom, binding.playerControlView, false);
        handler1 = new Handler();
//        handler2 = new Handler();
        playerControlView = Assertions.checkNotNull(this.binding.playerControlView);
        localIntent = new Intent("play_pause_Action");
        localBroadcastManager = LocalBroadcastManager.getInstance(ctx);
        if (audioClick) {
//            audioClick = false;
            exoBinding.llPlay.setVisibility(View.GONE);
            exoBinding.llPause.setVisibility(View.GONE);
            exoBinding.progressBar.setVisibility(View.VISIBLE);
            MakeArray2();
            GetAllMedia();
//            MakeArray();
        } else {
            GetAllMedia1();
            MakeArray2();
        }

        SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences Status = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_Status, Context.MODE_PRIVATE);
        IsRepeat = Status.getString(CONSTANTS.PREF_KEY_IsRepeat, "");
        IsShuffle = Status.getString(CONSTANTS.PREF_KEY_IsShuffle, "");
        binding.playerControlView.addView(exoBinding.getRoot());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 130);
        binding.llLayout.setLayoutParams(params);
        audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        percent = 100;
        hundredVolume = (int) (currentVolume * percent) / maxVolume;
        if (comefromDownload.equalsIgnoreCase("1")) {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            param.setMargins(0, 0, 0, 0);
            binding.llLayout.setLayoutParams(param);
        } else {
            LinearLayout.LayoutParams paramm = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramm.setMargins(0, 0, 0, 130);
            binding.llLayout.setLayoutParams(paramm);
        }
        exoBinding.llPlayearMain.setOnClickListener(view -> {
            SegmentTagPlayer = 1;
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (miniPlayer == 0) {
                miniPlayer = 1;
                audioClick = true;
            } else {
                audioClick = false;
            }
//            if (exoBinding.progressBar.getVisibility() == View.VISIBLE) {
//                isprogressbar = true;
//            }
            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String json = gson.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, json);
            editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
            editor.commit();
        });

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            getActivity().registerActivityLifecycleCallbacks(new AppLifecycleCallback());
//        }

        return view;
    }

    @Override
    public void onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getActivity().registerActivityLifecycleCallbacks(new AppLifecycleCallback());
        }

        super.onResume();
    }

    private void addToRecentPlay() {
        if (BWSApplication.isNetworkConnected(ctx)) {
//            BWSApplication.showProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
            Call<SucessModel> listCall = APIClient.getClient().getRecentlyplayed(id, UserID);
            listCall.enqueue(new Callback<SucessModel>() {
                @Override
                public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                    try {
//                        BWSApplication.hideProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
                        SucessModel model = response.body();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<SucessModel> call, Throwable t) {
//                    BWSApplication.hideProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
//            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    private void initializePlayer() {
        try {
//        player = new SimpleExoPlayer.Builder(ctx.getApplicationContext()).build();
            isDisclaimer = 0;
            callAllDisable(true);
            if (audioClick) {
                GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
                globalInitExoPlayer.GlobleInItPlayer(ctx, position, downloadAudioDetailsList, mainPlayModelList, "Mini");
                setPlayerCtrView();
            }
        } catch (Exception e) {
            Log.e("mini", e.getMessage());
        }
        try {
            if (player != null) {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                player.setWakeMode(C.WAKE_MODE_NONE);
                player.setHandleWakeLock(true);
//                if(player.getDeviceVolume() > 4) {
//                if(player.getDeviceVolume() != 2){
//                    player.setDeviceVolume(2);
//                }
//                player.setVolume(1f);
                player.setHandleAudioBecomingNoisy(true);
                player.addListener(new ExoPlayer.EventListener() {
                    @Override
                    public void onPlayerError(ExoPlaybackException error) {
                        String intruptMethod = "";
                        p = new Properties();
                        p.putValue("userId", UserID);
                        p.putValue("audioId", mainPlayModelList.get(position).getId());
                        p.putValue("audioName", mainPlayModelList.get(position).getName());
                        p.putValue("audioDescription", "");
                        p.putValue("directions", mainPlayModelList.get(position).getAudioDirection());
                        p.putValue("masterCategory", mainPlayModelList.get(position).getAudiomastercat());
                        p.putValue("subCategory", mainPlayModelList.get(position).getAudioSubCategory());
                        p.putValue("audioDuration", mainPlayModelList.get(position).getAudioDuration());
                        p.putValue("position", GetCurrentAudioPosition());
                        String AudioType = "";
                        if (downloadAudioDetailsList.contains(mainPlayModelList.get(position).getName())) {
                            p.putValue("audioType", "Downloaded");
                            AudioType = "Downloaded";
                        } else {
                            p.putValue("audioType", "Streaming");
                            AudioType = "Streaming";
                        }
                        p.putValue("source", GetSourceName(ctx));
                        p.putValue("playerType", "Mini");
                        p.putValue("audioService", appStatus(getActivity()));
                        p.putValue("bitRate", "");
                        p.putValue("sound", String.valueOf(hundredVolume));
                        if (error.type == ExoPlaybackException.TYPE_SOURCE) {
                            p.putValue("interruptionMethod", error.getMessage() + " " + error.getSourceException().getMessage());
                            intruptMethod = error.getMessage() + " " + error.getSourceException().getMessage();
                            Log.e("onPlaybackError", error.getMessage() + " " + error.getSourceException().getMessage());
                        } else if (error.type == ExoPlaybackException.TYPE_RENDERER) {
                            p.putValue("interruptionMethod", error.getMessage() + " " + error.getRendererException().getMessage());
                            intruptMethod = error.getMessage() + " " + error.getRendererException().getMessage();
                            Log.e("onPlaybackError", error.getMessage() + " " + error.getRendererException().getMessage());
                        } else if (error.type == ExoPlaybackException.TYPE_UNEXPECTED) {
                            p.putValue("interruptionMethod", error.getMessage() + " " + error.getUnexpectedException().getMessage());
                            intruptMethod = error.getMessage() + " " + error.getUnexpectedException().getMessage();
                            Log.e("onPlaybackError", error.getMessage() + " " + error.getUnexpectedException().getMessage());
                        } else if (error.type == ExoPlaybackException.TYPE_REMOTE) {
                            p.putValue("interruptionMethod", error.getMessage());
                            intruptMethod = error.getMessage();
                            Log.e("onPlaybackError", error.getMessage());
                        } else {
                            p.putValue("interruptionMethod", error.getMessage());
                            intruptMethod = error.getMessage();
                            Log.e("onPlaybackError", error.getMessage());
                        }
                        AudioInterrupted = true;
                        BWSApplication.addToSegment("Audio Interrupted", p, CONSTANTS.track);
                        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                        //should check null because in airplane mode it will be null
                        NetworkCapabilities nc;
                        float downSpeed = 0;
                        int batLevel = 0;
                        float upSpeed = 0;
                        if (BWSApplication.isNetworkConnected(getActivity())) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
                                downSpeed = (float) nc.getLinkDownstreamBandwidthKbps() / 1000;
                                upSpeed = (float) (nc.getLinkUpstreamBandwidthKbps() / 1000);
                            }
                        }
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            BatteryManager bm = (BatteryManager) getActivity().getSystemService(BATTERY_SERVICE);
                            batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

                        }
                        if (BWSApplication.isNetworkConnected(ctx)) {
                            Call<AudioInterruptionModel> listCall = APIClient.getClient().getAudioInterruption(UserID,
                                    mainPlayModelList.get(position).getId(), mainPlayModelList.get(position).getName(),
                                    "", mainPlayModelList.get(position).getAudioDirection()
                                    , mainPlayModelList.get(position).getAudiomastercat(),
                                    mainPlayModelList.get(position).getAudioSubCategory(),
                                    mainPlayModelList.get(position).getAudioDuration()
                                    , "", AudioType, "Mini", String.valueOf(hundredVolume)
                                    , appStatus(getActivity()), GetSourceName(ctx), GetCurrentAudioPosition(), "",
                                    intruptMethod, batLevel, BatteryStatus, downSpeed, upSpeed);
                            listCall.enqueue(new Callback<AudioInterruptionModel>() {
                                @Override
                                public void onResponse(Call<AudioInterruptionModel> call, Response<AudioInterruptionModel> response) {
                                    AudioInterruptionModel listModel = response.body();

                                }

                                @Override
                                public void onFailure(Call<AudioInterruptionModel> call, Throwable t) {
                                }
                            });

                        } else {
                        }
                    }

                    @Override
                    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                        Log.v("TAG", "Listener-onTracksChanged... MINI PLAYER");

                        oldSongPos = 0;
                        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                        Gson gson = new Gson();
                        String json = shared.getString(CONSTANTS.PREF_KEY_PlayerAudioList, String.valueOf(gson));
                        if (!json.equalsIgnoreCase(String.valueOf(gson))) {
                            Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                            }.getType();
                            mainPlayModelList = gson.fromJson(json, type);
                        }
                        if (player != null) {
                            player.setPlayWhenReady(true);
                        }
                        position = player.getCurrentWindowIndex();
                        GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
                        globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList);

//                        myBitmap = getMediaBitmap(getActivity(), mainPlayModelList.get(player.getCurrentWindowIndex()).getImageFile());
                        myAudioId = mainPlayModelList.get(player.getCurrentWindowIndex()).getId();
                        SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedz.edit();
                        editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
                        editor.commit();
                        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            Log.e("Nite Mode :", String.valueOf(AppCompatDelegate.getDefaultNightMode()));
                        }
                        UiModeManager uiModeManager = (UiModeManager) ctx.getSystemService(Context.UI_MODE_SERVICE);
                        if (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_AUTO
                                || uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES
                                || uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_CUSTOM) {
                            uiModeManager.setNightMode(UiModeManager.MODE_NIGHT_NO);

                            Log.e("Nite Mode :", String.valueOf(uiModeManager.getNightMode()));
                        }
                        localIntent.putExtra("MyData", "play");
                        localBroadcastManager.sendBroadcast(localIntent);
                        callButtonText(player.getCurrentWindowIndex());

                        if (SegmentTagPlayer == 0) {
                            p = new Properties();
                            p.putValue("userId", UserID);
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
                            p.putValue("playerType", "Mini");
                            p.putValue("audioService", appStatus(getActivity()));
                            p.putValue("bitRate", "");
                            p.putValue("sound", String.valueOf(hundredVolume));
                            BWSApplication.addToSegment("Audio Started", p, CONSTANTS.track);
                        }
                    }

                    @Override
                    public void onIsPlayingChanged(boolean isPlaying) {
//                        myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(player.getCurrentWindowIndex()).getImageFile());
                        if (player.getPlaybackState() == ExoPlayer.STATE_BUFFERING) {
                            exoBinding.llPlay.setVisibility(View.GONE);
                            exoBinding.llPause.setVisibility(View.GONE);
                            exoBinding.progressBar.setVisibility(View.VISIBLE);
                        } else if (isPlaying) {
                            exoBinding.llPlay.setVisibility(View.GONE);
                            exoBinding.llPause.setVisibility(View.VISIBLE);
                            exoBinding.progressBar.setVisibility(View.GONE);
                            localIntent.putExtra("MyData", "play");
                            localBroadcastManager.sendBroadcast(localIntent);
                        } else if (!isPlaying) {
                            exoBinding.llPlay.setVisibility(View.VISIBLE);
                            exoBinding.llPause.setVisibility(View.GONE);
                            exoBinding.progressBar.setVisibility(View.GONE);
                            localIntent.putExtra("MyData", "pause");
                            localBroadcastManager.sendBroadcast(localIntent);
                        }
//                    isprogressbar = false;
                        exoBinding.exoProgress.setBufferedPosition(player.getBufferedPosition());
                        exoBinding.exoProgress.setPosition(player.getCurrentPosition());
                        exoBinding.exoProgress.setDuration(player.getDuration());
                        if ((player.getCurrentPosition() >= oldSongPos + 299500) && (player.getCurrentPosition() <= oldSongPos + 310000)) {
                            oldSongPos = player.getCurrentPosition();
                            callHeartbeat();
                        }
                    }

                    @Override
                    public void onPlaybackStateChanged(int state) {
                        if (state == ExoPlayer.STATE_READY) {
                            p = new Properties();
                            p.putValue("userId", UserID);
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
                            p.putValue("playerType", "Mini");
                            p.putValue("audioService", appStatus(getActivity()));
                            p.putValue("bitRate", "");
                            p.putValue("sound", String.valueOf(hundredVolume));
                            BWSApplication.addToSegment("Audio Buffer Completed", p, CONSTANTS.track);
                            if (player.getPlayWhenReady()) {
                                exoBinding.llPlay.setVisibility(View.GONE);
                                exoBinding.llPause.setVisibility(View.VISIBLE);
                                exoBinding.progressBar.setVisibility(View.GONE);
                                localIntent.putExtra("MyData", "play");
                                localBroadcastManager.sendBroadcast(localIntent);
                                callHeartbeat();
                            } else if (!player.getPlayWhenReady()) {
                                exoBinding.llPlay.setVisibility(View.VISIBLE);
                                exoBinding.llPause.setVisibility(View.GONE);
                                exoBinding.progressBar.setVisibility(View.GONE);
                                localIntent.putExtra("MyData", "pause");
                                localBroadcastManager.sendBroadcast(localIntent);
                            }

//                        isprogressbar = false;
                        } else if (state == ExoPlayer.STATE_BUFFERING) {
                            exoBinding.llPlay.setVisibility(View.GONE);
                            exoBinding.llPause.setVisibility(View.GONE);
                            exoBinding.progressBar.setVisibility(View.VISIBLE);
                            p = new Properties();
                            p.putValue("userId", UserID);
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
                            p.putValue("playerType", "Mini");
                            p.putValue("audioService", appStatus(getActivity()));
                            p.putValue("bitRate", "");
                            p.putValue("sound", String.valueOf(hundredVolume));
                            BWSApplication.addToSegment("Audio Buffer Started", p, CONSTANTS.track);
                        } else if (state == ExoPlayer.STATE_ENDED) {
                            try {
                                p = new Properties();
                                p.putValue("userId", UserID);
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
                                p.putValue("playerType", "Mini");
                                p.putValue("audioService", appStatus(getActivity()));
                                p.putValue("bitRate", "");
                                p.putValue("sound", String.valueOf(hundredVolume));
                                BWSApplication.addToSegment("Audio Completed", p, CONSTANTS.track);
                                if (mainPlayModelList.get(player.getCurrentWindowIndex()).getId().
                                        equalsIgnoreCase(mainPlayModelList.get(mainPlayModelList.size() - 1).getId())) {
                                    exoBinding.llPlay.setVisibility(View.VISIBLE);
                                    exoBinding.llPause.setVisibility(View.GONE);
                                    exoBinding.progressBar.setVisibility(View.GONE);
                                    player.setPlayWhenReady(false);
                                    localIntent.putExtra("MyData", "pause");
                                    localBroadcastManager.sendBroadcast(localIntent);
                                    p = new Properties();
                                    p.putValue("userId", UserID);
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
                                    p.putValue("playerType", "Mini");
                                    p.putValue("audioService", appStatus(getActivity()));
                                    p.putValue("bitRate", "");
                                    p.putValue("sound", String.valueOf(hundredVolume));
                                    String source = GetSourceName(ctx);
                                    if (!source.equalsIgnoreCase("Playlist") && !source.equalsIgnoreCase("Downloaded Playlists")) {
                                        BWSApplication.addToSegment("Audio Playback Completed", p, CONSTANTS.track);
                                    }
                                    if (audioPlay && (AudioFlag.equalsIgnoreCase("SubPlayList") || AudioFlag.equalsIgnoreCase("Downloadlist"))) {
                                        SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_SEGMENT_PLAYLIST, Context.MODE_PRIVATE);
                                        String PlaylistID = (shared1.getString(CONSTANTS.PREF_KEY_PlaylistID, ""));
                                        String PlaylistName = (shared1.getString(CONSTANTS.PREF_KEY_PlaylistName, ""));
                                        String PlaylistDescription = (shared1.getString(CONSTANTS.PREF_KEY_PlaylistDescription, ""));
                                        String PlaylistType = (shared1.getString(CONSTANTS.PREF_KEY_PlaylistType, ""));
                                        String Totalhour = (shared1.getString(CONSTANTS.PREF_KEY_Totalhour, ""));
                                        String Totalminute = (shared1.getString(CONSTANTS.PREF_KEY_Totalminute, ""));
                                        String TotalAudio = (shared1.getString(CONSTANTS.PREF_KEY_TotalAudio, ""));
                                        String ScreenView = (shared1.getString(CONSTANTS.PREF_KEY_ScreenView, ""));

                                        p = new Properties();
                                        p.putValue("userId", UserID);
                                        p.putValue("playlistId", PlaylistID);
                                        p.putValue("playlistName", PlaylistName);
                                        p.putValue("playlistDescription", PlaylistDescription);
                                        if (PlaylistType.equalsIgnoreCase("1")) {
                                            p.putValue("playlistType", "Created");
                                        } else if (PlaylistType.equalsIgnoreCase("0")) {
                                            p.putValue("playlistType", "Default");
                                        }
                                        if (Totalhour.equalsIgnoreCase("")) {
                                            p.putValue("playlistDuration", "0h " + Totalminute + "m");
                                        } else if (Totalminute.equalsIgnoreCase("")) {
                                            p.putValue("playlistDuration", Totalhour + "h 0m");
                                        } else {
                                            p.putValue("playlistDuration", Totalhour + "h " + Totalminute + "m");
                                        }
                                        p.putValue("audioCount", TotalAudio);
                                        p.putValue("source", ScreenView);
                                        p.putValue("playerType", "Mini");
                                        p.putValue("audioService", appStatus(getActivity()));
                                        p.putValue("sound", String.valueOf(hundredVolume));
                                        BWSApplication.addToSegment("Playlist Completed", p, CONSTANTS.track);

                                        Log.e("Last audio End", mainPlayModelList.get(position).getName());
                                    } else {
                                        Log.e("Curr audio End", mainPlayModelList.get(position).getName());
                                    }
                        /*new Handler().postDelayed(() -> {
                            playerNotificationManager.setPlayer(null);
                        }, 2 * 1000);*/
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("End State: ", e.getMessage());
                            }
                        } else if (state == ExoPlayer.STATE_IDLE) {
                            if (AudioInterrupted) {
                                Log.e("Exo Player state", "ExoPlayer.STATE_IDLE");
                            }
                        }

                    }
                });
                setPlayerCtrView();
                callRepeatShuffle();
            }
        } catch (Exception e) {
            Log.e("mini", e.getMessage());
        }
        try {
            if (miniPlayer == 0) {
                if (audioClick) {
                    exoBinding.progressBar.setVisibility(View.VISIBLE);
                    exoBinding.llPlay.setVisibility(View.GONE);
                    exoBinding.llPause.setVisibility(View.GONE);
                }
                localIntent.putExtra("MyData", "pause");
                localBroadcastManager.sendBroadcast(localIntent);
            } else {
                if (player != null) {
                    if (player.getPlaybackState() == ExoPlayer.STATE_BUFFERING) {
                        exoBinding.llPlay.setVisibility(View.GONE);
                        exoBinding.llPause.setVisibility(View.GONE);
                        exoBinding.progressBar.setVisibility(View.VISIBLE);
                    } else if (player.getPlayWhenReady()) {
                        exoBinding.llPlay.setVisibility(View.GONE);
                        exoBinding.llPause.setVisibility(View.VISIBLE);
                        exoBinding.progressBar.setVisibility(View.GONE);
                    } else if (!player.getPlayWhenReady()) {
                        exoBinding.llPlay.setVisibility(View.VISIBLE);
                        exoBinding.llPause.setVisibility(View.GONE);
                        exoBinding.progressBar.setVisibility(View.GONE);
                    }
                    GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
                    globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList);
                    exoBinding.exoProgress.setBufferedPosition(player.getBufferedPosition());
                    exoBinding.exoProgress.setPosition(player.getCurrentPosition());
                    exoBinding.exoProgress.setDuration(player.getDuration());
                    counterinit = 0;
                    handler1.removeCallbacks(UpdateSongTime1);
                } else {
                    if (audioClick) {
                        exoBinding.progressBar.setVisibility(View.GONE);
                        exoBinding.llPlay.setVisibility(View.GONE);
                        exoBinding.llPause.setVisibility(View.VISIBLE);
                    } else if (PlayerINIT) {
                        exoBinding.progressBar.setVisibility(View.GONE);
                        exoBinding.llPlay.setVisibility(View.GONE);
                        exoBinding.llPause.setVisibility(View.VISIBLE);
                    }
                    handler1.postDelayed(UpdateSongTime1, 2000);
                }/*else if (isprogressbar) {
                exoBinding.llPlay.setVisibility(View.GONE);
                exoBinding.llPause.setVisibility(View.GONE);
                exoBinding.progressBar.setVisibility(View.VISIBLE);
            }*/
            }
            epAllClicks();
        } catch (Exception e) {
            e.printStackTrace();
//            Log.e("init media State: ", e.getMessage());
        }
    }

    private void callHeartbeat() {
        if (SegmentTagPlayer == 0) {
            p = new Properties();
            p.putValue("userId", UserID);
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
            p.putValue("playerType", "Mini");
            p.putValue("audioService", appStatus(getActivity()));
            p.putValue("bitRate", "");
            p.putValue("sound", String.valueOf(hundredVolume));
            BWSApplication.addToSegment("Audio Playing", p, CONSTANTS.track);
        }
    }

    private void epAllClicks() {
        try {
            exoBinding.llPause.setOnClickListener(view -> {
                if (player != null) {
                    player.setPlayWhenReady(false);
                    exoBinding.llPlay.setVisibility(View.VISIBLE);
                    exoBinding.llPause.setVisibility(View.GONE);
                    exoBinding.progressBar.setVisibility(View.GONE);
                    localIntent.putExtra("MyData", "pause");
                    localBroadcastManager.sendBroadcast(localIntent);
                    p = new Properties();
                    p.putValue("userId", UserID);
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
                    p.putValue("playerType", "Mini");
                    p.putValue("audioService", appStatus(getActivity()));
                    p.putValue("bitRate", "");
                    p.putValue("sound", String.valueOf(hundredVolume));
                    BWSApplication.addToSegment("Audio Paused", p, CONSTANTS.track);
                } else {
                    audioClick = true;
                    miniPlayer = 1;
                    GetAllMedia();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ep all click State: ", e.getMessage());
        }
    }

    private void initializePlayerDisclaimer() {
        try {
//        player = new SimpleExoPlayer.Builder(ctx.getApplicationContext()).build();
            if (audioClick) {
                GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
                globalInitExoPlayer.GlobleInItDisclaimer(ctx, mainPlayModelList,position);
                setPlayerCtrView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            exoBinding.exoProgress.setClickable(false);
            exoBinding.exoProgress.setEnabled(false);
            if (player != null) {
                player.addListener(new ExoPlayer.EventListener() {
                    @Override
                    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                        Log.v("TAG", "Listener-onTracksChanged... Disclaimer ");
                    }

                    @Override
                    public void onPlaybackStateChanged(int state) {
                        if (state == ExoPlayer.STATE_ENDED) {
                            //player back ended
                            p = new Properties();
                            p.putValue("userId", UserID);
                            p.putValue("position", GetCurrentAudioPosition());
                            p.putValue("source", GetSourceName(ctx));
                            p.putValue("playerType", "Mini");
                            if (downloadAudioDetailsList.contains(mainPlayModelList.get(position).getName())) {
                                p.putValue("audioType", "Downloaded");
                            } else {
                                p.putValue("audioType", "Streaming");
                            }
                            p.putValue("bitRate", "");
                            p.putValue("audioService", appStatus(getActivity()));
                            p.putValue("sound", String.valueOf(hundredVolume));
                            BWSApplication.addToSegment("Disclaimer Completed", p, CONSTANTS.track);
                            audioClick = true;
                            isDisclaimer = 0;
                            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared.edit();
                            editor.putString(CONSTANTS.PREF_KEY_IsDisclimer, "0");
                            editor.commit();
                            removeArray();
                            localBroadcastManager1.sendBroadcast(localIntent1);
                            Log.e("send brod cast", "desc");
                        } else if (state == ExoPlayer.STATE_READY) {
                            p = new Properties();
                            p.putValue("userId", UserID);
                            p.putValue("position", GetCurrentAudioPosition());
                            p.putValue("source", GetSourceName(ctx));
                            p.putValue("playerType", "Mini");
                            if (downloadAudioDetailsList.contains(mainPlayModelList.get(position).getName())) {
                                p.putValue("audioType", "Downloaded");
                            } else {
                                p.putValue("audioType", "Streaming");
                            }
                            p.putValue("bitRate", "");
                            p.putValue("audioService", appStatus(getActivity()));
                            p.putValue("sound", String.valueOf(hundredVolume));
                            BWSApplication.addToSegment("Disclaimer Started", p, CONSTANTS.track);
                            if (player.getPlayWhenReady()) {
                                exoBinding.llPlay.setVisibility(View.GONE);
                                exoBinding.llPause.setVisibility(View.VISIBLE);
                                exoBinding.progressBar.setVisibility(View.GONE);
                                localIntent.putExtra("MyData", "play");
                                localBroadcastManager.sendBroadcast(localIntent);
                                p = new Properties();
                                p.putValue("userId", UserID);
                                p.putValue("position", GetCurrentAudioPosition());
                                p.putValue("source", GetSourceName(ctx));
                                p.putValue("playerType", "Mini");
                                if (downloadAudioDetailsList.contains(mainPlayModelList.get(position).getName())) {
                                    p.putValue("audioType", "Downloaded");
                                } else {
                                    p.putValue("audioType", "Streaming");
                                }
                                p.putValue("bitRate", "");
                                p.putValue("audioService", appStatus(getActivity()));
                                p.putValue("sound", String.valueOf(hundredVolume));
                                BWSApplication.addToSegment("Disclaimer Playing", p, CONSTANTS.track);
                            } else if (!player.getPlayWhenReady()) {
                                exoBinding.llPlay.setVisibility(View.VISIBLE);
                                exoBinding.llPause.setVisibility(View.GONE);
                                exoBinding.progressBar.setVisibility(View.GONE);
                                localIntent.putExtra("MyData", "pause");
                                localBroadcastManager.sendBroadcast(localIntent);
                            }

//                        isprogressbar = false;
                        } else if (state == ExoPlayer.STATE_BUFFERING) {
                            exoBinding.llPlay.setVisibility(View.GONE);
                            exoBinding.llPause.setVisibility(View.GONE);
                            exoBinding.progressBar.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onIsPlayingChanged(boolean isPlaying) {

                        if (player != null) {
//                            myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(player.getCurrentWindowIndex()).getImageFile());
                            if (player.getPlaybackState() == ExoPlayer.STATE_BUFFERING) {
                                exoBinding.llPlay.setVisibility(View.GONE);
                                exoBinding.llPause.setVisibility(View.GONE);
                                exoBinding.progressBar.setVisibility(View.VISIBLE);
                            } else if (isPlaying) {
                                exoBinding.llPlay.setVisibility(View.GONE);
                                exoBinding.llPause.setVisibility(View.VISIBLE);
                                exoBinding.progressBar.setVisibility(View.GONE);
                                localIntent.putExtra("MyData", "play");
                                localBroadcastManager.sendBroadcast(localIntent);
                            } else if (!isPlaying) {
                                exoBinding.llPlay.setVisibility(View.VISIBLE);
                                exoBinding.llPause.setVisibility(View.GONE);
                                exoBinding.progressBar.setVisibility(View.GONE);
                                localIntent.putExtra("MyData", "pause");
                                localBroadcastManager.sendBroadcast(localIntent);
                            }
//                    isprogressbar = false;
                            exoBinding.exoProgress.setBufferedPosition(player.getBufferedPosition());
                            exoBinding.exoProgress.setPosition(player.getCurrentPosition());
                            exoBinding.exoProgress.setDuration(player.getDuration());
                        }
                    }

                });
                setPlayerCtrView();
            }
            exoBinding.llPause.setOnClickListener(view -> {
                player.setPlayWhenReady(false);
                exoBinding.llPlay.setVisibility(View.VISIBLE);
                exoBinding.llPause.setVisibility(View.GONE);
                exoBinding.progressBar.setVisibility(View.GONE);
                localIntent.putExtra("MyData", "pause");
                localBroadcastManager.sendBroadcast(localIntent);
                p = new Properties();
                p.putValue("userId", UserID);
                p.putValue("position", GetCurrentAudioPosition());
                p.putValue("source", GetSourceName(ctx));
                p.putValue("playerType", "Mini");
                if (downloadAudioDetailsList.contains(mainPlayModelList.get(position).getName())) {
                    p.putValue("audioType", "Downloaded");
                } else {
                    p.putValue("audioType", "Streaming");
                }
                p.putValue("bitRate", "");
                p.putValue("sound", String.valueOf(hundredVolume));
                p.putValue("audioService", appStatus(getActivity()));
                BWSApplication.addToSegment("Disclaimer Paused", p, CONSTANTS.track);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (miniPlayer == 0) {
                if (audioClick) {
                    exoBinding.progressBar.setVisibility(View.GONE);
                    exoBinding.llPlay.setVisibility(View.GONE);
                    exoBinding.llPause.setVisibility(View.VISIBLE);
                }
                localIntent.putExtra("MyData", "pause");
                localBroadcastManager.sendBroadcast(localIntent);
            } else {
                if (player != null) {
                    if (player.getPlaybackState() == ExoPlayer.STATE_BUFFERING) {
                        exoBinding.llPlay.setVisibility(View.GONE);
                        exoBinding.llPause.setVisibility(View.GONE);
                        exoBinding.progressBar.setVisibility(View.VISIBLE);
                    } else if (player.getPlayWhenReady()) {
                        exoBinding.llPlay.setVisibility(View.GONE);
                        exoBinding.llPause.setVisibility(View.VISIBLE);
                        exoBinding.progressBar.setVisibility(View.GONE);
                    } else if (!player.getPlayWhenReady()) {
                        exoBinding.llPlay.setVisibility(View.VISIBLE);
                        exoBinding.llPause.setVisibility(View.GONE);
                        exoBinding.progressBar.setVisibility(View.GONE);
                    }
                    exoBinding.exoProgress.setBufferedPosition(player.getBufferedPosition());
                    exoBinding.exoProgress.setPosition(player.getCurrentPosition());
                    exoBinding.exoProgress.setDuration(player.getDuration());
                } else {
                    if (audioClick) {
                        exoBinding.progressBar.setVisibility(View.GONE);
                        exoBinding.llPlay.setVisibility(View.GONE);
                        exoBinding.llPause.setVisibility(View.VISIBLE);
                    } else if (PlayerINIT) {
                        exoBinding.progressBar.setVisibility(View.GONE);
                        exoBinding.llPlay.setVisibility(View.GONE);
                        exoBinding.llPause.setVisibility(View.VISIBLE);
                    }
                } /*else if (isprogressbar) {
                exoBinding.llPlay.setVisibility(View.GONE);
                exoBinding.llPause.setVisibility(View.GONE);
                exoBinding.progressBar.setVisibility(View.VISIBLE);
                handler2.postDelayed(UpdateSongTime2, 2000);
            }*/
            }
//        MediaItem mediaItem1 = MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(R.raw.brain_wellness_spa_declaimer));
//        player.setMediaItem(mediaItem1);
            callAllDisable(false);
//        player.setPlayWhenReady(true);
//        player.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPlayerCtrView() {
        playerControlView.setPlayer(player);
        playerControlView.setProgressUpdateListener((positionx, bufferedPosition) -> {
            exoBinding.exoProgress.setPosition(positionx);
            exoBinding.exoProgress.setBufferedPosition(bufferedPosition);
            if (player != null) {
                exoBinding.exoProgress.setDuration(player.getDuration());
                if ((player.getCurrentPosition() >= oldSongPos + 299500) && (player.getCurrentPosition() <= oldSongPos + 310000)) {
                    oldSongPos = positionx;
                    callHeartbeat();
                }
            }
//            myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(position).getImageFile());
        });
        try {
            getMediaBitmap(ctx, mainPlayModelList.get(position).getImageFile());
        } catch (OutOfMemoryError e) {
            System.out.println(e);
        }
        playerControlView.setFocusable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            playerControlView.setFocusedByDefault(true);
        }
        playerControlView.show();
    }

    private void callRepeatShuffle() {
        if (!url.equalsIgnoreCase("")) {
            if (IsShuffle.equalsIgnoreCase("")) {
                player.setShuffleModeEnabled(false);
            } else if (IsShuffle.equalsIgnoreCase("1")) {
                player.setShuffleModeEnabled(true);
            }
            if (IsRepeat.equalsIgnoreCase("")) {
                player.setRepeatMode(Player.REPEAT_MODE_OFF);
            } else if (IsRepeat.equalsIgnoreCase("0")) {
                player.setRepeatMode(Player.REPEAT_MODE_ONE);
            } else if (IsRepeat.equalsIgnoreCase("1")) {
                player.setRepeatMode(Player.REPEAT_MODE_ALL);
            }
        }
    }

    private void callAllDisable(boolean b) {
        if (b) {
            exoBinding.exoProgress.setClickable(false);
            exoBinding.exoProgress.setEnabled(false);
        } else {
            exoBinding.exoProgress.setClickable(false);
            exoBinding.exoProgress.setEnabled(false);
        }
    }

    private void callButtonText(int ps) {
//        simpleSeekbar.setMax(100);
        try {
            if (!BWSApplication.isNetworkConnected(ctx)) {
                Gson gson = new Gson();
                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                String json2 = shared.getString(CONSTANTS.PREF_KEY_PlayerAudioList, String.valueOf(gson));
                if (!json2.equalsIgnoreCase(String.valueOf(gson))) {
                    Type type1 = new TypeToken<ArrayList<MainPlayModel>>() {
                    }.getType();
                    mainPlayModelList = gson.fromJson(json2, type1);
                }
            }
        } catch (Exception e) {
            Log.e("mini", e.getMessage());
        }
        try {
            url = mainPlayModelList.get(ps).getAudioFile();
            id = mainPlayModelList.get(ps).getId();
        } catch (Exception e) {
            Log.e("mini", e.getMessage());
        }
        try {
            myAudioId = id;
            if (url.equalsIgnoreCase("") || url.isEmpty()) {
                isDisclaimer = 1;
            } else {
                isDisclaimer = 0;
            }
        } catch (Exception e) {
            Log.e("mini", e.getMessage());
        }
        try {
            exoBinding.tvTitle.setText(mainPlayModelList.get(ps).getName());
            exoBinding.tvSubTitle.setText(mainPlayModelList.get(ps).getAudioDirection());
            if (mainPlayModelList.get(ps).getPlaylistID() == null) {
                mainPlayModelList.get(ps).setPlaylistID("");
            }
        } catch (Exception e) {
            Log.e("mini", e.getMessage());
        }
        exoBinding.llPlay.setOnClickListener(view -> {
            if (player != null) {
                exoBinding.llPlay.setVisibility(View.GONE);
                exoBinding.llPause.setVisibility(View.VISIBLE);
                exoBinding.progressBar.setVisibility(View.GONE);
                player.setPlayWhenReady(true);
                int pss = 0;
                if (mainPlayModelList.size() == 1 && position == 1) {
                    pss = 0;
                } else {
                    pss = position;
                }
                if (!mainPlayModelList.get(position).getAudioFile().equalsIgnoreCase("")) {
                    if (mainPlayModelList.get(pss).getId().equalsIgnoreCase(mainPlayModelList.get(mainPlayModelList.size() - 1).getId())
                            && (player.getDuration() - player.getCurrentPosition() <= 20)) {
//                    playerNotificationManager.setPlayer(player);
                        player.seekTo(position, 0);
                    }
                    player.setPlayWhenReady(true);
                    p = new Properties();
                    p.putValue("userId", UserID);
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
                    p.putValue("playerType", "Mini");
                    p.putValue("audioService", appStatus(getActivity()));
                    p.putValue("bitRate", "");
                    p.putValue("sound", String.valueOf(hundredVolume));
                    BWSApplication.addToSegment("Audio Resumed", p, CONSTANTS.track);
                } else {
                    player.setPlayWhenReady(true);
                    p = new Properties();
                    p.putValue("userId", UserID);
                    p.putValue("position", GetCurrentAudioPosition());
                    p.putValue("source", GetSourceName(ctx));
                    p.putValue("playerType", "Mini");
                    if (downloadAudioDetailsList.contains(mainPlayModelList.get(position).getName())) {
                        p.putValue("audioType", "Downloaded");
                    } else {
                        p.putValue("audioType", "Streaming");
                    }
                    p.putValue("bitRate", "");
                    p.putValue("audioService", appStatus(getActivity()));
                    p.putValue("sound", String.valueOf(hundredVolume));
                    BWSApplication.addToSegment("Disclaimer Resumed", p, CONSTANTS.track);
                }
            } else {
                audioClick = true;
                miniPlayer = 1;
                GetAllMedia();
            }
            localIntent.putExtra("MyData", "play");
            localBroadcastManager.sendBroadcast(localIntent);
        });

        try {
            if (url.equalsIgnoreCase("")) {
                Glide.with(ctx).load(R.drawable.disclaimer).thumbnail(0.05f)
                        .placeholder(R.drawable.disclaimer).error(R.drawable.disclaimer)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(28))).priority(Priority.HIGH)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(exoBinding.ivRestaurantImage);
            } else {
                Glide.with(ctx).load(mainPlayModelList.get(ps).getImageFile()).thumbnail(0.05f)
                        .placeholder(R.drawable.disclaimer).error(R.drawable.disclaimer)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(28))).priority(Priority.HIGH)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(exoBinding.ivRestaurantImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!url.equalsIgnoreCase("")) {
                if (addToRecentPlayId.equalsIgnoreCase("")) {
                    addToRecentPlay();
                } else if (!id.equalsIgnoreCase(addToRecentPlayId)) {
                    addToRecentPlay();
                    Log.e("Api call recent", id);
                }
            }
        } catch (Exception e) {
            Log.e("mini", e.getMessage());
        }
        addToRecentPlayId = id;
    }

    public List<String> GetAllMedia() {
        try {
            DB.taskDao()
                    .geAllDataBYDownloaded1("Complete",UserID).observe(getActivity(), audioList -> {
                downloadAudioDetailsList = audioList;
//            audioClick = true;
                MakeArray();
                DB.taskDao()
                        .geAllDataBYDownloaded1("Complete",UserID).removeObserver(audioListx -> {
                });
            });
        } catch (Exception | OutOfMemoryError e) {
            System.out.println(e.getMessage());
        }

        return downloadAudioDetailsList;
    }

    public List<String> GetAllMedia1() {
        try {
            DB.taskDao()
                    .geAllDataBYDownloaded1("Complete",UserID).observe(getActivity(), audioList -> {
                downloadAudioDetailsList = audioList;
                DB.taskDao()
                        .geAllDataBYDownloaded1("Complete",UserID).removeObserver(audioListx -> {
                });
            });
        } catch (Exception | OutOfMemoryError e) {
            System.out.println(e.getMessage());
        }
        return downloadAudioDetailsList;
    }

    private void MakeArray() {
        Gson gson = new Gson();
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
        String json = shared.getString(CONSTANTS.PREF_KEY_MainAudioList, String.valueOf(gson));
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
        SharedPreferences Status = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_Status, Context.MODE_PRIVATE);
        IsRepeat = Status.getString(CONSTANTS.PREF_KEY_IsRepeat, "");
        IsShuffle = Status.getString(CONSTANTS.PREF_KEY_IsShuffle, "");
        MainPlayModel mainPlayModel;
       mainPlayModelList = new ArrayList<>();
        position = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
        if (queuePlay) {
            playFrom = "queuePlay";
        } else if (audioPlay) {
            playFrom = "audioPlay";
        } else {
            playFrom = "audioPlay";
        }
        if (AudioFlag.equalsIgnoreCase("MainAudioList")) {
            Type type = new TypeToken<ArrayList<MainAudioModel.ResponseData.Detail>>() {
            }.getType();
            ArrayList<MainAudioModel.ResponseData.Detail> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile()); 
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();
        } else if (AudioFlag.equalsIgnoreCase("ViewAllAudioList")) {
            Type type = new TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail>>() {
            }.getType();
            ArrayList<ViewAllAudioListModel.ResponseData.Detail> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("SearchAudio")) {
            Type type = new TypeToken<ArrayList<SuggestedModel.ResponseData>>() {
            }.getType();
            ArrayList<SuggestedModel.ResponseData> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile()); 
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("SearchModelAudio")) {
            Type type = new TypeToken<ArrayList<SearchBothModel.ResponseData>>() {
            }.getType();
            ArrayList<SearchBothModel.ResponseData> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile()); 
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
            Type type = new TypeToken<ArrayList<AppointmentDetailModel.Audio>>() {
            }.getType();
            ArrayList<AppointmentDetailModel.Audio> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile()); 
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
            Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
            }.getType();
            ArrayList<DownloadAudioDetails> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile()); 
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("Downloadlist")) {
            Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
            }.getType();
            ArrayList<DownloadAudioDetails> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();

            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID(arrayList.get(i).getPlaylistId());
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile()); 
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("TopCategories")) {
            Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
            }.getType();
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();

            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile()); 
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("SubPlayList")) {
            Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
            }.getType();
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID(arrayList.get(i).getPlaylistID());
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile()); 
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json1 = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json1);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();

        }
        callButtonText(position);
        getPrepareShowData();
    }

    private void MakeArray2() {
        Gson gson = new Gson();
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
        String json = shared.getString(CONSTANTS.PREF_KEY_MainAudioList, String.valueOf(gson));
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
        SharedPreferences Status = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_Status, Context.MODE_PRIVATE);
        IsRepeat = Status.getString(CONSTANTS.PREF_KEY_IsRepeat, "");
        IsShuffle = Status.getString(CONSTANTS.PREF_KEY_IsShuffle, "");
        MainPlayModel mainPlayModel;
         mainPlayModelList = new ArrayList<>();
        position = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
        if (queuePlay) {
            playFrom = "queuePlay";
        } else if (audioPlay) {
            playFrom = "audioPlay";
        } else {
            playFrom = "audioPlay";
        }
        if (AudioFlag.equalsIgnoreCase("MainAudioList")) {
            Type type = new TypeToken<ArrayList<MainAudioModel.ResponseData.Detail>>() {
            }.getType();
            ArrayList<MainAudioModel.ResponseData.Detail> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();


        } else if (AudioFlag.equalsIgnoreCase("ViewAllAudioList")) {
            Type type = new TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail>>() {
            }.getType();
            ArrayList<ViewAllAudioListModel.ResponseData.Detail> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("SearchAudio")) {
            Type type = new TypeToken<ArrayList<SuggestedModel.ResponseData>>() {
            }.getType();
            ArrayList<SuggestedModel.ResponseData> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("SearchModelAudio")) {
            Type type = new TypeToken<ArrayList<SearchBothModel.ResponseData>>() {
            }.getType();
            ArrayList<SearchBothModel.ResponseData> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
            Type type = new TypeToken<ArrayList<AppointmentDetailModel.Audio>>() {
            }.getType();
            ArrayList<AppointmentDetailModel.Audio> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
            Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
            }.getType();
            ArrayList<DownloadAudioDetails> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile()); 
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("Downloadlist")) {
            Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
            }.getType();
            ArrayList<DownloadAudioDetails> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();

            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID(arrayList.get(i).getPlaylistId());
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("TopCategories")) {
            Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
            }.getType();
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();

            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("SubPlayList")) {
            Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
            }.getType();
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID(arrayList.get(i).getPlaylistID());
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile()); 
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json1 = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json1);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();

        }
//        myBitmap = getMediaBitmap(ctx,mainPlayModelList.get(player.getCurrentWindowIndex()).getImageFile());
//        callButtonText(position);
//        if (mainPlayModelList.get(position).getAudioFile().equalsIgnoreCase("")) {
//            initializePlayerDisclaimer();
//        } else {
//            initializePlayer();
//        }
        if (!audioClick) {
            getPrepareShowData();
        } else {
            callButtonText(position);
        }
    }

    private void removeArray() {
//        if(!BWSApplication.isNetworkConnected(ctx)){
        callNewPlayerRelease();
//        }

        isDisclaimer = 0;
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
        Gson gson = new Gson();
        String json1 = shared.getString(CONSTANTS.PREF_KEY_MainAudioList, String.valueOf(gson));
        mainPlayModelList = new ArrayList<>();
        MainPlayModel mainPlayModel;
        if (AudioFlag.equalsIgnoreCase("MainAudioList")) {
            Type type = new TypeToken<ArrayList<MainAudioModel.ResponseData.Detail>>() {
            }.getType();
            ArrayList<MainAudioModel.ResponseData.Detail> arrayList = gson.fromJson(json1, type);

            if (arrayList.get(position).getAudioFile().equalsIgnoreCase("")) {
                arrayList.remove(position);
            }

            for (int i = 0; i < arrayList.size(); i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile()); 
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();
        } else if (AudioFlag.equalsIgnoreCase("ViewAllAudioList")) {
            Type type = new TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail>>() {
            }.getType();
            ArrayList<ViewAllAudioListModel.ResponseData.Detail> arrayList = gson.fromJson(json1, type);
            if (arrayList.get(position).getAudioFile().equalsIgnoreCase("")) {
                arrayList.remove(position);
            }
            for (int i = 0; i < arrayList.size(); i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();
        } else if (AudioFlag.equalsIgnoreCase("SearchModelAudio")) {
            Type type = new TypeToken<ArrayList<SearchBothModel.ResponseData>>() {
            }.getType();
            ArrayList<SearchBothModel.ResponseData> arrayList = gson.fromJson(json1, type);
            if (arrayList.get(0).getAudioFile().equalsIgnoreCase("")) {
                arrayList.remove(0);
            }
            for (int i = 0; i < arrayList.size(); i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();
        } else if (AudioFlag.equalsIgnoreCase("SearchAudio")) {
            Type type = new TypeToken<ArrayList<SuggestedModel.ResponseData>>() {
            }.getType();
            ArrayList<SuggestedModel.ResponseData> arrayList = gson.fromJson(json1, type);
            if (arrayList.get(0).getAudioFile().equalsIgnoreCase("")) {
                arrayList.remove(0);
            }
            for (int i = 0; i < arrayList.size(); i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();
        } else if (AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
            Type type = new TypeToken<ArrayList<AppointmentDetailModel.Audio>>() {
            }.getType();
            ArrayList<AppointmentDetailModel.Audio> arrayList = gson.fromJson(json1, type);
            if (arrayList.get(0).getAudioFile().equalsIgnoreCase("")) {
                arrayList.remove(0);
            }
            for (int i = 0; i < arrayList.size(); i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();
        }else if (AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
            Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
            }.getType();
            ArrayList<DownloadAudioDetails> arrayList = gson.fromJson(json1, type);
            if (arrayList.get(position).getAudioFile().equalsIgnoreCase("")) {
                arrayList.remove(position);
            }
            for (int i = 0; i < arrayList.size(); i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile()); 
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();
        } else if (AudioFlag.equalsIgnoreCase("Downloadlist")) {
            Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
            }.getType();
            ArrayList<DownloadAudioDetails> arrayList = gson.fromJson(json1, type);
            if (arrayList.get(position).getAudioFile().equalsIgnoreCase("")) {
                arrayList.remove(position);
            }
            for (int i = 0; i < arrayList.size(); i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID(arrayList.get(i).getPlaylistId());
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile()); 
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();
        } else if (AudioFlag.equalsIgnoreCase("TopCategories")) {
            Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
            }.getType();
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json1, type);
            if (arrayList.get(position).getAudioFile().equalsIgnoreCase("")) {
                arrayList.remove(position);
            }
            for (int i = 0; i < arrayList.size(); i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();
        } else if (AudioFlag.equalsIgnoreCase("SubPlayList")) {
            Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
            }.getType();
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json1, type);
            if (arrayList.get(position).getAudioFile().equalsIgnoreCase("")) {
                arrayList.remove(position);
            }
            for (int i = 0; i < arrayList.size(); i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setId(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID(arrayList.get(i).getPlaylistID());
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz);
            editor.commit();
        }
        MakeArray();
    }

    private void getPrepareShowData() {
        callButtonText(position);
        if (!mainPlayModelList.get(position).getImageFile().equalsIgnoreCase("")) {
            try {
                if (player != null) {
                    GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
                    globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            initializePlayer();
            setPlayerCtrView();
        } else {
            try {
                if (player != null) {
                    GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
                    globalInitExoPlayer.InitNotificationAudioPLayerD(ctx);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            localIntent1 = new Intent("descIssue");
            localBroadcastManager1 = LocalBroadcastManager.getInstance(ctx);

            LocalBroadcastManager.getInstance(getActivity())
                    .registerReceiver(listener1, new IntentFilter("descIssue"));
            initializePlayerDisclaimer();
            setPlayerCtrView();
        }
    }

    class AppLifecycleCallback implements Application.ActivityLifecycleCallbacks {
        private int numStarted = 0;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (numStarted == 0) {
                Log.e("APPLICATION", "APP IN FOREGROUND");
                //app went to foreground
            }
            numStarted++;
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            numStarted--;
            if (numStarted == 0) {
                Log.e("APPLICATION", "App is in BACKGROUND");
                // app went to background
            }

            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(listener1);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
}