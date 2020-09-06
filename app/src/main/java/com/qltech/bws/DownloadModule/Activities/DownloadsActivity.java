package com.qltech.bws.DownloadModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DownloadModule.Fragments.AudioDownloadsFragment;
import com.qltech.bws.DownloadModule.Fragments.PlaylistsDownlaodsFragment;
import com.qltech.bws.DownloadModule.Models.DownloadlistModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.ActivityDownloadsBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadsActivity extends AppCompatActivity {
    ActivityDownloadsBinding binding;
    ArrayList<DownloadlistModel.Audio> audioList;
    ArrayList<DownloadlistModel.Playlist> playlistList;
    String UserID;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_downloads);
        ctx = DownloadsActivity.this;
        Glide.with(ctx).load(R.drawable.loading).asGif().into(binding.ImgV);
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        prepareData();
    }

    public void prepareData() {
        showProgressBar();
        if (BWSApplication.isNetworkConnected(this)) {
            Call<DownloadlistModel> listCall = APIClient.getClient().getDownloadlistPlaylist(UserID);
            listCall.enqueue(new Callback<DownloadlistModel>() {
                @Override
                public void onResponse(Call<DownloadlistModel> call, Response<DownloadlistModel> response) {
                    if (response.isSuccessful()) {
                        hideProgressBar();
                        DownloadlistModel listModel = response.body();
                        audioList = new ArrayList<>();
                        playlistList = new ArrayList<>();
                        audioList = listModel.getResponseData().getAudio();
                        playlistList = listModel.getResponseData().getPlaylist();
                        binding.viewPager.setOffscreenPageLimit(2);
                        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Audio"));
                        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Playlists"));
                        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

                        TabAdapter adapter = new TabAdapter(getSupportFragmentManager(), ctx, binding.tabLayout.getTabCount(),
                                UserID, binding.progressBarHolder, binding.ImgV);
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
                }

                @Override
                public void onFailure(Call<DownloadlistModel> call, Throwable t) {
                    hideProgressBar();
                }
            });
        } else {
            BWSApplication.showToast( getString(R.string.no_server_found), this);
        }
    }


    public class TabAdapter extends FragmentStatePagerAdapter {
        int totalTabs;
        private Context myContext;
        String UserID;
        FrameLayout progressBarHolder;
        ImageView ImgV;

        public TabAdapter(FragmentManager fm, Context myContext, int totalTabs, String UserID, FrameLayout progressBarHolder, ImageView ImgV) {
            super(fm);
            this.myContext = myContext;
            this.totalTabs = totalTabs;
            this.UserID = UserID;
            this.progressBarHolder = progressBarHolder;
            this.ImgV = ImgV;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    Bundle bundle = new Bundle();
                    AudioDownloadsFragment audioDownloadsFragment = new AudioDownloadsFragment();
                    bundle.putString("UserID",UserID);
                    bundle.putParcelableArrayList("audioDownloadsFragment", audioList);
                    audioDownloadsFragment.setArguments(bundle);
                    return audioDownloadsFragment;
                case 1:
                    bundle = new Bundle();
                    PlaylistsDownlaodsFragment playlistsDownlaodsFragment = new PlaylistsDownlaodsFragment();
                    bundle.putString("UserID",UserID);
                    bundle.putParcelableArrayList("playlistsDownlaodsFragment", playlistList);
                    playlistsDownlaodsFragment.setArguments(bundle);
                    return playlistsDownlaodsFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return totalTabs;
        }
    }

    public void hideProgressBar() {
        binding.progressBarHolder.setVisibility(View.GONE);
        binding.ImgV.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void showProgressBar() {
        binding.progressBarHolder.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        binding.ImgV.setVisibility(View.VISIBLE);
        binding.ImgV.invalidate();
    }
}