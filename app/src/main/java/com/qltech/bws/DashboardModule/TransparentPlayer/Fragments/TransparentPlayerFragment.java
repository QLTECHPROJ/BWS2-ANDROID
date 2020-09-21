package com.qltech.bws.DashboardModule.TransparentPlayer.Fragments;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Activities.DashboardActivity;
import com.qltech.bws.DashboardModule.Activities.PlayWellnessActivity;
import com.qltech.bws.DashboardModule.Models.AddToQueueModel;
import com.qltech.bws.DashboardModule.Models.AppointmentDetailModel;
import com.qltech.bws.DashboardModule.Models.MainAudioModel;
import com.qltech.bws.DashboardModule.Models.SubPlayListModel;
import com.qltech.bws.DashboardModule.Models.SucessModel;
import com.qltech.bws.DashboardModule.Models.ViewAllAudioListModel;
import com.qltech.bws.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.qltech.bws.DownloadModule.Models.DownloadlistModel;
import com.qltech.bws.R;
import com.qltech.bws.SplashModule.SplashScreenActivity;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.FragmentTransparentPlayerBinding;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.qltech.bws.DashboardModule.Activities.DashboardActivity.player;
import static com.qltech.bws.Utility.MusicService.SeekTo;
import static com.qltech.bws.Utility.MusicService.getEndTime;
import static com.qltech.bws.Utility.MusicService.getProgressPercentage;
import static com.qltech.bws.Utility.MusicService.getStartTime;
import static com.qltech.bws.Utility.MusicService.initMediaPlayer;
import static com.qltech.bws.Utility.MusicService.isMediaStart;
import static com.qltech.bws.Utility.MusicService.isPause;
import static com.qltech.bws.Utility.MusicService.isPlaying;
import static com.qltech.bws.Utility.MusicService.isPrepare;
import static com.qltech.bws.Utility.MusicService.mediaPlayer;
import static com.qltech.bws.Utility.MusicService.oTime;
import static com.qltech.bws.Utility.MusicService.pauseMedia;
import static com.qltech.bws.Utility.MusicService.play;
import static com.qltech.bws.Utility.MusicService.playMedia;
import static com.qltech.bws.Utility.MusicService.progressToTimer;
import static com.qltech.bws.Utility.MusicService.resumeMedia;
import static com.qltech.bws.Utility.MusicService.savePrefQueue;
import static com.qltech.bws.Utility.MusicService.stopMedia;

public class TransparentPlayerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener/*, AudioManager.OnAudioFocusChangeListener*/ {
    public FragmentTransparentPlayerBinding binding;
    String UserID, AudioFlag, IsRepeat, IsShuffle, audioFile, id;
    int position = 0, startTime, listSize;
    MainPlayModel mainPlayModel;
    Boolean queuePlay, audioPlay;
    ArrayList<MainPlayModel> mainPlayModelList;
    ArrayList<AddToQueueModel> addToQueueModelList;
    private Handler handler;
    //    private AudioManager mAudioManager;
    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            try {
                startTime = getStartTime();
                binding.simpleSeekbar.setMax(100);
                Time t = Time.valueOf("00:00:00");
                if (queuePlay) {
                    if (listSize != 0) {
                        t = Time.valueOf("00:" + addToQueueModelList.get(position).getAudioDuration());
                    } else {
                        stopMedia();
                    }
                } else if (audioPlay) {
                    t = Time.valueOf("00:" + mainPlayModelList.get(position).getAudioDuration());
                }
                long totalDuration = t.getTime();
                long currentDuration = getStartTime();

                int progress = (int) (getProgressPercentage(currentDuration, totalDuration));
                //Log.d("Progress", ""+progress);
                if (isPause) {
                    binding.simpleSeekbar.setProgress(oTime);
                } else {
                    binding.simpleSeekbar.setProgress(progress);
                }
                // Running this thread after 100 milliseconds
                handler.postDelayed(this, 60);
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
        mainPlayModelList = new ArrayList<>();
        addToQueueModelList = new ArrayList<>();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        handler = new Handler();
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson));
        String json1 = shared.getString(CONSTANTS.PREF_KEY_queueList, String.valueOf(gson));
        if (!json1.equalsIgnoreCase(String.valueOf(gson))) {
            Type type1 = new TypeToken<ArrayList<AddToQueueModel>>() {
            }.getType();
            addToQueueModelList = gson.fromJson(json1, type1);
        }
        queuePlay = shared.getBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
        audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
        position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");

        binding.simpleSeekbar.setOnSeekBarChangeListener(this);
        SharedPreferences Status = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
        IsRepeat = Status.getString(CONSTANTS.PREF_KEY_IsRepeat, "");
        IsShuffle = Status.getString(CONSTANTS.PREF_KEY_IsShuffle, "");
