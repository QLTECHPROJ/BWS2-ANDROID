package com.brainwellnessspa.DownloadModule.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Activities.MembershipChangeActivity;
import com.brainwellnessspa.DownloadModule.Activities.DownloadPlaylistActivity;
import com.brainwellnessspa.EncryptDecryptUtils.FileUtils;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.AudioDatabase;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.RoomDataBase.DownloadPlaylistDetails;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.AudioDownloadsLayoutBinding;
import com.brainwellnessspa.databinding.FragmentDownloadsBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.downloader.PRDownloader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.segment.analytics.Properties;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.brainwellnessspa.BWSApplication.MIGRATION_1_2;
import static com.brainwellnessspa.DownloadModule.Activities.DownloadPlaylistActivity.comeDeletePlaylist;
import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.downloadIdOne;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.filename;

public class PlaylistsDownlaodsFragment extends Fragment {
    FragmentDownloadsBinding binding;
    List<DownloadPlaylistDetails> playlistList;
    String UserID, AudioFlag, IsLock;
    PlaylistsDownloadsAdapter adapter;
    Runnable UpdateSongTime1;
    Handler handler1;
    boolean isMyDownloading = false;
    AudioDatabase DB;
    Properties p;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_downloads, container, false);
        View view = binding.getRoot();
        if (getArguments() != null) {
            UserID = getArguments().getString("UserID");
            IsLock = getArguments().getString("IsLock");
        }
        DB = Room.databaseBuilder(getActivity(),
                AudioDatabase.class,
                "Audio_database")
                .addMigrations(MIGRATION_1_2)
                .build();
        playlistList = new ArrayList<>();
        p = new Properties();
        p.putValue("userId", UserID);
        BWSApplication.addToSegment("Downloaded Playlist Viewed", p, CONSTANTS.screen);
        binding.tvFound.setText("Your downloaded playlists will appear here");
        GetAllMedia(getActivity());
        RefreshData();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvDownloadsList.setLayoutManager(mLayoutManager);
        binding.rvDownloadsList.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    @Override
    public void onResume() {
        if (comeDeletePlaylist == 1) {
            GetAllMedia(getActivity());
            comeDeletePlaylist = 0;
        }
        GetAllMedia(getActivity());
        RefreshData();
        super.onResume();
    }

    @Override
    public void onPause() {
        if (isMyDownloading) {
            handler1.removeCallbacks(UpdateSongTime1);
        }
        super.onPause();
    }

    public void RefreshData() {
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        if (!AudioFlag.equalsIgnoreCase("0")) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(13, 9, 13, 210);
            binding.llSpace.setLayoutParams(params);
        } else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(13, 9, 13, 28);
            binding.llSpace.setLayoutParams(params);
        }
    }

    private void GetAllMedia(FragmentActivity activity) {
        DatabaseClient
                .getInstance(getActivity())
                .getaudioDatabase()
                .taskDao()
                .getAllPlaylist1().observe(getActivity(), audioList -> {

            if (audioList != null) {
                if (audioList.size() != 0) {
                    List<DownloadPlaylistDetails> audioList1 = new ArrayList<>();
                    for (int i = 0; i < audioList.size(); i++) {
                        DownloadPlaylistDetails detail = new DownloadPlaylistDetails();
                        detail.setPlaylistID(audioList.get(i).getPlaylistID());
                        detail.setPlaylistName(audioList.get(i).getPlaylistName());
                        detail.setPlaylistDesc(audioList.get(i).getPlaylistDesc());
                        detail.setIsReminder(audioList.get(i).getPlaylistDesc());
                        detail.setPlaylistMastercat(audioList.get(i).getPlaylistMastercat());
                        detail.setPlaylistSubcat(audioList.get(i).getPlaylistSubcat());
                        detail.setPlaylistImage(audioList.get(i).getPlaylistImage());
                        detail.setPlaylistImageDetails(audioList.get(i).getPlaylistImageDetails());
                        detail.setTotalAudio(audioList.get(i).getTotalAudio());
                        detail.setTotalDuration(audioList.get(i).getTotalDuration());
                        detail.setTotalhour(audioList.get(i).getTotalhour());
                        detail.setTotalminute(audioList.get(i).getTotalminute());
                        detail.setCreated(audioList.get(i).getCreated());
                        detail.setDownload(audioList.get(i).getDownload());
                        detail.setLike(audioList.get(i).getLike());
                        audioList1.add(detail);
                    }
                    getDataList(audioList1);
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
            /*DatabaseClient
                    .getInstance(getActivity())
                    .getaudioDatabase()
                    .taskDao()
                    .getAllPlaylist1().removeObserver(audioList1 -> {
            });*/
        });
    }

    private void getDataList(List<DownloadPlaylistDetails> historyList) {
        if (historyList.size() == 0) {
            binding.tvFound.setVisibility(View.VISIBLE);
            binding.llError.setVisibility(View.VISIBLE);
        } else {
            binding.llError.setVisibility(View.GONE);
            adapter = new PlaylistsDownloadsAdapter(historyList, getActivity(), binding.llError, binding.tvFound, binding.rvDownloadsList);
            binding.rvDownloadsList.setAdapter(adapter);
        }
    }

    public class PlaylistsDownloadsAdapter extends RecyclerView.Adapter<PlaylistsDownloadsAdapter.MyViewHolder> {
        FragmentActivity ctx;
        List<DownloadAudioDetails> playlistWiseAudioDetails;
        List<DownloadAudioDetails> oneAudioDetailsList;
        List<DownloadPlaylistDetails> playlistList;
        LinearLayout llError;
        TextView tvFound;
        RecyclerView rvDownloadsList;
        List<String> fileNameList = new ArrayList<>(), playlistDownloadId = new ArrayList<>(), remainAudio = new ArrayList<>();
        private List<DownloadPlaylistDetails> listModelList;

        public PlaylistsDownloadsAdapter(List<DownloadPlaylistDetails> listModelList, FragmentActivity ctx, LinearLayout llError, TextView tvFound, RecyclerView rvDownloadsList) {
            this.listModelList = listModelList;
            this.ctx = ctx;
            this.llError = llError;
            this.tvFound = tvFound;
            this.rvDownloadsList = rvDownloadsList;
            handler1 = new Handler();
            oneAudioDetailsList = new ArrayList<>();
            getDownloadData();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            AudioDownloadsLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.audio_downloads_layout, parent, false);
            return new MyViewHolder(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvTitle.setText(listModelList.get(position).getPlaylistName());
            holder.binding.equalizerview.setVisibility(View.GONE);
            UpdateSongTime1 = new Runnable() {
                @Override
                public void run() {
                    try {
                        getDownloadData();
                        if (fileNameList.size() != 0) {
                            for (int f = 0; f < fileNameList.size(); f++) {
                                if (playlistDownloadId.get(f).equalsIgnoreCase(listModelList.get(position).getPlaylistID())) {
                                    getMediaByPer(listModelList.get(position).getPlaylistID(), listModelList.get(position).getTotalAudio(), holder.binding.pbProgress);
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                    }

                }
            };
        /*if(fileNameList.size()!=0){
            if(playlistDownloadId.contains(listModelList.get(position).getPlaylistID())){
                holder.binding.pbProgress.setVisibility(View.VISIBLE);
                handler1.postDelayed(UpdateSongTime1,500);
            }else{
                holder.binding.pbProgress.setVisibility(View.GONE);
            }
        }*/
            if (fileNameList.size() != 0) {
                for (int f = 0; f < fileNameList.size(); f++) {
                    if (playlistDownloadId.get(f).equalsIgnoreCase(listModelList.get(position).getPlaylistID())) {
                        getMediaByPer(listModelList.get(position).getPlaylistID(), listModelList.get(position).getTotalAudio(), holder.binding.pbProgress);
                        break;
                    }
                }

            } else {
                isMyDownloading = false;
            }

            try {
                if (listModelList.get(position).getTotalAudio().equalsIgnoreCase("") ||
                        listModelList.get(position).getTotalAudio().equalsIgnoreCase("0") &&
                                listModelList.get(position).getTotalhour().equalsIgnoreCase("")
                                && listModelList.get(position).getTotalminute().equalsIgnoreCase("")) {
                    holder.binding.tvTime.setText("0 Audio | 0h 0m");
                } else {
                    if (listModelList.get(position).getTotalminute().equalsIgnoreCase("")) {
                        holder.binding.tvTime.setText(listModelList.get(position).getTotalAudio() + " Audio | "
                                + listModelList.get(position).getTotalhour() + "h 0m");
                    } else {
                        holder.binding.tvTime.setText(listModelList.get(position).getTotalAudio() +
                                " Audios | " + listModelList.get(position).getTotalhour() + "h " + listModelList.get(position).getTotalminute() + "m");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.cvImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.cvImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            Glide.with(ctx).load(listModelList.get(position).getPlaylistImage()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg);
            if (IsLock.equalsIgnoreCase("1")) {
                holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                holder.binding.ivLock.setVisibility(View.VISIBLE);
            } else if (IsLock.equalsIgnoreCase("2")) {
                holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                holder.binding.ivLock.setVisibility(View.VISIBLE);
            } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                holder.binding.ivLock.setVisibility(View.GONE);
            }

            holder.binding.llMainLayout.setOnClickListener(view -> {
                try {
                    if (IsLock.equalsIgnoreCase("1")) {
                        holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                        holder.binding.ivLock.setVisibility(View.VISIBLE);
                        Intent i = new Intent(ctx, MembershipChangeActivity.class);
                        i.putExtra("ComeFrom", "Plan");
                        ctx.startActivity(i);
                    } else if (IsLock.equalsIgnoreCase("2")) {
                        holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                        holder.binding.ivLock.setVisibility(View.VISIBLE);
                        BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
                    } else if (IsLock.equalsIgnoreCase("0")
                            || IsLock.equalsIgnoreCase("")) {
                        handler1.removeCallbacks(UpdateSongTime1);
                        DatabaseClient
                                .getInstance(ctx)
                                .getaudioDatabase()
                                .taskDao()
                                .getCountDownloadProgress1("Complete", listModelList.get(position).getPlaylistID()).removeObserver(audioList -> {
                        });
                        comefromDownload = "1";
//                playlistWiseAudioDetails = GetMedia(listModelList.get(position).getPlaylistID());
                        holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                        holder.binding.ivLock.setVisibility(View.GONE);
                        Intent i = new Intent(ctx, DownloadPlaylistActivity.class);
                        i.putExtra("New", "0");
                        i.putExtra("PlaylistID", listModelList.get(position).getPlaylistID());
                        i.putExtra("PlaylistName", listModelList.get(position).getPlaylistName());
                        i.putExtra("PlaylistImage", listModelList.get(position).getPlaylistImage());
                        i.putExtra("PlaylistImageDetails", listModelList.get(position).getPlaylistImageDetails());
                        i.putExtra("TotalAudio", listModelList.get(position).getTotalAudio());
                        i.putExtra("Totalhour", listModelList.get(position).getTotalhour());
                        i.putExtra("Totalminute", listModelList.get(position).getTotalminute());
                        i.putExtra("PlaylistDescription", listModelList.get(position).getPlaylistDesc());
                        i.putExtra("Created", listModelList.get(position).getCreated());
                        i.putExtra("MyDownloads", "1");
                        ctx.startActivity(i);
//                        BWSApplication.showToast("Opened", ctx);
                        /*Properties p = new Properties();
                    p.putValue("userId", UserID);
                    p.putValue("playlistId", listModelList.get(position).getPlaylistID());
                    p.putValue("playlistName", listModelList.get(position).getPlaylistName());
                    p.putValue("playlistType", "");
                    BWSApplication.addToSegment("Downloaded Playlist Clicked", p, CONSTANTS.track);*/
        /*        Intent i = new Intent(ctx, DownloadedPlaylist.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                i.putExtra("PlaylistID", listModelList.get(position).getPlaylistID());
                i.putExtra("PlaylistName", listModelList.get(position).getPlaylistName());
                i.putExtra("PlaylistImage", listModelList.get(position).getPlaylistImage());
                i.putExtra("PlaylistImage", listModelList.get(position).getPlaylistImage());
                ctx.startActivity(i);
                ctx.finish();*/
                    }
                } catch (java.lang.IllegalStateException exception) {
                    // Attempt to catch rare mysterious Canvas stack underflow events that have been reported in
                    // ACRA, but simply should not be happening because Canvas save()/restore() calls are definitely
                    // balanced. The exception is: java.lang.IllegalStateException: Underflow in restore
                    // See: stackoverflow.com/questions/23893813/
                    if (exception.getMessage() != null && (//
                            exception.getMessage().contains("Underflow in restore") || //
                                    exception.getCause().getMessage().contains("Underflow in restore"))) { //
                        Log.e("downloadPlaylist NotOpn", "Caught a Canvas stack underflow! (java.lang.IllegalStateException: Underflow in restore)");
                    } else {
                        // It wasn't a Canvas underflow, so re-throw.
                        throw exception;
                    }
                }
            });

            holder.binding.llRemoveAudio.setOnClickListener(view -> {
                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                if (audioPlay && AudioFlag.equalsIgnoreCase("Downloadlist") && pID.equalsIgnoreCase(listModelList.get(position).getPlaylistName())) {
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
                    tvHeader.setText("Are you sure you want to remove the " + listModelList.get(position).getPlaylistName() + " from downloads??");
                    Btn.setText("Confirm");
                    dialog.setOnKeyListener((v, keyCode, event) -> {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            dialog.dismiss();
                        }
                        return false;
                    });

                    Btn.setOnClickListener(v -> {
//                        try {
                        if (isMyDownloading) {
                            handler1.removeCallbacks(UpdateSongTime1);
                        }
                        DB.taskDao()
                                .getAllPlaylist1().removeObserver(audioList -> {
                        });
                        getDownloadData(listModelList.get(position).getPlaylistID());
                        GetPlaylistMedia(listModelList.get(position).getPlaylistID());
                        Properties p = new Properties();
                        p.putValue("userId", UserID);
                        p.putValue("playlistId", listModelList.get(position).getPlaylistID());
                        p.putValue("playlistName", listModelList.get(position).getPlaylistName());
                        if (listModelList.get(position).getCreated().equalsIgnoreCase("1")) {
                            p.putValue("playlistType", "Created");
                        } else if (listModelList.get(position).getCreated().equalsIgnoreCase("0")) {
                            p.putValue("playlistType", "Default");
                        }
                        p.putValue("audioCount", listModelList.get(position).getTotalAudio());
                        p.putValue("playlistDescription", listModelList.get(position).getPlaylistDesc());
                        if (listModelList.get(position).getTotalhour().equalsIgnoreCase("")) {
                            p.putValue("playlistDuration", "0h " + listModelList.get(position).getTotalminute() + "m");
                        } else if (listModelList.get(position).getTotalminute().equalsIgnoreCase("")) {
                            p.putValue("playlistDuration", listModelList.get(position).getTotalhour() + "h 0m");
                        } else {
                            p.putValue("playlistDuration", listModelList.get(position).getTotalhour() + "h " + listModelList.get(position).getTotalminute() + "m");
                        }
                        p.putValue("source", "Downloaded Playlists");
                        BWSApplication.addToSegment("Downloaded Playlist Removed", p, CONSTANTS.track);
//                        } catch (Exception e) {
//                        }
                        notifyItemRemoved(position);
                        dialog.dismiss();
                    });

                    tvGoBack.setOnClickListener(v -> dialog.dismiss());
                    dialog.show();
                    dialog.setCancelable(false);
                }
            });
        }

        private void getDownloadData(String playlistID) {
            List<String> fileNameList, fileNameList1, audioFile, playlistDownloadId;
            try {
                SharedPreferences sharedy = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
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
                        if (playlistDownloadId.contains(playlistID)) {
                            Log.e("cancel", String.valueOf(playlistDownloadId.size()));
                            for (int i = 1; i < fileNameList1.size(); i++) {
                                if (playlistDownloadId.get(i).equalsIgnoreCase(playlistID)) {
                                    Log.e("cancel name id", "My id " + i + fileNameList1.get(i));
                                    fileNameList.remove(i);
                                    audioFile.remove(i);
                                    playlistDownloadId.remove(i);
                                    Log.e("cancel id", "My id " + playlistDownloadId.size() + i);
                                }
                            }
                        }
                    }
                    SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    String nameJson = gson.toJson(fileNameList);
                    String urlJson = gson.toJson(audioFile);
                    String playlistIdJson = gson.toJson(playlistDownloadId);
                    editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson);
                    editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson);
                    editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson);
                    editor.apply();
                    editor.commit();
                    if (playlistDownloadId.get(0).equalsIgnoreCase(playlistID)) {
                        PRDownloader.cancel(downloadIdOne);
                        filename = "";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void getDownloadData() {
            try {
                SharedPreferences sharedy = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String jsony = sharedy.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson));
                String jsonx = sharedy.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson));
                String jsonq = sharedy.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson));
                if (!jsony.equalsIgnoreCase(String.valueOf(gson))) {
                    Type type = new TypeToken<List<String>>() {
                    }.getType();
                    fileNameList = gson.fromJson(jsony, type);
                    playlistDownloadId = gson.fromJson(jsonq, type);
                    if (fileNameList.size() != 0) {
                        handler1.postDelayed(UpdateSongTime1, 30000);
                    } else {
                        handler1.removeCallbacks(UpdateSongTime1);
                        fileNameList = new ArrayList<>();
                        playlistDownloadId = new ArrayList<>();
                        isMyDownloading = false;
                    }
                } else {
                    handler1.removeCallbacks(UpdateSongTime1);
                    fileNameList = new ArrayList<>();
                    playlistDownloadId = new ArrayList<>();
                    isMyDownloading = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void getMediaByPer(String playlistID, String totalAudio, ProgressBar pbProgress) {
            DatabaseClient
                    .getInstance(ctx)
                    .getaudioDatabase()
                    .taskDao()
                    .getCountDownloadProgress1("Complete", playlistID).observe(this.ctx, audioList -> {

                if (audioList != null) {
                    if (audioList.size() < Integer.parseInt(totalAudio)) {
                        long progressPercent = audioList.size() * 100 / Integer.parseInt(totalAudio);
                        int downloadProgress1 = (int) progressPercent;
                        pbProgress.setVisibility(View.VISIBLE);
                        pbProgress.setProgress(downloadProgress1);
                        isMyDownloading = true;
//                    getMediaByPer(playlistID,totalAudio,pbProgress);
                        handler1.postDelayed(UpdateSongTime1, 30000);
                    } else {
                        getDownloadData();
                        pbProgress.setVisibility(View.GONE);
                        handler1.removeCallbacks(UpdateSongTime1);
                        isMyDownloading = false;
                        notifyDataSetChanged();
                        DatabaseClient
                                .getInstance(ctx)
                                .getaudioDatabase()
                                .taskDao()
                                .getCountDownloadProgress1("Complete", playlistID).removeObserver(audioListx -> {
                        });
                    }
                }
            });
        }

  /*  void getDownloadData() {
        SharedPreferences sharedx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson));
        if (!json.equalsIgnoreCase(String.valueOf(gson))) {
            Type type = new TypeToken<List<String>>() {
            }.getType();
            fileNameList = gson.fromJson(json, type);
            for (int i = 0; i < fileNameList.size(); i++) {
                if (playlistDownloadId.get(i).equalsIgnoreCase(listModelList.get(i).getPlaylistID())) {
                    remainAudio.add(playlistDownloadId.get(i));
                }
            }
        } else {
            fileNameList = new ArrayList<>();
            playlistDownloadId = new ArrayList<>();
            remainAudio = new ArrayList<>();
        }
    }*/

        public void GetSingleMedia(String AudioFile, Context ctx, String playlistID) {
      /*  class GetMedia extends AsyncTask<Void, Void, Void> {
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
            DatabaseClient
                    .getInstance(ctx)
                    .getaudioDatabase()
                    .taskDao()
                    .getLastIdByuId1(AudioFile).observe(this.ctx, audioList -> {
                if (audioList.size() != 0) {
                    if (audioList.size() == 1) {
                        FileUtils.deleteDownloadedFile(ctx, audioList.get(0).getName());
                    }
                }
            });
        }

        private void deleteDownloadFile(Context applicationContext, String PlaylistId) {

            AudioDatabase.databaseWriteExecutor.execute(() -> DB.taskDao().deleteByPlaylistId(PlaylistId));

            deletePlaylist(PlaylistId);
          /*  class DeleteMedia extends AsyncTask<Void, Void, Void> {
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
                    super.onPostExecute(aVoid);
                }
            }
            DeleteMedia st = new DeleteMedia();
            st.execute();*/
        }

        private void deletePlaylist(String playlistId) {

            AudioDatabase.databaseWriteExecutor.execute(() -> DB.taskDao().deletePlaylist(playlistId));
            GetAllMedia(ctx);
         /*   class DeleteMedia extends AsyncTask<Void, Void, Void> {
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
                    playlistList = new ArrayList<>();
                    super.onPostExecute(aVoid);
                }
            }
            DeleteMedia st = new DeleteMedia();
            st.execute();*/
        }

   /*     private void GetAllMedia(FragmentActivity activity) {
            DatabaseClient
                    .getInstance(ctx)
                    .getaudioDatabase()
                    .taskDao()
                    .getAllPlaylist1().observe(this.ctx, audioList -> {

                if (audioList.size() != 0) {
                    llError.setVisibility(View.GONE);
                    tvFound.setVisibility(View.GONE);
                    adapter = new PlaylistsDownloadsAdapter(audioList, ctx, UserID, progressBarHolder, ImgV, llError, tvFound, rvDownloadsList);
                    rvDownloadsList.setAdapter(adapter);
                } else {
                    llError.setVisibility(View.VISIBLE);
                    tvFound.setVisibility(View.VISIBLE);
                    rvDownloadsList.setVisibility(View.GONE);
                }
            });
        }*/

        public void GetPlaylistMedia(String playlistID) {
            DB.taskDao().getAllAudioByPlaylist1(playlistID).observe(this.ctx, audioList -> {
                deleteDownloadFile(ctx.getApplicationContext(), playlistID);
                try {
                    for (int i = 0; i < audioList.size(); i++) {
                        GetSingleMedia(audioList.get(i).getAudioFile(), ctx.getApplicationContext(), playlistID);
                    }
                } catch (Exception e) {
                }
            });
     /*   playlistWiseAudioDetails = new ArrayList<>();
        class GetMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                playlistWiseAudioDetails = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .getAllAudioByPlaylist(playlistID);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                deleteDownloadFile(ctx.getApplicationContext(), playlistID);
                try {
                    for (int i = 0; i < playlistWiseAudioDetails.size(); i++) {
                        GetSingleMedia(playlistWiseAudioDetails.get(i).getAudioFile(), ctx.getApplicationContext(), playlistID);
                    }
                }catch (Exception e){}
                super.onPostExecute(aVoid);
            }
        }
        GetMedia st = new GetMedia();
        st.execute();
        return playlistWiseAudioDetails;*/
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