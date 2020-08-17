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
import com.qltech.bws.DashboardModule.Models.PopularModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.SmallBoxLayoutBinding;

import java.util.List;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.MyViewHolder> {
    private List<PopularModel> listModelList;
    Context ctx;

    public PopularAdapter(List<PopularModel> listModelList,Context ctx) {
        this.listModelList = listModelList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SmallBoxLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.small_box_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PopularModel listModel = listModelList.get(position);


        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 10,
                1, 1, 0.28f, 10);
        holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        holder.binding.ivRestaurantImage.setImageResource(R.drawable.square_logo);

        holder.binding.tvTitle.setText(listModel.getTitle());
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
        SmallBoxLayoutBinding binding;

        public MyViewHolder(SmallBoxLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
