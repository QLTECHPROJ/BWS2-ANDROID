package com.brainwellnessspa.dashboardOldModule.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.R;
import com.brainwellnessspa.databinding.DirectionTagLayoutBinding;

import java.util.List;

public class DirectionAdapter extends RecyclerView.Adapter<DirectionAdapter.MyViewHolder> {
    private List<String> listModelList;
    Context ctx;

    public DirectionAdapter(List<String> listModelList, Context ctx) {
        this.listModelList = listModelList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DirectionTagLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.direction_tag_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.tvTitle.setText(listModelList.get(position));
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
