package com.brainwellnessspa.DashboardModule.Activities;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Playlist.MyPlaylistsFragment;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Services.ScreenReceiver;
import com.brainwellnessspa.Utility.MusicService;
import com.brainwellnessspa.databinding.ActivityDashboardBinding;

import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.ComeScreenAccount;
import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;
import static com.brainwellnessspa.InvoiceModule.Activities.InvoiceActivity.invoiceToDashboard;
import static com.brainwellnessspa.Utility.MusicService.NOTIFICATION_ID;
import static com.brainwellnessspa.Utility.MusicService.deleteCache;
import static com.brainwellnessspa.Utility.MusicService.getProgressPercentage;
import static com.brainwellnessspa.Utility.MusicService.isMediaStart;
import static com.brainwellnessspa.Utility.MusicService.isPause;
import static com.brainwellnessspa.Utility.MusicService.mediaPlayer;
import static com.brainwellnessspa.Utility.MusicService.oTime;
import static com.brainwellnessspa.Utility.MusicService.pauseMedia;
import static com.brainwellnessspa.Utility.MusicService.resumeMedia;

public class DashboardActivity extends AppCompatActivity implements AudioManager.OnAudioFocusChangeListener, SensorEventListener {
    public static int miniPlayer = 0;
    ActivityDashboardBinding binding;
    boolean doubleBackToExitPressedOnce = false;
    String Goplaylist = "", PlaylistID = "", PlaylistName = "", PlaylistImage = "";
    TelephonyManager mTelephonyMgr;
    AudioManager mAudioManager;
    BroadcastReceiver broadcastReceiver;
    public static boolean audioPause = false,audioClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_audio, R.id.navigation_playlist, R.id.navigation_search,
                R.id.navigation_appointment, R.id.navigation_account).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyMgr.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        /*try {
            Intent playbackServiceIntent = new Intent(this, MusicService.class);
            startService(playbackServiceIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(playbackServiceIntent);
            }
        }catch (Exception e){
            e.printStackTrace();
Android Media plyer stop playing after few min and not buffer that audio in online stream
            I have an issue with Media player always stopping randomly after some few mins. then I put secondary buffer progress I have check my audio is not buffering after some time that's why it is not play after that buffering min.

is there any solution to over come this issue? please help me for this as soon as possible.
    if (mediaPlayer == null)
                mediaPlayer = new MediaPlayer();
                initMediaplyer();
                if (mediaPlayer.isPlaying()) {
                    Log.e("Playinggggg", "stoppppp");
                    mediaPlayer.stop();
                    isMediaStart = false;
                    isPrepare = false;
                    isPause = false;
                }
                mediaPlayer = new MediaPlayer();
                initMediaplyer();
                mediaPlayer.setDataSource(url);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mediaPlayer.setAudioAttributes(
                            new AudioAttributes
                                    .Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .build());
                }
                mediaPlayer.prepareAsync();
                isPause = false;
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
                    isprogressbar = false;
                    setMediaPlaybackState(STATE_PLAYING);
                    mediaPlayer.setOnBufferingUpdateListener((mediaPlayer, i) -> {
                        binding.simpleSeekbar.setSecondaryProgress(i);
                    });
                    mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                        if(mediaPlayer.isPlaying()){
                            callComplete();
                        }
                    });
                    mediaPlayer.setOnErrorListener((mediaPlayer, i, i1) -> {
                        switch (i) {
                            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + i1);
                                break;
                            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + i1);
                                break;
                            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + i1);
                                break;
                            default:
                                Log.d("MediaPlayer Error", "not conform " + i1);
                                break;
                        }

                        return false;
                    });
                });
            }
        }

    if (isPause) {
            binding.llPlay.setVisibility(View.VISIBLE);
            binding.llPause.setVisibility(View.GONE);
            buildNotification(PlaybackStatus.PAUSED, ctx, mainPlayModelList, addToQueueModelList, playFrom, position);
        } else {
            binding.llPause.setVisibility(View.VISIBLE);
            binding.llPlay.setVisibility(View.GONE);
            buildNotification(PlaybackStatus.PLAYING, ctx, mainPlayModelList, addToQueueModelList, playFrom, position);
        }

    private void initMediaplyer() {
        try {

            if (mediaSessionManager != null) return; //mediaSessionManager exists

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaSessionManager = (MediaSessionManager) ctx.getSystemService(Context.MEDIA_SESSION_SERVICE);
            }
//        mediaPlayer.setWakeMode(ctx.getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
//        // Create a new MediaSession
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mediaSession = new MediaSessionCompat(ctx.getApplicationContext(), "AudioPlayer");
            //Get MediaSessions transport controls
            transportControls = mediaSession.getController().getTransportControls();
            //set MediaSession -> ready to receive media commands
            mediaSession.setActive(true);
            //indicate that the MediaSession handles transport control commands
            // through its MediaSessionCompat.Callback.
            mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

            //Set mediaSession's MetaData
//        updateMetaData();

            // Attach Callback to receive MediaSession updates
            mMediaControllerCompatCallback = new MediaControllerCompat.Callback() {

                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    super.onPlaybackStateChanged(state);
                    if( state == null ) {
                        return;
                    }

                    switch( state.getState() ) {
                        case PlaybackStateCompat.STATE_PLAYING: {
                            mCurrentState = STATE_PLAYING;
                            break;
                        }
                        case PlaybackStateCompat.STATE_PAUSED: {
                            mCurrentState = STATE_PAUSED;
                            break;
                        }
                    }
                }
            };
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
                    if (!url.equalsIgnoreCase("")) {
                        callNext();
                    }
                }

                @Override
                public void onSkipToPrevious() {
                    super.onSkipToPrevious();

                    if (!url.equalsIgnoreCase("")) {
                        callPrevious();
                    }
                }

            @Override
            public void onStop() {
                super.onStop();
            }

//            @Override
//            public void onSeekTo(long position) {
//                super.onSeekTo(position);
//            }
            });
        } catch (Exception e) {
            Log.e("playwell init media err",e.getMessage());
            e.printStackTrace();
        }
    }
        }*/
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "com.brainwellnessspa::MyWakelockTag");
        wakeLock.acquire();
