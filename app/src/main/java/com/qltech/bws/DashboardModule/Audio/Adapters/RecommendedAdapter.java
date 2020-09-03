package com.qltech.bws.DashboardModule.Audio.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Models.MainAudioModel;
import com.qltech.bws.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.qltech.bws.R;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.BigBoxLayoutBinding;

import java.util.ArrayList;

public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.MyViewHolder> {
    private ArrayList<MainAudioModel.ResponseData.Detail> listModelList;
    Context ctx;
    FragmentActivity activity;

    public RecommendedAdapter(ArrayList<MainAudioModel.ResponseData.Detail> listModelList, Context ctx, FragmentActivity activity) {
        this.listModelList = listModelList;
        this.ctx = ctx;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BigBoxLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.big_box_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.tvTitle.setText(listModelList.get(position).getName());
        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 20,
                1, 1, 0.48f, 20);
        holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(ctx).load(listModelList.get(position).getImageFile()).thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

        holder.binding.llMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new TransparentPlayerFragment();
                FragmentManager fragmentManager1 = activity.getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .add(R.id.rlAudiolist, fragment)
                        .addToBackStack("TransparentPlayerFragment")
                        .commit();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("modelList", listModelList);
                bundle.putInt("position", position);
                bundle.putString("AudioFlag","MainAudio");
                fragment.setArguments(bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (4 > listModelList.size()) {
            return listModelList.size();
        } else {
            return 4;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        BigBoxLayoutBinding binding;

        public MyViewHolder(BigBoxLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
