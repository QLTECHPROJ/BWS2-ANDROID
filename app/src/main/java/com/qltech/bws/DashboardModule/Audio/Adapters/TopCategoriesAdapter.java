package com.qltech.bws.DashboardModule.Audio.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.DashboardModule.Activities.PlayWellnessActivity;
import com.qltech.bws.DashboardModule.Models.MainAudioModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.RoundBoxLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class TopCategoriesAdapter  extends RecyclerView.Adapter<TopCategoriesAdapter.MyViewHolder> {
    private ArrayList<MainAudioModel.ResponseData.Detail> listModelList;
    Context ctx;
    FragmentActivity activity;

    public TopCategoriesAdapter(ArrayList<MainAudioModel.ResponseData.Detail> listModelList, Context ctx, FragmentActivity activity) {
        this.listModelList = listModelList;
        this.ctx = ctx;
        this.activity = activity;
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
        holder.binding.tvTitle.setText(listModelList.get(position).getName());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        float width = (displayMetrics.widthPixels / displayMetrics.density);
        float prop = ((width - 12 - 4) / 2) / (width - 12);


        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 46,
                1, 1, prop, 36);
        holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        Glide.with(ctx).load(listModelList.get(position).getImageFile()).thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
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
        RoundBoxLayoutBinding binding;

        public MyViewHolder(RoundBoxLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
