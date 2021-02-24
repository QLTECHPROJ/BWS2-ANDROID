package com.brainwellnessspa.DashboardModule.Audio;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.brainwellnessspa.DashboardModule.Models.MainAudioModel;
import com.brainwellnessspa.DashboardModule.Models.SegmentAudio;
import com.brainwellnessspa.DashboardModule.Models.ViewAllAudioListModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.Services.GlobalInitExoPlayer;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.AudiolistCustomLayoutBinding;
import com.brainwellnessspa.databinding.FragmentViewAllAudioBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
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

import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.audioClick;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.miniPlayer;
import static com.brainwellnessspa.DashboardModule.Audio.AudioFragment.IsLock;

import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.isDisclaimer;


import static com.brainwellnessspa.Services.GlobalInitExoPlayer.callNewPlayerRelease;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.player;

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
        refreshData();
        if (Name.equalsIgnoreCase("My Downloads")) {
            audioList = new ArrayList<>();
//            GetAllMedia(getActivity());
            callObserverMethod();
        } else {
            prepareData();
        }
        super.onResume();
    }

    private void callObserverMethod() {
        DatabaseClient
                .getInstance(getActivity())
                .getaudioDatabase()
                .taskDao()
                .geAllDataz("").observe(getActivity(), audioList -> {
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
            ArrayList<SegmentAudio> section = new ArrayList<>();
            for (int i = 0; i < audioList.size(); i++) {
                SegmentAudio e = new SegmentAudio();
                e.setAudioId(audioList.get(i).getID());
                e.setAudioName(audioList.get(i).getName());
                e.setMasterCategory(audioList.get(i).getAudiomastercat());
                e.setSubCategory(audioList.get(i).getAudioSubCategory());
                e.setAudioDuration(audioList.get(i).getAudioDirection());
                section.add(e);
            }
            Properties p = new Properties();
            p.putValue("userId", UserID);
            Gson gson = new Gson();
            p.putValue("audios", gson.toJson(section));
            p.putValue("source", Name);
            BWSApplication.addToSegment("Explore ViewAll Screen Viewed", p, CONSTANTS.screen);
            AudiolistAdapter adapter = new AudiolistAdapter(listModelList, IsLock);
            binding.rvMainAudio.setAdapter(adapter);
        });
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
                            ArrayList<SegmentAudio> section = new ArrayList<>();
                            for (int i = 0; i < listModel.getResponseData().getDetails().size(); i++) {
                                SegmentAudio e = new SegmentAudio();
                                e.setAudioId(listModel.getResponseData().getDetails().get(i).getID());
                                e.setAudioName(listModel.getResponseData().getDetails().get(i).getName());
                                e.setMasterCategory(listModel.getResponseData().getDetails().get(i).getAudiomastercat());
                                e.setSubCategory(listModel.getResponseData().getDetails().get(i).getAudioSubCategory());
                                e.setAudioDuration(listModel.getResponseData().getDetails().get(i).getAudioDirection());
                                section.add(e);
                            }
                            Properties p = new Properties();
                            p.putValue("userId", UserID);
                            Gson gson = new Gson();
                            p.putValue("audios", gson.toJson(section));
                            if (Name.equalsIgnoreCase(getString(R.string.recently_played))) {
                                p.putValue("source", Name);
                            } else if (Name.equalsIgnoreCase(getString(R.string.Library))) {
                                p.putValue("source", Name);
                            } else if (Name.equalsIgnoreCase(getString(R.string.get_inspired))) {
                                p.putValue("source", Name);
                            } else if (Name.equalsIgnoreCase(getString(R.string.popular))) {
                                p.putValue("source", Name);
                            } else if (Name.equalsIgnoreCase(getString(R.string.top_categories))) {
                                p.putValue("categoryName", Category);
                                p.putValue("source", Name);
                            }
                            BWSApplication.addToSegment("Explore ViewAll Screen Viewed", p, CONSTANTS.screen);
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
            GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
            globalInitExoPlayer.UpdateMiniPlayer(getActivity());
            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
            if (!AudioFlag.equalsIgnoreCase("0")) {
                openOnlyFragment();
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
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(12))).priority(Priority.HIGH)
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
                    if (IsLock.equalsIgnoreCase("1")) {
                        Intent i = new Intent(getActivity(), MembershipChangeActivity.class);
                        i.putExtra("ComeFrom", "Plan");
                        startActivity(i);
                    } else if (IsLock.equalsIgnoreCase("2")) {
                        BWSApplication.showToast(getString(R.string.reactive_plan), getActivity());
                    } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                        Intent i = new Intent(getActivity(), AddPlaylistActivity.class);
                        i.putExtra("AudioId", listModelList.get(position).getID());
                        i.putExtra("ScreenView", "Audio View All Screen");
                        i.putExtra("PlaylistID", "");
                        i.putExtra("PlaylistName", "");
                        i.putExtra("PlaylistImage", "");
                        i.putExtra("PlaylistType", "");
                        i.putExtra("Liked", "0");
                        startActivity(i);
                    }
                }
            });

            holder.binding.rlMainLayout.setOnClickListener(view -> {
                if (IsLock.equalsIgnoreCase("1")) {
                    if (listModelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(4, 6, 4, 280);
                        binding.llSpace.setLayoutParams(params);
                        if (!Name.equalsIgnoreCase(getString(R.string.top_categories))) {
                            callnewTrans(position, listModelList);
                        } else {
                            callTransFrag(position, listModelList,true);
                        }
                    } else if (listModelList.get(position).getIsPlay().equalsIgnoreCase("0")
                            || listModelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                        Intent i = new Intent(getActivity(), MembershipChangeActivity.class);
                        i.putExtra("ComeFrom", "Plan");
                        startActivity(i);
                    }
                } else if (IsLock.equalsIgnoreCase("2")) {
                    if (listModelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(4, 6, 4, 280);
                        binding.llSpace.setLayoutParams(params);
                        if (!Name.equalsIgnoreCase(getString(R.string.top_categories))) {
                            callnewTrans(position, listModelList);
                        } else {
                            callTransFrag(position, listModelList,true);
                        }
                    } else if (listModelList.get(position).getIsPlay().equalsIgnoreCase("0")
                            || listModelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                        BWSApplication.showToast(getString(R.string.reactive_plan), getActivity());
                    }
                } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(4, 6, 4, 280);
                    binding.llSpace.setLayoutParams(params);
                    if (!Name.equalsIgnoreCase(getString(R.string.top_categories))) {
                        callnewTrans(position, listModelList);
                    } else {
                        callTransFrag(position, listModelList,true);
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
        SharedPreferences shared = context.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
        String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        String MyPlaylist = shared.getString(CONSTANTS.PREF_KEY_myPlaylist, "");
        int positionSaved = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        String IsPlayDisclimer = (shared1.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1"));
        if (Name.equalsIgnoreCase("My Downloads")) {
            if (audioPlay && AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
                if (isDisclaimer == 1) {
                    if (player != null) {
                        if (!player.getPlayWhenReady()) {
                            player.setPlayWhenReady(true);
                        }
                    } else {
                        audioClick = true;
                        miniPlayer = 1;
                    }
                    Intent i = new Intent(getActivity(), AudioPlayerActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    getActivity().startActivity(i);
                    getActivity().overridePendingTransition(0, 0);
                    BWSApplication.showToast("The audio shall start playing after the disclaimer", context);
                } else {
                    if (player != null) {
                        if(position != positionSaved) {
                            player.seekTo(position, 0);
                            player.setPlayWhenReady(true);
                            miniPlayer = 1;
                            SharedPreferences sharedxx = context.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedxx.edit();
                            editor.putInt(CONSTANTS.PREF_KEY_position, position);
                            editor.commit();
                        }
                        Intent i = new Intent(getActivity(), AudioPlayerActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        getActivity().startActivity(i);
                        getActivity().overridePendingTransition(0, 0);
                    } else {
                        callTransFrag(position, listModelList,true);
                    }
                }
            } else {
                ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList2 = new ArrayList<>();
                listModelList2.addAll(listModelList);
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
                boolean audioc= true;
                if(isDisclaimer == 1){
                    if (player != null) {
                        player.setPlayWhenReady(true);
                        audioc = false;
                        listModelList2.add(position, mainPlayModel);
                    }else{
                        isDisclaimer = 0;
                        if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                            audioc = true;
                            listModelList2.add(position, mainPlayModel);
                        }
                    }
                }else {
                    isDisclaimer = 0;
                    if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                        audioc = true;
                        listModelList2.add(position, mainPlayModel);
                    }
                }
                callTransFrag(position, listModelList2,audioc);
            }
        } else {
            if (audioPlay && (AudioFlag.equalsIgnoreCase("MainAudioList") || AudioFlag.equalsIgnoreCase("ViewAllAudioList")) && MyPlaylist.equalsIgnoreCase(Name)) {
                if (isDisclaimer == 1) {
                    if (player != null) {
                        if (!player.getPlayWhenReady()) {
                            player.setPlayWhenReady(true);
                        }
                    } else {
                        audioClick = true;
                        miniPlayer = 1;
                    }
                    Intent i = new Intent(getActivity(), AudioPlayerActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    getActivity().startActivity(i);
                    getActivity().overridePendingTransition(0, 0);
                    BWSApplication.showToast("The audio shall start playing after the disclaimer", context);
                } else {
                    if( MyPlaylist.equalsIgnoreCase(getString(R.string.recently_played))){
                        ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList2 = new ArrayList<>();
                        if (!IsLock.equalsIgnoreCase("0")) {
                            SharedPreferences shared2 = context.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                            String UnlockAudioLists = shared2.getString(CONSTANTS.PREF_KEY_UnLockAudiList, "");
                            Gson gson1 = new Gson();
                            Type type1 = new TypeToken<List<String>>() {
                            }.getType();
                            List<String> UnlockAudioList = gson1.fromJson(UnlockAudioLists, type1);
                            int size = listModelList.size();
                            for (int i = 0; i < size; i++) {
                                if (UnlockAudioList.contains(listModelList.get(i).getID())) {
                                    listModelList2.add(listModelList.get(i));
                                }
                            }
                            if(position<listModelList2.size()){
                                position = position;
                            }else {
                                position = 0;
                            }
                        } else {
                            listModelList2.addAll(listModelList);
                        }
                        callTransFrag(position, listModelList2, true);
                    }else {
                        if (player != null) {
                            if (position != positionSaved) {
                                player.seekTo(position, 0);
                                player.setPlayWhenReady(true);
                                miniPlayer = 1;
                                SharedPreferences sharedxx = context.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedxx.edit();
                                editor.putInt(CONSTANTS.PREF_KEY_position, position);
                                editor.commit();
                            }
                            Intent i = new Intent(getActivity(), AudioPlayerActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            getActivity().startActivity(i);
                            getActivity().overridePendingTransition(0, 0);
                        } else {
                            ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList2 = new ArrayList<>();
                            if (!IsLock.equalsIgnoreCase("0")) {
                                SharedPreferences shared2 = context.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                                String UnlockAudioLists = shared2.getString(CONSTANTS.PREF_KEY_UnLockAudiList, "");
                                Gson gson1 = new Gson();
                                Type type1 = new TypeToken<List<String>>() {
                                }.getType();
                                List<String> UnlockAudioList = gson1.fromJson(UnlockAudioLists, type1);
                                int size = listModelList.size();
                                for (int i = 0; i < size; i++) {
                                    if (UnlockAudioList.contains(listModelList.get(i).getID())) {
                                        listModelList2.add(listModelList.get(i));
                                    }
                                }
                                if(position<listModelList2.size()){
                                    position = position;
                                }else {
                                    position = 0;
                                }
                            } else {
                                listModelList2.addAll(listModelList);
                            }
                            callTransFrag(position, listModelList2, true);
                        }
                    }
                }
            } else {
                ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList2 = new ArrayList<>();
                if (!IsLock.equalsIgnoreCase("0")) {
                    SharedPreferences shared2 = context.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                    String UnlockAudioLists = shared2.getString(CONSTANTS.PREF_KEY_UnLockAudiList, "");
                    Gson gson1 = new Gson();
                    Type type1 = new TypeToken<List<String>>() {
                    }.getType();
                    List<String> UnlockAudioList = gson1.fromJson(UnlockAudioLists, type1);
                    int size = listModelList.size();
                    for (int i = 0; i < size; i++) {
                        if (UnlockAudioList.contains(listModelList.get(i).getID())) {
                            listModelList2.add(listModelList.get(i));
                        }
                    }
                    if(position<listModelList2.size()){
                        position = position;
                    }else {
                        position = 0;
                    }
                } else {
                    listModelList2.addAll(listModelList);
                }

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
                boolean audioc= true;
                if(isDisclaimer == 1){
                    if (player != null) {
                        player.setPlayWhenReady(true);
                        audioc = false;
                        listModelList2.add(position, mainPlayModel);
                    }else{
                        isDisclaimer = 0;
                        if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                            audioc = true;
                            listModelList2.add(position, mainPlayModel);
                        }
                    }
                }else {
                    isDisclaimer = 0;
                    if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                     audioc = true;
                        listModelList2.add(position, mainPlayModel);
                    }
                }
                callTransFrag(position, listModelList2,audioc);
            }
        }
    }

    private void callTransFrag(int position, ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList,boolean audioc) {
        try {
            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String json = "";
            ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList2 = new ArrayList<>();
            ViewAllAudioListModel.ResponseData.Detail mainPlayModel = new ViewAllAudioListModel.ResponseData.Detail();
            if (Name.equalsIgnoreCase(getString(R.string.top_categories))) {
                SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                boolean audioPlay = shared1.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                AudioFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String catName = shared1.getString(CONSTANTS.PREF_KEY_Cat_Name, "");
                int positionSaved = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
                SharedPreferences shared11 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                String IsPlayDisclimer = (shared11.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1"));
                if (audioPlay && AudioFlag.equalsIgnoreCase("TopCategories") && catName.equalsIgnoreCase(Category)) {
                    if (isDisclaimer == 1) {
                        if (player != null) {
                            if (!player.getPlayWhenReady()) {
                                player.setPlayWhenReady(true);
                            }
                        } else {
                            BWSApplication.showToast("The audio shall start playing after the disclaimer", context);
                        }
                        openMyFragment(true);
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
                        if (player != null) {
                            if(position != positionSaved) {
                            player.seekTo(position, 0);
                            player.setPlayWhenReady(true);
                            miniPlayer = 1;
                            SharedPreferences sharedxx = context.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editord = sharedxx.edit();
                            editord.putInt(CONSTANTS.PREF_KEY_position, position);
                            editord.commit();}
                            openOnlyFragment();
                        } else {
                            openMyFragment(true);
                        }
                    }
                } else {
                    listModelList2 = new ArrayList<>();
                    listModelList2.addAll(listModelList);
                    mainPlayModel.setID("0");
                    mainPlayModel.setName("Disclaimer");
                    mainPlayModel.setAudioFile("");
                    mainPlayModel.setAudioDirection("The audio shall start playing after the disclaimer");
                    mainPlayModel.setAudiomastercat("");
                    mainPlayModel.setAudioSubCategory("");
                    mainPlayModel.setImageFile("");
                    mainPlayModel.setLike("");
                    mainPlayModel.setDownload("");
                    if(isDisclaimer == 1){
                        if (player != null) {
                            player.setPlayWhenReady(true);
                            audioc = false;
                            listModelList2.add(position, mainPlayModel);
                        }else{
                            isDisclaimer = 0;
                            if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                                audioc = true;
                                listModelList2.add(position, mainPlayModel);
                            }
                        }
                    }else {
                        isDisclaimer = 0;
                        if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                            audioc = true;
                            listModelList2.add(position, mainPlayModel);
                        }
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
                    openMyFragment(audioc);
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
                audioClick = true;
                callNewPlayerRelease();

                Intent i = new Intent(getActivity(), AudioPlayerActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                getActivity().startActivity(i);
                getActivity().overridePendingTransition(0, 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openOnlyFragment() {

        Fragment fragment = new MiniPlayerFragment();
        FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
        fragmentManager1.beginTransaction()
                .add(R.id.flContainer, fragment)
                .commit();
    }

    private void openMyFragment(boolean audioc) {
        miniPlayer = 1;
        audioClick = audioc;
        if(audioc) {
            callNewPlayerRelease();
        }
        openOnlyFragment();
    }
}