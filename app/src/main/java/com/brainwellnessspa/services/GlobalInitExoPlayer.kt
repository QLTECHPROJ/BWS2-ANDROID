package com.brainwellnessspa.services

import android.app.Activity
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaMetadata
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.activities.MyPlayerActivity
import com.brainwellnessspa.dashboardModule.models.*
import com.brainwellnessspa.dashboardOldModule.models.AppointmentDetailModel
import com.brainwellnessspa.dashboardOldModule.transParentPlayer.models.MainPlayModel
import com.brainwellnessspa.encryptDecryptUtils.DownloadMedia
import com.brainwellnessspa.encryptDecryptUtils.FileUtils
import com.brainwellnessspa.roomDataBase.AudioDatabase
import com.brainwellnessspa.roomDataBase.DownloadAudioDetails
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class GlobalInitExoPlayer : Service() {
    var serviceConected = false
    var intent: Intent? = null
    var fileNameList: ArrayList<String> = ArrayList()
    var audioFile: ArrayList<String> = ArrayList<String>()
    var playlistDownloadId: ArrayList<String> = ArrayList<String>()
    private var notDownloadedData: List<DownloadAudioDetails> = ArrayList<DownloadAudioDetails>()
    var notification1: Notification? = null
    private var playbackServiceIntent: Intent? = null
    var localBroadcastManager: LocalBroadcastManager? = null
    var localIntent: Intent? = null
    var mainPlayModelList1 = ArrayList<MainPlayModel>()

    companion object {
        @JvmStatic
        fun callNewPlayerRelease() {
            if (player != null) {
                player.stop()
                player.release()
                player = null
                PlayerINIT = false
            }
        }

        @JvmStatic
        fun callResumePlayer(ctx: Context) {
            var mainPlayModelList = ArrayList<MainPlayModel>()
            if (player != null) {
                if (player.playbackState == ExoPlayer.STATE_IDLE && AudioInterrupted) {
                    AudioInterrupted = false
                    player.playWhenReady = true
                    player.seekTo(player.currentWindowIndex, player.currentPosition)
                    player.prepare()
                    val sharedsa = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                    val gson = Gson()
                    val json = sharedsa.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toString())
                    if (!json.equals(gson.toString(), ignoreCase = true)) {
                        val type = object : TypeToken<ArrayList<MainPlayModel?>?>() {}.type
                        mainPlayModelList = gson.fromJson(json, type)
                    }
                    val globalInitExoPlayer = GlobalInitExoPlayer()
                    globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList)
                    Log.e("Exo PLayer Net:", "Player Resume after Net")
                }
            }
        }

        @JvmStatic
        fun getSpace(): Long {
            val stat = StatFs(Environment.getExternalStorageDirectory().path)
            var bytesAvailable: Long = 0
            bytesAvailable = stat.blockSizeLong * stat.availableBlocksLong / (1024 * 1024)
            Log.e("My Space", "Available MB : $bytesAvailable")
            return bytesAvailable
        }

        @JvmStatic
        fun getMediaBitmap(ctx: Context, songImg: String): Bitmap? {
            /*  class GetMedia extends AsyncTask<String, Void, Bitmap> {
            @Override
            protected Bitmap doInBackground(String... params) {
                if (songImg.equalsIgnoreCase("") || !isNetworkConnected(ctx)) {
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
            AudioDatabase.databaseWriteExecutor1.execute {
                if (songImg.equals("", ignoreCase = true) || !isNetworkConnected(ctx)) {
                    myBitmap = BitmapFactory.decodeResource(ctx.resources, R.drawable.ic_music_icon)
                } else {
                    try {
                        val url = URL(songImg)
                        val connection = url.openConnection() as HttpURLConnection
                        connection.connect()
                        val `is` = connection.inputStream
                        myBitmap = BitmapFactory.decodeStream(`is`)
                    } catch (e: IOException) {
                        if (e.message.equals("http://brainwellnessspa.com.au/bwsapi/public/images/AUDIO/", ignoreCase = true)) {
                            myBitmap = BitmapFactory.decodeResource(ctx.resources, R.drawable.ic_music_icon)
                        } else {
                            println(e)
                        }
                    }
                }
            }
            //        GetMedia st = new GetMedia();
            //        st.execute();
            return myBitmap
        }

        @JvmStatic
        fun relesePlayer(context: Context) {
            val p = Properties()
            p.putValue("Screen", "Dashboard")
            addToSegment("Application Killed", p, CONSTANTS.track)
            if (player != null) {
                try {
                    mediaSession.release()
                    mediaSessionConnector.setPlayer(null)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                playerNotificationManager.setPlayer(null)
                //            player.stop();
                player.release()
                player = null
                //            player = null;
                PlayerINIT = false
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

        @JvmStatic
        fun GetSourceName(ctx: Context): String? {
            var myFlagType: String? = ""
            try {
                val shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val audioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                val myPlaylist = shared.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
                if (audioFlag.equals("MainAudioList", ignoreCase = true) || audioFlag.equals("ViewAllAudioList", ignoreCase = true)) {
                    when {
                        myPlaylist.equals("Recently Played", ignoreCase = true) -> {
                            myFlagType = myPlaylist
                        }
                        myPlaylist.equals("Library", ignoreCase = true) -> {
                            myFlagType = myPlaylist
                        }
                        myPlaylist.equals("Get Inspired", ignoreCase = true) -> {
                            myFlagType = myPlaylist
                        }
                        myPlaylist.equals("Popular", ignoreCase = true) -> {
                            myFlagType = myPlaylist
                        }
                        myPlaylist.equals("Top Categories", ignoreCase = true) -> {
                            myFlagType = myPlaylist
                        }
                    }
                } else if (audioFlag.equals("LikeAudioList", ignoreCase = true)) {
                    myFlagType = "Liked Audios"
                } else if (audioFlag.equals("SubPlayList", ignoreCase = true)) {
                    myFlagType = "Playlist"
                } else if (audioFlag.equals("Downloadlist", ignoreCase = true)) {
                    myFlagType = "Downloaded Playlists"
                } else if (audioFlag.equals("DownloadListAudio", ignoreCase = true)) {
                    myFlagType = "Downloaded Audios"
                } else if (audioFlag.equals("AppointmentDetailList", ignoreCase = true)) {
                    myFlagType = "Appointment Audios"
                } else if (audioFlag.equals("SearchAudio", ignoreCase = true)) {
                    if (myPlaylist.equals("Recommended Search Audio", ignoreCase = true)) {
                        myFlagType = myPlaylist
                    } else if (myPlaylist.equals("Search Audio", ignoreCase = true)) {
                        myFlagType = myPlaylist
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                myFlagType = ""
            }
            return myFlagType
        }

        @JvmStatic
        fun GetDeviceVolume(ctx: Context): String {
            try {
                if (audioManager != null) {
                    audioManager = ctx.getSystemService(AUDIO_SERVICE) as AudioManager
                    currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                    maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                    percent = 100
                    hundredVolume = (currentVolume * percent) / maxVolume
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return hundredVolume.toString()
        }

        @JvmStatic
        fun GetCurrentAudioPosition(): String? {
            PlayerCurrantAudioPostion = if (player != null) {
                val pos = player.currentPosition
                String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(pos), TimeUnit.MILLISECONDS.toSeconds(pos) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(pos)))
            } else {
                "0"
            }
            return PlayerCurrantAudioPostion
        }
    }

    fun GlobleInItPlayer(ctx: Context, position: Int, downloadAudioDetailsList: List<String?>, mainPlayModelList: ArrayList<MainPlayModel>, playerType: String?) {
        //        relesePlayer();
        val shared1 = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        val UserID = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        val CoUserID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        val shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
        audioManager = ctx.getSystemService(AUDIO_SERVICE) as AudioManager
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        percent = 100
        hundredVolume = (currentVolume * percent) / maxVolume
        localIntent = Intent("play_pause_Action")
        localBroadcastManager = LocalBroadcastManager.getInstance(ctx)
        player = SimpleExoPlayer.Builder(ctx.applicationContext).build()
        if (downloadAudioDetailsList.isNotEmpty()) {
            if (downloadAudioDetailsList.contains(mainPlayModelList[0].name)) {
                val mediaItem = MediaItem.fromUri(FileUtils.getFilePath(ctx, mainPlayModelList[0].name))
                player.setMediaItem(mediaItem)
            } else {
                val mediaItem = MediaItem.fromUri(mainPlayModelList[0].audioFile)
                player.setMediaItem(mediaItem)
            }
        } else {
            val mediaItem1 = MediaItem.fromUri(mainPlayModelList[0].audioFile)
            player.setMediaItem(mediaItem1)
        }
        for (i in 1 until mainPlayModelList.size) {
            if (downloadAudioDetailsList.isNotEmpty()) {
                //                for (int f = 0; f < downloadAudioDetailsList.size(); f++) {
                if (downloadAudioDetailsList.contains(mainPlayModelList[i].name)) {
                    /*  if (filesDownloaded.get(f) != null) {
                            Log.e("Globle Player", mainPlayModelList.get(i).getName());
                            MediaItem mediaItem = MediaItem.fromUri(Uri.parse("file:///" + filesDownloaded.get(f).getPath()));
                            player.addMediaItem(mediaItem);
                            break;
                        } else { */
                    val mediaItem = MediaItem.fromUri(FileUtils.getFilePath(ctx, mainPlayModelList[i].name))
                    player.addMediaItem(mediaItem)
                    //                            Log.e("Globle Player else part", mainPlayModelList.get(i).getName());
                    //                        break;
                    //                        }
                } else  /*if (f == downloadAudioDetailsList.size() - 1) */ {
                    val mediaItem = MediaItem.fromUri(mainPlayModelList[i].audioFile)
                    player.addMediaItem(mediaItem)
                    //                        break;
                    //                        mediaSources[i] = new ExtractorMediaSource(Uri.parse(mainPlayModelList.get(i).getAudioFile()), dataSourceFactory, extractorsFactory, null, Throwable::printStackTrace);
                }
                //                }
            } else {
                //                mediaSources[i] = new ExtractorMediaSource(Uri.parse(mainPlayModelList.get(i).getAudioFile()), dataSourceFactory, extractorsFactory, null, Throwable::printStackTrace);
                val mediaItem = MediaItem.fromUri(mainPlayModelList[i].audioFile)
                player.addMediaItem(mediaItem)
            }
        }
        InitNotificationAudioPLayer(ctx, mainPlayModelList)
        val p = Properties()
        p.putValue("audioId", mainPlayModelList[position].id)
        p.putValue("audioName", mainPlayModelList[position].name)
        p.putValue("audioDescription", "")
        p.putValue("directions", mainPlayModelList[position].audioDirection)
        p.putValue("masterCategory", mainPlayModelList[position].audiomastercat)
        p.putValue("subCategory", mainPlayModelList[position].audioSubCategory)
        p.putValue("audioDuration", mainPlayModelList[position].audioDuration)
        p.putValue("position", GetCurrentAudioPosition())
        if (downloadAudioDetailsList.contains(mainPlayModelList[position].name)) {
            p.putValue("audioType", "Downloaded")
        } else {
            p.putValue("audioType", "Streaming")
        }
        p.putValue("source", GetSourceName(ctx))
        p.putValue("playerType", "Main")
        p.putValue("audioService", appStatus(ctx))
        p.putValue("bitRate", "")
        p.putValue("sound", hundredVolume.toString())
        addToSegment("Audio Playback Started", p, CONSTANTS.track)
        Log.e("Audio Volume", hundredVolume.toString())
        getMediaBitmap(ctx, mainPlayModelList[position].imageFile)
        player.prepare()
        player.setWakeMode(C.WAKE_MODE_NONE)
        player.setHandleAudioBecomingNoisy(true)
        player.setHandleWakeLock(true)
        player.seekTo(position, 0)
        player.setForegroundMode(true)
        if (player.deviceVolume != 2) {
            player.deviceVolume = 2
        }
        val audioAttributes = AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.CONTENT_TYPE_MUSIC).build()
        player.setAudioAttributes(audioAttributes,  /* handleAudioFocus= */true)
        player.playWhenReady = true
        audioClick = false
        PlayerINIT = true
        player.addListener(object : Player.EventListener {
            override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {
                Log.v("TAG", "Listener-onTracksChanged... MINI PLAYER")
                oldSongPos = 0
                val shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val gson = Gson()
                val json = shared.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toString())
                var mainPlayModelList1x = ArrayList<MainPlayModel>()
                if (!json.equals(gson.toString(), ignoreCase = true)) {
                    val type = object : TypeToken<ArrayList<MainPlayModel?>?>() {}.type
                    mainPlayModelList1x = gson.fromJson(json, type)
                }

                //                        myBitmap = getMediaBitmap(getActivity(), mainPlayModelList.get(player.getCurrentWindowIndex()).getImageFile());
                PlayerAudioId = mainPlayModelList1x[player.currentWindowIndex].id
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                //                        myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(player.getCurrentWindowIndex()).getImageFile());
                if (player.playbackState == ExoPlayer.STATE_BUFFERING) {
                } else if (isPlaying) {
                    localIntent!!.putExtra("MyData", "play")
                    localBroadcastManager!!.sendBroadcast(localIntent!!)
                } else if (!isPlaying) {
                    localIntent!!.putExtra("MyData", "pause")
                    localBroadcastManager!!.sendBroadcast(localIntent!!)
                }
            }

            override fun onPlayerError(error: ExoPlaybackException) {
                var intruptMethod: String? = ""
                val p = Properties()

                p.putValue("audioId", mainPlayModelList[position].id)
                p.putValue("audioName", mainPlayModelList[position].name)
                p.putValue("audioDescription", "")
                p.putValue("directions", mainPlayModelList[position].audioDirection)
                p.putValue("masterCategory", mainPlayModelList[position].audiomastercat)
                p.putValue("subCategory", mainPlayModelList[position].audioSubCategory)
                p.putValue("audioDuration", mainPlayModelList[position].audioDuration)
                p.putValue("position", GetCurrentAudioPosition())
                var AudioType = ""
                AudioType = if (downloadAudioDetailsList.contains(mainPlayModelList[position].name)) {
                    p.putValue("audioType", "Downloaded")
                    "Downloaded"
                } else {
                    p.putValue("audioType", "Streaming")
                    "Streaming"
                }
                p.putValue("source", GetSourceName(ctx))
                p.putValue("playerType", "Main")
                p.putValue("audioService", appStatus(ctx))
                p.putValue("bitRate", "")
                p.putValue("sound", hundredVolume.toString())
                when (error.type) {
                    ExoPlaybackException.TYPE_SOURCE -> {
                        p.putValue("interruptionMethod", error.message + " " + error.sourceException.message)
                        intruptMethod = error.message + " " + error.sourceException.message
                        Log.e("onPlaybackError", error.message + " " + error.sourceException.message)
                    }
                    ExoPlaybackException.TYPE_RENDERER -> {
                        p.putValue("interruptionMethod", error.message + " " + error.rendererException.message)
                        intruptMethod = error.message + " " + error.rendererException.message
                        Log.e("onPlaybackError", error.message + " " + error.rendererException.message)
                    }
                    ExoPlaybackException.TYPE_UNEXPECTED -> {
                        p.putValue("interruptionMethod", error.message + " " + error.unexpectedException.message)
                        intruptMethod = error.message + " " + error.unexpectedException.message
                        Log.e("onPlaybackError", error.message + " " + error.unexpectedException.message)
                    }
                    ExoPlaybackException.TYPE_REMOTE -> {
                        p.putValue("interruptionMethod", error.message)
                        intruptMethod = error.message
                        Log.e("onPlaybackError", error.message!!)
                    }
                    else -> {
                        p.putValue("interruptionMethod", error.message)
                        intruptMethod = error.message
                        Log.e("onPlaybackError", error.message!!)
                    }
                }
                AudioInterrupted = true
                addToSegment("Audio Interrupted", p, CONSTANTS.track)
                val cm = ctx.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
                //should check null because in airplane mode it will be null
                val nc: NetworkCapabilities?
                var downSpeed = 0f
                var batLevel = 0
                var upSpeed = 0f
                if (isNetworkConnected(ctx)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        nc = cm.getNetworkCapabilities(cm.activeNetwork)
                        downSpeed = nc!!.linkDownstreamBandwidthKbps.toFloat() / 1000
                        upSpeed = (nc.linkUpstreamBandwidthKbps / 1000).toFloat()
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val bm = ctx.getSystemService(BATTERY_SERVICE) as BatteryManager
                    batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                }
                try {
                    if (isNetworkConnected(ctx)) {
                        val listCall = APINewClient.client.getAudioInterruption(CoUserID, mainPlayModelList[position].id, mainPlayModelList[position].name, "", mainPlayModelList[position].audioDirection, mainPlayModelList[position].audiomastercat, mainPlayModelList[position].audioSubCategory, mainPlayModelList[position].audioDuration, "", AudioType, "Main", hundredVolume.toString(), appStatus(ctx), GetSourceName(ctx), GetCurrentAudioPosition(), "", intruptMethod, batLevel.toString(), BatteryStatus, downSpeed.toString(), upSpeed.toString(), "Android")
                        listCall.enqueue(object : Callback<AudioInterruptionModel?> {
                            override fun onResponse(call: Call<AudioInterruptionModel?>, response: Response<AudioInterruptionModel?>) {
                                val listModel = response.body()
                            }

                            override fun onFailure(call: Call<AudioInterruptionModel?>, t: Throwable) {}
                        })
                    } else {
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == ExoPlayer.STATE_READY) {
                    if (player.playWhenReady) {
                        localIntent!!.putExtra("MyData", "play")
                        localBroadcastManager!!.sendBroadcast(localIntent!!)
                    } else if (!player.playWhenReady) {
                        localIntent!!.putExtra("MyData", "pause")
                        localBroadcastManager!!.sendBroadcast(localIntent!!)
                    }

                    //                        isprogressbar = false;
                } else if (state == ExoPlayer.STATE_BUFFERING) {
                } else if (state == ExoPlayer.STATE_ENDED) {
                    try {
                        val shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                        val gson = Gson()
                        val json = shared.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toString())
                        var mainPlayModelList1x = ArrayList<MainPlayModel>()
                        if (!json.equals(gson.toString(), ignoreCase = true)) {
                            val type = object : TypeToken<ArrayList<MainPlayModel?>?>() {}.type
                            mainPlayModelList1x = gson.fromJson(json, type)
                        }
                        if (mainPlayModelList1x[player.currentWindowIndex].id.equals(mainPlayModelList1x[mainPlayModelList1x.size - 1].id, ignoreCase = true)) {
                            player.playWhenReady = false
                            localIntent!!.putExtra("MyData", "pause")
                            localBroadcastManager!!.sendBroadcast(localIntent!!)
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        Log.e("End State: ", e.message!!)
                    }
                }
            }
        })
        InitNotificationAudioPLayer(ctx, mainPlayModelList)
        if (!serviceConected) {
            try {
                playbackServiceIntent = Intent(ctx.applicationContext, GlobalInitExoPlayer::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(playbackServiceIntent)
                } else {
                    startService(playbackServiceIntent)
                }
                serviceConected = true
                //            bindService(playbackServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Log.e("Notification errrr: ", e.message!!)
            }
        }
    }

    fun GlobleInItDisclaimer(ctx: Context, mainPlayModelList: ArrayList<MainPlayModel>, pos: Int) {
        callNewPlayerRelease()
        player = SimpleExoPlayer.Builder(ctx.applicationContext).build()
        val mediaItem1: MediaItem = if (isNetworkConnected(ctx)) {
            MediaItem.fromUri(mainPlayModelList[pos].audioFile)
        } else {
            MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(R.raw.brain_wellness_spa_disclaimer))
        }
        player.setMediaItem(mediaItem1)
        InitNotificationAudioPLayerD(ctx)
        player.prepare()
        player.setWakeMode(C.WAKE_MODE_NONE)
        player.setHandleWakeLock(true)
        player.setForegroundMode(true)
        val audioAttributes = AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.CONTENT_TYPE_MUSIC).build()
        player.setAudioAttributes(audioAttributes, true)
        //        if (miniPlayer == 1) {
        player.playWhenReady = true
        if (player.deviceVolume != 2) {
            player.deviceVolume = 2
        }
        audioClick = false
        PlayerINIT = true
    }

    fun AddAudioToPlayer(size: Int, mainPlayModelList: ArrayList<MainPlayModel>, downloadAudioDetailsList: List<String?>, ctx: Context) {
        if (player != null) {
            for (i in size until mainPlayModelList.size) {
                if (downloadAudioDetailsList.isNotEmpty()) {
                    //                    for (int f = 0; f < downloadAudioDetailsList.size(); f++) {
                    if (downloadAudioDetailsList.contains(mainPlayModelList[i].name)) {
                        val extStore = FileUtils.readFile1(FileUtils.getFilePath(ctx, mainPlayModelList[i].name))
                        if (extStore.exists()) {
                            val mediaItem = MediaItem.fromUri(FileUtils.getFilePath(ctx, mainPlayModelList[i].name))
                            player.addMediaItem(i, mediaItem)
                        } else {
                            val mediaItem = MediaItem.fromUri(mainPlayModelList[i].audioFile)
                            player.addMediaItem(i, mediaItem)
                        }
                        player.prepare()
                        Log.e("Globle Player else part", mainPlayModelList[i].name)
                    } else  /* if (f == downloadAudioDetailsList.size() - 1)*/ {
                        val mediaItem = MediaItem.fromUri(mainPlayModelList[i].audioFile)
                        player.addMediaItem(i, mediaItem)
                        player.prepare()
                        //                        mediaSources[i] = new ExtractorMediaSource(Uri.parse(mainPlayModelList.get(i).getAudioFile()), dataSourceFactory, extractorsFactory, null, Throwable::printStackTrace);
                    }
                    //                    }
                } else {
                    //                mediaSources[i] = new ExtractorMediaSource(Uri.parse(mainPlayModelList.get(i).getAudioFile()), dataSourceFactory, extractorsFactory, null, Throwable::printStackTrace);
                    val mediaItem = MediaItem.fromUri(mainPlayModelList[i].audioFile)
                    player.addMediaItem(i, mediaItem)
                    player.prepare()
                }
            }
            InitNotificationAudioPLayer(ctx, mainPlayModelList)
            UpdateNotificationAudioPLayer(ctx)
        }
        //        playerNotificationManager.setPlayer(player);
    }

    fun InitNotificationAudioPLayer(ctx: Context, mainPlayModelList2: ArrayList<MainPlayModel>?) {
        var position = 0
        val sharedsa = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
        val gson = Gson()
        val json = sharedsa.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toString())
        if (!json.equals(gson.toString(), ignoreCase = true)) {
            val type = object : TypeToken<ArrayList<MainPlayModel?>?>() {}.type
            mainPlayModelList1 = gson.fromJson(json, type)
        }
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(ctx, "10001", (R.string.playback_channel_name), 0, notificationId, object : PlayerNotificationManager.MediaDescriptionAdapter {
            override fun getCurrentContentTitle(players: Player): String {
                val sharedsa = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val gson = Gson()
                val json = sharedsa.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toString())
                if (!json.equals(gson.toString(), ignoreCase = true)) {
                    val type = object : TypeToken<ArrayList<MainPlayModel?>?>() {}.type
                    mainPlayModelList1 = gson.fromJson(json, type)
                }
                var ps = 0
                ps = if (player != null) {
                    player.currentWindowIndex
                } else {
                    val shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                    shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                }
                return mainPlayModelList1[ps].name
            }

            override fun createCurrentContentIntent(player1: Player): PendingIntent? {
                intent = Intent(ctx, MyPlayerActivity::class.java)
                intent!!.putExtra("notification", "yes")
                intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                return PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }

            override fun getCurrentContentText(players: Player): String {
                val sharedsa = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val gson = Gson()
                val json = sharedsa.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toString())
                if (!json.equals(gson.toString(), ignoreCase = true)) {
                    val type = object : TypeToken<ArrayList<MainPlayModel?>?>() {}.type
                    mainPlayModelList1 = gson.fromJson(json, type)
                }
                var ps = 0
                ps = if (player != null) {
                    player.currentWindowIndex
                } else {
                    val shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                    shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                }
                return mainPlayModelList1[ps].audioDirection
            }

            override fun getCurrentLargeIcon(players: Player, callback: PlayerNotificationManager.BitmapCallback): Bitmap? {
                /*                       int ps = 0;
                        if (player != null) {
                            ps = player.getCurrentWindowIndex();
                        } else {
                            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                            ps = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                        }
                        myBitmap = getMediaBitmap(ctx, mainPlayModelList1.get(ps).getImageFile());*/
                return myBitmap
            }
        }, object : PlayerNotificationManager.NotificationListener {
            override fun onNotificationPosted(notificationId: Int, notification: Notification, ongoing: Boolean) {
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
                notification1 = notification
            }

            override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
                if (dismissedByUser) {
                    stopSelf()
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
        })
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
    }*/position = if (player != null) {
            player.currentWindowIndex
        } else {
            val shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
        }
        if (position == mainPlayModelList1.size - 1) {
            playerNotificationManager.setUseNextAction(true)
            playerNotificationManager.setUseNextActionInCompactView(true)
            //            showToast("Next available", activity);
        }
        //        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
        try {
            mediaSession = MediaSessionCompat(ctx, ctx.packageName)
            mediaSession.isActive = true
            playerNotificationManager.setMediaSessionToken(mediaSession.sessionToken)
            mediaSessionConnector = MediaSessionConnector(mediaSession)
            mediaSessionConnector.setPlayer(player)
            mediaSessionConnector.setMediaMetadataProvider {
                val duration: Long = if (player.duration < 0) player.currentPosition else player.duration
                val sharedsaxx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val jsonxx = sharedsaxx.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toString())
                if (!jsonxx.equals(gson.toString(), ignoreCase = true)) {
                    val type = object : TypeToken<ArrayList<MainPlayModel?>?>() {}.type
                    mainPlayModelList1 = gson.fromJson(jsonxx, type)
                }
                val builder = MediaMetadataCompat.Builder()
                var ps = 0
                ps = if (player != null) {
                    player.currentWindowIndex
                } else {
                    val shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                    shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder.putString(MediaMetadata.METADATA_KEY_ARTIST, mainPlayModelList1[ps].audioDirection)
                    builder.putString(MediaMetadata.METADATA_KEY_TITLE, mainPlayModelList1[ps].name)
                }
                builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, mainPlayModelList1[ps].imageFile)
                builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mainPlayModelList1[ps].id)
                try {
                    builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, myBitmap)
                } catch (e: OutOfMemoryError) {
                    e.printStackTrace()
                }
                if (duration > 0) {
                    builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                }
                builder.build()
            }
        } catch (e: java.lang.Exception) {
            UpdateNotificationAudioPLayer(ctx)
            e.printStackTrace()
        }
        //        }
        playerNotificationManager.setUseNextAction(false)
        playerNotificationManager.setUseNextActionInCompactView(false)
        playerNotificationManager.setUsePreviousAction(false)
        playerNotificationManager.setUsePreviousActionInCompactView(false)
        val controlDispatcher: ControlDispatcher = DefaultControlDispatcher(30000, 30000)
        playerNotificationManager.setControlDispatcher(controlDispatcher)
        playerNotificationManager.setSmallIcon(R.drawable.noti_app_logo_icon)
        playerNotificationManager.setColor(ContextCompat.getColor(ctx, R.color.blue))
        playerNotificationManager.setColorized(true)
        playerNotificationManager.setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
        playerNotificationManager.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        playerNotificationManager.setUseChronometer(true)
        playerNotificationManager.setPriority(NotificationCompat.PRIORITY_HIGH)
        playerNotificationManager.setUsePlayPauseActions(true)
        playerNotificationManager.setPlayer(player)
    }

    fun InitNotificationAudioPLayerD(ctx: Context) {
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(ctx, "10001", R.string.playback_channel_name, 0, notificationId, object : PlayerNotificationManager.MediaDescriptionAdapter {
            override fun getCurrentContentTitle(players: Player): String {
                return "Disclaimer"
            }

            override fun createCurrentContentIntent(player: Player): PendingIntent? {
                intent = Intent(ctx, MyPlayerActivity::class.java)
                intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                return PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }

            override fun getCurrentContentText(players: Player): String? {
                return "The audio shall start playing after the disclaimer"
            }

            override fun getCurrentLargeIcon(player: Player, callback: PlayerNotificationManager.BitmapCallback): Bitmap? {
                return BitmapFactory.decodeResource(ctx.resources, R.drawable.ic_music_icon)
            }
        }, object : PlayerNotificationManager.NotificationListener {
            override fun onNotificationPosted(notificationId: Int, notification: Notification, ongoing: Boolean) {
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
                notification1 = notification
            }

            override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
                if (dismissedByUser) {
                    stopSelf()
                    // Do what the app wants to do when dismissed by the user,
                    // like calling stopForeground(true); or stopSelf();
                }
            }
        })
        mediaSession = MediaSessionCompat(ctx, ctx.packageName)
        //        mediaSession.setCaptioningEnabled(true);
        mediaSession.isActive = true
        playerNotificationManager.setMediaSessionToken(mediaSession.sessionToken)
        //aa comment delete na krvi
        if (player != null) {
            mediaSessionConnector = MediaSessionConnector(mediaSession)
            mediaSessionConnector.setPlayer(player)
            mediaSessionConnector.setMediaMetadataProvider { player: Player ->
                val duration: Long = if (player.duration < 0) player.currentPosition else player.duration
                val builder = MediaMetadataCompat.Builder()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder.putString(MediaMetadata.METADATA_KEY_ARTIST, "The audio shall start playing after the disclaimer")
                    builder.putString(MediaMetadata.METADATA_KEY_TITLE, "Disclaimer")
                }
                builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, BitmapFactory.decodeResource(ctx.resources, R.drawable.ic_music_icon).toString())
                builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "0")

                //                if (duration > 0) {
                //                    builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION,  player.getDuration() == C.TIME_UNSET ? -1 : player.getDuration());
                //                    builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION,  -1);
                //                }
                try {
                    val icon: Bitmap = BitmapFactory.decodeResource(ctx.resources, R.drawable.ic_music_icon)
                    builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, icon)
                } catch (e: OutOfMemoryError) {
                    e.printStackTrace()
                }
                builder.build()
            }
        }
        playerNotificationManager.setUseNextAction(false)
        playerNotificationManager.setUsePreviousAction(false)
        playerNotificationManager.setUseNextActionInCompactView(false)
        playerNotificationManager.setUsePreviousActionInCompactView(false)
        val controlDispatcher: ControlDispatcher = DefaultControlDispatcher(0, 0)
        playerNotificationManager.setControlDispatcher(controlDispatcher)
        playerNotificationManager.setSmallIcon(R.drawable.noti_app_logo_icon)
        playerNotificationManager.setColor(ContextCompat.getColor(ctx, R.color.blue))
        playerNotificationManager.setColorized(true)
        playerNotificationManager.setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
        playerNotificationManager.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        playerNotificationManager.setUseChronometer(true)
        playerNotificationManager.setPriority(NotificationCompat.PRIORITY_HIGH)
        playerNotificationManager.setUsePlayPauseActions(true)
        playerNotificationManager.setPlayer(player)
    }

    fun UpdateMiniPlayer(ctx: Context, activity: Activity): String {
        var AudioFlag = "0"
        val shared1x = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        val expDate = shared1x.getString(CONSTANTS.PREF_KEY_ExpDate, "")
        //            expDate = "2020-09-29 06:34:10";
        Log.e("Exp Date !!!!", expDate!!)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var Expdate = Date()
        try {
            Expdate = format.parse(expDate)
            Log.e("Exp Date Expdate!!!!", Expdate.toString())
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val simpleDateFormat1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        simpleDateFormat1.timeZone = TimeZone.getTimeZone("UTC")
        val currdate = Calendar.getInstance().time
        var currdate1 = Date()
        val currantDateTime = simpleDateFormat1.format(currdate)
        try {
            currdate1 = format.parse(currantDateTime)
            Log.e("currant currdate !!!!", currdate1.toString())
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        Log.e("currant Date !!!!", currantDateTime)
        when {
            Expdate.before(currdate1) -> {
                Log.e("app", "Date1 is before Date2")
                IsLock = "1"
            }
            Expdate.after(currdate1) -> {
                Log.e("app", "Date1 is after Date2")
                IsLock = "0"
            }
            Expdate === currdate1 -> {
                Log.e("app", "Date1 is equal Date2")
                IsLock = "1"
            }
        }
        try {
            val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            AudioFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")!!
            val shared2 = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
            val UnlockAudioLists = shared2.getString(CONSTANTS.PREF_KEY_UnLockAudiList, "")
            val gson1 = Gson()
            val type1 = object : TypeToken<List<String?>?>() {}.type
            val UnlockAudioList = gson1.fromJson<List<String?>>(UnlockAudioLists, type1)
            if (!IsLock.equals("0", ignoreCase = true) && (AudioFlag.equals("MainAudioList", ignoreCase = true) || AudioFlag.equals("ViewAllAudioList", ignoreCase = true) || AudioFlag.equals("SearchAudio", ignoreCase = true) || AudioFlag.equals("SearchModelAudio", ignoreCase = true) || AudioFlag.equals("AppointmentDetailList", ignoreCase = true))) {
                val shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val gson = Gson()
                val arrayList1 = ArrayList<MainPlayModel>()
                val json = shared.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toString())
                val sharedd = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val editor = sharedd.edit()
                if (AudioFlag.equals("MainAudioList", ignoreCase = true)) {
                    val type = object : TypeToken<ArrayList<MainAudioModel.ResponseData.Detail?>?>() {}.type
                    val arrayList = gson.fromJson<ArrayList<MainAudioModel.ResponseData.Detail>>(json, type)
                    val arrayList2 = ArrayList<MainAudioModel.ResponseData.Detail>()
                    val size = arrayList.size
                    for (i in 0 until size) {
                        if (UnlockAudioList.contains(arrayList[i].iD)) {
                            arrayList2.add(arrayList[i])
                        }
                    }
                    if (arrayList2.size != 0) {
                        for (i in arrayList2.indices) {
                            val mainPlayModel = MainPlayModel()
                            mainPlayModel.id = arrayList[i].iD.toString()
                            mainPlayModel.name = arrayList[i].name.toString()
                            mainPlayModel.audioFile = arrayList[i].audioFile.toString()
                            mainPlayModel.playlistID = ""
                            mainPlayModel.audioDirection = arrayList[i].audioDirection.toString()
                            mainPlayModel.audiomastercat = arrayList[i].audiomastercat.toString()
                            mainPlayModel.audioSubCategory = arrayList[i].audioSubCategory.toString()
                            mainPlayModel.imageFile = arrayList[i].imageFile.toString()
                            mainPlayModel.audioDuration = arrayList[i].audioDuration.toString()
                            arrayList1.add(mainPlayModel)
                        }
                    }
                    if (arrayList2.size < arrayList.size) {
                        audioClick = if (player != null) {
                            callNewPlayerRelease()
                            true
                        } else {
                            true
                        }
                        val jsonx = gson.toJson(arrayList1)
                        val json11 = gson.toJson(arrayList2)
                        editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonx)
                        editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json11)
                        editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "")
                        editor.putString(CONSTANTS.PREF_KEY_PlayFrom, shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, ""))
                        editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, AudioFlag)
                        editor.apply()
                        editor.commit()
                    }
                } else if (AudioFlag.equals("ViewAllAudioList", ignoreCase = true)) {
                    val type = object : TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail?>?>() {}.type
                    val arrayList = gson.fromJson<ArrayList<ViewAllAudioListModel.ResponseData.Detail>>(json, type)
                    val arrayList2 = ArrayList<ViewAllAudioListModel.ResponseData.Detail>()
                    val size = arrayList.size
                    for (i in 0 until size) {
                        if (UnlockAudioList.contains(arrayList[i].iD)) {
                            arrayList2.add(arrayList[i])
                        }
                    }
                    if (arrayList2.size != 0) {
                        for (i in arrayList2.indices) {
                            val mainPlayModel = MainPlayModel()
                            mainPlayModel.id = arrayList[i].iD.toString()
                            mainPlayModel.name = arrayList[i].name.toString()
                            mainPlayModel.audioFile = arrayList[i].audioFile.toString()
                            mainPlayModel.playlistID = ""
                            mainPlayModel.audioDirection = arrayList[i].audioDirection.toString()
                            mainPlayModel.audiomastercat = arrayList[i].audiomastercat.toString()
                            mainPlayModel.audioSubCategory = arrayList[i].audioSubCategory.toString()
                            mainPlayModel.imageFile = arrayList[i].imageFile.toString()
                            mainPlayModel.audioDuration = arrayList[i].audioDuration.toString()
                            arrayList1.add(mainPlayModel)
                        }
                    }
                    if (arrayList2.size < arrayList.size) {
                        audioClick = if (player != null) {
                            callNewPlayerRelease()
                            true
                        } else {
                            true
                        }
                        val jsonx = gson.toJson(arrayList1)
                        val json11 = gson.toJson(arrayList2)
                        editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonx)
                        editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json11)
                        editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "")
                        editor.putString(CONSTANTS.PREF_KEY_PlayFrom, shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, ""))
                        editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, AudioFlag)
                        editor.apply()
                        editor.commit()
                    }
                } else if (AudioFlag.equals("SearchModelAudio", ignoreCase = true)) {
                    val type = object : TypeToken<ArrayList<SearchBothModel.ResponseData?>?>() {}.type
                    val arrayList = gson.fromJson<ArrayList<SearchBothModel.ResponseData>>(json, type)
                    val arrayList2 = ArrayList<SearchBothModel.ResponseData>()
                    val size = arrayList.size
                    for (i in 0 until size) {
                        if (UnlockAudioList.contains(arrayList[i].iD)) {
                            arrayList2.add(arrayList[i])
                        }
                    }
                    if (arrayList2.size != 0) {
                        for (i in arrayList2.indices) {
                            val mainPlayModel = MainPlayModel()
                            mainPlayModel.id = arrayList[i].iD!!
                            mainPlayModel.name = arrayList[i].name!!
                            mainPlayModel.audioFile = arrayList[i].audioFile!!
                            mainPlayModel.playlistID = ""
                            mainPlayModel.audioDirection = arrayList[i].audioDirection!!
                            mainPlayModel.audiomastercat = arrayList[i].audiomastercat!!
                            mainPlayModel.audioSubCategory = arrayList[i].audioSubCategory!!
                            mainPlayModel.imageFile = arrayList[i].imageFile!!
                            mainPlayModel.audioDuration = arrayList[i].audioDuration!!
                            arrayList1.add(mainPlayModel)
                        }
                    }
                    if (arrayList2.size < arrayList.size) {
                        audioClick = if (player != null) {
                            callNewPlayerRelease()
                            true
                        } else {
                            true
                        }
                        val jsonx = gson.toJson(arrayList1)
                        val json11 = gson.toJson(arrayList2)
                        editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonx)
                        editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json11)
                        editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "")
                        editor.putString(CONSTANTS.PREF_KEY_PlayFrom, shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, ""))
                        editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, AudioFlag)
                        editor.apply()
                        editor.commit()
                    }
                } else if (AudioFlag.equals("SearchAudio", ignoreCase = true)) {
                    val type = object : TypeToken<ArrayList<SuggestedModel.ResponseData?>?>() {}.type
                    val arrayList = gson.fromJson<ArrayList<SuggestedModel.ResponseData>>(json, type)
                    val arrayList2 = ArrayList<SuggestedModel.ResponseData>()
                    val size = arrayList.size
                    for (i in 0 until size) {
                        if (UnlockAudioList.contains(arrayList[i].iD)) {
                            arrayList2.add(arrayList[i])
                        }
                    }
                    if (arrayList2.size != 0) {
                        for (i in arrayList2.indices) {
                            val mainPlayModel = MainPlayModel()
                            mainPlayModel.id = arrayList[i].iD!!
                            mainPlayModel.name = arrayList[i].name!!
                            mainPlayModel.audioFile = arrayList[i].audioFile!!
                            mainPlayModel.playlistID = ""
                            mainPlayModel.audioDirection = arrayList[i].audioDirection!!
                            mainPlayModel.audiomastercat = arrayList[i].audiomastercat!!
                            mainPlayModel.audioSubCategory = arrayList[i].audioSubCategory!!
                            mainPlayModel.imageFile = arrayList[i].imageFile!!
                            mainPlayModel.audioDuration = arrayList[i].audioDuration!!
                            arrayList1.add(mainPlayModel)
                        }
                    }
                    if (arrayList2.size < arrayList.size) {
                        audioClick = if (player != null) {
                            callNewPlayerRelease()
                            true
                        } else {
                            true
                        }
                        val jsonx = gson.toJson(arrayList1)
                        val json11 = gson.toJson(arrayList2)
                        editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonx)
                        editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json11)
                        editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "")
                        editor.putString(CONSTANTS.PREF_KEY_PlayFrom, shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, ""))
                        editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, AudioFlag)
                        editor.apply()
                        editor.commit()
                    }
                } else if (AudioFlag.equals("AppointmentDetailList", ignoreCase = true)) {
                    val type = object : TypeToken<ArrayList<AppointmentDetailModel.Audio?>?>() {}.type
                    val arrayList = gson.fromJson<ArrayList<AppointmentDetailModel.Audio>>(json, type)
                    val arrayList2 = ArrayList<AppointmentDetailModel.Audio>()
                    val size = arrayList.size
                    for (i in 0 until size) {
                        if (UnlockAudioList.contains(arrayList[i].iD)) {
                            arrayList2.add(arrayList[i])
                        }
                    }
                    if (arrayList2.size != 0) {
                        for (i in arrayList2.indices) {
                            val mainPlayModel = MainPlayModel()
                            mainPlayModel.id = arrayList[i].iD.toString()
                            mainPlayModel.name = arrayList[i].name.toString()
                            mainPlayModel.audioFile = arrayList[i].audioFile.toString()
                            mainPlayModel.playlistID = ""
                            mainPlayModel.audioDirection = arrayList[i].audioDirection.toString()
                            mainPlayModel.audiomastercat = arrayList[i].audiomastercat.toString()
                            mainPlayModel.audioSubCategory = arrayList[i].audioSubCategory.toString()
                            mainPlayModel.imageFile = arrayList[i].imageFile.toString()
                            mainPlayModel.audioDuration = arrayList[i].audioDuration.toString()
                            arrayList1.add(mainPlayModel)
                        }
                    }
                    if (arrayList2.size < arrayList.size) {
                        audioClick = if (player != null) {
                            callNewPlayerRelease()
                            true
                        } else {
                            true
                        }
                        val jsonx = gson.toJson(arrayList1)
                        val json11 = gson.toJson(arrayList2)
                        editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonx)
                        editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json11)
                        editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "")
                        editor.putString(CONSTANTS.PREF_KEY_PlayFrom, shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, ""))
                        editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, AudioFlag)
                        editor.apply()
                        editor.commit()
                    }
                }
                if (arrayList1.size == 0) {
                    removeSharepref(ctx)
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        if (!DownloadMedia.isDownloading) {
            getPending(ctx, activity)
        }
        return AudioFlag
    }

    private fun getPending(ctx: Context, activity: Activity) {
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        val userId = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        val coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        DB = getAudioDataBase(ctx)
        DB.taskDao()?.getNotDownloadData("Complete", coUserId)?.observe((ctx as LifecycleOwner), { audioList: List<DownloadAudioDetails> ->
            notDownloadedData = ArrayList()
            DB.taskDao()?.getNotDownloadData("Complete", coUserId)?.removeObserver { audioListx: List<DownloadAudioDetails?>? -> } as (List<DownloadAudioDetails?>) -> Unit
            (notDownloadedData as ArrayList<DownloadAudioDetails>).addAll(audioList)
            if (notDownloadedData.isNotEmpty() && !DownloadMedia.isDownloading) {
                if (isNetworkConnected(ctx)) {
                    val sharedx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE)
                    val gson = Gson()
                    val json = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, gson.toString())
                    val json1 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadUrl, gson.toString())
                    val json2 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, gson.toString())
                    if (!json1.equals(gson.toString(), ignoreCase = true)) {
                        val type = object : TypeToken<List<String?>?>() {}.type
                        fileNameList = gson.fromJson(json, type)
                        audioFile = gson.fromJson(json1, type)
                        playlistDownloadId = gson.fromJson(json2, type)
                        if (fileNameList.size == 0) {
                            for (i in notDownloadedData.indices) {
                                audioFile.add(notDownloadedData[i].AudioFile!!)
                                fileNameList.add(notDownloadedData[i].Name!!)
                                playlistDownloadId.add(notDownloadedData[i].PlaylistId!!)
                            }
                        }
                    }
                    val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE)
                    val editor = shared1.edit()
                    val nameJson = gson.toJson(fileNameList)
                    val urlJson = gson.toJson(audioFile)
                    val playlistIdJson = gson.toJson(playlistDownloadId)
                    editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson)
                    editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson)
                    editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson)
                    editor.apply()
                    if (fileNameList.size != 0) {
                        DownloadMedia.isDownloading = true
                        val downloadMedia = DownloadMedia(ctx.applicationContext, activity)
                        downloadMedia.encrypt1(audioFile, fileNameList, playlistDownloadId)
                    }
                }
            }
        } as (List<DownloadAudioDetails?>) -> Unit)
    }

    private fun removeSharepref(ctx: Context) {
        val sharedm = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
        val editorr = sharedm.edit()
        editorr.remove(CONSTANTS.PREF_KEY_PlayerAudioList)
        editorr.remove(CONSTANTS.PREF_KEY_MainAudioList)
        editorr.remove(CONSTANTS.PREF_KEY_PlayerPosition)
        editorr.remove(CONSTANTS.PREF_KEY_AudioPlayerFlag)
        editorr.remove(CONSTANTS.PREF_KEY_PlayerPlaylistId)
        editorr.remove(CONSTANTS.PREF_KEY_PlayerPlaylistName)
        editorr.remove(CONSTANTS.PREF_KEY_PlayFrom)
        editorr.clear()
        editorr.apply()
        callNewPlayerRelease()
    }

    fun UpdateNotificationAudioPLayer(ctx: Context) {
        val sharedsa = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
        val gson = Gson()
        val json = sharedsa.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toString())
        if (!json.equals(gson.toString(), ignoreCase = true)) {
            val type = object : TypeToken<ArrayList<MainPlayModel?>?>() {}.type
            mainPlayModelList1 = gson.fromJson(json, type)
        }
        try {
            mediaSession = MediaSessionCompat(ctx, ctx.packageName)
            mediaSession.isActive = true
            playerNotificationManager.setMediaSessionToken(mediaSession.sessionToken)
            if (player != null) {
                //            mediaSession.setPlaybackState(
                //                new PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_PLAYING,
                //                        player.getCurrentPosition(),1 )
                //                        .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                //                        .build());
                mediaSessionConnector = MediaSessionConnector(mediaSession)
                mediaSessionConnector.setPlayer(player)
                mediaSessionConnector.setMediaMetadataProvider {
                    val duration: Long
                    duration = if (player.duration < 0) player.currentPosition else player.duration
                    val builder = MediaMetadataCompat.Builder()
                    val sharedsa1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                    val json1 = sharedsa1.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toString())
                    if (!json1.equals(gson.toString(), ignoreCase = true)) {
                        val type = object : TypeToken<ArrayList<MainPlayModel?>?>() {}.type
                        mainPlayModelList1 = gson.fromJson(json1, type)
                    }
                    var ps = 0
                    ps = if (player != null) {
                        player.currentWindowIndex
                    } else {
                        val shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                        shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder.putString(MediaMetadata.METADATA_KEY_ARTIST, mainPlayModelList1[ps].audioDirection)
                        builder.putString(MediaMetadata.METADATA_KEY_TITLE, mainPlayModelList1[ps].name)
                    }
                    builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, mainPlayModelList1[ps].imageFile)
                    builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mainPlayModelList1[ps].id)
                    if (duration > 0) {
                        builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                    }
                    try {
                        builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, myBitmap)
                    } catch (e: OutOfMemoryError) {
                        e.printStackTrace()
                    }
                    builder.build()
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            //            UpdateNotificationAudioPLayer(ctx);
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(startId, notification1!!, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForeground(startId, notification1)
            }
            serviceConected = true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.e("Start Command: ", e.message!!)
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        LocalBinder()
        return null
    }

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        val service: GlobalInitExoPlayer
            get() = // Return this instance of LocalService so clients can call public methods
                this@GlobalInitExoPlayer
    }

    /* @Override
    public void onDestroy() {
        Log.e("APPLICATION", "App is in onActivityDestroyed");
        showToast("onDestroy Called", activity);
        relesePlayer(getApplication());
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
        stopForeground(true);
        super.onDestroy();
    }*/

    /* @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("Appplication log", "onTaskRemoved Called");
//        showToast("onTaskRemoved Called", activity);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
        relesePlayer(getApplicationContext());
        stopForeground(true);
//        stopSelf();
//        stopForeground(true);
//        playerNotificationManager.cancel(notificationId);
        super.onTaskRemoved(rootIntent);
    }*/

    /* @Override
    public void onDestroy() {
        Log.e("APPLICATION", "App is in onActivityDestroyed");
        showToast("onDestroy Called", activity);
        relesePlayer(getApplication());
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
        stopForeground(true);
        super.onDestroy();
    }*/

    /* @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("Appplication log", "onTaskRemoved Called");
//        showToast("onTaskRemoved Called", activity);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
        relesePlayer(getApplicationContext());
        stopForeground(true);
//        stopSelf();
//        stopForeground(true);
//        playerNotificationManager.cancel(notificationId);
        super.onTaskRemoved(rootIntent);
    }*/

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
}