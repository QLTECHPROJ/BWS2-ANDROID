package com.brainwellnessspa.DashboardModule.Activities;

import android.app.Activity;
import android.app.Application;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Activities.MembershipChangeActivity;
import com.brainwellnessspa.DashboardModule.Models.AddToQueueModel;
import com.brainwellnessspa.DashboardModule.Models.AppointmentDetailModel;
import com.brainwellnessspa.DashboardModule.Models.AudioLikeModel;
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
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.ActivityAudioPlayerBinding;
import com.brainwellnessspa.databinding.AudioPlayerCustomLayoutBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.util.Assertions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.segment.analytics.Properties;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import me.toptas.fancyshowcase.listener.OnViewInflateListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.DashboardModule.Activities.AddQueueActivity.comeFromAddToQueue;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.audioClick;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.miniPlayer;
import static com.brainwellnessspa.DashboardModule.Audio.AudioFragment.IsLock;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.PlayerStatus;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.addToRecentPlayId;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.isDownloading;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.APP_SERVICE_STATUS;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.GetCurrentAudioPosition;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.GetSourceName;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.PlayerINIT;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.audioRemove;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.getMediaBitmap;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.player;

public class AudioPlayerActivity extends AppCompatActivity {
    public AudioManager audioManager;
    public int hundredVolume = 0, currentVolume = 0, maxVolume = 0, percent;
    List<String> downloadAudioDetailsList;
    List<String> downloadAudioDetailsListGloble;
    AudioPlayerCustomLayoutBinding exoBinding;
    byte[] descriptor;
    Bitmap myBitmap = null;
    List<File> filesDownloaded;
    ActivityAudioPlayerBinding binding;
    ArrayList<MainPlayModel> mainPlayModelList, mainPlayModelList2;
    ArrayList<AddToQueueModel> addToQueueModelList;
    String IsRepeat = "", IsShuffle = "", UserID, AudioFlag, id, name, url, playFrom = "";
    int position, listSize;
    Context ctx;
    Activity activity;
    Boolean queuePlay, audioPlay;
    PlayerNotificationManager playerNotificationManager;
    int notificationId = 1234, downloadPercentage = 0;
    List<DownloadAudioDetails> downloadAudioDetailsList1;
    FancyShowCaseView fancyShowCaseView11, fancyShowCaseView21, fancyShowCaseView31;
    FancyShowCaseQueue queue;
    PlayerControlView playerControlView;
    boolean isPrepared = false;
    Properties p;
    long oldSeekPosition = 0;
    private long mLastClickTime = 0;
//    Handler handler1, handler2;
    //    boolean ismyDes = false;
/*    Runnable UpdateSongTime2 = new Runnable() {
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
//            if(!BWSApplication.isNetworkConnected(ctx)){
//            audioClick = true;
            initializePlayer();
//            }
            Log.e("run  saa", "runasca");
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_audio_player);
        ctx = AudioPlayerActivity.this;
        activity = AudioPlayerActivity.this;
        playerControlView = Assertions.checkNotNull(this.binding.playerControlView);
        exoBinding = DataBindingUtil.inflate(LayoutInflater.from(this)
                , R.layout.audio_player_custom_layout, binding.playerControlView, false);
        binding.playerControlView.addView(exoBinding.getRoot());
        PlayerStatus = "Main";
        addToQueueModelList = new ArrayList<>();
        mainPlayModelList = new ArrayList<>();
        mainPlayModelList2 = new ArrayList<>();
        downloadAudioDetailsList = new ArrayList<>();
        downloadAudioDetailsList1 = new ArrayList<>();
        downloadAudioDetailsListGloble = new ArrayList<>();
        filesDownloaded = new ArrayList<>();
        audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        percent = 100;
        hundredVolume = (int) (currentVolume * percent) / maxVolume;
//        handler1 = new Handler();
//        handler2 = new Handler();
        miniPlayer = 1;
        if (audioClick) {
//            audioClick = false;
            exoBinding.llPlay.setVisibility(View.GONE);
            exoBinding.llPause.setVisibility(View.GONE);
//            // exoBinding.llProgressBar.setVisibility(View.VISIBLE);
            exoBinding.progressBar.setVisibility(View.VISIBLE);

            MakeArray2();
            GetAllMedia();
        } else {
            GetAllMedia1();
            MakeArray2();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        binding.llBack.setOnClickListener(view -> callBack());

        binding.llMore.setOnClickListener(view -> {
//            handler1.removeCallbacks(UpdateSongTime1);
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent i = new Intent(ctx, AddQueueActivity.class);
            if (AudioFlag.equalsIgnoreCase("TopCategories")) {
                i.putExtra("play", "TopCategories");
            } else
                i.putExtra("play", "play");
            i.putExtra("ID", id);
            i.putExtra("position", position);
            i.putExtra("PlaylistAudioId", "");
            startActivity(i);
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(new AppLifecycleCallback());
        }

    }

    private void MakeArray2() {
        audioClick = false;
        SharedPreferences Status = getSharedPreferences(CONSTANTS.PREF_KEY_Status, Context.MODE_PRIVATE);
        IsRepeat = Status.getString(CONSTANTS.PREF_KEY_IsRepeat, "");
        IsShuffle = Status.getString(CONSTANTS.PREF_KEY_IsShuffle, "");
//        showTooltiop();
        Gson gson = new Gson();
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        String json = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson));
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
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
//        binding.tvDireName.setText(R.string.Directions);
//        myBitmap = getMediaBitmap(mainPlayModelList.get(position).getImageFile());

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

    @Override
    public void onBackPressed() {
        callBack();
        super.onBackPressed();
    }

    private void showTooltiop() {
        Animation enterAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_top);
        Animation exitAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);

        fancyShowCaseView11 = new FancyShowCaseView.Builder(activity)
                .customView(R.layout.layout_player_menu, view -> {
                    RelativeLayout rlNext = view.findViewById(R.id.rlNext);
                    rlNext.setOnClickListener(v -> fancyShowCaseView11.hide());
                   /* RelativeLayout rlShowMeHow = view.findViewById(R.id.rlShowMeHow);
                    RelativeLayout rlNoThanks = view.findViewById(R.id.rlNoThanks);
                    rlShowMeHow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fancyShowCaseView11.hide();
                        }
                    });
                    rlNoThanks.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            queue.cancel(true);
                        }
                    });*/

                }).closeOnTouch(false)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .enterAnimation(enterAnimation).exitAnimation(exitAnimation)
                .focusOn(binding.llMore).closeOnTouch(false)
                .build();

        fancyShowCaseView21 = new FancyShowCaseView.Builder(activity)
                .customView(R.layout.layout_player_directions, (OnViewInflateListener) view -> {
                    RelativeLayout rlNext = view.findViewById(R.id.rlNext);
                    rlNext.setOnClickListener(v -> fancyShowCaseView21.hide());
                }).focusShape(FocusShape.ROUNDED_RECTANGLE)
                .enterAnimation(enterAnimation)
                .exitAnimation(exitAnimation).focusOn(exoBinding.llHighlights)
                .closeOnTouch(false).build();

        fancyShowCaseView31 = new FancyShowCaseView.Builder(activity)
                .customView(R.layout.layout_player_options, view -> {
                    view.findViewById(R.id.rlSearch);
                    ImageView ivOptions = view.findViewById(R.id.ivOptions);
                    RelativeLayout rlDone = view.findViewById(R.id.rlDone);
                    Glide.with(ctx)
                            .load(R.drawable.highlight_icons)
                            .asGif()
                            .placeholder(R.drawable.highlight_icons)
                            .crossFade()
                            .into(ivOptions);
                    rlDone.setOnClickListener(v -> fancyShowCaseView31.hide());
                })
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .enterAnimation(enterAnimation).exitAnimation(exitAnimation)
                .focusOn(binding.llBottom).closeOnTouch(false).build();


        queue = new FancyShowCaseQueue()
                .add(fancyShowCaseView11)
                .add(fancyShowCaseView21)
                .add(fancyShowCaseView31);
        queue.show();
       /* IsRegisters = "false";
        IsRegisters1 = "false";*/

    }

    @Override
    public void onResume() {

        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json1 = shared.getString(CONSTANTS.PREF_KEY_queueList, String.valueOf(gson));
        if (!json1.equalsIgnoreCase(String.valueOf(gson))) {
            Type type1 = new TypeToken<ArrayList<AddToQueueModel>>() {
            }.getType();
            addToQueueModelList = gson.fromJson(json1, type1);
        }
        String json = shared.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gson));
        if (!json.equalsIgnoreCase(String.valueOf(gson))) {
            Type type = new TypeToken<ArrayList<MainPlayModel>>() {
            }.getType();
            mainPlayModelList = gson.fromJson(json, type);
        }
//        callLLMoreViewQClicks();

