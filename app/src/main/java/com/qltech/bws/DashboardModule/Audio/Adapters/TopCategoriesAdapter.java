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
import androidx.recyclerview.widget.RecyclerView;

import com.qltech.bws.DashboardModule.Activities.PlayWellnessActivity;
import com.qltech.bws.DashboardModule.Models.MainAudioModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.RoundBoxLayoutBinding;

import java.util.List;

public class TopCategoriesAdapter  extends RecyclerView.Adapter<TopCategoriesAdapter.MyViewHolder> {
    private List<MainAudioModel> listModelList;
    Context ctx;

    public TopCategoriesAdapter(List<MainAudioModel> listModelList, Context ctx) {
        this.listModelList = listModelList;
        this.ctx = ctx;
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
        MainAudioModel listModel = listModelList.get(position);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        float width = (displayMetrics.widthPixels / displayMetrics.density);
        float prop = ((width - 12 - 4) / 2) / (width - 12);


        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 46,
                1, 1, prop, 36);
        holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
//        holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);


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
        RoundBoxLayoutBinding binding;

        public MyViewHolder(RoundBoxLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
