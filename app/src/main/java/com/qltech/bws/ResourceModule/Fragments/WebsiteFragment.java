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

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

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
    String website, UserID, Category;
    private long mLastClickTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_website, container, false);
        View view = binding.getRoot();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            website = bundle.getString("website");
            UserID = bundle.getString("UserID");
            Category = bundle.getString("Category");
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvWebsiteList.setLayoutManager(mLayoutManager);
        binding.rvWebsiteList.setItemAnimator(new DefaultItemAnimator());
        prepareData();
        return view;
    }

    void prepareData() {
        BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
        Call<ResourceListModel> listCall = APIClient.getClient().getResourcLists(UserID, CONSTANTS.FLAG_FOUR, Category);
        listCall.enqueue(new Callback<ResourceListModel>() {
            @Override
            public void onResponse(Call<ResourceListModel> call, Response<ResourceListModel> response) {
                if (response.isSuccessful()) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                    ResourceListModel listModel = response.body();
                    WebsiteAdapter adapter = new WebsiteAdapter(listModel.getResponseData(), getActivity(), website);
                    binding.rvWebsiteList.setAdapter(adapter);

                    if (listModel.getResponseData().size() != 0) {
                        binding.llError.setVisibility(View.GONE);
                        binding.rvWebsiteList.setVisibility(View.VISIBLE);
                    } else {
                        binding.llError.setVisibility(View.VISIBLE);
                        binding.rvWebsiteList.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResourceListModel> call, Throwable t) {
                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            }
        });
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
            if (!listModelList.get(position).getDescription().equalsIgnoreCase("")) {
                holder.binding.tvCreator.setVisibility(View.VISIBLE);
                holder.binding.tvCreator.setText(listModelList.get(position).getDescription());
            } else {
                holder.binding.tvCreator.setVisibility(View.GONE);
            }
            Glide.with(ctx).load(listModelList.get(position).getImage()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            holder.binding.rlMainLayout.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(getActivity(), ResourceDetailsActivity.class);
                i.putExtra("website", website);
                i.putExtra("title", listModelList.get(position).getTitle());
                i.putExtra("linkOne", listModelList.get(position).getResourceLink1());
                i.putExtra("linkTwo", listModelList.get(position).getResourceLink2());
                i.putExtra("image", listModelList.get(position).getImage());
                i.putExtra("description", listModelList.get(position).getDescription());
                startActivity(i);
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