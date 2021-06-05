package com.brainwellnessspa.DownloadModule.Fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardOldModule.Models.ViewAllAudioListModel;
import com.brainwellnessspa.DashboardOldModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.dashboardModule.models.HomeScreenModel;
import com.brainwellnessspa.dashboardModule.activities.MyPlayerActivity;
import com.brainwellnessspa.EncryptDecryptUtils.FileUtils;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.AudioDatabase;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.AudioDownloadsLayoutBinding;
import com.brainwellnessspa.databinding.FragmentDownloadsBinding;
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

import static android.content.Context.MODE_PRIVATE;

import static com.brainwellnessspa.BWSApplication.PlayerAudioId;
import static com.brainwellnessspa.BWSApplication.DB;
import static com.brainwellnessspa.BWSApplication.appStatus;
import static com.brainwellnessspa.BWSApplication.getAudioDataBase;
import static com.brainwellnessspa.DashboardOldModule.Activities.DashboardActivity.audioClick;
import static com.brainwellnessspa.DashboardOldModule.Activities.DashboardActivity.miniPlayer;
import static com.brainwellnessspa.DashboardOldModule.TransparentPlayer.Fragments.MiniPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.downloadIdOne;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.downloadProgress;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.filename;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.callNewPlayerRelease;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.player;

public class AudioDownloadsFragment extends Fragment {
    public static String comefromDownload = "0";
    FragmentDownloadsBinding binding;
    String UserID,CoUserID, IsPlayDisclimer;
    AudioDownlaodsAdapter adapter;
    boolean isThreadStart = false;
    public AudioManager audioManager;
    public int hundredVolume = 0, currentVolume = 0, maxVolume = 0, percent;
    List<String> fileNameList = new ArrayList<>(), playlistDownloadId = new ArrayList<>(), audiofilelist = new ArrayList<>();
    //    Runnable UpdateSongTime1;
    View view;
    List<String> downloadAudioDetailsList = new ArrayList<>();
    //    private Handler handler1;
    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("MyData")) {
                String data = intent.getStringExtra("MyData");
                Log.d("play_pause_Action", data);
                SharedPreferences sharedzw = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE);
                String AudioPlayerFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
                int PlayerPosition = sharedzw.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);

                if (AudioPlayerFlag.equalsIgnoreCase("DownloadListAudio")) {
                    if (player != null) {
                        if (data.equalsIgnoreCase("play")) {
//                    BWSApplication.showToast("Play", getActivity());
                            adapter.notifyDataSetChanged();
                        } else {
//                    BWSApplication.showToast("pause", getActivity());
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }
    };
    private final BroadcastReceiver listener1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getDownloadData();
            if (intent.hasExtra("Progress")) {
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_downloads, container, false);
        view = binding.getRoot();
        if (getArguments() != null) {
            UserID = getArguments().getString("UserID");
        }
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE);
        UserID = shared.getString(CONSTANTS.PREFE_ACCESS_UserID, "");
        CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "");
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        percent = 100;
        hundredVolume = (int) (currentVolume * percent) / maxVolume;
//        handler1 = new Handler();
        DB = getAudioDataBase(getActivity());

