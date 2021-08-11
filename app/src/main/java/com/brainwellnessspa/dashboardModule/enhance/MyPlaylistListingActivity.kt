package com.brainwellnessspa.dashboardModule.enhance

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.*
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.activities.MyPlayerActivity
import com.brainwellnessspa.dashboardModule.models.HomeScreenModel
import com.brainwellnessspa.dashboardModule.models.PlaylistDetailsModel
import com.brainwellnessspa.dashboardModule.models.SucessModel
import com.brainwellnessspa.dashboardModule.models.ViewAllAudioListModel
import com.brainwellnessspa.dashboardModule.models.MainPlayModel
import com.brainwellnessspa.databinding.*
import com.brainwellnessspa.encryptDecryptUtils.DownloadMedia
import com.brainwellnessspa.encryptDecryptUtils.FileUtils
import com.brainwellnessspa.membershipModule.activities.RecommendedCategoryActivity
import com.brainwellnessspa.membershipModule.activities.SleepTimeActivity
import com.brainwellnessspa.roomDataBase.AudioDatabase
import com.brainwellnessspa.roomDataBase.DownloadAudioDetails
import com.brainwellnessspa.roomDataBase.DownloadPlaylistDetails
import com.brainwellnessspa.services.GlobalInitExoPlayer
import com.brainwellnessspa.services.GlobalInitExoPlayer.Companion.GetCurrentAudioPosition
import com.brainwellnessspa.userModule.signupLogin.SignInActivity
import com.brainwellnessspa.utility.*
import com.brainwellnessspa.utility.ItemMoveCallback.ItemTouchHelperContract
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.downloader.PRDownloader
import com.google.android.flexbox.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class MyPlaylistListingActivity : AppCompatActivity(), StartDragListener {
    lateinit var ctx: Context
    lateinit var activity: Activity
    var coUserId: String? = ""
    var userId: String? = ""
    var screenView: String? = ""
    var myCreated: String? = ""
    var new: String? = ""
    var playlistId: String? = ""
    var playlistName: String? = ""
    var playlistImage: String? = ""
    var myDownloads: String? = ""
    var fileNameList = arrayListOf<String>()
    var playlistSongsList = arrayListOf<PlaylistDetailsModel.ResponseData.PlaylistSong>()
    var onlySingleDownloaded = arrayListOf<String>()
    var playlistDownloadId = arrayListOf<String>()
    var downloadPlaylistDetails = DownloadPlaylistDetails()
    var downloadPlaylistDetailsList = arrayListOf<DownloadPlaylistDetails>()
    var downloadAudios: List<String> = java.util.ArrayList()
    var downloadAudioDetailsList = arrayListOf<DownloadAudioDetails>()
    private var downloadAudioDetailsListGlobal = ArrayList<DownloadAudioDetails>()
    lateinit var adpater: PlayListsAdpater
    lateinit var adapter2: PlayListsAdpater2
    lateinit var binding: ActivityMyPlaylistListingBinding
    var count: Int = 0
    var hundredVolume: Int = 0
    var currentVolume: Int = 0
    var maxVolume: Int = 0
    var percent: Int = 0
    lateinit var searchEditText: EditText
    var touchHelper: ItemTouchHelper? = null
    var listModelGlobal: PlaylistDetailsModel = PlaylistDetailsModel()
    var sleepTime: String? = ""
    var gson = Gson()
    var selectedCategoriesName = arrayListOf<String>()

    private val listener: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("MyData")) {
                try {
                    val data = intent.getStringExtra("MyData")
                    Log.d("play_pause_Action", data!!)
                    val sharedw: SharedPreferences = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                    val audioFlag = sharedw.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                    val pID = sharedw.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                    if (myDownloads.equals("1", ignoreCase = true)) {
                        if (audioFlag.equals("Downloadlist", ignoreCase = true) && pID.equals(playlistName, ignoreCase = true)) {/*if (data.equalsIgnoreCase("pause")) {
                            isPlayPlaylist = 1;
                            binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_icon));
//                            handler3.postDelayed(UpdateSongTime3, 500);
                            adpater2.notifyDataSetChanged();

                        } else {
                            isPlayPlaylist = 0;
                            binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_blue_play_icon));
                        }*/
                            if (myDownloads.equals("1", ignoreCase = true)) {
                                if (data.equals("play", ignoreCase = true)) {
                                    isPlayPlaylist = 1
                                    binding.llPause.visibility = View.VISIBLE
                                    binding.llPlay.visibility = View.GONE
                                } else {
                                    isPlayPlaylist = 2
                                    binding.llPause.visibility = View.GONE
                                    binding.llPlay.visibility = View.VISIBLE
                                }
                                if (player != null) {
                                    adapter2.notifyDataSetChanged()
                                }
                            }
                        } else {
                            isPlayPlaylist = 0
                            binding.llPause.visibility = View.GONE
                            binding.llPlay.visibility = View.VISIBLE
                        }
                    } else {
                        if (audioFlag.equals("playlist", ignoreCase = true) && pID.equals(playlistId, ignoreCase = true)) {
                            if (data.equals("play", ignoreCase = true)) {
                                isPlayPlaylist = 1
                                binding.llPause.visibility = View.VISIBLE
                                binding.llPlay.visibility = View.GONE
                            } else {
                                isPlayPlaylist = 2
                                binding.llPause.visibility = View.GONE
                                binding.llPlay.visibility = View.VISIBLE
                            }
                            if (player != null) {
                                if (myCreated.equals("1", ignoreCase = true)) {
                                    adpater.notifyDataSetChanged()
                                } else {
                                    adapter2.notifyDataSetChanged()
                                }
                            }
                        } else {
                            isPlayPlaylist = 0
                            binding.llPause.visibility = View.GONE
                            binding.llPlay.visibility = View.VISIBLE
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private val listener1: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("MyReminder")) {
                prepareData()
            } else if (intent.hasExtra("MyFindAudio")) {
                prepareData()
                binding.searchView.requestFocus()
                searchEditText.setText("")
                binding.searchView.setQuery("", false)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_playlist_listing)
        ctx = this@MyPlaylistListingActivity
        activity = this@MyPlaylistListingActivity
        if (intent != null) {
            new = intent.getStringExtra("New")
            playlistId = intent.getStringExtra("PlaylistID")
            playlistName = intent.getStringExtra("PlaylistName")
            myDownloads = intent.getStringExtra("MyDownloads")
            if (intent != null) {
                screenView = intent.getStringExtra("ScreenView")
                if (screenView == null) {
                    screenView = ""
                }
            }
        }
        DB = getAudioDataBase(ctx)
        callObserveMethodGetAllMedia(ctx, DB)
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        binding.tvSearch.setOnClickListener {
            val i = Intent(ctx, AddAudioActivity::class.java)
            i.putExtra("PlaylistID", listModelGlobal.responseData!!.playlistID)
            startActivity(i)
        }
        val shared1 = ctx.getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
        sleepTime = shared1.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")
        val json = shared1.getString(CONSTANTS.selectedCategoriesName, gson.toString())
        if (!json.equals(gson.toString(), ignoreCase = true)) {
            val type1 = object : com.google.common.reflect.TypeToken<java.util.ArrayList<String?>?>() {}.type
            selectedCategoriesName = gson.fromJson(json, type1)
        }
        binding.tvSleepTime.text = "Your average sleep time is \n$sleepTime"
        val layoutManager = FlexboxLayoutManager(ctx)
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.alignItems = AlignItems.STRETCH
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        binding.rvAreaOfFocusCategory.layoutManager = layoutManager
        val adapter = AreaOfFocusAdapter(binding, ctx, selectedCategoriesName)
        binding.rvAreaOfFocusCategory.adapter = adapter

        binding.llBack.setOnClickListener {
            finish()
        }

        binding.llSleepTime.setOnClickListener {
            if (isNetworkConnected(activity)) {
                val intent = Intent(applicationContext, SleepTimeActivity::class.java)
                startActivity(intent)
            } else {
                showToast(getString(R.string.no_server_found), activity)
            }
        }
        binding.ivEditCategory.setOnClickListener {
            val i = Intent(activity, RecommendedCategoryActivity::class.java)
            i.putExtra("BackClick", "1")
            startActivity(i)
        }
        binding.llDownloads.setOnClickListener {
            callObserveMethodGetAllMedia(ctx, DB)
            callDownload("", "", "", listModelGlobal.responseData!!.playlistSongs!!, 0, binding.llDownloads, binding.ivDownloads, ctx, activity, DB)
        }
        binding.searchView.onActionViewExpanded()
        searchEditText = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(ContextCompat.getColor(activity, R.color.dark_blue_gray))
        searchEditText.setHintTextColor(ContextCompat.getColor(activity, R.color.gray))
        val closeButton: ImageView = binding.searchView.findViewById(R.id.search_close_btn)
        binding.searchView.clearFocus()
        closeButton.setOnClickListener {
            binding.searchView.clearFocus()
            searchEditText.setText("")
            binding.searchView.setQuery("", false)
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(search: String): Boolean {
                binding.searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(search: String): Boolean {
                try {
                    if (adapter2 != null) {
                        adapter2.filter.filter(search) //                        SearchFlag = search
                        Log.e("searchsearch", "" + search)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                return false
            }
        })
    }

    override fun onResume() {
            val shared1 = ctx.getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
        sleepTime = shared1.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")
        val json = shared1.getString(CONSTANTS.selectedCategoriesName, gson.toString())
        if (!json.equals(gson.toString(), ignoreCase = true)) {
            val type1 = object : com.google.common.reflect.TypeToken<java.util.ArrayList<String?>?>() {}.type
            selectedCategoriesName = gson.fromJson(json, type1)
        }
        binding.tvSleepTime.text = "Your average sleep time is \n$sleepTime"
        val layoutManager = FlexboxLayoutManager(ctx)
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.alignItems = AlignItems.STRETCH
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        binding.rvAreaOfFocusCategory.layoutManager = layoutManager
        val adapter = AreaOfFocusAdapter(binding, ctx, selectedCategoriesName)
        binding.rvAreaOfFocusCategory.adapter = adapter
        binding.searchView.clearFocus()
        searchEditText.setText("")
        binding.searchView.setQuery("", false)
        prepareData()
        super.onResume()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(ctx).unregisterReceiver(listener)
        LocalBroadcastManager.getInstance(ctx).unregisterReceiver(listener1)
        super.onDestroy()
    }

    private fun prepareData() {
        val gson = Gson()
        val shared1x = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
        val audioPlayerFlagx = shared1x.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        val playerPositionx = shared1x.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
        val json = shared1x.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toString())
        var mainPlayModelList: ArrayList<MainPlayModel> = arrayListOf()
        if (!audioPlayerFlagx.equals("0")) {
            if (!json.equals(gson.toString(), ignoreCase = true)) {
                val type = object : TypeToken<ArrayList<MainPlayModel>>() {}.type
                mainPlayModelList = gson.fromJson(json, type)
            }
            PlayerAudioId = mainPlayModelList[playerPositionx].id
        }
        if (isNetworkConnected(this)) {
            if (!myDownloads.equals("1", true)) {
                showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                val listCall: Call<PlaylistDetailsModel> = APINewClient.client.getPlaylistDetail(coUserId, playlistId)
                listCall.enqueue(object : Callback<PlaylistDetailsModel> {
                    override fun onResponse(call: Call<PlaylistDetailsModel>, response: Response<PlaylistDetailsModel>) {
                        hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel = response.body()!!

                        listModelGlobal = response.body()!!
                        when {
                            listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true) -> {
                                LocalBroadcastManager.getInstance(ctx).registerReceiver(listener1, IntentFilter("Reminder"))

                                try { //                            if (listModel.responseData.getIsReminder().equals("0", ignoreCase = true) ||
                                    //                                    listModel.responseData.getIsReminder().equals("", ignoreCase = true)) {
                                    //                                binding.ivReminder.setColorFilter(ContextCompat.getColor(ctx, R.color.white), PorterDuff.Mode.SRC_IN)
                                    //                            } else if (listModel.responseData.getIsReminder().equals("1", ignoreCase = true)) {
                                    //                                binding.ivReminder.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), PorterDuff.Mode.SRC_IN)
                                    //                            }
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }

                                var p = Properties()
                                p.putValue("playlistId", listModel.responseData!!.playlistID)
                                p.putValue("playlistName", listModel.responseData.playlistName)
                                p.putValue("playlistDescription", listModel.responseData.playlistDesc)
                                if (listModel.responseData.created.equals("1", ignoreCase = true)) {
                                    p.putValue("playlistType", "Created")
                                } else if (listModel.responseData.created.equals("0")) {
                                    p.putValue("playlistType", "Default")
                                } else if (listModel.responseData.created == "2") {
                                    p.putValue("playlistType", "Suggested")
                                }

                                if (listModel.responseData.totalhour == "") {
                                    p.putValue("playlistDuration", "0h " + listModel.responseData.totalminute + "m")
                                } else if (listModel.responseData.totalminute == "") {
                                    p.putValue("playlistDuration", listModel.responseData.totalhour + "h 0m")
                                } else {
                                    p.putValue("playlistDuration", listModel.responseData.totalhour + "h " + listModel.responseData.totalminute + "m")
                                }
                                p.putValue("audioCount", listModel.responseData.totalAudio)
                                p.putValue("source", screenView)
                                addToSegment("Playlist Viewed", p, CONSTANTS.screen)
                                binding.tvTag.visibility = View.VISIBLE
                                binding.tvTag.setText(R.string.Audios_in_Playlist)

                                getDownloadData(ctx, DB)
                                callObserveMethodGetAllMedia(ctx, DB)
                                downloadPlaylistDetailsList = getPlaylistDetail(listModel.responseData.playlistSongs!!.size, ctx, DB)

                                if (listModel.responseData.isReminder.equals("0", ignoreCase = true) || listModel.responseData.isReminder.equals("", ignoreCase = true)) {
                                    binding.tvReminder.text = ctx.getText(R.string.set_reminder)
                                    binding.llReminder.setBackgroundResource(R.drawable.rounded_extra_theme_corner)
                                } else if (listModel.responseData.isReminder.equals("1", ignoreCase = true)) {
                                    binding.tvReminder.text = ctx.getText(R.string.update_reminder)
                                    binding.llReminder.setBackgroundResource(R.drawable.rounded_extra_dark_theme_corner)
                                } else if (listModel.responseData.isReminder.equals("2", ignoreCase = true)) {
                                    binding.tvReminder.text = ctx.getText(R.string.update_reminder)
                                    binding.llReminder.setBackgroundResource(R.drawable.rounded_extra_theme_corner)
                                }

                                binding.llReminder.setOnClickListener {
                                    if (isNetworkConnected(ctx)) {
                                        p = Properties()
                                        p.putValue("playlistId", listModel.responseData.playlistID)
                                        p.putValue("playlistName", listModel.responseData.playlistName)
                                        p.putValue("playlistDescription", listModel.responseData.playlistDesc)
                                        when (listModel.responseData.created) {
                                            "1" -> {
                                                p.putValue("playlistType", "Created")
                                            }
                                            "0" -> {
                                                p.putValue("playlistType", "Default")
                                            }
                                            "2" -> {
                                                p.putValue("playlistType", "suggested")
                                            }
                                        }
                                        when {
                                            listModel.responseData.totalhour.equals("") -> {
                                                p.putValue("playlistDuration", "0h " + listModel.responseData.totalminute + "m")
                                            }
                                            listModel.responseData.totalminute.equals("") -> {
                                                p.putValue("playlistDuration", listModel.responseData.totalhour + "h 0m")
                                            }
                                            else -> {
                                                p.putValue("playlistDuration", listModel.responseData.totalhour + "h " + listModel.responseData.totalminute + "m")
                                            }
                                        }
                                        p.putValue("audioCount", listModel.responseData.totalAudio)
                                        p.putValue("source", screenView)
                                        p.putValue("playerType", "Mini")
                                        addToSegment("Playlist Reminder Clicked", p, CONSTANTS.track)
                                        if (listModel.responseData.isReminder.equals("0", ignoreCase = true) || listModel.responseData.isReminder.equals("", ignoreCase = true)) {
                                            binding.tvReminder.text = ctx.getText(R.string.set_reminder)
                                            binding.llReminder.setBackgroundResource(R.drawable.rounded_extra_theme_corner)
                                        } else if (listModel.responseData.isReminder.equals("1", ignoreCase = true)) {
                                            binding.tvReminder.text = ctx.getText(R.string.update_reminder)
                                            binding.llReminder.setBackgroundResource(R.drawable.rounded_extra_dark_theme_corner)
                                        } else if (listModel.responseData.isReminder.equals("2", ignoreCase = true)) {
                                            binding.tvReminder.text = ctx.getText(R.string.update_reminder)
                                            binding.llReminder.setBackgroundResource(R.drawable.rounded_extra_theme_corner)
                                        }
                                        getReminderDay(ctx, activity, coUserId, listModel.responseData.playlistID, listModel.responseData.playlistName, activity as FragmentActivity?, listModel.responseData.reminderTime, listModel.responseData.reminderDay, "0", listModel.responseData.reminderId, listModel.responseData.isReminder,listModel.responseData.created)
                                    } else {
                                        showToast(getString(R.string.no_server_found), activity)
                                    }
                                }

                                //                            getMedia();
                                //                            getMediaByPer(PlaylistId, SongListSize);
                                binding.rlSearch.visibility = View.VISIBLE
                                binding.llMore.visibility = View.VISIBLE
                                binding.llReminder.visibility = View.VISIBLE
                                binding.llMore.setOnClickListener {
                                    if (isNetworkConnected(ctx)) { //            handler2.removeCallbacks(UpdateSongTime2);
                                        val fragmentManager1: FragmentManager = (ctx as FragmentActivity).supportFragmentManager

                                        callPlaylistDetails(ctx, activity, coUserId, playlistId, playlistName, fragmentManager1, screenView)
                                    } else {
                                        showToast(getString(R.string.no_server_found), activity)
                                    }
                                }
                                downloadPlaylistDetails = DownloadPlaylistDetails()
                                downloadPlaylistDetails.PlaylistID = listModel.responseData.playlistID
                                downloadPlaylistDetails.PlaylistName = listModel.responseData.playlistName
                                downloadPlaylistDetails.PlaylistDesc = listModel.responseData.playlistDesc //                    downloadPlaylistDetails.isReminder = listModel.responseData!!.gsReminder
                                downloadPlaylistDetails.PlaylistMastercat = listModel.responseData.playlistMastercat
                                downloadPlaylistDetails.PlaylistSubcat = listModel.responseData.playlistSubcat
                                downloadPlaylistDetails.PlaylistImage = listModel.responseData.playlistImage
                                downloadPlaylistDetails.PlaylistImageDetails = listModel.responseData.playlistImageDetail
                                downloadPlaylistDetails.TotalAudio = listModel.responseData.totalAudio
                                downloadPlaylistDetails.TotalDuration = listModel.responseData.totalDuration
                                downloadPlaylistDetails.Totalhour = listModel.responseData.totalhour
                                downloadPlaylistDetails.Totalminute = listModel.responseData.totalminute
                                downloadPlaylistDetails.Created = listModel.responseData.created

                                setData(listModel.responseData)
                            }
                            listModel.responseCode.equals(getString(R.string.ResponseCodeDeleted), ignoreCase = true) -> {
                                deleteCall(activity)
                                showToast(listModel.responseMessage, activity)
                                val i = Intent(activity, SignInActivity::class.java)
                                i.putExtra("mobileNo", "")
                                i.putExtra("countryCode", "")
                                i.putExtra("name", "")
                                i.putExtra("email", "")
                                i.putExtra("countryShortName", "")
                                startActivity(i)
                                finish()
                            }
                            else -> {
                                showToast(listModel.responseMessage, activity)
                            }
                        }
                    }

                    override fun onFailure(call: Call<PlaylistDetailsModel>, t: Throwable) {
                        hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                    }
                })
            } else {
                getPlaylistDetail2(ctx, DB, coUserId!!, playlistId!!)
            }
        } else {
            getPlaylistDetail2(ctx, DB, coUserId!!, playlistId!!)
            showToast(getString(R.string.no_server_found), activity)
        }
        val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
        val audioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        val myPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
        val playFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
        val playerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
        if (myDownloads.equals("1", ignoreCase = true)) {
            if (audioPlayerFlag.equals("Downloadlist", ignoreCase = true) && myPlaylist.equals(playlistId, ignoreCase = true)) {
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
            if (audioPlayerFlag.equals("playlist", ignoreCase = true) && myPlaylist.equals(playlistId, ignoreCase = true)) {
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

    private fun setData(listModel: PlaylistDetailsModel.ResponseData?) {
        myCreated = listModel!!.created
        val measureRatio4 = measureRatio(ctx, 0f, 5f, 4.1f, 1f, 0f)
        binding.ivBanner.layoutParams.height = (measureRatio4.height * measureRatio4.ratio).toInt()
        binding.ivBanner.layoutParams.width = (measureRatio4.widthImg * measureRatio4.ratio).toInt()

        val measureRatio = measureRatio(ctx, 0f, 5f, 4.1f, 1f, 0f)
        binding.ivCloudBanner.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
        binding.ivCloudBanner.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()

        val measureRatio1 = measureRatio(ctx, 0f, 5f, 4.1f, 1f, 0f)
        binding.llPlayer.layoutParams.height = (measureRatio1.height * measureRatio1.ratio).toInt()
        binding.llPlayer.layoutParams.width = (measureRatio1.widthImg * measureRatio1.ratio).toInt()

        val measureRatio2 = measureRatio(ctx, 0f, 5f, 4.1f, 1f, 0f)
        binding.ivTransBanner.layoutParams.height = (measureRatio2.height * measureRatio2.ratio).toInt()
        binding.ivTransBanner.layoutParams.width = (measureRatio2.widthImg * measureRatio2.ratio).toInt()
        binding.ivTransBanner.setImageResource(R.drawable.rounded_light_app_theme)
        if (listModel.playlistName.equals("", ignoreCase = true) || listModel.playlistName == null) {
            binding.tvPlayListName.text = R.string.My_Playlist.toString()
        } else {
            binding.tvPlayListName.text = listModel.playlistName
        }
        binding.tvDescription.text = listModel.playlistDesc
        try { //            if(!MyDownloads.equals("1")) {
            if (isNetworkConnected(ctx)) {
                if (listModel.created != "2") {
                    if (listModel.playlistImageDetail != "") {
                        Glide.with(ctx).load(listModel.playlistImageDetail).thumbnail(0.05f).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivBanner)
                        binding.ivTransBanner.setImageResource(R.drawable.rounded_light_app_theme)
                    } else {
                        binding.ivCloudBanner.setImageResource(R.drawable.ic_cloud_bg)
                        binding.ivTransBanner.setImageResource(R.drawable.rounded_dark_app_theme)
                    }
                } else {
                    binding.ivCloudBanner.setImageResource(R.drawable.ic_cloud_bg)
                    binding.ivTransBanner.setImageResource(R.drawable.rounded_dark_app_theme)
                }
            } else {
                binding.ivCloudBanner.setImageResource(R.drawable.ic_cloud_bg)
                binding.ivTransBanner.setImageResource(R.drawable.rounded_dark_app_theme)
            } //            }
        } catch (e: Exception) {
            e.printStackTrace()
        } //        binding.tvPlaylist.setText("Playlist")

        //        if (listModel.totalAudio.equals("", ignoreCase = true) ||
        //                (listModel.totalAudio.equals("0", ignoreCase = true) &&
        //                        listModel.totalhour.equals("", ignoreCase = true)
        //                        && listModel.totalminute.equals("", ignoreCase = true))) {
        //            binding.tvLibraryDetail.setText("0 Audio | 0h 0m")
        //        } else {
        //            if (listModel.totalminute.equals("", ignoreCase = true)) {
        //                binding.tvLibraryDetail.setText(listModel.totalAudio + " Audio | "
        //                        + listModel.totalhour + "h 0m")
        //            } else {
        //                binding.tvLibraryDetail.setText(listModel.totalAudio + " Audio | "
        //                        + listModel.totalhour + "h " + listModel.totalminute + "m")
        //            }
        //        }
        binding.rvPlayLists1.layoutManager = LinearLayoutManager(ctx)
        binding.rvPlayLists2.layoutManager = LinearLayoutManager(ctx)

        binding.llReminder.visibility = View.INVISIBLE
        binding.llDownloads.visibility = View.INVISIBLE
        binding.btnAddAudio.setOnClickListener {
            val i = Intent(ctx, AddAudioActivity::class.java)
            i.putExtra("PlaylistID", listModel.playlistID)
            startActivity(i)
        }

        if (listModel.playlistSongs != null) {
            if (listModel.playlistSongs!!.isEmpty()) {
                binding.llAddAudio.visibility = View.VISIBLE
                binding.rvPlayLists1.visibility = View.GONE
                binding.tvReminder.setTextColor(ContextCompat.getColor(activity, R.color.light_gray))
                binding.llReminder.isEnabled = false
                binding.llReminder.isClickable = false
                binding.llPlay.isEnabled = false
                binding.llPlay.isClickable = false
                binding.llPause.isEnabled = false
                binding.llPause.isClickable = false
                binding.llPlayPause.isEnabled = false
                binding.llPlayPause.isClickable = false
                binding.rlSearch.visibility = View.GONE
                binding.tvTag.visibility = View.GONE
                binding.llDownloads.visibility = View.VISIBLE
                binding.llReminder.visibility = View.VISIBLE //                binding.llPlayPause.setVisibility(View.INVISIBLE)
                //                binding.llListing.setVisibility(View.GONE)

                binding.llPlayPause.visibility = View.VISIBLE
            } else {
                binding.llAddAudio.visibility = View.GONE
                binding.rlSearch.visibility = View.VISIBLE
                binding.tvReminder.setTextColor(ContextCompat.getColor(activity, R.color.white))
                binding.llReminder.isEnabled = true
                binding.llReminder.isClickable = true
                binding.tvTag.visibility = View.VISIBLE
                binding.llDownloads.visibility = View.VISIBLE
                binding.llReminder.visibility = View.VISIBLE
                if (myDownloads.equals("1", ignoreCase = true)) { //                    binding.llDelete.setVisibility(View.VISIBLE)
                    searchEditText.hint = "Search for audio"
                    binding.tvSearch.hint = "Search for audio"
                    binding.llReminder.visibility = View.INVISIBLE
                    binding.llDelete.visibility = View.VISIBLE
                    binding.llMore.visibility = View.GONE
                    binding.llDownloads.visibility = View.GONE
                    binding.rlSearch.visibility = View.VISIBLE
                    adapter2 = PlayListsAdpater2(listModel.playlistSongs!!, ctx, coUserId, listModel.created, binding, activity, playlistId, playlistName, myDownloads,listModel)
                    binding.rvPlayLists2.adapter = adapter2
                    binding.rvPlayLists1.visibility = View.GONE
                    binding.rvPlayLists2.visibility = View.VISIBLE
                    binding.ivDownloads.setImageResource(R.drawable.ic_download_done_icon)
                    binding.ivDownloads.setColorFilter(ContextCompat.getColor(activity, R.color.white), PorterDuff.Mode.SRC_IN)
                    enableDisableDownload(false, "orange") //                    binding.ivReminder.setColorFilter(activity.resources.getColor(R.color.gray), PorterDuff.Mode.SRC_IN)
                } else {
                    when {
                        listModel.created.equals("1", ignoreCase = true) -> {
                            binding.llSuggested.visibility = View.GONE
                            searchEditText.setHint(R.string.playlist_or_audio_search)
                            binding.tvSearch.setHint(R.string.playlist_or_audio_search)
                            binding.tvSearch.visibility = View.VISIBLE
                            binding.searchView.visibility = View.GONE
                            binding.llDelete.visibility = View.GONE
                            binding.rvPlayLists1.visibility = View.VISIBLE
                            binding.rvPlayLists2.visibility = View.GONE
                            adpater = PlayListsAdpater(listModel.playlistSongs!!, ctx, coUserId, listModel.created, binding, activity, this, playlistId, playlistName, listModel)
                            val callback: ItemTouchHelper.Callback = ItemMoveCallback(adpater)
                            touchHelper = ItemTouchHelper(callback)
                            touchHelper!!.attachToRecyclerView(binding.rvPlayLists1)
                            binding.rvPlayLists1.adapter = adpater //                                LocalBroadcastManager.getInstance(ctx)
                            //                                        .registerReceiver(listener1, IntentFilter("DownloadProgress"))
                        }
                        listModel.created == "2" -> {
                            binding.llSuggested.visibility = View.VISIBLE

                            searchEditText.hint = "Search for audio"
                            binding.tvSearch.hint = "Search for audio"
                            binding.tvSearch.visibility = View.GONE
                            binding.llDelete.visibility = View.GONE
                            binding.searchView.visibility = View.VISIBLE
                            adapter2 = PlayListsAdpater2(listModel.playlistSongs!!, ctx, coUserId, listModel.created, binding, activity, playlistId, playlistName, myDownloads, listModel)
                            binding.rvPlayLists1.visibility = View.GONE
                            binding.rvPlayLists2.visibility = View.VISIBLE
                            binding.rvPlayLists2.adapter = adapter2
                        }
                        else -> {
                            binding.llSuggested.visibility = View.GONE
                            searchEditText.hint = "Search for audio"
                            binding.tvSearch.hint = "Search for audio"
                            binding.tvSearch.visibility = View.GONE
                            binding.searchView.visibility = View.VISIBLE
                            binding.llDelete.visibility = View.GONE
                            adapter2 = PlayListsAdpater2(listModel.playlistSongs!!, ctx, coUserId, listModel.created, binding, activity, playlistId, playlistName, myDownloads, listModel)
                            binding.rvPlayLists1.visibility = View.GONE
                            binding.rvPlayLists2.visibility = View.VISIBLE
                            binding.rvPlayLists2.adapter = adapter2
                        }
                    }
                } //                } catch (e: java.lang.Exception) {
                //                    e.printStackTrace()
                //                }
                LocalBroadcastManager.getInstance(ctx).registerReceiver(listener, IntentFilter("play_pause_Action"))
            }
        }
    }

    override fun requestDrag(viewHolder: RecyclerView.ViewHolder?) {
        touchHelper!!.startDrag(viewHolder!!)
    }

    class AreaOfFocusAdapter(var binding: ActivityMyPlaylistListingBinding, var ctx: Context, var selectedCategoriesName: java.util.ArrayList<String>) : RecyclerView.Adapter<AreaOfFocusAdapter.MyViewHolder>() {

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

    class PlayListsAdpater(var listModel: ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>, var ctx: Context, var coUserId: String?, var created: String?, var binding: ActivityMyPlaylistListingBinding, var activity: Activity, var startDragListener: StartDragListener, var PlaylistID: String?, var PlaylistName: String?, var listModel1: PlaylistDetailsModel.ResponseData) : RecyclerView.Adapter<PlayListsAdpater.MyViewHolder>(), ItemTouchHelperContract {

        private var changedAudio = arrayListOf<String>()

        inner class MyViewHolder(var binding: MyplaylistSortingNewBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: MyplaylistSortingNewBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.myplaylist_sorting_new, parent, false)
            return MyViewHolder(v)
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

            holder.binding.tvTitle.text = listModel[position].name
            holder.binding.tvTime.text = listModel[position].audioDuration
            val measureRatio = measureRatio(ctx, 0f, 1f, 1f, 0.13f, 0f)
            holder.binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            holder.binding.ivBackgroundImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivBackgroundImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivBackgroundImage.scaleType = ImageView.ScaleType.FIT_XY

            holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg)

            val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val audioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
            val myPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
            if (audioPlayerFlag.equals("playlist", ignoreCase = true) && myPlaylist.equals(PlaylistID, ignoreCase = true)) {
                if (PlayerAudioId.equals(listModel[position].id, ignoreCase = true)) {
                    if (player != null) {
                        if (!player.playWhenReady) {
                            holder.binding.equalizerview.pause()
                        } else holder.binding.equalizerview.resume(true)
                    } else holder.binding.equalizerview.stop(true)
                    holder.binding.equalizerview.visibility = View.VISIBLE
                    holder.binding.llMainLayout.setBackgroundResource(R.color.highlight_background)
                    holder.binding.ivBackgroundImage.visibility = View.VISIBLE
                    holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg)
                } else {
                    holder.binding.equalizerview.visibility = View.GONE
                    holder.binding.llMainLayout.setBackgroundResource(R.color.white)
                    holder.binding.ivBackgroundImage.visibility = View.GONE
                } //                    handler3.postDelayed(UpdateSongTime3, 500);
            } else {
                holder.binding.equalizerview.visibility = View.GONE
                holder.binding.llMainLayout.setBackgroundResource(R.color.white)
                holder.binding.ivBackgroundImage.visibility = View.GONE //                    handler3.removeCallbacks(UpdateSongTime3);
            }

            Glide.with(ctx).load(listModel[position].imageFile).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)

            holder.binding.llMainLayout.setOnClickListener {
                try {
                    MyPlaylistListingActivity().callMainPlayer(holder.absoluteAdapterPosition, "Created", listModel, ctx, activity, listModel[0].playlistID, PlaylistName!!, created, "0")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding.llPlayPause.setOnClickListener { //                MyPlaylistListingActivity().callMainPlayer(position, "", listModel, ctx,  )
                when (isPlayPlaylist) {
                    1 -> {
                        player.playWhenReady = false
                        isPlayPlaylist = 2
                        binding.llPlay.visibility = View.VISIBLE
                        binding.llPause.visibility = View.GONE
                    }
                    2 -> {
                        if (player != null) {
                            if (PlayerAudioId.equals(listModel[listModel.size - 1].id, ignoreCase = true) && player.duration - player.currentPosition <= 20) {
                                val shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                                val editor = shared.edit()
                                editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                                editor.apply()
                                player.seekTo(0, 0)
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
                        MyPlaylistListingActivity().callMainPlayer(0, "Created", listModel, ctx, activity, listModel[0].playlistID, PlaylistName!!, created, "0")
                        binding.llPlay.visibility = View.GONE
                        binding.llPause.visibility = View.VISIBLE
                    }
                }
                notifyDataSetChanged()
            } //            Glide.with(ctx).load(R.drawable.ic_image_bg).thumbnail(0.05f)
            //                    .apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH)
            //                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivBackgroundImage)
            try {
                holder.binding.llRemove.setOnClickListener {
                    val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                    val audioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                    val myPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                    if (audioPlayerFlag.equals("playlist", ignoreCase = true) && myPlaylist.equals(PlaylistID, ignoreCase = true)) {
                        if (isDisclaimer == 1) {
                            showToast("The audio shall remove after the disclaimer", activity)
                        } else {
                            if (audioPlayerFlag.equals("playlist", ignoreCase = true) && myPlaylist.equals(PlaylistID, ignoreCase = true) && listModel.size == 1) {
                                showToast("Currently you play this playlist, you can't remove last audio", activity)
                            } else {
                                callRemove(listModel[holder.absoluteAdapterPosition].id, listModel, holder.absoluteAdapterPosition, ctx, activity, PlaylistID.toString(), listModel1)
                            }
                        }
                    } else {
                        if (audioPlayerFlag.equals("playlist", ignoreCase = true) && myPlaylist.equals(PlaylistID, ignoreCase = true) && listModel.size == 1) {
                            showToast("Currently you play this playlist, you can't remove last audio", activity)
                        } else {
                            callRemove(listModel[position].id, listModel, holder.absoluteAdapterPosition, ctx, activity, PlaylistID.toString(), listModel1)
                        }
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            holder.binding.llSort.setOnTouchListener { _, event ->
                if (event.action === MotionEvent.ACTION_DOWN) {
                    startDragListener.requestDrag(holder)
                }
                if (event.action === MotionEvent.ACTION_UP) {
                    startDragListener.requestDrag(holder)
                }
                false
            }
        }

        private fun callRemove(id: String, listModel: ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>, position: Int, ctx: Context, activity: Activity, PlaylistID: String, listModelMain: PlaylistDetailsModel.ResponseData) {
            val coUserId: String?
            val shared = this.ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
            coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
            if (isNetworkConnected(this.ctx)) {
                showProgressBar(binding.progressBar, binding.progressBarHolder, this.activity)
                val listCall = APINewClient.client.removeAudio(coUserId, id, PlaylistID)
                listCall.enqueue(object : Callback<SucessModel?> {
                    override fun onResponse(call: Call<SucessModel?>, response: Response<SucessModel?>) { //                        try {
                        val listModel1: SucessModel = response.body()!!
                        if (listModel1.responseCode.equals(ctx.getString(R.string.ResponseCodesuccess), ignoreCase = true)) {

                            ////                            handler2.removeCallbacks(UpdateSongTime2);
                            //                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder,activity)
                            val listModel1: SucessModel = response.body()!!
                            hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                            val p = Properties()
                            p.putValue("audioId", listModel[position].id)
                            p.putValue("audioName", listModel[position].name)
                            p.putValue("audioDescription", "")
                            p.putValue("directions", listModel[position].audioDirection)
                            p.putValue("masterCategory", listModel[position].audiomastercat)
                            p.putValue("subCategory", listModel[position].audioSubCategory)
                            p.putValue("audioDuration", listModel[position].audioDuration)
                            p.putValue("position", GetCurrentAudioPosition())
                            p.putValue("audioType", "Streaming")
                            p.putValue("source", "Created Playlist")
                            p.putValue("audioService", appStatus(ctx))
                            p.putValue("bitRate", "")
                            p.putValue("playlistId", listModel[position].playlistID)
                            p.putValue("playlistName", PlaylistName)
                            when {
                                created.equals("1") -> p.putValue("playlistType", "Created")
                                created.equals("0") -> p.putValue("playlistType", "Default")
                                created.equals("2") -> p.putValue("playlistType", "Suggested")
                            }
                            when {
                                listModelMain.totalhour == "" -> {
                                    p.putValue("playlistDuration", "0h " + listModelMain.totalminute + "m")
                                }
                                listModelMain.totalminute == "" -> {
                                    p.putValue("playlistDuration", listModelMain.totalhour + "h 0m")
                                }
                                else -> {
                                    p.putValue("playlistDuration", listModelMain.totalhour + "h " + listModelMain.totalminute + "m")
                                }
                            }
                            p.putValue("playlistDuration", "")
                            p.putValue("sound", hundredVolume.toString())
                            addToSegment("Audio Removed From Playlist", p, CONSTANTS.track)
                            listModel.removeAt(position)
                            if (listModel.isEmpty()) {
                                try {
                                    MyPlaylistListingActivity().enableDisableDownload(false, "gray")
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                            val audioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                            val myPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                            val playFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
                            var playerPosition: Int = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                            if (audioPlayerFlag.equals("playlist", ignoreCase = true) && myPlaylist.equals(listModel[position].playlistID, ignoreCase = true)) {

                                if (player != null) {
                                    player.removeMediaItem(position)
                                }
                                if (playerPosition == position && position < listModel.size - 1) { //                                            pos = pos + 1;
                                    if (isDisclaimer == 1) { //                                    BWSApplication.showToast("The audio shall remove after the disclaimer", getActivity());
                                    } else {
                                        if (player != null) { //                                            player.seekTo(pos, 0);
                                            player.playWhenReady = true
                                            saveToPref(playerPosition, listModel)
                                        } else {
                                            MyPlaylistListingActivity().callMainPlayer(playerPosition, "Created", listModel, ctx, activity, listModel[0].playlistID, PlaylistName!!, created, "0")
                                        }
                                    }
                                } else if (playerPosition == position && position == listModel.size - 1) {
                                    playerPosition = 0
                                    if (isDisclaimer == 1) { //                                    BWSApplication.showToast("The audio shall remove after the disclaimer", getActivity());
                                    } else {
                                        if (player != null) { //                                            player.seekTo(pos, 0);
                                            player.playWhenReady = true
                                            saveToPref(playerPosition, listModel)
                                        } else {
                                            MyPlaylistListingActivity().callMainPlayer(playerPosition, "Created", listModel, ctx, activity, listModel[0].playlistID, PlaylistName!!, created, "0")
                                        }
                                    }
                                } else if (playerPosition < position && playerPosition < listModel.size - 1) {
                                    saveToPref(playerPosition, listModel)
                                } else if (playerPosition < position && playerPosition == listModel.size - 1) {
                                    saveToPref(playerPosition, listModel)
                                } else if (playerPosition > position && playerPosition == listModel.size) {
                                    playerPosition -= 1
                                    saveToPref(playerPosition, listModel)
                                }
                            }
                            localIntent = Intent("Reminder")
                            localBroadcastManager = LocalBroadcastManager.getInstance(ctx)
                            localIntent.putExtra("MyReminder", "update")
                            localBroadcastManager.sendBroadcast(localIntent)
                            showToast(listModel1.responseMessage, activity)
                        } else if (listModel1.responseCode.equals(ctx.getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
                            deleteCall(activity)
                            showToast(listModel1.responseMessage, activity)
                            val i = Intent(activity, SignInActivity::class.java)
                            i.putExtra("mobileNo", "")
                            i.putExtra("countryCode", "")
                            i.putExtra("name", "")
                            i.putExtra("email", "")
                            i.putExtra("countryShortName", "")
                            activity.startActivity(i)
                            activity.finish()
                        }
                    }

                    private fun saveToPref(playerPosition: Int, listModel: ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>) {
                        val shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                        val editor = shared.edit()
                        val gson = Gson()
                        val json = gson.toJson(listModel)
                        editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json)
                        editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, playerPosition)
                        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, PlaylistID)
                        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, PlaylistName)
                        editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "Created")
                        editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "playlist")
                        editor.apply()
                    }

                    //                    private fun saveToPref(pos: Int, mData: ArrayList<playlistModel.ResponseData.PlaylistSong>) {
                    //                        val shareddd: SharedPreferences = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                    //                        val editor = shareddd.edit()
                    //                        val gson = Gson()
                    //                        val json = gson.toJson(mData)
                    //                        editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json)
                    //                        editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, pos)
                    //                        editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false)
                    //                        editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true)
                    //                        editor.putString(CONSTANTS.PREF_KEY_PlaylistId, PlaylistID)
                    //                        editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "myPlaylist")
                    //                        editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "playlist")
                    //                        editor.commit()
                    //                        callAddTransFrag()
                    //                    }

                    override fun onFailure(call: Call<SucessModel?>, t: Throwable) {
                        hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                    }
                })
            } else {
                showToast(this.ctx.getString(R.string.no_server_found), activity)
            }
        }

        override fun getItemCount(): Int {
            return listModel.size
        }

        override fun onRowMoved(fromPosition: Int, toPosition: Int) {
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(listModel, i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    Collections.swap(listModel, i, i - 1)
                }
            }
            changedAudio.clear()
            for (i in listModel.indices) {
                changedAudio.add(listModel[i].id)
            }
            callDragApi()
            notifyItemMoved(fromPosition, toPosition)

            val p = Properties()
            p.putValue("playlistId", PlaylistID)
            p.putValue("playlistName", PlaylistName)
            p.putValue("playlistDescription", "")
            when (listModel1.created) {
                "1" -> {
                    p.putValue("playlistType", "Created")
                }
                "0" -> {
                    p.putValue("playlistType", "Default")
                }
                "2" -> p.putValue("playlistType", "Suggested")
            }

            when {
                listModel1.totalhour.equals("", ignoreCase = true) -> {
                    p.putValue("playlistDuration", "0h " + listModel1.totalminute + "m")
                }
                listModel1.totalminute.equals("", ignoreCase = true) -> {
                    p.putValue("playlistDuration", listModel1.totalhour + "h 0m")
                }
                else -> {
                    p.putValue("playlistDuration", listModel1.totalhour + "h " + listModel1.totalminute + "m")
                }
            }
            p.putValue("audioCount", listModel1.totalAudio)
            p.putValue("source", "Your Created")
            p.putValue("playerType", "Mini")
            p.putValue("audioService", appStatus(ctx))
            p.putValue("sound", hundredVolume.toString())
            p.putValue("audioId", listModel[toPosition].id)
            p.putValue("audioName", listModel[toPosition].name)
            p.putValue("masterCategory", listModel[toPosition].audiomastercat)
            p.putValue("subCategory", listModel[toPosition].audioSubCategory)
            p.putValue("audioSortPosition", fromPosition)
            p.putValue("audioSortPositionNew", toPosition)
            addToSegment("Playlist Audio Sorted", p, CONSTANTS.track)
            val shared: SharedPreferences = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val audioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
            var pos = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
            if (audioFlag.equals("playList", ignoreCase = true)) {
                val pID = shared.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "0")
                if (pID.equals(PlaylistID, ignoreCase = true)) {
                    if (fromPosition == pos) {
                        pos = toPosition
                        val one = "1"
                        Log.e("one", one)
                    } /* else if (toPosition == pos) {
                            if (action == 0) {
                                pos = pos + 1;
                            } else if (action == 1) {
                                pos = pos - 1;
                            }
                        }*/ else if (fromPosition < pos && toPosition > pos) {
                        pos -= 1
                        val one = "2"
                        Log.e("one", one)
                    } else if (fromPosition > pos && toPosition > pos || fromPosition < pos && toPosition < pos) {
                        pos = pos
                        val one = "3"
                        Log.e("one", one)
                    } else if (fromPosition > pos && toPosition < pos) {
                        pos += 1
                        val one = "4"
                        Log.e("one", one)
                    } else if (fromPosition > pos && toPosition == pos) {
                        pos += 1
                        val one = "5"
                        Log.e("one", one)
                    } else if (fromPosition < pos && toPosition == pos) {
                        pos -= 1
                        val one = "6"
                        Log.e("one", one)
                    }
                    val mainPlayModelList = java.util.ArrayList<MainPlayModel>()
                    for (i in listModel.indices) {
                        val mainPlayModel = MainPlayModel()
                        mainPlayModel.id = listModel[i].id
                        mainPlayModel.name = listModel[i].name
                        mainPlayModel.audioFile = listModel[i].audioFile
                        mainPlayModel.playlistID = listModel[i].playlistID
                        mainPlayModel.audioDirection = listModel[i].audioDirection
                        mainPlayModel.audiomastercat = listModel[i].audiomastercat
                        mainPlayModel.audioSubCategory = listModel[i].audioSubCategory
                        mainPlayModel.imageFile = listModel[i].imageFile
                        mainPlayModel.audioDuration = listModel[i].audioDuration
                        mainPlayModelList.add(mainPlayModel)
                    }
                    val shareddd: SharedPreferences = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                    val editor = shareddd.edit()
                    val gson = Gson()
                    val json = gson.toJson(listModel)
                    val jsonz = gson.toJson(mainPlayModelList)
                    editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json)
                    editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonz)
                    editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, pos)
                    editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, PlaylistID)
                    editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, PlaylistName)
                    editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "created")
                    editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "playList")
                    editor.apply()
                    if (player != null) {
                        player.moveMediaItem(fromPosition, toPosition)
                    }
                }
            }
        }

        private fun callDragApi() {
            try {
                if (isNetworkConnected(ctx)) {
                    val listCall = APINewClient.client.sortAudio(coUserId, PlaylistID, TextUtils.join(",", changedAudio))
                    listCall.enqueue(object : Callback<SucessModel?> {
                        override fun onResponse(call: Call<SucessModel?>, response: Response<SucessModel?>) {
                            val listModel: SucessModel? = response.body()
                            if (listModel != null) {
                                when {
                                    listModel.responseCode.equals(activity.getString(R.string.ResponseCodesuccess), ignoreCase = true) -> {
                                    }
                                    listModel.responseCode.equals(activity.getString(R.string.ResponseCodeDeleted), ignoreCase = true) -> {
                                        deleteCall(activity)
                                        val i = Intent(activity, SignInActivity::class.java)
                                        i.putExtra("mobileNo", "")
                                        i.putExtra("countryCode", "")
                                        i.putExtra("name", "")
                                        i.putExtra("email", "")
                                        i.putExtra("countryShortName", "")
                                        activity.startActivity(i)
                                        activity.finish()
                                    }
                                    else -> {
                                        //                                showToast(listModel.responseMessage, activity)
                                    }
                                }
                            }
                        }

                        override fun onFailure(call: Call<SucessModel?>, t: Throwable) {}
                    })
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        override fun onRowSelected(myViewHolder: RecyclerView.ViewHolder?) {}

        override fun onRowClear(myViewHolder: RecyclerView.ViewHolder?) {}

    }

    class PlayListsAdpater2(var listModel: ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>, var ctx: Context, var coUserId: String?, var created: String?, var binding: ActivityMyPlaylistListingBinding, var activity: Activity, var playlistId: String?, var playlistName: String?, private var myDownloads: String?, var listModel1: PlaylistDetailsModel.ResponseData) : RecyclerView.Adapter<PlayListsAdpater2.MyViewHolder>(), Filterable {

        private var listFilterData: ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong> = listModel

        inner class MyViewHolder(var binding: MyPlaylistLayoutBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: MyPlaylistLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.my_playlist_layout, parent, false)
            return MyViewHolder(v)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) { //            searchEditText.setHint("Search for audio")
            //            binding.tvSearch.setHint("Search for audio")

            val mData: List<PlaylistDetailsModel.ResponseData.PlaylistSong> = listFilterData
            holder.binding.tvTitleA.text = mData[position].name
            holder.binding.tvTimeA.text = mData[position].audioDuration //            binding.tvSearch.setVisibility(View.GONE)
            binding.searchView.visibility = View.VISIBLE
            val measureRatio = measureRatio(ctx, 0f, 1f, 1f, 0.13f, 0f)
            holder.binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            holder.binding.ivBackgroundImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivBackgroundImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivBackgroundImage.scaleType = ImageView.ScaleType.FIT_XY
            holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg)
            Glide.with(ctx).load(mData[position].imageFile).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)

            Glide.with(ctx).load(R.drawable.ic_image_bg).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivBackgroundImage)

            val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val audioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
            val myPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
            if (myDownloads.equals("1", ignoreCase = true)) {
                if (audioPlayerFlag.equals("Downloadlist", ignoreCase = true) && myPlaylist.equals(playlistId, ignoreCase = true)) {
                    if (PlayerAudioId.equals(mData[position].id, ignoreCase = true)) {
                        if (player != null) {
                            if (!player.playWhenReady) {
                                holder.binding.equalizerview.pause()
                            } else holder.binding.equalizerview.resume(true)
                        } else holder.binding.equalizerview.stop(true)
                        holder.binding.equalizerview.visibility = View.VISIBLE
                        holder.binding.llMainLayout.setBackgroundResource(R.color.highlight_background)
                        holder.binding.ivBackgroundImage.visibility = View.VISIBLE
                        holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg)
                    } else {
                        holder.binding.equalizerview.visibility = View.GONE
                        holder.binding.llMainLayout.setBackgroundResource(R.color.white)
                        holder.binding.ivBackgroundImage.visibility = View.GONE
                    } //                    handler3.postDelayed(UpdateSongTime3, 500);
                } else {
                    holder.binding.equalizerview.visibility = View.GONE
                    holder.binding.llMainLayout.setBackgroundResource(R.color.white)
                    holder.binding.ivBackgroundImage.visibility = View.GONE //                    handler3.removeCallbacks(UpdateSongTime3);
                }
            } else {
                if (audioPlayerFlag.equals("playlist", ignoreCase = true) && myPlaylist.equals(playlistId, ignoreCase = true)) {
                    if (PlayerAudioId.equals(mData[position].id, ignoreCase = true)) {
                        if (player != null) {
                            if (!player.playWhenReady) {
                                holder.binding.equalizerview.pause()
                            } else holder.binding.equalizerview.resume(true)
                        } else holder.binding.equalizerview.stop(true)
                        holder.binding.equalizerview.visibility = View.VISIBLE
                        holder.binding.llMainLayout.setBackgroundResource(R.color.highlight_background)
                        holder.binding.ivBackgroundImage.visibility = View.VISIBLE
                        holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg)
                    } else {
                        holder.binding.equalizerview.visibility = View.GONE
                        holder.binding.llMainLayout.setBackgroundResource(R.color.white)
                        holder.binding.ivBackgroundImage.visibility = View.GONE
                    } //                    handler3.postDelayed(UpdateSongTime3, 500);
                } else {
                    holder.binding.equalizerview.visibility = View.GONE
                    holder.binding.llMainLayout.setBackgroundResource(R.color.white)
                    holder.binding.ivBackgroundImage.visibility = View.GONE //                    handler3.removeCallbacks(UpdateSongTime3);
                }
            }

            holder.binding.llMore.setOnClickListener {
                if (isNetworkConnected(ctx)) {
                    callAudioDetails(mData[position].id, ctx, activity, coUserId, "playlist", arrayListOf<DownloadAudioDetails>(), arrayListOf<ViewAllAudioListModel.ResponseData.Detail>(), mData, arrayListOf<MainPlayModel>(), position)
                } else {
                    showToast(activity.getString(R.string.no_server_found), activity)
                }
            }

            holder.binding.llMainLayout.setOnClickListener {
                var playfrom = ""
                if (created.equals("2")) {
                    playfrom = "suggested"
                }
                MyPlaylistListingActivity().callMainPlayer(position, playfrom, listFilterData, ctx, activity, playlistId!!, playlistName!!, created, myDownloads)
            }
            binding.llPlayPause.setOnClickListener { //                MyPlaylistListingActivity().callMainPlayer(position, "", listModel, ctx, activity)
                when (isPlayPlaylist) {
                    1 -> {
                        player.playWhenReady = false
                        isPlayPlaylist = 2
                        binding.llPlay.visibility = View.VISIBLE
                        binding.llPause.visibility = View.GONE
                    }
                    2 -> {
                        if (player != null) {
                            if (PlayerAudioId.equals(mData[mData.size - 1].id, ignoreCase = true) && player.duration - player.currentPosition <= 20) {
                                val shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                                val editor = shared.edit()
                                editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                                editor.apply()
                                player.seekTo(0, 0)
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
                        PlayerAudioId = mData[0].id
                        var playfrom: String = ""
                        if (created.equals("2")) {
                            playfrom = "suggested"
                        }
                        MyPlaylistListingActivity().callMainPlayer(0, playfrom, listModel, ctx, activity, listModel[0].playlistID, playlistName!!, created, myDownloads)
                        binding.llPlay.visibility = View.GONE
                        binding.llPause.visibility = View.VISIBLE
                    }
                }
                notifyDataSetChanged()
            }
            binding.llDelete.setOnClickListener {
                val shared: SharedPreferences = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val audioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                val pID = shared.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                if (audioFlag.equals("Downloadlist", ignoreCase = true) && pID.equals(playlistId, ignoreCase = true)) {
                    showToast("You can't delete a playlist while it's playing.", activity)
                } else {
                    val dialog = Dialog(ctx)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(R.layout.custom_popup_layout)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(activity, R.color.dark_blue_gray)))
                    dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    val tvGoBack = dialog.findViewById<TextView>(R.id.tvGoBack)
                    val tvHeader = dialog.findViewById<TextView>(R.id.tvHeader)
                    val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
                    val btn = dialog.findViewById<Button>(R.id.Btn)
                    tvTitle.text = "Remove playlist"
                    tvHeader.text = "Are you sure you want to remove the $playlistName from downloads??"
                    btn.text = "Confirm"
                    dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            dialog.dismiss()
                        }
                        false
                    }
                    val shared = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, AppCompatActivity.MODE_PRIVATE)
                    coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
                    btn.setOnClickListener {
                        getDeleteDownloadData(ctx)
                        getPlaylistMedia(playlistId!!, ctx, DB, coUserId)

                        val p = Properties()
                        p.putValue("playlistId", listModel1.playlistID)
                        p.putValue("playlistName", listModel1.playlistName)
                        when {
                            listModel1.created.equals("1", ignoreCase = true) -> {
                                p.putValue("playlistType", "Created")
                            }
                            listModel1.created.equals("0", ignoreCase = true) -> {
                                p.putValue("playlistType", "Default")
                            }
                            listModel1.created == "2" -> p.putValue("playlistType", "Suggested")
                        }

                        p.putValue("audioCount", listModel1.totalAudio)
                        p.putValue("playlistDescription", listModel1.playlistDesc)
                        when {
                            listModel1.totalhour.equals("", ignoreCase = true) -> {
                                p.putValue("playlistDuration", "0h " + listModel1.totalminute + "m")
                            }
                            listModel1.totalminute.equals("", ignoreCase = true) -> {
                                p.putValue("playlistDuration", listModel1.totalhour + "h 0m")
                            }
                            else -> {
                                p.putValue("playlistDuration", listModel1.totalhour + "h " + listModel1.totalminute + "m")
                            }
                        }
                        p.putValue("source", "Downloaded Playlists")
                        addToSegment("Downloaded Playlist Removed", p, CONSTANTS.track)
                        dialog.dismiss()
                        activity.finish()
                    }
                    tvGoBack.setOnClickListener { dialog.dismiss() }
                    dialog.show()
                    dialog.setCancelable(false)
                }
            }

        }

        fun getDeleteDownloadData(ctx: Context) { //        try {
            val fileNameList: List<String>
            val fileNameList1: List<String>
            val audioFile: List<String>
            val playlistDownloadId: List<String>
            val sharedy: SharedPreferences = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE)
            val gson = Gson()
            val jsony = sharedy.getString(CONSTANTS.PREF_KEY_DownloadName, gson.toString())
            val json1 = sharedy.getString(CONSTANTS.PREF_KEY_DownloadUrl, gson.toString())
            val jsonq = sharedy.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, gson.toString())
            if (!jsony.equals(gson.toString(), ignoreCase = true)) {
                val type = object : TypeToken<List<String?>?>() {}.type
                fileNameList = gson.fromJson(jsony, type)
                fileNameList1 = gson.fromJson(jsony, type)
                audioFile = gson.fromJson(json1, type)
                playlistDownloadId = gson.fromJson(jsonq, type)
                if (playlistDownloadId.isNotEmpty()) {
                    if (playlistDownloadId.contains(playlistId)) {
                        for (i in 1 until fileNameList1.size - 1) {
                            if (playlistDownloadId[i].equals(playlistId, ignoreCase = true)) {
                                fileNameList.drop(i)
                                audioFile.drop(i)
                                playlistDownloadId.drop(i)
                            }
                        }
                        val shared: SharedPreferences = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE)
                        val editor = shared.edit()
                        val nameJson = gson.toJson(fileNameList)
                        val urlJson = gson.toJson(audioFile)
                        val playlistIdJson = gson.toJson(playlistDownloadId)
                        editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson)
                        editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson)
                        editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson)
                        editor.apply()
                        if (fileNameList[0].equals(DownloadMedia.filename, ignoreCase = true) && playlistDownloadId[0].equals(playlistId, ignoreCase = true)) {
                            PRDownloader.cancel(DownloadMedia.downloadIdOne)
                            DownloadMedia.filename = ""
                        }
                    }
                }
            }
        }

        private fun getPlaylistMedia(playlistId: String, ctx: Context, DB: AudioDatabase, coUserId: String?) {
            DB.taskDao()?.getAllAudioByPlaylist1(playlistId, coUserId)?.observe(ctx as (LifecycleOwner), { audioList: List<DownloadAudioDetails?>? ->
                deleteDownloadFile(ctx, playlistId, DB, coUserId)
                if (audioList!!.isNotEmpty()) {
                    getSingleMedia(audioList[0]!!.AudioFile, ctx, playlistId, audioList as List<DownloadAudioDetails>, 0, DB, coUserId)
                }
            })
        }

        private fun deleteDownloadFile(ctx: Context, PlaylistId: String, DB: AudioDatabase, coUserId: String?) {
            AudioDatabase.databaseWriteExecutor.execute {
                DB.taskDao()?.deleteByPlaylistId(PlaylistId, coUserId)
            }
            deletePlaylist(PlaylistId, DB, coUserId)
        }

        private fun deletePlaylist(playlistId: String, DB: AudioDatabase, coUserId: String?) {
            AudioDatabase.databaseWriteExecutor.execute {
                DB.taskDao()?.deletePlaylist(playlistId, coUserId)
            }
        }

        fun getSingleMedia(AudioFile: String?, ctx: Context, playlistID: String?, audioList: List<DownloadAudioDetails?>?, i: Int, DB: AudioDatabase, CoUserID: String?) {
            DB.taskDao()?.getLastIdByuId1(AudioFile, coUserId)?.observe(ctx as (LifecycleOwner), { audioList1: List<DownloadAudioDetails?>? ->
                try {
                    if (audioList1!!.isNotEmpty()) {
                        if (audioList1.size == 1) {
                            FileUtils.deleteDownloadedFile(ctx, audioList1[0]!!.Name.toString())
                        }
                    }
                    if (i < audioList!!.size - 1) {
                        getSingleMedia(audioList[i + 1]!!.AudioFile, ctx.applicationContext, playlistID, audioList, i + 1, DB, CoUserID)
                        Log.e("DownloadMedia Call", (i + 1).toString())
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            })
        }

        override fun getItemCount(): Int {
            return listFilterData.size
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(charSequence: CharSequence): FilterResults {
                    val filterResults = FilterResults()
                    val charString = charSequence.toString()
                    listFilterData = if (charString.isEmpty()) {
                        listModel
                    } else {
                        val filteredList = ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>()
                        for (row in listModel) {
                            if (row.name.toLowerCase(Locale.ROOT).contains(charString.toLowerCase(Locale.ROOT))) {
                                filteredList.add(row)
                            }
                        }
                        filteredList
                    }
                    filterResults.values = listFilterData
                    return filterResults
                }

                override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                    if (listFilterData.isEmpty()) {
                        binding.llError.visibility = View.VISIBLE
                        binding.tvTag.visibility = View.GONE
                        binding.rvPlayLists2.visibility = View.GONE //                        binding.tvFound.setText("Couldn't find '" + SearchFlag + "'. Try searching again");
                        binding.tvFound.text = "Please try again with another search term." //                        Log.e("search", SearchFlag)
                    } else {
                        binding.llError.visibility = View.GONE
                        binding.tvTag.visibility = View.VISIBLE
                        binding.rvPlayLists2.visibility = View.VISIBLE
                        listFilterData = filterResults.values as ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    fun callMainPlayer(position: Int, view: String?, listModel: List<PlaylistDetailsModel.ResponseData.PlaylistSong>, ctx: Context, act: Activity, playlistId: String, playlistName: String, created: String?, myDownloads: String?) {
        val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
        val audioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        val myPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
        val playerPosition: Int = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
        val shared12 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
        val isPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "0")
        if (myDownloads.equals("1", true)) {
            if (isNetworkConnected(ctx)) {
                if (audioPlayerFlag.equals("Downloadlist", ignoreCase = true) && myPlaylist.equals(playlistId, ignoreCase = true)) {
                    if (isDisclaimer == 1) {
                        if (player != null) {
                            if (!player.playWhenReady) {
                                player.playWhenReady = true
                            }
                        } else {
                            audioClick = true
                        }
                        callMyPlayer(ctx, act)
                        showToast("The audio shall start playing after the disclaimer", activity)
                    } else {
                        if (player != null) {
                            if (position != playerPosition) {
                                player.seekTo(position, 0)
                                player.playWhenReady = true
                                PlayerAudioId = listModel[position].id
                                val sharedxx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                                val editor = sharedxx.edit()
                                editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position)
                                editor.apply()
                            }
                            callMyPlayer(ctx, act)
                        } else {
                            callPlayer(position, view, listModel, ctx, act, playlistId, playlistName, created, true, myDownloads)
                        }
                    }
                } else {
                    val listModelList2 = arrayListOf<PlaylistDetailsModel.ResponseData.PlaylistSong>()
                    listModelList2.addAll(listModel)
                    val gson = Gson()
                    val disclimerJson = shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
                    val type = object : TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio?>() {}.type
                    val arrayList = gson.fromJson<HomeScreenModel.ResponseData.DisclaimerAudio>(disclimerJson, type)
                    val mainPlayModel = PlaylistDetailsModel.ResponseData.PlaylistSong()
                    mainPlayModel.id = arrayList.id!!
                    mainPlayModel.name = arrayList.name!!
                    mainPlayModel.audioFile = arrayList.audioFile!!
                    mainPlayModel.audioDirection = arrayList.audioDirection!!
                    mainPlayModel.audiomastercat = arrayList.audiomastercat!!
                    mainPlayModel.audioSubCategory = arrayList.audioSubCategory!!
                    mainPlayModel.imageFile = arrayList.imageFile!!
                    mainPlayModel.audioDuration = arrayList.audioDuration!!
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
                    callPlayer(position, view, listModelList2, ctx, act, playlistId, playlistName, created, audioc, myDownloads)
                }
            } else {
                getAllCompletedMedia(audioPlayerFlag!!, myPlaylist!!, 0, ctx, playlistId, playlistName)
            }
        } else {
            if (audioPlayerFlag.equals("playlist", ignoreCase = true) && myPlaylist.equals(playlistId, ignoreCase = true)) {
                if (isDisclaimer == 1) {
                    if (player != null) {
                        if (!player.playWhenReady) {
                            player.playWhenReady = true
                        }
                    } else {
                        audioClick = true
                    }
                    callMyPlayer(ctx, act)
                    showToast("The audio shall start playing after the disclaimer", this@MyPlaylistListingActivity)
                } else {
                    if (player != null) {
                        if (position != playerPosition) {
                            player.seekTo(position, 0)
                            player.playWhenReady = true
                            val sharedxx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                            val editor = sharedxx.edit()
                            editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position)
                            editor.apply()
                        }
                        callMyPlayer(ctx, act)
                    } else {
                        callPlayer(position, view, listModel, ctx, act, playlistId, playlistName, created, true, myDownloads)
                    }
                }
            } else {
                val listModelList2 = arrayListOf<PlaylistDetailsModel.ResponseData.PlaylistSong>()
                listModelList2.addAll(listModel)
                val gson = Gson()
                val disclimerJson = shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
                val type = object : TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio?>() {}.type
                val arrayList = gson.fromJson<HomeScreenModel.ResponseData.DisclaimerAudio>(disclimerJson, type)
                val mainPlayModel = PlaylistDetailsModel.ResponseData.PlaylistSong()
                mainPlayModel.id = arrayList.id!!
                mainPlayModel.name = arrayList.name!!
                mainPlayModel.audioFile = arrayList.audioFile!!
                mainPlayModel.audioDirection = arrayList.audioDirection!!
                mainPlayModel.audiomastercat = arrayList.audiomastercat!!
                mainPlayModel.audioSubCategory = arrayList.audioSubCategory!!
                mainPlayModel.imageFile = arrayList.imageFile!!
                mainPlayModel.audioDuration = arrayList.audioDuration!!
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
                callPlayer(position, view, listModelList2, ctx, act, playlistId, playlistName, created, audioc, myDownloads)
            }
        }
    }

    private fun callMyPlayer(ctx: Context, act: Activity) {
        val i = Intent(ctx, MyPlayerActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        ctx.startActivity(i)
        act.overridePendingTransition(0, 0)
    }

    private fun callPlayer(position: Int, view: String?, listModel: List<PlaylistDetailsModel.ResponseData.PlaylistSong>, ctx: Context, act: Activity, playlistID: String, playlistName: String, created: String?, audioc: Boolean, MyDownloads: String?) {
        if (audioc) {
            GlobalInitExoPlayer.callNewPlayerRelease()
            try {
                val shared: SharedPreferences = getSharedPreferences(CONSTANTS.PREF_KEY_SEGMENT_PLAYLIST, MODE_PRIVATE)
                val editor = shared.edit()
                editor.putString(CONSTANTS.PREF_KEY_PlaylistID, listModelGlobal.responseData!!.playlistID)
                editor.putString(CONSTANTS.PREF_KEY_PlaylistName, listModelGlobal.responseData!!.playlistName)
                editor.putString(CONSTANTS.PREF_KEY_PlaylistDescription, listModelGlobal.responseData!!.playlistDesc)
                editor.putString(CONSTANTS.PREF_KEY_PlaylistType, listModelGlobal.responseData!!.created)
                editor.putString(CONSTANTS.PREF_KEY_Totalhour, listModelGlobal.responseData!!.totalhour)
                editor.putString(CONSTANTS.PREF_KEY_Totalminute, listModelGlobal.responseData!!.totalminute)
                editor.putString(CONSTANTS.PREF_KEY_TotalAudio, listModelGlobal.responseData!!.totalAudio)
                editor.putString(CONSTANTS.PREF_KEY_ScreenView, screenView)
                editor.apply()
                val p = Properties()
                p.putValue("playlistId", listModelGlobal.responseData!!.playlistID)
                p.putValue("playlistName", listModelGlobal.responseData!!.playlistName)
                p.putValue("playlistDescription", listModelGlobal.responseData!!.playlistDesc)
                when {
                    listModelGlobal.responseData!!.created.equals("1", ignoreCase = true) -> {
                        p.putValue("playlistType", "Created")
                    }
                    listModelGlobal.responseData!!.created.equals("0", ignoreCase = true) -> {
                        p.putValue("playlistType", "Default")
                    }
                    listModelGlobal.responseData!!.created.equals("2", ignoreCase = true) -> {
                        p.putValue("playlistType", "Suggested")
                    }
                }
                when {
                    listModelGlobal.responseData!!.totalhour.equals("", ignoreCase = true) -> {
                        p.putValue("playlistDuration", "0h " + listModelGlobal.responseData!!.totalminute + "m")
                    }
                    listModelGlobal.responseData!!.totalminute.equals("", ignoreCase = true) -> {
                        p.putValue("playlistDuration", listModelGlobal.responseData!!.totalhour + "h 0m")
                    }
                    else -> {
                        p.putValue("playlistDuration", listModelGlobal.responseData!!.totalhour + "h " + listModelGlobal.responseData!!.totalminute + "m")
                    }
                }
                p.putValue("audioCount", listModelGlobal.responseData!!.totalAudio)
                p.putValue("source", screenView)
                p.putValue("playerType", "Mini")
                p.putValue("audioService", appStatus(ctx))
                p.putValue("sound", hundredVolume.toString())
                addToSegment("Playlist Started", p, CONSTANTS.track)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        val shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
        val editor = shared.edit()
        val gson = Gson()
        val downloadAudioDetails = java.util.ArrayList<DownloadAudioDetails>()
        val json: String
        PlayerAudioId = listModel[position].id
        if (MyDownloads.equals("1", ignoreCase = true)) {
            for (i in listModel.indices) {
                val mainPlayModel = DownloadAudioDetails()
                mainPlayModel.ID = listModel[i].id
                mainPlayModel.Name = listModel[i].name
                mainPlayModel.AudioFile = listModel[i].audioFile
                mainPlayModel.AudioDirection = listModel[i].audioDirection
                mainPlayModel.Audiomastercat = listModel[i].audiomastercat
                mainPlayModel.AudioSubCategory = listModel[i].audioSubCategory
                mainPlayModel.ImageFile = listModel[i].imageFile
                mainPlayModel.AudioDuration = listModel[i].audioDuration
                downloadAudioDetails.add(mainPlayModel)
            }
            json = gson.toJson(downloadAudioDetails)
            editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "Downloadlist")
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json)
        } else {
            json = gson.toJson(listModel)
            editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "playlist")
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json)
        }
        editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position)
        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, playlistID)
        editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, playlistName)
        if(created.equals("2"))
            editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "Suggested")
        else
            editor.putString(CONSTANTS.PREF_KEY_PlayFrom, view)
        editor.apply()
        audioClick = audioc
        callMyPlayer(ctx, act)
    }

    private fun removeobserver(DB: AudioDatabase) {
        DB.taskDao()?.getPlaylist1(playlistId, coUserId)?.removeObserver {}
    }

    private fun getPlaylistDetail(SongListSize: Int, ctx: Context, DB: AudioDatabase): ArrayList<DownloadPlaylistDetails> { //        try {
        DB.taskDao()?.getPlaylist1(playlistId, coUserId)?.observe(ctx as (LifecycleOwner), { audioList: List<DownloadPlaylistDetails> ->
            downloadPlaylistDetailsList = ArrayList()
            downloadPlaylistDetailsList.addAll(audioList)
            when {
                downloadPlaylistDetailsList.isNotEmpty() -> {
                    getMediaByPer(playlistId!!, SongListSize, ctx, DB)
                    removeobserver(DB)
                }
                SongListSize == 0 -> {
                    enableDisableDownload(false, "gray")
                    removeobserver(DB)
                } /*else if (download.equals("1", ignoreCase = true) *//* New.equalsIgnoreCase("1") ||*//*) {
                                        enableDisableDownload(false, "orange")
                                        getMediaByPer(PlaylistID!!, SongListSize)
                                        removeobserver()
                                    } */
                else -> {
                    enableDisableDownload(true, "white")
                    removeobserver(DB)
                }
            }
        } as (List<DownloadPlaylistDetails?>) -> Unit) //                })
        //        } catch (e: java.lang.Exception) {
        //            e.printStackTrace()
        //        }
        return downloadPlaylistDetailsList
    }

    private fun getMediaByPer(PlaylistId: String, totalAudio: Int, ctx: Context, DB: AudioDatabase) { //        try {
        DB.taskDao()?.getCountDownloadProgress1("Complete", PlaylistId, coUserId)?.observe(ctx as (LifecycleOwner), { countx: List<DownloadAudioDetails?> ->
            count = countx.size //                if (downloadPlaylistDetailsList.size() != 0) {
            if (count <= totalAudio) {
                if (count == totalAudio) {
                    binding.pbProgress.visibility = View.GONE
                    binding.ivDownloads.visibility = View.VISIBLE

                    enableDisableDownload(false, "orange")
                    DB.taskDao()?.getCountDownloadProgress1("Complete", PlaylistId, coUserId)?.removeObserver {}
                } else {
                    val progressPercent: Long = (count * 100 / totalAudio).toLong()
                    val downloadProgress1 = progressPercent.toInt()
                    binding.pbProgress.visibility = View.VISIBLE
                    binding.ivDownloads.visibility = View.GONE
                    binding.pbProgress.progress = downloadProgress1
                    getMediaByPer(playlistId!!, totalAudio, ctx, DB)
                }
            } else {
                DB.taskDao()?.getCountDownloadProgress1("Complete", PlaylistId, coUserId)?.removeObserver {}
                binding.pbProgress.visibility = View.GONE
                binding.ivDownloads.visibility = View.VISIBLE
                enableDisableDownload(false, "orange")
            } //                } else {
            //                    binding.pbProgress.setVisibility(View.GONE);
            //                    binding.ivDownloads.setVisibility(View.VISIBLE);
            //                }
            callObserveMethodGetAllMedia(ctx, DB)
        }) //        } catch (e: java.lang.Exception) {
        //            e.printStackTrace()
        //        }
    }

    private fun getDownloadData(ctx: Context, DB: AudioDatabase) {
        try {
            val sharedy: SharedPreferences = getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE)
            val gson = Gson()
            val jsony = sharedy.getString(CONSTANTS.PREF_KEY_DownloadName, gson.toString())
            val json1 = sharedy.getString(CONSTANTS.PREF_KEY_DownloadUrl, gson.toString())
            val jsonq = sharedy.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, gson.toString())
            if (!jsony.equals(gson.toString(), ignoreCase = true)) {
                val type = object : TypeToken<ArrayList<String?>?>() {}.type
                fileNameList = gson.fromJson(jsony, type)
                playlistDownloadId = gson.fromJson(jsonq, type)
            } else {
                fileNameList = ArrayList()
                playlistDownloadId = ArrayList()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun getPlaylistDetail2(ctx: Context, DB: AudioDatabase, coUserId: String, PlaylistID: String) {
        DB.taskDao()?.getPlaylist1(PlaylistID, coUserId)?.observe(ctx as (LifecycleOwner), { audioList: List<DownloadPlaylistDetails> ->
            downloadPlaylistDetailsList = arrayListOf()
            downloadPlaylistDetailsList = audioList as ArrayList<DownloadPlaylistDetails>
            getMedia(ctx, DB)
        } as (List<DownloadPlaylistDetails?>) -> Unit)
    }

    private fun callObserveMethodGetAllMedia(ctx: Context, DB: AudioDatabase) {
        try {
            DB.taskDao()?.geAllData12(coUserId)?.observe(ctx as (LifecycleOwner), { audioList: List<DownloadAudioDetails>? ->
                if (audioList != null) {
                    downloadAudioDetailsList = audioList as ArrayList<DownloadAudioDetails>
                    onlySingleDownloaded = ArrayList()
                    if (audioList.isNotEmpty()) {
                        for (i in audioList) {
                            if (i.PlaylistId.equals("", ignoreCase = true)) {
                                onlySingleDownloaded.add(i.Name!!)
                            }
                        }
                    } else {
                        onlySingleDownloaded = ArrayList()
                    }
                } else {
                    onlySingleDownloaded = ArrayList()
                    downloadAudioDetailsList = ArrayList()
                }
            } as (List<DownloadAudioDetails?>) -> Unit)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun enableDisableDownload(b: Boolean, color: String) {
        if (b) {
            binding.llDownloads.isClickable = true
            binding.llDownloads.isEnabled = true
            binding.ivDownloads.setColorFilter(ContextCompat.getColor(activity, R.color.white), PorterDuff.Mode.SRC_IN)
            Log.e("Download Click", "True")
        } else {
            binding.llDownloads.isClickable = false
            binding.llDownloads.isEnabled = false
            if (color.equals("gray", ignoreCase = true)) {
                binding.ivDownloads.setImageResource(R.drawable.ic_download_bws)
                binding.ivDownloads.setColorFilter(ContextCompat.getColor(activity, R.color.light_gray), PorterDuff.Mode.SRC_IN)
            } else if (color.equals("orange", ignoreCase = true)) {
                binding.ivDownloads.setImageResource(R.drawable.ic_download_done_icon)
                binding.ivDownloads.setColorFilter(ContextCompat.getColor(activity, R.color.white), PorterDuff.Mode.SRC_IN)
            }
            Log.e("Download Click", "false")
        }
    }

    private fun callDownload(id: String, audioFile: String, Name: String, playlistSongs: ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>, position: Int, llDownload: RelativeLayout, ivDownloads: ImageView, ctx: Context, act: Activity, DB: AudioDatabase) {
        AudioDatabase.databaseWriteExecutor.execute {
            downloadAudioDetailsListGlobal = DB.taskDao()?.geAllData1ForAll() as ArrayList<DownloadAudioDetails>
        }
        if (id.isEmpty() && Name.isEmpty() && audioFile.isEmpty()) {
            val url = arrayListOf<String>()
            val name = arrayListOf<String>()
            val downloadPlaylistId = arrayListOf<String>()
            val playlistSongs2 = ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>()
            playlistSongs2.addAll(playlistSongs)
            if (downloadAudioDetailsListGlobal.isNotEmpty()) {
                for (y in downloadAudioDetailsListGlobal) {
                    if (playlistSongs2.size == 0) {
                        break
                    } else {
                        for (x in 0 until playlistSongs2.size) if (playlistSongs2.size != 0) {
                            if (x < playlistSongs2.size) {
                                if (playlistSongs2[x].audioFile.equals(y.AudioFile, ignoreCase = true)) {
                                    playlistSongs2.removeAt(x)
                                }
                            }
                            if (playlistSongs2.size == 0) {
                                break
                            }
                        } else break
                    }
                }
            }
            if (playlistSongs2.size != 0) {
                for (x in playlistSongs2.indices) {
                    name.add(playlistSongs2[x].name)
                    url.add(playlistSongs2[x].audioFile)
                    downloadPlaylistId.add(playlistSongs2[x].playlistID)
                }
            }
            val sharedx: SharedPreferences = getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE)
            val gson1 = Gson()
            val json = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, gson1.toString())
            val json1 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadUrl, gson1.toString())
            val json2 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, gson1.toString())
            if (!json1.equals(gson1.toString(), ignoreCase = true)) {
                val type = object : TypeToken<List<String?>?>() {}.type
                val fileNameList = gson1.fromJson<List<String>>(json, type)
                val audioFile1 = gson1.fromJson<List<String>>(json1, type)
                val playlistId1 = gson1.fromJson<List<String>>(json2, type)
                if (fileNameList.isNotEmpty()) {
                    url.addAll(audioFile1)
                    name.addAll(fileNameList)
                    downloadPlaylistId.addAll(playlistId1)
                }
            }
            if (url.size != 0) {
                if (!DownloadMedia.isDownloading) {
                    DownloadMedia.isDownloading = true
                    val downloadMedia = DownloadMedia(ctx, act)
                    downloadMedia.encrypt1(url, name, downloadPlaylistId)
                }
                val shared: SharedPreferences = getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE)
                val editor = shared.edit()
                val gson = Gson()
                val urlJson = gson.toJson(url)
                val nameJson = gson.toJson(name)
                val playlistIdJson = gson.toJson(downloadPlaylistId)
                fileNameList = name
                playlistDownloadId = downloadPlaylistId
                editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson)
                editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson)
                editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson)
                editor.apply()
            }
            showToast("Your playlist is being downloaded!", act)
            saveAllMedia(ctx, playlistSongs, DB)
        } else {
            var downloadOrNot = false
            if (downloadAudioDetailsListGlobal.size != 0) {
                for (i in downloadAudioDetailsListGlobal.indices) {
                    if (downloadAudioDetailsListGlobal[i].AudioFile.equals(audioFile, ignoreCase = true)) {
                        downloadOrNot = false
                        break
                    } else if (i == downloadAudioDetailsListGlobal.size - 1) {
                        downloadOrNot = true
                    }
                }
            } else {
                downloadOrNot = true
            }
            if (downloadOrNot) {
                val url = arrayListOf<String>()
                val name = arrayListOf<String>()
                val downloadPlaylistId = arrayListOf<String>()
                val sharedx: SharedPreferences = getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE)
                val gson1 = Gson()
                val json = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, gson1.toString())
                val json1 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadUrl, gson1.toString())
                val json2 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, gson1.toString())
                if (!json1.equals(gson1.toString(), ignoreCase = true)) {
                    val type = object : TypeToken<List<String?>?>() {}.type
                    val fileNameList = gson1.fromJson<List<String>>(json, type)
                    val audioFile1 = gson1.fromJson<List<String>>(json1, type)
                    val playlistId1 = gson1.fromJson<List<String>>(json2, type)
                    if (fileNameList.isNotEmpty()) {
                        url.addAll(audioFile1)
                        name.addAll(fileNameList)
                        downloadPlaylistId.addAll(playlistId1)
                    }
                }
                url.add(audioFile)
                name.add(Name)
                downloadPlaylistId.add("")
                if (url.size != 0) {
                    if (!DownloadMedia.isDownloading) {
                        DownloadMedia.isDownloading = true
                        val downloadMedia = DownloadMedia(ctx, act)
                        downloadMedia.encrypt1(url, name, downloadPlaylistId /*, playlistSongs*/)
                    }
                    val shared: SharedPreferences = getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE)
                    val editor = shared.edit()
                    val gson = Gson()
                    val urlJson = gson.toJson(url)
                    val nameJson = gson.toJson(name)
                    val playlistIdJson = gson.toJson(downloadPlaylistId)
                    editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson)
                    editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson)
                    editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson)
                    editor.apply()
                    fileNameList = name
                    playlistDownloadId = downloadPlaylistId
                }
                showToast("Yess! Download complete. Your wellness journey is ready!", act)
                saveMedia(playlistSongs, position, llDownload, ivDownloads, 0, ctx, DB)
            } else {
                showToast("Your audio is being downloaded!", act)
                saveMedia(playlistSongs, position, llDownload, ivDownloads, 100, ctx, DB)
            }
            val sharedx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val audioPlayerFlag = sharedx.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
            val playerPosition: Int = sharedx.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
            val gsonx = Gson()
            val json = sharedx.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gsonx.toString())
            val jsonw = sharedx.getString(CONSTANTS.PREF_KEY_MainAudioList, gsonx.toString())
            var arrayList = ArrayList<DownloadAudioDetails>()
            var arrayList2 = ArrayList<MainPlayModel>()
            var size = 0
            if (!jsonw.equals(gsonx.toString(), ignoreCase = true)) {
                val type1 = object : TypeToken<ArrayList<DownloadAudioDetails>?>() {}.type
                val type0 = object : TypeToken<ArrayList<MainPlayModel?>?>() {}.type
                val gson1 = Gson()
                arrayList = gson1.fromJson(jsonw, type1)
                arrayList2 = gson1.fromJson(json, type0)
                size = arrayList.size
            }
            if (audioPlayerFlag.equals("DownloadListAudio", ignoreCase = true)) {
                val mainPlayModel = DownloadAudioDetails()
                mainPlayModel.ID = playlistSongs[position].id
                mainPlayModel.Name = playlistSongs[position].name
                mainPlayModel.AudioFile = playlistSongs[position].audioFile
                mainPlayModel.AudioDirection = playlistSongs[position].audioDirection
                mainPlayModel.Audiomastercat = playlistSongs[position].audiomastercat
                mainPlayModel.AudioSubCategory = playlistSongs[position].audioSubCategory
                mainPlayModel.ImageFile = playlistSongs[position].imageFile
                mainPlayModel.AudioDuration = playlistSongs[position].audioDuration
                arrayList.add(mainPlayModel)
                val mainPlayModel1 = MainPlayModel()
                mainPlayModel1.id = playlistSongs[position].id
                mainPlayModel1.name = playlistSongs[position].name
                mainPlayModel1.audioFile = playlistSongs[position].audioFile
                mainPlayModel1.audioDirection = playlistSongs[position].audioDirection
                mainPlayModel1.audiomastercat = playlistSongs[position].audiomastercat
                mainPlayModel1.audioSubCategory = playlistSongs[position].audioSubCategory
                mainPlayModel1.imageFile = playlistSongs[position].imageFile
                mainPlayModel1.audioDuration = playlistSongs[position].audioDuration
                arrayList2.add(mainPlayModel1)
                val sharedd: SharedPreferences = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val editor = sharedd.edit()
                val gson = Gson()
                val jsonx = gson.toJson(arrayList2)
                val json1 = gson.toJson(arrayList)

                editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json)
                editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, playerPosition)
                editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "")
                editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "DownloadListAudio")
                editor.apply()
                if (arrayList2[playerPosition].audioFile != "") {
                    val downloadAudioDetailsList1: MutableList<String> = ArrayList()
                    val ge = GlobalInitExoPlayer()
                    downloadAudioDetailsList1.add(mainPlayModel1.name.toString())
                    ge.AddAudioToPlayer(size, arrayList2, downloadAudioDetailsList1, ctx)
                }
            } //            handler2.postDelayed(UpdateSongTime2, 3000);
        }
    }

    private fun saveAllMedia(ctx: Context, playlistSongs: ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>, DB: AudioDatabase) {
           val p = Properties()

           p.putValue("playlistId", downloadPlaylistDetails.PlaylistID)
           p.putValue("playlistName", downloadPlaylistDetails.PlaylistName)
           p.putValue("playlistDescription", downloadPlaylistDetails.PlaylistDesc)
        when {
            downloadPlaylistDetails.Created.equals("1", ignoreCase = true) -> {
                p.putValue("playlistType", "Created")
            }
            downloadPlaylistDetails.Created.equals("0", ignoreCase = true) -> {
                p.putValue("playlistType", "Default")
            }
            downloadPlaylistDetails.Created.equals("0", ignoreCase = true) -> {
                p.putValue("playlistType", "Suggested")
            }
        }
        when {
            downloadPlaylistDetails.Totalhour.equals("", ignoreCase = true) -> {
                p.putValue("playlistDuration", "0h " + downloadPlaylistDetails.Totalminute + "m")
            }
            downloadPlaylistDetails.Totalminute.equals("", ignoreCase = true) -> {
                p.putValue("playlistDuration", downloadPlaylistDetails.Totalhour + "h 0m")
            }
            else -> {
                p.putValue("playlistDuration", downloadPlaylistDetails.Totalhour + "h " + downloadPlaylistDetails.Totalminute + "m")
            }
        }
           p.putValue("audioCount", downloadPlaylistDetails.TotalAudio)
           p.putValue("source", screenView)
           p.putValue("audioService", appStatus(ctx))
           p.putValue("sound", hundredVolume.toString())
            addToSegment("Playlist Download Started", p, CONSTANTS.track)
        for (i in playlistSongs) {
            val downloadAudioDetails = DownloadAudioDetails()
            downloadAudioDetails.UserID = coUserId!!
            downloadAudioDetails.ID = i.id
            downloadAudioDetails.Name = i.name
            downloadAudioDetails.AudioFile = i.audioFile
            downloadAudioDetails.AudioDirection = i.audioDirection
            downloadAudioDetails.Audiomastercat = i.audiomastercat
            downloadAudioDetails.AudioSubCategory = i.audioSubCategory
            downloadAudioDetails.ImageFile = i.imageFile
            downloadAudioDetails.PlaylistId = i.playlistID
            downloadAudioDetails.AudioDuration = i.audioDuration
            downloadAudioDetails.IsSingle = "0"
            if (downloadAudioDetailsListGlobal.size != 0) {
                for (y in downloadAudioDetailsListGlobal) {
                    if (i.audioFile.equals(y.AudioFile, ignoreCase = true)) {
                        downloadAudioDetails.IsDownload = "Complete"
                        downloadAudioDetails.DownloadProgress = 100
                        break
                    } else {
                        downloadAudioDetails.IsDownload = "pending"
                        downloadAudioDetails.DownloadProgress = 0
                    }
                }
            } else {
                downloadAudioDetails.IsDownload = "pending"
                downloadAudioDetails.DownloadProgress = 0
            } //            try {
            AudioDatabase.databaseWriteExecutor.execute {
                DB.taskDao()?.insertMedia(downloadAudioDetails)
            } //            } catch (e: java.lang.Exception) {
            //                println(e.message)
            //            } catch (e: OutOfMemoryError) {
            //                println(e.message)
            //            }
        } //        try {
        downloadPlaylistDetails.UserID = coUserId!!
        AudioDatabase.databaseWriteExecutor.execute {
            DB.taskDao()?.insertPlaylist(downloadPlaylistDetails)
        }
        downloadPlaylistDetailsList = getPlaylistDetail(playlistSongs.size, ctx, DB)
        getMediaByPer(playlistId!!, playlistSongs.size, ctx, DB) //        } catch (e: java.lang.Exception) {
        //            println(e.message)
        //        } catch (e: OutOfMemoryError) {
        //            println(e.message)
        //        }
    }

    private fun saveMedia(playlistSongs: ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>, i: Int, llDownload: RelativeLayout, ivDownloads: ImageView, progress: Int, ctx: Context, DB: AudioDatabase) {
        val downloadAudioDetails = DownloadAudioDetails()
        downloadAudioDetails.UserID = coUserId!!
        downloadAudioDetails.ID = playlistSongs[i].id
        downloadAudioDetails.Name = playlistSongs[i].name
        downloadAudioDetails.AudioFile = playlistSongs[i].audioFile
        downloadAudioDetails.AudioDirection = playlistSongs[i].audioDirection
        downloadAudioDetails.Audiomastercat = playlistSongs[i].audiomastercat
        downloadAudioDetails.AudioSubCategory = playlistSongs[i].audioSubCategory
        downloadAudioDetails.ImageFile = playlistSongs[i].imageFile
        downloadAudioDetails.AudioDuration = playlistSongs[i].audioDuration
        downloadAudioDetails.IsSingle = "1"
        downloadAudioDetails.PlaylistId = ""
        if (progress == 0) {
            downloadAudioDetails.IsDownload = "pending"
        } else {
            downloadAudioDetails.IsDownload = "Complete"
        }
        downloadAudioDetails.DownloadProgress = progress
        try {
            AudioDatabase.databaseWriteExecutor.execute {
                DB.taskDao()?.insertMedia(downloadAudioDetails)
            }
        } catch (e: java.lang.Exception) {
            println(e.message)
        } catch (e: OutOfMemoryError) {
            println(e.message)
        }
        callObserveMethodGetAllMedia(ctx, DB)
        getMedia(ctx, DB)
    }

    /*  fun GetMedia(
              url: String?,
              ctx: Context?,
              download: String,
              llDownload: RelativeLayout,
              ivDownloads: ImageView,
              ctx:Context
      ) {
          DatabaseClient
              .getInstance(this)
              .getaudioDatabase()
              .taskDao()
              .getLastIdByuId1(url).observe(ctx as (LifecycleOwner), { audioList: List<DownloadAudioDetails> ->
                      if (audioList.size != 0) {
                          if (audioList[0].download.equals("1", ignoreCase = true)) {
                              disableDownload(llDownload, ivDownloads)
                          }
                      } else if (download.equals("1", ignoreCase = true)) {
                          disableDownload(llDownload, ivDownloads)
                      } else {
                          enableDownload(llDownload, ivDownloads)
                      }
                  })
      }*/

    private fun getMedia(ctx: Context, DB: AudioDatabase) {
        try { //        playlistWiseAudioDetails = new ArrayList<>();
            DB.taskDao()?.getAllAudioByPlaylist1(playlistId, coUserId)?.observe(ctx as (LifecycleOwner), { audioList: List<DownloadAudioDetails?>? ->
                if (myDownloads.equals("1", ignoreCase = true)) {
                    if (downloadPlaylistDetailsList.size != 0) {
                        val details = ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>()
                        val listModel = PlaylistDetailsModel.ResponseData()
                        listModel.playlistID = downloadPlaylistDetailsList[0].PlaylistID!!
                        listModel.playlistName = downloadPlaylistDetailsList[0].PlaylistName!!
                        listModel.playlistDesc = downloadPlaylistDetailsList[0].PlaylistDesc!!
                        listModel.playlistMastercat = downloadPlaylistDetailsList[0].PlaylistMastercat!!
                        listModel.playlistSubcat = downloadPlaylistDetailsList[0].PlaylistSubcat!!
                        listModel.playlistImage = downloadPlaylistDetailsList[0].PlaylistImage!!
                        listModel.playlistImageDetail = downloadPlaylistDetailsList[0].PlaylistImageDetails!!
                        listModel.totalAudio = downloadPlaylistDetailsList[0].TotalAudio!!
                        listModel.totalDuration = downloadPlaylistDetailsList[0].TotalDuration!!
                        listModel.totalhour = downloadPlaylistDetailsList[0].Totalhour!!
                        listModel.totalminute = downloadPlaylistDetailsList[0].Totalminute!!
                        listModel.created = downloadPlaylistDetailsList[0].Created!! //                        listModel.isReminder = downloadPlaylistDetailsList[0]!!.isReminder
                        if (audioList!!.isNotEmpty()) {
                            for (i in audioList.indices) {
                                val detail = PlaylistDetailsModel.ResponseData.PlaylistSong()
                                detail.id = audioList[i]!!.ID!!
                                detail.name = audioList[i]!!.Name!!
                                detail.audioFile = audioList[i]!!.AudioFile!!
                                detail.audioDirection = audioList[i]!!.AudioDirection!!
                                detail.audiomastercat = audioList[i]!!.Audiomastercat!!
                                detail.audioSubCategory = audioList[i]!!.AudioSubCategory!!
                                detail.imageFile = audioList[i]!!.ImageFile!!
                                detail.audioDuration = audioList[i]!!.AudioDuration!!
                                details.add(detail)
                            }
                            listModel.playlistSongs = details
                        }
                        setData(listModel)
                    }
                }
            })
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /*  private fun enableDownload(llDownload: RelativeLayout, ivDownloads: ImageView) {
          binding.ivDownloads.setImageResource(R.drawable.ic_download_bws)
          llDownload.isClickable = true
          llDownload.isEnabled = true
          ivDownloads.setColorFilter(
              ContextCompat.getColor(activity, R.color.black),
              PorterDuff.Mode.SRC_IN
          )
      }

      private fun disableDownload(llDownload: RelativeLayout, ivDownloads: ImageView) {
          binding.ivDownloads.setImageResource(R.drawable.ic_download_done_icon)
          ivDownloads.setColorFilter(
              ContextCompat.getColor(activity, R.color.black),
              PorterDuff.Mode.SRC_IN
          )
          llDownload.isClickable = false
          llDownload.isEnabled = false
      }*/

    private fun getAllCompletedMedia(audioFlag: String, pID: String, position: Int, ctx: Context, playlistID: String, playlistName: String) {
        var pos = 0
        val shared: SharedPreferences = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
        var positionSaved = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
        val shared12 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
        val isPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "0")
        if (audioFlag.equals("Downloadlist", ignoreCase = true) && pID.equals(playlistId, ignoreCase = true)) {
            if (isDisclaimer == 1) {
                if (player != null) {
                    if (!player.playWhenReady) {
                        player.playWhenReady = true
                    } else player.playWhenReady = true
                } else {
                    audioClick = true
                }
                callMyPlayer(ctx, activity)
                showToast("The audio shall start playing after the disclaimer", activity)
            } else {
                val listModelList2 = arrayListOf<PlaylistDetailsModel.ResponseData.PlaylistSong>()
                var view = ""
                val listModel = listModelGlobal.responseData
                if (listModel != null) {
                for (i in listModel.playlistSongs!!.indices) {
                        if (downloadAudioDetailsList.contains(listModel.playlistSongs!![i].name)) {
                            listModelList2.add(listModel.playlistSongs!![i])
                        }
                }
            }
                if (position != positionSaved) {
                    val listmodel = listModelGlobal.responseData
                    if (listmodel != null) {
                    if (downloadAudioDetailsList.contains(listmodel.playlistSongs!![position].name)) {
                        positionSaved = position
                        PlayerAudioId = listmodel.playlistSongs!![position].id
                        if (listModelList2.size != 0) {
                            view = if (listmodel.created == "1") {
                                "Created"
                            } else {
                                ""
                            }

                            callPlayer(pos, view, listModelList2, ctx, activity, playlistID, playlistName, listmodel.created, true, myDownloads)

                        } else {
                            showToast(ctx.getString(R.string.no_server_found), activity)
                        }
                    } else { //                                pos = 0;
                        showToast(ctx.getString(R.string.no_server_found), activity)
                    }
                }
                } else {
                    callMyPlayer(ctx, activity)
                }
//                SegmentTag()
            }
        } else {
            val listModelList2 = arrayListOf<PlaylistDetailsModel.ResponseData.PlaylistSong>()
            for (i in listModelGlobal.responseData!!.playlistSongs!!.indices) {
                if (downloadAudioDetailsList.contains(listModelGlobal.responseData!!.playlistSongs!![i].name)) {
                    listModelList2.add(listModelGlobal.responseData!!.playlistSongs!![i])
                }
            }
            if (downloadAudioDetailsList.contains(listModelGlobal.responseData!!.playlistSongs!![position].name)) {
                pos = position
                val gson = Gson()
                val disclimerJson = shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
                val type = object : TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio?>() {}.type
                val arrayList = gson.fromJson<HomeScreenModel.ResponseData.DisclaimerAudio>(disclimerJson, type)
                val mainPlayModel = PlaylistDetailsModel.ResponseData.PlaylistSong()
                mainPlayModel.id = arrayList.id!!
                mainPlayModel.name = arrayList.name!!
                mainPlayModel.audioFile = arrayList.audioFile!!
                mainPlayModel.audioDirection = arrayList.audioDirection!!
                mainPlayModel.audiomastercat = arrayList.audiomastercat!!
                mainPlayModel.audioSubCategory = arrayList.audioSubCategory!!
                mainPlayModel.imageFile = arrayList.imageFile!!
                mainPlayModel.audioDuration = arrayList.audioDuration!!
                var audioc = true
                var view = ""
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
                    if (listModelList2[pos].id != "0") {
                        if (listModelList2.size != 0) {
                            view = if (listModelGlobal.responseData!!.created == "1") {
                                "Created"
                            } else {
                                ""
                            }
                            callPlayer(pos, view, listModelList2, ctx, activity, playlistID, playlistName, listModelGlobal.responseData!!.created, audioc, myDownloads)
                        } else {
                            showToast(ctx.getString(R.string.no_server_found), activity)
                        }
                    } else if (listModelList2[pos].id == "0" && listModelList2.size > 1) {
                        view = if (listModelGlobal.responseData!!.created == "1") {
                            "Created"
                        } else {
                            ""
                        }
                        callPlayer(pos, view, listModelList2, ctx, activity, playlistID, playlistName, listModelGlobal.responseData!!.created, audioc, myDownloads)
                    } else {
                        showToast(ctx.getString(R.string.no_server_found), activity)
                    }
                } else {
                    showToast(ctx.getString(R.string.no_server_found), activity)
                }
            } else {
                showToast(ctx.getString(R.string.no_server_found), activity)
            } //            SegmentTag()
        }
    }

}
