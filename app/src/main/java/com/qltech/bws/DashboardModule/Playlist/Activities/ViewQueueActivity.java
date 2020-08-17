package com.qltech.bws.DashboardModule.Playlist.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.qltech.bws.DashboardModule.Adapters.QueueAdapter;
import com.qltech.bws.DashboardModule.Models.QueueModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.ActivityViewQueueBinding;

import java.util.ArrayList;
import java.util.List;

public class ViewQueueActivity extends AppCompatActivity {
    ActivityViewQueueBinding binding;
    List<QueueModel> listModelList = new ArrayList<>();
    private MediaPlayer mPlayer;
    private static int endTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_queue);

        binding.rvQueueList.setFocusable(false);
        binding.nestedScroll.requestFocus();
//        mPlayer = new MediaPlayer();
        mPlayer = MediaPlayer.create(ViewQueueActivity.this, R.raw.aasha);
        endTime = mPlayer.getDuration();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

      /*  try {
            mPlayer.setDataSource(ViewQueueActivity.this, Uri.parse(listModelList.get(1).getLink()));
            Log.e("listModel.getLink", "" + listModelList.get(1).getLink());
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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

        binding.simpleSeekbar.setClickable(false);
        QueueAdapter adapter = new QueueAdapter(listModelList, ViewQueueActivity.this, mPlayer, endTime);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ViewQueueActivity.this);
        binding.rvQueueList.setLayoutManager(mLayoutManager);
        binding.rvQueueList.setItemAnimator(new DefaultItemAnimator());
        binding.rvQueueList.setAdapter(adapter);

        prepareQueueData();

        binding.llPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.llPause.setVisibility(View.GONE);
                binding.llPlay.setVisibility(View.VISIBLE);
                binding.ivPause.setImageResource(R.drawable.ic_play_white_icon);
                mPlayer.pause();
            }
        });

        binding.llPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.llPlay.setVisibility(View.GONE);
                binding.llPause.setVisibility(View.VISIBLE);
                binding.ivPlay.setImageResource(R.drawable.ic_pause_icon);
                mPlayer.isPlaying();
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
        list = new QueueModel("I Can Attitude and Mind...", "12:37", "");
        listModelList.add(list);
        list = new QueueModel("Motivation Program", "12:37", "");
        listModelList.add(list);
        list = new QueueModel("Self-Discipline Program", "12:37", "");
        listModelList.add(list);
        list = new QueueModel("Love Thy Self", "12:37", "");
        listModelList.add(list);
        list = new QueueModel("I Can Attitude and Mind...", "12:37", "");
        listModelList.add(list);
        list = new QueueModel("Motivation Program", "12:37", "");
        listModelList.add(list);
        list = new QueueModel("Self-Discipline Program", "12:37", "");
        listModelList.add(list);
        list = new QueueModel("Love Thy Self", "12:37", "");
        listModelList.add(list);
        list = new QueueModel("I Can Attitude and Mind...", "12:37", "");
        listModelList.add(list);
    }

}