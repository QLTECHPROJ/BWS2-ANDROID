package com.brainwellnessspa.ManageModule

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.DashboardTwoModule.AddAudioActivity
import com.brainwellnessspa.DashboardTwoModule.Model.PlaylistDetailsModel
import com.brainwellnessspa.DashboardTwoModule.Model.SucessModel
import com.brainwellnessspa.R
import com.brainwellnessspa.Utility.*
import com.brainwellnessspa.Utility.ItemMoveCallback.ItemTouchHelperContract
import com.brainwellnessspa.databinding.ActivityMyPlaylistListingBinding
import com.brainwellnessspa.databinding.MyPlaylistLayoutBinding
import com.brainwellnessspa.databinding.MyplaylistSortingNewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MyPlaylistListingActivity : AppCompatActivity(), StartDragListener {
    lateinit var ctx: Context
    lateinit var activity: Activity
    var CoUserID: String? = ""
    var MyCreated: String? = ""
    lateinit var adpater: PlayListsAdpater
    lateinit var adpater2: PlayListsAdpater2
    var USERID: String? = ""
    lateinit var binding: ActivityMyPlaylistListingBinding

    lateinit var searchEditText: EditText
    var touchHelper: ItemTouchHelper? = null
    var listMOdelGloble: PlaylistDetailsModel = PlaylistDetailsModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_playlist_listing)
        ctx = this@MyPlaylistListingActivity
        activity = this@MyPlaylistListingActivity
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
        USERID = shared.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")
        binding.tvSearch.setOnClickListener { view14 ->
            val i = Intent(ctx, AddAudioActivity::class.java)
            i.putExtra("PlaylistID", listMOdelGloble.responseData!!.playlistID)
            startActivity(i)
        }

        binding.searchView.onActionViewExpanded()
        searchEditText = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(resources.getColor(R.color.dark_blue_gray))
        searchEditText.setHintTextColor(resources.getColor(R.color.gray))
        val closeButton: ImageView = binding.searchView.findViewById(R.id.search_close_btn)
        binding.searchView.clearFocus()
        closeButton.setOnClickListener { v: View? ->
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
        prepareData()
    }

    private fun prepareData() {
        if (BWSApplication.isNetworkConnected(this)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<PlaylistDetailsModel> = APINewClient.getClient().getPlaylistDetail(CoUserID, "5")
            listCall.enqueue(object : Callback<PlaylistDetailsModel> {
                override fun onResponse(call: Call<PlaylistDetailsModel>, response: Response<PlaylistDetailsModel>) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                    var listModel: PlaylistDetailsModel = PlaylistDetailsModel()
                    try {
                        listModel = response.body()!!
                        listMOdelGloble = response.body()!!
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    try {
//                            if (listModel.responseData.getIsReminder().equals("0", ignoreCase = true) ||
//                                    listModel.getResponseData().getIsReminder().equals("", ignoreCase = true)) {
//                                binding.ivReminder.setColorFilter(ContextCompat.getColor(ctx, R.color.white), PorterDuff.Mode.SRC_IN)
//                            } else if (listModel.getResponseData().getIsReminder().equals("1", ignoreCase = true)) {
//                                binding.ivReminder.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), PorterDuff.Mode.SRC_IN)
//                            }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                    setData(listModel.responseData)
                    binding.llDownload.setOnClickListener { view1 ->
//                            callObserveMethodGetAllMedia()
//                            callDownload("", "", "", playlistSongsList, 0, binding.llDownloads, binding.ivDownloads)
                    }
                    binding.llMore.setOnClickListener { view1 ->
                        //callMreIntent
                    }
                    /*binding.llReminder.setOnClickListener { view ->

                            if (listModel.getResponseData().getIsReminder().equals("0", ignoreCase = true) ||
                                    listModel.getResponseData().getIsReminder().equals("", ignoreCase = true)) {
                                binding.ivReminder.setColorFilter(ContextCompat.getColor(ctx, R.color.white), PorterDuff.Mode.SRC_IN)
                                AccountFragment.ComeScreenReminder = 0
                                val i = Intent(ctx, ReminderActivity::class.java)
                                i.putExtra("ComeFrom", "1")
                                i.putExtra("PlaylistID", PlaylistID)
                                i.putExtra("ReminderId", "")
                                i.putExtra("PlaylistName", listModel.)
                                i.putExtra("Time", listModel.getResponseData().getReminderTime())
                                i.putExtra("IsCheck", listModel.getResponseData().getIsReminder())
                                i.putExtra("Day", listModel.getResponseData().getReminderDay())
                                i.putExtra("ReminderDay", "")
                                startActivity(i)
                            } else if (listModel.getResponseData().getIsReminder().equals("1", ignoreCase = true)) {
                                binding.ivReminder.setColorFilter(ContextCompat.getColor(ctx, R.color.dark_yellow), PorterDuff.Mode.SRC_IN)
                                val listCall1 = APIClient.getClient().getReminderStatusPlaylist(UserID, PlaylistID, "0") //set 1 or not 0
                                listCall1.enqueue(object : Callback<ReminderStatusPlaylistModel?> {
                                    override fun onResponse(call1: Call<ReminderStatusPlaylistModel?>, response1: Response<ReminderStatusPlaylistModel?>) {
                                        try {
                                            val listModel1 = response1.body()
                                            BWSApplication.showToast(listModel1!!.responseMessage, ctx)
                                            if (listModel1!!.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                                listModel.getResponseData().setIsReminder(listModel1!!.responseData.isCheck)
                                                binding.ivReminder.setColorFilter(ContextCompat.getColor(ctx, R.color.white), PorterDuff.Mode.SRC_IN)
                                                prepareData(UserID, PlaylistID, "onResume")
                                            } else {
                                            }
                                        } catch (e: java.lang.Exception) {
                                            e.printStackTrace()
                                        }
                                        setData(listModel.getResponseData())
                                        binding.llDownloads.setOnClickListener { view1 ->
                                            callObserveMethodGetAllMedia()
                                            callDownload("", "", "", playlistSongsList, 0, binding.llDownloads, binding.ivDownloads)
                                        }
                                    }

                                    override fun onFailure(call1: Call<ReminderStatusPlaylistModel?>, t: Throwable) {}
                                })
                            }
                        }*/


                }

                override fun onFailure(call: Call<PlaylistDetailsModel>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }

    private fun setData(listModel: PlaylistDetailsModel.ResponseData?) {
        MyCreated = listModel!!.created
        val measureRatio = BWSApplication.measureRatio(ctx, 0f, 5f, 3f, 1f, 0f)

        if (listModel!!.playlistName.equals("", ignoreCase = true) ||
                listModel.playlistName == null) {
            binding.tvPlayListName.setText(R.string.My_Playlist)
        } else {
            binding.tvPlayListName.setText(listModel.playlistName)
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
        binding.llDownload.setVisibility(View.INVISIBLE)
        if (listModel.playlistSongs != null) {
            if (listModel.playlistSongs!!.size == 0) {
                binding.llAddAudio.visibility = View.VISIBLE
                binding.llDownload.setVisibility(View.VISIBLE)
                binding.llReminder.visibility = View.VISIBLE
//                binding.ivPlaylistStatus.setVisibility(View.INVISIBLE)
//                binding.llListing.setVisibility(View.GONE)
                binding.btnAddAudio.setOnClickListener { view ->
                    val i = Intent(ctx, AddAudioActivity::class.java)
                    i.putExtra("PlaylistID", listModel.playlistID)
                    startActivity(i)
                }
            } else {
                binding.llAddAudio.visibility = View.GONE
//                    binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon)
//                    binding.ivDownloads.setColorFilter(activity.resources.getColor(R.color.white), PorterDuff.Mode.SRC_IN)
//                    binding.ivPlaylistStatus.setVisibility(View.VISIBLE)
//                    binding.llListing.setVisibility(View.VISIBLE)
//                try {
                    /* if (MyDownloads.equals("1", ignoreCase = true)) {
                            binding.llDelete.setVisibility(View.VISIBLE)
                            binding.llReminder.visibility = View.INVISIBLE
                            binding.llDownloads.setVisibility(View.INVISIBLE)
                            binding.llMore.visibility = View.GONE
                            binding.rlSearch.visibility = View.VISIBLE
                            adpater2 = PlayListsAdpater2(listModel.playlistSongs,ctx,CoUserID, "0")
                            binding.rvPlayLists2.setAdapter(adpater2)
                            binding.rvPlayLists.setVisibility(View.GONE)
                            binding.rvPlayLists1.visibility = View.GONE
                            binding.rvPlayLists2.setVisibility(View.VISIBLE)
                            binding.ivDownloads.setImageResource(R.drawable.ic_download_play_icon)
                            binding.ivDownloads.setColorFilter(activity.resources.getColor(R.color.dark_yellow), PorterDuff.Mode.SRC_IN)
                            enableDisableDownload(false, "orange")
                            binding.ivReminder.setColorFilter(activity.resources.getColor(R.color.gray), PorterDuff.Mode.SRC_IN)
                        } else {*/
                    binding.llDownload.setVisibility(View.VISIBLE)
                    binding.llReminder.visibility = View.VISIBLE
                    if (listModel.created.equals("1", ignoreCase = true)) {
                        binding.rvPlayLists1.visibility = View.VISIBLE
                        binding.rvPlayLists2.visibility = View.GONE
                        adpater = PlayListsAdpater(listModel.playlistSongs!!, ctx, CoUserID, listModel.created, binding, activity, this)
                        val callback: ItemTouchHelper.Callback = ItemMoveCallback(adpater)
                        touchHelper = ItemTouchHelper(callback)
                        touchHelper!!.attachToRecyclerView(binding.rvPlayLists1)
                        binding.rvPlayLists1.adapter = adpater
//                                LocalBroadcastManager.getInstance(ctx)
//                                        .registerReceiver(listener1, IntentFilter("DownloadProgress"))
                    } else {
                        adpater2 = PlayListsAdpater2(listModel.playlistSongs!!, ctx, CoUserID, listModel.created, binding)
                        binding.rvPlayLists1.visibility = View.GONE
                        binding.rvPlayLists2.visibility=View.VISIBLE
                        binding.rvPlayLists2.adapter = adpater2
                    }
//                        }
//                } catch (e: java.lang.Exception) {
//                    e.printStackTrace()
//                }
//                    LocalBroadcastManager.getInstance(ctx)
//                            .registerReceiver(listener, IntentFilter("play_pause_Action"))
            }
        }
    }

    override fun requestDrag(viewHolder: RecyclerView.ViewHolder?) {
        touchHelper!!.startDrag(viewHolder!!)
    }

    class PlayListsAdpater(var listModel: List<PlaylistDetailsModel.ResponseData.PlaylistSong>, var ctx: Context, var CoUserID: String?, var created: String?, var binding: ActivityMyPlaylistListingBinding, var activity: Activity, var startDragListener: StartDragListener) : RecyclerView.Adapter<PlayListsAdpater.MyViewHolder>(), ItemTouchHelperContract {

        var changedAudio = arrayListOf<String>()

        inner class MyViewHolder(var binding: MyplaylistSortingNewBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: MyplaylistSortingNewBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.myplaylist_sorting_new, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

            holder.binding.tvTitle.setText(listModel[position].name)
            holder.binding.tvTime.setText(listModel[position].audioDuration)
            Glide.with(ctx).load(listModel[position].imageFile).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)

            Glide.with(ctx).load(R.drawable.ic_image_bg).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivBackgroundImage)
            try {
                holder.binding.llRemove.setOnClickListener { view ->
//                    handler2.removeCallbacks(UpdateSongTime2);
//                    val shared: SharedPreferences = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE)
//                    val audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true)
//                    AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0")
//                    val pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "0")
//                    if (audioPlay && AudioFlag.equals("SubPlayList", ignoreCase = true) && pID.equals(PlaylistID, ignoreCase = true)) {
//                        if (MiniPlayerFragment.isDisclaimer == 1) {
//                            BWSApplication.showToast("The audio shall remove after the disclaimer", ctx)
//                        } else {
//                            if (audioPlay && AudioFlag.equals("SubPlayList", ignoreCase = true) && pID.equals(PlaylistID, ignoreCase = true) && mData.size == 1) {
//                                BWSApplication.showToast("Currently you play this playlist, you can't remove last audio", ctx)
//                            } else {
                    callRemove(listModel[position].id, listModel, holder.adapterPosition)
//                            }
//                        }
//                    } else {
//                        if (audioPlay && AudioFlag.equals("SubPlayList", ignoreCase = true) && pID.equals(PlaylistID, ignoreCase = true) && mData.size == 1) {
//                            BWSApplication.showToast("Currently you play this playlist, you can't remove last audio", ctx)
//                        } else {
//                           callRemove(listModel[position].id, listModel, holder.adapterPosition)
//                        }
//                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            holder.binding.llSort.setOnTouchListener { v, event ->

                if (event.getAction() === MotionEvent.ACTION_DOWN) {
                    startDragListener.requestDrag(holder)
                }
                if (event.getAction() === MotionEvent.ACTION_UP) {
                    startDragListener.requestDrag(holder)
                }

                false
            }
        }

        private fun callRemove(id: String?, listModel: List<PlaylistDetailsModel.ResponseData.PlaylistSong>, postion: Int) {
            val AudioId = id!!
            var CoUserID: String? = ""
            val shared = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, MODE_PRIVATE)
            CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")
            if (BWSApplication.isNetworkConnected(ctx)) {
                BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                val listCall = APINewClient.getClient().RemoveAudio(CoUserID, AudioId, listModel[postion].playlistID)
                listCall.enqueue(object : Callback<SucessModel?> {
                    override fun onResponse(call: Call<SucessModel?>, response: Response<SucessModel?>) {
//                        try {
                        if (response.isSuccessful) {
////                            handler2.removeCallbacks(UpdateSongTime2);
//                                BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder,activity)
                            val listModel: SucessModel = response.body()!!
//                                mData.removeAt(position)
//                                if (mData.size == 0) {
//                                    enableDisableDownload(false, "gray")
//                                }
//                                val shared: SharedPreferences = getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE)
//                                val audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true)
//                                AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioFlag, "0")
//                                var pos = shared.getInt(CONSTANTS.PREF_KEY_position, 0)
//                                val pID = shared.getString(CONSTANTS.PREF_KEY_PlaylistId, "")
//                                if (audioPlay && AudioFlag.equals("SubPlayList", ignoreCase = true) && pID.equals(PlaylistID, ignoreCase = true)) {
//                                    if (GlobalInitExoPlayer.player != null) {
//                                        GlobalInitExoPlayer.player.removeMediaItem(position)
//                                    }
//                                    if (pos == position && position < mData.size - 1) {
////                                            pos = pos + 1;
//                                        if (MiniPlayerFragment.isDisclaimer == 1) {
////                                    BWSApplication.showToast("The audio shall remove after the disclaimer", getActivity());
//                                        } else {
//                                            if (GlobalInitExoPlayer.player != null) {
////                                            player.seekTo(pos, 0);
//                                                GlobalInitExoPlayer.player.playWhenReady = true
//                                                saveToPref(pos, mData)
//                                            } else {
//                                                callTransparentFrag(pos, getActivity(), mData, "myPlaylist", PlaylistID, true)
//                                            }
//                                        }
//                                    } else if (pos == position && position == mData.size - 1) {
//                                        pos = 0
//                                        if (MiniPlayerFragment.isDisclaimer == 1) {
////                                    BWSApplication.showToast("The audio shall remove after the disclaimer", getActivity());
//                                        } else {
//                                            if (GlobalInitExoPlayer.player != null) {
////                                            player.seekTo(pos, 0);
//                                                GlobalInitExoPlayer.player.playWhenReady = true
//                                                saveToPref(pos, mData)
//                                            } else {
//                                                callTransparentFrag(pos, getActivity(), mData, "myPlaylist", PlaylistID, true)
//                                            }
//                                        }
//                                    } else if (pos < position && pos < mData.size - 1) {
//                                        saveToPref(pos, mData)
//                                    } else if (pos < position && pos == mData.size - 1) {
//                                        saveToPref(pos, mData)
//                                    } else if (pos > position && pos == mData.size) {
//                                        pos = pos - 1
//                                        saveToPref(pos, mData)
//                                    }
//                                }
//                                adpater.notifyItemRemoved(position)
                            MyPlaylistListingActivity().prepareData()
                            BWSApplication.showToast(listModel!!.responseMessage, ctx)
                        }
//                        } catch (e: java.lang.Exception) {
//                            e.printStackTrace()
//                        }
                    }

//                    private fun saveToPref(pos: Int, mData: ArrayList<SubPlayListModel.ResponseData.PlaylistSong>) {
//                        val shareddd: SharedPreferences = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_AUDIO, MODE_PRIVATE)
//                        val editor = shareddd.edit()
//                        val gson = Gson()
//                        val json = gson.toJson(mData)
//                        editor.putString(CONSTANTS.PREF_KEY_modelList, json)
//                        editor.putInt(CONSTANTS.PREF_KEY_position, pos)
//                        editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false)
//                        editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true)
//                        editor.putString(CONSTANTS.PREF_KEY_PlaylistId, PlaylistID)
//                        editor.putString(CONSTANTS.PREF_KEY_myPlaylist, "myPlaylist")
//                        editor.putString(CONSTANTS.PREF_KEY_AudioFlag, "SubPlayList")
//                        editor.commit()
//                        callAddTransFrag()
//                    }

                    override fun onFailure(call: Call<SucessModel?>, t: Throwable) {
                        BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                    }
                })
            } else {
                BWSApplication.showToast(ctx.getString(R.string.no_server_found), ctx)
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
        }

        private fun callDragApi() {
            try {
                if (BWSApplication.isNetworkConnected(ctx)) {
                    val listCall = APINewClient.getClient().SortAudio(CoUserID, "", TextUtils.join(",", changedAudio))
                    listCall.enqueue(object : Callback<SucessModel?> {
                        override fun onResponse(call: Call<SucessModel?>, response: Response<SucessModel?>) {
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


    class PlayListsAdpater2(var listModel: List<PlaylistDetailsModel.ResponseData.PlaylistSong>, var ctx: Context, var CoUserID: String?, var created: String?, var binding: ActivityMyPlaylistListingBinding) : RecyclerView.Adapter<PlayListsAdpater2.MyViewHolder>(), Filterable {

        private var listFilterData: List<PlaylistDetailsModel.ResponseData.PlaylistSong> = listModel

        inner class MyViewHolder(var binding: MyPlaylistLayoutBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: MyPlaylistLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.my_playlist_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//            searchEditText.setHint("Search for audios")
//            binding.tvSearch.setHint("Search for audios")

            val mData: List<PlaylistDetailsModel.ResponseData.PlaylistSong> = listFilterData
            holder.binding.tvTitleA.text = mData[position].name

            holder.binding.tvTimeA.text = mData[position].audioDuration
//            binding.tvSearch.setVisibility(View.GONE)
            holder.binding.equalizerview.visibility = View.GONE
            binding.searchView.visibility = View.VISIBLE
            val measureRatio = BWSApplication.measureRatio(ctx, 0f, 1f, 1f, 0.13f, 0f)
            holder.binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            holder.binding.rlImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.rlImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivBackgroundImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivBackgroundImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivBackgroundImage.scaleType = ImageView.ScaleType.FIT_XY
            holder.binding.ivBackgroundImage.setImageResource(R.drawable.ic_image_bg)
            Glide.with(ctx).load(mData[position].imageFile).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)

            Glide.with(ctx).load(R.drawable.ic_image_bg).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivBackgroundImage)

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
                        val filteredList = ArrayList<PlaylistDetailsModel.ResponseData.PlaylistSong>()
                        for (row in listModel) {
                            if (row.name!!.toLowerCase(Locale.ROOT).contains(charString.toLowerCase(Locale.ROOT))) {
                                filteredList.add(row)
                            }
                        }
                        listFilterData = filteredList
                    }
                    filterResults.values = listFilterData
                    return filterResults
                }

                override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                    if (listFilterData.size == 0) {
//                        binding.llError.setVisibility(View.VISIBLE)
                        binding.rvPlayLists2.visibility = View.GONE
//                        binding.tvFound.setText("Couldn't find '$SearchFlag'. Try searching again")
//                        Log.e("search", SearchFlag)
                    } else {
//                        binding.llError.setVisibility(View.GONE)
                        binding.rvPlayLists2.visibility = View.VISIBLE
                        listFilterData = filterResults.values as List<PlaylistDetailsModel.ResponseData.PlaylistSong>
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }
}
