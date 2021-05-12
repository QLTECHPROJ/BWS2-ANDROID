package com.brainwellnessspa.dashboardModule.elevate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.FragmentElevateBinding

class ElevateFragment : Fragment() {
    lateinit var binding: FragmentElevateBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_elevate,
            container,
            false
        )
        val view = binding.getRoot()
        return view
    }
}