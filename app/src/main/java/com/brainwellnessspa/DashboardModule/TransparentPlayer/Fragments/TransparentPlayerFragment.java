package com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.media.MediaSessionManager;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Activities.PlayWellnessActivity;
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
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MusicService;
import com.brainwellnessspa.Utility.PlaybackStatus;
import com.brainwellnessspa.databinding.FragmentTransparentPlayerBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.ComeScreenAccount;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.player;
import static com.brainwellnessspa.DownloadModule.Adapters.AudioDownlaodsAdapter.comefromDownload;
import static com.brainwellnessspa.Utility.MusicService.SeekTo;
import static com.brainwellnessspa.Utility.MusicService.getEndTime;
import static com.brainwellnessspa.Utility.MusicService.getProgressPercentage;
import static com.brainwellnessspa.Utility.MusicService.getStartTime;
import static com.brainwellnessspa.Utility.MusicService.isCompleteStop;
import static com.brainwellnessspa.Utility.MusicService.isMediaStart;
import static com.brainwellnessspa.Utility.MusicService.isPause;
import static com.brainwellnessspa.Utility.MusicService.isPrepare;
import static com.brainwellnessspa.Utility.MusicService.isPreparing;
import static com.brainwellnessspa.Utility.MusicService.isStop;
import static com.brainwellnessspa.Utility.MusicService.isprogressbar;
import static com.brainwellnessspa.Utility.MusicService.mediaPlayer;
import static com.brainwellnessspa.Utility.MusicService.oTime;
import static com.brainwellnessspa.Utility.MusicService.pauseMedia;
import static com.brainwellnessspa.Utility.MusicService.progressToTimer;
import static com.brainwellnessspa.Utility.MusicService.resumeMedia;
import static com.brainwellnessspa.Utility.MusicService.savePrefQueue;
import static com.brainwellnessspa.Utility.MusicService.stopMedia;

public class TransparentPlayerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener/*, AudioManager.OnAudioFocusChangeListener*/ {
    public static int isDisclaimer = 0;
    public FragmentTransparentPlayerBinding binding;
    String UserID, AudioFlag, IsRepeat, IsShuffle, audioFile, id, name;
    int position = 0, startTime, listSize, myCount;
    MainPlayModel mainPlayModel;
    Boolean queuePlay, audioPlay;
    ArrayList<MainPlayModel> mainPlayModelList;
    ArrayList<AddToQueueModel> addToQueueModelList;
    List<DownloadAudioDetails> downloadAudioDetailsList;
    Activity activity;
    Context ctx;
    long myProgress = 0, diff = 0;
    SharedPreferences shared;
    String json;
    Gson gson;
    private long totalDuration, currentDuration = 0;
    private Handler handler12;
    public static final String ACTION_PLAY = "com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.ACTION_NEXT";
    public static final String ACTION_STOP = "com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.ACTION_STOP";

