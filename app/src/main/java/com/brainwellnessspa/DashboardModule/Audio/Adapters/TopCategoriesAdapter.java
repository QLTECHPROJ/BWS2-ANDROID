package com.brainwellnessspa.DashboardModule.Audio.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Audio.AudioFragment;
import com.brainwellnessspa.DashboardModule.Audio.ViewAllAudioFragment;
import com.brainwellnessspa.DashboardModule.Models.MainAudioModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.RoundBoxLayoutBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import static com.brainwellnessspa.BWSApplication.*;

public class TopCategoriesAdapter extends RecyclerView.Adapter<TopCategoriesAdapter.MyViewHolder> {
    Context ctx;
    FragmentActivity activity;
    String HomeID, Views;
    private ArrayList<MainAudioModel.ResponseData.Detail> listModelList;

    public TopCategoriesAdapter(ArrayList<MainAudioModel.ResponseData.Detail> listModelList, Context ctx, FragmentActivity activity,
                                String HomeID, String Views) {
        this.listModelList = listModelList;
        this.ctx = ctx;
        this.activity = activity;
        this.HomeID = HomeID;
        this.Views = Views;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RoundBoxLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.round_box_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.tvTitle.setText(listModelList.get(position).getCategoryName());
       /* MeasureRatio measureRatio = measureRatio(ctx, 16,
                1, 1, 0.52f, 10);
        holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());

*/
        holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(ctx).load(listModelList.get(position).getCatImage()).thumbnail(0.05f)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(124))).priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

        holder.binding.llMainLayout.setOnClickListener(view -> {
            Fragment viewAllAudioFragment = new ViewAllAudioFragment();
            FragmentManager fragmentManager1 = activity.getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .replace(R.id.flContainer, viewAllAudioFragment)
                    .commit();
            Bundle bundle = new Bundle();
            bundle.putString("ID", HomeID);
            bundle.putString("Name", Views);
            bundle.putString("Category", listModelList.get(position).getCategoryName());
            viewAllAudioFragment.setArguments(bundle);
        });
    }

    @Override
    public int getItemCount() {
        return listModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RoundBoxLayoutBinding binding;

        public MyViewHolder(RoundBoxLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}