package com.brainwellnessspa.Services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Activities.AudioPlayerActivity;
import com.brainwellnessspa.DashboardModule.Activities.DashboardActivity;
import com.brainwellnessspa.DashboardModule.Models.AppointmentDetailModel;
import com.brainwellnessspa.DashboardModule.Models.MainAudioModel;
import com.brainwellnessspa.DashboardModule.Models.ViewAllAudioListModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.EncryptDecryptUtils.FileUtils;
import com.brainwellnessspa.LikeModule.Models.LikesHistoryModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.segment.analytics.Properties;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.audioClick;
import static com.brainwellnessspa.DashboardModule.Audio.AudioFragment.IsLock;

public class GlobalInitExoPlayer extends Service {
    public static SimpleExoPlayer player;
    public static int notificationId = 1234;
    public static boolean serviceConected = false, PlayerINIT = false, audioRemove = false;
    public static Bitmap myBitmap = null;
    public static PlayerNotificationManager playerNotificationManager;
    public static String Name, Desc;
    public static boolean isprogressbar = false;
    public static String APP_SERVICE_STATUS = "Foreground";
    public static AudioManager audioManager;
    public static int hundredVolume = 0, currentVolume = 0, maxVolume = 0;
    public static int percent;
    public static String PlayerCurrantAudioPostion = "0";
    Notification notification1;
    GlobalInitExoPlayer globalInitExoPlayer;
    Intent playbackServiceIntent;
    static Bitmap notification_artwork;

    public static void callNewPlayerRelease(/*Context ctx*/) {
        if (player != null) {
            /*JobScheduler scheduler = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                scheduler = (JobScheduler) ctx.getSystemService(JOB_SCHEDULER_SERVICE);
                scheduler.cancel(123);
                Log.d("TAG", "Job cancelled");
            }*/
//                Intent intent = new Intent(ctx , GlobleInItExoPlayer.class);
//                ctx.stopService(intent);
            player.stop();
            player.release();
            player = null;
        }
    }

    public static Bitmap getMediaBitmap(Context ctx, String songImg) {
        class GetMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    if (songImg.equalsIgnoreCase("")) {
                        myBitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.disclaimer);
                    } else {
                        if (!BWSApplication.isNetworkConnected(ctx)) {
                            myBitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.disclaimer);
                        } else {
                            URL url = new URL(songImg);
                            myBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                try {
                    if (songImg.equalsIgnoreCase("")) {
                        myBitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.disclaimer);
                    } else {
                        if (!BWSApplication.isNetworkConnected(ctx)) {
                            myBitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.disclaimer);
                        } else {
                            URL url = new URL(songImg);
                            myBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onPostExecute(aVoid);
            }
        }

