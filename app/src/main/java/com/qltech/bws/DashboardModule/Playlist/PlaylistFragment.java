package com.qltech.bws.DashboardModule.Playlist;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Models.CreatePlaylistModel;
import com.qltech.bws.DashboardModule.Models.MainPlayListModel;
import com.qltech.bws.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.qltech.bws.R;
import com.qltech.bws.RoomDataBase.DatabaseClient;
import com.qltech.bws.RoomDataBase.DownloadPlaylistDetails;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.FragmentPlaylistBinding;
import com.qltech.bws.databinding.MainAudioLayoutBinding;
import com.qltech.bws.databinding.PlaylistCustomLayoutBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.qltech.bws.DashboardModule.Playlist.MyPlaylistsFragment.comeAllPlaylist;
import static com.qltech.bws.DashboardModule.Playlist.ViewAllPlaylistFragment.ComeFromPlaylistViewAll;
import static com.qltech.bws.DashboardModule.Search.SearchFragment.comefrom_search;
import static com.qltech.bws.DashboardModule.Audio.AudioFragment.IsLock;

public class PlaylistFragment extends Fragment {
    FragmentPlaylistBinding binding;
    String UserID, Check = "", AudioFlag;
    List<DownloadPlaylistDetails> downloadPlaylistDetailsList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist, container, false);
        View view = binding.getRoot();
        Glide.with(getActivity()).load(R.drawable.loading).asGif().into(binding.ImgV);
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        if (getArguments() != null) {
            Check = getArguments().getString("Check");
        }
        downloadPlaylistDetailsList = new ArrayList<>();
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvMainPlayList.setLayoutManager(manager);
        binding.rvMainPlayList.setItemAnimator(new DefaultItemAnimator());
        prepareData();

        return view;
    }

    private void callMyPlaylistsFragment(String s, String id, String name, String playlistImage, String MyDownloads) {
        try {
            comefrom_search = 0;
            Bundle bundle = new Bundle();
            Fragment myPlaylistsFragment = new MyPlaylistsFragment();
            FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
            bundle.putString("New", s);
            bundle.putString("PlaylistID", id);
            bundle.putString("PlaylistName", name);
            bundle.putString("PlaylistImage", playlistImage);
            bundle.putString("MyDownloads", MyDownloads);
            myPlaylistsFragment.setArguments(bundle);
            fragmentManager1.beginTransaction()
                    .replace(R.id.flContainer, myPlaylistsFragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ComeFromPlaylistViewAll == 1) {
            prepareData();
            ComeFromPlaylistViewAll = 0;
        }
        prepareData();
    }

    private void prepareData() {
        try {
            if (!AudioFlag.equalsIgnoreCase("0")) {
                Fragment fragment = new TransparentPlayerFragment();
                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .commit();

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(13, 6, 13, 200);
                binding.llSpace.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(13, 6, 13, 0);
                binding.llSpace.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (BWSApplication.isNetworkConnected(getActivity())) {
            showProgressBar();
            Call<MainPlayListModel> listCall = APIClient.getClient().getMainPlayLists(UserID);
            listCall.enqueue(new Callback<MainPlayListModel>() {
                @Override
                public void onResponse(Call<MainPlayListModel> call, Response<MainPlayListModel> response) {
                    if (response.isSuccessful()) {
                        hideProgressBar();
                        MainPlayListModel listModel = response.body();
                        binding.rlCreatePlaylist.setVisibility(View.VISIBLE);
                        downloadPlaylistDetailsList = GetPlaylistDetail(listModel.getResponseData());
                    }
                }

                @Override
                public void onFailure(Call<MainPlayListModel> call, Throwable t) {
                    hideProgressBar();
                }
            });
        } else {
            ArrayList<MainPlayListModel.ResponseData> responseData = new ArrayList<>();
            ArrayList<MainPlayListModel.ResponseData.Detail> details = new ArrayList<>();
            MainPlayListModel.ResponseData listModel = new MainPlayListModel.ResponseData();
            listModel.setGetLibraryID("2");
            listModel.setDetails(details);
            listModel.setUserID(UserID);
            listModel.setView("My Downloads");
            listModel.setIsLock(IsLock);
            responseData.add(listModel);
            downloadPlaylistDetailsList = GetPlaylistDetail(responseData);
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }

    private List<DownloadPlaylistDetails> GetPlaylistDetail(ArrayList<MainPlayListModel.ResponseData> responseData) {
        ArrayList<MainPlayListModel.ResponseData.Detail> details = new ArrayList<>();
        class GetTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {

                downloadPlaylistDetailsList = DatabaseClient
                        .getInstance(getActivity())
                        .getaudioDatabase()
                        .taskDao()
                        .getAllPlaylist();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                if (downloadPlaylistDetailsList.size() != 0) {
                    for (int i = 0; i < downloadPlaylistDetailsList.size(); i++) {
                        MainPlayListModel.ResponseData.Detail detail = new MainPlayListModel.ResponseData.Detail();
                        detail.setTotalAudio(downloadPlaylistDetailsList.get(i).getTotalAudio());
                        detail.setTotalhour(downloadPlaylistDetailsList.get(i).getTotalhour());
                        detail.setTotalminute(downloadPlaylistDetailsList.get(i).getTotalminute());
                        detail.setPlaylistID(downloadPlaylistDetailsList.get(i).getPlaylistID());
                        detail.setPlaylistDesc(downloadPlaylistDetailsList.get(i).getPlaylistDesc());
                        detail.setMasterCategory(downloadPlaylistDetailsList.get(i).getPlaylistMastercat());
                        detail.setSubCategory(downloadPlaylistDetailsList.get(i).getPlaylistSubcat());
                        detail.setPlaylistName(downloadPlaylistDetailsList.get(i).getPlaylistName());
                        detail.setPlaylistImage(downloadPlaylistDetailsList.get(i).getPlaylistImage());
                        detail.setPlaylistId(downloadPlaylistDetailsList.get(i).getPlaylistID());
                        details.add(detail);
                    }
                    for (int i = 0; i < responseData.size(); i++) {
                        if (responseData.get(i).getView().equalsIgnoreCase("My Downloads")) {
                            responseData.get(i).setDetails(details);
                        }
                    }

                    MainPlayListAdapter adapter = new MainPlayListAdapter(responseData, getActivity());
                    binding.rvMainPlayList.setAdapter(adapter);
                } else {
                    MainPlayListAdapter adapter = new MainPlayListAdapter(responseData, getActivity());
                    binding.rvMainPlayList.setAdapter(adapter);
                }
                super.onPostExecute(aVoid);
            }
        }

        GetTask st = new GetTask();
        st.execute();
        return downloadPlaylistDetailsList;
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

    public class MainPlayListAdapter extends RecyclerView.Adapter<MainPlayListAdapter.MyViewHolder> {
        Context ctx;
        private ArrayList<MainPlayListModel.ResponseData> listModelList;

        public MainPlayListAdapter(ArrayList<MainPlayListModel.ResponseData> listModelList, Context ctx) {
            this.listModelList = listModelList;
            this.ctx = ctx;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            MainAudioLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.main_audio_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            if (listModelList.get(position).getDetails() != null &&
                    listModelList.get(position).getDetails().size() > 2) {
                holder.binding.tvViewAll.setVisibility(View.VISIBLE);
            } else {
                holder.binding.tvViewAll.setVisibility(View.GONE);
            }

            holder.binding.tvViewAll.setOnClickListener(view -> {
                comeAllPlaylist = 0;
                Fragment viewAllPlaylistFragment = new ViewAllPlaylistFragment();
                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .replace(R.id.flContainer, viewAllPlaylistFragment)
                        .commit();
                Bundle bundle = new Bundle();
                if (listModelList.get(position).getView().equalsIgnoreCase("My Downloads")) {
                    bundle.putString("MyDownloads", "1");
                } else {
                    bundle.putString("MyDownloads", "0");
                }
                bundle.putString("GetLibraryID", listModelList.get(position).getGetLibraryID());
                bundle.putString("Name", listModelList.get(position).getView());
                viewAllPlaylistFragment.setArguments(bundle);
            });

            binding.rlCreatePlaylist.setOnClickListener(view -> {
                if (listModelList.get(position).getIsLock().equalsIgnoreCase("1")) {
                    BWSApplication.showToast("Please re-activate your membership plan", getActivity());
                } else if (listModelList.get(position).getIsLock().equalsIgnoreCase("0")
                        || listModelList.get(position).getIsLock().equalsIgnoreCase("")) {
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.create_palylist);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_transparent)));
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    final EditText edtCreate = dialog.findViewById(R.id.edtCreate);
                    final TextView tvCancel = dialog.findViewById(R.id.tvCancel);
                    final RelativeLayout rlCreate = dialog.findViewById(R.id.rlCreate);
                    edtCreate.requestFocus();
                    dialog.setOnKeyListener((v, keyCode, event) -> {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            dialog.dismiss();
                            return true;
                        }
                        return false;
                    });

                    rlCreate.setOnClickListener(view1 -> {
                        if (edtCreate.getText().toString().equalsIgnoreCase("")) {
                            BWSApplication.showToast("Please provide the playlist's name", getActivity());
                        } else {
                            if (BWSApplication.isNetworkConnected(getActivity())) {
                                Call<CreatePlaylistModel> listCall = APIClient.getClient().getCreatePlaylist(UserID, edtCreate.getText().toString());
                                listCall.enqueue(new Callback<CreatePlaylistModel>() {
                                    @Override
                                    public void onResponse(Call<CreatePlaylistModel> call, Response<CreatePlaylistModel> response) {
                                        if (response.isSuccessful()) {
                                            CreatePlaylistModel listModel = response.body();
                                            if (listModel.getResponseData().getIscreated().equalsIgnoreCase("0")) {
                                                BWSApplication.showToast(listModel.getResponseMessage(), getActivity());
                                            } else if (listModel.getResponseData().getIscreated().equalsIgnoreCase("1") ||
                                                    listModel.getResponseData().getIscreated().equalsIgnoreCase("")) {
                                                callMyPlaylistsFragment("1", listModel.getResponseData().getId(), listModel.getResponseData().getName(), "", "0");
                                                dialog.dismiss();
                                            }

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<CreatePlaylistModel> call, Throwable t) {
                                        hideProgressBar();
                                    }
                                });
                            } else {
                                BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
                            }
                        }
                    });

                    tvCancel.setOnClickListener(v -> dialog.dismiss());
                    dialog.show();
                    dialog.setCancelable(false);
                }
            });

            GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
            holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
            holder.binding.rvMainAudio.setLayoutManager(manager);

            if (listModelList.get(position).getDetails().size() == 0) {
                holder.binding.llMainLayout.setVisibility(View.GONE);
            } else {
                holder.binding.llMainLayout.setVisibility(View.VISIBLE);
                holder.binding.tvTitle.setText(listModelList.get(position).getView());
                if (listModelList.get(position).getView().equalsIgnoreCase(getString(R.string.your_created))) {
                    PlaylistAdapter adapter1 = new PlaylistAdapter(listModelList.get(position).getDetails(), getActivity(),
                            listModelList.get(position).getIsLock(), "0");
                    holder.binding.rvMainAudio.setAdapter(adapter1);
                } else if (listModelList.get(position).getView().equalsIgnoreCase("My Downloads")) {
                    PlaylistAdapter adapter2 = new PlaylistAdapter(listModelList.get(position).getDetails(), getActivity(),
                            listModelList.get(position).getIsLock(), "1");
                    holder.binding.rvMainAudio.setAdapter(adapter2);
                } else if (listModelList.get(position).getView().equalsIgnoreCase(getString(R.string.Bundle))) {
                    PlaylistAdapter adapter3 = new PlaylistAdapter(listModelList.get(position).getDetails(), getActivity(),
                            listModelList.get(position).getIsLock(), "0");
                    holder.binding.rvMainAudio.setAdapter(adapter3);
                } else if (listModelList.get(position).getView().equalsIgnoreCase(getString(R.string.populars))) {
                    PlaylistAdapter adapter4 = new PlaylistAdapter(listModelList.get(position).getDetails(), getActivity(),
                            listModelList.get(position).getIsLock(), "0");
                    holder.binding.rvMainAudio.setAdapter(adapter4);
                }
            }

            if (!listModelList.get(position).getDetails().equals("")) {

            }
        }

        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            MainAudioLayoutBinding binding;

            public MyViewHolder(MainAudioLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.MyViewHolder> {
        Context ctx;
        String IsLock, MyDownloads;
        private ArrayList<MainPlayListModel.ResponseData.Detail> listModelList;

        public PlaylistAdapter(ArrayList<MainPlayListModel.ResponseData.Detail> listModelList, Context ctx, String IsLock, String MyDownloads) {
            this.listModelList = listModelList;
            this.ctx = ctx;
            this.IsLock = IsLock;
            this.MyDownloads = MyDownloads;
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
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.44f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);

            holder.binding.tvPlaylistName.setText(listModelList.get(position).getPlaylistName());
            Glide.with(ctx).load(listModelList.get(position).getPlaylistImage()).thumbnail(0.05f)
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
                        BWSApplication.showToast("Please re-activate your membership plan", ctx);
                    } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                        holder.binding.ivLock.setVisibility(View.GONE);
                        callMyPlaylistsFragment("0", listModelList.get(position).getPlaylistID(), listModelList.get(position).getPlaylistName(),
                                listModelList.get(position).getPlaylistImage(), MyDownloads);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            if (2 > listModelList.size()) {
                return listModelList.size();
            } else {
                return 2;
            }
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            PlaylistCustomLayoutBinding binding;

            public MyViewHolder(PlaylistCustomLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}