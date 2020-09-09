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
import com.qltech.bws.Utility.MusicService;
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
import static com.qltech.bws.Utility.MusicService.isMediaStart;

public class PlayWellnessActivity extends AppCompatActivity implements
        SeekBar.OnSeekBarChangeListener {
    ActivityPlayWellnessBinding binding;
    String IsRepeat = "", IsShuffle = "", UserID, PlaylistId = "", AudioFlag, id;
    int oTime = 0, startTime = 0, endTime = 0, position, listSize;
    Context ctx;
    Activity activity;
    Boolean queuePlay, audioPlay;
    ArrayList<MainPlayModel> mainPlayModelList;
    ArrayList<AddToQueueModel> addToQueueModelList;
    private long mLastClickTime = 0;
    private Handler hdlr;
    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            Time t = Time.valueOf("00:00:00");
            String endtimetext = "";
            if (queuePlay) {
                if (listSize != 0) {
                    endtimetext = addToQueueModelList.get(position).getAudioDuration();
                    t = Time.valueOf("00:" + addToQueueModelList.get(position).getAudioDuration());
                    MusicService.stopMedia();
                }
            } else if (audioPlay) {
                endtimetext = mainPlayModelList.get(position).getAudioDuration();
                t = Time.valueOf("00:" + mainPlayModelList.get(position).getAudioDuration());
            }
            long totalDuration = t.getTime();
            long currentDuration = MusicService.getStartTime();

            int progress = (int) (MusicService.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            startTime = MusicService.getStartTime();
            binding.tvStartTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(startTime),
                    TimeUnit.MILLISECONDS.toSeconds(startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime))));

            if (currentDuration == totalDuration) {
                binding.tvStartTime.setText(endtimetext);
            }
            binding.simpleSeekbar.setProgress(progress);
            binding.simpleSeekbar.setMax(100);

            // Running this thread after 100 milliseconds
            hdlr.postDelayed(this, 60);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_play_wellness);
        hdlr = new Handler();
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
        getPrepareShowData(position);

        MusicService.mediaPlayer.setOnCompletionListener(mediaPlayer -> {
           /* if (queuePlay) {
                addToQueueModelList.remove(position);
                listSize = addToQueueModelList.size();
                if (position < listSize - 1) {
                    position = position + 1;
                } else {
                    if (listSize == 0) {
                        MusicService.stopMedia();
                    } else {
                        position = 0;
                    }
                }
            } else*/ if (IsRepeat.equalsIgnoreCase("1")) {
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
                if (listSize == 1) {
                } else {
                    Random random = new Random();
                    position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                    getPrepareShowData(position);
                }
            } else {
                if (position < (listSize - 1)) {
                    position = position + 1;
                    getPrepareShowData(position);
                } else {
                    binding.llPlay.setVisibility(View.VISIBLE);
                    binding.llPause.setVisibility(View.GONE);
                    MusicService.stopMedia();
                }
            }
            if (listSize == 1) {
                binding.llnext.setEnabled(false);
                binding.llprev.setEnabled(false);
                binding.llShuffle.setEnabled(false);
                binding.llnext.setClickable(false);
                binding.llprev.setClickable(false);
                binding.llShuffle.setClickable(false);
                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.ivnext.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.ivprev.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
                position = 0;
            } else if (position == listSize - 1 && IsRepeat.equalsIgnoreCase("1")) {
                binding.llnext.setEnabled(false);
                binding.llnext.setClickable(false);
                binding.ivnext.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else if (position == 0 && IsRepeat.equalsIgnoreCase("1")) {
                binding.llprev.setEnabled(false);
                binding.llprev.setClickable(false);
                binding.ivprev.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                binding.llnext.setEnabled(true);
                binding.llprev.setEnabled(true);
                binding.llShuffle.setEnabled(true);
                binding.llnext.setClickable(true);
                binding.llprev.setClickable(true);
                binding.llShuffle.setClickable(true);
                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.ivnext.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.ivprev.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        });

        binding.llBack.setOnClickListener(view -> {
            callBack();
        });

        binding.llLike.setOnClickListener(view -> {
            callLike();
        });

        addToRecentPlay();

        if (IsShuffle.equalsIgnoreCase("")) {
            binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (IsShuffle.equalsIgnoreCase("1")) {
            binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        if (queuePlay) {
            binding.llRepeat.setEnabled(false);
            binding.llRepeat.setClickable(false);
            binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            binding.llRepeat.setEnabled(true);
            binding.llRepeat.setClickable(true);
        }
        if (IsRepeat.equalsIgnoreCase("")) {
            binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (IsRepeat.equalsIgnoreCase("1")) {
            binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
        }


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
            finish();
        });

        binding.llViewQueue.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
//            MusicService.pauseMedia();
            if (binding.llPause.getVisibility() == View.VISIBLE) {
                MusicService.isPause = true;
            }
            Intent i = new Intent(ctx, ViewQueueActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            finish();
            SharedPreferences ViewQueue = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = ViewQueue.edit();
            editor.putInt(CONSTANTS.PREF_KEY_position, position);
            editor.commit();
        });

        binding.llPlay.setOnClickListener(v -> {
            binding.llPlay.setVisibility(View.GONE);
            binding.llPause.setVisibility(View.VISIBLE);
            if (!isMediaStart) {
                if (queuePlay) {
                    MusicService.play(ctx, Uri.parse(addToQueueModelList.get(position).getAudioFile()));
                    MusicService.playMedia();
                } else if (audioPlay) {
                    MusicService.play(ctx, Uri.parse(mainPlayModelList.get(position).getAudioFile()));
                    MusicService.playMedia();
                }
            } else
                MusicService.resumeMedia();
        });

        binding.llPause.setOnClickListener(view -> {
            hdlr.removeCallbacks(UpdateSongTime);
            binding.simpleSeekbar.setProgress(binding.simpleSeekbar.getProgress());
            MusicService.pauseMedia();
            binding.llPlay.setVisibility(View.VISIBLE);
            binding.llPause.setVisibility(View.GONE);
        });

        binding.llForwardSec.setOnClickListener(v -> {
            MusicService.ToForward(ctx);
            if (!binding.llPlay.isEnabled()) {
                binding.llPlay.setEnabled(true);
            }
        });

        binding.llBackWordSec.setOnClickListener(v -> {
            MusicService.ToBackward(ctx);
            if (!binding.llPlay.isEnabled()) {
                binding.llPlay.setEnabled(true);
            }
        });

        binding.llnext.setOnClickListener(view -> {
            MusicService.stopMedia();
            MusicService.isPause = false;
            if (IsRepeat.equalsIgnoreCase("1")) {
                // repeat is on play same song again
                if (position < listSize - 1) {
                    position = position + 1;
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
                }else if(listSize != 1){
                    position = 0;
                    getPrepareShowData(position);
                }
            }
        });

        binding.llprev.setOnClickListener(view -> {
            MusicService.stopMedia();
            MusicService.isPause = false;
            if (IsRepeat.equalsIgnoreCase("1")) {
                // repeat is on play same song again
                if (position > 0) {
                    position = position - 1;
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
                }else if(listSize != 1){
                    position = listSize - 1;
                    getPrepareShowData(position);
                }
            }
        });
    }

    private void callDownload() {
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

            } else {
                IsShuffle = "1";
                SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putString(CONSTANTS.PREF_KEY_IsShuffle, "1");
                if (IsRepeat.equalsIgnoreCase("0")) {
                    editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "");
                }
                editor.commit();
                MusicService.ToRepeat(false);
                IsRepeat = "";
                binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
                BWSApplication.showToast("Shuffle mode has been turned on",ctx);
            }
        } else if (IsShuffle.equalsIgnoreCase("1")) {
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(CONSTANTS.PREF_KEY_IsShuffle, "");
            editor.commit();
            IsShuffle = "";
            binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    private void callRepeat() {

        if (IsRepeat.equalsIgnoreCase("")) {
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "0");
            editor.commit();
            MusicService.ToRepeat(true);
            IsRepeat = "0";
            binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_one));
            binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (IsRepeat.equalsIgnoreCase("0")) {
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "1");
            editor.commit();
            MusicService.ToRepeat(false);
            IsRepeat = "1";
            binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
            binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (IsRepeat.equalsIgnoreCase("1")) {
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "");
            editor.commit();
            MusicService.ToRepeat(false);
            IsRepeat = "";
            binding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
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
        if (listSize == 1) {
            binding.llnext.setEnabled(false);
            binding.llprev.setEnabled(false);
            binding.llShuffle.setEnabled(false);
            binding.llnext.setClickable(false);
            binding.llprev.setClickable(false);
            binding.llShuffle.setClickable(false);
            SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
            SharedPreferences.Editor editor1 = shared1.edit();
            editor1.putString(CONSTANTS.PREF_KEY_IsShuffle, "");
            editor1.commit();
            binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.ivnext.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.ivprev.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            position = 0;
        } else if (position == listSize - 1 && IsRepeat.equalsIgnoreCase("1")) {
            binding.llnext.setEnabled(false);
            binding.llnext.setClickable(false);
            binding.ivnext.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (position == 0 && IsRepeat.equalsIgnoreCase("1")) {
            binding.llprev.setEnabled(false);
            binding.llprev.setClickable(false);
            binding.ivprev.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            binding.llnext.setEnabled(true);
            binding.llprev.setEnabled(true);
            binding.llShuffle.setEnabled(true);
            binding.llnext.setClickable(true);
            binding.llprev.setClickable(true);
            binding.llShuffle.setClickable(true);
            binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
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
            binding.tvName.setText(addToQueueModelList.get(position).getName());
            if (addToQueueModelList.get(position).getAudioDirection().equalsIgnoreCase("")) {
                binding.llDirection.setVisibility(View.GONE);
            } else {
                binding.llDirection.setVisibility(View.VISIBLE);
                binding.tvDireDesc.setText(addToQueueModelList.get(position).getAudioDirection());
            }
            binding.tvTitle.setText(addToQueueModelList.get(position).getAudioSubCategory());
            binding.tvDesc.setText(addToQueueModelList.get(position).getAudiomastercat());
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
                MusicService.play(ctx, Uri.parse(addToQueueModelList.get(position).getAudioFile()));
                MusicService.playMedia();
            } else {
                if (MusicService.isPause) {

                    binding.llPlay.setVisibility(View.VISIBLE);
                    binding.llPause.setVisibility(View.GONE);
//                    MusicService.resumeMedia();
                } else if (MusicService.isPlaying()) {
                    binding.llPause.setVisibility(View.VISIBLE);
                    binding.llPlay.setVisibility(View.GONE);
                } else {
                    binding.llPause.setVisibility(View.VISIBLE);
                    binding.llPlay.setVisibility(View.GONE);
                    MusicService.play(ctx, Uri.parse(addToQueueModelList.get(position).getAudioFile()));
                    MusicService.playMedia();
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
            startTime = MusicService.getStartTime();

        } else if (audioPlay) {
            listSize = mainPlayModelList.size();
            id = mainPlayModelList.get(position).getID();
            binding.tvName.setText(mainPlayModelList.get(position).getName());
            if (mainPlayModelList.get(position).getAudioDirection().equalsIgnoreCase("")) {
                binding.llDirection.setVisibility(View.GONE);
            } else {
                binding.llDirection.setVisibility(View.VISIBLE);
                binding.tvDireDesc.setText(mainPlayModelList.get(position).getAudioDirection());
            }
            binding.tvTitle.setText(mainPlayModelList.get(position).getAudioSubCategory());
            binding.tvDesc.setText(mainPlayModelList.get(position).getAudiomastercat());
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
                MusicService.play(getApplicationContext(), Uri.parse(mainPlayModelList.get(position).getAudioFile()));
                MusicService.playMedia();
            } else {
                if (MusicService.isPause) {
                    binding.llPlay.setVisibility(View.VISIBLE);
                    binding.llPause.setVisibility(View.GONE);
//                    MusicService.resumeMedia();
                } else {
                    binding.llPause.setVisibility(View.VISIBLE);
                    binding.llPlay.setVisibility(View.GONE);
                    MusicService.play(ctx, Uri.parse(mainPlayModelList.get(position).getAudioFile()));
                    MusicService.playMedia();
                }
            }
            binding.tvSongTime.setText(mainPlayModelList.get(position).getAudioDuration());
            startTime = MusicService.getStartTime();
        }

        binding.simpleSeekbar.setClickable(true);
        hdlr.postDelayed(UpdateSongTime, 60);
        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
    }

    @Override
    public void onBackPressed() {
        callBack();
    }

    private void callBack() {
        player = 1;
        if (binding.llPause.getVisibility() == View.VISIBLE) {
            MusicService.isPause = true;
        }
//        MusicService.pauseMedia();
        SharedPreferences shared2 = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared2.edit();
        editor.putInt(CONSTANTS.PREF_KEY_position, position);
        editor.commit();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        MusicService.releasePlayer();
    }

    @Override
    protected void onResume() {
        if (isMediaStart) {
            if (MusicService.isPlaying()) {
                binding.llPlay.setVisibility(View.GONE);
                binding.llPause.setVisibility(View.VISIBLE);
            }else{
                binding.llPlay.setVisibility(View.VISIBLE);
                binding.llPause.setVisibility(View.GONE);
            }
        }
        super.onResume();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        hdlr.removeCallbacks(UpdateSongTime);
    }

    public void updateProgressBar() {
        hdlr.postDelayed(UpdateSongTime, 100);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        hdlr.removeCallbacks(UpdateSongTime);
        int totalDuration = MusicService.getEndTime();
        int currentPosition = MusicService.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        MusicService.SeekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}