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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Activities.MembershipChangeActivity;
import com.brainwellnessspa.DashboardModule.Activities.AddPlaylistActivity;
import com.brainwellnessspa.DashboardModule.Activities.AudioPlayerActivity;
import com.brainwellnessspa.DashboardModule.Models.MainAudioModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment;
import com.brainwellnessspa.R;
import com.brainwellnessspa.RoomDataBase.AudioDatabase;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.BigBoxLayoutBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;

import static com.brainwellnessspa.BWSApplication.MIGRATION_1_2;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.audioClick;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.miniPlayer;
import static com.brainwellnessspa.DashboardModule.Audio.AudioFragment.IsLock;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.callNewPlayerRelease;
import static com.brainwellnessspa.Services.GlobalInitExoPlayer.player;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.MyViewHolder> {
    Context ctx;
    FragmentActivity activity;
    String IsPlayDisclimer;
    int index = -1;
    List<String> downloadAudioDetailsList = new ArrayList<>();
    AudioDatabase DB;
    private ArrayList<MainAudioModel.ResponseData.Detail> listModelList;

    public DownloadAdapter(ArrayList<MainAudioModel.ResponseData.Detail> listModelList, Context ctx, FragmentActivity activity) {
        this.listModelList = listModelList;
        this.ctx = ctx;
        this.activity = activity;
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
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(28))).priority(Priority.HIGH)
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
        holder.binding.llMainLayout.setOnLongClickListener(v -> {
            holder.binding.tvAddToPlaylist.setVisibility(View.VISIBLE);
            index = position;
            notifyDataSetChanged();
            return true;
        });

        holder.binding.tvAddToPlaylist.setOnClickListener(view -> {
            if (IsLock.equalsIgnoreCase("1")) {
                Intent i = new Intent(ctx, MembershipChangeActivity.class);
                i.putExtra("ComeFrom", "Plan");
                ctx.startActivity(i);
            } else if (IsLock.equalsIgnoreCase("2")) {
                BWSApplication.showToast(ctx.getString(R.string.reactive_plan), ctx);
            } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                Intent i = new Intent(ctx, AddPlaylistActivity.class);
                i.putExtra("AudioId", listModelList.get(position).getID());
                i.putExtra("ScreenView", "Audio Main Screen");
                i.putExtra("PlaylistID", "");
                i.putExtra("PlaylistName", "");
                i.putExtra("PlaylistImage", "");
                i.putExtra("PlaylistType", "");
                i.putExtra("Liked", "0");
                ctx.startActivity(i);
            }
        });

        holder.binding.llMainLayout.setOnClickListener(view -> {
            SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
            IsPlayDisclimer = (shared1.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1"));
            try {
                if (IsLock.equalsIgnoreCase("1")) {
                    Intent i = new Intent(ctx, MembershipChangeActivity.class);
                    i.putExtra("ComeFrom", "Plan");
                    ctx.startActivity(i);
                } else if (IsLock.equalsIgnoreCase("2")) {
                    BWSApplication.showToast(ctx.getString(R.string.reactive_plan), ctx);
                } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
                    SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                    boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                    String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                    if (BWSApplication.isNetworkConnected(ctx)) {
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
                                Intent i = new Intent(ctx, AudioPlayerActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                ctx.startActivity(i);
                                activity.overridePendingTransition(0, 0);
                                BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                            } else {
                                if (player != null) {
                                    int ix = player.getMediaItemCount();
                                    if (ix < listModelList.size()) {
                                        callTransFrag(position, listModelList, true);
                                    } else {
                                        player.seekTo(position, 0);
                                        player.setPlayWhenReady(true);
                                        miniPlayer = 1;
                                        SharedPreferences sharedxx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedxx.edit();
                                        editor.putInt(CONSTANTS.PREF_KEY_position, position);
                                        editor.commit();
                                        Intent i = new Intent(ctx, AudioPlayerActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        ctx.startActivity(i);
                                        activity.overridePendingTransition(0, 0);
                                    }
                                } else {
                                    callTransFrag(position, listModelList, true);
                                }
                            }
                        } else {
                            ArrayList<MainAudioModel.ResponseData.Detail> listModelList2 = new ArrayList<>();
                            listModelList2.addAll(listModelList);
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
                        getMedia(audioPlay, AudioFlag, position);
                    }
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        });
    }

    private void getMedia(boolean audioPlay, String AudioFlag, int position) {
        DB = Room.databaseBuilder(ctx,
                AudioDatabase.class,
                "Audio_database")
                .addMigrations(MIGRATION_1_2)
                .build();
        AudioDatabase.databaseWriteExecutor.execute(() -> {
            downloadAudioDetailsList = DB.taskDao().geAllDataBYDownloaded("Complete");
        });

        int pos = 0;
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
                Intent i = new Intent(ctx, AudioPlayerActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                ctx.startActivity(i);
                activity.overridePendingTransition(0, 0);
                BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
            } else {
                ArrayList<MainAudioModel.ResponseData.Detail> listModelList2 = new ArrayList<>();
                for (int i = 0; i < listModelList.size(); i++) {
                    if (downloadAudioDetailsList.contains(listModelList.get(i).getName())) {
                        listModelList2.add(listModelList.get(i));
                    }
                }
                if (downloadAudioDetailsList.contains(listModelList.get(position).getName())) {
                    pos = position;
                } else {
//                            pos = 0;
                    BWSApplication.showToast(ctx.getString(R.string.no_server_found), ctx);
                }
                if (listModelList2.size() != 0) {
                    callTransFrag(pos, listModelList2, true);
                } else {
                    BWSApplication.showToast(ctx.getString(R.string.no_server_found), ctx);
                }
            }
        } else {
            ArrayList<MainAudioModel.ResponseData.Detail> listModelList2 = new ArrayList<>();
            for (int i = 0; i < listModelList.size(); i++) {
                if (downloadAudioDetailsList.contains(listModelList.get(i).getName())) {
                    listModelList2.add(listModelList.get(i));
                }
            }
            if (downloadAudioDetailsList.contains(listModelList.get(position).getName())) {
                pos = position;


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
                boolean audioc = true;
                if (isDisclaimer == 1) {
                    if (player != null) {
                        player.setPlayWhenReady(true);
                        audioc = false;
                        listModelList2.add(pos, mainPlayModel);
                    } else {
                        isDisclaimer = 0;
                        if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                            audioc = true;
                            listModelList2.add(pos, mainPlayModel);
                        }
                    }
                } else {
                    isDisclaimer = 0;
                    if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                        listModelList2.add(pos, mainPlayModel);
                        audioc = true;
                    }
                }


                if (listModelList2.size() != 0) {
                    if (!listModelList2.get(pos).getAudioFile().equalsIgnoreCase("")) {
                        if (listModelList2.size() != 0) {
                            callTransFrag(pos, listModelList2, audioc);
                        } else {
                            BWSApplication.showToast(ctx.getString(R.string.no_server_found), ctx);
                        }
                    } else if (listModelList2.get(pos).getAudioFile().equalsIgnoreCase("") && listModelList2.size() > 1) {
                        callTransFrag(pos, listModelList2, audioc);
                    } else {
                        BWSApplication.showToast(ctx.getString(R.string.no_server_found), ctx);
                    }
                } else {
                    BWSApplication.showToast(ctx.getString(R.string.no_server_found), ctx);
                }
            } else {
                BWSApplication.showToast(ctx.getString(R.string.no_server_found), ctx);
            }

        }
         /*       super.onPostExecute(aVoid);
            }
        }
        GetTask st = new GetTask();
        st.execute();*/
    }

    private void callTransFrag(int position, ArrayList<MainAudioModel.ResponseData.Detail> listModelList, boolean audioc) {
        try {
            miniPlayer = 1;
            audioClick = audioc;
            if (audioc) {
                callNewPlayerRelease();
            }
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
            if(IsPlayDisclimer.equalsIgnoreCase("1")){
                Fragment fragment = new MiniPlayerFragment();
                FragmentManager fragmentManager1 = activity.getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .add(R.id.flContainer, fragment)
                        .commit();
            }
            Intent i = new Intent(ctx, AudioPlayerActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            ctx.startActivity(i);
            activity.overridePendingTransition(0, 0);
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
