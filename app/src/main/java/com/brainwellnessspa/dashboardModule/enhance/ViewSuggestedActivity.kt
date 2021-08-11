package com.brainwellnessspa.dashboardModule.enhance

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.app.NotificationManager
import android.content.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.activities.MyPlayerActivity
import com.brainwellnessspa.dashboardModule.models.*
import com.brainwellnessspa.dashboardModule.models.HomeScreenModel.ResponseData.DisclaimerAudio
import com.brainwellnessspa.dashboardModule.models.MainPlayModel
import com.brainwellnessspa.databinding.ActivityViewSuggestedBinding
import com.brainwellnessspa.databinding.DownloadsLayoutBinding
import com.brainwellnessspa.services.GlobalInitExoPlayer
import com.brainwellnessspa.userModule.signupLogin.SignInActivity
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class  ViewSuggestedActivity : AppCompatActivity() {
    lateinit var binding: ActivityViewSuggestedBinding
    lateinit var activity: Activity
    lateinit var ctx: Context
    var userId: String? = null
    var coUserId: String? = null
    var name: String? = null
    var playlistId: String? = null
    var listModel: ArrayList<SuggestedModel.ResponseData>? = null
    private var playlistModel: ArrayList<SearchPlaylistModel.ResponseData>? = null
    var adpater: AudiosListAdpater? = null
    var stackStatus = 0
    var myBackPress = false
    private var numStarted = 0
    private val listener: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("MyData")) {
                val data = intent.getStringExtra("MyData")
                Log.d("play_pause_Action", data!!)
                val sharedzw = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val audioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                if (!audioFlag.equals("Downloadlist", ignoreCase = true) && !audioFlag.equals("playlist", ignoreCase = true) && !audioFlag.equals("TopCategories", ignoreCase = true)) {
                    if (player != null) {
                        adpater!!.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_suggested)
        ctx = this@ViewSuggestedActivity
        activity = this@ViewSuggestedActivity
        val shared = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(AppLifecycleCallback())
        }
        if (intent != null) {
            name = intent.getStringExtra("Name")
            playlistId = intent.getStringExtra(CONSTANTS.PlaylistID)
            if (intent.hasExtra("AudiolistModel")) {
                listModel = intent.getParcelableArrayListExtra("AudiolistModel")
            }
            if (intent.hasExtra("PlaylistModel")) {
                playlistModel = intent.getParcelableArrayListExtra("PlaylistModel")
            }
        }

        binding.llBack.setOnClickListener {
            myBackPress = true
            val i = Intent(ctx, AddAudioActivity::class.java)
            i.putExtra("PlaylistID", playlistId)
            startActivity(i)
            finish()
        }
    }

    override fun onResume() {
        prepareData()
        super.onResume()
    }

    public override fun onPause() {
        LocalBroadcastManager.getInstance(this@ViewSuggestedActivity).unregisterReceiver(listener)
        super.onPause()
    }

    override fun onBackPressed() {
        myBackPress = true
        val i = Intent(ctx, AddAudioActivity::class.java)
        i.putExtra("PlaylistID", playlistId)
        startActivity(i)
        finish()
    }

    private fun prepareData() {
        val gson = Gson()
        val shared1x = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
        val audioPlayerFlagx = shared1x.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        val playerPositionx = shared1x.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
        val json = shared1x.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gson.toString())
        var mainPlayModelList = ArrayList<MainPlayModel>()
        if (audioPlayerFlagx != "0") {
            if (!json.equals(gson.toString(), ignoreCase = true)) {
                val type = object : TypeToken<ArrayList<MainPlayModel?>?>() {}.type
                mainPlayModelList = gson.fromJson(json, type)
            }
            PlayerAudioId = mainPlayModelList[playerPositionx].id
        }
        /* try {
            GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
            globalInitExoPlayer.UpdateMiniPlayer(ctx);
            SharedPreferences shared = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
            if (!AudioFlag.equalsIgnoreCase("0")) {
                comefromDownload = "1";
                callAddFrag();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(0, 8, 0, 210);
                binding.llSpace.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(0, 8, 0, 20);
                binding.llSpace.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        /* if (!AudioFlag.equalsIgnoreCase("0")) {
            comefromDownload = "1";
            callAddFrag();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(0, 8, 0, 210);
            binding.llSpace.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(0, 8, 0, 20);
            binding.llSpace.setLayoutParams(params);
        }*/

        binding.tvTitle.text = name
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        binding.rvMainAudio.layoutManager = layoutManager
        binding.rvMainAudio.itemAnimator = DefaultItemAnimator()
        if (name.equals("Suggested Audios", ignoreCase = true)) {
            val section = ArrayList<SegmentAudio>()
            for (i in listModel!!.indices) {
                val e = SegmentAudio()
                e.audioId = listModel!![i].iD
                e.audioName = listModel!![i].name
                e.masterCategory = listModel!![i].audiomastercat
                e.subCategory = listModel!![i].audioSubCategory
                e.audioDuration = listModel!![i].audioDirection
                section.add(e)
            }
            val p = Properties()
            p.putValue("audios", gson.toJson(section))
            p.putValue("source", "Search Screen")
            addToSegment("Suggested Audios List Viewed", p, CONSTANTS.screen)
            adpater = AudiosListAdpater(listModel)
            LocalBroadcastManager.getInstance(this@ViewSuggestedActivity).registerReceiver(listener, IntentFilter("play_pause_Action"))
            binding.rvMainAudio.adapter = adpater
        } /*else if (Name.equalsIgnoreCase("Suggested Playlist")) {
            ArrayList<SegmentPlaylist> section = new ArrayList<>();
            for (int i = 0; i < PlaylistModel.size(); i++) {
                SegmentPlaylist e = new SegmentPlaylist();
                e.setPlaylistId(PlaylistModel.get(i).getID());
                e.setPlaylistName(PlaylistModel.get(i).getName());
                e.setPlaylistType(PlaylistModel.get(i).getCreated());
                e.setPlaylistDuration(PlaylistModel.get(i).getTotalhour() + "h " + PlaylistModel.get(i).getTotalminute() + "m");
                e.setAudioCount(PlaylistModel.get(i).getTotalAudio());
                section.add(e);
            }
            Properties p = new Properties();
            p.putValue("playlists", gson.toJson(section));
            p.putValue("source", "Search Screen");
            BWSApplication.addToSegment("Suggested Playlists List Viewed", p, CONSTANTS.screen);
            SuggestionPlayListsAdpater adpater = new SuggestionPlayListsAdpater(PlaylistModel);
            binding.rvMainAudio.setAdapter(adpater);
        }*/
    }

    private fun callAddAudioToPlaylist(AudioID: String?, FromPlaylistId: String, s1: String) {
        if (isNetworkConnected(ctx)) {
            showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall = APINewClient.client.getAddSearchAudioFromPlaylist(coUserId, AudioID, playlistId, FromPlaylistId)
            listCall.enqueue(object : Callback<AddToPlaylistModel?> {
                override fun onResponse(call: Call<AddToPlaylistModel?>, response: Response<AddToPlaylistModel?>) {
                    try {
                        hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModels = response.body()
                        if (listModels != null) {
                            if (listModels.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                                val audioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                                val myPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                                val myPlaylistName = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "")
                                val playFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
                                var playerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                                if (audioPlayerFlag.equals("playList", ignoreCase = true) && myPlaylist.equals(playlistId, ignoreCase = true)) {
                                    val gsonx = Gson()
                                    val json = shared1.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gsonx.toString())
                                    val type = object : TypeToken<ArrayList<MainPlayModel?>?>() {}.type
                                    var mainPlayModelListold = ArrayList<MainPlayModel>()
                                    mainPlayModelListold = gsonx.fromJson(json, type)
                                    val id = mainPlayModelListold[playerPosition].id
                                    val size = mainPlayModelListold.size
                                    val mainPlayModelList = ArrayList<MainPlayModel>()
                                    val playlistSongs = ArrayList<SubPlayListModel.ResponseData.PlaylistSong>()
                                    for (i in listModels.responseData!!.indices) {
                                        val mainPlayModel = MainPlayModel()
                                        mainPlayModel.id = listModels.responseData!![i].iD!!
                                        mainPlayModel.name = listModels.responseData!![i].name!!
                                        mainPlayModel.audioFile = listModels.responseData!![i].audioFile!!
                                        mainPlayModel.playlistID = listModels.responseData!![i].playlistID!!
                                        mainPlayModel.audioDirection = listModels.responseData!![i].audioDirection!!
                                        mainPlayModel.audiomastercat = listModels.responseData!![i].audiomastercat!!
                                        mainPlayModel.audioSubCategory = listModels.responseData!![i].audioSubCategory!!
                                        mainPlayModel.imageFile = listModels.responseData!![i].imageFile!!
                                        mainPlayModel.audioDuration = listModels.responseData!![i].audioDuration!!
                                        mainPlayModelList.add(mainPlayModel)
                                    }
                                    for (i in listModels.responseData!!.indices) {
                                        val mainPlayModel = SubPlayListModel.ResponseData.PlaylistSong()
                                        mainPlayModel.iD = listModels.responseData!![i].iD
                                        mainPlayModel.name = listModels.responseData!![i].name
                                        mainPlayModel.audioFile = listModels.responseData!![i].audioFile
                                        mainPlayModel.playlistID = listModels.responseData!![i].playlistID
                                        mainPlayModel.audioDirection = listModels.responseData!![i].audioDirection
                                        mainPlayModel.audiomastercat = listModels.responseData!![i].audiomastercat
                                        mainPlayModel.audioSubCategory = listModels.responseData!![i].audioSubCategory
                                        mainPlayModel.imageFile = listModels.responseData!![i].imageFile
                                        mainPlayModel.like = listModels.responseData!![i].like
                                        mainPlayModel.download = listModels.responseData!![i].download
                                        mainPlayModel.audioDuration = listModels.responseData!![i].audioDuration
                                        playlistSongs.add(mainPlayModel)
                                    }
                                    for (i in mainPlayModelList.indices) {
                                        if (mainPlayModelList[i].id.equals(id, ignoreCase = true)) {
                                            playerPosition = i
                                            break
                                        }
                                    }
                                    val sharedd = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                                    val editor = sharedd.edit()
                                    val gson = Gson()
                                    val jsonx = gson.toJson(mainPlayModelList)
                                    val json11 = gson.toJson(playlistSongs)
                                    editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json11)
                                    editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonx)
                                    editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, playerPosition)
                                    editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, playlistId)
                                    editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, myPlaylistName)
                                    editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "Created")
                                    editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "playlist")
                                    editor.apply()
                                    if (mainPlayModelList[playerPosition].audioFile != "") {
                                        val downloadAudioDetailsList: List<String> = ArrayList()
                                        val ge = GlobalInitExoPlayer()
                                        ge.AddAudioToPlayer(size, mainPlayModelList, downloadAudioDetailsList, ctx)
                                    }
                                    if (player != null) {
                                        //                                    callAddFrag();
                                    }
                                }
                                showToast(listModels.responseMessage, activity)
                                if (s1.equals("1", ignoreCase = true)) {
                                    finish()
                                }
                            } else if (listModels.responseCode.equals(getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
                                deleteCall(activity)
                                showToast(listModels.responseMessage, activity)
                                val i = Intent(activity, SignInActivity::class.java)
                                i.putExtra("mobileNo", "")
                                i.putExtra("countryCode", "")
                                i.putExtra("name", "")
                                i.putExtra("email", "")
                                i.putExtra("countryShortName", "")
                                startActivity(i)
                                finish()
                            }
                        }
                    } catch (e: Exception) {
                        hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<AddToPlaylistModel?>, t: Throwable) {
                    hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            showToast(getString(R.string.no_server_found), activity)
        }
    }

    private fun callAddFrag() {
        /* Fragment fragment = new MiniPlayerFragment();
        FragmentManager fragmentManager1 = getSupportFragmentManager();
        fragmentManager1.beginTransaction()
                .add(R.id.flContainer, fragment)
                .commit();*/
    }

    inner class AudiosListAdpater(private val listModel: ArrayList<SuggestedModel.ResponseData>?) : RecyclerView.Adapter<AudiosListAdpater.MyViewHolder>() {
        var songId: String? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: DownloadsLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.downloads_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = listModel!![position].name
            holder.binding.tvTime.text = listModel[position].audioDuration
            holder.binding.pbProgress.visibility = View.GONE
            holder.binding.ivIcon.setImageResource(R.drawable.ic_add_two_icon)
            val measureRatio = measureRatio(ctx, 0f, 1f, 1f, 0.12f, 0f)
            holder.binding.cvImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.cvImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            holder.binding.ivBackgroundImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivBackgroundImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivBackgroundImage.scaleType = ImageView.ScaleType.FIT_XY
            Glide.with(ctx).load(listModel[position].imageFile).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)
            Glide.with(ctx).load(R.drawable.ic_image_bg).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivBackgroundImage)
            val sharedzw = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val audioPlayerFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
            val myPlaylist = sharedzw.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
            if (!audioPlayerFlag.equals("Downloadlist", ignoreCase = true) && !audioPlayerFlag.equals("SubPlayList", ignoreCase = true) && !audioPlayerFlag.equals("TopCategories", ignoreCase = true)) {
                if (PlayerAudioId.equals(listModel[position].iD, ignoreCase = true)) {
                    songId = PlayerAudioId
                    if (player != null) {
                        if (!player.playWhenReady) {
                            holder.binding.equalizerview.pause()
                        } else holder.binding.equalizerview.resume(true)
                    } else holder.binding.equalizerview.stop(true)
                    holder.binding.equalizerview.visibility = View.VISIBLE
                    holder.binding.llMainLayout.setBackgroundResource(R.color.highlight_background)
                    holder.binding.ivBackgroundImage.visibility = View.VISIBLE
                } else {
                    holder.binding.equalizerview.visibility = View.GONE
                    holder.binding.llMainLayout.setBackgroundResource(R.color.white)
                    holder.binding.ivBackgroundImage.visibility = View.GONE
                }
            } else {
                holder.binding.equalizerview.visibility = View.GONE
                holder.binding.llMainLayout.setBackgroundResource(R.color.white)
                holder.binding.ivBackgroundImage.visibility = View.GONE
            }
            if (listModel[position].isPlay.equals("0")) {
                holder.binding.ivLock.visibility = View.VISIBLE
            } else {
                holder.binding.ivLock.visibility = View.GONE
            }
            holder.binding.llMainLayoutForPlayer.setOnClickListener {
                 if (listModel[position].isPlay.equals("1")) {
                    callEnhanceActivity(ctx)
                } else if (listModel[position].isPlay.equals("0")) {
                     callMainTransFrag(position)
                 }
            }
            holder.binding.llRemoveAudio.setOnClickListener {
                if (IsLock.equals("1")) {
                    callEnhanceActivity(ctx)
                } else if (IsLock.equals("0")) {
                    val p = Properties()
                    p.putValue("audioId", listModel[position].iD)
                    p.putValue("audioName", listModel[position].name)
                    p.putValue("audioDescription", "")
                    p.putValue("directions", listModel[position].audioDirection)
                    p.putValue("masterCategory", listModel[position].audiomastercat)
                    p.putValue("subCategory", listModel[position].audioSubCategory)
                    p.putValue("audioDuration", listModel[position].audioDuration)
                    p.putValue("position", GlobalInitExoPlayer.GetCurrentAudioPosition())
                    p.putValue("audioType", "Streaming")
                    p.putValue("source", "Add Audio Screen")
                    p.putValue("audioService", appStatus(ctx))
                    p.putValue("bitRate", "")
                    p.putValue("sound", hundredVolume.toString())
                    addToSegment("Add To Playlist Clicked", p, CONSTANTS.track)
                    if (playlistId.equals("", ignoreCase = true)) {
                        val i = Intent(ctx, AddPlaylistActivity::class.java)
                        i.putExtra("AudioId", listModel[position].iD)
                        i.putExtra("ScreenView", "Audio Details Screen")
                        i.putExtra("PlaylistID", "")
                        i.putExtra("PlaylistName", "")
                        i.putExtra("PlaylistImage", "")
                        i.putExtra("PlaylistType", "")
                        i.putExtra("Liked", "0")
                        startActivity(i)
                    } else {
                        if (audioPlayerFlag.equals("playlist", ignoreCase = true) && myPlaylist.equals(playlistId, ignoreCase = true)) {
                            if (isDisclaimer == 1) {
                                showToast("The audio shall add after playing the disclaimer", activity)
                            } else {
                                callAddAudioToPlaylist(listModel[position].iD, "", "0")
                            }
                        } else {
                            callAddAudioToPlaylist(listModel[position].iD, "", "0")
                        }
                    }
                }
            }
        }

        private fun callMainTransFrag(position: Int) {
            try {
                val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val audioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                //                String MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PayerPlaylistId, "");
                val playFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
                val playerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                if (audioPlayerFlag.equals("SearchAudio", ignoreCase = true) && playFrom.equals("Recommended Search", ignoreCase = true)) {
                    if (isDisclaimer == 1) {
                        if (player != null) {
                            if (!player.playWhenReady) {
                                player.playWhenReady = true
                            }
                        } else {
                            audioClick = true
                        }
                        callMyPlayer()
                        showToast("The audio shall start playing after the disclaimer", activity)
                    } else {
                        val listModelList2 = ArrayList<SuggestedModel.ResponseData>()
                        listModelList2.add(listModel!![position])
                        if (player != null) {
                            if (position != playerPosition) {
                                player.seekTo(position, 0)
                                player.playWhenReady = true
                                val sharedxx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                                val editor = sharedxx.edit()
                                editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position)
                                editor.apply()
                            }
                            callMyPlayer()
                        } else {
                            callPlayer(0, listModelList2, true)
                        }
                    }
                } else {
                    val listModelList2 = ArrayList<SuggestedModel.ResponseData>()
                    val gson = Gson()
                    val shared12 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
                    val isPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "0")
                    val disclimerJson = shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
                    val type = object : TypeToken<DisclaimerAudio?>() {}.type
                    val arrayList = gson.fromJson<DisclaimerAudio>(disclimerJson, type)
                    val mainPlayModel = SuggestedModel.ResponseData()
                    mainPlayModel.iD = arrayList.id
                    mainPlayModel.name = arrayList.name
                    mainPlayModel.audioFile = arrayList.audioFile
                    mainPlayModel.audioDirection = arrayList.audioDirection
                    mainPlayModel.audiomastercat = arrayList.audiomastercat
                    mainPlayModel.audioSubCategory = arrayList.audioSubCategory
                    mainPlayModel.imageFile = arrayList.imageFile
                    mainPlayModel.audioDuration = arrayList.audioDuration
                    var audioc = false
                    if (isDisclaimer == 1) {
                        if (player != null) {
                            player.playWhenReady = true
                            audioc = false
                            listModelList2.add(mainPlayModel)
                        } else {
                            isDisclaimer = 0
                            if (isPlayDisclimer.equals("1", ignoreCase = true)) {
                                audioc = true
                                listModelList2.add(mainPlayModel)
                            }
                        }
                    } else {
                        isDisclaimer = 0
                        if (isPlayDisclimer.equals("1", ignoreCase = true)) {
                            audioc = true
                            listModelList2.add(mainPlayModel)
                        }
                    }
                    listModelList2.add(listModel!![position])
                    callPlayer(0, listModelList2, audioc)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun callMyPlayer() {
            val i = Intent(ctx, MyPlayerActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            ctx.startActivity(i)
            activity.overridePendingTransition(0, 0)
        }

        private fun callPlayer(position: Int, listModel: ArrayList<SuggestedModel.ResponseData>, audioc: Boolean) {
            if (audioc) {
                GlobalInitExoPlayer.callNewPlayerRelease()
            }
            val shared = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val editor = shared.edit()
            val gson = Gson()
            val json = gson.toJson(listModel)
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json)
            editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position)
            editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
            editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "")
            editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "Recommended Search")
            editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "SearchAudio")
            editor.apply()
            audioClick = audioc
            callMyPlayer()
        }

        override fun getItemCount(): Int {
            return listModel!!.size
        }

        inner class MyViewHolder(var binding: DownloadsLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }

    /* public class SuggestionPlayListsAdpater extends RecyclerView.Adapter<SuggestionPlayListsAdpater.MyViewHolder> {
        private ArrayList<SearchPlaylistModel.ResponseData> PlaylistModel;

        public SuggestionPlayListsAdpater(ArrayList<SearchPlaylistModel.ResponseData> PlaylistModel) {
            this.PlaylistModel = PlaylistModel;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            DownloadsLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.downloads_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvTitle.setText(PlaylistModel.get(position).getName());
            holder.binding.pbProgress.setVisibility(View.GONE);
            holder.binding.equalizerview.setVisibility(View.GONE);
            if (PlaylistModel.get(position).getTotalAudio().equalsIgnoreCase("") ||
                    PlaylistModel.get(position).getTotalAudio().equalsIgnoreCase("0") &&
                            PlaylistModel.get(position).getTotalhour().equalsIgnoreCase("")
                            && PlaylistModel.get(position).getTotalminute().equalsIgnoreCase("")) {
                holder.binding.tvTime.setText("0 Audio | 0h 0m");
            } else {
                if (PlaylistModel.get(position).getTotalminute().equalsIgnoreCase("")) {
                    holder.binding.tvTime.setText(PlaylistModel.get(position).getTotalAudio() + " Audio | "
                            + PlaylistModel.get(position).getTotalhour() + "h 0m");
                } else {
                    holder.binding.tvTime.setText(PlaylistModel.get(position).getTotalAudio() +
                            " Audios | " + PlaylistModel.get(position).getTotalhour() + "h " + PlaylistModel.get(position).getTotalminute() + "m");
                }
//            }

                MeasureRatio measureRatio = BWSApplication.measureRatio(ctx, 0,
                        1, 1, 0.12f, 0);
                holder.binding.cvImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
                holder.binding.cvImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
                holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
                holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
                holder.binding.ivRestaurantImage.setScaleType(ImageView.ScaleType.FIT_XY);
                holder.binding.ivBackgroundImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
                holder.binding.ivBackgroundImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());
                holder.binding.ivBackgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(ctx).load(PlaylistModel.get(position).getImage()).thumbnail(0.05f)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(28))).priority(Priority.HIGH)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage);
                Glide.with(ctx).load(R.drawable.ic_image_bg).thumbnail(0.05f)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(28))).priority(Priority.HIGH)
                        .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivBackgroundImage);
                holder.binding.ivIcon.setImageResource(R.drawable.ic_add_two_icon);
                holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg);
                holder.binding.ivLock.setVisibility(View.GONE);
//            }

                holder.binding.llMainLayout.setOnClickListener(view -> {
//                if (PlaylistModel.get(position).isLock().equalsIgnoreCase("1")) {
//                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                    holder.binding.ivLock.setVisibility(View.VISIBLE);
//                    Intent i = new Intent(ctx, MembershipChangeActivity.class);
//                    i.putExtra("ComeFrom", "Plan");
//                    startActivity(i);
//                } else if (PlaylistModel.get(position).isLock().equalsIgnoreCase("2")) {
//                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                    holder.binding.ivLock.setVisibility(View.VISIBLE);
//                    BWSApplication.showToast(getString(R.string.reactive_plan), activity);
//                } else if (PlaylistModel.get(position).isLock().equalsIgnoreCase("0")
//                        || PlaylistModel.get(position).isLock().equalsIgnoreCase("")) {
                    comefromDownload = "0";
                    addToSearch = true;
                    MyPlaylistIds = PlaylistModel.get(position).getID();
                    PlaylistIDMS = PlaylistID;
                    finish();
//                }
                });

                holder.binding.llRemoveAudio.setOnClickListener(view -> {
//                if (PlaylistModel.get(position).isLock().equalsIgnoreCase("1")) {
//                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                    holder.binding.ivLock.setVisibility(View.VISIBLE);
//                    Intent i = new Intent(ctx, MembershipChangeActivity.class);
//                    i.putExtra("ComeFrom", "Plan");
//                    startActivity(i);
//                } else if (PlaylistModel.get(position).isLock().equalsIgnoreCase("2")) {
//                    BWSApplication.showToast(getString(R.string.reactive_plan), activity);
//                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                    holder.binding.ivLock.setVisibility(View.VISIBLE);
//                } else if (PlaylistModel.get(position).isLock().equalsIgnoreCase("0") || PlaylistModel.get(position).isLock().equalsIgnoreCase("")) {
                    holder.binding.ivBackgroundImage.setVisibility(View.GONE);
                    holder.binding.ivLock.setVisibility(View.GONE);
                    comefromDownload = "0";
                    SharedPreferences shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE);
                    String AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
                    String MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PayerPlaylistId, "");
                    String PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "");
                    int PlayerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                    if (AudioPlayerFlag.equalsIgnoreCase("playlist") && MyPlaylist.equalsIgnoreCase(PlaylistID)) {
                        if (isDisclaimer == 1) {
                            BWSApplication.showToast("The audio shall add after playing the disclaimer", activity);
                        } else {
                            callAddAudioToPlaylist("", PlaylistModel.get(position).getID(), "1");
                        }
                    } else {
                        callAddAudioToPlaylist("", PlaylistModel.get(position).getID(), "1");
                    }
//            }
                });
            }
        }

        @Override
        public int getItemCount() {
            return PlaylistModel.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            DownloadsLayoutBinding binding;

            public MyViewHolder(DownloadsLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }*/
    internal inner class AppLifecycleCallback : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        override fun onActivityStarted(activity: Activity) {
            if (numStarted == 0) {
                stackStatus = 1
                Log.e("APPLICATION", "APP IN FOREGROUND")
                //app went to foreground
            }
            numStarted++
        }

        override fun onActivityResumed(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {
            numStarted--
            if (numStarted == 0) {
                if (!myBackPress) {
                    Log.e("APPLICATION", "Back press false")
                    stackStatus = 2
                } else {
                    myBackPress = true
                    stackStatus = 1
                    Log.e("APPLICATION", "back press true ")
                }
                Log.e("APPLICATION", "App is in BACKGROUND")
                // app went to background
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {
            if (numStarted == 0 && stackStatus == 2) {
                Log.e("Destroy", "Activity Destoryed")
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(notificationId)
                GlobalInitExoPlayer.relesePlayer(applicationContext)
            } else {
                Log.e("Destroy", "Activity go in main activity")
            }
        }
    }
}