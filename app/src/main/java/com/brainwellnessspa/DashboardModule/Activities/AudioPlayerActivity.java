package com.brainwellnessspa.DashboardModule.Activities;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.room.Room;

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
import com.brainwellnessspa.LikeModule.Models.LikesHistoryModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.AudioDatabase;
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

import static com.brainwellnessspa.BWSApplication.MIGRATION_1_2;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.audioClick;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.miniPlayer;
import static com.brainwellnessspa.DashboardModule.Audio.AudioFragment.IsLock;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.PlayerStatus;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.addToRecentPlayId;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.downloadProgress;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.filename;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.isDownloading;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.APP_SERVICE_STATUS;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.GetCurrentAudioPosition;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.GetSourceName;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.PlayerINIT;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.getMediaBitmap;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.player;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.relesePlayer;

public class AudioPlayerActivity extends AppCompatActivity {
    public AudioManager audioManager;
    public int hundredVolume = 0, currentVolume = 0, maxVolume = 0, percent;
    public boolean downloadClick = false;
    List<String> downloadAudioDetailsList;
    List<String> downloadAudioDetailsListGloble;
    AudioPlayerCustomLayoutBinding exoBinding;
    byte[] descriptor;
    Bitmap myBitmap = null;
    List<File> filesDownloaded;
    ActivityAudioPlayerBinding binding;
    ArrayList<MainPlayModel> mainPlayModelList, mainPlayModelList2;
    ArrayList<AddToQueueModel> addToQueueModelList;
    String IsRepeat = "", IsShuffle = "", UserID, AudioFlag, id, name, url, playFrom = "", PlayerFirstLogin = "0";
    int position, listSize;
    Context ctx;
    Activity activity;
    List<String> fileNameList = new ArrayList<>(), audioFile1 = new ArrayList<>(), playlistDownloadId = new ArrayList<>();
    Boolean queuePlay, audioPlay;
    int notificationId = 1234, downloadPercentage = 0;
    List<DownloadAudioDetails> downloadAudioDetailsList1;
    FancyShowCaseView fancyShowCaseView11, fancyShowCaseView21, fancyShowCaseView31;
    FancyShowCaseQueue queue;
    PlayerControlView playerControlView;
    boolean isPrepared = false;
    Properties p;
    long oldSeekPosition = 0;
    Handler handler2,handler1;
    int counterinit = 0;
    Runnable UpdateSongTime2 = new Runnable() {
        @Override
        public void run() {
            try {
//                        for (int f = 0; f < GlobalListModel.getPlaylistSongs().size(); f++) {
                if (fileNameList.size() != 0) {
                    for (int i = 0; i < fileNameList.size(); i++) {
                        if (fileNameList.get(i).equalsIgnoreCase(mainPlayModelList.get(position).getName())) {
                            if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(mainPlayModelList.get(position).getName())) {
                                if (downloadProgress <= 100) {
                                    if (downloadProgress == 100) {
                                        binding.pbProgress.setVisibility(View.GONE);
                                        binding.ivDownloads.setVisibility(View.VISIBLE);
                                        handler2.removeCallbacks(UpdateSongTime2);
                                    } else {
                                        binding.pbProgress.setProgress(downloadProgress);
                                        binding.pbProgress.setVisibility(View.VISIBLE);
                                        binding.ivDownloads.setVisibility(View.GONE);
                                    }
                                } else {
                                    binding.pbProgress.setVisibility(View.GONE);
                                    binding.ivDownloads.setVisibility(View.VISIBLE);
                                    handler2.removeCallbacks(UpdateSongTime2);
                                    getDownloadData();
                                }
                            } else {
                                binding.pbProgress.setVisibility(View.VISIBLE);
                                binding.ivDownloads.setVisibility(View.GONE);
                            handler2.removeCallbacks(UpdateSongTime2);
                            }
                        }
                    }
                }
                handler2.postDelayed(this, 10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Runnable UpdateSongTime1 = new Runnable() {
        @Override
        public void run() {
            handler1.removeCallbacks(UpdateSongTime1);
            if (counterinit <= 3) {
                initializePlayer();
                Log.e("run  saa", "runasca");
            }
        }
    };
    AudioDatabase DB;
    private long mLastClickTime = 0;

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
        handler1 = new Handler();
        DB = Room.databaseBuilder(ctx,
                AudioDatabase.class,
                "Audio_database")
                .addMigrations(MIGRATION_1_2)
                .build();
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
        handler2 = new Handler();
        miniPlayer = 1;
        showTooltiop();
        if (audioClick) {
//            audioClick = false;
            exoBinding.llPlay.setVisibility(View.GONE);
            exoBinding.llPause.setVisibility(View.GONE);
            exoBinding.progressBar.setVisibility(View.VISIBLE);
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
            if (IsLock.equalsIgnoreCase("1")) {
                Intent i = new Intent(ctx, MembershipChangeActivity.class);
                i.putExtra("ComeFrom", "Plan");
                ctx.startActivity(i);
            } else if (IsLock.equalsIgnoreCase("2")) {
                BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
            } else {
                Intent i = new Intent(ctx, AudioDetailActivity.class);
                if (AudioFlag.equalsIgnoreCase("TopCategories")) {
                    i.putExtra("play", "TopCategories");
                } else
                    i.putExtra("play", "play");
                i.putExtra("ID", id);
                i.putExtra("position", position);
                i.putExtra("PlaylistAudioId", "");
                startActivity(i);
                finish();
            }
        });

        binding.llDisclaimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ctx);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.full_desc_layout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue_gray)));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                final TextView tvTitle = dialog.findViewById(R.id.tvTitle);
                final TextView tvDesc = dialog.findViewById(R.id.tvDesc);
                final RelativeLayout tvClose = dialog.findViewById(R.id.tvClose);
                tvTitle.setText(R.string.Disclaimer);
                tvDesc.setText(R.string.Disclaimer_text);
                dialog.setOnKeyListener((view, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        return true;
                    }
                    return false;
                });

                tvClose.setOnClickListener(view1 -> dialog.dismiss());
                dialog.show();
                dialog.setCancelable(false);
            }
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
//        callButtonText(position);
//        if (mainPlayModelList.get(position).getAudioFile().equalsIgnoreCase("")) {
//            initializePlayerDisclaimer();
//        } else {
//            initializePlayer();
//        }
        getDownloadData();
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
        SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE);
        PlayerFirstLogin = (shared1.getString(CONSTANTS.PREF_KEY_PlayerFirstLogin, "0"));

        if (PlayerFirstLogin.equalsIgnoreCase("1")) {
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

                    }).focusShape(FocusShape.ROUNDED_RECTANGLE)
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
        }
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(CONSTANTS.PREF_KEY_PlayerFirstLogin, "0");
        editor.commit();
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
            handler2.removeCallbacks(UpdateSongTime2);
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
                player.setHandleAudioBecomingNoisy(true);
                player.addListener(new ExoPlayer.EventListener() {
                    @Override
                    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                        Log.e("TAG", "Listener-onTracksChanged... ");

                        SharedPreferences sharedsa = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                        Gson gson = new Gson();
                        String json = sharedsa.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gson));
                        if (!json.equalsIgnoreCase(String.valueOf(gson))) {
                            Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                            }.getType();
                            mainPlayModelList = gson.fromJson(json, type);
                        }
                        position = player.getCurrentWindowIndex();
                        GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
                        globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList);
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
                        getDownloadData();
                        GetMediaPer();
                        callButtonText(position);
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
                    }

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
                        exoBinding.progressBar.setVisibility(View.GONE);
                    } else if (state == ExoPlayer.STATE_READY && playWhenReady) {
                        exoBinding.llPlay.setVisibility(View.VISIBLE);
                        exoBinding.llPause.setVisibility(View.GONE);
                        exoBinding.progressBar.setVisibility(View.GONE);
                    } else if (state == ExoPlayer.STATE_BUFFERING) {
                        exoBinding.llPlay.setVisibility(View.GONE);
                        exoBinding.llPause.setVisibility(View.GONE);
                        exoBinding.progressBar.setVisibility(View.VISIBLE);
                    }
                }*/

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
                            p.putValue("playerType", "Main");
                            p.putValue("audioService", APP_SERVICE_STATUS);
                            p.putValue("bitRate", "");
                            p.putValue("sound", String.valueOf(hundredVolume));
                            BWSApplication.addToSegment("Audio Buffer Completed", p, CONSTANTS.track);
                            if (player.getPlayWhenReady()) {
                                exoBinding.llPlay.setVisibility(View.GONE);
                                exoBinding.llPause.setVisibility(View.VISIBLE);
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
                                exoBinding.progressBar.setVisibility(View.GONE);
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
                                e.printStackTrace();
                                Log.e("End State: ", e.getMessage());
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
                        Log.e("onPlaybackError", "onPlaybackError: " + error.getMessage());
                        if (error.type == ExoPlaybackException.TYPE_SOURCE) {
                            Log.e("onPlaybackError", "onPlaybackError: " + error.getSourceException().getMessage());
                        }
                        if (error.type == ExoPlaybackException.TYPE_RENDERER) {
                            Log.e("onPlaybackError", "onPlaybackError: " + error.getRendererException().getMessage());
                        }
                        if (error.type == ExoPlaybackException.TYPE_UNEXPECTED) {
                            Log.e("onPlaybackError", "onPlaybackError: " + error.getUnexpectedException().getMessage());
                        }
                        if (error.type == ExoPlaybackException.TYPE_REMOTE) {
                            Log.e("onPlaybackError", "onPlaybackError: " + error.getMessage());
                        }
                        if (error.type == ExoPlaybackException.TYPE_OUT_OF_MEMORY) {
                            Log.e("onPlaybackError", "onPlaybackError: " + error.getOutOfMemoryError().getMessage());
                        }
                        if (error.type == ExoPlaybackException.TYPE_TIMEOUT) {
                            Log.e("onPlaybackError", "onPlaybackError: " + error.getTimeoutException().getMessage());
                        }
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
                        player.seekTo(position, pos);
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
                setpleyerctrView();

                handler1.removeCallbacks(UpdateSongTime1);
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
                handler1.postDelayed(UpdateSongTime1, 2000);
                callRepeatShuffle();
            }
            callAllDisable(true);
            epAllClicks();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("init player State: ", e.getMessage());
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
                            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared.edit();
                            editor.putString(CONSTANTS.PREF_KEY_IsDisclimer, "0");
                            editor.commit();
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
                                    exoBinding.progressBar.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
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
                  /*  if (isLoading) {
                        exoBinding.llPlay.setVisibility(View.GONE);
                        exoBinding.llPause.setVisibility(View.GONE);
                        exoBinding.progressBar.setVisibility(View.VISIBLE);
                        Log.e("Isloading", "BigLoadingggggggggggggggggg");
                    }*/
