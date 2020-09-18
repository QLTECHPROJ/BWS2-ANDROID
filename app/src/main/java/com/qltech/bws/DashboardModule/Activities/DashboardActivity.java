package com.qltech.bws.DashboardModule.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.qltech.bws.R;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.ActivityDashboardBinding;

public class DashboardActivity extends AppCompatActivity {
    public static int player = 0;
    ActivityDashboardBinding binding;
    boolean doubleBackToExitPressedOnce = false;

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

        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        try {
            if (!AudioFlag.equalsIgnoreCase("0")) {
                Fragment fragment = new TransparentPlayerFragment();
                FragmentManager fragmentManager1 = getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .add(R.id.rlAudiolist, fragment)
                        .commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
// Checks if the device is on a metered network
        if (connMgr.isActiveNetworkMetered()) {
            // Checks user’s Data Saver settings.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                switch (connMgr.getRestrictBackgroundStatus()) {
                    case ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED:
                        // Background data usage is blocked for this app. Wherever possible,
                        // the app should also use less data in the foreground.

                    case ConnectivityManager.RESTRICT_BACKGROUND_STATUS_WHITELISTED:
                        // The app is allowed to bypass Data Saver. Nevertheless, wherever possible,
                        // the app should use less data in the foreground and background.

                    case ConnectivityManager.RESTRICT_BACKGROUND_STATUS_DISABLED:
                        // Data Saver is disabled. Since the device is connected to a
                        // metered network, the app should use less data wherever possible.
                }
            }
        } else {
            // The device is not on a metered network.
            // Use data as required to perform syncs, downloads, and updates.
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        try {
            if (binding.navView.getSelectedItemId() == R.id.navigation_audio) {
                binding.navView.setSelectedItemId(R.id.navigation_audio);
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                BWSApplication.showToast("Press again to exit.", DashboardActivity.this);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        MusicService.releasePlayer();
    }

}
