package com.qltech.bws.DashboardModule.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Models.AddToQueueModel;
import com.qltech.bws.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.ItemMoveCallback;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.Utility.MusicService;
import com.qltech.bws.databinding.ActivityViewQueueBinding;
import com.qltech.bws.databinding.QueueListLayoutBinding;

import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static com.qltech.bws.Utility.MusicService.isMediaStart;

public class ViewQueueActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    ActivityViewQueueBinding binding;
    int position, listSize, startTime = 0;
    String IsRepeat, IsShuffle;
    Context ctx;
    Activity activity;
    ArrayList<MainPlayModel> mainPlayModelList;
    ArrayList<AddToQueueModel> addToQueueModelList;
    SharedPreferences shared;
    Boolean queuePlay, audioPlay;
    private long mLastClickTime = 0;
    private Handler hdlr;
    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            startTime = MusicService.getStartTime();
            Time t = Time.valueOf("00:00:00");
            if (queuePlay) {
                t = Time.valueOf("00:" + addToQueueModelList.get(position).getAudioDuration());
            } else if (audioPlay) {
                t = Time.valueOf("00:" + mainPlayModelList.get(position).getAudioDuration());
            }
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
        SharedPreferences Status = getSharedPreferences(CONSTANTS.PREF_KEY_Status, Context.MODE_PRIVATE);
        IsRepeat = Status.getString(CONSTANTS.PREF_KEY_IsRepeat, "");
        IsShuffle = Status.getString(CONSTANTS.PREF_KEY_IsShuffle, "");

        binding.rvQueueList.setFocusable(false);
        binding.nestedScroll.requestFocus();

        queuePlay = shared.getBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
        audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);

        binding.llBack.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            callBack();
        });
        MeasureRatio measureRatio = BWSApplication.measureRatio(ViewQueueActivity.this, 0,
                1, 1, 0.1f, 0);
        binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        getPrepareShowData(position);
        binding.simpleSeekbar.setOnSeekBarChangeListener(this);

        MusicService.mediaPlayer.setOnCompletionListener(this);

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
            binding.simpleSeekbar.setProgress(binding.simpleSeekbar.getProgress());
            binding.llPlay.setVisibility(View.VISIBLE);
            binding.llPause.setVisibility(View.GONE);
            MusicService.pauseMedia();
        });

        binding.llPlay.setOnClickListener(view -> {
            binding.llPlay.setVisibility(View.GONE);
            binding.llPause.setVisibility(View.VISIBLE);
            MusicService.resumeMedia();
        });

        binding.llnext.setOnClickListener(view -> {
            MusicService.stopMedia();
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
                }
            }
        });

        binding.llprev.setOnClickListener(view -> {
            MusicService.stopMedia();
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
                }
            }
        });
    }

    private void getPrepareShowData(int position) {
        queuePlay = shared.getBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
        audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);

        if (audioPlay) {
            listSize = mainPlayModelList.size();
        } else if (queuePlay) {
            listSize = addToQueueModelList.size();
        }
        if (listSize == 1) {
            binding.llnext.setEnabled(false);
            binding.llnext.setEnabled(false);
            binding.llprev.setClickable(false);
            binding.llprev.setClickable(false);
            binding.ivnext.setColorFilter(ContextCompat.getColor(ctx, R.color.extra_light_blue), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.ivprev.setColorFilter(ContextCompat.getColor(ctx, R.color.extra_light_blue), android.graphics.PorterDuff.Mode.SRC_IN);
            position = 0;
        }else if(position == listSize - 1){
            binding.llnext.setEnabled(false);
            binding.llnext.setClickable(false);
            binding.ivnext.setColorFilter(ContextCompat.getColor(ctx, R.color.extra_light_blue), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if(position == 0){
            binding.llprev.setEnabled(false);
            binding.llprev.setClickable(false);
            binding.ivprev.setColorFilter(ContextCompat.getColor(ctx, R.color.extra_light_blue), android.graphics.PorterDuff.Mode.SRC_IN);
        }  else {
            binding.llnext.setEnabled(true);
            binding.llnext.setEnabled(true);
            binding.llprev.setClickable(true);
            binding.llprev.setClickable(true);
            binding.ivnext.setColorFilter(ContextCompat.getColor(ctx, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.ivprev.setColorFilter(ContextCompat.getColor(ctx, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
        if (audioPlay) {
            binding.tvTitle.setText(mainPlayModelList.get(position).getName());
            binding.tvName.setText(mainPlayModelList.get(position).getName());
//        binding.tvCategory.setText(mainPlayModelList.get(position).getAudioSubCategory());
            Glide.with(ctx).load(mainPlayModelList.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
            binding.tvTime.setText(mainPlayModelList.get(position).getAudioDuration());
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
        } else if (queuePlay) {
            if (listSize == 1) {
                position = 0;
            } else if (listSize == 0) {
            } else {
                binding.tvTitle.setText(addToQueueModelList.get(position).getName());
                binding.tvName.setText(addToQueueModelList.get(position).getName());
//            binding.tvCategory.setText(addToQueueModelList.get(position).getAudioSubCategory());
                Glide.with(ctx).load(addToQueueModelList.get(position).getImageFile()).thumbnail(0.05f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
                binding.tvTime.setText(addToQueueModelList.get(position).getAudioDuration());

                if (!isMediaStart) {
                    MusicService.play(ctx, Uri.parse(addToQueueModelList.get(position).getAudioFile()));
                    MusicService.playMedia();
                } else {
                    if (MusicService.isPause) {

                        binding.llPlay.setVisibility(View.VISIBLE);
                        binding.llPause.setVisibility(View.GONE);
//                    MusicService.resumeMedia();
                    } else {
                        binding.llPause.setVisibility(View.VISIBLE);
                        binding.llPlay.setVisibility(View.GONE);
                        MusicService.play(ctx, Uri.parse(addToQueueModelList.get(position).getAudioFile()));
                        MusicService.playMedia();
                    }
                }
            }
            binding.llNowPlaying.setOnClickListener(view ->
                    callBack());

            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson2 = new Gson();
            String json3 = gson2.toJson(addToQueueModelList);
            editor.putString(CONSTANTS.PREF_KEY_queueList, json3);
            editor.commit();
            startTime = MusicService.getStartTime();
        }
        binding.simpleSeekbar.setClickable(true);
        hdlr.postDelayed(UpdateSongTime, 60);
        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
    }

    private void callnextprev() {
    }

    private void callBack() {
        if (binding.llPause.getVisibility() == View.VISIBLE) {
            MusicService.isPause = true;
        }
        Intent i = new Intent(ctx, PlayWellnessActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
        finish();
    }

    @Override
    protected void onResume() {
        if (isMediaStart) {
            if (MusicService.isPlaying()) {
                binding.llPlay.setVisibility(View.GONE);
                binding.llPause.setVisibility(View.VISIBLE);
            }
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        callBack();
        super.onBackPressed();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        if (queuePlay) {
            addToQueueModelList.remove(position);
            listSize = addToQueueModelList.size();
            if (position < listSize - 1) {
                if (listSize == 0) {
                    MusicService.stopMedia();
                } else {
                    position = 0;
                }
            }
        } else if (IsRepeat.equalsIgnoreCase("1")) {
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
        if (listSize == 1 || position < listSize - 1) {
            binding.llnext.setEnabled(false);
            binding.llnext.setEnabled(false);
            binding.llprev.setClickable(false);
            binding.llprev.setClickable(false);
            binding.ivnext.setColorFilter(ContextCompat.getColor(ctx, R.color.extra_light_blue), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.ivprev.setColorFilter(ContextCompat.getColor(ctx, R.color.extra_light_blue), android.graphics.PorterDuff.Mode.SRC_IN);
            position = 0;
        } else if(position == listSize - 1){
            binding.llnext.setEnabled(false);
            binding.llnext.setClickable(false);
            binding.ivnext.setColorFilter(ContextCompat.getColor(ctx, R.color.extra_light_blue), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if(position == 0){
            binding.llprev.setEnabled(false);
            binding.llprev.setClickable(false);
            binding.ivprev.setColorFilter(ContextCompat.getColor(ctx, R.color.extra_light_blue), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            binding.llnext.setEnabled(true);
            binding.llnext.setEnabled(true);
            binding.llprev.setClickable(true);
            binding.llprev.setClickable(true);
            binding.ivnext.setColorFilter(ContextCompat.getColor(ctx, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.ivprev.setColorFilter(ContextCompat.getColor(ctx, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    public void updateProgressBar() {
        hdlr.postDelayed(UpdateSongTime, 100);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        hdlr.removeCallbacks(UpdateSongTime);

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

    public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.MyViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {
        ArrayList<AddToQueueModel> listModelList;
        Context ctx;


        public QueueAdapter(ArrayList<AddToQueueModel> listModelList, Context ctx) {
            this.listModelList = listModelList;
            this.ctx = ctx;

        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            QueueListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.queue_list_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            AddToQueueModel listModel = listModelList.get(position);

            holder.binding.tvTitle.setText(listModel.getName());
            holder.binding.tvTime.setText(listModel.getAudioDuration());

            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.1f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(listModel.getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

            holder.binding.llRemove.setOnClickListener(view -> callRemoveList(position));
            holder.binding.llMainLayout.setOnClickListener(view -> {
                binding.tvTitle.setText(listModel.getName());
                binding.tvName.setText(listModel.getName());
//            binding.tvCategory.setText(addToQueueModelList.get(position).getAudioSubCategory());
                Glide.with(ctx).load(listModel.getImageFile()).thumbnail(0.05f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
                binding.tvTime.setText(listModel.getAudioDuration());
                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, true);
                editor.putInt(CONSTANTS.PREF_KEY_position, position);
                editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
                editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, false);
                editor.commit();
                getPrepareShowData(position);
            });

        }

        private void callRemoveList(int position) {
            listModelList.remove(position);
            notifyDataSetChanged();
            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String json = gson.toJson(listModelList);
            editor.putString(CONSTANTS.PREF_KEY_queueList, json);
            editor.commit();
        }

        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        @Override
        public void onRowMoved(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(listModelList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(listModelList, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String json = gson.toJson(listModelList);
            editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
            editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
            editor.putString(CONSTANTS.PREF_KEY_queueList, json);
            editor.commit();

        }

        @Override
        public void onRowSelected(RecyclerView.ViewHolder myViewHolder) {

        }

        @Override
        public void onRowClear(RecyclerView.ViewHolder myViewHolder) {

        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            QueueListLayoutBinding binding;

            public MyViewHolder(QueueListLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}