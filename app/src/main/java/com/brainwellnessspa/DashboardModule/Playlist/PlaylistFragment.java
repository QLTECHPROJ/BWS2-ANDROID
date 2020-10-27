package com.brainwellnessspa.DashboardModule.Playlist;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.DashboardModule.Models.ViewAllAudioListModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.DownloadModule.Activities.DownloadPlaylistActivity;
import com.brainwellnessspa.databinding.MainPlaylistLayoutBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Activities.MembershipChangeActivity;
import com.brainwellnessspa.DashboardModule.Models.CreatePlaylistModel;
import com.brainwellnessspa.DashboardModule.Models.MainPlayListModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.RoomDataBase.DownloadPlaylistDetails;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.FragmentPlaylistBinding;
import com.brainwellnessspa.databinding.MainAudioLayoutBinding;
import com.brainwellnessspa.databinding.PlaylistCustomLayoutBinding;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.ComeScreenAccount;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.player;
import static com.brainwellnessspa.DashboardModule.Search.SearchFragment.comefrom_search;
import static com.brainwellnessspa.DashboardModule.Audio.AudioFragment.IsLock;
import static com.brainwellnessspa.Utility.MusicService.isCompleteStop;
import static com.brainwellnessspa.Utility.MusicService.isMediaStart;
import static com.brainwellnessspa.Utility.MusicService.isPause;
import static com.brainwellnessspa.Utility.MusicService.isPrepare;
import static com.brainwellnessspa.Utility.MusicService.releasePlayer;
import static com.brainwellnessspa.Utility.MusicService.stopMedia;

