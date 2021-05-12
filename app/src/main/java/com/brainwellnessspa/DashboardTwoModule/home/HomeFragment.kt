package com.brainwellnessspa.DashboardTwoModule.home

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
import androidx.fragment.app.FragmentActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.DashboardModule.Activities.DashboardActivity
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment
import com.brainwellnessspa.DashboardTwoModule.BottomNavigationActivity
import com.brainwellnessspa.DashboardTwoModule.Model.HomeScreenModel
import com.brainwellnessspa.DashboardTwoModule.Model.PlaylistDetailsModel
import com.brainwellnessspa.DashboardTwoModule.MyPlayerActivity
import com.brainwellnessspa.DashboardTwoModule.fragmentPlaylist.MyPlaylistListingActivity
import com.brainwellnessspa.DassAssSliderTwo.Activity.AssProcessActivity
import com.brainwellnessspa.ManageModule.RecommendedCategoryActivity
import com.brainwellnessspa.NotificationTwoModule.NotificationListActivity
import com.brainwellnessspa.R
import com.brainwellnessspa.ReminderModule.Models.DeleteRemiderModel
import com.brainwellnessspa.RoomDataBase.AudioDatabase
import com.brainwellnessspa.Services.GlobalInitExoPlayer.callNewPlayerRelease
import com.brainwellnessspa.Services.GlobalInitExoPlayer.player
import com.brainwellnessspa.UserModuleTwo.Activities.AddProfileActivity
import com.brainwellnessspa.UserModuleTwo.Activities.WalkScreenActivity
import com.brainwellnessspa.UserModuleTwo.Models.AddedUserListModel
import com.brainwellnessspa.UserModuleTwo.Models.SegmentUserList
import com.brainwellnessspa.UserModuleTwo.Models.VerifyPinModel
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.github.mikephil.charting.utils.*
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
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
    var CoUSERID: String? = null
    var USERID: String? = null
    var UserName: String? = null
    var UserIMAGE: String? = null
    var UserID: String? = null
    var Download = ""
    var Liked = ""
    var MyDownloads: String? = ""
    var SLEEPTIME: String? = null
    var DB: AudioDatabase? = null
    var selectedCategoriesName = arrayListOf<String>()
    var ScreenView = ""
    lateinit var editTexts: Array<EditText>
    var tvSendOTPbool = true
    var myBackPress = false
    var gson: Gson = Gson()
    var AudioId: String? = null
    var homelistModel: HomeScreenModel = HomeScreenModel()
    private var mBottomSheetBehavior: BottomSheetBehavior<View>? = null
    var mBottomSheetDialog: BottomSheetDialog? = null
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        val view = binding.getRoot()
        ctx = requireActivity()
        act = requireActivity()
        val shared1 =
            ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        USERID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        CoUSERID = shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")
        UserName = shared1.getString(CONSTANTS.PREFE_ACCESS_NAME, "")
        UserIMAGE = shared1.getString(CONSTANTS.PREFE_ACCESS_IMAGE, "")

        val shared = ctx.getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
        SLEEPTIME = shared.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")
        val json = shared.getString(CONSTANTS.selectedCategoriesName, gson.toString())
        if (!json.equals(gson.toString(), ignoreCase = true)) {
            val type1 = object : TypeToken<ArrayList<String?>?>() {}.type
            selectedCategoriesName = gson.fromJson(json, type1)
        }

        val p = Properties()
        p.putValue("coUserId", CoUSERID)
        addToSegment("Home Screen Viewed", p, CONSTANTS.screen)

        if (SLEEPTIME.equals("", true)) {
            binding.llSleepTime.visibility = View.GONE
        } else {
            binding.llSleepTime.visibility = View.VISIBLE
        }

        binding.tvSleepTime.text = "Your average sleep time is $SLEEPTIME"
        DB = Room.databaseBuilder(
            ctx,
            AudioDatabase::class.java,
            "Audio_database"
        )
            .addMigrations(MIGRATION_1_2)
            .build()

        binding.tvName.text = UserName
        if (UserIMAGE.equals("", true)) {
            binding.ivUser.setImageResource(R.drawable.ic_gray_user)
        } else {
            Glide.with(requireActivity()).load(UserIMAGE)
                .thumbnail(0.10f).apply(RequestOptions.bitmapTransform(RoundedCorners(126)))
                .into(binding.ivUser)
        }
