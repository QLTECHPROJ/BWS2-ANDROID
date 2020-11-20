package com.brainwellnessspa.DownloadModule.Adapters;

import android.annotation.SuppressLint;
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

import com.brainwellnessspa.DownloadModule.Activities.DownloadPlaylistActivity;
import com.brainwellnessspa.databinding.AudioDownloadsLayoutBinding;
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
import com.brainwellnessspa.RoomDataBase.DownloadPlaylistDetails;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.DownloadsLayoutBinding;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.player;
import static com.brainwellnessspa.DashboardModule.Audio.AudioFragment.IsLock;
import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.downloadIdOne;
import static com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia.filename;
import static com.brainwellnessspa.Utility.MusicService.isCompleteStop;
import static com.brainwellnessspa.Utility.MusicService.isMediaStart;
import static com.brainwellnessspa.Utility.MusicService.isPause;
import static com.brainwellnessspa.Utility.MusicService.isPrepare;
import static com.brainwellnessspa.Utility.MusicService.stopMedia;


public class PlaylistsDownloadsAdapter extends RecyclerView.Adapter<PlaylistsDownloadsAdapter.MyViewHolder> {
    FragmentActivity ctx;
    String UserID;
    FrameLayout progressBarHolder;
    ProgressBar ImgV;
    List<DownloadAudioDetails> playlistWiseAudioDetails;
    List<DownloadAudioDetails> oneAudioDetailsList;
    List<DownloadPlaylistDetails> playlistList;
    LinearLayout llError;
    TextView tvFound;
    int count;
    RecyclerView rvDownloadsList;
    Runnable UpdateSongTime1;
    Handler handler1;
    List<String> fileNameList = new ArrayList<>(), playlistDownloadId = new ArrayList<>(), remainAudio = new ArrayList<>();
    private List<DownloadPlaylistDetails> listModelList;

