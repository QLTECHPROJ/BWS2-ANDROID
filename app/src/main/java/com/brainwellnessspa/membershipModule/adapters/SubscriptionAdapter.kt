package com.brainwellnessspa.membershipModule.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.PlanlistInappModel
import com.brainwellnessspa.databinding.SubscribeBoxLayoutBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class SubscriptionAdapter(private val listModelList: List<PlanlistInappModel.ResponseData.AudioFile>, var ctx: Context) : RecyclerView.Adapter<SubscriptionAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v: SubscribeBoxLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.subscribe_box_layout, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.tvTitle.text = listModelList[position].name
        Glide.with(ctx).load(listModelList[position].imageFile).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(12))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)
    }

    override fun getItemCount(): Int {
        return listModelList.size
    }

    inner class MyViewHolder(var binding: SubscribeBoxLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}