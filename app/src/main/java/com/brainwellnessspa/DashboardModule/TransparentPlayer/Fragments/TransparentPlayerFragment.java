package com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Activities.PlayWellnessActivity;
import com.brainwellnessspa.DashboardModule.Models.AddToQueueModel;
import com.brainwellnessspa.DashboardModule.Models.AppointmentDetailModel;
import com.brainwellnessspa.DashboardModule.Models.MainAudioModel;
import com.brainwellnessspa.DashboardModule.Models.SearchBothModel;
import com.brainwellnessspa.DashboardModule.Models.SubPlayListModel;
import com.brainwellnessspa.DashboardModule.Models.SucessModel;
import com.brainwellnessspa.DashboardModule.Models.SuggestedModel;
import com.brainwellnessspa.DashboardModule.Models.ViewAllAudioListModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia;
import com.brainwellnessspa.EncryptDecryptUtils.FileUtils;
import com.brainwellnessspa.LikeModule.Models.LikesHistoryModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MusicService;
import com.brainwellnessspa.Utility.MyService;
import com.brainwellnessspa.Utility.PlaybackStatus;
import com.brainwellnessspa.databinding.FragmentTransparentPlayerBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.POWER_SERVICE;
import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.ComeScreenAccount;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.player;
import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;
import static com.brainwellnessspa.Utility.MusicService.Broadcast_PLAY_NEW_AUDIO;
import static com.brainwellnessspa.Utility.MusicService.Broadcast_PLAY_PAUSE;
import static com.brainwellnessspa.Utility.MusicService.SeekTo;
import static com.brainwellnessspa.Utility.MusicService.buildNotification;
import static com.brainwellnessspa.Utility.MusicService.getEndTime;
import static com.brainwellnessspa.Utility.MusicService.getProgressPercentage;
import static com.brainwellnessspa.Utility.MusicService.getStartTime;
import static com.brainwellnessspa.Utility.MusicService.isCompleteStop;
import static com.brainwellnessspa.Utility.MusicService.isMediaStart;
import static com.brainwellnessspa.Utility.MusicService.isPause;
import static com.brainwellnessspa.Utility.MusicService.isPrepare;
import static com.brainwellnessspa.Utility.MusicService.isPreparing;
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

public class TransparentPlayerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener/*, Playable, AudioManager.OnAudioFocusChangeListener*/ {
    public static int isDisclaimer = 0;
    public static String addToRecentPlayId = "", myAudioId = "";
    public static boolean isPlaying = false;
    public ArrayList<MainPlayModel> mainPlayModelList;
    public FragmentTransparentPlayerBinding binding;
    String UserID, AudioFlag, IsRepeat, IsShuffle, audioFile, id, name;
    int position = 0, startTime = 0, listSize = 0, myCount = 0;
    MainPlayModel mainPlayModel;
    Boolean queuePlay, audioPlay;
    ArrayList<AddToQueueModel> addToQueueModelList;
    List<DownloadAudioDetails> downloadAudioDetailsList;
    Activity activity;
    Context ctx;
    long myProgress = 0, diff = 0;
    SharedPreferences shared;
    String json;
    Gson gson;
    LocalBroadcastManager localBroadcastManager;
    Intent localIntent;
    private long totalDuration, currentDuration = 0;
    private Handler handler12;
    private long mLastClickTime = 0;
    /*    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getExtras().getString("actionname");
                switch (action) {
                    case BWSApplication.ACTION_PREVIUOS:
                        onTrackPrevious();
                        break;
                    case ACTION_PLAY:
                        if (isPlaying) {
                            onTrackPause();
                        } else {
                            onTrackPlay();
                        }
                        break;
                    case BWSApplication.ACTION_NEXT:
                        onTrackNext();
                        break;
                }
            }
        };*/
    private BroadcastReceiver playNewAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isPause || !isMediaStart) {
                binding.ivPlay.setVisibility(View.VISIBLE);
                binding.ivPause.setVisibility(View.GONE);

                localIntent.putExtra("MyData", "pause");
                localBroadcastManager.sendBroadcast(localIntent);
                buildNotification(PlaybackStatus.PAUSED, context, mainPlayModelList.get(position));
            } else {
                binding.ivPause.setVisibility(View.VISIBLE);
                binding.ivPlay.setVisibility(View.GONE);

                localIntent.putExtra("MyData", "pause");
                localBroadcastManager.sendBroadcast(localIntent);
                buildNotification(PlaybackStatus.PLAYING, context, mainPlayModelList.get(position));
            }
        }
    };
    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
    private Runnable UpdateSongTime12 = new Runnable() {
        @Override
        public void run() {
            try {
                startTime = getStartTime();
                binding.simpleSeekbar.setMax(100);
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
                        binding.progressBar.setVisibility(View.GONE);
                        binding.ivPause.setVisibility(View.GONE);
                        binding.ivPlay.setVisibility(View.VISIBLE);
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
                    if (mediaPlayer != null) {
                        totalDuration = mediaPlayer.getDuration();
                    } else
                        totalDuration = t.getTime();
                } else {
                    if (mediaPlayer != null) {
                        totalDuration = mediaPlayer.getDuration();
                    } else {
                        totalDuration = t.getTime();
                    }
                }

                if (isMediaStart) {
                    mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                        if(mediaPlayer.isPlaying()) {
                            callComplete();
                        }
                    });
                }
                myProgress = currentDuration;
                currentDuration = getStartTime();
                diff = totalDuration - myProgress;

