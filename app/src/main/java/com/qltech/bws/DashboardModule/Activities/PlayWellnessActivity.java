package com.qltech.bws.DashboardModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.ActivityPlayWellnessBinding;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PlayWellnessActivity extends AppCompatActivity {
    ActivityPlayWellnessBinding binding;
    private static int oTime = 0, startTime = 0, endTime = 0, forwardTime = 30000, backwardTime = 30000;
    private MediaPlayer mPlayer;
    private Handler hdlr = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_play_wellness);

        String url = "https://brainwellnessspa.com.au/Bws-consumer-panel/html/audio_file/Brain_Wellness_Spa_The_Happiness_Promise.mp3";

        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mPlayer.setDataSource(PlayWellnessActivity.this, Uri.parse(url));
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e("mPlayermPlayer", "" + mPlayer);
        binding.simpleSeekbar.setClickable(false);

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        MeasureRatio measureRatio = BWSApplication.measureRatio(PlayWellnessActivity.this, 0,
                1, 1, 1f, 0);
        binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        binding.ivRestaurantImage.setImageResource(R.drawable.square_logo);

        binding.llMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PlayWellnessActivity.this, AddQueueActivity.class);
                i.putExtra("play", "play");
                startActivity(i);
            }
        });

        binding.llViewQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PlayWellnessActivity.this, ViewQueueActivity.class);
                startActivity(i);
            }
        });

        showToast("Added to your queue");
        mPlayer.start();
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

        binding.llplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.start();
                binding.llplay.setVisibility(View.GONE);
                binding.llPause.setVisibility(View.VISIBLE);
                binding.llPause.setEnabled(true);
                binding.llplay.setEnabled(false);
            }
        });

        binding.llPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.llplay.setVisibility(View.VISIBLE);
                binding.llPause.setVisibility(View.GONE);
                mPlayer.pause();
                binding.llPause.setEnabled(false);
                binding.llplay.setEnabled(true);
            }
        });

        binding.llForwardSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((startTime + forwardTime) <= endTime) {
                    startTime = startTime + forwardTime;
                    mPlayer.seekTo(startTime);
                } else {
                    showToast("Please wait");
                }
                if (!binding.llplay.isEnabled()) {
                    binding.llplay.setEnabled(true);
                }
            }
        });

        binding.llBackWordSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((startTime - backwardTime) > 0) {
                    startTime = startTime - backwardTime;
                    mPlayer.seekTo(startTime);
                } else {
                    showToast("Please wait");
                }
                if (!binding.llplay.isEnabled()) {
                    binding.llplay.setEnabled(true);
                }
            }
        });
    }

    void showToast(String message) {
        Toast toast = new Toast(PlayWellnessActivity.this);
        View view = LayoutInflater.from(PlayWellnessActivity.this).inflate(R.layout.toast_layout, null);
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