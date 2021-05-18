package com.brainwellnessspa.dashboardModule.activities;

import android.app.NotificationManager;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.brainwellnessspa.DashboardOldModule.Activities.DashboardActivity;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.MyBatteryReceiver;
import com.brainwellnessspa.Utility.MyNetworkReceiver;
import com.brainwellnessspa.databinding.ActivityDashboardBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import ir.drax.netwatch.NetWatch;
import ir.drax.netwatch.cb.NetworkChangeReceiver_navigator;

import static com.brainwellnessspa.BWSApplication.deleteCache;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.callResumePlayer;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.notificationId;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.relesePlayer;

public class BottomNavigationActivity extends AppCompatActivity  implements NetworkChangeReceiver_navigator {
    public static int miniPlayer = 0;
    public static boolean audioClick = false, tutorial = false;
    ActivityDashboardBinding binding;
    boolean doubleBackToExitPressedOnce = false;
    boolean backpressed = false;
    String Goplaylist = "", PlaylistID = "", PlaylistName = "", PlaylistImage = "", PlaylistType = "", New = "";
    UiModeManager uiModeManager;
    MyNetworkReceiver myNetworkReceiver;
    MyBatteryReceiver myBatteryReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_Home, R.id.navigation_Manage,/* R.id.navigation_Wellness, R.id.navigation_Elevate, */R.id.navigation_Profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
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
        registerReceiver(myBatteryReceiver = new MyBatteryReceiver(),new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
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
    protected void onResume() {
        NetWatch.builder(this)
                .setCallBack(new NetworkChangeReceiver_navigator() {
                    @Override
                    public void onConnected(int source) {
                        // do some thing
                        callResumePlayer(BottomNavigationActivity.this);
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
        relesePlayer(BottomNavigationActivity.this);
        unregisterReceiver(myNetworkReceiver);
        deleteCache(BottomNavigationActivity.this);
        super.onDestroy();
    }

    @Override
    public void onConnected(int source) {
        callResumePlayer(BottomNavigationActivity.this);
    }

    @Override
    public View onDisconnected() {
        return null;
    }

}