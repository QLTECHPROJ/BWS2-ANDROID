package com.brainwellnessspa.DashboardModule.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Models.AddToQueueModel;
import com.brainwellnessspa.DashboardModule.Models.AppointmentDetailModel;
import com.brainwellnessspa.DashboardModule.Models.AudioLikeModel;
import com.brainwellnessspa.DashboardModule.Models.MainAudioModel;
import com.brainwellnessspa.DashboardModule.Models.SearchBothModel;
import com.brainwellnessspa.DashboardModule.Models.SubPlayListModel;
import com.brainwellnessspa.DashboardModule.Models.SuggestedModel;
import com.brainwellnessspa.DashboardModule.Models.ViewAllAudioListModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.LikeModule.Models.LikesHistoryModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivityAudioPlayerBinding;
import com.brainwellnessspa.databinding.AudioPlayerCustomLayoutBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.player;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.Utility.MusicService.isPause;

public class AudioPlayerActivity extends AppCompatActivity {
    private static final String SURFACE_CONTROL_NAME = "surfacedemo";
    private static final String ACTION_VIEW = "com.google.android.exoplayer.surfacedemo.action.VIEW";
    private static final String EXTENSION_EXTRA = "extension";
    private static final String DRM_SCHEME_EXTRA = "drm_scheme";
    private static final String DRM_LICENSE_URL_EXTRA = "drm_license_url";
    private static final String OWNER_EXTRA = "owner";
    AudioPlayerCustomLayoutBinding customLayoutBinding;
    private long mLastClickTime = 0;
    @Nullable
    private static SimpleExoPlayer player;
    @Nullable
    private static SurfaceControl surfaceControl;
    @Nullable
    private static Surface videoSurface;
    ActivityAudioPlayerBinding binding;
    List<MediaItem> mediaItemList;
    ArrayList<MainPlayModel> mainPlayModelList;
    ArrayList<AddToQueueModel> addToQueueModelList;
    String URL;
    String IsRepeat = "", IsShuffle = "", UserID, PlaylistId = "", AudioFlag, id, name, url, playFrom = "";
    int startTime = 0, endTime = 0, position, listSize, myCount, progress, downloadPercentage, audioBufferCapacityMs;
    Context ctx;
    Activity activity;
    Boolean queuePlay, audioPlay;
    private boolean isOwner; 
    @Nullable
    private SurfaceView nonFullScreenView;
    @Nullable
    private SurfaceView currentOutputView;

