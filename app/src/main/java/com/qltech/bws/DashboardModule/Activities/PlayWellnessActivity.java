package com.qltech.bws.DashboardModule.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Models.AudioLikeModel;
import com.qltech.bws.DashboardModule.Models.DownloadPlaylistModel;
import com.qltech.bws.DashboardModule.Models.MainAudioModel;
import com.qltech.bws.DashboardModule.Models.SucessModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.ActivityPlayWellnessBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayWellnessActivity extends AppCompatActivity {
    ActivityPlayWellnessBinding binding;
    String IsRepeat = "", IsShuffle = "", Like, Download, UserID, AudioFile, Name, ImageFile, PlaylistId,
            AudioId, AudioDirection, Audiomastercat, AudioSubCategory;
    private static int oTime = 0, startTime = 0, endTime = 0, forwardTime = 30000, backwardTime = 30000;
    private MediaPlayer mPlayer;
    Context ctx;
    Activity activity;
    private ArrayList<MainAudioModel.ResponseData.Detail> listModelList;
    int position;
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
        listModelList = new ArrayList<>();
        if (getIntent().getExtras() != null) {
            AudioId = getIntent().getStringExtra(CONSTANTS.ID);
            Name = getIntent().getStringExtra(CONSTANTS.Name);
            AudioFile = getIntent().getStringExtra(CONSTANTS.AudioFile);
            ImageFile = getIntent().getStringExtra(CONSTANTS.ImageFile);
            AudioDirection = getIntent().getStringExtra(CONSTANTS.AudioDirection);
            Audiomastercat = getIntent().getStringExtra(CONSTANTS.Audiomastercat);
            AudioSubCategory = getIntent().getStringExtra(CONSTANTS.AudioSubCategory);
            Like = getIntent().getStringExtra(CONSTANTS.Like);
            Download = getIntent().getStringExtra(CONSTANTS.Download);
            position = getIntent().getIntExtra(CONSTANTS.position, 0);
            listModelList = getIntent().getParcelableArrayListExtra(CONSTANTS.AudioList);
        }
        prepareData();
        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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
        mPlayer.setOnCompletionListener(mediaPlayer -> {
            if (position < listModelList.size() - 1) {
                position = position + 1;
                prepareData();
            }
        });

        binding.llplay.setOnClickListener(v -> {
            mPlayer.start();
            binding.llplay.setVisibility(View.GONE);
            binding.llPause.setVisibility(View.VISIBLE);
            binding.llPause.setEnabled(true);
            binding.llplay.setEnabled(false);
        });

        binding.llPause.setOnClickListener(view -> {
            binding.llplay.setVisibility(View.VISIBLE);
            binding.llPause.setVisibility(View.GONE);
            mPlayer.pause();
            binding.llPause.setEnabled(false);
            binding.llplay.setEnabled(true);
        });

        binding.llForwardSec.setOnClickListener(v -> {
            if ((startTime + forwardTime) <= endTime) {
                startTime = startTime + forwardTime;
                mPlayer.seekTo(startTime);
            } else {
                showToast("Please wait");
            }
            if (!binding.llplay.isEnabled()) {
                binding.llplay.setEnabled(true);
            }
        });

        binding.llBackWordSec.setOnClickListener(v -> {
            if ((startTime - backwardTime) > 0) {
                startTime = startTime - backwardTime;
                mPlayer.seekTo(startTime);
            } else {
                showToast("Please wait");
            }
            if (!binding.llplay.isEnabled()) {
                binding.llplay.setEnabled(true);
            }
        });
        binding.llnext.setOnClickListener(view -> {
            if (position < listModelList.size() - 1) {
                mPlayer.release();
                position = position + 1;
                prepareData();
            }

        });

        binding.llprev.setOnClickListener(view -> {
            if (position > 0) {
                mPlayer.pause();
                position = position - 1;
                prepareData();
            } else {
                mPlayer.pause();
                position = 0;
                prepareData();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareData();
    }

    void prepareData() {
        binding.tvName.setText(listModelList.get(position).getName());
        binding.tvDireDesc.setText(listModelList.get(position).getAudioDirection());
        binding.tvTitle.setText(listModelList.get(position).getAudioSubCategory());
        binding.tvDesc.setText(listModelList.get(position).getAudiomastercat());
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mPlayer.setDataSource(ctx, Uri.parse(listModelList.get(position).getAudioFile()));
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*URL url = null;
        try {
            url = new URL(AudioFile);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            String encodedUrl = uri.toASCIIString();
            mPlayer.setDataSource(ctx, Uri.parse(encodedUrl));
            mPlayer.prepare();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        binding.simpleSeekbar.setClickable(false);
        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                1, 1, 1f, 30);
        binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(ctx).load(ImageFile).thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
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

        showToast("Added to your queue");
        mPlayer.start();
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
}