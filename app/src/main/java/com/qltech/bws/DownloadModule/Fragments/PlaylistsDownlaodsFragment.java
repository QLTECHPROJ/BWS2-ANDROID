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

import com.qltech.bws.DownloadModule.Adapters.PlaylistsDownloadsAdapter;
import com.qltech.bws.DownloadModule.Models.PlaylistListModel;
import com.qltech.bws.R;
import com.qltech.bws.databinding.FragmentDownloadsBinding;

import java.util.ArrayList;
import java.util.List;

public class PlaylistsDownlaodsFragment extends Fragment {
    FragmentDownloadsBinding binding;
    List<PlaylistListModel> listModelList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_downloads, container, false);
        View view = binding.getRoot();

        PlaylistsDownloadsAdapter adapter = new PlaylistsDownloadsAdapter(listModelList, getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvDownloadsList.setLayoutManager(mLayoutManager);
        binding.rvDownloadsList.setItemAnimator(new DefaultItemAnimator());
        binding.rvDownloadsList.setAdapter(adapter);

        preparePlaylistsData();
        return view;
    }

    private void preparePlaylistsData() {
        PlaylistListModel list = new PlaylistListModel("Ultimate Anger Relief","8 Audios | 2h 54m");
        listModelList.add(list);
        list = new PlaylistListModel("Ultimate Executive Perform...","8 Audios | 2h 54m");
        listModelList.add(list);
        list = new PlaylistListModel("Ultimate Performance Acc...","8 Audios | 2h 54m");
        listModelList.add(list);
        list = new PlaylistListModel("Ultimate Relationship Brea...","8 Audios | 2h 54m");
        listModelList.add(list);
        list = new PlaylistListModel("Ultimate Anger Relief","8 Audios | 2h 54m");
        listModelList.add(list);
        list = new PlaylistListModel("Ultimate Executive Perform...","8 Audios | 2h 54m");
        listModelList.add(list);
        list = new PlaylistListModel("Ultimate Performance Acc...","8 Audios | 2h 54m");
        listModelList.add(list);
        list = new PlaylistListModel("Ultimate Relationship Brea...","8 Audios | 2h 54m");
        listModelList.add(list);
        list = new PlaylistListModel("Ultimate Anger Relief","8 Audios | 2h 54m");
        listModelList.add(list);
        list = new PlaylistListModel("Ultimate Executive Perform...","8 Audios | 2h 54m");
        listModelList.add(list);
        list = new PlaylistListModel("Ultimate Performance Acc...","8 Audios | 2h 54m");
        listModelList.add(list);
        list = new PlaylistListModel("Ultimate Relationship Brea...","8 Audios | 2h 54m");
        listModelList.add(list);
        list = new PlaylistListModel("Ultimate Anger Relief","8 Audios | 2h 54m");
        listModelList.add(list);
        list = new PlaylistListModel("Ultimate Executive Perform...","8 Audios | 2h 54m");
        listModelList.add(list);
        list = new PlaylistListModel("Ultimate Performance Acc...","8 Audios | 2h 54m");
        listModelList.add(list);
        list = new PlaylistListModel("Ultimate Relationship Brea...","8 Audios | 2h 54m");
        listModelList.add(list);
    }

}