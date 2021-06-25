package com.brainwellnessspa.dashboardOldModule.appointment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardOldModule.models.SessionListModel
import com.brainwellnessspa.dashboardOldModule.transParentPlayer.fragments.MiniPlayerFragment
import com.brainwellnessspa.databinding.FragmentSessionsBinding
import com.brainwellnessspa.databinding.SessionListLayoutBinding
import com.brainwellnessspa.services.GlobalInitExoPlayer
import com.brainwellnessspa.utility.APIClient
import com.brainwellnessspa.utility.CONSTANTS
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SessionsFragment : Fragment() {
    lateinit var binding: FragmentSessionsBinding
    var fManager: FragmentManager? = null
    lateinit var activity: Activity
    var p: Properties? = null
    var gsonBuilder: GsonBuilder? = null
    var gson: Gson? = null
    var section: ArrayList<String?>? = null
    var userId: String? = null
    var appointmentName: String? = null
    var appointmentMainName: String? = null
    var appointmentImage: String? = null
    var appointmentTypeId: String? = null
    var audioFlag: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sessions, container, false)
        activity = requireActivity()
        val shared1 = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREF_KEY_UserID, "")
        val shared = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
        audioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        if (arguments != null) {
            appointmentName = requireArguments().getString("appointmentName")
            appointmentImage = requireArguments().getString("appointmentImage")
            appointmentMainName = requireArguments().getString("appointmentMainName")
        }
        section = ArrayList()
        gsonBuilder = GsonBuilder()
        gson = gsonBuilder!!.create()
        binding.llBack.setOnClickListener { callBack() }
        Glide.with(requireActivity()).load(appointmentImage).thumbnail(0.05f).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage)
        val recentlyPlayed: RecyclerView.LayoutManager = LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rvSessionList.layoutManager = recentlyPlayed
        binding.rvSessionList.itemAnimator = DefaultItemAnimator()
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                callBack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        return binding.root
    }

    override fun onResume() {
        prepareSessionList()
        if (view == null) {
            return
        }
        if (AppointmentDetailsFragment.ComeFromAppointmentDetail == 1) {
            prepareSessionList()
            AppointmentDetailsFragment.ComeFromAppointmentDetail = 0
        }
        super.onResume()
    }

    private fun callBack() {
        if (AppointmentDetailsFragment.ComesessionScreen == 1) {
            val bundle = Bundle()
            val appointmentFragment: Fragment = AppointmentFragment()
            bundle.putString("appointmentMainName", appointmentMainName)
            bundle.putString("appointmentName", appointmentName)
            bundle.putString("appointmentImage", appointmentImage)
            appointmentFragment.arguments = bundle
            val fragmentManager1 = requireActivity().supportFragmentManager
            fragmentManager1.beginTransaction().replace(R.id.flContainer, appointmentFragment).commit()
        }
        val bundle = Bundle()
        val appointmentFragment: Fragment = AppointmentFragment()
        bundle.putString("appointmentMainName", appointmentMainName)
        bundle.putString("appointmentName", appointmentName)
        bundle.putString("appointmentImage", appointmentImage)
        appointmentFragment.arguments = bundle
        val fragmentManager1 = requireActivity().supportFragmentManager
        fragmentManager1.beginTransaction().replace(R.id.flContainer, appointmentFragment).commit()
    }

    private fun prepareSessionList() {
        try {
            val globalInitExoPlayer = GlobalInitExoPlayer()
            globalInitExoPlayer.UpdateMiniPlayer(requireActivity(), requireActivity())
            val shared = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
            audioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
            if (!audioFlag.equals("0", ignoreCase = true)) {
                val fragment: Fragment = MiniPlayerFragment()
                val fragmentManager1 = requireActivity().supportFragmentManager
                fragmentManager1.beginTransaction().add(R.id.flContainer, fragment).commit()
                val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params.setMargins(0, 6, 0, 260)
                binding.llSpace.layoutParams = params
            } else {
                val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params.setMargins(0, 6, 0, 50)
                binding.llSpace.layoutParams = params
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        /*      try {
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


                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 10, 0, 260);
                binding.llSpace.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 10, 0, 50);
                binding.llSpace.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
*/try {
            if (BWSApplication.isNetworkConnected(getActivity())) {
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                val listCall = APIClient.getClient().getAppointmentSession(userId, appointmentName)
                listCall.enqueue(object : Callback<SessionListModel?> {
                    override fun onResponse(call: Call<SessionListModel?>, response: Response<SessionListModel?>) {
                        try {
                            val listModel = response.body()
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                            if (listModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                binding.tvSessionTitle.text = listModel.responseData!![0].catName
                                val measureRatio = BWSApplication.measureRatio(getActivity(), 0f, 5f, 3f, 1f, 0f)
                                binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
                                binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
                                binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
                                Glide.with(requireActivity()).load(listModel.responseData!![0].image).thumbnail(0.05f).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivRestaurantImage)
                                val appointmentsAdapter = SessionListAdapter(listModel.responseData, getActivity(), fManager)
                                binding.rvSessionList.adapter = appointmentsAdapter
                                p = Properties()
                                p!!.putValue("userId", userId)
                                for (i in listModel.responseData!!.indices) {
                                    section!!.add(listModel.responseData!![i].id)
                                    section!!.add(listModel.responseData!![i].catName)
                                    section!!.add(listModel.responseData!![i].name)
                                    section!!.add(listModel.responseData!![i].descInfusion)
                                    section!!.add(listModel.responseData!![i].desc)
                                    section!!.add(listModel.responseData!![i].catMenual)
                                    section!!.add(listModel.responseData!![i].date)
                                    section!!.add(listModel.responseData!![i].duration)
                                    section!!.add(listModel.responseData!![i].time)
                                    section!!.add(listModel.responseData!![i].status)
                                }
                                p!!.putValue("appointmentSessions", gson!!.toJson(section))
                                BWSApplication.addToSegment("Appointment Session Listing Viewed", p, CONSTANTS.screen)
                            } else {
                                BWSApplication.showToast(listModel.responseMessage, activity)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<SessionListModel?>, t: Throwable) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                    }
                })
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class SessionListAdapter(private val listModelList: List<SessionListModel.ResponseData>?, var ctx: Context?, var f_manager: FragmentManager?) : RecyclerView.Adapter<SessionListAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: SessionListLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.session_list_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val listModel = listModelList!![position]
            holder.binding.tvTitle.text = listModel.name
            if (listModel.desc.equals("", ignoreCase = true)) {
                holder.binding.tvSubTitle.visibility = View.GONE
            } else {
                holder.binding.tvSubTitle.visibility = View.VISIBLE
                holder.binding.tvSubTitle.text = listModel.desc
            }
            if (listModel.date.equals("", ignoreCase = true) && listModel.status.equals("", ignoreCase = true) && listModel.duration.equals("", ignoreCase = true) && listModel.time.equals("", ignoreCase = true)) {
                holder.binding.llDateTime.visibility = View.GONE
            } else {
                holder.binding.llDateTime.visibility = View.VISIBLE
            }
            holder.binding.tvDate.text = listModel.date
            holder.binding.tvTime.text = listModel.time
            holder.binding.tvHourGlass.text = listModel.duration
            when {
                listModel.status.equals("Booked", ignoreCase = true) -> {
                    holder.binding.tvStatus.text = listModel.status
                    holder.binding.tvStatus.setBackgroundResource(R.drawable.text_background)
                }
                listModel.status.equals("Arrive", ignoreCase = true) -> {
                    holder.binding.tvStatus.text = listModel.status
                    holder.binding.tvStatus.setBackgroundResource(R.drawable.green_text_background)
                }
                listModel.status.equals("Did_Not_Arrive", ignoreCase = true) -> {
                    holder.binding.tvStatus.text = getString(R.string.did_not_arrive)
                    holder.binding.tvStatus.setBackgroundResource(R.drawable.green_text_background)
                }
            }
            holder.binding.cvSetSession.setOnClickListener {
                val appointmentDetailsFragment: Fragment = AppointmentDetailsFragment()
                val fragmentManager1 = requireActivity().supportFragmentManager
                val bundle = Bundle()
                bundle.putString("appointmentId", listModel.id)
                bundle.putString("appointmentMainName", appointmentMainName)
                bundle.putString("appointmentName", appointmentName)
                bundle.putString("appointmentImage", appointmentImage)
                appointmentDetailsFragment.arguments = bundle
                fragmentManager1.beginTransaction().addToBackStack("AppointmentDetailsFragment").replace(R.id.flContainer, appointmentDetailsFragment).commit()
                p = Properties()
                p!!.putValue("userId", userId)
                p!!.putValue("sessionId", listModel.id)
                p!!.putValue("sessionName", listModel.name)
                BWSApplication.addToSegment("Session List Item Clicked", p, CONSTANTS.track)
            }
        }

        override fun getItemCount(): Int {
            return listModelList!!.size
        }

        inner class MyViewHolder(var binding: SessionListLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }
}