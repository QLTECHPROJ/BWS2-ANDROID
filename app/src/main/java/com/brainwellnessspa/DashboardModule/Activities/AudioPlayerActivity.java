package com.brainwellnessspa.DashboardModule.Activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
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
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Models.AddToQueueModel;
import com.brainwellnessspa.DashboardModule.Models.AppointmentDetailModel;
import com.brainwellnessspa.DashboardModule.Models.AudioLikeModel;
import com.brainwellnessspa.DashboardModule.Models.MainAudioModel;
import com.brainwellnessspa.DashboardModule.Models.SearchBothModel;
import com.brainwellnessspa.DashboardModule.Models.SubPlayListModel;
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
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.ActivityAudioPlayerBinding;
import com.brainwellnessspa.databinding.AudioPlayerCustomLayoutBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.exoplayer2.util.Assertions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.DashboardModule.Audio.AudioFragment.IsLock;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.Utility.MusicService.oTime;
import static com.brainwellnessspa.Utility.MusicService.progressToTimer;

public class AudioPlayerActivity extends AppCompatActivity {
    private static final String SURFACE_CONTROL_NAME = "BrainWellnessApp";
    private static final String OWNER_EXTRA = "owner";
    @Nullable
    private static SimpleExoPlayer player;
    @Nullable
    private static SurfaceControl surfaceControl;
    @Nullable
    private static Surface videoSurface;
    List<DownloadAudioDetails> downloadAudioDetailsList;
    AudioPlayerCustomLayoutBinding exoBinding;
    byte[] descriptor;
    Bitmap myBitmap = null;
    List<File> bytesDownloaded;
    ActivityAudioPlayerBinding binding;
    ArrayList<MainPlayModel> mainPlayModelList;
    ArrayList<AddToQueueModel> addToQueueModelList;
    String IsRepeat = "", IsShuffle = "", UserID, PlaylistId = "", AudioFlag, id, name, url, playFrom = "";
    int position, listSize,downloadPercentage = 0;
    Context ctx;
    Activity activity;
    Boolean queuePlay, audioPlay;
    PlayerNotificationManager playerNotificationManager;
    int notificationId = 1234;
    private long mLastClickTime = 0;
    private boolean isOwner;
    @Nullable
    private SurfaceView nonFullScreenView;
    @Nullable
    private SurfaceView currentOutputView;
    List<DownloadAudioDetails> downloadAudioDetailsList1;

    private static void reparent(@Nullable SurfaceView surfaceView) {
        SurfaceControl surfaceControl = Assertions.checkNotNull(AudioPlayerActivity.surfaceControl);
        if (surfaceView == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                new SurfaceControl.Transaction()
                        .reparent(surfaceControl, /* newParent= */ null)
                        .setBufferSize(surfaceControl, /* w= */ 0, /* h= */ 0)
                        .setVisibility(surfaceControl, /* visible= */ false)
                        .apply();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                SurfaceControl newParentSurfaceControl = surfaceView.getSurfaceControl();
                new SurfaceControl.Transaction()
                        .reparent(surfaceControl, newParentSurfaceControl)
                        .setBufferSize(surfaceControl, surfaceView.getWidth(), surfaceView.getHeight())
                        .setVisibility(surfaceControl, /* visible= */ true)
                        .apply();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_audio_player);

        bytesDownloaded = new ArrayList<>();
        ctx = AudioPlayerActivity.this;
        activity = AudioPlayerActivity.this;
        addToQueueModelList = new ArrayList<>();
        mainPlayModelList = new ArrayList<>();
        downloadAudioDetailsList = new ArrayList<>();
        GetAllMedia();
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences Status = getSharedPreferences(CONSTANTS.PREF_KEY_Status, Context.MODE_PRIVATE);
        IsRepeat = Status.getString(CONSTANTS.PREF_KEY_IsRepeat, "");
        IsShuffle = Status.getString(CONSTANTS.PREF_KEY_IsShuffle, "");
        binding.llBack.setOnClickListener(view -> callBack());

        binding.llMore.setOnClickListener(view -> {
//            handler1.removeCallbacks(UpdateSongTime1);
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent i = new Intent(ctx, AddQueueActivity.class);
            if (AudioFlag.equalsIgnoreCase("TopCategories")) {
                i.putExtra("play", "TopCategories");
            } else
                i.putExtra("play", "play");
            i.putExtra("ID", id);
            i.putExtra("position", position);
            i.putExtra("PlaylistAudioId", "");
            startActivity(i);
//            finish();
        });
    }

