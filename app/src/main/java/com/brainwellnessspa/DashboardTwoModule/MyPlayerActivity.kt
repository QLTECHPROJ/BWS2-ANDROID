package com.brainwellnessspa.DashboardTwoModule

import android.app.UiModeManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.DashboardModule.Activities.AudioPlayerActivity
import com.brainwellnessspa.DashboardModule.Activities.AudioPlayerActivity.AudioInterrupted
import com.brainwellnessspa.DashboardModule.Activities.DashboardActivity
import com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.audioClick
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.addToRecentPlayId
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel
import com.brainwellnessspa.DashboardTwoModule.Model.HomeDataModel
import com.brainwellnessspa.DashboardTwoModule.Model.SucessModel
import com.brainwellnessspa.R
import com.brainwellnessspa.Services.GlobalInitExoPlayer
import com.brainwellnessspa.Services.GlobalInitExoPlayer.getMediaBitmap
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit

class MyPlayerActivity :AppCompatActivity(){
    lateinit var binding: ActivityViewPlayerBinding
    var AudioPlayerFlag: String? = ""
    var mainPlayModelList = ArrayList<MainPlayModel>()
    var position = 0
    var listSize:Int = 0
    var CoUserID: String? = ""
    var id: String? = ""
    var name: String? = ""
    var url: String? = ""
    var USERID: String? = ""
    lateinit var exoBinding:AudioPlayerNewLayoutBinding
    var gson = Gson()
    var playerControlView: PlayerControlView? = null
    var ctx: Context? = null
    var oldSeekPosition: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_player)
        ctx = this@MyPlayerActivity
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        USERID = shared.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")
        playerControlView = Assertions.checkNotNull(binding.playerControlView)
        exoBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.audio_player_new_layout, binding.playerControlView, false)
        binding.playerControlView.addView(exoBinding.getRoot())
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        makePlayerArray()
    }

    private fun makePlayerArray() {
        audioClick = true
        val shared = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
        val json = shared.getString(CONSTANTS.PREF_KEY_MainAudioList, gson.toString())
        AudioPlayerFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        var mainPlayModel: MainPlayModel
        mainPlayModelList = ArrayList()
        position = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
        if (AudioPlayerFlag.equals("MainAudioList", ignoreCase = true)) {
            val type = object : TypeToken<ArrayList<HomeDataModel.ResponseData.Audio.Detail?>>() {}.type
            val arrayList = gson.fromJson<ArrayList<HomeDataModel.ResponseData.Audio.Detail?>>(json, type)
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
        }
        getPrepareShowData()
    }

    private fun getPrepareShowData() {
        callButtonText(position)
        val globalInitExoPlayer = GlobalInitExoPlayer()
        globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList)
        initializePlayer()
        setPlayerCtrView()
    }
    private fun setPlayerCtrView() {
        playerControlView = Assertions.checkNotNull(binding.playerControlView)
        playerControlView!!.player = GlobalInitExoPlayer.player
        playerControlView!!.setProgressUpdateListener { positionx: Long, bufferedPosition: Long ->
            exoBinding.exoProgress.setPosition(positionx)
            exoBinding.exoProgress.setBufferedPosition(bufferedPosition)
            //            myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(position).getImageFile());
            exoBinding.tvStartTime.text = (String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(positionx),
                    TimeUnit.MILLISECONDS.toSeconds(positionx) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(positionx))))
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
            MiniPlayerFragment.isDisclaimer = 0
            if (DashboardActivity.audioClick) {
                val globalInitExoPlayer = GlobalInitExoPlayer()
                val downloadaudioList = arrayListOf<String>()
                globalInitExoPlayer.GlobleInItPlayer(ctx, position, downloadaudioList, mainPlayModelList, "Main")
                setPlayerCtrView()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
//        try {
            if (GlobalInitExoPlayer.player != null) {
                GlobalInitExoPlayer.player.setWakeMode(C.WAKE_MODE_NONE)
                GlobalInitExoPlayer.player.setHandleWakeLock(true)
                GlobalInitExoPlayer.player.setHandleAudioBecomingNoisy(true)
                GlobalInitExoPlayer.player.addListener(object : Player.EventListener {
                    override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {
                        Log.e("TAG", "Listener-onTracksChanged... Main Activity")
                        AudioPlayerActivity.oldSongPos = 0
                        val sharedsa = ctx!!.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                        val gson = Gson()
                        val json = sharedsa.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toString())
                        if (!json.equals(gson.toString(), ignoreCase = true)) {
                            val type = object : TypeToken<ArrayList<MainPlayModel?>?>() {}.type
                            mainPlayModelList = gson.fromJson(json, type)
                        }
                        position = GlobalInitExoPlayer.player.currentWindowIndex
                        val globalInitExoPlayer = GlobalInitExoPlayer()
                        globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList)
                        val shared = ctx!!.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                        val editor = shared.edit()
                        editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position)
                        editor.apply()
                        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
                                || AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        }
                        val uiModeManager = getSystemService(UI_MODE_SERVICE) as UiModeManager
                        if (uiModeManager.nightMode == UiModeManager.MODE_NIGHT_AUTO || uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES || uiModeManager.nightMode == UiModeManager.MODE_NIGHT_CUSTOM) {
                            uiModeManager.nightMode = UiModeManager.MODE_NIGHT_NO
                        }
//                        getDownloadData()
                        setPlayerCtrView()
//                        GetMediaPer()
                        callButtonText(position)
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        if (GlobalInitExoPlayer.player.playbackState == ExoPlayer.STATE_BUFFERING) {
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
                        exoBinding.exoProgress.setBufferedPosition(GlobalInitExoPlayer.player.bufferedPosition)
                        exoBinding.exoProgress.setPosition(GlobalInitExoPlayer.player.currentPosition)
                        val globalInitExoPlayer = GlobalInitExoPlayer()
                        globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList)
                        exoBinding.exoProgress.setDuration(GlobalInitExoPlayer.player.duration)
                        exoBinding.tvStartTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(GlobalInitExoPlayer.player.currentPosition),
                                TimeUnit.MILLISECONDS.toSeconds(GlobalInitExoPlayer.player.currentPosition) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(GlobalInitExoPlayer.player.currentPosition))))
                    }

                    override fun onPlaybackStateChanged(state: Int) {
//                        myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(position).getImageFile());
                        if (state == ExoPlayer.STATE_READY) {
                            if (GlobalInitExoPlayer.player.playWhenReady) {
                                exoBinding.llPlay.visibility = View.GONE
                                exoBinding.llPause.visibility = View.VISIBLE
                                exoBinding.progressBar.visibility = View.GONE
                            } else if (!GlobalInitExoPlayer.player.playWhenReady) {
                                exoBinding.llPlay.visibility = View.VISIBLE
                                exoBinding.llPause.visibility = View.GONE
                                exoBinding.progressBar.visibility = View.GONE
                            }

//                        isprogressbar = false;
                        } else if (state == ExoPlayer.STATE_BUFFERING) {
                            exoBinding.llPlay.visibility = View.GONE
                            exoBinding.llPause.visibility = View.GONE
                            exoBinding.progressBar.visibility = View.VISIBLE
                        } else if (state == ExoPlayer.STATE_ENDED) {
                            try {
                                if (mainPlayModelList[GlobalInitExoPlayer.player.currentWindowIndex].id.equals(mainPlayModelList[mainPlayModelList.size - 1].id, ignoreCase = true)) {
                                    exoBinding.llPlay.visibility = View.VISIBLE
                                    exoBinding.llPause.visibility = View.GONE
                                    exoBinding.progressBar.visibility = View.GONE
                                    GlobalInitExoPlayer.player.playWhenReady = false

                                } else {
                                    Log.e("Curr audio End", mainPlayModelList[position].name)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Log.e("End State: ", e.message!!)
                            }
                        } else if (state == ExoPlayer.STATE_IDLE) {
                            if (AudioPlayerActivity.AudioInterrupted) {
                                Log.e("Exo Player state", "ExoPlayer.STATE_IDLE")
                            }
                        }
                    }

                    override fun onPlayerError(error: ExoPlaybackException) {
                        var intruptMethod: String? = ""
                        if (error.type == ExoPlaybackException.TYPE_SOURCE) {
                            intruptMethod = error.message + " " + error.sourceException.message
                            Log.e("onPlaybackError", error.message + " " + error.sourceException.message)
                        } else if (error.type == ExoPlaybackException.TYPE_RENDERER) {
                            intruptMethod = error.message + " " + error.rendererException.message
                            Log.e("onPlaybackError", error.message + " " + error.rendererException.message)
                        } else if (error.type == ExoPlaybackException.TYPE_UNEXPECTED) {
                            intruptMethod = error.message + " " + error.unexpectedException.message
                            Log.e("onPlaybackError", error.message + " " + error.unexpectedException.message)
                        } else if (error.type == ExoPlaybackException.TYPE_REMOTE) {
                            intruptMethod = error.message
                            Log.e("onPlaybackError", error.message!!)
                        } else {
                            intruptMethod = error.message
                            Log.e("onPlaybackError", error.message!!)
                        }
                        AudioInterrupted = true
                        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
                        //should check null because in airplane mode it will be null
                        val nc: NetworkCapabilities?
                        var downSpeed = 0f
                        var batLevel = 0
                        var upSpeed = 0f
                        if (BWSApplication.isNetworkConnected(ctx)) {
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
                    }
                })
                exoBinding.exoProgress.addListener(object : OnScrubListener {
                    override fun onScrubStart(timeBar: TimeBar, pos: Long) {
                        exoBinding.exoProgress.setPosition(pos)
                        exoBinding.exoProgress.setDuration(GlobalInitExoPlayer.player.duration)
                        oldSeekPosition = pos
                        val globalInitExoPlayer = GlobalInitExoPlayer()
                        globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList)
                        globalInitExoPlayer.UpdateNotificationAudioPLayer(ctx)
                    }

                    override fun onScrubMove(timeBar: TimeBar, position: Long) {}
                    override fun onScrubStop(timeBar: TimeBar, pos: Long, canceled: Boolean) {
                        GlobalInitExoPlayer.player.seekTo(position, pos)
                        exoBinding.exoProgress.setPosition(pos)
                        exoBinding.exoProgress.setDuration(GlobalInitExoPlayer.player.duration)
                        exoBinding.tvStartTime.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(pos),
                                TimeUnit.MILLISECONDS.toSeconds(pos) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(pos))))
                        val globalInitExoPlayer = GlobalInitExoPlayer()
                        globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList)
                        globalInitExoPlayer.UpdateNotificationAudioPLayer(ctx)
                    }
                })
                if (GlobalInitExoPlayer.player.playbackState == ExoPlayer.STATE_BUFFERING) {
                    exoBinding.llPlay.visibility = View.GONE
                    exoBinding.llPause.visibility= View.GONE
                    exoBinding.progressBar.visibility = View.VISIBLE
                } else if (GlobalInitExoPlayer.player.playWhenReady) {
                    exoBinding.llPlay.visibility = View.GONE
                    exoBinding.llPause.visibility = View.VISIBLE
                    exoBinding.progressBar.visibility = View.GONE
                } else if (!GlobalInitExoPlayer.player.playWhenReady) {
                    exoBinding.llPlay.visibility = View.VISIBLE
                    exoBinding.llPause.visibility= View.GONE
                    exoBinding.progressBar.visibility = View.GONE
                }
                val globalInitExoPlayer = GlobalInitExoPlayer()
                globalInitExoPlayer.InitNotificationAudioPLayer(ctx, mainPlayModelList)
                exoBinding.exoProgress.setBufferedPosition(GlobalInitExoPlayer.player.bufferedPosition)
                exoBinding.exoProgress.setPosition(GlobalInitExoPlayer.player.currentPosition)
                exoBinding.exoProgress.setDuration(GlobalInitExoPlayer.player.duration)
                setPlayerCtrView()
            } else {
                if (audioClick) {
                    exoBinding.progressBar.visibility = View.GONE
                    exoBinding.llPlay.visibility = View.GONE
                    exoBinding.llPause.visibility = View.VISIBLE
                } else if (GlobalInitExoPlayer.PlayerINIT) {
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

            exoBinding.llPause.setOnClickListener { view ->
                try {
                    GlobalInitExoPlayer.player.playWhenReady = false
                    val pss = GlobalInitExoPlayer.player.currentWindowIndex
                    //                    myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(pss).getImageFile());
                    exoBinding.llPlay.visibility = View.VISIBLE
                    exoBinding.llPause.visibility = View.GONE
                    exoBinding.progressBar.visibility = View.GONE

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
            exoBinding.llForwardSec.setOnClickListener { view ->
                try {
                    if (GlobalInitExoPlayer.player.duration - GlobalInitExoPlayer.player.currentPosition <= 30000) {
                        BWSApplication.showToast("Please Wait... ", ctx)
                    } else {
                        GlobalInitExoPlayer.player.seekTo(position, GlobalInitExoPlayer.player.currentPosition + 30000)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
            exoBinding.llBackWordSec.setOnClickListener { view ->
                try {
                    if (GlobalInitExoPlayer.player.currentPosition > 30000) {
                        GlobalInitExoPlayer.player.seekTo(position, GlobalInitExoPlayer.player.currentPosition - 30000)
                    } else if (GlobalInitExoPlayer.player.currentPosition < 30000) {
                        GlobalInitExoPlayer.player.seekTo(position, 0)
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
            binding.llInfo.setAlpha(1f)
            binding.llInfo.setClickable(true)
            binding.llInfo.setEnabled(true)
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
            binding.llInfo.setClickable(false)
            binding.llInfo.setEnabled(false)
            binding.llInfo.setAlpha(0.6f) 
            binding.llDownload.isClickable = false
            binding.llDownload.isEnabled = false
            binding.llDownload.alpha = 0.6f 
            exoBinding.rlSeekbar.isClickable = false
            exoBinding.rlSeekbar.isEnabled = false
            exoBinding.exoProgress.isClickable = false
            exoBinding.exoProgress.isEnabled = false
        }
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
        if (!BWSApplication.isNetworkConnected(ctx)) {
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
        name = mainPlayModelList[ps].name
        exoBinding.llPlay.setOnClickListener {
            if (GlobalInitExoPlayer.player != null) {
                if (!mainPlayModelList[position].audioFile.equals("", ignoreCase = true)) {
                    if (mainPlayModelList[GlobalInitExoPlayer.player.currentWindowIndex].id.equals(mainPlayModelList[mainPlayModelList.size - 1].id, ignoreCase = true)
                            && GlobalInitExoPlayer.player.duration - GlobalInitExoPlayer.player.currentPosition <= 20) {
                        GlobalInitExoPlayer.player.seekTo(position, 0)
                    }
                    GlobalInitExoPlayer.player.playWhenReady = true
                    val pss = GlobalInitExoPlayer.player.currentWindowIndex
                    //                    myBitmap = getMediaBitmap(ctx, mainPlayModelList.get(pss).getImageFile());
                    exoBinding.llPlay.visibility = View.GONE
                    exoBinding.llPause.visibility = View.VISIBLE
                    exoBinding.progressBar.visibility = View.GONE
                } else {
                    exoBinding.llPlay.visibility = View.GONE
                    exoBinding.llPause.visibility = View.VISIBLE
                    exoBinding.progressBar.visibility = View.GONE
                    GlobalInitExoPlayer.player.playWhenReady = true
                }
            } else {
                audioClick = true
                makePlayerArray()
            }
        }
        if (mainPlayModelList[ps].playlistID == null) {
            mainPlayModelList[ps].playlistID = ""
        }
        binding.tvAudioName.text=(name)
        if (GlobalInitExoPlayer.player == null) {
            exoBinding.tvStartTime.text = "00:00"
        }
        exoBinding.tvSongTime.text = mainPlayModelList[ps].audioDuration
        if (!url.equals("", ignoreCase = true)) {
            if (addToRecentPlayId.equals("", ignoreCase = true)) {
                addToRecentPlay()
            } else if (!id.equals(addToRecentPlayId, ignoreCase = true)) {
                addToRecentPlay()
                Log.e("Api call recent", id.toString())
            }
        }
        addToRecentPlayId = id
    }
    private fun addToRecentPlay() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            val listCall = APINewClient.getClient().getRecentlyPlayed(CoUserID, id)
            listCall.enqueue(object : Callback<SucessModel?> {
                override fun onResponse(call: Call<SucessModel?>, response: Response<SucessModel?>) {
                    try {
                        val model: SucessModel = response.body()!!
                    }catch (  e:java.lang.Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<SucessModel?>, t: Throwable) {
                }
            })
        }
    }

}