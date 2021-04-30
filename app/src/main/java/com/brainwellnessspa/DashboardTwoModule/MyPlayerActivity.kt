package com.brainwellnessspa.DashboardTwoModule

import android.app.Activity
import android.app.Dialog
import android.app.UiModeManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.BWSApplication.PlayerAudioId
import com.brainwellnessspa.DashboardModule.Activities.AudioPlayerActivity
import com.brainwellnessspa.DashboardModule.Activities.AudioPlayerActivity.AudioInterrupted
import com.brainwellnessspa.DashboardModule.Activities.DashboardActivity
import com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.audioClick
import com.brainwellnessspa.DashboardModule.Adapters.DirectionAdapter
import com.brainwellnessspa.DashboardModule.Models.ViewAllAudioListModel
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Fragments.MiniPlayerFragment.addToRecentPlayId
import com.brainwellnessspa.DashboardModule.TransparentPlayer.Models.MainPlayModel
import com.brainwellnessspa.DashboardTwoModule.Model.*
import com.brainwellnessspa.R
import com.brainwellnessspa.RoomDataBase.DownloadAudioDetails
import com.brainwellnessspa.Services.GlobalInitExoPlayer
import com.brainwellnessspa.Services.GlobalInitExoPlayer.getMediaBitmap
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.ActivityViewPlayerBinding
import com.brainwellnessspa.databinding.AudioPlayerNewLayoutBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
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

