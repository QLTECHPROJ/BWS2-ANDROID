package com.qltech.bws.DashboardModule.Audio.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.qltech.bws.DashboardModule.Models.MainAudioModel;
import com.qltech.bws.DashboardModule.TransparentPlayer.Fragments.TransparentPlayerFragment;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.RoundBoxLayoutBinding;

import java.util.ArrayList;

import static com.qltech.bws.DashboardModule.Activities.DashboardActivity.player;

public class TopCategoriesAdapter  extends RecyclerView.Adapter<TopCategoriesAdapter.MyViewHolder> {
    private ArrayList<MainAudioModel.ResponseData.Detail> listModelList;
    Context ctx;
    FragmentActivity activity;
    String ComeFrom;

    public TopCategoriesAdapter(ArrayList<MainAudioModel.ResponseData.Detail> listModelList, Context ctx, FragmentActivity activity,String ComeFrom) {
        this.listModelList = listModelList;
        this.ctx = ctx;
        this.activity = activity;
        this.ComeFrom = ComeFrom;
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
        Glide.with(ctx).load(listModelList.get(position).getImageFile()).thumbnail(0.05f)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
        holder.binding.llMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player = 1;
                Fragment fragment = new TransparentPlayerFragment();
                FragmentManager fragmentManager1 = activity.getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .add(R.id.rlAudiolist, fragment)
                        .addToBackStack("TransparentPlayerFragment")
                        .commit();
                SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                Gson gson = new Gson();
                String json = gson.toJson(listModelList.get(position));
                editor.putString(CONSTANTS.PREF_KEY_modelList, json);
                editor.putInt(CONSTANTS.PREF_KEY_position, position);
                editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "MainAudioList");
                editor.commit();
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
        RoundBoxLayoutBinding binding;

        public MyViewHolder(RoundBoxLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
