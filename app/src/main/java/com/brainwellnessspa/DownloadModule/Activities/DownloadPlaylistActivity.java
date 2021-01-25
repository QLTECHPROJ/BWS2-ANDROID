package com.brainwellnessspa.DownloadModule.Activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Activities.AudioDetailActivity;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment;
import com.brainwellnessspa.EncryptDecryptUtils.FileUtils;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.RoomDataBase.DownloadPlaylistDetails;
import com.brainwellnessspa.Services.GlobalInitExoPlayer;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.ActivityDownloadPlaylistBinding;
import com.brainwellnessspa.databinding.DownloadPlaylistLayoutBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.downloader.PRDownloader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.segment.analytics.Properties;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.ComeScreenAccount;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.audioClick;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.miniPlayer;


import static com.brainwellnessspa.DashboardModule.Playlist.MyPlaylistsFragment.isPlayPlaylist;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.myAudioId;
import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.downloadIdOne;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.filename;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.APP_SERVICE_STATUS;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.callNewPlayerRelease;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.player;

public class DownloadPlaylistActivity extends AppCompatActivity {
    //    Handler handler3;
    public static int comeDeletePlaylist = 0;
    public AudioManager audioManager;
    public int hundredVolume = 0, currentVolume = 0, maxVolume = 0, percent;
    ActivityDownloadPlaylistBinding binding;
    PlayListsAdpater adpater;
    String IsPlayDisclimer, PlaylistDescription, Created, UserID, SearchFlag, AudioFlag, PlaylistID, PlaylistName, PlaylistImage, TotalAudio, Totalhour, Totalminute, PlaylistImageDetails;
    EditText searchEditText;
    Context ctx;
    DownloadAudioDetails addDisclaimer = new DownloadAudioDetails();
    int startTime;
    long myProgress = 0, diff = 0, currentDuration = 0;
    private List<DownloadPlaylistDetails> listModelList;
    //    private Runnable UpdateSongTime3;
    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("MyData")) {
                String data = intent.getStringExtra("MyData");
                Log.d("play_pause_Action", data);

                SharedPreferences sharedw = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                boolean audioPlay = sharedw.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                AudioFlag = sharedw.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pID = sharedw.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                if (audioPlay && AudioFlag.equalsIgnoreCase("Downloadlist") && pID.equalsIgnoreCase(PlaylistName)) {
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
                        binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_icon));
                    } else {
                        isPlayPlaylist = 2;
                        binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_blue_play_icon));
                    }
                    if (player != null) {
                        adpater.notifyDataSetChanged();
                    }

                } else {
                    isPlayPlaylist = 0;
                    binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_blue_play_icon));
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_download_playlist);
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        ctx = DownloadPlaylistActivity.this;
        addDisclaimer();
        ComeScreenAccount = 0;
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
        audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        percent = 100;
        hundredVolume = (int) (currentVolume * percent) / maxVolume;
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
        super.onResume();
        PrepareData();
    }

    public void PrepareData() {
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");

        /*
        try {
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
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            if (!AudioFlag.equalsIgnoreCase("0")) {
                comefromDownload = "1";
                callAddTranFrag();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/
        try {
            GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
            globalInitExoPlayer.UpdateMiniPlayer(ctx);
            if (!AudioFlag.equalsIgnoreCase("0")) {
                comefromDownload = "1";
                callAddTranFrag();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(10, 8, 10, 210);
                binding.llSpace.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(10, 8, 10, 20);
                binding.llSpace.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
        String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
        if (audioPlay && AudioFlag.equalsIgnoreCase("Downloadlist") && pID.equalsIgnoreCase(PlaylistName)) {
            if (player != null) {
                if (player.getPlayWhenReady()) {
                    isPlayPlaylist = 1;
//                    handler3.postDelayed(UpdateSongTime3, 500);
                    binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_icon));
                } else {
                    isPlayPlaylist = 2;
//                    handler3.postDelayed(UpdateSongTime3, 500);
                    binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_blue_play_icon));
                }
            } else {
                isPlayPlaylist = 0;
                binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_blue_play_icon));
            }
        } else {
            isPlayPlaylist = 0;
            binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_blue_play_icon));
        }
        binding.ivPlaylistStatus.setVisibility(View.VISIBLE);
        binding.tvLibraryName.setText(PlaylistName);
        /*MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                5, 3, 1f, 0);
        binding.ivBanner.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivBanner.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivBanner.setScaleType(ImageView.ScaleType.FIT_XY);*/
        if (BWSApplication.isNetworkConnected(ctx)) {
            if (!PlaylistImageDetails.equalsIgnoreCase("")) {
                try {
                    Glide.with(ctx).load(PlaylistImageDetails).thumbnail(0.05f)
                            .placeholder(R.drawable.audio_bg)
                            .error(R.drawable.audio_bg)
                            .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivBanner);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                binding.ivBanner.setImageResource(R.drawable.audio_bg);
            }
        } else {
            binding.ivBanner.setImageResource(R.drawable.audio_bg);
        }
        binding.searchView.onActionViewExpanded();
        searchEditText = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.dark_blue_gray));
        searchEditText.setHintTextColor(getResources().getColor(R.color.gray));
        ImageView closeButton = binding.searchView.findViewById(R.id.search_close_btn);
        binding.searchView.clearFocus();
        searchEditText.setHint("Search for audios");
        closeButton.setOnClickListener(v -> {
            binding.searchView.clearFocus();
            searchEditText.setText("");
            binding.searchView.setQuery("", false);
        });

        binding.llDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                if (audioPlay && AudioFlag.equalsIgnoreCase("Downloadlist") && pID.equalsIgnoreCase(PlaylistName)) {
                    BWSApplication.showToast("Currently this playlist is in player,so you can't delete this playlist as of now", ctx);
                } else {
                    final Dialog dialog = new Dialog(ctx);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.logout_layout);
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
            }
        });

        if (TotalAudio.equalsIgnoreCase("") || TotalAudio.equalsIgnoreCase("0") &&
                Totalhour.equalsIgnoreCase("") && Totalminute.equalsIgnoreCase("")) {
            binding.tvLibraryDetail.setText("0 Audio | 0h 0m");
        } else {
            if (Totalminute.equalsIgnoreCase("")) {
                binding.tvLibraryDetail.setText(TotalAudio + " Audio | " + Totalhour + "h 0m");
            } else {
                binding.tvLibraryDetail.setText(TotalAudio + " Audio | " + Totalhour + "h " + Totalminute + "m");
            }
        }

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
        binding.tvPlaylist.setText("Playlist");
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
                        for (int i = 1; i < fileNameList1.size(); i++) {
                            if (playlistDownloadId.get(i).equalsIgnoreCase(PlaylistID)) {
                                Log.e("cancel name id", "My id " + i + fileNameList1.get(i));
                                fileNameList.remove(i);
                                audioFile.remove(i);
                                playlistDownloadId.remove(i);
                                Log.e("cancel id", "My id " + playlistDownloadId.size() + i);
                            }
                        }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        LocalBroadcastManager.getInstance(ctx).unregisterReceiver(listener);
        finish();
    }

    private void getMedia(String playlistID) {
        DatabaseClient
                .getInstance(this)
                .getaudioDatabase()
                .taskDao()
                .getAllAudioByPlaylist1(PlaylistID).observe(this, audioList -> {

            adpater = new PlayListsAdpater(audioList, ctx);
            LocalBroadcastManager.getInstance(ctx).registerReceiver(listener, new IntentFilter("play_pause_Action"));
            binding.rvPlayLists.setAdapter(adpater);
        });
    }

    public void GetPlaylistMedia(String playlistID) {
        DatabaseClient
                .getInstance(this)
                .getaudioDatabase()
                .taskDao()
                .getAllAudioByPlaylist1(PlaylistID).observe(this, audioList -> {

            deleteDownloadFile(getApplicationContext(), playlistID);
            for (int i = 0; i < audioList.size(); i++) {
                GetSingleMedia(audioList.get(i).getAudioFile(), ctx.getApplicationContext(), playlistID);
            }
        });
    }

    private void deleteDownloadFile(Context applicationContext, String PlaylistId) {
        class DeleteMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(applicationContext)
                        .getaudioDatabase()
                        .taskDao()
                        .deleteByPlaylistId(PlaylistId);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
//                notifyItemRemoved(position);
                deletePlaylist(PlaylistID);
                super.onPostExecute(aVoid);
            }
        }
        DeleteMedia st = new DeleteMedia();
        st.execute();
    }

    public void GetSingleMedia(String AudioFile, Context ctx, String playlistID) {
        DatabaseClient
                .getInstance(this)
                .getaudioDatabase()
                .taskDao()
                .getLastIdByuId1(AudioFile).observe(this, audioList -> {
            if (audioList.size() != 0) {
                if (audioList.size() == 1) {
                    FileUtils.deleteDownloadedFile(ctx, audioList.get(0).getName());
                }
            }
        });
       /* class GetMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                oneAudioDetailsList = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .getLastIdByuId(AudioFile);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (oneAudioDetailsList.size() != 0) {
                    if (oneAudioDetailsList.size() == 1) {
                        FileUtils.deleteDownloadedFile(ctx, oneAudioDetailsList.get(0).getName());
                    }
                }
                super.onPostExecute(aVoid);
            }
        }
        GetMedia sts = new GetMedia();
        sts.execute();*/
    }

    private void deletePlaylist(String playlistId) {
        class DeleteMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .deletePlaylist(playlistId);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }
        DeleteMedia st = new DeleteMedia();
        st.execute();
    }

    private void addDisclaimer() {
        addDisclaimer = new DownloadAudioDetails();
        addDisclaimer.setID("0");
        addDisclaimer.setName("Disclaimer");
        addDisclaimer.setAudioFile("");
        addDisclaimer.setAudioDirection("The audio shall start playing after the disclaimer");
        addDisclaimer.setAudiomastercat("");
        addDisclaimer.setAudioSubCategory("");
        addDisclaimer.setImageFile("");
        addDisclaimer.setLike("");
        addDisclaimer.setDownload("");
        addDisclaimer.setAudioDuration("00:48");
    }

    private void callTransparentFrag(int position, Context ctx, List<DownloadAudioDetails> listModelList, String s, String playlistID) {
        miniPlayer = 1;
        audioClick = true;

        callNewPlayerRelease();
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        Gson gson = new Gson();
        String json = gson.toJson(listModelList);
        editor.putString(CONSTANTS.PREF_KEY_modelList, json);
        editor.putInt(CONSTANTS.PREF_KEY_position, position);
        editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
        editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
        editor.putString(CONSTANTS.PREF_KEY_PlaylistId, playlistID);
        editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
        editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "Downloadlist");
        editor.commit();
        callAddTranFrag();
    }

    private void callAddTranFrag() {
        try {

            Fragment fragment = new MiniPlayerFragment();
            FragmentManager fragmentManager1 = getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .add(R.id.flContainer, fragment)
                    .commit();
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
        p.putValue("audioService", APP_SERVICE_STATUS);
        p.putValue("sound", String.valueOf(hundredVolume));
        BWSApplication.addToSegment("Playlist Started", p, CONSTANTS.track);
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
//            holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg);
//            holder.binding.llMainLayout.setBackgroundResource(R.color.highlight_background);
            if (BWSApplication.isNetworkConnected(ctx)) {
                Glide.with(ctx).load(mData.get(position).getImageFile()).thumbnail(0.05f)
                        .placeholder(R.drawable.ic_music_icon)
                        .error(R.drawable.ic_music_icon)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            } else {
                holder.binding.ivRestaurantImage.setImageResource(R.drawable.ic_music_icon);
            }

            SharedPreferences sharedzw = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            boolean audioPlayz = sharedzw.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
            AudioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            String pIDz = sharedzw.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
            if (audioPlayz && AudioFlag.equalsIgnoreCase("Downloadlist") && pIDz.equalsIgnoreCase(PlaylistName)) {
                if (myAudioId.equalsIgnoreCase(mData.get(position).getID())) {
                    songId = myAudioId;
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
                    holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg);
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

            binding.ivPlaylistStatus.setOnClickListener(view -> {
                SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                IsPlayDisclimer = (shared1.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1"));
                if (isPlayPlaylist == 1) {
                    if (player != null) {
                        player.setPlayWhenReady(false);
                    }
                    isPlayPlaylist = 2;
                    binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_blue_play_icon));
                    callAddTranFrag();
                } else if (isPlayPlaylist == 2) {
                    if (player != null) {
                        if (myAudioId.equalsIgnoreCase(mData.get(mData.size() - 1).getID())
                                && (player.getDuration() - player.getCurrentPosition() <= 20)) {
                            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared.edit();
                            editor.putInt(CONSTANTS.PREF_KEY_position, 0);
                            editor.commit();
                            player.seekTo(0, 0);
                            player.setPlayWhenReady(true);

                        } else {
                            player.setPlayWhenReady(true);
                        }
                    }
                    isPlayPlaylist = 1;
                    binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_icon));
                    callAddTranFrag();
                } else {
                    SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                    boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                    AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                    String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                    if (BWSApplication.isNetworkConnected(ctx)) {

                        if (audioPlay && AudioFlag.equalsIgnoreCase("Downloadlist") && pID.equalsIgnoreCase(PlaylistName)) {
                            if (isDisclaimer == 1) {
                                if (player != null) {
                                    if (!player.getPlayWhenReady()) {
                                        player.setPlayWhenReady(true);
                                    } else
                                        player.setPlayWhenReady(true);
                                    callAddTranFrag();
                                    BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                                } else
                                    BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                            } else {
                                if (player != null) {
                                    player.seekTo(position, 0);
                                    player.setPlayWhenReady(true);
                                    miniPlayer = 1;
                                    SharedPreferences sharedxx = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedxx.edit();
                                    editor.putInt(CONSTANTS.PREF_KEY_position, position);
                                    editor.commit();
                                    callAddTranFrag();
                                } else {
                                    callTransparentFrag(0, ctx, listModelList, "", PlaylistName);
                                    SegmentTag();
                                }
                            }
                        } else {
                            isDisclaimer = 0;

                            List<DownloadAudioDetails> listModelList2 = new ArrayList<>();
                            if (IsPlayDisclimer.equalsIgnoreCase("1")) {

                                listModelList2.add(addDisclaimer);
                            }
                            listModelList2.addAll(listModelList);
                            callTransparentFrag(0, ctx, listModelList2, "", PlaylistName);
                            SegmentTag();
                        }
                    } else {
                        getAllCompletedMedia(audioPlay, AudioFlag, pID, 0, shared);
                    }
                    isPlayPlaylist = 1;
                    binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_icon));
                }
