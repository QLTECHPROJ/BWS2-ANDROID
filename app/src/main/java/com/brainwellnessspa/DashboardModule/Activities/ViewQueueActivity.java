package com.brainwellnessspa.DashboardModule.Activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import com.brainwellnessspa.DashboardModule.Models.AppointmentDetailModel;
import com.brainwellnessspa.DashboardModule.Models.MainAudioModel;
import com.brainwellnessspa.DashboardModule.Models.SearchBothModel;
import com.brainwellnessspa.DashboardModule.Models.SubPlayListModel;
import com.brainwellnessspa.DashboardModule.Models.SuggestedModel;
import com.brainwellnessspa.DashboardModule.Models.ViewAllAudioListModel;
import com.brainwellnessspa.LikeModule.Models.LikesHistoryModel;
import com.brainwellnessspa.Services.OnClearFromRecentService;
import com.brainwellnessspa.Utility.MusicService;
import com.brainwellnessspa.Utility.Playable;
import com.brainwellnessspa.Utility.PlaybackStatus;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Models.AddToQueueModel;
import com.brainwellnessspa.DashboardModule.Models.SucessModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia;
import com.brainwellnessspa.EncryptDecryptUtils.FileUtils;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.ItemMoveCallback;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.Utility.StartDragListener;
import com.brainwellnessspa.databinding.ActivityViewQueueBinding;
import com.brainwellnessspa.databinding.QueueListLayoutBinding;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment.addToRecentPlayId;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.Utility.MusicService.SeekTo;
import static com.brainwellnessspa.Utility.MusicService.buildNotification;
import static com.brainwellnessspa.Utility.MusicService.deleteCache;
import static com.brainwellnessspa.Utility.MusicService.getEndTime;
import static com.brainwellnessspa.Utility.MusicService.getProgressPercentage;
import static com.brainwellnessspa.Utility.MusicService.getStartTime;
import static com.brainwellnessspa.Utility.MusicService.isCompleteStop;
import static com.brainwellnessspa.Utility.MusicService.isMediaStart;
import static com.brainwellnessspa.Utility.MusicService.isPause;
import static com.brainwellnessspa.Utility.MusicService.isPlaying;
import static com.brainwellnessspa.Utility.MusicService.isPrepare;
import static com.brainwellnessspa.Utility.MusicService.isStop;
import static com.brainwellnessspa.Utility.MusicService.isprogressbar;
import static com.brainwellnessspa.Utility.MusicService.mediaPlayer;
import static com.brainwellnessspa.Utility.MusicService.mediaSession;
import static com.brainwellnessspa.Utility.MusicService.mediaSessionManager;
import static com.brainwellnessspa.Utility.MusicService.oTime;
import static com.brainwellnessspa.Utility.MusicService.pauseMedia;
import static com.brainwellnessspa.Utility.MusicService.progressToTimer;
import static com.brainwellnessspa.Utility.MusicService.resumeMedia;
import static com.brainwellnessspa.Utility.MusicService.savePrefQueue;
import static com.brainwellnessspa.Utility.MusicService.stopMedia;
import static com.brainwellnessspa.Utility.MusicService.transportControls;
import static com.facebook.FacebookSdk.getApplicationContext;

