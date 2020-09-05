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
import com.qltech.bws.MembershipModule.Adapters.MembershipPlanAdapter;
import com.qltech.bws.MembershipModule.Adapters.SubscriptionAdapter;
import com.qltech.bws.MembershipModule.Models.MembershipPlanListModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.databinding.ActivityMembershipChangeBinding;

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