    public void InitNotificationAudioPLayer() {
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                this,
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
                        return myBitmap;
                    }
                }
        );
        playerNotificationManager.setFastForwardIncrementMs(30000);
        playerNotificationManager.setRewindIncrementMs(30000);
        playerNotificationManager.setUseNavigationActions(true);
        playerNotificationManager.setSmallIcon(R.drawable.logo_design);
        playerNotificationManager.setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE);
        playerNotificationManager.setUseNavigationActionsInCompactView(true);
        playerNotificationManager.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        playerNotificationManager.setUseChronometer(true);
        playerNotificationManager.setPriority(NotificationCompat.PRIORITY_HIGH);
        playerNotificationManager.setUsePlayPauseActions(true);
        playerNotificationManager.setPlayer(player);
    }

    public Bitmap getMediaBitmap(String songImg) {
        class GetMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    if (songImg.equalsIgnoreCase("")) {
                        myBitmap = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.disclaimer);
                    } else {
                        URL url = new URL(songImg);
                        myBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
            }
        }

        GetMedia st = new GetMedia();
        st.execute();
        return myBitmap;
    }

    private void callBack() {
        try {
//        handler1.removeCallbacks(UpdateSongTime1);
//            player = 1;
//            if (binding.llPause.getVisibility() == View.VISIBLE) {
//                isPause = false;
//            }
//        pauseMedia();
            SharedPreferences shared2 = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared2.edit();
            Gson gson = new Gson();
            String json = gson.toJson(addToQueueModelList);
            if (queuePlay) {
                editor.putString(CONSTANTS.PREF_KEY_queueList, json);
            }
            editor.putInt(CONSTANTS.PREF_KEY_position, position);
            editor.commit();
            finish();
//        overridePendingTransition(R.anim.enter, R.anim.exit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callLike() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
            Call<AudioLikeModel> listCall = APIClient.getClient().getAudioLike(id, UserID);
            listCall.enqueue(new Callback<AudioLikeModel>() {
                @Override
                public void onResponse(Call<AudioLikeModel> call, Response<AudioLikeModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
                            AudioLikeModel model = response.body();
                            if (model.getResponseData().getFlag().equalsIgnoreCase("0")) {
                                exoBinding.ivLike.setImageResource(R.drawable.ic_unlike_icon);
                            } else if (model.getResponseData().getFlag().equalsIgnoreCase("1")) {
                                exoBinding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
                            }
                            SharedPreferences sharedxx = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                            boolean audioPlay = sharedxx.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                            int pos = sharedxx.getInt(CONSTANTS.PREF_KEY_position, 0);
                            AudioFlag = sharedxx.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                            SharedPreferences sharedq = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                            AudioFlag = sharedq.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                            Gson gsonq = new Gson();
                            String jsonq = sharedq.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gsonq));
                            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared.edit();
                            Gson gson = new Gson();
                            if (audioPlay) {
                                if (AudioFlag.equalsIgnoreCase("MainAudioList")) {
                                    Type type = new TypeToken<ArrayList<MainAudioModel.ResponseData.Detail>>() {
                                    }.getType();
                                    ArrayList<MainAudioModel.ResponseData.Detail> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("ViewAllAudioList")) {
                                    Type type = new TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail>>() {
                                    }.getType();
                                    ArrayList<ViewAllAudioListModel.ResponseData.Detail> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("SearchModelAudio")) {
                                    Type type = new TypeToken<ArrayList<SearchBothModel.ResponseData>>() {
                                    }.getType();
                                    ArrayList<SearchBothModel.ResponseData> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("SearchAudio")) {
                                    Type type = new TypeToken<ArrayList<SuggestedModel.ResponseData>>() {
                                    }.getType();
                                    ArrayList<SuggestedModel.ResponseData> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
                                    Type type = new TypeToken<ArrayList<AppointmentDetailModel.Audio>>() {
                                    }.getType();
                                    ArrayList<AppointmentDetailModel.Audio> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("LikeAudioList")) {
                                    Type type = new TypeToken<ArrayList<LikesHistoryModel.ResponseData.Audio>>() {
                                    }.getType();
                                    ArrayList<LikesHistoryModel.ResponseData.Audio> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
                                    Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
                                    }.getType();
                                    ArrayList<DownloadAudioDetails> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("Downloadlist")) {
                                    Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
                                    }.getType();
                                    ArrayList<DownloadAudioDetails> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("TopCategories")) {
                                    Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
                                    }.getType();
                                    ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("SubPlayList")) {
                                    Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
                                    }.getType();
                                    ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                }
                            }
                            if (queuePlay) {
                                addToQueueModelList.get(position).setLike(model.getResponseData().getFlag());
                            } else
                                mainPlayModelList.get(position).setLike(model.getResponseData().getFlag());

                            String json = gson.toJson(mainPlayModelList);
                            editor.putString(CONSTANTS.PREF_KEY_audioList, json);
                            if (queuePlay) {
                                String json1 = gson.toJson(addToQueueModelList);
                                editor.putString(CONSTANTS.PREF_KEY_queueList, json1);
                            }
                            editor.putInt(CONSTANTS.PREF_KEY_position, position);
                            editor.commit();
                            BWSApplication.showToast(model.getResponseMessage(), ctx);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<AudioLikeModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    @Override
    public void onResume() {
//        callRepeatShuffle();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
//        Assertions.checkNotNull(binding.playerControlView).setPlayer(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
      /*  if (isOwner && isFinishing()) {
            if (surfaceControl != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    surfaceControl.release();
                }
                surfaceControl = null;
            }
            if (videoSurface != null) {
                videoSurface.release();
                videoSurface = null;
            }
            if (player != null) {
                player.release();
                player = null;
            }
        }*/
    }

    private void initializePlayer() {
        player = new SimpleExoPlayer.Builder(getApplicationContext()).build();
        binding.tvNowPlaying.setText(R.string.NOW_PLAYING_FROM);
        isDisclaimer = 0;
        callAllDisable(true);
        if (downloadAudioDetailsList.size() != 0) {
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
        }
       /* BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        final ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory();
        DataSource.Factory dateSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, getPackageName()), (TransferListener) bandwidthMeter);
        MediaSource[] mediaSources = new MediaSource[mainPlayModelList.size()];
        for (int i = 0; i < mediaSources.length; i++) {

            String songUri = mainPlayModelList.get(i).getAudioFile();
                if (downloadAudioDetailsList.size() != 0) {
                    for(int f = 0;f<downloadAudioDetailsList.size();f++) {
                        if(downloadAudioDetailsList.get(f).getAudioFile().equalsIgnoreCase(mainPlayModelList.get(i).getAudioFile())){
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
                            break;
                        }
                    }
                }else {
                        mediaSources[i] = new ExtractorMediaSource(Uri.parse(songUri), dateSourceFactory, extractorsFactory, null, Throwable::printStackTrace);
                }
        }
        MediaSource mediaSource = mediaSources.length == 1 ? mediaSources[0]
                : new ConcatenatingMediaSource(mediaSources);*/
//        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector(trackSelectionFactory));
        player.prepare();
        player.seekTo(position, C.CONTENT_TYPE_MUSIC);
        player.setPlayWhenReady(true);
        player.setVideoSurface(videoSurface);
        callRepeatShuffle();
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
                exoBinding.tvStartTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(position),
                        TimeUnit.MILLISECONDS.toSeconds(position) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(position))));
            }
        });
        epAllClicks();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            surfaceControl = new SurfaceControl.Builder()
                    .setName(SURFACE_CONTROL_NAME)
                    .setBufferSize(/* width= */ 0, /* height= */ 0)
                    .build();
            videoSurface = new Surface(surfaceControl);
        }
        player.addListener(new ExoPlayer.EventListener() {

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.v("TAG", "Listener-onTracksChanged... ");
                callButtonText(player.getCurrentWindowIndex());
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    exoBinding.llPlay.setVisibility(View.GONE);
                    exoBinding.llPause.setVisibility(View.VISIBLE);
                    exoBinding.llProgressBar.setVisibility(View.GONE);
                    exoBinding.progressBar.setVisibility(View.GONE);
                } else if (!isPlaying) {
                    exoBinding.llPlay.setVisibility(View.VISIBLE);
                    exoBinding.llPause.setVisibility(View.GONE);
                    exoBinding.llProgressBar.setVisibility(View.GONE);
                    exoBinding.progressBar.setVisibility(View.GONE);
                }
                exoBinding.exoProgress.setBufferedPosition(player.getBufferedPosition());
                exoBinding.exoProgress.setPosition(player.getCurrentPosition());
                exoBinding.exoProgress.setDuration(player.getDuration());
                exoBinding.tvStartTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(player.getCurrentPosition()),
                        TimeUnit.MILLISECONDS.toSeconds(player.getCurrentPosition()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(player.getCurrentPosition()))));
            }

            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == ExoPlayer.STATE_READY) {
                    exoBinding.llPlay.setVisibility(View.GONE);
                    exoBinding.llPause.setVisibility(View.VISIBLE);
                    exoBinding.llProgressBar.setVisibility(View.GONE);
                    exoBinding.progressBar.setVisibility(View.GONE);
                } else if (state == ExoPlayer.STATE_BUFFERING) {
                    exoBinding.llPlay.setVisibility(View.GONE);
                    exoBinding.llPause.setVisibility(View.GONE);
                    exoBinding.llProgressBar.setVisibility(View.VISIBLE);
                    exoBinding.progressBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void epAllClicks() {
        exoBinding.llDownload.setOnClickListener(view -> {
            if (BWSApplication.isNetworkConnected(ctx)) {
                callDownload();
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), ctx);
            }
        });
        exoBinding.llPause.setOnClickListener(view -> player.setPlayWhenReady(false));
        exoBinding.llPlay.setOnClickListener(view -> player.setPlayWhenReady(true));
        exoBinding.llForwardSec.setOnClickListener(view -> player.seekTo(player.getCurrentPosition() + 30000));
        exoBinding.llBackWordSec.setOnClickListener(view -> player.seekTo(player.getCurrentPosition() - 30000));
        exoBinding.llLike.setOnClickListener(view -> {
//            handler1.removeCallbacks(UpdateSongTime1);
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            callLike();
        });
        exoBinding.llRepeat.setOnClickListener(view -> callRepeat());
        exoBinding.llShuffle.setOnClickListener(view -> callShuffle());
        exoBinding.llViewQueue.setOnClickListener(view -> {
//            handler1.removeCallbacks(UpdateSongTime1);
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
//            if (binding.llPause.getVisibility() == View.VISIBLE) {
//                isPause = false;
//            }
            SharedPreferences ViewQueue = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = ViewQueue.edit();
            Gson gsonx = new Gson();
            String jsonx = gsonx.toJson(addToQueueModelList);
            if (queuePlay) {
                editor.putString(CONSTANTS.PREF_KEY_queueList, jsonx);
            }
            editor.putInt(CONSTANTS.PREF_KEY_position, position);
            editor.commit();
            Intent i = new Intent(ctx, ViewQueueActivity.class);
            i.putExtra("ComeFromQueue", "0");
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            finish();
        });
        exoBinding.llNext.setOnClickListener(view -> player.next());
        exoBinding.llPrev.setOnClickListener(view -> player.previous());
    }

    private void initializePlayerDisclaimer() {
        player = new SimpleExoPlayer.Builder(getApplicationContext()).build();
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
                    exoBinding.llProgressBar.setVisibility(View.GONE);
                    exoBinding.progressBar.setVisibility(View.GONE);
                } else if (state == ExoPlayer.STATE_BUFFERING) {
                    exoBinding.llPlay.setVisibility(View.GONE);
                    exoBinding.llPause.setVisibility(View.GONE);
                    exoBinding.llProgressBar.setVisibility(View.VISIBLE);
                    exoBinding.progressBar.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    exoBinding.llPlay.setVisibility(View.GONE);
                    exoBinding.llPause.setVisibility(View.VISIBLE);
                    exoBinding.llProgressBar.setVisibility(View.GONE);
                    exoBinding.progressBar.setVisibility(View.GONE);
                } else {
                    exoBinding.llPlay.setVisibility(View.VISIBLE);
                    exoBinding.llPause.setVisibility(View.GONE);
                    exoBinding.llProgressBar.setVisibility(View.GONE);
                    exoBinding.progressBar.setVisibility(View.GONE);
                }
                exoBinding.exoProgress.setBufferedPosition(player.getBufferedPosition());
                exoBinding.exoProgress.setPosition(player.getCurrentPosition());
                exoBinding.exoProgress.setDuration(player.getDuration());
                exoBinding.tvStartTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(player.getCurrentPosition()),
                        TimeUnit.MILLISECONDS.toSeconds(player.getCurrentPosition()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(player.getCurrentPosition()))));
            }
        });
        exoBinding.llPause.setOnClickListener(view -> player.setPlayWhenReady(false));
        exoBinding.llPlay.setOnClickListener(view -> player.setPlayWhenReady(true));
        MediaItem mediaItem1 = MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(R.raw.brain_wellness_spa_declaimer));
        player.setMediaItem(mediaItem1);
        callAllDisable(false);
        binding.tvNowPlaying.setText("");
