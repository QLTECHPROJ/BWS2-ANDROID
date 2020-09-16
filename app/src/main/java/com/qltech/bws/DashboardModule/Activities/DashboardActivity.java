package com.qltech.bws.DashboardModule.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

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
import com.qltech.bws.Utility.MusicService;
import com.qltech.bws.databinding.ActivityDashboardBinding;

public class DashboardActivity extends AppCompatActivity{
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
        if (!AudioFlag.equalsIgnoreCase("0")) {
            Fragment fragment = new TransparentPlayerFragment();
            FragmentManager fragmentManager1 = getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .add(R.id.rlAudiolist, fragment)
                    .commit();
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
