package com.qltech.bws.MembershipModule.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.qltech.bws.MembershipModule.Activities.MembershipActivity;
import com.qltech.bws.MembershipModule.Activities.OrderSummaryActivity;
import com.qltech.bws.MembershipModule.Models.MembershipPlanListModel;
import com.qltech.bws.R;
import com.qltech.bws.databinding.MembershipPlanBinding;
import com.qltech.bws.databinding.SubscribeBoxLayoutBinding;

import java.util.List;

public class MembershipPlanAdapter extends RecyclerView.Adapter<com.qltech.bws.MembershipModule.Adapters.MembershipPlanAdapter.MyViewHolder> {
    private List<MembershipPlanListModel.Plan> listModelList;
    Context ctx;
    Button btnFreeJoin;

    public MembershipPlanAdapter(List<MembershipPlanListModel.Plan> listModelList, Context ctx, Button btnFreeJoin) {
        this.listModelList = listModelList;
        this.ctx = ctx;
        this.btnFreeJoin = btnFreeJoin;
    }

    @NonNull
    @Override
    public com.qltech.bws.MembershipModule.Adapters.MembershipPlanAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MembershipPlanBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , R.layout.membership_plan, parent, false);
        return new MembershipPlanAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull com.qltech.bws.MembershipModule.Adapters.MembershipPlanAdapter.MyViewHolder holder, int position) {
        MembershipPlanListModel.Plan listModel = listModelList.get(position);
//        holder.binding.tvTitle.setText(listModel.getTitle());

        holder.binding.tvPlanFeatures01.setText(listModel.getPlanFeatures().getFeature1());
        holder.binding.tvPlanFeatures02.setText(listModel.getPlanFeatures().getFeature2());
        holder.binding.tvPlanFeatures03.setText(listModel.getPlanFeatures().getFeature3());
        holder.binding.tvPlanFeatures04.setText(listModel.getPlanFeatures().getFeature4());
        holder.binding.tvPlanAmount.setText(listModel.getPlanAmount());
        holder.binding.tvSubName.setText(listModel.getSubName());
        holder.binding.tvPlanInterval.setText(listModel.getPlanInterval());

        if(listModel.getRecommendedFlag().equalsIgnoreCase("1")){
            holder.binding.tvRecommended.setVisibility(View.VISIBLE);
        }else{
            holder.binding.tvRecommended.setVisibility(View.GONE);
        }
        btnFreeJoin.setOnClickListener(view -> {
            Intent i = new Intent(ctx, OrderSummaryActivity.class);
            i.putExtra("PlanID",listModel.getPlanID());
            i.putExtra("PlanAmount",listModel.getPlanAmount());
            i.putExtra("PlanCurrency",listModel.getPlanCurrency());
            i.putExtra("PlanCurrency",listModel.getPlanCurrency());
            i.putExtra("PlanInterval",listModel.getPlanInterval());
            i.putExtra("PlanImage",listModel.getPlanImage());
            i.putExtra("PlanTenure",listModel.getPlanTenure());
            i.putExtra("PlanNextRenewal",listModel.getPlanNextRenewal());
            i.putExtra("SubName",listModel.getSubName());
            ctx.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return listModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        MembershipPlanBinding binding;

        public MyViewHolder(MembershipPlanBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
