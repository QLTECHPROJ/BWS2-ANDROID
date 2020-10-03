package com.qltech.bws.DashboardModule.Playlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Models.ViewAllAudioListModel;
import com.qltech.bws.DashboardModule.Models.ViewAllPlayListModel;
import com.qltech.bws.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.qltech.bws.R;
import com.qltech.bws.RoomDataBase.DatabaseClient;
import com.qltech.bws.RoomDataBase.DownloadAudioDetails;
import com.qltech.bws.RoomDataBase.DownloadPlaylistDetails;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.FragmentViewAllPlaylistBinding;
import com.qltech.bws.databinding.PlaylistCustomLayoutBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.qltech.bws.DashboardModule.Activities.DashboardActivity.player;
import static com.qltech.bws.DashboardModule.Search.SearchFragment.comefrom_search;
import static com.qltech.bws.DashboardModule.Audio.AudioFragment.IsLock;
import static com.qltech.bws.Utility.MusicService.isMediaStart;
import static com.qltech.bws.Utility.MusicService.isPause;
import static com.qltech.bws.Utility.MusicService.isPrepare;
import static com.qltech.bws.Utility.MusicService.stopMedia;

public class ViewAllPlaylistFragment extends Fragment {
    FragmentViewAllPlaylistBinding binding;
    String GetLibraryID, Name, UserID, AudioFlag, MyDownloads;
    public static String GetPlaylistLibraryID = "";
    List<DownloadPlaylistDetails> playlistList;
    List<DownloadAudioDetails> playlistWiseAudioDetails = new ArrayList<>();
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_all_playlist, container, false);
        view = binding.getRoot();
        Glide.with(getActivity()).load(R.drawable.loading).asGif().into(binding.ImgV);
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        if (getArguments() != null) {
            GetLibraryID = getArguments().getString("GetLibraryID");
            Name = getArguments().getString("Name");
            MyDownloads = getArguments().getString("MyDownloads");
        }

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                callBack();
                return true;
            }
            return false;
        });

        binding.llBack.setOnClickListener(view1 -> {
            callBack();
        });

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
        binding.rvMainAudio.setLayoutManager(manager);
        if (MyDownloads.equalsIgnoreCase("1")) {
            playlistList = new ArrayList<>();
            GetAllMedia();
        } else {
            prepareData();
        }
        return view;
    }

    private void GetAllMedia() {
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
                binding.tvTitle.setText("My Downloads");

                ArrayList<ViewAllPlayListModel.ResponseData.Detail> listModelList = new ArrayList<>();
                for (int i = 0; i < playlistList.size(); i++) {
                    ViewAllPlayListModel.ResponseData.Detail detail = new ViewAllPlayListModel.ResponseData.Detail();

                    detail.setPlaylistID(playlistList.get(i).getPlaylistID());
                    detail.setPlaylistDesc(playlistList.get(i).getPlaylistDesc());
                    detail.setPlaylistName(playlistList.get(i).getPlaylistName());
                    detail.setPlaylistImage(playlistList.get(i).getPlaylistImage());
                    listModelList.add(detail);
                }
                PlaylistAdapter adapter = new PlaylistAdapter(listModelList, IsLock);
                binding.rvMainAudio.setAdapter(adapter);
                super.onPostExecute(aVoid);
            }
        }
        GetTask getTask = new GetTask();
        getTask.execute();
    }

    private void callBack() {
        Fragment audioFragment = new PlaylistFragment();
        FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
        fragmentManager1.beginTransaction()
                .replace(R.id.flContainer, audioFragment)
                .commit();
        Bundle bundle = new Bundle();
        audioFragment.setArguments(bundle);
    }

    @Override
    public void onResume() {
        super.onResume();
//        prepareData();
    }

    private void prepareData() {
        try {
            if (IsLock.equalsIgnoreCase("1") && !AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
                SharedPreferences sharedm = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editorr = sharedm.edit();
                editorr.remove(CONSTANTS.PREF_KEY_modelList);
                editorr.remove(CONSTANTS.PREF_KEY_position);
                editorr.remove(CONSTANTS.PREF_KEY_queuePlay);
                editorr.remove(CONSTANTS.PREF_KEY_audioPlay);
                editorr.remove(CONSTANTS.PREF_KEY_AudioFlag);
                editorr.remove(CONSTANTS.PREF_KEY_PlaylistId);
                editorr.remove(CONSTANTS.PREF_KEY_myPlaylist);
                editorr.clear();
                editorr.commit();
            }
            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            if (!AudioFlag.equalsIgnoreCase("0")) {
                Fragment fragment = new TransparentPlayerFragment();
                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .add(R.id.flContainer, fragment)
                        .commit();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(4, 6, 4, 260);
                binding.llSpace.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(4, 6, 4, 50);
                binding.llSpace.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (BWSApplication.isNetworkConnected(getActivity())) {
            try {
                showProgressBar();
                Call<ViewAllPlayListModel> listCall = APIClient.getClient().getViewAllPlayLists(UserID, GetLibraryID);
                listCall.enqueue(new Callback<ViewAllPlayListModel>() {
                    @Override
                    public void onResponse(Call<ViewAllPlayListModel> call, Response<ViewAllPlayListModel> response) {
                        if (response.isSuccessful()) {
                            hideProgressBar();
                            ViewAllPlayListModel listModel = response.body();
                            binding.tvTitle.setText(listModel.getResponseData().getView());
                            PlaylistAdapter adapter = new PlaylistAdapter(listModel.getResponseData().getDetails(), listModel.getResponseData().getIsLock());
                            binding.rvMainAudio.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<ViewAllPlayListModel> call, Throwable t) {
                        hideProgressBar();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }

    private void hideProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.GONE);
            binding.ImgV.setVisibility(View.GONE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.VISIBLE);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            binding.ImgV.setVisibility(View.VISIBLE);
            binding.ImgV.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.MyViewHolder> {
        private List<ViewAllPlayListModel.ResponseData.Detail> listModelList;
        String IsLock;

        public PlaylistAdapter(List<ViewAllPlayListModel.ResponseData.Detail> listModelList, String IsLock) {
            this.listModelList = listModelList;
            this.IsLock = IsLock;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            PlaylistCustomLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.playlist_custom_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 0,
                    1, 1, 0.46f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);

            holder.binding.tvPlaylistName.setText(listModelList.get(position).getPlaylistName());
            Glide.with(getActivity()).load(listModelList.get(position).getPlaylistImage()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

            if (IsLock.equalsIgnoreCase("1")) {
                holder.binding.ivLock.setVisibility(View.VISIBLE);
            } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                holder.binding.ivLock.setVisibility(View.GONE);
            }

            holder.binding.rlMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (IsLock.equalsIgnoreCase("1")) {
                        holder.binding.ivLock.setVisibility(View.VISIBLE);
                        BWSApplication.showToast("Please re-activate your membership plan", getActivity());
                    } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                        holder.binding.ivLock.setVisibility(View.GONE);
                        if (MyDownloads.equalsIgnoreCase("1")) {
                            getMedia(listModelList.get(position).getPlaylistID());

                        } else {
                            Bundle bundle = new Bundle();
                            comefrom_search = 2;
                            GetPlaylistLibraryID = GetLibraryID;
                            Fragment myPlaylistsFragment = new MyPlaylistsFragment();
                            FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                            bundle.putString("New", "0");
                            bundle.putString("PlaylistID", listModelList.get(position).getPlaylistID());
                            bundle.putString("PlaylistName", listModelList.get(position).getPlaylistName());
                            bundle.putString("PlaylistImage", listModelList.get(position).getPlaylistImage());
                            bundle.putString("MyDownloads", MyDownloads);
                            myPlaylistsFragment.setArguments(bundle);
                            fragmentManager1.beginTransaction()
                                    .replace(R.id.flContainer, myPlaylistsFragment)
                                    .commit();
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            PlaylistCustomLayoutBinding binding;

            public MyViewHolder(PlaylistCustomLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    private void getMedia(String playlistID) {
        class GetMedia extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                playlistWiseAudioDetails = DatabaseClient
                        .getInstance(getActivity())
                        .getaudioDatabase()
                        .taskDao()
                        .getAllAudioByPlaylist(playlistID);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) { player = 1;
                if (isPrepare || isMediaStart || isPause) {
                    stopMedia();
                }
                isPause = false;
                isMediaStart = false;
                isPrepare = false;
                SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                Gson gson = new Gson();
                String json = gson.toJson(playlistWiseAudioDetails);
                editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                editor.putInt(CONSTANTS.PREF_KEY_position, 0);
                editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                editor.putString(CONSTANTS.PREF_KEY_PlaylistId, playlistID);
                editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
                editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SubPlayList");
                editor.commit();
                try {
                    Fragment fragment = new TransparentPlayerFragment();
                    FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                    fragmentManager1.beginTransaction()
                            .add(R.id.flContainer, fragment)
                            .commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                super.onPostExecute(aVoid);
            }
        }
        GetMedia st = new GetMedia();
        st.execute();
    }
}