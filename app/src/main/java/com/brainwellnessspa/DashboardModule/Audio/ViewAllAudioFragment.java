package com.brainwellnessspa.DashboardModule.Audio;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Activities.MembershipChangeActivity;
import com.brainwellnessspa.DashboardModule.Activities.AddPlaylistActivity;
import com.brainwellnessspa.DashboardModule.Activities.AudioPlayerActivity;
import com.brainwellnessspa.DashboardModule.Models.ViewAllAudioListModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.AudiolistCustomLayoutBinding;
import com.brainwellnessspa.databinding.FragmentViewAllAudioBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.audioClick;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.miniPlayer;
import static com.brainwellnessspa.DashboardModule.Audio.AudioFragment.IsLock;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.Utility.MusicService.isCompleteStop;
import static com.brainwellnessspa.Utility.MusicService.isMediaStart;
import static com.brainwellnessspa.Utility.MusicService.isPause;
import static com.brainwellnessspa.Utility.MusicService.isPrepare;
import static com.brainwellnessspa.DashboardModule.Playlist.MyPlaylistsFragment.disclaimerPlayed;
import static com.brainwellnessspa.Utility.MusicService.releasePlayer;
import static com.brainwellnessspa.Utility.MusicService.stopMedia;

import static com.brainwellnessspa.Services.GlobleInItExoPlayer.player;

