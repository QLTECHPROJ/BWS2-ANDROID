package com.qltech.bws.DashboardModule.TransparentPlayer.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qltech.bws.DashboardModule.Activities.PlayWellnessActivity;
import com.qltech.bws.DashboardModule.Models.AppointmentDetailModel;
import com.qltech.bws.DashboardModule.Models.MainAudioModel;
import com.qltech.bws.DashboardModule.Models.SubPlayListModel;
import com.qltech.bws.DashboardModule.Models.ViewAllAudioListModel;
import com.qltech.bws.DownloadModule.Models.DownloadlistModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MusicService;
import com.qltech.bws.databinding.FragmentTransparentPlayerBinding;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.qltech.bws.Utility.MusicService.isPause;

public class TransparentPlayerFragment extends Fragment {
    public static FragmentTransparentPlayerBinding binding;
    String UserID, AudioFlag;
    int position = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_transparent_player, container, false);
        View view = binding.getRoot();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson));
        position = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");


        if (AudioFlag.equalsIgnoreCase("MainAudioList")) {
            Type type = new TypeToken<ArrayList<MainAudioModel.ResponseData.Detail>>() {
            }.getType();
            ArrayList<MainAudioModel.ResponseData.Detail> arrayList = gson.fromJson(json, type);

            MusicService.play(getActivity(), Uri.parse(arrayList.get(position).getAudioFile()));
            MusicService.playMedia();
            Glide.with(getActivity()).load(arrayList.get(position).getImageFile()).thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);

            binding.tvTitle.setText(arrayList.get(position).getName());
            binding.tvSubTitle.setText("Play the " + arrayList.get(position).getName() + " every night on a low volume.");

            binding.llPlayearMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), PlayWellnessActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    getActivity().startActivity(i);
                    SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putInt(CONSTANTS.PREF_KEY_position, position);
                    editor.commit();
                }
            });
        } else if (AudioFlag.equalsIgnoreCase("ViewAllAudioList")) {
            Type type = new TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail>>() {
            }.getType();
            ArrayList<ViewAllAudioListModel.ResponseData.Detail> arrayList = gson.fromJson(json, type);
            MusicService.play(getActivity(), Uri.parse(arrayList.get(position).getAudioFile()));
            MusicService.playMedia();
            Glide.with(getActivity()).load(arrayList.get(position).getImageFile()).thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);

            binding.tvTitle.setText(arrayList.get(position).getName());
            binding.tvSubTitle.setText("Play the " + arrayList.get(position).getName() + " every night on a low volume.");

            binding.llPlayearMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), PlayWellnessActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    getActivity().startActivity(i);

                    SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putInt(CONSTANTS.PREF_KEY_position, position);
                    editor.commit();
                }
            });
        } else if (AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
            Type type = new TypeToken<ArrayList<AppointmentDetailModel.Audio>>() {
            }.getType();
            ArrayList<AppointmentDetailModel.Audio> arrayList = gson.fromJson(json, type);
            MusicService.play(getActivity(), Uri.parse(arrayList.get(position).getAudioFile()));
            MusicService.playMedia();
            Glide.with(getActivity()).load(arrayList.get(position).getImageFile()).thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);

            binding.tvTitle.setText(arrayList.get(position).getName());
            binding.tvSubTitle.setText("Play the " + arrayList.get(position).getName() + " every night on a low volume.");

            binding.llPlayearMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), PlayWellnessActivity.class);
                    /*i.putParcelableArrayListExtra("modelList", arrayList);
                    i.putExtra("position", position);
                    i.putExtra("AudioFlag", "AppointmentDetailModel");*/
                     i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    MusicService.pauseMedia();
                    getActivity().startActivity(i);
                    SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putInt(CONSTANTS.PREF_KEY_position, position);
                    editor.commit();
                }
            });
        } else if (AudioFlag.equalsIgnoreCase("Downloadlist")) {
            Type type = new TypeToken<ArrayList<DownloadlistModel.Audio>>() {
            }.getType();
            ArrayList<DownloadlistModel.Audio> arrayList = gson.fromJson(json, type);

            MusicService.play(getActivity(), Uri.parse(arrayList.get(position).getAudioFile()));
            MusicService.playMedia();
            Glide.with(getActivity()).load(arrayList.get(position).getImageFile()).thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);

            binding.tvTitle.setText(arrayList.get(position).getName());
            binding.tvSubTitle.setText("Play the " + arrayList.get(position).getName() + " every night on a low volume.");

            binding.llPlayearMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), PlayWellnessActivity.class);
                   /* i.putParcelableArrayListExtra("modelList", arrayList);
                    i.putExtra("position", position);
                    i.putExtra("AudioFlag", "Downloadlist");*/
                    i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    getActivity().startActivity(i);
                    SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putInt(CONSTANTS.PREF_KEY_position, position);
                    editor.commit();
                }
            });
        } else if (AudioFlag.equalsIgnoreCase("SubPlayList")) {
            Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
            }.getType();
            ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);
            MusicService.play(getActivity(), Uri.parse(arrayList.get(position).getAudioFile()));
            MusicService.playMedia();
            Glide.with(getActivity()).load(arrayList.get(position).getImageFile()).thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage);

            binding.tvTitle.setText(arrayList.get(position).getName());
            binding.tvSubTitle.setText("Play the " + arrayList.get(position).getName() + " every night on a low volume.");

            binding.llPlayearMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), PlayWellnessActivity.class);
                    /*i.putParcelableArrayListExtra("modelList", arrayList);
                    i.putExtra("position", position);
                    i.putExtra("AudioFlag", "SubPlayList");*/
                    i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    getActivity().startActivity(i);

                    SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putInt(CONSTANTS.PREF_KEY_position, position);
                    editor.commit();
                }
            });
        }

        binding.ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.ivPause.setVisibility(View.GONE);
                binding.ivPlay.setVisibility(View.VISIBLE);
                MusicService.pauseMedia();
            }
        });

        binding.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.ivPlay.setVisibility(View.GONE);
                binding.ivPause.setVisibility(View.VISIBLE);
                MusicService.playMedia();
            }
        });

        return view;
    }
}