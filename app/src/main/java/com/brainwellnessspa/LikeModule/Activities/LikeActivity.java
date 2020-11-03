package com.brainwellnessspa.LikeModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.brainwellnessspa.DownloadModule.Activities.DownloadsActivity;
import com.brainwellnessspa.LikeModule.Fragments.LikeAudiosFragment;
import com.brainwellnessspa.LikeModule.Fragments.LikePlaylistsFragment;
import com.brainwellnessspa.LikeModule.Models.LikesHistoryModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.databinding.ActivityLikeBinding;
import com.google.android.material.tabs.TabLayout;

import retrofit2.Callback;

public class LikeActivity extends AppCompatActivity {
    ActivityLikeBinding binding;
    Activity activity;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_like);
        activity = LikeActivity.this;
        ctx = LikeActivity.this;
        binding.llBack.setOnClickListener(view -> finish());

        prepareData();
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