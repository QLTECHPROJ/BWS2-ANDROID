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
import com.qltech.bws.DownloadModule.Models.DownloadlistModel;
import com.qltech.bws.InvoiceModule.Fragments.MembershipInvoiceFragment;
import com.qltech.bws.InvoiceModule.Models.InvoiceListModel;
import com.qltech.bws.R;
import com.qltech.bws.databinding.FragmentDownloadsBinding;

import java.util.ArrayList;
import java.util.List;

public class AudioDownloadsFragment extends Fragment {
    FragmentDownloadsBinding binding;
    ArrayList<DownloadlistModel.Audio> audioList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_downloads, container, false);
        View view = binding.getRoot();

        if (getArguments() != null) {
            audioList = getArguments().getParcelableArrayList("audioDownloadsFragment");
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvDownloadsList.setLayoutManager(mLayoutManager);
        binding.rvDownloadsList.setItemAnimator(new DefaultItemAnimator());

        if (audioList.size() != 0) {
            getDataList(audioList);
            binding.llError.setVisibility(View.GONE);
            binding.rvDownloadsList.setVisibility(View.VISIBLE);
        } else {
            binding.llError.setVisibility(View.VISIBLE);
            binding.rvDownloadsList.setVisibility(View.GONE);

        }

        return view;
    }
    private void getDataList(ArrayList<DownloadlistModel.Audio> historyList) {
        if (historyList.size() == 0) {
            binding.tvFound.setVisibility(View.VISIBLE);
        } else {
            binding.llError.setVisibility(View.GONE);
            AudioDownlaodsAdapter adapter = new AudioDownlaodsAdapter(historyList, getActivity());
            binding.rvDownloadsList.setAdapter(adapter);
        }
    }
}