    private static void reparent(@Nullable SurfaceView surfaceView) {
        SurfaceControl surfaceControl = Assertions.checkNotNull(AudioPlayerActivity.surfaceControl);
        if (surfaceView == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                new SurfaceControl.Transaction()
                        .reparent(surfaceControl, /* newParent= */ null)
                        .setBufferSize(surfaceControl, /* w= */ 0, /* h= */ 0)
                        .setVisibility(surfaceControl, /* visible= */ false)
                        .apply();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                SurfaceControl newParentSurfaceControl = surfaceView.getSurfaceControl();
                new SurfaceControl.Transaction()
                        .reparent(surfaceControl, newParentSurfaceControl)
                        .setBufferSize(surfaceControl, surfaceView.getWidth(), surfaceView.getHeight())
                        .setVisibility(surfaceControl, /* visible= */ true)
                        .apply();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_audio_player);

        ctx = AudioPlayerActivity.this;
        activity = AudioPlayerActivity.this;
//        if (getIntent() != null) {
//            URL = getIntent().getStringExtra("URL");
//        }
        mediaItemList = new ArrayList<>();
        addToQueueModelList = new ArrayList<>();
        mainPlayModelList = new ArrayList<>();
        isOwner = getIntent().getBooleanExtra(OWNER_EXTRA, true);
        MakeArray();
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences Status = getSharedPreferences(CONSTANTS.PREF_KEY_Status, Context.MODE_PRIVATE);
        IsRepeat = Status.getString(CONSTANTS.PREF_KEY_IsRepeat, "");
        IsShuffle = Status.getString(CONSTANTS.PREF_KEY_IsShuffle, "");
        binding.fullScreenView.setOnClickListener(
                v -> {
                    setCurrentOutputView(nonFullScreenView);
                    Assertions.checkNotNull(binding.fullScreenView).setVisibility(View.GONE);
                });
        attachSurfaceListener(binding.fullScreenView);

        binding.llMore.setOnClickListener(view -> {
//            handler1.removeCallbacks(UpdateSongTime1);
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent i = new Intent(ctx, AddQueueActivity.class);
            if (AudioFlag.equalsIgnoreCase("TopCategories")) {
                i.putExtra("play", "TopCategories");
            } else
                i.putExtra("play", "play");
            i.putExtra("ID", id);
            i.putExtra("position", position);
            i.putExtra("PlaylistAudioId", "");
            startActivity(i);
//            finish();
        });

        binding.llViewQueue.setOnClickListener(view -> {
//            handler1.removeCallbacks(UpdateSongTime1);
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
//            if (binding.llPause.getVisibility() == View.VISIBLE) {
//                isPause = false;
//            }
            SharedPreferences ViewQueue = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = ViewQueue.edit();
            Gson gsonx = new Gson();
            String jsonx = gsonx.toJson(addToQueueModelList);
            if (queuePlay) {
                editor.putString(CONSTANTS.PREF_KEY_queueList, jsonx);
            }
            editor.putInt(CONSTANTS.PREF_KEY_position, position);
            editor.commit();
            Intent i = new Intent(ctx, ViewQueueActivity.class);
            i.putExtra("ComeFromQueue", "0");
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            finish();
        });
    }
    private void callBack() {
        try {
//        handler1.removeCallbacks(UpdateSongTime1);
//            player = 1;
//            if (binding.llPause.getVisibility() == View.VISIBLE) {
//                isPause = false;
//            }
//        pauseMedia();
            SharedPreferences shared2 = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared2.edit();
            Gson gson = new Gson();
            String json = gson.toJson(addToQueueModelList);
            if (queuePlay) {
                editor.putString(CONSTANTS.PREF_KEY_queueList, json);
            }
            editor.putInt(CONSTANTS.PREF_KEY_position, position);
            editor.commit();
            finish();
//        overridePendingTransition(R.anim.enter, R.anim.exit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callLike() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
            Call<AudioLikeModel> listCall = APIClient.getClient().getAudioLike(id, UserID);
            listCall.enqueue(new Callback<AudioLikeModel>() {
                @Override
                public void onResponse(Call<AudioLikeModel> call, Response<AudioLikeModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
                            AudioLikeModel model = response.body();
                            if (model.getResponseData().getFlag().equalsIgnoreCase("0")) {
                                binding.ivLike.setImageResource(R.drawable.ic_unlike_icon);
                            } else if (model.getResponseData().getFlag().equalsIgnoreCase("1")) {
                                binding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
                            }
                            SharedPreferences sharedxx = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                            boolean audioPlay = sharedxx.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                            int pos = sharedxx.getInt(CONSTANTS.PREF_KEY_position, 0);
                            AudioFlag = sharedxx.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                            SharedPreferences sharedq = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                            AudioFlag = sharedq.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                            Gson gsonq = new Gson();
                            String jsonq = sharedq.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gsonq));
                            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared.edit();
                            Gson gson = new Gson();
                            if (audioPlay) {
                                if (AudioFlag.equalsIgnoreCase("MainAudioList")) {
                                    Type type = new TypeToken<ArrayList<MainAudioModel.ResponseData.Detail>>() {
                                    }.getType();
                                    ArrayList<MainAudioModel.ResponseData.Detail> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("ViewAllAudioList")) {
                                    Type type = new TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail>>() {
                                    }.getType();
                                    ArrayList<ViewAllAudioListModel.ResponseData.Detail> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("SearchModelAudio")) {
                                    Type type = new TypeToken<ArrayList<SearchBothModel.ResponseData>>() {
                                    }.getType();
                                    ArrayList<SearchBothModel.ResponseData> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("SearchAudio")) {
                                    Type type = new TypeToken<ArrayList<SuggestedModel.ResponseData>>() {
                                    }.getType();
                                    ArrayList<SuggestedModel.ResponseData> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
                                    Type type = new TypeToken<ArrayList<AppointmentDetailModel.Audio>>() {
                                    }.getType();
                                    ArrayList<AppointmentDetailModel.Audio> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("LikeAudioList")) {
                                    Type type = new TypeToken<ArrayList<LikesHistoryModel.ResponseData.Audio>>() {
                                    }.getType();
                                    ArrayList<LikesHistoryModel.ResponseData.Audio> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
                                    Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
                                    }.getType();
                                    ArrayList<DownloadAudioDetails> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("Downloadlist")) {
                                    Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
                                    }.getType();
                                    ArrayList<DownloadAudioDetails> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("TopCategories")) {
                                    Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
                                    }.getType();
                                    ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                } else if (AudioFlag.equalsIgnoreCase("SubPlayList")) {
                                    Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
                                    }.getType();
                                    ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gsonq.fromJson(jsonq, type);
                                    arrayList.get(position).setLike(model.getResponseData().getFlag());
                                    String json2 = gson.toJson(arrayList);
                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json2);
                                }
                            }
                            if (queuePlay) {
                                addToQueueModelList.get(position).setLike(model.getResponseData().getFlag());
                            } else
                                mainPlayModelList.get(position).setLike(model.getResponseData().getFlag());

                            String json = gson.toJson(mainPlayModelList);
                            editor.putString(CONSTANTS.PREF_KEY_audioList, json);
                            if (queuePlay) {
                                String json1 = gson.toJson(addToQueueModelList);
                                editor.putString(CONSTANTS.PREF_KEY_queueList, json1);
                            }
                            editor.putInt(CONSTANTS.PREF_KEY_position, position);
                            editor.commit();
                            BWSApplication.showToast(model.getResponseMessage(), ctx);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<AudioLikeModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.pbProgressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
