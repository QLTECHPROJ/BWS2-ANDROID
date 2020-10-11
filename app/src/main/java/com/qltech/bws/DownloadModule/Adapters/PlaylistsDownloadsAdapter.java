package com.qltech.bws.DownloadModule.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.BillingOrderModule.Activities.MembershipChangeActivity;
import com.qltech.bws.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.qltech.bws.EncryptDecryptUtils.FileUtils;
import com.qltech.bws.R;
import com.qltech.bws.RoomDataBase.DatabaseClient;
import com.qltech.bws.RoomDataBase.DownloadAudioDetails;
import com.qltech.bws.RoomDataBase.DownloadPlaylistDetails;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.DownloadsLayoutBinding;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.qltech.bws.DashboardModule.Activities.DashboardActivity.player;
import static com.qltech.bws.DashboardModule.Audio.AudioFragment.IsLock;
import static com.qltech.bws.Utility.MusicService.isMediaStart;
import static com.qltech.bws.Utility.MusicService.isPause;
import static com.qltech.bws.Utility.MusicService.isPrepare;
import static com.qltech.bws.Utility.MusicService.stopMedia;

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
    RecyclerView rvDownloadsList;
    //    Runnable UpdateSongTime1;
//    Handler handler1;
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
//        handler1 = new Handler();
        playlistWiseAudioDetails = new ArrayList<>();
        oneAudioDetailsList = new ArrayList<>();
//        getDownloadData();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DownloadsLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.downloads_layout, parent, false);
        return new MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.tvTitle.setText(listModelList.get(position).getPlaylistName());
      /*  UpdateSongTime1 = new Runnable() {
            @Override
            public void run() {
                if (fileNameList.size() != 0) {
                    for (int f = 0; f < listModelList.size(); f++) {
                        if (remainAudio.size() < listModelList.size()) {
                            int total = listModelList.size();
                            int remain = remainAudio.size();
                            long progressPercent = remain * 100 / total;
                            int downloadProgress = (int) progressPercent;
                            if (downloadProgress < 100) {
                                holder.binding.pbProgress.setProgress(downloadProgress);
                                holder.binding.pbProgress.setVisibility(View.VISIBLE);
                            } else {
                                holder.binding.pbProgress.setVisibility(View.GONE);
                                handler1.removeCallbacks(UpdateSongTime1);
                            }
                        }
                    }
                    handler1.postDelayed(this, 500);
                    getDownloadData();
                }*//*if() {
                    for(int i = 0;i<fileNameList.size();i++){
                        if(playlistDownloadId.get(i).equalsIgnoreCase("")){
                            remainAudio2.add(playlistDownloadId.get(i));
                        }
                    }
                }*//*
            }
        };*/
     /*   if(fileNameList.size()!=0){
            if(playlistDownloadId.contains(listModelList.get(position).getPlaylistID())){
                holder.binding.pbProgress.setVisibility(View.VISIBLE);
                handler1.postDelayed(UpdateSongTime1,500);
            }else{
                holder.binding.pbProgress.setVisibility(View.GONE);
            }
        }*/
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
        } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
            holder.binding.ivBackgroundImage.setVisibility(View.GONE);
            holder.binding.ivLock.setVisibility(View.GONE);
        }

        holder.binding.llMainLayout.setOnClickListener(view -> {
            if (IsLock.equalsIgnoreCase("1")) {
                holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                holder.binding.ivLock.setVisibility(View.VISIBLE);
//      TODO          BWSApplication.showToast("Please re-activate your membership plan", ctx);
                Intent i = new Intent(ctx, MembershipChangeActivity.class);
                i.putExtra("ComeFrom", "Plan");
                ctx.startActivity(i);
            } else if (IsLock.equalsIgnoreCase("0")
                    || IsLock.equalsIgnoreCase("")) {
                playlistWiseAudioDetails = GetMedia(listModelList.get(position).getPlaylistID());
                holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                holder.binding.ivLock.setVisibility(View.GONE);
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
        holder.binding.llRemoveAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlistWiseAudioDetails = GetPlaylistMedia(listModelList.get(position).getPlaylistID());
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
                player = 1;
                if (isPrepare || isMediaStart || isPause) {
                    stopMedia();
                }
                isPause = false;
                isMediaStart = false;
                isPrepare = false;
                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                Gson gson = new Gson();
                String json = gson.toJson(playlistWiseAudioDetails);
                editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                editor.putInt(CONSTANTS.PREF_KEY_position, 0);
                editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                editor.putString(CONSTANTS.PREF_KEY_PlaylistId, playlistID);
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
                for (int i = 0; i < playlistWiseAudioDetails.size(); i++) {
                    GetSingleMedia(playlistWiseAudioDetails.get(i).getAudioFile(), ctx.getApplicationContext(), playlistID);
                }
                super.onPostExecute(aVoid);
            }
        }
        GetMedia st = new GetMedia();
        st.execute();
        return playlistWiseAudioDetails;
    }

    @Override
    public int getItemCount() {
        return listModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        DownloadsLayoutBinding binding;

        public MyViewHolder(DownloadsLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
