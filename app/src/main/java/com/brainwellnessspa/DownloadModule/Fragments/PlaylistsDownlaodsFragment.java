package com.brainwellnessspa.DownloadModule.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DownloadModule.Activities.DownloadPlaylistActivity;
import com.brainwellnessspa.EncryptDecryptUtils.FileUtils;
import com.brainwellnessspa.R;
import com.brainwellnessspa.roomDataBase.AudioDatabase;

import com.brainwellnessspa.roomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.roomDataBase.DownloadPlaylistDetails;
import com.brainwellnessspa.utility.CONSTANTS;
import com.brainwellnessspa.utility.MeasureRatio;
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


import static com.brainwellnessspa.BWSApplication.DB;
import static com.brainwellnessspa.BWSApplication.getAudioDataBase;
import static com.brainwellnessspa.DownloadModule.Activities.DownloadPlaylistActivity.comeDeletePlaylist;
import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.downloadIdOne;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.downloadProgress;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.filename;

public class PlaylistsDownlaodsFragment extends Fragment {
    FragmentDownloadsBinding binding;
    List<DownloadPlaylistDetails> playlistList;
    String UserID,CoUserID;
    PlaylistsDownloadsAdapter adapter;
    //    Runnable UpdateSongTime1;
//    Handler handler1;
    boolean isMyDownloading = false;
    Properties p;
    List<String> fileNameList = new ArrayList<>(), playlistDownloadId = new ArrayList<>();
    private BroadcastReceiver listener1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getDownloadData();
            if (intent.hasExtra("Progress")) {
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_downloads, container, false);
        View view = binding.getRoot();

        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, AppCompatActivity.MODE_PRIVATE);
        UserID = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "");
        CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "");

        DB = getAudioDataBase(getActivity());
        playlistList = new ArrayList<>();
        binding.tvFound.setText("Your downloaded playlists will appear here");
        GetAllMedia(getActivity());
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
        super.onResume();
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(listener1);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        if (isMyDownloading) {
//            handler1.removeCallbacks(UpdateSongTime1);
        }
        super.onPause();
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
                if (fileNameList.size() != 0) {
//                    handler1.postDelayed(UpdateSongTime1, 30000);
                } else {
//                    handler1.removeCallbacks(UpdateSongTime1);
                    fileNameList = new ArrayList<>();
                    playlistDownloadId = new ArrayList<>();
                    isMyDownloading = false;
                }
            } else {
//                handler1.removeCallbacks(UpdateSongTime1);
                fileNameList = new ArrayList<>();
                playlistDownloadId = new ArrayList<>();
                isMyDownloading = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GetAllMedia(FragmentActivity activity) {
        DB.taskDao()
                .getAllPlaylist1(CoUserID).observe(getActivity(), audioList -> {

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
            LocalBroadcastManager.getInstance(getActivity())
                    .registerReceiver(listener1, new IntentFilter("DownloadProgress"));
        }
    }

    public class PlaylistsDownloadsAdapter extends RecyclerView.Adapter<PlaylistsDownloadsAdapter.MyViewHolder> {
        FragmentActivity ctx;
        List<DownloadAudioDetails> oneAudioDetailsList;
        LinearLayout llError;
        TextView tvFound;
        RecyclerView rvDownloadsList;
        private List<DownloadPlaylistDetails> listModelList;

        public PlaylistsDownloadsAdapter(List<DownloadPlaylistDetails> listModelList, FragmentActivity ctx, LinearLayout llError, TextView tvFound, RecyclerView rvDownloadsList) {
            this.listModelList = listModelList;
            this.ctx = ctx;
            this.llError = llError;
            this.tvFound = tvFound;
            this.rvDownloadsList = rvDownloadsList;
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
 /*           UpdateSongTime1 = new Runnable() {
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
            };*/
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
                holder.binding.pbProgress.setVisibility(View.GONE);
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
            holder.binding.ivBackgroundImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivBackgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(listModelList.get(position).getPlaylistImage()).thumbnail(0.05f)
                    .placeholder(R.drawable.ic_music_icon).error(R.drawable.ic_music_icon)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            Glide.with(ctx).load(R.drawable.ic_image_bg).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivBackgroundImage);
//            if (IsLock.equalsIgnoreCase("1")) {
//                holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                holder.binding.ivLock.setVisibility(View.VISIBLE);
//            } else if (IsLock.equalsIgnoreCase("2")) {
//                holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                holder.binding.ivLock.setVisibility(View.VISIBLE);
//            } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
//                holder.binding.ivBackgroundImage.setVisibility(View.GONE);
//                holder.binding.ivLock.setVisibility(View.GONE);
//            }

            holder.binding.llMainLayout.setOnClickListener(view -> {
                try {
//                    if (IsLock.equalsIgnoreCase("1")) {
//                        holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                        holder.binding.ivLock.setVisibility(View.VISIBLE);
//                        Intent i = new Intent(ctx, MembershipChangeActivity.class);
//                        i.putExtra("ComeFrom", "Plan");
//                        ctx.startActivity(i);
//                    } else if (IsLock.equalsIgnoreCase("2")) {
//                        holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                        holder.binding.ivLock.setVisibility(View.VISIBLE);
//                        BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
//                    } else if (IsLock.equalsIgnoreCase("0")
//                            || IsLock.equalsIgnoreCase("")) {
                        DB.taskDao()
                                .getCountDownloadProgress1("Complete", listModelList.get(position).getPlaylistID(),CoUserID).removeObserver(audioList -> {
                        });
                        comefromDownload = "1";
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
                        Properties p = new Properties();
                        p.putValue("userId", UserID);
                        p.putValue("playlistId", listModelList.get(position).getPlaylistID());
                        p.putValue("playlistName", listModelList.get(position).getPlaylistName());
                        p.putValue("playlistType", "");
                        BWSApplication.addToSegment("Downloaded Playlist Clicked", p, CONSTANTS.track);
//                    }
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
                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                String AudioPlayerFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
                String pID = shared.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
                String MyPlaylistName = shared.getString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "");
                if (AudioPlayerFlag.equalsIgnoreCase("Downloadlist") && pID.equalsIgnoreCase(listModelList.get(position).getPlaylistID())) {
                    BWSApplication.showToast("Unable to remove as this playlist is in player right now", ctx);
                } else {
                    final Dialog dialog = new Dialog(ctx);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.custom_popup_layout);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ctx.getResources().getColor(R.color.dark_blue_gray)));
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                    final TextView tvGoBack = dialog.findViewById(R.id.tvGoBack);
                    final TextView tvHeader = dialog.findViewById(R.id.tvHeader);
                    final TextView tvTitle = dialog.findViewById(R.id.tvTitle);
                    final Button Btn = dialog.findViewById(R.id.Btn);
                    tvTitle.setText("Remove playlist");
                    tvHeader.setText("Playlist has been removed");
                    tvHeader.setText("Are you sure you want to remove the " + listModelList.get(position).getPlaylistName() + " from downloads??");
                    Btn.setText("Confirm");
                    dialog.setOnKeyListener((v, keyCode, event) -> {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            dialog.dismiss();
                        }
                        return false;
                    });

                    Btn.setOnClickListener(v -> {
                        if (isMyDownloading) {
//                            handler1.removeCallbacks(UpdateSongTime1);
                        }
                        DB.taskDao().getAllPlaylist1(CoUserID).removeObserver(audioList -> {});
                        getDownloadDataForDelete(listModelList.get(position).getPlaylistID());
                        GetPlaylistMedia(listModelList.get(position).getPlaylistID());
                        Properties p = new Properties();
                        p.putValue("coUserId", CoUserID);
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
                        BWSApplication.showToast("Playlist has been removed", getActivity());

                        notifyItemRemoved(position);
                        dialog.dismiss();
                    });


                    tvGoBack.setOnClickListener(v -> dialog.dismiss());
                    dialog.show();
                    dialog.setCancelable(false);
                }
            });
        }

        private void getDownloadDataForDelete(String playlistID) {
            List<String> fileNameList = new ArrayList<>(), fileNameList1= new ArrayList<>(), audioFile= new ArrayList<>(), playlistDownloadId= new ArrayList<>();
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
                            for (int i = 1; i < fileNameList1.size() - 1; i++) {
                                if (playlistDownloadId.get(i).equalsIgnoreCase(playlistID)) {
                                    fileNameList.remove(i);
                                    audioFile.remove(i);
                                    playlistDownloadId.remove(i);
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
                                downloadProgress = 0;
                            }
                        }
                    }
                }
            } catch (Exception e) {
//                getDownloadDataForDelete(playlistID);
                e.printStackTrace();
                Log.e("Download Playlist ","Download Playlist remove issue:- "+e.getMessage());
            }


        }

        private void getMediaByPer(String playlistID, String totalAudio, ProgressBar pbProgress) {
            DB.taskDao().getCountDownloadProgress1("Complete", playlistID,CoUserID).observe(this.ctx, audioList -> {
                if (audioList != null) {
                    if (audioList.size() < Integer.parseInt(totalAudio)) {
                        long progressPercent = audioList.size() * 100 / Integer.parseInt(totalAudio);
                        int downloadProgress1 = (int) progressPercent;
                        pbProgress.setVisibility(View.VISIBLE);
                        pbProgress.setProgress(downloadProgress1);
                        isMyDownloading = true;
//                    getMediaByPer(playlistID,totalAudio,pbProgress);
//                        handler1.postDelayed(UpdateSongTime1, 30000);
                    } else {
                        getDownloadData();
                        pbProgress.setVisibility(View.GONE);
//                        handler1.removeCallbacks(UpdateSongTime1);
                        isMyDownloading = false;
                        notifyDataSetChanged();
                        DB.taskDao()
                                .getCountDownloadProgress1("Complete", playlistID,CoUserID).removeObserver(audioListx -> {
                        });
                    }
                }
            });
        }

  /*  void getDownloadData() {
        SharedPreferences sharedx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
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


        public void GetSingleMedia(String AudioFile, Context ctx,String playlistID,List<DownloadAudioDetails> audioList,int i) {
            DB.taskDao().getLastIdByuId1(AudioFile,CoUserID).observe(getActivity(), audioList1 -> {
                try {
                    if (audioList1.size() != 0) {
                        if (audioList1.size() == 1) {
                            FileUtils.deleteDownloadedFile(ctx, audioList1.get(0).getName());
                        }
                    }

                    if (i < audioList.size() - 1) {
                        GetSingleMedia(audioList.get(i + 1).getAudioFile(), ctx.getApplicationContext(), playlistID, audioList, i + 1);
                        Log.e("DownloadMedia Call", String.valueOf(i + 1));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        }

        private void deleteDownloadFile(String PlaylistId) {
            AudioDatabase.databaseWriteExecutor.execute(() -> DB.taskDao().deleteByPlaylistId(PlaylistId,CoUserID));
            deletePlaylist(PlaylistId);
        }

        private void deletePlaylist(String playlistId) {
            AudioDatabase.databaseWriteExecutor.execute(() -> DB.taskDao().deletePlaylist(playlistId,CoUserID));
            GetAllMedia(ctx);
        }

        public void GetPlaylistMedia(String playlistID) {
            DB.taskDao().getAllAudioByPlaylist1(playlistID,CoUserID).observe(this.ctx, audioList -> {
                deleteDownloadFile(playlistID);
                if (audioList.size() != 0) {
                    GetSingleMedia(audioList.get(0).getAudioFile(), ctx.getApplicationContext(), playlistID,audioList,0);
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