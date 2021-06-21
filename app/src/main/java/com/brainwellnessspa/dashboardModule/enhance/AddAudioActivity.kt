package com.brainwellnessspa.dashboardModule.enhance

import android.annotation.SuppressLint
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
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.activities.MyPlayerActivity
import com.brainwellnessspa.dashboardModule.models.*
import com.brainwellnessspa.dashboardModule.models.HomeScreenModel.ResponseData.DisclaimerAudio
import com.brainwellnessspa.dashboardOldModule.activities.DashboardActivity
import com.brainwellnessspa.dashboardOldModule.transParentPlayer.Fragments.MiniPlayerFragment
import com.brainwellnessspa.dashboardOldModule.transParentPlayer.Models.MainPlayModel
import com.brainwellnessspa.databinding.ActivityAddAudioBinding
import com.brainwellnessspa.databinding.DownloadsLayoutBinding
import com.brainwellnessspa.databinding.GlobalSearchLayoutBinding
import com.brainwellnessspa.downloadModule.fragments.AudioDownloadsFragment
import com.brainwellnessspa.services.GlobalInitExoPlayer
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
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

class AddAudioActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddAudioBinding
    lateinit var ctx: Context
    var coUserId: String? = null
    var userId: String? = null
    var userName: String? = null
    var playlistId: String? = ""
    var IsPlayDisclimer: String? = null
    var serachListAdpater: SerachListAdpater? = null
    lateinit var searchEditText: EditText
    lateinit var activity: Activity
    var listSize = 0
    var suggestedAdpater: SuggestedAdpater? = null

    //    private Runnable UpdateSongTime3;
    private val listener: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("MyData")) {
                val data = intent.getStringExtra("MyData")
                Log.d("play_pause_Action", data!!)
                val sharedzw = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val audioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                if (!audioFlag.equals("Downloadlist", ignoreCase = true) &&
                    !audioFlag.equals("playlist", ignoreCase = true) &&
                    !audioFlag.equals("TopCategories", ignoreCase = true)
                ) {
                    if (GlobalInitExoPlayer.player != null) {
                        if (listSize != 0) {
                            serachListAdpater!!.notifyDataSetChanged()
                        }
                        suggestedAdpater!!.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    //    Handler handler3;
    var p: Properties? = null
    var stackStatus = 0
    var myBackPress = false
    var notificationStatus = false
    var gsonBuilder: GsonBuilder? = null
    var section: ArrayList<String?>? = null
    private var numStarted = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_audio)
        ctx = this@AddAudioActivity
        activity = this@AddAudioActivity
        if (intent.extras != null) {
            playlistId = intent.getStringExtra(CONSTANTS.PlaylistID)
        }
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        userName = shared1.getString(CONSTANTS.PREFE_ACCESS_NAME, "")
        notificationStatus = false
        p = Properties()
        p!!.putValue("userId", userId)
        p!!.putValue("coUserId", coUserId)
        if (playlistId.equals("", ignoreCase = true)) {
            p!!.putValue("source", "Manage Search Screen")
        } else {
            p!!.putValue("source", "Add Audio Screen")
        }
        BWSApplication.addToSegment("Search Screen Viewed", p, CONSTANTS.screen)
        binding.searchView.onActionViewExpanded()
        searchEditText = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(ContextCompat.getColor(activity, R.color.dark_blue_gray))
        searchEditText.setHintTextColor(ContextCompat.getColor(activity, R.color.gray))
        val closeButton = binding.searchView.findViewById<ImageView>(R.id.search_close_btn)
        binding.searchView.clearFocus()
        closeButton.setOnClickListener {
            binding.searchView.clearFocus()
            searchEditText.setText("")
            binding.rvSerachList.adapter = null
            binding.rvSerachList.visibility = View.GONE
            binding.llError.visibility = View.GONE
            binding.searchView.setQuery("", false)
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(search: String): Boolean {
                binding.searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(search: String): Boolean {
                if (searchEditText.text.toString().equals("", ignoreCase = true)) {
                } else {
                    prepareSearchData(search, searchEditText)
                }
                p = Properties()
                p!!.putValue("userId", userId)
                p!!.putValue("coUserId", coUserId)
                if (playlistId.equals("", ignoreCase = true)) {
                    p!!.putValue("source", "Manage Search Screen")
                } else {
                    p!!.putValue("source", "Add Audio Screen")
                }
                p!!.putValue("searchKeyword", search)
                BWSApplication.addToSegment("Audio Searched", p, CONSTANTS.track)
                return false
            }
        })
        binding.llBack.setOnClickListener { callback() }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(AppLifecycleCallback())
        }
        val manager: RecyclerView.LayoutManager =
            LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        binding.rvSuggestedList.layoutManager = manager
        binding.rvSuggestedList.itemAnimator = DefaultItemAnimator()
        val layoutSerach: RecyclerView.LayoutManager =
            LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        binding.rvSerachList.layoutManager = layoutSerach
        binding.rvSerachList.itemAnimator = DefaultItemAnimator()
        val layoutPlay: RecyclerView.LayoutManager =
            LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        binding.rvPlayList.layoutManager = layoutPlay
        binding.rvPlayList.itemAnimator = DefaultItemAnimator()
    }

    override fun onResume() {
        prepareSuggestedData()
        super.onResume()
    }

    public override fun onPause() {
        LocalBroadcastManager.getInstance(this@AddAudioActivity).unregisterReceiver(listener)
        super.onPause()
    }

    private fun callback() {
        myBackPress = true
        AudioDownloadsFragment.comefromDownload = "0"
        finish()
    }

    private fun prepareSearchData(search: String, searchEditText: EditText?) {
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
            BWSApplication.PlayerAudioId = mainPlayModelList[playerPositionx].id
        }
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(
                binding.progressBar,
                binding.progressBarHolder,
                activity
            )
            val listCall = APINewClient.getClient().getSearchBoth(coUserId, search)
            listCall.enqueue(object : Callback<SearchBothModel?> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<SearchBothModel?>,
                    response: Response<SearchBothModel?>
                ) {
                    try {
                        val listModel = response.body()
                        if (listModel!!.responseCode.equals(
                                getString(R.string.ResponseCodesuccess),
                                ignoreCase = true
                            )
                        ) {
                            BWSApplication.hideProgressBar(
                                binding.progressBar,
                                binding.progressBarHolder,
                                activity
                            )
                            if (!searchEditText!!.text.toString().equals("", ignoreCase = true)) {
                                if (listModel.responseData!!.isEmpty()) {
                                    binding.rvSerachList.visibility = View.GONE
                                    binding.llError.visibility = View.VISIBLE
                                    binding.tvFound.text =
                                        "Please use another term and try searching again"
                                    //                                    binding.tvFound.setText("Couldn't find '" + search + "'. Try searching again");
                                } else {
                                    binding.llError.visibility = View.GONE
                                    binding.rvSerachList.visibility = View.VISIBLE
                                    serachListAdpater = SerachListAdpater(
                                        listModel.responseData,
                                        activity,
                                        binding.rvSerachList,
                                        coUserId
                                    )
                                    binding.rvSerachList.adapter = serachListAdpater
                                    LocalBroadcastManager.getInstance(ctx)
                                        .registerReceiver(
                                            listener,
                                            IntentFilter("play_pause_Action")
                                        )
                                }
                            } else if (searchEditText.text.toString()
                                    .equals("", ignoreCase = true)
                            ) {
                                binding.rvSerachList.adapter = null
                                binding.rvSerachList.visibility = View.GONE
                                binding.llError.visibility = View.GONE
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<SearchBothModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(
                        binding.progressBar,
                        binding.progressBarHolder,
                        activity
                    )
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }

    private fun prepareSuggestedData() {
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
            BWSApplication.PlayerAudioId = mainPlayModelList[playerPositionx].id
        }
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(
                binding.progressBar,
                binding.progressBarHolder,
                activity
            )
            val listCall = APINewClient.getClient().getSuggestedLists(coUserId)
            listCall.enqueue(object : Callback<SuggestedModel?> {
                override fun onResponse(
                    call: Call<SuggestedModel?>,
                    response: Response<SuggestedModel?>
                ) {
                    try {
                        if (response.isSuccessful) {
                            BWSApplication.hideProgressBar(
                                binding.progressBar,
                                binding.progressBarHolder,
                                activity
                            )
                            val listModel = response.body()
                            binding.tvSuggestedAudios.setText(R.string.Suggested_Audios)
                            binding.tvSAViewAll.visibility = View.VISIBLE
                            suggestedAdpater = SuggestedAdpater(listModel!!.responseData, ctx)
                            binding.rvSuggestedList.adapter = suggestedAdpater
                            p = Properties()
                            p!!.putValue("userId", userId)
                            p!!.putValue("coUserId", coUserId)
                            if (playlistId.equals("", ignoreCase = true)) {
                                p!!.putValue("source", "Manage Search Screen")
                            } else {
                                p!!.putValue("source", "Add Audio Screen")
                            }
                            section = ArrayList()
                            gsonBuilder = GsonBuilder()
                            val gson = gsonBuilder!!.create()
                            for (i in listModel.responseData!!.indices) {
                                section!!.add(listModel.responseData!![i]!!.iD)
                                section!!.add(listModel.responseData!![i]!!.name)
                                section!!.add(listModel.responseData!![i]!!.audiomastercat)
                                section!!.add(listModel.responseData!![i]!!.audioSubCategory)
                                section!!.add(listModel.responseData!![i]!!.audioDuration)
                            }
                            p!!.putValue("audios", gson.toJson(section))
                            BWSApplication.addToSegment(
                                "Suggested Audios List Viewed",
                                p,
                                CONSTANTS.screen
                            )
                            LocalBroadcastManager.getInstance(ctx)
                                .registerReceiver(listener, IntentFilter("play_pause_Action"))
                            binding.tvSAViewAll.setOnClickListener {
                                notificationStatus = true
                                val i = Intent(ctx, ViewSuggestedActivity::class.java)
                                i.putExtra("Name", "Suggested Audios")
                                i.putExtra("PlaylistID", playlistId)
                                i.putParcelableArrayListExtra(
                                    "AudiolistModel",
                                    listModel.responseData
                                )
                                startActivity(i)
                                finish()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<SuggestedModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(
                        binding.progressBar,
                        binding.progressBarHolder,
                        activity
                    )
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
        binding.tvSuggestedPlaylist.visibility = View.GONE
        binding.rvPlayList.visibility = View.GONE
        binding.tvSPViewAll.visibility = View.GONE
        /*  if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity);
            Call<SearchPlaylistModel> listCall = APINewClient.getClient().getSuggestedPlayLists(CoUSERID);
            listCall.enqueue(new Callback<SearchPlaylistModel>() {
                @Override
                public void onResponse(Call<SearchPlaylistModel> call, Response<SearchPlaylistModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                            SearchPlaylistModel listModel = response.body();
                            binding.tvSuggestedPlaylist.setText(R.string.Suggested_Playlist);

                            p = new Properties();
                            p.putValue("userId", UserID);
                            p.putValue("source", "Add Audio Screen");
                            BWSApplication.addToSegment("Recommended Playlists List Viewed", p, CONSTANTS.screen);

                            SuggestedPlayListsAdpater suggestedAdpater = new SuggestedPlayListsAdpater(listModel.getResponseData());
                            binding.rvPlayList.setAdapter(suggestedAdpater);

                            binding.tvSPViewAll.setOnClickListener(view -> {
                                notificationStatus = true;
                                Intent i = new Intent(ctx, ViewSuggestedActivity.class);
                                i.putExtra("Name", "Suggested Playlist");
                                i.putExtra("PlaylistID", PlaylistID);
                                i.putParcelableArrayListExtra("PlaylistModel", listModel.getResponseData());
                                startActivity(i);
                                finish();
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<SearchPlaylistModel> call, Throwable t) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity);
                }
            });
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity);
        }*/
    }

    override fun onBackPressed() {
        callback()
    }

    private fun callAddSearchAudio(AudioID: String?, s: String, FromPlaylistId: String?) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(
                binding.progressBar,
                binding.progressBarHolder,
                activity
            )
            val listCall = APINewClient.getClient()
                .getAddSearchAudioFromPlaylist(coUserId, AudioID, playlistId, FromPlaylistId)
            listCall.enqueue(object : Callback<AddToPlaylistModel?> {
                override fun onResponse(
                    call: Call<AddToPlaylistModel?>,
                    response: Response<AddToPlaylistModel?>
                ) {
                    try {
                        if (response.isSuccessful) {
                            BWSApplication.hideProgressBar(
                                binding.progressBar,
                                binding.progressBarHolder,
                                activity
                            )
                            val listModels = response.body()
                            if (listModels!!.responseCode.equals(
                                    getString(R.string.ResponseCodesuccess),
                                    ignoreCase = true
                                )
                            ) {
                                BWSApplication.showToast(listModels.responseMessage, activity)
                                val shared1 = ctx.getSharedPreferences(
                                    CONSTANTS.PREF_KEY_PLAYER,
                                    MODE_PRIVATE
                                )
                                val audioPlayerFlag =
                                    shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                                val myPlaylist =
                                    shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                                val myPlaylistName =
                                    shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "")
                                val playFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
                                var playerPosition =
                                    shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                                if (audioPlayerFlag.equals(
                                        "playlist",
                                        ignoreCase = true
                                    ) && myPlaylist.equals(playlistId, ignoreCase = true)
                                ) {
                                    val gsonx = Gson()
                                    val json = shared1.getString(
                                        CONSTANTS.PREF_KEY_PlayerAudioList,
                                        gsonx.toString()
                                    )
                                    val type =
                                        object : TypeToken<ArrayList<MainPlayModel?>?>() {}.type
                                    val mainPlayModelListold: ArrayList<MainPlayModel> =
                                        gsonx.fromJson(json, type)
                                    val id = mainPlayModelListold[playerPosition].id
                                    val mainPlayModelList = ArrayList<MainPlayModel>()
                                    val playlistSongs =
                                        ArrayList<SubPlayListModel.ResponseData.PlaylistSong>()
                                    val size = mainPlayModelListold.size
                                    for (i in listModels.responseData!!.indices) {
                                        val mainPlayModel = MainPlayModel()
                                        mainPlayModel.id = listModels.responseData!![i].iD
                                        mainPlayModel.name = listModels.responseData!![i].name
                                        mainPlayModel.audioFile =
                                            listModels.responseData!![i].audioFile
                                        mainPlayModel.playlistID =
                                            listModels.responseData!![i].playlistID
                                        mainPlayModel.audioDirection =
                                            listModels.responseData!![i].audioDirection
                                        mainPlayModel.audiomastercat =
                                            listModels.responseData!![i].audiomastercat
                                        mainPlayModel.audioSubCategory =
                                            listModels.responseData!![i].audioSubCategory
                                        mainPlayModel.imageFile =
                                            listModels.responseData!![i].imageFile
                                        mainPlayModel.audioDuration =
                                            listModels.responseData!![i].audioDuration
                                        mainPlayModelList.add(mainPlayModel)
                                    }
                                    for (i in listModels.responseData!!.indices) {
                                        val mainPlayModel =
                                            SubPlayListModel.ResponseData.PlaylistSong()
                                        mainPlayModel.id = listModels.responseData!![i].iD
                                        mainPlayModel.name = listModels.responseData!![i].name
                                        mainPlayModel.audioFile =
                                            listModels.responseData!![i].audioFile
                                        mainPlayModel.playlistID =
                                            listModels.responseData!![i].playlistID
                                        mainPlayModel.audioDirection =
                                            listModels.responseData!![i].audioDirection
                                        mainPlayModel.audiomastercat =
                                            listModels.responseData!![i].audiomastercat
                                        mainPlayModel.audioSubCategory =
                                            listModels.responseData!![i].audioSubCategory
                                        mainPlayModel.imageFile =
                                            listModels.responseData!![i].imageFile
                                        mainPlayModel.like = listModels.responseData!![i].like
                                        mainPlayModel.download =
                                            listModels.responseData!![i].download
                                        mainPlayModel.audioDuration =
                                            listModels.responseData!![i].audioDuration
                                        playlistSongs.add(mainPlayModel)
                                    }
                                    for (i in mainPlayModelList.indices) {
                                        if (mainPlayModelList[i].id.equals(id, ignoreCase = true)) {
                                            playerPosition = i
                                            break
                                        }
                                    }
                                    val sharedd = ctx.getSharedPreferences(
                                        CONSTANTS.PREF_KEY_PLAYER,
                                        MODE_PRIVATE
                                    )
                                    val editor = sharedd.edit()
                                    val gson = Gson()
                                    val jsonx = gson.toJson(mainPlayModelList)
                                    val json11 = gson.toJson(playlistSongs)
                                    editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json11)
                                    editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonx)
                                    editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, playerPosition)
                                    editor.putString(
                                        CONSTANTS.PREF_KEY_PlayerPlaylistId,
                                        playlistId
                                    )
                                    editor.putString(
                                        CONSTANTS.PREF_KEY_PlayerPlaylistName,
                                        myPlaylistName
                                    )
                                    editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "Created")
                                    editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "playlist")
                                    editor.apply()
                                    if (mainPlayModelList[playerPosition].audioFile != "") {
                                        val downloadAudioDetailsList: List<String> = ArrayList()
                                        val ge = GlobalInitExoPlayer()
                                        ge.AddAudioToPlayer(
                                            size,
                                            mainPlayModelList,
                                            downloadAudioDetailsList,
                                            ctx
                                        )
                                    }
                                }
                                if (s.equals("1", ignoreCase = true)) {
                                    finish()
                                }
                            } else if (listModels.responseCode.equals(
                                    getString(R.string.ResponseCodefail),
                                    ignoreCase = true
                                )
                            ) {
                                BWSApplication.showToast(listModels.responseMessage, activity)
                            }
                        }
                    } catch (e: Exception) {
                        BWSApplication.hideProgressBar(
                            binding.progressBar,
                            binding.progressBarHolder,
                            activity
                        )
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<AddToPlaylistModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(
                        binding.progressBar,
                        binding.progressBarHolder,
                        activity
                    )
                }
            })
        } else {
            BWSApplication.showToast(ctx.getString(R.string.no_server_found), activity)
        }
    }

    inner class SerachListAdpater(
        private val modelList: List<SearchBothModel.ResponseData>?, var ctx: Context?,
        var rvSerachList: RecyclerView, var UserID: String?
    ) : RecyclerView.Adapter<SerachListAdpater.MyViewHolder>() {
        var songId: String? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: GlobalSearchLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.global_search_layout, parent, false
            )
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = modelList!![position].name
            if (modelList[position].iscategory.equals("1", ignoreCase = true)) {
                holder.binding.tvPart.text = modelList[position].audioDuration
                holder.binding.llRemoveAudio.visibility = View.VISIBLE
                holder.binding.ivLock.visibility = View.GONE
                val sharedzw = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val audioPlayerFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                val myPlaylist = sharedzw.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                val playFrom = sharedzw.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
                val playerPosition = sharedzw.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                if (!audioPlayerFlag.equals("Downloadlist", ignoreCase = true) &&
                    !audioPlayerFlag.equals(
                        "SubPlayList",
                        ignoreCase = true
                    ) && !audioPlayerFlag.equals("TopCategories", ignoreCase = true)
                ) {
                    if (BWSApplication.PlayerAudioId.equals(
                            modelList[position].iD,
                            ignoreCase = true
                        )
                    ) {
                        songId = BWSApplication.PlayerAudioId
                        if (GlobalInitExoPlayer.player != null) {
                            if (!GlobalInitExoPlayer.player.playWhenReady) {
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
                holder.binding.llMainLayoutForPlayer.setOnClickListener {
//                    if (modelList.get(position).isLock().equalsIgnoreCase("1")) {
//                        if (modelList.get(position).isPlay().equalsIgnoreCase("1")) {
//                            callMainTransFrag(position);
//                        } else if (modelList.get(position).isPlay().equalsIgnoreCase("0")
//                                || modelList.get(position).isPlay().equalsIgnoreCase("")) {
//                            Intent i = new Intent(ctx, MembershipChangeActivity.class);
//                            i.putExtra("ComeFrom", "Plan");
//                            startActivity(i);
//                        }
//                    } else if (modelList.get(position).isLock().equalsIgnoreCase("2")) {
//                        if (modelList.get(position).isPlay().equalsIgnoreCase("1")) {
//                            callMainTransFrag(position);
//                        } else if (modelList.get(position).isPlay().equalsIgnoreCase("0")
//                                || modelList.get(position).isPlay().equalsIgnoreCase("")) {
//                            BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
//                        }
//                    } else if (modelList.get(position).isLock().equalsIgnoreCase("0") || modelList.get(position).isLock().equalsIgnoreCase("")) {
                    callMainTransFrag(position)
                }
                holder.binding.llRemoveAudio.setOnClickListener {
//                    if (modelList.get(position).isLock().equalsIgnoreCase("1")) {
//                        Intent i = new Intent(ctx, MembershipChangeActivity.class);
//                        i.putExtra("ComeFrom", "Plan");
//                        startActivity(i);
//                    } else if (modelList.get(position).isLock().equalsIgnoreCase("2")) {
//                        BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
//                    } else if (modelList.get(position).isLock().equalsIgnoreCase("0") || modelList.get(position).isLock().equalsIgnoreCase("")) {
                    val audioID = modelList[position].iD
                    if (playlistId.equals("", ignoreCase = true)) {
                        val i = Intent(ctx, AddPlaylistActivity::class.java)
                        i.putExtra("AudioId", audioID)
                        i.putExtra("ScreenView", "Audio Details Screen")
                        i.putExtra("PlaylistID", "")
                        i.putExtra("PlaylistName", "")
                        i.putExtra("PlaylistImage", "")
                        i.putExtra("PlaylistType", "")
                        i.putExtra("Liked", "0")
                        startActivity(i)
                    } else {
                        if (audioPlayerFlag.equals(
                                "playList",
                                ignoreCase = true
                            ) && myPlaylist.equals(playlistId, ignoreCase = true)
                        ) {
                            if (MiniPlayerFragment.isDisclaimer == 1) {
                                BWSApplication.showToast(
                                    "The audio shall add after playing the disclaimer",
                                    activity
                                )
                            } else {
                                callAddSearchAudio(audioID, "0", "")
                            }
                        } else {
                            callAddSearchAudio(audioID, "0", "")
                        }
                    }
                }
            } else if (modelList[position].iscategory.equals("0", ignoreCase = true)) {
                holder.binding.tvPart.setText(R.string.Playlist)
                holder.binding.tvPart.visibility = View.GONE
                holder.binding.equalizerview.visibility = View.GONE
                holder.binding.llRemoveAudio.visibility = View.GONE
                holder.binding.llMainLayout.visibility = View.GONE
                //                if (modelList.get(position).isLock().equalsIgnoreCase("1")) {
//                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                    holder.binding.ivLock.setVisibility(View.VISIBLE);
//                } else if (modelList.get(position).isLock().equalsIgnoreCase("2")) {
//                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                    holder.binding.ivLock.setVisibility(View.VISIBLE);
//                } else if (modelList.get(position).isLock().equalsIgnoreCase("0") || modelList.get(position).isLock().equalsIgnoreCase("")) {
                holder.binding.ivBackgroundImage.visibility = View.GONE
                holder.binding.ivLock.visibility = View.GONE
                //                }
                holder.binding.llMainLayout.setOnClickListener {
//                    if (modelList.get(position).isLock().equalsIgnoreCase("1")) {
//                        Intent i = new Intent(ctx, MembershipChangeActivity.class);
//                        i.putExtra("ComeFrom", "Plan");
//                        startActivity(i);
//                    } else if (modelList.get(position).isLock().equalsIgnoreCase("1")) {
//                        BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
//                    } else if (modelList.get(position).isLock().equalsIgnoreCase("0") || modelList.get(position).isLock().equalsIgnoreCase("")) {
                    AudioDownloadsFragment.comefromDownload = "0"
                    addToSearch = true
                    MyPlaylistIds = modelList[position].iD
                    PlaylistIDMS = playlistId
                    finish()
                }
                holder.binding.llRemoveAudio.setOnClickListener {
//                    if (modelList.get(position).isLock().equalsIgnoreCase("1")) {
//                        Intent i = new Intent(ctx, MembershipChangeActivity.class);
//                        i.putExtra("ComeFrom", "Plan");
//                        startActivity(i);
//                    } else if (modelList.get(position).isLock().equalsIgnoreCase("2")) {
//                        BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
//                    } else if (modelList.get(position).isLock().equalsIgnoreCase("0") || modelList.get(position).isLock().equalsIgnoreCase("")) {
                    val shared1 =
                        ctx!!.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                    val audioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                    val myPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                    val playFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
                    val playerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                    if (audioPlayerFlag.equals("playList", ignoreCase = true) && myPlaylist.equals(
                            playlistId,
                            ignoreCase = true
                        )
                    ) {
                        if (MiniPlayerFragment.isDisclaimer == 1) {
                            BWSApplication.showToast(
                                "The audio shall add after playing the disclaimer",
                                activity
                            )
                        } else {
                            callAddSearchAudio("", "1", modelList[position].iD)
                        }
                    } else {
                        callAddSearchAudio("", "1", modelList[position].iD)
                    }
                }
            }
            val measureRatio = BWSApplication.measureRatio(ctx, 0f, 1f, 1f, 0.12f, 0f)
            holder.binding.cvImage.layoutParams.height =
                (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.cvImage.layoutParams.width =
                (measureRatio.widthImg * measureRatio.ratio).toInt()
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
            Glide.with(ctx!!).load(modelList[position].imageFile).thumbnail(0.05f)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                .into(holder.binding.ivRestaurantImage)
            Glide.with(ctx!!).load(R.drawable.ic_image_bg).thumbnail(0.05f)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                .into(holder.binding.ivBackgroundImage)
            holder.binding.ivIcon.setImageResource(R.drawable.ic_add_two_icon)
        }

        private fun callMainTransFrag(position: Int) {
            try {
                val shared2 = ctx!!.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
                IsPlayDisclimer = shared2.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1")
                val shared1 = ctx!!.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val audioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                val myPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                val playFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
                val playerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                if (audioPlayerFlag.equals("SearchModelAudio", ignoreCase = true)
                    && playFrom.equals("Search Audio", ignoreCase = true)
                ) {
                    if (MiniPlayerFragment.isDisclaimer == 1) {
                        if (GlobalInitExoPlayer.player != null) {
                            if (!GlobalInitExoPlayer.player.playWhenReady) {
                                GlobalInitExoPlayer.player.playWhenReady = true
                            }
                        } else {
                            DashboardActivity.audioClick = true
                        }
                        callMyPlayer()
                        BWSApplication.showToast(
                            "The audio shall start playing after the disclaimer",
                            activity
                        )
                    } else {
                        val listModelList2 = ArrayList<SearchBothModel.ResponseData>()
                        listModelList2.add(modelList!![position])
                        callPlayer(0, listModelList2, true)
                    }
                } else {
                    val listModelList2 = ArrayList<SearchBothModel.ResponseData>()
                    val gson = Gson()
                    val shared12 =
                        ctx!!.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
                    val IsPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1")
                    val disclimerJson =
                        shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
                    val type = object : TypeToken<DisclaimerAudio?>() {}.type
                    val arrayList = gson.fromJson<DisclaimerAudio>(disclimerJson, type)
                    val mainPlayModel = SearchBothModel.ResponseData()
                    mainPlayModel.iD = arrayList.id
                    mainPlayModel.name = arrayList.name
                    mainPlayModel.audioFile = arrayList.audioFile
                    mainPlayModel.audioDirection = arrayList.audioDirection
                    mainPlayModel.audiomastercat = arrayList.audiomastercat
                    mainPlayModel.audioSubCategory = arrayList.audioSubCategory
                    mainPlayModel.imageFile = arrayList.imageFile
                    mainPlayModel.audioDuration = arrayList.audioDuration
                    var audioc = false
                    if (MiniPlayerFragment.isDisclaimer == 1) {
                        if (GlobalInitExoPlayer.player != null) {
                            GlobalInitExoPlayer.player.playWhenReady = true
                            audioc = false
                            listModelList2.add(mainPlayModel)
                        } else {
                            MiniPlayerFragment.isDisclaimer = 0
                            if (IsPlayDisclimer.equals("1", ignoreCase = true)) {
                                audioc = true
                                listModelList2.add(mainPlayModel)
                            }
                        }
                    } else {
                        MiniPlayerFragment.isDisclaimer = 0
                        if (IsPlayDisclimer.equals("1", ignoreCase = true)) {
                            audioc = true
                            listModelList2.add(mainPlayModel)
                        }
                    }
                    listModelList2.add(modelList!![position])
                    callPlayer(0, listModelList2, audioc)
                }
                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                params.setMargins(0, 8, 0, 210)
                binding.llSpace.layoutParams = params
                notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun callMyPlayer() {
            val i = Intent(ctx, MyPlayerActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            ctx!!.startActivity(i)
            activity.overridePendingTransition(0, 0)
        }

        private fun callPlayer(
            position: Int,
            listModel: ArrayList<SearchBothModel.ResponseData>,
            audioc: Boolean
        ) {
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
            editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "Search Audio")
            editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "SearchModelAudio")
            editor.apply()
            DashboardActivity.audioClick = audioc
            callMyPlayer()
        }

        override fun getItemCount(): Int {
            listSize = modelList!!.size
            return modelList.size
        }

        inner class MyViewHolder(var binding: GlobalSearchLayoutBinding) : RecyclerView.ViewHolder(
            binding.root
        )
    }

    inner class SuggestedAdpater(
        private val listModel: List<SuggestedModel.ResponseData?>?,
        var ctx: Context?
    ) : RecyclerView.Adapter<SuggestedAdpater.MyViewHolder>() {
        var songId: String? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: DownloadsLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.downloads_layout, parent, false
            )
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = listModel!![position]!!.name
            holder.binding.tvTime.text = listModel[position]!!.audioDuration
            val measureRatio = BWSApplication.measureRatio(ctx, 0f, 1f, 1f, 0.12f, 0f)
            holder.binding.cvImage.layoutParams.height =
                (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.cvImage.layoutParams.width =
                (measureRatio.widthImg * measureRatio.ratio).toInt()
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
            Glide.with(ctx!!).load(listModel[position]!!.imageFile).thumbnail(0.05f)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                .into(holder.binding.ivRestaurantImage)
            Glide.with(ctx!!).load(R.drawable.ic_image_bg).thumbnail(0.05f)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                .into(holder.binding.ivBackgroundImage)
            holder.binding.ivIcon.setImageResource(R.drawable.ic_add_two_icon)
            val sharedzw = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val audioPlayerFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
            val myPlaylist = sharedzw.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
            val playFrom = sharedzw.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
            val playerPosition = sharedzw.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
            if (!audioPlayerFlag.equals("Downloadlist", ignoreCase = true) &&
                !audioPlayerFlag.equals(
                    "SubPlayList",
                    ignoreCase = true
                ) && !audioPlayerFlag.equals("TopCategories", ignoreCase = true)
            ) {
                if (BWSApplication.PlayerAudioId.equals(
                        listModel[position]!!.iD,
                        ignoreCase = true
                    )
                ) {
                    songId = BWSApplication.PlayerAudioId
                    if (GlobalInitExoPlayer.player != null) {
                        if (!GlobalInitExoPlayer.player.playWhenReady) {
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

//
//            if (listModel.get(position).isLock().equalsIgnoreCase("1")) {
//                if (listModel.get(position).isPlay().equalsIgnoreCase("1")) {
//                    holder.binding.ivLock.setVisibility(View.GONE);
//                } else if (listModel.get(position).isPlay().equalsIgnoreCase("0")
//                        || listModel.get(position).isPlay().equalsIgnoreCase("")) {
//                    holder.binding.ivLock.setVisibility(View.VISIBLE);
//                }
//            } else if (listModel.get(position).isLock().equalsIgnoreCase("2")) {
//                if (listModel.get(position).isPlay().equalsIgnoreCase("1")) {
//                    holder.binding.ivLock.setVisibility(View.GONE);
//                } else if (listModel.get(position).isPlay().equalsIgnoreCase("0")
//                        || listModel.get(position).isPlay().equalsIgnoreCase("")) {
//                    holder.binding.ivLock.setVisibility(View.VISIBLE);
//                }
//            } else if (listModel.get(position).isLock().equalsIgnoreCase("0")
//                    || listModel.get(position).isLock().equalsIgnoreCase("")) {
            holder.binding.ivLock.visibility = View.GONE
            //            }
            holder.binding.llMainLayoutForPlayer.setOnClickListener {
//                if (listModel.get(position).isLock().equalsIgnoreCase("1")) {
//                    if (listModel.get(position).isPlay().equalsIgnoreCase("1")) {
//                        callMainTransFrag(position);
//                    } else if (listModel.get(position).isPlay().equalsIgnoreCase("0")
//                            || listModel.get(position).isPlay().equalsIgnoreCase("")) {
//                        Intent i = new Intent(ctx, MembershipChangeActivity.class);
//                        i.putExtra("ComeFrom", "Plan");
//                        startActivity(i);
//                    }
//                } else if (listModel.get(position).isLock().equalsIgnoreCase("2")) {
//                    if (listModel.get(position).isPlay().equalsIgnoreCase("1")) {
//                        callMainTransFrag(position);
//                    } else if (listModel.get(position).isPlay().equalsIgnoreCase("0")
//                            || listModel.get(position).isPlay().equalsIgnoreCase("")) {
//                        BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
//                    }
//                } else if (listModel.get(position).isLock().equalsIgnoreCase("0")
//                        || listModel.get(position).isLock().equalsIgnoreCase("")) {
                callMainTransFrag(position)
            }
            holder.binding.llRemoveAudio.setOnClickListener {
//                if (listModel.get(position).isLock().equalsIgnoreCase("1")) {
//                    Intent i = new Intent(ctx, MembershipChangeActivity.class);
//                    i.putExtra("ComeFrom", "Plan");
//                    startActivity(i);
//                } else if (listModel.get(position).isLock().equalsIgnoreCase("2")) {
//                    BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
//                } else if (listModel.get(position).isLock().equalsIgnoreCase("0") || listModel.get(position).isLock().equalsIgnoreCase("")) {
                if (playlistId.equals("", ignoreCase = true)) {
                    val i = Intent(ctx, AddPlaylistActivity::class.java)
                    i.putExtra("AudioId", listModel[position]!!.iD)
                    i.putExtra("ScreenView", "Audio Details Screen")
                    i.putExtra("PlaylistID", "")
                    i.putExtra("PlaylistName", "")
                    i.putExtra("PlaylistImage", "")
                    i.putExtra("PlaylistType", "")
                    i.putExtra("Liked", "0")
                    startActivity(i)
                } else {
                    if (audioPlayerFlag.equals("playlist", ignoreCase = true) && myPlaylist.equals(
                            playlistId,
                            ignoreCase = true
                        )
                    ) {
                        if (MiniPlayerFragment.isDisclaimer == 1) {
                            BWSApplication.showToast(
                                "The audio shall add after playing the disclaimer",
                                activity
                            )
                        } else {
                            callAddSearchAudio(listModel[position]!!.iD, "0", "")
                        }
                    } else {
                        callAddSearchAudio(listModel[position]!!.iD, "0", "")
                    }
                }
            }
        }

        private fun callMainTransFrag(position: Int) {
            try {
                val shared2 = ctx!!.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
                IsPlayDisclimer = shared2.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1")
                val shared1 = ctx!!.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val audioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                val playFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
                val playerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                if (audioPlayerFlag.equals("SearchAudio", ignoreCase = true)
                    && playFrom.equals("Recommended Search", ignoreCase = true)
                ) {
                    if (MiniPlayerFragment.isDisclaimer == 1) {
                        if (GlobalInitExoPlayer.player != null) {
                            if (!GlobalInitExoPlayer.player.playWhenReady) {
                                GlobalInitExoPlayer.player.playWhenReady = true
                            }
                        } else {
                            DashboardActivity.audioClick = true
                        }
                        callMyPlayer()
                        BWSApplication.showToast(
                            "The audio shall start playing after the disclaimer",
                            activity
                        )
                    } else {
                        val listModelList2 = ArrayList<SuggestedModel.ResponseData?>()
                        listModelList2.add(listModel!![position])
                        callPlayer(0, listModelList2, true)
                    }
                } else {
                    val listModelList2 = ArrayList<SuggestedModel.ResponseData?>()
                    val gson = Gson()
                    val shared12 =
                        ctx!!.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
                    val IsPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "1")
                    val disclimerJson =
                        shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
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
                    if (MiniPlayerFragment.isDisclaimer == 1) {
                        if (GlobalInitExoPlayer.player != null) {
                            GlobalInitExoPlayer.player.playWhenReady = true
                            audioc = false
                            listModelList2.add(mainPlayModel)
                        } else {
                            MiniPlayerFragment.isDisclaimer = 0
                            if (IsPlayDisclimer.equals("1", ignoreCase = true)) {
                                audioc = true
                                listModelList2.add(mainPlayModel)
                            }
                        }
                    } else {
                        MiniPlayerFragment.isDisclaimer = 0
                        if (IsPlayDisclimer.equals("1", ignoreCase = true)) {
                            audioc = true
                            listModelList2.add(mainPlayModel)
                        }
                    }
                    listModelList2.add(listModel!![position])
                    callPlayer(0, listModelList2, audioc)
                }
                notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun callMyPlayer() {
            val i = Intent(ctx, MyPlayerActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            ctx!!.startActivity(i)
            activity.overridePendingTransition(0, 0)
        }

        private fun callPlayer(
            position: Int,
            listModel: ArrayList<SuggestedModel.ResponseData?>,
            audioc: Boolean
        ) {
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
            DashboardActivity.audioClick = audioc
            callMyPlayer()
        }

        override fun getItemCount(): Int {
            return if (10 > listModel!!.size) {
                listModel.size
            } else {
                10
            }
        }

        inner class MyViewHolder(var binding: DownloadsLayoutBinding) : RecyclerView.ViewHolder(
            binding.root
        )
    }

    inner class SuggestedPlayListsAdpater(private val PlaylistModel: List<SearchPlaylistModel.ResponseData>) :
        RecyclerView.Adapter<SuggestedPlayListsAdpater.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: DownloadsLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.downloads_layout, parent, false
            )
            return MyViewHolder(v)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = PlaylistModel[position].name
            holder.binding.pbProgress.visibility = View.GONE
            holder.binding.equalizerview.visibility = View.GONE
            if (PlaylistModel[position].totalAudio.equals("", ignoreCase = true) ||
                (PlaylistModel[position].totalAudio.equals("0", ignoreCase = true) &&
                        PlaylistModel[position].totalhour.equals("", ignoreCase = true)
                        && PlaylistModel[position].totalminute.equals("", ignoreCase = true))
            ) {
                holder.binding.tvTime.text = "0 Audio | 0h 0m"
            } else {
                if (PlaylistModel[position].totalminute.equals("", ignoreCase = true)) {
                    holder.binding.tvTime.text = (PlaylistModel[position].totalAudio + " Audio | "
                            + PlaylistModel[position].totalhour + "h 0m")
                } else {
                    holder.binding.tvTime.text = PlaylistModel[position].totalAudio +
                            " Audios | " + PlaylistModel[position].totalhour + "h " + PlaylistModel[position].totalminute + "m"
                }
            }
            val measureRatio = BWSApplication.measureRatio(ctx, 0f, 1f, 1f, 0.12f, 0f)
            holder.binding.cvImage.layoutParams.height =
                (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.cvImage.layoutParams.width =
                (measureRatio.widthImg * measureRatio.ratio).toInt()
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
            Glide.with(ctx).load(PlaylistModel[position].image).thumbnail(0.05f)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                .into(holder.binding.ivRestaurantImage)
            Glide.with(ctx).load(R.drawable.ic_image_bg).thumbnail(0.05f)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                .into(holder.binding.ivBackgroundImage)
            holder.binding.ivIcon.setImageResource(R.drawable.ic_add_two_icon)
            //            if (PlaylistModel.get(position).isLock().equalsIgnoreCase("1")) {
//                holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                holder.binding.ivLock.setVisibility(View.VISIBLE);
//            } else if (PlaylistModel.get(position).isLock().equalsIgnoreCase("2")) {
//                holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                holder.binding.ivLock.setVisibility(View.VISIBLE);
//            } else if (PlaylistModel.get(position).isLock().equalsIgnoreCase("0") || PlaylistModel.get(position).isLock().equalsIgnoreCase("")) {
            holder.binding.ivBackgroundImage.visibility = View.GONE
            holder.binding.ivLock.visibility = View.GONE
            //            }
            holder.binding.llMainLayout.setOnClickListener {
//                if (PlaylistModel.get(position).isLock().equalsIgnoreCase("1")) {
//                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                    holder.binding.ivLock.setVisibility(View.VISIBLE);
//                    Intent i = new Intent(ctx, MembershipChangeActivity.class);
//                    i.putExtra("ComeFrom", "Plan");
//                    startActivity(i);
//                } else if (PlaylistModel.get(position).isLock().equalsIgnoreCase("1")) {
//                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                    holder.binding.ivLock.setVisibility(View.VISIBLE);
//                    BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
//                } else if (PlaylistModel.get(position).isLock().equalsIgnoreCase("0") || PlaylistModel.get(position).isLock().equalsIgnoreCase("")) {
                holder.binding.ivBackgroundImage.visibility = View.GONE
                holder.binding.ivLock.visibility = View.GONE
                AudioDownloadsFragment.comefromDownload = "0"
                addToSearch = true
                MyPlaylistIds = PlaylistModel[position].iD
                PlaylistIDMS = playlistId
                finish()
            }
            holder.binding.llRemoveAudio.setOnClickListener {
//                if (PlaylistModel.get(position).isLock().equalsIgnoreCase("1")) {
//                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                    holder.binding.ivLock.setVisibility(View.VISIBLE);
//                    Intent i = new Intent(ctx, MembershipChangeActivity.class);
//                    i.putExtra("ComeFrom", "Plan");
//                    startActivity(i);
//                } else if (PlaylistModel.get(position).isLock().equalsIgnoreCase("2")) {
//                    holder.binding.ivBackgroundImage.setVisibility(View.VISIBLE);
//                    holder.binding.ivLock.setVisibility(View.VISIBLE);
//                    BWSApplication.showToast(getString(R.string.reactive_plan), ctx);
//                } else if (PlaylistModel.get(position).isLock().equalsIgnoreCase("0") || PlaylistModel.get(position).isLock().equalsIgnoreCase("")) {
                holder.binding.ivBackgroundImage.visibility = View.GONE
                holder.binding.ivLock.visibility = View.GONE
                AudioDownloadsFragment.comefromDownload = "0"
                val shared = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val audioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                val pID = shared.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "0")
                if (audioFlag.equals("SubPlayList", ignoreCase = true) && pID.equals(
                        playlistId,
                        ignoreCase = true
                    )
                ) {
                    if (MiniPlayerFragment.isDisclaimer == 1) {
                        BWSApplication.showToast(
                            "The audio shall add after playing the disclaimer",
                            activity
                        )
                    } else {
                        callAddSearchAudio("", "1", PlaylistModel[position].iD)
                    }
                } else {
                    callAddSearchAudio("", "1", PlaylistModel[position].iD)
                }
            }
        }

        override fun getItemCount(): Int {
            return if (10 > PlaylistModel.size) {
                PlaylistModel.size
            } else {
                10
            }
        }

        inner class MyViewHolder(var binding: DownloadsLayoutBinding) : RecyclerView.ViewHolder(
            binding.root
        )
    }

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
                    notificationStatus = false
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
                if (!notificationStatus) {
                    val notificationManager =
                        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(GlobalInitExoPlayer.notificationId)
                    GlobalInitExoPlayer.relesePlayer(applicationContext)
                }
            } else {
                Log.e("Destroy", "Activity go in main activity")
            }
        }
    }

    companion object {
        var addToSearch = false
        var MyPlaylistIds: String? = ""
        var PlaylistIDMS: String? = ""
    }
}