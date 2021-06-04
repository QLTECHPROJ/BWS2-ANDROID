package com.brainwellnessspa.DownloadModule.Activities;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardOldModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.dashboardModule.models.HomeScreenModel;
import com.brainwellnessspa.dashboardModule.activities.MyPlayerActivity;
import com.brainwellnessspa.EncryptDecryptUtils.FileUtils;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.AudioDatabase;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.RoomDataBase.DownloadPlaylistDetails;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.ActivityDownloadPlaylistBinding;
import com.brainwellnessspa.databinding.DownloadPlaylistLayoutBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.downloader.PRDownloader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.segment.analytics.Properties;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ir.drax.netwatch.NetWatch;
import ir.drax.netwatch.cb.NetworkChangeReceiver_navigator;

import static com.brainwellnessspa.BWSApplication.PlayerAudioId;
import static com.brainwellnessspa.BWSApplication.appStatus;
import static com.brainwellnessspa.BWSApplication.DB;
import static com.brainwellnessspa.BWSApplication.getAudioDataBase;
import static com.brainwellnessspa.DashboardOldModule.Activities.DashboardActivity.audioClick;
import static com.brainwellnessspa.DashboardOldModule.Activities.DashboardActivity.miniPlayer;
import static com.brainwellnessspa.BWSApplication.isPlayPlaylist;
import static com.brainwellnessspa.DashboardOldModule.TransparentPlayer.Fragments.MiniPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.downloadIdOne;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.filename;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.callNewPlayerRelease;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.callResumePlayer;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.notificationId;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.player;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.relesePlayer;

