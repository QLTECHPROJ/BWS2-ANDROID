package com.qltech.bws.DashboardModule.Playlist.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.DashboardModule.Models.MainPlayModel;
import com.qltech.bws.R;
import com.qltech.bws.databinding.PlaylistCustomLayoutBinding;

import java.util.List;

public class PlaylistAdapter  extends RecyclerView.Adapter<PlaylistAdapter.MyViewHolder>  {
    private List<MainPlayModel.ResponseData.Detail> listModelList;
    Context ctx;

    public PlaylistAdapter(List<MainPlayModel.ResponseData.Detail> listModelList, Context ctx) {
        this.listModelList = listModelList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PlaylistCustomLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.playlist_custom_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.tvPlaylistName.setText(listModelList.get(position).getLibraryName());
        Glide.with(ctx).load(listModelList.get(position).getPlaylistImage()).thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        PlaylistCustomLayoutBinding binding;

        public MyViewHolder(PlaylistCustomLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
