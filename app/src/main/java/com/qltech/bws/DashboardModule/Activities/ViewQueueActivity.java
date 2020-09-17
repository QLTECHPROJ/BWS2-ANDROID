package com.qltech.bws.DashboardModule.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.qltech.bws.DashboardModule.Models.SucessModel;
import com.qltech.bws.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.ItemMoveCallback;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.ActivityViewQueueBinding;
import com.qltech.bws.databinding.QueueListLayoutBinding;

import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.qltech.bws.Utility.MusicService.SeekTo;
import static com.qltech.bws.Utility.MusicService.getEndTime;
import static com.qltech.bws.Utility.MusicService.getProgressPercentage;
import static com.qltech.bws.Utility.MusicService.getStartTime;
import static com.qltech.bws.Utility.MusicService.isMediaStart;
import static com.qltech.bws.Utility.MusicService.isPause;
import static com.qltech.bws.Utility.MusicService.isPlaying;
import static com.qltech.bws.Utility.MusicService.isPrepare;
import static com.qltech.bws.Utility.MusicService.mediaPlayer;
import static com.qltech.bws.Utility.MusicService.oTime;
import static com.qltech.bws.Utility.MusicService.pauseMedia;
import static com.qltech.bws.Utility.MusicService.play;
import static com.qltech.bws.Utility.MusicService.playMedia;
import static com.qltech.bws.Utility.MusicService.progressToTimer;
import static com.qltech.bws.Utility.MusicService.resumeMedia;
import static com.qltech.bws.Utility.MusicService.savePrefQueue;
import static com.qltech.bws.Utility.MusicService.stopMedia;

public class ViewQueueActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener/*, AudioManager.OnAudioFocusChangeListener*/ {
    ActivityViewQueueBinding binding;
    int position, listSize, startTime = 0;
    String IsRepeat, IsShuffle, id;
    Context ctx;
    Activity activity;
    ArrayList<MainPlayModel> mainPlayModelList;
    ArrayList<AddToQueueModel> addToQueueModelList;
    SharedPreferences shared;
    Boolean queuePlay, audioPlay;
    QueueAdapter adapter;
    private long mLastClickTime = 0;
    private Handler handler;
    //    private AudioManager mAudioManager;
    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            startTime = getStartTime();
            Time t = Time.valueOf("00:00:00");
            if (queuePlay) {
                if (listSize != 0) {
                    t = Time.valueOf("00:" + addToQueueModelList.get(position).getAudioDuration());
                } else {
                    stopMedia();
                }
            } else if (audioPlay) {
                t = Time.valueOf("00:" + mainPlayModelList.get(position).getAudioDuration());
            }
            long totalDuration = t.getTime();
            long currentDuration = getStartTime();

