package com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Activities.AudioPlayerActivity;
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
import com.brainwellnessspa.databinding.FragmentMiniExoCustomBinding;
import com.brainwellnessspa.databinding.FragmentMiniPlayerBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.exoplayer2.util.Assertions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.audioClick;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.miniPlayer;
import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;
import static com.brainwellnessspa.Services.GlobleInItExoPlayer.GlobleInItDisclaimer;
import static com.brainwellnessspa.Services.GlobleInItExoPlayer.GlobleInItPlayer;
import static com.brainwellnessspa.Services.GlobleInItExoPlayer.getMediaBitmap;
import static com.brainwellnessspa.Services.GlobleInItExoPlayer.player;
import static com.brainwellnessspa.Services.GlobleInItExoPlayer.myBitmap;

public class MiniPlayerFragment extends Fragment {
    public static int isDisclaimer = 0;
    public static String addToRecentPlayId = "", myAudioId = "";
    FragmentMiniPlayerBinding binding;
    FragmentMiniExoCustomBinding exoBinding;
    Context ctx;
    Activity activity;
    View view;
    List<DownloadAudioDetails> downloadAudioDetailsList;
    byte[] descriptor;
    List<File> bytesDownloaded;
    ArrayList<MainPlayModel> mainPlayModelList;
    ArrayList<AddToQueueModel> addToQueueModelList;
    String IsRepeat = "", IsShuffle = "", UserID, AudioFlag, id, name, url, playFrom = "";
    int position, listSize;
    Boolean queuePlay, audioPlay;
    private long mLastClickTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mini_player, container, false);
        view = binding.getRoot();
        ctx = getActivity();
        activity = getActivity();
        addToQueueModelList = new ArrayList<>();
        mainPlayModelList = new ArrayList<>();
        downloadAudioDetailsList = new ArrayList<>();
        bytesDownloaded = new ArrayList<>();
        exoBinding = DataBindingUtil.inflate(LayoutInflater.from(ctx)
                , R.layout.fragment_mini_exo_custom, binding.playerControlView, false);
        if(audioClick) {
            GetAllMedia();
        }else{
            MakeArray2();
        }
        SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences Status = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_Status, Context.MODE_PRIVATE);
        IsRepeat = Status.getString(CONSTANTS.PREF_KEY_IsRepeat, "");
        IsShuffle = Status.getString(CONSTANTS.PREF_KEY_IsShuffle, "");
        binding.playerControlView.addView(exoBinding.getRoot());
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
        exoBinding.llPlayearMain.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (miniPlayer == 0) {
                miniPlayer = 1;
                audioClick = true;
            }else {
                audioClick = false;
            }
