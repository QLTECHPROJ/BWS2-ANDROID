package com.brainwellnessspa.DownloadModule.Fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Activities.MembershipChangeActivity;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.EncryptDecryptUtils.FileUtils;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.AudioDownloadsLayoutBinding;
import com.brainwellnessspa.databinding.FragmentDownloadsBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.downloader.PRDownloader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.audioClick;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.miniPlayer;
import static com.brainwellnessspa.DashboardModule.Audio.AudioFragment.IsLock;
import static com.brainwellnessspa.DashboardModule.Playlist.MyPlaylistsFragment.disclaimerPlayed;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.myAudioId;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.downloadIdOne;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.downloadProgress;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.filename;
import static com.brainwellnessspa.Services.GlobleInItExoPlayer.player;

public class AudioDownloadsFragment extends Fragment {
    public static String comefromDownload = "0";
    FragmentDownloadsBinding binding;
    List<DownloadAudioDetails> audioList;
    String UserID, AudioFlag;
    AudioDownlaodsAdapter adapter;
    boolean isThreadStart = false;
    Runnable UpdateSongTime1;
    View view;
    private Handler handler1;
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
                if (audioPlayz && AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_downloads, container, false);
        view = binding.getRoot();
        if (getArguments() != null) {
            UserID = getArguments().getString("UserID");
        }
        handler1 = new Handler();
        audioList = new ArrayList<>();
//        audioList = GetAllMedia(getActivity());
        callObserverMethod();
        binding.tvFound.setText("Your downloaded audios will appear here");
        RefreshData();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvDownloadsList.setLayoutManager(mLayoutManager);
        binding.rvDownloadsList.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    private void callObserverMethod() {
        DatabaseClient
                .getInstance(getActivity())
                .getaudioDatabase()
                .taskDao()
                .geAllData1("").observe(getActivity(), audioList -> {
            if (audioList != null) {
                if (audioList.size() != 0) {
                    getDataList(audioList, UserID, binding.progressBarHolder, binding.progressBar, binding.llError, binding.rvDownloadsList);
                    binding.llError.setVisibility(View.GONE);
                    binding.rvDownloadsList.setVisibility(View.VISIBLE);
                }
            } else {
                binding.llError.setVisibility(View.VISIBLE);
                binding.rvDownloadsList.setVisibility(View.GONE);
            }

        });

    }

    @Override
    public void onResume() {
        super.onResume();

        RefreshData();
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(listener, new IntentFilter("play_pause_Action"));
//        audioList = GetAllMedia(getActivity());
    }


    @Override
    public void onPause() {
        if (isThreadStart) {
            handler1.removeCallbacks(UpdateSongTime1);
        }
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(listener);
        super.onPause();
    }

