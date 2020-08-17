package com.qltech.bws.BillingOrderModule.Fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.qltech.bws.R;
import com.qltech.bws.databinding.FragmentBillingAddressBinding;

public class BillingAddressFragment extends Fragment {
    FragmentBillingAddressBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_billing_address, container, false);
        View view = binding.getRoot();
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Saved Sucessfully", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}