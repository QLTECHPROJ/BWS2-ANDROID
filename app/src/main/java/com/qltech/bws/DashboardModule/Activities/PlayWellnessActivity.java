package com.qltech.bws.DashboardModule.Activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Models.AppointmentDetailModel;
import com.qltech.bws.DashboardModule.Models.AudioLikeModel;
import com.qltech.bws.DashboardModule.Models.DownloadPlaylistModel;
import com.qltech.bws.DashboardModule.Models.MainAudioModel;
import com.qltech.bws.DashboardModule.Models.SubPlayListModel;
import com.qltech.bws.DashboardModule.Models.SucessModel;
import com.qltech.bws.DashboardModule.Models.ViewAllAudioListModel;
import com.qltech.bws.DownloadModule.Models.DownloadlistModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.Utility.MusicService;
import com.qltech.bws.databinding.ActivityPlayWellnessBinding;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayWellnessActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    ActivityPlayWellnessBinding binding;
    String IsRepeat = "", IsShuffle = "", Like, Download, UserID, ImageFile, PlaylistId,
            AudioId, AudioFlag;
    private static int oTime = 0, startTime = 0, endTime = 0, forwardTime = 30000, backwardTime = 30000;
    private MediaPlayer mPlayer;
    Context ctx;
    Activity activity;
    ArrayList<MainAudioModel.ResponseData.Detail> mainAudioList;
    ArrayList<ViewAllAudioListModel.ResponseData.Detail> ViewAllAudioList;
    ArrayList<AppointmentDetailModel.Audio> AppointmentDetailList;
    ArrayList<DownloadlistModel.Audio> Downloadlist;
    ArrayList<SubPlayListModel.ResponseData.PlaylistSong> SubPlayList;
    int position;
    boolean isPrepere;
    private Handler hdlr = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_play_wellness);
        ctx = PlayWellnessActivity.this;
        activity = PlayWellnessActivity.this;
        Glide.with(ctx).load(R.drawable.loading).asGif().into(binding.ImgV);
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        IsRepeat = (shared1.getString(CONSTANTS.PREF_KEY_IsRepeat, ""));
        IsShuffle = (shared1.getString(CONSTANTS.PREF_KEY_IsShuffle, ""));

        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson));
        position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");

        if (AudioFlag.equalsIgnoreCase("MainAudioList")) {
            Type type = new TypeToken<ArrayList<MainAudioModel.ResponseData.Detail>>() {
            }.getType();
            ArrayList<MainAudioModel.ResponseData.Detail> arrayList = gson.fromJson(json, type);
            getPrepareShowData(arrayList.get(position).getName(),
                    arrayList.get(position).getAudioDirection(),
                    arrayList.get(position).getAudioSubCategory(),
                    arrayList.get(position).getAudiomastercat(),
                    arrayList.get(position).getImageFile(),
                    arrayList.get(position).getLike(),
                    arrayList.get(position).getDownload(),
                    arrayList.get(position).getAudioFile());
        } else if (AudioFlag.equalsIgnoreCase("ViewAllAudioList")) {
            Type type = new TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail>>() {
            }.getType();
            ArrayList<ViewAllAudioListModel.ResponseData.Detail> arrayList = gson.fromJson(json, type);
            getPrepareShowData(arrayList.get(position).getName(),
                    arrayList.get(position).getAudioDirection(),
                    arrayList.get(position).getAudioSubCategory(),
                    arrayList.get(position).getAudiomastercat(),
                    arrayList.get(position).getImageFile(),
                    arrayList.get(position).getLike(),
                    arrayList.get(position).getDownload(),
                    arrayList.get(position).getAudioFile());
        } else if (AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
            Type type = new TypeToken<ArrayList<AppointmentDetailModel.Audio>>() {
            }.getType();
            ArrayList<AppointmentDetailModel.Audio> arrayList = gson.fromJson(json, type);
            getPrepareShowData(arrayList.get(position).getName(),
                    arrayList.get(position).getAudioDirection(),
                    arrayList.get(position).getAudioSubCategory(),
                    arrayList.get(position).getAudiomastercat(),
                    arrayList.get(position).getImageFile(),
                    arrayList.get(position).getLike(),
                    arrayList.get(position).getDownload(),
                    arrayList.get(position).getAudioFile());
        } else if (AudioFlag.equalsIgnoreCase("Downloadlist")) {
            Type type = new TypeToken<ArrayList<DownloadlistModel.Audio>>() {
            }.getType();
            ArrayList<DownloadlistModel.Audio> arrayList = gson.fromJson(json, type);
            getPrepareShowData(arrayList.get(position).getName(),
                    arrayList.get(position).getAudioDirection(),
                    arrayList.get(position).getAudioSubCategory(),
                    arrayList.get(position).getAudiomastercat(),
                    arrayList.get(position).getImageFile(),
                    arrayList.get(position).getLike(),
                    arrayList.get(position).getDownload(),
                    arrayList.get(position).getAudioFile());
        } else if (AudioFlag.equalsIgnoreCase("SubPlayList")) {
            Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
            }.getType();
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);
            getPrepareShowData(arrayList.get(position).getName(),
                    arrayList.get(position).getAudioDirection(),
                    arrayList.get(position).getAudioSubCategory(),
                    arrayList.get(position).getAudiomastercat(),
                    arrayList.get(position).getImageFile(),
                    arrayList.get(position).getLike(),
                    arrayList.get(position).getDownload(),
                    arrayList.get(position).getAudioFile());
        }


        prepareData();
        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //callStateListener();

        binding.llLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BWSApplication.isNetworkConnected(ctx)) {
                    BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                    Call<AudioLikeModel> listCall = APIClient.getClient().getAudioLike(AudioId, UserID);
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
            Call<SucessModel> listCall = APIClient.getClient().getRecentlyplayed(AudioId, UserID);
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

        binding.llDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BWSApplication.isNetworkConnected(ctx)) {
                    BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
                    Call<DownloadPlaylistModel> listCall = APIClient.getClient().getDownloadlistPlaylist(UserID, AudioId, PlaylistId);
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
            i.putExtra("ID", AudioId);
            startActivity(i);
        });

        binding.llViewQueue.setOnClickListener(view -> {
            Intent i = new Intent(ctx, ViewQueueActivity.class);
            startActivity(i);
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
            if (position < mainAudioList.size() - 1) {
                MusicService.stopMedia();
                position = position + 1;
                prepareData();
            }

        });

        binding.llprev.setOnClickListener(view -> {
            if (position > 0) {
                MusicService.pauseMedia();
                position = position - 1;
                prepareData();
            } else {
                MusicService.pauseMedia();
                position = 0;
                prepareData();
            }
        });
    }

    private void getPrepareShowData(String name, String AudioDirection, String AudioSubCategory, String Audiomastercat,
                                    String ImageFile, String Like, String Download, String AudioFile) {
        binding.tvName.setText(name);
        binding.tvDireDesc.setText(AudioDirection);
        binding.tvTitle.setText(AudioSubCategory);
        binding.tvDesc.setText(Audiomastercat);
        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                1, 1, 1f, 30);
        binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(ctx).load(ImageFile).thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);

        if (Like.equalsIgnoreCase("1")) {
            binding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
        } else if (!Like.equalsIgnoreCase("0")) {
            binding.ivLike.setImageResource(R.drawable.ic_unlike_icon);
        }

        if (Download.equalsIgnoreCase("1")) {
            binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
            binding.ivDownloads.setColorFilter(Color.argb(99, 99, 99, 99));
            binding.ivDownloads.setAlpha(255);
            binding.llDownload.setClickable(false);
            binding.llDownload.setEnabled(false);
        } else if (!Download.equalsIgnoreCase("")) {
            binding.llDownload.setClickable(true);
            binding.llDownload.setEnabled(true);
            binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
        }
        MusicService.play(ctx, Uri.parse(AudioFile));
        MusicService.playMedia();
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

    void prepareData() {
        BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
        binding.simpleSeekbar.setClickable(false);

        MusicService.ToSeek(endTime, startTime);
        endTime = mPlayer.getDuration();
        startTime = mPlayer.getCurrentPosition();
        if (oTime == 0) {
            binding.simpleSeekbar.setMax(endTime);
            oTime = 1;
        }

        binding.tvSongTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(endTime),
                TimeUnit.MILLISECONDS.toSeconds(endTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(endTime))));
        binding.tvStartTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(startTime),
                TimeUnit.MILLISECONDS.toSeconds(startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime))));
        binding.simpleSeekbar.setProgress(startTime);
        hdlr.postDelayed(UpdateSongTime, 100);
    }


    void showToast(String message) {
        Toast toast = new Toast(ctx);
        View view = LayoutInflater.from(ctx).inflate(R.layout.toast_layout, null);
        TextView tvMessage = view.findViewById(R.id.tvMessage);
        tvMessage.setText(message);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 35);
        toast.setView(view);
        toast.show();
    }

    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            startTime = mPlayer.getCurrentPosition();
            binding.tvStartTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(startTime),
                    TimeUnit.MILLISECONDS.toSeconds(startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime))));
            binding.simpleSeekbar.setProgress(startTime);
            hdlr.postDelayed(this, 60);
        }
    };

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (position < mainAudioList.size() - 1) {
            position = position + 1;
            prepareData();
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