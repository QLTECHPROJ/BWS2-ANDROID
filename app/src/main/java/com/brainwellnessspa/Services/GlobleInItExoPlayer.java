package com.brainwellnessspa.Services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.audioClick;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.miniPlayer;

public class GlobleInItExoPlayer extends Service {
    public static SimpleExoPlayer player;
    public static int notificationId = 1234;
    public static boolean serviceConected = false;
    public static Bitmap myBitmap = null;
    public static PlayerNotificationManager playerNotificationManager;
    Notification notification1;
    GlobleInItExoPlayer globleInItExoPlayer;

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

    public void GlobleInItPlayer(Context ctx, int position, List<DownloadAudioDetails> downloadAudioDetailsList,
                                 ArrayList<MainPlayModel> mainPlayModelList, List<File> bytesDownloaded) {

        player = new SimpleExoPlayer.Builder(ctx.getApplicationContext()).build();
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
                } else if (f == downloadAudioDetailsList.size() - 1) {
                    MediaItem mediaItem1 = MediaItem.fromUri(mainPlayModelList.get(0).getAudioFile());
                    player.setMediaItem(mediaItem1);
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
        player.prepare();
        player.setWakeMode(C.WAKE_MODE_LOCAL);
        player.setHandleWakeLock(true);
        player.seekTo(position, C.CONTENT_TYPE_MUSIC);
        player.setForegroundMode(true);
//        InitNotificationAudioPLayer(ctx, mainPlayModelList);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .build();
        player.setAudioAttributes(audioAttributes, /* handleAudioFocus= */ true);
        if (miniPlayer == 1) {
            player.setPlayWhenReady(true);
        }
        audioClick = false;
        if (!serviceConected) {
            try {
                Intent playbackServiceIntent = new Intent(ctx.getApplicationContext(), GlobleInItExoPlayer.class);
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

    public void GlobleInItDisclaimer(Context ctx, ArrayList<MainPlayModel> mainPlayModelList) {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
        player = new SimpleExoPlayer.Builder(ctx.getApplicationContext()).build();
        MediaItem mediaItem1 = MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(R.raw.brain_wellness_spa_declaimer));
        player.setMediaItem(mediaItem1);
//       player.setPlayWhenReady(true);
        player.prepare();
        player.setWakeMode(C.WAKE_MODE_LOCAL);
        player.setHandleWakeLock(true);
        player.setForegroundMode(true);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .build();

        player.setAudioAttributes(audioAttributes, true);
        if (miniPlayer == 1) {
            player.setPlayWhenReady(true);
        }
//        InitNotificationAudioPLayer(ctx, mainPlayModelList);


//       player.addAnalyticsListener(new EventLogger(trackSelector));

        audioClick = false;
    }

    public void InitNotificationAudioPLayer(Context ctx, ArrayList<MainPlayModel> mainPlayModelList) {
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
                        getMediaBitmap(ctx, mainPlayModelList.get(player.getCurrentWindowIndex()).getImageFile());
                        return myBitmap;
                    }

                },
                new PlayerNotificationManager.NotificationListener() {
                    @Override
                    public void onNotificationPosted(int notificationId, @NotNull Notification notification, boolean ongoing) {
/*                        Intent serviceIntent = new Intent(ctx, GlobleInItExoPlayer.class);
                        ctx.stopService(serviceIntent);*/
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

    public class LocalBinder extends Binder {
        public GlobleInItExoPlayer getService() {
            // Return this instance of LocalService so clients can call public methods
            return GlobleInItExoPlayer.this;
        }
    }
}
