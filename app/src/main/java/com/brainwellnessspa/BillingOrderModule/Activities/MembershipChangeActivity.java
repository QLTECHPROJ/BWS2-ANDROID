package com.brainwellnessspa.BillingOrderModule.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.BillingOrderModule.Models.PlanListBillingModel;
import com.brainwellnessspa.MembershipModule.Activities.OrderSummaryActivity;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.databinding.ActivityMembershipChangeBinding;
import com.brainwellnessspa.databinding.MembershipPlanBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MembershipChangeActivity extends AppCompatActivity {
    ActivityMembershipChangeBinding binding;
    Context ctx;
    String UserID, ComeFrom;
    Activity activity;
    public static String renewPlanFlag, renewPlanId;
    MembershipPlanAdapter membershipPlanAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_membership_change);
        ctx = MembershipChangeActivity.this;
        activity = MembershipChangeActivity.this;
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        binding.llBack.setOnClickListener(view -> {
            callback();
        });

        if (getIntent() != null) {
            ComeFrom = getIntent().getStringExtra("ComeFrom");
        }

        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        binding.rvPlanList.setLayoutManager(mLayoutManager1);
        binding.rvPlanList.setItemAnimator(new DefaultItemAnimator());
        prepareMembershipData();
    }


    @Override
    public void onBackPressed() {
        callback();
    }

    private void callback() {
        if (ComeFrom.equalsIgnoreCase("Plan")) {
            finish();
        } else if (ComeFrom.equalsIgnoreCase("")) {
            Intent i = new Intent(ctx, BillingOrderActivity.class);
            startActivity(i);
            finish();
        } else {
            Intent i = new Intent(ctx, BillingOrderActivity.class);
            startActivity(i);
            finish();
        }

    }

    private void prepareMembershipData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<PlanListBillingModel> listCall = APIClient.getClient().getPlanListBilling(UserID);
            listCall.enqueue(new Callback<PlanListBillingModel>() {
                @Override
                public void onResponse(Call<PlanListBillingModel> call, Response<PlanListBillingModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            PlanListBillingModel membershipPlanListModel = response.body();
                            if (membershipPlanListModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                binding.tvTitle.setText(membershipPlanListModel.getResponseData().getTitle());
                                binding.tvDesc.setText(membershipPlanListModel.getResponseData().getDesc());
                                MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                                        5, 3, 1f, 0);
                                binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
                                binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
                                binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
                                binding.ivRestaurantImage.setImageResource(R.drawable.ic_membership_banner);
                                membershipPlanAdapter = new MembershipPlanAdapter(membershipPlanListModel.getResponseData().getPlan()
                                        , ctx, binding.btnFreeJoin);
                                binding.rvPlanList.setAdapter(membershipPlanAdapter);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<PlanListBillingModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), this);
        }
    }

    public class MembershipPlanAdapter extends RecyclerView.Adapter<MembershipPlanAdapter.MyViewHolder> {
        private ArrayList<PlanListBillingModel.ResponseData.Plan> listModelList;
        Context ctx;
        private int row_index = -1, pos = 0;
        Button btnFreeJoin;
        Intent i;

        public MembershipPlanAdapter(ArrayList<PlanListBillingModel.ResponseData.Plan> listModelList, Context ctx, Button btnFreeJoin) {
            this.listModelList = listModelList;
            this.ctx = ctx;
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
                    ChangeFunction(holder, listModel, position);
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
                finish();
            });
        }

        private void ChangeFunction(MyViewHolder holder, PlanListBillingModel.ResponseData.Plan listModel, int position) {
            holder.binding.llPlanSub.setBackgroundColor(ctx.getResources().getColor(R.color.blue));
            holder.binding.llFeatures.setVisibility(View.VISIBLE);
            holder.binding.tvPlanAmount.setTextColor(ctx.getResources().getColor(R.color.white));
            holder.binding.tvSubName.setTextColor(ctx.getResources().getColor(R.color.white));
            holder.binding.tvPlanInterval.setTextColor(ctx.getResources().getColor(R.color.white));
            holder.binding.llFeatures.setBackgroundColor(ctx.getResources().getColor(R.color.white));
            renewPlanFlag = listModel.getPlanFlag();
            renewPlanId = listModel.getPlanID();
            i = new Intent(ctx, OrderSummaryActivity.class);
            i.putExtra("comeFrom", "membership");
            i.putExtra("ComesTrue", ComeFrom);
            i.putParcelableArrayListExtra("PlanData", listModelList);
            i.putExtra("TrialPeriod", "");
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
}