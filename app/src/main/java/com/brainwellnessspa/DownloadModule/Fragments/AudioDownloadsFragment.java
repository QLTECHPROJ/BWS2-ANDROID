package com.brainwellnessspa.DownloadModule.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.DashboardModule.Activities.DashboardActivity;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.brainwellnessspa.DownloadModule.Adapters.AudioDownlaodsAdapter;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.SplashModule.SplashScreenActivity;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.FragmentDownloadsBinding;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AudioDownloadsFragment extends Fragment {
    FragmentDownloadsBinding binding;
    //    ArrayList<DownloadlistModel.Audio> audioList;
    List<DownloadAudioDetails> audioList;
    String UserID, AudioFlag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_downloads, container, false);
        View view = binding.getRoot();
        if (getArguments() != null) {
            UserID = getArguments().getString("UserID");
//            audioList = getArguments().getParcelableArrayList("audioDownloadsFragment");
        }
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");

        audioList = new ArrayList<>();
        audioList = GetAllMedia(getActivity());
        binding.tvFound.setText("Your downloaded audios will appear here");

        RefreshData();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvDownloadsList.setLayoutManager(mLayoutManager);
        binding.rvDownloadsList.setItemAnimator(new DefaultItemAnimator());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        RefreshData();
        audioList = GetAllMedia(getActivity());
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

    public List<DownloadAudioDetails> GetAllMedia(Context ctx) {
        class GetTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                audioList = DatabaseClient
                        .getInstance(ctx)
                        .getaudioDatabase()
                        .taskDao()
                        .geAllData("");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (audioList != null) {
                    if (audioList.size() != 0) {
                        getDataList(audioList, UserID, binding.progressBarHolder, binding.progressBar, binding.llError, binding.rvDownloadsList);
                        binding.llError.setVisibility(View.GONE);
                        binding.rvDownloadsList.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.llError.setVisibility(View.VISIBLE);
                    binding.rvDownloadsList.setVisibility(View.GONE);
                }
                super.onPostExecute(aVoid);
            }
        }
        GetTask st = new GetTask();
        st.execute();
        return audioList;
    }

    private void getDataList(List<DownloadAudioDetails> historyList, String UserID, FrameLayout progressBarHolder, ProgressBar ImgV, LinearLayout llError, RecyclerView rvDownloadsList) {
        if (historyList.size() == 0) {
            binding.tvFound.setVisibility(View.VISIBLE);
            binding.llError.setVisibility(View.VISIBLE);
            binding.llSpace.setVisibility(View.GONE);
        } else {
            binding.llError.setVisibility(View.GONE);
            binding.llSpace.setVisibility(View.VISIBLE);
            AudioDownlaodsAdapter adapter = new AudioDownlaodsAdapter(historyList, getActivity(), UserID, progressBarHolder, ImgV, llError, rvDownloadsList, binding.tvFound);
            binding.rvDownloadsList.setAdapter(adapter);
        }
    }
}