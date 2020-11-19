package com.brainwellnessspa.Utility;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.session.MediaButtonReceiver;

import com.brainwellnessspa.DashboardModule.Activities.PlayWellnessActivity;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.Services.NotificationActionService;
import com.google.gson.Gson;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Models.AddToQueueModel;
import com.brainwellnessspa.R;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static com.brainwellnessspa.BWSApplication.ACTION_PREVIUOS;
import static com.brainwellnessspa.BWSApplication.CHANNEL_ID;

public class MusicService extends Service {
    public static MediaPlayer mediaPlayer;
    public static boolean isPrepare = false, songComplete = false, isMediaStart = false,isrelese=false, isStop = false,isCompleteStop = false,isPreparing=false;
    public static boolean isPause = false,isprogressbar = false;
    public static boolean isResume = false;
    public static int oTime = 0, startTime = 0, endTime = 0, forwardTime = 30000, backwardTime = 30000;
    static public Handler handler;
    static boolean isPlaying = false;
    public static final String ACTION_PLAY = "com.valdioveliu.valdio.audioplayer.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.valdioveliu.valdio.audioplayer.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "com.valdioveliu.valdio.audioplayer.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.valdioveliu.valdio.audioplayer.ACTION_NEXT";
    public static final String ACTION_STOP = "com.valdioveliu.valdio.audioplayer.ACTION_STOP";
    public static final String MEDIA_CHANNEL_ID = "media_playback_channel";
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.brainwellnessspa.PlayNewAudio";
    //MediaSession
    private static Bitmap myBitmap;
    public static MainPlayModel mainPlayModel;
    public static MediaSessionManager mediaSessionManager;
    public static MediaSessionCompat mediaSession;
    public static MediaControllerCompat.TransportControls transportControls;

    //AudioPlayer notification ID
    public static final int NOTIFICATION_ID = 101;

    //Used to pause/resume MediaPlayer
    private int resumePosition;

    //AudioFocus
    private AudioManager audioManager;

    // Binder given to clients
    private final IBinder iBinder = new MusicService.LocalBinder();

    //List of available Audio files
    private int audioIndex = -1;


    //Handle incoming phone calls
    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;


    /**
     * Service lifecycle methods
     */


    @Override
    public void onCreate() {
        super.onCreate();
        // Perform one-time setup procedures

        // Manage incoming phone calls during playback.
        // Pause MediaPlayer on incoming call,
        // Resume on hangup.
        callStateListener();
        //ACTION_AUDIO_BECOMING_NOISY -- change in audio outputs -- BroadcastReceiver
        registerBecomingNoisyReceiver();
        //Listen for new Audio to play -- BroadcastReceiver
        register_playNewAudio();
    }

    //The system calls this method when an activity, requests the service be started
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       /* try {
            // You only need to create the channel on API 26+ devices
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createChannel();
            }

        } catch (NullPointerException e) {
            stopSelf();
        }

        //Request audio focus


        if (mediaSessionManager == null) {
            try {
                initMediaSession();
                initMediaPlayer();
            } catch (RemoteException e) {
                e.printStackTrace();
                stopSelf();
            }
            buildNotification(PlaybackStatus.PLAYING);
        }*/

        //Handle Intent action from MediaSession.TransportControls
        handleIncomingActions(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaSession.release();
        removeNotification();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }
//        removeAudioFocus();
        //Disable the PhoneStateListener
        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        removeNotification();

        //unregister BroadcastReceivers
        unregisterReceiver(becomingNoisyReceiver);
        unregisterReceiver(playNewAudio);

        //clear cached playlist
//        new StorageUtil(getApplicationContext()).clearCachedAudioPlaylist();
    }

    /**
     * Service Binder
     */
    public class LocalBinder extends Binder {
        public MusicService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MusicService.this;
        }
    }


    /**
     * MediaPlayer callback methods
     */
   /* @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //Invoked indicating buffering status of
        //a media resource being streamed over the network.
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //Invoked when playback of a media source has completed.
        stopMedia();

        removeNotification();
        //stop the service
        stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //Invoked when there has been an error during an asynchronous operation
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
        }
        return false;
    }*/