public class DownloadPlaylistActivity extends AppCompatActivity implements NetworkChangeReceiver_navigator {
    //    Handler handler3;
    public static int comeDeletePlaylist = 0;
    public AudioManager audioManager;
    Activity activity;
    public int hundredVolume = 0, currentVolume = 0, maxVolume = 0, percent;
    ActivityDownloadPlaylistBinding binding;
    PlayListsAdpater adpater;
    String IsPlayDisclimer, PlaylistDescription = "", Created = "", CoUserID, UserID, SearchFlag = "", AudioPlayerFlag = "",
            PlaylistID = "", PlaylistName = "", PlaylistImage = "", TotalAudio = "", Totalhour = "", Totalminute = "",
            PlaylistImageDetails = "", SLEEPTIME = "";
    EditText searchEditText;
    List<String> downloadAudioDetailsList = new ArrayList<>();
    Context ctx;
    DownloadAudioDetails addDisclaimer = new DownloadAudioDetails();
    int startTime;
    Activity act;
    long myProgress = 0, diff = 0, currentDuration = 0;
    int stackStatus = 0;
    boolean myBackPress = false;
    private List<DownloadPlaylistDetails> listModelList;
    private int numStarted = 0;
    //    private Runnable UpdateSongTime3;
    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("MyData")) {
                String data = intent.getStringExtra("MyData");
                Log.d("play_pause_Action", data);
                SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE);
                String AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
                String MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
                String MyPlaylistName = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "");
                String PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "");
                int PlayerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                if (AudioPlayerFlag.equalsIgnoreCase("Downloadlist") && MyPlaylist.equalsIgnoreCase(PlaylistID)) {
                        /*if (data.equalsIgnoreCase("pause")) {
                            isPlayPlaylist = 1;
                            binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_icon));
//                            handler3.postDelayed(UpdateSongTime3, 500);
                            adpater2.notifyDataSetChanged();

                        } else {
                            isPlayPlaylist = 0;
                            binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_blue_play_icon));
                        }*/
                    if (data.equalsIgnoreCase("play")) {
                        isPlayPlaylist = 1;
                        binding.llPause.setVisibility(View.VISIBLE);
                        binding.llPlay.setVisibility(View.GONE);
                    } else {
                        isPlayPlaylist = 2;
                        binding.llPause.setVisibility(View.GONE);
                        binding.llPlay.setVisibility(View.VISIBLE);
                    }
                    if (player != null) {
                        adpater.notifyDataSetChanged();
                    }

                } else {
                    isPlayPlaylist = 0;
                    binding.llPause.setVisibility(View.GONE);
                    binding.llPlay.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_download_playlist);
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, AppCompatActivity.MODE_PRIVATE);
        UserID = shared.getString(CONSTANTS.PREFE_ACCESS_UserID, "");
        CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "");
        SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE);
        SLEEPTIME = shared1.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "");

        ctx = DownloadPlaylistActivity.this;
        act = DownloadPlaylistActivity.this;
        activity = DownloadPlaylistActivity.this;
        addDisclaimer();
        binding.tvSleepTime.setText("Your average sleep time is \n" + SLEEPTIME);
        DB = getAudioDataBase(ctx);
        if (getIntent() != null) {
            PlaylistID = getIntent().getStringExtra("PlaylistID");
            PlaylistName = getIntent().getStringExtra("PlaylistName");
            PlaylistImage = getIntent().getStringExtra("PlaylistImage");
            PlaylistImageDetails = getIntent().getStringExtra("PlaylistImageDetails");
            TotalAudio = getIntent().getStringExtra("TotalAudio");
            Totalhour = getIntent().getStringExtra("Totalhour");
            Totalminute = getIntent().getStringExtra("Totalminute");
            Created = getIntent().getStringExtra("Created");
            PlaylistDescription = getIntent().getStringExtra("PlaylistDescription");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(new AppLifecycleCallback());
        }

        audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        percent = 100;
        hundredVolume = (int) (currentVolume * percent) / maxVolume;

        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                5, 4.1f, 1f, 0);
        binding.ivBanner.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivBanner.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivBanner.setScaleType(ImageView.ScaleType.FIT_XY);

        MeasureRatio measureRatio1 = BWSApplication.measureRatio(ctx, 0,
                5, 4.1f, 1f, 0);
        binding.ivTransBanner.getLayoutParams().height = (int) (measureRatio1.getHeight() * measureRatio1.getRatio());
        binding.ivTransBanner.getLayoutParams().width = (int) (measureRatio1.getWidthImg() * measureRatio1.getRatio());
        binding.ivTransBanner.setScaleType(ImageView.ScaleType.FIT_XY);

        MeasureRatio measureRatio2 = BWSApplication.measureRatio(ctx, 0,
                5, 4.1f, 1f, 0);
        binding.llPlayer.getLayoutParams().height = (int) (measureRatio2.getHeight() * measureRatio2.getRatio());
        binding.llPlayer.getLayoutParams().width = (int) (measureRatio2.getWidthImg() * measureRatio2.getRatio());
        if (BWSApplication.isNetworkConnected(ctx)) {
            if (!PlaylistImageDetails.equalsIgnoreCase("")) {
                try {
                    Glide.with(ctx).load(PlaylistImageDetails).thumbnail(0.05f)
                            .placeholder(R.drawable.audio_bg)
                            .error(R.drawable.audio_bg)
                            .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivBanner);
                    binding.ivTransBanner.setImageResource(R.drawable.rounded_light_app_theme);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                binding.ivCloudBanner.setBackground(getResources().getDrawable(R.drawable.ic_cloud_bg));
                binding.ivTransBanner.setImageResource(R.drawable.rounded_dark_app_theme);
            }
        } else {
            binding.ivCloudBanner.setBackground(getResources().getDrawable(R.drawable.ic_cloud_bg));
            binding.ivTransBanner.setImageResource(R.drawable.rounded_dark_app_theme);
        }

        Properties p = new Properties();
        p.putValue("userId", UserID);
        p.putValue("playlistId", PlaylistID);
        p.putValue("playlistName", PlaylistName);
        p.putValue("playlistDescription", PlaylistDescription);
        if (Created.equalsIgnoreCase("1")) {
            p.putValue("playlistType", "Created");
        } else if (Created.equalsIgnoreCase("0")) {
            p.putValue("playlistType", "Default");
        }
        if (Totalhour.equalsIgnoreCase("")) {
            p.putValue("playlistDuration", "0h " + Totalminute + "m");
        } else if (Totalminute.equalsIgnoreCase("")) {
            p.putValue("playlistDuration", Totalhour + "h 0m");
        } else {
            p.putValue("playlistDuration", Totalhour + "h " + Totalminute + "m");
        }
        p.putValue("audioCount", TotalAudio);
        p.putValue("source", "Downloaded Playlists");
        BWSApplication.addToSegment("Playlist Viewed", p, CONSTANTS.screen);

        binding.llBack.setOnClickListener(view -> {
            LocalBroadcastManager.getInstance(ctx).unregisterReceiver(listener);
            myBackPress = true;
            finish();
        });
        PrepareData();
    }

    @Override
    public void onPause() {
//        handler3.removeCallbacks(UpdateSongTime3);
        LocalBroadcastManager.getInstance(ctx).unregisterReceiver(listener);
        super.onPause();
    }

    @Override
    protected void onResume() {
        Gson gson = new Gson();
        SharedPreferences shared1x = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE);
        String AudioPlayerFlagx = shared1x.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
        int PlayerPositionx = shared1x.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
        String json = shared1x.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toString());
        ArrayList<MainPlayModel> mainPlayModelList = new ArrayList<>();
        if (!AudioPlayerFlagx.equals("0")) {
            if (!json.equalsIgnoreCase(String.valueOf(gson))) {
                Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                }.getType();
                mainPlayModelList = gson.fromJson(json, type);
            }
            PlayerAudioId = mainPlayModelList.get(PlayerPositionx).getID();
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
        PrepareData();
        super.onResume();
    }

    public void PrepareData() {
        SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE);
        String AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
        String MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
        String MyPlaylistName = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "");
        String PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "");
        int PlayerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
        if (AudioPlayerFlag.equalsIgnoreCase("Downloadlist") && MyPlaylist.equalsIgnoreCase(PlaylistID)) {
            if (player != null) {
                if (player.getPlayWhenReady()) {
                    isPlayPlaylist = 1;
//                    handler3.postDelayed(UpdateSongTime3, 500);

                    binding.llPause.setVisibility(View.VISIBLE);
                    binding.llPlay.setVisibility(View.GONE);
                } else {
                    isPlayPlaylist = 2;
//                    handler3.postDelayed(UpdateSongTime3, 500);

                    binding.llPause.setVisibility(View.GONE);
                    binding.llPlay.setVisibility(View.VISIBLE);
                }
            } else {
                isPlayPlaylist = 0;

                binding.llPause.setVisibility(View.GONE);
                binding.llPlay.setVisibility(View.VISIBLE);
            }
        } else {
            isPlayPlaylist = 0;

            binding.llPause.setVisibility(View.GONE);
            binding.llPlay.setVisibility(View.VISIBLE);
        }
        binding.tvPlayListName.setText(PlaylistName);

        binding.searchView.onActionViewExpanded();
        searchEditText = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.dark_blue_gray));
        searchEditText.setHintTextColor(getResources().getColor(R.color.gray));
        ImageView closeButton = binding.searchView.findViewById(R.id.search_close_btn);
        binding.searchView.clearFocus();
        searchEditText.setHint("Search for audio");
        closeButton.setOnClickListener(v -> {
            binding.searchView.clearFocus();
            searchEditText.setText("");
            binding.searchView.setQuery("", false);
        });

        binding.llDelete.setOnClickListener(view -> {
            SharedPreferences shared11 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE);
            String AudioPlayerFlag1 = shared11.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
            String MyPlaylist1 = shared11.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
            String MyPlaylistName1 = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "");
            String PlayFrom1 = shared11.getString(CONSTANTS.PREF_KEY_PlayFrom, "");
            int PlayerPosition1 = shared11.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
            if (AudioPlayerFlag1.equalsIgnoreCase("Downloadlist") && MyPlaylist1.equalsIgnoreCase(PlaylistID)) {
                BWSApplication.showToast("Currently this playlist is in player,so you can't delete this playlist as of now", activity);
            } else {
                final Dialog dialog = new Dialog(ctx);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_popup_layout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ctx.getResources().getColor(R.color.dark_blue_gray)));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                final TextView tvGoBack = dialog.findViewById(R.id.tvGoBack);
                final TextView tvHeader = dialog.findViewById(R.id.tvHeader);
                final TextView tvTitle = dialog.findViewById(R.id.tvTitle);
                final Button Btn = dialog.findViewById(R.id.Btn);
                tvTitle.setText("Remove playlist");
                tvHeader.setText("Are you sure you want to remove the " + PlaylistName + " from downloads??");
                Btn.setText("Confirm");
                dialog.setOnKeyListener((v, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                    }
                    return false;
                });

                Btn.setOnClickListener(v -> {
                    getDownloadData();
                    GetPlaylistMedia(PlaylistID);
                    finish();
                    comeDeletePlaylist = 1;
                    dialog.dismiss();
                });
                tvGoBack.setOnClickListener(v -> dialog.dismiss());
                dialog.show();
                dialog.setCancelable(false);

            }
        });

      /*  if (TotalAudio.equalsIgnoreCase("") || TotalAudio.equalsIgnoreCase("0") &&
                Totalhour.equalsIgnoreCase("") && Totalminute.equalsIgnoreCase("")) {
            binding.tvLibraryDetail.setText("0 Audio | 0h 0m");
        } else {
            if (Totalminute.equalsIgnoreCase("")) {
                binding.tvLibraryDetail.setText(TotalAudio + " Audio | " + Totalhour + "h 0m");
            } else {
                binding.tvLibraryDetail.setText(TotalAudio + " Audio | " + Totalhour + "h " + Totalminute + "m");
            }
        }
*/
        RecyclerView.LayoutManager playList = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        binding.rvPlayLists.setLayoutManager(playList);
        binding.rvPlayLists.setItemAnimator(new DefaultItemAnimator());
        getMedia(PlaylistID);
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String search) {
                binding.searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String search) {
                try {
                    if (adpater != null) {
                        adpater.getFilter().filter(search);
                        SearchFlag = search;
                        Log.e("searchsearch", "" + search);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        binding.tvTag.setVisibility(View.VISIBLE);
        binding.tvTag.setText("Audios in Playlist");
//        binding.tvPlaylist.setText("Playlist");
    }

    private void getDownloadData() {
        List<String> fileNameList, fileNameList1, audioFile, playlistDownloadId;
        try {
            SharedPreferences sharedy = getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
            Gson gson = new Gson();
            String jsony = sharedy.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson));
            String json1 = sharedy.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson));
            String jsonq = sharedy.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson));
            if (!jsony.equalsIgnoreCase(String.valueOf(gson))) {
                Type type = new TypeToken<List<String>>() {
                }.getType();
                fileNameList = gson.fromJson(jsony, type);
                fileNameList1 = gson.fromJson(jsony, type);
                audioFile = gson.fromJson(json1, type);
                playlistDownloadId = gson.fromJson(jsonq, type);

                if (playlistDownloadId.size() != 0) {
                    if (playlistDownloadId.contains(PlaylistID)) {
                        Log.e("cancel", String.valueOf(playlistDownloadId.size()));
                        for (int i = 1; i < fileNameList1.size() - 1; i++) {
                            if (playlistDownloadId.get(i).equalsIgnoreCase(PlaylistID)) {
                                Log.e("cancel name id", "My id " + i + fileNameList1.get(i));
                                fileNameList.remove(i);
                                audioFile.remove(i);
                                playlistDownloadId.remove(i);
                                Log.e("cancel id", "My id " + playlistDownloadId.size() + i);
                            }
                        }

                        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = shared.edit();
                        String nameJson = gson.toJson(fileNameList);
                        String urlJson = gson.toJson(audioFile);
                        String playlistIdJson = gson.toJson(playlistDownloadId);
                        editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson);
                        editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson);
                        editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
                        editor.commit();
                        if (playlistDownloadId.get(0).equalsIgnoreCase(PlaylistID)) {
                            PRDownloader.cancel(downloadIdOne);
                            filename = "";
                        }
                    }
                }
            }
        } catch (Exception e) {
//            getDownloadData();
            e.printStackTrace();
            Log.e("Download Playlist ", "Download Playlist remove issue" + e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        LocalBroadcastManager.getInstance(ctx).unregisterReceiver(listener);
        myBackPress = true;
        finish();
    }

    private void getMedia(String playlistID) {
        DB.taskDao().getAllAudioByPlaylist1(PlaylistID, CoUserID).observe(this, audioList -> {
            adpater = new PlayListsAdpater(audioList, ctx);
            LocalBroadcastManager.getInstance(ctx).registerReceiver(listener, new IntentFilter("play_pause_Action"));
            binding.rvPlayLists.setAdapter(adpater);
        });
    }

    public void GetPlaylistMedia(String playlistID) {
        DB.taskDao().getAllAudioByPlaylist1(PlaylistID, CoUserID).observe(this, audioList -> {
            deleteDownloadFile(getApplicationContext(), playlistID);
            if (audioList.size() != 0) {
                GetSingleMedia(audioList.get(0).getAudioFile(), ctx.getApplicationContext(), playlistID, audioList, 0);
            }
        });
    }

    private void deleteDownloadFile(Context applicationContext, String PlaylistId) {

        AudioDatabase.databaseWriteExecutor.execute(() -> DB.taskDao().deleteByPlaylistId(PlaylistId, CoUserID));
        deletePlaylist(PlaylistID);

    }

    public void GetSingleMedia(String AudioFile, Context ctx, String playlistID, List<DownloadAudioDetails> audioList, int i) {
        DB.taskDao().getLastIdByuId1(AudioFile, CoUserID).observe(this, audioList1 -> {
            try {
                if (audioList1.size() != 0) {
                    if (audioList1.size() == 1) {
                        FileUtils.deleteDownloadedFile(ctx, audioList1.get(0).getName());
                    }
                }

                if (i < audioList.size() - 1) {
                    GetSingleMedia(audioList.get(i + 1).getAudioFile(), ctx.getApplicationContext(), playlistID, audioList, i + 1);
                    Log.e("DownloadMedia Call", String.valueOf(i + 1));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    private void deletePlaylist(String playlistId) {
        AudioDatabase.databaseWriteExecutor.execute(() -> DB.taskDao().deletePlaylist(playlistId, CoUserID));
    }

    private void addDisclaimer() {
        addDisclaimer = new DownloadAudioDetails();
        Gson gson = new Gson();
        SharedPreferences shared12 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE);
        String IsPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1");
        String DisclimerJson = shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString());
        Type type = new TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio>() {
        }.getType();
        HomeScreenModel.ResponseData.DisclaimerAudio arrayList = gson.fromJson(DisclimerJson, type);
        addDisclaimer.setID(arrayList.getId());
        addDisclaimer.setName(arrayList.getName());
        addDisclaimer.setAudioFile(arrayList.getAudioFile());
        addDisclaimer.setAudioDirection(arrayList.getAudioDirection());
        addDisclaimer.setAudiomastercat(arrayList.getAudiomastercat());
        addDisclaimer.setAudioSubCategory(arrayList.getAudioSubCategory());
        addDisclaimer.setImageFile(arrayList.getImageFile());
        addDisclaimer.setAudioDuration(arrayList.getAudioDuration());
    }

    private void callTransparentFrag(int position, Context ctx, List<DownloadAudioDetails> listModelList, String s, String playlistID, boolean audioc) {
        miniPlayer = 1;
        audioClick = audioc;
        if (audioc) {
            callNewPlayerRelease();
        }
        SharedPreferences sharedd = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedd.edit();
        Gson gson = new Gson();
        String json11 = gson.toJson(listModelList);
        editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json11);
        editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, PlaylistID);
        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, PlaylistName);
        editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "");
        editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "Downloadlist");
        editor.commit();
        callAddTranFrag();
    }

    private void callAddTranFrag() {
        try {
            Intent i = new Intent(ctx, MyPlayerActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            overridePendingTransition(0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SegmentTag() {
        Properties p = new Properties();
        p.putValue("userId", UserID);
        p.putValue("playlistId", PlaylistID);
        p.putValue("playlistName", PlaylistName);
        p.putValue("playlistDescription", PlaylistDescription);
        if (Created.equalsIgnoreCase("1")) {
            p.putValue("playlistType", "Created");
        } else if (Created.equalsIgnoreCase("0")) {
            p.putValue("playlistType", "Default");
        }

        if (Totalhour.equalsIgnoreCase("")) {
            p.putValue("playlistDuration", "0h " + Totalminute + "m");
        } else if (Totalminute.equalsIgnoreCase("")) {
            p.putValue("playlistDuration", Totalhour + "h 0m");
        } else {
            p.putValue("playlistDuration", Totalhour + "h " + Totalminute + "m");
        }
        p.putValue("audioCount", TotalAudio);
        p.putValue("source", "Downloaded Playlists");
        p.putValue("playerType", "Mini");
        p.putValue("audioService", appStatus(ctx));
        p.putValue("sound", String.valueOf(hundredVolume));
        BWSApplication.addToSegment("Playlist Started", p, CONSTANTS.track);
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

    public class PlayListsAdpater extends RecyclerView.Adapter<PlayListsAdpater.MyViewHolders> implements Filterable {
        Context ctx;
        String UserID, songId;
        int ps = 0, nps = 0;
        private List<DownloadAudioDetails> listModelList;
        private List<DownloadAudioDetails> listFilterData;

        public PlayListsAdpater(List<DownloadAudioDetails> listModelList, Context ctx) {
            this.listModelList = listModelList;
            this.listFilterData = listModelList;
            this.ctx = ctx;
        }

        @NonNull
        @Override
        public MyViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            DownloadPlaylistLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.download_playlist_layout, parent, false);
            return new MyViewHolders(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolders holder, int position) {
            final List<DownloadAudioDetails> mData = listFilterData;
            holder.binding.tvTitleA.setText(mData.get(position).getName());
            holder.binding.tvTimeA.setText(mData.get(position).getAudioDuration());
            String id = mData.get(position).getID();
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.binding.ivBackgroundImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(R.drawable.ic_image_bg).thumbnail(0.05f)
                    .placeholder(R.drawable.ic_image_bg)
                    .error(R.drawable.ic_image_bg)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivBackgroundImage);
            if (BWSApplication.isNetworkConnected(ctx)) {
                Glide.with(ctx).load(mData.get(position).getImageFile()).thumbnail(0.05f)
                        .placeholder(R.drawable.ic_music_icon)
                        .error(R.drawable.ic_music_icon)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(28))).priority(Priority.HIGH)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            } else {
                Glide.with(ctx).load(R.drawable.ic_music_icon).thumbnail(0.05f)
                        .placeholder(R.drawable.ic_music_icon)
                        .error(R.drawable.ic_music_icon)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(28))).priority(Priority.HIGH)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            }

            SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE);
            AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
            String MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
            String MyPlaylistName = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "");
            String PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "");
            int PlayerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
            if (AudioPlayerFlag.equalsIgnoreCase("Downloadlist") && MyPlaylist.equalsIgnoreCase(PlaylistID)) {
                if (PlayerAudioId.equalsIgnoreCase(mData.get(position).getID())) {
                    if (player != null) {
                        if (!player.getPlayWhenReady()) {
                            holder.binding.equalizerview.pause();
                        } else {
                            holder.binding.equalizerview.resume(true);
                        }
                    } else
                        holder.binding.equalizerview.stop(true);
                    holder.binding.equalizerview.setVisibility(View.VISIBLE);
                    holder.binding.llMainLayout.setBackgroundResource(R.color.highlight_background);
                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                } else {
                    holder.binding.equalizerview.setVisibility(View.GONE);
                    holder.binding.llMainLayout.setBackgroundResource(R.color.white);
                    holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                }
//                    handler3.postDelayed(UpdateSongTime3,500);
            } else {
                holder.binding.equalizerview.setVisibility(View.GONE);
                holder.binding.llMainLayout.setBackgroundResource(R.color.white);
                holder.binding.ivBackgroundImage.setVisibility(View.GONE);
//                    handler3.removeCallbacks(UpdateSongTime3);
            }
            if (position == 0) {
                AudioDatabase.databaseWriteExecutor.execute(() -> {
                    downloadAudioDetailsList = DB.taskDao().geAllDataBYDownloaded("Complete", CoUserID);
                });
            }
            binding.llPlayPause.setOnClickListener(view -> {
                SharedPreferences sharedx1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                IsPlayDisclimer = (sharedx1.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1"));
                if (isPlayPlaylist == 1) {
                    if (player != null) {
                        player.setPlayWhenReady(false);
                    }
                    isPlayPlaylist = 2;

                    binding.llPause.setVisibility(View.GONE);
                    binding.llPlay.setVisibility(View.VISIBLE);
                } else if (isPlayPlaylist == 2) {
                    if (player != null) {
                        if (PlayerAudioId.equalsIgnoreCase(mData.get(mData.size() - 1).getID())
                                && (player.getDuration() - player.getCurrentPosition() <= 20)) {
                            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared.edit();
                            editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                            editor.commit();
                            player.seekTo(0, 0);
                            player.setPlayWhenReady(true);

                        } else {
                            player.setPlayWhenReady(true);
                        }
                    }
                    isPlayPlaylist = 1;
                    binding.llPause.setVisibility(View.VISIBLE);
                    binding.llPlay.setVisibility(View.GONE);
                } else {
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        if (AudioPlayerFlag.equalsIgnoreCase("Downloadlist") && MyPlaylist.equalsIgnoreCase(PlaylistID)) {
                            if (isDisclaimer == 1) {
                                if (player != null) {
                                    if (!player.getPlayWhenReady()) {
                                        player.setPlayWhenReady(true);
                                    } else
                                        player.setPlayWhenReady(true);
                                    callAddTranFrag();
                                    BWSApplication.showToast("The audio shall start playing after the disclaimer", activity);
                                } else
                                    BWSApplication.showToast("The audio shall start playing after the disclaimer", activity);
                            } else {
                                if (player != null) {
                                    if (position != PlayerPosition) {
                                        int ix = player.getMediaItemCount();
                                        if (ix < listModelList.size()) {
                                            callTransparentFrag(shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0), ctx, listModelList, "", PlaylistID, true);
                                        } else {
                                            player.seekTo(position, 0);
                                            player.setPlayWhenReady(true);
                                            SharedPreferences sharedxx = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedxx.edit();
                                            editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
                                            editor.commit();
                                            callAddTranFrag();
                                        }
                                    } else {
                                        callAddTranFrag();
                                    }
                                } else {
                                    callTransparentFrag(shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0), ctx, listModelList, "", PlaylistID, true);
                                    SegmentTag();
                                }
                            }
                        } else {
                            ArrayList<DownloadAudioDetails> listModelList2 = new ArrayList<>();
                            listModelList2.addAll(listModelList);
                            boolean audioc = true;
                            if (isDisclaimer == 1) {
                                if (player != null) {
                                    player.setPlayWhenReady(true);
                                    audioc = false;
                                    listModelList2.add(position, addDisclaimer);
                                } else {
                                    isDisclaimer = 0;
                                    if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                                        audioc = true;
                                        listModelList2.add(position, addDisclaimer);
                                    }
                                }
                            } else {
                                isDisclaimer = 0;
                                if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                                    audioc = true;
                                    listModelList2.add(position, addDisclaimer);
                                }
                            }
                            callTransparentFrag(0, ctx, listModelList2, "", PlaylistID, audioc);
                            SegmentTag();
                        }
                    } else {
                        getAllCompletedMedia(0);
                    }
                    isPlayPlaylist = 1;
                    binding.llPause.setVisibility(View.VISIBLE);
                    binding.llPlay.setVisibility(View.GONE);
                }
