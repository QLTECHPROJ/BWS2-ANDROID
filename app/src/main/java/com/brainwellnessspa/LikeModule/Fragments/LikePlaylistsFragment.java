package com.brainwellnessspa.LikeModule.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Models.AudioLikeModel;
import com.brainwellnessspa.DashboardModule.Models.MainAudioModel;
import com.brainwellnessspa.DashboardModule.Models.PlaylistLikeModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.brainwellnessspa.InvoiceModule.Models.InvoiceListModel;
import com.brainwellnessspa.LikeModule.Models.LikesHistoryModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.FragmentLikesBinding;
import com.brainwellnessspa.databinding.LikeListLayoutBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.player;
import static com.brainwellnessspa.Utility.MusicService.isCompleteStop;
import static com.brainwellnessspa.Utility.MusicService.isMediaStart;
import static com.brainwellnessspa.Utility.MusicService.isPause;
import static com.brainwellnessspa.Utility.MusicService.isPrepare;
import static com.brainwellnessspa.Utility.MusicService.stopMedia;

public class LikePlaylistsFragment extends Fragment {
    FragmentLikesBinding binding;
    String UserID, AudioFlag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_likes, container, false);
        View view = binding.getRoot();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvLikesList.setLayoutManager(mLayoutManager);
        binding.rvLikesList.setItemAnimator(new DefaultItemAnimator());
        prepareData();
        binding.llError.setVisibility(View.GONE);
        binding.tvFound.setText("No result found");
        return view;
    }

    public void prepareData() {
        if (!AudioFlag.equalsIgnoreCase("0")) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(13, 9, 13, 84);
            binding.llSpace.setLayoutParams(params);
        } else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(13, 9, 13, 28);
            binding.llSpace.setLayoutParams(params);
        }
        if (BWSApplication.isNetworkConnected(getActivity())) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            Call<LikesHistoryModel> listCall = APIClient.getClient().getLikeAudioPlaylistListing(UserID);
            listCall.enqueue(new Callback<LikesHistoryModel>() {
                @Override
                public void onResponse(Call<LikesHistoryModel> call, Response<LikesHistoryModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                        LikesHistoryModel listModel = response.body();
                        List<LikesHistoryModel.ResponseData.Playlist> listDataModel = listModel.getResponseData().getPlaylist();
                        if (listDataModel.size() == 0) {
                            binding.tvFound.setVisibility(View.VISIBLE);
                            binding.llError.setVisibility(View.VISIBLE);
                        } else {
                            binding.llError.setVisibility(View.GONE);
                            LikePlaylistsAdapter adapter = new LikePlaylistsAdapter(listModel.getResponseData().getPlaylist(), getActivity());
                            binding.rvLikesList.setAdapter(adapter);
                        }
                    }
                }

                @Override
                public void onFailure(Call<LikesHistoryModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }

    public class LikePlaylistsAdapter extends RecyclerView.Adapter<LikePlaylistsAdapter.MyViewHolder> {
        private List<LikesHistoryModel.ResponseData.Playlist> modelList;
        Context ctx;

        public LikePlaylistsAdapter(List<LikesHistoryModel.ResponseData.Playlist> modelList, Context ctx) {
            this.modelList = modelList;
            this.ctx = ctx;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LikeListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.like_list_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvTitle.setText(modelList.get(position).getPlaylistName());
            if (modelList.get(position).getTotalAudio().equalsIgnoreCase("") ||
                    modelList.get(position).getTotalAudio().equalsIgnoreCase("0") &&
                            modelList.get(position).getTotalhour().equalsIgnoreCase("")
                            && modelList.get(position).getTotalminute().equalsIgnoreCase("")) {
                holder.binding.tvTime.setText("0 Audio | 0h 0m");
            } else {
                if (modelList.get(position).getTotalminute().equalsIgnoreCase("")) {
                    holder.binding.tvTime.setText(modelList.get(position).getTotalAudio() + " Audio | "
                            + modelList.get(position).getTotalhour() + "h 0m");
                } else {
                    holder.binding.tvTime.setText(modelList.get(position).getTotalAudio() +
                            " Audios | " + modelList.get(position).getTotalhour() + "h " + modelList.get(position).getTotalminute() + "m");
                }
            }
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.cvImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.cvImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            Glide.with(ctx).load(modelList.get(position).getPlaylistImage()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

            holder.binding.llLikes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = new Dialog(ctx);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.logout_layout);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ctx.getResources().getColor(R.color.dark_blue_gray)));
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                    final TextView tvGoBack = dialog.findViewById(R.id.tvGoBack);
                    final TextView tvHeader = dialog.findViewById(R.id.tvHeader);
                    final TextView tvTitle = dialog.findViewById(R.id.tvTitle);
                    final Button Btn = dialog.findViewById(R.id.Btn);
                    tvTitle.setText("Remove from Liked Playlists?");
                    tvHeader.setText(modelList.get(position).getPlaylistName());
                    Btn.setText("Remove");
                    tvGoBack.setText("Cancel");
                    dialog.setOnKeyListener((v1, keyCode, event) -> {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            dialog.dismiss();
                        }
                        return false;
                    });

                    Btn.setOnClickListener(v2 -> {
                        callRemoveLike(modelList.get(position).getPlaylistId());
                        dialog.dismiss();
                    });
                    tvGoBack.setOnClickListener(v3 -> dialog.dismiss());
                    dialog.show();
                    dialog.setCancelable(false);
                }
            });

        }

        @Override
        public int getItemCount() {
            return modelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            LikeListLayoutBinding binding;

            public MyViewHolder(LikeListLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    private void callTransFrag(int position) {
        try {
            player = 1;
            if (isPrepare || isMediaStart || isPause) {
                stopMedia();
            }
            isPause = false;
            isMediaStart = false;
            isPrepare = false;
            isCompleteStop = false;


            Fragment fragment = new TransparentPlayerFragment();
            FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .add(R.id.flContainer, fragment)
                    .commit();

            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            ArrayList<MainAudioModel.ResponseData.Detail> listModelList2 = new ArrayList<>();
            MainAudioModel.ResponseData.Detail mainPlayModel = new MainAudioModel.ResponseData.Detail();
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

            listModelList2.add(listModelList.get(position));
            String json = gson.toJson(listModelList2);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
            editor.putInt(CONSTANTS.PREF_KEY_position, 0);
            editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
            editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
            editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
            editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
            editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "MainAudioList");
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void callRemoveLike(String id) {
        if (BWSApplication.isNetworkConnected(getActivity())) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            Call<PlaylistLikeModel> listCall = APIClient.getClient().getPlaylistLike(id, UserID);
            listCall.enqueue(new Callback<PlaylistLikeModel>() {
                @Override
                public void onResponse(Call<PlaylistLikeModel> call, Response<PlaylistLikeModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                        PlaylistLikeModel model = response.body();
                        BWSApplication.showToast(model.getResponseMessage(), getActivity());
                        prepareData();
                    }
                }

                @Override
                public void onFailure(Call<PlaylistLikeModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }
}