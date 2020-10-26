package com.brainwellnessspa.DownloadModule.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.DashboardModule.Models.AppointmentDetailModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.downloader.PRDownloader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Activities.MembershipChangeActivity;
import com.brainwellnessspa.DashboardModule.Models.AddToQueueModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.brainwellnessspa.EncryptDecryptUtils.FileUtils;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.AudioDownloadsLayoutBinding;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.player;
import static com.brainwellnessspa.DashboardModule.Audio.AudioFragment.IsLock;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.downloadIdOne;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.downloadProgress;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.filename;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.isDownloading;
import static com.brainwellnessspa.Utility.MusicService.isCompleteStop;
import static com.brainwellnessspa.Utility.MusicService.isMediaStart;
import static com.brainwellnessspa.Utility.MusicService.isPause;
import static com.brainwellnessspa.Utility.MusicService.isPrepare;
import static com.brainwellnessspa.Utility.MusicService.stopMedia;


public class AudioDownlaodsAdapter extends RecyclerView.Adapter<AudioDownlaodsAdapter.MyViewHolder> {
    public static String comefromDownload = "0";
    FragmentActivity ctx;
    String UserID;
    FrameLayout progressBarHolder;
    ProgressBar ImgV;
    LinearLayout llError;
    RecyclerView rvDownloadsList;
    TextView tvFound;
    List<DownloadAudioDetails> downloadAudioDetailsList;
    Runnable UpdateSongTime1;
    List<String> fileNameList = new ArrayList<>(), playlistDownloadId = new ArrayList<>(), audiofilelist = new ArrayList<>();
    private List<DownloadAudioDetails> listModelList;
    private Handler handler1;
    List<DownloadAudioDetails> downloadedSingleAudio;


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
        handler1 = new Handler();
        downloadAudioDetailsList = new ArrayList<>();
        downloadedSingleAudio = new ArrayList<>();
        downloadedSingleAudio = getMyMedia();
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
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AudioDownloadsLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.audio_downloads_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
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

