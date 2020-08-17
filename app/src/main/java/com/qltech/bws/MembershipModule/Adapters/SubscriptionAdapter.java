package com.qltech.bws.MembershipModule.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.qltech.bws.MembershipModule.Models.SubscriptionModel;
import com.qltech.bws.R;
import com.qltech.bws.databinding.SubscribeBoxLayoutBinding;

import java.util.List;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.MyViewHolder> {
    private List<SubscriptionModel> listModelList;

    public SubscriptionAdapter(List<SubscriptionModel> listModelList) {
        this.listModelList = listModelList;
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
        SubscriptionModel listModel = listModelList.get(position);
        holder.binding.tvTitle.setText(listModel.getTitle());
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
