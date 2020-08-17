package com.qltech.bws.DashboardModule.Adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.qltech.bws.DashboardModule.Models.QueueModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.QueueListLayoutBinding;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.MyViewHolder> {
    private List<QueueModel> listModelList;
    Context ctx;
    MediaPlayer mPlayer;
    private static int endTime = 0;
    String url = "https://brainwellnessspa.com.au/Bws-consumer-panel/html/audio_file/Brain_Wellness_Spa_The_Happiness_Promise.mp3";

    public QueueAdapter(List<QueueModel> listModelList, Context ctx, MediaPlayer mPlayer,int endTime) {
        this.listModelList = listModelList;
        this.ctx = ctx;
        this.mPlayer = mPlayer;
        this.endTime = endTime;
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
        QueueModel listModel = listModelList.get(position);
        holder.binding.tvTitle.setText(listModel.getTitle());
        holder.binding.tvTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(endTime),
                TimeUnit.MILLISECONDS.toSeconds(endTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(endTime))));

        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                1, 1, 0.1f, 0);
        holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        holder.binding.ivRestaurantImage.setImageResource(R.drawable.square_logo);

        holder.binding.llMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPlayer.isPlaying() == true){
                    mPlayer.stop();
                    Toast.makeText(ctx, "Playing Song stop", Toast.LENGTH_SHORT).show();
                }else {
                    mPlayer.start();
                    Toast.makeText(ctx, "Playing Song start", Toast.LENGTH_SHORT).show();
                }
                mPlayer.start();
                Toast.makeText(ctx, "Playing Songgggg", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        QueueListLayoutBinding binding;

        public MyViewHolder(QueueListLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