//        if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(name)) {
//            handler1.postDelayed(UpdateSongTime1, 500);
//        } else {
//            binding.pbProgress.setVisibility(View.GONE);
//            handler1.removeCallbacks(UpdateSongTime1);
//        }
//        GetMedia2();
        queuePlay = shared.getBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
        audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
        if (queuePlay) {
            playFrom = "queuePlay";
        } else if (audioPlay) {
            playFrom = "audioPlay";
        } else {
            playFrom = "audioPlay";
        }
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        if (queuePlay) {
            position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
            listSize = addToQueueModelList.size();
            if (addToQueueModelList.get(position).getLike().equalsIgnoreCase("1")) {
                binding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
            } else if (addToQueueModelList.get(position).getLike().equalsIgnoreCase("0")) {
                binding.ivLike.setImageResource(R.drawable.ic_unlike_icon);
            } else {
                binding.ivLike.setImageResource(R.drawable.ic_unlike_icon);
            }
        } else if (audioPlay) {
            position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
            listSize = mainPlayModelList.size();
            if (listSize != 0) {
                if (mainPlayModelList.get(position).getLike().equalsIgnoreCase("1")) {
                    binding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
                } else if (mainPlayModelList.get(position).getLike().equalsIgnoreCase("0")) {
                    binding.ivLike.setImageResource(R.drawable.ic_unlike_icon);
                } else {
                    binding.ivLike.setImageResource(R.drawable.ic_unlike_icon);
                }
                url = mainPlayModelList.get(position).getAudioFile();
            }
            try {
                myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(position).getImageFile());
            } catch (Exception e) {

            }
            if (audioRemove) {
                callButtonText(position);
                audioRemove = false;
            }
//            if (url.equalsIgnoreCase("") || url.isEmpty()) {
//                isDisclaimer = 1;
//                callAllDisable(false);
//                binding.tvNowPlaying.setText("");
//            } else {
//                binding.tvNowPlaying.setText(R.string.NOW_PLAYING_FROM);
//                isDisclaimer = 0;
//                callAllDisable(true);
//            }
        }
        if (comeFromAddToQueue) {
            if (player != null) {
                player.removeMediaItem(position);
                player.seekTo(position + 1, 0);
                player.setPlayWhenReady(true);
            }
            comeFromAddToQueue = false;
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (player != null) {
            player.setWakeMode(C.WAKE_MODE_LOCAL);
            player.setHandleWakeLock(true);
        }
        super.onPause();
//        Assertions.checkNotNull(binding.playerControlView).setPlayer(null);
    }

    private void callBack() {
        try {
//        handler1.removeCallbacks(UpdateSongTime1);
//            player = 1;
//            if (binding.llPause.getVisibility() == View.VISIBLE) {
//                isPause = false;
//            }
//        pauseMedia();
          /*  if (exoBinding.progressBar.getVisibility() == View.VISIBLE) {
                isprogressbar = true;
            }*/
            DatabaseClient
                    .getInstance(ctx)
                    .getaudioDatabase()
                    .taskDao()
                    .getaudioByPlaylist1(url, "").removeObserver(audiolist -> {
            });
            DatabaseClient.getInstance(ctx)
                    .getaudioDatabase()
                    .taskDao()
                    .getDownloadProgress1(url, "").removeObserver(downloadAudioDetails -> {
            });
            audioClick = false;
            SharedPreferences shared2 = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared2.edit();
            Gson gson = new Gson();
            String json = gson.toJson(addToQueueModelList);
            if (queuePlay) {
                editor.putString(CONSTANTS.PREF_KEY_queueList, json);
            }
            editor.putInt(CONSTANTS.PREF_KEY_position, position);
            editor.commit();
            finish();
//        overridePendingTransition(R.anim.enter, R.anim.exit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callLike() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
            Call<AudioLikeModel> listCall = APIClient.getClient().getAudioLike(id, UserID);
            listCall.enqueue(new Callback<AudioLikeModel>() {
                @Override
                public void onResponse(Call<AudioLikeModel> call, Response<AudioLikeModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
                            AudioLikeModel model = response.body();
                            if (model.getResponseData().getFlag().equalsIgnoreCase("0")) {
                                binding.ivLike.setImageResource(R.drawable.ic_heart_unfill_icon);
                            } else if (model.getResponseData().getFlag().equalsIgnoreCase("1")) {
                                binding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
                            }

                            SharedPreferences sharedxx = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                            boolean audioPlay = sharedxx.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                            int pos = sharedxx.getInt(CONSTANTS.PREF_KEY_position, 0);
                            AudioFlag = sharedxx.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                            SharedPreferences sharedq = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                            AudioFlag = sharedq.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                            Gson gsonq = new Gson();
                            String jsonq = sharedq.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gsonq));
                            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared.edit();
                            Gson gson = new Gson();
                            if (audioPlay) {
                                if (AudioFlag.equalsIgnoreCase("MainAudioList")) {
                                    Type type = new TypeToken<ArrayList<MainAudioModel.ResponseData.Detail>>() {
                                    }.getType();
                                    ArrayList<MainAudioModel.ResponseData.Detail> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("ViewAllAudioList")) {
                                    Type type = new TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail>>() {
                                    }.getType();
                                    ArrayList<ViewAllAudioListModel.ResponseData.Detail> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("SearchModelAudio")) {
                                    Type type = new TypeToken<ArrayList<SearchBothModel.ResponseData>>() {
                                    }.getType();
                                    ArrayList<SearchBothModel.ResponseData> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("SearchAudio")) {
                                    Type type = new TypeToken<ArrayList<SuggestedModel.ResponseData>>() {
                                    }.getType();
                                    ArrayList<SuggestedModel.ResponseData> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
                                    Type type = new TypeToken<ArrayList<AppointmentDetailModel.Audio>>() {
                                    }.getType();
                                    ArrayList<AppointmentDetailModel.Audio> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("LikeAudioList")) {
                                    Type type = new TypeToken<ArrayList<LikesHistoryModel.ResponseData.Audio>>() {
                                    }.getType();
                                    ArrayList<LikesHistoryModel.ResponseData.Audio> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
                                    Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
                                    }.getType();
                                    ArrayList<DownloadAudioDetails> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("Downloadlist")) {
                                    Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
                                    }.getType();
                                    ArrayList<DownloadAudioDetails> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("TopCategories")) {
                                    Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
                                    }.getType();
                                    ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("SubPlayList")) {
                                    Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
                                    }.getType();
                                    ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                }
                            }
                            if (queuePlay) {
                                addToQueueModelList.get(position).setLike(model.getResponseData().getFlag());
                            } else
                                mainPlayModelList.get(position).setLike(model.getResponseData().getFlag());

                            String json = gson.toJson(mainPlayModelList);
                            editor.putString(CONSTANTS.PREF_KEY_audioList, json);
                            if (queuePlay) {
                                String json1 = gson.toJson(addToQueueModelList);
                                editor.putString(CONSTANTS.PREF_KEY_queueList, json1);
                            }
                            editor.putInt(CONSTANTS.PREF_KEY_position, position);
                            editor.commit();
                            BWSApplication.showToast(model.getResponseMessage(), ctx);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<AudioLikeModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    private void initializePlayer() {
        try {
//        player = new SimpleExoPlayer.Builder(getApplicationContext()).build();
            isDisclaimer = 0;
            if (audioClick) {
                GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
                globalInitExoPlayer.GlobleInItPlayer(ctx, position, downloadAudioDetailsList, mainPlayModelList, "Main");
                setpleyerctrView();
            }
            if (player != null) {
                player.setWakeMode(C.WAKE_MODE_LOCAL);
                player.setHandleWakeLock(true);
                player.addListener(new ExoPlayer.EventListener() {

                    @Override
                    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                        Log.v("TAG", "Listener-onTracksChanged... ");
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
                        p.putValue("playerType", "Main");
                        p.putValue("audioService", APP_SERVICE_STATUS);
                        p.putValue("bitRate", "");
                        p.putValue("sound", String.valueOf(hundredVolume));
                        BWSApplication.addToSegment("Audio Started", p, CONSTANTS.track);
                        SharedPreferences sharedsa = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                        Gson gson = new Gson();
                        String json = sharedsa.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gson));
                        if (!json.equalsIgnoreCase(String.valueOf(gson))) {
                            Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                            }.getType();
                            mainPlayModelList = gson.fromJson(json, type);
                        }
                        player.setPlayWhenReady(true);
                    GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
                    globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList);
                        position = player.getCurrentWindowIndex();
                        myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(position).getImageFile());

                        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = shared.edit();
                        editor.putInt(CONSTANTS.PREF_KEY_position, position);
                        editor.commit();
                        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
                                || AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            Log.e("Nite Mode :", String.valueOf(AppCompatDelegate.getDefaultNightMode()));
                        }
                        UiModeManager uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
                        if (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_AUTO
                                || uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES
                                || uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_CUSTOM) {
                            uiModeManager.setNightMode(UiModeManager.MODE_NIGHT_NO);

                            Log.e("Nite Mode :", String.valueOf(uiModeManager.getNightMode()));
                        }
                        exoBinding.llPlay.setVisibility(View.GONE);
                        exoBinding.llPause.setVisibility(View.GONE);
                        // exoBinding.llProgressBar.setVisibility(View.VISIBLE);
                        exoBinding.progressBar.setVisibility(View.VISIBLE);

                        callButtonText(player.getCurrentWindowIndex());
                    }

