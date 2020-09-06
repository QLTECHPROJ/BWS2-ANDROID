package com.qltech.bws.DashboardModule.Search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Activities.AddPlaylistActivity;
import com.qltech.bws.DashboardModule.Models.SearchBothModel;
import com.qltech.bws.DashboardModule.Models.SearchPlaylistModel;
import com.qltech.bws.DashboardModule.Models.SuggestedModel;
import com.qltech.bws.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.DownloadsLayoutBinding;
import com.qltech.bws.databinding.FragmentSearchBinding;
import com.qltech.bws.databinding.GlobalSearchLayoutBinding;
import com.qltech.bws.databinding.PlaylistCustomLayoutBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {
    FragmentSearchBinding binding;
    private SearchViewModel searchViewModel;
    String UserID;
    EditText searchEditText;
    SerachListAdpater adpater;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        searchViewModel =
                ViewModelProviders.of(this).get(SearchViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);
        View view = binding.getRoot();
        Glide.with(getActivity()).load(R.drawable.loading).asGif().into(binding.ImgV);
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");

        if (!AudioFlag.equalsIgnoreCase("0")) {
            Fragment fragment = new TransparentPlayerFragment();
            FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .add(R.id.rlSearchList, fragment)
                    .addToBackStack("TransparentPlayerFragment")
                    .commit();
        }

        binding.searchView.onActionViewExpanded();
        searchEditText = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.gray));
        searchEditText.setHintTextColor(getResources().getColor(R.color.gray));
        ImageView closeButton = binding.searchView.findViewById(R.id.search_close_btn);
        binding.searchView.clearFocus();
        closeButton.setOnClickListener(v -> {
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
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

        RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvSerachList.setLayoutManager(recentlyPlayed);
        binding.rvSerachList.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvDownloadsList.setLayoutManager(layoutManager);
        binding.rvDownloadsList.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvPlayList.setItemAnimator(new DefaultItemAnimator());
        binding.rvPlayList.setLayoutManager(manager);
        prepareSuggestedAudioData();
        prepareSuggestedPlaylistData();
        searchViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return view;
    }

    private void prepareSearchData(String search, EditText searchEditText) {
        showProgressBar();
        if (BWSApplication.isNetworkConnected(getActivity())) {
            Call<SearchBothModel> listCall = APIClient.getClient().getSearchBoth(search);
            listCall.enqueue(new Callback<SearchBothModel>() {
                @Override
                public void onResponse(Call<SearchBothModel> call, Response<SearchBothModel> response) {
                    if (response.isSuccessful()) {
                        hideProgressBar();
                        SearchBothModel listModel = response.body();
                        if (!searchEditText.getText().toString().equalsIgnoreCase("")) {
                            if (listModel.getResponseData().size() == 0) {
                                binding.rvSerachList.setVisibility(View.GONE);
                                binding.llError.setVisibility(View.VISIBLE);
                                binding.tvFound.setText("Couldn't find '" + searchEditText.getText().toString() + "'");
                            } else {
                                binding.llError.setVisibility(View.GONE);
                                binding.rvSerachList.setVisibility(View.VISIBLE);
                                adpater = new SerachListAdpater(listModel.getResponseData(), getActivity(), binding.rvSerachList, UserID);
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
                    hideProgressBar();
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }

    private void prepareSuggestedAudioData() {
        showProgressBar();
        if (BWSApplication.isNetworkConnected(getActivity())) {
            Call<SuggestedModel> listCall = APIClient.getClient().getSuggestedLists();
            listCall.enqueue(new Callback<SuggestedModel>() {
                @Override
                public void onResponse(Call<SuggestedModel> call, Response<SuggestedModel> response) {
                    if (response.isSuccessful()) {
                        hideProgressBar();
                        SuggestedModel listModel = response.body();
                        SuggestionAudiosAdpater suggestedAdpater = new SuggestionAudiosAdpater(listModel.getResponseData(), getActivity(),
                                binding.rvDownloadsList);
                        binding.rvDownloadsList.setAdapter(suggestedAdpater);
                    }
                }

                @Override
                public void onFailure(Call<SuggestedModel> call, Throwable t) {
                    hideProgressBar();
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }

    private void prepareSuggestedPlaylistData() {
        showProgressBar();
        if (BWSApplication.isNetworkConnected(getActivity())) {
            Call<SearchPlaylistModel> listCall = APIClient.getClient().getSuggestedPlayLists();
            listCall.enqueue(new Callback<SearchPlaylistModel>() {
                @Override
                public void onResponse(Call<SearchPlaylistModel> call, Response<SearchPlaylistModel> response) {
                    if (response.isSuccessful()) {
                        hideProgressBar();
                        SearchPlaylistModel listModel = response.body();
                        SearchPlaylistAdapter suggestedAdpater = new SearchPlaylistAdapter(listModel.getResponseData());
                        binding.rvPlayList.setAdapter(suggestedAdpater);
                    }
                }

                @Override
                public void onFailure(Call<SearchPlaylistModel> call, Throwable t) {
                    hideProgressBar();
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), getActivity());
        }
    }

    private void hideProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.GONE);
            binding.ImgV.setVisibility(View.GONE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.VISIBLE);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            binding.ImgV.setVisibility(View.VISIBLE);
            binding.ImgV.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class SerachListAdpater extends RecyclerView.Adapter<SerachListAdpater.MyViewHolder> {
        private List<SearchBothModel.ResponseData> modelList;
        Context ctx;
        String UserID;
        RecyclerView rvSerachList;

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

            if (modelList.get(position).getIscategory().equalsIgnoreCase("1")) {
                holder.binding.tvPart.setText(R.string.Audio);
                holder.binding.llRemoveAudio.setVisibility(View.VISIBLE);
            } else if (modelList.get(position).getIscategory().equalsIgnoreCase("0")) {
                holder.binding.tvPart.setText(R.string.Playlist);
                holder.binding.llRemoveAudio.setVisibility(View.INVISIBLE);
            }
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(getActivity()).load(modelList.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            holder.binding.ivIcon.setImageResource(R.drawable.add_icon);

            holder.binding.llRemoveAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(ctx, AddPlaylistActivity.class);
                    i.putExtra("AudioId", modelList.get(position).getID());
                    startActivity(i);
                }
            });
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

    public class SuggestionAudiosAdpater extends RecyclerView.Adapter<SuggestionAudiosAdpater.MyViewHolder> {
        private List<SuggestedModel.ResponseData> modelList;
        Context ctx;
        RecyclerView rvDownloadsList;

        public SuggestionAudiosAdpater(List<SuggestedModel.ResponseData> modelList, Context ctx,
                                       RecyclerView rvDownloadsList) {
            this.modelList = modelList;
            this.ctx = ctx;
            this.rvDownloadsList = rvDownloadsList;
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
            Glide.with(getActivity()).load(modelList.get(position).getImageFile()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            holder.binding.ivIcon.setImageResource(R.drawable.add_icon);

            holder.binding.llRemoveAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(ctx, AddPlaylistActivity.class);
                    i.putExtra("AudioId", modelList.get(position).getID());
                    startActivity(i);
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


    public class SearchPlaylistAdapter extends RecyclerView.Adapter<SearchPlaylistAdapter.MyViewHolder> {
        private List<SearchPlaylistModel.ResponseData> listModelList;

        public SearchPlaylistAdapter(List<SearchPlaylistModel.ResponseData> listModelList) {
            this.listModelList = listModelList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            PlaylistCustomLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.playlist_custom_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 0,
                    1, 1, 0.45f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);

            MeasureRatio measureRatio1 = BWSApplication.measureRatio(getActivity(), 0,
                    1, 1, 0.45f, 0);
            holder.binding.rlMainLayout.getLayoutParams().height = (int) (measureRatio1.getHeight() * measureRatio1.getRatio());
            holder.binding.rlMainLayout.getLayoutParams().width = (int) (measureRatio1.getWidthImg() * measureRatio1.getRatio());

            holder.binding.tvPlaylistName.setText(listModelList.get(position).getName());
            Glide.with(getActivity()).load(listModelList.get(position).getImage()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

            holder.binding.rlMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            PlaylistCustomLayoutBinding binding;

            public MyViewHolder(PlaylistCustomLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}