public class ViewAllAudioFragment extends Fragment {
    public static boolean viewallAudio = false;
    public static int ComeFromAudioViewAll = 0;
    FragmentViewAllAudioBinding binding;
    String ID, Name, UserID, AudioFlag, Category;
    List<DownloadAudioDetails> audioList;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_all_audio, container, false);
        View view = binding.getRoot();

        context = getActivity();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        if (getArguments() != null) {
            ID = getArguments().getString("ID");
            Name = getArguments().getString("Name");
            Category = getArguments().getString("Category");
        }

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                callBack();
                return true;
            }
            return false;
        });
        binding.llBack.setOnClickListener(view1 -> callBack());
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
        binding.rvMainAudio.setLayoutManager(manager);
       /* if (Name.equalsIgnoreCase("My Downloads")) {
            audioList = new ArrayList<>();
            GetAllMedia(getActivity());
        } else {
            prepareData();
        }*/
        return view;
    }

    private void callBack() {
        Fragment audioFragment = new AudioFragment();
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
        refreshData();
        if (Name.equalsIgnoreCase("My Downloads")) {
            audioList = new ArrayList<>();
            GetAllMedia(getActivity());
        } else {
            prepareData();
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
                refreshData();
                binding.tvTitle.setText(Name);
                ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList = new ArrayList<>();
                for (int i = 0; i < audioList.size(); i++) {
                    ViewAllAudioListModel.ResponseData.Detail mainPlayModel = new ViewAllAudioListModel.ResponseData.Detail();

                    mainPlayModel.setID(audioList.get(i).getID());
                    mainPlayModel.setName(audioList.get(i).getName());
                    mainPlayModel.setAudioFile(audioList.get(i).getAudioFile());
                    mainPlayModel.setAudioDirection(audioList.get(i).getAudioDirection());
                    mainPlayModel.setAudiomastercat(audioList.get(i).getAudiomastercat());
                    mainPlayModel.setAudioSubCategory(audioList.get(i).getAudioSubCategory());
                    mainPlayModel.setImageFile(audioList.get(i).getImageFile());
                    mainPlayModel.setLike(audioList.get(i).getLike());
                    mainPlayModel.setDownload(audioList.get(i).getDownload());
                    mainPlayModel.setAudioDuration(audioList.get(i).getAudioDuration());
                    listModelList.add(mainPlayModel);
                }
                AudiolistAdapter adapter = new AudiolistAdapter(listModelList, IsLock);
                binding.rvMainAudio.setAdapter(adapter);
                super.onPostExecute(aVoid);
            }
        }
        GetTask st = new GetTask();
        st.execute();
        return audioList;
    }

    private void prepareData() {
        refreshData();
        if (BWSApplication.isNetworkConnected(getActivity())) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            Call<ViewAllAudioListModel> listCall = APIClient.getClient().getViewAllAudioLists(UserID, ID, Category);
            listCall.enqueue(new Callback<ViewAllAudioListModel>() {
                @Override
                public void onResponse(Call<ViewAllAudioListModel> call, Response<ViewAllAudioListModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                            ViewAllAudioListModel listModel = response.body();
                            if (Category.equalsIgnoreCase("")) {
                                binding.tvTitle.setText(listModel.getResponseData().getView());
                            } else {
                                binding.tvTitle.setText(Category);
                            }
                            AudiolistAdapter adapter = new AudiolistAdapter(listModel.getResponseData().getDetails(), listModel.getResponseData().getIsLock());
                            binding.rvMainAudio.setAdapter(adapter);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ViewAllAudioListModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }

    private void refreshData() {
        try {
            SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            AudioFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            SharedPreferences shared2 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
            String UnlockAudioLists = shared2.getString(CONSTANTS.PREF_KEY_UnLockAudiList, "");
            Gson gson1 = new Gson();
            Type type1 = new TypeToken<List<String>>() {
            }.getType();
            List<String> UnlockAudioList = gson1.fromJson(UnlockAudioLists, type1);
            if (!IsLock.equalsIgnoreCase("0") && (AudioFlag.equalsIgnoreCase("MainAudioList")
                    || AudioFlag.equalsIgnoreCase("ViewAllAudioList"))) {
                String audioID = "";
                SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                Gson gson = new Gson();
                String json = shared.getString(CONSTANTS.PREF_KEY_audioList, String.valueOf(gson));
                Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                }.getType();
                ArrayList<MainPlayModel> arrayList = gson.fromJson(json, type);

                if (arrayList.get(0).getAudioFile().equalsIgnoreCase("")) {
                    arrayList.remove(0);
                }
                audioID = arrayList.get(0).getID();

                if (UnlockAudioList.contains(audioID)) {
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
                    if (isMediaStart) {
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
                if (isMediaStart) {
                    stopMedia();
                    releasePlayer();
                }
            }
            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            if (!AudioFlag.equalsIgnoreCase("0")) {
                Fragment fragment = new MiniPlayerFragment();
                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .add(R.id.flContainer, fragment)
                        .commit();

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(4, 6, 4, 280);
                binding.llSpace.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(4, 6, 4, 50);
                binding.llSpace.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class AudiolistAdapter extends RecyclerView.Adapter<AudiolistAdapter.MyViewHolder> {
        String IsLock;
        private ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList;
        int index = -1;

        public AudiolistAdapter(ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList, String IsLock) {
            this.listModelList = listModelList;
            this.IsLock = IsLock;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            AudiolistCustomLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.audiolist_custom_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 0,
                    1, 1, 0.46f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.binding.tvAddToPlaylist.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.tvAddToPlaylist.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.tvPlaylistName.setText(listModelList.get(position).getName());
            Glide.with(getActivity()).load(listModelList.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

            if (IsLock.equalsIgnoreCase("1")) {
                if (listModelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                    holder.binding.ivLock.setVisibility(View.GONE);
                } else if (listModelList.get(position).getIsPlay().equalsIgnoreCase("0")
                        || listModelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                }
            } else if (IsLock.equalsIgnoreCase("2")) {
                if (listModelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                    holder.binding.ivLock.setVisibility(View.GONE);
                } else if (listModelList.get(position).getIsPlay().equalsIgnoreCase("0")
                        || listModelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                }
            } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                holder.binding.ivLock.setVisibility(View.GONE);
            }
            if (index == position) {
                holder.binding.tvAddToPlaylist.setVisibility(View.VISIBLE);
            } else
                holder.binding.tvAddToPlaylist.setVisibility(View.GONE);
            holder.binding.tvAddToPlaylist.setText("Add To Playlist");
            holder.binding.rlMainLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    holder.binding.tvAddToPlaylist.setVisibility(View.VISIBLE);
                    index = position;
                    notifyDataSetChanged();
                    return true;
                }
            });
            holder.binding.tvAddToPlaylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), AddPlaylistActivity.class);
                    i.putExtra("AudioId", listModelList.get(position).getID());
                    i.putExtra("PlaylistID", "");
                    startActivity(i);
                }
            });

            holder.binding.rlMainLayout.setOnClickListener(view -> {
                if (IsLock.equalsIgnoreCase("1")) {
                    if (listModelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                        holder.binding.ivLock.setVisibility(View.GONE);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(4, 6, 4, 280);
                        binding.llSpace.setLayoutParams(params);
                        if (!Name.equalsIgnoreCase(getString(R.string.top_categories))) {
                            callnewTrans(position, listModelList);
                        } else {
                            callTransFrag(position, listModelList);
                        }
                    } else if (listModelList.get(position).getIsPlay().equalsIgnoreCase("0")
                            || listModelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                        holder.binding.ivLock.setVisibility(View.VISIBLE);
                        Intent i = new Intent(getActivity(), MembershipChangeActivity.class);
                        i.putExtra("ComeFrom", "Plan");
                        startActivity(i);
                    }
                } else if (IsLock.equalsIgnoreCase("2")) {
                    if (listModelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                        holder.binding.ivLock.setVisibility(View.GONE);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(4, 6, 4, 280);
                        binding.llSpace.setLayoutParams(params);
                        if (!Name.equalsIgnoreCase(getString(R.string.top_categories))) {
                            callnewTrans(position, listModelList);
                        } else {
                            callTransFrag(position, listModelList);
                        }
                    } else if (listModelList.get(position).getIsPlay().equalsIgnoreCase("0")
                            || listModelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                        holder.binding.ivLock.setVisibility(View.VISIBLE);
                        BWSApplication.showToast("Please re-activate your membership plan", getActivity());
                    }
                } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                    holder.binding.ivLock.setVisibility(View.GONE);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(4, 6, 4, 280);
                    binding.llSpace.setLayoutParams(params);
                    if (!Name.equalsIgnoreCase(getString(R.string.top_categories))) {
                        callnewTrans(position, listModelList);
                    } else {
                        callTransFrag(position, listModelList);
                    }
                }
            });
        }


        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            AudiolistCustomLayoutBinding binding;

            public MyViewHolder(AudiolistCustomLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    private void callnewTrans(int position, ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList) {
        SharedPreferences shared = context.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
        boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
        String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        String MyPlaylist = shared.getString(CONSTANTS.PREF_KEY_myPlaylist, "");
        if (Name.equalsIgnoreCase("My Downloads")) {
            if (audioPlay && AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
                if (isDisclaimer == 1) {
                    BWSApplication.showToast("The audio shall start playing after the disclaimer", context);
                } else {
                    callTransFrag(position, listModelList);
                }
            } else {
                isDisclaimer = 0;
                disclaimerPlayed = 0;
                ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList2 = new ArrayList<>();
                ViewAllAudioListModel.ResponseData.Detail mainPlayModel = new ViewAllAudioListModel.ResponseData.Detail();
                mainPlayModel.setID("0");
                mainPlayModel.setName("Disclaimer");
                mainPlayModel.setAudioFile("");
                mainPlayModel.setAudioDirection("The audio shall start playing after the disclaimer");
                mainPlayModel.setAudiomastercat("");
                mainPlayModel.setAudioSubCategory("");
                mainPlayModel.setImageFile("");
                mainPlayModel.setLike("");
                mainPlayModel.setDownload("");
                mainPlayModel.setAudioDuration("00:48");
                listModelList2.addAll(listModelList);
                listModelList2.add(position, mainPlayModel);
                callTransFrag(position, listModelList2);
            }
        } else {
            if (audioPlay && (AudioFlag.equalsIgnoreCase("MainAudioList") || AudioFlag.equalsIgnoreCase("ViewAllAudioList")) && MyPlaylist.equalsIgnoreCase(Name)) {
                if (isDisclaimer == 1) {
                    BWSApplication.showToast("The audio shall start playing after the disclaimer", context);
                } else {
                    callTransFrag(position, listModelList);
                }
            } else {
                isDisclaimer = 0;
                disclaimerPlayed = 0;
                ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList2 = new ArrayList<>();
                ViewAllAudioListModel.ResponseData.Detail mainPlayModel = new ViewAllAudioListModel.ResponseData.Detail();
                mainPlayModel.setID("0");
                mainPlayModel.setName("Disclaimer");
                mainPlayModel.setAudioFile("");
                mainPlayModel.setAudioDirection("The audio shall start playing after the disclaimer");
                mainPlayModel.setAudiomastercat("");
                mainPlayModel.setAudioSubCategory("");
                mainPlayModel.setImageFile("");
                mainPlayModel.setLike("");
                mainPlayModel.setDownload("");
                mainPlayModel.setAudioDuration("00:48");
                listModelList2.addAll(listModelList);
                listModelList2.add(position, mainPlayModel);
                callTransFrag(position, listModelList2);
            }
        }
    }

    private void callTransFrag(int position, ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList) {
        try {
            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String json = "";
            ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList2 = new ArrayList<>();
            ViewAllAudioListModel.ResponseData.Detail mainPlayModel = new ViewAllAudioListModel.ResponseData.Detail();
            if (Name.equalsIgnoreCase(getString(R.string.top_categories))) {
                SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                boolean audioPlay = shared1.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                AudioFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String catName = shared1.getString(CONSTANTS.PREF_KEY_Cat_Name, "");
                if (audioPlay && AudioFlag.equalsIgnoreCase("TopCategories") && catName.equalsIgnoreCase(Category)) {
                    if (isDisclaimer == 1) {
                        BWSApplication.showToast("The audio shall start playing after the disclaimer", context);
                    } else {
                        listModelList2 = new ArrayList<>();
                        listModelList2.addAll(listModelList);
                        json = gson.toJson(listModelList2);
                        editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "TopCategories");
                        editor.putString(CONSTANTS.PREF_KEY_Cat_Name, Category);
                        editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                        editor.putInt(CONSTANTS.PREF_KEY_position, position);
                        editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                        editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                        editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                        editor.putString(CONSTANTS.PREF_KEY_myPlaylist, Name);
                        editor.commit();
                        openMyFragment();
                    }
                } else {
                    listModelList2 = new ArrayList<>();
                    isDisclaimer = 0;
                    disclaimerPlayed = 0;
                    mainPlayModel.setID("0");
                    mainPlayModel.setName("Disclaimer");
                    mainPlayModel.setAudioFile("");
                    mainPlayModel.setAudioDirection("The audio shall start playing after the disclaimer");
                    mainPlayModel.setAudiomastercat("");
                    mainPlayModel.setAudioSubCategory("");
                    mainPlayModel.setImageFile("");
                    mainPlayModel.setLike("");
                    mainPlayModel.setDownload("");
                    mainPlayModel.setAudioDuration("00:48");
                    if (position != 0) {
                        listModelList2.addAll(listModelList);
                        listModelList2.add(position, mainPlayModel);
                    } else {
                        listModelList2.add(mainPlayModel);
                        listModelList2.addAll(listModelList);
                    }
                    json = gson.toJson(listModelList2);
                    editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "TopCategories");
                    editor.putString(CONSTANTS.PREF_KEY_Cat_Name, Category);
                    editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                    editor.putInt(CONSTANTS.PREF_KEY_position, position);
                    editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                    editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                    editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                    editor.putString(CONSTANTS.PREF_KEY_myPlaylist, Name);
                    editor.commit();
                    openMyFragment();
                }
            } else {
                json = gson.toJson(listModelList);
                if (Name.equalsIgnoreCase("My Downloads")) {
                    editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "DownloadListAudio");
                } else {
                    editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "ViewAllAudioList");
                }
                editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                editor.putInt(CONSTANTS.PREF_KEY_position, position);
                editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                editor.putString(CONSTANTS.PREF_KEY_myPlaylist, Name);
                editor.commit();
