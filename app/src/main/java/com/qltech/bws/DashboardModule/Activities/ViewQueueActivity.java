package com.qltech.bws.DashboardModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qltech.bws.DashboardModule.Adapters.QueueAdapter;
import com.qltech.bws.DashboardModule.Models.AddToQueueModel;
import com.qltech.bws.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.ItemMoveCallback;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.Utility.MusicService;
import com.qltech.bws.databinding.ActivityViewQueueBinding;

import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ViewQueueActivity extends AppCompatActivity {
    ActivityViewQueueBinding binding;
    int position, listSize, startTime = 0;
    String IsRepeat, IsShuffle;
    Context ctx;
    Activity activity;
    ArrayList<MainPlayModel> mainPlayModelList;
    ArrayList<AddToQueueModel> addToQueueModelList;
    SharedPreferences shared;

    private Handler hdlr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_queue);
        ctx = ViewQueueActivity.this;
        activity = ViewQueueActivity.this;
        hdlr = new Handler();
        addToQueueModelList = new ArrayList<>();

        mainPlayModelList = new ArrayList<>();
        shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = shared.getString(CONSTANTS.PREF_KEY_queueList, String.valueOf(gson));
        position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
        if (!json.equalsIgnoreCase(String.valueOf(gson))) {
            Type type = new TypeToken<ArrayList<AddToQueueModel>>() {
            }.getType();
            addToQueueModelList = gson.fromJson(json, type);
        }
        String json2 = shared.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gson));
        position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
        Type type2 = new TypeToken<ArrayList<MainPlayModel>>() {
        }.getType();
        mainPlayModelList = gson.fromJson(json2, type2);
        listSize = mainPlayModelList.size();

        SharedPreferences Status = getSharedPreferences(CONSTANTS.PREF_KEY_Status, Context.MODE_PRIVATE);
        IsRepeat = Status.getString(CONSTANTS.PREF_KEY_IsRepeat, "");
        IsShuffle = Status.getString(CONSTANTS.PREF_KEY_IsShuffle, "");

        binding.rvQueueList.setFocusable(false);
        binding.nestedScroll.requestFocus();

        binding.llBack.setOnClickListener(view -> {
            Intent i = new Intent(ctx, PlayWellnessActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            finish();
        });
        MeasureRatio measureRatio = BWSApplication.measureRatio(ViewQueueActivity.this, 0,
                1, 1, 0.1f, 0);
        binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        getPrepareShowData(position);
        binding.simpleSeekbar.setClickable(false);

        if (addToQueueModelList.size() != 0) {
            QueueAdapter adapter = new QueueAdapter(addToQueueModelList, ViewQueueActivity.this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ViewQueueActivity.this);
            binding.rvQueueList.setLayoutManager(mLayoutManager);
            binding.rvQueueList.setItemAnimator(new DefaultItemAnimator());
            ItemTouchHelper.Callback callback =
                    new ItemMoveCallback(adapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(binding.rvQueueList);
            binding.rvQueueList.setAdapter(adapter);
        }
        binding.llPause.setOnClickListener(view -> {
            binding.llPause.setVisibility(View.GONE);
            binding.llPlay.setVisibility(View.VISIBLE);
            binding.ivPause.setImageResource(R.drawable.ic_play_white_icon);
            MusicService.pauseMedia();
        });

        binding.llPlay.setOnClickListener(view -> {
            binding.llPlay.setVisibility(View.GONE);
            binding.llPause.setVisibility(View.VISIBLE);
            binding.ivPlay.setImageResource(R.drawable.ic_pause_icon);
            MusicService.resumeMedia();
        });

        binding.llnext.setOnClickListener(view -> {
            MusicService.stopMedia();
            if (IsRepeat.equalsIgnoreCase("1")) {
                // repeat is on play same song again
                getPrepareShowData(position);
            } else if (IsShuffle.equalsIgnoreCase("1")) {
                // shuffle is on - play a random song
                Random random = new Random();
                position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                getPrepareShowData(position);
            } else {
                if (position < listSize - 1) {
                    position = position + 1;
                } else {
                    position = 0;
                }
                getPrepareShowData(position);
            }
        });

        binding.llprev.setOnClickListener(view -> {
            MusicService.stopMedia();
            if (IsRepeat.equalsIgnoreCase("1")) {
                // repeat is on play same song again
                getPrepareShowData(position);
            } else if (IsShuffle.equalsIgnoreCase("1")) {
                // shuffle is on - play a random song
                Random random = new Random();
                position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                getPrepareShowData(position);
            } else {
                if (position > 0) {
                    position = position - 1;
                } else {
                    position = 0;
                }
                getPrepareShowData(position);
            }
        });

    }

    private void getPrepareShowData(int position) {
        if (listSize == 1) {
            position = 0;
        }
        BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
        Boolean queuePlay = shared.getBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
        Boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
        if (audioPlay) {
            binding.tvName.setText(mainPlayModelList.get(position).getName());
//        binding.tvTitle.setText(mainPlayModelList.get(position).getAudioSubCategory());
            Glide.with(ctx).load(mainPlayModelList.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
            binding.tvTime.setText(mainPlayModelList.get(position).getAudioDuration());
            if (MusicService.isPause) {
                MusicService.resumeMedia();
            } else {
                MusicService.play(ctx, Uri.parse(mainPlayModelList.get(position).getAudioFile()));
                MusicService.playMedia();
            }
        } else if (queuePlay) {
            binding.tvName.setText(addToQueueModelList.get(position).getName());
//        binding.tvTitle.setText(mainPlayModelList.get(position).getAudioSubCategory());
            Glide.with(ctx).load(addToQueueModelList.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
            binding.tvTime.setText(addToQueueModelList.get(position).getAudioDuration());
            if (MusicService.isPause) {
                MusicService.resumeMedia();
            } else {
                MusicService.play(ctx, Uri.parse(mainPlayModelList.get(position).getAudioFile()));
                MusicService.playMedia();
            }
        }
        binding.simpleSeekbar.setClickable(false);
        startTime = MusicService.getStartTime();
        hdlr.postDelayed(UpdateSongTime, 60);
        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ctx, PlayWellnessActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
        finish();
    }
    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            startTime = MusicService.getStartTime();
            Time t = Time.valueOf("00:" + mainPlayModelList.get(position).getAudioDuration());
            long totalDuration = t.getTime();
            long currentDuration = MusicService.getStartTime();

            int progress = (int) (MusicService.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            binding.simpleSeekbar.setProgress(progress);
            binding.simpleSeekbar.setMax(100);

            // Running this thread after 100 milliseconds
            hdlr.postDelayed(this, 60);
        }
    };
}