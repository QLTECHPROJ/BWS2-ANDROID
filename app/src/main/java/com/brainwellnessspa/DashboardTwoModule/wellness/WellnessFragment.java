package com.brainwellnessspa.DashboardTwoModule.wellness;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brainwellnessspa.R;
import com.brainwellnessspa.databinding.FragmentWellnessBinding;

public class WellnessFragment extends Fragment {
    FragmentWellnessBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_wellness, container, false);
        View view = binding.getRoot();
        return view;
    }
}