package com.brainwellnessspa.ResourceModule.Fragments;

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
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.R;
import com.brainwellnessspa.ResourceModule.Activities.ResourceDetailsActivity;
import com.brainwellnessspa.ResourceModule.Models.ResourceListModel;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.DocumentariesListLayoutBinding;
import com.brainwellnessspa.databinding.FragmentDocumentariesBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentariesFragment extends Fragment {
    FragmentDocumentariesBinding binding;
    String documentaries, UserID, Category;
    private long mLastClickTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_documentaries, container, false);
        View view = binding.getRoot();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            documentaries = bundle.getString("documentaries");
            UserID = bundle.getString("UserID");
            Category = bundle.getString("Category");
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvDocumentariesList.setLayoutManager(mLayoutManager);
        binding.rvDocumentariesList.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    @Override
    public void onResume() {
        prepareData();
        super.onResume();
    }

    void prepareData() {
        BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
        Call<ResourceListModel> listCall = APIClient.getClient().getResourcLists(UserID, CONSTANTS.FLAG_TWO, Category);
        listCall.enqueue(new Callback<ResourceListModel>() {
            @Override
            public void onResponse(Call<ResourceListModel> call, Response<ResourceListModel> response) {
                try {
                    if (response.isSuccessful()) {
                        ResourceListModel listModel = response.body();
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                        DocumentariesAdapter adapter = new DocumentariesAdapter(listModel.getResponseData(), getActivity(), documentaries);
                        binding.rvDocumentariesList.setAdapter(adapter);

                        if (listModel.getResponseData().size() != 0) {
                            binding.llError.setVisibility(View.GONE);
                            binding.rvDocumentariesList.setVisibility(View.VISIBLE);
                        } else {
                            binding.llError.setVisibility(View.VISIBLE);
                            binding.rvDocumentariesList.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResourceListModel> call, Throwable t) {
                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            }
        });
    }

    public class DocumentariesAdapter extends RecyclerView.Adapter<DocumentariesAdapter.MyViewHolder> {
        private List<ResourceListModel.ResponseData> listModelList;
        Context ctx;
        String documentaries;

        public DocumentariesAdapter(List<ResourceListModel.ResponseData> listModelList, Context ctx, String documentaries) {
            this.listModelList = listModelList;
            this.ctx = ctx;
            this.documentaries = documentaries;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            DocumentariesListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.documentaries_list_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvTitle.setText(listModelList.get(position).getTitle());
            holder.binding.tvCreator.setText(listModelList.get(position).getAuthor());
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    25, 11, 0.80f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(listModelList.get(position).getImage()).thumbnail(0.05f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            holder.binding.rlMainLayout.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(getActivity(), ResourceDetailsActivity.class);
                i.putExtra("documentaries", documentaries);
                i.putExtra("title", listModelList.get(position).getTitle());
                i.putExtra("author", listModelList.get(position).getAuthor());
                i.putExtra("linkOne", listModelList.get(position).getResourceLink1());
                i.putExtra("linkTwo", listModelList.get(position).getResourceLink2());
                i.putExtra("image", listModelList.get(position).getDetailimage());
                i.putExtra("description", listModelList.get(position).getDescription());
                startActivity(i);
            });
        }

        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            DocumentariesListLayoutBinding binding;

            public MyViewHolder(DocumentariesListLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}