package com.brainwellnessspa.dashboardModule.enhance

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.NotificationManager
import android.content.*
import android.content.Context.MODE_PRIVATE
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.activities.MyPlayerActivity
import com.brainwellnessspa.dashboardModule.models.*
import com.brainwellnessspa.databinding.*
import com.brainwellnessspa.membershipModule.activities.SleepTimeActivity
import com.brainwellnessspa.roomDataBase.*
import com.brainwellnessspa.services.GlobalInitExoPlayer
import com.brainwellnessspa.services.GlobalInitExoPlayer.Companion.GetCurrentAudioPosition
import com.brainwellnessspa.services.GlobalInitExoPlayer.Companion.callAllRemovePlayer
import com.brainwellnessspa.userModule.signupLogin.SignInActivity
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class ManageFragment : Fragment() {
    lateinit var binding: FragmentManageBinding
    lateinit var ctx: Context
    lateinit var act: Activity
    private lateinit var audioAdapter: AudioAdapter
    lateinit var playlistAdapter: PlaylistAdapter
    var coUserId: String? = ""
    var userId: String? = ""
    var myDownloads: String? = ""
    var sleeptime: String? = ""

    var downloadAudioDetailsList = arrayListOf<String>()
    var homelistModel: HomeDataModel = HomeDataModel()
    private val listener: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("MyData")) {
                setPlayPauseIcon()
            }
        }
    }
    private val listener1: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("MyReminder")) {
                prepareData()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage, container, false)
        val view: View = binding.root
        ctx = requireActivity()
        act = requireActivity()
        val shared = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, AppCompatActivity.MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        binding.rvMainPlayList.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMainAudioList.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)

        DB = getAudioDataBase(ctx)
        getDownloadedList(ctx, DB)
        val sharedd = ctx.getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
        sleeptime = sharedd.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")

        if (sleeptime.equals("", true)) {
            binding.llSleepTime.visibility = View.GONE
        } else {
            binding.llSleepTime.visibility = View.VISIBLE

        }
        binding.tvSleepTime.text = "Your average sleep time is \n$sleeptime"

        networkCheck()
        binding.llSearch.setOnClickListener {
            if (isNetworkConnected(activity)) {
                val i = Intent(ctx, AddAudioActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                i.putExtra("PlaylistID", "")
                startActivity(i)
            } else {
                showToast(getString(R.string.no_server_found), activity)
            }
        }
        binding.llSleepTime.setOnClickListener {
            if (IsLock.equals("1")) {
                callEnhanceActivity(ctx, act)
            } else if (IsLock.equals("0")) {
                if (isNetworkConnected(activity)) {
                    val dialog = Dialog(ctx)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(R.layout.cancel_membership)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(ctx, R.color.transparent_white)))
                    dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
                    val tvSubTitle = dialog.findViewById<TextView>(R.id.tvSubTitle)
                    val tvGoBack = dialog.findViewById<TextView>(R.id.tvGoBack)
                    val btn = dialog.findViewById<Button>(R.id.Btn)
                    tvTitle.text = "Change Sleep Time"
                    tvSubTitle.text = "Changing Sleep Time will make you reselect Area of Focus as well. Would you like to proceed ?"
                    tvGoBack.text = "No"
                    btn.text = "Yes"
                    dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            dialog.dismiss()
                            return@setOnKeyListener true
                        }
                        false
                    }
                    btn.setOnClickListener {
                        dialog.dismiss()
                        val intent = Intent(activity, SleepTimeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                    }
                    /* This click event is called when not cancelling subscription */
                    tvGoBack.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                    dialog.setCancelable(false)

                } else {
                    showToast(getString(R.string.no_server_found), activity)
                }
            }
        }

        binding.tvViewAll.setOnClickListener {
            if (isNetworkConnected(ctx)) {
                val audioFragment: Fragment = MainPlaylistFragment()
                val fragmentManager1 = requireActivity().supportFragmentManager
                fragmentManager1.beginTransaction().replace(R.id.flContainer, audioFragment).commit()
            } else {
                showToast(getString(R.string.no_server_found), act)
            }
        }

        binding.rlCreatePlaylist.setOnClickListener {
            if (IsLock.equals("1")) {
                callEnhanceActivity(ctx, act)
            } else if (IsLock.equals("0")) {
                val p = Properties()
                p.putValue("source", "Enhance Screen")
                addToSegment("Create Playlist Clicked", p, CONSTANTS.track)
                val dialog = Dialog(ctx)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.create_palylist)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(act, R.color.blue_transparent)))
                dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                val edtCreate = dialog.findViewById<EditText>(R.id.edtCreate)
                val tvCancel = dialog.findViewById<TextView>(R.id.tvCancel)
                val btnSendCode = dialog.findViewById<Button>(R.id.btnSendCode)
                edtCreate.clearFocus()
                val popupTextWatcher: TextWatcher = object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        val number = edtCreate.text.toString().trim { it <= ' ' }
                        if (number.isNotEmpty()) {
                            btnSendCode.isEnabled = true
                            btnSendCode.setTextColor(ContextCompat.getColor(act, R.color.black))
                            btnSendCode.setBackgroundResource(R.drawable.white_round_cornor)
                        } else {
                            btnSendCode.isEnabled = false
                            btnSendCode.setTextColor(ContextCompat.getColor(act, R.color.white))
                            btnSendCode.setBackgroundResource(R.drawable.gray_round_cornor)
                        }
                    }

                    override fun afterTextChanged(s: Editable) {}
                }
                edtCreate.addTextChangedListener(popupTextWatcher)
                dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss()
                        return@setOnKeyListener true
                    }
                    false
                }
                btnSendCode.setOnClickListener {
                    if (isNetworkConnected(ctx)) {
                        showProgressBar(binding.progressBar, binding.progressBarHolder, act)
                        val listCall = APINewClient.client.getCreatePlaylist(coUserId, edtCreate.text.toString())
                        listCall.enqueue(object : Callback<CreateNewPlaylistModel?> {
                            override fun onResponse(call: Call<CreateNewPlaylistModel?>, response: Response<CreateNewPlaylistModel?>) {
                                try {
                                    hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                                    val listModel = response.body()
                                    if (listModel != null) {
                                        if (listModel.responseCode.equals(act.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                            if (listModel.responseData!!.iscreate.equals("0", ignoreCase = true)) {
                                                showToast(listModel.responseMessage, act)
                                                dialog.dismiss()
                                            } else if (listModel.responseData!!.iscreate.equals("1", ignoreCase = true) || listModel.responseData!!.iscreate.equals("", ignoreCase = true)) { //                                        try {
                                                val p = Properties()
                                                p.putValue("source", "Enhance Screen")
                                                p.putValue("playlistId", listModel.responseData!!.playlistID)
                                                p.putValue("playlistName", listModel.responseData!!.playlistName)
                                                addToSegment(" Playlist Created", p, CONSTANTS.track)
                                                callMyPlaylistActivity("1", listModel.responseData!!.playlistID, listModel.responseData!!.playlistName, act)
                                                act.overridePendingTransition(0, 0)
                                                dialog.dismiss() //                                        } catch (e: Exception) {
                                                //                                            e.printStackTrace()
                                                //                                            dialog.dismiss()
                                                //                                        }
                                            }
                                        } else if (listModel.responseCode.equals(act.getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
                                            deleteCall(act)
                                            showToast(listModel.responseMessage, act)
                                            val i = Intent(act, SignInActivity::class.java)
                                            i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                            i.putExtra("mobileNo", "")
                                            i.putExtra("countryCode", "")
                                            i.putExtra("name", "")
                                            i.putExtra("email", "")
                                            i.putExtra("countryShortName", "")
                                            act.startActivity(i)
                                            act.finish()
                                        }
                                    }
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                            }

                            override fun onFailure(call: Call<CreateNewPlaylistModel?>, t: Throwable) {
                                hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                            }
                        })
                    } else {
                        showToast(ctx.getString(R.string.no_server_found), act)
                    }
                }
                tvCancel.setOnClickListener { dialog.dismiss() }
                dialog.show()
                dialog.setCancelable(false)
            }
        }

        return view
    }

    private fun callMyPlaylistActivity(new1: String, playlistID: String?, playlistName: String?, act: Activity) {
        val i = Intent(act, MyPlaylistListingActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        i.putExtra("New", new1)
        i.putExtra("PlaylistID", playlistID)
        i.putExtra("PlaylistName", playlistName)
        i.putExtra("PlaylistImage", "")
        i.putExtra("MyDownloads", "0")
        i.putExtra("ScreenView", "Enhance Screen")
        i.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        act.startActivity(i)
    }

    override fun onResume() {
        var gb = GlobalInitExoPlayer()
        gb.UpdateMiniPlayer(ctx, act)
        gb.UpdateNotificationAudioPLayer(ctx)
        networkCheck()
        prepareData()
        super.onResume()
    }

    private fun networkCheck() {
        if (isNetworkConnected(activity)) {
            binding.llSetReminder.visibility = View.VISIBLE
            binding.llUser.visibility = View.VISIBLE
            binding.ivLightBg.visibility = View.VISIBLE
            binding.llListData.visibility = View.VISIBLE
            binding.llPlayer.visibility = View.VISIBLE
            binding.llNoInternet.visibility = View.GONE
        } else {
            binding.llSetReminder.visibility = View.GONE
            binding.llUser.visibility = View.VISIBLE
            binding.ivLightBg.visibility = View.GONE
            binding.llListData.visibility = View.GONE
            binding.llPlayer.visibility = View.GONE
            binding.llNoInternet.visibility = View.VISIBLE
            val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.setMargins(0, 80, 0, 110)
            binding.llSpace1.layoutParams = params //            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }

    override fun onPause() {
        DB.taskDao()?.geAllDataz("", coUserId)?.removeObserver {}
        DB.taskDao()?.geAllLiveDataBYDownloaded("Complete", coUserId)?.removeObserver {}
        super.onPause()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(ctx).unregisterReceiver(listener)
        LocalBroadcastManager.getInstance(ctx).unregisterReceiver(listener1)
        super.onDestroy()
    }

    private fun callObserverMethod(listModel: List<HomeDataModel.ResponseData.Audio>, act: Activity, DB: AudioDatabase) {
        DB.taskDao()?.geAllDataz("", coUserId)?.observe(ctx as (LifecycleOwner), { downloadAudioDetails: List<DownloadAudioDetailsUniq> ->
            val details = ArrayList<HomeDataModel.ResponseData.Audio.Detail>()
            if (downloadAudioDetails.isNotEmpty()) {
                for (i in downloadAudioDetails.indices) {
                    val detail = HomeDataModel.ResponseData.Audio.Detail()
                    detail.id = downloadAudioDetails[i].ID
                    detail.name = downloadAudioDetails[i].Name
                    detail.audioFile = downloadAudioDetails[i].AudioFile
                    detail.audioDirection = downloadAudioDetails[i].AudioDirection
                    detail.audiomastercat = downloadAudioDetails[i].Audiomastercat
                    detail.audioSubCategory = downloadAudioDetails[i].AudioSubCategory
                    detail.imageFile = downloadAudioDetails[i].ImageFile
                    detail.audioDuration = downloadAudioDetails[i].AudioDuration
                    details.add(detail)
                }
                for (i in listModel.indices) {
                    if (listModel[i].view.equals("My Downloads", ignoreCase = true)) {
                        listModel[i].details = details
                    }
                }
                val fragmentManager1: FragmentManager = (ctx as FragmentActivity).supportFragmentManager

                audioAdapter = AudioAdapter(listModel, ctx, binding, act, fragmentManager1, DB)
                binding.rvMainAudioList.adapter = audioAdapter
            } else {
                if (isNetworkConnected(act)) {
                    val fragmentManager1: FragmentManager = (ctx as FragmentActivity).supportFragmentManager
                    audioAdapter = AudioAdapter(listModel, ctx, binding, act, fragmentManager1, DB)
                    binding.rvMainAudioList.adapter = audioAdapter
                }
            }
        })
    }

    fun callMainPlayer(position: Int, views: String?, listModel: List<HomeDataModel.ResponseData.Audio.Detail>, ctx: Context, act: Activity, DB: AudioDatabase) {
        val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, AppCompatActivity.MODE_PRIVATE)
        val audioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        val playFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
        val playerPosition: Int = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
        val shared12 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE)
        val isPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "0")
        if ((audioPlayerFlag.equals("MainAudioList", ignoreCase = true) || audioPlayerFlag.equals("ViewAllAudioList", ignoreCase = true)) && playFrom.equals(views, ignoreCase = true)) {
            if (isNetworkConnected(ctx)) {
                if (isDisclaimer == 1) {
                    if (player != null) {
                        if (!player.playWhenReady) {
                            player.playWhenReady = true
                        }
                    } else {
                        audioClick = true
                    }
                    callMyPlayer(ctx, act)
                    showToast("The audio shall start playing after the disclaimer", act)
                } else {
                    if (player != null) {
                        if (position != playerPosition) {
                            player.seekTo(position, 0)
                            player.playWhenReady = true
                            val sharedxx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, AppCompatActivity.MODE_PRIVATE)
                            val editor = sharedxx.edit()
                            editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position)
                            editor.apply()
                        }
                        callMyPlayer(ctx, act)
                    } else {
                        callPlayer(position, views, listModel, ctx, act, true)
                    }
                }
            } else {
                if (views.equals("My Downloads") && !isNetworkConnected(ctx)) {
                    Log.e("download audio in gm", downloadAudioDetailsList.toString())
                    getMedia(views!!, audioPlayerFlag!!, position, listModel, ctx, act, DB, playerPosition)
                }
            }
        } else {
            val listModelList2 = arrayListOf<HomeDataModel.ResponseData.Audio.Detail>()
            listModelList2.addAll(listModel)
            val gson = Gson()
            val disclimerJson = shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
            val type = object : TypeToken<HomeDataModel.ResponseData.Audio.Detail?>() {}.type
            val arrayList = gson.fromJson<HomeDataModel.ResponseData.Audio.Detail>(disclimerJson, type)
            val mainPlayModel = HomeDataModel.ResponseData.Audio.Detail()
            mainPlayModel.id = arrayList.id
            mainPlayModel.name = arrayList.name
            mainPlayModel.audioFile = arrayList.audioFile
            mainPlayModel.audioDirection = arrayList.audioDirection
            mainPlayModel.audiomastercat = arrayList.audiomastercat
            mainPlayModel.audioSubCategory = arrayList.audioSubCategory
            mainPlayModel.imageFile = arrayList.imageFile
            mainPlayModel.audioDuration = arrayList.audioDuration
            var audioc = true
            if (isDisclaimer == 1) {
                if (player != null) {
                    player.playWhenReady = true
                    audioc = false
                    listModelList2.add(position, mainPlayModel)
                } else {
                    isDisclaimer = 0
                    if (isPlayDisclimer.equals("1", ignoreCase = true)) {
                        audioc = true
                        listModelList2.add(position, mainPlayModel)
                    }
                }
            } else {
                isDisclaimer = 0
                if (isPlayDisclimer.equals("1", ignoreCase = true)) {
                    audioc = true
                    listModelList2.add(position, mainPlayModel)
                }
            }
            if (isNetworkConnected(ctx)) {
                callPlayer(position, views, listModelList2, ctx, act, audioc)
            } else {
                if (views.equals("My Downloads") && !isNetworkConnected(ctx)) {
                    getMedia(views!!, audioPlayerFlag!!, position, listModel, ctx, act, DB, playerPosition)
                }
            }
        }
    }

    private fun getDownloadedList(ctx: Context, DB: AudioDatabase): ArrayList<String> {
        val shared = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, AppCompatActivity.MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        DB.taskDao()?.geAllLiveDataBYDownloaded("Complete", coUserId)?.observe(ctx as (LifecycleOwner), { audioList: List<String?>? ->
            downloadAudioDetailsList = audioList as ArrayList<String>
            Log.e("download audio in fun", downloadAudioDetailsList.toString())
        })
        return downloadAudioDetailsList
    }

    private fun getMedia(views: String?, AudioFlag: String, position: Int, listModelList: List<HomeDataModel.ResponseData.Audio.Detail>, ctx: Context, act: Activity, DB: AudioDatabase, playerPosition: Int) {
        val shared = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, AppCompatActivity.MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        DB.taskDao()?.geAllLiveDataBYDownloaded("Complete", coUserId)?.observe(ctx as (LifecycleOwner), { audioList: List<String?>? ->
            downloadAudioDetailsList = audioList as ArrayList<String>
            Log.e("download audio in fun", downloadAudioDetailsList.toString())
            var pos = 0
            if (AudioFlag.equals("DownloadListAudio", ignoreCase = true)) {
                if (isDisclaimer == 1) {
                    if (player != null) {
                        if (!player.playWhenReady) {
                            player.playWhenReady = true
                        }
                    } else {
                        audioClick = true
                    }
                    callMyPlayer(ctx, act)
                    showToast("The audio shall start playing after the disclaimer", act)
                } else {
                    val listModelList2 = arrayListOf<HomeDataModel.ResponseData.Audio.Detail>()
                    for (i in listModelList) {
                        if (downloadAudioDetailsList.contains(i.name)) {
                            listModelList2.add(i)
                        }
                    }
                    if (player != null) {
                        if (position != playerPosition) {
                            if (downloadAudioDetailsList.contains(listModelList[position].name)) {
                                pos = position
                                callPlayer(pos, views!!, listModelList2, ctx, act, true)
                            } else { //                                pos = 0;
                                showToast(ctx.getString(R.string.no_server_found), act)
                            }
                        } else {
                            callMyPlayer(ctx, act)
                        }
                        if (listModelList2.size == 0) { //                                callTransFrag(pos, listModelList2, true);
                            showToast(ctx.getString(R.string.no_server_found), act)
                        }
                    } else {
                        if (downloadAudioDetailsList.contains(listModelList[position].name)) {
                            pos = position
                            callPlayer(pos, views!!, listModelList2, ctx, act, true)
                        } else { //                                pos = 0;
                            showToast(ctx.getString(R.string.no_server_found), act)
                        }
                    }
                    if (listModelList2.size != 0) {
                        callPlayer(pos, views!!, listModelList2, ctx, act, true)
                    } else {
                        Log.e("else", "2")
                        showToast(ctx.getString(R.string.no_server_found), act)
                    }
                }
            } else {
                val shared12 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE)
                val isPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "0")
                val listModelList2 = arrayListOf<HomeDataModel.ResponseData.Audio.Detail>()
                for (i in listModelList) {
                    if (downloadAudioDetailsList.contains(i.name)) {
                        listModelList2.add(i)
                    }
                }
                Log.e("downloadded audio", downloadAudioDetailsList.toString())
                if (downloadAudioDetailsList.contains(listModelList[position].name)) {
                    pos = position
                    val gson = Gson()
                    val disclimerJson = shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
                    val type = object : TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio?>() {}.type
                    val arrayList = gson.fromJson<HomeScreenModel.ResponseData.DisclaimerAudio>(disclimerJson, type)
                    val mainPlayModel = HomeDataModel.ResponseData.Audio.Detail()
                    mainPlayModel.id = arrayList.id
                    mainPlayModel.name = arrayList.name
                    mainPlayModel.audioFile = arrayList.audioFile
                    mainPlayModel.audioDirection = arrayList.audioDirection
                    mainPlayModel.audiomastercat = arrayList.audiomastercat
                    mainPlayModel.audioSubCategory = arrayList.audioSubCategory
                    mainPlayModel.imageFile = arrayList.imageFile
                    mainPlayModel.audioDuration = arrayList.audioDuration
                    var audioc = true
                    if (isDisclaimer == 1) {
                        if (player != null) {
                            player.playWhenReady = true
                            audioc = false
                            listModelList2.add(pos, mainPlayModel)
                        } else {
                            isDisclaimer = 0
                            if (isPlayDisclimer.equals("1", ignoreCase = true)) {
                                audioc = true
                                listModelList2.add(pos, mainPlayModel)
                            }
                        }
                    } else {
                        isDisclaimer = 0
                        if (isPlayDisclimer.equals("1", ignoreCase = true)) {
                            listModelList2.add(pos, mainPlayModel)
                            audioc = true
                        }
                    }
                    if (listModelList2.size != 0) {
                        if (!listModelList2[pos].id.equals("0")) {
                            callPlayer(pos, views!!, listModelList2, ctx, act, audioc)
                        } else if (listModelList2[pos].id.equals("0") && listModelList2.size > 1) {
                            callPlayer(pos, views!!, listModelList2, ctx, act, audioc)
                        } else {
                            Log.e("else", "3")
                            showToast(ctx.getString(R.string.no_server_found), act)
                        }
                    } else {
                        Log.e("else", "4")
                        showToast(ctx.getString(R.string.no_server_found), act)
                    }
                } else {
                    Log.e("else", "5")
                    showToast(ctx.getString(R.string.no_server_found), act)
                }
            }

        })
    }

    private fun callMyPlayer(ctx: Context, act: Activity) {
        val i = Intent(ctx, MyPlayerActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        ctx.startActivity(i)
        act.overridePendingTransition(0, 0)
    }

    private fun callPlayer(position1: Int, view: String?, listModel: List<HomeDataModel.ResponseData.Audio.Detail>, ctx: Context, act: Activity, audioc: Boolean) {
        if (audioc) {
            GlobalInitExoPlayer.callNewPlayerRelease()
        }
        val shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, AppCompatActivity.MODE_PRIVATE)
        val editor = shared.edit()
        val gson = Gson()
        var json = ""
        var position = position1
        if (view.equals("My Downloads", true)) {
            val downloadAudioDetails = ArrayList<DownloadAudioDetails>()
            for (i in listModel.indices) {
                val mainPlayModel = DownloadAudioDetails()
                mainPlayModel.ID = listModel[i].id!!
                mainPlayModel.Name = listModel[i].name!!
                mainPlayModel.AudioFile = listModel[i].audioFile!!
                mainPlayModel.AudioDirection = listModel[i].audioDirection!!
                mainPlayModel.Audiomastercat = listModel[i].audiomastercat!!
                mainPlayModel.AudioSubCategory = listModel[i].audioSubCategory!!
                mainPlayModel.ImageFile = listModel[i].imageFile!!
                mainPlayModel.AudioDuration = listModel[i].audioDuration!!
                downloadAudioDetails.add(mainPlayModel)
            }
            json = gson.toJson(downloadAudioDetails)
            editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "DownloadListAudio")
        } else {
            if(IsLock == "1") {
                val listDetail = ArrayList<HomeDataModel.ResponseData.Audio.Detail>()
                for (i in listModel.indices) {
                    if(listModel[i].isPlay.equals("1")) {
                        val mainPlayModel = HomeDataModel.ResponseData.Audio.Detail()
                        mainPlayModel.id = listModel[i].id!!
                        mainPlayModel.name = listModel[i].name!!
                        mainPlayModel.audioFile = listModel[i].audioFile!!
                        mainPlayModel.isPlay = listModel[i].isPlay!!
                        mainPlayModel.audioDirection = listModel[i].audioDirection!!
                        mainPlayModel.audiomastercat = listModel[i].audiomastercat!!
                        mainPlayModel.audioSubCategory = listModel[i].audioSubCategory!!
                        mainPlayModel.imageFile = listModel[i].imageFile!!
                        mainPlayModel.audioDuration = listModel[i].audioDuration!!
                        listDetail.add(mainPlayModel)
                    }
                }
                position = if (position < listDetail.size) {
                    position
                } else {
                    0
                }
                json = gson.toJson(listDetail)
                editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "MainAudioList")
            }else{
                json = gson.toJson(listModel)
                editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "MainAudioList")
            }
        }
        editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json)
        editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position)
        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "")
        editor.putString(CONSTANTS.PREF_KEY_PlayFrom, view)
        editor.apply()
        audioClick = audioc
        callMyPlayer(ctx, act)
    }

    private fun getPlaylistAudio(PlaylistID: String, CoUserID: String, playlistSongs: List<HomeDataModel.ResponseData.SuggestedPlaylist.PlaylistSong>) {
        val audiolistDiff = arrayListOf<DownloadAudioDetails>()
        val audiolistDiff1 = arrayListOf<String>()
        DB = getAudioDataBase(ctx);
        DB.taskDao().getAllAudioByPlaylist1(PlaylistID, CoUserID).observe(this, { audioList: List<DownloadAudioDetails?> ->
            if (audioList.size == playlistSongs.size) {
                for (i in audioList) {
                    var found = false
                    for (j in playlistSongs) {
                        if (i!!.ID == j.id) {
                            found = true
                        }
                    }
                    if (!found) {
                        audiolistDiff.add(i!!)
                    }
                }
                if (audiolistDiff.isNotEmpty()) {
                    val sharedsa = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
                    val audioPlayerFlag = sharedsa.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "")
                    val playFrom = sharedsa.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
                    if (audioPlayerFlag.equals("playlist", ignoreCase = true) || audioPlayerFlag.equals("Downloadlist", ignoreCase = true)) {
                        if (playFrom.equals("Suggested", ignoreCase = true)) {
                            callAllRemovePlayer(ctx,act)
                        }
                    }
                    GetPlaylistMedia(PlaylistID, userId!!, ctx)
                }
            } else {
                val sharedsa = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
                val audioPlayerFlag = sharedsa.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "")
                val playFrom = sharedsa.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
                var gson: Gson = Gson()
                val json = sharedsa.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toString())
                var mainPlayModelList = ArrayList<MainPlayModel>()
                if (audioPlayerFlag != "0") {
                    if (!json.equals(gson.toString(), ignoreCase = true)) {
                        val type = object : TypeToken<ArrayList<MainPlayModel?>?>() {}.type
                        mainPlayModelList = gson.fromJson(json, type)
                    }
                }
                if (audioPlayerFlag.equals("playlist", ignoreCase = true) || audioPlayerFlag.equals("Downloadlist", ignoreCase = true)) {
                    if (playFrom.equals("Suggested", ignoreCase = true)) {
                        if (mainPlayModelList.size == playlistSongs.size) {
                            for (i in mainPlayModelList) {
                                var found = false
                                for (j in playlistSongs) {
                                    if (i.id == j.id) {
                                        found = true
                                    }
                                }
                                if (!found) {
                                    audiolistDiff1.add(i.id)
                                }
                            }
                            if (audiolistDiff1.isNotEmpty()) {
                                val sharedsa = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
                                val audioPlayerFlag = sharedsa.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "")
                                val playFrom = sharedsa.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
                                if (audioPlayerFlag.equals("playlist", ignoreCase = true) || audioPlayerFlag.equals("Downloadlist", ignoreCase = true)) {
                                    if (playFrom.equals("Suggested", ignoreCase = true)) {
                                        callAllRemovePlayer(ctx, act)
                                    }
                                }
                                GetPlaylistMedia(PlaylistID, userId!!, ctx)
                            }
                        } else {
                            callAllRemovePlayer(ctx, act)
                        }
                    }
                }
                GetPlaylistMedia(PlaylistID, userId!!, ctx)
            }
        })
    }

    private fun prepareData() {
        if (isNetworkConnected(ctx)) {
            showProgressBar(binding.progressBar, binding.progressBarHolder, act)
            val listCall = APINewClient.client.getHomeData(coUserId)
            listCall.enqueue(object : Callback<HomeDataModel?> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<HomeDataModel?>, response: Response<HomeDataModel?>) {
                    hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                    val listModel = response.body()!!
                    homelistModel = response.body()!!
                    if (listModel.responseCode.equals(ctx.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                        getDownloadedList(ctx, DB)
//                        Log.e("download audio", downloadAudioDetailsList.toString())
                        binding.llMainLayout.visibility = View.VISIBLE
                        binding.llSpace.visibility = View.VISIBLE
                        binding.llPlayer.visibility = View.VISIBLE
                        binding.llUser.visibility = View.VISIBLE
                        binding.tvPlaylistName.text = listModel.responseData!!.suggestedPlaylist!!.playlistName
                        binding.tvSleepTimeTitle.text = listModel.responseData!!.suggestedPlaylist!!.playlistDirection
                        binding.tvTime.text = listModel.responseData!!.suggestedPlaylist!!.totalhour + ":" + listModel.responseData!!.suggestedPlaylist!!.totalminute
                        val section = java.util.ArrayList<String>()
                        for (i in listModel.responseData!!.audio.indices) {
                            section.add(listModel.responseData!!.audio[i].view!!)
                        }
                        val p = Properties()

                        val gson: Gson
                        val gsonBuilder = GsonBuilder()
                        gson = gsonBuilder.create()
                        p.putValue("sections", gson.toJson(section))
                        addToSegment("Enhance Screen Viewed", p, CONSTANTS.screen)
                        val measureRatio = measureRatio(ctx, 0f, 1f, 1f, 0.38f, 0f)
                        binding.ivCreatePlaylist.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
                        binding.ivCreatePlaylist.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
                        binding.ivCreatePlaylist.scaleType = ImageView.ScaleType.FIT_XY
                        Glide.with(act).load(R.drawable.ic_create_playlist).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(20))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivCreatePlaylist)

                        IsLock = listModel.responseData!!.IsLock
                        playlistAdapter = PlaylistAdapter(listModel.responseData!!.playlist[0], ctx, binding, act, DB)
                        binding.rvMainPlayList.adapter = playlistAdapter

                        if (listModel.responseData!!.playlist[0].details!!.size > 4) {
                            binding.tvViewAll.visibility = View.VISIBLE
                        } else {
                            binding.tvViewAll.visibility = View.GONE
                        }
                        getPlaylistDetail(listModel.responseData!!.suggestedPlaylist!!.playlistID!!, DB, listModel.responseData!!.suggestedPlaylist!!.playlistSongs)
                        LocalBroadcastManager.getInstance(ctx).registerReceiver(listener1, IntentFilter("Reminder"))
                        binding.llPlayerView1.setOnClickListener {
                            callPlaylistDetails()
                        }
                        binding.llPlayerView2.setOnClickListener {
                            callPlaylistDetails()
                        }
                        binding.llPlaylistDetails.setOnClickListener {
                            callPlaylistDetails()
                        }
                        if (listModel.responseData!!.suggestedPlaylist!!.isReminder.equals("0", ignoreCase = true) || listModel.responseData!!.suggestedPlaylist!!.isReminder.equals("", ignoreCase = true)) {
                            binding.tvReminder.text = "Set reminder"
                            binding.llSetReminder.setBackgroundResource(R.drawable.rounded_extra_theme_corner)
                        } else if (listModel.responseData!!.suggestedPlaylist!!.isReminder.equals("1", ignoreCase = true)) {
                            binding.tvReminder.text = "Update reminder"
                            binding.llSetReminder.setBackgroundResource(R.drawable.rounded_extra_dark_theme_corner)
                        } else if (listModel.responseData!!.suggestedPlaylist!!.isReminder.equals("2", ignoreCase = true)) {
                            binding.tvReminder.text = "Update reminder"
                            binding.llSetReminder.setBackgroundResource(R.drawable.rounded_extra_theme_corner)
                        }

                        binding.tvReminder.setOnClickListener {
                            if (IsLock.equals("1")) {
                                callEnhanceActivity(ctx, act)
                            } else if (IsLock.equals("0")) {
                                if (listModel.responseData!!.suggestedPlaylist!!.isReminder.equals("0", ignoreCase = true) || listModel.responseData!!.suggestedPlaylist!!.isReminder.equals("", ignoreCase = true)) {
                                    binding.tvReminder.text = "Set reminder"
                                    binding.llSetReminder.setBackgroundResource(R.drawable.rounded_extra_theme_corner)
                                } else if (listModel.responseData!!.suggestedPlaylist!!.isReminder.equals("1", ignoreCase = true)) {
                                    binding.tvReminder.text = "Update reminder"
                                    binding.llSetReminder.setBackgroundResource(R.drawable.rounded_extra_dark_theme_corner)
                                } else if (listModel.responseData!!.suggestedPlaylist!!.isReminder.equals("2", ignoreCase = true)) {
                                    binding.tvReminder.text = "Update reminder"
                                    binding.llSetReminder.setBackgroundResource(R.drawable.rounded_extra_theme_corner)
                                }
                                getReminderDay(ctx, act, coUserId, listModel.responseData!!.suggestedPlaylist!!.playlistID, listModel.responseData!!.suggestedPlaylist!!.playlistName, activity, listModel.responseData!!.suggestedPlaylist!!.reminderTime, listModel.responseData!!.suggestedPlaylist!!.reminderDay, "0", listModel.responseData!!.suggestedPlaylist!!.reminderId, listModel.responseData!!.suggestedPlaylist!!.isReminder, "2")
                            }
                        }

                        setPlayPauseIcon()
                        LocalBroadcastManager.getInstance(ctx).registerReceiver(listener, IntentFilter("play_pause_Action"))
                        binding.llPlayPause.setOnClickListener {
                            if (IsLock.equals("1")) {
                                callEnhanceActivity(ctx, act)
                            } else if (IsLock.equals("0")) {
                                if (isNetworkConnected(activity)) {
                                    val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, AppCompatActivity.MODE_PRIVATE)
                                    val playerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                                    when (isPlayPlaylist) {
                                        1 -> {
                                            player.playWhenReady = false
                                            isPlayPlaylist = 2
                                            binding.llPlay.visibility = View.VISIBLE
                                            binding.llPause.visibility = View.GONE
                                        }
                                        2 -> {
                                            if (player != null) {
                                                val lastIndexID = listModel.responseData!!.suggestedPlaylist!!.playlistSongs!![listModel.responseData!!.suggestedPlaylist!!.playlistSongs!!.size - 1].id
                                                if (PlayerAudioId.equals(lastIndexID, ignoreCase = true) && player.duration - player.currentPosition <= 20) {
                                                    val shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
                                                    val editor = shared.edit()
                                                    editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                                                    editor.apply()
                                                    player.seekTo(0, 0)
                                                    PlayerAudioId = listModel.responseData!!.suggestedPlaylist!!.playlistSongs!![0].id
                                                    player.playWhenReady = true
                                                } else {
                                                    player.playWhenReady = true
                                                }
                                            }
                                            isPlayPlaylist = 1
                                            binding.llPlay.visibility = View.GONE
                                            binding.llPause.visibility = View.VISIBLE
                                        }
                                        else -> {
                                            PlayerAudioId = listModel.responseData!!.suggestedPlaylist!!.playlistSongs!![playerPosition].id
                                            callMainPlayerSuggested(0, "", listModel.responseData!!.suggestedPlaylist!!.playlistSongs!!, ctx, act, listModel.responseData!!.suggestedPlaylist!!.playlistID!!, listModel.responseData!!.suggestedPlaylist!!.playlistName!!)
                                            binding.llPlay.visibility = View.GONE
                                            binding.llPause.visibility = View.VISIBLE
                                        }
                                    }
                                } else {
                                    showToast(ctx.getString(R.string.no_server_found), activity)
                                }
                            }
                        }

                        callObserverMethod(listModel.responseData!!.audio, act, DB)
                    } else if (listModel.responseCode.equals(ctx.getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
                        deleteCall(act)
                        showToast(listModel.responseMessage, act)
                        val i = Intent(act, SignInActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                        i.putExtra("mobileNo", "")
                        i.putExtra("countryCode", "")
                        i.putExtra("name", "")
                        i.putExtra("email", "")
                        i.putExtra("countryShortName", "")
                        startActivity(i)
                        act.finish()
                    } else {
                        showToast(listModel.responseMessage, activity)
                    }
                }

                private fun callPlaylistDetails() {
                    if (IsLock.equals("1")) {
                        binding.ivLock.visibility = View.VISIBLE
                        callEnhanceActivity(ctx, act)
                    } else if (IsLock.equals("0")) {
                        binding.ivLock.visibility = View.GONE
                        if (isNetworkConnected(activity)) {
                            try {
                                callMyPlaylistActivity("0", homelistModel.responseData!!.suggestedPlaylist!!.playlistID, homelistModel.responseData!!.suggestedPlaylist!!.playlistName, act)
                                act.overridePendingTransition(0, 0)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            showToast(ctx.getString(R.string.no_server_found), activity)
                        }
                    }
                }

                override fun onFailure(call: Call<HomeDataModel?>, t: Throwable) {
                    hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                }
            })
        } else {
            val responseData = ArrayList<HomeDataModel.ResponseData.Audio>()
            val details = ArrayList<HomeDataModel.ResponseData.Audio.Detail>()
            val listModel = HomeDataModel.ResponseData.Audio()
            listModel.homeAudioID = "6"
            listModel.details = details
            listModel.view = "My Downloads"
            listModel.userId = coUserId
            responseData.add(listModel)
            callObserverMethod(responseData, act, DB) //            showToast(getString(R.string.no_server_found), act)
        }
    }

    private fun setPlayPauseIcon() {
        val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, AppCompatActivity.MODE_PRIVATE)
        val audioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        val myPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
        if (myDownloads.equals("1", ignoreCase = true)) {
            if (audioPlayerFlag.equals("Downloadlist", ignoreCase = true) && myPlaylist.equals(homelistModel.responseData!!.suggestedPlaylist!!.playlistID, ignoreCase = true)) {
                if (player != null) {
                    if (player.playWhenReady) {
                        isPlayPlaylist = 1 //                    handler3.postDelayed(UpdateSongTime3, 500);
                        binding.llPause.visibility = View.VISIBLE
                        binding.llPlay.visibility = View.GONE
                    } else {
                        isPlayPlaylist = 2 //                    handler3.postDelayed(UpdateSongTime3, 500);
                        binding.llPause.visibility = View.GONE
                        binding.llPlay.visibility = View.VISIBLE
                    }
                } else {
                    isPlayPlaylist = 0
                    binding.llPause.visibility = View.GONE
                    binding.llPlay.visibility = View.VISIBLE
                }
            } else {
                isPlayPlaylist = 0
                binding.llPause.visibility = View.GONE
                binding.llPlay.visibility = View.VISIBLE
            }
        } else {
            if (audioPlayerFlag.equals("playlist", ignoreCase = true) && myPlaylist.equals(homelistModel.responseData!!.suggestedPlaylist!!.playlistID, ignoreCase = true)) {
                if (player != null) {
                    if (player.playWhenReady) {
                        isPlayPlaylist = 1
                        binding.llPause.visibility = View.VISIBLE
                        binding.llPlay.visibility = View.GONE
                    } else {
                        isPlayPlaylist = 2
                        binding.llPause.visibility = View.GONE
                        binding.llPlay.visibility = View.VISIBLE
                    }
                } else {
                    isPlayPlaylist = 0
                    binding.llPause.visibility = View.GONE
                    binding.llPlay.visibility = View.VISIBLE
                }
            } else {
                isPlayPlaylist = 0
                binding.llPause.visibility = View.GONE
                binding.llPlay.visibility = View.VISIBLE
            }
        }
    }

    private fun getAllCompletedMedia(audioFlag: String?, pID: String, pName: String, position: Int, listModel: List<HomeDataModel.ResponseData.SuggestedPlaylist.PlaylistSong>, ctx: Context, act: Activity, DB: AudioDatabase) {
        DB.taskDao()?.geAllLiveDataBYDownloaded("Complete", coUserId)?.observe(ctx as (LifecycleOwner), { audioList: List<String?>? ->
            downloadAudioDetailsList = audioList as ArrayList<String>
            Log.e("download audio in fun", downloadAudioDetailsList.toString())
            var pos = 0
            val shared: SharedPreferences = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
            var positionSaved = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
            val myPlaylist = shared.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
            val shared12 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE)
            val isPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "0")
            if (audioFlag.equals("Downloadlist", ignoreCase = true) && myPlaylist.equals(pID, ignoreCase = true)) {
                if (isDisclaimer == 1) {
                    if (player != null) {
                        if (!player.playWhenReady) {
                            player.playWhenReady = true
                        } else player.playWhenReady = true
                    } else {
                        audioClick = true
                    }
                    callMyPlayer(ctx, act)
                    showToast("The audio shall start playing after the disclaimer", act)
                } else {
                    val listModelList2 = arrayListOf<HomeDataModel.ResponseData.SuggestedPlaylist.PlaylistSong>()
                    for (i in listModel) {
                        if (downloadAudioDetailsList.contains(i.name)) {
                            listModelList2.add(i)
                        }
                    }
                    if (position != positionSaved) {
                        if (downloadAudioDetailsList.contains(listModel[position].name)) {
                            positionSaved = position
                            PlayerAudioId = listModel[position].id
                            if (listModelList2.size != 0) {
                                callPlayerSuggested(pos, "", listModelList2, ctx, act, pID, pName, true)
                            } else {
                                showToast(ctx.getString(R.string.no_server_found), act)
                            }
                        } else { //                                pos = 0;
                            showToast(ctx.getString(R.string.no_server_found), act)
                        }
                    } //                SegmentTag()
                }
            } else {
                val listModelList2 = arrayListOf<HomeDataModel.ResponseData.SuggestedPlaylist.PlaylistSong>()
                for (i in listModel) {
                    if (downloadAudioDetailsList.contains(i.name)) {
                        listModelList2.add(i)
                    }
                }
                if (downloadAudioDetailsList.contains(listModel[position].name)) {
                    pos = position
                    val gson = Gson()
                    val disclimerJson = shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
                    val type = object : TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio?>() {}.type
                    val arrayList = gson.fromJson<HomeScreenModel.ResponseData.DisclaimerAudio>(disclimerJson, type)
                    val mainPlayModel = HomeDataModel.ResponseData.SuggestedPlaylist.PlaylistSong()
                    mainPlayModel.id = arrayList.id
                    mainPlayModel.name = arrayList.name
                    mainPlayModel.audioFile = arrayList.audioFile
                    mainPlayModel.audioDirection = arrayList.audioDirection
                    mainPlayModel.audiomastercat = arrayList.audiomastercat
                    mainPlayModel.audioSubCategory = arrayList.audioSubCategory
                    mainPlayModel.imageFile = arrayList.imageFile
                    mainPlayModel.audioDuration = arrayList.audioDuration
                    var audioc = true
                    if (isDisclaimer == 1) {
                        if (player != null) {
                            player.playWhenReady = true
                            audioc = false
                            listModelList2.add(pos, mainPlayModel)
                        } else {
                            isDisclaimer = 0
                            if (isPlayDisclimer.equals("1", ignoreCase = true)) {
                                audioc = true
                                listModelList2.add(pos, mainPlayModel)
                            }
                        }
                    } else {
                        isDisclaimer = 0
                        if (isPlayDisclimer.equals("1", ignoreCase = true)) {
                            audioc = true
                            listModelList2.add(pos, mainPlayModel)
                        }
                    }
                    if (listModelList2.size != 0) {
                        if (!listModelList2[pos].id.equals("0")) {
                            if (listModelList2.size != 0) {
                                callPlayerSuggested(pos, "", listModelList2, ctx, act, pID, pName, audioc)
                            } else {
                                showToast(ctx.getString(R.string.no_server_found), act)
                            }
                        } else if (listModelList2[pos].id.equals("0") && listModelList2.size > 1) {
                            callPlayerSuggested(pos, "", listModelList2, ctx, act, pID, pName, audioc)
                        } else {
                            showToast(ctx.getString(R.string.no_server_found), act)
                        }
                    } else {
                        showToast(ctx.getString(R.string.no_server_found), act)
                    }
                } else {
                    showToast(ctx.getString(R.string.no_server_found), act)
                } //            SegmentTag()
            }
        })
    }

    private fun callMainPlayerSuggested(position: Int, views: String?, listModel: List<HomeDataModel.ResponseData.SuggestedPlaylist.PlaylistSong>, ctx: Context, act: Activity, playlistID: String, playlistName: String) {
        val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
        val audioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        val myPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
        val myPlaylistName = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "")
        val playerPosition: Int = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
        val shared12 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE)
        val isPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "0")
        if (myDownloads.equals("1", true)) {
            if (isNetworkConnected(ctx)) {
                if (audioPlayerFlag.equals("Downloadlist", ignoreCase = true) && myPlaylist.equals(playlistID, ignoreCase = true)) {
                    if (isDisclaimer == 1) {
                        if (player != null) {
                            if (!player.playWhenReady) {
                                player.playWhenReady = true
                            }
                        } else {
                            audioClick = true
                        }
                        callMyPlayer(ctx, act)
                        showToast("The audio shall start playing after the disclaimer", act)
                    } else {
                        if (player != null) {
                            if (position != playerPosition) {
                                player.seekTo(position, 0)
                                player.playWhenReady = true
                                val sharedxx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
                                val editor = sharedxx.edit()
                                editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position)
                                editor.apply()
                            }
                            callMyPlayer(ctx, act)
                        } else {
                            callPlayerSuggested(position, views, listModel, ctx, act, playlistID, playlistName, true)
                        }
                    }
                } else {
                    val listModelList2 = arrayListOf<HomeDataModel.ResponseData.SuggestedPlaylist.PlaylistSong>()
                    listModelList2.addAll(listModel)
                    val gson = Gson()
                    val disclimerJson = shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
                    val type = object : TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio?>() {}.type
                    val arrayList = gson.fromJson<HomeScreenModel.ResponseData.DisclaimerAudio>(disclimerJson, type)
                    val mainPlayModel = HomeDataModel.ResponseData.SuggestedPlaylist.PlaylistSong()
                    mainPlayModel.id = arrayList.id
                    mainPlayModel.name = arrayList.name
                    mainPlayModel.audioFile = arrayList.audioFile
                    mainPlayModel.audioDirection = arrayList.audioDirection
                    mainPlayModel.audiomastercat = arrayList.audiomastercat
                    mainPlayModel.audioSubCategory = arrayList.audioSubCategory
                    mainPlayModel.imageFile = arrayList.imageFile
                    mainPlayModel.audioDuration = arrayList.audioDuration
                    var audioc = true
                    if (isDisclaimer == 1) {
                        if (player != null) {
                            player.playWhenReady = true
                            audioc = false
                            listModelList2.add(position, mainPlayModel)
                        } else {
                            isDisclaimer = 0
                            if (isPlayDisclimer.equals("1", ignoreCase = true)) {
                                audioc = true
                                listModelList2.add(position, mainPlayModel)
                            }
                        }
                    } else {
                        isDisclaimer = 0
                        if (isPlayDisclimer.equals("1", ignoreCase = true)) {
                            audioc = true
                            listModelList2.add(position, mainPlayModel)
                        }
                    }
                    callPlayerSuggested(position, views, listModelList2, ctx, act, playlistID, playlistName, audioc)
                }
            } else {
                getAllCompletedMedia(audioPlayerFlag, playlistID, playlistName, position, listModel, ctx, act, DB)
            }
        } else {
            if (audioPlayerFlag.equals("playlist", ignoreCase = true) && myPlaylist.equals(playlistID, ignoreCase = true)) {
                if (isDisclaimer == 1) {
                    if (player != null) {
                        if (!player.playWhenReady) {
                            player.playWhenReady = true
                        }
                    } else {
                        audioClick = true
                    }
                    callMyPlayer(ctx, act)
                    showToast("The audio shall start playing after the disclaimer", act)
                } else {
                    if (player != null) {
                        if (position != playerPosition) {
                            player.seekTo(position, 0)
                            player.playWhenReady = true
                            val sharedxx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
                            val editor = sharedxx.edit()
                            editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position)
                            editor.apply()
                        }
                        callMyPlayer(ctx, act)
                    } else {
                        callPlayerSuggested(position, views, listModel, ctx, act, playlistID, playlistName, true)
                    }
                }
            } else {
                val listModelList2 = arrayListOf<HomeDataModel.ResponseData.SuggestedPlaylist.PlaylistSong>()
                listModelList2.addAll(listModel)
                val gson = Gson()
                val disclimerJson = shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
                val type = object : TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio?>() {}.type
                val arrayList = gson.fromJson<HomeScreenModel.ResponseData.DisclaimerAudio>(disclimerJson, type)
                val mainPlayModel = HomeDataModel.ResponseData.SuggestedPlaylist.PlaylistSong()
                mainPlayModel.id = arrayList.id
                mainPlayModel.name = arrayList.name
                mainPlayModel.audioFile = arrayList.audioFile
                mainPlayModel.audioDirection = arrayList.audioDirection
                mainPlayModel.audiomastercat = arrayList.audiomastercat
                mainPlayModel.audioSubCategory = arrayList.audioSubCategory
                mainPlayModel.imageFile = arrayList.imageFile
                mainPlayModel.audioDuration = arrayList.audioDuration
                var audioc = true
                if (isDisclaimer == 1) {
                    if (player != null) {
                        player.playWhenReady = true
                        audioc = false
                        listModelList2.add(position, mainPlayModel)
                    } else {
                        isDisclaimer = 0
                        if (isPlayDisclimer.equals("1", ignoreCase = true)) {
                            audioc = true
                            listModelList2.add(position, mainPlayModel)
                        }
                    }
                } else {
                    isDisclaimer = 0
                    if (isPlayDisclimer.equals("1", ignoreCase = true)) {
                        audioc = true
                        listModelList2.add(position, mainPlayModel)
                    }
                }
                callPlayerSuggested(position, views, listModelList2, ctx, act, playlistID, playlistName, audioc)
            }
        }
    }

    private fun getPlaylistDetail(PlaylistID: String, DB: AudioDatabase, playlistSongs: List<HomeDataModel.ResponseData.SuggestedPlaylist.PlaylistSong>?) {
        try {
            DB.taskDao()?.getPlaylist1(PlaylistID, coUserId)?.observe(this, { audioList: List<DownloadPlaylistDetails?> ->
                if (audioList.isNotEmpty()) {
                    myDownloads = "1"
                    getPlaylistAudio(PlaylistID, userId!!, playlistSongs!!)

                } else {
                    myDownloads = "0"
                }
            })
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun callPlayerSuggested(position: Int, view: String?, listModel: List<HomeDataModel.ResponseData.SuggestedPlaylist.PlaylistSong>, ctx: Context, act: Activity, playlistID: String, playlistName: String, audioc: Boolean) {
        if (audioc) {
            GlobalInitExoPlayer.callNewPlayerRelease()
        }
        val shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
        val editor = shared.edit()
        val gson = Gson()

        val downloadAudioDetails = ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>()
        for (i in listModel.indices) {
            val mainPlayModel = PlaylistDetailsModel.ResponseData.PlaylistSong()
            mainPlayModel.id = listModel[i].id!!
            mainPlayModel.name = listModel[i].name!!
            mainPlayModel.audioFile = listModel[i].audioFile!!
            mainPlayModel.audioDirection = listModel[i].audioDirection!!
            mainPlayModel.audiomastercat = listModel[i].audiomastercat!!
            mainPlayModel.audioSubCategory = listModel[i].audioSubCategory!!
            mainPlayModel.imageFile = listModel[i].imageFile!!
            mainPlayModel.audioDuration = listModel[i].audioDuration!!
            downloadAudioDetails.add(mainPlayModel)
        }
        val json = gson.toJson(downloadAudioDetails)
        editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json)
        editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position)
        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, playlistID)
        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, playlistName)
        editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "Suggested")
        if (myDownloads.equals("1", ignoreCase = true)) {
            editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "Downloadlist")
        } else {
            editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "playlist")
        }
        editor.apply()
        audioClick = audioc
        callMyPlayer(ctx, act)
    }

    private fun callAddToplaylistClick(event: String, listModel: List<HomeDataModel.ResponseData.Audio.Detail>, listm: List<HomeDataModel.ResponseData.Play.Detail>?, position: Int, ctx: Context) {

        val i = Intent(ctx, AddPlaylistActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val p = Properties()
        if (event.equals("Playlist View All Screen")) {
            p.putValue("playlistId", listm!![position].playlistID)
            p.putValue("playlistName", listm[position].playlistName)
            p.putValue("source", event)
            if (listm[position].created.equals("1", ignoreCase = true)) {
                p.putValue("playlistType", "Created")
            } else if (listm[position].created == "0") {
                p.putValue("playlistType", "Default")
            } else if (listm[position].created.equals("2")) p.putValue("playlistType", "Suggested")

            if (listm[position].totalhour == "") {
                p.putValue("playlistDuration", "0h " + listm[position].totalhour + "m")
            } else if (listm[position].totalminute == "") {
                p.putValue("playlistDuration", listm[position].totalhour + "h 0m")
            } else {
                p.putValue("playlistDuration", listm[position].totalhour + "h " + listm[position].totalminute + "m")
            }
            i.putExtra("PlaylistID", listm[position].playlistID)
            i.putExtra("AudioId", "")
        } else {
            p.putValue("audioId", listModel[position].id)
            p.putValue("audioName", listModel[position].name)
            p.putValue("audioDescription", "")
            p.putValue("directions", listModel[position].audioDirection)
            p.putValue("masterCategory", listModel[position].audiomastercat)
            p.putValue("subCategory", listModel[position].audioSubCategory)
            p.putValue("audioDuration", listModel[position].audioDuration)
            p.putValue("position", GetCurrentAudioPosition())
            if (downloadAudioDetailsList.contains(listModel[position].name)) {
                p.putValue("audioType", "Downloaded")
            } else {
                p.putValue("audioType", "Streaming")
            }
            p.putValue("source", event)
            p.putValue("audioService", appStatus(ctx))
            p.putValue("bitRate", "")
            p.putValue("sound", hundredVolume.toString())
            i.putExtra("AudioId", listModel[position].id)
            i.putExtra("PlaylistID", "")
        }
        addToSegment("Add To Playlist Clicked", p, CONSTANTS.track)
        i.putExtra("ScreenView", event)
        i.putExtra("PlaylistName", "")
        i.putExtra("PlaylistImage", "")
        i.putExtra("PlaylistType", "")
        i.putExtra("Liked", "0")
        ctx.startActivity(i)
    }

    class AudioAdapter(private val listModel: List<HomeDataModel.ResponseData.Audio>, private val ctx: Context, var binding: FragmentManageBinding, val act: Activity, var fragmentManager1: FragmentManager, var DB: AudioDatabase) : RecyclerView.Adapter<AudioAdapter.MyViewHolder>() {

        inner class MyViewHolder(var binding: MainAudioLayoutBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: MainAudioLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.main_audio_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvViewAll.setOnClickListener {
                if (isNetworkConnected(ctx)) {
                    val bundle = Bundle()
                    bundle.putString("ID", listModel[position].homeAudioID)
                    bundle.putString("Name", listModel[position].view)
                    bundle.putString("Category", "")
                    val viewAllAudioFragment: Fragment = ViewAllAudioFragment()
                    viewAllAudioFragment.arguments = bundle
                    fragmentManager1.beginTransaction().replace(R.id.flContainer, viewAllAudioFragment).commit()
                } else {
                    showToast(ctx.getString(R.string.no_server_found), act)
                }
            }

            if (listModel[position].details!!.isEmpty()) {
                holder.binding.llMainLayout.visibility = View.GONE
            } else {
                holder.binding.llMainLayout.visibility = View.VISIBLE
                holder.binding.tvTitle.text = listModel[position].view
                if (listModel[position].view.equals("My Downloads", ignoreCase = true)) {
                    val myDownloads: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
                    holder.binding.rvMainAudio.layoutManager = myDownloads
                    holder.binding.rvMainAudio.itemAnimator = DefaultItemAnimator()
                    val myDownloadsAdapter = DownloadAdapter(listModel[position].details!!, ctx, binding, act, listModel[position].view, DB)
                    holder.binding.rvMainAudio.adapter = myDownloadsAdapter
                    if (listModel[position].details != null && listModel[position].details!!.size > 4) {
                        holder.binding.tvViewAll.visibility = View.VISIBLE
                    } else {
                        holder.binding.tvViewAll.visibility = View.GONE
                    }
                } else if (listModel[position].view.equals(ctx.getString(R.string.Library), ignoreCase = true)) {
                    val recommendedAdapter = LibraryAdapter(listModel[position].details!!, ctx, binding, act, listModel[position].view, DB)
                    val recommended: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
                    holder.binding.rvMainAudio.layoutManager = recommended
                    holder.binding.rvMainAudio.itemAnimator = DefaultItemAnimator()
                    holder.binding.rvMainAudio.adapter = recommendedAdapter
                    if (listModel[position].details != null && listModel[position].details!!.size > 4) {
                        holder.binding.tvViewAll.visibility = View.VISIBLE
                    } else {
                        holder.binding.tvViewAll.visibility = View.GONE
                    }
                } else if (listModel[position].view.equals(ctx.getString(R.string.my_like), ignoreCase = true)) {
                    holder.binding.llMainLayout.visibility = View.GONE/*RecentlyPlayedAdapter recentlyPlayedAdapter = new RecentlyPlayedAdapter(listModel.details!!, ctx);
                    RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
                    holder.binding.rvMainAudio.setLayoutManager(recentlyPlayed);
                    holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
                    holder.binding.rvMainAudio.setAdapter(recentlyPlayedAdapter);*/
                } else if (listModel[position].view.equals(ctx.getString(R.string.recently_played), ignoreCase = true)) {
                    val recentlyPlayedAdapter = RecentlyPlayedAdapter(listModel[position].details!!, ctx, binding, act, listModel[position].view, DB)
                    val recentlyPlayed: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
                    holder.binding.rvMainAudio.layoutManager = recentlyPlayed
                    holder.binding.rvMainAudio.itemAnimator = DefaultItemAnimator()
                    holder.binding.rvMainAudio.adapter = recentlyPlayedAdapter
                    if (listModel[position].details!!.size > 6) {
                        holder.binding.tvViewAll.visibility = View.VISIBLE
                    } else {
                        holder.binding.tvViewAll.visibility = View.GONE
                    }
                } else if (listModel[position].view.equals(ctx.getString(R.string.get_inspired), ignoreCase = true)) {
                    val recommendedAdapter = RecommendedAdapter(listModel[position].details!!, ctx, binding, act, listModel[position].view, DB)
                    val inspired: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
                    holder.binding.rvMainAudio.layoutManager = inspired
                    holder.binding.rvMainAudio.itemAnimator = DefaultItemAnimator()
                    holder.binding.rvMainAudio.adapter = recommendedAdapter
                    if (listModel[position].details!!.size > 4) {
                        holder.binding.tvViewAll.visibility = View.VISIBLE
                    } else {
                        holder.binding.tvViewAll.visibility = View.GONE
                    }
                } else if (listModel[position].view.equals(ctx.getString(R.string.recommended_audio), ignoreCase = true)) {
                    val recommendedAdapter = RecommendedAdapter(listModel[position].details!!, ctx, binding, act, listModel[position].view, DB)
                    val inspired: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
                    holder.binding.rvMainAudio.layoutManager = inspired
                    holder.binding.rvMainAudio.itemAnimator = DefaultItemAnimator()
                    holder.binding.rvMainAudio.adapter = recommendedAdapter
                    if (listModel[position].details!!.size > 4) {
                        holder.binding.tvViewAll.visibility = View.VISIBLE
                    } else {
                        holder.binding.tvViewAll.visibility = View.GONE
                    }
                } else if (listModel[position].view.equals(ctx.getString(R.string.popular_audio), ignoreCase = true)) {
                    val popularPlayedAdapter = PopularPlayedAdapter(listModel[position].details!!, ctx, binding, act, listModel[position].view, DB)
                    val recentlyPlayed: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
                    holder.binding.rvMainAudio.layoutManager = recentlyPlayed
                    holder.binding.rvMainAudio.itemAnimator = DefaultItemAnimator()
                    holder.binding.rvMainAudio.adapter = popularPlayedAdapter
                    if (listModel[position].details!!.size > 6) {
                        holder.binding.tvViewAll.visibility = View.VISIBLE
                    } else {
                        holder.binding.tvViewAll.visibility = View.GONE
                    }
                } else if (listModel[position].view.equals(ctx.getString(R.string.top_categories), ignoreCase = true)) {
                    holder.binding.tvViewAll.visibility = View.GONE
                    val topCategoriesAdapter = TopCategoriesAdapter(listModel[position].details!!, ctx, binding, act, listModel[position].homeAudioID.toString(), listModel[position].view, fragmentManager1)
                    val topCategories: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
                    holder.binding.rvMainAudio.layoutManager = topCategories
                    holder.binding.rvMainAudio.itemAnimator = DefaultItemAnimator()
                    holder.binding.rvMainAudio.adapter = topCategoriesAdapter
                }
            }
        }

        override fun getItemCount(): Int {
            return listModel.size
        }
    }

    class PlaylistAdapter(private val listModel: HomeDataModel.ResponseData.Play, private val ctx: Context, var binding: FragmentManageBinding, val act: Activity, var DB: AudioDatabase) : RecyclerView.Adapter<PlaylistAdapter.MyViewHolder>() {
        var index = -1

        inner class MyViewHolder(var binding: PlaylistCustomLayoutBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: PlaylistCustomLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.playlist_custom_layout, parent, false)
            return MyViewHolder(v)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val measureRatio = measureRatio(ctx, 0f, 1f, 1f, 0.38f, 0f)
            holder.binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            holder.binding.tvAddToPlaylist.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.tvAddToPlaylist.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            val measureRatio1 = measureRatio(ctx, 0f, 1f, 1f, 0.38f, 0f)
            holder.binding.rlMainLayout.layoutParams.height = (measureRatio1.height * measureRatio1.ratio).toInt()
            holder.binding.rlMainLayout.layoutParams.width = (measureRatio1.widthImg * measureRatio1.ratio).toInt()
            holder.binding.tvPlaylistName.text = listModel.details!![position].playlistName
            Glide.with(ctx).load(listModel.details!![position].playlistImage).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(34))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)

            if (IsLock.equals("1")) {
                holder.binding.ivLock.visibility = View.VISIBLE
                binding.ivLock.visibility = View.VISIBLE
                binding.ivLockCreate.visibility = View.VISIBLE
            } else {
                holder.binding.ivLock.visibility = View.GONE
                binding.ivLock.visibility = View.GONE
                binding.ivLockCreate.visibility = View.GONE
            }

            if (index == position) {
                holder.binding.tvAddToPlaylist.visibility = View.VISIBLE
            } else holder.binding.tvAddToPlaylist.visibility = View.GONE

            holder.binding.tvAddToPlaylist.text = "Add To Playlist"

            holder.binding.rlMainLayout.setOnLongClickListener {
                if (IsLock.equals("1")) {
                    callEnhanceActivity(ctx, act)
                } else if (IsLock.equals("0")) {
                    holder.binding.tvAddToPlaylist.visibility = View.VISIBLE
                    index = position
                    notifyDataSetChanged()
                }
                true
            }

            holder.binding.tvAddToPlaylist.setOnClickListener {
                if (IsLock.equals("1")) {
                    callEnhanceActivity(ctx, act)
                } else if (IsLock.equals("0")) {
                    val listm = arrayListOf<HomeDataModel.ResponseData.Audio.Detail>()
                    ManageFragment().callAddToplaylistClick("Playlist View All Screen", listm, listModel.details, position, ctx)
                }
            }


            holder.binding.rlMainLayout.setOnClickListener {
                if (IsLock.equals("1")) {
                    callEnhanceActivity(ctx, act)
                } else if (IsLock.equals("0")) {
                    if (isNetworkConnected(ctx)) {
                        ManageFragment().callMyPlaylistActivity("0", listModel.details!![position].playlistID, listModel.details!![position].playlistName, act)
                        act.overridePendingTransition(0, 0)
                    } else {
                        showToast(ctx.getString(R.string.no_server_found), act)
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return if (listModel.details!!.size < 4) listModel.details!!.size
            else 4
        }
    }

    class RecommendedAdapter(val listModel: List<HomeDataModel.ResponseData.Audio.Detail>, val ctx: Context, var binding: FragmentManageBinding, val act: Activity, var view: String?, var DB: AudioDatabase) : RecyclerView.Adapter<RecommendedAdapter.MyViewHolder>() {
        var index = -1

        inner class MyViewHolder(var binding: BigBoxLayoutBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: BigBoxLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.big_box_layout, parent, false)
            return MyViewHolder(v)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = listModel[position].name
            val measureRatio = measureRatio(ctx, 20f, 1f, 1f, 0.48f, 20f)
            holder.binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.tvAddToPlaylist.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.tvAddToPlaylist.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY

            Glide.with(ctx).load(listModel[position].imageFile).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(32))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)

            if (listModel[position].isPlay.equals("0")) {
                holder.binding.ivLock.visibility = View.VISIBLE
            } else {
                holder.binding.ivLock.visibility = View.GONE
            }

            if (index == position) {
                holder.binding.tvAddToPlaylist.visibility = View.VISIBLE
            } else holder.binding.tvAddToPlaylist.visibility = View.GONE

            holder.binding.tvAddToPlaylist.text = "Add To Playlist"

            holder.binding.llMainLayout.setOnLongClickListener {

                if (listModel[position].isPlay.equals("0")) {
                    callEnhanceActivity(ctx, act)
                } else {
                    holder.binding.tvAddToPlaylist.visibility = View.VISIBLE
                    index = position
                    notifyDataSetChanged()
                }
                true
            }

            holder.binding.tvAddToPlaylist.setOnClickListener {
                if (IsLock.equals("1")) {
                    callEnhanceActivity(ctx, act)
                } else if (IsLock.equals("0")) {
                    val listm = arrayListOf<HomeDataModel.ResponseData.Play.Detail>()
                    ManageFragment().callAddToplaylistClick("Audio View All Screen", listModel, listm, position, ctx)
                }
            }

            holder.binding.llMainLayout.setOnClickListener {
                if (listModel[position].isPlay.equals("0")) {
                    callEnhanceActivity(ctx, act)
                } else {
                    ManageFragment().callMainPlayer(position, view, listModel, ctx, act, DB)
                }
            }
        }

        override fun getItemCount(): Int {
            return if (4 > listModel.size) {
                listModel.size
            } else {
                4
            }
        }
    }

    class LibraryAdapter(private val listModel: List<HomeDataModel.ResponseData.Audio.Detail>, private val ctx: Context, var binding: FragmentManageBinding, val act: Activity, var view: String?, var DB: AudioDatabase) : RecyclerView.Adapter<LibraryAdapter.MyViewHolder>() {

        var index = -1

        inner class MyViewHolder(var binding: BigBoxLayoutBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: BigBoxLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.big_box_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = listModel[position].name
            val measureRatio = measureRatio(ctx, 20f, 1f, 1f, 0.48f, 20f)
            holder.binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.tvAddToPlaylist.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.tvAddToPlaylist.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY

            if (listModel[position].isPlay.equals("0")) {
                holder.binding.ivLock.visibility = View.VISIBLE
            } else {
                holder.binding.ivLock.visibility = View.GONE
            }

            if (index == position) {
                holder.binding.tvAddToPlaylist.visibility = View.VISIBLE
            } else holder.binding.tvAddToPlaylist.visibility = View.GONE

            holder.binding.tvAddToPlaylist.text = "Add To Playlist"

            holder.binding.llMainLayout.setOnLongClickListener {
                if (listModel[position].isPlay.equals("0")) {
                    callEnhanceActivity(ctx, act)
                } else {
                    holder.binding.tvAddToPlaylist.visibility = View.VISIBLE
                    index = position
                    notifyDataSetChanged()
                }
                true
            }

            holder.binding.tvAddToPlaylist.setOnClickListener {
                if (IsLock.equals("1")) {
                    callEnhanceActivity(ctx, act)
                } else if (IsLock.equals("0")) {
                    val listm = arrayListOf<HomeDataModel.ResponseData.Play.Detail>()
                    ManageFragment().callAddToplaylistClick("Audio View All Screen", listModel, listm, position, ctx)
                }
            }

            Glide.with(ctx).load(listModel[position].imageFile).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(32))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)

            holder.binding.llMainLayout.setOnClickListener {
                if (listModel[position].isPlay.equals("0")) {
                    callEnhanceActivity(ctx, act)
                } else {
                    ManageFragment().callMainPlayer(position, view, listModel, ctx, act, DB)
                }
            }

        }

        override fun getItemCount(): Int {
            return if (4 > listModel.size) {
                listModel.size
            } else {
                4
            }
        }
    }

    class DownloadAdapter(val listModel: List<HomeDataModel.ResponseData.Audio.Detail>, val ctx: Context, var binding: FragmentManageBinding, val act: Activity, var view: String?, var DB: AudioDatabase) : RecyclerView.Adapter<DownloadAdapter.MyViewHolder>() {

        inner class MyViewHolder(var binding: BigBoxLayoutBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: BigBoxLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.big_box_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = listModel[position].name
            val measureRatio = measureRatio(ctx, 20f, 1f, 1f, 0.48f, 20f)
            holder.binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            if (IsLock.equals("1")) {
                holder.binding.ivLock.visibility = View.VISIBLE
            } else {
                holder.binding.ivLock.visibility = View.GONE
            }
            Glide.with(ctx).load(listModel[position].imageFile).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(32))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)
            holder.binding.llMainLayout.setOnClickListener {
                if (IsLock.equals("1")) {
                    callEnhanceActivity(ctx, act)
                } else if (IsLock.equals("0")) {
                    ManageFragment().callMainPlayer(position, view, listModel, ctx, act, DB)
                }
            }
        }

        override fun getItemCount(): Int {
            return if (4 > listModel.size) {
                listModel.size
            } else {
                4
            }
        }
    }

    class RecentlyPlayedAdapter(private val listModel: List<HomeDataModel.ResponseData.Audio.Detail>, private val ctx: Context, var binding: FragmentManageBinding, val act: Activity, var view: String?, var DB: AudioDatabase) : RecyclerView.Adapter<RecentlyPlayedAdapter.MyViewHolder>() {
        var index = -1

        inner class MyViewHolder(var binding: SmallBoxLayoutBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: SmallBoxLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.small_box_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = listModel[position].name
            val measureRatio = measureRatio(ctx, 16f, 1f, 1f, 0.28f, 10f)
            holder.binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.tvAddToPlaylist.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.tvAddToPlaylist.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            Glide.with(ctx).load(listModel[position].imageFile).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(32))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)
            if (listModel[position].isPlay.equals("0")) {
                holder.binding.ivLock.visibility = View.VISIBLE
            } else {
                holder.binding.ivLock.visibility = View.GONE
            }

            if (index == position) {
                holder.binding.tvAddToPlaylist.visibility = View.VISIBLE
            } else holder.binding.tvAddToPlaylist.visibility = View.GONE

            holder.binding.tvAddToPlaylist.text = "Add To Playlist"

            holder.binding.llMainLayout.setOnLongClickListener {
                if (listModel[position].isPlay.equals("0")) {
                    callEnhanceActivity(ctx, act)
                } else {
                    holder.binding.tvAddToPlaylist.visibility = View.VISIBLE
                    index = position
                    notifyDataSetChanged()
                }
                true
            }

            holder.binding.tvAddToPlaylist.setOnClickListener {
                if (IsLock.equals("1")) {
                    callEnhanceActivity(ctx, act)
                } else if (IsLock.equals("0")) {
                    val listm = arrayListOf<HomeDataModel.ResponseData.Play.Detail>()
                    ManageFragment().callAddToplaylistClick("Audio View All Screen", listModel, listm, position, ctx)
                }
            }

            holder.binding.llMainLayout.setOnClickListener {
                if (listModel[position].isPlay.equals("0")) {
                    callEnhanceActivity(ctx, act)
                } else {
                    ManageFragment().callMainPlayer(position, view, listModel, ctx, act, DB)
                }
            }
        }

        override fun getItemCount(): Int {
            return if (4 > listModel.size) {
                listModel.size
            } else {
                4
            }
        }
    }

    class PopularPlayedAdapter(private val listModel: List<HomeDataModel.ResponseData.Audio.Detail>, private val ctx: Context, var binding: FragmentManageBinding, val act: Activity, var view: String?, var DB: AudioDatabase) : RecyclerView.Adapter<PopularPlayedAdapter.MyViewHolder>() {
        var index = -1

        inner class MyViewHolder(var binding: SmallBoxLayoutBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: SmallBoxLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.small_box_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = listModel[position].name
            val measureRatio = measureRatio(ctx, 16f, 1f, 1f, 0.28f, 10f)
            holder.binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            holder.binding.tvAddToPlaylist.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.tvAddToPlaylist.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            Glide.with(ctx).load(listModel[position].imageFile).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(32))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)

            if (listModel[position].isPlay.equals("0")) {
                holder.binding.ivLock.visibility = View.VISIBLE
            } else {
                holder.binding.ivLock.visibility = View.GONE
            }

            if (index == position) {
                holder.binding.tvAddToPlaylist.visibility = View.VISIBLE
            } else holder.binding.tvAddToPlaylist.visibility = View.GONE

            holder.binding.tvAddToPlaylist.text = "Add To Playlist"

            holder.binding.llMainLayout.setOnLongClickListener {
                if (listModel[position].isPlay.equals("0")) {
                    callEnhanceActivity(ctx, act)
                } else {
                    holder.binding.tvAddToPlaylist.visibility = View.VISIBLE
                    index = position
                    notifyDataSetChanged()
                }
                true
            }

            holder.binding.tvAddToPlaylist.setOnClickListener {
                if (IsLock.equals("1")) {
                    callEnhanceActivity(ctx, act)
                } else if (IsLock.equals("0")) {
                    val listm = arrayListOf<HomeDataModel.ResponseData.Play.Detail>()
                    ManageFragment().callAddToplaylistClick("Audio View All Screen", listModel, listm, position, ctx)
                }
            }

            holder.binding.llMainLayout.setOnClickListener {
                if (listModel[position].isPlay.equals("0")) {
                    callEnhanceActivity(ctx, act)
                } else {
                    ManageFragment().callMainPlayer(position, view, listModel, ctx, act, DB)
                }
            }
        }

        override fun getItemCount(): Int {
            return if (4 > listModel.size) {
                listModel.size
            } else {
                4
            }
        }
    }

    class TopCategoriesAdapter(private val listModel: List<HomeDataModel.ResponseData.Audio.Detail>, private val ctx: Context, var binding: FragmentManageBinding, val act: Activity, private var homeView: String, private var viewString: String?, var fragmentManager1: FragmentManager) : RecyclerView.Adapter<TopCategoriesAdapter.MyViewHolder>() {

        inner class MyViewHolder(var binding: RoundBoxLayoutBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: RoundBoxLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.round_box_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = listModel[position].categoryName
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            Glide.with(ctx).load(listModel[position].catImage).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(124))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)
            if (listModel[position].isPlay.equals("0")) {
                holder.binding.ivLock.visibility = View.VISIBLE
            } else {
                holder.binding.ivLock.visibility = View.GONE
            }

            holder.binding.llMainLayout.setOnClickListener {

                if (listModel[position].isPlay.equals("0")) {
                    callEnhanceActivity(ctx, act)
                } else {
                    if (isNetworkConnected(ctx)) {
                        val bundle = Bundle()
                        bundle.putString("ID", homeView)
                        bundle.putString("Name", viewString)
                        bundle.putString("Category", listModel[position].categoryName)
                        val viewAllAudioFragment: Fragment = ViewAllAudioFragment()
                        viewAllAudioFragment.arguments = bundle
                        fragmentManager1.beginTransaction().replace(R.id.flContainer, viewAllAudioFragment).commit()
                    } else {
                        showToast(ctx.getString(R.string.no_server_found), act)
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return if (4 > listModel.size) {
                listModel.size
            } else {
                4
            }
        }
    }
}