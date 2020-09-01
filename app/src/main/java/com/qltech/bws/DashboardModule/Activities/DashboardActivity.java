package com.qltech.bws.DashboardModule.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.qltech.bws.DashboardModule.Audio.AudioFragment;
import com.qltech.bws.R;
import com.qltech.bws.databinding.ActivityDashboardBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class DashboardActivity extends AppCompatActivity {
    ActivityDashboardBinding binding;

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

        binding.llPlayearMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DashboardActivity.this, PlayWellnessActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (binding.navView.getSelectedItemId() == R.id.navigation_audio) {
            binding.navView.setSelectedItemId(R.id.navigation_audio);
            super.onBackPressed();
            finish();
        } else if (binding.navView.getSelectedItemId() == R.id.navigation_playlist) {
            binding.navView.setSelectedItemId(R.id.navigation_playlist);
            /*if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                finishAffinity();
                return;
            }*/
        } else if (binding.navView.getSelectedItemId() == R.id.navigation_search) {
            binding.navView.setSelectedItemId(R.id.navigation_search);
        } else if (binding.navView.getSelectedItemId() == R.id.navigation_appointment) {
            binding.navView.setSelectedItemId(R.id.navigation_appointment);
        } else if (binding.navView.getSelectedItemId() == R.id.navigation_account) {
            binding.navView.setSelectedItemId(R.id.navigation_account);
        }
    }
}