package com.brainwellnessspa.dashboardModule.manage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardOldModule.Models.SegmentAudio;
import com.brainwellnessspa.DashboardOldModule.Models.ViewAllAudioListModel;
import com.brainwellnessspa.DashboardOldModule.TransparentPlayer.Models.MainPlayModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.DatabaseClient;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.Utility.APINewClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.dashboardModule.activities.AddPlaylistActivity;
import com.brainwellnessspa.dashboardModule.activities.MyPlayerActivity;
import com.brainwellnessspa.dashboardModule.models.HomeScreenModel;
import com.brainwellnessspa.dashboardModule.models.PlaylistDetailsModel;
import com.brainwellnessspa.databinding.AudiolistCustomLayoutBinding;
import com.brainwellnessspa.databinding.FragmentViewAllAudioBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.segment.analytics.Properties;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.DashboardOldModule.Activities.DashboardActivity.audioClick;
import static com.brainwellnessspa.DashboardOldModule.TransparentPlayer.Fragments.MiniPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.callNewPlayerRelease;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.player;

public class ViewAllAudioFragment extends Fragment {
    FragmentViewAllAudioBinding binding;
    String ID, Name, userId, Category, coUserId, UserName;
    List<DownloadAudioDetails> audioList;
    Context context;
    Activity activity;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        activity = getActivity();
        SharedPreferences shared1 =
                getActivity().getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "");
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "");
        UserName = shared1.getString(CONSTANTS.PREFE_ACCESS_NAME, "");
        if (getArguments() != null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_all_audio, container, false);
            ID = getArguments().getString("ID");
            Name = getArguments().getString("Name");
            Category = getArguments().getString("Category");
        }
        View view = binding.getRoot();
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(
                (v, keyCode, event) -> {
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

        return view;
    }

    private void callBack() {
        Fragment audioFragment = new ManageFragment();
        FragmentManager fragmentManager1 = requireActivity().getSupportFragmentManager();
        fragmentManager1.beginTransaction().replace(R.id.flContainer, audioFragment).commit();
        Bundle bundle = new Bundle();
        audioFragment.setArguments(bundle);
    }

    @Override
    public void onResume() {
        //        refreshData();
        if (Name.equalsIgnoreCase("My Downloads")) {
            audioList = new ArrayList<>();
            callObserverMethod();
        } else {
            prepareData();
        }
        super.onResume();
    }

    private void callObserverMethod() {
        DatabaseClient.getInstance(getActivity())
                .getaudioDatabase()
                .taskDao()
                .geAllDataz("")
                .observe(
                        requireActivity(),
                        audioList -> {
                            //            refreshData();
                            binding.tvTitle.setText(Name);
                            ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList =
                                    new ArrayList<>();
                            for (int i = 0; i < audioList.size(); i++) {
                                ViewAllAudioListModel.ResponseData.Detail mainPlayModel =
                                        new ViewAllAudioListModel.ResponseData.Detail();

                                mainPlayModel.setID(audioList.get(i).getID());
                                mainPlayModel.setName(audioList.get(i).getName());
                                mainPlayModel.setAudioFile(audioList.get(i).getAudioFile());
                                mainPlayModel.setAudioDirection(audioList.get(i).getAudioDirection());
                                mainPlayModel.setAudiomastercat(audioList.get(i).getAudiomastercat());
                                mainPlayModel.setAudioSubCategory(audioList.get(i).getAudioSubCategory());
                                mainPlayModel.setImageFile(audioList.get(i).getImageFile());
                                mainPlayModel.setAudioDuration(audioList.get(i).getAudioDuration());
                                listModelList.add(mainPlayModel);
                            }

                            AudiolistAdapter adapter = new AudiolistAdapter(listModelList);
                            binding.rvMainAudio.setAdapter(adapter);
                        });
    }

    private void prepareData() {
        //        refreshData();
        if (BWSApplication.isNetworkConnected(getActivity())) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            Call<ViewAllAudioListModel> listCall =
                    APINewClient.getClient().getViewAllAudioLists(coUserId, ID, Category);
            listCall.enqueue(
                    new Callback<ViewAllAudioListModel>() {
                        @Override
                        public void onResponse(
                                @NotNull Call<ViewAllAudioListModel> call,
                                @NotNull Response<ViewAllAudioListModel> response) {
                            try {
                                if (response.isSuccessful()) {
                                    BWSApplication.hideProgressBar(
                                            binding.progressBar, binding.progressBarHolder, getActivity());
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
                                        e.setMasterCategory(
                                                listModel.getResponseData().getDetails().get(i).getAudiomastercat());
                                        e.setSubCategory(
                                                listModel.getResponseData().getDetails().get(i).getAudioSubCategory());
                                        e.setAudioDuration(
                                                listModel.getResponseData().getDetails().get(i).getAudioDirection());
                                        section.add(e);
                                    }
                                    Properties p = new Properties();
                                    p.putValue("userId", userId);
                                    p.putValue("coUserId", coUserId);
                                    Gson gson = new Gson();
                                    p.putValue("audios", gson.toJson(section));
                                    if (Name.equalsIgnoreCase(getString(R.string.top_categories))) {
                                        p.putValue("categoryName", Category);
                                    }
                                    p.putValue("source", Name);
                                    BWSApplication.addToSegment("Audio ViewAll Screen Viewed", p, CONSTANTS.screen);
                                    AudiolistAdapter adapter =
                                            new AudiolistAdapter(listModel.getResponseData().getDetails());
                                    binding.rvMainAudio.setAdapter(adapter);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<ViewAllAudioListModel> call, @NotNull Throwable t) {
                            BWSApplication.hideProgressBar(
                                    binding.progressBar, binding.progressBarHolder, getActivity());
                        }
                    });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }

  /*    private void refreshData() {
      try {
          GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
          globalInitExoPlayer.UpdateMiniPlayer(getActivity());
          SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
          AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
          if (!AudioFlag.equalsIgnoreCase("0")) {
              openOnlyFragment();
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
  }*/

    public class AudiolistAdapter extends RecyclerView.Adapter<AudiolistAdapter.MyViewHolder> {
        private final ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList;
        int index = -1;

        public AudiolistAdapter(ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList) {
            this.listModelList = listModelList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            AudiolistCustomLayoutBinding v =
                    DataBindingUtil.inflate(
                            LayoutInflater.from(parent.getContext()),
                            R.layout.audiolist_custom_layout,
                            parent,
                            false);
            return new MyViewHolder(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 0, 1, 1, 0.46f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height =
                    (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width =
                    (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.binding.tvAddToPlaylist.getLayoutParams().height =
                    (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.tvAddToPlaylist.getLayoutParams().width =
                    (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.rlMainLayout.getLayoutParams().height =
                    (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.rlMainLayout.getLayoutParams().width =
                    (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.tvAudioName.setText(listModelList.get(position).getName());
            Glide.with(requireActivity())
                    .load(listModelList.get(position).getImageFile())
                    .thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(38)))
                    .priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .into(holder.binding.ivRestaurantImage);

            holder.binding.llMore.setVisibility(View.VISIBLE);
            holder.binding.llMore.setOnClickListener(
                    v ->
                            BWSApplication.callAudioDetails(
                                    listModelList.get(position).getID(),
                                    context,
                                    getActivity(),
                                    coUserId,
                                    "viewAllAudioList",
                                    new ArrayList<DownloadAudioDetails>(),
                                    listModelList,
                                    new ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>(),
                                    new ArrayList<MainPlayModel>(),
                                    position));
      /*            if (IsLock.equalsIgnoreCase("1")) {
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
      */
            if (index == position) {
                holder.binding.tvAddToPlaylist.setVisibility(View.VISIBLE);
            } else holder.binding.tvAddToPlaylist.setVisibility(View.GONE);
            holder.binding.tvAddToPlaylist.setText("Add To Playlist");
            holder.binding.rlMainLayout.setOnLongClickListener(
                    v -> {
                        holder.binding.tvAddToPlaylist.setVisibility(View.VISIBLE);
                        index = position;
                        notifyDataSetChanged();
                        return true;
                    });

            holder.binding.tvAddToPlaylist.setOnClickListener(
                    view -> {
                        Intent i = new Intent(getActivity(), AddPlaylistActivity.class);
                        i.putExtra("AudioId", listModelList.get(position).getID());
                        i.putExtra("ScreenView", "Audio View All Screen");
                        i.putExtra("PlaylistID", "");
                        i.putExtra("PlaylistName", "");
                        i.putExtra("PlaylistImage", "");
                        i.putExtra("PlaylistType", "");
                        i.putExtra("Liked", "0");
                        startActivity(i);
                    });
            holder.binding.rlMainLayout.setOnClickListener(view -> callMainTransFrag(position));
        }

        public void callMainTransFrag(int position) {
            try {
                SharedPreferences shared1 =
                        context.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                String AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
                String MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
                //                String PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "");
                int PlayerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                if (Name.equalsIgnoreCase("My Downloads")) {
                    if (AudioPlayerFlag.equalsIgnoreCase("DownloadListAudio")) {
                        if (isDisclaimer == 1) {
                            if (player != null) {
                                if (!player.getPlayWhenReady()) {
                                    player.setPlayWhenReady(true);
                                }
                            } else {
                                audioClick = true;
                            }
                            callMyPlayer();
                            BWSApplication.showToast(
                                    "The audio shall start playing after the disclaimer", activity);
                        } else {
                            if (player != null) {
                                if (position != PlayerPosition) {
                                    player.seekTo(position, 0);
                                    player.setPlayWhenReady(true);
                                    SharedPreferences sharedxx =
                                            context.getSharedPreferences(
                                                    CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedxx.edit();
                                    editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
                                    editor.apply();
                                }
                                callMyPlayer();
                            } else {
                                callPlayer(position, listModelList, true);
                            }
                        }
                    } else {
                        ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList2 = new ArrayList<>();

                        listModelList2.addAll(listModelList);
                        Gson gson = new Gson();
                        SharedPreferences shared12 =
                                context.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                        String IsPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1");
                        String DisclimerJson =
                                shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString());
                        Type type = new TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio>() {
                        }.getType();
                        HomeScreenModel.ResponseData.DisclaimerAudio arrayList =
                                gson.fromJson(DisclimerJson, type);
                        ViewAllAudioListModel.ResponseData.Detail mainPlayModel =
                                new ViewAllAudioListModel.ResponseData.Detail();
                        mainPlayModel.setID(arrayList.getId());
                        mainPlayModel.setName(arrayList.getName());
                        mainPlayModel.setAudioFile(arrayList.getAudioFile());
                        mainPlayModel.setAudioDirection(arrayList.getAudioDirection());
                        mainPlayModel.setAudiomastercat(arrayList.getAudiomastercat());
                        mainPlayModel.setAudioSubCategory(arrayList.getAudioSubCategory());
                        mainPlayModel.setImageFile(arrayList.getImageFile());
                        mainPlayModel.setAudioDuration(arrayList.getAudioDuration());
                        boolean audioc = true;
                        if (isDisclaimer == 1) {
                            if (player != null) {
                                player.setPlayWhenReady(true);
                                audioc = false;
                                listModelList2.add(mainPlayModel);
                            } else {
                                isDisclaimer = 0;
                                if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                                    audioc = true;
                                    listModelList2.add(mainPlayModel);
                                }
                            }
                        } else {
                            isDisclaimer = 0;
                            if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                                audioc = true;
                                listModelList2.add(mainPlayModel);
                            }
                        }
                        callPlayer(position, listModelList2, audioc);
                    }
                } else if (Name.equalsIgnoreCase(getString(R.string.top_categories))) {
                    String catName = shared1.getString(CONSTANTS.PREF_KEY_Cat_Name, "");
                    if (catName.equalsIgnoreCase(Category)) {
                        if (isDisclaimer == 1) {
                            if (player != null) {
                                if (!player.getPlayWhenReady()) {
                                    player.setPlayWhenReady(true);
                                }
                            } else {
                                audioClick = true;
                            }
                            callMyPlayer();
                            BWSApplication.showToast(
                                    "The audio shall start playing after the disclaimer", activity);
                        } else {
                            if (player != null) {
                                if (position != PlayerPosition) {
                                    player.seekTo(position, 0);
                                    player.setPlayWhenReady(true);
                                    SharedPreferences sharedxx =
                                            context.getSharedPreferences(
                                                    CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedxx.edit();
                                    editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
                                    editor.apply();
                                }
                                callMyPlayer();
                            } else {
                                callPlayer(position, listModelList, true);
                            }
                        }
                    } else {
                        ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList2 = new ArrayList<>();
                        listModelList2.addAll(listModelList);
                        Gson gson = new Gson();
                        SharedPreferences shared12 =
                                context.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                        String IsPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1");
                        String DisclimerJson =
                                shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString());
                        Type type = new TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio>() {
                        }.getType();
                        HomeScreenModel.ResponseData.DisclaimerAudio arrayList =
                                gson.fromJson(DisclimerJson, type);
                        ViewAllAudioListModel.ResponseData.Detail mainPlayModel =
                                new ViewAllAudioListModel.ResponseData.Detail();
                        mainPlayModel.setID(arrayList.getId());
                        mainPlayModel.setName(arrayList.getName());
                        mainPlayModel.setAudioFile(arrayList.getAudioFile());
                        mainPlayModel.setAudioDirection(arrayList.getAudioDirection());
                        mainPlayModel.setAudiomastercat(arrayList.getAudiomastercat());
                        mainPlayModel.setAudioSubCategory(arrayList.getAudioSubCategory());
                        mainPlayModel.setImageFile(arrayList.getImageFile());
                        mainPlayModel.setAudioDuration(arrayList.getAudioDuration());
                        boolean audioc = true;
                        if (isDisclaimer == 1) {
                            if (player != null) {
                                player.setPlayWhenReady(true);
                                audioc = false;
                                listModelList2.add(mainPlayModel);
                            } else {
                                isDisclaimer = 0;
                                if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                                    audioc = true;
                                    listModelList2.add(mainPlayModel);
                                }
                            }
                        } else {
                            isDisclaimer = 0;
                            if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                                audioc = true;
                                listModelList2.add(mainPlayModel);
                            }
                        }
                        callPlayer(position, listModelList2, audioc);
                    }
                } else {
                    if ((AudioPlayerFlag.equalsIgnoreCase("MainAudioList")
                            || AudioPlayerFlag.equalsIgnoreCase("ViewAllAudioList"))
                            && MyPlaylist.equalsIgnoreCase(Name)) {

                        if (isDisclaimer == 1) {
                            if (player != null) {
                                if (!player.getPlayWhenReady()) {
                                    player.setPlayWhenReady(true);
                                }
                            } else {
                                audioClick = true;
                            }
                            callMyPlayer();
                            BWSApplication.showToast(
                                    "The audio shall start playing after the disclaimer", activity);
                        } else {
                            if (player != null) {
                                if (position != PlayerPosition) {
                                    player.seekTo(position, 0);
                                    player.setPlayWhenReady(true);
                                    SharedPreferences sharedxx =
                                            context.getSharedPreferences(
                                                    CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedxx.edit();
                                    editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
                                    editor.apply();
                                }
                                callMyPlayer();
                            } else {
                                callPlayer(0, listModelList, true);
                            }
                        }
                    } else {
                        ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList2 = new ArrayList<>();
                        listModelList2.addAll(listModelList);
                        Gson gson = new Gson();
                        SharedPreferences shared12 =
                                context.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                        String IsPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1");
                        String DisclimerJson =
                                shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString());
                        Type type = new TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio>() {
                        }.getType();
                        HomeScreenModel.ResponseData.DisclaimerAudio arrayList =
                                gson.fromJson(DisclimerJson, type);
                        ViewAllAudioListModel.ResponseData.Detail mainPlayModel =
                                new ViewAllAudioListModel.ResponseData.Detail();
                        mainPlayModel.setID(arrayList.getId());
                        mainPlayModel.setName(arrayList.getName());
                        mainPlayModel.setAudioFile(arrayList.getAudioFile());
                        mainPlayModel.setAudioDirection(arrayList.getAudioDirection());
                        mainPlayModel.setAudiomastercat(arrayList.getAudiomastercat());
                        mainPlayModel.setAudioSubCategory(arrayList.getAudioSubCategory());
                        mainPlayModel.setImageFile(arrayList.getImageFile());
                        mainPlayModel.setAudioDuration(arrayList.getAudioDuration());
                        boolean audioc = true;
                        if (isDisclaimer == 1) {
                            if (player != null) {
                                player.setPlayWhenReady(true);
                                audioc = false;
                                listModelList2.add(mainPlayModel);
                            } else {
                                isDisclaimer = 0;
                                if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                                    audioc = true;
                                    listModelList2.add(mainPlayModel);
                                }
                            }
                        } else {
                            isDisclaimer = 0;
                            if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                                audioc = true;
                                listModelList2.add(mainPlayModel);
                            }
                        }
                        callPlayer(position, listModelList2, audioc);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void callMyPlayer() {
            Intent i = new Intent(context, MyPlayerActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            context.startActivity(i);
            activity.overridePendingTransition(0, 0);
        }

        private void callPlayer(
                int position,
                ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModel,
                boolean audioc) {
            if (audioc) {
                callNewPlayerRelease();
            }
            SharedPreferences shared =
                    context.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String json;
            if (Name.equalsIgnoreCase("My Downloads")) {
                ArrayList<DownloadAudioDetails> downloadAudioDetails = new ArrayList<>();
                for (int i = 0; i < listModelList.size(); i++) {
                    DownloadAudioDetails mainPlayModel = new DownloadAudioDetails();
                    mainPlayModel.setID(listModelList.get(i).getID());
                    mainPlayModel.setName(listModelList.get(i).getName());
                    mainPlayModel.setAudioFile(listModelList.get(i).getAudioFile());
                    mainPlayModel.setAudioDirection(listModelList.get(i).getAudioDirection());
                    mainPlayModel.setAudiomastercat(listModelList.get(i).getAudiomastercat());
                    mainPlayModel.setAudioSubCategory(listModelList.get(i).getAudioSubCategory());
                    mainPlayModel.setImageFile(listModelList.get(i).getImageFile());
                    mainPlayModel.setAudioDuration(listModelList.get(i).getAudioDuration());
                    downloadAudioDetails.add(mainPlayModel);
                }
                editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "DownloadListAudio");
                json = gson.toJson(downloadAudioDetails);
            } else if (Name.equalsIgnoreCase(getString(R.string.top_categories))) {
                editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, getString(R.string.top_categories));
                editor.putString(CONSTANTS.PREF_KEY_Cat_Name, Category);
                json = gson.toJson(listModel);
            } else {
                editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "ViewAllAudioList");
                json = gson.toJson(listModel);
            }
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json);
            editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
            editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "");
            editor.putString(CONSTANTS.PREF_KEY_PlayFrom, Name);
            editor.apply();
            audioClick = audioc;
            callMyPlayer();
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

  /*    private void callnewTrans(int position, ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList) {
          SharedPreferences shared = context.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
          boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
          String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
          String MyPlaylist = shared.getString(CONSTANTS.PREF_KEY_myPlaylist, "");
          int positionSaved = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
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
                          if (position != positionSaved) {
                              player.seekTo(position, 0);
                              player.setPlayWhenReady(true);
                              miniPlayer = 1;
                              SharedPreferences sharedxx = context.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                              SharedPreferences.Editor editor = sharedxx.edit();
                              editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
                              editor.commit();
                          }
                          Intent i = new Intent(getActivity(), AudioPlayerActivity.class);
                          i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                          getActivity().startActivity(i);
                          getActivity().overridePendingTransition(0, 0);
                      } else {
                          callTransFrag(position, listModelList, true);
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
                  boolean audioc = true;
                  if (isDisclaimer == 1) {
                      if (player != null) {
                          player.setPlayWhenReady(true);
                          audioc = false;
                          listModelList2.add(position, mainPlayModel);
                      } else {
                          isDisclaimer = 0;
                          if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                              audioc = true;
                              listModelList2.add(position, mainPlayModel);
                          }
                      }
                  } else {
                      isDisclaimer = 0;
                      if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                          audioc = true;
                          listModelList2.add(position, mainPlayModel);
                      }
                  }
                  callTransFrag(position, listModelList2, audioc);
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
                      if (MyPlaylist.equalsIgnoreCase(getString(R.string.recently_played))) {
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
                              if (position < listModelList2.size()) {
                                  position = position;
                              } else {
                                  position = 0;
                              }
                          } else {
                              listModelList2.addAll(listModelList);
                          }
                          callTransFrag(position, listModelList2, true);
                      } else {
                          if (player != null) {
                              if (position != positionSaved) {
                                  player.seekTo(position, 0);
                                  player.setPlayWhenReady(true);
                                  miniPlayer = 1;
                                  SharedPreferences sharedxx = context.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                                  SharedPreferences.Editor editor = sharedxx.edit();
                                  editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
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
                                  if (position < listModelList2.size()) {
                                      position = position;
                                  } else {
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
                      if (position < listModelList2.size()) {
                          position = position;
                      } else {
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
                  boolean audioc = true;
                  if (isDisclaimer == 1) {
                      if (player != null) {
                          player.setPlayWhenReady(true);
                          audioc = false;
                          listModelList2.add(position, mainPlayModel);
                      } else {
                          isDisclaimer = 0;
                          if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                              audioc = true;
                              listModelList2.add(position, mainPlayModel);
                          }
                      }
                  } else {
                      isDisclaimer = 0;
                      if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                          audioc = true;
                          listModelList2.add(position, mainPlayModel);
                      }
                  }
                  callTransFrag(position, listModelList2, audioc);
              }
          }
      }

      private void callTransFrag(int position, ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList, boolean audioc) {
          try {
              SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
              SharedPreferences.Editor editor = shared.edit();
              Gson gson = new Gson();
              String json = "";
              ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList2 = new ArrayList<>();
              ViewAllAudioListModel.ResponseData.Detail mainPlayModel = new ViewAllAudioListModel.ResponseData.Detail();
              if (Name.equalsIgnoreCase(getString(R.string.top_categories))) {
                  SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                  boolean audioPlay = shared1.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                  AudioFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
                  String catName = shared1.getString(CONSTANTS.PREF_KEY_Cat_Name, "");
                  int positionSaved = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
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
                          editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "TopCategories");
                          editor.putString(CONSTANTS.PREF_KEY_Cat_Name, Category);
                          editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json);
                          editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
                          editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                          editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                          editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                          editor.putString(CONSTANTS.PREF_KEY_myPlaylist, Name);
                          editor.commit();
                          if (player != null) {
                              if (position != positionSaved) {
                                  player.seekTo(position, 0);
                                  player.setPlayWhenReady(true);
                                  miniPlayer = 1;
                                  SharedPreferences sharedxx = context.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                                  SharedPreferences.Editor editord = sharedxx.edit();
                                  editord.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
                                  editord.commit();
                              }
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
                      if (isDisclaimer == 1) {
                          if (player != null) {
                              player.setPlayWhenReady(true);
                              audioc = false;
                              listModelList2.add(position, mainPlayModel);
                          } else {
                              isDisclaimer = 0;
                              if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                                  audioc = true;
                                  listModelList2.add(position, mainPlayModel);
                              }
                          }
                      } else {
                          isDisclaimer = 0;
                          if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                              audioc = true;
                              listModelList2.add(position, mainPlayModel);
                          }
                      }
                      json = gson.toJson(listModelList2);
                      editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "TopCategories");
                      editor.putString(CONSTANTS.PREF_KEY_Cat_Name, Category);
                      editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json);
                      editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
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
                      editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "DownloadListAudio");
                  } else {
                      editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "ViewAllAudioList");
                  }
                  editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json);
                  editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
                  editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                  editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                  editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                  editor.putString(CONSTANTS.PREF_KEY_myPlaylist, Name);
                  editor.commit();
  //                openMyFragment();
                  miniPlayer = 1;
                  audioClick = true;
                  callNewPlayerRelease();

  //                SharedPreferences shared1 = context.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
  //                String IsPlayDisclimer = (shared1.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1"));
  //                if(IsPlayDisclimer.equalsIgnoreCase("1")){
  //                    openOnlyFragment();
  //                }
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
          if (audioc) {
              callNewPlayerRelease();
          }
          openOnlyFragment();
      }*/
}