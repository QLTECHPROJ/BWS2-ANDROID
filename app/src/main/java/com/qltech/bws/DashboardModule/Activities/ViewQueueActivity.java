package com.qltech.bws.DashboardModule.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Models.AddToQueueModel;
import com.qltech.bws.DashboardModule.Models.SucessModel;
import com.qltech.bws.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.qltech.bws.EncryptDecryptUtils.DownloadMedia;
import com.qltech.bws.EncryptDecryptUtils.FileUtils;
import com.qltech.bws.R;
import com.qltech.bws.RoomDataBase.DatabaseClient;
import com.qltech.bws.RoomDataBase.DownloadAudioDetails;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.ItemMoveCallback;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.Utility.StartDragListener;
import com.qltech.bws.databinding.ActivityViewQueueBinding;
import com.qltech.bws.databinding.QueueListLayoutBinding;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import static com.qltech.bws.Utility.MusicService.progressToTimer;
import static com.qltech.bws.Utility.MusicService.resumeMedia;
import static com.qltech.bws.Utility.MusicService.savePrefQueue;
import static com.qltech.bws.Utility.MusicService.stopMedia;

public class ViewQueueActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,/* AudioManager.OnAudioFocusChangeListener,*/ StartDragListener {
    ActivityViewQueueBinding binding;
    int position, listSize, startTime = 0;
    String IsRepeat, IsShuffle, id, AudioId = "", ComeFromQueue = "", play = "", url, name;
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
    int mypos = 0;
    long totalDuration;
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
            long currentDuration = getStartTime();

            int progress = getProgressPercentage(currentDuration, totalDuration);
            long diff = totalDuration - currentDuration;
            if (diff < 15) {
                callComplete();
            }
            if (currentDuration == totalDuration && currentDuration != getStartTime()) {
                callComplete();
            } else if (isPause) {
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
        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                1, 1, 0.14f, 0);
        binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        getPrepareShowData(position);
        binding.simpleSeekbar.setOnSeekBarChangeListener(this);
        callAdapterMethod();
        binding.llNowPlaying.setOnClickListener(view -> {
            if (binding.llPause.getVisibility() == View.VISIBLE) {
                isPause = false;
            }
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
        });

        binding.llPause.setOnClickListener(view -> {
            handler.removeCallbacks(UpdateSongTime);
            binding.simpleSeekbar.setProgress(binding.simpleSeekbar.getProgress());
            pauseMedia();
            binding.llPlay.setVisibility(View.VISIBLE);
            binding.llPause.setVisibility(View.GONE);
            binding.llProgressBar.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
            oTime = binding.simpleSeekbar.getProgress();
        });

        binding.llPlay.setOnClickListener(view -> {
            binding.llPlay.setVisibility(View.GONE);
            binding.llPause.setVisibility(View.VISIBLE);
            binding.llProgressBar.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
            resumeMedia();
            isPause = false;
            handler.postDelayed(UpdateSongTime, 500);
        });

        binding.llnext.setOnClickListener(view -> {

            if (BWSApplication.isNetworkConnected(ctx)) {
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
                    if (queuePlay) {
                        adapter.callRemoveList(position);
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
                        adapter.callRemoveList(position);
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
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), ctx);
            }
        });

        binding.llprev.setOnClickListener(view -> {
            if (BWSApplication.isNetworkConnected(ctx)) {
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
                    if (queuePlay) {
                        adapter.callRemoveList(position);
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
                        adapter.callRemoveList(position);
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
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), ctx);
            }
        });
    }

    private void callAdapterMethod() {
        if (addToQueueModelList.size() != 0) {
            if (queuePlay) {
                if (addToQueueModelList.get(position).getName().equalsIgnoreCase(binding.tvName.getText().toString())) {
                    mypos = position;
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
                        .getaudioByPlaylist(url, PlaylistId);
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
        addToRecentPlay();
        binding.simpleSeekbar.setClickable(true);
        handler.postDelayed(UpdateSongTime, 500);
        if (isMediaStart) {
            mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                callComplete();
            });
        }
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(CONSTANTS.PREF_KEY_position, position);
        editor.commit();
        BWSApplication.hideProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
    }

    private void setMediaPlayer(String download, FileDescriptor fileDescriptor) {
        if (null == mediaPlayer) {
            mediaPlayer = new MediaPlayer();
            Log.e("Playinggggg", "Playinggggg");
        }
        try {
            if (mediaPlayer == null)
                mediaPlayer = new MediaPlayer();
            if (mediaPlayer.isPlaying()) {
                Log.e("Playinggggg", "stoppppp");
                mediaPlayer.stop();
                isMediaStart = false;
                isPrepare = false;
            }
            mediaPlayer = new MediaPlayer();
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
            } else {
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
                    if (listSize == 1) {
                        binding.llPlay.setVisibility(View.VISIBLE);
                        binding.llPause.setVisibility(View.GONE);
                        stopMedia();
                    } else {
                        position = 0;
                        getPrepareShowData(position);
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
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
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

    @Override
    protected void onResume() {
        if (isPrepare && !isMediaStart) {
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
                    1, 1, 0.12f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(listModel.getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

            holder.binding.llRemove.setOnClickListener(view -> callRemoveList(position1));
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
                    if (queuePlay)
                        addToQueueModelList.remove(mypos);
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
            notifyDataSetChanged();
        }

        public void callRemoveList(int position1) {
            for (int i = 0; i < addToQueueModelList.size(); i++) {
                if (addToQueueModelList.get(i).getName().equalsIgnoreCase(binding.tvName.getText().toString()))
                    addToQueueModelList.remove(i);
            }
            setInIt(listModelList.get(position).getName(), listModelList.get(position).getAudiomastercat(),
                    listModelList.get(position).getImageFile(), listModelList.get(position).getAudioDuration());
            String Name = listModelList.get(position1).getName();
            listModelList.remove(position1);
       /*     for (int i = 0; i < addToQueueModelList.size(); i++) {
                if (addToQueueModelList.get(i).getName().equalsIgnoreCase(Name))
                    addToQueueModelList.remove(i);
            }*/
            notifyDataSetChanged();
            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String json = gson.toJson(addToQueueModelList);
            editor.putString(CONSTANTS.PREF_KEY_queueList, json);
            editor.commit();
            BWSApplication.showToast("The audio has been removed from the queue", ctx);
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
            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String json = "";
            if (queuePlay) {
                ArrayList<AddToQueueModel> listModelList1 = new ArrayList<>();
                listModelList1.clear();
                listModelList1 = listModelList;
                listModelList1.add(addToQueueModelList.get(mypos));
                json = gson.toJson(listModelList1);
            } else {
                json = gson.toJson(listModelList);
            }
            editor.putString(CONSTANTS.PREF_KEY_queueList, json);
            editor.commit();
            addToQueueModelList2 = listModelList;
            notifyItemMoved(fromPosition, toPosition);


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