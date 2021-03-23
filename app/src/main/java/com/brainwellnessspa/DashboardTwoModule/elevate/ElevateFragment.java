package com.brainwellnessspa.DashboardTwoModule.elevate;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brainwellnessspa.R;
import com.brainwellnessspa.databinding.FragmentElevateBinding;

public class ElevateFragment extends Fragment {
    FragmentElevateBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_elevate, container, false);
        View view = binding.getRoot();
        return view;
    }
}