//        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
//        filter.addAction(Intent.ACTION_SCREEN_OFF);
//        BroadcastReceiver mReceiver = new ScreenReceiver();
//        registerReceiver(mReceiver, filter);
        if (getIntent().hasExtra("Goplaylist")) {
            Goplaylist = getIntent().getStringExtra("Goplaylist");
            PlaylistID = getIntent().getStringExtra("PlaylistID");
            PlaylistName = getIntent().getStringExtra("PlaylistName");
            PlaylistImage = getIntent().getStringExtra("PlaylistImage");
        }
         if (Goplaylist.equalsIgnoreCase("1")) {
            binding.navView.setSelectedItemId(R.id.navigation_playlist);
            Fragment myPlaylistsFragment = new MyPlaylistsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("New", "0");
            bundle.putString("PlaylistID", PlaylistID);
            bundle.putString("PlaylistName", PlaylistName);
            bundle.putString("PlaylistImage", PlaylistImage);
            bundle.putString("MyDownloads", "0");
            myPlaylistsFragment.setArguments(bundle);
            FragmentManager fragmentManager1 = getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .replace(R.id.flContainer, myPlaylistsFragment)
                    .commit();
        }

        if (binding.navView.getSelectedItemId() == R.id.navigation_audio) {
            ComeScreenAccount = 0;
            comefromDownload = "0";
        } else if (binding.navView.getSelectedItemId() == R.id.navigation_playlist) {
            ComeScreenAccount = 0;
            comefromDownload = "0";
        } else if (binding.navView.getSelectedItemId() == R.id.navigation_search) {
            ComeScreenAccount = 0;
            comefromDownload = "0";
        } else if (binding.navView.getSelectedItemId() == R.id.navigation_appointment) {
            ComeScreenAccount = 0;
            comefromDownload = "0";
        } else if (binding.navView.getSelectedItemId() == R.id.navigation_account) {
            ComeScreenAccount = 1;
            comefromDownload = "0";
        }

//
//        ConnectivityManager connMgr = (ConnectivityManager)
//                getSystemService(Context.CONNECTIVITY_SERVICE);
//// Checks if the device is on a metered network
//        if (connMgr.isActiveNetworkMetered()) {
//            // Checks user’s Data Saver settings.
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                switch (connMgr.getRestrictBackgroundStatus()) {
//                    case ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED:
//                        // Background data usage is blocked for this app. Wherever possible,
//                        // the app should also use less data in the foreground.
//
//                    case ConnectivityManager.RESTRICT_BACKGROUND_STATUS_WHITELISTED:
//                        // The app is allowed to bypass Data Saver. Nevertheless, wherever possible,
//                        // the app should use less data in the foreground and background.
//
//                    case ConnectivityManager.RESTRICT_BACKGROUND_STATUS_DISABLED:
//                        // Data Saver is disabled. Since the device is connected to a
//                        // metered network, the app should use less data wherever possible.
//                }
//            }
//        } else {
//            // The device is not on a metered network.
//            // Use data as required to perform syncs, downloads, and updates.
//        }
    }

    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING || state == TelephonyManager.CALL_STATE_OFFHOOK) {
                if(!isPause){
                    if(mediaPlayer!=null) {
                        if (isMediaStart && !audioPause) {
                            oTime = getProgressPercentage(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration());
                            pauseMedia();
                            audioPause = true;
                        }
                    }
                }  // Put here the code to stop your music
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };

    @Override
    public void onBackPressed() {
        if (invoiceToDashboard == 1) {
            finishAffinity();
            deleteCache(DashboardActivity.this);
        }

        if (binding.navView.getSelectedItemId() == R.id.navigation_audio) {
            binding.navView.setSelectedItemId(R.id.navigation_audio);
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            BWSApplication.showToast("Press again to exit", DashboardActivity.this);

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteCache(DashboardActivity.this);
        mTelephonyMgr.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    @Override
    public void onAudioFocusChange(int i) {
        switch (i) {
            case AudioManager.AUDIOFOCUS_GAIN:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Resume your media player here
                if (audioPause)
                    resumeMedia();
                audioPause = false;
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if(!isPause) {
                    if (mediaPlayer != null) {
                        if (isMediaStart && !audioPause) {
                            oTime = getProgressPercentage(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration());
                            pauseMedia();
                            audioPause = true;
//                    binding.ivPlay.setVisibility(View.VISIBLE);
//                    binding.ivPause.setVisibility(View.GONE);
                        }
                    }
                }
//                MusicService.pauseMedia();// Pause your media player here
                break;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
