package com.brainwellnessspa.dashboardModule.manage;

import android.annotation.SuppressLint;
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
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardOldModule.Models.SegmentPlaylist;
import com.brainwellnessspa.DashboardOldModule.Models.ViewAllPlayListModel;
import com.brainwellnessspa.dashboardModule.activities.AddPlaylistActivity;
import com.brainwellnessspa.DownloadModule.Activities.DownloadPlaylistActivity;
import com.brainwellnessspa.R;

import com.brainwellnessspa.utility.APINewClient;
import com.brainwellnessspa.utility.CONSTANTS;
import com.brainwellnessspa.utility.MeasureRatio;
import com.brainwellnessspa.databinding.FragmentViewAllPlaylistBinding;
import com.brainwellnessspa.databinding.PlaylistViewAllLayoutBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.segment.analytics.Properties;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.BWSApplication.getAudioDataBase;
import static com.brainwellnessspa.BWSApplication.DB;

public class ViewAllPlaylistFragment extends Fragment {
  public static String GetPlaylistLibraryID = "";
  FragmentViewAllPlaylistBinding binding;
  String GetLibraryID, Name, coUserId, USERID, UserName, AudioFlag, MyDownloads, ScreenView = "";
  View view;

  @Override
  public View onCreateView(
      @NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding =
        DataBindingUtil.inflate(inflater, R.layout.fragment_view_all_playlist, container, false);
    view = binding.getRoot();
    SharedPreferences shared1 =
        requireActivity()
            .getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE);
    USERID = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "");
    coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "");
    UserName = shared1.getString(CONSTANTS.PREFE_ACCESS_NAME, "");
    SharedPreferences shared =
        requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
    AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
      DB = getAudioDataBase(getActivity());
    if (getArguments() != null) {
      GetLibraryID = getArguments().getString("GetLibraryID");
      Name = getArguments().getString("Name");
      MyDownloads = getArguments().getString("MyDownloads");
    }

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

  @SuppressLint("SetTextI18n")
  private void GetAllMedia() {
    DB.taskDao()
        .getAllPlaylist1(coUserId)
        .observe(
            requireActivity(),
            audioList -> {
                binding.tvTitle.setText("My Downloads");
                ScreenView = "My Downloads";
                ArrayList<ViewAllPlayListModel.ResponseData.Detail> listModelList = new ArrayList<>();
                for (int i = 0; i < audioList.size(); i++) {
                    ViewAllPlayListModel.ResponseData.Detail detail =
                            new ViewAllPlayListModel.ResponseData.Detail();
                    detail.setTotalAudio(audioList.get(i).getTotalAudio());
                    detail.setTotalhour(audioList.get(i).getTotalhour());
                    detail.setTotalminute(audioList.get(i).getTotalminute());
                    detail.setPlaylistID(audioList.get(i).getPlaylistID());
                    detail.setPlaylistDesc(audioList.get(i).getPlaylistDesc());
                    detail.setPlaylistMastercat(audioList.get(i).getPlaylistMastercat());
                    detail.setPlaylistSubcat(audioList.get(i).getPlaylistSubcat());
                    detail.setPlaylistName(audioList.get(i).getPlaylistName());
                    detail.setPlaylistImage(audioList.get(i).getPlaylistImage());
                    //                detail.setPlaylistImageDetails("");
                    detail.setCreated(audioList.get(i).getCreated());
                    listModelList.add(detail);
                }
                Properties p = new Properties();
                ArrayList<SegmentPlaylist> section = new ArrayList<>();
                for (int i = 0; i < audioList.size(); i++) {
                    SegmentPlaylist e = new SegmentPlaylist();
                    e.setPlaylistId(audioList.get(i).getPlaylistID());
                    e.setPlaylistName(audioList.get(i).getPlaylistName());
                    e.setPlaylistType(audioList.get(i).getCreated());
                    e.setPlaylistDuration(
                            audioList.get(i).getTotalhour()
                                    + "h "
                                    + audioList.get(i).getTotalminute()
                                    + "m");
                    e.setAudioCount(audioList.get(i).getTotalAudio());
                    section.add(e);
                }
                p.putValue("userId", coUserId);
                Gson gson = new Gson();
                p.putValue("playlists", gson.toJson(section));
                p.putValue("section", ScreenView);
                BWSApplication.addToSegment("View All Playlist Screen Viewed", p, CONSTANTS.screen);
                PlaylistAdapter adapter = new PlaylistAdapter(listModelList);
                binding.rvMainAudio.setAdapter(adapter);
            });
  }

  private void callBack() {
    Fragment audioFragment = new MainPlaylistFragment();
    FragmentManager fragmentManager1 = requireActivity().getSupportFragmentManager();
    fragmentManager1.beginTransaction().replace(R.id.flContainer, audioFragment).commit();
  }

  @Override
  public void onResume() {
    if (MyDownloads.equalsIgnoreCase("1")) {
      GetAllMedia();
    } else {
      prepareData();
    }
    super.onResume();
  }

  private void prepareData() {
    /*     try {
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
        AudioFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
        SharedPreferences shared2 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        String UnlockAudioLists = shared2.getString(CONSTANTS.PREF_KEY_UnLockAudiList, "");
        Gson gson1 = new Gson();
        Type type1 = new TypeToken<List<String>>() {
        }.getType();
        List<String> UnlockAudioList = gson1.fromJson(UnlockAudioLists, type1);
        if (!IsLock.equalsIgnoreCase("0") && (AudioFlag.equalsIgnoreCase("MainAudioList")
                || AudioFlag.equalsIgnoreCase("ViewAllAudioList"))) {
            String audioID = "";
            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = shared.getString(CONSTANTS.PREF_KEY_PlayerAudioList, String.valueOf(gson));
            Type type = new TypeToken<ArrayList<MainPlayModel>>() {
            }.getType();
            ArrayList<MainPlayModel> arrayList = gson.fromJson(json, type);

            if (arrayList.get(0).getAudioFile().equalsIgnoreCase("")) {
                arrayList.remove(0);
            }
            audioID = arrayList.get(0).getID();

            if (UnlockAudioList.contains(audioID)) {
            } else {
                SharedPreferences sharedm = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                SharedPreferences.Editor editorr = sharedm.edit();
                editorr.remove(CONSTANTS.PREF_KEY_MainAudioList);
                editorr.remove(CONSTANTS.PREF_KEY_PlayerAudioList);
                editorr.remove(CONSTANTS.PREF_KEY_PlayerPosition);
                editorr.remove(CONSTANTS.PREF_KEY_queuePlay);
                editorr.remove(CONSTANTS.PREF_KEY_audioPlay);
                editorr.remove(CONSTANTS.PREF_KEY_AudioPlayerFlag);
                editorr.remove(CONSTANTS.PREF_KEY_PlaylistId);
                editorr.remove(CONSTANTS.PREF_KEY_myPlaylist);
                editorr.clear();
                editorr.commit();
                callNewPlayerRelease();
            }
        } else if (!IsLock.equalsIgnoreCase("0") && !AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
            SharedPreferences sharedm = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editorr = sharedm.edit();
            editorr.remove(CONSTANTS.PREF_KEY_MainAudioList);
            editorr.remove(CONSTANTS.PREF_KEY_PlayerAudioList);
            editorr.remove(CONSTANTS.PREF_KEY_PlayerPosition);
            editorr.remove(CONSTANTS.PREF_KEY_queuePlay);
            editorr.remove(CONSTANTS.PREF_KEY_audioPlay);
            editorr.remove(CONSTANTS.PREF_KEY_AudioPlayerFlag);
            editorr.remove(CONSTANTS.PREF_KEY_PlaylistId);
            editorr.remove(CONSTANTS.PREF_KEY_myPlaylist);
            editorr.clear();
            editorr.commit();
            callNewPlayerRelease();

        }
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
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
    }*/

    if (BWSApplication.isNetworkConnected(getActivity())) {
      try {
        BWSApplication.showProgressBar(
            binding.progressBar, binding.progressBarHolder, getActivity());
        Call<ViewAllPlayListModel> listCall =
            APINewClient.getClient().getViewAllPlayLists(coUserId, GetLibraryID);
        listCall.enqueue(
            new Callback<ViewAllPlayListModel>() {
              @Override
              public void onResponse(
                  @NotNull Call<ViewAllPlayListModel> call,
                  @NotNull Response<ViewAllPlayListModel> response) {
                try {
                  ViewAllPlayListModel listModel = response.body();
                  if (listModel != null) {
                    if (listModel
                        .getResponseCode()
                        .equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                      BWSApplication.hideProgressBar(
                          binding.progressBar, binding.progressBarHolder, getActivity());
                      binding.tvTitle.setText(listModel.getResponseData().getView());
                      ScreenView = listModel.getResponseData().getView();
                      Properties p = new Properties();
                      ArrayList<SegmentPlaylist> section = new ArrayList<>();
                      for (int i = 0; i < listModel.getResponseData().getDetails().size(); i++) {
                        SegmentPlaylist e = new SegmentPlaylist();
                        e.setPlaylistId(
                            listModel.getResponseData().getDetails().get(i).getPlaylistID());
                        e.setPlaylistName(
                            listModel.getResponseData().getDetails().get(i).getPlaylistName());
                        e.setPlaylistType(
                            listModel.getResponseData().getDetails().get(i).getCreated());
                        e.setPlaylistDuration(
                            listModel.getResponseData().getDetails().get(i).getTotalhour()
                                + "h "
                                + listModel.getResponseData().getDetails().get(i).getTotalminute()
                                + "m");
                        e.setAudioCount(
                            listModel.getResponseData().getDetails().get(i).getTotalAudio());
                        section.add(e);
                      }
                      p.putValue("userId", coUserId);
                      Gson gson = new Gson();
                      p.putValue("playlists", gson.toJson(section));
                      p.putValue("section", ScreenView);
                      BWSApplication.addToSegment(
                          "View All Playlist Screen Viewed", p, CONSTANTS.screen);
                      PlaylistAdapter adapter =
                          new PlaylistAdapter(listModel.getResponseData().getDetails());
                      binding.rvMainAudio.setAdapter(adapter);
                    }
                  }
                } catch (Exception e) {
                  e.printStackTrace();
                }
              }

              @Override
              public void onFailure(
                  @NotNull Call<ViewAllPlayListModel> call, @NotNull Throwable t) {
                BWSApplication.hideProgressBar(
                    binding.progressBar, binding.progressBarHolder, getActivity());
              }
            });
      } catch (Exception e) {
        e.printStackTrace();
      }

    } else {
      BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
    }
  }

  public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.MyViewHolder> {
    String IsLock;
    int index = -1;
    private List<ViewAllPlayListModel.ResponseData.Detail> listModelList;

    public PlaylistAdapter(List<ViewAllPlayListModel.ResponseData.Detail> listModelList) {
      this.listModelList = listModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      PlaylistViewAllLayoutBinding v =
          DataBindingUtil.inflate(
              LayoutInflater.from(parent.getContext()),
              R.layout.playlist_view_all_layout,
              parent,
              false);
      return new MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
      MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 0, 1, 1, 0.44f, 0);
      holder.binding.ivRestaurantImage.getLayoutParams().height =
          (int) (measureRatio.getHeight() * measureRatio.getRatio());
      holder.binding.ivRestaurantImage.getLayoutParams().width =
          (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
      holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
      holder.binding.tvAddToPlaylist.getLayoutParams().height =
          (int) (measureRatio.getHeight() * measureRatio.getRatio());
      holder.binding.tvAddToPlaylist.getLayoutParams().width =
          (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
      holder.binding.tvPlaylistName.setText(listModelList.get(position).getPlaylistName());
      MeasureRatio measureRatio1 = BWSApplication.measureRatio(getActivity(), 0, 1, 1, 0.44f, 0);
      holder.binding.rlMainLayout.getLayoutParams().height =
          (int) (measureRatio1.getHeight() * measureRatio1.getRatio());
      holder.binding.rlMainLayout.getLayoutParams().width =
          (int) (measureRatio1.getWidthImg() * measureRatio1.getRatio());
      Glide.with(requireActivity())
          .load(listModelList.get(position).getPlaylistImage())
          .thumbnail(0.05f)
          .apply(RequestOptions.bitmapTransform(new RoundedCorners(42)))
          .priority(Priority.HIGH)
          .diskCacheStrategy(DiskCacheStrategy.ALL)
          .skipMemoryCache(false)
          .into(holder.binding.ivRestaurantImage);

      //            if (IsLock.equalsIgnoreCase("1")) {
      //                holder.binding.ivLock.setVisibility(View.VISIBLE);
      //            } else if (IsLock.equalsIgnoreCase("2")) {
      //                holder.binding.ivLock.setVisibility(View.VISIBLE);
      //            } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
      holder.binding.ivLock.setVisibility(View.GONE);
      //            }
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
            holder.binding.ivLock.setVisibility(View.GONE);
            Intent i = new Intent(getActivity(), AddPlaylistActivity.class);
            i.putExtra("AudioId", "");
            i.putExtra("ScreenView", "Playlist View All Screen");
            i.putExtra("PlaylistID", listModelList.get(position).getPlaylistID());
            i.putExtra("PlaylistName", listModelList.get(position).getPlaylistName());
            i.putExtra("PlaylistImage", listModelList.get(position).getPlaylistImage());
            i.putExtra("PlaylistType", listModelList.get(position).getCreated());
            i.putExtra("Liked", "0");
            startActivity(i);
          });

      holder.binding.rlMainLayout.setOnClickListener(
          view -> {
            //                if (IsLock.equalsIgnoreCase("1")) {
            //                    holder.binding.ivLock.setVisibility(View.VISIBLE);
            //                    Intent i = new Intent(getActivity(),
            // MembershipChangeActivity.class);
            //                    i.putExtra("ComeFrom", "Plan");
            //                    startActivity(i);
            //                } else if (IsLock.equalsIgnoreCase("2")) {
            //                    holder.binding.ivLock.setVisibility(View.VISIBLE);
            //                    BWSApplication.showToast(getString(R.string.reactive_plan),
            // getActivity());
            //                } else if (IsLock.equalsIgnoreCase("0") ||
            // IsLock.equalsIgnoreCase("")) {
            holder.binding.ivLock.setVisibility(View.GONE);
            if (MyDownloads.equalsIgnoreCase("1")) {
              //                            getMedia(listModelList.get(position).getPlaylistID());
              Intent i = new Intent(getActivity(), DownloadPlaylistActivity.class);
              i.putExtra("New", "0");
              i.putExtra("PlaylistID", listModelList.get(position).getPlaylistID());
              i.putExtra("PlaylistName", listModelList.get(position).getPlaylistName());
              i.putExtra("PlaylistImage", listModelList.get(position).getPlaylistImage());
              i.putExtra("PlaylistImageDetails", "");
              i.putExtra("TotalAudio", listModelList.get(position).getTotalAudio());
              i.putExtra("Totalhour", listModelList.get(position).getTotalhour());
              i.putExtra("Totalminute", listModelList.get(position).getTotalminute());
              i.putExtra("MyDownloads", "1");
              requireActivity().startActivity(i);
            } else {
              GetPlaylistLibraryID = GetLibraryID;
              Intent i = new Intent(getActivity(), MyPlaylistListingActivity.class);
              i.putExtra("New", "0");
              i.putExtra("PlaylistID", listModelList.get(position).getPlaylistID());
              i.putExtra("PlaylistName", listModelList.get(position).getPlaylistName());
              i.putExtra("PlaylistImage", listModelList.get(position).getPlaylistImage());
              i.putExtra("MyDownloads", MyDownloads);
              i.putExtra("ScreenView", ScreenView);
              i.putExtra("PlaylistType", listModelList.get(position).getCreated());
              i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
              startActivity(i);
            }
            //                    }
          });
    }

    @Override
    public int getItemCount() {
      return listModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
      PlaylistViewAllLayoutBinding binding;

      public MyViewHolder(PlaylistViewAllLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
      }
    }
  }
}