    public void RefreshData() {
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        if (!AudioFlag.equalsIgnoreCase("0")) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(13, 9, 13, 190);
            binding.llSpace.setLayoutParams(params);
        } else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(13, 9, 13, 28);
            binding.llSpace.setLayoutParams(params);
        }
    }

   /* public List<DownloadAudioDetails> GetAllMedia(Context ctx) {
        class GetTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                audioList = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .geAllData("");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (audioList != null) {
                    if (audioList.size() != 0) {
                        getDataList(audioList, UserID, binding.progressBarHolder, binding.progressBar, binding.llError, binding.rvDownloadsList);
                        binding.llError.setVisibility(View.GONE);
                        binding.rvDownloadsList.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.llError.setVisibility(View.VISIBLE);
                    binding.rvDownloadsList.setVisibility(View.GONE);
                }
                super.onPostExecute(aVoid);
            }
        }
        GetTask st = new GetTask();
        st.execute();
        return audioList;
    }*/

    private void getDataList(List<DownloadAudioDetails> historyList, String UserID, FrameLayout progressBarHolder, ProgressBar ImgV, LinearLayout llError, RecyclerView rvDownloadsList) {
        if (historyList.size() == 0) {
            binding.tvFound.setVisibility(View.VISIBLE);
            binding.llError.setVisibility(View.VISIBLE);
            binding.llSpace.setVisibility(View.GONE);
        } else {
            binding.llError.setVisibility(View.GONE);
            binding.llSpace.setVisibility(View.VISIBLE);
            adapter = new AudioDownlaodsAdapter(historyList, getActivity(), UserID, progressBarHolder, ImgV, llError, rvDownloadsList, binding.tvFound);
            LocalBroadcastManager.getInstance(getActivity())
                    .registerReceiver(listener, new IntentFilter("play_pause_Action"));
            binding.rvDownloadsList.setAdapter(adapter);
        }
    }

    public class AudioDownlaodsAdapter extends RecyclerView.Adapter<AudioDownlaodsAdapter.MyViewHolder> {
        FragmentActivity ctx;
        String UserID, songId, AudioFlag;
        FrameLayout progressBarHolder;
        ProgressBar ImgV;
        LinearLayout llError;
        RecyclerView rvDownloadsList;
        TextView tvFound;
        List<DownloadAudioDetails> downloadAudioDetailsList;

        List<String> fileNameList = new ArrayList<>(), playlistDownloadId = new ArrayList<>(), audiofilelist = new ArrayList<>();
        //    Handler handler3;
        int startTime;
        long myProgress = 0;
        private List<DownloadAudioDetails> listModelList;
        private long currentDuration = 0;
//    private Runnable UpdateSongTime3;


        public AudioDownlaodsAdapter(List<DownloadAudioDetails> listModelList, FragmentActivity ctx, String UserID,
                                     FrameLayout progressBarHolder, ProgressBar ImgV, LinearLayout llError, RecyclerView rvDownloadsList, TextView tvFound) {
            this.listModelList = listModelList;
            this.ctx = ctx;
            this.UserID = UserID;
            this.progressBarHolder = progressBarHolder;
            this.ImgV = ImgV;
            this.llError = llError;
            this.rvDownloadsList = rvDownloadsList;
            this.tvFound = tvFound;
        /*SharedPreferences sharedx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson));
        if (!json.equalsIgnoreCase(String.valueOf(gson))) {
            Type type = new TypeToken<List<String>>() {
            }.getType();
//            fileNameList = gson.fromJson(json, type);
        }*/
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
            UpdateSongTime1 = new Runnable() {
                @Override
                public void run() {
                    try {
                   /* downloadedSingleAudio = getMyMedia();
                    for (int f = 0; f < listModelList.size(); f++) {
                        if(downloadedSingleAudio.size()!=0) {
                            for (int i = 0; i < downloadedSingleAudio.size(); i++) {
                                if (downloadedSingleAudio.get(i).getName().equalsIgnoreCase(listModelList.get(position).getName())) {
                                        if (downloadedSingleAudio.get(i).getDownloadProgress() <= 100) {
                                            //disableName.add(mData.get(position).getName());
                                            notifyItemChanged(position);
                                        }
                                }
                            }
                        }
                    }
                    downloadedSingleAudio = getMyMedia();*/
//                        for (int f = 0; f < GlobalListModel.getPlaylistSongs().size(); f++) {
                        if (fileNameList.size() != 0) {
                            for (int i = 0; i < fileNameList.size(); i++) {
                                if (fileNameList.get(i).equalsIgnoreCase(listModelList.get(position).getName()) && playlistDownloadId.get(i).equalsIgnoreCase("")) {
                                    if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(listModelList.get(position).getName())) {
                                        if (downloadProgress <= 100) {
                                            if (BWSApplication.isNetworkConnected(ctx)) {
                                                notifyItemChanged(position);
                                            }
                                        } else {
                                            holder.binding.pbProgress.setVisibility(View.GONE);
                                            getDownloadData();
                                        }
                                    } else {
//                                        notifyItemChanged(i);
                                    }
                                }
                            }
//                            }
                        }
                        if (downloadProgress == 0) {
                            notifyDataSetChanged();
                            getDownloadData();
                        }
                        handler1.postDelayed(this, 3000);
                        isThreadStart = true;
                    } catch (Exception e) {
                    }
                }
            };

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
                            handler1.postDelayed(UpdateSongTime1, 3000);
                        } else {
                            holder.binding.pbProgress.setVisibility(View.VISIBLE);
                            handler1.postDelayed(UpdateSongTime1, 3000);
                        }
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
            Glide.with(ctx).load(listModelList.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg);
            comefromDownload = "1";
            if (IsLock.equalsIgnoreCase("1")) {
                holder.binding.ivLock.setVisibility(View.VISIBLE);
            } else if (IsLock.equalsIgnoreCase("2")) {
                holder.binding.ivLock.setVisibility(View.VISIBLE);
            } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                holder.binding.ivLock.setVisibility(View.GONE);
            }

            SharedPreferences sharedzw = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            boolean audioPlayz = sharedzw.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
            AudioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            String pIDz = sharedzw.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
            if (audioPlayz && AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
                if (myAudioId.equalsIgnoreCase(listModelList.get(position).getID())) {
                    songId = myAudioId;
                    if (player != null) {
                        if (!player.getPlayWhenReady()) {
                            holder.binding.equalizerview.stopBars();
                        } else {
                            holder.binding.equalizerview.animateBars();
                        }
                    } else
                        holder.binding.equalizerview.stopBars();
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

            holder.binding.llMainLayout.setOnClickListener(view -> {
                if (IsLock.equalsIgnoreCase("1")) {
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                    Intent i = new Intent(ctx, MembershipChangeActivity.class);
                    i.putExtra("ComeFrom", "Plan");
                    ctx.startActivity(i);
                } else if (IsLock.equalsIgnoreCase("2")) {
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                    BWSApplication.showToast("Please re-activate your membership plan", ctx);
                } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                    comefromDownload = "1";
                    holder.binding.ivLock.setVisibility(View.GONE);
                    SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                    boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                    String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                    if (audioPlay && AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
                        if (isDisclaimer == 1) {
                            BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                        } else {
                            callTransFrag(position, listModelList);
                        }
                    } else {
                        isDisclaimer = 0;
                        disclaimerPlayed = 0;
                        List<DownloadAudioDetails> listModelList2 = new ArrayList<>();
                        DownloadAudioDetails mainPlayModel = new DownloadAudioDetails();
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
                        listModelList2.addAll(listModelList);
                        listModelList2.add(position, mainPlayModel);
                        callTransFrag(position, listModelList2);
                    }
                }
//            handler3.postDelayed(UpdateSongTime3, 500);
                notifyDataSetChanged();
            });

            holder.binding.llRemoveAudio.setOnClickListener(view -> {
                try {
                    SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                    String AudioFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                    if (AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
                        String name = "";
                        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                        Gson gson = new Gson();
                        String json = shared.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gson));
                        Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                        }.getType();
                        ArrayList<MainPlayModel> arrayList = gson.fromJson(json, type);

                        if (arrayList.get(0).getAudioFile().equalsIgnoreCase("")) {
                            arrayList.remove(0);
                        }
                        name = arrayList.get(0).getName();

                        if (name.equalsIgnoreCase(listModelList.get(position).getName())) {
                            BWSApplication.showToast("Currently this audio is in player,so you can't delete this audio as of now", ctx);
                        } else {
                            deleteAudio(holder.getAdapterPosition());
                        }
                    } else {
                        deleteAudio(holder.getAdapterPosition());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        private void callTransFrag(int position, List<DownloadAudioDetails> listModelList) {
            try {
                miniPlayer = 1;
                audioClick = true;
                if (player != null) {
                    player.stop();
                    player.release();
                    player = null;
                }

                Fragment fragment = new MiniPlayerFragment();
                FragmentManager fragmentManager1 = ctx.getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .add(R.id.flContainer, fragment)
                        .commit();
                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                Gson gson = new Gson();
                String json = gson.toJson(listModelList);
                editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                editor.putInt(CONSTANTS.PREF_KEY_position, position);
                editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
                editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "DownloadListAudio");
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void deleteAudio(int position) {
            getDownloadData();
            final Dialog dialog = new Dialog(ctx);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.logout_layout);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ctx.getResources().getColor(R.color.dark_blue_gray)));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            final TextView tvGoBack = dialog.findViewById(R.id.tvGoBack);
            final TextView tvHeader = dialog.findViewById(R.id.tvHeader);
            final TextView tvTitle = dialog.findViewById(R.id.tvTitle);
            final Button Btn = dialog.findViewById(R.id.Btn);
            tvTitle.setText("Remove audio");
            tvHeader.setText("Are you sure you want to remove the " + listModelList.get(position).getName() + " from downloads?");
            Btn.setText("Confirm");
            dialog.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                }
                return false;
            });

            Btn.setOnClickListener(v -> {
                String AudioFile = listModelList.get(position).getAudioFile();
                String AudioName = listModelList.get(position).getName();
                if (fileNameList.size() != 0) {
                    for (int i = 0; i < fileNameList.size(); i++) {
                        if (fileNameList.get(i).equalsIgnoreCase(listModelList.get(position).getName()) && playlistDownloadId.get(i).equalsIgnoreCase("")) {
                            if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(listModelList.get(position).getName())) {
                                if (downloadProgress <= 100) {
                                    PRDownloader.cancel(downloadIdOne);
                                }
                            } else {
                                fileNameList.remove(i);
                                playlistDownloadId.remove(i);
                                audiofilelist.remove(i);
                                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE);
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
                deleteDownloadFile(ctx.getApplicationContext(), AudioFile, AudioName, position);
                dialog.dismiss();
            });
            tvGoBack.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
            dialog.setCancelable(false);
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
                    audiofilelist = gson.fromJson(jsonx, type);
                    if (fileNameList.size() != 0) {
                        handler1.postDelayed(UpdateSongTime1, 3000);
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

        private void deleteDownloadFile(Context applicationContext, String audioFile, String audioName, int position) {
            class DeleteMedia extends AsyncTask<Void, Void, Void> {
                @Override
                protected Void doInBackground(Void... voids) {
                    DatabaseClient.getInstance(applicationContext)
                            .getaudioDatabase()
                            .taskDao()
                            .deleteByAudioFile(audioFile, "");

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    DatabaseClient
                            .getInstance(ctx)
                            .getaudioDatabase()
                            .taskDao()
                            .getLastIdByuId1(audioFile).observe(getActivity(), audioList -> {
                        if (audioList.size() == 0) {
                            FileUtils.deleteDownloadedFile(applicationContext, audioName);
                        }
                    });
                    listModelList = new ArrayList<>();
//                    listModelList = GetAllMedia(ctx);
                    CallObserverMethod2();
                    super.onPostExecute(aVoid);
                }
            }
            DeleteMedia st = new DeleteMedia();
            st.execute();
        }

        private void CallObserverMethod2() {
            DatabaseClient
                    .getInstance(getActivity())
                    .getaudioDatabase()
                    .taskDao()
                    .geAllData1("").observe(getActivity(), audioList -> {
                if (audioList.size() != 0) {
                    if (audioList.size() == 0) {
                        tvFound.setVisibility(View.VISIBLE);
                    } else {
                        llError.setVisibility(View.GONE);
                        LocalBroadcastManager.getInstance(getActivity())
                                .registerReceiver(listener, new IntentFilter("play_pause_Action"));
                        adapter = new AudioDownlaodsAdapter(audioList, ctx, UserID, progressBarHolder, ImgV, llError, rvDownloadsList, tvFound);
                        rvDownloadsList.setAdapter(adapter);
                    }
                    llError.setVisibility(View.GONE);
                    rvDownloadsList.setVisibility(View.VISIBLE);
                } else {
                    llError.setVisibility(View.VISIBLE);
                    rvDownloadsList.setVisibility(View.GONE);
                }
            });

        }

        /*public List<DownloadAudioDetails> GetAllMedia(FragmentActivity ctx) {
            downloadAudioDetailsList = new ArrayList<>();
            class GetTask extends AsyncTask<Void, Void, Void> {
                @Override
                protected Void doInBackground(Void... voids) {
                    downloadAudioDetailsList = DatabaseClient
                            .getInstance(ctx)
                            .getaudioDatabase()
                            .taskDao()
                            .geAllData("");
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    if (downloadAudioDetailsList.size() != 0) {
                        if (downloadAudioDetailsList.size() == 0) {
                            tvFound.setVisibility(View.VISIBLE);
                        } else {
                            llError.setVisibility(View.GONE);
                            LocalBroadcastManager.getInstance(getActivity())
                                    .registerReceiver(listener, new IntentFilter("play_pause_Action"));
                            adapter = new AudioDownlaodsAdapter(downloadAudioDetailsList, ctx, UserID, progressBarHolder, ImgV, llError, rvDownloadsList, tvFound);
                            rvDownloadsList.setAdapter(adapter);
                        }
                        llError.setVisibility(View.GONE);
                        rvDownloadsList.setVisibility(View.VISIBLE);
                    } else {
                        llError.setVisibility(View.VISIBLE);
                        rvDownloadsList.setVisibility(View.GONE);
                    }
                    super.onPostExecute(aVoid);
                }
            }

            GetTask st = new GetTask();
            st.execute();
            return downloadAudioDetailsList;
        }*/

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