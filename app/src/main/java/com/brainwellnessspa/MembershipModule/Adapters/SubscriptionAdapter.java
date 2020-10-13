package com.brainwellnessspa.MembershipModule.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.brainwellnessspa.MembershipModule.Models.MembershipPlanListModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.databinding.SubscribeBoxLayoutBinding;

import java.util.ArrayList;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.MyViewHolder> {
    private ArrayList<MembershipPlanListModel.AudioFile> listModelList;
    Context ctx;

    public SubscriptionAdapter(ArrayList<MembershipPlanListModel.AudioFile> listModelList, Context ctx) {
        this.listModelList = listModelList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SubscribeBoxLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.subscribe_box_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MembershipPlanListModel.AudioFile listModel = listModelList.get(position);
        holder.binding.tvTitle.setText(listModel.getName());
        Glide.with(ctx).load(listModel.getImageFile()).thumbnail(0.05f)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
    }

    @Override
    public int getItemCount() {
        return listModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        SubscribeBoxLayoutBinding binding;

        public MyViewHolder(SubscribeBoxLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}