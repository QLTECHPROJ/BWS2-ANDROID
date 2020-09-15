package com.qltech.bws.DashboardModule.Audio.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Models.MainAudioModel;
import com.qltech.bws.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.qltech.bws.R;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.Utility.MusicService;
import com.qltech.bws.databinding.SmallBoxLayoutBinding;

import java.util.ArrayList;

import static com.qltech.bws.DashboardModule.Activities.DashboardActivity.player;
import static com.qltech.bws.LoginModule.Activities.OtpActivity.IsLocked;
import static com.qltech.bws.Utility.MusicService.isMediaStart;
import static com.qltech.bws.Utility.MusicService.isPause;
import static com.qltech.bws.Utility.MusicService.isPrepare;

public class RecentlyPlayedAdapter extends RecyclerView.Adapter<RecentlyPlayedAdapter.MyViewHolder> {
    Context ctx;
    FragmentActivity activity;
    String ComeFrom;
    private ArrayList<MainAudioModel.ResponseData.Detail> listModelList;

    public RecentlyPlayedAdapter(ArrayList<MainAudioModel.ResponseData.Detail> listModelList, Context ctx, FragmentActivity activity, String ComeFrom) {
        this.listModelList = listModelList;
        this.ctx = ctx;
        this.activity = activity;
        this.ComeFrom = ComeFrom;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SmallBoxLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.small_box_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.tvTitle.setText(listModelList.get(position).getName());
        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 10,
                1, 1, 0.28f, 10);
        holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(ctx).load(listModelList.get(position).getImageFile()).thumbnail(0.05f)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

        holder.binding.llMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IsLocked.equalsIgnoreCase("1")) {
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                    BWSApplication.showToast("Please re-activate your membership plan", ctx);
                } else if (IsLocked.equalsIgnoreCase("0") || IsLocked.equalsIgnoreCase("")) {
                    holder.binding.ivLock.setVisibility(View.GONE);
                    player = 1;
                    if (isPrepare||isMediaStart ||isPause) {
                        MusicService.stopMedia();
                    }
                    isPause = false;
                    isMediaStart = false;
                    isPrepare = false;
                    Fragment fragment = new TransparentPlayerFragment();
                    FragmentManager fragmentManager1 = activity.getSupportFragmentManager();
                    fragmentManager1.beginTransaction()
                            .add(R.id.rlAudiolist, fragment)
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
        SmallBoxLayoutBinding binding;

        public MyViewHolder(SmallBoxLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
