package com.qltech.bws.DashboardModule.Audio.Adapters;

import android.content.Context;
import android.content.Intent;
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
import com.qltech.bws.DashboardModule.Activities.PlayWellnessActivity;
import com.qltech.bws.DashboardModule.Models.MainAudioModel;
import com.qltech.bws.DashboardModule.Playlist.PlaylistFragment;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.BigBoxLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class MyDownloadsAdapter extends RecyclerView.Adapter<MyDownloadsAdapter.MyViewHolder>  {
    private ArrayList<MainAudioModel.ResponseData.Detail> listModelList;
    Context ctx;
    FragmentActivity activity;

    public MyDownloadsAdapter(ArrayList<MainAudioModel.ResponseData.Detail> listModelList, Context ctx, FragmentActivity activity) {
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
        Fragment viewAllAudioFragment = new PlaylistFragment();
        FragmentManager fragmentManager1 = activity.getSupportFragmentManager();
        fragmentManager1.beginTransaction()
                .add(R.id.rlPlaylist, viewAllAudioFragment)
                .addToBackStack("ViewAllAudioFragment")
                .commit();
        holder.binding.llMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ctx, PlayWellnessActivity.class);
                i.putExtra("ID",listModelList.get(position).getID());
                i.putExtra("Name",listModelList.get(position).getName());
                i.putExtra("AudioFile",listModelList.get(position).getAudioFile());
                i.putExtra("ImageFile",listModelList.get(position).getImageFile());
                i.putExtra("AudioDirection",listModelList.get(position).getAudioDirection());
                i.putExtra("Audiomastercat",listModelList.get(position).getAudiomastercat());
                i.putExtra("AudioSubCategory",listModelList.get(position).getAudioSubCategory());
                i.putExtra("Like",listModelList.get(position).getLike());
                i.putExtra("Download",listModelList.get(position).getDownload());
                i.putExtra("position",position);
                i.putParcelableArrayListExtra("AudioList",listModelList);
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
