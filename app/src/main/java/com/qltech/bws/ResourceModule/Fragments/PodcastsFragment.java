package com.qltech.bws.ResourceModule.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qltech.bws.R;
import com.qltech.bws.ResourceModule.Activities.ResourceDetailsActivity;
import com.qltech.bws.ResourceModule.Models.AudioBooksModel;
import com.qltech.bws.ResourceModule.Models.PodcastsModel;
import com.qltech.bws.databinding.AudioBooksLayoutBinding;
import com.qltech.bws.databinding.FragmentPodcastsBinding;
import com.qltech.bws.databinding.PodcastsListLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class PodcastsFragment extends Fragment {
    FragmentPodcastsBinding binding;
    String podcasts;
    List<PodcastsModel> listModelList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_podcasts, container, false);
        View view = binding.getRoot();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            podcasts = bundle.getString("podcasts");
        }

        PodcastsAdapter adapter = new PodcastsAdapter(listModelList,getActivity(),podcasts);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvPodcastsList.setLayoutManager(mLayoutManager);
        binding.rvPodcastsList.setItemAnimator(new DefaultItemAnimator());
        binding.rvPodcastsList.setAdapter(adapter);
        preparePodcastsData();

        return view;
    }

    private void preparePodcastsData() {
        PodcastsModel list = new PodcastsModel("The Tony Robbins Podcast", "Tony Robbins");
        listModelList.add(list);
        list = new PodcastsModel("The Overwhelmed Brain", "Paul Colaianni");
        listModelList.add(list);
        list = new PodcastsModel("The Brendon Show", "Brendon");
        listModelList.add(list);
        list = new PodcastsModel("The Life Coach School", "Brooke Castillo");
        listModelList.add(list);
        list = new PodcastsModel("The Tony Robbins Podcast", "Tony Robbins");
        listModelList.add(list);
        list = new PodcastsModel("The Overwhelmed Brain", "Paul Colaianni");
        listModelList.add(list);
        list = new PodcastsModel("The Brendon Show", "Brendon");
        listModelList.add(list);
        list = new PodcastsModel("The Life Coach School", "Brooke Castillo");
        listModelList.add(list);
        list = new PodcastsModel("The Tony Robbins Podcast", "Tony Robbins");
        listModelList.add(list);
        list = new PodcastsModel("The Overwhelmed Brain", "Paul Colaianni");
        listModelList.add(list);
        list = new PodcastsModel("The Brendon Show", "Brendon");
        listModelList.add(list);
        list = new PodcastsModel("The Life Coach School", "Brooke Castillo");
        listModelList.add(list);
        list = new PodcastsModel("The Tony Robbins Podcast", "Tony Robbins");
        listModelList.add(list);
        list = new PodcastsModel("The Overwhelmed Brain", "Paul Colaianni");
        listModelList.add(list);
        list = new PodcastsModel("The Brendon Show", "Brendon");
        listModelList.add(list);
        list = new PodcastsModel("The Life Coach School", "Brooke Castillo");
        listModelList.add(list);
    }

    public class PodcastsAdapter extends RecyclerView.Adapter<PodcastsAdapter.MyViewHolder> {
        private List<PodcastsModel> listModelList;
        Context ctx;
        String podcasts;

        public PodcastsAdapter(List<PodcastsModel> listModelList, Context ctx, String podcasts) {
            this.listModelList = listModelList;
            this.ctx = ctx;
            this.podcasts = podcasts;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            PodcastsListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.podcasts_list_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            PodcastsModel listModel = listModelList.get(position);
            holder.binding.tvTitle.setText(listModel.getTitle());
            holder.binding.tvCreator.setText(listModel.getSubTitle());
            holder.binding.rlMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), ResourceDetailsActivity.class);
                    i.putExtra("podcasts",podcasts);
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            PodcastsListLayoutBinding binding;

            public MyViewHolder(PodcastsListLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}