package com.brainwellnessspa.Services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.StatFs;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardOldModule.Models.AppointmentDetailModel;
import com.brainwellnessspa.DashboardOldModule.Models.AudioInterruptionModel;
import com.brainwellnessspa.DashboardOldModule.Models.MainAudioModel;
import com.brainwellnessspa.dashboardModule.models.SearchBothModel;
import com.brainwellnessspa.dashboardModule.models.SuggestedModel;
import com.brainwellnessspa.DashboardOldModule.Models.ViewAllAudioListModel;
import com.brainwellnessspa.DashboardOldModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.dashboardModule.activities.MyPlayerActivity;
import com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia;
import com.brainwellnessspa.EncryptDecryptUtils.FileUtils;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.AudioDatabase;

import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.Utility.APINewClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ControlDispatcher;
import com.google.android.exoplayer2.DefaultControlDispatcher;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.segment.analytics.Properties;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.BWSApplication.BatteryStatus;
import static com.brainwellnessspa.BWSApplication.DB;
import static com.brainwellnessspa.BWSApplication.PlayerAudioId;
import static com.brainwellnessspa.BWSApplication.appStatus;
import static com.brainwellnessspa.BWSApplication.AudioInterrupted;
import static com.brainwellnessspa.BWSApplication.getAudioDataBase;
import static com.brainwellnessspa.BWSApplication.oldSongPos;
import static com.brainwellnessspa.DashboardOldModule.Activities.DashboardActivity.audioClick;
import static com.brainwellnessspa.BWSApplication.IsLock;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.isDownloading;

public class GlobalInitExoPlayer extends Service {
    public static SimpleExoPlayer player;
    public static int notificationId = 1234;
    public static NotificationManager notificationManager;
    public static boolean serviceConected = false, PlayerINIT = false, audioRemove = false, serviceRemoved = false;
    public static Bitmap myBitmap = null;
    public static Bitmap myBitmapImage = null;
    public static Intent intent;
    public static PlayerNotificationManager playerNotificationManager;
    public static MediaSessionCompat mediaSession;
    public static String Name;
    public static AudioManager audioManager;
    public static int hundredVolume = 0, currentVolume = 0, maxVolume = 0;
    public static int percent;
    public static String PlayerCurrantAudioPostion = "0";
    public static MediaSessionConnector mediaSessionConnector;
    List<String> fileNameList = new ArrayList<>(), audioFile = new ArrayList<>(), playlistDownloadId = new ArrayList<>();
    List<DownloadAudioDetails> notDownloadedData;
    Notification notification1;
    Intent playbackServiceIntent;
    LocalBroadcastManager localBroadcastManager;
    Intent localIntent;
    ArrayList<MainPlayModel> mainPlayModelList1 = new ArrayList<>();

    public static void callNewPlayerRelease() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
            PlayerINIT = false;
        }
    }

    public static void callResumePlayer(Context ctx) {
        ArrayList<MainPlayModel> mainPlayModelList = new ArrayList<>();
        if(player!=null){
            if (player.getPlaybackState() == ExoPlayer.STATE_IDLE && AudioInterrupted) {
                AudioInterrupted = false;
                player.setPlayWhenReady(true);
                player.seekTo(player.getCurrentWindowIndex(),player.getCurrentPosition());
                player.prepare();
                SharedPreferences sharedsa = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String json = sharedsa.getString(CONSTANTS.PREF_KEY_PlayerAudioList, String.valueOf(gson));
                if (!json.equalsIgnoreCase(String.valueOf(gson))) {
                    Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                    }.getType();
                    mainPlayModelList = gson.fromJson(json, type);
                }
                GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
                globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList);
                Log.e("Exo PLayer Net:", "Player Resume after Net");
            }
        }
    }

    public static long getSpace() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = 0;
         bytesAvailable = (stat.getBlockSizeLong() * stat.getAvailableBlocksLong()) / (1024 * 1024);
        Log.e("My Space", "Available MB : " + bytesAvailable);
        return bytesAvailable;
    }

    public static Bitmap getMediaBitmap(Context ctx, String songImg) {
      /*  class GetMedia extends AsyncTask<String, Void, Bitmap> {
            @Override
            protected Bitmap doInBackground(String... params) {
                if (songImg.equalsIgnoreCase("") || !BWSApplication.isNetworkConnected(ctx)) {
                    myBitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_music_icon);
                } else {
                    try {
                        URL url = new URL(songImg);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                            connection.setDoInput(true);
                        connection.connect();
                        InputStream is = connection.getInputStream();
                        myBitmap = BitmapFactory.decodeStream(is);
                    } catch (IOException | OutOfMemoryError e) {
                        if (e.getMessage().equalsIgnoreCase("http://brainwellnessspa.com.au/bwsapi/public/images/AUDIO/")) {
                            myBitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_music_icon);
                        } else {
                            System.out.println(e);
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                myBitmapImage = result;
                super.onPostExecute(result);
            }
        }*/

        AudioDatabase.databaseWriteExecutor1.execute(() -> {
            if (songImg.equalsIgnoreCase("") || !BWSApplication.isNetworkConnected(ctx)) {
                myBitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_music_icon);
            } else {
                try {
                    URL url = new URL(songImg);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    InputStream is = connection.getInputStream();
                    myBitmap = BitmapFactory.decodeStream(is);
                } catch (IOException e) {
                    if (e.getMessage().equalsIgnoreCase("http://brainwellnessspa.com.au/bwsapi/public/images/AUDIO/")) {
                        myBitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_music_icon);
                    } else {
                        System.out.println(e);
                    }
                }
            }
        });