//        audioList = GetAllMedia(getActivity());
        callObserverMethod();
        binding.tvFound.setText("Your downloaded audios will appear here");
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvDownloadsList.setLayoutManager(mLayoutManager);
        binding.rvDownloadsList.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    public void callObserverMethod() {
        DB.taskDao().geAllDataz("",CoUserID).observe(getActivity(), audioList -> {
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
                        dad.setAudioDuration(audioList.get(i).getAudioDuration());
                        dad.setPlaylistId(audioList.get(i).getPlaylistId());
                        dad.setIsSingle(audioList.get(i).getIsSingle());
                        dad.setIsDownload(audioList.get(i).getIsDownload());
                        dad.setDownloadProgress(audioList.get(i).getDownloadProgress());
                        audioList1.add(dad);
                    }
                    getDataList(audioList1, binding.progressBarHolder, binding.progressBar, binding.llError, binding.rvDownloadsList);
                    binding.llError.setVisibility(View.GONE);
                    binding.rvDownloadsList.setVisibility(View.VISIBLE);
                } else {
                    binding.llError.setVisibility(View.VISIBLE);
                    binding.rvDownloadsList.setVisibility(View.GONE);
                }
            } else {
                binding.llError.setVisibility(View.VISIBLE);
                binding.rvDownloadsList.setVisibility(View.GONE);
            }
            DB.taskDao().geAllDataz("",CoUserID).removeObserver(audioListx -> {
            });
        });

    }

    private void getDownloadData() {
        try {
            SharedPreferences sharedy = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
            Gson gson = new Gson();
            String jsony = sharedy.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson));
            String jsonx = sharedy.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson));
            String jsonq = sharedy.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson));
            if (!jsony.equalsIgnoreCase(String.valueOf(gson))) {
                Type type = new TypeToken<List<String>>() {
                }.getType();
                fileNameList = gson.fromJson(jsony, type);
                playlistDownloadId = gson.fromJson(jsonq, type);
                audiofilelist = gson.fromJson(jsonx, type);
                if (fileNameList.size() != 0) {
//                        handler1.postDelayed(UpdateSongTime1, 30000);
                } else {
                    audiofilelist = new ArrayList<>();
                    fileNameList = new ArrayList<>();
                    playlistDownloadId = new ArrayList<>();
                    isThreadStart = false;
                }
            } else {
                fileNameList = new ArrayList<>();
                audiofilelist = new ArrayList<>();
                playlistDownloadId = new ArrayList<>();
                isThreadStart = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        Gson gson = new Gson();
        SharedPreferences shared1x = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE);
        String AudioPlayerFlagx = shared1x.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
        int PlayerPositionx = shared1x.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
        String json = shared1x.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toString());
        ArrayList<MainPlayModel> mainPlayModelList = new ArrayList<>();
        if(!AudioPlayerFlagx.equals("0")) {
            if (!json.equalsIgnoreCase(String.valueOf(gson))) {
                Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                }.getType();
                mainPlayModelList = gson.fromJson(json, type);
            }
            PlayerAudioId = mainPlayModelList.get(PlayerPositionx).getID();
        }
        callObserverMethod();
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(listener, new IntentFilter("play_pause_Action"));
//        audioList = GetAllMedia(getActivity());

        super.onResume();
    }


    @Override
    public void onPause() {
        if (isThreadStart) {
//            handler1.removeCallbacks(UpdateSongTime1);
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(listener);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(listener1);
        super.onDestroy();
    }
    private void getDataList(List<DownloadAudioDetails> historyList, FrameLayout progressBarHolder, ProgressBar ImgV, LinearLayout llError, RecyclerView rvDownloadsList) {
        if (historyList.size() == 0) {
            binding.tvFound.setVisibility(View.VISIBLE);
            binding.llError.setVisibility(View.VISIBLE);
            binding.llSpace.setVisibility(View.GONE);
        } else {
            binding.llError.setVisibility(View.GONE);
            binding.llSpace.setVisibility(View.VISIBLE);
            adapter = new AudioDownlaodsAdapter(historyList, getActivity(),  progressBarHolder, ImgV, llError, rvDownloadsList, binding.tvFound);
            binding.rvDownloadsList.setAdapter(adapter);
            LocalBroadcastManager.getInstance(getActivity())
                    .registerReceiver(listener, new IntentFilter("play_pause_Action"));
            LocalBroadcastManager.getInstance(getActivity())
                    .registerReceiver(listener1, new IntentFilter("DownloadProgress"));
        }
    }

    private void callAddTransFrag() {
        Intent i =new Intent(getActivity(), MyPlayerActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        getActivity().startActivity(i);
        getActivity().overridePendingTransition(0, 0);
    }

    public class AudioDownlaodsAdapter extends RecyclerView.Adapter<AudioDownlaodsAdapter.MyViewHolder> {
        FragmentActivity ctx;
        String UserID;
        FrameLayout progressBarHolder;
        ProgressBar ImgV;
        LinearLayout llError;
        RecyclerView rvDownloadsList;
        TextView tvFound;
        //    Handler handler3;
        int startTime;
        long myProgress = 0;
        private List<DownloadAudioDetails> listModelList;
        private long currentDuration = 0;
//    private Runnable UpdateSongTime3;


        public AudioDownlaodsAdapter(List<DownloadAudioDetails> listModelList, FragmentActivity ctx,
                                     FrameLayout progressBarHolder, ProgressBar ImgV, LinearLayout llError, RecyclerView rvDownloadsList, TextView tvFound) {
            this.listModelList = listModelList;
            this.ctx = ctx;
            this.UserID = UserID;
            this.progressBarHolder = progressBarHolder;
            this.ImgV = ImgV;
            this.llError = llError;
            this.rvDownloadsList = rvDownloadsList;
            this.tvFound = tvFound;
            getDownloadData();
        }

        @NonNull
        @Override
        public AudioDownlaodsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            AudioDownloadsLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.audio_downloads_layout, parent, false);
            return new AudioDownlaodsAdapter.MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull AudioDownlaodsAdapter.MyViewHolder holder, int position) {
//        handler3 = new Handler();
        /*    UpdateSongTime1 = new Runnable() {
                @Override
                public void run() {
                    try {
                        if (fileNameList.size() != 0) {
                            for (int i = 0; i < fileNameList.size(); i++) {
                                if (fileNameList.get(i).equalsIgnoreCase(listModelList.get(position).getName()) && playlistDownloadId.get(i).equalsIgnoreCase("")) {
                                    if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(listModelList.get(position).getName())) {
                                        if (downloadProgress <= 100) {
                                            if (BWSApplication.isNetworkConnected(ctx)) {
                                                notifyItemChanged(position);
                                            }
                                        } else {
                                            notifyDataSetChanged();
                                            holder.binding.pbProgress.setVisibility(View.GONE);
                                            getDownloadData();
                                        }
                                    }
                                }
                            }
                        }
                        if (downloadProgress == 0) {
                            notifyDataSetChanged();
                            getDownloadData();
                        }
                        handler1.postDelayed(this, 30000);
                        isThreadStart = true;
                    } catch (Exception e) {
                    }
                }
            };
*/
            if (position == 0) {
                AudioDatabase.databaseWriteExecutor.execute(() -> {
                    downloadAudioDetailsList = DB.taskDao().    geAllDataBYDownloaded("Complete",CoUserID);
                });
            }
            if (fileNameList.size() != 0) {
                for (int i = 0; i < fileNameList.size(); i++) {
                    if (fileNameList.get(i).equalsIgnoreCase(listModelList.get(position).getName()) && playlistDownloadId.get(i).equalsIgnoreCase("")) {
                        if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(listModelList.get(position).getName())) {
                            if (downloadProgress <= 100) {
                                if (downloadProgress == 100) {
                                    holder.binding.pbProgress.setVisibility(View.GONE);
                                } else {
                                    holder.binding.pbProgress.setProgress(downloadProgress);
                                    holder.binding.pbProgress.setVisibility(View.VISIBLE);
                                }
                            } else {
                                holder.binding.pbProgress.setVisibility(View.GONE);
                            }
//                            handler1.postDelayed(UpdateSongTime1, 30000);
                            break;
                        } else {
                            holder.binding.pbProgress.setProgress(0);
                            holder.binding.pbProgress.setVisibility(View.VISIBLE);

                            break;
//                            handler1.postDelayed(UpdateSongTime1, 30000);
                        }
                    } else if (i == fileNameList.size() - 1) {
                        holder.binding.pbProgress.setVisibility(View.GONE);
                    }
                }
            } else {
                isThreadStart = false;
                holder.binding.pbProgress.setVisibility(View.GONE);
            }
            holder.binding.tvTitle.setText(listModelList.get(position).getName());
            holder.binding.tvTime.setText(listModelList.get(position).getAudioDuration());
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.cvImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.cvImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);

            Glide.with(ctx).load(listModelList.get(position).getImageFile()).thumbnail(0.05f)
                    .placeholder(R.drawable.ic_music_icon).error(R.drawable.ic_music_icon)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            Glide.with(ctx).load(R.drawable.ic_image_bg).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivBackgroundImage);
            comefromDownload = "1";
