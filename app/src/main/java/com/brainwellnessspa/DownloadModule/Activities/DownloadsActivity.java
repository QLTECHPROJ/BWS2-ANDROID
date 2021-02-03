package com.brainwellnessspa.DownloadModule.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.room.Room;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Models.SegmentAudio;
import com.brainwellnessspa.DashboardModule.Models.SegmentPlaylist;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment;
import com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment;
import com.brainwellnessspa.DownloadModule.Fragments.PlaylistsDownlaodsFragment;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.AudioDatabase;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.RoomDataBase.DownloadPlaylistDetails;
import com.brainwellnessspa.Services.GlobalInitExoPlayer;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivityDownloadsBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.segment.analytics.Properties;

import java.util.ArrayList;
import java.util.List;

import static com.brainwellnessspa.BWSApplication.MIGRATION_1_2;
import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.ComeScreenAccount;
import static com.brainwellnessspa.DashboardModule.Audio.AudioFragment.IsLock;
import static com.brainwellnessspa.DownloadModule.Activities.DownloadPlaylistActivity.comeDeletePlaylist;
import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;

public class DownloadsActivity extends AppCompatActivity {
    public static boolean ComeFrom_Playlist = false;
    ActivityDownloadsBinding binding;
    List<DownloadAudioDetails> audioDownloadList;
    List<DownloadPlaylistDetails> playlistList;
    String UserID, AudioFlag;
    Context ctx;
    Properties p;
    AudioDatabase DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_downloads);
        ctx = DownloadsActivity.this;
        ComeScreenAccount = 0;
        comefromDownload = "1";
        SharedPreferences shared2 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared2.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        DB = Room.databaseBuilder(ctx,
                AudioDatabase.class,
                "Audio_database")
                .addMigrations(MIGRATION_1_2)
                .build();
        p = new Properties();
        binding.llBack.setOnClickListener(view -> {
            ComeScreenAccount = 1;
            comefromDownload = "0";
            finish();
        });
        prepareData();
    }

    @Override
    public void onBackPressed() {
        ComeScreenAccount = 1;
        comefromDownload = "0";
        finish();
    }

    public void prepareData() {
        ComeScreenAccount = 0;
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
                p.putValue("userId", UserID);
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
        ComeScreenAccount = 0;
        comefromDownload = "1";
        callMembershipMediaPlayer();
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
      /*  try {
            SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            AudioFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            SharedPreferences shared2 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
            String UnlockAudioLists = shared2.getString(CONSTANTS.PREF_KEY_UnLockAudiList, "");
            Gson gson1 = new Gson();
            Type type1 = new TypeToken<List<String>>() {
            }.getType();
            List<String> UnlockAudioList = gson1.fromJson(UnlockAudioLists, type1);
            if (!IsLock.equalsIgnoreCase("0") && (AudioFlag.equalsIgnoreCase("MainAudioList")
                    || AudioFlag.equalsIgnoreCase("ViewAllAudioList"))) {
                String audioID = "";
                SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String json = shared.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gson));
                Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                }.getType();
                ArrayList<MainPlayModel> arrayList = gson.fromJson(json, type);

                if (arrayList.get(0).getAudioFile().equalsIgnoreCase("")) {
                    arrayList.remove(0);
                }
                audioID = arrayList.get(0).getID();

                if (UnlockAudioList.contains(audioID)) {
                } else {
                    SharedPreferences sharedm = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorr = sharedm.edit();
                    editorr.remove(CONSTANTS.PREF_KEY_modelList);
                    editorr.remove(CONSTANTS.PREF_KEY_audioList);
                    editorr.remove(CONSTANTS.PREF_KEY_position);
                    editorr.remove(CONSTANTS.PREF_KEY_queuePlay);
                    editorr.remove(CONSTANTS.PREF_KEY_audioPlay);
                    editorr.remove(CONSTANTS.PREF_KEY_AudioFlag);
                    editorr.remove(CONSTANTS.PREF_KEY_PlaylistId);
                    editorr.remove(CONSTANTS.PREF_KEY_myPlaylist);
                    editorr.clear();
                    editorr.commit();

                    callNewPlayerRelease();
                }

            } else if (!IsLock.equalsIgnoreCase("0") && !AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
                SharedPreferences sharedm = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editorr = sharedm.edit();
                editorr.remove(CONSTANTS.PREF_KEY_modelList);
                editorr.remove(CONSTANTS.PREF_KEY_audioList);
                editorr.remove(CONSTANTS.PREF_KEY_position);
                editorr.remove(CONSTANTS.PREF_KEY_queuePlay);
                editorr.remove(CONSTANTS.PREF_KEY_audioPlay);
                editorr.remove(CONSTANTS.PREF_KEY_AudioFlag);
                editorr.remove(CONSTANTS.PREF_KEY_PlaylistId);
                editorr.remove(CONSTANTS.PREF_KEY_myPlaylist);
                editorr.clear();
                editorr.commit();

                callNewPlayerRelease();
            }
            SharedPreferences shared22 = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            AudioFlag = shared22.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
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

        DB.taskDao().geAllDataz("").observe(this, audioList -> {
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
                        dad.setLike(audioList.get(i).getLike());
                        dad.setDownload(audioList.get(i).getDownload());
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
            DB.taskDao().geAllDataz("").removeObserver(audioListx -> {
            });
        });
        DatabaseClient
                .getInstance(ctx)
                .getaudioDatabase()
                .taskDao()
                .getAllPlaylist1().observe(this, audioList -> {

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
                        detail.setDownload(audioList.get(i).getDownload());
                        detail.setLike(audioList.get(i).getLike());
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
}


