package com.brainwellnessspa.manageModule;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.MediaController;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.R;
import com.brainwellnessspa.dashboardModule.models.PlanlistInappModel;
import com.brainwellnessspa.databinding.VideoSeriesBoxLayoutBinding;

import java.util.List;

public class VideoSeriesListAdapter extends RecyclerView.Adapter<VideoSeriesListAdapter.MyViewHolder> {
    private final List<PlanlistInappModel.ResponseData.TestminialVideo> model;
    Context ctx;

    public VideoSeriesListAdapter(List<PlanlistInappModel.ResponseData.TestminialVideo> model, Context ctx) {
        this.model = model;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VideoSeriesBoxLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.video_series_box_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final MediaController mediacontroller = new MediaController(ctx);
        mediacontroller.setAnchorView(holder.bind.videoView);

        holder.bind.videoView.setMediaController(mediacontroller);
        holder.bind.videoView.setVideoURI(Uri.parse("8czMWUH7vW4"));
        holder.bind.videoView.requestFocus();

/*
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(getApplicationContext(), "Video over", Toast.LENGTH_SHORT).show();
                if (index++ == arrayList.size()) {
                    index = 0;
                    mp.release();
                    Toast.makeText(getApplicationContext(), "Video over", Toast.LENGTH_SHORT).show();
                } else {
                    videoView.setVideoURI(Uri.parse(arrayList.get(index)));
                    videoView.start();
                }
            }
        });
*/

        holder.bind.videoView.setOnErrorListener((mp, what, extra) -> {
            Log.d("API123", "What " + what + " extra " + extra);
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        VideoSeriesBoxLayoutBinding bind;

        public MyViewHolder(VideoSeriesBoxLayoutBinding bind) {
            super(bind.getRoot());
            this.bind = bind;
        }
    }
}