//        GetMedia st = new GetMedia();
//        st.execute();
        return myBitmap;
    }

    public static void relesePlayer(Context context) {
        SharedPreferences shared2 = context.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        String UserID = (shared2.getString(CONSTANTS.PREFE_ACCESS_UserID, ""));
        String CoUserID = (shared2.getString(CONSTANTS.PREFE_ACCESS_CoUserID, ""));
        Properties p = new Properties();
        p.putValue("userId", UserID);
        p.putValue("Screen", "Dashboard");
        BWSApplication.addToSegment("Application Killed", p, CONSTANTS.track);
        if (player != null) {
            try {
                mediaSession.release();
                mediaSessionConnector.setPlayer(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            playerNotificationManager.setPlayer(null);
//            player.stop();
            player.release();
            player = null;
//            player = null;
            PlayerINIT = false;
        }


//        if (player != null) {
//            mediaSession.release();
//            mediaSessionConnector.setPlayer(null);
//            playerNotificationManager.setPlayer(null);
//            player.release();
//            player = null;
//           /* mediaSession.setActive(false);
//            playerNotificationManager.setPlayer(null);
//            player.release();
//            notificationManager.cancel(notificationId);
////            player = null;
//            if (mediaSession != null) {
//                mediaSession.setActive(false);
//                mediaSession.release();
//            }*/
//            PlayerINIT = false;
//        }

    }

    public static String GetSourceName(Context ctx) {
        String myFlagType = "";
        try {
            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
            String MyPlaylist = shared.getString(CONSTANTS.PREF_KEY_PlayFrom, "");

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
            myFlagType = "";
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
//        relesePlayer();

        SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        String UserID = (shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, ""));
        String CoUserID = (shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, ""));
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
        audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        percent = 100;
        hundredVolume = (int) (currentVolume * percent) / maxVolume;
        localIntent = new Intent("play_pause_Action");
        localBroadcastManager = LocalBroadcastManager.getInstance(ctx);
        player = new SimpleExoPlayer.Builder(ctx.getApplicationContext()).build();
        if (downloadAudioDetailsList.size() != 0) {
            if (downloadAudioDetailsList.contains(mainPlayModelList.get(0).getName())) {
                MediaItem mediaItem = MediaItem.fromUri(FileUtils.getFilePath(ctx, mainPlayModelList.get(0).getName()));
                player.setMediaItem(mediaItem);
            } else{
                MediaItem mediaItem = MediaItem.fromUri(mainPlayModelList.get(0).getAudioFile());
                player.setMediaItem(mediaItem);
            }
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

        InitNotificationAudioPLayer(ctx, mainPlayModelList);
        Properties p = new Properties();
        p.putValue("userId", UserID);
        p.putValue("coUserId", CoUserID);
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
        p.putValue("playerType", "Main");
        p.putValue("audioService", appStatus(ctx));
        p.putValue("bitRate", "");
        p.putValue("sound", String.valueOf(hundredVolume));
        BWSApplication.addToSegment("Audio Playback Started", p, CONSTANTS.track);
        Log.e("Audio Volume", String.valueOf(hundredVolume));
        getMediaBitmap(ctx, mainPlayModelList.get(position).getImageFile());
        player.prepare();
        player.setWakeMode(C.WAKE_MODE_NONE);
        player.setHandleAudioBecomingNoisy(true);
        player.setHandleWakeLock(true);
        player.seekTo(position, 0);
        player.setForegroundMode(true);
        if(player.getDeviceVolume() != 2){
            player.setDeviceVolume(2);
        }

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .build();
        player.setAudioAttributes(audioAttributes, /* handleAudioFocus= */ true);
        player.setPlayWhenReady(true);
        audioClick = false;
        PlayerINIT = true;
        player.addListener(new ExoPlayer.EventListener() {

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.v("TAG", "Listener-onTracksChanged... MINI PLAYER");

                oldSongPos = 0;
                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String json = shared.getString(CONSTANTS.PREF_KEY_PlayerAudioList, String.valueOf(gson));
                ArrayList<MainPlayModel>  mainPlayModelList1x = new ArrayList<>();
                if (!json.equalsIgnoreCase(String.valueOf(gson))) {
                    Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                    }.getType();
                   mainPlayModelList1x = gson.fromJson(json, type);
                }

//                        myBitmap = getMediaBitmap(getActivity(), mainPlayModelList.get(player.getCurrentWindowIndex()).getImageFile());
                PlayerAudioId = mainPlayModelList1x.get(player.getCurrentWindowIndex()).getID();
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
//                        myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(player.getCurrentWindowIndex()).getImageFile());
                if (player.getPlaybackState() == ExoPlayer.STATE_BUFFERING) {
                } else if (isPlaying) {
                    localIntent.putExtra("MyData", "play");
                    localBroadcastManager.sendBroadcast(localIntent);
                } else if (!isPlaying) {
                    localIntent.putExtra("MyData", "pause");
                    localBroadcastManager.sendBroadcast(localIntent);
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                String intruptMethod = "";
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
                String AudioType = "";
                if (downloadAudioDetailsList.contains(mainPlayModelList.get(position).getName())) {
                    p.putValue("audioType", "Downloaded");
                    AudioType = "Downloaded";
                } else {
                    p.putValue("audioType", "Streaming");
                    AudioType = "Streaming";
                }
                p.putValue("source", GetSourceName(ctx));
                p.putValue("playerType", "Main");
                p.putValue("audioService", appStatus(ctx));
                p.putValue("bitRate", "");
                p.putValue("sound", String.valueOf(hundredVolume));
                if (error.type == ExoPlaybackException.TYPE_SOURCE) {
                    p.putValue("interruptionMethod", error.getMessage() + " " + error.getSourceException().getMessage());
                    intruptMethod = error.getMessage() + " " + error.getSourceException().getMessage();
                    Log.e("onPlaybackError", error.getMessage() + " " + error.getSourceException().getMessage());
                } else if (error.type == ExoPlaybackException.TYPE_RENDERER) {
                    p.putValue("interruptionMethod", error.getMessage() + " " + error.getRendererException().getMessage());
                    intruptMethod = error.getMessage() + " " + error.getRendererException().getMessage();
                    Log.e("onPlaybackError", error.getMessage() + " " + error.getRendererException().getMessage());
                } else if (error.type == ExoPlaybackException.TYPE_UNEXPECTED) {
                    p.putValue("interruptionMethod", error.getMessage() + " " + error.getUnexpectedException().getMessage());
                    intruptMethod = error.getMessage() + " " + error.getUnexpectedException().getMessage();
                    Log.e("onPlaybackError", error.getMessage() + " " + error.getUnexpectedException().getMessage());
                } else if (error.type == ExoPlaybackException.TYPE_REMOTE) {
                    p.putValue("interruptionMethod", error.getMessage());
                    intruptMethod = error.getMessage();
                    Log.e("onPlaybackError", error.getMessage());
                } else {
                    p.putValue("interruptionMethod", error.getMessage());
                    intruptMethod = error.getMessage();
                    Log.e("onPlaybackError", error.getMessage());
                }
                AudioInterrupted = true;
                BWSApplication.addToSegment("Audio Interrupted", p, CONSTANTS.track);
                ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
                //should check null because in airplane mode it will be null
                NetworkCapabilities nc;
                float downSpeed = 0;
                int batLevel = 0;
                float upSpeed = 0;

                if (BWSApplication.isNetworkConnected(ctx)) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
                        downSpeed = (float) nc.getLinkDownstreamBandwidthKbps() / 1000;
                        upSpeed = (float) (nc.getLinkUpstreamBandwidthKbps() / 1000);
                    }
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    BatteryManager bm = (BatteryManager) ctx.getSystemService(BATTERY_SERVICE);
                    batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

                }
                try {
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        Call<AudioInterruptionModel> listCall = APINewClient.getClient().getAudioInterruption(CoUserID,UserID,
                                mainPlayModelList.get(position).getID(), mainPlayModelList.get(position).getName(),
                                "", mainPlayModelList.get(position).getAudioDirection()
                                , mainPlayModelList.get(position).getAudiomastercat(),
                                mainPlayModelList.get(position).getAudioSubCategory(),
                                mainPlayModelList.get(position).getAudioDuration()
                                , "", AudioType, "Main", String.valueOf(hundredVolume)
                                , appStatus(ctx), GetSourceName(ctx), GetCurrentAudioPosition(), "",
                                intruptMethod, String.valueOf(batLevel), BatteryStatus,  String.valueOf(downSpeed), String.valueOf(upSpeed),"Android");
                        listCall.enqueue(new Callback<AudioInterruptionModel>() {
                            @Override
                            public void onResponse(Call<AudioInterruptionModel> call, Response<AudioInterruptionModel> response) {
                                AudioInterruptionModel listModel = response.body();

                            }

                            @Override
                            public void onFailure(Call<AudioInterruptionModel> call, Throwable t) {
                            }
                        });

                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == ExoPlayer.STATE_READY) {

                    if (player.getPlayWhenReady()) {

                        localIntent.putExtra("MyData", "play");
                        localBroadcastManager.sendBroadcast(localIntent);

                    } else if (!player.getPlayWhenReady()) {
                        localIntent.putExtra("MyData", "pause");
                        localBroadcastManager.sendBroadcast(localIntent);
                    }

//                        isprogressbar = false;
                } else if (state == ExoPlayer.STATE_BUFFERING) {

                } else if (state == ExoPlayer.STATE_ENDED) {
                    try {
                        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                        Gson gson = new Gson();
                        String json = shared.getString(CONSTANTS.PREF_KEY_PlayerAudioList, String.valueOf(gson));
                        ArrayList<MainPlayModel>  mainPlayModelList1x = new ArrayList<>();
                        if (!json.equalsIgnoreCase(String.valueOf(gson))) {
                            Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                            }.getType();
                            mainPlayModelList1x = gson.fromJson(json, type);
                        }
                        if (mainPlayModelList1x.get(player.getCurrentWindowIndex()).getID().
                                equalsIgnoreCase(mainPlayModelList1x.get(mainPlayModelList1x.size() - 1).getID())) {

                            player.setPlayWhenReady(false);
                            localIntent.putExtra("MyData", "pause");
                            localBroadcastManager.sendBroadcast(localIntent);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("End State: ", e.getMessage());
                    }
                }

            }
        });

        InitNotificationAudioPLayer(ctx, mainPlayModelList);
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
                Log.e("Notification errrr: ", e.getMessage());
            }
        }
    }


    public void GlobleInItDisclaimer(Context ctx, ArrayList<MainPlayModel> mainPlayModelList,int pos) {
        callNewPlayerRelease();
        player = new SimpleExoPlayer.Builder(ctx.getApplicationContext()).build();
        MediaItem mediaItem1;
        if(BWSApplication.isNetworkConnected(ctx)) {
            mediaItem1 = MediaItem.fromUri(mainPlayModelList.get(pos).getAudioFile());
        }else{
            mediaItem1 = MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(R.raw.brain_wellness_spa_declaimer));
        }
        player.setMediaItem(mediaItem1);
        InitNotificationAudioPLayerD(ctx);
        player.prepare();
        player.setWakeMode(C.WAKE_MODE_NONE);
        player.setHandleWakeLock(true);
        player.setForegroundMode(true);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .build();

        player.setAudioAttributes(audioAttributes, true);
