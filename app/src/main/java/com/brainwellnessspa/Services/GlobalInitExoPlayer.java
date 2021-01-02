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
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.EncryptDecryptUtils.FileUtils;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.brainwellnessspa.DashboardModule.Activities.DashboardActivity;
import com.segment.analytics.Properties;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.audioClick;

public class GlobalInitExoPlayer extends Service {
    public static SimpleExoPlayer player;
    public static int notificationId = 1234;
    public static boolean serviceConected = false;
    public static Bitmap myBitmap = null;
    public static PlayerNotificationManager playerNotificationManager;
    Notification notification1;
    GlobalInitExoPlayer globalInitExoPlayer;
    public static String Name, Desc;
    Intent playbackServiceIntent;
    public static boolean isprogressbar = false;
    public static String APP_SERVICE_STATUS = "Foreground";
    public static AudioManager audioManager;
    public static int hundredVolume, currentVolume, maxVolume;
    public static int percent;
    public static String PlayerCurrantAudioPostion="0";

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

            }
        }

        GetMedia st = new GetMedia();
        st.execute();
        return myBitmap;
    }

    public void GlobleInItPlayer(Context ctx, int position, List<String> downloadAudioDetailsList,
                                 ArrayList<MainPlayModel> mainPlayModelList, String playerType) {
        SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        String UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        String ViewType = shared.getString(CONSTANTS.PREF_KEY_myPlaylist, "");

//        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//        final ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
//        TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory();
//        DataSource.Factory dateSourceFactory = new DefaultDataSourceFactory(ctx, Util.getUserAgent(ctx, getPackageName()));
//        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector(trackSelectionFactory));
        player = new SimpleExoPlayer.Builder(ctx.getApplicationContext()).build();
        if (downloadAudioDetailsList.size() != 0) {
            for (int f = 0; f < downloadAudioDetailsList.size(); f++) {
                if (downloadAudioDetailsList.get(f).equalsIgnoreCase(mainPlayModelList.get(0).getName())) {
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
                    break;
                } else if (f == downloadAudioDetailsList.size() - 1) {
                    MediaItem mediaItem = MediaItem.fromUri(mainPlayModelList.get(0).getAudioFile());
                    player.setMediaItem(mediaItem);
//                    mediaSources[0] = new ExtractorMediaSource(Uri.parse(mainPlayModelList.get(0).getAudioFile()), dataSourceFactory, extractorsFactory, null, Throwable::printStackTrace);
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
                    if (downloadAudioDetailsList.get(f).equalsIgnoreCase(mainPlayModelList.get(i).getName())) {
                      /*  if (filesDownloaded.get(f) != null) {
                            Log.e("Globle Player", mainPlayModelList.get(i).getName());
                            MediaItem mediaItem = MediaItem.fromUri(Uri.parse("file:///" + filesDownloaded.get(f).getPath()));
                            player.addMediaItem(mediaItem);
                            break;
                        } else { */
                        MediaItem mediaItem = MediaItem.fromUri(FileUtils.getFilePath(ctx, mainPlayModelList.get(i).getName()));
                        player.addMediaItem(mediaItem);
//                            Log.e("Globle Player else part", mainPlayModelList.get(i).getName());
                        break;
//                        }
                    } else if (f == downloadAudioDetailsList.size() - 1) {
                        MediaItem mediaItem = MediaItem.fromUri(mainPlayModelList.get(i).getAudioFile());
                        player.addMediaItem(mediaItem);
                        break;
//                        mediaSources[i] = new ExtractorMediaSource(Uri.parse(mainPlayModelList.get(i).getAudioFile()), dataSourceFactory, extractorsFactory, null, Throwable::printStackTrace);
                    }
                }
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
        p.putValue("audioType", "");
        p.putValue("source", "");
        p.putValue("playerType", playerType);
        p.putValue("audioService", APP_SERVICE_STATUS);
        p.putValue("bitRate", "");
        p.putValue("sound", GetDeviceVolume(ctx));
        BWSApplication.addToSegment("Audio Playback Started", p, CONSTANTS.track);

        Log.e("Audio Volume", GetDeviceVolume(ctx));
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
//        InitNotificationAudioPLayer(ctx, mainPlayModelList);
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

    public static void relesePlayer() {
        if (player != null) {
//        playerNotificationManager.setPlayer(null);
            player.stop();
            player.release();
            player = null;
        }
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
//        InitNotificationAudioPLayerD(ctx);
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
    }

    public String GetSourceName(String AudioFlag) {
        String myFlagType = "";
        if (AudioFlag.equalsIgnoreCase("Recently Played")) {


        }
        /*Playlist
Downloaded Playlists
Top Categories
Queue
Downloaded Audios
Liked Audios
Recently Played
Library
Get Inspired
Popular
Recommended Search Audio
Search Audio
Appointment Audios*/
        return "";
    }

    public void AddAudioToPlayer(int size, ArrayList<MainPlayModel> mainPlayModelList, List<String> downloadAudioDetailsList, Context ctx) {
        if (player != null) {
            for (int i = size; i < mainPlayModelList.size(); i++) {
                if (downloadAudioDetailsList.size() != 0) {
                    for (int f = 0; f < downloadAudioDetailsList.size(); f++) {
                        if (downloadAudioDetailsList.get(f).equalsIgnoreCase(mainPlayModelList.get(i).getName())) {
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
                        } else if (f == downloadAudioDetailsList.size() - 1) {
                            MediaItem mediaItem = MediaItem.fromUri(mainPlayModelList.get(i).getAudioFile());
                            player.addMediaItem(i, mediaItem);
                            player.prepare();
//                        mediaSources[i] = new ExtractorMediaSource(Uri.parse(mainPlayModelList.get(i).getAudioFile()), dataSourceFactory, extractorsFactory, null, Throwable::printStackTrace);
                        }
                    }
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
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                ctx,
                "10001",
                R.string.playback_channel_name,
                notificationId,
                new PlayerNotificationManager.MediaDescriptionAdapter() {
                    @Override
                    public String getCurrentContentTitle(Player players) {
//                        Log.e("AudioFIle", mainPlayModelList.get(player.getCurrentWindowIndex()).getAudioFile());
                        return mainPlayModelList.get(players.getCurrentWindowIndex()).getName();
                    }

                    @Nullable
                    @Override
                    public PendingIntent createCurrentContentIntent(Player player) {
                        /*int window = player.getCurrentWindowIndex();
                        return createPendingIntent(window);*/
                        Intent intent = new Intent(ctx, DashboardActivity.class);
                        PendingIntent contentPendingIntent = PendingIntent.getActivity
                                (ctx, 0, intent, 0);
                        return contentPendingIntent;
                    }

                    @Nullable
                    @Override
                    public String getCurrentContentText(Player players) {
                        return mainPlayModelList.get(players.getCurrentWindowIndex()).getAudioDirection();
                    }

                    @Nullable
                    @Override
                    public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
//                        getMediaBitmap(ctx, mainPlayModelList.get(player.getCurrentWindowIndex()).getImageFile());
//                        playerNotificationManager.invalidate();
//                        return myBitmap;
                        return getMediaBitmap(ctx, mainPlayModelList.get(player.getCurrentWindowIndex()).getImageFile());
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

        if (!mainPlayModelList.get(player.getCurrentWindowIndex()).getAudioFile().equalsIgnoreCase("")) {
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
        playerNotificationManager.setPriority(NotificationCompat.PRIORITY_HIGH);
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
                        Intent intent = new Intent(ctx, DashboardActivity.class);
                        PendingIntent contentPendingIntent = PendingIntent.getActivity
                                (ctx, 0, intent, 0);
                        return contentPendingIntent;
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
        playerNotificationManager.setPriority(NotificationCompat.PRIORITY_HIGH);
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

    public static String GetDeviceVolume(Context ctx) {
        audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        percent = 100;
        hundredVolume = (int) (currentVolume * percent) / maxVolume;
        return String.valueOf(hundredVolume);
    }

    public static String GetCurrentAudioPosition() {
        if(player!=null) {
            long pos = player.getCurrentPosition();
            PlayerCurrantAudioPostion =
            String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(pos),
                    TimeUnit.MILLISECONDS.toSeconds(pos) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(pos)));
        }else{
            PlayerCurrantAudioPostion="0";
        }
        return PlayerCurrantAudioPostion;
    }

    public class LocalBinder extends Binder {
        public GlobalInitExoPlayer getService() {
            // Return this instance of LocalService so clients can call public methods
            return GlobalInitExoPlayer.this;
        }
    }
}
