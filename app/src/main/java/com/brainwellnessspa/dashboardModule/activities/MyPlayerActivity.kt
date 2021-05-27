package com.brainwellnessspa.dashboardModule.activities

import android.app.Activity
import android.app.UiModeManager
import android.content.*
import android.graphics.PorterDuff
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.room.Room
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.DashboardOldModule.Activities.DashboardActivity.audioClick
import com.brainwellnessspa.DashboardOldModule.Models.AudioInterruptionModel
import com.brainwellnessspa.DashboardOldModule.Models.ViewAllAudioListModel
import com.brainwellnessspa.DashboardOldModule.TransparentPlayer.Fragments.MiniPlayerFragment.isDisclaimer
import com.brainwellnessspa.DashboardOldModule.TransparentPlayer.Fragments.MiniPlayerFragment.addToRecentPlayId
import com.brainwellnessspa.DashboardOldModule.TransparentPlayer.Models.MainPlayModel
import com.brainwellnessspa.EncryptDecryptUtils.DownloadMedia
import com.brainwellnessspa.R
import com.brainwellnessspa.RoomDataBase.AudioDatabase
import com.brainwellnessspa.RoomDataBase.DatabaseClient
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails
import com.brainwellnessspa.Services.GlobalInitExoPlayer
import com.brainwellnessspa.Services.GlobalInitExoPlayer.*
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.dashboardModule.models.*
import com.brainwellnessspa.databinding.ActivityViewPlayerBinding
import com.brainwellnessspa.databinding.AudioPlayerNewLayoutBinding
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.TimeBar
import com.google.android.exoplayer2.ui.TimeBar.OnScrubListener
import com.google.android.exoplayer2.util.Assertions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MyPlayerActivity : AppCompatActivity() {
    lateinit var binding: ActivityViewPlayerBinding
    var AudioPlayerFlag: String? = ""
    var mainPlayModelList = arrayListOf<MainPlayModel>()
    var position = 0
    var listSize: Int = 0
    var CoUserID: String? = ""
    lateinit var act: Activity
    var id: String? = ""
    var name: String? = ""
    var url: String? = ""
    var UserID: String? = ""
    lateinit var exoBinding: AudioPlayerNewLayoutBinding
    var gson = Gson()
    var playerControlView: PlayerControlView? = null
    lateinit var ctx: Context
    var oldSeekPosition: Long = 0
    var downloadClick = false
    var downloadAudioDetailsList = arrayListOf<String>()
    var downloadAudioDetailsListGloble = arrayListOf<String>()
    var filesDownloaded: List<File>? = null

    var DB: AudioDatabase? = null
    var fileNameList = arrayListOf<String>()
    var audioFile1 = arrayListOf<String>()
    var playlistDownloadId = arrayListOf<String>()

    private val listener: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            getDownloadData()
            if (intent.hasExtra("Progress")) {
                GetMediaPer();
            }
        }
    }
    private val listener1: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("MyReminder")) {
                audioClick = false
                makePlayerArray()
            }
        }
    }

    override fun onResume() {
        LocalBroadcastManager.getInstance(act)
            .registerReceiver(listener, IntentFilter("DownloadProgress"))
        super.onResume()
    }

    override fun onBackPressed() {
        callBack()
        super.onBackPressed()
    }

    private fun callBack() {
        finish()
    }

    private fun callHeartbeat() {
        val p = Properties()
        p.putValue("userId", UserID)
        p.putValue("coUserId", CoUserID)
        p.putValue("audioId", mainPlayModelList[position].id)
        p.putValue("audioName", mainPlayModelList[position].name)
        p.putValue("audioDescription", "")
        p.putValue("directions", mainPlayModelList[position].audioDirection)
        p.putValue("masterCategory", mainPlayModelList[position].audiomastercat)
        p.putValue("subCategory", mainPlayModelList[position].audioSubCategory)
        p.putValue("audioDuration", mainPlayModelList[position].audioDuration)
        p.putValue("position", GetCurrentAudioPosition())
        if (downloadAudioDetailsList.contains(mainPlayModelList[position].name)) {
            p.putValue("audioType", "Downloaded")
        } else {
            p.putValue("audioType", "Streaming")
        }
        p.putValue("source", GetSourceName(ctx))
        p.putValue("playerType", "Mini")
        p.putValue("audioService", appStatus(act))
        p.putValue("bitRate", "")
        p.putValue("sound", hundredVolume.toString())
        addToSegment("Audio Playing", p, CONSTANTS.track)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_player)
        ctx = this@MyPlayerActivity
        act = this@MyPlayerActivity
        DB = Room.databaseBuilder(
            this,
            AudioDatabase::class.java,
            "Audio_database"
        )
            .addMigrations(BWSApplication.MIGRATION_1_2)
            .build()
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        this.UserID = shared1.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        CoUserID = shared1.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")
        playerControlView = Assertions.checkNotNull(binding.playerControlView)
        if (intent.hasExtra("notification")) {
            val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
            val UserID = shared1.getString(CONSTANTS.PREF_KEY_UserID, "")
            val shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val xposition = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
            val gson = Gson()
            var mainPlayModelList2 = java.util.ArrayList<MainPlayModel>()
            val json = shared.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toString())
            if (!json.equals(gson.toString(), ignoreCase = true)) {
                val type = object : TypeToken<java.util.ArrayList<MainPlayModel?>?>() {}.type
                mainPlayModelList2 = gson.fromJson(json, type)
            }
            val p = Properties()
            p.putValue("userId", UserID)
            try {
                p.putValue("audioId", mainPlayModelList2[xposition].id)
                p.putValue("audioName", mainPlayModelList2[xposition].name)
                p.putValue("audioDescription", "")
                p.putValue("directions", mainPlayModelList2[xposition].audioDirection)
                p.putValue("masterCategory", mainPlayModelList2[xposition].audiomastercat)
                p.putValue("subCategory", mainPlayModelList2[xposition].audioSubCategory)
                p.putValue("audioDuration", mainPlayModelList2[xposition].audioDuration)
                p.putValue("position", GetCurrentAudioPosition())
            } catch (e: Exception) {
                e.printStackTrace()
            }

            p.putValue("audioType", "")
            p.putValue("source", GetSourceName(ctx))
            p.putValue("playerType", "Notification Player")
            p.putValue("audioService", appStatus(ctx))
            p.putValue("bitRate", "")
            p.putValue("sound", hundredVolume.toString())
            addToSegment("Notification Player Clicked", p, CONSTANTS.track)
        }

        LocalBroadcastManager.getInstance(ctx)
                .registerReceiver(listener1, IntentFilter("Reminder"))
        exoBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.audio_player_new_layout,
            binding.playerControlView,
            false
        )

        binding.playerControlView.addView(exoBinding.getRoot())
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        binding.llInfo.setOnClickListener {
            if (isNetworkConnected(ctx)) {
            val shared = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val json = shared.getString(CONSTANTS.PREF_KEY_MainAudioList, gson.toString())
            AudioPlayerFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
            if (AudioPlayerFlag.equals("playlist", true)) {

            }
            callAudioDetails(
                mainPlayModelList[position].id, ctx, act, CoUserID, "audioPlayer",
                arrayListOf<DownloadAudioDetails>(),
                arrayListOf<ViewAllAudioListModel.ResponseData.Detail>(),
                arrayListOf<PlaylistDetailsModel.ResponseData.PlaylistSong>(),
                mainPlayModelList, position
            )
            } else {
                showToast(getString(R.string.no_server_found), act)
            }
        }

        binding.llBack.setOnClickListener {
            callBack()
        }

        makePlayerArray()

        if (audioClick) {
            exoBinding.llPlay.visibility = View.GONE
            exoBinding.llPause.visibility = View.GONE
            exoBinding.progressBar.visibility = View.VISIBLE
            audioClick = false
            GetAllMedia()
        } else {
            GetAllMedia1()
        }
        /* binding.llDisclaimer.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(ctx);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.full_desc_layout);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_blue_gray)));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            final TextView tvTitle = dialog.findViewById(R.id.tvTitle);
            final TextView tvDesc = dialog.findViewById(R.id.tvDesc);
            final RelativeLayout tvClose = dialog.findViewById(R.id.tvClose);
            tvTitle.setText(R.string.Disclaimer);
            tvDesc.setText(R.string.Disclaimer_text);
            dialog.setOnKeyListener((view, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    return true;
                }
                return false;
            });

            tvClose.setOnClickListener(view1 -> dialog.dismiss());
            dialog.show();
            dialog.setCancelable(false);
        });*/
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(act).unregisterReceiver(listener)
        LocalBroadcastManager.getInstance(ctx).unregisterReceiver(listener1)
        super.onDestroy()
    }

    private fun makePlayerArray() {
        val shared = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
        val json = shared.getString(CONSTANTS.PREF_KEY_MainAudioList, gson.toString())
        AudioPlayerFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        var mainPlayModel: MainPlayModel
        mainPlayModelList = ArrayList()
        position = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
        if (AudioPlayerFlag.equals("MainAudioList", ignoreCase = true)) {
            val type =
                object : TypeToken<ArrayList<HomeDataModel.ResponseData.Audio.Detail?>>() {}.type
            val arrayList =
                gson.fromJson<ArrayList<HomeDataModel.ResponseData.Audio.Detail?>>(json, type)
            listSize = arrayList.size
            for (i in 0 until listSize) {
                mainPlayModel = MainPlayModel()
                mainPlayModel.id = arrayList[i]!!.id
                mainPlayModel.name = arrayList[i]!!.name
                mainPlayModel.audioFile = arrayList[i]!!.audioFile
                mainPlayModel.playlistID = ""
                mainPlayModel.audioDirection = arrayList[i]!!.audioDirection
                mainPlayModel.audiomastercat = arrayList[i]!!.audiomastercat
                mainPlayModel.audioSubCategory = arrayList[i]!!.audioSubCategory
                mainPlayModel.imageFile = arrayList[i]!!.imageFile
                mainPlayModel.audioDuration = arrayList[i]!!.audioDuration
                mainPlayModelList.add(mainPlayModel)
            }
            val sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val editor = sharedz.edit()
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toJson(mainPlayModelList))
            editor.apply()
        } else if (AudioPlayerFlag.equals("ViewAllAudioList", ignoreCase = true)) {
            val type =
                object : TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail?>>() {}.type
            val arrayList =
                gson.fromJson<ArrayList<ViewAllAudioListModel.ResponseData.Detail?>>(json, type)
            listSize = arrayList.size
            for (i in 0 until listSize) {
                mainPlayModel = MainPlayModel()
                mainPlayModel.id = arrayList[i]!!.id
                mainPlayModel.name = arrayList[i]!!.name
                mainPlayModel.audioFile = arrayList[i]!!.audioFile
                mainPlayModel.playlistID = ""
                mainPlayModel.audioDirection = arrayList[i]!!.audioDirection
                mainPlayModel.audiomastercat = arrayList[i]!!.audiomastercat
                mainPlayModel.audioSubCategory = arrayList[i]!!.audioSubCategory
                mainPlayModel.imageFile = arrayList[i]!!.imageFile
                mainPlayModel.audioDuration = arrayList[i]!!.audioDuration
                mainPlayModelList.add(mainPlayModel)
            }
            val sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val editor = sharedz.edit()
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toJson(mainPlayModelList))
            editor.apply()
        } else if (AudioPlayerFlag.equals(getString(R.string.top_categories), ignoreCase = true)) {
            val type =
                object : TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail?>>() {}.type
            val arrayList =
                gson.fromJson<ArrayList<ViewAllAudioListModel.ResponseData.Detail?>>(json, type)
            listSize = arrayList.size
            for (i in 0 until listSize) {
                mainPlayModel = MainPlayModel()
                mainPlayModel.id = arrayList[i]!!.id
                mainPlayModel.name = arrayList[i]!!.name
                mainPlayModel.audioFile = arrayList[i]!!.audioFile
                mainPlayModel.playlistID = ""
                mainPlayModel.audioDirection = arrayList[i]!!.audioDirection
                mainPlayModel.audiomastercat = arrayList[i]!!.audiomastercat
                mainPlayModel.audioSubCategory = arrayList[i]!!.audioSubCategory
                mainPlayModel.imageFile = arrayList[i]!!.imageFile
                mainPlayModel.audioDuration = arrayList[i]!!.audioDuration
                mainPlayModelList.add(mainPlayModel)
            }
            val sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val editor = sharedz.edit()
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toJson(mainPlayModelList))
            editor.apply()
        } else if (AudioPlayerFlag.equals("SearchModelAudio", ignoreCase = true)) {
            val type = object : TypeToken<ArrayList<SearchBothModel.ResponseData?>>() {}.type
            val arrayList = gson.fromJson<ArrayList<SearchBothModel.ResponseData?>>(json, type)
            listSize = arrayList.size
            for (i in 0 until listSize) {
                mainPlayModel = MainPlayModel()
                mainPlayModel.id = arrayList[i]!!.iD
                mainPlayModel.name = arrayList[i]!!.name
                mainPlayModel.audioFile = arrayList[i]!!.audioFile
                mainPlayModel.playlistID = ""
                mainPlayModel.audioDirection = arrayList[i]!!.audioDirection
                mainPlayModel.audiomastercat = arrayList[i]!!.audiomastercat
                mainPlayModel.audioSubCategory = arrayList[i]!!.audioSubCategory
                mainPlayModel.imageFile = arrayList[i]!!.imageFile
                mainPlayModel.audioDuration = arrayList[i]!!.audioDuration
                mainPlayModelList.add(mainPlayModel)
            }
            val sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val editor = sharedz.edit()
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toJson(mainPlayModelList))
            editor.apply()
        } else if (AudioPlayerFlag.equals("SearchAudio", ignoreCase = true)) {
            val type = object : TypeToken<ArrayList<SuggestedModel.ResponseData?>>() {}.type
            val arrayList = gson.fromJson<ArrayList<SuggestedModel.ResponseData?>>(json, type)
            listSize = arrayList.size
            for (i in 0 until listSize) {
                mainPlayModel = MainPlayModel()
                mainPlayModel.id = arrayList[i]!!.iD
                mainPlayModel.name = arrayList[i]!!.name
                mainPlayModel.audioFile = arrayList[i]!!.audioFile
                mainPlayModel.playlistID = ""
                mainPlayModel.audioDirection = arrayList[i]!!.audioDirection
                mainPlayModel.audiomastercat = arrayList[i]!!.audiomastercat
                mainPlayModel.audioSubCategory = arrayList[i]!!.audioSubCategory
                mainPlayModel.imageFile = arrayList[i]!!.imageFile
                mainPlayModel.audioDuration = arrayList[i]!!.audioDuration
                mainPlayModelList.add(mainPlayModel)
            }
            val sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val editor = sharedz.edit()
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toJson(mainPlayModelList))
            editor.apply()
        } else if (AudioPlayerFlag.equals("DownloadListAudio", ignoreCase = true)) {
            val type = object : TypeToken<ArrayList<DownloadAudioDetails?>>() {}.type
            val arrayList = gson.fromJson<ArrayList<DownloadAudioDetails?>>(json, type)
            listSize = arrayList.size
            for (i in 0 until listSize) {
                mainPlayModel = MainPlayModel()
                mainPlayModel.id = arrayList[i]!!.id
                mainPlayModel.name = arrayList[i]!!.name
                mainPlayModel.audioFile = arrayList[i]!!.audioFile
                mainPlayModel.playlistID = ""
                mainPlayModel.audioDirection = arrayList[i]!!.audioDirection
                mainPlayModel.audiomastercat = arrayList[i]!!.audiomastercat
                mainPlayModel.audioSubCategory = arrayList[i]!!.audioSubCategory
                mainPlayModel.imageFile = arrayList[i]!!.imageFile
                mainPlayModel.audioDuration = arrayList[i]!!.audioDuration
                mainPlayModelList.add(mainPlayModel)
            }
            val sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val editor = sharedz.edit()
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toJson(mainPlayModelList))
            editor.apply()
        } else if (AudioPlayerFlag.equals("Downloadlist", ignoreCase = true)) {
            val type = object : TypeToken<ArrayList<DownloadAudioDetails?>>() {}.type
            val arrayList = gson.fromJson<ArrayList<DownloadAudioDetails?>>(json, type)
            listSize = arrayList.size
            for (i in 0 until listSize) {
                mainPlayModel = MainPlayModel()
                mainPlayModel.id = arrayList[i]!!.id
                mainPlayModel.name = arrayList[i]!!.name
                mainPlayModel.audioFile = arrayList[i]!!.audioFile
                mainPlayModel.playlistID = ""
                mainPlayModel.audioDirection = arrayList[i]!!.audioDirection
                mainPlayModel.audiomastercat = arrayList[i]!!.audiomastercat
                mainPlayModel.audioSubCategory = arrayList[i]!!.audioSubCategory
                mainPlayModel.imageFile = arrayList[i]!!.imageFile
                mainPlayModel.audioDuration = arrayList[i]!!.audioDuration
                mainPlayModelList.add(mainPlayModel)
            }
            val sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val editor = sharedz.edit()
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toJson(mainPlayModelList))
            editor.apply()
        } else if (AudioPlayerFlag.equals("playlist", ignoreCase = true)) {
            val type = object :
                TypeToken<ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong?>>() {}.type
            val arrayList =
                gson.fromJson<ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong?>>(
                    json,
                    type
                )
            listSize = arrayList.size
            for (i in 0 until listSize) {
                mainPlayModel = MainPlayModel()
                mainPlayModel.id = arrayList[i]!!.id
                mainPlayModel.name = arrayList[i]!!.name
                mainPlayModel.audioFile = arrayList[i]!!.audioFile
                mainPlayModel.playlistID = arrayList[i]!!.playlistID
                mainPlayModel.audioDirection = arrayList[i]!!.audioDirection
                mainPlayModel.audiomastercat = arrayList[i]!!.audiomastercat
                mainPlayModel.audioSubCategory = arrayList[i]!!.audioSubCategory
                mainPlayModel.imageFile = arrayList[i]!!.imageFile
                mainPlayModel.audioDuration = arrayList[i]!!.audioDuration
                mainPlayModelList.add(mainPlayModel)
            }
            val sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val editor = sharedz.edit()
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toJson(mainPlayModelList))
            editor.apply()
        }
        //        binding.tvDireName.setText(R.string.Directions);