//        if (miniPlayer == 1) {
        player.setPlayWhenReady(true);
        if(player.getDeviceVolume() != 2){
            player.setDeviceVolume(2);
        }
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
            InitNotificationAudioPLayer(ctx, mainPlayModelList);
            UpdateNotificationAudioPLayer(ctx);
        }
//        playerNotificationManager.setPlayer(player);
    }

   /* @NonNull
    @Override
    public SimpleExoPlayer createPlayer(Context ctx) {
        return new ToroExoPlayer(toro.context, renderersFactory, trackSelector, loadControl,
                new DefaultBandwidthMeter(), ctx.getc.drmSessionManager, Util.getLooper());
    }

    public static Looper getCurrentOrMainLooper() {
        @Nullable Looper myLooper = Looper.myLooper();
        return myLooper != null ? myLooper : Looper.getMainLooper();
    }
*/

    public void InitNotificationAudioPLayer(Context ctx, ArrayList<MainPlayModel> mainPlayModelList2) {
        int position = 0;
        SharedPreferences sharedsa = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedsa.getString(CONSTANTS.PREF_KEY_PlayerAudioList, String.valueOf(gson));
        if (!json.equalsIgnoreCase(String.valueOf(gson))) {
            Type type = new TypeToken<ArrayList<MainPlayModel>>() {
            }.getType();
            mainPlayModelList1 = gson.fromJson(json, type);
        }
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                ctx,
                "10001",
                R.string.playback_channel_name,
                0,
                notificationId,
                new PlayerNotificationManager.MediaDescriptionAdapter() {
                    @Override
                    public String getCurrentContentTitle(Player players) {
                        SharedPreferences sharedsa = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                        Gson gson = new Gson();
                        String json = sharedsa.getString(CONSTANTS.PREF_KEY_PlayerAudioList, String.valueOf(gson));
                        if (!json.equalsIgnoreCase(String.valueOf(gson))) {
                            Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                            }.getType();
                            mainPlayModelList1 = gson.fromJson(json, type);
                        }
                        int ps = 0;
                        if (player != null) {
                            ps = player.getCurrentWindowIndex();
                        } else {
                            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                            ps = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                        }
                        return mainPlayModelList1.get(ps).getName();
                    }

                    @Nullable
                    @Override
                    public PendingIntent createCurrentContentIntent(Player player1) {
                        intent = new Intent(ctx, MyPlayerActivity.class);
                        intent.putExtra("notification","yes");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        return PendingIntent.getActivity(ctx, 0, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    @Nullable
                    @Override
                    public String getCurrentContentText(Player players) {
                        SharedPreferences sharedsa = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                        Gson gson = new Gson();
                        String json = sharedsa.getString(CONSTANTS.PREF_KEY_PlayerAudioList, String.valueOf(gson));
                        if (!json.equalsIgnoreCase(String.valueOf(gson))) {
                            Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                            }.getType();
                            mainPlayModelList1 = gson.fromJson(json, type);
                        }
                        int ps = 0;
                        if (player != null) {
                            ps = player.getCurrentWindowIndex();
                        } else {
                            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                            ps = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                        }
                        return mainPlayModelList1.get(ps).getAudioDirection();
                    }

                    @Nullable
                    @Override
                    public Bitmap getCurrentLargeIcon(Player players, PlayerNotificationManager.BitmapCallback callback) {
 /*                       int ps = 0;
                        if (player != null) {
                            ps = player.getCurrentWindowIndex();
                        } else {
                            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                            ps = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                        }
                        myBitmap = getMediaBitmap(ctx, mainPlayModelList1.get(ps).getImageFile());*/
                        return myBitmap;
                    }
                },
                new PlayerNotificationManager.NotificationListener() {
                    @Override
                    public void onNotificationPosted(int notificationId, @NotNull Notification notification, boolean ongoing) {
                        if (ongoing) {
                            /*try {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    startForeground(notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
                                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    startForeground(notificationId, notification);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("Start Command: ", e.getMessage());
                            }*/
                        }
                        notification1 = notification;
                    }

                    @Override
                    public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                        if (dismissedByUser) {
                            stopSelf();
                          /*  try {
                                stopForeground(true);
                                serviceConected = false;
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("Notification errrr: ", e.getMessage());
                            }*/
                            // Do what the app wants to do when dismissed by the user,
                            // like calling stopForeground(true); or stopSelf();
                        }
                    }
                });
