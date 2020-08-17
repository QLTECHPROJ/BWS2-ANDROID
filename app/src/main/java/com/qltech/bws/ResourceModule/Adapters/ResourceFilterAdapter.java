package com.qltech.bws.ResourceModule.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.qltech.bws.R;
import com.qltech.bws.ResourceModule.Models.ResourceFilterModel;
import com.qltech.bws.databinding.FilterListLayoutBinding;

import java.util.List;

public class ResourceFilterAdapter extends RecyclerView.Adapter<ResourceFilterAdapter.MyViewHolder> {
    private List<ResourceFilterModel> listModelList;
    Context ctx;

    public ResourceFilterAdapter(List<ResourceFilterModel> listModelList, Context ctx) {
        this.listModelList = listModelList;
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
        ResourceFilterModel listModel = listModelList.get(position);
        holder.binding.tvTitle.setText(listModel.getTitle());
    }

    @Override
    public int getItemCount() {
        return listModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        FilterListLayoutBinding binding;

        public MyViewHolder(FilterListLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