//                Log.e("myProgress old!!!",String.valueOf(myProgress));
                if (myProgress == currentDuration && myProgress != 0 && !isPause && audioFile.equalsIgnoreCase("")) {
//                    Log.e("myProgress",String.valueOf(myProgress));
                    myCount++;
                    Log.e("myCount", String.valueOf(myCount));

                    if (myCount == 5) {
                        Log.e("myCount complete", String.valueOf(myCount));
                        callComplete();
                        Log.e("calll complete errr", "eee");
                        myCount = 0;
                    }
                } else if (myProgress == currentDuration && myProgress != 0 && !isPause && diff < 500) {
//                    Log.e("myProgress",String.valueOf(myProgress));
                    myCount++;
                    Log.e("myCount", String.valueOf(myCount));

                    if (myCount == 20) {
                        Log.e("myCount complete", String.valueOf(myCount));
                        callComplete();
                        Log.e("calll complete errr", "eee");
                        myCount = 0;
                    }
                }
                if (currentDuration == totalDuration && currentDuration != 0 && !isStop && !audioFile.equalsIgnoreCase("")) {
                    callComplete();
                    Log.e("calll complete trans", "trans");

                }
                int progress = (int) (getProgressPercentage(currentDuration, totalDuration));
                if (player == 1) {
                    if (currentDuration == 0 && isCompleteStop) {
                        binding.progressBar.setVisibility(View.GONE);
//                        binding.llProgress.setVisibility(View.VISIBLE);
                        binding.ivPause.setVisibility(View.GONE);
                        binding.ivPlay.setVisibility(View.VISIBLE);
                    } else if (currentDuration == 0 && !isPause) {
                        binding.progressBar.setVisibility(View.VISIBLE);
//                        binding.llProgress.setVisibility(View.VISIBLE);
                        binding.ivPause.setVisibility(View.GONE);
                        binding.ivPlay.setVisibility(View.GONE);
                    } else if (currentDuration >= 1 && !isPause) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.ivPause.setVisibility(View.VISIBLE);
                        binding.ivPlay.setVisibility(View.GONE);
                    } else if (currentDuration >= 1 && isPause) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.ivPause.setVisibility(View.GONE);
                        binding.ivPlay.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.ivPause.setVisibility(View.GONE);
                    binding.ivPlay.setVisibility(View.VISIBLE);
                }

                //Log.d("Progress", ""+progress);
                if (isPause) {
                    binding.simpleSeekbar.setProgress(oTime);
                } else {
                    binding.simpleSeekbar.setProgress(progress);
                }
                // Running this thread after 100 milliseconds
                handler12.postDelayed(this, 100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_transparent_player, container, false);
        View view = binding.getRoot();
        activity = getActivity();
        ctx = getActivity();
        mainPlayModelList = new ArrayList<>();
        addToQueueModelList = new ArrayList<>();
        downloadAudioDetailsList = new ArrayList<>();
        SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        handler12 = new Handler();
        shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        gson = new Gson();
        json = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson));
        String json1 = shared.getString(CONSTANTS.PREF_KEY_queueList, String.valueOf(gson));
        if (!json1.equalsIgnoreCase(String.valueOf(gson))) {
            Type type1 = new TypeToken<ArrayList<AddToQueueModel>>() {
            }.getType();
            addToQueueModelList = gson.fromJson(json1, type1);
        }
        try {
            ctx.getApplicationContext().startService(new Intent(ctx.getApplicationContext(), MusicService.class));
            ctx.getApplicationContext().startService(new Intent(ctx.getApplicationContext(), MyService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        IntentFilter filter = new IntentFilter(Broadcast_PLAY_NEW_AUDIO);
        getActivity().registerReceiver(playNewAudio, filter);
          localIntent = new Intent("play_pause_Action");
          localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        PowerManager powerManager = (PowerManager) ctx.getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "com.brainwellnessspa::MyWakelockTag");
        wakeLock.acquire();
        PowerManager.WakeLock wakeLock1 = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
                "com.brainwellnessspa::MyWakelockTag");
        wakeLock1.acquire();
        PowerManager.WakeLock wakeLock2 = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
                "com.brainwellnessspa::MyWakelockTag");
        wakeLock2.acquire();
        PowerManager.WakeLock wakeLock3 = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
                "com.brainwellnessspa::MyWakelockTag");
        wakeLock3.acquire();
        PowerManager.WakeLock wakeLock4 = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK,
                "com.brainwellnessspa::MyWakelockTag");
        wakeLock4.acquire();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 130);
        binding.llLayout.setLayoutParams(params);

        if (comefromDownload.equalsIgnoreCase("1")) {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            param.setMargins(0, 0, 0, 0);
            binding.llLayout.setLayoutParams(param);
        } else {
            LinearLayout.LayoutParams paramm = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramm.setMargins(0, 0, 0, 130);
            binding.llLayout.setLayoutParams(paramm);

        }
        if (isMediaStart) {
            mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                if(mediaPlayer.isPlaying()) {
                    Log.e("player to go", "::>>>>>callcomplete...");
                    callComplete();  //call....
                }
            });
        }

        queuePlay = shared.getBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
        audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
        position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        binding.simpleSeekbar.setOnSeekBarChangeListener(this);
        SharedPreferences Status = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
        IsRepeat = Status.getString(CONSTANTS.PREF_KEY_IsRepeat, "");
        IsShuffle = Status.getString(CONSTANTS.PREF_KEY_IsShuffle, "");
    /*    mAudioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);*/
        if (queuePlay) {
            getPrepareShowData();
        } else if (audioPlay) {
            MakeArray();
        }
        if (listSize == 1) {
            IsShuffle = "";
        }


