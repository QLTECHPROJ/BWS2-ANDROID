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
import android.widget.ImageView;
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
import com.brainwellnessspa.DashboardTwoModule.Model.AddToPlaylistModel;
import com.brainwellnessspa.DashboardTwoModule.Model.SearchPlaylistModel;
import com.brainwellnessspa.DashboardModule.Models.SegmentAudio;
import com.brainwellnessspa.DashboardModule.Models.SegmentPlaylist;
import com.brainwellnessspa.DashboardModule.Models.SubPlayListModel;
import com.brainwellnessspa.DashboardTwoModule.Model.SuggestedModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Services.GlobalInitExoPlayer;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.APINewClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.ActivityViewSuggestedBinding;
import com.brainwellnessspa.databinding.DownloadsLayoutBinding;
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

import static com.brainwellnessspa.DashboardTwoModule.AddAudioActivity.MyPlaylistIds;
import static com.brainwellnessspa.DashboardTwoModule.AddAudioActivity.PlaylistIDMS;
import static com.brainwellnessspa.DashboardTwoModule.AddAudioActivity.addToSearch;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.audioClick;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.miniPlayer;

import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.myAudioId;
import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.callNewPlayerRelease;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.notificationId;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.player;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.relesePlayer;

public class ViewSuggestedActivity extends AppCompatActivity {
    ActivityViewSuggestedBinding binding;
    Activity activity;
    Context ctx;
    String UserID, Name, PlaylistID;
    ArrayList<SuggestedModel.ResponseData> AudiolistsModel;
    ArrayList<SearchPlaylistModel.ResponseData> PlaylistModel;
    AudiosListAdpater adpater;
    private int numStarted = 0;
    int stackStatus = 0;
    boolean myBackPress = false;
    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("MyData")) {
   /*             String data = intent.getStringExtra("MyData");
                Log.d("play_pause_Action", data);
                SharedPreferences sharedzw = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                boolean audioPlayz = sharedzw.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                AudioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pIDz = sharedzw.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                if (!AudioFlag.equalsIgnoreCase("Downloadlist") &&
                        !AudioFlag.equalsIgnoreCase("SubPlayList") &&
                        !AudioFlag.equalsIgnoreCase("TopCategories")) {
                    if (player != null) {
                        adpater.notifyDataSetChanged();
                    }
                }*/
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
            myBackPress = true;
            Intent i = new Intent(ctx, AddAudioActivity.class);
            i.putExtra("PlaylistID", PlaylistID);
            startActivity(i);
            finish();
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(new AppLifecycleCallback());
        }
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

    }

    @Override
    protected void onResume() {
        PrepareData();
        super.onResume();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(ViewSuggestedActivity.this).unregisterReceiver(listener);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        myBackPress = true;
        Intent i = new Intent(ctx, AddAudioActivity.class);
        i.putExtra("PlaylistID", PlaylistID);
        startActivity(i);
        finish();
    }

    public void PrepareData() {
       /* try {
            GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
            globalInitExoPlayer.UpdateMiniPlayer(ctx);
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
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        /* if (!AudioFlag.equalsIgnoreCase("0")) {
            comefromDownload = "1";
            callAddFrag();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(0, 8, 0, 210);
            binding.llSpace.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(0, 8, 0, 20);
            binding.llSpace.setLayoutParams(params);
        }*/
        binding.tvTitle.setText(Name);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        binding.rvMainAudio.setLayoutManager(layoutManager);
        binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
        if (Name.equalsIgnoreCase("Suggested Audios")) {
            ArrayList<SegmentAudio> section = new ArrayList<>();
            for (int i = 0; i < AudiolistsModel.size(); i++) {
                SegmentAudio e = new SegmentAudio();
                e.setAudioId(AudiolistsModel.get(i).getID());
                e.setAudioName(AudiolistsModel.get(i).getName());
                e.setMasterCategory(AudiolistsModel.get(i).getAudiomastercat());
                e.setSubCategory(AudiolistsModel.get(i).getAudioSubCategory());
                e.setAudioDuration(AudiolistsModel.get(i).getAudioDirection());
                section.add(e);
            }
            Properties p = new Properties();
            p.putValue("userId", UserID);
            Gson gson = new Gson();
            p.putValue("audios", gson.toJson(section));
            p.putValue("source", "Search Screen");
            BWSApplication.addToSegment("Suggested Audios List Viewed", p, CONSTANTS.screen);
            adpater = new AudiosListAdpater(AudiolistsModel);
            LocalBroadcastManager.getInstance(ViewSuggestedActivity.this)
                    .registerReceiver(listener, new IntentFilter("play_pause_Action"));
            binding.rvMainAudio.setAdapter(adpater);
        } else if (Name.equalsIgnoreCase("Suggested Playlist")) {
            ArrayList<SegmentPlaylist> section = new ArrayList<>();
            for (int i = 0; i < PlaylistModel.size(); i++) {
                SegmentPlaylist e = new SegmentPlaylist();
                e.setPlaylistId(PlaylistModel.get(i).getID());
                e.setPlaylistName(PlaylistModel.get(i).getName());
                e.setPlaylistType(PlaylistModel.get(i).getCreated());
                e.setPlaylistDuration(PlaylistModel.get(i).getTotalhour() + "h " + PlaylistModel.get(i).getTotalminute() + "m");
                e.setAudioCount(PlaylistModel.get(i).getTotalAudio());
                section.add(e);
            }
            Properties p = new Properties();
            p.putValue("userId", UserID);
            Gson gson = new Gson();
            p.putValue("playlists", gson.toJson(section));
            p.putValue("source", "Search Screen");
            BWSApplication.addToSegment("Suggested Playlists List Viewed", p, CONSTANTS.screen);
            SuggestionPlayListsAdpater adpater = new SuggestionPlayListsAdpater(PlaylistModel);
            binding.rvMainAudio.setAdapter(adpater);
        }
    }

    private void callAddAudioToPlaylist(String AudioID, String FromPlaylistId, String s1) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<AddToPlaylistModel> listCall = APINewClient.getClient().getAddSearchAudioFromPlaylist(UserID, AudioID, PlaylistID, FromPlaylistId);
            listCall.enqueue(new Callback<AddToPlaylistModel>() {
                @Override
                public void onResponse(Call<AddToPlaylistModel> call, Response<AddToPlaylistModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            AddToPlaylistModel listModels = response.body();
                            SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE);
                            String AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
                            String MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PayerPlaylistId, "");
                            String PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "");
                            Integer PlayerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                            if ( AudioPlayerFlag.equalsIgnoreCase("playList") && MyPlaylist.equalsIgnoreCase(PlaylistID)) {

                                Gson gsonx = new Gson();
                                    String json = shared1.getString(CONSTANTS.PREF_KEY_PlayerAudioList, String.valueOf(gsonx));
                                    Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                                    }.getType();
                                    ArrayList<MainPlayModel> mainPlayModelListold = new ArrayList<>();
                                    mainPlayModelListold = gsonx.fromJson(json, type);
                                    String id = mainPlayModelListold.get(PlayerPosition).getID();
                                    int size = mainPlayModelListold.size();
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
                                    editor.putString(CONSTANTS.PREF_KEY_PayerPlaylistId, PlaylistID);
                                    editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "Created");
                                    editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "playlist");
                                    editor.commit();

                                    if (!mainPlayModelList.get(PlayerPosition).getAudioFile().equals("")) {
                                        List<String> downloadAudioDetailsList = new ArrayList<>();
                                        GlobalInitExoPlayer ge = new GlobalInitExoPlayer();
                                        ge.AddAudioToPlayer(size, mainPlayModelList, downloadAudioDetailsList, ctx);
                                    }
                                    if (player != null) {
                                        callAddFrag();
                                    }
                                }
                            BWSApplication.showToast(listModels.getResponseMessage(), ctx);
                            if (s1.equalsIgnoreCase("1")) {
                                finish();
                            }
                        }
                    } catch (Exception e) {   BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);

                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<AddToPlaylistModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    private void callAddFrag() {
        Fragment fragment = new MiniPlayerFragment();
        FragmentManager fragmentManager1 = getSupportFragmentManager();
        fragmentManager1.beginTransaction()
                .add(R.id.flContainer, fragment)
                .commit();
    }

    public class AudiosListAdpater extends RecyclerView.Adapter<AudiosListAdpater.MyViewHolder> {
        String songId;
        private ArrayList<SuggestedModel.ResponseData> AudiolistsModel;

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
            holder.binds.equalizerview.setVisibility(View.GONE);
            holder.binds.ivIcon.setImageResource(R.drawable.ic_add_two_icon);
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binds.cvImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binds.cvImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binds.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binds.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binds.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.binds.ivBackgroundImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binds.ivBackgroundImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binds.ivBackgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(AudiolistsModel.get(position).getImageFile()).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binds.ivRestaurantImage);
            Glide.with(ctx).load(R.drawable.ic_image_bg).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binds.ivBackgroundImage);
          /*  SharedPreferences sharedzw = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            boolean audioPlayz = sharedzw.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
            AudioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            String pIDz = sharedzw.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
//            if (!AudioFlag.equalsIgnoreCase("Downloadlist") &&
//                    !AudioFlag.equalsIgnoreCase("SubPlayList") &&
//                    !AudioFlag.equalsIgnoreCase("TopCategories")) {
//                if (myAudioId.equalsIgnoreCase(AudiolistsModel.get(position).getID())) {
//                    songId = myAudioId;
//                    if (player != null) {
//                        if (!player.getPlayWhenReady()) {
//                            holder.binds.equalizerview.pause();
//                        } else
//                            holder.binds.equalizerview.resume(true);
//                    } else
//                        holder.binds.equalizerview.stop(true);
//                    holder.binds.equalizerview.setVisibility(View.VISIBLE);
//                    holder.binds.llMainLayout.setBackgroundResource(R.color.highlight_background);
//                    holder.binds.ivBackgroundImage.setVisibility(View.VISIBLE);
//                } else {
//                    holder.binds.equalizerview.setVisibility(View.GONE);
//                    holder.binds.llMainLayout.setBackgroundResource(R.color.white);
//                    holder.binds.ivBackgroundImage.setVisibility(View.GONE);
//                }
//            } else {
                holder.binds.llMainLayout.setBackgroundResource(R.color.white);
                holder.binds.ivBackgroundImage.setVisibility(View.GONE);
            }*/
            holder.binds.equalizerview.setVisibility(View.GONE);


