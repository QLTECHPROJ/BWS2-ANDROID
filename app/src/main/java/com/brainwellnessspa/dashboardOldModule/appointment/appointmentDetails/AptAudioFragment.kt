package com.brainwellnessspa.dashboardOldModule.appointment.appointmentDetails

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PorterDuff
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.billingOrderModule.activities.MembershipChangeActivity
import com.brainwellnessspa.dashboardModule.enhance.AddPlaylistActivity
import com.brainwellnessspa.dashboardModule.models.HomeScreenModel.ResponseData.DisclaimerAudio
import com.brainwellnessspa.dashboardOldModule.models.AppointmentDetailModel
import com.brainwellnessspa.dashboardOldModule.transParentPlayer.models.MainPlayModel
import com.brainwellnessspa.databinding.AudioAptListLayoutBinding
import com.brainwellnessspa.databinding.FragmentAptAudioBinding
import com.brainwellnessspa.encryptDecryptUtils.DownloadMedia
import com.brainwellnessspa.encryptDecryptUtils.FileUtils
import com.brainwellnessspa.roomDataBase.AudioDatabase
import com.brainwellnessspa.roomDataBase.DownloadAudioDetails
import com.brainwellnessspa.services.GlobalInitExoPlayer
import com.brainwellnessspa.services.GlobalInitExoPlayer.Companion.GetCurrentAudioPosition
import com.brainwellnessspa.services.GlobalInitExoPlayer.Companion.GetSourceName
import com.brainwellnessspa.services.GlobalInitExoPlayer.Companion.callNewPlayerRelease
import com.brainwellnessspa.utility.CONSTANTS
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.segment.analytics.Properties
import java.util.*

class AptAudioFragment : Fragment() {
    private var fManager: FragmentManager? = null
    var audioManager: AudioManager? = null
    var hundredVolume = 0
    var currentVolume = 0
    var maxVolume = 0
    var percent = 0
    lateinit var binding: FragmentAptAudioBinding
    var userId: String? = null
    var audioFlag: String? = null
    var isPlayDisclimer: String? = null
    private var appointmentDetail: ArrayList<AppointmentDetailModel.Audio>? = null

    //    Handler handler3;
    var startTime = 0
    var appointmentsAdapter: AudioListAdapter? = null
    var p: Properties? = null

    //    private Handler handler1;
    var fileNameList: List<String> = ArrayList()
    var playlistDownloadId: List<String> = ArrayList()
    var audiofilelist: List<String> = ArrayList()

