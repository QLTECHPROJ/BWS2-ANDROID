package com.brainwellnessspa.dashboardModule.activities

import android.Manifest
import android.app.Activity
import android.app.UiModeManager
import android.content.*
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.*
import com.brainwellnessspa.databinding.ActivityViewPlayerBinding
import com.brainwellnessspa.databinding.AudioPlayerNewLayoutBinding
import com.brainwellnessspa.encryptDecryptUtils.DownloadMedia
import com.brainwellnessspa.roomDataBase.AudioDatabase
import com.brainwellnessspa.roomDataBase.DownloadAudioDetails
import com.brainwellnessspa.services.GlobalInitExoPlayer
import com.brainwellnessspa.services.GlobalInitExoPlayer.*
import com.brainwellnessspa.services.GlobalInitExoPlayer.Companion.GetCurrentAudioPosition
import com.brainwellnessspa.services.GlobalInitExoPlayer.Companion.GetSourceName
import com.brainwellnessspa.services.GlobalInitExoPlayer.Companion.callNewPlayerRelease
import com.brainwellnessspa.services.GlobalInitExoPlayer.Companion.getMediaBitmap
import com.brainwellnessspa.userModule.signupLogin.SignInActivity
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
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
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MyPlayerActivity : AppCompatActivity() {
    /* This file is for Main player activity*/
    lateinit var binding: ActivityViewPlayerBinding
    var audioPlayerFlag: String? = ""
    var mainPlayModelList = arrayListOf<MainPlayModel>()
    var position = 0
    var listSize: Int = 0
    var coUserId: String? = ""
    lateinit var act: Activity
    var id: String? = ""
    var name: String? = ""
    var url: String? = ""
    var userId: String? = ""
    lateinit var exoBinding: AudioPlayerNewLayoutBinding
    var gson = Gson()
    private var playerControlView: PlayerControlView? = null
    lateinit var ctx: Context
    var oldSeekPosition: Long = 0
    private var downloadClick = false
    var downloadAudioDetailsList = arrayListOf<String>()
    //    var downloadAudioDetailsListGlobal = arrayListOf<String>()
//    var filesDownloaded: List<File>? = null
    var fileNameList = arrayListOf<String>()
    var audioFile1 = arrayListOf<String>()
    var playlistDownloadId = arrayListOf<String>()
    var localBroadcastManager: LocalBroadcastManager? = null
    var localIntent: Intent? = null

    /* This listener is for get download Progress Percent  */
    private val listener: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            getDownloadData()
            if (intent.hasExtra("Progress")) {
                GetMediaPer()
            }
        }
    }

    /*This lisner1 is shows that ... user came after delet audio from audio details */
    private val listener1: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("MyReminder")) {
                audioClick = false
                makePlayerArray()
            }
        }
    }

    override fun onResume() {
        localIntent = Intent("play_pause_Action")
        localBroadcastManager = LocalBroadcastManager.getInstance(ctx)

        LocalBroadcastManager.getInstance(act).registerReceiver(listener, IntentFilter("DownloadProgress"))
        super.onResume()
    }

    /*defult back press method*/
    override fun onBackPressed() {
        callBack()
        super.onBackPressed()
    }

    /*method for back press*/
    private fun callBack() {
        finish()
    }

    /* method for put hart bit at every 5 min at segment */
    private fun callHeartbeat() {
        val p = Properties()
        addAudioSegmentEvent(ctx, position, mainPlayModelList, "Audio Playing", CONSTANTS.track, downloadAudioDetailsList, p)
    }

    /*
    main crate function */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_player)
        ctx = this@MyPlayerActivity
        act = this@MyPlayerActivity
        /* get user id from share pref*/
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        DB = getAudioDataBase(ctx)
        playerControlView = Assertions.checkNotNull(binding.playerControlView)
        /* get intent data when click on music notification bar */
        if (intent.hasExtra("notification")) {
            val sharedd = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
            val userId = sharedd.getString(CONSTANTS.PREF_KEY_UserID, "")
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

        /* register local broadcast for receive when user delete audio from audio detail*/
        LocalBroadcastManager.getInstance(ctx).registerReceiver(listener1, IntentFilter("Reminder"))

        /* assign variable player control view and add view */
        exoBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.audio_player_new_layout, binding.playerControlView, false)

        binding.playerControlView.addView(exoBinding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        /* info button click */
        binding.llInfo.setOnClickListener {
             if (IsLock.equals("1")) {
                    callEnhanceActivity(ctx, act)
                } else if (IsLock.equals("0")) {
                 if (isNetworkConnected(ctx)) {
                     val shared = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                     audioPlayerFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                     callAudioDetails(mainPlayModelList[position].id, ctx, act, coUserId, "audioPlayer", arrayListOf<DownloadAudioDetails>(), arrayListOf<ViewAllAudioListModel.ResponseData.Detail>(), arrayListOf<PlaylistDetailsModel.ResponseData.PlaylistSong>(), mainPlayModelList, position)
                 } else {
                     showToast(getString(R.string.no_server_found), act)
                 }
             }
        }

        /* back button click */
        binding.llBack.setOnClickListener {
            callBack()
        }

        makePlayerArray()

        /* condition for audio play newly or only update info */
        if (audioClick) {
            exoBinding.llPlay.visibility = View.GONE
            exoBinding.llPause.visibility = View.GONE
            exoBinding.progressBar.visibility = View.VISIBLE
            audioClick = false
            getAllMedia(DB)
        } else {
            getAllMedia1(DB)
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

    /* main make array function for get player array data */
    private fun makePlayerArray() {
        val shared = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
        val json = shared.getString(CONSTANTS.PREF_KEY_MainAudioList, gson.toString())
        audioPlayerFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        val playerPlaylistID = shared.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "0")
        var mainPlayModel: MainPlayModel
        mainPlayModelList = ArrayList()
        position = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
        when {
            audioPlayerFlag.equals("MainAudioList", ignoreCase = true) -> {
                val type = object : TypeToken<ArrayList<HomeDataModel.ResponseData.Audio.Detail?>>() {}.type
                val arrayList = gson.fromJson<ArrayList<HomeDataModel.ResponseData.Audio.Detail?>>(json, type)
                listSize = arrayList.size
                for (i in 0 until listSize) {
                    mainPlayModel = MainPlayModel()
                    mainPlayModel.id = arrayList[i]!!.id.toString()
                    mainPlayModel.name = arrayList[i]!!.name.toString()
                    mainPlayModel.audioFile = arrayList[i]!!.audioFile.toString()
                    mainPlayModel.playlistID = ""
                    mainPlayModel.audioDirection = arrayList[i]!!.audioDirection.toString()
                    mainPlayModel.audiomastercat = arrayList[i]!!.audiomastercat.toString()
                    mainPlayModel.audioSubCategory = arrayList[i]!!.audioSubCategory.toString()
                    mainPlayModel.imageFile = arrayList[i]!!.imageFile.toString()
                    mainPlayModel.audioDuration = arrayList[i]!!.audioDuration.toString()
                    mainPlayModelList.add(mainPlayModel)
                }
                val sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val editor = sharedz.edit()
                editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toJson(mainPlayModelList))
                editor.apply()
            }
            audioPlayerFlag.equals("ViewAllAudioList", ignoreCase = true) -> {
                val type = object : TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail?>>() {}.type
                val arrayList = gson.fromJson<ArrayList<ViewAllAudioListModel.ResponseData.Detail?>>(json, type)
                listSize = arrayList.size
                for (i in 0 until listSize) {
                    mainPlayModel = MainPlayModel()
                    mainPlayModel.id = arrayList[i]!!.iD.toString()
                    mainPlayModel.name = arrayList[i]!!.name.toString()
                    mainPlayModel.audioFile = arrayList[i]!!.audioFile.toString()
                    mainPlayModel.playlistID = ""
                    mainPlayModel.audioDirection = arrayList[i]!!.audioDirection.toString()
                    mainPlayModel.audiomastercat = arrayList[i]!!.audiomastercat.toString()
                    mainPlayModel.audioSubCategory = arrayList[i]!!.audioSubCategory.toString()
                    mainPlayModel.imageFile = arrayList[i]!!.imageFile.toString()
                    mainPlayModel.audioDuration = arrayList[i]!!.audioDuration.toString()
                    mainPlayModelList.add(mainPlayModel)
                }
                val sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val editor = sharedz.edit()
                editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toJson(mainPlayModelList))
                editor.apply()
            }
            audioPlayerFlag.equals(getString(R.string.top_categories), ignoreCase = true) -> {
                val type = object : TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail?>>() {}.type
                val arrayList = gson.fromJson<ArrayList<ViewAllAudioListModel.ResponseData.Detail?>>(json, type)
                listSize = arrayList.size
                for (i in 0 until listSize) {
                    mainPlayModel = MainPlayModel()
                    mainPlayModel.id = arrayList[i]!!.iD.toString()
                    mainPlayModel.name = arrayList[i]!!.name.toString()
                    mainPlayModel.audioFile = arrayList[i]!!.audioFile.toString()
                    mainPlayModel.playlistID = ""
                    mainPlayModel.audioDirection = arrayList[i]!!.audioDirection.toString()
                    mainPlayModel.audiomastercat = arrayList[i]!!.audiomastercat.toString()
                    mainPlayModel.audioSubCategory = arrayList[i]!!.audioSubCategory.toString()
                    mainPlayModel.imageFile = arrayList[i]!!.imageFile.toString()
                    mainPlayModel.audioDuration = arrayList[i]!!.audioDuration.toString()
                    mainPlayModelList.add(mainPlayModel)
                }
                val sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val editor = sharedz.edit()
                editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toJson(mainPlayModelList))
                editor.apply()
            }
            audioPlayerFlag.equals("SearchModelAudio", ignoreCase = true) -> {
                val type = object : TypeToken<ArrayList<SearchBothModel.ResponseData?>>() {}.type
                val arrayList = gson.fromJson<ArrayList<SearchBothModel.ResponseData?>>(json, type)
                listSize = arrayList.size
                for (i in 0 until listSize) {
                    mainPlayModel = MainPlayModel()
                    mainPlayModel.id = arrayList[i]!!.iD.toString()
                    mainPlayModel.name = arrayList[i]!!.name.toString()
                    mainPlayModel.audioFile = arrayList[i]!!.audioFile.toString()
                    mainPlayModel.playlistID = ""
                    mainPlayModel.audioDirection = arrayList[i]!!.audioDirection.toString()
                    mainPlayModel.audiomastercat = arrayList[i]!!.audiomastercat.toString()
                    mainPlayModel.audioSubCategory = arrayList[i]!!.audioSubCategory.toString()
                    mainPlayModel.imageFile = arrayList[i]!!.imageFile.toString()
                    mainPlayModel.audioDuration = arrayList[i]!!.audioDuration.toString()
                    mainPlayModelList.add(mainPlayModel)
                }
                val sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val editor = sharedz.edit()
                editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toJson(mainPlayModelList))
                editor.apply()
            }
            audioPlayerFlag.equals("SearchAudio", ignoreCase = true) -> {
                val type = object : TypeToken<ArrayList<SuggestedModel.ResponseData?>>() {}.type
                val arrayList = gson.fromJson<ArrayList<SuggestedModel.ResponseData?>>(json, type)
                listSize = arrayList.size
                for (i in 0 until listSize) {
                    mainPlayModel = MainPlayModel()
                    mainPlayModel.id = arrayList[i]!!.iD.toString()
                    mainPlayModel.name = arrayList[i]!!.name.toString()
                    mainPlayModel.audioFile = arrayList[i]!!.audioFile.toString()
                    mainPlayModel.playlistID = ""
                    mainPlayModel.audioDirection = arrayList[i]!!.audioDirection.toString()
                    mainPlayModel.audiomastercat = arrayList[i]!!.audiomastercat.toString()
                    mainPlayModel.audioSubCategory = arrayList[i]!!.audioSubCategory.toString()
                    mainPlayModel.imageFile = arrayList[i]!!.imageFile.toString()
                    mainPlayModel.audioDuration = arrayList[i]!!.audioDuration.toString()
                    mainPlayModelList.add(mainPlayModel)
                }
                val sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val editor = sharedz.edit()
                editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toJson(mainPlayModelList))
                editor.apply()
            }
            audioPlayerFlag.equals("DownloadListAudio", ignoreCase = true) -> {
                val type = object : TypeToken<ArrayList<DownloadAudioDetails?>>() {}.type
                val arrayList = gson.fromJson<ArrayList<DownloadAudioDetails?>>(json, type)
                listSize = arrayList.size
                for (i in 0 until listSize) {
                    mainPlayModel = MainPlayModel()
                    mainPlayModel.id = arrayList[i]!!.ID!!
                    mainPlayModel.name = arrayList[i]!!.Name!!
                    mainPlayModel.audioFile = arrayList[i]!!.AudioFile!!
                    mainPlayModel.playlistID = ""
                    mainPlayModel.audioDirection = arrayList[i]!!.AudioDirection!!
                    mainPlayModel.audiomastercat = arrayList[i]!!.Audiomastercat!!
                    mainPlayModel.audioSubCategory = arrayList[i]!!.AudioSubCategory!!
                    mainPlayModel.imageFile = arrayList[i]!!.ImageFile!!
                    mainPlayModel.audioDuration = arrayList[i]!!.AudioDuration!!
                    mainPlayModelList.add(mainPlayModel)
                }
                val sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val editor = sharedz.edit()
                editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toJson(mainPlayModelList))
                editor.apply()
            }
            audioPlayerFlag.equals("Downloadlist", ignoreCase = true) -> {
                val type = object : TypeToken<ArrayList<DownloadAudioDetails?>>() {}.type
                val arrayList = gson.fromJson<ArrayList<DownloadAudioDetails?>>(json, type)
                listSize = arrayList.size
                for (i in 0 until listSize) {
                    mainPlayModel = MainPlayModel()
                    mainPlayModel.id = arrayList[i]!!.ID!!
                    mainPlayModel.name = arrayList[i]!!.Name!!
                    mainPlayModel.audioFile = arrayList[i]!!.AudioFile!!
                    mainPlayModel.playlistID = ""
                    mainPlayModel.audioDirection = arrayList[i]!!.AudioDirection!!
                    mainPlayModel.audiomastercat = arrayList[i]!!.Audiomastercat!!
                    mainPlayModel.audioSubCategory = arrayList[i]!!.AudioSubCategory!!
                    mainPlayModel.imageFile = arrayList[i]!!.ImageFile!!
                    mainPlayModel.audioDuration = arrayList[i]!!.AudioDuration!!
                    mainPlayModelList.add(mainPlayModel)
                }
                val sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val editor = sharedz.edit()
                editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toJson(mainPlayModelList))
                editor.apply()
            }
            audioPlayerFlag.equals("playlist", ignoreCase = true) -> {
                val type = object : TypeToken<ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong?>>() {}.type
                val arrayList = gson.fromJson<ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong?>>(json, type)
                listSize = arrayList.size
                for (i in 0 until listSize) {
                    mainPlayModel = MainPlayModel()
                    mainPlayModel.id = arrayList[i]!!.id
                    mainPlayModel.name = arrayList[i]!!.name
                    mainPlayModel.audioFile = arrayList[i]!!.audioFile
                    mainPlayModel.playlistID = playerPlaylistID!!
                    mainPlayModel.audioDirection = arrayList[i]!!.audioDirection
                    mainPlayModel.audiomastercat = arrayList[i]!!.audiomastercat
                    mainPlayModel.audioSubCategory = arrayList[i]!!.audioSubCategory
                    mainPlayModel.imageFile = arrayList[i]!!.imageFile

                    mainPlayModel.audioDuration = arrayList[i]!!.audioDuration.substring(3)

                    mainPlayModelList.add(mainPlayModel)
                }
                val sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val editor = sharedz.edit()
                editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toJson(mainPlayModelList))
                editor.apply()
            }
        }
        //        binding.tvDireName.setText(R.string.Directions);
        //        callButtonText(position);
        //        if (mainPlayModelList.get(position).getAudioFile().equalsIgnoreCase("")) {
        //            initializePlayerDisclaimer();
        //        } else {
        //            initializePlayer();
        //        }
        getDownloadData()
        if (!audioClick) getPrepareShowData()
        else callButtonText(position)

