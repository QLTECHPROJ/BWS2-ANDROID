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

import com.qltech.bws.MembershipModule.Activities.OrderSummaryActivity;
import com.qltech.bws.MembershipModule.Models.MembershipPlanListModel;
import com.qltech.bws.R;
import com.qltech.bws.databinding.MembershipPlanBinding;

import java.util.ArrayList;

public class MembershipPlanAdapter extends RecyclerView.Adapter<com.qltech.bws.MembershipModule.Adapters.MembershipPlanAdapter.MyViewHolder> {
    private ArrayList<MembershipPlanListModel.Plan> listModelList;
    Context ctx;
    private int row_index = -1, pos = 0;
    Button btnFreeJoin;
    String TrialPeriod;
    Intent i;
    public static String planFlag, planId;

    public MembershipPlanAdapter(ArrayList<MembershipPlanListModel.Plan> listModelList, Context ctx, Button btnFreeJoin, String TrialPeriod) {
        this.listModelList = listModelList;
        this.ctx = ctx;
        this.TrialPeriod = TrialPeriod;
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

        holder.binding.tvPlanFeatures01.setText(listModel.getPlanFeatures().get(0).getFeature());
        holder.binding.tvPlanFeatures02.setText(listModel.getPlanFeatures().get(1).getFeature());
        holder.binding.tvPlanFeatures03.setText(listModel.getPlanFeatures().get(2).getFeature());
        holder.binding.tvPlanFeatures04.setText(listModel.getPlanFeatures().get(3).getFeature());
        holder.binding.tvPlanAmount.setText("$" + listModel.getPlanAmount());
        holder.binding.tvSubName.setText(listModel.getSubName());
        holder.binding.tvPlanInterval.setText(listModel.getPlanInterval());

        if (listModel.getRecommendedFlag().equalsIgnoreCase("1")) {
            holder.binding.tvRecommended.setVisibility(View.VISIBLE);
          /*  if (pos == 0) {
                holder.binding.llPlanSub.setBackgroundColor(ctx.getResources().getColor(R.color.blue));
                holder.binding.llFeatures.setVisibility(View.VISIBLE);
                holder.binding.tvPlanAmount.setTextColor(ctx.getResources().getColor(R.color.white));
                holder.binding.tvSubName.setTextColor(ctx.getResources().getColor(R.color.white));
                holder.binding.tvPlanInterval.setTextColor(ctx.getResources().getColor(R.color.white));
                holder.binding.llFeatures.setBackgroundColor(ctx.getResources().getColor(R.color.white));
            }*/
        } else {
            holder.binding.tvRecommended.setVisibility(View.GONE);
        }
        holder.binding.llPlanMain.setOnClickListener(view -> {
            row_index = position;
            pos++;

            notifyDataSetChanged();
        });

        if (row_index == position) {
            ChangeFunction(holder, listModel, position);
        } else {
            if (listModel.getRecommendedFlag().equalsIgnoreCase("1") && pos == 0) {
                holder.binding.tvRecommended.setVisibility(View.VISIBLE);
                ChangeFunction(holder,listModel,position);
            } else {
                holder.binding.llPlanSub.setBackground(ctx.getResources().getDrawable(R.drawable.rounded_light_gray));
                holder.binding.tvPlanAmount.setTextColor(ctx.getResources().getColor(R.color.black));
                holder.binding.tvSubName.setTextColor(ctx.getResources().getColor(R.color.black));
                holder.binding.tvPlanInterval.setTextColor(ctx.getResources().getColor(R.color.black));
                holder.binding.llFeatures.setVisibility(View.GONE);
            }
        }
        btnFreeJoin.setOnClickListener(view -> {
            ctx.startActivity(i);
        });
    }

    private void ChangeFunction(MyViewHolder holder, MembershipPlanListModel.Plan listModel, int position) {
        holder.binding.llPlanSub.setBackgroundColor(ctx.getResources().getColor(R.color.blue));
        holder.binding.llFeatures.setVisibility(View.VISIBLE);
        holder.binding.tvPlanAmount.setTextColor(ctx.getResources().getColor(R.color.white));
        holder.binding.tvSubName.setTextColor(ctx.getResources().getColor(R.color.white));
        holder.binding.tvPlanInterval.setTextColor(ctx.getResources().getColor(R.color.white));
        holder.binding.llFeatures.setBackgroundColor(ctx.getResources().getColor(R.color.white));
        planFlag = listModel.getPlanFlag();
        planId = listModel.getPlanID();
        i = new Intent(ctx, OrderSummaryActivity.class);
        i.putParcelableArrayListExtra("PlanData", listModelList);
        i.putExtra("TrialPeriod", TrialPeriod);
        i.putExtra("position", position);
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
