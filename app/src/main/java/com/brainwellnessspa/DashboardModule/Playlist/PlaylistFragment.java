package com.brainwellnessspa.DashboardModule.Playlist;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Activities.MembershipChangeActivity;
import com.brainwellnessspa.DashboardModule.Activities.AddPlaylistActivity;
import com.brainwellnessspa.DashboardModule.Models.CreatePlaylistModel;
import com.brainwellnessspa.DashboardModule.Models.MainPlayListModel;
import com.brainwellnessspa.Services.GlobalInitExoPlayer;
import com.brainwellnessspa.UserModule.Models.ProfileViewModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.RoomDataBase.DownloadPlaylistDetails;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.FragmentPlaylistBinding;
import com.brainwellnessspa.databinding.MainPlaylistLayoutBinding;
import com.brainwellnessspa.databinding.PlaylistCustomLayoutBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.segment.analytics.Properties;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.ComeScreenAccount;
import static com.brainwellnessspa.DashboardModule.Audio.AudioFragment.IsLock;

import static com.brainwellnessspa.DashboardModule.Search.SearchFragment.comefrom_search;
import static com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment.comefromDownload;

public class PlaylistFragment extends Fragment {
    FragmentPlaylistBinding binding;
    String UserID, Check = "", AudioFlag;
    View view;
    MainPlayListAdapter adapter;
    ArrayList<MainPlayListModel.ResponseData> listModelList;
    public static String PlaylistSource = "";
    PlaylistAdapter playlistAdapter;
    public static int ComeScreenMyPlaylist = 0;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist, container, false);
        view = binding.getRoot();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        ComeScreenAccount = 0;
        comefromDownload = "0";
        if (getArguments() != null) {
            Check = getArguments().getString("Check");
        }
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvMainPlayList.setLayoutManager(manager);
        binding.rvMainPlayList.setItemAnimator(new DefaultItemAnimator());
