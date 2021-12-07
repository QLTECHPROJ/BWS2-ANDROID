package com.brainwellnessspa.dashboardModule.wellness

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.FragmentWellnessBinding

class WellnessFragment : Fragment() {
    lateinit var binding: FragmentWellnessBinding
    lateinit var ctx: Context
    lateinit var act: Activity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_wellness, container, false)
        val view = binding.root
        ctx = requireActivity()
        act = requireActivity()

        binding.llRemainDev.setOnClickListener {
            /* val i = Intent(requireActivity(), SessionPcDetailActivity::class.java)
             startActivity(i)*/
            /*val fragment: Fragment = SessionDetailFragment()
            val fragmentManager1 = requireActivity().supportFragmentManager
            fragmentManager1.beginTransaction().replace(R.id.flContainer, fragment).commit()
            val bundle = Bundle()
            fragment.arguments = bundle*/
        }
        networkCheck()
        return view
    }

    override fun onResume() {
        networkCheck()
        super.onResume()
    }

    private fun networkCheck() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            binding.llRemainDev.visibility = View.VISIBLE /* VISIBLE*/
            binding.llNoInternet.visibility = View.GONE
        } else {
            binding.llRemainDev.visibility = View.GONE
            binding.llNoInternet.visibility = View.VISIBLE /* VISIBLE*/
        }
    }
}