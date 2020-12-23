package com.brainwellnessspa.DashboardModule.Audio.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.DashboardModule.Activities.AddPlaylistActivity;
import com.brainwellnessspa.DashboardModule.Activities.AudioPlayerActivity;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Activities.MembershipChangeActivity;
import com.brainwellnessspa.DashboardModule.Models.MainAudioModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.BigBoxLayoutBinding;

import java.util.ArrayList;

import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.audioClick;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.miniPlayer;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.callNewPlayerRelease;
import static com.brainwellnessspa.DashboardModule.Playlist.MyPlaylistsFragment.disclaimerPlayed;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.player;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.MyViewHolder> {
    Context ctx;
    FragmentActivity activity;
    String IsLock;
    int index = -1;
    private ArrayList<MainAudioModel.ResponseData.Detail> listModelList;

    public DownloadAdapter(ArrayList<MainAudioModel.ResponseData.Detail> listModelList, Context ctx, FragmentActivity activity,
                           String IsLock) {
        this.listModelList = listModelList;
        this.ctx = ctx;
        this.activity = activity;
        this.IsLock = IsLock;
    }

    @NonNull
    @Override
    public DownloadAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BigBoxLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.big_box_layout, parent, false);
        return new DownloadAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadAdapter.MyViewHolder holder, int position) {
        holder.binding.tvTitle.setText(listModelList.get(position).getName());
        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 20,
                1, 1, 0.48f, 20);
        holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        holder.binding.tvAddToPlaylist.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        holder.binding.tvAddToPlaylist.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        Glide.with(ctx).load(listModelList.get(position).getImageFile()).thumbnail(0.05f)
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
        holder.binding.llMainLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.binding.tvAddToPlaylist.setVisibility(View.VISIBLE);
                index = position;
                notifyDataSetChanged();
                return true;
            }
        });
        holder.binding.tvAddToPlaylist.setOnClickListener(view -> {
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
                i.putExtra("AudioId", listModelList.get(position).getID());
                i.putExtra("PlaylistID", "");
                ctx.startActivity(i);
            }

        });
        holder.binding.llMainLayout.setOnClickListener(view -> {
            try {
                if (IsLock.equalsIgnoreCase("1")) {
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                    Intent i = new Intent(ctx, MembershipChangeActivity.class);
                    i.putExtra("ComeFrom", "Plan");
                    ctx.startActivity(i);
                } else if (IsLock.equalsIgnoreCase("2")) {
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                    BWSApplication.showToast("Please re-activate your membership plan", ctx);
                } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                    holder.binding.ivLock.setVisibility(View.GONE);
                    SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                    boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                    String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                    if (audioPlay && AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
                        if (isDisclaimer == 1) {
                            if(player!=null){
                                player.setPlayWhenReady(true);
                            }
                            BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                        } else {
                            callTransFrag(position, listModelList);
                        }
                    } else {
                        isDisclaimer = 0;
                        disclaimerPlayed = 0;
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
                        mainPlayModel.setAudioDuration("00:48");
                        listModelList2.addAll(listModelList);
                        listModelList2.add(position, mainPlayModel);
                        callTransFrag(position, listModelList2);
                    }
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        });
    }

    private void callTransFrag(int position, ArrayList<MainAudioModel.ResponseData.Detail> listModelList) {
        try {
            miniPlayer = 1;
            audioClick = true;

            callNewPlayerRelease();
           /* Fragment fragment = new MiniPlayerFragment();
            FragmentManager fragmentManager1 = activity.getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .add(R.id.flContainer, fragment)
                    .commit();*/
            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            Gson gson = new Gson();
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
                mainPlayModel.setLike(listModelList.get(i).getLike());
                mainPlayModel.setDownload(listModelList.get(i).getDownload());
                mainPlayModel.setAudioDuration(listModelList.get(i).getAudioDuration());
                downloadAudioDetails.add(mainPlayModel);
            }
            String json = gson.toJson(downloadAudioDetails);
            editor.putString(CONSTANTS.PREF_KEY_modelList, json);
            editor.putInt(CONSTANTS.PREF_KEY_position, position);
            editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
            editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
            editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
            editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
            editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "DownloadListAudio");
            editor.commit();
            Intent i = new Intent(ctx, AudioPlayerActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            ctx.startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (4 > listModelList.size()) {
            return listModelList.size();
        } else {
            return 4;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        BigBoxLayoutBinding binding;

        public MyViewHolder(BigBoxLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
