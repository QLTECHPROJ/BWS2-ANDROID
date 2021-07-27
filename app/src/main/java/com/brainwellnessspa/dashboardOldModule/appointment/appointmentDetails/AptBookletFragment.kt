package com.brainwellnessspa.dashboardOldModule.appointment.appointmentDetails

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardOldModule.models.AppointmentDetailModel
import com.brainwellnessspa.databinding.FragmentAptBookletBinding
import com.brainwellnessspa.utility.CONSTANTS
import com.segment.analytics.Properties

class AptBookletFragment : Fragment() {
    lateinit var binding: FragmentAptBookletBinding
    private var appointmentDetail: AppointmentDetailModel.ResponseData? = null
    var userId: String? = null
    var p: Properties? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apt_booklet, container, false)
        val view = binding.root
        val shared1 = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREF_KEY_UserID, "")
        if (arguments != null) {
            appointmentDetail = requireArguments().getParcelable("AppointmentDetail")
        }
        binding.tvTilte.text = appointmentDetail!!.name
        binding.btnComplete.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(appointmentDetail!!.booklet)
            startActivity(i)
            BWSApplication.showToast("Complete the booklet", activity)
            /*p = Properties()
            p!!.putValue("sessionId", appointmentDetail!!.id)
            p!!.putValue("sessionName", appointmentDetail!!.name)
            p!!.putValue("bookletUrl", appointmentDetail!!.bookUrl)
            BWSApplication.addToSegment("Complete Booklet Clicked", p, CONSTANTS.track)*/
        }
        return view
    }
}