//                handler3.postDelayed(UpdateSongTime3,500);
                notifyDataSetChanged();
            });

            holder.binding.llMainLayout.setOnClickListener(view -> {
                SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                IsPlayDisclimer = (shared1.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1"));
                SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                if (BWSApplication.isNetworkConnected(ctx)) {
                    if (audioPlay && AudioFlag.equalsIgnoreCase("Downloadlist") && pID.equalsIgnoreCase(PlaylistName)) {
                        if (isDisclaimer == 1) {
                            if (player != null) {
                                if (!player.getPlayWhenReady()) {
                                    player.setPlayWhenReady(true);
                                } else
                                    player.setPlayWhenReady(true);
                                callAddTranFrag();
                                BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                            } else
                                BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                        } else {
                            if (player != null) {
                                player.seekTo(position, 0);
                                player.setPlayWhenReady(true);
                                miniPlayer = 1;
                                SharedPreferences sharedxx = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedxx.edit();
                                editor.putInt(CONSTANTS.PREF_KEY_position, position);
                                editor.commit();
                                callAddTranFrag();
                            } else {
                                callTransparentFrag(holder.getAdapterPosition(), ctx, listModelList, "", PlaylistName);
                                SegmentTag();
                            }
                        }
                    } else {
                        isDisclaimer = 0;

                        List<DownloadAudioDetails> listModelList2 = new ArrayList<>();
                        if (position != 0) {
                            listModelList2.addAll(listModelList);
                            if (IsPlayDisclimer.equalsIgnoreCase("1")) {

                                listModelList2.add(holder.getAdapterPosition(), addDisclaimer);
                            }
                        } else {
                            if (IsPlayDisclimer.equalsIgnoreCase("1")) {

                                listModelList2.add(addDisclaimer);
                            }
                            listModelList2.addAll(listModelList);
                        }
                        callTransparentFrag(holder.getAdapterPosition(), ctx, listModelList2, "", PlaylistName);
                        SegmentTag();
                    }
                } else {
                    getAllCompletedMedia(audioPlay, AudioFlag, pID, position, shared);
                }
                isPlayPlaylist = 1;
                binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_icon));
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
                SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                if (audioPlay && AudioFlag.equalsIgnoreCase("Downloadlist") && pID.equalsIgnoreCase(PlaylistName)) {
                    if (isDisclaimer == 1) {
                        BWSApplication.showToast("You can see details after the disclaimer", ctx);
                    } else {
                        Intent i = new Intent(ctx, AudioDetailActivity.class);
                        i.putExtra("play", "playlist");
                        i.putExtra("ID", mData.get(position).getID());
                        i.putExtra("PlaylistAudioId", "");
                        i.putExtra("position", position);
                        Gson gson = new Gson();
                        String json = gson.toJson(mData);
                        i.putExtra("data", json);
                        i.putExtra("comeFrom", "myDownloadPlaylist");
                        startActivity(i);
                    }
                } else {
                    Intent i = new Intent(ctx, AudioDetailActivity.class);
                    i.putExtra("play", "playlist");
                    i.putExtra("ID", mData.get(position).getID());
                    i.putExtra("PlaylistAudioId", "");
                    i.putExtra("position", position);
                    Gson gson = new Gson();
                    String json = gson.toJson(mData);
                    i.putExtra("data", json);
                    i.putExtra("comeFrom", "myDownloadPlaylist");
                    startActivity(i);
                }
            });
        }

        private void getAllCompletedMedia(boolean audioPlay, String AudioFlag, String pID, int position, SharedPreferences shared) {
            class GetTask extends AsyncTask<Void, Void, Void> {
                List<String> downloadAudioDetailsList = new ArrayList<>();

                @Override
                protected Void doInBackground(Void... voids) {
                    downloadAudioDetailsList = DatabaseClient
                            .getInstance(ctx)
                            .getaudioDatabase()
                            .taskDao()
                            .geAllDataBYDownloaded("Complete");
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    int pos = 0;
                    if (audioPlay && AudioFlag.equalsIgnoreCase("Downloadlist") && pID.equalsIgnoreCase(PlaylistName)) {
                        if (isDisclaimer == 1) {
                            if (player != null) {
                                if (!player.getPlayWhenReady()) {
                                    player.setPlayWhenReady(true);
                                } else
                                    player.setPlayWhenReady(true);
                                callAddTranFrag();
                                BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                            } else
                                BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                        } else {
                            ArrayList<DownloadAudioDetails> listModelList2 = new ArrayList<>();
                            for (int i = 0; i < listModelList.size(); i++) {
                                if (downloadAudioDetailsList.contains(listModelList.get(i).getName())) {
                                    listModelList2.add(listModelList.get(i));
                                }
                            }
                            if (downloadAudioDetailsList.contains(listModelList.get(position).getName())) {
                                pos = position;
                            } else {
                                pos = 0;
                            }
                            callTransparentFrag(pos, ctx, listModelList2, "", PlaylistName);
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
                        } else {
                            pos = 0;
                        }
                        isDisclaimer = 0;
                        if (IsPlayDisclimer.equalsIgnoreCase("1")) {

                            listModelList2.add(pos, addDisclaimer);
                        }
                        callTransparentFrag(pos, ctx, listModelList2, "", PlaylistName);
                        SegmentTag();
                    }
                    notifyDataSetChanged();
                    super.onPostExecute(aVoid);
                }
            }
            GetTask st = new GetTask();
            st.execute();
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
                        binding.tvFound.setText("Couldn't find '" + SearchFlag + "'. Try searching again");
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
}