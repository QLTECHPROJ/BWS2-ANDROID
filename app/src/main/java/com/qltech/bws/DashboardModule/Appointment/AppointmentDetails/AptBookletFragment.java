package com.qltech.bws.DashboardModule.Appointment.AppointmentDetails;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qltech.bws.BWSApplication;
import com.qltech.bws.DashboardModule.Models.AppointmentDetailModel;
import com.qltech.bws.R;
import com.qltech.bws.databinding.FragmentAptBookletBinding;

public class AptBookletFragment extends Fragment {
    FragmentAptBookletBinding binding;
    AppointmentDetailModel.ResponseData appointmentDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apt_booklet, container, false);
        View view = binding.getRoot();
        if (getArguments() != null) {
            appointmentDetail = getArguments().getParcelable("AppointmentDetail");
        }
        binding.tvTilte.setText(appointmentDetail.getName());
        binding.btnComplete.setOnClickListener(view1 -> {
            BWSApplication.showToast("Complete the booklet", getActivity());
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(appointmentDetail.getBooklet()));
            startActivity(i);
        });
        return view;
    }
}