    //MediaSession
    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;
    PlaybackStatus playbackStatus;
    //AudioPlayer notification ID
    private static final int NOTIFICATION_ID = 101;
    private Runnable UpdateSongTime12 = new Runnable() {
        @Override
        public void run() {
            try {
                startTime = getStartTime();
                binding.simpleSeekbar.setMax(100);
                Time t = Time.valueOf("00:00:00");
                if (queuePlay) {
                    if (listSize != 0) {
                        if (!BWSApplication.isNetworkConnected(ctx)) {
                            if (mediaPlayer != null) {
                                totalDuration = mediaPlayer.getDuration();
                            } else {
                                t = Time.valueOf("00:" + downloadAudioDetailsList.get(0).getAudioDuration());
                            }
                        } else {
                            if (mediaPlayer != null) {
                                totalDuration = mediaPlayer.getDuration();
                            } else {
                                t = Time.valueOf("00:" + addToQueueModelList.get(position).getAudioDuration());
                            }
                        }
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.ivPause.setVisibility(View.GONE);
                        binding.ivPlay.setVisibility(View.VISIBLE);
                        stopMedia();
                    }
                } else if (audioPlay) {
                    if (!BWSApplication.isNetworkConnected(ctx)) {
                        if (mediaPlayer != null) {
                            totalDuration = mediaPlayer.getDuration();
                        } else {
                            t = Time.valueOf("00:" + downloadAudioDetailsList.get(0).getAudioDuration());
                        }
                    } else {
                        if (mediaPlayer != null) {
                            totalDuration = mediaPlayer.getDuration();
                        } else {
                            t = Time.valueOf("00:" + mainPlayModelList.get(position).getAudioDuration());
                        }
                    }
                }

                if (!BWSApplication.isNetworkConnected(ctx)) {
                    if (mediaPlayer != null) {
                        totalDuration = mediaPlayer.getDuration();
                    } else
                        totalDuration = t.getTime();
                } else {
                    if (mediaPlayer != null) {
                        totalDuration = mediaPlayer.getDuration();
                    } else {
                        totalDuration = t.getTime();
                    }
                }
                myProgress = currentDuration;
                currentDuration = getStartTime();
                diff = totalDuration - myProgress;

//                Log.e("myProgress old!!!",String.valueOf(myProgress));
                if (myProgress == currentDuration && myProgress != 0 && !isPause && audioFile.equalsIgnoreCase("")) {
//                    Log.e("myProgress",String.valueOf(myProgress));
                    myCount++;
                    Log.e("myCount", String.valueOf(myCount));

                    if (myCount == 5) {
                        Log.e("myCount complete", String.valueOf(myCount));
                        callComplete();
                        Log.e("calll complete errr", "eee");
                        myCount = 0;
                    }
                } else if (myProgress == currentDuration && myProgress != 0 && !isPause && diff < 500) {
//                    Log.e("myProgress",String.valueOf(myProgress));
                    myCount++;
                    Log.e("myCount", String.valueOf(myCount));

                    if (myCount == 20) {
                        Log.e("myCount complete", String.valueOf(myCount));
                        callComplete();
                        Log.e("calll complete errr", "eee");
                        myCount = 0;
                    }
                }
                if (currentDuration == totalDuration && currentDuration != 0 && !isStop && !audioFile.equalsIgnoreCase("")) {
                    callComplete();
                    Log.e("calll complete trans", "trans");

                }
                if (currentDuration == totalDuration && currentDuration != 0 && !isStop && audioFile.equalsIgnoreCase("")) {
                    mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                        callComplete();
                    });
                }
                int progress = (int) (getProgressPercentage(currentDuration, totalDuration));
                if (player == 1) {
                    if (currentDuration == 0 && isCompleteStop) {
                        binding.progressBar.setVisibility(View.GONE);
//                        binding.llProgress.setVisibility(View.VISIBLE);
                        binding.ivPause.setVisibility(View.GONE);
                        binding.ivPlay.setVisibility(View.VISIBLE);
                    } else if (currentDuration == 0 && !isPause) {
                        binding.progressBar.setVisibility(View.VISIBLE);
//                        binding.llProgress.setVisibility(View.VISIBLE);
                        binding.ivPause.setVisibility(View.GONE);
                        binding.ivPlay.setVisibility(View.GONE);
                    } else if (currentDuration >= 1 && !isPause) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.ivPause.setVisibility(View.VISIBLE);
                        binding.ivPlay.setVisibility(View.GONE);
                    } else if (currentDuration >= 1 && isPause) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.ivPause.setVisibility(View.GONE);
                        binding.ivPlay.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.ivPause.setVisibility(View.GONE);
                    binding.ivPlay.setVisibility(View.VISIBLE);
                }

