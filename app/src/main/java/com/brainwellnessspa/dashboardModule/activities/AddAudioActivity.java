package com.brainwellnessspa.dashboardModule.activities;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.dashboardOldModule.Models.SubPlayListModel;
import com.brainwellnessspa.dashboardOldModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.services.GlobalInitExoPlayer;
import com.brainwellnessspa.utility.APINewClient;
import com.brainwellnessspa.utility.CONSTANTS;
import com.brainwellnessspa.utility.MeasureRatio;
import com.brainwellnessspa.dashboardModule.models.AddToPlaylistModel;
import com.brainwellnessspa.dashboardModule.models.HomeScreenModel;
import com.brainwellnessspa.dashboardModule.models.SearchBothModel;
import com.brainwellnessspa.dashboardModule.models.SearchPlaylistModel;
import com.brainwellnessspa.dashboardModule.models.SuggestedModel;
import com.brainwellnessspa.databinding.ActivityAddAudioBinding;
import com.brainwellnessspa.databinding.DownloadsLayoutBinding;
import com.brainwellnessspa.databinding.GlobalSearchLayoutBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.segment.analytics.Properties;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.BWSApplication.PlayerAudioId;
import static com.brainwellnessspa.dashboardOldModule.Activities.DashboardActivity.audioClick;
import static com.brainwellnessspa.dashboardOldModule.TransparentPlayer.Fragments.MiniPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;
import static com.brainwellnessspa.services.GlobalInitExoPlayer.callNewPlayerRelease;
import static com.brainwellnessspa.services.GlobalInitExoPlayer.notificationId;
import static com.brainwellnessspa.services.GlobalInitExoPlayer.player;
import static com.brainwellnessspa.services.GlobalInitExoPlayer.relesePlayer;

