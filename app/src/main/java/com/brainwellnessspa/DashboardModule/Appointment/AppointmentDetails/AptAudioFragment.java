package com.brainwellnessspa.DashboardModule.Appointment.AppointmentDetails;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import com.brainwellnessspa.BillingOrderModule.Activities.MembershipChangeActivity;
import com.brainwellnessspa.DashboardModule.Activities.AddPlaylistActivity;
import com.brainwellnessspa.DashboardModule.Models.AppointmentDetailModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia;
import com.brainwellnessspa.EncryptDecryptUtils.FileUtils;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.Services.GlobalInitExoPlayer;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.AudioAptListLayoutBinding;
import com.brainwellnessspa.databinding.FragmentAptAudioBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.audioClick;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.miniPlayer;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.myAudioId;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.downloadProgress;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.filename;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.isDownloading;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.callNewPlayerRelease;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.player;

public class AptAudioFragment extends Fragment {
    public static int comeRefreshData = 0;
    public FragmentManager f_manager;
    FragmentAptAudioBinding binding;
    String UserID, AudioFlag;
    ArrayList<AppointmentDetailModel.Audio> appointmentDetail;
    //    Handler handler3;
    int startTime;
    AudioListAdapter appointmentsAdapter;
    long myProgress = 0;
    private Handler handler1;
    private long currentDuration = 0;
    //    private Runnable UpdateSongTime3;
    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("MyData")) {
                String data = intent.getStringExtra("MyData");
                Log.d("play_pause_Action", data);
                SharedPreferences sharedzw = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                boolean audioPlayz = sharedzw.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                AudioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pIDz = sharedzw.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                if (audioPlayz && AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
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

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(listener, new IntentFilter("play_pause_Action"));
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apt_audio, container, false);
        View view = binding.getRoot();
        handler1 = new Handler();
//        handler3 = new Handler();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(listener, new IntentFilter("play_pause_Action"));
        appointmentDetail = new ArrayList<>();
        if (getArguments() != null) {
            appointmentDetail = getArguments().getParcelableArrayList("AppointmentDetailList");
        }
        if (appointmentDetail.size() == 0) {
        } else {
            appointmentsAdapter = new AudioListAdapter(appointmentDetail, getActivity(), f_manager);
            RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            binding.rvAudioList.setLayoutManager(recentlyPlayed);
            binding.rvAudioList.setItemAnimator(new DefaultItemAnimator());
            binding.rvAudioList.setAdapter(appointmentsAdapter);
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
        super.onDestroy();
    }

    public void GetMedia(String AudioFile, Context ctx, String download, RelativeLayout llDownload, ImageView ivDownload) {
      /*  oneAudioDetailsList = new ArrayList<>();
        class GetMedia extends AsyncTask<Void, Void, Void> {
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
                    if (oneAudioDetailsList.get(0).getDownload().equalsIgnoreCase("1")) {
                        disableDownload(llDownload, ivDownload);
                    }
                } else if (download.equalsIgnoreCase("1")) {
                    disableDownload(llDownload, ivDownload);
                } else {
                    enableDownload(llDownload, ivDownload);
                }
                super.onPostExecute(aVoid);
            }
        }
        GetMedia st = new GetMedia();
        st.execute();*/
        DatabaseClient
                .getInstance(getActivity())
                .getaudioDatabase()
                .taskDao()
                .getLastIdByuId1(AudioFile).observe(getActivity(), audioList -> {
            if (audioList.size() != 0) {
                if (audioList.get(0).getDownload().equalsIgnoreCase("1")) {
                    disableDownload(llDownload, ivDownload);
                }
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
        List<String> fileNameList = new ArrayList<>(), playlistDownloadId = new ArrayList<>();
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
            UpdateSongTime1 = new Runnable() {
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
            };
            SharedPreferences sharedzw = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            boolean audioPlayz = sharedzw.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
            AudioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            String pIDz = sharedzw.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
            if (audioPlayz && (AudioFlag.equalsIgnoreCase("AppointmentDetailList") ||
                    AudioFlag.equalsIgnoreCase("MainAudioList") || AudioFlag.equalsIgnoreCase("ViewAllAudioList"))) {
                if (myAudioId.equalsIgnoreCase(audiolist.getID())) {
                    songId = myAudioId;
                    if (player != null) {
                        if (!player.getPlayWhenReady()) {
                            holder.binding.equalizerview.stopBars();
                        } else
                            holder.binding.equalizerview.animateBars();
                    } else
                        holder.binding.equalizerview.stopBars();
                    holder.binding.equalizerview.setVisibility(View.VISIBLE);
                    holder.binding.ivPlayIcon.setVisibility(View.GONE);
                    holder.binding.llMainLayout.setBackgroundResource(R.color.highlight_background);
                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                    holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg);
                } else {
                    holder.binding.equalizerview.setVisibility(View.GONE);
                    holder.binding.ivPlayIcon.setVisibility(View.VISIBLE);
                    holder.binding.llMainLayout.setBackgroundResource(R.color.white);
                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                }
//                handler3.postDelayed(UpdateSongTime3, 500);
            } else {
                holder.binding.equalizerview.setVisibility(View.GONE);
                holder.binding.ivPlayIcon.setVisibility(View.VISIBLE);
                holder.binding.llMainLayout.setBackgroundResource(R.color.white);
                holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                handler3.removeCalldobacks(UpdateSongTime3);
            }
            holder.binding.tvTitle.setText(audiolist.getName());
            if (audiolist.getAudioDirection().equalsIgnoreCase("")) {
                holder.binding.tvTime.setVisibility(View.GONE);
            } else {
                holder.binding.tvTime.setVisibility(View.VISIBLE);
                holder.binding.tvTime.setText(audiolist.getAudioDirection());
            }
            if (fileNameList.size() != 0) {
                for (int i = 0; i < fileNameList.size(); i++) {
                    if (fileNameList.get(i).equalsIgnoreCase(listModelList.get(position).getName())) {
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
                        } else {
                            holder.binding.pbProgress.setVisibility(View.VISIBLE);
                            holder.binding.ivDownload.setVisibility(View.GONE);
                            handler1.postDelayed(UpdateSongTime1, 200);
                        }
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
            holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg);
            Glide.with(getActivity()).load(audiolist.getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

            holder.binding.llMainLayout.setOnClickListener(view -> {
                comeRefreshData = 1;
                try {
                    miniPlayer = 1;
                    audioClick = true;

                    callNewPlayerRelease();

                    SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    Gson gson = new Gson();
                    ArrayList<AppointmentDetailModel.Audio> listModelList2 = new ArrayList<>();
                    AppointmentDetailModel.Audio mainPlayModel = new AppointmentDetailModel.Audio();
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
                    listModelList2.add(listModelList.get(position));

                    String json = gson.toJson(listModelList2);
                    editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                    editor.putInt(CONSTANTS.PREF_KEY_position, 0);
                    editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                    editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                    editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                    editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
                    editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "AppointmentDetailList");
                    editor.commit();


                    Fragment fragment = new MiniPlayerFragment();
                    FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                    fragmentManager1.beginTransaction()
                            .add(R.id.flContainer, fragment)
                            .commit();
//                    handler3.postDelayed(UpdateSongTime3, 500);
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
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
                    DownloadMedia downloadMedia = new DownloadMedia(getActivity().getApplicationContext());
                    downloadMedia.encrypt1(url1, name1, downloadPlaylistId);
                }
                holder.binding.pbProgress.setVisibility(View.VISIBLE);
                holder.binding.ivDownload.setVisibility(View.GONE);
                fileNameList = url1;
                handler1.postDelayed(UpdateSongTime1, 500);
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
                        BWSApplication.showToast("Please re-activate your membership plan", ctx);
                    } else if (listModelList.get(position).getIsLock().equalsIgnoreCase("0") ||
                            listModelList.get(position).getIsLock().equalsIgnoreCase("")) {
                        Intent i = new Intent(ctx, AddPlaylistActivity.class);
                        i.putExtra("AudioId", listModelList.get(position).getID());
                        i.putExtra("ScreenView", "Appointment Audio Screen");
                        i.putExtra("PlaylistID", "");
                        startActivity(i);
                    }
                }
            });
        }

        private void getDownloadData() {
            try {
                SharedPreferences sharedy = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
                Gson gson = new Gson();
                String jsony = sharedy.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson));
                String jsonq = sharedy.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson));
                if (!jsony.equalsIgnoreCase(String.valueOf(gson))) {
                    Type type = new TypeToken<List<String>>() {
                    }.getType();
                    fileNameList = gson.fromJson(jsony, type);
                    playlistDownloadId = gson.fromJson(jsonq, type);
                    if (fileNameList.size() != 0) {
                        handler1.postDelayed(UpdateSongTime1, 500);
                    } else {
                        fileNameList = new ArrayList<>();
                        playlistDownloadId = new ArrayList<>();
                    }
                } else {
                    fileNameList = new ArrayList<>();
                    playlistDownloadId = new ArrayList<>();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void SaveMedia(byte[] encodeBytes, String dirPath, AppointmentDetailModel.Audio audio, RelativeLayout llDownload) {
            class SaveMedia extends AsyncTask<Void, Void, Void> {
                @Override
                protected Void doInBackground(Void... voids) {
                    DownloadAudioDetails downloadAudioDetails = new DownloadAudioDetails();
                    downloadAudioDetails.setID(audio.getID());
                    downloadAudioDetails.setName(audio.getName());
                    downloadAudioDetails.setAudioFile(audio.getAudioFile());
                    downloadAudioDetails.setPlaylistId("");
                    downloadAudioDetails.setAudioDirection(audio.getAudioDirection());
                    downloadAudioDetails.setAudiomastercat(audio.getAudiomastercat());
                    downloadAudioDetails.setAudioSubCategory(audio.getAudioSubCategory());
                    downloadAudioDetails.setImageFile(audio.getImageFile());
                    downloadAudioDetails.setLike(audio.getLike());
                    downloadAudioDetails.setDownload("1");
                    downloadAudioDetails.setAudioDuration(audio.getAudioDuration());
                    downloadAudioDetails.setIsSingle("1");
                    downloadAudioDetails.setPlaylistId("");
                    downloadAudioDetails.setIsDownload("pending");
                    downloadAudioDetails.setDownloadProgress(0);

                    SharedPreferences sharedx1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                    String AudioFlag = sharedx1.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                    boolean audioPlay = sharedx1.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                    Gson gsonx = new Gson();
                    String json11 = sharedx1.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gsonx));
                    String jsonw = sharedx1.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gsonx));
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
                    int position = sharedx1.getInt(CONSTANTS.PREF_KEY_position, 0);
                    if (audioPlay && AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
                        arrayList.add(downloadAudioDetails);
                        MainPlayModel mainPlayModel1 = new MainPlayModel();
                        mainPlayModel1.setID(downloadAudioDetails.getID());
                        mainPlayModel1.setName(downloadAudioDetails.getName());
                        mainPlayModel1.setAudioFile(downloadAudioDetails.getAudioFile());
                        mainPlayModel1.setAudioDirection(downloadAudioDetails.getAudioDirection());
                        mainPlayModel1.setAudiomastercat(downloadAudioDetails.getAudiomastercat());
                        mainPlayModel1.setAudioSubCategory(downloadAudioDetails.getAudioSubCategory());
                        mainPlayModel1.setImageFile(downloadAudioDetails.getImageFile());
                        mainPlayModel1.setLike(downloadAudioDetails.getLike());
                        mainPlayModel1.setDownload(downloadAudioDetails.getDownload());
                        mainPlayModel1.setAudioDuration(downloadAudioDetails.getAudioDuration());
                        arrayList2.add(mainPlayModel1);
                        SharedPreferences sharedd = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedd.edit();
                        Gson gson = new Gson();
                        String jsonx = gson.toJson(arrayList2);
                        String json1q1 = gson.toJson(arrayList);
                        editor.putString(CONSTANTS.PREF_KEY_modelList, json1q1);
                        editor.putString(CONSTANTS.PREF_KEY_audioList, jsonx);
                        editor.putInt(CONSTANTS.PREF_KEY_position, position);
                        editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                        editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                        editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                        editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
                        editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "DownloadListAudio");
                        editor.commit();

                        if (!arrayList2.get(position).getAudioFile().equals("")) {
                            List<String> downloadAudioDetailsList = new ArrayList<>();
                            GlobalInitExoPlayer ge = new GlobalInitExoPlayer();
                            downloadAudioDetailsList.add(downloadAudioDetails.getName());
                            ge.AddAudioToPlayer(size, arrayList2, downloadAudioDetailsList, ctx);
                        }
                        callAddTransFrag();
                    }
                    DatabaseClient.getInstance(getActivity().getApplicationContext())
                            .getaudioDatabase()
                            .taskDao()
                            .insertMedia(downloadAudioDetails);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    llDownload.setClickable(false);
                    llDownload.setEnabled(false);
                    super.onPostExecute(aVoid);
                }
            }
            SaveMedia st = new SaveMedia();
            st.execute();
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