//        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
//        mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
//                AudioManager.AUDIOFOCUS_GAIN);
        if (queuePlay) {
            getPrepareShowData();
        } else if (audioPlay) {
            if (AudioFlag.equalsIgnoreCase("MainAudioList")) {
                Type type = new TypeToken<MainAudioModel.ResponseData.Detail>() {
                }.getType();
                MainAudioModel.ResponseData.Detail arrayList = gson.fromJson(json, type);
//            listSize = arrayList.size();
//            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.getID());
                mainPlayModel.setName(arrayList.getName());
                mainPlayModel.setAudioFile(arrayList.getAudioFile());
                mainPlayModel.setAudioDirection(arrayList.getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.getImageFile());
                mainPlayModel.setLike(arrayList.getLike());
                mainPlayModel.setDownload(arrayList.getDownload());
                mainPlayModel.setAudioDuration(arrayList.getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
//            }
                getPrepareShowData();

            } else if (AudioFlag.equalsIgnoreCase("ViewAllAudioList")) {
                Type type = new TypeToken<ViewAllAudioListModel.ResponseData.Detail>() {
                }.getType();
                ViewAllAudioListModel.ResponseData.Detail arrayList = gson.fromJson(json, type);
//            listSize = arrayList.size();
//                for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.getID());
                mainPlayModel.setName(arrayList.getName());
                mainPlayModel.setAudioFile(arrayList.getAudioFile());
                mainPlayModel.setAudioDirection(arrayList.getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.getImageFile());
                mainPlayModel.setLike(arrayList.getLike());
                mainPlayModel.setDownload(arrayList.getDownload());
                mainPlayModel.setAudioDuration(arrayList.getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
//                }
                getPrepareShowData();
            } else if (AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
                Type type = new TypeToken<AppointmentDetailModel.Audio>() {
                }.getType();
                AppointmentDetailModel.Audio arrayList = gson.fromJson(json, type);
//            listSize = arrayList.size();
//                for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.getID());
                mainPlayModel.setName(arrayList.getName());
                mainPlayModel.setAudioFile(arrayList.getAudioFile());
                mainPlayModel.setAudioDirection(arrayList.getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.getImageFile());
                mainPlayModel.setLike(arrayList.getLike());
                mainPlayModel.setDownload(arrayList.getDownload());
                mainPlayModel.setAudioDuration(arrayList.getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
//                }
                getPrepareShowData();
            } else if (AudioFlag.equalsIgnoreCase("Downloadlist")) {
                Type type = new TypeToken<ArrayList<DownloadlistModel.Audio>>() {
                }.getType();
                ArrayList<DownloadlistModel.Audio> arrayList = gson.fromJson(json, type);
                listSize = arrayList.size();
                for (int i = 0; i < listSize; i++) {
                    mainPlayModel = new MainPlayModel();
                    mainPlayModel.setID(arrayList.get(i).getAudioID());
                    mainPlayModel.setName(arrayList.get(i).getName());
                    mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                    mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                    mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                    mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                    mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                    mainPlayModel.setLike(arrayList.get(i).getLike());
                    mainPlayModel.setDownload(arrayList.get(i).getDownload());
                    mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                    mainPlayModelList.add(mainPlayModel);
                }
                getPrepareShowData();
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
                    mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                    mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                    mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                    mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                    mainPlayModel.setLike(arrayList.get(i).getLike());
                    mainPlayModel.setDownload(arrayList.get(i).getDownload());
                    mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                    mainPlayModelList.add(mainPlayModel);
                }
                getPrepareShowData();
            }
        }
        if (listSize == 1) {
            IsShuffle = "";
        }
        binding.ivPause.setOnClickListener(view1 -> {
            binding.ivPause.setVisibility(View.GONE);
            binding.ivPlay.setVisibility(View.VISIBLE);
            handler.removeCallbacks(UpdateSongTime);
            binding.simpleSeekbar.setProgress(binding.simpleSeekbar.getProgress());
            if (!isMediaStart) {
//                callAsyncTask();
                play(Uri.parse(audioFile));
                playMedia();
            } else {
                pauseMedia();
            }
            oTime = binding.simpleSeekbar.getProgress();
        });

        binding.ivPlay.setOnClickListener(view12 -> {
            if (!isMediaStart) {
                initMediaPlayer();
                stopMedia();
                mediaPlayer = new MediaPlayer();
                try {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.ivPlay.setVisibility(View.GONE);
                    binding.ivPause.setVisibility(View.GONE);
                    mediaPlayer.setDataSource(String.valueOf(audioFile));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mediaPlayer.setAudioAttributes(
                                new AudioAttributes
                                        .Builder()
                                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                        .build());
                    }
                    mediaPlayer.prepareAsync();
                    isPrepare = true;
                } catch (IOException e) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.ivPlay.setVisibility(View.GONE);
                    binding.ivPause.setVisibility(View.GONE);
                    e.printStackTrace();
                    Log.e("Failedddddddd", "prepare() failed");
                }
                playMedia();
            } else {
                resumeMedia();
                isPause = false;
            }
            binding.progressBar.setVisibility(View.GONE);
            binding.ivPlay.setVisibility(View.GONE);
            binding.ivPause.setVisibility(View.VISIBLE);
            player = 1;
            handler.postDelayed(UpdateSongTime, 60);
        });

        return view;
    }

    private void addToRecentPlay() {
        if (BWSApplication.isNetworkConnected(getActivity())) {
//            BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
            Call<SucessModel> listCall = APIClient.getClient().getRecentlyplayed(id, UserID);
            listCall.enqueue(new Callback<SucessModel>() {
                @Override
                public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                    if (response.isSuccessful()) {
//                        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                        SucessModel model = response.body();
                    }
                }

                @Override
                public void onFailure(Call<SucessModel> call, Throwable t) {
//                    BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }

    private void getPrepareShowData() {
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
                Glide.with(getActivity()).load(addToQueueModelList.get(position).getImageFile()).thumbnail(0.05f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
                binding.tvTitle.setText(addToQueueModelList.get(position).getName());
                binding.tvSubTitle.setText(addToQueueModelList.get(position).getAudioDirection());
                audioFile = addToQueueModelList.get(position).getAudioFile();
                if (player == 1) {
                    binding.progressBar.setVisibility(View.GONE);
                    if (isPause) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.ivPause.setVisibility(View.GONE);
                        binding.ivPlay.setVisibility(View.VISIBLE);
                        binding.simpleSeekbar.setProgress(oTime);
                    } else if (!isPrepare && !isMediaStart) {
                        initMediaPlayer();
                        stopMedia();
                        mediaPlayer = new MediaPlayer();
                        try {
                            binding.progressBar.setVisibility(View.GONE);
                            binding.ivPlay.setVisibility(View.GONE);
                            binding.ivPause.setVisibility(View.GONE);
                            mediaPlayer.setDataSource(String.valueOf(audioFile));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                mediaPlayer.setAudioAttributes(
                                        new AudioAttributes
                                                .Builder()
                                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                                .build());
                            }
                            mediaPlayer.prepareAsync();
                            isPrepare = true;
                        } catch (IOException e) {
                            binding.progressBar.setVisibility(View.GONE);
                            binding.ivPlay.setVisibility(View.GONE);
                            binding.ivPause.setVisibility(View.GONE);
                            e.printStackTrace();
                            Log.e("Failedddddddd", "prepare() failed");
                        }
                        binding.progressBar.setVisibility(View.GONE);
                        binding.ivPlay.setVisibility(View.GONE);
                        binding.ivPause.setVisibility(View.VISIBLE);
                        playMedia();
                       /* play(Uri.parse(audioFile));
                        new Handler().postDelayed(() -> {
                            binding.progressBar.setVisibility(View.VISIBLE);
                            binding.ivPlay.setVisibility(View.GONE);
                            binding.ivPause.setVisibility(View.GONE);
                        }, 2 * 1000);

                        play(Uri.parse(audioFile));
                        playMedia();*/
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.ivPause.setVisibility(View.VISIBLE);
                        binding.ivPlay.setVisibility(View.GONE);
                    }
                } else {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.ivPause.setVisibility(View.GONE);
                    binding.ivPlay.setVisibility(View.VISIBLE);
                }
            }
        } else if (audioPlay) {
            listSize = mainPlayModelList.size();
            if (listSize == 1) {
                position = 0;
            }
            id = mainPlayModelList.get(position).getID();
            Glide.with(getActivity()).load(mainPlayModelList.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
            binding.tvTitle.setText(mainPlayModelList.get(position).getName());
            binding.tvSubTitle.setText(mainPlayModelList.get(position).getAudioDirection());
            audioFile = mainPlayModelList.get(position).getAudioFile();
            if (player == 1) {
                binding.progressBar.setVisibility(View.GONE);
                if (isPause) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.ivPause.setVisibility(View.GONE);
                    binding.ivPlay.setVisibility(View.VISIBLE);
                    binding.simpleSeekbar.setProgress(oTime);
                } else if (!isPrepare && !isMediaStart) {
                    initMediaPlayer();
                    stopMedia();
                    mediaPlayer = new MediaPlayer();
                    try {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.ivPlay.setVisibility(View.GONE);
                        binding.ivPause.setVisibility(View.GONE);
                        mediaPlayer.setDataSource(String.valueOf(audioFile));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            mediaPlayer.setAudioAttributes(
                                    new AudioAttributes
                                            .Builder()
                                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                            .build());
                        }
                        mediaPlayer.prepareAsync();
                        isPrepare = true;
                    } catch (IOException e) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.ivPlay.setVisibility(View.GONE);
                        binding.ivPause.setVisibility(View.GONE);
                        e.printStackTrace();
                        Log.e("Failedddddddd", "prepare() failed");
                    }
                    binding.progressBar.setVisibility(View.GONE);
                    binding.ivPlay.setVisibility(View.GONE);
                    binding.ivPause.setVisibility(View.VISIBLE);
                    playMedia();
                    /*play(Uri.parse(audioFile));
                    new Handler().postDelayed(() -> {
                        binding.progressBar.setVisibility(View.VISIBLE);
                        binding.ivPlay.setVisibility(View.GONE);
                        binding.ivPause.setVisibility(View.GONE);
                    }, 2 * 1000);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.ivPlay.setVisibility(View.GONE);
                    binding.ivPause.setVisibility(View.VISIBLE);
                    playMedia();*/
                } else {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.ivPause.setVisibility(View.VISIBLE);
                    binding.ivPlay.setVisibility(View.GONE);
                }
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.ivPause.setVisibility(View.GONE);
                binding.ivPlay.setVisibility(View.VISIBLE);
            }
        }
        binding.simpleSeekbar.setClickable(true);
        if (isMediaStart) {
            mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                callComplete();
            });
        }
        startTime = getStartTime();

        handler.postDelayed(UpdateSongTime, 60);
        addToRecentPlay();
        binding.llPlayearMain.setOnClickListener(view -> {
            if (player == 0) {
                player = 1;
            }
            if (binding.ivPause.getVisibility() == View.VISIBLE) {
                isPause = false;
            } else if (binding.ivPlay.getVisibility() == View.VISIBLE) {
                isPause = true;
            }
            Intent i = new Intent(getActivity(), PlayWellnessActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            getActivity().startActivity(i);
            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
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

//            simpleNotification();
        });
    }

    private void callComplete() {
        isPrepare = false;
        isMediaStart = false;
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
                addToQueueModelList.remove(position);
                listSize = addToQueueModelList.size();
                if (listSize == 0) {
                    stopMedia();
                } else if (listSize == 1) {
                    position = 0;
                    getPrepareShowData();
                } else {
                    Random random = new Random();
                    position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                    getPrepareShowData();
                }
            } else {
                if (listSize == 1) {

                } else {
                    Random random = new Random();
                    position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                    getPrepareShowData();
                }
            }
        } else {
            if (queuePlay) {
                addToQueueModelList.remove(position);
                listSize = addToQueueModelList.size();
                if (position < listSize - 1) {
                    getPrepareShowData();
                } else {
                    if (listSize == 0) {
                        savePrefQueue(0, false, true, addToQueueModelList, getActivity());
                        stopMedia();
                    } else {
                        position = 0;
                        getPrepareShowData();
                    }
                }
            } else {
                if (position < (listSize - 1)) {
                    position = position + 1;
                    getPrepareShowData();
                } else {
                    binding.ivPlay.setVisibility(View.VISIBLE);
                    binding.ivPause.setVisibility(View.GONE);
                    stopMedia();
                }
            }
        }
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(CONSTANTS.PREF_KEY_position, position);
        editor.commit();
    }

    private void callAsyncTask() {
/*        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                play(Uri.parse(audioFile));
                playMedia();
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.ivPlay.setVisibility(View.GONE);
                binding.ivPause.setVisibility(View.GONE);

                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                binding.ivPlay.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
                binding.ivPause.setVisibility(View.VISIBLE);
            }
        }

        SaveTask st = new SaveTask();
        st.execute();*/
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(UpdateSongTime);

    }

    public void updateProgressBar() {
        handler.postDelayed(UpdateSongTime, 100);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(UpdateSongTime);

        int totalDuration = getEndTime();
        int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        SeekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    @Override
    public void onResume() {
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
        Gson gson = new Gson();
        String json1 = shared.getString(CONSTANTS.PREF_KEY_queueList, String.valueOf(gson));
        if (!json1.equalsIgnoreCase(String.valueOf(gson))) {
            Type type1 = new TypeToken<ArrayList<AddToQueueModel>>() {
            }.getType();
            addToQueueModelList = gson.fromJson(json1, type1);
        }
        queuePlay = shared.getBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
        audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        if (queuePlay) {
            position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
            listSize = addToQueueModelList.size();
        } else if (audioPlay) {
            position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
            listSize = mainPlayModelList.size();
        }
        if (listSize == 1) {
            position = 0;
        }
        SharedPreferences Status = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
        IsRepeat = Status.getString(CONSTANTS.PREF_KEY_IsRepeat, "");
        IsShuffle = Status.getString(CONSTANTS.PREF_KEY_IsShuffle, "");
        if ((isPrepare || isMediaStart || isPlaying()) && !isPause) {
            binding.ivPlay.setVisibility(View.GONE);
            binding.ivPause.setVisibility(View.VISIBLE);
        } else {
            binding.ivPlay.setVisibility(View.VISIBLE);
            binding.ivPause.setVisibility(View.GONE);
        }
        super.onResume();
    }

/*    @Override
    public void onAudioFocusChange(int i) {
        switch (i) {
            case AudioManager.AUDIOFOCUS_GAIN:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Resume your media player here
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (isMediaStart) {
                    pauseMedia();
                    binding.ivPlay.setVisibility(View.VISIBLE);
                    binding.ivPause.setVisibility(View.GONE);
                }
//                MusicService.pauseMedia();// Pause your media player here
                break;
        }
    }*/
}