        GetMedia st = new GetMedia();
        st.execute();
        return myBitmap;
    }

    public static void relesePlayer() {
        if (player != null) {
//        playerNotificationManager.setPlayer(null);
            player.stop();
            player.release();
            player = null;
        }
    }

    public static String GetSourceName(Context ctx) {
        String myFlagType = "";
        try {
            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            String MyPlaylist = shared.getString(CONSTANTS.PREF_KEY_myPlaylist, "");

            if (AudioFlag.equalsIgnoreCase("MainAudioList") || AudioFlag.equalsIgnoreCase("ViewAllAudioList")) {
                if (MyPlaylist.equalsIgnoreCase("Recently Played")) {
                    myFlagType = MyPlaylist;
                } else if (MyPlaylist.equalsIgnoreCase("Library")) {
                    myFlagType = MyPlaylist;
                } else if (MyPlaylist.equalsIgnoreCase("Get Inspired")) {
                    myFlagType = MyPlaylist;
                } else if (MyPlaylist.equalsIgnoreCase("Popular")) {
                    myFlagType = MyPlaylist;
                } else if (MyPlaylist.equalsIgnoreCase("Top Categories")) {
                    myFlagType = MyPlaylist;
                }
            } else if (AudioFlag.equalsIgnoreCase("LikeAudioList")) {
                myFlagType = "Liked Audios";
            } else if (AudioFlag.equalsIgnoreCase("SubPlayList")) {
                myFlagType = "Playlist";
            } else if (AudioFlag.equalsIgnoreCase("Downloadlist")) {
                myFlagType = "Downloaded Playlists";
            } else if (AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
                myFlagType = "Downloaded Audios";
            } else if (AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
                myFlagType = "Appointment Audios";
            } else if (AudioFlag.equalsIgnoreCase("SearchAudio")) {
                if (MyPlaylist.equalsIgnoreCase("Recommended Search Audio")) {
                    myFlagType = MyPlaylist;
                } else if (MyPlaylist.equalsIgnoreCase("Search Audio")) {
                    myFlagType = MyPlaylist;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
/*
Top Categories  dddd
Recently Played  dddd
Library  ddddd
Get Inspired  dddd
Popular dddd
Queue   nottt
Playlist dddd
Downloaded Playlists ddd
Downloaded Audios ddd
Liked Audios dddd
Recommended Search Audio dddd
Search Audio ddd
Appointment Audios dddd*/
        return myFlagType;
    }

    public static String GetDeviceVolume(Context ctx) {
        try {
            if (audioManager != null) {
                audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
                currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                percent = 100;
                hundredVolume = (int) (currentVolume * percent) / maxVolume;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(hundredVolume);
    }

    public static String GetCurrentAudioPosition() {
        if (player != null) {
            long pos = player.getCurrentPosition();
            PlayerCurrantAudioPostion =
                    String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(pos),
                            TimeUnit.MILLISECONDS.toSeconds(pos) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(pos)));
        } else {
            PlayerCurrantAudioPostion = "0";
        }
        return PlayerCurrantAudioPostion;
    }

    public void GlobleInItPlayer(Context ctx, int position, List<String> downloadAudioDetailsList,
                                 ArrayList<MainPlayModel> mainPlayModelList, String playerType) {
        SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        String UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        String ViewType = shared.getString(CONSTANTS.PREF_KEY_myPlaylist, "");
        audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        percent = 100;
        hundredVolume = (int) (currentVolume * percent) / maxVolume;
//        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//        final ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
//        TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory();
//        DataSource.Factory dateSourceFactory = new DefaultDataSourceFactory(ctx, Util.getUserAgent(ctx, getPackageName()));
//        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector(trackSelectionFactory));
        player = new SimpleExoPlayer.Builder(ctx.getApplicationContext()).build();
        if (downloadAudioDetailsList.size() != 0) {
//            for (int f = 0; f < downloadAudioDetailsList.size(); f++) {
            if (downloadAudioDetailsList.contains(mainPlayModelList.get(0).getName())) {
//                    DownloadMedia downloadMedia = new DownloadMedia(getApplicationContext());
//                    getDownloadMedia(downloadMedia,downloadAudioDetailsList.get(f).getName());
                  /*  if (filesDownloaded.get(f) != null) {
                        Log.e("Globle Player", mainPlayModelList.get(0).getName());

                        MediaItem mediaItem = MediaItem.fromUri(Uri.parse("file:///" + filesDownloaded.get(f).getPath()));
                        player.setMediaItem(mediaItem);
                    } else {*/

//                    fileDescriptor = new File(FileUtils.getFilePath(ctx,name));
                MediaItem mediaItem = MediaItem.fromUri(FileUtils.getFilePath(ctx, mainPlayModelList.get(0).getName()));
                player.setMediaItem(mediaItem);
//                        Log.e("Globle Player else part", mainPlayModelList.get(0).getName());
//                    }
//                    break;
            } else/* if (f == downloadAudioDetailsList.size() - 1) */ {
                MediaItem mediaItem = MediaItem.fromUri(mainPlayModelList.get(0).getAudioFile());
                player.setMediaItem(mediaItem);
//                    mediaSources[0] = new ExtractorMediaSource(Uri.parse(mainPlayModelList.get(0).getAudioFile()), dataSourceFactory, extractorsFactory, null, Throwable::printStackTrace);
//                    break;
            }
//            }
        } else {
            MediaItem mediaItem1 = MediaItem.fromUri(mainPlayModelList.get(0).getAudioFile());
            player.setMediaItem(mediaItem1);
        }

        for (int i = 1; i < mainPlayModelList.size(); i++) {
            if (downloadAudioDetailsList.size() != 0) {
//                for (int f = 0; f < downloadAudioDetailsList.size(); f++) {
                if (downloadAudioDetailsList.contains(mainPlayModelList.get(i).getName())) {
                      /*  if (filesDownloaded.get(f) != null) {
                            Log.e("Globle Player", mainPlayModelList.get(i).getName());
                            MediaItem mediaItem = MediaItem.fromUri(Uri.parse("file:///" + filesDownloaded.get(f).getPath()));
                            player.addMediaItem(mediaItem);
                            break;
                        } else { */
                    MediaItem mediaItem = MediaItem.fromUri(FileUtils.getFilePath(ctx, mainPlayModelList.get(i).getName()));
                    player.addMediaItem(mediaItem);
//                            Log.e("Globle Player else part", mainPlayModelList.get(i).getName());
//                        break;
//                        }
                } else /*if (f == downloadAudioDetailsList.size() - 1) */ {
                    MediaItem mediaItem = MediaItem.fromUri(mainPlayModelList.get(i).getAudioFile());
                    player.addMediaItem(mediaItem);
//                        break;
//                        mediaSources[i] = new ExtractorMediaSource(Uri.parse(mainPlayModelList.get(i).getAudioFile()), dataSourceFactory, extractorsFactory, null, Throwable::printStackTrace);
                }
//                }
            } else {
//                mediaSources[i] = new ExtractorMediaSource(Uri.parse(mainPlayModelList.get(i).getAudioFile()), dataSourceFactory, extractorsFactory, null, Throwable::printStackTrace);
                MediaItem mediaItem = MediaItem.fromUri(mainPlayModelList.get(i).getAudioFile());
                player.addMediaItem(mediaItem);
            }
        }

        Properties p = new Properties();
        p.putValue("userId", UserID);
        p.putValue("audioId", mainPlayModelList.get(position).getID());
        p.putValue("audioName", mainPlayModelList.get(position).getName());
        p.putValue("audioDescription", "");
        p.putValue("directions", mainPlayModelList.get(position).getAudioDirection());
        p.putValue("masterCategory", mainPlayModelList.get(position).getAudiomastercat());
        p.putValue("subCategory", mainPlayModelList.get(position).getAudioSubCategory());
        p.putValue("audioDuration", mainPlayModelList.get(position).getAudioDuration());
        p.putValue("position", GetCurrentAudioPosition());
        if (downloadAudioDetailsList.contains(mainPlayModelList.get(position).getName())) {
            p.putValue("audioType", "Downloaded");
        } else {
            p.putValue("audioType", "Streaming");
        }
        p.putValue("source", GetSourceName(ctx));
        p.putValue("playerType", playerType);
        p.putValue("audioService", APP_SERVICE_STATUS);
        p.putValue("bitRate", "");
        p.putValue("sound", String.valueOf(hundredVolume));
        BWSApplication.addToSegment("Audio Playback Started", p, CONSTANTS.track);

        Log.e("Audio Volume", String.valueOf(hundredVolume));
//            String source = "file:////storage/3639-3632/my sounds/Gujarati songs/Chok Puravo d.mp3";
//            // The MediaSource represents the media to be played.
//            MediaSource mediaSource =
//                    new ExtractorMediaSource(
//                            Uri.parse(source), dataSourceFactory, extractorsFactory, null, null);
//            player.prepare(mediaSource);
//        MediaSource mediaSource = mediaSources.length == 1 ? mediaSources[0]
//                : new ConcatenatingMediaSource(mediaSources);
//        player.setMediaSource(mediaSource);
//        player.prepare(mediaSource);
        InitNotificationAudioPLayer(ctx, mainPlayModelList);
        player.prepare();
        player.setWakeMode(C.WAKE_MODE_LOCAL);
        player.setHandleWakeLock(true);
        player.seekTo(position, C.CONTENT_TYPE_MUSIC);
        player.setForegroundMode(true);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .build();
        player.setAudioAttributes(audioAttributes, /* handleAudioFocus= */ true);
        player.setPlayWhenReady(true);
        audioClick = false;
        PlayerINIT = true;
        if (!serviceConected) {
            try {
                playbackServiceIntent = new Intent(ctx.getApplicationContext(), GlobalInitExoPlayer.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(playbackServiceIntent);
                } else {
                    startService(playbackServiceIntent);
                }
                serviceConected = true;
//            bindService(playbackServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /*ComponentName componentName = new ComponentName(ctx, PlayerJobService.class);
        JobInfo info = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            info = new JobInfo.Builder(123, componentName)
                    .setPeriodic(15 * 60 * 1000)
                    .build();
        }
        JobScheduler scheduler = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            scheduler = (JobScheduler) ctx.getSystemService(JOB_SCHEDULER_SERVICE);
            int resultCode = scheduler.schedule(info);
            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                Log.e("TAG", "Job scheduled");
            } else {
                Log.e("TAG", "Job scheduling failed");
            }
        }*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        relesePlayer();
    }

    public void GlobleInItDisclaimer(Context ctx, ArrayList<MainPlayModel> mainPlayModelList) {
        if (player != null) {
            player.stop();
            player.release();
//            player = null;
        }
        player = new SimpleExoPlayer.Builder(ctx.getApplicationContext()).build();
        MediaItem mediaItem1 = MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(R.raw.brain_wellness_spa_declaimer));
        player.setMediaItem(mediaItem1);
        InitNotificationAudioPLayerD(ctx);
        player.prepare();
        player.setWakeMode(C.WAKE_MODE_LOCAL);
        player.setHandleWakeLock(true);
        player.setForegroundMode(true);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .build();

        player.setAudioAttributes(audioAttributes, true);
//        if (miniPlayer == 1) {
        player.setPlayWhenReady(true);
//        }
//       player.addAnalyticsListener(new EventLogger(trackSelector));
        audioClick = false;
        PlayerINIT = true;
    }

    public void AddAudioToPlayer(int size, ArrayList<MainPlayModel> mainPlayModelList, List<String> downloadAudioDetailsList, Context ctx) {
        if (player != null) {
            for (int i = size; i < mainPlayModelList.size(); i++) {
                if (downloadAudioDetailsList.size() != 0) {
//                    for (int f = 0; f < downloadAudioDetailsList.size(); f++) {
                    if (downloadAudioDetailsList.contains(mainPlayModelList.get(i).getName())) {
                        File extStore = FileUtils.readFile1(FileUtils.getFilePath(ctx, mainPlayModelList.get(i).getName()));
                        if (extStore.exists()) {
                            MediaItem mediaItem = MediaItem.fromUri(FileUtils.getFilePath(ctx, mainPlayModelList.get(i).getName()));
                            player.addMediaItem(i, mediaItem);
                        } else {
                            MediaItem mediaItem = MediaItem.fromUri(mainPlayModelList.get(i).getAudioFile());
                            player.addMediaItem(i, mediaItem);
                        }
                        player.prepare();
                        Log.e("Globle Player else part", mainPlayModelList.get(i).getName());
                    } else/* if (f == downloadAudioDetailsList.size() - 1)*/ {
                        MediaItem mediaItem = MediaItem.fromUri(mainPlayModelList.get(i).getAudioFile());
                        player.addMediaItem(i, mediaItem);
                        player.prepare();
//                        mediaSources[i] = new ExtractorMediaSource(Uri.parse(mainPlayModelList.get(i).getAudioFile()), dataSourceFactory, extractorsFactory, null, Throwable::printStackTrace);
                    }
//                    }
                } else {
//                mediaSources[i] = new ExtractorMediaSource(Uri.parse(mainPlayModelList.get(i).getAudioFile()), dataSourceFactory, extractorsFactory, null, Throwable::printStackTrace);
                    MediaItem mediaItem = MediaItem.fromUri(mainPlayModelList.get(i).getAudioFile());
                    player.addMediaItem(i, mediaItem);
                    player.prepare();
                }
            }
        }
    }

    public void InitNotificationAudioPLayer(Context ctx, ArrayList<MainPlayModel> mainPlayModelList) {
        int position = 0;
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                ctx,
                "10001",
                R.string.playback_channel_name,
                notificationId,
                new PlayerNotificationManager.MediaDescriptionAdapter() {
                    @Override
                    public String getCurrentContentTitle(Player players) {
                        return mainPlayModelList.get(players.getCurrentWindowIndex()).getName();
                    }

                    @Nullable
                    @Override
                    public PendingIntent createCurrentContentIntent(Player player) {
                        /*int window = player.getCurrentWindowIndex();
                        return createPendingIntent(window);*/
                        Intent intent = new Intent(ctx, AudioPlayerActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        return PendingIntent.getActivity(ctx, 0, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    @Nullable
                    @Override
                    public String getCurrentContentText(Player players) {
                        return mainPlayModelList.get(players.getCurrentWindowIndex()).getAudioDirection();
                    }

                    @Nullable
                    @Override
                    public Bitmap getCurrentLargeIcon(Player players, PlayerNotificationManager.BitmapCallback callback) {
                      /*  int window = player.getCurrentWindowIndex();
                        Bitmap largeIcon = getLargeIcon(window);
                        if (largeIcon == null && getLargeIconUri(window) != null) {
                            // load bitmap async
                            loadBitmap(getLargeIconUri(window), callback);
                            return getPlaceholderBitmap();
                        }
                        return largeIcon;callback.onBitmap(myBitmap)*/
                        getMediaBitmap(ctx, mainPlayModelList.get(players.getCurrentWindowIndex()).getImageFile());
                        Log.e("IMAGES NOTIFICATION", mainPlayModelList.get(players.getCurrentWindowIndex()).getImageFile());
                        return myBitmap;
                    }
                },

                new PlayerNotificationManager.NotificationListener() {
                    @Override
                    public void onNotificationPosted(int notificationId, @NotNull Notification notification, boolean ongoing) {
                        notification1 = notification;
                    }

                    @Override
                    public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                        if (dismissedByUser) {
                            // Do what the app wants to do when dismissed by the user,
                            // like calling stopForeground(true); or stopSelf();
                        }
                        stopSelf();
                    }
                });

        if (player != null) {
            position = player.getCurrentWindowIndex();
        } else {
            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
        }
        if (!mainPlayModelList.get(position).getAudioFile().equalsIgnoreCase("")) {
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

        playerNotificationManager.setSmallIcon(R.drawable.dark_logo);
        playerNotificationManager.setColor(Color.BLACK);
        playerNotificationManager.setColorized(true);
        playerNotificationManager.setBadgeIconType(NotificationCompat.BADGE_ICON_NONE);
        playerNotificationManager.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        playerNotificationManager.setUseChronometer(true);
        playerNotificationManager.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        playerNotificationManager.setUsePlayPauseActions(true);
        playerNotificationManager.setPlayer(player);
    }

    public void InitNotificationAudioPLayerD(Context ctx) {
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                ctx,
                "10001",
                R.string.playback_channel_name,
                notificationId,
                new PlayerNotificationManager.MediaDescriptionAdapter() {
                    @Override
                    public String getCurrentContentTitle(Player players) {
                        return "Disclaimer";
                    }

                    @Nullable
                    @Override
                    public PendingIntent createCurrentContentIntent(Player player) {
                        Intent intent = new Intent(ctx, AudioPlayerActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        return PendingIntent.getActivity(ctx, 0, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    @Nullable
                    @Override
                    public String getCurrentContentText(Player players) {
                        return "The audio shall start playing after the disclaimer";
                    }

                    @Nullable
                    @Override
                    public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                        return BitmapFactory.decodeResource(ctx.getResources(), R.drawable.disclaimer);
                    }
                },

                new PlayerNotificationManager.NotificationListener() {
                    @Override
                    public void onNotificationPosted(int notificationId, @NotNull Notification notification, boolean ongoing) {
                        notification1 = notification;
                    }

                    @Override
                    public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                        if (dismissedByUser) {
                            // Do what the app wants to do when dismissed by the user,
                            // like calling stopForeground(true); or stopSelf();
                        }
                        stopSelf();
                    }
                });

        playerNotificationManager.setFastForwardIncrementMs(0);
        playerNotificationManager.setRewindIncrementMs(0);
        playerNotificationManager.setUseNavigationActions(false);
        playerNotificationManager.setUseNavigationActionsInCompactView(false);
        playerNotificationManager.setSmallIcon(R.drawable.dark_logo);
        playerNotificationManager.setColor(Color.BLACK);
        playerNotificationManager.setColorized(true);
        playerNotificationManager.setBadgeIconType(NotificationCompat.BADGE_ICON_NONE);
        playerNotificationManager.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        playerNotificationManager.setUseChronometer(true);
        playerNotificationManager.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        playerNotificationManager.setUsePlayPauseActions(true);
        playerNotificationManager.setPlayer(player);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(notificationId, notification1, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForeground(notificationId, notification1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        serviceConected = true;
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        new LocalBinder();
        return null;
    }

    public String UpdateMiniPlayer(Context ctx) {
        String AudioFlag = "0";
        SharedPreferences shared1x = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        String expDate = (shared1x.getString(CONSTANTS.PREF_KEY_ExpDate, ""));
//            expDate = "2020-09-29 06:34:10";
        Log.e("Exp Date !!!!", expDate);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date Expdate = new Date();
        try {
            Expdate = format.parse(expDate);
            Log.e("Exp Date Expdate!!!!", String.valueOf(Expdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat1.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date currdate = Calendar.getInstance().getTime();
        Date currdate1 = new Date();
        String currantDateTime = simpleDateFormat1.format(currdate);
        try {
            currdate1 = format.parse(currantDateTime);
            Log.e("currant currdate !!!!", String.valueOf(currdate1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.e("currant Date !!!!", currantDateTime);
        if (Expdate.before(currdate1)) {
            Log.e("app", "Date1 is before Date2");
            IsLock = "1";
        } else if (Expdate.after(currdate1)) {
            Log.e("app", "Date1 is after Date2");
            IsLock = "0";
        } else if (Expdate == currdate1) {
            Log.e("app", "Date1 is equal Date2");
            IsLock = "1";
        }
        try {
            SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            AudioFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");

            SharedPreferences shared2 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
            String UnlockAudioLists = shared2.getString(CONSTANTS.PREF_KEY_UnLockAudiList, "");
            Gson gson1 = new Gson();
            Type type1 = new TypeToken<List<String>>() {
            }.getType();
            List<String> UnlockAudioList = gson1.fromJson(UnlockAudioLists, type1);
            if (!IsLock.equalsIgnoreCase("0") && (AudioFlag.equalsIgnoreCase("MainAudioList")
                    || AudioFlag.equalsIgnoreCase("ViewAllAudioList")
                    || AudioFlag.equalsIgnoreCase("LikeAudioList")
                    || AudioFlag.equalsIgnoreCase("AppointmentDetailList"))) {
                String audioID = "";
                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                Gson gson = new Gson();
                ArrayList<MainPlayModel> arrayList1 = new ArrayList<>();
                String json = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson));

                SharedPreferences sharedd = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedd.edit();
                if (AudioFlag.equalsIgnoreCase("MainAudioList")) {
                    Type type = new TypeToken<ArrayList<MainAudioModel.ResponseData.Detail>>() {
                    }.getType();
                    ArrayList<MainAudioModel.ResponseData.Detail> arrayList = gson.fromJson(json, type);
                    ArrayList<MainAudioModel.ResponseData.Detail> arrayList2 = new ArrayList<>();

                    int size = arrayList.size();
                    for (int i = 0; i < size; i++) {
                        if (UnlockAudioList.contains(arrayList.get(i).getID())) {
                            arrayList2.add(arrayList.get(i));
                        }
                    }
                    if (arrayList2.size() != 0) {
                        for (int i = 0; i < arrayList2.size(); i++) {
                            MainPlayModel mainPlayModel = new MainPlayModel();
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
                            arrayList1.add(mainPlayModel);
                        }
                    }
                    if(arrayList2.size()<arrayList.size()){
                        if(player!=null) {
                            callNewPlayerRelease();
                            audioClick = true;
                        }
                    }

                    if (arrayList2.size() != 0) {
                        String jsonx = gson.toJson(arrayList1);
                        String json11 = gson.toJson(arrayList2);
                        editor.putString(CONSTANTS.PREF_KEY_modelList, json11);
                        editor.putString(CONSTANTS.PREF_KEY_audioList, jsonx);
                    } else {
                        removeSharepref(ctx);
                    }
                } else if (AudioFlag.equalsIgnoreCase("ViewAllAudioList")) {
                    Type type = new TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail>>() {
                    }.getType();
                    ArrayList<ViewAllAudioListModel.ResponseData.Detail> arrayList = gson.fromJson(json, type);
                    ArrayList<ViewAllAudioListModel.ResponseData.Detail> arrayList2 = gson.fromJson(json, type);

                    int size = arrayList.size();
                    for (int i = 0; i < size; i++) {
                        if (UnlockAudioList.contains(arrayList.get(i).getID())) {
                            arrayList2.add(arrayList.get(i));
                        }
                    }
                    if (arrayList2.size() != 0) {
                        for (int i = 0; i < arrayList2.size(); i++) {
                            MainPlayModel mainPlayModel = new MainPlayModel();
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
                            arrayList1.add(mainPlayModel);
                        }
                    }
                    if (arrayList2.size() != 0) {
                        String jsonx = gson.toJson(arrayList1);
                        String json11 = gson.toJson(arrayList2);
                        editor.putString(CONSTANTS.PREF_KEY_modelList, json11);
                        editor.putString(CONSTANTS.PREF_KEY_audioList, jsonx);
                    } else {
                        removeSharepref(ctx);
                    }
                } else if (AudioFlag.equalsIgnoreCase("LikeAudioList")) {
                    Type type = new TypeToken<ArrayList<LikesHistoryModel.ResponseData.Audio>>() {
                    }.getType();
                    ArrayList<LikesHistoryModel.ResponseData.Audio> arrayList = gson.fromJson(json, type);
                    ArrayList<LikesHistoryModel.ResponseData.Audio> arrayList2 = gson.fromJson(json, type);

                    int size = arrayList.size();
                    for (int i = 0; i < size; i++) {
                        if (UnlockAudioList.contains(arrayList.get(i).getID())) {
                            arrayList2.add(arrayList.get(i));
                        }
                    }
                    if (arrayList2.size() != 0) {
                        for (int i = 0; i < arrayList2.size(); i++) {
                            MainPlayModel mainPlayModel = new MainPlayModel();
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
                            arrayList1.add(mainPlayModel);
                        }
                    }
                    if (arrayList2.size() != 0) {
                        String jsonx = gson.toJson(arrayList1);
                        String json11 = gson.toJson(arrayList2);
                        editor.putString(CONSTANTS.PREF_KEY_modelList, json11);
                        editor.putString(CONSTANTS.PREF_KEY_audioList, jsonx);
                    } else {
                        removeSharepref(ctx);
                    }
                } else if (AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
                    Type type = new TypeToken<ArrayList<AppointmentDetailModel.Audio>>() {
                    }.getType();
                    ArrayList<AppointmentDetailModel.Audio> arrayList = gson.fromJson(json, type);
                    ArrayList<AppointmentDetailModel.Audio> arrayList2 = gson.fromJson(json, type);

                    int size = arrayList.size();
                    for (int i = 0; i < size; i++) {
                        if (UnlockAudioList.contains(arrayList.get(i).getID())) {
                            arrayList2.add(arrayList.get(i));
                        }
                    }
                    if (arrayList2.size() != 0) {
                        for (int i = 0; i < arrayList2.size(); i++) {
                            MainPlayModel mainPlayModel = new MainPlayModel();
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
                            arrayList1.add(mainPlayModel);
                        }
                    }
                    if (arrayList2.size() != 0) {
                        String jsonx = gson.toJson(arrayList1);
                        String json11 = gson.toJson(arrayList2);
                        editor.putString(CONSTANTS.PREF_KEY_modelList, json11);
                        editor.putString(CONSTANTS.PREF_KEY_audioList, jsonx);
                    } else {
                        removeSharepref(ctx);
                    }
                }
                if (arrayList1.size() != 0) {
                    editor.putInt(CONSTANTS.PREF_KEY_position, 0);
                    editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                    editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                    editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                    editor.putString(CONSTANTS.PREF_KEY_myPlaylist, shared1.getString(CONSTANTS.PREF_KEY_myPlaylist, ""));
                    editor.putString(CONSTANTS.PREF_KEY_AudioFlag, AudioFlag);
                    editor.apply();
                    editor.commit();
                } else {
                    removeSharepref(ctx);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return AudioFlag;
    }

    private void removeSharepref(Context ctx) {
        SharedPreferences sharedm = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorr = sharedm.edit();
        editorr.remove(CONSTANTS.PREF_KEY_modelList);
        editorr.remove(CONSTANTS.PREF_KEY_audioList);
        editorr.remove(CONSTANTS.PREF_KEY_position);
        editorr.remove(CONSTANTS.PREF_KEY_queuePlay);
        editorr.remove(CONSTANTS.PREF_KEY_audioPlay);
        editorr.remove(CONSTANTS.PREF_KEY_AudioFlag);
        editorr.remove(CONSTANTS.PREF_KEY_PlaylistId);
        editorr.remove(CONSTANTS.PREF_KEY_myPlaylist);
        editorr.clear();
        editorr.commit();
        callNewPlayerRelease();
    }

    public class LocalBinder extends Binder {
        public GlobalInitExoPlayer getService() {
            // Return this instance of LocalService so clients can call public methods
            return GlobalInitExoPlayer.this;
        }
    }
}
