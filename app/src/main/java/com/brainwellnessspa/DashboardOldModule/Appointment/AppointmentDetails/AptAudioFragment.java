package com.brainwellnessspa.DashboardOldModule.Appointment.AppointmentDetails;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.billingOrderModule.activities.MembershipChangeActivity;
import com.brainwellnessspa.dashboardModule.activities.AddPlaylistActivity;
import com.brainwellnessspa.DashboardOldModule.Models.AppointmentDetailModel;
import com.brainwellnessspa.DashboardOldModule.TransparentPlayer.Fragments.MiniPlayerFragment;
import com.brainwellnessspa.DashboardOldModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia;
import com.brainwellnessspa.EncryptDecryptUtils.FileUtils;
import com.brainwellnessspa.R;
import com.brainwellnessspa.roomDataBase.AudioDatabase;
import com.brainwellnessspa.roomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.services.GlobalInitExoPlayer;
import com.brainwellnessspa.utility.CONSTANTS;
import com.brainwellnessspa.utility.MeasureRatio;
import com.brainwellnessspa.dashboardModule.models.HomeScreenModel;
import com.brainwellnessspa.databinding.AudioAptListLayoutBinding;
import com.brainwellnessspa.databinding.FragmentAptAudioBinding;
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

import static android.content.Context.MODE_PRIVATE;
import static com.brainwellnessspa.BWSApplication.DB;
import static com.brainwellnessspa.BWSApplication.getAudioDataBase;
import static com.brainwellnessspa.DashboardOldModule.Activities.DashboardActivity.audioClick;
import static com.brainwellnessspa.DashboardOldModule.Activities.DashboardActivity.miniPlayer;


import static com.brainwellnessspa.DashboardOldModule.TransparentPlayer.Fragments.MiniPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.downloadProgress;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.filename;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.isDownloading;
import static com.brainwellnessspa.services.GlobalInitExoPlayer.GetCurrentAudioPosition;
import static com.brainwellnessspa.services.GlobalInitExoPlayer.GetSourceName;
import static com.brainwellnessspa.services.GlobalInitExoPlayer.callNewPlayerRelease;
import static com.brainwellnessspa.services.GlobalInitExoPlayer.player;

public class AptAudioFragment extends Fragment {
    public static int comeRefreshData = 0;
    public FragmentManager f_manager;
    public AudioManager audioManager;
    public int hundredVolume = 0, currentVolume = 0, maxVolume = 0, percent;
    FragmentAptAudioBinding binding;
    String UserID, AudioFlag, IsPlayDisclimer;
    ArrayList<AppointmentDetailModel.Audio> appointmentDetail;
    //    Handler handler3;
    int startTime;
    AudioListAdapter appointmentsAdapter;
    long myProgress = 0;
    Properties p;
//    private Handler handler1;
    List<String> fileNameList = new ArrayList<>(), playlistDownloadId = new ArrayList<>(), audiofilelist = new ArrayList<>();
    private long currentDuration = 0;
    //    private Runnable UpdateSongTime3;
    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("MyData")) {
                String data = intent.getStringExtra("MyData");
                Log.d("play_pause_Action", data);
                SharedPreferences sharedzw = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                 AudioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
                String pIDz = sharedzw.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
                if (AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
                    if (player != null) {
                        if (data.equalsIgnoreCase("play")) {
//                    BWSApplication.showToast("Play", getActivity());
                            appointmentsAdapter.notifyDataSetChanged();
                        } else {
//                    BWSApplication.showToast("pause", getActivity());
                            appointmentsAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }
    };
    private BroadcastReceiver listener1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getDownloadData();
            if (intent.hasExtra("Progress")) {
                appointmentsAdapter.notifyDataSetChanged();
            }
        }
    };
    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(listener, new IntentFilter("play_pause_Action"));
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apt_audio, container, false);
        View view = binding.getRoot();