//        Assertions.checkNotNull(binding.playerControlView).setPlayer(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
      /*  if (isOwner && isFinishing()) {
            if (surfaceControl != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    surfaceControl.release();
                }
                surfaceControl = null;
            }
            if (videoSurface != null) {
                videoSurface.release();
                videoSurface = null;
            }
            if (player != null) {
                player.release();
                player = null;
            }
        }*/
    }

    private void initializePlayer() {
       /* DrmSessionManager drmSessionManager;
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri uri = ACTION_VIEW.equals(action)
                ? Assertions.checkNotNull(intent.getData())
                : Uri.parse(mainPlayModelList.get(position).getAudioFile());
        String userAgent = Util.getUserAgent(this, "Brain Wellness App");
        if (intent.hasExtra(DRM_SCHEME_EXTRA)) {
            String drmScheme = Assertions.checkNotNull(intent.getStringExtra(DRM_SCHEME_EXTRA));
            String drmLicenseUrl = Assertions.checkNotNull(intent.getStringExtra(DRM_LICENSE_URL_EXTRA));
            UUID drmSchemeUuid = Assertions.checkNotNull(Util.getDrmUuid(drmScheme));
            HttpDataSource.Factory licenseDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent);
            HttpMediaDrmCallback drmCallback =
                    new HttpMediaDrmCallback(drmLicenseUrl, licenseDataSourceFactory);
            drmSessionManager =
                    new DefaultDrmSessionManager.Builder()
                            .setUuidAndExoMediaDrmProvider(drmSchemeUuid, FrameworkMediaDrm.DEFAULT_PROVIDER)
                            .build(drmCallback);
        } else {
            drmSessionManager = DrmSessionManager.getDummyDrmSessionManager();
        }

        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(
                        this, Util.getUserAgent(this, "Brain Wellness App"));
        MediaSource mediaSource;
        @C.ContentType int type = Util.inferContentType(uri, intent.getStringExtra(EXTENSION_EXTRA));
        if (type == C.TYPE_DASH) {
            mediaSource =
                    new DashMediaSource.Factory(dataSourceFactory)
                            .setDrmSessionManager(drmSessionManager)
                            .createMediaSource(uri);
        } else if (type == C.TYPE_OTHER) {
            mediaSource =
                    new ProgressiveMediaSource.Factory(dataSourceFactory)
                            .setDrmSessionManager(drmSessionManager)
                            .createMediaSource(uri);
        } else {
            throw new IllegalStateException();
        }*/
        SimpleExoPlayer player = new SimpleExoPlayer.Builder(getApplicationContext()).build();
        player.prepare();
        player.addListener(new ExoPlayer.EventListener() {

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.v("TAG", "Listener-onTracksChanged... ");
                callButtonText(player.getCurrentWindowIndex());
            }

            @Override
            public void onPlaybackStateChanged(int state) {
                callButtonText(player.getCurrentWindowIndex());
            }
        });
        MediaItem mediaItem1 = MediaItem.fromUri(mainPlayModelList.get(0).getAudioFile());
        player.setMediaItem(mediaItem1);
         for(int i = 1;i<mediaItemList.size();i++){
            MediaItem mediaItem = MediaItem.fromUri(mainPlayModelList.get(i).getAudioFile());
            player.addMediaItem(mediaItem);
        }
