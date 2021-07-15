package com.brainwellnessspa.dashboardModule.session

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.FragmentSessionDetailBinding

class SessionDetailFragment : Fragment() {
    lateinit var binding:FragmentSessionDetailBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_session_detail, container, false)
        val view = binding.root

        return view
    }
}