public class ViewQueueActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,/* AudioManager.OnAudioFocusChangeListener,*/ StartDragListener/*, Playable */ {
    ActivityViewQueueBinding binding;
    int position, listSize, startTime = 0;
    String IsRepeat, IsShuffle, id, AudioId = "", ComeFromQueue = "", play = "", url, name, StrigRemoveName,playFrom="";
    Context ctx;
    Activity activity;
    ArrayList<MainPlayModel> mainPlayModelList;
    ArrayList<AddToQueueModel> addToQueueModelList, addToQueueModelList2;
    ArrayList<AddToQueueModel> addToQueueModeNowPlaying;
    SharedPreferences shared;
    Boolean queuePlay, audioPlay;
    QueueAdapter adapter;
    List<DownloadAudioDetails> downloadAudioDetailsList;
    ItemTouchHelper touchHelper;
    int mypos = 0, myCount;
    long totalDuration, currentDuration, myProgress = 0, diff = 0;
    private long mLastClickTime = 0;
    private Handler handler;
    boolean addSong = false;
    //    boolean isPlaying = false;
//    BroadcastReceiver broadcastReceiver;
    //    private AudioManager mAudioManager;
    private BroadcastReceiver playNewAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isPause || !isMediaStart) {
                binding.llPlay.setVisibility(View.VISIBLE);
                binding.llPause.setVisibility(View.GONE);
                buildNotification(PlaybackStatus.PAUSED, context,mainPlayModelList,addToQueueModelList,playFrom,position);
            } else {
                binding.llPause.setVisibility(View.VISIBLE);
                binding.llPlay.setVisibility(View.GONE);
                buildNotification(PlaybackStatus.PLAYING, context,mainPlayModelList,addToQueueModelList,playFrom,position);
            }
        }
    };
    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            startTime = getStartTime();
            Time t = Time.valueOf("00:00:00");
            if (queuePlay) {
                if (listSize != 0) {
                    if (!BWSApplication.isNetworkConnected(ctx)) {
                        if (mediaPlayer != null) {
                            totalDuration = mediaPlayer.getDuration();
                        } else {
                            t = Time.valueOf("00:" + downloadAudioDetailsList.get(0).getAudioDuration());
                        }
                    } else {
                        if (mediaPlayer != null) {
                            totalDuration = mediaPlayer.getDuration();
                        } else {
                            t = Time.valueOf("00:" + addToQueueModelList.get(position).getAudioDuration());
                        }
                    }
                } else {
                    stopMedia();
                }
            } else if (audioPlay) {
                if (!BWSApplication.isNetworkConnected(ctx)) {
                    if (mediaPlayer != null) {
                        totalDuration = mediaPlayer.getDuration();
                    } else {
                        t = Time.valueOf("00:" + downloadAudioDetailsList.get(0).getAudioDuration());
                    }
                } else {
                    if (mediaPlayer != null) {
                        totalDuration = mediaPlayer.getDuration();
                    } else {
                        t = Time.valueOf("00:" + mainPlayModelList.get(position).getAudioDuration());
                    }
                }
            }
            if (!BWSApplication.isNetworkConnected(ctx)) {
                totalDuration = mediaPlayer.getDuration();
            } else {
                if (mediaPlayer != null) {
                    totalDuration = mediaPlayer.getDuration();
                } else {
                    totalDuration = t.getTime();
                }
            }
            myProgress = currentDuration;
            currentDuration = getStartTime();
            diff = totalDuration - myProgress;
            Log.e("myProgress old!!!", String.valueOf(myProgress));
            if (myProgress == currentDuration && myProgress != 0 && !isPause && diff < 500) {
                Log.e("myProgress", String.valueOf(myProgress));
                myCount++;
                Log.e("myCount", String.valueOf(myCount));

                if (myCount == 10) {
                    Log.e("myCount complete", String.valueOf(myCount));
                    callComplete();
                    myCount = 0;
                }
            }
            int progress = getProgressPercentage(currentDuration, totalDuration);
            if (currentDuration == 0 && isCompleteStop) {
                binding.progressBar.setVisibility(View.GONE);
                binding.llProgressBar.setVisibility(View.GONE);
                binding.llPause.setVisibility(View.GONE);
                binding.llPlay.setVisibility(View.VISIBLE);
            } else if (currentDuration == 0 && isprogressbar) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.llProgressBar.setVisibility(View.VISIBLE);
                binding.llPause.setVisibility(View.GONE);
                binding.llPlay.setVisibility(View.GONE);
            } else if (currentDuration > 1 && !isPause) {
                binding.progressBar.setVisibility(View.GONE);
                binding.llProgressBar.setVisibility(View.GONE);
                binding.llPause.setVisibility(View.VISIBLE);
                binding.llPlay.setVisibility(View.GONE);
                isprogressbar = false;
            } else if (currentDuration >= 1 && isPause) {
                binding.progressBar.setVisibility(View.GONE);
                binding.llProgressBar.setVisibility(View.GONE);
                binding.llPause.setVisibility(View.GONE);
                binding.llPlay.setVisibility(View.VISIBLE);
                isprogressbar = false;
            }

            if (isPause) {
                binding.simpleSeekbar.setProgress(oTime);
            } else {
                binding.simpleSeekbar.setProgress(progress);
            }
            binding.simpleSeekbar.setMax(100);
            handler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_queue);
        ctx = ViewQueueActivity.this;
        activity = ViewQueueActivity.this;

        deleteCache(ctx);
        downloadAudioDetailsList = new ArrayList<>();
        if (getIntent().getExtras() != null) {
            AudioId = getIntent().getStringExtra(CONSTANTS.ID);
        }

        if (getIntent().getExtras() != null) {
            ComeFromQueue = getIntent().getStringExtra("ComeFromQueue");
        }
        if (getIntent().getExtras() != null) {
            play = getIntent().getStringExtra("play");
        }
        handler = new Handler();
        addToQueueModelList = new ArrayList<>();
        addToQueueModelList2 = new ArrayList<>();

        mainPlayModelList = new ArrayList<>();
        shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = shared.getString(CONSTANTS.PREF_KEY_queueList, String.valueOf(gson));
        position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
  /*      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ctx.getApplicationContext().startForegroundService(new Intent(ctx.getApplicationContext(), MusicService.class));
        }else{
            try {
                ctx.getApplicationContext().startService(new Intent(ctx.getApplicationContext(), MusicService.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            ctx.getApplicationContext().startService(new Intent(ctx.getApplicationContext(), MusicService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        if (!json.equalsIgnoreCase(String.valueOf(gson))) {
            Type type = new TypeToken<ArrayList<AddToQueueModel>>() {
            }.getType();
            addToQueueModelList = gson.fromJson(json, type);
            addToQueueModelList2 = gson.fromJson(json, type);
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
//        binding.nestedScroll.requestFocus();

        queuePlay = shared.getBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
        audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
        if(queuePlay){
            playFrom = "queuePlay";
        }else if (audioPlay){
            playFrom = "audioPlay";
        }else{
            playFrom = "audioPlay";
        }
        binding.llBack.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            callBack();
        });

        getPrepareShowData(position);
        binding.simpleSeekbar.setOnSeekBarChangeListener(this);
        callAdapterMethod();
        binding.llNowPlaying.setOnClickListener(view -> {
            handler.removeCallbacks(UpdateSongTime);
            if (binding.llPause.getVisibility() == View.VISIBLE) {
                isPause = false;
            }
            if (ComeFromQueue.equalsIgnoreCase("1")) {
                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                Gson gson2 = new Gson();
                String json22 = gson2.toJson(addToQueueModelList);
                editor.putString(CONSTANTS.PREF_KEY_queueList, json22);
                editor.putInt(CONSTANTS.PREF_KEY_position, position);
                editor.commit();
                Intent i = new Intent(ctx, PlayWellnessActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                finish();
            } else {
                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                Gson gson2 = new Gson();
                String json22 = gson2.toJson(addToQueueModelList);
                editor.putString(CONSTANTS.PREF_KEY_queueList, json22);
                editor.putInt(CONSTANTS.PREF_KEY_position, position);
                editor.commit();
                Intent i = new Intent(ctx, PlayWellnessActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                finish();
            }

        });

        binding.llPause.setOnClickListener(view -> {
            callPause();
        });

        binding.llPlay.setOnClickListener(view -> {
            callPlay();
        });

        binding.llnext.setOnClickListener(view -> {
            callNext();
        });

        binding.llprev.setOnClickListener(view -> {
            callPrevious    ();
        });
    }

    private void callPause() {

        handler.removeCallbacks(UpdateSongTime);
        binding.simpleSeekbar.setProgress(binding.simpleSeekbar.getProgress());
        pauseMedia();
        binding.llPlay.setVisibility(View.VISIBLE);
        binding.llPause.setVisibility(View.GONE);
        binding.llProgressBar.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.GONE);
        oTime = binding.simpleSeekbar.getProgress();
        buildNotification(PlaybackStatus.PAUSED, ctx, mainPlayModelList,addToQueueModelList,playFrom,position);
    }

    private void callPrevious() {

        if (BWSApplication.isNetworkConnected(ctx)) {
            stopMedia();
            isMediaStart = false;
            isPrepare = false;
            isPause = false;
            if (IsRepeat.equalsIgnoreCase("1") || IsRepeat.equalsIgnoreCase("0")) {
                // repeat is on play same song again
                if (position > 0) {
                    position = position - 1;
                    getPrepareShowData(position);
                } else if (listSize != 1) {
                    position = listSize - 1;
                    getPrepareShowData(position);
                }
            }/* else if (IsRepeat.equalsIgnoreCase("0")) {
                    getPrepareShowData(position);
                } */ else if (IsShuffle.equalsIgnoreCase("1")) {
                // shuffle is on - play a random song
                if (queuePlay) {
//                    adapter.callRemoveList(position, "1");
                    listSize = addToQueueModelList.size();
                    if (listSize == 0) {
                        stopMedia();
                    } else if (listSize == 1) {
                        stopMedia();
                    } else {
                        Random random = new Random();
                        position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                        getPrepareShowData(position);
                    }
                } else {
                    Random random = new Random();
                    position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                    getPrepareShowData(position);
                }
            } else {
                if (queuePlay) {
//                    adapter.callRemoveList(position, "1");
                    listSize = addToQueueModelList.size();
                    if (position > 0) {
                        getPrepareShowData(position - 1);
                    } else {
                        if (listSize == 0) {
                            savePrefQueue(0, false, true, addToQueueModelList, ctx);
                            stopMedia();
                        } else {
                            position = 0;
                            getPrepareShowData(position);
                        }
                    }
                } else {
                    if (position > 0) {
                        position = position - 1;

                        getPrepareShowData(position);
                    } else if (listSize != 1) {
                        position = listSize - 1;
                        getPrepareShowData(position);
                    }
                }
            }
            buildNotification(PlaybackStatus.PLAYING, ctx,mainPlayModelList,addToQueueModelList,playFrom,position);
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    private void callNext() {

        if (BWSApplication.isNetworkConnected(ctx)) {
            stopMedia();
            isMediaStart = false;
            isPrepare = false;
            isPause = false;
            if (IsRepeat.equalsIgnoreCase("1") || IsRepeat.equalsIgnoreCase("0")) {
                // repeat is on play same song again
                if (position < listSize - 1) {
                    position = position + 1;
                } else {
                    position = 0;
                }
                getPrepareShowData(position);
            } /*else if (IsRepeat.equalsIgnoreCase("0")) {
                    getPrepareShowData(position);
                } */ else if (IsShuffle.equalsIgnoreCase("1")) {
                // shuffle is on - play a random song
                if (queuePlay) {
//                    adapter.callRemoveList(position, "1");
                    listSize = addToQueueModelList.size();
                    if (listSize == 0) {
                        stopMedia();
                    } else if (listSize == 1) {
                        stopMedia();
                    } else {
                        Random random = new Random();
                        position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                        getPrepareShowData(position);
                    }
                } else {
                    Random random = new Random();
                    position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                    getPrepareShowData(position);
                }
            } else {
                if (queuePlay) {
//                    adapter.callRemoveList(position, "1");
                    listSize = addToQueueModelList.size();
                    if (position < listSize - 1) {
                        getPrepareShowData(position);
                    } else {
                        if (listSize == 0) {
                            savePrefQueue(0, false, true, addToQueueModelList, ctx);
                            stopMedia();
                        } else {
                            position = 0;
                            getPrepareShowData(position);
                        }
                    }
                } else {
                    if (position < listSize - 1) {
                        position = position + 1;
                        getPrepareShowData(position);
                    } else if (listSize != 1) {
                        position = 0;
                        getPrepareShowData(position);
                    }
                }
            }
            buildNotification(PlaybackStatus.PLAYING, ctx,mainPlayModelList,addToQueueModelList,playFrom,position);
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    private void callPlay() {

        if (!isMediaStart) {
            isCompleteStop = false;
            isprogressbar = true;
            handler.postDelayed(UpdateSongTime, 500);
            binding.llPlay.setVisibility(View.GONE);
            binding.llPause.setVisibility(View.GONE);
            binding.llProgressBar.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
            callMedia();
        } else if (isCompleteStop) {
            isCompleteStop = false;
            isprogressbar = true;
            handler.postDelayed(UpdateSongTime, 500);
            binding.llPlay.setVisibility(View.GONE);
            binding.llPause.setVisibility(View.GONE);
            binding.llProgressBar.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
            callMedia();
        } else {
            binding.llPlay.setVisibility(View.GONE);
            binding.llPause.setVisibility(View.VISIBLE);
            binding.llProgressBar.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
            resumeMedia();
            isPause = false;
            handler.postDelayed(UpdateSongTime, 500);
            buildNotification(PlaybackStatus.PLAYING, ctx, mainPlayModelList,addToQueueModelList,playFrom,position);
        }
    }

    private void callAdapterMethod() {
        if (addToQueueModelList.size() != 0) {
            if (queuePlay) {
                if (addToQueueModelList.get(position).getName().equalsIgnoreCase(binding.tvName.getText().toString())) {
                    mypos = position;
                    StrigRemoveName = addToQueueModelList.get(position).getName();
                    addToQueueModelList2.remove(position);
                }
            }
            adapter = new QueueAdapter(addToQueueModelList2, ctx, this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ctx);
            binding.rvQueueList.setLayoutManager(mLayoutManager);
            binding.rvQueueList.setItemAnimator(new DefaultItemAnimator());
            ItemTouchHelper.Callback callback =
                    new ItemMoveCallback(adapter);
            touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(binding.rvQueueList);
            binding.rvQueueList.setAdapter(adapter);
        }
    }

    public void GetMedia(String url, Context ctx, String PlaylistId) {

        downloadAudioDetailsList = new ArrayList<>();
        class GetMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                downloadAudioDetailsList = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .getLastIdByuId(url);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (isPause) {
                    binding.llPlay.setVisibility(View.VISIBLE);
                    binding.llPause.setVisibility(View.GONE);
                    binding.llProgressBar.setVisibility(View.GONE);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.simpleSeekbar.setProgress(oTime);
//                    resumeMedia();
                } else if (isCompleteStop) {
                    binding.llProgressBar.setVisibility(View.GONE);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.llPlay.setVisibility(View.VISIBLE);
                    binding.llPause.setVisibility(View.GONE);
                } else if ((isMediaStart || isPlaying()) && !isPause) {
                    binding.llPause.setVisibility(View.VISIBLE);
                    binding.llPlay.setVisibility(View.GONE);
                    binding.llProgressBar.setVisibility(View.GONE);
                    binding.progressBar.setVisibility(View.GONE);
                } else {
                    callMedia();
                }
                super.onPostExecute(aVoid);

            }
        }
        GetMedia st = new GetMedia();
        st.execute();
    }

    private void getPrepareShowData(int position) {
        queuePlay = shared.getBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
        audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
        if(queuePlay){
            playFrom = "queuePlay";
        }else if (audioPlay){
            playFrom = "audioPlay";
        }else{
            playFrom = "audioPlay";
        }
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
        BWSApplication.showProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
        if (audioPlay) {
            id = mainPlayModelList.get(position).getID();
            url = mainPlayModelList.get(position).getAudioFile();
            name = mainPlayModelList.get(position).getName();
            setInIt(mainPlayModelList.get(position).getName(), mainPlayModelList.get(position).getAudiomastercat(),
                    mainPlayModelList.get(position).getImageFile(), mainPlayModelList.get(position).getAudioDuration());
            GetMedia(url, ctx, mainPlayModelList.get(position).getPlaylistID());
        } else if (queuePlay) {
            if (listSize == 1) {
                position = 0;
            }
            id = addToQueueModelList.get(position).getID();
            url = addToQueueModelList.get(position).getAudioFile();
            name = addToQueueModelList.get(position).getName();
            setInIt(addToQueueModelList.get(position).getName(), addToQueueModelList.get(position).getAudiomastercat(),
                    addToQueueModelList.get(position).getImageFile(), addToQueueModelList.get(position).getAudioDuration());
            GetMedia(url, ctx, addToQueueModelList.get(position).getPlaylistID());
            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson2 = new Gson();
            String json3 = gson2.toJson(addToQueueModelList);
            editor.putString(CONSTANTS.PREF_KEY_queueList, json3);
            editor.commit();
            startTime = getStartTime();
        }
        if (!url.equalsIgnoreCase("")) {
            if (!id.equalsIgnoreCase(addToRecentPlayId)) {
                addToRecentPlay();
                Log.e("Api call recent", id);
            }
        }
        addToRecentPlayId = id;
        binding.simpleSeekbar.setClickable(true);
        handler.postDelayed(UpdateSongTime, 500);

        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(CONSTANTS.PREF_KEY_position, position);
        editor.commit();
        BWSApplication.hideProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);

        if (isMediaStart) {
            mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                if(mediaPlayer.isPlaying()) {
                    callComplete();
                    Log.e("calll complete trans", "trans");
                }
            });
        }
    }

    private void setMediaPlayer(String download, FileDescriptor fileDescriptor) {
        if (null == mediaPlayer) {
            mediaPlayer = new MediaPlayer();
            Log.e("Playinggggg", "Playinggggg");
        }
        try {
            if (mediaPlayer == null)
                mediaPlayer = new MediaPlayer();
            initMediaplyer();
            if (mediaPlayer.isPlaying()) {
                Log.e("Playinggggg", "stoppppp");
                mediaPlayer.stop();
                isMediaStart = false;
                isPrepare = false;
            }
            mediaPlayer = new MediaPlayer();
            initMediaplyer();
            if (download.equalsIgnoreCase("1")) {
                mediaPlayer.setDataSource(fileDescriptor);
            } else {
                mediaPlayer.setDataSource(url);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaPlayer.setAudioAttributes(
                        new AudioAttributes
                                .Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build());
            }
            mediaPlayer.prepareAsync();
            isPrepare = true;
        } catch (IllegalStateException | IOException e) {
            FileDescriptor fileDescriptor1 = null;
            setMediaPlayer("0", fileDescriptor1);
            e.printStackTrace();
        }
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.setOnPreparedListener(mp -> {
                Log.e("Playinggggg", "Startinggg");
                mediaPlayer.start();
                isMediaStart = true;
                binding.llProgressBar.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
                binding.llPlay.setVisibility(View.GONE);
                binding.llPause.setVisibility(View.VISIBLE);
            });
        }
        if (isPause) {
            binding.llPlay.setVisibility(View.VISIBLE);
            binding.llPause.setVisibility(View.GONE);
            buildNotification(PlaybackStatus.PAUSED, ctx, mainPlayModelList,addToQueueModelList,playFrom,position);
        } else {
            binding.llPause.setVisibility(View.VISIBLE);
            binding.llPlay.setVisibility(View.GONE);
            buildNotification(PlaybackStatus.PLAYING, ctx,mainPlayModelList,addToQueueModelList,playFrom,position);
        }
        if (isMediaStart /*&& !audioFile.equalsIgnoreCase("")*/) {
            mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                if(mediaPlayer.isPlaying()) {
                    callComplete();
                    Log.e("calll complete real", "real");
                }
            });
        }
    }
    private void initMediaplyer() {
        deleteCache(ctx.getApplicationContext());
        if (mediaSessionManager != null) return; //mediaSessionManager exists

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaSessionManager = (MediaSessionManager) ctx.getSystemService(Context.MEDIA_SESSION_SERVICE);
        }

