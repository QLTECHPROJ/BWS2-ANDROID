package com.brainwellnessspa.DashboardModule.Activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Activities.MembershipChangeActivity;
import com.brainwellnessspa.DashboardModule.Models.AddToPlaylist;
import com.brainwellnessspa.DashboardModule.Models.SearchPlaylistModel;
import com.brainwellnessspa.DashboardModule.Models.SubPlayListModel;
import com.brainwellnessspa.DashboardModule.Models.SuggestedModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.Utility.MusicService;
import com.brainwellnessspa.databinding.ActivityViewSuggestedBinding;
import com.brainwellnessspa.databinding.DownloadsLayoutBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.player;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment.myAudioId;
import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;
import static com.brainwellnessspa.Utility.MusicService.getStartTime;
import static com.brainwellnessspa.Utility.MusicService.isCompleteStop;
import static com.brainwellnessspa.Utility.MusicService.isMediaStart;
import static com.brainwellnessspa.Utility.MusicService.isPause;
import static com.brainwellnessspa.Utility.MusicService.isPrepare;
import static com.brainwellnessspa.DashboardModule.Activities.AddAudioActivity.addToSearch;
import static com.brainwellnessspa.DashboardModule.Activities.AddAudioActivity.MyPlaylistIds;
import static com.brainwellnessspa.DashboardModule.Activities.AddAudioActivity.PlaylistIDMS;

