package com.brainwellnessspa.billingOrderModule.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.FragmentCurrentPlanBinding
import com.brainwellnessspa.utility.CONSTANTS

class CurrentPlanFragment : Fragment() {
    lateinit var binding: FragmentCurrentPlanBinding
    var userId: String? = ""
    var coUserId: String? = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_current_plan, container, false)
        val view = binding.root
        val shared = requireActivity().getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, AppCompatActivity.MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")

     /*   binding.btnUpgradePlan.setOnClickListener {
            val i = Intent(activity, UpgradePlanActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(i)
        }

        binding.tvCancel.setOnClickListener {
            val i = Intent(activity, CancelMembershipActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            i.putExtra("screenView","1")
            startActivity(i)
        }*/
        return view
    }
}