//        prepareData();
        return view;
    }

    private void callMyPlaylistsFragment(String s, String id, String name, String playlistImage, String MyDownloads, String ScreenView) {
        try {
            comefrom_search = 0;
            Bundle bundle = new Bundle();
            Fragment myPlaylistsFragment = new MyPlaylistsFragment();
            FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
            bundle.putString("New", s);
            bundle.putString("PlaylistID", id);
            bundle.putString("PlaylistName", name);
            bundle.putString("PlaylistImage", playlistImage);
            bundle.putString("PlaylistSource", PlaylistSource);
            bundle.putString("MyDownloads", MyDownloads);
            bundle.putString("ScreenView", ScreenView);
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
        prepareData();
        openMiniPlayer();
        ComeScreenAccount = 0;
        comefromDownload = "0";
        super.onResume();
    }

    private void prepareData() {
        if (BWSApplication.isNetworkConnected(getActivity())) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            Call<ProfileViewModel> listCall = APIClient.getClient().getProfileView(UserID);
            listCall.enqueue(new Callback<ProfileViewModel>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call<ProfileViewModel> call, Response<ProfileViewModel> response) {
                    try {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                        ProfileViewModel viewModel = response.body();
                        IsLock = viewModel.getResponseData().getIsLock();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ProfileViewModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                }
            });
        }

        if (BWSApplication.isNetworkConnected(getActivity())) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            Call<MainPlayListModel> listCall = APIClient.getClient().getMainPlayLists(UserID);
            listCall.enqueue(new Callback<MainPlayListModel>() {
                @Override
                public void onResponse(Call<MainPlayListModel> call, Response<MainPlayListModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                            MainPlayListModel listModel = response.body();
                            binding.rlCreatePlaylist.setVisibility(View.VISIBLE);
                            listModelList = listModel.getResponseData();
//                            adapter = new MainPlayListAdapter();
//                            binding.rvMainPlayList.setAdapter(adapter);
                            GetPlaylistDetail(listModel.getResponseData());
                            ArrayList<String> section = new ArrayList<>();
                            for (int i = 0; i < listModel.getResponseData().size(); i++) {
                                section.add(listModel.getResponseData().get(i).getView());
                            }
                            Properties p = new Properties();
                            p.putValue("userId", UserID);
                            Gson gson;
                            GsonBuilder gsonBuilder = new GsonBuilder();
                            gson = gsonBuilder.create();
                            p.putValue("sections", gson.toJson(section));
                            BWSApplication.addToSegment("Playlist Screen Viewed", p, CONSTANTS.screen);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<MainPlayListModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
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
            GetPlaylistDetail(responseData);
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }

    public void openMiniPlayer() {
        try {
            GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
            globalInitExoPlayer.UpdateMiniPlayer(getActivity());
            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            if (!AudioFlag.equalsIgnoreCase("0")) {

                Fragment fragment = new MiniPlayerFragment();
                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .add(R.id.flContainer, fragment)
                        .commit();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 6, 0, 260);
                binding.llSpace.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 6, 0, 50);
                binding.llSpace.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GetPlaylistDetail(ArrayList<MainPlayListModel.ResponseData> responseData) {
        DatabaseClient
                .getInstance(getActivity())
                .getaudioDatabase()
                .taskDao()
                .getAllPlaylist1().observe(getActivity(), audioList -> {
            ArrayList<MainPlayListModel.ResponseData.Detail> details = new ArrayList<>();

            if (audioList.size() != 0) {
                for (int i = 0; i < audioList.size(); i++) {
                    MainPlayListModel.ResponseData.Detail detail = new MainPlayListModel.ResponseData.Detail();
                    detail.setTotalAudio(audioList.get(i).getTotalAudio());
                    detail.setTotalhour(audioList.get(i).getTotalhour());
                    detail.setTotalminute(audioList.get(i).getTotalminute());
                    detail.setPlaylistID(audioList.get(i).getPlaylistID());
                    detail.setPlaylistDesc(audioList.get(i).getPlaylistDesc());
                    detail.setMasterCategory(audioList.get(i).getPlaylistMastercat());
                    detail.setSubCategory(audioList.get(i).getPlaylistSubcat());
                    detail.setPlaylistName(audioList.get(i).getPlaylistName());
                    detail.setPlaylistImage(audioList.get(i).getPlaylistImage());
                    detail.setPlaylistImageDetails(audioList.get(i).getPlaylistImageDetails());
                    detail.setPlaylistId(audioList.get(i).getPlaylistID());
                    detail.setCreated(audioList.get(i).getCreated());
                    details.add(detail);
                }
                for (int i = 0; i < responseData.size(); i++) {
                    if (responseData.get(i).getView().equalsIgnoreCase("My Downloads")) {
                        responseData.get(i).setDetails(details);
                    }
                }

                listModelList = new ArrayList<>();
                listModelList.addAll(responseData);
                adapter = new MainPlayListAdapter();
                binding.rvMainPlayList.setAdapter(adapter);
            } else {
                if (BWSApplication.isNetworkConnected(getActivity())) {
                    adapter = new MainPlayListAdapter();
                    binding.rvMainPlayList.setAdapter(adapter);
                }
            }
        });
    }

    public class MainPlayListAdapter extends RecyclerView.Adapter<MainPlayListAdapter.MyViewHolder> {

        public MainPlayListAdapter() {
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            MainPlaylistLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.main_playlist_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            if (listModelList.get(position).getDetails() != null &&
                    listModelList.get(position).getDetails().size() > 6) {
                holder.binding.tvViewAll.setVisibility(View.VISIBLE);
            } else {
                holder.binding.tvViewAll.setVisibility(View.GONE);
            }

            holder.binding.tvViewAll.setOnClickListener(view -> {
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

            if (listModelList.get(position).getIsLock().equalsIgnoreCase("1")) {
                binding.ivLock.setVisibility(View.VISIBLE);
            } else if (listModelList.get(position).getIsLock().equalsIgnoreCase("2")) {
                binding.ivLock.setVisibility(View.VISIBLE);
            } else if (listModelList.get(position).getIsLock().equalsIgnoreCase("0")
                    || listModelList.get(position).getIsLock().equalsIgnoreCase("")) {
                binding.ivLock.setVisibility(View.GONE);
            }

            binding.rlCreatePlaylist.setOnClickListener(view -> {
                Properties p = new Properties();
                p.putValue("userId", UserID);
                p.putValue("source", "Playlist Main Screen");
                BWSApplication.addToSegment("Create Playlist Clicked", p, CONSTANTS.track);
                if (listModelList.get(position).getIsLock().equalsIgnoreCase("1")) {
                    binding.ivLock.setVisibility(View.VISIBLE);
                    Intent i = new Intent(getActivity(), MembershipChangeActivity.class);
                    i.putExtra("ComeFrom", "Plan");
                    startActivity(i);
                } else if (listModelList.get(position).getIsLock().equalsIgnoreCase("2")) {
                    binding.ivLock.setVisibility(View.VISIBLE);
                    BWSApplication.showToast(getString(R.string.reactive_plan), getActivity());
                } else if (listModelList.get(position).getIsLock().equalsIgnoreCase("0")
                        || listModelList.get(position).getIsLock().equalsIgnoreCase("")) {
                    binding.ivLock.setVisibility(View.GONE);
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.create_palylist);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_transparent)));
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    final EditText edtCreate = dialog.findViewById(R.id.edtCreate);
                    final TextView tvCancel = dialog.findViewById(R.id.tvCancel);
                    final Button btnSendCode = dialog.findViewById(R.id.btnSendCode);
                    edtCreate.requestFocus();
                    TextWatcher popupTextWatcher = new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            String number = edtCreate.getText().toString().trim();
                            if (!number.isEmpty()) {
                                btnSendCode.setEnabled(true);
                                btnSendCode.setTextColor(getResources().getColor(R.color.white));
                                btnSendCode.setBackgroundResource(R.drawable.extra_round_cornor);
                            } else {
                                btnSendCode.setEnabled(false);
                                btnSendCode.setTextColor(getResources().getColor(R.color.white));
                                btnSendCode.setBackgroundResource(R.drawable.gray_round_cornor);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    };

                    edtCreate.addTextChangedListener(popupTextWatcher);
                    dialog.setOnKeyListener((v, keyCode, event) -> {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            dialog.dismiss();
                            return true;
                        }
                        return false;
                    });

                    btnSendCode.setOnClickListener(view1 -> {
                        if (BWSApplication.isNetworkConnected(getActivity())) {
                            Call<CreatePlaylistModel> listCall = APIClient.getClient().getCreatePlaylist(UserID, edtCreate.getText().toString());
                            listCall.enqueue(new Callback<CreatePlaylistModel>() {
                                @Override
                                public void onResponse(Call<CreatePlaylistModel> call, Response<CreatePlaylistModel> response) {
                                    try {
                                        if (response.isSuccessful()) {
                                            CreatePlaylistModel listModel = response.body();
                                            if (listModel.getResponseData().getIscreated().equalsIgnoreCase("0")) {
                                                BWSApplication.showToast(listModel.getResponseMessage(), getActivity());
                                            } else if (listModel.getResponseData().getIscreated().equalsIgnoreCase("1") ||
                                                    listModel.getResponseData().getIscreated().equalsIgnoreCase("")) {
                                                ComeScreenMyPlaylist = 1;
                                                callMyPlaylistsFragment("1", listModel.getResponseData().getId(), listModel.getResponseData().getName(), "", "0", "Your Created");
                                                /*Properties p = new Properties();
                                                p.putValue("userId", UserID);
                                                p.putValue("playlistId", listModel.getResponseData().getId());
                                                p.putValue("playlistName", listModel.getResponseData().getName());
                                                p.putValue("source", "Add To Playlist Screen");
                                                BWSApplication.addToSegment("Playlist Created", p, CONSTANTS.track);*/
                                                dialog.dismiss();
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<CreatePlaylistModel> call, Throwable t) {
                                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                                }
                            });
                        } else {
                            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
                        }
                    });
                    tvCancel.setOnClickListener(v -> dialog.dismiss());
                    dialog.show();
                    dialog.setCancelable(false);
                }
            });

            RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
            holder.binding.rvMainAudio.setLayoutManager(manager);

            if (listModelList.get(position).getDetails().size() == 0) {
                holder.binding.llMainLayout.setVisibility(View.GONE);
            } else {
                holder.binding.llMainLayout.setVisibility(View.VISIBLE);
                holder.binding.tvTitle.setText(listModelList.get(position).getView());
                if (listModelList.get(position).getView().equalsIgnoreCase(getString(R.string.your_created))) {
                    playlistAdapter = new PlaylistAdapter(listModelList.get(position).getDetails(), getActivity(),
                            listModelList.get(position).getIsLock(), "0", listModelList.get(position).getView());
                    holder.binding.rvMainAudio.setAdapter(playlistAdapter);
                } else if (listModelList.get(position).getView().equalsIgnoreCase("My Downloads")) {
                    playlistAdapter = new PlaylistAdapter(listModelList.get(position).getDetails(), getActivity(),
                            listModelList.get(position).getIsLock(), "1", listModelList.get(position).getView());
                    holder.binding.rvMainAudio.setAdapter(playlistAdapter);
                } else if (listModelList.get(position).getView().equalsIgnoreCase(getString(R.string.Recommended_Playlist))) {
                    playlistAdapter = new PlaylistAdapter(listModelList.get(position).getDetails(), getActivity(),
                            listModelList.get(position).getIsLock(), "0", listModelList.get(position).getView());
                    holder.binding.rvMainAudio.setAdapter(playlistAdapter);
                } else if (listModelList.get(position).getView().equalsIgnoreCase(getString(R.string.populars))) {
                    playlistAdapter = new PlaylistAdapter(listModelList.get(position).getDetails(), getActivity(),
                            listModelList.get(position).getIsLock(), "0", listModelList.get(position).getView());
                    holder.binding.rvMainAudio.setAdapter(playlistAdapter);
                }
            }
        }

        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            MainPlaylistLayoutBinding binding;

            public MyViewHolder(MainPlaylistLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.MyViewHolder> {
        Context ctx;
        String IsLock, MyDownloads, screenView;
        int index = -1;
        private ArrayList<MainPlayListModel.ResponseData.Detail> listModelList;

        public PlaylistAdapter(ArrayList<MainPlayListModel.ResponseData.Detail> listModelList, Context ctx, String IsLock, String MyDownloads, String screenView) {
            this.listModelList = listModelList;
            this.ctx = ctx;
            this.IsLock = IsLock;
            this.MyDownloads = MyDownloads;
            this.screenView = screenView;
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
                    1, 1, 0.38f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.binding.tvAddToPlaylist.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.tvAddToPlaylist.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            MeasureRatio measureRatio1 = BWSApplication.measureRatio(getActivity(), 0,
                    1, 1, 0.38f, 0);
            holder.binding.rlMainLayout.getLayoutParams().height = (int) (measureRatio1.getHeight() * measureRatio1.getRatio());
            holder.binding.rlMainLayout.getLayoutParams().width = (int) (measureRatio1.getWidthImg() * measureRatio1.getRatio());

            holder.binding.tvPlaylistName.setText(listModelList.get(position).getPlaylistName());
            Glide.with(ctx).load(listModelList.get(position).getPlaylistImage()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

            if (IsLock.equalsIgnoreCase("1")) {
                holder.binding.ivLock.setVisibility(View.VISIBLE);
            } else if (IsLock.equalsIgnoreCase("2")) {
                holder.binding.ivLock.setVisibility(View.VISIBLE);
            } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                holder.binding.ivLock.setVisibility(View.GONE);
            }

            if (index == position) {
                holder.binding.tvAddToPlaylist.setVisibility(View.VISIBLE);
            } else
                holder.binding.tvAddToPlaylist.setVisibility(View.GONE);

            holder.binding.tvAddToPlaylist.setText("Add To Playlist");
            holder.binding.rlMainLayout.setOnLongClickListener(v -> {
                holder.binding.tvAddToPlaylist.setVisibility(View.VISIBLE);
                index = position;
                notifyDataSetChanged();
                return true;
            });
            holder.binding.tvAddToPlaylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (IsLock.equalsIgnoreCase("1")) {
                        holder.binding.ivLock.setVisibility(View.VISIBLE);
                        Intent i = new Intent(ctx, MembershipChangeActivity.class);
                        i.putExtra("ComeFrom", "Plan");
                        ctx.startActivity(i);
                    } else if (IsLock.equalsIgnoreCase("2")) {
                        holder.binding.ivLock.setVisibility(View.VISIBLE);
                        Intent i = new Intent(ctx, MembershipChangeActivity.class);
                        i.putExtra("ComeFrom", "Plan");
                        ctx.startActivity(i);
                    } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                        holder.binding.ivLock.setVisibility(View.GONE);
                        Intent i = new Intent(ctx, AddPlaylistActivity.class);
                        i.putExtra("AudioId", "");
                        i.putExtra("ScreenView", "Playlist Main Screen");
                        i.putExtra("PlaylistID", listModelList.get(position).getPlaylistID());
                        i.putExtra("PlaylistName", listModelList.get(position).getPlaylistName());
                        i.putExtra("PlaylistImage", listModelList.get(position).getPlaylistImage());
                        i.putExtra("PlaylistType", listModelList.get(position).getCreated());
                        i.putExtra("Liked", "0");
                        ctx.startActivity(i);
                    }

                }
            });
            holder.binding.rlMainLayout.setOnClickListener(view -> {
                if (IsLock.equalsIgnoreCase("1")) {
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                    Intent i = new Intent(getActivity(), MembershipChangeActivity.class);
                    i.putExtra("ComeFrom", "Plan");
                    startActivity(i);
                } else if (IsLock.equalsIgnoreCase("2")) {
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                    BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
                } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                    holder.binding.ivLock.setVisibility(View.GONE);
                    if (MyDownloads.equalsIgnoreCase("1")) {
                        callMyPlaylistsFragment("0", listModelList.get(position).getPlaylistID(), listModelList.get(position).getPlaylistName(),
                                listModelList.get(position).getPlaylistImage(), MyDownloads, "Downloaded Playlists");
//                        getMedia(listModelList.get(position).getPlaylistID());
                       /* Intent i = new Intent(ctx, DownloadPlaylistActivity.class);
                        i.putExtra("New", "0");
                        i.putExtra("PlaylistID", listModelList.get(position).getPlaylistID());
                        i.putExtra("PlaylistName", listModelList.get(position).getPlaylistName());
                        i.putExtra("PlaylistImage", listModelList.get(position).getPlaylistImage());
                        i.putExtra("PlaylistImageDetails", listModelList.get(position).getPlaylistImageDetails());
                        i.putExtra("TotalAudio", listModelList.get(position).getTotalAudio());
                        i.putExtra("Totalhour", listModelList.get(position).getTotalhour());
                        i.putExtra("Totalminute", listModelList.get(position).getTotalminute());
                        i.putExtra("MyDownloads", "1");
                        ctx.startActivity(i);*/
                    } else {
                        callMyPlaylistsFragment("0", listModelList.get(position).getPlaylistID(), listModelList.get(position).getPlaylistName(),
                                listModelList.get(position).getPlaylistImage(), MyDownloads, screenView);
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            if (6 > listModelList.size()) {
                return listModelList.size();
            } else {
                return 6;
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