//        }
        binding.ivPause.setOnClickListener(view1 -> {
            callPause();
        });

        binding.ivPlay.setOnClickListener(view12 -> {
            callPlay();
        });

        return view;
    }

    private void callPlay() {
       /* if (isPlaying) {
            onTrackPause();
        } else {
            onTrackPlay();
        }*/
        try {
            if (!isMediaStart) {
                isCompleteStop = false;
                isprogressbar = true;
                handler12.postDelayed(UpdateSongTime12, 500);
                binding.progressBar.setVisibility(View.VISIBLE);
//                binding.llProgress.setVisibility(View.GONE);
                binding.ivPlay.setVisibility(View.GONE);
                binding.ivPause.setVisibility(View.GONE);
                callMedia();
            } else if (isCompleteStop) {
                isCompleteStop = false;
                isprogressbar = true;
                handler12.postDelayed(UpdateSongTime12, 500);
                binding.progressBar.setVisibility(View.VISIBLE);
//                binding.llProgress.setVisibility(View.GONE);
                binding.ivPlay.setVisibility(View.GONE);
                binding.ivPause.setVisibility(View.GONE);
                callMedia();
            } else {
                resumeMedia();
                binding.progressBar.setVisibility(View.GONE);
//                binding.llProgress.setVisibility(View.GONE);
            binding.ivPlay.setVisibility(View.GONE);
            binding.ivPause.setVisibility(View.VISIBLE);
            isPause = false;
        }
        player = 1;
        buildNotification(PlaybackStatus.PLAYING, ctx, mainPlayModelList.get(position));

        localIntent.putExtra("MyData", "play");
        localBroadcastManager.sendBroadcast(localIntent);
        handler12.postDelayed(UpdateSongTime12, 100);
        /*Intent intent = new Intent();
        intent.setAction("com.brainwellnessspa.Broadcast");
        intent.putExtra("MyData", "play");
        getActivity().sendBroadcast(intent);*/
//        getActivity().registerReceiver(broadcastReceiver_playPause, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callPause() {
        handler12.removeCallbacks(UpdateSongTime12);
        binding.simpleSeekbar.setProgress(binding.simpleSeekbar.getProgress());
      /*  if (isPlaying) {
            onTrackPause();
        } else {
            onTrackPlay();
        }*/
        if (!isMediaStart) {
//                callAsyncTask();
            callMedia();
        } else {
            pauseMedia();
            binding.ivPause.setVisibility(View.GONE);
            binding.ivPlay.setVisibility(View.VISIBLE);
        }
        oTime = binding.simpleSeekbar.getProgress();
        buildNotification(PlaybackStatus.PAUSED, ctx, mainPlayModelList.get(position));

        localIntent.putExtra("MyData", "pause");
        localBroadcastManager.sendBroadcast(localIntent);
       /* Intent intent = new Intent();
        intent.setAction("com.brainwellnessspa.Broadcast");
        intent.putExtra("MyData", "pause");
        getActivity().sendBroadcast(intent); */
//        getActivity().registerReceiver(broadcastReceiver_playPause, filter);

    }

   /* @Override
    public void onTrackPrevious() {
        if (!audioFile.equalsIgnoreCase("")) {
            if (isPlaying) {
                onTrackPause();
            } else {
                onTrackPlay();
            }
            isPlaying = false;
            callPrev();
        }


//        position--;
//        BWSApplication.createNotification(getActivity(), mainPlayModelList.get(position),
//                R.drawable.ic_pause_black_24dp, position, mainPlayModelList.size() - 1);
//        binding.tvTitle.setText(mainPlayModelList.get(position).getName());
    }
*/

   /* @Override
    public void onTrackPlay() {
//        if (isPlaying) {
//            BWSApplication.createNotification(getActivity(), mainPlayModelList.get(position),
//                    R.drawable.ic_play_arrow_black_24dp, position, mainPlayModelList.size() - 1);
//            binding.ivPause.setImageResource(R.drawable.ic_play_icon);
//            binding.tvTitle.setText(mainPlayModelList.get(position).getName());
//            isPlaying = false;
//        } else {
        BWSApplication.createNotification(getActivity(), mainPlayModelList.get(position),
                R.drawable.ic_pause_black_24dp, position, mainPlayModelList.size() - 1);
//            binding.ivPlay.setImageResource(R.drawable.ic_all_pause_icon);
        if (!isMediaStart) {
            isCompleteStop = false;
            isprogressbar = true;
//            handler12.postDelayed(UpdateSongTime12, 500);
            binding.progressBar.setVisibility(View.VISIBLE);
//                binding.llProgress.setVisibility(View.GONE);
            binding.ivPlay.setVisibility(View.GONE);
            binding.ivPause.setVisibility(View.GONE);
            callMedia();
        } else if (isCompleteStop) {
            isCompleteStop = false;
            isprogressbar = true;
//            handler12.postDelayed(UpdateSongTime12, 500);
            binding.progressBar.setVisibility(View.VISIBLE);
//                binding.llProgress.setVisibility(View.GONE);
            binding.ivPlay.setVisibility(View.GONE);
            binding.ivPause.setVisibility(View.GONE);
            callMedia();
        } else {
            resumeMedia();
            binding.progressBar.setVisibility(View.GONE);
//                binding.llProgress.setVisibility(View.GONE);
            binding.ivPlay.setVisibility(View.GONE);
            binding.ivPause.setVisibility(View.VISIBLE);
            isPause = false;
        }
        player = 1;
//        handler12.postDelayed(UpdateSongTime12, 100);
        binding.tvTitle.setText(mainPlayModelList.get(position).getName());
        isPlaying = true;
//        }
    }
*/

   /* @Override
    public void onTrackPause() {
//        if (isPlaying) {
        BWSApplication.createNotification(getActivity(), mainPlayModelList.get(position),
                R.drawable.ic_play_arrow_black_24dp, position, mainPlayModelList.size() - 1);
//            binding.ivPause.setImageResource(R.drawable.ic_play_icon);
        isPlaying = false;
        if (!isMediaStart) {
//                callAsyncTask();
            callMedia();
        } else {
            pauseMedia();
            binding.ivPause.setVisibility(View.GONE);
            binding.ivPlay.setVisibility(View.VISIBLE);
        }
//        } else {
//            BWSApplication.createNotification(getActivity(), mainPlayModelList.get(position),
//                    R.drawable.ic_pause_black_24dp, position, mainPlayModelList.size() - 1);
//            binding.ivPlay.setImageResource(R.drawable.ic_all_pause_icon);
//            binding.tvTitle.setText(mainPlayModelList.get(position).getName());
//            isPlaying = true;
//        }
    }*/

  /*  @Override
    public void onTrackNext() {
        if (!audioFile.equalsIgnoreCase("")) {
            if (isPlaying) {
                onTrackPause();
            } else {
                onTrackPlay();
            }
            isPlaying = false;
            callNext();
        }
//        position++;
//        BWSApplication.createNotification(getActivity(), mainPlayModelList.get(position),
//                R.drawable.ic_pause_black_24dp, position, mainPlayModelList.size() - 1);
//        binding.tvTitle.setText(mainPlayModelList.get(position).getName());
    }*/

    private void MakeArray() {
        shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
        json = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson));
        mainPlayModelList = new ArrayList<>();
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
            getPrepareShowData();

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
            getPrepareShowData();
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
            getPrepareShowData();
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
            getPrepareShowData();
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
            getPrepareShowData();
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
            getPrepareShowData();
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
            getPrepareShowData();
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
            getPrepareShowData();
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
            getPrepareShowData();
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
            String json = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData();
        }
    }

    private void callPrev() {
        if (isPrepare || isMediaStart || isPause) {
            stopMedia();
        }
        isMediaStart = false;
        isPrepare = false;
        isPause = false;
        isCompleteStop = false;
        if (IsRepeat.equalsIgnoreCase("1") || IsRepeat.equalsIgnoreCase("0")) {
            // repeat is on play same song again
            if (position > 0) {
                position = position - 1;
                getPrepareShowData();
            } else if (listSize != 1) {
                position = listSize - 1;
                getPrepareShowData();
            }
        }/* else if (IsRepeat.equalsIgnoreCase("0")) {
                getPrepareShowData(position);
            }*/ else if (IsShuffle.equalsIgnoreCase("1")) {
            // shuffle is on - play a random song
            if (queuePlay) {
                if (BWSApplication.isNetworkConnected(ctx)) {
                    addToQueueModelList.remove(position);
                    listSize = addToQueueModelList.size();
                    if (listSize == 0) {
                        stopMedia();
                    } else if (listSize == 1) {
                        stopMedia();
                    } else {
                        Random random = new Random();
                        position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                        getPrepareShowData();
                    }
                } else {
                    BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                }
            } else {
                Random random = new Random();
                position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                getPrepareShowData();
            }
        } else {
            if (queuePlay) {
                if (BWSApplication.isNetworkConnected(ctx)) {
                    addToQueueModelList.remove(position);
                    listSize = addToQueueModelList.size();
                    if (position > 0) {
                        position = position - 1;
                        getPrepareShowData();
                    } else {
                        if (listSize == 0) {
                            savePrefQueue(0, false, true, addToQueueModelList, ctx);
                            binding.ivPlay.setVisibility(View.VISIBLE);
                            binding.ivPause.setVisibility(View.GONE);
                            stopMedia();
                        } else {
                            position = 0;
                            getPrepareShowData();
                        }
                    }
                } else {
                    BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                }
            } else {
                if (position > 0) {
                    position = position - 1;

                    getPrepareShowData();
                } else if (listSize != 1) {
                    position = listSize - 1;
                    getPrepareShowData();
                }
            }
        }
    }

    private void callNext() {

        if (isPrepare || isMediaStart || isPause) {
            stopMedia();
        }
        isMediaStart = false;
        isPrepare = false;
        isPause = false;
        isCompleteStop = false;
        if (IsRepeat.equalsIgnoreCase("1") || IsRepeat.equalsIgnoreCase("0")) {
            // repeat is on play same song again
            if (position < listSize - 1) {
                position = position + 1;
            } else {
                position = 0;
            }
            getPrepareShowData();
        }/* else if (IsRepeat.equalsIgnoreCase("0")) {
                getPrepareShowData(position);
            }*/ else if (IsShuffle.equalsIgnoreCase("1")) {
            // shuffle is on - play a random song
            if (queuePlay) {
                if (BWSApplication.isNetworkConnected(ctx)) {
                    addToQueueModelList.remove(position);
                    listSize = addToQueueModelList.size();
                    if (listSize == 0) {
                        isCompleteStop = true;
                        stopMedia();
                    } else if (listSize == 1) {
                        isCompleteStop = true;
                        stopMedia();
                    } else {
                        Random random = new Random();
                        position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                        getPrepareShowData();
                    }
                } else {
                    BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                }
            } else {
                Random random = new Random();
                position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                getPrepareShowData();
            }
        } else {
            if (queuePlay) {
                if (BWSApplication.isNetworkConnected(ctx)) {
                    addToQueueModelList.remove(position);
                    listSize = addToQueueModelList.size();
                    if (position < listSize - 1) {
                        getPrepareShowData();
                    } else {
                        if (listSize == 0) {
                            savePrefQueue(0, false, true, addToQueueModelList, ctx);
                            stopMedia();
                        } else {
                            position = 0;
                            getPrepareShowData();
                        }
                    }
                } else {
                    BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                }
            } else {
                if (position < listSize - 1) {
                    position = position + 1;
                    getPrepareShowData();
                } else if (listSize != 1) {
                    position = 0;
                    getPrepareShowData();
                }
            }
        }
    }

    private void addToRecentPlay() {
        if (BWSApplication.isNetworkConnected(ctx)) {
//            BWSApplication.showProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
            Call<SucessModel> listCall = APIClient.getClient().getRecentlyplayed(id, UserID);
            listCall.enqueue(new Callback<SucessModel>() {
                @Override
                public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                    try {
                        if (response.isSuccessful()) {
//                        BWSApplication.hideProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
                            SucessModel model = response.body();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<SucessModel> call, Throwable t) {
//                    BWSApplication.hideProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
//            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    public void GetMedia(String url, Context ctx) {
        try {
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
                    try {
                        if (audioPlay) {
                            if (listSize != 0) {
                                binding.tvTitle.setText(mainPlayModelList.get(position).getName());
                                binding.tvSubTitle.setText(mainPlayModelList.get(position).getAudioDirection());
                                try {
                                    if (audioFile.equalsIgnoreCase("")) {
                                        Glide.with(ctx).load(R.drawable.disclaimer).thumbnail(0.05f)
                                                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
                                    } else {
                                        Glide.with(ctx).load(mainPlayModelList.get(position).getImageFile()).thumbnail(0.05f)
                                                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        if (player == 1) {
                            binding.progressBar.setVisibility(View.GONE);
//                    binding.llProgress.setVisibility(View.GONE);
                            if (isPause) {
                                binding.progressBar.setVisibility(View.GONE);
//                        binding.llProgress.setVisibility(View.GONE);
                                binding.ivPause.setVisibility(View.GONE);
                                binding.ivPlay.setVisibility(View.VISIBLE);
                                binding.simpleSeekbar.setProgress(oTime);
                                localIntent.putExtra("MyData", "pause");
                                localBroadcastManager.sendBroadcast(localIntent);
                            } else if (isCompleteStop) {
                                binding.progressBar.setVisibility(View.GONE);
                                binding.ivPlay.setVisibility(View.VISIBLE);
                                binding.ivPause.setVisibility(View.GONE);
                            } else if (isMediaStart && !isPause) {
                                binding.progressBar.setVisibility(View.GONE);
//                        binding.llProgress.setVisibility(View.GONE);
                                binding.ivPause.setVisibility(View.VISIBLE);
                                binding.ivPlay.setVisibility(View.GONE);
                                localIntent.putExtra("MyData", "play");
                                localBroadcastManager.sendBroadcast(localIntent);
                            } else {
                                binding.progressBar.setVisibility(View.VISIBLE);
//                        binding.llProgress.setVisibility(View.VISIBLE);
                                binding.ivPause.setVisibility(View.GONE);
                                binding.ivPlay.setVisibility(View.GONE);
                                callMedia();
                            }
                        } else {
                            binding.progressBar.setVisibility(View.GONE);

//                    binding.llProgress.setVisibility(View.GONE);
                            binding.ivPause.setVisibility(View.GONE);
                            binding.ivPlay.setVisibility(View.VISIBLE);
                            localIntent.putExtra("MyData", "pause");
                            localBroadcastManager.sendBroadcast(localIntent);
                        }
                        initMediaplyer();
                        if(isMediaStart) {
                            mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                                if (mediaPlayer.isPlaying()) {
                                    Log.e("player to go", "::>>>>>callcomplete prepare...");
                                    callComplete();  //call....
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    super.onPostExecute(aVoid);

                }
            }

            GetMedia st = new GetMedia();
            st.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPrepareShowData() {
        handler12.postDelayed(UpdateSongTime12, 100);
        try {
            if (queuePlay) {
                listSize = addToQueueModelList.size();
                if (listSize == 1) {
                    position = 0;
                }
                if (position == listSize) {
                    position = position - 1;
                }
                if (listSize != 0) {
                    id = addToQueueModelList.get(position).getID();
                    myAudioId = id;
                    name = addToQueueModelList.get(position).getName();
                    audioFile = addToQueueModelList.get(position).getAudioFile();
                    GetMedia(audioFile, ctx);
                    Glide.with(ctx).load(addToQueueModelList.get(position).getImageFile()).thumbnail(0.05f)
                            .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
                    binding.tvTitle.setText(addToQueueModelList.get(position).getName());
                    binding.tvSubTitle.setText(addToQueueModelList.get(position).getAudioDirection());
                    handler12.postDelayed(UpdateSongTime12, 100);
                }
            } else if (audioPlay) {
                listSize = mainPlayModelList.size();
                if (listSize == 1) {
                    position = 0;
                }
                if (listSize != 0) {
                    id = mainPlayModelList.get(position).getID();
                    myAudioId = id;
                    name = mainPlayModelList.get(position).getName();
                    audioFile = mainPlayModelList.get(position).getAudioFile();
                    binding.tvTitle.setText(mainPlayModelList.get(position).getName());
                    binding.tvSubTitle.setText(mainPlayModelList.get(position).getAudioDirection());
                    if (audioFile.equalsIgnoreCase("")) {
                        Glide.with(ctx).load(R.drawable.disclaimer).thumbnail(0.05f)
                                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
                    } else {
                        Glide.with(ctx).load(mainPlayModelList.get(position).getImageFile()).thumbnail(0.05f)
                                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
                    }
                    GetMedia(audioFile, ctx);
                    handler12.postDelayed(UpdateSongTime12, 100);
                    if (audioFile.equalsIgnoreCase("") || audioFile.isEmpty()) {
                        isDisclaimer = 1;
                        binding.simpleSeekbar.setClickable(false);
                        binding.simpleSeekbar.setEnabled(false);
                    } else {
                        isDisclaimer = 0;
                        binding.simpleSeekbar.setClickable(true);
                        binding.simpleSeekbar.setEnabled(true);
                    }
                }
            }

            startTime = getStartTime();

            if (!audioFile.equalsIgnoreCase("")) {
                if (!id.equalsIgnoreCase(addToRecentPlayId)) {
                    addToRecentPlay();
                    Log.e("Api call recent", id);
                }
            }
            addToRecentPlayId = id;
            Log.e("addToRecentPlayID", addToRecentPlayId);
            Log.e("new addToRecentPlayID", id);
            binding.llPlayearMain.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                handler12.removeCallbacks(UpdateSongTime12);
                if (player == 0) {
                    player = 1;
                }
                if (!isPause && binding.progressBar.getVisibility() == View.GONE) {
                    isPause = false;
                    isprogressbar = false;
                } else if (isPause && binding.progressBar.getVisibility() == View.GONE) {
                    isPause = true;
                    isprogressbar = false;
                } else if (isCompleteStop && binding.progressBar.getVisibility() == View.GONE) {
                    isprogressbar = false;
                } else if (binding.progressBar.getVisibility() == View.VISIBLE && (binding.ivPause.getVisibility() == View.GONE && binding.ivPlay.getVisibility() == View.GONE)) {
                    isprogressbar = true;
                }
                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                Gson gson = new Gson();
                String json = gson.toJson(mainPlayModelList);
                editor.putString(CONSTANTS.PREF_KEY_audioList, json);
                String json1 = gson.toJson(addToQueueModelList);
                if (queuePlay) {
                    editor.putString(CONSTANTS.PREF_KEY_queueList, json1);
                }
                editor.putInt(CONSTANTS.PREF_KEY_position, position);
                editor.commit();
                handler12.removeCallbacks(UpdateSongTime12);
                Intent i = new Intent(ctx, PlayWellnessActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                ctx.startActivity(i);
            });
                /*BWSApplication.createChannel(getActivity());
            getActivity().registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
            getActivity().startService(new Intent(getActivity().getBaseContext(), OnClearFromRecentService.class));*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMediaPlayer(String download, FileDescriptor fileDescriptor) {
        if (download.equalsIgnoreCase("2")) {
            mediaPlayer = MediaPlayer.create(getActivity(), R.raw.brain_wellness_spa_declaimer);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            initMediaplyer();
//            Uri uri = Uri.parse("android.resource://com.brainwellnessspa/" + R.raw.brain_wellness_spa_declaimer);
//            mediaPlayer.setDataSource(String.valueOf(uri));
            mediaPlayer.start();
            isPrepare = true;
            isMediaStart = true;
            binding.progressBar.setVisibility(View.GONE);
            binding.ivPause.setVisibility(View.VISIBLE);
            binding.ivPlay.setVisibility(View.GONE);
            localIntent.putExtra("MyData", "pause");
            localBroadcastManager.sendBroadcast(localIntent);
        } else {
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
                isPreparing = true;
                mediaPlayer = new MediaPlayer();
                initMediaplyer();
                if (download.equalsIgnoreCase("1")) {
                    mediaPlayer.setDataSource(fileDescriptor);
                } else {
                    mediaPlayer.setDataSource(audioFile);
                    Log.e("Playinggggxxxxx", "Startinggg1xxxxx");
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mediaPlayer.setAudioAttributes(
                            new AudioAttributes
                                    .Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .build());
                    Log.e("Playinggggg11111111", "Startinggg111111111");
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
                    localIntent.putExtra("MyData", "play");
                    localBroadcastManager.sendBroadcast(localIntent);
                });
            }
        }

        if (isPause) {
            binding.ivPlay.setVisibility(View.VISIBLE);
            binding.ivPause.setVisibility(View.GONE);
            buildNotification(PlaybackStatus.PAUSED, ctx, mainPlayModelList.get(position));
            localIntent.putExtra("MyData", "pause");
            localBroadcastManager.sendBroadcast(localIntent);
        } else {
            binding.ivPause.setVisibility(View.VISIBLE);
            binding.ivPlay.setVisibility(View.GONE);
            buildNotification(PlaybackStatus.PLAYING, ctx, mainPlayModelList.get(position));

            localIntent.putExtra("MyData", "play");
            localBroadcastManager.sendBroadcast(localIntent);
        }
        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            if(mediaPlayer.isPlaying()) {
                Log.e("player to go", "::>>>>>callcomplete...");
                callComplete();  //call....
            }
        });
    }

    private void initMediaplyer() {
        if (mediaSessionManager != null) return; //mediaSessionManager exists

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaSessionManager = (MediaSessionManager) ctx.getSystemService(Context.MEDIA_SESSION_SERVICE);
        }
        // Create a new MediaSession
        mediaSession = new MediaSessionCompat(ctx.getApplicationContext(), "AudioPlayer");
        //Get MediaSessions transport controls
        transportControls = mediaSession.getController().getTransportControls();
        //set MediaSession -> ready to receive media commands
        mediaSession.setActive(true);
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
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

                if (!audioFile.equalsIgnoreCase("")) {
                    callNext();
//                updateMetaData();
                    buildNotification(PlaybackStatus.PLAYING, ctx, mainPlayModelList.get(position));

                    localIntent.putExtra("MyData", "play");
                    localBroadcastManager.sendBroadcast(localIntent);
                }
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                if (!audioFile.equalsIgnoreCase("")) {
                    callPrev();

                    localIntent.putExtra("MyData", "play");
                    localBroadcastManager.sendBroadcast(localIntent);
//                updateMetaData();
                    buildNotification(PlaybackStatus.PLAYING, ctx, mainPlayModelList.get(position));
                }
            }


            @Override
            public void onStop() {
                super.onStop();
//                    removeNotification();
//                    //Stop the service
//                    stopSelf();
            }

/*            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
            }*/
        });

    }

    private void callMedia1() {
      /*  BWSApplication.createNotification(ctx, mainPlayModelList.get(position),
                R.drawable.ic_pause_black_24dp, position, mainPlayModelList.size() - 1);*/
        binding.progressBar.setVisibility(View.VISIBLE);
//        binding.llProgress.setVisibility(View.VISIBLE);
        binding.ivPlay.setVisibility(View.GONE);
        binding.ivPause.setVisibility(View.GONE);
        FileDescriptor fileDescriptor = null;
        if (downloadAudioDetailsList.size() != 0) {
            binding.progressBar.setVisibility(View.VISIBLE);
//        binding.llProgress.setVisibility(View.VISIBLE);
            binding.ivPlay.setVisibility(View.GONE);
            binding.ivPause.setVisibility(View.GONE);
            DownloadMedia downloadMedia = new DownloadMedia(getApplicationContext());
            try {
                byte[] decrypt = null;
                decrypt = downloadMedia.decrypt(name);
                if (decrypt != null) {
                    fileDescriptor = FileUtils.getTempFileDescriptor(getApplicationContext(), decrypt);
                    if (audioFile.equalsIgnoreCase("") || audioFile.isEmpty()) {
                        setMediaPlayer("2", fileDescriptor);
                    } else {
                        setMediaPlayer("1", fileDescriptor);
                    }
                } else {
                    if (audioFile.equalsIgnoreCase("") || audioFile.isEmpty()) {
                        setMediaPlayer("2", fileDescriptor);
                    } else {
                        if (BWSApplication.isNetworkConnected(ctx)) {
                            setMediaPlayer("0", fileDescriptor);
//                mediaPlayer.setDataSource(audioFile);
                        } else {
                            binding.progressBar.setVisibility(View.GONE);
//                        binding.llProgress.setVisibility(View.GONE);
                            binding.ivPlay.setVisibility(View.VISIBLE);
                            binding.ivPause.setVisibility(View.GONE);
                            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (audioFile.equalsIgnoreCase("") || audioFile.isEmpty()) {
                setMediaPlayer("2", fileDescriptor);
            } else {
                if (BWSApplication.isNetworkConnected(ctx)) {
                    setMediaPlayer("0", fileDescriptor);
//                mediaPlayer.setDataSource(audioFile);
                } else {
                    binding.progressBar.setVisibility(View.GONE);
//                binding.llProgress.setVisibility(View.GONE);
                    binding.ivPlay.setVisibility(View.VISIBLE);
                    binding.ivPause.setVisibility(View.GONE);
                    BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                }
            }
        }
    }

    public void callMedia() {
      /*  BWSApplication.createNotification(ctx, mainPlayModelList.get(position),
                R.drawable.ic_pause_black_24dp, position, mainPlayModelList.size() - 1);*/
        FileDescriptor fileDescriptor = null;
        if (audioFile.equalsIgnoreCase("")) {
            setMediaPlayer("2", fileDescriptor);

        } else {
            if (downloadAudioDetailsList.size() != 0) {
                isprogressbar = true;
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.ivPlay.setVisibility(View.GONE);
                binding.ivPause.setVisibility(View.GONE);
                isPause = false;
                DownloadMedia downloadMedia = new DownloadMedia(ctx.getApplicationContext());
                getDownloadMedia(downloadMedia);

            } else {
                if (BWSApplication.isNetworkConnected(ctx)) {
                    isprogressbar = true;
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.ivPlay.setVisibility(View.GONE);
                    binding.ivPause.setVisibility(View.GONE);
                    setMediaPlayer("0", fileDescriptor);
                } else {
                    isprogressbar = false;
                    binding.progressBar.setVisibility(View.GONE);
                    binding.ivPlay.setVisibility(View.VISIBLE);
                    binding.ivPause.setVisibility(View.GONE);
                    localIntent.putExtra("MyData", "play");
                    localBroadcastManager.sendBroadcast(localIntent);
                    BWSApplication.showToast(ctx.getString(R.string.no_server_found), ctx);
                }
            }
        }
    }

    public void getDownloadMedia(DownloadMedia downloadMedia) {
        class getDownloadMedia extends AsyncTask<Void, Void, Void> {
            FileDescriptor fileDescriptor = null;

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    byte[] decrypt = null;
                    decrypt = downloadMedia.decrypt(name);
                    if (decrypt != null) {
                        fileDescriptor = FileUtils.getTempFileDescriptor(ctx.getApplicationContext(), decrypt);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (fileDescriptor != null) {
                    setMediaPlayer("1", fileDescriptor);
                } else {
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        setMediaPlayer("0", fileDescriptor);
                    } else {
                        isprogressbar = false;
                        binding.progressBar.setVisibility(View.GONE);
                        binding.ivPlay.setVisibility(View.VISIBLE);
                        binding.ivPause.setVisibility(View.GONE);
                        localIntent.putExtra("MyData", "play");
                        localBroadcastManager.sendBroadcast(localIntent);
                        BWSApplication.showToast(ctx.getString(R.string.no_server_found), ctx);
                    }
                }
                super.onPostExecute(aVoid);
            }
        }

        getDownloadMedia st = new getDownloadMedia();
        st.execute();
    }

    private void callComplete() {
        handler12.removeCallbacks(UpdateSongTime12);
        isPrepare = false;
        isMediaStart = false;
        if (audioPlay && (audioFile.equalsIgnoreCase("") || audioFile.isEmpty())) {
            isDisclaimer = 0;
            removeArray();
        } else {
            if (IsRepeat.equalsIgnoreCase("1")) {
                if (position < (listSize - 1)) {
                    position = position + 1;
                } else {
                    position = 0;
                }
                getPrepareShowData();
            } else if (IsRepeat.equalsIgnoreCase("0")) {
                getPrepareShowData();
            } else if (IsShuffle.equalsIgnoreCase("1")) {
                // shuffle is on - play a random song
                if (queuePlay) {
                    try {
                        addToQueueModelList.remove(position);
                    } catch (Exception e) {
                    }
                    listSize = addToQueueModelList.size();
                    if (listSize == 0) {
                        isCompleteStop = true;
                        stopMedia();
                    } else if (listSize == 1) {
                        position = 0;
                        getPrepareShowData();
                    } else {
                        int oldPosition = position;
                        Random random = new Random();
                        position = random.nextInt((listSize - 1) - 0 + 1) + 0;
                        if (oldPosition == position) {
                            Random random1 = new Random();
                            position = random1.nextInt((listSize - 1) - 0 + 1) + 0;
                        }
                        getPrepareShowData();
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
                        getPrepareShowData();
                    }
                }
            } else {
                if (queuePlay) {
                    try {
                        addToQueueModelList.remove(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    listSize = addToQueueModelList.size();
                    if (position < listSize - 1) {
                        getPrepareShowData();
                    } else {
                        if (listSize == 0) {
                            savePrefQueue(0, false, true, addToQueueModelList, ctx);
                            isCompleteStop = true;
                            stopMedia();
                        } else {
                            position = 0;
                            getPrepareShowData();
                        }
                    }
                } else {
                    if (position < (listSize - 1)) {
                        int oldPosition = position;
                        position = position + 1;
                        if (oldPosition == position) {
                            position++;
                        }
                        getPrepareShowData();
                    } else {
                        if (listSize == 1) {
                            binding.ivPlay.setVisibility(View.VISIBLE);
                            binding.ivPause.setVisibility(View.GONE);
                            binding.pbProgressBar.setVisibility(View.GONE);
                            isCompleteStop = true;
                            stopMedia();
                        } else {
                            binding.ivPlay.setVisibility(View.VISIBLE);
                            binding.ivPause.setVisibility(View.GONE);
                            binding.pbProgressBar.setVisibility(View.GONE);
                            isCompleteStop = true;
                            stopMedia();
//                        position = 0;
//                        getPrepareShowData();
                        }
                    }
                }
            }
        }
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(CONSTANTS.PREF_KEY_position, position);
        editor.commit();
        /*BWSApplication.createChannel(getActivity());
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
        getActivity().startService(new Intent(getActivity().getBaseContext(), OnClearFromRecentService.class));*/
    }

    private void removeArray() {
        shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        gson = new Gson();
        json = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson));
        mainPlayModelList = new ArrayList<>();
        if (AudioFlag.equalsIgnoreCase("MainAudioList")) {
            Type type = new TypeToken<ArrayList<MainAudioModel.ResponseData.Detail>>() {
            }.getType();
            ArrayList<MainAudioModel.ResponseData.Detail> arrayList = gson.fromJson(json, type);

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
            ArrayList<ViewAllAudioListModel.ResponseData.Detail> arrayList = gson.fromJson(json, type);
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
            ArrayList<SearchBothModel.ResponseData> arrayList = gson.fromJson(json, type);
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
            ArrayList<SuggestedModel.ResponseData> arrayList = gson.fromJson(json, type);
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
            ArrayList<AppointmentDetailModel.Audio> arrayList = gson.fromJson(json, type);
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
            ArrayList<LikesHistoryModel.ResponseData.Audio> arrayList = gson.fromJson(json, type);
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
            ArrayList<DownloadAudioDetails> arrayList = gson.fromJson(json, type);
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
            ArrayList<DownloadAudioDetails> arrayList = gson.fromJson(json, type);
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
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);

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
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
    /*    handler.removeCallbacks(UpdateSongTime);
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
        handler12.removeCallbacks(UpdateSongTime12);

    }

    public void updateProgressBar() {
        handler12.postDelayed(UpdateSongTime12, 100);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler12.removeCallbacks(UpdateSongTime12);

        int totalDuration = getEndTime();
        int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        SeekTo(currentPosition);

        oTime = binding.simpleSeekbar.getProgress();
        // update timer progress again
        updateProgressBar();
    }

    @Override
    public void onDestroy() {
//        getActivity().unregisterReceiver(playNewAudio);
//        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(listener);
        super.onDestroy();
    }
    @Override
    public void onDestroyView() {
//        getActivity().unregisterReceiver(playNewAudio);
//        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(listener);
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ComeScreenAccount == 1) {
            binding.llLayout.setVisibility(View.GONE);
        } else if (ComeScreenAccount == 0) {
            binding.llLayout.setVisibility(View.VISIBLE);
        }

//        handler12.postDelayed(UpdateSongTime12, 500);
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
        Gson gson = new Gson();
        String json1 = shared.getString(CONSTANTS.PREF_KEY_queueList, String.valueOf(gson));
        if (!json1.equalsIgnoreCase(String.valueOf(gson))) {
            Type type1 = new TypeToken<ArrayList<AddToQueueModel>>() {
            }.getType();
            addToQueueModelList = gson.fromJson(json1, type1);
        }
        String json = shared.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gson));
        Type type = new TypeToken<ArrayList<MainPlayModel>>() {
        }.getType();
        mainPlayModelList = new ArrayList<>();
        if (!json.equalsIgnoreCase(String.valueOf(gson))) {
            mainPlayModelList = gson.fromJson(json, type);
        }
        queuePlay = shared.getBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
        audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        IntentFilter filter = new IntentFilter(Broadcast_PLAY_NEW_AUDIO);
        getActivity().registerReceiver(playNewAudio, filter);
        localIntent = new Intent("play_pause_Action");
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        PowerManager powerManager = (PowerManager) ctx.getSystemService(POWER_SERVICE);
        try {
            if (queuePlay) {
                position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
                listSize = addToQueueModelList.size();
                id = addToQueueModelList.get(position).getID();
                myAudioId = id;
                name = addToQueueModelList.get(position).getName();
                audioFile = addToQueueModelList.get(position).getAudioFile();
            } else if (audioPlay) {
                position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
                listSize = mainPlayModelList.size();
                if (listSize == 1) {
                    position = 0;
                }
                if (listSize != 0) {
                    id = mainPlayModelList.get(position).getID();
                    myAudioId = id;
                    name = mainPlayModelList.get(position).getName();
                    audioFile = mainPlayModelList.get(position).getAudioFile();
       /*         if (audioFile.equalsIgnoreCase("")) {
                    Glide.with(ctx).load(R.drawable.disclaimer).thumbnail(0.05f)
                            .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
                } else {
                    Glide.with(ctx).load(mainPlayModelList.get(position).getImageFile()).thumbnail(0.05f)
                            .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
                }
                binding.tvTitle.setText(mainPlayModelList.get(position).getName());
                binding.tvSubTitle.setText(mainPlayModelList.get(position).getAudioDirection());
                if (audioFile.equalsIgnoreCase("") || audioFile.isEmpty()) {
                    isDisclaimer = 1;
                    binding.simpleSeekbar.setClickable(false);
                    binding.flProgress.setClickable(false);
                    binding.flProgress.setEnabled(false);
                } else {
                    isDisclaimer = 0;
                    binding.simpleSeekbar.setClickable(true);
                    binding.flProgress.setClickable(true);
                    binding.flProgress.setEnabled(true);
                }*/
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listSize == 1) {
            position = 0;
        }
        SharedPreferences Status = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
        IsRepeat = Status.getString(CONSTANTS.PREF_KEY_IsRepeat, "");
        IsShuffle = Status.getString(CONSTANTS.PREF_KEY_IsShuffle, "");
      /*  if (isPrepare && !isMediaStart) {
            callMedia();
        } else if (isMediaStart && !isPause) {
            binding.ivPlay.setVisibility(View.GONE);
            binding.ivPause.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.ivPlay.setVisibility(View.GONE);
            binding.ivPause.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void onPause() {
//        handler12.removeCallbacks(UpdateSongTime12);
//        Log.e("Stop runnble", "stop");

        handler12.removeCallbacks(UpdateSongTime12);
        getActivity().unregisterReceiver(playNewAudio);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(listener);
        super.onPause();
    }

}