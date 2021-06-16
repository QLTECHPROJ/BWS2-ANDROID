package com.brainwellnessspa.billingOrderModule.fragments

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.R
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.billingOrderModule.activities.CancelMembershipActivity
import com.brainwellnessspa.billingOrderModule.activities.UpgradePlanActivity
import com.brainwellnessspa.databinding.FragmentCurrentPlanBinding

class CurrentPlanFragment : Fragment() {
    lateinit var binding: FragmentCurrentPlanBinding
    var userId: String? = null
    var coUserId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_current_plan, container, false)
        val view = binding.root
        val shared = requireActivity().getSharedPreferences(
            CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER,
            AppCompatActivity.MODE_PRIVATE
        )
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")

        binding.btnUpgradePlan.setOnClickListener {
            val i = Intent(activity, UpgradePlanActivity::class.java)
            startActivity(i)
        }

        binding.tvCancel.setOnClickListener {
            val i = Intent(activity, CancelMembershipActivity::class.java)
            startActivity(i)
        }
        return view
    }
}