//        player.setMediaItems(mediaItemList, position, 0);
        player.setPlayWhenReady(true);
//        player.setRepeatMode(Player.REPEAT_MODE_ALL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        surfaceControl = new SurfaceControl.Builder()
                        .setName(SURFACE_CONTROL_NAME)
                        .setBufferSize(/* width= */ 0, /* height= */ 0)
                        .build();
            videoSurface = new Surface(surfaceControl);
        }
        player.setVideoSurface(videoSurface);
        AudioPlayerActivity.player = player;
//        callButtonText();
    }

    private void callButtonText(int ps) {

        url = mainPlayModelList.get(ps).getAudioFile();
        id = mainPlayModelList.get(ps).getID();
        if (url.equalsIgnoreCase("") || url.isEmpty()) {
            isDisclaimer = 1;
            binding.tvNowPlaying.setText("");
        } else {
            binding.tvNowPlaying.setText(R.string.NOW_PLAYING_FROM);
            isDisclaimer = 0;
        }
        if (mainPlayModelList.get(ps).getPlaylistID() == null) {
            mainPlayModelList.get(ps).setPlaylistID("");
        }
        binding.tvName.setText(mainPlayModelList.get(ps).getName());
        if (mainPlayModelList.get(ps).getAudioDirection().equalsIgnoreCase("")) {
            binding.llDirection.setVisibility(View.GONE);
        } else {
            binding.llDirection.setVisibility(View.VISIBLE);
            binding.tvDireDesc.setText(mainPlayModelList.get(ps).getAudioDirection());
        }
        binding.tvTitle.setText(mainPlayModelList.get(ps).getAudiomastercat());
        binding.tvDesc.setText(mainPlayModelList.get(ps).getAudioSubCategory());
        if (url.equalsIgnoreCase("")) {
            Glide.with(ctx).load(R.drawable.disclaimer).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
        } else {
            Glide.with(ctx).load(mainPlayModelList.get(ps).getImageFile()).thumbnail(0.05f)
                    .placeholder(R.drawable.disclaimer).error(R.drawable.disclaimer)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);
        }
        if (mainPlayModelList.get(ps).getLike().equalsIgnoreCase("1")) {
            binding.ivLike.setImageResource(R.drawable.ic_fill_like_icon);
        } else if (mainPlayModelList.get(ps).getLike().equalsIgnoreCase("0")) {
            binding.ivLike.setImageResource(R.drawable.ic_unlike_icon);
        } else {
            binding.ivLike.setImageResource(R.drawable.ic_unlike_icon);
        }

