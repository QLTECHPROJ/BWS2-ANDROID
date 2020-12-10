package com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brainwellnessspa.R;
import com.brainwellnessspa.databinding.FragmentMiniPlayerBinding;

public class MiniPlayerFragment extends Fragment {
    FragmentMiniPlayerBinding binding;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mini_player, container, false);
        view = binding.getRoot();

        return view;
    }
}