package com.qltech.bws.DashboardModule.TransparentPlayer.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qltech.bws.DashboardModule.Activities.PlayWellnessActivity;
import com.qltech.bws.DashboardModule.Models.AppointmentDetailModel;
import com.qltech.bws.DashboardModule.Models.MainAudioModel;
import com.qltech.bws.DashboardModule.Models.SubPlayListModel;
import com.qltech.bws.DashboardModule.Models.ViewAllAudioListModel;
import com.qltech.bws.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.qltech.bws.DownloadModule.Models.DownloadlistModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MusicService;
import com.qltech.bws.databinding.FragmentTransparentPlayerBinding;

import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;
import static com.qltech.bws.DashboardModule.Activities.DashboardActivity.player;
import static com.qltech.bws.Utility.MusicService.isMediaStart;

public class TransparentPlayerFragment extends Fragment implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    public FragmentTransparentPlayerBinding binding;
    String UserID, AudioFlag, IsRepeat, IsShuffle;
    int position = 0, startTime, oTime, listSize;
    private Handler hdlr;
    MainPlayModel mainPlayModel;
    ArrayList<MainPlayModel> mainPlayModelList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_transparent_player, container, false);
        View view = binding.getRoot();
        mainPlayModelList = new ArrayList<>();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        hdlr = new Handler();
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson));
        position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");

        binding.simpleSeekbar.setOnSeekBarChangeListener(this);
        SharedPreferences Status = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
        IsRepeat = Status.getString(CONSTANTS.PREF_KEY_IsRepeat, "");
        IsShuffle = Status.getString(CONSTANTS.PREF_KEY_IsShuffle, "");
        if (AudioFlag.equalsIgnoreCase("MainAudioList")) {
            Type type = new TypeToken<MainAudioModel.ResponseData.Detail>() {
            }.getType();
            MainAudioModel.ResponseData.Detail arrayList = gson.fromJson(json, type);
//            listSize = arrayList.size();
//            for (int i = 0; i < listSize; i++) {
            mainPlayModel = new MainPlayModel();
            mainPlayModel.setID(arrayList.getID());
            mainPlayModel.setName(arrayList.getName());
            mainPlayModel.setAudioFile(arrayList.getAudioFile());
            mainPlayModel.setAudioDirection(arrayList.getAudioDirection());
            mainPlayModel.setAudiomastercat(arrayList.getAudiomastercat());
            mainPlayModel.setAudioSubCategory(arrayList.getAudioSubCategory());
            mainPlayModel.setImageFile(arrayList.getImageFile());
            mainPlayModel.setLike(arrayList.getLike());
            mainPlayModel.setDownload(arrayList.getDownload());
            mainPlayModel.setAudioDuration(arrayList.getAudioDuration());
            mainPlayModelList.add(mainPlayModel);
//            }
            playmedia();

        } else if (AudioFlag.equalsIgnoreCase("ViewAllAudioList")) {
            Type type = new TypeToken<ViewAllAudioListModel.ResponseData.Detail>() {
            }.getType();
            ViewAllAudioListModel.ResponseData.Detail arrayList = gson.fromJson(json, type);
//            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.getID());
                mainPlayModel.setName(arrayList.getName());
                mainPlayModel.setAudioFile(arrayList.getAudioFile());
                mainPlayModel.setAudioDirection(arrayList.getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.getImageFile());
                mainPlayModel.setLike(arrayList.getLike());
                mainPlayModel.setDownload(arrayList.getDownload());
                mainPlayModel.setAudioDuration(arrayList.getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            playmedia();
        } else if (AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
            Type type = new TypeToken<AppointmentDetailModel.Audio>() {
            }.getType();
            AppointmentDetailModel.Audio arrayList = gson.fromJson(json, type);
//            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.getID());
                mainPlayModel.setName(arrayList.getName());
                mainPlayModel.setAudioFile(arrayList.getAudioFile());
                mainPlayModel.setAudioDirection(arrayList.getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.getImageFile());
                mainPlayModel.setLike(arrayList.getLike());
                mainPlayModel.setDownload(arrayList.getDownload());
                mainPlayModel.setAudioDuration(arrayList.getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            playmedia();
        } else if (AudioFlag.equalsIgnoreCase("Downloadlist")) {
            Type type = new TypeToken<ArrayList<DownloadlistModel.Audio>>() {
            }.getType();
            ArrayList<DownloadlistModel.Audio> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getAudioID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            playmedia();
        } else if (AudioFlag.equalsIgnoreCase("SubPlayList")) {
            Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
            }.getType();
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);
            listSize = arrayList.size();
            for (int i = 0; i < listSize; i++) {
                mainPlayModel = new MainPlayModel();
                mainPlayModel.setID(arrayList.get(i).getID());
                mainPlayModel.setName(arrayList.get(i).getName());
                mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
                mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
                mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
                mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
                mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
                mainPlayModel.setLike(arrayList.get(i).getLike());
                mainPlayModel.setDownload(arrayList.get(i).getDownload());
                mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
                mainPlayModelList.add(mainPlayModel);
            }
            playmedia();
        }

        if (listSize == 1) {
            SharedPreferences sharedxx = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_Status, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedxx.edit();
            editor.putString(CONSTANTS.PREF_KEY_IsShuffle, "");
            editor.commit();
            IsShuffle = "";
        }
        binding.ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.ivPause.setVisibility(View.GONE);
                binding.ivPlay.setVisibility(View.VISIBLE);
                if (player == 1) {
                    MusicService.play(getActivity(), Uri.parse(mainPlayModelList.get(position).getAudioFile()));
                    MusicService.playMedia();
                } else {
                    MusicService.pauseMedia();
                }

            }
        });

        binding.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.ivPlay.setVisibility(View.GONE);
                binding.ivPause.setVisibility(View.VISIBLE);
                if (player == 1) {
                    MusicService.play(getActivity(), Uri.parse(mainPlayModelList.get(position).getAudioFile()));
                    MusicService.playMedia();
                } else {
                    MusicService.resumeMedia();
                }
            }
        });

        return view;
    }

    private void playmedia() {
        if(listSize == 1){
            position = 0;
        }
        Glide.with(getActivity()).load(mainPlayModelList.get(position).getImageFile()).thumbnail(0.05f)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);

        binding.tvTitle.setText(mainPlayModelList.get(position).getName());
        binding.tvSubTitle.setText("Play the " + mainPlayModelList.get(position).getName() + " every night on a low volume.");
        if (player == 1) {
            if (MusicService.isPause) {
                binding.ivPlay.setVisibility(View.GONE);
                binding.ivPause.setVisibility(View.VISIBLE);
                MusicService.resumeMedia();
            } else {
                binding.ivPause.setVisibility(View.GONE);
                binding.ivPlay.setVisibility(View.VISIBLE);
                MusicService.play(getActivity(), Uri.parse(mainPlayModelList.get(position).getAudioFile()));
                MusicService.playMedia();
            }
        }
        binding.simpleSeekbar.setClickable(false);

        if (isMediaStart) {
            MusicService.mediaPlayer.setOnCompletionListener(this);
        }
        startTime = MusicService.getStartTime();

        hdlr.postDelayed(UpdateSongTime, 60);

        binding.llPlayearMain.setOnClickListener(view -> {
            if (player == 0) {
                player = 1;
            } else {
                if (MusicService.isPlaying()) {
                    MusicService.pauseMedia();
                }
            }
            Intent i = new Intent(getActivity(), PlayWellnessActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            getActivity().startActivity(i);
            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String json = gson.toJson(mainPlayModelList);
            editor.putString(CONSTANTS.PREF_KEY_audioList, json);
            editor.putInt(CONSTANTS.PREF_KEY_position, position);
            editor.commit();
        });
    }

    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            startTime = MusicService.getStartTime();

            binding.simpleSeekbar.setMax(100);
            Time t = Time.valueOf("00:" + mainPlayModelList.get(position).getAudioDuration());
            long totalDuration = t.getTime();
            long currentDuration = MusicService.getStartTime();

            int progress = (int) (MusicService.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            binding.simpleSeekbar.setProgress(progress);

            // Running this thread after 100 milliseconds
            hdlr.postDelayed(this, 60);
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (IsRepeat.equalsIgnoreCase("1")) {
            // repeat is on play same song again
            playmedia();
        } else if (IsShuffle.equalsIgnoreCase("1")) {
            // shuffle is on - play a random song
            if (listSize == 1) {

            } else {
                Random random = new Random();
                position = random.nextInt((listSize - 1) - 0 + 1) + 0;
            }
            playmedia();
        } else {
            if (position < (listSize - 1)) {
                position = position + 1;
            } else {
                position = 0;
            }
            playmedia();
        }
    }
}