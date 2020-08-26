package com.qltech.bws.BillingOrderModule.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.qltech.bws.BillingOrderModule.Activities.CancelMembershipActivity;
import com.qltech.bws.BillingOrderModule.Models.BillingAddressViewModel;
import com.qltech.bws.BillingOrderModule.Models.CurrentPlanVieViewModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.APIClient;
import com.qltech.bws.Utility.CONSTANTS;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.FragmentCurrentPlanBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrentPlanFragment extends Fragment {
    FragmentCurrentPlanBinding binding;
    String UserID;
    private long mLastClickTime = 0;

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

        showProgressBar();
        if (BWSApplication.isNetworkConnected(getActivity())) {
            Call<CurrentPlanVieViewModel> listCall = APIClient.getClient().getCurrentPlanView(UserID);
            listCall.enqueue(new Callback<CurrentPlanVieViewModel>() {
                @Override
                public void onResponse(Call<CurrentPlanVieViewModel> call, Response<CurrentPlanVieViewModel> response) {
                    if (response.isSuccessful()) {
                        hideProgressBar();
                        CurrentPlanVieViewModel listModel = response.body();
                        if (listModel.getResponseData().getOrderTotal().equalsIgnoreCase("")){
                            binding.tvDoller.setText("$0.00");
                        }else {
                            binding.tvDoller.setText("$" + listModel.getResponseData().getOrderTotal());
                        }
                        binding.tvDoller.setText("$" + listModel.getResponseData().getOrderTotal());
                        if (listModel.getResponseData().getFeature() == null || listModel.getResponseData().getFeature().equals("")) {
                            binding.llFeatured.setVisibility(View.GONE);
                        } else {
                            binding.llFeatured.setVisibility(View.VISIBLE);
                            binding.tvFeatures01.setText(listModel.getResponseData().getFeature().getFeature1());
                            binding.tvFeatures02.setText(listModel.getResponseData().getFeature().getFeature2());
                            binding.tvFeatures03.setText(listModel.getResponseData().getFeature().getFeature3());
                            binding.tvFeatures04.setText(listModel.getResponseData().getFeature().getFeature4());
                        }
                    }
                }

                @Override
                public void onFailure(Call<CurrentPlanVieViewModel> call, Throwable t) {
                    hideProgressBar();
                }
            });
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_server_found), Toast.LENGTH_SHORT).show();
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
}