//        handler1 = new Handler();
//        handler3 = new Handler();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(listener, new IntentFilter("play_pause_Action"));
        appointmentDetail = new ArrayList<>();
        if (getArguments() != null) {
            appointmentDetail = getArguments().getParcelableArrayList("AppointmentDetailList");
        }
        DB = getAudioDataBase(getActivity());
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        percent = 100;
        hundredVolume = (int) (currentVolume * percent) / maxVolume;

        if (appointmentDetail.size() == 0) {
        } else {
            appointmentsAdapter = new AudioListAdapter(appointmentDetail, getActivity(), f_manager);
            RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            binding.rvAudioList.setLayoutManager(recentlyPlayed);
            binding.rvAudioList.setItemAnimator(new DefaultItemAnimator());
            binding.rvAudioList.setAdapter(appointmentsAdapter);
            LocalBroadcastManager.getInstance(getActivity())
                    .registerReceiver(listener1, new IntentFilter("DownloadProgress"));
        }
        return view;
    }


    @Override
    public void onPause() {
//        handler3.removeCallbacks(UpdateSongTime3);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(listener);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(listener1);
        super.onDestroy();
    }
    private void getDownloadData() {
        try {
            SharedPreferences sharedy = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
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
                }
            } else {
                fileNameList = new ArrayList<>();
                audiofilelist = new ArrayList<>();
                playlistDownloadId = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void GetMedia(String AudioFile, Context ctx, String download, RelativeLayout llDownload, ImageView ivDownload) {
        SharedPreferences shared1 =
                ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        String UserId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "");
        String CoUserID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "");

        DB = getAudioDataBase(ctx);
        DB.taskDao()
                .getLastIdByuId1(AudioFile,CoUserID).observe(getActivity(), audioList -> {
            if (audioList.size() != 0) {
//                if (audioList.get(0).getDownload().equalsIgnoreCase("1")) {
                    disableDownload(llDownload, ivDownload);
//                }
            } else if (download.equalsIgnoreCase("1")) {
                disableDownload(llDownload, ivDownload);
            } else {
                enableDownload(llDownload, ivDownload);
            }

        });
    }

    private void enableDownload(RelativeLayout llDownload, ImageView ivDownload) {
        try {
            llDownload.setClickable(true);
            llDownload.setEnabled(true);
            ivDownload.setColorFilter(getActivity().getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
            ivDownload.setImageResource(R.drawable.ic_download_white_icon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void disableDownload(RelativeLayout llDownload, ImageView ivDownload) {
        try {
            ivDownload.setImageResource(R.drawable.ic_download_white_icon);
            ivDownload.setColorFilter(getActivity().getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
            llDownload.setClickable(false);
            llDownload.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callAddTransFrag() {

        Fragment fragment = new MiniPlayerFragment();
        FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
        fragmentManager1.beginTransaction()
                .add(R.id.flContainer, fragment)
                .commit();
    }

    public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.MyViewHolder> {
        public FragmentManager f_manager;
        Context ctx;
        String Name, songId;
        int ps = 0, nps = 0;
        Runnable UpdateSongTime1;
        private ArrayList<AppointmentDetailModel.Audio> listModelList;

        public AudioListAdapter(ArrayList<AppointmentDetailModel.Audio> listModelList, Context ctx, FragmentManager f_manager) {
            this.listModelList = listModelList;
            this.ctx = ctx;
            this.f_manager = f_manager;
            SharedPreferences sharedx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
            Gson gson = new Gson();
            String json = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson));
            if (!json.equalsIgnoreCase(String.valueOf(gson))) {
                Type type = new TypeToken<List<String>>() {
                }.getType();
                fileNameList = gson.fromJson(json, type);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            AudioAptListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.audio_apt_list_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            AppointmentDetailModel.Audio audiolist = listModelList.get(position);
         /*   UpdateSongTime1 = new Runnable() {
                @Override
                public void run() {
                    for (int f = 0; f < listModelList.size(); f++) {
                        if (fileNameList.size() != 0) {
                            for (int i = 0; i < fileNameList.size(); i++) {
                                if (fileNameList.get(i).equalsIgnoreCase(listModelList.get(f).getName())) {
                                    if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(listModelList.get(f).getName())) {
                                        if (downloadProgress <= 100) {
                                            if (BWSApplication.isNetworkConnected(ctx)) {
                                                notifyItemChanged(f);
                                            }
                                        } else {
                                            holder.binding.pbProgress.setVisibility(View.GONE);
                                            getDownloadData();
                                        }
                                    } else {
                                        if (BWSApplication.isNetworkConnected(ctx)) {
                                            notifyItemChanged(f);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (downloadProgress == 0) {
                        if (BWSApplication.isNetworkConnected(ctx)) {
                            notifyDataSetChanged();
                        }
                        getDownloadData();
                    }
                    handler1.postDelayed(this, 300);
                }
            };*/
//            holder.binding.equalizerview.setcolo
            SharedPreferences sharedzw = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            AudioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
            String pIDz = sharedzw.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
//            if (audioPlayz && (AudioFlag.equalsIgnoreCase("AppointmentDetailList") ||
//                    AudioFlag.equalsIgnoreCase("MainAudioList") || AudioFlag.equalsIgnoreCase("ViewAllAudioList"))) {
//                if (myAudioId.equalsIgnoreCase(audiolist.getID())) {
//                    songId = myAudioId;
//                    if (player != null) {
//                        if (!player.getPlayWhenReady()) {
//                            holder.binding.equalizerview.pause();
//                        } else
//                            holder.binding.equalizerview.resume(true);
//                    } else
//                        holder.binding.equalizerview.stop(true);
//                    holder.binding.equalizerview.setVisibility(View.VISIBLE);
//                    holder.binding.ivPlayIcon.setVisibility(View.GONE);
//                    holder.binding.llMainLayout.setBackgroundResource(R.color.highlight_background);
//                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                } else {
//                    holder.binding.equalizerview.setVisibility(View.GONE);
//                    holder.binding.ivPlayIcon.setVisibility(View.VISIBLE);
//                    holder.binding.llMainLayout.setBackgroundResource(R.color.white);
//                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                }
////                handler3.postDelayed(UpdateSongTime3, 500);
//            } else {
                holder.binding.equalizerview.setVisibility(View.GONE);
                holder.binding.ivPlayIcon.setVisibility(View.VISIBLE);
                holder.binding.llMainLayout.setBackgroundResource(R.color.white);
                holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                handler3.removeCalldobacks(UpdateSongTime3);
//            }
            holder.binding.tvTitle.setText(audiolist.getName());
            if (audiolist.getAudioDirection().equalsIgnoreCase("")) {
                holder.binding.tvTime.setVisibility(View.GONE);
            } else {
                holder.binding.tvTime.setVisibility(View.VISIBLE);
                holder.binding.tvTime.setText(audiolist.getAudioDirection());
            }
            if (fileNameList.size() != 0) {
                for (int i = 0; i < fileNameList.size(); i++) {
                    if (fileNameList.get(i).equalsIgnoreCase(listModelList.get(position).getName()) && playlistDownloadId.get(i).equalsIgnoreCase("")) {
                        if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(listModelList.get(position).getName())) {
                            if (downloadProgress <= 100) {
                                if (downloadProgress == 100) {
                                    holder.binding.pbProgress.setVisibility(View.GONE);
                                    holder.binding.ivDownload.setVisibility(View.VISIBLE);
                                } else {
                                    holder.binding.pbProgress.setProgress(downloadProgress);
                                    holder.binding.pbProgress.setVisibility(View.VISIBLE);
                                    holder.binding.ivDownload.setVisibility(View.GONE);
                                }
                            } else {
                                holder.binding.pbProgress.setVisibility(View.GONE);
                                holder.binding.ivDownload.setVisibility(View.VISIBLE);
                            }
                            break;
                        } else {
                            holder.binding.pbProgress.setProgress(0);
                            holder.binding.pbProgress.setVisibility(View.VISIBLE);
                            holder.binding.ivDownload.setVisibility(View.GONE);
                            break;
                        }
                    } else if (i == fileNameList.size() - 1) {
                        holder.binding.pbProgress.setVisibility(View.GONE);
                        holder.binding.ivDownload.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                holder.binding.pbProgress.setVisibility(View.GONE);
                holder.binding.ivDownload.setVisibility(View.VISIBLE);
            }
            GetMedia(audiolist.getAudioFile(), getActivity(), audiolist.getDownload(), holder.binding.llDownload, holder.binding.ivDownload);

            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.13f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.binding.ivBackgroundImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(getActivity()).load(audiolist.getImageFile()).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            Glide.with(getActivity()).load(R.drawable.ic_image_bg).thumbnail(0.05f)

                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivBackgroundImage);
            holder.binding.llMainLayout.setOnClickListener(view -> {
                comeRefreshData = 1;
                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
                String MyPlaylist = shared.getString(CONSTANTS.PREF_KEY_PlayFrom, "");
                SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                IsPlayDisclimer = (shared1.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1"));
                if (AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
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
                        BWSApplication.showToast("The audio shall start playing after the disclaimer", getActivity());
                    } else {
                        ArrayList<AppointmentDetailModel.Audio> listModelList2 = new ArrayList<>();
                        listModelList2.add(listModelList.get(position));
                        callTransFrag(0, listModelList2, true);
                    }
                } else {
                    ArrayList<AppointmentDetailModel.Audio> listModelList2 = new ArrayList<>();
                    listModelList2.add(listModelList.get(position));
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
                    AppointmentDetailModel.Audio mainPlayModel=
                            new AppointmentDetailModel.Audio();
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
                            listModelList2.add(0,mainPlayModel);
                        } else {
                            isDisclaimer = 0;
                            if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                                audioc = true;
                                listModelList2.add(0,mainPlayModel);
                            }
                        }
                    } else {
                        isDisclaimer = 0;
                        if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                            audioc = true;
                            listModelList2.add(0,mainPlayModel);
                        }
                    }
                    callTransFrag(0, listModelList2, audioc);
                }
            });

            holder.binding.llDownload.setOnClickListener(view -> {
                List<String> url1 = new ArrayList<>();
                List<String> name1 = new ArrayList<>();
                List<String> downloadPlaylistId = new ArrayList<>();
                SharedPreferences sharedx = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
                Gson gson1 = new Gson();
                String json = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson1));
                String json1 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson1));
                String json2 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson1));
                if (!json1.equalsIgnoreCase(String.valueOf(gson1))) {
                    Type type = new TypeToken<List<String>>() {
                    }.getType();
                    List<String> fileNameList = gson1.fromJson(json, type);
                    List<String> audioFile1 = gson1.fromJson(json1, type);
                    List<String> playlistId1 = gson1.fromJson(json2, type);
                    if (fileNameList.size() != 0) {
                        url1.addAll(audioFile1);
                        name1.addAll(fileNameList);
                        downloadPlaylistId.addAll(playlistId1);
                    }
                }
                Name = listModelList.get(position).getName();
                String audioFile = listModelList.get(position).getAudioFile();
                url1.add(audioFile);
                name1.add(Name);
                downloadPlaylistId.add("");
                if (url1.size() != 0) {
                    SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    Gson gson = new Gson();
                    String urlJson = gson.toJson(url1);
                    String nameJson = gson.toJson(name1);
                    String playlistIdJson = gson.toJson(downloadPlaylistId);
                    editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson);
                    editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson);
                    editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
                    editor.commit();
                }
                if (!isDownloading) {
                    isDownloading = true;
                    DownloadMedia downloadMedia = new DownloadMedia(getActivity().getApplicationContext(),getActivity());
                    downloadMedia.encrypt1(url1, name1, downloadPlaylistId);
                }
                holder.binding.pbProgress.setVisibility(View.VISIBLE);
                holder.binding.ivDownload.setVisibility(View.GONE);
                fileNameList = url1;
