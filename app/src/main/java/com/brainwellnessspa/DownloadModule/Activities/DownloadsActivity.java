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

import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.google.android.material.tabs.TabLayout;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment;
import com.brainwellnessspa.DownloadModule.Fragments.PlaylistsDownlaodsFragment;
import com.brainwellnessspa.DownloadModule.Models.DownloadlistModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivityDownloadsBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.brainwellnessspa.DashboardModule.Audio.AudioFragment.IsLock;
import static com.brainwellnessspa.DownloadModule.Adapters.AudioDownlaodsAdapter.comefromDownload;
import static com.brainwellnessspa.Utility.MusicService.isMediaStart;
import static com.brainwellnessspa.Utility.MusicService.releasePlayer;
import static com.brainwellnessspa.Utility.MusicService.stopMedia;
import static com.brainwellnessspa.DownloadModule.Activities.DownloadPlaylistActivity.comeDeletePlaylist;
import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.ComeScreenAccount;

public class DownloadsActivity extends AppCompatActivity {
    ActivityDownloadsBinding binding;
    ArrayList<DownloadlistModel.Audio> audioList;
    ArrayList<DownloadlistModel.Playlist> playlistList;
    String UserID, AudioFlag;
    public static boolean ComeFrom_Playlist = false;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_downloads);
        ctx = DownloadsActivity.this;
        ComeScreenAccount = 0;
        SharedPreferences shared2 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared2.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");

        binding.llBack.setOnClickListener(view -> {
            comefromDownload = "0";
            ComeScreenAccount = 1;
            finish();
        });
        prepareData();
    }

    @Override
    public void onBackPressed() {
        comefromDownload = "0";
        ComeScreenAccount = 1;
        finish();
    }

    public void prepareData() {
        callMembershipMediaPlayer();
/*        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<DownloadlistModel> listCall = APIClient.getClient().getDownloadlistPlaylist(UserID);
            listCall.enqueue(new Callback<DownloadlistModel>() {
                @Override
                public void onResponse(Call<DownloadlistModel> call, Response<DownloadlistModel> response) {
                    if (response.isSuccessful()) {*/
//                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);

        audioList = new ArrayList<>();
        playlistList = new ArrayList<>();


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
        callMembershipMediaPlayer();
    }

    private void callMembershipMediaPlayer() {
        try {
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
                SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
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
                    if (isMediaStart) {
                        stopMedia();
                        releasePlayer();
                    }
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
                if (isMediaStart) {
                    stopMedia();
                    releasePlayer();
                }
            }
            SharedPreferences shared22 = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            AudioFlag = shared22.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            if (!AudioFlag.equalsIgnoreCase("0")) {
                comefromDownload = "1";
                Fragment fragment = new TransparentPlayerFragment();
                FragmentManager fragmentManager1 = getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .add(R.id.flContainer, fragment)
                        .commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
//                    bundle.putParcelableArrayList("audioDownloadsFragment", audioList);
                    audioDownloadsFragment.setArguments(bundle);
                    return audioDownloadsFragment;
                case 1:
                    bundle = new Bundle();
                    PlaylistsDownlaodsFragment playlistsDownlaodsFragment = new PlaylistsDownlaodsFragment();
                    bundle.putString("UserID", UserID);
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

    @Override
    protected void onResume() {
        super.onResume();
        prepareData1();
        if (comeDeletePlaylist == 1) {
            prepareData1();
            comeDeletePlaylist = 0;
        }
    }
}