                //Log.d("Progress", ""+progress);
                if (isPause) {
                    binding.simpleSeekbar.setProgress(oTime);
                } else {
                    binding.simpleSeekbar.setProgress(progress);
                }
                // Running this thread after 100 milliseconds
                handler12.postDelayed(this, 100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_transparent_player, container, false);
        View view = binding.getRoot();
        activity = getActivity();
        ctx = getActivity();
        mainPlayModelList = new ArrayList<>();
        addToQueueModelList = new ArrayList<>();
        downloadAudioDetailsList = new ArrayList<>();
        SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        handler12 = new Handler();
        shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        gson = new Gson();
        json = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson));
        String json1 = shared.getString(CONSTANTS.PREF_KEY_queueList, String.valueOf(gson));
        if (!json1.equalsIgnoreCase(String.valueOf(gson))) {
            Type type1 = new TypeToken<ArrayList<AddToQueueModel>>() {
            }.getType();
            addToQueueModelList = gson.fromJson(json1, type1);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 130);
        binding.llLayout.setLayoutParams(params);

        if (comefromDownload.equalsIgnoreCase("1")) {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            param.setMargins(0, 0, 0, 0);
            binding.llLayout.setLayoutParams(param);
        } else {
            LinearLayout.LayoutParams paramm = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramm.setMargins(0, 0, 0, 130);
            binding.llLayout.setLayoutParams(paramm);

        }
        if (isMediaStart) {
            mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                callComplete();
                Log.e("calll complete real", "real");
            });
        }

        queuePlay = shared.getBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
        audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
        position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        binding.simpleSeekbar.setOnSeekBarChangeListener(this);
        SharedPreferences Status = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
        IsRepeat = Status.getString(CONSTANTS.PREF_KEY_IsRepeat, "");
        IsShuffle = Status.getString(CONSTANTS.PREF_KEY_IsShuffle, "");
    /*    mAudioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);*/
        if (queuePlay) {
            getPrepareShowData();
        } else if (audioPlay) {
            MakeArray();
        }
        if (listSize == 1) {
            IsShuffle = "";
        }
        binding.ivPause.setOnClickListener(view1 -> {
            handler12.removeCallbacks(UpdateSongTime12);
            binding.simpleSeekbar.setProgress(binding.simpleSeekbar.getProgress());
            if (!isMediaStart) {
//                callAsyncTask();
                callMedia();
            } else {
                pauseMedia();
                binding.ivPause.setVisibility(View.GONE);
                binding.ivPlay.setVisibility(View.VISIBLE);
            }
            oTime = binding.simpleSeekbar.getProgress();
        });

        binding.ivPlay.setOnClickListener(view12 -> {
            if (!isMediaStart) {
                isCompleteStop = false;
                isprogressbar = true;
                handler12.postDelayed(UpdateSongTime12, 500);
                binding.progressBar.setVisibility(View.VISIBLE);
//                binding.llProgress.setVisibility(View.GONE);
                binding.ivPlay.setVisibility(View.GONE);
                binding.ivPause.setVisibility(View.GONE);
                callMedia();
            } else if (isCompleteStop) {
                isCompleteStop = false;
                isprogressbar = true;
                handler12.postDelayed(UpdateSongTime12, 500);
                binding.progressBar.setVisibility(View.VISIBLE);
//                binding.llProgress.setVisibility(View.GONE);
                binding.ivPlay.setVisibility(View.GONE);
                binding.ivPause.setVisibility(View.GONE);
                callMedia();
            } else {
                resumeMedia();
                binding.progressBar.setVisibility(View.GONE);
//                binding.llProgress.setVisibility(View.GONE);
                binding.ivPlay.setVisibility(View.GONE);
                binding.ivPause.setVisibility(View.VISIBLE);
                isPause = false;
            }
            player = 1;
            handler12.postDelayed(UpdateSongTime12, 100);
        });

        return view;
    }

    private void MakeArray() {
        shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
        json = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson));
        mainPlayModelList = new ArrayList<>();
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
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData();

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
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData();
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
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData();
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
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData();
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
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData();
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
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData();
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
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData();
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
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData();
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
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData();
        }
    }

    private void addToRecentPlay() {
        if (BWSApplication.isNetworkConnected(ctx)) {
//            BWSApplication.showProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
            Call<SucessModel> listCall = APIClient.getClient().getRecentlyplayed(id, UserID);
            listCall.enqueue(new Callback<SucessModel>() {
                @Override
                public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                    if (response.isSuccessful()) {
//                        BWSApplication.hideProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
                        SucessModel model = response.body();
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

    public void GetMedia(String url, Context ctx) {
        try {
            downloadAudioDetailsList = new ArrayList<>();
            class GetMedia extends AsyncTask<Void, Void, Void> {
                @Override
                protected Void doInBackground(Void... voids) {
                    downloadAudioDetailsList = DatabaseClient
                            .getInstance(ctx)
                            .getaudioDatabase()
                            .taskDao()
                            .getLastIdByuId(url);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    try {
                        if (audioPlay) {
                            if (listSize != 0) {
                                binding.tvTitle.setText(mainPlayModelList.get(position).getName());
                                binding.tvSubTitle.setText(mainPlayModelList.get(position).getAudioDirection());
                                try {
                                    if (audioFile.equalsIgnoreCase("")) {
                                        Glide.with(ctx).load(R.drawable.disclaimer).thumbnail(0.05f)
                                                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
                                    } else {
                                        Glide.with(ctx).load(mainPlayModelList.get(position).getImageFile()).thumbnail(0.05f)
                                                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        if (player == 1) {
                            binding.progressBar.setVisibility(View.GONE);
//                    binding.llProgress.setVisibility(View.GONE);
                            if (isPause) {
                                binding.progressBar.setVisibility(View.GONE);
//                        binding.llProgress.setVisibility(View.GONE);
                                binding.ivPause.setVisibility(View.GONE);
                                binding.ivPlay.setVisibility(View.VISIBLE);
                                binding.simpleSeekbar.setProgress(oTime);
                            } else if (isCompleteStop) {
                                binding.progressBar.setVisibility(View.GONE);
                                binding.ivPlay.setVisibility(View.VISIBLE);
                                binding.ivPause.setVisibility(View.GONE);
                            } else if (isMediaStart && !isPause) {
                                binding.progressBar.setVisibility(View.GONE);
//                        binding.llProgress.setVisibility(View.GONE);
                                binding.ivPause.setVisibility(View.VISIBLE);
                                binding.ivPlay.setVisibility(View.GONE);
                            } else {
                                binding.progressBar.setVisibility(View.VISIBLE);
//                        binding.llProgress.setVisibility(View.VISIBLE);
                                binding.ivPause.setVisibility(View.GONE);
                                binding.ivPlay.setVisibility(View.GONE);
                                callMedia();
                            }
                        } else {
                            binding.progressBar.setVisibility(View.GONE);

//                    binding.llProgress.setVisibility(View.GONE);
                            binding.ivPause.setVisibility(View.GONE);
                            binding.ivPlay.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    super.onPostExecute(aVoid);

                }
            }

            GetMedia st = new GetMedia();
            st.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPrepareShowData() {
        handler12.postDelayed(UpdateSongTime12, 100);
        try {
            if (queuePlay) {
                listSize = addToQueueModelList.size();
                if (listSize == 1) {
                    position = 0;
                }
                if (position == listSize) {
                    position = position - 1;
                }
                if (listSize != 0) {
                    id = addToQueueModelList.get(position).getID();
                    name = addToQueueModelList.get(position).getName();
                    audioFile = addToQueueModelList.get(position).getAudioFile();
                    GetMedia(audioFile, ctx);
                    Glide.with(ctx).load(addToQueueModelList.get(position).getImageFile()).thumbnail(0.05f)
                            .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
                    binding.tvTitle.setText(addToQueueModelList.get(position).getName());
                    binding.tvSubTitle.setText(addToQueueModelList.get(position).getAudioDirection());
                    handler12.postDelayed(UpdateSongTime12, 100);
                }
            } else if (audioPlay) {
                listSize = mainPlayModelList.size();
                if (listSize == 1) {
                    position = 0;
                }
                if (listSize != 0) {
                    id = mainPlayModelList.get(position).getID();
                    name = mainPlayModelList.get(position).getName();
                    audioFile = mainPlayModelList.get(position).getAudioFile();
                    binding.tvTitle.setText(mainPlayModelList.get(position).getName());
                    binding.tvSubTitle.setText(mainPlayModelList.get(position).getAudioDirection());
                    if (audioFile.equalsIgnoreCase("")) {
                        Glide.with(ctx).load(R.drawable.disclaimer).thumbnail(0.05f)
                                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
                    } else {
                        Glide.with(ctx).load(mainPlayModelList.get(position).getImageFile()).thumbnail(0.05f)
                                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
                    }
                    GetMedia(audioFile, ctx);
                    handler12.postDelayed(UpdateSongTime12, 100);
                    if (audioFile.equalsIgnoreCase("") || audioFile.isEmpty()) {
                        isDisclaimer = 1;
                        binding.simpleSeekbar.setClickable(false);
                        binding.simpleSeekbar.setEnabled(false);
                    } else {
                        isDisclaimer = 0;
                        binding.simpleSeekbar.setClickable(true);
                        binding.simpleSeekbar.setEnabled(true);
                    }
                }
            }
            startTime = getStartTime();
            simple_Notification(playbackStatus, mainPlayModelList);
            if (!audioFile.equalsIgnoreCase("")) {
                addToRecentPlay();
            }
            binding.llPlayearMain.setOnClickListener(view -> {
                handler12.removeCallbacks(UpdateSongTime12);
                if (player == 0) {
                    player = 1;
                }
                if (!isPause && binding.progressBar.getVisibility() == View.GONE) {
                    isPause = false;
                    isprogressbar = false;
                } else if (isPause && binding.progressBar.getVisibility() == View.GONE) {
                    isPause = true;
                    isprogressbar = false;
                } else if (isCompleteStop && binding.progressBar.getVisibility() == View.GONE) {
                    isprogressbar = false;
                } else if (binding.progressBar.getVisibility() == View.VISIBLE && (binding.ivPause.getVisibility() == View.GONE && binding.ivPlay.getVisibility() == View.GONE)) {
                    isprogressbar = true;
                }
                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                Gson gson = new Gson();
                String json = gson.toJson(mainPlayModelList);
                editor.putString(CONSTANTS.PREF_KEY_audioList, json);
                String json1 = gson.toJson(addToQueueModelList);
                if (queuePlay) {
                    editor.putString(CONSTANTS.PREF_KEY_queueList, json1);
                }
                editor.putInt(CONSTANTS.PREF_KEY_position, position);
                editor.commit();
                handler12.removeCallbacks(UpdateSongTime12);
                Intent i = new Intent(ctx, PlayWellnessActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                ctx.startActivity(i);

//            simpleNotification();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMediaPlayer(String download, FileDescriptor fileDescriptor) {
        if (download.equalsIgnoreCase("2")) {
            mediaPlayer = MediaPlayer.create(getActivity(), R.raw.brain_wellness_spa_declaimer);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            Uri uri = Uri.parse("android.resource://com.brainwellnessspa/" + R.raw.brain_wellness_spa_declaimer);
//            mediaPlayer.setDataSource(String.valueOf(uri));
            mediaPlayer.start();
            isPrepare = true;
            isMediaStart = true;
            binding.progressBar.setVisibility(View.GONE);
            binding.ivPause.setVisibility(View.VISIBLE);
            binding.ivPlay.setVisibility(View.GONE);
        } else {
            if (null == mediaPlayer) {
                mediaPlayer = new MediaPlayer();
                Log.e("Playinggggg", "Playinggggg");
            }
            try {
                if (mediaPlayer == null)
                    mediaPlayer = new MediaPlayer();
                if (mediaPlayer.isPlaying()) {
                    Log.e("Playinggggg", "stoppppp");
                    mediaPlayer.stop();
                    isMediaStart = false;
                    isPrepare = false;
                }
                isPreparing = true;
                mediaPlayer = new MediaPlayer();
                if (download.equalsIgnoreCase("1")) {
                    mediaPlayer.setDataSource(fileDescriptor);
                } else {
                    mediaPlayer.setDataSource(audioFile);
                    Log.e("Playinggggxxxxx", "Startinggg1xxxxx");
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mediaPlayer.setAudioAttributes(
                            new AudioAttributes
                                    .Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .build());
                    Log.e("Playinggggg11111111", "Startinggg111111111");
                }
                mediaPlayer.prepareAsync();
                isPrepare = true;
            } catch (IllegalStateException | IOException e) {
                FileDescriptor fileDescriptor1 = null;
                setMediaPlayer("0", fileDescriptor1);
                e.printStackTrace();
            }
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.setOnPreparedListener(mp -> {
                    Log.e("Playinggggg", "Startinggg");
                    mediaPlayer.start();
                    isMediaStart = true;
                });
            }
        }
    }

    private void callMedia() {
        binding.progressBar.setVisibility(View.VISIBLE);
//        binding.llProgress.setVisibility(View.VISIBLE);
        binding.ivPlay.setVisibility(View.GONE);
        binding.ivPause.setVisibility(View.GONE);
        FileDescriptor fileDescriptor = null;
        if (downloadAudioDetailsList.size() != 0) {
            binding.progressBar.setVisibility(View.VISIBLE);
//        binding.llProgress.setVisibility(View.VISIBLE);
            binding.ivPlay.setVisibility(View.GONE);
            binding.ivPause.setVisibility(View.GONE);
            DownloadMedia downloadMedia = new DownloadMedia(ctx.getApplicationContext());
            try {
                byte[] decrypt = null;
                decrypt = downloadMedia.decrypt(name);
                if (decrypt != null) {
                    fileDescriptor = FileUtils.getTempFileDescriptor(ctx.getApplicationContext(), decrypt);
                    if (audioFile.equalsIgnoreCase("") || audioFile.isEmpty()) {
                        setMediaPlayer("2", fileDescriptor);
                    } else {
                        setMediaPlayer("1", fileDescriptor);
                    }
                } else {
                    if (audioFile.equalsIgnoreCase("") || audioFile.isEmpty()) {
                        setMediaPlayer("2", fileDescriptor);
                    } else {
                        if (BWSApplication.isNetworkConnected(ctx)) {
                            setMediaPlayer("0", fileDescriptor);
//                mediaPlayer.setDataSource(audioFile);
                        } else {
                            binding.progressBar.setVisibility(View.GONE);
//                        binding.llProgress.setVisibility(View.GONE);
                            binding.ivPlay.setVisibility(View.VISIBLE);
                            binding.ivPause.setVisibility(View.GONE);
                            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (audioFile.equalsIgnoreCase("") || audioFile.isEmpty()) {
                setMediaPlayer("2", fileDescriptor);
            } else {
                if (BWSApplication.isNetworkConnected(ctx)) {
                    setMediaPlayer("0", fileDescriptor);
//                mediaPlayer.setDataSource(audioFile);
                } else {
                    binding.progressBar.setVisibility(View.GONE);
//                binding.llProgress.setVisibility(View.GONE);
                    binding.ivPlay.setVisibility(View.VISIBLE);
                    binding.ivPause.setVisibility(View.GONE);
                    BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                }
            }
        }
    }

    private void getDownloadMedia(DownloadMedia downloadMedia) {
        class getDownloadMedia extends AsyncTask<Void, Void, Void> {
            FileDescriptor fileDescriptor = null;

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    byte[] decrypt = null;
                    decrypt = downloadMedia.decrypt(name);
                    if (decrypt != null) {
                        fileDescriptor = FileUtils.getTempFileDescriptor(getActivity(), decrypt);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (fileDescriptor != null) {
                    setMediaPlayer("1", fileDescriptor);
                } else {
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        setMediaPlayer("0", fileDescriptor);
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.ivPlay.setVisibility(View.VISIBLE);
                        binding.ivPause.setVisibility(View.GONE);
                        BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                    }
                }
                super.onPostExecute(aVoid);
            }
        }

        getDownloadMedia st = new getDownloadMedia();
        st.execute();
    }

    private void callComplete() {
        handler12.removeCallbacks(UpdateSongTime12);
        isPrepare = false;
        isMediaStart = false;
        if (audioPlay && (audioFile.equalsIgnoreCase("") || audioFile.isEmpty())) {
            isDisclaimer = 0;
            removeArray();
        } else {
            if (IsRepeat.equalsIgnoreCase("1")) {
                if (position < (listSize - 1)) {
                    position = position + 1;
                } else {
                    position = 0;
                }
                getPrepareShowData();
            } else if (IsRepeat.equalsIgnoreCase("0")) {
                getPrepareShowData();
            } else if (IsShuffle.equalsIgnoreCase("1")) {
                // shuffle is on - play a random song
                if (queuePlay) {
                    try {
                        addToQueueModelList.remove(position);
                    } catch (Exception e) {
                    }
                    listSize = addToQueueModelList.size();
                    if (listSize == 0) {
                        isCompleteStop = true;
                        stopMedia();
                    } else if (listSize == 1) {
                        position = 0;
                        getPrepareShowData();
                    } else {
                        int oldPosition = position;
                        Random random = new Random();
                        position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                        if (oldPosition == position) {
                            Random random1 = new Random();
                            position = random1.nextInt((listSize - 1) - 0 + 1) + 0;
                        }
                        getPrepareShowData();
                    }
                } else {
                    if (listSize == 1) {

                    } else {
                        int oldPosition = position;
                        Random random = new Random();
                        position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                        if (oldPosition == position) {
                            Random random1 = new Random();
                            position = random1.nextInt((listSize - 1) - 0 + 1) + 0;
                        }
                        getPrepareShowData();
                    }
                }
            } else {
                if (queuePlay) {
                    try {
                        addToQueueModelList.remove(position);
                    } catch (Exception e) {
                    }
                    listSize = addToQueueModelList.size();
                    if (position < listSize - 1) {
                        getPrepareShowData();
                    } else {
                        if (listSize == 0) {
                            savePrefQueue(0, false, true, addToQueueModelList, ctx);
                            isCompleteStop = true;
                            stopMedia();
                        } else {
                            position = 0;
                            getPrepareShowData();
                        }
                    }
                } else {
                    if (position < (listSize - 1)) {
                        int oldPosition = position;
                        position = position + 1;
                        if (oldPosition == position) {
                            position++;
                        }
                        getPrepareShowData();
                    } else {
                        if (listSize == 1) {
                            binding.ivPlay.setVisibility(View.VISIBLE);
                            binding.ivPause.setVisibility(View.GONE);
                            binding.pbProgressBar.setVisibility(View.GONE);
                            isCompleteStop = true;
                            stopMedia();
                        } else {
                            binding.ivPlay.setVisibility(View.VISIBLE);
                            binding.ivPause.setVisibility(View.GONE);
                            binding.pbProgressBar.setVisibility(View.GONE);
                            isCompleteStop = true;
                            stopMedia();
//                        position = 0;
//                        getPrepareShowData();
                        }
                    }
                }
            }
        }
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(CONSTANTS.PREF_KEY_position, position);
        editor.commit();
    }

    private void removeArray() {
        shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        gson = new Gson();
        json = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson));
        mainPlayModelList = new ArrayList<>();
        if (AudioFlag.equalsIgnoreCase("MainAudioList")) {
            Type type = new TypeToken<ArrayList<MainAudioModel.ResponseData.Detail>>() {
            }.getType();
            ArrayList<MainAudioModel.ResponseData.Detail> arrayList = gson.fromJson(json, type);

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
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
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
            ArrayList<ViewAllAudioListModel.ResponseData.Detail> arrayList = gson.fromJson(json, type);
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
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
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
            ArrayList<SearchBothModel.ResponseData> arrayList = gson.fromJson(json, type);
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
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
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
            ArrayList<SuggestedModel.ResponseData> arrayList = gson.fromJson(json, type);
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
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
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
            ArrayList<AppointmentDetailModel.Audio> arrayList = gson.fromJson(json, type);
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
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
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
            ArrayList<DownloadAudioDetails> arrayList = gson.fromJson(json, type);
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
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
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
            ArrayList<DownloadAudioDetails> arrayList = gson.fromJson(json, type);
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
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
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
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);

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
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
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
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);
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
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
    /*    handler.removeCallbacks(UpdateSongTime);
        if (isMediaStart) {
            int totalDuration = getEndTime();
            int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);

            // forward or backward to certain seconds
            SeekTo(currentPosition);
        }
        // update timer progress again
        updateProgressBar();*/
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        handler12.removeCallbacks(UpdateSongTime12);

    }

    public void updateProgressBar() {
        handler12.postDelayed(UpdateSongTime12, 100);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler12.removeCallbacks(UpdateSongTime12);

        int totalDuration = getEndTime();
        int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        SeekTo(currentPosition);

        oTime = binding.simpleSeekbar.getProgress();
        // update timer progress again
        updateProgressBar();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ComeScreenAccount == 1) {
            binding.llLayout.setVisibility(View.GONE);
        } else if (ComeScreenAccount == 0) {
            binding.llLayout.setVisibility(View.VISIBLE);
        }

        handler12.postDelayed(UpdateSongTime12, 500);
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
        Gson gson = new Gson();
        String json1 = shared.getString(CONSTANTS.PREF_KEY_queueList, String.valueOf(gson));
        if (!json1.equalsIgnoreCase(String.valueOf(gson))) {
            Type type1 = new TypeToken<ArrayList<AddToQueueModel>>() {
            }.getType();
            addToQueueModelList = gson.fromJson(json1, type1);
        }
        String json = shared.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gson));
        Type type = new TypeToken<ArrayList<MainPlayModel>>() {
        }.getType();
        mainPlayModelList = new ArrayList<>();
        if (!json.equalsIgnoreCase(String.valueOf(gson))) {
            mainPlayModelList = gson.fromJson(json, type);
        }
        queuePlay = shared.getBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
        audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        try {
            if (queuePlay) {
                position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
                listSize = addToQueueModelList.size();
                id = addToQueueModelList.get(position).getID();
                name = addToQueueModelList.get(position).getName();
                audioFile = addToQueueModelList.get(position).getAudioFile();
            } else if (audioPlay) {
                position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
                listSize = mainPlayModelList.size();
                if (listSize == 1) {
                    position = 0;
                }
                if (listSize != 0) {
                    id = mainPlayModelList.get(position).getID();
                    name = mainPlayModelList.get(position).getName();
                    audioFile = mainPlayModelList.get(position).getAudioFile();
       /*         if (audioFile.equalsIgnoreCase("")) {
                    Glide.with(ctx).load(R.drawable.disclaimer).thumbnail(0.05f)
                            .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
                } else {
                    Glide.with(ctx).load(mainPlayModelList.get(position).getImageFile()).thumbnail(0.05f)
                            .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
                }
                binding.tvTitle.setText(mainPlayModelList.get(position).getName());
                binding.tvSubTitle.setText(mainPlayModelList.get(position).getAudioDirection());
                if (audioFile.equalsIgnoreCase("") || audioFile.isEmpty()) {
                    isDisclaimer = 1;
                    binding.simpleSeekbar.setClickable(false);
                    binding.flProgress.setClickable(false);
                    binding.flProgress.setEnabled(false);
                } else {
                    isDisclaimer = 0;
                    binding.simpleSeekbar.setClickable(true);
                    binding.flProgress.setClickable(true);
                    binding.flProgress.setEnabled(true);
                }*/
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listSize == 1) {
            position = 0;
        }
        SharedPreferences Status = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
        IsRepeat = Status.getString(CONSTANTS.PREF_KEY_IsRepeat, "");
        IsShuffle = Status.getString(CONSTANTS.PREF_KEY_IsShuffle, "");
      /*  if (isPrepare && !isMediaStart) {
            callMedia();
        } else if (isMediaStart && !isPause) {
            binding.ivPlay.setVisibility(View.GONE);
            binding.ivPause.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.ivPlay.setVisibility(View.GONE);
            binding.ivPause.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void onPause() {
        handler12.removeCallbacks(UpdateSongTime12);
        Log.e("Stop runnble", "stop");
        super.onPause();
    }

    private void simple_Notification(PlaybackStatus playbackStatus, ArrayList<MainPlayModel> mainPlayModelList) {
/*//declare an id for your notification
//id is used in many things especially when setting action buttons and their intents
        int notificationId = 0;
//init notification and declare specifications
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity())
                .setSmallIcon(R.drawable.square_app_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.square_app_icon))
                .setContentTitle("Android Development Course")
                .setContentText("Become an Android Developer.")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
//set a tone when notification appears
        Uri path = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(path);

//call notification manager so it can build and deliver the notification to the OS
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

//Android 8 introduced a new requirement of setting the channelId property by using a NotificationChannel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "YOUR_CHANNEL_ID";
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        notificationManager.notify(notificationId, builder.build());*/

        int notificationAction = android.R.drawable.ic_media_pause;//needs to be initialized
        PendingIntent play_pauseAction = null;

        //Build a new notification according to the current state of the MediaPlayer
        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = android.R.drawable.ic_media_pause;
            //create the pause action
            play_pauseAction = playbackAction(1);
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = android.R.drawable.ic_media_play;
            //create the play action
            play_pauseAction = playbackAction(0);
        }

        // Create a new Notification
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getActivity())
                .setShowWhen(false)
                // Set the Notification style
//                .setStyle(new NotificationCompat().MediaStyle()
                // Attach our MediaSession token
//                .setMediaSession(mediaSession.getSessionToken())
                // Show our playback controls in the compact notification view.
//                .setShowActionsInCompactView(0, 1, 2))
                .setColor(getResources().getColor(R.color.colorPrimary))
                // Set the large and small icons
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.square_app_icon))
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                // Set Notification content information
                .setContentText(mainPlayModelList.get(position).getAudioDirection())
                .setContentTitle(mainPlayModelList.get(position).getName())
                .setContentInfo("Brain Wellness Spa")
                // Add playback actions
                .addAction(R.drawable.ic_backword_icon, "previous", playbackAction(3))
                .addAction(notificationAction, "pause", play_pauseAction)
                .addAction(R.drawable.ic_forward_icon, "next", playbackAction(2));

        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

//Android 8 introduced a new requirement of setting the channelId property by using a NotificationChannel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "YOUR_CHANNEL_ID";
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder.setChannelId(channelId);
        }

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

/*    private void skipToNext() {
        if (audioIndex == audioList.size() - 1) {
            //if last in playlist
            audioIndex = 0;
            activeAudio = audioList.get(audioIndex);
        } else {
            //get next in playlist
            activeAudio = audioList.get(++audioIndex);
        }

        //Update stored index
        new StorageUtil(getApplicationContext()).storeAudioIndex(audioIndex);

        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
    }*/

   /* private void skipToPrevious() {

        if (audioIndex == 0) {
            //if first in playlist
            //set index to the last of audioList
            audioIndex = audioList.size() - 1;
            activeAudio = audioList.get(audioIndex);
        } else {
            //get previous in playlist
            activeAudio = audioList.get(--audioIndex);
        }

        //Update stored index
        new StorageUtil(getApplicationContext()).storeAudioIndex(audioIndex);

        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
    }*/

    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(getActivity(), MusicService.class);
        switch (actionNumber) {
            case 0:
                // Play
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(getActivity(), actionNumber, playbackAction, 0);
            case 1:
                // Pause
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(getActivity(), actionNumber, playbackAction, 0);
            case 2:
                // Next track
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(getActivity(), actionNumber, playbackAction, 0);
            case 3:
                // Previous track
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(getActivity(), actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }

    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;

        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
        } else if (actionString.equalsIgnoreCase(ACTION_NEXT)) {
            transportControls.skipToNext();
        } else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) {
            transportControls.skipToPrevious();
        } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();
        }
    }

   /* TODO Need this code Can't delete
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            //Load data from SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            audioList = storage.loadAudio();
            audioIndex = storage.loadAudioIndex();

            if (audioIndex != -1 && audioIndex < audioList.size()) {
                //index is in a valid range
                activeAudio = audioList.get(audioIndex);
            } else {
                stopSelf();
            }
        } catch (NullPointerException e) {
            stopSelf();
        }

        //Request audio focus
        if (requestAudioFocus() == false) {
            //Could not gain focus
            stopSelf();
        }

        if (mediaSessionManager == null) {
            try {
                initMediaSession();
                initMediaPlayer();
            } catch (RemoteException e) {
                e.printStackTrace();
                stopSelf();
            }
            buildNotification(PlaybackStatus.PLAYING);
        }

        //Handle Intent action from MediaSession.TransportControls
        handleIncomingActions(intent);
        return super.onStartCommand(intent, flags, startId);
    }*/
/* @Override
    public void onAudioFocusChange(int i) {
        switch (i) {
            case AudioManager.AUDIOFOCUS_GAIN:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Resume your media player here
                resumeMedia();
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (isMediaStart) {
                    pauseMedia();
//                    binding.ivPlay.setVisibility(View.VISIBLE);
//                    binding.ivPause.setVisibility(View.GONE);
                }
//                MusicService.pauseMedia();// Pause your media player here
                break;
        }
    }*/
}