//            if (IsLock.equalsIgnoreCase("1")) {
//                holder.binding.ivLock.setVisibility(View.VISIBLE);
//            } else if (IsLock.equalsIgnoreCase("2")) {
//                holder.binding.ivLock.setVisibility(View.VISIBLE);
//            } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                holder.binding.ivLock.setVisibility(View.GONE);
//            }

            SharedPreferences sharedzw = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE);
            String AudioPlayerFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");

            if (AudioPlayerFlag.equalsIgnoreCase("DownloadListAudio")) {
                if (BWSApplication.PlayerAudioId.equalsIgnoreCase(listModelList.get(position).getID())) {
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
            } else {
                holder.binding.equalizerview.setVisibility(View.GONE);
                holder.binding.llMainLayout.setBackgroundResource(R.color.white);
                holder.binding.ivBackgroundImage.setVisibility(View.GONE);
            }

            holder.binding.llMainLayout.setOnClickListener(view -> {
                comefromDownload = "1";
//                if (IsLock.equalsIgnoreCase("1")) {
//                    holder.binding.ivLock.setVisibility(View.VISIBLE);
//                    Intent i = new Intent(ctx, MembershipChangeActivity.class);
//                    i.putExtra("ComeFrom", "Plan");
//                    ctx.startActivity(i);
//                } else if (IsLock.equalsIgnoreCase("2")) {
//                    holder.binding.ivLock.setVisibility(View.VISIBLE);
//                    BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
//                } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                    comefromDownload = "1";
                    holder.binding.ivLock.setVisibility(View.GONE);
                int PlayerPosition = sharedzw.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                    SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE);
                    IsPlayDisclimer = (shared1.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1"));
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        if (AudioPlayerFlag.equalsIgnoreCase("DownloadListAudio")) {
                            if (isDisclaimer == 1) {
                                BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                            } else {
                                if (player != null) {
                                    if (position != PlayerPosition) {
                                        int i = player.getMediaItemCount();
                                        if (i < listModelList.size()) {
                                            callTransFrag(position, listModelList, true);
                                        } else {
                                            player.seekTo(position, 0);
                                            player.setPlayWhenReady(true);
                                            callTransFrag(position,listModelList,false);
                                            callAddTransFrag();
                                        }
                                    }else{
                                        callAddTransFrag();
                                    }
                                } else {
                                    callTransFrag(position, listModelList, true);
                                }
                            }
                        } else {
                            List<DownloadAudioDetails> listModelList2 = new ArrayList<>();
                            listModelList2.addAll(listModelList);
                            DownloadAudioDetails mainPlayModel = new DownloadAudioDetails();
                            Gson gson = new Gson();
                            SharedPreferences shared12 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE);
                            String IsPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1");
                            String  DisclimerJson = shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString());
                            Type type = new TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio>() {
                            }.getType();
                            HomeScreenModel.ResponseData.DisclaimerAudio arrayList = gson.fromJson(DisclimerJson, type);
                            mainPlayModel.setID(arrayList.getId());
                            mainPlayModel.setName(arrayList.getName());
                            mainPlayModel.setAudioFile(arrayList.getAudioFile());
                            mainPlayModel.setAudioDirection(arrayList.getAudioDirection());
                            mainPlayModel.setAudiomastercat(arrayList.getAudiomastercat());
                            mainPlayModel.setAudioSubCategory(arrayList.getAudioSubCategory());
                            mainPlayModel.setImageFile(arrayList.getImageFile());
                            mainPlayModel.setAudioDuration(arrayList.getAudioDuration());
                            boolean audioc = true;
                            if (isDisclaimer == 1) {
                                if (player != null) {
                                    player.setPlayWhenReady(true);
                                    audioc = false;
                                    listModelList2.add(position, mainPlayModel);
                                } else {
                                    isDisclaimer = 0;
                                    if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                                        audioc = true;
                                        listModelList2.add(position, mainPlayModel);
                                    }
                                }
                            } else {
                                isDisclaimer = 0;
                                if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                                    audioc = true;
                                    listModelList2.add(position, mainPlayModel);
                                }
                            }
                            callTransFrag(position, listModelList2, audioc);
                        }
                    } else {
                        getMedia(AudioPlayerFlag, position);
                    }
                    Properties p = new Properties();
                    p.putValue("userId", UserID);
                    p.putValue("audioId", listModelList.get(position).getID());
                    p.putValue("audioName", listModelList.get(position).getName());
                    BWSApplication.addToSegment("Downloaded Audio Clicked", p, CONSTANTS.track);
