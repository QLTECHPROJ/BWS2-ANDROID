/*
package com.brainwellnessspa.manageModule

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.MediaController
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.PlanlistInappModel
import com.brainwellnessspa.databinding.VideoSeriesBoxLayoutBinding

internal class VideoSeriesListAdapter(
    private val model: List<PlanlistInappModel.ResponseData.TestminialVideo>,
    var ctx: Context
) :
    RecyclerView.Adapter<VideoSeriesListAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v: VideoSeriesBoxLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.video_series_box_layout, parent, false
        )
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mediacontroller = MediaController(ctx)
        mediacontroller.setAnchorView(holder.bind.videoView)
        holder.bind.videoView.setMediaController(mediacontroller)
        holder.bind.videoView.setVideoURI(Uri.parse("8czMWUH7vW4"))
        holder.bind.videoView.requestFocus()

*/
/*
        holder.bind.videoView.setOnCompletionListener((MediaPlayer.OnCompletionListener) mp -> {
            Toast.makeText(getApplicationContext(), "Video over", Toast.LENGTH_SHORT).show();
            if (index++ == arrayList.size()) {
                index = 0;
                mp.release();
                Toast.makeText(getApplicationContext(), "Video over", Toast.LENGTH_SHORT).show();
            } else {
                holder.bind.videoView.setVideoURI(Uri.parse("8czMWUH7vW4"));
                holder.bind.videoView.start();
            }


        });
*//*

        holder.bind.videoView.setOnErrorListener { mp, what, extra ->
            Log.d("API123", "What $what extra $extra")
            false
        }
    }

    override fun getItemCount(): Int {
        return model.size
    }

    inner class MyViewHolder(var bind: VideoSeriesBoxLayoutBinding) : RecyclerView.ViewHolder(
        bind.root
    )
}*/