    public PlaylistsDownloadsAdapter(List<DownloadPlaylistDetails> listModelList, FragmentActivity ctx, String UserID,
                                     FrameLayout progressBarHolder, ProgressBar ImgV, LinearLayout llError, TextView tvFound, RecyclerView rvDownloadsList) {
        this.listModelList = listModelList;
        this.ctx = ctx;
        this.UserID = UserID;
        this.progressBarHolder = progressBarHolder;
        this.ImgV = ImgV;
        this.llError = llError;
        this.tvFound = tvFound;
        this.rvDownloadsList = rvDownloadsList;
        handler1 = new Handler();
        playlistWiseAudioDetails = new ArrayList<>();
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
                    if(fileNameList.size()!=0) {
                        for (int f = 0; f < fileNameList.size(); f++) {
                            if (playlistDownloadId.get(f).equalsIgnoreCase(listModelList.get(position).getPlaylistID())) {
                                getMediaByPer(listModelList.get(position).getPlaylistID(), listModelList.get(position).getTotalAudio(), holder.binding.pbProgress);
                            }
                        }
                    }
                }catch (Exception e){}

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
        if(fileNameList.size()!=0) {
                for (int f = 0; f < fileNameList.size(); f++) {
                    if (playlistDownloadId.get(f).equalsIgnoreCase(listModelList.get(position).getPlaylistID())) {
                        getMediaByPer(listModelList.get(position).getPlaylistID(), listModelList.get(position).getTotalAudio(), holder.binding.pbProgress);
                    }
                }

        }
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
            if (IsLock.equalsIgnoreCase("1")) {
                holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                holder.binding.ivLock.setVisibility(View.VISIBLE);
                Intent i = new Intent(ctx, MembershipChangeActivity.class);
                i.putExtra("ComeFrom", "Plan");
                ctx.startActivity(i);
            }
            if (IsLock.equalsIgnoreCase("2")) {
                holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                holder.binding.ivLock.setVisibility(View.VISIBLE);
                BWSApplication.showToast("Please re-activate your membership plan", ctx);
            } else if (IsLock.equalsIgnoreCase("0")
                    || IsLock.equalsIgnoreCase("")) {
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
                i.putExtra("MyDownloads", "1");
                ctx.startActivity(i);
        /*        Intent i = new Intent(ctx, DownloadedPlaylist.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                i.putExtra("PlaylistID", listModelList.get(position).getPlaylistID());
                i.putExtra("PlaylistName", listModelList.get(position).getPlaylistName());
                i.putExtra("PlaylistImage", listModelList.get(position).getPlaylistImage());
                i.putExtra("PlaylistImage", listModelList.get(position).getPlaylistImage());
                ctx.startActivity(i);
                ctx.finish();*/
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
                getDownloadData(listModelList.get(position).getPlaylistID());
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
                    try {
                        playlistWiseAudioDetails = GetPlaylistMedia(listModelList.get(position).getPlaylistID());
                    } catch (Exception e) {
                    }
                    dialog.dismiss();
                });

                tvGoBack.setOnClickListener(v -> dialog.dismiss());
                dialog.show();
                dialog.setCancelable(false);
            }
        });
    }
    private void getDownloadData(String playlistID) {
        List<String> fileNameList,fileNameList1, audioFile, playlistDownloadId;
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
                    playlistDownloadId.contains(playlistID);
                    for (int i = 1; i < fileNameList1.size(); i++) {
                        if (playlistDownloadId.get(i).equalsIgnoreCase(playlistID)) {
                            fileNameList.remove(i);
                            audioFile.remove(i);
                            playlistDownloadId.remove(i);
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
                editor.commit();
                if(fileNameList.get(0).equalsIgnoreCase(filename) &&playlistDownloadId.get(0).equalsIgnoreCase(playlistID)){
                    PRDownloader.cancel(downloadIdOne);
                    filename ="";
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
                    handler1.postDelayed(UpdateSongTime1, 3000);
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
    private void getMediaByPer(String playlistID, String totalAudio, ProgressBar pbProgress) {
        class getMediaByPer extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                count = DatabaseClient.getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .getCountDownloadProgress("Complete", playlistID);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (count < Integer.parseInt(totalAudio)) {
                    long progressPercent = count * 100 / Integer.parseInt(totalAudio);
                    int downloadProgress1 = (int) progressPercent;
                    pbProgress.setVisibility(View.VISIBLE);
                    pbProgress.setProgress(downloadProgress1);
//                    getMediaByPer(playlistID,totalAudio,pbProgress);
                    handler1.postDelayed(UpdateSongTime1, 3000);
                } else {
                    pbProgress.setVisibility(View.GONE);
                    handler1.removeCallbacks(UpdateSongTime1);
                }
                super.onPostExecute(aVoid);
            }
        }

        getMediaByPer st = new getMediaByPer();
        st.execute();
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
                    if (oneAudioDetailsList.size() == 1) {
                        FileUtils.deleteDownloadedFile(ctx, oneAudioDetailsList.get(0).getName());
                    }
                }

                super.onPostExecute(aVoid);
            }
        }
        GetMedia sts = new GetMedia();
        sts.execute();
    }

    private void deleteDownloadFile(Context applicationContext, String PlaylistId) {
        class DeleteMedia extends AsyncTask<Void, Void, Void> {
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
                deletePlaylist(PlaylistId);
                super.onPostExecute(aVoid);
            }
        }
        DeleteMedia st = new DeleteMedia();
        st.execute();
    }

    private void deletePlaylist(String playlistId) {
        class DeleteMedia extends AsyncTask<Void, Void, Void> {
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
                GetAllMedia(ctx);
                super.onPostExecute(aVoid);
            }
        }
        DeleteMedia st = new DeleteMedia();
        st.execute();
    }

    private void GetAllMedia(FragmentActivity activity) {
        class GetTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                playlistList = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .getAllPlaylist();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (playlistList.size() != 0) {
                    llError.setVisibility(View.GONE);
                    tvFound.setVisibility(View.GONE);
                    PlaylistsDownloadsAdapter adapter = new PlaylistsDownloadsAdapter(playlistList, ctx, UserID, progressBarHolder, ImgV, llError, tvFound, rvDownloadsList);
                    rvDownloadsList.setAdapter(adapter);
                } else {
                    llError.setVisibility(View.VISIBLE);
                    tvFound.setVisibility(View.VISIBLE);
                    rvDownloadsList.setVisibility(View.GONE);
                }
                super.onPostExecute(aVoid);
            }
        }
        GetTask getTask = new GetTask();
        getTask.execute();
    }

    public List<DownloadAudioDetails> GetMedia(String playlistID) {
        playlistWiseAudioDetails = new ArrayList<>();
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
                listModelList2.add(mainPlayModel);
                listModelList2.addAll(playlistWiseAudioDetails);
                String json = gson.toJson(listModelList2);
                editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                editor.putInt(CONSTANTS.PREF_KEY_position, 0);
                editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
                editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "Downloadlist");
                editor.commit();
                try {
                    Fragment fragment = new TransparentPlayerFragment();
                    FragmentManager fragmentManager1 = ctx.getSupportFragmentManager();
                    fragmentManager1.beginTransaction()
                            .add(R.id.flContainer, fragment)
                            .commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onPostExecute(aVoid);
            }
        }
        GetMedia st = new GetMedia();
        st.execute();
        return playlistWiseAudioDetails;
    }

    public List<DownloadAudioDetails> GetPlaylistMedia(String playlistID) {
        playlistWiseAudioDetails = new ArrayList<>();
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
        return playlistWiseAudioDetails;
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
