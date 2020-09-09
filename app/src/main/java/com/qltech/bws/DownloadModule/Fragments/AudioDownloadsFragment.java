package com.qltech.bws.DownloadModule.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.qltech.bws.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.qltech.bws.DownloadModule.Adapters.AudioDownlaodsAdapter;
import com.qltech.bws.DownloadModule.Models.DownloadlistModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.FragmentDownloadsBinding;

import java.util.ArrayList;

public class AudioDownloadsFragment extends Fragment {
    FragmentDownloadsBinding binding;
    ArrayList<DownloadlistModel.Audio> audioList;
    String UserID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_downloads, container, false);
        View view = binding.getRoot();
        Glide.with(getActivity()).load(R.drawable.loading).asGif().into(binding.ImgV);
        if (getArguments() != null) {
            UserID = getArguments().getString("UserID");
            audioList = getArguments().getParcelableArrayList("audioDownloadsFragment");
        }
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");

        if (!AudioFlag.equalsIgnoreCase("0")) {
            Fragment fragment = new TransparentPlayerFragment();
            FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .add(R.id.f_audio, fragment)
                    .commit();
        }
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvDownloadsList.setLayoutManager(mLayoutManager);
        binding.rvDownloadsList.setItemAnimator(new DefaultItemAnimator());

        if (audioList.size() != 0) {
            getDataList(audioList, UserID, binding.progressBarHolder, binding.ImgV);
            binding.llError.setVisibility(View.GONE);
            binding.rvDownloadsList.setVisibility(View.VISIBLE);
        } else {
            binding.llError.setVisibility(View.VISIBLE);
            binding.rvDownloadsList.setVisibility(View.GONE);
        }
        return view;
    }


    private void getDataList(ArrayList<DownloadlistModel.Audio> historyList, String UserID, FrameLayout progressBarHolder, ImageView ImgV) {
        if (historyList.size() == 0) {
            binding.tvFound.setVisibility(View.VISIBLE);
        } else {
            binding.llError.setVisibility(View.GONE);
            AudioDownlaodsAdapter adapter = new AudioDownlaodsAdapter(historyList, getActivity(), UserID, progressBarHolder, ImgV);
            binding.rvDownloadsList.setAdapter(adapter);
        }
    }
}