//            if (!isPause && binding.progressBar.getVisibility() == View.GONE) {
//                isPause = false;
//                isprogressbar = false;
//            } else if (isPause && binding.progressBar.getVisibility() == View.GONE) {
//                isPause = true;
//                isprogressbar = false;
//            } else if (isCompleteStop && binding.progressBar.getVisibility() == View.GONE) {
//                isprogressbar = false;
//            } else if (binding.progressBar.getVisibility() == View.VISIBLE && (binding.ivPause.getVisibility() == View.GONE && binding.ivPlay.getVisibility() == View.GONE)) {
//                isprogressbar = true;
//            }
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
//            handler12.removeCallbacks(UpdateSongTime12);
//                Intent i = new Intent(ctx, PlayWellnessActivity.class);
            /* TODO : MY COM*/

            Intent i = new Intent(ctx, AudioPlayerActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            ctx.startActivity(i);
        });
        return view;
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

    /*public void InitNotificationAudioPLayer() {
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                ctx,
                "10001",
                R.string.playback_channel_name,
                notificationId,
                new PlayerNotificationManager.MediaDescriptionAdapter() {
                    @Override
                    public String getCurrentContentTitle(Player player) {
                        return mainPlayModelList.get(player.getCurrentWindowIndex()).getName();
                    }

                    @Nullable
                    @Override
                    public PendingIntent createCurrentContentIntent(Player player) {
                        return null;
                    }

                    @Nullable
                    @Override
                    public String getCurrentContentText(Player player) {
                        return mainPlayModelList.get(player.getCurrentPeriodIndex()).getAudioDirection();
                    }

                    @Nullable
                    @Override
                    public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                        getMediaBitmap(mainPlayModelList.get(player.getCurrentWindowIndex()).getImageFile());
                        return myBitmap;
                    }
                }
        );
        if (!mainPlayModelList.get(player.getCurrentPeriodIndex()).getAudioFile().equalsIgnoreCase("")) {
            playerNotificationManager.setFastForwardIncrementMs(30000);
            playerNotificationManager.setRewindIncrementMs(30000);
            playerNotificationManager.setUseNavigationActions(true);
            playerNotificationManager.setUseNavigationActionsInCompactView(true);
        } else {
            playerNotificationManager.setFastForwardIncrementMs(0);
            playerNotificationManager.setRewindIncrementMs(0);
            playerNotificationManager.setUseNavigationActions(false);
            playerNotificationManager.setUseNavigationActionsInCompactView(false);
        }
        playerNotificationManager.setSmallIcon(R.drawable.logo_design);
        playerNotificationManager.setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE);
        playerNotificationManager.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        playerNotificationManager.setUseChronometer(true);
        playerNotificationManager.setPriority(NotificationCompat.PRIORITY_HIGH);
        playerNotificationManager.setUsePlayPauseActions(true);
        playerNotificationManager.setPlayer(player);
    }*/

    private void initializePlayer() {
//        player = new SimpleExoPlayer.Builder(ctx.getApplicationContext()).build();
        isDisclaimer = 0;
        callAllDisable(true);
        if(audioClick) {
            GlobleInItPlayer(ctx, position, downloadAudioDetailsList, mainPlayModelList, bytesDownloaded);
        }if(player!=null){
                player.setWakeMode(2);
            player.addListener(new ExoPlayer.EventListener() {
                @Override
                public void onPositionDiscontinuity(int reason) {

                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {

                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                    Log.v("TAG", "Listener-onTracksChanged... ");
                    player.setPlayWhenReady(true);
                    position = player.getCurrentWindowIndex();
                    SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putInt(CONSTANTS.PREF_KEY_position, position);
                    editor.commit();
                    myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(player.getCurrentWindowIndex()).getImageFile());
                    callButtonText(player.getCurrentWindowIndex());
                }

                @Override
                public void onIsLoadingChanged(boolean isLoading) {

                }

                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    if (isPlaying) {
                        exoBinding.llPlay.setVisibility(View.GONE);
                        exoBinding.llPause.setVisibility(View.VISIBLE);
                        exoBinding.progressBar.setVisibility(View.GONE);
                    } else if (!isPlaying) {
                        exoBinding.llPlay.setVisibility(View.VISIBLE);
                        exoBinding.llPause.setVisibility(View.GONE);
                        exoBinding.progressBar.setVisibility(View.GONE);
                    }
                    exoBinding.exoProgress.setBufferedPosition(player.getBufferedPosition());
                    exoBinding.exoProgress.setPosition(player.getCurrentPosition());
                    exoBinding.exoProgress.setDuration(player.getDuration());
                }

              /*  @Override
                public void onPlayWhenReadyChanged(boolean playWhenReady, int state) {
                    if (state == ExoPlayer.STATE_READY && !playWhenReady) {
                            exoBinding.llPlay.setVisibility(View.VISIBLE);
                            exoBinding.llPause.setVisibility(View.GONE);
                            exoBinding.progressBar.setVisibility(View.GONE);
                    } else if (state == ExoPlayer.STATE_READY && playWhenReady) {
                            exoBinding.llPlay.setVisibility(View.VISIBLE);
                            exoBinding.llPause.setVisibility(View.GONE);
                            exoBinding.progressBar.setVisibility(View.GONE);
                    } else if (state == ExoPlayer.STATE_BUFFERING) {
                        exoBinding.llPlay.setVisibility(View.GONE);
                        exoBinding.llPause.setVisibility(View.GONE);
                        exoBinding.progressBar.setVisibility(View.VISIBLE);
                    }
                }*/

                @Override
                public void onPlaybackStateChanged(int state) {
                    if (state == ExoPlayer.STATE_READY) {
                        exoBinding.llPlay.setVisibility(View.GONE);
                        exoBinding.llPause.setVisibility(View.VISIBLE);
                        exoBinding.progressBar.setVisibility(View.GONE);
                    } else if (state == ExoPlayer.STATE_BUFFERING) {
                        exoBinding.llPlay.setVisibility(View.GONE);
                        exoBinding.llPause.setVisibility(View.GONE);
                        exoBinding.progressBar.setVisibility(View.VISIBLE);
                    }
                }
            });
            exoBinding.exoProgress.addListener(new TimeBar.OnScrubListener() {
                @Override
                public void onScrubStart(TimeBar timeBar, long position) {
                    exoBinding.exoProgress.setPosition(position);
                }

                @Override
                public void onScrubMove(TimeBar timeBar, long position) {

                }

                @Override
                public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
                    player.seekTo(position);
                    exoBinding.exoProgress.setPosition(position);
                }
            });
            callRepeatShuffle();

        }
       /* if (downloadAudioDetailsList.size() != 0) {
            for (int f = 0; f < downloadAudioDetailsList.size(); f++) {
                if (downloadAudioDetailsList.get(f).getAudioFile().equalsIgnoreCase(mainPlayModelList.get(0).getAudioFile())) {
//                    DownloadMedia downloadMedia = new DownloadMedia(getApplicationContext());
//                    getDownloadMedia(downloadMedia,downloadAudioDetailsList.get(f).getName());

                    Uri uri = Uri.fromFile(bytesDownloaded.get(f));
                    DataSpec dataSpec = new DataSpec(uri);
                    final FileDataSource fileDataSource = new FileDataSource();
                    try {
                        fileDataSource.open(dataSpec);
                    } catch (FileDataSource.FileDataSourceException e) {
                        e.printStackTrace();
                    }

                    MediaItem mediaItem = MediaItem.fromUri(uri);
                    player.setMediaItem(mediaItem);
                    break;
                }
            }
        } else {
            MediaItem mediaItem1 = MediaItem.fromUri(mainPlayModelList.get(0).getAudioFile());
            player.setMediaItem(mediaItem1);
        }
        for (int i = 1; i < mainPlayModelList.size(); i++) {
            if (downloadAudioDetailsList.size() != 0) {
                for (int f = 0; f < downloadAudioDetailsList.size(); f++) {
                    if (downloadAudioDetailsList.get(f).getAudioFile().equalsIgnoreCase(mainPlayModelList.get(i).getAudioFile())) {
//                    DownloadMedia downloadMedia = new DownloadMedia(getApplicationContext());
//                    getDownloadMedia(downloadMedia,downloadAudioDetailsList.get(f).getName());
                        Uri uri = Uri.fromFile(bytesDownloaded.get(f));
                        DataSpec dataSpec = new DataSpec(uri);
                        final FileDataSource fileDataSource = new FileDataSource();
                        try {
                            fileDataSource.open(dataSpec);
                        } catch (FileDataSource.FileDataSourceException e) {
                            e.printStackTrace();
                        }

                        MediaItem mediaItem = MediaItem.fromUri(uri);
                        player.addMediaItem(mediaItem);
                    } else {
                        MediaItem mediaItem = MediaItem.fromUri(mainPlayModelList.get(i).getAudioFile());
                        player.addMediaItem(mediaItem);
                    }
                }
            } else {
                MediaItem mediaItem = MediaItem.fromUri(mainPlayModelList.get(i).getAudioFile());
                player.addMediaItem(mediaItem);
            }
        } */
