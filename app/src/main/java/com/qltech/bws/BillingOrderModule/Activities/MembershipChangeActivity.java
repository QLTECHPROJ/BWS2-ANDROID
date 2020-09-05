package com.qltech.bws.BillingOrderModule.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.BillingOrderModule.Models.PlanListBillingModel;
import com.qltech.bws.MembershipModule.Activities.OrderSummaryActivity;
import com.qltech.bws.MembershipModule.Adapters.MembershipPlanAdapter;
import com.qltech.bws.MembershipModule.Adapters.SubscriptionAdapter;
import com.qltech.bws.MembershipModule.Models.MembershipPlanListModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.ActivityMembershipChangeBinding;
import com.qltech.bws.databinding.MembershipPlanBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MembershipChangeActivity extends AppCompatActivity {
    ActivityMembershipChangeBinding binding;
    Context ctx;
    String UserID;
    MembershipPlanAdapter membershipPlanAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership_change);

        ctx = MembershipChangeActivity.this;
        Glide.with(ctx).load(R.drawable.loading).asGif().into(binding.ImgV);

        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        binding.rvPlanList.setLayoutManager(mLayoutManager1);
        binding.rvPlanList.setItemAnimator(new DefaultItemAnimator());

    }

    private void prepareMembershipData() {
        showProgressBar();
        if (BWSApplication.isNetworkConnected(this)) {
            Call<PlanListBillingModel> listCall = APIClient.getClient().getPlanListBilling(UserID);
            listCall.enqueue(new Callback<PlanListBillingModel>() {
                @Override
                public void onResponse(Call<PlanListBillingModel> call, Response<PlanListBillingModel> response) {
                    if (response.isSuccessful()) {
                        hideProgressBar();
                        PlanListBillingModel membershipPlanListModel = response.body();
                        if (membershipPlanListModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                            binding.tvTitle.setText(membershipPlanListModel.getResponseData().getTitle());
                            binding.tvDesc.setText(membershipPlanListModel.getResponseData().getDesc());

                            membershipPlanAdapter = new MembershipPlanAdapter(membershipPlanListModel.getResponseData().getPlan()
                                    ,ctx,binding.btnFreeJoin, "");
                            binding.rvPlanList.setAdapter(membershipPlanAdapter);

                        }
                    }
                }

                @Override
                public void onFailure(Call<PlanListBillingModel> call, Throwable t) {
                    hideProgressBar();
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
        }
    }

    public class MembershipPlanAdapter extends RecyclerView.Adapter<MembershipPlanAdapter.MyViewHolder> {
        private ArrayList<PlanListBillingModel.ResponseData.Plan> listModelList;
        Context ctx;
        private int row_index = -1, pos = 0;
        Button btnFreeJoin;
        String TrialPeriod;
        Intent i;
        public String planFlag, planId;

        public MembershipPlanAdapter(ArrayList<PlanListBillingModel.ResponseData.Plan> listModelList, Context ctx, Button btnFreeJoin,
                                     String TrialPeriod) {
            this.listModelList = listModelList;
            this.ctx = ctx;
            this.TrialPeriod = TrialPeriod;
            this.btnFreeJoin = btnFreeJoin;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            MembershipPlanBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.membership_plan, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            PlanListBillingModel.ResponseData.Plan listModel = listModelList.get(position);
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

        private void ChangeFunction(MyViewHolder holder, PlanListBillingModel.ResponseData.Plan listModel, int position) {
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

    private void hideProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.GONE);
            binding.ImgV.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            binding.ImgV.setVisibility(View.VISIBLE);
            binding.ImgV.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}