package com.brainwellnessspa.dashboardOldModule.appointment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardOldModule.models.NextSessionViewModel
import com.brainwellnessspa.dashboardOldModule.models.PreviousAppointmentsModel
import com.brainwellnessspa.dashboardOldModule.transParentPlayer.fragments.MiniPlayerFragment
import com.brainwellnessspa.databinding.FragmentAppointmentBinding
import com.brainwellnessspa.databinding.PreviousAppointmentsLayoutBinding
import com.brainwellnessspa.downloadModule.fragments.AudioDownloadsFragment
import com.brainwellnessspa.services.GlobalInitExoPlayer
import com.brainwellnessspa.utility.APIClient
import com.brainwellnessspa.utility.CONSTANTS
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AppointmentFragment : Fragment() {
    lateinit var binding: FragmentAppointmentBinding
    var userId: String? = null
    private var appointmentName: String? = null
    var appointmentMainName: String? = null
    var appointmentImage: String? = null
    var audioFlag: String? = null
    lateinit var activity: Activity
    lateinit var p: Properties
    var section: ArrayList<String?>? = null
    var previoussection: ArrayList<String?>? = null
    var gsonBuilder: GsonBuilder? = null
    var gson: Gson? = null
    var nextSessionViewModel: NextSessionViewModel? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_appointment, container, false)
        val view = binding.root
        activity = requireActivity()
        val shared1 = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREF_KEY_UserID, "")
        val shared = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
        audioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        val bundle = this.arguments
        if (bundle != null) {
            appointmentName = bundle.getString("appointmentName")
            appointmentImage = bundle.getString("appointmentImage")
            appointmentMainName = bundle.getString("appointmentMainName")
        }
        section = ArrayList()
        previoussection = ArrayList()
        gsonBuilder = GsonBuilder()
        gson = gsonBuilder!!.create()
        val recentlyPlayed: RecyclerView.LayoutManager = LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rvPreviousData.layoutManager = recentlyPlayed
        binding.rvPreviousData.itemAnimator = DefaultItemAnimator()
        AudioDownloadsFragment.comefromDownload = "0"
        binding.cvSetSession.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://brainwellnessspa.com.au/bookings/services.php")
            startActivity(i)
            p = Properties()
            p.putValue("userId", userId)
            p.putValue("bookingLink", "https://brainwellnessspa.com.au/bookings/services.php")
            BWSApplication.addToSegment("Book a New Appointment Clicked", p, CONSTANTS.track)
        }
        return view
    }

    override fun onResume() {
        AudioDownloadsFragment.comefromDownload = "0"
        preparePreviousAppointmentsData()
        super.onResume()
    }

    private fun preparePreviousAppointmentsData() {
        try {
            val globalInitExoPlayer = GlobalInitExoPlayer()
            globalInitExoPlayer.UpdateMiniPlayer(requireActivity(), requireActivity())
            val shared = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
            audioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
            if (!audioFlag.equals("0", ignoreCase = true)) {
                callAddTransFrag()
                val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params.setMargins(0, 6, 0, 260)
                binding.llSpace.layoutParams = params
            } else {
                val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params.setMargins(0, 6, 0, 130)
                binding.llSpace.layoutParams = params
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        /*
        try {
            SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            audioFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
            SharedPreferences shared2 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
            String UnlockAudioLists = shared2.getString(CONSTANTS.PREF_KEY_UnLockAudiList, "");
            Gson gson1 = new Gson();
            Type type1 = new TypeToken<List<String>>() {
            }.getType();
            List<String> UnlockAudioList = gson1.fromJson(UnlockAudioLists, type1);
            if (!IsLock.equalsIgnoreCase("0") && (audioFlag.equalsIgnoreCase("MainAudioList")
                    || audioFlag.equalsIgnoreCase("ViewAllAudioList"))) {
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
            } else if (!IsLock.equalsIgnoreCase("0") && !audioFlag.equalsIgnoreCase("AppointmentDetailList")) {
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
            audioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
            if (!audioFlag.equalsIgnoreCase("0")) {
               callAddTransFrag();

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, 8, 10, 260);
                binding.llSpace.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, 8, 10, 130);
                binding.llSpace.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
*/try {
            if (BWSApplication.isNetworkConnected(getActivity())) {
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                val listCall = APIClient.getClient().getNextSessionVIew(userId)
                listCall.enqueue(object : Callback<NextSessionViewModel?> {
                    override fun onResponse(call: Call<NextSessionViewModel?>, response: Response<NextSessionViewModel?>) {
                        try {
                            val listModel = response.body()
                            if (listModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                nextSessionViewModel = listModel
                                binding.tvNextSessionTitle.setText(R.string.Next_Session)
                                if (listModel.responseData!!.response.equals("", ignoreCase = true)) {
                                    binding.cvShowSession.visibility = View.GONE
                                    binding.cvSetSession.visibility = View.VISIBLE
                                } else if (listModel.responseData!!.response.equals("1", ignoreCase = true)) {
                                    binding.cvShowSession.visibility = View.VISIBLE
                                    binding.cvSetSession.visibility = View.GONE
                                    binding.tvTitle.text = listModel.responseData!!.name
                                    binding.tvDate.text = listModel.responseData!!.date
                                    binding.tvTime.text = listModel.responseData!!.time
                                    binding.tvHourGlass.text = listModel.responseData!!.duration
                                    if (listModel.responseData!!.task!!.subtitle.equals("", ignoreCase = true)) {
                                        binding.tvSubTitle.visibility = View.GONE
                                    } else {
                                        binding.tvSubTitle.visibility = View.VISIBLE
                                        binding.tvSubTitle.text = listModel.responseData!!.task!!.subtitle
                                    }
                                    if (listModel.responseData!!.task!!.title.equals("", ignoreCase = true)) {
                                        binding.tvNextSession.visibility = View.GONE
                                        binding.llCheckBox1.visibility = View.GONE
                                        binding.llCheckBox2.visibility = View.GONE
                                    } else {
                                        binding.tvNextSession.visibility = View.VISIBLE
                                        binding.llCheckBox1.visibility = View.VISIBLE
                                        binding.llCheckBox2.visibility = View.VISIBLE
                                    }
                                    binding.tvNextSession.text = listModel.responseData!!.task!!.title
                                    binding.cbTask1.isEnabled = false
                                    binding.cbTask1.isClickable = false
                                    binding.cbTask2.isEnabled = false
                                    binding.cbTask2.isClickable = false
                                    if (listModel.responseData!!.task!!.audioTask.equals("", ignoreCase = true)) {
                                        binding.cbTask1.visibility = View.GONE
                                        binding.tvTaskTitle1.visibility = View.GONE
                                    } else {
                                        binding.cbTask1.visibility = View.VISIBLE
                                        binding.tvTaskTitle1.visibility = View.VISIBLE
                                        binding.tvTaskTitle1.text = listModel.responseData!!.task!!.audioTask
                                    }
                                    if (listModel.responseData!!.task!!.bookletTask.equals("", ignoreCase = true)) {
                                        binding.cbTask2.visibility = View.GONE
                                        binding.tvTaskTitle2.visibility = View.GONE
                                    } else {
                                        binding.cbTask2.visibility = View.VISIBLE
                                        binding.tvTaskTitle2.visibility = View.VISIBLE
                                        binding.tvTaskTitle2.text = listModel.responseData!!.task!!.bookletTask
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<NextSessionViewModel?>, t: Throwable) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                    }
                })
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), getActivity())
            }
            if (BWSApplication.isNetworkConnected(getActivity())) {
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                val listCall1 = APIClient.getClient().getAppointmentVIew(userId)
                listCall1.enqueue(object : Callback<PreviousAppointmentsModel?> {
                    override fun onResponse(call: Call<PreviousAppointmentsModel?>, response: Response<PreviousAppointmentsModel?>) {
                        try {
                            val listModel = response.body()
                            if (listModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                                binding.tvPreviousAppointments.setText(R.string.Previous_Appointments)
                                val appointmentsAdapter = PreviousAppointmentsAdapter(listModel.responseData, getActivity())
                                binding.rvPreviousData.adapter = appointmentsAdapter
                                p = Properties()
                                p.putValue("userId", userId)
                                if (nextSessionViewModel!!.responseData!!.response.equals("", ignoreCase = true)) {
                                    p.putValue("nextSession", "")
                                } else {
                                    section!!.add(nextSessionViewModel!!.responseData!!.id)
                                    section!!.add(nextSessionViewModel!!.responseData!!.name)
                                    section!!.add(nextSessionViewModel!!.responseData!!.date)
                                    section!!.add(nextSessionViewModel!!.responseData!!.duration)
                                    section!!.add(nextSessionViewModel!!.responseData!!.time)
                                    section!!.add(nextSessionViewModel!!.responseData!!.task!!.title)
                                    section!!.add(nextSessionViewModel!!.responseData!!.task!!.subtitle)
                                    section!!.add(nextSessionViewModel!!.responseData!!.task!!.audioTask)
                                    section!!.add(nextSessionViewModel!!.responseData!!.task!!.bookletTask)
                                    p.putValue("nextSession", gson!!.toJson(section))
                                }
                                for (i in listModel.responseData!!.indices) {
                                    previoussection!!.add(listModel.responseData!![i].category)
                                    previoussection!!.add(listModel.responseData!![i].catMenual)
                                }
                                p.putValue("previousAppointments", gson!!.toJson(previoussection))
                                BWSApplication.addToSegment("Appointment Screen Viewed", p, CONSTANTS.screen)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<PreviousAppointmentsModel?>, t: Throwable) {
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

    private fun callAddTransFrag() {
        val fragment: Fragment = MiniPlayerFragment()
        val fragmentManager1 = requireActivity().supportFragmentManager
        fragmentManager1.beginTransaction().add(R.id.flContainer, fragment).commit()
    }

    inner class PreviousAppointmentsAdapter(private val listModel: List<PreviousAppointmentsModel.ResponseData>?, var ctx: Context?) : RecyclerView.Adapter<PreviousAppointmentsAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: PreviousAppointmentsLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.previous_appointments_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = listModel!![position].category
            val measureRatio = BWSApplication.measureRatio(ctx, 0f, 1f, 1f, 0.11f, 0f)
            holder.binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            Glide.with(ctx!!).load(listModel[position].image).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(12))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)
            holder.binding.llMainLayout.setOnClickListener {
                p = Properties()
                p.putValue("userId", userId)
                p.putValue("appointmentName", listModel[position].category)
                p.putValue("appointmentCategory", listModel[position].catMenual)
                BWSApplication.addToSegment("Appointment Item Clicked", p, CONSTANTS.track)
                val bundle = Bundle()
                val sessionsFragment: Fragment = SessionsFragment()
                bundle.putString("appointmentMainName", listModel[position].category)
                bundle.putString("appointmentName", listModel[position].catMenual)
                bundle.putString("appointmentImage", listModel[position].image)
                sessionsFragment.arguments = bundle
                val fragmentManager1 = requireActivity().supportFragmentManager
                fragmentManager1.beginTransaction().replace(R.id.flContainer, sessionsFragment).commit()
            }
        }

        override fun getItemCount(): Int {
            return listModel!!.size
        }

        inner class MyViewHolder(var binding: PreviousAppointmentsLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }
}