package com.qltech.bws.MembershipModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.qltech.bws.BillingOrderModule.Activities.BillingOrderActivity;
import com.qltech.bws.MembershipModule.Models.MembershipPlanListModel;
import com.qltech.bws.R;
import com.qltech.bws.databinding.ActivityOrderSummaryBinding;

import java.util.ArrayList;

public class OrderSummaryActivity extends AppCompatActivity {
    ActivityOrderSummaryBinding binding;
    String TrialPeriod, comeFrom = "";
    private ArrayList<MembershipPlanListModel.Plan> listModelList;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_summary);

        if (getIntent() != null) {
            TrialPeriod = getIntent().getStringExtra("TrialPeriod");
            listModelList = getIntent().getParcelableArrayListExtra("PlanData");
            position = getIntent().getIntExtra("position", 0);
            if (getIntent().hasExtra("comeFrom")) {
                comeFrom = getIntent().getStringExtra("comeFrom");
            }
        }
        if (!comeFrom.equalsIgnoreCase("")) {
            binding.tvTrialPeriod.setVisibility(View.GONE);
        }
        binding.tvPlanInterval.setText(listModelList.get(position).getPlanInterval() + " Membership");
        binding.tvPlanTenure.setText(listModelList.get(position).getPlanTenure());
        binding.tvPlanNextRenewal.setText(listModelList.get(position).getPlanNextRenewal());
        binding.tvSubName.setText(listModelList.get(position).getSubName());
        binding.tvTrialPeriod.setText(TrialPeriod);
        binding.tvPlanAmount.setText("$" + listModelList.get(position).getPlanAmount());
        binding.tvTotalAmount.setText("$" + listModelList.get(position).getPlanAmount());

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!comeFrom.equalsIgnoreCase("")) {
                    Intent i = new Intent(OrderSummaryActivity.this, BillingOrderActivity.class);
                    i.putExtra("payment", 1);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(OrderSummaryActivity.this, CheckoutGetCodeActivity.class);
                    i.putParcelableArrayListExtra("PlanData", listModelList);
                    i.putExtra("TrialPeriod", TrialPeriod);
                    i.putExtra("position", position);
                    startActivity(i);
                }
            }
        });
    }
}