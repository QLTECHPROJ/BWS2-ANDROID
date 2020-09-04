package com.qltech.bws.DashboardModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.qltech.bws.DashboardModule.Adapters.QueueAdapter;
import com.qltech.bws.DashboardModule.Models.QueueModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.Utility.MusicService;
import com.qltech.bws.databinding.ActivityViewQueueBinding;

import java.util.ArrayList;
import java.util.List;

public class ViewQueueActivity extends AppCompatActivity {
    ActivityViewQueueBinding binding;
    List<QueueModel> listModelList = new ArrayList<>();
    String AudioFlag;
    int position;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_queue);
        ctx = ViewQueueActivity.this;
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson));
        position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");

        binding.rvQueueList.setFocusable(false);
        binding.nestedScroll.requestFocus();

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        MeasureRatio measureRatio = BWSApplication.measureRatio(ViewQueueActivity.this, 0,
                1, 1, 0.1f, 0);
        binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        binding.ivRestaurantImage.setImageResource(R.drawable.square_logo);

        /*if (MusicService.isPause) {
            MusicService.resumeMedia();
        } else {
            MusicService.play(ctx, Uri.parse(mainPlayModelList.get(position).getAudioFile()));
            MusicService.playMedia();
        }*/

        binding.simpleSeekbar.setClickable(false);
//        QueueAdapter adapter = new QueueAdapter(listModelList, ViewQueueActivity.this, mPlayer, endTime);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ViewQueueActivity.this);
        binding.rvQueueList.setLayoutManager(mLayoutManager);
        binding.rvQueueList.setItemAnimator(new DefaultItemAnimator());
//        binding.rvQueueList.setAdapter(adapter);

        prepareQueueData();

        binding.llPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.llPause.setVisibility(View.GONE);
                binding.llPlay.setVisibility(View.VISIBLE);
                binding.ivPause.setImageResource(R.drawable.ic_play_white_icon);
//                MusicService.pauseMedia();
            }
        });

        binding.llPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.llPlay.setVisibility(View.GONE);
                binding.llPause.setVisibility(View.VISIBLE);
                binding.ivPlay.setImageResource(R.drawable.ic_pause_icon);
//                MusicService.resumeMedia();
            }
        });
    }

    private void prepareQueueData() {
        QueueModel list = new QueueModel("Motivation Program", "12:37", "https://brainwellnessspa.com.au/Bws-consumer-panel/html/audio_file/Brain_Wellness_Spa_The_Happiness_Promise.mp3");
        listModelList.add(list);
        list = new QueueModel("Self-Discipline Program", "12:37", "http://brainwellnessspa.com.au/Bws-consumer-panel/html/audio_file/Brain_Wellness_Spa_Abolishing_Hunger_Pains.mp3");
        listModelList.add(list);
        list = new QueueModel("Love Thy Self", "12:37", "https://brainwellnessspa.com.au/Bws-consumer-panel/html/audio_file/Brain_Wellness_Spa_Anger.mp3");
        listModelList.add(list);
        list = new QueueModel("I Can Attitude and Mind...", "12:37", "https://brainwellnessspa.com.au/Bws-consumer-panel/html/audio_file/Brain_Wellness_Spa_Anxiety.mp3");
        listModelList.add(list);
        list = new QueueModel("Motivation Program", "12:37", "https://brainwellnessspa.com.au/Bws-consumer-panel/html/audio_file/Brain_Wellness_Spa_Home_Maintenance.mp3");
        listModelList.add(list);
        list = new QueueModel("Self-Discipline Program", "12:37", "");
        listModelList.add(list);
        list = new QueueModel("Love Thy Self", "12:37", "");
        listModelList.add(list);
    }

}