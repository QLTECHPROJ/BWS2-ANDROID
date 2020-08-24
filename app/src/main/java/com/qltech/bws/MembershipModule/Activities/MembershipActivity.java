package com.qltech.bws.MembershipModule.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.MembershipModule.Adapters.MembershipPlanAdapter;
import com.qltech.bws.MembershipModule.Adapters.SubscriptionAdapter;
import com.qltech.bws.MembershipModule.Models.MembershipPlanListModel;
import com.qltech.bws.MembershipModule.Models.SubscriptionModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.ActivityMembershipBinding;
import com.qltech.bws.databinding.MembershipPlanBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MembershipActivity extends AppCompatActivity {
    List<SubscriptionModel> listModelList = new ArrayList<>();
    ActivityMembershipBinding binding;
    SubscriptionAdapter subscriptionAdapter;
    MembershipPlanAdapter membershipPlanAdapter;
    Context ctx;
//    String PlanPosition, PlanID, PlanAmount, PlanCurrency, PlanInterval, PlanImage, PlanTenure, PlanNextRenewal, SubName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_membership);
        ctx = MembershipActivity.this;
        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Glide.with(ctx).load(R.drawable.loading).asGif().into(binding.ImgV);

        MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 29,
                5, 3, 1.1f, 29);
        binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        binding.ivRestaurantImage.setImageResource(R.drawable.ic_membership_banner);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
        binding.rvList.setLayoutManager(mLayoutManager);
        binding.rvList.setItemAnimator(new DefaultItemAnimator());

        binding.rvPlanList.setLayoutManager(mLayoutManager);
        binding.rvPlanList.setItemAnimator(new DefaultItemAnimator());
        prepareMembershipData();
    }

    private void prepareMembershipData() {
        showProgressBar();
        if (BWSApplication.isNetworkConnected(this)) {
            Call<MembershipPlanListModel> listCall = APIClient.getClient().getMembershipPlanList();
            listCall.enqueue(new Callback<MembershipPlanListModel>() {
                @Override
                public void onResponse(Call<MembershipPlanListModel> call, Response<MembershipPlanListModel> response) {
                    if (response.isSuccessful()) {
                        hideProgressBar();
                        MembershipPlanListModel membershipPlanListModel = response.body();
                        if (membershipPlanListModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                            Glide.with(getApplicationContext()).load(membershipPlanListModel.getResponseData().getImage()).asGif().into(binding.ivRestaurantImage);
                            binding.tvTitle.setText(membershipPlanListModel.getResponseData().getTitle());
                            binding.tvDesc.setText(membershipPlanListModel.getResponseData().getDesc());

                            membershipPlanAdapter = new MembershipPlanAdapter(membershipPlanListModel.getResponseData().getPlan(),ctx,binding.btnFreeJoin );
                            binding.rvPlanList.setAdapter(membershipPlanAdapter);

                            subscriptionAdapter = new SubscriptionAdapter(listModelList);
                            binding.rvList.setAdapter(subscriptionAdapter);

                        }
                    }
                }

                @Override
                public void onFailure(Call<MembershipPlanListModel> call, Throwable t) {
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
        }
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

    private void hideProgressBar() {
        binding.progressBarHolder.setVisibility(View.GONE);
        binding.ImgV.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void showProgressBar() {
        binding.progressBarHolder.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        binding.ImgV.setVisibility(View.VISIBLE);
        binding.ImgV.invalidate();
    } 
}