//        player.setMediaItems(mediaItemList, position, 0);
        player.setPlayWhenReady(true);
        player.prepare();
//        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            surfaceControl = new SurfaceControl.Builder()
                    .setName(SURFACE_CONTROL_NAME)
                    .setBufferSize(/* width= */ 0, /* height= */ 0)
                    .build();
            videoSurface = new Surface(surfaceControl);
        }
        player.setVideoSurface(videoSurface);
        AudioPlayerActivity.player = player;
    }

    private void callRepeatShuffle() {
        if (url.equalsIgnoreCase("")) {
            exoBinding.llShuffle.setClickable(false);
            exoBinding.llShuffle.setEnabled(false);
            exoBinding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            exoBinding.llRepeat.setEnabled(false);
            exoBinding.llRepeat.setClickable(false);
            exoBinding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
            exoBinding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            if (IsShuffle.equalsIgnoreCase("")) {
                if (listSize == 1) {
                    exoBinding.llShuffle.setClickable(false);
                    exoBinding.llShuffle.setEnabled(false);
                    exoBinding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    exoBinding.llShuffle.setClickable(true);
                    exoBinding.llShuffle.setEnabled(true);
                    exoBinding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                player.setShuffleModeEnabled(false);
            } else if (IsShuffle.equalsIgnoreCase("1")) {
                if (listSize == 1) {
                    exoBinding.llShuffle.setClickable(false);
                    exoBinding.llShuffle.setEnabled(false);
                    exoBinding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    exoBinding.llShuffle.setClickable(true);
                    exoBinding.llShuffle.setEnabled(true);
                    exoBinding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                player.setShuffleModeEnabled(true);
            }
            if (IsRepeat.equalsIgnoreCase("")) {
                if (queuePlay) {
                    exoBinding.llRepeat.setEnabled(false);
                    exoBinding.llRepeat.setClickable(false);
                    exoBinding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
                    exoBinding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    exoBinding.llRepeat.setClickable(true);
                    exoBinding.llRepeat.setEnabled(true);
                    exoBinding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
                    exoBinding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                player.setRepeatMode(Player.REPEAT_MODE_OFF);
            } else if (IsRepeat.equalsIgnoreCase("0")) {
                if (queuePlay) {
                    exoBinding.llRepeat.setEnabled(false);
                    exoBinding.llRepeat.setClickable(false);
                    exoBinding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_one));
                    exoBinding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    IsRepeat = "0";
                    exoBinding.llRepeat.setClickable(true);
                    exoBinding.llRepeat.setEnabled(true);
                    exoBinding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_one));
                    exoBinding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                player.setRepeatMode(Player.REPEAT_MODE_ONE);
            } else if (IsRepeat.equalsIgnoreCase("1")) {
                if (queuePlay) {
                    exoBinding.llRepeat.setEnabled(false);
                    exoBinding.llRepeat.setClickable(false);
                    exoBinding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
                    exoBinding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    if (listSize == 1) {
                        exoBinding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                    } else {
                        exoBinding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
                    }
                    exoBinding.llRepeat.setClickable(true);
                    exoBinding.llRepeat.setEnabled(true);
                    exoBinding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
                }
                player.setRepeatMode(Player.REPEAT_MODE_ALL);
            }
        }
    }

    private void callShuffle() {
        if (IsShuffle.equalsIgnoreCase("")) {
            if (listSize == 1) {
                exoBinding.llShuffle.setClickable(false);
                exoBinding.llShuffle.setEnabled(false);
                exoBinding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                IsShuffle = "1";
                player.setShuffleModeEnabled(true);
                SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putString(CONSTANTS.PREF_KEY_IsShuffle, "1");
//                editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "");
                editor.commit();
//                IsRepeat = "";
//                if (queuePlay) {
//                    exoBinding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
//                } else
//                    exoBinding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                exoBinding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
                exoBinding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        } else if (IsShuffle.equalsIgnoreCase("1")) {
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(CONSTANTS.PREF_KEY_IsShuffle, "");
            editor.commit();
            IsShuffle = "";
            player.setShuffleModeEnabled(false);
            exoBinding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    private void callRepeat() {
        if (IsRepeat.equalsIgnoreCase("")) {
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "0");
//            if (IsShuffle.equalsIgnoreCase("1")) {
//                editor.putString(CONSTANTS.PREF_KEY_IsShuffle, "");
//            }
            editor.commit();
//            IsShuffle = "";
//            if (listSize == 1) {
//                exoBinding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
//            } else
//                exoBinding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            IsRepeat = "0";
            player.setRepeatMode(Player.REPEAT_MODE_ONE);
            exoBinding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_one));
            exoBinding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (IsRepeat.equalsIgnoreCase("0")) {
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "1");
//            editor.putString(CONSTANTS.PREF_KEY_IsShuffle, "");
            IsRepeat = "1";
//            IsShuffle = "";
            if (listSize == 1) {
                editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "");
                IsRepeat = "";
                exoBinding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
//                exoBinding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                exoBinding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), android.graphics.PorterDuff.Mode.SRC_IN);
//                exoBinding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            }
            editor.commit();
            player.setRepeatMode(Player.REPEAT_MODE_ALL);
            exoBinding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
        } else if (IsRepeat.equalsIgnoreCase("1")) {
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
//            editor.putString(CONSTANTS.PREF_KEY_IsShuffle, "");
            editor.putString(CONSTANTS.PREF_KEY_IsRepeat, "");
            IsRepeat = "";
//            IsShuffle = "";
            if (listSize == 1) {
//                exoBinding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else
//                exoBinding.ivShuffle.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            editor.commit();
            player.setRepeatMode(Player.REPEAT_MODE_OFF);
            exoBinding.ivRepeat.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            exoBinding.ivRepeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat_music_icon));
        }
    }

    private void callAllDisable(boolean b) {
        if (b) {
            exoBinding.llNext.setAlpha(1f);
            exoBinding.llPrev.setAlpha(1f);
            exoBinding.llForwardSec.setClickable(true);
            exoBinding.llForwardSec.setEnabled(true);
            exoBinding.llForwardSec.setAlpha(1f);
            exoBinding.llBackWordSec.setClickable(true);
            exoBinding.llBackWordSec.setEnabled(true);
            exoBinding.llBackWordSec.setAlpha(1f);
            exoBinding.llDownload.setClickable(true);
            exoBinding.llDownload.setEnabled(true);
            exoBinding.llDownload.setAlpha(1f);
            exoBinding.llRepeat.setClickable(true);
            exoBinding.llRepeat.setEnabled(true);
            exoBinding.llRepeat.setAlpha(1f);
            exoBinding.llShuffle.setClickable(true);
            exoBinding.llShuffle.setEnabled(true);
            exoBinding.llShuffle.setAlpha(1f);
            exoBinding.llLike.setClickable(true);
            exoBinding.llLike.setEnabled(true);
            exoBinding.llLike.setAlpha(1f);
            exoBinding.rlSeekbar.setClickable(true);
            exoBinding.rlSeekbar.setEnabled(true);
            exoBinding.exoProgress.setClickable(true);
            exoBinding.exoProgress.setEnabled(true);
            callLLMoreViewQClicks();
//            binding.simpleSeekbar.set
        } else {
            exoBinding.llNext.setAlpha(0.6f);
            exoBinding.llPrev.setAlpha(0.6f);
            exoBinding.llForwardSec.setClickable(false);
            exoBinding.llForwardSec.setEnabled(false);
            exoBinding.llForwardSec.setAlpha(0.6f);
            exoBinding.llBackWordSec.setClickable(false);
            exoBinding.llBackWordSec.setEnabled(false);
            exoBinding.llBackWordSec.setAlpha(0.6f);
            binding.llMore.setClickable(false);
            binding.llMore.setEnabled(false);
            binding.llMore.setAlpha(0.6f);
            exoBinding.llViewQueue.setClickable(false);
            exoBinding.llViewQueue.setEnabled(false);
            exoBinding.llViewQueue.setAlpha(0.6f);
            exoBinding.llDownload.setClickable(false);
            exoBinding.llDownload.setEnabled(false);
            exoBinding.llDownload.setAlpha(0.6f);
            exoBinding.llRepeat.setClickable(false);
            exoBinding.llRepeat.setEnabled(false);
            exoBinding.llRepeat.setAlpha(0.6f);
            exoBinding.llShuffle.setClickable(false);
            exoBinding.llShuffle.setEnabled(false);
            exoBinding.llShuffle.setAlpha(0.6f);
            exoBinding.llLike.setClickable(false);
            exoBinding.llLike.setEnabled(false);
            exoBinding.llLike.setAlpha(0.6f);
            exoBinding.rlSeekbar.setClickable(false);
            exoBinding.rlSeekbar.setEnabled(false);
            exoBinding.exoProgress.setClickable(false);
            exoBinding.exoProgress.setEnabled(false);
        }
    }

    private void callLLMoreViewQClicks() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            if (IsLock.equalsIgnoreCase("1")) {
                binding.llMore.setClickable(false);
                binding.llMore.setEnabled(false);
                binding.ivMore.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else if (IsLock.equalsIgnoreCase("2")) {
                binding.llMore.setClickable(false);
                binding.llMore.setEnabled(false);
                binding.ivMore.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                binding.llMore.setClickable(true);
                binding.llMore.setEnabled(true);
                binding.ivMore.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        } else {
            binding.llMore.setClickable(false);
            binding.llMore.setEnabled(false);
            binding.ivMore.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        if (BWSApplication.isNetworkConnected(ctx)) {
            if (IsLock.equalsIgnoreCase("1")) {
                exoBinding.llViewQueue.setClickable(false);
                exoBinding.llViewQueue.setEnabled(false);
                exoBinding.ivViewQueue.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else if (IsLock.equalsIgnoreCase("2")) {
                exoBinding.llViewQueue.setClickable(false);
                exoBinding.llViewQueue.setEnabled(false);
                exoBinding.ivViewQueue.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                exoBinding.llViewQueue.setClickable(true);
                exoBinding.llViewQueue.setEnabled(true);
                exoBinding.ivViewQueue.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        } else {
            exoBinding.llViewQueue.setClickable(false);
            exoBinding.llViewQueue.setEnabled(false);
            exoBinding.ivViewQueue.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    private void callDownload() {
        if (!url.equalsIgnoreCase("")) {
            disableDownload();
            byte[] EncodeBytes = new byte[1024];
            List<String> url1 = new ArrayList<>();
            List<String> name1 = new ArrayList<>();
            List<String> downloadPlaylistId = new ArrayList<>();
            SharedPreferences sharedx = getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
            Gson gson1 = new Gson();
            String json = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson1));
            String json1 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson1));
            String json2 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson1));
            if (!json1.equalsIgnoreCase(String.valueOf(gson1))) {
                Type type = new TypeToken<List<String>>() {
                }.getType();
                List<String> fileNameList = gson1.fromJson(json, type);
                List<String> audioFile1 = gson1.fromJson(json1, type);
                List<String> playlistId1 = gson1.fromJson(json2, type);
                if (fileNameList.size() != 0) {
                    url1.addAll(audioFile1);
                    name1.addAll(fileNameList);
                    downloadPlaylistId.addAll(playlistId1);
                }
            }
            url1.add(url);
            name1.add(name);
            downloadPlaylistId.add("");
            if (url1.size() != 0) {
                SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                Gson gson = new Gson();
                String urlJson = gson.toJson(url1);
                String nameJson = gson.toJson(name1);
                String playlistIdJson = gson.toJson(downloadPlaylistId);
                editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson);
                editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson);
                editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
                editor.commit();
            }
            DownloadMedia downloadMedia = new DownloadMedia(getApplicationContext());
            downloadMedia.encrypt1(url1, name1, downloadPlaylistId);

            exoBinding.pbProgress.setVisibility(View.VISIBLE);
            exoBinding.ivDownloads.setVisibility(View.GONE);
            SaveMedia(EncodeBytes, FileUtils.getFilePath(getApplicationContext(), name));
        }
    }

    private void disableDownload() {
        exoBinding.ivDownloads.setImageResource(R.drawable.ic_download_white_icon);
        exoBinding.ivDownloads.setColorFilter(getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
        exoBinding.llDownload.setClickable(false);
        exoBinding.llDownload.setEnabled(false);
    }

    private void SaveMedia(byte[] EncodeBytes, String dirPath) {
        class SaveMedia extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DownloadAudioDetails downloadAudioDetails = new DownloadAudioDetails();
                if (queuePlay) {
                    downloadAudioDetails.setID(addToQueueModelList.get(position).getID());
                    downloadAudioDetails.setName(addToQueueModelList.get(position).getName());
                    downloadAudioDetails.setAudioFile(addToQueueModelList.get(position).getAudioFile());
                    downloadAudioDetails.setAudioDirection(addToQueueModelList.get(position).getAudioDirection());
                    downloadAudioDetails.setAudiomastercat(addToQueueModelList.get(position).getAudiomastercat());
                    downloadAudioDetails.setAudioSubCategory(addToQueueModelList.get(position).getAudioSubCategory());
                    downloadAudioDetails.setImageFile(addToQueueModelList.get(position).getImageFile());
                    downloadAudioDetails.setLike(addToQueueModelList.get(position).getLike());
                    downloadAudioDetails.setDownload("1");
                    downloadAudioDetails.setAudioDuration(addToQueueModelList.get(position).getAudioDuration());
                    downloadAudioDetails.setIsSingle("1");
                    downloadAudioDetails.setPlaylistId("");
                } else if (audioPlay) {
                    downloadAudioDetails.setID(mainPlayModelList.get(position).getID());
                    downloadAudioDetails.setName(mainPlayModelList.get(position).getName());
                    downloadAudioDetails.setAudioFile(mainPlayModelList.get(position).getAudioFile());
                    downloadAudioDetails.setAudioDirection(mainPlayModelList.get(position).getAudioDirection());
                    downloadAudioDetails.setAudiomastercat(mainPlayModelList.get(position).getAudiomastercat());
                    downloadAudioDetails.setAudioSubCategory(mainPlayModelList.get(position).getAudioSubCategory());
                    downloadAudioDetails.setImageFile(mainPlayModelList.get(position).getImageFile());
                    downloadAudioDetails.setLike(mainPlayModelList.get(position).getLike());
                    downloadAudioDetails.setDownload("1");
                    downloadAudioDetails.setAudioDuration(mainPlayModelList.get(position).getAudioDuration());
                    downloadAudioDetails.setIsSingle("1");
                    downloadAudioDetails.setPlaylistId("");
                    downloadAudioDetails.setIsDownload("pending");
                    downloadAudioDetails.setDownloadProgress(0);
                }
                DatabaseClient.getInstance(getApplicationContext())
                        .getaudioDatabase()
                        .taskDao()
                        .insertMedia(downloadAudioDetails);
                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {
                disableDownload();
                GetMedia2();
                super.onPostExecute(aVoid);
            }
        }

        SaveMedia st = new SaveMedia();
        st.execute();
    }

    public void GetMedia2() {
        downloadAudioDetailsList1 = new ArrayList<>();
        class GetMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                downloadAudioDetailsList1 = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .getaudioByPlaylist(url, "");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (!url.equalsIgnoreCase("")) {
                    if (downloadAudioDetailsList1.size() != 0) {
                        if (downloadAudioDetailsList1.get(0).getDownload().equalsIgnoreCase("1")) {
                            exoBinding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                            exoBinding.llDownload.setClickable(false);
                            exoBinding.llDownload.setEnabled(false);
                            exoBinding.ivDownloads.setColorFilter(getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
                        } else/* if (!mainPlayModelList.get(position).getDownload().equalsIgnoreCase("")) */ {
                            exoBinding.llDownload.setClickable(true);
                            exoBinding.llDownload.setEnabled(true);
                            exoBinding.ivDownloads.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                            exoBinding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                        }
                    } else/* if (!mainPlayModelList.get(position).getDownload().equalsIgnoreCase("")) */ {
                        exoBinding.llDownload.setClickable(true);
                        exoBinding.llDownload.setEnabled(true);
                        exoBinding.ivDownloads.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                        exoBinding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                    }
                }
                getMediaByPer();
                super.onPostExecute(aVoid);
            }
        }
        GetMedia st = new GetMedia();
        st.execute();
    }

    private void getMediaByPer() {
        class getMediaByPer extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                downloadPercentage = DatabaseClient.getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .getDownloadProgress(url, "");

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                if (downloadAudioDetailsList1.size() != 0) {
                    if (downloadPercentage <= 100) {
                        if (downloadPercentage == 100) {
                            exoBinding.pbProgress.setVisibility(View.GONE);
                            exoBinding.ivDownloads.setVisibility(View.VISIBLE);
//                            handler1.removeCallbacks(UpdateSongTime1);
                        } else {
                            exoBinding.pbProgress.setVisibility(View.VISIBLE);
                            exoBinding.ivDownloads.setVisibility(View.GONE);
                            exoBinding.pbProgress.setIndeterminate(false);
                            exoBinding.pbProgress.setProgress(downloadPercentage);
                            getMediaByPer();
//                             handler1.postDelayed(UpdateSongTime1, 500);
                        }
                    } else {
                        exoBinding.pbProgress.setVisibility(View.GONE);
                        exoBinding.ivDownloads.setVisibility(View.VISIBLE);
//                        handler1.removeCallbacks(UpdateSongTime1);
                    }
                } else {
                    exoBinding.pbProgress.setVisibility(View.GONE);
                    exoBinding.ivDownloads.setVisibility(View.VISIBLE);
                }
                super.onPostExecute(aVoid);
            }
        }
        getMediaByPer st = new getMediaByPer();
        st.execute();
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
                        fileDescriptor = FileUtils.getTempFileDescriptor1(getApplicationContext(), descriptor);
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
                MakeArray();
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
            binding.tvNowPlaying.setText("");
        } else {
            binding.tvNowPlaying.setText(R.string.NOW_PLAYING_FROM);
            isDisclaimer = 0;
        }
        if (mainPlayModelList.get(ps).getPlaylistID() == null) {
            mainPlayModelList.get(ps).setPlaylistID("");
        }
        myBitmap = getMediaBitmap(mainPlayModelList.get(ps).getImageFile());
        binding.tvName.setText(mainPlayModelList.get(ps).getName());
        if (mainPlayModelList.get(ps).getAudioDirection().equalsIgnoreCase("")) {
            binding.llDirection.setVisibility(View.GONE);
        } else {
            binding.llDirection.setVisibility(View.VISIBLE);
            binding.tvDireDesc.setText(mainPlayModelList.get(ps).getAudioDirection());
        }
        binding.tvTitle.setText(mainPlayModelList.get(ps).getAudiomastercat());
        binding.tvDesc.setText(mainPlayModelList.get(ps).getAudioSubCategory());

        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                1, 1, 0.92f, 0);
        binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        exoBinding.tvSongTime.setText(mainPlayModelList.get(ps).getAudioDuration());
        if (url.equalsIgnoreCase("")) {
            Glide.with(ctx).load(R.drawable.disclaimer).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
        } else {
            Glide.with(ctx).load(mainPlayModelList.get(ps).getImageFile()).thumbnail(0.05f)
                    .placeholder(R.drawable.disclaimer).error(R.drawable.disclaimer)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
        }
        if (mainPlayModelList.get(ps).getLike().equalsIgnoreCase("1")) {
            exoBinding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
        } else if (mainPlayModelList.get(ps).getLike().equalsIgnoreCase("0")) {
            exoBinding.ivLike.setImageResource(R.drawable.ic_unlike_icon);
        } else {
            exoBinding.ivLike.setImageResource(R.drawable.ic_unlike_icon);
        }
        GetMedia2();
