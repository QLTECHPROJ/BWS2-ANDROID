package com.brainwellnessspa.dashboardModule.manage

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
import com.brainwellnessspa.DashboardOldModule.Activities.DashboardActivity.audioClick
import com.brainwellnessspa.DashboardOldModule.Models.ViewAllAudioListModel
import com.brainwellnessspa.DashboardOldModule.TransparentPlayer.Fragments.MiniPlayerFragment.isDisclaimer
import com.brainwellnessspa.DashboardOldModule.TransparentPlayer.Models.MainPlayModel
import com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia
import com.brainwellnessspa.EncryptDecryptUtils.FileUtils
import com.brainwellnessspa.R
import com.brainwellnessspa.RoomDataBase.AudioDatabase

import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails
import com.brainwellnessspa.RoomDataBase.DownloadPlaylistDetails
import com.brainwellnessspa.Services.GlobalInitExoPlayer
import com.brainwellnessspa.Services.GlobalInitExoPlayer.callNewPlayerRelease
import com.brainwellnessspa.Services.GlobalInitExoPlayer.player
import com.brainwellnessspa.Utility.*
import com.brainwellnessspa.Utility.ItemMoveCallback.ItemTouchHelperContract
import com.brainwellnessspa.dashboardModule.activities.AddAudioActivity
import com.brainwellnessspa.dashboardModule.activities.MyPlayerActivity
import com.brainwellnessspa.dashboardModule.models.HomeScreenModel
import com.brainwellnessspa.dashboardModule.models.PlaylistDetailsModel
import com.brainwellnessspa.dashboardModule.models.SucessModel
import com.brainwellnessspa.databinding.*
import com.brainwellnessspa.manageModule.RecommendedCategoryActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.downloader.PRDownloader
import com.google.android.flexbox.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class MyPlaylistListingActivity : AppCompatActivity(), StartDragListener {
    lateinit var ctx: Context
    var activity: Activity? = null
    var CoUserID: String? = ""
    var USERID: String? = ""
    var MyCreated: String? = ""
    var New: String? = ""
    var PlaylistID: String? = ""
    var PlaylistName: String? = ""
    var PlaylistImage: String? = ""
    var MyDownloads: String? = ""
    var fileNameList = arrayListOf<String>()
    var playlistSongsList = arrayListOf<PlaylistDetailsModel.ResponseData.PlaylistSong>()
    var onlySingleDownloaded = arrayListOf<String>()
    var playlistDownloadId = arrayListOf<String>()
    var downloadPlaylistDetails = DownloadPlaylistDetails()
    var downloadPlaylistDetailsList = arrayListOf<DownloadPlaylistDetails>()
    var downloadAudios: List<String> = java.util.ArrayList()
    var downloadAudioDetailsList = arrayListOf<DownloadAudioDetails>()
    var downloadAudioDetailsListGloble = ArrayList<DownloadAudioDetails>()
    lateinit var adpater: PlayListsAdpater
    lateinit var adpater2: PlayListsAdpater2
    lateinit var binding: ActivityMyPlaylistListingBinding
    var count: Int = 0
    var hundredVolume: Int = 0
    var currentVolume: Int = 0
    var maxVolume: Int = 0
    var percent: Int = 0
    lateinit var searchEditText: EditText
    var touchHelper: ItemTouchHelper? = null
    var listMOdelGloble: PlaylistDetailsModel = PlaylistDetailsModel()
    var SLEEPTIME: String? = null
    var gson = Gson()
    var selectedCategoriesName = arrayListOf<String>()


    private val listener: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("MyData")) {
                try {
                    val data = intent.getStringExtra("MyData")
                    Log.d("play_pause_Action", data!!)
                    val sharedw: SharedPreferences =
                        getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                    val AudioFlag = sharedw.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                    val pID = sharedw.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                    if (MyDownloads.equals("1", ignoreCase = true)) {
                        if (AudioFlag.equals("Downloadlist", ignoreCase = true) && pID.equals(
                                PlaylistName,
                                ignoreCase = true
                            )
                        ) {
                            /*if (data.equalsIgnoreCase("pause")) {
                            isPlayPlaylist = 1;
                            binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_icon));
//                            handler3.postDelayed(UpdateSongTime3, 500);
                            adpater2.notifyDataSetChanged();

                        } else {
                            isPlayPlaylist = 0;
                            binding.ivPlaylistStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_blue_play_icon));
                        }*/
                            if (MyDownloads.equals("1", ignoreCase = true)) {
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
                                    adpater2.notifyDataSetChanged()
                                }
                            }
                        } else {
                            isPlayPlaylist = 0
                            binding.llPause.visibility = View.GONE
                            binding.llPlay.visibility = View.VISIBLE
                        }
                    } else {
                        if (AudioFlag.equals(
                                "playlist",
                                ignoreCase = true
                            ) && pID.equals(PlaylistID, ignoreCase = true)
                        ) {
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
                                if (MyCreated.equals("1", ignoreCase = true)) {
                                    adpater.notifyDataSetChanged()
                                } else {
                                    adpater2.notifyDataSetChanged()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_playlist_listing)
        ctx = this@MyPlaylistListingActivity
        activity = this@MyPlaylistListingActivity
        if (intent != null) {
            New = intent.getStringExtra("New")
            PlaylistID = intent.getStringExtra("PlaylistID")
            PlaylistName = intent.getStringExtra("PlaylistName")
            MyDownloads = intent.getStringExtra("MyDownloads")
        }
        DB = getAudioDataBase(ctx)
        callObserveMethodGetAllMedia(ctx, DB)
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        USERID = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        binding.tvSearch.setOnClickListener {
            val i = Intent(ctx, AddAudioActivity::class.java)
            i.putExtra("PlaylistID", listMOdelGloble.responseData!!.playlistID)
            startActivity(i)
        }
        val shared1 = ctx.getSharedPreferences(CONSTANTS.RecommendedCatMain, Context.MODE_PRIVATE)
        SLEEPTIME = shared1.getString(CONSTANTS.PREFE_ACCESS_SLEEPTIME, "")
        val json = shared1.getString(CONSTANTS.selectedCategoriesName, gson.toString())
        if (!json.equals(gson.toString(), ignoreCase = true)) {
            val type1 = object :
                com.google.common.reflect.TypeToken<java.util.ArrayList<String?>?>() {}.type
            selectedCategoriesName = gson.fromJson(json, type1)
        }
        binding.tvSleepTime.text = "Your average sleep time is \n$SLEEPTIME"
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
        binding.ivEditCategory.setOnClickListener {
            val i = Intent(activity, RecommendedCategoryActivity::class.java)
            i.putExtra("BackClick", "1")
            startActivity(i)
        }
        binding.llDownloads.setOnClickListener {
            callObserveMethodGetAllMedia(ctx, DB)
            callDownload(
                "",
                "",
                "",
                listMOdelGloble.responseData!!.playlistSongs!!,
                0,
                binding.llDownloads,
                binding.ivDownloads,
                ctx, activity!!, DB
            )
        }
        binding.searchView.onActionViewExpanded()
        searchEditText = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(ContextCompat.getColor(activity!!, R.color.dark_blue_gray))
        searchEditText.setHintTextColor(ContextCompat.getColor(activity!!, R.color.gray))
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
                    if (adpater2 != null) {
                        adpater2.filter.filter(search)
//                        SearchFlag = search
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
        val AudioPlayerFlagx = shared1x.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        val PlayerPositionx = shared1x.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
        val json = shared1x.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toString())
        var mainPlayModelList: ArrayList<MainPlayModel> = arrayListOf()
        if (!AudioPlayerFlagx.equals("0")) {
            if (!json.equals(gson.toString(), ignoreCase = true)) {
                val type = object : TypeToken<ArrayList<MainPlayModel>>() {}.type
                mainPlayModelList = gson.fromJson(json, type)
            }
            PlayerAudioId = mainPlayModelList[PlayerPositionx].id
        }
        if (isNetworkConnected(this)) {
            if (!MyDownloads.equals("1", true)) {
                showProgressBar(
                    binding.progressBar,
                    binding.progressBarHolder,
                    activity
                )
                val listCall: Call<PlaylistDetailsModel> =
                    APINewClient.getClient().getPlaylistDetail(CoUserID, PlaylistID)
                listCall.enqueue(object : Callback<PlaylistDetailsModel> {
                    override fun onResponse(
                        call: Call<PlaylistDetailsModel>,
                        response: Response<PlaylistDetailsModel>
                    ) {
                        hideProgressBar(
                            binding.progressBar,
                            binding.progressBarHolder,
                            activity
                        )
                        var listModel = PlaylistDetailsModel()
                        LocalBroadcastManager.getInstance(ctx)
                            .registerReceiver(listener1, IntentFilter("Reminder"))
                        try {
                            listModel = response.body()!!
                            listMOdelGloble = response.body()!!
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        try {
//                            if (listModel.responseData.getIsReminder().equals("0", ignoreCase = true) ||
//                                    listModel.responseData.getIsReminder().equals("", ignoreCase = true)) {
//                                binding.ivReminder.setColorFilter(ContextCompat.getColor(ctx, R.color.white), PorterDuff.Mode.SRC_IN)
//                            } else if (listModel.responseData.getIsReminder().equals("1", ignoreCase = true)) {
//                                binding.ivReminder.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), PorterDuff.Mode.SRC_IN)
//                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }


                        binding.tvTag.visibility = View.VISIBLE
                        binding.tvTag.setText(R.string.Audios_in_Playlist)


                        getDownloadData(ctx, DB)
                        callObserveMethodGetAllMedia(ctx, DB)
                        downloadPlaylistDetailsList = GetPlaylistDetail(
                            listModel.responseData!!.playlistSongs!!.size,
                            ctx,
                            DB
                        )

                        if (listModel.responseData!!.isReminder.equals("0", ignoreCase = true)
                            || listModel.responseData!!.isReminder.equals("", ignoreCase = true)
                        ) {
                            binding.tvReminder.text = ctx.getText(R.string.set_reminder)
                            binding.llReminder.setBackgroundResource(R.drawable.rounded_extra_theme_corner)
                        } else if (listModel.responseData!!.isReminder.equals(
                                "1",
                                ignoreCase = true
                            )
                        ) {
                            binding.tvReminder.text = ctx.getText(R.string.update_reminder)
                            binding.llReminder.setBackgroundResource(R.drawable.rounded_extra_dark_theme_corner)
                        } else if (listModel.responseData!!.isReminder.equals(
                                "2",
                                ignoreCase = true
                            )
                        ) {
                            binding.tvReminder.text = ctx.getText(R.string.update_reminder)
                            binding.llReminder.setBackgroundResource(R.drawable.rounded_extra_theme_corner)
                        }

                        binding.llReminder.setOnClickListener {
                            if (isNetworkConnected(ctx)) {
                                if (listModel.responseData!!.isReminder.equals(
                                        "0",
                                        ignoreCase = true
                                    )
                                    || listModel.responseData!!.isReminder.equals(
                                        "",
                                        ignoreCase = true
                                    )
                                ) {
                                    binding.tvReminder.text = ctx.getText(R.string.set_reminder)
                                    binding.llReminder.setBackgroundResource(R.drawable.rounded_extra_theme_corner)
                                    getReminderDay(
                                        ctx,
                                        activity,
                                        CoUserID,
                                        listModel.responseData!!.playlistID,
                                        listModel.responseData!!.playlistName,
                                        activity as FragmentActivity?,
                                        listModel.responseData!!.reminderTime,
                                        listModel.responseData!!.reminderDay
                                    )
                                } else if (listModel.responseData!!.isReminder.equals(
                                        "1",
                                        ignoreCase = true
                                    )
                                ) {
                                    binding.tvReminder.text = ctx.getText(R.string.update_reminder)
                                    binding.llReminder.setBackgroundResource(R.drawable.rounded_extra_dark_theme_corner)
                                    getReminderDay(
                                        ctx,
                                        activity,
                                        CoUserID,
                                        listModel.responseData!!.playlistID,
                                        listModel.responseData!!.playlistName,
                                        activity as FragmentActivity?,
                                        listModel.responseData!!.reminderTime,
                                        listModel.responseData!!.reminderDay
                                    )
                                } else if (listModel.responseData!!.isReminder.equals(
                                        "2",
                                        ignoreCase = true
                                    )
                                ) {
                                    binding.tvReminder.text = ctx.getText(R.string.update_reminder)
                                    binding.llReminder.setBackgroundResource(R.drawable.rounded_extra_theme_corner)
                                    getReminderDay(
                                        ctx,
                                        activity,
                                        CoUserID,
                                        listModel.responseData!!.playlistID,
                                        listModel.responseData!!.playlistName,
                                        activity as FragmentActivity?,
                                        listModel.responseData!!.reminderTime,
                                        listModel.responseData!!.reminderDay
                                    )
                                }
                            } else {
                                showToast(getString(R.string.no_server_found), activity)
                            }
                        }

                        //                            GetMedia();
//                            getMediaByPer(PlaylistId, SongListSize);
                        binding.rlSearch.visibility = View.VISIBLE
                        binding.llMore.visibility = View.VISIBLE
                        binding.llReminder.visibility = View.VISIBLE
                        binding.llMore.setOnClickListener {
                            if (isNetworkConnected(ctx)) {
//            handler2.removeCallbacks(UpdateSongTime2);
                                val fragmentManager1: FragmentManager =
                                    (ctx as FragmentActivity).supportFragmentManager

                                callPlaylistDetails(
                                    ctx,
                                    activity,
                                    CoUserID,
                                    PlaylistID,
                                    PlaylistName,
                                    fragmentManager1
                                )
                            } else {
                                showToast(getString(R.string.no_server_found), activity)
                            }
                        }
                        downloadPlaylistDetails = DownloadPlaylistDetails()
                        downloadPlaylistDetails.playlistID = listModel.responseData!!.playlistID
                        downloadPlaylistDetails.playlistName = listModel.responseData!!.playlistName
                        downloadPlaylistDetails.playlistDesc = listModel.responseData!!.playlistDesc
//                    downloadPlaylistDetails.isReminder = listModel.responseData!!.gsReminder
                        downloadPlaylistDetails.playlistMastercat =
                            listModel.responseData!!.playlistMastercat
                        downloadPlaylistDetails.playlistSubcat =
                            listModel.responseData!!.playlistSubcat
                        downloadPlaylistDetails.playlistImage =
                            listModel.responseData!!.playlistImage
                        downloadPlaylistDetails.playlistImageDetails =
                            listModel.responseData!!.playlistImageDetail
                        downloadPlaylistDetails.totalAudio = listModel.responseData!!.totalAudio
                        downloadPlaylistDetails.totalDuration =
                            listModel.responseData!!.totalDuration
                        downloadPlaylistDetails.totalhour = listModel.responseData!!.totalhour
                        downloadPlaylistDetails.totalminute = listModel.responseData!!.totalminute
                        downloadPlaylistDetails.created = listModel.responseData!!.created

                        setData(listModel.responseData)
                    }

                    override fun onFailure(call: Call<PlaylistDetailsModel>, t: Throwable) {
                        hideProgressBar(
                            binding.progressBar,
                            binding.progressBarHolder,
                            activity
                        )
                    }
                })
            } else {
                GetPlaylistDetail2(ctx, DB, CoUserID!!, PlaylistID!!)
            }
        } else {
            GetPlaylistDetail2(ctx, DB, CoUserID!!, PlaylistID!!)
            showToast(getString(R.string.no_server_found), activity)
        }
        val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
        val AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        val MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
        val PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
        val PlayerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
        if (MyDownloads.equals("1", ignoreCase = true)) {
            if (AudioPlayerFlag.equals("Downloadlist", ignoreCase = true) && MyPlaylist.equals(
                    PlaylistID,
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
                    PlaylistID,
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

    private fun setData(listModel: PlaylistDetailsModel.ResponseData?) {
        MyCreated = listModel!!.created
        val measureRatio4 = measureRatio(ctx, 0f, 5f, 4.1f, 1f, 0f)
        binding.ivBanner.layoutParams.height =
            (measureRatio4.height * measureRatio4.ratio).toInt()
        binding.ivBanner.layoutParams.width =
            (measureRatio4.widthImg * measureRatio4.ratio).toInt()

        val measureRatio = measureRatio(ctx, 0f, 5f, 4.1f, 1f, 0f)
        binding.ivCloudBanner.layoutParams.height =
            (measureRatio.height * measureRatio.ratio).toInt()
        binding.ivCloudBanner.layoutParams.width =
            (measureRatio.widthImg * measureRatio.ratio).toInt()

        val measureRatio1 = measureRatio(ctx, 0f, 5f, 4.1f, 1f, 0f)
        binding.llPlayer.layoutParams.height =
            (measureRatio1.height * measureRatio1.ratio).toInt()
        binding.llPlayer.layoutParams.width =
            (measureRatio1.widthImg * measureRatio1.ratio).toInt()

        val measureRatio2 = measureRatio(ctx, 0f, 5f, 4.1f, 1f, 0f)
        binding.ivTransBanner.layoutParams.height =
            (measureRatio2.height * measureRatio2.ratio).toInt()
        binding.ivTransBanner.layoutParams.width =
            (measureRatio2.widthImg * measureRatio2.ratio).toInt()
        binding.ivTransBanner.setImageResource(R.drawable.rounded_light_app_theme)
        if (listModel.playlistName.equals("", ignoreCase = true) ||
            listModel.playlistName == null
        ) {
            binding.tvPlayListName.text = R.string.My_Playlist.toString()
        } else {
            binding.tvPlayListName.text = listModel.playlistName
        }
        binding.tvDescription.text = listModel.playlistDesc
        try {
//            if(!MyDownloads.equals("1")) {
            if (isNetworkConnected(ctx)) {
                if (!listModel.created.equals("2")) {
                    if (!listModel.playlistImageDetail.equals("")) {
                        Glide.with(ctx).load(listModel.playlistImageDetail).thumbnail(0.05f)
                            .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                            .into(binding.ivBanner)
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
            }
//            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
//        binding.tvPlaylist.setText("Playlist")

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
                binding.tvReminder.setTextColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.light_gray
                    )
                )
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
                binding.llReminder.visibility = View.VISIBLE
//                binding.llPlayPause.setVisibility(View.INVISIBLE)
//                binding.llListing.setVisibility(View.GONE)

                binding.llPlayPause.visibility = View.VISIBLE
            } else {
                binding.llAddAudio.visibility = View.GONE
                binding.rlSearch.visibility = View.VISIBLE
                binding.tvReminder.setTextColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.white
                    )
                )
                binding.llReminder.isEnabled = true
                binding.llReminder.isClickable = true
                binding.tvTag.visibility = View.VISIBLE
                binding.llDownloads.visibility = View.VISIBLE
                binding.llReminder.visibility = View.VISIBLE
                if (MyDownloads.equals("1", ignoreCase = true)) {
//                    binding.llDelete.setVisibility(View.VISIBLE)
                    searchEditText.hint = "Search for audio"
                    binding.tvSearch.hint = "Search for audio"
                    binding.llReminder.visibility = View.INVISIBLE
                    binding.llDelete.visibility = View.VISIBLE
                    binding.llMore.visibility = View.GONE
                    binding.llDownloads.visibility = View.GONE
                    binding.rlSearch.visibility = View.VISIBLE
                    adpater2 = PlayListsAdpater2(
                        listModel.playlistSongs!!,
                        ctx,
                        CoUserID,
                        listModel.created,
                        binding,
                        activity!!,
                        PlaylistID,
                        PlaylistName,
                        MyDownloads
                    )
                    binding.rvPlayLists2.adapter = adpater2
                    binding.rvPlayLists1.visibility = View.GONE
                    binding.rvPlayLists2.visibility = View.VISIBLE
                    binding.ivDownloads.setImageResource(R.drawable.ic_download_done_icon)
                    binding.ivDownloads.setColorFilter(
                        ContextCompat.getColor(activity!!, R.color.white),
                        PorterDuff.Mode.SRC_IN
                    )
                    enableDisableDownload(false, "orange")
//                    binding.ivReminder.setColorFilter(activity.resources.getColor(R.color.gray), PorterDuff.Mode.SRC_IN)
                } else {
                    if (listModel.created.equals("1", ignoreCase = true)) {
                        binding.llSuggested.visibility = View.GONE
                        searchEditText.setHint(R.string.playlist_or_audio_search)
                        binding.tvSearch.setHint(R.string.playlist_or_audio_search)
                        binding.tvSearch.visibility = View.VISIBLE
                        binding.searchView.visibility = View.GONE
                        binding.llDelete.visibility = View.GONE
                        binding.rvPlayLists1.visibility = View.VISIBLE
                        binding.rvPlayLists2.visibility = View.GONE
                        adpater = PlayListsAdpater(
                            listModel.playlistSongs!!,
                            ctx,
                            CoUserID,
                            listModel.created,
                            binding,
                            activity!!,
                            this,
                            PlaylistID,
                            PlaylistName
                        )
                        val callback: ItemTouchHelper.Callback = ItemMoveCallback(adpater)
                        touchHelper = ItemTouchHelper(callback)
                        touchHelper!!.attachToRecyclerView(binding.rvPlayLists1)
                        binding.rvPlayLists1.adapter = adpater
//                                LocalBroadcastManager.getInstance(ctx)
//                                        .registerReceiver(listener1, IntentFilter("DownloadProgress"))
                    } else if (listModel.created.equals("2")) {
                        binding.llSuggested.visibility = View.VISIBLE

                        searchEditText.hint = "Search for audio"
                        binding.tvSearch.hint = "Search for audio"
                        binding.tvSearch.visibility = View.GONE
                        binding.llDelete.visibility = View.GONE
                        binding.searchView.visibility = View.VISIBLE
                        adpater2 = PlayListsAdpater2(
                            listModel.playlistSongs!!,
                            ctx,
                            CoUserID,
                            listModel.created,
                            binding,
                            activity!!,
                            PlaylistID,
                            PlaylistName,
                            MyDownloads
                        )
                        binding.rvPlayLists1.visibility = View.GONE
                        binding.rvPlayLists2.visibility = View.VISIBLE
                        binding.rvPlayLists2.adapter = adpater2
                    } else {
                        binding.llSuggested.visibility = View.GONE
                        searchEditText.hint = "Search for audio"
                        binding.tvSearch.hint = "Search for audio"
                        binding.tvSearch.visibility = View.GONE
                        binding.searchView.visibility = View.VISIBLE
                        binding.llDelete.visibility = View.GONE
                        adpater2 = PlayListsAdpater2(
                            listModel.playlistSongs!!,
                            ctx,
                            CoUserID,
                            listModel.created,
                            binding,
                            activity!!,
                            PlaylistID,
                            PlaylistName,
                            MyDownloads
                        )
                        binding.rvPlayLists1.visibility = View.GONE
                        binding.rvPlayLists2.visibility = View.VISIBLE
                        binding.rvPlayLists2.adapter = adpater2
                    }
                }
//                } catch (e: java.lang.Exception) {
//                    e.printStackTrace()
//                }
                LocalBroadcastManager.getInstance(ctx)
                    .registerReceiver(listener, IntentFilter("play_pause_Action"))
            }
        }
    }

    override fun requestDrag(viewHolder: RecyclerView.ViewHolder?) {
        touchHelper!!.startDrag(viewHolder!!)
    }

    class AreaOfFocusAdapter(
        var binding: ActivityMyPlaylistListingBinding,
        var ctx: Context,
        var selectedCategoriesName: java.util.ArrayList<String>
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

    class PlayListsAdpater(
        var listModel: ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>,
        var ctx: Context,
        var CoUserID: String?,
        var created: String?,
        var binding: ActivityMyPlaylistListingBinding,
        var activity: Activity,
        var startDragListener: StartDragListener,
        var PlaylistID: String?,
        var PlaylistName: String?
    ) : RecyclerView.Adapter<PlayListsAdpater.MyViewHolder>(), ItemTouchHelperContract {

        var changedAudio = arrayListOf<String>()

        inner class MyViewHolder(var binding: MyplaylistSortingNewBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: MyplaylistSortingNewBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.myplaylist_sorting_new,
                parent,
                false
            )
            return MyViewHolder(v)
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

            holder.binding.tvTitle.text = listModel[position].name
            holder.binding.tvTime.text = listModel[position].audioDuration
            val measureRatio = measureRatio(ctx, 0f, 1f, 1f, 0.13f, 0f)
            holder.binding.ivRestaurantImage.layoutParams.height =
                (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width =
                (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            holder.binding.ivBackgroundImage.layoutParams.height =
                (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivBackgroundImage.layoutParams.width =
                (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivBackgroundImage.scaleType = ImageView.ScaleType.FIT_XY

            holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg)

            val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
            val MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
            if (AudioPlayerFlag.equals("playlist", ignoreCase = true) && MyPlaylist.equals(
                    PlaylistID,
                    ignoreCase = true
                )
            ) {
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
                }
                //                    handler3.postDelayed(UpdateSongTime3, 500);
            } else {
                holder.binding.equalizerview.visibility = View.GONE
                holder.binding.llMainLayout.setBackgroundResource(R.color.white)
                holder.binding.ivBackgroundImage.visibility = View.GONE
                //                    handler3.removeCallbacks(UpdateSongTime3);
            }
            Glide.with(ctx).load(listModel[position].imageFile).thumbnail(0.05f)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                .into(holder.binding.ivRestaurantImage)

            val into = Glide.with(ctx).load(R.drawable.ic_image_bg).thumbnail(0.05f)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                .into(holder.binding.ivBackgroundImage)
            holder.binding.llMainLayout.setOnClickListener {
                try {
                    MyPlaylistListingActivity().callMainPlayer(
                        holder.absoluteAdapterPosition,
                        "Created",
                        listModel,
                        ctx,
                        activity,
                        listModel[0].playlistID!!,
                        PlaylistName!!,
                        created,
                        "0"
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding.llPlayPause.setOnClickListener {
//                MyPlaylistListingActivity().callMainPlayer(position, "", listModel, ctx,  )
                if (isPlayPlaylist == 1) {
                    player.playWhenReady = false
                    isPlayPlaylist = 2
                    binding.llPlay.visibility = View.VISIBLE
                    binding.llPause.visibility = View.GONE
                } else if (isPlayPlaylist == 2) {
                    if (player != null) {
                        if (PlayerAudioId.equals(
                                listModel[listModel.size - 1].id,
                                ignoreCase = true
                            )
                            && player.duration - player.currentPosition <= 20
                        ) {
                            val shared =
                                ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
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
                } else {
                    MyPlaylistListingActivity().callMainPlayer(
                        0,
                        "Created",
                        listModel,
                        ctx,
                        activity,
                        listModel[0].playlistID!!,
                        PlaylistName!!,
                        created, "0"
                    )
                    binding.llPlay.visibility = View.GONE
                    binding.llPause.visibility = View.VISIBLE
                }
                notifyDataSetChanged()
            }
//            Glide.with(ctx).load(R.drawable.ic_image_bg).thumbnail(0.05f)
//                    .apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivBackgroundImage)
            try {
                holder.binding.llRemove.setOnClickListener {
                    val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                    val AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                    val MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                    if (AudioPlayerFlag.equals("playlist", ignoreCase = true) && MyPlaylist.equals(
                            PlaylistID,
                            ignoreCase = true
                        )
                    ) {
                        if (isDisclaimer == 1) {
                            showToast(
                                "The audio shall remove after the disclaimer",
                                activity
                            )
                        } else {
                            if (AudioPlayerFlag.equals(
                                    "playlist",
                                    ignoreCase = true
                                ) && MyPlaylist.equals(
                                    PlaylistID,
                                    ignoreCase = true
                                ) && listModel.size == 1
                            ) {
                                showToast(
                                    "Currently you play this playlist, you can't remove last audio",
                                    activity
                                )
                            } else {
                                callRemove(
                                    listModel[holder.absoluteAdapterPosition].id.toString(),
                                    listModel,
                                    holder.absoluteAdapterPosition,
                                    ctx,
                                    activity,
                                    PlaylistID.toString()
                                )
                            }
                        }
                    } else {
                        if (AudioPlayerFlag.equals(
                                "playlist",
                                ignoreCase = true
                            ) && MyPlaylist.equals(
                                PlaylistID,
                                ignoreCase = true
                            ) && listModel.size == 1
                        ) {
                            showToast(
                                "Currently you play this playlist, you can't remove last audio",
                                activity
                            )
                        } else {
                            callRemove(
                                listModel[position].id.toString(),
                                listModel,
                                holder.absoluteAdapterPosition,
                                ctx,
                                activity,
                                PlaylistID.toString()
                            )
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

        private fun callRemove(
            id: String,
            listModel: ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>,
            position: Int,
            ctx: Context,
            activity: Activity,
            PlaylistID: String
        ) {
            var CoUserID: String? = ""
            val shared =
                this.ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
            CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
            if (isNetworkConnected(this.ctx)) {
                showProgressBar(
                    binding.progressBar,
                    binding.progressBarHolder,
                    this.activity
                )
                val listCall = APINewClient.getClient().RemoveAudio(CoUserID, id, PlaylistID)
                listCall.enqueue(object : Callback<SucessModel?> {
                    override fun onResponse(
                        call: Call<SucessModel?>,
                        response: Response<SucessModel?>
                    ) {
//                        try {
                        if (response.isSuccessful) {
////                            handler2.removeCallbacks(UpdateSongTime2);
//                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder,activity)
                            val listModel1: SucessModel = response.body()!!
                            listModel.removeAt(position)
                            if (listModel.isEmpty()) {
                                try {
                                    MyPlaylistListingActivity().enableDisableDownload(false, "gray")
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            val shared1 = ctx.getSharedPreferences(
                                CONSTANTS.PREF_KEY_PLAYER,
                                MODE_PRIVATE
                            )
                            val AudioPlayerFlag =
                                shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                            val MyPlaylist =
                                shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                            val PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
                            var PlayerPosition: Int =
                                shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                            if (AudioPlayerFlag.equals(
                                    "playlist",
                                    ignoreCase = true
                                ) && MyPlaylist.equals(
                                    listModel[position].playlistID,
                                    ignoreCase = true
                                )
                            ) {

                                if (player != null) {
                                    player.removeMediaItem(position)
                                }
                                if (PlayerPosition == position && position < listModel.size - 1) {
//                                            pos = pos + 1;
                                    if (isDisclaimer == 1) {
//                                    BWSApplication.showToast("The audio shall remove after the disclaimer", getActivity());
                                    } else {
                                        if (player != null) {
//                                            player.seekTo(pos, 0);
                                            player.playWhenReady = true
                                            saveToPref(PlayerPosition, listModel)
                                        } else {
                                            MyPlaylistListingActivity().callMainPlayer(
                                                PlayerPosition,
                                                "Created",
                                                listModel,
                                                ctx,
                                                activity,
                                                listModel[0].playlistID!!,
                                                PlaylistName!!,
                                                created, "0"
                                            )
                                        }
                                    }
                                } else if (PlayerPosition == position && position == listModel.size - 1) {
                                    PlayerPosition = 0
                                    if (isDisclaimer == 1) {
//                                    BWSApplication.showToast("The audio shall remove after the disclaimer", getActivity());
                                    } else {
                                        if (player != null) {
//                                            player.seekTo(pos, 0);
                                            player.playWhenReady = true
                                            saveToPref(PlayerPosition, listModel)
                                        } else {
                                            MyPlaylistListingActivity().callMainPlayer(
                                                PlayerPosition,
                                                "Created",
                                                listModel,
                                                ctx,
                                                activity,
                                                listModel[0].playlistID!!,
                                                PlaylistName!!,
                                                created, "0"
                                            )
                                        }
                                    }
                                } else if (PlayerPosition < position && PlayerPosition < listModel.size - 1) {
                                    saveToPref(PlayerPosition, listModel)
                                } else if (PlayerPosition < position && PlayerPosition == listModel.size - 1) {
                                    saveToPref(PlayerPosition, listModel)
                                } else if (PlayerPosition > position && PlayerPosition == listModel.size) {
                                    PlayerPosition -= 1
                                    saveToPref(PlayerPosition, listModel)
                                }
                            }
                            localIntent = Intent("Reminder")
                            localBroadcastManager = LocalBroadcastManager.getInstance(ctx)
                            localIntent.putExtra("MyReminder", "update")
                            localBroadcastManager.sendBroadcast(localIntent)
                            showToast(listModel1.responseMessage, activity)
                        }
//                        } catch (e: java.lang.Exception) {
//                            e.printStackTrace()
//                        }
                    }

                    private fun saveToPref(
                        playerPosition: Int,
                        listModel: ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>
                    ) {
                        val shared =
                            ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
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
                        hideProgressBar(
                            binding.progressBar,
                            binding.progressBarHolder,
                            activity
                        )
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
                changedAudio.add(listModel[i].id.toString())
            }
            callDragApi()
            notifyItemMoved(fromPosition, toPosition)

            /*  val p = Properties()
            val shared =
                    ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
            val USERID = shared.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
            CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")
            p.putValue("userId",USERID )
            p.putValue("coUserId", CoUserID)
            p.putValue("playlistId", PlaylistID)
            p.putValue("playlistName", PlaylistName)
            p.putValue("playlistDescription", "")
            if (created.equals("1", ignoreCase = true)) {
                p.putValue("playlistType", "Created")
            } else if (created.equals("0", ignoreCase = true)) {
                p.putValue("playlistType", "Default")
            }

            if (Totalhour.equals("", ignoreCase = true)) {
                p.putValue("playlistDuration", "0h " + Totalminute + "m")
            } else if (Totalminute.equals("", ignoreCase = true)) {
                p.putValue("playlistDuration", Totalhour + "h 0m")
            } else {
                p.putValue("playlistDuration", Totalhour + "h " + Totalminute + "m")
            }
            p.putValue("audioCount", TotalAudio)
            p.putValue("source", "Your Created")
            p.putValue("playerType", "Mini")
            p.putValue("audioService", APP_SERVICE_STATUS)
            p.putValue("sound", hundredVolume.toString())
            p.putValue("audioId", listModelList.get(toPosition).getID())
            p.putValue("audioName", listModelList.get(toPosition).getName())
            p.putValue("masterCategory", listModelList.get(toPosition).getAudiomastercat())
            p.putValue("subCategory", listModelList.get(toPosition).getAudioSubCategory())
            p.putValue("audioSortPosition", fromPosition)
            p.putValue("audioSortPositionNew", toPosition)
            addToSegment("Playlist Audio Sorted", p, CONSTANTS.track)*/
            val shared: SharedPreferences =
                ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
            var pos = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
            if (AudioFlag.equals("playList", ignoreCase = true)) {
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
                    val shareddd: SharedPreferences =
                        ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
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
                    val listCall = APINewClient.getClient()
                        .SortAudio(CoUserID, PlaylistID, TextUtils.join(",", changedAudio))
                    listCall.enqueue(object : Callback<SucessModel?> {
                        override fun onResponse(
                            call: Call<SucessModel?>,
                            response: Response<SucessModel?>
                        ) {
                            if (response.isSuccessful) {
                                val listModel = response.body()
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

    class PlayListsAdpater2(
        var listModel: ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>,
        var ctx: Context,
        var CoUserID: String?,
        var created: String?,
        var binding: ActivityMyPlaylistListingBinding,
        var activity: Activity,
        var PlaylistID: String?,
        var PlaylistName: String?,
        var MyDownloads: String?
    ) : RecyclerView.Adapter<PlayListsAdpater2.MyViewHolder>(), Filterable {

        private var listFilterData: ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong> =
            listModel

        inner class MyViewHolder(var binding: MyPlaylistLayoutBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: MyPlaylistLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.my_playlist_layout,
                parent,
                false
            )
            return MyViewHolder(v)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//            searchEditText.setHint("Search for audio")
//            binding.tvSearch.setHint("Search for audio")

            val mData: List<PlaylistDetailsModel.ResponseData.PlaylistSong> = listFilterData
            holder.binding.tvTitleA.text = mData[position].name
            holder.binding.tvTimeA.text = mData[position].audioDuration
//            binding.tvSearch.setVisibility(View.GONE)
            binding.searchView.visibility = View.VISIBLE
            val measureRatio = measureRatio(ctx, 0f, 1f, 1f, 0.13f, 0f)
            holder.binding.ivRestaurantImage.layoutParams.height =
                (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width =
                (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            holder.binding.ivBackgroundImage.layoutParams.height =
                (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivBackgroundImage.layoutParams.width =
                (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivBackgroundImage.scaleType = ImageView.ScaleType.FIT_XY
            holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg)
            Glide.with(ctx).load(mData[position].imageFile).thumbnail(0.05f)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                .into(holder.binding.ivRestaurantImage)

            Glide.with(ctx).load(R.drawable.ic_image_bg).thumbnail(0.05f)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                .into(holder.binding.ivBackgroundImage)


            val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
            val MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
            if (MyDownloads.equals("1", ignoreCase = true)) {
                if (AudioPlayerFlag.equals("Downloadlist", ignoreCase = true) && MyPlaylist.equals(
                        PlaylistID,
                        ignoreCase = true
                    )
                ) {
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
                    }
                    //                    handler3.postDelayed(UpdateSongTime3, 500);
                } else {
                    holder.binding.equalizerview.visibility = View.GONE
                    holder.binding.llMainLayout.setBackgroundResource(R.color.white)
                    holder.binding.ivBackgroundImage.visibility = View.GONE
                    //                    handler3.removeCallbacks(UpdateSongTime3);
                }
            } else {
                if (AudioPlayerFlag.equals("playlist", ignoreCase = true) && MyPlaylist.equals(
                        PlaylistID,
                        ignoreCase = true
                    )
                ) {
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
                    }
                    //                    handler3.postDelayed(UpdateSongTime3, 500);
                } else {
                    holder.binding.equalizerview.visibility = View.GONE
                    holder.binding.llMainLayout.setBackgroundResource(R.color.white)
                    holder.binding.ivBackgroundImage.visibility = View.GONE
                    //                    handler3.removeCallbacks(UpdateSongTime3);
                }
            }

            holder.binding.llMore.setOnClickListener {
                if (isNetworkConnected(ctx)) {
                    callAudioDetails(
                        mData[position].id, ctx, activity, CoUserID, "playlist",
                        arrayListOf<DownloadAudioDetails>(),
                        arrayListOf<ViewAllAudioListModel.ResponseData.Detail>(),
                        mData, arrayListOf<MainPlayModel>(), position
                    )
                } else {
                    showToast(activity.getString(R.string.no_server_found), activity)
                }
            }

            holder.binding.llMainLayout.setOnClickListener {
                var playfrom: String = ""
                if (created.equals("2")) {
                    playfrom = "suggested"
                }
                MyPlaylistListingActivity().callMainPlayer(
                    position,
                    playfrom,
                    listFilterData,
                    ctx,
                    activity,
                    PlaylistID!!,
                    PlaylistName!!,
                    created, MyDownloads
                )
            }
            binding.llPlayPause.setOnClickListener {
//                MyPlaylistListingActivity().callMainPlayer(position, "", listModel, ctx, activity)
                if (isPlayPlaylist == 1) {
                    player.playWhenReady = false
                    isPlayPlaylist = 2
                    binding.llPlay.visibility = View.VISIBLE
                    binding.llPause.visibility = View.GONE
                } else if (isPlayPlaylist == 2) {
                    if (player != null) {
                        if (PlayerAudioId.equals(mData[mData.size - 1].id, ignoreCase = true)
                            && player.duration - player.currentPosition <= 20
                        ) {
                            val shared =
                                ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                            val editor = shared.edit()
                            editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                            editor.commit()
                            player.seekTo(0, 0)
                            player.playWhenReady = true
                        } else {
                            player.playWhenReady = true
                        }
                    }
                    isPlayPlaylist = 1
                    binding.llPlay.visibility = View.GONE
                    binding.llPause.visibility = View.VISIBLE
                } else {
                    PlayerAudioId = mData[0].id
                    var playfrom: String = ""
                    if (created.equals("2")) {
                        playfrom = "suggested"
                    }
                    MyPlaylistListingActivity().callMainPlayer(
                        0,
                        playfrom,
                        listModel,
                        ctx,
                        activity,
                        listModel[0].playlistID!!,
                        PlaylistName!!,
                        created, MyDownloads
                    )
                    binding.llPlay.visibility = View.GONE
                    binding.llPause.visibility = View.VISIBLE
                }
                notifyDataSetChanged()
            }
            binding.llDelete.setOnClickListener {
                val shared: SharedPreferences =
                    ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                val pID = shared.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                if (AudioFlag.equals("Downloadlist", ignoreCase = true) && pID.equals(
                        PlaylistID,
                        ignoreCase = true
                    )
                ) {
                    showToast(
                        "Unable to remove as this playlist is in player right now",
                        activity
                    )
                } else {
                    val dialog = Dialog(ctx)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(R.layout.custom_popup_layout)
                    dialog.window!!.setBackgroundDrawable(
                        ColorDrawable(
                            ContextCompat.getColor(
                                activity,
                                R.color.dark_blue_gray
                            )
                        )
                    )
                    dialog.window!!.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    val tvGoBack = dialog.findViewById<TextView>(R.id.tvGoBack)
                    val tvHeader = dialog.findViewById<TextView>(R.id.tvHeader)
                    val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
                    val Btn = dialog.findViewById<Button>(R.id.Btn)
                    tvTitle.text = "Remove playlist"
                    tvHeader.text =
                        "Are you sure you want to remove the $PlaylistName from downloads??"
                    Btn.text = "Confirm"
                    dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            dialog.dismiss()
                        }
                        false
                    }
                    val shared = ctx.getSharedPreferences(
                        CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER,
                        AppCompatActivity.MODE_PRIVATE
                    )
                    CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
                    Btn.setOnClickListener {
                        getDeleteDownloadData(ctx)
                        GetPlaylistMedia(PlaylistID!!, ctx, DB, CoUserID)
                        dialog.dismiss()
                        activity.finish()
                    }
                    tvGoBack.setOnClickListener { dialog.dismiss() }
                    dialog.show()
                    dialog.setCancelable(false)
                }
            }

        }

        fun getDeleteDownloadData(ctx: Context) {
//        try {
            val fileNameList: List<String>
            val fileNameList1: List<String>
            val audioFile: List<String>
            val playlistDownloadId: List<String>
            val sharedy: SharedPreferences =
                ctx.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE)
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
                    if (playlistDownloadId.contains(PlaylistID)) {
                        for (i in 1 until fileNameList1.size - 1) {
                            if (playlistDownloadId[i].equals(PlaylistID, ignoreCase = true)) {
                                fileNameList.drop(i)
                                audioFile.drop(i)
                                playlistDownloadId.drop(i)
                            }
                        }
                        val shared: SharedPreferences =
                            ctx.getSharedPreferences(
                                CONSTANTS.PREF_KEY_DownloadPlaylist,
                                MODE_PRIVATE
                            )
                        val editor = shared.edit()
                        val nameJson = gson.toJson(fileNameList)
                        val urlJson = gson.toJson(audioFile)
                        val playlistIdJson = gson.toJson(playlistDownloadId)
                        editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson)
                        editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson)
                        editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson)
                        editor.commit()
                        if (fileNameList[0].equals(
                                DownloadMedia.filename,
                                ignoreCase = true
                            ) && playlistDownloadId[0].equals(PlaylistID, ignoreCase = true)
                        ) {
                            PRDownloader.cancel(DownloadMedia.downloadIdOne)
                            DownloadMedia.filename = ""
                        }
                    }
                }
            }
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//            Log.e("Download Playlist ", "Download Playlist remove issue" + e.message)
//        }
        }

        fun GetPlaylistMedia(
            playlistID: String,
            ctx: Context,
            DB: AudioDatabase,
            CoUserID: String?
        ) {
            DB.taskDao().getAllAudioByPlaylist1(playlistID, CoUserID)
                .observe(ctx as (LifecycleOwner), { audioList: List<DownloadAudioDetails> ->
                    deleteDownloadFile(ctx, playlistID, DB, CoUserID)
                    if (audioList.isNotEmpty()) {
                        GetSingleMedia(
                            audioList[0].audioFile,
                            ctx,
                            playlistID,
                            audioList,
                            0, DB, CoUserID
                        )
                    }
                })
        }

        private fun deleteDownloadFile(
            ctx: Context,
            PlaylistId: String,
            DB: AudioDatabase,
            CoUserID: String?
        ) {
            AudioDatabase.databaseWriteExecutor.execute {
                DB.taskDao().deleteByPlaylistId(PlaylistId, CoUserID)
            }
            deletePlaylist(PlaylistId, ctx, DB, CoUserID)
        }

        private fun deletePlaylist(
            playlistId: String,
            ctx: Context,
            DB: AudioDatabase,
            CoUserID: String?
        ) {
            AudioDatabase.databaseWriteExecutor.execute {
                DB.taskDao().deletePlaylist(playlistId, CoUserID)
            }
        }

        fun GetSingleMedia(
            AudioFile: String?,
            ctx: Context,
            playlistID: String?,
            audioList: List<DownloadAudioDetails>,
            i: Int, DB: AudioDatabase, CoUserID: String?
        ) {
            DB.taskDao().getLastIdByuId1(AudioFile, CoUserID)
                .observe(ctx as (LifecycleOwner), { audioList1: List<DownloadAudioDetails> ->
                    try {
                        if (audioList1.isNotEmpty()) {
                            if (audioList1.size == 1) {
                                FileUtils.deleteDownloadedFile(ctx, audioList1[0].name)
                            }
                        }
                        if (i < audioList.size - 1) {
                            GetSingleMedia(
                                audioList[i + 1].audioFile,
                                ctx.applicationContext,
                                playlistID,
                                audioList,
                                i + 1, DB, CoUserID
                            )
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
                    if (charString.isEmpty()) {
                        listFilterData = listModel
                    } else {
                        val filteredList =
                            ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>()
                        for (row in listModel) {
                            if (row.name!!.toLowerCase(Locale.ROOT)
                                    .contains(charString.toLowerCase(Locale.ROOT))
                            ) {
                                filteredList.add(row)
                            }
                        }
                        listFilterData = filteredList
                    }
                    filterResults.values = listFilterData
                    return filterResults
                }

                override fun publishResults(
                    charSequence: CharSequence,
                    filterResults: FilterResults
                ) {
                    if (listFilterData.isEmpty()) {
                        binding.llError.visibility = View.VISIBLE
                        binding.tvTag.visibility = View.GONE
                        binding.rvPlayLists2.visibility = View.GONE
//                        binding.tvFound.setText("Couldn't find '" + SearchFlag + "'. Try searching again");
                        binding.tvFound.setText("Please use another term and try searching again")
//                        Log.e("search", SearchFlag)
                    } else {
                        binding.llError.visibility = View.GONE
                        binding.tvTag.visibility = View.VISIBLE
                        binding.rvPlayLists2.visibility = View.VISIBLE
                        listFilterData =
                            filterResults.values as ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    fun callMainPlayer(
        position: Int,
        view: String?,
        listModel: List<PlaylistDetailsModel.ResponseData.PlaylistSong>,
        ctx: Context,
        act: Activity,
        playlistID: String,
        playlistName: String,
        created: String?,
        MyDownloads: String?
    ) {
        val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
        val AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        val MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
        val PlayerPosition: Int = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
        val shared12 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
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
                        callMyPlayer(ctx, act)
                        showToast(
                            "The audio shall start playing after the disclaimer",
                            activity
                        )
                    } else {
                        if (player != null) {
                            if (position != PlayerPosition) {
                                player.seekTo(position, 0)
                                player.playWhenReady = true
                                PlayerAudioId = listModel[position].id
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
                            callPlayer(
                                position,
                                view,
                                listModel,
                                ctx,
                                act,
                                playlistID,
                                playlistName,
                                created,
                                true, MyDownloads
                            )
                        }
                    }
                } else {
                    val listModelList2 =
                        arrayListOf<PlaylistDetailsModel.ResponseData.PlaylistSong>()
                    listModelList2.addAll(listModel)
                    val gson = Gson()
                    val DisclimerJson =
                        shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
                    val type =
                        object : TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio?>() {}.type
                    val arrayList =
                        gson.fromJson<HomeScreenModel.ResponseData.DisclaimerAudio>(
                            DisclimerJson,
                            type
                        )
                    val mainPlayModel = PlaylistDetailsModel.ResponseData.PlaylistSong()
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
                    callPlayer(
                        position,
                        view,
                        listModelList2,
                        ctx,
                        act,
                        playlistID,
                        playlistName,
                        created,
                        audioc, MyDownloads
                    )
                }
            } else {
                getAllCompletedMedia(
                    AudioPlayerFlag!!,
                    MyPlaylist!!,
                    0,
                    ctx,
                    playlistID,
                    playlistName
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
                    callMyPlayer(ctx, act)
                    showToast(
                        "The audio shall start playing after the disclaimer",
                        this@MyPlaylistListingActivity
                    )
                } else {
                    if (player != null) {
                        if (position != PlayerPosition) {
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
                        callPlayer(
                            position, view, listModel, ctx, act, playlistID,
                            playlistName, created, true, MyDownloads
                        )
                    }
                }
            } else {
                val listModelList2 = arrayListOf<PlaylistDetailsModel.ResponseData.PlaylistSong>()
                listModelList2.addAll(listModel)
                val gson = Gson()
                val DisclimerJson =
                    shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
                val type =
                    object : TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio?>() {}.type
                val arrayList =
                    gson.fromJson<HomeScreenModel.ResponseData.DisclaimerAudio>(DisclimerJson, type)
                val mainPlayModel = PlaylistDetailsModel.ResponseData.PlaylistSong()
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
                callPlayer(
                    position, view, listModelList2, ctx, act, playlistID,
                    playlistName, created, audioc, MyDownloads
                )
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
        listModel: List<PlaylistDetailsModel.ResponseData.PlaylistSong>,
        ctx: Context,
        act: Activity,
        playlistID: String,
        playlistName: String,
        created: String?,
        audioc: Boolean, MyDownloads: String?
    ) {
        if (audioc) {
            callNewPlayerRelease()
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
        editor.putString(CONSTANTS.PREF_KEY_PlayFrom, view)
        editor.apply()
        audioClick = audioc
        callMyPlayer(ctx, act)
    }


    private fun removeobserver(DB: AudioDatabase) {
        DB.taskDao()
            .getPlaylist1(PlaylistID, CoUserID).removeObserver {}
    }

    private fun GetPlaylistDetail(
        SongListSize: Int,
        ctx: Context, DB: AudioDatabase
    ): ArrayList<DownloadPlaylistDetails> {
//        try {
        DB.taskDao()
            .getPlaylist1(PlaylistID, CoUserID)
            .observe(ctx as (LifecycleOwner), { audioList: List<DownloadPlaylistDetails> ->
                downloadPlaylistDetailsList = ArrayList()
                downloadPlaylistDetailsList.addAll(audioList)
                when {
                    downloadPlaylistDetailsList.isNotEmpty() -> {
                        getMediaByPer(PlaylistID!!, SongListSize, ctx, DB)
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
            })
//                })
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
        return downloadPlaylistDetailsList
    }

    private fun getMediaByPer(
        PlaylistId: String,
        totalAudio: Int,
        ctx: Context,
        DB: AudioDatabase
    ) {
//        try {
        DB.taskDao().getCountDownloadProgress1("Complete", PlaylistId, CoUserID)
            .observe(ctx as (LifecycleOwner), { countx: List<DownloadPlaylistDetails?> ->
                count = countx.size
                //                if (downloadPlaylistDetailsList.size() != 0) {
                if (count <= totalAudio) {
                    if (count == totalAudio) {
                        binding.pbProgress.visibility = View.GONE
                        binding.ivDownloads.visibility = View.VISIBLE

                        enableDisableDownload(false, "orange")
                        DB.taskDao().getCountDownloadProgress1("Complete", PlaylistId, CoUserID)
                            .removeObserver {}
                    } else {
                        val progressPercent: Long = (count * 100 / totalAudio).toLong()
                        val downloadProgress1 = progressPercent.toInt()
                        binding.pbProgress.visibility = View.VISIBLE
                        binding.ivDownloads.visibility = View.GONE
                        binding.pbProgress.progress = downloadProgress1
                        getMediaByPer(PlaylistID!!, totalAudio, ctx, DB)
                    }
                } else {
                    DB.taskDao().getCountDownloadProgress1("Complete", PlaylistId, CoUserID)
                        .removeObserver {}
                    binding.pbProgress.visibility = View.GONE
                    binding.ivDownloads.visibility = View.VISIBLE
                    enableDisableDownload(false, "orange")
                }
                //                } else {
//                    binding.pbProgress.setVisibility(View.GONE);
//                    binding.ivDownloads.setVisibility(View.VISIBLE);
//                }
                callObserveMethodGetAllMedia(ctx, DB)
            })
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
    }

    private fun getDownloadData(ctx: Context, DB: AudioDatabase) {
        try {
            val sharedy: SharedPreferences =
                getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE)
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


    private fun GetPlaylistDetail2(
        ctx: Context,
        DB: AudioDatabase,
        CoUserID: String,
        PlaylistID: String
    ) {
        DB.taskDao()
            .getPlaylist1(PlaylistID, CoUserID)
            .observe(ctx as (LifecycleOwner), { audioList: List<DownloadPlaylistDetails> ->
                downloadPlaylistDetailsList = arrayListOf()
                downloadPlaylistDetailsList = audioList as ArrayList<DownloadPlaylistDetails>
                GetMedia(ctx, DB)
            })
    }

    private fun callObserveMethodGetAllMedia(ctx: Context, DB: AudioDatabase) {
        try {
            DB.taskDao()
                .geAllData12(CoUserID)
                .observe(ctx as (LifecycleOwner), { audioList: List<DownloadAudioDetails>? ->
                    if (audioList != null) {
                        downloadAudioDetailsList = audioList as ArrayList<DownloadAudioDetails>
                        onlySingleDownloaded = ArrayList()
                        if (audioList.isNotEmpty()) {
                            for (i in audioList) {
                                if (i!!.playlistId.equals(
                                        "",
                                        ignoreCase = true
                                    )
                                ) {
                                    onlySingleDownloaded.add(i.name)
                                }
                            }
                        } else {
                            onlySingleDownloaded = ArrayList()
                        }
                    } else {
                        onlySingleDownloaded = ArrayList()
                        downloadAudioDetailsList = ArrayList()
                    }
                })
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun enableDisableDownload(b: Boolean, color: String) {
        if (b) {
            binding.llDownloads.isClickable = true
            binding.llDownloads.isEnabled = true
            binding.ivDownloads.setColorFilter(
                ContextCompat.getColor(activity!!, R.color.white),
                PorterDuff.Mode.SRC_IN
            )
            Log.e("Download Click", "True")
        } else {
            binding.llDownloads.isClickable = false
            binding.llDownloads.isEnabled = false
            if (color.equals("gray", ignoreCase = true)) {
                binding.ivDownloads.setImageResource(R.drawable.ic_download_bws)
                binding.ivDownloads.setColorFilter(
                    ContextCompat.getColor(activity!!, R.color.light_gray),
                    PorterDuff.Mode.SRC_IN
                )
            } else if (color.equals("orange", ignoreCase = true)) {
                binding.ivDownloads.setImageResource(R.drawable.ic_download_done_icon)
                binding.ivDownloads.setColorFilter(
                    ContextCompat.getColor(activity!!, R.color.white),
                    PorterDuff.Mode.SRC_IN
                )
            }
            Log.e("Download Click", "false")
        }
    }

    private fun callDownload(
        id: String,
        audioFile: String,
        Name: String,
        playlistSongs: ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>,
        position: Int,
        llDownload: RelativeLayout,
        ivDownloads: ImageView,
        ctx: Context,
        act: Activity,
        DB: AudioDatabase
    ) {
        AudioDatabase.databaseWriteExecutor.execute {
            downloadAudioDetailsListGloble =
                DB.taskDao().geAllData1ForAll() as ArrayList<DownloadAudioDetails>
        }
        if (id.isEmpty() && Name.isEmpty() && audioFile.isEmpty()) {
            val url = arrayListOf<String>()
            val name = arrayListOf<String>()
            val downloadPlaylistId = arrayListOf<String>()
            val playlistSongs2 = ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>()
            playlistSongs2.addAll(playlistSongs)
            if (downloadAudioDetailsListGloble.isNotEmpty()) {
                for (y in downloadAudioDetailsListGloble) {
                    if (playlistSongs2.size == 0) {
                        break
                    } else {
                        for (x in 0 until playlistSongs2.size)
                            if (playlistSongs2.size != 0) {
                                if (x < playlistSongs2.size) {
                                    if (playlistSongs2[x].audioFile.equals(
                                            y.audioFile,
                                            ignoreCase = true
                                        )
                                    ) {
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
                    name.add(playlistSongs2[x].name!!)
                    url.add(playlistSongs2[x].audioFile!!)
                    downloadPlaylistId.add(playlistSongs2[x].playlistID!!)
                }
            }
            val sharedx: SharedPreferences =
                getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE)
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
                    val downloadMedia =
                        DownloadMedia(ctx, act)
                    downloadMedia.encrypt1(url, name, downloadPlaylistId)
                }
                val shared: SharedPreferences =
                    getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE)
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
            showToast("Downloading the playlist right now",act)
            saveAllMedia(ctx, playlistSongs, DB)
        } else {
            var downloadOrNot = false
            if (downloadAudioDetailsListGloble.size != 0) {
                for (i in downloadAudioDetailsListGloble.indices) {
                    if (downloadAudioDetailsListGloble[i].audioFile.equals(
                            audioFile,
                            ignoreCase = true
                        )
                    ) {
                        downloadOrNot = false
                        break
                    } else if (i == downloadAudioDetailsListGloble.size - 1) {
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
                val sharedx: SharedPreferences =
                    getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE)
                val gson1 = Gson()
                val json = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, gson1.toString())
                val json1 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadUrl, gson1.toString())
                val json2 =
                    sharedx.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, gson1.toString())
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
                        val downloadMedia =
                            DownloadMedia(ctx, act)
                        downloadMedia.encrypt1(url, name, downloadPlaylistId /*, playlistSongs*/)
                    }
                    val shared: SharedPreferences =
                        getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE)
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
                showToast("Your audio has been downloaded",act)
                SaveMedia(playlistSongs, position, llDownload, ivDownloads, 0, ctx, DB)
            } else {
                showToast("Downloading the audio right now",act)
                SaveMedia(playlistSongs, position, llDownload, ivDownloads, 100, ctx, DB)
            }
            val sharedx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val AudioPlayerFlag = sharedx.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
            var PlayerPosition: Int = sharedx.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
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
            if (AudioPlayerFlag.equals("DownloadListAudio", ignoreCase = true)) {
                val mainPlayModel = DownloadAudioDetails()
                mainPlayModel.id = playlistSongs[position].id
                mainPlayModel.name = playlistSongs[position].name
                mainPlayModel.audioFile = playlistSongs[position].audioFile
                mainPlayModel.audioDirection = playlistSongs[position].audioDirection
                mainPlayModel.audiomastercat = playlistSongs[position].audiomastercat
                mainPlayModel.audioSubCategory = playlistSongs[position].audioSubCategory
                mainPlayModel.imageFile = playlistSongs[position].imageFile
                mainPlayModel.audioDuration = playlistSongs[position].audioDuration
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
                val sharedd: SharedPreferences =
                    getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val editor = sharedd.edit()
                val gson = Gson()
                val jsonx = gson.toJson(arrayList2)
                val json1 = gson.toJson(arrayList)

                editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json)
                editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, PlayerPosition)
                editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "")
                editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "DownloadListAudio")
                if (arrayList2[PlayerPosition].audioFile != "") {
                    val downloadAudioDetailsList1: MutableList<String> = ArrayList()
                    val ge = GlobalInitExoPlayer()
                    downloadAudioDetailsList1.add(mainPlayModel1.name)
                    ge.AddAudioToPlayer(size, arrayList2, downloadAudioDetailsList1, ctx)
                }
            }
            //            handler2.postDelayed(UpdateSongTime2, 3000);
        }
    }

    private fun saveAllMedia(
        ctx: Context,
        playlistSongs: ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>, DB: AudioDatabase
    ) {
        /*   p = Properties()
           p.putValue("userId", UserID)
           p.putValue("playlistId", downloadPlaylistDetails.getPlaylistID())
           p.putValue("playlistName", downloadPlaylistDetails.getPlaylistName())
           p.putValue("playlistDescription", downloadPlaylistDetails.getPlaylistDesc())
           if (downloadPlaylistDetails.getCreated().equals("1", ignoreCase = true)) {
               p.putValue("playlistType", "Created")
           } else if (downloadPlaylistDetails.getCreated().equals("0", ignoreCase = true)) {
               p.putValue("playlistType", "Default")
           }
           if (downloadPlaylistDetails.getTotalhour().equals("", ignoreCase = true)) {
               p.putValue("playlistDuration", "0h " + downloadPlaylistDetails.getTotalminute() + "m")
           } else if (downloadPlaylistDetails.getTotalminute().equals("", ignoreCase = true)) {
               p.putValue("playlistDuration", downloadPlaylistDetails.getTotalhour() + "h 0m")
           } else {
               p.putValue("playlistDuration", downloadPlaylistDetails.getTotalhour() + "h " + downloadPlaylistDetails.getTotalminute() + "m")
           }
           p.putValue("audioCount", downloadPlaylistDetails.getTotalAudio())
           p.putValue("source", "Downloaded Playlists")
           p.putValue("playerType", "Mini")
           p.putValue("audioService", BWSApplication.appStatus(ctx))
           p.putValue("sound", hundredVolume.toString())*/
//        BWSApplication.addToSegment("Playlist Download Started", p, CONSTANTS.track)
        for (i in playlistSongs) {
            val downloadAudioDetails = DownloadAudioDetails()
            downloadAudioDetails.userId = CoUserID
            downloadAudioDetails.id = i.id
            downloadAudioDetails.name = i.name
            downloadAudioDetails.audioFile = i.audioFile
            downloadAudioDetails.audioDirection = i.audioDirection
            downloadAudioDetails.audiomastercat = i.audiomastercat
            downloadAudioDetails.audioSubCategory = i.audioSubCategory
            downloadAudioDetails.imageFile = i.imageFile
            downloadAudioDetails.playlistId = i.playlistID
            downloadAudioDetails.audioDuration = i.audioDuration
            downloadAudioDetails.isSingle = "0"
            if (downloadAudioDetailsListGloble.size != 0) {
                for (y in downloadAudioDetailsListGloble) {
                    if (i.audioFile.equals(
                            y.audioFile,
                            ignoreCase = true
                        )
                    ) {
                        downloadAudioDetails.isDownload = "Complete"
                        downloadAudioDetails.downloadProgress = 100
                        break
                    } else {
                        downloadAudioDetails.isDownload = "pending"
                        downloadAudioDetails.downloadProgress = 0
                    }
                }
            } else {
                downloadAudioDetails.isDownload = "pending"
                downloadAudioDetails.downloadProgress = 0
            }
//            try {
            AudioDatabase.databaseWriteExecutor.execute {
                DB.taskDao().insertMedia(downloadAudioDetails)
            }
//            } catch (e: java.lang.Exception) {
//                println(e.message)
//            } catch (e: OutOfMemoryError) {
//                println(e.message)
//            }
        }
//        try {
        downloadPlaylistDetails.userId = CoUserID
        AudioDatabase.databaseWriteExecutor.execute {
            DB.taskDao().insertPlaylist(downloadPlaylistDetails)
        }
        downloadPlaylistDetailsList = GetPlaylistDetail(playlistSongs.size, ctx, DB)
        getMediaByPer(PlaylistID!!, playlistSongs.size, ctx, DB)
//        } catch (e: java.lang.Exception) {
//            println(e.message)
//        } catch (e: OutOfMemoryError) {
//            println(e.message)
//        }
    }

    private fun SaveMedia(
        playlistSongs: ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>,
        i: Int,
        llDownload: RelativeLayout,
        ivDownloads: ImageView,
        progress: Int,
        ctx: Context,
        DB: AudioDatabase
    ) {
        val downloadAudioDetails = DownloadAudioDetails()
        downloadAudioDetails.userId = CoUserID
        downloadAudioDetails.id = playlistSongs[i].id
        downloadAudioDetails.name = playlistSongs[i].name
        downloadAudioDetails.audioFile = playlistSongs[i].audioFile
        downloadAudioDetails.audioDirection = playlistSongs[i].audioDirection
        downloadAudioDetails.audiomastercat = playlistSongs[i].audiomastercat
        downloadAudioDetails.audioSubCategory = playlistSongs[i].audioSubCategory
        downloadAudioDetails.imageFile = playlistSongs[i].imageFile
        downloadAudioDetails.audioDuration = playlistSongs[i].audioDuration
        downloadAudioDetails.isSingle = "1"
        downloadAudioDetails.playlistId = ""
        if (progress == 0) {
            downloadAudioDetails.isDownload = "pending"
        } else {
            downloadAudioDetails.isDownload = "Complete"
        }
        downloadAudioDetails.downloadProgress = progress
        try {
            AudioDatabase.databaseWriteExecutor.execute {
                DB.taskDao().insertMedia(downloadAudioDetails)
            }
        } catch (e: java.lang.Exception) {
            println(e.message)
        } catch (e: OutOfMemoryError) {
            println(e.message)
        }
        callObserveMethodGetAllMedia(ctx, DB)
        GetMedia(ctx, DB)
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

    fun GetMedia(ctx: Context, DB: AudioDatabase) {
        try {
//        playlistWiseAudioDetails = new ArrayList<>();
            DB.taskDao().getAllAudioByPlaylist1(PlaylistID, CoUserID)
                .observe(ctx as (LifecycleOwner), { audioList: List<DownloadAudioDetails> ->
                    if (MyDownloads.equals("1", ignoreCase = true)) {
                        if (downloadPlaylistDetailsList.size != 0) {
                            val responseData = PlaylistDetailsModel()
                            val details =
                                ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>()
                            val listModel = PlaylistDetailsModel.ResponseData()
                            listModel.playlistID = downloadPlaylistDetailsList[0].playlistID
                            listModel.playlistName = downloadPlaylistDetailsList[0].playlistName
                            listModel.playlistDesc = downloadPlaylistDetailsList[0].playlistDesc
                            listModel.playlistMastercat =
                                downloadPlaylistDetailsList[0].playlistMastercat
                            listModel.playlistSubcat =
                                downloadPlaylistDetailsList[0].playlistSubcat
                            listModel.playlistImage = downloadPlaylistDetailsList[0].playlistImage
                            listModel.playlistImageDetail =
                                downloadPlaylistDetailsList[0].playlistImageDetails
                            listModel.totalAudio = downloadPlaylistDetailsList[0].totalAudio
                            listModel.totalDuration = downloadPlaylistDetailsList[0].totalDuration
                            listModel.totalhour = downloadPlaylistDetailsList[0].totalhour
                            listModel.totalminute = downloadPlaylistDetailsList[0].totalminute
                            listModel.created = downloadPlaylistDetailsList[0].created
//                        listModel.isReminder = downloadPlaylistDetailsList[0]!!.isReminder
                            if (audioList.isNotEmpty()) {
                                for (i in audioList.indices) {
                                    val detail = PlaylistDetailsModel.ResponseData.PlaylistSong()
                                    detail.id = audioList[i].id
                                    detail.name = audioList[i].name
                                    detail.audioFile = audioList[i].audioFile
                                    detail.audioDirection = audioList[i].audioDirection
                                    detail.audiomastercat = audioList[i].audiomastercat
                                    detail.audioSubCategory = audioList[i].audioSubCategory
                                    detail.imageFile = audioList[i].imageFile
                                    detail.audioDuration = audioList[i].audioDuration
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

    private fun getAllCompletedMedia(
        AudioFlag: String,
        pID: String,
        position: Int,
        ctx: Context,
        playlistID: String,
        playlistName: String
    ) {
        var pos = 0
        val shared: SharedPreferences =
            getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
        var positionSaved = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
        val shared12 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
        val IsPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1")
        if (AudioFlag.equals("Downloadlist", ignoreCase = true) && pID.equals(
                PlaylistID,
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
                callMyPlayer(ctx, activity!!)
                showToast("The audio shall start playing after the disclaimer", activity!!)
            } else {
                val listModelList2 = arrayListOf<PlaylistDetailsModel.ResponseData.PlaylistSong>()
                var view = ""
                for (i in listMOdelGloble.responseData!!.playlistSongs!!.indices) {
                    if (downloadAudioDetailsList.contains(listMOdelGloble.responseData!!.playlistSongs!![i].name)) {
                        listModelList2.add(listMOdelGloble.responseData!!.playlistSongs!![i])
                    }
                }
                if (position != positionSaved) {
                    if (downloadAudioDetailsList.contains(listMOdelGloble.responseData!!.playlistSongs!![position].name)) {
                        positionSaved = position
                        PlayerAudioId = listMOdelGloble.responseData!!.playlistSongs!![position].id
                        if (listModelList2.size != 0) {
                            view = if (listMOdelGloble.responseData!!.created.equals("1")) {
                                "Created"
                            } else {
                                ""
                            }
                            activity?.let {
                                callPlayer(
                                    pos,
                                    view,
                                    listModelList2,
                                    ctx,
                                    it,
                                    playlistID,
                                    playlistName,
                                    listMOdelGloble.responseData!!.created,
                                    true, MyDownloads
                                )
                            }
                        } else {
                            showToast(ctx.getString(R.string.no_server_found), activity)
                        }
                    } else {
//                                pos = 0;
                        showToast(ctx.getString(R.string.no_server_found), activity)
                    }
                } else {
                    callMyPlayer(ctx, activity!!)
                }
//                SegmentTag()
            }
        } else {
            val listModelList2 = arrayListOf<PlaylistDetailsModel.ResponseData.PlaylistSong>()
            for (i in listMOdelGloble.responseData!!.playlistSongs!!.indices) {
                if (downloadAudioDetailsList.contains(listMOdelGloble.responseData!!.playlistSongs!![i].name)) {
                    listModelList2.add(listMOdelGloble.responseData!!.playlistSongs!![i])
                }
            }
            if (downloadAudioDetailsList.contains(listMOdelGloble.responseData!!.playlistSongs!![position].name)) {
                pos = position
                val gson = Gson()
                val DisclimerJson =
                    shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
                val type =
                    object : TypeToken<HomeScreenModel.ResponseData.DisclaimerAudio?>() {}.type
                val arrayList =
                    gson.fromJson<HomeScreenModel.ResponseData.DisclaimerAudio>(DisclimerJson, type)
                val mainPlayModel = PlaylistDetailsModel.ResponseData.PlaylistSong()
                mainPlayModel.id = arrayList.id
                mainPlayModel.name = arrayList.name
                mainPlayModel.audioFile = arrayList.audioFile
                mainPlayModel.audioDirection = arrayList.audioDirection
                mainPlayModel.audiomastercat = arrayList.audiomastercat
                mainPlayModel.audioSubCategory = arrayList.audioSubCategory
                mainPlayModel.imageFile = arrayList.imageFile
                mainPlayModel.audioDuration = arrayList.audioDuration
                var audioc = true
                var view = ""
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
                            view = if (listMOdelGloble.responseData!!.created.equals("1")) {
                                "Created"
                            } else {
                                ""
                            }
                            callPlayer(
                                pos,
                                view,
                                listModelList2,
                                ctx,
                                activity!!,
                                playlistID,
                                playlistName,
                                listMOdelGloble.responseData!!.created,
                                audioc, MyDownloads
                            )
                        } else {
                            showToast(ctx.getString(R.string.no_server_found), activity)
                        }
                    } else if (listModelList2[pos].id.equals("0") && listModelList2.size > 1) {
                        view = if (listMOdelGloble.responseData!!.created.equals("1")) {
                            "Created"
                        } else {
                            ""
                        }
                        callPlayer(
                            pos,
                            view,
                            listModelList2,
                            ctx,
                            activity!!,
                            playlistID,
                            playlistName,
                            listMOdelGloble.responseData!!.created,
                            audioc, MyDownloads
                        )
                    } else {
                        showToast(ctx.getString(R.string.no_server_found), activity)
                    }
                } else {
                    showToast(ctx.getString(R.string.no_server_found), activity)
                }
            } else {
                showToast(ctx.getString(R.string.no_server_found), activity)
            }
//            SegmentTag()
        }
    }

}
