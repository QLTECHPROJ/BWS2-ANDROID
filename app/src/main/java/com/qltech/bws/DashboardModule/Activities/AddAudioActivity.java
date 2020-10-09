package com.qltech.bws.DashboardModule.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Models.SearchBothModel;
import com.qltech.bws.DashboardModule.Models.SearchPlaylistModel;
import com.qltech.bws.DashboardModule.Models.SucessModel;
import com.qltech.bws.DashboardModule.Models.SuggestedModel;
import com.qltech.bws.DashboardModule.Playlist.MyPlaylistsFragment;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.ActivityAddAudioBinding;
import com.qltech.bws.databinding.DownloadsLayoutBinding;
import com.qltech.bws.databinding.GlobalSearchLayoutBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.qltech.bws.DashboardModule.Search.SearchFragment.comefrom_search;

public class AddAudioActivity extends AppCompatActivity {
    ActivityAddAudioBinding binding;
    Context ctx;
    String UserID, PlaylistID;
    SerachListAdpater adpater;
    EditText searchEditText;
    Activity activity;
    public static boolean addToSearch = false;
    public static String MyPlaylistIds = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_audio);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_audio);
        ctx = AddAudioActivity.this;
        activity = AddAudioActivity.this;

        if (getIntent().getExtras() != null) {
            PlaylistID = getIntent().getStringExtra(CONSTANTS.PlaylistID);
        }
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        binding.searchView.onActionViewExpanded();
        searchEditText = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.gray));
        searchEditText.setHintTextColor(getResources().getColor(R.color.gray));
        ImageView closeButton = binding.searchView.findViewById(R.id.search_close_btn);
        binding.searchView.clearFocus();
        closeButton.setOnClickListener(view -> {
            binding.searchView.clearFocus();
            searchEditText.setText("");
            binding.rvSerachList.setAdapter(null);
            binding.rvSerachList.setVisibility(View.GONE);
            binding.llError.setVisibility(View.GONE);
            binding.searchView.setQuery("", false);
        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String search) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String search) {
                if (searchEditText.getText().toString().equalsIgnoreCase("")) {

                } else {
                    prepareSearchData(search, searchEditText);
                }
                return false;
            }
        });

        binding.llBack.setOnClickListener(view -> {
            finish();
        });

        RecyclerView.LayoutManager suggested = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        binding.rvSuggestedList.setLayoutManager(suggested);
        binding.rvSuggestedList.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager serachList = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        binding.rvSerachList.setLayoutManager(serachList);
        binding.rvSerachList.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager manager = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        binding.rvPlayList.setItemAnimator(new DefaultItemAnimator());
        binding.rvPlayList.setLayoutManager(manager);
        prepareSuggestedData();
    }

    private void prepareSearchData(String search, EditText searchEditText) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<SearchBothModel> listCall = APIClient.getClient().getSearchBoth(UserID, search);
            listCall.enqueue(new Callback<SearchBothModel>() {
                @Override
                public void onResponse(Call<SearchBothModel> call, Response<SearchBothModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        SearchBothModel listModel = response.body();
                        if (!searchEditText.getText().toString().equalsIgnoreCase("")) {
                            if (listModel.getResponseData().size() == 0) {
                                binding.rvSerachList.setVisibility(View.GONE);
                                binding.llError.setVisibility(View.VISIBLE);
                                binding.tvFound.setText("Search term not found please use another one");
                            } else {
                                binding.llError.setVisibility(View.GONE);
                                binding.rvSerachList.setVisibility(View.VISIBLE);
                                adpater = new SerachListAdpater(listModel.getResponseData(), activity, binding.rvSerachList, UserID);
                                binding.rvSerachList.setAdapter(adpater);
                            }
                        } else if (searchEditText.getText().toString().equalsIgnoreCase("")) {
                            binding.rvSerachList.setAdapter(null);
                            binding.rvSerachList.setVisibility(View.GONE);
                            binding.llError.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<SearchBothModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    /*private void prepareSearchData(String search, EditText searchEditText, String PlaylistID) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<SuggestionAudiosModel> listCall = APIClient.getClient().getAddSearchAudio(search, PlaylistID);
            listCall.enqueue(new Callback<SuggestionAudiosModel>() {
                @Override
                public void onResponse(Call<SuggestionAudiosModel> call, Response<SuggestionAudiosModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        SuggestionAudiosModel listModel = response.body();
                        if (!searchEditText.getText().toString().equalsIgnoreCase("")) {
                            if (listModel.getResponseData().size() == 0) {
                                binding.rvSerachList.setVisibility(View.GONE);
                                binding.llError.setVisibility(View.VISIBLE);
                                binding.tvFound.setText("Search term not found please use another one");
                            } else {
                                binding.llError.setVisibility(View.GONE);
                                binding.rvSerachList.setVisibility(View.VISIBLE);
                                adpater = new SerachListAdpater(listModel.getResponseData(), ctx, binding.rvSerachList, UserID, PlaylistID);
                                binding.rvSerachList.setAdapter(adpater);
                            }
                        } else if (searchEditText.getText().toString().equalsIgnoreCase("")) {
                            binding.rvSerachList.setAdapter(null);
                            binding.rvSerachList.setVisibility(View.GONE);
                            binding.llError.setVisibility(View.GONE);
                        }

                    }
                }

                @Override
                public void onFailure(Call<SuggestionAudiosModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }*/

    private void prepareSuggestedData() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<SuggestedModel> listCall = APIClient.getClient().getSuggestedLists(UserID);
            listCall.enqueue(new Callback<SuggestedModel>() {
                @Override
                public void onResponse(Call<SuggestedModel> call, Response<SuggestedModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        SuggestedModel listsModel = response.body();
                        binding.tvSuggestedAudios.setText(R.string.Suggested_Audios);
                        binding.tvSAViewAll.setVisibility(View.VISIBLE);
                        SuggestedAdpater suggestedAdpater = new SuggestedAdpater(listsModel.getResponseData(), ctx);
                        binding.rvSuggestedList.setAdapter(suggestedAdpater);

                        binding.tvSAViewAll.setOnClickListener(view -> {
                            Intent i = new Intent(ctx,ViewSuggestedActivity.class);
                            i.putExtra("Name", "Suggested Audios");
                            i.putExtra("PlaylistID",PlaylistID);
                            i.putParcelableArrayListExtra("AudiolistModel", listsModel.getResponseData());
                            startActivity(i);
                            finish();
                        });
                    }
                }

                @Override
                public void onFailure(Call<SuggestedModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }

        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<SearchPlaylistModel> listCall = APIClient.getClient().getSuggestedPlayLists(UserID);
            listCall.enqueue(new Callback<SearchPlaylistModel>() {
                @Override
                public void onResponse(Call<SearchPlaylistModel> call, Response<SearchPlaylistModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        SearchPlaylistModel listModel = response.body();
                        binding.tvSuggestedPlaylist.setText(R.string.Suggested_Playlist);
                        binding.tvSPViewAll.setVisibility(View.VISIBLE);

                        SuggestedPlayListsAdpater suggestedAdpater = new SuggestedPlayListsAdpater(listModel.getResponseData());
                        binding.rvPlayList.setAdapter(suggestedAdpater);

                        binding.tvSPViewAll.setOnClickListener(view -> {
                            Intent i = new Intent(ctx,ViewSuggestedActivity.class);
                            i.putExtra("Name", "Suggested Playlist");
                            i.putExtra("PlaylistID",PlaylistID);
                            i.putParcelableArrayListExtra("PlaylistModel", listModel.getResponseData());
                            startActivity(i);
                            finish();
                        });
                    }
                }

                @Override
                public void onFailure(Call<SearchPlaylistModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    public class SerachListAdpater extends RecyclerView.Adapter<SerachListAdpater.MyViewHolder> {
        Context ctx;
        String UserID;
        RecyclerView rvSerachList;
        private List<SearchBothModel.ResponseData> modelList;

        public SerachListAdpater(List<SearchBothModel.ResponseData> modelList, Context ctx,
                                 RecyclerView rvSerachList, String UserID) {
            this.modelList = modelList;
            this.ctx = ctx;
            this.rvSerachList = rvSerachList;
            this.UserID = UserID;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            GlobalSearchLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.global_search_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvTitle.setText(modelList.get(position).getName());

            if (modelList.get(position).getIsLock().equalsIgnoreCase("1")) {
                if (modelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                    holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                    holder.binding.ivLock.setVisibility(View.GONE);
                } else if (modelList.get(position).getIsPlay().equalsIgnoreCase("0")
                        || modelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                }
            } else if (modelList.get(position).getIsLock().equalsIgnoreCase("0") || modelList.get(position).getIsLock().equalsIgnoreCase("")) {
                holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                holder.binding.ivLock.setVisibility(View.GONE);
            }

            if (modelList.get(position).getIscategory().equalsIgnoreCase("1")) {
                holder.binding.tvPart.setText(R.string.Audio);
                holder.binding.llRemoveAudio.setVisibility(View.VISIBLE);
                holder.binding.llRemoveAudio.setOnClickListener(view -> {
                    if (modelList.get(position).getIsLock().equalsIgnoreCase("1")) {
                        if (modelList.get(position).getIsPlay().equalsIgnoreCase("1")) {
                            holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                            holder.binding.ivLock.setVisibility(View.GONE);
                            String AudioID = modelList.get(position).getID();
                            if (BWSApplication.isNetworkConnected(ctx)) {
                                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                Call<SucessModel> listCall = APIClient.getClient().getAddSearchAudioFromPlaylist(UserID, AudioID, PlaylistID, "");
                                listCall.enqueue(new Callback<SucessModel>() {
                                    @Override
                                    public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                                        if (response.isSuccessful()) {
                                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                            SucessModel listModel = response.body();
                                            BWSApplication.showToast(listModel.getResponseMessage(), ctx);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<SucessModel> call, Throwable t) {
                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                    }
                                });
                            } else {
                                BWSApplication.showToast(ctx.getString(R.string.no_server_found), ctx);
                            }
                        } else if (modelList.get(position).getIsPlay().equalsIgnoreCase("0")
                                || modelList.get(position).getIsPlay().equalsIgnoreCase("")) {
                            holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                            holder.binding.ivLock.setVisibility(View.VISIBLE);
                            BWSApplication.showToast("Please re-activate your membership plan", ctx);
                        }
                    } else if (modelList.get(position).getIsLock().equalsIgnoreCase("0") || modelList.get(position).getIsLock().equalsIgnoreCase("")) {
                        holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                        holder.binding.ivLock.setVisibility(View.GONE);
                        String AudioID = modelList.get(position).getID();
                        if (BWSApplication.isNetworkConnected(ctx)) {
                            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            Call<SucessModel> listCall = APIClient.getClient().getAddSearchAudioFromPlaylist(UserID, AudioID, PlaylistID, "");
                            listCall.enqueue(new Callback<SucessModel>() {
                                @Override
                                public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                                    if (response.isSuccessful()) {
                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                        SucessModel listModel = response.body();
                                        BWSApplication.showToast(listModel.getResponseMessage(), ctx);
                                        finish();
                                    }
                                }

                                @Override
                                public void onFailure(Call<SucessModel> call, Throwable t) {
                                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                }
                            });
                        } else {
                            BWSApplication.showToast(ctx.getString(R.string.no_server_found), ctx);
                        }
                    }
                });
            } else if (modelList.get(position).getIscategory().equalsIgnoreCase("0")) {
                holder.binding.tvPart.setText(R.string.Playlist);
                holder.binding.llRemoveAudio.setVisibility(View.VISIBLE);
                holder.binding.llRemoveAudio.setOnClickListener(view -> {
                    if (modelList.get(position).getIsLock().equalsIgnoreCase("1")) {
                        holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                        holder.binding.ivLock.setVisibility(View.VISIBLE);
                        BWSApplication.showToast("Please re-activate your membership plan", ctx);
                    } else if (modelList.get(position).getIsLock().equalsIgnoreCase("0") || modelList.get(position).getIsLock().equalsIgnoreCase("")) {
                        holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                        holder.binding.ivLock.setVisibility(View.GONE);
                        if (BWSApplication.isNetworkConnected(ctx)) {
                            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            Call<SucessModel> listCall = APIClient.getClient().getAddSearchAudioFromPlaylist(UserID, "", PlaylistID, modelList.get(position).getID());
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
                holder.binding.llMainLayout.setOnClickListener(view -> {
                    if (modelList.get(position).getIsLock().equalsIgnoreCase("1")) {
                        holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                        holder.binding.ivLock.setVisibility(View.VISIBLE);
                        BWSApplication.showToast("Please re-activate your membership plan", ctx);
                    } else if (modelList.get(position).getIsLock().equalsIgnoreCase("0") || modelList.get(position).getIsLock().equalsIgnoreCase("")) {
                        comefrom_search = 1;
                        holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                        holder.binding.ivLock.setVisibility(View.GONE);
                        Fragment myPlaylistsFragment = new MyPlaylistsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("New", "0");
                        bundle.putString("PlaylistID", modelList.get(position).getID());
                        bundle.putString("PlaylistName", modelList.get(position).getName());
                        bundle.putString("MyDownloads", "0");
                        myPlaylistsFragment.setArguments(bundle);
                        FragmentManager fragmentManager1 = getSupportFragmentManager();
                        fragmentManager1.beginTransaction()
                                .replace(R.id.flContainer, myPlaylistsFragment)
                                .commit();
                    }
                });
            }
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.cvImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.cvImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            Glide.with(ctx).load(modelList.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            holder.binding.ivIcon.setImageResource(R.drawable.add_icon);
            holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg);
        }

        @Override
        public int getItemCount() {
            return modelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            GlobalSearchLayoutBinding binding;

            public MyViewHolder(GlobalSearchLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    public class SuggestedAdpater extends RecyclerView.Adapter<SuggestedAdpater.MyViewHolder> {
        private List<SuggestedModel.ResponseData> listModel;
        Context ctx;

        public SuggestedAdpater(List<SuggestedModel.ResponseData> listModel, Context ctx) {
            this.listModel = listModel;
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
            holder.binding.tvTitle.setText(listModel.get(position).getName());
            holder.binding.tvTime.setText(listModel.get(position).getAudioDuration());

            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(listModel.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            holder.binding.ivIcon.setImageResource(R.drawable.add_icon);

            if (listModel.get(position).getIsLock().equalsIgnoreCase("1")) {
                if (listModel.get(position).getIsPlay().equalsIgnoreCase("1")) {
                    holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                    holder.binding.ivLock.setVisibility(View.GONE);
                } else if (listModel.get(position).getIsPlay().equalsIgnoreCase("0")
                        || listModel.get(position).getIsPlay().equalsIgnoreCase("")) {
                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                }
            } else if (listModel.get(position).getIsLock().equalsIgnoreCase("0")
                    || listModel.get(position).getIsLock().equalsIgnoreCase("")) {
                holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                holder.binding.ivLock.setVisibility(View.GONE);
            }

            holder.binding.llRemoveAudio.setOnClickListener(view -> {
                if (listModel.get(position).getIsLock().equalsIgnoreCase("1")) {
                    if (listModel.get(position).getIsPlay().equalsIgnoreCase("1")) {
                        holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                        holder.binding.ivLock.setVisibility(View.GONE);
                        String AudioID = listModel.get(position).getID();
                        if (BWSApplication.isNetworkConnected(ctx)) {
                            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            Call<SucessModel> listCall = APIClient.getClient().getAddSearchAudioFromPlaylist(UserID, AudioID, PlaylistID, "");
                            listCall.enqueue(new Callback<SucessModel>() {
                                @Override
                                public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                                    if (response.isSuccessful()) {
                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                        SucessModel listModel = response.body();
                                        BWSApplication.showToast(listModel.getResponseMessage(), ctx);
                                        finish();
                                    }
                                }

                                @Override
                                public void onFailure(Call<SucessModel> call, Throwable t) {
                                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                }
                            });
                        } else {
                            BWSApplication.showToast(ctx.getString(R.string.no_server_found), ctx);
                        }
                    } else if (listModel.get(position).getIsPlay().equalsIgnoreCase("0")
                            || listModel.get(position).getIsPlay().equalsIgnoreCase("")) {
                        holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
                        holder.binding.ivLock.setVisibility(View.VISIBLE);
                        BWSApplication.showToast("Please re-activate your membership plan", ctx);
                    }
                } else if (listModel.get(position).getIsLock().equalsIgnoreCase("0") || listModel.get(position).getIsLock().equalsIgnoreCase("")) {
                    String AudioID = listModel.get(position).getID();
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        Call<SucessModel> listCall = APIClient.getClient().getAddSearchAudioFromPlaylist(UserID, AudioID, PlaylistID, "");
                        listCall.enqueue(new Callback<SucessModel>() {
                            @Override
                            public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                                if (response.isSuccessful()) {
                                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                                    SucessModel listModel = response.body();
                                    BWSApplication.showToast(listModel.getResponseMessage(), ctx);
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<SucessModel> call, Throwable t) {
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            }
                        });
                    } else {
                        BWSApplication.showToast(ctx.getString(R.string.no_server_found), ctx);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if (10 > listModel.size()) {
                return listModel.size();
            } else {
                return 10;
            }
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            DownloadsLayoutBinding binding;

            public MyViewHolder(DownloadsLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    public class SuggestedPlayListsAdpater extends RecyclerView.Adapter<SuggestedPlayListsAdpater.MyViewHolder> {
        private List<SearchPlaylistModel.ResponseData> PlaylistModel;

        public SuggestedPlayListsAdpater(List<SearchPlaylistModel.ResponseData> PlaylistModel) {
            this.PlaylistModel = PlaylistModel;
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
                    holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                    holder.binding.ivLock.setVisibility(View.GONE);
                    addToSearch = true;
                    MyPlaylistIds = PlaylistModel.get(position).getID();
                    finish();
                    *//*Fragment myPlaylistsFragment = new MyPlaylistsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("New", "0");
                    bundle.putString("PlaylistID", PlaylistModel.get(position).getID());
                    bundle.putString("PlaylistName", PlaylistModel.get(position).getName());
                    bundle.putString("MyDownloads", "0");
                    myPlaylistsFragment.setArguments(bundle);
                    FragmentManager fragmentManager1 = getSupportFragmentManager();
                    fragmentManager1.beginTransaction()
                            .replace(R.id.flContainer, myPlaylistsFragment)
                            .commit();*//*
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
            if (10 > PlaylistModel.size()) {
                return PlaylistModel.size();
            } else {
                return 10;
            }
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