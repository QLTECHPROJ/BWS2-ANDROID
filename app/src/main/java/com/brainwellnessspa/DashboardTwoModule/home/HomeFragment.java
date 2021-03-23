package com.brainwellnessspa.DashboardTwoModule.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.brainwellnessspa.R;
import com.brainwellnessspa.databinding.FragmentHomeBinding;


public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View view = binding.getRoot();
        homeViewModel.getText().observe(getViewLifecycleOwner(), s -> {
//                textView.setText(s);
        });
        return view;
    }
}