//            if (AudiolistsModel.get(position).isLock().equalsIgnoreCase("1")) {
//                if (AudiolistsModel.get(position).isPlay().equalsIgnoreCase("1")) {
//                    holder.binds.ivLock.setVisibility(View.GONE);
//                } else if (AudiolistsModel.get(position).isPlay().equalsIgnoreCase("0")
//                        || AudiolistsModel.get(position).isPlay().equalsIgnoreCase("")) {
//                    holder.binds.ivLock.setVisibility(View.VISIBLE);
//                }
//            } else if (AudiolistsModel.get(position).isLock().equalsIgnoreCase("2")) {
//                if (AudiolistsModel.get(position).isPlay().equalsIgnoreCase("1")) {
//                    holder.binds.ivLock.setVisibility(View.GONE);
//                } else if (AudiolistsModel.get(position).isPlay().equalsIgnoreCase("0")
//                        || AudiolistsModel.get(position).isPlay().equalsIgnoreCase("")) {
//                    holder.binds.ivLock.setVisibility(View.VISIBLE);
//                }
//            } else if (AudiolistsModel.get(position).isLock().equalsIgnoreCase("0")
//                    || AudiolistsModel.get(position).isLock().equalsIgnoreCase("")) {
                holder.binds.ivLock.setVisibility(View.GONE);
