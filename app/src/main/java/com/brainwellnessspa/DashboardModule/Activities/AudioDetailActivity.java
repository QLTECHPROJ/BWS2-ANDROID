package com.brainwellnessspa.DashboardModule.Activities;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Adapters.DirectionAdapter;
import com.brainwellnessspa.DashboardModule.Models.AddToQueueModel;
import com.brainwellnessspa.DashboardModule.Models.AppointmentDetailModel;
import com.brainwellnessspa.DashboardModule.Models.AudioLikeModel;
import com.brainwellnessspa.DashboardModule.Models.DirectionModel;
import com.brainwellnessspa.DashboardModule.Models.MainAudioModel;
import com.brainwellnessspa.DashboardModule.Models.SearchBothModel;
import com.brainwellnessspa.DashboardModule.Models.SubPlayListModel;
import com.brainwellnessspa.DashboardModule.Models.SucessModel;
import com.brainwellnessspa.DashboardModule.Models.SuggestedModel;
import com.brainwellnessspa.DashboardModule.Models.ViewAllAudioListModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia;
import com.brainwellnessspa.LikeModule.Activities.LikeActivity;
import com.brainwellnessspa.LikeModule.Models.LikesHistoryModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.AudioDatabase;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.Services.GlobalInitExoPlayer;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivityQueueBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.segment.analytics.Properties;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.BWSApplication.MIGRATION_1_2;
import static com.brainwellnessspa.DashboardModule.Activities.MyPlaylistActivity.ComeFindAudio;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.isDownloading;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.APP_SERVICE_STATUS;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.GetCurrentAudioPosition;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.GetSourceName;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.notificationId;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.player;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.relesePlayer;

