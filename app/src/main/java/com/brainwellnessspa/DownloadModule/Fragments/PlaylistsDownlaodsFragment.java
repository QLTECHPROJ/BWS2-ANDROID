package com.brainwellnessspa.DownloadModule.Fragments;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.brainwellnessspa.DownloadModule.Adapters.PlaylistsDownloadsAdapter;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadPlaylistDetails;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.FragmentDownloadsBinding;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.brainwellnessspa.DownloadModule.Activities.DownloadPlaylistActivity.comeDeletePlaylist;

public class PlaylistsDownlaodsFragment extends Fragment {
    FragmentDownloadsBinding binding;
    List<DownloadPlaylistDetails> playlistList;
    String UserID, AudioFlag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_downloads, container, false);
        View view = binding.getRoot();
        if (getArguments() != null) {
            UserID = getArguments().getString("UserID");
//            playlistList = getArguments().getParcelableArrayList("playlistsDownlaodsFragment");
        }
//        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
//        String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");

//        binding.tvFound.setText("Audio you are searching for is not available in the list");
        playlistList = new ArrayList<>();
        binding.tvFound.setText("Your downloaded playlists will appear here");
        GetAllMedia(getActivity());

        RefreshData();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvDownloadsList.setLayoutManager(mLayoutManager);
        binding.rvDownloadsList.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (comeDeletePlaylist == 1) {
            GetAllMedia(getActivity());
            comeDeletePlaylist = 0;
        }
        GetAllMedia(getActivity());
        RefreshData();
    }

    public void RefreshData() {
        if (!AudioFlag.equalsIgnoreCase("0")) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(13, 9, 13, 84);
            binding.llSpace.setLayoutParams(params);
        } else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(13, 9, 13, 28);
            binding.llSpace.setLayoutParams(params);
        }
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
                    getDataList(playlistList, UserID, binding.progressBarHolder, binding.progressBar);
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

    private void getDataList(List<DownloadPlaylistDetails> historyList, String UserID, FrameLayout progressBarHolder, ProgressBar ImgV) {
        if (historyList.size() == 0) {
            binding.tvFound.setVisibility(View.VISIBLE);
            binding.llError.setVisibility(View.VISIBLE);
        } else {
            binding.llError.setVisibility(View.GONE);
            PlaylistsDownloadsAdapter adapter = new PlaylistsDownloadsAdapter(historyList, getActivity(), UserID, progressBarHolder, ImgV, binding.llError, binding.tvFound, binding.rvDownloadsList);
            binding.rvDownloadsList.setAdapter(adapter);
        }
    }
}