            int progress = (int) (getProgressPercentage(currentDuration, totalDuration));
            if (currentDuration == totalDuration) {
            } else if (isPause) {
                binding.simpleSeekbar.setProgress(oTime);
            } else {
                binding.simpleSeekbar.setProgress(progress);
            }
            binding.simpleSeekbar.setMax(100);
            handler.postDelayed(this, 60);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_queue);
        ctx = ViewQueueActivity.this;
        activity = ViewQueueActivity.this;
        handler = new Handler();
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
/*        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);*/
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
        if (isMediaStart) {
            mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                handler.removeCallbacks(UpdateSongTime);
                isPrepare = false;
                isMediaStart = false;
                isPause = false;
                if (IsRepeat.equalsIgnoreCase("1")) {
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
                    if (queuePlay) {
                        adapter.callRemoveList(position);
                        listSize = addToQueueModelList.size();
                        if (listSize == 0) {
                            stopMedia();
                        } else if (listSize == 1) {
                            position = 0;
                            getPrepareShowData(position);
                        } else {
                            Random random = new Random();
                            position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                            getPrepareShowData(position);
                        }
                    }  else {
                        if (listSize == 1) {

                        } else {
                            Random random = new Random();
                            position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                            getPrepareShowData(position);
                        }
                    }
                } else {
                    if (queuePlay) {
                        adapter.callRemoveList(position);
                        listSize = addToQueueModelList.size();
                        if (position < listSize - 1) {
                            getPrepareShowData(position);
                        } else {
                            if (listSize == 0) {
                                stopMedia();
                            } else {
                                position = 0;
                                getPrepareShowData(position);
                            }
                        }
                    } else {
                        if (position < (listSize - 1)) {
                            position = position + 1;
                            getPrepareShowData(position);
                        } else {
                            binding.llPlay.setVisibility(View.VISIBLE);
                            binding.llPause.setVisibility(View.GONE);
                            stopMedia();
                        }
                    }
                }
                if (listSize == 1) {
                    binding.llnext.setEnabled(false);
                    binding.llnext.setEnabled(false);
                    binding.llprev.setClickable(false);
                    binding.llprev.setClickable(false);
                    binding.ivnext.setColorFilter(ContextCompat.getColor(ctx, R.color.extra_light_blue), android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.ivprev.setColorFilter(ContextCompat.getColor(ctx, R.color.extra_light_blue), android.graphics.PorterDuff.Mode.SRC_IN);
                    position = 0;
                } /*else if (position == listSize - 1 && IsRepeat.equalsIgnoreCase("1")) {
                binding.llnext.setEnabled(false);
                binding.llnext.setClickable(false);
                binding.ivnext.setColorFilter(ContextCompat.getColor(ctx, R.color.extra_light_blue), android.graphics.PorterDuff.Mode.SRC_IN);
            } else if (position == 0 && IsRepeat.equalsIgnoreCase("1")) {
                binding.llprev.setEnabled(false);
                binding.llprev.setClickable(false);
                binding.ivprev.setColorFilter(ContextCompat.getColor(ctx, R.color.extra_light_blue), android.graphics.PorterDuff.Mode.SRC_IN);
            }*/ else {
                    binding.llnext.setEnabled(true);
                    binding.llnext.setEnabled(true);
                    binding.llprev.setClickable(true);
                    binding.llprev.setClickable(true);
                    binding.ivnext.setColorFilter(ContextCompat.getColor(ctx, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                    binding.ivprev.setColorFilter(ContextCompat.getColor(ctx, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                }
            });
        }
        callAdapterMethod();
        binding.llNowPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack();
            }
        });

        binding.llPause.setOnClickListener(view -> {
            handler.removeCallbacks(UpdateSongTime);
            binding.simpleSeekbar.setProgress(binding.simpleSeekbar.getProgress());
            pauseMedia();
            binding.llPlay.setVisibility(View.VISIBLE);
            binding.llPause.setVisibility(View.GONE);
            oTime = binding.simpleSeekbar.getProgress();
        });

        binding.llPlay.setOnClickListener(view -> {
            binding.llPlay.setVisibility(View.GONE);
            binding.llPause.setVisibility(View.VISIBLE);
            resumeMedia();
            isPause = false;
            handler.postDelayed(UpdateSongTime, 60);
        });

        binding.llnext.setOnClickListener(view -> {
            stopMedia();
            isMediaStart = false;
            isPrepare = false;
            isPause = false;
            if (IsRepeat.equalsIgnoreCase("1")) {
                // repeat is on play same song again
                if (position < listSize - 1) {
                    position = position + 1;
                } else {
                    position = 0;
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
                } else if (listSize != 1) {
                    position = 0;
                    getPrepareShowData(position);
                }
            }
        });

        binding.llprev.setOnClickListener(view -> {
            stopMedia();
            isMediaStart = false;
            isPrepare = false;
            isPause = false;
            if (IsRepeat.equalsIgnoreCase("1")) {
                // repeat is on play same song again
                if (position > 0) {
                    position = position - 1;
                    getPrepareShowData(position);
                } else if (listSize != 1) {
                    position = listSize - 1;
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
                } else if (listSize != 1) {
                    position = listSize - 1;
                    getPrepareShowData(position);
                }
            }
        });
    }

    private void callAdapterMethod() {
        if (addToQueueModelList.size() != 0) {
            adapter = new QueueAdapter(addToQueueModelList, ViewQueueActivity.this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ViewQueueActivity.this);
            binding.rvQueueList.setLayoutManager(mLayoutManager);
            binding.rvQueueList.setItemAnimator(new DefaultItemAnimator());
            ItemTouchHelper.Callback callback =
                    new ItemMoveCallback(adapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(binding.rvQueueList);
            binding.rvQueueList.setAdapter(adapter);
        }
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
        }/* else if (position == listSize - 1 && IsRepeat.equalsIgnoreCase("1")) {
            binding.llnext.setEnabled(false);
            binding.llnext.setClickable(false);
            binding.ivnext.setColorFilter(ContextCompat.getColor(ctx, R.color.extra_light_blue), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (position == 0 && IsRepeat.equalsIgnoreCase("1")) {
            binding.llprev.setEnabled(false);
            binding.llprev.setClickable(false);
            binding.ivprev.setColorFilter(ContextCompat.getColor(ctx, R.color.extra_light_blue), android.graphics.PorterDuff.Mode.SRC_IN);
        } */ else {
            binding.llnext.setEnabled(true);
            binding.llnext.setEnabled(true);
            binding.llprev.setClickable(true);
            binding.llprev.setClickable(true);
            binding.ivnext.setColorFilter(ContextCompat.getColor(ctx, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.ivprev.setColorFilter(ContextCompat.getColor(ctx, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        BWSApplication.showProgressBar(binding.ImgV, binding.progressBarHolder, activity);
        if (audioPlay) {

            id = mainPlayModelList.get(position).getID();
            setInIt(mainPlayModelList.get(position).getName(), mainPlayModelList.get(position).getAudiomastercat(),
                    mainPlayModelList.get(position).getImageFile(), mainPlayModelList.get(position).getAudioDuration());

           /* if (!isMediaStart) {
                play(getApplicationContext(), Uri.parse(mainPlayModelList.get(position).getAudioFile()));
                playMedia();
                binding.llPause.setVisibility(View.VISIBLE);
                binding.llPlay.setVisibility(View.GONE);
            } else {*/
            if (isPause) {
                binding.llPlay.setVisibility(View.VISIBLE);
                binding.llPause.setVisibility(View.GONE);
                binding.simpleSeekbar.setProgress(oTime);
//                    resumeMedia();
            } else if ((isPrepare || isMediaStart || isPlaying()) && !isPause) {
                binding.llPause.setVisibility(View.VISIBLE);
                binding.llPlay.setVisibility(View.GONE);
            } else {
                binding.llPause.setVisibility(View.VISIBLE);
                binding.llPlay.setVisibility(View.GONE);
                play(Uri.parse(mainPlayModelList.get(position).getAudioFile()));
                playMedia();
//                }
            }
        } else if (queuePlay) {
            if (listSize == 1) {
                position = 0;
            }
            id = addToQueueModelList.get(position).getID();
            setInIt(addToQueueModelList.get(position).getName(), addToQueueModelList.get(position).getAudiomastercat(),
                    addToQueueModelList.get(position).getImageFile(), addToQueueModelList.get(position).getAudioDuration());
         /*   if (!isMediaStart) {
                play( Uri.parse(addToQueueModelList.get(position).getAudioFile()));
                playMedia();
                binding.llPause.setVisibility(View.VISIBLE);
                binding.llPlay.setVisibility(View.GONE);
            } else {*/
            if (isPause) {
                binding.llPlay.setVisibility(View.VISIBLE);
                binding.llPause.setVisibility(View.GONE);
                binding.simpleSeekbar.setProgress(oTime);
//                    resumeMedia();
            } else if ((isPrepare || isMediaStart || isPlaying()) && !isPause) {
                binding.llPause.setVisibility(View.VISIBLE);
                binding.llPlay.setVisibility(View.GONE);
            } else {
                binding.llPause.setVisibility(View.VISIBLE);
                binding.llPlay.setVisibility(View.GONE);
                play(Uri.parse(addToQueueModelList.get(position).getAudioFile()));
                playMedia();
            }
//            }
            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson2 = new Gson();
            String json3 = gson2.toJson(addToQueueModelList);
            editor.putString(CONSTANTS.PREF_KEY_queueList, json3);
            editor.commit();
            startTime = getStartTime();
        }
        addToRecentPlay();
        binding.simpleSeekbar.setClickable(true);
        handler.postDelayed(UpdateSongTime, 60);
        BWSApplication.hideProgressBar(binding.ImgV, binding.progressBarHolder, activity);
    }

    private void setInIt(String name, String audiomastercat, String imageFile, String audioDuration) {
        binding.tvTitle.setText(name);
        binding.tvName.setText(name);
        binding.tvCategory.setText(audiomastercat);
        Glide.with(ctx).load(imageFile).thumbnail(0.05f)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
        binding.tvTime.setText(audioDuration);
    }

    private void addToRecentPlay() {
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        String UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

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

    private void callBack() {
        if (binding.llPause.getVisibility() == View.VISIBLE) {
            isPause = false;
        }
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        Gson gson = new Gson();
        String json = gson.toJson(addToQueueModelList);
        editor.putString(CONSTANTS.PREF_KEY_queueList, json);
        editor.putInt(CONSTANTS.PREF_KEY_position, position);
        editor.commit();
        Intent i = new Intent(ctx, PlayWellnessActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
        finish();
    }

    @Override
    protected void onResume() {
        if ((isPrepare || isMediaStart || isPlaying()) && !isPause) {
            binding.llPlay.setVisibility(View.GONE);
            binding.llPause.setVisibility(View.VISIBLE);
        } else {
            binding.llPlay.setVisibility(View.VISIBLE);
            binding.llPause.setVisibility(View.GONE);
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        callBack();
        super.onBackPressed();
    }

    public void updateProgressBar() {
        handler.postDelayed(UpdateSongTime, 60);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(UpdateSongTime);

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(UpdateSongTime);
        int totalDuration = getEndTime();

        int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        SeekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.MyViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {
        ArrayList<AddToQueueModel> listModelList;
        Context ctx;
        boolean queueClick = false;


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
            binding.tvCategory.setText(listModel.getAudiomastercat());

            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.1f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(listModel.getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

            holder.binding.llRemove.setOnClickListener(view -> callRemoveList(position));
            if(queuePlay || !queueClick){
                if(listModel.getName().equalsIgnoreCase(binding.tvName.getText().toString())){
                    callRemoveList1(position);
                }
            }
            holder.binding.llMainLayout.setOnClickListener(view -> {
                if (isPrepare || isMediaStart || isPause) {
                    stopMedia();
                }
                isPause = false;
                isPrepare = false;
                isMediaStart = false;

                setInIt(listModel.getName(), listModel.getAudiomastercat(),
                        listModel.getImageFile(), listModel.getAudioDuration());
                addToQueueModelList = listModelList;
                savePrefQueue(position, true, false, addToQueueModelList, ctx);
                getPrepareShowData(position);
                queueClick = true;
                callRemoveList1(position);
            });

        }

        public void callRemoveList1(int position) {
//            listModelList.remove(position);
//            notifyDataSetChanged();
        }
   public void callRemoveList(int position) {
            listModelList.remove(position);
            notifyDataSetChanged();
            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String json = gson.toJson(listModelList);
            editor.putString(CONSTANTS.PREF_KEY_queueList, json);
            editor.commit();
            BWSApplication.showToast("The audio has been removed from the queue", ctx);
            addToQueueModelList = listModelList;
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
            editor.putString(CONSTANTS.PREF_KEY_queueList, json);
            editor.commit();
            addToQueueModelList = listModelList;

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
/*    @Override
    public void onAudioFocusChange(int i) {
        switch (i) {
            case AudioManager.AUDIOFOCUS_GAIN:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Resume your media player here
                resumeMedia();
                binding.llPlay.setVisibility(View.GONE);
                binding.llPause.setVisibility(View.VISIBLE);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (isMediaStart) {
                    pauseMedia();
                    binding.llPlay.setVisibility(View.VISIBLE);
                    binding.llPause.setVisibility(View.GONE);
                }
//                MusicService.pauseMedia();// Pause your media player here
                break;
        }
    }*/
}