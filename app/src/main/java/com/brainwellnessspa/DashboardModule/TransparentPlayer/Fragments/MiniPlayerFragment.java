package com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments;

import android.app.Activity;
import android.app.Application;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import com.brainwellnessspa.DashboardModule.Activities.AudioPlayerActivity;
import com.brainwellnessspa.DashboardModule.Models.AddToQueueModel;
import com.brainwellnessspa.DashboardModule.Models.AppointmentDetailModel;
import com.brainwellnessspa.DashboardModule.Models.MainAudioModel;
import com.brainwellnessspa.DashboardModule.Models.SearchBothModel;
import com.brainwellnessspa.DashboardModule.Models.SubPlayListModel;
import com.brainwellnessspa.DashboardModule.Models.SucessModel;
import com.brainwellnessspa.DashboardModule.Models.SuggestedModel;
import com.brainwellnessspa.DashboardModule.Models.ViewAllAudioListModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia;
import com.brainwellnessspa.EncryptDecryptUtils.FileUtils;
import com.brainwellnessspa.LikeModule.Models.LikesHistoryModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.Services.GlobalInitExoPlayer;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.FragmentMiniExoCustomBinding;
import com.brainwellnessspa.databinding.FragmentMiniPlayerBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.ComeScreenAccount;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.audioClick;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.miniPlayer;
import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.APP_SERVICE_STATUS;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.GetCurrentAudioPosition;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.GetSourceName;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.PlayerINIT;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.getMediaBitmap;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.myBitmap;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.player;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.relesePlayer;

