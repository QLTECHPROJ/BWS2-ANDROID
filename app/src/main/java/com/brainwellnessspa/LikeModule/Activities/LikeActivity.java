package com.brainwellnessspa.LikeModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.brainwellnessspa.DownloadModule.Activities.DownloadsActivity;
import com.brainwellnessspa.LikeModule.Fragments.LikeAudiosFragment;
import com.brainwellnessspa.LikeModule.Fragments.LikePlaylistsFragment;
import com.brainwellnessspa.LikeModule.Models.LikesHistoryModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivityLikeBinding;
import com.google.android.material.tabs.TabLayout;

import retrofit2.Callback;

public class LikeActivity extends AppCompatActivity {
    ActivityLikeBinding binding;
    Activity activity;
    String AudioFlag, UserID;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_like);
        activity = LikeActivity.this;
        ctx = LikeActivity.this;
        SharedPreferences shared2 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared2.getString(CONSTANTS.PREF_KEY_UserID, ""));
        binding.llBack.setOnClickListener(view -> finish());
        prepareData();
        RefreshData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        RefreshData();
    }

    public void RefreshData() {
        SharedPreferences shared22 = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
        AudioFlag = shared22.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        if (!AudioFlag.equalsIgnoreCase("0")) {
            Fragment fragment = new TransparentPlayerFragment();
            FragmentManager fragmentManager1 = getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .add(R.id.flContainer, fragment)
                    .commit();
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            params.setMargins(10, 8, 10, 210);
//            binding.llSpace.setLayoutParams(params);
        }
      /*  else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(10, 8, 10, 20);
            binding.llSpace.setLayoutParams(params);
        }*/
    }

    public void prepareData() {
        binding.viewPager.setOffscreenPageLimit(2);
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Audios"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Playlists"));
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        TabAdapter adapter = new TabAdapter(getSupportFragmentManager(), ctx, binding.tabLayout.getTabCount());
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        RefreshData();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public class TabAdapter extends FragmentStatePagerAdapter {
        int totalTabs;
        private Context myContext;
        Callback<LikesHistoryModel> likesHistoryModelCallback;

        public TabAdapter(FragmentManager fm, Context myContext, int totalTabs) {
            super(fm);
            this.myContext = myContext;
            this.totalTabs = totalTabs;
        }

        public TabAdapter(FragmentManager fm, Callback<LikesHistoryModel> likesHistoryModelCallback, int totalTabs) {
            super(fm);
            this.likesHistoryModelCallback = likesHistoryModelCallback;
            this.totalTabs = totalTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    LikeAudiosFragment likeAudiosFragment = new LikeAudiosFragment();
                    Bundle bundle = new Bundle();
                    likeAudiosFragment.setArguments(bundle);
                    return likeAudiosFragment;
                case 1:
                    LikePlaylistsFragment likePlaylistsFragment = new LikePlaylistsFragment();
                    bundle = new Bundle();
                    likePlaylistsFragment.setArguments(bundle);
                    return likePlaylistsFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return totalTabs;
        }
    }
}