//        private fun askBatteryOptimizations() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val packageName = ctx.packageName
                val pm = ctx.getSystemService(POWER_SERVICE) as PowerManager
                val isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(packageName)
                if (!isIgnoringBatteryOptimizations) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    intent.data = Uri.parse("package:$packageName")
                    //                registerForActivityResult(00)
                    startActivityForResult(intent, 15695)
                    //                ctx.startActivity(intent)
//                    ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS), 15695)
                }
            }
//        }


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestCode == 15695) {
                val pm = getSystemService(POWER_SERVICE) as PowerManager
                var isIgnoringBatteryOptimizations = false
                isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(packageName)
                if (isIgnoringBatteryOptimizations) {
                    // Ignoring battery optimization
                } else {
                    // Not ignoring battery optimization
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            15695 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return
            }
        }
    }
    /* initialize Player Disclaimer function when uset play disclaimer */
    private fun initializePlayerDisclaimer() {
        //        player = new SimpleExoPlayer.Builder(getApplicationContext()).build();
        try {
            isDisclaimer = 1
            /* condition for if player is null and user play Disclaimer it play from global file*/
            if (audioClick) {
                val globalInitExoPlayer = GlobalInitExoPlayer()
                globalInitExoPlayer.GlobleInItDisclaimer(ctx, mainPlayModelList, position)
                setPlayerCtrView()
            }
            if (player != null) {
                /* player add Listener */
                player.addListener(object : Player.EventListener {
                    override fun onPlaybackStateChanged(state: Int) {
                        if (state == ExoPlayer.STATE_ENDED) {
                            //player back ended
                            audioClick = true
                            isDisclaimer = 0
                            val shared = getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
                            val editor = shared.edit()
                            editor.putString(CONSTANTS.PREF_KEY_IsDisclimer, "0")
                            editor.apply()
                            removeArray()
                            val p = Properties()
                            addDisclaimerToSegment("Disclaimer Completed", ctx, p)
                        }
                        if (state == ExoPlayer.STATE_READY) {
                            val p = Properties()
                            addDisclaimerToSegment("Disclaimer Started", ctx, p)
                            try {
                                if (player.playWhenReady) {
                                    exoBinding.llPlay.visibility = View.GONE
                                    exoBinding.llPause.visibility = View.VISIBLE
                                    exoBinding.progressBar.visibility = View.GONE
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
                            exoBinding.tvStartTime.text = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(player.currentPosition), TimeUnit.MILLISECONDS.toSeconds(player.currentPosition) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(player.currentPosition)))
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
            /* pause button click */
            exoBinding.llPause.setOnClickListener {
                if (player != null) {
                    player.playWhenReady = false
                    exoBinding.llPlay.visibility = View.VISIBLE
                    exoBinding.llPause.visibility = View.GONE
                    exoBinding.progressBar.visibility = View.GONE
                    val p = Properties()
                    addDisclaimerToSegment("Disclaimer Paused", ctx, p)
                }
            }
            callAllDisable(false)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /* remove disclaimer array index form array*/
    private fun removeArray() {

        //        if(!BWSApplication.isNetworkConnected(ctx)){
        callNewPlayerRelease()
        //        }
        isDisclaimer = 0
        val shared = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
        val json = shared.getString(CONSTANTS.PREF_KEY_MainAudioList, gson.toString())
        val json1 = shared.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toString())
        audioPlayerFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        position = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
        when {
            audioPlayerFlag.equals("MainAudioList", ignoreCase = true) -> {
                val type = object : TypeToken<ArrayList<HomeDataModel.ResponseData.Audio.Detail?>>() {}.type
                val arrayList = gson.fromJson<ArrayList<HomeDataModel.ResponseData.Audio.Detail?>>(json, type)
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
            }
            audioPlayerFlag.equals("ViewAllAudioList", ignoreCase = true) -> {
                val type = object : TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail?>>() {}.type
                val arrayList = gson.fromJson<ArrayList<ViewAllAudioListModel.ResponseData.Detail?>>(json, type)
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
            }
            audioPlayerFlag.equals(getString(R.string.top_categories), ignoreCase = true) -> {
                val type = object : TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail?>>() {}.type
                val arrayList = gson.fromJson<ArrayList<ViewAllAudioListModel.ResponseData.Detail?>>(json, type)
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
            }
            audioPlayerFlag.equals("SearchModelAudio", ignoreCase = true) -> {
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
            }
            audioPlayerFlag.equals("SearchAudio", ignoreCase = true) -> {
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
            }
            audioPlayerFlag.equals("DownloadListAudio", ignoreCase = true) -> {
                val type = object : TypeToken<ArrayList<DownloadAudioDetails?>>() {}.type
                val arrayList = gson.fromJson<ArrayList<DownloadAudioDetails?>>(json, type)
                val type1 = object : TypeToken<ArrayList<MainPlayModel?>>() {}.type
                mainPlayModelList = gson.fromJson(json1, type1)
                listSize = arrayList.size
                if (arrayList[position]!!.ID.equals("0", ignoreCase = true)) {
                    arrayList.removeAt(position)
                    mainPlayModelList.removeAt(position)
                }
                val sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val editor = sharedz.edit()
                editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toJson(mainPlayModelList))
                editor.putString(CONSTANTS.PREF_KEY_MainAudioList, gson.toJson(arrayList))
                editor.apply()
            }
            audioPlayerFlag.equals("Downloadlist", ignoreCase = true) -> {
                val type = object : TypeToken<ArrayList<DownloadAudioDetails?>>() {}.type
                val arrayList = gson.fromJson<ArrayList<DownloadAudioDetails?>>(json, type)
                val type1 = object : TypeToken<ArrayList<MainPlayModel?>>() {}.type
                mainPlayModelList = gson.fromJson(json1, type1)
                listSize = arrayList.size
                if (arrayList[position]!!.ID.equals("0", ignoreCase = true)) {
                    arrayList.removeAt(position)
                    mainPlayModelList.removeAt(position)
                }
                val sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val editor = sharedz.edit()
                editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toJson(mainPlayModelList))
                editor.putString(CONSTANTS.PREF_KEY_MainAudioList, gson.toJson(arrayList))
                editor.apply()
            }
            audioPlayerFlag.equals("playlist", ignoreCase = true) -> {
                val type = object : TypeToken<ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong?>>() {}.type
                val arrayList = gson.fromJson<ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong?>>(json, type)
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

    /* main function for prepare player*/
    private fun getPrepareShowData() {
        callButtonText(position)
        if (mainPlayModelList[position].id.equals("0", ignoreCase = true)) {
            //            localIntent1 = Intent("descIssue")
            //            localBroadcastManager1 = LocalBroadcastManager.getInstance(ctx!!)
            //            LocalBroadcastManager.getInstance(ctx!!)
            //                    .registerReceiver(listener1, IntentFilter("descIssue"))
            initializePlayerDisclaimer()
            val globalInitExoPlayer = GlobalInitExoPlayer()
            globalInitExoPlayer.InitNotificationAudioPLayerD(ctx)
        } else {
            val globalInitExoPlayer = GlobalInitExoPlayer()
            globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList)
            initializePlayer()
        }
        setPlayerCtrView()
    }

    /* function for view all data */
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
        /*download button click*/
        binding.llDownload.setOnClickListener {
            if (IsLock.equals("1")) {
            } else if (IsLock.equals("0")) {
                if (isNetworkConnected(ctx)) {
                    if (mainPlayModelList[position].id != "0") callDownload()
                } else {
                    showToast(getString(R.string.no_server_found), act)
                }
            }
        }
        val gson = Gson()
        val shared = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
        val json2 = shared.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toString())
        val playlistName = shared.getString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "")
        val audioPlayerFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        if (!isNetworkConnected(ctx)) {
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
        getMedia2()
        /* play button click*/
        exoBinding.llPlay.setOnClickListener {
            if (player != null) {
                if (!mainPlayModelList[position].id.equals("0", ignoreCase = true)) {
                    if (mainPlayModelList[player.currentWindowIndex].id.equals(mainPlayModelList[mainPlayModelList.size - 1].id, ignoreCase = true) && player.duration - player.currentPosition <= 20) {
                        player.seekTo(position, 0)
                    }
                    player.playWhenReady = true
                    val p = Properties()
                    addAudioSegmentEvent(ctx, position, mainPlayModelList, "Audio Resumed", CONSTANTS.track, downloadAudioDetailsList, p)
                } else {
                    player.playWhenReady = true
                    val p = Properties()
                    addDisclaimerToSegment("Disclaimer Resumed", ctx, p)
                }
            } else {
                audioClick = true
                getAllMedia(DB)
            }
            localIntent!!.putExtra("MyData", "play")
            localBroadcastManager!!.sendBroadcast(localIntent!!)
        }
        if (audioPlayerFlag.equals("playlist", ignoreCase = true) || audioPlayerFlag.equals("Downloadlist", ignoreCase = true)) {
            binding.tvPlaylistName.visibility = View.VISIBLE
            binding.tvPlaylistName.text = playlistName
        } else {
            binding.tvPlaylistName.visibility = View.GONE
        }
        binding.tvAudioName.text = (name)
        if (player == null) {
            exoBinding.tvStartTime.text = getString(R.string.start_time)
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

    /* exoplayer control view set function*/
    private fun setPlayerCtrView() {
        playerControlView = Assertions.checkNotNull(binding.playerControlView)
        playerControlView!!.player = player
        playerControlView!!.setProgressUpdateListener { positionx: Long, bufferedPosition: Long ->
            val sharedsa = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val playFrom = sharedsa.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
            exoBinding.exoProgress.setPosition(positionx)
            exoBinding.exoProgress.setBufferedPosition(bufferedPosition)
            //            myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(position).getImageFile());
            exoBinding.tvStartTime.text = (String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(positionx), TimeUnit.MILLISECONDS.toSeconds(positionx) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(positionx))))
            if (player != null) {
                exoBinding.exoProgress.setDuration(player.duration)
                if (player.currentPosition >= oldSongPos + 299500 && player.currentPosition <= oldSongPos + 310000) {
                    oldSongPos = positionx
                    callHeartbeat()
                }
                if((String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(positionx), TimeUnit.MILLISECONDS.toSeconds(positionx) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(positionx)))) == (String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(player.duration), TimeUnit.MILLISECONDS.toSeconds(player.duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(player.duration))))){
                    Log.e("STATE_ENDED ", "My Player onPlaybackStateChanged Done")
                    //                        try {
                    if (audioPlayerFlag.equals("playlist", ignoreCase = true)) {
                        if (playFrom.equals("Suggested", ignoreCase = true)) {
                            val global = GlobalInitExoPlayer()
                            global.getUserActivityCall(ctx, mainPlayModelList[position].id, mainPlayModelList[position].playlistID, "complete")
                        }
                    }

                    val p = Properties()
                    addAudioSegmentEvent(ctx, position, mainPlayModelList, "Audio Completed", CONSTANTS.track, downloadAudioDetailsList, p)

                    if (mainPlayModelList[player.currentWindowIndex].id.equals(mainPlayModelList[mainPlayModelList.size - 1].id, ignoreCase = true)) {
                        exoBinding.llPlay.visibility = View.VISIBLE
                        exoBinding.llPause.visibility = View.GONE
                        exoBinding.progressBar.visibility = View.GONE
                        player.playWhenReady = false
                        localIntent!!.putExtra("MyData", "pause")
                        localBroadcastManager!!.sendBroadcast(localIntent!!)
                        val p = Properties()
                        val source = GetSourceName(ctx)
                        if (!source.equals("Playlist", ignoreCase = true) && !source.equals("Downloaded Playlists", ignoreCase = true)) {
                            addAudioSegmentEvent(ctx, position, mainPlayModelList, "Audio Playback Completed", CONSTANTS.track, downloadAudioDetailsList, p)
                        }
                        if (audioPlayerFlag.equals("playlist", ignoreCase = true) || audioPlayerFlag.equals("Downloadlist", ignoreCase = true)) {
                            val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_SEGMENT_PLAYLIST, MODE_PRIVATE)
                            val playlistId = shared1.getString(CONSTANTS.PREF_KEY_PlaylistID, "")
                            val playlistName = shared1.getString(CONSTANTS.PREF_KEY_PlaylistName, "")
                            val playlistDescription = shared1.getString(CONSTANTS.PREF_KEY_PlaylistDescription, "")
                            val playlistType = shared1.getString(CONSTANTS.PREF_KEY_PlaylistType, "")
                            val totalhour = shared1.getString(CONSTANTS.PREF_KEY_Totalhour, "")
                            val totalminute = shared1.getString(CONSTANTS.PREF_KEY_Totalminute, "")
                            val totalAudio = shared1.getString(CONSTANTS.PREF_KEY_TotalAudio, "")
                            val screenView = shared1.getString(CONSTANTS.PREF_KEY_ScreenView, "")
                            val p = Properties()
                            p.putValue("playlistId", playlistId)
                            p.putValue("playlistName", playlistName)
                            p.putValue("playlistDescription", playlistDescription)
                            when {
                                playlistType.equals("1", ignoreCase = true) -> {
                                    p.putValue("playlistType", "Created")
                                }
                                playlistType.equals("0", ignoreCase = true) -> {
                                    p.putValue("playlistType", "Default")
                                }
                                playlistType.equals("2") -> p.putValue("playlistType", "Suggested")
                            }
                            when {
                                totalhour.equals("", ignoreCase = true) -> {
                                    p.putValue("playlistDuration", "0h " + totalminute + "m")
                                }
                                totalminute.equals("", ignoreCase = true) -> {
                                    p.putValue("playlistDuration", totalhour + "h 0m")
                                }
                                else -> {
                                    p.putValue("playlistDuration", totalhour + "h " + totalminute + "m")
                                }
                            }
                            p.putValue("audioCount", totalAudio)
                            p.putValue("source", screenView)
                            p.putValue("playerType", "Mini")
                            p.putValue("audioService", appStatus(ctx))
                            p.putValue("sound", hundredVolume.toString())
                            addToSegment("Playlist Completed", p, CONSTANTS.track)
                            Log.e("Last audio End", mainPlayModelList[position].name)
                        } else {
                            Log.e("Curr audio End", mainPlayModelList[position].name)
                        }
                    }
                    /*  } catch (e: java.lang.Exception) {
                          e.printStackTrace()
                          Log.e("End State: ", e.message!!)
                      }*/
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

    /* initialize main player*/
    private fun initializePlayer() {
        try {
            /* condition for if player is null and user play audio it play from global file*/
            isDisclaimer = 0
            if (audioClick) {
                val globalInitExoPlayer = GlobalInitExoPlayer()
                globalInitExoPlayer.GlobleInItPlayer(ctx, position, downloadAudioDetailsList, mainPlayModelList, "Main")
                setPlayerCtrView()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //        try {
        /* set plyer when it is not null*/
        if (player != null) {
            player.setWakeMode(C.WAKE_MODE_NONE)
            player.setHandleWakeLock(true)
            player.setHandleAudioBecomingNoisy(true)
            /* player add listener */
            player.addListener(object : Player.EventListener {
                override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {
                    Log.e("TAG", "Listener-onTracksChanged... Main Activity  :..." + player.currentWindowIndex.toString())
                    oldSongPos = 0
                    val sharedsa = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                    val gson = Gson()
                    val json = sharedsa.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toString())
                    val playFrom = sharedsa.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
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
                    if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES || AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                    val uiModeManager = getSystemService(UI_MODE_SERVICE) as UiModeManager
                    if (uiModeManager.nightMode == UiModeManager.MODE_NIGHT_AUTO || uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES || uiModeManager.nightMode == UiModeManager.MODE_NIGHT_CUSTOM) {
                        uiModeManager.nightMode = UiModeManager.MODE_NIGHT_NO
                    }
                    if (audioPlayerFlag.equals("playlist", ignoreCase = true)) {
                        if (playFrom.equals("Suggested", ignoreCase = true)) {
                            val global = GlobalInitExoPlayer()
                            global.getUserActivityCall(ctx, mainPlayModelList[position].id, mainPlayModelList[position].playlistID, "start")
                        }
                    }
                    getDownloadData()
                    setPlayerCtrView()
                    GetMediaPer()
                    callButtonText(position)
                    localIntent!!.putExtra("MyData", "play")
                    localBroadcastManager!!.sendBroadcast(localIntent!!)
                    val p = Properties()
                    addAudioSegmentEvent(ctx, position, mainPlayModelList, "Audio Started", CONSTANTS.track, downloadAudioDetailsList, p)
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
                        localIntent!!.putExtra("MyData", "play")
                        localBroadcastManager!!.sendBroadcast(localIntent!!)
                    } else if (!isPlaying) {
                        exoBinding.llPlay.visibility = View.VISIBLE
                        exoBinding.llPause.visibility = View.GONE
                        exoBinding.progressBar.visibility = View.GONE
                        localIntent!!.putExtra("MyData", "pause")
                        localBroadcastManager!!.sendBroadcast(localIntent!!)
                    }
                    if (player.playbackState == ExoPlayer.STATE_ENDED) {
                        Log.e("STATE_ENDED My player", "Done")
                    }
                    exoBinding.exoProgress.setBufferedPosition(player.bufferedPosition)
                    exoBinding.exoProgress.setPosition(player.currentPosition)
                    val globalInitExoPlayer = GlobalInitExoPlayer()
                    globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList)
                    exoBinding.exoProgress.setDuration(player.duration)
                    exoBinding.tvStartTime.text = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(player.currentPosition), TimeUnit.MILLISECONDS.toSeconds(player.currentPosition) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(player.currentPosition)))
                    if (player.currentPosition >= oldSongPos + 299500 && player.currentPosition <= oldSongPos + 310000) {
                        oldSongPos = player.currentPosition
                        callHeartbeat()
                    }
                }

                override fun onPlaybackStateChanged(state: Int) {
                    val sharedsa = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                    val playFrom = sharedsa.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
                    Log.v("TAG", "Listener-onPlaybackStateChanged... Main Activity")
                    //                        myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(position).getImageFile());
                    if (state == ExoPlayer.STATE_READY) {
                        val p = Properties()
                        addAudioSegmentEvent(ctx, position, mainPlayModelList, "Audio Buffer Completed", CONSTANTS.track, downloadAudioDetailsList, p)
                        if (player.playWhenReady) {
                            exoBinding.llPlay.visibility = View.GONE
                            exoBinding.llPause.visibility = View.VISIBLE
                            exoBinding.progressBar.visibility = View.GONE
                            localIntent!!.putExtra("MyData", "play")
                            localBroadcastManager!!.sendBroadcast(localIntent!!)
                            callHeartbeat()
                        } else if (!player.playWhenReady) {
                            exoBinding.llPlay.visibility = View.VISIBLE
                            exoBinding.llPause.visibility = View.GONE
                            exoBinding.progressBar.visibility = View.GONE
                            localIntent!!.putExtra("MyData", "pause")
                            localBroadcastManager!!.sendBroadcast(localIntent!!)
                        }
                        //                        isprogressbar = false;
                    } else if (state == ExoPlayer.STATE_BUFFERING) {
                        exoBinding.llPlay.visibility = View.GONE
                        exoBinding.llPause.visibility = View.GONE
                        exoBinding.progressBar.visibility = View.VISIBLE
                        val p = Properties()
                        addAudioSegmentEvent(ctx, position, mainPlayModelList, "Audio Buffer Started", CONSTANTS.track, downloadAudioDetailsList, p)
                    } else if (state == ExoPlayer.STATE_ENDED) {
                        Log.e("STATE_ENDED ", "My Player onPlaybackStateChanged Done")
                        try {
                            if (audioPlayerFlag.equals("playlist", ignoreCase = true)) {
                                if (playFrom.equals("Suggested", ignoreCase = true)) {
                                    val global = GlobalInitExoPlayer()
                                    global.getUserActivityCall(ctx, mainPlayModelList[position].id, mainPlayModelList[position].playlistID, "complete")
                                }
                            }

                            val p = Properties()
                            addAudioSegmentEvent(ctx, position, mainPlayModelList, "Audio Completed", CONSTANTS.track, downloadAudioDetailsList, p)

                            if (mainPlayModelList[player.currentWindowIndex].id.equals(mainPlayModelList[mainPlayModelList.size - 1].id, ignoreCase = true)) {
                                exoBinding.llPlay.visibility = View.VISIBLE
                                exoBinding.llPause.visibility = View.GONE
                                exoBinding.progressBar.visibility = View.GONE
                                player.playWhenReady = false
                                localIntent!!.putExtra("MyData", "pause")
                                localBroadcastManager!!.sendBroadcast(localIntent!!)
                                val p = Properties()
                                val source = GetSourceName(ctx)
                                if (!source.equals("Playlist", ignoreCase = true) && !source.equals("Downloaded Playlists", ignoreCase = true)) {
                                    addAudioSegmentEvent(ctx, position, mainPlayModelList, "Audio Playback Completed", CONSTANTS.track, downloadAudioDetailsList, p)
                                }
                                if (audioPlayerFlag.equals("playlist", ignoreCase = true) || audioPlayerFlag.equals("Downloadlist", ignoreCase = true)) {
                                    val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_SEGMENT_PLAYLIST, MODE_PRIVATE)
                                    val playlistId = shared1.getString(CONSTANTS.PREF_KEY_PlaylistID, "")
                                    val playlistName = shared1.getString(CONSTANTS.PREF_KEY_PlaylistName, "")
                                    val playlistDescription = shared1.getString(CONSTANTS.PREF_KEY_PlaylistDescription, "")
                                    val playlistType = shared1.getString(CONSTANTS.PREF_KEY_PlaylistType, "")
                                    val totalhour = shared1.getString(CONSTANTS.PREF_KEY_Totalhour, "")
                                    val totalminute = shared1.getString(CONSTANTS.PREF_KEY_Totalminute, "")
                                    val totalAudio = shared1.getString(CONSTANTS.PREF_KEY_TotalAudio, "")
                                    val screenView = shared1.getString(CONSTANTS.PREF_KEY_ScreenView, "")
                                    val p = Properties()
                                    p.putValue("playlistId", playlistId)
                                    p.putValue("playlistName", playlistName)
                                    p.putValue("playlistDescription", playlistDescription)
                                    when {
                                        playlistType.equals("1", ignoreCase = true) -> {
                                            p.putValue("playlistType", "Created")
                                        }
                                        playlistType.equals("0", ignoreCase = true) -> {
                                            p.putValue("playlistType", "Default")
                                        }
                                        playlistType.equals("2") -> p.putValue("playlistType", "Suggested")
                                    }
                                    when {
                                        totalhour.equals("", ignoreCase = true) -> {
                                            p.putValue("playlistDuration", "0h " + totalminute + "m")
                                        }
                                        totalminute.equals("", ignoreCase = true) -> {
                                            p.putValue("playlistDuration", totalhour + "h 0m")
                                        }
                                        else -> {
                                            p.putValue("playlistDuration", totalhour + "h " + totalminute + "m")
                                        }
                                    }
                                    p.putValue("audioCount", totalAudio)
                                    p.putValue("source", screenView)
                                    p.putValue("playerType", "Mini")
                                    p.putValue("audioService", appStatus(ctx))
                                    p.putValue("sound", hundredVolume.toString())
                                    addToSegment("Playlist Completed", p, CONSTANTS.track)
                                    Log.e("Last audio End", mainPlayModelList[position].name)
                                } else {
                                    Log.e("Curr audio End", mainPlayModelList[position].name)
                                }
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
                    when (error.type) {
                        ExoPlaybackException.TYPE_SOURCE -> {
                            p.putValue("interruptionMethod", error.message + " " + error.sourceException.message)
                            intruptMethod = error.message + " " + error.sourceException.message
                            Log.e("onPlaybackError", error.message + " " + error.sourceException.message)
                        }
                        ExoPlaybackException.TYPE_RENDERER -> {
                            p.putValue("interruptionMethod", error.message + " " + error.rendererException.message)
                            intruptMethod = error.message + " " + error.rendererException.message
                            Log.e("onPlaybackError", error.message + " " + error.rendererException.message)
                        }
                        ExoPlaybackException.TYPE_UNEXPECTED -> {
                            p.putValue("interruptionMethod", error.message + " " + error.unexpectedException.message)
                            intruptMethod = error.message + " " + error.unexpectedException.message
                            Log.e("onPlaybackError", error.message + " " + error.unexpectedException.message)
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
                    addAudioSegmentEvent(ctx, position, mainPlayModelList, "Audio Interrupted", CONSTANTS.track, downloadAudioDetailsList, p)
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
                        var audioType = ""
                        audioType = if (downloadAudioDetailsList.contains(mainPlayModelList[position].name)) {
                            "Downloaded"
                        } else {
                            "Streaming"
                        }
                        if (isNetworkConnected(ctx)) {
                            val listCall = APINewClient.client.getAudioInterruption(coUserId, mainPlayModelList[position].id, mainPlayModelList[position].name, "", mainPlayModelList[position].audioDirection, mainPlayModelList[position].audiomastercat, mainPlayModelList[position].audioSubCategory, mainPlayModelList[position].audioDuration, "", audioType, "Main", hundredVolume.toString(), appStatus(ctx), GetSourceName(ctx), GetCurrentAudioPosition(), "", intruptMethod, batLevel.toString(), BatteryStatus, downSpeed.toString(), upSpeed.toString(), "Android")
                            listCall.enqueue(object : Callback<AudioInterruptionModel?> {
                                override fun onResponse(call: Call<AudioInterruptionModel?>, response: Response<AudioInterruptionModel?>) {
                                    val listModel = response.body()
                                    if (listModel != null) {
                                        if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                        } else if (listModel.responseCode.equals(ctx.getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
                                            deleteCall(act)
                                            showToast(listModel.responseMessage, act)
                                            val i = Intent(act, SignInActivity::class.java)
                                            i.putExtra("mobileNo", "")
                                            i.putExtra("countryCode", "")
                                            i.putExtra("name", "")
                                            i.putExtra("email", "")
                                            i.putExtra("countryShortName", "")
                                            startActivity(i)
                                            finish()
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<AudioInterruptionModel?>, t: Throwable) {
                                }
                            })
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            })

            /* exo player seekbar listener */
            exoBinding.exoProgress.addListener(object : OnScrubListener {
                override fun onScrubStart(timeBar: TimeBar, pos: Long) {
                    exoBinding.exoProgress.setPosition(pos)
                    exoBinding.exoProgress.setDuration(player.duration)
                    oldSeekPosition = pos
                    val globalInitExoPlayer = GlobalInitExoPlayer()
                    globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList)
                    globalInitExoPlayer.UpdateNotificationAudioPLayer(ctx)
                    val p = Properties()
                    p.putValue("seekPosition", pos)
                    when {
                        oldSeekPosition < pos -> {
                            p.putValue("seekDirection", "Forwarded")
                        }
                        oldSeekPosition > pos -> {
                            p.putValue("seekDirection", "Backward")
                        }
                        else -> {
                            p.putValue("seekDirection", "")
                        }
                    }
                    addAudioSegmentEvent(ctx, position, mainPlayModelList, "Audio Seek Started", CONSTANTS.track, downloadAudioDetailsList, p)
                }

                override fun onScrubMove(timeBar: TimeBar, position: Long) {}
                override fun onScrubStop(timeBar: TimeBar, pos: Long, canceled: Boolean) {
                    player.seekTo(position, pos)
                    exoBinding.exoProgress.setPosition(pos)
                    exoBinding.exoProgress.setDuration(player.duration)
                    exoBinding.tvStartTime.text = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(pos), TimeUnit.MILLISECONDS.toSeconds(pos) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(pos)))
                    val globalInitExoPlayer = GlobalInitExoPlayer()
                    globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList)
                    globalInitExoPlayer.UpdateNotificationAudioPLayer(ctx)
                    val p = Properties()
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
                    addAudioSegmentEvent(ctx, position, mainPlayModelList, "Audio Seek Completed", CONSTANTS.track, downloadAudioDetailsList, p)
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
                localIntent!!.putExtra("MyData", "play")
                localBroadcastManager!!.sendBroadcast(localIntent!!)
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

    /* all other clicks*/
    private fun epAllClicks() {
        try {

            /* pause button click */
            exoBinding.llPause.setOnClickListener {
                try {
                    player.playWhenReady = false
                    val pss = player.currentWindowIndex
                    //                    myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(pss).getImageFile());
                    exoBinding.llPlay.visibility = View.VISIBLE
                    exoBinding.llPause.visibility = View.GONE
                    exoBinding.progressBar.visibility = View.GONE
                    val p = Properties()
                    addAudioSegmentEvent(ctx, position, mainPlayModelList, "Audio Paused", CONSTANTS.track, downloadAudioDetailsList, p)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                localIntent!!.putExtra("MyData", "pause")
                localBroadcastManager!!.sendBroadcast(localIntent!!)
            }
            /* 30 sec forword click */
            exoBinding.llForwardSec.setOnClickListener {
                try {
                    if (player.duration - player.currentPosition <= 30000) {
                        showToast("Please Wait... ", act)
                    } else {
                        player.seekTo(position, player.currentPosition + 30000)
                        val p = Properties()
                        addAudioSegmentEvent(ctx, position, mainPlayModelList, "Audio Forward", CONSTANTS.track, downloadAudioDetailsList, p)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
            /* 30 sec backword sec click*/
            exoBinding.llBackWordSec.setOnClickListener {
                try {
                    if (player.currentPosition > 30000) {
                        player.seekTo(position, player.currentPosition - 30000)
                    } else if (player.currentPosition < 30000) {
                        player.seekTo(position, 0)
                    }
                    val p = Properties()
                    addAudioSegmentEvent(ctx, position, mainPlayModelList, "Audio Backward", CONSTANTS.track, downloadAudioDetailsList, p)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.e("ep all State: ", e.message!!)
        }
    }

    /* all button enable disable click condition*/
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

    /* add to recent play api call function*/
    private fun addToRecentPlay() {
        if (isNetworkConnected(ctx)) {
            val listCall = APINewClient.client.getRecentlyPlayed(coUserId, id)
            listCall.enqueue(object : Callback<SucessModel?> {
                override fun onResponse(call: Call<SucessModel?>, response: Response<SucessModel?>) {
                    try {
                        val model: SucessModel = response.body()!!
                        if (model.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {

                        } else if (model.responseCode.equals(getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
                            deleteCall(act)
                            showToast(model.responseMessage, act)
                            val i = Intent(act, SignInActivity::class.java)
                            i.putExtra("mobileNo", "")
                            i.putExtra("countryCode", "")
                            i.putExtra("name", "")
                            i.putExtra("email", "")
                            i.putExtra("countryShortName", "")
                            startActivity(i)
                            finish()
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<SucessModel?>, t: Throwable) {
                }
            })
        }
    }

    /* download audio function*/
    private fun callDownload() {

        downloadClick = true
        if (downloadAudioDetailsList.contains(mainPlayModelList[position].name)) {
            disableDownload()
            saveMedia(100)
            val unicode = 0x1F642
            val textIcon4 = String(Character.toChars(unicode))
            showToast("Your audio is being downloaded! $textIcon4", act)
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
            saveMedia(0)
            val unicode = 0x1F44C
            val textIcon = String(Character.toChars(unicode))
            showToast("Yess! Download complete. Your wellness journey is ready! $textIcon", act)
            val p = Properties()
            addAudioSegmentEvent(ctx, position, mainPlayModelList, "Audio Download Started", CONSTANTS.track, downloadAudioDetailsList, p)
            // }
        }
    }

    /* disable download finction*/
    private fun disableDownload() {
        binding.ivDownloads.setImageResource(R.drawable.ic_download_done_icon)
        binding.ivDownloads.setColorFilter(ContextCompat.getColor(ctx, R.color.white), PorterDuff.Mode.SRC_IN)

        binding.llDownload.isClickable = false
        binding.llDownload.isEnabled = false
    }

    /* enable download function */
    private fun enableDownload() {
        binding.ivDownloads.setImageResource(R.drawable.ic_white_download_icon)
        binding.llDownload.isClickable = true
        binding.llDownload.isEnabled = true
        binding.ivDownloads.setColorFilter(ContextCompat.getColor(ctx, R.color.white), PorterDuff.Mode.SRC_IN)
    }

    /* save audio file to local database */
    private fun saveMedia(progressx: Int) {
        downloadClick = true
        val downloadAudioDetails = DownloadAudioDetails()
        //                if (audioPlay) {
        downloadAudioDetails.ID = mainPlayModelList[position].id
        downloadAudioDetails.UserID = coUserId!!
        downloadAudioDetails.Name = mainPlayModelList[position].name
        downloadAudioDetails.AudioFile = mainPlayModelList[position].audioFile
        downloadAudioDetails.AudioDirection = mainPlayModelList[position].audioDirection
        downloadAudioDetails.Audiomastercat = mainPlayModelList[position].audiomastercat
        downloadAudioDetails.AudioSubCategory = mainPlayModelList[position].audioSubCategory
        downloadAudioDetails.ImageFile = mainPlayModelList[position].imageFile
        downloadAudioDetails.AudioDuration = mainPlayModelList[position].audioDuration
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
        }*/
        downloadAudioDetails.IsSingle = "1"
        downloadAudioDetails.PlaylistId = ""
        if (progressx == 0) {
            downloadAudioDetails.IsDownload = "pending"
        } else {
            downloadAudioDetails.IsDownload = "Complete"
        }
        downloadAudioDetails.DownloadProgress = progressx
        DB = getAudioDataBase(ctx)
        try {
            AudioDatabase.databaseWriteExecutor.execute {
                DB!!.taskDao()?.insertMedia(downloadAudioDetails)
            }
        } catch (e: java.lang.Exception) {
            println(e.message)
        } catch (e: OutOfMemoryError) {
            println(e.message)
        }
    }

    /* get audio file data from local db for displaying progress
    * and for download button clickable or not */
    private fun getMedia2() {
        DB = getAudioDataBase(ctx)
        try {
            DB!!.taskDao()?.getaudioByPlaylist1(mainPlayModelList[position].audioFile, "", coUserId)?.observe(this, { audiolist: List<DownloadAudioDetails> ->
                if (audiolist.isNotEmpty()) {
                    disableDownload()
                    if (audiolist[0].DownloadProgress == 100) {
                        binding.ivDownloads.visibility = View.VISIBLE
                        binding.pbProgress.visibility = View.GONE
                    } else {
                        binding.ivDownloads.visibility = View.GONE
                        binding.pbProgress.visibility = View.VISIBLE
                        GetMediaPer()
                    }
                    DB!!.taskDao()?.getaudioByPlaylist1(mainPlayModelList[position].audioFile, "", coUserId)?.removeObserver { }
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
            }*/DB!!.taskDao()?.getaudioByPlaylist1(mainPlayModelList[position].audioFile, "", coUserId)?.removeObserver { }
                }
            } as (List<DownloadAudioDetails?>) -> Unit)
        } catch (e: java.lang.Exception) {
            println(e.message)
        } catch (e: OutOfMemoryError) {
            println(e.message)
        }
    }

    /* get midia downloaded percent and display progress bar  */
    private fun GetMediaPer() {
        if (fileNameList.size != 0) {
            for (i in fileNameList.indices) {
                if (fileNameList[i].equals(mainPlayModelList[position].name, ignoreCase = true) && playlistDownloadId[i].equals("", ignoreCase = true)) {
                    if (!DownloadMedia.filename.equals("", ignoreCase = true) && DownloadMedia.filename.equals(mainPlayModelList[position].name, ignoreCase = true)) {
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

    /* get download function added data from Share Preference */
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
                fileNameList = ArrayList()
                playlistDownloadId = ArrayList()
                //                remainAudio = new ArrayList<>();
                //                handler2.removeCallbacks(UpdateSongTime2)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /* get completed downlaoded file name list for play and player initialize*/
    private fun getAllMedia(DB: AudioDatabase): List<String?> {
        try {
            DB.taskDao()?.geAllDataBYDownloadedForAll("Complete")?.observe(this, { audioList: List<String?> ->
                downloadAudioDetailsList = audioList as ArrayList<String>
                audioClick = true
                if (!downloadClick) {
                    getPrepareShowData()
                }
                DB.taskDao()?.geAllDataBYDownloadedForAll("Complete")?.removeObserver { }
            })
        } catch (e: java.lang.Exception) {
            println(e.message)
        } catch (e: OutOfMemoryError) {
            println(e.message)
        }
        return downloadAudioDetailsList
    }
    /*fun GetAllMediaForAll(DB: AudioDatabase): List<String?> {
        try {
            DB.taskDao()
                .geAllDataBYDownloadedForAll("Complete").observe(this, { audioList: List<String?> ->
                        downloadAudioDetailsListGlobal = audioList as ArrayList<String>
                        DB.taskDao()
                                .geAllDataBYDownloadedForAll("Complete")
                                .removeObserver { }
                    })
        } catch (e: java.lang.Exception) {
            println(e.message)
        } catch (e: OutOfMemoryError) {
            println(e.message)
        }
        return downloadAudioDetailsListGlobal
    }*/

    /* get completed downlaoded file name list for play */
    private fun getAllMedia1(DB: AudioDatabase): List<String?> {
        try {
            DB.taskDao()?.geAllDataBYDownloadedForAll("Complete")?.observe(this, { audioList: List<String?> ->
                downloadAudioDetailsList = audioList as ArrayList<String>
                DB.taskDao()?.geAllDataBYDownloadedForAll("Complete")?.removeObserver { }
            })
        } catch (e: java.lang.Exception) {
            println(e.message)
        } catch (e: OutOfMemoryError) {
            println(e.message)
        }
        return downloadAudioDetailsList
    }
}