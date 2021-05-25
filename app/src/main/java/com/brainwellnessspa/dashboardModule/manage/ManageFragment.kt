package com.brainwellnessspa.dashboardModule.manage

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.*
import android.content.Context.MODE_PRIVATE
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.BWSApplication.isPlayPlaylist
import com.brainwellnessspa.DashboardOldModule.Activities.DashboardActivity.audioClick
import com.brainwellnessspa.DashboardOldModule.TransparentPlayer.Fragments.MiniPlayerFragment.isDisclaimer
import com.brainwellnessspa.R
import com.brainwellnessspa.RoomDataBase.*
import com.brainwellnessspa.Services.GlobalInitExoPlayer.callNewPlayerRelease
import com.brainwellnessspa.Services.GlobalInitExoPlayer.player
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.dashboardModule.activities.AddAudioActivity
import com.brainwellnessspa.dashboardModule.activities.AddPlaylistActivity
import com.brainwellnessspa.dashboardModule.activities.MyPlayerActivity
import com.brainwellnessspa.dashboardModule.models.CreateNewPlaylistModel
import com.brainwellnessspa.dashboardModule.models.HomeDataModel
import com.brainwellnessspa.dashboardModule.models.HomeScreenModel
import com.brainwellnessspa.dashboardModule.models.PlaylistDetailsModel
import com.brainwellnessspa.databinding.*
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
    var CoUserID: String? = ""
    var USERID: String? = ""
    var MyDownloads: String? = ""
    var SLEEPTIME: String? = null
    var DB: AudioDatabase? = null
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage, container, false)
        val view: View = binding.root
        ctx = requireActivity()
        act = requireActivity()
        val shared = ctx.getSharedPreferences(
            CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER,
            AppCompatActivity.MODE_PRIVATE
        )
        USERID = shared.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")
        binding.rvMainPlayList.layoutManager =
            LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMainAudioList.layoutManager =
            LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)


        val sharedd = ctx.getSharedPreferences(CONSTANTS.RecommendedCatMain, MODE_PRIVATE)
        SLEEPTIME = sharedd.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")

        if (SLEEPTIME.equals("", true)) {
            binding.llSleepTime.visibility = View.GONE
        } else {
            binding.llSleepTime.visibility = View.VISIBLE

        }
        binding.tvSleepTime.text = "Your average sleep time is $SLEEPTIME"
        if (BWSApplication.isNetworkConnected(activity)) {
            binding.llSetReminder.visibility = View.VISIBLE
        } else {
            binding.llSetReminder.visibility = View.GONE
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 80, 0, 110)
            binding.llSpace1.setLayoutParams(params)
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
        binding.llSearch.setOnClickListener {
            if (BWSApplication.isNetworkConnected(activity)) {
                val i = Intent(ctx, AddAudioActivity::class.java)
                i.putExtra("PlaylistID", "")
                startActivity(i)
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), activity)
            }
        }

        binding.tvViewAll.setOnClickListener {
            val audioFragment: Fragment = MainPlaylistFragment()
            val fragmentManager1 = requireActivity().supportFragmentManager
            fragmentManager1.beginTransaction()
                .replace(R.id.flContainer, audioFragment)
                .commit()
        }
        DB = Room.databaseBuilder(
            ctx,
            AudioDatabase::class.java,
            "Audio_database"
        )
            .addMigrations(BWSApplication.MIGRATION_1_2)
            .build()

        binding.rlCreatePlaylist.setOnClickListener {
            val dialog = Dialog(ctx)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.create_palylist)
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(
                    ContextCompat.getColor(
                        act,
                        R.color.blue_transparent
                    )
                )
            )
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            val edtCreate = dialog.findViewById<EditText>(R.id.edtCreate)
            val tvCancel = dialog.findViewById<TextView>(R.id.tvCancel)
            val btnSendCode = dialog.findViewById<Button>(R.id.btnSendCode)
            edtCreate.requestFocus()
            val popupTextWatcher: TextWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    val number = edtCreate.text.toString().trim { it <= ' ' }
                    if (number.isNotEmpty()) {
                        btnSendCode.isEnabled = true
                        btnSendCode.setTextColor(
                            ContextCompat.getColor(
                                act,
                                R.color.black
                            )
                        )
                        btnSendCode.setBackgroundResource(R.drawable.white_round_cornor)
                    } else {
                        btnSendCode.isEnabled = false
                        btnSendCode.setTextColor(
                            ContextCompat.getColor(
                                act,
                                R.color.white
                            )
                        )
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
                if (BWSApplication.isNetworkConnected(ctx)) {
                    BWSApplication.showProgressBar(
                        binding.progressBar,
                        binding.progressBarHolder,
                        act
                    )
                    val listCall = APINewClient.getClient()
                        .getCreatePlaylist(CoUserID, edtCreate.text.toString())
                    listCall.enqueue(object : Callback<CreateNewPlaylistModel?> {
                        override fun onResponse(
                            call: Call<CreateNewPlaylistModel?>,
                            response: Response<CreateNewPlaylistModel?>
                        ) {
                            try {
                                BWSApplication.hideProgressBar(
                                    binding.progressBar,
                                    binding.progressBarHolder,
                                    act
                                )
                                if (response.isSuccessful) {
                                    val listModel = response.body()
                                    if (listModel!!.responseData!!.iscreate.equals(
                                            "0",
                                            ignoreCase = true
                                        )
                                    ) {
                                        BWSApplication.showToast(
                                            listModel.responseMessage,
                                            act
                                        )
                                        dialog.dismiss()
                                    } else if (listModel.responseData!!.iscreate.equals(
                                            "1",
                                            ignoreCase = true
                                        ) ||
                                        listModel.responseData!!.iscreate.equals(
                                            "",
                                            ignoreCase = true
                                        )
                                    ) {
//                                        try {
                                        val i = Intent(ctx, MyPlaylistListingActivity::class.java)
                                        i.putExtra("New", "1")
                                        i.putExtra(
                                            "PlaylistID",
                                            listModel.responseData!!.playlistID
                                        )
                                        i.putExtra(
                                            "PlaylistName",
                                            listModel.responseData!!.playlistName
                                        )
                                        i.putExtra("PlaylistImage", "")
                                        i.putExtra("MyDownloads", "0")
                                        i.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                                        ctx.startActivity(i)
                                        act.overridePendingTransition(0, 0)
                                        dialog.dismiss()
//                                        } catch (e: Exception) {
//                                            e.printStackTrace()
//                                            dialog.dismiss()
//                                        }
                                    }
                                }
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(call: Call<CreateNewPlaylistModel?>, t: Throwable) {
                            BWSApplication.hideProgressBar(
                                binding.progressBar,
                                binding.progressBarHolder,
                                act
                            )
                        }
                    })
                } else {
                    BWSApplication.showToast(ctx.getString(R.string.no_server_found), act)
                }
            }
            tvCancel.setOnClickListener { v: View? -> dialog.dismiss() }
            dialog.show()
            dialog.setCancelable(false)
        }

        return view
    }

    override fun onResume() {
        prepareData()
        super.onResume()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(listener)
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(listener1)
        super.onDestroy()
    }

    private fun callObserverMethod(listModel: List<HomeDataModel.ResponseData.Audio>) {
        DatabaseClient
            .getInstance(act)
            .getaudioDatabase()
            .taskDao()
            .geAllDataz("")
            .observe(requireActivity(), { downloadAudioDetails: List<DownloadAudioDetailsUniq> ->
                val details = ArrayList<HomeDataModel.ResponseData.Audio.Detail>()
                if (downloadAudioDetails.isNotEmpty()) {
                    for (i in downloadAudioDetails.indices) {
                        val detail = HomeDataModel.ResponseData.Audio.Detail()
                        detail.id = downloadAudioDetails[i].id
                        detail.name = downloadAudioDetails[i].name
                        detail.audioFile = downloadAudioDetails[i].audioFile
                        detail.audioDirection = downloadAudioDetails[i].audioDirection
                        detail.audiomastercat = downloadAudioDetails[i].audiomastercat
                        detail.audioSubCategory = downloadAudioDetails[i].audioSubCategory
                        detail.imageFile = downloadAudioDetails[i].imageFile
                        detail.audioDuration = downloadAudioDetails[i].audioDuration
                        details.add(detail)
                    }
                    for (i in listModel.indices) {
                        if (listModel[i].view.equals("My Downloads", ignoreCase = true)) {
                            listModel[i].details = details
                        }
                    }
                    val fragmentManager1: FragmentManager =
                        (ctx as FragmentActivity).supportFragmentManager

                    audioAdapter = AudioAdapter(listModel, ctx, binding, act, fragmentManager1)
                    binding.rvMainAudioList.adapter = audioAdapter
                } else {
                    if (BWSApplication.isNetworkConnected(act)) {
                        val fragmentManager1: FragmentManager =
                            (ctx as FragmentActivity).supportFragmentManager
                        audioAdapter = AudioAdapter(listModel, ctx, binding, act, fragmentManager1)
                        binding.rvMainAudioList.adapter = audioAdapter
                    }
                }
            })
    }

    fun callMainPlayer(
        position: Int,
        views: String?,
        listModel: List<HomeDataModel.ResponseData.Audio.Detail>,
        ctx: Context,
        act: Activity
    ) {
        val shared1 =
            ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, AppCompatActivity.MODE_PRIVATE)
        val AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        val MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
        val PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
        val playerPosition: Int = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
        val shared12 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
        var IsPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1")
        if ((AudioPlayerFlag.equals("MainAudioList", ignoreCase = true) ||
                    AudioPlayerFlag.equals(
                        "ViewAllAudioList",
                        ignoreCase = true
                    )) && PlayFrom.equals(views, ignoreCase = true)
        ) {
            if (BWSApplication.isNetworkConnected(ctx)) {
                if (isDisclaimer == 1) {
                    if (player != null) {
                        if (!player.playWhenReady) {
                            player.playWhenReady = true
                        }
                    } else {
                        audioClick = true
                    }
                    callMyPlayer(ctx, act)
                    BWSApplication.showToast(
                        "The audio shall start playing after the disclaimer",
                        act
                    )
                } else {
                    if (player != null) {
                        if (position != playerPosition) {
                            player.seekTo(position, 0)
                            player.playWhenReady = true
                            val sharedxx = ctx.getSharedPreferences(
                                CONSTANTS.PREF_KEY_PLAYER,
                                AppCompatActivity.MODE_PRIVATE
                            )
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
                if (views.equals("My Downloads") && !BWSApplication.isNetworkConnected(ctx)) {
                    getMedia(views!!, AudioPlayerFlag!!, position, listModel, ctx, act)
                }
            }
        } else {
            val listModelList2 = arrayListOf<HomeDataModel.ResponseData.Audio.Detail>()
            listModelList2.addAll(listModel)
            val gson = Gson()
            val disclimerJson = shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
            val type = object : TypeToken<HomeDataModel.ResponseData.Audio.Detail?>() {}.type
            val arrayList =
                gson.fromJson<HomeDataModel.ResponseData.Audio.Detail>(disclimerJson, type)
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
            if (BWSApplication.isNetworkConnected(ctx)) {
                callPlayer(position, views, listModelList2, ctx, act, audioc)
            } else {
                if (views.equals("My Downloads") && !BWSApplication.isNetworkConnected(ctx)) {
                    getMedia(views!!, AudioPlayerFlag!!, position, listModel, ctx, act)
                }
            }
        }
    }

    private fun getMedia(
        views: String?,
        AudioFlag: String,
        position: Int,
        listModelList: List<HomeDataModel.ResponseData.Audio.Detail>,
        ctx: Context,
        act: Activity
    ) {
        DB = Room.databaseBuilder(
            ctx,
            AudioDatabase::class.java,
            "Audio_database"
        )
            .addMigrations(BWSApplication.MIGRATION_1_2)
            .build()
        AudioDatabase.databaseWriteExecutor.execute {
            downloadAudioDetailsList =
                DB!!.taskDao().geAllDataBYDownloaded("Complete") as ArrayList<String>
        }
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
                BWSApplication.showToast("The audio shall start playing after the disclaimer", act)
            } else {
                val listModelList2 = arrayListOf<HomeDataModel.ResponseData.Audio.Detail>()
                for (i in listModelList.indices) {
                    if (downloadAudioDetailsList.contains(listModelList[i].name)) {
                        listModelList2.add(listModelList[i])
                    }
                }
                if (downloadAudioDetailsList.contains(listModelList[position].id)) {
                    pos = position
                } else {
                    BWSApplication.showToast(ctx.getString(R.string.no_server_found), act)
                }
                if (listModelList2.size != 0) {
                    callPlayer(pos, views!!, listModelList2, ctx, act, true)
                } else {
                    BWSApplication.showToast(ctx.getString(R.string.no_server_found), act)
                }
            }
        } else {
            val shared12 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
            val IsPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1")
            val listModelList2 = arrayListOf<HomeDataModel.ResponseData.Audio.Detail>()
            for (i in listModelList.indices) {
                if (downloadAudioDetailsList.contains(listModelList[i].name)) {
                    listModelList2.add(listModelList.get(i))
                }
            }
            if (downloadAudioDetailsList.contains(listModelList[position].name)) {
                pos = position
                val gson = Gson()
                val DisclimerJson =
                    shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
                val type =
                    object : TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio?>() {}.type
                val arrayList =
                    gson.fromJson<HomeScreenModel.ResponseData.DisclaimerAudio>(DisclimerJson, type)
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
                        if (IsPlayDisclimer.equals("1", ignoreCase = true)) {
                            audioc = true
                            listModelList2.add(pos, mainPlayModel)
                        }
                    }
                } else {
                    isDisclaimer = 0
                    if (IsPlayDisclimer.equals("1", ignoreCase = true)) {
                        listModelList2.add(pos, mainPlayModel)
                        audioc = true
                    }
                }
                if (listModelList2.size != 0) {
                    if (!listModelList2[pos].id.equals("0")) {
                        if (listModelList2.size != 0) {
                            callPlayer(pos, views!!, listModelList2, ctx, act, audioc)
                        } else {
                            BWSApplication.showToast(ctx.getString(R.string.no_server_found), act)
                        }
                    } else if (listModelList2[pos].id.equals("0") && listModelList2.size > 1) {
                        callPlayer(pos, views!!, listModelList2, ctx, act, audioc)
                    } else {
                        BWSApplication.showToast(ctx.getString(R.string.no_server_found), act)
                    }
                } else {
                    BWSApplication.showToast(ctx.getString(R.string.no_server_found), act)
                }
            } else {
                BWSApplication.showToast(ctx.getString(R.string.no_server_found), act)
            }
        }
    }

    private fun callMyPlayer(ctx: Context, act: Activity) {
        val i = Intent(ctx, MyPlayerActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        ctx.startActivity(i)
        act.overridePendingTransition(0, 0)
    }

    private fun callPlayer(
        position: Int,
        view: String?,
        listModel: List<HomeDataModel.ResponseData.Audio.Detail>,
        ctx: Context,
        act: Activity, audioc: Boolean
    ) {
        if (audioc) {
            callNewPlayerRelease()
        }
        val shared =
            ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, AppCompatActivity.MODE_PRIVATE)
        val editor = shared.edit()
        val gson = Gson()
        var json = ""
        if (view.equals("My Downloads", true)) {
            val downloadAudioDetails = ArrayList<DownloadAudioDetails>()
            for (i in listModel.indices) {
                val mainPlayModel = DownloadAudioDetails()
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
            json = gson.toJson(downloadAudioDetails)
            editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "DownloadListAudio")
        } else {
            json = gson.toJson(listModel)
            editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "MainAudioList")
        }
        editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json)
        editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position)
        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
        editor.putString(CONSTANTS.PREF_KEY_PlayFrom, view)
        editor.apply()
        audioClick = audioc
        callMyPlayer(ctx, act)
    }

    private fun prepareData() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, act)
            val listCall = APINewClient.getClient().getHomeData(CoUserID)
            listCall.enqueue(object : Callback<HomeDataModel?> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<HomeDataModel?>,
                    response: Response<HomeDataModel?>
                ) {
                    BWSApplication.hideProgressBar(
                        binding.progressBar,
                        binding.progressBarHolder,
                        act
                    )

                    val listModel = response.body()!!
                    homelistModel = response.body()!!
                    binding.llMainLayout.visibility = View.VISIBLE
                    binding.llSpace.visibility = View.VISIBLE
                    binding.llPlayer.visibility = View.VISIBLE
                    binding.llUser.visibility = View.VISIBLE
                    binding.tvPlaylistName.text =
                        listModel.responseData!!.suggestedPlaylist!!.playlistName
                    binding.tvTime.text =
                        listModel.responseData!!.suggestedPlaylist!!.totalhour + ":" + listModel.responseData!!.suggestedPlaylist!!.totalminute
                    val section = java.util.ArrayList<String>()
                    for (i in listModel.responseData!!.audio.indices) {
                        section.add(listModel.responseData!!.audio[i].view!!)
                    }
                    val p = Properties()
                    p.putValue("coUserId", CoUserID)
                    val gson: Gson
                    val gsonBuilder = GsonBuilder()
                    gson = gsonBuilder.create()
                    p.putValue("sections", gson.toJson(section))
                    BWSApplication.addToSegment("Manage Screen Viewed", p, CONSTANTS.screen)
                    val measureRatio = BWSApplication.measureRatio(ctx, 0f, 1f, 1f, 0.38f, 0f)
                    binding.ivCreatePlaylist.layoutParams.height =
                        (measureRatio.height * measureRatio.ratio).toInt()
                    binding.ivCreatePlaylist.layoutParams.width =
                        (measureRatio.widthImg * measureRatio.ratio).toInt()
                    binding.ivCreatePlaylist.scaleType = ImageView.ScaleType.FIT_XY
                    Glide.with(act).load(R.drawable.ic_create_playlist)
                        .thumbnail(0.05f)
                        .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                        .priority(Priority.HIGH)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                        .into(binding.ivCreatePlaylist)

                    playlistAdapter =
                        PlaylistAdapter(listModel.responseData!!.playlist[0], ctx, binding, act)
                    binding.rvMainPlayList.adapter = playlistAdapter

                    if (listModel.responseData!!.playlist[0].details!!.size > 4) {
                        binding.tvViewAll.visibility = View.VISIBLE
                    } else {
                        binding.tvViewAll.visibility = View.GONE
                    }
                    getPlaylistDetail(listModel.responseData!!.suggestedPlaylist!!.playlistID!!)
                    LocalBroadcastManager.getInstance(ctx)
                        .registerReceiver(listener1, IntentFilter("Reminder"))
                    binding.llPlayerView1.setOnClickListener {
                        callPlaylistDetails()
                    }
                    binding.llPlayerView2.setOnClickListener {
                        callPlaylistDetails()
                    }
                    binding.llPlaylistDetails.setOnClickListener {
                        callPlaylistDetails()
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
                            BWSApplication.getReminderDay(
                                ctx,
                                act,
                                CoUserID,
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
                            binding.tvReminder.text = "Update reminder"
                            binding.llSetReminder.setBackgroundResource(R.drawable.rounded_extra_dark_theme_corner)
                            BWSApplication.getReminderDay(
                                ctx,
                                act,
                                CoUserID,
                                listModel.responseData!!.suggestedPlaylist!!.playlistID,
                                listModel.responseData!!.suggestedPlaylist!!.playlistName,
                                activity,
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
                            BWSApplication.getReminderDay(
                                ctx,
                                act,
                                CoUserID,
                                listModel.responseData!!.suggestedPlaylist!!.playlistID,
                                listModel.responseData!!.suggestedPlaylist!!.playlistName,
                                activity,
                                listModel.responseData!!.suggestedPlaylist!!.reminderTime,
                                listModel.responseData!!.suggestedPlaylist!!.reminderDay
                            )
                        }
                    }

                    setPlayPauseIcon()

                    LocalBroadcastManager.getInstance(ctx)
                        .registerReceiver(listener, IntentFilter("play_pause_Action"))
                    binding.llPlayPause.setOnClickListener {
                        if (BWSApplication.isNetworkConnected(getActivity())) {
                            val shared1 = ctx.getSharedPreferences(
                                CONSTANTS.PREF_KEY_PLAYER,
                                AppCompatActivity.MODE_PRIVATE
                            )
                            val AudioPlayerFlag =
                                shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                            val MyPlaylist =
                                shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
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
                                                MODE_PRIVATE
                                            )
                                            val editor = shared.edit()
                                            editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                                            editor.apply()
                                            player.seekTo(0, 0)
                                            BWSApplication.PlayerAudioId =
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
                                    BWSApplication.PlayerAudioId =
                                        listModel.responseData!!.suggestedPlaylist!!.playlistSongs!![PlayerPosition].id
                                    callMainPlayerSuggested(
                                        0,
                                        "",
                                        listModel.responseData!!.suggestedPlaylist!!.playlistSongs!!,
                                        ctx,
                                        act,
                                        listModel.responseData!!.suggestedPlaylist!!.playlistSongs!![0].playlistID!!
                                    )
                                    binding.llPlay.visibility = View.GONE
                                    binding.llPause.visibility = View.VISIBLE
                                }
                            }
                        } else {
                            BWSApplication.showToast(getString(R.string.no_server_found), activity)
                        }
                    }

                    callObserverMethod(listModel.responseData!!.audio)
                }

                private fun callPlaylistDetails() {
                    if (BWSApplication.isNetworkConnected(activity)) {
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
                        BWSApplication.showToast(getString(R.string.no_server_found), activity)
                    }
                }

                override fun onFailure(call: Call<HomeDataModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(
                        binding.progressBar,
                        binding.progressBarHolder,
                        act
                    )
                }
            })
        } else {
            val responseData = ArrayList<HomeDataModel.ResponseData.Audio>()
            val details = ArrayList<HomeDataModel.ResponseData.Audio.Detail>()
            val listModel = HomeDataModel.ResponseData.Audio()
            listModel.homeAudioID = "6"
            listModel.details = details
            listModel.view = "My Downloads"
            listModel.coUserId = CoUserID
            listModel.userID = USERID
            responseData.add(listModel)
            callObserverMethod(responseData)
            BWSApplication.showToast(getString(R.string.no_server_found), act)
        }
    }

    private fun setPlayPauseIcon() {
        val shared1 =
            ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, AppCompatActivity.MODE_PRIVATE)
        val AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        val MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
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

    private fun getAllCompletedMedia(
        AudioFlag: String?,
        pID: String,
        position: Int,
        listModel: List<HomeDataModel.ResponseData.SuggestedPlaylist.PlaylistSong>,
        ctx: Context,
        act: Activity
    ) {
        DB = Room.databaseBuilder(
            ctx,
            AudioDatabase::class.java,
            "Audio_database"
        )
            .addMigrations(BWSApplication.MIGRATION_1_2)
            .build()
        AudioDatabase.databaseWriteExecutor.execute {
            downloadAudioDetailsList =
                DB!!.taskDao().geAllDataBYDownloaded("Complete") as ArrayList<String>
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
                BWSApplication.showToast("The audio shall start playing after the disclaimer", act)
            } else {
                val listModelList2 =
                    arrayListOf<HomeDataModel.ResponseData.SuggestedPlaylist.PlaylistSong>()
                var view = ""
                for (i in listModel.indices) {
                    if (downloadAudioDetailsList.contains(listModel[i].name)) {
                        listModelList2.add(listModel[i])
                    }
                }
                if (position != positionSaved) {
                    if (downloadAudioDetailsList.contains(listModel[position].name)) {
                        positionSaved = position
                        BWSApplication.PlayerAudioId = listModel[position].id
                        if (listModelList2.size != 0) {
                            callPlayerSuggested(pos, "", listModelList2, ctx, act, pID, true)
                        } else {
                            BWSApplication.showToast(ctx.getString(R.string.no_server_found), act)
                        }
                    } else {
//                                pos = 0;
                        BWSApplication.showToast(ctx.getString(R.string.no_server_found), act)
                    }
                }
//                SegmentTag()
            }
        } else {
            val listModelList2 =
                arrayListOf<HomeDataModel.ResponseData.SuggestedPlaylist.PlaylistSong>()
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
                            callPlayerSuggested(pos, "", listModelList2, ctx, act, pID, audioc)
                        } else {
                            BWSApplication.showToast(ctx.getString(R.string.no_server_found), act)
                        }
                    } else if (listModelList2[pos].id.equals("0") && listModelList2.size > 1) {
                        callPlayerSuggested(pos, "", listModelList2, ctx, act, pID, audioc)
                    } else {
                        BWSApplication.showToast(ctx.getString(R.string.no_server_found), act)
                    }
                } else {
                    BWSApplication.showToast(ctx.getString(R.string.no_server_found), act)
                }
            } else {
                BWSApplication.showToast(ctx.getString(R.string.no_server_found), act)
            }
