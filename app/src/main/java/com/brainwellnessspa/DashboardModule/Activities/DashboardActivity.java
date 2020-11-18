package com.brainwellnessspa.DashboardModule.Activities;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.AudioManager;
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
import com.brainwellnessspa.Utility.MusicService;
import com.brainwellnessspa.databinding.ActivityDashboardBinding;

import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.ComeScreenAccount;
import static com.brainwellnessspa.DownloadModule.Adapters.AudioDownlaodsAdapter.comefromDownload;
import static com.brainwellnessspa.InvoiceModule.Activities.InvoiceActivity.invoiceToDashboard;
import static com.brainwellnessspa.Utility.MusicService.NOTIFICATION_ID;
import static com.brainwellnessspa.Utility.MusicService.isMediaStart;
import static com.brainwellnessspa.Utility.MusicService.isPause;
import static com.brainwellnessspa.Utility.MusicService.pauseMedia;
import static com.brainwellnessspa.Utility.MusicService.resumeMedia;

public class DashboardActivity extends AppCompatActivity implements AudioManager.OnAudioFocusChangeListener, SensorEventListener {
    public static int player = 0;
    ActivityDashboardBinding binding;
    boolean doubleBackToExitPressedOnce = false;
    String Goplaylist = "", PlaylistID = "", PlaylistName = "", PlaylistImage = "";
    TelephonyManager mTelephonyMgr;
    AudioManager mAudioManager;
    BroadcastReceiver broadcastReceiver;
    public static boolean audioPause = false;

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
        try {
            Intent playbackServiceIntent = new Intent(this, MusicService.class);
            startService(playbackServiceIntent);
        }catch (Exception e){
            e.printStackTrace();
        }
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "com.brainwellnessspa::MyWakelockTag");
        wakeLock.acquire();
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
                if (isMediaStart && !audioPause) {
                    pauseMedia();
                    audioPause = true;
                }
                }  // Put here the code to stop your music
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (invoiceToDashboard == 1) {
            finishAffinity();
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
        mTelephonyMgr.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
//        BWSApplication.notificationManager.cancelAll();
//        unregisterReceiver(broadcastReceiver);


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
                    if (isMediaStart && !audioPause) {
                        pauseMedia();
                        audioPause = true;
//                    binding.ivPlay.setVisibility(View.VISIBLE);
//                    binding.ivPause.setVisibility(View.GONE);
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
