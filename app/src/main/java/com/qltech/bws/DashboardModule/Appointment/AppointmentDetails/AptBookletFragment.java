package com.qltech.bws.DashboardModule.Appointment.AppointmentDetails;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.qltech.bws.R;
import com.qltech.bws.databinding.FragmentAptBookletBinding;

public class AptBookletFragment extends Fragment {
    FragmentAptBookletBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apt_booklet, container, false);
        View view = binding.getRoot();

        binding.btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Complete the booklet", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}