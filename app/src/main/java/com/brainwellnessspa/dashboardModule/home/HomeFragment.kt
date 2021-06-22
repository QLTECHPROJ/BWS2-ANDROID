package com.brainwellnessspa.dashboardModule.home

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.*
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.dashboardOldModule.activities.DashboardActivity.audioClick
import com.brainwellnessspa.dashboardOldModule.transParentPlayer.Fragments.MiniPlayerFragment.isDisclaimer
import com.brainwellnessspa.R
import com.brainwellnessspa.roomDataBase.AudioDatabase
import com.brainwellnessspa.roomDataBase.DownloadPlaylistDetails
import com.brainwellnessspa.services.GlobalInitExoPlayer.callNewPlayerRelease
import com.brainwellnessspa.services.GlobalInitExoPlayer.player
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.dashboardModule.activities.BottomNavigationActivity
import com.brainwellnessspa.dashboardModule.activities.MyPlayerActivity
import com.brainwellnessspa.dashboardModule.enhance.MyPlaylistListingActivity
import com.brainwellnessspa.dashboardModule.models.HomeScreenModel
import com.brainwellnessspa.dashboardModule.models.PlaylistDetailsModel
import com.brainwellnessspa.assessmentProgressModule.activities.AssProcessActivity
import com.brainwellnessspa.databinding.*
import com.brainwellnessspa.membershipModule.activities.SleepTimeActivity
import com.brainwellnessspa.userModule.activities.AddProfileActivity
import com.brainwellnessspa.userModule.signupLogin.WalkScreenActivity
import com.brainwellnessspa.userModule.models.AddedUserListModel
import com.brainwellnessspa.userModule.models.SegmentUserList
import com.brainwellnessspa.userModule.models.VerifyPinModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.github.mikephil.charting.utils.*
import com.google.android.flexbox.*
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.brainwellnessspa.encryptDecryptUtils.DownloadMedia.isDownloading
import com.brainwellnessspa.membershipModule.activities.RecommendedCategoryActivity
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.installations.InstallationTokenResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.segment.analytics.Properties
import com.segment.analytics.Traits
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var ctx: Context
    lateinit var act: Activity
    var adapter: UserListAdapter? = null
    var coUserId: String? = ""
    var userId: String? = ""
    var userName: String? = ""
    var userImage: String? = ""
    var scoreLevel: String? = ""
    var Download = ""
    var Liked = ""
    var MyDownloads: String? = ""
    var sleepTime: String? = ""
    var selectedCategoriesName = arrayListOf<String>()
    var downloadAudioDetailsList = arrayListOf<String>()
    lateinit var editTexts: Array<EditText>
    var tvSendOTPbool = true
    var myBackPress = false
    var gson: Gson = Gson()
    var homelistModel: HomeScreenModel = HomeScreenModel()
    private var mBottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var mBottomSheetDialog: BottomSheetDialog? = null
    lateinit var dialog: Dialog
    var score = "Increase"
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
                prepareHomeData()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        val view = binding.root
        ctx = requireActivity()
        act = requireActivity()
        val shared1 =
            ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        userName = shared1.getString(CONSTANTS.PREFE_ACCESS_NAME, "")
        userImage = shared1.getString(CONSTANTS.PREFE_ACCESS_IMAGE, "")
        scoreLevel = shared1.getString(CONSTANTS.PREFE_ACCESS_SCORELEVEL, "")

        val shared = ctx.getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
        sleepTime = shared.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")
        val json = shared.getString(CONSTANTS.selectedCategoriesName, gson.toString())
        if (!json.equals(gson.toString(), ignoreCase = true)) {
            val type1 = object : TypeToken<ArrayList<String?>?>() {}.type
            selectedCategoriesName = gson.fromJson(json, type1)
        }

        val p = Properties()
        p.putValue("coUserId", coUserId)
        addToSegment("Home Screen Viewed", p, CONSTANTS.screen)

        if (sleepTime.equals("", true)) {
            binding.llSleepTime.visibility = View.GONE
        } else {
            binding.llSleepTime.visibility = View.VISIBLE
        }

        binding.tvSleepTime.text = "Your average sleep time is \n$sleepTime"
        DB = getAudioDataBase(ctx)
        binding.tvName.text = userName
        val name: String?
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
                Glide.with(requireActivity()).load(userImage)
                    .thumbnail(0.10f).apply(RequestOptions.bitmapTransform(RoundedCorners(126)))
                    .into(binding.ivUser)
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

        val layoutManager = FlexboxLayoutManager(ctx)
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.alignItems = AlignItems.STRETCH
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        binding.rvAreaOfFocusCategory.layoutManager = layoutManager
        val adapter = AreaOfFocusAdapter(binding, ctx, selectedCategoriesName)
        binding.rvAreaOfFocusCategory.adapter = adapter

        binding.llCheckIndexscore.setOnClickListener {
            val intent = Intent(activity, AssProcessActivity::class.java)
            intent.putExtra(CONSTANTS.ASSPROCESS, "0")
            act.startActivity(intent)
            act.finish()
        }
        networkCheck()

        binding.llBottomView.setOnClickListener {
            if (isNetworkConnected(activity)) {
                val layoutBinding: UserListCustomLayoutBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(act),
                    R.layout.user_list_custom_layout,
                    null,
                    false
                )
                mBottomSheetDialog = BottomSheetDialog(ctx, R.style.BaseBottomSheetDialog)
                mBottomSheetDialog!!.setContentView(layoutBinding.root)
                mBottomSheetBehavior = BottomSheetBehavior<View>()
                mBottomSheetBehavior!!.isHideable = true
                mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                mBottomSheetDialog!!.show()
                val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(act)
                layoutBinding.rvUserList.layoutManager = mLayoutManager
                layoutBinding.rvUserList.itemAnimator = DefaultItemAnimator()
                prepareUserData(
                    layoutBinding.rvUserList,
                    layoutBinding.progressBar,
                    layoutBinding.llAddNewUser, mBottomSheetDialog!!
                )
                layoutBinding.llAddNewUser.setOnClickListener {
                    val i = Intent(act, AddProfileActivity::class.java)
                    i.putExtra("AddProfile", "Add")
                    i.putExtra("CoUserID", "")
                    i.putExtra("CoEMAIL", "")
                    i.putExtra("CoName", "")
                    i.putExtra("CoNumber", "")
                    startActivity(i)
                    mBottomSheetDialog!!.hide()
                }
            } else {
                showToast(getString(R.string.no_server_found), activity)
            }
        }

        binding.ivEditCategory.setOnClickListener {
            /* val shared1 =
                     ctx.getSharedPreferences(CONSTANTS.InAppPurchase, Context.MODE_PRIVATE)
             val purchaseToken = shared1.getString(CONSTANTS.PREF_KEY_PurchaseToken, "")
             val purchaseID = shared1.getString(CONSTANTS.PREF_KEY_PurchaseID, "")

             var refreshToken = ""
             var accessToken = ""
             var expTime: SubscriptionPurchase = SubscriptionPurchase()
             AudioDatabase.databaseWriteExecutor.execute {
                 expTime = getRefreshToken()
             }*/
            /* AudioDatabase.databaseWriteExecutor.execute {
                 accessToken = getAccessToken(refreshToken)
             }
             AudioDatabase.databaseWriteExecutor.execute {
                 expTime = getSubscriptionExpire(accessToken, refrashToken, purchaseID, purchaseToken)
                 Log.e("purchase exp time ", expTime.toString())
             }
             Log.e("purchase exp time ", expTime.toString())
             showToast(expTime.toString(), act)*/
            if (isNetworkConnected(activity)) {
                val i = Intent(act, RecommendedCategoryActivity::class.java)
                i.putExtra("BackClick", "1")
                startActivity(i)
            } else {
                showToast(getString(R.string.no_server_found), activity)
            }
        }

        binding.llClick.setOnClickListener {
            if (isNetworkConnected(activity)) {
                val i = Intent(act, NotificationListActivity::class.java)
                startActivity(i)
            } else {
                showToast(getString(R.string.no_server_found), activity)
            }
        }
        prepareHomeData()

        return view
    }

    override fun onResume() {
        networkCheck()
        super.onResume()
    }

    private fun networkCheck() {
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
            binding.barChart.visibility = View.GONE
//            showToast(getString(R.string.no_server_found), activity)
        }
        if (!isDownloading) {
            callObserve2(ctx, act)
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(listener)
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(listener1)
        super.onDestroy()
    }

    private fun prepareUserData(
        rvUserList: RecyclerView,
        progressBar: ProgressBar,
        llAddNewUser: LinearLayout, mBottomSheetDialog: BottomSheetDialog
    ) {
        if (isNetworkConnected(act)) {
            progressBar.visibility = View.VISIBLE
            progressBar.invalidate()
            val listCall = APINewClient.getClient().getUserList(userId)
            listCall.enqueue(object : Callback<AddedUserListModel> {
                override fun onResponse(
                    call: Call<AddedUserListModel>,
                    response: Response<AddedUserListModel>
                ) {
                    try {
                        progressBar.visibility = View.GONE
                        val listModel: AddedUserListModel = response.body()!!
                        adapter = UserListAdapter(listModel.responseData!!, mBottomSheetDialog)
                        rvUserList.adapter = adapter

                        if (listModel.responseData!!.userList!!.size == listModel.responseData!!.maxuseradd!!.toInt()) {
                            llAddNewUser.visibility = View.GONE
                        } else {
                            llAddNewUser.visibility = View.VISIBLE
                        }
                        val section = java.util.ArrayList<SegmentUserList>()
                        for (i in listModel.responseData!!.userList!!.indices) {
                            val e = SegmentUserList()
                            e.coUserId = listModel.responseData!!.userList!![i].coUserId
                            e.name = listModel.responseData!!.userList!![i].name
                            e.mobile = listModel.responseData!!.userList!![i].mobile
                            e.email = listModel.responseData!!.userList!![i].email
                            e.image = listModel.responseData!!.userList!![i].image
                            e.dob = listModel.responseData!!.userList!![i].dob
                            section.add(e)
                        }

                        val p = Properties()
                        val gson = Gson()
                        p.putValue("coUserId", coUserId)
                        p.putValue("userId", userId)
                        p.putValue("maxuseradd", listModel.responseData!!.maxuseradd)
                        p.putValue("coUserList", gson.toJson(section))
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

    @SuppressLint("HardwareIds")
    fun prepareHomeData() {
        val sharedPreferences2 = ctx.getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE)
        var fcm_id = sharedPreferences2.getString(CONSTANTS.Token, "")
        val DeviceId = Settings.Secure.getString(
            BWSApplication.getContext().contentResolver,
            Settings.Secure.ANDROID_ID
        )

        Log.e("newToken", fcm_id.toString())
        Log.e("deviceid", DeviceId.toString())
        Log.e("UserID", userId.toString())
        Log.e("CoUSerID", coUserId.toString())
        if (TextUtils.isEmpty(fcm_id)) {
            FirebaseInstallations.getInstance().getToken(true)
                .addOnCompleteListener(act) { task: Task<InstallationTokenResult> ->
                    val newToken = task.result.token
                    Log.e("newToken", newToken)
                    val editor =
                        ctx.getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE).edit()
                    editor.putString(CONSTANTS.Token, newToken) //Friend
                    editor.apply()
                    editor.commit()
                }
            val sharedPreferences3 = ctx.getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE)
            fcm_id = sharedPreferences3.getString(CONSTANTS.Token, "")
        }
        if (isNetworkConnected(act)) {
            showProgressBar(binding.progressBar, binding.progressBarHolder, act)
            val listCall = APINewClient.getClient().getHomeScreenData(coUserId)
            listCall.enqueue(object : Callback<HomeScreenModel?> {
                @SuppressLint("ResourceAsColor", "SetTextI18n")
                override fun onResponse(
                    call: Call<HomeScreenModel?>,
                    response: Response<HomeScreenModel?>
                ) {
                    try {
                        hideProgressBar(
                            binding.progressBar,
                            binding.progressBarHolder,
                            act
                        )
                        val listModel = response.body()!!
                        val gson = Gson()
                        homelistModel = response.body()!!
                        val shared = ctx.getSharedPreferences(
                            CONSTANTS.PREF_KEY_LOGIN,
                            AppCompatActivity.MODE_PRIVATE
                        )
                        val editor = shared.edit()
                        editor.putString(
                            CONSTANTS.PREF_KEY_IsDisclimer,
                            listModel.responseData!!.shouldPlayDisclaimer
                        )
                        editor.putString(
                            CONSTANTS.PREF_KEY_Disclimer,
                            gson.toJson(listModel.responseData!!.disclaimerAudio)
                        )
                        editor.apply()
                        when {
                            listModel.responseData!!.scoreIncDec.equals("", ignoreCase = true) -> {
                                binding.llCheckPercent.visibility = View.INVISIBLE
                            }
                            listModel.responseData!!.scoreIncDec.equals(
                                "Increase",
                                ignoreCase = true
                            ) -> {
                                binding.llCheckPercent.visibility = View.VISIBLE
                                binding.tvPercent.setTextColor(
                                    ContextCompat.getColor(
                                        act,
                                        R.color.redtheme
                                    )
                                )
                                binding.ivIndexArrow.setBackgroundResource(R.drawable.ic_down_arrow_icon)
                            }
                            listModel.responseData!!.scoreIncDec.equals(
                                "Decrease",
                                ignoreCase = true
                            ) -> {
                                binding.llCheckPercent.visibility = View.VISIBLE
                                binding.tvPercent.setTextColor(
                                    ContextCompat.getColor(act, R.color.green_dark_s)
                                )
                                binding.ivIndexArrow.setBackgroundResource(R.drawable.ic_up_arrow_icon)
                            }
                        }
                        LocalBroadcastManager.getInstance(ctx)
                            .registerReceiver(listener, IntentFilter("play_pause_Action"))
                        LocalBroadcastManager.getInstance(ctx)
                            .registerReceiver(listener1, IntentFilter("Reminder"))
                        binding.tvPercent.text =
                            listModel.responseData!!.indexScoreDiff!!.split(".")[0] + "%"
                        binding.tvSevere.text = listModel.responseData!!.indexScore.toString()
                        binding.tvSevereTxt.text = scoreLevel
                        binding.llIndicate.progress = listModel.responseData!!.indexScore!!.toInt()

                        binding.tvPlaylistName.text =
                            listModel.responseData!!.suggestedPlaylist!!.playlistName
                        binding.tvTime.text =
                            listModel.responseData!!.suggestedPlaylist!!.totalhour.toString() + ":" + listModel.responseData!!.suggestedPlaylist!!.totalminute.toString()


                        if (listModel.responseData!!.shouldCheckIndexScore.equals("0", true)) {
                            binding.llCheckIndexscore.visibility = View.GONE
                        } else if (listModel.responseData!!.shouldCheckIndexScore.equals(
                                "1",
                                ignoreCase = true
                            )
                        ) {
                            binding.llCheckIndexscore.visibility = View.VISIBLE
                        }


                        if (listModel.responseData!!.suggestedPlaylist!!.isReminder.equals(
                                "0",
                                ignoreCase = true
                            )
                            || listModel.responseData!!.suggestedPlaylist!!.isReminder.equals(
                                "",
                                ignoreCase = true
                            )
                        ) {
                            binding.tvReminder.text = "Set reminder"
                            binding.llSetReminder.setBackgroundResource(R.drawable.rounded_extra_theme_corner)
                        } else if (listModel.responseData!!.suggestedPlaylist!!.isReminder.equals(
                                "1",
                                ignoreCase = true
                            )
                        ) {
                            binding.tvReminder.text = "Update reminder"
                            binding.llSetReminder.setBackgroundResource(R.drawable.rounded_extra_dark_theme_corner)
                        } else if (listModel.responseData!!.suggestedPlaylist!!.isReminder.equals(
                                "2",
                                ignoreCase = true
                            )
                        ) {
                            binding.tvReminder.text = "Update reminder"
                            binding.llSetReminder.setBackgroundResource(R.drawable.rounded_extra_theme_corner)
                        }

                        binding.tvReminder.setOnClickListener {
                            if (listModel.responseData!!.suggestedPlaylist!!.isReminder.equals(
                                    "0",
                                    ignoreCase = true
                                )
                                || listModel.responseData!!.suggestedPlaylist!!.isReminder.equals(
                                    "",
                                    ignoreCase = true
                                )
                            ) {
                                binding.tvReminder.text = "Set reminder"
                                binding.llSetReminder.setBackgroundResource(R.drawable.rounded_extra_theme_corner)
                                getReminderDay(
                                    ctx,
                                    act,
                                    coUserId,
                                    listModel.responseData!!.suggestedPlaylist!!.playlistID,
                                    listModel.responseData!!.suggestedPlaylist!!.playlistName,
                                    activity!!,
                                    listModel.responseData!!.suggestedPlaylist!!.reminderTime,
                                    listModel.responseData!!.suggestedPlaylist!!.reminderDay
                                )
                            } else if (listModel.responseData!!.suggestedPlaylist!!.isReminder.equals(
                                    "1",
                                    ignoreCase = true
                                )
                            ) {
                                binding.tvReminder.text = "Update reminder"
                                binding.llSetReminder.setBackgroundResource(R.drawable.rounded_extra_dark_theme_corner)
                                getReminderDay(
                                    ctx,
                                    act,
                                    coUserId,
                                    listModel.responseData!!.suggestedPlaylist!!.playlistID,
                                    listModel.responseData!!.suggestedPlaylist!!.playlistName,
                                    activity!!,
                                    listModel.responseData!!.suggestedPlaylist!!.reminderTime,
                                    listModel.responseData!!.suggestedPlaylist!!.reminderDay
                                )
                            } else if (listModel.responseData!!.suggestedPlaylist!!.isReminder.equals(
                                    "2",
                                    ignoreCase = true
                                )
                            ) {
                                binding.tvReminder.text = "Update reminder"
                                binding.llSetReminder.setBackgroundResource(R.drawable.rounded_extra_theme_corner)
                                getReminderDay(
                                    ctx,
                                    act,
                                    coUserId,
                                    listModel.responseData!!.suggestedPlaylist!!.playlistID,
                                    listModel.responseData!!.suggestedPlaylist!!.playlistName,
                                    activity!!,
                                    listModel.responseData!!.suggestedPlaylist!!.reminderTime,
                                    listModel.responseData!!.suggestedPlaylist!!.reminderDay
                                )
                            }
                        }

                        getPlaylistDetail(
                            listModel.responseData!!.suggestedPlaylist!!.playlistID!!,
                            DB
                        )

                        getPastIndexScore(
                            homelistModel.responseData!!,
                            binding.barChart,
                            act
                        )

                        setPlayPauseIcon()

                        val sharedd = ctx.getSharedPreferences(
                            CONSTANTS.RecommendedCatMain,
                            Context.MODE_PRIVATE
                        )
                        sleepTime = sharedd.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")

                        if (sleepTime.equals("", true)) {
                            binding.llSleepTime.visibility = View.GONE
                        } else {
                            binding.llSleepTime.visibility = View.VISIBLE
                        }
                        binding.tvSleepTime.text = "Your average sleep time is \n$sleepTime"

                        binding.llPlayerView1.setOnClickListener {
                            callPlaylistDetails()
                        }
                        binding.llPlayerView2.setOnClickListener {
                            callPlaylistDetails()
                        }
                        binding.llPlaylistDetails.setOnClickListener {
                            callPlaylistDetails()
                        }

                        binding.llPlayPause.setOnClickListener {
                            if (isNetworkConnected(activity)) {
                                val shared1 = ctx.getSharedPreferences(
                                    CONSTANTS.PREF_KEY_PLAYER,
                                    AppCompatActivity.MODE_PRIVATE
                                )
//                            val AudioPlayerFlag =
//                                shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
//                            val MyPlaylist =
//                                shared1.getString(CONSTANTS.PREF_KEY_PayerPlaylistId, "")
//                            val PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
                                val PlayerPosition =
                                    shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                                when (isPlayPlaylist) {
                                    1 -> {
                                        player.playWhenReady = false
                                        isPlayPlaylist = 2
                                        binding.llPlay.visibility = View.VISIBLE
                                        binding.llPause.visibility = View.GONE
                                    }
                                    2 -> {
                                        if (player != null) {
                                            val lastIndexID =
                                                listModel.responseData!!.suggestedPlaylist!!.playlistSongs!![listModel.responseData!!.suggestedPlaylist!!.playlistSongs!!.size - 1].id
                                            if (PlayerAudioId.equals(
                                                    lastIndexID,
                                                    ignoreCase = true
                                                )
                                                && player.duration - player.currentPosition <= 20
                                            ) {
                                                val sharedd = ctx.getSharedPreferences(
                                                    CONSTANTS.PREF_KEY_PLAYER,
                                                    Context.MODE_PRIVATE
                                                )
                                                val editor = sharedd.edit()
                                                editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                                                editor.apply()
                                                player.seekTo(0, 0)
                                                PlayerAudioId =
                                                    listModel.responseData!!.suggestedPlaylist!!.playlistSongs!![0].id
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
                                        PlayerAudioId =
                                            listModel.responseData!!.suggestedPlaylist!!.playlistSongs!![PlayerPosition].id
                                        callMainPlayerSuggested(
                                            0,
                                            "",
                                            listModel.responseData!!.suggestedPlaylist!!.playlistSongs!!,
                                            ctx,
                                            act,
                                            listModel.responseData!!.suggestedPlaylist!!.playlistID!!,
                                            listModel.responseData!!.suggestedPlaylist!!.playlistName!!
                                        )
                                        binding.llPlay.visibility = View.GONE
                                        binding.llPause.visibility = View.VISIBLE
                                    }
                                }
                            } else {
                                showToast(getString(R.string.no_server_found), activity)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                private fun callPlaylistDetails() {
                    if (isNetworkConnected(activity)) {
                        try {
                            val i = Intent(ctx, MyPlaylistListingActivity::class.java)
                            i.putExtra("New", "0")
                            i.putExtra(
                                "PlaylistID",
                                homelistModel.responseData!!.suggestedPlaylist!!.playlistID
                            )
                            i.putExtra(
                                "PlaylistName",
                                homelistModel.responseData!!.suggestedPlaylist!!.playlistName
                            )
                            i.putExtra(
                                "PlaylistImage",
                                homelistModel.responseData!!.suggestedPlaylist!!.playlistImage
                            )
                            i.putExtra("PlaylistSource", "")
                            i.putExtra("MyDownloads", "0")
                            i.putExtra("ScreenView", "")
                            i.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                            ctx.startActivity(i)
                            act.overridePendingTransition(0, 0)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        showToast(getString(R.string.no_server_found), activity)
                    }
                }

                override fun onFailure(call: Call<HomeScreenModel?>, t: Throwable) {
                    hideProgressBar(
                        binding.progressBar,
                        binding.progressBarHolder,
                        act
                    )
                }
            })
        }
    }

    private fun setPlayPauseIcon() {

        val shared1 =
            ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, AppCompatActivity.MODE_PRIVATE)
        val AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        val MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
//        val MyPlaylistName = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "")
//        val PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
//        val PlayerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
        if (MyDownloads.equals("1", ignoreCase = true)) {
            if (AudioPlayerFlag.equals("Downloadlist", ignoreCase = true) && MyPlaylist.equals(
                    homelistModel.responseData!!.suggestedPlaylist!!.playlistID,
                    ignoreCase = true
                )
            ) {
                if (player != null) {
                    if (player.playWhenReady) {
                        isPlayPlaylist = 1
                        //                    handler3.postDelayed(UpdateSongTime3, 500);
                        binding.llPause.visibility = View.VISIBLE
                        binding.llPlay.visibility = View.GONE
                    } else {
                        isPlayPlaylist = 2
                        //                    handler3.postDelayed(UpdateSongTime3, 500);
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
            if (AudioPlayerFlag.equals("playlist", ignoreCase = true) && MyPlaylist.equals(
                    homelistModel.responseData!!.suggestedPlaylist!!.playlistID,
                    ignoreCase = true
                )
            ) {
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

    private fun callMainPlayerSuggested(
        position: Int,
        view: String?,
        listModel: List<HomeScreenModel.ResponseData.SuggestedPlaylist.PlaylistSong>,
        ctx: Context,
        act: Activity?,
        playlistID: String, playlistName: String
    ) {
        val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
        val AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        val MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
//        val MyPlaylistName = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "")
//        val PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
        val PlayerPosition: Int = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
        val shared12 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE)
        val IsPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1")
        if (MyDownloads.equals("1", true)) {
            if (isNetworkConnected(ctx)) {
                if (AudioPlayerFlag.equals("Downloadlist", ignoreCase = true) && MyPlaylist.equals(
                        playlistID,
                        ignoreCase = true
                    )
                ) {
                    if (isDisclaimer == 1) {
                        if (player != null) {
                            if (!player.playWhenReady) {
                                player.playWhenReady = true
                            }
                        } else {
                            audioClick = true
                        }
                        callMyPlayer(ctx, act!!)
                        showToast(
                            "The audio shall start playing after the disclaimer",
                            act
                        )
                    } else {
                        if (player != null) {
                            if (position != PlayerPosition) {
                                player.seekTo(position, 0)
                                player.playWhenReady = true
                                val sharedxx = ctx.getSharedPreferences(
                                    CONSTANTS.PREF_KEY_PLAYER,
                                    Context.MODE_PRIVATE
                                )
                                val editor = sharedxx.edit()
                                editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position)
                                editor.apply()
                            }
                            callMyPlayer(ctx, act!!)
                        } else {
                            callPlayerSuggested(
                                position,
                                view,
                                listModel,
                                ctx,
                                act!!,
                                playlistID, playlistName,
                                true
                            )
                        }
                    }
                } else {
                    val listModelList2 =
                        arrayListOf<HomeScreenModel.ResponseData.SuggestedPlaylist.PlaylistSong>()
                    listModelList2.addAll(listModel)
                    val DisclimerJson =
                        shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
                    val type =
                        object : TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio?>() {}.type
                    val arrayList =
                        gson.fromJson<HomeScreenModel.ResponseData.DisclaimerAudio>(
                            DisclimerJson,
                            type
                        )
                    val mainPlayModel =
                        HomeScreenModel.ResponseData.SuggestedPlaylist.PlaylistSong()
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
                            if (IsPlayDisclimer.equals("1", ignoreCase = true)) {
                                audioc = true
                                listModelList2.add(position, mainPlayModel)
                            }
                        }
                    } else {
                        isDisclaimer = 0
                        if (IsPlayDisclimer.equals("1", ignoreCase = true)) {
                            audioc = true
                            listModelList2.add(position, mainPlayModel)
                        }
                    }
                    callPlayerSuggested(
                        position,
                        view,
                        listModelList2,
                        ctx,
                        act!!,
                        playlistID, playlistName,
                        audioc
                    )
                }
            } else {
                getAllCompletedMedia(
                    AudioPlayerFlag,
                    playlistID, playlistName,
                    position,
                    listModel,
                    ctx,
                    act!!,
                    DB
                )
            }
        } else {
            if (AudioPlayerFlag.equals("playlist", ignoreCase = true) && MyPlaylist.equals(
                    playlistID,
                    ignoreCase = true
                )
            ) {
                if (isDisclaimer == 1) {
                    if (player != null) {
                        if (!player.playWhenReady) {
                            player.playWhenReady = true
                        }
                    } else {
                        audioClick = true
                    }
                    callMyPlayer(ctx, act!!)
                    showToast(
                        "The audio shall start playing after the disclaimer",
                        act
                    )
                } else {
                    if (player != null) {
                        if (position != PlayerPosition) {
                            player.seekTo(position, 0)
                            player.playWhenReady = true
                            val shared = ctx.getSharedPreferences(
                                CONSTANTS.PREF_KEY_PLAYER,
                                Context.MODE_PRIVATE
                            )
                            val editor = shared.edit()
                            editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position)
                            editor.apply()
                        }
                        callMyPlayer(ctx, act!!)
                    } else {
                        callPlayerSuggested(
                            position,
                            view,
                            listModel,
                            ctx,
                            act!!,
                            playlistID,
                            playlistName,
                            true
                        )
                    }
                }
            } else {
                val listModelList2 =
                    arrayListOf<HomeScreenModel.ResponseData.SuggestedPlaylist.PlaylistSong>()
                listModelList2.addAll(listModel)
                val DisclimerJson =
                    shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
                val type =
                    object : TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio?>() {}.type
                val arrayList =
                    gson.fromJson<HomeScreenModel.ResponseData.DisclaimerAudio>(DisclimerJson, type)
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
                        if (IsPlayDisclimer.equals("1", ignoreCase = true)) {
                            audioc = true
                            listModelList2.add(position, mainPlayModel)
                        }
                    }
                } else {
                    isDisclaimer = 0
                    if (IsPlayDisclimer.equals("1", ignoreCase = true)) {
                        audioc = true
                        listModelList2.add(position, mainPlayModel)
                    }
                }
                callPlayerSuggested(
                    position,
                    view,
                    listModelList2,
                    ctx,
                    act!!,
                    playlistID,
                    playlistName,
                    audioc
                )
            }
        }
    }

    private fun getAllCompletedMedia(
        AudioFlag: String?,
        pID: String, pName: String,
        position: Int,
        listModel: List<HomeScreenModel.ResponseData.SuggestedPlaylist.PlaylistSong>,
        ctx: Context,
        act: Activity, DB: AudioDatabase
    ) {
        AudioDatabase.databaseWriteExecutor.execute {
            downloadAudioDetailsList =
                DB.taskDao().geAllDataBYDownloaded("Complete", coUserId) as ArrayList<String>
        }
        var pos = 0
        val shared: SharedPreferences =
            ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
        var positionSaved = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
        val MyPlaylist = shared.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
        val shared12 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
        val IsPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1")
        if (AudioFlag.equals("Downloadlist", ignoreCase = true) && MyPlaylist.equals(
                pID,
                ignoreCase = true
            )
        ) {
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
                val listModelList2 =
                    arrayListOf<HomeScreenModel.ResponseData.SuggestedPlaylist.PlaylistSong>()
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
                            callPlayerSuggested(pos, "", listModelList2, ctx, act, pID, pName, true)
                        } else {
                            showToast(ctx.getString(R.string.no_server_found), act)
                        }
                    } else {
//                                pos = 0;
                        showToast(ctx.getString(R.string.no_server_found), act)
                    }
                }
//                SegmentTag()
            }
        } else {
            val listModelList2 =
                arrayListOf<HomeScreenModel.ResponseData.SuggestedPlaylist.PlaylistSong>()
            for (i in listModel.indices) {
                if (downloadAudioDetailsList.contains(listModel[i].name)) {
                    listModelList2.add(listModel[i])
                }
            }
            if (downloadAudioDetailsList.contains(listModel[position].name)) {
                pos = position
                val gson = Gson()
                val DisclimerJson =
                    shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
                val type =
                    object : TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio?>() {}.type
                val arrayList =
                    gson.fromJson<HomeScreenModel.ResponseData.DisclaimerAudio>(DisclimerJson, type)
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
                        if (IsPlayDisclimer.equals("1", ignoreCase = true)) {
                            audioc = true
                            listModelList2.add(pos, mainPlayModel)
                        }
                    }
                } else {
                    isDisclaimer = 0
                    if (IsPlayDisclimer.equals("1", ignoreCase = true)) {
                        audioc = true
                        listModelList2.add(pos, mainPlayModel)
                    }
                }
                if (listModelList2.size != 0) {
                    if (!listModelList2[pos].id.equals("0")) {
                        if (listModelList2.size != 0) {
                            callPlayerSuggested(
                                pos,
                                "",
                                listModelList2,
                                ctx,
                                act,
                                pID,
                                pName,
                                audioc
                            )
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
            }
//            SegmentTag()
        }
    }

    private fun getPlaylistDetail(PlaylistID: String, DB: AudioDatabase) {
        try {
            DB.taskDao()
                .getPlaylist1(PlaylistID, coUserId)
                .observe(this, { audioList: List<DownloadPlaylistDetails?> ->
                    MyDownloads = if (audioList.isNotEmpty()) {
                        "1"
                    } else {
                        "0"
                    }
                })
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun callPlayerSuggested(
        position: Int,
        view: String?,
        listModel: List<HomeScreenModel.ResponseData.SuggestedPlaylist.PlaylistSong>,
        ctx: Context,
        act: Activity,
        playlistID: String, playlistName: String,
        audioc: Boolean
    ) {
        if (audioc) {
            callNewPlayerRelease()
        }
        val shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
        val editor = shared.edit()
        val gson = Gson()
        val downloadAudioDetails = ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>()
        for (i in listModel.indices) {
            val mainPlayModel = PlaylistDetailsModel.ResponseData.PlaylistSong()
            mainPlayModel.id = listModel[i].id
            mainPlayModel.name = listModel[i].name
            mainPlayModel.audioFile = listModel[i].audioFile
            mainPlayModel.audioDirection = listModel[i].audioDirection
            mainPlayModel.audiomastercat = listModel[i].audiomastercat
            mainPlayModel.audioSubCategory = listModel[i].audioSubCategory
            mainPlayModel.imageFile = listModel[i].imageFile
            mainPlayModel.audioDuration = listModel[i].audioDuration
            downloadAudioDetails.add(mainPlayModel)
        }
        val json = gson.toJson(downloadAudioDetails)
        editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json)
        editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position)
        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, playlistID)
        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, playlistName)
        editor.putString(CONSTANTS.PREF_KEY_PlayFrom, view)
        if (MyDownloads.equals("1", ignoreCase = true)) {
            editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "Downloadlist")
        } else {
            editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "playlist")
        }
        editor.apply()
        audioClick = audioc
        callMyPlayer(ctx, act)
    }

    private fun callMyPlayer(ctx: Context, act: Activity) {
        val i = Intent(ctx, MyPlayerActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        ctx.startActivity(i)
        act.overridePendingTransition(0, 0)
    }

    class AreaOfFocusAdapter(
        var binding: FragmentHomeBinding,
        var ctx: Context,
        private var selectedCategoriesName: ArrayList<String>
    ) : RecyclerView.Adapter<AreaOfFocusAdapter.MyViewHolder>() {

        inner class MyViewHolder(var bindingAdapter: SelectedCategoryRawBinding) :
            RecyclerView.ViewHolder(bindingAdapter.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: SelectedCategoryRawBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.selected_category_raw,
                parent,
                false
            )
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

    inner class UserListAdapter(
        private val model: AddedUserListModel.ResponseData,
        val mBottomSheetDialog: BottomSheetDialog
    ) :
        RecyclerView.Adapter<UserListAdapter.MyViewHolder>() {
        var selectedItem = -1
        var pos = 0
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: MultipleProfileChangeLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.multiple_profile_change_layout,
                parent,
                false
            )
            return MyViewHolder(v)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val modelList = model.userList
            holder.bind.tvName.text = modelList!![position].name

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
                Glide.with(act).load(modelList[position].image)
                    .thumbnail(0.10f).apply(RequestOptions.bitmapTransform(RoundedCorners(126)))
                    .into(holder.bind.ivProfileImage)
            }

            holder.bind.ivCheck.setImageResource(R.drawable.ic_user_checked_icon)

            if (modelList[position].name.equals(userName, ignoreCase = true)) {
                holder.bind.ivCheck.visibility = View.VISIBLE
            } else {
                holder.bind.ivCheck.visibility = View.INVISIBLE
            }

            if (selectedItem == position) {
                holder.bind.ivCheck.visibility = View.VISIBLE
            } else {
                if (coUserId!! == modelList[position].coUserId && pos == 0) {
                    holder.bind.ivCheck.visibility = View.VISIBLE
                } else {
                    holder.bind.ivCheck.visibility = View.INVISIBLE
                }
            }

            holder.bind.llAddNewCard.setOnClickListener {
                if (coUserId!! == modelList[position].coUserId) {
                    mBottomSheetDialog.hide()
                } else {
                    selectedItem = position
                    pos++
                    notifyDataSetChanged()
                    val dialog = Dialog(act)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(R.layout.comfirm_pin_layout)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog.window!!.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    val btnDone = dialog.findViewById<Button>(R.id.btnDone)
                    val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
                    val txtError = dialog.findViewById<TextView>(R.id.txtError)
                    val edtOTP1 = dialog.findViewById<EditText>(R.id.edtOTP1)
                    val edtOTP2 = dialog.findViewById<EditText>(R.id.edtOTP2)
                    val edtOTP3 = dialog.findViewById<EditText>(R.id.edtOTP3)
                    val edtOTP4 = dialog.findViewById<EditText>(R.id.edtOTP4)
                    val progressBar = dialog.findViewById<ProgressBar>(R.id.progressBar)
                    tvTitle.text = "Unlock"
                    editTexts = arrayOf(edtOTP1, edtOTP2, edtOTP3, edtOTP4)
                    edtOTP1.addTextChangedListener(
                        PinTextWatcher(
                            0,
                            edtOTP1,
                            edtOTP2,
                            edtOTP3,
                            edtOTP4,
                            btnDone
                        )
                    )
                    edtOTP2.addTextChangedListener(
                        PinTextWatcher(
                            1,
                            edtOTP1,
                            edtOTP2,
                            edtOTP3,
                            edtOTP4,
                            btnDone
                        )
                    )
                    edtOTP3.addTextChangedListener(
                        PinTextWatcher(
                            2,
                            edtOTP1,
                            edtOTP2,
                            edtOTP3,
                            edtOTP4,
                            btnDone
                        )
                    )
                    edtOTP4.addTextChangedListener(
                        PinTextWatcher(
                            3,
                            edtOTP1,
                            edtOTP2,
                            edtOTP3,
                            edtOTP4,
                            btnDone
                        )
                    )
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
                        if (edtOTP1.text.toString().equals("", ignoreCase = true)
                            && edtOTP2.text.toString().equals("", ignoreCase = true)
                            && edtOTP3.text.toString().equals("", ignoreCase = true)
                            && edtOTP4.text.toString().equals("", ignoreCase = true)
                        ) {
                            txtError.visibility = View.VISIBLE
                            txtError.text = "Please enter OTP"
                        } else {
                            if (isNetworkConnected(act)) {
                                txtError.visibility = View.GONE
                                txtError.text = ""
                                progressBar.visibility = View.VISIBLE
                                progressBar.invalidate()
                                val listCall = APINewClient.getClient().getVerifyPin(
                                    modelList[position].coUserId,
                                    edtOTP1.text.toString() + "" +
                                            edtOTP2.text.toString() + "" +
                                            edtOTP3.text.toString() + "" +
                                            edtOTP4.text.toString()
                                )
                                listCall.enqueue(object : Callback<VerifyPinModel?> {
                                    @SuppressLint("HardwareIds")
                                    override fun onResponse(
                                        call: Call<VerifyPinModel?>,
                                        response: Response<VerifyPinModel?>
                                    ) {
                                        try {
                                            progressBar.visibility = View.GONE
                                            val listModel = response.body()
                                            val responseData: VerifyPinModel.ResponseData? =
                                                listModel!!.responseData
                                            when {
                                                listModel.responseCode.equals(
                                                    getString(R.string.ResponseCodesuccess),
                                                    ignoreCase = true
                                                ) -> {
                                                    dialog.dismiss()
                                                    mBottomSheetDialog.hide()
                                                    /*if (!listModel.responseData!!.userID.equals(
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
                                                    Log.e(
                                                        "New UserId MobileNo",
                                                        listModel.responseData!!.mainAccountID + "....." + listModel.responseData!!.userId
                                                    )
                                                    Log.e(
                                                        "Old UserId MobileNo",
                                                        "$userId.....$coUserId"
                                                    )
                                                    logout = false
                                                    userId = listModel.responseData!!.mainAccountID
                                                    coUserId = listModel.responseData!!.userId
                                                    if (responseData!!.isProfileCompleted.equals(
                                                            "0",
                                                            ignoreCase = true
                                                        )
                                                    ) {
                                                        val intent =
                                                            Intent(
                                                                act,
                                                                WalkScreenActivity::class.java
                                                            )
                                                        intent.putExtra(
                                                            CONSTANTS.ScreenView,
                                                            "1"
                                                        )
                                                        act.startActivity(intent)
                                                        act.finish()
                                                    } else if (responseData.isAssessmentCompleted.equals(
                                                            "0",
                                                            ignoreCase = true
                                                        )
                                                    ) {
                                                        val intent = Intent(
                                                            activity,
                                                            AssProcessActivity::class.java
                                                        )
                                                        intent.putExtra(CONSTANTS.ASSPROCESS, "0")
                                                        act.startActivity(intent)
                                                        act.finish()
                                                    } else if (responseData.avgSleepTime.equals(
                                                            "",
                                                            ignoreCase = true
                                                        )
                                                    ) {
                                                        val intent = Intent(
                                                            activity,
                                                            SleepTimeActivity::class.java
                                                        )
                                                        act.startActivity(intent)
                                                        act.finish()
                                                    } else if (responseData.isProfileCompleted.equals(
                                                            "1",
                                                            ignoreCase = true
                                                        ) &&
                                                        responseData.isAssessmentCompleted.equals(
                                                            "1",
                                                            ignoreCase = true
                                                        )
                                                    ) {
                                                        val intent = Intent(
                                                            act,
                                                            BottomNavigationActivity::class.java
                                                        )
                                                        intent.putExtra("IsFirst", "1")
                                                        act.startActivity(intent)
                                                        act.finish()
                                                    }
                                                    val shared = act.getSharedPreferences(
                                                        CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER,
                                                        AppCompatActivity.MODE_PRIVATE
                                                    )
                                                    val editor = shared.edit()
                                                    editor.putString(
                                                        CONSTANTS.PREFE_ACCESS_mainAccountID,
                                                        listModel.responseData!!.mainAccountID
                                                    )
                                                    editor.putString(
                                                        CONSTANTS.PREFE_ACCESS_UserId,
                                                        listModel.responseData!!.userId
                                                    )
                                                    editor.putString(
                                                        CONSTANTS.PREFE_ACCESS_EMAIL,
                                                        listModel.responseData!!.email
                                                    )
                                                    editor.putString(
                                                        CONSTANTS.PREFE_ACCESS_NAME,
                                                        listModel.responseData!!.name
                                                    )
                                                    editor.putString(
                                                        CONSTANTS.PREFE_ACCESS_MOBILE,
                                                        listModel.responseData!!.mobile
                                                    )
                                                    editor.putString(
                                                        CONSTANTS.PREFE_ACCESS_SLEEPTIME,
                                                        listModel.responseData!!.avgSleepTime
                                                    )
                                                    editor.putString(
                                                        CONSTANTS.PREFE_ACCESS_INDEXSCORE,
                                                        listModel.responseData!!.indexScore
                                                    )
                                                    editor.putString(
                                                        CONSTANTS.PREFE_ACCESS_ISPROFILECOMPLETED,
                                                        listModel.responseData!!.isProfileCompleted
                                                    )
                                                    editor.putString(
                                                        CONSTANTS.PREFE_ACCESS_ISAssCOMPLETED,
                                                        listModel.responseData!!.isAssessmentCompleted
                                                    )
                                                    editor.putString(
                                                        CONSTANTS.PREFE_ACCESS_IMAGE,
                                                        responseData.image
                                                    )
                                                    editor.apply()
                                                    val sharedd =
                                                        act.getSharedPreferences(
                                                            CONSTANTS.RecommendedCatMain,
                                                            Context.MODE_PRIVATE
                                                        )
                                                    val editord = sharedd.edit()
                                                    editord.putString(
                                                        CONSTANTS.PREFE_ACCESS_SLEEPTIME,
                                                        listModel.responseData!!.avgSleepTime
                                                    )
                                                    val selectedCategoriesTitle =
                                                        arrayListOf<String>()
                                                    val selectedCategoriesName =
                                                        arrayListOf<String>()
                                                    val gson = Gson()
                                                    for (i in listModel.responseData!!.areaOfFocus!!) {
                                                        selectedCategoriesTitle.add(i.mainCat!!)
                                                        selectedCategoriesName.add(i.recommendedCat!!)
                                                    }
                                                    editord.putString(
                                                        CONSTANTS.selectedCategoriesTitle,
                                                        gson.toJson(selectedCategoriesTitle)
                                                    ) //Friend
                                                    editord.putString(
                                                        CONSTANTS.selectedCategoriesName,
                                                        gson.toJson(selectedCategoriesName)
                                                    ) //Friend
                                                    editord.apply()
                                                    prepareHomeData()
//                                                    showToast(
//                                                        listModel.responseMessage,
//                                                        act
//                                                    )

                                                    analytics.identify(
                                                        Traits()
                                                            .putEmail(listModel.responseData!!.email)
                                                            .putName(listModel.responseData!!.name)
                                                            .putPhone(listModel.responseData!!.mobile)
                                                            .putValue(
                                                                "coUserId",
                                                                listModel.responseData!!.userId
                                                            )
                                                            .putValue(
                                                                "userId",
                                                                listModel.responseData!!.mainAccountID
                                                            )
                                                            .putValue(
                                                                "deviceId",
                                                                Settings.Secure.getString(
                                                                    act.contentResolver,
                                                                    Settings.Secure.ANDROID_ID
                                                                )
                                                            )
                                                            .putValue("deviceType", "Android")
                                                            .putValue(
                                                                "name",
                                                                listModel.responseData!!.name
                                                            )
                                                            .putValue("countryCode", "")
                                                            .putValue("countryName", "")
                                                            .putValue(
                                                                "phone",
                                                                listModel.responseData!!.mobile
                                                            )
                                                            .putValue(
                                                                "email",
                                                                listModel.responseData!!.email
                                                            )
                                                            .putValue(
                                                                "DOB",
                                                                listModel.responseData!!.dob
                                                            )
                                                            .putValue(
                                                                "profileImage",
                                                                listModel.responseData!!.image
                                                            )
                                                            .putValue("plan", "")
                                                            .putValue("planStatus", "")
                                                            .putValue("planStartDt", "")
                                                            .putValue("planExpiryDt", "")
                                                            .putValue("clinikoId", "")
                                                            .putValue(
                                                                "isProfileCompleted",
                                                                listModel.responseData!!.isProfileCompleted
                                                            )
                                                            .putValue(
                                                                "isAssessmentCompleted",
                                                                listModel.responseData!!.isAssessmentCompleted
                                                            )
                                                            .putValue(
                                                                "indexScore",
                                                                listModel.responseData!!.indexScore
                                                            )
                                                            .putValue("scoreLevel", "")
                                                            .putValue(
                                                                "areaOfFocus",
                                                                listModel.responseData!!.areaOfFocus
                                                            )
                                                            .putValue(
                                                                "avgSleepTime",
                                                                listModel.responseData!!.avgSleepTime
                                                            )
                                                    )
                                                }
                                                listModel.responseCode.equals(
                                                    getString(R.string.ResponseCodefail),
                                                    ignoreCase = true
                                                ) -> {
                                                    txtError.visibility = View.VISIBLE
                                                    txtError.text = listModel.responseMessage
                                                }
                                                else -> {
                                                    txtError.visibility = View.VISIBLE
                                                    txtError.text = listModel.responseMessage
                                                }
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<VerifyPinModel?>,
                                        t: Throwable
                                    ) {
                                        progressBar.visibility = View.GONE
                                    }
                                })
                            }
                        }
                    }
                    dialog.show()
                    dialog.setCanceledOnTouchOutside(true)
                    dialog.setCancelable(true)
                }
            }
        }

        override fun getItemCount(): Int {
            return model.userList!!.size
        }

        inner class MyViewHolder(var bind: MultipleProfileChangeLayoutBinding) :
            RecyclerView.ViewHolder(bind.root)
    }

    inner class PinTextWatcher internal constructor(
        private val currentIndex: Int,
        var edtOTP1: EditText,
        var edtOTP2: EditText,
        var edtOTP3: EditText,
        var edtOTP4: EditText,
        var btnDone: Button
    ) : TextWatcher {
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
                btnDone.setTextColor(ContextCompat.getColor(act, R.color.white))
                btnDone.setBackgroundResource(R.drawable.light_green_rounded_filled)
            } else {
                btnDone.isEnabled = false
                btnDone.setTextColor(ContextCompat.getColor(act, R.color.white))
                btnDone.setBackgroundResource(R.drawable.gray_round_cornor)
            }
        }

        override fun afterTextChanged(s: Editable) {
            var text = newTypedString
            Log.e("OTP VERIFICATION", "" + text)

            /* Detect paste event and set first char */if (text.length > 1) text =
                text[0].toString() // TODO: We can fill out other EditTexts
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
                for (editText in editTexts) if (editText.text.toString()
                        .trim { it <= ' ' }.isEmpty()
                ) return false
                return true
            }

        private fun hideKeyboard() {
            if (act.currentFocus != null) {
                val inputMethodManager =
                    act.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(act.currentFocus!!.windowToken, 0)
            }
        }

        init {
            if (currentIndex == 0) isFirst =
                true else if (currentIndex == editTexts.size - 1) isLast = true
        }
    }

    inner class PinOnKeyListener internal constructor(private val currentIndex: Int) :
        View.OnKeyListener {
        override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                if (editTexts[currentIndex].text.toString()
                        .isEmpty() && currentIndex != 0
                ) editTexts[currentIndex - 1].requestFocus()
            }
            return false
        }
    }
}