//            }

            holder.binds.llMainLayoutForPlayer.setOnClickListener(view -> {
//                if (AudiolistsModel.get(position).isLock().equalsIgnoreCase("1")) {
//                    if (AudiolistsModel.get(position).isPlay().equalsIgnoreCase("1")) {
//                        callMainTransFrag(position);
//                    } else if (AudiolistsModel.get(position).isPlay().equalsIgnoreCase("0")
//                            || AudiolistsModel.get(position).isPlay().equalsIgnoreCase("")) {
//                        Intent i = new Intent(ctx, MembershipChangeActivity.class);
//                        i.putExtra("ComeFrom", "Plan");
//                        startActivity(i);
//                    }
//                } else if (AudiolistsModel.get(position).isLock().equalsIgnoreCase("2")) {
//                    if (AudiolistsModel.get(position).isPlay().equalsIgnoreCase("1")) {
//                        callMainTransFrag(position);
//                    } else if (AudiolistsModel.get(position).isPlay().equalsIgnoreCase("0")
//                            || AudiolistsModel.get(position).isPlay().equalsIgnoreCase("")) {
//                        BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
//                    }
//                } else if (AudiolistsModel.get(position).isLock().equalsIgnoreCase("0")
//                        || AudiolistsModel.get(position).isLock().equalsIgnoreCase("")) {
                    callMainTransFrag(position);
//                }
            });

            holder.binds.llRemoveAudio.setOnClickListener(view -> {
//                if (AudiolistsModel.get(position).isLock().equalsIgnoreCase("1")) {
//                    Intent i = new Intent(ctx, MembershipChangeActivity.class);
//                    i.putExtra("ComeFrom", "Plan");
//                    startActivity(i);
//                } else if (AudiolistsModel.get(position).isLock().equalsIgnoreCase("2")) {
//                    BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
//                } else if (AudiolistsModel.get(position).isLock().equalsIgnoreCase("0")
//                        || AudiolistsModel.get(position).isLock().equalsIgnoreCase("")) {

                SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE);
                String AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
                String MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PayerPlaylistId, "");
                String PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "");
                Integer PlayerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                if (AudioPlayerFlag.equalsIgnoreCase("playlist") && MyPlaylist.equalsIgnoreCase(PlaylistID)) {
                    if (isDisclaimer == 1) {
                        BWSApplication.showToast("The audio shall add after playing the disclaimer", ctx);
                    } else {
                        callAddAudioToPlaylist(AudiolistsModel.get(position).getID(), "", "0");
                    }
                } else {
                    callAddAudioToPlaylist(AudiolistsModel.get(position).getID(), "", "0");
                }