//        homeViewModel!!.text.observe(viewLifecycleOwner, { s: String? -> })

        binding.rvAreaOfFocusCategory.layoutManager = GridLayoutManager(ctx, 3)
        val adapter = AreaOfFocusAdapter(binding, ctx, selectedCategoriesName)
        binding.rvAreaOfFocusCategory.adapter = adapter

        binding.llCheckIndexSocre.setOnClickListener {
            val intent = Intent(ctx, WalkScreenActivity::class.java)
            intent.putExtra(CONSTANTS.ScreenView, "2")
            startActivity(intent)
        }

        binding.llBottomView.setOnClickListener { v: View? ->
            val layoutBinding: UserListCustomLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(activity),
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
            val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
            layoutBinding.rvUserList.layoutManager = mLayoutManager
            layoutBinding.rvUserList.itemAnimator = DefaultItemAnimator()
            prepareUserData(
                layoutBinding.rvUserList,
                layoutBinding.progressBar,
                layoutBinding.llAddNewUser
            )
            layoutBinding.llAddNewUser.setOnClickListener { v1: View? ->
                val i = Intent(activity, AddProfileActivity::class.java)
                i.putExtra("AddProfile", "Add")
                i.putExtra("CoUserID", "")
                i.putExtra("CoEMAIL", "")
                i.putExtra("CoName", "")
                i.putExtra("CoNumber", "")
                startActivity(i)
                mBottomSheetDialog!!.hide()
            }
        }

        binding.ivEditCategory.setOnClickListener {
            val i = Intent(activity, RecommendedCategoryActivity::class.java)
            i.putExtra("BackClick", "1")
            startActivity(i)
        }

        binding.llClick.setOnClickListener {
            val i = Intent(activity, NotificationListActivity::class.java)
            startActivity(i)
        }
        return view
    }

    override fun onResume() {
        prepareHomeData()
        super.onResume()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(listener)
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(listener1)
        super.onDestroy()
    }

    private fun prepareUserData(
        rvUserList: RecyclerView,
        progressBar: ProgressBar,
        llAddNewUser: LinearLayout
    ) {
        if (isNetworkConnected(activity)) {
            progressBar.visibility = View.VISIBLE
            progressBar.invalidate()
            val listCall = APINewClient.getClient().getUserList(USERID)
            listCall.enqueue(object : Callback<AddedUserListModel> {
                override fun onResponse(
                    call: Call<AddedUserListModel>,
                    response: Response<AddedUserListModel>
                ) {
                    try {
                        progressBar.visibility = View.GONE
                        val listModel: AddedUserListModel = response.body()!!
                        adapter = UserListAdapter(listModel.responseData!!)
                        rvUserList.adapter = adapter

                        if (listModel.responseData!!.coUserList!!.size == listModel.responseData!!.maxuseradd!!.toInt()) {
                            llAddNewUser.visibility = View.GONE
                        } else {
                            llAddNewUser.visibility = View.VISIBLE
                        }
                        val section = java.util.ArrayList<SegmentUserList>()
                        for (i in listModel.responseData!!.coUserList!!.indices) {
                            val e = SegmentUserList()
                            e.coUserId = listModel.responseData!!.coUserList!![i].coUserId
                            e.name = listModel.responseData!!.coUserList!![i].name
                            e.mobile = listModel.responseData!!.coUserList!![i].mobile
                            e.email = listModel.responseData!!.coUserList!![i].email
                            e.image = listModel.responseData!!.coUserList!![i].image
                            e.dob = listModel.responseData!!.coUserList!![i].dob
                            section.add(e)
                        }

                        val p = Properties()
                        val gson = Gson()
                        p.putValue("coUserId", CoUSERID)
                        p.putValue("userId", USERID)
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

    fun prepareHomeData() {
        val sharedPreferences2 = ctx.getSharedPreferences(CONSTANTS.Token, Context.MODE_PRIVATE)
        var fcm_id = sharedPreferences2.getString(CONSTANTS.Token, "")
        val DeviceId = Settings.Secure.getString(
            BWSApplication.getContext().contentResolver,
            Settings.Secure.ANDROID_ID
        )

        Log.e("newToken", fcm_id!!)
        Log.e("deviceid", DeviceId!!)
        Log.e("UserID", USERID!!)
        Log.e("CoUSerID", CoUSERID!!)
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
        if (BWSApplication.isNetworkConnected(activity)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall = APINewClient.getClient().getHomeScreenData(CoUSERID)
            listCall.enqueue(object : Callback<HomeScreenModel?> {
                @SuppressLint("ResourceAsColor")
                override fun onResponse(
                    call: Call<HomeScreenModel?>,
                    response: Response<HomeScreenModel?>
                ) {
                    try {
                        BWSApplication.hideProgressBar(
                            binding.progressBar,
                            binding.progressBarHolder,
                            activity
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
                        editor.commit()
                        if (listModel.responseData!!.scoreIncDec.equals("", ignoreCase = true)) {
                            binding.llCheckPercent.visibility = View.INVISIBLE
                        } else if (listModel.responseData!!.scoreIncDec.equals(
                                "Increase",
                                ignoreCase = true
                            )
                        ) {
                            binding.llCheckPercent.visibility = View.VISIBLE
                            binding.tvPercent.setTextColor(
                                ContextCompat.getColor(
                                    act,
                                    R.color.redtheme
                                )
                            )
                            binding.ivIndexArrow.setBackgroundResource(R.drawable.ic_down_arrow_icon)
                        } else if (listModel.responseData!!.scoreIncDec.equals(
                                "Decrease",
                                ignoreCase = true
                            )
                        ) {
                            binding.llCheckPercent.visibility = View.VISIBLE
                            binding.tvPercent.setTextColor(
                                ContextCompat.getColor(act, R.color.green_dark_s)
                            )
                            binding.ivIndexArrow.setBackgroundResource(R.drawable.ic_up_arrow_icon)
                        }
                        LocalBroadcastManager.getInstance(ctx)
                            .registerReceiver(listener, IntentFilter("play_pause_Action"))
                        LocalBroadcastManager.getInstance(ctx)
                            .registerReceiver(listener1, IntentFilter("Reminder"))
                        binding.tvPercent.text =
                            listModel.responseData!!.indexScoreDiff!!.split(".")[0] + "%"
                        binding.tvSevere.text = listModel.responseData!!.indexScore.toString()
                        binding.llIndicate.progress = listModel.responseData!!.indexScore!!.toInt()

                        binding.tvPlaylistName.text =
                            listModel.responseData!!.suggestedPlaylist!!.playlistName
                        binding.tvTime.text =
                            listModel.responseData!!.suggestedPlaylist!!.totalhour.toString() + ":" + listModel.responseData!!.suggestedPlaylist!!.totalminute.toString()


                        if (listModel.responseData!!.shouldCheckIndexScore.equals("0", true)) {
                            binding.llCheckIndexSocre.visibility = View.GONE
                        } else if (listModel.responseData!!.shouldCheckIndexScore.equals(
                                "1",
                                ignoreCase = true
                            )
                        ) {
                            binding.llCheckIndexSocre.visibility = View.VISIBLE
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
                            binding.tvReminder.setText("Set Reminder")
                        } else if (listModel.responseData!!.suggestedPlaylist!!.isReminder.equals(
                                "1",
                                ignoreCase = true
                            )
                        ) {
                            binding.tvReminder.setText("Update Reminder")
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
                                binding.tvReminder.setText("Set Reminder")
                                getReminderDay(
                                    ctx,
                                    act,
                                    CoUSERID,
                                    listModel.responseData!!.suggestedPlaylist!!.playlistID,
                                    listModel.responseData!!.suggestedPlaylist!!.playlistName,
                                    activity,
                                    listModel.responseData!!.suggestedPlaylist!!.reminderTime,
                                    listModel.responseData!!.suggestedPlaylist!!.reminderDay
                                )
                            } else if (listModel.responseData!!.suggestedPlaylist!!.isReminder.equals(
                                    "1",
                                    ignoreCase = true
                                )
                            ) {
                                binding.tvReminder.setText("Update Reminder")
                                val dialog = Dialog(ctx)
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                dialog.setContentView(R.layout.delete_reminder)
                                dialog.window!!.setBackgroundDrawable(
                                    ColorDrawable(
                                        ContextCompat.getColor(
                                            ctx,
                                            R.color.dark_blue_gray
                                        )
                                    )
                                )
                                dialog.window!!.setLayout(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
                                val tvSubTitle = dialog.findViewById<TextView>(R.id.tvSubTitle)
                                val tvText = dialog.findViewById<TextView>(R.id.tvText)
                                val tvGoBack = dialog.findViewById<TextView>(R.id.tvGoBack)
                                val tvconfirm = dialog.findViewById<RelativeLayout>(R.id.tvconfirm)
                                tvTitle.text = "Update Reminder"
                                tvSubTitle.text = "You can update or delete your reminder"
                                tvText.text = "Update"
                                tvGoBack.text = "Delete"
                                dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                                        dialog.hide()
                                        return@setOnKeyListener true
                                    }
                                    false
                                }
                                tvconfirm.setOnClickListener {
                                    dialog.hide()
                                    getReminderDay(
                                        ctx,
                                        act,
                                        CoUSERID,
                                        listModel.responseData!!.suggestedPlaylist!!.playlistID,
                                        listModel.responseData!!.suggestedPlaylist!!.playlistName,
                                        activity,
                                        listModel.responseData!!.suggestedPlaylist!!.reminderTime,
                                        listModel.responseData!!.suggestedPlaylist!!.reminderDay
                                    )
                                }
                                tvGoBack.setOnClickListener { v: View? ->
                                    val listCall = APINewClient.getClient().getDeleteRemider(
                                        CoUSERID,
                                        listModel.responseData!!.suggestedPlaylist!!.reminderId
                                    )
                                    listCall.enqueue(object : Callback<DeleteRemiderModel?> {
                                        override fun onResponse(
                                            call: Call<DeleteRemiderModel?>,
                                            response: Response<DeleteRemiderModel?>
                                        ) {
                                            try {
                                                val model = response.body()
                                                if (model!!.responseCode.equals(
                                                        ctx.getString(R.string.ResponseCodesuccess),
                                                        ignoreCase = true
                                                    )
                                                ) {
                                                    BWSApplication.showToast(
                                                        model.responseMessage,
                                                        activity
                                                    )
                                                    dialog.dismiss()
                                                }
                                            } catch (e: java.lang.Exception) {
                                                e.printStackTrace()
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<DeleteRemiderModel?>,
                                            t: Throwable
                                        ) {
                                        }
                                    })
                                }
                                dialog.show()
                                dialog.setCancelable(false)
                            }
                        }

                        GetPlaylistDetail(listModel.responseData!!.suggestedPlaylist!!.playlistID!!)

                        BWSApplication.getPastIndexScore(
                            homelistModel.responseData!!,
                            binding.barChart,
                            activity
                        )

                        setPlayPauseIcon()

                        val sharedd = ctx.getSharedPreferences(
                            CONSTANTS.RecommendedCatMain,
                            Context.MODE_PRIVATE
                        )
                        SLEEPTIME = sharedd.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")

                        if (SLEEPTIME.equals("", true)) {
                            binding.llSleepTime.visibility = View.GONE
                        } else {
                            binding.llSleepTime.visibility = View.VISIBLE
                        }
                        binding.tvSleepTime.text = "Your average sleep time is $SLEEPTIME"

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
                            val shared1 = ctx.getSharedPreferences(
                                CONSTANTS.PREF_KEY_PLAYER,
                                AppCompatActivity.MODE_PRIVATE
                            )
                            val AudioPlayerFlag =
                                shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                            val MyPlaylist =
                                shared1.getString(CONSTANTS.PREF_KEY_PayerPlaylistId, "")
                            val PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
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
                                        if (BWSApplication.PlayerAudioId.equals(
                                                lastIndexID,
                                                ignoreCase = true
                                            )
                                            && player.duration - player.currentPosition <= 20
                                        ) {
                                            val shared = ctx.getSharedPreferences(
                                                CONSTANTS.PREF_KEY_PLAYER,
                                                Context.MODE_PRIVATE
                                            )
                                            val editor = shared.edit()
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
                                        activity,
                                        listModel.responseData!!.suggestedPlaylist!!.playlistSongs!![0].playlistID!!
                                    )
                                    binding.llPlay.visibility = View.GONE
                                    binding.llPause.visibility = View.VISIBLE
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                private fun callPlaylistDetails() {
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
                }

                override fun onFailure(call: Call<HomeScreenModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(
                        binding.progressBar,
                        binding.progressBarHolder,
                        activity
                    )
                }
            })
        }
    }

    private fun setPlayPauseIcon() {

        val shared1 =
            ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, AppCompatActivity.MODE_PRIVATE)
        val AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        val MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PayerPlaylistId, "")
        val PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
        val PlayerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
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
        activity: FragmentActivity?,
        playlistID: String
    ) {
        val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
        val AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        val MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PayerPlaylistId, "")
        val PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
        var PlayerPosition: Int = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
        val shared12 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE)
        val IsPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1")
        if (MyDownloads.equals("1", true)) {
            if (AudioPlayerFlag.equals("Downloadlist", ignoreCase = true) && MyPlaylist.equals(
                    playlistID,
                    ignoreCase = true
                )
            ) {
                if (MiniPlayerFragment.isDisclaimer == 1) {
                    if (player != null) {
                        if (!player.playWhenReady) {
                            player.playWhenReady = true
                        }
                    } else {
                        DashboardActivity.audioClick = true
                    }
                    callMyPlayer(ctx, act)
                    BWSApplication.showToast(
                        "The audio shall start playing after the disclaimer",
                        activity
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
                        callMyPlayer(ctx, act)
                    } else {
                        callPlayerSuggested(position, view, listModel, ctx, act, playlistID)
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
                if (MiniPlayerFragment.isDisclaimer == 1) {
                    if (player != null) {
                        player.playWhenReady = true
                        audioc = false
                        listModelList2.add(position, mainPlayModel)
                    } else {
                        MiniPlayerFragment.isDisclaimer = 0
                        if (IsPlayDisclimer.equals("1", ignoreCase = true)) {
                            audioc = true
                            listModelList2.add(position, mainPlayModel)
                        }
                    }
                } else {
                    MiniPlayerFragment.isDisclaimer = 0
                    if (IsPlayDisclimer.equals("1", ignoreCase = true)) {
                        audioc = true
                        listModelList2.add(position, mainPlayModel)
                    }
                }
                callPlayerSuggested(position, view, listModelList2, ctx, act, playlistID)
            }
        } else {
            if (AudioPlayerFlag.equals("playlist", ignoreCase = true) && MyPlaylist.equals(
                    playlistID,
                    ignoreCase = true
                )
            ) {
                if (MiniPlayerFragment.isDisclaimer == 1) {
                    if (player != null) {
                        if (!player.playWhenReady) {
                            player.playWhenReady = true
                        }
                    } else {
                        DashboardActivity.audioClick = true
                    }
                    callMyPlayer(ctx, act)
                    BWSApplication.showToast(
                        "The audio shall start playing after the disclaimer",
                        activity
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
                        callMyPlayer(ctx, act)
                    } else {
                        callPlayerSuggested(position, view, listModel, ctx, act, playlistID)
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
                if (MiniPlayerFragment.isDisclaimer == 1) {
                    if (player != null) {
                        player.playWhenReady = true
                        audioc = false
                        listModelList2.add(position, mainPlayModel)
                    } else {
                        MiniPlayerFragment.isDisclaimer = 0
                        if (IsPlayDisclimer.equals("1", ignoreCase = true)) {
                            audioc = true
                            listModelList2.add(position, mainPlayModel)
                        }
                    }
                } else {
                    MiniPlayerFragment.isDisclaimer = 0
                    if (IsPlayDisclimer.equals("1", ignoreCase = true)) {
                        audioc = true
                        listModelList2.add(position, mainPlayModel)
                    }
                }
                callPlayerSuggested(position, view, listModelList2, ctx, act, playlistID)
            }
        }
    }

    private fun GetPlaylistDetail(PlaylistID: String) {
        try {
            /* DB!!.taskDao()
                     .getPlaylist1(PlaylistID).observe(this, { audioList: List<DownloadPlaylistDetails?> ->
                         if (audioList.isNotEmpty()) {
                             MyDownloads = "1"
                         } else {
                             MyDownloads = "0"
                         }
                     })*/
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
        playlistID: String
    ) {
        callNewPlayerRelease()
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
        editor.putString(CONSTANTS.PREF_KEY_PayerPlaylistId, playlistID)
        editor.putString(CONSTANTS.PREF_KEY_PlayFrom, view)
        if (MyDownloads.equals("1", ignoreCase = true)) {
            editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "Downloadlist")
        } else {
            editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "playlist")
        }
        editor.apply()
        DashboardActivity.audioClick = true
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

    inner class UserListAdapter(private val model: AddedUserListModel.ResponseData) :
        RecyclerView.Adapter<UserListAdapter.MyViewHolder>() {
        var selectedItem = -1
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: MultipleProfileChangeLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.multiple_profile_change_layout,
                parent,
                false
            )
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val modelList = model.coUserList
            holder.bind.tvName.text = modelList!![position].name
            if (modelList[position].image.equals("", true)) {
                holder.bind.ivProfileImage.setImageResource(R.drawable.ic_user_default_icon)
            } else {
                Glide.with(activity!!).load(modelList[position].image)
                    .thumbnail(0.10f).apply(RequestOptions.bitmapTransform(RoundedCorners(126)))
                    .into(holder.bind.ivProfileImage)
            }
            holder.bind.ivCheck.setImageResource(R.drawable.ic_user_checked_icon)
            holder.bind.ivCheck.visibility = View.INVISIBLE
            if (selectedItem == position) {
                holder.bind.ivCheck.visibility = View.VISIBLE
            }
            holder.bind.llAddNewCard.setOnClickListener {
                val previousItem = selectedItem
                selectedItem = position
                notifyItemChanged(previousItem)
                notifyItemChanged(position)
                val dialog = Dialog(activity!!)
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
                        if (BWSApplication.isNetworkConnected(activity)) {
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
                                                if (responseData!!.isProfileCompleted.equals(
                                                        "0",
                                                        ignoreCase = true
                                                    )
                                                ) {
                                                    val intent =
                                                        Intent(activity, WalkScreenActivity::class.java)
                                                    intent.putExtra(CONSTANTS.ScreenView, "ProfileView")
                                                    act.startActivity(intent)
                                                    act.finish()
                                                } else if (responseData.isAssessmentCompleted.equals(
                                                        "0",
                                                        ignoreCase = true
                                                    )
                                                ) {
                                                    val intent =
                                                        Intent(activity, AssProcessActivity::class.java)
                                                    intent.putExtra(CONSTANTS.ASSPROCESS, "0")
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
                                                        activity,
                                                        BottomNavigationActivity::class.java
                                                    )
                                                    act.startActivity(intent)
                                                    act.finish()
                                                }
                                                val shared = act.getSharedPreferences(
                                                    CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER,
                                                    AppCompatActivity.MODE_PRIVATE
                                                )
                                                val editor = shared.edit()
                                                editor.putString(
                                                    CONSTANTS.PREFE_ACCESS_UserID,
                                                    listModel.responseData!!.userID
                                                )
                                                editor.putString(
                                                    CONSTANTS.PREFE_ACCESS_CoUserID,
                                                    listModel.responseData!!.coUserId
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
                                                    CONSTANTS.PREFE_ACCESS_SLEEPTIME,
                                                    listModel.responseData!!.avgSleepTime
                                                )
                                                editor.putString(
                                                    CONSTANTS.PREFE_ACCESS_INDEXSCORE,
                                                    listModel.responseData!!.indexScore
                                                )
                                                editor.putString(
                                                    CONSTANTS.PREFE_ACCESS_IMAGE,
                                                    responseData.image
                                                )
                                                editor.commit()
                                                prepareHomeData()
                                                showToast(
                                                    listModel.responseMessage,
                                                    activity
                                                )
                                                dialog.dismiss()
                                                mBottomSheetDialog!!.hide()

                                                analytics.identify(
                                                    Traits()
                                                        .putEmail(listModel.responseData!!.email)
                                                        .putName(listModel.responseData!!.name)
                                                        .putPhone(listModel.responseData!!.mobile)
                                                        .putValue(
                                                            "coUserId",
                                                            listModel.responseData!!.coUserId
                                                        )
                                                        .putValue(
                                                            "userId",
                                                            listModel.responseData!!.userID
                                                        )
                                                        .putValue(
                                                            "deviceId", Settings.Secure.getString(
                                                                activity!!.contentResolver,
                                                                Settings.Secure.ANDROID_ID
                                                            )
                                                        )
                                                        .putValue("deviceType", "Android")
                                                        .putValue("name", listModel.responseData!!.name)
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
                                                        .putValue("DOB", listModel.responseData!!.dob)
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

                                override fun onFailure(call: Call<VerifyPinModel?>, t: Throwable) {
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

        override fun getItemCount(): Int {
            return model.getCoUser()!!.size
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
            val OTP1 = edtOTP1.text.toString().trim { it <= ' ' }
            val OTP2 = edtOTP2.text.toString().trim { it <= ' ' }
            val OTP3 = edtOTP3.text.toString().trim { it <= ' ' }
            val OTP4 = edtOTP4.text.toString().trim { it <= ' ' }
            if (!OTP1.isEmpty() && !OTP2.isEmpty() && !OTP3.isEmpty() && !OTP4.isEmpty()) {
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
            } else if (text.length == 0) {
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
            if (activity!!.currentFocus != null) {
                val inputMethodManager =
                    activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, 0)
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

    inner class PopupTextWatcher(var edtCreate: EditText, var btnSendCode: Button) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val create = edtCreate.text.toString().trim { it <= ' ' }
            if (create.equals(
                    homelistModel.responseData!!.suggestedPlaylist!!.playlistName,
                    ignoreCase = true
                )
            ) {
                btnSendCode.isEnabled = false
                btnSendCode.setTextColor(resources.getColor(R.color.white))
                btnSendCode.setBackgroundResource(R.drawable.gray_round_cornor)
            } else if (create.isEmpty()) {
                btnSendCode.isEnabled = false
                btnSendCode.setTextColor(resources.getColor(R.color.white))
                btnSendCode.setBackgroundResource(R.drawable.gray_round_cornor)
            } else {
                btnSendCode.isEnabled = true
                btnSendCode.setTextColor(resources.getColor(R.color.light_black))
                btnSendCode.setBackgroundResource(R.drawable.white_round_cornor)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }
}