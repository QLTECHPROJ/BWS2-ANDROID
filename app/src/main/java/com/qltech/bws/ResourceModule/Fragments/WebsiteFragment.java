package com.qltech.bws.ResourceModule.Fragments;

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

import com.qltech.bws.R;
import com.qltech.bws.ResourceModule.Activities.ResourceDetailsActivity;
import com.qltech.bws.ResourceModule.Models.PodcastsModel;
import com.qltech.bws.ResourceModule.Models.WebsiteModel;
import com.qltech.bws.databinding.FragmentWebsiteBinding;
import com.qltech.bws.databinding.PodcastsListLayoutBinding;
import com.qltech.bws.databinding.WebsiteListLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class WebsiteFragment extends Fragment {
    FragmentWebsiteBinding binding;
    String website;
    List<WebsiteModel> listModelList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_website, container, false);
        View view = binding.getRoot();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            website = bundle.getString("website");
        }

        WebsiteAdapter adapter = new WebsiteAdapter(listModelList, getActivity(), website);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvWebsiteList.setLayoutManager(mLayoutManager);
        binding.rvWebsiteList.setItemAnimator(new DefaultItemAnimator());
        binding.rvWebsiteList.setAdapter(adapter);
        prepareWebsiteListsData();

        return view;
    }

    private void prepareWebsiteListsData() {
        WebsiteModel list = new WebsiteModel("Gaia", "We're dedicated to finding and creating informative and enlightening films, original...");
        listModelList.add(list);
        list = new WebsiteModel("Mind Valley", "The World's Best Personal Growth Programs - Learning Masterclass Videos/programs");
        listModelList.add(list);
        list = new WebsiteModel("Addicted To Success", "Addicted2Success is a Digital Media/News company that was founded in April 2011 by...");
        listModelList.add(list);
        list = new WebsiteModel("CBT Techniques & Worksheets", "Even if you're relatively unfamiliar with psychology, chances are you've heard of...");
        listModelList.add(list);
        list = new WebsiteModel("Gaia", "We're dedicated to finding and creating informative and enlightening films, original...");
        listModelList.add(list);
        list = new WebsiteModel("Mind Valley", "The World's Best Personal Growth Programs - Learning Masterclass Videos/programs");
        listModelList.add(list);
        list = new WebsiteModel("Addicted To Success", "Addicted2Success is a Digital Media/News company that was founded in April 2011 by...");
        listModelList.add(list);
        list = new WebsiteModel("CBT Techniques & Worksheets", "Even if you're relatively unfamiliar with psychology, chances are you've heard of...");
        listModelList.add(list);
    }

    public class WebsiteAdapter extends RecyclerView.Adapter<WebsiteAdapter.MyViewHolder> {
        private List<WebsiteModel> listModelList;
        Context ctx;
        String podcasts;

        public WebsiteAdapter(List<WebsiteModel> listModelList, Context ctx, String podcasts) {
            this.listModelList = listModelList;
            this.ctx = ctx;
            this.podcasts = podcasts;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            WebsiteListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.website_list_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            WebsiteModel listModel = listModelList.get(position);
            holder.binding.tvTitle.setText(listModel.getTitle());
            holder.binding.tvCreator.setText(listModel.getSubTitle());
            holder.binding.rlMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), ResourceDetailsActivity.class);
                    i.putExtra("website", website);
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            WebsiteListLayoutBinding binding;

            public MyViewHolder(WebsiteListLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }

    }
}