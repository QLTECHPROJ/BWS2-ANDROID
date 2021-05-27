package com.brainwellnessspa.billingOrderModule.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brainwellnessspa.R;
import com.brainwellnessspa.databinding.FragmentPaymentBillingOrderBinding;
import com.brainwellnessspa.databinding.FragmentPaymentBinding;

import org.jetbrains.annotations.NotNull;

public class PaymentFragment extends Fragment {
  FragmentPaymentBillingOrderBinding binding;

  @Override
  public View onCreateView(
          @NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding =
        DataBindingUtil.inflate(
            inflater, R.layout.fragment_payment_billing_order, container, false);
    View view = binding.getRoot();
    return view;
  }
}