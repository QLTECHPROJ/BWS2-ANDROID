package com.qltech.bws.DashboardModule.Playlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.DashboardModule.Activities.AddAudioActivity;
import com.qltech.bws.DashboardModule.Activities.MyPlaylistActivity;
import com.qltech.bws.DashboardModule.Models.SubPlayListModel;
import com.qltech.bws.DashboardModule.Models.SucessModel;
import com.qltech.bws.DashboardModule.Models.SuggestionAudiosModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.DownloadsLayoutBinding;
import com.qltech.bws.databinding.FragmentMyPlaylistsBinding;
import com.qltech.bws.databinding.MyPlaylistLayoutBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPlaylistsFragment extends Fragment {
    FragmentMyPlaylistsBinding binding;
    String UserID, New, PlaylistID, PlaylistName = "", PlaylistImage;
    SuggestionAudiosAdpater adpater;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_playlists, container, false);
        View view = binding.getRoot();

        if (getArguments() != null) {
            New = getArguments().getString("New");
            PlaylistID = getArguments().getString("PlaylistID");
            PlaylistName = getArguments().getString("PlaylistName");
            PlaylistImage = getArguments().getString("PlaylistImage");
        }

        Glide.with(getActivity()).load(R.drawable.loading).asGif().into(binding.ImgV);
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        if (PlaylistName.equalsIgnoreCase("") || PlaylistName == null) {
            binding.tvLibraryName.setText(R.string.My_Playlist);
        } else {
            binding.tvLibraryName.setText(PlaylistName);
        }

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment playlistFragment = new PlaylistFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.rlPlaylist, playlistFragment)
                        .commit();
                Bundle bundle = new Bundle();
                bundle.putString("Check", "1");
                playlistFragment.setArguments(bundle);
            }
        });
        binding.tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AddAudioActivity.class);
                startActivity(i);
            }
        });


        binding.searchView.onActionViewExpanded();

        EditText searchEditText = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.gray));
        searchEditText.setHintTextColor(getResources().getColor(R.color.gray));
        ImageView closeButton = binding.searchView.findViewById(R.id.search_close_btn);
        binding.searchView.clearFocus();
        searchEditText.setClickable(false);
        searchEditText.setEnabled(false);
        closeButton.setOnClickListener(v -> {
            binding.searchView.clearFocus();
            searchEditText.setText("");
            binding.searchView.setQuery("", false);
        });
        searchEditText.setHint("Search for audio");
        RecyclerView.LayoutManager suggestedList = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvSuggestedList.setLayoutManager(suggestedList);
        binding.rvSuggestedList.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager playList = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvPlayLists.setLayoutManager(playList);
        binding.rvPlayLists.setItemAnimator(new DefaultItemAnimator());

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String search) {
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String search) {
                prepareSearchData(search);
                Log.e("searchsearch", "" + search);
                return false;
            }
        });

        binding.llMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MyPlaylistActivity.class);
                i.putExtra("PlaylistID",PlaylistID);
                startActivity(i);
            }
        });

        if (New.equalsIgnoreCase("1")) {
            binding.llAddAudio.setVisibility(View.VISIBLE);
            binding.llListing.setVisibility(View.GONE);
            binding.btnAddAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), AddAudioActivity.class);
                    startActivity(i);
                }
            });
        } else if (New.equalsIgnoreCase("0")) {
            binding.llAddAudio.setVisibility(View.GONE);
            binding.llListing.setVisibility(View.VISIBLE);
            prepareData(UserID, PlaylistID);
        }

        return view;
    }

    private void prepareSearchData(String search){
        showProgressBar();
        if (BWSApplication.isNetworkConnected(getActivity())) {
            Call<SuggestionAudiosModel> listCall = APIClient.getClient().getAddSearchAudio(search);
            listCall.enqueue(new Callback<SuggestionAudiosModel>() {
                @Override
                public void onResponse(Call<SuggestionAudiosModel> call, Response<SuggestionAudiosModel> response) {
                    if (response.isSuccessful()) {
                        hideProgressBar();
                        SuggestionAudiosModel listModel = response.body();
                        if (listModel != null) {
                            adpater = new SuggestionAudiosAdpater(listModel.getResponseData(), getActivity(), binding.rvSuggestedList, UserID);
                        }
                        binding.rvSuggestedList.setAdapter(adpater);
                    }
                }

                @Override
                public void onFailure(Call<SuggestionAudiosModel> call, Throwable t) {
                    hideProgressBar();
                }
            });
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
        }
    }
    private void prepareData(String UserID, String PlaylistID) {
        showProgressBar();
        if (BWSApplication.isNetworkConnected(getActivity())) {

            Call<SubPlayListModel> listCall = APIClient.getClient().getSubPlayLists(UserID, PlaylistID);
            listCall.enqueue(new Callback<SubPlayListModel>() {
                @Override
                public void onResponse(Call<SubPlayListModel> call, Response<SubPlayListModel> response) {
                    if (response.isSuccessful()) {
                        hideProgressBar();
                        SubPlayListModel listModel = response.body();

                        if (listModel.getResponseData().getTotalAudio().equalsIgnoreCase("") &&
                                listModel.getResponseData().getTotalhour().equalsIgnoreCase("")
                                && listModel.getResponseData().getTotalminute().equalsIgnoreCase("")) {
                            binding.tvLibraryDetail.setText("0 Audio | 0h 0m");
                        } else {
                            binding.tvLibraryDetail.setText(listModel.getResponseData().getTotalAudio() + " Audio | "
                                    + listModel.getResponseData().getTotalhour() + "h " + listModel.getResponseData().getTotalminute() + "m");
                        }

                        if (listModel.getResponseData().getPlaylistSongs().size() == 0) {
                            binding.llAddAudio.setVisibility(View.VISIBLE);
                            binding.llListing.setVisibility(View.GONE);
                            binding.btnAddAudio.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent i = new Intent(getActivity(), AddAudioActivity.class);
                                    startActivity(i);
                                }
                            });
                        } else if (New.equalsIgnoreCase("0")) {
                            binding.llAddAudio.setVisibility(View.GONE);
                            binding.llListing.setVisibility(View.VISIBLE);
                            PlayListsAdpater adapter = new PlayListsAdpater(listModel.getResponseData(), getActivity(), UserID);
                            binding.rvPlayLists.setAdapter(adapter);
                        }


                    }
                }

                @Override
                public void onFailure(Call<SubPlayListModel> call, Throwable t) {
                    hideProgressBar();
                }
            });
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
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

    public class PlayListsAdpater extends RecyclerView.Adapter<PlayListsAdpater.MyViewHolder> {
        private SubPlayListModel.ResponseData listModelList;
        Context ctx;
        String UserID;

        public PlayListsAdpater(SubPlayListModel.ResponseData listModelList, Context ctx, String UserID) {
            this.listModelList = listModelList;
            this.ctx = ctx;
            this.UserID = UserID;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            MyPlaylistLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.my_playlist_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvTitle.setText(listModelList.getPlaylistSongs().get(position).getName());
            holder.binding.tvTime.setText(listModelList.getPlaylistSongs().get(position).getAudioDuration());

            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(listModelList.getPlaylistSongs().get(position).getImageFile()).thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

            if (listModelList.getPlaylistSongs().get(position).getDownload().equalsIgnoreCase("1")) {
                holder.binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
                holder. binding.ivDownloads.setColorFilter(Color.argb(99, 99, 99, 99));
                holder.binding.ivDownloads.setAlpha(255);
                holder. binding.llDownload.setClickable(false);
                holder. binding.llDownload.setEnabled(false);
            } else if (!listModelList.getPlaylistSongs().get(position).getDownload().equalsIgnoreCase("")) {
                holder.binding.llDownload.setClickable(true);
                holder.binding.llDownload.setEnabled(true);
                holder.binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon);
            }

            holder.binding.llRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showProgressBar();
                    String AudioId = listModelList.getPlaylistSongs().get(position).getID();
                    if (BWSApplication.isNetworkConnected(getActivity())) {
                        Call<SucessModel> listCall = APIClient.getClient().getRemoveAudioFromPlaylist(UserID, AudioId, PlaylistID);
                        listCall.enqueue(new Callback<SucessModel>() {
                            @Override
                            public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                                if (response.isSuccessful()) {
                                    hideProgressBar();
                                    SucessModel listModel = response.body();
                                    prepareData(UserID, PlaylistID);
                                    Toast.makeText(getActivity(), listModel.getResponseMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onFailure(Call<SucessModel> call, Throwable t) {
                                hideProgressBar();
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return listModelList.getPlaylistSongs().size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            MyPlaylistLayoutBinding binding;

            public MyViewHolder(MyPlaylistLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    public class SuggestionAudiosAdpater extends RecyclerView.Adapter<SuggestionAudiosAdpater.MyViewHolder> {
        private List<SuggestionAudiosModel.ResponseData> modelList;
        private List<SuggestionAudiosModel.ResponseData> listFilterData;
        Context ctx;
        RecyclerView rvSuggestedList;
        String UserID;

        public SuggestionAudiosAdpater(List<SuggestionAudiosModel.ResponseData> modelList, Context ctx,
                                       RecyclerView rvSuggestedList, String UserID) {
            this.modelList = modelList;
            this.listFilterData = modelList;
            this.ctx = ctx;
            this.rvSuggestedList = rvSuggestedList;
            this.UserID = UserID;
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
            final SuggestionAudiosModel.ResponseData mData = listFilterData.get(position);
            holder.binding.ivIcon.setImageResource(R.drawable.add_icon);
            holder.binding.tvTitle.setText(modelList.get(position).getName());
            holder.binding.tvTime.setText(modelList.get(position).getAudioDuration());

            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(modelList.get(position).getImageFile()).thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

            holder.binding.llRemoveAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String AudioID = modelList.get(position).getID();
                    showProgressBar();
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        Call<SucessModel> listCall = APIClient.getClient().getAddSearchAudioFromPlaylist(UserID, AudioID, "");
                        listCall.enqueue(new Callback<SucessModel>() {
                            @Override
                            public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                                if (response.isSuccessful()) {
                                    hideProgressBar();
                                    SucessModel listModel = response.body();
//                                    showToast("Added to My Playlist.");
                                    showToast(listModel.getResponseMessage());

                                }
                            }

                            @Override
                            public void onFailure(Call<SucessModel> call, Throwable t) {
                                hideProgressBar();
                            }
                        });
                    } else {
                        Toast.makeText(ctx, ctx.getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        void showToast(String message) {
            Toast toast = new Toast(ctx);
            View view = LayoutInflater.from(ctx).inflate(R.layout.toast_layout, null);
            TextView tvMessage = view.findViewById(R.id.tvMessage);
            tvMessage.setText(message);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 35);
            toast.setView(view);
            toast.show();
        }

        @Override
        public int getItemCount() {
            return listFilterData.size();
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