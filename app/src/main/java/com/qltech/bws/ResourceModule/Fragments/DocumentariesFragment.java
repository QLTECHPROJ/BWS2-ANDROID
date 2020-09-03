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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.R;
import com.qltech.bws.ResourceModule.Activities.ResourceDetailsActivity;
import com.qltech.bws.ResourceModule.Models.ResourceListModel;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.DocumentariesListLayoutBinding;
import com.qltech.bws.databinding.FragmentDocumentariesBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentariesFragment extends Fragment {
    FragmentDocumentariesBinding binding;
    String documentaries, UserID, Category;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_documentaries, container, false);
        View view = binding.getRoot();
        Glide.with(getActivity()).load(R.drawable.loading).asGif().into(binding.ImgV);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            documentaries = bundle.getString("documentaries");
            UserID = bundle.getString("UserID");
            Category = bundle.getString("Category");
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvDocumentariesList.setLayoutManager(mLayoutManager);
        binding.rvDocumentariesList.setItemAnimator(new DefaultItemAnimator());
        prepareData();
        return view;
    }

    void prepareData() {
        showProgressBar();
        if (BWSApplication.isNetworkConnected(getActivity())) {
            Call<ResourceListModel> listCall = APIClient.getClient().getResourcLists(UserID, CONSTANTS.FLAG_TWO, Category);
            listCall.enqueue(new Callback<ResourceListModel>() {
                @Override
                public void onResponse(Call<ResourceListModel> call, Response<ResourceListModel> response) {
                    if (response.isSuccessful()) {
                        ResourceListModel listModel = response.body();
                        hideProgressBar();
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
                }

                @Override
                public void onFailure(Call<ResourceListModel> call, Throwable t) {
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
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 2,
                    4, 2, 0.8f, 2);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(ctx).load(listModelList.get(position).getImage()).thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            holder.binding.rlMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), ResourceDetailsActivity.class);
                    i.putExtra("documentaries", documentaries);
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
            DocumentariesListLayoutBinding binding;

            public MyViewHolder(DocumentariesListLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}