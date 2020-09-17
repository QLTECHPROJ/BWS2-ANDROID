package com.qltech.bws.DashboardModule.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Models.AddToQueueModel;
import com.qltech.bws.DashboardModule.Models.AudioLikeModel;
import com.qltech.bws.DashboardModule.Models.DownloadPlaylistModel;
import com.qltech.bws.DashboardModule.Models.SucessModel;
import com.qltech.bws.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.ActivityPlayWellnessBinding;

import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.qltech.bws.DashboardModule.Activities.DashboardActivity.player;
import static com.qltech.bws.Utility.MusicService.SeekTo;
import static com.qltech.bws.Utility.MusicService.ToBackward;
import static com.qltech.bws.Utility.MusicService.ToForward;
import static com.qltech.bws.Utility.MusicService.ToRepeat;
import static com.qltech.bws.Utility.MusicService.getEndTime;
import static com.qltech.bws.Utility.MusicService.getProgressPercentage;
import static com.qltech.bws.Utility.MusicService.getStartTime;
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

public class PlayWellnessActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener/*,AudioManager.OnAudioFocusChangeListener*/ {
    ActivityPlayWellnessBinding binding;
    String IsRepeat = "", IsShuffle = "", UserID, PlaylistId = "", AudioFlag, id, name, url;
    int startTime = 0, endTime = 0, position, listSize;
    Context ctx;
    Activity activity;
    Boolean queuePlay, audioPlay;
    ArrayList<MainPlayModel> mainPlayModelList;
    ArrayList<AddToQueueModel> addToQueueModelList;
    private long mLastClickTime = 0, totalDuration, currentDuration;
    private Handler handler;
    //    private AudioManager mAudioManager;
    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
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