//                }

                    @Override
                    public void onIsPlayingChanged(boolean isPlaying) {
                        if (player != null) {
                            myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(position).getImageFile());
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
                }
                setpleyerctrView();
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
            }

            exoBinding.llPause.setOnClickListener(view -> {
                if (player != null) {
                    player.setPlayWhenReady(false);
                    exoBinding.llPlay.setVisibility(View.VISIBLE);
                    exoBinding.llPause.setVisibility(View.GONE);
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
                }
            });
            callAllDisable(false);
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
            if (listSize == 1) {
                exoBinding.llNext.setEnabled(false);
                exoBinding.llPrev.setEnabled(false);
//            exoBinding.llShuffle.setEnabled(false);
//            exoBinding.llShuffle.setClickable(false);
//            IsShuffle = "";
//            exoBinding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
                exoBinding.llNext.setAlpha(0.6f);
                exoBinding.llPrev.setAlpha(0.6f);
            }

            exoBinding.llPause.setOnClickListener(view -> {
                try {
                    player.setPlayWhenReady(false);
                    int pss = player.getCurrentWindowIndex();
                    myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(pss).getImageFile());
                    exoBinding.llPlay.setVisibility(View.VISIBLE);
                    exoBinding.llPause.setVisibility(View.GONE);
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

            exoBinding.llForwardSec.setOnClickListener(view -> {
                try {
                    if (player.getDuration() - player.getCurrentPosition() <= 30000) {
                        BWSApplication.showToast("Please Wait... ", ctx);
                    } else {
                        player.seekTo(position, player.getCurrentPosition() + 30000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            exoBinding.llBackWordSec.setOnClickListener(view -> {
                try {
                    if (player.getCurrentPosition() > 30000) {
                        player.seekTo(position, player.getCurrentPosition() - 30000);
                    } else if (player.getCurrentPosition() < 30000) {
                        player.seekTo(position, 0);
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
                try {
                    if (player != null) {
                        GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
                        globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList);
                        int pss = player.getCurrentWindowIndex();
                        myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(pss).getImageFile());

                        if (player.hasNext()) {
                            handler2.removeCallbacks(UpdateSongTime2);
                            DatabaseClient
                                    .getInstance(ctx)
                                    .getaudioDatabase()
                                    .taskDao()
                                    .getaudioByPlaylist1(url, "").removeObserver(audiolist -> {
                            });
                            enableDownload();
                            binding.ivDownloads.setVisibility(View.VISIBLE);
                            binding.pbProgress.setVisibility(View.GONE);
                            callButtonText(position + 1);
                            player.next();
                            p = new Properties();
                            p.putValue("userId", UserID);
                            p.putValue("audioId", mainPlayModelList.get(pss).getID());
                            p.putValue("audioName", mainPlayModelList.get(pss).getName());
                            p.putValue("audioDuration", mainPlayModelList.get(pss).getAudioDuration());
                            if (downloadAudioDetailsList.contains(mainPlayModelList.get(pss).getName())) {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            exoBinding.llPrev.setOnClickListener(view -> {
                try {
                    if (player != null) {
                        GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
                        globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList);
                        int pss = player.getCurrentWindowIndex();
                        myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(pss).getImageFile());
                        if (player.hasPrevious()) {
                            DatabaseClient
                                    .getInstance(ctx)
                                    .getaudioDatabase()
                                    .taskDao()
                                    .getaudioByPlaylist1(url, "").removeObserver(audiolist -> {
                            });
                            handler2.removeCallbacks(UpdateSongTime2);
                            enableDownload();
                            binding.ivDownloads.setVisibility(View.VISIBLE);
                            binding.pbProgress.setVisibility(View.GONE);
                            callButtonText(position - 1);
                            player.previous();
                            p = new Properties();
                            p.putValue("userId", UserID);
                            p.putValue("audioId", mainPlayModelList.get(pss).getID());
                            p.putValue("audioName", mainPlayModelList.get(pss).getName());
                            p.putValue("audioDuration", mainPlayModelList.get(pss).getAudioDuration());
                            if (downloadAudioDetailsList.contains(mainPlayModelList.get(pss).getName())) {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ep all State: ", e.getMessage());
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
                if (player != null) {
                    player.setRepeatMode(Player.REPEAT_MODE_OFF);
                }
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
                if (player != null) {
                    player.setRepeatMode(Player.REPEAT_MODE_ONE);
                }
            } else if (IsRepeat.equalsIgnoreCase("1")) {
                if (queuePlay) {
                    binding.llRepeat.setEnabled(false);
                    binding.llRepeat.setClickable(false);
                    binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
                    binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    /*if (listSize == 1) {
                        binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                    } else {*/
                    binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
//                    }
                    binding.llRepeat.setClickable(true);
                    binding.llRepeat.setEnabled(true);
                    binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
                }
                if (player != null) {
                    player.setRepeatMode(Player.REPEAT_MODE_ALL);
                }
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
            if (player != null) {
                player.setRepeatMode(Player.REPEAT_MODE_ONE);
            }
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
            if (player != null) {
                player.setRepeatMode(Player.REPEAT_MODE_ALL);
            }
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
            if (player != null) {
                player.setRepeatMode(Player.REPEAT_MODE_OFF);
            }
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
            exoBinding.llNext.setClickable(true);
            exoBinding.llNext.setEnabled(true);
            exoBinding.llPrev.setClickable(true);
            exoBinding.llPrev.setEnabled(true);
            exoBinding.llNext.setAlpha(1f);
            exoBinding.llPrev.setAlpha(1f);
            binding.llMore.setAlpha(1f);
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
            exoBinding.llNext.setClickable(false);
            exoBinding.llNext.setEnabled(false);
            exoBinding.llPrev.setClickable(false);
            exoBinding.llPrev.setEnabled(false);
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
                binding.llMore.setClickable(true);
                binding.llMore.setEnabled(true);
                binding.llMore.setAlpha(1f);
            } else if (IsLock.equalsIgnoreCase("2")) {
                binding.llMore.setClickable(true);
                binding.llMore.setEnabled(true);
                binding.llMore.setAlpha(1f);
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
        downloadClick = true;
        if (downloadAudioDetailsList.contains(mainPlayModelList.get(position).getName())) {
            disableDownload();
            SaveMedia(100);
        } else {
            fileNameList = new ArrayList<>();
            audioFile1 = new ArrayList<>();
            playlistDownloadId = new ArrayList<>();
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
                playlistDownloadId = gson1.fromJson(json2, type);
            }
            audioFile1.add(mainPlayModelList.get(position).getAudioFile());
            fileNameList.add(mainPlayModelList.get(position).getName());
            playlistDownloadId.add("");
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String nameJson = gson.toJson(fileNameList);
            String urlJson = gson.toJson(audioFile1);
            String playlistIdJson = gson.toJson(playlistDownloadId);
            editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson);
            editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson);
            editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
            editor.commit();
            if (!isDownloading) {
                isDownloading = true;
                DownloadMedia downloadMedia = new DownloadMedia(getApplicationContext());
                downloadMedia.encrypt1(audioFile1, fileNameList, playlistDownloadId);
            }
            binding.pbProgress.setVisibility(View.VISIBLE);
            binding.ivDownloads.setVisibility(View.GONE);
            GetMediaPer();
            disableDownload();
            SaveMedia(0);
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
            // }
        }
    }

    private void disableDownload() {
        binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
        binding.ivDownloads.setColorFilter(getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
        binding.llDownload.setClickable(false);
        binding.llDownload.setEnabled(false);
    }

    private void enableDownload() {
        binding.llDownload.setClickable(true);
        binding.llDownload.setEnabled(true);
        binding.ivDownloads.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
        binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
    }

    private void SaveMedia(int progressx) {
        downloadClick = true;
    /*    class SaveMedia extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DownloadAudioDetails downloadAudioDetails = new DownloadAudioDetails();
//                if (audioPlay) {
                    downloadAudioDetails.setID(mainPlayModelList.get(position).getID());
                    downloadAudioDetails.setName(mainPlayModelList.get(position).getName());
                    downloadAudioDetails.setAudioFile(mainPlayModelList.get(position).getAudioFile());
                    downloadAudioDetails.setAudioDirection(mainPlayModelList.get(position).getAudioDirection());
                    downloadAudioDetails.setAudiomastercat(mainPlayModelList.get(position).getAudiomastercat());
                    downloadAudioDetails.setAudioSubCategory(mainPlayModelList.get(position).getAudioSubCategory());
                    downloadAudioDetails.setImageFile(mainPlayModelList.get(position).getImageFile());
                    downloadAudioDetails.setLike(mainPlayModelList.get(position).getLike());
                    downloadAudioDetails.setAudioDuration(mainPlayModelList.get(position).getAudioDuration());
               *//* } else if (queuePlay) {
                    downloadAudioDetails.setID(addToQueueModelList.get(position).getID());
                    downloadAudioDetails.setName(addToQueueModelList.get(position).getName());
                    downloadAudioDetails.setAudioFile(addToQueueModelList.get(position).getAudioFile());
                    downloadAudioDetails.setAudioDirection(addToQueueModelList.get(position).getAudioDirection());
                    downloadAudioDetails.setAudiomastercat(addToQueueModelList.get(position).getAudiomastercat());
                    downloadAudioDetails.setAudioSubCategory(addToQueueModelList.get(position).getAudioSubCategory());
                    downloadAudioDetails.setImageFile(addToQueueModelList.get(position).getImageFile());
                    downloadAudioDetails.setLike(addToQueueModelList.get(position).getLike());
                    downloadAudioDetails.setAudioDuration(addToQueueModelList.get(position).getAudioDuration());
                }*//*
                downloadAudioDetails.setDownload("1");
                downloadAudioDetails.setIsSingle("1");
                downloadAudioDetails.setPlaylistId("");
                if (progressx == 0) {
                    downloadAudioDetails.setIsDownload("pending");
                } else {
                    downloadAudioDetails.setIsDownload("Complete");
                }
                downloadAudioDetails.setDownloadProgress(progressx);
                DatabaseClient.getInstance(activity)
                        .getaudioDatabase()
                        .taskDao()
                        .insertMedia(downloadAudioDetails);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                BWSApplication.hideProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
                GetMediaPer();
                disableDownload();
                super.onPostExecute(aVoid);
            }
        }
        SaveMedia st = new SaveMedia();
        st.execute();*/
        DownloadAudioDetails downloadAudioDetails = new DownloadAudioDetails();
//                if (audioPlay) {
        downloadAudioDetails.setID(mainPlayModelList.get(position).getID());
        downloadAudioDetails.setName(mainPlayModelList.get(position).getName());
        downloadAudioDetails.setAudioFile(mainPlayModelList.get(position).getAudioFile());
        downloadAudioDetails.setAudioDirection(mainPlayModelList.get(position).getAudioDirection());
        downloadAudioDetails.setAudiomastercat(mainPlayModelList.get(position).getAudiomastercat());
        downloadAudioDetails.setAudioSubCategory(mainPlayModelList.get(position).getAudioSubCategory());
        downloadAudioDetails.setImageFile(mainPlayModelList.get(position).getImageFile());
        downloadAudioDetails.setLike(mainPlayModelList.get(position).getLike());
        downloadAudioDetails.setAudioDuration(mainPlayModelList.get(position).getAudioDuration());
               /* } else if (queuePlay) {
            downloadAudioDetails.setID(addToQueueModelList.get(position).getID());
            downloadAudioDetails.setName(addToQueueModelList.get(position).getName());
            downloadAudioDetails.setAudioFile(addToQueueModelList.get(position).getAudioFile());
            downloadAudioDetails.setAudioDirection(addToQueueModelList.get(position).getAudioDirection());
            downloadAudioDetails.setAudiomastercat(addToQueueModelList.get(position).getAudiomastercat());
            downloadAudioDetails.setAudioSubCategory(addToQueueModelList.get(position).getAudioSubCategory());
            downloadAudioDetails.setImageFile(addToQueueModelList.get(position).getImageFile());
            downloadAudioDetails.setLike(addToQueueModelList.get(position).getLike());
            downloadAudioDetails.setAudioDuration(addToQueueModelList.get(position).getAudioDuration());
        }*/
        downloadAudioDetails.setDownload("1");
        downloadAudioDetails.setIsSingle("1");
        downloadAudioDetails.setPlaylistId("");
        if (progressx == 0) {
            downloadAudioDetails.setIsDownload("pending");
        } else {
            downloadAudioDetails.setIsDownload("Complete");
        }
        downloadAudioDetails.setDownloadProgress(progressx);
        AudioDatabase.databaseWriteExecutor.execute(() -> DB.taskDao().insertMedia(downloadAudioDetails));
    }

    public void GetMedia2() {
        DB.taskDao().getaudioByPlaylist1(mainPlayModelList.get(position).getAudioFile(), "").observe(this, audiolist -> {
            if (audiolist.size() != 0) {
//                binding.ivDownloads.setVisibility(View.VISIBLE);
//                    binding.pbProgress.setVisibility(View.GONE);
                disableDownload();
                if (audiolist.get(0).getDownloadProgress() == 100) {
                    binding.ivDownloads.setVisibility(View.VISIBLE);
                    binding.pbProgress.setVisibility(View.GONE);
                } else {
                    binding.ivDownloads.setVisibility(View.GONE);
                    binding.pbProgress.setVisibility(View.VISIBLE);
                    GetMediaPer();
                }
              DB.taskDao().getaudioByPlaylist1(mainPlayModelList.get(position).getAudioFile(), "").removeObserver(audiolistx -> {});
            } else {
               /* boolean entryNot = false;
                for (int i = 0; i < fileNameList.size(); i++) {
                    if (fileNameList.get(i).equalsIgnoreCase(mainPlayModelList.get(position).getName())
                            && playlistDownloadId.get(i).equalsIgnoreCase("")) {
                        entryNot = true;
                        break;
                    }
                }
                if (!entryNot) {*/
                enableDownload();
                binding.ivDownloads.setVisibility(View.VISIBLE);
                binding.pbProgress.setVisibility(View.GONE);
            /*    } else {
                    GetMediaPer();
                    disableDownload();
                }*/
                DB.taskDao().getaudioByPlaylist1(mainPlayModelList.get(position).getAudioFile(), "").removeObserver(audiolistx -> {
                });
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
                    } else {
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

    private void callButtonText(int ps) {
        getDownloadData();
/*        if (!downloadAudioDetailsList.contains(mainPlayModelList.get(position).getName())) {
            fileNameList = new ArrayList<>();
            audioFile1 = new ArrayList<>();
            playlistDownloadId = new ArrayList<>();
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
                playlistDownloadId = gson1.fromJson(json2, type);
            }
        }else {

        }*/
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
            GetMediaPer();
            GetMedia2();
            binding.tvNowPlaying.setText(R.string.NOW_PLAYING_FROM);
            isDisclaimer = 0;
        }
        binding.llDownload.setOnClickListener(view -> {
            if (BWSApplication.isNetworkConnected(ctx)) {
                if (IsLock.equalsIgnoreCase("1")) {
                    Intent i = new Intent(ctx, MembershipChangeActivity.class);
                    i.putExtra("ComeFrom", "Plan");
                    ctx.startActivity(i);
                } else if (IsLock.equalsIgnoreCase("2")) {
                    BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
                } else {
                    callDownload();
                }
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), ctx);
            }
        });
        exoBinding.llPlay.setOnClickListener(view -> {
            if (player != null) {
                if (!mainPlayModelList.get(position).getAudioFile().equalsIgnoreCase("")) {
                    if (mainPlayModelList.get(player.getCurrentWindowIndex()).getID().equalsIgnoreCase(mainPlayModelList.get(mainPlayModelList.size() - 1).getID())
                            && (player.getDuration() - player.getCurrentPosition() <= 20)) {
                        player.seekTo(position, 0);
                    }
                    player.setPlayWhenReady(true);
                    int pss = player.getCurrentWindowIndex();
                    myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(pss).getImageFile());
                    exoBinding.llPlay.setVisibility(View.GONE);
                    exoBinding.llPause.setVisibility(View.VISIBLE);
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
    }

    private void GetMediaPer() {
        if (fileNameList.size() != 0) {
            for (int i = 0; i < fileNameList.size(); i++) {
                if (fileNameList.get(i).equalsIgnoreCase(mainPlayModelList.get(position).getName()) && playlistDownloadId.get(i).equalsIgnoreCase("")) {
                    if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(mainPlayModelList.get(position).getName())) {
                        if (downloadProgress <= 100) {
                            if (downloadProgress == 100) {
                                binding.pbProgress.setVisibility(View.GONE);
                                binding.ivDownloads.setVisibility(View.VISIBLE);
                                disableDownload();
                                handler2.removeCallbacks(UpdateSongTime2);
                            } else {
                                binding.pbProgress.setProgress(downloadProgress);
                                binding.pbProgress.setVisibility(View.VISIBLE);
                                binding.ivDownloads.setVisibility(View.GONE);
                                disableDownload();
                                handler2.postDelayed(UpdateSongTime2, 10000);
                            }
                        } else {
                            binding.pbProgress.setVisibility(View.GONE);
                            binding.ivDownloads.setVisibility(View.VISIBLE);
                            disableDownload();
                            handler2.removeCallbacks(UpdateSongTime2);
                        }
                    } else {
                        binding.pbProgress.setVisibility(View.VISIBLE);
                        binding.pbProgress.setProgress(0);
                        binding.ivDownloads.setVisibility(View.GONE);
                        disableDownload();
                        handler2.postDelayed(UpdateSongTime2, 10000);
                    }
                }
            }
        } else {
            binding.pbProgress.setVisibility(View.GONE);
            binding.ivDownloads.setVisibility(View.VISIBLE);
            handler2.removeCallbacks(UpdateSongTime2);
        }
    }

    private void getDownloadData() {
        try {
            SharedPreferences sharedy = getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
            Gson gson = new Gson();
            String jsony = sharedy.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson));
            String json1 = sharedy.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson));
            String jsonq = sharedy.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson));
            if (!jsony.equalsIgnoreCase(String.valueOf(gson))) {
                Type type = new TypeToken<List<String>>() {
                }.getType();
                fileNameList = gson.fromJson(jsony, type);
                playlistDownloadId = gson.fromJson(jsonq, type);
                if (fileNameList.contains(mainPlayModelList.get(position).getName())) {
                    handler2.postDelayed(UpdateSongTime2, 10000);
                    GetMediaPer();
                }
            } else {
                fileNameList = new ArrayList<>();
                playlistDownloadId = new ArrayList<>();
//                remainAudio = new ArrayList<>();
                handler2.removeCallbacks(UpdateSongTime2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> GetAllMedia() {
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
                 getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                MakeArray();
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
            if (!downloadClick) {
                MakeArray();
            }
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
        DatabaseClient
                .getInstance(this)
                .getaudioDatabase()
                .taskDao()
                .geAllDataBYDownloaded1("Complete").removeObserver(audioListx -> {
        });

        audioClick = true;
        SharedPreferences Status = getSharedPreferences(CONSTANTS.PREF_KEY_Status, Context.MODE_PRIVATE);
        IsRepeat = Status.getString(CONSTANTS.PREF_KEY_IsRepeat, "");
        IsShuffle = Status.getString(CONSTANTS.PREF_KEY_IsShuffle, "");
//        showTooltiop(); no need
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
//        if(!BWSApplication.isNetworkConnected(ctx)){
        relesePlayer();
//        }
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
        callButtonText(position);
        SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        String IsPlayDisclimer = (shared1.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1"));
        if (mainPlayModelList.get(position).getAudioFile().equalsIgnoreCase("")) {
//            if(!ismyDes) {
            if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                initializePlayerDisclaimer();
            } else {
                removeArray();
            }
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