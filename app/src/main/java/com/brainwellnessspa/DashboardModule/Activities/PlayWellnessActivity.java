package com.brainwellnessspa.DashboardModule.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.media.AudioAttributes;
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

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Models.AddToQueueModel;
import com.brainwellnessspa.DashboardModule.Models.AppointmentDetailModel;
import com.brainwellnessspa.DashboardModule.Models.AudioLikeModel;
import com.brainwellnessspa.DashboardModule.Models.MainAudioModel;
import com.brainwellnessspa.DashboardModule.Models.SubPlayListModel;
import com.brainwellnessspa.DashboardModule.Models.SucessModel;
import com.brainwellnessspa.DashboardModule.Models.ViewAllAudioListModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia;
import com.brainwellnessspa.EncryptDecryptUtils.FileUtils;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.ActivityPlayWellnessBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.downloader.PRDownloader;
import com.downloader.Status;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

import static com.brainwellnessspa.DashboardModule.Activities.AddQueueActivity.comeFromAddToQueue;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.player;
import static com.brainwellnessspa.DashboardModule.Audio.AudioFragment.IsLock;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.downloadIdOne;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.downloadProgress;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.filename;
import static com.brainwellnessspa.Utility.MusicService.SeekTo;
import static com.brainwellnessspa.Utility.MusicService.ToBackward;
import static com.brainwellnessspa.Utility.MusicService.ToForward;
import static com.brainwellnessspa.Utility.MusicService.getEndTime;
import static com.brainwellnessspa.Utility.MusicService.getProgressPercentage;
import static com.brainwellnessspa.Utility.MusicService.getStartTime;
import static com.brainwellnessspa.Utility.MusicService.isCompleteStop;
import static com.brainwellnessspa.Utility.MusicService.isMediaStart;
import static com.brainwellnessspa.Utility.MusicService.isPause;
import static com.brainwellnessspa.Utility.MusicService.isPrepare;
import static com.brainwellnessspa.Utility.MusicService.isStop;
import static com.brainwellnessspa.Utility.MusicService.isprogressbar;
import static com.brainwellnessspa.Utility.MusicService.mediaPlayer;
import static com.brainwellnessspa.Utility.MusicService.oTime;
import static com.brainwellnessspa.Utility.MusicService.pauseMedia;
import static com.brainwellnessspa.Utility.MusicService.progressToTimer;
import static com.brainwellnessspa.Utility.MusicService.resumeMedia;
import static com.brainwellnessspa.Utility.MusicService.savePrefQueue;
import static com.brainwellnessspa.Utility.MusicService.stopMedia;

public class PlayWellnessActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener/*, AudioManager.OnAudioFocusChangeListener, OnProgressListener*/ {
    ActivityPlayWellnessBinding binding;
    String IsRepeat = "", IsShuffle = "", UserID, PlaylistId = "", AudioFlag, id, name, url;
    int startTime = 0, endTime = 0, position, listSize, myCount, progress, downloadPercentage;
    Context ctx;
    Activity activity;
    Boolean queuePlay, audioPlay;
    ArrayList<MainPlayModel> mainPlayModelList;
    ArrayList<AddToQueueModel> addToQueueModelList;
    List<DownloadAudioDetails> downloadAudioDetailsList;
    long myProgress = 0,diff = 0;
    private long mLastClickTime = 0, totalDuration, currentDuration = 0;
    private Handler handler;
//    private Handler handler1;
    //        private AudioManager mAudioManager;
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
                            if (mediaPlayer != null) {
                                totalDuration = mediaPlayer.getDuration();
                            } else {
                                t = Time.valueOf("00:" + downloadAudioDetailsList.get(0).getAudioDuration());
                            }
                        } else {
                            endtimetext = addToQueueModelList.get(position).getAudioDuration();
                            if (mediaPlayer != null) {
                                totalDuration = mediaPlayer.getDuration();
                            } else {
                                t = Time.valueOf("00:" + addToQueueModelList.get(position).getAudioDuration());
                            }
                        }
                    } else {
                        endtimetext = addToQueueModelList.get(position).getAudioDuration();
                        if (mediaPlayer != null) {
                            totalDuration = mediaPlayer.getDuration();
                        } else {
                            t = Time.valueOf("00:" + addToQueueModelList.get(position).getAudioDuration());
                        }
                    }
                } else {
                    binding.llPlay.setVisibility(View.VISIBLE);
                    binding.llPause.setVisibility(View.GONE);
                    stopMedia();
                }
            } else if (audioPlay) {
                if (!BWSApplication.isNetworkConnected(ctx)) {
                    if (downloadAudioDetailsList.size() != 0) {
                        endtimetext = downloadAudioDetailsList.get(0).getAudioDuration();
                        if (mediaPlayer != null) {
                            totalDuration = mediaPlayer.getDuration();
                        } else {
                            t = Time.valueOf("00:" + downloadAudioDetailsList.get(0).getAudioDuration());
                        }
                    } else {
                        endtimetext = mainPlayModelList.get(position).getAudioDuration();
                        if (mediaPlayer != null) {
                            totalDuration = mediaPlayer.getDuration();
                        } else {
                            t = Time.valueOf("00:" + mainPlayModelList.get(position).getAudioDuration());
                        }
                    }
                } else {
                    endtimetext = mainPlayModelList.get(position).getAudioDuration();
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
                } else
                    totalDuration = t.getTime();
            }
            myProgress = currentDuration;
            currentDuration = getStartTime();
            diff = totalDuration - myProgress;
