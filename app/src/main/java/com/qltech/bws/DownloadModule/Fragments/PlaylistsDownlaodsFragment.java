package com.qltech.bws.DownloadModule.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Playlist.MyPlaylistsFragment;
import com.qltech.bws.DownloadModule.Activities.DownloadedPlaylist;
import com.qltech.bws.DownloadModule.Adapters.PlaylistsDownloadsAdapter;
import com.qltech.bws.EncryptDecryptUtils.FileUtils;
import com.qltech.bws.R;
import com.qltech.bws.RoomDataBase.DatabaseClient;
import com.qltech.bws.RoomDataBase.DownloadAudioDetails;
import com.qltech.bws.RoomDataBase.DownloadPlaylistDetails;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.DownloadsLayoutBinding;
import com.qltech.bws.databinding.FragmentDownloadsBinding;

import java.util.ArrayList;
import java.util.List;

import static com.qltech.bws.DashboardModule.Audio.AudioFragment.IsLock;
import static com.qltech.bws.DashboardModule.Search.SearchFragment.comefrom_search;

public class PlaylistsDownlaodsFragment extends Fragment {
    FragmentDownloadsBinding binding;
    List<DownloadPlaylistDetails> playlistList;
    String UserID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_downloads, container, false);
        View view = binding.getRoot();
        if (getArguments() != null) {
            UserID = getArguments().getString("UserID");
//            playlistList = getArguments().getParcelableArrayList("playlistsDownlaodsFragment");
        }

        playlistList = new ArrayList<>();

       GetAllMedia(getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvDownloadsList.setLayoutManager(mLayoutManager);
        binding.rvDownloadsList.setItemAnimator(new DefaultItemAnimator());


        return view;
    }

    private void GetAllMedia(FragmentActivity activity) {
        class GetTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                playlistList = DatabaseClient
                        .getInstance(getActivity())
                        .getaudioDatabase()
                        .taskDao()
                        .getAllPlaylist();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (playlistList.size() != 0) {
                    getDataList(playlistList, UserID, binding.progressBarHolder, binding.ImgV);
                    binding.llError.setVisibility(View.GONE);
                    binding.rvDownloadsList.setVisibility(View.VISIBLE);
                } else {
                    binding.llError.setVisibility(View.VISIBLE);
                    binding.rvDownloadsList.setVisibility(View.GONE);
                }
                super.onPostExecute(aVoid);

            }
        }
        GetTask getTask = new GetTask();
        getTask.execute();

    }

    private void getDataList(List<DownloadPlaylistDetails>  historyList, String UserID, FrameLayout progressBarHolder, ImageView ImgV) {
        if (historyList.size() == 0) {
            binding.tvFound.setVisibility(View.VISIBLE);binding.llError.setVisibility(View.VISIBLE);
        } else {
            binding.llError.setVisibility(View.GONE);
            PlaylistsDownloadsAdapter adapter = new PlaylistsDownloadsAdapter(historyList, getActivity(), UserID, progressBarHolder, ImgV,binding.llError,binding.tvFound,binding.rvDownloadsList);
            binding.rvDownloadsList.setAdapter(adapter);
        }
    }
}