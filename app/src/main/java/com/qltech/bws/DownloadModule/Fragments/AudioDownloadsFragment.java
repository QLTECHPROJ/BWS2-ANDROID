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

import com.qltech.bws.DownloadModule.Adapters.AudioDownlaodsAdapter;
import com.qltech.bws.DownloadModule.Models.AudioListModel;
import com.qltech.bws.R;
import com.qltech.bws.databinding.FragmentDownloadsBinding;

import java.util.ArrayList;
import java.util.List;

public class AudioDownloadsFragment extends Fragment {
    FragmentDownloadsBinding binding;
    List<AudioListModel> listModelList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_downloads, container, false);
        View view = binding.getRoot();
        AudioDownlaodsAdapter adapter = new AudioDownlaodsAdapter(listModelList, getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvDownloadsList.setLayoutManager(mLayoutManager);
        binding.rvDownloadsList.setItemAnimator(new DefaultItemAnimator());
        binding.rvDownloadsList.setAdapter(adapter);

        prepareAudioData();
        return view;
    }

    private void prepareAudioData() {
        AudioListModel list = new AudioListModel("Motivation Program", "12:37");
        listModelList.add(list);
        list = new AudioListModel("Self-Discipline Program", "12:37");
        listModelList.add(list);
        list = new AudioListModel("Love Thy Self", "12:37");
        listModelList.add(list);
        list = new AudioListModel("I Can Attitude and Mind...", "12:37");
        listModelList.add(list);
        list = new AudioListModel("Motivation Program", "12:37");
        listModelList.add(list);
        list = new AudioListModel("Self-Discipline Program", "12:37");
        listModelList.add(list);
        list = new AudioListModel("Love Thy Self", "12:37");
        listModelList.add(list);
        list = new AudioListModel("I Can Attitude and Mind...", "12:37");
        listModelList.add(list);
        list = new AudioListModel("Motivation Program", "12:37");
        listModelList.add(list);
        list = new AudioListModel("Self-Discipline Program", "12:37");
        listModelList.add(list);
        list = new AudioListModel("Love Thy Self", "12:37");
        listModelList.add(list);
        list = new AudioListModel("I Can Attitude and Mind...", "12:37");
        listModelList.add(list);
        list = new AudioListModel("Motivation Program", "12:37");
        listModelList.add(list);
        list = new AudioListModel("Self-Discipline Program", "12:37");
        listModelList.add(list);
        list = new AudioListModel("Love Thy Self", "12:37");
        listModelList.add(list);
        list = new AudioListModel("I Can Attitude and Mind...", "12:37");
        listModelList.add(list);
    }

}