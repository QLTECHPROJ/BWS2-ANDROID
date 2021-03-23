package com.brainwellnessspa.DashboardTwoModule.profile;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brainwellnessspa.R;
import com.brainwellnessspa.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        View view = binding.getRoot();
        return view;
    }
}