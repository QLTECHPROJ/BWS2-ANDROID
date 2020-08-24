package com.qltech.bws.MembershipModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.qltech.bws.R;
import com.qltech.bws.databinding.ActivityOrderSummaryBinding;

public class OrderSummaryActivity extends AppCompatActivity {

    ActivityOrderSummaryBinding binding;
    String PlanPosition, PlanID, PlanAmount, PlanCurrency, PlanInterval, PlanImage, PlanTenure, PlanNextRenewal, SubName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_summary);

        if (getIntent() != null) {
            PlanID = getIntent().getStringExtra("PlanID");
            PlanAmount = getIntent().getStringExtra("PlanAmount");
            PlanCurrency = getIntent().getStringExtra("PlanCurrency");
            PlanInterval = getIntent().getStringExtra("PlanInterval");
            PlanImage = getIntent().getStringExtra("PlanImage");
            PlanTenure = getIntent().getStringExtra("PlanTenure");
            PlanNextRenewal = getIntent().getStringExtra("PlanNextRenewal");
            SubName = getIntent().getStringExtra("SubName");
        }
        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OrderSummaryActivity.this, CheckoutGetCodeActivity.class);
                i.putExtra("PlanID", PlanID);
                i.putExtra("PlanAmount", PlanAmount);
                i.putExtra("PlanCurrency", PlanCurrency);
                i.putExtra("PlanCurrency", PlanCurrency);
                i.putExtra("PlanInterval", PlanInterval);
                i.putExtra("PlanImage", PlanImage);
                i.putExtra("PlanTenure", PlanTenure);
                i.putExtra("PlanNextRenewal", PlanNextRenewal);
                i.putExtra("SubName", SubName);
                startActivity(i);
            }
        });
    }
}