//        callButtonText(position);
//        if (mainPlayModelList.get(position).getAudioFile().equalsIgnoreCase("")) {
//            initializePlayerDisclaimer();
//        } else {
//            initializePlayer();
//        }
        getDownloadData()
        if (!audioClick)
            getPrepareShowData()
        else
            callButtonText(position)
    }

    private fun initializePlayerDisclaimer() {
//        player = new SimpleExoPlayer.Builder(getApplicationContext()).build();
        try {
            isDisclaimer = 1
            if (audioClick) {
                val globalInitExoPlayer = GlobalInitExoPlayer()
                globalInitExoPlayer.GlobleInItDisclaimer(ctx, mainPlayModelList, position)
                setPlayerCtrView()
            }
            if (player != null) {
                player.addListener(object : Player.EventListener {
                    override fun onPlaybackStateChanged(state: Int) {
                        if (state == ExoPlayer.STATE_ENDED) {
                            //player back ended
                            audioClick = true
                            isDisclaimer = 0
                            val shared =
                                getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
                            val editor = shared.edit()
                            editor.putString(CONSTANTS.PREF_KEY_IsDisclimer, "0")
                            editor.apply()
                            removeArray()
                            val p = Properties()
                            p.putValue("userId", UserID)
                            p.putValue("coUserId", CoUserID)
                            p.putValue("position", GetCurrentAudioPosition())
                            p.putValue("source", GetSourceName(ctx))
                            p.putValue("playerType", "Main")
                            if (downloadAudioDetailsList.contains(mainPlayModelList[position].name)) {
                                p.putValue("audioType", "Downloaded")
                            } else {
                                p.putValue("audioType", "Streaming")
                            }
                            p.putValue("bitRate", "")
                            p.putValue("audioService", appStatus(ctx))
                            p.putValue("sound", hundredVolume.toString())
                            addToSegment("Disclaimer Completed", p, CONSTANTS.track)
                        }
                        if (state == ExoPlayer.STATE_READY) {
                            val p = Properties()
                            p.putValue("userId", UserID)
                            p.putValue("coUserId", CoUserID)
                            p.putValue("position", GetCurrentAudioPosition())
                            p.putValue("source", GetSourceName(ctx))
                            p.putValue("playerType", "Main")
                            if (downloadAudioDetailsList.contains(mainPlayModelList[position].name)) {
                                p.putValue("audioType", "Downloaded")
                            } else {
                                p.putValue("audioType", "Streaming")
                            }
                            p.putValue("bitRate", "")
                            p.putValue("audioService", appStatus(ctx))
                            p.putValue("sound", hundredVolume.toString())
                            addToSegment("Disclaimer Started", p, CONSTANTS.track)
                            try {
                                if (player.playWhenReady) {
                                    exoBinding.llPlay.visibility = View.GONE
                                    exoBinding.llPause.visibility = View.VISIBLE
                                    exoBinding.progressBar.visibility = View.GONE
                                    val p = Properties()
                                    p.putValue("userId", UserID)
                                    p.putValue("coUserId", CoUserID)
                                    p.putValue("position", GetCurrentAudioPosition())
                                    p.putValue("source", GetSourceName(ctx))
                                    p.putValue("playerType", "Main")
                                    if (downloadAudioDetailsList.contains(mainPlayModelList[position].name)) {
                                        p.putValue("audioType", "Downloaded")
                                    } else {
                                        p.putValue("audioType", "Streaming")
                                    }
                                    p.putValue("bitRate", "")
                                    p.putValue("audioService", appStatus(ctx))
                                    p.putValue("sound", hundredVolume.toString())
                                    addToSegment(
                                        "Disclaimer Playing",
                                        p,
                                        CONSTANTS.track
                                    )
                                } else if (!player.playWhenReady) {
                                    exoBinding.llPlay.visibility = View.VISIBLE
                                    exoBinding.llPause.visibility = View.GONE
                                    exoBinding.progressBar.visibility = View.GONE
                                }
                            } catch (e: java.lang.Exception) {
                            }
                        } else if (state == ExoPlayer.STATE_BUFFERING) {
                            exoBinding.llPlay.visibility = View.GONE
                            exoBinding.llPause.visibility = View.GONE
                            exoBinding.progressBar.visibility = View.VISIBLE
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        if (player != null) {
//                            myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(position).getImageFile());
                            if (player.playbackState == ExoPlayer.STATE_BUFFERING) {
                                exoBinding.llPlay.visibility = View.GONE
                                exoBinding.llPause.visibility = View.GONE
                                exoBinding.progressBar.visibility = View.VISIBLE
                            } else if (isPlaying) {
                                exoBinding.llPlay.visibility = View.GONE
                                exoBinding.llPause.visibility = View.VISIBLE
                                exoBinding.progressBar.visibility = View.GONE
                            } else if (!isPlaying) {
                                exoBinding.llPlay.visibility = View.VISIBLE
                                exoBinding.llPause.visibility = View.GONE
                                exoBinding.progressBar.visibility = View.GONE
                            }
                            exoBinding.exoProgress.setBufferedPosition(player.bufferedPosition)
                            exoBinding.exoProgress.setPosition(player.currentPosition)
                            exoBinding.exoProgress.setDuration(player.duration)
                            exoBinding.tvStartTime.text = String.format(
                                "%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(player.currentPosition),
                                TimeUnit.MILLISECONDS.toSeconds(player.currentPosition) - TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(player.currentPosition)
                                )
                            )
                        }
                    }
                })
                if (player != null) {
                    if (player.playbackState == ExoPlayer.STATE_BUFFERING) {
                        exoBinding.llPlay.visibility = View.GONE
                        exoBinding.llPause.visibility = View.GONE
                        exoBinding.progressBar.visibility = View.VISIBLE
                    } else if (player.playWhenReady) {
                        exoBinding.llPlay.visibility = View.GONE
                        exoBinding.llPause.visibility = View.VISIBLE
                        exoBinding.progressBar.visibility = View.GONE
                    } else if (!player.playWhenReady) {
                        exoBinding.llPlay.visibility = View.VISIBLE
                        exoBinding.llPause.visibility = View.GONE
                        exoBinding.progressBar.visibility = View.GONE
                    }
                    exoBinding.exoProgress.setBufferedPosition(player.bufferedPosition)
                    exoBinding.exoProgress.setPosition(player.currentPosition)
                    exoBinding.exoProgress.setDuration(player.duration)
                }
                setPlayerCtrView()
            } else {
                if (audioClick) {
                    exoBinding.progressBar.visibility = View.GONE
                    exoBinding.llPlay.visibility = View.GONE
                    exoBinding.llPause.visibility = View.VISIBLE
                } else if (PlayerINIT) {
                    exoBinding.progressBar.visibility = View.GONE
                    exoBinding.llPlay.visibility = View.GONE
                    exoBinding.llPause.visibility = View.VISIBLE
                }
            }
            exoBinding.llPause.setOnClickListener {
                if (player != null) {
                    player.playWhenReady = false
                    exoBinding.llPlay.visibility = View.VISIBLE
                    exoBinding.llPause.visibility = View.GONE
                    exoBinding.progressBar.visibility = View.GONE
                    val p = Properties()
                    p.putValue("userId", UserID)
                    p.putValue("coUserId", CoUserID)
                    p.putValue("position", GetCurrentAudioPosition())
                    p.putValue("source", GetSourceName(ctx))
                    p.putValue("playerType", "Main")
                    if (downloadAudioDetailsList.contains(mainPlayModelList[position].name)) {
                        p.putValue("audioType", "Downloaded")
                    } else {
                        p.putValue("audioType", "Streaming")
                    }
                    p.putValue("bitRate", "")
                    p.putValue("sound", hundredVolume.toString())
                    p.putValue("audioService", appStatus(ctx))
                    addToSegment("Disclaimer Paused", p, CONSTANTS.track)
                }
            }
            callAllDisable(false)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun removeArray() {


//        if(!BWSApplication.isNetworkConnected(ctx)){
        callNewPlayerRelease()
        //        }
        isDisclaimer = 0
        val shared = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
        val json = shared.getString(CONSTANTS.PREF_KEY_MainAudioList, gson.toString())
        val json1 = shared.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toString())
        AudioPlayerFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        position = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
        if (AudioPlayerFlag.equals("MainAudioList", ignoreCase = true)) {
            val type =
                object : TypeToken<ArrayList<HomeDataModel.ResponseData.Audio.Detail?>>() {}.type
            val arrayList =
                gson.fromJson<ArrayList<HomeDataModel.ResponseData.Audio.Detail?>>(json, type)
            val type1 = object : TypeToken<ArrayList<MainPlayModel>>() {}.type
            mainPlayModelList = gson.fromJson(json1, type1)
            listSize = arrayList.size
            if (arrayList[position]!!.id.equals("0", ignoreCase = true)) {
                arrayList.removeAt(position)
                mainPlayModelList.removeAt(position)
            }
            val sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val editor = sharedz.edit()
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toJson(mainPlayModelList))
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, gson.toJson(arrayList))
            editor.apply()
        } else if (AudioPlayerFlag.equals("ViewAllAudioList", ignoreCase = true)) {
            val type =
                object : TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail?>>() {}.type
            val arrayList =
                gson.fromJson<ArrayList<ViewAllAudioListModel.ResponseData.Detail?>>(json, type)
            val type1 = object : TypeToken<ArrayList<MainPlayModel?>>() {}.type
            mainPlayModelList = gson.fromJson(json1, type1)
            listSize = arrayList.size
            if (arrayList[position]!!.id.equals("0", ignoreCase = true)) {
                arrayList.removeAt(position)
                mainPlayModelList.removeAt(position)
            }
            val sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val editor = sharedz.edit()
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toJson(mainPlayModelList))
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, gson.toJson(arrayList))
            editor.apply()
        } else if (AudioPlayerFlag.equals(getString(R.string.top_categories), ignoreCase = true)) {
            val type =
                object : TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail?>>() {}.type
            val arrayList =
                gson.fromJson<ArrayList<ViewAllAudioListModel.ResponseData.Detail?>>(json, type)
            val type1 = object : TypeToken<ArrayList<MainPlayModel?>>() {}.type
            mainPlayModelList = gson.fromJson(json1, type1)
            listSize = arrayList.size
            if (arrayList[position]!!.id.equals("0", ignoreCase = true)) {
                arrayList.removeAt(position)
                mainPlayModelList.removeAt(position)
            }
            val sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val editor = sharedz.edit()
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toJson(mainPlayModelList))
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, gson.toJson(arrayList))
            editor.apply()
        } else if (AudioPlayerFlag.equals("SearchModelAudio", ignoreCase = true)) {
            val type = object : TypeToken<ArrayList<SearchBothModel.ResponseData?>>() {}.type
            val arrayList = gson.fromJson<ArrayList<SearchBothModel.ResponseData?>>(json, type)
            val type1 = object : TypeToken<ArrayList<MainPlayModel?>>() {}.type
            mainPlayModelList = gson.fromJson(json1, type1)
            listSize = arrayList.size
            if (arrayList[position]!!.iD.equals("0", ignoreCase = true)) {
                arrayList.removeAt(position)
                mainPlayModelList.removeAt(position)
            }
            val sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val editor = sharedz.edit()
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toJson(mainPlayModelList))
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, gson.toJson(arrayList))
            editor.apply()
        } else if (AudioPlayerFlag.equals("SearchAudio", ignoreCase = true)) {
            val type = object : TypeToken<ArrayList<SuggestedModel.ResponseData?>>() {}.type
            val arrayList = gson.fromJson<ArrayList<SuggestedModel.ResponseData?>>(json, type)
            val type1 = object : TypeToken<ArrayList<MainPlayModel?>>() {}.type
            mainPlayModelList = gson.fromJson(json1, type1)
            listSize = arrayList.size
            if (arrayList[position]!!.iD.equals("0", ignoreCase = true)) {
                arrayList.removeAt(position)
                mainPlayModelList.removeAt(position)
            }
            val sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val editor = sharedz.edit()
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toJson(mainPlayModelList))
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, gson.toJson(arrayList))
            editor.apply()
        } else if (AudioPlayerFlag.equals("DownloadListAudio", ignoreCase = true)) {
            val type = object : TypeToken<ArrayList<DownloadAudioDetails?>>() {}.type
            val arrayList = gson.fromJson<ArrayList<DownloadAudioDetails?>>(json, type)
            val type1 = object : TypeToken<ArrayList<MainPlayModel?>>() {}.type
            mainPlayModelList = gson.fromJson(json1, type1)
            listSize = arrayList.size
            if (arrayList[position]!!.id.equals("0", ignoreCase = true)) {
                arrayList.removeAt(position)
                mainPlayModelList.removeAt(position)
            }
            val sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val editor = sharedz.edit()
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toJson(mainPlayModelList))
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, gson.toJson(arrayList))
            editor.apply()
        } else if (AudioPlayerFlag.equals("Downloadlist", ignoreCase = true)) {
            val type = object : TypeToken<ArrayList<DownloadAudioDetails?>>() {}.type
            val arrayList = gson.fromJson<ArrayList<DownloadAudioDetails?>>(json, type)
            val type1 = object : TypeToken<ArrayList<MainPlayModel?>>() {}.type
            mainPlayModelList = gson.fromJson(json1, type1)
            listSize = arrayList.size
            if (arrayList[position]!!.id.equals("0", ignoreCase = true)) {
                arrayList.removeAt(position)
                mainPlayModelList.removeAt(position)
            }
            val sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val editor = sharedz.edit()
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toJson(mainPlayModelList))
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, gson.toJson(arrayList))
            editor.apply()
        } else if (AudioPlayerFlag.equals("playlist", ignoreCase = true)) {
            val type = object :
                TypeToken<ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong?>>() {}.type
            val arrayList =
                gson.fromJson<ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong?>>(
                    json,
                    type
                )
            val type1 = object : TypeToken<ArrayList<MainPlayModel?>>() {}.type
            mainPlayModelList = gson.fromJson(json1, type1)
            listSize = arrayList.size
            if (arrayList[position]!!.id.equals("0", ignoreCase = true)) {
                arrayList.removeAt(position)
                mainPlayModelList.removeAt(position)
            }
            val sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val editor = sharedz.edit()
            editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toJson(mainPlayModelList))
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, gson.toJson(arrayList))
            editor.apply()
        }
        //        binding.tvDireName.setText(R.string.Directions);
