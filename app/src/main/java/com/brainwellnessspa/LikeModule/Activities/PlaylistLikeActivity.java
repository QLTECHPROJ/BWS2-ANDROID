package com.brainwellnessspa.LikeModule.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Models.PlaylistLikeModel;
import com.brainwellnessspa.DashboardModule.Models.ReminderStatusPlaylistModel;
import com.brainwellnessspa.DashboardModule.Models.SubPlayListModel;
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.brainwellnessspa.DownloadModule.Activities.DownloadPlaylistActivity;
import com.brainwellnessspa.R;
import com.brainwellnessspa.ReminderModule.Activities.ReminderActivity;
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails;
import com.brainwellnessspa.RoomDataBase.DownloadPlaylistDetails;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.ActivityPlaylistLikeBinding;
import com.brainwellnessspa.databinding.DownloadPlaylistLayoutBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brainwellnessspa.DashboardModule.Account.AccountFragment.ComeScreenReminder;
import static com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.player;
import static com.brainwellnessspa.DashboardModule.Playlist.MyPlaylistsFragment.disclaimerPlayed;
import static com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment.isDisclaimer;
import static com.brainwellnessspa.Utility.MusicService.isCompleteStop;
import static com.brainwellnessspa.Utility.MusicService.isMediaStart;
import static com.brainwellnessspa.Utility.MusicService.isPause;
import static com.brainwellnessspa.Utility.MusicService.isPrepare;
import static com.brainwellnessspa.Utility.MusicService.stopMedia;

