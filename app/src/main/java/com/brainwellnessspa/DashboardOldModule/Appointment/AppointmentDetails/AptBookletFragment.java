package com.brainwellnessspa.DashboardOldModule.Appointment.AppointmentDetails;

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

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardOldModule.Models.AppointmentDetailModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.FragmentAptBookletBinding;
import com.segment.analytics.Properties;

public class AptBookletFragment extends Fragment {
    FragmentAptBookletBinding binding;
    AppointmentDetailModel.ResponseData appointmentDetail;
    String UserID;
    Properties p;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apt_booklet, container, false);
        View view = binding.getRoot();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        if (getArguments() != null) {
            appointmentDetail = getArguments().getParcelable("AppointmentDetail");
        }
        binding.tvTilte.setText(appointmentDetail.getName());
        binding.btnComplete.setOnClickListener(view1 -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(appointmentDetail.getBooklet()));
            startActivity(i);
            BWSApplication.showToast("Complete the booklet", getActivity());
            p = new Properties();
            p.putValue("userId", UserID);
            p.putValue("sessionId", appointmentDetail.getId());
            p.putValue("sessionName", appointmentDetail.getName());
            p.putValue("bookletUrl", appointmentDetail.getBookUrl());
            BWSApplication.addToSegment("Complete Booklet Clicked", p, CONSTANTS.track);
        });
        return view;
    }
}