//        callButtonText(position);
//        if (mainPlayModelList.get(position).getAudioFile().equalsIgnoreCase("")) {
//            initializePlayerDisclaimer();
//        } else {
//            initializePlayer();
//        }
        audioClick = true
        getDownloadData()
        getPrepareShowData()
    }

    private fun getPrepareShowData() {
        callButtonText(position)
        if (mainPlayModelList[position].id.equals("0", ignoreCase = true)) {
//            localIntent1 = Intent("descIssue")
//            localBroadcastManager1 = LocalBroadcastManager.getInstance(ctx!!)
//            LocalBroadcastManager.getInstance(ctx!!)
//                    .registerReceiver(listener1, IntentFilter("descIssue"))
            initializePlayerDisclaimer()
        } else {
            val globalInitExoPlayer = GlobalInitExoPlayer()
            globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList)
            initializePlayer()
        }
        setPlayerCtrView()
    }

    private fun callButtonText(ps: Int) {
        /*        if (!downloadAudioDetailsList.contains(mainPlayModelList.get(position).getName())) {
            fileNameList = new ArrayList<>();
            audioFile1 = new ArrayList<>();
            playlistDownloadId = new ArrayList<>();
            SharedPreferences sharedx = getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE);
            Gson gson1 = new Gson();
            String json = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, String.valueOf(gson1));
            String json1 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadUrl, String.valueOf(gson1));
            String json2 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, String.valueOf(gson1));
            if (!json1.equalsIgnoreCase(String.valueOf(gson1))) {
                Type type = new TypeToken<List<String>>() {
                }.getType();
                fileNameList = gson1.fromJson(json, type);
                audioFile1 = gson1.fromJson(json1, type);
                playlistDownloadId = gson1.fromJson(json2, type);
            }
        }else {

        }*/
//        simpleSeekbar.setMax(100);
        binding.llDownload.setOnClickListener {
            if (isNetworkConnected(ctx)) {
//                if (AudioFragment.IsLock.equals("1", ignoreCase = true)) {
//                    val i = Intent(ctx, MembershipChangeActivity::class.java)
//                    i.putExtra("ComeFrom", "Plan")
//                    ctx.startActivity(i)
//                } else if (AudioFragment.IsLock.equals("2", ignoreCase = true)) {
//                    BWSApplication.showToast(getString(R.string.reactive_plan), ctx)
//                } else {
                if (!mainPlayModelList[position].id.equals("0"))
                    callDownload()
//                }
            } else {
                BWSApplication.showToast(getString(R.string.no_server_found), act)
            }
        }
        if (!isNetworkConnected(ctx)) {
            val gson = Gson()
            val shared = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val json2 = shared.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toString())
            if (!json2.equals(gson.toString(), ignoreCase = true)) {
                val type1 = object : TypeToken<ArrayList<MainPlayModel?>?>() {}.type
                mainPlayModelList = gson.fromJson(json2, type1)
            }
        }
        url = mainPlayModelList[ps].audioFile
        id = mainPlayModelList[ps].id
        PlayerAudioId = id
        name = mainPlayModelList[ps].name

        getDownloadData()
        GetMediaPer()
        GetMedia2()
        exoBinding.llPlay.setOnClickListener {
            if (player != null) {
                if (!mainPlayModelList[position].id.equals("0", ignoreCase = true)) {
                    if (mainPlayModelList[player.currentWindowIndex].id.equals(
                            mainPlayModelList[mainPlayModelList.size - 1].id,
                            ignoreCase = true
                        )
                        && player.duration - player.currentPosition <= 20
                    ) {
                        player.seekTo(position, 0)
                    }
                    player.playWhenReady = true
                    val p = Properties()
                    p.putValue("userId", UserID)
                    p.putValue("audioId", mainPlayModelList[position].id)
                    p.putValue("audioName", mainPlayModelList[position].name)
                    p.putValue("audioDescription", "")
                    p.putValue("directions", mainPlayModelList[position].audioDirection)
                    p.putValue("masterCategory", mainPlayModelList[position].audiomastercat)
                    p.putValue("subCategory", mainPlayModelList[position].audioSubCategory)
                    p.putValue("audioDuration", mainPlayModelList[position].audioDuration)
                    p.putValue("position", GetCurrentAudioPosition())
                    if (downloadAudioDetailsList.contains(mainPlayModelList[position].name)) {
                        p.putValue("audioType", "Downloaded")
                    } else {
                        p.putValue("audioType", "Streaming")
                    }
                    p.putValue("source", GetSourceName(ctx))
                    p.putValue("playerType", "Mini")
                    p.putValue("audioService", appStatus(ctx))
                    p.putValue("bitRate", "")
                    p.putValue("sound", hundredVolume.toString())
                    addToSegment("Audio Resumed", p, CONSTANTS.track)
                } else {
                    player.playWhenReady = true
                    val p = Properties()
                    p.putValue("userId", UserID)
                    p.putValue("position", GetCurrentAudioPosition())
                    p.putValue("source", GetSourceName(ctx))
                    p.putValue("playerType", "Mini")
                    if (downloadAudioDetailsList.contains(mainPlayModelList[position].name)) {
                        p.putValue("audioType", "Downloaded")
                    } else {
                        p.putValue("audioType", "Streaming")
                    }
                    p.putValue("bitRate", "")
                    p.putValue("audioService", appStatus(ctx))
                    p.putValue("sound", hundredVolume.toString())
                    addToSegment("Disclaimer Resumed", p, CONSTANTS.track)
                }
            } else {
                audioClick = true
                GetAllMedia()
            }
        }
        if (mainPlayModelList[ps].playlistID == null) {
            mainPlayModelList[ps].playlistID = ""
        }
        binding.tvAudioName.text = (name)
        if (player == null) {
            exoBinding.tvStartTime.text = "00:00"
        }
        exoBinding.tvSongTime.text = mainPlayModelList[ps].audioDuration
        if (!id.equals("0", ignoreCase = true)) {
            if (addToRecentPlayId.equals("", ignoreCase = true)) {
                addToRecentPlay()
            } else if (!id.equals(addToRecentPlayId, ignoreCase = true)) {
                addToRecentPlay()
                Log.e("Api call recent", id.toString())
            }
        }
        addToRecentPlayId = id

    }

    private fun setPlayerCtrView() {
        playerControlView = Assertions.checkNotNull(binding.playerControlView)
        playerControlView!!.player = player
        playerControlView!!.setProgressUpdateListener { positionx: Long, bufferedPosition: Long ->
            exoBinding.exoProgress.setPosition(positionx)
            exoBinding.exoProgress.setBufferedPosition(bufferedPosition)
            //            myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(position).getImageFile());
            exoBinding.tvStartTime.text = (String.format(
                "%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(positionx),
                TimeUnit.MILLISECONDS.toSeconds(positionx) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(
                        positionx
                    )
                )
            ))
            if (player != null) {
                exoBinding.exoProgress.setDuration(player.duration)
                if (player.currentPosition >= oldSongPos + 299500 && player.currentPosition <= oldSongPos + 310000) {
                    oldSongPos = positionx
                    callHeartbeat()
                }
            }
        }
        try {
            getMediaBitmap(ctx, mainPlayModelList[position].imageFile)
        } catch (e: OutOfMemoryError) {
            println(e)
        }
        playerControlView!!.isFocusable = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            playerControlView!!.isFocusedByDefault = true
        }
        playerControlView!!.show()

    }

    private fun initializePlayer() {
        try {
//        player = new SimpleExoPlayer.Builder(getApplicationContext()).build();
            isDisclaimer = 0
            if (audioClick) {
                val globalInitExoPlayer = GlobalInitExoPlayer()
                globalInitExoPlayer.GlobleInItPlayer(
                    ctx,
                    position,
                    downloadAudioDetailsList,
                    mainPlayModelList,
                    "Main"
                )
                setPlayerCtrView()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
//        try {
        if (player != null) {
            player.setWakeMode(C.WAKE_MODE_NONE)
            player.setHandleWakeLock(true)
            player.setHandleAudioBecomingNoisy(true)
            player.addListener(object : Player.EventListener {
                override fun onTracksChanged(
                    trackGroups: TrackGroupArray,
                    trackSelections: TrackSelectionArray
                ) {
                    Log.e("TAG", "Listener-onTracksChanged... Main Activity")
                    oldSongPos = 0
                    val sharedsa =
                        ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                    val gson = Gson()
                    val json =
                        sharedsa.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toString())
                    if (!json.equals(gson.toString(), ignoreCase = true)) {
                        val type = object : TypeToken<ArrayList<MainPlayModel?>?>() {}.type
                        mainPlayModelList = gson.fromJson(json, type)
                    }
                    position = player.currentWindowIndex
                    val globalInitExoPlayer = GlobalInitExoPlayer()
                    globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList)
                    val shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                    val editor = shared.edit()
                    editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position)
                    editor.apply()
                    if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
                        || AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                    ) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                    val uiModeManager = getSystemService(UI_MODE_SERVICE) as UiModeManager
                    if (uiModeManager.nightMode == UiModeManager.MODE_NIGHT_AUTO || uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES || uiModeManager.nightMode == UiModeManager.MODE_NIGHT_CUSTOM) {
                        uiModeManager.nightMode = UiModeManager.MODE_NIGHT_NO
                    }
                    getDownloadData()
                    setPlayerCtrView()
                    GetMediaPer()
                    callButtonText(position)

                    val p = Properties()
                    p.putValue("userId", UserID)
                    p.putValue("coUserId", CoUserID)
                    p.putValue("audioId", mainPlayModelList[position].id)
                    p.putValue("audioName", mainPlayModelList[position].name)
                    p.putValue("audioDescription", "")
                    p.putValue("directions", mainPlayModelList[position].audioDirection)
                    p.putValue("masterCategory", mainPlayModelList[position].audiomastercat)
                    p.putValue("subCategory", mainPlayModelList[position].audioSubCategory)
                    p.putValue("audioDuration", mainPlayModelList[position].audioDuration)
                    p.putValue("position", GetCurrentAudioPosition())
                    if (downloadAudioDetailsList.contains(mainPlayModelList[position].name)) {
                        p.putValue("audioType", "Downloaded")
                    } else {
                        p.putValue("audioType", "Streaming")
                    }
                    p.putValue("source", GetSourceName(ctx))
                    p.putValue("playerType", "Mini")
                    p.putValue("audioService", appStatus(act))
                    p.putValue("bitRate", "")
                    p.putValue("sound", hundredVolume.toString())
                    addToSegment("Audio Started", p, CONSTANTS.track)
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (player.playbackState == ExoPlayer.STATE_BUFFERING) {
                        exoBinding.llPlay.visibility = View.GONE
                        exoBinding.llPause.visibility = View.GONE
                        exoBinding.progressBar.visibility = View.VISIBLE
                    } else if (isPlaying) {
                        exoBinding.llPlay.visibility = View.GONE
                        exoBinding.llPause.visibility = View.VISIBLE
                        exoBinding.progressBar.visibility = View.GONE
                    } else if (!isPlaying) {
                        exoBinding.llPlay.visibility = View.VISIBLE
                        exoBinding.llPause.visibility = View.GONE
                        exoBinding.progressBar.visibility = View.GONE
                    }
                    exoBinding.exoProgress.setBufferedPosition(player.bufferedPosition)
                    exoBinding.exoProgress.setPosition(player.currentPosition)
                    val globalInitExoPlayer = GlobalInitExoPlayer()
                    globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList)
                    exoBinding.exoProgress.setDuration(player.duration)
                    exoBinding.tvStartTime.setText(
                        String.format(
                            "%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(player.currentPosition),
                            TimeUnit.MILLISECONDS.toSeconds(player.currentPosition) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(player.currentPosition)
                            )
                        )
                    )
                    if (player.currentPosition >= oldSongPos + 299500 && player.currentPosition <= oldSongPos + 310000) {
                        oldSongPos = player.currentPosition
                        callHeartbeat()
                    }
                }

                override fun onPlaybackStateChanged(state: Int) {
//                        myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(position).getImageFile());
                    if (state == ExoPlayer.STATE_READY) {

                        val p = Properties()
                        p.putValue("userId", UserID)
                        p.putValue("coUserId", CoUserID)
                        p.putValue("audioId", mainPlayModelList[position].id)
                        p.putValue("audioName", mainPlayModelList[position].name)
                        p.putValue("audioDescription", "")
                        p.putValue("directions", mainPlayModelList[position].audioDirection)
                        p.putValue("masterCategory", mainPlayModelList[position].audiomastercat)
                        p.putValue("subCategory", mainPlayModelList[position].audioSubCategory)
                        p.putValue("audioDuration", mainPlayModelList[position].audioDuration)
                        p.putValue("position", GetCurrentAudioPosition())
                        if (downloadAudioDetailsList.contains(mainPlayModelList[position].name)) {
                            p.putValue("audioType", "Downloaded")
                        } else {
                            p.putValue("audioType", "Streaming")
                        }
                        p.putValue("source", GetSourceName(ctx))
                        p.putValue("playerType", "Mini")
                        p.putValue("audioService", appStatus(act))
                        p.putValue("bitRate", "")
                        p.putValue("sound", hundredVolume.toString())
                        addToSegment("Audio Buffer Completed", p, CONSTANTS.track)
                        if (player.playWhenReady) {
                            exoBinding.llPlay.visibility = View.GONE
                            exoBinding.llPause.visibility = View.VISIBLE
                            exoBinding.progressBar.visibility = View.GONE
                            callHeartbeat()
                        } else if (!player.playWhenReady) {
                            exoBinding.llPlay.visibility = View.VISIBLE
                            exoBinding.llPause.visibility = View.GONE
                            exoBinding.progressBar.visibility = View.GONE
                        }
//                        isprogressbar = false;
                    } else if (state == ExoPlayer.STATE_BUFFERING) {
                        exoBinding.llPlay.visibility = View.GONE
                        exoBinding.llPause.visibility = View.GONE
                        exoBinding.progressBar.visibility = View.VISIBLE

                        val p = Properties()
                        p.putValue("userId", UserID)
                        p.putValue("coUserId", CoUserID)
                        p.putValue("audioId", mainPlayModelList[position].id)
                        p.putValue("audioName", mainPlayModelList[position].name)
                        p.putValue("audioDescription", "")
                        p.putValue("directions", mainPlayModelList[position].audioDirection)
                        p.putValue("masterCategory", mainPlayModelList[position].audiomastercat)
                        p.putValue("subCategory", mainPlayModelList[position].audioSubCategory)
                        p.putValue("audioDuration", mainPlayModelList[position].audioDuration)
                        p.putValue("position", GetCurrentAudioPosition())
                        if (downloadAudioDetailsList.contains(mainPlayModelList[position].name)) {
                            p.putValue("audioType", "Downloaded")
                        } else {
                            p.putValue("audioType", "Streaming")
                        }
                        p.putValue("source", GetSourceName(ctx))
                        p.putValue("playerType", "Mini")
                        p.putValue("audioService", appStatus(act))
                        p.putValue("bitRate", "")
                        p.putValue("sound", hundredVolume.toString())
                        addToSegment("Audio Buffer Started", p, CONSTANTS.track)
                    } else if (state == ExoPlayer.STATE_ENDED) {
                        try {
                            val p = Properties()
                            p.putValue("userId", UserID)
                            p.putValue("coUserId", CoUserID)
                            p.putValue("audioId", mainPlayModelList[position].id)
                            p.putValue("audioName", mainPlayModelList[position].name)
                            p.putValue("audioDescription", "")
                            p.putValue("directions", mainPlayModelList[position].audioDirection)
                            p.putValue("masterCategory", mainPlayModelList[position].audiomastercat)
                            p.putValue("subCategory", mainPlayModelList[position].audioSubCategory)
                            p.putValue("audioDuration", mainPlayModelList[position].audioDuration)
                            p.putValue("position", GetCurrentAudioPosition())
                            if (downloadAudioDetailsList.contains(mainPlayModelList[position].name)) {
                                p.putValue("audioType", "Downloaded")
                            } else {
                                p.putValue("audioType", "Streaming")
                            }
                            p.putValue("source", GetSourceName(ctx))
                            p.putValue("playerType", "Main")
                            p.putValue("audioService", appStatus(ctx))
                            p.putValue("bitRate", "")
                            p.putValue("sound", hundredVolume.toString())
                            addToSegment("Audio Completed", p, CONSTANTS.track)
                            if (mainPlayModelList[player.currentWindowIndex].id.equals(
                                    mainPlayModelList[mainPlayModelList.size - 1].id,
                                    ignoreCase = true
                                )
                            ) {
                                exoBinding.llPlay.visibility = View.VISIBLE
                                exoBinding.llPause.visibility = View.GONE
                                exoBinding.progressBar.visibility = View.GONE
                                player.playWhenReady = false
                                localIntent.putExtra("MyData", "pause")
                                localBroadcastManager.sendBroadcast(localIntent)
                                val p = Properties()
                                p.putValue("userId", UserID)
                                p.putValue("audioId", mainPlayModelList[position].id)
                                p.putValue("audioName", mainPlayModelList[position].name)
                                p.putValue("audioDescription", "")
                                p.putValue("directions", mainPlayModelList[position].audioDirection)
                                p.putValue(
                                    "masterCategory",
                                    mainPlayModelList[position].audiomastercat
                                )
                                p.putValue(
                                    "subCategory",
                                    mainPlayModelList[position].audioSubCategory
                                )
                                p.putValue(
                                    "audioDuration",
                                    mainPlayModelList[position].audioDuration
                                )
                                p.putValue("position", GetCurrentAudioPosition())
                                if (downloadAudioDetailsList.contains(mainPlayModelList[position].name)) {
                                    p.putValue("audioType", "Downloaded")
                                } else {
                                    p.putValue("audioType", "Streaming")
                                }
                                p.putValue("source", GetSourceName(ctx))
                                p.putValue("playerType", "Mini")
                                p.putValue("audioService", appStatus(ctx))
                                p.putValue("bitRate", "")
                                p.putValue("sound", hundredVolume.toString())
                                val source = GetSourceName(ctx)
                                if (!source.equals("Playlist", ignoreCase = true) && !source.equals(
                                        "Downloaded Playlists",
                                        ignoreCase = true
                                    )
                                ) {
                                    addToSegment("Audio Playback Completed", p, CONSTANTS.track)
                                }
                                if (AudioPlayerFlag.equals(
                                        "playlist",
                                        ignoreCase = true
                                    ) || AudioPlayerFlag.equals("Downloadlist", ignoreCase = true)
                                ) {
                                    val shared1 = ctx.getSharedPreferences(
                                        CONSTANTS.PREF_KEY_SEGMENT_PLAYLIST,
                                        MODE_PRIVATE
                                    )
                                    val PlaylistID =
                                        shared1.getString(CONSTANTS.PREF_KEY_PlaylistID, "")
                                    val PlaylistName =
                                        shared1.getString(CONSTANTS.PREF_KEY_PlaylistName, "")
                                    val PlaylistDescription = shared1.getString(
                                        CONSTANTS.PREF_KEY_PlaylistDescription,
                                        ""
                                    )
                                    val PlaylistType =
                                        shared1.getString(CONSTANTS.PREF_KEY_PlaylistType, "")
                                    val Totalhour =
                                        shared1.getString(CONSTANTS.PREF_KEY_Totalhour, "")
                                    val Totalminute =
                                        shared1.getString(CONSTANTS.PREF_KEY_Totalminute, "")
                                    val TotalAudio =
                                        shared1.getString(CONSTANTS.PREF_KEY_TotalAudio, "")
                                    val ScreenView =
                                        shared1.getString(CONSTANTS.PREF_KEY_ScreenView, "")
                                    val p = Properties()
                                    p.putValue("userId", UserID)
                                    p.putValue("playlistId", PlaylistID)
                                    p.putValue("playlistName", PlaylistName)
                                    p.putValue("playlistDescription", PlaylistDescription)
                                    if (PlaylistType.equals("1", ignoreCase = true)) {
                                        p.putValue("playlistType", "Created")
                                    } else if (PlaylistType.equals("0", ignoreCase = true)) {
                                        p.putValue("playlistType", "Default")
                                    }
                                    when {
                                        Totalhour.equals("", ignoreCase = true) -> {
                                            p.putValue("playlistDuration", "0h " + Totalminute + "m")
                                        }
                                        Totalminute.equals("", ignoreCase = true) -> {
                                            p.putValue("playlistDuration", Totalhour + "h 0m")
                                        }
                                        else -> {
                                            p.putValue(
                                                "playlistDuration",
                                                Totalhour + "h " + Totalminute + "m"
                                            )
                                        }
                                    }
                                    p.putValue("audioCount", TotalAudio)
                                    p.putValue("source", ScreenView)
                                    p.putValue("playerType", "Mini")
                                    p.putValue("audioService", appStatus(ctx))
                                    p.putValue("sound", hundredVolume.toString())
                                    addToSegment("Playlist Completed", p, CONSTANTS.track)
                                    Log.e("Last audio End", mainPlayModelList[position].name)
                                } else {
                                    Log.e("Curr audio End", mainPlayModelList[position].name)
                                }
                                /*new Handler().postDelayed(() -> {
                            playerNotificationManager.setPlayer(null);
                        }, 2 * 1000);*/
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            Log.e("End State: ", e.message!!)
                        }

                    } else if (state == ExoPlayer.STATE_IDLE) {
                        if (AudioInterrupted) {
                            Log.e("Exo Player state", "ExoPlayer.STATE_IDLE")
                        }
                    }
                }

                override fun onPlayerError(error: ExoPlaybackException) {
                    val intruptMethod: String?
                    val p = Properties()
                    p.putValue("userId", UserID)
                    p.putValue("CoUserId", CoUserID)
                    p.putValue("audioId", mainPlayModelList[position].id)
                    p.putValue("audioName", mainPlayModelList[position].name)
                    p.putValue("audioDescription", "")
                    p.putValue("directions", mainPlayModelList[position].audioDirection)
                    p.putValue("masterCategory", mainPlayModelList[position].audiomastercat)
                    p.putValue("subCategory", mainPlayModelList[position].audioSubCategory)
                    p.putValue("audioDuration", mainPlayModelList[position].audioDuration)
                    p.putValue("position", GetCurrentAudioPosition())
                    var audioType = ""
                    audioType =
                        if (downloadAudioDetailsList.contains(mainPlayModelList[position].name)) {
                            p.putValue("audioType", "Downloaded")
                            "Downloaded"
                        } else {
                            p.putValue("audioType", "Streaming")
                            "Streaming"
                        }
                    p.putValue("source", GetSourceName(ctx))
                    p.putValue("playerType", "Main")
                    p.putValue("audioService", appStatus(ctx))
                    p.putValue("bitRate", "")
                    p.putValue("appType", "Android")
                    p.putValue("sound", hundredVolume.toString())
                    when (error.type) {
                        ExoPlaybackException.TYPE_SOURCE -> {
                            p.putValue(
                                "interruptionMethod",
                                error.message + " " + error.sourceException.message
                            )
                            intruptMethod = error.message + " " + error.sourceException.message
                            Log.e(
                                "onPlaybackError",
                                error.message + " " + error.sourceException.message
                            )
                        }
                        ExoPlaybackException.TYPE_RENDERER -> {
                            p.putValue(
                                "interruptionMethod",
                                error.message + " " + error.rendererException.message
                            )
                            intruptMethod = error.message + " " + error.rendererException.message
                            Log.e(
                                "onPlaybackError",
                                error.message + " " + error.rendererException.message
                            )
                        }
                        ExoPlaybackException.TYPE_UNEXPECTED -> {
                            p.putValue(
                                "interruptionMethod",
                                error.message + " " + error.unexpectedException.message
                            )
                            intruptMethod = error.message + " " + error.unexpectedException.message
                            Log.e(
                                "onPlaybackError",
                                error.message + " " + error.unexpectedException.message
                            )
                        }
                        ExoPlaybackException.TYPE_REMOTE -> {
                            p.putValue("interruptionMethod", error.message)
                            intruptMethod = error.message
                            Log.e("onPlaybackError", error.message!!)
                        }
                        else -> {
                            p.putValue("interruptionMethod", error.message)
                            intruptMethod = error.message
                            Log.e("onPlaybackError", error.message!!)
                        }
                    }
                    AudioInterrupted = true
                    addToSegment("Audio Interrupted", p, CONSTANTS.track)
                    val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
                    //should check null because in airplane mode it will be null
                    val nc: NetworkCapabilities?
                    var downSpeed = 0f
                    var batLevel = 0
                    var upSpeed = 0f
                    if (isNetworkConnected(ctx)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            nc = cm.getNetworkCapabilities(cm.activeNetwork)
                            downSpeed = nc!!.linkDownstreamBandwidthKbps.toFloat() / 1000
                            upSpeed = (nc.linkUpstreamBandwidthKbps / 1000).toFloat()
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val bm = getSystemService(BATTERY_SERVICE) as BatteryManager
                        batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                    }
                    try {
                        if (isNetworkConnected(ctx)) {
                            val listCall = APINewClient.getClient().getAudioInterruption(
                                CoUserID,
                                this@MyPlayerActivity.UserID,
                                mainPlayModelList[position].id,
                                mainPlayModelList[position].name,
                                "",
                                mainPlayModelList[position].audioDirection,
                                mainPlayModelList[position].audiomastercat,
                                mainPlayModelList[position].audioSubCategory,
                                mainPlayModelList[position].audioDuration,
                                "",
                                audioType,
                                "Main",
                                hundredVolume.toString(),
                                appStatus(ctx),
                                GetSourceName(ctx),
                                GetCurrentAudioPosition(),
                                "",
                                intruptMethod,
                                batLevel.toString(),
                                BatteryStatus,
                                downSpeed.toString(),
                                upSpeed.toString(),
                                "Android"
                            )
                            listCall.enqueue(object : Callback<AudioInterruptionModel?> {
                                override fun onResponse(
                                    call: Call<AudioInterruptionModel?>,
                                    response: Response<AudioInterruptionModel?>
                                ) {
                                    val listModel = response.body()
                                }

                                override fun onFailure(
                                    call: Call<AudioInterruptionModel?>,
                                    t: Throwable
                                ) {
                                }
                            })
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            })
            exoBinding.exoProgress.addListener(object : OnScrubListener {
                override fun onScrubStart(timeBar: TimeBar, pos: Long) {
                    exoBinding.exoProgress.setPosition(pos)
                    exoBinding.exoProgress.setDuration(player.duration)
                    oldSeekPosition = pos
                    val globalInitExoPlayer = GlobalInitExoPlayer()
                    globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList)
                    globalInitExoPlayer.UpdateNotificationAudioPLayer(ctx)
                    val p = Properties()
                    p.putValue("userId", UserID)
                    p.putValue("audioId", mainPlayModelList[position].id)
                    p.putValue("audioName", mainPlayModelList[position].name)
                    p.putValue("audioDescription", "")
                    p.putValue("directions", mainPlayModelList[position].audioDirection)
                    p.putValue("masterCategory", mainPlayModelList[position].audiomastercat)
                    p.putValue("subCategory", mainPlayModelList[position].audioSubCategory)
                    p.putValue("audioDuration", mainPlayModelList[position].audioDuration)
                    p.putValue("position", GetCurrentAudioPosition())
                    p.putValue("seekPosition", pos)
                    when {
                        oldSeekPosition < pos -> {
                            p.putValue("seekDirection", "Forwarded")
                        }
                        oldSeekPosition > pos -> {
                            p.putValue("seekDirection", "Backwarded")
                        }
                        else -> {
                            p.putValue("seekDirection", "")
                        }
                    }
                    if (downloadAudioDetailsList.contains(mainPlayModelList[position].name)) {
                        p.putValue("audioType", "Downloaded")
                    } else {
                        p.putValue("audioType", "Streaming")
                    }
                    p.putValue("source", GetSourceName(ctx))
                    p.putValue("playerType", "Main")
                    p.putValue("audioService", appStatus(ctx))
                    p.putValue("bitRate", "")
                    p.putValue("sound", hundredVolume.toString())
                    addToSegment("Audio Seek Started", p, CONSTANTS.track)
                }

                override fun onScrubMove(timeBar: TimeBar, position: Long) {}
                override fun onScrubStop(timeBar: TimeBar, pos: Long, canceled: Boolean) {
                    player.seekTo(position, pos)
                    exoBinding.exoProgress.setPosition(pos)
                    exoBinding.exoProgress.setDuration(player.duration)
                    exoBinding.tvStartTime.text = String.format(
                        "%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(pos),
                        TimeUnit.MILLISECONDS.toSeconds(pos) - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(
                                pos
                            )
                        )
                    )
                    val globalInitExoPlayer = GlobalInitExoPlayer()
                    globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList)
                    globalInitExoPlayer.UpdateNotificationAudioPLayer(ctx)
                    val p = Properties()
                    p.putValue("userId", UserID)
                    p.putValue("audioId", mainPlayModelList[position].id)
                    p.putValue("audioName", mainPlayModelList[position].name)
                    p.putValue("audioDescription", "")
                    p.putValue("directions", mainPlayModelList[position].audioDirection)
                    p.putValue("masterCategory", mainPlayModelList[position].audiomastercat)
                    p.putValue("subCategory", mainPlayModelList[position].audioSubCategory)
                    p.putValue("audioDuration", mainPlayModelList[position].audioDuration)
                    p.putValue("position", GetCurrentAudioPosition())
                    p.putValue("seekPosition", pos)
                    when {
                        oldSeekPosition < pos -> {
                            p.putValue("seekDirection", "Forwarded")
                        }
                        oldSeekPosition > pos -> {
                            p.putValue("seekDirection", "Backwarded")
                        }
                        else -> {
                            p.putValue("seekDirection", "")
                        }
                    }
                    if (downloadAudioDetailsList.contains(mainPlayModelList[position].name)) {
                        p.putValue("audioType", "Downloaded")
                    } else {
                        p.putValue("audioType", "Streaming")
                    }
                    p.putValue("source", GetSourceName(ctx))
                    p.putValue("playerType", "Main")
                    p.putValue("audioService", appStatus(ctx))
                    p.putValue("bitRate", "")
                    p.putValue("sound", hundredVolume.toString())
                    addToSegment("Audio Seek Completed", p, CONSTANTS.track)
                }
            })

            if (player.playbackState == ExoPlayer.STATE_BUFFERING) {
                exoBinding.llPlay.visibility = View.GONE
                exoBinding.llPause.visibility = View.GONE
                exoBinding.progressBar.visibility = View.VISIBLE
            } else if (player.playWhenReady) {
                exoBinding.llPlay.visibility = View.GONE
                exoBinding.llPause.visibility = View.VISIBLE
                exoBinding.progressBar.visibility = View.GONE
            } else if (!player.playWhenReady) {
                exoBinding.llPlay.visibility = View.VISIBLE
                exoBinding.llPause.visibility = View.GONE
                exoBinding.progressBar.visibility = View.GONE
            }
            val globalInitExoPlayer = GlobalInitExoPlayer()
            globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList)
            exoBinding.exoProgress.setBufferedPosition(player.bufferedPosition)
            exoBinding.exoProgress.setPosition(player.currentPosition)
            exoBinding.exoProgress.setDuration(player.duration)
            setPlayerCtrView()
        } else {
            if (audioClick) {
                exoBinding.progressBar.visibility = View.GONE
                exoBinding.llPlay.visibility = View.GONE
                exoBinding.llPause.visibility = View.VISIBLE
            } else if (PlayerINIT) {
                exoBinding.progressBar.visibility = View.GONE
                exoBinding.llPlay.visibility = View.GONE
                exoBinding.llPause.visibility = View.VISIBLE
            }
        }
        callAllDisable(true)
        epAllClicks()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            //            Log.e("init player State: ", e.getMessage());
