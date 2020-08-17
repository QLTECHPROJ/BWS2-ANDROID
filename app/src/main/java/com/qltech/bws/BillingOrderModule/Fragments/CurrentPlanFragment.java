package com.qltech.bws.BillingOrderModule.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.qltech.bws.BillingOrderModule.Activities.CancelMembershipActivity;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.FragmentCurrentPlanBinding;

public class CurrentPlanFragment extends Fragment {
    FragmentCurrentPlanBinding binding;

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

        binding.btnCancelSubscrible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CancelMembershipActivity.class);
                startActivity(i);
            }
        });
        return view;
    }
}