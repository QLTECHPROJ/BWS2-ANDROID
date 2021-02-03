package com.brainwellnessspa.LikeModule.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment;
import com.brainwellnessspa.LikeModule.Fragments.LikeAudiosFragment;
import com.brainwellnessspa.LikeModule.Fragments.LikePlaylistsFragment;
import com.brainwellnessspa.LikeModule.Models.LikesHistoryModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Services.GlobalInitExoPlayer;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivityLikeBinding;
import com.google.android.material.tabs.TabLayout;

import retrofit2.Callback;

import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.ComeScreenAccount;

import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;

public class LikeActivity extends AppCompatActivity {
    public static boolean ComeFrom_LikePlaylist = false;
    public static int RefreshLikePlaylist = 0;
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
        ComeScreenAccount = 0;
        comefromDownload = "1";
        SharedPreferences shared2 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared2.getString(CONSTANTS.PREF_KEY_UserID, ""));
        binding.llBack.setOnClickListener(view -> {
            comefromDownload = "0";
            ComeScreenAccount = 1;
            finish();
        });
        prepareData();
    }

    @Override
    protected void onResume() {
        ComeScreenAccount = 0;
        comefromDownload = "1";
        callMembershipMediaPlayer();
        if (BWSApplication.isNetworkConnected(ctx)) {
            Call<LikesHistoryModel> listCall = APIClient.getClient().getLikeAudioPlaylistListing(UserID);
            listCall.enqueue(new Callback<LikesHistoryModel>() {
                @Override
                public void onResponse(Call<LikesHistoryModel> call, Response<LikesHistoryModel> response) {
                    try {
                        LikesHistoryModel listModel = response.body();
                        if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                            List<LikesHistoryModel.ResponseData.Audio> listDataModel = listModel.getResponseData().getAudio();
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
                           /* p = new Properties();
                            p.putValue("userId", UserID);
                            p.putValue("id", appointmentDetailModel.getResponseData().getId());
                            p.putValue("name", appointmentDetailModel.getResponseData().getName());
                            p.putValue("desc", appointmentDetailModel.getResponseData().getDesc());
                            p.putValue("status", appointmentDetailModel.getResponseData().getStatus());
                            p.putValue("facilitator", appointmentDetailModel.getResponseData().getFacilitator());
                            p.putValue("userName", appointmentDetailModel.getResponseData().getUserName());
                            p.putValue("date", appointmentDetailModel.getResponseData().getDate());
                            p.putValue("time", appointmentDetailModel.getResponseData().getTime());
                            p.putValue("bookUrl", appointmentDetailModel.getResponseData().getBookUrl());
                            p.putValue("booklet", appointmentDetailModel.getResponseData().getBooklet());
                            p.putValue("myAnswers", appointmentDetailModel.getResponseData().getMyAnswers());

                            for (int i = 0; i < appointmentDetailModel.getResponseData().getAudio().size(); i++) {
                                section.add(appointmentDetailModel.getResponseData().getAudio().get(i).getID());
                                section.add(appointmentDetailModel.getResponseData().getAudio().get(i).getName());
                                section.add(appointmentDetailModel.getResponseData().getAudio().get(i).getAudiomastercat());
                                section.add(appointmentDetailModel.getResponseData().getAudio().get(i).getAudioSubCategory());
                                section.add(appointmentDetailModel.getResponseData().getAudio().get(i).getAudioDuration());
                            }
                            p.putValue("sessionAudios", gson.toJson(section));
                            BWSApplication.addToSegment("Appointment Session Details Viewed", p, CONSTANTS.screen);*/
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

    public void prepareData() {
        callMembershipMediaPlayer();
        binding.viewPager.setOffscreenPageLimit(2);
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Audios"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Playlists"));
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        TabAdapter adapter = new TabAdapter(getSupportFragmentManager(), ctx, binding.tabLayout.getTabCount());
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
        finish();
    }

    public class TabAdapter extends FragmentStatePagerAdapter {
        int totalTabs;
        Callback<LikesHistoryModel> likesHistoryModelCallback;
        private Context myContext;

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