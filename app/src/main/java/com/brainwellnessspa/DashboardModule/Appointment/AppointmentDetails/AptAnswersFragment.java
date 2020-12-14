package com.brainwellnessspa.DashboardModule.Appointment.AppointmentDetails;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Models.AppointmentDetailModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.databinding.FragmentAptAnswersBinding;

public class AptAnswersFragment extends Fragment {
    FragmentAptAnswersBinding binding;
    AppointmentDetailModel.ResponseData appointmentDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apt_answers, container, false);
        View view = binding.getRoot();
        if (getArguments() != null) {
            appointmentDetail = getArguments().getParcelable("AppointmentDetail");
        }
        binding.tvTilte.setText(appointmentDetail.getName());

        binding.btnComplete.setOnClickListener(view1 -> {
            BWSApplication.showToast("Download PDF", getActivity());
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(appointmentDetail.getMyAnswers()));
            startActivity(i);
        });
        return view;
    }
}