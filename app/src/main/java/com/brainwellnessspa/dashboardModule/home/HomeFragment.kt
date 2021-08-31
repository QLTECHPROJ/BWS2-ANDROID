package com.brainwellnessspa.dashboardModule.home

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.NotificationManager
import android.content.*
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.assessmentProgressModule.activities.AssProcessActivity
import com.brainwellnessspa.assessmentProgressModule.models.AssessmentQusModel
import com.brainwellnessspa.dashboardModule.activities.BottomNavigationActivity
import com.brainwellnessspa.dashboardModule.activities.MyPlayerActivity
import com.brainwellnessspa.dashboardModule.enhance.MyPlaylistListingActivity
import com.brainwellnessspa.dashboardModule.models.HomeScreenModel
import com.brainwellnessspa.dashboardModule.models.MainPlayModel
import com.brainwellnessspa.dashboardModule.models.PlaylistDetailsModel
import com.brainwellnessspa.dashboardModule.models.SucessModel
import com.brainwellnessspa.databinding.*
import com.brainwellnessspa.encryptDecryptUtils.DownloadMedia.isDownloading
import com.brainwellnessspa.membershipModule.activities.RecommendedCategoryActivity
import com.brainwellnessspa.membershipModule.activities.SleepTimeActivity
import com.brainwellnessspa.roomDataBase.AudioDatabase
import com.brainwellnessspa.roomDataBase.DownloadAudioDetails
import com.brainwellnessspa.roomDataBase.DownloadPlaylistDetails
import com.brainwellnessspa.services.GlobalInitExoPlayer
import com.brainwellnessspa.services.GlobalInitExoPlayer.Companion.callAllRemovePlayer
import com.brainwellnessspa.userModule.activities.ProfileProgressActivity
import com.brainwellnessspa.userModule.coUserModule.AddCouserActivity
import com.brainwellnessspa.userModule.coUserModule.CouserSetupPinActivity
import com.brainwellnessspa.userModule.models.AddedUserListModel
import com.brainwellnessspa.userModule.models.AuthOtpModel
import com.brainwellnessspa.userModule.models.SegmentUserList
import com.brainwellnessspa.userModule.signupLogin.EmailVerifyActivity
import com.brainwellnessspa.userModule.signupLogin.SignInActivity
import com.brainwellnessspa.userModule.splashscreen.SplashActivity
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.github.mikephil.charting.utils.*
import com.google.android.flexbox.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var act: Activity
    lateinit var ctx: Context
    var adapter: UserListAdapter? = null
    var userId: String? = ""
    var mainAccountId: String? = ""
    var userName: String? = ""
    private var userImage: String? = ""
    var scoreLevel: String? = ""
    var download = ""
    var isMainAccount: String? = ""
    var liked = ""
    var myDownloads: String? = ""
    var sleepTime: String? = ""
    var indexScore: String? = ""
    var selectedCategoriesName = arrayListOf<String>()
    var downloadAudioDetailsList = arrayListOf<String>()
    lateinit var editTexts: Array<EditText>
    var tvSendOTPbool = true
    var myBackPress = false
    var gson: Gson = Gson()
    var homelistModel: HomeScreenModel = HomeScreenModel()
    lateinit var mBottomSheetBehavior: BottomSheetBehavior<View>
    lateinit var mBottomSheetDialog: BottomSheetDialog
    lateinit var dialog: Dialog

    /* This listener is use for get play or pause button status when user play pause from music notifiction bar */
    private val listener: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("MyData")) {
                setPlayPauseIcon()
            }
        }
    }

    /* This listner i use for again call api when user set or update reminder*/
    private val listener1: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("MyReminder")) {
                prepareHomeDataReminder()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        val view = binding.root
        act = requireActivity()
        ctx = requireActivity()/* Get mainAccountId, and MAin Account Id from share pref*/
        val shared1 = requireActivity().getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        mainAccountId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        userName = shared1.getString(CONSTANTS.PREFE_ACCESS_NAME, "")
        userImage = shared1.getString(CONSTANTS.PREFE_ACCESS_IMAGE, "")
        scoreLevel = shared1.getString(CONSTANTS.PREFE_ACCESS_SCORELEVEL, "")
        indexScore = shared1.getString(CONSTANTS.PREFE_ACCESS_INDEXSCORE, "")
        isMainAccount = shared1.getString(CONSTANTS.PREFE_ACCESS_isMainAccount, "")
        val json5 = shared1.getString(CONSTANTS.PREFE_ACCESS_AreaOfFocus, gson.toString())
        var areaOfFocus: String? = ""

        val p = Properties()
        val gson = Gson()
        if (!json5.equals(gson.toString())) areaOfFocus = json5
        /* Get sleep time from share pref*/
        val shared = requireActivity().getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
        sleepTime = shared.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")
        val json = shared.getString(CONSTANTS.selectedCategoriesName, gson.toString())
        if (!json.equals(gson.toString())) {
            val type1 = object : TypeToken<ArrayList<String?>?>() {}.type
            selectedCategoriesName = gson.fromJson(json, type1)
        }

        val layoutBinding: UserListCustomLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(requireActivity()), R.layout.user_list_custom_layout, null, false)
        mBottomSheetDialog = BottomSheetDialog(requireActivity(), R.style.BaseBottomSheetDialog)
        mBottomSheetDialog.setContentView(layoutBinding.root)
        mBottomSheetBehavior = BottomSheetBehavior<View>()
        mBottomSheetBehavior.isHideable = true
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//        p.putValue("WellnessScore", indexScore)
//        p.putValue("areaOfFocus", gson.toJson(areaOfFocus))
        addToSegment("Home Screen Viewed", p, CONSTANTS.screen)

        if (sleepTime.equals("", true)) {
            binding.llSleepTime.visibility = View.GONE
        } else {
            binding.llSleepTime.visibility = View.VISIBLE
        }

        binding.tvSleepTime.text = "Your average sleep time is \n$sleepTime"
        DB = getAudioDataBase(requireActivity())
        binding.tvName.text = userName
        val name: String?
        binding.llBottomView.isEnabled = true
        binding.llBottomView.isClickable = true

        if (isMainAccount.equals("1")) {
        } else {
        }

        prepareHomeData()
        /* Condition for get user Image*/
        if (isNetworkConnected(activity)) {
            if (userImage.equals("", ignoreCase = true)) {
                binding.ivUser.visibility = View.GONE
                name = if (userName.equals("", ignoreCase = true)) {
                    "Guest"
                } else {
                    userName.toString()
                }
                val letter = name.substring(0, 1)
                binding.rlLetter.visibility = View.VISIBLE
                binding.tvLetter.text = letter
            } else {
                binding.ivUser.visibility = View.VISIBLE
                binding.rlLetter.visibility = View.GONE
                Glide.with(requireActivity()).load(userImage).thumbnail(0.10f).apply(RequestOptions.bitmapTransform(RoundedCorners(126))).into(binding.ivUser)
            }
        } else {
            binding.ivUser.visibility = View.GONE
            name = if (userName.equals("", ignoreCase = true)) {
                "Guest"
            } else {
                userName.toString()
            }
            val letter = name.substring(0, 1)
            binding.rlLetter.visibility = View.VISIBLE
            binding.tvLetter.text = letter
        }

        /* Set Adapter for area of focus*/
        val layoutManager = FlexboxLayoutManager(requireActivity())
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.alignItems = AlignItems.STRETCH
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        binding.rvAreaOfFocusCategory.layoutManager = layoutManager
        val adapter = AreaOfFocusAdapter(binding, requireActivity(), selectedCategoriesName)
        binding.rvAreaOfFocusCategory.adapter = adapter

        binding.llSleepTime.setOnClickListener {
            if(IsLock.equals("1")){
              callEnhanceActivity(ctx, act)
            }else if(IsLock.equals("0")) {
                if (isNetworkConnected(activity)) {
                    val dialog = Dialog(ctx)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(R.layout.cancel_membership)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireActivity(), R.color.transparent_white)))
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

        binding.tvReminder.setOnClickListener {
            if (IsLock.equals("1")) {
                callEnhanceActivity(ctx, act)
            } else if (IsLock.equals("0")) {
                if (homelistModel.responseData!!.suggestedPlaylist?.isReminder.equals("0") ||
                    homelistModel.responseData!!.suggestedPlaylist?.isReminder.equals("")) {
                    binding.tvReminder.text = getString(R.string.set_reminder)
                    binding.llSetReminder.setBackgroundResource(R.drawable.rounded_extra_theme_corner)
                } else if (homelistModel.responseData!!.suggestedPlaylist?.isReminder.equals("1")) {
                    binding.tvReminder.text = getString(R.string.update_reminder)
                    binding.llSetReminder.setBackgroundResource(R.drawable.rounded_extra_dark_theme_corner)
                } else if (homelistModel.responseData!!.suggestedPlaylist?.isReminder.equals("2")) {
                    binding.tvReminder.text = getString(R.string.update_reminder)
                    binding.llSetReminder.setBackgroundResource(R.drawable.rounded_extra_theme_corner)
                }
                getReminderDay(requireActivity(), requireActivity(), userId, homelistModel.responseData!!.suggestedPlaylist?.playlistID, homelistModel.responseData!!.suggestedPlaylist?.playlistName, requireActivity(), homelistModel.responseData!!.suggestedPlaylist?.reminderTime, homelistModel.responseData!!.suggestedPlaylist?.reminderDay, "0", homelistModel.responseData!!.suggestedPlaylist?.reminderId, homelistModel.responseData!!.suggestedPlaylist?.isReminder, "2")
            }
        }
        /* click for Go to Playlist listing detail page */
        binding.llPlayerView1.setOnClickListener {
            callPlaylistDetailsClick()
        }
        binding.llPlayerView2.setOnClickListener {
            callPlaylistDetailsClick()
        }
        binding.llPlaylistDetails.setOnClickListener {
            callPlaylistDetailsClick()
        }

        binding.llPlayPause.setOnClickListener {
            if (IsLock.equals("1")) {
                callEnhanceActivity(ctx, act)
            } else if (IsLock.equals("0")) {
                if (isNetworkConnected(activity)) {
                    val shared1 = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, AppCompatActivity.MODE_PRIVATE) //                            val AudioPlayerFlag = //                                shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0") //                            val MyPlaylist = //                                shared1.getString(CONSTANTS.PREF_KEY_PayerPlaylistId, "") //                            val PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
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
                                val lastIndexID = homelistModel.responseData!!.suggestedPlaylist?.playlistSongs!![homelistModel.responseData!!.suggestedPlaylist?.playlistSongs!!.size - 1].id
                                if (PlayerAudioId.equals(lastIndexID, ignoreCase = true) && player.duration - player.currentPosition <= 20) {
                                    val sharedd = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
                                    val editor = sharedd.edit()
                                    editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                                    editor.apply()
                                    player.seekTo(0, 0)
                                    PlayerAudioId = homelistModel.responseData!!.suggestedPlaylist?.playlistSongs!![0].id
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
                            PlayerAudioId = homelistModel.responseData!!.suggestedPlaylist?.playlistSongs!![playerPosition].id
                            callMainPlayerSuggested(0, "", homelistModel.responseData!!.suggestedPlaylist?.playlistSongs!!, requireActivity(), homelistModel.responseData!!.suggestedPlaylist?.playlistID.toString(), homelistModel.responseData!!.suggestedPlaylist?.playlistName.toString())
                            binding.llPlay.visibility = View.GONE
                            binding.llPause.visibility = View.VISIBLE
                        }
                    }
                } else {
                    showToast(getString(R.string.no_server_found), activity)
                }
            }
        }
        /* check Index score banner click*/
        binding.llCheckIndexscore.setOnClickListener {
            if(IsLock.equals("1")){
              callEnhanceActivity(ctx, act)
            }else if(IsLock.equals("0")) {
                val intent = Intent(activity, AssProcessActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                requireActivity().startActivity(intent)
                requireActivity().finish()
            }
        }/* network check function */
        networkCheck()
//        profileViewData(activity)
        /* User list layout click */
        binding.llBottomView.setOnClickListener {
               if (isNetworkConnected(requireActivity())) {
                val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireActivity())
                layoutBinding.rvUserList.layoutManager = mLayoutManager
                layoutBinding.rvUserList.itemAnimator = DefaultItemAnimator()

                /* get all user list function */
                prepareUserData(layoutBinding.rvUserList, layoutBinding.progressBar, layoutBinding.llAddNewUser, mBottomSheetDialog)

                mBottomSheetDialog.show()
            } else {
                showToast(getString(R.string.no_server_found), activity)
            }
        }

        binding.llTodayClicked.setOnClickListener {
            binding.tvToday.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black))
            binding.tvMonth.setTextColor(ContextCompat.getColor(requireActivity(), R.color.light_gray))
            binding.tvYear.setTextColor(ContextCompat.getColor(requireActivity(), R.color.light_gray))
            binding.ivToday.visibility = View.VISIBLE
            binding.ivMonth.visibility = View.INVISIBLE
            binding.ivYear.visibility = View.INVISIBLE
            binding.llToday.visibility = View.VISIBLE
            binding.llMonth.visibility = View.GONE
            binding.llYear.visibility = View.GONE
        }

        binding.llMonthClicked.setOnClickListener {
            binding.tvToday.setTextColor(ContextCompat.getColor(requireActivity(), R.color.light_gray))
            binding.tvMonth.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black))
            binding.tvYear.setTextColor(ContextCompat.getColor(requireActivity(), R.color.light_gray))
            binding.ivToday.visibility = View.INVISIBLE
            binding.ivMonth.visibility = View.VISIBLE
            binding.ivYear.visibility = View.INVISIBLE
            binding.llToday.visibility = View.GONE
            binding.llMonth.visibility = View.VISIBLE
            binding.llYear.visibility = View.GONE
        }

        binding.llYearClicked.setOnClickListener {
            binding.tvToday.setTextColor(ContextCompat.getColor(requireActivity(), R.color.light_gray))
            binding.tvMonth.setTextColor(ContextCompat.getColor(requireActivity(), R.color.light_gray))
            binding.tvYear.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black))
            binding.ivToday.visibility = View.INVISIBLE
            binding.ivMonth.visibility = View.INVISIBLE
            binding.ivYear.visibility = View.VISIBLE
            binding.llToday.visibility = View.GONE
            binding.llMonth.visibility = View.GONE
            binding.llYear.visibility = View.VISIBLE
        }

        /* Edit area of focus category icon click */
        binding.ivEditCategory.setOnClickListener {
            if(IsLock.equals("1")){
              callEnhanceActivity(ctx, act)
            }else if(IsLock.equals("0")) {
                if (isNetworkConnected(activity)) {
                    val i = Intent(requireActivity(), RecommendedCategoryActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    i.putExtra("BackClick", "1")
                    startActivity(i)
                } else {
                    showToast(getString(R.string.no_server_found), activity)
                }
            }
        }

        /* Notification ball icon click */
        binding.llNotification.setOnClickListener {
            //            if(IsLock.equals("1")){
            //              callEnhanceActivity(ctx, act)
            //            }else if(IsLock.equals("0")) {
            if (isNetworkConnected(activity)) {
                val i = Intent(requireActivity(), NotificationListActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(i)
            } else {
                showToast(getString(R.string.no_server_found), activity)
            }
            //            }
        }
        return view
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestCode == 15695) {
                val pm = ctx.getSystemService(POWER_SERVICE) as PowerManager
                var isIgnoringBatteryOptimizations = false
                isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(ctx.packageName)
                if (isIgnoringBatteryOptimizations) {
                    // Ignoring battery optimization
                } else {
                    // Not ignoring battery optimization
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        val shared = requireActivity().getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
        sleepTime = shared.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")
        val json = shared.getString(CONSTANTS.selectedCategoriesName, gson.toString())
        if (!json.equals(gson.toString(), ignoreCase = true)) {
            val type1 = object : TypeToken<ArrayList<String?>?>() {}.type
            selectedCategoriesName = gson.fromJson(json, type1)
        }
        val layoutManager = FlexboxLayoutManager(requireActivity())
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.alignItems = AlignItems.STRETCH
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        binding.rvAreaOfFocusCategory.layoutManager = layoutManager
        val adapter = AreaOfFocusAdapter(binding, requireActivity(), selectedCategoriesName)
        binding.rvAreaOfFocusCategory.adapter = adapter
        networkCheck()
        var gb = GlobalInitExoPlayer()
        gb.UpdateMiniPlayer(ctx,act)
        gb.UpdateNotificationAudioPLayer(ctx)
        setPlayPauseIcon()
//        profileViewData(activity)
        super.onResume()
    }

    private fun getReminderPopup(playlistID: String, playlistName: String, reminderTime: String, reminderDay: String, isReminder: String, reminderId: String) {
        dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.reminder_popup_layout)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireActivity(), R.color.transparent_white)))
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val btn = dialog.findViewById<Button>(R.id.Btn)
        val p = Properties()
        p.putValue("reminderId ", reminderId)
        p.putValue("playlistId ", playlistID)
        p.putValue("playlistName ", playlistName)
        p.putValue("playlistType", "Suggested")
        dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog.hide()
                // askBatteryOptimizations()
                return@setOnKeyListener true
            }
            false
        }
        btn.setOnClickListener {
//            p.putValue("isReminderSet", "Yes")
            addToSegment("Set Reminder Pop Up Clicked", p, CONSTANTS.screen)
            dialog.hide()
            getReminderDay(requireActivity(), requireActivity(), userId, playlistID, playlistName, requireActivity(), reminderTime, reminderDay, "1", reminderId, isReminder, "2")
        }
        /* tvGoBack.setOnClickListener {
             p.putValue("isReminderSet", "Later")
             addToSegment("Set Reminder Pop Up Clicked", p, CONSTANTS.screen)
             dialog.hide()
         }*/
        dialog.show()
        dialog.setCancelable(false)
    }

    /* network is available or not function for visible other layout of net is not available image display */
    private fun networkCheck() {
        val areNotificationEnabled = NotificationManagerCompat.from(requireActivity()).areNotificationsEnabled()
        Log.e("areNotificationEnabled", areNotificationEnabled.toString())
        if (isNetworkConnected(activity)) {
            binding.llSetReminder.visibility = View.VISIBLE
            binding.llIndexScore.visibility = View.VISIBLE
            binding.llSevere.visibility = View.VISIBLE
            binding.llActivities.visibility = View.VISIBLE
            binding.llUser.visibility = View.VISIBLE
            binding.ivLightBg.visibility = View.VISIBLE
            binding.llNoInternet.visibility = View.GONE
            binding.llPlayer.visibility = View.VISIBLE
            binding.llAreaOfFocus.visibility = View.VISIBLE
            binding.barChart.visibility = View.VISIBLE
        } else {
            binding.llSetReminder.visibility = View.GONE
            binding.llIndexScore.visibility = View.GONE
            binding.llUser.visibility = View.VISIBLE
            binding.llSevere.visibility = View.GONE
            binding.llActivities.visibility = View.GONE
            binding.ivLightBg.visibility = View.GONE
            binding.llNoInternet.visibility = View.VISIBLE
            binding.llPlayer.visibility = View.GONE
            binding.llAreaOfFocus.visibility = View.GONE
            binding.barChart.visibility = View.GONE //            showToast(getString(R.string.no_server_found), activity)
        }

        if (!isDownloading) {
            callObserve2(requireActivity(), requireActivity())
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(listener)
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(listener1)
        super.onDestroy()
    }

    /* Get User List api and function */
    private fun prepareUserData(rvUserList: RecyclerView, progressBar: ProgressBar, llAddNewUser: LinearLayout, mBottomSheetDialog: BottomSheetDialog?) {
        if (isNetworkConnected(requireActivity())) {
            progressBar.visibility = View.VISIBLE
            progressBar.invalidate()
            val listCall = APINewClient.client.getUserList(mainAccountId)
            listCall.enqueue(object : Callback<AddedUserListModel> {
                override fun onResponse(call: Call<AddedUserListModel>, response: Response<AddedUserListModel>) {
                    try {
                        progressBar.visibility = View.GONE
                        val listModel: AddedUserListModel = response.body()!!
                        adapter = UserListAdapter(listModel.responseData!!, mBottomSheetDialog, llAddNewUser)
                        rvUserList.adapter = adapter

                        val section = java.util.ArrayList<SegmentUserList>()
                        for (i in listModel.responseData!!.userList!!.indices) {
                            val e = SegmentUserList()
                            e.userId = listModel.responseData!!.userList!![i].userID
                            e.name = listModel.responseData!!.userList!![i].name
                            e.mobile = listModel.responseData!!.userList!![i].mobile
                            e.email = listModel.responseData!!.userList!![i].email
                            e.image = listModel.responseData!!.userList!![i].image
                            e.dob = listModel.responseData!!.userList!![i].dob
                            section.add(e)
                        }

                        val p = Properties()
                        val gson = Gson()
                        p.putValue("maxuseradd", listModel.responseData!!.maxuseradd)
                        p.putValue("UserList", gson.toJson(section))
                        addToSegment("User List Popup Viewed", p, CONSTANTS.screen)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<AddedUserListModel>, t: Throwable) {
                    progressBar.visibility = View.GONE
                }
            })
        }
    }

    /* This function is use for get main home data */
    fun prepareHomeData() {/* Get firebase token form share pref*/
        Log.e("MainAccountId", mainAccountId.toString())
        Log.e("UserId", userId.toString())

        if (isNetworkConnected(requireActivity())) {
            showProgressBar(binding.progressBar, binding.progressBarHolder, act)
            APINewClient.client.getHomeScreenData(userId).enqueue(object : Callback<HomeScreenModel?> {
                override fun onResponse(call: Call<HomeScreenModel?>, response: Response<HomeScreenModel?>) {
                    try {
                        hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                        val listModel = response.body()!!
                        val gson = Gson()
                        homelistModel = listModel
                        when {
                            listModel.responseCode.equals(getString(R.string.ResponseCodesuccess)) -> {
                                val response = listModel.responseData
                                if (response != null) {
                                    IsLock = response.IsLock
                                    val shared = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE)
                                    val editor = shared.edit()
                                    editor.putString(CONSTANTS.PREF_KEY_IsDisclimer, "0")
                                    editor.putString(CONSTANTS.PREF_KEY_Disclimer, gson.toJson(response.disclaimerAudio))
                                    editor.apply()

                                    binding.tvPercent.text = response.indexScoreDiff!!.split(".")[0] + "%"
                                    binding.tvSevere.text = response.indexScore.toString()
                                    binding.tvSevereTxt.text = scoreLevel
                                    binding.llIndicate.progress = response.indexScore!!.toInt()

                                    binding.tvPlaylistName.text = response.suggestedPlaylist?.playlistName
                                    binding.tvSleepTimeTitle.text = response.suggestedPlaylist?.playlistDirection
                                    if (response.IsLock.equals("1",ignoreCase = true)){
                                        binding.ivLock.visibility = View.VISIBLE
                                    }else {
                                        binding.ivLock.visibility = View.GONE
                                    }

                                    binding.tvTime.text = response.suggestedPlaylist?.totalhour.toString() + ":" + response.suggestedPlaylist?.totalminute.toString()

                                    if (response.shouldCheckIndexScore.equals("0", true)) {
                                        binding.llCheckIndexscore.visibility = View.GONE
                                    } else if (response.shouldCheckIndexScore.equals("1", ignoreCase = true)) {
                                        binding.llCheckIndexscore.visibility = View.VISIBLE
                                    }

                                    if (response.suggestedPlaylist?.isReminder.equals("0", ignoreCase = true) || response.suggestedPlaylist?.isReminder.equals("", ignoreCase = true)) {
                                        binding.tvReminder.text = getString(R.string.set_reminder)
                                        binding.llSetReminder.setBackgroundResource(R.drawable.rounded_extra_theme_corner)
                                    } else if (response.suggestedPlaylist?.isReminder.equals("1", ignoreCase = true)) {
                                        binding.tvReminder.text = getString(R.string.update_reminder)
                                        binding.llSetReminder.setBackgroundResource(R.drawable.rounded_extra_dark_theme_corner)
                                    } else if (response.suggestedPlaylist?.isReminder.equals("2", ignoreCase = true)) {
                                        binding.tvReminder.text = getString(R.string.update_reminder)
                                        binding.llSetReminder.setBackgroundResource(R.drawable.rounded_extra_theme_corner)
                                    }


                                    if (!response.dayFrequency.equals("", ignoreCase = true)) {
                                        binding.tvTodayFeq.text = response.dayFrequency
                                    } else {
                                        binding.tvTodayFeq.text = "0"
                                    }

                                    if (!response.dayTotalTime.equals("", ignoreCase = true)) {
                                        binding.tvTodayTotalTime.text = response.dayTotalTime
                                    } else {
                                        binding.tvTodayTotalTime.text = "00:00:00"
                                    }

                                    if (!response.dayRegularity.equals("", ignoreCase = true)) {
                                        binding.tvTodayRegularity.text = response.dayRegularity
                                    } else {
                                        binding.tvTodayRegularity.text = "0%"
                                    }


                                    if (!response.monthFrequency.equals("", ignoreCase = true)) {
                                        binding.tvMonthFeq.text = response.monthFrequency
                                    } else {
                                        binding.tvMonthFeq.text = "0"
                                    }

                                    if (!response.monthTotalTime.equals("", ignoreCase = true)) {
                                        binding.tvMonthTotalTime.text = response.monthTotalTime
                                    } else {
                                        binding.tvMonthTotalTime.text = "00:00:00"
                                    }

                                    if (!response.monthRegularity.equals("", ignoreCase = true)) {
                                        binding.tvMonthRegularity.text = response.monthRegularity
                                    } else {
                                        binding.tvMonthRegularity.text = "0%"
                                    }

                                    if (!response.yearFrequency.equals("", ignoreCase = true)) {
                                        binding.tvYearFeq.text = response.yearFrequency
                                    } else {
                                        binding.tvYearFeq.text = "0"
                                    }

                                    if (!response.yearTotalTime.equals("", ignoreCase = true)) {
                                        binding.tvYearTotalTime.text = response.yearTotalTime
                                    } else {
                                        binding.tvYearTotalTime.text = "00:00:00"
                                    }

                                    if (!response.yearRegularity.equals("", ignoreCase = true)) {
                                        binding.tvYearRegularity.text = response.yearRegularity
                                    } else {
                                        binding.tvYearRegularity.text = "0%"
                                    }
                                    /* Get downloaded Playlist detail*/
                                    getPlaylistDetail(response.suggestedPlaylist?.playlistID.toString(), DB, response.suggestedPlaylist!!.playlistSongs!!)

                                    /* Get Past Index Score graph function */
                                    getPastIndexScore(homelistModel.responseData, binding.barChart, binding.llPastIndexScore, requireActivity())

                                    getUserActivity(homelistModel.responseData, binding.barMyActivitiesChart, binding.llLegendActivity, requireActivity())

                                    try {
                                        if (response.isFirst.equals("1", ignoreCase = true)) {
                                            getReminderPopup(response.suggestedPlaylist?.playlistID.toString(), response.suggestedPlaylist?.playlistName.toString(), response.suggestedPlaylist?.reminderTime.toString(), response.suggestedPlaylist?.reminderDay.toString(), response.suggestedPlaylist?.isReminder.toString(), response.suggestedPlaylist?.reminderId.toString())
                                        } else {
                                            dialog = Dialog(requireActivity())
                                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                            dialog.setContentView(R.layout.reminder_popup_layout)
                                            dialog.window!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireActivity(), R.color.transparent_white)))
                                            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                                            dialog.dismiss()
                                            // askBatteryOptimizations()
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                    /* reminder button click */
                                    setPlayPauseIcon()

                                    val sharedd = requireActivity().getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
                                    sleepTime = sharedd.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")

                                    if (sleepTime.equals("", true)) {
                                        binding.llSleepTime.visibility = View.GONE
                                    } else {
                                        binding.llSleepTime.visibility = View.VISIBLE
                                    }
                                    binding.tvSleepTime.text = "Your average sleep time is \n$sleepTime"

                                    when {
                                        response.scoreIncDec.equals("", ignoreCase = true) -> {
                                            binding.llCheckPercent.visibility = View.INVISIBLE
                                        }
                                        response.scoreIncDec.equals("Increase", ignoreCase = true) -> {
                                            binding.llCheckPercent.visibility = View.VISIBLE
                                            binding.tvPercent.setTextColor(ContextCompat.getColor(requireActivity(), R.color.redtheme))
                                            binding.ivIndexArrow.setBackgroundResource(R.drawable.ic_down_arrow_icon)
                                        }
                                        response.scoreIncDec.equals("Decrease", ignoreCase = true) -> {
                                            binding.llCheckPercent.visibility = View.VISIBLE
                                            binding.tvPercent.setTextColor(ContextCompat.getColor(requireActivity(), R.color.green_dark_s))
                                            binding.ivIndexArrow.setBackgroundResource(R.drawable.ic_up_arrow_icon)
                                        }
                                    }


                                    /* register reciver fro get play pause action update and reminder update */
                                    LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(listener, IntentFilter("play_pause_Action"))
                                    LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(listener1, IntentFilter("Reminder"))

                                }
                            }
                            listModel.responseCode.equals(getString(R.string.ResponseCodeDeleted), ignoreCase = true) -> {
                                deleteCall(activity)
                                showToast(listModel.responseMessage, activity)
                                val i = Intent(activity, SignInActivity::class.java)
                                i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                i.putExtra("mobileNo", "")
                                i.putExtra("countryCode", "")
                                i.putExtra("name", "")
                                i.putExtra("email", "")
                                i.putExtra("countryShortName", "")
                                startActivity(i)
                                requireActivity().finish()
                            }
                            else -> {
                                showToast(listModel.responseMessage, activity)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<HomeScreenModel?>, t: Throwable) {
                    hideProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
                }
            })
        }
    }

    fun prepareHomeDataReminder() {/* Get firebase token form share pref*/
        Log.e("MainAccountId", mainAccountId.toString())
        Log.e("UserId", userId.toString())

        if (isNetworkConnected(requireActivity())) {
            showProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
            APINewClient.client.getHomeScreenData(userId).enqueue(object : Callback<HomeScreenModel?> {
                override fun onResponse(call: Call<HomeScreenModel?>, response: Response<HomeScreenModel?>) {
                    try {
                        hideProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
                        val listModel = response.body()!!
                        val gson = Gson()
                        homelistModel = listModel
                        when {
                            listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true) -> {
                                val response = listModel.responseData
                                if (response != null) {
                                    val shared = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, AppCompatActivity.MODE_PRIVATE)
                                    val editor = shared.edit()
                                    editor.putString(CONSTANTS.PREF_KEY_IsDisclimer, response.shouldPlayDisclaimer)
                                    editor.putString(CONSTANTS.PREF_KEY_Disclimer, gson.toJson(response.disclaimerAudio))
                                    editor.apply()

                                    IsLock = response.IsLock
                                    binding.tvPercent.text = response.indexScoreDiff!!.split(".")[0] + "%"
                                    binding.tvSevere.text = response.indexScore.toString()
                                    binding.tvSevereTxt.text = scoreLevel
                                    binding.llIndicate.progress = response.indexScore!!.toInt()

                                    binding.tvPlaylistName.text = response.suggestedPlaylist?.playlistName
                                    binding.tvSleepTimeTitle.text = response.suggestedPlaylist?.playlistDirection
                                    binding.tvTime.text = response.suggestedPlaylist?.totalhour.toString() + ":" + response.suggestedPlaylist?.totalminute.toString()

                                    if (!response.dayFrequency.equals("", ignoreCase = true)) {
                                        binding.tvTodayFeq.text = response.dayFrequency
                                    } else {
                                        binding.tvTodayFeq.text = "0"
                                    }

                                    if (!response.dayTotalTime.equals("", ignoreCase = true)) {
                                        binding.tvTodayTotalTime.text = response.dayTotalTime
                                    } else {
                                        binding.tvTodayTotalTime.text = "00:00:00"
                                    }

                                    if (!response.dayRegularity.equals("", ignoreCase = true)) {
                                        binding.tvTodayRegularity.text = response.dayRegularity
                                    } else {
                                        binding.tvTodayRegularity.text = "0%"
                                    }


                                    if (!response.monthFrequency.equals("", ignoreCase = true)) {
                                        binding.tvMonthFeq.text = response.monthFrequency
                                    } else {
                                        binding.tvMonthFeq.text = "0"
                                    }

                                    if (!response.monthTotalTime.equals("", ignoreCase = true)) {
                                        binding.tvMonthTotalTime.text = response.monthTotalTime
                                    } else {
                                        binding.tvMonthTotalTime.text = "00:00:00"
                                    }

                                    if (!response.monthRegularity.equals("", ignoreCase = true)) {
                                        binding.tvMonthRegularity.text = response.monthRegularity
                                    } else {
                                        binding.tvMonthRegularity.text = "0%"
                                    }

                                    if (!response.yearFrequency.equals("", ignoreCase = true)) {
                                        binding.tvYearFeq.text = response.yearFrequency
                                    } else {
                                        binding.tvYearFeq.text = "0"
                                    }

                                    if (!response.yearTotalTime.equals("", ignoreCase = true)) {
                                        binding.tvYearTotalTime.text = response.yearTotalTime
                                    } else {
                                        binding.tvYearTotalTime.text = "00:00:00"
                                    }

                                    if (!response.yearRegularity.equals("", ignoreCase = true)) {
                                        binding.tvYearRegularity.text = response.yearRegularity
                                    } else {
                                        binding.tvYearRegularity.text = "0%"
                                    }

                                    if (response.shouldCheckIndexScore.equals("0", true)) {
                                        binding.llCheckIndexscore.visibility = View.GONE
                                    } else if (response.shouldCheckIndexScore.equals("1", ignoreCase = true)) {
                                        binding.llCheckIndexscore.visibility = View.VISIBLE
                                    }

                                    if (response.suggestedPlaylist?.isReminder.equals("0", ignoreCase = true) || response.suggestedPlaylist?.isReminder.equals("", ignoreCase = true)) {
                                        binding.tvReminder.text = getString(R.string.set_reminder)
                                        binding.llSetReminder.setBackgroundResource(R.drawable.rounded_extra_theme_corner)
                                    } else if (response.suggestedPlaylist?.isReminder.equals("1", ignoreCase = true)) {
                                        binding.tvReminder.text = getString(R.string.update_reminder)
                                        binding.llSetReminder.setBackgroundResource(R.drawable.rounded_extra_dark_theme_corner)
                                    } else if (response.suggestedPlaylist?.isReminder.equals("2", ignoreCase = true)) {
                                        binding.tvReminder.text = getString(R.string.update_reminder)
                                        binding.llSetReminder.setBackgroundResource(R.drawable.rounded_extra_theme_corner)
                                    }

                                    /* Get downloaded Playlist detail*/
                                    getPlaylistDetail(response.suggestedPlaylist?.playlistID.toString(), DB, response.suggestedPlaylist!!.playlistSongs!!)
                                    // askBatteryOptimizations()
                                    try {
                                        if (response.isFirst.equals("1", ignoreCase = true)) {
                                            getReminderPopup(response.suggestedPlaylist?.playlistID.toString(), response.suggestedPlaylist?.playlistName.toString(), response.suggestedPlaylist?.reminderTime.toString(), response.suggestedPlaylist?.reminderDay.toString(), response.suggestedPlaylist?.isReminder.toString(), response.suggestedPlaylist?.reminderId.toString())
                                        } else {
                                            dialog = Dialog(requireActivity())
                                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                            dialog.setContentView(R.layout.reminder_popup_layout)
                                            dialog.window!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireActivity(), R.color.transparent_white)))
                                            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                                            dialog.dismiss()
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                    /* reminder button click */
                                    binding.tvReminder.setOnClickListener {
                                        if (IsLock.equals("1")) {
                                            callEnhanceActivity(ctx, act)
                                        } else if (IsLock.equals("0")) {
                                            if (response.suggestedPlaylist?.isReminder.equals("0", ignoreCase = true) || response.suggestedPlaylist?.isReminder.equals("", ignoreCase = true)) {
                                                binding.tvReminder.text = getString(R.string.set_reminder)
                                                binding.llSetReminder.setBackgroundResource(R.drawable.rounded_extra_theme_corner)
                                            } else if (response.suggestedPlaylist?.isReminder.equals("1", ignoreCase = true)) {
                                                binding.tvReminder.text = getString(R.string.update_reminder)
                                                binding.llSetReminder.setBackgroundResource(R.drawable.rounded_extra_dark_theme_corner)
                                            } else if (response.suggestedPlaylist?.isReminder.equals("2", ignoreCase = true)) {
                                                binding.tvReminder.text = getString(R.string.update_reminder)
                                                binding.llSetReminder.setBackgroundResource(R.drawable.rounded_extra_theme_corner)
                                            }
                                            getReminderDay(requireActivity(), requireActivity(), userId, response.suggestedPlaylist?.playlistID, response.suggestedPlaylist?.playlistName, requireActivity(), response.suggestedPlaylist?.reminderTime, response.suggestedPlaylist?.reminderDay, "0", response.suggestedPlaylist?.reminderId, response.suggestedPlaylist?.isReminder, "2")
                                        }
                                    }

                                    val sharedd = requireActivity().getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
                                    sleepTime = sharedd.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")

                                    if (sleepTime.equals("", true)) {
                                        binding.llSleepTime.visibility = View.GONE
                                    } else {
                                        binding.llSleepTime.visibility = View.VISIBLE
                                    }
                                    binding.tvSleepTime.text = "Your average sleep time is \n$sleepTime"

                                    when {
                                        response.scoreIncDec.equals("", ignoreCase = true) -> {
                                            binding.llCheckPercent.visibility = View.INVISIBLE
                                        }
                                        response.scoreIncDec.equals("Increase", ignoreCase = true) -> {
                                            binding.llCheckPercent.visibility = View.VISIBLE
                                            binding.tvPercent.setTextColor(ContextCompat.getColor(requireActivity(), R.color.redtheme))
                                            binding.ivIndexArrow.setBackgroundResource(R.drawable.ic_down_arrow_icon)
                                        }
                                        response.scoreIncDec.equals("Decrease", ignoreCase = true) -> {
                                            binding.llCheckPercent.visibility = View.VISIBLE
                                            binding.tvPercent.setTextColor(ContextCompat.getColor(requireActivity(), R.color.green_dark_s))
                                            binding.ivIndexArrow.setBackgroundResource(R.drawable.ic_up_arrow_icon)
                                        }
                                    }

                                    /* click for Go to Playlist listing detail page */
                                    binding.llPlayerView1.setOnClickListener {
                                        callPlaylistDetailsClick()
                                    }
                                    binding.llPlayerView2.setOnClickListener {
                                        callPlaylistDetailsClick()
                                    }
                                    binding.llPlaylistDetails.setOnClickListener {
                                        callPlaylistDetailsClick()
                                    }

                                    setPlayPauseIcon()
                                    binding.llPlayPause.setOnClickListener {
                                        if (IsLock.equals("1")) {
                                            callEnhanceActivity(ctx, act)
                                        } else if (IsLock.equals("0")) {
                                            if (isNetworkConnected(activity)) {
                                                val shared1 = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, AppCompatActivity.MODE_PRIVATE) //                            val AudioPlayerFlag = //                                shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0") //                            val MyPlaylist = //                                shared1.getString(CONSTANTS.PREF_KEY_PayerPlaylistId, "") //                            val PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
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
                                                            val lastIndexID = response.suggestedPlaylist?.playlistSongs!![response.suggestedPlaylist?.playlistSongs!!.size - 1].id
                                                            if (PlayerAudioId.equals(lastIndexID, ignoreCase = true) && player.duration - player.currentPosition <= 20) {
                                                                val sharedd = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
                                                                val editor = sharedd.edit()
                                                                editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                                                                editor.apply()
                                                                player.seekTo(0, 0)
                                                                PlayerAudioId = response.suggestedPlaylist?.playlistSongs!![0].id
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
                                                        PlayerAudioId = response.suggestedPlaylist?.playlistSongs!![playerPosition].id
                                                        callMainPlayerSuggested(0, "", response.suggestedPlaylist?.playlistSongs!!, requireActivity(), response.suggestedPlaylist?.playlistID.toString(), response.suggestedPlaylist?.playlistName.toString())
                                                        binding.llPlay.visibility = View.GONE
                                                        binding.llPause.visibility = View.VISIBLE
                                                    }
                                                }
                                            } else {
                                                showToast(getString(R.string.no_server_found), activity)
                                            }
                                        }
                                    }
                                    /* register reciver fro get play pause action update and reminder update */
                                    LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(listener, IntentFilter("play_pause_Action"))
                                    LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(listener1, IntentFilter("Reminder"))

                                }
                            }
                            listModel.responseCode.equals(getString(R.string.ResponseCodeDeleted), ignoreCase = true) -> {
                                deleteCall(activity)
                                showToast(listModel.responseMessage, activity)
                                val i = Intent(activity, SignInActivity::class.java)
                                i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                i.putExtra("mobileNo", "")
                                i.putExtra("countryCode", "")
                                i.putExtra("name", "")
                                i.putExtra("email", "")
                                i.putExtra("countryShortName", "")
                                startActivity(i)
                                requireActivity().finish()
                            }
                            else -> {
                                showToast(listModel.responseMessage, activity)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<HomeScreenModel?>, t: Throwable) {
                    hideProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
                }
            })
        }
    }

    /* click for Go to Playlist listing detail page */
    private fun callPlaylistDetailsClick() {
        if(IsLock.equals("1")){
            binding.ivLock.visibility = View.VISIBLE
          callEnhanceActivity(ctx,act)
        }else if(IsLock.equals("0")) {
            binding.ivLock.visibility = View.GONE
            val response = homelistModel.responseData
            if (isNetworkConnected(activity)) {
                try {
                    if (response != null) {
                        val i = Intent(requireActivity(), MyPlaylistListingActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                        i.putExtra("New", "0")
                        i.putExtra("PlaylistID", response.suggestedPlaylist?.playlistID)
                        i.putExtra("PlaylistName", response.suggestedPlaylist?.playlistName)
                        i.putExtra("PlaylistImage", response.suggestedPlaylist?.playlistImage)
                        i.putExtra("PlaylistSource", "")
                        i.putExtra("MyDownloads", "0")
                        i.putExtra("ScreenView", "")
                        i.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                        requireActivity().startActivity(i)
                        requireActivity().overridePendingTransition(0, 0)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                showToast(getString(R.string.no_server_found), activity)
            }
        }
    }

    /* set Play Pause Icon accordingly */
    private fun setPlayPauseIcon() {
        val response = homelistModel.responseData

        if (response != null) {
            /* Get String of Player play from */
            val shared1 = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, AppCompatActivity.MODE_PRIVATE)
            val audioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
            val myPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "") //        val MyPlaylistName = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "") //        val PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "") //        val PlayerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
            if (myDownloads.equals("1", ignoreCase = true)) {
                if (audioPlayerFlag.equals("Downloadlist", ignoreCase = true) && myPlaylist.equals(response.suggestedPlaylist?.playlistID, ignoreCase = true)) {
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
                if (audioPlayerFlag.equals("playlist", ignoreCase = true) && myPlaylist.equals(response.suggestedPlaylist?.playlistID, ignoreCase = true)) {
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

    }

    /* function for play suggested playlist */
    private fun callMainPlayerSuggested(position: Int, view: String?, listModel: List<HomeScreenModel.ResponseData.SuggestedPlaylist.PlaylistSong>, ctx: Context, playlistID: String, playlistName: String) {
        val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
        val audioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        val myPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "") //        val MyPlaylistName = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "") //        val PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
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
                        callMyPlayer(ctx)
                        showToast("The audio shall start playing after the disclaimer", requireActivity())
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
                            callMyPlayer(ctx)
                        } else {
                            callPlayerSuggested(position, listModel, ctx, playlistID, playlistName, true)
                        }
                    }
                } else {
                    val listModelList2 = arrayListOf<HomeScreenModel.ResponseData.SuggestedPlaylist.PlaylistSong>()
                    listModelList2.addAll(listModel)
                    val disclimerJson = shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
                    val type = object : TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio?>() {}.type
                    val arrayList = gson.fromJson<HomeScreenModel.ResponseData.DisclaimerAudio>(disclimerJson, type)
                    val mainPlayModel = HomeScreenModel.ResponseData.SuggestedPlaylist.PlaylistSong()
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
                    callPlayerSuggested(position, listModelList2, ctx, playlistID, playlistName, audioc)
                }
            } else {
                getAllCompletedMedia(audioPlayerFlag, playlistID, playlistName, position, listModel, ctx, DB)
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
                    callMyPlayer(ctx)
                    showToast("The audio shall start playing after the disclaimer", requireActivity())
                } else {
                    if (player != null) {
                        if (position != playerPosition) {
                            player.seekTo(position, 0)
                            player.playWhenReady = true
                            val shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
                            val editor = shared.edit()
                            editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position)
                            editor.apply()
                        }
                        callMyPlayer(ctx)
                    } else {
                        callPlayerSuggested(position, listModel, ctx, playlistID, playlistName, true)
                    }
                }
            } else {
                val listModelList2 = arrayListOf<HomeScreenModel.ResponseData.SuggestedPlaylist.PlaylistSong>()
                listModelList2.addAll(listModel)
                val disclimerJson = shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
                val type = object : TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio?>() {}.type
                val arrayList = gson.fromJson<HomeScreenModel.ResponseData.DisclaimerAudio>(disclimerJson, type)
                val mainPlayModel = HomeScreenModel.ResponseData.SuggestedPlaylist.PlaylistSong()
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
                callPlayerSuggested(position, listModelList2, ctx, playlistID, playlistName, audioc)
            }
        }
    }

    /* Get Downloaded Media for offline play and play that media  */
    private fun getAllCompletedMedia(audioFlag: String?, pID: String, pName: String, position: Int, listModel: List<HomeScreenModel.ResponseData.SuggestedPlaylist.PlaylistSong>, ctx: Context, DB: AudioDatabase) {
        AudioDatabase.databaseWriteExecutor.execute {
            downloadAudioDetailsList = DB.taskDao()?.geAllDataBYDownloaded("Complete", userId) as ArrayList<String>
        }
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
                callMyPlayer(ctx)
                showToast("The audio shall start playing after the disclaimer", requireActivity())
            } else {
                val listModelList2 = arrayListOf<HomeScreenModel.ResponseData.SuggestedPlaylist.PlaylistSong>()
                for (i in listModel.indices) {
                    if (downloadAudioDetailsList.contains(listModel[i].name)) {
                        listModelList2.add(listModel[i])
                    }
                }
                if (position != positionSaved) {
                    if (downloadAudioDetailsList.contains(listModel[position].name)) {
                        positionSaved = position
                        PlayerAudioId = listModel[position].id
                        if (listModelList2.size != 0) {
                            callPlayerSuggested(pos, listModelList2, ctx, pID, pName, true)
                        } else {
                            showToast(ctx.getString(R.string.no_server_found), requireActivity())
                        }
                    } else { //                                pos = 0;
                        showToast(ctx.getString(R.string.no_server_found), requireActivity())
                    }
                } //                SegmentTag()
            }
        } else {
            val listModelList2 = arrayListOf<HomeScreenModel.ResponseData.SuggestedPlaylist.PlaylistSong>()
            for (i in listModel.indices) {
                if (downloadAudioDetailsList.contains(listModel[i].name)) {
                    listModelList2.add(listModel[i])
                }
            }
            if (downloadAudioDetailsList.contains(listModel[position].name)) {
                pos = position
                val gson = Gson()
                val disclimerJson = shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
                val type = object : TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio?>() {}.type
                val arrayList = gson.fromJson<HomeScreenModel.ResponseData.DisclaimerAudio>(disclimerJson, type)
                val mainPlayModel = HomeScreenModel.ResponseData.SuggestedPlaylist.PlaylistSong()
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
                            callPlayerSuggested(pos, listModelList2, ctx, pID, pName, audioc)
                        } else {
                            showToast(ctx.getString(R.string.no_server_found), requireActivity())
                        }
                    } else if (listModelList2[pos].id.equals("0") && listModelList2.size > 1) {
                        callPlayerSuggested(pos, listModelList2, requireActivity(), pID, pName, audioc)
                    } else {
                        showToast(ctx.getString(R.string.no_server_found), requireActivity())
                    }
                } else {
                    showToast(ctx.getString(R.string.no_server_found), requireActivity())
                }
            } else {
                showToast(ctx.getString(R.string.no_server_found), requireActivity())
            } //            SegmentTag()
        }
    }

    /* Get Playlist is downloaded or not */
    private fun getPlaylistDetail(PlaylistID: String, DB: AudioDatabase, playlistSongs: List<HomeScreenModel.ResponseData.SuggestedPlaylist.PlaylistSong>) {
        try {
            DB.taskDao()?.getPlaylist1(PlaylistID, userId)?.observe(this, { audioList: List<DownloadPlaylistDetails?> ->
                if (audioList.isNotEmpty()) {
                    myDownloads = "1"
                    getPlaylistAudio(PlaylistID, userId!!, playlistSongs)
                } else {
                    myDownloads = "0"
                }
            })
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun getPlaylistAudio(PlaylistID: String, CoUserID: String, playlistSongs: List<HomeScreenModel.ResponseData.SuggestedPlaylist.PlaylistSong>) {
        val audiolistDiff = arrayListOf<DownloadAudioDetails>()
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
                if(audiolistDiff.isNotEmpty()){
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
                                        callAllRemovePlayer(ctx, activity)
                                    }
                                }
                                GetPlaylistMedia(PlaylistID, userId!!, ctx)
                            }
                        }
                    }
                }
                GetPlaylistMedia(PlaylistID, userId!!, ctx)
            }
        })
    }

    /* player is Ready for play function*/
    private fun callPlayerSuggested(position: Int, listModel: List<HomeScreenModel.ResponseData.SuggestedPlaylist.PlaylistSong>, ctx: Context, playlistID: String, playlistName: String, audioc: Boolean) {
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
        callMyPlayer(requireActivity())
    }

    /* Open Player function */
    private fun callMyPlayer(ctx: Context) {
        val i = Intent(ctx, MyPlayerActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        requireActivity().startActivity(i)
        requireActivity().overridePendingTransition(0, 0)
    }

    /* Area of focus adapter */
    class AreaOfFocusAdapter(var binding: FragmentHomeBinding, var ctx: Context, private var selectedCategoriesName: ArrayList<String>) : RecyclerView.Adapter<AreaOfFocusAdapter.MyViewHolder>() {

        inner class MyViewHolder(var bindingAdapter: SelectedCategoryRawBinding) : RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: SelectedCategoryRawBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.selected_category_raw, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindingAdapter.tvCategory.text = selectedCategoriesName[position]
            holder.bindingAdapter.tvhours.text = (position + 1).toString()

            if (selectedCategoriesName.size == 3) {
                when (position) {
                    0 -> {
                        holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg)
                        holder.bindingAdapter.llNumber.setBackgroundResource(R.drawable.circuler_chip_bg)
                    }
                    1 -> {
                        holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg_green)
                        holder.bindingAdapter.llNumber.setBackgroundResource(R.drawable.circuler_chip_bg_green)
                    }
                    2 -> {
                        holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg_blue)
                        holder.bindingAdapter.llNumber.setBackgroundResource(R.drawable.circuler_chip_bg_blue)
                    }
                }
            } else if (selectedCategoriesName.size == 2) {
                if (position == 0) {
                    holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg)
                    holder.bindingAdapter.llNumber.setBackgroundResource(R.drawable.circuler_chip_bg)
                } else if (position == 1) {
                    holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg_green)
                    holder.bindingAdapter.llNumber.setBackgroundResource(R.drawable.circuler_chip_bg_green)
                }
            } else if (selectedCategoriesName.size == 1) {
                if (position == 0) {
                    holder.bindingAdapter.llCategory.setBackgroundResource(R.drawable.round_chip_bg)
                    holder.bindingAdapter.llNumber.setBackgroundResource(R.drawable.circuler_chip_bg)
                }
            }
        }

        override fun getItemCount(): Int {
            return selectedCategoriesName.size
        }
    }

    /* Set User List Adapter*/
    inner class UserListAdapter(private val model: AddedUserListModel.ResponseData, val mBottomSheetDialog: BottomSheetDialog?, private val llAddNewUser: LinearLayout) : RecyclerView.Adapter<UserListAdapter.MyViewHolder>() {
        var selectedItem = -1
        var pos = 0
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: MultipleProfileChangeLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.multiple_profile_change_layout, parent, false)
            return MyViewHolder(v)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val modelList = model.userList
            holder.bind.tvName.text = modelList!![position].name

            if (isMainAccount.equals("1", ignoreCase = true)) {
                llAddNewUser.visibility = View.VISIBLE
            } else {
                llAddNewUser.visibility = View.GONE
            }

            llAddNewUser.setOnClickListener {
                if(IsLock.equals("1")){
                  callEnhanceActivity(ctx, act)
                }else if(IsLock.equals("0")) {
                    if (isMainAccount.equals("1", ignoreCase = true)) {
                        llAddNewUser.visibility = View.VISIBLE
                        if (!model.maxuseradd.equals("", ignoreCase = true)) {
                            if (model.totalUserCount?.toInt() == model.maxuseradd?.toInt()) {
                                showToast("Please update your plan", activity)
                            } else {
                                /* Add new user button click */
                                IsFirstClick = "0"
                                addCouserBackStatus = 1
                                val i = Intent(requireActivity(), AddCouserActivity::class.java)
                                i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                startActivity(i)
                                mBottomSheetDialog?.hide()
                            }
                        } else {
                            showToast("Please purchase your plan", activity)
                        }
                    } else {
                        llAddNewUser.visibility = View.GONE
                    }
                }
            }

            val name: String?
            if (modelList[position].image.equals("", ignoreCase = true)) {
                holder.bind.ivProfileImage.visibility = View.GONE
                name = if (modelList[position].name.equals("", ignoreCase = true)) {
                    "Guest"
                } else {
                    modelList[position].name.toString()
                }
                val letter = name.substring(0, 1)
                holder.bind.rlLetter.visibility = View.VISIBLE
                holder.bind.tvLetter.text = letter
            } else {
                holder.bind.ivProfileImage.visibility = View.VISIBLE
                holder.bind.rlLetter.visibility = View.GONE
                Glide.with(requireActivity()).load(modelList[position].image).thumbnail(0.10f).apply(RequestOptions.bitmapTransform(RoundedCorners(126))).into(holder.bind.ivProfileImage)
            }

            holder.bind.ivCheck.setImageResource(R.drawable.ic_user_checked_icon)

            if (modelList[position].name.equals(userName)) {
                holder.bind.ivCheck.visibility = View.VISIBLE
            } else {
                holder.bind.ivCheck.visibility = View.INVISIBLE
            }

            if (selectedItem == position) {
                holder.bind.ivCheck.visibility = View.VISIBLE
            } else {
                if (userId!! == modelList[position].userID && pos == 0) {
                    holder.bind.ivCheck.visibility = View.VISIBLE
                } else {
                    holder.bind.ivCheck.visibility = View.INVISIBLE
                }
            }

            holder.bind.llAddNewCard.setOnClickListener {
                if (IsLock.equals("1")) {
                  callEnhanceActivity(ctx, act)
                } else if (IsLock.equals("0")) {
                    if (modelList[position].isPinSet.equals("1")) {
                        if (userId!! == modelList[position].userID) {
                            mBottomSheetDialog?.hide()
                        } else {
                            selectedItem = position
                            pos++
                            notifyDataSetChanged()
                            val dialog = Dialog(requireActivity())
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                            dialog.setContentView(R.layout.comfirm_pin_layout)
                            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            val btnDone = dialog.findViewById<Button>(R.id.btnDone)
                            val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
                            val txtError = dialog.findViewById<TextView>(R.id.txtError)
                            val tvForgotPin: TextView = dialog.findViewById(R.id.tvForgotPin)
                            val edtOTP1 = dialog.findViewById<EditText>(R.id.edtOTP1)
                            val edtOTP2 = dialog.findViewById<EditText>(R.id.edtOTP2)
                            val edtOTP3 = dialog.findViewById<EditText>(R.id.edtOTP3)
                            val edtOTP4 = dialog.findViewById<EditText>(R.id.edtOTP4)
                            val progressBar = dialog.findViewById<ProgressBar>(R.id.progressBar)
                            tvTitle.text = "Unlock the app"
                            editTexts = arrayOf(edtOTP1, edtOTP2, edtOTP3, edtOTP4)
                            edtOTP1.addTextChangedListener(PinTextWatcher(0, edtOTP1, edtOTP2, edtOTP3, edtOTP4, btnDone))
                            edtOTP2.addTextChangedListener(PinTextWatcher(1, edtOTP1, edtOTP2, edtOTP3, edtOTP4, btnDone))
                            edtOTP3.addTextChangedListener(PinTextWatcher(2, edtOTP1, edtOTP2, edtOTP3, edtOTP4, btnDone))
                            edtOTP4.addTextChangedListener(PinTextWatcher(3, edtOTP1, edtOTP2, edtOTP3, edtOTP4, btnDone))
                            edtOTP1.setOnKeyListener(PinOnKeyListener(0))
                            edtOTP2.setOnKeyListener(PinOnKeyListener(1))
                            edtOTP3.setOnKeyListener(PinOnKeyListener(2))
                            edtOTP4.setOnKeyListener(PinOnKeyListener(3))
                            dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    dialog.dismiss()
                                    return@setOnKeyListener true

                                }
                                false
                            }

                            btnDone.setOnClickListener {
                                if (edtOTP1.text.toString().equals("") && edtOTP2.text.toString().equals("") && edtOTP3.text.toString().equals("") && edtOTP4.text.toString().equals("")) {
                                    txtError.visibility = View.VISIBLE
                                    txtError.text = "Please enter OTP"
                                } else {
                                    if (isNetworkConnected(requireActivity())) {
                                        txtError.visibility = View.GONE
                                        txtError.text = ""
                                        progressBar.visibility = View.VISIBLE
                                        progressBar.invalidate()
                                        val listCall = APINewClient.client.getVerifyPin(modelList[position].userID, edtOTP1.text.toString() + "" + edtOTP2.text.toString() + "" + edtOTP3.text.toString() + "" + edtOTP4.text.toString())
                                        listCall.enqueue(object : Callback<AuthOtpModel> {
                                            @SuppressLint("HardwareIds")
                                            override fun onResponse(call: Call<AuthOtpModel>, response: Response<AuthOtpModel>) {
                                                try {
                                                    progressBar.visibility = View.GONE
                                                    val listModel: AuthOtpModel = response.body()!!
                                                    when {
                                                        listModel.ResponseCode.equals(getString(R.string.ResponseCodesuccess)) -> {
                                                            dialog.dismiss()
                                                            mBottomSheetDialog?.hide()/*if (!listModel.responseData!!.userID.equals(
                                                            userId,
                                                            ignoreCase = true
                                                        )
                                                        && !listModel.responseData!!.coUserId.equals(
                                                            coUserId,
                                                            ignoreCase = true
                                                        )
                                                    ) {
                                                        callObserve1(ctx)
                                                    } else {
                                                        callObserve2(ctx)
                                                    }*/
                                                        Log.e("New UserId MobileNo", listModel.ResponseData.MainAccountID + "....." + listModel.ResponseData.UserId)
                                                        Log.e("Old UserId MobileNo", "$mainAccountId.....$userId")
                                                        logout = false
                                                        mainAccountId = listModel.ResponseData.MainAccountID
                                                        userId = listModel.ResponseData.UserId
                                                        IsLock = listModel.ResponseData.Islock
                                                        if (listModel.ResponseData.isPinSet == "1") {
                                                            if (listModel.ResponseData.IsFirst == "1") {
                                                                val intent = Intent(activity, EmailVerifyActivity::class.java)
                                                                requireActivity().startActivity(intent)
                                                                requireActivity().finish()
                                                            } else if (listModel.ResponseData.isAssessmentCompleted == "0") {
                                                                val intent = Intent(activity, AssProcessActivity::class.java)
                                                                intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                                                                requireActivity().startActivity(intent)
                                                                requireActivity().finish()
                                                            } else if (listModel.ResponseData.isProfileCompleted == "0") {
                                                                val intent = Intent(activity, ProfileProgressActivity::class.java)
                                                                requireActivity().startActivity(intent)
                                                                requireActivity().finish()
                                                            } else if (listModel.ResponseData.AvgSleepTime == "") {
                                                                val intent = Intent(activity, SleepTimeActivity::class.java)
                                                                requireActivity().startActivity(intent)
                                                                requireActivity().finish()
                                                            } else if (listModel.ResponseData.isProfileCompleted == "1" && listModel.ResponseData.isAssessmentCompleted == "1") {
                                                                val intent = Intent(activity, BottomNavigationActivity::class.java)
                                                                intent.putExtra("IsFirst", "0")
                                                                requireActivity().startActivity(intent)
                                                                requireActivity().finish()
                                                            }
                                                        } else if (listModel.ResponseData.isPinSet == "0" || listModel.ResponseData.isPinSet == "") {
                                                            IsFirstClick = "0"
                                                            val intent = Intent(activity, AddCouserActivity::class.java)
                                                            requireActivity().startActivity(intent)
                                                            requireActivity().finish()
                                                        }
                                                        val shared = requireActivity().getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
                                                        val editor = shared.edit()
                                                        editor.putString(CONSTANTS.PREFE_ACCESS_mainAccountID, listModel.ResponseData.MainAccountID)
                                                        editor.putString(CONSTANTS.PREFE_ACCESS_UserId, listModel.ResponseData.UserId)
                                                        editor.putString(CONSTANTS.PREFE_ACCESS_EMAIL, listModel.ResponseData.Email)
                                                        editor.putString(CONSTANTS.PREFE_ACCESS_NAME, listModel.ResponseData.Name)
                                                        editor.putString(CONSTANTS.PREFE_ACCESS_MOBILE, listModel.ResponseData.Mobile)
                                                        editor.putString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, listModel.ResponseData.AvgSleepTime)
                                                        editor.putString(CONSTANTS.PREFE_ACCESS_INDEXSCORE, listModel.ResponseData.indexScore)
                                                        editor.putString(CONSTANTS.PREFE_ACCESS_SCORELEVEL, listModel.ResponseData.ScoreLevel)
                                                        editor.putString(CONSTANTS.PREFE_ACCESS_IMAGE, listModel.ResponseData.Image)
                                                        editor.putString(CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED, listModel.ResponseData.isProfileCompleted)
                                                        editor.putString(CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED, listModel.ResponseData.isAssessmentCompleted)
                                                        editor.putString(CONSTANTS.PREFE_ACCESS_isMainAccount, listModel.ResponseData.isMainAccount)
                                                        editor.putString(CONSTANTS.PREFE_ACCESS_coUserCount, listModel.ResponseData.CoUserCount)
                                                        editor.putString(CONSTANTS.PREFE_ACCESS_directLogin, listModel.ResponseData.directLogin)
                                                        editor.putString(CONSTANTS.PREFE_ACCESS_isEmailVerified, listModel.ResponseData.isEmailVerified)
                                                        editor.putString(CONSTANTS.PREFE_ACCESS_isSetLoginPin, "1")
                                                        editor.putString(CONSTANTS.PREFE_ACCESS_isPinSet, listModel.ResponseData.isPinSet)
                                                        try {
                                                            if (listModel.ResponseData.planDetails.isNotEmpty()) {
                                                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanId, listModel.ResponseData.planDetails[0].PlanId)
                                                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanPurchaseDate, listModel.ResponseData.planDetails[0].PlanPurchaseDate)
                                                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanExpireDate, listModel.ResponseData.planDetails[0].PlanExpireDate)
                                                                editor.putString(CONSTANTS.PREFE_ACCESS_TransactionId, listModel.ResponseData.planDetails[0].TransactionId)
                                                                editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodStart, listModel.ResponseData.planDetails[0].TrialPeriodStart)
                                                                editor.putString(CONSTANTS.PREFE_ACCESS_TrialPeriodEnd, listModel.ResponseData.planDetails[0].TrialPeriodEnd)
                                                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanStatus, listModel.ResponseData.planDetails[0].PlanStatus)
                                                                editor.putString(CONSTANTS.PREFE_ACCESS_PlanContent, listModel.ResponseData.planDetails[0].PlanContent)
                                                            }
                                                        }catch (e:Exception){
                                                            e.printStackTrace()
                                                        }
                                                        editor.apply()
                                                        val sharded = requireActivity().getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
                                                        val edited = sharded.edit()
                                                        edited.putString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, listModel.ResponseData.AvgSleepTime)
                                                        val selectedCategoriesTitle = arrayListOf<String>()
                                                        val selectedCategoriesName = arrayListOf<String>()
                                                        val gson = Gson()
                                                        for (i in listModel.ResponseData.AreaOfFocus) {
                                                            selectedCategoriesTitle.add(i.MainCat)
                                                            selectedCategoriesName.add(i.RecommendedCat)
                                                        }
                                                        edited.putString(CONSTANTS.selectedCategoriesTitle, gson.toJson(selectedCategoriesTitle)) //Friend
                                                        edited.putString(CONSTANTS.selectedCategoriesName, gson.toJson(selectedCategoriesName)) //Friend
                                                        edited.apply()

                                                            prepareHomeData()

                                                            val activity = SplashActivity()
                                                            activity.setAnalytics(getString(R.string.segment_key_real_2_staging), requireActivity())

                                                            //    showToast(listModel.responseMessage,requireActivity())
                                                            callIdentify(requireActivity())
                                                            val p1 = Properties()
                                                            p1.putValue("deviceId", Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID))
                                                            p1.putValue("deviceType", "Android")
                                                            p1.putValue("name", listModel.ResponseData.Name)
                                                            p1.putValue("countryCode", "")
                                                            p1.putValue("countryName", "")
                                                            p1.putValue("phone", listModel.ResponseData.Mobile)
                                                            p1.putValue("email", listModel.ResponseData.Email)
                                                            p1.putValue("plan", "")
                                                            p1.putValue("planStatus", "")
                                                            p1.putValue("planStartDt", "")
                                                            p1.putValue("planExpiryDt", "")
                                                            p1.putValue("clinikoId", "")
                                                            var isProf = false
                                                            var isAss = false
                                                            isProf = if (listModel.ResponseData.isProfileCompleted.equals("1")) true else false
                                                            isAss = if (listModel.ResponseData.isAssessmentCompleted.equals("1")) true else false
                                                            p1.putValue("isProfileCompleted", isProf)
                                                            p1.putValue("isAssessmentCompleted", isAss)
                                                            p1.putValue("WellnessScore", listModel.ResponseData.indexScore)
                                                            p1.putValue("scoreLevel", listModel.ResponseData.ScoreLevel)
                                                            p1.putValue("areaOfFocus", listModel.ResponseData.AreaOfFocus)
                                                            p1.putValue("avgSleepTime", listModel.ResponseData.AvgSleepTime)
                                                            addToSegment("CoUser Login", p1, CONSTANTS.track)
                                                        }
                                                        listModel.ResponseCode.equals(getString(R.string.ResponseCodeDeleted)) -> {
                                                            txtError.visibility = View.GONE
                                                            txtError.text = ""
                                                            deleteCall(activity)
                                                            val i = Intent(activity, SignInActivity::class.java)
                                                            i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                                            i.putExtra("mobileNo", "")
                                                            i.putExtra("countryCode", "")
                                                            i.putExtra("name", "")
                                                            i.putExtra("email", "")
                                                            i.putExtra("countryShortName", "")
                                                            startActivity(i)
                                                            requireActivity().finish()
                                                        }
                                                        listModel.ResponseCode.equals(getString(R.string.ResponseCodefail)) -> {
                                                            txtError.visibility = View.VISIBLE
                                                            txtError.text = listModel.ResponseMessage
                                                        }
                                                        else -> {
                                                            txtError.visibility = View.VISIBLE
                                                            txtError.text = listModel.ResponseMessage
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }

                                            override fun onFailure(call: Call<AuthOtpModel>, t: Throwable) {
                                                progressBar.visibility = View.GONE
                                            }
                                        })
                                    }
                                }
                            }

                            tvForgotPin.setOnClickListener {
                                dialog.dismiss()
                                val dialog = Dialog(requireActivity())
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                dialog.setContentView(R.layout.add_couser_continue_layout)
                                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                                val mainLayout = dialog.findViewById<ConstraintLayout>(R.id.mainLayout)
                                val ivIcon = dialog.findViewById<ImageView>(R.id.ivIcon)
                                val tvText = dialog.findViewById<TextView>(R.id.tvText)
                                ivIcon.setImageResource(R.drawable.ic_email_success_icon)
                                val email: String = modelList[position].email.toString()
                                tvText.text = "A new pin has been sent to \nyour mail id \n$email."

                                dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                                        dialog.dismiss()
                                        return@setOnKeyListener true
                                    }
                                    false
                                }
                                mainLayout.setOnClickListener {
                                    if (isNetworkConnected(requireActivity())) {
                                        showProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
                                        val listCall: Call<SucessModel> = APINewClient.client.getForgotPin(modelList[position].userID, modelList[position].email)
                                        listCall.enqueue(object : Callback<SucessModel> {
                                            override fun onResponse(call: Call<SucessModel>, response: Response<SucessModel>) {
                                                hideProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
                                                val listModel: SucessModel = response.body()!!
                                                when {
                                                    listModel.responseCode.equals(requireActivity().getString(R.string.ResponseCodesuccess), ignoreCase = true) -> {
                                                        dialog.dismiss()
                                                    }
                                                    listModel.responseCode.equals(requireActivity().getString(R.string.ResponseCodefail), ignoreCase = true) -> {
                                                        showToast(listModel.responseMessage, requireActivity())
                                                    }
                                                    else -> {
                                                        showToast(listModel.responseMessage, requireActivity())
                                                    }
                                                }
                                            }

                                            override fun onFailure(call: Call<SucessModel>, t: Throwable) {
                                                hideProgressBar(binding.progressBar, binding.progressBarHolder, requireActivity())
                                            }
                                        })
                                    } else {
                                        showToast(requireActivity().getString(R.string.no_server_found), requireActivity())
                                    }
                                }

                                dialog.show()
                                dialog.setCancelable(true)
                            }

                        dialog.show()
                        dialog.setCanceledOnTouchOutside(true)
                        dialog.setCancelable(true)
                    }
                } else if (modelList[position].isPinSet.equals("0") || modelList[position].isPinSet.equals("")) {
                    comeHomeScreen = "1"
                    val i = Intent(requireActivity(), CouserSetupPinActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    i.putExtra("subUserId", modelList[position].userID)
                    requireActivity().startActivity(i)
                    mBottomSheetDialog?.hide()
                }
            }
            }
        }

        override fun getItemCount(): Int {
            return model.userList!!.size
        }

        inner class MyViewHolder(var bind: MultipleProfileChangeLayoutBinding) : RecyclerView.ViewHolder(bind.root)
    }

    /* Pin Watcher class for set pin in new user*/
    inner class PinTextWatcher internal constructor(private val currentIndex: Int, private var edtOTP1: EditText, private var edtOTP2: EditText, private var edtOTP3: EditText, private var edtOTP4: EditText, var btnDone: Button) : TextWatcher {
        private var isFirst = false
        private var isLast = false
        private var newTypedString = ""
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            newTypedString = s.subSequence(start, start + count).toString().trim { it <= ' ' }
            val otp1 = edtOTP1.text.toString().trim { it <= ' ' }
            val otp2 = edtOTP2.text.toString().trim { it <= ' ' }
            val otp3 = edtOTP3.text.toString().trim { it <= ' ' }
            val otp4 = edtOTP4.text.toString().trim { it <= ' ' }
            if (otp1.isNotEmpty() && otp2.isNotEmpty() && otp3.isNotEmpty() && otp4.isNotEmpty()) {
                btnDone.isEnabled = true
                btnDone.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
                btnDone.setBackgroundResource(R.drawable.light_green_rounded_filled)
            } else {
                btnDone.isEnabled = false
                btnDone.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
                btnDone.setBackgroundResource(R.drawable.gray_round_cornor)
            }
        }

        override fun afterTextChanged(s: Editable) {
            var text = newTypedString
            Log.e("OTP VERIFICATION", "" + text)

            /* Detect paste event and set first char */if (text.length > 1) text = text[0].toString() // TODO: We can fill out other EditTexts
            editTexts[currentIndex].removeTextChangedListener(this)
            editTexts[currentIndex].setText(text)
            editTexts[currentIndex].setSelection(text.length)
            editTexts[currentIndex].addTextChangedListener(this)
            if (text.length == 1) {
                moveToNext()
            } else if (text.isEmpty()) {
                if (!tvSendOTPbool) {
                    editTexts[0].requestFocus()
                } else {
                    moveToPrevious()
                }
            }
        }

        private fun moveToNext() {
            if (!isLast) editTexts[currentIndex + 1].requestFocus()
            if (isAllEditTextsFilled && isLast) { // isLast is optional
                editTexts[currentIndex].clearFocus()
                hideKeyboard()
            }
        }

        private fun moveToPrevious() {
            if (!isFirst) editTexts[currentIndex - 1].requestFocus()
        }

        private val isAllEditTextsFilled: Boolean
            get() {
                for (editText in editTexts) if (editText.text.toString().trim { it <= ' ' }.isEmpty()) return false
                return true
            }

        private fun hideKeyboard() {
            if (requireActivity().currentFocus != null) {
                val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus!!.windowToken, 0)
            }
        }

        init {
            if (currentIndex == 0) isFirst = true else if (currentIndex == editTexts.size - 1) isLast = true
        }
    }

    inner class PinOnKeyListener internal constructor(private val currentIndex: Int) : View.OnKeyListener {
        override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                if (editTexts[currentIndex].text.toString().isEmpty() && currentIndex != 0) editTexts[currentIndex - 1].requestFocus()
            }
            return false
        }
    }
}