public class AddAudioActivity extends AppCompatActivity {
    public static boolean addToSearch = false;
    public static String MyPlaylistIds = "";
    public static String PlaylistIDMS = "";
    ActivityAddAudioBinding binding;
    Context ctx;
    String CoUSERID, USERID, UserName, PlaylistID = "", IsPlayDisclimer;
    SerachListAdpater serachListAdpater;
    EditText searchEditText;
    Activity activity;
    int listSize = 0;
    SuggestedAdpater suggestedAdpater;
    //    private Runnable UpdateSongTime3;
    private final BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("MyData")) {
                String data = intent.getStringExtra("MyData");
                Log.d("play_pause_Action", data);
                SharedPreferences sharedzw = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                String AudioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
                if (!AudioFlag.equalsIgnoreCase("Downloadlist") &&
                        !AudioFlag.equalsIgnoreCase("playlist") &&
                        !AudioFlag.equalsIgnoreCase("TopCategories")) {
                    if (player != null) {
                        if (listSize != 0) {
                            serachListAdpater.notifyDataSetChanged();
                        }
                        suggestedAdpater.notifyDataSetChanged();
                    }
                }
            }
        }
    };
    //    Handler handler3;
    Properties p;
    int stackStatus = 0;
    boolean myBackPress = false;
    boolean notificationStatus = false;
    GsonBuilder gsonBuilder;
    ArrayList<String> section;
    private int numStarted = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_audio);
        ctx = AddAudioActivity.this;
        activity = AddAudioActivity.this;
        if (getIntent().getExtras() != null) {
            PlaylistID = getIntent().getStringExtra(CONSTANTS.PlaylistID);
        }

        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        USERID = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "");
        CoUSERID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "");
        UserName = shared1.getString(CONSTANTS.PREFE_ACCESS_NAME, "");

        notificationStatus = false;

        p = new Properties();
        p.putValue("userId", USERID);
        p.putValue("coUserId", CoUSERID);
        if (PlaylistID.equalsIgnoreCase("")) {
            p.putValue("source", "Manage Search Screen");
        } else {
            p.putValue("source", "Add Audio Screen");
        }

        BWSApplication.addToSegment("Search Screen Viewed", p, CONSTANTS.screen);

        binding.searchView.onActionViewExpanded();
        searchEditText = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.dark_blue_gray));
        searchEditText.setHintTextColor(getResources().getColor(R.color.gray));
        ImageView closeButton = binding.searchView.findViewById(R.id.search_close_btn);
        binding.searchView.clearFocus();
        closeButton.setOnClickListener(view -> {
            binding.searchView.clearFocus();
            searchEditText.setText("");
            binding.rvSerachList.setAdapter(null);
            binding.rvSerachList.setVisibility(View.GONE);
            binding.llError.setVisibility(View.GONE);
            binding.searchView.setQuery("", false);
        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String search) {
                binding.searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String search) {
                if (searchEditText.getText().toString().equalsIgnoreCase("")) {
                } else {
                    prepareSearchData(search, searchEditText);
                }

                p = new Properties();
                p.putValue("userId", USERID);
                p.putValue("coUserId", CoUSERID);
                if (PlaylistID.equalsIgnoreCase("")) {
                    p.putValue("source", "Manage Search Screen");
                } else {
                    p.putValue("source", "Add Audio Screen");
                }
                p.putValue("searchKeyword", search);
                BWSApplication.addToSegment("Audio Searched", p, CONSTANTS.track);
                return false;
            }
        });

        binding.llBack.setOnClickListener(view -> callback());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(new AppLifecycleCallback());
        }
        RecyclerView.LayoutManager manager = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        binding.rvSuggestedList.setLayoutManager(manager);
        binding.rvSuggestedList.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager layoutSerach = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        binding.rvSerachList.setLayoutManager(layoutSerach);
        binding.rvSerachList.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager layoutPlay = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        binding.rvPlayList.setLayoutManager(layoutPlay);
        binding.rvPlayList.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void onResume() {
        prepareSuggestedData();
        super.onResume();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(AddAudioActivity.this).unregisterReceiver(listener);
        super.onPause();
    }

    private void callback() {
        myBackPress = true;
        comefromDownload = "0";
        finish();
    }

    private void prepareSearchData(String search, EditText searchEditText) {
        Gson gson = new Gson();
        SharedPreferences shared1x = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE);
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
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<SearchBothModel> listCall = APINewClient.getClient().getSearchBoth(CoUSERID, search);
            listCall.enqueue(new Callback<SearchBothModel>() {
                @Override
                public void onResponse(@NotNull Call<SearchBothModel> call, @NotNull Response<SearchBothModel> response) {
                    try {
                        SearchBothModel listModel = response.body();
                        if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            if (!searchEditText.getText().toString().equalsIgnoreCase("")) {
                                if (listModel.getResponseData().size() == 0) {
                                    binding.rvSerachList.setVisibility(View.GONE);
                                    binding.llError.setVisibility(View.VISIBLE);
                                    binding.tvFound.setText("Please use another term and try searching again");
//                                    binding.tvFound.setText("Couldn't find '" + search + "'. Try searching again");
                                } else {
                                    binding.llError.setVisibility(View.GONE);
                                    binding.rvSerachList.setVisibility(View.VISIBLE);
                                    serachListAdpater = new SerachListAdpater(listModel.getResponseData(), activity, binding.rvSerachList, CoUSERID);
                                    binding.rvSerachList.setAdapter(serachListAdpater);
                                    LocalBroadcastManager.getInstance(ctx)
                                            .registerReceiver(listener, new IntentFilter("play_pause_Action"));
                                }
                            } else if (searchEditText.getText().toString().equalsIgnoreCase("")) {
                                binding.rvSerachList.setAdapter(null);
                                binding.rvSerachList.setVisibility(View.GONE);
                                binding.llError.setVisibility(View.GONE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<SearchBothModel> call, @NotNull Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity);
        }
    }

    private void prepareSuggestedData() {
        Gson gson = new Gson();
        SharedPreferences shared1x = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE);
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
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<SuggestedModel> listCall = APINewClient.getClient().getSuggestedLists(CoUSERID);
            listCall.enqueue(new Callback<SuggestedModel>() {
                @Override
                public void onResponse(@NotNull Call<SuggestedModel> call, @NotNull Response<SuggestedModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            SuggestedModel listModel = response.body();
                            binding.tvSuggestedAudios.setText(R.string.Suggested_Audios);
                            binding.tvSAViewAll.setVisibility(View.VISIBLE);
                            suggestedAdpater = new SuggestedAdpater(listModel.getResponseData(), ctx);
                            binding.rvSuggestedList.setAdapter(suggestedAdpater);
                            p = new Properties();
                            p.putValue("userId", USERID);
                            p.putValue("coUserId", CoUSERID);
                            if (PlaylistID.equalsIgnoreCase("")) {
                                p.putValue("source", "Manage Search Screen");
                            } else {
                                p.putValue("source", "Add Audio Screen");
                            }

                            section = new ArrayList<>();
                            gsonBuilder = new GsonBuilder();
                            Gson gson = gsonBuilder.create();
                            for (int i = 0; i < listModel.getResponseData().size(); i++) {
                                section.add(listModel.getResponseData().get(i).getID());
                                section.add(listModel.getResponseData().get(i).getName());
                                section.add(listModel.getResponseData().get(i).getAudiomastercat());
                                section.add(listModel.getResponseData().get(i).getAudioSubCategory());
                                section.add(listModel.getResponseData().get(i).getAudioDuration());
                            }
                            p.putValue("audios", gson.toJson(section));
                            BWSApplication.addToSegment("Suggested Audios List Viewed", p, CONSTANTS.screen);
                            LocalBroadcastManager.getInstance(ctx)
                                    .registerReceiver(listener, new IntentFilter("play_pause_Action"));
                            binding.tvSAViewAll.setOnClickListener(view -> {
                                notificationStatus = true;
                                Intent i = new Intent(ctx, ViewSuggestedActivity.class);
                                i.putExtra("Name", "Suggested Audios");
                                i.putExtra("PlaylistID", PlaylistID);
                                i.putParcelableArrayListExtra("AudiolistModel", listModel.getResponseData());
                                startActivity(i);
                                finish();
                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<SuggestedModel> call, @NotNull Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity);
        }
        binding.tvSuggestedPlaylist.setVisibility(View.GONE);
        binding.rvPlayList.setVisibility(View.GONE);
        binding.tvSPViewAll.setVisibility(View.GONE);
      /*  if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<SearchPlaylistModel> listCall = APINewClient.getClient().getSuggestedPlayLists(CoUSERID);
            listCall.enqueue(new Callback<SearchPlaylistModel>() {
                @Override
                public void onResponse(Call<SearchPlaylistModel> call, Response<SearchPlaylistModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            SearchPlaylistModel listModel = response.body();
                            binding.tvSuggestedPlaylist.setText(R.string.Suggested_Playlist);

                            p = new Properties();
                            p.putValue("userId", UserID);
                            p.putValue("source", "Add Audio Screen");
                            BWSApplication.addToSegment("Recommended Playlists List Viewed", p, CONSTANTS.screen);

                            SuggestedPlayListsAdpater suggestedAdpater = new SuggestedPlayListsAdpater(listModel.getResponseData());
                            binding.rvPlayList.setAdapter(suggestedAdpater);

                            binding.tvSPViewAll.setOnClickListener(view -> {
                                notificationStatus = true;
                                Intent i = new Intent(ctx, ViewSuggestedActivity.class);
                                i.putExtra("Name", "Suggested Playlist");
                                i.putExtra("PlaylistID", PlaylistID);
                                i.putParcelableArrayListExtra("PlaylistModel", listModel.getResponseData());
                                startActivity(i);
                                finish();
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<SearchPlaylistModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity);
        }*/
    }

    @Override
    public void onBackPressed() {
        callback();
    }

    private void callAddSearchAudio(String AudioID, String s, String FromPlaylistId) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<AddToPlaylistModel> listCall = APINewClient.getClient().getAddSearchAudioFromPlaylist(CoUSERID, AudioID, PlaylistID, FromPlaylistId);
            listCall.enqueue(new Callback<AddToPlaylistModel>() {
                @Override
                public void onResponse(@NotNull Call<AddToPlaylistModel> call, @NotNull Response<AddToPlaylistModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            AddToPlaylistModel listModels = response.body();
                            if (listModels.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                BWSApplication.showToast(listModels.getResponseMessage(), activity);

                                SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE);
                                String AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
                                String MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
                                String MyPlaylistName = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "");
                                String PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "");
                                int PlayerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                                if (AudioPlayerFlag.equalsIgnoreCase("playlist") && MyPlaylist.equalsIgnoreCase(PlaylistID)) {
                                    Gson gsonx = new Gson();
                                    String json = shared1.getString(CONSTANTS.PREF_KEY_PlayerAudioList, String.valueOf(gsonx));
                                    Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                                    }.getType();
                                    ArrayList<MainPlayModel> mainPlayModelListold = new ArrayList<>();
                                    mainPlayModelListold = gsonx.fromJson(json, type);
                                    String id = mainPlayModelListold.get(PlayerPosition).getID();
                                    ArrayList<MainPlayModel> mainPlayModelList = new ArrayList<>();
                                    ArrayList<SubPlayListModel.ResponseData.PlaylistSong> playlistSongs = new ArrayList<>();
                                    int size = mainPlayModelListold.size();
                                    for (int i = 0; i < listModels.getResponseData().size(); i++) {
                                        MainPlayModel mainPlayModel = new MainPlayModel();
                                        mainPlayModel.setID(listModels.getResponseData().get(i).getID());
                                        mainPlayModel.setName(listModels.getResponseData().get(i).getName());
                                        mainPlayModel.setAudioFile(listModels.getResponseData().get(i).getAudioFile());
                                        mainPlayModel.setPlaylistID(listModels.getResponseData().get(i).getPlaylistID());
                                        mainPlayModel.setAudioDirection(listModels.getResponseData().get(i).getAudioDirection());
                                        mainPlayModel.setAudiomastercat(listModels.getResponseData().get(i).getAudiomastercat());
                                        mainPlayModel.setAudioSubCategory(listModels.getResponseData().get(i).getAudioSubCategory());
                                        mainPlayModel.setImageFile(listModels.getResponseData().get(i).getImageFile());
                                        mainPlayModel.setAudioDuration(listModels.getResponseData().get(i).getAudioDuration());
                                        mainPlayModelList.add(mainPlayModel);
                                    }
                                    for (int i = 0; i < listModels.getResponseData().size(); i++) {
                                        SubPlayListModel.ResponseData.PlaylistSong mainPlayModel = new SubPlayListModel.ResponseData.PlaylistSong();
                                        mainPlayModel.setID(listModels.getResponseData().get(i).getID());
                                        mainPlayModel.setName(listModels.getResponseData().get(i).getName());
                                        mainPlayModel.setAudioFile(listModels.getResponseData().get(i).getAudioFile());
                                        mainPlayModel.setPlaylistID(listModels.getResponseData().get(i).getPlaylistID());
                                        mainPlayModel.setAudioDirection(listModels.getResponseData().get(i).getAudioDirection());
                                        mainPlayModel.setAudiomastercat(listModels.getResponseData().get(i).getAudiomastercat());
                                        mainPlayModel.setAudioSubCategory(listModels.getResponseData().get(i).getAudioSubCategory());
                                        mainPlayModel.setImageFile(listModels.getResponseData().get(i).getImageFile());
                                        mainPlayModel.setLike(listModels.getResponseData().get(i).getLike());
                                        mainPlayModel.setDownload(listModels.getResponseData().get(i).getDownload());
                                        mainPlayModel.setAudioDuration(listModels.getResponseData().get(i).getAudioDuration());
                                        playlistSongs.add(mainPlayModel);
                                    }
                                    for (int i = 0; i < mainPlayModelList.size(); i++) {
                                        if (mainPlayModelList.get(i).getID().equalsIgnoreCase(id)) {
                                            PlayerPosition = i;
                                            break;
                                        }
                                    }
                                    SharedPreferences sharedd = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedd.edit();
                                    Gson gson = new Gson();
                                    String jsonx = gson.toJson(mainPlayModelList);
                                    String json11 = gson.toJson(playlistSongs);
                                    editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json11);
                                    editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonx);
                                    editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, PlayerPosition);
                                    editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, PlaylistID);
                                    editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, MyPlaylistName);
                                    editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "Created");
                                    editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "playlist");
                                    editor.commit();
                                    if (!mainPlayModelList.get(PlayerPosition).getAudioFile().equals("")) {
                                        List<String> downloadAudioDetailsList = new ArrayList<>();
                                        GlobalInitExoPlayer ge = new GlobalInitExoPlayer();
                                        ge.AddAudioToPlayer(size, mainPlayModelList, downloadAudioDetailsList, ctx);
                                    }
                                }
                                if (s.equalsIgnoreCase("1")) {
                                    finish();
                                }
                            } else if (listModels.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodefail))) {
                                BWSApplication.showToast(listModels.getResponseMessage(), activity);
                            }

                        }
                    } catch (Exception e) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(@NotNull Call<AddToPlaylistModel> call, @NotNull Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(ctx.getString(R.string.no_server_found), activity);
        }
    }

    private void callAddFrag() {
       /* Fragment fragment = new MiniPlayerFragment();
        FragmentManager fragmentManager1 = getSupportFragmentManager();
        fragmentManager1.beginTransaction()
                .add(R.id.flContainer, fragment)
                .commit();*/
    }

    public class SerachListAdpater extends RecyclerView.Adapter<SerachListAdpater.MyViewHolder> {
        Context ctx;
        String UserID, songId;
        RecyclerView rvSerachList;
        private List<SearchBothModel.ResponseData> modelList;

        public SerachListAdpater(List<SearchBothModel.ResponseData> modelList, Context ctx,
                                 RecyclerView rvSerachList, String UserID) {
            this.modelList = modelList;
            this.ctx = ctx;
            this.rvSerachList = rvSerachList;
            this.UserID = UserID;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            GlobalSearchLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.global_search_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvTitle.setText(modelList.get(position).getName());

            if (modelList.get(position).getIscategory().equalsIgnoreCase("1")) {
                holder.binding.tvPart.setText(modelList.get(position).getAudioDuration());
                holder.binding.llRemoveAudio.setVisibility(View.VISIBLE);
                holder.binding.ivLock.setVisibility(View.GONE);
                SharedPreferences sharedzw = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                String AudioPlayerFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
                String MyPlaylist = sharedzw.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
                String PlayFrom = sharedzw.getString(CONSTANTS.PREF_KEY_PlayFrom, "");
                int PlayerPosition = sharedzw.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);

                if (!AudioPlayerFlag.equalsIgnoreCase("Downloadlist") &&
                        !AudioPlayerFlag.equalsIgnoreCase("SubPlayList") && !AudioPlayerFlag.equalsIgnoreCase("TopCategories")) {
                    if (PlayerAudioId.equalsIgnoreCase(modelList.get(position).getID())) {
                        songId = PlayerAudioId;
                        if (player != null) {
                            if (!player.getPlayWhenReady()) {
                                holder.binding.equalizerview.pause();
                            } else
                                holder.binding.equalizerview.resume(true);
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
                } else {
                    holder.binding.equalizerview.setVisibility(View.GONE);
                    holder.binding.llMainLayout.setBackgroundResource(R.color.white);
                    holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                }
                holder.binding.llMainLayoutForPlayer.setOnClickListener(view -> {
//                    if (modelList.get(position).isLock().equalsIgnoreCase("1")) {
//                        if (modelList.get(position).isPlay().equalsIgnoreCase("1")) {
//                            callMainTransFrag(position);
//                        } else if (modelList.get(position).isPlay().equalsIgnoreCase("0")
//                                || modelList.get(position).isPlay().equalsIgnoreCase("")) {
//                            Intent i = new Intent(ctx, MembershipChangeActivity.class);
//                            i.putExtra("ComeFrom", "Plan");
//                            startActivity(i);
//                        }
//                    } else if (modelList.get(position).isLock().equalsIgnoreCase("2")) {
//                        if (modelList.get(position).isPlay().equalsIgnoreCase("1")) {
//                            callMainTransFrag(position);
//                        } else if (modelList.get(position).isPlay().equalsIgnoreCase("0")
//                                || modelList.get(position).isPlay().equalsIgnoreCase("")) {
//                            BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
//                        }
//                    } else if (modelList.get(position).isLock().equalsIgnoreCase("0") || modelList.get(position).isLock().equalsIgnoreCase("")) {
                    callMainTransFrag(position);
//                    }
                });

                holder.binding.llRemoveAudio.setOnClickListener(view -> {
//                    if (modelList.get(position).isLock().equalsIgnoreCase("1")) {
//                        Intent i = new Intent(ctx, MembershipChangeActivity.class);
//                        i.putExtra("ComeFrom", "Plan");
//                        startActivity(i);
//                    } else if (modelList.get(position).isLock().equalsIgnoreCase("2")) {
//                        BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
//                    } else if (modelList.get(position).isLock().equalsIgnoreCase("0") || modelList.get(position).isLock().equalsIgnoreCase("")) {
                    String AudioID = modelList.get(position).getID();
                    if (PlaylistID.equalsIgnoreCase("")) {
                        Intent i = new Intent(ctx, AddPlaylistActivity.class);
                        i.putExtra("AudioId", AudioID);
                        i.putExtra("ScreenView", "Audio Details Screen");
                        i.putExtra("PlaylistID", "");
                        i.putExtra("PlaylistName", "");
                        i.putExtra("PlaylistImage", "");
                        i.putExtra("PlaylistType", "");
                        i.putExtra("Liked", "0");
                        startActivity(i);
                    } else {
                        if (AudioPlayerFlag.equalsIgnoreCase("playList") && MyPlaylist.equalsIgnoreCase(PlaylistID)) {
                            if (isDisclaimer == 1) {
                                BWSApplication.showToast("The audio shall add after playing the disclaimer", activity);
                            } else {
                                callAddSearchAudio(AudioID, "0", "");
                            }
                        } else {
                            callAddSearchAudio(AudioID, "0", "");
                        }
                    }
                });
            } else if (modelList.get(position).getIscategory().equalsIgnoreCase("0")) {
                holder.binding.tvPart.setText(R.string.Playlist);
                holder.binding.tvPart.setVisibility(View.GONE);
                holder.binding.equalizerview.setVisibility(View.GONE);
                holder.binding.llRemoveAudio.setVisibility(View.GONE);
                holder.binding.llMainLayout.setVisibility(View.GONE);
//                if (modelList.get(position).isLock().equalsIgnoreCase("1")) {
//                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                    holder.binding.ivLock.setVisibility(View.VISIBLE);
//                } else if (modelList.get(position).isLock().equalsIgnoreCase("2")) {
//                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                    holder.binding.ivLock.setVisibility(View.VISIBLE);
//                } else if (modelList.get(position).isLock().equalsIgnoreCase("0") || modelList.get(position).isLock().equalsIgnoreCase("")) {
                holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                holder.binding.ivLock.setVisibility(View.GONE);
//                }

                holder.binding.llMainLayout.setOnClickListener(view -> {
//                    if (modelList.get(position).isLock().equalsIgnoreCase("1")) {
//                        Intent i = new Intent(ctx, MembershipChangeActivity.class);
//                        i.putExtra("ComeFrom", "Plan");
//                        startActivity(i);
//                    } else if (modelList.get(position).isLock().equalsIgnoreCase("1")) {
//                        BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
//                    } else if (modelList.get(position).isLock().equalsIgnoreCase("0") || modelList.get(position).isLock().equalsIgnoreCase("")) {
                    comefromDownload = "0";
                    addToSearch = true;
                    MyPlaylistIds = modelList.get(position).getID();
                    PlaylistIDMS = PlaylistID;
                    finish();
//                    }
                });

                holder.binding.llRemoveAudio.setOnClickListener(view -> {
//                    if (modelList.get(position).isLock().equalsIgnoreCase("1")) {
//                        Intent i = new Intent(ctx, MembershipChangeActivity.class);
//                        i.putExtra("ComeFrom", "Plan");
//                        startActivity(i);
//                    } else if (modelList.get(position).isLock().equalsIgnoreCase("2")) {
//                        BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
//                    } else if (modelList.get(position).isLock().equalsIgnoreCase("0") || modelList.get(position).isLock().equalsIgnoreCase("")) {

                    SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE);
                    String AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
                    String MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
                    String PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "");
                    int PlayerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                    if (AudioPlayerFlag.equalsIgnoreCase("playList") && MyPlaylist.equalsIgnoreCase(PlaylistID)) {
                        if (isDisclaimer == 1) {
                            BWSApplication.showToast("The audio shall add after playing the disclaimer", activity);
                        } else {
                            callAddSearchAudio("", "1", modelList.get(position).getID());
                        }
                    } else {
                        callAddSearchAudio("", "1", modelList.get(position).getID());
                    }
//                    }
                });
            }
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.cvImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.cvImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.binding.ivBackgroundImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(modelList.get(position).getImageFile()).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            Glide.with(ctx).load(R.drawable.ic_image_bg).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivBackgroundImage);
            holder.binding.ivIcon.setImageResource(R.drawable.ic_add_two_icon);
        }

        public void callMainTransFrag(int position) {
            try {
                SharedPreferences shared2 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                IsPlayDisclimer = (shared2.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1"));
                SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE);
                String AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
                String MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
                String PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "");
                int PlayerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                if (AudioPlayerFlag.equalsIgnoreCase("SearchModelAudio")
                        && PlayFrom.equalsIgnoreCase("Search Audio")) {
                    if (isDisclaimer == 1) {
                        if (player != null) {
                            if (!player.getPlayWhenReady()) {
                                player.setPlayWhenReady(true);
                            }
                        } else {
                            audioClick = true;
                        }
                        callMyPlayer();
                        BWSApplication.showToast("The audio shall start playing after the disclaimer", activity);
                    } else {
                        ArrayList<SearchBothModel.ResponseData> listModelList2 = new ArrayList<>();
                        listModelList2.add(modelList.get(position));
                        callPlayer(0, listModelList2, true);
                    }
                } else {
                    ArrayList<SearchBothModel.ResponseData> listModelList2 = new ArrayList<>();
                    Gson gson = new Gson();
                    SharedPreferences shared12 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE);
                    String IsPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1");
                    String DisclimerJson = shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString());
                    Type type = new TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio>() {
                    }.getType();
                    HomeScreenModel.ResponseData.DisclaimerAudio arrayList = gson.fromJson(DisclimerJson, type);
                    SearchBothModel.ResponseData mainPlayModel = new SearchBothModel.ResponseData();
                    mainPlayModel.setID(arrayList.getId());
                    mainPlayModel.setName(arrayList.getName());
                    mainPlayModel.setAudioFile(arrayList.getAudioFile());
                    mainPlayModel.setAudioDirection(arrayList.getAudioDirection());
                    mainPlayModel.setAudiomastercat(arrayList.getAudiomastercat());
                    mainPlayModel.setAudioSubCategory(arrayList.getAudioSubCategory());
                    mainPlayModel.setImageFile(arrayList.getImageFile());
                    mainPlayModel.setAudioDuration(arrayList.getAudioDuration());
                    boolean audioc = false;
                    if (isDisclaimer == 1) {
                        if (player != null) {
                            player.setPlayWhenReady(true);
                            audioc = false;
                            listModelList2.add(mainPlayModel);
                        } else {
                            isDisclaimer = 0;
                            if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                                audioc = true;
                                listModelList2.add(mainPlayModel);
                            }
                        }
                    } else {
                        isDisclaimer = 0;
                        if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                            audioc = true;
                            listModelList2.add(mainPlayModel);
                        }
                    }
                    listModelList2.add(modelList.get(position));
                    callPlayer(0, listModelList2, audioc);
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(0, 8, 0, 210);
                binding.llSpace.setLayoutParams(params);
                notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void callMyPlayer() {
            Intent i = new Intent(ctx, MyPlayerActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            ctx.startActivity(i);
            activity.overridePendingTransition(0, 0);
        }

        private void callPlayer(int position, ArrayList<SearchBothModel.ResponseData> listModel, boolean audioc) {
            if (audioc) {
                callNewPlayerRelease();
            }
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, AppCompatActivity.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String json = gson.toJson(listModel);
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json);
            editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
            editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
            editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "");
            editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "Search Audio");
            editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "SearchModelAudio");
            editor.apply();
            audioClick = audioc;
            callMyPlayer();
        }

        @Override
        public int getItemCount() {
            listSize = modelList.size();
            return modelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            GlobalSearchLayoutBinding binding;

            public MyViewHolder(GlobalSearchLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    public class SuggestedAdpater extends RecyclerView.Adapter<SuggestedAdpater.MyViewHolder> {
        Context ctx;
        String songId;
        private List<SuggestedModel.ResponseData> listModel;

        public SuggestedAdpater(List<SuggestedModel.ResponseData> listModel, Context ctx) {
            this.listModel = listModel;
            this.ctx = ctx;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            DownloadsLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.downloads_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvTitle.setText(listModel.get(position).getName());
            holder.binding.tvTime.setText(listModel.get(position).getAudioDuration());
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.cvImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.cvImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.binding.ivBackgroundImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(listModel.get(position).getImageFile()).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            Glide.with(ctx).load(R.drawable.ic_image_bg).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivBackgroundImage);
            holder.binding.ivIcon.setImageResource(R.drawable.ic_add_two_icon);
            SharedPreferences sharedzw = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            String AudioPlayerFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
            String MyPlaylist = sharedzw.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
            String PlayFrom = sharedzw.getString(CONSTANTS.PREF_KEY_PlayFrom, "");
            int PlayerPosition = sharedzw.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);

            if (!AudioPlayerFlag.equalsIgnoreCase("Downloadlist") &&
                    !AudioPlayerFlag.equalsIgnoreCase("SubPlayList") && !AudioPlayerFlag.equalsIgnoreCase("TopCategories")) {
                if (PlayerAudioId.equalsIgnoreCase(listModel.get(position).getID())) {
                    songId = PlayerAudioId;
                    if (player != null) {
                        if (!player.getPlayWhenReady()) {
                            holder.binding.equalizerview.pause();
                        } else
                            holder.binding.equalizerview.resume(true);
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
            } else {
                holder.binding.equalizerview.setVisibility(View.GONE);
                holder.binding.llMainLayout.setBackgroundResource(R.color.white);
                holder.binding.ivBackgroundImage.setVisibility(View.GONE);
            }

//
//            if (listModel.get(position).isLock().equalsIgnoreCase("1")) {
//                if (listModel.get(position).isPlay().equalsIgnoreCase("1")) {
//                    holder.binding.ivLock.setVisibility(View.GONE);
//                } else if (listModel.get(position).isPlay().equalsIgnoreCase("0")
//                        || listModel.get(position).isPlay().equalsIgnoreCase("")) {
//                    holder.binding.ivLock.setVisibility(View.VISIBLE);
//                }
//            } else if (listModel.get(position).isLock().equalsIgnoreCase("2")) {
//                if (listModel.get(position).isPlay().equalsIgnoreCase("1")) {
//                    holder.binding.ivLock.setVisibility(View.GONE);
//                } else if (listModel.get(position).isPlay().equalsIgnoreCase("0")
//                        || listModel.get(position).isPlay().equalsIgnoreCase("")) {
//                    holder.binding.ivLock.setVisibility(View.VISIBLE);
//                }
//            } else if (listModel.get(position).isLock().equalsIgnoreCase("0")
//                    || listModel.get(position).isLock().equalsIgnoreCase("")) {
            holder.binding.ivLock.setVisibility(View.GONE);
//            }

            holder.binding.llMainLayoutForPlayer.setOnClickListener(view -> {
//                if (listModel.get(position).isLock().equalsIgnoreCase("1")) {
//                    if (listModel.get(position).isPlay().equalsIgnoreCase("1")) {
//                        callMainTransFrag(position);
//                    } else if (listModel.get(position).isPlay().equalsIgnoreCase("0")
//                            || listModel.get(position).isPlay().equalsIgnoreCase("")) {
//                        Intent i = new Intent(ctx, MembershipChangeActivity.class);
//                        i.putExtra("ComeFrom", "Plan");
//                        startActivity(i);
//                    }
//                } else if (listModel.get(position).isLock().equalsIgnoreCase("2")) {
//                    if (listModel.get(position).isPlay().equalsIgnoreCase("1")) {
//                        callMainTransFrag(position);
//                    } else if (listModel.get(position).isPlay().equalsIgnoreCase("0")
//                            || listModel.get(position).isPlay().equalsIgnoreCase("")) {
//                        BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
//                    }
//                } else if (listModel.get(position).isLock().equalsIgnoreCase("0")
//                        || listModel.get(position).isLock().equalsIgnoreCase("")) {
                callMainTransFrag(position);
//                }
            });

            holder.binding.llRemoveAudio.setOnClickListener(view -> {
//                if (listModel.get(position).isLock().equalsIgnoreCase("1")) {
//                    Intent i = new Intent(ctx, MembershipChangeActivity.class);
//                    i.putExtra("ComeFrom", "Plan");
//                    startActivity(i);
//                } else if (listModel.get(position).isLock().equalsIgnoreCase("2")) {
//                    BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
//                } else if (listModel.get(position).isLock().equalsIgnoreCase("0") || listModel.get(position).isLock().equalsIgnoreCase("")) {
                if (PlaylistID.equalsIgnoreCase("")) {
                    Intent i = new Intent(ctx, AddPlaylistActivity.class);
                    i.putExtra("AudioId", listModel.get(position).getID());
                    i.putExtra("ScreenView", "Audio Details Screen");
                    i.putExtra("PlaylistID", "");
                    i.putExtra("PlaylistName", "");
                    i.putExtra("PlaylistImage", "");
                    i.putExtra("PlaylistType", "");
                    i.putExtra("Liked", "0");
                    startActivity(i);
                } else {
                    if (AudioPlayerFlag.equalsIgnoreCase("playlist") && MyPlaylist.equalsIgnoreCase(PlaylistID)) {
                        if (isDisclaimer == 1) {
                            BWSApplication.showToast("The audio shall add after playing the disclaimer", activity);
                        } else {
                            callAddSearchAudio(listModel.get(position).getID(), "0", "");
                        }
                    } else {
                        callAddSearchAudio(listModel.get(position).getID(), "0", "");
                    }
                }
            });
        }

        public void callMainTransFrag(int position) {
            try {
                SharedPreferences shared2 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                IsPlayDisclimer = (shared2.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1"));
                SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE);
                String AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
                String PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "");
                int PlayerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                if (AudioPlayerFlag.equalsIgnoreCase("SearchAudio")
                        && PlayFrom.equalsIgnoreCase("Recommended Search")) {
                    if (isDisclaimer == 1) {
                        if (player != null) {
                            if (!player.getPlayWhenReady()) {
                                player.setPlayWhenReady(true);
                            }
                        } else {
                            audioClick = true;
                        }
                        callMyPlayer();
                        BWSApplication.showToast("The audio shall start playing after the disclaimer", activity);
                    } else {
                        ArrayList<SuggestedModel.ResponseData> listModelList2 = new ArrayList<>();
                        listModelList2.add(listModel.get(position));
                        callPlayer(0, listModelList2, true);
                    }
                } else {
                    ArrayList<SuggestedModel.ResponseData> listModelList2 = new ArrayList<>();
                    Gson gson = new Gson();
                    SharedPreferences shared12 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE);
                    String IsPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1");
                    String DisclimerJson = shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString());
                    Type type = new TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio>() {
                    }.getType();
                    HomeScreenModel.ResponseData.DisclaimerAudio arrayList = gson.fromJson(DisclimerJson, type);
                    SuggestedModel.ResponseData mainPlayModel = new SuggestedModel.ResponseData();
                    mainPlayModel.setID(arrayList.getId());
                    mainPlayModel.setName(arrayList.getName());
                    mainPlayModel.setAudioFile(arrayList.getAudioFile());
                    mainPlayModel.setAudioDirection(arrayList.getAudioDirection());
                    mainPlayModel.setAudiomastercat(arrayList.getAudiomastercat());
                    mainPlayModel.setAudioSubCategory(arrayList.getAudioSubCategory());
                    mainPlayModel.setImageFile(arrayList.getImageFile());
                    mainPlayModel.setAudioDuration(arrayList.getAudioDuration());
                    boolean audioc = false;
                    if (isDisclaimer == 1) {
                        if (player != null) {
                            player.setPlayWhenReady(true);
                            audioc = false;
                            listModelList2.add(mainPlayModel);
                        } else {
                            isDisclaimer = 0;
                            if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                                audioc = true;
                                listModelList2.add(mainPlayModel);
                            }
                        }
                    } else {
                        isDisclaimer = 0;
                        if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                            audioc = true;
                            listModelList2.add(mainPlayModel);
                        }
                    }
                    listModelList2.add(listModel.get(position));
                    callPlayer(0, listModelList2, audioc);
                }
                notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void callMyPlayer() {
            Intent i = new Intent(ctx, MyPlayerActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            ctx.startActivity(i);
            activity.overridePendingTransition(0, 0);
        }

        private void callPlayer(int position, ArrayList<SuggestedModel.ResponseData> listModel, boolean audioc) {
            if (audioc) {
                callNewPlayerRelease();
            }
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, AppCompatActivity.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String json = gson.toJson(listModel);
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json);
            editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
            editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
            editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "");
            editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "Recommended Search");
            editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "SearchAudio");
            editor.apply();
            audioClick = audioc;
            callMyPlayer();
        }

        @Override
        public int getItemCount() {
            if (10 > listModel.size()) {
                return listModel.size();
            } else {
                return 10;
            }
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            DownloadsLayoutBinding binding;

            public MyViewHolder(DownloadsLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    public class SuggestedPlayListsAdpater extends RecyclerView.Adapter<SuggestedPlayListsAdpater.MyViewHolder> {
        private List<SearchPlaylistModel.ResponseData> PlaylistModel;

        public SuggestedPlayListsAdpater(List<SearchPlaylistModel.ResponseData> PlaylistModel) {
            this.PlaylistModel = PlaylistModel;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            DownloadsLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.downloads_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvTitle.setText(PlaylistModel.get(position).getName());
            holder.binding.pbProgress.setVisibility(View.GONE);
            holder.binding.equalizerview.setVisibility(View.GONE);
            if (PlaylistModel.get(position).getTotalAudio().equalsIgnoreCase("") ||
                    PlaylistModel.get(position).getTotalAudio().equalsIgnoreCase("0") &&
                            PlaylistModel.get(position).getTotalhour().equalsIgnoreCase("")
                            && PlaylistModel.get(position).getTotalminute().equalsIgnoreCase("")) {
                holder.binding.tvTime.setText("0 Audio | 0h 0m");
            } else {
                if (PlaylistModel.get(position).getTotalminute().equalsIgnoreCase("")) {
                    holder.binding.tvTime.setText(PlaylistModel.get(position).getTotalAudio() + " Audio | "
                            + PlaylistModel.get(position).getTotalhour() + "h 0m");
                } else {
                    holder.binding.tvTime.setText(PlaylistModel.get(position).getTotalAudio() +
                            " Audios | " + PlaylistModel.get(position).getTotalhour() + "h " + PlaylistModel.get(position).getTotalminute() + "m");
                }
            }

            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.cvImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.cvImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.binding.ivBackgroundImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(PlaylistModel.get(position).getImage()).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            Glide.with(ctx).load(R.drawable.ic_image_bg).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivBackgroundImage);
            holder.binding.ivIcon.setImageResource(R.drawable.ic_add_two_icon);
//            if (PlaylistModel.get(position).isLock().equalsIgnoreCase("1")) {
//                holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                holder.binding.ivLock.setVisibility(View.VISIBLE);
//            } else if (PlaylistModel.get(position).isLock().equalsIgnoreCase("2")) {
//                holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                holder.binding.ivLock.setVisibility(View.VISIBLE);
//            } else if (PlaylistModel.get(position).isLock().equalsIgnoreCase("0") || PlaylistModel.get(position).isLock().equalsIgnoreCase("")) {
            holder.binding.ivBackgroundImage.setVisibility(View.GONE);
            holder.binding.ivLock.setVisibility(View.GONE);
//            }

            holder.binding.llMainLayout.setOnClickListener(view -> {
//                if (PlaylistModel.get(position).isLock().equalsIgnoreCase("1")) {
//                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                    holder.binding.ivLock.setVisibility(View.VISIBLE);
//                    Intent i = new Intent(ctx, MembershipChangeActivity.class);
//                    i.putExtra("ComeFrom", "Plan");
//                    startActivity(i);
//                } else if (PlaylistModel.get(position).isLock().equalsIgnoreCase("1")) {
//                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                    holder.binding.ivLock.setVisibility(View.VISIBLE);
//                    BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
//                } else if (PlaylistModel.get(position).isLock().equalsIgnoreCase("0") || PlaylistModel.get(position).isLock().equalsIgnoreCase("")) {
                holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                holder.binding.ivLock.setVisibility(View.GONE);
                comefromDownload = "0";
                addToSearch = true;
                MyPlaylistIds = PlaylistModel.get(position).getID();
                PlaylistIDMS = PlaylistID;
                finish();
                    /*Fragment myPlaylistsFragment = new MyPlaylistsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("New", "0");
                    bundle.putString("PlaylistID", PlaylistModel.get(position).getID());
                    bundle.putString("PlaylistName", PlaylistModel.get(position).getName());
                    bundle.putString("MyDownloads", "0");
                    myPlaylistsFragment.setArguments(bundle);
                    FragmentManager fragmentManager1 = getSupportFragmentManager();
                    fragmentManager1.beginTransaction()
                            .replace(R.id.flContainer, myPlaylistsFragment)
                            .commit();*/
//                }
            });

            holder.binding.llRemoveAudio.setOnClickListener(view -> {
//                if (PlaylistModel.get(position).isLock().equalsIgnoreCase("1")) {
//                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                    holder.binding.ivLock.setVisibility(View.VISIBLE);
//                    Intent i = new Intent(ctx, MembershipChangeActivity.class);
//                    i.putExtra("ComeFrom", "Plan");
//                    startActivity(i);
//                } else if (PlaylistModel.get(position).isLock().equalsIgnoreCase("2")) {
//                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                    holder.binding.ivLock.setVisibility(View.VISIBLE);
//                    BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
//                } else if (PlaylistModel.get(position).isLock().equalsIgnoreCase("0") || PlaylistModel.get(position).isLock().equalsIgnoreCase("")) {
                holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                holder.binding.ivLock.setVisibility(View.GONE);
                comefromDownload = "0";
                SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
                String pID = shared.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "0");
                if (AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                    if (isDisclaimer == 1) {
                        BWSApplication.showToast("The audio shall add after playing the disclaimer", activity);
                    } else {
                        callAddSearchAudio("", "1", PlaylistModel.get(position).getID());
                    }
                } else {
                    callAddSearchAudio("", "1", PlaylistModel.get(position).getID());
                }
//                }
            });
        }

        @Override
        public int getItemCount() {
            if (10 > PlaylistModel.size()) {
                return PlaylistModel.size();
            } else {
                return 10;
            }
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            DownloadsLayoutBinding binding;

            public MyViewHolder(DownloadsLayoutBinding binding) {
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
                    notificationStatus = false;
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
                if (!notificationStatus) {
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(notificationId);
                    relesePlayer(getApplicationContext());
                }
            } else {
                Log.e("Destroy", "Activity go in main activity");
            }
        }
    }
}