package com.brainwellnessspa.DashboardTwoModule.wellness

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.FragmentWellnessBinding

class WellnessFragment : Fragment() {
    lateinit var binding: FragmentWellnessBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_wellness,
            container,
            false
        )
        val view = binding.getRoot()
        return view
    }
}