package com.qltech.bws.DashboardModule.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.downloader.OnProgressListener;
import com.downloader.Progress;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Models.AddToQueueModel;
import com.qltech.bws.DashboardModule.Models.AudioLikeModel;
import com.qltech.bws.DashboardModule.Models.SucessModel;
import com.qltech.bws.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.qltech.bws.EncryptDecryptUtils.DownloadMedia;
import com.qltech.bws.EncryptDecryptUtils.FileUtils;
import com.qltech.bws.R;
import com.qltech.bws.RoomDataBase.DatabaseClient;
import com.qltech.bws.RoomDataBase.DownloadAudioDetails;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.ActivityPlayWellnessBinding;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.qltech.bws.DashboardModule.Activities.DashboardActivity.player;
import static com.qltech.bws.DashboardModule.Audio.AudioFragment.IsLock;
import static com.qltech.bws.EncryptDecryptUtils.DownloadMedia.downloadProgress;
import static com.qltech.bws.EncryptDecryptUtils.DownloadMedia.filename;
import static com.qltech.bws.Utility.MusicService.SeekTo;
import static com.qltech.bws.Utility.MusicService.ToBackward;
import static com.qltech.bws.Utility.MusicService.ToForward;
import static com.qltech.bws.Utility.MusicService.getEndTime;
import static com.qltech.bws.Utility.MusicService.getProgressPercentage;
import static com.qltech.bws.Utility.MusicService.getStartTime;
import static com.qltech.bws.Utility.MusicService.isMediaStart;
import static com.qltech.bws.Utility.MusicService.isPause;
import static com.qltech.bws.Utility.MusicService.isPlaying;
import static com.qltech.bws.Utility.MusicService.isPrepare;
import static com.qltech.bws.Utility.MusicService.isPreparing;
import static com.qltech.bws.Utility.MusicService.mediaPlayer;
import static com.qltech.bws.Utility.MusicService.oTime;
import static com.qltech.bws.Utility.MusicService.pauseMedia;
import static com.qltech.bws.Utility.MusicService.progressToTimer;
import static com.qltech.bws.Utility.MusicService.resumeMedia;
import static com.qltech.bws.Utility.MusicService.savePrefQueue;
import static com.qltech.bws.Utility.MusicService.stopMedia;

public class PlayWellnessActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, AudioManager.OnAudioFocusChangeListener/*, OnProgressListener*/ {
    ActivityPlayWellnessBinding binding;
    String IsRepeat = "", IsShuffle = "", UserID, PlaylistId = "", AudioFlag, id, name, url;
    int startTime = 0, endTime = 0, position, listSize;
    Context ctx;
    Activity activity;
    Boolean queuePlay, audioPlay;
    ArrayList<MainPlayModel> mainPlayModelList;
    ArrayList<AddToQueueModel> addToQueueModelList;
    List<DownloadAudioDetails> downloadAudioDetailsList;
    private long mLastClickTime = 0, totalDuration, currentDuration;
    private Handler handler;
    private Handler handler1;
    //    private AudioManager mAudioManager;
    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            Time t = Time.valueOf("00:00:00");
            String endtimetext = "";
            if (queuePlay) {
                if (listSize != 0) {
                    if (!BWSApplication.isNetworkConnected(ctx)) {
                        if (downloadAudioDetailsList.size() != 0) {
                            endtimetext = downloadAudioDetailsList.get(0).getAudioDuration();
                            t = Time.valueOf("00:" + downloadAudioDetailsList.get(0).getAudioDuration());
                        } else {
                            endtimetext = addToQueueModelList.get(position).getAudioDuration();
                            t = Time.valueOf("00:" + addToQueueModelList.get(position).getAudioDuration());
                        }
                    } else {
                        endtimetext = addToQueueModelList.get(position).getAudioDuration();
                        t = Time.valueOf("00:" + addToQueueModelList.get(position).getAudioDuration());
                    }
                } else {
                    stopMedia();
                }
            } else if (audioPlay) {
                if (!BWSApplication.isNetworkConnected(ctx)) {
                    if (downloadAudioDetailsList.size() != 0) {
                        endtimetext = downloadAudioDetailsList.get(0).getAudioDuration();
                        t = Time.valueOf("00:" + downloadAudioDetailsList.get(0).getAudioDuration());
                    } else {
                        endtimetext = mainPlayModelList.get(position).getAudioDuration();
                        t = Time.valueOf("00:" + mainPlayModelList.get(position).getAudioDuration());
                    }
                } else {
                    endtimetext = mainPlayModelList.get(position).getAudioDuration();
                    t = Time.valueOf("00:" + mainPlayModelList.get(position).getAudioDuration());
                }
            }
            if (!BWSApplication.isNetworkConnected(ctx)) {
                if (mediaPlayer != null) {
                    totalDuration = mediaPlayer.getDuration();
                } else
                    totalDuration = t.getTime();
            } else {
                totalDuration = t.getTime();
            }
            currentDuration = getStartTime();

