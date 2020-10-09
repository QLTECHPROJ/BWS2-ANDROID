package com.qltech.bws.BillingOrderModule.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.qltech.bws.BillingOrderModule.Activities.BillingOrderActivity;
import com.qltech.bws.BillingOrderModule.Activities.CancelMembershipActivity;
import com.qltech.bws.BillingOrderModule.Activities.MembershipChangeActivity;
import com.qltech.bws.BillingOrderModule.Models.CurrentPlanVieViewModel;
import com.qltech.bws.BillingOrderModule.Models.PayNowDetailsModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.FeaturedLayoutBinding;
import com.qltech.bws.databinding.FragmentCurrentPlanBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.qltech.bws.BillingOrderModule.Activities.MembershipChangeActivity.renewPlanId;
import static com.qltech.bws.DashboardModule.Audio.AudioFragment.IsLock;

/*Active => Cancel button
remaining 10 days =>cancelled status=> pay now button => => Direct payment
after complete plan(10days)=>in active => pay now => plan selection
suspended => paynow => Direct payment*/

public class CurrentPlanFragment extends Fragment {
    FragmentCurrentPlanBinding binding;
    String UserID;
    private long mLastClickTime = 0;
    FeaturedListAdpater adpater;
    public static String invoicePayId, PlanStatus = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_current_plan, container, false);
        View view = binding.getRoot();

        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        RecyclerView.LayoutManager serachList = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvFeatured.setLayoutManager(serachList);
        binding.rvFeatured.setItemAnimator(new DefaultItemAnimator());
        PrepareData();
        binding.btnCancelSubscrible.setOnClickListener(view13 -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent i = new Intent(getActivity(), CancelMembershipActivity.class);
            startActivity(i);
        });

        binding.tvChangeCard.setOnClickListener(view12 -> {
            Intent i = new Intent(getActivity(), BillingOrderActivity.class);
            i.putExtra("payment", 1);
            startActivity(i);
            getActivity().finish();
        });

        return view;
    }

    private void PrepareData() {
        BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
        Call<CurrentPlanVieViewModel> listCall = APIClient.getClient().getCurrentPlanView(UserID);
        listCall.enqueue(new Callback<CurrentPlanVieViewModel>() {
            @Override
            public void onResponse(Call<CurrentPlanVieViewModel> call, Response<CurrentPlanVieViewModel> response) {
                if (response.isSuccessful()) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                    try {
                        CurrentPlanVieViewModel listModel = response.body();
                        binding.tvHeader.setText(listModel.getResponseData().getPlan());
                        MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 0,
                                5, 3, 1f, 0);
                        binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
                        binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
                        binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
                        binding.ivRestaurantImage.setImageResource(R.drawable.ic_membership_banner);

                        if (listModel.getResponseData().getActivate().equalsIgnoreCase("")) {
                            binding.tvPlan.setText("");
                            binding.tvPlan.setVisibility(View.GONE);
                        } else {
                            binding.tvPlan.setVisibility(View.VISIBLE);
                            binding.tvPlan.setText("Active Since: " + listModel.getResponseData().getActivate());
                        }

                        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE);
                        SharedPreferences.Editor editor = shared.edit();
                        editor.putString(CONSTANTS.PREF_KEY_ExpDate, listModel.getResponseData().getExpireDate());
                        editor.putString(CONSTANTS.PREF_KEY_IsLock, IsLock);
                        editor.commit();


                        if (listModel.getResponseData().getReattempt().equalsIgnoreCase("")) {
                            binding.tvSubName.setText(listModel.getResponseData().getSubtitle());
                        } else {
                            binding.tvSubName.setText(listModel.getResponseData().getReattempt());
                        }

                        binding.tvPlanAmount.setText("$" + listModel.getResponseData().getOrderTotal() + " ");
                        binding.tvPlanInterval.setText(listModel.getResponseData().getPlanStr());
                        binding.tvPayUsing.setText(listModel.getResponseData().getCardDigit());
                        invoicePayId = listModel.getResponseData().getInvoicePayId();
                        PlanStatus = listModel.getResponseData().getStatus();

                        if (listModel.getResponseData().getStatus().equalsIgnoreCase("1")) {
                            binding.tvRecommended.setBackgroundResource(R.drawable.green_background);
                            binding.tvRecommended.setText(R.string.Active);
                            binding.btnCancelSubscrible.setVisibility(View.VISIBLE);
                            binding.btnPayNow.setVisibility(View.GONE);
                            binding.tvPayUsing.setVisibility(View.GONE);
                            binding.tvChangeCard.setVisibility(View.GONE);
                        } else if (listModel.getResponseData().getStatus().equalsIgnoreCase("2")) {
                            binding.tvRecommended.setBackgroundResource(R.drawable.dark_brown_background);
                            binding.tvRecommended.setText(R.string.InActive);
                            binding.btnCancelSubscrible.setVisibility(View.GONE);
                            binding.btnPayNow.setVisibility(View.VISIBLE);
                            binding.tvPayUsing.setVisibility(View.GONE);
                            binding.tvChangeCard.setVisibility(View.GONE);

                            binding.btnPayNow.setOnClickListener(view1 -> {
                                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                    return;
                                }
                                mLastClickTime = SystemClock.elapsedRealtime();
                                Intent i = new Intent(getActivity(), MembershipChangeActivity.class);
                                startActivity(i);
                                getActivity().finish();
                            });
                        } else if (listModel.getResponseData().getStatus().equalsIgnoreCase("3")) {
                            binding.tvRecommended.setBackgroundResource(R.drawable.yellow_background);
                            binding.tvRecommended.setText(R.string.Suspended);
                            binding.btnCancelSubscrible.setVisibility(View.GONE);
                            binding.btnPayNow.setVisibility(View.VISIBLE);
                            binding.tvPayUsing.setVisibility(View.VISIBLE);
                            binding.tvChangeCard.setVisibility(View.VISIBLE);

                            binding.btnPayNow.setOnClickListener(view1 -> {
                                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                    return;
                                }
                                mLastClickTime = SystemClock.elapsedRealtime();
                                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                                Call<PayNowDetailsModel> listCall = APIClient.getClient().getPayNowDetails(UserID, listModel.getResponseData().getCardId(),
                                        listModel.getResponseData().getPlanId(), listModel.getResponseData().getPlanFlag(),
                                        listModel.getResponseData().getInvoicePayId(), listModel.getResponseData().getStatus());
                                listCall.enqueue(new Callback<PayNowDetailsModel>() {
                                    @Override
                                    public void onResponse(Call<PayNowDetailsModel> call, Response<PayNowDetailsModel> response) {
                                        if (response.isSuccessful()) {
                                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                                            PayNowDetailsModel listModel1 = response.body();
                                            BWSApplication.showToast(listModel1.getResponseMessage(), getActivity());
                                            getActivity().finish();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<PayNowDetailsModel> call, Throwable t) {
                                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
                                    }
                                });
                            });
                        } else if (listModel.getResponseData().getStatus().equalsIgnoreCase("4")) {
                            binding.tvRecommended.setBackgroundResource(R.drawable.dark_red_background);
                            binding.tvRecommended.setText(R.string.Cancelled);
                            binding.btnCancelSubscrible.setVisibility(View.GONE);
                            binding.btnPayNow.setVisibility(View.GONE);
                            binding.tvPayUsing.setVisibility(View.GONE);
                            binding.tvChangeCard.setVisibility(View.GONE);
                        }
                        adpater = new FeaturedListAdpater(listModel.getResponseData().getFeature());
                        binding.rvFeatured.setAdapter(adpater);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CurrentPlanVieViewModel> call, Throwable t) {
                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        PrepareData();
    }

    public class FeaturedListAdpater extends RecyclerView.Adapter<FeaturedListAdpater.MyViewHolder> {
        private List<CurrentPlanVieViewModel.ResponseData.Feature> modelList;

        public FeaturedListAdpater(List<CurrentPlanVieViewModel.ResponseData.Feature> modelList) {
            this.modelList = modelList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            FeaturedLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.featured_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvFeatured.setText(modelList.get(position).getFeature());
        }

        @Override
        public int getItemCount() {
            return modelList.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            FeaturedLayoutBinding binding;

            public MyViewHolder(FeaturedLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}