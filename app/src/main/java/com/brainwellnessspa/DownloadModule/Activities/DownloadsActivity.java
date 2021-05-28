package com.brainwellnessspa.DownloadModule.Activities;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.room.Room;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardOldModule.Models.SegmentAudio;
import com.brainwellnessspa.DashboardOldModule.Models.SegmentPlaylist;
import com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment;
import com.brainwellnessspa.DownloadModule.Fragments.PlaylistsDownlaodsFragment;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.AudioDatabase;

import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.RoomDataBase.DownloadPlaylistDetails;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivityDownloadsBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.segment.analytics.Properties;

import java.util.ArrayList;
import java.util.List;

import ir.drax.netwatch.NetWatch;
import ir.drax.netwatch.cb.NetworkChangeReceiver_navigator;


import static com.brainwellnessspa.BWSApplication.DB;
import static com.brainwellnessspa.BWSApplication.IsLock;
import static com.brainwellnessspa.BWSApplication.getAudioDataBase;
import static com.brainwellnessspa.DownloadModule.Activities.DownloadPlaylistActivity.comeDeletePlaylist;
import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.callResumePlayer;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.notificationId;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.relesePlayer;

public class DownloadsActivity extends AppCompatActivity implements NetworkChangeReceiver_navigator {
    public static boolean ComeFrom_Playlist = false;
    ActivityDownloadsBinding binding;
    List<DownloadAudioDetails> audioDownloadList;
    List<DownloadPlaylistDetails> playlistList;
    String UserID, CoUserID;
    Context ctx;
    Properties p;
    private int numStarted = 0;
    int stackStatus = 0;
    boolean myBackPress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_downloads);
        ctx = DownloadsActivity.this;
        comefromDownload = "1";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(new AppLifecycleCallback());
        }
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, AppCompatActivity.MODE_PRIVATE);
        UserID = shared.getString(CONSTANTS.PREFE_ACCESS_UserID, "");
        CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "");
        DB = getAudioDataBase(ctx);
        p = new Properties();
        binding.llBack.setOnClickListener(view -> {
            myBackPress = true;
            comefromDownload = "0";
            finish();
        });
        prepareData();
    }

    @Override
    public void onBackPressed() {
        myBackPress = true;
        comefromDownload = "0";
        finish();
    }

    public void prepareData() {
        comefromDownload = "1";
        callMembershipMediaPlayer();
        audioDownloadList = new ArrayList<>();
        playlistList = new ArrayList<>();
/*        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<DownloadlistModel> listCall = APIClient.getClient().getDownloadlistPlaylist(UserID);
            listCall.enqueue(new Callback<DownloadlistModel>() {
                @Override
                public void onResponse(Call<DownloadlistModel> call, Response<DownloadlistModel> response) {
                    if (response.isSuccessful()) {*/
//                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);

//                        audioList = listModel.getResponseData().getAudio();
//                        playlistList = listModel.getResponseData().getPlaylist();
        binding.viewPager.setOffscreenPageLimit(2);
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Audios"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Playlists"));
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        TabAdapter adapter = new TabAdapter(getSupportFragmentManager(), ctx, binding.tabLayout.getTabCount(),
                UserID, binding.progressBarHolder, binding.progressBar);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
        if (ComeFrom_Playlist) {
            binding.viewPager.setCurrentItem(1);
            ComeFrom_Playlist = false;
        } else {
            binding.viewPager.setCurrentItem(0);
        }
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
                p = new Properties();
                p.putValue("coUserId", CoUserID);
                if (tab.getPosition() == 0) {
                    p.putValue("tabType", "Audio Tab");
                    SegmentAudio e = new SegmentAudio();
                    ArrayList<SegmentAudio> section = new ArrayList<>();
                    for (int i = 0; i < audioDownloadList.size(); i++) {
                        e.setAudioId(audioDownloadList.get(i).getID());
                        e.setAudioName(audioDownloadList.get(i).getName());
                        e.setMasterCategory(audioDownloadList.get(i).getAudiomastercat());
                        e.setSubCategory(audioDownloadList.get(i).getAudioSubCategory());
                        e.setAudioDuration(audioDownloadList.get(i).getAudioDirection());
                        section.add(e);
                    }
                    Gson gson = new Gson();
                    p.putValue("audios", gson.toJson(section));
                    BWSApplication.addToSegment("My Download Screen Viewed", p, CONSTANTS.screen);
                } else if (tab.getPosition() == 1) {
                    p.putValue("tabType", "Playlist Tab");
                    ArrayList<SegmentPlaylist> section1 = new ArrayList<>();
                    SegmentPlaylist e = new SegmentPlaylist();
                    for (int i = 0; i < playlistList.size(); i++) {
                        e.setPlaylistId(playlistList.get(i).getPlaylistID());
                        e.setPlaylistName(playlistList.get(i).getPlaylistName());
                        if (playlistList.get(i).getCreated().equalsIgnoreCase("1")) {
                            e.setPlaylistType("Created");
                        } else {
                            e.setPlaylistType("Default");
                        }
                        e.setPlaylistDuration(playlistList.get(i).getTotalDuration());
                        e.setAudioCount(playlistList.get(i).getTotalAudio());
                        section1.add(e);
                    }
                    Gson gson = new Gson();
                    p.putValue("playlists", gson.toJson(section1));
                    BWSApplication.addToSegment("My Download Screen Viewed", p, CONSTANTS.screen);
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

    public void prepareData1() {
        comefromDownload = "1";
        callMembershipMediaPlayer();
    }

    private void callMembershipMediaPlayer() {
//        try {
//            GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
//            globalInitExoPlayer.UpdateMiniPlayer(ctx);
//            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
//            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
//            if (!AudioFlag.equalsIgnoreCase("0")) {
//                Fragment fragment = new MiniPlayerFragment();
//                FragmentManager fragmentManager1 = getSupportFragmentManager();
//                fragmentManager1.beginTransaction()
//                        .add(R.id.flContainer, fragment)
//                        .commit();
//                comefromDownload = "1";
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
      /*  try {
            SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            AudioFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
            SharedPreferences shared2 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
            String UnlockAudioLists = shared2.getString(CONSTANTS.PREF_KEY_UnLockAudiList, "");
            Gson gson1 = new Gson();
            Type type1 = new TypeToken<List<String>>() {
            }.getType();
            List<String> UnlockAudioList = gson1.fromJson(UnlockAudioLists, type1);
            if (!IsLock.equalsIgnoreCase("0") && (AudioFlag.equalsIgnoreCase("MainAudioList")
                    || AudioFlag.equalsIgnoreCase("ViewAllAudioList"))) {
                String audioID = "";
                SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String json = shared.getString(CONSTANTS.PREF_KEY_PlayerAudioList, String.valueOf(gson));
                Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                }.getType();
                ArrayList<MainPlayModel> arrayList = gson.fromJson(json, type);

                if (arrayList.get(0).getAudioFile().equalsIgnoreCase("")) {
                    arrayList.remove(0);
                }
                audioID = arrayList.get(0).getID();

                if (UnlockAudioList.contains(audioID)) {
                } else {
                    SharedPreferences sharedm = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorr = sharedm.edit();
                    editorr.remove(CONSTANTS.PREF_KEY_MainAudioList);
                    editorr.remove(CONSTANTS.PREF_KEY_PlayerAudioList);
                    editorr.remove(CONSTANTS.PREF_KEY_PlayerPosition);
                    editorr.remove(CONSTANTS.PREF_KEY_queuePlay);
                    editorr.remove(CONSTANTS.PREF_KEY_audioPlay);
                    editorr.remove(CONSTANTS.PREF_KEY_AudioPlayerFlag);
                    editorr.remove(CONSTANTS.PREF_KEY_PlaylistId);
                    editorr.remove(CONSTANTS.PREF_KEY_myPlaylist);
                    editorr.clear();
                    editorr.commit();

                    callNewPlayerRelease();
                }

            } else if (!IsLock.equalsIgnoreCase("0") && !AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
                SharedPreferences sharedm = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                SharedPreferences.Editor editorr = sharedm.edit();
                editorr.remove(CONSTANTS.PREF_KEY_MainAudioList);
                editorr.remove(CONSTANTS.PREF_KEY_PlayerAudioList);
                editorr.remove(CONSTANTS.PREF_KEY_PlayerPosition);
                editorr.remove(CONSTANTS.PREF_KEY_queuePlay);
                editorr.remove(CONSTANTS.PREF_KEY_audioPlay);
                editorr.remove(CONSTANTS.PREF_KEY_AudioPlayerFlag);
                editorr.remove(CONSTANTS.PREF_KEY_PlaylistId);
                editorr.remove(CONSTANTS.PREF_KEY_myPlaylist);
                editorr.clear();
                editorr.commit();

                callNewPlayerRelease();
            }
            SharedPreferences shared22 = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            AudioFlag = shared22.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
            if (!AudioFlag.equalsIgnoreCase("0")) {
                comefromDownload = "1";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    protected void onResume() {
        prepareData1();
        if (comeDeletePlaylist == 1) {
            prepareData1();
            comeDeletePlaylist = 0;
        }
        NetWatch.builder(this)
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
        DB.taskDao().geAllDataz("",CoUserID).observe(this, audioList -> {
            if (audioList != null) {
                if (audioList.size() != 0) {
                    List<DownloadAudioDetails> audioList1 = new ArrayList<>();
                    for (int i = 0; i < audioList.size(); i++) {
                        DownloadAudioDetails dad = new DownloadAudioDetails();
                        dad.setID(audioList.get(i).getID());
                        dad.setName(audioList.get(i).getName());
                        dad.setAudioFile(audioList.get(i).getAudioFile());
                        dad.setAudioDirection(audioList.get(i).getAudioDirection());
                        dad.setAudiomastercat(audioList.get(i).getAudiomastercat());
                        dad.setAudioSubCategory(audioList.get(i).getAudioSubCategory());
                        dad.setImageFile(audioList.get(i).getImageFile());
                        dad.setAudioDuration(audioList.get(i).getAudioDuration());
                        dad.setPlaylistId(audioList.get(i).getPlaylistId());
                        dad.setIsSingle(audioList.get(i).getIsSingle());
                        dad.setIsDownload(audioList.get(i).getIsDownload());
                        dad.setDownloadProgress(audioList.get(i).getDownloadProgress());
                        audioList1.add(dad);
                        audioDownloadList = audioList1;
                    }
                } else {
                }
            } else {
            }
            DB.taskDao().geAllDataz("",CoUserID).removeObserver(audioListx -> {
            });
        });
        DB.taskDao()
                .getAllPlaylist1(CoUserID).observe(this, audioList -> {

            if (audioList != null) {
                if (audioList.size() != 0) {
                    List<DownloadPlaylistDetails> audioList1 = new ArrayList<>();
                    for (int i = 0; i < audioList.size(); i++) {
                        DownloadPlaylistDetails detail = new DownloadPlaylistDetails();
                        detail.setPlaylistID(audioList.get(i).getPlaylistID());
                        detail.setPlaylistName(audioList.get(i).getPlaylistName());
                        detail.setPlaylistDesc(audioList.get(i).getPlaylistDesc());
                        detail.setIsReminder(audioList.get(i).getPlaylistDesc());
                        detail.setPlaylistMastercat(audioList.get(i).getPlaylistMastercat());
                        detail.setPlaylistSubcat(audioList.get(i).getPlaylistSubcat());
                        detail.setPlaylistImage(audioList.get(i).getPlaylistImage());
                        detail.setPlaylistImageDetails(audioList.get(i).getPlaylistImageDetails());
                        detail.setTotalAudio(audioList.get(i).getTotalAudio());
                        detail.setTotalDuration(audioList.get(i).getTotalDuration());
                        detail.setTotalhour(audioList.get(i).getTotalhour());
                        detail.setTotalminute(audioList.get(i).getTotalminute());
                        detail.setCreated(audioList.get(i).getCreated());
                        audioList1.add(detail);
                        playlistList = audioList1;
                    }
                } else {
                }
            } else {
            }
        });
        super.onResume();
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
        String UserID;
        FrameLayout progressBarHolder;
        ProgressBar ImgV;
        private Context myContext;

        public TabAdapter(FragmentManager fm, Context myContext, int totalTabs, String UserID, FrameLayout progressBarHolder, ProgressBar ImgV) {
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
                    bundle.putString("UserID", UserID);
                    bundle.putString("IsLock", IsLock);
//                    bundle.putParcelableArrayList("audioDownloadsFragment", audioList);
                    audioDownloadsFragment.setArguments(bundle);
                    return audioDownloadsFragment;
                case 1:
                    bundle = new Bundle();
                    PlaylistsDownlaodsFragment playlistsDownlaodsFragment = new PlaylistsDownlaodsFragment();
                    bundle.putString("UserID", UserID);
                    bundle.putString("IsLock", IsLock);
//                    bundle.putParcelableArrayList("playlistsDownlaodsFragment", playlistList);
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

    class AppLifecycleCallback implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (numStarted == 0) {
                stackStatus = 1;
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


