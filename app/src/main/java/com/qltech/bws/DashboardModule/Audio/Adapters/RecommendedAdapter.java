package com.qltech.bws.DashboardModule.Audio.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.qltech.bws.DashboardModule.Activities.PlayWellnessActivity;
import com.qltech.bws.DashboardModule.Models.MainAudioModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.BigBoxLayoutBinding;

import java.util.List;

public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.MyViewHolder> {
    private List<MainAudioModel> listModelList;
    Context ctx;

    public RecommendedAdapter(List<MainAudioModel> listModelList, Context ctx) {
        this.listModelList = listModelList;
        this.ctx = ctx;
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
        MainAudioModel listModel = listModelList.get(position);
        holder.binding.tvTitle.setText(listModel.getTitle());

        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 20,
                1, 1, 0.48f, 20);
        holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        holder.binding.ivRestaurantImage.setImageResource(R.drawable.square_logo);

        holder.binding.llMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ctx, PlayWellnessActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                ctx.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        BigBoxLayoutBinding binding;

        public MyViewHolder(BigBoxLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
