package com.brainwellnessspa.dashboardModule.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.DirectionTagLayoutBinding

class DirectionAdapter(private val listModelList: List<String>, var ctx: Context) :
    RecyclerView.Adapter<DirectionAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v: DirectionTagLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.direction_tag_layout,
            parent,
            false
        )
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.tvTitle.text = listModelList[position]
    }

    override fun getItemCount(): Int {
        return listModelList.size
    }

    class MyViewHolder(var binding: DirectionTagLayoutBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}