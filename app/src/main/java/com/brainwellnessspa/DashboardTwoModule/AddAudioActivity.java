package com.brainwellnessspa.DashboardTwoModule;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Activities.MembershipChangeActivity;
import com.brainwellnessspa.DashboardTwoModule.Model.AddToPlaylistModel;
import com.brainwellnessspa.DashboardTwoModule.Model.SearchBothModel;
import com.brainwellnessspa.DashboardTwoModule.Model.SearchPlaylistModel;
import com.brainwellnessspa.DashboardModule.Models.SubPlayListModel;
import com.brainwellnessspa.DashboardTwoModule.Model.SuggestedModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Services.GlobalInitExoPlayer;
import com.brainwellnessspa.Utility.APINewClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.ActivityAddAudioBinding;
import com.brainwellnessspa.databinding.DownloadsLayoutBinding;
import com.brainwellnessspa.databinding.GlobalSearchLayoutBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.segment.analytics.Properties;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.audioClick;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.miniPlayer;
import static com.brainwellnessspa.DashboardModule.Activities.MyPlaylistActivity.comeRename;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.myAudioId;
import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.callNewPlayerRelease;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.notificationId;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.player;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.relesePlayer;

public class AddAudioActivity extends AppCompatActivity {
    public static boolean addToSearch = false;
    public static String MyPlaylistIds = "";
    public static String PlaylistIDMS = "";
    ActivityAddAudioBinding binding;
    Context ctx;
    String CoUSERID, USERID, UserName, PlaylistID = "", AudioFlag, IsPlayDisclimer;
    SerachListAdpater serachListAdpater;
    EditText searchEditText;
    Activity activity;
    int listSize = 0;
    SuggestedAdpater suggestedAdpater;
    //    Handler handler3;
    Properties p;
    private int numStarted = 0;
    int stackStatus = 0;
    boolean myBackPress = false;
    boolean notificationStatus = false;
    //    private Runnable UpdateSongTime3;
    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("MyData")) {
                String data = intent.getStringExtra("MyData");
                Log.d("play_pause_Action", data);
                SharedPreferences sharedzw = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                boolean audioPlayz = sharedzw.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                AudioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pIDz = sharedzw.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                if (!AudioFlag.equalsIgnoreCase("Downloadlist") && !AudioFlag.equalsIgnoreCase("SubPlayList") && !AudioFlag.equalsIgnoreCase("TopCategories")) {
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
        USERID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "");
        CoUSERID = shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "");
        UserName = shared1.getString(CONSTANTS.PREFE_ACCESS_NAME, "");

        notificationStatus = false;
        /*ArrayList<String> section = new ArrayList<>();
        section.add("Recommended Audios");
        section.add("Recommended Playlists");
        Gson gson;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        p = new Properties();
        p.putValue("userId", CoUSERID);
        p.putValue("source", "Search Screen");
        p.putValue("sections", gson.toJson(section));
        BWSApplication.addToSegment("Search Screen Viewed", p, CONSTANTS.screen);*/

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
               /* p = new Properties();
                p.putValue("userId", CoUSERID);
                p.putValue("source", "Add Audio Screen");
                p.putValue("searchKeyword", search);
                BWSApplication.addToSegment("Audio/Playlist Searched", p, CONSTANTS.track);*/
                return false;
            }
        });

        binding.llBack.setOnClickListener(view -> {
            callback();
        });
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
        comeRename = 1;
        finish();
    }

    private void prepareSearchData(String search, EditText searchEditText) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<SearchBothModel> listCall = APINewClient.getClient().getSearchBoth(CoUSERID, search);
            listCall.enqueue(new Callback<SearchBothModel>() {
                @Override
                public void onResponse(Call<SearchBothModel> call, Response<SearchBothModel> response) {
                    try {
                        SearchBothModel listModel = response.body();
                        if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            if (!searchEditText.getText().toString().equalsIgnoreCase("")) {
                                if (listModel.getResponseData().size() == 0) {
                                    binding.rvSerachList.setVisibility(View.GONE);
                                    binding.llError.setVisibility(View.VISIBLE);
                                    binding.tvFound.setText("Couldn't find '" + search + "'. Try searching again");
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
                public void onFailure(Call<SearchBothModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    private void prepareSuggestedData() {
        SharedPreferences shareddes = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shareddes.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        try {
            GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
            globalInitExoPlayer.UpdateMiniPlayer(ctx);
            if (!AudioFlag.equalsIgnoreCase("0")) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 6, 0, 260);
                binding.llSpace.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 6, 0, 50);
                binding.llSpace.setLayoutParams(params);
            }
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            if (!AudioFlag.equalsIgnoreCase("0")) {
                comefromDownload = "1";
                callAddFrag();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(0, 8, 0, 210);
                binding.llSpace.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(0, 8, 0, 20);
                binding.llSpace.setLayoutParams(params);
            }
            /*
            SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            AudioFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");

            SharedPreferences shared2 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
            String UnlockAudioLists = shared2.getString(CONSTANTS.PREF_KEY_UnLockAudiList, "");
            Gson gson1 = new Gson();
            Type type1 = new TypeToken<List<String>>() {
            }.getType();
            List<String> UnlockAudioList = gson1.fromJson(UnlockAudioLists, type1);
            if (!IsLock.equalsIgnoreCase("0") && (AudioFlag.equalsIgnoreCase("MainAudioList")
                    || AudioFlag.equalsIgnoreCase("ViewAllAudioList"))) {
                String audioID = "";
                SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
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
                    SharedPreferences sharedm = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
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
                SharedPreferences sharedm = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
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
            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<SuggestedModel> listCall = APINewClient.getClient().getSuggestedLists(CoUSERID);
            listCall.enqueue(new Callback<SuggestedModel>() {
                @Override
                public void onResponse(Call<SuggestedModel> call, Response<SuggestedModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            SuggestedModel listModel = response.body();
                            binding.tvSuggestedAudios.setText(R.string.Recommended_Audios);
                            binding.tvSAViewAll.setVisibility(View.VISIBLE);
                            suggestedAdpater = new SuggestedAdpater(listModel.getResponseData(), ctx);
                            binding.rvSuggestedList.setAdapter(suggestedAdpater);

                            LocalBroadcastManager.getInstance(ctx)
                                    .registerReceiver(listener, new IntentFilter("play_pause_Action"));
                            binding.tvSAViewAll.setOnClickListener(view -> {
                                notificationStatus = true;
                                Intent i = new Intent(ctx, ViewSuggestedActivity.class);
                                i.putExtra("Name", "Recommended  Audios");
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
                public void onFailure(Call<SuggestedModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }

        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<SearchPlaylistModel> listCall = APINewClient.getClient().getSuggestedPlayLists(CoUSERID);
            listCall.enqueue(new Callback<SearchPlaylistModel>() {
                @Override
                public void onResponse(Call<SearchPlaylistModel> call, Response<SearchPlaylistModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            SearchPlaylistModel listModel = response.body();
                            binding.tvSuggestedPlaylist.setText(R.string.Recommendeds_Playlist);
                            /*p = new Properties();
                            p.putValue("userId", UserID);
                            p.putValue("source", "Add Audio Screen");
                            BWSApplication.addToSegment("Recommended Playlists List Viewed", p, CONSTANTS.screen);*/
                            binding.tvSPViewAll.setVisibility(View.VISIBLE);

                            SuggestedPlayListsAdpater suggestedAdpater = new SuggestedPlayListsAdpater(listModel.getResponseData());
                            binding.rvPlayList.setAdapter(suggestedAdpater);

                            binding.tvSPViewAll.setOnClickListener(view -> {
                                notificationStatus = true;
                                Intent i = new Intent(ctx, ViewSuggestedActivity.class);
                                i.putExtra("Name", "Recommended Playlist");
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
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
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
                public void onResponse(Call<AddToPlaylistModel> call, Response<AddToPlaylistModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            AddToPlaylistModel listModels = response.body();
                            if (listModels.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                BWSApplication.showToast(listModels.getResponseMessage(), ctx);
                                SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                                String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                                int pos = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
                                String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                                if (audioPlay) {
                                    if (AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                                        Gson gsonx = new Gson();
                                        String json = shared.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gsonx));
                                        Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                                        }.getType();
                                        ArrayList<MainPlayModel> mainPlayModelListold = new ArrayList<>();
                                        mainPlayModelListold = gsonx.fromJson(json, type);
                                        String id = mainPlayModelListold.get(pos).getID();
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
                                            mainPlayModel.setLike(listModels.getResponseData().get(i).getLike());
                                            mainPlayModel.setDownload(listModels.getResponseData().get(i).getDownload());
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
                                                pos = i;
                                                break;
                                            }
                                        }
                                        SharedPreferences sharedd = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedd.edit();
                                        Gson gson = new Gson();
                                        String jsonx = gson.toJson(mainPlayModelList);
                                        String json11 = gson.toJson(playlistSongs);
                                        editor.putString(CONSTANTS.PREF_KEY_modelList, json11);
                                        editor.putString(CONSTANTS.PREF_KEY_audioList, jsonx);
                                        editor.putInt(CONSTANTS.PREF_KEY_position, pos);
                                        editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                                        editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                                        editor.putString(CONSTANTS.PREF_KEY_PlaylistId, PlaylistID);
                                        editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "myPlaylist");
                                        editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SubPlayList");
                                        editor.commit();

                                        if (!mainPlayModelList.get(pos).getAudioFile().equals("")) {
                                            List<String> downloadAudioDetailsList = new ArrayList<>();
                                            GlobalInitExoPlayer ge = new GlobalInitExoPlayer();
                                            ge.AddAudioToPlayer(size, mainPlayModelList, downloadAudioDetailsList, ctx);
                                        }
                                        callAddFrag();

                                    }
                                }
                                if (s.equalsIgnoreCase("1")) {
                                    finish();
                                }
                            } else if (listModels.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodefail))) {
                                BWSApplication.showToast(listModels.getResponseMessage(), ctx);
                            }

                        }
                    } catch (Exception e) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<AddToPlaylistModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(ctx.getString(R.string.no_server_found), ctx);
        }
    }

    private void callAddFrag() {
        Fragment fragment = new MiniPlayerFragment();
        FragmentManager fragmentManager1 = getSupportFragmentManager();
        fragmentManager1.beginTransaction()
                .add(R.id.flContainer, fragment)
                .commit();
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
            holder.binding.equalizerview.setVisibility(View.GONE);

            if (modelList.get(position).getIscategory().equalsIgnoreCase("1")) {
                holder.binding.tvPart.setText(R.string.Audio);
                holder.binding.llRemoveAudio.setVisibility(View.VISIBLE);
                holder.binding.equalizerview.setVisibility(View.GONE);
//                if (modelList.get(position).isLock().equalsIgnoreCase("1")) {
//                    if (modelList.get(position).isPlay().equalsIgnoreCase("1")) {
//                        holder.binding.ivBackgroundImage.setVisibility(View.GONE);
//                        holder.binding.ivLock.setVisibility(View.GONE);
//                    } else if (modelList.get(position).isPlay().equalsIgnoreCase("0")
//                            || modelList.get(position).isPlay().equalsIgnoreCase("")) {
//                        holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                        holder.binding.ivLock.setVisibility(View.VISIBLE);
//                    }
//                } else if (modelList.get(position).isLock().equalsIgnoreCase("2")) {
//                    if (modelList.get(position).isPlay().equalsIgnoreCase("1")) {
//                        holder.binding.ivBackgroundImage.setVisibility(View.GONE);
//                        holder.binding.ivLock.setVisibility(View.GONE);
//                    } else if (modelList.get(position).isPlay().equalsIgnoreCase("0")
//                            || modelList.get(position).isPlay().equalsIgnoreCase("")) {
//                        holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                        holder.binding.ivLock.setVisibility(View.VISIBLE);
//                    }
//                } else if (modelList.get(position).isLock().equalsIgnoreCase("0") || modelList.get(position).isLock().equalsIgnoreCase("")) {
                    holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                    holder.binding.ivLock.setVisibility(View.GONE);
//                }
                SharedPreferences sharedzw = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                boolean audioPlayz = sharedzw.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                AudioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pIDz = sharedzw.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                if (!AudioFlag.equalsIgnoreCase("Downloadlist") &&
                        !AudioFlag.equalsIgnoreCase("SubPlayList")
                        && !AudioFlag.equalsIgnoreCase("TopCategories")) {
                    if (myAudioId.equalsIgnoreCase(modelList.get(position).getID())) {
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
                        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                        boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                        String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                        String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                        if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                            if (isDisclaimer == 1) {
                                BWSApplication.showToast("The audio shall add after playing the disclaimer", ctx);
                            } else {
                                callAddSearchAudio(AudioID, "0", "");
                            }
                        } else {
                            callAddSearchAudio(AudioID, "0", "");
                        }
//                    }
                });
            } else if (modelList.get(position).getIscategory().equalsIgnoreCase("0")) {
                holder.binding.tvPart.setText(R.string.Playlist);
                holder.binding.equalizerview.setVisibility(View.GONE);
                holder.binding.llRemoveAudio.setVisibility(View.VISIBLE);
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
                        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                        boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                        String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                        String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                        if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                            if (isDisclaimer == 1) {
                                BWSApplication.showToast("The audio shall add after playing the disclaimer", ctx);
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
                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String MyPlaylist = shared.getString(CONSTANTS.PREF_KEY_myPlaylist, "");
                SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                IsPlayDisclimer = (shared1.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1"));
                if (audioPlay && (AudioFlag.equalsIgnoreCase("SearchModelAudio")
                        && MyPlaylist.equalsIgnoreCase("Search Audio"))) {
                    if (isDisclaimer == 1) {
                        if (player != null) {
                            if (!player.getPlayWhenReady()) {
                                player.setPlayWhenReady(true);
                            }
                        } else {
                            audioClick = true;
                            miniPlayer = 1;
                        }
                        callAddFrag();
                        BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                    } else {
                        ArrayList<SearchBothModel.ResponseData> listModelList2 = new ArrayList<>();
                        listModelList2.add(modelList.get(position));
                        callTransFrag(0, listModelList2, true);
                    }
                } else {
                    ArrayList<SearchBothModel.ResponseData> listModelList2 = new ArrayList<>();
                    listModelList2.add(modelList.get(position));
                    SearchBothModel.ResponseData mainPlayModel = new SearchBothModel.ResponseData();
                    mainPlayModel.setID("0");
                    mainPlayModel.setName("Disclaimer");
                    mainPlayModel.setAudioFile("");
                    mainPlayModel.setAudioDirection("The audio shall start playing after the disclaimer");
                    mainPlayModel.setAudiomastercat("");
                    mainPlayModel.setAudioSubCategory("");
                    mainPlayModel.setImageFile("");
                    mainPlayModel.setLike("");
                    mainPlayModel.setDownload("");
                    mainPlayModel.setAudioDuration("00:48");
                    boolean audioc = true;
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
                    callTransFrag(0, listModelList2, audioc);
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(0, 8, 0, 210);
                binding.llSpace.setLayoutParams(params);
                notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void callTransFrag(int position, ArrayList<SearchBothModel.ResponseData> listModelList, boolean audioc) {
            try {
                miniPlayer = 1;
                audioClick = audioc;
                if (audioc) {
                    callNewPlayerRelease();
                }
                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                Gson gson = new Gson();

                String json = gson.toJson(listModelList);
                editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                editor.putInt(CONSTANTS.PREF_KEY_position, 0);
                editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "Search Audio");
                editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SearchModelAudio");
                editor.commit();
                callAddFrag();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            holder.binding.equalizerview.setVisibility(View.GONE);
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
            SharedPreferences sharedzw = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            boolean audioPlayz = sharedzw.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
            AudioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            String pIDz = sharedzw.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
            if (!AudioFlag.equalsIgnoreCase("Downloadlist") &&
                    !AudioFlag.equalsIgnoreCase("SubPlayList") && !AudioFlag.equalsIgnoreCase("TopCategories")) {
                if (myAudioId.equalsIgnoreCase(listModel.get(position).getID())) {
                    songId = myAudioId;
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
                    String AudioID = listModel.get(position).getID();
                    SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                    boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                    String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                    String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                    if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                        if (isDisclaimer == 1) {
                            BWSApplication.showToast("The audio shall add after playing the disclaimer", ctx);
                        } else {
                            callAddSearchAudio(AudioID, "0", "");
                        }
                    } else {
                        callAddSearchAudio(AudioID, "0", "");
                    }
//                }
            });
        }

        public void callMainTransFrag(int position) {
            try {
                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String MyPlaylist = shared.getString(CONSTANTS.PREF_KEY_myPlaylist, "");
                SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                IsPlayDisclimer = (shared1.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1"));
                if (audioPlay && (AudioFlag.equalsIgnoreCase("SearchAudio")
                        && MyPlaylist.equalsIgnoreCase("Recommended Search Audio"))) {
                    if (isDisclaimer == 1) {
                        if (player != null) {
                            if (!player.getPlayWhenReady()) {
                                player.setPlayWhenReady(true);
                            }
                        } else {
                            audioClick = true;
                            miniPlayer = 1;
                        }
                        callAddFrag();
                        BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                    } else {
                        ArrayList<SuggestedModel.ResponseData> listModelList2 = new ArrayList<>();
                        listModelList2.add(listModel.get(position));
                        callTransFrag(0, listModelList2, true);
                    }
                } else {
                    ArrayList<SuggestedModel.ResponseData> listModelList2 = new ArrayList<>();
                    listModelList2.add(listModel.get(position));
                    SuggestedModel.ResponseData mainPlayModel = new SuggestedModel.ResponseData();
                    mainPlayModel.setID("0");
                    mainPlayModel.setName("Disclaimer");
                    mainPlayModel.setAudioFile("");
                    mainPlayModel.setAudioDirection("The audio shall start playing after the disclaimer");
                    mainPlayModel.setAudiomastercat("");
                    mainPlayModel.setAudioSubCategory("");
                    mainPlayModel.setImageFile("");
                    mainPlayModel.setLike("");
                    mainPlayModel.setDownload("");
                    mainPlayModel.setAudioDuration("00:48");
                    boolean audioc = true;
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
                    callTransFrag(0, listModelList2, audioc);
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(0, 8, 0, 210);
                binding.llSpace.setLayoutParams(params);
                notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void callTransFrag(int position, ArrayList<SuggestedModel.ResponseData> listModelList, boolean audioc) {
            try {
                miniPlayer = 1;
                audioClick = audioc;
                if (audioc) {
                    callNewPlayerRelease();
                }
                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                Gson gson = new Gson();

                String json = gson.toJson(listModelList);
                editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                editor.putInt(CONSTANTS.PREF_KEY_position, 0);
                editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "Recommended Search Audio");
                editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SearchAudio");
                editor.commit();
                callAddFrag();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                    SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                    boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                    String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                    String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                    if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                        if (isDisclaimer == 1) {
                            BWSApplication.showToast("The audio shall add after playing the disclaimer", ctx);
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
                if (!notificationStatus){
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