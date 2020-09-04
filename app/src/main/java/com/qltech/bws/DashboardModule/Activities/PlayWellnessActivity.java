package com.qltech.bws.DashboardModule.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qltech.bws.BWSApplication;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayWellnessActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    ActivityPlayWellnessBinding binding;
    String IsRepeat = "", IsShuffle = "", UserID, PlaylistId = "", AudioFlag;
    int oTime = 0, startTime = 0, endTime = 0, position, listSize;
    Context ctx;
    Activity activity;
    private Handler hdlr;
    ArrayList<MainPlayModel> mainPlayModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_play_wellness);
        hdlr = new Handler();
        ctx = PlayWellnessActivity.this;
        activity = PlayWellnessActivity.this;
        mainPlayModelList = new ArrayList<>();
        Glide.with(ctx).load(R.drawable.loading).asGif().into(binding.ImgV);
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences Status = getSharedPreferences(CONSTANTS.PREF_KEY_Status, Context.MODE_PRIVATE);
        IsRepeat = Status.getString(CONSTANTS.PREF_KEY_IsRepeat, "");
        IsShuffle = Status.getString(CONSTANTS.PREF_KEY_IsShuffle, "");

        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = shared.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gson));
        position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
        Type type = new TypeToken<ArrayList<MainPlayModel>>() {
        }.getType();
        mainPlayModelList = gson.fromJson(json, type);
        listSize = mainPlayModelList.size();
        getPrepareShowData();

        /*if (AudioFlag.equalsIgnoreCase("MainAudioList")) {
            Type type = new TypeToken<ArrayList<MainAudioModel.ResponseData.Detail>>() {
            }.getType();
            ArrayList<MainAudioModel.ResponseData.Detail> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {
                mainPlayModel.setID(arrayList.get(position).getID());
                mainPlayModel.setName(arrayList.get(position).getName());
                mainPlayModel.setAudioFile(arrayList.get(position).getAudioFile());
                mainPlayModel.setAudioDirection(arrayList.get(position).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(position).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(position).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(position).getImageFile());
                mainPlayModel.setLike(arrayList.get(position).getLike());
                mainPlayModel.setDownload(arrayList.get(position).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(position).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            getPrepareShowData();
        } else if (AudioFlag.equalsIgnoreCase("ViewAllAudioList")) {
            Type type = new TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail>>() {
            }.getType();
            ArrayList<ViewAllAudioListModel.ResponseData.Detail> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {
                mainPlayModel.setID(arrayList.get(position).getID());
                mainPlayModel.setName(arrayList.get(position).getName());
                mainPlayModel.setAudioFile(arrayList.get(position).getAudioFile());
                mainPlayModel.setAudioDirection(arrayList.get(position).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(position).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(position).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(position).getImageFile());
                mainPlayModel.setLike(arrayList.get(position).getLike());
                mainPlayModel.setDownload(arrayList.get(position).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(position).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            getPrepareShowData();
        } else if (AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
            Type type = new TypeToken<ArrayList<AppointmentDetailModel.Audio>>() {
            }.getType();
            ArrayList<AppointmentDetailModel.Audio> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {
                mainPlayModel.setID(arrayList.get(position).getID());
                mainPlayModel.setName(arrayList.get(position).getName());
                mainPlayModel.setAudioFile(arrayList.get(position).getAudioFile());
                mainPlayModel.setAudioDirection(arrayList.get(position).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(position).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(position).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(position).getImageFile());
                mainPlayModel.setLike(arrayList.get(position).getLike());
                mainPlayModel.setDownload(arrayList.get(position).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(position).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            getPrepareShowData();
        } else if (AudioFlag.equalsIgnoreCase("Downloadlist")) {
            Type type = new TypeToken<ArrayList<DownloadlistModel.Audio>>() {
            }.getType();
            ArrayList<DownloadlistModel.Audio> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {
                mainPlayModel.setID(arrayList.get(position).getAudioID());
                mainPlayModel.setName(arrayList.get(position).getName());
                mainPlayModel.setAudioFile(arrayList.get(position).getAudioFile());
                mainPlayModel.setAudioDirection(arrayList.get(position).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(position).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(position).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(position).getImageFile());
                mainPlayModel.setLike(arrayList.get(position).getLike());
                mainPlayModel.setDownload(arrayList.get(position).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(position).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            getPrepareShowData();
        } else if (AudioFlag.equalsIgnoreCase("SubPlayList")) {
            Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
            }.getType();
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {
                mainPlayModel.setID(arrayList.get(position).getID());
                mainPlayModel.setName(arrayList.get(position).getName());
                mainPlayModel.setAudioFile(arrayList.get(position).getAudioFile());
                mainPlayModel.setAudioDirection(arrayList.get(position).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(position).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(position).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(position).getImageFile());
                mainPlayModel.setLike(arrayList.get(position).getLike());
                mainPlayModel.setDownload(arrayList.get(position).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(position).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            getPrepareShowData();
        }*/

        binding.llBack.setOnClickListener(view -> {
            MusicService.pauseMedia();
            SharedPreferences shared2 = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared2.edit();
            editor.putInt(CONSTANTS.PREF_KEY_position, position);
            editor.commit();
            finish();
        });

        //callStateListener();

        binding.llLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BWSApplication.isNetworkConnected(ctx)) {
                    BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                    Call<AudioLikeModel> listCall = APIClient.getClient().getAudioLike(mainPlayModelList.get(position).getID(), UserID);
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
                                Toast.makeText(ctx, model.getResponseMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<AudioLikeModel> call, Throwable t) {
                            BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
            Call<SucessModel> listCall = APIClient.getClient().getRecentlyplayed(mainPlayModelList.get(position).getID(), UserID);
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
            Toast.makeText(getApplicationContext(), getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
        }

        if (IsShuffle.equalsIgnoreCase("")) {
            binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (IsShuffle.equalsIgnoreCase("1")) {
            binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        if (IsRepeat.equalsIgnoreCase("")) {
            binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (IsRepeat.equalsIgnoreCase("1")) {
            binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        binding.llRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IsRepeat.equalsIgnoreCase("")) {
                    SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "1");
                    editor.commit();
                    MusicService.ToRepeat(true);
                    binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
                } else if (IsRepeat.equalsIgnoreCase("1")) {
                    SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "");
                    editor.commit();
                    MusicService.ToRepeat(false);
                    binding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                }
            }
        });

        binding.llShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IsShuffle.equalsIgnoreCase("")) {
                    SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putString(CONSTANTS.PREF_KEY_IsShuffle, "1");
                    editor.commit();
                    binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
                    Collections.shuffle(mainPlayModelList, new Random(System.nanoTime()));
                } else if (IsShuffle.equalsIgnoreCase("1")) {
                    SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putString(CONSTANTS.PREF_KEY_IsShuffle, "");
                    editor.commit();
                    binding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                }
            }
        });

        binding.llDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BWSApplication.isNetworkConnected(ctx)) {
                    BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                    Call<DownloadPlaylistModel> listCall = APIClient.getClient().getDownloadlistPlaylist(UserID, mainPlayModelList.get(position).getID(), PlaylistId);
                    listCall.enqueue(new Callback<DownloadPlaylistModel>() {
                        @Override
                        public void onResponse(Call<DownloadPlaylistModel> call, Response<DownloadPlaylistModel> response) {
                            if (response.isSuccessful()) {
                                BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                                DownloadPlaylistModel model = response.body();
                                Toast.makeText(ctx, model.getResponseMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<DownloadPlaylistModel> call, Throwable t) {
                            BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.llMore.setOnClickListener(view -> {
            Intent i = new Intent(ctx, AddQueueActivity.class);
            i.putExtra("play", "play");
            i.putExtra("ID", mainPlayModelList.get(position).getID());
            startActivity(i);
        });

        binding.llViewQueue.setOnClickListener(view -> {
            Intent i = new Intent(ctx, ViewQueueActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            SharedPreferences ViewQueue = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = ViewQueue.edit();
            editor.putInt(CONSTANTS.PREF_KEY_position, position);
            editor.commit();
        });

//        mPlayer.setOnCompletionListener(mediaPlayer -> {
//            if (position < mainAudioList.size() - 1) {
//                position = position + 1;
//                prepareData();
//            }
//        });

        binding.llplay.setOnClickListener(v -> {
            binding.llplay.setVisibility(View.GONE);
            binding.llPause.setVisibility(View.VISIBLE);
            MusicService.stopMedia();
        });

        binding.llPause.setOnClickListener(view -> {
            binding.llplay.setVisibility(View.VISIBLE);
            binding.llPause.setVisibility(View.GONE);
            MusicService.pauseMedia();
        });

        binding.llForwardSec.setOnClickListener(v -> {
            MusicService.ToForward(ctx);
            if (!binding.llplay.isEnabled()) {
                binding.llplay.setEnabled(true);
            }
        });

        binding.llBackWordSec.setOnClickListener(v -> {
            MusicService.ToBackward(ctx);
            if (!binding.llplay.isEnabled()) {
                binding.llplay.setEnabled(true);
            }
        });

        binding.llnext.setOnClickListener(view -> {
            if (position < listSize - 1) {
                MusicService.pauseMedia();
                position = position + 1;
                getPrepareShowData();
            } else {
                position = 0;
                getPrepareShowData();
            }

        });

        binding.llprev.setOnClickListener(view -> {
            if (position > 0) {
                MusicService.pauseMedia();
                position = position - 1;
                getPrepareShowData();
            } else {
                MusicService.pauseMedia();
                position = 0;
                getPrepareShowData();
            }
        });
    }

    private void getPrepareShowData() {
        BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
        binding.tvName.setText(mainPlayModelList.get(position).getName());
        binding.tvDireDesc.setText(mainPlayModelList.get(position).getAudioDirection());
        binding.tvTitle.setText(mainPlayModelList.get(position).getAudioSubCategory());
        binding.tvDesc.setText(mainPlayModelList.get(position).getAudiomastercat());
        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                1, 1, 1f, 30);
        binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(ctx).load(mainPlayModelList.get(position).getImageFile()).thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
        binding.simpleSeekbar.setOnSeekBarChangeListener(this); // Important

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

        if (MusicService.isPause) {
            MusicService.resumeMedia();
        } else {
            MusicService.play(ctx, Uri.parse(mainPlayModelList.get(position).getAudioFile()));
            MusicService.playMedia();
        }
        binding.simpleSeekbar.setClickable(false);

        startTime = MusicService.getStartTime();
        if (oTime == 0) {
            binding.simpleSeekbar.setMax(endTime);
            oTime = 1;
        }
        binding.tvSongTime.setText(mainPlayModelList.get(position).getAudioDuration());
        binding.tvStartTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(startTime),
                TimeUnit.MILLISECONDS.toSeconds(startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime))));
        binding.simpleSeekbar.setProgress(startTime);
        hdlr.postDelayed(UpdateSongTime, 100);
        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicService.releasePlayer();
    }

    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            startTime = MusicService.getStartTime();
            binding.tvStartTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(startTime),
                    TimeUnit.MILLISECONDS.toSeconds(startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime))));
            binding.simpleSeekbar.setProgress(startTime);
            hdlr.postDelayed(this, 60);
        }
    };

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (position < listSize - 1) {
            position = position + 1;
            getPrepareShowData();
        } else {
            position = 0;
            getPrepareShowData();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}