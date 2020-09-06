package com.qltech.bws.DashboardModule.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Models.SucessModel;
import com.qltech.bws.DashboardModule.Models.SuggestedModel;
import com.qltech.bws.DashboardModule.Models.SuggestionAudiosModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.ActivityAddAudioBinding;
import com.qltech.bws.databinding.DownloadsLayoutBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAudioActivity extends AppCompatActivity {
    ActivityAddAudioBinding binding;
    Context ctx;
    String UserID, PlaylistID;
    SerachListAdpater adpater;
    EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_audio);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_audio);
        ctx = AddAudioActivity.this;

        if (getIntent().getExtras() != null) {
            PlaylistID = getIntent().getStringExtra(CONSTANTS.PlaylistID);
        }
        Glide.with(ctx).load(R.drawable.loading).asGif().into(binding.ImgV);
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
                    prepareSearchData(search, searchEditText, PlaylistID);
                }
                return false;
            }
        });

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RecyclerView.LayoutManager suggested = new LinearLayoutManager(AddAudioActivity.this, LinearLayoutManager.VERTICAL, false);
        binding.rvSuggestedList.setLayoutManager(suggested);
        binding.rvSuggestedList.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager serachList = new LinearLayoutManager(AddAudioActivity.this, LinearLayoutManager.VERTICAL, false);
        binding.rvSerachList.setLayoutManager(serachList);
        binding.rvSerachList.setItemAnimator(new DefaultItemAnimator());

        prepareSuggestedData();
    }

    private void prepareSearchData(String search, EditText searchEditText, String PlaylistID) {
        showProgressBar();
        if (BWSApplication.isNetworkConnected(ctx)) {
            Call<SuggestionAudiosModel> listCall = APIClient.getClient().getAddSearchAudio(search, PlaylistID);
            listCall.enqueue(new Callback<SuggestionAudiosModel>() {
                @Override
                public void onResponse(Call<SuggestionAudiosModel> call, Response<SuggestionAudiosModel> response) {
                    if (response.isSuccessful()) {
                        hideProgressBar();
                        SuggestionAudiosModel listModel = response.body();
                        if (!searchEditText.getText().toString().equalsIgnoreCase("")) {
                            if (listModel.getResponseData().size() == 0) {
                                binding.rvSerachList.setVisibility(View.GONE);
                                binding.llError.setVisibility(View.VISIBLE);
                                binding.tvFound.setText("Couldn't find '" + searchEditText.getText().toString() + "'");
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
                    hideProgressBar();
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), ctx);
        }
    }

    private void prepareSuggestedData() {
        showProgressBar();
        if (BWSApplication.isNetworkConnected(ctx)) {
            Call<SuggestedModel> listCall = APIClient.getClient().getSuggestedLists();
            listCall.enqueue(new Callback<SuggestedModel>() {
                @Override
                public void onResponse(Call<SuggestedModel> call, Response<SuggestedModel> response) {
                    if (response.isSuccessful()) {
                        hideProgressBar();
                        SuggestedModel listModel = response.body();
                        SuggestedAdpater suggestedAdpater = new SuggestedAdpater(listModel.getResponseData(), AddAudioActivity.this);
                        binding.rvSuggestedList.setAdapter(suggestedAdpater);
                    }
                }

                @Override
                public void onFailure(Call<SuggestedModel> call, Throwable t) {
                    hideProgressBar();
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

    private void hideProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.GONE);
            binding.ImgV.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            binding.ImgV.setVisibility(View.VISIBLE);
            binding.ImgV.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class SerachListAdpater extends RecyclerView.Adapter<SerachListAdpater.MyViewHolder> {
        private List<SuggestionAudiosModel.ResponseData> modelList;
        Context ctx;
        String UserID, PlaylistID;
        RecyclerView rvSerachList;

        public SerachListAdpater(List<SuggestionAudiosModel.ResponseData> modelList, Context ctx,
                                 RecyclerView rvSerachList, String UserID, String PlaylistID) {
            this.modelList = modelList;
            this.ctx = ctx;
            this.rvSerachList = rvSerachList;
            this.UserID = UserID;
            this.PlaylistID = PlaylistID;
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
            holder.binding.tvTitle.setText(modelList.get(position).getName());
            holder.binding.tvTime.setText(modelList.get(position).getAudioDuration());

            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(modelList.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            holder.binding.ivIcon.setImageResource(R.drawable.add_icon);
            holder.binding.llRemoveAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String AudioID = modelList.get(position).getID();
                    showProgressBar();
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        Call<SucessModel> listCall = APIClient.getClient().getAddSearchAudioFromPlaylist(UserID, AudioID, PlaylistID);
                        listCall.enqueue(new Callback<SucessModel>() {
                            @Override
                            public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                                if (response.isSuccessful()) {
                                    hideProgressBar();
                                    SucessModel listModel = response.body();
                                    BWSApplication.showToast(listModel.getResponseMessage(), ctx);
                                }
                            }

                            @Override
                            public void onFailure(Call<SucessModel> call, Throwable t) {
                                hideProgressBar();
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
            return modelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            DownloadsLayoutBinding binding;

            public MyViewHolder(DownloadsLayoutBinding binding) {
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
            holder.binding.llRemoveAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String AudioID = listModel.get(position).getID();
                    showProgressBar();
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        Call<SucessModel> listCall = APIClient.getClient().getAddSearchAudioFromPlaylist(UserID, AudioID, PlaylistID);
                        listCall.enqueue(new Callback<SucessModel>() {
                            @Override
                            public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                                if (response.isSuccessful()) {
                                    hideProgressBar();
                                    SucessModel listModel = response.body();
                                    BWSApplication.showToast(listModel.getResponseMessage(), ctx);
                                }
                            }

                            @Override
                            public void onFailure(Call<SucessModel> call, Throwable t) {
                                hideProgressBar();
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
            return listModel.size();
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