//                }
//            handler3.postDelayed(UpdateSongTime3, 500);
                notifyDataSetChanged();
            });

            holder.binding.llRemoveAudio.setOnClickListener(view -> {
                try {
                    if (AudioPlayerFlag.equalsIgnoreCase("DownloadListAudio")) {
                        String name = "";
                        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE);
                        Gson gson = new Gson();
                        String json = shared.getString(CONSTANTS.PREF_KEY_PlayerAudioList, String.valueOf(gson));
                        Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                        }.getType();
                        ArrayList<MainPlayModel> arrayList = gson.fromJson(json, type);

                        if (arrayList.get(0).getAudioFile().equalsIgnoreCase("")) {
                            arrayList.remove(0);
                        }
                        name = arrayList.get(0).getName();
                        if (  AudioPlayerFlag.equalsIgnoreCase("DownloadListAudio")) {
                            if (isDisclaimer == 1) {
                                BWSApplication.showToast("The audio shall remove after the disclaimer", ctx);
                            } else {
                                if (AudioPlayerFlag.equalsIgnoreCase("DownloadListAudio") && listModelList.size() == 1) {
                                    BWSApplication.showToast("Currently you play this playlist, you can't remove last audio", ctx);
                                } else {
                                    deleteAudio(holder.getAdapterPosition());
                                }
                            }
                        } else {
                            if (AudioPlayerFlag.equalsIgnoreCase("DownloadListAudio") && listModelList.size() == 1) {
                                BWSApplication.showToast("Currently you play this playlist, you can't remove last audio", ctx);
                            } else {
                                deleteAudio(holder.getAdapterPosition());
                            }
                        }
                    } else {
                        deleteAudio(holder.getAdapterPosition());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        private void getMedia(String AudioFlag, int position) {

            int pos = 0;
            SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE);
            String AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
            int PlayerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
            if (AudioPlayerFlag.equalsIgnoreCase("DownloadListAudio")) {
                if (isDisclaimer == 1) {
                    if (player != null) {
                        if (!player.getPlayWhenReady()) {
                            player.setPlayWhenReady(true);
                        }
                    } else {
                        audioClick = true;
                        miniPlayer = 1;
                    }
                    callAddTransFrag();
                    BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                } else {
                    ArrayList<DownloadAudioDetails> listModelList2 = new ArrayList<>();
                    for (int i = 0; i < listModelList.size(); i++) {
                        if (downloadAudioDetailsList.contains(listModelList.get(i).getName())) {
                            listModelList2.add(listModelList.get(i));
                        }
                    }
                    if(player!= null) {
                        if (position != PlayerPosition) {
                            if (downloadAudioDetailsList.contains(listModelList.get(position).getName())) {
                                pos = position;
                                callTransFrag(pos, listModelList2, true);
                            } else {
//                                pos = 0;
                                BWSApplication.showToast(ctx.getString(R.string.no_server_found), ctx);
                            }
                        } else {
                            callAddTransFrag();
                        }
                        if (listModelList2.size() == 0) {
//                                callTransFrag(pos, listModelList2, true);
                            BWSApplication.showToast(ctx.getString(R.string.no_server_found), ctx);
                        }
                    }else{
                        if (downloadAudioDetailsList.contains(listModelList.get(position).getName())) {
                            pos = position;
                            callTransFrag(pos, listModelList2, true);
                        } else {
//                                pos = 0;
                            BWSApplication.showToast(ctx.getString(R.string.no_server_found), ctx);
                        }
                    }
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
                    Gson gson = new Gson();
                    SharedPreferences shared12 =
                            ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                    String IsPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1");
                    String DisclimerJson =
                            shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString());
                    Type type = new TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio>() {
                    }.getType();
                    HomeScreenModel.ResponseData.DisclaimerAudio arrayList =
                            gson.fromJson(DisclimerJson, type);
                    DownloadAudioDetails mainPlayModel=
                            new DownloadAudioDetails();
                    mainPlayModel.setID(arrayList.getId());
                    mainPlayModel.setName(arrayList.getName());
                    mainPlayModel.setAudioFile(arrayList.getAudioFile());
                    mainPlayModel.setAudioDirection(arrayList.getAudioDirection());
                    mainPlayModel.setAudiomastercat(arrayList.getAudiomastercat());
                    mainPlayModel.setAudioSubCategory(arrayList.getAudioSubCategory());
                    mainPlayModel.setImageFile(arrayList.getImageFile());
                    mainPlayModel.setAudioDuration(arrayList.getAudioDuration());
                    boolean audioc = true;
                    if (isDisclaimer == 1) {
                        if (player != null) {
                            player.setPlayWhenReady(true);
                            audioc = false;
                            listModelList2.add(pos, mainPlayModel);
                        } else {
                            isDisclaimer = 0;
                            if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                                audioc = true;
                                listModelList2.add(pos, mainPlayModel);
                            }
                        }
                    } else {
                        isDisclaimer = 0;
                        if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                            audioc = true;
                            listModelList2.add(pos, mainPlayModel);
                        }
                    }

                    if (listModelList2.size() != 0) {
                        if (!listModelList2.get(pos).getAudioFile().equalsIgnoreCase("")) {
                            if (listModelList2.size() != 0) {
                                callTransFrag(pos, listModelList2, audioc);
                            } else {
                                BWSApplication.showToast(ctx.getString(R.string.no_server_found), ctx);
                            }
                        } else if (listModelList2.get(pos).getAudioFile().equalsIgnoreCase("") && listModelList2.size() > 1) {
                            callTransFrag(pos, listModelList2, audioc);
                        } else {
                            BWSApplication.showToast(ctx.getString(R.string.no_server_found), ctx);
                        }
                    } else {
                        BWSApplication.showToast(ctx.getString(R.string.no_server_found), ctx);
                    }
                } else {
                    BWSApplication.showToast(ctx.getString(R.string.no_server_found), ctx);
                }
            }

        }

        private void callTransFrag(int position, List<DownloadAudioDetails> listModelList, boolean audioc) {
            try {
                miniPlayer = 1;
                audioClick = audioc;
                if (audioc) {
                    callNewPlayerRelease();
                }
                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                Gson gson = new Gson();
                String json = gson.toJson(listModelList);
                editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json);
                editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
                editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
                editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "");
                editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "");
                editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "DownloadListAudio");
                editor.apply();
                if(audioc) {
                    callAddTransFrag();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void deleteAudio(int position) {
            getDownloadData();
            final Dialog dialog = new Dialog(ctx);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_popup_layout);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ctx.getResources().getColor(R.color.dark_blue_gray)));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            final TextView tvGoBack = dialog.findViewById(R.id.tvGoBack);
            final TextView tvHeader = dialog.findViewById(R.id.tvHeader);
            final TextView tvTitle = dialog.findViewById(R.id.tvTitle);
            final Button Btn = dialog.findViewById(R.id.Btn);
            tvTitle.setText("Remove audio");