                    for (int f = 0; f < listModelList.size(); f++) {
                        if (fileNameList.size() != 0) {
                            for (int i = 0; i < fileNameList.size(); i++) {
                                if (fileNameList.get(i).equalsIgnoreCase(listModelList.get(f).getName())) {
                                    if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(listModelList.get(position).getName())) {
                                        if (downloadProgress <= 100) {
                                            if (BWSApplication.isNetworkConnected(ctx)) {
                                                notifyItemChanged(position);
                                            }
                                         /*   holder.binding.pbProgress.setProgress(downloadProgress);
                                            holder.binding.pbProgress.setVisibility(View.VISIBLE);
                                            holder.binding.ivDownloads.setVisibility(View.GONE);*/
                                        } else {
                                            holder.binding.pbProgress.setVisibility(View.GONE);
                                            //                                            handler2.removeCallbacks(UpdateSongTime2);
                                            getDownloadData();
                                        }
                                    } else {
                                        notifyItemChanged(position);
                                    }
                                }
                            }
                        }
                    }
                    if (downloadProgress == 0) {
                        notifyDataSetChanged();
                        getDownloadData();
                    }

                    handler1.postDelayed(this, 3000);
                } catch (Exception e) {
                }
            }};
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
//                                handler2.removeCallbacks(UpdateSongTime2);
                        }
                        handler1.postDelayed(UpdateSongTime1, 3000);
                    } else {
                        holder.binding.pbProgress.setVisibility(View.VISIBLE);
                        handler1.postDelayed(UpdateSongTime1, 3000);
                    }
                }
            }
        } else {
            holder.binding.pbProgress.setVisibility(View.GONE);
        }
        /*if(downloadedSingleAudio.size()!=0) {
                for (int i = 0; i < downloadedSingleAudio.size(); i++) {
                    if (downloadedSingleAudio.get(i).getName().equalsIgnoreCase(listModelList.get(position).getName())) {
                        if (downloadedSingleAudio.get(i).getDownloadProgress()<=100) {
                            //disableName.add(mData.get(position).getName());
                            if(downloadedSingleAudio.get(i).getDownloadProgress()==100){
                                holder.binding.pbProgress.setVisibility(View.GONE);
                                handler1.removeCallbacks(UpdateSongTime1);
                            }else {
                                holder.binding.pbProgress.setProgress(downloadedSingleAudio.get(i).getDownloadProgress());
                                holder.binding.pbProgress.setVisibility(View.VISIBLE);
                                handler1.postDelayed(UpdateSongTime1, 2000);
                            }
                        } else {
                            holder.binding.pbProgress.setVisibility(View.GONE);
                        }
                    }
                }
            }
         else {
            holder.binding.pbProgress.setVisibility(View.GONE);
        }*/
        holder.binding.tvTitle.setText(listModelList.get(position).getName());
        holder.binding.tvTime.setText(listModelList.get(position).getAudioDuration());
        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                1, 1, 0.12f, 0);
        holder.binding.cvImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        holder.binding.cvImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        Glide.with(ctx).load(listModelList.get(position).getImageFile()).thumbnail(0.05f)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
        holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg);
        comefromDownload = "1";
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
            if (IsLock.equalsIgnoreCase("1")) {
                holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                holder.binding.ivLock.setVisibility(View.VISIBLE);
                Intent i = new Intent(ctx, MembershipChangeActivity.class);
                i.putExtra("ComeFrom", "Plan");
                ctx.startActivity(i);
            } else if (IsLock.equalsIgnoreCase("2")) {
                holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                holder.binding.ivLock.setVisibility(View.VISIBLE);
                BWSApplication.showToast("Please re-activate your membership plan", ctx);
            } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                comefromDownload = "1";
                holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                holder.binding.ivLock.setVisibility(View.GONE);
          /*      DownloadMedia downloadMedia = new DownloadMedia(ctx.getApplicationContext());
                try {
                    FileDescriptor fileDescriptor = FileUtils.getTempFileDescriptor(ctx.getApplicationContext(), downloadMedia.decrypt(listModelList.get(position).getName()));
                    play2(fileDescriptor);
                    playMedia();

                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                try {
                    SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                    boolean queuePlay = shared1.getBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                    if (queuePlay) {
                        int position1 = shared1.getInt(CONSTANTS.PREF_KEY_position, 0);
                        ArrayList<AddToQueueModel> addToQueueModelList = new ArrayList<>();
                        Gson gson = new Gson();
                        String json1 = shared1.getString(CONSTANTS.PREF_KEY_queueList, String.valueOf(gson));
                        if (!json1.equalsIgnoreCase(String.valueOf(gson))) {
                            Type type1 = new TypeToken<ArrayList<AddToQueueModel>>() {
                            }.getType();
                            addToQueueModelList = gson.fromJson(json1, type1);
                        }
                        addToQueueModelList.remove(position1);
                        SharedPreferences shared2 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = shared2.edit();
                        String json = gson.toJson(addToQueueModelList);
                        editor.putString(CONSTANTS.PREF_KEY_queueList, json);
                        editor.commit();

                    }
                    player = 1;
                    if (isPrepare || isMediaStart || isPause) {
                        stopMedia();
                    }
                    isPause = false;
                    isMediaStart = false;
                    isPrepare = false;

                    isCompleteStop = false;

                    SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    Gson gson = new Gson();
                    ArrayList<DownloadAudioDetails> listModelList2 = new ArrayList<>();
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
                    mainPlayModel.setAudioDuration("0:48");
                    listModelList2.add(mainPlayModel);
                    listModelList2.add(listModelList.get(position));

                    String json = gson.toJson(listModelList2);
                    editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                    editor.putInt(CONSTANTS.PREF_KEY_position, 0);
                    editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                    editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                    editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                    editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
                    editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "DownloadListAudio");
                    editor.commit();
                    Fragment fragment = new TransparentPlayerFragment();
                    FragmentManager fragmentManager1 = ctx.getSupportFragmentManager();
                    fragmentManager1.beginTransaction()
                            .add(R.id.flContainer, fragment)
                            .commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        holder.binding.llRemoveAudio.setOnClickListener(view -> {
            try {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private List<DownloadAudioDetails> getMyMedia() {
        downloadedSingleAudio = new ArrayList<>();
        class GetMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                downloadedSingleAudio = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .getAllAudioByPlaylist("");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }
        GetMedia st = new GetMedia();
        st.execute();
        return downloadedSingleAudio;
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

    private void deleteDownloadFile(Context applicationContext, String audioFile, String audioName, int position) {
        FileUtils.deleteDownloadedFile(applicationContext, audioName);
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
                listModelList = new ArrayList<>();
                listModelList = GetAllMedia(ctx);
                super.onPostExecute(aVoid);
            }
        }
        DeleteMedia st = new DeleteMedia();
        st.execute();
    }

    public List<DownloadAudioDetails> GetAllMedia(FragmentActivity ctx) {
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
                        AudioDownlaodsAdapter adapter = new AudioDownlaodsAdapter(downloadAudioDetailsList, ctx, UserID, progressBarHolder, ImgV, llError, rvDownloadsList, tvFound);
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
