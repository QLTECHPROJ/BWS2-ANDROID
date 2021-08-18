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
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.activities.MyPlayerActivity
import com.brainwellnessspa.dashboardModule.models.*
import com.brainwellnessspa.dashboardModule.models.HomeScreenModel.ResponseData.DisclaimerAudio
import com.brainwellnessspa.databinding.ActivityAddAudioBinding
import com.brainwellnessspa.databinding.DownloadsLayoutBinding
import com.brainwellnessspa.databinding.GlobalSearchLayoutBinding
import com.brainwellnessspa.downloadModule.fragments.AudioDownloadsFragment
import com.brainwellnessspa.services.GlobalInitExoPlayer
import com.brainwellnessspa.services.GlobalInitExoPlayer.Companion.GetCurrentAudioPosition
import com.brainwellnessspa.userModule.signupLogin.SignInActivity
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
    var p: Properties? = null
    var stackStatus = 0
    var myBackPress = false
    var notificationStatus = false
    var gsonBuilder: GsonBuilder? = null
    var section: ArrayList<String?>? = null
    private var numStarted = 0

    /* This listner is use for get play or pause button status when user play pause from music notifiction bar */
    private val listener: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("MyData")) {
                val data = intent.getStringExtra("MyData")
                Log.d("play_pause_Action", data!!)
                val sharedzw = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val audioFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                if (!audioFlag.equals("Downloadlist", ignoreCase = true) && !audioFlag.equals("playlist", ignoreCase = true) && !audioFlag.equals("TopCategories", ignoreCase = true)) {
                    if (player != null) {
                        if (listSize != 0) {
                            serachListAdpater!!.notifyDataSetChanged()
                        }
                        suggestedAdpater!!.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    /* Main Method of Add Audio Activity */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_audio)
        ctx = this@AddAudioActivity
        activity = this@AddAudioActivity
        if (intent.extras != null) {
            playlistId = intent.getStringExtra(CONSTANTS.PlaylistID)
        }
        /* Get User ID and MAin Account ID*/
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        userName = shared1.getString(CONSTANTS.PREFE_ACCESS_NAME, "")
        notificationStatus = false
        p = Properties()
        if (playlistId.equals("", ignoreCase = true)) {
            p!!.putValue("source", "Manage Search Screen")
        } else {
            p!!.putValue("source", "Add Audio Screen")
        }
        addToSegment("Search Screen Viewed", p, CONSTANTS.screen)
        binding.searchView.onActionViewExpanded()
        searchEditText = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(ContextCompat.getColor(activity, R.color.dark_blue_gray))
        searchEditText.setHintTextColor(ContextCompat.getColor(activity, R.color.gray))
        val closeButton = binding.searchView.findViewById<ImageView>(R.id.search_close_btn)
        binding.searchView.clearFocus()

        /* Back Icon Click */
        binding.llBack.setOnClickListener { callback() }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(AppLifecycleCallback())
        }
        val manager: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        binding.rvSuggestedList.layoutManager = manager
        binding.rvSuggestedList.itemAnimator = DefaultItemAnimator()
        val layoutSerach: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        binding.rvSerachList.layoutManager = layoutSerach
        binding.rvSerachList.itemAnimator = DefaultItemAnimator()
        val layoutPlay: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        binding.rvPlayList.layoutManager = layoutPlay
        binding.rvPlayList.itemAnimator = DefaultItemAnimator()

        /* close button click of search view */
        closeButton.setOnClickListener {
            binding.searchView.clearFocus()
            searchEditText.setText("")
            binding.rvSerachList.adapter = null
            binding.rvSerachList.visibility = View.GONE
            binding.llError.visibility = View.GONE
            binding.searchView.setQuery("", false)
        }
        /* Search view click */
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(search: String): Boolean {
                binding.searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(search: String): Boolean {
                prepareSearchData(search, searchEditText)
                p = Properties()
                if (playlistId.equals("", ignoreCase = true)) {
                    p!!.putValue("source", "Manage Search Screen")
                } else {
                    p!!.putValue("source", "Add Audio Screen")
                }
                p!!.putValue("searchKeyword", search)
                addToSegment("Audio Searched", p, CONSTANTS.track)
//                showToast("onQueryTextChange", activity)
                return false
            }

        })
    }

    override fun onResume() {
        binding.searchView.clearFocus()
        searchEditText.setText("")
        binding.rvSerachList.adapter = null
        binding.rvSerachList.visibility = View.GONE
        binding.llError.visibility = View.GONE
        binding.searchView.setQuery("", false)
        prepareSuggestedData()
        super.onResume()
    }

    public override fun onPause() {
        LocalBroadcastManager.getInstance(ctx).unregisterReceiver(listener)
        super.onPause()
    }

    private fun callback() {
        myBackPress = true
        AudioDownloadsFragment.comefromDownload = "0"
        finish()
    }

    /* main Api function for search audio */
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
            PlayerAudioId = mainPlayModelList[playerPositionx].id
        }
        if (isNetworkConnected(ctx)) {
            showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall = APINewClient.client.getSearchBoth(coUserId, search)
            listCall.enqueue(object : Callback<SearchBothModel?> {
                override fun onResponse(call: Call<SearchBothModel?>, response: Response<SearchBothModel?>) {
                    try {
                        val listModel = response.body()
                        if (listModel!!.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                            if (!searchEditText!!.text.toString().equals("", ignoreCase = true)) {
                                if (listModel.responseData!!.isEmpty()) {
                                    binding.rvSerachList.visibility = View.GONE
                                    binding.llError.visibility = View.VISIBLE
                                    binding.tvFound.text = "Please try again with another search term."
                                    showToast("Please try again", activity)
                                    //                                    binding.tvFound.setText("Couldn't find '" + search + "'. Try searching again");
                                } else {
                                    showToast("Not Please try again", activity)
                                    /* set adapter data to search screen */
                                    binding.llError.visibility = View.GONE
                                    binding.rvSerachList.visibility = View.VISIBLE
                                    serachListAdpater = SerachListAdpater(listModel.responseData, activity, binding.rvSerachList, coUserId)
                                    binding.rvSerachList.adapter = serachListAdpater
                                    LocalBroadcastManager.getInstance(ctx).registerReceiver(listener, IntentFilter("play_pause_Action"))
                                }
                            } else if (searchEditText.text.toString().equals("", ignoreCase = true)) {
                                showToast("adapter null", activity)
                                binding.rvSerachList.adapter = null
                                binding.rvSerachList.visibility = View.GONE
                                binding.llError.visibility = View.GONE
                            }
                        } else if (listModel.responseCode.equals(getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
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
                        } else {
                            showToast(listModel.responseMessage, activity)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<SearchBothModel?>, t: Throwable) {
                    hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            showToast(getString(R.string.no_server_found), activity)
        }
    }

    /* suggested serch audio api function */
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
            PlayerAudioId = mainPlayModelList[playerPositionx].id
        }
        if (isNetworkConnected(ctx)) {
            showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall = APINewClient.client.getSuggestedLists(coUserId)
            listCall.enqueue(object : Callback<SuggestedModel?> {
                override fun onResponse(call: Call<SuggestedModel?>, response: Response<SuggestedModel?>) {
                    try {
                        hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel = response.body()
                        if (listModel != null) {
                            when {
                                listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true) -> {
                                    binding.tvSuggestedAudios.setText(R.string.Suggested_Audios)
                                    binding.tvSAViewAll.visibility = View.VISIBLE
                                    suggestedAdpater = SuggestedAdpater(listModel.responseData, ctx)
                                    binding.rvSuggestedList.adapter = suggestedAdpater
                                    p = Properties()
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
                                    addToSegment("Suggested Audios List Viewed", p, CONSTANTS.screen)
                                    LocalBroadcastManager.getInstance(ctx).registerReceiver(listener, IntentFilter("play_pause_Action"))

                                    /* view all button click for view all audios */
                                    binding.tvSAViewAll.setOnClickListener {
                                        notificationStatus = true
                                        val i = Intent(ctx, ViewSuggestedActivity::class.java)
                                        i.putExtra("Name", "Suggested Audios")
                                        i.putExtra("PlaylistID", playlistId)
                                        i.putParcelableArrayListExtra("AudiolistModel", listModel.responseData)
                                        startActivity(i)
                                        finish()
                                    }
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
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<SuggestedModel?>, t: Throwable) {
                    hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            showToast(getString(R.string.no_server_found), activity)
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

    /* add audio to play list function and audio add to player*/
    private fun callAddSearchAudio(AudioID: String?, s: String, FromPlaylistId: String?) {
        if (isNetworkConnected(ctx)) {
            showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall = APINewClient.client.getAddSearchAudioFromPlaylist(coUserId, AudioID, playlistId, FromPlaylistId)
            listCall.enqueue(object : Callback<AddToPlaylistModel?> {
                override fun onResponse(call: Call<AddToPlaylistModel?>, response: Response<AddToPlaylistModel?>) {
                    try {
                        hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModels = response.body()
                        if (listModels!!.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            showToast(listModels.responseMessage, activity)
                            val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                            val audioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                            val myPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                            val myPlaylistName = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "")
                            val playFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
                            var playerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                            if (audioPlayerFlag.equals("playlist", ignoreCase = true) && myPlaylist.equals(playlistId, ignoreCase = true)) {
                                val gsonx = Gson()
                                val json = shared1.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gsonx.toString())
                                val type = object : TypeToken<ArrayList<MainPlayModel?>?>() {}.type
                                val mainPlayModelListold: ArrayList<MainPlayModel> = gsonx.fromJson(json, type)
                                val id = mainPlayModelListold[playerPosition].id
                                val mainPlayModelList = ArrayList<MainPlayModel>()
                                val playlistSongs = ArrayList<SubPlayListModel.ResponseData.PlaylistSong>()
                                val size = mainPlayModelListold.size
                                for (i in listModels.responseData!!.indices) {
                                    val mainPlayModel = MainPlayModel()
                                    mainPlayModel.id = listModels.responseData!![i].iD.toString()
                                    mainPlayModel.name = listModels.responseData!![i].name.toString()
                                    mainPlayModel.audioFile = listModels.responseData!![i].audioFile.toString()
                                    mainPlayModel.playlistID = listModels.responseData!![i].playlistID.toString()
                                    mainPlayModel.audioDirection = listModels.responseData!![i].audioDirection.toString()
                                    mainPlayModel.audiomastercat = listModels.responseData!![i].audiomastercat.toString()
                                    mainPlayModel.audioSubCategory = listModels.responseData!![i].audioSubCategory.toString()
                                    mainPlayModel.imageFile = listModels.responseData!![i].imageFile.toString()
                                    mainPlayModel.audioDuration = listModels.responseData!![i].audioDuration.toString()
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
                                /* add audio to player */
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
                                    /* add audio to payer list */
                                    val downloadAudioDetailsList: List<String> = ArrayList()
                                    val ge = GlobalInitExoPlayer()
                                    ge.AddAudioToPlayer(size, mainPlayModelList, downloadAudioDetailsList, ctx)
                                }
                            }
                            if (s.equals("1", ignoreCase = true)) {
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
                        } else if (listModels.responseCode.equals(getString(R.string.ResponseCodefail), ignoreCase = true)) {
                            showToast(listModels.responseMessage, activity)
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
            showToast(ctx.getString(R.string.no_server_found), activity)
        }
    }

    /* Search Audio data set in to adapter */
    inner class SerachListAdpater(private val listModel: List<SearchBothModel.ResponseData>?, var ctx: Context?, var rvSerachList: RecyclerView, var UserID: String?) : RecyclerView.Adapter<SerachListAdpater.MyViewHolder>() {
        var songId: String? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: GlobalSearchLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.global_search_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = listModel!![position].name
            if (listModel[position].isPlay.equals("0")) {
                holder.binding.ivLock.visibility = View.VISIBLE
            } else {
                holder.binding.ivLock.visibility = View.GONE
            }
            if (listModel[position].iscategory.equals("1", ignoreCase = true)) {
                holder.binding.tvPart.text = listModel[position].audioDuration
                holder.binding.llRemoveAudio.visibility = View.VISIBLE
//                holder.binding.ivLock.visibility = View.GONE
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
                holder.binding.llMainLayoutForPlayer.setOnClickListener {
                    if (listModel[position].isPlay.equals("0")) {
                        callEnhanceActivity(ctx, activity)
                    } else {
                        callMainTransFrag(position)
                    }
                }

                holder.binding.llRemoveAudio.setOnClickListener {
                    if (IsLock.equals("1")) {
                        callEnhanceActivity(ctx, activity)
                    } else if (IsLock.equals("0")) {
                        val audioID = listModel[position].iD
                        if (playlistId.equals("", ignoreCase = true)) {
                            val p = Properties()
                            p.putValue("audioId", listModel[position].iD)
                            p.putValue("audioName", listModel[position].name)
                            p.putValue("audioDescription", "")
                            p.putValue("directions", listModel[position].audioDirection)
                            p.putValue("masterCategory", listModel[position].audiomastercat)
                            p.putValue("subCategory", listModel[position].audioSubCategory)
                            p.putValue("audioDuration", listModel[position].audioDuration)
                            p.putValue("position", GetCurrentAudioPosition())
                            p.putValue("audioType", "Streaming")
                            p.putValue("source", "Add Audio Screen")
                            p.putValue("audioService", appStatus(ctx))
                            p.putValue("bitRate", "")
                            p.putValue("sound", hundredVolume.toString())
                            addToSegment("Add To Playlist Clicked", p, CONSTANTS.track)
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
                            if (audioPlayerFlag.equals("playList", ignoreCase = true) && myPlaylist.equals(playlistId, ignoreCase = true)) {
                                if (isDisclaimer == 1) {
                                    showToast("The audio shall add after playing the disclaimer", activity)
                                } else {
                                    callAddSearchAudio(audioID, "0", "")
                                }
                            } else {
                                callAddSearchAudio(audioID, "0", "")
                            }
                        }
                    }
                }
            } else if (listModel[position].iscategory.equals("0", ignoreCase = true)) {
                holder.binding.tvPart.setText(R.string.Playlist)
                holder.binding.tvPart.visibility = View.GONE
                holder.binding.equalizerview.visibility = View.GONE
                holder.binding.llRemoveAudio.visibility = View.GONE
                holder.binding.llMainLayout.visibility = View.GONE
                holder.binding.ivBackgroundImage.visibility = View.GONE
                holder.binding.llMainLayout.setOnClickListener {
                    if (IsLock.equals("1")) {
                        callEnhanceActivity(ctx, activity)
                    } else if (IsLock.equals("0")) {
                        AudioDownloadsFragment.comefromDownload = "0"
                        addToSearch = true
                        MyPlaylistIds = listModel[position].iD
                        PlaylistIDMS = playlistId
                        finish()
                    }
                }
                holder.binding.llRemoveAudio.setOnClickListener {
                    if (IsLock.equals("1")) {
                        callEnhanceActivity(ctx, activity)
                    } else if (IsLock.equals("0")) {
                        val shared1 = ctx!!.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                        val audioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                        val myPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                        if (audioPlayerFlag.equals("playList", ignoreCase = true) && myPlaylist.equals(playlistId, ignoreCase = true)) {
                            if (isDisclaimer == 1) {
                                showToast("The audio shall add after playing the disclaimer", activity)
                            } else {
                                callAddSearchAudio("", "1", listModel[position].iD)
                            }
                        } else {
                            callAddSearchAudio("", "1", listModel[position].iD)
                        }
                    }
                }
            }
            val measureRatio = measureRatio(ctx, 0f, 1f, 1f, 0.12f, 0f)
            holder.binding.cvImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.cvImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            holder.binding.ivBackgroundImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivBackgroundImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivBackgroundImage.scaleType = ImageView.ScaleType.FIT_XY
            Glide.with(ctx!!).load(listModel[position].imageFile).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)
            Glide.with(ctx!!).load(R.drawable.ic_image_bg).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivBackgroundImage)
            holder.binding.ivIcon.setImageResource(R.drawable.ic_add_two_icon)
        }

        private fun callMainTransFrag(position: Int) {
            try {
                val shared2 = ctx!!.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
                IsPlayDisclimer = shared2.getString(CONSTANTS.PREF_KEY_IsDisclimer, "0")
                val shared1 = ctx!!.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val audioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                val playFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
                if (audioPlayerFlag.equals("SearchModelAudio", ignoreCase = true) && playFrom.equals("Search Audio", ignoreCase = true)) {
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
                        val listModelList2 = ArrayList<SearchBothModel.ResponseData>()
                        listModelList2.add(listModel!![position])
                        callPlayer(0, listModelList2, true)
                    }
                } else {
                    val listModelList2 = ArrayList<SearchBothModel.ResponseData>()
                    val gson = Gson()
                    val shared12 = ctx!!.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
                    val isPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "0")
                    val disclimerJson = shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
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
                    if (isDisclaimer == 1) {
                        if (player != null) {
                            player.playWhenReady = true
                            audioc = false
                            listModelList2.add(mainPlayModel)
                        } else {
                            isDisclaimer = 0
                            if (IsPlayDisclimer.equals("1", ignoreCase = true)) {
                                audioc = true
                                listModelList2.add(mainPlayModel)
                            }
                        }
                    } else {
                        isDisclaimer = 0
                        if (IsPlayDisclimer.equals("1", ignoreCase = true)) {
                            audioc = true
                            listModelList2.add(mainPlayModel)
                        }
                    }
                    listModelList2.add(listModel!![position])
                    callPlayer(0, listModelList2, audioc)
                }
                val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
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

        private fun callPlayer(position: Int, listModel: ArrayList<SearchBothModel.ResponseData>, audioc: Boolean) {
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
            audioClick = audioc
            callMyPlayer()
        }

        override fun getItemCount(): Int {
            listSize = listModel!!.size
            return listModel.size
        }

        inner class MyViewHolder(var binding: GlobalSearchLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }

    /* Suggested Audio data set in to adapter */
    inner class SuggestedAdpater(private val listModel: List<SuggestedModel.ResponseData?>?, var ctx: Context?) : RecyclerView.Adapter<SuggestedAdpater.MyViewHolder>() {
        var songId: String? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: DownloadsLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.downloads_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = listModel!![position]!!.name
            holder.binding.tvTime.text = listModel[position]!!.audioDuration
            val measureRatio = measureRatio(ctx, 0f, 1f, 1f, 0.12f, 0f)
            holder.binding.cvImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.cvImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            holder.binding.ivBackgroundImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivBackgroundImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivBackgroundImage.scaleType = ImageView.ScaleType.FIT_XY
            Glide.with(ctx!!).load(listModel[position]!!.imageFile).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)
            Glide.with(ctx!!).load(R.drawable.ic_image_bg).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivBackgroundImage)
            holder.binding.ivIcon.setImageResource(R.drawable.ic_add_two_icon)
            if (listModel[position]!!.isPlay.equals("0")) {
                holder.binding.ivLock.visibility = View.VISIBLE
            } else {
                holder.binding.ivLock.visibility = View.GONE
            }
            val sharedzw = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
            val audioPlayerFlag = sharedzw.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
            val myPlaylist = sharedzw.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
            if (!audioPlayerFlag.equals("Downloadlist", ignoreCase = true) && !audioPlayerFlag.equals("SubPlayList", ignoreCase = true) && !audioPlayerFlag.equals("TopCategories", ignoreCase = true)) {
                if (PlayerAudioId.equals(listModel[position]!!.iD, ignoreCase = true)) {
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
            holder.binding.llMainLayoutForPlayer.setOnClickListener {
                if (listModel[position]!!.isPlay.equals("0")) {
                    callEnhanceActivity(ctx, activity)
                } else {
                    callMainTransFrag(position)
                }
            }
            holder.binding.llRemoveAudio.setOnClickListener {
                if (IsLock.equals("1")) {
                    callEnhanceActivity(ctx, activity)
                } else if (IsLock.equals("0")) {
                    if (playlistId.equals("", ignoreCase = true)) {
                        val p = Properties()
                        p.putValue("audioId", listModel[position]!!.iD)
                        p.putValue("audioName", listModel[position]!!.name)
                        p.putValue("audioDescription", "")
                        p.putValue("directions", listModel[position]!!.audioDirection)
                        p.putValue("masterCategory", listModel[position]!!.audiomastercat)
                        p.putValue("subCategory", listModel[position]!!.audioSubCategory)
                        p.putValue("audioDuration", listModel[position]!!.audioDuration)
                        p.putValue("position", GlobalInitExoPlayer.GetCurrentAudioPosition())
                        p.putValue("audioType", "Streaming")
                        p.putValue("source", "Add Audio Screen")
                        p.putValue("playerType", "Main")
                        p.putValue("audioService", appStatus(ctx))
                        p.putValue("bitRate", "")
                        p.putValue("sound", hundredVolume.toString())
                        addToSegment("Add To Playlist Clicked", p, CONSTANTS.track)
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
                        if (audioPlayerFlag.equals("playlist", ignoreCase = true) && myPlaylist.equals(playlistId, ignoreCase = true)) {
                            if (isDisclaimer == 1) {
                                showToast("The audio shall add after playing the disclaimer", activity)
                            } else {
                                callAddSearchAudio(listModel[position]!!.iD, "0", "")
                            }
                        } else {
                            callAddSearchAudio(listModel[position]!!.iD, "0", "")
                        }
                    }
                }
            }
        }

        private fun callMainTransFrag(position: Int) {
            try {
                val shared2 = ctx!!.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
                IsPlayDisclimer = shared2.getString(CONSTANTS.PREF_KEY_IsDisclimer, "0")
                val shared1 = ctx!!.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val audioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                val playFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
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
                        val listModelList2 = ArrayList<SuggestedModel.ResponseData?>()
                        listModelList2.add(listModel!![position])
                        callPlayer(0, listModelList2, true)
                    }
                } else {
                    val listModelList2 = ArrayList<SuggestedModel.ResponseData?>()
                    val gson = Gson()
                    val shared12 = ctx!!.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, MODE_PRIVATE)
                    val IsPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "0")
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
                            if (IsPlayDisclimer.equals("1", ignoreCase = true)) {
                                audioc = true
                                listModelList2.add(mainPlayModel)
                            }
                        }
                    } else {
                        isDisclaimer = 0
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

        private fun callPlayer(position: Int, listModel: ArrayList<SuggestedModel.ResponseData?>, audioc: Boolean) {
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
            return if (10 > listModel!!.size) {
                listModel.size
            } else {
                10
            }
        }

        inner class MyViewHolder(var binding: DownloadsLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }

    /* Suggested playlist data set in to adapter */
    /*inner class SuggestedPlayListsAdpater(private val PlaylistModel: List<SearchPlaylistModel.ResponseData>) : RecyclerView.Adapter<SuggestedPlayListsAdpater.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: DownloadsLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.downloads_layout, parent, false)
            return MyViewHolder(v)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = PlaylistModel[position].name
            holder.binding.pbProgress.visibility = View.GONE
            holder.binding.equalizerview.visibility = View.GONE
            if (PlaylistModel[position].totalAudio.equals("", ignoreCase = true) || (PlaylistModel[position].totalAudio.equals("0", ignoreCase = true) && PlaylistModel[position].totalhour.equals("", ignoreCase = true) && PlaylistModel[position].totalminute.equals("", ignoreCase = true))) {
                holder.binding.tvTime.text = "0 Audio | 0h 0m"
            } else {
                if (PlaylistModel[position].totalminute.equals("", ignoreCase = true)) {
                    holder.binding.tvTime.text = (PlaylistModel[position].totalAudio + " Audio | " + PlaylistModel[position].totalhour + "h 0m")
                } else {
                    holder.binding.tvTime.text = PlaylistModel[position].totalAudio + " Audios | " + PlaylistModel[position].totalhour + "h " + PlaylistModel[position].totalminute + "m"
                }
            }
            val measureRatio = measureRatio(ctx, 0f, 1f, 1f, 0.12f, 0f)
            holder.binding.cvImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.cvImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            holder.binding.ivBackgroundImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivBackgroundImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivBackgroundImage.scaleType = ImageView.ScaleType.FIT_XY
            Glide.with(ctx).load(PlaylistModel[position].image).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)
            Glide.with(ctx).load(R.drawable.ic_image_bg).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivBackgroundImage)
            holder.binding.ivIcon.setImageResource(R.drawable.ic_add_two_icon)

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
                if (audioFlag.equals("SubPlayList", ignoreCase = true) && pID.equals(playlistId, ignoreCase = true)) {
                    if (isDisclaimer == 1) {
                        showToast("The audio shall add after playing the disclaimer", activity)
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

        inner class MyViewHolder(var binding: DownloadsLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }*/

    /* app life cycle class */
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
                    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(notificationId)
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