//                openMyFragment();
                miniPlayer = 1;
                if (isPrepare || isMediaStart || isPause) {
                    stopMedia();
                }
                isPause = false;
                isMediaStart = false;
                isPrepare = false;
                isCompleteStop = false;
                Intent i = new Intent(getActivity(), AudioPlayerActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                getActivity().startActivity(i);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openMyFragment() {
        miniPlayer = 1;
        audioClick = true;
        if(player!=null){
            player.release();
        }
        if (isPrepare || isMediaStart || isPause) {
            stopMedia();
        }
        isPause = false;
        isMediaStart = false;
        isPrepare = false;
        isCompleteStop = false;
        Fragment fragment = new MiniPlayerFragment();
        FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
        fragmentManager1.beginTransaction()
                .add(R.id.flContainer, fragment)
                .commit();
    }

   /* public class TopAudiolistAdapter extends RecyclerView.Adapter<TopAudiolistAdapter.MyViewHolder> {
        String IsLock;
        private ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList;

        public TopAudiolistAdapter(ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList, String IsLock) {
            this.listModelList = listModelList;
            this.IsLock = IsLock;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            AudiolistCustomLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.audiolist_custom_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 0,
                    1, 1, 0.46f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);

            holder.binding.tvPlaylistName.setText(listModelList.get(position).getName());
            Glide.with(getActivity()).load(listModelList.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

            if (IsLock.equalsIgnoreCase("1")) {
                if (listModelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                    holder.binding.ivLock.setVisibility(View.GONE);
                } else if (listModelList.get(position).getIsPlay().equalsIgnoreCase("0")
                        || listModelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                }
            } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                holder.binding.ivLock.setVisibility(View.GONE);
            }

            holder.binding.rlMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (IsLock.equalsIgnoreCase("1")) {
                        if (listModelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                            holder.binding.ivLock.setVisibility(View.GONE);
                        } else if (listModelList.get(position).getIsPlay().equalsIgnoreCase("0")
                                || listModelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                            holder.binding.ivLock.setVisibility(View.VISIBLE);
                            Intent i = new Intent(getActivity(), MembershipChangeActivity.class);
                            i.putExtra("ComeFrom", "Plan");
                            startActivity(i);
                        }
                    } else if (IsLock.equalsIgnoreCase("2")) {
                        if (listModelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                            holder.binding.ivLock.setVisibility(View.GONE);
                        } else if (listModelList.get(position).getIsPlay().equalsIgnoreCase("0")
                                || listModelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                            holder.binding.ivLock.setVisibility(View.VISIBLE);
                            BWSApplication.showToast("Please re-activate your membership plan", getActivity());
                        }
                    } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                        holder.binding.ivLock.setVisibility(View.GONE);

                        callTransFrag(position,listModelList);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            AudiolistCustomLayoutBinding binding;

            public MyViewHolder(AudiolistCustomLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }*/
}