//            binding.gridLayout.addView(view);
//            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
//            layoutParams.width = 0;
//            layoutParams.height = 0;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                layoutParams.columnSpec = GridLayout.spec(i % 1, 1f);
//                layoutParams.rowSpec = GridLayout.spec(i / 1, 1f);
//            }
//            layoutParams.bottomMargin = 10;
//            layoutParams.leftMargin = 10;
//            layoutParams.topMargin = 10;
//            layoutParams.rightMargin = 10;
//            view.setLayoutParams(layoutParams);
    }

    private void setCurrentOutputView(@Nullable SurfaceView surfaceView) {
        currentOutputView = surfaceView;
        if (surfaceView != null && surfaceView.getHolder().getSurface() != null) {
            reparent(surfaceView);
        }
    }

    private void attachSurfaceListener(SurfaceView surfaceView) {
        surfaceView
                .getHolder()
                .addCallback(
                        new SurfaceHolder.Callback() {
                            @Override
                            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                                if (surfaceView == currentOutputView) {
                                    reparent(surfaceView);
                                }
                            }

                            @Override
                            public void surfaceChanged(
                                    SurfaceHolder surfaceHolder, int format, int width, int height) {
                            }

                            @Override
                            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                            }
                        });
    }

    public List<DownloadAudioDetails> GetAllMedia() {
        class GetTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                downloadAudioDetailsList = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .geAllData1();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (downloadAudioDetailsList.size() != 0) {
                    for (int i = 0; i < downloadAudioDetailsList.size(); i++) {
                        DownloadMedia downloadMedia = new DownloadMedia(getApplicationContext());
                        getDownloadMedia(downloadMedia, downloadAudioDetailsList.get(i).getName(), i);
                    }
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
        View viewed = LayoutInflater.from(ctx).inflate(R.layout.audio_player_custom_layout, null, false);
        exoBinding = DataBindingUtil.inflate(LayoutInflater.from(this)
                , R.layout.audio_player_custom_layout, binding.playerControlView,false);
        binding.playerControlView.addView(exoBinding.getRoot());

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
            String json1 = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json1);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData();
        }
    }

    private void removeArray() {
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
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

        callButtonText(position);
//        if (isOwner && player == null) {
        if (mainPlayModelList.get(position).getAudioFile().equalsIgnoreCase("")) {
            initializePlayerDisclaimer();

        } else {
            initializePlayer();
        }
        setCurrentOutputView(nonFullScreenView);
        PlayerControlView playerControlView = Assertions.checkNotNull(this.binding.playerControlView);
        playerControlView.setPlayer(player);
        playerControlView.setProgressUpdateListener((position, bufferedPosition) -> {
            exoBinding.exoProgress.setPosition(position);
            exoBinding.exoProgress.setBufferedPosition(bufferedPosition);

            exoBinding.tvStartTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(position),
                    TimeUnit.MILLISECONDS.toSeconds(position) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(position))));
        });
//        playerControlView.set
        playerControlView.show();
        InitNotificationAudioPLayer();
    }

}