/*   override fun getCurrentLargeIcon(
        player: Player?,
        callback: PlayerNotificationManager.BitmapCallback?
    ): Bitmap? {
        val mediaDescription = player?.currentTag as MediaDescriptionCompat?
        val uri = mediaDescription?.iconUri ?: return null
        Glide
            .with(context)
            .asBitmap()
            .load(uri)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                    // Nothing to do here
                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    callback?.onBitmap(resource)
                    currentBitmap = resource
                }
            })
        return currentBitmap
    }*/
        if (player != null) {
            position = player.getCurrentWindowIndex();
        } else {
            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            position = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
        }

        if (position == (mainPlayModelList1.size() - 1)) {
            playerNotificationManager.setUseNextAction(true);
            playerNotificationManager.setUseNextActionInCompactView(true);
//            BWSApplication.showToast("Next available", activity);
        }
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
        try {
            mediaSession = new MediaSessionCompat(ctx, ctx.getPackageName());
            mediaSession.setActive(true);
            playerNotificationManager.setMediaSessionToken(mediaSession.getSessionToken());

            mediaSessionConnector = new MediaSessionConnector(mediaSession);
            mediaSessionConnector.setPlayer(player);
            mediaSessionConnector.setMediaMetadataProvider(player1 -> {
                long duration;
                if (player.getDuration() < 0)
                    duration = player.getCurrentPosition();
                else
                    duration = player.getDuration();
                SharedPreferences sharedsaxx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                String jsonxx = sharedsaxx.getString(CONSTANTS.PREF_KEY_PlayerAudioList, String.valueOf(gson));
                if (!jsonxx.equalsIgnoreCase(String.valueOf(gson))) {
                    Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                    }.getType();
                    mainPlayModelList1 = gson.fromJson(jsonxx, type);
                }
                MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
                int ps = 0;
                if (player != null) {
                    ps = player.getCurrentWindowIndex();
                } else {
                    SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                    ps = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder.putString(MediaMetadata.METADATA_KEY_ARTIST, mainPlayModelList1.get(ps).getAudioDirection());
                    builder.putString(MediaMetadata.METADATA_KEY_TITLE, mainPlayModelList1.get(ps).getName());
                }
                builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, mainPlayModelList1.get(ps).getImageFile());
                builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mainPlayModelList1.get(ps).getID());
                try {
                    builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, myBitmap);
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
                if (duration > 0) {
                    builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration);
                }
                return builder.build();
            });
        } catch (Exception e) {
            UpdateNotificationAudioPLayer(ctx);
            e.printStackTrace();
        }
