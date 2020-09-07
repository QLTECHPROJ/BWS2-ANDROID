package com.qltech.bws.DashboardModule.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.qltech.bws.DashboardModule.Models.AddToQueueModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.ItemMoveCallback;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.Utility.MusicService;
import com.qltech.bws.databinding.QueueListLayoutBinding;

import java.util.ArrayList;
import java.util.Collections;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.MyViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {
    ArrayList<AddToQueueModel> listModelList;
    Context ctx;


    public QueueAdapter(ArrayList<AddToQueueModel> listModelList, Context ctx) {
        this.listModelList = listModelList;
        this.ctx = ctx;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        QueueListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.queue_list_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AddToQueueModel listModel = listModelList.get(position);

        holder.binding.tvTitle.setText(listModel.getName());
        holder.binding.tvTime.setText(listModel.getAudioDuration());

        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                1, 1, 0.1f, 0);
        holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(ctx).load(listModel.getImageFile()).thumbnail(0.05f)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);

        holder.binding.llRemove.setOnClickListener(view -> callRemoveList(position));
        holder.binding.llMainLayout.setOnClickListener(view -> {
            MusicService.play(ctx, Uri.parse(listModel.getAudioFile()));
            MusicService.playMedia();
            SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, true);
            editor.putInt(CONSTANTS.PREF_KEY_position, position);
            editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, false);
            editor.commit();
        });

    }

    private void callRemoveList(int position) {
        listModelList.remove(position);
        notifyDataSetChanged();
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        Gson gson = new Gson();
        String json = gson.toJson(listModelList);
        editor.putString(CONSTANTS.PREF_KEY_queueList, json);
        editor.commit();
    }

    @Override
    public int getItemCount() {
        return listModelList.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(listModelList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(listModelList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        SharedPreferences shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        Gson gson = new Gson();
        String json = gson.toJson(listModelList);
        editor.putString(CONSTANTS.PREF_KEY_queueList, json);
        editor.commit();

    }

    @Override
    public void onRowSelected(RecyclerView.ViewHolder myViewHolder) {

    }

    @Override
    public void onRowClear(RecyclerView.ViewHolder myViewHolder) {

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        QueueListLayoutBinding binding;

        public MyViewHolder(QueueListLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
