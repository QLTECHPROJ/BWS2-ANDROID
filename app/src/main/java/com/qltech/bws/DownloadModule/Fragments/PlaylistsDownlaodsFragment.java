package com.qltech.bws.DownloadModule.Fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.qltech.bws.DownloadModule.Adapters.AudioDownlaodsAdapter;
import com.qltech.bws.DownloadModule.Adapters.PlaylistsDownloadsAdapter;
import com.qltech.bws.DownloadModule.Models.DownloadlistModel;
import com.qltech.bws.DownloadModule.Models.PlaylistListModel;
import com.qltech.bws.R;
import com.qltech.bws.databinding.FragmentDownloadsBinding;

import java.util.ArrayList;
import java.util.List;

public class PlaylistsDownlaodsFragment extends Fragment {
    FragmentDownloadsBinding binding;
    ArrayList<DownloadlistModel.Playlist> playlistList;
    String UserID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_downloads, container, false);
        View view = binding.getRoot();
        if (getArguments() != null) {
            UserID = getArguments().getString("UserID");
            playlistList = getArguments().getParcelableArrayList("playlistsDownlaodsFragment");
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvDownloadsList.setLayoutManager(mLayoutManager);
        binding.rvDownloadsList.setItemAnimator(new DefaultItemAnimator());

        if (playlistList.size() != 0) {
            getDataList(playlistList, UserID, binding.progressBarHolder, binding.ImgV);
            binding.llError.setVisibility(View.GONE);
            binding.rvDownloadsList.setVisibility(View.VISIBLE);
        } else {
            binding.llError.setVisibility(View.VISIBLE);
            binding.rvDownloadsList.setVisibility(View.GONE);
        }
        return view;
    }

    private void getDataList(ArrayList<DownloadlistModel.Playlist> historyList, String UserID, FrameLayout progressBarHolder, ImageView ImgV) {
        if (historyList.size() == 0) {
            binding.tvFound.setVisibility(View.VISIBLE);
        } else {
            binding.llError.setVisibility(View.GONE);
            PlaylistsDownloadsAdapter adapter = new PlaylistsDownloadsAdapter(historyList, getActivity(), UserID, progressBarHolder, ImgV);
            binding.rvDownloadsList.setAdapter(adapter);
        }
    }
}