//            SegmentTag()
        }
    }

    private fun callMainPlayerSuggested(
        position: Int,
        view: String?,
        listModel: List<HomeDataModel.ResponseData.SuggestedPlaylist.PlaylistSong>,
        ctx: Context,
        act: Activity,
        playlistID: String
    ) {
        val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
        val AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        val MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
        val PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
        var playerPosition: Int = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
        val shared12 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
        val IsPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1")
        if (MyDownloads.equals("1", true)) {
            if (BWSApplication.isNetworkConnected(ctx)) {
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
                        callMyPlayer(ctx, act)
                        BWSApplication.showToast(
                            "The audio shall start playing after the disclaimer",
                            act
                        )
                    } else {
                        if (player != null) {
                            if (position != playerPosition) {
                                player.seekTo(position, 0)
                                player.playWhenReady = true
                                val sharedxx =
                                    ctx.getSharedPreferences(
                                        CONSTANTS.PREF_KEY_PLAYER,
                                        MODE_PRIVATE
                                    )
                                val editor = sharedxx.edit()
                                editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position)
                                editor.apply()
                            }
                            callMyPlayer(ctx, act)
                        } else {
                            callPlayerSuggested(
                                position,
                                view,
                                listModel,
                                ctx,
                                act,
                                playlistID,
                                true
                            )
                        }
                    }
                } else {
                    val listModelList2 =
                        arrayListOf<HomeDataModel.ResponseData.SuggestedPlaylist.PlaylistSong>()
                    listModelList2.addAll(listModel)
                    val gson = Gson()
                    val disclimerJson =
                        shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
                    val type =
                        object : TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio?>() {}.type
                    val arrayList =
                        gson.fromJson<HomeScreenModel.ResponseData.DisclaimerAudio>(
                            disclimerJson,
                            type
                        )
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
                        act,
                        playlistID,
                        audioc
                    )
                }
            } else {
                getAllCompletedMedia(AudioPlayerFlag, playlistID, position, listModel, ctx, act)
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
                    callMyPlayer(ctx, act)
                    BWSApplication.showToast(
                        "The audio shall start playing after the disclaimer",
                        act
                    )
                } else {
                    if (player != null) {
                        if (position != playerPosition) {
                            player.seekTo(position, 0)
                            player.playWhenReady = true
                            val sharedxx =
                                ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                            val editor = sharedxx.edit()
                            editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position)
                            editor.apply()
                        }
                        callMyPlayer(ctx, act)
                    } else {
                        callPlayerSuggested(position, view, listModel, ctx, act, playlistID, true)
                    }
                }
            } else {
                val listModelList2 =
                    arrayListOf<HomeDataModel.ResponseData.SuggestedPlaylist.PlaylistSong>()
                listModelList2.addAll(listModel)
                val gson = Gson()
                val disclimerJson =
                    shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
                val type =
                    object : TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio?>() {}.type
                val arrayList =
                    gson.fromJson<HomeScreenModel.ResponseData.DisclaimerAudio>(disclimerJson, type)
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
                callPlayerSuggested(position, view, listModelList2, ctx, act, playlistID, audioc)
            }
        }
    }

    private fun getPlaylistDetail(PlaylistID: String) {
        try {
            DB!!.taskDao()
                .getPlaylist1(PlaylistID)
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
        listModel: List<HomeDataModel.ResponseData.SuggestedPlaylist.PlaylistSong>,
        ctx: Context,
        act: Activity,
        playlistID: String,
        audioc: Boolean
    ) {
        if (audioc) {
            callNewPlayerRelease()
        }
        val shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
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

    class AudioAdapter(
        private val listModel: List<HomeDataModel.ResponseData.Audio>,
        private val ctx: Context,
        var binding: FragmentManageBinding,
        val act: Activity,
        var fragmentManager1: FragmentManager
    ) : RecyclerView.Adapter<AudioAdapter.MyViewHolder>() {

        inner class MyViewHolder(var binding: MainAudioLayoutBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: MainAudioLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.main_audio_layout,
                parent,
                false
            )
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvViewAll.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("ID", listModel[position].homeAudioID)
                bundle.putString("Name", listModel[position].view)
                bundle.putString("Category", "")
                val viewAllAudioFragment: Fragment =
                    ViewAllAudioFragment()
                viewAllAudioFragment.arguments = bundle
                fragmentManager1.beginTransaction()
                    .replace(R.id.flContainer, viewAllAudioFragment)
                    .commit()
            }

            if (listModel[position].details!!.isEmpty()) {
                holder.binding.llMainLayout.visibility = View.GONE
            } else {
                holder.binding.llMainLayout.visibility = View.VISIBLE
                holder.binding.tvTitle.text = listModel[position].view
                if (listModel[position].view.equals("My Downloads", ignoreCase = true)) {
                    val myDownloads: RecyclerView.LayoutManager =
                        LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
                    holder.binding.rvMainAudio.layoutManager = myDownloads
                    holder.binding.rvMainAudio.itemAnimator = DefaultItemAnimator()
                    val myDownloadsAdapter = DownloadAdapter(
                        listModel[position].details!!,
                        ctx,
                        binding,
                        act,
                        listModel[position].view
                    )
                    holder.binding.rvMainAudio.adapter = myDownloadsAdapter
                    if (listModel[position].details != null &&
                        listModel[position].details!!.size > 4
                    ) {
                        holder.binding.tvViewAll.visibility = View.VISIBLE
                    } else {
                        holder.binding.tvViewAll.visibility = View.GONE
                    }
                } else if (listModel[position].view.equals(
                        ctx.getString(R.string.Library),
                        ignoreCase = true
                    )
                ) {
                    val recommendedAdapter = LibraryAdapter(
                        listModel[position].details!!,
                        ctx,
                        binding,
                        act,
                        listModel[position].view
                    )
                    val recommended: RecyclerView.LayoutManager =
                        LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
                    holder.binding.rvMainAudio.layoutManager = recommended
                    holder.binding.rvMainAudio.itemAnimator = DefaultItemAnimator()
                    holder.binding.rvMainAudio.adapter = recommendedAdapter
                    if (listModel[position].details != null &&
                        listModel[position].details!!.size > 4
                    ) {
                        holder.binding.tvViewAll.visibility = View.VISIBLE
                    } else {
                        holder.binding.tvViewAll.visibility = View.GONE
                    }
                } else if (listModel[position].view.equals(
                        ctx.getString(R.string.my_like),
                        ignoreCase = true
                    )
                ) {
                    holder.binding.llMainLayout.visibility = View.GONE
                    /*RecentlyPlayedAdapter recentlyPlayedAdapter = new RecentlyPlayedAdapter(listModel.details!!, ctx);
                    RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
                    holder.binding.rvMainAudio.setLayoutManager(recentlyPlayed);
                    holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
                    holder.binding.rvMainAudio.setAdapter(recentlyPlayedAdapter);*/
                } else if (listModel[position].view.equals(
                        ctx.getString(R.string.recently_played),
                        ignoreCase = true
                    )
                ) {
                    val recentlyPlayedAdapter = RecentlyPlayedAdapter(
                        listModel[position].details!!,
                        ctx,
                        binding,
                        act,
                        listModel[position].view
                    )
                    val recentlyPlayed: RecyclerView.LayoutManager =
                        LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
                    holder.binding.rvMainAudio.layoutManager = recentlyPlayed
                    holder.binding.rvMainAudio.itemAnimator = DefaultItemAnimator()
                    holder.binding.rvMainAudio.adapter = recentlyPlayedAdapter
                    if (listModel[position].details!!.size > 6) {
                        holder.binding.tvViewAll.visibility = View.VISIBLE
                    } else {
                        holder.binding.tvViewAll.visibility = View.GONE
                    }
                } else if (listModel[position].view.equals(
                        ctx.getString(R.string.get_inspired),
                        ignoreCase = true
                    )
                ) {
                    val recommendedAdapter = RecommendedAdapter(
                        listModel[position].details!!,
                        ctx,
                        binding,
                        act,
                        listModel[position].view
                    )
                    val inspired: RecyclerView.LayoutManager =
                        LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
                    holder.binding.rvMainAudio.layoutManager = inspired
                    holder.binding.rvMainAudio.itemAnimator = DefaultItemAnimator()
                    holder.binding.rvMainAudio.adapter = recommendedAdapter
                    if (listModel[position].details!!.size > 4) {
                        holder.binding.tvViewAll.visibility = View.VISIBLE
                    } else {
                        holder.binding.tvViewAll.visibility = View.GONE
                    }
                } else if (listModel[position].view.equals(
                        ctx.getString(R.string.recommended_audio),
                        ignoreCase = true
                    )
                ) {
                    val recommendedAdapter = RecommendedAdapter(
                        listModel[position].details!!,
                        ctx,
                        binding,
                        act,
                        listModel[position].view
                    )
                    val inspired: RecyclerView.LayoutManager =
                        LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
                    holder.binding.rvMainAudio.layoutManager = inspired
                    holder.binding.rvMainAudio.itemAnimator = DefaultItemAnimator()
                    holder.binding.rvMainAudio.adapter = recommendedAdapter
                    if (listModel[position].details!!.size > 4) {
                        holder.binding.tvViewAll.visibility = View.VISIBLE
                    } else {
                        holder.binding.tvViewAll.visibility = View.GONE
                    }
                } else if (listModel[position].view.equals(
                        ctx.getString(R.string.popular_audio),
                        ignoreCase = true
                    )
                ) {
                    val popularPlayedAdapter = PopularPlayedAdapter(
                        listModel[position].details!!,
                        ctx,
                        binding,
                        act,
                        listModel[position].view
                    )
                    val recentlyPlayed: RecyclerView.LayoutManager =
                        LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
                    holder.binding.rvMainAudio.layoutManager = recentlyPlayed
                    holder.binding.rvMainAudio.itemAnimator = DefaultItemAnimator()
                    holder.binding.rvMainAudio.adapter = popularPlayedAdapter
                    if (listModel[position].details!!.size > 6
                    ) {
                        holder.binding.tvViewAll.visibility = View.VISIBLE
                    } else {
                        holder.binding.tvViewAll.visibility = View.GONE
                    }
                } else if (listModel[position].view.equals(
                        ctx.getString(R.string.top_categories),
                        ignoreCase = true
                    )
                ) {
                    holder.binding.tvViewAll.visibility = View.GONE
                    val topCategoriesAdapter = TopCategoriesAdapter(
                        listModel[position].details!!,
                        ctx,
                        binding,
                        act,
                        listModel[position].homeAudioID.toString(),
                        listModel[position].view,
                        fragmentManager1
                    )
                    val topCategories: RecyclerView.LayoutManager =
                        LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
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

    class PlaylistAdapter(
        private val listModel: HomeDataModel.ResponseData.Play,
        private val ctx: Context,
        var binding: FragmentManageBinding,
        val act: Activity
    ) : RecyclerView.Adapter<PlaylistAdapter.MyViewHolder>() {
        var index = -1

        inner class MyViewHolder(var binding: PlaylistCustomLayoutBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: PlaylistCustomLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.playlist_custom_layout,
                parent,
                false
            )
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val measureRatio = BWSApplication.measureRatio(ctx, 0f, 1f, 1f, 0.38f, 0f)
            holder.binding.ivRestaurantImage.layoutParams.height =
                (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width =
                (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            holder.binding.tvAddToPlaylist.layoutParams.height =
                (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.tvAddToPlaylist.layoutParams.width =
                (measureRatio.widthImg * measureRatio.ratio).toInt()
            val measureRatio1 = BWSApplication.measureRatio(ctx, 0f, 1f, 1f, 0.38f, 0f)
            holder.binding.rlMainLayout.layoutParams.height =
                (measureRatio1.height * measureRatio1.ratio).toInt()
            holder.binding.rlMainLayout.layoutParams.width =
                (measureRatio1.widthImg * measureRatio1.ratio).toInt()
            holder.binding.tvPlaylistName.text = listModel.details!![position].playlistName
            Glide.with(ctx).load(listModel.details!![position].playlistImage).thumbnail(0.05f)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(34))).priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                .into(holder.binding.ivRestaurantImage)

            if (index == position) {
                holder.binding.tvAddToPlaylist.visibility = View.VISIBLE
            } else holder.binding.tvAddToPlaylist.visibility = View.GONE

            holder.binding.tvAddToPlaylist.text = "Add To Playlist"

            holder.binding.rlMainLayout.setOnLongClickListener {
                holder.binding.tvAddToPlaylist.visibility = View.VISIBLE
                index = position
                notifyDataSetChanged()
                true
            }

            holder.binding.tvAddToPlaylist.setOnClickListener {
                val i = Intent(ctx, AddPlaylistActivity::class.java)
                i.putExtra("AudioId", "")
                i.putExtra("ScreenView", "Playlist View All Screen")
                i.putExtra("PlaylistID", listModel.details!![position].playlistID)
                i.putExtra("PlaylistName", "")
                i.putExtra("PlaylistImage", "")
                i.putExtra("PlaylistType", "")
                i.putExtra("Liked", "0")
                ctx.startActivity(i)
            }


            holder.binding.rlMainLayout.setOnClickListener {
                try {
                    val i = Intent(ctx, MyPlaylistListingActivity::class.java)
                    i.putExtra("New", "0")
                    i.putExtra("PlaylistID", listModel.details!![position].playlistID)
                    i.putExtra("PlaylistName", listModel.details!![position].playlistName)
                    i.putExtra("PlaylistImage", listModel.details!![position].playlistImage)
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
        }

        override fun getItemCount(): Int {
            return if (listModel.details!!.size < 4)
                listModel.details!!.size
            else 4
        }
    }

    class RecommendedAdapter(
        private val listModel: List<HomeDataModel.ResponseData.Audio.Detail>,
        private val ctx: Context,
        var binding: FragmentManageBinding,
        val act: Activity,
        var view: String?
    ) : RecyclerView.Adapter<RecommendedAdapter.MyViewHolder>() {
        var index = -1

        inner class MyViewHolder(var binding: BigBoxLayoutBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: BigBoxLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.big_box_layout,
                parent,
                false
            )
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = listModel[position].name
            val measureRatio = BWSApplication.measureRatio(ctx, 20f, 1f, 1f, 0.48f, 20f)
            holder.binding.ivRestaurantImage.layoutParams.height =
                (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width =
                (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.tvAddToPlaylist.layoutParams.height =
                (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.tvAddToPlaylist.layoutParams.width =
                (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY

            Glide.with(ctx).load(listModel[position].imageFile).thumbnail(0.05f)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(32))).priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                .into(holder.binding.ivRestaurantImage)

            if (index == position) {
                holder.binding.tvAddToPlaylist.visibility = View.VISIBLE
            } else holder.binding.tvAddToPlaylist.visibility = View.GONE

            holder.binding.tvAddToPlaylist.text = "Add To Playlist"

            holder.binding.llMainLayout.setOnLongClickListener {
                holder.binding.tvAddToPlaylist.visibility = View.VISIBLE
                index = position
                notifyDataSetChanged()
                true
            }

            holder.binding.tvAddToPlaylist.setOnClickListener {
                val i = Intent(ctx, AddPlaylistActivity::class.java)
                i.putExtra("AudioId", listModel[position].id)
                i.putExtra("ScreenView", "Audio View All Screen")
                i.putExtra("PlaylistID", "")
                i.putExtra("PlaylistName", "")
                i.putExtra("PlaylistImage", "")
                i.putExtra("PlaylistType", "")
                i.putExtra("Liked", "0")
                ctx.startActivity(i)
            }

            holder.binding.llMainLayout.setOnClickListener {
                ManageFragment().callMainPlayer(position, view, listModel, ctx, act)
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

    class LibraryAdapter(
        private val listModel: List<HomeDataModel.ResponseData.Audio.Detail>,
        private val ctx: Context,
        var binding: FragmentManageBinding,
        val act: Activity,
        var view: String?
    ) : RecyclerView.Adapter<LibraryAdapter.MyViewHolder>() {

        var index = -1

        inner class MyViewHolder(var binding: BigBoxLayoutBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: BigBoxLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.big_box_layout,
                parent,
                false
            )
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = listModel[position].name
            val measureRatio = BWSApplication.measureRatio(ctx, 20f, 1f, 1f, 0.48f, 20f)
            holder.binding.ivRestaurantImage.layoutParams.height =
                (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width =
                (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.tvAddToPlaylist.layoutParams.height =
                (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.tvAddToPlaylist.layoutParams.width =
                (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY

            if (index == position) {
                holder.binding.tvAddToPlaylist.visibility = View.VISIBLE
            } else holder.binding.tvAddToPlaylist.visibility = View.GONE

            holder.binding.tvAddToPlaylist.text = "Add To Playlist"

            holder.binding.llMainLayout.setOnLongClickListener {
                holder.binding.tvAddToPlaylist.visibility = View.VISIBLE
                index = position
                notifyDataSetChanged()
                true
            }

            holder.binding.tvAddToPlaylist.setOnClickListener {
                val i = Intent(ctx, AddPlaylistActivity::class.java)
                i.putExtra("AudioId", listModel[position].id)
                i.putExtra("ScreenView", "Audio View All Screen")
                i.putExtra("PlaylistID", "")
                i.putExtra("PlaylistName", "")
                i.putExtra("PlaylistImage", "")
                i.putExtra("PlaylistType", "")
                i.putExtra("Liked", "0")
                ctx.startActivity(i)
            }

            Glide.with(ctx).load(listModel[position].imageFile).thumbnail(0.05f)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(32))).priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                .into(holder.binding.ivRestaurantImage)

            holder.binding.llMainLayout.setOnClickListener {
                ManageFragment().callMainPlayer(position, view, listModel, ctx, act)
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

    class DownloadAdapter(
        private val listModel: List<HomeDataModel.ResponseData.Audio.Detail>,
        private val ctx: Context,
        var binding: FragmentManageBinding,
        val act: Activity,
        var view: String?
    ) : RecyclerView.Adapter<DownloadAdapter.MyViewHolder>() {

        inner class MyViewHolder(var binding: BigBoxLayoutBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: BigBoxLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.big_box_layout,
                parent,
                false
            )
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = listModel[position].name
            val measureRatio = BWSApplication.measureRatio(ctx, 20f, 1f, 1f, 0.48f, 20f)
            holder.binding.ivRestaurantImage.layoutParams.height =
                (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width =
                (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            Glide.with(ctx).load(listModel[position].imageFile).thumbnail(0.05f)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(32))).priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                .into(holder.binding.ivRestaurantImage)
            holder.binding.llMainLayout.setOnClickListener {
                ManageFragment().callMainPlayer(position, view, listModel, ctx, act)
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

    class RecentlyPlayedAdapter(
        private val listModel: List<HomeDataModel.ResponseData.Audio.Detail>,
        private val ctx: Context,
        var binding: FragmentManageBinding,
        val act: Activity,
        var view: String?
    ) : RecyclerView.Adapter<RecentlyPlayedAdapter.MyViewHolder>() {
        var index = -1

        inner class MyViewHolder(var binding: SmallBoxLayoutBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: SmallBoxLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.small_box_layout,
                parent,
                false
            )
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = listModel[position].name
            val measureRatio = BWSApplication.measureRatio(ctx, 16f, 1f, 1f, 0.28f, 10f)
            holder.binding.ivRestaurantImage.layoutParams.height =
                (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width =
                (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.tvAddToPlaylist.layoutParams.height =
                (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.tvAddToPlaylist.layoutParams.width =
                (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            Glide.with(ctx).load(listModel[position].imageFile).thumbnail(0.05f)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(32))).priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                .into(holder.binding.ivRestaurantImage)

            if (index == position) {
                holder.binding.tvAddToPlaylist.visibility = View.VISIBLE
            } else holder.binding.tvAddToPlaylist.visibility = View.GONE

            holder.binding.tvAddToPlaylist.text = "Add To Playlist"

            holder.binding.llMainLayout.setOnLongClickListener {
                holder.binding.tvAddToPlaylist.visibility = View.VISIBLE
                index = position
                notifyDataSetChanged()
                true
            }

            holder.binding.tvAddToPlaylist.setOnClickListener {
                val i = Intent(ctx, AddPlaylistActivity::class.java)
                i.putExtra("AudioId", listModel[position].id)
                i.putExtra("ScreenView", "Audio View All Screen")
                i.putExtra("PlaylistID", "")
                i.putExtra("PlaylistName", "")
                i.putExtra("PlaylistImage", "")
                i.putExtra("PlaylistType", "")
                i.putExtra("Liked", "0")
                ctx.startActivity(i)
            }

            holder.binding.llMainLayout.setOnClickListener {
                ManageFragment().callMainPlayer(position, view, listModel, ctx, act)
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

    class PopularPlayedAdapter(
        private val listModel: List<HomeDataModel.ResponseData.Audio.Detail>,
        private val ctx: Context,
        var binding: FragmentManageBinding,
        val act: Activity,
        var view: String?
    ) : RecyclerView.Adapter<PopularPlayedAdapter.MyViewHolder>() {
        var index = -1

        inner class MyViewHolder(var binding: SmallBoxLayoutBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: SmallBoxLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.small_box_layout,
                parent,
                false
            )
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = listModel[position].name
            val measureRatio = BWSApplication.measureRatio(ctx, 16f, 1f, 1f, 0.28f, 10f)
            holder.binding.ivRestaurantImage.layoutParams.height =
                (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width =
                (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            holder.binding.tvAddToPlaylist.layoutParams.height =
                (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.tvAddToPlaylist.layoutParams.width =
                (measureRatio.widthImg * measureRatio.ratio).toInt()
            Glide.with(ctx).load(listModel[position].imageFile).thumbnail(0.05f)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(32))).priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                .into(holder.binding.ivRestaurantImage)


            if (index == position) {
                holder.binding.tvAddToPlaylist.visibility = View.VISIBLE
            } else holder.binding.tvAddToPlaylist.visibility = View.GONE

            holder.binding.tvAddToPlaylist.text = "Add To Playlist"

            holder.binding.llMainLayout.setOnLongClickListener {
                holder.binding.tvAddToPlaylist.visibility = View.VISIBLE
                index = position
                notifyDataSetChanged()
                true
            }

            holder.binding.tvAddToPlaylist.setOnClickListener {
                val i = Intent(ctx, AddPlaylistActivity::class.java)
                i.putExtra("AudioId", listModel[position].id)
                i.putExtra("ScreenView", "Audio View All Screen")
                i.putExtra("PlaylistID", "")
                i.putExtra("PlaylistName", "")
                i.putExtra("PlaylistImage", "")
                i.putExtra("PlaylistType", "")
                i.putExtra("Liked", "0")
                ctx.startActivity(i)
            }

            holder.binding.llMainLayout.setOnClickListener {
                ManageFragment().callMainPlayer(position, view, listModel, ctx, act)
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

    class TopCategoriesAdapter(
        private val listModel: List<HomeDataModel.ResponseData.Audio.Detail>,
        private val ctx: Context,
        var binding: FragmentManageBinding,
        val act: Activity,
        private var homeView: String,
        private var viewString: String?,
        var fragmentManager1: FragmentManager
    ) : RecyclerView.Adapter<TopCategoriesAdapter.MyViewHolder>() {

        inner class MyViewHolder(var binding: RoundBoxLayoutBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: RoundBoxLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.round_box_layout,
                parent,
                false
            )
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = listModel[position].categoryName
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            Glide.with(ctx).load(listModel[position].catImage).thumbnail(0.05f)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(124))).priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                .into(holder.binding.ivRestaurantImage)

            holder.binding.llMainLayout.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("ID", homeView)
                bundle.putString("Name", viewString)
                bundle.putString("Category", listModel[position].categoryName)
                val viewAllAudioFragment: Fragment =
                    ViewAllAudioFragment()
                viewAllAudioFragment.arguments = bundle
                fragmentManager1.beginTransaction()
                    .replace(R.id.flContainer, viewAllAudioFragment)
                    .commit()
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