//                handler1.postDelayed(UpdateSongTime1, 500);
                String dirPath = FileUtils.getFilePath(getActivity().getApplicationContext(), Name);
                SaveMedia(new byte[1024], dirPath, listModelList.get(position), holder.binding.llDownload);
            });

            holder.binding.llRemoveAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listModelList.get(position).getIsLock().equalsIgnoreCase("1")) {
                        Intent i = new Intent(ctx, MembershipChangeActivity.class);
                        i.putExtra("ComeFrom", "Plan");
                        ctx.startActivity(i);
                    } else if (listModelList.get(position).getIsLock().equalsIgnoreCase("2")) {
                        BWSApplication.showToast(getString(R.string.reactive_plan), getActivity());
                    } else if (listModelList.get(position).getIsLock().equalsIgnoreCase("0") ||
                            listModelList.get(position).getIsLock().equalsIgnoreCase("")) {
                        Intent i = new Intent(ctx, AddPlaylistActivity.class);
                        i.putExtra("AudioId", listModelList.get(position).getID());
                        i.putExtra("ScreenView", "Appointment Audio Screen");
                        i.putExtra("PlaylistID", "");
                        i.putExtra("PlaylistName", "");
                        i.putExtra("PlaylistImage", "");
                        i.putExtra("PlaylistType", "");
                        i.putExtra("Liked", "0");
                        startActivity(i);
                    }
                }
            });
        }

        private void callTransFrag(int position, ArrayList<AppointmentDetailModel.Audio> listModelList, boolean audioc) {
            try {
                miniPlayer = 1;
                audioClick = audioc;
                if (audioc) {
                    callNewPlayerRelease();
                }
                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                Gson gson = new Gson();
                String json = gson.toJson(listModelList);
                editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json);
                editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
                editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "");
                editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "");
                editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "AppointmentDetailList");
                editor.commit();
                boolean commit = editor.commit();
                callAddTransFrag();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void SaveMedia(byte[] encodeBytes, String dirPath, AppointmentDetailModel.Audio audio, RelativeLayout llDownload) {
            SharedPreferences shared1 =
                    ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
            String UserId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "");
            String CoUserID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "");

            DownloadAudioDetails downloadAudioDetails = new DownloadAudioDetails();
            downloadAudioDetails.setUserId(CoUserID);
            downloadAudioDetails.setID(audio.getID());
            downloadAudioDetails.setName(audio.getName());
            downloadAudioDetails.setAudioFile(audio.getAudioFile());
            downloadAudioDetails.setPlaylistId("");
            downloadAudioDetails.setAudioDirection(audio.getAudioDirection());
            downloadAudioDetails.setAudiomastercat(audio.getAudiomastercat());
            downloadAudioDetails.setAudioSubCategory(audio.getAudioSubCategory());
            downloadAudioDetails.setImageFile(audio.getImageFile());
            downloadAudioDetails.setAudioDuration(audio.getAudioDuration());
            downloadAudioDetails.setIsSingle("1");
            downloadAudioDetails.setPlaylistId("");
            downloadAudioDetails.setIsDownload("pending");
            downloadAudioDetails.setDownloadProgress(0);

            try {
                p.putValue("userId", UserID);
                p.putValue("audioId", downloadAudioDetails.getID());
                p.putValue("audioName", downloadAudioDetails.getName());
                p.putValue("audioDescription", "");
                p.putValue("directions", downloadAudioDetails.getAudioDirection());
                p.putValue("masterCategory", downloadAudioDetails.getAudiomastercat());
                p.putValue("subCategory", downloadAudioDetails.getAudioSubCategory());
                p.putValue("audioDuration", downloadAudioDetails.getAudioDuration());
                p.putValue("position", GetCurrentAudioPosition());
                String name = audio.getName();
                if (name.contains(downloadAudioDetails.getName())) {
                    p.putValue("audioType", "Downloaded");
                } else {
                    p.putValue("audioType", "Streaming");
                }
                p.putValue("source", GetSourceName(getActivity()));
                p.putValue("bitRate", "");
                p.putValue("sound", String.valueOf(hundredVolume));
                BWSApplication.addToSegment("Audio Download Started", p, CONSTANTS.track);
            } catch (Exception e) {
                e.printStackTrace();
            }

            SharedPreferences sharedx1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            String AudioFlag = sharedx1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
            Gson gsonx = new Gson();
            String json11 = sharedx1.getString(CONSTANTS.PREF_KEY_PlayerAudioList, String.valueOf(gsonx));
            String jsonw = sharedx1.getString(CONSTANTS.PREF_KEY_MainAudioList, String.valueOf(gsonx));
            ArrayList<DownloadAudioDetails> arrayList = new ArrayList<>();
            ArrayList<MainPlayModel> arrayList2 = new ArrayList<>();
            int size = 0;
            if (!jsonw.equalsIgnoreCase(String.valueOf(gsonx))) {
                Type type1 = new TypeToken<ArrayList<DownloadAudioDetails>>() {
                }.getType();
                Type type0 = new TypeToken<ArrayList<MainPlayModel>>() {
                }.getType();
                Gson gson1 = new Gson();
                arrayList = gson1.fromJson(jsonw, type1);
                arrayList2 = gson1.fromJson(json11, type0);
                size = arrayList2.size();
            }
            int position = sharedx1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
            if (AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
                arrayList.add(downloadAudioDetails);
                MainPlayModel mainPlayModel1 = new MainPlayModel();
                mainPlayModel1.setID(downloadAudioDetails.getID());
                mainPlayModel1.setName(downloadAudioDetails.getName());
                mainPlayModel1.setAudioFile(downloadAudioDetails.getAudioFile());
                mainPlayModel1.setAudioDirection(downloadAudioDetails.getAudioDirection());
                mainPlayModel1.setAudiomastercat(downloadAudioDetails.getAudiomastercat());
                mainPlayModel1.setAudioSubCategory(downloadAudioDetails.getAudioSubCategory());
                mainPlayModel1.setImageFile(downloadAudioDetails.getImageFile());
                mainPlayModel1.setAudioDuration(downloadAudioDetails.getAudioDuration());
                arrayList2.add(mainPlayModel1);
                SharedPreferences sharedd = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedd.edit();
                Gson gson = new Gson();
                String jsonx = gson.toJson(arrayList2);
                String json1q1 = gson.toJson(arrayList);
                editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json1q1);
                editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonx);
                editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
                editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
                editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "");
                editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "");
                editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "DownloadListAudio");
                editor.commit();

                if (!arrayList2.get(position).getAudioFile().equals("")) {
                    List<String> downloadAudioDetailsList = new ArrayList<>();
                    GlobalInitExoPlayer ge = new GlobalInitExoPlayer();
                    downloadAudioDetailsList.add(downloadAudioDetails.getName());
                    ge.AddAudioToPlayer(size, arrayList2, downloadAudioDetailsList, ctx);
                }
                callAddTransFrag();
            }
            DB = getAudioDataBase(ctx);
            try {
                AudioDatabase.databaseWriteExecutor.execute(() -> DB.taskDao().insertMedia(downloadAudioDetails));
            }catch(Exception|OutOfMemoryError e) {
                System.out.println(e.getMessage());
            }
            llDownload.setClickable(false);
            llDownload.setEnabled(false);

        }

        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            AudioAptListLayoutBinding binding;

            public MyViewHolder(AudioAptListLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}