public class PlaylistLikeActivity extends AppCompatActivity {
    ActivityPlaylistLikeBinding binding;
    String UserID, AudioFlag, PlaylistID, PlaylistName, SearchFlag, PlaylistImage;
    Context ctx;
    PlayListsAdpater adpater;
    Activity activity;
    SubPlayListModel.ResponseData.PlaylistSong addDisclaimer = new SubPlayListModel.ResponseData.PlaylistSong();
    EditText searchEditText;
    public static int RefreshLikePlaylist = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_playlist_like);
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        ctx = PlaylistLikeActivity.this;
        activity = PlaylistLikeActivity.this;
        if (getIntent().getExtras() != null) {
            PlaylistID = getIntent().getStringExtra("PlaylistID");
        }
        addDisclaimer();
        binding.searchView.onActionViewExpanded();
        searchEditText = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.gray));
        searchEditText.setHintTextColor(getResources().getColor(R.color.gray));
        ImageView closeButton = binding.searchView.findViewById(R.id.search_close_btn);
        binding.searchView.clearFocus();
        searchEditText.setHint("Search for audios");
        closeButton.setOnClickListener(v -> {
            binding.searchView.clearFocus();
            searchEditText.setText("");
            binding.searchView.setQuery("", false);
        });
        binding.llBack.setOnClickListener(view -> finish());
        PrepareData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PrepareData();
    }

    public void PrepareData() {
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        if (!AudioFlag.equalsIgnoreCase("0")) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(10, 8, 10, 210);
            binding.llSpace.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(10, 8, 10, 20);
            binding.llSpace.setLayoutParams(params);
        }
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<SubPlayListModel> listCall = APIClient.getClient().getSubPlayLists(UserID, PlaylistID);
            listCall.enqueue(new Callback<SubPlayListModel>() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public void onResponse(Call<SubPlayListModel> call, Response<SubPlayListModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        SubPlayListModel listModel = response.body();
                        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                                5, 3, 1f, 0);
                        binding.ivBanner.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
                        binding.ivBanner.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
                        binding.ivBanner.setScaleType(ImageView.ScaleType.FIT_XY);
                        binding.tvTag.setVisibility(View.VISIBLE);
                        binding.tvTag.setText("Audios in Playlist");
                        binding.tvPlaylist.setText("Playlist");
                        binding.tvLibraryName.setText(listModel.getResponseData().getPlaylistName());
                        if (!listModel.getResponseData().getPlaylistImage().equalsIgnoreCase("")) {
                            try {
                                Glide.with(ctx).load(listModel.getResponseData().getPlaylistImage()).thumbnail(0.05f)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivBanner);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            binding.ivBanner.setImageResource(R.drawable.audio_bg);
                        }

                        if (listModel.getResponseData().getTotalAudio().equalsIgnoreCase("") ||
                                listModel.getResponseData().getTotalAudio().equalsIgnoreCase("0") &&
                                        listModel.getResponseData().getTotalhour().equalsIgnoreCase("")
                                        && listModel.getResponseData().getTotalminute().equalsIgnoreCase("")) {
                            binding.tvLibraryDetail.setText("0 Audio | 0h 0m");
                        } else {
                            if (listModel.getResponseData().getTotalminute().equalsIgnoreCase("")) {
                                binding.tvLibraryDetail.setText(listModel.getResponseData().getTotalAudio() + " Audio | "
                                        + listModel.getResponseData().getTotalhour() + "h 0m");
                            } else {
                                binding.tvLibraryDetail.setText(listModel.getResponseData().getTotalAudio() + " Audio | "
                                        + listModel.getResponseData().getTotalhour() + "h " +
                                        listModel.getResponseData().getTotalminute() + "m");
                            }
                        }
                        binding.rlSearch.setVisibility(View.VISIBLE);
                        binding.ivPlaylistStatus.setVisibility(View.VISIBLE);
                        binding.tvTag.setText(R.string.Audios_in_Playlist);
                        RecyclerView.LayoutManager playList = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
                        binding.rvPlayLists.setLayoutManager(playList);
                        binding.rvPlayLists.setItemAnimator(new DefaultItemAnimator());
                        adpater = new PlayListsAdpater(listModel.getResponseData().getPlaylistSongs(), ctx);
                        binding.rvPlayLists.setAdapter(adpater);

                        binding.llLike.setOnClickListener(new View.OnClickListener() {
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
                                tvHeader.setText(listModel.getResponseData().getPlaylistName());
                                Btn.setText("Remove");
                                tvGoBack.setText("Cancel");
                                dialog.setOnKeyListener((v1, keyCode, event) -> {
                                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                                        dialog.dismiss();
                                    }
                                    return false;
                                });

                                Btn.setOnClickListener(v2 -> {
                                    callRemoveLike(PlaylistID);
                                    dialog.dismiss();
                                });
                                tvGoBack.setOnClickListener(v3 -> dialog.dismiss());
                                dialog.show();
                                dialog.setCancelable(false);
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<SubPlayListModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    private void callRemoveLike(String id) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<PlaylistLikeModel> listCall = APIClient.getClient().getPlaylistLike(id, UserID);
            listCall.enqueue(new Callback<PlaylistLikeModel>() {
                @Override
                public void onResponse(Call<PlaylistLikeModel> call, Response<PlaylistLikeModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        PlaylistLikeModel model = response.body();
                        BWSApplication.showToast(model.getResponseMessage(), ctx);
                        PrepareData();
                        finish();
                        RefreshLikePlaylist = 1;
                    }
                }

                @Override
                public void onFailure(Call<PlaylistLikeModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }


    public class PlayListsAdpater extends RecyclerView.Adapter<PlayListsAdpater.MyViewHolders> implements Filterable {
        Context ctx;
        String UserID;
        private ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listModelList;
        private ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listFilterData;

        public PlayListsAdpater(ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listModelList, Context ctx) {
            this.listModelList = listModelList;
            this.listFilterData = listModelList;
            this.ctx = ctx;
        }

        @NonNull
        @Override
        public MyViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            DownloadPlaylistLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.download_playlist_layout, parent, false);
            return new MyViewHolders(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolders holder, int position) {
            final ArrayList<SubPlayListModel.ResponseData.PlaylistSong> mData = listFilterData;
            holder.binding.tvTitleA.setText(mData.get(position).getName());
            holder.binding.tvTimeA.setText(mData.get(position).getAudioDuration());
            String id = mData.get(position).getID();
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(mData.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            binding.ivPlaylistStatus.setOnClickListener(view -> {
                SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                if (audioPlay && AudioFlag.equalsIgnoreCase("LikePlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                    if (isDisclaimer == 1) {
                        BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                    } else {
                        callTransparentFrag(0, ctx, listModelList, "", PlaylistID);
                    }
                } else {
                    isDisclaimer = 0;
                    disclaimerPlayed = 0;
                    ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listModelList2 = new ArrayList<>();
                    listModelList2.add(addDisclaimer);
                    listModelList2.addAll(listModelList);
                    callTransparentFrag(0, ctx, listModelList2, "", PlaylistID);
                }
            });
            holder.binding.llMainLayout.setOnClickListener(view -> {
                SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "");
                if (audioPlay && AudioFlag.equalsIgnoreCase("LikePlayList") && pID.equalsIgnoreCase(PlaylistID)) {
                    if (isDisclaimer == 1) {
                        BWSApplication.showToast("The audio shall start playing after the disclaimer", ctx);
                    } else {
                        callTransparentFrag(holder.getAdapterPosition(), ctx, listModelList, "", PlaylistID);
                    }
                } else {
                    isDisclaimer = 0;
                    disclaimerPlayed = 0;
                    ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listModelList2 = new ArrayList<>();
                    if (position != 0) {
                        listModelList2.addAll(listModelList);
                        listModelList2.add(holder.getAdapterPosition(), addDisclaimer);
                    } else {
                        listModelList2.add(addDisclaimer);
                        listModelList2.addAll(listModelList);
                    }
                    callTransparentFrag(holder.getAdapterPosition(), ctx, listModelList2, "", PlaylistID);
                }
            });

            if (BWSApplication.isNetworkConnected(ctx)) {
                holder.binding.llMore.setClickable(true);
                holder.binding.llMore.setEnabled(true);
                holder.binding.ivMore.setColorFilter(ContextCompat.getColor(ctx, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);

            } else {
                holder.binding.llMore.setClickable(false);
                holder.binding.llMore.setEnabled(false);
                holder.binding.ivMore.setColorFilter(ContextCompat.getColor(ctx, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            }
            holder.binding.llMore.setOnClickListener(view -> {
                SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE);
                boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
                String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
              /*  if (audioPlay && AudioFlag.equalsIgnoreCase("Downloadlist") && pID.equalsIgnoreCase(PlaylistID)) {
                    if (isDisclaimer == 1) {
                        BWSApplication.showToast("You can see details after the disclaimer", ctx);
                    } else {
                        Intent i = new Intent(ctx, AddQueueActivity.class);
                        i.putExtra("play", "playlist");
                        i.putExtra("ID", mData.get(position).getID());
                        i.putExtra("PlaylistAudioId", mData.get(position).getPlaylistAudioId());
                        i.putExtra("position", position);
                        i.putParcelableArrayListExtra("data", mData);
                        i.putExtra("comeFrom", "myPlayList");
                        startActivity(i);
                    }
                } else {
                    Intent i = new Intent(ctx, AddQueueActivity.class);
                    i.putExtra("play", "playlist");
                    i.putExtra("ID", mData.get(position).getID());
                    i.putExtra("PlaylistAudioId", mData.get(position).getPlaylistAudioId());
                    i.putExtra("position", position);
                    i.putParcelableArrayListExtra("data", mData);
                    i.putExtra("comeFrom", "myPlayList");
                    startActivity(i);
                }*/
            });

        }

        @Override
        public int getItemCount() {
            return listFilterData.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    final FilterResults filterResults = new FilterResults();
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        listFilterData = listModelList;
                    } else {
                        ArrayList<SubPlayListModel.ResponseData.PlaylistSong> filteredList = new ArrayList<>();
                        for (SubPlayListModel.ResponseData.PlaylistSong row : listModelList) {
                            if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }
                        listFilterData = filteredList;
                    }
                    filterResults.values = listFilterData;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    if (listFilterData.size() == 0) {
                        binding.llError.setVisibility(View.VISIBLE);
                        binding.rvPlayLists.setVisibility(View.GONE);
                        binding.tvFound.setText("Couldn't find '" + SearchFlag + "'. Try searching again");
                        Log.e("search", SearchFlag);
                        binding.tvTag.setVisibility(View.GONE);
                    } else {
                        binding.tvTag.setVisibility(View.VISIBLE);
                        binding.llError.setVisibility(View.GONE);
                        binding.rvPlayLists.setVisibility(View.VISIBLE);
                        listFilterData = (ArrayList<SubPlayListModel.ResponseData.PlaylistSong>) filterResults.values;
                        notifyDataSetChanged();
                    }
                }
            };
        }

        public class MyViewHolders extends RecyclerView.ViewHolder {
            DownloadPlaylistLayoutBinding binding;

            public MyViewHolders(DownloadPlaylistLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    private void addDisclaimer() {
        addDisclaimer = new SubPlayListModel.ResponseData.PlaylistSong();
        addDisclaimer.setID("0");
        addDisclaimer.setName("Disclaimer");
        addDisclaimer.setAudioFile("");
        addDisclaimer.setAudioDirection("The audio shall start playing after the disclaimer");
        addDisclaimer.setAudiomastercat("");
        addDisclaimer.setAudioSubCategory("");
        addDisclaimer.setImageFile("");
        addDisclaimer.setLike("");
        addDisclaimer.setDownload("");
        addDisclaimer.setAudioDuration("0:48");
    }

    private void callTransparentFrag(int position, Context ctx, ArrayList<SubPlayListModel.ResponseData.PlaylistSong> listModelList, String s, String playlistID) {
        player = 1;
        if (isPrepare || isMediaStart || isPause) {
            stopMedia();
        }
        isPause = false;
        isMediaStart = false;
        isPrepare = false;
        isCompleteStop = false;

        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        Gson gson = new Gson();
        String json = gson.toJson(listModelList);
        editor.putString(CONSTANTS.PREF_KEY_modelList, json);
        editor.putInt(CONSTANTS.PREF_KEY_position, position);
        editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
        editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
        editor.putString(CONSTANTS.PREF_KEY_PlaylistId, playlistID);
        editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "");
        editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "LikePlayList");
        editor.commit();
        try {
            Fragment fragment = new TransparentPlayerFragment();
            FragmentManager fragmentManager1 = getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .add(R.id.flContainer, fragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}