            int progress = getProgressPercentage(currentDuration, totalDuration);
            //Log.d("Progress", ""+progress);
            startTime = getStartTime();
            if (currentDuration == totalDuration) {
                binding.tvStartTime.setText(endtimetext);
            } else if (isPause) {
                binding.simpleSeekbar.setProgress(oTime);
                int timeeee = progressToTimer(oTime, (int) (totalDuration));
                binding.tvStartTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(timeeee),
                        TimeUnit.MILLISECONDS.toSeconds(timeeee) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeeee))));
            } else {
                binding.simpleSeekbar.setProgress(progress);
                binding.tvStartTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(startTime),
                        TimeUnit.MILLISECONDS.toSeconds(startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime))));
            }
            binding.simpleSeekbar.setMax(100);

            // Running this thread after 100 milliseconds
            handler.postDelayed(this, 60);
        }
    };
    private Runnable UpdateSongTime1 = new Runnable() {
        @Override
        public void run() {
            if(!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(name)){
                if(downloadProgress <100) {
                    binding.pbProgress.setProgress(downloadProgress);
                    binding.pbProgress.setVisibility(View.VISIBLE);
                }else{
                    binding.pbProgress.setVisibility(View.GONE);
                    handler1.removeCallbacks(UpdateSongTime1);
                }
            }else{
                binding.pbProgress.setVisibility(View.GONE);
                handler1.removeCallbacks(UpdateSongTime1);
            }
            handler1.postDelayed(this, 10);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        overridePendingTransition(R.anim.enter, R.anim.exit);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_play_wellness);
        handler = new Handler();
        handler1 = new Handler();
        ctx = PlayWellnessActivity.this;
        activity = PlayWellnessActivity.this;
        addToQueueModelList = new ArrayList<>();
        downloadAudioDetailsList = new ArrayList<>();
        mainPlayModelList = new ArrayList<>();
        Glide.with(ctx).load(R.drawable.loading).asGif().into(binding.ImgV);
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences Status = getSharedPreferences(CONSTANTS.PREF_KEY_Status, Context.MODE_PRIVATE);
        IsRepeat = Status.getString(CONSTANTS.PREF_KEY_IsRepeat, "");
        IsShuffle = Status.getString(CONSTANTS.PREF_KEY_IsShuffle, "");

        binding.simpleSeekbar.setOnSeekBarChangeListener(this);
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = shared.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gson));
        position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
        Type type = new TypeToken<ArrayList<MainPlayModel>>() {
        }.getType();
        mainPlayModelList = gson.fromJson(json, type);
        String json1 = shared.getString(CONSTANTS.PREF_KEY_queueList, String.valueOf(gson));
        if (!json1.equalsIgnoreCase(String.valueOf(gson))) {
            Type type1 = new TypeToken<ArrayList<AddToQueueModel>>() {
            }.getType();
            addToQueueModelList = gson.fromJson(json1, type1);
        }
        queuePlay = shared.getBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
        audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);

        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                1, 1, 1f, 30);
        binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        /*mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);*/
        if (BWSApplication.isNetworkConnected(ctx)) {
            if (IsLock.equalsIgnoreCase("0")) {
                binding.llMore.setClickable(true);
                binding.llMore.setEnabled(true);
                binding.ivMore.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                binding.llMore.setClickable(false);
                binding.llMore.setEnabled(false);
                binding.ivMore.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        } else {
            binding.llMore.setClickable(false);
            binding.llMore.setEnabled(false);
            binding.ivMore.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        if (BWSApplication.isNetworkConnected(ctx)) {
            if (IsLock.equalsIgnoreCase("0")) {
                binding.llViewQueue.setClickable(true);
                binding.llViewQueue.setEnabled(true);
                binding.ivViewQueue.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                binding.llViewQueue.setClickable(false);
                binding.llViewQueue.setEnabled(false);
                binding.ivViewQueue.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        } else {
            binding.llViewQueue.setClickable(false);
            binding.llViewQueue.setEnabled(false);
            binding.ivViewQueue.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        getPrepareShowData(position);
        if(!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(name)){
            handler1.postDelayed(UpdateSongTime1, 10);
         }else{
            binding.pbProgress.setVisibility(View.GONE);
            handler1.removeCallbacks(UpdateSongTime1);
        }
        callRepeatShuffle();
        if (isMediaStart) {
            mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                callComplete();
            });
        }
        binding.llBack.setOnClickListener(view -> {
            callBack();
        });

        binding.llLike.setOnClickListener(view -> {
            callLike();
        });

        binding.llRepeat.setOnClickListener(view -> callRepeat());

        binding.llShuffle.setOnClickListener(view -> callShuffle());

        binding.llDownload.setOnClickListener(view -> callDownload());

        binding.llMore.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent i = new Intent(ctx, AddQueueActivity.class);
            i.putExtra("play", "play");
            i.putExtra("ID", id);
            i.putExtra("position", position);
            i.putExtra("PlaylistAudioId", "");
            startActivity(i);
//            finish();
        });

        binding.llViewQueue.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