/*
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        //Invoked to communicate some info
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //Invoked when the media source is ready for playback.
        playMedia();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        //Invoked indicating the completion of a seek operation.
    }

    @Override
    public void onAudioFocusChange(int focusState) {

        //Invoked when the audio focus of the system is updated.
        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mediaPlayer == null) initMediaPlayer();
                else if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                mediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }*/


    /**
     * AudioFocus
     */
 /*   private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }*/


    private void skipToNext() {

//        if (audioIndex == audioList.size() - 1) {
//            //if last in playlist
//            audioIndex = 0;
//            activeAudio = audioList.get(audioIndex);
//        } else {
//            //get next in playlist
//            activeAudio = audioList.get(++audioIndex);
//        }

        //Update stored index
//        new StorageUtil(getApplicationContext()).storeAudioIndex(audioIndex);

        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
    }

    private void skipToPrevious() {

//        if (audioIndex == 0) {
//            //if first in playlist
//            //set index to the last of audioList
//            audioIndex = audioList.size() - 1;
//            activeAudio = audioList.get(audioIndex);
//        } else {
//            //get previous in playlist
//            activeAudio = audioList.get(--audioIndex);
//        }

        //Update stored index
//        new StorageUtil(getApplicationContext()).storeAudioIndex(audioIndex);

        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
    }


    /**
     * ACTION_AUDIO_BECOMING_NOISY -- change in audio outputs
     */
    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //pause audio on ACTION_AUDIO_BECOMING_NOISY
            pauseMedia();
            buildNotification(PlaybackStatus.PAUSED,context,mainPlayModel);
        }
    };

    private void registerBecomingNoisyReceiver() {
        //register after getting audio focus
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }

    /**
     * Handle PhoneState changes
     */
    private void callStateListener() {
        // Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Starting listening for PhoneState changes
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            pauseMedia();
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (mediaPlayer != null) {
                            if (ongoingCall) {
                                ongoingCall = false;
                                resumeMedia();
                            }
                        }
                        break;
                }
            }
        };
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }

    /**
     * MediaSession and Notification actions
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initMediaSession() throws RemoteException {
        if (mediaSessionManager != null) return; //mediaSessionManager exists

        mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        // Create a new MediaSession
        mediaSession = new MediaSessionCompat(getApplicationContext(), "AudioPlayer");
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
     /*   mediaSession.setCallback(new MediaSessionCompat.Callback() {
            // Implement callbacks
            @Override
            public void onPlay() {
                super.onPlay();

                resumeMedia();
                buildNotification(PlaybackStatus.PLAYING,context);
            }

            @Override
            public void onPause() {
                super.onPause();

                pauseMedia();
                buildNotification(PlaybackStatus.PAUSED,context);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();

                skipToNext();
//                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING,context);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();

                skipToPrevious();
//                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING,context);
            }

            @Override
            public void onStop() {
                super.onStop();
                removeNotification();
                //Stop the service
                stopSelf();
            }

            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
            }
        });*/
    }

