package com.brainwellnessspa.LikeModule.Activities;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Activities.DashboardActivity;
import com.brainwellnessspa.DashboardModule.Models.SegmentAudio;
import com.brainwellnessspa.DashboardModule.Models.SegmentPlaylist;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment;
import com.brainwellnessspa.InvoiceModule.Activities.InvoiceActivity;
import com.brainwellnessspa.LikeModule.Fragments.LikeAudiosFragment;
import com.brainwellnessspa.LikeModule.Fragments.LikePlaylistsFragment;
import com.brainwellnessspa.LikeModule.Models.LikesHistoryModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Services.GlobalInitExoPlayer;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivityLikeBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.segment.analytics.Properties;

import java.util.ArrayList;
import java.util.List;

import ir.drax.netwatch.NetWatch;
import ir.drax.netwatch.cb.NetworkChangeReceiver_navigator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.ComeScreenAccount;
import static com.brainwellnessspa.DashboardModule.Activities.AudioPlayerActivity.AudioInterrupted;
import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.APP_SERVICE_STATUS;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.callResumePlayer;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.notificationId;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.player;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.relesePlayer;

public class LikeActivity extends AppCompatActivity implements NetworkChangeReceiver_navigator {
    public static boolean ComeFrom_LikePlaylist = false;
    public static int RefreshLikePlaylist = 0;
    ActivityLikeBinding binding;
    Activity activity;
    String AudioFlag, UserID, tabType = "";
    Context ctx;
    Properties p;
    GsonBuilder gsonBuilder;
    Gson gson;
    ArrayList<SegmentAudio> audioSection;
    ArrayList<SegmentPlaylist> playlistSection;
    List<LikesHistoryModel.ResponseData.Playlist> playlistDataModel;
    List<LikesHistoryModel.ResponseData.Audio> audioDataModel;
    private int numStarted = 0;
    int stackStatus = 0;
    boolean myBackPress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_like);
        SharedPreferences shared2 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared2.getString(CONSTANTS.PREF_KEY_UserID, ""));
        activity = LikeActivity.this;
        ctx = LikeActivity.this;
        ComeScreenAccount = 0;
        comefromDownload = "1";
        audioSection = new ArrayList<>();
        playlistSection = new ArrayList<>();
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        binding.llBack.setOnClickListener(view -> {
            comefromDownload = "0";
            ComeScreenAccount = 1;
            myBackPress = true;
            finish();
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(new AppLifecycleCallback());
        }

        prepareAllData();
        prepareData();
    }

    @Override
    protected void onResume() {
        ComeScreenAccount = 0;
        comefromDownload = "1";

        NetWatch.builder(LikeActivity.this)
                .setCallBack(new NetworkChangeReceiver_navigator() {
                    @Override
                    public void onConnected(int source) {
                        // do some thing
                        callResumePlayer(ctx);
                    }

                    @Override
                    public View onDisconnected() {
                        // do some other stuff


                        return null;//To display a dialog simply return a custom view or just null to ignore it
                    }
                })
                .setNotificationCancelable(false)
                .build();
        callMembershipMediaPlayer();
        super.onResume();
    }

    private void callMembershipMediaPlayer() {
        try {
            GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
            globalInitExoPlayer.UpdateMiniPlayer(ctx);
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            if (!AudioFlag.equalsIgnoreCase("0")) {
                Fragment fragment = new MiniPlayerFragment();
                FragmentManager fragmentManager1 = getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .add(R.id.flContainer, fragment)
                        .commit();
                comefromDownload = "1";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void prepareData() {
        callMembershipMediaPlayer();
        binding.viewPager.setOffscreenPageLimit(2);
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Audios"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Playlists"));
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        TabAdapter adapter = new TabAdapter(getSupportFragmentManager(), binding.tabLayout.getTabCount());
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
        if (ComeFrom_LikePlaylist) {
            binding.viewPager.setCurrentItem(1);
            ComeFrom_LikePlaylist = false;
        } else {
            binding.viewPager.setCurrentItem(0);
        }
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
                try {
                    if (tab.getPosition() == 0) {
                        tabType = "Audio Tab";
                        p = new Properties();
                        p.putValue("userId", UserID);
                        p.putValue("tabType", "Audio Tab");
                        for (int i = 0; i < audioDataModel.size(); i++) {
                            SegmentAudio e = new SegmentAudio();
                            e.setAudioId(audioDataModel.get(i).getID());
                            e.setAudioName(audioDataModel.get(i).getName());
                            e.setMasterCategory(audioDataModel.get(i).getAudiomastercat());
                            e.setSubCategory(audioDataModel.get(i).getAudioSubCategory());
                            e.setAudioDuration(audioDataModel.get(i).getAudioDuration());
                            audioSection.add(e);
                        }
                        p.putValue("likedAudios", gson.toJson(audioSection));
                        BWSApplication.addToSegment("Liked Screen Viewed", p, CONSTANTS.screen);
                    } else if (tab.getPosition() == 1) {
                        tabType = "Playlist Tab";
                        p = new Properties();
                        p.putValue("userId", UserID);
                        p.putValue("tabType", "Playlist Tab");
                        for (int i = 0; i < playlistDataModel.size(); i++) {
                            SegmentPlaylist e = new SegmentPlaylist();
                            e.setPlaylistId(playlistDataModel.get(i).getPlaylistId());
                            e.setPlaylistName(playlistDataModel.get(i).getPlaylistName());
                            if (playlistDataModel.get(i).getCreated().equalsIgnoreCase("1")) {
                                e.setPlaylistType("Created");
                            } else {
                                e.setPlaylistType("Default");
                            }
                            e.setPlaylistDuration(playlistDataModel.get(i).getTotalDuration());
                            e.setAudioCount(playlistDataModel.get(i).getTotalAudio());
                            playlistSection.add(e);
                        }
                        p.putValue("likedPlaylists", gson.toJson(playlistSection));
                        BWSApplication.addToSegment("Liked Screen Viewed", p, CONSTANTS.screen);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        comefromDownload = "0";
        ComeScreenAccount = 1;
        myBackPress = true;
        finish();
    }

    @Override
    public void onConnected(int source) {
        callResumePlayer(ctx);
    }

    @Override
    public View onDisconnected() {
        return null;
    }

    @Override
    protected void onDestroy() {

        NetWatch.unregister(this);
        super.onDestroy();
    }

    public class TabAdapter extends FragmentStatePagerAdapter {
        int totalTabs;

        public TabAdapter(FragmentManager fm, int totalTabs) {
            super(fm);
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

    public void prepareAllData() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            Call<LikesHistoryModel> listCall = APIClient.getClient().getLikeAudioPlaylistListing(UserID);
            listCall.enqueue(new Callback<LikesHistoryModel>() {
                @Override
                public void onResponse(Call<LikesHistoryModel> call, Response<LikesHistoryModel> response) {
                    try {
                        LikesHistoryModel listModel = response.body();
                        if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                            List<LikesHistoryModel.ResponseData.Audio> listDataModel = listModel.getResponseData().getAudio();
                            audioDataModel = listDataModel;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<LikesHistoryModel> call, Throwable t) {
                }
            });

            Call<LikesHistoryModel> listCalls = APIClient.getClient().getLikeAudioPlaylistListing(UserID);
            listCalls.enqueue(new Callback<LikesHistoryModel>() {
                @Override
                public void onResponse(Call<LikesHistoryModel> call, Response<LikesHistoryModel> response) {
                    try {
                        LikesHistoryModel listModel = response.body();
                        if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                            List<LikesHistoryModel.ResponseData.Playlist> listDataModel = listModel.getResponseData().getPlaylist();
                            playlistDataModel = listDataModel;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<LikesHistoryModel> call, Throwable t) {
                }
            });
        } else {
        }
    }

    class AppLifecycleCallback implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (numStarted == 0) {
                stackStatus = 1;
                APP_SERVICE_STATUS = getString(R.string.Foreground);
                Log.e("APPLICATION", "APP IN FOREGROUND");
                //app went to foreground
            }
            numStarted++;
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            numStarted--;
            if (numStarted == 0) {
                if (!myBackPress) {
                    Log.e("APPLICATION", "Back press false");
                    stackStatus = 2;
                } else {
                    myBackPress = true;
                    stackStatus = 1;
                    Log.e("APPLICATION", "back press true ");
                }
                APP_SERVICE_STATUS = getString(R.string.Background);
                Log.e("APPLICATION", "App is in BACKGROUND");
                // app went to background
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (numStarted == 0 && stackStatus == 2) {
                Log.e("Destroy", "Activity Destoryed");
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(notificationId);
                relesePlayer(getApplicationContext());
            } else {
                Log.e("Destroy", "Activity go in main activity");
            }
        }
    }

}