package com.qltech.bws.ResourceModule.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.qltech.bws.R;
import com.qltech.bws.ResourceModule.Models.ResourceFilterModel;
import com.qltech.bws.ResourceModule.Models.ResourceListModel;
import com.qltech.bws.databinding.FilterListLayoutBinding;

import java.util.List;

public class ResourceFilterAdapter extends RecyclerView.Adapter<ResourceFilterAdapter.MyViewHolder> {
    private List<ResourceListModel.ResponseData> listModel;
    Context ctx;

    public ResourceFilterAdapter(List<ResourceListModel.ResponseData> listModel, Context ctx) {
        this.listModel = listModel;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FilterListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.filter_list_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.tvTitle.setText(listModel.get(position).getMasterCategory());
    }

    @Override
    public int getItemCount() {
        return listModel.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        FilterListLayoutBinding binding;

        public MyViewHolder(FilterListLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
