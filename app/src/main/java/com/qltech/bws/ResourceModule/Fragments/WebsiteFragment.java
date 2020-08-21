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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.R;
import com.qltech.bws.ResourceModule.Activities.ResourceDetailsActivity;
import com.qltech.bws.ResourceModule.Models.ResourceListModel;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.FragmentWebsiteBinding;
import com.qltech.bws.databinding.WebsiteListLayoutBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebsiteFragment extends Fragment {
    FragmentWebsiteBinding binding;
    String website, UserID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_website, container, false);
        View view = binding.getRoot();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            website = bundle.getString("website");
            UserID = bundle.getString("UserID");
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvWebsiteList.setLayoutManager(mLayoutManager);
        binding.rvWebsiteList.setItemAnimator(new DefaultItemAnimator());
        prepareData();
        return view;
    }

    void prepareData() {
        if (BWSApplication.isNetworkConnected(getActivity())) {
            Call<ResourceListModel> listCall = APIClient.getClient().getResourcLists(UserID, CONSTANTS.FLAG_TWO,"");
            listCall.enqueue(new Callback<ResourceListModel>() {
                @Override
                public void onResponse(Call<ResourceListModel> call, Response<ResourceListModel> response) {
                    if (response.isSuccessful()) {
                        ResourceListModel listModel = response.body();
                        WebsiteAdapter adapter = new WebsiteAdapter(listModel.getResponseData(), getActivity(), website);
                        binding.rvWebsiteList.setAdapter(adapter);
                    }
                }

                @Override
                public void onFailure(Call<ResourceListModel> call, Throwable t) {
                }
            });
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
        }
    }

    public class WebsiteAdapter extends RecyclerView.Adapter<WebsiteAdapter.MyViewHolder> {
        private List<ResourceListModel.ResponseData> listModelList;
        Context ctx;
        String podcasts;

        public WebsiteAdapter(List<ResourceListModel.ResponseData> listModelList, Context ctx, String podcasts) {
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
            holder.binding.tvTitle.setText(listModelList.get(position).getTitle());
            holder.binding.tvCreator.setText(listModelList.get(position).getAuthor());
            Glide.with(ctx).load(listModelList.get(position).getImage()).thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            holder.binding.rlMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), ResourceDetailsActivity.class);
                    i.putExtra("website", website);
                    i.putExtra("title",listModelList.get(position).getTitle());
                    i.putExtra("linkOne",listModelList.get(position).getResourceLink1());
                    i.putExtra("linkTwo",listModelList.get(position).getResourceLink2());
                    i.putExtra("image",listModelList.get(position).getImage());
                    i.putExtra("description",listModelList.get(position).getDescription());
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