//        }
        playerNotificationManager.setUseNextAction(false);
        playerNotificationManager.setUseNextActionInCompactView(false);
        playerNotificationManager.setUsePreviousAction(false);
        playerNotificationManager.setUsePreviousActionInCompactView(false);
        ControlDispatcher controlDispatcher = new DefaultControlDispatcher(30000, 30000);
        playerNotificationManager.setControlDispatcher(controlDispatcher);
        playerNotificationManager.setSmallIcon(R.drawable.noti_app_logo_icon);
        playerNotificationManager.setColor(ContextCompat.getColor(ctx, R.color.blue));
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
                R.string.playback_channel_name, 0,
                notificationId,
                new PlayerNotificationManager.MediaDescriptionAdapter() {
                    @Override
                    public String getCurrentContentTitle(Player players) {
                        return "Disclaimer";
                    }

                    @Nullable
                    @Override
                    public PendingIntent createCurrentContentIntent(Player player) {
                        intent = new Intent(ctx, MyPlayerActivity.class);
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
                        return BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_music_icon);
                    }
                },

                new PlayerNotificationManager.NotificationListener() {
                    @Override
                    public void onNotificationPosted(int notificationId, @NotNull Notification notification, boolean ongoing) {
                        if (ongoing) {
/*
                            try {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    startForeground(notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
                                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    startForeground(notificationId, notification);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("Start Command: ", e.getMessage());
                            }
*/
                        }
                        notification1 = notification;
                    }

                    @Override
                    public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                        if (dismissedByUser) {
                            stopSelf();
                            // Do what the app wants to do when dismissed by the user,
                            // like calling stopForeground(true); or stopSelf();
                        }
                    }
                });

        mediaSession = new MediaSessionCompat(ctx, ctx.getPackageName());