public class MiniPlayerFragment extends Fragment {
    public static int isDisclaimer = 0;
    public static String addToRecentPlayId = "", myAudioId = "";
    public static String PlayerStatus = "";
    public AudioManager audioManager;
    public int hundredVolume = 0, currentVolume = 0, maxVolume = 0, percent;
    FragmentMiniPlayerBinding binding;
    FragmentMiniExoCustomBinding exoBinding;
    Context ctx;
    Activity activity;
    View view;
    List<String> downloadAudioDetailsList;
    List<String> downloadAudioDetailsListGloble;
    byte[] descriptor;
    List<File> filesDownloaded;
    ArrayList<MainPlayModel> mainPlayModelList, mainPlayModelList2;
    ArrayList<AddToQueueModel> addToQueueModelList;
    String IsRepeat = "", IsShuffle = "", UserID, AudioFlag= "", id= "", name, url = "", playFrom = "";
    int position, listSize;
    Boolean queuePlay, audioPlay;
    LocalBroadcastManager localBroadcastManager;
    Intent localIntent;
    boolean isPrepared = false;
    PlayerControlView playerControlView;
    Properties p;
    private long mLastClickTime = 0;
    /*    Handler handler1, handler2;
        //    boolean ismyDes = false;
        Runnable UpdateSongTime2 = new Runnable() {
            @Override
            public void run() {
                handler2.removeCallbacks(UpdateSongTime2);
    //            audioClick = true;
                initializePlayerDisclaimer();
                Log.e("runaa", "run");
            }
        };
        /*    Handler handler1, handler2;
            //    boolean ismyDes = false;
            Runnable UpdateSongTime2 = new Runnable() {
                @Override
                public void run() {
                    handler2.removeCallbacks(UpdateSongTime2);
        //            audioClick = true;
                    initializePlayerDisclaimer();
                    Log.e("runaa", "run");
                }
            };

            Runnable UpdateSongTime1 = new Runnable() {
                @Override
                public void run() {
                    handler1.removeCallbacks(UpdateSongTime1);
        //                audioClick = true;
                    initializePlayer();
                    Log.e("run  saa", "runasca");
                }
            };*/
    private BroadcastReceiver listener = new BroadcastReceiver() {
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
        PlayerStatus = "Mini";
        addToQueueModelList = new ArrayList<>();
        mainPlayModelList = new ArrayList<>();
        mainPlayModelList2 = new ArrayList<>();
        downloadAudioDetailsList = new ArrayList<>();
        downloadAudioDetailsListGloble = new ArrayList<>();
        filesDownloaded = new ArrayList<>();
        exoBinding = DataBindingUtil.inflate(LayoutInflater.from(ctx)
                , R.layout.fragment_mini_exo_custom, binding.playerControlView, false);
//        handler1 = new Handler();
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
            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String json = gson.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, json);
            String json1 = gson.toJson(addToQueueModelList);
            try {
                if (queuePlay) {
                    editor.putString(CONSTANTS.PREF_KEY_queueList, json1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            editor.putInt(CONSTANTS.PREF_KEY_position, position);
            editor.commit();
            Intent i = new Intent(ctx, AudioPlayerActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            ctx.startActivity(i);
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getActivity().registerActivityLifecycleCallbacks(new AppLifecycleCallback());
        }
        return view;
    }

    @Override
    public void onResume() {
        if (ComeScreenAccount == 1) {
            binding.llLayout.setVisibility(View.GONE);
        } else if (ComeScreenAccount == 0) {
            binding.llLayout.setVisibility(View.VISIBLE);
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
                setpleyerctrView();
            }
            if (player != null) {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                player.setWakeMode(C.WAKE_MODE_LOCAL);
                player.setHandleWakeLock(true);
                player.setHandleAudioBecomingNoisy(true);
                player.addListener(new ExoPlayer.EventListener() {
                    @Override
                    public void onPositionDiscontinuity(int reason) {

                    }

                    @Override
                    public void onPlayerError(ExoPlaybackException error) {

                    }

                    @Override
                    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                        Log.v("TAG", "Listener-onTracksChanged... ");

                        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                        Gson gson = new Gson();
                        String json = shared.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gson));
                        if (!json.equalsIgnoreCase(String.valueOf(gson))) {
                            Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                            }.getType();
                            mainPlayModelList = gson.fromJson(json, type);
                        }
                        player.setPlayWhenReady(true);
                        position = player.getCurrentWindowIndex();
                        GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
                        globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList);

                        myBitmap = getMediaBitmap(getActivity(), mainPlayModelList.get(player.getCurrentWindowIndex()).getImageFile());
                        myAudioId = mainPlayModelList.get(player.getCurrentWindowIndex()).getID();
                        SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedz.edit();
                        editor.putInt(CONSTANTS.PREF_KEY_position, position);
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

                        p = new Properties();
                        p.putValue("userId", UserID);
                        p.putValue("audioId", mainPlayModelList.get(position).getID());
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
                        p.putValue("audioService", APP_SERVICE_STATUS);
                        p.putValue("bitRate", "");
                        p.putValue("sound", String.valueOf(hundredVolume));
                        BWSApplication.addToSegment("Audio Started", p, CONSTANTS.track);
                    }

                    @Override
                    public void onIsPlayingChanged(boolean isPlaying) {
                        myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(player.getCurrentWindowIndex()).getImageFile());
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

                    @Override
                    public void onPlaybackStateChanged(int state) {
                        if (state == ExoPlayer.STATE_READY) {
                            p = new Properties();
                            p.putValue("userId", UserID);
                            p.putValue("audioId", mainPlayModelList.get(position).getID());
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
                            p.putValue("audioService", APP_SERVICE_STATUS);
                            p.putValue("bitRate", "");
                            p.putValue("sound", String.valueOf(hundredVolume));
                            BWSApplication.addToSegment("Audio Buffer Completed", p, CONSTANTS.track);
                            if (player.getPlayWhenReady()) {
                                exoBinding.llPlay.setVisibility(View.GONE);
                                exoBinding.llPause.setVisibility(View.VISIBLE);
                                exoBinding.progressBar.setVisibility(View.GONE);
                                localIntent.putExtra("MyData", "play");
                                localBroadcastManager.sendBroadcast(localIntent);

                                p = new Properties();
                                p.putValue("userId", UserID);
                                p.putValue("audioId", mainPlayModelList.get(position).getID());
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
                                p.putValue("audioService", APP_SERVICE_STATUS);
                                p.putValue("bitRate", "");
                                p.putValue("sound", String.valueOf(hundredVolume));
                                BWSApplication.addToSegment("Audio Playing", p, CONSTANTS.track);
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
                            p.putValue("audioId", mainPlayModelList.get(position).getID());
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
                            p.putValue("audioService", APP_SERVICE_STATUS);
                            p.putValue("bitRate", "");
                            p.putValue("sound", String.valueOf(hundredVolume));
                            BWSApplication.addToSegment("Audio Buffer Started", p, CONSTANTS.track);
                        } else if (state == ExoPlayer.STATE_ENDED) {
                            try {
                                p = new Properties();
                                p.putValue("userId", UserID);
                                p.putValue("audioId", mainPlayModelList.get(position).getID());
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
                                p.putValue("audioService", APP_SERVICE_STATUS);
                                p.putValue("bitRate", "");
                                p.putValue("sound", String.valueOf(hundredVolume));
                                BWSApplication.addToSegment("Audio Completed", p, CONSTANTS.track);
                                if (mainPlayModelList.get(player.getCurrentWindowIndex()).getID().
                                        equalsIgnoreCase(mainPlayModelList.get(mainPlayModelList.size() - 1).getID())) {
                                    exoBinding.llPlay.setVisibility(View.VISIBLE);
                                    exoBinding.llPause.setVisibility(View.GONE);
                                    exoBinding.progressBar.setVisibility(View.GONE);
                                    player.setPlayWhenReady(false);
                                    localIntent.putExtra("MyData", "pause");
                                    localBroadcastManager.sendBroadcast(localIntent);
                                    p = new Properties();
                                    p.putValue("userId", UserID);
                                    p.putValue("audioId", mainPlayModelList.get(position).getID());
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
                                    p.putValue("audioService", APP_SERVICE_STATUS);
                                    p.putValue("bitRate", "");
                                    p.putValue("sound", String.valueOf(hundredVolume));
                                    BWSApplication.addToSegment("Audio Playback Completed", p, CONSTANTS.track);
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
                                        p.putValue("audioService", APP_SERVICE_STATUS);
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
                      /*GetAllMedia();
                        audioClick = true;

                        playerControlView.setPlayer(player);
                        playerControlView.setProgressUpdateListener((position, bufferedPosition) -> {
                            exoBinding.exoProgress.setPosition(position);
                            exoBinding.exoProgress.setBufferedPosition(bufferedPosition);
                        });
                        playerControlView.setFocusable(true);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            playerControlView.setFocusedByDefault(true);
                        }
                        playerControlView.show();
                        Log.e("Exoplayer Idle", "my Exop in Idle");*/
                        }

                    }
                });

           /* exoBinding.exoProgress.addListener(new TimeBar.OnScrubListener() {
                @Override
                public void onScrubStart(TimeBar timeBar, long position) {
                    exoBinding.exoProgress.setPosition(position);
                }

                @Override
                public void onScrubMove(TimeBar timeBar, long position) {

                }

                @Override
                public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
                    player.seekTo(position);
                    exoBinding.exoProgress.setPosition(position);
                }
            });*/
                setpleyerctrView();
                callRepeatShuffle();
            }
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
                } else {
                    if (audioClick) {
                        exoBinding.progressBar.setVisibility(View.GONE);
                        exoBinding.llPlay.setVisibility(View.GONE);
                        exoBinding.llPause.setVisibility(View.VISIBLE);
                        Log.e("newBUff", "exoBinding.progressBar.setVisibility(View.GONE);");
                    } else if (PlayerINIT) {
                        exoBinding.progressBar.setVisibility(View.GONE);
                        exoBinding.llPlay.setVisibility(View.GONE);
                        exoBinding.llPause.setVisibility(View.VISIBLE);
                        Log.e("PlayerINIT", "exoBinding.progressBar.setVisibility(View.GONE);");
                    }
                }/*else if (isprogressbar) {
                exoBinding.llPlay.setVisibility(View.GONE);
                exoBinding.llPause.setVisibility(View.GONE);
                exoBinding.progressBar.setVisibility(View.VISIBLE);
                handler1.postDelayed(UpdateSongTime1, 2000);
            }*/
            }
            epAllClicks();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("init media State: ", e.getMessage());
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
                    p.putValue("audioId", mainPlayModelList.get(position).getID());
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
                    p.putValue("audioService", APP_SERVICE_STATUS);
                    p.putValue("bitRate", "");
                    p.putValue("sound", String.valueOf(hundredVolume));
                    BWSApplication.addToSegment("Audio Paused", p, CONSTANTS.track);
                } else {
                    audioClick = true;
                    miniPlayer = 1;
                    GetAllMedia();
                }
            });

