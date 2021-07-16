package com.brainwellnessspa.dashboardOldModule.appointment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardOldModule.appointment.appointmentDetails.AptAnswersFragment
import com.brainwellnessspa.dashboardOldModule.appointment.appointmentDetails.AptAudioFragment
import com.brainwellnessspa.dashboardOldModule.appointment.appointmentDetails.AptBookletFragment
import com.brainwellnessspa.dashboardOldModule.appointment.appointmentDetails.AptDetailsFragment
import com.brainwellnessspa.dashboardOldModule.models.AppointmentDetailModel
import com.brainwellnessspa.databinding.FragmentAppointmentDetailsBinding
import com.brainwellnessspa.services.GlobalInitExoPlayer
import com.brainwellnessspa.utility.APIClient
import com.brainwellnessspa.utility.CONSTANTS
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AppointmentDetailsFragment : Fragment() {
    lateinit var binding: FragmentAppointmentDetailsBinding
    var activity: Activity? = null
    var userId: String? = null
    private var appointmentTypeId: String? = null
    private var appointmentName: String? = null
    var appointmentMainName: String? = null
    var appointmentImage: String? = null
    var audioFlag: String? = null
    var globalAppointmentDetailModel: AppointmentDetailModel? = null
    var params: LinearLayout.LayoutParams? = null
    var p: Properties? = null
    var gsonBuilder: GsonBuilder? = null
    var gson: Gson? = null
    var section: ArrayList<String>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_appointment_details, container, false)
        activity = getActivity()
        val shared1 = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREF_KEY_UserID, "")
        val shared = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
        audioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        if (arguments != null) {
            appointmentTypeId = requireArguments().getString("appointmentId")
            appointmentMainName = requireArguments().getString("appointmentMainName")
            appointmentName = requireArguments().getString("appointmentName")
            appointmentImage = requireArguments().getString("appointmentImage")
        }
        section = ArrayList()
        gsonBuilder = GsonBuilder()
        gson = gsonBuilder!!.create()
        appointmentData
        binding.llBack.setOnClickListener { callBack() }
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                callBack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
        return binding.root
    }

    override fun onResume() {
        refreshData()
        super.onResume()
    }

    private fun callBack() {
        ComeFromAppointmentDetail = 1
        ComesessionScreen = 1
        val fm = requireActivity().supportFragmentManager
        fm.popBackStack("AppointmentDetailsFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    fun refreshData() {
        try {
            val globalInitExoPlayer = GlobalInitExoPlayer()
            globalInitExoPlayer.UpdateMiniPlayer(requireActivity(), requireActivity())
            val shared = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
            audioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
            if (!audioFlag.equals("0", ignoreCase = true)) {
//                val fragment: Fragment = MiniPlayerFragment()
                val fragmentManager1 = requireActivity().supportFragmentManager
//                fragmentManager1.beginTransaction().add(R.id.flContainer, fragment).commit()
                params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params!!.setMargins(0, 0, 0, 290)
                binding.llViewOne.layoutParams = params
                params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params!!.setMargins(0, 0, 0, 350)
                binding.llViewTwo.layoutParams = params
            } else {
                params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params!!.setMargins(0, 0, 0, 110)
                binding.llViewOne.layoutParams = params
                params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params!!.setMargins(0, 0, 0, 220)
                binding.llViewTwo.layoutParams = params
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        /* try {
            SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            AudioFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
            SharedPreferences shared2 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
            String UnlockAudioLists = shared2.getString(CONSTANTS.PREF_KEY_UnLockAudiList, "");
            Gson gson1 = new Gson();
            Type type1 = new TypeToken<List<String>>() {
            }.getType();
            List<String> UnlockAudioList = gson1.fromJson(UnlockAudioLists, type1);
            if (!IsLock.equalsIgnoreCase("0") && (AudioFlag.equalsIgnoreCase("MainAudioList")
                    || AudioFlag.equalsIgnoreCase("ViewAllAudioList"))) {
                String audioID = "";
                SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String json = shared.getString(CONSTANTS.PREF_KEY_PlayerAudioList, String.valueOf(gson));
                Type type = new TypeToken<ArrayList<MainPlayModel>>() {
                }.getType();
                ArrayList<MainPlayModel> arrayList = gson.fromJson(json, type);

                if (arrayList.get(0).getAudioFile().equalsIgnoreCase("")) {
                    arrayList.remove(0);
                }
                audioID = arrayList.get(0).getID();

                if (UnlockAudioList.contains(audioID)) {
                } else {
                    SharedPreferences sharedm = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorr = sharedm.edit();
                    editorr.remove(CONSTANTS.PREF_KEY_MainAudioList);
                    editorr.remove(CONSTANTS.PREF_KEY_PlayerAudioList);
                    editorr.remove(CONSTANTS.PREF_KEY_PlayerPosition);
                    editorr.remove(CONSTANTS.PREF_KEY_queuePlay);
                    editorr.remove(CONSTANTS.PREF_KEY_audioPlay);
                    editorr.remove(CONSTANTS.PREF_KEY_AudioPlayerFlag);
                    editorr.remove(CONSTANTS.PREF_KEY_PlaylistId);
                    editorr.remove(CONSTANTS.PREF_KEY_myPlaylist);
                    editorr.clear();
                    editorr.commit();
                    callNewPlayerRelease();
                }
            } else if (!IsLock.equalsIgnoreCase("0") && !AudioFlag.equalsIgnoreCase("AppointmentDetailList")) {
                SharedPreferences sharedm = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                SharedPreferences.Editor editorr = sharedm.edit();
                editorr.remove(CONSTANTS.PREF_KEY_MainAudioList);
                editorr.remove(CONSTANTS.PREF_KEY_PlayerAudioList);
                editorr.remove(CONSTANTS.PREF_KEY_PlayerPosition);
                editorr.remove(CONSTANTS.PREF_KEY_queuePlay);
                editorr.remove(CONSTANTS.PREF_KEY_audioPlay);
                editorr.remove(CONSTANTS.PREF_KEY_AudioPlayerFlag);
                editorr.remove(CONSTANTS.PREF_KEY_PlaylistId);
                editorr.remove(CONSTANTS.PREF_KEY_myPlaylist);
                editorr.clear();
                editorr.commit();
                callNewPlayerRelease();

            }
            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
            if (!AudioFlag.equalsIgnoreCase("0")) {
               callAddTransFrag();
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 280);
                binding.llViewOne.setLayoutParams(params);
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 280);
                binding.llViewTwo.setLayoutParams(params);
            } else {
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 130);
                binding.llViewOne.setLayoutParams(params);
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 130);
                binding.llViewTwo.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private val appointmentData: Unit
        get() {
            if (BWSApplication.isNetworkConnected(getActivity())) {
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                val listCall = APIClient.client.getAppointmentDetails(userId, appointmentTypeId)
                listCall!!.enqueue(object : Callback<AppointmentDetailModel?> {
                    override fun onResponse(call: Call<AppointmentDetailModel?>, response: Response<AppointmentDetailModel?>) {
                        try {
                            val appointmentDetailModel = response.body()
                            if (appointmentDetailModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                globalAppointmentDetailModel = appointmentDetailModel
                                if (appointmentDetailModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                    if (appointmentDetailModel.responseData?.audio?.size == 0 && appointmentDetailModel.responseData?.booklet.equals("", ignoreCase = true) && appointmentDetailModel.responseData?.myAnswers.equals("", ignoreCase = true)) {
                                        binding.llViewOne.visibility = View.GONE
                                        binding.llViewTwo.visibility = View.VISIBLE
                                        binding.viewPager.offscreenPageLimit = 1
                                        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Details"))
                                    } else if (appointmentDetailModel.responseData?.audio?.size != 0 && appointmentDetailModel.responseData?.booklet.equals("", ignoreCase = true) && appointmentDetailModel.responseData?.myAnswers.equals("", ignoreCase = true)) {
                                        binding.llViewOne.visibility = View.VISIBLE
                                        binding.llViewTwo.visibility = View.GONE
                                        binding.viewPager.offscreenPageLimit = 2
                                        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Details"))
                                        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Audio"))
                                    } else if (appointmentDetailModel.responseData?.audio?.size == 0 && !appointmentDetailModel.responseData?.booklet.equals("", ignoreCase = true) && appointmentDetailModel.responseData?.myAnswers.equals("", ignoreCase = true)) {
                                        binding.llViewOne.visibility = View.VISIBLE
                                        binding.llViewTwo.visibility = View.GONE
                                        binding.viewPager.offscreenPageLimit = 2
                                        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Details"))
                                        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Booklet"))
                                    } else if (appointmentDetailModel.responseData?.audio?.size == 0 && appointmentDetailModel.responseData?.booklet.equals("", ignoreCase = true) && !appointmentDetailModel.responseData?.myAnswers.equals("", ignoreCase = true)) {
                                        binding.llViewOne.visibility = View.VISIBLE
                                        binding.llViewTwo.visibility = View.GONE
                                        binding.viewPager.offscreenPageLimit = 2
                                        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Details"))
                                        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("My answers"))
                                    } else if (appointmentDetailModel.responseData?.audio?.size != 0 && !appointmentDetailModel.responseData?.booklet.equals("", ignoreCase = true) && appointmentDetailModel.responseData?.myAnswers.equals("", ignoreCase = true)) {
                                        binding.llViewOne.visibility = View.VISIBLE
                                        binding.llViewTwo.visibility = View.GONE
                                        binding.viewPager.offscreenPageLimit = 3
                                        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Details"))
                                        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Audio"))
                                        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Booklet"))
                                    } else if (appointmentDetailModel.responseData?.audio?.size != 0 && appointmentDetailModel.responseData?.booklet.equals("", ignoreCase = true) && !appointmentDetailModel.responseData?.myAnswers.equals("", ignoreCase = true)) {
                                        binding.llViewOne.visibility = View.VISIBLE
                                        binding.llViewTwo.visibility = View.GONE
                                        binding.viewPager.offscreenPageLimit = 3
                                        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Details"))
                                        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Audio"))
                                        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("My answers"))
                                    } else if (appointmentDetailModel.responseData?.audio?.size == 0 && !appointmentDetailModel.responseData?.booklet.equals("", ignoreCase = true) && !appointmentDetailModel.responseData?.myAnswers.equals("", ignoreCase = true)) {
                                        binding.llViewOne.visibility = View.VISIBLE
                                        binding.llViewTwo.visibility = View.GONE
                                        binding.viewPager.offscreenPageLimit = 3
                                        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Details"))
                                        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Booklet"))
                                        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("My answers"))
                                    } else if (appointmentDetailModel.responseData?.audio?.size != 0 && !appointmentDetailModel.responseData?.booklet.equals("", ignoreCase = true) && !appointmentDetailModel.responseData?.myAnswers.equals("", ignoreCase = true)) {
                                        binding.llViewOne.visibility = View.VISIBLE
                                        binding.llViewTwo.visibility = View.GONE
                                        binding.viewPager.offscreenPageLimit = 4
                                        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Details"))
                                        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Audio"))
                                        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Booklet"))
                                        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("My answers"))
                                    }


                                    binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
                                    val adapter = TabAdapter(requireActivity().supportFragmentManager, requireActivity(), binding.tabLayout.tabCount)
                                    binding.viewPager.adapter = adapter
                                    binding.viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(binding.tabLayout))
                                    binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
                                        override fun onTabSelected(tab: TabLayout.Tab) {
                                            if (AptAudioFragment.comeRefreshData == 1) {
                                                refreshData()
                                            }
                                            binding.viewPager.currentItem = tab.position
                                        }

                                        override fun onTabUnselected(tab: TabLayout.Tab) {}
                                        override fun onTabReselected(tab: TabLayout.Tab) {}
                                    })
                                    val measureRatio = BWSApplication.measureRatio(getActivity(), 10f, 1f, 1f, 0.24f, 10f)
                                    binding.civProfile.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
                                    binding.civProfile.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
                                    binding.tvTilte.text = globalAppointmentDetailModel!!.responseData?.name
                                    binding.tvFacilitator.text = globalAppointmentDetailModel!!.responseData?.facilitator
                                    binding.tvUserName.text = globalAppointmentDetailModel!!.responseData?.userName
                                    binding.tvSubTitle.text = globalAppointmentDetailModel!!.responseData?.desc
                                    binding.tvDate.text = globalAppointmentDetailModel!!.responseData?.date
                                    binding.tvTime.text = globalAppointmentDetailModel!!.responseData?.time
                                    Glide.with(requireActivity()).load(globalAppointmentDetailModel!!.responseData?.image).thumbnail(0.10f).apply(RequestOptions.bitmapTransform(RoundedCorners(126))).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.civProfile)
                                    if (globalAppointmentDetailModel!!.responseData?.date.equals("", ignoreCase = true) && globalAppointmentDetailModel!!.responseData?.userName.equals("", ignoreCase = true) && globalAppointmentDetailModel!!.responseData?.time.equals("", ignoreCase = true)) {
                                        binding.llDetails.visibility = View.GONE
                                    } else {
                                        binding.llDetails.visibility = View.VISIBLE
                                    }
                                    if (globalAppointmentDetailModel!!.responseData?.bookUrl.equals("", ignoreCase = true)) {
                                        binding.btnCompletes.visibility = View.GONE
                                    } else {
                                        binding.btnCompletes.visibility = View.VISIBLE
                                    }
                                    binding.btnCompletes.setOnClickListener {
                                        BWSApplication.showToast("Book Now", getActivity())
                                        val i = Intent(Intent.ACTION_VIEW)
                                        i.data = Uri.parse(globalAppointmentDetailModel!!.responseData?.bookUrl)
                                        startActivity(i)
                                        BWSApplication.showToast("Complete the booklet", getActivity())
                                        p = Properties()
                                        p!!.putValue("sessionId", appointmentDetailModel.responseData?.id)
                                        p!!.putValue("sessionName", appointmentDetailModel.responseData?.name)
                                        p!!.putValue("bookletUrl", appointmentDetailModel.responseData?.bookUrl)
                                        BWSApplication.addToSegment("Complete Booklet Clicked", p, CONSTANTS.track)
                                    }
                                } else {
                                    BWSApplication.showToast(appointmentDetailModel.responseMessage, getActivity())
                                }
                                p = Properties()
                                p!!.putValue("id", appointmentDetailModel.responseData?.id)
                                p!!.putValue("name", appointmentDetailModel.responseData?.name)
                                p!!.putValue("desc", appointmentDetailModel.responseData?.desc)
                                p!!.putValue("status", appointmentDetailModel.responseData?.status)
                                p!!.putValue("facilitator", appointmentDetailModel.responseData?.facilitator)
                                p!!.putValue("userName", appointmentDetailModel.responseData?.userName)
                                p!!.putValue("date", appointmentDetailModel.responseData?.date)
                                p!!.putValue("time", appointmentDetailModel.responseData?.time)
                                p!!.putValue("bookUrl", appointmentDetailModel.responseData?.bookUrl)
                                p!!.putValue("booklet", appointmentDetailModel.responseData?.booklet)
                                p!!.putValue("myAnswers", appointmentDetailModel.responseData?.myAnswers)
                                for (i in appointmentDetailModel.responseData?.audio?.indices!!) {
                                    section!!.add(appointmentDetailModel.responseData?.audio!![i]?.iD.toString())
                                    section!!.add(appointmentDetailModel.responseData?.audio!![i]?.name.toString())
                                    section!!.add(appointmentDetailModel.responseData?.audio!![i]?.audiomastercat.toString())
                                    section!!.add(appointmentDetailModel.responseData?.audio!![i]?.audioSubCategory.toString())
                                    section!!.add(appointmentDetailModel.responseData?.audio!![i]?.audioDuration.toString())
                                }
                                p!!.putValue("sessionAudios", gson!!.toJson(section))
                                BWSApplication.addToSegment("Appointment Session Details Viewed", p, CONSTANTS.screen)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<AppointmentDetailModel?>, t: Throwable) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                    }
                })
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity())
            }
        }

    inner class TabAdapter(fm: FragmentManager?, var myContext: Context, var totalTabs: Int) : FragmentStatePagerAdapter(fm!!) {
        override fun getItem(position: Int): Fragment {
            val aptDetailsFragment = AptDetailsFragment()
            val aptAudioFragment = AptAudioFragment()
            val aptBookletFragment = AptBookletFragment()
            val aptAnswersFragment = AptAnswersFragment()
            val bundle = Bundle()
            val bundle2 = Bundle()
            bundle.putParcelable("AppointmentDetail", globalAppointmentDetailModel!!.responseData)
            bundle2.putParcelable("AppointmentDetail", globalAppointmentDetailModel!!.responseData)
            bundle2.putParcelableArrayList("AppointmentDetailList", globalAppointmentDetailModel!!.responseData?.audio)
            if (globalAppointmentDetailModel!!.responseData?.audio?.size == 0 && globalAppointmentDetailModel!!.responseData?.booklet.equals("", ignoreCase = true) && globalAppointmentDetailModel!!.responseData?.myAnswers.equals("", ignoreCase = true)) {
                when (position) {
                    0 -> {
                        aptDetailsFragment.arguments = bundle
                        return aptDetailsFragment
                    }
                }
            } else if (globalAppointmentDetailModel!!.responseData?.audio?.size != 0 && globalAppointmentDetailModel!!.responseData?.booklet.equals("", ignoreCase = true) && globalAppointmentDetailModel!!.responseData?.myAnswers.equals("", ignoreCase = true)) {
                when (position) {
                    0 -> {
                        aptDetailsFragment.arguments = bundle
                        return aptDetailsFragment
                    }
                    1 -> {
                        aptAudioFragment.arguments = bundle2
                        return aptAudioFragment
                    }
                }
            } else if (globalAppointmentDetailModel!!.responseData?.audio?.size == 0 && !globalAppointmentDetailModel!!.responseData?.booklet.equals("", ignoreCase = true) && globalAppointmentDetailModel!!.responseData?.myAnswers.equals("", ignoreCase = true)) {
                when (position) {
                    0 -> {
                        aptDetailsFragment.arguments = bundle
                        return aptDetailsFragment
                    }
                    1 -> {
                        aptBookletFragment.arguments = bundle
                        return aptBookletFragment
                    }
                }
            } else if (globalAppointmentDetailModel!!.responseData?.audio?.size == 0 && globalAppointmentDetailModel!!.responseData?.booklet.equals("", ignoreCase = true) && !globalAppointmentDetailModel!!.responseData?.myAnswers.equals("", ignoreCase = true)) {
                when (position) {
                    0 -> {
                        aptDetailsFragment.arguments = bundle
                        return aptDetailsFragment
                    }
                    1 -> {
                        aptAnswersFragment.arguments = bundle
                        return aptAnswersFragment
                    }
                }
            } else if (globalAppointmentDetailModel!!.responseData?.audio?.size != 0 && !globalAppointmentDetailModel!!.responseData?.booklet.equals("", ignoreCase = true) && globalAppointmentDetailModel!!.responseData?.myAnswers.equals("", ignoreCase = true)) {
                when (position) {
                    0 -> {
                        aptDetailsFragment.arguments = bundle
                        return aptDetailsFragment
                    }
                    1 -> {
                        aptAudioFragment.arguments = bundle2
                        return aptAudioFragment
                    }
                    2 -> {
                        aptBookletFragment.arguments = bundle
                        return aptBookletFragment
                    }
                }
            } else if (globalAppointmentDetailModel!!.responseData?.audio?.size != 0 && globalAppointmentDetailModel!!.responseData?.booklet.equals("", ignoreCase = true) && !globalAppointmentDetailModel!!.responseData?.myAnswers.equals("", ignoreCase = true)) {
                when (position) {
                    0 -> {
                        aptDetailsFragment.arguments = bundle
                        return aptDetailsFragment
                    }
                    1 -> {
                        aptAudioFragment.arguments = bundle2
                        return aptAudioFragment
                    }
                    2 -> {
                        aptAnswersFragment.arguments = bundle
                        return aptAnswersFragment
                    }
                }
            } else if (globalAppointmentDetailModel!!.responseData?.audio?.size == 0 && !globalAppointmentDetailModel!!.responseData?.booklet.equals("", ignoreCase = true) && !globalAppointmentDetailModel!!.responseData?.myAnswers.equals("", ignoreCase = true)) {
                when (position) {
                    0 -> {
                        aptDetailsFragment.arguments = bundle
                        return aptDetailsFragment
                    }
                    1 -> {
                        aptBookletFragment.arguments = bundle
                        return aptBookletFragment
                    }
                    2 -> {
                        aptAnswersFragment.arguments = bundle
                        return aptAnswersFragment
                    }
                }
            } else if (globalAppointmentDetailModel!!.responseData?.audio?.size != 0 && !globalAppointmentDetailModel!!.responseData?.booklet.equals("", ignoreCase = true) && !globalAppointmentDetailModel!!.responseData?.myAnswers.equals("", ignoreCase = true)) {
                when (position) {
                    0 -> {
                        aptDetailsFragment.arguments = bundle
                        return aptDetailsFragment
                    }
                    1 -> {
                        aptAudioFragment.arguments = bundle2
                        return aptAudioFragment
                    }
                    2 -> {
                        aptBookletFragment.arguments = bundle
                        return aptBookletFragment
                    }
                    3 -> {
                        aptAnswersFragment.arguments = bundle
                        return aptAnswersFragment
                    }
                }
            }
            return getItem(position)
        }

        override fun getCount(): Int {
            return totalTabs
        }
    }

    companion object {
        var ComeFromAppointmentDetail = 0
        var ComesessionScreen = 0
    }
}