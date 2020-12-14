package com.brainwellnessspa.Services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.miniPlayer;

public class GlobleInItExoPlayer extends Service {
    public static SimpleExoPlayer player;
    public static int notificationId = 1234;
    public static Bitmap myBitmap = null;
    public static PlayerNotificationManager playerNotificationManager;

    public static void GlobleInItPlayer(Context ctx, int position, List<DownloadAudioDetails> downloadAudioDetailsList,
                                        ArrayList<MainPlayModel> mainPlayModelList, List<File> bytesDownloaded) {
        if(player!=null){
            player.stop();
            player.release();
            player = null;
        }
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
               }else if(f == downloadAudioDetailsList.size()-1){
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
       player.setWakeMode(2);
       player.seekTo(position, C.CONTENT_TYPE_MUSIC);
       if (miniPlayer == 1) {
           player.setPlayWhenReady(true);
       }
        InitNotificationAudioPLayer(ctx,mainPlayModelList);
   }

   public static void GlobleInItDisclaimer(Context ctx, ArrayList<MainPlayModel> mainPlayModelList) {
       if(player!=null){
           player.stop();
           player.release();
           player = null;
       }
       player = new SimpleExoPlayer.Builder(ctx.getApplicationContext()).build();
       MediaItem mediaItem1 = MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(R.raw.brain_wellness_spa_declaimer));
       player.setMediaItem(mediaItem1);
//       player.setPlayWhenReady(true);
       player.prepare();
       player.setWakeMode(2);
       InitNotificationAudioPLayer(ctx,mainPlayModelList);
       if (miniPlayer == 1) {
           player.setPlayWhenReady(true);
       }
   }

    public static void InitNotificationAudioPLayer(Context ctx, ArrayList<MainPlayModel> mainPlayModelList) {
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
                        getMediaBitmap(ctx,mainPlayModelList.get(player.getCurrentWindowIndex()).getImageFile());
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
    }

    public static Bitmap getMediaBitmap(Context ctx,String songImg) {
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
//        startForeground(1,playerNotificationManager, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
        return null;
    }
}