//        mediaPlayer.setWakeMode(ctx.getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        // Create a new MediaSession
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaSession = new MediaSessionCompat(ctx.getApplicationContext(), "AudioPlayer");
        //Get MediaSessions transport controls
        transportControls = mediaSession.getController().getTransportControls();
        //set MediaSession -> ready to receive media commands
        mediaSession.setActive(true);
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //Set mediaSession's MetaData
//        updateMetaData();

        // Attach Callback to receive MediaSession updates

        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            // Implement callbacks
            @Override
            public void onPlay() {
                super.onPlay();
                callPlay();
            }

            @Override
            public void onPause() {
                super.onPause();
                callPause();
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                if (!url.equalsIgnoreCase("")) {
                    callNext();
//                updateMetaData();
                }
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();

                if (!url.equalsIgnoreCase("")) {
                    callPrevious();
//                updateMetaData();
                }
            }

            @Override
            public void onStop() {
                super.onStop();
//                    removeNotification();
//                    //Stop the service
//                    stopSelf();
            }

         /*   @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
            }*/
        });

    }

    private void callMedia() {
        FileDescriptor fileDescriptor = null;
        if (downloadAudioDetailsList.size() != 0) {
            binding.llProgressBar.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.llPlay.setVisibility(View.GONE);
            binding.llPause.setVisibility(View.GONE);
            DownloadMedia downloadMedia = new DownloadMedia(getApplicationContext());

            try {
                byte[] decrypt = null;
                decrypt = downloadMedia.decrypt(name);
                if (decrypt != null) {
                    binding.llProgressBar.setVisibility(View.GONE);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.llPause.setVisibility(View.VISIBLE);
                    binding.llPlay.setVisibility(View.GONE);
                    fileDescriptor = FileUtils.getTempFileDescriptor(getApplicationContext(), decrypt);
                    setMediaPlayer("1", fileDescriptor);
                } else {
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        setMediaPlayer("0", fileDescriptor);
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.llProgressBar.setVisibility(View.GONE);
                        binding.llPlay.setVisibility(View.VISIBLE);
                        binding.llPause.setVisibility(View.GONE);
                        BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (BWSApplication.isNetworkConnected(ctx)) {
                binding.llProgressBar.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.llPlay.setVisibility(View.GONE);
                binding.llPause.setVisibility(View.GONE);
                setMediaPlayer("0", fileDescriptor);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.llProgressBar.setVisibility(View.GONE);
                binding.llPlay.setVisibility(View.VISIBLE);
                binding.llPause.setVisibility(View.GONE);
                BWSApplication.showToast(getString(R.string.no_server_found), ctx);
            }
        }
    }

    private void callComplete() {
        handler.removeCallbacks(UpdateSongTime);
        isPrepare = false;
        isMediaStart = false;
        isPause = false;
        if (audioPlay && (url.equalsIgnoreCase("") || url.isEmpty())) {
            isDisclaimer = 0;
            removeArray();
        } else {
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
//                    adapter.callRemoveList(position, "1");
                    listSize = addToQueueModelList.size();
                    if (listSize == 0) {
                        stopMedia();
                    } else if (listSize == 1) {
                        position = 0;
                        getPrepareShowData(position);
                    } else {
                        int oldPosition = position;
                        Random random = new Random();
                        position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                        if (oldPosition == position) {
                            Random random1 = new Random();
                            position = random1.nextInt((listSize - 1) - 0 + 1) + 0;
                        }
                        getPrepareShowData(position);
                    }
                } else {
                    if (listSize == 1) {

                    } else {
                        int oldPosition = position;
                        Random random = new Random();
                        position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                        if (oldPosition == position) {
                            Random random1 = new Random();
                            position = random1.nextInt((listSize - 1) - 0 + 1) + 0;
                        }
                        getPrepareShowData(position);
                    }
                }
            } else {
                if (queuePlay) {
//                    adapter.callRemoveList(position, "1");
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
                        int oldPosition = position;
                        position = position + 1;
                        if (oldPosition == position) {
                            position++;
                        }
                        getPrepareShowData(position);
                    } else {
                        if (listSize == 1) {
                            binding.llPlay.setVisibility(View.VISIBLE);
                            binding.llPause.setVisibility(View.GONE);
                            stopMedia();
                        } else {
                            binding.llPlay.setVisibility(View.VISIBLE);
                            binding.llPause.setVisibility(View.GONE);
                            stopMedia();
//                        position = 0;
//                        getPrepareShowData(position);
                        }
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
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putInt(CONSTANTS.PREF_KEY_position, position);
            editor.commit();
        }
    }

    private void removeArray() {
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
        String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        Gson gson = new Gson();
        String json1 = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson));
        mainPlayModelList = new ArrayList<>();
        MainPlayModel mainPlayModel;
        if (AudioFlag.equalsIgnoreCase("MainAudioList")) {
            Type type = new TypeToken<ArrayList<MainAudioModel.ResponseData.Detail>>() {
            }.getType();
            ArrayList<MainAudioModel.ResponseData.Detail> arrayList = gson.fromJson(json1, type);

            if (arrayList.get(position).getAudioFile().equalsIgnoreCase("")) {
                arrayList.remove(position);
            }
            for (int i = 0; i < arrayList.size(); i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();

        } else if (AudioFlag.equalsIgnoreCase("ViewAllAudioList")) {
            Type type = new TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail>>() {
            }.getType();
            ArrayList<ViewAllAudioListModel.ResponseData.Detail> arrayList = gson.fromJson(json1, type);
            if (arrayList.get(position).getAudioFile().equalsIgnoreCase("")) {
                arrayList.remove(position);
            }
            for (int i = 0; i < arrayList.size(); i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
        } else if (AudioFlag.equalsIgnoreCase("SearchModelAudio")) {
            Type type = new TypeToken<ArrayList<SearchBothModel.ResponseData>>() {
            }.getType();
            ArrayList<SearchBothModel.ResponseData> arrayList = gson.fromJson(json1, type);
            if (arrayList.get(0).getAudioFile().equalsIgnoreCase("")) {
                arrayList.remove(0);
            }
            for (int i = 0; i < arrayList.size(); i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
        } else if (AudioFlag.equalsIgnoreCase("SearchAudio")) {
            Type type = new TypeToken<ArrayList<SuggestedModel.ResponseData>>() {
            }.getType();
            ArrayList<SuggestedModel.ResponseData> arrayList = gson.fromJson(json1, type);
            if (arrayList.get(0).getAudioFile().equalsIgnoreCase("")) {
                arrayList.remove(0);
            }
            for (int i = 0; i < arrayList.size(); i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
        } else if (AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
            Type type = new TypeToken<ArrayList<AppointmentDetailModel.Audio>>() {
            }.getType();
            ArrayList<AppointmentDetailModel.Audio> arrayList = gson.fromJson(json1, type);
            if (arrayList.get(0).getAudioFile().equalsIgnoreCase("")) {
                arrayList.remove(0);
            }
            for (int i = 0; i < arrayList.size(); i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
        } else if (AudioFlag.equalsIgnoreCase("LikeAudioList")) {
            Type type = new TypeToken<ArrayList<LikesHistoryModel.ResponseData.Audio>>() {
            }.getType();
            ArrayList<LikesHistoryModel.ResponseData.Audio> arrayList = gson.fromJson(json1, type);
            if (arrayList.get(position).getAudioFile().equalsIgnoreCase("")) {
                arrayList.remove(position);
            }
            for (int i = 0; i < arrayList.size(); i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
        } else if (AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
            Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
            }.getType();
            ArrayList<DownloadAudioDetails> arrayList = gson.fromJson(json1, type);
            if (arrayList.get(position).getAudioFile().equalsIgnoreCase("")) {
                arrayList.remove(position);
            }
            for (int i = 0; i < arrayList.size(); i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
        } else if (AudioFlag.equalsIgnoreCase("Downloadlist")) {
            Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
            }.getType();
            ArrayList<DownloadAudioDetails> arrayList = gson.fromJson(json1, type);
            if (arrayList.get(position).getAudioFile().equalsIgnoreCase("")) {
                arrayList.remove(position);
            }
            for (int i = 0; i < arrayList.size(); i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID(arrayList.get(i).getPlaylistId());
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
        } else if (AudioFlag.equalsIgnoreCase("TopCategories")) {
            Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
            }.getType();
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json1, type);
            if (arrayList.get(position).getAudioFile().equalsIgnoreCase("")) {
                arrayList.remove(position);
            }
            for (int i = 0; i < arrayList.size(); i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
        } else if (AudioFlag.equalsIgnoreCase("SubPlayList")) {
            Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
            }.getType();
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json1, type);
            if (arrayList.get(position).getAudioFile().equalsIgnoreCase("")) {
                arrayList.remove(position);
            }
            for (int i = 0; i < arrayList.size(); i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID(arrayList.get(i).getPlaylistID());
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
        }
        MakeArray();
    }

    private void MakeArray() {
        Gson gson = new Gson();
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
        String json = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson));
        String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        MainPlayModel mainPlayModel;
        mainPlayModelList = new ArrayList<>();
        position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
        String json2 = shared.getString(CONSTANTS.PREF_KEY_queueList, String.valueOf(gson));
        if (!json2.equalsIgnoreCase(String.valueOf(gson))) {
            Type type1 = new TypeToken<ArrayList<AddToQueueModel>>() {
            }.getType();
            addToQueueModelList = gson.fromJson(json2, type1);
        }
        queuePlay = shared.getBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
        audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
        if(queuePlay){
            playFrom = "queuePlay";
        }else if (audioPlay){
            playFrom = "audioPlay";
        }else{
            playFrom = "audioPlay";
        }
        if (AudioFlag.equalsIgnoreCase("MainAudioList")) {
            Type type = new TypeToken<ArrayList<MainAudioModel.ResponseData.Detail>>() {
            }.getType();
            ArrayList<MainAudioModel.ResponseData.Detail> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData(position);

        } else if (AudioFlag.equalsIgnoreCase("ViewAllAudioList")) {
            Type type = new TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail>>() {
            }.getType();
            ArrayList<ViewAllAudioListModel.ResponseData.Detail> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData(position);
        } else if (AudioFlag.equalsIgnoreCase("SearchAudio")) {
            Type type = new TypeToken<ArrayList<SuggestedModel.ResponseData>>() {
            }.getType();
            ArrayList<SuggestedModel.ResponseData> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData(position);
        } else if (AudioFlag.equalsIgnoreCase("SearchModelAudio")) {
            Type type = new TypeToken<ArrayList<SearchBothModel.ResponseData>>() {
            }.getType();
            ArrayList<SearchBothModel.ResponseData> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData(position);
        } else if (AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
            Type type = new TypeToken<ArrayList<AppointmentDetailModel.Audio>>() {
            }.getType();
            ArrayList<AppointmentDetailModel.Audio> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData(position);
        } else if (AudioFlag.equalsIgnoreCase("LikeAudioList")) {
            Type type = new TypeToken<ArrayList<LikesHistoryModel.ResponseData.Audio>>() {
            }.getType();
            ArrayList<LikesHistoryModel.ResponseData.Audio> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData(position);
        } else if (AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
            Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
            }.getType();
            ArrayList<DownloadAudioDetails> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData(position);
        } else if (AudioFlag.equalsIgnoreCase("Downloadlist")) {
            Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
            }.getType();
            ArrayList<DownloadAudioDetails> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();

            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID(arrayList.get(i).getPlaylistId());
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData(position);
        } else if (AudioFlag.equalsIgnoreCase("TopCategories")) {
            Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
            }.getType();
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();

            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData(position);
        } else if (AudioFlag.equalsIgnoreCase("SubPlayList")) {
            Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
            }.getType();
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
//            if (isDisclaimer == 0 && disclaimerPlayed == 0) {
//                addDeclaimer();
//            }
            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID(arrayList.get(i).getPlaylistID());
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json1 = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json1);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData(position);
        }
    }

    private void setInIt(String name, String audiomastercat, String imageFile, String audioDuration) {
        binding.tvTitle.setText(name);
        binding.tvName.setText(name);
        binding.tvCategory.setText(audiomastercat);
        if (url.equalsIgnoreCase("")) {
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.135f, 0);
            binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(R.drawable.disclaimer).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
        } else {
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.14f, 0);
            binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(imageFile).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
        }
        if (url.equalsIgnoreCase("") || url.isEmpty()) {
            isDisclaimer = 1;
            callAllDisable(false);

        } else {
            isDisclaimer = 0;
            callAllDisable(true);
        }
        binding.tvTime.setText(audioDuration);
    }

    private void addToRecentPlay() {
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        String UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        try {
            if (BWSApplication.isNetworkConnected(ctx)) {
                BWSApplication.showProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
                Call<SucessModel> listCall = APIClient.getClient().getRecentlyplayed(id, UserID);
                listCall.enqueue(new Callback<SucessModel>() {
                    @Override
                    public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
                            SucessModel model = response.body();
                        }
                    }

                    @Override
                    public void onFailure(Call<SucessModel> call, Throwable t) {
                        BWSApplication.hideProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
                    }
                });
            } else {
//            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callBack() {
        handler.removeCallbacks(UpdateSongTime);
        if (ComeFromQueue.equalsIgnoreCase("1")) {
            Intent i = new Intent(ctx, AddQueueActivity.class);
            i.putExtra("ID", AudioId);
            i.putExtra("play", play);
            startActivity(i);
            finish();
        } else if (ComeFromQueue.equalsIgnoreCase("0") ||
                ComeFromQueue.equalsIgnoreCase("")) {
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
    }

    private void callAllDisable(boolean b) {
        if (b) {
            binding.llnext.setClickable(true);
            binding.llnext.setEnabled(true);
            binding.llnext.setAlpha(1f);
            binding.llprev.setClickable(true);
            binding.llprev.setEnabled(true);
            binding.llprev.setAlpha(1f);
            binding.simpleSeekbar.setClickable(true);
            binding.simpleSeekbar.setEnabled(true);
//            binding.simpleSeekbar.set
        } else {
            binding.llnext.setClickable(false);
            binding.llnext.setEnabled(false);
            binding.llnext.setAlpha(0.6f);
            binding.llprev.setClickable(false);
            binding.llprev.setEnabled(false);
            binding.llprev.setAlpha(0.6f);
            binding.simpleSeekbar.setClickable(false);
            binding.simpleSeekbar.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
   /*     if (isPrepare && !isMediaStart) {
            callMedia();
        } else if ((isMediaStart || isPlaying()) && !isPause) {
            binding.llPlay.setVisibility(View.GONE);
            binding.llPause.setVisibility(View.VISIBLE);
            binding.llProgressBar.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
        } else {
            binding.llPlay.setVisibility(View.VISIBLE);
            binding.llPause.setVisibility(View.GONE);
            binding.llProgressBar.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
        }*/
        if (url.equalsIgnoreCase("") || url.isEmpty()) {
            isDisclaimer = 1;
            callAllDisable(false);

        } else {
            isDisclaimer = 0;
            callAllDisable(true);
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        callBack();
        super.onBackPressed();
    }

    public void updateProgressBar() {
        handler.postDelayed(UpdateSongTime, 500);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
     /*   handler.removeCallbacks(UpdateSongTime);
        if (isMediaStart) {
            int totalDuration = getEndTime();
            int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);

            // forward or backward to certain seconds
            SeekTo(currentPosition);
        }
        // update timer progress again
        updateProgressBar();*/
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

        oTime = binding.simpleSeekbar.getProgress();
        // forward or backward to certain seconds
        SeekTo(currentPosition);
        // update timer progress again
        updateProgressBar();
    }

    @Override
    public void requestDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
    }

 /*   @Override
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

    public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.MyViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {
        ArrayList<AddToQueueModel> listModelList;
        Context ctx;
        StartDragListener startDragListener;

        public QueueAdapter(ArrayList<AddToQueueModel> listModelList, Context ctx, StartDragListener startDragListener) {
            this.listModelList = listModelList;
            this.ctx = ctx;
            this.startDragListener = startDragListener;

        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            QueueListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.queue_list_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position1) {
            AddToQueueModel listModel = listModelList.get(position1);

            holder.binding.tvTitle.setText(listModel.getName());
            holder.binding.tvTime.setText(listModel.getAudioDuration());
            binding.tvCategory.setText(listModel.getAudiomastercat());

            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(listModel.getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

            holder.binding.llRemove.setOnClickListener(view -> callRemoveList(position1, "0"));
            holder.binding.llSort.setOnTouchListener((v, event) -> {
                if (event.getAction() ==
                        MotionEvent.ACTION_DOWN) {
                    startDragListener.requestDrag(holder);
                }
                if (event.getAction() ==
                        MotionEvent.ACTION_UP) {
                    startDragListener.requestDrag(holder);
                }
                return false;
            });
            holder.binding.llMainLayout.setOnClickListener(view -> {
                if (BWSApplication.isNetworkConnected(ctx)) {
                    if (isPrepare || isMediaStart || isPause) {
                        stopMedia();
                    }
                    isPause = false;
                    isPrepare = false;
                    isMediaStart = false;

                    setInIt(listModel.getName(), listModel.getAudiomastercat(),
                            listModel.getImageFile(), listModel.getAudioDuration());
                    if (queuePlay) {
                        for (int i = 0; i < addToQueueModelList.size(); i++) {
                            if (addToQueueModelList.get(i).getName().equalsIgnoreCase(StrigRemoveName)) {
                                addToQueueModelList.remove(i);
                                break;
                            }
                        }
                    }
                    savePrefQueue(position1, true, false, addToQueueModelList, ctx);
                    position = position1;
                    getPrepareShowData(position);
                    callRemoveList1(position1);
                } else {
                    BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                }
            });
        }

        public void callRemoveList1(int position) {
            listModelList.remove(position);
            notifyItemRemoved(position);
        }

        public void callRemoveList(int position1, String s) {
            for (int i = 0; i < addToQueueModelList.size(); i++) {
                if (addToQueueModelList.get(i).getName().equalsIgnoreCase(binding.tvName.getText().toString())) {
                    addToQueueModelList.remove(i);
                    break;
                }
            }
//            if(s.equalsIgnoreCase("1")) {
//                setInIt(listModelList.get(position).getName(), listModelList.get(position).getAudiomastercat(),
//                        listModelList.get(position).getImageFile(), listModelList.get(position).getAudioDuration());
//            }
            if (position1 == listModelList.size()) {
                position1 = position1 - 1;
            }
            listModelList.remove(position1);
       /*     for (int i = 0; i < addToQueueModelList.size(); i++) {
                if (addToQueueModelList.get(i).getName().equalsIgnoreCase(Name))
                    addToQueueModelList.remove(i);
            }*/

            notifyItemRemoved(position1);
            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String json = gson.toJson(addToQueueModelList);
            editor.putString(CONSTANTS.PREF_KEY_queueList, json);
            editor.commit();
            if (s.equalsIgnoreCase("0")) {
                BWSApplication.showToast("Removed from the queue", ctx);
            }
            addToQueueModelList2 = listModelList;
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
            String json = "";
            if (queuePlay && !addSong) {
                ArrayList<AddToQueueModel> listModelList1 = new ArrayList<>();
                listModelList1.clear();
                listModelList1 = new ArrayList<>();
                listModelList1.addAll(listModelList);
                listModelList1.add(addToQueueModelList.get(mypos));
                addSong = true;
                json = gson.toJson(listModelList1);
            } else {
                json = gson.toJson(listModelList);
            }
            editor.putString(CONSTANTS.PREF_KEY_queueList, json);
            editor.commit();
            addToQueueModelList2 = listModelList;
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
    public void onTrackPrevious() {
        if (!url.equalsIgnoreCase("")) {
            isPlaying = false;
            callPrevious();
        }

        BWSApplication.createChannel(ctx);
        registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
    }

    @Override
    public void onTrackPlay() {
        BWSApplication.createNotification(ctx, mainPlayModelList.get(position),
                R.drawable.ic_pause_black_24dp, position, mainPlayModelList.size() - 1);
        if (!isMediaStart) {
            isCompleteStop = false;
            isprogressbar = true;
            handler.postDelayed(UpdateSongTime, 500);
            binding.llPlay.setVisibility(View.GONE);
            binding.llPause.setVisibility(View.GONE);
            binding.llProgressBar.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
            callMedia();
        } else if (isCompleteStop) {
            isCompleteStop = false;
            isprogressbar = true;
            handler.postDelayed(UpdateSongTime, 500);
            binding.llPlay.setVisibility(View.GONE);
            binding.llPause.setVisibility(View.GONE);
            binding.llProgressBar.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
            callMedia();
        } else {
            binding.llPlay.setVisibility(View.GONE);
            binding.llPause.setVisibility(View.VISIBLE);
            binding.llProgressBar.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
            resumeMedia();
            isPause = false;
        }
        handler.postDelayed(UpdateSongTime, 100);
        binding.tvTitle.setText(mainPlayModelList.get(position).getName());
        isPlaying = true;
    }

    @Override
    public void onTrackPause() {
        BWSApplication.createNotification(ctx, mainPlayModelList.get(position),
                R.drawable.ic_play_arrow_black_24dp, position, mainPlayModelList.size() - 1);
        isPlaying = false;
        handler.removeCallbacks(UpdateSongTime);
        binding.simpleSeekbar.setProgress(binding.simpleSeekbar.getProgress());
        pauseMedia();
        binding.llProgressBar.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.GONE);
        binding.llPlay.setVisibility(View.VISIBLE);
        binding.llPause.setVisibility(View.GONE);
        oTime = binding.simpleSeekbar.getProgress();
    }

    @Override
    public void onTrackNext() {
        if (!url.equalsIgnoreCase("")) {
            isPlaying = false;
            callNext();
        }
        BWSApplication.createChannel(ctx);
        registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
    }*/
}