public class ViewSuggestedActivity extends AppCompatActivity {
    ActivityViewSuggestedBinding binding;
    Activity activity;
    Context ctx;
    String UserID, AudioFlag, Name, PlaylistID;
    ArrayList<SuggestedModel.ResponseData> AudiolistsModel;
    ArrayList<SearchPlaylistModel.ResponseData> PlaylistModel;
    int startTime;
    AudiosListAdpater adpater;
    private long currentDuration = 0;
    long myProgress = 0, diff = 0;
    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("MyData")) {
                String data = intent.getStringExtra("MyData");
                Log.d("play_pause_Action", data);
                SharedPreferences sharedzw = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                boolean audioPlayz = sharedzw.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                AudioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pIDz = sharedzw.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                if (AudioFlag.equalsIgnoreCase("Downloadlist") &&
                        !AudioFlag.equalsIgnoreCase("SubPlayList") &&
                        !AudioFlag.equalsIgnoreCase("TopCategories")) {
                    if (isMediaStart) {
                        adpater.notifyDataSetChanged();
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_suggested);
        ctx = ViewSuggestedActivity.this;
        activity = ViewSuggestedActivity.this;
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        binding.llBack.setOnClickListener(view -> {
            Intent i = new Intent(ctx, AddAudioActivity.class);
            i.putExtra("PlaylistID", PlaylistID);
            startActivity(i);
            finish();
        });

        if (getIntent() != null) {
            Name = getIntent().getStringExtra("Name");
        }
        if (getIntent() != null) {
            PlaylistID = getIntent().getStringExtra(CONSTANTS.PlaylistID);
        }
        if (getIntent() != null) {
            AudiolistsModel = getIntent().getParcelableArrayListExtra("AudiolistModel");
        }
        if (getIntent() != null) {
            PlaylistModel = getIntent().getParcelableArrayListExtra("PlaylistModel");
        }
        PrepareData();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(ViewSuggestedActivity.this).unregisterReceiver(listener);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ctx, AddAudioActivity.class);
        i.putExtra("PlaylistID", PlaylistID);
        startActivity(i);
        finish();
    }

    public void PrepareData() {
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        if (!AudioFlag.equalsIgnoreCase("0")) {
            comefromDownload = "1";
            Fragment fragment = new TransparentPlayerFragment();
            FragmentManager fragmentManager1 = getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .add(R.id.flContainer, fragment)
                    .commit();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(0, 8, 0, 210);
            binding.llSpace.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(0, 8, 0, 20);
            binding.llSpace.setLayoutParams(params);
        }
        binding.tvTitle.setText(Name);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        binding.rvMainAudio.setLayoutManager(layoutManager);
        binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
        if (Name.equalsIgnoreCase("Recommended  Audios")) {
            adpater = new AudiosListAdpater(AudiolistsModel);
            LocalBroadcastManager.getInstance(ViewSuggestedActivity.this)
                    .registerReceiver(listener, new IntentFilter("play_pause_Action"));
            binding.rvMainAudio.setAdapter(adpater);
        } else if (Name.equalsIgnoreCase("Recommended Playlist")) {
            SuggestionPlayListsAdpater adpater = new SuggestionPlayListsAdpater(PlaylistModel);
            binding.rvMainAudio.setAdapter(adpater);
        }
    }

    private void callAddAudioToPlaylist(String AudioID, String FromPlaylistId, String s1) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<AddToPlaylist> listCall = APIClient.getClient().getAddSearchAudioFromPlaylist(UserID, AudioID, PlaylistID, FromPlaylistId);
            listCall.enqueue(new Callback<AddToPlaylist>() {
                @Override
                public void onResponse(Call<AddToPlaylist> call, Response<AddToPlaylist> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            AddToPlaylist listModels = response.body();
                            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
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
                                    String json1 = gson.toJson(playlistSongs);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json1);
                                    editor.putString(CONSTANTS.PREF_KEY_audioList, jsonx);
                                    editor.putInt(CONSTANTS.PREF_KEY_position, pos);
                                    editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                                    editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                                    editor.putString(CONSTANTS.PREF_KEY_PlaylistId, PlaylistID);
                                    editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "myPlaylist");
                                    editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SubPlayList");
                                    editor.commit();
                                }
                            }
                            BWSApplication.showToast(listModels.getResponseMessage(), ctx);
                            if (s1.equalsIgnoreCase("1")) {
                                finish();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<AddToPlaylist> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    public class AudiosListAdpater extends RecyclerView.Adapter<AudiosListAdpater.MyViewHolder> {
        private ArrayList<SuggestedModel.ResponseData> AudiolistsModel;
        String songId;

        public AudiosListAdpater(ArrayList<SuggestedModel.ResponseData> AudiolistsModel) {
            this.AudiolistsModel = AudiolistsModel;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            DownloadsLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.downloads_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binds.tvTitle.setText(AudiolistsModel.get(position).getName());
            holder.binds.tvTime.setText(AudiolistsModel.get(position).getAudioDuration());
            holder.binds.pbProgress.setVisibility(View.GONE);
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binds.cvImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binds.cvImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            Glide.with(ctx).load(AudiolistsModel.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binds.ivRestaurantImage);
            holder.binds.ivIcon.setImageResource(R.drawable.add_icon);
            holder.binds.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg);
            SharedPreferences sharedzw = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            boolean audioPlayz = sharedzw.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
            AudioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            String pIDz = sharedzw.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
            if (!AudioFlag.equalsIgnoreCase("Downloadlist") &&
                    !AudioFlag.equalsIgnoreCase("SubPlayList") &&
                    !AudioFlag.equalsIgnoreCase("TopCategories")) {
                if (myAudioId.equalsIgnoreCase(AudiolistsModel.get(position).getID())) {
                    songId = myAudioId;
                    if (isPause || !isMediaStart) {
                        holder.binds.equalizerview.stopBars();
                    } else
                        holder.binds.equalizerview.animateBars();
                    holder.binds.equalizerview.setVisibility(View.VISIBLE);
                    holder.binds.llMainLayout.setBackgroundResource(R.color.highlight_background);
                    holder.binds.ivBackgroundImage.setVisibility(View.VISIBLE);
                } else {
                    holder.binds.equalizerview.setVisibility(View.GONE);
                    holder.binds.llMainLayout.setBackgroundResource(R.color.white);
                    holder.binds.ivBackgroundImage.setVisibility(View.GONE);
                }
            } else {
                holder.binds.equalizerview.setVisibility(View.GONE);
                holder.binds.llMainLayout.setBackgroundResource(R.color.white);
                holder.binds.ivBackgroundImage.setVisibility(View.GONE);
            }


            if (AudiolistsModel.get(position).getIsLock().equalsIgnoreCase("1")) {
                if (AudiolistsModel.get(position).getIsPlay().equalsIgnoreCase("1")) {
                    holder.binds.ivLock.setVisibility(View.GONE);
                } else if (AudiolistsModel.get(position).getIsPlay().equalsIgnoreCase("0")
                        || AudiolistsModel.get(position).getIsPlay().equalsIgnoreCase("")) {
                    holder.binds.ivLock.setVisibility(View.VISIBLE);
                }
            } else if (AudiolistsModel.get(position).getIsLock().equalsIgnoreCase("0")
                    || AudiolistsModel.get(position).getIsLock().equalsIgnoreCase("")) {
                holder.binds.ivLock.setVisibility(View.GONE);
            }

            holder.binds.llMainLayoutForPlayer.setOnClickListener(view -> {
                if (AudiolistsModel.get(position).getIsLock().equalsIgnoreCase("1")) {
                    if (AudiolistsModel.get(position).getIsPlay().equalsIgnoreCase("1")) {
                        holder.binds.ivLock.setVisibility(View.GONE);
                        try {
                            player = 1;
                            if (isPrepare || isMediaStart || isPause) {
                                MusicService.stopMedia();
                            }
                            isPause = false;
                            isMediaStart = false;
                            isPrepare = false;
                            isCompleteStop = false;
                            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared.edit();
                            Gson gson = new Gson();
                            ArrayList<SuggestedModel.ResponseData> listModelList2 = new ArrayList<>();
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
                            listModelList2.add(mainPlayModel);
                            listModelList2.add(AudiolistsModel.get(position));
                            String json = gson.toJson(listModelList2);
                            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                            editor.putInt(CONSTANTS.PREF_KEY_position, 0);
                            editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                            editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                            editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                            editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
                            editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SearchAudio");
                            editor.commit();
                            Fragment fragment = new TransparentPlayerFragment();
                            FragmentManager fragmentManager1 = getSupportFragmentManager();
                            fragmentManager1.beginTransaction()
                                    .add(R.id.flContainer, fragment).commit();
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            params.setMargins(0, 8, 0, 210);
                            binding.llSpace.setLayoutParams(params);
                            notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (AudiolistsModel.get(position).getIsPlay().equalsIgnoreCase("0")
                            || AudiolistsModel.get(position).getIsPlay().equalsIgnoreCase("")) {
                        holder.binds.ivLock.setVisibility(View.VISIBLE);
                    }
                } else if (AudiolistsModel.get(position).getIsLock().equalsIgnoreCase("0")
                        || AudiolistsModel.get(position).getIsLock().equalsIgnoreCase("")) {
                    holder.binds.ivLock.setVisibility(View.GONE);
                    try {
                        player = 1;
                        if (isPrepare || isMediaStart || isPause) {
                            MusicService.stopMedia();
                        }
                        isPause = false;
                        isMediaStart = false;
                        isPrepare = false;
                        isCompleteStop = false;
                        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = shared.edit();
                        Gson gson = new Gson();
                        ArrayList<SuggestedModel.ResponseData> listModelList2 = new ArrayList<>();
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
                        listModelList2.add(mainPlayModel);
                        listModelList2.add(AudiolistsModel.get(position));
                        String json = gson.toJson(listModelList2);
                        editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                        editor.putInt(CONSTANTS.PREF_KEY_position, 0);
                        editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                        editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                        editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                        editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
                        editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SearchAudio");
                        editor.commit();
                        Fragment fragment = new TransparentPlayerFragment();
                        FragmentManager fragmentManager1 = getSupportFragmentManager();
                        fragmentManager1.beginTransaction()
                                .add(R.id.flContainer, fragment).commit();
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        params.setMargins(0, 8, 0, 210);
                        binding.llSpace.setLayoutParams(params);
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.binds.llRemoveAudio.setOnClickListener(view -> {
                if (AudiolistsModel.get(position).getIsLock().equalsIgnoreCase("1")) {
                    if (AudiolistsModel.get(position).getIsPlay().equalsIgnoreCase("1")) {
                        holder.binds.ivLock.setVisibility(View.GONE);
                        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                        boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                        String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                        String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                        if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                            if (isDisclaimer == 1) {
                                BWSApplication.showToast("The audio shall add after playing the disclaimer", ctx);
                            } else {
                                callAddAudioToPlaylist(AudiolistsModel.get(position).getID(), "", "0");
                            }
                        } else {
                            callAddAudioToPlaylist(AudiolistsModel.get(position).getID(), "", "0");
                        }
                    } else if (AudiolistsModel.get(position).getIsPlay().equalsIgnoreCase("0")
                            || AudiolistsModel.get(position).getIsPlay().equalsIgnoreCase("")) {
                        holder.binds.ivLock.setVisibility(View.VISIBLE);
                        Intent i = new Intent(ctx, MembershipChangeActivity.class);
                        i.putExtra("ComeFrom", "Plan");
                        startActivity(i);
                    }
                } else if (AudiolistsModel.get(position).getIsLock().equalsIgnoreCase("2")) {
                    if (AudiolistsModel.get(position).getIsPlay().equalsIgnoreCase("1")) {
                        holder.binds.ivLock.setVisibility(View.GONE);
                        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                        boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                        String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                        String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                        if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                            if (isDisclaimer == 1) {
                                BWSApplication.showToast("The audio shall add after playing the disclaimer", ctx);
                            } else {
                                callAddAudioToPlaylist(AudiolistsModel.get(position).getID(), "", "0");
                            }
                        } else {
                            callAddAudioToPlaylist(AudiolistsModel.get(position).getID(), "", "0");
                        }
                    } else if (AudiolistsModel.get(position).getIsPlay().equalsIgnoreCase("0")
                            || AudiolistsModel.get(position).getIsPlay().equalsIgnoreCase("")) {
                        holder.binds.ivLock.setVisibility(View.VISIBLE);
                        BWSApplication.showToast("Please re-activate your membership plan", ctx);
                    }
                } else if (AudiolistsModel.get(position).getIsLock().equalsIgnoreCase("0")
                        || AudiolistsModel.get(position).getIsLock().equalsIgnoreCase("")) {
                    holder.binds.ivLock.setVisibility(View.GONE);
                    SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                    boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                    String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                    String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                    if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                        if (isDisclaimer == 1) {
                            BWSApplication.showToast("The audio shall add after playing the disclaimer", ctx);
                        } else {
                            callAddAudioToPlaylist(AudiolistsModel.get(position).getID(), "", "0");
                        }
                    } else {
                        callAddAudioToPlaylist(AudiolistsModel.get(position).getID(), "", "0");
                    }
                }
            });
        }
        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return AudiolistsModel.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            DownloadsLayoutBinding binds;

            public MyViewHolder(DownloadsLayoutBinding binds) {
                super(binds.getRoot());
                this.binds = binds;
            }
        }
    }

    public class SuggestionPlayListsAdpater extends RecyclerView.Adapter<SuggestionPlayListsAdpater.MyViewHolder> {
        private ArrayList<SearchPlaylistModel.ResponseData> PlaylistModel;

        public SuggestionPlayListsAdpater(ArrayList<SearchPlaylistModel.ResponseData> PlaylistModel) {
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
            Glide.with(ctx).load(PlaylistModel.get(position).getImage()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            holder.binding.ivIcon.setImageResource(R.drawable.add_icon);
            holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg);
            if (PlaylistModel.get(position).getIsLock().equalsIgnoreCase("1")) {
                holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                holder.binding.ivLock.setVisibility(View.VISIBLE);
            }
            if (PlaylistModel.get(position).getIsLock().equalsIgnoreCase("2")) {
                holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                holder.binding.ivLock.setVisibility(View.VISIBLE);
            } else if (PlaylistModel.get(position).getIsLock().equalsIgnoreCase("0") || PlaylistModel.get(position).getIsLock().equalsIgnoreCase("")) {
                holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                holder.binding.ivLock.setVisibility(View.GONE);
            }

            holder.binding.llMainLayout.setOnClickListener(view -> {
                if (PlaylistModel.get(position).getIsLock().equalsIgnoreCase("1")) {
                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                    Intent i = new Intent(ctx, MembershipChangeActivity.class);
                    i.putExtra("ComeFrom", "Plan");
                    startActivity(i);
                } else if (PlaylistModel.get(position).getIsLock().equalsIgnoreCase("2")) {
                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                    BWSApplication.showToast("Please re-activate your membership plan", ctx);
                } else if (PlaylistModel.get(position).getIsLock().equalsIgnoreCase("0")
                        || PlaylistModel.get(position).getIsLock().equalsIgnoreCase("")) {
                    comefromDownload = "0";
                    addToSearch = true;
                    MyPlaylistIds = PlaylistModel.get(position).getID();
                    PlaylistIDMS = PlaylistID;
                    finish();
                }
            });

            holder.binding.llRemoveAudio.setOnClickListener(view -> {
                if (PlaylistModel.get(position).getIsLock().equalsIgnoreCase("1")) {
                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                    Intent i = new Intent(ctx, MembershipChangeActivity.class);
                    i.putExtra("ComeFrom", "Plan");
                    startActivity(i);
                } else if (PlaylistModel.get(position).getIsLock().equalsIgnoreCase("2")) {
                    BWSApplication.showToast("Please re-activate your membership plan", ctx);
                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                } else if (PlaylistModel.get(position).getIsLock().equalsIgnoreCase("0") || PlaylistModel.get(position).getIsLock().equalsIgnoreCase("")) {
                    holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                    holder.binding.ivLock.setVisibility(View.GONE);
                    comefromDownload = "0";
                    SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                    boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                    String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                    String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
                    if (audioPlay && AudioFlag.equalsIgnoreCase("SubPlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                        if (isDisclaimer == 1) {
                            BWSApplication.showToast("The audio shall add after playing the disclaimer", ctx);
                        } else {
                            callAddAudioToPlaylist("", PlaylistModel.get(position).getID(), "1");
                        }
                    } else {
                        callAddAudioToPlaylist("", PlaylistModel.get(position).getID(), "1");
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return PlaylistModel.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            DownloadsLayoutBinding binding;

            public MyViewHolder(DownloadsLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}