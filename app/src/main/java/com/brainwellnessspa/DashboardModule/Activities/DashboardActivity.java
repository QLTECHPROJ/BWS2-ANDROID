package com.brainwellnessspa.DashboardModule.Activities;

import android.app.NotificationManager;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Account.AccountFragment;
import com.brainwellnessspa.DashboardModule.Audio.AudioFragment;
import com.brainwellnessspa.DashboardModule.Playlist.MyPlaylistsFragment;
import com.brainwellnessspa.DashboardModule.Playlist.PlaylistFragment;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MyNetworkReceiver;
import com.brainwellnessspa.databinding.ActivityDashboardBinding;
import com.segment.analytics.Properties;

import ir.drax.netwatch.NetWatch;
import ir.drax.netwatch.cb.NetworkChangeReceiver_navigator;

import static com.brainwellnessspa.BWSApplication.deleteCache;
import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.ComeScreenAccount;
import static com.brainwellnessspa.DashboardModule.Playlist.MyPlaylistsFragment.ComeBackPlaylist;
import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;
import static com.brainwellnessspa.InvoiceModule.Activities.InvoiceActivity.invoiceToDashboard;
import static com.brainwellnessspa.InvoiceModule.Activities.InvoiceActivity.invoiceToRecepit;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.callResumePlayer;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.notificationId;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.relesePlayer;

public class DashboardActivity extends AppCompatActivity implements NetworkChangeReceiver_navigator /*implements AudioManager.OnAudioFocusChangeListener */ {
    public static int miniPlayer = 0;
    public static boolean audioClick = false, tutorial = false;
    ActivityDashboardBinding binding;
    boolean doubleBackToExitPressedOnce = false;
    boolean backpressed = false;
    String Goplaylist = "", PlaylistID = "", PlaylistName = "", PlaylistImage = "", PlaylistType = "", New = "";
    UiModeManager uiModeManager;
    MyNetworkReceiver myNetworkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_audio, R.id.navigation_playlist, R.id.navigation_search,
                R.id.navigation_appointment, R.id.navigation_account).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(binding.navView, navController);

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Log.e("Nite Mode :", String.valueOf(AppCompatDelegate.getDefaultNightMode()));
        }

        uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        if (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_AUTO
                || uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES
                || uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_CUSTOM) {
            uiModeManager.setNightMode(UiModeManager.MODE_NIGHT_NO);

            Log.e("Nite Mode :", String.valueOf(uiModeManager.getNightMode()));
        }
        registerReceiver(myNetworkReceiver = new MyNetworkReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            boolean isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(packageName);
            if (!isIgnoringBatteryOptimizations) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivityForResult(intent, 15695);
            }
        }

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
                "com.brainwellnessspa::MyWakelockTag");
        wakeLock.acquire(600 * 60 * 1000L /*600 minutes*/);