            int progress = (int) (getProgressPercentage(currentDuration, totalDuration));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_play_wellness);
        handler = new Handler();
        ctx = PlayWellnessActivity.this;
        activity = PlayWellnessActivity.this;
        addToQueueModelList = new ArrayList<>();
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
        getPrepareShowData(position);
        if (isMediaStart) {
            mediaPlayer.setOnCompletionListener(mediaPlayer -> {
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
                            position = 0;
                            getPrepareShowData(position);
                        } else {
                            Random random = new Random();
                            position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                            getPrepareShowData(position);
                        }
                    } else {
                        if (listSize == 1) {

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
                callRepeatShuffle();
            });
        }
        callRepeatShuffle();
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
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            finish();
        });

        binding.llPlay.setOnClickListener(v -> {
            binding.llPlay.setVisibility(View.GONE);
            binding.llPause.setVisibility(View.VISIBLE);
            if (!isMediaStart) {
                if (queuePlay) {
                    play(Uri.parse(addToQueueModelList.get(position).getAudioFile()));
                    playMedia();
                } else if (audioPlay) {
                    play(Uri.parse(mainPlayModelList.get(position).getAudioFile()));
                    playMedia();
                }
            } else {
                resumeMedia();
                isPause = false;
            }
            handler.postDelayed(UpdateSongTime, 60);
        });

        binding.llPause.setOnClickListener(view -> {
            handler.removeCallbacks(UpdateSongTime);
            binding.simpleSeekbar.setProgress(binding.simpleSeekbar.getProgress());
            pauseMedia();
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
                Random random = new Random();
                position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                getPrepareShowData(position);
            } else {
                if (position < listSize - 1) {
                    position = position + 1;
                    getPrepareShowData(position);
                } else if (listSize != 1) {
                    position = 0;
                    getPrepareShowData(position);
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
                Random random = new Random();
                position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                getPrepareShowData(position);
            } else {
                if (position > 0) {
                    position = position - 1;

                    getPrepareShowData(position);
                } else if (listSize != 1) {
                    position = listSize - 1;
                    getPrepareShowData(position);
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
         /*   if (listSize == 1) {
                binding.llRepeat.setClickable(false);
                binding.llRepeat.setEnabled(false);
                binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {*/
            binding.llRepeat.setClickable(true);
            binding.llRepeat.setEnabled(true);
            binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
//            }
        } else if (IsRepeat.equalsIgnoreCase("1")) {
            binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
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

        int progress = (int) (getProgressPercentage(currentDuration, totalDuration));
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
      /*  DownloadMedia downloadMedia = new DownloadMedia(ctx);
        downloadMedia.encrypt(url, name);*/

        if (BWSApplication.isNetworkConnected(ctx)) {
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
                            binding.llDownload.setClickable(true);
                            binding.llDownload.setEnabled(true);
                            binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
                        } else if (model.getResponseData().getFlag().equalsIgnoreCase("1")) {
                            binding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
                            binding.ivDownloads.setColorFilter(Color.argb(99, 99, 99, 99));
                            binding.ivDownloads.setAlpha(255);
                            binding.llDownload.setClickable(false);
                            binding.llDownload.setEnabled(false);
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
                ToRepeat(false);
                IsRepeat = "";
                binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
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
            editor.commit();
            ToRepeat(true);
            IsRepeat = "0";
            binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_one));
            BWSApplication.showToast("Repeat mode has been turned on", ctx);
            binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (IsRepeat.equalsIgnoreCase("0")) {
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "1");
            editor.commit();
            ToRepeat(false);
            IsRepeat = "1";
            binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
            BWSApplication.showToast("Repeat mode has been turned on", ctx);
            binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (IsRepeat.equalsIgnoreCase("1")) {
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "");
            editor.commit();
            ToRepeat(false);
            IsRepeat = "";
            binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
            BWSApplication.showToast("Repeat mode has been turned off", ctx);
            binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
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

    private void getPrepareShowData(int position) {
        handler.postDelayed(UpdateSongTime, 60);
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
            binding.tvName.setText(addToQueueModelList.get(position).getName());
            if (addToQueueModelList.get(position).getAudioDirection().equalsIgnoreCase("")) {
                binding.llDirection.setVisibility(View.GONE);
            } else {
                binding.llDirection.setVisibility(View.VISIBLE);
                binding.tvDireDesc.setText(addToQueueModelList.get(position).getAudioDirection());
            }
            binding.tvTitle.setText(addToQueueModelList.get(position).getAudiomastercat());
            binding.tvDesc.setText(addToQueueModelList.get(position).getAudioSubCategory());
            Glide.with(getApplicationContext()).load(addToQueueModelList.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
            if (addToQueueModelList.get(position).getLike().equalsIgnoreCase("1")) {
                binding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
            } else if (!addToQueueModelList.get(position).getLike().equalsIgnoreCase("0")) {
                binding.ivLike.setImageResource(R.drawable.ic_unlike_icon);
            }
            if (addToQueueModelList.get(position).getDownload().equalsIgnoreCase("1")) {
                binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                binding.ivDownloads.setColorFilter(Color.argb(99, 99, 99, 99));
                binding.ivDownloads.setAlpha(255);
                binding.llDownload.setClickable(false);
                binding.llDownload.setEnabled(false);
            } else if (!addToQueueModelList.get(position).getDownload().equalsIgnoreCase("")) {
                binding.llDownload.setClickable(true);
                binding.llDownload.setEnabled(true);
                binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
            }
            if (!isMediaStart) {
                play(Uri.parse(addToQueueModelList.get(position).getAudioFile()));
                playMedia();
                binding.llPause.setVisibility(View.VISIBLE);
                binding.llPlay.setVisibility(View.GONE);
            } else {
                if (isPause) {

                    binding.llPlay.setVisibility(View.VISIBLE);
                    binding.llPause.setVisibility(View.GONE);
                    binding.simpleSeekbar.setProgress(oTime);
                    int timeeee = progressToTimer(oTime, (int) (totalDuration));
                    binding.tvStartTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(timeeee),
                            TimeUnit.MILLISECONDS.toSeconds(timeeee) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeeee))));
//                    resumeMedia();
                } else if ((isPrepare || isMediaStart || isPlaying()) && !isPause) {
                    binding.llPause.setVisibility(View.VISIBLE);
                    binding.llPlay.setVisibility(View.GONE);
                } else {
                    binding.llPause.setVisibility(View.VISIBLE);
                    binding.llPlay.setVisibility(View.GONE);
                    play(Uri.parse(addToQueueModelList.get(position).getAudioFile()));
                    playMedia();
                }
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
            } else if (!mainPlayModelList.get(position).getLike().equalsIgnoreCase("0")) {
                binding.ivLike.setImageResource(R.drawable.ic_unlike_icon);
            }
            if (mainPlayModelList.get(position).getDownload().equalsIgnoreCase("1")) {
                binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                binding.ivDownloads.setColorFilter(Color.argb(99, 99, 99, 99));
                binding.ivDownloads.setAlpha(255);
                binding.llDownload.setClickable(false);
                binding.llDownload.setEnabled(false);
            } else if (!mainPlayModelList.get(position).getDownload().equalsIgnoreCase("")) {
                binding.llDownload.setClickable(true);
                binding.llDownload.setEnabled(true);
                binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
            }
            if (!isMediaStart) {
                play(Uri.parse(mainPlayModelList.get(position).getAudioFile()));
                playMedia();
                binding.llPause.setVisibility(View.VISIBLE);
                binding.llPlay.setVisibility(View.GONE);
            } else {
                if (isPause) {
                    binding.llPlay.setVisibility(View.VISIBLE);
                    binding.llPause.setVisibility(View.GONE);
                    binding.simpleSeekbar.setProgress(oTime);
//                    resumeMedia();
                    int timeeee = progressToTimer(oTime, (int) (totalDuration));
                    binding.tvStartTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(timeeee),
                            TimeUnit.MILLISECONDS.toSeconds(timeeee) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeeee))));
                } else if (((isPrepare || isMediaStart) || isPlaying()) && !isPause) {
                    binding.llPause.setVisibility(View.VISIBLE);
                    binding.llPlay.setVisibility(View.GONE);
                } else {
                    binding.llPause.setVisibility(View.VISIBLE);
                    binding.llPlay.setVisibility(View.GONE);
                    play(Uri.parse(mainPlayModelList.get(position).getAudioFile()));
                    playMedia();
                }
            }
            binding.tvSongTime.setText(mainPlayModelList.get(position).getAudioDuration());
            startTime = getStartTime();
        }
        addToRecentPlay();

        binding.simpleSeekbar.setClickable(true);
        handler.postDelayed(UpdateSongTime, 60);
        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
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
        queuePlay = shared.getBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
        audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        if(queuePlay){
            position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
            listSize = addToQueueModelList.size();
        }else if(audioPlay){
            position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
            listSize = mainPlayModelList.size();
        }
        if(listSize == 1){
            position = 0;
        }
        if ((isPrepare || isMediaStart || isPlaying()) && !isPause) {
            binding.llPlay.setVisibility(View.GONE);
            binding.llPause.setVisibility(View.VISIBLE);
        } else {
            binding.llPlay.setVisibility(View.VISIBLE);
            binding.llPause.setVisibility(View.GONE);
        }
        super.onResume();
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
        if (isMediaStart) {
            int totalDuration = getEndTime();
            int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);

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
}