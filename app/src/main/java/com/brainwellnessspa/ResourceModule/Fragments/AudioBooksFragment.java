package com.brainwellnessspa.ResourceModule.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.R;
import com.brainwellnessspa.ResourceModule.Activities.ResourceDetailsActivity;
import com.brainwellnessspa.ResourceModule.Models.ResourceListModel;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.AudioBooksLayoutBinding;
import com.brainwellnessspa.databinding.FragmentAudioBooksBinding;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AudioBooksFragment extends Fragment {
    FragmentAudioBooksBinding binding;
    String audio_books, UserID, Category;
    private long mLastClickTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_audio_books, container, false);
        View view = binding.getRoot();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            audio_books = bundle.getString("audio_books");
            UserID = bundle.getString("UserID");
            Category = bundle.getString("Category");
        }

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        binding.rvAudioBooksList.setLayoutManager(manager);
        binding.rvAudioBooksList.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    @Override
    public void onResume() {
        prepareData();
        super.onResume();
    }

    void prepareData() {
        BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
        Call<ResourceListModel> listCall = APIClient.getClient().getResourcLists(UserID, CONSTANTS.FLAG_ONE, Category);
        listCall.enqueue(new Callback<ResourceListModel>() {
            @Override
            public void onResponse(Call<ResourceListModel> call, Response<ResourceListModel> response) {
                try {
                    ResourceListModel listModel = response.body();
                    if (listModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                        AudioBooksAdapter adapter = new AudioBooksAdapter(listModel.getResponseData(), getActivity(), audio_books);
                        binding.rvAudioBooksList.setAdapter(adapter);

                        if (listModel.getResponseData().size() != 0) {
                            binding.llError.setVisibility(View.GONE);
                            binding.rvAudioBooksList.setVisibility(View.VISIBLE);
                        } else {
                            binding.llError.setVisibility(View.VISIBLE);
                            binding.rvAudioBooksList.setVisibility(View.GONE);
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

    public class AudioBooksAdapter extends RecyclerView.Adapter<AudioBooksAdapter.MyViewHolder> {
        List<ResourceListModel.ResponseData> listModelList;
        Context ctx;
        String audio_books;

        public AudioBooksAdapter(List<ResourceListModel.ResponseData> listModelList, Context ctx, String audio_books) {
            this.listModelList = listModelList;
            this.ctx = ctx;
            this.audio_books = audio_books;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            AudioBooksLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.audio_books_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                    1, 1, 0.42f, 0);
            holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
            holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.binding.tvTitle.setText(listModelList.get(position).getTitle());
            holder.binding.tvCreator.setText(listModelList.get(position).getAuthor());
            Glide.with(ctx).load(listModelList.get(position).getImage()).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(40))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            holder.binding.rlMainLayout.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(getActivity(), ResourceDetailsActivity.class);
                i.putExtra("audio_books", audio_books);
                i.putExtra("id", listModelList.get(position).getID());
                i.putExtra("title", listModelList.get(position).getTitle());
                i.putExtra("author", listModelList.get(position).getAuthor());
                i.putExtra("linkOne", listModelList.get(position).getResourceLink1());
                i.putExtra("linkTwo", listModelList.get(position).getResourceLink2());
                i.putExtra("image", listModelList.get(position).getDetailimage());
                i.putExtra("description", listModelList.get(position).getDescription());
                i.putExtra("mastercat", listModelList.get(position).getMasterCategory());
                i.putExtra("subcat", listModelList.get(position).getSubCategory());
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
            });
        }
        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            AudioBooksLayoutBinding binding;

            public MyViewHolder(AudioBooksLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}