//        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
//        filter.addAction(Intent.ACTION_SCREEN_OFF);
//        BroadcastReceiver mReceiver = new ScreenReceiver();
//        registerReceiver(mReceiver, filter);
        if (getIntent().hasExtra("Goplaylist")) {
            New = getIntent().getStringExtra("New");
            Goplaylist = getIntent().getStringExtra("Goplaylist");
            PlaylistID = getIntent().getStringExtra("PlaylistID");
            PlaylistName = getIntent().getStringExtra("PlaylistName");
            PlaylistImage = getIntent().getStringExtra("PlaylistImage");
            PlaylistType = getIntent().getStringExtra("PlaylistType");
            if(getIntent().hasExtra("notification")){
                Properties p = new Properties();
              /*  AnalyticsContext.Campaign campaign = new AnalyticsContext.Campaign();
                campaign.putName(getIntent().getStringExtra(CONSTANTS.title));
                campaign.putValue("playlistID", PlaylistID);
                campaign.putValue("playlistName", PlaylistName);
                campaign.putContent(getIntent().getStringExtra("message"));
                campaign.putMedium("Push");
                campaign.putSource("Admin");
                p.putValue("action", "Accept");
                p.putValue("campaign", campaign);*/
                SharedPreferences shared2 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                String   UserID = (shared2.getString(CONSTANTS.PREF_KEY_UserID, ""));
                p.putValue("userId", UserID);
                p.putValue("playlistId",PlaylistID);
                p.putName(PlaylistName);
                p.putValue("message",getIntent().getStringExtra("message"));
                BWSApplication.addToSegment("Push Notification Tapped",p, CONSTANTS.track);
            }
        }
        if (Goplaylist.equalsIgnoreCase("1")) {
            binding.navView.setSelectedItemId(R.id.navigation_playlist);
            Fragment myPlaylistsFragment = new MyPlaylistsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("New", New);
            bundle.putString("PlaylistID", PlaylistID);
            bundle.putString("PlaylistName", PlaylistName);
            bundle.putString("PlaylistImage", PlaylistImage);
            bundle.putString("PlaylistType", PlaylistType);
            bundle.putString("MyDownloads", "0");

/*            Properties p = new Properties();
            p.putValue("PlaylistID", PlaylistID);
            p.putValue("PlaylistName", PlaylistName);
            p.putValue("PlaylistImage", PlaylistImage);
            p.putValue("PlaylistType", PlaylistType);
            BWSApplication.addToSegment("Push Notification Tapped", p, CONSTANTS.track);*/
            myPlaylistsFragment.setArguments(bundle);
            FragmentManager fragmentManager1 = getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .replace(R.id.flContainer, myPlaylistsFragment)
                    .commit();
        }

        if (invoiceToRecepit == 1) {
            binding.navView.setSelectedItemId(R.id.navigation_account);
            Fragment fragment = new AccountFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.flContainer, fragment)
                    .commit();
        }

        if (ComeBackPlaylist) {
            binding.navView.setSelectedItemId(R.id.navigation_playlist);
            Fragment fragment = new PlaylistFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.flContainer, fragment)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (requestCode == 15695) {
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                boolean isIgnoringBatteryOptimizations = false;
                isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getPackageName());
                if (isIgnoringBatteryOptimizations) {
                    // Ignoring battery optimization
                } else {
                    // Not ignoring battery optimization
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (tutorial) {
            binding.navView.setSelectedItemId(R.id.navigation_audio);
            Fragment fragment = new AudioFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.flContainer, fragment)
                    .commit();
        }

        if (invoiceToDashboard == 1) {
            finishAffinity();
            deleteCache(DashboardActivity.this);
        }

        if (binding.navView.getSelectedItemId() == R.id.navigation_audio) {
            binding.navView.setSelectedItemId(R.id.navigation_audio);
            Fragment fragment = new AudioFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.flContainer, fragment)
                    .commit();
            if (doubleBackToExitPressedOnce) {
                finish();
                backpressed = true;
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
    protected void onPause() {
//        BWSApplication.showToast("Pauseeeeeee", DashboardActivity.this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        NetWatch.builder(this)
                .setCallBack(new NetworkChangeReceiver_navigator() {
                    @Override
                    public void onConnected(int source) {
                        // do some thing
                        callResumePlayer(DashboardActivity.this);
                    }

                    @Override
                    public View onDisconnected() {
                        // do some other stuff
                        return null;//To display a dialog simply return a custom view or just null to ignore it
                    }
                })
                .setNotificationCancelable(false)
                .build();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        NetWatch.unregister(this);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
        relesePlayer(DashboardActivity.this);
        unregisterReceiver(myNetworkReceiver);
        deleteCache(DashboardActivity.this);
        super.onDestroy();
    }

    @Override
    public void onConnected(int source) {
        callResumePlayer(DashboardActivity.this);
    }

    @Override
    public View onDisconnected() {
        return null;
    }

  /*  @Override
    public void onAudioFocusChange(int i) {
        switch (i) {
            case AudioManager.AUDIOFOCUS_GAIN:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Resume your media player here
//                if (audioPause)
//                    resumeMedia();
//                audioPause = false;
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
//                if(!isPause) {
//                    if (mediaPlayer != null) {
//                        if (isMediaStart && !audioPause) {
//                            oTime = getProgressPercentage(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration());
//                            pauseMedia();
//                            audioPause = true;
////                    binding.ivPlay.setVisibility(View.VISIBLE);
////                    binding.ivPause.setVisibility(View.GONE);
//                        }
//                    }
//                }
//                MusicService.pauseMedia();// Pause your media player here
                break;
        }
    }*/
}