/*
        exoBinding.llPlay.setOnClickListener(view -> {
            if (player != null) {
                exoBinding.llPlay.setVisibility(View.GONE);
                exoBinding.llPause.setVisibility(View.VISIBLE);
                exoBinding.progressBar.setVisibility(View.GONE);
                if (mainPlayModelList.get(player.getCurrentWindowIndex()).getID().equalsIgnoreCase(mainPlayModelList.get(mainPlayModelList.size() - 1).getID())
                        && (player.getDuration() - player.getCurrentPosition() <= 20)) {
//                    playerNotificationManager.setPlayer(player);
                    player.seekTo(position, 0);
                }
                player.setPlayWhenReady(true);
                p = new Properties();
                p.putValue("userId", UserID);
                p.putValue("audioId", mainPlayModelList.get(position).getID());
                p.putValue("audioName", mainPlayModelList.get(position).getName());
                p.putValue("audioDescription", "");
                p.putValue("directions", mainPlayModelList.get(position).getAudioDirection());
                p.putValue("masterCategory", mainPlayModelList.get(position).getAudiomastercat());
                p.putValue("subCategory", mainPlayModelList.get(position).getAudioSubCategory());
                p.putValue("audioDuration", mainPlayModelList.get(position).getAudioDuration());
                p.putValue("position", GetCurrentAudioPosition());
                if (downloadAudioDetailsList.contains(mainPlayModelList.get(position).getName())){
                    p.putValue("audioType", "Downloaded");
                }else {
                    p.putValue("audioType", "Streaming");
                }
                p.putValue("source", GetSourceName(ctx));
                p.putValue("playerType", "Mini");
                p.putValue("audioService", APP_SERVICE_STATUS);
                p.putValue("bitRate", "");
                p.putValue("sound", */
            /*GetDeviceVolume(ctx)*//*
"0");
                BWSApplication.addToSegment("Audio Resumed", p, CONSTANTS.track);
            } else {
                audioClick = true;
                miniPlayer = 1;
                GetAllMedia();
            }

            localIntent.putExtra("MyData", "play");
            localBroadcastManager.sendBroadcast(localIntent);
        });
*/
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
                globalInitExoPlayer.GlobleInItDisclaimer(ctx, mainPlayModelList);
                setpleyerctrView();
            }

            exoBinding.exoProgress.setClickable(false);
            exoBinding.exoProgress.setEnabled(false);
            if (player != null) {
                player.addListener(new ExoPlayer.EventListener() {
                    @Override
                    public void onPlaybackStateChanged(int state) {
                        if (state == ExoPlayer.STATE_ENDED) {
                            //player back ended
                            audioClick = true;
                            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared.edit();
                            editor.putString(CONSTANTS.PREF_KEY_IsDisclimer, "0");
                            editor.commit();
                            removeArray();
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
                            p.putValue("audioService", APP_SERVICE_STATUS);
                            p.putValue("sound", String.valueOf(hundredVolume));
                            BWSApplication.addToSegment("Disclaimer Completed", p, CONSTANTS.track);
                        }
                        if (state == ExoPlayer.STATE_READY) {
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
                            p.putValue("audioService", APP_SERVICE_STATUS);
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
                                p.putValue("audioService", APP_SERVICE_STATUS);
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
//                @Override
//                public void onIsLoadingChanged(boolean isLoading) {
//                    isPrepared = isLoading;
                    /*if (isLoading) {
                        exoBinding.llPlay.setVisibility(View.GONE);
                        exoBinding.llPause.setVisibility(View.GONE);
                        exoBinding.progressBar.setVisibility(View.VISIBLE);
                        Log.e("Isloading", "MiniLoadingggggggggggggggggg");
                    }*/
//                }

                    @Override
                    public void onIsPlayingChanged(boolean isPlaying) {
                        if (player != null) {
                            myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(player.getCurrentWindowIndex()).getImageFile());
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
                setpleyerctrView();
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
                p.putValue("audioService", APP_SERVICE_STATUS);
                BWSApplication.addToSegment("Disclaimer Paused", p, CONSTANTS.track);
            });

       /* exoBinding.llPlay.setOnClickListener(view -> {
            if (player != null) {
                exoBinding.llPlay.setVisibility(View.GONE);
                exoBinding.llPause.setVisibility(View.VISIBLE);
                exoBinding.progressBar.setVisibility(View.GONE);
                player.setPlayWhenReady(true);
                p = new Properties();
                p.putValue("userId", UserID);
                p.putValue("position", GetCurrentAudioPosition());
                p.putValue("source", GetSourceName(ctx));
                p.putValue("playerType", "Mini");
                if (downloadAudioDetailsList.contains(mainPlayModelList.get(position).getName())){
                    p.putValue("audioType", "Downloaded");
                }else {
                    p.putValue("audioType", "Streaming");
                }
                p.putValue("bitRate", "");
                p.putValue("sound", *//*GetDeviceVolume(ctx)*//*"0");
                BWSApplication.addToSegment("Disclaimer Resumed", p, CONSTANTS.track);
            } else {
                audioClick = true;
                miniPlayer = 1;
                GetAllMedia();
            }
            localIntent.putExtra("MyData", "play");
            localBroadcastManager.sendBroadcast(localIntent);
        });*/
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
                        Log.e("newBUff", "exoBinding.progressBar.setVisibility(View.GONE);");
                    } else if (PlayerINIT) {
                        exoBinding.progressBar.setVisibility(View.GONE);
                        exoBinding.llPlay.setVisibility(View.GONE);
                        exoBinding.llPause.setVisibility(View.VISIBLE);
                        Log.e("PlayerINIT", "exoBinding.progressBar.setVisibility(View.GONE);");
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

    private void setpleyerctrView() {
        playerControlView.setPlayer(player);
        playerControlView.setProgressUpdateListener((position, bufferedPosition) -> {
            exoBinding.exoProgress.setPosition(position);
            exoBinding.exoProgress.setBufferedPosition(bufferedPosition);
        });
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

    private void getDownloadMedia(DownloadMedia downloadMedia, String name, int i) {
        class getDownloadMedia extends AsyncTask<Void, Void, Void> {
            File fileDescriptor = null;
            int x;

            @Override
            protected Void doInBackground(Void... voids) {
//                try {

                downloadAudioDetailsListGloble.add(name);
                fileDescriptor = new File(FileUtils.getFilePath(ctx, name));
             /*   descriptor = downloadMedia.decrypt(name);
                try {
                    if (descriptor != null) {
                        fileDescriptor = FileUtils.getTempFileDescriptor1(ctx.getApplicationContext(), descriptor);
                    }
                } catch (IOException e) {
                    descriptor = downloadMedia.decrypt(name);
                    try {
                        if (descriptor != null) {
                            fileDescriptor = FileUtils.getTempFileDescriptor1(ctx.getApplicationContext(), descriptor);
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }*/
                Log.e("Download do in bg Call", String.valueOf(x) + ":-" + name);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                filesDownloaded.add(fileDescriptor);
//                Log.e("filename", fileDescriptor.getName());
//                Log.e("MakeArry not Call",String.valueOf(i));
                descriptor = null;
                fileDescriptor = null;
//                if(i == downloadAudioDetailsList.size()) {
//                    Log.e("MakeArry Call",String.valueOf(i));
                x = i;
                x = x + 1;
                for (int j = 0; j < downloadAudioDetailsList.size(); j++) {
                    if (x < mainPlayModelList2.size()) {
                        if (downloadAudioDetailsList.get(j).equals(mainPlayModelList2.get(x).getName())) {
                            getDownloadMedia(downloadMedia, mainPlayModelList2.get(x).getName(), x);
                            break;
                        }
                    } else {
                        MakeArray();
                        Log.e("MakeArry Call", String.valueOf(x));
                        break;
                    }
                    if (j == downloadAudioDetailsList.size() - 1) {
                        x = x + 1;
                        j = 0;
                        Log.e("again for Call", String.valueOf(x));
                    }
                }
             /*   if (x < mainPlayModelList.size() - 1) {
//                    getDownloadMedia(downloadMedia, downloadAudioDetailsList.get(x), x);
                    Log.e("DownloadMedia Call", String.valueOf(x));
                } else {
//                    MakeArray();
                    Log.e("MakeArry Call", String.valueOf(x));
                }*/
//                }
                super.onPostExecute(aVoid);
            }
        }

        getDownloadMedia st = new getDownloadMedia();
        st.execute();
    }

    private void callButtonText(int ps) {
//        simpleSeekbar.setMax(100);
        if (!BWSApplication.isNetworkConnected(ctx)) {
            Gson gson = new Gson();
            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            String json2 = shared.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gson));
            if (!json2.equalsIgnoreCase(String.valueOf(gson))) {
                Type type1 = new TypeToken<ArrayList<MainPlayModel>>() {
                }.getType();
                mainPlayModelList = gson.fromJson(json2, type1);
            }
        }
        url = mainPlayModelList.get(ps).getAudioFile();
        id = mainPlayModelList.get(ps).getID();
        myAudioId = id;
        if (url.equalsIgnoreCase("") || url.isEmpty()) {
            isDisclaimer = 1;
        } else {
            isDisclaimer = 0;
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
                }
                if (!mainPlayModelList.get(position).getAudioFile().equalsIgnoreCase("")) {
                    if (mainPlayModelList.get(pss).getID().equalsIgnoreCase(mainPlayModelList.get(mainPlayModelList.size() - 1).getID())
                            && (player.getDuration() - player.getCurrentPosition() <= 20)) {
//                    playerNotificationManager.setPlayer(player);
                        player.seekTo(position, 0);
                    }
                    player.setPlayWhenReady(true);
                    p = new Properties();
                    p.putValue("userId", UserID);
                    p.putValue("audioId", mainPlayModelList.get(position).getID());
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
                    p.putValue("audioService", APP_SERVICE_STATUS);
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
                    p.putValue("audioService", APP_SERVICE_STATUS);
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

        if (mainPlayModelList.get(ps).getPlaylistID() == null) {
            mainPlayModelList.get(ps).setPlaylistID("");
        }
        try {
            if (url.equalsIgnoreCase("")) {
                Glide.with(ctx).load(R.drawable.disclaimer).thumbnail(0.05f)
                        .placeholder(R.drawable.disclaimer).error(R.drawable.disclaimer)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(exoBinding.ivRestaurantImage);
            } else {
                Glide.with(ctx).load(mainPlayModelList.get(ps).getImageFile()).thumbnail(0.05f)
                        .placeholder(R.drawable.disclaimer).error(R.drawable.disclaimer)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(exoBinding.ivRestaurantImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        exoBinding.tvTitle.setText(mainPlayModelList.get(ps).getName());
        exoBinding.tvSubTitle.setText(mainPlayModelList.get(ps).getAudioDirection());
        if (!url.equalsIgnoreCase("")) {
            if (!id.equalsIgnoreCase(addToRecentPlayId)) {
                addToRecentPlay();
                Log.e("Api call recent", id);
            }
        }
        addToRecentPlayId = id;
    }

    public List<String> GetAllMedia() {
        DatabaseClient
                .getInstance(ctx)
                .getaudioDatabase()
                .taskDao()
                .geAllDataBYDownloaded1("Complete").observe(getActivity(), audioList -> {
            downloadAudioDetailsList = audioList;
//            audioClick = true;
            MakeArray();
            DatabaseClient
                    .getInstance(ctx)
                    .getaudioDatabase()
                    .taskDao()
                    .geAllDataBYDownloaded1("Complete").removeObserver(audioListx -> {
            });
        });
  /*      class GetTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                downloadAudioDetailsList = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .geAllDataBYDownloaded("Complete");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                audioClick = true;
                MakeArray();
//                if(mainPlayModelList.get(position).getAudioFile().equals("")){
//                   getPrepareShowData();
//                }
//                mainPlayModelList2 = new ArrayList<>();
//                mainPlayModelList2 = mainPlayModelList;
             *//*   if (downloadAudioDetailsList.size() != 0) {
                    if (mainPlayModelList.get(position).getAudioFile().equals("")) {
//                        getPrepareShowData();
//                          ismyDes = true;
                        mainPlayModelList2.remove(position);
                    }
                    int x = 0;
                    downloadAudioDetailsListGloble = new ArrayList<>();
                    for (int i = 0; i < downloadAudioDetailsList.size(); i++) {
                        if (x < mainPlayModelList2.size()) {
                            if (downloadAudioDetailsList.get(i).equals(mainPlayModelList2.get(x).getName())) {
                                DownloadMedia downloadMedia = new DownloadMedia(ctx.getApplicationContext());
                                getDownloadMedia(downloadMedia, mainPlayModelList2.get(x).getName(), x);
                                break;
                            }
                        } else {
                            MakeArray();
                            Log.e("MakeArry Call", String.valueOf(x));
                            break;
                        }
                        if (i == downloadAudioDetailsList.size() - 1) {
                            x = x + 1;
                            if (downloadAudioDetailsList.size() > 1) {
                                i = 0;
                            } else {
                                MakeArray();
                            }
                            Log.e("again for Call", String.valueOf(x));
                        }
                    }
                } else {
                    MakeArray();
                }*//*
//                MakeArray();
                super.onPostExecute(aVoid);
            }
        }
//        MakeArray2();
        GetTask st = new GetTask();
        st.execute();*/
        return downloadAudioDetailsList;
    }

    public List<String> GetAllMedia1() {
        DatabaseClient
                .getInstance(ctx)
                .getaudioDatabase()
                .taskDao()
                .geAllDataBYDownloaded1("Complete").observe(getActivity(), audioList -> {
            downloadAudioDetailsList = audioList;
            DatabaseClient
                    .getInstance(ctx)
                    .getaudioDatabase()
                    .taskDao()
                    .geAllDataBYDownloaded1("Complete").removeObserver(audioListx -> {
            });
        });
      /*  class GetTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                downloadAudioDetailsList = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .geAllDataBYDownloaded("Complete");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }
//        MakeArray2();
        GetTask st = new GetTask();
        st.execute();*/
        return downloadAudioDetailsList;
    }

    private void MakeArray() {
        Gson gson = new Gson();
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        String json = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson));
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        SharedPreferences Status = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_Status, Context.MODE_PRIVATE);
        IsRepeat = Status.getString(CONSTANTS.PREF_KEY_IsRepeat, "");
        IsShuffle = Status.getString(CONSTANTS.PREF_KEY_IsShuffle, "");
        MainPlayModel mainPlayModel;
        addToQueueModelList = new ArrayList<>();
        mainPlayModelList = new ArrayList<>();
        position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
        String json2 = shared.getString(CONSTANTS.PREF_KEY_queueList, String.valueOf(gson));
        if (!json2.equalsIgnoreCase(String.valueOf(gson))) {
            Type type1 = new TypeToken<ArrayList<AddToQueueModel>>() {
            }.getType();
            addToQueueModelList = gson.fromJson(json2, type1);
        }
        queuePlay = shared.getBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
        audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
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
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
        } else if (AudioFlag.equalsIgnoreCase("ViewAllAudioList")) {
            Type type = new TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail>>() {
            }.getType();
            ArrayList<ViewAllAudioListModel.ResponseData.Detail> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("SearchAudio")) {
            Type type = new TypeToken<ArrayList<SuggestedModel.ResponseData>>() {
            }.getType();
            ArrayList<SuggestedModel.ResponseData> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("SearchModelAudio")) {
            Type type = new TypeToken<ArrayList<SearchBothModel.ResponseData>>() {
            }.getType();
            ArrayList<SearchBothModel.ResponseData> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
            Type type = new TypeToken<ArrayList<AppointmentDetailModel.Audio>>() {
            }.getType();
            ArrayList<AppointmentDetailModel.Audio> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("LikeAudioList")) {
            Type type = new TypeToken<ArrayList<LikesHistoryModel.ResponseData.Audio>>() {
            }.getType();
            ArrayList<LikesHistoryModel.ResponseData.Audio> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
            Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
            }.getType();
            ArrayList<DownloadAudioDetails> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("Downloadlist")) {
            Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
            }.getType();
            ArrayList<DownloadAudioDetails> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();

            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID(arrayList.get(i).getPlaylistId());
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("TopCategories")) {
            Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
            }.getType();
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();

            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("SubPlayList")) {
            Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
            }.getType();
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
//            if (isDisclaimer == 0 && disclaimerPlayed == 0) {
//                addDeclaimer();
//            }
            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID(arrayList.get(i).getPlaylistID());
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json1 = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json1);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();

        }
        callButtonText(position);
        getPrepareShowData();
    }

    private void MakeArray2() {
        Gson gson = new Gson();
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        String json = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson));
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        SharedPreferences Status = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_Status, Context.MODE_PRIVATE);
        IsRepeat = Status.getString(CONSTANTS.PREF_KEY_IsRepeat, "");
        IsShuffle = Status.getString(CONSTANTS.PREF_KEY_IsShuffle, "");
        MainPlayModel mainPlayModel;
        addToQueueModelList = new ArrayList<>();
        mainPlayModelList = new ArrayList<>();
        position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
        String json2 = shared.getString(CONSTANTS.PREF_KEY_queueList, String.valueOf(gson));
        if (!json2.equalsIgnoreCase(String.valueOf(gson))) {
            Type type1 = new TypeToken<ArrayList<AddToQueueModel>>() {
            }.getType();
            addToQueueModelList = gson.fromJson(json2, type1);
        }
        queuePlay = shared.getBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
        audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
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
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();


        } else if (AudioFlag.equalsIgnoreCase("ViewAllAudioList")) {
            Type type = new TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail>>() {
            }.getType();
            ArrayList<ViewAllAudioListModel.ResponseData.Detail> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("SearchAudio")) {
            Type type = new TypeToken<ArrayList<SuggestedModel.ResponseData>>() {
            }.getType();
            ArrayList<SuggestedModel.ResponseData> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("SearchModelAudio")) {
            Type type = new TypeToken<ArrayList<SearchBothModel.ResponseData>>() {
            }.getType();
            ArrayList<SearchBothModel.ResponseData> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
            Type type = new TypeToken<ArrayList<AppointmentDetailModel.Audio>>() {
            }.getType();
            ArrayList<AppointmentDetailModel.Audio> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("LikeAudioList")) {
            Type type = new TypeToken<ArrayList<LikesHistoryModel.ResponseData.Audio>>() {
            }.getType();
            ArrayList<LikesHistoryModel.ResponseData.Audio> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
            Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
            }.getType();
            ArrayList<DownloadAudioDetails> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("Downloadlist")) {
            Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
            }.getType();
            ArrayList<DownloadAudioDetails> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();

            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID(arrayList.get(i).getPlaylistId());
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("TopCategories")) {
            Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
            }.getType();
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();

            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("SubPlayList")) {
            Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
            }.getType();
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
//            if (isDisclaimer == 0 && disclaimerPlayed == 0) {
//                addDeclaimer();
//            }
            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID(arrayList.get(i).getPlaylistID());
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json1 = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json1);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
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
        relesePlayer();