//                @Override
//                public void onIsLoadingChanged(boolean isLoading) {
//                    isPrepared = isLoading;
//                        myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(position).getImageFile());
//                    BWSApplication.showToast("onIsLoadingChangeddddddddddd", ctx);
//                    if (isLoading) {
//                        exoBinding.llPlay.setVisibility(View.GONE);
//                        exoBinding.llPause.setVisibility(View.GONE);
//                        // exoBinding.llProgressBar.setVisibility(View.VISIBLE);
//                        exoBinding.progressBar.setVisibility(View.VISIBLE);
//                        Log.e("Isloading", "BigLoadingggggggggggggggggg");
//                    }
//                }

                    @Override
                    public void onIsPlayingChanged(boolean isPlaying) {
                        if (player.getPlaybackState() == ExoPlayer.STATE_BUFFERING) {
                            exoBinding.llPlay.setVisibility(View.GONE);
                            exoBinding.llPause.setVisibility(View.GONE);
                            exoBinding.progressBar.setVisibility(View.VISIBLE);
                        } else if (isPlaying) {
                            exoBinding.llPlay.setVisibility(View.GONE);
                            exoBinding.llPause.setVisibility(View.VISIBLE);
                            exoBinding.progressBar.setVisibility(View.GONE);
                        } else if (!isPlaying) {
                            exoBinding.llPlay.setVisibility(View.VISIBLE);
                            exoBinding.llPause.setVisibility(View.GONE);
                            exoBinding.progressBar.setVisibility(View.GONE);
                        }
                        exoBinding.exoProgress.setBufferedPosition(player.getBufferedPosition());
                        exoBinding.exoProgress.setPosition(player.getCurrentPosition());
                        exoBinding.exoProgress.setDuration(player.getDuration());
                        exoBinding.tvStartTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(player.getCurrentPosition()),
                                TimeUnit.MILLISECONDS.toSeconds(player.getCurrentPosition()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(player.getCurrentPosition()))));
                    }

              /*  @Override
                public void onPlayWhenReadyChanged(boolean playWhenReady, int state) {
                    if (state == ExoPlayer.STATE_READY && !playWhenReady) {
                        exoBinding.llPlay.setVisibility(View.VISIBLE);
                        exoBinding.llPause.setVisibility(View.GONE);
                        // exoBinding.llProgressBar.setVisibility(View.GONE);
                        exoBinding.progressBar.setVisibility(View.GONE);
                    } else if (state == ExoPlayer.STATE_READY && playWhenReady) {
                        exoBinding.llPlay.setVisibility(View.VISIBLE);
                        exoBinding.llPause.setVisibility(View.GONE);
                        // exoBinding.llProgressBar.setVisibility(View.GONE);
                        exoBinding.progressBar.setVisibility(View.GONE);
                    } else if (state == ExoPlayer.STATE_BUFFERING) {
                        exoBinding.llPlay.setVisibility(View.GONE);
                        exoBinding.llPause.setVisibility(View.GONE);
                        // exoBinding.llProgressBar.setVisibility(View.VISIBLE);
                        exoBinding.progressBar.setVisibility(View.VISIBLE);
                    }
                }*/

                    @Override
                    public void onPlaybackStateChanged(int state) {
                        if (state == ExoPlayer.STATE_READY) {
                            try {
                                myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(position).getImageFile());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
                            p.putValue("playerType", "Main");
                            p.putValue("audioService", APP_SERVICE_STATUS);
                            p.putValue("bitRate", "");
                            p.putValue("sound", String.valueOf(hundredVolume));
                            BWSApplication.addToSegment("Audio Buffer Completed", p, CONSTANTS.track);
                            if (player.getPlayWhenReady()) {
                                exoBinding.llPlay.setVisibility(View.GONE);
                                exoBinding.llPause.setVisibility(View.VISIBLE);
//                                // exoBinding.llProgressBar.setVisibility(View.GONE);
                                exoBinding.progressBar.setVisibility(View.GONE);
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
                                p.putValue("playerType", "Main");
                                p.putValue("audioService", APP_SERVICE_STATUS);
                                p.putValue("bitRate", "");
                                p.putValue("sound", String.valueOf(hundredVolume));
                                BWSApplication.addToSegment("Audio Playing", p, CONSTANTS.track);
                            } else if (!player.getPlayWhenReady()) {
                                exoBinding.llPlay.setVisibility(View.VISIBLE);
                                exoBinding.llPause.setVisibility(View.GONE);
//                                // exoBinding.llProgressBar.setVisibility(View.GONE);
                                exoBinding.progressBar.setVisibility(View.GONE);
                            }

//                        isprogressbar = false;
                        } else if (state == ExoPlayer.STATE_BUFFERING) {
                            myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(position).getImageFile());
                            exoBinding.llPlay.setVisibility(View.GONE);
                            exoBinding.llPause.setVisibility(View.GONE);
                            // exoBinding.llProgressBar.setVisibility(View.VISIBLE);
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
                            p.putValue("playerType", "Main");
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
                                p.putValue("playerType", "Main");
                                p.putValue("audioService", APP_SERVICE_STATUS);
                                p.putValue("bitRate", "");
                                p.putValue("sound", String.valueOf(hundredVolume));
                                BWSApplication.addToSegment("Audio Completed", p, CONSTANTS.track);
                                if (mainPlayModelList.get(player.getCurrentWindowIndex()).getID().
                                        equalsIgnoreCase(mainPlayModelList.get(mainPlayModelList.size() - 1).getID())) {

                                    exoBinding.llPlay.setVisibility(View.VISIBLE);
                                    exoBinding.llPause.setVisibility(View.GONE);
                                    // exoBinding.llProgressBar.setVisibility(View.GONE);
                                    exoBinding.progressBar.setVisibility(View.GONE);
                                    player.setPlayWhenReady(false);
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
                                    p.putValue("playerType", "Main");
                                    p.putValue("audioService", APP_SERVICE_STATUS);
                                    p.putValue("bitRate", "");
                                    p.putValue("sound", String.valueOf(hundredVolume));
                                    BWSApplication.addToSegment("Audio Playback Completed", p, CONSTANTS.track);
                                    Log.e("Last audio End", mainPlayModelList.get(position).getName());
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
                                } else {
                                    Log.e("Curr audio End", mainPlayModelList.get(position).getName());
                                }
                            } catch (Exception e) {

                            }
                        } else if (state == ExoPlayer.STATE_IDLE) {
                       /* GetAllMedia();
                        audioClick = true;

                        playerControlView.setPlayer(player);
                        playerControlView.setProgressUpdateListener((position, bufferedPosition) -> {
                            exoBinding.exoProgress.setPosition(position);
                            exoBinding.exoProgress.setBufferedPosition(bufferedPosition);
                        });
                        playerControlView.show();

                        Log.e("Exoplayer Idle", "my Exop in Idle");*/
                        }
                    }

                    @Override
                    public void onPlayerError(ExoPlaybackException error) {
                        Log.i("onPlaybackError", "onPlaybackError: " + error.getMessage());
                    }
                });
                exoBinding.exoProgress.addListener(new TimeBar.OnScrubListener() {
                    @Override
                    public void onScrubStart(TimeBar timeBar, long pos) {
                        exoBinding.exoProgress.setPosition(pos);
                        oldSeekPosition = pos;
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
                        p.putValue("seekPosition", pos);
                        if (oldSeekPosition < pos) {
                            p.putValue("seekDirection", "Forwarded");
                        } else if (oldSeekPosition > pos) {
                            p.putValue("seekDirection", "Backwarded");
                        } else {
                            p.putValue("seekDirection", "");
                        }
                        if (downloadAudioDetailsList.contains(mainPlayModelList.get(position).getName())) {
                            p.putValue("audioType", "Downloaded");
                        } else {
                            p.putValue("audioType", "Streaming");
                        }
                        p.putValue("source", GetSourceName(ctx));
                        p.putValue("playerType", "Main");
                        p.putValue("audioService", APP_SERVICE_STATUS);
                        p.putValue("bitRate", "");
                        p.putValue("sound", String.valueOf(hundredVolume));
                        BWSApplication.addToSegment("Audio Seek Started", p, CONSTANTS.track);
                    }

                    @Override
                    public void onScrubMove(TimeBar timeBar, long position) {

                    }

                    @Override
                    public void onScrubStop(TimeBar timeBar, long pos, boolean canceled) {
                        player.seekTo(pos);
                        exoBinding.exoProgress.setPosition(pos);
                        exoBinding.tvStartTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(pos),
                                TimeUnit.MILLISECONDS.toSeconds(pos) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(pos))));
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
                        p.putValue("seekPosition", pos);
                        if (oldSeekPosition < pos) {
                            p.putValue("seekDirection", "Forwarded");
                        } else if (oldSeekPosition > pos) {
                            p.putValue("seekDirection", "Backwarded");
                        } else {
                            p.putValue("seekDirection", "");
                        }
                        if (downloadAudioDetailsList.contains(mainPlayModelList.get(position).getName())) {
                            p.putValue("audioType", "Downloaded");
                        } else {
                            p.putValue("audioType", "Streaming");
                        }
                        p.putValue("source", GetSourceName(ctx));
                        p.putValue("playerType", "Main");
                        p.putValue("audioService", APP_SERVICE_STATUS);
                        p.putValue("bitRate", "");
                        p.putValue("sound", String.valueOf(hundredVolume));
                        BWSApplication.addToSegment("Audio Seek Completed", p, CONSTANTS.track);
                    }
                });
                callRepeatShuffle();
                if (player.getPlaybackState() == ExoPlayer.STATE_BUFFERING) {
                    exoBinding.llPlay.setVisibility(View.GONE);
                    exoBinding.llPause.setVisibility(View.GONE);
                    // exoBinding.llProgressBar.setVisibility(View.VISIBLE);
                    exoBinding.progressBar.setVisibility(View.VISIBLE);
                } else if (player.getPlayWhenReady()) {
                    exoBinding.llPlay.setVisibility(View.GONE);
                    exoBinding.llPause.setVisibility(View.VISIBLE);
                    // exoBinding.llProgressBar.setVisibility(View.GONE);
                    exoBinding.progressBar.setVisibility(View.GONE);
                } else if (!player.getPlayWhenReady()) {
                    exoBinding.llPlay.setVisibility(View.VISIBLE);
                    exoBinding.llPause.setVisibility(View.GONE);
                    // exoBinding.llProgressBar.setVisibility(View.GONE);
                    exoBinding.progressBar.setVisibility(View.GONE);
                }

                exoBinding.exoProgress.setBufferedPosition(player.getBufferedPosition());
                exoBinding.exoProgress.setPosition(player.getCurrentPosition());
                exoBinding.exoProgress.setDuration(player.getDuration());
                setpleyerctrView();
            } else {
                if (audioClick) {
                    // exoBinding.llProgressBar.setVisibility(View.GONE);
                    exoBinding.progressBar.setVisibility(View.GONE);
                    exoBinding.llPlay.setVisibility(View.GONE);
                    exoBinding.llPause.setVisibility(View.VISIBLE);
                    Log.e("newBUff", "exoBinding.progressBar.setVisibility(View.GONE);");
                } else if (PlayerINIT) {
                    // exoBinding.llProgressBar.setVisibility(View.GONE);
                    exoBinding.progressBar.setVisibility(View.GONE);
                    exoBinding.llPlay.setVisibility(View.GONE);
                    exoBinding.llPause.setVisibility(View.VISIBLE);
                    Log.e("PlayerINIT", "exoBinding.progressBar.setVisibility(View.GONE);");
                }
            }  /*else if(player == null){
            handler1.postDelayed(UpdateSongTime1, 2000);
        }*/
            callAllDisable(true);
            epAllClicks();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializePlayerDisclaimer() {
//        player = new SimpleExoPlayer.Builder(getApplicationContext()).build();
        try {
            isDisclaimer = 1;
            if (audioClick) {
                GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
                globalInitExoPlayer.GlobleInItDisclaimer(ctx, mainPlayModelList);
                setpleyerctrView();
            }

            if (player != null) {
                player.addListener(new ExoPlayer.EventListener() {
                    @Override
                    public void onPlaybackStateChanged(int state) {
                        if (state == ExoPlayer.STATE_ENDED) {
                            //player back ended
                            audioClick = true;
                            removeArray();
                            p = new Properties();
                            p.putValue("userId", UserID);
                            p.putValue("position", GetCurrentAudioPosition());
                            p.putValue("source", GetSourceName(ctx));
                            p.putValue("playerType", "Main");
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
                            p.putValue("playerType", "Main");
                            if (downloadAudioDetailsList.contains(mainPlayModelList.get(position).getName())) {
                                p.putValue("audioType", "Downloaded");
                            } else {
                                p.putValue("audioType", "Streaming");
                            }
                            p.putValue("bitRate", "");
                            p.putValue("audioService", APP_SERVICE_STATUS);
                            p.putValue("sound", String.valueOf(hundredVolume));
                            BWSApplication.addToSegment("Disclaimer Started", p, CONSTANTS.track);
                            try {
                                if (player.getPlayWhenReady()) {
                                    exoBinding.llPlay.setVisibility(View.GONE);
                                    exoBinding.llPause.setVisibility(View.VISIBLE);
//                                    // exoBinding.llProgressBar.setVisibility(View.GONE);
                                    exoBinding.progressBar.setVisibility(View.GONE);
                                    p = new Properties();
                                    p.putValue("userId", UserID);
                                    p.putValue("position", GetCurrentAudioPosition());
                                    p.putValue("source", GetSourceName(ctx));
                                    p.putValue("playerType", "Main");
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
//                                    // exoBinding.llProgressBar.setVisibility(View.GONE);
                                    exoBinding.progressBar.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                            }
//                        isprogressbar = false;
                        } else if (state == ExoPlayer.STATE_BUFFERING) {
                            exoBinding.llPlay.setVisibility(View.GONE);
                            exoBinding.llPause.setVisibility(View.GONE);
//                            // exoBinding.llProgressBar.setVisibility(View.VISIBLE);
                            exoBinding.progressBar.setVisibility(View.VISIBLE);
                        }
                    }
//                @Override
//                public void onIsLoadingChanged(boolean isLoading) {
//                    isPrepared = isLoading;
                  /*  if (isLoading) {
                        exoBinding.llPlay.setVisibility(View.GONE);
                        exoBinding.llPause.setVisibility(View.GONE);
                        // exoBinding.llProgressBar.setVisibility(View.VISIBLE);
                        exoBinding.progressBar.setVisibility(View.VISIBLE);
                        Log.e("Isloading", "BigLoadingggggggggggggggggg");
                    }*/
//                }

                    @Override
                    public void onIsPlayingChanged(boolean isPlaying) {
                        if (player.getPlaybackState() == ExoPlayer.STATE_BUFFERING) {
                            exoBinding.llPlay.setVisibility(View.GONE);
                            exoBinding.llPause.setVisibility(View.GONE);
                            exoBinding.progressBar.setVisibility(View.VISIBLE);
                        } else if (isPlaying) {
                            exoBinding.llPlay.setVisibility(View.GONE);
                            exoBinding.llPause.setVisibility(View.VISIBLE);
                            exoBinding.progressBar.setVisibility(View.GONE);
                        } else if (!isPlaying) {
                            exoBinding.llPlay.setVisibility(View.VISIBLE);
                            exoBinding.llPause.setVisibility(View.GONE);
                            exoBinding.progressBar.setVisibility(View.GONE);
                        }
                        exoBinding.exoProgress.setBufferedPosition(player.getBufferedPosition());
                        exoBinding.exoProgress.setPosition(player.getCurrentPosition());
                        exoBinding.exoProgress.setDuration(player.getDuration());
                        exoBinding.tvStartTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(player.getCurrentPosition()),
                                TimeUnit.MILLISECONDS.toSeconds(player.getCurrentPosition()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(player.getCurrentPosition()))));
                    }

                    @Override
                    public void onPlayerError(ExoPlaybackException error) {
                        Log.i("onPlaybackError", "onPlaybackError: " + error.getMessage());
                    }
                });
                if (player != null) {
                    if (player.getPlaybackState() == ExoPlayer.STATE_BUFFERING) {
                        exoBinding.llPlay.setVisibility(View.GONE);
                        exoBinding.llPause.setVisibility(View.GONE);
//                   // exoBinding.llProgressBar.setVisibility(View.VISIBLE);
                        exoBinding.progressBar.setVisibility(View.VISIBLE);
                    } else if (player.getPlayWhenReady()) {
                        exoBinding.llPlay.setVisibility(View.GONE);
                        exoBinding.llPause.setVisibility(View.VISIBLE);
//                    // exoBinding.llProgressBar.setVisibility(View.GONE);
                        exoBinding.progressBar.setVisibility(View.GONE);
                    } else if (!player.getPlayWhenReady()) {
                        exoBinding.llPlay.setVisibility(View.VISIBLE);
                        exoBinding.llPause.setVisibility(View.GONE);
//                    // exoBinding.llProgressBar.setVisibility(View.GONE);
                        exoBinding.progressBar.setVisibility(View.GONE);
                    }
                    exoBinding.exoProgress.setBufferedPosition(player.getBufferedPosition());
                    exoBinding.exoProgress.setPosition(player.getCurrentPosition());
                    exoBinding.exoProgress.setDuration(player.getDuration());
                }
                setpleyerctrView();
            } else {
                if (audioClick) {
//                // exoBinding.llProgressBar.setVisibility(View.GONE);
                    exoBinding.progressBar.setVisibility(View.GONE);
                    exoBinding.llPlay.setVisibility(View.GONE);
                    exoBinding.llPause.setVisibility(View.VISIBLE);
                    Log.e("newBUff", "exoBinding.progressBar.setVisibility(View.GONE);");
                } else if (PlayerINIT) {
//                // exoBinding.llProgressBar.setVisibility(View.GONE);
                    exoBinding.progressBar.setVisibility(View.GONE);
                    exoBinding.llPlay.setVisibility(View.GONE);
                    exoBinding.llPause.setVisibility(View.VISIBLE);
                    Log.e("PlayerINIT", "exoBinding.progressBar.setVisibility(View.GONE);");
                }
            }  /*else if(player == null) {
            if (isprogressbar) {
                exoBinding.llPlay.setVisibility(View.GONE);
                exoBinding.llPause.setVisibility(View.GONE);
                // exoBinding.llProgressBar.setVisibility(View.VISIBLE);
                exoBinding.progressBar.setVisibility(View.VISIBLE);
                if(isDisclaimer == 1) {
                    handler2.postDelayed(UpdateSongTime2, 2000);
                }
            }
        }*/

            exoBinding.llPause.setOnClickListener(view -> {
                player.setPlayWhenReady(false);
                exoBinding.llPlay.setVisibility(View.VISIBLE);
                exoBinding.llPause.setVisibility(View.GONE);
//                // exoBinding.llProgressBar.setVisibility(View.GONE);
                exoBinding.progressBar.setVisibility(View.GONE);
                p = new Properties();
                p.putValue("userId", UserID);
                p.putValue("position", GetCurrentAudioPosition());
                p.putValue("source", GetSourceName(ctx));
                p.putValue("playerType", "Main");
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

/*
        exoBinding.llPlay.setOnClickListener(view -> {
            if (player != null) {
                exoBinding.llPlay.setVisibility(View.GONE);
                exoBinding.llPause.setVisibility(View.VISIBLE);
                // exoBinding.llProgressBar.setVisibility(View.GONE);
                exoBinding.progressBar.setVisibility(View.GONE);
                player.setPlayWhenReady(true);
                p = new Properties();
                p.putValue("userId", UserID);
                p.putValue("position", GetCurrentAudioPosition());
                p.putValue("source", GetSourceName(ctx));
                p.putValue("playerType", "Main");
                if (downloadAudioDetailsList.contains(mainPlayModelList.get(position).getName())){
                    p.putValue("audioType", "Downloaded");
                }else {
                    p.putValue("audioType", "Streaming");
                }
                p.putValue("bitRate", "");
                p.putValue("sound", */
            /*GetDeviceVolume(ctx)*//*
"0");
                BWSApplication.addToSegment("Disclaimer Resumed", p, CONSTANTS.track);
            } else {
                audioClick = true;
                miniPlayer = 1;
                initializePlayerDisclaimer();
            }
        });
*/
//        MediaItem mediaItem1 = MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(R.raw.brain_wellness_spa_declaimer));
//        player.setMediaItem(mediaItem1);
            callAllDisable(false);
//        player.setMediaItems(mediaItemList, position, 0);
//        player.setPlayWhenReady(true);
//        player.prepare();
//        player.setRepeatMode(Player.REPEAT_MODE_ALL);
//        AudioPlayerActivity.player = player;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setpleyerctrView() {
        playerControlView.setPlayer(player);
        playerControlView.setProgressUpdateListener((position, bufferedPosition) -> {
            exoBinding.exoProgress.setPosition(position);
            exoBinding.exoProgress.setBufferedPosition(bufferedPosition);

            exoBinding.tvStartTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(position),
                    TimeUnit.MILLISECONDS.toSeconds(position) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(position))));
        });
        playerControlView.setFocusable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            playerControlView.setFocusedByDefault(true);
        }
        playerControlView.show();
    }

    private void epAllClicks() {
        try {
            binding.llDownload.setOnClickListener(view -> {
                if (BWSApplication.isNetworkConnected(ctx)) {
                    if (IsLock.equalsIgnoreCase("1")) {
                        Intent i = new Intent(ctx, MembershipChangeActivity.class);
                        i.putExtra("ComeFrom", "Plan");
                        ctx.startActivity(i);
                    } else if (IsLock.equalsIgnoreCase("2")) {
                        BWSApplication.showToast("Please re-activate your membership plan", ctx);
                    } else {
                        callDownload();
                    }
                } else {
                    BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                }
            });
            if (listSize == 1) {
                if (listSize == 1) {
                    exoBinding.llNext.setEnabled(false);
                    exoBinding.llPrev.setEnabled(false);
//            exoBinding.llShuffle.setEnabled(false);
                    exoBinding.llNext.setClickable(false);
                    exoBinding.llPrev.setClickable(false);
//            exoBinding.llShuffle.setClickable(false);
//            IsShuffle = "";
//            exoBinding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
                    exoBinding.ivnext.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
                    exoBinding.ivprev.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
                }
            }

            exoBinding.llPause.setOnClickListener(view -> {
                try {
                    player.setPlayWhenReady(false);
                    exoBinding.llPlay.setVisibility(View.VISIBLE);
                    exoBinding.llPause.setVisibility(View.GONE);
//                // exoBinding.llProgressBar.setVisibility(View.GONE);
                    exoBinding.progressBar.setVisibility(View.GONE);
//                p = new Properties();
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
                    p.putValue("playerType", "Main");
                    p.putValue("audioService", APP_SERVICE_STATUS);
                    p.putValue("bitRate", "");
                    p.putValue("sound", String.valueOf(hundredVolume));
                    BWSApplication.addToSegment("Audio Paused", p, CONSTANTS.track);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

/*
        exoBinding.llPlay.setOnClickListener(view -> {
            if (player != null) {
                if (mainPlayModelList.get(player.getCurrentWindowIndex()).getID().equalsIgnoreCase(mainPlayModelList.get(mainPlayModelList.size() - 1).getID())
                        && (player.getDuration() - player.getCurrentPosition() <= 20)) {
//                    playerNotificationManager.setPlayer(player);
                            player.seekTo(position, 0);
                        }
                        player.setPlayWhenReady(true);

                exoBinding.llPlay.setVisibility(View.GONE);
                exoBinding.llPause.setVisibility(View.VISIBLE);
                // exoBinding.llProgressBar.setVisibility(View.GONE);
                exoBinding.progressBar.setVisibility(View.GONE);
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
                p.putValue("playerType", "Main");
                p.putValue("audioService", APP_SERVICE_STATUS);
                p.putValue("bitRate", "");
                p.putValue("sound", */
            /*GetDeviceVolume(ctx)*//*
"0");
                BWSApplication.addToSegment("Audio Resumed", p, CONSTANTS.track);
            }
        });
*/

            exoBinding.llForwardSec.setOnClickListener(view -> {
                try {
                    if (player.getDuration() - player.getCurrentPosition() <= 30000) {
                        BWSApplication.showToast("Please Wait... ", ctx);
                    } else {
                        player.seekTo(player.getCurrentPosition() + 30000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            exoBinding.llBackWordSec.setOnClickListener(view -> {
                try {
                    if (player.getCurrentPosition() > 30000) {
                        player.seekTo(player.getCurrentPosition() - 30000);
                    } else if (player.getCurrentPosition() < 30000) {
                        player.seekTo(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            binding.llLike.setOnClickListener(view -> {
//            handler1.removeCallbacks(UpdateSongTime1);
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                callLike();
            });

            binding.llRepeat.setOnClickListener(view -> callRepeat());

            binding.llShuffle.setOnClickListener(view -> callShuffle());

            binding.llViewQueue.setOnClickListener(view -> {
//            handler1.removeCallbacks(UpdateSongTime1);
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
//            if (binding.llPause.getVisibility() == View.VISIBLE) {
//                isPause = false;
//            }
                SharedPreferences ViewQueue = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = ViewQueue.edit();
                Gson gsonx = new Gson();
                String jsonx = gsonx.toJson(addToQueueModelList);
                if (queuePlay) {
                    editor.putString(CONSTANTS.PREF_KEY_queueList, jsonx);
                }
                editor.putInt(CONSTANTS.PREF_KEY_position, position);
                editor.commit();
                Intent i = new Intent(ctx, ViewQueueActivity.class);
                i.putExtra("ComeFromQueue", "0");
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                finish();
            });

            exoBinding.llNext.setOnClickListener(view -> {
//            if(position == mainPlayModelList.size()-1) {
//                position = 0;
//            }
                DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .getaudioByPlaylist1(url, "").removeObserver(audiolist -> {
                });
                DatabaseClient.getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .getDownloadProgress1(url, "").removeObserver(downloadAudioDetails -> {
                });

                binding.llDownload.setClickable(true);
                binding.llDownload.setEnabled(true);
                binding.ivDownloads.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                binding.pbProgress.setVisibility(View.GONE);
                binding.ivDownloads.setVisibility(View.VISIBLE);
                if (player != null) {
                    if (player.hasNext()) {
                        player.next();
                        p = new Properties();
                        p.putValue("userId", UserID);
                        p.putValue("audioId", mainPlayModelList.get(player.getCurrentWindowIndex()).getID());
                        p.putValue("audioName", mainPlayModelList.get(player.getCurrentWindowIndex()).getName());
                        p.putValue("audioDuration", mainPlayModelList.get(player.getCurrentWindowIndex()).getAudioDuration());
                        if (downloadAudioDetailsList.contains(mainPlayModelList.get(position).getName())) {
                            p.putValue("audioType", "Downloaded");
                        } else {
                            p.putValue("audioType", "Streaming");
                        }
                        p.putValue("source", GetSourceName(ctx));
                        p.putValue("playerType", "Main");
                        p.putValue("audioService", APP_SERVICE_STATUS);
                        p.putValue("bitRate", "");
                        p.putValue("sound", String.valueOf(hundredVolume));
                        BWSApplication.addToSegment("Audio Next Clicked", p, CONSTANTS.track);
                    }
                }
            });

            exoBinding.llPrev.setOnClickListener(view -> {
                DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .getaudioByPlaylist1(url, "").removeObserver(audiolist -> {
                });
                DatabaseClient.getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .getDownloadProgress1(url, "").removeObserver(downloadAudioDetails -> {
                });

                binding.llDownload.setClickable(true);
                binding.llDownload.setEnabled(true);
                binding.ivDownloads.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                binding.pbProgress.setVisibility(View.GONE);
                binding.ivDownloads.setVisibility(View.VISIBLE);
                if (player != null) {
                    if (player.hasPrevious()) {
                        player.previous();
                        p = new Properties();
                        p.putValue("userId", UserID);
                        p.putValue("audioId", mainPlayModelList.get(player.getCurrentWindowIndex()).getID());
                        p.putValue("audioName", mainPlayModelList.get(player.getCurrentWindowIndex()).getName());
                        p.putValue("audioDuration", mainPlayModelList.get(player.getCurrentWindowIndex()).getAudioDuration());
                        if (downloadAudioDetailsList.contains(mainPlayModelList.get(position).getName())) {
                            p.putValue("audioType", "Downloaded");
                        } else {
                            p.putValue("audioType", "Streaming");
                        }
                        p.putValue("source", GetSourceName(ctx));
                        p.putValue("playerType", "Main");
                        p.putValue("audioService", APP_SERVICE_STATUS);
                        p.putValue("bitRate", "");
                        p.putValue("sound", String.valueOf(hundredVolume));
                        BWSApplication.addToSegment("Audio Previous Clicked", p, CONSTANTS.track);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callRepeatShuffle() {
        if (url.equalsIgnoreCase("")) {
            binding.llShuffle.setClickable(false);
            binding.llShuffle.setEnabled(false);
            binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.llRepeat.setEnabled(false);
            binding.llRepeat.setClickable(false);
            binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
            binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
           /* if (IsShuffle.equalsIgnoreCase("")) {
                if (listSize == 1) {
                    binding.llShuffle.setClickable(false);
                    binding.llShuffle.setEnabled(false);
                    binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    binding.llShuffle.setClickable(true);
                    binding.llShuffle.setEnabled(true);
                    binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                player.setShuffleModeEnabled(false);
            } else if (IsShuffle.equalsIgnoreCase("1")) {
                if (listSize == 1) {
                    binding.llShuffle.setClickable(false);
                    binding.llShuffle.setEnabled(false);
                    binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    binding.llShuffle.setClickable(true);
                    binding.llShuffle.setEnabled(true);
                    binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                player.setShuffleModeEnabled(true);
            }
*/
            if (IsRepeat.equalsIgnoreCase("")) {
                if (queuePlay) {
                    binding.llRepeat.setEnabled(false);
                    binding.llRepeat.setClickable(false);
                    binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
                    binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    binding.llRepeat.setClickable(true);
                    binding.llRepeat.setEnabled(true);
                    binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
                    binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                player.setRepeatMode(Player.REPEAT_MODE_OFF);
            } else if (IsRepeat.equalsIgnoreCase("0")) {
                if (queuePlay) {
                    binding.llRepeat.setEnabled(false);
                    binding.llRepeat.setClickable(false);
                    binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_one));
                    binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    IsRepeat = "0";
                    binding.llRepeat.setClickable(true);
                    binding.llRepeat.setEnabled(true);
                    binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_one));
                    binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                player.setRepeatMode(Player.REPEAT_MODE_ONE);
            } else if (IsRepeat.equalsIgnoreCase("1")) {
                if (queuePlay) {
                    binding.llRepeat.setEnabled(false);
                    binding.llRepeat.setClickable(false);
                    binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
                    binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    if (listSize == 1) {
                        binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                    } else {
                        binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
                    }
                    binding.llRepeat.setClickable(true);
                    binding.llRepeat.setEnabled(true);
                    binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
                }
                player.setRepeatMode(Player.REPEAT_MODE_ALL);
            }
        }
    }

    private void callShuffle() {
        if (IsShuffle.equalsIgnoreCase("")) {
            if (listSize == 1) {
                binding.llShuffle.setClickable(false);
                binding.llShuffle.setEnabled(false);
                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                IsShuffle = "1";
                player.setShuffleModeEnabled(true);
                SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putString(CONSTANTS.PREF_KEY_IsShuffle, "1");
//                editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "");
                editor.commit();
//                IsRepeat = "";
//                if (queuePlay) {
//                    binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
//                } else
//                    binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        } else if (IsShuffle.equalsIgnoreCase("1")) {
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(CONSTANTS.PREF_KEY_IsShuffle, "");
            editor.commit();
            IsShuffle = "";
            player.setShuffleModeEnabled(false);
            binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    private void callRepeat() {
        if (IsRepeat.equalsIgnoreCase("")) {
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "0");
//            if (IsShuffle.equalsIgnoreCase("1")) {
//                editor.putString(CONSTANTS.PREF_KEY_IsShuffle, "");
//            }
            editor.commit();
//            IsShuffle = "";
//            if (listSize == 1) {
//                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
//            } else
//                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            IsRepeat = "0";
            player.setRepeatMode(Player.REPEAT_MODE_ONE);
            binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_one));
            binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
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
            p.putValue("bitRate", "");
            p.putValue("audioService", APP_SERVICE_STATUS);
            p.putValue("sound", String.valueOf(hundredVolume));
            BWSApplication.addToSegment("Audio Repeated Once", p, CONSTANTS.track);
        } else if (IsRepeat.equalsIgnoreCase("0")) {
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "1");
//            editor.putString(CONSTANTS.PREF_KEY_IsShuffle, "");
            IsRepeat = "1";
//            IsShuffle = "";
            if (listSize == 1) {
                editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "");
                IsRepeat = "";
                binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
//                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
//                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            }
            editor.commit();
            player.setRepeatMode(Player.REPEAT_MODE_ALL);
            binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
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
            p.putValue("bitRate", "");
            p.putValue("audioService", APP_SERVICE_STATUS);
            p.putValue("sound", String.valueOf(hundredVolume));
            BWSApplication.addToSegment("All Audio Repeated", p, CONSTANTS.track);
        } else if (IsRepeat.equalsIgnoreCase("1")) {
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
//            editor.putString(CONSTANTS.PREF_KEY_IsShuffle, "");
            editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "");
            IsRepeat = "";
//            IsShuffle = "";
            if (listSize == 1) {
//                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else
//                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                editor.commit();
            player.setRepeatMode(Player.REPEAT_MODE_OFF);
            binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
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
            p.putValue("bitRate", "");
            p.putValue("audioService", APP_SERVICE_STATUS);
            p.putValue("sound", String.valueOf(hundredVolume));
            BWSApplication.addToSegment("Audio Repeated Off", p, CONSTANTS.track);
        }
    }

    private void callAllDisable(boolean b) {
        if (b) {
            exoBinding.llNext.setAlpha(1f);
            binding.llMore.setAlpha(1f);
            exoBinding.llPrev.setAlpha(1f);
            binding.llMore.setClickable(true);
            binding.llMore.setEnabled(true);
            exoBinding.llForwardSec.setClickable(true);
            exoBinding.llForwardSec.setEnabled(true);
            exoBinding.llForwardSec.setAlpha(1f);
            exoBinding.llBackWordSec.setClickable(true);
            exoBinding.llBackWordSec.setEnabled(true);
            exoBinding.llBackWordSec.setAlpha(1f);
            binding.llDownload.setClickable(true);
            binding.llDownload.setEnabled(true);
            binding.llDownload.setAlpha(1f);
            binding.llRepeat.setClickable(true);
            binding.llRepeat.setEnabled(true);
            binding.llRepeat.setAlpha(1f);
            binding.llShuffle.setClickable(true);
            binding.llShuffle.setEnabled(true);
            binding.llShuffle.setAlpha(1f);
            binding.llLike.setClickable(true);
            binding.llLike.setEnabled(true);
            binding.llLike.setAlpha(1f);
            exoBinding.rlSeekbar.setClickable(true);
            exoBinding.rlSeekbar.setEnabled(true);
            exoBinding.exoProgress.setClickable(true);
            exoBinding.exoProgress.setEnabled(true);
            callLLMoreViewQClicks();
//            binding.simpleSeekbar.set
        } else {
            exoBinding.llNext.setAlpha(0.6f);
            exoBinding.llPrev.setAlpha(0.6f);
            exoBinding.llForwardSec.setClickable(false);
            exoBinding.llForwardSec.setEnabled(false);
            exoBinding.llForwardSec.setAlpha(0.6f);
            exoBinding.llBackWordSec.setClickable(false);
            exoBinding.llBackWordSec.setEnabled(false);
            exoBinding.llBackWordSec.setAlpha(0.6f);
            binding.llMore.setClickable(false);
            binding.llMore.setEnabled(false);
            binding.llMore.setAlpha(0.6f);
            binding.llViewQueue.setClickable(false);
            binding.llViewQueue.setEnabled(false);
            binding.llViewQueue.setAlpha(0.6f);
            binding.llDownload.setClickable(false);
            binding.llDownload.setEnabled(false);
            binding.llDownload.setAlpha(0.6f);
            binding.llRepeat.setClickable(false);
            binding.llRepeat.setEnabled(false);
            binding.llRepeat.setAlpha(0.6f);
            binding.llShuffle.setClickable(false);
            binding.llShuffle.setEnabled(false);
            binding.llShuffle.setAlpha(0.6f);
            binding.llLike.setClickable(false);
            binding.llLike.setEnabled(false);
            binding.llLike.setAlpha(0.6f);
            exoBinding.rlSeekbar.setClickable(false);
            exoBinding.rlSeekbar.setEnabled(false);
            exoBinding.exoProgress.setClickable(false);
            exoBinding.exoProgress.setEnabled(false);
        }
    }

    private void callLLMoreViewQClicks() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            if (IsLock.equalsIgnoreCase("1")) {
                binding.llMore.setClickable(false);
                binding.llMore.setEnabled(false);
                binding.llMore.setAlpha(0.6f);
            } else if (IsLock.equalsIgnoreCase("2")) {
                binding.llMore.setClickable(false);
                binding.llMore.setEnabled(false);
                binding.llMore.setAlpha(0.6f);
            } else {
                binding.llMore.setClickable(true);
                binding.llMore.setEnabled(true);
                binding.llMore.setAlpha(1f);
            }
        } else {
            binding.llMore.setClickable(false);
            binding.llMore.setEnabled(false);
            binding.llMore.setAlpha(0.6f);
        }

        if (BWSApplication.isNetworkConnected(ctx)) {
            if (IsLock.equalsIgnoreCase("1")) {
                binding.llViewQueue.setClickable(false);
                binding.llViewQueue.setEnabled(false);
                binding.ivViewQueue.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else if (IsLock.equalsIgnoreCase("2")) {
                binding.llViewQueue.setClickable(false);
                binding.llViewQueue.setEnabled(false);
                binding.ivViewQueue.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                binding.llViewQueue.setClickable(true);
                binding.llViewQueue.setEnabled(true);
                binding.ivViewQueue.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        } else {
            binding.llViewQueue.setClickable(false);
            binding.llViewQueue.setEnabled(false);
            binding.ivViewQueue.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    private void callDownload() {
        if (!mainPlayModelList.get(position).getAudioFile().equalsIgnoreCase("")) {
            disableDownload();
            byte[] EncodeBytes = new byte[1024];
            List<String> fileNameList = new ArrayList<>();
            List<String> audioFile1 = new ArrayList<>();
            List<String> playlistId1 = new ArrayList<>();
            SharedPreferences sharedx = getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
            Gson gson1 = new Gson();
            String json = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson1));
            String json1 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson1));
            String json2 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson1));
            if (!json1.equalsIgnoreCase(String.valueOf(gson1))) {
                Type type = new TypeToken<List<String>>() {
                }.getType();
                fileNameList = gson1.fromJson(json, type);
                audioFile1 = gson1.fromJson(json1, type);
                playlistId1 = gson1.fromJson(json2, type);

            }
            audioFile1.add(mainPlayModelList.get(position).getAudioFile());
            fileNameList.add(mainPlayModelList.get(position).getName());
            playlistId1.add("");
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String nameJson = gson.toJson(fileNameList);
            String urlJson = gson.toJson(audioFile1);
            String playlistIdJson = gson.toJson(playlistId1);
            editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson);
            editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson);
            editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
            editor.commit();
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
            p.putValue("playerType", "Main");
            p.putValue("bitRate", "");
            p.putValue("audioService", APP_SERVICE_STATUS);
            p.putValue("sound", String.valueOf(hundredVolume));
            BWSApplication.addToSegment("Audio Download Started", p, CONSTANTS.track);
            if (!isDownloading) {
                DownloadMedia downloadMedia = new DownloadMedia(getApplicationContext());
                downloadMedia.encrypt1(audioFile1, fileNameList, playlistId1);
            }
            binding.pbProgress.setVisibility(View.VISIBLE);
            binding.ivDownloads.setVisibility(View.GONE);
            SaveMedia(EncodeBytes, FileUtils.getFilePath(getApplicationContext(), name));
        }
    }

    private void disableDownload() {
        binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
        binding.ivDownloads.setColorFilter(getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
        binding.llDownload.setClickable(false);
        binding.llDownload.setEnabled(false);
    }

    private void SaveMedia(byte[] EncodeBytes, String dirPath) {
        class SaveMedia extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DownloadAudioDetails downloadAudioDetails = new DownloadAudioDetails();
                if (queuePlay) {
                    downloadAudioDetails.setID(addToQueueModelList.get(position).getID());
                    downloadAudioDetails.setName(addToQueueModelList.get(position).getName());
                    downloadAudioDetails.setAudioFile(addToQueueModelList.get(position).getAudioFile());
                    downloadAudioDetails.setAudioDirection(addToQueueModelList.get(position).getAudioDirection());
                    downloadAudioDetails.setAudiomastercat(addToQueueModelList.get(position).getAudiomastercat());
                    downloadAudioDetails.setAudioSubCategory(addToQueueModelList.get(position).getAudioSubCategory());
                    downloadAudioDetails.setImageFile(addToQueueModelList.get(position).getImageFile());
                    downloadAudioDetails.setLike(addToQueueModelList.get(position).getLike());
                    downloadAudioDetails.setAudioDuration(addToQueueModelList.get(position).getAudioDuration());
                } else if (audioPlay) {
                    downloadAudioDetails.setID(mainPlayModelList.get(position).getID());
                    downloadAudioDetails.setName(mainPlayModelList.get(position).getName());
                    downloadAudioDetails.setAudioFile(mainPlayModelList.get(position).getAudioFile());
                    downloadAudioDetails.setAudioDirection(mainPlayModelList.get(position).getAudioDirection());
                    downloadAudioDetails.setAudiomastercat(mainPlayModelList.get(position).getAudiomastercat());
                    downloadAudioDetails.setAudioSubCategory(mainPlayModelList.get(position).getAudioSubCategory());
                    downloadAudioDetails.setImageFile(mainPlayModelList.get(position).getImageFile());
                    downloadAudioDetails.setLike(mainPlayModelList.get(position).getLike());
                    downloadAudioDetails.setAudioDuration(mainPlayModelList.get(position).getAudioDuration());
                }
                downloadAudioDetails.setDownload("1");
                downloadAudioDetails.setIsSingle("1");
                downloadAudioDetails.setPlaylistId("");
                downloadAudioDetails.setIsDownload("pending");
                downloadAudioDetails.setDownloadProgress(0);

                DatabaseClient.getInstance(getApplicationContext())
                        .getaudioDatabase()
                        .taskDao()
                        .insertMedia(downloadAudioDetails);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                disableDownload();
                GetMedia2();
                super.onPostExecute(aVoid);
            }
        }
        SaveMedia st = new SaveMedia();
        st.execute();
    }

    public void GetMedia2() {
        DatabaseClient
                .getInstance(ctx)
                .getaudioDatabase()
                .taskDao()
                .getaudioByPlaylist1(url, "").observe(this, audiolist -> {

            if (!url.equalsIgnoreCase("")) {
                if (audiolist.size() != 0) {
                    if (audiolist.get(0).getDownload().equalsIgnoreCase("1")) {
                        binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                        binding.llDownload.setClickable(false);
                        binding.llDownload.setEnabled(false);
                        binding.ivDownloads.setColorFilter(getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
                        binding.ivDownloads.setVisibility(View.VISIBLE);
                        binding.pbProgress.setVisibility(View.GONE);
                    } else/* if (!mainPlayModelList.get(position).getDownload().equalsIgnoreCase("")) */ {
                        binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                        binding.llDownload.setClickable(false);
                        binding.llDownload.setEnabled(false);
                        binding.ivDownloads.setColorFilter(getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
                        binding.ivDownloads.setVisibility(View.VISIBLE);
                        binding.pbProgress.setVisibility(View.GONE);
                    }
                } else/* if (!mainPlayModelList.get(position).getDownload().equalsIgnoreCase("")) */ {
                    binding.llDownload.setClickable(true);
                    binding.llDownload.setEnabled(true);
                    binding.ivDownloads.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                    binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                    binding.ivDownloads.setVisibility(View.VISIBLE);
                    binding.pbProgress.setVisibility(View.GONE);
                }
            }
            if (audiolist.size() != 0) {
                getMediaByPer();
            }
        });
       /* downloadAudioDetailsList1 = new ArrayList<>();
        class GetMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                downloadAudioDetailsList1 = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .getaudioByPlaylist(url, "");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (!url.equalsIgnoreCase("")) {
                    if (downloadAudioDetailsList1.size() != 0) {
                        if (downloadAudioDetailsList1.get(0).getDownload().equalsIgnoreCase("1")) {
                            binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                            binding.llDownload.setClickable(false);
                            binding.llDownload.setEnabled(false);
                            binding.ivDownloads.setColorFilter(getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
                        } else {
                            binding.llDownload.setClickable(true);
                            binding.llDownload.setEnabled(true);
                            binding.ivDownloads.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                            binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                        }
                        getMediaByPer();
                    } else  {
                        binding.llDownload.setClickable(true);
                        binding.llDownload.setEnabled(true);
                        binding.ivDownloads.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                        binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                    }
                }
                super.onPostExecute(aVoid);
            }
        }
        GetMedia st = new GetMedia();
        st.execute();*/
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

    private void getMediaByPer() {
        DatabaseClient.getInstance(ctx)
                .getaudioDatabase()
                .taskDao()
                .getDownloadProgress1(url, "").observe(this, downloadPercentage -> {
            if (downloadPercentage.size() != 0) {
                if (downloadPercentage.get(0).getDownloadProgress() <= 100) {
                    if (downloadPercentage.get(0).getDownloadProgress() == 100) {
                        binding.pbProgress.setVisibility(View.GONE);
                        binding.ivDownloads.setVisibility(View.VISIBLE);
//                            handler1.removeCallbacks(UpdateSongTime1);
                        DatabaseClient.getInstance(ctx)
                                .getaudioDatabase()
                                .taskDao()
                                .getDownloadProgress1(url, "").removeObserver(downloadAudioDetails -> {
                        });
                    } else {
//                        if (binding.pbProgress.getVisibility() == View.GONE) {
                        binding.pbProgress.setVisibility(View.VISIBLE);
//                        }
//                        if (binding.ivDownloads.getVisibility() == View.VISIBLE) {
                        binding.ivDownloads.setVisibility(View.GONE);
//                        }
                        binding.pbProgress.setIndeterminate(false);
                        binding.pbProgress.setProgress(downloadPercentage.get(0).getDownloadProgress());
//                            getMediaByPer();
//                             handler1.postDelayed(UpdateSongTime1, 500);
                    }
                } else {
                    binding.pbProgress.setVisibility(View.GONE);
                    binding.ivDownloads.setVisibility(View.VISIBLE);
//                        handler1.removeCallbacks(UpdateSongTime1);
                    DatabaseClient.getInstance(ctx)
                            .getaudioDatabase()
                            .taskDao()
                            .getDownloadProgress1(url, "").removeObserver(downloadAudioDetails -> {
                    });
                }
            } else {
                binding.pbProgress.setVisibility(View.GONE);
                binding.ivDownloads.setVisibility(View.VISIBLE);
                DatabaseClient.getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .getDownloadProgress1(url, "").removeObserver(downloadAudioDetails -> {
                });
            }
        });

    }

    /* private void getMediaByPer() {
         class getMediaByPer extends AsyncTask<Void, Void, Void> {
             @Override
             protected Void doInBackground(Void... voids) {
                 downloadPercentage = DatabaseClient.getInstance(ctx)
                         .getaudioDatabase()
                         .taskDao()
                         .getDownloadProgress(url, "");

                 return null;
             }

             @Override
             protected void onPostExecute(Void aVoid) {
                 if (downloadPercentage <= 100) {
                     if (downloadPercentage == 100) {
                         binding.pbProgress.setVisibility(View.GONE);
                         binding.ivDownloads.setVisibility(View.VISIBLE);
 //                        handler1.removeCallbacks(UpdateSongTime1);
                     } else {
                         binding.pbProgress.setVisibility(View.VISIBLE);
                         binding.ivDownloads.setVisibility(View.GONE);
                         binding.pbProgress.setIndeterminate(false);
                         binding.pbProgress.setProgress(downloadPercentage);
                         getMediaByPer();
 //                         handler1.postDelayed(UpdateSongTime1, 500);
                     }
                 } else {
                     binding.pbProgress.setVisibility(View.GONE);
                     binding.ivDownloads.setVisibility(View.VISIBLE);
 //                    handler1.removeCallbacks(UpdateSongTime1);
                 }

                 super.onPostExecute(aVoid);
             }
         }
         getMediaByPer st = new getMediaByPer();
         st.execute();
     }*/

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
                    e.printStackTrace();
                    descriptor = downloadMedia.decrypt(name);
                    try {
                        if (descriptor != null) {
                            fileDescriptor = FileUtils.getTempFileDescriptor1(ctx.getApplicationContext(), descriptor);
                        }
                    } catch (IOException e1) {
                        e.printStackTrace();
                    }
                }
*/

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
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            String json2 = shared.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gson));
            if (!json2.equalsIgnoreCase(String.valueOf(gson))) {
                Type type1 = new TypeToken<ArrayList<MainPlayModel>>() {
                }.getType();
                mainPlayModelList = gson.fromJson(json2, type1);
            }
        }
        url = mainPlayModelList.get(ps).getAudioFile();
        id = mainPlayModelList.get(ps).getID();
        name = mainPlayModelList.get(ps).getName();

        if (url.equalsIgnoreCase("") || url.isEmpty()) {
            isDisclaimer = 1;
            binding.tvNowPlaying.setText("");
        } else {
            binding.tvNowPlaying.setText(R.string.NOW_PLAYING_FROM);
            isDisclaimer = 0;
        }
        exoBinding.llPlay.setOnClickListener(view -> {
            if (player != null) {
                if (!mainPlayModelList.get(player.getCurrentWindowIndex()).getAudioFile().equalsIgnoreCase("")) {
                    if (mainPlayModelList.get(player.getCurrentWindowIndex()).getID().equalsIgnoreCase(mainPlayModelList.get(mainPlayModelList.size() - 1).getID())
                            && (player.getDuration() - player.getCurrentPosition() <= 20)) {
//                    playerNotificationManager.setPlayer(player);
                        player.seekTo(position, 0);
                    }
                    player.setPlayWhenReady(true);

                    exoBinding.llPlay.setVisibility(View.GONE);
                    exoBinding.llPause.setVisibility(View.VISIBLE);
                    // exoBinding.llProgressBar.setVisibility(View.GONE);
                    exoBinding.progressBar.setVisibility(View.GONE);
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
                    p.putValue("playerType", "Main");
                    p.putValue("audioService", APP_SERVICE_STATUS);
                    p.putValue("bitRate", "");
                    p.putValue("sound", String.valueOf(hundredVolume));
                    BWSApplication.addToSegment("Audio Resumed", p, CONSTANTS.track);
                } else {
                    exoBinding.llPlay.setVisibility(View.GONE);
                    exoBinding.llPause.setVisibility(View.VISIBLE);
                    // exoBinding.llProgressBar.setVisibility(View.GONE);
                    exoBinding.progressBar.setVisibility(View.GONE);
                    player.setPlayWhenReady(true);
                    p = new Properties();
                    p.putValue("userId", UserID);
                    p.putValue("position", GetCurrentAudioPosition());
                    p.putValue("source", GetSourceName(ctx));
                    p.putValue("playerType", "Main");
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
                initializePlayerDisclaimer();
            }
        });
        if (mainPlayModelList.get(ps).getPlaylistID() == null) {
            mainPlayModelList.get(ps).setPlaylistID("");
        }
        myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(ps).getImageFile());
        binding.tvName.setText(mainPlayModelList.get(ps).getName());
        if (mainPlayModelList.get(ps).getAudioDirection().equalsIgnoreCase("")) {
            binding.llDirection.setVisibility(View.GONE);
        } else {
            binding.llDirection.setVisibility(View.VISIBLE);
            binding.tvDireDesc.setText(mainPlayModelList.get(ps).getAudioDirection());
        }
        binding.tvTitle.setText(mainPlayModelList.get(ps).getAudiomastercat());
        binding.tvDesc.setText(mainPlayModelList.get(ps).getAudioSubCategory());

        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                1, 1, 0.92f, 0);
        binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        if (player == null) {
            exoBinding.tvStartTime.setText("00:00");
        }
        exoBinding.tvSongTime.setText(mainPlayModelList.get(ps).getAudioDuration());
        try {
            if (url.equalsIgnoreCase("")) {
                Glide.with(ctx).load(R.drawable.disclaimer).thumbnail(0.05f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
            } else {
                Glide.with(ctx).load(mainPlayModelList.get(ps).getImageFile()).thumbnail(0.05f)
                        .placeholder(R.drawable.disclaimer).error(R.drawable.disclaimer)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mainPlayModelList.get(ps).getLike().equalsIgnoreCase("1")) {
            binding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
        } else if (mainPlayModelList.get(ps).getLike().equalsIgnoreCase("0")) {
            binding.ivLike.setImageResource(R.drawable.ic_heart_unfill_icon);
        } else {
            binding.ivLike.setImageResource(R.drawable.ic_heart_unfill_icon);
        }
        if (!url.equalsIgnoreCase("")) {
            if (!id.equalsIgnoreCase(addToRecentPlayId)) {
                addToRecentPlay();
                Log.e("Api call recent", id);
            }
        }
        addToRecentPlayId = id;
        GetMedia2();
    }

    public List<String> GetAllMedia() {
       /* class GetTask extends AsyncTask<Void, Void, Void> {
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
//                    getPrepareShowData();
//                }
//                mainPlayModelList2 = new ArrayList<>();
//                mainPlayModelList2 = mainPlayModelList;

                *//*if (downloadAudioDetailsList.size() != 0) {
                    if (mainPlayModelList.get(position).getAudioFile().equals("")) {
//                        ismyDes = true;
//                        getPrepareShowData();
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

                super.onPostExecute(aVoid);
            }
        }
        GetTask st = new GetTask();
        st.execute();*/
        DatabaseClient
                .getInstance(this)
                .getaudioDatabase()
                .taskDao()
                .geAllDataBYDownloaded1("Complete").observe(this, audioList -> {
            downloadAudioDetailsList = audioList;
//            audioClick = true;
            MakeArray();
            DatabaseClient
                    .getInstance(this)
                    .getaudioDatabase()
                    .taskDao()
                    .geAllDataBYDownloaded1("Complete").removeObserver(audioListx -> {
            });
        });
        return downloadAudioDetailsList;
    }

    public List<String> GetAllMedia1() {
        DatabaseClient
                .getInstance(this)
                .getaudioDatabase()
                .taskDao()
                .geAllDataBYDownloaded1("Complete").observe(this, audioList -> {
            downloadAudioDetailsList = audioList;
            DatabaseClient
                    .getInstance(this)
                    .getaudioDatabase()
                    .taskDao()
                    .geAllDataBYDownloaded1("Complete").removeObserver(audioListx -> {
            });
        });
       /* class GetTask extends AsyncTask<Void, Void, Void> {
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
        GetTask st = new GetTask();
        st.execute();*/
        return downloadAudioDetailsList;
    }

    private void MakeArray() {
        audioClick = true;
        SharedPreferences Status = getSharedPreferences(CONSTANTS.PREF_KEY_Status, Context.MODE_PRIVATE);
        IsRepeat = Status.getString(CONSTANTS.PREF_KEY_IsRepeat, "");
        IsShuffle = Status.getString(CONSTANTS.PREF_KEY_IsShuffle, "");
//        showTooltiop();
        Gson gson = new Gson();
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        String json = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson));
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
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
        getPrepareShowData();
    }

    private void removeArray() {
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
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
        binding.tvDireName.setText(R.string.Directions);
        myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(position).getImageFile());
        callButtonText(position);

        if (mainPlayModelList.get(position).getAudioFile().equalsIgnoreCase("")) {
//            if(!ismyDes) {
            initializePlayerDisclaimer();
        } else {
            GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
            globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList);
            initializePlayer();
        }

        playerControlView.setPlayer(player);

        playerControlView.setProgressUpdateListener((position, bufferedPosition) -> {
            exoBinding.exoProgress.setPosition(position);
            exoBinding.exoProgress.setBufferedPosition(bufferedPosition);

            exoBinding.tvStartTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(position),
                    TimeUnit.MILLISECONDS.toSeconds(position) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(position))));
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