//            Log.e("myProgress old!!!", String.valueOf(myProgress));
            if (myProgress == currentDuration && myProgress != 0 && !isPause && url.equalsIgnoreCase("")) {
//                    Log.e("myProgress",String.valueOf(myProgress));
                myCount++;
                Log.e("myCount", String.valueOf(myCount));

                if (myCount == 10) {
                    Log.e("myCount complete", String.valueOf(myCount));
                    callComplete();
                    myCount = 0;
                }
            } else if (myProgress == currentDuration && myProgress != 0 && !isPause && diff < 2000) {
//                Log.e("myProgress", String.valueOf(myProgress));
                myCount++;
                Log.e("myCount", String.valueOf(myCount));

                if (myCount == 200) {
                    Log.e("myCount complete", String.valueOf(myCount));
                    callComplete();
                    myCount = 0;
                }
            }
            progress = getProgressPercentage(currentDuration, totalDuration);
             if (currentDuration == 0 && isCompleteStop) {
                binding.progressBar.setVisibility(View.GONE);
                binding.llProgressBar.setVisibility(View.GONE);
                binding.llPause.setVisibility(View.GONE);
                binding.llPlay.setVisibility(View.VISIBLE);
            }else if (currentDuration == 0 && isprogressbar) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.llProgressBar.setVisibility(View.VISIBLE);
                binding.llPause.setVisibility(View.GONE);
                binding.llPlay.setVisibility(View.GONE);
            }else if (currentDuration > 1 && !isPause) {
                binding.progressBar.setVisibility(View.GONE);
                binding.llProgressBar.setVisibility(View.GONE);
                binding.llPause.setVisibility(View.VISIBLE);
                binding.llPlay.setVisibility(View.GONE);
                isprogressbar = false;
            } else if (currentDuration >= 1 && isPause) {
                binding.progressBar.setVisibility(View.GONE);
                binding.llProgressBar.setVisibility(View.GONE);
                binding.llPause.setVisibility(View.GONE);
                binding.llPlay.setVisibility(View.VISIBLE);
                isprogressbar = false;
            }
            //Log.d("Progress", ""+progress);
            startTime = getStartTime();
            if (currentDuration == totalDuration && currentDuration != 0 && !isStop) {
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
            handler.postDelayed(this, 100);
        }
    };