//        }
    }

    private fun epAllClicks() {
        try {

            exoBinding.llPause.setOnClickListener {
                try {
                    player.playWhenReady = false
                    val pss = player.currentWindowIndex
                    //                    myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(pss).getImageFile());
                    exoBinding.llPlay.visibility = View.VISIBLE
                    exoBinding.llPause.visibility = View.GONE
                    exoBinding.progressBar.visibility = View.GONE
                    val p = Properties()
                    p.putValue("userId", UserID)
                    p.putValue("audioId", mainPlayModelList[position].id)
                    p.putValue("audioName", mainPlayModelList[position].name)
                    p.putValue("audioDescription", "")
                    p.putValue("directions", mainPlayModelList[position].audioDirection)
                    p.putValue("masterCategory", mainPlayModelList[position].audiomastercat)
                    p.putValue("subCategory", mainPlayModelList[position].audioSubCategory)
                    p.putValue("audioDuration", mainPlayModelList[position].audioDuration)
                    p.putValue("position", GetCurrentAudioPosition())
                    if (downloadAudioDetailsList.contains(mainPlayModelList[position].name)) {
                        p.putValue("audioType", "Downloaded")
                    } else {
                        p.putValue("audioType", "Streaming")
                    }
                    p.putValue("source", GetSourceName(ctx))
                    p.putValue("playerType", "Mini")
                    p.putValue("audioService", appStatus(ctx))
                    p.putValue("bitRate", "")
                    p.putValue("sound", hundredVolume.toString())
                    addToSegment("Audio Paused", p, CONSTANTS.track)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
            exoBinding.llForwardSec.setOnClickListener {
                try {
                    if (player.duration - player.currentPosition <= 30000) {
                        BWSApplication.showToast("Please Wait... ", act)
                    } else {
                        player.seekTo(position, player.currentPosition + 30000)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
            exoBinding.llBackWordSec.setOnClickListener {
                try {
                    if (player.currentPosition > 30000) {
                        player.seekTo(position, player.currentPosition - 30000)
                    } else if (player.currentPosition < 30000) {
                        player.seekTo(position, 0)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.e("ep all State: ", e.message!!)
        }
    }

    private fun callAllDisable(b: Boolean) {
        if (b) {
            binding.llInfo.alpha = 1f
            binding.llInfo.isClickable = true
            binding.llInfo.isEnabled = true
            exoBinding.llForwardSec.isClickable = true
            exoBinding.llForwardSec.isEnabled = true
            exoBinding.llForwardSec.alpha = 1f
            exoBinding.llBackWordSec.isClickable = true
            exoBinding.llBackWordSec.isEnabled = true
            exoBinding.llBackWordSec.alpha = 1f
            binding.llDownload.isClickable = true
            binding.llDownload.isEnabled = true
            binding.llDownload.alpha = 1f
            exoBinding.rlSeekbar.isClickable = true
            exoBinding.rlSeekbar.isEnabled = true
            exoBinding.exoProgress.isClickable = true
            exoBinding.exoProgress.isEnabled = true
//            callllInfoViewQClicks()
        } else {
            exoBinding.llForwardSec.isClickable = false
            exoBinding.llForwardSec.isEnabled = false
            exoBinding.llForwardSec.alpha = 0.6f
            exoBinding.llBackWordSec.isClickable = false
            exoBinding.llBackWordSec.isEnabled = false
            exoBinding.llBackWordSec.alpha = 0.6f
            binding.llInfo.isClickable = false
            binding.llInfo.isEnabled = false
            binding.llInfo.alpha = 0.6f
            binding.llDownload.isClickable = false
            binding.llDownload.isEnabled = false
            binding.llDownload.alpha = 0.6f
            exoBinding.rlSeekbar.isClickable = false
            exoBinding.rlSeekbar.isEnabled = false
            exoBinding.exoProgress.isClickable = false
            exoBinding.exoProgress.isEnabled = false
        }
    }

    private fun addToRecentPlay() {
        if (isNetworkConnected(ctx)) {
            val listCall = APINewClient.getClient().getRecentlyPlayed(CoUserID, id)
            listCall.enqueue(object : Callback<SucessModel?> {
                override fun onResponse(
                    call: Call<SucessModel?>,
                    response: Response<SucessModel?>
                ) {
                    try {
                        val model: SucessModel = response.body()!!
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<SucessModel?>, t: Throwable) {
                }
            })
        }
    }

    private fun callDownload() {
        downloadClick = true
        if (downloadAudioDetailsList.contains(mainPlayModelList[position].name)) {
            disableDownload()
            SaveMedia(100)
        } else {
            fileNameList = ArrayList()
            audioFile1 = ArrayList()
            playlistDownloadId = ArrayList()
            val sharedx = getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE)
            val gson1 = Gson()
            val json = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, gson1.toString())
            val json1 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadUrl, gson1.toString())
            val json2 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, gson1.toString())
            if (!json1.equals(gson1.toString(), ignoreCase = true)) {
                val type = object : TypeToken<ArrayList<String?>>() {}.type
                fileNameList = gson1.fromJson(json, type)
                audioFile1 = gson1.fromJson(json1, type)
                playlistDownloadId = gson1.fromJson(json2, type)
            }
            audioFile1.add(mainPlayModelList[position].audioFile)
            fileNameList.add(mainPlayModelList[position].name)
            playlistDownloadId.add("")
            val shared = getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE)
            val editor = shared.edit()
            val gson = Gson()
            val nameJson = gson.toJson(fileNameList)
            val urlJson = gson.toJson(audioFile1)
            val playlistIdJson = gson.toJson(playlistDownloadId)
            editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson)
            editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson)
            editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson)
            editor.apply()
            if (!DownloadMedia.isDownloading) {
                DownloadMedia.isDownloading = true
                val downloadMedia = DownloadMedia(applicationContext, act)
                downloadMedia.encrypt1(audioFile1, fileNameList, playlistDownloadId)
            }
            binding.pbProgress.visibility = View.VISIBLE
            binding.ivDownloads.visibility = View.GONE
            GetMediaPer()
            disableDownload()
            SaveMedia(0)
            val p = Properties()
            p.putValue("userId", UserID)
            p.putValue("audioId", mainPlayModelList[position].id)
            p.putValue("audioName", mainPlayModelList[position].name)
            p.putValue("audioDescription", "")
            p.putValue("directions", mainPlayModelList[position].audioDirection)
            p.putValue("masterCategory", mainPlayModelList[position].audiomastercat)
            p.putValue("subCategory", mainPlayModelList[position].audioSubCategory)
            p.putValue("audioDuration", mainPlayModelList[position].audioDuration)
            p.putValue("position", GetCurrentAudioPosition())
            if (downloadAudioDetailsList.contains(mainPlayModelList[position].name)) {
                p.putValue("audioType", "Downloaded")
            } else {
                p.putValue("audioType", "Streaming")
            }
            p.putValue("source", GetSourceName(ctx))
            p.putValue("playerType", "Main")
            p.putValue("bitRate", "")
            p.putValue("audioService", appStatus(ctx))
            p.putValue("sound", hundredVolume.toString())
            BWSApplication.addToSegment("Audio Download Started", p, CONSTANTS.track)
            // }
        }
    }

    private fun disableDownload() {
        binding.ivDownloads.setImageResource(R.drawable.ic_download_done_icon)
        binding.ivDownloads.setColorFilter(
            ContextCompat.getColor(
                ctx,
                R.color.white
            ),
            PorterDuff.Mode.SRC_IN
        )

        binding.llDownload.isClickable = false
        binding.llDownload.isEnabled = false
    }

    private fun enableDownload() {
        binding.ivDownloads.setImageResource(R.drawable.ic_white_download_icon)
        binding.llDownload.isClickable = true
        binding.llDownload.isEnabled = true
        binding.ivDownloads.setColorFilter(
            ContextCompat.getColor(
                ctx,
                R.color.white
            ),
            PorterDuff.Mode.SRC_IN
        )
    }

    private fun SaveMedia(progressx: Int) {
        downloadClick = true
        val downloadAudioDetails = DownloadAudioDetails()
        //                if (audioPlay) {
        downloadAudioDetails.id = mainPlayModelList[position].id
        downloadAudioDetails.name = mainPlayModelList[position].name
        downloadAudioDetails.audioFile = mainPlayModelList[position].audioFile
        downloadAudioDetails.audioDirection = mainPlayModelList[position].audioDirection
        downloadAudioDetails.audiomastercat = mainPlayModelList[position].audiomastercat
        downloadAudioDetails.audioSubCategory = mainPlayModelList[position].audioSubCategory
        downloadAudioDetails.imageFile = mainPlayModelList[position].imageFile
        downloadAudioDetails.like = mainPlayModelList[position].like
        downloadAudioDetails.audioDuration = mainPlayModelList[position].audioDuration
        /* } else if (queuePlay) {
            downloadAudioDetails.setID(addToQueueModelList.get(position).getID());
            downloadAudioDetails.setName(addToQueueModelList.get(position).getName());
            downloadAudioDetails.setAudioFile(addToQueueModelList.get(position).getAudioFile());
            downloadAudioDetails.setAudioDirection(addToQueueModelList.get(position).getAudioDirection());
            downloadAudioDetails.setAudiomastercat(addToQueueModelList.get(position).getAudiomastercat());
            downloadAudioDetails.setAudioSubCategory(addToQueueModelList.get(position).getAudioSubCategory());
            downloadAudioDetails.setImageFile(addToQueueModelList.get(position).getImageFile());
            downloadAudioDetails.setLike(addToQueueModelList.get(position).getLike());
            downloadAudioDetails.setAudioDuration(addToQueueModelList.get(position).getAudioDuration());
        }*/downloadAudioDetails.download = "1"
        downloadAudioDetails.isSingle = "1"
        downloadAudioDetails.playlistId = ""
        if (progressx == 0) {
            downloadAudioDetails.isDownload = "pending"
        } else {
            downloadAudioDetails.isDownload = "Complete"
        }
        downloadAudioDetails.downloadProgress = progressx
        try {
            AudioDatabase.databaseWriteExecutor.execute {
                DB!!.taskDao().insertMedia(downloadAudioDetails)
            }
        } catch (e: java.lang.Exception) {
            println(e.message)
        } catch (e: OutOfMemoryError) {
            println(e.message)
        }
    }

    fun GetMedia2() {
        try {
            DB!!.taskDao().getaudioByPlaylist1(mainPlayModelList[position].audioFile, "").observe(
                this,
                { audiolist: List<DownloadAudioDetails> ->
                    if (audiolist.isNotEmpty()) {
                        disableDownload()
                        if (audiolist[0].downloadProgress == 100) {
                            binding.ivDownloads.visibility = View.VISIBLE
                            binding.pbProgress.visibility = View.GONE
                        } else {
                            binding.ivDownloads.visibility = View.GONE
                            binding.pbProgress.visibility = View.VISIBLE
                            GetMediaPer()
                        }
                        DB!!.taskDao()
                            .getaudioByPlaylist1(mainPlayModelList[position].audioFile, "")
                            .removeObserver { audiolistx: List<DownloadAudioDetails?>? -> }
                    } else {
                        /* boolean entryNot = false;
             for (int i = 0; i < fileNameList.size(); i++) {
                 if (fileNameList.get(i).equalsIgnoreCase(mainPlayModelList.get(position).getName())
                         && playlistDownloadId.get(i).equalsIgnoreCase("")) {
                     entryNot = true;
                     break;
                 }
             }
             if (!entryNot) {*/
                        enableDownload()
                        binding.ivDownloads.visibility = View.VISIBLE
                        binding.pbProgress.visibility = View.GONE
                        /*    } else {
        GetMediaPer();
        disableDownload();
    }*/DB!!.taskDao().getaudioByPlaylist1(mainPlayModelList[position].audioFile, "")
                            .removeObserver { }
                    }
                })
        } catch (e: java.lang.Exception) {
            println(e.message)
        } catch (e: OutOfMemoryError) {
            println(e.message)
        }
    }

    private fun GetMediaPer() {
        if (fileNameList.size != 0) {
            for (i in fileNameList.indices) {
                if (fileNameList.get(i).equals(
                        mainPlayModelList[position].name,
                        ignoreCase = true
                    ) && playlistDownloadId.get(i).equals("", ignoreCase = true)
                ) {
                    if (!DownloadMedia.filename.equals(
                            "",
                            ignoreCase = true
                        ) && DownloadMedia.filename.equals(
                            mainPlayModelList[position].name,
                            ignoreCase = true
                        )
                    ) {
                        if (DownloadMedia.downloadProgress <= 100) {
                            if (DownloadMedia.downloadProgress == 100) {
                                binding.pbProgress.visibility = View.GONE
                                binding.ivDownloads.visibility = View.VISIBLE
                                disableDownload()
//                                handler2.removeCallbacks(UpdateSongTime2)
                            } else {
                                binding.pbProgress.progress = DownloadMedia.downloadProgress
                                binding.pbProgress.visibility = View.VISIBLE
                                binding.ivDownloads.visibility = View.GONE
                                disableDownload()
//                                handler2.postDelayed(UpdateSongTime2, 10000)
                            }
                            break
                        } else {
                            binding.pbProgress.visibility = View.GONE
                            binding.ivDownloads.visibility = View.VISIBLE
                            disableDownload()
//                            handler2.removeCallbacks(UpdateSongTime2)
                        }
                    } else {
                        binding.pbProgress.visibility = View.VISIBLE
                        binding.pbProgress.progress = 0
                        binding.ivDownloads.visibility = View.GONE
                        disableDownload()
//                        handler2.postDelayed(UpdateSongTime2, 10000)
                        break
                    }
                } else if (i == fileNameList.size - 1) {
                    binding.pbProgress.visibility = View.GONE
//                    handler2.removeCallbacks(UpdateSongTime2)
                }
            }
        } else {
            binding.pbProgress.visibility = View.GONE
            binding.ivDownloads.visibility = View.VISIBLE
//            handler2.removeCallbacks(UpdateSongTime2)
        }
    }

    private fun getDownloadData() {
        try {
            val sharedy = getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, MODE_PRIVATE)
            val gson = Gson()
            val jsony = sharedy.getString(CONSTANTS.PREF_KEY_DownloadName, gson.toString())
            val json1 = sharedy.getString(CONSTANTS.PREF_KEY_DownloadUrl, gson.toString())
            val jsonq = sharedy.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, gson.toString())
            if (!jsony.equals(gson.toString(), ignoreCase = true)) {
                val type = object : TypeToken<List<String?>?>() {}.type
                fileNameList = gson.fromJson(jsony, type)
                playlistDownloadId = gson.fromJson(jsonq, type)
                if (fileNameList.contains(mainPlayModelList[position].name)) {
//                    handler2.postDelayed(UpdateSongTime2, 10000)
                    GetMediaPer()
                }
            } else {
                fileNameList = ArrayList<String>()
                playlistDownloadId = ArrayList<String>()
                //                remainAudio = new ArrayList<>();
//                handler2.removeCallbacks(UpdateSongTime2)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun GetAllMedia(): List<String?> {
        try {
            DatabaseClient
                .getInstance(this)
                .getaudioDatabase()
                .taskDao()
                .geAllDataBYDownloaded1("Complete").observe(this, { audioList: List<String?> ->
                    downloadAudioDetailsList = audioList as ArrayList<String>
                    audioClick = true;
                    if (!downloadClick) {
                        getPrepareShowData()
                    }
                    DatabaseClient
                        .getInstance(this)
                        .getaudioDatabase()
                        .taskDao()
                        .geAllDataBYDownloaded1("Complete")
                        .removeObserver { }
                })
        } catch (e: java.lang.Exception) {
            println(e.message)
        } catch (e: OutOfMemoryError) {
            println(e.message)
        }
        return downloadAudioDetailsList
    }

    fun GetAllMedia1(): List<String?> {
        try {
            DatabaseClient
                .getInstance(this)
                .getaudioDatabase()
                .taskDao()
                .geAllDataBYDownloaded1("Complete").observe(this, { audioList: List<String?> ->
                    downloadAudioDetailsList = audioList as ArrayList<String>
                    DatabaseClient
                        .getInstance(this)
                        .getaudioDatabase()
                        .taskDao()
                        .geAllDataBYDownloaded1("Complete")
                        .removeObserver { }
                })
        } catch (e: java.lang.Exception) {
            println(e.message)
        } catch (e: OutOfMemoryError) {
            println(e.message)
        }
        return downloadAudioDetailsList
    }
}