    //    private Runnable UpdateSongTime3;
    private val listener: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("MyData")) {
                val data = intent.getStringExtra("MyData")
                Log.d("play_pause_Action", data!!)
                val sharedzw = activity!!.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
                audioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                val pIDz = sharedzw.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                if (audioFlag.equals("AppointmentDetailList", ignoreCase = true)) {
                    if (BWSApplication.player != null) {
                        if (data.equals("play", ignoreCase = true)) {
//                    BWSApplication.showToast("Play", getActivity());
                            appointmentsAdapter!!.notifyDataSetChanged()
                        } else {
//                    BWSApplication.showToast("pause", getActivity());
                            appointmentsAdapter!!.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }
    private val listener1: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            downloadData
            if (intent.hasExtra("Progress")) {
                appointmentsAdapter!!.notifyDataSetChanged()
            }
        }
    }

    override fun onResume() {
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(listener, IntentFilter("play_pause_Action"))
        super.onResume()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apt_audio, container, false)
        val view = binding.root
        //        handler1 = new Handler();
//        handler3 = new Handler();
        val shared1 = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREF_KEY_UserID, "")
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(listener, IntentFilter("play_pause_Action"))
        appointmentDetail = ArrayList()
        if (arguments != null) {
            appointmentDetail = requireArguments().getParcelableArrayList("AppointmentDetailList")
        }
        BWSApplication.DB = BWSApplication.getAudioDataBase(activity)
        audioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        currentVolume = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
        maxVolume = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        percent = 100
        hundredVolume = (currentVolume * percent) / maxVolume
        if (appointmentDetail!!.size != 0) {
            appointmentsAdapter = AudioListAdapter(appointmentDetail, activity, fManager)
            val recentlyPlayed: RecyclerView.LayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            binding.rvAudioList.layoutManager = recentlyPlayed
            binding.rvAudioList.itemAnimator = DefaultItemAnimator()
            binding.rvAudioList.adapter = appointmentsAdapter
            LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(listener1, IntentFilter("DownloadProgress"))
        }
        return view
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(listener)
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(listener1)
        super.onDestroy()
    }

    //                        handler1.postDelayed(updateSongTime1, 30000);
    private val downloadData: Unit
        get() {
            try {
                val sharedy = requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE)
                val gson = Gson()
                val jsony = sharedy.getString(CONSTANTS.PREF_KEY_DownloadName, gson.toString())
                val jsonx = sharedy.getString(CONSTANTS.PREF_KEY_DownloadUrl, gson.toString())
                val jsonq = sharedy.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, gson.toString())
                if (!jsony.equals(gson.toString(), ignoreCase = true)) {
                    val type = object : TypeToken<List<String?>?>() {}.type
                    fileNameList = gson.fromJson(jsony, type)
                    playlistDownloadId = gson.fromJson(jsonq, type)
                    audiofilelist = gson.fromJson(jsonx, type)
                    if (fileNameList.isNotEmpty()) {
//                        handler1.postDelayed(updateSongTime1, 30000);
                    } else {
                        audiofilelist = ArrayList()
                        fileNameList = ArrayList()
                        playlistDownloadId = ArrayList()
                    }
                } else {
                    fileNameList = ArrayList()
                    audiofilelist = ArrayList()
                    playlistDownloadId = ArrayList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    fun getMedia(audioFile: String?, ctx: Context?, download: String, llDownload: RelativeLayout, ivDownload: ImageView) {
        val shared1 = ctx!!.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        val userId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        val coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        BWSApplication.DB = BWSApplication.getAudioDataBase(ctx)
        BWSApplication.DB.taskDao()?.getLastIdByuId1(audioFile, coUserId)?.observe(requireActivity(), { audioList: List<DownloadAudioDetails?> ->
            when {
                audioList.isNotEmpty() -> {
            //                if (audioList.get(0).getDownload().equalsIgnoreCase("1")) {
                    disableDownload(llDownload, ivDownload)
                    //                }
                }
                download.equals("1", ignoreCase = true) -> {
                    disableDownload(llDownload, ivDownload)
                }
                else -> {
                    enableDownload(llDownload, ivDownload)
                }
            }
        })
    }

    private fun enableDownload(llDownload: RelativeLayout, ivDownload: ImageView) {
        try {
            llDownload.isClickable = true
            llDownload.isEnabled = true
            ivDownload.setColorFilter(ContextCompat.getColor(requireActivity(), R.color.black), PorterDuff.Mode.SRC_IN)
            ivDownload.setImageResource(R.drawable.ic_download_white_icon)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun disableDownload(llDownload: RelativeLayout, ivDownload: ImageView) {
        try {
            ivDownload.setImageResource(R.drawable.ic_download_white_icon)
            ivDownload.setColorFilter(ContextCompat.getColor(requireActivity(), R.color.dark_yellow), PorterDuff.Mode.SRC_IN)
            llDownload.isClickable = false
            llDownload.isEnabled = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callAddTransFrag() {
//        val fragment: Fragment = MiniPlayerFragment()
        val fragmentManager1 = requireActivity().supportFragmentManager
//        fragmentManager1.beginTransaction().add(R.id.flContainer, fragment).commit()
    }

    inner class AudioListAdapter(private val listModelList: ArrayList<AppointmentDetailModel.Audio>?, var ctx: Context?, var f_manager: FragmentManager?) : RecyclerView.Adapter<AudioListAdapter.MyViewHolder>() {
        var name: String? = null
        var songId: String? = null
        var ps = 0
        var nps = 0
        var updateSongTime1: Runnable? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: AudioAptListLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.audio_apt_list_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val audiolist = listModelList!![position]
            /*   updateSongTime1 = new Runnable() {
                @Override
                public void run() {
                    for (int f = 0; f < listModelList.size(); f++) {
                        if (fileNameList.size() != 0) {
                            for (int i = 0; i < fileNameList.size(); i++) {
                                if (fileNameList.get(i).equalsIgnoreCase(listModelList.get(f).getName())) {
                                    if (!filename.equalsIgnoreCase("") && filename.equalsIgnoreCase(listModelList.get(f).getName())) {
                                        if (downloadProgress <= 100) {
                                            if (BWSApplication.isNetworkConnected(ctx)) {
                                                notifyItemChanged(f);
                                            }
                                        } else {
                                            holder.binding.pbProgress.setVisibility(View.GONE);
                                            getDownloadData();
                                        }
                                    } else {
                                        if (BWSApplication.isNetworkConnected(ctx)) {
                                            notifyItemChanged(f);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (downloadProgress == 0) {
                        if (BWSApplication.isNetworkConnected(ctx)) {
                            notifyDataSetChanged();
                        }
                        getDownloadData();
                    }
                    handler1.postDelayed(this, 300);
                }
            };*/
//            holder.binding.equalizerview.setcolo
            val sharedzw = activity!!.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
            audioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
            val pIDz = sharedzw.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
            //            if (audioPlayz && (AudioFlag.equalsIgnoreCase("AppointmentDetailList") ||
//                    AudioFlag.equalsIgnoreCase("MainAudioList") || AudioFlag.equalsIgnoreCase("ViewAllAudioList"))) {
//                if (myAudioId.equalsIgnoreCase(audiolist.getID())) {
//                    songId = myAudioId;
//                    if (player != null) {
//                        if (!player.getPlayWhenReady()) {
//                            holder.binding.equalizerview.pause();
//                        } else
//                            holder.binding.equalizerview.resume(true);
//                    } else
//                        holder.binding.equalizerview.stop(true);
//                    holder.binding.equalizerview.setVisibility(View.VISIBLE);
//                    holder.binding.ivPlayIcon.setVisibility(View.GONE);
//                    holder.binding.llMainLayout.setBackgroundResource(R.color.highlight_background);
//                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                } else {
//                    holder.binding.equalizerview.setVisibility(View.GONE);
//                    holder.binding.ivPlayIcon.setVisibility(View.VISIBLE);
//                    holder.binding.llMainLayout.setBackgroundResource(R.color.white);
//                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                }
////                handler3.postDelayed(UpdateSongTime3, 500);
//            } else {
            holder.binding.equalizerview.visibility = View.GONE
            holder.binding.ivPlayIcon.visibility = View.VISIBLE
            holder.binding.llMainLayout.setBackgroundResource(R.color.white)
            holder.binding.ivBackgroundImage.visibility = View.VISIBLE
            //                handler3.removeCalldobacks(UpdateSongTime3);
//            }
            holder.binding.tvTitle.text = audiolist.name
            if (audiolist.audioDirection.equals("", ignoreCase = true)) {
                holder.binding.tvTime.visibility = View.GONE
            } else {
                holder.binding.tvTime.visibility = View.VISIBLE
                holder.binding.tvTime.text = audiolist.audioDirection
            }
            if (fileNameList.isNotEmpty()) {
                for (i in fileNameList.indices) {
                    if (fileNameList[i].equals(listModelList[position].name, ignoreCase = true) && playlistDownloadId[i].equals("", ignoreCase = true)) {
                        if (!DownloadMedia.filename.equals("", ignoreCase = true) && DownloadMedia.filename.equals(listModelList[position].name, ignoreCase = true)) {
                            if (DownloadMedia.downloadProgress <= 100) {
                                if (DownloadMedia.downloadProgress == 100) {
                                    holder.binding.pbProgress.visibility = View.GONE
                                    holder.binding.ivDownload.visibility = View.VISIBLE
                                } else {
                                    holder.binding.pbProgress.progress = DownloadMedia.downloadProgress
                                    holder.binding.pbProgress.visibility = View.VISIBLE
                                    holder.binding.ivDownload.visibility = View.GONE
                                }
                            } else {
                                holder.binding.pbProgress.visibility = View.GONE
                                holder.binding.ivDownload.visibility = View.VISIBLE
                            }
                            break
                        } else {
                            holder.binding.pbProgress.progress = 0
                            holder.binding.pbProgress.visibility = View.VISIBLE
                            holder.binding.ivDownload.visibility = View.GONE
                            break
                        }
                    } else if (i == fileNameList.size - 1) {
                        holder.binding.pbProgress.visibility = View.GONE
                        holder.binding.ivDownload.visibility = View.VISIBLE
                    }
                }
            } else {
                holder.binding.pbProgress.visibility = View.GONE
                holder.binding.ivDownload.visibility = View.VISIBLE
            }
            getMedia(audiolist.audioFile, activity, audiolist.download.toString(), holder.binding.llDownload, holder.binding.ivDownload)
            val measureRatio = BWSApplication.measureRatio(ctx, 0f, 1f, 1f, 0.13f, 0f)
            holder.binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            holder.binding.ivBackgroundImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivBackgroundImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivBackgroundImage.scaleType = ImageView.ScaleType.FIT_XY
            Glide.with(activity!!).load(audiolist.imageFile).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)
            Glide.with(activity!!).load(R.drawable.ic_image_bg).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivBackgroundImage)
            holder.binding.llMainLayout.setOnClickListener {
                comeRefreshData = 1
                val shared = ctx!!.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
                val audioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                val myPlaylist = shared.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
                val shared1 = ctx!!.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE)
                isPlayDisclimer = shared1.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1")
                if (audioFlag.equals("AppointmentDetailList", ignoreCase = true)) {
                    if (BWSApplication.isDisclaimer == 1) {
                        if (BWSApplication.player != null) {
                            if (!BWSApplication.player.playWhenReady) {
                                BWSApplication.player.playWhenReady = true
                            }
                        } else {
                            BWSApplication.audioClick = true
                            BWSApplication.miniPlayer = 1
                        }
                        callAddTransFrag()
                        BWSApplication.showToast("The audio shall start playing after the disclaimer", activity)
                    } else {
                        val listModelList2 = ArrayList<AppointmentDetailModel.Audio>()
                        listModelList2.add(listModelList[position])
                        callTransFrag(0, listModelList2, true)
                    }
                } else {
                    val listModelList2 = ArrayList<AppointmentDetailModel.Audio>()
                    listModelList2.add(listModelList[position])
                    val gson = Gson()
                    val shared12 = ctx!!.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE)
                    val isPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1")
                    val disclimerJson = shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
                    val type = object : TypeToken<DisclaimerAudio?>() {}.type
                    val arrayList = gson.fromJson<DisclaimerAudio>(disclimerJson, type)
                    val mainPlayModel = AppointmentDetailModel.Audio()
                    mainPlayModel.iD = arrayList.id
                    mainPlayModel.name = arrayList.name
                    mainPlayModel.audioFile = arrayList.audioFile
                    mainPlayModel.audioDirection = arrayList.audioDirection
                    mainPlayModel.audiomastercat = arrayList.audiomastercat
                    mainPlayModel.audioSubCategory = arrayList.audioSubCategory
                    mainPlayModel.imageFile = arrayList.imageFile
                    mainPlayModel.audioDuration = arrayList.audioDuration
                    var audioc = true
                    if (BWSApplication.isDisclaimer == 1) {
                        if (BWSApplication.player != null) {
                            BWSApplication.player.playWhenReady = true
                            audioc = false
                            listModelList2.add(0, mainPlayModel)
                        } else {
                            BWSApplication.isDisclaimer = 0
                            if (isPlayDisclimer.equals("1", ignoreCase = true)) {
                                audioc = true
                                listModelList2.add(0, mainPlayModel)
                            }
                        }
                    } else {
                        BWSApplication.isDisclaimer = 0
                        if (isPlayDisclimer.equals("1", ignoreCase = true)) {
                            audioc = true
                            listModelList2.add(0, mainPlayModel)
                        }
                    }
                    callTransFrag(0, listModelList2, audioc)
                }
            }
            holder.binding.llDownload.setOnClickListener {
                val url1: MutableList<String> = ArrayList()
                val name1: MutableList<String?> = ArrayList()
                val downloadPlaylistId: MutableList<String> = ArrayList()
                val sharedx = activity!!.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE)
                val gson1 = Gson()
                val json = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, gson1.toString())
                val json1 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadUrl, gson1.toString())
                val json2 = sharedx.getString(CONSTANTS.PREF_KEY_DownloadPlaylistId, gson1.toString())
                if (!json1.equals(gson1.toString(), ignoreCase = true)) {
                    val type = object : TypeToken<List<String?>?>() {}.type
                    val fileNameList = gson1.fromJson<List<String?>>(json, type)
                    val audioFile1 = gson1.fromJson<List<String>>(json1, type)
                    val playlistId1 = gson1.fromJson<List<String>>(json2, type)
                    if (fileNameList.isNotEmpty()) {
                        url1.addAll(audioFile1)
                        name1.addAll(fileNameList)
                        downloadPlaylistId.addAll(playlistId1)
                    }
                }
                name = listModelList[position].name
                val audioFile = listModelList[position].audioFile
                url1.add(audioFile.toString())
                name1.add(name)
                downloadPlaylistId.add("")
                if (url1.size != 0) {
                    val shared = ctx!!.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE)
                    val editor = shared.edit()
                    val gson = Gson()
                    val urlJson = gson.toJson(url1)
                    val nameJson = gson.toJson(name1)
                    val playlistIdJson = gson.toJson(downloadPlaylistId)
                    editor.putString(CONSTANTS.PREF_KEY_DownloadName, nameJson)
                    editor.putString(CONSTANTS.PREF_KEY_DownloadUrl, urlJson)
                    editor.putString(CONSTANTS.PREF_KEY_DownloadPlaylistId, playlistIdJson)
                    editor.apply()
                }
                if (!DownloadMedia.isDownloading) {
                    DownloadMedia.isDownloading = true
                    val downloadMedia = DownloadMedia(activity!!.applicationContext, activity)
                    downloadMedia.encrypt1(url1, name1, downloadPlaylistId)
                }
                holder.binding.pbProgress.visibility = View.VISIBLE
                holder.binding.ivDownload.visibility = View.GONE
                fileNameList = url1
                //                handler1.postDelayed(updateSongTime1, 500);
                val dirPath = FileUtils.getFilePath(activity!!.applicationContext, name.toString())
                saveMedia(ByteArray(1024), dirPath, listModelList[position], holder.binding.llDownload)
            }
            holder.binding.llRemoveAudio.setOnClickListener {
                if (listModelList[position].isLock.equals("1", ignoreCase = true)) {
                    val i = Intent(ctx, MembershipChangeActivity::class.java)
                    i.putExtra("ComeFrom", "Plan")
                    ctx!!.startActivity(i)
                } else if (listModelList[position].isLock.equals("2", ignoreCase = true)) {
                    BWSApplication.showToast(getString(R.string.reactive_plan), activity)
                } else if (listModelList[position].isLock.equals("0", ignoreCase = true) || listModelList[position].isLock.equals("", ignoreCase = true)) {
                 /*   val p = Properties()
                    p.putValue("audioId", listModelList[position].iD)
                    p.putValue("audioName", listModelList[position].name)
                    p.putValue("audioDescription", "")
                    p.putValue("directions", listModelList[position].audioDirection)
                    p.putValue("masterCategory", listModelList[position].audiomastercat)
                    p.putValue("subCategory", listModelList[position].audioSubCategory)
                    p.putValue("audioDuration", listModelList[position].audioDuration)
                    p.putValue("position", GetCurrentAudioPosition())
                    p.putValue("audioType", "Streaming")
                    p.putValue("source", GetSourceName(ctx!!))
                    p.putValue("playerType", "Main")
                    p.putValue("audioService", BWSApplication.appStatus(ctx))
                    p.putValue("bitRate", "")
                    p.putValue("sound", BWSApplication.hundredVolume.toString())
                    BWSApplication.addToSegment("Add To Playlist Clicked", p, CONSTANTS.track)*/
                    val i = Intent(ctx, AddPlaylistActivity::class.java)
                    i.putExtra("AudioId", listModelList[position].iD)
                    i.putExtra("ScreenView", "Appointment Audio Screen")
                    i.putExtra("PlaylistID", "")
                    i.putExtra("PlaylistName", "")
                    i.putExtra("PlaylistImage", "")
                    i.putExtra("PlaylistType", "")
                    i.putExtra("Liked", "0")
                    startActivity(i)
                }
            }
        }

        private fun callTransFrag(position: Int, listModelList: ArrayList<AppointmentDetailModel.Audio>, audioc: Boolean) {
            try {
                BWSApplication.miniPlayer = 1
                BWSApplication.audioClick = audioc
                if (audioc) {
                    callNewPlayerRelease()
                }
                val shared = ctx!!.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
                val editor = shared.edit()
                val gson = Gson()
                val json = gson.toJson(listModelList)
                editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json)
                editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "")
                editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "")
                editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "AppointmentDetailList")
                editor.apply()
                callAddTransFrag()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun saveMedia(encodeBytes: ByteArray, dirPath: String, audio: AppointmentDetailModel.Audio, llDownload: RelativeLayout) {
            val shared1 = ctx!!.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
            val userId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
            val coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
            val downloadAudioDetails = DownloadAudioDetails()
            downloadAudioDetails.UserID = coUserId!!
            downloadAudioDetails.ID = audio.iD!!
            downloadAudioDetails.Name = audio.name!!
            downloadAudioDetails.AudioFile = audio.audioFile!!
            downloadAudioDetails.PlaylistId = ""
            downloadAudioDetails.AudioDirection = audio.audioDirection!!
            downloadAudioDetails.Audiomastercat = audio.audiomastercat!!
            downloadAudioDetails.AudioSubCategory = audio.audioSubCategory!!
            downloadAudioDetails.ImageFile = audio.imageFile!!
            downloadAudioDetails.AudioDuration = audio.audioDuration!!
            downloadAudioDetails.IsSingle = "1"
            downloadAudioDetails.PlaylistId = ""
            downloadAudioDetails.IsDownload = "pending"
            downloadAudioDetails.DownloadProgress = 0
            try {
            /*    p!!.putValue("audioId", downloadAudioDetails.ID)
                p!!.putValue("audioName", downloadAudioDetails.Name)
                p!!.putValue("audioDescription", "")
                p!!.putValue("directions", downloadAudioDetails.AudioDirection)
                p!!.putValue("masterCategory", downloadAudioDetails.Audiomastercat)
                p!!.putValue("subCategory", downloadAudioDetails.AudioSubCategory)
                p!!.putValue("audioDuration", downloadAudioDetails.AudioDuration)
                p!!.putValue("position", GetCurrentAudioPosition())
                val name = audio.name
                if (name?.contains(downloadAudioDetails.Name!!) == true) {
                    p!!.putValue("audioType", "Downloaded")
                } else {
                    p!!.putValue("audioType", "Streaming")
                }
                p!!.putValue("source", GetSourceName(activity!!))
                p!!.putValue("bitRate", "")
                p!!.putValue("sound", hundredVolume.toString())
                BWSApplication.addToSegment("Audio Download Started", p, CONSTANTS.track)*/
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val sharedx1 = activity!!.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
            val audioFlag = sharedx1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
            val gsonx = Gson()
            val json11 = sharedx1.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gsonx.toString())
            val jsonw = sharedx1.getString(CONSTANTS.PREF_KEY_MainAudioList, gsonx.toString())
            var arrayList = ArrayList<DownloadAudioDetails?>()
            var arrayList2 = ArrayList<MainPlayModel>()
            var size = 0
            if (!jsonw.equals(gsonx.toString(), ignoreCase = true)) {
                val type1 = object : TypeToken<ArrayList<DownloadAudioDetails?>?>() {}.type
                val type0 = object : TypeToken<ArrayList<MainPlayModel?>?>() {}.type
                val gson1 = Gson()
                arrayList = gson1.fromJson(jsonw, type1)
                arrayList2 = gson1.fromJson(json11, type0)
                size = arrayList2.size
            }
            val position = sharedx1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
            if (audioFlag.equals("DownloadListAudio", ignoreCase = true)) {
                arrayList.add(downloadAudioDetails)
                val mainPlayModel1 = MainPlayModel()
                mainPlayModel1.id = downloadAudioDetails.ID!!
                mainPlayModel1.name = downloadAudioDetails.Name!!
                mainPlayModel1.audioFile = downloadAudioDetails.AudioFile!!
                mainPlayModel1.audioDirection = downloadAudioDetails.AudioDirection!!
                mainPlayModel1.audiomastercat = downloadAudioDetails.Audiomastercat!!
                mainPlayModel1.audioSubCategory = downloadAudioDetails.AudioSubCategory!!
                mainPlayModel1.imageFile = downloadAudioDetails.ImageFile!!
                mainPlayModel1.audioDuration = downloadAudioDetails.AudioDuration!!
                arrayList2.add(mainPlayModel1)
                val sharedd = activity!!.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
                val editor = sharedd.edit()
                val gson = Gson()
                val jsonx = gson.toJson(arrayList2)
                val json1q1 = gson.toJson(arrayList)
                editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json1q1)
                editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonx)
                editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position)
                editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "")
                editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "")
                editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "DownloadListAudio")
                editor.apply()
                if (arrayList2[position].audioFile != "") {
                    val downloadAudioDetailsList: MutableList<String?> = ArrayList()
                    val ge = GlobalInitExoPlayer()
                    downloadAudioDetailsList.add(downloadAudioDetails.Name)
                    ge.AddAudioToPlayer(size, arrayList2, downloadAudioDetailsList, ctx!!)
                }
                callAddTransFrag()
            }
            BWSApplication.DB = BWSApplication.getAudioDataBase(ctx)
            try {
                AudioDatabase.databaseWriteExecutor.execute { BWSApplication.DB.taskDao()?.insertMedia(downloadAudioDetails) }
            } catch (e: Exception) {
                println(e.message)
            } catch (e: OutOfMemoryError) {
                println(e.message)
            }
            llDownload.isClickable = false
            llDownload.isEnabled = false
        }

        override fun getItemCount(): Int {
            return listModelList!!.size
        }

        inner class MyViewHolder(var binding: AudioAptListLayoutBinding) : RecyclerView.ViewHolder(binding.root)

        init {
            val sharedx = ctx!!.getSharedPreferences(CONSTANTS.PREF_KEY_DownloadPlaylist, Context.MODE_PRIVATE)
            val gson = Gson()
            val json = sharedx.getString(CONSTANTS.PREF_KEY_DownloadName, gson.toString())
            if (!json.equals(gson.toString(), ignoreCase = true)) {
                val type = object : TypeToken<List<String?>?>() {}.type
                fileNameList = gson.fromJson(json, type)
            }
        }
    }

    companion object {
        @JvmField
        var comeRefreshData = 0
    }
}