//            tvHeader.setText("Are you sure you want to remove the " + listModelList.get(position).getName() + " from downloads?");
            tvHeader.setText("Audio has been removed");
            Btn.setText("Confirm");
            dialog.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                }
                return false;
            });

            Btn.setOnClickListener(v -> {
//                handler1.removeCallbacks(UpdateSongTime1);
             /*   DatabaseClient
                        .getInstance(getActivity())
                        .getaudioDatabase()
                        .taskDao()
                        .geAllData1("").removeObserver(audioList1 -> {
                });*/
                try {
                    String AudioFile = listModelList.get(position).getAudioFile();
                    String AudioName = listModelList.get(position).getName();
                    try {
                        if (fileNameList.size() != 0) {
                            for (int i = 0; i < fileNameList.size(); i++) {
                                if (fileNameList.get(i).equalsIgnoreCase(listModelList.get(position).getName()) && playlistDownloadId.get(i).equalsIgnoreCase("")) {
                                    if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(listModelList.get(position).getName())) {
//                                if (downloadProgress <= 100) {
                                        PRDownloader.cancel(downloadIdOne);
//                                }
                                    } else {
                                        fileNameList.remove(i);
                                        playlistDownloadId.remove(i);
                                        audiofilelist.remove(i);
                                        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
                                        SharedPreferences.Editor editor = shared.edit();
                                        Gson gson = new Gson();
                                        String urlJson = gson.toJson(audiofilelist);
                                        String nameJson = gson.toJson(fileNameList);
                                        String playlistIdJson = gson.toJson(playlistDownloadId);
                                        editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson);
                                        editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson);
                                        editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
                                        editor.commit();
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("DownloadHangCrash", e.getMessage());
                    }
                    SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE);
                    String AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
                    int pos = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                    try {
                        Properties p = new Properties();
                        p.putValue("coUserId", CoUserID);
                        p.putValue("audioId", listModelList.get(position).getID());
                        p.putValue("audioName", listModelList.get(position).getName());
                        p.putValue("audioDescription", "");
                        p.putValue("audioDuration", listModelList.get(position).getAudioDuration());
                        p.putValue("directions", listModelList.get(position).getAudioDirection());
                        p.putValue("masterCategory", listModelList.get(position).getAudiomastercat());
                        p.putValue("subCategory", listModelList.get(position).getAudioSubCategory());
                        p.putValue("audioService", appStatus(getActivity()));
                        p.putValue("audioType", "Downloaded");
                        p.putValue("bitRate", "");
                        p.putValue("sound", String.valueOf(hundredVolume));
                        BWSApplication.addToSegment("Downloaded Audio Removed", p, CONSTANTS.track);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    listModelList.remove(position);
                    if (AudioPlayerFlag.equalsIgnoreCase("DownloadListAudio")) {
                        if (player != null) {
                            player.removeMediaItem(position);
                            if(player.getPlayWhenReady()){
                                player.setPlayWhenReady(true);
                            }else{
                                player.setPlayWhenReady(false);
                            }
                        }
                        if (pos == position && position < listModelList.size() - 1) {
//                                            pos = pos + 1;
                            if (isDisclaimer == 1) {
//                                    BWSApplication.showToast("The audio shall remove after the disclaimer", getActivity());
                            } else {
                                if (player != null) {
//                                player.seekTo(pos);
                                    callSaveToPref(pos, listModelList);
                                } else {
                                    callTransFrag(pos, listModelList, false);
                                }
                            }
                        } else if (pos == position && position == listModelList.size() - 1) {
                            pos = 0;
                            if (isDisclaimer == 1) {
//                                    BWSApplication.showToast("The audio shall remove after the disclaimer", getActivity());
                            } else {
                                if (player != null) {
//                                player.seekTo(pos);
                                    callSaveToPref(pos, listModelList);
                                } else {
                                    callTransFrag(pos, listModelList, false);
                                }
                            }
                        } else if (pos < position && pos < listModelList.size() - 1) {
                            if (player != null) {
//                                player.seekTo(pos);
                                callSaveToPref(pos, listModelList);
                            } else {
                                callTransFrag(pos, listModelList, false);
                            }
                        } else if (pos > position && pos == listModelList.size()) {
                            pos = pos - 1;
                            if (player != null) {
//                                player.seekTo(pos);
                                callSaveToPref(pos, listModelList);
                            } else {
                                callTransFrag(pos, listModelList, false);
                            }
                        }
                    }
                    deleteDownloadFile(AudioFile, AudioName, position);
                    notifyItemRemoved(position);
                    dialog.dismiss();
                } catch (Exception e) {
                }
            });
            tvGoBack.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
            dialog.setCancelable(false);
        }

        private void callSaveToPref(int position, List<DownloadAudioDetails> listModelList) {

            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String json = gson.toJson(listModelList);
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json);
            editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
            editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
            editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "");
            editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "");
            editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "DownloadListAudio");
            editor.apply();
        }

        private void deleteDownloadFile(String audioFile, String audioName, int position) {

            AudioDatabase.databaseWriteExecutor.execute(() -> {
                DB.taskDao().deleteByAudioFile(audioFile, "",CoUserID);
            });

            DB.taskDao().getLastIdByuIdForAll(audioFile).observe((LifecycleOwner) ctx, audioList -> {
                if (audioList.size() == 0 || audioList == null) {
                    try {
                        FileUtils.deleteDownloadedFile(ctx, audioName);
                    } catch (Exception e) {

                    }
                }
                callObserverMethod();
                DB.taskDao().getLastIdByuIdForAll(audioFile).removeObserver(audioListx -> {
                });
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
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            AudioDownloadsLayoutBinding binding;

            public MyViewHolder(AudioDownloadsLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}