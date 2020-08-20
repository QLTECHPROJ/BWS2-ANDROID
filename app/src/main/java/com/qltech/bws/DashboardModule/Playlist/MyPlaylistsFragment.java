package com.qltech.bws.DashboardModule.Playlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.qltech.bws.DashboardModule.Activities.MyPlaylistActivity;
import com.qltech.bws.DashboardModule.Adapters.SuggestionAudiosAdpater;
import com.qltech.bws.DashboardModule.Audio.PlayListsModel;
import com.qltech.bws.DashboardModule.Models.SucessModel;
import com.qltech.bws.DashboardModule.Models.SuggestionAudiosModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.FragmentMyPlaylistsBinding;
import com.qltech.bws.databinding.MyPlaylistLayoutBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPlaylistsFragment extends Fragment {
    FragmentMyPlaylistsBinding binding;
    String UserID;
    List<SuggestionAudiosModel> listModelList = new ArrayList<>();
    List<PlayListsModel> playLists = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_playlists, container, false);
        View view = binding.getRoot();

        Glide.with(getActivity()).load(R.drawable.loading).asGif().into(binding.ImgV);
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        binding.searchView.onActionViewExpanded();
        EditText searchEditText = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.gray));
        searchEditText.setHintTextColor(getResources().getColor(R.color.gray));
        ImageView closeButton = binding.searchView.findViewById(R.id.search_close_btn);
        binding.searchView.clearFocus();

        closeButton.setOnClickListener(v -> {
            binding.searchView.clearFocus();
            searchEditText.setText("");
            binding.searchView.setQuery("", false);
        });
        SuggestionAudiosAdpater suggestionAudiosAdpater = new SuggestionAudiosAdpater(listModelList, getActivity());
        RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvSuggestedList.setLayoutManager(recentlyPlayed);
        binding.rvSuggestedList.setItemAnimator(new DefaultItemAnimator());
        binding.rvSuggestedList.setAdapter(suggestionAudiosAdpater);

        PlayListsAdpater playListsAdpater = new PlayListsAdpater(playLists, getActivity(), UserID);
        RecyclerView.LayoutManager playLists = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvPlayLists.setLayoutManager(playLists);
        binding.rvPlayLists.setItemAnimator(new DefaultItemAnimator());
        binding.rvPlayLists.setAdapter(playListsAdpater);

        binding.ivStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.ivStatus.setImageResource(R.drawable.ic_play_icon);
            }
        });

        binding.llMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MyPlaylistActivity.class);
                startActivity(i);
            }
        });
        prepareSuggestionAudioData();
        preparePlayLists();
        return view;
    }

    private void prepareSuggestionAudioData() {
        SuggestionAudiosModel list = new SuggestionAudiosModel("I Can Attitude and Mind...", R.drawable.add_icon);
        listModelList.add(list);
        list = new SuggestionAudiosModel("Self-Discipline Program", R.drawable.add_icon);
        listModelList.add(list);
    }

    private void hideProgressBar() {
        binding.progressBarHolder.setVisibility(View.GONE);
        binding.ImgV.setVisibility(View.GONE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void showProgressBar() {
        binding.progressBarHolder.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        binding.ImgV.setVisibility(View.VISIBLE);
        binding.ImgV.invalidate();
    }

    private void preparePlayLists() {
        PlayListsModel list = new PlayListsModel("Home Maintenance");
        playLists.add(list);
        list = new PlayListsModel("Powerful and loving self...");
        playLists.add(list);
        list = new PlayListsModel("Focus Fixer Program");
        playLists.add(list);
        list = new PlayListsModel("Motivation Program");
        playLists.add(list);
        list = new PlayListsModel("Self-Discipline Program");
        playLists.add(list);
        list = new PlayListsModel("Love Thy Self.");
        playLists.add(list);
        list = new PlayListsModel("I Can Attitude and Mind...");
        playLists.add(list);
        list = new PlayListsModel("Passion Program");
        playLists.add(list);
        list = new PlayListsModel("Self-Discipline Program");
        playLists.add(list);
        list = new PlayListsModel("Love Thy Self.");
        playLists.add(list);
        list = new PlayListsModel("I Can Attitude and Mind...");
        playLists.add(list);
        list = new PlayListsModel("Passion Program");
        playLists.add(list);
    }

    public class PlayListsAdpater extends RecyclerView.Adapter<PlayListsAdpater.MyViewHolder> {
        private List<PlayListsModel> listModelList;
        Context ctx;
        String UserID;

        public PlayListsAdpater(List<PlayListsModel> listModelList, Context ctx, String UserID) {
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
            PlayListsModel listModel = listModelList.get(position);
            holder.binding.tvTitle.setText(listModel.getTitle());

            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.12f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.binding.ivRestaurantImage.setImageResource(R.drawable.square_logo);

            holder.binding.llRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   /* if (BWSApplication.isNetworkConnected(getActivity())) {
                        Call<SucessModel> listCall = APIClient.getClient().getRemoveAudioFromPlaylist(UserID, AudioId, PlaylistID);
                        listCall.enqueue(new Callback<SucessModel>() {
                            @Override
                            public void onResponse(Call<SucessModel> call, Response<SucessModel> response) {
                                if (response.isSuccessful()) {
                                    SucessModel listModel = response.body();


                                }
                            }

                            @Override
                            public void onFailure(Call<SucessModel> call, Throwable t) {
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
                    }*/
                }
            });
        }

        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            MyPlaylistLayoutBinding binding;

            public MyViewHolder(MyPlaylistLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}