//            binding.gridLayout.addView(view);
//            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
//            layoutParams.width = 0;
//            layoutParams.height = 0;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                layoutParams.columnSpec = GridLayout.spec(i % 1, 1f);
//                layoutParams.rowSpec = GridLayout.spec(i / 1, 1f);
//            }
//            layoutParams.bottomMargin = 10;
//            layoutParams.leftMargin = 10;
//            layoutParams.topMargin = 10;
//            layoutParams.rightMargin = 10;
//            view.setLayoutParams(layoutParams);
    }

    private void setCurrentOutputView(@Nullable SurfaceView surfaceView) {
        currentOutputView = surfaceView;
        if (surfaceView != null && surfaceView.getHolder().getSurface() != null) {
            reparent(surfaceView);
        }
    }

    private void attachSurfaceListener(SurfaceView surfaceView) {
        surfaceView
                .getHolder()
                .addCallback(
                        new SurfaceHolder.Callback() {
                            @Override
                            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                                if (surfaceView == currentOutputView) {
                                    reparent(surfaceView);
                                }
                            }

                            @Override
                            public void surfaceChanged(
                                    SurfaceHolder surfaceHolder, int format, int width, int height) {
                            }

                            @Override
                            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                            }
                        });
    }

    private void MakeArray() {
        Gson gson = new Gson();
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
        String json = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson));
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        MainPlayModel mainPlayModel;
        mediaItemList = new ArrayList<>();
        addToQueueModelList = new ArrayList<>();
        mainPlayModelList = new ArrayList<>();
        position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
        String json2 = shared.getString(CONSTANTS.PREF_KEY_queueList, String.valueOf(gson));
        if (!json2.equalsIgnoreCase(String.valueOf(gson))) {
            Type type1 = new TypeToken<ArrayList<AddToQueueModel>>() {
            }.getType();
            addToQueueModelList = gson.fromJson(json2, type1);
        }
        queuePlay = shared.getBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
        audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
        if (queuePlay) {
            playFrom = "queuePlay";
        } else if (audioPlay) {
            playFrom = "audioPlay";
        } else {
            playFrom = "audioPlay";
        }
        if (AudioFlag.equalsIgnoreCase("MainAudioList")) {
            Type type = new TypeToken<ArrayList<MainAudioModel.ResponseData.Detail>>() {
            }.getType();
            ArrayList<MainAudioModel.ResponseData.Detail> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
                MediaItem.Builder builder = new MediaItem.Builder();
                MediaItem mediaItem =
                        builder
                                .setUri(arrayList.get(i).getAudioFile())
                                .setMimeType(MimeTypes.APPLICATION_MPD)
                                .setTag(arrayList.get(i).getID())
                                .build();
                mediaItemList.add(mediaItem);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData();

        } else if (AudioFlag.equalsIgnoreCase("ViewAllAudioList")) {
            Type type = new TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail>>() {
            }.getType();
            ArrayList<ViewAllAudioListModel.ResponseData.Detail> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
                MediaItem.Builder builder = new MediaItem.Builder();
                MediaItem mediaItem =
                        builder
                                .setUri(arrayList.get(i).getAudioFile())
                                .setMimeType(MimeTypes.APPLICATION_MPD)
                                .setTag(arrayList.get(i).getID())
                                .build();
                mediaItemList.add(mediaItem);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData();
        } else if (AudioFlag.equalsIgnoreCase("SearchAudio")) {
            Type type = new TypeToken<ArrayList<SuggestedModel.ResponseData>>() {
            }.getType();
            ArrayList<SuggestedModel.ResponseData> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
                MediaItem.Builder builder = new MediaItem.Builder();
                MediaItem mediaItem =
                        builder
                                .setUri(arrayList.get(i).getAudioFile())
                                .setMimeType(MimeTypes.APPLICATION_MPD)
                                .setTag(arrayList.get(i).getID())
                                .build();
                mediaItemList.add(mediaItem);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData();
        } else if (AudioFlag.equalsIgnoreCase("SearchModelAudio")) {
            Type type = new TypeToken<ArrayList<SearchBothModel.ResponseData>>() {
            }.getType();
            ArrayList<SearchBothModel.ResponseData> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
                MediaItem.Builder builder = new MediaItem.Builder();
                MediaItem mediaItem =
                        builder
                                .setUri(arrayList.get(i).getAudioFile())
                                .setMimeType(MimeTypes.APPLICATION_MPD)
                                .setTag(arrayList.get(i).getID())
                                .build();
                mediaItemList.add(mediaItem);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData();
        } else if (AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
            Type type = new TypeToken<ArrayList<AppointmentDetailModel.Audio>>() {
            }.getType();
            ArrayList<AppointmentDetailModel.Audio> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
                MediaItem.Builder builder = new MediaItem.Builder();
                MediaItem mediaItem =
                        builder
                                .setUri(arrayList.get(i).getAudioFile())
                                .setMimeType(MimeTypes.APPLICATION_MPD)
                                .setTag(arrayList.get(i).getID())
                                .build();
                mediaItemList.add(mediaItem);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData();
        } else if (AudioFlag.equalsIgnoreCase("LikeAudioList")) {
            Type type = new TypeToken<ArrayList<LikesHistoryModel.ResponseData.Audio>>() {
            }.getType();
            ArrayList<LikesHistoryModel.ResponseData.Audio> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
                MediaItem.Builder builder = new MediaItem.Builder();
                MediaItem mediaItem =
                        builder
                                .setUri(arrayList.get(i).getAudioFile())
                                .setMimeType(MimeTypes.APPLICATION_MPD)
                                .setTag(arrayList.get(i).getID())
                                .build();
                mediaItemList.add(mediaItem);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData();
        } else if (AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
            Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
            }.getType();
            ArrayList<DownloadAudioDetails> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {

                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
                MediaItem.Builder builder = new MediaItem.Builder();
                MediaItem mediaItem =
                        builder
                                .setUri(arrayList.get(i).getAudioFile())
                                .setMimeType(MimeTypes.APPLICATION_MPD)
                                .setTag(arrayList.get(i).getID())
                                .build();
                mediaItemList.add(mediaItem);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData();
        } else if (AudioFlag.equalsIgnoreCase("Downloadlist")) {
            Type type = new TypeToken<ArrayList<DownloadAudioDetails>>() {
            }.getType();
            ArrayList<DownloadAudioDetails> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();

            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID(arrayList.get(i).getPlaylistId());
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
                MediaItem.Builder builder = new MediaItem.Builder();
                MediaItem mediaItem =
                        builder
                                .setUri(arrayList.get(i).getAudioFile())
                                .setMimeType(MimeTypes.APPLICATION_MPD)
                                .setTag(arrayList.get(i).getID())
                                .build();
                mediaItemList.add(mediaItem);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData();
        } else if (AudioFlag.equalsIgnoreCase("TopCategories")) {
            Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
            }.getType();
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();

            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID("");
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
                MediaItem.Builder builder = new MediaItem.Builder();
                MediaItem mediaItem =
                        builder
                                .setUri(arrayList.get(i).getAudioFile())
                                .setMimeType(MimeTypes.APPLICATION_MPD)
                                .setTag(arrayList.get(i).getID())
                                .build();
                mediaItemList.add(mediaItem);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData();
        } else if (AudioFlag.equalsIgnoreCase("SubPlayList")) {
            Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
            }.getType();
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
//            if (isDisclaimer == 0 && disclaimerPlayed == 0) {
//                addDeclaimer();
//            }
            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setPlaylistID(arrayList.get(i).getPlaylistID());
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
                MediaItem.Builder builder = new MediaItem.Builder();
                MediaItem mediaItem =
                        builder
                                .setUri(arrayList.get(i).getAudioFile())
                                .setMimeType(MimeTypes.APPLICATION_MPD)
                                .setTag(arrayList.get(i).getID())
                                .build();
                mediaItemList.add(mediaItem);
            }
            SharedPreferences sharedz = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedz.edit();
            Gson gsonz = new Gson();
            String json1 = gson.toJson(arrayList);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json1);
            String jsonz = gsonz.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
            editor.commit();
            getPrepareShowData();
        }
    }

    private void getPrepareShowData() {
//        if (isOwner && player == null) {
        initializePlayer();
//        }
        setCurrentOutputView(nonFullScreenView);

        PlayerControlView playerControlView = Assertions.checkNotNull(this.binding.playerControlView);
        playerControlView.setPlayer(player);
        playerControlView.show();
    }
}