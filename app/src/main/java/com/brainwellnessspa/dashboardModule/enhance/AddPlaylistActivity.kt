package com.brainwellnessspa.dashboardModule.enhance

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.BWSApplication.isDisclaimer
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.models.AddToPlaylistModel
import com.brainwellnessspa.dashboardModule.models.CreateNewPlaylistModel
import com.brainwellnessspa.dashboardModule.models.CreatePlaylistingModel
import com.brainwellnessspa.dashboardModule.models.SubPlayListModel
import com.brainwellnessspa.dashboardOldModule.transParentPlayer.models.MainPlayModel
import com.brainwellnessspa.databinding.ActivityAddPlaylistBinding
import com.brainwellnessspa.databinding.AddPlayListLayoutBinding
import com.brainwellnessspa.services.GlobalInitExoPlayer
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

class AddPlaylistActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddPlaylistBinding
    var userName: String? = null
    var coUserId: String? = null
    var userId: String? = null
    var audioId: String? = ""
    var fromPlaylistID: String? = ""
    var playlistName: String? = ""
    var screenView: String? = ""
    var playlistImage: String? = ""
    var playlistType: String? = ""
    lateinit var ctx: Context
    lateinit var activity: Activity
    var p: Properties? = null
    var stackStatus = 0
    var myBackPress = false
    private var numStarted = 0

    @SuppressLint("SetTextI18n") override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_playlist)
        ctx = this@AddPlaylistActivity
        activity = this@AddPlaylistActivity
        val shared1 = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        userName = shared1.getString(CONSTANTS.PREFE_ACCESS_NAME, "")
        if (intent.extras != null) {
            audioId = intent.getStringExtra("AudioId")
            fromPlaylistID = intent.getStringExtra("PlaylistID")
            playlistName = intent.getStringExtra("PlaylistName")
            playlistImage = intent.getStringExtra("PlaylistImage")
            playlistType = intent.getStringExtra("PlaylistType")
            screenView = intent.getStringExtra("ScreenView")
        }
        binding.llBack.setOnClickListener { finish() }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(AppLifecycleCallback())
        }
        val played: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        binding.rvPlayLists.layoutManager = played
        binding.rvPlayLists.itemAnimator = DefaultItemAnimator()
        binding.llError.visibility = View.GONE
        binding.tvFound.text = "No result found"
        binding.btnAddPlatLists.setOnClickListener {
            myBackPress = true
            val dialog = Dialog(ctx)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.create_palylist)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(activity, R.color.blue_transparent)))
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            val edtCreate = dialog.findViewById<EditText>(R.id.edtCreate)
            val tvCancel = dialog.findViewById<TextView>(R.id.tvCancel)
            val btnSendCode = dialog.findViewById<Button>(R.id.btnSendCode)
            dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss()
                    return@setOnKeyListener true
                }
                false
            }
            val popupTextWatcher: TextWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    val number = edtCreate.text.toString().trim { it <= ' ' }
                    if (number.isNotEmpty()) {
                        btnSendCode.isEnabled = true
                        btnSendCode.setTextColor(ContextCompat.getColor(activity, R.color.light_black))
                        btnSendCode.setBackgroundResource(R.drawable.white_round_cornor)
                    } else {
                        btnSendCode.isEnabled = false
                        btnSendCode.setTextColor(ContextCompat.getColor(activity, R.color.white))
                        btnSendCode.setBackgroundResource(R.drawable.gray_round_cornor)
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            }
            edtCreate.addTextChangedListener(popupTextWatcher)
            btnSendCode.setOnClickListener {
                if (edtCreate.text.toString().equals("", ignoreCase = true)) {
                    BWSApplication.showToast("Please provide the playlist's name", activity)
                } else {
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        val listCall = APINewClient.getClient().getCreatePlaylist(coUserId, edtCreate.text.toString())
                        listCall.enqueue(object : Callback<CreateNewPlaylistModel?> {
                            override fun onResponse(call: Call<CreateNewPlaylistModel?>, response: Response<CreateNewPlaylistModel?>) {
                                try {
                                    if (response.isSuccessful) {
                                        val listsModel = response.body()
                                        if (listsModel!!.responseData!!.iscreate.equals("1", ignoreCase = true)) {
                                            dialog.dismiss()
                                            prepareData(ctx)
                                            val playlistID = listsModel.responseData!!.playlistID
                                            val shared = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                                            val audioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                                            val pID = shared.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "0")
                                            if (audioFlag.equals("playlist", ignoreCase = true) && pID.equals(playlistID, ignoreCase = true)) {
                                                if (isDisclaimer == 1) {
                                                    BWSApplication.showToast("The audio shall add after playing the disclaimer", activity)
                                                } else {
                                                    callAddPlaylistFromPlaylist(playlistID, listsModel.responseData!!.playlistName, "1")
                                                }
                                            } else {
                                                callAddPlaylistFromPlaylist(playlistID, listsModel.responseData!!.playlistName, "1")
                                            } //                                            Properties p = new Properties();
                                            //                                            p.putValue("userId", UserID);
                                            //                                            p.putValue("playlistId", PlaylistID);
                                            //                                            p.putValue("playlistName", listsModel.getResponseData().getName());
                                            //                                            p.putValue("source", "Add To Playlist Screen");
                                            //                                            BWSApplication.addToSegment("Playlist Created", p, CONSTANTS.track);
                                        } else {
                                            BWSApplication.showToast(listsModel.responseMessage, activity)
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                            override fun onFailure(call: Call<CreateNewPlaylistModel?>, t: Throwable) {
                            }
                        })
                    } else {
                        BWSApplication.showToast(getString(R.string.no_server_found), activity)
                    }
                }
            }
            tvCancel.setOnClickListener { dialog.dismiss() }
            dialog.show()
            dialog.setCancelable(false)
        }
    }

    override fun onBackPressed() {/*   comefrom_search = 0;
        myBackPress = true;
        if (comeAddPlaylist == 1) {
            Intent i = new Intent(ctx, MyPlaylistActivity.class);
            i.putExtra("PlaylistID", fromPlaylistID);
            i.putExtra("PlaylistName", playlistName);
            i.putExtra("PlaylistIDImage", PlaylistImage);
            i.putExtra("ScreenView", ScreenView);
            i.putExtra("PlaylistType", PlaylistType);
            i.putExtra("Liked", "0");
            startActivity(i);
            finish();
        } else {
            finish();
        }*/
        finish()
    }

    override fun onResume() {
        prepareData(this@AddPlaylistActivity)
        super.onResume()
    }

    private fun prepareData(ctx: Context) {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall = APINewClient.getClient().getPlaylisting(coUserId)
            listCall.enqueue(object : Callback<CreatePlaylistingModel?> {
                override fun onResponse(call: Call<CreatePlaylistingModel?>, response: Response<CreatePlaylistingModel?>) {
                    try {
                        if (response.isSuccessful) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                            val model = response.body()
                            if (model!!.responseData!!.isEmpty()) {
                                binding.llError.visibility = View.GONE
                                binding.rvPlayLists.visibility = View.GONE
                            } else {
                                binding.rvPlayLists.visibility = View.VISIBLE

                                //                            p = new Properties();
                                //                            p.putValue("userId", UserID);
                                //                            p.putValue("source", ScreenView);
                                //                            ArrayList<SegmentPlaylist> section = new ArrayList<>();
                                //                            for (int i = 0; i < model.getResponseData().size(); i++) {
                                //                                SegmentPlaylist e = new SegmentPlaylist();
                                //                                e.setPlaylistId(model.getResponseData().get(i).getID());
                                //                                e.setPlaylistName(model.getResponseData().get(i).getName());
                                //                                e.setPlaylistType(model.getResponseData().get(i).getCreated());
                                //                                e.setPlaylistDuration(model.getResponseData().get(i).getTotalhour() + "h " + model.getResponseData().get(i).getTotalminute() + "m");
                                //                                e.setAudioCount(model.getResponseData().get(i).getTotalAudio());
                                //                                section.add(e);
                                //                            }
                                //                            Gson gson = new Gson();
                                //                            p.putValue("playlists", gson.toJson(section));
                                //                            BWSApplication.addToSegment("Playlist List Viewed", p, CONSTANTS.screen);
                                val addPlaylistAdapter = AddPlaylistAdapter(model.responseData, ctx)
                                binding.rvPlayLists.adapter = addPlaylistAdapter
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<CreatePlaylistingModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }

    private fun callAddPlaylistFromPlaylist(PlaylistID: String?, name: String?, New: String) {
        myBackPress = true
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall = APINewClient.getClient().getAddSearchAudioFromPlaylist(coUserId, audioId, PlaylistID, fromPlaylistID)
            listCall.enqueue(object : Callback<AddToPlaylistModel?> {
                override fun onResponse(call: Call<AddToPlaylistModel?>, response: Response<AddToPlaylistModel?>) {
                    try {
                        val listModels = response.body()
                        if (listModels!!.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity) //                                BWSApplication.showToast(listModels.getResponseMessage(), activity);
                            val shared = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                            val audioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                            var pos = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                            val pID = shared.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                            if (audioFlag.equals("playList", ignoreCase = true) && pID.equals(PlaylistID, ignoreCase = true)) {
                                val gsonx = Gson()
                                val json = shared.getString(CONSTANTS.PREF_KEY_PlayerAudioList, gsonx.toString())
                                val type = object : TypeToken<ArrayList<MainPlayModel?>?>() {}.type
                                val mainPlayModelListold: ArrayList<MainPlayModel> = gsonx.fromJson(json, type)
                                val id = mainPlayModelListold[pos].id
                                val size = mainPlayModelListold.size
                                val mainPlayModelList = ArrayList<MainPlayModel>()
                                val playlistSongs = ArrayList<SubPlayListModel.ResponseData.PlaylistSong>()
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
                                    mainPlayModel.id = listModels.responseData!![i].iD
                                    mainPlayModel.name = listModels.responseData!![i].name
                                    mainPlayModel.audioFile = listModels.responseData!![i].audioFile
                                    mainPlayModel.playlistID = listModels.responseData!![i].playlistID
                                    mainPlayModel.audioDirection = listModels.responseData!![i].audioDirection
                                    mainPlayModel.audiomastercat = listModels.responseData!![i].audiomastercat
                                    mainPlayModel.audioSubCategory = listModels.responseData!![i].audioSubCategory
                                    mainPlayModel.imageFile = listModels.responseData!![i].imageFile
                                    mainPlayModel.audioDuration = listModels.responseData!![i].audioDuration
                                    playlistSongs.add(mainPlayModel)
                                }
                                for (i in mainPlayModelList.indices) {
                                    if (mainPlayModelList[i].id.equals(id, ignoreCase = true)) {
                                        pos = i
                                        break
                                    }
                                }
                                val sharedd = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                                val editor = sharedd.edit()
                                val gson = Gson()
                                val jsonx = gson.toJson(mainPlayModelList)
                                val json1 = gson.toJson(playlistSongs)
                                editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json1)
                                editor.putString(CONSTANTS.PREF_KEY_PlayerAudioList, jsonx)
                                editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, pos)
                                editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, PlaylistID)
                                editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, playlistName)
                                editor.putString(CONSTANTS.PREF_KEY_PlayFrom, "created")
                                editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "playlist")
                                editor.apply()
                                if (mainPlayModelList[pos].audioFile != "") {
                                    val downloadAudioDetailsList: List<String> = ArrayList()
                                    val ge = GlobalInitExoPlayer()
                                    ge.AddAudioToPlayer(size, mainPlayModelList, downloadAudioDetailsList, ctx)
                                }
                            }/*  if (comeAddPlaylist == 1) {
                                final Dialog dialog = new Dialog(ctx);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.go_to_playlist);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_transparent)));
                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                final TextView tvCancel = dialog.findViewById(R.id.tvCancel);
                                final RelativeLayout rlCreate = dialog.findViewById(R.id.rlCreate);
                                dialog.setOnKeyListener((v, keyCode, event) -> {
                                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                                        dialog.dismiss();
                                        return true;
                                    }
                                    return false;
                                });

                                rlCreate.setOnClickListener(view2 -> {
                                    addToPlayList = true;
                                    MyPlaylistId = PlaylistID;
                                    dialog.dismiss();
                                    finish();
                                });

                                tvCancel.setOnClickListener(v -> {
                                    dialog.dismiss();
                                    finish();
                                });
                                dialog.show();
                                dialog.setCancelable(false);
                            } else {
                                if (d.equalsIgnoreCase("0")) {
                                    dialog.dismiss();
                                }

                            }*/
                            val dialog = Dialog(ctx)
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                            dialog.setContentView(R.layout.go_to_playlist)
                            dialog.window!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(activity, R.color.blue_transparent)))
                            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                            val tvCancel = dialog.findViewById<TextView>(R.id.tvCancel)
                            val rlCreate = dialog.findViewById<RelativeLayout>(R.id.rlCreate)
                            dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    dialog.dismiss()
                                    return@setOnKeyListener true
                                }
                                false
                            }
                            rlCreate.setOnClickListener { //                                addToPlayList = true;
                                //                                MyPlaylistId = PlaylistID;
                                val intent = Intent(ctx, MyPlaylistListingActivity::class.java)
                                intent.putExtra("New", New)
                                intent.putExtra("PlaylistID", PlaylistID)
                                intent.putExtra("PlaylistName", name)
                                intent.putExtra("MyDownloads", "0")
                                startActivity(intent)
                                finish()
                                overridePendingTransition(0, 0)
                                dialog.dismiss()
                            }
                            tvCancel.setOnClickListener {
                                dialog.dismiss()
                                finish()
                            }
                            dialog.show()
                            dialog.setCancelable(false)
                        } else if (listModels.responseCode.equals(getString(R.string.ResponseCodefail), ignoreCase = true)) {
                            BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                            BWSApplication.showToast(listModels.responseMessage, activity)
                        }
                    } catch (e: Exception) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<AddToPlaylistModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }

    private inner class AddPlaylistAdapter(private val listModel: List<CreatePlaylistingModel.ResponseData>?, var ctx: Context) : RecyclerView.Adapter<AddPlaylistAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: AddPlayListLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.add_play_list_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = listModel!![position].name
            val measureRatio = BWSApplication.measureRatio(ctx, 0f, 1f, 1f, 0.16f, 0f)
            holder.binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            Glide.with(ctx).load(listModel[position].image).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(32))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)
            holder.binding.llMainLayout.setOnClickListener {
                val playlistID = listModel[position].id
                val shared = getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, MODE_PRIVATE)
                val audioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                val pID = shared.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "0")
                if (audioFlag.equals("playlist", ignoreCase = true) && pID.equals(playlistID, ignoreCase = true)) {
                    if (isDisclaimer == 1) {
                        BWSApplication.showToast("The audio shall add after playing the disclaimer", activity)
                    } else {
                        callAddPlaylistFromPlaylist(playlistID, listModel[position].name, "0")
                    }
                } else {
                    callAddPlaylistFromPlaylist(playlistID, listModel[position].name, "0")
                }
            }
        }

        override fun getItemCount(): Int {
            return listModel!!.size
        }

        inner class MyViewHolder(var binding: AddPlayListLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }

    internal inner class AppLifecycleCallback : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        override fun onActivityStarted(activity: Activity) {
            if (numStarted == 0) {
                stackStatus = 1
                Log.e("APPLICATION", "APP IN FOREGROUND") //app went to foreground
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
                Log.e("APPLICATION", "App is in BACKGROUND") // app went to background
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {
            if (numStarted == 0 && stackStatus == 2) {
                Log.e("Destroy", "Activity Destoryed")
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(BWSApplication.notificationId)
                GlobalInitExoPlayer.relesePlayer(applicationContext)
            } else {
                Log.e("Destroy", "Activity go in main activity")
            }
        }
    }

    companion object {
        var addToPlayList = false
        var MyPlaylistId = ""
    }
}