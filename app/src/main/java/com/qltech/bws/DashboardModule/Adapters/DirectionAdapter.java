package com.qltech.bws.DashboardModule.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.qltech.bws.DashboardModule.Models.DirectionModel;
import com.qltech.bws.DashboardModule.Models.QueueModel;
import com.qltech.bws.R;
import com.qltech.bws.databinding.DirectionTagLayoutBinding;
import com.qltech.bws.databinding.QueueListLayoutBinding;

import java.util.List;

public class DirectionAdapter extends RecyclerView.Adapter<DirectionAdapter.MyViewHolder> {
    private List<DirectionModel.ResponseData> listModelList;
    Context ctx;

    public DirectionAdapter(List<DirectionModel.ResponseData> listModelList, Context ctx) {
        this.listModelList = listModelList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DirectionTagLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.direction_tag_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DirectionModel.ResponseData listModel = listModelList.get(position);
//        holder.binding.tvTitle.setText(listModel.getTitle());
    }

    @Override
    public int getItemCount() {
        return listModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        DirectionTagLayoutBinding binding;

        public MyViewHolder(DirectionTagLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