//    private void updateMetaData() {
//        Bitmap albumArt = BitmapFactory.decodeResource(getResources(),
//                R.drawable.logo_design); //replace with medias albumArt
//        // Update the current metadata
//        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
//                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
//                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, activeAudio.getArtist())
//                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, activeAudio.getAlbum())
//                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, activeAudio.getTitle())
//                .build());
//    }


        public static void buildNotification(PlaybackStatus playbackStatus,Context context,MainPlayModel track) {

        /**
         * Notification actions -> playbackAction()
         *  0 -> Play
         *  1 -> Pause
         *  2 -> Next track
         *  3 -> Previous track
         */
        try {
            getMediaBitmep(track,context,playbackStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }
       }
    public static void getMediaBitmep(MainPlayModel track, Context context, PlaybackStatus playbackStatus) {
        class GetMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    if(track.getAudioFile().equalsIgnoreCase("")){
                        myBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.disclaimer);
                    }else {
                        URL url = new URL(track.getImageFile());
                        myBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {  super.onPostExecute(aVoid);
                int notificationAction = 0;//needs to be initialized
                PendingIntent play_pauseAction = null;

                //Build a new notification according to the current state of the MediaPlayer
                if (playbackStatus == PlaybackStatus.PLAYING) {
                    notificationAction = R.drawable.ic_pause_black_24dp;
                    //create the pause action
                    play_pauseAction = playbackAction(1,context);
                } else if (playbackStatus == PlaybackStatus.PAUSED) {
                    notificationAction = R.drawable.ic_play_arrow_black_24dp;
                    //create the play action
                    play_pauseAction = playbackAction(0,context);
                }
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
//        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");
        PendingIntent pendingIntentPrevious;
                Intent intent = new Intent(context, PlayWellnessActivity.class);
                intent.putExtra("com.brainwellnessspa.notifyId", NOTIFICATION_ID);
                PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        int drw_previous;
        Intent intentPrevious = new Intent(context, NotificationActionService.class).setAction(ACTION_PREVIUOS);
        pendingIntentPrevious = PendingIntent.getBroadcast(context, 0, intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);
        drw_previous = R.drawable.ic_skip_previous_black_24dp;

        Intent intentPlay = new Intent(context, NotificationActionService.class).setAction(ACTION_PLAY);
        PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntentNext;
        int drw_next;
        Intent intentNext = new Intent(context, NotificationActionService.class).setAction(ACTION_NEXT);
        pendingIntentNext = PendingIntent.getBroadcast(context, 0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
        drw_next = R.drawable.ic_skip_next_black_24dp;
                //create notification
                Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_music_note)
                        .setContentTitle(track.getName())
                        .setContentText(track.getAudioDirection())
                        .setLargeIcon(myBitmap)
                        .setOnlyAlertOnce(true)//show notification for only first time
                        .setShowWhen(false)
                        .setOngoing(true)
                        .setContentIntent(pIntent)
                        .addAction(drw_previous, "Previous", playbackAction(3,context))
                        .addAction(notificationAction, "Play", play_pauseAction)
                        .addAction(drw_next, "Next", playbackAction(2,context))
                        /*.addAction(android.R.drawable.ic_media_previous, "previous", playbackAction(3,context))
                        .addAction(notificationAction, "pause", play_pauseAction)
                        .addAction(android.R.drawable.ic_media_next, "next", playbackAction(2,context))*/
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                .setMediaSession(mediaSession.getSessionToken())
                                .setShowActionsInCompactView(0, 1, 2))
                        .setDeleteIntent(
                                MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                        .build();
//.setMediaSession(mediaSessionCompat.getSessionToken())
//                notificationManagerCompat.notify(1, notification);
                ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notification);
/*
                // Create a new Notification
                NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context, MEDIA_CHANNEL_ID)
                        // Hide the timestamp
                        .setShowWhen(false)
                        // Set the Notification style
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                // Attach our MediaSession token
                                .setMediaSession(mediaSession.getSessionToken())
                                // Show our playback controls in the compat view
                                .setShowActionsInCompactView(0, 1, 2))
                        // Set the Notification color
                        .setColor(context.getResources().getColor(R.color.colorAccent))
                        // Set the large and small icons
                        .setLargeIcon(myBitmap)
                        .setSmallIcon(android.R.drawable.stat_sys_headset)
                        // Set Notification content information
//                .setContentText(activeAudio.getArtist())
//                .setContentTitle(activeAudio.getAlbum())
//                .setContentInfo(activeAudio.getTitle())
                        // Add playback actions
                        .addAction(android.R.drawable.ic_media_previous, "previous", playbackAction(3,context))
                        .addAction(notificationAction, "pause", play_pauseAction)
                        .addAction(android.R.drawable.ic_media_next, "next", playbackAction(2,context));*/
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                                "KOD Dev", NotificationManager.IMPORTANCE_LOW);

                        NotificationManager  notificationManager = context.getSystemService(NotificationManager.class);
                        if (notificationManager != null) {
                            notificationManager.createNotificationChannel(channel);
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            NotificationManager  notificationManager = context.getSystemService(NotificationManager.class);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        GetMedia st = new GetMedia();
        st.execute();
    }

    public static PendingIntent playbackAction(int actionNumber,Context context) {
        Intent playbackAction = new Intent(context, MusicService.class);
        switch (actionNumber) {
            case 0:
                // Play
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(context, actionNumber, playbackAction, 0);
            case 1:
                // Pause
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(context, actionNumber, playbackAction, 0);
            case 2:
                // Next track
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(context, actionNumber, playbackAction, 0);
            case 3:
                // Previous track
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(context, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }

    private void removeNotification() {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(NOTIFICATION_ID);
    }

    public static void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;

        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
        } else if (actionString.equalsIgnoreCase(ACTION_NEXT)) {
            transportControls.skipToNext();
        } else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) {
            transportControls.skipToPrevious();
        } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();
        }
    }


    /**
     * Play new Audio
     */
    private BroadcastReceiver playNewAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

