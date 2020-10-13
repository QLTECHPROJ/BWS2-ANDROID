package com.brainwellnessspa.ResourceModule.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.R;
import com.brainwellnessspa.ResourceModule.Activities.ResourceDetailsActivity;
import com.brainwellnessspa.ResourceModule.Models.ResourceListModel;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.FragmentPodcastsBinding;
import com.brainwellnessspa.databinding.PodcastsListLayoutBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PodcastsFragment extends Fragment {
    FragmentPodcastsBinding binding;
    String podcasts, UserID, Category;
    private long mLastClickTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_podcasts, container, false);
        View view = binding.getRoot();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            podcasts = bundle.getString("podcasts");
            UserID = bundle.getString("UserID");
            Category = bundle.getString("Category");
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvPodcastsList.setLayoutManager(mLayoutManager);
        binding.rvPodcastsList.setItemAnimator(new DefaultItemAnimator());
        prepareData();
        return view;
    }

    void prepareData() {
        BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
        Call<ResourceListModel> listCall = APIClient.getClient().getResourcLists(UserID, CONSTANTS.FLAG_THREE, Category);
        listCall.enqueue(new Callback<ResourceListModel>() {
            @Override
            public void onResponse(Call<ResourceListModel> call, Response<ResourceListModel> response) {
                if (response.isSuccessful()) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                    ResourceListModel listModel = response.body();
                    PodcastsAdapter adapter = new PodcastsAdapter(listModel.getResponseData(), getActivity(), podcasts);
                    binding.rvPodcastsList.setAdapter(adapter);

                    if (listModel.getResponseData().size() != 0) {
                        binding.llError.setVisibility(View.GONE);
                        binding.rvPodcastsList.setVisibility(View.VISIBLE);
                    } else {
                        binding.llError.setVisibility(View.VISIBLE);
                        binding.rvPodcastsList.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResourceListModel> call, Throwable t) {
                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            }
        });
    }

    public class PodcastsAdapter extends RecyclerView.Adapter<PodcastsAdapter.MyViewHolder> {
        private List<ResourceListModel.ResponseData> listModelList;
        Context ctx;
        String podcasts;

        public PodcastsAdapter(List<ResourceListModel.ResponseData> listModelList, Context ctx, String podcasts) {
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
            holder.binding.tvTitle.setText(listModelList.get(position).getTitle());
            holder.binding.tvCreator.setText(listModelList.get(position).getAuthor());
            Glide.with(ctx).load(listModelList.get(position).getImage()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.binding.rlMainLayout.setElevation(10);
            }
            holder.binding.rlMainLayout.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(getActivity(), ResourceDetailsActivity.class);
                i.putExtra("podcasts", podcasts);
                i.putExtra("title", listModelList.get(position).getTitle());
                i.putExtra("author", listModelList.get(position).getAuthor());
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
            PodcastsListLayoutBinding binding;

            public MyViewHolder(PodcastsListLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}