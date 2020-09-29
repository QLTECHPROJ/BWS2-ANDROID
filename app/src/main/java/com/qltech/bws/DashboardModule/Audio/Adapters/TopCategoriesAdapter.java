package com.qltech.bws.DashboardModule.Audio.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.qltech.bws.DashboardModule.Models.MainAudioModel;
import com.qltech.bws.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.RoundBoxLayoutBinding;

import java.util.ArrayList;

import static com.qltech.bws.DashboardModule.Activities.DashboardActivity.player;
import static com.qltech.bws.Utility.MusicService.isMediaStart;
import static com.qltech.bws.Utility.MusicService.isPause;
import static com.qltech.bws.Utility.MusicService.isPrepare;
import static com.qltech.bws.Utility.MusicService.stopMedia;

public class TopCategoriesAdapter extends RecyclerView.Adapter<TopCategoriesAdapter.MyViewHolder> {
    private ArrayList<MainAudioModel.ResponseData.Detail> listModelList;
    Context ctx;
    FragmentActivity activity;
    String IsLock;

    public TopCategoriesAdapter(ArrayList<MainAudioModel.ResponseData.Detail> listModelList, Context ctx, FragmentActivity activity,
                                String IsLock) {
        this.listModelList = listModelList;
        this.ctx = ctx;
        this.activity = activity;
        this.IsLock = IsLock;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RoundBoxLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.round_box_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.tvTitle.setText(listModelList.get(position).getCategoryName());
        Glide.with(ctx).load(listModelList.get(position).getCatImage()).thumbnail(0.05f)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

        if (IsLock.equalsIgnoreCase("1")) {
            holder.binding.ivLock.setVisibility(View.VISIBLE);
           /* if (listModelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                holder.binding.ivLock.setVisibility(View.GONE);
            } else if (listModelList.get(position).getIsPlay().equalsIgnoreCase("0")
                    || listModelList.get(position).getIsPlay().equalsIgnoreCase("")) {

            }*/
        } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
            holder.binding.ivLock.setVisibility(View.GONE);
        }

      /*  holder.binding.llMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IsLock.equalsIgnoreCase("1")) {
                    if (listModelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                        holder.binding.ivLock.setVisibility(View.GONE);
                        try {
                            player = 1;
                            if (isPrepare || isMediaStart || isPause) {
                                stopMedia();
                            }
                            isPause = false;
                            isMediaStart = false;
                            isPrepare = false;
                            Fragment fragment = new TransparentPlayerFragment();
                            FragmentManager fragmentManager1 = activity.getSupportFragmentManager();
                            fragmentManager1.beginTransaction()
                                    .add(R.id.flContainer, fragment)
                                    .commit();
                            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = shared.edit();
                            Gson gson = new Gson();
                            String json = gson.toJson(listModelList.get(position));
                            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                            editor.putInt(CONSTANTS.PREF_KEY_position, position);
                            editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                            editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                            editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                            editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
                            editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "MainAudioList");
                            editor.commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (listModelList.get(position).getIsPlay().equalsIgnoreCase("0")
                            || listModelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                        holder.binding.ivLock.setVisibility(View.VISIBLE);
                        BWSApplication.showToast("Please re-activate your membership plan", ctx);
                    }
                } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                    holder.binding.ivLock.setVisibility(View.GONE);
                    try {
                        player = 1;
                        if (isPrepare || isMediaStart || isPause) {
                            stopMedia();
                        }
                        isPause = false;
                        isMediaStart = false;
                        isPrepare = false;
                        Fragment fragment = new TransparentPlayerFragment();
                        FragmentManager fragmentManager1 = activity.getSupportFragmentManager();
                        fragmentManager1.beginTransaction()
                                .add(R.id.flContainer, fragment)
                                .commit();
                        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = shared.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(listModelList.get(position));
                        editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                        editor.putInt(CONSTANTS.PREF_KEY_position, position);
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
            }
        });*/
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
        RoundBoxLayoutBinding binding;

        public MyViewHolder(RoundBoxLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