public class AudioDetailActivity extends AppCompatActivity {
    public AudioManager audioManager;
    ActivityQueueBinding binding;
    String play, UserID, PlaylistId, AudioId, Like, Download, IsRepeat, IsShuffle, myPlaylist = "", comeFrom = "", audioFileName,
            AudioFile = "", PlaylistAudioId = "", AudioFlag;
    Context ctx;
    List<String> fileNameList = new ArrayList<>(), playlistDownloadId = new ArrayList<>();
    Activity activity;
    ArrayList<AddToQueueModel> addToQueueModelList;
    ArrayList<MainPlayModel> mainPlayModelList;
    ArrayList<SubPlayListModel.ResponseData.PlaylistSong> mData;
    ArrayList<DownloadAudioDetails> mDataDownload;
    ArrayList<LikesHistoryModel.ResponseData.Audio> mDataLike;
    AddToQueueModel addToQueueModel;
    Boolean queuePlay, audioPlay;
    List<DownloadAudioDetails> oneAudioDetailsList;
    SharedPreferences shared;
    List<String> downloadAudioDetailsList;
    private int numStarted = 0;
    int stackStatus = 0;
    boolean myBackPress = false;
    Properties p;
    AudioDatabase DB;
    int position = 0, listSize, playerpos, hundredVolume = 0, currentVolume = 0, maxVolume = 0, percent;
    //    Handler handler1;
//    List<String> fileNameList;
    private long mLastClickTime = 0;

/*
    private Runnable UpdateSongTime1 = new Runnable() {
        @Override
        public void run() {
            if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(audioFileName)) {
                if (downloadProgress <= 100) {
                    binding.pbProgress.setProgress(downloadProgress);
                    binding.pbProgress.setVisibility(View.VISIBLE);
                    binding.ivDownloads.setVisibility(View.GONE);
                } else {
                    binding.pbProgress.setVisibility(View.GONE);
                    binding.ivDownloads.setVisibility(View.VISIBLE);
                    handler1.removeCallbacks(UpdateSongTime1);
                }
            } else {
                binding.pbProgress.setVisibility(View.GONE);
                binding.ivDownloads.setVisibility(View.VISIBLE);
                binding.ivDownloads.setColorFilter(getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
                handler1.removeCallbacks(UpdateSongTime1);
            }
            handler1.postDelayed(this, 500);
        }
    };
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_queue);
        ctx = AudioDetailActivity.this;
        activity = AudioDetailActivity.this;
        oneAudioDetailsList = new ArrayList<>();
        downloadAudioDetailsList = new ArrayList<>();
//        handler1 = new Handler();
//        fileNameList = new ArrayList<>();
        addToQueueModelList = new ArrayList<>();
        mainPlayModelList = new ArrayList<>();
        mData = new ArrayList<>();

        DB = Room.databaseBuilder(ctx,
                AudioDatabase.class,
                "Audio_database")
                .addMigrations(MIGRATION_1_2)
                .build();
        audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        percent = 100;
        hundredVolume = (int) (currentVolume * percent) / maxVolume;
        /*SharedPreferences sharedx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
        Gson gson1 = new Gson();
        String json11 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson1));
        if (!json11.equalsIgnoreCase(String.valueOf(gson1))) {
            Type type = new TypeToken<List<String>>() {
            }.getType();
//            fileNameList = gson1.fromJson(json11, type);
        }*/

        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = shared.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gson));
        String json1 = shared.getString(CONSTANTS.PREF_KEY_queueList, String.valueOf(gson));
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        myPlaylist = shared.getString(CONSTANTS.PREF_KEY_myPlaylist, "");
        PlaylistId = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
        if (!json.equalsIgnoreCase(String.valueOf(gson))) {
            Type type = new TypeToken<ArrayList<MainPlayModel>>() {
            }.getType();
            mainPlayModelList = gson.fromJson(json, type);
        }
        if (!json1.equalsIgnoreCase(String.valueOf(gson))) {
            Type type1 = new TypeToken<ArrayList<AddToQueueModel>>() {
            }.getType();
            addToQueueModelList = gson.fromJson(json1, type1);
        }

        getDownloadData();
        GetAllMediaDownload();
        SharedPreferences Status = getSharedPreferences(CONSTANTS.PREF_KEY_Status, Context.MODE_PRIVATE);
        IsRepeat = Status.getString(CONSTANTS.PREF_KEY_IsRepeat, "");
        IsShuffle = Status.getString(CONSTANTS.PREF_KEY_IsShuffle, "");
        queuePlay = shared.getBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
        audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);

        if (getIntent().getExtras() != null) {
            AudioId = getIntent().getStringExtra(CONSTANTS.ID);
            position = getIntent().getIntExtra(CONSTANTS.position, 0);
        }
        if (getIntent().hasExtra("PlaylistAudioId")) {
            PlaylistAudioId = getIntent().getStringExtra("PlaylistAudioId");
        }
      /*  if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(audioFileName)) {
            handler1.postDelayed(UpdateSongTime1, 500);
        } else {
            binding.pbProgress.setVisibility(View.GONE);
            handler1.removeCallbacks(UpdateSongTime1);
        }*/
        if (getIntent().hasExtra("play")) {
            play = getIntent().getStringExtra("play");
        } else {
            play = "";
        }

        if (getIntent().hasExtra("comeFrom")) {
            comeFrom = getIntent().getStringExtra("comeFrom");
            position = getIntent().getIntExtra("position", 0);
            if (comeFrom.equalsIgnoreCase("myDownloadPlaylist")) {
                String js1 = getIntent().getStringExtra("data");
                Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
                }.getType();
                mDataDownload = gson.fromJson(js1, type);
            } else if (comeFrom.equalsIgnoreCase("myLikeAudioList")) {
                String js1 = getIntent().getStringExtra("data");
                Type type = new TypeToken<ArrayList<LikesHistoryModel.ResponseData.Audio>>() {
                }.getType();
                mDataLike = gson.fromJson(js1, type);
            } else {
                mData = getIntent().getParcelableArrayListExtra("data");
            }
        } else {
            comeFrom = "";
        }
        playerpos = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
        p = new Properties();
        p.putValue("userId", UserID);
        if (!comeFrom.equalsIgnoreCase("")) {
            if (comeFrom.equalsIgnoreCase("myDownloadPlaylist")) {
                if (downloadAudioDetailsList.contains(mDataDownload.get(position).getName())) {
                    p.putValue("audioType", "Downloaded");
                } else {
                    p.putValue("audioType", "Streaming");
                }
                p.putValue("audioId", mDataDownload.get(position).getID());
                p.putValue("audioName", mDataDownload.get(position).getName());
                p.putValue("audioDescription", "");
                p.putValue("directions", mDataDownload.get(position).getAudioDirection());
                p.putValue("masterCategory", mDataDownload.get(position).getAudiomastercat());
                p.putValue("subCategory", mDataDownload.get(position).getAudioSubCategory());
                p.putValue("audioDuration", mDataDownload.get(position).getAudioDuration());
            } else if (comeFrom.equalsIgnoreCase("myLikeAudioList")) {
                if (downloadAudioDetailsList.contains(mDataLike.get(position).getName())) {
                    p.putValue("audioType", "Downloaded");
                } else {
                    p.putValue("audioType", "Streaming");
                }
                p.putValue("audioId", mDataLike.get(position).getID());
                p.putValue("audioName", mDataLike.get(position).getName());
                p.putValue("audioDescription", "");
                p.putValue("directions", mDataLike.get(position).getAudioDirection());
                p.putValue("masterCategory", mDataLike.get(position).getAudiomastercat());
                p.putValue("subCategory", mDataLike.get(position).getAudioSubCategory());
                p.putValue("audioDuration", mDataLike.get(position).getAudioDuration());
            } else {
                if (downloadAudioDetailsList.contains(mData.get(position).getName())) {
                    p.putValue("audioType", "Downloaded");
                } else {
                    p.putValue("audioType", "Streaming");
                }
                p.putValue("audioId", mData.get(position).getID());
                p.putValue("audioName", mData.get(position).getName());
                p.putValue("audioDescription", "");
                p.putValue("directions", mData.get(position).getAudioDirection());
                p.putValue("masterCategory", mData.get(position).getAudiomastercat());
                p.putValue("subCategory", mData.get(position).getAudioSubCategory());
                p.putValue("audioDuration", mData.get(position).getAudioDuration());
            }
            p.putValue("position", GetCurrentAudioPosition());
            p.putValue("source", GetSourceName(ctx));
            p.putValue("playerType", "Mini");
        } else {
            p.putValue("audioId", mainPlayModelList.get(playerpos).getID());
            p.putValue("audioName", mainPlayModelList.get(playerpos).getName());
            p.putValue("audioDescription", "");
            p.putValue("directions", mainPlayModelList.get(playerpos).getAudioDirection());
            p.putValue("masterCategory", mainPlayModelList.get(playerpos).getAudiomastercat());
            p.putValue("subCategory", mainPlayModelList.get(playerpos).getAudioSubCategory());
            p.putValue("audioDuration", mainPlayModelList.get(playerpos).getAudioDuration());
            p.putValue("position", GetCurrentAudioPosition());
            if (downloadAudioDetailsList.contains(mainPlayModelList.get(position).getName())) {
                p.putValue("audioType", "Downloaded");
            } else {
                p.putValue("audioType", "Streaming");
            }
            p.putValue("source", GetSourceName(ctx));
            p.putValue("playerType", "Main");
        }
        p.putValue("bitRate", "");
        p.putValue("sound", String.valueOf(hundredVolume));
        BWSApplication.addToSegment("Audio Details Viewed", p, CONSTANTS.track);

        if (queuePlay) {
            listSize = addToQueueModelList.size();
        } else if (audioPlay) {
            listSize = mainPlayModelList.size();
        }
        if (IsShuffle.equalsIgnoreCase("")) {
            if (listSize == 1) {
                binding.llShuffle.setClickable(false);
                binding.llShuffle.setEnabled(false);
                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                binding.llShuffle.setClickable(true);
                binding.llShuffle.setEnabled(true);
                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
            }
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
        }

        if (IsRepeat.equalsIgnoreCase("")) {
            if (queuePlay) {
                binding.llRepeat.setClickable(false);
                binding.llRepeat.setEnabled(false);
                binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
                binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                binding.llRepeat.setClickable(true);
                binding.llRepeat.setEnabled(true);
                binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
                binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
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
        } else if (IsRepeat.equalsIgnoreCase("1")) {
            if (queuePlay) {
                binding.llRepeat.setEnabled(false);
                binding.llRepeat.setClickable(false);
                binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
                binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                binding.llRepeat.setClickable(true);
                binding.llRepeat.setEnabled(true);
                binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
                binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        }

        binding.llLike.setOnClickListener(view ->
                callLike());

        binding.llDownload.setOnClickListener(view ->
                callDownload()
        );


        binding.llAddQueue.setOnClickListener(view ->
                callAddToQueue());

        binding.llRepeat.setOnClickListener(view -> callRepeat());

        binding.llShuffle.setOnClickListener(view -> callShuffle());

        binding.llRemovePlaylist.setOnClickListener(view -> {
            myBackPress = true;
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            int pos = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
            String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
            if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistId) && mainPlayModelList.size() == 1) {
                BWSApplication.showToast("Currently you play this playlist, you can't remove last audio", ctx);
            } else {
                callRemoveFromPlayList();
            }
        });

        binding.llBack.setOnClickListener(view -> {
            /*  Intent i = new Intent(ctx, PlayWellnessActivity.class);
            i.putExtra("Like", Like);
            i.putExtra("Download", Download);
            startActivity(i);*/
            callBack();
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(new AppLifecycleCallback());
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
                }
            } else {
                fileNameList = new ArrayList<>();
                playlistDownloadId = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> GetAllMediaDownload() {
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
        return downloadAudioDetailsList;
    }

    @Override
    protected void onResume() {
        prepareData();
        super.onResume();
    }

    private void callBack() {
        myBackPress = true;
        ComeFindAudio = 1;
        if (!comeFrom.equalsIgnoreCase("")) {
            finish();
        } else {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
//            Intent i = new Intent(ctx, PlayWellnessActivity.class);
//            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            startActivity(i);
      /*      SharedPreferences shared11 = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared11.edit();
            Gson gson11 = new Gson();
            String json11 = gson11.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, json11);
            editor.commit();*/
            Intent i = new Intent(ctx, AudioPlayerActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            ctx.startActivity(i);
            finish();
            overridePendingTransition(0, 0);
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
                SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putString(CONSTANTS.PREF_KEY_IsShuffle, "1");
                if (IsRepeat.equalsIgnoreCase("0")) {
                    editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "");
                }
                editor.commit();
                IsRepeat = "";
                if (queuePlay) {
                    binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
                } else
                    binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
//                BWSApplication.showToast("Shuffle mode has been turned on", ctx);
                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        } else if (IsShuffle.equalsIgnoreCase("1")) {
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(CONSTANTS.PREF_KEY_IsShuffle, "");
            editor.commit();
            IsShuffle = "";
//            BWSApplication.showToast("Shuffle mode has been turned off", ctx);
            binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    private void callRepeat() {

        if (IsRepeat.equalsIgnoreCase("")) {
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "0");
            if (IsShuffle.equalsIgnoreCase("1")) {
                editor.putString(CONSTANTS.PREF_KEY_IsShuffle, "");
            }
            editor.commit();
            IsShuffle = "";
            if (listSize == 1) {
                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else
                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
            IsRepeat = "0";
            binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_one));
//            BWSApplication.showToast("Repeat mode has been turned on", ctx);
            binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (IsRepeat.equalsIgnoreCase("0")) {
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "1");
            editor.putString(CONSTANTS.PREF_KEY_IsShuffle, "");
            IsRepeat = "1";
            if (listSize == 1) {
                editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "");
                IsRepeat = "";
                IsShuffle = "";
//                BWSApplication.showToast("Repeat mode has been turned off", ctx);
                binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
//                BWSApplication.showToast("Repeat mode has been turned on", ctx);
                binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
            }
            editor.commit();
            binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
        } else if (IsRepeat.equalsIgnoreCase("1")) {
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "");
            editor.commit();
            if (listSize == 1) {
                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else
                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
            IsRepeat = "";
            binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
//            BWSApplication.showToast("Repeat mode has been turned off", ctx);
        }
    }

    private void callAddToQueue() {
        addToQueueModel = new AddToQueueModel();
        int i = position;

        if (!comeFrom.equalsIgnoreCase("")) {
            if (comeFrom.equalsIgnoreCase("myDownloadPlaylist")) {
                AudioFile = mDataDownload.get(i).getAudioFile();
                if (AudioFile.equalsIgnoreCase("")) {
                    i = i + 1;
                    AudioFile = mDataDownload.get(i).getAudioFile();
                }
                PlaylistId = "";
                addToQueueModel.setID(mDataDownload.get(i).getID());
                addToQueueModel.setName(mDataDownload.get(i).getName());
                addToQueueModel.setAudioFile(mDataDownload.get(i).getAudioFile());
                addToQueueModel.setPlaylistID("");
                addToQueueModel.setAudioDirection(mDataDownload.get(i).getAudioDirection());
                addToQueueModel.setAudiomastercat(mDataDownload.get(i).getAudiomastercat());
                addToQueueModel.setAudioSubCategory(mDataDownload.get(i).getAudioSubCategory());
                addToQueueModel.setImageFile(mDataDownload.get(i).getImageFile());
                addToQueueModel.setLike(mDataDownload.get(i).getLike());
                addToQueueModel.setDownload(mDataDownload.get(i).getDownload());
                addToQueueModel.setAudioDuration(mDataDownload.get(i).getAudioDuration());
            } else if (comeFrom.equalsIgnoreCase("myLikeAudioList")) {
                AudioFile = mDataLike.get(i).getAudioFile();
                if (AudioFile.equalsIgnoreCase("")) {
                    i = i + 1;
                    AudioFile = mDataLike.get(i).getAudioFile();
                }
                PlaylistId = "";
                addToQueueModel.setID(mDataLike.get(i).getID());
                addToQueueModel.setName(mDataLike.get(i).getName());
                addToQueueModel.setAudioFile(mDataLike.get(i).getAudioFile());
                addToQueueModel.setPlaylistID("");
                addToQueueModel.setAudioDirection(mDataLike.get(i).getAudioDirection());
                addToQueueModel.setAudiomastercat(mDataLike.get(i).getAudiomastercat());
                addToQueueModel.setAudioSubCategory(mDataLike.get(i).getAudioSubCategory());
                addToQueueModel.setImageFile(mDataLike.get(i).getImageFile());
                addToQueueModel.setLike(mDataLike.get(i).getLike());
                addToQueueModel.setDownload(mDataLike.get(i).getDownload());
                addToQueueModel.setAudioDuration(mDataLike.get(i).getAudioDuration());
            } else {
                AudioFile = mData.get(i).getAudioFile();
                if (AudioFile.equalsIgnoreCase("")) {
                    i = i + 1;
                    AudioFile = mData.get(i).getAudioFile();
                }
                PlaylistId = mData.get(i).getPlaylistID();
                addToQueueModel.setID(mData.get(i).getID());
                addToQueueModel.setName(mData.get(i).getName());
                addToQueueModel.setAudioFile(mData.get(i).getAudioFile());
                addToQueueModel.setPlaylistID(mData.get(i).getPlaylistID());
                addToQueueModel.setAudioDirection(mData.get(i).getAudioDirection());
                addToQueueModel.setAudiomastercat(mData.get(i).getAudiomastercat());
                addToQueueModel.setAudioSubCategory(mData.get(i).getAudioSubCategory());
                addToQueueModel.setImageFile(mData.get(i).getImageFile());
                addToQueueModel.setLike(mData.get(i).getLike());
                addToQueueModel.setDownload(mData.get(i).getDownload());
                addToQueueModel.setAudioDuration(mData.get(i).getAudioDuration());
            }
        } else {
            AudioFile = mainPlayModelList.get(i).getAudioFile();
            if (AudioFile.equalsIgnoreCase("")) {
                i = i + 1;
                AudioFile = mainPlayModelList.get(i).getAudioFile();
            }
            PlaylistId = mainPlayModelList.get(i).getPlaylistID();
            addToQueueModel.setID(mainPlayModelList.get(i).getID());
            addToQueueModel.setName(mainPlayModelList.get(i).getName());
            addToQueueModel.setAudioFile(mainPlayModelList.get(i).getAudioFile());
            addToQueueModel.setPlaylistID(mainPlayModelList.get(i).getPlaylistID());
            addToQueueModel.setAudioDirection(mainPlayModelList.get(i).getAudioDirection());
            addToQueueModel.setAudiomastercat(mainPlayModelList.get(i).getAudiomastercat());
            addToQueueModel.setAudioSubCategory(mainPlayModelList.get(i).getAudioSubCategory());
            addToQueueModel.setImageFile(mainPlayModelList.get(i).getImageFile());
            addToQueueModel.setLike(mainPlayModelList.get(i).getLike());
            addToQueueModel.setDownload(mainPlayModelList.get(i).getDownload());
            addToQueueModel.setAudioDuration(mainPlayModelList.get(i).getAudioDuration());
        }
        if (addToQueueModelList.size() == 0) {
            BWSApplication.showToast("Added to the queue", ctx);
            addToQueueModelList.add(addToQueueModel);
        } else {
            for (int x = 0; x < addToQueueModelList.size(); x++) {
                if (addToQueueModelList.get(x).getAudioFile().equals(addToQueueModel.getAudioFile())) {
                    if (queuePlay && addToQueueModelList.get(position).getAudioFile().equals(addToQueueModel.getAudioFile())) {
                        BWSApplication.showToast("Added to the queue", ctx);
                        addToQueueModelList.add(addToQueueModel);
                        break;
                    } else {
                        addToQueueModel = new AddToQueueModel();
                        BWSApplication.showToast("Audio already in queue", ctx);
                        break;
                    }
                } else if (x == (addToQueueModelList.size() - 1)) {
                    BWSApplication.showToast("Added to the queue", ctx);
                    addToQueueModelList.add(addToQueueModel);
                    break;
                }
            }
        }
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        Gson gson = new Gson();
        String json = gson.toJson(addToQueueModelList);
        editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
        editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
        editor.putString(CONSTANTS.PREF_KEY_queueList, json);
        editor.commit();
    }

    private void callRemoveFromPlayList() {
        myBackPress = true;
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<SucessModel> listCall = APIClient.getClient().getRemoveAudioFromPlaylist(UserID, AudioId, PlaylistId);
            listCall.enqueue(new Callback<SucessModel>() {
                @Override
                public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                    try {
                        myBackPress = true;
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            SucessModel listModel = response.body();
                            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                            boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                            int pos = shared.getInt(CONSTANTS.PREF_KEY_position, 0);

                            if (audioPlay) {
                                if (AudioFlag.equalsIgnoreCase("SubPlayList")) {
                                    Gson gson12 = new Gson();
                                    String json12 = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson12));
                                    Type type1 = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
                                    }.getType();
                                    ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList1 = gson12.fromJson(json12, type1);

                                    if (!comeFrom.equalsIgnoreCase("")) {
                                        mData.remove(position);
                                        String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                                        int oldpos = pos;
                                        if (pID.equalsIgnoreCase(PlaylistId)) {
                                            if (mData.size() != 0) {
                                                if (pos == position && position < mData.size() - 1) {
                                                    pos = pos;
                                                } else if (pos == position && position == mData.size() - 1) {
                                                    pos = 0;
                                                } else if (pos < position && pos < mData.size() - 1) {
                                                    pos = pos;
                                                } else if (pos < position && pos == mData.size() - 1) {
                                                    pos = pos;
                                                } else if (pos > position && pos == mData.size()) {
                                                    pos = pos - 1;
                                                }
                                                SharedPreferences sharedd = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedd.edit();
                                                Gson gson = new Gson();
                                                String json = gson.toJson(mData);
                                                editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                                                editor.putInt(CONSTANTS.PREF_KEY_position, pos);
                                                editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                                                editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                                                editor.putString(CONSTANTS.PREF_KEY_PlaylistId, PlaylistId);
                                                editor.putString(CONSTANTS.PREF_KEY_myPlaylist, myPlaylist);
                                                editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SubPlayList");
                                                editor.commit();
                                                Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
                                                }.getType();
                                                ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);
                                                listSize = arrayList.size();
                                                for (int i = 0; i < listSize; i++) {
                                                    MainPlayModel mainPlayModel = new MainPlayModel();
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
                                                SharedPreferences sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor1 = sharedz.edit();
                                                Gson gsonz = new Gson();
                                                String jsonz = gsonz.toJson(mainPlayModelList);
                                                editor1.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
                                                editor1.commit();
                                                if (player != null) {
                                                    player.removeMediaItem(oldpos);
                                                    player.setPlayWhenReady(true);
                                                }
                                                finish();
                                            }
                                        }
                                        finish();
                                    } else {
                                        mainPlayModelList.remove(pos);
                                        arrayList1.remove(pos);
                                        String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                                        if (pID.equalsIgnoreCase(PlaylistId)) {
                                            int oldpos = pos;
                                            if (mainPlayModelList.size() != 0) {
                                                if (pos < mainPlayModelList.size() - 1) {
                                                    pos = pos;
                                                } else if (pos == mainPlayModelList.size() - 1) {
                                                    pos = 0;
                                                } else if (pos == mainPlayModelList.size()) {
                                                    pos = 0;
                                                } else if (pos > mainPlayModelList.size()) {
                                                    pos = pos - 1;
                                                }
                                                SharedPreferences sharedd = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedd.edit();
                                                Gson gson = new Gson();
                                                String json = gson.toJson(mainPlayModelList);
                                                String json1 = gson.toJson(arrayList1);
                                                editor.putString(CONSTANTS.PREF_KEY_modelList, json1);
                                                editor.putString(CONSTANTS.PREF_KEY_audioList, json);
                                                editor.putInt(CONSTANTS.PREF_KEY_position, pos);
                                                editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                                                editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                                                editor.putString(CONSTANTS.PREF_KEY_PlaylistId, PlaylistId);
                                                editor.putString(CONSTANTS.PREF_KEY_myPlaylist, myPlaylist);
                                                editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SubPlayList");
                                                editor.commit();
//                                                if(mainPlayModelList.size()==1){
//                                                    miniPlayer = 1;
//                                                    audioClick = true;
//                                                    callNewPlayerRelease();
//                                                }else {
                                                if (player != null) {
                                                    player.removeMediaItem(oldpos);
                                                }
//                                                }
                                                Intent i = new Intent(ctx, AudioPlayerActivity.class);
                                                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                ctx.startActivity(i);
                                                finish();
                                                overridePendingTransition(0, 0);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<SucessModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    private void callDownload() {
        try {
            int i = position;
            String audioFile = "", Name = "";
            if (!comeFrom.equalsIgnoreCase("")) {
                if (comeFrom.equalsIgnoreCase("myDownloadPlaylist")) {
                    Name = mDataDownload.get(i).getName();
                    audioFile = mDataDownload.get(i).getAudioFile();
                    if (audioFile.equalsIgnoreCase("")) {
                        i = i + 1;
                        Name = mDataDownload.get(i).getName();
                        audioFile = mDataDownload.get(i).getAudioFile();
                    }
                } else if (comeFrom.equalsIgnoreCase("myLikeAudioList")) {
                    Name = mDataLike.get(i).getName();
                    audioFile = mDataLike.get(i).getAudioFile();
                    if (audioFile.equalsIgnoreCase("")) {
                        i = i + 1;
                        Name = mDataLike.get(i).getName();
                        audioFile = mDataLike.get(i).getAudioFile();
                    }
                } else {
                    Name = mData.get(i).getName();
                    audioFile = mData.get(i).getAudioFile();
                    if (audioFile.equalsIgnoreCase("")) {
                        i = i + 1;
                        Name = mData.get(i).getName();
                        audioFile = mData.get(i).getAudioFile();
                    }
                }
            } else {
                Name = mainPlayModelList.get(i).getName();
                audioFile = mainPlayModelList.get(i).getAudioFile();
                if (audioFile.equalsIgnoreCase("")) {
                    i = i + 1;
                    Name = mainPlayModelList.get(i).getName();
                    audioFile = mainPlayModelList.get(i).getAudioFile();
                }
            }
            if (downloadAudioDetailsList.contains(Name)) {
                callDisableDownload();
                SaveMedia(i, 100);
            } else {
                List<String> url1 = new ArrayList<>();
                List<String> name1 = new ArrayList<>();
                List<String> downloadPlaylistId = new ArrayList<>();
                SharedPreferences sharedx = getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
                Gson gson1 = new Gson();
                String json = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson1));
                String json1 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson1));
                String json2 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson1));
                if (!json1.equalsIgnoreCase(String.valueOf(gson1))) {
                    Type type = new TypeToken<List<String>>() {
                    }.getType();
                    List<String> fileNameList = gson1.fromJson(json, type);
                    List<String> audioFile1 = gson1.fromJson(json1, type);
                    List<String> playlistId1 = gson1.fromJson(json2, type);
                    if (fileNameList.size() != 0) {
                        url1.addAll(audioFile1);
                        name1.addAll(fileNameList);
                        downloadPlaylistId.addAll(playlistId1);
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
                        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
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
                        DownloadMedia downloadMedia = new DownloadMedia(getApplicationContext());
                        downloadMedia.encrypt1(url1, name1, downloadPlaylistId);
                    }
                    callDisableDownload();
                    SaveMedia(i, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void SaveMedia(int i, int progress) {
        DownloadAudioDetails downloadAudioDetails = new DownloadAudioDetails();
        p = new Properties();
        if (!comeFrom.equalsIgnoreCase("")) {
            if (comeFrom.equalsIgnoreCase("myDownloadPlaylist")) {
                downloadAudioDetails.setID(mDataDownload.get(i).getID());
                downloadAudioDetails.setName(mDataDownload.get(i).getName());
                downloadAudioDetails.setAudioFile(mDataDownload.get(i).getAudioFile());
                downloadAudioDetails.setPlaylistId("");
                downloadAudioDetails.setAudioDirection(mDataDownload.get(i).getAudioDirection());
                downloadAudioDetails.setAudiomastercat(mDataDownload.get(i).getAudiomastercat());
                downloadAudioDetails.setAudioSubCategory(mDataDownload.get(i).getAudioSubCategory());
                downloadAudioDetails.setImageFile(mDataDownload.get(i).getImageFile());
                downloadAudioDetails.setLike(mDataDownload.get(i).getLike());
                downloadAudioDetails.setAudioDuration(mDataDownload.get(i).getAudioDuration());
            } else if (comeFrom.equalsIgnoreCase("myLikeAudioList")) {
                downloadAudioDetails.setID(mDataLike.get(i).getID());
                downloadAudioDetails.setName(mDataLike.get(i).getName());
                downloadAudioDetails.setAudioFile(mDataLike.get(i).getAudioFile());
                downloadAudioDetails.setPlaylistId("");
                downloadAudioDetails.setAudioDirection(mDataLike.get(i).getAudioDirection());
                downloadAudioDetails.setAudiomastercat(mDataLike.get(i).getAudiomastercat());
                downloadAudioDetails.setAudioSubCategory(mDataLike.get(i).getAudioSubCategory());
                downloadAudioDetails.setImageFile(mDataLike.get(i).getImageFile());
                downloadAudioDetails.setLike(mDataLike.get(i).getLike());
                downloadAudioDetails.setAudioDuration(mDataLike.get(i).getAudioDuration());
            } else {
                downloadAudioDetails.setID(mData.get(i).getID());
                downloadAudioDetails.setName(mData.get(i).getName());
                downloadAudioDetails.setAudioFile(mData.get(i).getAudioFile());
                downloadAudioDetails.setPlaylistId(mData.get(i).getPlaylistID());
                downloadAudioDetails.setAudioDirection(mData.get(i).getAudioDirection());
                downloadAudioDetails.setAudiomastercat(mData.get(i).getAudiomastercat());
                downloadAudioDetails.setAudioSubCategory(mData.get(i).getAudioSubCategory());
                downloadAudioDetails.setImageFile(mData.get(i).getImageFile());
                downloadAudioDetails.setLike(mData.get(i).getLike());
                downloadAudioDetails.setAudioDuration(mData.get(i).getAudioDuration());
            }
            p.putValue("playerType", "Mini");
            downloadAudioDetails.setDownload("1");
            downloadAudioDetails.setIsSingle("1");
            downloadAudioDetails.setPlaylistId("");
            if (progress == 0) {
                downloadAudioDetails.setIsDownload("pending");
            } else {
                downloadAudioDetails.setIsDownload("Complete");
            }
            downloadAudioDetails.setDownloadProgress(progress);
        } else {
            p.putValue("playerType", "Main");
            downloadAudioDetails.setID(mainPlayModelList.get(i).getID());
            downloadAudioDetails.setName(mainPlayModelList.get(i).getName());
            downloadAudioDetails.setAudioFile(mainPlayModelList.get(i).getAudioFile());
            downloadAudioDetails.setPlaylistId(mainPlayModelList.get(i).getPlaylistID());
            downloadAudioDetails.setAudioDirection(mainPlayModelList.get(i).getAudioDirection());
            downloadAudioDetails.setAudiomastercat(mainPlayModelList.get(i).getAudiomastercat());
            downloadAudioDetails.setAudioSubCategory(mainPlayModelList.get(i).getAudioSubCategory());
            downloadAudioDetails.setImageFile(mainPlayModelList.get(i).getImageFile());
            downloadAudioDetails.setLike(mainPlayModelList.get(i).getLike());
            downloadAudioDetails.setDownload("1");
            downloadAudioDetails.setAudioDuration(mainPlayModelList.get(i).getAudioDuration());
            downloadAudioDetails.setIsSingle("1");
            downloadAudioDetails.setPlaylistId("");
            downloadAudioDetails.setIsDownload("pending");
            downloadAudioDetails.setDownloadProgress(0);
        }

        p.putValue("userId", UserID);
        p.putValue("audioId", downloadAudioDetails.getID());
        p.putValue("audioName", downloadAudioDetails.getName());
        p.putValue("audioDescription", "");
        p.putValue("directions", downloadAudioDetails.getAudioDirection());
        p.putValue("masterCategory", downloadAudioDetails.getAudiomastercat());
        p.putValue("subCategory", downloadAudioDetails.getAudioSubCategory());
        p.putValue("audioDuration", downloadAudioDetails.getAudioDuration());
        p.putValue("position", GetCurrentAudioPosition());
        if (downloadAudioDetailsList.contains(downloadAudioDetails.getName())) {
            p.putValue("audioType", "Downloaded");
        } else {
            p.putValue("audioType", "Streaming");
        }
        p.putValue("source", GetSourceName(ctx));
        p.putValue("bitRate", "");
        p.putValue("sound", String.valueOf(hundredVolume));
        BWSApplication.addToSegment("Audio Download Started", p, CONSTANTS.track);

        Log.e("Download Media Audio", "1");
        try {
            AudioDatabase.databaseWriteExecutor.execute(() -> DB.taskDao().insertMedia(downloadAudioDetails));
        } catch (Exception | OutOfMemoryError e) {
            System.out.println(e.getMessage());
        }
        Log.e("Download Media Audio", "3");
        SharedPreferences sharedx1 = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = sharedx1.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        boolean audioPlay = sharedx1.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
        int position = sharedx1.getInt(CONSTANTS.PREF_KEY_position, 0);
        Gson gsonx = new Gson();
        String json11 = sharedx1.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gsonx));
        String jsonw = sharedx1.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gsonx));
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
        if (audioPlay && AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
            arrayList.add(downloadAudioDetails);
            MainPlayModel mainPlayModel1 = new MainPlayModel();
            mainPlayModel1.setID(downloadAudioDetails.getID());
            mainPlayModel1.setName(downloadAudioDetails.getName());
            mainPlayModel1.setAudioFile(downloadAudioDetails.getAudioFile());
            mainPlayModel1.setAudioDirection(downloadAudioDetails.getAudioDirection());
            mainPlayModel1.setAudiomastercat(downloadAudioDetails.getAudiomastercat());
            mainPlayModel1.setAudioSubCategory(downloadAudioDetails.getAudioSubCategory());
            mainPlayModel1.setImageFile(downloadAudioDetails.getImageFile());
            mainPlayModel1.setLike(downloadAudioDetails.getLike());
            mainPlayModel1.setDownload(downloadAudioDetails.getDownload());
            mainPlayModel1.setAudioDuration(downloadAudioDetails.getAudioDuration());
            arrayList2.add(mainPlayModel1);
            SharedPreferences sharedd = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedd.edit();
            Gson gson = new Gson();
            String jsonx = gson.toJson(arrayList2);
            String json1q1 = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json1q1);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonx);
            editor.putInt(CONSTANTS.PREF_KEY_position, position);
            editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
            editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
            editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
            editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
            editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "DownloadListAudio");
            editor.commit();
            if (!arrayList2.get(position).getAudioFile().equals("")) {
                List<String> downloadAudioDetailsList = new ArrayList<>();
                GlobalInitExoPlayer ge = new GlobalInitExoPlayer();
                downloadAudioDetailsList.add(downloadAudioDetails.getName());
                ge.AddAudioToPlayer(size, arrayList2, downloadAudioDetailsList, ctx);
            }