/*    private Runnable UpdateSongTime1 = new Runnable() {
        @Override
        public void run() {
            if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(name)) {
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
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        overridePendingTransition(R.anim.enter, R.anim.exit);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_play_wellness);
        handler = new Handler();
//        handler1 = new Handler();
        ctx = PlayWellnessActivity.this;
        activity = PlayWellnessActivity.this;
        addToQueueModelList = new ArrayList<>();
        downloadAudioDetailsList = new ArrayList<>();
        mainPlayModelList = new ArrayList<>();
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
                1, 1, 0.92f, 0);
        binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
/*        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);*/
        if (BWSApplication.isNetworkConnected(ctx)) {
            if (IsLock.equalsIgnoreCase("1")) {
                binding.llMore.setClickable(false);
                binding.llMore.setEnabled(false);
                binding.ivMore.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else if (IsLock.equalsIgnoreCase("2")) {
                binding.llMore.setClickable(false);
                binding.llMore.setEnabled(false);
                binding.ivMore.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                binding.llMore.setClickable(true);
                binding.llMore.setEnabled(true);
                binding.ivMore.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        } else {
            binding.llMore.setClickable(false);
            binding.llMore.setEnabled(false);
            binding.ivMore.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
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
        handler.postDelayed(UpdateSongTime, 100);
        getPrepareShowData(position);
        /*if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(name)) {
            handler1.postDelayed(UpdateSongTime1, 500);
        } else {
            binding.pbProgress.setVisibility(View.GONE);
            handler1.removeCallbacks(UpdateSongTime1);
        }*/
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
//            finish();
        });

        binding.llViewQueue.setOnClickListener(view -> {
//            handler1.removeCallbacks(UpdateSongTime1);
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
            handler.postDelayed(UpdateSongTime, 100);
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
            if (IsRepeat.equalsIgnoreCase("1") || IsRepeat.equalsIgnoreCase("0")) {
                // repeat is on play same song again
                if (position < listSize - 1) {
                    position = position + 1;
                } else {
                    position = 0;
                }
                getPrepareShowData(position);
            }/* else if (IsRepeat.equalsIgnoreCase("0")) {
                getPrepareShowData(position);
            }*/ else if (IsShuffle.equalsIgnoreCase("1")) {
                // shuffle is on - play a random song
                if (queuePlay) {
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        addToQueueModelList.remove(position);
                        listSize = addToQueueModelList.size();
                        if (listSize == 0) {
                            isCompleteStop = true;
                            stopMedia();
                        } else if (listSize == 1) {
                            isCompleteStop = true;
                            stopMedia();
                        } else {
                            Random random = new Random();
                            position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                            getPrepareShowData(position);
                        }
                    } else {
                        BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                    }
                } else {
                    Random random = new Random();
                    position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                    getPrepareShowData(position);
                }
            } else {
                if (queuePlay) {
                    if (BWSApplication.isNetworkConnected(ctx)) {
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
                        BWSApplication.showToast(getString(R.string.no_server_found), ctx);
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
            if (IsRepeat.equalsIgnoreCase("1") || IsRepeat.equalsIgnoreCase("0")) {
                // repeat is on play same song again
                if (position > 0) {
                    position = position - 1;
                    getPrepareShowData(position);
                } else if (listSize != 1) {
                    position = listSize - 1;
                    getPrepareShowData(position);
                }
            }/* else if (IsRepeat.equalsIgnoreCase("0")) {
                getPrepareShowData(position);
            }*/ else if (IsShuffle.equalsIgnoreCase("1")) {
                // shuffle is on - play a random song
                if (queuePlay) {
                    if (BWSApplication.isNetworkConnected(ctx)) {
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
                        BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                    }
                } else {
                    Random random = new Random();
                    position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                    getPrepareShowData(position);
                }
            } else {
                if (queuePlay) {
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        addToQueueModelList.remove(position);
                        listSize = addToQueueModelList.size();
                        if (position > 0) {
                            getPrepareShowData(position - 1);
                        } else {
                            if (listSize == 0) {
                                savePrefQueue(0, false, true, addToQueueModelList, ctx);
                                binding.llPlay.setVisibility(View.VISIBLE);
                                binding.llPause.setVisibility(View.GONE);
                                stopMedia();
                            } else {
                                position = 0;
                                getPrepareShowData(position);
                            }
                        }
                    } else {
                        BWSApplication.showToast(getString(R.string.no_server_found), ctx);
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
                binding.llPlay.setVisibility(View.VISIBLE);
                binding.llPause.setVisibility(View.GONE);
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
        if (!url.equalsIgnoreCase("")) {
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
                if (fileNameList.size() != 0) {
                    url1.addAll(audioFile1);
                    name1.addAll(fileNameList);
                    downloadPlaylistId.addAll(playlistId1);
                }
            }
            url1.add(url);
            name1.add(name);
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
            DownloadMedia downloadMedia = new DownloadMedia(getApplicationContext());
            downloadMedia.encrypt1(url1, name1, downloadPlaylistId);

          /*  if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(name)) {
                handler1.postDelayed(UpdateSongTime1, 500);
            } else {
                binding.pbProgress.setVisibility(View.GONE);
                handler1.removeCallbacks(UpdateSongTime1);
            }*/
          binding.pbProgress.setVisibility(View.VISIBLE);
          binding.ivDownloads.setVisibility(View.GONE);
            SaveMedia(EncodeBytes, FileUtils.getFilePath(getApplicationContext(), name));
   /*     if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
            Call<DownloadPlaylistModel> listCall = APIClient.getClient().getDownloadlistPlaylist(UserID, id, PlaylistId);
            listCall.enqueue(new Callback<DownloadPlaylistModel>() {
                @Override
                public void onResponse(Call<DownloadPlaylistModel> call, Response<DownloadPlaylistModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
                        DownloadPlaylistModel model = response.body();
                        if (model.getResponseData().getFlag().equalsIgnoreCase("0")
                                || model.getResponseData().getFlag().equalsIgnoreCase("")) {
                            binding.llDownloads.setClickable(true);
                            binding.llDownloads.setEnabled(true);
                            binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
                        } else if (model.getResponseData().getFlag().equalsIgnoreCase("1")) {
                            binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
                            binding.ivDownloads.setColorFilter(getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
                            binding.llDownloads.setClickable(false);
                            binding.llDownloads.setEnabled(false);
                        }
                        BWSApplication.showToast(model.getResponseMessage(), ctx);
                    }
                }

                @Override
                public void onFailure(Call<DownloadPlaylistModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
                }
            });

        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }*/
        }
    }

    private void getMediaByPer() {
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

                if (downloadAudioDetailsList.size() != 0) {
                    if (downloadPercentage <= 100) {
                        if (downloadPercentage == 100) {
                            binding.pbProgress.setVisibility(View.GONE);
                            binding.ivDownloads.setVisibility(View.VISIBLE);
//                            handler1.removeCallbacks(UpdateSongTime1);
                        } else {
                            binding.pbProgress.setVisibility(View.VISIBLE);
                            binding.ivDownloads.setVisibility(View.GONE);
                            binding.pbProgress.setIndeterminate(false);
                            binding.pbProgress.setProgress(downloadPercentage);
                            getMediaByPer();
//                             handler1.postDelayed(UpdateSongTime1, 500);
                        }
                    } else {
                        binding.pbProgress.setVisibility(View.GONE);
                        binding.ivDownloads.setVisibility(View.VISIBLE);
//                        handler1.removeCallbacks(UpdateSongTime1);
                    }
                }
                super.onPostExecute(aVoid);
            }
        }
        getMediaByPer st = new getMediaByPer();
        st.execute();
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
                    downloadAudioDetails.setIsDownload("pending");
                    downloadAudioDetails.setDownloadProgress(0);
                }

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

    private void disableDownload() {
        binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
        binding.ivDownloads.setColorFilter(getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
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
                editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "");
                editor.commit();
                IsRepeat = "";
                if (queuePlay) {
                    binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
                } else
                    binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
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
//            BWSApplication.showToast("Repeat mode has been turned on", ctx);
            binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (IsRepeat.equalsIgnoreCase("0")) {
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "1");
            IsRepeat = "1";
            if (listSize == 1) {
                editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "");
                IsRepeat = "";
//                BWSApplication.showToast("Repeat mode has been turned off", ctx);
                binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
//                BWSApplication.showToast("Repeat mode has been turned on", ctx);
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
//            BWSApplication.showToast("Repeat mode has been turned off", ctx);
        }
    }

    private void callLike() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
            Call<AudioLikeModel> listCall = APIClient.getClient().getAudioLike(id, UserID);
            listCall.enqueue(new Callback<AudioLikeModel>() {
                @Override
                public void onResponse(Call<AudioLikeModel> call, Response<AudioLikeModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
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
                    BWSApplication.hideProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    private void addToRecentPlay() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
            Call<SucessModel> listCall = APIClient.getClient().getRecentlyplayed(id, UserID);
            listCall.enqueue(new Callback<SucessModel>() {
                @Override
                public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
                        SucessModel model = response.body();
                    }
                }

                @Override
                public void onFailure(Call<SucessModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
//            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    public void GetMedia() {

        downloadAudioDetailsList = new ArrayList<>();

        class GetMedia extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                downloadAudioDetailsList = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .getaudioByPlaylist(url, "");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (!url.equalsIgnoreCase("")) {
                    if (downloadAudioDetailsList.size() != 0) {
                        if (downloadAudioDetailsList.get(0).getDownload().equalsIgnoreCase("1")) {
                            binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                            binding.llDownload.setClickable(false);
                            binding.llDownload.setEnabled(false);
                            binding.ivDownloads.setColorFilter(getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
                        } else/* if (!mainPlayModelList.get(position).getDownload().equalsIgnoreCase("")) */ {
                            binding.llDownload.setClickable(true);
                            binding.llDownload.setEnabled(true);
                            binding.ivDownloads.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                            binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                        }
                    } else/* if (!mainPlayModelList.get(position).getDownload().equalsIgnoreCase("")) */ {
                        binding.llDownload.setClickable(true);
                        binding.llDownload.setEnabled(true);
                        binding.ivDownloads.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                        binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                    }
                }

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
                } else if (isCompleteStop) {
                    binding.llProgressBar.setVisibility(View.GONE);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.llPlay.setVisibility(View.VISIBLE);
                    binding.llPause.setVisibility(View.GONE);
                } else if (isMediaStart && !isPause) {
                    binding.llProgressBar.setVisibility(View.GONE);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.llPause.setVisibility(View.VISIBLE);
                    binding.llPlay.setVisibility(View.GONE);
                } else {
                    binding.llProgressBar.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.llPause.setVisibility(View.GONE);
                    binding.llPlay.setVisibility(View.GONE);
                    callMedia();
                }
                super.onPostExecute(aVoid);

            }
        }
        GetMedia st = new GetMedia();
        st.execute();
    }
    public void GetMedia2() {

        downloadAudioDetailsList = new ArrayList<>();

        class GetMedia extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                downloadAudioDetailsList = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .getaudioByPlaylist(url, "");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (!url.equalsIgnoreCase("")) {
                    if (downloadAudioDetailsList.size() != 0) {
                        if (downloadAudioDetailsList.get(0).getDownload().equalsIgnoreCase("1")) {
                            binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                            binding.llDownload.setClickable(false);
                            binding.llDownload.setEnabled(false);
                            binding.ivDownloads.setColorFilter(getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
                        } else/* if (!mainPlayModelList.get(position).getDownload().equalsIgnoreCase("")) */ {
                            binding.llDownload.setClickable(true);
                            binding.llDownload.setEnabled(true);
                            binding.ivDownloads.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                            binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                        }
                    } else/* if (!mainPlayModelList.get(position).getDownload().equalsIgnoreCase("")) */ {
                        binding.llDownload.setClickable(true);
                        binding.llDownload.setEnabled(true);
                        binding.ivDownloads.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                        binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                    }
                }

                getMediaByPer();

                super.onPostExecute(aVoid);

            }
        }
        GetMedia st = new GetMedia();
        st.execute();
    }

    private void getPrepareShowData(int position) {
        handler.postDelayed(UpdateSongTime, 100);
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
        BWSApplication.showProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
        if (queuePlay) {
            listSize = addToQueueModelList.size();
            if (listSize == 1) {
                position = 0;
            }
            if(listSize!=0) {
                id = addToQueueModelList.get(position).getID();
                name = addToQueueModelList.get(position).getName();
                url = addToQueueModelList.get(position).getAudioFile();
                GetMedia();
                binding.tvName.setText(addToQueueModelList.get(position).getName());
                binding.tvDireName.setText(R.string.Directions);
                binding.tvDireName.setText(R.string.Directions);
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
            }
            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String json = gson.toJson(addToQueueModelList);
            editor.putString(CONSTANTS.PREF_KEY_queueList, json);
            editor.putInt(CONSTANTS.PREF_KEY_position, position);
            editor.commit();
            startTime = getStartTime();

        } else if (audioPlay) {
            try {
                listSize = mainPlayModelList.size();
                if (listSize == 1) {
                    position = 0;
                }
                if(listSize!=0) {
                    id = mainPlayModelList.get(position).getID();
                    name = mainPlayModelList.get(position).getName();
                    url = mainPlayModelList.get(position).getAudioFile();
                    if (url.equalsIgnoreCase("") || url.isEmpty()) {
                        isDisclaimer = 1;
                        callAllDisable(false);

                    } else {
                        isDisclaimer = 0;
                        callAllDisable(true);
                    }
                    if (mainPlayModelList.get(position).getPlaylistID() == null) {
                        mainPlayModelList.get(position).setPlaylistID("");
                    }
                    GetMedia();
                    binding.tvName.setText(mainPlayModelList.get(position).getName());
                    if (mainPlayModelList.get(position).getAudioDirection().equalsIgnoreCase("")) {
                        binding.llDirection.setVisibility(View.GONE);
                    } else {
                        binding.llDirection.setVisibility(View.VISIBLE);
                        binding.tvDireDesc.setText(mainPlayModelList.get(position).getAudioDirection());
                    }
                    binding.tvTitle.setText(mainPlayModelList.get(position).getAudiomastercat());
                    binding.tvDesc.setText(mainPlayModelList.get(position).getAudioSubCategory());
                    if (url.equalsIgnoreCase("")) {
                        Glide.with(ctx).load(R.drawable.disclaimer).thumbnail(0.05f)
                                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
                    } else {
                        /*TODO */
                        Glide.with(ctx).load(mainPlayModelList.get(position).getImageFile()).thumbnail(0.05f)
                                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
                    }
                    if (mainPlayModelList.get(position).getLike().equalsIgnoreCase("1")) {
                        binding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
                    } else if (mainPlayModelList.get(position).getLike().equalsIgnoreCase("0")) {
                        binding.ivLike.setImageResource(R.drawable.ic_unlike_icon);
                    }
                    binding.tvSongTime.setText(mainPlayModelList.get(position).getAudioDuration());
                }
                startTime = getStartTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        getMediaByPer();

        if (!url.equalsIgnoreCase("")) {
            addToRecentPlay();
        }

        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(CONSTANTS.PREF_KEY_position, position);
        editor.commit();

        handler.postDelayed(UpdateSongTime, 100);
        BWSApplication.hideProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
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
            isPause = false;
            DownloadMedia downloadMedia = new DownloadMedia(getApplicationContext());
            getDownloadMedia(downloadMedia);

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

    private void getDownloadMedia(DownloadMedia downloadMedia) {
        class getDownloadMedia extends AsyncTask<Void, Void, Void> {
            FileDescriptor fileDescriptor = null;

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    byte[] decrypt = null;
                    decrypt = downloadMedia.decrypt(name);
                    if (decrypt != null) {
                        fileDescriptor = FileUtils.getTempFileDescriptor(getApplicationContext(), decrypt);
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
                        binding.llProgressBar.setVisibility(View.GONE);
                        binding.llPlay.setVisibility(View.VISIBLE);
                        binding.llPause.setVisibility(View.GONE);
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
        handler.removeCallbacks(UpdateSongTime);
        isPrepare = false;
        isMediaStart = false;
        isPause = false;
        if (audioPlay && (url.equalsIgnoreCase("") || url.isEmpty())) {
            isDisclaimer = 0;
            removeArray();
            getPrepareShowData(position);
        } else {
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
                        binding.llPlay.setVisibility(View.VISIBLE);
                        binding.llPause.setVisibility(View.GONE);
                        binding.pbProgressBar.setVisibility(View.GONE);
                        binding.llProgressBar.setVisibility(View.GONE);
                        isCompleteStop = true;
                        stopMedia();
                    } else if (listSize == 1) {
                        binding.llPlay.setVisibility(View.VISIBLE);
                        binding.llPause.setVisibility(View.GONE);
                        binding.pbProgressBar.setVisibility(View.GONE);
                        binding.llProgressBar.setVisibility(View.GONE);
                        isCompleteStop = true;
                        stopMedia();
                    } else {
                        int oldPosition = position;
                        Random random = new Random();
                        position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                        if (oldPosition == position) {
                            Random random1 = new Random();
                            position = random1.nextInt((listSize - 1) - 0 + 1) + 0;
                        }
                        getPrepareShowData(position);
                    }
                } else {
                    if (listSize == 1) {
                        binding.llPlay.setVisibility(View.VISIBLE);
                        binding.llPause.setVisibility(View.GONE);
                        binding.llProgressBar.setVisibility(View.GONE);
                        binding.progressBar.setVisibility(View.GONE);
                        isCompleteStop = true;
                        stopMedia();
                    } else {
                        int oldPosition = position;
                        Random random = new Random();
                        position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                        if (oldPosition == position) {
                            Random random1 = new Random();
                            position = random1.nextInt((listSize - 1) - 0 + 1) + 0;
                        }
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
                            binding.llPlay.setVisibility(View.VISIBLE);
                            binding.llPause.setVisibility(View.GONE);
                            binding.pbProgressBar.setVisibility(View.GONE);
                            binding.llProgressBar.setVisibility(View.GONE);
                            isCompleteStop = true;
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
                        if (listSize == 1) {
                            binding.llPlay.setVisibility(View.VISIBLE);
                            binding.llPause.setVisibility(View.GONE);
                            binding.pbProgressBar.setVisibility(View.GONE);
                            binding.llProgressBar.setVisibility(View.GONE);
                            isCompleteStop = true;
                            stopMedia();
                        } else {
                            binding.llPlay.setVisibility(View.VISIBLE);
                            binding.llPause.setVisibility(View.GONE);
                            binding.pbProgressBar.setVisibility(View.GONE);
                            binding.llProgressBar.setVisibility(View.GONE);
                            isCompleteStop = true;
                            stopMedia();
//                        position = 0;
//                        getPrepareShowData(position);
                        }
                    }
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

    private void removeArray() {
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        Gson gson = new Gson();
        String json1 = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson));
        mainPlayModelList = new ArrayList<>();
        MainPlayModel mainPlayModel;
        if (AudioFlag.equalsIgnoreCase("MainAudioList")) {

            Type type = new TypeToken<ArrayList<MainAudioModel.ResponseData.Detail>>() {
            }.getType();
            ArrayList<MainAudioModel.ResponseData.Detail> arrayList = gson.fromJson(json1, type);

            arrayList.remove(0);
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
            ArrayList<ViewAllAudioListModel.ResponseData.Detail> arrayList = gson.fromJson(json1, type);
            arrayList.remove(0);
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
            ArrayList<AppointmentDetailModel.Audio> arrayList = gson.fromJson(json1, type);
            arrayList.remove(0);
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
            ArrayList<DownloadAudioDetails> arrayList = gson.fromJson(json1, type);
            arrayList.remove(0);
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
            ArrayList<DownloadAudioDetails> arrayList = gson.fromJson(json1, type);
            arrayList.remove(0);
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
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json1, type);

            arrayList.remove(0);
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
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json1, type);
            arrayList.remove(0);
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
        shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);

    }

    private void MakeArray2() {
        Gson gson = new Gson();
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
        mainPlayModelList = new ArrayList<>();
        MainPlayModel mainPlayModel;
        String json = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson));
        if (AudioFlag.equalsIgnoreCase("MainAudioList")) {
            Type type = new TypeToken<ArrayList<MainAudioModel.ResponseData.Detail>>() {
            }.getType();
            ArrayList<MainAudioModel.ResponseData.Detail> arrayList = gson.fromJson(json, type);
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
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("ViewAllAudioList")) {
            Type type = new TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail>>() {
            }.getType();
            ArrayList<ViewAllAudioListModel.ResponseData.Detail> arrayList = gson.fromJson(json, type);
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
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
        } else if (AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
            Type type = new TypeToken<ArrayList<AppointmentDetailModel.Audio>>() {
            }.getType();
            ArrayList<AppointmentDetailModel.Audio> arrayList = gson.fromJson(json, type);
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
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
        } else if (AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
            Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
            }.getType();
            ArrayList<DownloadAudioDetails> arrayList = gson.fromJson(json, type);
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
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
        } else if (AudioFlag.equalsIgnoreCase("Downloadlist")) {
            Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
            }.getType();
            ArrayList<DownloadAudioDetails> arrayList = gson.fromJson(json, type);
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
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
        } else if (AudioFlag.equalsIgnoreCase("TopCategories")) {
            Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
            }.getType();
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);
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
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
        } else if (AudioFlag.equalsIgnoreCase("SubPlayList")) {
            Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
            }.getType();
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);

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
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
        }
    }


    @Override
    public void onBackPressed() {
        callBack();
    }

    private void callBack() {
        handler.removeCallbacks(UpdateSongTime);
//        handler1.removeCallbacks(UpdateSongTime1);
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

    private void addDeclaimer() {
        MainPlayModel mainPlayModel = new MainPlayModel();
        mainPlayModel.setID("0");
        mainPlayModel.setName("Disclaimer");
        mainPlayModel.setAudioFile("");
        mainPlayModel.setPlaylistID("");
        mainPlayModel.setAudioDirection("The audio shall start playing after the disclaimer");
        mainPlayModel.setAudiomastercat("");
        mainPlayModel.setAudioSubCategory("");
        mainPlayModel.setImageFile("");
        mainPlayModel.setLike("");
        mainPlayModel.setDownload("");
        mainPlayModel.setAudioDuration("0:48");
        mainPlayModelList.add(mainPlayModel);
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
            if (IsLock.equalsIgnoreCase("1")) {
                binding.llMore.setClickable(false);
                binding.llMore.setEnabled(false);
                binding.ivMore.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else if (IsLock.equalsIgnoreCase("2")) {
                binding.llMore.setClickable(false);
                binding.llMore.setEnabled(false);
                binding.ivMore.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                binding.llMore.setClickable(true);
                binding.llMore.setEnabled(true);
                binding.ivMore.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        } else {
            binding.llMore.setClickable(false);
            binding.llMore.setEnabled(false);
            binding.ivMore.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
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
        /*if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(name)) {
            handler1.postDelayed(UpdateSongTime1, 500);
        } else {
            binding.pbProgress.setVisibility(View.GONE);
            handler1.removeCallbacks(UpdateSongTime1);
        }*/
        getMediaByPer();
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
            if(listSize!=0) {
                if (mainPlayModelList.get(position).getLike().equalsIgnoreCase("1")) {
                    binding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
                } else if (mainPlayModelList.get(position).getLike().equalsIgnoreCase("0")) {
                    binding.ivLike.setImageResource(R.drawable.ic_unlike_icon);
                }
                url = mainPlayModelList.get(position).getAudioFile();
            }
            if (url.equalsIgnoreCase("") || url.isEmpty()) {
                isDisclaimer = 1;
                callAllDisable(false);

            } else {
                isDisclaimer = 0;
                callAllDisable(true);
            }
        }
        if (comeFromAddToQueue) {
            getPrepareShowData(position);
            comeFromAddToQueue = false;
        }
        if (listSize == 1) {
            position = 0;
        }
       /* if (isPrepare && !isMediaStart) {
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
        }*/
        super.onResume();
    }

    private void callAllDisable(boolean b) {
        if (b) {
            binding.llnext.setClickable(true);
            binding.llnext.setEnabled(true);
            binding.llnext.setAlpha(1f);
            binding.llprev.setClickable(true);
            binding.llprev.setEnabled(true);
            binding.llprev.setAlpha(1f);
            binding.llForwardSec.setClickable(true);
            binding.llForwardSec.setEnabled(true);
            binding.llForwardSec.setAlpha(1f);
            binding.llBackWordSec.setClickable(true);
            binding.llBackWordSec.setEnabled(true);
            binding.llBackWordSec.setAlpha(1f);
            binding.llMore.setClickable(true);
            binding.llMore.setEnabled(true);
            binding.llMore.setAlpha(1f);
            binding.llViewQueue.setClickable(true);
            binding.llViewQueue.setEnabled(true);
            binding.llViewQueue.setAlpha(1f);
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
            binding.rlSeekbar.setClickable(true);
            binding.rlSeekbar.setEnabled(true);
            binding.simpleSeekbar.setClickable(true);
            binding.simpleSeekbar.setEnabled(true);
//            binding.simpleSeekbar.set
        } else {
            binding.llnext.setClickable(false);
            binding.llnext.setEnabled(false);
            binding.llnext.setAlpha(0.7f);
            binding.llprev.setClickable(false);
            binding.llprev.setEnabled(false);
            binding.llprev.setAlpha(0.7f);
            binding.llForwardSec.setClickable(false);
            binding.llForwardSec.setEnabled(false);
            binding.llForwardSec.setAlpha(0.7f);
            binding.llBackWordSec.setClickable(false);
            binding.llBackWordSec.setEnabled(false);
            binding.llBackWordSec.setAlpha(0.7f);
            binding.llMore.setClickable(false);
            binding.llMore.setEnabled(false);
            binding.llMore.setAlpha(0.7f);
            binding.llViewQueue.setClickable(false);
            binding.llViewQueue.setEnabled(false);
            binding.llViewQueue.setAlpha(0.7f);
            binding.llDownload.setClickable(false);
            binding.llDownload.setEnabled(false);
            binding.llDownload.setAlpha(0.7f);
            binding.llRepeat.setClickable(false);
            binding.llRepeat.setEnabled(false);
            binding.llRepeat.setAlpha(0.7f);
            binding.llShuffle.setClickable(false);
            binding.llShuffle.setEnabled(false);
            binding.llShuffle.setAlpha(0.7f);
            binding.llLike.setClickable(false);
            binding.llLike.setEnabled(false);
            binding.llLike.setAlpha(0.7f);
            binding.rlSeekbar.setClickable(false);
            binding.rlSeekbar.setEnabled(false);
            binding.simpleSeekbar.setClickable(false);
            binding.simpleSeekbar.setEnabled(false);
        }
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
        handler.postDelayed(UpdateSongTime, 100);
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
 /*   @Override
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
    }*/

/*    @Override
    public void onProgress(Progress progress) {
        if(!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(name)){
            handler.postDelayed(UpdateSongTime1, 10);
        }else{
            handler.removeCallbacks(UpdateSongTime1);
        }
    }*/
}