//                }
            });
        }

        public void callMainTransFrag(int position) {
            try {
                SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE);
                String AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
                String MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PayerPlaylistId, "");
                String PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "");
                Integer PlayerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                if (AudioPlayerFlag.equalsIgnoreCase("SearchAudio")
                        && PlayFrom.equalsIgnoreCase("Recommended Search")) {
                    ArrayList<SuggestedModel.ResponseData> listModelList2 = new ArrayList<>();
                    listModelList2.add(AudiolistsModel.get(position));
                    if (player != null) {
                        if (position != PlayerPosition) {
                            player.seekTo(position, 0);
                            player.setPlayWhenReady(true);
                            SharedPreferences sharedxx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, AppCompatActivity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedxx.edit();
                            editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
                            editor.apply();
                        }
                        callMyPlayer();
                    } else {
                        callPlayer(0, listModelList2);
                    }
                } else {
                    ArrayList<SuggestedModel.ResponseData> listModelList2 = new ArrayList<>();
                    listModelList2.add(AudiolistsModel.get(position));
                    callPlayer(0,listModelList2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        private void callMyPlayer() {
            Intent i =new Intent(ctx, MyPlayerActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            ctx.startActivity(i);
            activity.overridePendingTransition(0, 0);
        }

        private void callPlayer(int position, ArrayList<SuggestedModel.ResponseData> listModel) {
            callNewPlayerRelease();
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, AppCompatActivity.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String json = gson.toJson(listModel);
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json);
            editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
            editor.putString(CONSTANTS.PREF_KEY_PayerPlaylistId, "");
            editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "Recommended Search");
            editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "SearchAudio");
            editor.apply();
            audioClick = true;
            callMyPlayer();
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
//            }

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
            holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg);
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
//                } else if (PlaylistModel.get(position).isLock().equalsIgnoreCase("2")) {
//                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                    holder.binding.ivLock.setVisibility(View.VISIBLE);
//                    BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
//                } else if (PlaylistModel.get(position).isLock().equalsIgnoreCase("0")
//                        || PlaylistModel.get(position).isLock().equalsIgnoreCase("")) {
                    comefromDownload = "0";
                    addToSearch = true;
                    MyPlaylistIds = PlaylistModel.get(position).getID();
                    PlaylistIDMS = PlaylistID;
                    finish();
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
//                    BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
//                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                    holder.binding.ivLock.setVisibility(View.VISIBLE);
//                } else if (PlaylistModel.get(position).isLock().equalsIgnoreCase("0") || PlaylistModel.get(position).isLock().equalsIgnoreCase("")) {
                holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                holder.binding.ivLock.setVisibility(View.GONE);
                comefromDownload = "0";
                SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE);
                String AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
                String MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PayerPlaylistId, "");
                String PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "");
                Integer PlayerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                if (AudioPlayerFlag.equalsIgnoreCase("playlist") && MyPlaylist.equalsIgnoreCase(PlaylistID)) {
                    if (isDisclaimer == 1) {
                        BWSApplication.showToast("The audio shall add after playing the disclaimer", ctx);
                    } else {
                        callAddAudioToPlaylist("", PlaylistModel.get(position).getID(), "1");
                    }
                } else {
                    callAddAudioToPlaylist("", PlaylistModel.get(position).getID(), "1");
                }
//            }
            });
        }
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