//                callAddTransFrag();
        }
        callDisableDownload();
    }

    private void callLike() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<AudioLikeModel> listCall = APIClient.getClient().getAudioLike(AudioId, UserID);
            listCall.enqueue(new Callback<AudioLikeModel>() {
                @Override
                public void onResponse(Call<AudioLikeModel> call, Response<AudioLikeModel> response) {
                    if (response.isSuccessful()) {
                        binding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        AudioLikeModel model = response.body();
                        if (model.getResponseData().getFlag().equalsIgnoreCase("0")) {
                            binding.ivLike.setImageResource(R.drawable.ic_like_white_icon);
                            Like = "0";
                        } else if (model.getResponseData().getFlag().equalsIgnoreCase("1")) {
                            binding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
                            Like = "1";
                        }
                        SharedPreferences sharedxx = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                        boolean audioPlay = sharedxx.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                        int pos = sharedxx.getInt(CONSTANTS.PREF_KEY_position, 0);
                        AudioFlag = sharedxx.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = shared.edit();
                        Gson gson = new Gson();
                        if (audioPlay && AudioFlag.equalsIgnoreCase("LikeAudioList")) {
                            AudioFlag = sharedxx.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                            try {
                                if (model.getResponseData().getFlag().equalsIgnoreCase("0")) {

                                } else if (model.getResponseData().getFlag().equalsIgnoreCase("1")) {
                                    SharedPreferences sharedx = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                                    AudioFlag = sharedx.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                                    Gson gsonx = new Gson();
                                    String json = sharedx.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gsonx));
                                    Type type1 = new TypeToken<ArrayList<LikesHistoryModel.ResponseData.Audio>>() {
                                    }.getType();
                                    ArrayList<LikesHistoryModel.ResponseData.Audio> arrayList = gsonx.fromJson(json, type1);
                                    LikesHistoryModel.ResponseData.Audio mainPlayModel = new LikesHistoryModel.ResponseData.Audio();
                                    MainPlayModel mainPlayModel1 = new MainPlayModel();

                                    int size = mainPlayModelList.size();
                                    if (!comeFrom.equalsIgnoreCase("")) {
                                        if (comeFrom.equalsIgnoreCase("myDownloadPlaylist")) {
                                            mainPlayModel.setID(mDataDownload.get(position).getID());
                                            mainPlayModel.setName(mDataDownload.get(position).getName());
                                            mainPlayModel.setAudioFile(mDataDownload.get(position).getAudioFile());
                                            mainPlayModel.setAudioDirection(mDataDownload.get(position).getAudioDirection());
                                            mainPlayModel.setAudiomastercat(mDataDownload.get(position).getAudiomastercat());
                                            mainPlayModel.setAudioSubCategory(mDataDownload.get(position).getAudioSubCategory());
                                            mainPlayModel.setImageFile(mDataDownload.get(position).getImageFile());
                                            mainPlayModel.setLike(Like);
                                            mainPlayModel.setDownload(mDataDownload.get(position).getDownload());
                                            mainPlayModel.setAudioDuration(mDataDownload.get(position).getAudioDuration());

                                            mainPlayModel1.setID(mDataDownload.get(position).getID());
                                            mainPlayModel1.setName(mDataDownload.get(position).getName());
                                            mainPlayModel1.setAudioFile(mDataDownload.get(position).getAudioFile());
                                            mainPlayModel1.setAudioDirection(mDataDownload.get(position).getAudioDirection());
                                            mainPlayModel1.setAudiomastercat(mDataDownload.get(position).getAudiomastercat());
                                            mainPlayModel1.setAudioSubCategory(mDataDownload.get(position).getAudioSubCategory());
                                            mainPlayModel1.setImageFile(mDataDownload.get(position).getImageFile());
                                            mainPlayModel1.setLike(Like);
                                            mainPlayModel1.setDownload(mDataDownload.get(position).getDownload());
                                            mainPlayModel1.setAudioDuration(mDataDownload.get(position).getAudioDuration());
                                        } else if (comeFrom.equalsIgnoreCase("myLikeAudioList")) {
                                            mainPlayModel.setID(mDataLike.get(position).getID());
                                            mainPlayModel.setName(mDataLike.get(position).getName());
                                            mainPlayModel.setAudioFile(mDataLike.get(position).getAudioFile());
                                            mainPlayModel.setAudioDirection(mDataLike.get(position).getAudioDirection());
                                            mainPlayModel.setAudiomastercat(mDataLike.get(position).getAudiomastercat());
                                            mainPlayModel.setAudioSubCategory(mDataLike.get(position).getAudioSubCategory());
                                            mainPlayModel.setImageFile(mDataLike.get(position).getImageFile());
                                            mainPlayModel.setLike(Like);
                                            mainPlayModel.setDownload(mDataLike.get(position).getDownload());
                                            mainPlayModel.setAudioDuration(mDataLike.get(position).getAudioDuration());

                                            mainPlayModel1.setID(mDataLike.get(position).getID());
                                            mainPlayModel1.setName(mDataLike.get(position).getName());
                                            mainPlayModel1.setAudioFile(mDataLike.get(position).getAudioFile());
                                            mainPlayModel1.setAudioDirection(mDataLike.get(position).getAudioDirection());
                                            mainPlayModel1.setAudiomastercat(mDataLike.get(position).getAudiomastercat());
                                            mainPlayModel1.setAudioSubCategory(mDataLike.get(position).getAudioSubCategory());
                                            mainPlayModel1.setImageFile(mDataLike.get(position).getImageFile());
                                            mainPlayModel1.setLike(Like);
                                            mainPlayModel1.setDownload(mDataLike.get(position).getDownload());
                                            mainPlayModel1.setAudioDuration(mDataLike.get(position).getAudioDuration());
                                        } else {
                                            mainPlayModel.setID(mData.get(position).getID());
                                            mainPlayModel.setName(mData.get(position).getName());
                                            mainPlayModel.setAudioFile(mData.get(position).getAudioFile());
                                            mainPlayModel.setAudioDirection(mData.get(position).getAudioDirection());
                                            mainPlayModel.setAudiomastercat(mData.get(position).getAudiomastercat());
                                            mainPlayModel.setAudioSubCategory(mData.get(position).getAudioSubCategory());
                                            mainPlayModel.setImageFile(mData.get(position).getImageFile());
                                            mainPlayModel.setLike(Like);
                                            mainPlayModel.setDownload(mData.get(position).getDownload());
                                            mainPlayModel.setAudioDuration(mData.get(position).getAudioDuration());

                                            mainPlayModel1.setID(mData.get(position).getID());
                                            mainPlayModel1.setName(mData.get(position).getName());
                                            mainPlayModel1.setAudioFile(mData.get(position).getAudioFile());
                                            mainPlayModel1.setAudioDirection(mData.get(position).getAudioDirection());
                                            mainPlayModel1.setAudiomastercat(mData.get(position).getAudiomastercat());
                                            mainPlayModel1.setAudioSubCategory(mData.get(position).getAudioSubCategory());
                                            mainPlayModel1.setImageFile(mData.get(position).getImageFile());
                                            mainPlayModel1.setLike(Like);
                                            mainPlayModel1.setDownload(mData.get(position).getDownload());
                                            mainPlayModel1.setAudioDuration(mData.get(position).getAudioDuration());
                                        }
                                    } else {
                                        mainPlayModel.setID(mainPlayModelList.get(position).getID());
                                        mainPlayModel.setName(mainPlayModelList.get(position).getName());
                                        mainPlayModel.setAudioFile(mainPlayModelList.get(position).getAudioFile());
                                        mainPlayModel.setAudioDirection(mainPlayModelList.get(position).getAudioDirection());
                                        mainPlayModel.setAudiomastercat(mainPlayModelList.get(position).getAudiomastercat());
                                        mainPlayModel.setAudioSubCategory(mainPlayModelList.get(position).getAudioSubCategory());
                                        mainPlayModel.setImageFile(mainPlayModelList.get(position).getImageFile());
                                        mainPlayModel.setLike(Like);
                                        mainPlayModel.setDownload(mainPlayModelList.get(position).getDownload());
                                        mainPlayModel.setAudioDuration(mainPlayModelList.get(position).getAudioDuration());

                                        mainPlayModel1.setID(mainPlayModelList.get(position).getID());
                                        mainPlayModel1.setName(mainPlayModelList.get(position).getName());
                                        mainPlayModel1.setAudioFile(mainPlayModelList.get(position).getAudioFile());
                                        mainPlayModel1.setAudioDirection(mainPlayModelList.get(position).getAudioDirection());
                                        mainPlayModel1.setAudiomastercat(mainPlayModelList.get(position).getAudiomastercat());
                                        mainPlayModel1.setAudioSubCategory(mainPlayModelList.get(position).getAudioSubCategory());
                                        mainPlayModel1.setImageFile(mainPlayModelList.get(position).getImageFile());
                                        mainPlayModel1.setLike(Like);
                                        mainPlayModel1.setDownload(mainPlayModelList.get(position).getDownload());
                                        mainPlayModel1.setAudioDuration(mainPlayModelList.get(position).getAudioDuration());
                                    }

                                    SharedPreferences sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                                    String jsonz = sharedz.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gson));
//                                    mainPlayModelList=new ArrayList<>();
                                    if (!jsonz.equalsIgnoreCase(String.valueOf(gson))) {
                                        Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                                        }.getType();
                                        mainPlayModelList = gson.fromJson(jsonz, type);
                                    }
                                    arrayList.add(arrayList.size(), mainPlayModel);
                                    mainPlayModelList.add(mainPlayModelList.size(), mainPlayModel1);
                                    SharedPreferences sharedd = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor1 = sharedd.edit();
                                    String jsonx = gson.toJson(mainPlayModelList);
                                    String json1 = gson.toJson(arrayList);
                                    editor1.putString(CONSTANTS.PREF_KEY_modelList, json1);
                                    editor1.putString(CONSTANTS.PREF_KEY_audioList, jsonx);
                                    editor1.putInt(CONSTANTS.PREF_KEY_position, pos);
                                    editor1.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                                    editor1.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                                    editor1.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                                    editor1.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
                                    editor1.putString(CONSTANTS.PREF_KEY_AudioFlag, "LikeAudioList");
                                    editor1.commit();
                                    pos = sharedxx.getInt(CONSTANTS.PREF_KEY_position, 0);

                                    if (!mainPlayModelList.get(pos).getAudioFile().equalsIgnoreCase("")) {
                                        List<String> downloadAudioDetailsList = new ArrayList<>();
                                        GlobalInitExoPlayer ge = new GlobalInitExoPlayer();
                                        ge.AddAudioToPlayer(size, mainPlayModelList, downloadAudioDetailsList, ctx);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (audioPlay) {
                            try {
                                SharedPreferences sharedq = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                                AudioFlag = sharedq.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                                String jsonq = sharedq.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson));
                                Gson gsonq = new Gson();
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
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                       /* crash aave 6 etle temp comment kryu
                       if (queuePlay) {
                            addToQueueModelList.get(position).setLike(Like);
                        } else
                            mainPlayModelList.get(position).setLike(Like);

                        String json = gson.toJson(mainPlayModelList);
                        editor.putString(CONSTANTS.PREF_KEY_audioList, json);*/
                        if (queuePlay) {
                            String json1 = gson.toJson(addToQueueModelList);
                            editor.putString(CONSTANTS.PREF_KEY_queueList, json1);
                        }
                        editor.commit();

                        BWSApplication.showToast(model.getResponseMessage(), ctx);

                    }
                }

                @Override
                public void onFailure(Call<AudioLikeModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    @Override
    public void onBackPressed() {
        callBack();
    }

    private void prepareData() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<DirectionModel> listCall = APIClient.getClient().getAudioDetailLists(UserID, AudioId);
            listCall.enqueue(new Callback<DirectionModel>() {
                @Override
                public void onResponse(Call<DirectionModel> call, Response<DirectionModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        Log.e("AudioFlag", AudioFlag);
                        Log.e("play", play);
                        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                        String MyPlaylist = shared.getString(CONSTANTS.PREF_KEY_myPlaylist, "");

                        binding.llOptions.setVisibility(View.VISIBLE);
                        if (comeFrom.equalsIgnoreCase("myPlayList") || comeFrom.equalsIgnoreCase("myLikeAudioList")) {
                            binding.llRemovePlaylist.setVisibility(View.GONE);
                        } else {
                            if (MyPlaylist.equalsIgnoreCase("myPlaylist")) {
                                binding.llRemovePlaylist.setVisibility(View.VISIBLE);
                            } else {
                                binding.llRemovePlaylist.setVisibility(View.GONE);
                            }
                        }
                        DirectionModel directionModel = response.body();
                        int ix = position;
                        if (!comeFrom.equalsIgnoreCase("")) {

                            if (comeFrom.equalsIgnoreCase("myDownloadPlaylist")) {
                                AudioFile = mDataDownload.get(ix).getAudioFile();
                                PlaylistId = "";
                                audioFileName = mDataDownload.get(ix).getName();
                            } else if (comeFrom.equalsIgnoreCase("myLikeAudioList")) {
                                AudioFile = mDataLike.get(ix).getAudioFile();
                                PlaylistId = "";
                                audioFileName = mDataLike.get(ix).getName();
                            } else {
                                AudioFile = mData.get(ix).getAudioFile();
                                PlaylistId = mData.get(ix).getPlaylistID();
                                audioFileName = mData.get(ix).getName();
                            }
                        } else {
                            if (mainPlayModelList.size() != 0) {
                                AudioFile = mainPlayModelList.get(ix).getAudioFile();
                                PlaylistId = mainPlayModelList.get(ix).getPlaylistID();
                                audioFileName = mainPlayModelList.get(ix).getName();
                            }
                        }

                        /*if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(audioFileName)) {
                            handler1.postDelayed(UpdateSongTime1, 500);
                        } else {
                            binding.pbProgress.setVisibility(View.GONE);
                            handler1.removeCallbacks(UpdateSongTime1);
                        }*/
                        if (PlaylistId == null) {
                            PlaylistId = "";
                        } else {
                            PlaylistId = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                        }
                        GetMedia(AudioFile, activity, directionModel.getResponseData().get(0).getDownload(), PlaylistId, audioFileName);
                        binding.cvImage.setVisibility(View.VISIBLE);
                        binding.llLike.setVisibility(View.VISIBLE);
                        binding.llAddPlaylist.setVisibility(View.VISIBLE);
                        binding.llAddQueue.setVisibility(View.GONE);
                        binding.llDownload.setVisibility(View.VISIBLE);
                        binding.llShuffle.setVisibility(View.VISIBLE);
                        binding.llRepeat.setVisibility(View.VISIBLE);
                        binding.llViewQueue.setVisibility(View.VISIBLE);

                        try {
                            Glide.with(ctx).load(directionModel.getResponseData().get(0).getImageFile()).thumbnail(0.05f)
                                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(12))).priority(Priority.HIGH)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (directionModel.getResponseData().get(0).getAudioDescription().equalsIgnoreCase("")) {
                            binding.tvTitleDec.setVisibility(View.GONE);
                            binding.tvSubDec.setVisibility(View.GONE);
                        } else {
                            binding.tvTitleDec.setVisibility(View.VISIBLE);
                            binding.tvSubDec.setVisibility(View.VISIBLE);
                        }

                        binding.tvSubDec.setText(directionModel.getResponseData().get(0).getAudioDescription());
                        int linecount = binding.tvSubDec.getLineCount();
                        if (linecount >= 4) {
                            binding.tvReadMore.setVisibility(View.VISIBLE);
                        } else {
                            binding.tvReadMore.setVisibility(View.GONE);
                        }

                        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                        AudioFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                        if (!AudioFlag.equalsIgnoreCase("0")) {
                            binding.llViewQueue.setClickable(true);
                            binding.llViewQueue.setEnabled(true);
                            binding.ivViewQueue.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                        } else {
                            binding.llViewQueue.setClickable(false);
                            binding.llViewQueue.setEnabled(false);
                            binding.ivViewQueue.setColorFilter(getResources().getColor(R.color.light_gray), PorterDuff.Mode.SRC_IN);
                        }
                        binding.tvReadMore.setOnClickListener(view -> {
                            myBackPress = true;
                            final Dialog dialog = new Dialog(ctx);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.full_desc_layout);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue_gray)));
                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            final TextView tvDesc = dialog.findViewById(R.id.tvDesc);
                            final RelativeLayout tvClose = dialog.findViewById(R.id.tvClose);
                            tvDesc.setText(directionModel.getResponseData().get(0).getAudioDescription());

                            dialog.setOnKeyListener((v, keyCode, event) -> {
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    dialog.dismiss();
                                    return true;
                                }
                                return false;
                            });

                            tvClose.setOnClickListener(v -> dialog.dismiss());
                            dialog.show();
                            dialog.setCancelable(false);
                        });

                        Like = directionModel.getResponseData().get(0).getLike();
                        Download = directionModel.getResponseData().get(0).getDownload();
                        binding.tvName.setText(directionModel.getResponseData().get(0).getName());
                        if (directionModel.getResponseData().get(0).getAudiomastercat().equalsIgnoreCase("")) {
                            binding.tvDesc.setVisibility(View.GONE);
                        } else {
                            binding.tvDesc.setVisibility(View.VISIBLE);
                            binding.tvDesc.setText(directionModel.getResponseData().get(0).getAudiomastercat());
                        }
                        binding.tvDuration.setText(directionModel.getResponseData().get(0).getAudioDuration());

                        if (directionModel.getResponseData().get(0).getAudioDirection().equalsIgnoreCase("")) {
                            binding.tvSubDire.setText("");
                            binding.tvSubDire.setVisibility(View.GONE);
                            binding.tvDire.setVisibility(View.GONE);
                        } else {
                            binding.tvSubDire.setText(directionModel.getResponseData().get(0).getAudioDirection());
                            binding.tvSubDire.setVisibility(View.VISIBLE);
                            binding.tvDire.setVisibility(View.VISIBLE);
                        }

                        if (directionModel.getResponseData().get(0).getLike().equalsIgnoreCase("1")) {
                            binding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
                        } else if (!directionModel.getResponseData().get(0).getLike().equalsIgnoreCase("0")) {
                            binding.ivLike.setImageResource(R.drawable.ic_like_white_icon);
                        }

                        binding.llAddPlaylist.setOnClickListener(view -> {
                            myBackPress = true;
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            playerpos = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
                            p = new Properties();
                            p.putValue("userId", UserID);
                            if (!comeFrom.equalsIgnoreCase("")) {
                                if (comeFrom.equalsIgnoreCase("myDownloadPlaylist")) {
                                    if (downloadAudioDetailsList.contains(mDataDownload.get(position).getName())) {
                                        p.putValue("audioType", "Downloaded");
                                    } else {
                                        p.putValue("audioType", "Streaming");
                                    }
                                    p.putValue("audioId", mDataDownload.get(position).getID());
                                    p.putValue("audioName", mDataDownload.get(position).getName());
                                    p.putValue("audioDescription", "");
                                    p.putValue("directions", mDataDownload.get(position).getAudioDirection());
                                    p.putValue("masterCategory", mDataDownload.get(position).getAudiomastercat());
                                    p.putValue("subCategory", mDataDownload.get(position).getAudioSubCategory());
                                    p.putValue("audioDuration", mDataDownload.get(position).getAudioDuration());
                                } else if (comeFrom.equalsIgnoreCase("myLikeAudioList")) {
                                    if (downloadAudioDetailsList.contains(mDataLike.get(position).getName())) {
                                        p.putValue("audioType", "Downloaded");
                                    } else {
                                        p.putValue("audioType", "Streaming");
                                    }
                                    p.putValue("audioId", mDataLike.get(position).getID());
                                    p.putValue("audioName", mDataLike.get(position).getName());
                                    p.putValue("audioDescription", "");
                                    p.putValue("directions", mDataLike.get(position).getAudioDirection());
                                    p.putValue("masterCategory", mDataLike.get(position).getAudiomastercat());
                                    p.putValue("subCategory", mDataLike.get(position).getAudioSubCategory());
                                    p.putValue("audioDuration", mDataLike.get(position).getAudioDuration());
                                } else {
                                    if (downloadAudioDetailsList.contains(mData.get(position).getName())) {
                                        p.putValue("audioType", "Downloaded");
                                    } else {
                                        p.putValue("audioType", "Streaming");
                                    }
                                    p.putValue("audioId", mData.get(position).getID());
                                    p.putValue("audioName", mData.get(position).getName());
                                    p.putValue("audioDescription", "");
                                    p.putValue("directions", mData.get(position).getAudioDirection());
                                    p.putValue("masterCategory", mData.get(position).getAudiomastercat());
                                    p.putValue("subCategory", mData.get(position).getAudioSubCategory());
                                    p.putValue("audioDuration", mData.get(position).getAudioDuration());
                                }

                                p.putValue("position", GetCurrentAudioPosition());
                                p.putValue("source", GetSourceName(ctx));
                                p.putValue("playerType", "Mini");
                            } else {
                                p.putValue("audioId", mainPlayModelList.get(playerpos).getID());
                                p.putValue("audioName", mainPlayModelList.get(playerpos).getName());
                                p.putValue("audioDescription", "");
                                p.putValue("directions", mainPlayModelList.get(playerpos).getAudioDirection());
                                p.putValue("masterCategory", mainPlayModelList.get(playerpos).getAudiomastercat());
                                p.putValue("subCategory", mainPlayModelList.get(playerpos).getAudioSubCategory());
                                p.putValue("audioDuration", mainPlayModelList.get(playerpos).getAudioDuration());
                                p.putValue("position", GetCurrentAudioPosition());
                                if (downloadAudioDetailsList.contains(mainPlayModelList.get(position).getName())) {
                                    p.putValue("audioType", "Downloaded");
                                } else {
                                    p.putValue("audioType", "Streaming");
                                }
                                p.putValue("source", GetSourceName(ctx));
                                p.putValue("playerType", "Main");
                            }
                            p.putValue("bitRate", "");
                            p.putValue("sound", String.valueOf(hundredVolume));
                            BWSApplication.addToSegment("Add to Playlist Clicked", p, CONSTANTS.track);
                            myBackPress = true;
                            Intent i = new Intent(ctx, AddPlaylistActivity.class);
                            i.putExtra("AudioId", AudioId);
                            i.putExtra("ScreenView", "Audio Details Screen");
                            i.putExtra("PlaylistID", "");
                            i.putExtra("PlaylistName", "");
                            i.putExtra("PlaylistImage", "");
                            i.putExtra("PlaylistType", "");
                            i.putExtra("Liked", "0");
                            startActivity(i);
                        });

                        binding.llViewQueue.setOnClickListener(view -> {
                            myBackPress = true;
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            Intent i = new Intent(ctx, ViewQueueActivity.class);
                            i.putExtra("ComeFromQueue", "1");
                            i.putExtra("ID", AudioId);
                            i.putExtra("play", play);
                            startActivity(i);
                            finish();
                        });

                        if (directionModel.getResponseData().get(0).getAudioSubCategory().equalsIgnoreCase("")) {
                            binding.rvDirlist.setVisibility(View.GONE);
                        } else {
                            binding.rvDirlist.setVisibility(View.VISIBLE);
                            String[] elements = directionModel.getResponseData().get(0).getAudioSubCategory().split(",");
                            List<String> direction = Arrays.asList(elements);

                            DirectionAdapter directionAdapter = new DirectionAdapter(direction, ctx);
                            RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
                            binding.rvDirlist.setLayoutManager(recentlyPlayed);
                            binding.rvDirlist.setItemAnimator(new DefaultItemAnimator());
                            binding.rvDirlist.setAdapter(directionAdapter);
                        }
                    }
                }

                @Override
                public void onFailure(Call<DirectionModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                    BWSApplication.showToast(t.getMessage(), ctx);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }

    }

    public void GetMedia(String AudioFile, Context ctx, String download, String PlayListId, String audioFileName) {
        DatabaseClient
                .getInstance(this)
                .getaudioDatabase()
                .taskDao()
                .getaudioByPlaylist1(AudioFile, "").observe(this, audioList -> {
            if (audioList.size() != 0) {
                callDisableDownload();
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
                    binding.llDownload.setClickable(true);
                    binding.llDownload.setEnabled(true);
                    binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
                    binding.ivDownloads.setVisibility(View.VISIBLE);
                } else {
                    binding.llDownload.setClickable(false);
                    binding.llDownload.setEnabled(false);
                    binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
                    binding.ivDownloads.setColorFilter(getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
                }
            }
        });
    }

    private void callDisableDownload() {
        binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
        binding.ivDownloads.setColorFilter(getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
        binding.tvDownloads.setTextColor(activity.getResources().getColor(R.color.white));
        binding.llDownload.setClickable(false);
        binding.llDownload.setEnabled(false);
    }
    class AppLifecycleCallback implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (numStarted == 0) {
                stackStatus = 1;
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
                if (!myBackPress) {
                    Log.e("APPLICATION", "Back press false");
                    stackStatus = 2;
                } else {
                    myBackPress = true;
                    stackStatus = 1;
                    Log.e("APPLICATION", "back press true ");
                }
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
            if (numStarted == 0 && stackStatus == 2) {
                Log.e("Destroy", "Activity Destoryed");
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(notificationId);
                relesePlayer(getApplicationContext());
            } else {
                Log.e("Destroy", "Activity go in main activity");
            }
        }
    }

}