public class PlaylistFragment extends Fragment {
    FragmentPlaylistBinding binding;
    String UserID, Check = "", AudioFlag;
    View view;
    List<DownloadPlaylistDetails> downloadPlaylistDetailsList;
    List<DownloadAudioDetails> playlistWiseAudioDetails = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist, container, false);
        view = binding.getRoot();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        ComeScreenAccount = 0;
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
        prepareData();
    }

    private void prepareData() {
        try {
            SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            AudioFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            if (!IsLock.equalsIgnoreCase("0") && (AudioFlag.equalsIgnoreCase("MainAudioList")
                    || AudioFlag.equalsIgnoreCase("ViewAllAudioList"))) {
                String audioFile = "";
                SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                Gson gson = new Gson();
                String json = shared.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gson));
                Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                }.getType();
                ArrayList<MainPlayModel> arrayList = gson.fromJson(json, type);

                if (arrayList.get(0).getAudioFile().equalsIgnoreCase("")) {
                    arrayList.remove(0);
                }
                audioFile = arrayList.get(0).getName();

                if (audioFile.equalsIgnoreCase("Hope") || audioFile.equalsIgnoreCase("Mindfulness")) {

                } else {
                    SharedPreferences sharedm = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorr = sharedm.edit();
                    editorr.remove(CONSTANTS.PREF_KEY_modelList);
                    editorr.remove(CONSTANTS.PREF_KEY_audioList);
                    editorr.remove(CONSTANTS.PREF_KEY_position);
                    editorr.remove(CONSTANTS.PREF_KEY_queuePlay);
                    editorr.remove(CONSTANTS.PREF_KEY_audioPlay);
                    editorr.remove(CONSTANTS.PREF_KEY_AudioFlag);
                    editorr.remove(CONSTANTS.PREF_KEY_PlaylistId);
                    editorr.remove(CONSTANTS.PREF_KEY_myPlaylist);
                    editorr.clear();
                    editorr.commit();
                    if(isMediaStart){
                        stopMedia();
                        releasePlayer();
                    }
                }

            } else if (!IsLock.equalsIgnoreCase("0") && !AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
                SharedPreferences sharedm = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editorr = sharedm.edit();
                editorr.remove(CONSTANTS.PREF_KEY_modelList);
                editorr.remove(CONSTANTS.PREF_KEY_audioList);
                editorr.remove(CONSTANTS.PREF_KEY_position);
                editorr.remove(CONSTANTS.PREF_KEY_queuePlay);
                editorr.remove(CONSTANTS.PREF_KEY_audioPlay);
                editorr.remove(CONSTANTS.PREF_KEY_AudioFlag);
                editorr.remove(CONSTANTS.PREF_KEY_PlaylistId);
                editorr.remove(CONSTANTS.PREF_KEY_myPlaylist);
                editorr.clear();
                editorr.commit();
                if(isMediaStart){
                    stopMedia();
                    releasePlayer();
                }
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
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            Call<MainPlayListModel> listCall = APIClient.getClient().getMainPlayLists(UserID);
            listCall.enqueue(new Callback<MainPlayListModel>() {
                @Override
                public void onResponse(Call<MainPlayListModel> call, Response<MainPlayListModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                        MainPlayListModel listModel = response.body();
                        binding.rlCreatePlaylist.setVisibility(View.VISIBLE);
                        downloadPlaylistDetailsList = GetPlaylistDetail(listModel.getResponseData());
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
                        detail.setPlaylistImageDetails(downloadPlaylistDetailsList.get(i).getPlaylistImageDetails());
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
            MainPlaylistLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.main_playlist_layout, parent, false);
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
                if (listModelList.get(position).getIsLock().equalsIgnoreCase("1")) {
                    binding.ivLock.setVisibility(View.VISIBLE);
                    Intent i = new Intent(getActivity(), MembershipChangeActivity.class);
                    i.putExtra("ComeFrom", "Plan");
                    startActivity(i);
                } else if (listModelList.get(position).getIsLock().equalsIgnoreCase("2")) {
                    binding.ivLock.setVisibility(View.VISIBLE);
                    BWSApplication.showToast("Please re-activate your membership plan", getActivity());
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

                } else if (listModelList.get(position).getView().equalsIgnoreCase(getString(R.string.Recommended_Playlist))) {
                    PlaylistAdapter adapter3 = new PlaylistAdapter(listModelList.get(position).getDetails(), getActivity(),
                            listModelList.get(position).getIsLock(), "0");
                    holder.binding.rvMainAudio.setAdapter(adapter3);
                } else if (listModelList.get(position).getView().equalsIgnoreCase(getString(R.string.populars))) {
                    PlaylistAdapter adapter4 = new PlaylistAdapter(listModelList.get(position).getDetails(), getActivity(),
                            listModelList.get(position).getIsLock(), "0");
                    holder.binding.rvMainAudio.setAdapter(adapter4);
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
            protected void onPostExecute(Void aVoid) {
                player = 1;
                if (isPrepare || isMediaStart || isPause) {
                    stopMedia();
                }
                isPause = false;
                isMediaStart = false;
                isPrepare = false;
                isCompleteStop = false;
                SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                Gson gson = new Gson();
                ArrayList<DownloadAudioDetails> listModelList2 = new ArrayList<>();
                DownloadAudioDetails  mainPlayModel = new DownloadAudioDetails();
                mainPlayModel.setID("0");
                mainPlayModel.setName("Disclaimer");
                mainPlayModel.setAudioFile("");
                mainPlayModel.setAudioDirection("The audio shall start playing after the disclaimer");
                mainPlayModel.setAudiomastercat("");
                mainPlayModel.setAudioSubCategory("");
                mainPlayModel.setImageFile("");
                mainPlayModel.setLike("");
                mainPlayModel.setDownload("");
                mainPlayModel.setAudioDuration("0:48");
                listModelList2.add(mainPlayModel);
                listModelList2.addAll(playlistWiseAudioDetails);

                String json = gson.toJson(listModelList2);
                editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                editor.putInt(CONSTANTS.PREF_KEY_position, 0);
                editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
                editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "Downloadlist");
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
            } else if (IsLock.equalsIgnoreCase("2")) {
                holder.binding.ivLock.setVisibility(View.VISIBLE);
            } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                holder.binding.ivLock.setVisibility(View.GONE);
            }

            holder.binding.rlMainLayout.setOnClickListener(view -> {
                if (IsLock.equalsIgnoreCase("1")) {
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                    Intent i = new Intent(getActivity(), MembershipChangeActivity.class);
                    i.putExtra("ComeFrom", "Plan");
                    startActivity(i);
                } else if (IsLock.equalsIgnoreCase("2")) {
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                    BWSApplication.showToast("Please re-activate your membership plan", ctx);
                } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                    holder.binding.ivLock.setVisibility(View.GONE);
                    if (MyDownloads.equalsIgnoreCase("1")) {
                        callMyPlaylistsFragment("0", listModelList.get(position).getPlaylistID(), listModelList.get(position).getPlaylistName(),
                                listModelList.get(position).getPlaylistImage(), MyDownloads);
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