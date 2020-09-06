package com.qltech.bws.FaqModule.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.qltech.bws.FaqModule.Models.FaqListModel;
import com.qltech.bws.R;
import com.qltech.bws.databinding.AudioFaqLayoutBinding;

import java.util.List;

public class AudioFaqAdapter extends RecyclerView.Adapter<AudioFaqAdapter.MyViewHolder> {
    private List<FaqListModel.ResponseData> modelList;
    Context ctx;
    RecyclerView rvFaqList;
    TextView tvFound;

    public AudioFaqAdapter(List<FaqListModel.ResponseData> modelList, Context ctx, RecyclerView rvFaqList,
                           TextView tvFound) {
        this.modelList = modelList;
        this.ctx = ctx;
        this.rvFaqList = rvFaqList;
        this.tvFound = tvFound;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AudioFaqLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.audio_faq_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.tvTitle.setText(modelList.get(position).getTitle());
        holder.binding.tvDesc.setText(modelList.get(position).getDesc());
        holder.binding.ivClickRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.binding.llMainLayout.setBackgroundResource(R.color.discalimer_gray);
                holder.binding.tvDesc.setFocusable(true);
                holder.binding.tvDesc.requestFocus();
                holder.binding.tvDesc.setVisibility(View.VISIBLE);
                holder.binding.ivClickRight.setVisibility(View.GONE);
                holder.binding.ivClickDown.setVisibility(View.VISIBLE);
                holder.binding.ivClickDown.setImageResource(R.drawable.ic_down_black_icon);
            }
        });

        holder.binding.ivClickDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.binding.llMainLayout.setBackgroundResource(R.color.white);
                holder.binding.tvDesc.setVisibility(View.GONE);
                holder.binding.ivClickRight.setVisibility(View.VISIBLE);
                holder.binding.ivClickDown.setVisibility(View.GONE);
                holder.binding.ivClickDown.setImageResource(R.drawable.ic_back_black_icon);
            }
        });

        if (modelList.size() == 0) {
            tvFound.setVisibility(View.VISIBLE);
            rvFaqList.setVisibility(View.GONE);
        } else {
            tvFound.setVisibility(View.GONE);
            rvFaqList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        AudioFaqLayoutBinding binding;

        public MyViewHolder(AudioFaqLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