//                handler3.postDelayed(UpdateSongTime3,500);
                notifyDataSetChanged();
            });

            holder.binding.llMainLayout.setOnClickListener(view -> {
                SharedPreferences sharedx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                IsPlayDisclimer = (sharedx.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1"));

                if (BWSApplication.isNetworkConnected(ctx)) {
                    if (AudioPlayerFlag.equalsIgnoreCase("Downloadlist") && MyPlaylist.equalsIgnoreCase(PlaylistID)) {
                        if (isDisclaimer == 1) {
                            if (player != null) {
                                if (!player.getPlayWhenReady()) {
                                    player.setPlayWhenReady(true);
                                } else
                                    player.setPlayWhenReady(true);
                                callAddTranFrag();
                                BWSApplication.showToast("The audio shall start playing after the disclaimer", activity);
                            } else
                                BWSApplication.showToast("The audio shall start playing after the disclaimer", activity);
                        } else {
                            if (player != null) {
                                if (position != PlayerPosition) {
                                    int ix = player.getMediaItemCount();
                                    if (ix < listModelList.size()) {
                                        callTransparentFrag(position, ctx, listModelList, "", PlaylistID, true);
                                    } else {
                                        player.seekTo(position, 0);
                                        player.setPlayWhenReady(true);
                                        miniPlayer = 1;
                                        SharedPreferences sharedxx = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedxx.edit();
                                        editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
                                        editor.commit();
                                        callAddTranFrag();
                                    }
                                } else {
                                    callAddTranFrag();
                                }
                            } else {
                                callTransparentFrag(position, ctx, listModelList, "", PlaylistID, true);
                                SegmentTag();
                            }
                        }
                    } else {
                        ArrayList<DownloadAudioDetails> listModelList2 = new ArrayList<>();
                        listModelList2.addAll(listModelList);
                        boolean audioc = true;
                        if (isDisclaimer == 1) {
                            if (player != null) {
                                player.setPlayWhenReady(true);
                                audioc = false;
                                listModelList2.add(position, addDisclaimer);
                            } else {
                                isDisclaimer = 0;
                                if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                                    audioc = true;
                                    listModelList2.add(position, addDisclaimer);
                                }
                            }
                        } else {
                            isDisclaimer = 0;
                            if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                                audioc = true;
                                listModelList2.add(position, addDisclaimer);
                            }
                        }
                        callTransparentFrag(position, ctx, listModelList2, "", PlaylistID, audioc);
                        SegmentTag();
                    }
                } else {
                    getAllCompletedMedia(position);
                }
                isPlayPlaylist = 1;
                binding.llPause.setVisibility(View.VISIBLE);
                binding.llPlay.setVisibility(View.GONE);
//                handler3.postDelayed(UpdateSongTime3,500);
                notifyDataSetChanged();
            });

            if (BWSApplication.isNetworkConnected(ctx)) {
                holder.binding.llMore.setClickable(true);
                holder.binding.llMore.setEnabled(true);
                holder.binding.ivMore.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);

            } else {
                holder.binding.llMore.setClickable(false);
                holder.binding.llMore.setEnabled(false);
                holder.binding.ivMore.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            }
            holder.binding.llMore.setOnClickListener(view -> {

                if (AudioPlayerFlag.equalsIgnoreCase("Downloadlist") && MyPlaylist.equalsIgnoreCase(PlaylistID)) {
                    if (isDisclaimer == 1) {
                        BWSApplication.showToast("You can see details after the disclaimer", activity);
                    } else {
                        BWSApplication.callAudioDetails(mData.get(position).getID(), ctx, act, CoUserID, "downloadList",
                                mData, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), position);
                    }
                } else {
                    BWSApplication.callAudioDetails(mData.get(position).getID(), ctx, act, CoUserID, "downloadList",
                            mData, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), position);
                }
            });
        }

        private void getAllCompletedMedia(int position) {

            SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE);
            AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
            String MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
            String MyPlaylistName = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "");
            String PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "");
            int PlayerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
            int pos = 0;
            if (AudioPlayerFlag.equalsIgnoreCase("Downloadlist") && MyPlaylist.equalsIgnoreCase(PlaylistID)) {
                if (isDisclaimer == 1) {
                    if (player != null) {
                        if (!player.getPlayWhenReady()) {
                            player.setPlayWhenReady(true);
                        } else
                            player.setPlayWhenReady(true);
                        callAddTranFrag();
                        BWSApplication.showToast("The audio shall start playing after the disclaimer", activity);
                    } else
                        BWSApplication.showToast("The audio shall start playing after the disclaimer", activity);
                } else {
                    ArrayList<DownloadAudioDetails> listModelList2 = new ArrayList<>();
                    for (int i = 0; i < listModelList.size(); i++) {
                        if (downloadAudioDetailsList.contains(listModelList.get(i).getName())) {
                            listModelList2.add(listModelList.get(i));
                        }
                    }

                    if (position != PlayerPosition) {
                        if (downloadAudioDetailsList.contains(listModelList.get(position).getName())) {
                            pos = position;
                            if (listModelList2.size() != 0) {
                                callTransparentFrag(pos, ctx, listModelList2, "", PlaylistID, true);
                            } else {
                                BWSApplication.showToast(ctx.getString(R.string.no_server_found), activity);
                            }
                        } else {
//                                pos = 0;
                            BWSApplication.showToast(ctx.getString(R.string.no_server_found), activity);
                        }
                    } else {
                        callAddTranFrag();
                    }
                    SegmentTag();

                }
            } else {
                ArrayList<DownloadAudioDetails> listModelList2 = new ArrayList<>();
                for (int i = 0; i < listModelList.size(); i++) {
                    if (downloadAudioDetailsList.contains(listModelList.get(i).getName())) {
                        listModelList2.add(listModelList.get(i));
                    }
                }
                if (downloadAudioDetailsList.contains(listModelList.get(position).getName())) {
                    pos = position;

                    boolean audioc = true;
                    if (isDisclaimer == 1) {
                        if (player != null) {
                            player.setPlayWhenReady(true);
                            audioc = false;
                            listModelList2.add(pos, addDisclaimer);
                        } else {
                            isDisclaimer = 0;
                            if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                                audioc = true;
                                listModelList2.add(pos, addDisclaimer);
                            }
                        }
                    } else {
                        isDisclaimer = 0;
                        if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                            audioc = true;
                            listModelList2.add(pos, addDisclaimer);
                        }
                    }
                    if (listModelList2.size() != 0) {
                        if (!listModelList2.get(pos).getAudioFile().equalsIgnoreCase("")) {
                            if (listModelList2.size() != 0) {
                                callTransparentFrag(pos, ctx, listModelList2, "", PlaylistID, audioc);
                            } else {
                                BWSApplication.showToast(ctx.getString(R.string.no_server_found), activity);
                            }
                        } else if (listModelList2.get(pos).getAudioFile().equalsIgnoreCase("") && listModelList2.size() > 1) {
                            callTransparentFrag(pos, ctx, listModelList2, "", PlaylistID, audioc);
                        } else {
                            BWSApplication.showToast(ctx.getString(R.string.no_server_found), activity);
                        }
                    } else {
                        BWSApplication.showToast(ctx.getString(R.string.no_server_found), activity);
                    }
                } else {
                    BWSApplication.showToast(ctx.getString(R.string.no_server_found), activity);
                }
                SegmentTag();
            }
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return listFilterData.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    final FilterResults filterResults = new FilterResults();
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        listFilterData = listModelList;
                    } else {
                        List<DownloadAudioDetails> filteredList = new ArrayList<>();
                        for (DownloadAudioDetails row : listModelList) {
                            if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }
                        listFilterData = filteredList;
                    }
                    filterResults.values = listFilterData;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    if (listFilterData.size() == 0) {
                        binding.llError.setVisibility(View.VISIBLE);
                        binding.rvPlayLists.setVisibility(View.GONE);
//                        binding.tvFound.setText("Couldn't find '" + SearchFlag + "'. Try searching again");
                        binding.tvFound.setText("No result found");
                        Log.e("search", SearchFlag);
                        binding.tvTag.setVisibility(View.GONE);
                    } else {
                        binding.tvTag.setVisibility(View.VISIBLE);
                        binding.llError.setVisibility(View.GONE);
                        binding.rvPlayLists.setVisibility(View.VISIBLE);
                        listFilterData = (List<DownloadAudioDetails>) filterResults.values;
                        notifyDataSetChanged();
                    }
                }
            };
        }

        public class MyViewHolders extends RecyclerView.ViewHolder {
            DownloadPlaylistLayoutBinding binding;

            public MyViewHolders(DownloadPlaylistLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
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