//        player.prepare();
//        player.seekTo(position, C.CONTENT_TYPE_MUSIC);
        if (miniPlayer == 0) {
            exoBinding.progressBar.setVisibility(View.GONE);
            exoBinding.llPlay.setVisibility(View.VISIBLE);
            exoBinding.llPause.setVisibility(View.GONE);
        }else{
            if(player!=null){
                if(player.getPlayWhenReady()){
                    exoBinding.llPlay.setVisibility(View.GONE);
                    exoBinding.llPause.setVisibility(View.VISIBLE);
                    exoBinding.progressBar.setVisibility(View.GONE);
                }else if(!player.getPlayWhenReady()){
                    exoBinding.llPlay.setVisibility(View.VISIBLE);
                    exoBinding.llPause.setVisibility(View.GONE);
                    exoBinding.progressBar.setVisibility(View.GONE);
                }
                exoBinding.exoProgress.setBufferedPosition(player.getBufferedPosition());
                exoBinding.exoProgress.setPosition(player.getCurrentPosition());
                exoBinding.exoProgress.setDuration(player.getDuration());
            }
        }
        epAllClicks();
    }

    private void initializePlayerDisclaimer() {
//        player = new SimpleExoPlayer.Builder(ctx.getApplicationContext()).build();
        if(audioClick) {
            GlobleInItDisclaimer(ctx, mainPlayModelList);
        }
        if(player!=null) {
            player.addListener(new ExoPlayer.EventListener() {
                @Override
                public void onPlaybackStateChanged(int state) {
                    if (state == ExoPlayer.STATE_ENDED) {
                        //player back ended
                        removeArray();
                    }
                    if (state == ExoPlayer.STATE_READY) {
                        exoBinding.llPlay.setVisibility(View.GONE);
                        exoBinding.llPause.setVisibility(View.VISIBLE);
                        exoBinding.progressBar.setVisibility(View.GONE);
                    } else if (state == ExoPlayer.STATE_BUFFERING) {
                        exoBinding.llPlay.setVisibility(View.GONE);
                        exoBinding.llPause.setVisibility(View.GONE);
                        exoBinding.progressBar.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    if (isPlaying) {
                        exoBinding.llPlay.setVisibility(View.GONE);
                        exoBinding.llPause.setVisibility(View.VISIBLE);
                        exoBinding.progressBar.setVisibility(View.GONE);
                    } else if (!isPlaying) {
                        exoBinding.llPlay.setVisibility(View.VISIBLE);
                        exoBinding.llPause.setVisibility(View.GONE);
                        exoBinding.progressBar.setVisibility(View.GONE);
                    }
                    exoBinding.exoProgress.setBufferedPosition(player.getBufferedPosition());
                    exoBinding.exoProgress.setPosition(player.getCurrentPosition());
                    exoBinding.exoProgress.setDuration(player.getDuration());
                }
            });
        }
        exoBinding.llPause.setOnClickListener(view -> {
            player.setPlayWhenReady(false);
            exoBinding.llPlay.setVisibility(View.VISIBLE);
            exoBinding.llPause.setVisibility(View.GONE);
            exoBinding.progressBar.setVisibility(View.GONE);
        });
        exoBinding.llPlay.setOnClickListener(view -> {
            if(player!=null) {
                exoBinding.llPlay.setVisibility(View.GONE);
                exoBinding.llPause.setVisibility(View.VISIBLE);
                exoBinding.progressBar.setVisibility(View.GONE);
                player.setPlayWhenReady(true);
            }else{
                audioClick = true;
                miniPlayer = 1;
                GetAllMedia();
                PlayerControlView playerControlView = Assertions.checkNotNull(this.binding.playerControlView);
                playerControlView.setPlayer(player);
                playerControlView.setProgressUpdateListener((position, bufferedPosition) -> {
                    exoBinding.exoProgress.setPosition(position);
                    exoBinding.exoProgress.setBufferedPosition(bufferedPosition);
                });
                playerControlView.show();
            }
        });
        if (miniPlayer == 0) {
            exoBinding.progressBar.setVisibility(View.GONE);
            exoBinding.llPlay.setVisibility(View.VISIBLE);
            exoBinding.llPause.setVisibility(View.GONE);
        }else{
            if(player!=null){
                if(player.isPlaying()){
                    exoBinding.llPlay.setVisibility(View.GONE);
                    exoBinding.llPause.setVisibility(View.VISIBLE);
                    exoBinding.progressBar.setVisibility(View.GONE);
                }else if(!player.isPlaying()){
                    exoBinding.llPlay.setVisibility(View.VISIBLE);
                    exoBinding.llPause.setVisibility(View.GONE);
                    exoBinding.progressBar.setVisibility(View.GONE);

                }
                exoBinding.exoProgress.setBufferedPosition(player.getBufferedPosition());
                exoBinding.exoProgress.setPosition(player.getCurrentPosition());
                exoBinding.exoProgress.setDuration(player.getDuration());
            }
        }
//        MediaItem mediaItem1 = MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(R.raw.brain_wellness_spa_declaimer));
//        player.setMediaItem(mediaItem1);
        callAllDisable(false);
//        player.setPlayWhenReady(true);
//        player.prepare();
    }

    private void epAllClicks() {
        exoBinding.llPause.setOnClickListener(view -> {
            player.setPlayWhenReady(false);
            exoBinding.llPlay.setVisibility(View.VISIBLE);
            exoBinding.llPause.setVisibility(View.GONE);
            exoBinding.progressBar.setVisibility(View.GONE);
        });
        exoBinding.llPlay.setOnClickListener(view ->{
            if(player!=null) {
                exoBinding.llPlay.setVisibility(View.GONE);
                exoBinding.llPause.setVisibility(View.VISIBLE);
                exoBinding.progressBar.setVisibility(View.GONE);
                player.setPlayWhenReady(true);
            }else{
                audioClick = true;
                miniPlayer = 1;
                GetAllMedia();
                PlayerControlView playerControlView = Assertions.checkNotNull(this.binding.playerControlView);
                playerControlView.setPlayer(player);
                playerControlView.setProgressUpdateListener((position, bufferedPosition) -> {
                    exoBinding.exoProgress.setPosition(position);
                    exoBinding.exoProgress.setBufferedPosition(bufferedPosition);
                });
                playerControlView.show();
            }
        });
    }

    private void callRepeatShuffle() {
        if (!url.equalsIgnoreCase("")) {
            if (IsShuffle.equalsIgnoreCase("")) {
                player.setShuffleModeEnabled(false);
            } else if (IsShuffle.equalsIgnoreCase("1")) {
                player.setShuffleModeEnabled(true);
            }
            if (IsRepeat.equalsIgnoreCase("")) {
                player.setRepeatMode(Player.REPEAT_MODE_OFF);
            } else if (IsRepeat.equalsIgnoreCase("0")) {
                player.setRepeatMode(Player.REPEAT_MODE_ONE);
            } else if (IsRepeat.equalsIgnoreCase("1")) {
                player.setRepeatMode(Player.REPEAT_MODE_ALL);
            }
        }
    }

    private void callAllDisable(boolean b) {
        if (b) {
            exoBinding.exoProgress.setClickable(true);
            exoBinding.exoProgress.setEnabled(true);
        } else {
            exoBinding.exoProgress.setClickable(false);
            exoBinding.exoProgress.setEnabled(false);
        }
    }

    private void getDownloadMedia(DownloadMedia downloadMedia, String name, int i) {

        class getDownloadMedia extends AsyncTask<Void, Void, Void> {
            File fileDescriptor = null;

            @Override
            protected Void doInBackground(Void... voids) {
//                try {

                descriptor = downloadMedia.decrypt(name);
                try {
                    if (descriptor != null) {
                        fileDescriptor = FileUtils.getTempFileDescriptor1(ctx.getApplicationContext(), descriptor);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                bytesDownloaded.add(fileDescriptor);
//                Log.e("MakeArry not Call",String.valueOf(i));
                descriptor = null;
                fileDescriptor = null;
//                if(i == downloadAudioDetailsList.size()) {
//                    Log.e("MakeArry Call",String.valueOf(i));
                if (i < downloadAudioDetailsList.size() - 1) {
                    getDownloadMedia(downloadMedia, downloadAudioDetailsList.get(i + 1).getName(), i + 1);
                    Log.e("DownloadMedia Call", String.valueOf(i + 1));
                } else {
                    MakeArray();
                    Log.e("MakeArry Call", String.valueOf(i));
                }
//                }
                super.onPostExecute(aVoid);
            }
        }

        getDownloadMedia st = new getDownloadMedia();
        st.execute();
    }

    private void callButtonText(int ps) {
//        simpleSeekbar.setMax(100);
        url = mainPlayModelList.get(ps).getAudioFile();
        id = mainPlayModelList.get(ps).getID();
        if (url.equalsIgnoreCase("") || url.isEmpty()) {
            isDisclaimer = 1;
        } else {
            isDisclaimer = 0;
        }
        if (mainPlayModelList.get(ps).getPlaylistID() == null) {
            mainPlayModelList.get(ps).setPlaylistID("");
        }
        myBitmap = getMediaBitmap(ctx,mainPlayModelList.get(ps).getImageFile());

        if (url.equalsIgnoreCase("")) {
            Glide.with(ctx).load(R.drawable.disclaimer).thumbnail(0.05f)
                    .placeholder(R.drawable.disclaimer).error(R.drawable.disclaimer)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(exoBinding.ivRestaurantImage);
        } else {
            Glide.with(ctx).load(mainPlayModelList.get(ps).getImageFile()).thumbnail(0.05f)
                    .placeholder(R.drawable.disclaimer).error(R.drawable.disclaimer)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(exoBinding.ivRestaurantImage);
        }
        exoBinding.tvTitle.setText(mainPlayModelList.get(ps).getName());
        exoBinding.tvSubTitle.setText(mainPlayModelList.get(ps).getAudioDirection());
        if (!url.equalsIgnoreCase("")) {
            if (!id.equalsIgnoreCase(addToRecentPlayId)) {
                addToRecentPlay();
                Log.e("Api call recent", id);
            }
        }
    }

    public List<DownloadAudioDetails> GetAllMedia() {
        class GetTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                downloadAudioDetailsList = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .geAllDataBYDownloaded("Complete");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (downloadAudioDetailsList.size() != 0) {
//                    for (int i = 0; i < downloadAudioDetailsList.size(); i++) {
                    DownloadMedia downloadMedia = new DownloadMedia(ctx.getApplicationContext());
                    getDownloadMedia(downloadMedia, downloadAudioDetailsList.get(0).getName(), 0);
//                    }
                } else {
                    MakeArray();
                }
                super.onPostExecute(aVoid);
            }
        }
        GetTask st = new GetTask();
        st.execute();
        return downloadAudioDetailsList;
    }

    private void MakeArray() {
        Gson gson = new Gson();
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
        String json = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson));
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        MainPlayModel mainPlayModel;
        addToQueueModelList = new ArrayList<>();
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
        if (queuePlay) {
            playFrom = "queuePlay";
        } else if (audioPlay) {
            playFrom = "audioPlay";
        } else {
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
            
        }
        getPrepareShowData();
    }
    private void MakeArray2() {
        Gson gson = new Gson();
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
        String json = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson));
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        MainPlayModel mainPlayModel;
        addToQueueModelList = new ArrayList<>();
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
        if (queuePlay) {
            playFrom = "queuePlay";
        } else if (audioPlay) {
            playFrom = "audioPlay";
        } else {
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
            
        }
        getPrepareShowData();
    }
    private void removeArray() {
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
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

    private void getPrepareShowData() {
        myBitmap = getMediaBitmap(ctx,mainPlayModelList.get(position).getImageFile());
        callButtonText(position);
        if (mainPlayModelList.get(position).getAudioFile().equalsIgnoreCase("")) {
            initializePlayerDisclaimer();
        } else {
            initializePlayer();
        }

        PlayerControlView playerControlView = Assertions.checkNotNull(this.binding.playerControlView);
        playerControlView.setPlayer(player);
        playerControlView.setProgressUpdateListener((position, bufferedPosition) -> {
            exoBinding.exoProgress.setPosition(position);
            exoBinding.exoProgress.setBufferedPosition(bufferedPosition);
        });
        playerControlView.show();
    }
}