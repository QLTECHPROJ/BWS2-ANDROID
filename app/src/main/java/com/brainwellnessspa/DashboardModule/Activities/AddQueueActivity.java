package com.brainwellnessspa.DashboardModule.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Adapters.DirectionAdapter;
import com.brainwellnessspa.DashboardModule.Models.AddToQueueModel;
import com.brainwellnessspa.DashboardModule.Models.AudioLikeModel;
import com.brainwellnessspa.DashboardModule.Models.DirectionModel;
import com.brainwellnessspa.DashboardModule.Models.SubPlayListModel;
import com.brainwellnessspa.DashboardModule.Models.SucessModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia;
import com.brainwellnessspa.EncryptDecryptUtils.FileUtils;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivityQueueBinding;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.DashboardModule.Activities.MyPlaylistActivity.ComeFindAudio;
import static com.brainwellnessspa.Utility.MusicService.isMediaStart;
import static com.brainwellnessspa.Utility.MusicService.isPause;
import static com.brainwellnessspa.Utility.MusicService.isPrepare;
import static com.brainwellnessspa.Utility.MusicService.stopMedia;

public class AddQueueActivity extends AppCompatActivity {
    public static boolean comeFromAddToQueue = false;
    ActivityQueueBinding binding;
    String play, UserID, PlaylistId, AudioId, Like, Download, IsRepeat, IsShuffle, myPlaylist = "", comeFrom = "", audioFileName,
            AudioFile = "", PlaylistAudioId = "", AudioFlag;
    Context ctx;
    Activity activity;
    ArrayList<String> queue;
    ArrayList<AddToQueueModel> addToQueueModelList;
    ArrayList<MainPlayModel> mainPlayModelList;
    ArrayList<SubPlayListModel.ResponseData.PlaylistSong> mData;
    MainPlayModel mainPlayMode;
    AddToQueueModel addToQueueModel;
    int position, listSize;
    Boolean queuePlay, audioPlay;
    List<DownloadAudioDetails> oneAudioDetailsList;
    SharedPreferences shared;
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
        ctx = AddQueueActivity.this;
        activity = AddQueueActivity.this;
        oneAudioDetailsList = new ArrayList<>();
//        handler1 = new Handler();
//        fileNameList = new ArrayList<>();
        addToQueueModelList = new ArrayList<>();
        mainPlayModelList = new ArrayList<>();
        mData = new ArrayList<>();
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
            mData = getIntent().getParcelableArrayListExtra("data");
        } else {
            comeFrom = "";
        }

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
                callDownload());


        binding.llAddQueue.setOnClickListener(view ->
                callAddToQueue());

        binding.llRepeat.setOnClickListener(view -> callRepeat());

        binding.llShuffle.setOnClickListener(view -> callShuffle());

        binding.llRemovePlaylist.setOnClickListener(view -> {
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            int pos = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
            String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
            if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistId)&& mainPlayModelList.size() == 1) {
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
        prepareData();
    }

    private void callBack() {
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
      /*      SharedPreferences shared11 = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = shared11.edit();
            Gson gson11 = new Gson();
            String json11 = gson11.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, json11);
            editor.commit();*/
            finish();
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
                BWSApplication.showToast("Shuffle mode has been turned on", ctx);
                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        } else if (IsShuffle.equalsIgnoreCase("1")) {
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(CONSTANTS.PREF_KEY_IsShuffle, "");
            editor.commit();
            IsShuffle = "";
            BWSApplication.showToast("Shuffle mode has been turned off", ctx);
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
            BWSApplication.showToast("Repeat mode has been turned on", ctx);
            binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (IsRepeat.equalsIgnoreCase("0")) {
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "1");
            IsRepeat = "1";
            if (listSize == 1) {
                editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "");
                IsRepeat = "";
                BWSApplication.showToast("Repeat mode has been turned off", ctx);
                binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                BWSApplication.showToast("Repeat mode has been turned on", ctx);
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
            BWSApplication.showToast("Repeat mode has been turned off", ctx);
        }
    }

    private void callAddToQueue() {
        addToQueueModel = new AddToQueueModel();
        int i = position;
        if (!comeFrom.equalsIgnoreCase("")) {
            addToQueueModel.setID(mData.get(i).getID());
            addToQueueModel.setName(mData.get(i).getName());
            addToQueueModel.setAudioFile(mData.get(i).getAudioFile());
            AudioFile = mData.get(i).getAudioFile();
            PlaylistId = mData.get(i).getPlaylistID();
            addToQueueModel.setPlaylistID(mData.get(i).getPlaylistID());
            addToQueueModel.setAudioDirection(mData.get(i).getAudioDirection());
            addToQueueModel.setAudiomastercat(mData.get(i).getAudiomastercat());
            addToQueueModel.setAudioSubCategory(mData.get(i).getAudioSubCategory());
            addToQueueModel.setImageFile(mData.get(i).getImageFile());
            addToQueueModel.setLike(mData.get(i).getLike());
            addToQueueModel.setDownload(mData.get(i).getDownload());
            addToQueueModel.setAudioDuration(mData.get(i).getAudioDuration());
        } else {
            addToQueueModel.setID(mainPlayModelList.get(i).getID());
            addToQueueModel.setName(mainPlayModelList.get(i).getName());
            addToQueueModel.setAudioFile(mainPlayModelList.get(i).getAudioFile());
            AudioFile = mainPlayModelList.get(i).getAudioFile();
            PlaylistId = mainPlayModelList.get(i).getPlaylistID();
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
            BWSApplication.showToast("Audio has been added to queue", ctx);
            addToQueueModelList.add(addToQueueModel);
        } else {
            for (int x = 0; x < addToQueueModelList.size(); x++) {
                if (addToQueueModelList.get(x).getAudioFile().equals(addToQueueModel.getAudioFile())) {
                    if (queuePlay && addToQueueModelList.get(position).getAudioFile().equals(addToQueueModel.getAudioFile())) {
                        BWSApplication.showToast("Audio has been added to queue", ctx);
                        addToQueueModelList.add(addToQueueModel);
                        break;
                    } else {
                        addToQueueModel = new AddToQueueModel();
                        BWSApplication.showToast("Already in Queue", ctx);
                        break;
                    }
                } else if (x == (addToQueueModelList.size() - 1)) {
                    BWSApplication.showToast("Audio has been added to queue", ctx);
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
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<SucessModel> listCall = APIClient.getClient().getRemoveAudioFromPlaylist(UserID, AudioId, PlaylistId);
            listCall.enqueue(new Callback<SucessModel>() {
                @Override
                public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        SucessModel listModel = response.body();
                        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
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
                                    if (pID.equalsIgnoreCase(PlaylistId)) {
                                        if (mData.size() != 0) {
                                            if (pos == position && position < mData.size() - 1) {
                                                pos = pos;
                                                if (isPrepare || isMediaStart || isPause) {
                                                    stopMedia();
                                                }
                                                isPause = false;
                                                isMediaStart = false;
                                                isPrepare = false;
                                            } else if (pos == position && position == mData.size() - 1) {
                                                pos = 0;
                                                if (isPrepare || isMediaStart || isPause) {
                                                    stopMedia();
                                                }
                                                isPause = false;
                                                isMediaStart = false;
                                                isPrepare = false;
                                            } else if (pos < position && pos < mData.size() - 1) {
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
                                            SharedPreferences sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                                            SharedPreferences.Editor editor1 = sharedz.edit();
                                            Gson gsonz = new Gson();
                                            String jsonz = gsonz.toJson(mainPlayModelList);
                                            editor1.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
                                            editor1.commit();
                                        }
                                        comeFromAddToQueue = true;
                                    }
                                } else {
                                    mainPlayModelList.remove(pos);
                                    arrayList1.remove(pos);
                                    String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                                    if (pID.equalsIgnoreCase(PlaylistId)) {
                                        if (mainPlayModelList.size() != 0) {
                                            if (pos < mainPlayModelList.size() - 1) {
                                                pos = pos;
                                            } else if (pos == mainPlayModelList.size() - 1) {
                                                pos = 0;
                                            } else if (pos > mainPlayModelList.size()) {
                                                pos = pos - 1;
                                            }
                                            if (isPrepare || isMediaStart || isPause) {
                                                stopMedia();
                                            }
                                            isPause = false;
                                            isMediaStart = false;
                                            isPrepare = false;
                                            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = new ArrayList<>();
                                            for (int i = 0; i < mainPlayModelList.size(); i++) {
                                                SubPlayListModel.ResponseData.PlaylistSong mainPlayModel = new SubPlayListModel.ResponseData.PlaylistSong();
                                                mainPlayModel.setID(mainPlayModelList.get(i).getID());
                                                mainPlayModel.setName(mainPlayModelList.get(i).getName());
                                                mainPlayModel.setAudioFile(mainPlayModelList.get(i).getAudioFile());
                                                mainPlayModel.setPlaylistID(mainPlayModelList.get(i).getPlaylistID());
                                                mainPlayModel.setAudioDirection(mainPlayModelList.get(i).getAudioDirection());
                                                mainPlayModel.setAudiomastercat(mainPlayModelList.get(i).getAudiomastercat());
                                                mainPlayModel.setAudioSubCategory(mainPlayModelList.get(i).getAudioSubCategory());
                                                mainPlayModel.setImageFile(mainPlayModelList.get(i).getImageFile());
                                                mainPlayModel.setLike(mainPlayModelList.get(i).getLike());
                                                mainPlayModel.setDownload(mainPlayModelList.get(i).getDownload());
                                                mainPlayModel.setAudioDuration(mainPlayModelList.get(i).getAudioDuration());
                                                arrayList.add(mainPlayModel);
                                            }
                                            SharedPreferences sharedd = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedd.edit();
                                            Gson gson = new Gson();
                                            String json = gson.toJson(mainPlayModelList);
                                            String json1 = gson.toJson(arrayList);
                                            editor.putString(CONSTANTS.PREF_KEY_modelList, json1);
                                            editor.putString(CONSTANTS.PREF_KEY_audioList, json);
                                            editor.putInt(CONSTANTS.PREF_KEY_position, pos);
                                            editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                                            editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                                            editor.putString(CONSTANTS.PREF_KEY_PlaylistId, PlaylistId);
                                            editor.putString(CONSTANTS.PREF_KEY_myPlaylist, myPlaylist);
                                            editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SubPlayList");
                                            editor.commit();
                                            comeFromAddToQueue = true;
                                        }
                                    }
                                }
                            }
                        }
                        finish();
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
      /*  if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<DownloadPlaylistModel> listCall = APIClient.getClient().getDownloadlistPlaylist(UserID, AudioId, PlaylistId);
            listCall.enqueue(new Callback<DownloadPlaylistModel>() {
                @Override
                public void onResponse(Call<DownloadPlaylistModel> call, Response<DownloadPlaylistModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        DownloadPlaylistModel model = response.body();
                        if (model.getResponseData().getFlag().equalsIgnoreCase("0")
                                || model.getResponseData().getFlag().equalsIgnoreCase("")) {
                            binding.llDownload.setClickable(true);
                            binding.llDownload.setEnabled(true);
                            binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
                            Download = "0";
                        } else if (model.getResponseData().getFlag().equalsIgnoreCase("1")) {
                            binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
                            binding.ivDownloads.setColorFilter(getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
                            binding.llDownload.setClickable(false);
                            binding.llDownload.setEnabled(false);
                            Download = "1";
                        }
                        mainPlayModelList.get(position).setDownload(Download);

                        BWSApplication.showToast(model.getResponseMessage(), ctx);
                    }
                }

                @Override
                public void onFailure(Call<DownloadPlaylistModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });

        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }*/
        int i = position;
        String audioFile = "", Name = "";
        if (!comeFrom.equalsIgnoreCase("")) {
            Name = mData.get(i).getName();
            audioFile = mData.get(i).getAudioFile();
        } else {
            Name = mainPlayModelList.get(i).getName();
            audioFile = mainPlayModelList.get(i).getAudioFile();
        }
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
//        fileNameList = url1;
        callDisableDownload();
        DownloadMedia downloadMedia = new DownloadMedia(getApplicationContext());
        downloadMedia.encrypt1(url1, name1,downloadPlaylistId);
        /*if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(audioFileName)) {
            handler1.postDelayed(UpdateSongTime1, 500);
        } else {
            binding.pbProgress.setVisibility(View.GONE);
            handler1.removeCallbacks(UpdateSongTime1);
        }*/
        String dirPath = FileUtils.getFilePath(getApplicationContext(), Name);
        SaveMedia(new byte[1024], dirPath, i);

    }

    private void SaveMedia(byte[] encodeBytes, String dirPath, int i) {
        class SaveMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                DownloadAudioDetails downloadAudioDetails = new DownloadAudioDetails();
                if (!comeFrom.equalsIgnoreCase("")) {
                    downloadAudioDetails.setID(mData.get(i).getID());
                    downloadAudioDetails.setName(mData.get(i).getName());
                    downloadAudioDetails.setAudioFile(mData.get(i).getAudioFile());
                    downloadAudioDetails.setPlaylistId(mData.get(i).getPlaylistID());
                    downloadAudioDetails.setAudioDirection(mData.get(i).getAudioDirection());
                    downloadAudioDetails.setAudiomastercat(mData.get(i).getAudiomastercat());
                    downloadAudioDetails.setAudioSubCategory(mData.get(i).getAudioSubCategory());
                    downloadAudioDetails.setImageFile(mData.get(i).getImageFile());
                    downloadAudioDetails.setLike(mData.get(i).getLike());
                    downloadAudioDetails.setDownload("1");
                    downloadAudioDetails.setAudioDuration(mData.get(i).getAudioDuration());
                    downloadAudioDetails.setIsSingle("1");
                    downloadAudioDetails.setPlaylistId("");
                    downloadAudioDetails.setIsDownload("pending");
                    downloadAudioDetails.setDownloadProgress(0);
                } else {
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
                DatabaseClient.getInstance(activity)
                        .getaudioDatabase()
                        .taskDao()
                        .insertMedia(downloadAudioDetails);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                callDisableDownload();
                super.onPostExecute(aVoid);
            }
        }
        SaveMedia st = new SaveMedia();
        st.execute();
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
                        if (queuePlay) {
                            addToQueueModelList.get(position).setLike(Like);
                        } else
                            mainPlayModelList.get(position).setLike(Like);
                        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                        SharedPreferences.Editor editor = shared.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(mainPlayModelList);
                        editor.putString(CONSTANTS.PREF_KEY_audioList, json);
                        String json1 = gson.toJson(addToQueueModelList);
                        if (queuePlay) {
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


                        if (AudioFlag.equalsIgnoreCase("MainAudioList")) {
                            binding.llOptions.setVisibility(View.VISIBLE);
                            binding.llRemovePlaylist.setVisibility(View.GONE);
                        }  else if (play.equalsIgnoreCase("TopCategories")) {
                            binding.llOptions.setVisibility(View.VISIBLE);
                            binding.llRemovePlaylist.setVisibility(View.GONE);
                        } else if (play.equalsIgnoreCase("play")) {
                            binding.llOptions.setVisibility(View.VISIBLE);
                            binding.llRemovePlaylist.setVisibility(View.VISIBLE);
                        } else if (play.equalsIgnoreCase("playlist")) {
                            binding.llOptions.setVisibility(View.VISIBLE);
                            binding.llRemovePlaylist.setVisibility(View.GONE);
                        } else if (play.equalsIgnoreCase("myPlayList")) {
                            binding.llOptions.setVisibility(View.VISIBLE);
                            binding.llRemovePlaylist.setVisibility(View.VISIBLE);
                        } else {
                            binding.llOptions.setVisibility(View.VISIBLE);
                            binding.llRemovePlaylist.setVisibility(View.GONE);
                        }

                        DirectionModel directionModel = response.body();
                        int ix = position;
                        if (!comeFrom.equalsIgnoreCase("")) {
                            AudioFile = mData.get(ix).getAudioFile();
                            PlaylistId = mData.get(ix).getPlaylistID();
                            audioFileName = mData.get(ix).getName();
                        } else {
                            AudioFile = mainPlayModelList.get(ix).getAudioFile();
                            PlaylistId = mainPlayModelList.get(ix).getPlaylistID();
                            audioFileName = mainPlayModelList.get(ix).getName();
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
                        GetMedia(AudioFile, activity, directionModel.getResponseData().get(0).getDownload(), PlaylistId);
                        binding.cvImage.setVisibility(View.VISIBLE);
                        binding.llLike.setVisibility(View.VISIBLE);
                        binding.llAddPlaylist.setVisibility(View.VISIBLE);
                        binding.llAddQueue.setVisibility(View.VISIBLE);
                        binding.llDownload.setVisibility(View.VISIBLE);
                        binding.llShuffle.setVisibility(View.VISIBLE);
                        binding.llRepeat.setVisibility(View.VISIBLE);
                        binding.llViewQueue.setVisibility(View.VISIBLE);
                        Glide.with(ctx).load(directionModel.getResponseData().get(0).getImageFile()).thumbnail(0.05f)
                                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);

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

                        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
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
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            Intent i = new Intent(ctx, AddPlaylistActivity.class);
                            i.putExtra("AudioId", AudioId);
                            i.putExtra("PlaylistID", "");
                            startActivity(i);
                        });

                        binding.llViewQueue.setOnClickListener(view -> {
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

    public void GetMedia(String AudioFile, Context ctx, String download, String PlayListId) {

        oneAudioDetailsList = new ArrayList<>();
        class GetMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                oneAudioDetailsList = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .getaudioByPlaylist(AudioFile, PlayListId);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (oneAudioDetailsList.size() != 0) {
                    if (oneAudioDetailsList.get(0).getDownload().equalsIgnoreCase("1")) {
                        callDisableDownload();
                    }
                } else if (download.equalsIgnoreCase("1")) {
                    callDisableDownload();
                } else {
                    binding.llDownload.setClickable(true);
                    binding.llDownload.setEnabled(true);
                    binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
                }
                super.onPostExecute(aVoid);
            }
        }
        GetMedia st = new GetMedia();
        st.execute();
    }

    private void callDisableDownload() {
        binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
        binding.ivDownloads.setColorFilter(getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
        binding.tvDownloads.setTextColor(activity.getResources().getColor(R.color.white));
        binding.llDownload.setClickable(false);
        binding.llDownload.setEnabled(false);
    }

}