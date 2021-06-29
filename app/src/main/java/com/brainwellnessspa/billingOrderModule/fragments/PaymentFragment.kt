package com.brainwellnessspa.billingOrderModule.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.FragmentPaymentBillingOrderBinding

class PaymentFragment : Fragment() {
    lateinit var binding: FragmentPaymentBillingOrderBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_billing_order, container, false)
        return binding.root
    }
}