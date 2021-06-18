package com.brainwellnessspa.dashboardOldModule.appointment.AppointmentDetails;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brainwellnessspa.utility.CONSTANTS;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.brainwellnessspa.dashboardOldModule.models.AppointmentDetailModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.databinding.FragmentAptDetailsBinding;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.segment.analytics.Properties;

public class AptDetailsFragment extends Fragment {
    FragmentAptDetailsBinding binding;
    AppointmentDetailModel.ResponseData appointmentDetail;
    Properties p;
    String UserID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apt_details, container, false);
        View view = binding.getRoot();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        if (getArguments() != null) {
            appointmentDetail = getArguments().getParcelable("AppointmentDetail");
        }
       /* MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 0,
                1, 1, 0.24f, 0);
        binding.civProfile.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.civProfile.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());*/
        binding.tvTilte.setText(appointmentDetail.getName());
        binding.tvFacilitator.setText(appointmentDetail.getFacilitator());
        binding.tvUserName.setText(appointmentDetail.getUserName());
        binding.tvSubTitle.setText(appointmentDetail.getDesc());
        binding.tvDate.setText(appointmentDetail.getDate());
        binding.tvTime.setText(appointmentDetail.getTime());

        if (appointmentDetail.getDate().equalsIgnoreCase("")
                && appointmentDetail.getUserName().equalsIgnoreCase("")
                && appointmentDetail.getTime().equalsIgnoreCase("")) {
            binding.llDetails.setVisibility(View.GONE);
        } else {
            binding.llDetails.setVisibility(View.VISIBLE);
        }


        Glide.with(getActivity()).load(appointmentDetail.getImage()).thumbnail(0.10f)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(126)))
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.civProfile);

        if (appointmentDetail.getBookUrl().equalsIgnoreCase("")) {
            binding.btnComplete.setVisibility(View.GONE);
        } else {
            binding.btnComplete.setVisibility(View.VISIBLE);
        }

        binding.btnComplete.setOnClickListener(view1 -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(appointmentDetail.getBookUrl()));
            startActivity(i);
            BWSApplication.showToast("Book Now", getActivity());
            p = new Properties();
            p.putValue("userId", UserID);
            p.putValue("sessionId", appointmentDetail.getId());
            p.putValue("sessionName", appointmentDetail.getName());
            p.putValue("sessionBookUrl", appointmentDetail.getBookUrl());
            BWSApplication.addToSegment("Session Book Clicked", p, CONSTANTS.track);
        });
        return view;
    }
}