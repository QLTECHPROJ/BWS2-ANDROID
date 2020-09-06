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
import com.qltech.bws.BillingOrderModule.Activities.CancelMembershipActivity;
import com.qltech.bws.BillingOrderModule.Activities.MembershipChangeActivity;
import com.qltech.bws.BillingOrderModule.Models.CurrentPlanVieViewModel;
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

public class CurrentPlanFragment extends Fragment {
    FragmentCurrentPlanBinding binding;
    String UserID;
    private long mLastClickTime = 0;
    FeaturedListAdpater adpater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_current_plan, container, false);
        View view = binding.getRoot();

        MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 29,
                5, 3, 1.1f, 29);
        binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
        binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
        binding.ivRestaurantImage.setImageResource(R.drawable.current_plan_image);

        Glide.with(getActivity()).load(R.drawable.loading).asGif().into(binding.ImgV);
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        RecyclerView.LayoutManager serachList = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvFeatured.setLayoutManager(serachList);
        binding.rvFeatured.setItemAnimator(new DefaultItemAnimator());

        showProgressBar();
        if (BWSApplication.isNetworkConnected(getActivity())) {
            Call<CurrentPlanVieViewModel> listCall = APIClient.getClient().getCurrentPlanView(UserID);
            listCall.enqueue(new Callback<CurrentPlanVieViewModel>() {
                @Override
                public void onResponse(Call<CurrentPlanVieViewModel> call, Response<CurrentPlanVieViewModel> response) {
                    if (response.isSuccessful()) {
                        hideProgressBar();
                        CurrentPlanVieViewModel listModel = response.body();

                        binding.tvHeader.setText(listModel.getResponseData().getPlan());
                        if (listModel.getResponseData().getActivate().equalsIgnoreCase("")){
                            binding.tvPlan.setText("");
                            binding.tvPlan.setVisibility(View.GONE);
                        }else {
                            binding.tvPlan.setVisibility(View.VISIBLE);
                            binding.tvPlan.setText("Active Since: "+listModel.getResponseData().getActivate());
                        }
                        binding.tvSubName.setText(listModel.getResponseData().getSubtitle());
                        binding.tvPlanAmount.setText(listModel.getResponseData().getOrderTotal());
                        binding.tvPlanInterval.setText(listModel.getResponseData().getPlanStr());
                        binding.tvPayUsing.setText(listModel.getResponseData().getCardDigit());

                        if (listModel.getResponseData().getStatus().equalsIgnoreCase("1")){
                            binding.tvRecommended.setBackgroundResource(R.drawable.green_background);
                            binding.tvRecommended.setText(R.string.Active);
                            binding.btnCancelSubscrible.setVisibility(View.VISIBLE);
                            binding.btnPayNow.setVisibility(View.GONE);
                            binding.tvPayUsing.setVisibility(View.GONE);
                            binding.tvChangeCard.setVisibility(View.GONE);
                        }else if (listModel.getResponseData().getStatus().equalsIgnoreCase("2")){
                            binding.tvRecommended.setBackgroundResource(R.drawable.dark_blue_background);
                            binding.tvRecommended.setText(R.string.InActive);
                            binding.btnCancelSubscrible.setVisibility(View.GONE);
                            binding.btnPayNow.setVisibility(View.VISIBLE); /*membership-ordersummary - payment */
                            binding.tvPayUsing.setVisibility(View.VISIBLE);
                            binding.tvChangeCard.setVisibility(View.VISIBLE);
                        }else if (listModel.getResponseData().getStatus().equalsIgnoreCase("3")){
                            binding.tvRecommended.setBackgroundResource(R.drawable.yellow_background);
                            binding.tvRecommended.setText(R.string.Suspended);
                            binding.btnCancelSubscrible.setVisibility(View.GONE);
                            binding.btnPayNow.setVisibility(View.VISIBLE);/*payment screen api call*/
                            binding.tvPayUsing.setVisibility(View.VISIBLE);
                            binding.tvChangeCard.setVisibility(View.VISIBLE);
                        }else if (listModel.getResponseData().getStatus().equalsIgnoreCase("4")){
                            binding.tvRecommended.setBackgroundResource(R.drawable.dark_blue_background);
                            binding.tvRecommended.setText(R.string.Cancelled);
                            binding.btnCancelSubscrible.setVisibility(View.GONE);
                            binding.btnPayNow.setVisibility(View.VISIBLE);
                            binding.tvPayUsing.setVisibility(View.VISIBLE);
                            binding.tvChangeCard.setVisibility(View.VISIBLE);
                        }

                        adpater = new FeaturedListAdpater(listModel.getResponseData().getFeature());
                        binding.rvFeatured.setAdapter(adpater);
                    }
                }

                @Override
                public void onFailure(Call<CurrentPlanVieViewModel> call, Throwable t) {
                    hideProgressBar();
                }
            });
        } else {
            BWSApplication.showToast( getString(R.string.no_server_found), getActivity());
        }

        binding.btnCancelSubscrible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(getActivity(), CancelMembershipActivity.class);
                startActivity(i);
            }
        });

        binding.btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(getActivity(), MembershipChangeActivity.class);
                startActivity(i);
            }
        });

        binding.tvChangeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PaymentFragment paymentFragment = new PaymentFragment();
                Bundle bundle = new Bundle();
                paymentFragment.setArguments(bundle);
            }
        });
        return view;
    }

    private void hideProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.GONE);
            binding.ImgV.setVisibility(View.GONE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showProgressBar() {
        try {
            binding.progressBarHolder.setVisibility(View.VISIBLE);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            binding.ImgV.setVisibility(View.VISIBLE);
            binding.ImgV.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
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