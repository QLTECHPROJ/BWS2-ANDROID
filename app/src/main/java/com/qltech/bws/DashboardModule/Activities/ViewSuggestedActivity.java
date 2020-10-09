package com.qltech.bws.DashboardModule.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Models.SearchPlaylistModel;
import com.qltech.bws.DashboardModule.Models.SucessModel;
import com.qltech.bws.DashboardModule.Models.SuggestedModel;
import com.qltech.bws.DashboardModule.Playlist.MyPlaylistsFragment;
import com.qltech.bws.DashboardModule.Search.ViewAllSearchFragment;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.ActivityViewSuggestedBinding;
import com.qltech.bws.databinding.DownloadsLayoutBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.qltech.bws.DashboardModule.Search.SearchFragment.comefrom_search;

public class ViewSuggestedActivity extends AppCompatActivity {
    ActivityViewSuggestedBinding binding;
    Activity activity;
    Context ctx;
    String UserID, AudioFlag, Name, PlaylistID;
    ArrayList<SearchPlaylistModel.ResponseData> PlaylistModel;
    ArrayList<SuggestedModel.ResponseData> AudiolistModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_suggested);
        ctx = ViewSuggestedActivity.this;
        activity = ViewSuggestedActivity.this;
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
        binding.llBack.setOnClickListener(view -> {
            Intent i = new Intent(ctx, AddAudioActivity.class);
            startActivity(i);
            finish();
        });

        if (getIntent() != null) {
            Name = getIntent().getStringExtra("Name");
        }
        if (getIntent() != null) {
            PlaylistID = getIntent().getStringExtra(CONSTANTS.PlaylistID);
        }
        if (getIntent() != null) {
            AudiolistModel = getIntent().getParcelableArrayListExtra("AudiolistModel");
        }

        if (getIntent() != null) {
            PlaylistModel = getIntent().getParcelableArrayListExtra("PlaylistModel");
        }
        PrepareData();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ctx, AddAudioActivity.class);
        startActivity(i);
        finish();
    }

    public void PrepareData() {
        binding.tvTitle.setText(Name);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        binding.rvMainAudio.setLayoutManager(layoutManager);
        binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
        if (Name.equalsIgnoreCase("Suggested Audios")) {
            SuggestionAudioListAdpater suggestedAdpater = new SuggestionAudioListAdpater(AudiolistModel, ctx);
            binding.rvMainAudio.setAdapter(suggestedAdpater);
        } else if (Name.equalsIgnoreCase("Suggested Playlist")) {
            SuggestionPlayListsAdpater adpater = new SuggestionPlayListsAdpater(PlaylistModel, ctx);
            binding.rvMainAudio.setAdapter(adpater);
        }
    }

    public class SuggestionAudioListAdpater extends RecyclerView.Adapter<SuggestionAudioListAdpater.MyViewHolder> {
        Context ctx;
        private ArrayList<SuggestedModel.ResponseData> AudiolistModel;

        public SuggestionAudioListAdpater(ArrayList<SuggestedModel.ResponseData> AudiolistModel, Context ctx) {
            this.AudiolistModel = AudiolistModel;
            this.ctx = ctx;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            DownloadsLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.downloads_layout, parent, false);
            return new SuggestionAudioListAdpater.MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvTitle.setText(AudiolistModel.get(position).getName());
            holder.binding.tvTime.setText(AudiolistModel.get(position).getAudioDuration());
            holder.binding.pbProgress.setVisibility(View.GONE);
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.cvImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.cvImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            Glide.with(ctx).load(AudiolistModel.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            holder.binding.ivIcon.setImageResource(R.drawable.add_icon);
            holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg);
            if (AudiolistModel.get(position).getIsLock().equalsIgnoreCase("1")) {
                if (AudiolistModel.get(position).getIsPlay().equalsIgnoreCase("1")) {
                    holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                    holder.binding.ivLock.setVisibility(View.GONE);
                } else if (AudiolistModel.get(position).getIsPlay().equalsIgnoreCase("0")
                        || AudiolistModel.get(position).getIsPlay().equalsIgnoreCase("")) {
                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                }
            } else if (AudiolistModel.get(position).getIsLock().equalsIgnoreCase("0") || AudiolistModel.get(position).getIsLock().equalsIgnoreCase("")) {
                holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                holder.binding.ivLock.setVisibility(View.GONE);
            }

            holder.binding.llRemoveAudio.setOnClickListener(view -> {
                if (AudiolistModel.get(position).getIsLock().equalsIgnoreCase("1")) {
                    if (AudiolistModel.get(position).getIsPlay().equalsIgnoreCase("1")) {
                        holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                        holder.binding.ivLock.setVisibility(View.GONE);
                        Intent i = new Intent(ctx, AddPlaylistActivity.class);
                        i.putExtra("AudioId", AudiolistModel.get(position).getID());
                        i.putExtra("PlaylistID", "");
                        startActivity(i);
                    } else if (AudiolistModel.get(position).getIsPlay().equalsIgnoreCase("0")
                            || AudiolistModel.get(position).getIsPlay().equalsIgnoreCase("")) {
                        holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                        holder.binding.ivLock.setVisibility(View.VISIBLE);
                        BWSApplication.showToast("Please re-activate your membership plan", ctx);
                    }
                } else if (AudiolistModel.get(position).getIsLock().equalsIgnoreCase("0") || AudiolistModel.get(position).getIsLock().equalsIgnoreCase("")) {
                    holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                    holder.binding.ivLock.setVisibility(View.GONE);
                    Intent i = new Intent(ctx, AddPlaylistActivity.class);
                    i.putExtra("AudioId", AudiolistModel.get(position).getID());
                    i.putExtra("PlaylistID", "");
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return AudiolistModel.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            DownloadsLayoutBinding binding;

            public MyViewHolder(DownloadsLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    public class SuggestionPlayListsAdpater extends RecyclerView.Adapter<SuggestionPlayListsAdpater.MyViewHolder> {
        Context ctx;
        private ArrayList<SearchPlaylistModel.ResponseData> PlaylistModel;

        public SuggestionPlayListsAdpater(ArrayList<SearchPlaylistModel.ResponseData> PlaylistModel, Context ctx) {
            this.PlaylistModel = PlaylistModel;
            this.ctx = ctx;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            DownloadsLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.downloads_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvTitle.setText(PlaylistModel.get(position).getName());
            holder.binding.pbProgress.setVisibility(View.GONE);

            if (PlaylistModel.get(position).getTotalAudio().equalsIgnoreCase("") ||
                    PlaylistModel.get(position).getTotalAudio().equalsIgnoreCase("0") &&
                            PlaylistModel.get(position).getTotalhour().equalsIgnoreCase("")
                            && PlaylistModel.get(position).getTotalminute().equalsIgnoreCase("")) {
                holder.binding.tvTime.setText("0 Audio | 0h 0m");
            } else {
                if (PlaylistModel.get(position).getTotalminute().equalsIgnoreCase("")) {
                    holder.binding.tvTime.setText(PlaylistModel.get(position).getTotalAudio() + " Audio | "
                            + PlaylistModel.get(position).getTotalhour() + "h 0m");
                } else {
                    holder.binding.tvTime.setText(PlaylistModel.get(position).getTotalAudio() +
                            " Audios | " + PlaylistModel.get(position).getTotalhour() + "h " + PlaylistModel.get(position).getTotalminute() + "m");
                }
            }

            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.cvImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.cvImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            Glide.with(ctx).load(PlaylistModel.get(position).getImage()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            holder.binding.ivIcon.setImageResource(R.drawable.add_icon);
            holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg);
            if (PlaylistModel.get(position).getIsLock().equalsIgnoreCase("1")) {
                BWSApplication.showToast("Please re-activate your membership plan", ctx);
                holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                holder.binding.ivLock.setVisibility(View.VISIBLE);
            } else if (PlaylistModel.get(position).getIsLock().equalsIgnoreCase("0") || PlaylistModel.get(position).getIsLock().equalsIgnoreCase("")) {
                holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                holder.binding.ivLock.setVisibility(View.GONE);
            }

           /* holder.binding.llMainLayout.setOnClickListener(view -> {
                if (PlaylistModel.get(position).getIsLock().equalsIgnoreCase("1")) {
                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                    BWSApplication.showToast("Please re-activate your membership plan", ctx);
                } else if (PlaylistModel.get(position).getIsLock().equalsIgnoreCase("0") || PlaylistModel.get(position).getIsLock().equalsIgnoreCase("")) {
                    comefrom_search = 1;
                    holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                    holder.binding.ivLock.setVisibility(View.GONE);
                    Fragment myPlaylistsFragment = new MyPlaylistsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("New", "0");
                    bundle.putString("PlaylistID", PlaylistModel.get(position).getID());
                    bundle.putString("PlaylistName", PlaylistModel.get(position).getName());
                    bundle.putString("MyDownloads", "0");
                    myPlaylistsFragment.setArguments(bundle);
                    FragmentManager fragmentManager1 = getSupportFragmentManager();
                    fragmentManager1.beginTransaction()
                            .replace(R.id.flContainer, myPlaylistsFragment)
                            .commit();
                }
            });*/

            holder.binding.llRemoveAudio.setOnClickListener(view -> {
                if (PlaylistModel.get(position).getIsLock().equalsIgnoreCase("1")) {
                    BWSApplication.showToast("Please re-activate your membership plan", ctx);
                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                } else if (PlaylistModel.get(position).getIsLock().equalsIgnoreCase("0") || PlaylistModel.get(position).getIsLock().equalsIgnoreCase("")) {
                    holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                    holder.binding.ivLock.setVisibility(View.GONE);
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        Call<SucessModel> listCall = APIClient.getClient().getAddSearchAudioFromPlaylist(UserID, "", PlaylistID, PlaylistModel.get(position).getID());
                        listCall.enqueue(new Callback<SucessModel>() {
                            @Override
                            public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                                if (response.isSuccessful()) {
                                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                    SucessModel listModels = response.body();
                                    BWSApplication.showToast(listModels.getResponseMessage(), ctx);
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<SucessModel> call, Throwable t) {
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            }
                        });
                    } else {
                        BWSApplication.showToast(getString(R.string.no_server_found), ctx);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return PlaylistModel.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            DownloadsLayoutBinding binding;

            public MyViewHolder(DownloadsLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}