//        mediaSession.setCaptioningEnabled(true);
        mediaSession.setActive(true);
        playerNotificationManager.setMediaSessionToken(mediaSession.getSessionToken());
        //aa comment delete na krvi
        if(player!= null) {
            mediaSessionConnector = new MediaSessionConnector(mediaSession);
           mediaSessionConnector.setPlayer(player);
            mediaSessionConnector.setMediaMetadataProvider(player -> {
                long duration;
                if (player.getDuration() < 0)
                    duration = player.getCurrentPosition();
                else
                    duration = player.getDuration();

                MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder.putString(MediaMetadata.METADATA_KEY_ARTIST, "The audio shall start playing after the disclaimer");
                    builder.putString(MediaMetadata.METADATA_KEY_TITLE, "Disclaimer");
                }
                builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, String.valueOf(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_music_icon)));
                builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "0");

//                if (duration > 0) {
//                    builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION,  player.getDuration() == C.TIME_UNSET ? -1 : player.getDuration());
//                    builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION,  -1);
//                }

                try {
                    Bitmap icon;
                    icon = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_music_icon);
                    builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, icon);
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }

                return builder.build();
            });
        }
        playerNotificationManager.setUseNextAction(false);
        playerNotificationManager.setUsePreviousAction(false);
        playerNotificationManager.setUseNextActionInCompactView(false);
        playerNotificationManager.setUsePreviousActionInCompactView(false);
        ControlDispatcher controlDispatcher = new DefaultControlDispatcher(0, 0);
        playerNotificationManager.setControlDispatcher(controlDispatcher);
        playerNotificationManager.setSmallIcon(R.drawable.noti_app_logo_icon);
        playerNotificationManager.setColor(ContextCompat.getColor(ctx, R.color.blue));
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
                startForeground(startId, notification1, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForeground(startId, notification1);
            }
            serviceConected = true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Start Command: ", e.getMessage());
        }
        return START_STICKY;
    }

   /* @Override
    public void onDestroy() {
        Log.e("APPLICATION", "App is in onActivityDestroyed");
        BWSApplication.showToast("onDestroy Called", activity);
        relesePlayer(getApplication());
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
        stopForeground(true);
        super.onDestroy();
    }*/

   /* @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("Appplication log", "onTaskRemoved Called");
//        BWSApplication.showToast("onTaskRemoved Called", activity);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
        relesePlayer(getApplicationContext());
        stopForeground(true);
//        stopSelf();
//        stopForeground(true);
//        playerNotificationManager.cancel(notificationId);
        super.onTaskRemoved(rootIntent);
    }*/

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        new LocalBinder();
        return null;
    }

    public String UpdateMiniPlayer(Context ctx, Activity activity) {
        String AudioFlag = "0";
        SharedPreferences shared1x = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
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
            SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            AudioFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");

            SharedPreferences shared2 = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
            String UnlockAudioLists = shared2.getString(CONSTANTS.PREF_KEY_UnLockAudiList, "");
            Gson gson1 = new Gson();
            Type type1 = new TypeToken<List<String>>() {
            }.getType();
            List<String> UnlockAudioList = gson1.fromJson(UnlockAudioLists, type1);
            if (!IsLock.equalsIgnoreCase("0") && (AudioFlag.equalsIgnoreCase("MainAudioList")
                    || AudioFlag.equalsIgnoreCase("ViewAllAudioList")
                    || AudioFlag.equalsIgnoreCase("SearchAudio")
                    || AudioFlag.equalsIgnoreCase("SearchModelAudio")
                    || AudioFlag.equalsIgnoreCase("AppointmentDetailList"))) {
                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                Gson gson = new Gson();
                ArrayList<MainPlayModel> arrayList1 = new ArrayList<>();
                String json = shared.getString(CONSTANTS.PREF_KEY_PlayerAudioList, String.valueOf(gson));

                SharedPreferences sharedd = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedd.edit();
                if (AudioFlag.equalsIgnoreCase("MainAudioList")) {
                    Type type = new TypeToken<ArrayList<MainAudioModel.ResponseData.Detail>>() {
                    }.getType();
                    ArrayList<MainAudioModel.ResponseData.Detail> arrayList = gson.fromJson(json, type), arrayList2 = new ArrayList<>();

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
                            
                            mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                            arrayList1.add(mainPlayModel);
                        }
                    }
                    if (arrayList2.size() < arrayList.size()) {
                        if (player != null) {
                            callNewPlayerRelease();
                            audioClick = true;
                        } else {
                            audioClick = true;
                        }
                        String jsonx = gson.toJson(arrayList1);
                        String json11 = gson.toJson(arrayList2);
                        editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList,jsonx);
                        editor.putString(CONSTANTS.PREF_KEY_MainAudioList,json11);
                        editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
                        editor.putString(CONSTANTS.PREF_KEY_PlayFrom, shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, ""));
                        editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, AudioFlag);
                        editor.apply();
                        editor.commit();
                    }
                } else if (AudioFlag.equalsIgnoreCase("ViewAllAudioList")) {
                    Type type = new TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail>>() {
                    }.getType();
                    ArrayList<ViewAllAudioListModel.ResponseData.Detail> arrayList = gson.fromJson(json, type), arrayList2 = new ArrayList<>();

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
                            mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                            arrayList1.add(mainPlayModel);
                        }
                    }
                    if (arrayList2.size() < arrayList.size()) {
                        if (player != null) {
                            callNewPlayerRelease();
                            audioClick = true;
                        } else {
                            audioClick = true;
                        }
                        String jsonx = gson.toJson(arrayList1);
                        String json11 = gson.toJson(arrayList2);
                        editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList,jsonx);
                        editor.putString(CONSTANTS.PREF_KEY_MainAudioList,json11);
                        editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
                        editor.putString(CONSTANTS.PREF_KEY_PlayFrom, shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, ""));
                        editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, AudioFlag);
                        editor.apply();
                        editor.commit();
                    }
                }  else if (AudioFlag.equalsIgnoreCase("SearchModelAudio")) {
                    Type type = new TypeToken<ArrayList<SearchBothModel.ResponseData>>() {
                    }.getType();
                    ArrayList<SearchBothModel.ResponseData> arrayList = gson.fromJson(json, type), arrayList2 = new ArrayList<>();

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
                            
                            mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                            arrayList1.add(mainPlayModel);
                        }
                    }
                    if (arrayList2.size() < arrayList.size()) {
                        if (player != null) {
                            callNewPlayerRelease();
                            audioClick = true;
                        } else {
                            audioClick = true;
                        }
                        String jsonx = gson.toJson(arrayList1);
                        String json11 = gson.toJson(arrayList2);
                        editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList,jsonx);
                        editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json11);
                        editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                        
                        
                        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
                        editor.putString(CONSTANTS.PREF_KEY_PlayFrom, shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, ""));
                        editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, AudioFlag);
                        editor.apply();
                        editor.commit();
                    }
                } else if (AudioFlag.equalsIgnoreCase("SearchAudio")) {
                    Type type = new TypeToken<ArrayList<SuggestedModel.ResponseData>>() {
                    }.getType();
                    ArrayList<SuggestedModel.ResponseData> arrayList = gson.fromJson(json, type), arrayList2 = new ArrayList<>();

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
                            
                            mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                            arrayList1.add(mainPlayModel);
                        }
                    }
                    if (arrayList2.size() < arrayList.size()) {
                        if (player != null) {
                            callNewPlayerRelease();
                            audioClick = true;
                        } else {
                            audioClick = true;
                        }
                        String jsonx = gson.toJson(arrayList1);
                        String json11 = gson.toJson(arrayList2);
                        editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonx);
                        editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json11);
                        editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                        
                        
                        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
                        editor.putString(CONSTANTS.PREF_KEY_PlayFrom, shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, ""));
                        editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, AudioFlag);
                        editor.apply();
                        editor.commit();
                    }
                } else if (AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
                    Type type = new TypeToken<ArrayList<AppointmentDetailModel.Audio>>() {
                    }.getType();
                    ArrayList<AppointmentDetailModel.Audio> arrayList = gson.fromJson(json, type), arrayList2 = new ArrayList<>();

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
                            
                            mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                            arrayList1.add(mainPlayModel);
                        }
                    }
                    if (arrayList2.size() < arrayList.size()) {
                        if (player != null) {
                            callNewPlayerRelease();
                            audioClick = true;
                        } else {
                            audioClick = true;
                        }
                        String jsonx = gson.toJson(arrayList1);
                        String json11 = gson.toJson(arrayList2);
                        editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList,jsonx );
                        editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json11);
                        editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
                        editor.putString(CONSTANTS.PREF_KEY_PlayFrom, shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, ""));
                        editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, AudioFlag);
                        editor.apply();
                        editor.commit();
                    }
                }
                if (arrayList1.size() == 0) {
                    removeSharepref(ctx);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!isDownloading) {
            getPending(ctx,activity);
        }
        return AudioFlag;
    }

    private void getPending(Context ctx,Activity activity) {
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, AppCompatActivity.MODE_PRIVATE);
        String UserID = shared.getString(CONSTANTS.PREFE_ACCESS_UserID, "");
        String CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "");
        DB = getAudioDataBase(ctx);
        DB.taskDao()
                .getNotDownloadData("Complete",CoUserID).observe((LifecycleOwner) ctx, audioList -> {

            notDownloadedData = new ArrayList<>();
            DB.taskDao()
                    .getNotDownloadData("Complete",CoUserID).removeObserver(audioListx -> {
            });
            if (audioList != null) {
                notDownloadedData.addAll(audioList);

                if (notDownloadedData.size() != 0 && !isDownloading) {
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        SharedPreferences sharedx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
                        Gson gson = new Gson();
                        String json = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson));
                        String json1 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson));
                        String json2 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson));
                        if (!json1.equalsIgnoreCase(String.valueOf(gson))) {
                            Type type = new TypeToken<List<String>>() {
                            }.getType();
                            fileNameList = gson.fromJson(json, type);
                            audioFile = gson.fromJson(json1, type);
                            playlistDownloadId = gson.fromJson(json2, type);
                            if (fileNameList.size() == 0) {
                                for (int i = 0; i < notDownloadedData.size(); i++) {
                                    audioFile.add(notDownloadedData.get(i).getAudioFile());
                                    fileNameList.add(notDownloadedData.get(i).getName());
                                    playlistDownloadId.add(notDownloadedData.get(i).getPlaylistId());
                                }
                            }
                        }
                        SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = shared1.edit();
                        String nameJson = gson.toJson(fileNameList);
                        String urlJson = gson.toJson(audioFile);
                        String playlistIdJson = gson.toJson(playlistDownloadId);
                        editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson);
                        editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson);
                        editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
                        editor.commit();
                        if (fileNameList.size() != 0) {
                            isDownloading = true;
                            DownloadMedia downloadMedia = new DownloadMedia(ctx.getApplicationContext(),activity);
                            downloadMedia.encrypt1(audioFile, fileNameList, playlistDownloadId);
                        }
                    }
                }
            }
        });
    }

    private void removeSharepref(Context ctx) {
        SharedPreferences sharedm = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorr = sharedm.edit();
        editorr.remove(CONSTANTS.PREF_KEY_PlayerAudioList);
        editorr.remove(CONSTANTS.PREF_KEY_MainAudioList);
        editorr.remove(CONSTANTS.PREF_KEY_PlayerPosition);
        editorr.remove(CONSTANTS.PREF_KEY_AudioPlayerFlag);
        editorr.remove(CONSTANTS.PREF_KEY_PlayerPlaylistId);
        editorr.remove(CONSTANTS.PREF_KEY_PlayFrom);
        editorr.clear();
        editorr.apply();
        callNewPlayerRelease();
    }

    public void UpdateNotificationAudioPLayer(Context ctx) {
        SharedPreferences sharedsa = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedsa.getString(CONSTANTS.PREF_KEY_PlayerAudioList, String.valueOf(gson));
        if (!json.equalsIgnoreCase(String.valueOf(gson))) {
            Type type = new TypeToken<ArrayList<MainPlayModel>>() {
            }.getType();
            mainPlayModelList1 = gson.fromJson(json, type);
        }
        try {
            mediaSession = new MediaSessionCompat(ctx, ctx.getPackageName());
            mediaSession.setActive(true);
            playerNotificationManager.setMediaSessionToken(mediaSession.getSessionToken());

            if (player != null) {
//            mediaSession.setPlaybackState(
//                new PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_PLAYING,
//                        player.getCurrentPosition(),1 )
//                        .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
//                        .build());
                mediaSessionConnector = new MediaSessionConnector(mediaSession);
                mediaSessionConnector.setPlayer(player);
                mediaSessionConnector.setMediaMetadataProvider(player1 -> {
                    long duration;
                    if (player.getDuration() < 0)
                        duration = player.getCurrentPosition();
                    else
                        duration = player.getDuration();

                    MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
                    SharedPreferences sharedsa1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                    String json1 = sharedsa1.getString(CONSTANTS.PREF_KEY_PlayerAudioList, String.valueOf(gson));
                    if (!json1.equalsIgnoreCase(String.valueOf(gson))) {
                        Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                        }.getType();
                        mainPlayModelList1 = gson.fromJson(json1, type);
                    }
                    int ps = 0;
                    if (player != null) {
                        ps = player.getCurrentWindowIndex();
                    } else {
                        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                        ps = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder.putString(MediaMetadata.METADATA_KEY_ARTIST, mainPlayModelList1.get(ps).getAudioDirection());
                        builder.putString(MediaMetadata.METADATA_KEY_TITLE, mainPlayModelList1.get(ps).getName());
                    }
                    builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, mainPlayModelList1.get(ps).getImageFile());
                    builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mainPlayModelList1.get(ps).getID());

                    if (duration > 0) {
                        builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration);
                    }

                    try {
                        builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, myBitmap);
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    }

                    return builder.build();
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
//            UpdateNotificationAudioPLayer(ctx);
        }
    }

  /*  @Override
    public long getSupportedPrepareActions() {
        return 0;
    }

    @Override
    public void onPrepare(boolean playWhenReady) {

    }

    @Override
    public void onPrepareFromMediaId(String mediaId, boolean playWhenReady, @Nullable Bundle extras) {

    }

    @Override
    public void onPrepareFromSearch(String query, boolean playWhenReady, @Nullable Bundle extras) {

    }

    @Override
    public void onPrepareFromUri(Uri uri, boolean playWhenReady, @Nullable Bundle extras) {

    }

    @Override
    public boolean onCommand(Player player, ControlDispatcher controlDispatcher, String command, @Nullable Bundle extras, @Nullable ResultReceiver cb) {
        return false;
    }*/

    public class LocalBinder extends Binder {
        public GlobalInitExoPlayer getService() {
            // Return this instance of LocalService so clients can call public methods
            return GlobalInitExoPlayer.this;
        }
    }
}
