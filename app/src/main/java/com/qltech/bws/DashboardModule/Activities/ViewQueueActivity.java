package com.qltech.bws.DashboardModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import com.google.gson.reflect.TypeToken;
import com.qltech.bws.DashboardModule.Adapters.QueueAdapter;
import com.qltech.bws.DashboardModule.Models.AddToQueueModel;
import com.qltech.bws.DashboardModule.Models.QueueModel;
import com.qltech.bws.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.ItemMoveCallback;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.Utility.MusicService;
import com.qltech.bws.databinding.ActivityViewQueueBinding;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ViewQueueActivity extends AppCompatActivity {
    ActivityViewQueueBinding binding;
    int position;
    Context ctx;
    ArrayList<AddToQueueModel> addToQueueModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_queue);
        ctx = ViewQueueActivity.this;
        addToQueueModels = new ArrayList<>();
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = shared.getString(CONSTANTS.PREF_KEY_queueList, String.valueOf(gson));
        position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
         Type type = new TypeToken<ArrayList<AddToQueueModel>>() {
        }.getType();
        addToQueueModels = gson.fromJson(json, type);
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

        QueueAdapter adapter = new QueueAdapter(addToQueueModels, ViewQueueActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ViewQueueActivity.this);
        binding.rvQueueList.setLayoutManager(mLayoutManager);
        binding.rvQueueList.setItemAnimator(new DefaultItemAnimator());
        ItemTouchHelper.Callback callback =
                new ItemMoveCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(binding.rvQueueList);
        binding.rvQueueList.setAdapter(adapter);

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

}