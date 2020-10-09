package com.qltech.bws.DashboardModule.Appointment.AppointmentDetails;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qltech.bws.DashboardModule.Models.AppointmentDetailModel;
import com.qltech.bws.R;
import com.qltech.bws.BWSApplication;
import com.qltech.bws.Utility.MeasureRatio;
import com.qltech.bws.databinding.FragmentAptDetailsBinding;

public class AptDetailsFragment extends Fragment {
    FragmentAptDetailsBinding binding;
    AppointmentDetailModel.ResponseData appointmentDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apt_details, container, false);
        View view = binding.getRoot();
        if (getArguments() != null) {
            appointmentDetail = getArguments().getParcelable("AppointmentDetail");
        }
        MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 0,
                1, 1, 0.12f, 0);
        binding.civProfile.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.civProfile.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());

        if (appointmentDetail.getDate().equalsIgnoreCase("")
                && appointmentDetail.getUserName().equalsIgnoreCase("")
                && appointmentDetail.getTime().equalsIgnoreCase("")) {
            binding.llDetails.setVisibility(View.GONE);
        } else {
            binding.llDetails.setVisibility(View.VISIBLE);
        }
        binding.tvTilte.setText(appointmentDetail.getName());

        binding.tvFacilitator.setText(appointmentDetail.getFacilitator());
        binding.tvUserName.setText(appointmentDetail.getUserName());
        binding.tvSubTitle.setText(appointmentDetail.getDesc());
        binding.tvDate.setText(appointmentDetail.getDate());
        binding.tvTime.setText(appointmentDetail.getTime());
        Glide.with(getActivity()).load(appointmentDetail.getImage()).thumbnail(0.05f).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.civProfile);

        if (appointmentDetail.getBookUrl().equalsIgnoreCase("")) {
            binding.btnComplete.setVisibility(View.GONE);
        } else {
            binding.btnComplete.setVisibility(View.VISIBLE);
        }
        binding.btnComplete.setOnClickListener(view1 -> {
            BWSApplication.showToast("Book Now", getActivity());
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(appointmentDetail.getBookUrl()));
            startActivity(i);
        });
        return view;
    }
}