class   MyPlayerActivity :AppCompatActivity(){
    lateinit var binding: ActivityViewPlayerBinding
    var AudioPlayerFlag: String? = ""
    var mainPlayModelList = ArrayList<MainPlayModel>()
    var position = 0
    var listSize:Int = 0
    var CoUserID: String? = ""
    lateinit var act: Activity;
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
        act = this@MyPlayerActivity
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        USERID = shared.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")
        playerControlView = Assertions.checkNotNull(binding.playerControlView)
        exoBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.audio_player_new_layout, binding.playerControlView, false)
        binding.playerControlView.addView(exoBinding.getRoot())
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding.llInfo.setOnClickListener { v ->
            BWSApplication.callAudioDetails(mainPlayModelList[position].id,ctx,act,CoUserID)

////            TODO Mansi  Hint This code is Audio Detail Dialog
//            val dialog = Dialog(ctx as MyPlayerActivity)
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//            dialog.setContentView(R.layout.open_detail_page_layout)
//            dialog.window!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.blue_transparent)))
//            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//            val tvTitleDec = dialog.findViewById<TextView>(R.id.tvTitleDec)
//            val tvSubDec = dialog.findViewById<TextView>(R.id.tvSubDec)
//            val tvReadMore = dialog.findViewById<TextView>(R.id.tvReadMore)
//            val tvSubDire = dialog.findViewById<TextView>(R.id.tvSubDire)
//            val tvDire = dialog.findViewById<TextView>(R.id.tvDire)
//            val tvDesc = dialog.findViewById<TextView>(R.id.tvDesc)
//            val tvDuration = dialog.findViewById<TextView>(R.id.tvDuration)
//            val ivRestaurantImage = dialog.findViewById<ImageView>(R.id.ivRestaurantImage)
//            val ivLike = dialog.findViewById<ImageView>(R.id.ivLike)
//            val progressBar = dialog.findViewById<ProgressBar>(R.id.progressBar)
//            val progressBarHolder = dialog.findViewById<FrameLayout>(R.id.progressBarHolder)
//            val cvImage = dialog.findViewById<RelativeLayout>(R.id.cvImage)
//            val llLike = dialog.findViewById<LinearLayout>(R.id.llLike)
//            val llAddPlaylist = dialog.findViewById<LinearLayout>(R.id.llAddPlaylist)
//            val llAddQueue = dialog.findViewById<LinearLayout>(R.id.llAddQueue)
//            val llDownload = dialog.findViewById<LinearLayout>(R.id.llDownload)
//            val llRemovePlaylist = dialog.findViewById<LinearLayout>(R.id.llRemovePlaylist)
//            val llShuffle = dialog.findViewById<LinearLayout>(R.id.llShuffle)
//            val llRepeat = dialog.findViewById<LinearLayout>(R.id.llRepeat)
//            val llViewQueue = dialog.findViewById<LinearLayout>(R.id.llViewQueue)
//            val rvDirlist: RecyclerView = dialog.findViewById(R.id.rvDirlist)
//            if (BWSApplication.isNetworkConnected(ctx)) {
//                progressBar.visibility = View.VISIBLE
//                progressBar.invalidate()
//                val listCall = APINewClient.getClient().getAudioDetail(CoUserID, mainPlayModelList[position].id)
//                listCall.enqueue(object : Callback<AudioDetailModel?> {
//                    override fun onResponse(call: Call<AudioDetailModel?>, response: Response<AudioDetailModel?>) {
//                        try {
//                            progressBar.visibility = View.GONE
//                            val listModel = response.body()
//                            cvImage.visibility = View.VISIBLE
//                            llLike.visibility = View.GONE
//                            llAddPlaylist.visibility = View.VISIBLE
//                            llAddQueue.visibility = View.GONE
//                            llDownload.visibility = View.VISIBLE
//                            llShuffle.visibility = View.VISIBLE
//                            llRepeat.visibility = View.VISIBLE
//                            llViewQueue.visibility = View.VISIBLE
////                            AudioId = listModel!!.responseData!![0].id
//                            llRemovePlaylist.visibility = View.VISIBLE
//
////                        if (comeFrom.equalsIgnoreCase("myPlayList") || comeFrom.equalsIgnoreCase("myLikeAudioList")) {
////                            binding.llRemovePlaylist.setVisibility(View.GONE);
////                        } else {
////                            if (MyPlaylist.equalsIgnoreCase("myPlaylist")) {
////                                binding.llRemovePlaylist.setVisibility(View.VISIBLE);
////                            } else {
////                                binding.llRemovePlaylist.setVisibility(View.GONE);
////                            }
////                        }
//                            try {
//                                Glide.with(ctx as MyPlayerActivity).load(listModel!!.responseData!![0].imageFile).thumbnail(0.05f)
//                                        .apply(RequestOptions.bitmapTransform(RoundedCorners(12))).priority(Priority.HIGH)
//                                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(ivRestaurantImage)
//                            } catch (e: java.lang.Exception) {
//                                e.printStackTrace()
//                            }
//                            if (listModel!!.responseData!![0].audioDescription.equals("", ignoreCase = true)) {
//                                tvTitleDec.visibility = View.GONE
//                                tvSubDec.visibility = View.GONE
//                            } else {
//                                tvTitleDec.visibility = View.VISIBLE
//                                tvSubDec.visibility = View.VISIBLE
//                            }
//                            tvSubDec.text = listModel.responseData!![0].audioDescription
//                            val linecount = tvSubDec.lineCount
//                            if (linecount >= 4) {
//                                tvReadMore.visibility = View.VISIBLE
//                            } else {
//                                tvReadMore.visibility = View.GONE
//                            }
//                            if (listModel.responseData!![0].audiomastercat.equals("", ignoreCase = true)) {
//                                tvDesc.visibility = View.GONE
//                            } else {
//                                tvDesc.visibility = View.VISIBLE
//                                tvDesc.text = listModel.responseData!![0].audiomastercat
//                            }
//                            tvDuration.text = listModel.responseData!![0].audioDuration
//                            if (listModel.responseData!![0].audioDirection.equals("", ignoreCase = true)) {
//                                tvSubDire.text = ""
//                                tvSubDire.visibility = View.GONE
//                                tvDire.visibility = View.GONE
//                            } else {
//                                tvSubDire.text = listModel.responseData!![0].audioDirection
//                                tvSubDire.visibility = View.VISIBLE
//                                tvDire.visibility = View.VISIBLE
//                            }
//
////                            if (listModel.getResponseData().get(0).getLike().equalsIgnoreCase("1")) {
////                                ivLike.setImageResource(R.drawable.ic_fill_like_icon);
////                            } else if (!listModel.getResponseData().get(0).getLike().equalsIgnoreCase("0")) {
////                                ivLike.setImageResource(R.drawable.ic_like_white_icon);
////                            }
//                            tvReadMore.setOnClickListener { v12: View? ->
//                                val dialog1 = Dialog(ctx as MyPlayerActivity)
//                                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
//                                dialog1.setContentView(R.layout.full_desc_layout)
//                                dialog1.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                                dialog1.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//                                val tvDesc = dialog1.findViewById<TextView>(R.id.tvDesc)
//                                val tvClose = dialog1.findViewById<RelativeLayout>(R.id.tvClose)
//                                tvDesc.text = listModel.responseData!![0].audioDescription
//                                dialog1.setOnKeyListener { v3: DialogInterface?, keyCode: Int, event: KeyEvent? ->
//                                    if (keyCode == KeyEvent.KEYCODE_BACK) {
//                                        dialog1.dismiss()
//                                        return@setOnKeyListener true
//                                    }
//                                    false
//                                }
//                                tvClose.setOnClickListener { v14: View? -> dialog1.dismiss() }
//                                dialog1.show()
//                                dialog1.setCancelable(false)
//                            }
//                            if (listModel.responseData!![0].audioSubCategory.equals("", ignoreCase = true)) {
//                                rvDirlist.visibility = View.GONE
//                            } else {
//                                rvDirlist.visibility = View.VISIBLE
//                                val elements = listModel.responseData!![0].audioSubCategory!!.split(",").toTypedArray()
//                                val direction = Arrays.asList(*elements)
//                                val directionAdapter = DirectionAdapter(direction, ctx)
//                                val recentlyPlayed: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
//                                rvDirlist.layoutManager = recentlyPlayed
//                                rvDirlist.itemAnimator = DefaultItemAnimator()
//                                rvDirlist.adapter = directionAdapter
//                            }
//                        } catch (e: java.lang.Exception) {
//                            e.printStackTrace()
//                        }
//                    }
//
//                    override fun onFailure(call: Call<AudioDetailModel?>, t: Throwable) {
//                        progressBar.visibility = View.GONE
//                    }
//                })
//            }
//            llAddPlaylist.setOnClickListener { v13: View? ->
//                if (BWSApplication.isNetworkConnected(ctx)) {
//                    BWSApplication.showProgressBar(progressBar, progressBarHolder, ctx as MyPlayerActivity)
//                    val listCall = APINewClient.getClient().RemoveAudio(CoUserID,  /*AudioId*/"10",  /*PlaylistId*/"34")
//                    listCall.enqueue(object : Callback<SucessModel?> {
//                        override fun onResponse(call: Call<SucessModel?>, response: Response<SucessModel?>) {
//                            try {
//                                if (response.isSuccessful) {
//                                    BWSApplication.hideProgressBar(progressBar, progressBarHolder, ctx as MyPlayerActivity)
//                                    val listModel = response.body()
//                                    val shared: SharedPreferences = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE)
//                                    val audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true)
//                                    //                                AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0");
////                                int pos = shared.getInt(CONSTANTS.PREF_KEY_position, 0);
////
////                                if (audioPlay) {
////                                    if (AudioFlag.equalsIgnoreCase("SubPlayList")) {
////                                        Gson gson12 = new Gson();
////                                        String json12 = shared.getString(CONSTANTS.PREF_KEY_modelList, String.valueOf(gson12));
////                                        Type type1 = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
////                                        }.getType();
////                                        ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList1 = gson12.fromJson(json12, type1);
////
////                                        if (!comeFrom.equalsIgnoreCase("")) {
////                                            mData.remove(position);
////                                            String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
////                                            int oldpos = pos;
////                                            if (pID.equalsIgnoreCase(PlaylistId)) {
////                                                if (mData.size() != 0) {
////                                                    if (pos == position && position < mData.size() - 1) {
////                                                        pos = pos;
////                                                    } else if (pos == position && position == mData.size() - 1) {
////                                                        pos = 0;
////                                                    } else if (pos < position && pos < mData.size() - 1) {
////                                                        pos = pos;
////                                                    } else if (pos < position && pos == mData.size() - 1) {
////                                                        pos = pos;
////                                                    } else if (pos > position && pos == mData.size()) {
////                                                        pos = pos - 1;
////                                                    }
////                                                    SharedPreferences sharedd = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
////                                                    SharedPreferences.Editor editor = sharedd.edit();
////                                                    Gson gson = new Gson();
////                                                    String json = gson.toJson(mData);
////                                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json);
////                                                    editor.putInt(CONSTANTS.PREF_KEY_position, pos);
////                                                    editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
////                                                    editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
////                                                    editor.putString(CONSTANTS.PREF_KEY_PlaylistId, PlaylistId);
////                                                    editor.putString(CONSTANTS.PREF_KEY_myPlaylist, myPlaylist);
////                                                    editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SubPlayList");
////                                                    editor.commit();
////                                                    Type type = new TypeToken<ArrayList<SubPlayListModel.ResponseData.PlaylistSong>>() {
////                                                    }.getType();
////                                                    ArrayList<SubPlayListModel.ResponseData.PlaylistSong> arrayList = gson.fromJson(json, type);
////                                                    listSize = arrayList.size();
////                                                    for (int i = 0; i < listSize; i++) {
////                                                        MainPlayModel mainPlayModel = new MainPlayModel();
////                                                        mainPlayModel.setID(arrayList.get(i).getID());
////                                                        mainPlayModel.setName(arrayList.get(i).getName());
////                                                        mainPlayModel.setAudioFile(arrayList.get(i).getAudioFile());
////                                                        mainPlayModel.setPlaylistID(arrayList.get(i).getPlaylistID());
////                                                        mainPlayModel.setAudioDirection(arrayList.get(i).getAudioDirection());
////                                                        mainPlayModel.setAudiomastercat(arrayList.get(i).getAudiomastercat());
////                                                        mainPlayModel.setAudioSubCategory(arrayList.get(i).getAudioSubCategory());
////                                                        mainPlayModel.setImageFile(arrayList.get(i).getImageFile());
////                                                        mainPlayModel.setLike(arrayList.get(i).getLike());
////                                                        mainPlayModel.setDownload(arrayList.get(i).getDownload());
////                                                        mainPlayModel.setAudioDuration(arrayList.get(i).getAudioDuration());
////                                                        mainPlayModelList.add(mainPlayModel);
////                                                    }
////                                                    SharedPreferences sharedz = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
////                                                    SharedPreferences.Editor editor1 = sharedz.edit();
////                                                    Gson gsonz = new Gson();
////                                                    String jsonz = gsonz.toJson(mainPlayModelList);
////                                                    editor1.putString(CONSTANTS.PREF_KEY_audioList, jsonz);
////                                                    editor1.commit();
////                                                    if (player != null) {
////                                                        player.removeMediaItem(oldpos);
////                                                        player.setPlayWhenReady(true);
////                                                    }
////                                                    finish();
////                                                }
////                                            }
////                                            finish();
////                                        } else {
////                                            mainPlayModelList.remove(pos);
////                                            arrayList1.remove(pos);
////                                            String pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0");
////                                            if (pID.equalsIgnoreCase(PlaylistId)) {
////                                                int oldpos = pos;
////                                                if (mainPlayModelList.size() != 0) {
////                                                    if (pos < mainPlayModelList.size() - 1) {
////                                                        pos = pos;
////                                                    } else if (pos == mainPlayModelList.size() - 1) {
////                                                        pos = 0;
////                                                    } else if (pos == mainPlayModelList.size()) {
////                                                        pos = 0;
////                                                    } else if (pos > mainPlayModelList.size()) {
////                                                        pos = pos - 1;
////                                                    }
////                                                    SharedPreferences sharedd = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, Context.MODE_PRIVATE);
////                                                    SharedPreferences.Editor editor = sharedd.edit();
////                                                    Gson gson = new Gson();
////                                                    String json = gson.toJson(mainPlayModelList);
////                                                    String json1 = gson.toJson(arrayList1);
////                                                    editor.putString(CONSTANTS.PREF_KEY_modelList, json1);
////                                                    editor.putString(CONSTANTS.PREF_KEY_audioList, json);
////                                                    editor.putInt(CONSTANTS.PREF_KEY_position, pos);
////                                                    editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
////                                                    editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
////                                                    editor.putString(CONSTANTS.PREF_KEY_PlaylistId, PlaylistId);
////                                                    editor.putString(CONSTANTS.PREF_KEY_myPlaylist, myPlaylist);
////                                                    editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SubPlayList");
////                                                    editor.commit();
//////                                                if(mainPlayModelList.size()==1){
//////                                                    miniPlayer = 1;
//////                                                    audioClick = true;
//////                                                    callNewPlayerRelease();
//////                                                }else {
////                                                    if (player != null) {
////                                                        player.removeMediaItem(oldpos);
////                                                    }
//////                                                }
////                                                    Intent i = new Intent(ctx, AudioPlayerActivity.class);
////                                                    i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
////                                                    ctx.startActivity(i);
////                                                    finish();
////                                                    overridePendingTransition(0, 0);
////                                                }
////                                            }
////                                        }
////                                    }
////                                }
//                                }
//                            } catch (e: java.lang.Exception) {
//                                e.printStackTrace()
//                            }
//                        }
//
//                        override fun onFailure(call: Call<SucessModel?>, t: Throwable) {
//                            BWSApplication.hideProgressBar(progressBar, progressBarHolder, ctx as MyPlayerActivity)
//                        }
//                    })
//                } else {
//                    BWSApplication.showToast(getString(R.string.no_server_found), ctx)
//                }
//            }
//            llAddPlaylist.setOnClickListener { view11: View? ->
////                comeAddPlaylist = 2;
//                val i = Intent(ctx, AddPlaylistActivity::class.java)
//                i.putExtra("AudioId", mainPlayModelList[position].id)
//                i.putExtra("ScreenView", "Audio Details Screen")
//                i.putExtra("PlaylistID", "")
//                i.putExtra("PlaylistName", "")
//                i.putExtra("PlaylistImage", "")
//                i.putExtra("PlaylistType", "")
//                i.putExtra("Liked", "0")
//                startActivity(i)
//            }
//            dialog.setOnKeyListener { v1: DialogInterface?, keyCode: Int, event: KeyEvent? ->
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    dialog.dismiss()
//                    return@setOnKeyListener true
//                }
//                false
//            }
//            dialog.show()
//            dialog.setCancelable(false)
        }

        makePlayerArray()
    }

    private fun makePlayerArray() {
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
        }else if (AudioPlayerFlag.equals("ViewAllAudioList", ignoreCase = true)) {
            val type = object : TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail?>>() {}.type
            val arrayList = gson.fromJson<ArrayList<ViewAllAudioListModel.ResponseData.Detail?>>(json, type)
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
        }else if (AudioPlayerFlag.equals(getString(R.string.top_categories), ignoreCase = true)) {
            val type = object : TypeToken<ArrayList<ViewAllAudioListModel.ResponseData.Detail?>>() {}.type
            val arrayList = gson.fromJson<ArrayList<ViewAllAudioListModel.ResponseData.Detail?>>(json, type)
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
        }else if (AudioPlayerFlag.equals("SearchModelAudio", ignoreCase = true)) {
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
        }else if (AudioPlayerFlag.equals("SearchAudio", ignoreCase = true)) {
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
        }else if (AudioPlayerFlag.equals("DownloadListAudio", ignoreCase = true)) {
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
        }else if (AudioPlayerFlag.equals("Downloadlist", ignoreCase = true)) {
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
        }else if (AudioPlayerFlag.equals("playlist", ignoreCase = true)) {
            val type = object : TypeToken<ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong?>>() {}.type
            val arrayList = gson.fromJson<ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong?>>(json, type)
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
        if(audioClick)
            getPrepareShowData()
        else
            callButtonText(position)
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
        PlayerAudioId = id
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
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<SucessModel?>, t: Throwable) {
                }
            })
        }
    }

}