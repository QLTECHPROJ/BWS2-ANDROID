package com.brainwellnessspa.DownloadModule.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.DownloadModule.Adapters.AudioDownlaodsAdapter;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.databinding.FragmentDownloadsBinding;

import java.util.ArrayList;
import java.util.List;

public class AudioDownloadsFragment extends Fragment {
    FragmentDownloadsBinding binding;
    //    ArrayList<DownloadlistModel.Audio> audioList;
    List<DownloadAudioDetails> audioList;
    String UserID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_downloads, container, false);
        View view = binding.getRoot();
        if (getArguments() != null) {
            UserID = getArguments().getString("UserID");
//            audioList = getArguments().getParcelableArrayList("audioDownloadsFragment");
        }
//        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
//        String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");

        audioList = new ArrayList<>();
        audioList = GetAllMedia(getActivity());
        binding.tvFound.setText("Your downloaded audios will appear here");

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvDownloadsList.setLayoutManager(mLayoutManager);
        binding.rvDownloadsList.setItemAnimator(new DefaultItemAnimator());
        return view;
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
        } else {
            binding.llError.setVisibility(View.GONE);
            AudioDownlaodsAdapter adapter = new AudioDownlaodsAdapter(historyList, getActivity(), UserID, progressBarHolder, ImgV, llError, rvDownloadsList, binding.tvFound);
            binding.rvDownloadsList.setAdapter(adapter);
        }
    }
}