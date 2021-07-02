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
import com.brainwellnessspa.databinding.FragmentAptDetailsBinding
import com.brainwellnessspa.utility.CONSTANTS
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.segment.analytics.Properties

class AptDetailsFragment : Fragment() {
    lateinit var binding: FragmentAptDetailsBinding
    private var appointmentDetail: AppointmentDetailModel.ResponseData? = null
    var p: Properties? = null
    var userId: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apt_details, container, false)
        val view = binding.root
        val shared1 = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREF_KEY_UserID, "")
        if (arguments != null) {
            appointmentDetail = requireArguments().getParcelable("AppointmentDetail")
        }
        /* MeasureRatio measureRatio = BWSApplication.measureRatio(getActivity(), 0,
                1, 1, 0.24f, 0);
        binding.civProfile.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        binding.civProfile.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());*/binding.tvTilte.text = appointmentDetail!!.name
        binding.tvFacilitator.text = appointmentDetail!!.facilitator
        binding.tvUserName.text = appointmentDetail!!.userName
        binding.tvSubTitle.text = appointmentDetail!!.desc
        binding.tvDate.text = appointmentDetail!!.date
        binding.tvTime.text = appointmentDetail!!.time
        if (appointmentDetail!!.date.equals("", ignoreCase = true) && appointmentDetail!!.userName.equals("", ignoreCase = true) && appointmentDetail!!.time.equals("", ignoreCase = true)) {
            binding.llDetails.visibility = View.GONE
        } else {
            binding.llDetails.visibility = View.VISIBLE
        }
        Glide.with(requireActivity()).load(appointmentDetail!!.image).thumbnail(0.10f).apply(RequestOptions.bitmapTransform(RoundedCorners(126))).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.civProfile)
        if (appointmentDetail!!.bookUrl.equals("", ignoreCase = true)) {
            binding.btnComplete.visibility = View.GONE
        } else {
            binding.btnComplete.visibility = View.VISIBLE
        }
        binding.btnComplete.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(appointmentDetail!!.bookUrl)
            startActivity(i)
            BWSApplication.showToast("Book Now", activity)
            p = Properties()
            p!!.putValue("sessionId", appointmentDetail!!.id)
            p!!.putValue("sessionName", appointmentDetail!!.name)
            p!!.putValue("sessionBookUrl", appointmentDetail!!.bookUrl)
            BWSApplication.addToSegment("Session Book Clicked", p, CONSTANTS.track)
        }
        return view
    }
}