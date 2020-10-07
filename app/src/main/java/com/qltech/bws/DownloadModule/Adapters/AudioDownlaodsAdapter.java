package com.qltech.bws.DownloadModule.Adapters;

import android.content.Context;
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
import com.qltech.bws.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.qltech.bws.EncryptDecryptUtils.FileUtils;
import com.qltech.bws.R;
import com.qltech.bws.RoomDataBase.DatabaseClient;
import com.qltech.bws.RoomDataBase.DownloadAudioDetails;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.DownloadsLayoutBinding;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.qltech.bws.DashboardModule.Activities.DashboardActivity.player;
import static com.qltech.bws.DashboardModule.Audio.AudioFragment.IsLock;
import static com.qltech.bws.EncryptDecryptUtils.DownloadMedia.downloadProgress;
import static com.qltech.bws.EncryptDecryptUtils.DownloadMedia.filename;
import static com.qltech.bws.Utility.MusicService.isMediaStart;
import static com.qltech.bws.Utility.MusicService.isPause;
import static com.qltech.bws.Utility.MusicService.isPrepare;
import static com.qltech.bws.Utility.MusicService.stopMedia;

public class AudioDownlaodsAdapter extends RecyclerView.Adapter<AudioDownlaodsAdapter.MyViewHolder> {
    public static String comefromDownload = "";
    FragmentActivity ctx;
    String UserID;
    FrameLayout progressBarHolder;
    ProgressBar ImgV;
    LinearLayout llError;
    RecyclerView rvDownloadsList;
    TextView tvFound;
    List<DownloadAudioDetails> downloadAudioDetailsList;
    Runnable UpdateSongTime1;
    List<String> fileNameList = new ArrayList<>();
    private List<DownloadAudioDetails> listModelList;
    private Handler handler1;

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
        DownloadsLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.audio_downloads_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UpdateSongTime1 = new Runnable() {
            @Override
            public void run() {
                for (int f = 0; f < listModelList.size(); f++) {
                    if (fileNameList.contains(listModelList.get(f).getName())) {
                        if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(listModelList.get(f).getName())) {
                            if (downloadProgress <= 100) {
                                holder.binding.pbProgress.setProgress(downloadProgress);
                                holder.binding.pbProgress.setVisibility(View.VISIBLE);
                            } else {
                                holder.binding.pbProgress.setVisibility(View.GONE);
                                handler1.removeCallbacks(UpdateSongTime1);
                            }
                        }
                    }
                }
                handler1.postDelayed(this, 500);
            }
        };
        if (fileNameList.size() != 0) {
            if (fileNameList.contains(listModelList.get(position).getName())) {
                holder.binding.pbProgress.setVisibility(View.VISIBLE);
                handler1.postDelayed(UpdateSongTime1, 500);
            } else {
                holder.binding.pbProgress.setVisibility(View.GONE);
            }
        }
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
        } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
            holder.binding.ivBackgroundImage.setVisibility(View.GONE);
            holder.binding.ivLock.setVisibility(View.GONE);
        }

        holder.binding.llMainLayout.setOnClickListener(view -> {
            if (IsLock.equalsIgnoreCase("1")) {
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
                    player = 1;
                    if (isPrepare || isMediaStart || isPause) {
                        stopMedia();
                    }
                    isPause = false;
                    isMediaStart = false;
                    isPrepare = false;
                    Fragment fragment = new TransparentPlayerFragment();
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
                    editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "Downloadlist");
                    editor.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        holder.binding.llRemoveAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String AudioFile = listModelList.get(position).getAudioFile();
                String AudioName = listModelList.get(position).getName();
                deleteDownloadFile(ctx.getApplicationContext(), AudioFile, AudioName, position);
            }
        });
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
        DownloadsLayoutBinding binding;

        public MyViewHolder(DownloadsLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
