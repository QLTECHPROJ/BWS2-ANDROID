package com.brainwellnessspa.LikeModule.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Activities.MyPlaylistActivity;
import com.brainwellnessspa.DashboardModule.Models.PlaylistLikeModel;
import com.brainwellnessspa.DashboardModule.Playlist.MyPlaylistsFragment;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.brainwellnessspa.BillingOrderModule.Activities.MembershipChangeActivity;
import com.segment.analytics.Properties;

import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.audioClick;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.miniPlayer;

import static com.brainwellnessspa.DashboardModule.Search.SearchFragment.comefrom_search;
import static com.brainwellnessspa.LikeModule.Activities.LikeActivity.RefreshLikePlaylist;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.callNewPlayerRelease;
import static com.brainwellnessspa.DashboardModule.Playlist.MyPlaylistsFragment.RefreshIconData;

public class LikePlaylistsFragment extends Fragment {
    FragmentLikesBinding binding;
    String UserID, AudioFlag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_likes, container, false);
        View view = binding.getRoot();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvLikesList.setLayoutManager(mLayoutManager);
        binding.rvLikesList.setItemAnimator(new DefaultItemAnimator());

     /*   Properties p = new Properties();
        p.putValue("userId", UserID);
        BWSApplication.addToSegment("Liked Playlists Viewed", p, CONSTANTS.screen);*/

        prepareData();
        binding.llError.setVisibility(View.GONE);
        binding.tvFound.setText("Your like playlists will appear here");
        return view;
    }

    @Override
    public void onResume() {
        if (RefreshLikePlaylist == 1) {
            prepareData();
        }
        prepareData();
        super.onResume();
    }

    public void prepareData() {
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        if (!AudioFlag.equalsIgnoreCase("0")) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(13, 9, 13, 190);
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
                    try {
                        LikesHistoryModel listModel = response.body();
                        if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                            List<LikesHistoryModel.ResponseData.Playlist> listDataModel = listModel.getResponseData().getPlaylist();
                            if (listDataModel.size() == 0) {
                                binding.tvFound.setVisibility(View.VISIBLE);
                                binding.llError.setVisibility(View.VISIBLE);
                                binding.rvLikesList.setVisibility(View.GONE);
                            } else {
                                binding.llError.setVisibility(View.GONE);
                                binding.rvLikesList.setVisibility(View.VISIBLE);
                                LikePlaylistsAdapter adapter = new LikePlaylistsAdapter(listModel.getResponseData().getPlaylist(), getActivity());
                                binding.rvLikesList.setAdapter(adapter);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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

    private void callTransFrag(int position, List<LikesHistoryModel.ResponseData.Playlist.Audiolist> listModelList2) {
        try {
            miniPlayer = 1;
            audioClick = true;

            callNewPlayerRelease();

            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
            String json = gson.toJson(listModelList2);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
            editor.putInt(CONSTANTS.PREF_KEY_position, position);
            editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
            editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
            editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
            editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
            editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SubPlayList");
            editor.commit();
            callAddTransFrag();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void callAddTransFrag() {
        try {
            Fragment fragment = new MiniPlayerFragment();
            FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .add(R.id.flContainer, fragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void callRemoveLike(String id) {
        try {
            if (BWSApplication.isNetworkConnected(getActivity())) {
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                Call<PlaylistLikeModel> listCall = APIClient.getClient().getPlaylistLike(id, UserID);
                listCall.enqueue(new Callback<PlaylistLikeModel>() {
                    @Override
                    public void onResponse(Call<PlaylistLikeModel> call, Response<PlaylistLikeModel> response) {
                        try {
                            if (response.isSuccessful()) {
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                                PlaylistLikeModel model = response.body();
                                prepareData();
                                BWSApplication.showToast(model.getResponseMessage(), getActivity());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class LikePlaylistsAdapter extends RecyclerView.Adapter<LikePlaylistsAdapter.MyViewHolder> {
        Context ctx;
        private List<LikesHistoryModel.ResponseData.Playlist> modelList;

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
            holder.binding.equalizerview.setVisibility(View.GONE);

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

            if (modelList.get(position).getIsLock().equalsIgnoreCase("1")) {
                holder.binding.ivLock.setVisibility(View.VISIBLE);
            } else if (modelList.get(position).getIsLock().equalsIgnoreCase("2")) {
                holder.binding.ivLock.setVisibility(View.VISIBLE);
            } else if (modelList.get(position).getIsLock().equalsIgnoreCase("0") || modelList.get(position).getIsLock().equalsIgnoreCase("")) {
                holder.binding.ivLock.setVisibility(View.GONE);
            }

            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.cvImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.cvImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            Glide.with(ctx).load(modelList.get(position).getPlaylistImage()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

            holder.binding.llMenu.setOnClickListener(v -> {
                if (modelList.get(position).getIsLock().equalsIgnoreCase("1")) {
                    Intent i = new Intent(ctx, MembershipChangeActivity.class);
                    i.putExtra("ComeFrom", "Plan");
                    startActivity(i);
                } else if (modelList.get(position).getIsLock().equalsIgnoreCase("2")) {
                    BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
                } else if (modelList.get(position).getIsLock().equalsIgnoreCase("0") || modelList.get(position).getIsLock().equalsIgnoreCase("")) {
                    RefreshIconData = Integer.parseInt(modelList.get(position).getTotalAudio());
                    Intent i = new Intent(getActivity(), MyPlaylistActivity.class);
                    i.putExtra("PlaylistID", modelList.get(position).getPlaylistId());
                    i.putExtra("PlaylistName", modelList.get(position).getPlaylistName());
                    i.putExtra("PlaylistIDImage", modelList.get(position).getPlaylistImage());
                    i.putExtra("PlaylistType", modelList.get(position).getCreated());
                    i.putExtra("ScreenView", "Liked Playlist");
                    i.putExtra("Liked", "1");
                    startActivity(i);
                }
            });

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

            holder.binding.llMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (modelList.get(position).getIsLock().equalsIgnoreCase("1")) {
                        holder.binding.ivLock.setVisibility(View.VISIBLE);
                        Intent i = new Intent(ctx, MembershipChangeActivity.class);
                        i.putExtra("ComeFrom", "Plan");
                        startActivity(i);
                    } else if (modelList.get(position).getIsLock().equalsIgnoreCase("2")) {
                        holder.binding.ivLock.setVisibility(View.VISIBLE);
                        BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
                    } else if (modelList.get(position).getIsLock().equalsIgnoreCase("0") || modelList.get(position).getIsLock().equalsIgnoreCase("")) {
                        holder.binding.ivLock.setVisibility(View.GONE);
                        comefrom_search = 4;
                        Bundle bundle = new Bundle();
                        Fragment myPlaylistsFragment = new MyPlaylistsFragment();
                        FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                        bundle.putString("New", "");
                        bundle.putString("PlaylistID", modelList.get(position).getPlaylistId());
                        bundle.putString("PlaylistName", modelList.get(position).getPlaylistName());
                        bundle.putString("PlaylistImage", modelList.get(position).getPlaylistImage());
                        bundle.putString("PlaylistType", modelList.get(position).getCreated());
                        bundle.putString("ScreenView", "Liked");
                        bundle.putString("MyDownloads", "");
                        myPlaylistsFragment.setArguments(bundle);
                        fragmentManager1.beginTransaction()
                                .replace(R.id.flContainer, myPlaylistsFragment)
                                .commit();
                    }
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
}