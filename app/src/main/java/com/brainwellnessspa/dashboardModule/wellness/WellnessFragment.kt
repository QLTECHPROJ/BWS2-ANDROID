package com.brainwellnessspa.dashboardModule.wellness

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.enhance.ManageFragment
import com.brainwellnessspa.dashboardModule.session.SessionDetailFragment
import com.brainwellnessspa.dashboardModule.session.SessionExpContinueActivity
import com.brainwellnessspa.databinding.FragmentWellnessBinding

class WellnessFragment : Fragment() {
    lateinit var binding: FragmentWellnessBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_wellness, container, false)
        val view = binding.root

        binding.llExpSession.setOnClickListener {
          /*  val i = Intent(requireActivity(), SessionExpContinueActivity::class.java)
            startActivity(i)*/
            val fragment: Fragment = SessionDetailFragment()
            val fragmentManager1 = requireActivity().supportFragmentManager
            fragmentManager1.beginTransaction().replace(R.id.flContainer, fragment).commit()
            val bundle = Bundle()
            fragment.arguments = bundle
        }
        networkCheck()
        return view
    }

    override fun onResume() {
        networkCheck()
        super.onResume()
    }

    private fun networkCheck() {
        if (BWSApplication.isNetworkConnected(activity)) {
            binding.llRemainDev.visibility = View.VISIBLE /* VISIBLE*/
            binding.llNoInternet.visibility = View.GONE
        } else {
            binding.llRemainDev.visibility = View.GONE
            binding.llNoInternet.visibility = View.VISIBLE /* VISIBLE*/
        }
    }
}