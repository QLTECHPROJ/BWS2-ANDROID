package com.qltech.bws.ResourceModule.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.R;
import com.qltech.bws.ResourceModule.Activities.ResourceDetailsActivity;
import com.qltech.bws.ResourceModule.Models.ResourceListModel;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.AudioBooksLayoutBinding;
import com.qltech.bws.databinding.BannerImageBinding;
import com.qltech.bws.databinding.FragmentAudioBooksBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AudioBooksFragment extends Fragment {
    FragmentAudioBooksBinding binding;
    String audio_books, UserID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_audio_books, container, false);
        View view = binding.getRoot();
        Glide.with(getActivity()).load(R.drawable.loading).asGif().into(binding.ImgV);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            audio_books = bundle.getString("audio_books");
            UserID = bundle.getString("UserID");
        }


        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        binding.rvAudioBooksList.setLayoutManager(manager);
        binding.rvAudioBooksList.setItemAnimator(new DefaultItemAnimator());

        prepareData();
        return view;
    }

    void prepareData() {
        showProgressBar();
        if (BWSApplication.isNetworkConnected(getActivity())) {
            Call<ResourceListModel> listCall = APIClient.getClient().getResourcLists(UserID, CONSTANTS.FLAG_ONE,"");
            listCall.enqueue(new Callback<ResourceListModel>() {
                @Override
                public void onResponse(Call<ResourceListModel> call, Response<ResourceListModel> response) {
                    if (response.isSuccessful()) {
                        ResourceListModel listModel = response.body();
                        hideProgressBar();
                        AudioBooksAdapter adapter = new AudioBooksAdapter(listModel.getResponseData(), getActivity(), audio_books);
                        binding.rvAudioBooksList.setAdapter(adapter);
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
            /*RecyclerView.ViewHolder viewHolder = null;
            switch (viewType) {
                case ListItem.TYPE_GENERAL:
                    AudioBooksLayoutBinding v1 = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                            , R.layout.audio_books_layout, parent, false);
                    viewHolder = new GeneralViewHolder(v1);
                    break;

                case ListItem.TYPE_BANNER:
                    BannerImageBinding v2 = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                            R.layout.banner_image, parent, false);
                    viewHolder = new BannerViewHolder(v2);
                    break;
            }
            return viewHolder;*/
            AudioBooksLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.audio_books_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            /*switch (holder.getItemViewType()) {
                case ListItem.TYPE_GENERAL:
                    GeneralItem generalItem = (GeneralItem) consolidatedList.get(position);
                    GeneralViewHolder generalViewHolder = (GeneralViewHolder) holder;
                    generalViewHolder.binding.tvTitle.setText(generalItem.getPojoOfJsonArray().getTitle());
                    generalViewHolder.binding.tvCreator.setText(generalItem.getPojoOfJsonArray().getSubTitle());
                    generalViewHolder.binding.rlMainLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(getActivity(), ResourceDetailsActivity.class);
                            i.putExtra("audio_books", audio_books);
                            startActivity(i);
                        }
                    });
                    break;
                case ListItem.TYPE_BANNER:
                    break;
            }*/
            holder.binding.tvTitle.setText(listModelList.get(position).getTitle());
            holder.binding.tvCreator.setText(listModelList.get(position).getAuthor());
            Glide.with(ctx).load(listModelList.get(position).getImage()).thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
            holder.binding.rlMainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), ResourceDetailsActivity.class);
                    i.putExtra("audio_books",audio_books);
                    i.putExtra("title",listModelList.get(position).getTitle());
                    i.putExtra("author",listModelList.get(position).getAuthor());
                    i.putExtra("linkOne",listModelList.get(position).getResourceLink1());
                    i.putExtra("linkTwo",listModelList.get(position).getResourceLink2());
                    i.putExtra("image",listModelList.get(position).getImage());
                    i.putExtra("description",listModelList.get(position).getDescription());
                    startActivity(i);
                }
            });

        }

      /*  @Override
        public int getItemViewType(int position) {
            return consolidatedList.get(position).getType();
        }
*/
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

        public class BannerViewHolder extends RecyclerView.ViewHolder {
            BannerImageBinding binding;

            public BannerViewHolder(BannerImageBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}