//            //Get the new media index form SharedPreferences
//            audioIndex = new StorageUtil(getApplicationContext()).loadAudioIndex();
//            if (audioIndex != -1 && audioIndex < audioList.size()) {
//                //index is in a valid range
//                activeAudio = audioList.get(audioIndex);
//            } else {
//                stopSelf();
//            }

            //A PLAY_NEW_AUDIO action received
            //reset mediaPlayer to play the new Audio
//            stopMedia();
//            mediaPlayer.reset();
//            initMediaPlayer();
//            updateMetaData();
            buildNotification(PlaybackStatus.PLAYING,context,mainPlayModel);
        }
    };

    private void register_playNewAudio() {
        //Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(Broadcast_PLAY_NEW_AUDIO);
        registerReceiver(playNewAudio, filter);
    }

    private void createChannel() {
        // The id of the channel.
        String id = MEDIA_CHANNEL_ID;
        // The user-visible name of the channel.
        CharSequence name = "Media playback";
        // The user-visible description of the channel.
        String description = "Media playback controls";
        int importance;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_LOW;
        } else {
            importance = 0;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.setShowBadge(false);
            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(
                    mChannel
            );

        }
    }

    public static void initMediaPlayer() {
        if (null == mediaPlayer) {
            mediaPlayer = new MediaPlayer();
            Log.e("Playinggggg", "Playinggggg");
        }
    }
    public static void play(Uri AudioFile) {
        initMediaPlayer();
        stopMedia();
        playAudio(AudioFile);
    }
    public static void playAudio(Uri AudioFile) {
//        if (!isPLAYING) {
//            isPLAYING = true;
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(String.valueOf(AudioFile));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaPlayer.setAudioAttributes(
                        new AudioAttributes
                                .Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build());
            }
            mediaPlayer.prepareAsync();
            isPrepare = true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Failedddddddd", "prepare() failed");
        }
