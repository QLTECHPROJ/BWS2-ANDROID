package com.qltech.bws.MembershipModule.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.qltech.bws.MembershipModule.Adapters.SubscriptionAdapter;
import com.qltech.bws.MembershipModule.Models.SubscriptionModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.ActivityMembershipBinding;

import java.util.ArrayList;
import java.util.List;

public class MembershipActivity extends AppCompatActivity {
    List<SubscriptionModel> listModelList = new ArrayList<>();
    ActivityMembershipBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_membership);

        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        MeasureRatio measureRatio = BWSApplication.measureRatio(MembershipActivity.this, 29,
                5, 3, 1.1f, 29);
        binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        binding.ivRestaurantImage.setImageResource(R.drawable.ic_membership_banner);

        binding.btnFreeJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MembershipActivity.this,OrderSummaryActivity.class);
                startActivity(i);
            }
        });
        SubscriptionAdapter adapter = new SubscriptionAdapter(listModelList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MembershipActivity.this, LinearLayoutManager.HORIZONTAL, false);
        binding.rvList.setLayoutManager(mLayoutManager);
        binding.rvList.setItemAnimator(new DefaultItemAnimator());
        binding.rvList.setAdapter(adapter);
        prepareMembershipData();
    }

    private void prepareMembershipData() {
        SubscriptionModel list = new SubscriptionModel("Monthly subscription");
        listModelList.add(list);
        list = new SubscriptionModel("Monthly subscription");
        listModelList.add(list);
        list = new SubscriptionModel("Monthly subscription");
        listModelList.add(list);
        list = new SubscriptionModel("Monthly subscription");
        listModelList.add(list);
        list = new SubscriptionModel("Monthly subscription");
        listModelList.add(list);
        list = new SubscriptionModel("Monthly subscription");
        listModelList.add(list);
        list = new SubscriptionModel("Monthly subscription");
        listModelList.add(list);
        list = new SubscriptionModel("Monthly subscription");
        listModelList.add(list);
    }
}