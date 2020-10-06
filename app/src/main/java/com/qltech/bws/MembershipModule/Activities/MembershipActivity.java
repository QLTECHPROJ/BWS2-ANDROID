package com.qltech.bws.MembershipModule.Activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.FaqModule.Models.FaqListModel;
import com.qltech.bws.MembershipModule.Adapters.MembershipPlanAdapter;
import com.qltech.bws.MembershipModule.Adapters.SubscriptionAdapter;
import com.qltech.bws.MembershipModule.Models.MembershipPlanListModel;
import com.qltech.bws.R;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.ActivityMembershipBinding;
import com.qltech.bws.databinding.AudioFaqLayoutBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MembershipActivity extends AppCompatActivity {
    ActivityMembershipBinding binding;
    SubscriptionAdapter subscriptionAdapter;
    MembershipPlanAdapter membershipPlanAdapter;
    Context ctx;
    MembershipFaqAdapter adapter;
    private long mLastClickTime = 0;
    Activity activity;
//    String PlanPosition, PlanID, PlanAmount, PlanCurrency, PlanInterval, PlanImage, PlanTenure, PlanNextRenewal, SubName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_membership);
        ctx = MembershipActivity.this;
        activity = MembershipActivity.this;
        binding.llBack.setOnClickListener(view -> finish());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
        binding.rvList.setLayoutManager(mLayoutManager);
        binding.rvList.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        binding.rvPlanList.setLayoutManager(mLayoutManager1);
        binding.rvPlanList.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager serachList = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        binding.rvFaqList.setLayoutManager(serachList);
        binding.rvFaqList.setItemAnimator(new DefaultItemAnimator());

        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<FaqListModel> listCall = APIClient.getClient().getFaqLists();
            listCall.enqueue(new Callback<FaqListModel>() {
                @Override
                public void onResponse(Call<FaqListModel> call, Response<FaqListModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        FaqListModel listModel = response.body();
                        binding.tvFaqTitle.setText(R.string.f_A_Q);
                        adapter = new MembershipFaqAdapter(listModel.getResponseData(), ctx, binding.rvFaqList, binding.tvFound);
                        binding.rvFaqList.setAdapter(adapter);
                    }
                }

                @Override
                public void onFailure(Call<FaqListModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), this);
        }
        prepareMembershipData();
    }

    private void prepareMembershipData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<MembershipPlanListModel> listCall = APIClient.getClient().getMembershipPlanList();
            listCall.enqueue(new Callback<MembershipPlanListModel>() {
                @Override
                public void onResponse(Call<MembershipPlanListModel> call, Response<MembershipPlanListModel> response) {
                    if (response.isSuccessful()) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                        MembershipPlanListModel membershipPlanListModel = response.body();
                        if (membershipPlanListModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                            binding.btnFreeJoin.setVisibility(View.VISIBLE);
                            binding.tvTitle.setText(membershipPlanListModel.getResponseData().getTitle());
                            binding.tvDesc.setText(membershipPlanListModel.getResponseData().getDesc());
                            binding.tvTag.setText("Access More Than 65 Audio Programs.");
                            binding.tvText.setText("By clicking on Register or Sign up you agree to our T&amp;Cs, Privacy Policy &amp; Disclaimer");

                            MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 29,
                                    5, 3, 1.1f, 29);
                            binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
                            binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
                            binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
                            binding.ivRestaurantImage.setImageResource(R.drawable.ic_membership_banner);

                            membershipPlanAdapter = new MembershipPlanAdapter(membershipPlanListModel.getResponseData().getPlan(), ctx, binding.btnFreeJoin,
                                    membershipPlanListModel.getResponseData().getTrialPeriod());
                            binding.rvPlanList.setAdapter(membershipPlanAdapter);

                            subscriptionAdapter = new SubscriptionAdapter(membershipPlanListModel.getResponseData().getAudioFiles(), ctx);
                            binding.rvList.setAdapter(subscriptionAdapter);

                        }
                    }
                }

                @Override
                public void onFailure(Call<MembershipPlanListModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), this);
        }
    }

    public class MembershipFaqAdapter extends RecyclerView.Adapter<MembershipFaqAdapter.MyViewHolder> {
        private List<FaqListModel.ResponseData> modelList;
        Context ctx;
        RecyclerView rvFaqList;
        TextView tvFound;

        public MembershipFaqAdapter(List<FaqListModel.ResponseData> modelList, Context ctx, RecyclerView rvFaqList, TextView tvFound) {
            this.modelList = modelList;
            this.ctx = ctx;
            this.rvFaqList = rvFaqList;
            this.tvFound = tvFound;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            AudioFaqLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.audio_faq_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvTitle.setText(modelList.get(position).getTitle());
            holder.binding.tvDesc.setText(modelList.get(position).getDesc());

            holder.binding.ivClickRight.setOnClickListener(view -> {
                holder.binding.llMainLayout.setBackgroundResource(R.color.discalimer_gray);
                holder.binding.tvDesc.setFocusable(true);
                holder.binding.tvDesc.requestFocus();
                holder.binding.tvDesc.setVisibility(View.VISIBLE);
                holder.binding.ivClickRight.setVisibility(View.GONE);
                holder.binding.ivClickDown.setVisibility(View.VISIBLE);
                holder.binding.ivClickDown.setImageResource(R.drawable.ic_down_black_icon);
            });

            holder.binding.ivClickDown.setOnClickListener(view -> {
                holder.binding.llMainLayout.setBackgroundResource(R.color.white);
                holder.binding.tvDesc.setVisibility(View.GONE);
                holder.binding.ivClickRight.setVisibility(View.VISIBLE);
                holder.binding.ivClickDown.setVisibility(View.GONE);
                holder.binding.ivClickDown.setImageResource(R.drawable.ic_back_black_icon);
            });

            if (modelList.size() == 0) {
                tvFound.setVisibility(View.GONE);
                rvFaqList.setVisibility(View.GONE);
            } else {
                tvFound.setVisibility(View.GONE);
                rvFaqList.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return modelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            AudioFaqLayoutBinding binding;

            public MyViewHolder(AudioFaqLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}