//        }else {
//            isPLAYING = false;
//            stopPlaying();
//        }
    }
    public static void PhoneCall() {
/*
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    //INCOMING call
                    //do all necessary action to pause the audio
                    if (mediaPlayer != null) {//check mp
                        setPlayerButton(true, false, true);

                        if (mediaPlayer.isPlaying()) {

                            mediaPlayer.pause();
                        }
                    }

                } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                    //Not IN CALL
                    //do anything if the phone-state is idle
                } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    //A call is dialing, active or on hold
                    //do all necessary action to pause the audio
                    //do something here
                    if (mediaPlayer != null) {//check mp
                        setPlayerButton(true, false, true);

                        if (mediaPlayer.isPlaying()) {

                            mediaPlayer.pause();
                        }
                    }
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
*/
    }

    public static int getEndTime() {
        endTime = mediaPlayer.getDuration();
        return endTime;
    }

    public static int getStartTime() {
        try {
            if (!isMediaStart) {
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    startTime = 0;
                } else {
                    startTime = 0;
                }
            } else if (isPause) {
                startTime = mediaPlayer.getCurrentPosition();
            } else {
                startTime = mediaPlayer.getCurrentPosition();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return startTime;
    }

    public static void ToRepeat(boolean Status) {
        mediaPlayer.setLooping(Status);
    }

    public static void ToForward(Context context) {
        endTime = getEndTime();
        startTime = mediaPlayer.getCurrentPosition();
        if ((startTime + forwardTime) <= endTime) {
            startTime = startTime + forwardTime;
            mediaPlayer.seekTo(startTime);
        } else {
            BWSApplication.showToast("Please wait", context);
        }
    }

    public static void ToBackward(Context context) {
        startTime = mediaPlayer.getCurrentPosition();
        if ((startTime - backwardTime) > 0) {
            startTime = startTime - backwardTime;
            mediaPlayer.seekTo(startTime);
        } else {
            BWSApplication.showToast("Please wait", context);
        }
    }

    public static void SeekTo(int CurruntTime) {
        mediaPlayer.seekTo(CurruntTime);
    }

    public static void showToast(String message, Context conext) {
        Toast toast = new Toast(conext);
        View view = LayoutInflater.from(conext).inflate(R.layout.toast_layout, null);
        TextView tvMessage = view.findViewById(R.id.tvMessage);
        tvMessage.setText(message);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 35);
        toast.setView(view);
        toast.show();
    }

    public static void stopPlaying() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.setOnPreparedListener(mp -> {
                Log.e("Playinggggg", "Startinggg");
                mediaPlayer.start();
                isMediaStart = true;
            });
        }
    }

    public static void stopMedia() {
        try {
            if (mediaPlayer == null) return;
            if (mediaPlayer.isPlaying()) {
                Log.e("Playinggggg", "stoppppp");
                mediaPlayer.stop();
                isMediaStart = false;
                isPrepare = false;
                isStop = true;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public static void pauseMedia() {
         if (mediaPlayer.isPlaying()) {
            Log.e("Playinggggg", "pauseeeeeee");
            mediaPlayer.pause();
            isPause = true;
//            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    public static boolean isPlaying() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                isPlaying = true;
            }
        } else {
            isPlaying = false;
        }
        return isPlaying;
    }

    public static void savePrefQueue(int position, boolean queue, boolean audio, ArrayList<AddToQueueModel> addToQueueModelList, Context ctx) {
        SharedPreferences shared11 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared11.edit();
        Gson gson11 = new Gson();
        String json11 = gson11.toJson(addToQueueModelList);
        editor.putString(CONSTANTS.PREF_KEY_queueList, json11);
        editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, queue);
        editor.putInt(CONSTANTS.PREF_KEY_position, position);
        editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, audio);
        editor.commit();
    }

    public static void resumeMedia() {
        if (isMediaStart) {
            if (!mediaPlayer.isPlaying()) {
                Log.e("Playinggggg", "resumeeeeeee");
//            mediaPlayer.seekTo(resumePosition);
                isResume = true;
                isPause = false;
                mediaPlayer.start();
            }
        }
    }

    public static void releasePlayer() {
        if (null != mediaPlayer) {
            mediaPlayer.release();
            isrelese = true;
        }
    }

    public static int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }

    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = totalDuration / 1000;
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void destroyPlayer() {
        stopMedia();
        releasePlayer();
        mediaPlayer = null;
    }

    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

/*    private void initMediaSession() throws RemoteException {
        if (mediaSessionManager != null) return; //mediaSessionManager exists

        mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        // Create a new MediaSession
        mediaSession = new MediaSessionCompat(getApplicationContext(), "AudioPlayer");
        //Get MediaSessions transport controls
        transportControls = mediaSession.getController().getTransportControls();
        //set MediaSession -> ready to receive media commands
        mediaSession.setActive(true);
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //Set mediaSession's MetaData
        updateMetaData();

        // Attach Callback to receive MediaSession updates
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            // Implement callbacks
            @Override
            public void onPlay() {
                super.onPlay();
                resumeMedia();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onPause() {
                super.onPause();
                pauseMedia();
                buildNotification(PlaybackStatus.PAUSED);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                skipToNext();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                skipToPrevious();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onStop() {
                super.onStop();
                removeNotification();
                //Stop the service
                stopSelf();
            }

            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
            }
        });
    }

    private void updateMetaData() {
        Bitmap albumArt = BitmapFactory.decodeResource(getResources(),
                R.drawable.image); //replace with medias albumArt
        // Update the current metadata
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, activeAudio.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, activeAudio.getAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, activeAudio.getTitle())
                .build());
    }*/
}