//        }
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        Gson gson = new Gson();
        String json1 = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson));
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
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
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
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
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
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
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
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
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
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
        } else if (AudioFlag.equalsIgnoreCase("LikeAudioList")) {
            Type type = new TypeToken<ArrayList<LikesHistoryModel.ResponseData.Audio>>() {
            }.getType();
            ArrayList<LikesHistoryModel.ResponseData.Audio> arrayList = gson.fromJson(json1, type);
            if (arrayList.get(position).getAudioFile().equalsIgnoreCase("")) {
                arrayList.remove(position);
            }
            for (int i = 0; i < arrayList.size(); i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
        } else if (AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
            Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
            }.getType();
            ArrayList<DownloadAudioDetails> arrayList = gson.fromJson(json1, type);
            if (arrayList.get(position).getAudioFile().equalsIgnoreCase("")) {
                arrayList.remove(position);
            }
            for (int i = 0; i < arrayList.size(); i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
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
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID(arrayList.get(i).getPlaylistId());
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
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
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
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
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID(arrayList.get(i).getPlaylistID());
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
        }
        MakeArray();
    }

    private void getPrepareShowData() {
        /*try {
            myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(position).getImageFile());
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        callButtonText(position);
        if (mainPlayModelList.get(position).getAudioFile().equalsIgnoreCase("")) {
//            if(!ismyDes) {
            initializePlayerDisclaimer();
//            }
        } else {
            GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
            globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList);
            initializePlayer();
        }

        playerControlView.setPlayer(player);
        playerControlView.setProgressUpdateListener((position, bufferedPosition) -> {
            exoBinding.exoProgress.setPosition(position);
            exoBinding.exoProgress.setBufferedPosition(bufferedPosition);
        });
        playerControlView.setFocusable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            playerControlView.setFocusedByDefault(true);
        }
        playerControlView.show();
    }

    class AppLifecycleCallback implements Application.ActivityLifecycleCallbacks {
        private int numStarted = 0;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (numStarted == 0) {
                APP_SERVICE_STATUS = getString(R.string.Foreground);
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
                APP_SERVICE_STATUS = getString(R.string.Background);
                Log.e("APPLICATION", "App is in BACKGROUND");
                // app went to background
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
}