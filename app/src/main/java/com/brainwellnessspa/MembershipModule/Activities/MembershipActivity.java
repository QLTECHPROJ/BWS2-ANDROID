package com.brainwellnessspa.MembershipModule.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.FaqModule.Models.FaqListModel;
import com.brainwellnessspa.MembershipModule.Adapters.MembershipPlanAdapter;
import com.brainwellnessspa.MembershipModule.Adapters.SubscriptionAdapter;
import com.brainwellnessspa.MembershipModule.Models.MembershipPlanListModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.APIClient;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.Utility.MeasureRatio;
import com.brainwellnessspa.WebView.TncActivity;
import com.brainwellnessspa.databinding.ActivityMembershipBinding;
import com.brainwellnessspa.databinding.AudioFaqLayoutBinding;
import com.segment.analytics.Properties;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_membership);
        ctx = MembershipActivity.this;
        activity = MembershipActivity.this;

        binding.llBack.setOnClickListener(view -> {
//            Intent i = new Intent(ctx, LoginActivity.class);
//            startActivity(i);
//            finish();
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
        binding.rvList.setLayoutManager(mLayoutManager);
        binding.rvList.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        binding.rvPlanList.setLayoutManager(mLayoutManager1);
        binding.rvPlanList.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager serachList = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        binding.rvFaqList.setLayoutManager(serachList);
        binding.rvFaqList.setItemAnimator(new DefaultItemAnimator());

        Properties p = new Properties();
        BWSApplication.addToSegment("Plan List Viewed", p, CONSTANTS.screen);

        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<FaqListModel> listCall = APIClient.getClient().getFaqListings();
            listCall.enqueue(new Callback<FaqListModel>() {
                @Override
                public void onResponse(Call<FaqListModel> call, Response<FaqListModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            FaqListModel listModel = response.body();
                            binding.tvFaqTitle.setText(R.string.f_A_Q);
                            adapter = new MembershipFaqAdapter(listModel.getResponseData(), ctx, binding.rvFaqList, binding.tvFound);
                            binding.rvFaqList.setAdapter(adapter);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<FaqListModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity);
        }
    }

    @Override
    protected void onResume() {
        prepareMembershipData();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
//        Intent i = new Intent(ctx, LoginActivity.class);
//        startActivity(i);
//        finish();
    }

    private void prepareMembershipData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<MembershipPlanListModel> listCall = APIClient.getClient().getMembershipPlanList();
            listCall.enqueue(new Callback<MembershipPlanListModel>() {
                @Override
                public void onResponse(Call<MembershipPlanListModel> call, Response<MembershipPlanListModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            MembershipPlanListModel membershipPlanListModel = response.body();
                            if (membershipPlanListModel.getResponseCode().equalsIgnoreCase(getString(R.string.ResponseCodesuccess))) {
                                binding.btnFreeJoin.setVisibility(View.VISIBLE);
                                binding.tvTitle.setText(membershipPlanListModel.getResponseData().getTitle());
                                binding.tvDesc.setText(membershipPlanListModel.getResponseData().getDesc());
                                binding.tvTag.setText(R.string.membership_title);
                                binding.tvText.setText(getString(R.string.privacy_policy_t_n_c));
                                binding.tvtncs.setText(getString(R.string.t_n_csm));
                                binding.tvPrivacyPolicys.setText(getString(R.string.privacy_policysm));
                                binding.tvAnd.setText(getString(R.string.and));
                                binding.tvDisclaimers.setText(R.string.disclaimers);
                                binding.tvtncs.getPaint().setUnderlineText(true);
                                binding.tvPrivacyPolicys.getPaint().setUnderlineText(true);
                                binding.tvDisclaimers.getPaint().setUnderlineText(true);
                                binding.tvtncs.setOnClickListener(view -> {
                                    Intent i = new Intent(ctx, TncActivity.class);
                                    i.putExtra(CONSTANTS.Web, "Tnc");
                                    startActivity(i);
                                });

                                binding.tvPrivacyPolicys.setOnClickListener(view -> {
                                    Intent i = new Intent(ctx, TncActivity.class);
                                    i.putExtra(CONSTANTS.Web, "PrivacyPolicy");
                                    startActivity(i);
                                });

                                binding.tvDisclaimers.setOnClickListener(view -> {
                                    final Dialog dialog = new Dialog(ctx);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(R.layout.full_desc_layout);
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue_gray)));
                                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                    final TextView tvTitle = dialog.findViewById(R.id.tvTitle);
                                    final TextView tvDesc = dialog.findViewById(R.id.tvDesc);
                                    final RelativeLayout tvClose = dialog.findViewById(R.id.tvClose);
                                    tvTitle.setText(R.string.Disclaimer);
                                    tvDesc.setText(R.string.Disclaimer_text);
                                    dialog.setOnKeyListener((v, keyCode, event) -> {
                                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                                            dialog.dismiss();
                                            return true;
                                        }
                                        return false;
                                    });

                                    tvClose.setOnClickListener(v -> dialog.dismiss());
                                    dialog.show();
                                    dialog.setCancelable(false);
                                });
                                MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                                        5, 3, 1f, 0);
                                binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
                                binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
                                binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
                                binding.ivRestaurantImage.setImageResource(R.drawable.ic_membership_banner);

                                membershipPlanAdapter = new MembershipPlanAdapter(membershipPlanListModel.getResponseData().getPlan(), ctx, binding.btnFreeJoin,
                                        membershipPlanListModel.getResponseData().getTrialPeriod(), activity);
                                binding.rvPlanList.setAdapter(membershipPlanAdapter);

//                                subscriptionAdapter = new SubscriptionAdapter(membershipPlanListModel.getResponseData().getAudioFiles(), ctx);
//                                binding.rvList.setAdapter(subscriptionAdapter);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<MembershipPlanListModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity);
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