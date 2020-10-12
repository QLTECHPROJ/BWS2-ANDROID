package com.brainwellnessspa.MembershipModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import com.brainwellnessspa.BillingOrderModule.Activities.MembershipChangeActivity;
import com.brainwellnessspa.BillingOrderModule.Activities.PaymentActivity;
import com.brainwellnessspa.BillingOrderModule.Models.PlanListBillingModel;
import com.brainwellnessspa.MembershipModule.Models.MembershipPlanListModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ActivityOrderSummaryBinding;

import java.util.ArrayList;

public class OrderSummaryActivity extends AppCompatActivity {
    ActivityOrderSummaryBinding binding;
    String TrialPeriod, comeFrom = "", UserId, renewPlanFlag, renewPlanId, ComesTrue;
    private ArrayList<MembershipPlanListModel.Plan> listModelList;
    ArrayList<PlanListBillingModel.ResponseData.Plan> listModelList2;
    int position;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_summary);
        SharedPreferences shared1 = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserId = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        if (getIntent() != null) {
            TrialPeriod = getIntent().getStringExtra("TrialPeriod");
            renewPlanFlag = getIntent().getStringExtra("renewPlanFlag");
            renewPlanId = getIntent().getStringExtra("renewPlanId");
            position = getIntent().getIntExtra("position", 0);
            if (getIntent().hasExtra("comeFrom")) {
                comeFrom = getIntent().getStringExtra("comeFrom");
                listModelList2 = getIntent().getParcelableArrayListExtra("PlanData");
            } else {
                listModelList = getIntent().getParcelableArrayListExtra("PlanData");
            }
        }

        if (getIntent() != null) {
            ComesTrue = getIntent().getStringExtra("ComesTrue");
        }
        if (!comeFrom.equalsIgnoreCase("")) {
            binding.tvTrialPeriod.setVisibility(View.GONE);
            binding.tvPlanInterval.setText(listModelList2.get(position).getPlanInterval() + " Membership");
            binding.tvPlanTenure.setText(listModelList2.get(position).getPlanTenure());
            binding.tvPlanNextRenewal.setText(listModelList2.get(position).getPlanNextRenewal());
            binding.tvSubName.setText(listModelList2.get(position).getSubName());
            binding.tvPlanAmount.setText("$" + listModelList2.get(position).getPlanAmount());
            binding.tvTotalAmount.setText("$" + listModelList2.get(position).getPlanAmount());
        } else {
            binding.tvTrialPeriod.setVisibility(View.VISIBLE);
            binding.tvPlanInterval.setText(listModelList.get(position).getPlanInterval() + " Membership");
            binding.tvPlanTenure.setText(listModelList.get(position).getPlanTenure());
            binding.tvPlanNextRenewal.setText(listModelList.get(position).getPlanNextRenewal());
            binding.tvSubName.setText(listModelList.get(position).getSubName());
            binding.tvTrialPeriod.setText(TrialPeriod);
            binding.tvPlanAmount.setText("$" + listModelList.get(position).getPlanAmount());
            binding.tvTotalAmount.setText("$" + listModelList.get(position).getPlanAmount());
        }

        binding.llBack.setOnClickListener(view -> {
            if (!comeFrom.equalsIgnoreCase("")) {
                Intent i = new Intent(OrderSummaryActivity.this, MembershipChangeActivity.class);
                i.putExtra("ComeFrom", ComesTrue);
                startActivity(i);
                finish();
            } else {
                finish();
            }
        });

        binding.btnCheckout.setOnClickListener(view -> {
            if (!comeFrom.equalsIgnoreCase("")) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(OrderSummaryActivity.this, PaymentActivity.class);
                i.putExtra("ComesTrue", ComesTrue);
                i.putExtra("comeFrom", "membership");
                i.putParcelableArrayListExtra("PlanData", listModelList2);
                i.putExtra("TrialPeriod", "");
                i.putExtra("position", position);
                startActivity(i);
                finish();
            } else {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(OrderSummaryActivity.this, CheckoutGetCodeActivity.class);
                i.putParcelableArrayListExtra("PlanData", listModelList);
                i.putExtra("TrialPeriod", TrialPeriod);
                i.putExtra("position", position);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!comeFrom.equalsIgnoreCase("")) {
            Intent i = new Intent(OrderSummaryActivity.this, MembershipChangeActivity.class);
            i.putExtra("ComeFrom", ComesTrue);
            startActivity(i);
            finish();
        } else {
            finish();
        }
    }
}