package com.qltech.bws.DashboardModule.Playlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.qltech.bws.DashboardModule.Playlist.Activities.MyPlaylistActivity;
import com.qltech.bws.DashboardModule.Adapters.SuggestionAudiosAdpater;
import com.qltech.bws.DashboardModule.Audio.PlayListsModel;
import com.qltech.bws.DashboardModule.Models.SuggestionAudiosModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.FragmentMyPlaylistsBinding;
import com.qltech.bws.databinding.MyPlaylistLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class MyPlaylistsFragment extends Fragment {
    FragmentMyPlaylistsBinding binding;
    List<SuggestionAudiosModel> listModelList = new ArrayList<>();
    List<PlayListsModel> playLists = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_playlists, container, false);
        View view = binding.getRoot();

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

        PlayListsAdpater playListsAdpater = new PlayListsAdpater(playLists, getActivity());
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

        public PlayListsAdpater(List<PlayListsModel> listModelList, Context ctx) {
            this.listModelList = listModelList;
            this.ctx = ctx;
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