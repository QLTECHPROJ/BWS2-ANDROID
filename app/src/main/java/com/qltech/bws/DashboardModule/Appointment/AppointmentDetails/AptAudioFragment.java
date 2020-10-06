package com.qltech.bws.DashboardModule.Appointment.AppointmentDetails;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Activities.AddPlaylistActivity;
import com.qltech.bws.DashboardModule.Models.AppointmentDetailModel;
import com.qltech.bws.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.qltech.bws.EncryptDecryptUtils.DownloadMedia;
import com.qltech.bws.EncryptDecryptUtils.FileUtils;
import com.qltech.bws.R;
import com.qltech.bws.RoomDataBase.DatabaseClient;
import com.qltech.bws.RoomDataBase.DownloadAudioDetails;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.AudioAptListLayoutBinding;
import com.qltech.bws.databinding.FragmentAptAudioBinding;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.qltech.bws.DashboardModule.Activities.DashboardActivity.player;
import static com.qltech.bws.EncryptDecryptUtils.DownloadMedia.downloadProgress;
import static com.qltech.bws.EncryptDecryptUtils.DownloadMedia.filename;
import static com.qltech.bws.Utility.MusicService.isMediaStart;
import static com.qltech.bws.Utility.MusicService.isPause;
import static com.qltech.bws.Utility.MusicService.isPrepare;
import static com.qltech.bws.Utility.MusicService.stopMedia;

public class AptAudioFragment extends Fragment {
    public FragmentManager f_manager;
    FragmentAptAudioBinding binding;
    String PlaylistId, UserID;
    ArrayList<AppointmentDetailModel.Audio> appointmentDetail;
    List<DownloadAudioDetails> oneAudioDetailsList;
    public static int comeRefreshData = 0;
    private Handler handler1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apt_audio, container, false);
        View view = binding.getRoot();
        oneAudioDetailsList = new ArrayList<>();
        handler1 = new Handler();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        appointmentDetail = new ArrayList<>();
        if (getArguments() != null) {
            appointmentDetail = getArguments().getParcelableArrayList("AppointmentDetailList");
        }
        if (appointmentDetail.size() == 0) {
        } else {
            AudioListAdapter appointmentsAdapter = new AudioListAdapter(appointmentDetail, getActivity(), f_manager);
            RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            binding.rvAudioList.setLayoutManager(recentlyPlayed);
            binding.rvAudioList.setItemAnimator(new DefaultItemAnimator());
            binding.rvAudioList.setAdapter(appointmentsAdapter);
        }
        return view;
    }

    public void GetMedia(String AudioFile, Context ctx, String download, RelativeLayout llDownload, ImageView ivDownload) {
        oneAudioDetailsList = new ArrayList<>();
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
        st.execute();
    }

    public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.MyViewHolder> {
        public FragmentManager f_manager;
        Context ctx;
        String Name;
        List<String> fileNameList= new ArrayList<>();

        private ArrayList<AppointmentDetailModel.Audio> listModelList;
        Runnable UpdateSongTime1;
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
                   if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(Name)) {
                       if (downloadProgress < 100) {
                           holder.binding.pbProgress.setProgress(downloadProgress);
                           holder.binding.pbProgress.setVisibility(View.VISIBLE);
                       } else {
                           holder.binding.pbProgress.setVisibility(View.GONE);
                           handler1.removeCallbacks(UpdateSongTime1);
                       }
                   }
                    handler1.postDelayed(this, 500);
                }
            };
            if(fileNameList.size()!=0) {
                if (fileNameList.contains(listModelList.get(position).getName())) {
                    holder.binding.pbProgress.setVisibility(View.VISIBLE);
                    handler1.postDelayed(UpdateSongTime1, 500);
                } else {
                    holder.binding.pbProgress.setVisibility(View.GONE);
                }
            }
            holder.binding.tvTitle.setText(audiolist.getName());
            if (audiolist.getAudioDirection().equalsIgnoreCase("")) {
                holder.binding.tvTime.setVisibility(View.GONE);
            } else {
                holder.binding.tvTime.setVisibility(View.VISIBLE);
                holder.binding.tvTime.setText(audiolist.getAudioDirection());
            }
            if(!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(audiolist.getName())){
                handler1.postDelayed(UpdateSongTime1, 500);
            }else{
                holder.binding.pbProgress.setVisibility(View.GONE);
                handler1.removeCallbacks(UpdateSongTime1);
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
                    player = 1;
                    if (isPrepare || isMediaStart || isPause) {
                        stopMedia();
                    }
                    isPause = false;
                    isMediaStart = false;
                    isPrepare = false;
                    Fragment fragment = new TransparentPlayerFragment();
                    FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                    fragmentManager1.beginTransaction()
                            .add(R.id.flContainer, fragment)
                            .commit();

                    SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(listModelList.get(position));
                    editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                    editor.putInt(CONSTANTS.PREF_KEY_position, position);
                    editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                    editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                    editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                    editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
                    editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "AppointmentDetailList");
                    editor.commit();
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
                DownloadMedia downloadMedia = new DownloadMedia(getActivity().getApplicationContext());
                downloadMedia.encrypt1(url1, name1);
                fileNameList = url1;
                handler1.postDelayed(UpdateSongTime1, 500);
                String dirPath = FileUtils.getFilePath(getActivity().getApplicationContext(), Name);
                SaveMedia(new byte[1024], dirPath, listModelList.get(position), holder.binding.llDownload);
            });

            holder.binding.llRemoveAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(ctx, AddPlaylistActivity.class);
                    i.putExtra("AudioId", listModelList.get(position).getID());
                    i.putExtra("PlaylistID", "");
                    startActivity(i);
                }
            });
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
                    downloadAudioDetails.setEncodedBytes(encodeBytes);
                    downloadAudioDetails.setDirPath(dirPath);

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

    private void enableDownload(RelativeLayout llDownload, ImageView ivDownload) {
        llDownload.setClickable(true);
        llDownload.setEnabled(true);
        ivDownload.setColorFilter(getActivity().getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
        ivDownload.setImageResource(R.drawable.ic_download_white_icon);
    }

    private void disableDownload(RelativeLayout llDownload, ImageView ivDownload) {
        ivDownload.setImageResource(R.drawable.ic_download_white_icon);
        ivDownload.setColorFilter(getActivity().getResources().getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN);
        llDownload.setClickable(false);
        llDownload.setEnabled(false);
    }
}