//            pauseMedia();
            if (binding.llPause.getVisibility() == View.VISIBLE) {
                isPause = false;
            }
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

        binding.llPlay.setOnClickListener(v -> {
            if (!isMediaStart) {
                callMedia();
            } else {
                binding.llPlay.setVisibility(View.GONE);
                binding.llPause.setVisibility(View.VISIBLE);
                binding.llProgressBar.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
                resumeMedia();
                isPause = false;
            }
            handler.postDelayed(UpdateSongTime, 60);
        });

        binding.llPause.setOnClickListener(view -> {
            handler.removeCallbacks(UpdateSongTime);
            binding.simpleSeekbar.setProgress(binding.simpleSeekbar.getProgress());
            pauseMedia();
            binding.llProgressBar.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
            binding.llPlay.setVisibility(View.VISIBLE);
            binding.llPause.setVisibility(View.GONE);
            oTime = binding.simpleSeekbar.getProgress();
        });

        binding.llForwardSec.setOnClickListener(v -> {
            ToForward(ctx);
            setProgressBar();
            if (!binding.llPlay.isEnabled()) {
                binding.llPlay.setEnabled(true);
            }
        });

        binding.llBackWordSec.setOnClickListener(v -> {
            ToBackward(ctx);
            setProgressBar();
            if (!binding.llPlay.isEnabled()) {
                binding.llPlay.setEnabled(true);
            }
        });

        binding.llnext.setOnClickListener(view -> {
            stopMedia();
            isMediaStart = false;
            isPrepare = false;
            isPause = false;
            if (IsRepeat.equalsIgnoreCase("1")) {
                // repeat is on play same song again
                if (position < listSize - 1) {
                    position = position + 1;
                } else {
                    position = 0;
                }
                getPrepareShowData(position);
            } else if (IsRepeat.equalsIgnoreCase("0")) {
                getPrepareShowData(position);
            } else if (IsShuffle.equalsIgnoreCase("1")) {
                // shuffle is on - play a random song
                if (queuePlay) {
                    addToQueueModelList.remove(position);
                    listSize = addToQueueModelList.size();
                    if (listSize == 0) {
                        stopMedia();
                    } else if (listSize == 1) {
                        stopMedia();
                    } else {
                        Random random = new Random();
                        position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                        getPrepareShowData(position);
                    }
                } else {
                    Random random = new Random();
                    position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                    getPrepareShowData(position);
                }
            } else {
                if (queuePlay) {
                    addToQueueModelList.remove(position);
                    listSize = addToQueueModelList.size();
                    if (position < listSize - 1) {
                        getPrepareShowData(position);
                    } else {
                        if (listSize == 0) {
                            savePrefQueue(0, false, true, addToQueueModelList, ctx);
                            stopMedia();
                        } else {
                            position = 0;
                            getPrepareShowData(position);
                        }
                    }
                } else {
                    if (position < listSize - 1) {
                        position = position + 1;
                        getPrepareShowData(position);
                    } else if (listSize != 1) {
                        position = 0;
                        getPrepareShowData(position);
                    }
                }
            }
        });

        binding.llprev.setOnClickListener(view -> {
            stopMedia();
            isMediaStart = false;
            isPrepare = false;
            isPause = false;
            if (IsRepeat.equalsIgnoreCase("1")) {
                // repeat is on play same song again
                if (position > 0) {
                    position = position - 1;
                    getPrepareShowData(position);
                } else if (listSize != 1) {
                    position = listSize - 1;
                    getPrepareShowData(position);
                }
            } else if (IsRepeat.equalsIgnoreCase("0")) {
                getPrepareShowData(position);
            } else if (IsShuffle.equalsIgnoreCase("1")) {
                // shuffle is on - play a random song
                if (queuePlay) {
                    addToQueueModelList.remove(position);
                    listSize = addToQueueModelList.size();
                    if (listSize == 0) {
                        stopMedia();
                    } else if (listSize == 1) {
                        stopMedia();
                    } else {
                        Random random = new Random();
                        position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                        getPrepareShowData(position);
                    }
                } else {
                    Random random = new Random();
                    position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                    getPrepareShowData(position);
                }
            } else {
                if (queuePlay) {
                    addToQueueModelList.remove(position);
                    listSize = addToQueueModelList.size();
                    if (position > 0) {
                        getPrepareShowData(position - 1);
                    } else {
                        if (listSize == 0) {
                            savePrefQueue(0, false, true, addToQueueModelList, ctx);
                            stopMedia();
                        } else {
                            position = 0;
                            getPrepareShowData(position);
                        }
                    }
                } else {
                    if (position > 0) {
                        position = position - 1;

                        getPrepareShowData(position);
                    } else if (listSize != 1) {
                        position = listSize - 1;
                        getPrepareShowData(position);
                    }
                }
            }
        });
    }

    private void callRepeatShuffle() {
        if (IsShuffle.equalsIgnoreCase("")) {
            if (listSize == 1) {
                binding.llShuffle.setClickable(false);
                binding.llShuffle.setEnabled(false);
                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                binding.llShuffle.setClickable(true);
                binding.llShuffle.setEnabled(true);
                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
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
    }

    private void setProgressBar() {
        Time t = Time.valueOf("00:00:00");
        String endtimetext = "";
        if (queuePlay) {
            if (listSize != 0) {
                endtimetext = addToQueueModelList.get(position).getAudioDuration();
                t = Time.valueOf("00:" + addToQueueModelList.get(position).getAudioDuration());
            } else {
                stopMedia();
            }
        } else if (audioPlay) {
            endtimetext = mainPlayModelList.get(position).getAudioDuration();
            t = Time.valueOf("00:" + mainPlayModelList.get(position).getAudioDuration());
        }
        totalDuration = t.getTime();
        currentDuration = getStartTime();

        int progress = getProgressPercentage(currentDuration, totalDuration);
        //Log.d("Progress", ""+progress);
        startTime = getStartTime();
        if (currentDuration == totalDuration) {
            binding.tvStartTime.setText(endtimetext);
        } else if (isPause) {
            binding.simpleSeekbar.setProgress(progress);
            int timeeee = progressToTimer(progress, (int) (totalDuration));
            binding.tvStartTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(timeeee),
                    TimeUnit.MILLISECONDS.toSeconds(timeeee) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeeee))));
            oTime = binding.simpleSeekbar.getProgress();
        } else {
            binding.simpleSeekbar.setProgress(progress);
            binding.tvStartTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(startTime),
                    TimeUnit.MILLISECONDS.toSeconds(startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime))));
        }
    }

    private void callDownload() {
        disableDownload();
        byte[] EncodeBytes = new byte[1024];
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
            if(fileNameList.size()!=0) {
                url1.addAll(audioFile1);
                name1.addAll(fileNameList);
                downloadPlaylistId.addAll(playlistId1);
            }
        }
        url1.add(url);
        name1.add(name);
        downloadPlaylistId.add("");
        DownloadMedia downloadMedia = new DownloadMedia(getApplicationContext());
        downloadMedia.encrypt1(url1, name1);
        if(!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(name)){
            handler1.postDelayed(UpdateSongTime1, 10);
        }else{
            binding.pbProgress.setVisibility(View.GONE);
            handler1.removeCallbacks(UpdateSongTime1);
        }
        SaveMedia(EncodeBytes, FileUtils.getFilePath(getApplicationContext(), name));
   /*     if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
            Call<DownloadPlaylistModel> listCall = APIClient.getClient().getDownloadlistPlaylist(UserID, id, PlaylistId);
            listCall.enqueue(new Callback<DownloadPlaylistModel>() {
                @Override
                public void onResponse(Call<DownloadPlaylistModel> call, Response<DownloadPlaylistModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                        DownloadPlaylistModel model = response.body();
                        if (model.getResponseData().getFlag().equalsIgnoreCase("0")
                                || model.getResponseData().getFlag().equalsIgnoreCase("")) {
                            binding.llDownloads.setClickable(true);
                            binding.llDownloads.setEnabled(true);
                            binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
                        } else if (model.getResponseData().getFlag().equalsIgnoreCase("1")) {
                            binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
                            binding.ivDownloads.setColorFilter(Color.argb(99, 99, 99, 99));
                            binding.ivDownloads.setAlpha(255);
                            binding.llDownloads.setClickable(false);
                            binding.llDownloads.setEnabled(false);
                        }
                        BWSApplication.showToast(model.getResponseMessage(), ctx);
                    }
                }

                @Override
                public void onFailure(Call<DownloadPlaylistModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                }
            });

        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }*/
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
                    downloadAudioDetails.setDownload("1");
                    downloadAudioDetails.setAudioDuration(addToQueueModelList.get(position).getAudioDuration());
                    downloadAudioDetails.setIsSingle("1");
                    downloadAudioDetails.setPlaylistId("");
                } else if (audioPlay) {
                    downloadAudioDetails.setID(mainPlayModelList.get(position).getID());
                    downloadAudioDetails.setName(mainPlayModelList.get(position).getName());
                    downloadAudioDetails.setAudioFile(mainPlayModelList.get(position).getAudioFile());
                    downloadAudioDetails.setAudioDirection(mainPlayModelList.get(position).getAudioDirection());
                    downloadAudioDetails.setAudiomastercat(mainPlayModelList.get(position).getAudiomastercat());
                    downloadAudioDetails.setAudioSubCategory(mainPlayModelList.get(position).getAudioSubCategory());
                    downloadAudioDetails.setImageFile(mainPlayModelList.get(position).getImageFile());
                    downloadAudioDetails.setLike(mainPlayModelList.get(position).getLike());
                    downloadAudioDetails.setDownload("1");
                    downloadAudioDetails.setAudioDuration(mainPlayModelList.get(position).getAudioDuration());
                    downloadAudioDetails.setIsSingle("1");
                    downloadAudioDetails.setPlaylistId("");
                }
                downloadAudioDetails.setEncodedBytes(EncodeBytes);
                downloadAudioDetails.setDirPath(dirPath);

                DatabaseClient.getInstance(getApplicationContext())
                        .getaudioDatabase()
                        .taskDao()
                        .insertMedia(downloadAudioDetails);
                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {
                disableDownload();
                super.onPostExecute(aVoid);
            }
        }

        SaveMedia st = new SaveMedia();
        st.execute();
    }

    private void disableDownload() {
        binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
        binding.ivDownloads.setColorFilter(Color.argb(99, 99, 99, 99));
        binding.ivDownloads.setAlpha(255);
        binding.llDownload.setClickable(false);
        binding.llDownload.setEnabled(false);
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
                    binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
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
            binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
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
                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
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
                binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                BWSApplication.showToast("Repeat mode has been turned on", ctx);
                binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            }
            editor.commit();
            binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
        } else if (IsRepeat.equalsIgnoreCase("1")) {
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(CONSTANTS.PREF_KEY_IsShuffle, "");
            editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "");
            IsRepeat = "";
            if (listSize == 1) {
                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else
                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            editor.commit();
            binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
            BWSApplication.showToast("Repeat mode has been turned off", ctx);
        }
    }

    private void callLike() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
            Call<AudioLikeModel> listCall = APIClient.getClient().getAudioLike(id, UserID);
            listCall.enqueue(new Callback<AudioLikeModel>() {
                @Override
                public void onResponse(Call<AudioLikeModel> call, Response<AudioLikeModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                        AudioLikeModel model = response.body();
                        if (model.getResponseData().getFlag().equalsIgnoreCase("0")) {
                            binding.ivLike.setImageResource(R.drawable.ic_unlike_icon);
                        } else if (model.getResponseData().getFlag().equalsIgnoreCase("1")) {
                            binding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
                        }
                        if (queuePlay) {
                            addToQueueModelList.get(position).setLike(model.getResponseData().getFlag());
                        } else
                            mainPlayModelList.get(position).setLike(model.getResponseData().getFlag());
                        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
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
                        BWSApplication.showToast(model.getResponseMessage(), ctx);
                    }
                }

                @Override
                public void onFailure(Call<AudioLikeModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    private void addToRecentPlay() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
            Call<SucessModel> listCall = APIClient.getClient().getRecentlyplayed(id, UserID);
            listCall.enqueue(new Callback<SucessModel>() {
                @Override
                public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                        SucessModel model = response.body();
                    }
                }

                @Override
                public void onFailure(Call<SucessModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    public void GetMedia(String AudioFile, Context ctx, String download, String PlayListId) {

        downloadAudioDetailsList = new ArrayList<>();

        class GetMedia extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                downloadAudioDetailsList = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .getaudioByPlaylist(AudioFile, PlayListId);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (downloadAudioDetailsList.size() != 0) {
                    if (downloadAudioDetailsList.get(0).getDownload().equalsIgnoreCase("1")) {
                        binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                        binding.llDownload.setClickable(false);
                        binding.llDownload.setEnabled(false);
                        binding.ivDownloads.setColorFilter(activity.getResources().getColor(R.color.light_gray), PorterDuff.Mode.SRC_IN);
                    } else/* if (!mainPlayModelList.get(position).getDownload().equalsIgnoreCase("")) */ {
                        binding.llDownload.setClickable(true);
                        binding.llDownload.setEnabled(true);
                        binding.ivDownloads.setColorFilter(activity.getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                        binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                    }
                } else if (download.equalsIgnoreCase("1")) {
                    binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                    binding.llDownload.setClickable(false);
                    binding.llDownload.setEnabled(false);
                    binding.ivDownloads.setColorFilter(activity.getResources().getColor(R.color.light_gray), PorterDuff.Mode.SRC_IN);
                } else/* if (!mainPlayModelList.get(position).getDownload().equalsIgnoreCase("")) */ {
                    binding.llDownload.setClickable(true);
                    binding.llDownload.setEnabled(true);
                    binding.ivDownloads.setColorFilter(activity.getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                    binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                }
           /* if (!isMediaStart) {
             callMedia();
                binding.llPause.setVisibility(View.VISIBLE);
                binding.llPlay.setVisibility(View.GONE);
            } else {*/
                if (isPause) {
                    binding.llProgressBar.setVisibility(View.GONE);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.llPlay.setVisibility(View.VISIBLE);
                    binding.llPause.setVisibility(View.GONE);
                    binding.simpleSeekbar.setProgress(oTime);
                    int timeeee = progressToTimer(oTime, (int) (totalDuration));
                    binding.tvStartTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(timeeee),
                            TimeUnit.MILLISECONDS.toSeconds(timeeee) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeeee))));
//                    resumeMedia();
                } else if (isMediaStart && !isPause) {
                    binding.llProgressBar.setVisibility(View.GONE);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.llPause.setVisibility(View.VISIBLE);
                    binding.llPlay.setVisibility(View.GONE);
                } else {
                    callMedia();
                }
                super.onPostExecute(aVoid);

            }
        }

        GetMedia st = new GetMedia();
        st.execute();
    }

    private void getPrepareShowData(int position) {
        handler.postDelayed(UpdateSongTime, 60);
        if (queuePlay) {
            binding.llRepeat.setEnabled(false);
            binding.llRepeat.setClickable(false);
        }
        if (queuePlay) {
            listSize = addToQueueModelList.size();
        } else if (audioPlay) {
            listSize = mainPlayModelList.size();
        }
        if (listSize == 1) {
            binding.llnext.setEnabled(false);
            binding.llprev.setEnabled(false);
            binding.llShuffle.setEnabled(false);
            binding.llnext.setClickable(false);
            binding.llprev.setClickable(false);
            binding.llShuffle.setClickable(false);
            IsShuffle = "";
            binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.ivnext.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.ivprev.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            position = 0;
        } /*else if (position == listSize - 1 && IsRepeat.equalsIgnoreCase("1")) {
            binding.llnext.setEnabled(false);
            binding.llnext.setClickable(false);
            binding.ivnext.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (position == 0 && IsRepeat.equalsIgnoreCase("1")) {
            binding.llprev.setEnabled(false);
            binding.llprev.setClickable(false);
            binding.ivprev.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
        }*/ else {
            binding.llnext.setEnabled(true);
            binding.llprev.setEnabled(true);
            binding.llShuffle.setEnabled(true);
            binding.llnext.setClickable(true);
            binding.llprev.setClickable(true);
            binding.llShuffle.setClickable(true);
            if (IsShuffle.equalsIgnoreCase("")) {
                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            } else if (IsShuffle.equalsIgnoreCase("1")) {
                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
            }
            binding.ivnext.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.ivprev.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
        if (queuePlay) {
            listSize = addToQueueModelList.size();
            if (listSize == 1) {
                position = 0;
            }
            id = addToQueueModelList.get(position).getID();
            name = addToQueueModelList.get(position).getName();
            url = addToQueueModelList.get(position).getAudioFile();
            GetMedia(url, ctx, addToQueueModelList.get(position).getDownload(), addToQueueModelList.get(position).getPlaylistID());
            binding.tvName.setText(addToQueueModelList.get(position).getName());
            if (addToQueueModelList.get(position).getAudioDirection().equalsIgnoreCase("")) {
                binding.llDirection.setVisibility(View.GONE);
            } else {
                binding.llDirection.setVisibility(View.VISIBLE);
                binding.tvDireDesc.setText(addToQueueModelList.get(position).getAudioDirection());
            }
            binding.tvTitle.setText(addToQueueModelList.get(position).getAudiomastercat());
            binding.tvDesc.setText(addToQueueModelList.get(position).getAudioSubCategory());
            if (addToQueueModelList.get(position).getPlaylistID() == null) {
                addToQueueModelList.get(position).setPlaylistID("");
            }
            Glide.with(getApplicationContext()).load(addToQueueModelList.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
            if (addToQueueModelList.get(position).getLike().equalsIgnoreCase("1")) {
                binding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
            } else if (addToQueueModelList.get(position).getLike().equalsIgnoreCase("0")) {
                binding.ivLike.setImageResource(R.drawable.ic_unlike_icon);
            }
            binding.tvSongTime.setText(addToQueueModelList.get(position).getAudioDuration());

            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String json = gson.toJson(addToQueueModelList);
            editor.putString(CONSTANTS.PREF_KEY_queueList, json);
            editor.putInt(CONSTANTS.PREF_KEY_position, position);
            editor.commit();
            startTime = getStartTime();

        } else if (audioPlay) {
            listSize = mainPlayModelList.size();
            id = mainPlayModelList.get(position).getID();
            name = mainPlayModelList.get(position).getName();
            url = mainPlayModelList.get(position).getAudioFile();
            if (mainPlayModelList.get(position).getPlaylistID() == null) {
                mainPlayModelList.get(position).setPlaylistID("");
            }
            GetMedia(url, ctx, mainPlayModelList.get(position).getDownload(), mainPlayModelList.get(position).getPlaylistID());
            binding.tvName.setText(mainPlayModelList.get(position).getName());
            if (mainPlayModelList.get(position).getAudioDirection().equalsIgnoreCase("")) {
                binding.llDirection.setVisibility(View.GONE);
            } else {
                binding.llDirection.setVisibility(View.VISIBLE);
                binding.tvDireDesc.setText(mainPlayModelList.get(position).getAudioDirection());
            }
            binding.tvTitle.setText(mainPlayModelList.get(position).getAudiomastercat());
            binding.tvDesc.setText(mainPlayModelList.get(position).getAudioSubCategory());
            Glide.with(getApplicationContext()).load(mainPlayModelList.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);

            if (mainPlayModelList.get(position).getLike().equalsIgnoreCase("1")) {
                binding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
            } else if (mainPlayModelList.get(position).getLike().equalsIgnoreCase("0")) {
                binding.ivLike.setImageResource(R.drawable.ic_unlike_icon);
            }
            binding.tvSongTime.setText(mainPlayModelList.get(position).getAudioDuration());
            startTime = getStartTime();
        }
        addToRecentPlay();

        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(CONSTANTS.PREF_KEY_position, position);
        editor.commit();
        binding.simpleSeekbar.setClickable(true);
        handler.postDelayed(UpdateSongTime, 60);
        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
    }

    private void setMediaPlayer(String download, FileDescriptor fileDescriptor) {
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
                isPause = false;
            }
            mediaPlayer = new MediaPlayer();
            if (download.equalsIgnoreCase("1")) {
                mediaPlayer.setDataSource(fileDescriptor);
            } else {
                mediaPlayer.setDataSource(url);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaPlayer.setAudioAttributes(
                        new AudioAttributes
                                .Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build());
            }
            mediaPlayer.prepareAsync();
            isPause = false;
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
                binding.llProgressBar.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
                binding.llPlay.setVisibility(View.GONE);
                binding.llPause.setVisibility(View.VISIBLE);
            });
        }
    }

    private void callMedia() {
        FileDescriptor fileDescriptor = null;
        if (downloadAudioDetailsList.size() != 0) {
            binding.llProgressBar.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.llPlay.setVisibility(View.GONE);
            binding.llPause.setVisibility(View.GONE);
            DownloadMedia downloadMedia = new DownloadMedia(getApplicationContext());

            try {
                byte[] decrypt = null;
                decrypt = downloadMedia.decrypt(name);
                if (decrypt != null) {
                    fileDescriptor = FileUtils.getTempFileDescriptor(getApplicationContext(), decrypt);
//                    play2(fileDescriptor);
//                    playMedia();
                    setMediaPlayer("1", fileDescriptor);
                } else {
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        setMediaPlayer("0", fileDescriptor);
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.llProgressBar.setVisibility(View.GONE);
                        binding.llPlay.setVisibility(View.VISIBLE);
                        binding.llPause.setVisibility(View.GONE);
                        BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (BWSApplication.isNetworkConnected(ctx)) {
                binding.llProgressBar.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.llPlay.setVisibility(View.GONE);
                binding.llPause.setVisibility(View.GONE);
                setMediaPlayer("0", fileDescriptor);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.llProgressBar.setVisibility(View.GONE);
                binding.llPlay.setVisibility(View.VISIBLE);
                binding.llPause.setVisibility(View.GONE);
                BWSApplication.showToast(getString(R.string.no_server_found), ctx);
            }
        }
    }

    private void callComplete() {
        handler.removeCallbacks(UpdateSongTime);
        isPrepare = false;
        isMediaStart = false;
        isPause = false;
        if (IsRepeat.equalsIgnoreCase("1")) {
            if (position < (listSize - 1)) {
                position = position + 1;
            } else {
                position = 0;
            }
            getPrepareShowData(position);
        } else if (IsRepeat.equalsIgnoreCase("0")) {
            getPrepareShowData(position);
        } else if (IsShuffle.equalsIgnoreCase("1")) {
            // shuffle is on - play a random song
            if (queuePlay) {
                addToQueueModelList.remove(position);
                listSize = addToQueueModelList.size();
                if (listSize == 0) {
                    stopMedia();
                } else if (listSize == 1) {
                    stopMedia();
                } else {
                    Random random = new Random();
                    position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                    getPrepareShowData(position);
                }
            } else {
                if (listSize == 1) {
                    stopMedia();
                } else {
                    Random random = new Random();
                    position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                    getPrepareShowData(position);
                }
            }
        } else {
            if (queuePlay) {
                addToQueueModelList.remove(position);
                listSize = addToQueueModelList.size();
                if (position < listSize - 1) {
                    getPrepareShowData(position);
                } else {
                    if (listSize == 0) {
                        savePrefQueue(0, false, true, addToQueueModelList, ctx);
                        stopMedia();
                    } else {
                        position = 0;
                        getPrepareShowData(position);
                    }
                }
            } else {
                if (position < (listSize - 1)) {
                    position = position + 1;
                    getPrepareShowData(position);
                } else {
                    binding.llPlay.setVisibility(View.VISIBLE);
                    binding.llPause.setVisibility(View.GONE);
                    stopMedia();
                }
            }
        }
        if (listSize == 1) {
            binding.llnext.setEnabled(false);
            binding.llprev.setEnabled(false);
            binding.llnext.setClickable(false);
            binding.llprev.setClickable(false);
            binding.ivnext.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.ivprev.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            position = 0;
        } /*else if (position == listSize - 1 && IsRepeat.equalsIgnoreCase("1")) {
                binding.llnext.setEnabled(false);
                binding.llnext.setClickable(false);
                binding.ivnext.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else if (position == 0 && IsRepeat.equalsIgnoreCase("1")) {
                binding.llprev.setEnabled(false);
                binding.llprev.setClickable(false);
                binding.ivprev.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            }*/ else {
            binding.llnext.setEnabled(true);
            binding.llprev.setEnabled(true);
            binding.llnext.setClickable(true);
            binding.llprev.setClickable(true);
            binding.ivnext.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.ivprev.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(CONSTANTS.PREF_KEY_position, position);
        editor.commit();
        callRepeatShuffle();
    }

    @Override
    public void onBackPressed() {
        callBack();
    }

    private void callBack() {
        player = 1;
        if (binding.llPause.getVisibility() == View.VISIBLE) {
            isPause = false;
        }
//        pauseMedia();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        releasePlayer();
    }

    @Override
    protected void onResume() {
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
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
        mainPlayModelList = gson.fromJson(json, type);
        if (BWSApplication.isNetworkConnected(ctx)) {
            if (IsLock.equalsIgnoreCase("0")) {
                binding.llMore.setClickable(true);
                binding.llMore.setEnabled(true);
                binding.ivMore.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                binding.llMore.setClickable(false);
                binding.llMore.setEnabled(false);
                binding.ivMore.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        } else {
            binding.llMore.setClickable(false);
            binding.llMore.setEnabled(false);
            binding.ivMore.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        if (BWSApplication.isNetworkConnected(ctx)) {
            if (IsLock.equalsIgnoreCase("0")) {
                binding.llViewQueue.setClickable(true);
                binding.llViewQueue.setEnabled(true);
                binding.ivViewQueue.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                binding.llViewQueue.setClickable(false);
                binding.llViewQueue.setEnabled(false);
                binding.ivViewQueue.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        } else {
            binding.llViewQueue.setClickable(false);
            binding.llViewQueue.setEnabled(false);
            binding.ivViewQueue.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        queuePlay = shared.getBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
        audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        if (queuePlay) {
            position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
            listSize = addToQueueModelList.size();
            if (addToQueueModelList.get(position).getLike().equalsIgnoreCase("1")) {
                binding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
            } else if (addToQueueModelList.get(position).getLike().equalsIgnoreCase("0")) {
                binding.ivLike.setImageResource(R.drawable.ic_unlike_icon);
            }
        } else if (audioPlay) {
            position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
            listSize = mainPlayModelList.size();
            if (mainPlayModelList.get(position).getLike().equalsIgnoreCase("1")) {
                binding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
            } else if (mainPlayModelList.get(position).getLike().equalsIgnoreCase("0")) {
                binding.ivLike.setImageResource(R.drawable.ic_unlike_icon);
            }
        }
        if (listSize == 1) {
            position = 0;
        }
        if (isPrepare && !isMediaStart) {
            callMedia();
        } else if ((isMediaStart && isPlaying()) && !isPause) {
            binding.llPlay.setVisibility(View.GONE);
            binding.llPause.setVisibility(View.VISIBLE);
            binding.llProgressBar.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
        } else {
            binding.llPlay.setVisibility(View.VISIBLE);
            binding.llPause.setVisibility(View.GONE);
            binding.llProgressBar.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
        }
        super.onResume();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
     /*   handler.removeCallbacks(UpdateSongTime);
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
        handler.removeCallbacks(UpdateSongTime);
    }

    public void updateProgressBar() {
        handler.postDelayed(UpdateSongTime, 60);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(UpdateSongTime);
        if (isMediaStart) {
            int totalDuration = getEndTime();
            int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);

            oTime = binding.simpleSeekbar.getProgress();
            // forward or backward to certain seconds
            SeekTo(currentPosition);
        }
        // update timer progress again
        updateProgressBar();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    @Override
    public void onAudioFocusChange(int i) {
        switch (i) {
            case AudioManager.AUDIOFOCUS_GAIN:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Resume your media player here
                resumeMedia();
                binding.llPlay.setVisibility(View.GONE);
                binding.llPause.setVisibility(View.VISIBLE);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (isMediaStart) {
                    pauseMedia();
                    binding.llPlay.setVisibility(View.VISIBLE);
                    binding.llPause.setVisibility(View.GONE);
                }
//                MusicService.pauseMedia();// Pause your media player here
                break;
        }
    }

/*    @Override
    public void onProgress(Progress progress) {
        if(!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(name)){
            handler.postDelayed(UpdateSongTime1, 10);
        }else{
            handler.removeCallbacks(UpdateSongTime1);
        }
    }*/
}