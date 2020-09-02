package com.qltech.bws.DashboardModule.Appointment.AppointmentDetails;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.qltech.bws.DashboardModule.Appointment.SessionsFragment;
import com.qltech.bws.DashboardModule.Models.AppointmentDetail;
import com.qltech.bws.R;
import com.qltech.bws.databinding.FragmentAptAnswersBinding;

public class AptAnswersFragment extends Fragment {
    FragmentAptAnswersBinding binding;
    AppointmentDetail.ResponseData appointmentDetail;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apt_answers, container, false);
        View view = binding.getRoot();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            appointmentDetail = bundle.getParcelable("AppointmentDetail");
        }
        binding.btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Download PDF", Toast.LENGTH_SHORT).show();
                Fragment sessionsFragment = new SessionsFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.flSession, sessionsFragment)
                        .commit();
            }
        });

        return view;
    }
}